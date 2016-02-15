package com.krishagni.catissueplus.core.administrative.services;

import com.krishagni.catissueplus.core.administrative.domain.Container;
import com.krishagni.catissueplus.core.administrative.events.ContainerTypeQueryCriteria;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerTypeDetail;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public interface StorageContainerTypeService {	
	public ResponseEvent<StorageContainerTypeDetail> getStorageContainerType(RequestEvent<ContainerTypeQueryCriteria> req);

	public ResponseEvent<StorageContainerTypeDetail> createStorageContainerType(RequestEvent<StorageContainerTypeDetail> req);
	
	public ResponseEvent<StorageContainerTypeDetail> updateStorageContainerType(RequestEvent<StorageContainerTypeDetail> req);
	
	public ResponseEvent<Container> createContainerHierarchy(RequestEvent<Container> req);
	
}
