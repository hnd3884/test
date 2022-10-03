package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpRemoteLocateCommand
{
    private String baseLocationURI;
    
    public WpRemoteLocateCommand() {
        this.baseLocationURI = "./Vendor/MSFT/RemoteFind/";
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject deviceDetails) {
        final ReplaceRequestCommand remoteFindSettings = this.getRemoteFindSettings();
        remoteFindSettings.setRequestCmdId("GetLocation");
        final GetRequestCommand remoteLocateFetchCommand = this.getRemoteLocateCommands();
        remoteLocateFetchCommand.setRequestCmdId("GetLocation");
        final SequenceRequestCommand sequenceCommand = new SequenceRequestCommand();
        sequenceCommand.setRequestCmdId("GetLocation");
        sequenceCommand.addRequestCmd(remoteLocateFetchCommand);
        final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
        atomicCommand.setRequestCmdId("GetLocation");
        atomicCommand.addRequestCmd(sequenceCommand);
        responseSyncML.getSyncBody().addRequestCmd(remoteFindSettings);
        responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
    }
    
    private GetRequestCommand getRemoteLocateCommands() {
        final GetRequestCommand remoteLocateCommand = new GetRequestCommand();
        final String baseLocationGetURI = this.baseLocationURI + "Location/";
        final Item latitude = this.createCommandItemTagElement(baseLocationGetURI + "Latitude", null);
        final Item longitude = this.createCommandItemTagElement(baseLocationGetURI + "Longitude", null);
        final Item altitude = this.createCommandItemTagElement(baseLocationGetURI + "Altitude", null);
        final Item accuracy = this.createCommandItemTagElement(baseLocationGetURI + "Accuracy", null);
        final Item altitudeAccuracy = this.createCommandItemTagElement(baseLocationGetURI + "AltitudeAccuracy", null);
        final Item age = this.createCommandItemTagElement(baseLocationGetURI + "Age", null);
        remoteLocateCommand.addRequestItem(latitude);
        remoteLocateCommand.addRequestItem(longitude);
        remoteLocateCommand.addRequestItem(altitude);
        remoteLocateCommand.addRequestItem(accuracy);
        remoteLocateCommand.addRequestItem(altitudeAccuracy);
        remoteLocateCommand.addRequestItem(age);
        return remoteLocateCommand;
    }
    
    private ReplaceRequestCommand getRemoteFindSettings() {
        final ReplaceRequestCommand remoteFindSettings = new ReplaceRequestCommand();
        final Item desiredAccuracy = this.createCommandItemTagElement(this.baseLocationURI + "DesiredAccuracy", "1");
        final Item maxAge = this.createCommandItemTagElement(this.baseLocationURI + "MaximumAge", "60");
        final Item timeOut = this.createCommandItemTagElement(this.baseLocationURI + "Timeout", "60");
        remoteFindSettings.addRequestItem(desiredAccuracy);
        remoteFindSettings.addRequestItem(maxAge);
        remoteFindSettings.addRequestItem(timeOut);
        return remoteFindSettings;
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        commandItem.setData(sValue);
        return commandItem;
    }
}
