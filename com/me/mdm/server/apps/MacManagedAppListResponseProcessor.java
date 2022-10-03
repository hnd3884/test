package com.me.mdm.server.apps;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.dd.plist.NSDictionary;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class MacManagedAppListResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final JSONObject response = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        try {
            final String strData = this.convertMacInstalledListAsManagedList(params.get("strData").toString());
            params.put("strData", (Object)strData);
            new IOSManagedAppListResponseProcessor().processQueuedCommand(params);
        }
        catch (final Exception e) {
            MacManagedAppListResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing seq response for managed app list for resource:" + String.valueOf(n));
        }
        return response;
    }
    
    private String convertMacInstalledListAsManagedList(final String strData) {
        final NSDictionary dict = new NSDictionary();
        dict.put("CommandUUID", (Object)"ManagedApplicationList");
        final NSDictionary managedAppsDict = new NSDictionary();
        final NSArray installedAppsArray = PlistWrapper.getInstance().getArrayForKey("InstalledApplicationList", strData);
        for (int i = 0; i < installedAppsArray.count(); ++i) {
            final NSDictionary appsDict = (NSDictionary)installedAppsArray.objectAtIndex(i);
            final NSString identifier = (NSString)appsDict.get((Object)"Identifier");
            if (identifier != null) {
                final NSObject isInstallingApp = appsDict.containsKey("Installing") ? appsDict.get((Object)"Installing") : null;
                String managed = "Managed";
                if (isInstallingApp != null && isInstallingApp.toString().equalsIgnoreCase("true")) {
                    managed = "Installing";
                }
                final NSDictionary statusDict = new NSDictionary();
                statusDict.put("Status", (Object)managed);
                managedAppsDict.put(identifier.toString(), (NSObject)statusDict);
                MacManagedAppListResponseProcessor.logger.log(Level.INFO, "convertMacInstalledListAsManagedList() {0} {1}", new Object[] { identifier, managed });
            }
        }
        dict.put("ManagedApplicationList", (NSObject)managedAppsDict);
        return dict.toXMLPropertyList().toString();
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static {
        MacManagedAppListResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
