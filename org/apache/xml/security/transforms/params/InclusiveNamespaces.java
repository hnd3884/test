package org.apache.xml.security.transforms.params;

import java.util.StringTokenizer;
import java.util.SortedSet;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.ElementProxy;

public class InclusiveNamespaces extends ElementProxy implements TransformParam
{
    public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
    public static final String _ATT_EC_PREFIXLIST = "PrefixList";
    public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";
    
    public InclusiveNamespaces(final Document document, final String s) {
        this(document, prefixStr2Set(s));
    }
    
    public InclusiveNamespaces(final Document document, final Set set) {
        super(document);
        final StringBuffer sb = new StringBuffer();
        final Iterator iterator = new TreeSet(set).iterator();
        while (iterator.hasNext()) {
            final String s = (String)iterator.next();
            if (s.equals("xmlns")) {
                sb.append("#default ");
            }
            else {
                sb.append(s + " ");
            }
        }
        super._constructionElement.setAttributeNS(null, "PrefixList", sb.toString().trim());
    }
    
    public String getInclusiveNamespaces() {
        return super._constructionElement.getAttributeNS(null, "PrefixList");
    }
    
    public InclusiveNamespaces(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public static SortedSet prefixStr2Set(final String s) {
        final TreeSet set = new TreeSet();
        if (s == null || s.length() == 0) {
            return set;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " \t\r\n");
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.equals("#default")) {
                set.add("xmlns");
            }
            else {
                set.add(nextToken);
            }
        }
        return set;
    }
    
    public String getBaseNamespace() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }
    
    public String getBaseLocalName() {
        return "InclusiveNamespaces";
    }
}
