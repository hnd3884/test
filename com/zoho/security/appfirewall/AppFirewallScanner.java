package com.zoho.security.appfirewall;

import java.util.Iterator;
import com.zoho.security.agent.LocalConfigurations;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_MATCH;
import java.util.List;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import java.util.HashMap;
import java.util.ArrayList;
import com.zoho.security.eventfw.ExecutionTimer;
import javax.servlet.http.HttpServletRequest;

public class AppFirewallScanner
{
    public static final String LB_ADDED_REMOTE_IP_HEADER = "LB_SSL_REMOTE_IP";
    
    public static boolean requestFirewallRegulator(final HttpServletRequest request, final FirewallStage stage) {
        boolean blackListedContentDetected = false;
        final ExecutionTimer scanInfoTimer = ExecutionTimer.startInstance();
        final List<AppFirewallRule> appFirewallRules = AppFirewallInitializer.getRules(stage);
        if (appFirewallRules.size() > 0) {
            final List<HashMap<String, Object>> ruleInfoObject = new ArrayList<HashMap<String, Object>>();
            final Map<String, Object> ruleMatchObject = new HashMap<String, Object>();
            blackListedContentDetected = multiStageFirewall(request, appFirewallRules, ruleInfoObject, ruleMatchObject, stage);
            ZSEC_PERFORMANCE_ANOMALY.pushBWAFScaninfo(request.getRequestURI(), (List)ruleInfoObject, scanInfoTimer);
            if (blackListedContentDetected) {
                final String matchid = ruleMatchObject.get("id");
                ZSEC_AFW_RULE_MATCH.pushBlockedRequestStatus("BLOCK", request.getRequestURI(), getRemoteAddr(request), (List)ruleInfoObject, (Map)ruleMatchObject, matchid, (ExecutionTimer)null);
            }
        }
        return blackListedContentDetected;
    }
    
    private static String getRemoteAddr(final HttpServletRequest request) {
        final String ipAddr = request.getHeader("LB_SSL_REMOTE_IP");
        if (ipAddr != null) {
            return ipAddr;
        }
        return request.getRemoteAddr();
    }
    
    private static boolean multiStageFirewall(final HttpServletRequest request, final List<AppFirewallRule> appFirewallRules, final List<HashMap<String, Object>> ruleInfoObject, final Map<String, Object> ruleMatchObject, final FirewallStage stage) {
        for (final AppFirewallRule appFirewallRule : appFirewallRules) {
            if (appFirewallRule.isExpired()) {
                AppFirewallInitializer.removeFireWallRule(appFirewallRule, stage);
            }
            else {
                final ExecutionTimer riTimer = ExecutionTimer.startInstance();
                boolean isBlackListedRequest = true;
                JSONArray directiveInfo = null;
                for (final AppFirewallDirective firewallDirective : appFirewallRule.getValidFirewallDirectives()) {
                    try {
                        final JSONArray resultJSON = (JSONArray)firewallDirective.findBlackListComponent(request);
                        isBlackListedRequest = (isBlackListedRequest && resultJSON != null);
                        if (!isBlackListedRequest) {
                            break;
                        }
                        if (firewallDirective.getId() != null) {
                            final JSONObject directiveInfoObj = new JSONObject();
                            if (directiveInfo == null) {
                                directiveInfo = new JSONArray();
                            }
                            directiveInfoObj.put("id", (Object)firewallDirective.getId());
                            directiveInfoObj.put("componentinfo", (Object)resultJSON);
                            directiveInfo.put((Object)directiveInfoObj);
                        }
                        else {
                            ruleMatchObject.put(firewallDirective.getDirective().getValue(), resultJSON);
                        }
                    }
                    catch (final JSONException e) {
                        ZSEC_AFW_RULE_MATCH.pushJsonException("Exception Occurred while creating JSONObject for firewall Directive " + e.getMessage(), "AFW_JSONEXCEPTION", (ExecutionTimer)null);
                    }
                }
                ZSEC_PERFORMANCE_ANOMALY.pushBWAFRuleinfo(request.getRequestURI(), appFirewallRule.getId(), riTimer);
                if (isBlackListedRequest) {
                    if (appFirewallRule.getId() != null) {
                        ruleMatchObject.put("id", appFirewallRule.getId());
                        ruleMatchObject.put("directiveinfo", directiveInfo);
                    }
                    else {
                        ruleMatchObject.put("requestip", request.getRemoteAddr());
                    }
                    request.setAttribute("ZSEC_MATCHED_APPFIREWALL_RULE", (Object)appFirewallRule);
                    return true;
                }
                if (appFirewallRule.getId() == null) {
                    continue;
                }
                final HashMap<String, Object> ruleInfo = new HashMap<String, Object>();
                ruleInfo.put("id", appFirewallRule.getId());
                ruleInfoObject.add(ruleInfo);
            }
        }
        if (AppFirewallInitializer.enableExpiryCheck && AppFirewallInitializer.getExpiredRuleIdList().size() > 0) {
            LocalConfigurations.saveRuleToFile();
        }
        return false;
    }
}
