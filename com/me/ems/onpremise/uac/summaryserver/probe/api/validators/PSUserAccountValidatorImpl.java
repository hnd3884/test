package com.me.ems.onpremise.uac.summaryserver.probe.api.validators;

import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.List;
import javax.ws.rs.core.Response;
import java.util.Map;
import org.json.JSONObject;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.factory.UserAccountValidator;
import com.me.ems.onpremise.uac.validators.UserAccountValidatorImpl;

public class PSUserAccountValidatorImpl extends UserAccountValidatorImpl implements UserAccountValidator
{
    private static final Logger LOGGER;
    
    @Override
    public void validateAddition(final UserDetails userDetails) throws APIException {
        super.validateAddition(userDetails);
        this.validateSummaryProbeScope(userDetails);
    }
    
    @Override
    public void validateUpdate(final UserDetails userDetails) throws APIException {
        try {
            PSUserAccountValidatorImpl.LOGGER.log(Level.INFO, "User Update Validation Begins.");
            final String userName = userDetails.getUserName();
            String domainName = userDetails.getDomainName();
            domainName = ((domainName == null || domainName.trim().isEmpty()) ? "-" : domainName);
            final Long loginId = DMUserHandler.getLoginIdForUser(userName, domainName);
            final int probeScopeType = userDetails.getProbeScopeType();
            final List<String> probeIDs = userDetails.getProbeIDs();
            final String thisProbeID = String.valueOf(ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID());
            String modifyUserAction;
            if (probeScopeType == 1 || probeIDs.contains(thisProbeID)) {
                if (loginId == null) {
                    this.validateAddition(userDetails);
                    final JSONObject probeHandlerObject = new JSONObject((Map)userDetails.getProbeHandlerObject());
                    final boolean isValidProbeHandlerObj = probeHandlerObject.isNull("password") || probeHandlerObject.isNull("salt") || probeHandlerObject.isNull("algorithm") || probeHandlerObject.isNull("createdTime");
                    if (isValidProbeHandlerObj) {
                        throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "INSUFFICIENT SUMMARY GENERATED CREDENTIAL DATA");
                    }
                    modifyUserAction = "addUserInProbe";
                    PSUserAccountValidatorImpl.LOGGER.log(Level.INFO, "User Modification Action = Add");
                }
                else {
                    this.validateUserData(userDetails);
                    this.runNameChangeCheck(userDetails);
                    modifyUserAction = "updateUserInProbe";
                    PSUserAccountValidatorImpl.LOGGER.log(Level.INFO, "User Modification Action = Update");
                }
            }
            else {
                if (loginId == null) {
                    throw new APIException(Response.Status.BAD_REQUEST, "UAC003", "User does not Exists");
                }
                modifyUserAction = "deleteUserInProbe";
                PSUserAccountValidatorImpl.LOGGER.log(Level.INFO, "User Modification Action = Delete");
            }
            userDetails.setModifyUserProbeAction(modifyUserAction);
            PSUserAccountValidatorImpl.LOGGER.log(Level.INFO, "User Update Validation Successful.");
        }
        catch (final APIException apiException) {
            PSUserAccountValidatorImpl.LOGGER.log(Level.SEVERE, "Exception while validating update user request");
            throw apiException;
        }
    }
    
    private void validateProbeHandlerObject(final JSONObject probeHandlerObject) throws APIException {
        try {
            if (probeHandlerObject.length() == 0) {
                throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "EMPTY LIST OF SUMMARY GENERATED DATA");
            }
            if (probeHandlerObject.isNull("loginID") || probeHandlerObject.isNull("userID") || probeHandlerObject.isNull("accountID") || probeHandlerObject.isNull("contactInfoID")) {
                throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0009", "INSUFFICIENT SUMMARY GENERATED DATA");
            }
        }
        catch (final APIException apiException) {
            PSUserAccountValidatorImpl.LOGGER.log(Level.SEVERE, "Exception while processing probe handler object");
            throw apiException;
        }
    }
    
    public void validateSummaryProbeScope(final UserDetails userDetails) throws APIException {
        final JSONObject probeHandlerObject = new JSONObject((Map)userDetails.getProbeHandlerObject());
        this.validateProbeHandlerObject(probeHandlerObject);
    }
    
    private void runNameChangeCheck(final UserDetails userDetails) throws APIException {
        final Long loginId = userDetails.getUserID();
        final String oldUserName = DMUserHandler.getDCUser(loginId);
        String userName = userDetails.getUserName();
        boolean isUserNameChangeAllowed = false;
        final String noOfTechnicians = LicenseProvider.getInstance().getNoOfTechnicians();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        try {
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
            userName = ((userName == null || userName.isEmpty()) ? oldUserName : userName.toLowerCase().trim());
            userDetails.setUserName(userName);
        }
        catch (final APIException dx) {
            throw dx;
        }
        catch (final Exception e) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
