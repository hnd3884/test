package com.adventnet.iam.security;

import org.w3c.dom.Element;
import java.util.regex.Pattern;

public class RegexRule
{
    private String name;
    private Pattern pattern;
    private int timeOutInMillis;
    private int iterationCount;
    
    public RegexRule(final Element element) {
        this.name = element.getAttribute("name");
        this.pattern = Pattern.compile(element.getAttribute("value"));
        this.timeOutInMillis = this.getIntegerValue(element.getAttribute("timeout"));
        this.iterationCount = this.getIntegerValue(element.getAttribute("iteration"));
    }
    
    public RegexRule(final String name, final String value) {
        this.name = name;
        this.pattern = Pattern.compile(value);
    }
    
    public RegexRule(final String name, final String value, final int timeout, final int maxIterationCount) {
        this.name = name;
        this.pattern = Pattern.compile(value);
        this.timeOutInMillis = timeout;
        this.iterationCount = maxIterationCount;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.pattern.toString();
    }
    
    public Pattern getPattern() {
        return this.pattern;
    }
    
    public int getIterationCount() {
        return this.iterationCount;
    }
    
    public int getTimeOut() {
        return this.timeOutInMillis;
    }
    
    public void setTimeOut(final int timeout) {
        this.timeOutInMillis = timeout;
    }
    
    private int getIntegerValue(final String value) {
        if (SecurityUtil.isValid(value)) {
            return Integer.parseInt(value);
        }
        return -1;
    }
}
