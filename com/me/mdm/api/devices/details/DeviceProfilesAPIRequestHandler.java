package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.mdm.server.device.DeviceFacade;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceProfilesAPIRequestHandler extends ApiRequestHandler
{
    private ProfileFacade profile;
    
    public DeviceProfilesAPIRequestHandler() {
        this.profile = null;
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profile.associateProfilesToDevices(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while associating profiles to device", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profile.disassociateProfilesToDevices(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while disassociating profile from device", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new DeviceFacade().getDeviceProfiles(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while getting details profile associated to device", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
