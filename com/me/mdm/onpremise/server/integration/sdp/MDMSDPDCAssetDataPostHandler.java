package com.me.mdm.onpremise.server.integration.sdp;

import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class MDMSDPDCAssetDataPostHandler
{
    private static final Logger SDPINTEGLOGGER;
    private static MDMSDPDCAssetDataPostHandler integHandler;
    
    public static MDMSDPDCAssetDataPostHandler getInstance() {
        if (MDMSDPDCAssetDataPostHandler.integHandler == null) {
            MDMSDPDCAssetDataPostHandler.integHandler = new MDMSDPDCAssetDataPostHandler();
        }
        return MDMSDPDCAssetDataPostHandler.integHandler;
    }
    
    public void postDCMDMAssetData(final DCQueueData dcQData, final int queueDataType) throws JSONException {
        final boolean isSDPIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isSDPIntegrationEnabled();
        final boolean isAEIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isAEIntegrationEnabled();
        final boolean isMDMAssetIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isMDMAssetIntegrationEnabled();
        MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is ServiceDesk Integration Enabled : {0}", isSDPIntegrationEnabled);
        MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is AssetExplorer Integration Enabled : {0}", isAEIntegrationEnabled);
        MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "Is ServiceDesk MDM Asset Enabled : {0}", isMDMAssetIntegrationEnabled);
        final String assetQueueData = (String)dcQData.queueData;
        final JSONObject assetJSON = new JSONObject();
        assetJSON.put("ASSET_QUEUE_DATA", (Object)assetQueueData);
        if ((isSDPIntegrationEnabled || isAEIntegrationEnabled) && isMDMAssetIntegrationEnabled) {
            if (isSDPIntegrationEnabled) {
                assetJSON.put("APPNAME", (Object)"HelpDesk");
            }
            else {
                assetJSON.put("APPNAME", (Object)"AssetExplorer");
            }
            dcQData.queueData = assetJSON;
            this.postDCMDMAssetDataToSDPQueue(dcQData, queueDataType);
        }
    }
    
    private boolean checkSDPCompatibleCheck(final int queueDataType) {
        boolean isSDPCompatible = false;
        try {
            int sdpBN = 0;
            final String sdpBuildNumber = MDMSDPIntegrationUtil.getInstance().getSDPBuildNumber();
            if (sdpBuildNumber == null) {
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() Desktopcentral droping MDM data due to SDP build number is NULL {0}", sdpBN);
                return isSDPCompatible;
            }
            sdpBN = Integer.parseInt(sdpBuildNumber);
            MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() sdpBuildNumber {0} ", sdpBuildNumber);
            switch (queueDataType) {
                case 18: {
                    if (sdpBN > 8202) {
                        MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() Adding iOS data to SDP Queue  ");
                        isSDPCompatible = true;
                        break;
                    }
                    MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() MDM droping iOS data due to incompatable SDP build number found SDPBuilNumber : {0}", sdpBN);
                    break;
                }
                case 19: {
                    if (sdpBN > 8212) {
                        MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() Adding Android data to SDP Queue  ");
                        isSDPCompatible = true;
                        break;
                    }
                    MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() MDM droping Android data due to incompatable SDP build number found SDPBuilNumber : {0}", sdpBN);
                    break;
                }
                case 20: {
                    if (sdpBN > 8212) {
                        MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() Adding Windows data to SDP Queue  ");
                        isSDPCompatible = true;
                        break;
                    }
                    MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.INFO, "checkSDPCompatibleCheck() MDM droping Windows data due to incompatable SDP build number found SDPBuilNumber : {0}", sdpBN);
                    break;
                }
            }
        }
        catch (final Exception ex) {
            MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception in checkSDPCompatibleCheck", ex);
        }
        return isSDPCompatible;
    }
    
    private void postDCMDMAssetDataToSDPQueue(final DCQueueData dcQData, final int queueDataType) {
        try {
            if (dcQData == null) {
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "SDP DC MDM QueueData is null");
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
            }
            else {
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "============= Adding SDP DC MDM INV Data post QUEUE==============");
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "=================================================================");
                dcQData.queueDataType = queueDataType;
                DCQueueHandler.addToQueue("mdm-sdp-data", dcQData);
                MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.FINEST, "SDP DC MDM Inventory data has been added to Queue... ");
            }
        }
        catch (final Exception e) {
            MDMSDPDCAssetDataPostHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception in postDCMDMAssetDataToSDPQueue", e);
        }
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSDPDCAssetDataPostHandler.integHandler = null;
    }
}
