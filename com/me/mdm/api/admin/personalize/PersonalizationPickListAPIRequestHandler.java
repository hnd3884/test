package com.me.mdm.api.admin.personalize;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.admin.PersonalizeSettingsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PersonalizationPickListAPIRequestHandler extends ApiRequestHandler
{
    private PersonalizeSettingsFacade personalizeSettingsFacade;
    
    public PersonalizationPickListAPIRequestHandler() {
        this.personalizeSettingsFacade = new PersonalizeSettingsFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.personalizeSettingsFacade.getPersonalizationPickList());
            return responseJSON;
        }
        catch (final JSONException ex) {
            Logger.getLogger("UserManagementLogger").log(Level.SEVERE, "Error getting picklist for personalization settings    ", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
