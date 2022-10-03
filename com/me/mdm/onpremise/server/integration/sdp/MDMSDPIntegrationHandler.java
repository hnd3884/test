package com.me.mdm.onpremise.server.integration.sdp;

import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.net.URLConnection;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.Level;
import java.net.URLEncoder;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Properties;
import java.util.logging.Logger;

public class MDMSDPIntegrationHandler
{
    private static final Logger SDPINTEGLOGGER;
    private static MDMSDPIntegrationHandler mdmInvDataPost;
    
    public static MDMSDPIntegrationHandler getInstance() {
        if (MDMSDPIntegrationHandler.mdmInvDataPost == null) {
            MDMSDPIntegrationHandler.mdmInvDataPost = new MDMSDPIntegrationHandler();
        }
        return MDMSDPIntegrationHandler.mdmInvDataPost;
    }
    
    public void updateAssetOwnerToSDP(final String ciType, final String strUDID, String assetName, String userName, final String authenticationKey, final String apiurlforuser) {
        try {
            final Properties propReq = new Properties();
            propReq.setProperty("AUTHENTICATION_KEY", authenticationKey);
            propReq.setProperty("OPERATION_NAME", "update");
            propReq.setProperty("TYPE_OF_OPERATION", "updateasset");
            propReq.setProperty("IS_NOT_TRAIL", String.valueOf(true));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();
            final Element apiElement = MDMSDPIntegrationUtil.getInstance().createAPIElement(doc);
            final Element ciTypeElement = MDMSDPIntegrationUtil.getInstance().createCITypeElement(doc, ciType);
            apiElement.appendChild(ciTypeElement);
            assetName = StringEscapeUtils.escapeXml(assetName);
            assetName = URLEncoder.encode(assetName, "UTF-8");
            final Element criteriasElement = MDMSDPIntegrationUtil.getInstance().createCriteriaElement(doc, strUDID);
            ciTypeElement.appendChild(criteriasElement);
            userName = StringEscapeUtils.escapeXml(userName);
            userName = URLEncoder.encode(userName, "UTF-8");
            final Element newValueElement = MDMSDPIntegrationUtil.getInstance().createNewOwnerValueElement(doc, userName);
            ciTypeElement.appendChild(newValueElement);
            final String sXMLContent = MDMSDPIntegrationUtil.getInstance().DocToString(doc.getFirstChild());
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Data to be posted for User Mapping in SDP - {0}", sXMLContent);
            final URLConnection conn = MDMSDPIntegrationUtil.getInstance().createSDPURLConnection(apiurlforuser, "NO_CONTENT_TYPE", true, true, true);
            final String responsefromSdp = MDMSDPIntegrationUtil.getInstance().requestSDP(conn, doc, propReq);
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP for User Mapping - {0}", responsefromSdp);
            final boolean sdpStatus = MDMSDPIntegrationUtil.getInstance().retrieveStatusFromJSONResponse(responsefromSdp, propReq);
            MDMSDPIntegrationUtil.getInstance().addSDPAccesslog("Update Asset Owner", strUDID, sdpStatus);
        }
        catch (final Exception e) {
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while updating Asset Owner to SDP - {0}", e);
        }
    }
    
