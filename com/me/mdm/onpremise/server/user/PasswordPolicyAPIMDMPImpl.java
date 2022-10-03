package com.me.mdm.onpremise.server.user;

import javax.transaction.SystemException;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.PasswordPolicyAPI;

public class PasswordPolicyAPIMDMPImpl implements PasswordPolicyAPI
{
    private Logger logger;
    
    public PasswordPolicyAPIMDMPImpl() {
        this.logger = Logger.getLogger("UserManagementLogger");
    }
    
    public JSONObject getPasswordPolicyDetails(final JSONObject requestJSON) throws APIHTTPException {
        try {
            JSONObject policyDetails = DMOnPremiseUserUtil.getPasswordPolicyDetails();
            if (policyDetails == null || policyDetails.length() <= 0) {
                policyDetails = DMOnPremiseUserUtil.getDefaultPasswordPolicy();
            }
            return new JSONObject(policyDetails.toString().toLowerCase());
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getPasswordPolicyDetails() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getPasswordPolicyDetails() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addPasswordPolicy(final JSONObject jsonObject) throws APIHTTPException {
        try {
            MDMUtil.getUserTransaction().begin();
            final Integer minLenth = jsonObject.optInt("min_length", 5);
            final Integer preventReuseFor = jsonObject.optInt("PREVENT_REUSE_FOR".toLowerCase(), 0);
            final int isComplexPassword = jsonObject.optInt("is_complex_password", 0);
            final Boolean loginRestrictionEnabled = jsonObject.optBoolean("enable_login_restriction", true);
            final Integer badAttempts = jsonObject.optInt("bad_attempt", 10);
            final Integer lockPeriod = jsonObject.optInt("lock_period", 5);
            final JSONObject policyJSON = new JSONObject();
            policyJSON.put("MIN_LENGTH", (Object)String.valueOf(minLenth));
            policyJSON.put("PREVENT_REUSE_FOR", (Object)String.valueOf(preventReuseFor));
            policyJSON.put("IS_COMPLEX_PASSWORD", isComplexPassword == 1);
            policyJSON.put("ENABLE_LOGIN_RESTRICTION", (Object)loginRestrictionEnabled);
            policyJSON.put("BAD_ATTEMPT", (Object)String.valueOf(badAttempts));
            policyJSON.put("LOCK_PERIOD", (Object)String.valueOf(lockPeriod));
            DMOnPremiseUserUtil.addOrUpdatePasswordPolicy(policyJSON);
            final JSONObject responseJSON = new APIUtil().wrapServerJSONToUserJSON(DMOnPremiseUserUtil.getPasswordPolicyDetails());
            MDMUtil.getUserTransaction().commit();
            return responseJSON;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- addPasswordPolicy() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- addPasswordPolicy() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void removePasswordPolicy(final JSONObject requestJSON) throws APIHTTPException {
        try {
            MDMUtil.getUserTransaction().begin();
            final JSONObject defaultPasswordPolicy = DMOnPremiseUserUtil.getDefaultPasswordPolicy();
            defaultPasswordPolicy.put("MIN_LENGTH", (Object)String.valueOf(defaultPasswordPolicy.getInt("MIN_LENGTH")));
            defaultPasswordPolicy.put("PREVENT_REUSE_FOR", (Object)String.valueOf(defaultPasswordPolicy.getInt("PREVENT_REUSE_FOR")));
            defaultPasswordPolicy.put("BAD_ATTEMPT", (Object)String.valueOf(defaultPasswordPolicy.getInt("BAD_ATTEMPT")));
            defaultPasswordPolicy.put("LOCK_PERIOD", (Object)String.valueOf(defaultPasswordPolicy.getInt("LOCK_PERIOD")));
            DMOnPremiseUserUtil.addOrUpdatePasswordPolicy(defaultPasswordPolicy);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- removePasswordPolicy() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- removePasswordPolicy() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
