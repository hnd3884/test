package java.beans;

import java.util.Locale;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

class NameGenerator
{
    private Map<Object, String> valueToName;
    private Map<String, Integer> nameToCount;
    
    public NameGenerator() {
        this.valueToName = new IdentityHashMap<Object, String>();
        this.nameToCount = new HashMap<String, Integer>();
    }
    
    public void clear() {
        this.valueToName.clear();
        this.nameToCount.clear();
    }
    
    public static String unqualifiedClassName(final Class clazz) {
        if (clazz.isArray()) {
            return unqualifiedClassName(clazz.getComponentType()) + "Array";
        }
        final String name = clazz.getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
    
    public static String capitalize(final String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
    }
    
    public String instanceName(final Object o) {
        if (o == null) {
            return "null";
        }
        if (o instanceof Class) {
            return unqualifiedClassName((Class)o);
        }
        final String s = this.valueToName.get(o);
        if (s != null) {
            return s;
        }
        final String unqualifiedClassName = unqualifiedClassName(o.getClass());
        final Integer n = this.nameToCount.get(unqualifiedClassName);
        final int n2 = (n == null) ? 0 : (n + 1);
        this.nameToCount.put(unqualifiedClassName, new Integer(n2));
        final String string = unqualifiedClassName + n2;
        this.valueToName.put(o, string);
        return string;
    }
}
