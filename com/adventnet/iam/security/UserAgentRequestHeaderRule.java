package com.adventnet.iam.security;

import java.util.logging.Level;
import java.util.Collections;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.util.Map;
import java.util.logging.Logger;

public class UserAgentRequestHeaderRule extends HeaderRule
{
    private static final Logger LOGGER;
    private Map<String, ParameterRule> userAgentRulesMap;
    static final String USERAGENT = "user-agent";
    private Version browserVersion;
    private Version osVersion;
    private String deviceFamilyRegex;
    
    public UserAgentRequestHeaderRule(final Element userAgentsElement) {
        this.browserVersion = null;
        this.osVersion = null;
        this.deviceFamilyRegex = null;
        this.userAgentRulesMap = new HashMap<String, ParameterRule>();
        for (final USERAGENTTAG allowedElement : USERAGENTTAG.values()) {
            final ParameterRule userAgentElementRule = this.convertIntoParamRule(userAgentsElement, allowedElement);
            if (userAgentElementRule != null) {
                userAgentElementRule.storeParameterValue = false;
                this.userAgentRulesMap.put(allowedElement.getName(), userAgentElementRule);
            }
        }
    }
    
    ParameterRule convertIntoParamRule(final Element userAgentElement, final USERAGENTTAG tag) {
        ParameterRule userAgentSubElementRule = null;
        final Element userAgentsubElement = RuleSetParser.getFirstChildNodeByTagName(userAgentElement, tag.getName());
        if (userAgentsubElement != null) {
            userAgentSubElementRule = new ParameterRule(userAgentsubElement);
            String deviceFamily = null;
            if (tag == USERAGENTTAG.DEVICE && !"".equals(deviceFamily = userAgentsubElement.getAttribute("family").trim())) {
                this.deviceFamilyRegex = deviceFamily;
            }
            else {
                final Version v = new Version(tag.getName(), userAgentsubElement.getAttribute("major").trim(), userAgentsubElement.getAttribute("minor"));
                if (tag == USERAGENTTAG.BROWSER) {
                    this.browserVersion = v;
                }
                else {
                    this.osVersion = v;
                }
            }
        }
        return userAgentSubElementRule;
    }
    
    @Override
    public String getHeaderName() {
        return "user-agent";
    }
    
    Map<String, ParameterRule> getUserAgentRulesMap() {
        return this.userAgentRulesMap;
    }
    
    public Map<String, ParameterRule> getUserAgentRulesMapForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ParameterRule>)this.userAgentRulesMap);
    }
    
    ParameterRule getUserAgentRule(final String tag) {
        return this.userAgentRulesMap.get(tag);
    }
    
    boolean validate(final SecurityRequestWrapper securedRequest, final String tag, final String tagValue) {
        final ParameterRule componentRule = this.userAgentRulesMap.get(tag);
        componentRule.validateParamValue(securedRequest, componentRule.getParamName(), tagValue, null);
        return true;
    }
    
    void validateVersions(final String tag, final String tagValue, final double userAgentMajorVersion, final double userAgentMinorVersion) {
        final Version v = "browser".equals(tag) ? this.browserVersion : this.osVersion;
        if (v != null) {
            v.limitCheck(tagValue, userAgentMajorVersion, userAgentMinorVersion);
        }
    }
    
    boolean validateDeviceType(final SecurityRequestWrapper securedRequest, final String deviceType) {
        if (!SecurityUtil.matchPattern(deviceType, this.deviceFamilyRegex, securedRequest)) {
            UserAgentRequestHeaderRule.LOGGER.log(Level.SEVERE, " REQUEST HEADER VALIDATION : Device family = \"{0}\" is not allowed to access the resource", new Object[] { deviceType });
            throw new IAMSecurityException("DEVICE_NOT_ALLOWED");
        }
        return true;
    }
    
    boolean contains(final String tag) {
        return this.userAgentRulesMap.containsKey(tag);
    }
    
    @Override
    public String toString() {
        return this.userAgentRulesMap.toString();
    }
    
    static {
        LOGGER = Logger.getLogger(UserAgentRequestHeaderRule.class.getName());
    }
    
    public enum USERAGENTTAG
    {
        BROWSER("browser"), 
        OS("os"), 
        DEVICE("device");
        
        private String name;
        
        private USERAGENTTAG(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
