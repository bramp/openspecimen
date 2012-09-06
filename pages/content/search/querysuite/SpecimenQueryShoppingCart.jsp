<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="java.util.*"%>
<%@ page import="edu.wustl.common.beans.NameValueBean"%>

<%@ page import="edu.wustl.catissuecore.actionForm.ShoppingCartForm"%>
<%@ page import="edu.wustl.catissuecore.util.global.Constants"%>
<%@ page import="edu.wustl.catissuecore.querysuite.QueryShoppingCart"%>
<%@ page import="edu.wustl.catissuecore.util.global.AppUtility"%>
<%@ page import="edu.wustl.catissuecore.util.global.Variables"%>
<%@ page import="edu.wustl.catissuecore.actionForm.AdvanceSearchForm"%>

<%@ include file="/pages/content/common/AutocompleterCommon.jsp" %> 
<link rel="STYLESHEET" type="text/css" href="dhtmlx_suite/css/dhtmlxgrid.css"/>
<link href="css/catissue_suite.css" rel="stylesheet" type="text/css" />
<link href="css/catissue_suite.css" rel="stylesheet" type="text/css" /> 
<script language="JavaScript"  type="text/javascript" src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<script  language="JavaScript" type="text/javascript" src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script src="dhtmlx_suite/js/dhtmlxcombo.js"></script>
<script src="dhtmlx_suite/js/connector.js"></script>

<link rel="STYLESHEET" type="text/css" href="dhtmlx_suite/ext/dhtmlxgrid_validation.js">
<script src="dhtmlx_suite/ext/dhtmlxgrid_validation.js" type="text/javascript" charset="utf-8"></script>
<script   language="JavaScript" type="text/javascript" src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>
<script  language="JavaScript" type="text/javascript"  src="dhtmlx_suite/ext/dhtmlxgrid_srnd.js"></script>
    <script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_filter.js"></script>
    <script type="text/javascript" src="dhtmlx_suite/ext/dhtmlxgrid_pgn.js"></script>
    <link rel="STYLESHEET" type="text/css" href="dhtmlx_suite/ext/dhtmlxgrid_pgn_bricks.css">
	<link rel="STYLESHEET" type="text/css" href="dhtmlx_suite/css/dhtmlxcombo.css">
	<link rel="STYLESHEET" type="text/css" href="dhtmlx_suite/skins/dhtmlxgrid_dhx_skyblue.css">
<script type="text/javascript" src="dhtmlx_suite/gridexcells/dhtmlxgrid_excell_combo.js"></script>
<script src="jss/script.js"></script>

<style>
.active-column-0 {width:30px}
.active-column-4 {width:150px}
.active-column-6 {width:150px}
</style>
<%
    AdvanceSearchForm form = (AdvanceSearchForm)request.getAttribute("advanceSearchForm");  
	String checkAllPages = (String)session.getAttribute("checkAllPages");
	String pageOf = (String)request.getAttribute(Constants.PAGE_OF);
	String isSpecimenIdPresent = (String)request.getAttribute(Constants.IS_SPECIMENID_PRESENT);
	String isContainerPresent=(String)request.getAttribute(Constants.IS_CONTAINER_PRESENT);
	
	if(isSpecimenIdPresent==null)
	 isSpecimenIdPresent = "";
	 
	if(isContainerPresent==null)
	 isContainerPresent = "";
	
	String isSpecimenArrayPresent = (String)request.getAttribute(Constants.IS_SPECIMENARRAY_PRESENT);
	
	String disabled = "";
	boolean disabledList = false;
	if(isSpecimenArrayPresent!= null && isSpecimenArrayPresent.equals("true"))
	{
		disabled = "DISABLED";
		disabledList = true;
	}
	
	String disabledOrder = "";
	String disabledShipping="";
	
	boolean disabledButton = false;
	if(isSpecimenIdPresent.equals("false"))
	{
		disabled = "DISABLED";
		disabledList = true;
		disabledButton = true;
		disabledOrder = "DISABLED";
		disabledShipping="DISABLED";
	}
	
	if(isContainerPresent.equals("true"))
	{
		disabledButton = false;
		disabledShipping="";
	}
	
	if(disabledOrder=="DISABLED" && disabledShipping=="DISABLED")
	{
		disabledButton = true;
	}

    QueryShoppingCart cart = (QueryShoppingCart) session.getAttribute(Constants.QUERY_SHOPPING_CART);
	List columnList = new ArrayList();
	List dataList = new ArrayList() ;
    
	if(cart!=null)
	{
	  columnList = cart.getColumnList();
	  if(columnList!=null)
	   columnList.add(0,"");
      dataList = cart.getCart();
	}
