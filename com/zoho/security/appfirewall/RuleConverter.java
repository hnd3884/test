package com.zoho.security.appfirewall;

import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public abstract class RuleConverter
{
    List<AppFirewallRule> appFirewallRuleList;
    static final List<String> POSTSTAGEDIRECTIVES;
    static final List<String> POSTAUTHENTICATION_STAGEDIRECTIVES;
    
    public RuleConverter() {
        this.appFirewallRuleList = null;
    }
    
    abstract List<AppFirewallRule> convert(final Object p0);
    
    protected AppFirewallRule convertToAppFirewallRule(final String id, final String description, final Map<String, List<DirectiveConfiguration>> directiveMap, final Map<String, String> actionsMap) throws AppFirewallException {
        final AppFirewallRule appFirewallRule = new AppFirewallRule();
        final List<AppFirewallDirective> validFirewallDirectives = new ArrayList<AppFirewallDirective>();
        for (final Map.Entry<String, List<DirectiveConfiguration>> configuredDirective : directiveMap.entrySet()) {
            final DirectiveConfiguration.Directive directive = DirectiveConfiguration.Directive.valueOf(configuredDirective.getKey());
            final AppFirewallDirective appFirewallDirective = AppFirewallDirectiveFactory.createAppFirewallDirective(configuredDirective.getValue(), directive);
            if (!appFirewallRule.isPostStage() && RuleConverter.POSTSTAGEDIRECTIVES.contains(directive.getValue())) {
                appFirewallRule.setPostStage(true);
            }
            if (!appFirewallRule.isPostAuthenticationStage() && RuleConverter.POSTAUTHENTICATION_STAGEDIRECTIVES.contains(directive.getValue())) {
                appFirewallRule.setPostAuthenticationStage(true);
            }
            validFirewallDirectives.add(appFirewallDirective);
        }
        if (validFirewallDirectives.size() == 0) {
            throw new AppFirewallException(" rule can't be empty , it must contains atleast one valid AppFirewallDirective , \n expecting Element(s) from [url,method,headers,params,inputstream,file]");
        }
        appFirewallRule.setValidFirewallDirectives(validFirewallDirectives);
        appFirewallRule.setDescription(description);
        appFirewallRule.setActions(actionsMap);
        appFirewallRule.setID(id);
        return appFirewallRule;
    }
    
    protected DirectiveConfiguration.Directive getValidDirective(final String configuredDirective) {
        try {
            return DirectiveConfiguration.Directive.valueOf(configuredDirective);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    protected void addRule(final AppFirewallRule rule) {
        if (this.appFirewallRuleList == null) {
            this.appFirewallRuleList = new ArrayList<AppFirewallRule>();
        }
        if (rule != null) {
            if (rule.isExpired()) {
                AppFirewallInitializer.getExpiredRuleIdList().add(rule.getId());
            }
            else {
                this.appFirewallRuleList.add(rule);
            }
        }
    }
    
    protected JSONObject getJSONObject(final JSONObject jsonObject, final String key) {
        return jsonObject.has(key) ? jsonObject.getJSONObject(key) : null;
    }
    
    protected JSONArray getJSONArray(final JSONObject jsonObject, final String key) {
        return jsonObject.has(key) ? jsonObject.getJSONArray(key) : null;
    }
    
    static {
        POSTSTAGEDIRECTIVES = Arrays.asList("files", "inputstream");
        POSTAUTHENTICATION_STAGEDIRECTIVES = Arrays.asList("users");
    }
}
