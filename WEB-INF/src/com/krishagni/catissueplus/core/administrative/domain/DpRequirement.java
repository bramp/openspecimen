package com.krishagni.catissueplus.core.administrative.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.krishagni.catissueplus.core.biospecimen.domain.BaseEntity;
import com.krishagni.catissueplus.core.common.util.Status;

public class DpRequirement extends BaseEntity {
	private DistributionProtocol distributionProtocol;
	
	private String specimenType;
	
	private String anatomicSite;
	
	private Set<String> pathologyStatuses = new HashSet<String>();

	private Long specimenCount;
	
	private BigDecimal quantity;
	
	private String comments;
	
	private String activityStatus;
	
	public DistributionProtocol getDistributionProtocol() {
		return distributionProtocol;
	}
	
	public void setDistributionProtocol(DistributionProtocol distributionProtocol) {
		this.distributionProtocol = distributionProtocol;
	}
	
	public String getSpecimenType() {
		return specimenType;
	}
	
	public void setSpecimenType(String specimenType) {
		this.specimenType = specimenType;
	}
	
	public String getAnatomicSite() {
		return anatomicSite;
	}
	
	public void setAnatomicSite(String anatomicSite) {
		this.anatomicSite = anatomicSite;
	}
	
	public Set<String> getPathologyStatuses() {
		return pathologyStatuses;
	}
	
	public void setPathologyStatuses(Set<String> pathologyStatuses) {
		this.pathologyStatuses = pathologyStatuses;
	}

	public void updatePathologyStatuses(Set<String> pathologyStatuses) {
		getPathologyStatuses().addAll(pathologyStatuses);
		getPathologyStatuses().retainAll(pathologyStatuses);
	}
	
	public Long getSpecimenCount() {
		return specimenCount;
	}
	
	public void setSpecimenCount(Long specimenCount) {
		this.specimenCount = specimenCount;
	}
	
	public BigDecimal getQuantity() {
		return quantity;
	}
	
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getActivityStatus() {
		return activityStatus;
	}
	
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}
	
	public void update(DpRequirement dpr) {
		setDistributionProtocol(dpr.getDistributionProtocol());
		setSpecimenType(dpr.getSpecimenType());
		setAnatomicSite(dpr.getAnatomicSite());
		updatePathologyStatuses(dpr.getPathologyStatuses());
		setSpecimenCount(dpr.getSpecimenCount());
		setQuantity(dpr.getQuantity());
		setComments(dpr.getComments());
		setActivityStatus(dpr.getActivityStatus());
	}
	
	public boolean equalsSpecimenGroup(DpRequirement dpr) {
		return equalsSpecimenGroup(dpr.getSpecimenType(), dpr.getAnatomicSite(), dpr.getPathologyStatuses());
	}

	public boolean equalsSpecimenGroup(String specimenType, String anatomicSite, Set<String> pathologyStatuses) {
		return getSpecimenType().equals(specimenType) &&
				getAnatomicSite().equals(anatomicSite) &&
				CollectionUtils.isEqualCollection(getPathologyStatuses(), pathologyStatuses);
	}

	public void delete() {
		setActivityStatus(Status.ACTIVITY_STATUS_DISABLED.getStatus());
	}
	
}
