package com.me.mdm.api.admin.personalize;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.admin.PersonalizeSettingsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PersonalizeSettingsAPIRequestHandler extends ApiRequestHandler
{
    private PersonalizeSettingsFacade personalizeSettingsFacade;
    
    public PersonalizeSettingsAPIRequestHandler() {
        this.personalizeSettingsFacade = new PersonalizeSettingsFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.personalizeSettingsFacade.updatePersonalisationSettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.personalizeSettingsFacade.getPersonalizeSettings());
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
