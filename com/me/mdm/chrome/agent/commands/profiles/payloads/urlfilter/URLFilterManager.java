package com.me.mdm.chrome.agent.commands.profiles.payloads.urlfilter;

import java.util.Iterator;
import java.util.Set;
import com.me.mdm.chrome.agent.utils.ChromeAgentJSONUtil;
import java.io.IOException;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import org.json.JSONObject;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class URLFilterManager
{
    public Logger logger;
    public static final String WHITE_LISTED_URLS = "WhitelistedURLs";
    public static final String BLACK_LISTED_URLS = "BlacklistedURLs";
    public static final String APPLIED_PAYLOAD_IDENTIFIER = "AppliedPayloadIdentifier";
    
    public URLFilterManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public List<String> getWhiteListedURLS(final Context context) {
        this.logger.log(Level.INFO, "Going to get Whitelisted url's");
        int whitelistUrls = 0;
        final JSONArray whitelistedURLinSinglePayload = new JSONArray();
        List<String> whitelistURL = new ArrayList<String>();
        try {
            final JSONArray whitelistedURLsinPayloads = new JSONArray();
            final JSONArray appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedPayloadIdentifier");
            for (int i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                final JSONObject payloadData = new MDMAgentParamsTableHandler(context).getJSONObject(String.valueOf(appliedPayloadIdentifiers.get(i)));
                final JSONArray jsonarray = new JSONArray();
                if (payloadData.optJSONArray("WhitelistedURLs") != null) {
                    for (int j = 0; j < payloadData.optJSONArray("WhitelistedURLs").length(); ++j) {
                        jsonarray.put((Object)String.valueOf(payloadData.optJSONArray("WhitelistedURLs").getJSONObject(j).get("URL")));
                    }
                    if (jsonarray != null) {
                        for (int j = 0; j < jsonarray.length(); ++j) {
                            final String bookmark = jsonarray.optString(j);
                            whitelistedURLinSinglePayload.put(whitelistUrls++, (Object)bookmark);
                        }
                    }
                }
            }
            whitelistURL = JSONUtil.getInstance().convertJSONArrayTOList(whitelistedURLinSinglePayload);
        }
        catch (final JSONException e) {
            this.logger.log(Level.INFO, "Exception in getting Bwhitelisted URLs :", (Throwable)e);
        }
        return whitelistURL;
    }
    
    public List<String> getBlackListedURLS(final Context context) {
        this.logger.log(Level.INFO, "Going to get Blacklisted url's");
        int blacklistUrls = 0;
        final JSONArray blacklistedURLinSinglePayload = new JSONArray();
        List<String> blacklistURL = new ArrayList<String>();
        try {
            final JSONArray blacklistedURLsinPayloads = new JSONArray();
            final JSONArray appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedPayloadIdentifier");
            for (int i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                final JSONObject payloadData = new MDMAgentParamsTableHandler(context).getJSONObject(String.valueOf(appliedPayloadIdentifiers.get(i)));
                final JSONArray jsonarray = payloadData.optJSONArray("BlacklistedURLs");
                if (jsonarray != null) {
                    for (int j = 0; j < jsonarray.length(); ++j) {
                        final String bookmark = jsonarray.optString(j);
                        blacklistedURLinSinglePayload.put(blacklistUrls++, (Object)bookmark);
                    }
                }
            }
            blacklistURL = JSONUtil.getInstance().convertJSONArrayTOList(blacklistedURLinSinglePayload);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in getting BlacklistedURLs :", e);
        }
        return blacklistURL;
    }
    
    public void addPayloadIdentifierToDB(final Context context, final String payloadIdentifierName) {
        try {
            int i;
            JSONArray appliedPayloadIdentifiers;
            for (i = 0, appliedPayloadIdentifiers = new JSONArray(), appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedPayloadIdentifier"), i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                if (String.valueOf(appliedPayloadIdentifiers.get(i)).equals(payloadIdentifierName)) {
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
            if (i == appliedPayloadIdentifiers.length()) {
                appliedPayloadIdentifiers.put((Object)payloadIdentifierName);
            }
            new MDMAgentParamsTableHandler(context).addJSONArray("AppliedPayloadIdentifier", appliedPayloadIdentifiers);
        }
        catch (final JSONException e) {
            this.logger.log(Level.INFO, " Exception in addPayloadIdentifierToDB :\",", (Throwable)e);
        }
    }
    
    public void applyURLFilterPolicy(final Context context) throws IOException {
        final List<String> whitelistURLs = this.getWhiteListedURLS(context);
        final List<String> blacklistURLs = this.getBlackListedURLS(context);
        if (blacklistURLs.size() > 0 || whitelistURLs.size() > 0) {
            GoogleChromeAPIWrapper.initiateUserPolicy();
            this.setURLFilterPolicy(whitelistURLs, blacklistURLs);
            GoogleChromeAPIWrapper.updateUserPolicy(context);
        }
    }
    
    public void setURLFilterPolicy(final List<String> whitelistURLs, final List<String> blacklistURLs) {
        if (blacklistURLs.size() > 0) {
            GoogleChromeAPIWrapper.setBlacklistURLs(blacklistURLs);
        }
        if (whitelistURLs.size() > 0) {
            whitelistURLs.add("chrome://*");
            GoogleChromeAPIWrapper.setWhiteListURLs(whitelistURLs);
            final List<String> blackListAllUrls = new ArrayList<String>();
            blackListAllUrls.add("*");
            GoogleChromeAPIWrapper.setBlacklistURLs(blackListAllUrls);
        }
    }
    
    public void revertURLFilterPolicy(final Context context) throws IOException {
        GoogleChromeAPIWrapper.getUserPolicy(context);
        GoogleChromeAPIWrapper.revertBlacklistURLs();
        GoogleChromeAPIWrapper.revertWhiteListURLs();
        GoogleChromeAPIWrapper.updateUserPolicy(context);
    }
    
    public void removePayloadData(final Context context, final String payloadIdentifierName) {
        try {
            JSONArray appliedPayloadIdentifiers = new JSONArray();
            final ChromeAgentJSONUtil jSONUtil = new ChromeAgentJSONUtil();
            appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedPayloadIdentifier");
            final List<String> appliedPayloadList = jSONUtil.convertJSONArrayTOList(appliedPayloadIdentifiers);
            for (int i = 0; i < appliedPayloadList.size(); ++i) {
                if (appliedPayloadList.get(i).equals(payloadIdentifierName)) {
                    appliedPayloadList.remove(i);
                    new MDMAgentParamsTableHandler(context).addJSONArray("AppliedPayloadIdentifier", jSONUtil.convertListToJSONArray(appliedPayloadList));
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, " Exception in removePayload data :", e);
        }
    }
    
    private String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s);
        }
        return builder.toString();
    }
}
