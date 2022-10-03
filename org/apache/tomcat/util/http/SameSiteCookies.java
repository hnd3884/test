package org.apache.tomcat.util.http;

import org.apache.tomcat.util.res.StringManager;

public enum SameSiteCookies
{
    UNSET("Unset"), 
    NONE("None"), 
    LAX("Lax"), 
    STRICT("Strict");
    
    private static final StringManager sm;
    private final String value;
    
    private SameSiteCookies(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public static SameSiteCookies fromString(final String value) {
        for (final SameSiteCookies sameSiteCookies : values()) {
            if (sameSiteCookies.getValue().equalsIgnoreCase(value)) {
                return sameSiteCookies;
            }
        }
        throw new IllegalStateException(SameSiteCookies.sm.getString("cookies.invalidSameSiteCookies", new Object[] { value }));
    }
    
    static {
        sm = StringManager.getManager((Class)SameSiteCookies.class);
    }
}
