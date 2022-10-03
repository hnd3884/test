package com.adventnet.sym.webclient.mdm;

import com.me.mdm.server.customgroup.GroupFacade;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.List;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class EnrollmentRequestMigratedTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public EnrollmentRequestMigratedTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
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
            final String modelType = viewCtx.getRequest().getParameter("deviceType");
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
                if (selectedStatus.equals("1") || selectedStatus.equals("0")) {
                    final Criteria statusCriteria = criteria = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer(selectedStatus), 0).and(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0));
                }
                else {
                    criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(selectedStatus), 0);
                }
            }
            else {
                criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 2, 4, 5 }, 8);
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
            if (modelType != null && modelType != "" && !"all".equals(modelType)) {
                request.setAttribute("modelType", (Object)modelType);
                final int modeltype = Integer.parseInt(modelType);
                if (modeltype == 12) {
                    final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new int[] { 1, 2 }, 8, false);
                    criteria = criteria.and(modelCriteria);
                }
                else {
                    final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)modeltype, 0, false);
                    criteria = criteria.and(modelCriteria);
                }
                final String multiUser = request.getParameter("isMultiUser");
                if (Integer.parseInt(modelType) == 2 && multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                    criteria = criteria.and(new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)multiUser, 0));
                }
            }
            final String mdmGroupIdStr = request.getParameter("mdmGroupId");
            if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                request.setAttribute("mdmGroupId", (Object)mdmGroupIdStr);
                final Long mdmGroupId = new Long(mdmGroupIdStr);
                final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                if (loginID != null) {
                    final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                    if (isMDMAdmin) {
                        query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
                    }
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
            query.setCriteria(query.getCriteria().and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0).or(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)7, 1))));
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentRequestTRAction.class.getName()).log(Level.SEVERE, "Exception in EnrollmentRequestTRAction:setCriteria()", ex);
        }
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        try {
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Resource.RESOURCE_ID");
            final HashMap hashMap = new GroupFacade().getAssociatedGroupsForResList(list);
            viewCtx.getRequest().setAttribute("ASSOCIATED_GROUP_NAMES", (Object)hashMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while Add Group Names..", e);
        }
    }
}
