package com.me.mdm.api.core.profiles;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;

public class ProfilesWrapper
{
    public static Long getCollectionForProfile(final APIRequest apiRequest) throws APIHTTPException, DataAccessException {
        try {
            final JSONObject message = apiRequest.toJSONObject();
            if (message.getJSONObject("msg_header").getJSONObject("resource_identifier").has("profile_id")) {
                final Long profileId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "profile_id", Long.valueOf(0L));
                final String pathInfo = apiRequest.pathInfo.toLowerCase();
                if (!pathInfo.contains("/versions/")) {
                    return ProfileHandler.getRecentProfileCollectionID(profileId);
                }
                String version = pathInfo.substring(pathInfo.indexOf("/versions/") + "/versions/".length());
                version = version.substring(0, version.indexOf("/"));
                if (!isLong(version)) {
                    throw new APIHTTPException("COM0008", new Object[] { "version - " + version });
                }
                final DataObject DO = MDMUtil.getPersistence().get("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"), (Object)Long.valueOf(version), 0).and(new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileId, 0)));
                if (DO.isEmpty()) {
                    throw new APIHTTPException("COM0008", new Object[] { "version - " + version });
                }
                return (Long)DO.getValue("ProfileToCollection", "COLLECTION_ID", (Criteria)null);
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(ProfilesWrapper.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return null;
    }
    
    public static JSONObject toJSONWithCollectionID(final APIRequest apiRequest) throws APIHTTPException, DataAccessException, JSONException {
        final Long collectionID = getCollectionForProfile(apiRequest);
        final JSONObject requestJSON = apiRequest.toJSONObject();
        if (collectionID != null) {
            final JSONObject headerJSON = requestJSON.getJSONObject("msg_header");
            final JSONObject idJSON = headerJSON.getJSONObject("resource_identifier");
            idJSON.put("collection_id", (Object)collectionID);
            headerJSON.put("resource_identifier", (Object)idJSON);
            requestJSON.put("msg_header", (Object)headerJSON);
        }
        return requestJSON;
    }
    
    private static boolean isLong(final String longString) {
        try {
            Long.valueOf(longString);
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
}
