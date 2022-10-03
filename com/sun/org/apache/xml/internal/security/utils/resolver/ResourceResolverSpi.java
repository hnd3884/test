package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.File;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import java.util.Map;
import com.sun.org.slf4j.internal.Logger;

public abstract class ResourceResolverSpi
{
    private static final Logger LOG;
    protected Map<String, String> properties;
    
    public abstract XMLSignatureInput engineResolveURI(final ResourceResolverContext p0) throws ResourceResolverException;
    
    public void engineSetProperty(final String s, final String s2) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(s, s2);
    }
    
    public String engineGetProperty(final String s) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(s);
    }
    
    public void engineAddProperies(final Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            if (this.properties == null) {
                this.properties = new HashMap<String, String>();
            }
            this.properties.putAll(map);
        }
    }
    
    public boolean engineIsThreadSafe() {
        return false;
    }
    
    public abstract boolean engineCanResolveURI(final ResourceResolverContext p0);
    
    public String[] engineGetPropertyKeys() {
        return new String[0];
    }
    
    public boolean understandsProperty(final String s) {
        final String[] engineGetPropertyKeys = this.engineGetPropertyKeys();
        if (engineGetPropertyKeys != null) {
            final String[] array = engineGetPropertyKeys;
            for (int length = array.length, i = 0; i < length; ++i) {
                if (array[i].equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String fixURI(String s) {
        s = s.replace(File.separatorChar, '/');
        if (s.length() >= 4) {
            final char upperCase = Character.toUpperCase(s.charAt(0));
            final char char1 = s.charAt(1);
            final char char2 = s.charAt(2);
            final char char3 = s.charAt(3);
            if ('A' <= upperCase && upperCase <= 'Z' && char1 == ':' && char2 == '/' && char3 != '/') {
                ResourceResolverSpi.LOG.debug("Found DOS filename: {}", s);
            }
        }
        if (s.length() >= 2 && s.charAt(1) == ':') {
            final char upperCase2 = Character.toUpperCase(s.charAt(0));
            if ('A' <= upperCase2 && upperCase2 <= 'Z') {
                s = "/" + s;
            }
        }
        return s;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ResourceResolverSpi.class);
    }
}
