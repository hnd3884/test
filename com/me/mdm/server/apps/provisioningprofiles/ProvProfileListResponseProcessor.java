package com.me.mdm.server.apps.provisioningprofiles;

import com.dd.plist.NSString;
import com.dd.plist.NSDate;
import com.dd.plist.NSDictionary;
import org.json.JSONArray;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.agent.util.ResponseTester;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.server.command.CommandResponseProcessor;

public class ProvProfileListResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            this.processResponse((Long)params.get("resourceId"), (String)params.get("strData"));
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while ProvProfileListResponseProcessor processQueuedCommand ", e);
        }
        return new JSONObject();
    }
    
    private void processResponse(final Long resourceID, String responseStr) {
        try {
            if (ResponseTester.isTestMode("ProvProfileListResponseTestMode")) {
                responseStr = ResponseTester.getTestResponseString("provprofiletestresponse.xml");
            }
            final String status = PlistWrapper.getInstance().getValueForKeyString("Status", responseStr);
            if (status != null && status.equalsIgnoreCase("Acknowledged")) {
                final NSArray array = PlistWrapper.getInstance().getArrayForKey("ProvisioningProfileList", responseStr);
                if (array == null || array.count() < 1) {
                    new DeviceProvProfilesDataHandler().clearInstalledProfiles(resourceID);
                }
                else {
                    final JSONArray jsonArray = this.parseProvisioningProfilesArray(resourceID, array);
                    final JSONArray jArrayWithIDs = new ProvisioningProfilesDataHandler().addOrUpdateProvisioningProfiles(jsonArray);
                    new DeviceProvProfilesDataHandler().clearAndUpdateInstalledProfiles(resourceID, jArrayWithIDs);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while ProvProfileListResponseProcessor ", e);
        }
    }
    
    private JSONArray parseProvisioningProfilesArray(final Long resourceID, final NSArray array) {
        final JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < array.count(); ++i) {
                final NSDictionary dict = (NSDictionary)array.objectAtIndex(i);
                final long expiryMillis = (dict.get((Object)"ExpiryDate") == null) ? -1L : ((NSDate)dict.get((Object)"ExpiryDate")).getDate().getTime();
                final String name = (dict.get((Object)"Name") == null) ? "App Provisioning Profile" : dict.get((Object)"Name").toString();
                final NSString uuid = (NSString)dict.get((Object)"UUID");
                if (uuid == null) {
                    Logger.getLogger("MDMLogger").log(Level.SEVERE, "Bad data. No UUID present for provisioning profile.. ignoring this ");
                }
                else {
                    final JSONObject json = new JSONObject();
                    json.put("PROV_UUID", (Object)uuid.toString());
                    json.put("PROV_NAME", (Object)name);
                    json.put("PROV_EXPIRY_DATE", expiryMillis);
                    json.put("RESOURCE_ID", (Object)resourceID);
                    json.put("INSTALLED_SOURCE", (Object)ProvisioningProfileConstants.SOURCE_UNKNOWN);
                    jsonArray.put((Object)json);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error while parseProvisioningProfilesArray ", e);
        }
        return jsonArray;
    }
}
