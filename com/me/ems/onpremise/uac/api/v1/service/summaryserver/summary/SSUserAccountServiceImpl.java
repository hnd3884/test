package com.me.ems.onpremise.uac.api.v1.service.summaryserver.summary;

import com.me.devicemanagement.onpremise.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import com.me.ems.onpremise.uac.core.UserConstants;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.ems.onpremise.uac.core.UserOperationsInterface;
import com.me.ems.framework.uac.handler.UserOperationsHandler;
import java.util.Set;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.SSOnPremiseUserUtil;
import java.util.Collection;
import java.util.LinkedHashSet;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.io.File;
import com.me.ems.onpremise.uac.core.CoreUserUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.api.DCSDPRequestAPI;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.ProbeUsersUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import java.util.Map;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.ems.onpremise.security.securitysettings.SecuritySettingsUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.HashMap;
import com.me.ems.onpremise.uac.summaryserver.summary.util.SSCoreUserUtil;
import com.me.ems.onpremise.uac.summaryserver.common.util.UserServiceUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import com.me.ems.onpremise.uac.api.v1.service.factory.UserAccountService;
import com.me.ems.onpremise.uac.api.v1.service.UserAccountServiceImpl;

public class SSUserAccountServiceImpl extends UserAccountServiceImpl implements UserAccountService
{
    @Override
    public Response addUser(final UserDetails userDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        final JSONObject addUserObject = new JSONObject();
        try {
            SSUserAccountServiceImpl.logger.log(Level.INFO, "-------------------------------------------------");
            SSUserAccountServiceImpl.logger.log(Level.INFO, "User Addition Begins.");
            final List<String> roleNameList = new ArrayList<String>();
            UserAccountServiceImpl.addCommonParameter(userDetails, addUserObject);
            final List<Long> probeList = UserServiceUtil.processProbeScope(userDetails, addUserObject, roleNameList);
            UserServiceUtil.processComputerScope(userDetails, addUserObject, roleNameList);
            UserServiceUtil.processDeviceScope(userDetails, addUserObject, roleNameList);
            UserAccountServiceImpl.processNetworkDeviceScope(userDetails, addUserObject, roleNameList);
            UserAccountServiceImpl.processRoles(userDetails, addUserObject, roleNameList);
            addUserObject.put("technicianID", (Object)user.getUserID());
            addUserObject.put("companyLogoLink", (Object)"images/login/login_logo.gif");
            addUserObject.put("adminEmail", (Object)user.getEmail());
            final JSONObject probeHandlerObject = new JSONObject();
            final SSCoreUserUtil sscoreUserUtil = new SSCoreUserUtil();
            final Long loginId = sscoreUserUtil.addUser(addUserObject, probeHandlerObject);
            if (addUserObject.has("token")) {
                probeHandlerObject.put("token", addUserObject.get("token"));
            }
            SSUserAccountServiceImpl.logger.log(Level.INFO, "UserManagement: Returned Id Value : {0}", loginId);
            SSUserAccountServiceImpl.logger.log(Level.INFO, "UserManagement: SpiceUser Name: {0}", userDetails.getSpiceUser());
            final Map<String, Object> resultMap = new HashMap<String, Object>();
            if (loginId != null && loginId != -1L) {
                final boolean mailStatus = addUserObject.getBoolean("mailSent");
                String remarks = "ems.admin.uac.user_activation_mail_not_sent";
                int eventCode = 736;
                if (mailStatus) {
                    remarks = "ems.admin.uac.user_activation_mail_sent_successfully";
                    eventCode = 735;
                }
                DCEventLogUtil.getInstance().addEvent(eventCode, user.getName(), (HashMap)null, remarks, (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
                UserServiceUtil.updateSpiceWorkDetails(userDetails, loginId);
                DCEventLogUtil.getInstance().addEvent(701, user.getName(), (HashMap)null, "ems.admin.uac.user_successfully_added", (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
                SSUserAccountServiceImpl.logger.log(Level.INFO, "UserMgmt: User '" + userDetails.getUserName() + "' added successfully by " + user.getName());
                resultMap.put("mailStatus", mailStatus);
                String domainName = userDetails.getDomainName();
                domainName = ((domainName == null || domainName.trim().isEmpty()) ? "-" : domainName);
                resultMap.put("domainName", domainName.equals("-") ? "local" : domainName);
                resultMap.put("userName", userDetails.getUserName());
                resultMap.put("loginId", loginId);
                resultMap.put("securityAdvisoryCode", SecuritySettingsUtil.getInstance().getSecurityAdvisoryCodeAfterAddedAnyUser());
                final Map meta = new HashMap();
                meta.put("userCount", DMUserHandler.getUsersCountWithLogin());
                resultMap.put("meta", meta);
                httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
                httpServletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject);
                httpServletRequest.setAttribute("targetProbes", (Object)probeList);
                httpServletRequest.setAttribute("isReqdForNewProbe", (Object)(userDetails.getProbeScopeType() == 1));
                httpServletRequest.setAttribute("eventID", (Object)950701);
                httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
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
            SSUserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occurred  addUser method ", (Throwable)ex);
            throw new APIException(Response.Status.BAD_REQUEST, errorCode, errorMessage);
        }
        catch (final Exception exception) {
            DCEventLogUtil.getInstance().addEvent(702, user.getName(), (HashMap)null, "ems.admin.uac.add_user_failed", (Object)(userDetails.getDomainName() + "@@@" + userDetails.getUserName()), true);
            SSUserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occurred  addUser method ", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Response deleteUser(final Long loginID, final User currentUser, final Map<String, Object> contactDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            SSUserAccountServiceImpl.logger.log(Level.INFO, "-------------------------------------------------");
            SSUserAccountServiceImpl.logger.log(Level.INFO, "User Deletion Begins.");
            final boolean userCreatedByDC = DMOnPremiseUserUtil.isUserCreatedByDC(loginID);
            final Long userId = DMUserHandler.getDCUserID(loginID);
            final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginID);
            String userDomainName = DMUserHandler.getDCUserDomain(loginID);
            final String loginName = DMUserHandler.getUserName(loginID);
            String tempMsg = userCreatedByDC ? "dc.admin.uac.USER_SUCCESSFULLY_DEL" : "dc.admin.uac.USER_SUCCESSFULLY_DEL_SDP";
            int eventID = 705;
            final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
            final HashMap<String, Object> userDelPII = new HashMap<String, Object>(4);
            UserAccountServiceImpl.validateContactDetails(contactDetails, userDelPII, userId);
            if (userDomainName == null || userDomainName.equalsIgnoreCase("null") || userDomainName.equalsIgnoreCase("-")) {
                userDomainName = "local";
            }
            String tempMsgArgs = DMIAMEncoder.encodeHTML(userDomainName) + "@@@" + DMIAMEncoder.encodeHTML(loginName);
            boolean status;
            if (loginID.equals(defaultAdminUVHLoginID)) {
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                final boolean bAssetExplorerEnabled = SolutionUtil.getInstance().isAEIntegrationMode();
                final boolean bSdpEnabled = SolutionUtil.getInstance().isInvIntegrationMode();
                if (bSdpEnabled && !isMSP) {
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
                        ApiFactoryProvider.getUserManagementAPIHandler().handleAdminUserDelete(loginID, DMUserHandler.getUserContactProps(userId, (HashMap)userDelPII));
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
            SSUserAccountServiceImpl.logger.log(Level.INFO, () -> "'" + s + "' deleted successfully by '" + user.getName() + "'.");
            final Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("userCreatedByDC", userCreatedByDC);
            if (eventID == 705) {
                resultMap.put("domainName", userDomainName);
                resultMap.put("userName", loginName);
                httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
                httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
                if (ProbeUsersUtil.isUserManagingAllProbes(loginID)) {
                    httpServletRequest.setAttribute("isReqdForNewProbe", (Object)false);
                    httpServletRequest.setAttribute("eventID", (Object)950708);
                    httpServletRequest.setAttribute("eventUniqueID", (Object)loginID);
                }
                SSUserAccountServiceImpl.logger.log(Level.INFO, "User Deletion Successful.");
                SSUserAccountServiceImpl.logger.log(Level.INFO, "-------------------------------------------------");
                return Response.status(Response.Status.OK).entity((Object)resultMap).build();
            }
            SSUserAccountServiceImpl.logger.log(Level.INFO, "User Deletion Failed.");
            SSUserAccountServiceImpl.logger.log(Level.INFO, "-------------------------------------------------");
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.rest.api_internal_error");
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception e) {
            DCEventLogUtil.getInstance().addEvent(706, currentUser.getName(), (HashMap)null, "dc.admin.uac.USER_DEL_FAILED", (Object)"", true);
            SSUserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occured  deleteUser method ", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Response modifyUser(final UserDetails userDetails, final User dcUser, final HttpServletRequest httpServletRequest) throws APIException {
        final JSONObject modifyUserObject = new JSONObject();
        final Long loginId = userDetails.getUserID();
        modifyUserObject.put("loginID", (Object)loginId);
        final String roleID = userDetails.getRoleID();
        final List<Long> managedProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
        String userName = userDetails.getUserName();
        final String oldUserName = DMUserHandler.getDCUser(loginId);
        userName = ((userName == null || userName.isEmpty()) ? oldUserName : userName);
        String domainName = userDetails.getDomainName();
        String oldDomainName = DMUserHandler.getDCUserDomain(loginId);
        domainName = ((domainName == null || domainName.isEmpty()) ? "-" : domainName);
        userDetails.setDomainName(domainName);
        final boolean isDomainNameChanged = oldDomainName != null && !oldDomainName.equalsIgnoreCase(domainName);
        final boolean isScopeChangedToAllProbes = !ProbeUsersUtil.isUserManagingAllProbes(loginId) && userDetails.getProbeScopeType() == 1;
        try {
            final boolean isProbeScopeChanged = ProbeUsersUtil.getUserProbeScopeType(loginId) != userDetails.getProbeScopeType();
            SSUserAccountServiceImpl.logger.log(Level.INFO, "-------------------------------------------------");
            SSUserAccountServiceImpl.logger.log(Level.INFO, "User Modification Begins.");
            final List<String> roleNameList = new ArrayList<String>();
            try {
                UserAccountServiceImpl.addCommonParameter(userDetails, modifyUserObject);
                UserServiceUtil.processComputerScope(userDetails, modifyUserObject, roleNameList);
                UserServiceUtil.processDeviceScope(userDetails, modifyUserObject, roleNameList);
                UserAccountServiceImpl.processNetworkDeviceScope(userDetails, modifyUserObject, roleNameList);
                final List<Long> probeList = UserServiceUtil.processProbeScope(userDetails, modifyUserObject, roleNameList);
                UserAccountServiceImpl.processRoles(userDetails, modifyUserObject, roleNameList);
                final String noOfTechniciansBought = LicenseProvider.getInstance().getNoOfTechnicians();
                if (noOfTechniciansBought == null) {
                    userName = oldUserName;
                }
                if (noOfTechniciansBought != null && !noOfTechniciansBought.equals("1")) {
                    userName = oldUserName;
                    if (isDomainNameChanged && DMUserHandler.isUserAccountAvailable(userName, domainName)) {
                        throw new APIException(Response.Status.BAD_REQUEST, "UAC006", "ems.admin.uac.user_exists", new String[] { domainName.equals("-") ? "local" : domainName, userName });
                    }
                }
                userDetails.setUserName(userName);
                UserServiceUtil.validateADUser(userDetails);
                UserServiceUtil.processMappingData(userDetails, modifyUserObject, loginId, roleID);
                userDetails.setDomainName(domainName);
                modifyUserObject.put("contactinfoID", ((Hashtable<K, Object>)DMUserHandler.getContactInfoProp(DMUserHandler.getDCUserID(loginId))).get("contactInfoID"));
                final boolean isLocalUser = userDetails.getAuthType().equals("localAuthentication");
                modifyUserObject.put("domainName", (Object)userDetails.getDomainName());
                final boolean isLanguageChanged = DMOnPremiseUserUtil.isLanguageChanged(dcUser.getLoginID(), userDetails.getLanguage());
                if (dcUser.getLoginID().equals(loginId) && isLanguageChanged) {
                    SSUserAccountServiceImpl.logger.log(Level.INFO, "Language changed for - {0}", userName);
                }
                if (domainName.equals("-")) {
                    userDetails.setDomainName("local");
                }
                final boolean userCreatedByDC = DMOnPremiseUserUtil.isUserCreatedByDC(loginId);
                if (!userCreatedByDC) {
                    UserServiceUtil.validateScopeOfSDPUser(userDetails);
                }
                new SSCoreUserUtil().modifyUser(modifyUserObject);
                oldDomainName = ((oldDomainName == null || oldDomainName.equals("-")) ? "local" : oldDomainName);
                SSUserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: User '" + oldDomainName + "\\" + oldUserName + "' has been successfully modified to " + userDetails.getDomainName() + "\\" + userName);
                SSUserAccountServiceImpl.logger.log(Level.INFO, "UserMgmt: User '" + userName + "' successfully modified by " + dcUser.getName());
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
                DCEventLogUtil.getInstance().addEvent(703, dcUser.getName(), (HashMap)null, event, (Object)args, true);
                if (oldUserName != null && !oldUserName.equalsIgnoreCase(userName)) {
                    SSUserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: User '" + oldUserName + "' has been successfully renamed as '" + userName + "'");
                    DCEventLogUtil.getInstance().addEvent(703, dcUser.getName(), (HashMap)null, "ems.admin.uac.user_successfully_rename", (Object)(oldUserName + "@@@" + userName), true);
                }
                UserServiceUtil.updateSpiceWorkDetails(userDetails, loginId);
                final Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("userCreatedByDC", userCreatedByDC);
                resultMap.put("domainName", userDetails.getDomainName());
                resultMap.put("userName", userName);
                if (isDomainNameChanged) {
                    resultMap.put("oldDomainName", oldDomainName);
                    resultMap.put("oldUserName", oldUserName);
                }
                final boolean isUserNewToProbe = UserServiceUtil.isUserNewToProbe(managedProbes, probeList);
                final Set<Long> uniqueProbeList = new LinkedHashSet<Long>(managedProbes);
                uniqueProbeList.addAll(probeList);
                final List<Long> targetProbes = new ArrayList<Long>(uniqueProbeList);
                if (isUserNewToProbe || isScopeChangedToAllProbes) {
                    SSUserAccountServiceImpl.logger.log(Level.INFO, "User has new Probe in Scope");
                    final JSONObject probeHandlerObject = new JSONObject((Map)SSOnPremiseUserUtil.generateModifyUserProbeHandlerObject(loginId));
                    httpServletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject);
                }
                httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
                httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
                if (isProbeScopeChanged) {
                    httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
                    httpServletRequest.setAttribute("eventID", (Object)950702);
                    httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
                }
                SSUserAccountServiceImpl.logger.log(Level.INFO, "User Modification Successful.");
                SSUserAccountServiceImpl.logger.log(Level.INFO, "-------------------------------------------------");
                return Response.status(Response.Status.OK).entity((Object)resultMap).build();
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
                DCEventLogUtil.getInstance().addEvent(704, dcUser.getName(), (HashMap)null, "dc.admin.uac.USER_MOD_FAILED", (Object)(oldDomainName + "@@@" + oldUserName), true);
                SSUserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occurred  modify method ", (Throwable)ex);
                throw new APIException(Response.Status.BAD_REQUEST, errorCode, errorMessage);
            }
        }
        catch (final APIException e) {
            throw e;
        }
        catch (final Exception e2) {
            DCEventLogUtil.getInstance().addEvent(704, dcUser.getName(), (HashMap)null, "dc.admin.uac.USER_MOD_FAILED", (Object)(oldDomainName + "@@@" + oldUserName), true);
            SSUserAccountServiceImpl.logger.log(Level.SEVERE, "UserMgmt: Error occured  modifyUser method ", e2);
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
            final Row loginExtnRow = userDO.getRow("AaaLoginExtn");
            Integer mdmScope = loginExtnRow.getInt("MDM_SCOPE");
            mdmScope = ((mdmScope == null) ? 0 : mdmScope);
            userDetailsMap.put("deviceScopeType", mdmScope);
            if (mdmScope == 1) {
                final List<Hashtable<String, Object>> mdmDeviceGroups = SSCoreUserUtil.getAssignedDeviceGroupsForUser(loginID);
                userDetailsMap.put("probeMobileDeviceGroups", mdmDeviceGroups);
            }
            final int dcScope = loginExtnRow.getLong("SCOPE").intValue();
            userDetailsMap.put("computerScopeType", dcScope);
            if (dcScope == 1) {
                final List<Hashtable<String, Object>> staticComputerGroups = SSCoreUserUtil.getAssignedComputerGroupsForUser(loginID);
                userDetailsMap.put("probeStaticCustomGroups", staticComputerGroups);
            }
            else if (dcScope == 2) {
                userDetailsMap.put("remoteOfficeGroups", ApiFactoryProvider.getUserManagementAPIHandler().getComputerUserMappingRemoteOffice(loginID));
            }
            final int probeScope = ProbeUsersUtil.getUserProbeScopeType(loginID);
            userDetailsMap.put("probeScopeType", probeScope);
            final List<Map<String, Object>> listOfManagedProbes = new ArrayList<Map<String, Object>>();
            final List<Long> managedProbeIDs = ProbeUsersUtil.getProbeIdsForLoginId(loginID);
            final ProbeDetailsUtil probeDetailsUtil = new ProbeDetailsUtil();
            for (final Long probeID : managedProbeIDs) {
                final Map<String, Object> probeDetails = new HashMap<String, Object>();
                probeDetails.put("probeID", probeID);
                probeDetails.put("probeName", probeDetailsUtil.getProbeName(probeID));
                listOfManagedProbes.add(probeDetails);
            }
            userDetailsMap.put("managedProbes", listOfManagedProbes);
            userDetailsMap.put("isSDPUser", UserServiceUtil.isSDPUser(loginID));
            return userDetailsMap;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception e) {
            SSUserAccountServiceImpl.logger.log(Level.WARNING, "UserMgmt: Exception occurred in getUserDetails", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> modifyUserContact(final Map<String, Object> userDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        final Map<String, Object> resultDetails = super.modifyUserContact(userDetails, user, httpServletRequest);
        final Long loginId = userDetails.get("loginID");
        final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
        httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950703);
            httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
        }
        return resultDetails;
    }
    
    @Override
    public Map<String, Object> addOrUpdatePassword(final Map tokenDetails, final HttpServletRequest httpServletRequest) throws APIException {
        final Map<String, Object> resultDetails = super.addOrUpdatePassword(tokenDetails, httpServletRequest);
        final Long loginId = tokenDetails.get("LOGIN_ID");
        final Map dcUser = DMUserHandler.getLoginDetails(loginId);
        final String loginName = dcUser.get("NAME");
        final String domainName = dcUser.get("DOMAINNAME");
        final Long userID = dcUser.get("USER_ID");
        ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(loginName, "system", domainName, userID);
        final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
        httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950706);
            httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
        }
        return resultDetails;
    }
    
    @Override
    public Map<String, Object> sendPasswordLink(final Long loginId, final User user, final Map<String, Object> probeDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final SSCoreUserUtil coreUserUtil = new SSCoreUserUtil();
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
            final JSONObject probeHandlerObject = new JSONObject();
            if (jsonObject.has("token")) {
                probeHandlerObject.put("token", jsonObject.get("token"));
            }
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
            final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject);
            httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
            if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
                httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
                httpServletRequest.setAttribute("eventID", (Object)950705);
                httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
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
            SSUserAccountServiceImpl.logger.log(Level.WARNING, "Exception while sent password link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public void sendPasswordLink(final Map requestDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final SSCoreUserUtil coreUserUtil = new SSCoreUserUtil();
            Long userId = null;
            Long loginId = null;
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
                loginId = loginRow.getLong("LOGIN_ID");
            }
            final String userMail = (String)dataObject.getFirstValue("AaaContactInfo", "EMAILID");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("newUser", false);
            jsonObject.put("userEmail", (Object)userMail);
            jsonObject.put("domainName", (Object)domainName);
            jsonObject.put("technicianID", (Object)userId);
            jsonObject.put("userID", (Object)userId);
            final JSONObject probeHandlerObject = new JSONObject();
            if (domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) {
                new SSCoreUserUtil().addOrUpdateUserStatus(dataObject, jsonObject);
                if (jsonObject.has("token")) {
                    probeHandlerObject.put("token", jsonObject.get("token"));
                }
            }
            else {
                jsonObject.put("eventCode", (Object)UserConstants.UserAlertConstant.THIRD_PARTY_RESET_PASSWORD);
                final Map alertResponse = new SSCoreUserUtil().sendAccountPasswordResetMail(jsonObject);
                jsonObject.put("mailSent", alertResponse.get("status").equals("success"));
            }
            final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject);
            httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
            if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
                httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
                httpServletRequest.setAttribute("eventID", (Object)950704);
                httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
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
            SSUserAccountServiceImpl.logger.log(Level.WARNING, "Exception while sent password link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> resendUserInvite(final Long loginId, final User user, final Map<String, Object> probeDetails, final HttpServletRequest httpServletRequest) throws APIException {
        try {
            if (!ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                throw new APIException("SMTP002");
            }
            final SSCoreUserUtil coreUserUtil = new SSCoreUserUtil();
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
            final JSONObject probeHandlerObject = new JSONObject();
            if (jsonObject.has("token")) {
                probeHandlerObject.put("token", jsonObject.get("token"));
            }
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
            final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject);
            httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
            if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
                httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
                httpServletRequest.setAttribute("eventID", (Object)950707);
                httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
            }
            return resultDetails;
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            SSUserAccountServiceImpl.logger.log(Level.WARNING, "Exception while resend user activation link", ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public Map<String, Object> updateAdminPersonalizationDetails(final Map<String, Object> detailsMap, final User dcUser, final HttpServletRequest httpServletRequest) throws APIException {
        final Map<String, Object> resultDetails = super.updateAdminPersonalizationDetails(detailsMap, dcUser, httpServletRequest);
        final Long loginId = dcUser.getLoginID();
        final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
        httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950801);
            httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
        }
        return resultDetails;
    }
    
    @Override
    public Map<String, Object> getUserTokenDetails(final Map tokenDetails, final HttpServletRequest httpServletRequest) throws APIException {
        final Map<String, Object> resultDetails = super.getUserTokenDetails(tokenDetails, httpServletRequest);
        final Long loginId = tokenDetails.get("LOGIN_ID");
        final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(loginId);
        httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        if (ProbeUsersUtil.isUserManagingAllProbes(loginId)) {
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950709);
            httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
        }
        return resultDetails;
    }
    
    @Override
    public org.json.simple.JSONObject getAddOrUpdateUserLogData(final UserDetails userDetails) {
        final org.json.simple.JSONObject logData = super.getAddOrUpdateUserLogData(userDetails);
        final int probeScope = userDetails.getProbeScopeType();
        try {
            logData.put((Object)"probe_scope", (Object)((probeScope == 0) ? "no_probe" : ((probeScope == 1) ? "all_probes" : ((probeScope == 2) ? "specific_probe(s)" : Integer.valueOf(probeScope)))));
        }
        catch (final Exception exception) {
            logData.put((Object)"probe_scope", (Object)probeScope);
        }
        return logData;
    }
}
