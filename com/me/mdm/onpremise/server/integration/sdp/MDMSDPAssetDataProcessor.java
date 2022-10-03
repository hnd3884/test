package com.me.mdm.onpremise.server.integration.sdp;

import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import org.json.JSONArray;
import java.net.URLConnection;
import java.net.ConnectException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import java.net.URLEncoder;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.core.windows.SyncMLMessageParser;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class MDMSDPAssetDataProcessor
{
    private static final Logger SDPINTEGLOGGER;
    private static MDMSDPAssetDataProcessor sdpAssetHandler;
    
    public static MDMSDPAssetDataProcessor getInstance() {
        if (MDMSDPAssetDataProcessor.sdpAssetHandler == null) {
            MDMSDPAssetDataProcessor.sdpAssetHandler = new MDMSDPAssetDataProcessor();
        }
        return MDMSDPAssetDataProcessor.sdpAssetHandler;
    }
    
    public void handleMDMAssetData(final DCQueueData dcQData) {
        try {
            HashMap<String, String> hmap = new HashMap<String, String>();
            Long customerID = null;
            String customerName = "";
            final int platform = dcQData.queueDataType;
            final JSONObject assetJSON = (JSONObject)dcQData.queueData;
            final String commandResponseData = String.valueOf(assetJSON.get("ASSET_QUEUE_DATA"));
            final String appName = String.valueOf(assetJSON.get("APPNAME"));
            switch (platform) {
                case 18: {
                    hmap = PlistWrapper.getInstance().getHashFromPlist(commandResponseData);
                    hmap.put("platform", "iOS");
                    break;
                }
                case 19: {
                    hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(commandResponseData));
                    hmap.put("platform", "android");
                    break;
                }
                case 20: {
                    final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
                    final SyncMLMessage requestSyncML = converter.transform(commandResponseData);
                    final SyncMLMessageParser parser = new SyncMLMessageParser();
                    final JSONObject jsonObject = parser.parseSyncMLMessageHeader(requestSyncML);
                    hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(jsonObject);
                    hmap.put("platform", "windows");
                    break;
                }
                case 21: {
                    hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(commandResponseData));
                    hmap.put("platform", "ChromeOS");
                    break;
                }
            }
            customerID = dcQData.customerID;
            if (customerID != null) {
                customerName = CustomerInfoUtil.getInstance().getCustomerNameFromID(customerID);
                hmap.put("customername", customerName);
            }
            final String strUDID = hmap.get("UDID");
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "handleAssetData() resourceID : {0}", resourceID);
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName((long)resourceID);
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "handleAssetData() deviceName : {0}", deviceName);
            boolean sdpDataPosted = false;
            boolean aeDataPosted = false;
            final boolean ifSDP = appName.equals("SDP_AND_AE") || appName.equals("HelpDesk");
            if (ifSDP) {
                final String sdpBuildNumber = MDMSDPIntegrationUtil.getInstance().getSDPBuildNumber();
                hmap.put("buildno", sdpBuildNumber);
            }
            if (appName.equals("SDP_AND_AE")) {
                sdpDataPosted = this.postAssetDataToSDP(resourceID, deviceName, hmap, "HelpDesk");
                aeDataPosted = this.postAssetDataToSDP(resourceID, deviceName, hmap, "AssetExplorer");
                MDMSDPIntegrationUtil.getInstance().addSDPAccesslog("Add/Update Asset Data", strUDID, sdpDataPosted && aeDataPosted);
            }
            else if (appName.equals("HelpDesk")) {
                sdpDataPosted = this.postAssetDataToSDP(resourceID, deviceName, hmap, appName);
                MDMSDPIntegrationUtil.getInstance().addSDPAccesslog("Add/Update Asset Data", strUDID, sdpDataPosted);
            }
            else if (appName.equals("AssetExplorer")) {
                aeDataPosted = this.postAssetDataToSDP(resourceID, deviceName, hmap, "AssetExplorer");
                MDMSDPIntegrationUtil.getInstance().addSDPAccesslog("Add/Update Asset Data", strUDID, aeDataPosted);
            }
            final boolean sdpMDMAssetpostOwner = MDMSDPIntegrationUtil.getInstance().isMDMPostOwner();
            if (ifSDP && sdpMDMAssetpostOwner) {
                this.mapUserwithDevice(strUDID, "HelpDesk");
            }
        }
        catch (final Exception e) {
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "Exception while handling asset data : ", e);
        }
    }
    
    private boolean postAssetDataToSDP(final Long resourceID, String deviceName, HashMap hsContentForPost, final String appName) {
        if (deviceName != null) {
            deviceName = deviceName.trim();
            deviceName = URLEncoder.encode(deviceName);
        }
        hsContentForPost = MDMSDPIntegrationUtil.getInstance().handleMDMAssetContentforSDPXML(resourceID, hsContentForPost);
        final String postPath = MDMSDPIntegrationUtil.getInstance().getMDMAssetPostPath(resourceID, deviceName, hsContentForPost);
        final String xmlData = (String)hsContentForPost.get("xmlcontent");
        Boolean bDataPosted = false;
        try {
            final boolean isSDPIntegrationEnabled = MDMSDPIntegrationUtil.getInstance().isSDPIntegrationEnabled();
            final String apiBaseUrl = this.getAPIBaseUrl(appName) + postPath;
            final URLConnection urlConn = MDMSDPIntegrationUtil.getInstance().createURLConnection(appName, apiBaseUrl, true);
            CustomerInfoUtil.getInstance();
            final Boolean isMDMP = CustomerInfoUtil.isMDMP();
            String authenticationKey = SolutionUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
            if (!isMDMP) {
                authenticationKey = Encoder.convertFromBase(authenticationKey);
            }
            if (authenticationKey != null) {
                urlConn.setRequestProperty("AUTHTOKEN", authenticationKey);
            }
            final OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream(), "UTF-8");
            wr.write(xmlData);
            wr.write("\n\n");
            wr.flush();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line = rd.readLine();
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "Response form SDP  -  {0}", line);
            boolean writeResponse = true;
            if (isSDPIntegrationEnabled) {
                final String sdpBuildNumber = (String)hsContentForPost.get("buildno");
                final Long buildno = Long.parseLong(sdpBuildNumber);
                if (buildno > 9302L) {
                    writeResponse = false;
                }
            }
            else {
                writeResponse = true;
            }
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "Posting data completed for  -  {0}", deviceName);
            if (writeResponse) {
                while (line != null) {
                    line = rd.readLine();
                }
                wr.close();
                rd.close();
                bDataPosted = true;
            }
            else {
                final JSONObject responseJSON = new JSONObject(line);
                final JSONObject propJSON = (JSONObject)responseJSON.get("response_status");
                final JSONArray statusJSON = propJSON.getJSONArray("messages");
                final JSONObject statuscode = statusJSON.getJSONObject(0);
                final int sPropertyValue = statuscode.getInt("statuscode");
                MDMSDPIntegrationUtil.getInstance().addMETrackingSDPData(sPropertyValue);
                if (sPropertyValue == 4000) {
                    bDataPosted = true;
                }
                else {
                    MDMSDPIntegrationUtil.getInstance().addMETrackingSDPData(5001);
                    bDataPosted = false;
                }
            }
        }
        catch (final ConnectException ex) {
            bDataPosted = false;
            MDMSDPIntegrationUtil.getInstance().addMETrackingSDPData(5005);
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "SDP is not Reachable");
        }
        catch (final Exception ex2) {
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "Error while posting data to SDP", ex2);
            bDataPosted = false;
        }
        return bDataPosted;
    }
    
    private String getAPIBaseUrl(final String appName) {
        String apiBaseUrl = "";
        try {
            if (appName.equals("HelpDesk")) {
                apiBaseUrl = MDMSDPIntegrationUtil.getInstance().getServiceDeskBaseURL();
            }
            else if (appName.equals("AssetExplorer")) {
                apiBaseUrl = MDMSDPIntegrationUtil.getInstance().getAssetExplorerBaseURL();
            }
        }
        catch (final Exception e) {
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.SEVERE, "Exception in getAPIBaseUrl ", e);
        }
        return apiBaseUrl;
    }
    
    public void mapUserwithDevice(final String strUDID, final String appName) {
        try {
            final int sdpBuild = Integer.valueOf(SolutionUtil.getInstance().getSDPBuildNumber());
            final Boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            final HashMap userDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(strUDID);
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName((long)resourceID);
            final String userName = userDetails.get("NAME");
            final Long customerId = userDetails.get("CUSTOMER_ID");
            final String domainNetBiosName = userDetails.get("DOMAIN_NETBIOS_NAME");
            final Properties domainProps = DMDomainDataHandler.getInstance().getDomainProps(domainNetBiosName, customerId);
            final String adDomainName = domainProps.getProperty("AD_DOMAIN_NAME", domainNetBiosName);
            final int modelType = MDMUtil.getInstance().getModelType(resourceID);
            final String ciType = (modelType == 2) ? "Smart Phone" : "Tablet";
            final String assetName = deviceName.concat(" (").concat(strUDID).concat(")");
            final String authenticationKey = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
            final String apiBaseUrl = this.getAPIBaseUrl(appName);
            String apiurl = apiBaseUrl.concat("/api/v3/users");
            if ((isMSP && sdpBuild < MDMSDPIntegrationConstants.SDP_MSP_V3_COMPATIBLE_BUILD) || (!isMSP && sdpBuild < 11000)) {
                apiurl = apiBaseUrl.concat("/sdpapi/requester");
            }
            final String apiurlforuser = apiBaseUrl.concat("/api/cmdb/ci");
            final String sdpUserName = MDMSDPIntegrationHandler.getInstance().getSDPUserNameForUserName(userName, domainNetBiosName, authenticationKey, apiurl);
            if (sdpUserName != null) {
                MDMSDPIntegrationHandler.getInstance().updateAssetOwnerToSDP(ciType, strUDID, assetName, sdpUserName, authenticationKey, apiurlforuser);
            }
        }
        catch (final Exception e) {
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.SEVERE, "Exception while mapping User with Device ", e);
        }
    }
    
    public void deviceDeleteChangesinSDP(final DeviceEvent deviceEvent) {
        try {
            final List assetUDIDList = new ArrayList();
            final String assetUDID = deviceEvent.udid;
            assetUDIDList.add(assetUDID);
            final int modelType = MDMUtil.getInstance().getModelType(deviceEvent.resourceID);
            final String ciType = (modelType == 2) ? "Smart Phone" : "Tablet";
            final String assetMDMDelValue = MDMSDPIntegrationUtil.getInstance().getMDMAssetDelValue();
            if (assetMDMDelValue.equals("dispose")) {
                MDMSDPIntegrationHandler.getInstance().updateAssetStatusToSDP(ciType, assetUDIDList, "Disposed");
            }
            else if (assetMDMDelValue.equals("delete")) {
                MDMSDPIntegrationHandler.getInstance().deleteAssetfromSDP(ciType, assetUDIDList);
            }
        }
        catch (final Exception e) {
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.SEVERE, "Exception while handling device delete changes in SDP ", e);
        }
    }
    
    public void deviceAddedChangesinSDP(final DeviceEvent deviceEvent) {
        try {
            final List assetUDIDList = new ArrayList();
            final String assetUDID = deviceEvent.udid;
            assetUDIDList.add(assetUDID);
            final int modelType = MDMUtil.getInstance().getModelType(deviceEvent.resourceID);
            final String ciType = (modelType == 2) ? "Smart Phone" : "Tablet";
            MDMSDPIntegrationHandler.getInstance().updateAssetStatusToSDP(ciType, assetUDIDList, "In store");
        }
        catch (final Exception e) {
            MDMSDPAssetDataProcessor.SDPINTEGLOGGER.log(Level.SEVERE, "Exeception while handling device added changes in SDP", e);
        }
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSDPAssetDataProcessor.sdpAssetHandler = null;
    }
}
