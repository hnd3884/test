package com.me.mdm.core.dataprotection.windows;

import org.json.JSONException;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import com.me.mdm.core.dataprotection.data.AppRule;
import com.me.mdm.core.dataprotection.data.NetworkRule;
import com.me.mdm.core.dataprotection.data.EnterpriseRule;
import java.util.ArrayList;
import com.me.mdm.core.dataprotection.data.EnterprisePolicy;
import org.json.JSONObject;
import com.me.mdm.core.dataprotection.DataPolicyHandler;

public class WindowsDataPolicyHandler extends DataPolicyHandler
{
    public static final int APP_CONST = 101;
    
    @Override
    protected EnterprisePolicy convertApiJsonToPolicy(final JSONObject jsonObject) throws Exception {
        final EnterprisePolicy enterprisePolicy = new EnterprisePolicy();
        final String policyName = String.valueOf(jsonObject.get("policy_name"));
        final String policyDescription = jsonObject.optString("policy_desc", "");
        final JSONArray allowedApps = jsonObject.getJSONArray("allowed_apps");
        final String primaryDomain = String.valueOf(jsonObject.get("primary_domain_name"));
        final JSONArray otherDomians = jsonObject.optJSONArray("other_domain_names");
        final JSONArray protectedDomains = jsonObject.getJSONArray("protected_domain_names");
        final JSONArray enterpriseIPRange = jsonObject.getJSONArray("enterprise_ip_range");
        final JSONArray enterpriseCloudResources = jsonObject.optJSONArray("enterprise_cloud_resources");
        final JSONArray neutralResources = jsonObject.optJSONArray("neutral_resources");
        final JSONArray inernalProxyServer = jsonObject.optJSONArray("internal_proxy_server");
        final JSONArray proxyServer = jsonObject.optJSONArray("proxy_server");
        final Integer enforcementLevel = jsonObject.getInt("enforcement_level");
        final Boolean allowUserDecrypt = jsonObject.optBoolean("allow_user_decryption", true);
        final Long dataCertID = jsonObject.getLong("data_recovery_cert_id");
        enterprisePolicy.policyName = policyName;
        enterprisePolicy.policyDescription = policyDescription;
        final List<EnterpriseRule> ruleList = new ArrayList<EnterpriseRule>();
        final NetworkRule primaryDomainrule = new NetworkRule(primaryDomain, 1);
        ruleList.add(primaryDomainrule);
        for (int i = 0; i < protectedDomains.length(); ++i) {
            final NetworkRule protectedDomain = new NetworkRule(String.valueOf(protectedDomains.get(i)), 3);
            ruleList.add(protectedDomain);
        }
        if (otherDomians != null) {
            for (int i = 0; i < otherDomians.length(); ++i) {
                final NetworkRule otherDomain = new NetworkRule(String.valueOf(otherDomians.get(i)), 2);
                ruleList.add(otherDomain);
            }
        }
        for (int i = 0; i < enterpriseIPRange.length(); ++i) {
            final NetworkRule otherDomain = new NetworkRule(String.valueOf(enterpriseIPRange.get(i)), 5);
            ruleList.add(otherDomain);
        }
        if (enterpriseCloudResources != null) {
            for (int i = 0; i < enterpriseCloudResources.length(); ++i) {
                final NetworkRule otherDomain = new NetworkRule(String.valueOf(enterpriseCloudResources.get(i)), 4);
                ruleList.add(otherDomain);
            }
        }
        if (inernalProxyServer != null) {
            for (int i = 0; i < inernalProxyServer.length(); ++i) {
                final NetworkRule otherDomain = new NetworkRule(String.valueOf(inernalProxyServer.get(i)), 6);
                ruleList.add(otherDomain);
            }
        }
        if (neutralResources != null) {
            for (int i = 0; i < neutralResources.length(); ++i) {
                final NetworkRule otherDomain = new NetworkRule(String.valueOf(neutralResources.get(i)), 8);
                ruleList.add(otherDomain);
            }
        }
        if (proxyServer != null) {
            for (int i = 0; i < proxyServer.length(); ++i) {
                final NetworkRule otherDomain = new NetworkRule(String.valueOf(proxyServer.get(i)), 7);
                ruleList.add(otherDomain);
            }
        }
        for (int i = 0; i < allowedApps.length(); ++i) {
            final JSONObject app = allowedApps.getJSONObject(i);
            final String appIdentifier = String.valueOf(app.get("app_identifier"));
            final Boolean isAllowed = app.optBoolean("is_allowed", (boolean)Boolean.TRUE);
            final Integer appType = app.optInt("app_type", 1);
            final AppRule allowedApp = new AppRule(appIdentifier, appType, isAllowed);
            ruleList.add(allowedApp);
        }
        final WindowsConfigRule windowsConfigRule = new WindowsConfigRule(enforcementLevel, allowUserDecrypt, dataCertID);
        ruleList.add(windowsConfigRule);
        enterprisePolicy.rules = ruleList;
        return enterprisePolicy;
    }
    
