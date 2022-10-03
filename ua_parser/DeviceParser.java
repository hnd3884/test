package ua_parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

public class DeviceParser
{
    private final List<DevicePattern> patterns;
    
    public DeviceParser(final List<DevicePattern> patterns) {
        this.patterns = patterns;
    }
    
    public Device parse(final String agentString) {
        if (agentString == null) {
            return null;
        }
        for (final DevicePattern p : this.patterns) {
            final String device;
            if ((device = p.match(agentString)) != null) {
                return new Device(device);
            }
        }
        return Device.OTHER;
    }
    
    public static DeviceParser fromList(final List<Map<String, String>> configList) {
        final List<DevicePattern> configPatterns = new ArrayList<DevicePattern>();
        for (final Map<String, String> configMap : configList) {
            configPatterns.add(patternFromMap(configMap));
        }
        return new DeviceParser(new CopyOnWriteArrayList<DevicePattern>(configPatterns));
    }
    
    protected static DevicePattern patternFromMap(final Map<String, String> configMap) {
        final String regex = configMap.get("regex");
        if (regex == null) {
            throw new IllegalArgumentException("Device is missing regex");
        }
        final Pattern pattern = "i".equals(configMap.get("regex_flag")) ? Pattern.compile(regex, 2) : Pattern.compile(regex);
        return new DevicePattern(pattern, configMap.get("device_replacement"));
    }
    
    protected static class DevicePattern
    {
        private static final Pattern SUBSTITUTIONS_PATTERN;
        private final Pattern pattern;
        private final String deviceReplacement;
        
        public DevicePattern(final Pattern pattern, final String deviceReplacement) {
            this.pattern = pattern;
            this.deviceReplacement = deviceReplacement;
        }
        
        public String match(final String agentString) {
            final Matcher matcher = this.pattern.matcher(agentString);
            if (!matcher.find()) {
                return null;
            }
            String device = null;
            if (this.deviceReplacement != null) {
                if (this.deviceReplacement.contains("$")) {
                    device = this.deviceReplacement;
                    for (final String substitution : this.getSubstitutions(this.deviceReplacement)) {
                        final int i = Integer.valueOf(substitution.substring(1));
                        final String replacement = (matcher.groupCount() >= i && matcher.group(i) != null) ? Matcher.quoteReplacement(matcher.group(i)) : "";
                        device = device.replaceFirst("\\" + substitution, replacement);
                    }
                    device = device.trim();
                }
                else {
                    device = this.deviceReplacement;
                }
            }
            else if (matcher.groupCount() >= 1) {
                device = matcher.group(1);
            }
            return device;
        }
        
        private List<String> getSubstitutions(final String deviceReplacement) {
            final Matcher matcher = DevicePattern.SUBSTITUTIONS_PATTERN.matcher(deviceReplacement);
            final List<String> substitutions = new ArrayList<String>();
            while (matcher.find()) {
                substitutions.add(matcher.group());
            }
            return substitutions;
        }
        
        static {
            SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");
        }
    }
}
