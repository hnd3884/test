package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpUserInfoUpdateCommand
{
    private Logger logger;
    
    public WpUserInfoUpdateCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final JSONObject managedUserJson = jsonObject.getJSONObject("managedUserJson");
            final String commandName = (String)jsonObject.get("commandName");
            final AtomicRequestCommand updateUserInfoAtomicCmd = new AtomicRequestCommand();
            updateUserInfoAtomicCmd.setRequestCmdId(commandName);
            final ReplaceRequestCommand updateUserInfoReplaceCmd = new ReplaceRequestCommand();
            updateUserInfoReplaceCmd.setRequestCmdId(commandName);
            final ArrayList items = new ArrayList();
            final Boolean isWindowsPhone = jsonObject.optBoolean("isWindowsPhone", (boolean)Boolean.TRUE);
            if (isWindowsPhone) {
                items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/DeviceName", String.valueOf(jsonObject.get("ManagedDeviceExtn.NAME")), null));
            }
            final String dmClientBaseLocURI = "./Vendor/MSFT/DMClient/Provider/MEMDM";
            final String emailId = managedUserJson.optString("EMAIL_ADDRESS", (String)null);
            final int templateType = jsonObject.optInt("adminEnrollmentTemplateType", -1);
            if (!MDMStringUtils.isEmpty(emailId) && templateType != 32) {
                items.add(this.createTargetItemTagElement(dmClientBaseLocURI + "/UPN", emailId, null));
            }
            final Long managedUserId = managedUserJson.getLong("MANAGED_USER_ID");
            final String currentServerUrl = jsonObject.optString("TARGET", (String)null);
            if (currentServerUrl != null) {
                final String[] serverUrlSplit = currentServerUrl.split("muid=[0-9]*");
                if (serverUrlSplit.length == 2) {
                    final String newServerUrl = serverUrlSplit[0] + "muid=" + managedUserId + serverUrlSplit[1];
                    items.add(this.createTargetItemTagElement(dmClientBaseLocURI + "/ManagementServiceAddress", newServerUrl, null));
                }
            }
            updateUserInfoReplaceCmd.setRequestItems(items);
            updateUserInfoAtomicCmd.addRequestCmd(updateUserInfoReplaceCmd);
            responseSyncML.getSyncBody().addRequestCmd(updateUserInfoAtomicCmd);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final JSONException exp) {
            this.logger.log(Level.SEVERE, "Exception in wpUserInfoUpdateCommand.processRequestForDEPToken. ManagedUserJson not present {0}", (Throwable)exp);
        }
    }
    
    public Item createTargetItemTagElement(final String locationUri, final String itemData, final String sMetaFormat) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        item.setData(itemData);
        if (sMetaFormat != null) {
            final Meta meta = new Meta();
            meta.setFormat(sMetaFormat);
            item.setMeta(meta);
        }
        return item;
    }
}
