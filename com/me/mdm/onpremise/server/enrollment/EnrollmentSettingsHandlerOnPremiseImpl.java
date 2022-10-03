package com.me.mdm.onpremise.server.enrollment;

import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;

public class EnrollmentSettingsHandlerOnPremiseImpl extends EnrollmentSettingsHandler
{
    protected boolean isApproverHandlingAvailable() {
        return true;
    }
    
    public boolean isADAuthenticationApplicable() {
        return true;
    }
    
    protected JSONObject isAuthenticationHandlingAvailable(final Long customerID) {
        try {
            return new JSONObject().put("status", false);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, null, (Throwable)e);
            return null;
        }
    }
    
    protected boolean isSelfEnrollmentEnabledByDefault() {
        return false;
    }
    
    public int getAuthMode(final Long erid) {
        final JSONObject enrollmentRequestProperties = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestProperties(erid);
        try {
            if (String.valueOf(enrollmentRequestProperties.get("Resource.DOMAIN_NETBIOS_NAME")).equalsIgnoreCase("MDM")) {
                return 1;
            }
            return EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(erid)).getInt("AUTH_MODE");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    protected void addOrUpdateDirectoryAuthenticationSettings(final JSONObject requestJSON) throws JSONException {
    }
}
