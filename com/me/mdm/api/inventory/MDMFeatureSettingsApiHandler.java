package com.me.mdm.api.inventory;

import java.util.List;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsFacade;
import java.util.Collection;
import com.me.mdm.server.customgroup.GroupFacade;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.role.RBDAUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MDMFeatureSettingsApiHandler extends ApiRequestHandler
{
    public static Logger logger;
    
    @Override
    public Object doGet(final APIRequest apiRequest) {
        try {
            if (apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").has(FeatureSettingConstants.Api.Key.feature_type)) {
                final int feature_type = apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").getInt(FeatureSettingConstants.Api.Key.feature_type);
                final JSONObject response = new JSONObject();
                response.put("status", 200);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(apiRequest.toJSONObject()));
                jsonObject.put(FeatureSettingConstants.Api.Key.feature_type, feature_type);
                response.put("RESPONSE", (Object)MDMFeatureSettingsHandler.getSettings(jsonObject));
                return response;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e) {
            MDMFeatureSettingsApiHandler.logger.log(Level.SEVERE, "Exception while fetching feature details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) {
        try {
            final long loginId = APIUtil.getLoginID(apiRequest.toJSONObject());
            if (!RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
                throw new APIHTTPException("BATTERY001", new Object[0]);
            }
            final JSONObject msgBody = apiRequest.toJSONObject().getJSONObject("msg_body");
            msgBody.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(apiRequest.toJSONObject()));
            msgBody.put("user_id", (Object)APIUtil.getUserID(apiRequest.toJSONObject()));
            if (msgBody.getBoolean(FeatureSettingConstants.Api.Key.is_enabled)) {
                final List groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(msgBody.getJSONArray("groups"));
                if (groupList != null && !groupList.isEmpty()) {
                    new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(apiRequest.toJSONObject()));
                    MDMFeatureSettingsApiHandler.logger.log(Level.INFO, "Groups validated successfully");
                }
            }
            MDMFeatureSettingsFacade.configureSettings(msgBody);
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            return response;
        }
        catch (final APIHTTPException ex) {
            MDMFeatureSettingsApiHandler.logger.log(Level.WARNING, "Exception while adding battery settings", ex);
            throw ex;
        }
        catch (final Exception e) {
            MDMFeatureSettingsApiHandler.logger.log(Level.WARNING, "Exception while adding battery settings", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        MDMFeatureSettingsApiHandler.logger = Logger.getLogger(MDMFeatureSettingsApiHandler.class.getName());
    }
}
