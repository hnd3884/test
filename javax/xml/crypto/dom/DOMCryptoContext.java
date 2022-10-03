package javax.xml.crypto.dom;

import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import org.w3c.dom.Element;
import java.net.URI;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.KeySelector;
import java.util.HashMap;
import javax.xml.crypto.XMLCryptoContext;

public class DOMCryptoContext implements XMLCryptoContext
{
    private HashMap nsMap;
    private HashMap idMap;
    private HashMap objMap;
    private String baseURI;
    private KeySelector ks;
    private URIDereferencer dereferencer;
    private HashMap propMap;
    private String defaultPrefix;
    
    protected DOMCryptoContext() {
        this.nsMap = new HashMap();
        this.idMap = new HashMap();
        this.objMap = new HashMap();
        this.propMap = new HashMap();
    }
    
    public String getNamespacePrefix(final String s, final String s2) {
        if (s == null) {
            throw new NullPointerException("namespaceURI cannot be null");
        }
        final String s3 = this.nsMap.get(s);
        return (s3 != null) ? s3 : s2;
    }
    
    public String putNamespacePrefix(final String s, final String s2) {
        if (s == null) {
            throw new NullPointerException("namespaceURI is null");
        }
        return this.nsMap.put(s, s2);
    }
    
    public String getDefaultNamespacePrefix() {
        return this.defaultPrefix;
    }
    
    public void setDefaultNamespacePrefix(final String defaultPrefix) {
        this.defaultPrefix = defaultPrefix;
    }
    
    public String getBaseURI() {
        return this.baseURI;
    }
    
    public void setBaseURI(final String baseURI) {
        if (baseURI != null) {
            URI.create(baseURI);
        }
        this.baseURI = baseURI;
    }
    
    public URIDereferencer getURIDereferencer() {
        return this.dereferencer;
    }
    
    public void setURIDereferencer(final URIDereferencer dereferencer) {
        this.dereferencer = dereferencer;
    }
    
    public Object getProperty(final String s) {
        if (s == null) {
            throw new NullPointerException("name is null");
        }
        return this.propMap.get(s);
    }
    
    public Object setProperty(final String s, final Object o) {
        if (s == null) {
            throw new NullPointerException("name is null");
        }
        return this.propMap.put(s, o);
    }
    
    public KeySelector getKeySelector() {
        return this.ks;
    }
    
    public void setKeySelector(final KeySelector ks) {
        this.ks = ks;
    }
    
    public Element getElementById(final String s) {
        if (s == null) {
            throw new NullPointerException("idValue is null");
        }
        return this.idMap.get(s);
    }
    
    public void setIdAttributeNS(final Element element, final String s, final String s2) {
        if (element == null) {
            throw new NullPointerException("element is null");
        }
        if (s2 == null) {
            throw new NullPointerException("localName is null");
        }
        final String attributeNS = element.getAttributeNS(s, s2);
        if (attributeNS == null || attributeNS.length() == 0) {
            throw new IllegalArgumentException(s2 + " is not an " + "attribute");
        }
        this.idMap.put(attributeNS, element);
    }
    
    public Iterator iterator() {
        return Collections.unmodifiableMap((Map<?, ?>)this.idMap).entrySet().iterator();
    }
    
    public Object get(final Object o) {
        return this.objMap.get(o);
    }
    
    public Object put(final Object o, final Object o2) {
        return this.objMap.put(o, o2);
    }
}
