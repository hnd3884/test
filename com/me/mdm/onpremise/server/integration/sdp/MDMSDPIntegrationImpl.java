package com.me.mdm.onpremise.server.integration.sdp;

import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.mdm.server.factory.SDPIntegrationAPI;

public class MDMSDPIntegrationImpl implements SDPIntegrationAPI
{
    private static final Logger SDPINTEGLOGGER;
    
    public void postMDMDataToSDP(final DCQueueData dcQData, final int queueDataType) {
        try {
            if (CustomerInfoUtil.isDC()) {
                MDMSDPDCAssetDataPostHandler.getInstance().postDCMDMAssetData(dcQData, queueDataType);
            }
            else if (CustomerInfoUtil.isMDMP()) {
                MDMSDPAssetDataPostHandler.getInstance().postMDMAssetData(dcQData, queueDataType);
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationImpl.SDPINTEGLOGGER.log(Level.WARNING, "Exception in MDMSDPIntegrationImpl - postMDMDataToSDP", e);
        }
    }
    
    public void handleSDPAlerts(final Properties propRequest, final String alertType) {
        switch (alertType) {
            case "SDP_MDM_HELPDESK_NEW_APP_ALERT": {
                MDMSDPAlertHandler.getInstance().handleSDPNewAppAlert(propRequest);
                break;
            }
            case "SDP_MDM_HELPDESK_BLACKLIST_APP_ALERT": {
                MDMSDPAlertHandler.getInstance().handleSDPBlackListAppAlert(propRequest);
                break;
            }
            case "SDP_MDM_HELPDESK_UNMANAGED_CERTIFICATE_ALERT": {
                MDMSDPAlertHandler.getInstance().handleSDPCertificateAlert(propRequest);
                break;
            }
        }
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
    }
}
