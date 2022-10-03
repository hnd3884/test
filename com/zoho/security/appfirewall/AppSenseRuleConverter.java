package com.zoho.security.appfirewall;

import java.util.Iterator;
import org.json.JSONException;
import java.util.Map;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_CONVERSION;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class AppSenseRuleConverter extends RuleConverter
{
    static final String DATA = "DATA";
    static final String CHILD = "CHILD";
    
    @Override
    List<AppFirewallRule> convert(final Object rulesObject) {
        final JSONArray rulesJsonArray = ((JSONObject)rulesObject).getJSONArray("DATA");
        for (int i = 0; i < rulesJsonArray.length(); ++i) {
            final JSONObject ruleJsonObject = rulesJsonArray.getJSONObject(i);
            final AppFirewallRule rule = this.extractRules(ruleJsonObject);
            this.addRule(rule);
        }
        return this.appFirewallRuleList;
    }
    
    private AppFirewallRule extractRules(final JSONObject ruleJsonObject) throws JSONException {
        final long autoID = ruleJsonObject.getLong("RULE_AUTO_ID");
        final String description = ruleJsonObject.getString("DESCRIPTION");
        final JSONArray ruleJsonArray = this.extractChildData(ruleJsonObject);
        final Map<String, List<DirectiveConfiguration>> directiveMap = new HashMap<String, List<DirectiveConfiguration>>();
        Map<String, String> actionsMap = null;
        try {
            final String actionType = ruleJsonObject.has("RULE_ACTION") ? AppFirewallRule.ACTIONS.getAction(ruleJsonObject.getInt("RULE_ACTION")) : null;
            if (actionType != null) {
                actionsMap = new HashMap<String, String>();
                final String actionValue = ruleJsonObject.has("ACTION_VALUE") ? ruleJsonObject.getString("ACTION_VALUE") : actionType;
                actionsMap.put(actionType, actionValue);
            }
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            final long expiryTime = dateFormat.parse(ruleJsonObject.getString("EXPIRY_TIME")).getTime();
            for (int j = 0; j < ruleJsonArray.length(); ++j) {
                final JSONObject directiveObject = ruleJsonArray.getJSONObject(j);
                final DirectiveConfiguration compObj = this.getDirectiveConfiguration(directiveObject.getString("CONDITIONS_AUTO_ID"), this.extractChildData(directiveObject));
                final String directiveName = compObj.getDirectiveName();
                if (directiveMap.containsKey(directiveName)) {
                    directiveMap.get(directiveName).add(compObj);
                }
                else {
                    final List<DirectiveConfiguration> list = new ArrayList<DirectiveConfiguration>();
                    list.add(compObj);
                    directiveMap.put(directiveName, list);
                }
            }
            final AppFirewallRule rule = this.convertToAppFirewallRule(Long.toString(autoID), description, directiveMap, actionsMap);
            rule.setExpiryTime(expiryTime);
            return rule;
        }
        catch (final Exception e) {
            ZSEC_AFW_RULE_CONVERSION.pushException("APPSENSE", "DESC : " + description, e.getMessage(), "AFW_RULE_CREATION_FAILED", (ExecutionTimer)null);
            return null;
        }
    }
    
    private JSONArray extractChildData(final JSONObject jsonObject) throws JSONException {
        return jsonObject.getJSONArray("CHILD").getJSONObject(0).getJSONArray("DATA");
    }
    
    DirectiveConfiguration getDirectiveConfiguration(final String autoID, final JSONArray directiveArray) {
        final DirectiveConfiguration directiveConf = new DirectiveConfiguration();
        directiveConf.setId(autoID);
        final Map<String, AppFirewallComponent> componentMap = new HashMap<String, AppFirewallComponent>();
        for (int i = 0; i < directiveArray.length(); ++i) {
            final JSONObject directiveObj = directiveArray.getJSONObject(i);
            String componentName = null;
            final String component = directiveObj.getString("COMPONENT");
            final String component_autoID = Long.toString(directiveObj.getLong("COMPONENT_AUTO_ID"));
            final String[] componentArr = component.split("\\.");
            if (componentArr.length == 2) {
                final String dirName = componentArr[0].toUpperCase();
                final DirectiveConfiguration.Directive directive = this.getValidDirective(dirName);
                if (directive == null) {
                    ZSEC_AFW_RULE_CONVERSION.pushError("APPSENSE", "DIRECTIVE : " + dirName, "INVALID_DIRECTIVE", "expected directives are :[url,method,headers,params,inputstream,file]", (ExecutionTimer)null);
                    throw new AppFirewallException(" Directive : \"" + dirName + "\"Not allowed as per schema , it must contain one of  [url,method,headers,params,inputstream,file]");
                }
                directiveConf.setName(dirName);
                componentName = componentArr[1];
                if (!directive.getComponentList().contains(componentName)) {
                    ZSEC_AFW_RULE_CONVERSION.pushError("APPSENSE", "COMPONENT :" + componentName, "INVALID_COMPONENT", "allowed components -  " + directive.getComponentList(), (ExecutionTimer)null);
                    throw new AppFirewallException("Component  : " + componentName + " Not allowed as per schema , it must contain one of " + directive.getComponentList());
                }
                final String operatorValue = directiveObj.getString("OPERATOR");
                try {
                    final Operator operator = "".equals(operatorValue) ? Operator.STRINGMATCHES : Operator.valueOf(operatorValue.toUpperCase());
                    final AppFirewallComponent validComponent = new AppFirewallComponent(component_autoID, operator, componentName, directiveObj.getString("VALUE"), Long.MAX_VALUE, directive);
                    if (!componentMap.containsKey(componentName)) {
                        componentMap.put(componentName, validComponent);
                    }
                }
                catch (final Exception e) {
                    ZSEC_AFW_RULE_CONVERSION.pushException("APPSENSE", "COMPONENT :  " + componentName + " , OPERATOR : " + operatorValue, e.getMessage(), "INVALID_OPERATOR", (ExecutionTimer)null);
                    throw new AppFirewallException("Unable to create AppFirewall Component For Operator :" + operatorValue + ", componentName :" + componentName);
                }
            }
        }
        if (directiveConf.getDirectiveName() != null) {
            final DirectiveConfiguration.Directive directive2 = DirectiveConfiguration.Directive.valueOf(directiveConf.getDirectiveName());
            for (final String comp : directive2.getComponentList()) {
                if (directiveConf.getComponentList() == null) {
                    directiveConf.initalizeComponentList();
                }
                if (componentMap.containsKey(comp)) {
                    directiveConf.getComponentList().add(componentMap.get(comp));
                }
            }
        }
        return directiveConf;
    }
}
