package com.adventnet.util.parser;

public class RuleObject
{
    private String command;
    private String errorRule;
    private String validRule;
    private Object parserErule;
    private Object parserVrule;
    public static int ERROR;
    public static int VALID;
    private int responseRule;
    
    public RuleObject() {
        this.command = null;
        this.errorRule = null;
        this.validRule = null;
        this.parserErule = null;
        this.parserVrule = null;
        this.responseRule = RuleObject.ERROR;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setErrorRule(final String errorRule) {
        this.errorRule = errorRule;
    }
    
    public String getErrorRule() {
        return this.errorRule;
    }
    
    public void setValidRule(final String validRule) {
        this.validRule = validRule;
    }
    
    public String getValidRule() {
        return this.validRule;
    }
    
    public void setParserErrorRule(final Object parserErule) {
        this.parserErule = parserErule;
    }
    
    public Object getParserErrorRule() {
        return this.parserErule;
    }
    
    public void setParserValidRule(final Object parserVrule) {
        this.parserVrule = parserVrule;
    }
    
    public Object getParserValidRule() {
        return this.parserVrule;
    }
    
    public void setResponseRule(final int responseRule) {
        this.responseRule = responseRule;
    }
    
    public int getResponseRule() {
        return this.responseRule;
    }
    
    static {
        RuleObject.ERROR = 0;
        RuleObject.VALID = 1;
    }
}
