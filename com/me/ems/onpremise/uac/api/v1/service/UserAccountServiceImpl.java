package com.me.ems.onpremise.uac.api.v1.service;

import com.adventnet.persistence.DataAccessException;
import org.apache.commons.lang.RandomStringUtils;
import com.me.devicemanagement.onpremise.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.ems.onpremise.uac.core.UserConstants;
import com.me.ems.onpremise.uac.api.v1.service.factory.PasswordServiceFactoryProvider;
import com.adventnet.i18n.I18N;
import com.adventnet.authentication.util.AuthUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.authentication.PasswordException;
import com.me.ems.onpremise.uac.core.PasswordPolicyUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.apache.commons.lang.StringUtils;
import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.ems.onpremise.uac.core.UserOperationsInterface;
import com.me.ems.framework.uac.handler.UserOperationsHandler;
import java.io.File;
import com.me.devicemanagement.framework.server.api.DCSDPRequestAPI;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Map;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.security.securitysettings.SecuritySettingsUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.HashMap;
import com.me.ems.onpremise.uac.core.CoreUserUtil;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import org.apache.commons.lang3.ArrayUtils;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Collection;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import java.util.List;
import com.me.ems.onpremise.uac.core.TFAUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.api.v1.service.factory.UserAccountService;

public class UserAccountServiceImpl implements UserAccountService
{
    protected static Logger logger;
    
    public static void addCommonParameter(final UserDetails userDetails, final JSONObject userObject) throws Exception {
        final String userName = userDetails.getUserName();
        UserAccountServiceImpl.logger.log(Level.INFO, () -> "USER NAME : '" + s + "'");
        userObject.put("userName", (Object)userName);
        userObject.put("loginName", (Object)userName);
        userObject.put("USER_EMAIL_ID", (Object)userDetails.getMailID());
        userObject.put("USER_PH_NO", (Object)userDetails.getPhoneNumber());
        userObject.put("USER_LOCALE", (Object)userDetails.getLanguage());
        String domainName = userDetails.getDomainName();
        domainName = ((domainName == null || domainName.trim().isEmpty()) ? "-" : domainName);
        userObject.put("domainName", (Object)domainName);
        List<String> customerIDs = userDetails.getCustomerIDs();
        final String roleID = userDetails.getRoleID();
        customerIDs = ((customerIDs == null) ? new ArrayList<String>(1) : customerIDs);
        userObject.put("sCustomerIDs", (Object)DMOnPremiseUserHandler.getCustomerIdListForRole(roleID, String.join(",", customerIDs)));
        userObject.put("isTwoFactorEnabledGlobaly", TFAUtil.isTwoFactorEnabled());
    }
    
    public static void processRoles(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) throws Exception {
        final String roleID = userDetails.getRoleID();
        final String enableTaskSharing = UserMgmtUtil.getUserMgmtParameter("ENABLE_TASK_SHARING");
        if (enableTaskSharing != null && !Boolean.parseBoolean(enableTaskSharing)) {
            roleNameList.add("RESTRICT_USER_TASKS");
        }
        try {
            roleNameList.addAll(DMOnPremiseUserHandler.getEditionAndEnterpriseRoles());
            final List<Long> list = DMUserHandler.getRoleList(roleID);
            final List<Long> roleIdsList = DMUserHandler.getRoleIdsFromRoleName((List)roleNameList);
            roleIdsList.addAll(list);
            final Object summaryGroupID = DMUserHandler.getSummaryGroupID(roleID);
            UserAccountServiceImpl.logger.log(Level.INFO, () -> "ROLE : '" + DMUserHandler.getRoleNameForRoleId(s) + "'");
            userObject.put("role_ID", (Object)roleID);
            userObject.put("roleIdsList", (Collection)roleIdsList);
            userObject.put("summaryGroupID", summaryGroupID);
        }
        catch (final Exception exception) {
            UserAccountServiceImpl.logger.log(Level.SEVERE, "Exception while processing Role data of the User");
            throw exception;
        }
    }
    
    public static void processComputerScope(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int computerScopeType = userDetails.getComputerScopeType();
        String[] customGroupList = new String[0];
        String[] remoteOfficeList = new String[0];
        if (computerScopeType == 1) {
            customGroupList = userDetails.getStaticComputerGroups().toArray(new String[0]);
        }
        else if (computerScopeType == 2) {
            remoteOfficeList = userDetails.getRemoteOfficeGroups().toArray(new String[0]);
        }
        else if (computerScopeType == 0) {
            roleNameList.add("All_Managed_Computer");
        }
        userObject.put("scope", computerScopeType);
        userObject.put("cgList", (Object)customGroupList);
        userObject.put("roList", (Object)remoteOfficeList);
    }
    
    public static void processDeviceScope(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int deviceScopeType = userDetails.getDeviceScopeType();
        String[] customGroupList = (String[])userObject.get("cgList");
        if (deviceScopeType == 1) {
            customGroupList = (String[])ArrayUtils.addAll((Object[])customGroupList, (Object[])userDetails.getMobileDeviceGroups().toArray(new String[0]));
        }
        else if (deviceScopeType == 0) {
            roleNameList.add("All_Managed_Mobile_Devices");
        }
        userObject.put("cgList", (Object)customGroupList);
        userObject.put("mdmScope", deviceScopeType);
    }
    
    public static void processNetworkDeviceScope(final UserDetails userDetails, final JSONObject userObject, final List<String> roleNameList) {
        final int networkDeviceScopeType = userDetails.getNetworkDeviceScopeType();
        String[] networkDeviceCustomGroupList = new String[0];
        if (networkDeviceScopeType == 1) {
            networkDeviceCustomGroupList = userDetails.getNetworkDeviceGroups().toArray(new String[0]);
        }
        else {
            roleNameList.add("All_Managed_NetworkDevices");
        }
        userObject.put("networkDeviceScope", networkDeviceScopeType);
        userObject.put("networkDeviceCustomGroupList", (Object)networkDeviceCustomGroupList);
    }
    
