package com.me.mdm.api.core.profiles.config;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfileTrashAPIRequestHandler extends ApiRequestHandler
{
    private ProfileFacade profile;
    
    public ProfileTrashAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject message = apiRequest.toJSONObject();
            message.put("move_to_trash", true);
            this.profile.deleteOrTrashProfile(message);
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception while moving the profile to trash", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while moving the profile to trash", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
