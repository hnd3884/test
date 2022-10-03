package com.adventnet.util.parser;

import java.util.ArrayList;

class Parser
{
    private ArrayList ruleList;
    private String className;
    private String type;
    private ParserInterface piface;
    private ArrayList errorRules;
    
    Parser() {
        this.ruleList = null;
        this.className = null;
        this.type = null;
        this.piface = null;
        this.errorRules = null;
    }
    
    Object getRule(final String s) {
        for (int i = 0; i < this.ruleList.size(); ++i) {
            final RuleObject ruleObject = this.ruleList.get(i);
            if (ruleObject.getCommand().equals(s)) {
                return ruleObject;
            }
        }
        return null;
    }
    
    void setClassName(final String className) {
        this.className = className;
    }
    
    String getClassName() {
        return this.className;
    }
    
    ParserInterface getParserInterface() {
        return this.piface;
    }
    
    void setParserInterface(final ParserInterface piface) {
        this.piface = piface;
    }
    
    void setRuleList(final ArrayList ruleList) {
        this.ruleList = ruleList;
    }
    
    ArrayList getRuleList() {
        return this.ruleList;
    }
    
    void setType(final String type) {
        this.type = type;
    }
    
    String getType() {
        return this.type;
    }
    
    ArrayList getErrorRules() {
        return this.errorRules;
    }
    
    void setErrorRules(final ArrayList errorRules) {
        this.errorRules = errorRules;
    }
}
