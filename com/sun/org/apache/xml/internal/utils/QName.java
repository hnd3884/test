package com.sun.org.apache.xml.internal.utils;

import java.util.StringTokenizer;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.Stack;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.Serializable;

public class QName implements Serializable
{
    static final long serialVersionUID = 467434581652829920L;
    protected String _localName;
    protected String _namespaceURI;
    protected String _prefix;
    public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
    private int m_hashCode;
    
    public QName() {
    }
    
    public QName(final String namespaceURI, final String localName) {
        this(namespaceURI, localName, false);
    }
    
    public QName(final String namespaceURI, final String localName, final boolean validate) {
        if (localName == null) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
        }
        if (validate && !XML11Char.isXML11ValidNCName(localName)) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this._namespaceURI = namespaceURI;
        this._localName = localName;
        this.m_hashCode = this.toString().hashCode();
    }
    
    public QName(final String namespaceURI, final String prefix, final String localName) {
        this(namespaceURI, prefix, localName, false);
    }
    
    public QName(final String namespaceURI, final String prefix, final String localName, final boolean validate) {
        if (localName == null) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
        }
        if (validate) {
            if (!XML11Char.isXML11ValidNCName(localName)) {
                throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
            }
            if (null != prefix && !XML11Char.isXML11ValidNCName(prefix)) {
                throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_PREFIX_INVALID", null));
            }
        }
        this._namespaceURI = namespaceURI;
        this._prefix = prefix;
        this._localName = localName;
        this.m_hashCode = this.toString().hashCode();
    }
    
    public QName(final String localName) {
        this(localName, false);
    }
    
    public QName(final String localName, final boolean validate) {
        if (localName == null) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_NULL", null));
        }
        if (validate && !XML11Char.isXML11ValidNCName(localName)) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this._namespaceURI = null;
        this._localName = localName;
        this.m_hashCode = this.toString().hashCode();
    }
    
    public QName(final String qname, final Stack namespaces) {
        this(qname, namespaces, false);
    }
    
    public QName(final String qname, final Stack namespaces, final boolean validate) {
        String namespace = null;
        String prefix = null;
        final int indexOfNSSep = qname.indexOf(58);
        if (indexOfNSSep > 0) {
            prefix = qname.substring(0, indexOfNSSep);
            if (prefix.equals("xml")) {
                namespace = "http://www.w3.org/XML/1998/namespace";
            }
            else {
                if (prefix.equals("xmlns")) {
                    return;
                }
                final int depth = namespaces.size();
                for (int i = depth - 1; i >= 0; --i) {
                    for (NameSpace ns = (NameSpace)namespaces.elementAt(i); null != ns; ns = ns.m_next) {
                        if (null != ns.m_prefix && prefix.equals(ns.m_prefix)) {
                            namespace = ns.m_uri;
                            i = -1;
                            break;
                        }
                    }
                }
            }
            if (null == namespace) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { prefix }));
            }
        }
        this._localName = ((indexOfNSSep < 0) ? qname : qname.substring(indexOfNSSep + 1));
        if (validate && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName))) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this._namespaceURI = namespace;
        this._prefix = prefix;
        this.m_hashCode = this.toString().hashCode();
    }
    
    public QName(final String qname, final Element namespaceContext, final PrefixResolver resolver) {
        this(qname, namespaceContext, resolver, false);
    }
    
    public QName(final String qname, final Element namespaceContext, final PrefixResolver resolver, final boolean validate) {
        this._namespaceURI = null;
        final int indexOfNSSep = qname.indexOf(58);
        if (indexOfNSSep > 0 && null != namespaceContext) {
            final String prefix = qname.substring(0, indexOfNSSep);
            this._prefix = prefix;
            if (prefix.equals("xml")) {
                this._namespaceURI = "http://www.w3.org/XML/1998/namespace";
            }
            else {
                if (prefix.equals("xmlns")) {
                    return;
                }
                this._namespaceURI = resolver.getNamespaceForPrefix(prefix, namespaceContext);
            }
            if (null == this._namespaceURI) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { prefix }));
            }
        }
        this._localName = ((indexOfNSSep < 0) ? qname : qname.substring(indexOfNSSep + 1));
        if (validate && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName))) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this.m_hashCode = this.toString().hashCode();
    }
    
    public QName(final String qname, final PrefixResolver resolver) {
        this(qname, resolver, false);
    }
    
    public QName(final String qname, final PrefixResolver resolver, final boolean validate) {
        String prefix = null;
        this._namespaceURI = null;
        final int indexOfNSSep = qname.indexOf(58);
        if (indexOfNSSep > 0) {
            prefix = qname.substring(0, indexOfNSSep);
            if (prefix.equals("xml")) {
                this._namespaceURI = "http://www.w3.org/XML/1998/namespace";
            }
            else {
                this._namespaceURI = resolver.getNamespaceForPrefix(prefix);
            }
            if (null == this._namespaceURI) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_PREFIX_MUST_RESOLVE", new Object[] { prefix }));
            }
            this._localName = qname.substring(indexOfNSSep + 1);
        }
        else {
            if (indexOfNSSep == 0) {
                throw new RuntimeException(XMLMessages.createXMLMessage("ER_NAME_CANT_START_WITH_COLON", null));
            }
            this._localName = qname;
        }
        if (validate && (this._localName == null || !XML11Char.isXML11ValidNCName(this._localName))) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_ARG_LOCALNAME_INVALID", null));
        }
        this.m_hashCode = this.toString().hashCode();
        this._prefix = prefix;
    }
    
    public String getNamespaceURI() {
        return this._namespaceURI;
    }
    
    public String getPrefix() {
        return this._prefix;
    }
    
    public String getLocalName() {
        return this._localName;
    }
    
    @Override
    public String toString() {
        return (this._prefix != null) ? (this._prefix + ":" + this._localName) : ((this._namespaceURI != null) ? ("{" + this._namespaceURI + "}" + this._localName) : this._localName);
    }
    
    public String toNamespacedString() {
        return (this._namespaceURI != null) ? ("{" + this._namespaceURI + "}" + this._localName) : this._localName;
    }
    
    public String getNamespace() {
        return this.getNamespaceURI();
    }
    
    public String getLocalPart() {
        return this.getLocalName();
    }
    
    @Override
    public int hashCode() {
        return this.m_hashCode;
    }
    
    public boolean equals(final String ns, final String localPart) {
        final String thisnamespace = this.getNamespaceURI();
        return this.getLocalName().equals(localPart) && ((null == thisnamespace || null == ns) ? (null == thisnamespace && null == ns) : thisnamespace.equals(ns));
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QName) {
            final QName qname = (QName)object;
            final String thisnamespace = this.getNamespaceURI();
            final String thatnamespace = qname.getNamespaceURI();
            return this.getLocalName().equals(qname.getLocalName()) && ((null == thisnamespace || null == thatnamespace) ? (null == thisnamespace && null == thatnamespace) : thisnamespace.equals(thatnamespace));
        }
        return false;
    }
    
    public static QName getQNameFromString(final String name) {
        final StringTokenizer tokenizer = new StringTokenizer(name, "{}", false);
        final String s1 = tokenizer.nextToken();
        final String s2 = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : null;
        QName qname;
        if (null == s2) {
            qname = new QName(null, s1);
        }
        else {
            qname = new QName(s1, s2);
        }
        return qname;
    }
    
    public static boolean isXMLNSDecl(final String attRawName) {
        return attRawName.startsWith("xmlns") && (attRawName.equals("xmlns") || attRawName.startsWith("xmlns:"));
    }
    
    public static String getPrefixFromXMLNSDecl(final String attRawName) {
        final int index = attRawName.indexOf(58);
        return (index >= 0) ? attRawName.substring(index + 1) : "";
    }
    
    public static String getLocalPart(final String qname) {
        final int index = qname.indexOf(58);
        return (index < 0) ? qname : qname.substring(index + 1);
    }
    
    public static String getPrefixPart(final String qname) {
        final int index = qname.indexOf(58);
        return (index >= 0) ? qname.substring(0, index) : "";
    }
}
