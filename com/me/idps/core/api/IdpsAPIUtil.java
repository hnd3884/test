package com.me.idps.core.api;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.IdpsJSONutil;
import org.json.JSONObject;

public class IdpsAPIUtil
{
    public static Long getResourceID(final JSONObject apiRequest, final String resourceKey) throws JSONException {
        try {
            return IdpsJSONutil.optLongForUVH(apiRequest.getJSONObject("msg_header").getJSONObject("resource_identifier"), resourceKey, -1L);
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception  in getResourceID", e);
            return -1L;
        }
    }
    
    public static Long getCustomerID(final JSONObject apiRequest) {
        try {
            if (apiRequest.getJSONObject("msg_header").getJSONObject("filters").has("customer_id")) {
                return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("customer_id");
            }
            throw new IdpsAPIException("COM0022");
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in getCustomerID()", (Throwable)e);
            return -1L;
        }
    }
    
    public static Long getUserID(final JSONObject apiRequest) {
        try {
            return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("user_id");
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in getUserID()", (Throwable)e);
            return -1L;
        }
    }
    
    public static String getUserName(final JSONObject apiRequest) {
        try {
            return String.valueOf(apiRequest.getJSONObject("msg_header").getJSONObject("filters").get("user_name"));
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in getUserName()", (Throwable)e);
            return null;
        }
    }
    
    public static Long getLoginID(final JSONObject apiRequest) {
        try {
            return apiRequest.getJSONObject("msg_header").getJSONObject("filters").getLong("login_id");
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in getLoginID()", (Throwable)e);
            return -1L;
        }
    }
    
    public static String getStringFilter(final JSONObject request, final String key) {
        try {
            return request.getJSONObject("msg_header").getJSONObject("filters").optString(key, (String)null);
        }
        catch (final JSONException e) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception while getting filter", (Throwable)e);
            return null;
        }
    }
}
