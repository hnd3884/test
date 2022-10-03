package com.me.mdm.chrome.agent.commands.profiles.payloads.ExtensionInstallSources;

import java.util.Iterator;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.ExtensionInstallSources;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.chrome.agent.utils.ChromeAgentJSONUtil;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import org.json.JSONArray;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class ExtensionInstallSourceManager
{
    public Logger logger;
    public static final String EXTENSION_INSTALL_SOURCES_PAYLOADS = "ExtensionInstallSourcesPayloads";
    
    public ExtensionInstallSourceManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public void addPayloadIdentifierToDB(final Context context, final String payloadIdentifierName) {
        try {
            int i;
            JSONArray appliedPayloadIdentifiers;
            for (i = 0, appliedPayloadIdentifiers = new JSONArray(), appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("ExtensionInstallSourcesPayloads"), i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                if (String.valueOf(appliedPayloadIdentifiers.get(i)).equals(payloadIdentifierName)) {
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
            if (i == appliedPayloadIdentifiers.length()) {
                appliedPayloadIdentifiers.put((Object)payloadIdentifierName);
            }
            new MDMAgentParamsTableHandler(context).addJSONArray("ExtensionInstallSourcesPayloads", appliedPayloadIdentifiers);
        }
        catch (final JSONException e) {
            this.logger.log(Level.INFO, "\" Exception in addPayloadIdentifierToDB :\",", (Throwable)e);
        }
    }
    
    public List getExtensionURLS(final Context context) {
        JSONArray allExtensionInstallSources = new JSONArray();
        List extensionURLS = new ArrayList();
        try {
            final JSONArray appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("ExtensionInstallSourcesPayloads");
            for (int i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                final JSONArray payloadURLs = new MDMAgentParamsTableHandler(context).getJSONArray(appliedPayloadIdentifiers.get(i).toString());
                allExtensionInstallSources = new ChromeAgentJSONUtil().mergeJSONArray(payloadURLs, allExtensionInstallSources);
            }
            this.logger.info("Allowed Extension URL's :" + allExtensionInstallSources);
            extensionURLS = JSONUtil.getInstance().convertJSONArrayTOList(allExtensionInstallSources);
        }
        catch (final Exception e) {
            this.logger.info("Exception while fetching Extension URL's" + e);
        }
        return extensionURLS;
    }
    
    public void removePayloadData(final Context context, final String payloadIdentifierName) {
        try {
            JSONArray appliedPayloadIdentifiers = new JSONArray();
            final ChromeAgentJSONUtil jSONUtil = new ChromeAgentJSONUtil();
            appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("ExtensionInstallSourcesPayloads");
            this.logger.info("Applied Payload Identifiers :" + appliedPayloadIdentifiers);
            for (int i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                if (appliedPayloadIdentifiers.get(i).toString().equals(payloadIdentifierName)) {
                    appliedPayloadIdentifiers = JSONUtil.getInstance().removeByPos(appliedPayloadIdentifiers, i);
                    this.logger.info("List : " + appliedPayloadIdentifiers);
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    new MDMAgentParamsTableHandler(context).addJSONArray("ExtensionInstallSourcesPayloads", appliedPayloadIdentifiers);
                    this.logger.info("" + new MDMAgentParamsTableHandler(context).optJSONArray("ExtensionInstallSourcesPayloads"));
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, " Exception in removePayload data :", e);
        }
    }
    
    public void applyExtensionInstallSources(final Context context, final PayloadResponse payloadResp, final Boolean isProfileInstall) {
        try {
            final List urls = this.getExtensionURLS(context);
            final UserPolicy userPolicy = new UserPolicy();
            final ExtensionInstallSources extensionInstallSources = new ExtensionInstallSources();
            extensionInstallSources.setUrls(urls);
            userPolicy.setExtensionInstallSources(extensionInstallSources);
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while applying Extension install sources : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, isProfileInstall);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
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
