package com.adventnet.sym.webclient.mdm.enroll;

import java.util.List;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMViewRetrieverAction;

public class PendingRequestTRAction extends DMViewRetrieverAction
{
    public Logger logger;
    
    public PendingRequestTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    private void modifyToRBDAQuery(final SelectQuery selectQuery) {
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (loginID != null) {
                final Boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginID, true);
                if (!isMDMAdmin) {
                    selectQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    selectQuery.addJoin(new Join("CustomGroupMemberRel", "UserCustomGroupMapping", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, "CustomGroupMemberRel", "DeviceGroups", 1));
                    final Criteria cgCriteria = new Criteria(Column.getColumn("DeviceGroups", "GROUP_RESOURCE_ID", "DeviceGroups.GROUP_RESOURCE_ID"), (Object)null, 1);
                    final Criteria cgReqCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "USER_ID"), (Object)userID, 0);
                    final Criteria mdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
                    final Criteria nomdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
                    final Criteria userCustomGroupCriteria = new Criteria(Column.getColumn("DeviceGroups", "LOGIN_ID", "DeviceGroups.LOGIN_ID"), (Object)loginID, 0);
                    Criteria criteria = selectQuery.getCriteria();
                    if (criteria == null) {
                        criteria = userCustomGroupCriteria.and(mdCriteria.and(cgCriteria)).or(nomdCriteria.and(cgReqCriteria));
                    }
                    else {
                        criteria = criteria.and(userCustomGroupCriteria.and(mdCriteria.and(cgCriteria)).or(nomdCriteria.and(cgReqCriteria)));
                    }
                    selectQuery.setCriteria(criteria);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(PendingRequestTRAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final HttpServletRequest request = viewCtx.getRequest();
            final String ownedBy = request.getParameter("ownedBy");
            final String platform = request.getParameter("platformType");
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final HashMap enrollParamMap = MDMEnrollmentUtil.getInstance().getLastEnrollParamMap();
            final int ownedByInt = enrollParamMap.get("OWNED_BY");
            final int platformType = enrollParamMap.get("PLATFORM_TYPE");
            final String selectedStatus = request.getParameter("selectedStatus");
            final Long defaultMDMGroupId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerID, platformType, ownedByInt);
            request.setAttribute("pendingviewtoolID", (Object)"40080");
            request.setAttribute("ownedBy", (Object)ownedBy);
            request.setAttribute("platformType", (Object)platform);
            request.setAttribute("selectedStatus", (Object)selectedStatus);
            request.setAttribute("defaultMDMGroupId", (Object)defaultMDMGroupId);
            final Boolean isDeviceProvisioningUser = ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
            request.setAttribute("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
            final List mdmGpList = MDMGroupHandler.getMDMNonUserGroups();
            if (mdmGpList != null) {
                request.setAttribute("mdmGroupList", (Object)mdmGpList);
            }
            Criteria criteria = null;
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            Criteria cCust = null;
            if (customerId != null) {
                cCust = new Criteria(new Column("UserResource", "CUSTOMER_ID"), (Object)customerId, 0);
            }
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Criteria statusCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0).or(new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0).and(new Criteria(new Column("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID"), (Object)null, 0)));
            final Criteria authModeCri = new Criteria(new Column("DeviceEnrollmentRequest", "AUTH_MODE"), (Object)4, 1);
            criteria = statusCriteria.and(authModeCri);
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID != null) {
                final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin) {
                    final Criteria userIdCri = new Criteria(new Column("DeviceEnrollmentRequest", "USER_ID"), (Object)userID, 0);
                    criteria = criteria.and(userIdCri);
                }
            }
            String selectedTab = (String)request.getAttribute("selectedTabInput");
            selectedTab = ((selectedTab != null) ? selectedTab : "pending");
            request.setAttribute("selectedTabInput", (Object)selectedTab);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FulldeviceContainerUi")) {
                request.setAttribute("FulldeviceContainerUi", (Object)true);
            }
            else {
                request.setAttribute("FulldeviceContainerUi", (Object)false);
            }
            if (ownedBy != null && !ownedBy.equals("-1")) {
                final Criteria ownedBycriteria = new Criteria(new Column("DeviceEnrollmentRequest", "OWNED_BY"), (Object)new Integer(ownedBy), 0);
                if (criteria == null) {
                    criteria = ownedBycriteria;
                }
                else {
                    criteria = criteria.and(ownedBycriteria);
                }
            }
            if (platform != null && !platform.equals("-1")) {
                final Criteria platformcriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)new Integer(platform), 0);
                if (criteria == null) {
                    criteria = platformcriteria;
                }
                else {
                    criteria = criteria.and(platformcriteria);
                }
            }
            if (selectedStatus != null && !selectedStatus.equals("-1") && (selectedStatus.equals("1") || selectedStatus.equals("0"))) {
                final Criteria selectedstatusCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer(selectedStatus), 0).and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0));
                if (criteria == null) {
                    criteria = selectedstatusCriteria;
                }
                else {
                    criteria = criteria.and(selectedstatusCriteria);
                }
            }
            final String mdmGroupIdStr = request.getParameter("mdmGroupId");
            if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                request.setAttribute("mdmGroupId", (Object)mdmGroupIdStr);
                final Long mdmGroupId = new Long(mdmGroupIdStr);
                if (loginID != null) {
                    query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToGroup", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                    final Criteria cgReqCriteria = new Criteria(Column.getColumn("EnrollmentRequestToGroup", "GROUP_RESOURCE_ID"), (Object)mdmGroupId, 0);
                    final Criteria nomdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
                    if (criteria == null) {
                        criteria = nomdCriteria.and(cgReqCriteria);
                    }
                    else {
                        criteria = criteria.and(nomdCriteria.and(cgReqCriteria));
                    }
                }
            }
            if (cCust != null) {
                if (criteria == null) {
                    criteria = cCust;
                }
                else {
                    criteria = criteria.and(cCust);
                }
            }
            criteria = criteria.and(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)null, 0).or(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)1, 0)));
            query.setCriteria(criteria);
            this.modifyToRBDAQuery(query);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            Logger.getLogger(PendingRequestTRAction.class.getName()).log(Level.SEVERE, "Exception in PendingRequestTRAction:setCriteria()", ex);
        }
    }
}