    public void updateAssetStatusToSDP(final String ciType, final List assetUDIDList, final String assetStatus) {
        try {
            final String authenticationKey = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
            final String apiurl = MDMSDPIntegrationUtil.getInstance().getServiceDeskBaseURL() + "/api/cmdb/ci";
            String strUDID = null;
            final Properties propReq = new Properties();
            propReq.setProperty("AUTHENTICATION_KEY", authenticationKey);
            propReq.setProperty("OPERATION_NAME", "update");
            propReq.setProperty("TYPE_OF_OPERATION", "updateasset");
            propReq.setProperty("IS_NOT_TRAIL", String.valueOf(true));
            for (final Object assetUDID : assetUDIDList) {
                strUDID = String.valueOf(assetUDID);
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document doc = builder.newDocument();
                final Element apiElement = MDMSDPIntegrationUtil.getInstance().createAPIElement(doc);
                final Element ciTypeElement = MDMSDPIntegrationUtil.getInstance().createCITypeElement(doc, ciType);
                apiElement.appendChild(ciTypeElement);
                final Element newValueElement = MDMSDPIntegrationUtil.getInstance().createNewValueElement(doc, assetStatus);
                ciTypeElement.appendChild(newValueElement);
                final Element criteriasElement = MDMSDPIntegrationUtil.getInstance().createCriteriaElement(doc, strUDID);
                ciTypeElement.appendChild(criteriasElement);
                final String sXMLContent = MDMSDPIntegrationUtil.getInstance().DocToString(doc.getFirstChild());
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Data to be posted to update Asset Status of a device in SDP - {0}", sXMLContent);
                final URLConnection conn = MDMSDPIntegrationUtil.getInstance().createSDPURLConnection(apiurl, "NO_CONTENT_TYPE", true, true, true);
                final String responsefromSdp = MDMSDPIntegrationUtil.getInstance().requestSDP(conn, doc, propReq);
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP for Asset Status update - {0}", responsefromSdp);
                final boolean sdpStatus = MDMSDPIntegrationUtil.getInstance().retrieveStatusFromJSONResponse(responsefromSdp, propReq);
                MDMSDPIntegrationUtil.getInstance().addSDPAccesslog("Update Asset Status", strUDID, sdpStatus);
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while updating Asset Status to SDP - {0}", e);
        }
    }
    
    public void deleteAssetfromSDP(final String ciType, final List assetUDIDList) {
        try {
            final String authenticationKey = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
            final String apiurl = MDMSDPIntegrationUtil.getInstance().getServiceDeskBaseURL() + "/api/cmdb/ci";
            String strUDID = null;
            final Properties propReq = new Properties();
            propReq.setProperty("AUTHENTICATION_KEY", authenticationKey);
            propReq.setProperty("OPERATION_NAME", "delete");
            propReq.setProperty("TYPE_OF_OPERATION", "deleteasset");
            propReq.setProperty("IS_NOT_TRAIL", String.valueOf(true));
            for (final Object assetUDID : assetUDIDList) {
                strUDID = String.valueOf(assetUDID);
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document doc = builder.newDocument();
                final Element apiElement = MDMSDPIntegrationUtil.getInstance().createAPIElement(doc);
                final Element ciTypeElement = MDMSDPIntegrationUtil.getInstance().createCITypeElement(doc, ciType);
                apiElement.appendChild(ciTypeElement);
                final Element criteriasElement = MDMSDPIntegrationUtil.getInstance().createCriteriaElement(doc, String.valueOf(assetUDID));
                ciTypeElement.appendChild(criteriasElement);
                final String sXMLContent = MDMSDPIntegrationUtil.getInstance().DocToString(doc.getFirstChild());
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Data to be posted to delete an Asset in SDP - {0}", sXMLContent);
                final URLConnection conn = MDMSDPIntegrationUtil.getInstance().createSDPURLConnection(apiurl, "NO_CONTENT_TYPE", true, true, true);
                final String responsefromSdp = MDMSDPIntegrationUtil.getInstance().requestSDP(conn, doc, propReq);
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP to delete an Asset - {0}", responsefromSdp);
                final boolean sdpStatus = MDMSDPIntegrationUtil.getInstance().retrieveStatusFromJSONResponse(responsefromSdp, propReq);
                MDMSDPIntegrationUtil.getInstance().addSDPAccesslog("Delete Asset", strUDID, sdpStatus);
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while deleting Asset Status to SDP -{0}", e);
        }
    }
    
    public Map getSDPRequesterDetailsV3(final String domainName, final String userName, final String authenticationKey, String apiURL) {
        final Map responseMap = new HashMap();
        try {
            final JSONObject searchJson = new JSONObject();
            searchJson.put("field", (Object)"login_name");
            searchJson.put("condition", (Object)"EQ");
            searchJson.put("value", (Object)userName);
            final JSONObject domainJson = new JSONObject();
            domainJson.put("field", (Object)"domain.name");
            domainJson.put("condition", (Object)"EQ");
            domainJson.put("logical_operator", (Object)"AND");
            domainJson.put("value", (Object)domainName);
            final JSONArray domainArray = new JSONArray();
            domainArray.put((Object)domainJson);
            searchJson.put("children", (Object)domainArray);
            final JSONObject critJSON = new JSONObject();
            final JSONObject listJSON = new JSONObject();
            listJSON.put("search_criteria", (Object)searchJson);
            final JSONArray fieldArray = new JSONArray();
            fieldArray.put((Object)"name");
            critJSON.put("fields_required", (Object)fieldArray);
            critJSON.put("list_info", (Object)listJSON);
            final String critString = URLEncoder.encode(critJSON.toString(), "UTF-8");
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Url for getting login user name{0}", apiURL);
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Criteria for getting login user name{0}", critString);
            CustomerInfoUtil.getInstance();
            final Boolean isMDMP = CustomerInfoUtil.isMDMP();
            String techKey = authenticationKey;
            if (!isMDMP) {
                techKey = Encoder.convertFromBase(authenticationKey);
            }
            apiURL = apiURL + "?" + "TECHNICIAN_KEY" + "=" + techKey + "&input_data=" + critString + "&PORTALID=1";
            final String responsefromSDP = MDMSDPIntegrationUtil.getInstance().requestSDP(apiURL, "GET");
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Responce recevied from SDP Getlogin username method{0}", responsefromSDP);
            if (responsefromSDP != null && responsefromSDP.contains("response_status")) {
                final JSONObject responseJson = new JSONObject(responsefromSDP);
                final JSONArray responceArray = responseJson.getJSONArray("response_status");
                final JSONObject statusJson = responceArray.getJSONObject(0);
                final int status_code = (int)statusJson.get("status_code");
                if (status_code == 2000) {
                    final JSONArray userArray = responseJson.getJSONArray("users");
                    if (userArray.length() > 0) {
                        final JSONObject userJson = userArray.getJSONObject(0);
                        final String user = userJson.getString("name");
                        responseMap.put("username", user);
                    }
                }
            }
            if (responsefromSDP != null && responsefromSDP.equalsIgnoreCase("SDPNotReached")) {
                responseMap.put("message", "SDPNotReached");
            }
        }
        catch (final Exception e) {
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while forming the crriteria  ", e);
            return null;
        }
        return responseMap;
    }
    
    public String getSDPUserNameForUserName(final String userName, final String domainName, final String authenticationKey, final String apiURL) {
        String sdpUserName = null;
        final String loginName = MDMSDPIntegrationUtil.getInstance().getSDPMappedLoginName(userName, domainName);
        try {
            Map responseMap = null;
            if (apiURL.contains("v3")) {
                responseMap = this.getSDPRequesterDetailsV3(domainName, userName, authenticationKey, apiURL);
            }
            else {
                final String operationName = "GET_ALL";
                final Properties propReq = new Properties();
                propReq.setProperty("AUTHENTICATION_KEY", authenticationKey);
                propReq.setProperty("OPERATION_NAME", operationName);
                propReq.setProperty("IS_NOT_TRAIL", String.valueOf(true));
                final Document mappedUsersDoc = MDMSDPIntegrationUtil.getInstance().createMappedUsersElement(userName, domainName);
                final String sXMLContent = MDMSDPIntegrationUtil.getInstance().DocToString(mappedUsersDoc.getFirstChild());
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Data to be posted to get all mapped Users from SDP - {0}", sXMLContent);
                final URLConnection conn = MDMSDPIntegrationUtil.getInstance().createSDPURLConnection(apiURL, "NO_CONTENT_TYPE", true, true, true);
                final String responsefromSdp = MDMSDPIntegrationUtil.getInstance().requestSDP(conn, mappedUsersDoc, propReq);
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Response from SDP to get all mapped users - {0}", responsefromSdp);
                responseMap = MDMSDPIntegrationUtil.getInstance().retrieveStatusFromResponseMap(responsefromSdp);
            }
            sdpUserName = responseMap.get("username");
            final String message = responseMap.get("message");
            if (sdpUserName != null && sdpUserName.trim().length() != 0) {
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "SDP UserName : {0} for LoginName : {1} ", new Object[] { sdpUserName, loginName });
                return sdpUserName;
            }
            if (message.equalsIgnoreCase("SDPNotReached") || message.equalsIgnoreCase("API key received is not associated to any technician. Authentication failed.")) {
                MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, " Do not communicate with SDP,SDP is not reachable or Invalid API Key ");
            }
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "SDP User Details Received - UserName  : {0} and LoginName : {1} ", new Object[] { sdpUserName, loginName, domainName });
        }
        catch (final Exception e) {
            MDMSDPIntegrationHandler.SDPINTEGLOGGER.log(Level.INFO, "Exception in getting SDP user name: {0}", e);
        }
        return sdpUserName;
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSDPIntegrationHandler.mdmInvDataPost = null;
    }
}
