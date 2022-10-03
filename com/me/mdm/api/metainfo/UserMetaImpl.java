package com.me.mdm.api.metainfo;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;

public class UserMetaImpl implements UserMetaAPI
{
    @Override
    public JSONObject getUserMeta() {
        try {
            final JSONObject result = new JSONObject();
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            result.put(MetaConstants.USER_ID, (Object)userID);
            result.put(MetaConstants.LOGIN_ID, (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID());
            result.put(MetaConstants.USER_NAME, (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName());
            result.put("email_id", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getEmailForUser(userID));
            result.put(MetaConstants.LANGUAGE_CODE, (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale().getLanguage());
            result.put(MetaConstants.COUNTRY, (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale().getCountry());
            result.put(MetaConstants.TIMEZONE, (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
            result.put(MetaConstants.CUSTOMER_ID, (Object)this.getCustomerList());
            result.put(MetaConstants.ROLES_MAPPED, (Object)this.getRoles());
            result.put(MetaConstants.TIME_FORMAT, (Object)DMUserHandler.getUserTimeFormat());
            return result;
        }
        catch (final JSONException ex) {
            Logger.getLogger("UserMetaImpl").log(Level.SEVERE, "Exception in UserMetaAPIRequestHandler", (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger("UserMetaImpl").log(Level.SEVERE, "Exception in UserMetaAPIRequestHandler", ex2);
        }
        return null;
    }
    
    private JSONArray getCustomerList() {
        try {
            final JSONArray customerArray = new JSONArray();
            if (CustomerInfoUtil.getInstance().isMSP()) {
                final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final ArrayList customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
                for (final HashMap customerMap : customerList) {
                    customerArray.put((Object)customerMap.get("CUSTOMER_ID").toString());
                }
            }
            else {
                customerArray.put((Object)CustomerInfoUtil.getInstance().getDefaultCustomer().toString());
            }
            return customerArray;
        }
        catch (final Exception ex) {
            Logger.getLogger("UserMetaImpl").log(Level.SEVERE, "Exception in UserMetaAPIRequestHandler", ex);
            return null;
        }
    }
    
    private JSONArray getRoles() {
        try {
            final JSONArray role_array = new JSONArray();
            for (final String role : ApiFactoryProvider.getAuthUtilAccessAPI().getRoles()) {
                role_array.put((Object)role);
            }
            return role_array;
        }
        catch (final Exception ex) {
            Logger.getLogger("UserMetaImpl").log(Level.SEVERE, "Exception in UserMetaAPIRequestHandler", ex);
            return null;
        }
    }
}
