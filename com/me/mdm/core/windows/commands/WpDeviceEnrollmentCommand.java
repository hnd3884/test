package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpDeviceEnrollmentCommand
{
    Logger logger;
    
    public WpDeviceEnrollmentCommand() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final GetRequestCommand devInfoGet = new GetRequestCommand();
            devInfoGet.setRequestCmdId("Enrollment");
            final ArrayList items = new ArrayList();
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/DNSComputerName"));
            items.add(this.createTargetItemTagElement("./DevDetail/SwV"));
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/ExchangeID"));
            items.add(this.createTargetItemTagElement("./DevInfo/Man"));
            items.add(this.createTargetItemTagElement("./DevInfo/Mod"));
            items.add(this.createTargetItemTagElement("./DevDetail/DevTyp"));
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMEI"));
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/IMEI"));
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/OSPlatform"));
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/SMBIOSSerialNumber"));
            devInfoGet.setRequestItems(items);
            responseSyncML.getSyncBody().addRequestCmd(devInfoGet);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, " Exception in processRequestForDEPToken ", exp);
        }
    }
    
    Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
}
