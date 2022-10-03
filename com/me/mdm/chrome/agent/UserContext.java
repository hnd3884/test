package com.me.mdm.chrome.agent;

import org.json.JSONException;
import org.json.JSONObject;

public class UserContext extends Context
{
    public UserContext(final String udid, final JSONObject esaDetails) throws JSONException, Exception {
        super(udid, esaDetails);
    }
    
    @Override
    public String getCMPAEnterpriseAndUDID() {
        return "enterprises/" + this.getEnterpriseId() + "/users/" + this.getUdid();
    }
}
