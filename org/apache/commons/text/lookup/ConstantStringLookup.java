package org.apache.commons.text.lookup;

import org.apache.commons.lang3.ClassUtils;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class ConstantStringLookup extends AbstractStringLookup
{
    private static final char FIELD_SEPRATOR = '.';
    static final ConstantStringLookup INSTANCE;
    private static ConcurrentHashMap<String, String> constantCache;
    
    static void clear() {
        ConstantStringLookup.constantCache.clear();
    }
    
    @Override
    public synchronized String lookup(final String key) {
        if (key == null) {
            return null;
        }
        String result = ConstantStringLookup.constantCache.get(key);
        if (result != null) {
            return result;
        }
        final int fieldPos = key.lastIndexOf(46);
        if (fieldPos < 0) {
            return null;
        }
        try {
            final Object value = this.resolveField(key.substring(0, fieldPos), key.substring(fieldPos + 1));
            if (value != null) {
                final String string = Objects.toString(value, null);
                ConstantStringLookup.constantCache.put(key, string);
                result = string;
            }
        }
        catch (final Exception ex) {
            return null;
        }
        return result;
    }
    
    protected Object resolveField(final String className, final String fieldName) throws Exception {
        final Class<?> clazz = this.fetchClass(className);
        if (clazz == null) {
            return null;
        }
        return clazz.getField(fieldName).get(null);
    }
    
    protected Class<?> fetchClass(final String className) throws ClassNotFoundException {
        return ClassUtils.getClass(className);
    }
    
    static {
        INSTANCE = new ConstantStringLookup();
        ConstantStringLookup.constantCache = new ConcurrentHashMap<String, String>();
    }
}
