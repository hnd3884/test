package com.me.mdm.onpremise.server.user;

import com.me.devicemanagement.onpremise.server.twofactor.GoogleAuthAction;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.APIUtil;
import javax.transaction.SystemException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.TwoFactorAuthenticationAPI;

public class TwoFactorAuthenticationAPIMDMPImpl implements TwoFactorAuthenticationAPI
{
    private Logger logger;
    
    public TwoFactorAuthenticationAPIMDMPImpl() {
        this.logger = Logger.getLogger("UserManagementLogger");
    }
    
    public JSONObject getTFADetails(final JSONObject requestJSON) throws APIHTTPException {
        try {
            return TwoFactorAuthenticationHandler.getInstance().getTFADetails();
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- getTFADetails() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- getTFADetails() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addTFA(final JSONObject requestJSON) throws APIHTTPException {
        try {
            MDMUtil.getUserTransaction().begin();
            final JSONObject responseJSON = TwoFactorAuthenticationHandler.getInstance().addOrUpdateTFADetails(requestJSON);
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
                this.logger.log(Level.SEVERE, " -- addTFA() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- addTFA() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public boolean removeTFA(final JSONObject requestJSON) throws APIHTTPException {
        try {
            MDMUtil.getUserTransaction().begin();
            requestJSON.put("enable_tfa", false);
            final boolean stateChange = TwoFactorAuthenticationHandler.getInstance().addOrUpdateTFADetails(requestJSON).optBoolean("state_change");
            MDMUtil.getUserTransaction().commit();
            return stateChange;
        }
        catch (final Exception e) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final SystemException e2) {
                this.logger.log(Level.SEVERE, " -- error transaction rollback() >   Error ", (Throwable)e2);
            }
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, " -- removeTFA() >   Error ", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, " -- removeTFA() >   Error ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void sendEmailInvitation(final JSONObject apiRequest) {
        try {
            final Long userId = APIUtil.getResourceID(apiRequest, "technician_id");
            final JSONObject userJSON = new JSONObject();
            userJSON.put("user_id", (Object)userId);
            final JSONObject userDetails = TechniciansHandler.getTechniciansInstance(apiRequest, userJSON).getUserDetails(userJSON);
            if (MDMStringUtils.isEmpty(userDetails.optString("user_email"))) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("dc.mdm.email.account_type.config_mail", new Object[0]).concat(String.format(" %s", userDetails.optString("user_name"))) });
            }
            TwoFactorAction.sendEmailInvitation(userId);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.INFO, "Invalid input on while sending email invite", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.INFO, "Issue on sending email invite for TFA", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    public JSONObject getQRCodeForGoogleAuthInvite(final JSONObject apiRequest) {
        try {
            final JSONObject responseJSON = new JSONObject();
            final Long userId = APIUtil.getResourceID(apiRequest, "technician_id");
            final JSONObject userJSON = new JSONObject();
            userJSON.put("user_id", (Object)userId);
            final JSONObject userData = TechniciansHandler.getTechniciansInstance(apiRequest, userJSON).getUserDetails(userJSON);
            final GoogleAuthAction googleAuthAction = new GoogleAuthAction((long)userId);
            final String barUrl = googleAuthAction.getQRBarPath();
            final String keyLabel = googleAuthAction.getKeyLabel();
            final String secret = googleAuthAction.getSecret();
            responseJSON.put("bar_url", (Object)barUrl);
            responseJSON.put("key_label", (Object)keyLabel);
            responseJSON.put("secret_key", (Object)secret);
            responseJSON.put("user_id", (Object)userId);
            responseJSON.put("user_name", userData.get("user_name"));
            responseJSON.put("user_email", userData.get("user_email"));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.INFO, "Invalid input on getting QR code for Google TFA", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.INFO, "Issue on getting QR code for Google TFA", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
}
