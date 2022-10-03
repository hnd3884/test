package com.me.mdm.api.command.container;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.command.CommandFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.APIEndpointStratergy;
import com.me.mdm.api.command.CommandWrapper;
import com.me.mdm.api.command.CommandAPIStratergy;
import com.me.mdm.api.ApiRequestHandler;

public class ContainerCommandAPIRequestHandler extends ApiRequestHandler
{
    public ContainerCommandAPIRequestHandler() {
        super(new CommandAPIStratergy(new ContainerCommandWrapper()));
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            new CommandFacade().executeContainerCommand(new ContainerCommandWrapper().toJSONWithCommand(apiRequest));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException(500, "COM0005", new Object[0]);
        }
    }
}