%>
<head>
<script language="javascript">
function reloadGrid(obj)
{
//alert(obj.value);

	mygrid.clearAll(); 
	mygrid.loadXML("LoadGridServlet?reqParam="+obj.value);	
}
function updateHiddenFields()
	{
		
		var isChecked = "false";
		var checkedRows = mygrid.getCheckedRows(0);
//alert(checkedRows);
		if(checkedRows.length > 0)
		{
        	isChecked = "true";
			var cb = checkedRows.split(",");
	//		alert("cb.size()   "+cb.size());
			rowCount = mygrid.getRowsNum();
			var specIds="";
			for(i=0;i<cb.size();i++)
			{
			//alert(cb[i]+"   cb[i]");
				var cl = mygrid.cells(cb[i],9).getValue();
				//alert("cl   "+cl);
				//alert(specIds);
				if(specIds.length >0)
				specIds = specIds+","+cl;
				
				else
				specIds = cl;
				
			}
			document.forms[0].orderedString.value=specIds;
		}
		else
		{
			isChecked = "false";
		}
		
		return isChecked;
	}
function showEvents()
{
   
	 var autoDiv1 = document.getElementById("eventlist1");
     var autoDiv2 = document.getElementById("eventlist2");

	 var chkbox=  document.getElementById("ch1");

	 if(chkbox.checked== true)
	{
		   
		   autoDiv1.style.display  = 'block';
		   autoDiv2.style.display  = 'none';
	}
    else
   {
       
		autoDiv2.style.display  = 'block';
		autoDiv1.style.display  = 'none';
		 
   }
}
function showHideComponents()
{
   showEvents();
   showPriterTypeLocation();
}
function gotoAdvanceQuery()
{
	var action = "QueryWizard.do?";
	document.forms[0].action = action;
	document.forms[0].submit();
}

function onSubmit(orderedString)
{
	if(document.forms[0].chkName[2].checked == true)
	{
		if(document.getElementById('specimenEventParameter').value == "Transfer")
		{
		    dobulkTransferOperations(orderedString);
		}
		else if(document.getElementById('specimenEventParameter').value == "Disposal")
		{
			
			dobulkDisposals();
		}
		else
		{
			alert("Only Transfer and Disposal bulk functionality is available in this version");
		}
	}
	else if(document.forms[0].chkName[1].checked == true)
	{
		editMultipleSp();
	}
	else if(document.forms[0].chkName[0].checked == true)
	{
		addToOrderList(orderedString);
	}
	else if(document.forms[0].chkName[3].checked == true)
	{
		//create Shipment Request
		createShipmentRequest();
	}
	else if(document.forms[0].chkName[4].checked == true)
	{
		//create Shipment
		createShipment();
	}
	else if(document.forms[0].chkName[5].checked == true)
	{
		//distribute Order
		distributeOrder();
	}
	else if(document.forms[0].chkName[6].checked == true)
	{
		printSpecimensLabels(orderedString);
	}
}

function setCheckBoxState()
		{	
			var chkBox = document.getElementById('checkAll1');
			if(chkBox != null)
			{
			chkBox.checked = true;
			rowCount = mygrid.getRowsNum();
        	for(i=1;i<=rowCount;i++)
				{
					var cl = mygrid.cells(i,0);
					if(cl.isCheckbox())
					cl.setChecked(true);
				}
			}
		
		}

