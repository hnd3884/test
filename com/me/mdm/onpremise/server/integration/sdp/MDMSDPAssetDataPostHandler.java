package com.me.mdm.onpremise.server.integration.sdp;

import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class MDMSDPAssetDataPostHandler
{
    private static final Logger SDPINTEGLOGGER;
    private static MDMSDPAssetDataPostHandler integHandler;
    
    public static MDMSDPAssetDataPostHandler getInstance() {
        if (MDMSDPAssetDataPostHandler.integHandler == null) {
            MDMSDPAssetDataPostHandler.integHandler = new MDMSDPAssetDataPostHandler();
        }
        return MDMSDPAssetDataPostHandler.integHandler;
    }
    
    public void postMDMAssetData(final DCQueueData dcQData, final int queueDataType) throws JSONException {
        final boolean isSDPIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isSDPIntegrationEnabled();
        final boolean isAEIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isAEIntegrationEnabled();
        final boolean isMDMAssetIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isMDMAssetIntegrationEnabled();
        final boolean isMDMAEAssetIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isMDMAEAssetIntegrationEnabled();
        MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is ServiceDesk Integration Enabled : {0}", isSDPIntegrationEnabled);
        MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is AssetExplorer Integration Enabled : {0}", isAEIntegrationEnabled);
        MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is ServiceDesk MDM Asset Enabled : {0}", isMDMAssetIntegrationEnabled);
        MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is AssetExplorer MDM Asset Enabled : {0}", isMDMAEAssetIntegrationEnabled);
        final String assetQueueData = (String)dcQData.queueData;
        final JSONObject assetJSON = new JSONObject();
        assetJSON.put("ASSET_QUEUE_DATA", (Object)assetQueueData);
        if (isSDPIntegrationEnabled && isMDMAssetIntegrationEnabled && isAEIntegrationEnabled && isMDMAEAssetIntegrationEnabled) {
            assetJSON.put("APPNAME", (Object)"SDP_AND_AE");
            dcQData.queueData = assetJSON;
            this.postMDMAssetDataToSDPQueue(dcQData, queueDataType);
        }
        else if (isSDPIntegrationEnabled && isMDMAssetIntegrationEnabled) {
            assetJSON.put("APPNAME", (Object)"HelpDesk");
            dcQData.queueData = assetJSON;
            this.postMDMAssetDataToSDPQueue(dcQData, queueDataType);
        }
        else if (isAEIntegrationEnabled && isMDMAEAssetIntegrationEnabled) {
            assetJSON.put("APPNAME", (Object)"AssetExplorer");
            dcQData.queueData = assetJSON;
            this.postMDMAssetDataToSDPQueue(dcQData, queueDataType);
        }
    }
    
    private void postMDMAssetDataToSDPQueue(final DCQueueData dcQData, final int queueDataType) {
        try {
            if (dcQData == null) {
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "SDP MDM QueueData is null");
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
            }
            else {
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "============= Adding SDP MDM INV Data post QUEUE==============");
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
                dcQData.queueDataType = queueDataType;
                DCQueueHandler.addToQueue("mdm-sdp-data", dcQData);
                MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "SDP MDM Inventory data has been added to Queue... ");
            }
        }
        catch (final Exception e) {
            MDMSDPAssetDataPostHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception in postMDMAssetDataToSDPQueue", e);
        }
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSDPAssetDataPostHandler.integHandler = null;
    }
}
