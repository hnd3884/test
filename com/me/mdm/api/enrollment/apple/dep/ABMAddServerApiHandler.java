package com.me.mdm.api.enrollment.apple.dep;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.adep.ABMAuthTokenFacade;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ABMAddServerApiHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Long customerID = APIUtil.getCustomerID(apiRequest.toJSONObject());
        try {
            final JSONObject result = ABMAuthTokenFacade.getInstance().createTokenId(customerID);
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)result);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception in Add new ABM server..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Add new ABM server..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
