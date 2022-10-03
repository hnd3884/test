package com.me.ems.onpremise.uac.api.v1.service;

import javax.ws.rs.core.Response;
import java.util.Map;
import org.json.JSONObject;
import com.me.ems.onpremise.uac.core.PasswordPolicyUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.ems.onpremise.uac.api.v1.model.PasswordPolicy;

public class PasswordPolicyService
{
    public PasswordPolicy getPasswordPolicyDetails() {
        JSONObject passwordPolicyDetails = DMOnPremiseUserUtil.getPasswordPolicyDetails();
        if (passwordPolicyDetails != null && passwordPolicyDetails.length() > 0) {
            return PasswordPolicyUtil.constructPasswordPolicy(passwordPolicyDetails);
        }
        passwordPolicyDetails = DMOnPremiseUserUtil.getDefaultPasswordPolicy();
        return PasswordPolicyUtil.constructPasswordPolicy(passwordPolicyDetails);
    }
    
    public Response savePasswordPolicyDetails(final Map passwordPolicy) {
        if (!this.validatePasswordPolicyDetails(passwordPolicy)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        final boolean isSaved = PasswordPolicyUtil.savePasswordPolicyDetails(passwordPolicy);
        if (isSaved) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    public boolean validatePasswordPolicyDetails(final Map passwordPolicy) {
        return passwordPolicy.get("complexPasswordEnabled") != null && passwordPolicy.get("loginRestrictionEnabled") != null && passwordPolicy.get("preventReuseFor") != null && passwordPolicy.get("minimumLength") != null && (!passwordPolicy.get("loginRestrictionEnabled") || (passwordPolicy.get("lockPeriod") != null && passwordPolicy.get("badAttemptCount") != null));
    }
}
