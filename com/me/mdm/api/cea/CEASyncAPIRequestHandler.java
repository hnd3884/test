package com.me.mdm.api.cea;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.easmanagement.CEAApiFacade;
import com.me.mdm.api.ApiRequestHandler;

public class CEASyncAPIRequestHandler extends ApiRequestHandler
{
    private static CEAApiFacade ceaApiFacade;
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            CEASyncAPIRequestHandler.ceaApiFacade.syncCEAServer(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    static {
        CEASyncAPIRequestHandler.ceaApiFacade = new CEAApiFacade();
    }
}
