package com.zoho.security.appfirewall;

import java.util.Iterator;
import java.util.Map;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_CONVERSION;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;

public class AppSenseLocalFileConverter extends RuleConverter
{
    @Override
    List<AppFirewallRule> convert(final Object rulesObject) {
        final JSONArray rulesJsonArray = (JSONArray)rulesObject;
        for (int i = 0; i < rulesJsonArray.length(); ++i) {
            final JSONObject ruleJsonObject = rulesJsonArray.getJSONObject(i);
            final AppFirewallRule rule = this.extractRules(ruleJsonObject);
            this.addRule(rule);
        }
        return this.appFirewallRuleList;
    }
    
    private AppFirewallRule extractRules(final JSONObject ruleJsonObject) {
        final long autoID = ruleJsonObject.getLong("id");
        final String description = ruleJsonObject.getString("description");
        final JSONObject ruleJson = ruleJsonObject.getJSONObject("directive");
        final JSONObject actionJsonObject = this.getJSONObject(ruleJsonObject, "actions");
        final long expiry_time = ruleJsonObject.getLong("expiry_time");
        final Map<String, List<DirectiveConfiguration>> directiveMap = new HashMap<String, List<DirectiveConfiguration>>();
        Map<String, String> actionMap = null;
        try {
            for (final Object key : ruleJson.keySet()) {
                final String directiveName = (String)key;
                final JSONArray dirArr = ruleJson.getJSONArray(directiveName);
                for (int i = 0; i < dirArr.length(); ++i) {
                    final JSONObject dirObj = dirArr.getJSONObject(i);
                    final DirectiveConfiguration compObj = this.getDirectiveConfiguration(directiveName, dirObj);
                    if (directiveMap.containsKey(directiveName)) {
                        directiveMap.get(directiveName).add(compObj);
                    }
                    else {
                        final List<DirectiveConfiguration> list = new ArrayList<DirectiveConfiguration>();
                        list.add(compObj);
                        directiveMap.put(directiveName, list);
                    }
                }
            }
            if (actionJsonObject != null) {
                actionMap = new HashMap<String, String>();
                for (final Object actionType : actionJsonObject.keySet()) {
                    final String type = (String)actionType;
                    actionMap.put(type, actionJsonObject.getString(type));
                }
            }
            final AppFirewallRule rule = this.convertToAppFirewallRule(Long.toString(autoID), description, directiveMap, actionMap);
            rule.setExpiryTime(expiry_time);
            return rule;
        }
        catch (final AppFirewallException e) {
            ZSEC_AFW_RULE_CONVERSION.pushException("LOCAL_FILE", "DESC : " + description, e.getMessage(), "AFW_RULE_INVALID_SYNTAX", (ExecutionTimer)null);
            return null;
        }
    }
    
    DirectiveConfiguration getDirectiveConfiguration(final String dirName, final JSONObject dirObj) {
        final DirectiveConfiguration directiveConf = new DirectiveConfiguration();
        final DirectiveConfiguration.Directive directive = this.getValidDirective(dirName);
        if (directive != null) {
            directiveConf.setName(dirName);
            directiveConf.setId(dirObj.getString("id"));
            for (final String componentName : directive.getComponentList()) {
                if (dirObj.has(componentName)) {
                    final JSONObject compObj = dirObj.getJSONObject(componentName);
                    final String compID = compObj.getString("id");
                    final String value = compObj.getString("value");
                    final Operator operator = Operator.valueOf(compObj.getString("operator").toUpperCase());
                    final AppFirewallComponent validComponent = new AppFirewallComponent(compID, operator, componentName, value, Long.MAX_VALUE, directive);
                    if (directiveConf.getComponentList() == null) {
                        directiveConf.initalizeComponentList();
                    }
                    directiveConf.getComponentList().add(validComponent);
                }
            }
        }
        return directiveConf;
    }
}
