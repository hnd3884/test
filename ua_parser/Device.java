package ua_parser;

import java.util.Map;

public class Device
{
    public static final Device OTHER;
    public final String family;
    
    public Device(final String family) {
        this.family = family;
    }
    
    public static Device fromMap(final Map<String, String> m) {
        return new Device(m.get("family"));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Device)) {
            return false;
        }
        final Device o = (Device)other;
        return (this.family != null && this.family.equals(o.family)) || this.family == o.family;
    }
    
    @Override
    public int hashCode() {
        return (this.family == null) ? 0 : this.family.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("{\"family\": %s}", (this.family == null) ? "\"\"" : ('\"' + this.family + '\"'));
    }
    
    static {
        OTHER = new Device("Other");
    }
}
