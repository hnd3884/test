package com.me.ems.onpremise.uac.validators;

import com.me.ems.onpremise.uac.core.CoreUserUtil;
import java.util.Map;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.factory.UserAccountValidator;

public class UserAccountValidatorImpl implements UserAccountValidator
{
    public static Logger logger;
    
    public void validateUserData(final UserDetails userDetails) throws APIException {
        String userName = userDetails.getUserName();
        final String authType = userDetails.getAuthType();
        final String roleID = userDetails.getRoleID();
        final String language = userDetails.getLanguage();
        if (userName == null || userName.isEmpty() || roleID == null || roleID.isEmpty()) {
            throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "insufficient data");
        }
        userName = userName.toLowerCase().trim();
        userDetails.setUserName(userName);
        userDetails.setAuthType((authType == null || authType.isEmpty()) ? "localAuthentication" : authType);
        userDetails.setLanguage((language == null || language.isEmpty()) ? "en_US" : language);
    }
    
    public void runCommonValidation(final UserDetails userDetails) throws Exception {
        final List<String> customerIDs = userDetails.getCustomerIDs();
        final Long roleID = Long.valueOf(userDetails.getRoleID());
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        final String userWithoutSmtp = SyMUtil.getSyMParameter("user_without_smtp");
        if (userDetails.getUserID() != null && !userDetails.getUserID().equals(DBUtil.getUVHValue("AaaUser:user_id:0")) && (userWithoutSmtp == null || !userWithoutSmtp.equalsIgnoreCase("true")) && !ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
            throw new APIException("SMTP002");
        }
        ApiFactoryProvider.getUserManagementAPIHandler().validateUser(userDetails);
        if (isMSP) {
            final boolean isDefaultAdministratorRole = DMUserHandler.isDefaultAdministratorRole(roleID);
            try {
                if (CustomerInfoUtil.getInstance().getCustomerIdsFromDB() == null) {
                    throw new APIException(Response.Status.PRECONDITION_FAILED, "IAM0007", "desktopcentral.webclient.taglib.SoMMessageTag.No_Customer_Is_Managed");
                }
            }
            catch (final SyMException ex) {
                UserAccountValidatorImpl.logger.log(Level.SEVERE, "Error occurred while add or modify method ", (Throwable)ex);
                throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
            }
            if (!isDefaultAdministratorRole && (customerIDs == null || customerIDs.isEmpty())) {
                throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "ems.api.error.insuffcient_data");
            }
        }
    }
    
    @Override
    public void validateAddition(final UserDetails userDetails) throws APIException {
        this.validateUserData(userDetails);
        final String userName = userDetails.getUserName();
        String domainName = userDetails.getDomainName();
        domainName = ((domainName == null || domainName.trim().isEmpty()) ? "-" : domainName);
        try {
            if (!domainName.equalsIgnoreCase("-")) {
                final HashMap<String, Object> resultMap = UserManagementUtil.checkIfUserIsValid(userDetails.getUserName(), domainName);
                if (!resultMap.get("isValidUser")) {
                    throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
                }
            }
            final Long loginID = DMUserHandler.getLoginIdForUser(userName, domainName);
            if ((domainName.equals("-") || domainName.equalsIgnoreCase("local")) && !UserManagementUtil.isUserNameLengthCriteriaPassed(userName)) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC004", "ems.admin.uac.username_char_limit");
            }
            final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
            final boolean ignoreDefaultAdmin = userName.equalsIgnoreCase("admin") && (domainName.equals("-") || domainName.equalsIgnoreCase("local")) && DMOnPremiseUserUtil.isDefaultAdminDisabled(defaultAdminUVHLoginID);
            boolean isUserAccountAvailable = true;
            final Long sdpUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, "HelpDesk");
            final Long aeUserStatus = SolutionUtil.getInstance().getSDPUserStatus(loginID, "AssetExplorer");
            if (sdpUserStatus != -1L || aeUserStatus != -1L) {
                isUserAccountAvailable = false;
            }
            if (loginID != null && !ignoreDefaultAdmin && isUserAccountAvailable) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC006", "ems.admin.uac.user_exists", new String[] { domainName.equals("-") ? "local" : domainName, userName });
            }
            if (LicenseProvider.getInstance().isUserLimitReached()) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC007", "ems.admin.uac.current_user_mismatch");
            }
            this.runCommonValidation(userDetails);
        }
        catch (final APIException dx) {
            throw dx;
        }
        catch (final Exception e) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public void validateUpdate(final UserDetails userDetails) throws APIException {
        final String roleID = userDetails.getRoleID();
        final Long loginID = userDetails.getUserID();
        final Long userID = DMUserHandler.getDCUserID(loginID);
        final String oldUserName = DMUserHandler.getDCUser(loginID);
        String userName = userDetails.getUserName();
        userName = ((userName == null || userName.isEmpty()) ? oldUserName : userName.toLowerCase().trim());
        userDetails.setUserName(userName);
        final String noOfTechnicians = LicenseProvider.getInstance().getNoOfTechnicians();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String authType = userDetails.getAuthType();
        final String language = userDetails.getLanguage();
        userDetails.setAuthType((authType == null || authType.isEmpty()) ? "localAuthentication" : authType);
        userDetails.setLanguage((language == null || language.isEmpty()) ? "en_US" : language);
        boolean isUserNameChangeAllowed = false;
        try {
            if (userID == null) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
            }
            if (roleID == null || roleID.isEmpty()) {
                throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "ems.rest.api_insufficient_input_params", new String[] { "roleId" });
            }
            if (licenseType != null && licenseType.equals("R") && noOfTechnicians != null && noOfTechnicians.equals("1")) {
                isUserNameChangeAllowed = true;
            }
            if (!isUserNameChangeAllowed && !oldUserName.equalsIgnoreCase(userName)) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC004", "ems.admin.uac.user_change_not_allowed");
            }
            if (isUserNameChangeAllowed) {
                String domainName = userDetails.getDomainName();
                domainName = ((domainName == null || domainName.trim().isEmpty()) ? "-" : domainName);
                if ((domainName.equals("-") || domainName.equalsIgnoreCase("local")) && !UserManagementUtil.isUserNameLengthCriteriaPassed(userName)) {
                    throw new APIException(Response.Status.BAD_REQUEST, "UAC004", "ems.admin.uac.username_char_limit");
                }
            }
            this.runCommonValidation(userDetails);
        }
        catch (final APIException dx) {
            throw dx;
        }
        catch (final Exception e) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    @Override
    public void validateDelete(final Long loginID, final Map<String, Object> contactDetails) throws APIException {
        final Long userID = DMUserHandler.getDCUserID(loginID);
        if (userID == null) {
            throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "ems.admin.uac.user_not_exists");
        }
    }
    
    public void validatePasswordChange(final Map<String, String> updatePasswordMap) throws APIException {
        try {
            final Long loginId = (updatePasswordMap.get("loginId") != null) ? Long.valueOf(updatePasswordMap.get("loginId")) : null;
            final String loginName = updatePasswordMap.get("loginName");
            final String newPassword = updatePasswordMap.get("newPassword");
            if (loginId == null || loginName == null || newPassword == null) {
                UserAccountValidatorImpl.logger.log(Level.INFO, "validatePasswordChange - some params are null. name: {0} ID: {1}", new Object[] { loginName, loginId });
                throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0003", "ems.rest.api.param.missing", new String[] { "loginId or loginName or newPassword" });
            }
            this.validateLoginNameAgainstId(loginId, loginName);
            final Long currentLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (new UserManagementUtil().isDefaultPasswordUsed(currentLoginId) && !LicenseProvider.getInstance().getLicenseType().equalsIgnoreCase("T") && !loginId.equals(currentLoginId)) {
                UserAccountValidatorImpl.logger.log(Level.INFO, "Current user's loginID is {0}, but passed loginID is {1}.", new Object[] { currentLoginId, loginId });
                throw new APIException(Response.Status.BAD_REQUEST, "UAC002", "ems.admin.uac.current_user_mismatch");
            }
        }
        catch (final APIException dce) {
            throw dce;
        }
        catch (final Exception e) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    public void validateLoginNameAgainstId(final Long loginId, final String loginName) throws APIException {
        final String loginNameFromDb = CoreUserUtil.getLoginNameFromId(loginId);
        if (!loginNameFromDb.equalsIgnoreCase(loginName)) {
            throw new APIException(Response.Status.BAD_REQUEST, "IAM0006", "ems.admin.uac.login_name_mismatch");
        }
    }
    
    static {
        UserAccountValidatorImpl.logger = Logger.getLogger("UserManagementLogger");
    }
}
