<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ page import="edu.wustl.catissuecore.util.global.Utility"%>
<%@ page import="edu.wustl.catissuecore.actionForm.FluidSpecimenReviewEventParametersForm"%>
<%@ page import="edu.wustl.catissuecore.util.global.Constants"%>
<%@ include file="/pages/content/common/AutocompleterCommon.jsp" %> 
<head>
<!-- Mandar : 434 : for tooltip -->
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>

	<script language="javascript">
		
	</script>
<!-- Mandar 21-Aug-06 : For calendar changes -->
<script src="jss/calendarComponent.js"></script>
<SCRIPT>var imgsrc="images/";</SCRIPT>
<LINK href="css/calanderComponent.css" type=text/css rel=stylesheet>
<!-- Mandar 21-Aug-06 : calendar changes end -->

</head>
	
<%
        String operation = (String) request.getAttribute(Constants.OPERATION);
        String formName,specimenId=null;

        boolean readOnlyValue;
        if (operation.equals(Constants.EDIT))
        {
            formName = Constants.FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS_EDIT_ACTION;
            readOnlyValue = true;
        }
        else
        {
            formName = Constants.FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS_ADD_ACTION;
			specimenId = (String) request.getAttribute(Constants.SPECIMEN_ID);
            readOnlyValue = false;
        }

		Object obj = request.getAttribute("fluidSpecimenReviewEventParametersForm");
		String currentEventParametersDate = ""; 
		FluidSpecimenReviewEventParametersForm form = null;
		if(obj != null && obj instanceof FluidSpecimenReviewEventParametersForm)
		{
			form = (FluidSpecimenReviewEventParametersForm)obj;
		currentEventParametersDate = form.getDateOfEvent();
		if(currentEventParametersDate == null)
			currentEventParametersDate = "";
		}

		
%>	
			
<html:errors/>
    
<table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="600">

<html:form action="<%=Constants.FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS_ADD_ACTION%>">


	<!-- NEW FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS REGISTRATION BEGINS-->
	<tr>
	<td>
	
	<table summary="" cellpadding="3" cellspacing="0" border="0">
		<tr>
			<td><html:hidden property="operation" value="<%=operation%>"/></td>
		</tr>
		
		<tr>
			<td><html:hidden property="id" /></td>
		</tr>

		<tr>
			<td>
				<html:hidden property="specimenId" value="<%=specimenId%>"/>
			</td>
		</tr>
		
		<tr>
			 <td class="formMessage" colspan="3">* indicates a required field</td>
		</tr>
		
		<tr>
			<td class="formTitle" height="20" colspan="3">
				<logic:equal name="operation" value="<%=Constants.ADD%>">
					<bean:message key="fluidspecimenrevieweventparameters.title"/>
				</logic:equal>
				<logic:equal name="operation" value="<%=Constants.EDIT%>">
					<bean:message key="fluidspecimenrevieweventparameters.edittitle"/>
				</logic:equal>
			</td>
		</tr>
		<!-- Name of the fluidspecimenrevieweventparameters -->
<!-- User -->		
		<tr>
			<td class="formRequiredNotice" width="5">*</td>
			<td class="formRequiredLabel">
				<label for="type">
					<bean:message key="eventparameters.user"/> 
				</label>
			</td>
			<td class="formField">
<!-- Mandar : 434 : for tooltip -->
				<html:select property="userId" styleClass="formFieldSized" styleId="userId" size="1"
				 onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
					<html:options collection="<%=Constants.USERLIST%>" labelProperty="name" property="value"/>
				</html:select>
			</td>
		</tr>

<!-- date -->		
		<tr>
			<td class="formRequiredNotice" width="5">*</td>
			<td class="formRequiredLabel">
				<label for="type">
					<bean:message key="eventparameters.dateofevent"/> 
				</label>
			</td>
			<td class="formField">
