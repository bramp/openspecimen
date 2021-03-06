package com.krishagni.catissueplus.core.administrative.domain.factory.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.krishagni.catissueplus.core.administrative.domain.DistributionOrder;
import com.krishagni.catissueplus.core.administrative.domain.DistributionOrderItem;
import com.krishagni.catissueplus.core.administrative.domain.DistributionProtocol;
import com.krishagni.catissueplus.core.administrative.domain.Site;
import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.administrative.domain.factory.DistributionOrderErrorCode;
import com.krishagni.catissueplus.core.administrative.domain.factory.DistributionOrderFactory;
import com.krishagni.catissueplus.core.administrative.domain.factory.DistributionProtocolErrorCode;
import com.krishagni.catissueplus.core.administrative.domain.factory.SiteErrorCode;
import com.krishagni.catissueplus.core.administrative.domain.factory.UserErrorCode;
import com.krishagni.catissueplus.core.administrative.events.DistributionOrderDetail;
import com.krishagni.catissueplus.core.administrative.events.DistributionOrderItemDetail;
import com.krishagni.catissueplus.core.administrative.events.DistributionProtocolDetail;
import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.SpecimenErrorCode;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenInfo;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.errors.ActivityStatusErrorCode;
import com.krishagni.catissueplus.core.common.errors.ErrorType;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.UserSummary;
import com.krishagni.catissueplus.core.common.util.AuthUtil;
import com.krishagni.catissueplus.core.common.util.NumUtil;
import com.krishagni.catissueplus.core.common.util.Status;

public class DistributionOrderFactoryImpl implements DistributionOrderFactory {
	private DaoFactory daoFactory;
	
	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	@Override
	public DistributionOrder createDistributionOrder(DistributionOrderDetail detail, DistributionOrder.Status status) {
		DistributionOrder order = new DistributionOrder();
		OpenSpecimenException ose = new OpenSpecimenException(ErrorType.USER_ERROR);
		
		order.setId(detail.getId());
		setName(detail, order, ose);
		setDistributionProtocol(detail, order, ose);		
		setRequesterAndReceivingSite(detail, order, ose);
		setDistributionDate(detail, order, ose);
		setDistributor(detail, order, ose);
		setOrderItems(detail, order, ose);
		setStatus(detail, status, order,  ose);
		setActivityStatus(detail, order, ose);
		setTrackingUrl(detail, order, ose);
		setComments(detail, order, ose);
		
		ose.checkAndThrow();
		return order;
	}
	
	private void setName(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		String name = detail.getName();
		if (StringUtils.isBlank(name)) {
			ose.addError(DistributionOrderErrorCode.NAME_REQUIRED);
			return;
		}
		
		order.setName(name);
	}

	private void setDistributionProtocol(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		DistributionProtocolDetail dpDetail = detail.getDistributionProtocol();
		Long dpId = dpDetail != null ? dpDetail.getId() : null;
		String dpShortTitle = dpDetail != null ? dpDetail.getShortTitle() : null;
		
		if (dpId == null && StringUtils.isBlank(dpShortTitle)) {
			ose.addError(DistributionOrderErrorCode.DP_REQUIRED);
			return;
		}
		
		DistributionProtocol dp = null;
		if (dpId != null) {
			dp = daoFactory.getDistributionProtocolDao().getById(dpId);
		} else {
			dp = daoFactory.getDistributionProtocolDao().getByShortTitle(dpShortTitle);
		}
		
		if (dp == null) {
			ose.addError(DistributionProtocolErrorCode.NOT_FOUND);
			return;
		}
		
		order.setDistributionProtocol(dp);
	}

	private void setRequesterAndReceivingSite(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		User requester = getUser(detail.getRequester(), null);
		if (requester == null) {
			ose.addError(UserErrorCode.NOT_FOUND);
			return;
		}
		
		order.setRequester(requester);
		
		Long siteId = detail.getSiteId();
		String siteName = detail.getSiteName();		
		if (siteId == null && StringUtils.isBlank(siteName)) {
			return;
		}

		Site site = null;
		if (siteId != null) {
			site = daoFactory.getSiteDao().getById(siteId);
		} else {
			site = daoFactory.getSiteDao().getSiteByName(siteName);
		}

		if (site == null) {
			ose.addError(SiteErrorCode.NOT_FOUND);
			return;
		}

		if (!requester.getInstitute().equals(site.getInstitute())) {
			ose.addError(DistributionOrderErrorCode.INVALID_REQUESTER_RECV_SITE_INST, requester.formattedName(), site.getName());
			return;
		}
		
		order.setSite(site);
	}

