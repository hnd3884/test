package ua_parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class OSParser
{
    private final List<OSPattern> patterns;
    
    public OSParser(final List<OSPattern> patterns) {
        this.patterns = patterns;
    }
    
    public static OSParser fromList(final List<Map<String, String>> configList) {
        final List<OSPattern> configPatterns = new ArrayList<OSPattern>();
        for (final Map<String, String> configMap : configList) {
            configPatterns.add(patternFromMap(configMap));
        }
        return new OSParser(new CopyOnWriteArrayList<OSPattern>(configPatterns));
    }
    
    public OS parse(final String agentString) {
        if (agentString == null) {
            return null;
        }
        for (final OSPattern p : this.patterns) {
            final OS os;
            if ((os = p.match(agentString)) != null) {
                return os;
            }
        }
        return OS.OTHER;
    }
    
    protected static OSPattern patternFromMap(final Map<String, String> configMap) {
        final String regex = configMap.get("regex");
        if (regex == null) {
            throw new IllegalArgumentException("OS is missing regex");
        }
        return new OSPattern(Pattern.compile(regex), configMap.get("os_replacement"), configMap.get("os_v1_replacement"), configMap.get("os_v2_replacement"), configMap.get("os_v3_replacement"));
    }
    
    protected static class OSPattern
    {
        private final Pattern pattern;
        private final String osReplacement;
        private final String v1Replacement;
        private final String v2Replacement;
        private final String v3Replacement;
        
        public OSPattern(final Pattern pattern, final String osReplacement, final String v1Replacement, final String v2Replacement, final String v3Replacement) {
            this.pattern = pattern;
            this.osReplacement = osReplacement;
            this.v1Replacement = v1Replacement;
            this.v2Replacement = v2Replacement;
            this.v3Replacement = v3Replacement;
        }
        
        public OS match(final String agentString) {
            String family = null;
            String v1 = null;
            String v2 = null;
            String v3 = null;
            String v4 = null;
            final Matcher matcher = this.pattern.matcher(agentString);
            if (!matcher.find()) {
                return null;
            }
            final int groupCount = matcher.groupCount();
            if (this.osReplacement != null) {
                if (groupCount >= 1) {
                    family = Pattern.compile("(" + Pattern.quote("$1") + ")").matcher(this.osReplacement).replaceAll(matcher.group(1));
                }
                else {
                    family = this.osReplacement;
                }
            }
            else if (groupCount >= 1) {
                family = matcher.group(1);
            }
            if (this.v1Replacement != null) {
                v1 = this.getReplacement(matcher, this.v1Replacement);
            }
            else if (groupCount >= 2) {
                v1 = matcher.group(2);
            }
            if (this.v2Replacement != null) {
                v2 = this.getReplacement(matcher, this.v2Replacement);
            }
            else if (groupCount >= 3) {
                v2 = matcher.group(3);
            }
            if (this.v3Replacement != null) {
                v3 = this.getReplacement(matcher, this.v3Replacement);
            }
            else if (groupCount >= 4) {
                v3 = matcher.group(4);
            }
            if (groupCount >= 5) {
                v4 = matcher.group(5);
            }
            return (family == null) ? null : new OS(family, v1, v2, v3, v4);
        }
        
        private String getReplacement(final Matcher matcher, final String replacement) {
            if (this.isBackReference(replacement)) {
                final int group = this.getGroup(replacement);
                return matcher.group(group);
            }
            return replacement;
        }
        
        private boolean isBackReference(final String replacement) {
            return replacement.startsWith("$");
        }
        
        private int getGroup(final String backReference) {
            return Integer.valueOf(backReference.substring(1));
        }
    }
}
