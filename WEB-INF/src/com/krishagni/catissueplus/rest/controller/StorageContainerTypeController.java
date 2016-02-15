
package com.krishagni.catissueplus.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.krishagni.catissueplus.core.administrative.domain.Container;
import com.krishagni.catissueplus.core.administrative.events.ContainerTypeQueryCriteria;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerTypeDetail;
import com.krishagni.catissueplus.core.administrative.services.StorageContainerTypeService;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

@Controller
@RequestMapping("/storage-container-types")
public class StorageContainerTypeController {

	@Autowired
	private StorageContainerTypeService storageContainerTypeSvc;
	
	@RequestMapping(method = RequestMethod.GET, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerTypeDetail getStorageContainer(@PathVariable("id") Long containerId) {
		return getContainerType(new ContainerTypeQueryCriteria(containerId));
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/byname")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerTypeDetail getStorageContainer(@RequestParam(value = "name", required = true) String name) {
		return getContainerType(new ContainerTypeQueryCriteria(name));
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerTypeDetail createStorageContainerType(@RequestBody StorageContainerTypeDetail detail) {
		RequestEvent<StorageContainerTypeDetail> req = new RequestEvent<StorageContainerTypeDetail>(detail);
		ResponseEvent<StorageContainerTypeDetail> resp = storageContainerTypeSvc.createStorageContainerType(req);
		resp.throwErrorIfUnsuccessful();
		return resp.getPayload();
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerTypeDetail updateStorageContainerType(
			@PathVariable("id")
			Long id,
			
			@RequestBody
			StorageContainerTypeDetail detail) {
		detail.setId(id);
		
		RequestEvent<StorageContainerTypeDetail> req = new RequestEvent<StorageContainerTypeDetail>(detail);
		ResponseEvent<StorageContainerTypeDetail> resp = storageContainerTypeSvc.updateStorageContainerType(req);
		resp.throwErrorIfUnsuccessful();
		return resp.getPayload();
	}
	
	@RequestMapping(method = RequestMethod.POST, value="/create-hierarchy")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Container createHierarchy(@RequestBody Container container) {
		RequestEvent<Container> req = new RequestEvent<Container>(container);
		ResponseEvent<Container> resp = storageContainerTypeSvc.createContainerHierarchy(req);
		resp.throwErrorIfUnsuccessful();
		return resp.getPayload();
	}
	
	private StorageContainerTypeDetail getContainerType(ContainerTypeQueryCriteria crit) {
		RequestEvent<ContainerTypeQueryCriteria> req = new RequestEvent<ContainerTypeQueryCriteria>(crit);
		ResponseEvent<StorageContainerTypeDetail> resp = storageContainerTypeSvc.getStorageContainerType(req);
		resp.throwErrorIfUnsuccessful();
		return resp.getPayload();
	}	

}
