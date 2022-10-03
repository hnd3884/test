package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import java.util.Iterator;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMViewRetrieverAction;

public class UnassignedDevicesTRAction extends DMViewRetrieverAction
{
    public Logger logger;
    
    public UnassignedDevicesTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final HttpServletRequest request = viewCtx.getRequest();
            Boolean allowDeviceDetailsToBeAddedBeforeDeviceEnrollment = Boolean.FALSE;
            final String viewname = viewCtx.getUniqueId();
            Criteria criteria = null;
            final Criteria enrollReqCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0);
            Criteria templatecriteria = null;
            final String enrollmentTemplateStr = request.getParameter("enrollmentTemplate");
            if (enrollmentTemplateStr != null && !enrollmentTemplateStr.isEmpty()) {
                final Integer enrollmentTemplate = Integer.parseInt(enrollmentTemplateStr);
                String derivedTableName = "";
                switch (enrollmentTemplate) {
                    case 20: {
                        derivedTableName = "AndroidNFCDeviceForEnrollment";
                        break;
                    }
                    case 11: {
                        derivedTableName = "AppleConfDeviceForEnrollment";
                        break;
                    }
                    case 10: {
                        derivedTableName = "AppleDEPDeviceForEnrollment";
                        break;
                    }
                    case 21: {
                        derivedTableName = "KNOXMobileDeviceForEnrollment";
                        break;
                    }
                    case 22: {
                        derivedTableName = "AndroidQRDeviceForEnrollment";
                        break;
                    }
                    case 23: {
                        derivedTableName = "AndroidZTDeviceForEnrollment";
                        break;
                    }
                    case 30: {
                        derivedTableName = "WindowsICDDeviceForEnrollment";
                        break;
                    }
                    case 31: {
                        derivedTableName = "WinLaptopDeviceForEnrollment";
                        allowDeviceDetailsToBeAddedBeforeDeviceEnrollment = Boolean.TRUE;
                        break;
                    }
                    case 32: {
                        derivedTableName = "WinAzureADDeviceForEnrollment";
                        break;
                    }
                    case 40: {
                        derivedTableName = "GSChromeDeviceForEnrollment";
                        break;
                    }
                    case 33: {
                        derivedTableName = "WinModernMgmtDeviceForEnrollment";
                        break;
                    }
                    case 12: {
                        derivedTableName = "MacModernMgmtDeviceForEnrollment";
                        break;
                    }
                }
                templatecriteria = new Criteria(new Column(derivedTableName, "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                query.setCriteria(templatecriteria);
                if (viewname.equalsIgnoreCase("UnassignedDeviceListEmberView") || viewname.equalsIgnoreCase("UnassignedDeviceExportView")) {
                    final Criteria userAssignCriteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)null, 0);
                    final Criteria criteria2 = enrollReqCriteria.negate().or(userAssignCriteria.negate());
                    final Criteria criteria3 = criteria = enrollReqCriteria.or(userAssignCriteria);
                    if (!allowDeviceDetailsToBeAddedBeforeDeviceEnrollment) {
                        criteria = criteria2.and(criteria3);
                    }
                }
                if (viewname.equalsIgnoreCase("UnassignedDeviceExportView")) {
                    criteria = criteria.and(enrollReqCriteria.negate());
                }
            }
            if (viewname.equalsIgnoreCase("UnassignedDeviceExportView")) {
                final Criteria inEnrolling = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0).and(new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)null, 1)).and(new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0));
                criteria = ((criteria == null) ? inEnrolling.negate() : criteria.and(inEnrolling.negate()));
            }
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            final Criteria customerCriteria = new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0);
            criteria = ((criteria == null) ? customerCriteria : criteria.and(customerCriteria));
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID != null) {
                final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin && enrollmentTemplateStr != null && !enrollmentTemplateStr.isEmpty()) {
                    query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                    query.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
                    query.addJoin(new Join("EnrollmentTemplate", "AaaUser", new String[] { "ADDED_USER" }, new String[] { "USER_ID" }, 1));
                    try {
                        RBDAUtil.getInstance().modifyRBDAQueryByTechnician(query);
                    }
                    catch (final Exception ex) {
                        Logger.getLogger(UnassignedDevicesTRAction.class.getName()).log(Level.SEVERE, "{0}", ex);
                    }
                }
            }
            final String[] searchColumns = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
            final String[] searchValues = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
            final Criteria searchCriteria = this.overrideColumnSearch(viewCtx, searchColumns, searchValues);
            if (searchCriteria != null) {
                criteria = ((criteria != null) ? criteria.and(searchCriteria) : criteria);
            }
            final Criteria existingCriteria = query.getCriteria();
            if (existingCriteria == null) {
                query.setCriteria(criteria);
            }
            else {
                query.setCriteria(existingCriteria.and(criteria));
            }
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in  : UnassignedDevicesTRAction:setCriteria()", e);
        }
    }
    
    private Criteria overrideColumnSearch(final ViewContext viewCtx, final String[] searchColumnsArray, final String[] searchValueArray) {
        final String viewname = viewCtx.getUniqueId();
        if ((viewname.equalsIgnoreCase("UnassignedDeviceListEmberView") || viewname.equalsIgnoreCase("depManagedDeviceView")) && searchColumnsArray != null && searchColumnsArray.length != 0 && (Arrays.stream(searchColumnsArray).anyMatch("DeviceForEnrollment.IMEI"::equals) || Arrays.stream(searchColumnsArray).anyMatch("DeviceForEnrollment.SERIAL_NUMBER"::equals) || Arrays.stream(searchColumnsArray).anyMatch("UserResource.NAME"::equals) || Arrays.stream(searchColumnsArray).anyMatch("ManagedUser.EMAIL_ADDRESS"::equals))) {
            final HashMap searchValuesMap = new HashMap();
            String newSearchColumn = "";
            String newSearchValue = "";
            final String[] searchColumns = searchColumnsArray;
            final String[] searchValues = searchValueArray;
            for (int i = 0; i < searchColumns.length; ++i) {
                if (searchColumns[i].equals("DeviceForEnrollment.IMEI")) {
                    searchValuesMap.put("imei", searchValues[i]);
                }
                else if (searchColumns[i].equals("UserResource.NAME")) {
                    searchValuesMap.put("username", searchValues[i]);
                }
                else if (searchColumns[i].equals("ManagedUser.EMAIL_ADDRESS")) {
                    searchValuesMap.put("mail", searchValues[i]);
                }
                else if (searchColumns[i].equals("DeviceForEnrollment.SERIAL_NUMBER")) {
                    searchValuesMap.put("serial_no", searchValues[i]);
                }
                else {
                    newSearchColumn = newSearchColumn + searchColumns[i] + ",";
                    newSearchValue = newSearchValue + searchValues[i] + ",";
                }
            }
            viewCtx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)newSearchColumn);
            viewCtx.setStateOrURLStateParam("SEARCH_VALUE", (Object)newSearchValue);
            return this.getSearchCriteria(searchValuesMap);
        }
        return null;
    }
    
    private Criteria getSearchCriteria(final HashMap searchValuesMap) {
        Criteria searchCriteria = null;
        for (final Object key : searchValuesMap.keySet()) {
            final String searchValue = searchValuesMap.get(key);
            Criteria columnCriteria = null;
            if (searchValue != null && !searchValue.isEmpty()) {
                if (key.equals("imei")) {
                    final Criteria imeiCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "IMEI"), (Object)searchValue, 12);
                    final Criteria mdDeviceImeiCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "IMEI"), (Object)searchValue, 12);
                    columnCriteria = imeiCriteria.or(mdDeviceImeiCriteria);
                }
                else if (key.equals("mail")) {
                    final Criteria assignedUserMailCriteria = new Criteria(Column.getColumn("AssignedManagedUser", "EMAIL_ADDRESS"), (Object)searchValue, 12);
                    final Criteria mailCriteria = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)searchValue, 12);
                    columnCriteria = assignedUserMailCriteria.or(mailCriteria);
                }
                else if (key.equals("username")) {
                    final Criteria assignedUsernameCriteria = new Criteria(Column.getColumn("AssignedUserResource", "NAME"), (Object)searchValue, 12);
                    final Criteria usernameCriteria = new Criteria(Column.getColumn("UserResource", "NAME"), (Object)searchValue, 12);
                    columnCriteria = assignedUsernameCriteria.or(usernameCriteria);
                }
                else if (key.equals("serial_no")) {
                    columnCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)searchValue, 12).or(new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)searchValue, 12));
                }
                searchCriteria = ((searchCriteria == null) ? columnCriteria : searchCriteria.and(columnCriteria));
            }
        }
        return searchCriteria;
    }
}
