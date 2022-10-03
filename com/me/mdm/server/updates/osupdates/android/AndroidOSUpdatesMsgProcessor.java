package com.me.mdm.server.updates.osupdates.android;

import com.me.mdm.server.updates.osupdates.OSUpdateCriteriaEvaluator;
import com.me.mdm.server.updates.osupdates.ResourcesMissingUpdatesListener;
import com.me.mdm.server.updates.osupdates.ResourceUpdateEventListener;
import com.me.mdm.server.updates.osupdates.ExtendedOSDetailsDataHandler;
import com.me.mdm.server.updates.osupdates.OSUpdatesProcessor;
import org.json.JSONException;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AndroidOSUpdatesMsgProcessor
{
    private static final Logger LOGGER;
    
    public void processResponse(final Long resourceID, final JSONObject msgJSON, final String msgType) throws JSONException, Exception {
        final boolean isUpdateAvailable = msgJSON.getBoolean("IsUpdateAvailable");
        if (msgType.equalsIgnoreCase("PendingOSUpdates")) {
            if (isUpdateAvailable) {
                final JSONArray updates = msgJSON.getJSONArray("UpdateDetails");
                for (int i = 0; i < updates.length(); ++i) {
                    final JSONObject update = updates.getJSONObject(i);
                    this.processDiscoveredUpdate(update, resourceID);
                }
            }
            else {
                AndroidOSUpdatesMsgProcessor.LOGGER.log(Level.SEVERE, "No update available on this device response received. Clearing device available entries");
                new ResourceOSUpdateDataHandler().deleteAvailableUpdatesForResource(resourceID, new ArrayList<String>());
            }
        }
        else if (msgType.equalsIgnoreCase("OsDownloadFailure") || msgType.equalsIgnoreCase("StorageUsability")) {
            final Long collectionID = msgJSON.optLong("CollectionID");
            if (collectionID != -1L) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID.toString(), 7, String.valueOf(msgJSON.get("Remarks")));
            }
        }
        else if (msgType.equalsIgnoreCase("OsDownloadSuccess")) {
            final Long collectionID = msgJSON.optLong("CollectionID");
            if (collectionID != -1L) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID.toString(), 6, String.valueOf(msgJSON.get("Remarks")));
            }
        }
    }
    
    private void processDiscoveredUpdate(final JSONObject update, final Long resID) {
        try {
            final OSUpdatesProcessor processor = new OSUpdatesProcessor();
            processor.setExtnOSDetailsDataHandler(null);
            final ArrayList<Long> list = new ArrayList<Long>();
            list.add(resID);
            processor.setResourcesToCheckMissingUpdate(list, new ResourceUpdateEventListener());
            final JSONObject json = this.prepareOSUpdateDetailsJson(update);
            processor.processOSUpdateDetails(json, new AndroidUniqueOSEvaluator());
        }
        catch (final Exception e) {
            AndroidOSUpdatesMsgProcessor.LOGGER.log(Level.SEVERE, null, e);
        }
    }
    
    private JSONObject prepareOSUpdateDetailsJson(final JSONObject update) throws Exception {
        final JSONObject json = new JSONObject();
        final int updateType = update.optInt("UpdateType", 1);
        json.put("UPDATE_NAME", (Object)((updateType == 2) ? "Android Security Patch" : "Android OS Update"));
        json.put("UPDATE_DESCRIPTION", (Object)"Android OS Update");
        json.put("UPDATE_TYPE", updateType);
        json.put("ADDED_AT", System.currentTimeMillis());
        json.put("UPDATE_PLATFORM", 2);
        final Long downloadSize = -1L;
        final Long installSize = -1L;
        final Boolean restartReqd = Boolean.TRUE;
        json.put("DOWNLOAD_SIZE", (Object)downloadSize);
        json.put("INSTALL_SIZE", (Object)installSize);
        json.put("RESTART_REQUIRED", (Object)restartReqd);
        json.put("VERSION", (Object)"--");
        return json;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
