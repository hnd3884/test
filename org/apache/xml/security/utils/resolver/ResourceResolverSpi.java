package org.apache.xml.security.utils.resolver;

import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.util.HashMap;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import java.util.Map;
import org.apache.commons.logging.Log;

public abstract class ResourceResolverSpi
{
    static Log log;
    protected Map _properties;
    
    public ResourceResolverSpi() {
        this._properties = null;
    }
    
    public abstract XMLSignatureInput engineResolve(final Attr p0, final String p1) throws ResourceResolverException;
    
    public void engineSetProperty(final String s, final String s2) {
        if (this._properties == null) {
            this._properties = new HashMap();
        }
        this._properties.put(s, s2);
    }
    
    public String engineGetProperty(final String s) {
        if (this._properties == null) {
            return null;
        }
        return this._properties.get(s);
    }
    
    public void engineAddProperies(final Map map) {
        if (map != null) {
            if (this._properties == null) {
                this._properties = new HashMap();
            }
            this._properties.putAll(map);
        }
    }
    
    public boolean engineIsThreadSafe() {
        return false;
    }
    
    public abstract boolean engineCanResolve(final Attr p0, final String p1);
    
    public String[] engineGetPropertyKeys() {
        return new String[0];
    }
    
    public boolean understandsProperty(final String s) {
        final String[] engineGetPropertyKeys = this.engineGetPropertyKeys();
        if (engineGetPropertyKeys != null) {
            for (int i = 0; i < engineGetPropertyKeys.length; ++i) {
                if (engineGetPropertyKeys[i].equals(s)) {
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
            if ('A' <= upperCase && upperCase <= 'Z' && char1 == ':' && char2 == '/' && char3 != '/' && ResourceResolverSpi.log.isDebugEnabled()) {
                ResourceResolverSpi.log.debug((Object)("Found DOS filename: " + s));
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
        ResourceResolverSpi.log = LogFactory.getLog(ResourceResolverSpi.class.getName());
    }
}
