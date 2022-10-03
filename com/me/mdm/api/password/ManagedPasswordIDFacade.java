package com.me.mdm.api.password;

import org.json.JSONException;
import com.me.mdm.server.security.passcode.MDMManagedPasswordHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ManagedPasswordIDFacade
{
    private Logger logger;
    
    public ManagedPasswordIDFacade() {
        this.logger = Logger.getLogger("MDMDeviceSecurityLogger");
    }
    
    public JSONObject createMDMManagedPasswordID(final JSONObject messageJSON) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            if (!messageJSON.has("msg_body")) {
                this.logger.log(Level.SEVERE, " -- createMDMManagedPasswordID() >   Error : No message body ");
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject requestJSON = messageJSON.getJSONObject("msg_body");
            if (requestJSON.length() == 0) {
                this.logger.log(Level.SEVERE, " -- createMDMManagedPasswordID() >   Error : Empty JSON body");
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long customerId = APIUtil.getCustomerID(messageJSON);
            final Long usedID = APIUtil.getUserID(messageJSON);
            final String passwordStr = String.valueOf(requestJSON.get("managed_password"));
            final Long passwordID = MDMManagedPasswordHandler.getMDMManagedPasswordID(passwordStr, customerId, usedID);
            this.logger.log(Level.FINEST, " Managed Password ID created by {0} passwordID {1}", new Object[] { usedID, passwordID });
            responseJSON.put("managed_password_id", (Object)passwordID);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "ManagedPasswordIDFacade Exception while doing JSONOperation", (Throwable)ex);
            return responseJSON;
        }
    }
}
