package com.me.mdm.onpremise.util;

import java.util.Hashtable;
import com.me.mdm.api.filter.MDMPAPIUnifiedAuthenticationHandler;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import java.util.Properties;
import java.util.List;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.util.CreatorDataPost;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.me.mdm.server.util.CloudAPIDataPost;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import org.json.JSONObject;
import com.me.mdm.onpremise.server.chrome.ChromeOAuthOPHandler;
import com.me.mdm.onpremise.notification.IOSFCMNotificationCreatorHandler;
import com.me.mdm.onpremise.server.android.agent.AndroidAgentSecretsHandler;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.mdm.server.factory.SecureKeyProviderAPI;

public class SecureKeyProviderImpl implements SecureKeyProviderAPI
{
    private String className;
    private static final Logger LOGGER;
    private Logger mdmEnrolllogger;
    private String csrSignUrl;
    
    public SecureKeyProviderImpl() {
        this.className = "SecureKeyProviderImpl";
        this.mdmEnrolllogger = Logger.getLogger("MDMEnrollment");
        this.csrSignUrl = "https://mdm.manageengine.com/api/v1/mdm/";
    }
    
    public HashMap<Integer, HashMap<String, String>> getWindowsWakeUpCredentials() {
        try {
            final HashMap<Integer, HashMap<String, String>> wakeupCredentials = new HashMap<Integer, HashMap<String, String>>();
            final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("WPNSInfo"));
            sql.addSelectColumn(new Column("WPNSInfo", "*"));
            final DataObject dao = MDMUtil.getPersistence().get(sql);
            final Iterator iter = dao.getRows("WPNSInfo");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final HashMap<String, String> wakeUpData = new HashMap<String, String>();
                wakeUpData.put("PACKAGE_SID", CryptoUtil.decrypt(row.get("PACKAGE_SID").toString(), "1594715223334"));
                wakeUpData.put("CLIENT_SECRET", CryptoUtil.decrypt(row.get("CLIENT_SECRET").toString(), "1594715223334"));
                wakeUpData.put("PFN", CryptoUtil.decrypt(row.get("PFN").toString(), "1594715223334"));
                wakeupCredentials.put((Integer)row.get("APP_TYPE"), wakeUpData);
            }
            return wakeupCredentials;
        }
        catch (final Exception ex) {
            SecureKeyProviderImpl.LOGGER.log(Level.SEVERE, "Exception in MDMUtil.getWindowsWakeUpCredentials {0}", ex);
            return null;
        }
    }
    
    public String getSecret(final String key) throws Exception {
        if (key.equals("WindowsVendorPass")) {
            return "Vembu123";
        }
        if (key.equals("GCMAPIKey")) {
            return AndroidAgentSecretsHandler.getGCMAPIKey();
        }
        if (key.equals("ELMKey")) {
            return AndroidAgentSecretsHandler.getELMKey();
        }
        if (key.equals("GCMProjectId")) {
            return AndroidAgentSecretsHandler.getGCMProjectId();
        }
        if (key.equals("AttestationApiKey")) {
            return AndroidAgentSecretsHandler.getAttestationKey();
        }
        if (key.equals("BackwardCompatibilityElmKey")) {
            return AndroidAgentSecretsHandler.getBackwardCompatibilityElmKey();
        }
        if (key.equals("FCMAPIKey")) {
            return new IOSFCMNotificationCreatorHandler().getFcmServerKey();
        }
        if (key.startsWith("CHROMEMGMT_CLIENT")) {
            return new ChromeOAuthOPHandler().getChromeClientDetails(key);
        }
        return null;
    }
    
    public JSONObject signCSR(final JSONObject submitJSONObject) throws Exception {
        JSONObject responseJSON = null;
        try {
            final String postURL = this.csrSignUrl + "sign_csr_purposekey_api";
            submitJSONObject.put("MEDCTrackId", (Object)METrackerUtil.getMEDCTrackId());
            final CloudAPIDataPost postData = new CloudAPIDataPost();
            postData.postDataToCloud(postURL, submitJSONObject);
            responseJSON = new JSONObject(postData.response);
            final String methodName = "SignCSR";
            if (postData.status != 200) {
                SecureKeyProviderImpl.LOGGER.info("CSr Signing Request Failed with Error response " + postData.response);
                return null;
            }
            SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Csr Signing Request Completed Successfully");
            APNsCertificateHandler.getInstance().handleVendorSignedResponse(responseJSON);
            if (System.currentTimeMillis() > responseJSON.getLong("vendorExpiryDate")) {
                SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Vendor signed certificate has expired");
                return null;
            }
        }
        catch (final Exception ex) {
            SecureKeyProviderImpl.LOGGER.log(Level.INFO, "200 received for the API but , has following exception {0}", ex);
            SecureKeyProviderImpl.LOGGER.log(Level.INFO, "200 received for the API but , has exception. Response received {0}", responseJSON);
            return null;
        }
        return responseJSON;
    }
    
    public JSONObject getAndUpdateSignedData(final JSONObject creatorAPIJSON) throws Exception {
        final String methodName = "getAndUpdateSignedData";
        try {
            final JSONObject vendorSignedInfoJSONObject = APNsCertificateHandler.getInstance().getVendorSignedInfo();
            final Long fieldId = (Long)vendorSignedInfoJSONObject.get("CSR_REQUEST_ID");
            CreatorDataPost.creator_auth_token = "e75ecc5a22c9f396189820402cea34e8";
            final String viewURL = String.valueOf(creatorAPIJSON.get("report_name"));
            final String formName = String.valueOf(creatorAPIJSON.get("form_name"));
            final String parameters = "authtoken=" + CreatorDataPost.creator_auth_token + "&scope=creatorapi&criteria=(ID=" + String.valueOf(fieldId) + ")&raw=true&zc_ownername=desktopcentral1";
            final String getURL = viewURL + "?" + parameters;
            final DownloadStatus downloadStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(getURL, (String)null, new SSLValidationType[0]);
            final int responseCode = downloadStatus.getStatus();
            if (responseCode == 0) {
                final String responseContent = downloadStatus.getUrlDataBuffer();
                SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Data posted to creator successfully..response: " + responseContent);
                if (JSONUtil.getInstance().isValidJSON(responseContent)) {
                    final JSONObject responseJSONObject = new JSONObject(responseContent);
                    final JSONArray jsonArray = responseJSONObject.getJSONArray(formName);
                    final JSONObject contentObject = jsonArray.getJSONObject(0);
                    final String responseString = contentObject.get("ResponseObject").toString();
                    if (JSONUtil.getInstance().isValidJSON(responseString)) {
                        final JSONObject responseData = new JSONObject(responseString);
                        responseData.put("VALID_RESPONSE_FROM_VENDOR", true);
                        SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Vendor Successfully Signed the CSR");
                        return responseData;
                    }
                }
                else {
                    SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Vendor Sign Failed due to Some Error in Creator");
                }
            }
            else {
                SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Vendor Sign Failed due to Some Error in Creator");
            }
        }
        catch (final Exception e) {
            SecureKeyProviderImpl.LOGGER.logp(Level.INFO, this.className, methodName, "Exception occurred : " + e);
            throw e;
        }
        return null;
    }
    
    public JSONObject enrollESA(final JSONObject data) throws Exception {
        final JSONObject enrollData = new JSONObject(data.toString());
        enrollData.put("Action", (Object)"Enroll");
        return this.postAFWDataToCloud(this.csrSignUrl + "afw_enroll_purposekey_api", enrollData);
    }
    
    public JSONObject unenrollESA(final JSONObject data) throws Exception {
        final JSONObject enrollData = new JSONObject(data.toString());
        enrollData.put("Action", (Object)"UnEnroll");
        return this.postAFWDataToCloud(this.csrSignUrl + "afw_unenroll_purposekey_api", enrollData);
    }
    
    public JSONObject processAFWRegistrationRequest(final JSONObject data) throws Exception {
        final JSONObject enrollData = new JSONObject(data.toString());
        enrollData.put("Action", (Object)"RegisterAFW");
        return this.postAFWDataToCloud(this.csrSignUrl + "afw_reg_req_purposekey_api", enrollData);
    }
    
    private JSONObject postAFWDataToCloud(final String postURL, final JSONObject submitJSONObject) throws Exception {
        String responseString = null;
        final JSONObject data = new JSONObject();
        final JSONObject errData = new JSONObject();
        try {
            final CloudAPIDataPost postData = new CloudAPIDataPost();
            postData.postDataToCloud(postURL, submitJSONObject);
            responseString = postData.response;
            if (postData.status == 200) {
                if (JSONUtil.getInstance().isValidJSON(responseString)) {
                    SecureKeyProviderImpl.LOGGER.log(Level.INFO, "AFW  Request Completed Successfully");
                    final JSONObject jsonResponse = new JSONObject(responseString);
                    return jsonResponse;
                }
                SecureKeyProviderImpl.LOGGER.log(Level.SEVERE, "200 received from API but, Respose in not a valid JSONObject ");
                errData.put("ErrorMsg", (Object)I18N.getMsg("dc.mdm.other_error.msg", new Object[0]));
            }
            else {
                SecureKeyProviderImpl.LOGGER.log(Level.INFO, "200 NOT received from API");
                errData.put("ErrorMsg", (Object)I18N.getMsg("mdm.afw.check_connectivity", new Object[] { "https://mdm.manageengine.com" }));
            }
        }
        catch (final Exception ex) {
            SecureKeyProviderImpl.LOGGER.log(Level.SEVERE, "Exception in postAFWDataToCloud", ex);
            errData.put("ErrorMsg", (Object)I18N.getMsg("dc.mdm.other_error.msg", new Object[0]));
        }
        data.put("Status", (Object)"Error");
        data.put("Data", (Object)errData);
        return data;
    }
    
    private JSONObject postDataToCreator(final String action, final JSONObject data) throws JSONException {
        CreatorDataPost.getInstance().resetCodes();
        CreatorDataPost.creator_auth_token = "e75ecc5a22c9f396189820402cea34e8";
        CreatorDataPost.creator_form_name = "AFWInteg";
        CreatorDataPost.creator_owner_name = "desktopcentral1";
        CreatorDataPost.creator_application_name = "afw-msa-tool";
        final ArrayList<String> fieldList = new ArrayList<String>();
        fieldList.add("Action_field");
        fieldList.add("Request_Data");
        fieldList.add("ProductCode");
        CreatorDataPost.xml_field_list = fieldList;
        final JSONObject errData = new JSONObject();
        final String submitContentString = data.toString();
        final List list = new ArrayList();
        list.add(action);
        list.add(submitContentString);
        list.add(ProductUrlLoader.getInstance().getValue("productcode"));
        final List<List> valuesList = new ArrayList<List>();
        valuesList.add(list);
        CreatorDataPost.xml_filed_values = valuesList;
        this.mdmEnrolllogger.log(Level.INFO, "Posting data to creator: {0}", list);
        final int code = CreatorDataPost.getInstance().submitCreatorData();
        if (code == 200) {
            try {
                this.mdmEnrolllogger.log(Level.INFO, "AFW :: Enroll ESA Request Completed Successfully");
                final String resultXML = CreatorDataPost.getResultData();
                this.mdmEnrolllogger.log(Level.INFO, "AFW :: Enroll ESA  Response: {0}", resultXML);
                final Properties properties = this.parseXMLToProperties(resultXML);
                final Long afwReqId = Long.parseLong(((Hashtable<K, String>)properties).get("ID"));
                final String viewURL = "https://creator.zoho.com/api/json/" + CreatorDataPost.creator_application_name + "/view/AFWInteg_Report";
                final String parameters = "authtoken=" + CreatorDataPost.creator_auth_token + "&scope=creatorapi&criteria=(ID=" + String.valueOf(afwReqId) + ")&raw=true&zc_ownername=desktopcentral1";
                final String getURL = viewURL + "?" + parameters;
                final DownloadStatus downloadStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(getURL, (String)null, new SSLValidationType[0]);
                final int responseCode = downloadStatus.getStatus();
                if (responseCode == 0) {
                    final String responseContent = downloadStatus.getUrlDataBuffer();
                    this.mdmEnrolllogger.log(Level.INFO, "Data posted to creator successfully..response: {0}", responseContent);
                    if (JSONUtil.getInstance().isValidJSON(responseContent)) {
                        final JSONObject responseJSONObject = new JSONObject(responseContent);
                        final JSONArray jsonArray = responseJSONObject.getJSONArray("AFWInteg");
                        final JSONObject contentObject = jsonArray.getJSONObject(0);
                        final String responseString = contentObject.get("Response_Data").toString();
                        return new JSONObject(responseString);
                    }
                    errData.put("ErrorMsg", (Object)"Unknown Error. Contact support with logs");
                }
            }
            catch (final Exception ex) {
                errData.put("ErrorMsg", (Object)"Unknown Error. Contact support with logs");
            }
            finally {
                CreatorDataPost.getInstance().resetFields();
                CreatorDataPost.getInstance().resetCodes();
            }
        }
        else {
            errData.put("ErrorMsg", (Object)"Check the connectivity for https://creator.zoho.com and try again");
        }
        data.put("Status", (Object)"Error");
        data.put("Data", (Object)errData);
        return data;
    }
    
    private Properties parseXMLToProperties(final String resultXML) throws ParserConfigurationException, SAXException, IOException {
        final InputStream isr = IOUtils.toInputStream(resultXML);
        final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = dBuilder.parse(isr);
        final Properties prop = new Properties();
        doc.getDocumentElement().normalize();
        final NodeList nList = doc.getElementsByTagName("field");
        for (int temp = 0; temp < nList.getLength(); ++temp) {
            final Node nNode = nList.item(temp);
            if (nNode.getNodeType() == 1) {
                final Element eElement = (Element)nNode;
                prop.setProperty(eElement.getAttribute("name"), eElement.getElementsByTagName("value").item(0).getTextContent());
            }
        }
        return prop;
    }
    
    public JSONObject getFCMAgentNotificationSecret() throws Exception {
        return new IOSFCMNotificationCreatorHandler().getFCMAgentDetails();
    }
    
    public UnifiedAuthenticationService getDefaultUnifiedAuthHandler() {
        return (UnifiedAuthenticationService)new MDMPAPIUnifiedAuthenticationHandler();
    }
    
    static {
        LOGGER = Logger.getLogger(SecureKeyProviderImpl.class.getName());
    }
}
