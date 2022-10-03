package jdk.jfr.internal.settings;

import java.util.Iterator;
import java.util.Set;

final class BooleanValue
{
    private String value;
    private boolean booleanValue;
    
    private BooleanValue(final boolean booleanValue) {
        this.value = "false";
        this.booleanValue = booleanValue;
        this.value = (booleanValue ? "true" : "false");
    }
    
    public String union(final Set<String> set) {
        final Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            if ("true".equals(iterator.next())) {
                return "true";
            }
        }
        return "false";
    }
    
    public void setValue(final String value) {
        this.value = value;
        this.booleanValue = Boolean.valueOf(value);
    }
    
    public final String getValue() {
        return this.value;
    }
    
    public boolean getBoolean() {
        return this.booleanValue;
    }
    
    public static BooleanValue valueOf(final String s) {
        if ("true".equals(s)) {
            return new BooleanValue(true);
        }
        if ("false".equals(s)) {
            return new BooleanValue(false);
        }
        throw new InternalError("Unknown default value for settings '" + s + "'");
    }
}