<%
if(currentEventParametersDate.trim().length() > 0)
{
	Integer eventParametersYear = new Integer(Utility.getYear(currentEventParametersDate ));
	Integer eventParametersMonth = new Integer(Utility.getMonth(currentEventParametersDate ));
	Integer eventParametersDay = new Integer(Utility.getDay(currentEventParametersDate ));
%>
<ncombo:DateTimeComponent name="dateOfEvent"
			  id="dateOfEvent"
			  formName="fluidSpecimenReviewEventParametersForm"
			  month= "<%= eventParametersMonth %>"
			  year= "<%= eventParametersYear %>"
			  day= "<%= eventParametersDay %>"
			  value="<%=currentEventParametersDate %>"
			  styleClass="formDateSized10"
					/>
<% 
	}
	else
	{  
 %>
<ncombo:DateTimeComponent name="dateOfEvent"
			  id="dateOfEvent"
			  formName="fluidSpecimenReviewEventParametersForm"
			  styleClass="formDateSized10"
					/>
<% 
	} 
%> 
<bean:message key="page.dateFormat" />&nbsp;


			</td>
		</tr>		
		
		<!-- hours & minutes -->		
		<!-- 
			 Bug ID:  AutocompleteBugID
			 Patch ID: AutocompleteBugID_7
			 Description:<html:select> tag is replaced by Autocomplete
	   -->	
		<tr>
			<td class="formRequiredNotice" width="5">*</td>
			<td class="formRequiredLabel">
				<label for="eventparameters.time">
					<bean:message key="eventparameters.time"/>
				</label>
			</td>
			<td class="formField">
				<autocomplete:AutoCompleteTag property="timeInHours"
					  optionsList = "<%=request.getAttribute(Constants.HOUR_LIST)%>"
					  initialValue="<%=form.getTimeInHours()%>"
					  styleClass="formFieldSized5"
					  staticField="false"
			    />	
				&nbsp;
				<label for="eventparameters.timeinhours">
					<bean:message key="eventparameters.timeinhours"/>&nbsp; 
				</label>
                   <autocomplete:AutoCompleteTag property="timeInMinutes"
						  optionsList = "<%=request.getAttribute(Constants.MINUTES_LIST)%>"
						  initialValue="<%=form.getTimeInMinutes()%>"
						  styleClass="formFieldSized5"
						  staticField="false"
				    />	
				<label for="eventparameters.timeinminutes">
					&nbsp;<bean:message key="eventparameters.timeinminutes"/> 
				</label>
			</td>
		</tr>


<!-- cellCount -->		
		<tr>
			<td class="formRequiredNotice" width="5">&nbsp;</td>
			<td class="formLabel">
				<label for="cellCount">
					<bean:message key="fluidspecimenrevieweventparameters.cellcount"/> 
				</label>
			</td>
			<td class="formField">
				<html:text styleClass="formDateSized"  maxlength="10" size="35" styleId="cellCount" property="cellCount" />
			</td>
		</tr>

<!-- comments -->		
		<tr>
			<td class="formRequiredNotice" width="5">&nbsp;</td>
			<td class="formLabel">
				<label for="comments">
					<bean:message key="eventparameters.comments"/> 
				</label>
			</td>
			<td class="formField">
				<html:textarea styleClass="formFieldSized"  styleId="comments" property="comments" />
			</td>
		</tr>

<!-- buttons -->
		<tr>
		  <td align="right" colspan="3">
			<!-- action buttons begins -->
			<%
        		String changeAction = "setFormAction('" + formName + "');";
			%> 
			<table cellpadding="4" cellspacing="0" border="0">
				<tr>
					<td>
						<html:submit styleClass="actionButton" value="Submit" onclick="<%=changeAction%>" />
					</td>
					<%-- td><html:reset styleClass="actionButton"/></td --%> 
				</tr>
			</table>
			<!-- action buttons end -->
			</td>
		</tr>

		</table>
		
	  </td>
	 </tr>

	 <!-- NEW fluidspecimenrevieweventparameters ends-->
	 
	 </html:form>
 </table>