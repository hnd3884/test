package jdk.management.jfr;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.CompositeData;
import jdk.jfr.Configuration;
import java.util.Map;

public final class ConfigurationInfo
{
    private final Map<String, String> settings;
    private final String name;
    private final String label;
    private final String description;
    private final String provider;
    private final String contents;
    
    ConfigurationInfo(final Configuration configuration) {
        this.settings = configuration.getSettings();
        this.name = configuration.getName();
        this.label = configuration.getLabel();
        this.description = configuration.getDescription();
        this.provider = configuration.getProvider();
        this.contents = configuration.getContents();
    }
    
    private ConfigurationInfo(final CompositeData compositeData) {
        this.settings = createMap(compositeData.get("settings"));
        this.name = (String)compositeData.get("name");
        this.label = (String)compositeData.get("label");
        this.description = (String)compositeData.get("description");
        this.provider = (String)compositeData.get("provider");
        this.contents = (String)compositeData.get("contents");
    }
    
    private static Map<String, String> createMap(final Object o) {
        if (o instanceof TabularData) {
            final TabularData tabularData = (TabularData)o;
            final HashMap hashMap = new HashMap(tabularData.values().size());
            for (final Object next : tabularData.values()) {
                if (next instanceof CompositeData) {
                    final CompositeData compositeData = (CompositeData)next;
                    final Object value = compositeData.get("key");
                    final Object value2 = compositeData.get("value");
                    if (!(value instanceof String) || !(value2 instanceof String)) {
                        continue;
                    }
                    hashMap.put((Object)value, (Object)value2);
                }
            }
            return Collections.unmodifiableMap((Map<? extends String, ? extends String>)hashMap);
        }
        return Collections.emptyMap();
    }
    
    public String getProvider() {
        return this.provider;
    }
    
    public String getContents() {
        return this.contents;
    }
    
    public Map<String, String> getSettings() {
        return this.settings;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public static ConfigurationInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        return new ConfigurationInfo(compositeData);
    }
    
    @Override
    public String toString() {
        final Stringifier stringifier = new Stringifier();
        stringifier.add("name", this.name);
        stringifier.add("label", this.label);
        stringifier.add("description", this.description);
        stringifier.add("provider", this.provider);
        return stringifier.toString();
    }
}
