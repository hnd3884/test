package com.me.mdm.core.windows.commands;

import java.util.Iterator;
import java.util.UUID;
import org.json.JSONException;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.DeleteRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WinBlacklistAppCommand
{
    private Logger logger;
    
    public WinBlacklistAppCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        try {
            final HashMap hashMap = (HashMap)cmdParams.get("data");
            final String command = (String)cmdParams.get("requestType");
            final HashMap appxHash = hashMap.get("appx");
            final List enabledList = appxHash.get("enabled");
            SyncMLRequestCommand replaceRequestCommand = null;
            final AtomicRequestCommand atomicRequestCommand = new AtomicRequestCommand();
            atomicRequestCommand.setRequestCmdId(command);
            if (enabledList.size() > 0) {
                replaceRequestCommand = new ReplaceRequestCommand();
                final String data = this.getBlacklistData(hashMap);
                replaceRequestCommand.addRequestItem(this.createTargetItemTagElement("./Vendor/MSFT/AppLocker/ApplicationLaunchRestrictions/" + command + "/StoreApps/Policy", data));
            }
            else {
                replaceRequestCommand = new DeleteRequestCommand();
                replaceRequestCommand.addRequestItem(this.createTargetItemTagElement("./Vendor/MSFT/AppLocker/ApplicationLaunchRestrictions/" + command + "/StoreApps/Policy", null));
            }
            replaceRequestCommand.setRequestCmdId(command);
            atomicRequestCommand.addRequestCmd(replaceRequestCommand);
            responseSyncML.getSyncBody().addRequestCmd(atomicRequestCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in generating command", e);
        }
    }
    
    private Item createTargetItemTagElement(final String locationUri, final String data) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        final Meta meta = new Meta();
        meta.setFormat("chr");
        item.setMeta(meta);
        if (data != null) {
            item.setData(data);
        }
        return item;
    }
    
    public JSONObject processResponse(final SyncMLMessage requestSyncML) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }
    
    private String getBlacklistData(final HashMap hashMap) throws JSONException {
        final String baseXML = "<RuleCollection Type=\"%type%\" EnforcementMode=\"%mode%\"><FilePublisherRule Id=\"%uuid%\" Name=\"Whitelist All\" Description=\"Allow Admins\"  UserOrGroupSid=\"S-1-1-0\" Action=\"Allow\"><Conditions><FilePublisherCondition PublisherName=\"*\" ProductName=\"*\" BinaryName=\"*\" /></Conditions></FilePublisherRule>%blacklisttule%</RuleCollection>";
        final String innerXML = "<FilePublisherRule Id=\"%uuid%\" Name=\"Blacklist App{0}\" Description=\"Allow Admins\"  UserOrGroupSid=\"S-1-1-0\" Action=\"Deny\"><Conditions><FilePublisherCondition PublisherName=\"%publishename%\" ProductName=\"%productname%\" BinaryName=\"%binaryname%\" /></Conditions></FilePublisherRule>";
        final HashMap appxHash = hashMap.get("appx");
        String finalXML = baseXML.replaceAll("%type%", "appx");
        final List enabledList = appxHash.get("enabled");
        finalXML = finalXML.replaceAll("%mode%", "enabled");
        final Iterator iterator = enabledList.iterator();
        final StringBuilder replacementXML = new StringBuilder();
        int i = 1;
        while (iterator.hasNext()) {
            final JSONObject jsonObject = iterator.next();
            replacementXML.append(innerXML.replaceAll("%publishename%", String.valueOf(jsonObject.get("publisherName"))).replaceAll("%binaryname%", String.valueOf(jsonObject.get("binaryName"))).replaceAll("%productname%", String.valueOf(jsonObject.get("productName"))).replaceAll("%app%", "" + i).replaceAll("%uuid%", UUID.randomUUID().toString()));
            ++i;
        }
        finalXML = finalXML.replaceAll("%blacklisttule%", replacementXML.toString()).replaceAll("%uuid%", UUID.randomUUID().toString());
        return finalXML;
    }
}
