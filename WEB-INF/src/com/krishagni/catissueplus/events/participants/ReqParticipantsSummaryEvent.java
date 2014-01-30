
package com.krishagni.catissueplus.events.participants;

import com.krishagni.catissueplus.events.RequestEvent;

public class ReqParticipantsSummaryEvent extends RequestEvent {

	private Long cpId;

	private String searchString;

	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