    @Override
    public Response addUser(final UserDetails userDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            UserAccountServiceImpl.logger.log(Level.FINER, "UserMgmt: addUser method starts");
            final List roleNameList = new ArrayList();
            String domainName = userDetails.getDomainName();
            domainName = ((domainName == null || domainName.trim().isEmpty()) ? "-" : domainName);
            userDetails.setDomainName(domainName);
            final JSONObject addUserObj = new JSONObject();
            addCommonParameter(userDetails, addUserObj);
            processComputerScope(userDetails, addUserObj, roleNameList);
            processDeviceScope(userDetails, addUserObj, roleNameList);
            processNetworkDeviceScope(userDetails, addUserObj, roleNameList);
            processRoles(userDetails, addUserObj, roleNameList);
            addUserObj.put("technicianID", (Object)user.getUserID());
            addUserObj.put("companyLogoLink", (Object)"images/login/login_logo.gif");
            addUserObj.put("adminEmail", (Object)user.getEmail());
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            final Long loginId = coreUserUtil.addUser(addUserObj);
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: Returned Id Value :" + loginId);
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: SpiceUser Name:" + userDetails.getSpiceUser());
            final Map<String, Object> resultMap = new HashMap<String, Object>();
            if (loginId != null && loginId != -1L) {
                boolean mailStatus = true;
                if (addUserObj.has("mailSent")) {
                    mailStatus = addUserObj.getBoolean("mailSent");
                }
                String remarks = "ems.admin.uac.user_activation_mail_not_sent";
                int eventCode = 736;
                if (mailStatus) {
                    remarks = "ems.admin.uac.user_activation_mail_sent_successfully";
                    eventCode = 735;
                }
                DCEventLogUtil.getInstance().addEvent(eventCode, user.getName(), (HashMap)null, remarks, (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
                DCEventLogUtil.getInstance().addEvent(701, user.getName(), (HashMap)null, "ems.admin.uac.user_successfully_added", (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
                UserAccountServiceImpl.logger.log(Level.INFO, "UserMgmt: User '" + userDetails.getUserName() + "' added successfully by " + user.getName());
                resultMap.put("mailStatus", mailStatus);
                resultMap.put("domainName", domainName.equals("-") ? "local" : domainName);
                resultMap.put("userName", userDetails.getUserName());
                resultMap.put("loginId", loginId);
                resultMap.put("securityAdvisoryCode", SecuritySettingsUtil.getInstance().getSecurityAdvisoryCodeAfterAddedAnyUser());
                final Map meta = new HashMap();
                meta.put("userCount", DMUserHandler.getUsersCountWithLogin());
                resultMap.put("meta", meta);
                return Response.status(Response.Status.OK).entity((Object)resultMap).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity((Object)resultMap).build();
        }
        catch (final APIException dce) {
            throw dce;
        }
        catch (final SyMException ex) {
            final String errorMessage = ex.getMessage();
            String errorCode = "GENERIC0005";
            switch (ex.getErrorCode()) {
                case 717: {
                    errorCode = "UAC006";
                    break;
                }
                case 720: {
                    errorCode = "UAC008";
                    break;
                }
            }
            DCEventLogUtil.getInstance().addEvent(702, user.getName(), (HashMap)null, "ems.admin.uac.add_user_failed", (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
            UserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occurred  addUser method ", (Throwable)ex);
            throw new APIException(Response.Status.BAD_REQUEST, errorCode, errorMessage);
        }
        catch (final Exception exception) {
            DCEventLogUtil.getInstance().addEvent(702, user.getName(), (HashMap)null, "ems.admin.uac.add_user_failed", (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
            UserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occurred  addUser method ", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Response modifyUser(final UserDetails userDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        final Long userID = userDetails.getUserID();
        final String oldUserName = DMUserHandler.getDCUser(userID);
        String oldDomainName = DMUserHandler.getDCUserDomain(userID);
        String userName = userDetails.getUserName();
        userName = ((userName == null || userName.isEmpty()) ? oldUserName : userName);
        String domainName = userDetails.getDomainName();
        domainName = ((domainName == null || domainName.isEmpty()) ? "-" : domainName);
        final boolean isDomainNameChanged = oldDomainName != null && !oldDomainName.equalsIgnoreCase(domainName);
        final boolean isLocalUser = userDetails.getAuthType().equals("localAuthentication");
        final String roleID = userDetails.getRoleID();
        try {
            UserAccountServiceImpl.logger.log(Level.FINER, "UserMgmt: modifyUser method starts");
            final int computerScopeType = userDetails.getComputerScopeType();
            final int deviceScopeType = userDetails.getDeviceScopeType();
            String[] customGroupList = new String[0];
            String[] remoteOfficeList = new String[0];
            final List roleNameList = new ArrayList();
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            final String enableTaskSharing = UserMgmtUtil.getUserMgmtParameter("ENABLE_TASK_SHARING");
            final List<String> editionAndEnterpriseRoles = DMOnPremiseUserHandler.getEditionAndEnterpriseRoles();
            if (computerScopeType == 1) {
                customGroupList = userDetails.getStaticComputerGroups().toArray(new String[0]);
            }
            else if (computerScopeType == 2) {
                remoteOfficeList = userDetails.getRemoteOfficeGroups().toArray(new String[0]);
            }
            else if (computerScopeType == 0) {
                roleNameList.add("All_Managed_Computer");
            }
            if (deviceScopeType == 1) {
                customGroupList = (String[])ArrayUtils.addAll((Object[])customGroupList, (Object[])userDetails.getMobileDeviceGroups().toArray(new String[0]));
            }
            else if (deviceScopeType == 0) {
                roleNameList.add("All_Managed_Mobile_Devices");
            }
            if (enableTaskSharing != null && !Boolean.parseBoolean(enableTaskSharing)) {
                roleNameList.add("RESTRICT_USER_TASKS");
            }
            roleNameList.addAll(editionAndEnterpriseRoles);
            final LicenseProvider dcLicenseHandler = LicenseProvider.getInstance();
            final String noOfTechnBuy = dcLicenseHandler.getNoOfTechnicians();
            if (noOfTechnBuy == null) {
                userName = oldUserName;
            }
            if (noOfTechnBuy != null && !noOfTechnBuy.equals("1")) {
                userName = oldUserName;
                if (isDomainNameChanged && DMOnPremiseUserHandler.isUserAccountAvailable(userName, domainName)) {
                    throw new APIException(Response.Status.BAD_REQUEST, "UAC006", "ems.admin.uac.user_exists", new String[] { domainName.equals("-") ? "local" : domainName, userName });
                }
            }
            final boolean userCreatedByDC = DMOnPremiseUserUtil.isUserCreatedByDC(userID);
            if (!isLocalUser) {
                final HashMap<String, Object> resultMap = UserManagementUtil.checkIfUserIsValid(userName, domainName);
                if (!resultMap.get("isValidUser")) {
                    throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
                }
            }
            final List<Long> list = DMOnPremiseUserHandler.getRoleList(roleID);
            final List<Long> roleIdsList = DMOnPremiseUserHandler.getRoleIdsFromRoleName(roleNameList);
            roleIdsList.addAll(list);
            String[] newMappedArray = new String[0];
            List<String> oldMappedList;
            if (CustomerInfoUtil.getInstance().isMSP()) {
                oldMappedList = CoreUserUtil.getCustomerMappingDetailsInString(userID);
                if (userDetails.getCustomerIDs() != null && userDetails.getCustomerIDs().size() > 0) {
                    newMappedArray = userDetails.getCustomerIDs().toArray(new String[0]);
                }
                else if (DMUserHandler.isDefaultAdministratorRole(Long.valueOf(roleID))) {
                    newMappedArray = DMUserHandler.getCustomerIdListForRole(roleID, (String)null).split(",");
                }
            }
            else {
                oldMappedList = ApiFactoryProvider.getUserManagementAPIHandler().getMappingDetailsInString(userID);
                if (userDetails.getStaticComputerGroups() != null && userDetails.getStaticComputerGroups().size() > 0) {
                    newMappedArray = userDetails.getStaticComputerGroups().toArray(new String[0]);
                }
                else if (userDetails.getRemoteOfficeGroups() != null && userDetails.getRemoteOfficeGroups().size() > 0) {
                    newMappedArray = userDetails.getRemoteOfficeGroups().toArray(new String[0]);
                }
            }
            final String[] oldMappedArray = oldMappedList.toArray(new String[0]);
            userDetails.setDomainName(domainName);
            final JSONObject modifyObj = new JSONObject();
            modifyObj.put("USER_EMAIL_ID", (Object)userDetails.getMailID());
            modifyObj.put("USER_PH_NO", (Object)userDetails.getPhoneNumber());
            modifyObj.put("USER_LOCALE", (Object)userDetails.getLanguage());
            modifyObj.put("mdmScope", userDetails.getDeviceScopeType());
            modifyObj.put("scope", userDetails.getComputerScopeType());
            modifyObj.put("cgList", (Object)customGroupList);
            modifyObj.put("roList", (Object)remoteOfficeList);
            modifyObj.put("role_ID", (Object)roleID);
            modifyObj.put("contactinfoID", ((Hashtable<K, Object>)DMUserHandler.getContactInfoProp(DMUserHandler.getDCUserID(userID))).get("contactInfoID"));
            List<String> customerIDs = userDetails.getCustomerIDs();
            customerIDs = ((customerIDs == null) ? new ArrayList<String>(1) : customerIDs);
            modifyObj.put("sCustomerIDs", (Object)DMOnPremiseUserHandler.getCustomerIdListForRole(roleID, String.join(",", customerIDs)));
            modifyObj.put("domainName", (Object)userDetails.getDomainName());
            modifyObj.put("userName", (Object)userName);
            modifyObj.put("roleIdsList", (Collection)roleIdsList);
            modifyObj.put("oldMappedList", (Object)oldMappedArray);
            modifyObj.put("newMappedList", (Object)newMappedArray);
            modifyObj.put("loginID", (Object)userID);
            if (user.getLoginID().equals(userID) && DMOnPremiseUserUtil.isLanguageChanged(user.getLoginID(), userDetails.getLanguage())) {
                UserAccountServiceImpl.logger.log(Level.INFO, "Language changed for user " + userName);
            }
            if (roleID != null) {
                final Object summaryGroupID = DMOnPremiseUserHandler.getSummaryGroupID(roleID);
                modifyObj.put("summaryGroupID", summaryGroupID);
            }
            if (domainName.equals("-")) {
                userDetails.setDomainName("local");
            }
            processNetworkDeviceScope(userDetails, modifyObj, roleNameList);
            coreUserUtil.modifyUser(modifyObj);
            oldDomainName = ((oldDomainName == null || oldDomainName.equals("-")) ? "local" : oldDomainName);
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: User '" + oldDomainName + "\\" + oldUserName + "' has been successfully modified to " + userDetails.getDomainName() + "\\" + userName);
            UserAccountServiceImpl.logger.log(Level.INFO, "UserMgmt: User '" + userName + "' successfully modified by " + user.getName());
            String args = "";
            String event = "";
            if (isDomainNameChanged) {
                args = oldDomainName + "@@@" + oldUserName + "@@@" + userDetails.getDomainName() + "@@@" + userName;
                event = (userCreatedByDC ? "ems.admin.uac.user_domain_successfully_mod" : "ems.admin.uac.user_domain_successfully_mod_sdp");
            }
            else {
                args = oldDomainName + "@@@" + oldUserName;
                event = (userCreatedByDC ? "ems.admin.uac.user_successfully_mod" : "ems.admin.uac.user_successfully_mod_sdp");
            }
            DCEventLogUtil.getInstance().addEvent(703, user.getName(), (HashMap)null, event, (Object)args, true);
            if (oldUserName != null && !oldUserName.equalsIgnoreCase(userName)) {
                UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: User '" + oldUserName + "' has been successfully renamed as '" + userName + "'");
                DCEventLogUtil.getInstance().addEvent(703, user.getName(), (HashMap)null, "ems.admin.uac.user_successfully_rename", (Object)(oldUserName + "@@@" + userName), true);
            }
            final Map<String, Object> resultMap2 = new HashMap<String, Object>();
            resultMap2.put("userCreatedByDC", userCreatedByDC);
            resultMap2.put("domainName", userDetails.getDomainName());
            resultMap2.put("userName", userName);
            if (isDomainNameChanged) {
                resultMap2.put("oldDomainName", oldDomainName);
                resultMap2.put("oldUserName", oldUserName);
            }
            return Response.status(Response.Status.OK).entity((Object)resultMap2).build();
        }
        catch (final APIException e) {
            throw e;
        }
        catch (final SyMException ex) {
            final String errorMessage = ex.getMessage();
            String errorCode = "GENERIC0005";
            switch (ex.getErrorCode()) {
                case 720: {
                    errorCode = "UAC008";
                    break;
                }
            }
            DCEventLogUtil.getInstance().addEvent(704, user.getName(), (HashMap)null, "dc.admin.uac.USER_MOD_FAILED", (Object)(oldDomainName + "@@@" + oldUserName), true);
            UserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occurred  modify method ", (Throwable)ex);
            throw new APIException(Response.Status.BAD_REQUEST, errorCode, errorMessage);
        }
        catch (final Exception e2) {
            DCEventLogUtil.getInstance().addEvent(704, user.getName(), (HashMap)null, "dc.admin.uac.USER_MOD_FAILED", (Object)(oldDomainName + "@@@" + oldUserName), true);
            UserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occured  modifyUser method ", e2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Response deleteUser(final Long loginID, final User currentUser, final Map<String, Object> contactDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            final boolean userCreatedByDC = DMOnPremiseUserUtil.isUserCreatedByDC(loginID);
            final Long userID = DMUserHandler.getDCUserID(loginID);
            String userDomainName = DMUserHandler.getDCUserDomain(loginID);
            final String loginName = DMUserHandler.getUserName(loginID);
            final String emailID = DMUserHandler.getUserEmailID(userID);
            String tempMsg = userCreatedByDC ? "dc.admin.uac.USER_SUCCESSFULLY_DEL" : "dc.admin.uac.USER_SUCCESSFULLY_DEL_SDP";
            int eventID = 705;
            final HashMap userDelPII = new HashMap(4);
            final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
            validateContactDetails(contactDetails, userDelPII, userID);
            if (userDomainName == null || userDomainName.equalsIgnoreCase("null") || userDomainName.equalsIgnoreCase("-")) {
                userDomainName = "local";
            }
            String tempMsgArgs = DMIAMEncoder.encodeHTML(userDomainName) + "@@@" + DMIAMEncoder.encodeHTML(loginName);
            boolean status;
            if (loginID.equals(defaultAdminUVHLoginID)) {
                final boolean bAssetExplorerEnabled = SolutionUtil.getInstance().isAEIntegrationMode();
                final boolean bSDPDeskEnabled = SolutionUtil.getInstance().isIntegrationMode();
                final boolean bSdpEnabled = SolutionUtil.getInstance().isInvIntegrationMode();
                Long buildno = null;
                try {
                    final String sdpBuildNumber = SolutionUtil.getInstance().getSDPBuildNumber();
                    buildno = Long.parseLong(sdpBuildNumber);
                    UserAccountServiceImpl.logger.log(Level.INFO, "build number for SDP", sdpBuildNumber);
                }
                catch (final Exception e) {
                    UserAccountServiceImpl.logger.log(Level.INFO, "Exception while getting sdp build no", e);
                }
                if (bSdpEnabled && !isMSP && ((bSDPDeskEnabled && buildno != null && buildno > 11299L) || bAssetExplorerEnabled)) {
                    String appName = "HelpDesk";
                    if (bAssetExplorerEnabled) {
                        appName = "AssetExplorer";
                    }
                    final Long appID = SolutionUtil.getInstance().getIntegratedApplicationId(appName);
                    final Properties properties = new Properties();
                    properties.setProperty("applicationID", appID.toString());
                    properties.setProperty("loginID", loginID.toString());
                    properties.setProperty("status", "1");
                    SolutionUtil.getInstance().addIntegratedServiceUser(properties);
                    final DCSDPRequestAPI sdpUserHandler = (DCSDPRequestAPI)Class.forName("com.me.dconpremise.webclient.sdp.util.DCRequestHandlerUtil").newInstance();
                    sdpUserHandler.changeSDPUser(loginID, "DISABLED", true);
                    status = true;
                }
                else {
                    status = DMOnPremiseUserUtil.hideDefaultAdmin(loginID);
                    if (status) {
                        ApiFactoryProvider.getUserManagementAPIHandler().handleAdminUserDelete(loginID, DMUserHandler.getUserContactProps(userID, userDelPII));
                    }
                }
            }
            else {
                final JSONObject deleteUserJObj = new JSONObject();
                deleteUserJObj.put("loginID", (Object)loginID);
                deleteUserJObj.put("isPluginUser", false);
                deleteUserJObj.put("fromSDP", false);
                final CoreUserUtil coreUserUtil = new CoreUserUtil();
                status = coreUserUtil.deleteUser(deleteUserJObj, userDelPII);
            }
            if (!status) {
                tempMsg = "dc.admin.uac.USER_DEL_FAILED";
                tempMsgArgs = "";
                eventID = 706;
            }
            else {
                String imagePath = System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "user_profile" + File.separator + loginID + ".png";
                ApiFactoryProvider.getFileAccessAPI().deleteFile(imagePath);
                imagePath = System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "user_full_profile" + File.separator + loginID + ".png";
                ApiFactoryProvider.getFileAccessAPI().deleteFile(imagePath);
            }
            DCEventLogUtil.getInstance().addEvent(eventID, currentUser.getName(), (HashMap)null, tempMsg, (Object)tempMsgArgs, true);
            UserAccountServiceImpl.logger.log(Level.INFO, "UserMgmt: user '" + loginName + "' deleted successfully by " + currentUser.getName());
            final Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("userCreatedByDC", userCreatedByDC);
            if (eventID == 705) {
                resultMap.put("domainName", userDomainName);
                resultMap.put("userName", loginName);
                return Response.status(Response.Status.OK).entity((Object)resultMap).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity((Object)resultMap).build();
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception e2) {
            DCEventLogUtil.getInstance().addEvent(706, currentUser.getName(), (HashMap)null, "dc.admin.uac.USER_DEL_FAILED", (Object)"", true);
            UserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occured  deleteUser method ", e2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> getUserDetails(final Long loginID) throws APIException {
        try {
            final DataObject userDO = CoreUserUtil.getUserDetails(loginID);
            if (userDO == null || userDO.isEmpty()) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
            }
            final Map<String, Object> userDetailsMap = new HashMap<String, Object>();
            final Row aaaLoginRow = userDO.getRow("AaaLogin");
            final Long userId = aaaLoginRow.getLong("USER_ID");
            userDetailsMap.put("userName", aaaLoginRow.get("NAME"));
            final String domainName = (String)aaaLoginRow.get("DOMAINNAME");
            final String authType = (domainName == null || domainName.equalsIgnoreCase("-")) ? "localAuthentication" : "adAuthentication";
            userDetailsMap.put("authType", authType);
            if (!domainName.equals("-")) {
                userDetailsMap.put("domainName", domainName);
            }
            final Row umRoleRow = userDO.getRow("UMRole");
            userDetailsMap.put("roleID", umRoleRow.get("UM_ROLE_ID"));
            userDetailsMap.put("roleName", umRoleRow.get("UM_ROLE_NAME"));
            final Row aaaContactRow = userDO.getRow("AaaContactInfo");
            final String emailId = (String)aaaContactRow.get("EMAILID");
            final String contactNo = (String)aaaContactRow.get("LANDLINE");
            if (emailId != null && !emailId.isEmpty()) {
                userDetailsMap.put("mailID", emailId);
            }
            if (contactNo != null && !contactNo.isEmpty()) {
                userDetailsMap.put("phoneNumber", contactNo);
            }
            userDetailsMap.put("loginId", loginID);
            final List<String> classNames = UserOperationsHandler.getUserOperationsImplClassNames();
            for (final String className : classNames) {
                final UserOperationsInterface userOperation = (UserOperationsInterface)Class.forName(className).newInstance();
                userOperation.getUserDetails(userDetailsMap);
            }
            userDetailsMap.put("language", I18NUtil.getUserLocaleFromDB(loginID));
            userDetailsMap.remove("loginId");
            if (CustomerInfoUtil.getInstance().isMSP()) {
                final ArrayList<HashMap> customers = CustomerInfoUtil.getInstance().getCustomersForUser(loginID);
                final ArrayList<HashMap> result = new ArrayList<HashMap>();
                for (final HashMap entry : customers) {
                    final HashMap customer = new HashMap();
                    customer.put("customerID", entry.get("CUSTOMER_ID"));
                    customer.put("customerName", entry.get("CUSTOMER_NAME"));
                    result.add(customer);
                }
                userDetailsMap.put("customerIDs", result);
            }
            else {
                final Row loginExtnRow = userDO.getRow("AaaLoginExtn");
                Integer mdmScope = loginExtnRow.getInt("MDM_SCOPE");
                mdmScope = ((mdmScope == null) ? 0 : mdmScope);
                userDetailsMap.put("deviceScopeType", mdmScope);
                if (mdmScope == 1) {
                    final List<Hashtable> result2 = ApiFactoryProvider.getUserManagementAPIHandler().getMobileUserMappingCustomGroup(loginID);
                    if (result2 != null && !result2.isEmpty()) {
                        userDetailsMap.put("mobileDeviceGroups", result2);
                    }
                }
                final int dcScope = loginExtnRow.getLong("SCOPE").intValue();
                userDetailsMap.put("computerScopeType", dcScope);
                if (dcScope == 1) {
                    final List<Hashtable> result3 = ApiFactoryProvider.getUserManagementAPIHandler().getComputerUserMappingCustomGroup(loginID);
                    if (result3 != null && !result3.isEmpty()) {
                        userDetailsMap.put("staticComputerGroups", result3);
                    }
                }
                else if (dcScope == 2) {
                    final List<Hashtable> result3 = ApiFactoryProvider.getUserManagementAPIHandler().getComputerUserMappingRemoteOffice(loginID);
                    if (result3 != null && !result3.isEmpty()) {
                        userDetailsMap.put("remoteOfficeGroups", result3);
                    }
                }
            }
            return userDetailsMap;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception e) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: Exception occurred in getUserDetails", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public static Response validateContactDetails(final Map<String, Object> contactDetails, final Map<String, Object> userDelPII, final Long userID) throws Exception {
        final String emailID = DMUserHandler.getUserEmailID(userID);
        final List<String> additionalEmailIDs = contactDetails.get("additionalEmail");
        final List<String> additionalPhoneNOs = contactDetails.get("additionalPhone");
        final String alternateEmailIDs = contactDetails.get("alternateEmail");
        final String alternatePhoneNOs = contactDetails.get("alternatePhone");
        final boolean isEmailPresentForOthers = UserManagementUtil.isEmailExistForOtherUser(emailID, userID);
        if (!isEmailPresentForOthers && UserManagementUtil.isUserMailServerEmailSame(emailID)) {
            throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC009", "ems.admin.uac.user_email_del_failed", new String[] { emailID });
        }
        if (additionalEmailIDs != null && !additionalEmailIDs.isEmpty()) {
            userDelPII.put("additionalEmailIDsList", additionalEmailIDs);
        }
        if (additionalPhoneNOs != null && !additionalPhoneNOs.isEmpty()) {
            userDelPII.put("additionalPhoneNOsList", additionalPhoneNOs);
        }
        if (StringUtils.isNotEmpty(alternateEmailIDs)) {
            userDelPII.put("alternateEmail", alternateEmailIDs.toLowerCase());
        }
        if (StringUtils.isNotEmpty(alternatePhoneNOs)) {
            userDelPII.put("alternatePhone", alternatePhoneNOs.toLowerCase());
        }
        return null;
    }
    
    @Override
    public Map<String, Object> modifyUserContact(final Map<String, Object> userDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            final Long loginID = userDetails.get("loginID");
            UserAccountServiceImpl.logger.log(Level.INFO, "modify user contact details for login id: {0}", loginID);
            final String userName = DMUserHandler.getUserName(loginID);
            final String domainName = DMUserHandler.getDCUserDomain(loginID);
            if (userName == null) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
            }
            final String mailId = userDetails.get("mailId");
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            if (coreUserUtil.isEmailAlreadyExists(mailId, loginID)) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC008", "ems.admin.admin.email_already_exists");
            }
            final Criteria loginCriteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = coreUserUtil.getModifyUserDetailDO(loginCriteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AaaContactInfo");
                final Long contactInfoID = (Long)row.get("CONTACTINFO_ID");
                final JSONObject modifyContact = new JSONObject();
                modifyContact.put("USER_EMAIL_ID", (Object)mailId);
                coreUserUtil.updateContactInfo(contactInfoID, modifyContact);
                DCEventLogUtil.getInstance().addEvent(739, user.getName(), (HashMap)null, "ems.user.uac.user_contact_updated", (Object)(domainName + "@@@" + userName), true);
            }
            final Map<String, Object> resultDetails = new HashMap<String, Object>();
            resultDetails.put("userName", userName);
            resultDetails.put("status", 0);
            return resultDetails;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: Exception occurred in update user Contact details", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> sendPasswordLink(final Long loginId, final User user, final Map<String, Object> probeDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
            final DataObject dataObject = coreUserUtil.getContactDOFromMail(criteria);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
            }
            final int accountStatus = (int)dataObject.getFirstValue("AaaAccountStatusExtn", "STATUS");
            if (accountStatus == 1) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC013", "ems.user.uac.user_not_active");
            }
            final Row contactRow = dataObject.getRow("AaaContactInfo");
            final String mailId = contactRow.getString("EMAILID");
            if (mailId == null || mailId.isEmpty()) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC005", "ems.admin.uac.invalid_mail_id");
            }
            if (coreUserUtil.isEmailAlreadyExists(mailId, loginId)) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC012", "ems.admin.admin.user_email_duplicate");
            }
            final String name = (String)dataObject.getValue("AaaLogin", "NAME", new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
            final String domainName = (String)dataObject.getValue("AaaLogin", "DOMAINNAME", new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("newUser", false);
            jsonObject.put("adminEmail", (Object)user.getEmail());
            jsonObject.put("userEmail", (Object)mailId);
            jsonObject.put("domainName", (Object)domainName);
            jsonObject.put("technicianID", (Object)user.getUserID());
            coreUserUtil.addOrUpdateUserStatus(dataObject, jsonObject);
            final boolean mailStatus = jsonObject.optBoolean("mailSent");
            String remarks = "ems.user.uac.reset_password_failed";
            int eventCode = 738;
            if (mailStatus) {
                remarks = "ems.user.security.reset_password_initiated";
                eventCode = 737;
            }
            DCEventLogUtil.getInstance().addEvent(eventCode, user.getName(), (HashMap)null, remarks, (Object)(domainName + "@@@" + name), true);
            if (!mailStatus) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC016", "ems.user.uac.reset_password_failed", new String[] { domainName, name });
            }
            final Map<String, Object> resultDetails = new HashMap<String, Object>();
            resultDetails.put("userName", name);
            resultDetails.put("status", 0);
            resultDetails.put("mailSent", mailStatus);
            return resultDetails;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while sent password link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> resendUserInvite(final Long loginId, final User user, final Map<String, Object> probeDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
            final DataObject dataObject = coreUserUtil.getContactDOFromMail(criteria);
            if (dataObject == null || dataObject.isEmpty()) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
            }
            final int accountStatus = (int)dataObject.getFirstValue("AaaAccountStatusExtn", "STATUS");
            if (accountStatus != 1) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC006", "ems.user.uac.user_active_already");
            }
            final Row contactRow = dataObject.getRow("AaaContactInfo");
            final String mailId = contactRow.getString("EMAILID");
            if (mailId == null || mailId.isEmpty()) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC005", "ems.admin.uac.invalid_mail_id");
            }
            if (coreUserUtil.isEmailAlreadyExists(mailId, loginId)) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC012", "ems.admin.admin.user_email_duplicate");
            }
            final String name = (String)dataObject.getValue("AaaLogin", "NAME", new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
            final String domainName = (String)dataObject.getValue("AaaLogin", "DOMAINNAME", new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("newUser", true);
            jsonObject.put("userEmail", (Object)mailId);
            jsonObject.put("domainName", (Object)domainName);
            jsonObject.put("technicianID", (Object)user.getUserID());
            jsonObject.put("adminEmail", (Object)user.getEmail());
            coreUserUtil.addOrUpdateUserStatus(dataObject, jsonObject);
            final boolean mailStatus = jsonObject.optBoolean("mailSent");
            String remarks = "ems.user.uac.resend_user_invite_failed";
            int eventCode = 736;
            if (mailStatus) {
                remarks = "ems.admin.uac.user_activation_mail_sent_successfully";
                eventCode = 735;
            }
            DCEventLogUtil.getInstance().addEvent(eventCode, user.getName(), (HashMap)null, remarks, (Object)(domainName + "@@@" + name), true);
            if (!mailStatus) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC015", "ems.user.uac.resend_user_invite_failed", new String[] { domainName, name });
            }
            final Map<String, Object> resultDetails = new HashMap<String, Object>();
            resultDetails.put("userName", name);
            resultDetails.put("status", 1);
            resultDetails.put("mailSent", mailStatus);
            return resultDetails;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while resend user activation link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> getTokenDetails(final Long loginID) throws APIException {
        try {
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            final DataObject dataObject = coreUserUtil.getTokenDetails(criteria);
            if (dataObject.isEmpty()) {
                throw new APIException(Response.Status.NOT_FOUND, "UAC017", "ems.user.uac.user_token_not_found");
            }
            final Map<String, Object> responseMap = new HashMap<String, Object>();
            final Long currentTime = System.currentTimeMillis();
            final Row userLinkDetailRow = dataObject.getRow("AaaUserLinkDetails");
            final Long expiryTime = userLinkDetailRow.getLong("EXPIRY_TIME");
            final Integer tokenType = userLinkDetailRow.getInt("TOKEN_TYPE");
            final Row aaaLogin = dataObject.getRow("AaaLogin");
            final String domainName = aaaLogin.getString("DOMAINNAME");
            if (currentTime > expiryTime) {
                final String errorCode = (tokenType == 101) ? "UAC011" : "UAC014";
                throw new APIException(Response.Status.BAD_REQUEST, errorCode, "ems.admin.admin.token_expired");
            }
            responseMap.put("expiryTime", expiryTime);
            responseMap.put("tokenType", tokenType);
            final String baseURLStr = SyMUtil.getServerBaseUrlForMail();
            String tokenUrl = "";
            final String token = userLinkDetailRow.getString("TOKEN");
            if (tokenType == 101) {
                if (domainName.equalsIgnoreCase("-")) {
                    tokenUrl = baseURLStr + "/client#/login/create-password?userToken=" + token;
                }
                else {
                    tokenUrl = baseURLStr + "/client#/login/activate?userToken=" + token;
                }
            }
            else if (tokenType == 102) {
                tokenUrl = baseURLStr + "/client#/login/reset-password?userToken=" + token;
            }
            responseMap.put("token", tokenUrl);
            final Row contactDetailRow = dataObject.getRow("AaaContactInfo");
            final Row aaaloginRow = dataObject.getRow("AaaLogin");
            responseMap.put("userName", aaaloginRow.get("NAME"));
            responseMap.put("mailAddress", contactDetailRow.get("EMAILID"));
            final Long diff = expiryTime - currentTime;
            if (diff > 0L) {
                final long days = diff / 86400000L;
                if (days > 0L) {
                    responseMap.put("expiryDay", days);
                }
                final long balanceHours = diff % 86400000L;
                if (balanceHours > 0L) {
                    final long hours = balanceHours / 3600000L;
                    if (hours > 0L) {
                        responseMap.put("expiryHour", hours);
                    }
                    final long balanceMinutes = balanceHours % 3600000L;
                    if (balanceMinutes > 0L) {
                        final long minutes = balanceMinutes / 60000L;
                        if (minutes > 0L) {
                            responseMap.put("expiryMinute", minutes);
                        }
                    }
                }
            }
            return responseMap;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while get token details", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> getAddUserDetails(final User dcUser) {
        final Map<String, Object> preUserAddFormMap = new HashMap<String, Object>();
        try {
            final Boolean isExistingAdmin = Boolean.valueOf(SyMUtil.getUserParameter(dcUser.getUserID(), "isExistingAdmin"));
            preUserAddFormMap.put("isExistingAdmin", isExistingAdmin);
            preUserAddFormMap.putAll(ApiFactoryProvider.getUserManagementAPIHandler().getAddUserDetails());
        }
        catch (final Exception e) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: Exception occurred - getAddUserDetails ", e);
        }
        return preUserAddFormMap;
    }
    
    @Override
    public Map<String, Object> checkMailId(final Map<String, Object> userDetails, final User user) throws APIException {
        try {
            UserAccountServiceImpl.logger.log(Level.INFO, "Duplicate Check for Mail Id");
            final String mailId = userDetails.get("mailId");
            final Long userId = userDetails.containsKey("loginId") ? Long.valueOf(userDetails.get("loginId")) : user.getLoginID();
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            final DataObject userDO = CoreUserUtil.getUserDetails(userId);
            if (userDO == null || userDO.isEmpty()) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
            }
            final Map<String, Object> resultDetails = new HashMap<String, Object>();
            resultDetails.put("isDuplicate", coreUserUtil.isEmailAlreadyExists(mailId, userId));
            return resultDetails;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: Exception occurred in update user Contact details", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> getUserStartDetails(final User dcUser) throws APIException {
        Map<String, Object> returnMap;
        try {
            returnMap = new HashMap<String, Object>();
            final String firstLogin = SyMUtil.getUserParameter(dcUser.getUserID(), "firstlogin");
            if (firstLogin != null && !ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
                returnMap.put("adminFirstLogin", Boolean.valueOf(firstLogin));
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccount"));
            selectQuery.addJoin(new Join("AaaAccount", "AaaAccountStatus", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatus", "ACCOUNT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatus", "STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatus", "UPDATEDTIME"));
            final Criteria criteria = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)dcUser.getLoginID(), 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Row accountStatus = dataObject.getRow("AaaAccountStatus");
            final String status = accountStatus.getString("STATUS");
            Long activatedTime = accountStatus.getLong("UPDATEDTIME");
            activatedTime += 30000L;
            if (activatedTime > System.currentTimeMillis() && !ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
                returnMap.put("technicianFirstLogin", Boolean.TRUE);
            }
            returnMap.put("status", status);
            returnMap.put("activatedTime", activatedTime);
        }
        catch (final Exception e) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while personalising admin", e);
            throw new APIException("GENERIC0005");
        }
        return returnMap;
    }
    
    @Override
    public Map<String, Object> updateAdminPersonalizationDetails(final Map<String, Object> detailsMap, final User dcUser, final HttpServletRequest httpServletRequest) throws APIException {
        Map<String, Object> returnMap;
        try {
            this.validateAdminPersonalization(detailsMap, dcUser);
            detailsMap.put("loginID", dcUser.getLoginID());
            this.modifyUserContact(detailsMap, dcUser, httpServletRequest);
            returnMap = ApiFactoryProvider.getPersonalizationAPIForRest().updateTimeZoneAndLanguage((Map)detailsMap, dcUser);
            SyMUtil.deleteUserParameter(dcUser.getUserID(), "firstlogin");
        }
        catch (final APIException e) {
            throw e;
        }
        catch (final Exception e2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while personalising admin", e2);
            throw new APIException("GENERIC0005");
        }
        return returnMap;
    }
    
    protected void validateAdminPersonalization(final Map<String, Object> detailsMap, final User dcUser) throws APIException {
        try {
            final String mailId = detailsMap.get("mailId");
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            if (coreUserUtil.isEmailAlreadyExists(mailId, dcUser.getLoginID())) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC008", "ems.admin.admin.email_already_exists");
            }
            ApiFactoryProvider.getPersonalizationAPIForRest().validateTimeZoneAndLanguage((Map)detailsMap);
        }
        catch (final APIException e) {
            throw e;
        }
        catch (final Exception e2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while validating personalising admin", e2);
            throw new APIException("GENERIC0005");
        }
    }
    
    @Override
    public Map<String, Object> addOrUpdatePassword(final Map tokenDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            final String domainName = tokenDetails.get("DOMAINNAME");
            final boolean localUser = domainName == null || domainName.equalsIgnoreCase("-");
            if (!localUser) {
                throw new APIException(Response.Status.NOT_FOUND, "UAC010", "ems.admin.admin.token_invalid");
            }
            final String loginName = tokenDetails.get("NAME");
            final String newPassword = SyMUtil.decodeAsUTF16LE((String)tokenDetails.get("password"));
            PasswordPolicyUtil.validatePassword(loginName, newPassword);
            final int tokenType = tokenDetails.get("TOKEN_TYPE");
            switch (tokenType) {
                case 102: {
                    this.changePassword(tokenDetails);
                    DCEventLogUtil.getInstance().addEvent(741, loginName, (HashMap)null, "ems.user.security.successfully_reset_password", (Object)loginName, true);
                    break;
                }
                case 101: {
                    this.activateLocalUser(tokenDetails);
                    DCEventLogUtil.getInstance().addEvent(740, loginName, (HashMap)null, "ems.user.security.user_successfully_activated", (Object)loginName, true);
                    break;
                }
                default: {
                    throw new APIException(Response.Status.PRECONDITION_FAILED, "IAM0006", "ems.admin.admin.token_type_expired");
                }
            }
            final Map<String, Object> resultDetails = new HashMap<String, Object>();
            resultDetails.put("userName", loginName);
            resultDetails.put("domainName", domainName);
            resultDetails.put("status", 0);
            resultDetails.put("tokenType", tokenType);
            return resultDetails;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final PasswordException pwdEx) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Password Validation Failed", (Throwable)pwdEx);
            final String message = pwdEx.getMessage();
            throw new APIException(Response.Status.PRECONDITION_FAILED, "PASS0001", message);
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while change password", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public void changePassword(final Map tokenDetails) throws APIException {
        final JSONObject passwordPolicy = DMOnPremiseUserUtil.getPasswordPolicyDetails();
        final Integer reuseFor = (Integer)(passwordPolicy.has("PREVENT_REUSE_FOR") ? passwordPolicy.get("PREVENT_REUSE_FOR") : 0);
        try {
            final Long loginId = tokenDetails.get("LOGIN_ID");
            final Long userId = tokenDetails.get("USER_ID");
            final String loginName = tokenDetails.get("NAME");
            final String newPassword = SyMUtil.decodeAsUTF16LE((String)tokenDetails.get("password"));
            final Integer minLength = (Integer)(passwordPolicy.has("MIN_LENGTH") ? passwordPolicy.get("MIN_LENGTH") : 5);
            final boolean isComplexPwd = passwordPolicy.has("IS_COMPLEX_PASSWORD") && (boolean)passwordPolicy.get("IS_COMPLEX_PASSWORD");
            final boolean isValidPwd = DMOnPremiseUserUtil.isValidPassword(loginName, newPassword, passwordPolicy);
            if (newPassword != null && !newPassword.equals("") && isValidPwd) {
                if (newPassword.equalsIgnoreCase("admin")) {
                    throw new APIException("PASS0001", "desktopcentral.admin.new_password_default", new String[0]);
                }
                if (newPassword.equalsIgnoreCase(loginName)) {
                    throw new APIException("PASS0001", "desktopcentral.admin.new_password_user_name_same", new String[0]);
                }
                final UserManagementUtil userManagementUtil = new UserManagementUtil();
                SYMClientUtil.changePassword(loginName, newPassword);
                AuthUtil.closeAllSessions(userId);
                final DataObject userDO = this.getUserAccountDetails(loginId);
                new CoreUserUtil().addOrUpdateAccountStatusExtnRow(userDO, Integer.valueOf(0), Integer.valueOf(1));
                SyMUtil.getPersistence().update(userDO);
                new CoreUserUtil().removeUserLinkDetails(userId);
            }
            else if (!isValidPwd) {
                String message = I18N.getMsg("dc.uac.PASSWORDPOLICY.PASSWORD_VALIDATION", new Object[] { minLength });
                message = (isComplexPwd ? (message + ' ' + I18N.getMsg("dc.uac.PASSWORDPOLICY.COMPLEXITY_VALIDATION", new Object[0])) : message);
                throw new APIException("PASS0001", message, new String[0]);
            }
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while change password", ex2);
            PasswordServiceFactoryProvider.getChangePasswordServiceObject().getExceptionMsgForPassword(ex2.getMessage(), reuseFor);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public void activateLocalUser(final Map tokenDetails) throws Exception {
        try {
            final Long loginId = tokenDetails.get("LOGIN_ID");
            final String newPassword = SyMUtil.decodeAsUTF16LE((String)tokenDetails.get("password"));
            final DataObject userDO = this.getUserAccountDetails(loginId);
            this.addUserPassword(userDO, newPassword);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final PasswordException pwdEx) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Password Validation Failed", (Throwable)pwdEx);
            final String message = pwdEx.getMessage();
            throw new APIException(Response.Status.PRECONDITION_FAILED, "PASS0001", message);
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while activate LocalUser password", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public void sendPasswordLink(final Map requestDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final CoreUserUtil coreUserUtil = new CoreUserUtil();
            Long userId = null;
            final String mail = requestDetails.get("mailId");
            String name = null;
            String domainName = null;
            final Criteria criteria = new Criteria(Column.getColumn("AaaContactInfo", "EMAILID"), (Object)mail, 0);
            final DataObject dataObject = coreUserUtil.getContactDOFromMail(criteria);
            if (dataObject == null || dataObject.isEmpty()) {
                return;
            }
            final int accountStatus = (int)dataObject.getFirstValue("AaaAccountStatusExtn", "STATUS");
            if (accountStatus == 1) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC013", "ems.admin.uac.invalid_mail_id");
            }
            final Iterator<Row> loginRows = dataObject.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final Row loginRow = loginRows.next();
                if (userId != null) {
                    throw new APIException(Response.Status.PRECONDITION_FAILED, "UAC012", "ems.admin.admin.user_email_duplicate");
                }
                userId = loginRow.getLong("USER_ID");
                name = loginRow.getString("NAME");
                domainName = loginRow.getString("DOMAINNAME");
            }
            final String userMail = (String)dataObject.getFirstValue("AaaContactInfo", "EMAILID");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("newUser", false);
            jsonObject.put("userEmail", (Object)userMail);
            jsonObject.put("domainName", (Object)domainName);
            jsonObject.put("technicianID", (Object)userId);
            jsonObject.put("userID", (Object)userId);
            jsonObject.put("eventCode", (Object)UserConstants.UserAlertConstant.THIRD_PARTY_RESET_PASSWORD);
            if (domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) {
                new CoreUserUtil().addOrUpdateUserStatus(dataObject, jsonObject);
            }
            else {
                final Map alertResponse = new CoreUserUtil().sendAccountPasswordResetMail(jsonObject);
                jsonObject.put("mailSent", alertResponse.get("status").equals("success"));
            }
            final boolean mailStatus = jsonObject.optBoolean("mailSent");
            String remarks = "ems.user.uac.reset_password_failed";
            if (mailStatus) {
                remarks = "ems.user.security.reset_password_initiated";
            }
            AlertsUtil.getInstance().addAlert(AlertConstants.USER_RESET_PASSWORD_INITIATED_ALERT, remarks, (Object)(domainName + "@@@" + name));
            if (!mailStatus) {
                throw new APIException(Response.Status.EXPECTATION_FAILED, "UAC019", "ems.admin.admin.email_server_not_reachable");
            }
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while sent password link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> getUserTokenDetails(final Map tokenDetails, final HttpServletRequest httpServletRequest) throws APIException {
        UserAccountServiceImpl.logger.log(Level.INFO, "Get the user token details");
        try {
            final Map<String, Object> userDetailsMap = new HashMap<String, Object>();
            final Long loginId = tokenDetails.get("LOGIN_ID");
            final String loginName = tokenDetails.get("NAME");
            final String domainName = tokenDetails.get("DOMAINNAME");
            final boolean localUser = domainName == null || domainName.equalsIgnoreCase("-");
            final String authType = localUser ? "localAuthentication" : "adAuthentication";
            userDetailsMap.put("authType", authType);
            userDetailsMap.put("status", 1);
            userDetailsMap.put("userName", loginName);
            if (!localUser) {
                this.activateADUser(loginId);
                userDetailsMap.put("domainName", domainName);
                userDetailsMap.put("status", 0);
                DCEventLogUtil.getInstance().addEvent(740, loginName, (HashMap)null, "ems.user.security.user_successfully_activated", (Object)loginName, true);
            }
            userDetailsMap.put("tokenType", tokenDetails.get("TOKEN_TYPE"));
            return userDetailsMap;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final PasswordException pwdEx) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Password Validation Failed", (Throwable)pwdEx);
            final String message = pwdEx.getMessage();
            throw new APIException(Response.Status.PRECONDITION_FAILED, "PASS0001", message);
        }
        catch (final Exception ex2) {
            UserAccountServiceImpl.logger.log(Level.WARNING, "Exception while Validating user token", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public void activateADUser(final Long loginId) throws Exception {
        final DataObject userDO = this.getUserAccountDetails(loginId);
        final String decodedPassword = RandomStringUtils.random(25, false, true);
        this.addUserPassword(userDO, decodedPassword);
    }
    
    public void addUserPassword(final DataObject userDO, final String password) throws Exception {
        final Long userId = (Long)userDO.getFirstValue("AaaLogin", "USER_ID");
        new CoreUserUtil().addOrUpdateAccountStatusExtnRow(userDO, Integer.valueOf(0), Integer.valueOf(1));
        final JSONObject userObject = new JSONObject();
        userObject.put("password", (Object)password);
        DMOnPremiseUserUtil.setNewUserPassword(userDO, userObject);
        new CoreUserUtil().removeUserLinkDetails(userId);
    }
    
    public DataObject getUserAccountDetails(final Long loginId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        selectQuery.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addJoin(new Join("AaaAccount", "AaaAccountStatusExtn", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "DOMAINNAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "ACCOUNT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "SERVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatusExtn", "ACCOUNT_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatusExtn", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("AaaAccountStatusExtn", "REMARKS"));
        final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
        selectQuery.setCriteria(criteria);
        final DataObject userDO = SyMUtil.getPersistence().get(selectQuery);
        return userDO;
    }
    
    static {
        UserAccountServiceImpl.logger = Logger.getLogger("UserManagementLogger");
    }
}
