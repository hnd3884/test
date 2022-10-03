package com.me.mdm.server.apps.ios.vpp;

import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;
import com.me.devicemanagement.framework.server.httpclient.DMHttpClient;
import java.text.DateFormat;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.mdm.webclient.i18n.MDMI18N;
import org.json.JSONException;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppRequestResponseTester;
import com.me.mdm.agent.util.ResponseTester;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import java.util.HashMap;

public class VPPAppAPIRequestHandler
{
    private static VPPAppAPIRequestHandler appReq;
    private static HashMap<String, String> serviceMap;
    public static Logger logger;
    
    public static VPPAppAPIRequestHandler getInstance() {
        if (VPPAppAPIRequestHandler.appReq == null) {
            VPPAppAPIRequestHandler.appReq = new VPPAppAPIRequestHandler();
            setServiceUrlMap();
        }
        return VPPAppAPIRequestHandler.appReq;
    }
    
    public static void setServiceUrlMap() {
        try {
            final DataObject serviceUrlDO = MDMUtil.getPersistence().get("MdVPPServiceDetails", (Criteria)null);
            final Iterator serviceItem = serviceUrlDO.getRows("MdVPPServiceDetails");
            while (serviceItem.hasNext()) {
                final Row serviceRow = serviceItem.next();
                final String service = (String)serviceRow.get("SERVICE_NAME");
                final String url = (String)serviceRow.get("SERVICE_URL");
                VPPAppAPIRequestHandler.serviceMap.put(service, url);
            }
        }
        catch (final Exception ex) {
            VPPAppAPIRequestHandler.serviceMap.put("invitationEmailUrl", "https://buy.itunes.apple.com/WebObjects/MZFinance.woa/wa/associateVPPUserWithITSAccount?inviteCode=%inviteCode%&mt=8");
            VPPAppAPIRequestHandler.serviceMap.put("registerUserSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/registerVPPUserSrv");
            VPPAppAPIRequestHandler.serviceMap.put("editUserSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/editVPPUserSrv");
            VPPAppAPIRequestHandler.serviceMap.put("retireUserSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/retireVPPUserSrv");
            VPPAppAPIRequestHandler.serviceMap.put("getUsersSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/getVPPUsersSrv");
            VPPAppAPIRequestHandler.serviceMap.put("getLicensesSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/getVPPLicensesSrv");
            VPPAppAPIRequestHandler.serviceMap.put("manageVPPLicensesByAdamIdSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/manageVPPLicensesByAdamIdSrv");
            VPPAppAPIRequestHandler.serviceMap.put("VPPServiceConfigSrv", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/VPPServiceConfigSrv");
            VPPAppAPIRequestHandler.serviceMap.put("getVPPAssetsSrvUrl", "https://vpp.itunes.apple.com/WebObjects/MZFinance.woa/wa/getVPPAssetsSrv");
            VPPAppAPIRequestHandler.serviceMap.put("clientConfigSrvUrl", "https://vpp.itunes.apple.com/mdm/VPPClientConfigSrv");
            VPPAppAPIRequestHandler.serviceMap.put("contentMetadataLookupUrl", "https://uclient-api.itunes.apple.com/WebObjects/MZStorePlatform.woa/wa/lookup");
            VPPAppAPIRequestHandler.serviceMap.put("maxBatchAssociateLicenseCount", "1");
            VPPAppAPIRequestHandler.serviceMap.put("maxBatchDisassociateLicenseCount", "1");
            VPPAppAPIRequestHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public String getServiceUrl(final String service) {
        return VPPAppAPIRequestHandler.serviceMap.get(service);
    }
    
    public JSONObject getVppServerResponse(final String requestCommand, final String service, final String sToken, final Long businessStoreID) throws JSONException, Exception {
        JSONObject responseJSON = null;
        try {
            boolean isOkayToSendRequest = true;
            if (businessStoreID != null && businessStoreID != -1L) {
                isOkayToSendRequest = this.getIfOkayToSendRequest(businessStoreID);
            }
            if (isOkayToSendRequest) {
                VPPAppAPIRequestHandler.logger.log(Level.INFO, "Vpp App management request sent for service: {0}", new Object[] { service });
                DownloadStatus status = null;
                status = DownloadManager.getInstance().getURLResponseWithoutCookie((String)VPPAppAPIRequestHandler.serviceMap.get(service), requestCommand, new SSLValidationType[] { SSLValidationType.DEFAULT_SSL_VALIDATION });
                if (ResponseTester.isTestMode("VPPResponseTestMode")) {
                    final DownloadStatus tempStatus = new VPPAppRequestResponseTester().getTestResponseString(requestCommand, service);
                    if (tempStatus != null) {
                        status = tempStatus;
                    }
                }
                int responseStatus = status.getStatus();
                VPPAppAPIRequestHandler.logger.log(Level.INFO, "Vpp App management: Status for the request made{0}.", new Object[] { responseStatus });
                if (responseStatus == 10011) {
                    final String errorMessage = status.getErrorMessage();
                    if (errorMessage != null) {
                        Long retryAfterDate = 0L;
                        try {
                            final Date date = new Date();
                            retryAfterDate = Long.valueOf(errorMessage) + date.getTime();
                        }
                        catch (final NumberFormatException ex) {
                            try {
                                final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM YYYY HH:mm:ss z", Locale.ENGLISH);
                                final Date date2 = dateFormat.parse(errorMessage);
                                retryAfterDate = date2.getTime();
                            }
                            catch (final ParseException pex) {
                                VPPAppAPIRequestHandler.logger.log(Level.WARNING, "Exception in reading response from server : {0}", ex);
                                responseJSON = this.getErrorJson(-503, "Unable to distribute Apps at the moment. Apple Server is busy, try again after some time.");
                                VPPAppAPIRequestHandler.logger.log(Level.INFO, "Vpp App management request received for service: {0}", service);
                                return responseJSON;
                            }
                        }
                        if (businessStoreID != null && businessStoreID != -1L) {
                            MDBusinessStoreUtil.addOrUpdateBusinessStoreParam("VPPRetryAfter", retryAfterDate.toString(), businessStoreID);
                        }
                        VPPAppAPIRequestHandler.logger.log(Level.INFO, "VPP Server returned 503 Service Unavailable status. Retry after : {0}", new Object[] { retryAfterDate });
                        final Date date = new Date(retryAfterDate);
                        responseJSON = this.getErrorJson(-503, "Unable to distribute Apps at the moment. Apple Server is busy, try again after " + date.toString());
                    }
                    else {
                        responseJSON = this.getErrorJson(-503, "mdm.vpp.appleServerBusy");
                    }
                }
                else if (responseStatus == 10008) {
                    String errorMsg = I18N.getMsg("mdm.api.error.vpp_not_reachable", new Object[0]);
                    if (status.getErrorMessage() != null && status.getErrorMessage().contains("Premature EOF encountered")) {
                        errorMsg = "Unable to distribute Apps at the moment. Apple Server is busy, try again after some time.";
                        responseStatus = -503;
                    }
                    responseJSON = this.getErrorJson(responseStatus, errorMsg);
                }
                else {
                    try {
                        responseJSON = new JSONObject(status.getUrlDataBuffer());
                    }
                    catch (final JSONException jsonEx) {
                        VPPAppAPIRequestHandler.logger.log(Level.SEVERE, "The VPP response data for which the json error occured: {0}", status.getUrlDataBuffer());
                        VPPAppAPIRequestHandler.logger.log(Level.SEVERE, "Exception in processing the VPP response to josn : {0}", (Throwable)jsonEx);
                        responseJSON = this.getErrorJson(-503, I18N.getMsg("mdm.vpp.appleServerBusy", new Object[0]));
                    }
                    catch (final Exception ex2) {
                        VPPAppAPIRequestHandler.logger.log(Level.SEVERE, "Exception in processing the VPP response : {0}", ex2);
                        responseJSON = this.getErrorJson(-503, I18N.getMsg("mdm.vpp.appleServerBusy", new Object[0]));
                    }
                }
            }
            else {
                VPPAppAPIRequestHandler.logger.log(Level.WARNING, "Retry After time still not exceeded! Aborting request.");
                final String retryAfterDate2 = MDMUtil.getSyMParameter("VPPRetryAfter");
                if (retryAfterDate2 != null) {
                    final Date date3 = new Date(Long.parseLong(retryAfterDate2));
                    responseJSON = this.getErrorJson(-504, "Unable to distribute Apps at the moment. Apple Server is busy, try again after " + date3.toString());
                }
                else {
                    responseJSON = this.getErrorJson(-504, "Unable to distribute Apps at the moment. Apple Server is busy, try again after some time.");
                }
            }
        }
        catch (final Exception ex3) {
            VPPAppAPIRequestHandler.logger.log(Level.SEVERE, "Exception in getting response from VPP in method getVppServerResponse {0}", ex3);
            responseJSON = this.getErrorJson(-506, MDMI18N.getMsg("mdm.vpp.sync.failureCommonMessage", new Object[0]));
        }
        VPPAppAPIRequestHandler.logger.log(Level.INFO, "Vpp App management response command : {0}", responseJSON);
        return responseJSON;
    }
    
    private boolean getIfOkayToSendRequest(final Long businessStoreID) {
        if (businessStoreID != null) {
            final String retryValue = MDBusinessStoreUtil.getBusinessStoreParamValue("VPPRetryAfter", businessStoreID);
            if (retryValue != null) {
                final Long retryAfter = Long.parseLong(retryValue);
                final Date date = new Date();
                if (retryAfter < date.getTime()) {
                    MDBusinessStoreUtil.addOrUpdateBusinessStoreParam("VPPRetryAfter", null, businessStoreID);
                    return true;
                }
                return false;
            }
        }
        return true;
    }
    
    private JSONObject getErrorJson(final int errorNumber, final String errorMessage) throws JSONException {
        final JSONObject errorJson = new JSONObject();
        errorJson.put("status", -1);
        errorJson.put("errorNumber", errorNumber);
        errorJson.put("errorMessage", (Object)errorMessage);
        return errorJson;
    }
    
    public JSONObject getNewVppClientConfigResponse(final String sToken) throws Exception {
        JSONObject vppResponse = new JSONObject();
        final DMHttpClient dmHttpClient = new DMHttpClient();
        dmHttpClient.setUseProxyIfConfigured(true);
        final DMHttpRequest request = new DMHttpRequest();
        request.url = "https://vpp.itunes.apple.com/mdm/v2/client/config";
        final JSONObject headers = new JSONObject();
        headers.put("Authorization", (Object)("Bearer {" + sToken + "}"));
        request.headers = headers;
        request.method = "POST";
        final DMHttpResponse dmHttpResponse = dmHttpClient.execute(request);
        if (dmHttpResponse.status == 200) {
            vppResponse = new JSONObject(dmHttpResponse.responseBodyAsString);
        }
        return vppResponse;
    }
    
    static {
        VPPAppAPIRequestHandler.appReq = null;
        VPPAppAPIRequestHandler.serviceMap = new HashMap<String, String>();
        VPPAppAPIRequestHandler.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
}
