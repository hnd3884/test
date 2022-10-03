package com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess;

import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.utils.ChromeAgentJSONUtil;
import java.util.logging.Logger;

public abstract class VerifiedAccessManager
{
    public Logger logger;
    ChromeAgentJSONUtil jSONUtil;
    public static final String IS_ATTESTATION_ENABLED = "IsAttestationEnabled";
    public static final String IS_VERIFIED_MODE_ENABLED = "IsVerifiedModeEnabled";
    public static final String IS_ATTESTATION_ENABLED_FOR_CONTENT_PROTECTION = "IsAttestationEnabledForContentProtection";
    public static final String ACCOUNTS_WITH_FULL_CONTROL = "AccountsWithFullControl";
    public static final String ACCOUNTS_WITH_LIMITED_CONTROL = "AccountsWithLimitedControl";
    
    public VerifiedAccessManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.jSONUtil = new ChromeAgentJSONUtil();
    }
    
    public abstract void addAccessControlAccounts(final Context p0, final PayloadResponse p1, final Boolean p2);
    
    public List getFullControlAccessAccounts(final Context context, final String key) {
        List fullControlAccounts = new ArrayList();
        final JSONArray jsonArray = new JSONArray();
        try {
            final JSONArray appliedPayloads = new MDMAgentParamsTableHandler(context).optJSONArray(key);
            JSONArray accountsWithFullControl = new JSONArray();
            this.logger.info("Applied payloads:" + appliedPayloads);
            if (appliedPayloads.length() > 0) {
                for (int i = 0; i < appliedPayloads.length(); ++i) {
                    final String payloadName = String.valueOf(appliedPayloads.get(i));
                    final JSONObject payloadData = new MDMAgentParamsTableHandler(context).getJSONObject(payloadName);
                    final JSONArray fullControlAccounts2 = payloadData.optJSONArray("AccountsWithFullControl");
                    accountsWithFullControl = this.jSONUtil.mergeJSONArray(fullControlAccounts2, jsonArray);
                }
            }
            if (accountsWithFullControl.length() > 0) {
                this.logger.info("Full control Accounts :" + accountsWithFullControl);
                fullControlAccounts = JSONUtil.getInstance().convertJSONArrayTOList(accountsWithFullControl);
            }
        }
        catch (final Exception e) {
            this.logger.info("Exception while getting Accounts With Full Control : " + e);
        }
        return fullControlAccounts;
    }
    
    public List getPartialControlAccessAccounts(final Context context, final String key) {
        List partialControlAccounts = new ArrayList();
        final JSONArray jsonArray = new JSONArray();
        try {
            final JSONArray appliedPayloads = new MDMAgentParamsTableHandler(context).optJSONArray(key);
            JSONArray accountsWithPartialControl = new JSONArray();
            if (appliedPayloads.length() > 0) {
                for (int i = 0; i < appliedPayloads.length(); ++i) {
                    final String payloadName = String.valueOf(appliedPayloads.get(i));
                    final JSONObject payloadData = new MDMAgentParamsTableHandler(context).getJSONObject(payloadName);
                    final JSONArray fullControlAccounts1 = payloadData.optJSONArray("AccountsWithLimitedControl");
                    accountsWithPartialControl = this.jSONUtil.mergeJSONArray(jsonArray, fullControlAccounts1);
                }
            }
            if (accountsWithPartialControl.length() > 0) {
                partialControlAccounts = JSONUtil.getInstance().convertJSONArrayTOList(accountsWithPartialControl);
            }
        }
        catch (final Exception e) {
            this.logger.info("Exception while getting Accounts With Full Control");
        }
        return partialControlAccounts;
    }
    
    public void addPayloadIdentifierToDB(final Context context, final String payloadIdentifierName, final String key) {
        try {
            int i;
            JSONArray appliedPayloadIdentifiers;
            for (i = 0, appliedPayloadIdentifiers = new JSONArray(), appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray(key), i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                if (String.valueOf(appliedPayloadIdentifiers.get(i)).equals(payloadIdentifierName)) {
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
            if (i == appliedPayloadIdentifiers.length()) {
                appliedPayloadIdentifiers.put((Object)payloadIdentifierName);
            }
            new MDMAgentParamsTableHandler(context).addJSONArray(key, appliedPayloadIdentifiers);
        }
        catch (final JSONException e) {
            this.logger.log(Level.INFO, "\" Exception in addPayloadIdentifierToDB :\",", (Throwable)e);
        }
    }
    
    public void removePayloadData(final Context context, final String payloadIdentifierName, final String key) {
        try {
            JSONArray appliedPayloadIdentifiers = new JSONArray();
            final ChromeAgentJSONUtil jSONUtil = new ChromeAgentJSONUtil();
            appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray(key);
            final List appliedPayloadList = jSONUtil.convertJSONArrayTOList(appliedPayloadIdentifiers);
            for (int i = 0; i < appliedPayloadList.size(); ++i) {
                if (appliedPayloadList.get(i).equals(payloadIdentifierName)) {
                    appliedPayloadList.remove(i);
                    new MDMAgentParamsTableHandler(context).addJSONArray(key, jSONUtil.convertListToJSONArray(appliedPayloadList));
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, " Exception in removePayload data :", e);
        }
    }
    
    public String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s + ",");
        }
        return builder.toString();
    }
}
