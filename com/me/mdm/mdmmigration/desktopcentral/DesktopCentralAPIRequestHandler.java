package com.me.mdm.mdmmigration.desktopcentral;

import com.me.mdm.mdmmigration.MigrationConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.mdmmigration.mecloud.MECloudAPIRequestHandler;

public class DesktopCentralAPIRequestHandler extends MECloudAPIRequestHandler
{
    @Override
    protected String getNewEnrollmentURL() throws Exception {
        final Object newEnrollmentURL = new JSONObject(this.apiDetails.get("APIServiceConfiguration").toString()).opt("NEW_ENROLLMENT_URL".toLowerCase());
        return newEnrollmentURL.toString();
    }
    
    @Override
    protected JSONObject getNewServerAuthDetails() throws Exception {
        JSONObject authDetails = this.getAuthDetailsForAuthType(8, null);
        if (authDetails != null && authDetails.length() == 0) {
            authDetails = this.getAuthDetailsForAuthType(7, null);
        }
        return authDetails;
    }
    
    @Override
    public void getAuthorization(final boolean getNewKey) throws Exception {
        JSONObject authInfoObject = this.getAuthDetailsForAuthType(7, null);
        if (authInfoObject == null) {
            authInfoObject = this.getAuthDetailsForAuthType(8, 1);
        }
        final String authorizationHeader = this.getMDMAuthorizationHeader(authInfoObject, getNewKey);
        this.headerObject.put("Authorization", (Object)authorizationHeader);
    }
    
    @Override
    public JSONArray configurationDetails() {
        final JSONArray response = new JSONArray();
        final JSONArray requirements = MigrationConstants.MEOnPremiseRequirements.requirements;
        for (int i = 0; i < requirements.length(); ++i) {
            final JSONObject object = new JSONObject();
            object.put("display_name", (Object)requirements.get(i).toString());
            if (requirements.getString(i).equalsIgnoreCase("API Key")) {
                object.put("api_key", (Object)"AUTHORIZATION_HEADER".toLowerCase());
            }
            else if (requirements.getString(i).equalsIgnoreCase("Server URL")) {
                object.put("api_key", (Object)"Server_URL");
            }
            object.put("type", (Object)"String");
            response.put((Object)object);
        }
        return response;
    }
}