	private void setDistributionDate(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		Date creationDate = detail.getCreationDate();
		if (creationDate == null) {
			creationDate = Calendar.getInstance().getTime();
		} else if (creationDate.after(Calendar.getInstance().getTime())) {
			ose.addError(DistributionOrderErrorCode.INVALID_CREATION_DATE);
			return;
		}

		order.setCreationDate(creationDate);
		
		Date executionDate = detail.getExecutionDate();
		if (executionDate == null) {
			executionDate = Calendar.getInstance().getTime();
		} else if (executionDate.after(Calendar.getInstance().getTime())) {
			ose.addError(DistributionOrderErrorCode.INVALID_EXECUTION_DATE);
			return;
		}
		
		order.setExecutionDate(executionDate);
	}

	private void setDistributor(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		User distributor = getUser(detail.getDistributor(), AuthUtil.getCurrentUser());
		if (distributor == null) {
			ose.addError(UserErrorCode.NOT_FOUND);
			return;
		}
		
		order.setDistributor(distributor);
	}
		
	private void setOrderItems(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		Set<DistributionOrderItem> items = new HashSet<DistributionOrderItem>();
		Set<Long> specimens = new HashSet<Long>();
		
		boolean dupAdded = false;
		for (DistributionOrderItemDetail oid : detail.getOrderItems()) {
			DistributionOrderItem item = getOrderItem(oid, order, ose);
			if (item == null) {
				continue;
			}
			
			items.add(item);
			if (!specimens.add(item.getSpecimen().getId()) && !dupAdded) {
				ose.addError(DistributionOrderErrorCode.DUPLICATE_SPECIMENS);
				dupAdded = true;
			}
		}
		
		order.setOrderItems(items);
	}
		
	private void setStatus(DistributionOrderDetail detail, DistributionOrder.Status initialStatus, DistributionOrder order, OpenSpecimenException ose) {		
		if (initialStatus != null) {
			order.setStatus(initialStatus);
			return;
		}
		
		if (detail.getStatus() == null) {
			ose.addError(DistributionOrderErrorCode.INVALID_STATUS, detail.getStatus());
		}
		
		DistributionOrder.Status status = null;
		try {
			status = DistributionOrder.Status.valueOf(detail.getStatus());
		} catch (IllegalArgumentException iae) {
			ose.addError(DistributionOrderErrorCode.INVALID_STATUS, detail.getStatus());
			return;
		}
		
		order.setStatus(status);
	}
	
	private void setActivityStatus(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		String activityStatus = detail.getActivityStatus();
		if (StringUtils.isBlank(activityStatus)) {
			activityStatus = Status.ACTIVITY_STATUS_ACTIVE.getStatus();
		}
		
		if (!Status.isValidActivityStatus(activityStatus)) {
			ose.addError(ActivityStatusErrorCode.INVALID);
			return;
		}
		
		order.setActivityStatus(activityStatus);
	}

	private void setTrackingUrl(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		order.setTrackingUrl(detail.getTrackingUrl());
	}
	
	private void setComments(DistributionOrderDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		order.setComments(detail.getComments());
	}
		
	private User getUser(UserSummary userSummary, User defaultUser) {
		if (userSummary == null) {
			return defaultUser;
		}
		
		User user = defaultUser;
		if (userSummary.getId() != null) {
			user = daoFactory.getUserDao().getById(userSummary.getId());
		} else if (StringUtils.isNotBlank(userSummary.getLoginName()) && StringUtils.isNotBlank(userSummary.getDomain())) {
			user = daoFactory.getUserDao().getUser(userSummary.getLoginName(), userSummary.getDomain());
		}
		
		return user;
	}
	
	private DistributionOrderItem getOrderItem(DistributionOrderItemDetail detail, DistributionOrder order, OpenSpecimenException ose) {
		Specimen specimen = getSpecimen(detail.getSpecimen(), ose);
		if (specimen == null) {
			return null;
		} 

		if (detail.getQuantity() == null || NumUtil.lessThanEqualsZero(detail.getQuantity())) {
			ose.addError(DistributionOrderErrorCode.INVALID_QUANTITY);
			return null;
		}
				
		DistributionOrderItem orderItem = new DistributionOrderItem();
		orderItem.setQuantity(detail.getQuantity());
		orderItem.setSpecimen(specimen);
		orderItem.setOrder(order);
		
		try {
			orderItem.setStatus(DistributionOrderItem.Status.valueOf(detail.getStatus()));
		} catch (IllegalArgumentException iae) {
			ose.addError(DistributionOrderErrorCode.INVALID_SPECIMEN_STATUS, detail.getStatus());
		}
		
		return orderItem;
	}
	
	private Specimen getSpecimen(SpecimenInfo info, OpenSpecimenException ose) {
		Specimen specimen = null;
		Object key = null;
		
		if (info.getId() != null) {
			key = info.getId();
			specimen = daoFactory.getSpecimenDao().getById(info.getId());
		} else if (StringUtils.isNotBlank(info.getLabel())) {
			key = info.getLabel();
			specimen = daoFactory.getSpecimenDao().getByLabel(info.getLabel());
		}
		
		if (specimen == null) {
			ose.addError(SpecimenErrorCode.NOT_FOUND, key);
		}
		
		return specimen;
	}
}
