package com.me.mdm.onpremise.server.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.webclient.admin.FirewallAndDCOMPortAction;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class EnrollmentFacadeOnPremise
{
    Logger logger;
    
    public EnrollmentFacadeOnPremise() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public JSONObject unblockPorts() throws APIHTTPException {
        try {
            final String firewallAndDCOMStatus = SyMUtil.getSyMParameter("FIREWALL_AND_DCOM_STATUS");
            boolean configureStatus = Boolean.FALSE;
            if (firewallAndDCOMStatus != null && !firewallAndDCOMStatus.equals("")) {
                configureStatus = new FirewallAndDCOMPortAction().configureFirewallAndDCOMSettings(firewallAndDCOMStatus);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("port_unblocked", configureStatus);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in unblockPorts : ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
