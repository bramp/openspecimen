package com.krishagni.catissueplus.core.administrative.domain;

public class Container {
	
	private Long StorageContainerTypeId;
	
	private String storageContainerTypeName;
	
	private String site;
	
	private String parentContainerName;
	
	private Long noOfContainers;

	public Long getStorageContainerTypeId() {
		return StorageContainerTypeId;
	}

	public void setStorageContainerTypeId(Long storageContainerTypeId) {
		StorageContainerTypeId = storageContainerTypeId;
	}

	public String getStorageContainerTypeName() {
		return storageContainerTypeName;
	}

	public void setStorageContainerTypeName(String storageContainerTypeName) {
		this.storageContainerTypeName = storageContainerTypeName;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getParentContainerName() {
		return parentContainerName;
	}

	public void setParentContainerName(String parentContainerName) {
		this.parentContainerName = parentContainerName;
	}

	public Long getNoOfContainers() {
		return noOfContainers;
	}

	public void setNoOfContainers(Long noOfContainers) {
		this.noOfContainers = noOfContainers;
	}

}