    @Override
    protected JSONObject convertEnterprisePolicyToApiJSON(final EnterprisePolicy enterprisePolicy) throws Exception {
        final HashMap constToKey = new HashMap();
        constToKey.put(4, "enterprise_cloud_resources");
        constToKey.put(6, "internal_proxy_server");
        constToKey.put(5, "enterprise_ip_range");
        constToKey.put(8, "neutral_resources");
        constToKey.put(2, "other_domain_names");
        constToKey.put(1, "primary_domain_name");
        constToKey.put(3, "protected_domain_names");
        constToKey.put(7, "proxy_server");
        constToKey.put(101, "allowed_apps");
        final JSONObject response = new JSONObject();
        response.put("policy_name", (Object)enterprisePolicy.policyName);
        response.put("policy_id", (Object)enterprisePolicy.policyID);
        response.put("policy_desc", (Object)enterprisePolicy.policyDescription);
        final Iterator iterator = enterprisePolicy.rules.iterator();
        final HashMap convertedMap = new HashMap();
        while (iterator.hasNext()) {
            final EnterpriseRule enterpriseRule = iterator.next();
            if (enterpriseRule instanceof NetworkRule) {
                this.handleNetworkRuleConversion(convertedMap, (NetworkRule)enterpriseRule);
            }
            else if (enterpriseRule instanceof AppRule) {
                this.handleAppRuleconversion(convertedMap, (AppRule)enterpriseRule);
            }
            else {
                if (!(enterpriseRule instanceof WindowsConfigRule)) {
                    continue;
                }
                final WindowsConfigRule windowsConfigRule = (WindowsConfigRule)enterpriseRule;
                response.put("data_recovery_cert_id", (Object)windowsConfigRule.dataRecoveryCert);
                response.put("allow_user_decryption", (Object)windowsConfigRule.allowUserDecrypt);
                response.put("enforcement_level", (Object)windowsConfigRule.enforcementLevel);
            }
        }
        for (final int cutConst : constToKey.keySet()) {
            if (cutConst == 101) {
                final JSONArray jsonArray = convertedMap.get(cutConst);
                if (jsonArray == null) {
                    continue;
                }
                response.put((String)constToKey.get(cutConst), (Object)jsonArray);
            }
            else {
                final List jsonArray2 = convertedMap.get(cutConst);
                if (jsonArray2 == null) {
                    continue;
                }
                response.put((String)constToKey.get(cutConst), (Collection)jsonArray2);
            }
        }
        return response;
    }
    
    private void handleNetworkRuleConversion(final HashMap hashMap, final NetworkRule networkRule) throws JSONException {
        List curList = hashMap.get(networkRule.ruleType);
        if (curList == null) {
            curList = new ArrayList();
        }
        curList.add(networkRule.value);
        hashMap.put(networkRule.ruleType, curList);
    }
    
    private void handleAppRuleconversion(final HashMap hashMap, final AppRule appRule) throws JSONException {
        JSONArray curList = hashMap.get(101);
        if (curList == null) {
            curList = new JSONArray();
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_identifier", (Object)appRule.appIdentifier);
        jsonObject.put("app_type", (Object)appRule.appType);
        jsonObject.put("is_allowed", (Object)appRule.isAllowed);
        curList.put((Object)jsonObject);
        hashMap.put(101, curList);
    }
}
