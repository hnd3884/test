package com.me.idps.core.service.okta;

import org.json.JSONArray;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.httpclient.DMHttpResponse;
import com.me.devicemanagement.framework.server.httpclient.DMHttpRequest;

public class OktaAPIs
{
    private static String relayState;
    private Boolean multiOptionalFactorEnroll;
    private Boolean warnBeforePasswordExpired;
    private static String authnApi;
    private static String usersApi;
    private static String usersMeApi;
    private static String groupAPI;
    
    public OktaAPIs() {
        this.multiOptionalFactorEnroll = false;
        this.warnBeforePasswordExpired = false;
    }
    
    public DMHttpResponse executeDMHttpRequest(final DMHttpRequest dmHttpRequest) throws JSONException {
        IDPSlogger.SOM.log(Level.FINEST, "hitting url : " + dmHttpRequest.url);
        final DMHttpResponse dmHttpResponse = SyMUtil.executeDMHttpRequest(dmHttpRequest);
        return dmHttpResponse;
    }
    
    private DMHttpResponse processRequest(final String api_token, final String url, final String method, final JSONObject urlParams, final JSONObject body) throws JSONException {
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = url;
        final JSONObject headers = new JSONObject();
        headers.put("Content-Type", (Object)"application/json");
        headers.put("Authorization", (Object)("SSWS " + api_token));
        dmHttpRequest.headers = headers;
        if (body != null) {
            headers.put("Content-Length", body.toString().getBytes().length);
            dmHttpRequest.data = body.toString().getBytes(StandardCharsets.UTF_8);
        }
        dmHttpRequest.method = method;
        if (urlParams != null) {
            dmHttpRequest.parameters = urlParams;
        }
        return this.executeDMHttpRequest(dmHttpRequest);
    }
    
    public Boolean isValidPassword(final String domainName, final String username, final String password) throws Throwable {
        final JSONObject body = new JSONObject();
        body.put("username", (Object)username);
        body.put("password", (Object)password);
        body.put("relayState", (Object)OktaAPIs.relayState);
        final JSONObject options = new JSONObject();
        options.put("multiOptionalFactorEnroll", (Object)this.multiOptionalFactorEnroll);
        options.put("warnBeforePasswordExpired", (Object)this.warnBeforePasswordExpired);
        body.put("options", (Object)options);
        final String domainUrl = OktaAPIs.authnApi.replace("$", domainName);
        final DMHttpRequest dmHttpRequest = new DMHttpRequest();
        dmHttpRequest.url = domainUrl;
        dmHttpRequest.data = body.toString().getBytes(StandardCharsets.UTF_8);
        dmHttpRequest.method = "POST";
        final JSONObject headers = new JSONObject();
        headers.put("Content-Length", body.toString().getBytes().length);
        headers.put("Content-Type", (Object)"application/json");
        dmHttpRequest.headers = headers;
        final DMHttpResponse response = this.executeDMHttpRequest(dmHttpRequest);
        final String responseString = response.responseBodyAsString;
        final JSONObject responseJSONbject = (responseString != null) ? new JSONObject(responseString) : new JSONObject();
        if (responseJSONbject.has("status") && responseJSONbject.get("status").equals("SUCCESS")) {
            return true;
        }
        return false;
    }
    