function onDelete()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var flag = confirm("Are you sure you want to delete the selected item(s)?");
				if(flag)
				{
					var action = "AddDeleteCart.do?operation=delete";
					document.forms[0].action = action;
					document.forms[0].target = "_parent";
					document.forms[0].submit();
				}
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}

function onExport()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "ExportCart.do?operation=export";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		
function dobulkTransferOperations(orderedString)
		{
			//orderedString.value = mygrid.getCheckedRows(0);
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {		
				var action = "BulkSpecimenCart.do?operation=bulkTransfers";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		
function dobulkDisposals()
		{
			var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "BulkSpecimenCart.do?operation=bulkDisposals";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}

function addToOrderList(orderedString)
		{
			//orderedString.value = mygrid.getCheckedRows(0);
		    var isChecked = updateHiddenFields();
		    if(isChecked == "true")
		    {
				var action = "BulkSpecimenCart.do?operation=addToOrderList";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		function editMultipleSp()
		{
		
			 var isChecked = updateHiddenFields();
		    if(isChecked == "true")
		    {
				var action = "BulkSpecimenCart.do?operation=editMultipleSp";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}		

function createShipmentRequest()
{
		var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "BulkSpecimenCart.do?operation=createShipmentRequest";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
}

function createShipment()
{
		var isChecked = updateHiddenFields();
		    
		    if(isChecked == "true")
		    {
				var action = "BulkSpecimenCart.do?operation=createShipment";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
	
}

function distributeOrder()
{
	var isChecked = updateHiddenFields();
	   
	if(isChecked == "true")
	{
		var action = "BulkSpecimenCart.do?operation=requestToDistribute";
		document.forms[0].action = action;
		document.forms[0].submit();
	}
	else
	{
		alert("Please select at least one checkbox");
	}
	
}
function printSpecimensLabels(orderedString)
{
//   orderedString.value = mygrid.getCheckedRows(0);
	var isChecked = updateHiddenFields();
	   
	if(isChecked == "true")
	{
		var action = "BulkSpecimenCart.do?operation=printLabels";
		document.forms[0].action = action;
		document.forms[0].submit();
	}
	else
	{
		alert("Please select at least one checkbox");
	}
	
}
function checkAll(element)
{
	mygrid.setEditable(true);
	var state=element.checked;
	rowCount = mygrid.getRowsNum();
	var chkName="";
	//var hidendiv = document.getElementById('hiddenTab');
	//var createRow = document.getElementById('hiddenTab').insertRow(0);
	for(i=1;i<=rowCount;i++)
	{
	
			chkName = "value1(CHK_" + i + ")";
		//var chkName = "value1(CHK_" + i + ")";
		
		var cl = mygrid.cells(i,0);
		
		var ids = i-1;
		//var crtdCell = createRow.insertCell(ids);
		if(cl.isCheckbox())
		{
			cl.setChecked(state);
		//	crtdCell.innerHTML="<input type='hidden' name='"+chkName +"' id='"+ids+"' value='1'>";
			//alert(createRow.innerHTML);
		}
		else
		{
			//crtdCell.innerHTML="<input type='hidden' name='"+chkName +"' id='"+ids+"' value='0'>";
		}
	}
}

function loadSpecimenGrid()
{

	mygrid = new dhtmlXGridObject("specimenGrid");
	mygrid.setImagePath("dhtmlx_suite/imgs/");
	//CHKBOX,SCG_NAME,Label,Barcode,Parent_Specimen_Id,Class,Type,Avl_Quantity,Lineage,Identifier
	mygrid.setInitWidthsP("5,20,15,,15,15,,15,15,");
	mygrid.setEditable(true);
	mygrid.setSkin("light");
	mygrid.enableAutoHeight(true);
	mygrid.setColSorting(",str,str,str,str,str,str,str,str,str");
	mygrid.setHeader(",Specimen Collection Group Name,Label (Barcode),,Parent Label,Class (Type),,Quantity,Lineage,");
	
	mygrid.attachHeader(",,#text_filter,,,#text_filter,,,#select_filter,");
	mygrid.setColTypes("ch,ro,ro,ro,ro,ro,ro,ro,ro,ro");
	mygrid.enableSmartRendering(true,15);
	//mygrid.enableTooltips(",true,true,true,true,true,true,true,true,true");
	mygrid.enableRowsHover(true,'grid_hover')
	mygrid.init();
	//mygrid.addRow(1,",3/23,,,1.0,Tissue(Fixed Tissue),Not Specified,Not Specified,New,2.0,Collected,2141",1);
	//mygrid.addRow(2,",3_1/24,,3,1.0,Tissue(Fixed Tissue),Not Specified,Not Specified,New,2.0,Collected,2142",2);
	var tagVal = document.getElementById('tagName').value;
	//alert(tagVal);
	mygrid.loadXML("LoadGridServlet?reqParam="+tagVal);	
	mygrid.setColumnHidden(3,true);
	mygrid.setColumnHidden(6,true);
	mygrid.setColumnHidden(9,true);
	
}
	</script>
</head>
<body onload="loadSpecimenGrid();">

<html:html>

<html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
	<%=messageKey%>
</html:messages>
<html:form action="AddDeleteCart.do">


<table width="100%" border="0" cellpadding="0" cellspacing="0" class="maintable">
  <tr>
    <td class="td_color_bfdcf3"><table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td class="td_table_head"><span class="wh_ar_b">Specimen List</span></td>
        <td><img src="images/uIEnhancementImages/table_title_corner2.gif" alt="Page Title - Specimen List" width="31" height="24" /></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td class="tablepadding"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="90%" valign="bottom" class="td_tab_bg">&nbsp;</td>
      </tr>
    </table>
  
   <table width="100%" border="0" cellpadding="3" cellspacing="0" class="whitetable_bg">
      
     <tr>
        <td colspan="2" align="left" class="toptd">
		<%@ include file="/pages/content/common/ActionErrors.jsp" %>
		</td>
      </tr>
      
	  <tr>
        <!--<td colspan="2" align="left" class="tr_bg_blue1"><span class="blue_ar_b"> &nbsp;Specimen List &nbsp;</span></td>-->
      </tr>
     
      <tr>
        <td colspan="2">
		<table width="99%" border="0" align="center" cellpadding="0" cellspacing="0">
           
			<tr >
			<td width="25%">
			
			</td>
	<td width="75%">
	
	</td>
		</tr>
		<!--  **************  Code for New Grid  *********************** -->	
		<tr>
			<td class="black_ar" colspan="2">
				<label><b>Select a list to view:<b></label>
			
				<select name="specimenLists" size="1" class="formFieldSizedNew" onChange="reloadGrid(this)" id="tagName">
				<%
					List<NameValueBean> labelList =  (List)request.getAttribute("dropDownList");
					if(labelList !=null && labelList.size() > 0)
		{
			for (NameValueBean object : labelList) 
			{
				%>
				<option name="Australia" value="<%=object.getValue()%>" ><%=object.getName()%></option>
<%				
			}
		}
%>
				</select>
				
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="    Delete List     " />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;
			</td>
			</tr>
			<tr>
			<td colspan="2">
			<table><tr>
				<td align="left" class="black_ar" >
					<input type='checkbox' name='checkAll1' id='checkAll1' property="" onClick='checkAll(this)'>
					</td>
					<td valign="middle" class="black_ar" align="left">
				<span valign="middle"><bean:message key="buttons.checkAll" /></span>
				</td>
				</tr>
				</table>
				</td>
			</tr>
			<tr>
			<td  valign="top" colspan="2">
			  <div id="specimenGrid" width='100%' height='100%' style='overflow:hidden'>
			</td>
           </tr>
		   <tr>
			<td colspan="2">&nbsp;
			<div id="hiddenDiv" style="display:hidden">	
				<table id="hiddenTab">
				</table>
			</div>
			</td>
			</tr>
		   <tr>
			<td>
			&nbsp; &nbsp;<html:button styleClass="black_ar" property="deleteCart" onclick="onDelete()">
				<bean:message key="buttons.delete"/>
			</html:button>
		
			&nbsp;<html:button styleClass="black_ar" property="exportCart" onclick="onExport()">
				<bean:message key="buttons.export"/>
			</html:button>
			</td>
			<td>
				
			</td>
		   </tr>
   <!--  **************  Code for New Grid  *********************** -->
		</table></td>
      </tr>
<tr>
        <td colspan="2">
         
      </tr>
	<tr>
        <td colspan="2" class="bottomtd"></td>
    </tr>	
	
	<tr>
		
        <td colspan="2" align="left" class="tr_bg_blue1">
			<label for="selectLabel">&nbsp;<span class="blue_ar_b"><bean:message key="mylist.label.selectLabel" /> </span>
			</label>
		</td>
	</tr>
	
	<tr>
        <td colspan="2" class="black_ar">
		<table width="100%" border="0" cellpadding="1" cellspacing="0">
          <tr>
			 <td class="black_ar" width="2%"><input type="radio" name="chkName"      value="OrderSpecimen" onclick="showHideComponents()" checked=true <%=disabledOrder%> ></td>
             <td class="black_ar" width="23%" ><bean:message key="mylist.label.orderBioSpecimen"/></td>
			 <td class="black_ar" width="2%"><INPUT TYPE='RADIO' NAME='chkName' onclick="showHideComponents()" value="Specimenpage" <%=disabled%> ></td>
			 <td class="black_ar" width="23%" ><bean:message key="mylist.label.multipleSpecimenPage"/>
               </td>
			<td class="black_ar" width="2%"><INPUT TYPE='RADIO' NAME='chkName'     onclick="showHideComponents()" id="ch1" value="Events" <%=disabled%> ></INPUT></td>
            
			<td class="black_ar" width="15%" ><bean:message key="mylist.label.specimenEvent"/> </td>
			 
		
			
		   <td class="black_ar" width="33%">
		 	   <div id="eventlist1" style="display:none">
			
			   <autocomplete:AutoCompleteTag  property="specimenEventParameter" 
						  optionsList = "<%=request.getAttribute("eventArray")%>" styleClass="black_ar" size="27"
						  initialValue="Transfer"/>
				 </div> 
		     
			   <div id="eventlist2" style="display:block"><input type="text" styleClass="black_ar" size="25" id="specimenEventParameter1" name="specimenEventParameter" value="Transfer" readonly="true"/></div>
             </td>
		  </tr>
		
          <tr>
			<td class="black_ar"><input type="radio" name="chkName" onclick="showHideComponents()" value="requestShipment" <%=disabledShipping%> ></td>
            <td class="black_ar" ><bean:message key="shipment.request"/></td>			
			<td class="black_ar"><input type="radio" name="chkName"  value="createShipment" onclick="showHideComponents()" <%=disabledShipping%> ></td>
            <td class="black_ar" ><bean:message key="shipment.create"/></td>
            
            
            		<td class="black_ar"><input type="radio" name="chkName"  value="distributeOrder" onclick="showHideComponents()" <%=disabledShipping%> ></td>
            <td class="black_ar" >Distribute</td>
            
			<td class="black_ar"><input type="radio" name="chkName"
								value="printLabels" id="printCheckbox"
								onclick="showHideComponents()"><bean:message
								key="mylist.label.printLabels" /></td>
							<td>
		  </tr>
        </table>          
      </tr>
<tr>
			<td class="bottomtd"></td></tr>
<tr>
       <td colspan="2" class="buttonbg"> <html:button styleClass="blue_ar_b" property="proceed" onclick="onSubmit(this.form.orderedString)" disabled="<%=disabledButton%>" >
				<bean:message key="buttons.submit"/>	
			</html:button></td>
      </tr>
	
   </table></td></tr>
	
    
 
    <tr>
		<td>
			<input type="hidden" name="orderedString">
		</td>
    </tr>
  </table>
<script language="JavaScript" type="text/javascript">
	//showHideComponents();
</script>
	</body>
	</html:form>
</html:html>