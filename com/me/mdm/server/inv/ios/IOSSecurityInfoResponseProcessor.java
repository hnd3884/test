package com.me.mdm.server.inv.ios;

import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSSecurityInfoResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final Long resourceID = params.optLong("resourceId");
            final Long customerID = params.optLong("customerId");
            final String udid = params.optString("strUDID");
            final String strData = params.optString("strData");
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("SecurityInfo", strData);
            HashMap hsmap = new HashMap();
            hsmap = PlistWrapper.getInstance().getHashFromDict(nsDict);
            hsmap.put("DeviceRooted", Boolean.toString(Boolean.FALSE));
            hsmap.put("StorageEncryption", Boolean.toString(Boolean.TRUE));
            Logger.getLogger("MDMLogger").log(Level.FINE, "ProcessData() -- SecurityInfo ->{0}", hsmap);
            hsmap.put("UDID", udid);
            hsmap.put("CUSTOMER_ID", customerID);
            MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, hsmap);
            new IOSPasscodeComplianceHandler().checkDevicePasscodeCompliance(resourceID);
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception while processing security info command", e);
        }
        return null;
    }
}
