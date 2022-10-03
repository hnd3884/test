package com.adventnet.iam.security;

import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.Element;
import com.zoho.security.dos.Util;
import java.util.TreeMap;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class ThrottlesRule
{
    private static final Logger LOGGER;
    private String name;
    private final Windows window;
    private final Scopes scope;
    private String key;
    private String timeLogIntervalDurationStr;
    private HashMap<KeyType, List<String>> keyMap;
    private TreeMap<Long, ThrottleRule> throttleRuleMap;
    private boolean hasDynamicKey;
    private boolean dynamicThrottle;
    private List<String> serviceScopeThrottleRulesAsList;
    private RollingWindowInfo rollingWindowInfo;
    
    public ThrottlesRule(final Windows window, final Scopes scope) {
        this(window, scope, null);
    }
    
    public ThrottlesRule(final Windows window, final Scopes scope, final String key) {
        this(window, scope, key, null, false, null);
    }
    
    public ThrottlesRule(final Windows window, final Scopes scope, final String key, final String name, final boolean enableDynamicThrottle) {
        this(window, scope, key, name, enableDynamicThrottle, null);
    }
    
    public ThrottlesRule(final Windows window, final Scopes scope, final String key, final String name, final boolean enableDynamicThrottle, final String timeLogIntervalDuration) {
        this.keyMap = null;
        this.throttleRuleMap = null;
        this.serviceScopeThrottleRulesAsList = null;
        this.rollingWindowInfo = null;
        this.window = window;
        this.scope = scope;
        this.key = key;
        this.dynamicThrottle = enableDynamicThrottle;
        this.timeLogIntervalDurationStr = timeLogIntervalDuration;
        if (SecurityUtil.isValid(name)) {
            this.name = name;
        }
        if (Util.isValidString(key)) {
            this.parseKey();
        }
        if ((this.hasDynamicKey || this.isDynamicThrottleEnabled()) && this.name == null) {
            throw new RuntimeException("The 'name' attribute is mandatory for throttles rule when it has 'url.dynamic_key' or 'dynamic-throttle'.");
        }
        if (window == Windows.ROLLING) {
            this.rollingWindowInfo = new RollingWindowInfo();
            if (Util.isValidString(timeLogIntervalDuration)) {
                this.rollingWindowInfo.setTimeLogIntervalDuration(Util.getTimeInMillis(timeLogIntervalDuration));
                if (this.rollingWindowInfo.getTimeLogIntervalDuration() < 1L) {
                    throw new RuntimeException("The 'time-log-interval-duration' value is must be >= 1 milliseconds. Invalid value: '" + timeLogIntervalDuration + "'.");
                }
            }
        }
    }
    
    ThrottlesRule(final Element throttlesEle) {
        this(Windows.valueOf(throttlesEle.getAttribute(Attributes.WINDOW.getAttributeName())), Scopes.valueOf(throttlesEle.getAttribute(Attributes.SCOPE.getAttributeName())), throttlesEle.getAttribute(Attributes.KEY.getAttributeName()), throttlesEle.getAttribute(Attributes.NAME.getAttributeName()), "true".equals(throttlesEle.getAttribute(Attributes.DYNAMIC_THROTTLE.getAttributeName())), throttlesEle.getAttribute(Attributes.TIMELOGINTERVALDURATION.getAttributeName()));
        if (!Util.isValidString(this.key)) {
            throw new RuntimeException("The null/empty key value is not allowed.");
        }
        if (this.window != Windows.ROLLING && Util.isValidString(throttlesEle.getAttribute(Attributes.TIMELOGINTERVALDURATION.getAttributeName()))) {
            throw new RuntimeException("The 'time-log-interval-duration' is only supported for ROLLING window.");
        }
        this.initThrottleRule(throttlesEle);
    }
    
    public static ThrottlesRule createDynamicThrottlesRule(final ThrottlesRule throttlesRule) {
        return new ThrottlesRule(throttlesRule.getWindow(), throttlesRule.getScope(), throttlesRule.getKey(), throttlesRule.getName(), throttlesRule.isDynamicThrottleEnabled(), throttlesRule.getTimeLogIntervalDurationStr());
    }
    
    private void initThrottleRule(final Element throttlesEle) throws RuntimeException {
        final List<Element> throttleElements = RuleSetParser.getChildNodesByTagName(throttlesEle, "throttle");
        try {
            if (!SecurityUtil.isValidList(throttleElements)) {
                throw new RuntimeException("The <throttles> element must has at least one <throttle> element");
            }
            for (final Element throttleEle : throttleElements) {
                final ThrottleRule throttle = ThrottleRule.createThrottleRule(throttleEle, this.window);
                this.addThrottle(throttle);
            }
        }
        catch (final RuntimeException e) {
            ThrottlesRule.LOGGER.log(Level.SEVERE, "SecurityMisConfiguration in parsing <throttle> element. The <throttles> rule info: {0}", new Object[] { this.toString() });
            throw e;
        }
    }
    
    public void addThrottle(final ThrottleRule throttle) {
        if (throttle == null) {
            return;
        }
        if (this.throttleRuleMap == null) {
            this.throttleRuleMap = new TreeMap<Long, ThrottleRule>();
        }
        if (this.throttleRuleMap.containsKey(throttle.getDuration())) {
            throw new RuntimeException("The throttle with the duration \"" + throttle.getDuration() + "\"is already exist.");
        }
        if (this.window == Windows.LIVE && this.throttleRuleMap.size() > 1) {
            throw new RuntimeException("If throttles 'window' is 'LIVE', then shouldn't configure more than one throttle element.");
        }
        if (this.window == Windows.ROLLING) {
            this.rollingWindowInfo.updateMaxDuration(throttle.getDuration());
        }
        this.throttleRuleMap.put(throttle.getDuration(), throttle);
    }
    
    void addThrottleRules(final List<ThrottleRule> throttleRuleList) {
        if (throttleRuleList == null) {
            return;
        }
        for (final ThrottleRule throttleRule : throttleRuleList) {
            this.addThrottle(throttleRule);
        }
    }
    
    private void parseKey() {
        for (final String keys : this.key.split("\\+")) {
            final String[] splittedKey = keys.split("\\.");
            try {
                final KeyType type = KeyType.valueOf(splittedKey[0].toUpperCase());
                String name = splittedKey[1];
                try {
                    switch (type) {
                        case URL: {
                            name = name.toUpperCase();
                            if (URLKeysName.valueOf(name) == URLKeysName.DYNAMIC_KEY) {
                                this.hasDynamicKey = true;
                            }
                            this.addKeyToMap(type, name);
                            break;
                        }
                        case USER: {
                            name = name.toUpperCase();
                            UserKeysName.valueOf(name);
                            this.addKeyToMap(type, name);
                            break;
                        }
                        case PROXY: {
                            name = name.toUpperCase();
                            ProxyKeysName.valueOf(name);
                            this.addKeyToMap(type, name);
                            break;
                        }
                        default: {
                            this.addKeyToMap(type, name);
                            break;
                        }
                    }
                }
                catch (final IllegalArgumentException e) {
                    throw new RuntimeException("The throttles \"key\" has a key-name \"" + name + "\" is invalid. If the throttles key-type is \"user\", then the key-name should be any of \"remoteip\" or \"zuid\" or \"authtoken\" or \"oauthtoken\".");
                }
            }
            catch (final IllegalArgumentException e2) {
                throw new RuntimeException("The throttles \"key\" has a key-type \"" + splittedKey[0] + "\"' is invalid. The key-type should be any of the \"url\" or \"param\" or \"header\" or \"user\".");
            }
        }
    }
    
    private void addKeyToMap(final KeyType type, final String name) {
        if (this.keyMap == null) {
            this.keyMap = new HashMap<KeyType, List<String>>();
        }
        List<String> keyList = this.keyMap.get(type);
        if (keyList == null) {
            keyList = new ArrayList<String>();
        }
        keyList.add(name);
        this.keyMap.put(type, keyList);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void disableDynamicThrottle() {
        this.dynamicThrottle = false;
    }
    
    public boolean isDynamicThrottleEnabled() {
        return this.dynamicThrottle;
    }
    
    public Windows getWindow() {
        return this.window;
    }
    
    public Scopes getScope() {
        return this.scope;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getTimeLogIntervalDurationStr() {
        return this.timeLogIntervalDurationStr;
    }
    
    long getTimeLogInterval() {
        return this.rollingWindowInfo.getTimeLogIntervalDuration();
    }
    
    List<String> getServiceScopeThrottleRulesAsList() {
        if (this.serviceScopeThrottleRulesAsList == null) {
            synchronized (this) {
                final List<String> throttleRulesAsList = new ArrayList<String>();
                for (final ThrottleRule throttle : this.throttleRuleMap.values()) {
                    throttleRulesAsList.addAll(throttle.getRuleAsList());
                }
                this.serviceScopeThrottleRulesAsList = throttleRulesAsList;
            }
        }
        return this.serviceScopeThrottleRulesAsList;
    }
    
    Map<KeyType, List<String>> getKeyMap() {
        return this.keyMap;
    }
    
    public TreeMap<Long, ThrottleRule> getThrottleRuleMap() {
        return this.throttleRuleMap;
    }
    
    public ThrottleRule getThrottleRule(final long throttleDuration) {
        return this.throttleRuleMap.get(throttleDuration);
    }
    
    long getMaxDuration() {
        return this.rollingWindowInfo.getMaxDuration();
    }
    
    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Key: \"");
        strBuilder.append(this.key);
        strBuilder.append("\", window: \"");
        strBuilder.append(this.window.name());
        strBuilder.append("\", scope: \"");
        strBuilder.append(this.scope.name());
        strBuilder.append("\"");
        if (this.window == Windows.ROLLING) {
            strBuilder.append(", timeLogIntervalDuration: \"");
            strBuilder.append(this.rollingWindowInfo.getTimeLogIntervalDuration());
            strBuilder.append(" sec\"]");
        }
        return strBuilder.append(".").toString();
    }
    
    static {
        LOGGER = Logger.getLogger(ThrottlesRule.class.getName());
    }
    
    public enum Attributes
    {
        WINDOW("window"), 
        SCOPE("scope"), 
        KEY("key"), 
        TIMELOGINTERVALDURATION("time-log-interval-duration"), 
        NAME("name"), 
        DYNAMIC_THROTTLE("dynamic-throttle");
        
        private String attributeName;
        
        private Attributes(final String attributeName) {
            this.attributeName = null;
            this.attributeName = attributeName;
        }
        
        public String getAttributeName() {
            return this.attributeName;
        }
    }
    
    public enum Windows
    {
        LIVE, 
        FIXED, 
        ROLLING, 
        SLIDING;
    }
    
    public enum Scopes
    {
        APPSERVER, 
        SERVICE;
    }
    
    public enum KeyType
    {
        URL, 
        HEADER, 
        PARAM, 
        USER, 
        DYNAMICKEY, 
        PROXY;
        
        public String getAliasName() {
            switch (this) {
                case URL: {
                    return "ul";
                }
                case HEADER: {
                    return "hr";
                }
                case PARAM: {
                    return "pm";
                }
                case DYNAMICKEY: {
                    return "dk";
                }
                case PROXY: {
                    return "py";
                }
                default: {
                    return "ur";
                }
            }
        }
    }
    
    public enum URLKeysName
    {
        PATH, 
        DYNAMIC_KEY;
        
        public String getAliasName() {
            switch (this) {
                case PATH: {
                    return "ph";
                }
                default: {
                    return "dk";
                }
            }
        }
    }
    
    public enum UserKeysName
    {
        ZUID, 
        AUTHTOKEN, 
        OAUTHTOKEN, 
        REMOTEIP;
        
        public String getAliasName() {
            switch (this) {
                case ZUID: {
                    return "zd";
                }
                case AUTHTOKEN: {
                    return "ak";
                }
                case OAUTHTOKEN: {
                    return "ok";
                }
                default: {
                    return "rp";
                }
            }
        }
    }
    
    public enum ProxyKeysName
    {
        IP, 
        NAME;
        
        public String getAliasName() {
            switch (this) {
                case IP: {
                    return "ip";
                }
                default: {
                    return "name";
                }
            }
        }
    }
    
    public enum ApiRateLimitResponseHeaderValueFields
    {
        duration("duration"), 
        remaining_count("remaining-count"), 
        retry_after("retry-after");
        
        private final String header;
        
        private ApiRateLimitResponseHeaderValueFields(final String header) {
            this.header = header;
        }
        
        public String getHeader() {
            return this.header;
        }
    }
    
    private class RollingWindowInfo
    {
        private long maxDuration;
        private long timeLogIntervalDuration;
        
        private RollingWindowInfo() {
            this.maxDuration = -1L;
            this.timeLogIntervalDuration = 1000L;
        }
        
        public void updateMaxDuration(final long maxDuration) {
            if (maxDuration > this.maxDuration) {
                this.maxDuration = maxDuration;
            }
        }
        
        private void setTimeLogIntervalDuration(final long timeLogIntervalDuration) {
            this.timeLogIntervalDuration = timeLogIntervalDuration;
        }
        
        public long getMaxDuration() {
            return this.maxDuration;
        }
        
        public long getTimeLogIntervalDuration() {
            return this.timeLogIntervalDuration;
        }
    }
}
