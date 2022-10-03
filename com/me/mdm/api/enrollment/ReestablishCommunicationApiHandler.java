package com.me.mdm.api.enrollment;

import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ReestablishCommunicationApiHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public ReestablishCommunicationApiHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject body = apiRequest.toJSONObject().getJSONObject("msg_body");
            final Long erid = Long.valueOf(body.get("erid").toString());
            try {
                new EnrollmentFacade().validateEridInput(erid, APIUtil.getCustomerID(apiRequest.toJSONObject()));
            }
            catch (final APIHTTPException ex) {
                final String errorCode = ex.toJSONObject().get("error_code").toString();
                if (errorCode.equals("ENR00101")) {
                    throw new APIHTTPException("COM0008", new Object[0]);
                }
            }
            MDMiOSEntrollmentUtil.getInstance().addReenrollReq(erid);
            this.logger.log(Level.INFO, "ReestablishCommunication request successfully added..");
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject().put("success", true));
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in ReestablishCommunicationApiHandler.. ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
