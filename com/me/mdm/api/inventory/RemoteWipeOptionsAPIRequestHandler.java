package com.me.mdm.api.inventory;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.command.CommandFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class RemoteWipeOptionsAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    private CommandFacade commandFacade;
    
    public RemoteWipeOptionsAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.commandFacade = new CommandFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.commandFacade.saveRemoteWipeOptions(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "doPost()", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
