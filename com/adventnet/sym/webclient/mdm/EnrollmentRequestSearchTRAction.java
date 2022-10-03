package com.adventnet.sym.webclient.mdm;

import java.util.List;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
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

public class EnrollmentRequestSearchTRAction extends DMViewRetrieverAction
{
    public Logger logger;
    
    public EnrollmentRequestSearchTRAction() {
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
            Logger.getLogger(EnrollmentRequestSearchTRAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final HttpServletRequest request = viewCtx.getRequest();
            final String selectedStatus = request.getParameter("selectedStatus");
            final String ownedBy = request.getParameter("ownedBy");
            final String platform = request.getParameter("platformType");
            final String period = viewCtx.getRequest().getParameter("period");
            final String startDate = viewCtx.getRequest().getParameter("startDate");
            final String endDate = viewCtx.getRequest().getParameter("endDate");
            final String templateType = viewCtx.getRequest().getParameter("templateType");
            final String viewName = viewCtx.getUniqueId();
            final Integer type = (request.getParameter("type") != null && !request.getParameter("type").isEmpty()) ? Integer.parseInt(request.getParameter("type")) : -1;
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final HashMap enrollParamMap = MDMEnrollmentUtil.getInstance().getLastEnrollParamMap();
            final int ownedByInt = enrollParamMap.get("OWNED_BY");
            final int platformType = enrollParamMap.get("PLATFORM_TYPE");
            final Long defaultMDMGroupId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerID, platformType, ownedByInt);
            request.setAttribute("toolID", (Object)"40021");
            request.setAttribute("ownedBy", (Object)ownedBy);
            request.setAttribute("platformType", (Object)platform);
            request.setAttribute("defaultMDMGroupId", (Object)defaultMDMGroupId);
            final Boolean isDeviceProvisioningUser = ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
            request.setAttribute("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
            if (viewName.equalsIgnoreCase("DevicesAwaitingLicense")) {
                request.setAttribute("isDCEE", (Object)ProductUrlLoader.getInstance().getValue("productcode").equals("DCEE"));
                request.setAttribute("licenseType", (Object)LicenseProvider.getInstance().getProductCategoryString());
                request.setAttribute("licenseCostType", (Object)LicenseProvider.getInstance().getLicenseType());
            }
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
            if (selectedStatus != null && !selectedStatus.equals("-1")) {
                request.setAttribute("selectedStatus", (Object)selectedStatus);
                criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(selectedStatus), 0);
            }
            else {
                criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 2, 4 }, 8);
            }
            final String selectedTab = (request.getParameter("selectedEnrollTab") == null) ? "enrolled" : request.getParameter("selectedEnrollTab");
            request.setAttribute("selectedTabInput", (Object)selectedTab);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FulldeviceContainerUi")) {
                request.setAttribute("FulldeviceContainerUi", (Object)true);
            }
            else {
                request.setAttribute("FulldeviceContainerUi", (Object)false);
            }
            final Criteria enrollNotSelfReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 1);
            final Criteria enrollSelfReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 0);
            final Criteria enrollStatusCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0);
            final Criteria managedDeviceNameCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)null, 1);
            final Criteria selfEnrollDeviceCheckedIn = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "UDID"), (Object)null, 1);
            final Criteria enrollReqCri = enrollNotSelfReqCri.or(enrollSelfReqCri.and(enrollStatusCri.or(managedDeviceNameCri).or(selfEnrollDeviceCheckedIn)));
            if (criteria == null) {
                criteria = enrollReqCri;
            }
            else {
                criteria = criteria.and(enrollReqCri);
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
            final String mdmGroupIdStr = request.getParameter("mdmGroupId");
            if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                request.setAttribute("mdmGroupId", (Object)mdmGroupIdStr);
                final Long mdmGroupId = new Long(mdmGroupIdStr);
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                if (loginID != null) {
                    query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToGroup", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                    final Criteria cgCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)mdmGroupId, 0);
                    final Criteria cgReqCriteria = new Criteria(Column.getColumn("EnrollmentRequestToGroup", "GROUP_RESOURCE_ID"), (Object)mdmGroupId, 0);
                    final Criteria mdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
                    final Criteria nomdCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
                    if (criteria == null) {
                        criteria = mdCriteria.and(cgCriteria).or(nomdCriteria.and(cgReqCriteria));
                    }
                    else {
                        criteria = criteria.and(mdCriteria.and(cgCriteria).or(nomdCriteria.and(cgReqCriteria)));
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
            if (type != -1) {
                if (criteria == null) {
                    criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)type, 0);
                }
                else {
                    criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)type, 0));
                }
            }
            if (!MDMStringUtils.isEmpty(period)) {
                final Calendar cal = Calendar.getInstance();
                int noOfDays = Integer.parseInt(period);
                if (noOfDays != 0) {
                    noOfDays *= -1;
                    final Date today = new Date();
                    cal.setTime(today);
                    cal.add(5, noOfDays);
                    cal.set(11, 0);
                    cal.set(12, 0);
                    cal.set(13, 0);
                    final long filter = cal.getTime().getTime();
                    final Criteria periodCrit = new Criteria(Column.getColumn("ManagedDevice", "REGISTERED_TIME"), (Object)filter, 4);
                    if (criteria == null) {
                        criteria = periodCrit;
                    }
                    else {
                        criteria = criteria.and(periodCrit);
                    }
                }
            }
            if (!MDMStringUtils.isEmpty(startDate) && !MDMStringUtils.isEmpty(endDate)) {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                long start = 0L;
                long end = 0L;
                try {
                    start = sdf.parse(startDate).getTime();
                    end = sdf.parse(endDate).getTime();
                    if (start > end) {
                        final long temp = start;
                        start = end;
                        end = temp;
                    }
                    end += 86400000L;
                }
                catch (final ParseException exp) {
                    this.logger.log(Level.WARNING, "Exception occured while parsing start and end date in enrollment view ", exp);
                }
                final Criteria criteria2 = new Criteria(Column.getColumn("ManagedDevice", "REGISTERED_TIME"), (Object)start, 4);
                final Criteria criteria3 = new Criteria(Column.getColumn("ManagedDevice", "REGISTERED_TIME"), (Object)end, 6);
                final Criteria periodCrit2 = criteria2.and(criteria3);
                if (criteria == null) {
                    criteria = periodCrit2;
                }
                else {
                    criteria = criteria.and(periodCrit2);
                }
            }
            final Criteria deviceForEnroll = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
            final Criteria noDeviceForEnroll = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 0);
            final Criteria deviceToUser = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
            final Criteria nonTemplateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 0);
            final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 1);
            final Criteria adminEnrollmentCriteria = templateCriteria.and(noDeviceForEnroll.or(deviceForEnroll.and(deviceToUser))).or(nonTemplateCriteria);
            criteria = criteria.and(adminEnrollmentCriteria);
            if (templateType != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)Integer.valueOf(templateType), 0));
            }
            query.setCriteria(criteria);
            this.modifyToRBDAQuery(query);
            query.setCriteria(query.getCriteria());
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentRequestSearchTRAction.class.getName()).log(Level.SEVERE, "Exception in EnrollmentRequestSearchTRAction:setCriteria()", ex);
        }
    }
}
