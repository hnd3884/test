package com.me.ems.onpremise.uac.summaryserver.summary.api.validators;

import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;
import java.util.logging.Logger;
import com.me.ems.onpremise.uac.factory.UserAccountValidator;
import com.me.ems.onpremise.uac.validators.UserAccountValidatorImpl;

public class SSUserAccountValidatorImpl extends UserAccountValidatorImpl implements UserAccountValidator
{
    private static final Logger LOGGER;
    
    @Override
    public void validateAddition(final UserDetails userDetails) throws APIException {
        super.validateAddition(userDetails);
        this.validateSummaryProbeScope(userDetails);
    }
    
    @Override
    public void validateUpdate(final UserDetails userDetails) throws APIException {
        super.validateUpdate(userDetails);
        this.validateSummaryProbeScope(userDetails);
    }
    
    public void validateSummaryProbeScope(final UserDetails userDetails) throws APIException {
        try {
            final int computerScopeType = userDetails.getComputerScopeType();
            final int deviceScopeType = userDetails.getDeviceScopeType();
            final int probeScopeType = userDetails.getProbeScopeType();
            final List<String> probeIDs = userDetails.getProbeIDs();
            if (probeScopeType == 0) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC1151", "INVALID PROBE SCOPE");
            }
            if (probeScopeType == 2 && (probeIDs == null || probeIDs.isEmpty())) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC1152", "EMPTY PROBE LIST");
            }
            if (probeScopeType == 2 && probeIDs.size() != 1 && (computerScopeType != 0 || deviceScopeType != 0)) {
                throw new APIException(Response.Status.BAD_REQUEST, "UAC1153", "CustomGroup/RemoteOffice User cannot be created in more than one probe");
            }
        }
        catch (final APIException apiException) {
            SSUserAccountValidatorImpl.LOGGER.log(Level.SEVERE, "Exception while validating Probe Scope.");
            throw apiException;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
