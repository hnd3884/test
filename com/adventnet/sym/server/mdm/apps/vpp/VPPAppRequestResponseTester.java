package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.agent.util.ResponseTester;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;

public class VPPAppRequestResponseTester
{
    public DownloadStatus getTestResponseString(final String requestCommand, final String serviceName) {
        try {
            final DownloadStatus downloadStatus = new DownloadStatus();
            final String fileName = this.getResponseFileName(requestCommand, serviceName);
            final String responseStr = ResponseTester.getTestResponseString(fileName);
            final JSONObject responseJSON = new JSONObject(responseStr);
            downloadStatus.setStatus(responseJSON.optInt("HTTP_STATUS", 200));
            downloadStatus.setErrorMessage(responseJSON.optString("HTTP_ERROR_MESSAGE", (String)null));
            downloadStatus.setUrlDataBuffer(String.valueOf(responseJSON.get("VPP_RESPONSE")));
            return downloadStatus;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error in getTestResponseString ", e);
            return null;
        }
    }
    
    private String getResponseFileName(final String requestCommand, final String serviceName) {
        String fileName = null;
        switch (serviceName) {
            case "getVPPAssetsSrvUrl": {
                fileName = "TestresponseGetVppAssets.json";
                break;
            }
            case "getLicensesSrvUrl": {
                try {
                    final JSONObject requestCommandJSON = new JSONObject(requestCommand);
                    final String adamId = String.valueOf(requestCommandJSON.get("adamId"));
                    fileName = "TestresponseGetVppLicense_" + adamId + ".json";
                }
                catch (final Exception e) {
                    Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error in getResponseFileName , in GET_VPP_LICENSES_SRV", e);
                }
                break;
            }
            case "manageVPPLicensesByAdamIdSrvUrl": {
                try {
                    final JSONObject requestCommandJSON = new JSONObject(requestCommand);
                    final String adamId = String.valueOf(requestCommandJSON.get("adamIdStr"));
                    fileName = "TestresponseManageVppLicense.json";
                }
                catch (final Exception e) {
                    Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error in getResponseFileName , in MANAGE_VPP_LICENSES_BY_ADAMID_SRV", e);
                }
                break;
            }
            case "VPPServiceConfigSrv": {
                try {
                    fileName = "TestresponseVppServiceConfig.json";
                }
                catch (final Exception e) {
                    Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error in getResponseFileName , in MANAGE_VPP_LICENSES_BY_ADAMID_SRV", e);
                }
                break;
            }
            case "clientConfigSrvUrl": {
                try {
                    fileName = "TestresponseVppServiceConfig.json";
                }
                catch (final Exception e) {
                    Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error in getResponseFileName , in MANAGE_VPP_LICENSES_BY_ADAMID_SRV", e);
                }
                break;
            }
        }
        return fileName;
    }
}
