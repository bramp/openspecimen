package com.krishagni.catissueplus.core.administrative.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.krishagni.catissueplus.core.administrative.domain.factory.ContainerTypeErrorCode;
import com.krishagni.catissueplus.core.administrative.repository.ContainerTypeListCriteria;
import com.krishagni.catissueplus.core.biospecimen.domain.BaseEntity;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.DependentEntityDetail;
import com.krishagni.catissueplus.core.common.util.Status;

@Configurable
@Audited
public class ContainerType extends BaseEntity {
	private static final String ENTITY_NAME = "container_type";
	
	private String name;
	
	private int noOfColumns;
	
	private int noOfRows;
	
	private String columnLabelingScheme;
	
	private String rowLabelingScheme;
	
	private Double temperature;
	
	private boolean storeSpecimenEnabled;
	
	private String abbreviation;
	
	private ContainerType canHold;
	
	private String activityStatus;
	
	private Set<StorageContainer> containers = new HashSet<StorageContainer>();
	
	@Autowired 
	private DaoFactory daoFactory;
	
	public static String getEntityName() {
		return ENTITY_NAME;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNoOfColumns() {
		return noOfColumns;
	}

	public void setNoOfColumns(int noOfColumns) {
		this.noOfColumns = noOfColumns;
	}

	public int getNoOfRows() {
		return noOfRows;
	}

	public void setNoOfRows(int noOfRows) {
		this.noOfRows = noOfRows;
	}

	public String getColumnLabelingScheme() {
		return columnLabelingScheme;
	}

	public void setColumnLabelingScheme(String columnLabelingScheme) {
		this.columnLabelingScheme = columnLabelingScheme;
	}

	public String getRowLabelingScheme() {
		return rowLabelingScheme;
	}

	public void setRowLabelingScheme(String rowLabelingScheme) {
		this.rowLabelingScheme = rowLabelingScheme;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	
	public boolean isStoreSpecimenEnabled() {
		return storeSpecimenEnabled;
	}

	public void setStoreSpecimenEnabled(boolean storeSpecimenEnabled) {
		this.storeSpecimenEnabled = storeSpecimenEnabled;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public ContainerType getCanHold() {
		return canHold;
	}

	public void setCanHold(ContainerType canHold) {
		this.canHold = canHold;
	}
	
	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	@NotAudited
	public Set<StorageContainer> getContainers() {
		return containers;
	}

	public void setContainers(Set<StorageContainer> storageContainers) {
		this.containers = storageContainers;
	}

	public void update(ContainerType containerType) {
		setName(containerType.getName());
		setNoOfColumns(containerType.getNoOfColumns());
		setNoOfRows(containerType.getNoOfRows());
		setColumnLabelingScheme(containerType.getColumnLabelingScheme());
		setRowLabelingScheme(containerType.getRowLabelingScheme());
		setTemperature(containerType.getTemperature());
		setStoreSpecimenEnabled(containerType.isStoreSpecimenEnabled());
		setAbbreviation(containerType.getAbbreviation());
		setActivityStatus(containerType.getActivityStatus());
		
		updateCanHold(containerType.getCanHold());
	}
	
	public List<DependentEntityDetail> getDependentEntities() {
		ContainerTypeListCriteria crit = new ContainerTypeListCriteria().canHold(getId());
		List<ContainerType> listContainerType = daoFactory.getContainerTypeDao().getContainerTypes(crit);
		return DependentEntityDetail.singletonList(ContainerType.getEntityName(), listContainerType.size());
	}
	
	public void delete() {
		List<DependentEntityDetail> ded = getDependentEntities();
		if (ded.size() > 0) {
			throw OpenSpecimenException.userError(ContainerTypeErrorCode.REF_ENTITY_FOUND);
		} else {
			String activityStatus = Status.ACTIVITY_STATUS_DISABLED.getStatus();
			setActivityStatus(activityStatus);
		}
	}

	private void updateCanHold(ContainerType canHold) {
		if (cycleExistsInHierarchy(canHold)) {
			throw OpenSpecimenException.userError(ContainerTypeErrorCode.CYCLES_NOT_ALLOWED, getName(), canHold.getName());
		}
		
		setCanHold(canHold);
	}

	private boolean cycleExistsInHierarchy(ContainerType canHold) {
		ContainerType other = canHold;
		while (other != null) {
			if (this.equals(other)) {
				return true;
			}

			other = other.getCanHold();
		}
		
		return false;
	}
}
