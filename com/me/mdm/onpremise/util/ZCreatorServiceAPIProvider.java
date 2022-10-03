package com.me.mdm.onpremise.util;

import org.json.JSONException;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ZCreatorServiceAPIProvider
{
    private Logger mdmEnrolllogger;
    
    public ZCreatorServiceAPIProvider() {
        this.mdmEnrolllogger = Logger.getLogger("MDMEnrollment");
    }
    
    private JSONObject getCreatorServiceAPIs() {
        try {
            final String authToken = "e75ecc5a22c9f396189820402cea34e8";
            final String ownerName = "desktopcentral1";
            final String applicationname = "desktop-central-request-support-form";
            final String fromName = "MDMCreatorServiceAPI";
            final String formReportName = "MDMCreatorServiceAPI_Report";
            final String criteriaFieldName = "APIVersion";
            final String criteriaFieldValue = "2";
            final String viewCriteria = "(" + criteriaFieldName + "=" + criteriaFieldValue + ")";
            final String apiURLForMDMServiceAPIss = "https://creator.zoho.com/api/json/" + applicationname + "/view/" + formReportName;
            final String parameters = "authtoken=" + authToken + "&scope=creatorapi&criteria=" + viewCriteria + "&raw=true&zc_ownername=desktopcentral1";
            final String constructedFinalURL = apiURLForMDMServiceAPIss + "?" + parameters;
            this.mdmEnrolllogger.log(Level.INFO, "MDMCreatorServiceAPI:Going to download MDMServiceURL from Creator:{0}", apiURLForMDMServiceAPIss);
            final DownloadStatus downloadStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(constructedFinalURL, (String)null, new SSLValidationType[0]);
            final int responseCode = downloadStatus.getStatus();
            if (responseCode == 0) {
                final String responseContent = downloadStatus.getUrlDataBuffer();
                this.mdmEnrolllogger.log(Level.INFO, "MDMCreatorServiceAPI: Data posted to creator successfully..response: {0}", responseContent);
                if (JSONUtil.getInstance().isValidJSON(responseContent)) {
                    final JSONObject responseJSONObject = new JSONObject(responseContent);
                    final JSONArray jsonArray = responseJSONObject.getJSONArray(fromName);
                    final JSONObject contentObject = jsonArray.getJSONObject(0);
                    this.mdmEnrolllogger.log(Level.INFO, "MDMCreatorServiceAPI: MDMDiscoveryServiceAPIS for Version {0} is : {1}", new Object[] { criteriaFieldValue, contentObject });
                    if (JSONUtil.getInstance().isValidJSON(contentObject.toString())) {
                        return contentObject;
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ZCreatorServiceAPIProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private JSONObject getMDMService(final String serviceName) {
        final JSONObject obj = this.getCreatorServiceAPIs();
        switch (serviceName) {
            case "afw": {
                return null;
            }
            case "csrsign": {
                if (obj != null) {
                    try {
                        final String json = String.valueOf(obj.get("CSRSignAPI"));
                        return new JSONObject(json);
                    }
                    catch (final JSONException ex) {
                        Logger.getLogger(ZCreatorServiceAPIProvider.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
                    }
                    break;
                }
                break;
            }
        }
        return null;
    }
    
    private JSONObject getCSRSignAPIs() {
        return this.getMDMService("csrsign");
    }
    
    public JSONObject getCSRSigningCreatorProps() {
        JSONObject creatorProps = this.getCSRSignAPIs();
        if (creatorProps == null) {
            creatorProps = this.getDefaultCreatorCSRProps();
        }
        return creatorProps;
    }
    
    private JSONObject getDefaultCreatorCSRProps() {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("form_name", (Object)"MDMAutoCSRSig");
            obj.put("report_name", (Object)"https://creator.zoho.com/api/json/desktop-central-request-support-form/view/MDMAutoCSRSigReqReport");
            return obj;
        }
        catch (final JSONException ex) {
            Logger.getLogger(ZCreatorServiceAPIProvider.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return obj;
        }
    }
}
