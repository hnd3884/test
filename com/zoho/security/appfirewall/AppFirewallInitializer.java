package com.zoho.security.appfirewall;

import com.zoho.security.agent.LocalConfigurations;
import com.zoho.security.agent.Components;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_EXPIRY;
import java.util.Iterator;
import java.util.Collection;
import org.json.JSONArray;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class AppFirewallInitializer
{
    private static List<AppFirewallRule> preStageAppFirewallRules;
    private static List<AppFirewallRule> postStageAppFirewallRules;
    private static List<AppFirewallRule> postAuthenticationAppFirewallRules;
    private static List<String> expiredRuleId;
    private static int size;
    public static boolean enableExpiryCheck;
    
    public static void initializeAppFirewallRule(final Object rulesObject) throws AppFirewallException, SAXException {
        AppFirewallInitializer.preStageAppFirewallRules = new CopyOnWriteArrayList<AppFirewallRule>();
        AppFirewallInitializer.postStageAppFirewallRules = new CopyOnWriteArrayList<AppFirewallRule>();
        AppFirewallInitializer.postAuthenticationAppFirewallRules = new CopyOnWriteArrayList<AppFirewallRule>();
        AppFirewallInitializer.expiredRuleId = new ArrayList<String>();
        addAppFireWallRules(RuleConverterFactory.convert(rulesObject));
        setSize();
    }
    
    private static void setSize() {
        AppFirewallInitializer.size = AppFirewallInitializer.preStageAppFirewallRules.size() + AppFirewallInitializer.postStageAppFirewallRules.size() + AppFirewallInitializer.postAuthenticationAppFirewallRules.size();
    }
    
    public static synchronized void addAppFirewallRuleOnNotification(final Object rulesObject) throws AppFirewallException, SAXException {
        if (!AppFirewallPolicyLoader.isAppFirewallPolicyInitialized) {
            initializeAppFirewallRule(rulesObject);
            AppFirewallPolicyLoader.isAppFirewallPolicyInitialized = true;
        }
        else {
            addAppFireWallRules(RuleConverterFactory.convert(rulesObject));
            setSize();
        }
    }
    
    public static int getNoOfRules() {
        return AppFirewallInitializer.size;
    }
    
    public static synchronized void removeFireWallRulesByID(final JSONArray ruleAutoIDArray) {
        for (int i = 0; i < ruleAutoIDArray.length(); ++i) {
            final String ruleAutoId = Long.toString(ruleAutoIDArray.getLong(i));
            for (final AppFirewallRule firewallRule : new ArrayList(AppFirewallInitializer.preStageAppFirewallRules)) {
                if (ruleAutoId.equalsIgnoreCase(firewallRule.getId())) {
                    AppFirewallInitializer.preStageAppFirewallRules.remove(firewallRule);
                }
            }
            for (final AppFirewallRule firewallRule : new ArrayList(AppFirewallInitializer.postStageAppFirewallRules)) {
                if (ruleAutoId.equalsIgnoreCase(firewallRule.getId())) {
                    AppFirewallInitializer.postStageAppFirewallRules.remove(firewallRule);
                }
            }
            for (final AppFirewallRule firewallRule : new ArrayList(AppFirewallInitializer.postAuthenticationAppFirewallRules)) {
                if (ruleAutoId.equalsIgnoreCase(firewallRule.getId())) {
                    AppFirewallInitializer.postAuthenticationAppFirewallRules.remove(firewallRule);
                }
            }
        }
    }
    
    private static void addAppFireWallRules(final List<AppFirewallRule> ruleList) {
        if (ruleList != null) {
            if (AppFirewallInitializer.expiredRuleId.size() > 0) {
                ZSEC_AFW_RULE_EXPIRY.pushSuccess("Expired rules removed from localfile", (List)getExpiredRuleIdList(), (ExecutionTimer)null);
                LocalConfigurations.saveToFile(Components.COMPONENT.APPFIREWALL.name());
            }
            for (final AppFirewallRule appFirewallRule : ruleList) {
                if (appFirewallRule.isPostStage()) {
                    AppFirewallInitializer.postStageAppFirewallRules.add(appFirewallRule);
                }
                else if (appFirewallRule.isPostAuthenticationStage()) {
                    AppFirewallInitializer.postAuthenticationAppFirewallRules.add(appFirewallRule);
                }
                else {
                    AppFirewallInitializer.preStageAppFirewallRules.add(appFirewallRule);
                }
            }
        }
    }
    
    public static List<AppFirewallRule> getRules(final FirewallStage stage) {
        switch (stage) {
            case POST_STAGE: {
                return AppFirewallInitializer.postStageAppFirewallRules;
            }
            case POST_AUTHENTICATION_STAGE: {
                return AppFirewallInitializer.postAuthenticationAppFirewallRules;
            }
            default: {
                return AppFirewallInitializer.preStageAppFirewallRules;
            }
        }
    }
    
    public static boolean isRuleExist() {
        return AppFirewallInitializer.size > 0;
    }
    
    public static JSONArray getRulesAsJSON() {
        final JSONArray rulesJSON = new JSONArray();
        for (final AppFirewallRule ar : AppFirewallInitializer.preStageAppFirewallRules) {
            rulesJSON.put((Object)ar.toJSON());
        }
        for (final AppFirewallRule ar : AppFirewallInitializer.postStageAppFirewallRules) {
            rulesJSON.put((Object)ar.toJSON());
        }
        for (final AppFirewallRule ar : AppFirewallInitializer.postAuthenticationAppFirewallRules) {
            rulesJSON.put((Object)ar.toJSON());
        }
        return rulesJSON;
    }
    
    public static void removeFireWallRule(final AppFirewallRule appFirewallRule, final FirewallStage stage) {
        final List<AppFirewallRule> rules = getRules(stage);
        if (appFirewallRule.getId() != null && rules.remove(appFirewallRule)) {
            AppFirewallInitializer.expiredRuleId.add(appFirewallRule.getId());
        }
    }
    
    public static List<String> getExpiredRuleIdList() {
        return AppFirewallInitializer.expiredRuleId;
    }
    
    public static void resetFlag() {
        AppFirewallInitializer.expiredRuleId.clear();
        AppFirewallInitializer.enableExpiryCheck = true;
    }
    
    static {
        AppFirewallInitializer.preStageAppFirewallRules = null;
        AppFirewallInitializer.postStageAppFirewallRules = null;
        AppFirewallInitializer.postAuthenticationAppFirewallRules = null;
        AppFirewallInitializer.expiredRuleId = null;
        AppFirewallInitializer.enableExpiryCheck = true;
    }
}
