package com.zoho.security.wafad.instrument.sqli;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.Map;

public class MySQLiExceptionAttackDiscovery extends MySQLInjectionAttackDiscovery
{
    public static final String SQL_EXCEPTION_PARAM_NAME = "EXCEPTION";
    private static final Map<Integer, Set<Pattern>> ERROR_NO_TO_MESSAGE_MAP;
    private SQLException sqlException;
    private Pattern matchedPattern;
    
    @Override
    protected boolean matchesCondition(final Map<String, Object> params) {
        if (params.containsKey("EXCEPTION")) {
            this.sqlException = params.get("EXCEPTION");
            if (MySQLiExceptionAttackDiscovery.ERROR_NO_TO_MESSAGE_MAP.containsKey(this.sqlException.getErrorCode())) {
                final Set<Pattern> messagePatterns = MySQLiExceptionAttackDiscovery.ERROR_NO_TO_MESSAGE_MAP.get(this.sqlException.getErrorCode());
                this.matchedPattern = this.match(messagePatterns, this.sqlException.getMessage());
                if (this.matchedPattern != null) {
                    params.put("EXCEPTION", " ErrorCode: " + this.sqlException.getErrorCode() + " Message: " + this.sqlException.getMessage());
                    return true;
                }
                return super.matchesCondition(params);
            }
        }
        return false;
    }
    
    private Pattern match(final Set<Pattern> patterns, final String sqlMessage) {
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(sqlMessage).find()) {
                return pattern;
            }
        }
        return null;
    }
    
    @Override
    protected String getMatchedCondition() {
        if (this.sqlException == null || this.matchedPattern == null) {
            return super.getMatchedCondition();
        }
        return "[EXCEPTION] field data matches with pattern [ErrorCode: " + this.sqlException.getErrorCode() + " Message: " + this.matchedPattern.pattern() + "]";
    }
    
    static {
        (ERROR_NO_TO_MESSAGE_MAP = new HashMap<Integer, Set<Pattern>>()).put(1064, new HashSet<Pattern>(Arrays.asList(Pattern.compile(".* near '.*' at line \\d*"))));
        MySQLiExceptionAttackDiscovery.ERROR_NO_TO_MESSAGE_MAP.put(1054, new HashSet<Pattern>(Arrays.asList(Pattern.compile("Unknown column '.*' in 'order clause'"), Pattern.compile("Unknown column '.*' in 'group statement'"))));
        MySQLiExceptionAttackDiscovery.ERROR_NO_TO_MESSAGE_MAP.put(1222, new HashSet<Pattern>(Arrays.asList(Pattern.compile("The used SELECT statements have a different number of columns"))));
        MySQLiExceptionAttackDiscovery.ERROR_NO_TO_MESSAGE_MAP.put(1241, new HashSet<Pattern>(Arrays.asList(Pattern.compile("Operand should contain \\d* column\\(s\\)"))));
    }
}
