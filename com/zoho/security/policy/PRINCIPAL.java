package com.zoho.security.policy;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum PRINCIPAL
{
    USER("user"), 
    PORTAL_USER("puser"), 
    ROLE("role"), 
    GROUP("group");
    
    private static final Logger LOGGER;
    private String value;
    
    private PRINCIPAL(final String value) {
        this.value = null;
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public static PRINCIPAL valueOfString(final String value) {
        PRINCIPAL requested = null;
        try {
            requested = valueOf(value.toUpperCase());
        }
        catch (final IllegalArgumentException e) {
            PRINCIPAL.LOGGER.log(Level.WARNING, "Invalid principal value : {0}", value);
        }
        if (requested == null) {
            switch (value) {
                case "puser": {
                    return PRINCIPAL.PORTAL_USER;
                }
            }
        }
        return requested;
    }
    
    static {
        LOGGER = Logger.getLogger(PRINCIPAL.class.getName());
    }
}
