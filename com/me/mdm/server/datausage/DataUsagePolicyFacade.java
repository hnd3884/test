package com.me.mdm.server.datausage;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.payload.PayloadException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.UUID;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.ProfileFacade;

public class DataUsagePolicyFacade extends ProfileFacade
{
    public static Logger logger;
    
    @Override
    protected int getProfileType() {
        return 8;
    }
    
    public JSONObject createDataUsageProfile(final JSONObject message) throws Exception {
        try {
            if (!message.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject requestJSON = message.getJSONObject("msg_body");
            if (requestJSON.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject json = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(message);
            final Long userId = APIUtil.getUserID(message);
            final Integer platformType = 0;
            json.put("PROFILE_DESCRIPTION", (Object)requestJSON.optString("profile_description", ""));
            if (new ProfileHandler().checkProfileNameExist(customerId, String.valueOf(requestJSON.get("profile_name")), 1, null)) {
                throw new APIHTTPException("COM0010", new Object[] { "profile_name - " + requestJSON.get("profile_name") });
            }
            json.put("PROFILE_NAME", (Object)String.valueOf(requestJSON.get("profile_name")));
            json.put("PROFILE_TYPE", 8);
            json.put("PLATFORM_TYPE", (Object)platformType);
            json.put("SCOPE", requestJSON.optInt("scope", 0));
            json.put("CUSTOMER_ID", (Object)customerId);
            json.put("CREATED_BY", (Object)userId);
            json.put("LAST_MODIFIED_BY", (Object)userId);
            json.put("SECURITY_TYPE", -1);
            String identifier = requestJSON.optString("profile_payload_identifier", (String)null);
            if (identifier == null) {
                identifier = "com.mdm.datausage." + UUID.randomUUID().toString() + "." + String.valueOf(requestJSON.get("profile_name")).replaceAll("[^a-zA-Z0-9]", "");
            }
            json.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)identifier);
            json.put("SCOPE", requestJSON.optInt("scope", 0));
            MDMUtil.getUserTransaction().begin();
            ProfileConfigHandler.addProfileCollection(json);
            MDMUtil.getUserTransaction().commit();
            final JSONObject idJSON = new JSONObject();
            idJSON.put("profile_id", (Object)JSONUtil.optLongForUVH(json, "PROFILE_ID", (Long)null));
            final JSONObject headerJSON = new JSONObject();
            headerJSON.put("filters", (Object)message.getJSONObject("msg_header").getJSONObject("filters"));
            headerJSON.put("resource_identifier", (Object)idJSON);
            final JSONObject messageJSON = new JSONObject();
            final JSONObject filterJson = new JSONObject();
            filterJson.put("customer_id", (Object)customerId);
            headerJSON.put("filters", (Object)filterJson);
            messageJSON.put("msg_header", (Object)headerJSON);
            return idJSON;
        }
        catch (final PayloadException ex) {
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception e) {
                DataUsagePolicyFacade.logger.log(Level.SEVERE, "transaction rollback failed....", e);
            }
            throw ex;
        }
        catch (final Exception e2) {
            try {
                SyMUtil.getUserTransaction().rollback();
            }
            catch (final Exception ex2) {
                DataUsagePolicyFacade.logger.log(Level.SEVERE, "transaction rollback failed....", ex2);
            }
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            DataUsagePolicyFacade.logger.log(Level.SEVERE, "error in createDataProfile()...", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        DataUsagePolicyFacade.logger = Logger.getLogger("MDMConfigLogger");
    }
}
