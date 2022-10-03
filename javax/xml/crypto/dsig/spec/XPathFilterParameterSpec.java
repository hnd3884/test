package javax.xml.crypto.dsig.spec;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

public final class XPathFilterParameterSpec implements TransformParameterSpec
{
    private String xPath;
    private Map nsMap;
    
    public XPathFilterParameterSpec(final String xPath) {
        if (xPath == null) {
            throw new NullPointerException();
        }
        this.xPath = xPath;
        this.nsMap = Collections.EMPTY_MAP;
    }
    
    public XPathFilterParameterSpec(final String xPath, final Map map) {
        if (xPath == null || map == null) {
            throw new NullPointerException();
        }
        this.xPath = xPath;
        this.nsMap = new HashMap(map);
        final Iterator iterator = this.nsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                throw new ClassCastException("not a String");
            }
        }
        this.nsMap = Collections.unmodifiableMap((Map<?, ?>)this.nsMap);
    }
    
    public String getXPath() {
        return this.xPath;
    }
    
    public Map getNamespaceMap() {
        return this.nsMap;
    }
}
