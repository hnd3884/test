package com.me.mdm.server.windows.notification;

import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpDeviceChannelUri
{
    private final Logger logger;
    
    public WpDeviceChannelUri() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public String getChannelUri(final SyncMLMessage syncMlMessage) {
        final List<SyncMLResponseCommand> responseCmds = syncMlMessage.getSyncBody().getResponseCmds();
        for (final SyncMLResponseCommand responseCmd : responseCmds) {
            if (responseCmd instanceof ResultsResponseCommand) {
                final List<Item> responseItems = responseCmd.getResponseItems();
                for (final Item responseItem : responseItems) {
                    if (responseItem.getSource().getLocUri().equalsIgnoreCase("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/ChannelURI")) {
                        return responseItem.getData().toString();
                    }
                }
            }
        }
        return null;
    }
    
    public Boolean updateChannelUri(final JSONObject jsonObj, final SyncMLMessage syncMlMessage) {
        Boolean retVal = Boolean.FALSE;
        try {
            final HashMap deviceDetails = new HashMap();
            final Long lResourceID = jsonObj.getLong("RESOURCE_ID");
            final List<SyncMLResponseCommand> responseCmds = syncMlMessage.getSyncBody().getResponseCmds();
            for (final SyncMLResponseCommand responseCmd : responseCmds) {
                if (responseCmd instanceof ResultsResponseCommand) {
                    final List<Item> responseItems = responseCmd.getResponseItems();
                    for (final Item responseItem : responseItems) {
                        deviceDetails.put(responseItem.getSource().getLocUri(), responseItem.getData());
                        retVal = Boolean.TRUE;
                    }
                }
            }
            if (retVal) {
                final String sChannelURI = deviceDetails.get("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/ChannelURI").toString();
                this.updateIosDeviceCommDetailsRow(lResourceID, 1, sChannelURI);
                this.logger.log(Level.INFO, "Windows MDM channelUri for resourceId {0} is {1}", new String[] { Long.toString(lResourceID), sChannelURI });
            }
            return retVal;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
        return retVal;
    }
    
    public Boolean updateNativeAppChannelUri(final String udid, final String channelUri) {
        Boolean retVal = Boolean.TRUE;
        try {
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            this.updateIosDeviceCommDetailsRow(resourceId, 2, channelUri);
            this.logger.log(Level.INFO, "Windows Native App channelUri for resourceId {0} is {1}", new String[] { Long.toString(resourceId), channelUri });
        }
        catch (final Exception ex) {
            retVal = Boolean.FALSE;
            this.logger.log(Level.SEVERE, "Exception in WpDeviceChannelUri.updateNativeAppChannelUri", ex);
        }
        return retVal;
    }
    
    private Boolean updateIosDeviceCommDetailsRow(final Long resourceID, final int updateChoice, final String sChannelUri) {
        Boolean isSuccessUpdate = Boolean.TRUE;
        try {
            int notificationType = 3;
            if (updateChoice == 1) {
                notificationType = 3;
            }
            else if (updateChoice == 2) {
                notificationType = 303;
            }
            final JSONObject properties = new JSONObject();
            properties.put("NOTIFICATION_TOKEN_ENCRYPTED", (Object)sChannelUri);
            PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceID, notificationType, properties);
        }
        catch (final Exception ex) {
            isSuccessUpdate = Boolean.FALSE;
            this.logger.log(Level.SEVERE, "Error in WpDeviceChannelUri.updateIosDeviceCommDetailsRow", ex);
        }
        return isSuccessUpdate;
    }
}
