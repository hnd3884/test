package com.me.mdm.server.profiles.ios;

import java.util.Set;
import java.util.Iterator;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collection;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import java.util.HashSet;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ConfigProfileRemoveHandler
{
    private static final Logger LOGGER;
    
    public void removeConfigProfile(final JSONObject removeJSON) {
        try {
            ConfigProfileRemoveHandler.LOGGER.log(Level.INFO, "Entering remove user profile payload. Request:{0}", new Object[] { removeJSON });
            final List resourceList = this.addRemoveCommandToDevice(removeJSON);
            NotificationHandler.getInstance().SendNotification(resourceList, 1);
        }
        catch (final Exception ex) {
            ConfigProfileRemoveHandler.LOGGER.log(Level.INFO, "Exception while adding the remove payload.", ex);
        }
    }
    
    private List addRemoveCommandToDevice(final JSONObject removeJSON) {
        List<Long> resourceList = null;
        try {
            final Iterator iterator = removeJSON.keys();
            final Set<Long> resourceSet = new HashSet<Long>();
            while (iterator.hasNext()) {
                final String payloadIdentifier = iterator.next();
                final JSONObject payloadObject = removeJSON.optJSONObject(payloadIdentifier);
                String payloadID = null;
                JSONArray resourceArray = new JSONArray();
                if (payloadObject != null) {
                    payloadID = payloadObject.getString("PAYLOAD_ID");
                    resourceArray = payloadObject.getJSONArray("RESOURCE_ID");
                }
                else {
                    payloadID = payloadIdentifier;
                    resourceArray = removeJSON.optJSONArray(payloadIdentifier);
                }
                final Long commandId = this.addCommandForIdentifier(payloadID);
                final List tempResourceList = JSONUtil.getInstance().convertJSONArrayTOList(resourceArray);
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, tempResourceList);
                final Set<Long> tempResourceSet = new HashSet<Long>(tempResourceList);
                resourceSet.addAll(tempResourceSet);
            }
            resourceList = new ArrayList<Long>(resourceSet);
            ConfigProfileRemoveHandler.LOGGER.log(Level.INFO, "Remove payload for the user installed profile command added.Resource:{0}", new Object[] { resourceList });
        }
        catch (final JSONException e) {
            ConfigProfileRemoveHandler.LOGGER.log(Level.SEVERE, "Exception while getting the resource for payload", (Throwable)e);
        }
        return (resourceList != null) ? resourceList : new ArrayList<Long>();
    }
    
    private Long addCommandForIdentifier(final String payloadIdentifier) {
        final String commandUUID = "RemoveUserInstalledProfile;" + payloadIdentifier;
        return DeviceCommandRepository.getInstance().addCommand(commandUUID, "RemoveUserInstalledProfile");
    }
    
    public String getPayloadIdentifierFromCommandUUID(final String commandUUID) {
        final String[] tempString = commandUUID.split(";");
        final String payloadId = tempString[1];
        ConfigProfileRemoveHandler.LOGGER.log(Level.INFO, "Payload id for commandId:{0}", new Object[] { payloadId });
        try {
            return IOSConfigPayloadDataHandler.getPayloadIdentifierFromPayloadId(Long.valueOf(payloadId));
        }
        catch (final NumberFormatException e) {
            return payloadId;
        }
    }
    
    public void removeConfigProfileAndRefresh(final JSONObject removeJSON) {
        try {
            ConfigProfileRemoveHandler.LOGGER.log(Level.INFO, "Entering remove & refresh user profile payload. Request:{0}", new Object[] { removeJSON });
            final List resourceList = this.addRemoveCommandToDevice(removeJSON);
            if (resourceList != null & resourceList.size() > 0) {
                final Long commandId = DeviceCommandRepository.getInstance().addCommand("ProfileList");
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList);
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
        }
        catch (final Exception ex) {
            ConfigProfileRemoveHandler.LOGGER.log(Level.SEVERE, "Exception in remove and refresh payloads", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
