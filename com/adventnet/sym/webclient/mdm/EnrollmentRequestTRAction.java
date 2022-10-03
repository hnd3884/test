package com.adventnet.sym.webclient.mdm;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.util.List;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import java.text.ParseException;
import java.util.logging.Level;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;

public class EnrollmentRequestTRAction extends MDMEmberSqlViewController
{
    public Logger logger;
    
    public EnrollmentRequestTRAction() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String result = "";
        String sjoin = "";
        final HttpServletRequest request = viewCtx.getRequest();
        final Boolean isDeviceProvisioningUser = ManagedDeviceHandler.getInstance().isDeviceProvisioningUser();
        request.setAttribute("isDeviceProvisioningUser", (Object)isDeviceProvisioningUser);
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            return DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equals("SCRITERIA")) {
            try {
                String criteria = "";
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                final String selectedStatus = request.getParameter("selectedStatus");
                final String ownedBy = request.getParameter("ownedBy");
                final String platform = request.getParameter("platformType");
                final String period = viewCtx.getRequest().getParameter("period");
                final String lastContactPeriod = viewCtx.getRequest().getParameter("last_contact_period");
                final String startDate = viewCtx.getRequest().getParameter("startDate");
                final String endDate = viewCtx.getRequest().getParameter("endDate");
                final String templateType = viewCtx.getRequest().getParameter("templateType");
                final String modelType = viewCtx.getRequest().getParameter("deviceType");
                final String authTokenFilter = viewCtx.getRequest().getParameter("authToken");
                final String viewName = viewCtx.getUniqueId();
                final Integer type = (request.getParameter("type") != null && !request.getParameter("type").isEmpty()) ? Integer.parseInt(request.getParameter("type")) : -1;
                final Long customerID = MSPWebClientUtil.getCustomerID(request);
                final HashMap enrollParamMap = MDMEnrollmentUtil.getInstance().getLastEnrollParamMap();
                final int ownedByInt = enrollParamMap.get("OWNED_BY");
                final int platformType = enrollParamMap.get("PLATFORM_TYPE");
                final Long defaultMDMGroupId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerID, platformType, ownedByInt);
                String uemTrailEnableParameter = UserMgmtUtil.getUserMgmtParameter("UEMTrailEnable");
                uemTrailEnableParameter = ((uemTrailEnableParameter != null) ? uemTrailEnableParameter : "");
                request.setAttribute("toolID", (Object)"40021");
                request.setAttribute("ownedBy", (Object)ownedBy);
                request.setAttribute("platformType", (Object)platform);
                request.setAttribute("defaultMDMGroupId", (Object)defaultMDMGroupId);
                if (viewName.equalsIgnoreCase("DevicesAwaitingLicense")) {
                    request.setAttribute("isDCEE", (Object)ProductUrlLoader.getInstance().getValue("productcode").equals("DCEE"));
                    request.setAttribute("licenseType", (Object)LicenseProvider.getInstance().getProductCategoryString());
                    request.setAttribute("licenseCostType", (Object)LicenseProvider.getInstance().getLicenseType());
                    request.setAttribute("UEMTrail", (Object)uemTrailEnableParameter);
                }
                final Boolean isBulkDeprovisionEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("BULK_DEPROVISION");
                request.setAttribute("isBulkDeprovisionEnabled", (Object)isBulkDeprovisionEnabled);
                final List mdmGpList = MDMGroupHandler.getMDMNonUserGroups();
                if (mdmGpList != null) {
                    request.setAttribute("mdmGroupList", (Object)mdmGpList);
                }
                final Long customerId = MSPWebClientUtil.getCustomerID(request);
                if (customerId != null) {
                    criteria = criteria + " (UserResource.CUSTOMER_ID = " + customerId + ")";
                }
                final String selectedTab = (request.getParameter("selectedEnrollTab") == null) ? "enrolled" : request.getParameter("selectedEnrollTab");
                request.setAttribute("selectedTabInput", (Object)selectedTab);
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FulldeviceContainerUi")) {
                    request.setAttribute("FulldeviceContainerUi", (Object)true);
                }
                else {
                    request.setAttribute("FulldeviceContainerUi", (Object)false);
                }
                final String enrollNotSelfReqCri = " (DeviceEnrollmentRequest.ENROLLMENT_TYPE != " + String.valueOf(2) + ") ";
                final String enrollSelfReqCri = " (DeviceEnrollmentRequest.ENROLLMENT_TYPE = " + String.valueOf(2) + ") ";
                final String enrollStatusCri = " (DeviceEnrollmentRequest.REQUEST_STATUS = " + String.valueOf(3) + ") ";
                final String managedDeviceNameCri = " (ManagedDeviceExtn.NAME IS NOT NULL) ";
                final String selfEnrollDeviceCheckedIn = " (DeviceEnrollmentRequest.UDID IS NOT NULL) ";
                final String enrollReqCri = " ( ( " + enrollNotSelfReqCri + " ) OR (( " + enrollSelfReqCri + ") AND ((( " + enrollStatusCri + " ) OR ( " + managedDeviceNameCri + ")) OR ( " + selfEnrollDeviceCheckedIn + "))))";
                criteria = criteria + " and " + enrollReqCri;
                final String deviceForEnroll = " (DeviceForEnrollment.ENROLLMENT_DEVICE_ID is not null)";
                final String noDeviceForEnroll = " (DeviceForEnrollment.ENROLLMENT_DEVICE_ID is null)";
                final String deviceToUser = " (DeviceEnrollmentToUser.ENROLLMENT_DEVICE_ID is not null)";
                final String nonTemplateCriteria = " (EnrollmentTemplate.TEMPLATE_ID is null)";
                final String templateCriteria = " (EnrollmentTemplate.TEMPLATE_ID is not null)";
                final String adminEnrollmentCriteria = " ( " + templateCriteria + " and (" + noDeviceForEnroll + " or ( " + deviceForEnroll + " and " + deviceToUser + " ))) or ( " + nonTemplateCriteria + ")";
                criteria = criteria + " and ( " + adminEnrollmentCriteria + " )";
                if (templateType != null) {
                    criteria = criteria + " and " + "(EnrollmentTemplate.TEMPLATE_TYPE = " + Integer.valueOf(templateType) + ")";
                }
                if (selectedStatus != null && !selectedStatus.equals("-1")) {
                    if (selectedStatus.equals("0")) {
                        final JSONObject thresholdJson = new InactiveDevicePolicyTask().getInactiveDevicePolicyThresholdValues(customerID);
                        final Long inactiveThreshold = (Long)thresholdJson.get("InactiveThreshold");
                        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
                        final Long inactiveTime = currentTime - inactiveThreshold;
                        criteria = " (" + criteria + ") and (AgentContact.LAST_CONTACT_TIME <= " + inactiveTime + ") and (ManagedDevice.MANAGED_STATUS = 2)";
                    }
                    else {
                        request.setAttribute("selectedStatus", (Object)selectedStatus);
                        criteria = " (" + criteria + ") and (ManagedDevice.MANAGED_STATUS = " + selectedStatus + ")";
                    }
                }
                else {
                    criteria = " (" + criteria + ") and (ManagedDevice.MANAGED_STATUS IN (2,4,5))";
                }
                if (ownedBy != null && !ownedBy.equals("-1")) {
                    criteria = " (" + criteria + ") and DeviceEnrollmentRequest.OWNED_BY = " + ownedBy;
                }
                if (platform != null && !platform.equals("-1") && !platform.equals("5") && !platform.equals("1") && !platform.equals("6") && !platform.equals("7")) {
                    criteria = " (" + criteria + ") and DeviceEnrollmentRequest.PLATFORM_TYPE = " + platform;
                }
                if (platform != null && !platform.equals("-1") && (platform.equals("5") || platform.equals("1") || platform.equals("6") || platform.equals("7"))) {
                    criteria = " (" + criteria + ") and DeviceEnrollmentRequest.PLATFORM_TYPE = " + new Integer(1);
                    final String s = platform;
                    switch (s) {
                        case "6": {
                            criteria = " (" + criteria + ") and (MdModelInfo.MODEL_TYPE IN (3,4)) ";
                            break;
                        }
                        case "7": {
                            criteria = " (" + criteria + ") and (MdModelInfo.MODEL_TYPE = 5) ";
                            break;
                        }
                        default: {
                            criteria = " (" + criteria + ") and (MdModelInfo.MODEL_TYPE NOT IN (3,4)) ";
                            break;
                        }
                    }
                }
                final String mdmGroupIdStr = request.getParameter("mdmGroupId");
                if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                    request.setAttribute("mdmGroupId", (Object)mdmGroupIdStr);
                    final Long mdmGroupId = new Long(mdmGroupIdStr);
                    final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    if (loginID != null) {
                        final String cgCriteria = " (CustomGroupMemberRel.GROUP_RESOURCE_ID = " + mdmGroupId + ")";
                        final String mdCriteria = " (ManagedDevice.RESOURCE_ID is not null)";
                        final String nomdCriteria = " (ManagedDevice.RESOURCE_ID is null)";
                        criteria = criteria + " and ( " + mdCriteria + " and " + cgCriteria + " or ( " + nomdCriteria + "))";
                    }
                }
                if (type != -1) {
                    criteria = criteria + " and ( DeviceEnrollmentRequest.ENROLLMENT_TYPE = " + type + ")";
                }
                if (modelType != null && modelType != "" && !modelType.equalsIgnoreCase("all")) {
                    final int model = Integer.parseInt(modelType);
                    criteria = criteria + "and (MdModelInfo.MODEL_TYPE = " + model + ")";
                    final String multiUser = request.getParameter("isMultiUser");
                    if (Integer.parseInt(modelType) == 2 && multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                        criteria = criteria + "and (MdDeviceInfo.IS_MULTIUSER = '" + multiUser + "')";
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
                        final String periodCrit = " (ManagedDevice.REGISTERED_TIME >= " + filter + ")";
                        criteria = criteria + " and " + periodCrit;
                    }
                }
                if (!MDMStringUtils.isEmpty(lastContactPeriod)) {
                    final Calendar cal = Calendar.getInstance();
                    int noOfDays = Integer.parseInt(lastContactPeriod);
                    if (noOfDays != 0) {
                        noOfDays *= -1;
                        final Date today = new Date();
                        cal.setTime(today);
                        cal.add(5, noOfDays);
                        cal.set(11, 0);
                        cal.set(12, 0);
                        cal.set(13, 0);
                        final long filter = cal.getTime().getTime();
                        final String periodCrit = " (AgentContact.LAST_CONTACT_TIME >= " + filter + ")";
                        criteria = criteria + " and " + periodCrit;
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
                    final String criteria2 = " (ManagedDevice.REGISTERED_TIME >= " + start + ")";
                    final String criteria3 = " (ManagedDevice.REGISTERED_TIME <= " + end + ")";
                    criteria = criteria + " and " + criteria2 + " and " + criteria3;
                }
                if (!MDMStringUtils.isEmpty(authTokenFilter) && Boolean.parseBoolean(authTokenFilter)) {
                    criteria += " and (EnrollmentRequestWithAuthToken.ENROLLMENT_REQUEST_ID is not null)";
                    MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Enrollment_Module", "DEVICES_WITH_AUTHTOKEN_FILTER");
                }
                criteria += this.modifyToRBDAQuery("criteria");
                final String[] searchcol = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
                if (searchcol != null && searchcol.length != 0) {
                    final String[] searchVal = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
                    for (int i = 0; i < searchcol.length; ++i) {
                        criteria = criteria + " and (" + searchcol[i] + " like '%" + DMIAMEncoder.encodeSQLForNonPatternContext(searchVal[i]) + "%')";
                    }
                }
                result = criteria;
            }
            catch (final Exception exp2) {
                this.logger.log(Level.SEVERE, "Exception while set criteria in enrollment view", exp2);
            }
        }
        if (variableName.equals("SJOIN")) {
            try {
                final String mdmGroupIdStr2 = request.getParameter("mdmGroupId");
                if (mdmGroupIdStr2 != null && !"all".equals(mdmGroupIdStr2)) {
                    request.setAttribute("mdmGroupId", (Object)mdmGroupIdStr2);
                    final Long loginID2 = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                    if (loginID2 != null) {
                        sjoin += " LEFT JOIN CustomGroupMemberRel ON ManagedDevice.RESOURCE_ID = CustomGroupMemberRel.MEMBER_RESOURCE_ID";
                    }
                }
                sjoin = (result = sjoin + this.modifyToRBDAQuery("join"));
            }
            catch (final Exception exp2) {
                this.logger.log(Level.SEVERE, "Exception while adding join to query", exp2);
            }
        }
        return result;
    }
    
    private String modifyToRBDAQuery(final String variable) {
        String result = "";
        try {
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (loginID != null) {
                final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Mobile_Devices");
                if (!isMDMAdmin) {
                    if (variable.equalsIgnoreCase("join")) {
                        result += " LEFT JOIN CustomGroupMemberRel ON Resource.RESOURCE_ID = CustomGroupMemberRel.MEMBER_RESOURCE_ID ";
                        result += " LEFT JOIN UserCustomGroupMapping DeviceGroups ON CustomGroupMemberRel.GROUP_RESOURCE_ID = DeviceGroups.GROUP_RESOURCE_ID ";
                    }
                    else if (variable.equalsIgnoreCase("criteria")) {
                        final String cgCriteria = " (DeviceGroups.GROUP_RESOURCE_ID is not null)";
                        final String cgReqCriteria = " (DeviceEnrollmentRequest.USER_ID = " + userID + ")";
                        final String mdCriteria = " (ManagedDevice.RESOURCE_ID is not null)";
                        final String nomdCriteria = " (ManagedDevice.RESOURCE_ID is null)";
                        final String userCustomGroupCriteria = " (DeviceGroups.LOGIN_ID = " + loginID + ")";
                        result = result + " (" + userCustomGroupCriteria + " and " + mdCriteria + " and " + cgCriteria + " ) or ( " + nomdCriteria + " and " + cgReqCriteria + " )";
                        result = " and ( " + result + " ) ";
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentRequestTRAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
