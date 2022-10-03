package com.me.mdm.api.core.certificate;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.ScepServerFacade;
import com.me.mdm.api.ApiRequestHandler;

public class SCEPServerAPIRequestHandler extends ApiRequestHandler
{
    ScepServerFacade facade;
    
    public SCEPServerAPIRequestHandler() {
        this.facade = new ScepServerFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject json = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(json);
            responseDetails.put("RESPONSE", (Object)this.facade.getScepServers(customerId, json));
            return responseDetails;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject json = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(json);
            final Long loginID = APIUtil.getLoginID(json);
            responseDetails.put("RESPONSE", (Object)this.facade.addScepServer(customerId, loginID, json));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Error during SCEP server add ", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[] { e2 });
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error during scep server add ", e3);
            throw new APIHTTPException("COM0004", new Object[] { e3 });
        }
    }
}
