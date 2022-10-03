package com.adventnet.sym.server.mdm.gdpr;

import java.util.Map;
import com.me.mdm.webclient.home.MDMHomePageUtils;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class GDPRSettingsFacade
{
    public Logger logger;
    
    public GDPRSettingsFacade() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject getServerPrivacyDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject result = new JSONObject();
        final JSONObject securityjson = GDPRSettingsUtil.getInstance().setSecuritySettingsData(APIUtil.getCustomerID(jsonObject));
        result.put("security_status", securityjson.getInt("SecuritySetting"));
        final JSONObject integrationjson = GDPRSettingsUtil.getInstance().setIntegrationSettingsData();
        if (integrationjson.opt("IntegrationSettings") != null) {
            result.put("integration_settings_status", integrationjson.get("IntegrationSettings"));
        }
        if (integrationjson.opt("IntegrationSettingsData") != null) {
            final JSONArray integrationsettingsdata = (JSONArray)integrationjson.get("IntegrationSettingsData");
            final int size = integrationsettingsdata.length();
            final JSONArray resultdata = new JSONArray();
            for (int i = 0; i < size; ++i) {
                final JSONObject app = integrationsettingsdata.getJSONObject(i);
                app.put("url_link", (Object)app.get("URL_LINK"));
                app.put("app_name", (Object)app.get("APPNAME"));
                app.remove("APPNAME");
                resultdata.put((Object)app);
            }
            if (size > 0) {
                result.put("integration_settings_data", (Object)resultdata);
            }
        }
        return result;
    }
    
    public JSONObject saveServerPrivacySettings(final JSONObject jsonObject) throws Exception {
        final JSONObject result = new JSONObject();
        final Long customerid = APIUtil.getCustomerID(jsonObject);
        final Long userId = APIUtil.getUserID(jsonObject);
        final String userName = DMUserHandler.getUserNameFromUserID(userId);
        final JSONObject body = jsonObject.getJSONObject("msg_body");
        final JSONObject data = new JSONObject();
        data.put("id", body.optLong("id"));
        data.put("status", body.optBoolean("status"));
        data.put("isDelete", body.optBoolean("is_delete"));
        boolean isError = false;
        final JSONArray consentList = new JSONArray();
        consentList.put((Object)data);
        final JSONObject json = new JSONObject();
        json.put("consentList", (Object)consentList);
        json.put("userName", (Object)userName);
        json.put("userId", (Object)userId);
        json.put("customerId", (Object)customerid);
        isError = GDPRSettingsUtil.getInstance().saveConsentList(json);
        result.put("success", !isError);
        return result;
    }
    
    public JSONObject getSecuritySettings(final JSONObject jsonObject) throws Exception {
        final Long customerId = APIUtil.getCustomerID(jsonObject);
        JSONObject result = new JSONObject();
        final JSONObject json = GDPRSettingsUtil.getInstance().setSecuritySettingsData(customerId);
        final Map data = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getSecureSettings(customerId);
        result = JSONUtil.mapToJSON(data);
        result.remove("certificateDetails");
        result.put("gdpr_widget_enabled", new MDMHomePageUtils().getGdprWidgetProps().get("showGdprWidget"));
        return result;
    }
    
    public JSONObject saveSecuritySettings(final JSONObject jsonObject) throws Exception {
        final JSONObject responseJSON = GDPRSettingsUtil.getInstance().saveSecuritySettings(jsonObject.getJSONObject("msg_header").getJSONObject("filters"));
        if (responseJSON.getInt("STATUS_CODE") == 0) {
            responseJSON.put("success", true);
            responseJSON.remove("STATUS_CODE");
        }
        return responseJSON;
    }
    
    public JSONObject toggleGdprWidget(final JSONObject jsonObject) throws Exception {
        final JSONObject result = new JSONObject();
        final boolean showWidget = jsonObject.getJSONObject("msg_header").getJSONObject("filters").optBoolean("gdpr_widget_enabled", false);
        if (GDPRSettingsUtil.getInstance().updatetoggleGdprWidgetParamater(String.valueOf(showWidget), APIUtil.getCustomerID(jsonObject))) {
            result.put("success", true);
            result.put("gdpr_widget_enabled", showWidget);
        }
        return result;
    }
}
