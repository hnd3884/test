package org.apache.commons.text;

import java.util.ResourceBundle;
import java.util.Map;
import org.apache.commons.text.lookup.StringLookup;

@Deprecated
public abstract class StrLookup<V> implements StringLookup
{
    private static final StrLookup<String> NONE_LOOKUP;
    private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP;
    
    public static StrLookup<?> noneLookup() {
        return StrLookup.NONE_LOOKUP;
    }
    
    public static StrLookup<String> systemPropertiesLookup() {
        return StrLookup.SYSTEM_PROPERTIES_LOOKUP;
    }
    
    public static <V> StrLookup<V> mapLookup(final Map<String, V> map) {
        return new MapStrLookup<V>(map);
    }
    
    public static StrLookup<String> resourceBundleLookup(final ResourceBundle resourceBundle) {
        return new ResourceBundleLookup(resourceBundle);
    }
    
    protected StrLookup() {
    }
    
    static {
        NONE_LOOKUP = new MapStrLookup<String>(null);
        SYSTEM_PROPERTIES_LOOKUP = new SystemPropertiesStrLookup();
    }
    
    static class MapStrLookup<V> extends StrLookup<V>
    {
        private final Map<String, V> map;
        
        MapStrLookup(final Map<String, V> map) {
            this.map = map;
        }
        
        @Override
        public String lookup(final String key) {
            if (this.map == null) {
                return null;
            }
            final Object obj = this.map.get(key);
            if (obj == null) {
                return null;
            }
            return obj.toString();
        }
        
        @Override
        public String toString() {
            return super.toString() + " [map=" + this.map + "]";
        }
    }
    
    private static final class ResourceBundleLookup extends StrLookup<String>
    {
        private final ResourceBundle resourceBundle;
        
        private ResourceBundleLookup(final ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }
        
        @Override
        public String lookup(final String key) {
            if (this.resourceBundle == null || key == null || !this.resourceBundle.containsKey(key)) {
                return null;
            }
            return this.resourceBundle.getString(key);
        }
        
        @Override
        public String toString() {
            return super.toString() + " [resourceBundle=" + this.resourceBundle + "]";
        }
    }
    
    private static final class SystemPropertiesStrLookup extends StrLookup<String>
    {
        @Override
        public String lookup(final String key) {
            if (key.length() > 0) {
                try {
                    return System.getProperty(key);
                }
                catch (final SecurityException scex) {
                    return null;
                }
            }
            return null;
        }
    }
}
