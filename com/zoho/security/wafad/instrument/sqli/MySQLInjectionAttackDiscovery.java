package com.zoho.security.wafad.instrument.sqli;

import java.util.Map;
import com.zoho.security.wafad.instrument.WAFAttackDiscoveryEvent;
import com.zoho.security.wafad.instrument.WAFAttackDiscoveryEventPush;

public class MySQLInjectionAttackDiscovery extends WAFAttackDiscoveryEventPush
{
    public static final String QUERY_PARAM_NAME = "QUERY";
    private static final WAFAttackDiscoveryEvent SQL_INJECTION;
    private static final String[] PAYLOADS;
    private String matchedPayLoad;
    
    public MySQLInjectionAttackDiscovery() {
        super(MySQLInjectionAttackDiscovery.SQL_INJECTION);
        this.matchedPayLoad = "";
    }
    
    protected boolean matchesCondition(final Map<String, Object> params) {
        final String query = params.get("QUERY");
        if (query != null) {
            for (final String payLoad : MySQLInjectionAttackDiscovery.PAYLOADS) {
                if (containsIgnoreCase(query, payLoad)) {
                    this.matchedPayLoad = payLoad;
                    return true;
                }
            }
        }
        return false;
    }
    
    protected String getMatchedCondition() {
        return "[QUERY] field data contains [" + this.matchedPayLoad + "]";
    }
    
    public static boolean containsIgnoreCase(final String src, final String what) {
        final int length = what.length();
        if (length == 0) {
            return true;
        }
        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));
        for (int i = src.length() - length; i > 0; --i) {
            final char ch = src.charAt(i);
            if (ch == firstLo || ch == firstUp) {
                if (src.regionMatches(true, i, what, 0, length)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        SQL_INJECTION = new WAFAttackDiscoveryEvent("SQL_INJECTION");
        PAYLOADS = new String[] { " '' ", "#", " -- ", "/*", "load_file", "outfile", "dumpfile", "sys_exec", "sys_eval", "@@version" };
    }
}