    public JSONObject getUsersMe(final String domainName, final String api_token) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.usersMeApi.replace("$", domainName);
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", null, null);
        final String responseString = response.responseBodyAsString;
        final JSONObject responseJSON = (responseString != null) ? new JSONObject(responseString) : new JSONObject();
        return responseJSON;
    }
    
    private String getAfterLink(final JSONObject responseHeaders) throws JSONException {
        String afterLink = null;
        String key = "";
        if (responseHeaders.has("link")) {
            key = "link";
        }
        else if (responseHeaders.has("Link")) {
            key = "Link";
        }
        if (responseHeaders.has(key)) {
            final String j = String.valueOf(responseHeaders.get(key));
            final String[] k = j.split(";");
            if (k.length >= 1 && k[1].contains("next")) {
                afterLink = k[0].replaceAll("<", "");
                afterLink = afterLink.replaceAll(">", "").trim();
                final String[] url = afterLink.split("\\?");
                final String[] para = url[1].split("&");
                for (int i = 0; i < para.length; ++i) {
                    if (para[i].contains("after")) {
                        afterLink = para[i].split("=")[1];
                        break;
                    }
                }
            }
        }
        return afterLink;
    }
    
    public JSONObject getUsers(final String domainName, final String api_token, final JSONObject urlParams) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.usersApi.replace("$", domainName);
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", urlParams, null);
        final String afterLink = this.getAfterLink(response.responseHeaders);
        final String responseString = response.responseBodyAsString;
        JSONArray responseJSONArray = null;
        JSONObject errorObj = null;
        if (!responseString.isEmpty() && responseString.charAt(0) == '[') {
            responseJSONArray = new JSONArray(responseString);
        }
        else {
            errorObj = new JSONObject(responseString);
        }
        final JSONObject responseWithHeader = new JSONObject();
        responseWithHeader.put("Link", (Object)afterLink);
        if (responseJSONArray != null) {
            responseWithHeader.put("response", (Object)responseJSONArray);
        }
        else {
            responseWithHeader.put("error", (Object)errorObj);
        }
        return responseWithHeader;
    }
    
    public JSONObject getUser(final String domainName, final String api_token, final String userId) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.usersApi.replace("$", domainName);
        }
        domainUrl = domainUrl + "/" + userId;
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", null, null);
        final String responseString = response.responseBodyAsString;
        JSONObject responseJSONObject = null;
        JSONObject errorObj = null;
        final JSONObject responseWithHeader = new JSONObject();
        if (!responseString.isEmpty() && responseString.charAt(0) == '{') {
            responseJSONObject = new JSONObject(responseString);
        }
        if (responseJSONObject.has("errorCode")) {
            errorObj = responseJSONObject;
        }
        if (responseJSONObject != null) {
            responseWithHeader.put("response", (Object)responseJSONObject);
        }
        else {
            responseWithHeader.put("error", (Object)errorObj);
        }
        return responseWithHeader;
    }
    
    public JSONObject getGroup(final String domainName, final String api_token, final String groupId) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.groupAPI.replace("$", domainName);
        }
        domainUrl = domainUrl + "/" + groupId;
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", null, null);
        final String responseString = response.responseBodyAsString;
        JSONObject responseJSONObject = null;
        JSONObject errorObj = null;
        final JSONObject responseWithHeader = new JSONObject();
        if (!responseString.isEmpty() && responseString.charAt(0) == '{') {
            responseJSONObject = new JSONObject(responseString);
        }
        if (responseJSONObject.has("errorCode")) {
            errorObj = responseJSONObject;
        }
        if (responseJSONObject != null) {
            responseWithHeader.put("response", (Object)responseJSONObject);
        }
        else {
            responseWithHeader.put("error", (Object)errorObj);
        }
        return responseWithHeader;
    }
    
    public JSONObject getGroups(final String domainName, final String api_token, final JSONObject urlParams) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.groupAPI.replace("$", domainName);
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", urlParams, null);
        final String afterLink = this.getAfterLink(response.responseHeaders);
        final String responseString = response.responseBodyAsString;
        JSONArray responseJSONArray = null;
        JSONObject errorObj = null;
        if (!responseString.isEmpty() && responseString.charAt(0) == '[') {
            responseJSONArray = new JSONArray(responseString);
        }
        else {
            errorObj = new JSONObject(responseString);
        }
        final JSONObject responseWithHeader = new JSONObject();
        responseWithHeader.put("Link", (Object)afterLink);
        if (responseJSONArray != null) {
            responseWithHeader.put("response", (Object)responseJSONArray);
        }
        else {
            responseWithHeader.put("error", (Object)errorObj);
        }
        return responseWithHeader;
    }
    
    public JSONObject createUser(final String domainName, final JSONObject userObject, final String api_token) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.usersApi.replace("$", domainName);
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "POST", null, userObject);
        final String responseString = response.responseBodyAsString;
        return new JSONObject(responseString);
    }
    
    public JSONObject createGroup(final String domainName, final JSONObject groupObject, final String api_token) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.groupAPI.replace("$", domainName);
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "POST", null, groupObject);
        final String responseString = response.responseBodyAsString;
        return new JSONObject(responseString);
    }
    
    public boolean addUserToGroup(final String domainName, final String groupId, final String userId, final String api_token) throws Exception {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.groupAPI.replace("$", domainName);
            domainUrl = domainUrl + "/" + groupId + "/users/" + userId;
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "PUT", null, null);
        return response.status == 204;
    }
    
    public boolean removeUserToGroup(final String domainName, final String groupId, final String userId, final String api_token) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty()) {
            domainUrl = OktaAPIs.groupAPI.replace("$", domainName);
            domainUrl = domainUrl + "/" + groupId + "/users/" + userId;
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "DELETE", null, null);
        return response.status == 204;
    }
    
    public JSONObject getGroupUsers(final String domainName, final String groupId, final String api_token, final JSONObject urlParams) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty() && !groupId.isEmpty()) {
            domainUrl = OktaAPIs.groupAPI.replace("$", domainName);
            domainUrl = domainUrl + "/" + groupId + "/users";
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", urlParams, null);
        final String afterLink = this.getAfterLink(response.responseHeaders);
        final String responseString = response.responseBodyAsString;
        JSONArray responseJSONArray = null;
        JSONObject errorObj = null;
        if (!responseString.isEmpty() && responseString.charAt(0) == '[') {
            responseJSONArray = new JSONArray(responseString);
        }
        else {
            errorObj = new JSONObject(responseString);
        }
        final JSONObject responseWithHeader = new JSONObject();
        responseWithHeader.put("Link", (Object)afterLink);
        if (responseJSONArray != null) {
            responseWithHeader.put("response", (Object)responseJSONArray);
        }
        else {
            responseWithHeader.put("error", (Object)errorObj);
        }
        return responseWithHeader;
    }
    
    public JSONObject getUsersGroups(final String domainName, final String userId, final String api_token, final JSONObject urlParams) throws JSONException {
        String domainUrl = new String();
        if (domainName != null && !domainName.isEmpty() && !userId.isEmpty()) {
            domainUrl = OktaAPIs.usersApi.replace("$", domainName);
            domainUrl = domainUrl + "/" + userId + "/groups";
        }
        final DMHttpResponse response = this.processRequest(api_token, domainUrl, "GET", urlParams, null);
        final String afterLink = this.getAfterLink(response.responseHeaders);
        final String responseString = response.responseBodyAsString;
        JSONArray responseJSONArray = null;
        JSONObject errorObj = null;
        if (!responseString.isEmpty() && responseString.charAt(0) == '[') {
            responseJSONArray = new JSONArray(responseString);
        }
        else {
            errorObj = new JSONObject(responseString);
        }
        final JSONObject responseWithHeader = new JSONObject();
        responseWithHeader.put("Link", (Object)afterLink);
        if (responseJSONArray != null) {
            responseWithHeader.put("response", (Object)responseJSONArray);
        }
        else {
            responseWithHeader.put("error", (Object)errorObj);
        }
        return responseWithHeader;
    }
    
    static {
        OktaAPIs.relayState = "relaystate";
        OktaAPIs.authnApi = "https://$/api/v1/authn";
        OktaAPIs.usersApi = "https://$/api/v1/users";
        OktaAPIs.usersMeApi = "https://$/api/v1/users/me";
        OktaAPIs.groupAPI = "https://$/api/v1/groups";
    }
}
