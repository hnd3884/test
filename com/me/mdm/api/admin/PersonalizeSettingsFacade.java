package com.me.mdm.api.admin;

import java.util.Map;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;

public class PersonalizeSettingsFacade
{
    public JSONObject getPersonalizeSettings() {
        return MDMApiFactoryProvider.getPersonalizationAPI().getPersonalizeSettings();
    }
    
    public JSONObject updatePersonalisationSettings(final JSONObject toJSONObject) throws JSONException {
        final JSONObject requestJSON = toJSONObject.getJSONObject("msg_body");
        final Long userID = APIUtil.getUserID(requestJSON);
        requestJSON.put("user_id", (Object)userID);
        return MDMApiFactoryProvider.getPersonalizationAPI().updatePersonalisationSettings(requestJSON);
    }
    
    public JSONObject getPersonalizationPickList() throws JSONException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("time_zone", (Object)APIUtil.getJSONObjectFromMap(SyMUtil.getAvailableTimeZone()));
        responseJSON.put("time_format", (Object)APIUtil.getJSONObjectFromProperties(SyMUtil.getTimeProperties()));
        responseJSON.put("locales", (Object)APIUtil.getJSONObjectFromProperties(SyMUtil.getLocalesProperties()));
        return responseJSON;
    }
}
