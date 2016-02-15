package com.krishagni.catissueplus.core.administrative.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.krishagni.catissueplus.core.administrative.domain.Container;
import com.krishagni.catissueplus.core.administrative.domain.StorageContainerType;
import com.krishagni.catissueplus.core.administrative.domain.factory.StorageContainerTypeErrorCode;
import com.krishagni.catissueplus.core.administrative.domain.factory.StorageContainerTypeFactory;
import com.krishagni.catissueplus.core.administrative.events.ContainerTypeQueryCriteria;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerDetail;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerSummary;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerTypeDetail;
import com.krishagni.catissueplus.core.administrative.events.StorageLocationSummary;
import com.krishagni.catissueplus.core.administrative.services.StorageContainerService;
import com.krishagni.catissueplus.core.administrative.services.StorageContainerTypeService;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.PlusTransactional;
import com.krishagni.catissueplus.core.common.access.AccessCtrlMgr;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public class StorageContainerTypeServiceImpl implements StorageContainerTypeService {
	private DaoFactory daoFactory;
	
	private StorageContainerTypeFactory containerTypeFactory;

	@Autowired
	private StorageContainerService storageContainerSvc;

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public void setContainerTypeFactory(StorageContainerTypeFactory containerTypeFactory) {
		this.containerTypeFactory = containerTypeFactory;
	}
	
	public void setStorageContainerSvc(StorageContainerService storageContainerSvc) {
		this.storageContainerSvc = storageContainerSvc;
	}
	
	@Override
	@PlusTransactional
	public ResponseEvent<StorageContainerTypeDetail> getStorageContainerType(
			RequestEvent<ContainerTypeQueryCriteria> req) {
		try {
			AccessCtrlMgr.getInstance().ensureUserIsAdmin();
			ContainerTypeQueryCriteria crit = req.getPayload();
			StorageContainerType containerType = getContainerType(crit.getId(), crit.getName());
			return ResponseEvent.response(StorageContainerTypeDetail.from(containerType));
		} catch (OpenSpecimenException ose) {
			return ResponseEvent.error(ose);
		} catch (Exception e) {
			return ResponseEvent.serverError(e);
		}
	}
	
	@Override
	@PlusTransactional
	public ResponseEvent<StorageContainerTypeDetail> createStorageContainerType(
			RequestEvent<StorageContainerTypeDetail> req) {
		try {
			AccessCtrlMgr.getInstance().ensureUserIsAdmin();
			StorageContainerTypeDetail input = req.getPayload();
			StorageContainerType containerType = containerTypeFactory.createStorageContainerType(input);
			ensureUniqueConstraints(null, containerType);
			
			daoFactory.getStorageContainerTypeDao().saveOrUpdate(containerType, true);
			return ResponseEvent.response(StorageContainerTypeDetail.from(containerType));
		} catch (OpenSpecimenException ose) {
			return ResponseEvent.error(ose);
		} catch (Exception e) {
			return ResponseEvent.serverError(e);
		}
	}
	
	@Override
	@PlusTransactional
	public ResponseEvent<StorageContainerTypeDetail> updateStorageContainerType(
			RequestEvent<StorageContainerTypeDetail> req) {
		try {
			AccessCtrlMgr.getInstance().ensureUserIsAdmin();
			StorageContainerTypeDetail input = req.getPayload();
			StorageContainerType existing = getContainerType(input.getId(), input.getName());
			StorageContainerType containerType = containerTypeFactory.createStorageContainerType(input);
			ensureUniqueConstraints(existing, containerType);
			
			existing.update(containerType);
			daoFactory.getStorageContainerTypeDao().saveOrUpdate(existing);
			return ResponseEvent.response(StorageContainerTypeDetail.from(existing));
		} catch (OpenSpecimenException ose) {
			return ResponseEvent.error(ose);
		} catch (Exception e) {
			return ResponseEvent.serverError(e);
		}
	}
	
	@Override
	@PlusTransactional
	public ResponseEvent<Container> createContainerHierarchy(RequestEvent<Container> req) {
		try {
			Container input = req.getPayload();
			StorageContainerType existing = getContainerType(input.getStorageContainerTypeId(), 
					input.getStorageContainerTypeName());
			StorageContainerTypeDetail typeDetail = StorageContainerTypeDetail.from(existing);
			createHierarchy(typeDetail, input);
			return ResponseEvent.response(input);
		} catch (OpenSpecimenException ose) {
			return ResponseEvent.error(ose);
		} catch (Exception e) {
			return ResponseEvent.serverError(e);
		}
	}
	
	private StorageContainerType getContainerType(Long id, String name) {
		StorageContainerType containerType = null;
		Object key = null;
		if (id != null) {
			containerType = daoFactory.getStorageContainerTypeDao().getById(id);
			key = id;
		} else if (StringUtils.isNotBlank(name)) {
			containerType = daoFactory.getStorageContainerTypeDao().getByName(name);
			key = name;
		}
		
		if (containerType == null) {
			throw OpenSpecimenException.userError(StorageContainerTypeErrorCode.NOT_FOUND, key);
		}
		
		return containerType;
	}
	
	private void ensureUniqueConstraints(StorageContainerType existing, StorageContainerType newContainerType) {
		if(existing != null && existing.getName().equals(newContainerType.getName())) {
			return;
		}
		
		StorageContainerType containerType = daoFactory.getStorageContainerTypeDao().getByName(newContainerType.getName());
		if(containerType != null) {
			throw OpenSpecimenException.userError(StorageContainerTypeErrorCode.DUP_NAME, newContainerType.getName());
		}
	}
	
	private void createHierarchy(StorageContainerTypeDetail existing, Container input) throws IOException {
		if(input.getParentContainerName() != null) {
			StorageContainerTypeDetail parent = new StorageContainerTypeDetail();
			parent.setName(input.getParentContainerName());
			existing.setName(input.getParentContainerName() + existing.getAbbreviation());
			createStorageContainers(existing, parent, 1, input);
		} else {
			existing.setName(existing.getAbbreviation());
			createStorageContainers(existing, null, 1, input);
		}
	}
	
	private void createStorageContainers(StorageContainerTypeDetail container, StorageContainerTypeDetail parent, 
			long counter, Container input) throws IOException {
		String name = container.getName();
		if (container.getName() == null || container.getName().isEmpty()) {
			String cnt = String.valueOf(counter);
			name = parent.getName() + container.getAbbreviation() + cnt;
			container.setName(name);
		}
		createObject(container, parent, input);
		if (container.getCanHold() == null) {
			return;
		}
		long noOfRows = container.getNoOfRows();
		long noOfColumns = container.getNoOfColumns();
		
		for (long row = 1; row <= noOfRows; row++ ) {
			for (long column =1; column <= noOfColumns; column++) {
				StorageContainerTypeDetail child = container.getCanHold();
				if (row == 1 && column ==1) {
					counter = 1;
				}
				child.setName(null);
				createStorageContainers(child, container, counter, null);
				counter++;
			}
		}
	}
	
	private void createObject(StorageContainerTypeDetail container, StorageContainerTypeDetail parent, 
			Container input) throws IOException {
		StorageContainerDetail obj = new StorageContainerDetail();
		obj.setName(container.getName());
		obj.setTemperature(container.getTemperature());
		obj.setNoOfColumns(container.getNoOfColumns());
		obj.setNoOfRows(container.getNoOfRows());
		obj.setColumnLabelingScheme(container.getColumnLabelingScheme());
		obj.setRowLabelingScheme(container.getRowLabelingScheme());
		if (input != null) {
			obj.setSiteName(input.getSite());
		} else {
			obj.setSiteName("");
		}
		
		if(parent != null) {
			StorageLocationSummary location = new StorageLocationSummary();
			location.setName(parent.getName());
			obj.setStorageLocation(location);
		} else {
			StorageLocationSummary location = new StorageLocationSummary();
			location.setName("");
			obj.setStorageLocation(location);
		}
		obj.setActivityStatus("Active");
		obj.setStoreSpecimensEnabled(container.isStoreSpecimenEnabled());
		
		RequestEvent<StorageContainerDetail> req = new RequestEvent<StorageContainerDetail>(obj);
		storageContainerSvc.createStorageContainer(req);
	}

}
