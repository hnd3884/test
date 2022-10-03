package javax.script;

import java.util.Map;

public interface Bindings extends Map<String, Object>
{
    Object put(final String p0, final Object p1);
    
    void putAll(final Map<? extends String, ?> p0);
    
    boolean containsKey(final Object p0);
    
    Object get(final Object p0);
    
    Object remove(final Object p0);
}
