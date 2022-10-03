package javax.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum StandardLocation implements JavaFileManager.Location
{
    CLASS_OUTPUT, 
    SOURCE_OUTPUT, 
    CLASS_PATH, 
    SOURCE_PATH, 
    ANNOTATION_PROCESSOR_PATH, 
    PLATFORM_CLASS_PATH, 
    NATIVE_HEADER_OUTPUT;
    
    private static final ConcurrentMap<String, JavaFileManager.Location> locations;
    
    public static JavaFileManager.Location locationFor(final String s) {
        if (StandardLocation.locations.isEmpty()) {
            for (final StandardLocation standardLocation : values()) {
                StandardLocation.locations.putIfAbsent(standardLocation.getName(), standardLocation);
            }
        }
        StandardLocation.locations.putIfAbsent(s.toString(), new JavaFileManager.Location() {
            @Override
            public String getName() {
                return s;
            }
            
            @Override
            public boolean isOutputLocation() {
                return s.endsWith("_OUTPUT");
            }
        });
        return StandardLocation.locations.get(s);
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    @Override
    public boolean isOutputLocation() {
        switch (this) {
            case CLASS_OUTPUT:
            case SOURCE_OUTPUT:
            case NATIVE_HEADER_OUTPUT: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        locations = new ConcurrentHashMap<String, JavaFileManager.Location>();
    }
}
