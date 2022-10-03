package com.me.mdm.api.core.profiles.distribution;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfilesToGroupsDistributionAPIRequestHandler extends ApiRequestHandler
{
    private ProfileFacade profile;
    
    public ProfilesToGroupsDistributionAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profile.associateProfilesToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while associating profile to groups", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profile.disassociateProfilesToGroups(ProfilesWrapper.toJSONWithCollectionID(apiRequest));
            return JSONUtil.toJSON("status", 204);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while disassociating profile to the group", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.profile.getGroupDistributionDetails(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return responseJSON;
        }
        catch (final JSONException | DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
