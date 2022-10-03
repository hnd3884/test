package org.apache.commons.digester;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class SimpleRegexMatcher extends RegexMatcher
{
    private static final Log baseLog;
    private Log log;
    
    public SimpleRegexMatcher() {
        this.log = SimpleRegexMatcher.baseLog;
    }
    
    public Log getLog() {
        return this.log;
    }
    
    public void setLog(final Log log) {
        this.log = log;
    }
    
    public boolean match(final String basePattern, final String regexPattern) {
        return basePattern != null && regexPattern != null && this.match(basePattern, regexPattern, 0, 0);
    }
    
    private boolean match(final String basePattern, final String regexPattern, int baseAt, int regexAt) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)("Base: " + basePattern));
            this.log.trace((Object)("Regex: " + regexPattern));
            this.log.trace((Object)("Base@" + baseAt));
            this.log.trace((Object)("Regex@" + regexAt));
        }
        if (regexAt >= regexPattern.length()) {
            return baseAt >= basePattern.length();
        }
        if (baseAt >= basePattern.length()) {
            return false;
        }
        final char regexCurrent = regexPattern.charAt(regexAt);
        switch (regexCurrent) {
            case '*': {
                if (++regexAt >= regexPattern.length()) {
                    return true;
                }
                final char nextRegex = regexPattern.charAt(regexAt);
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Searching for next '" + nextRegex + "' char"));
                }
                for (int nextMatch = basePattern.indexOf(nextRegex, baseAt); nextMatch != -1; nextMatch = basePattern.indexOf(nextRegex, nextMatch + 1)) {
                    if (this.log.isTraceEnabled()) {
                        this.log.trace((Object)("Trying '*' match@" + nextMatch));
                    }
                    if (this.match(basePattern, regexPattern, nextMatch, regexAt)) {
                        return true;
                    }
                }
                this.log.trace((Object)"No matches found.");
                return false;
            }
            case '?': {
                return this.match(basePattern, regexPattern, ++baseAt, ++regexAt);
            }
            default: {
                if (this.log.isTraceEnabled()) {
                    this.log.trace((Object)("Camparing " + regexCurrent + " to " + basePattern.charAt(baseAt)));
                }
                return regexCurrent == basePattern.charAt(baseAt) && this.match(basePattern, regexPattern, ++baseAt, ++regexAt);
            }
        }
    }
    
    static {
        baseLog = LogFactory.getLog(SimpleRegexMatcher.class);
    }
}
