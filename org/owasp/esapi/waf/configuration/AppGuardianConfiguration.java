package org.owasp.esapi.waf.configuration;

import java.util.Iterator;
import java.util.ArrayList;
import org.owasp.esapi.waf.rules.Rule;
import java.util.List;
import java.util.HashMap;
import org.apache.log4j.Level;

public class AppGuardianConfiguration
{
    public static final int LOG = 0;
    public static final int REDIRECT = 1;
    public static final int BLOCK = 2;
    public static final int OPERATOR_EQ = 0;
    public static final int OPERATOR_CONTAINS = 1;
    public static final int OPERATOR_IN_LIST = 2;
    public static final int OPERATOR_EXISTS = 3;
    public static Level LOG_LEVEL;
    public static String LOG_DIRECTORY;
    private Level logLevel;
    private String logDirectory;
    public static int DEFAULT_FAIL_ACTION;
    public static String DEFAULT_CHARACTER_ENCODING;
    public static String DEFAULT_CONTENT_TYPE;
    public static final String JAVASCRIPT_TARGET_TOKEN = "##1##";
    public static final String JAVASCRIPT_REDIRECT = "<html><body><script>document.location='##1##';</script></body></html>";
    private HashMap<String, Object> aliases;
    private String defaultErrorPage;
    private int defaultResponseCode;
    private boolean forceHttpOnlyFlagToSession;
    private boolean forceSecureFlagToSession;
    private String sessionCookieName;
    private List<Rule> beforeBodyRules;
    private List<Rule> afterBodyRules;
    private List<Rule> beforeResponseRules;
    private List<Rule> cookieRules;
    
    public String getSessionCookieName() {
        return this.sessionCookieName;
    }
    
    public void setSessionCookieName(final String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }
    
    public AppGuardianConfiguration() {
        this.logLevel = Level.INFO;
        this.logDirectory = "/WEB-INF/logs";
        this.forceHttpOnlyFlagToSession = false;
        this.forceSecureFlagToSession = false;
        this.beforeBodyRules = new ArrayList<Rule>();
        this.afterBodyRules = new ArrayList<Rule>();
        this.beforeResponseRules = new ArrayList<Rule>();
        this.cookieRules = new ArrayList<Rule>();
        this.aliases = new HashMap<String, Object>();
    }
    
    @Deprecated
    public Level getLogLevel() {
        return this.logLevel;
    }
    
    @Deprecated
    public void setLogLevel(final Level level) {
        AppGuardianConfiguration.LOG_LEVEL = level;
        this.logLevel = level;
    }
    
    @Deprecated
    public void setLogDirectory(final String dir) {
        AppGuardianConfiguration.LOG_DIRECTORY = dir;
        this.logDirectory = dir;
    }
    
    @Deprecated
    public String getLogDirectory() {
        return this.logDirectory;
    }
    
    public String getDefaultErrorPage() {
        return this.defaultErrorPage;
    }
    
    public void setDefaultErrorPage(final String defaultErrorPage) {
        this.defaultErrorPage = defaultErrorPage;
    }
    
    public int getDefaultResponseCode() {
        return this.defaultResponseCode;
    }
    
    public void setDefaultResponseCode(final int defaultResponseCode) {
        this.defaultResponseCode = defaultResponseCode;
    }
    
    public void addAlias(final String key, final Object obj) {
        this.aliases.put(key, obj);
    }
    
    public List<Rule> getBeforeBodyRules() {
        return this.beforeBodyRules;
    }
    
    public List<Rule> getAfterBodyRules() {
        return this.afterBodyRules;
    }
    
    public List<Rule> getBeforeResponseRules() {
        return this.beforeResponseRules;
    }
    
    public List<Rule> getCookieRules() {
        return this.cookieRules;
    }
    
    public void addBeforeBodyRule(final Rule r) {
        this.beforeBodyRules.add(r);
    }
    
    public void addAfterBodyRule(final Rule r) {
        this.afterBodyRules.add(r);
    }
    
    public void addBeforeResponseRule(final Rule r) {
        this.beforeResponseRules.add(r);
    }
    
    public void addCookieRule(final Rule r) {
        this.cookieRules.add(r);
    }
    
    public void setApplyHTTPOnlyFlagToSessionCookie(final boolean shouldApply) {
        this.forceHttpOnlyFlagToSession = shouldApply;
    }
    
    public void setApplySecureFlagToSessionCookie(final boolean shouldApply) {
        this.forceSecureFlagToSession = shouldApply;
    }
    
    public boolean isUsingHttpOnlyFlagOnSessionCookie() {
        return this.forceHttpOnlyFlagToSession;
    }
    
    public boolean isUsingSecureFlagOnSessionCookie() {
        return this.forceSecureFlagToSession;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WAF Configuration\n");
        sb.append("Before body rules:\n");
        for (final Rule rule : this.beforeBodyRules) {
            sb.append("  " + rule.toString() + "\n");
        }
        sb.append("After body rules:\n");
        for (final Rule rule : this.afterBodyRules) {
            sb.append("  " + rule.toString() + "\n");
        }
        sb.append("Before response rules:\n");
        for (final Rule rule : this.beforeResponseRules) {
            sb.append("  " + rule.toString() + "\n");
        }
        sb.append("Cookie rules:\n");
        for (final Rule rule : this.cookieRules) {
            sb.append("  " + rule.toString() + "\n");
        }
        return sb.toString();
    }
    
    static {
        AppGuardianConfiguration.LOG_LEVEL = Level.INFO;
        AppGuardianConfiguration.LOG_DIRECTORY = "/WEB-INF/logs";
        AppGuardianConfiguration.DEFAULT_FAIL_ACTION = 0;
        AppGuardianConfiguration.DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";
        AppGuardianConfiguration.DEFAULT_CONTENT_TYPE = "text/html; charset=" + AppGuardianConfiguration.DEFAULT_CHARACTER_ENCODING;
    }
}
