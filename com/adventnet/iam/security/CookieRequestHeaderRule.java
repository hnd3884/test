package com.adventnet.iam.security;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.w3c.dom.Element;
import java.util.List;
import java.util.Map;

public class CookieRequestHeaderRule extends HeaderRule
{
    private Map<String, ParameterRule> cookieMapWithStrictName;
    private Map<String, ParameterRule> cookieMapWithRegexName;
    private List<String> mandatoryCookies;
    static final String COOKIES = "cookies";
    static final String COOKIE = "cookie";
    
    public CookieRequestHeaderRule(final Element cookiesElement) {
        this.cookieMapWithStrictName = new LinkedHashMap<String, ParameterRule>();
        this.cookieMapWithRegexName = new LinkedHashMap<String, ParameterRule>();
        this.mandatoryCookies = new ArrayList<String>();
        final List<Element> cookiesList = RuleSetParser.getChildNodesByTagName(cookiesElement, "cookie");
        if (cookiesList.size() > 0) {
            for (int i = 0; i < cookiesList.size(); ++i) {
                if (cookiesList.get(i).getNodeType() == 1) {
                    final Element cookieElement = cookiesList.get(i);
                    final String cookieName = cookieElement.getAttribute("name").trim();
                    if (!SecurityUtil.isValid(cookieName)) {
                        throw new RuntimeException("Cookie Rule with invalid name not allowed \n");
                    }
                    final ParameterRule cookieRule = new ParameterRule(cookieElement);
                    cookieRule.storeParameterValue = false;
                    if (cookieRule.isParamNameInRegex()) {
                        this.cookieMapWithRegexName.put(cookieName, cookieRule);
                    }
                    else {
                        this.cookieMapWithStrictName.put(cookieName, cookieRule);
                        if (cookieRule.getMinOccurrences() == 1) {
                            this.mandatoryCookies.add(cookieName);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String getHeaderName() {
        return "cookie";
    }
    
    Map<String, ParameterRule> getCookieMapWithStrictName() {
        return this.cookieMapWithStrictName;
    }
    
    Map<String, ParameterRule> getCookieMapWithRegexName() {
        return this.cookieMapWithRegexName;
    }
    
    public Map<String, ParameterRule> getCookieMapWithStrictNameForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ParameterRule>)this.cookieMapWithStrictName);
    }
    
    public Map<String, ParameterRule> getCookieMapWithRegexNameForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ParameterRule>)this.cookieMapWithRegexName);
    }
    
    @Override
    public String toString() {
        return this.cookieMapWithStrictName.toString() + this.cookieMapWithRegexName.toString();
    }
    
    public List<String> getMandatoryCookieNames() {
        return this.mandatoryCookies;
    }
}
