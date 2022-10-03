package org.apache.tomcat.util.buf;

import java.util.Locale;
import org.apache.tomcat.util.res.StringManager;

public enum EncodedSolidusHandling
{
    DECODE("decode"), 
    REJECT("reject"), 
    PASS_THROUGH("passthrough");
    
    private static final StringManager sm;
    private final String value;
    
    private EncodedSolidusHandling(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public static EncodedSolidusHandling fromString(final String from) {
        final String trimmedLower = from.trim().toLowerCase(Locale.ENGLISH);
        for (final EncodedSolidusHandling value : values()) {
            if (value.getValue().equals(trimmedLower)) {
                return value;
            }
        }
        throw new IllegalStateException(EncodedSolidusHandling.sm.getString("encodedSolidusHandling.invalid", from));
    }
    
    static {
        sm = StringManager.getManager(EncodedSolidusHandling.class);
    }
}
