package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Element;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Set;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;

public class InclusiveNamespaces extends ElementProxy implements TransformParam
{
    public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
    public static final String _ATT_EC_PREFIXLIST = "PrefixList";
    public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";
    
    public InclusiveNamespaces(final Document document, final String s) {
        this(document, prefixStr2Set(s));
    }
    
    public InclusiveNamespaces(final Document document, final Set<String> set) {
        super(document);
        SortedSet set2;
        if (set instanceof SortedSet) {
            set2 = (SortedSet)set;
        }
        else {
            set2 = new TreeSet(set);
        }
        final StringBuilder sb = new StringBuilder();
        for (final String s : set2) {
            if ("xmlns".equals(s)) {
                sb.append("#default ");
            }
            else {
                sb.append(s);
                sb.append(" ");
            }
        }
        this.setLocalAttribute("PrefixList", sb.toString().trim());
    }
    
    public InclusiveNamespaces(final Element element, final String s) throws XMLSecurityException {
        super(element, s);
    }
    
    public String getInclusiveNamespaces() {
        return this.getLocalAttribute("PrefixList");
    }
    
    public static SortedSet<String> prefixStr2Set(final String s) {
        final TreeSet set = new TreeSet();
        if (s == null || s.length() == 0) {
            return set;
        }
        for (final String s2 : s.split("\\s")) {
            if (s2.equals("#default")) {
                set.add("xmlns");
            }
            else {
                set.add(s2);
            }
        }
        return set;
    }
    
    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#";
    }
    
    @Override
    public String getBaseLocalName() {
        return "InclusiveNamespaces";
    }
}
