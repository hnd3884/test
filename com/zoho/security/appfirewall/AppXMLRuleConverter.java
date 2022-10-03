package com.zoho.security.appfirewall;

import java.util.Arrays;
import java.util.Map;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_CONVERSION;
import java.util.ArrayList;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import java.util.HashMap;
import java.util.Iterator;
import org.w3c.dom.Element;
import com.adventnet.iam.security.RuleSetParser;
import org.w3c.dom.Document;
import java.util.List;

public class AppXMLRuleConverter extends RuleConverter
{
    private static final String RULE = "rule";
    private static final List<String> COMPLEXELEMENTS;
    
    @Override
    List<AppFirewallRule> convert(final Object rulesObject) {
        final Element appFirewallRulesRoot = ((Document)rulesObject).getDocumentElement();
        for (final Element ruleRoot : RuleSetParser.getChildNodesByTagName(appFirewallRulesRoot, "rule")) {
            this.addRule(this.extractRules(ruleRoot));
        }
        return this.appFirewallRuleList;
    }
    
    private AppFirewallRule extractRules(final Element ruleRoot) {
        final Map<String, List<DirectiveConfiguration>> directiveMap = new HashMap<String, List<DirectiveConfiguration>>();
        Map<String, String> actionsMap = null;
        String description = "";
        try {
            for (final Element configuredDirectiveEle : RuleSetParser.getUniqueChildNodes(ruleRoot)) {
                final String directiveName = configuredDirectiveEle.getNodeName().toUpperCase();
                if ("description".equalsIgnoreCase(configuredDirectiveEle.getNodeName())) {
                    description = configuredDirectiveEle.getTextContent();
                }
                else if ("actions".equalsIgnoreCase(configuredDirectiveEle.getNodeName())) {
                    final List<Element> actionList = SecurityFrameworkUtil.getChildNodesByTagName(configuredDirectiveEle, removeLastChar(configuredDirectiveEle.getNodeName()));
                    if (actionList.size() <= 0) {
                        continue;
                    }
                    actionsMap = new HashMap<String, String>();
                    for (final Element actionEle : actionList) {
                        actionsMap.put(actionEle.getAttribute("type").toUpperCase(), actionEle.getTextContent());
                    }
                }
                else if (AppXMLRuleConverter.COMPLEXELEMENTS.contains(directiveName)) {
                    final List<Element> innerDirectiveList = SecurityFrameworkUtil.getChildNodesByTagName(configuredDirectiveEle, removeLastChar(configuredDirectiveEle.getNodeName()));
                    if (innerDirectiveList.size() <= 0) {
                        continue;
                    }
                    directiveMap.put(directiveName, new ArrayList<DirectiveConfiguration>());
                    for (final Element innerDirectiveElement : innerDirectiveList) {
                        final DirectiveConfiguration compObj = this.getDirectiveConfiguration(directiveName, innerDirectiveElement);
                        directiveMap.get(directiveName).add(compObj);
                    }
                }
                else {
                    final DirectiveConfiguration compObj2 = this.getDirectiveConfiguration(directiveName, configuredDirectiveEle);
                    final List<DirectiveConfiguration> list = new ArrayList<DirectiveConfiguration>();
                    list.add(compObj2);
                    directiveMap.put(directiveName, list);
                }
            }
            return this.convertToAppFirewallRule(null, description, directiveMap, actionsMap);
        }
        catch (final AppFirewallException e) {
            ZSEC_AFW_RULE_CONVERSION.pushException("XML", "DESC : " + description, e.getMessage(), "AFW_RULE_CREATION_FAILED", (ExecutionTimer)null);
            return null;
        }
    }
    
    DirectiveConfiguration getDirectiveConfiguration(final String dirName, final Element configuredDirectiveEle) {
        final DirectiveConfiguration directiveConf = new DirectiveConfiguration();
        final DirectiveConfiguration.Directive directive = this.getValidDirective(dirName);
        if (directive == null) {
            ZSEC_AFW_RULE_CONVERSION.pushError("XML", "DIRECTIVE : " + dirName, "INVALID_DIRECTIVE", "expected directives are :[url,method,headers,params,inputstream,file]", (ExecutionTimer)null);
            throw new AppFirewallException(" Directive : \"" + dirName + "\"Not allowed as per schema , it must contain one of  [url,method,headers,params,inputstream,file]");
        }
        directiveConf.setName(dirName);
        for (final String componentName : directive.getComponentList()) {
            final Element element = RuleSetParser.getFirstChildNodeByTagName(configuredDirectiveEle, componentName);
            if (element != null) {
                final String value = element.getTextContent();
                final String operatorValue = element.getAttribute("operator");
                try {
                    final Operator operator = "".equals(operatorValue) ? Operator.STRINGMATCHES : Operator.valueOf(operatorValue.toUpperCase());
                    final String componentLength = element.getAttribute("length");
                    final long length = "".equals(componentLength) ? Long.MAX_VALUE : Long.parseLong(componentLength);
                    final AppFirewallComponent validComponent = new AppFirewallComponent(operator, componentName, value, length, directive);
                    if (directiveConf.getComponentList() == null) {
                        directiveConf.initalizeComponentList();
                    }
                    directiveConf.getComponentList().add(validComponent);
                }
                catch (final Exception e) {
                    ZSEC_AFW_RULE_CONVERSION.pushException("XML", "COMPONENT :  " + componentName + " , OPERATOR : " + operatorValue, e.getMessage(), "INVALID_OPERATOR", (ExecutionTimer)null);
                    throw new AppFirewallException("Unable to create AppFirewall Component For Operator :" + operatorValue + ", componentName :" + componentName);
                }
            }
        }
        if (directiveConf.getComponentList() == null) {
            ZSEC_AFW_RULE_CONVERSION.pushError("XML", "DIRECTIVE : " + directive.getValue(), "EMPTY_COMPONENT", "expected components are : " + directive.getComponentList(), (ExecutionTimer)null);
            throw new AppFirewallException(" Directive : \"" + directive.getValue() + "\" can't be empty , it must contains atleast one valid AppFirewallComponent , \n expected components are :   " + directive.getComponentList());
        }
        return directiveConf;
    }
    
    private static String removeLastChar(final String str) {
        return str.substring(0, str.length() - 1);
    }
    
    static {
        COMPLEXELEMENTS = Arrays.asList("HEADERS", "PARAMS", "FILES", "USERS");
    }
}
