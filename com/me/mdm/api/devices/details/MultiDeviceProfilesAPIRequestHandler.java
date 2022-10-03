package com.me.mdm.api.devices.details;

import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MultiDeviceProfilesAPIRequestHandler extends ApiRequestHandler
{
    private ProfileFacade profile;
    
    public MultiDeviceProfilesAPIRequestHandler() {
        this.profile = null;
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = ProfilesWrapper.toJSONWithCollectionID(apiRequest);
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("device_id");
            this.profile.associateProfilesToDevices(request);
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in POST /devices/profiles", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
