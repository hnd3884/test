package ua_parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class UserAgentParser
{
    private final List<UAPattern> patterns;
    
    public UserAgentParser(final List<UAPattern> patterns) {
        this.patterns = patterns;
    }
    
    public static UserAgentParser fromList(final List<Map<String, String>> configList) {
        final List<UAPattern> configPatterns = new ArrayList<UAPattern>();
        for (final Map<String, String> configMap : configList) {
            configPatterns.add(patternFromMap(configMap));
        }
        return new UserAgentParser(new CopyOnWriteArrayList<UAPattern>(configPatterns));
    }
    
    public UserAgent parse(final String agentString) {
        if (agentString == null) {
            return null;
        }
        for (final UAPattern p : this.patterns) {
            final UserAgent agent;
            if ((agent = p.match(agentString)) != null) {
                return agent;
            }
        }
        return UserAgent.OTHER;
    }
    
    protected static UAPattern patternFromMap(final Map<String, String> configMap) {
        final String regex = configMap.get("regex");
        if (regex == null) {
            throw new IllegalArgumentException("User agent is missing regex");
        }
        return new UAPattern(Pattern.compile(regex), configMap.get("family_replacement"), configMap.get("v1_replacement"), configMap.get("v2_replacement"));
    }
    
    protected static class UAPattern
    {
        private final Pattern pattern;
        private final String familyReplacement;
        private final String v1Replacement;
        private final String v2Replacement;
        
        public UAPattern(final Pattern pattern, final String familyReplacement, final String v1Replacement, final String v2Replacement) {
            this.pattern = pattern;
            this.familyReplacement = familyReplacement;
            this.v1Replacement = v1Replacement;
            this.v2Replacement = v2Replacement;
        }
        
        public UserAgent match(final String agentString) {
            String family = null;
            String v1 = null;
            String v2 = null;
            String v3 = null;
            final Matcher matcher = this.pattern.matcher(agentString);
            if (!matcher.find()) {
                return null;
            }
            final int groupCount = matcher.groupCount();
            if (this.familyReplacement != null) {
                if (this.familyReplacement.contains("$1") && groupCount >= 1 && matcher.group(1) != null) {
                    family = this.familyReplacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)));
                }
                else {
                    family = this.familyReplacement;
                }
            }
            else if (groupCount >= 1) {
                family = matcher.group(1);
            }
            if (this.v1Replacement != null) {
                v1 = this.v1Replacement;
            }
            else if (groupCount >= 2) {
                final String group2 = matcher.group(2);
                if (!this.isBlank(group2)) {
                    v1 = group2;
                }
            }
            if (this.v2Replacement != null) {
                v2 = this.v2Replacement;
            }
            else if (groupCount >= 3) {
                final String group3 = matcher.group(3);
                if (!this.isBlank(group3)) {
                    v2 = group3;
                }
                if (groupCount >= 4) {
                    final String group4 = matcher.group(4);
                    if (!this.isBlank(group4)) {
                        v3 = group4;
                    }
                }
            }
            return (family == null) ? null : new UserAgent(family, v1, v2, v3);
        }
        
        private boolean isBlank(final String value) {
            return value == null || value.isEmpty();
        }
    }
}
