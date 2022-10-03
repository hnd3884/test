package org.apache.xmlbeans.impl.common;

import org.apache.xmlbeans.xml.stream.XMLName;

public class XmlNameImpl implements XMLName
{
    private String namespaceUri;
    private String localName;
    private String prefix;
    private int hash;
    
    public XmlNameImpl() {
        this.namespaceUri = null;
        this.localName = null;
        this.prefix = null;
        this.hash = 0;
    }
    
    public XmlNameImpl(final String localName) {
        this.namespaceUri = null;
        this.localName = null;
        this.prefix = null;
        this.hash = 0;
        this.localName = localName;
    }
    
    public XmlNameImpl(final String namespaceUri, final String localName) {
        this.namespaceUri = null;
        this.localName = null;
        this.prefix = null;
        this.hash = 0;
        this.setNamespaceUri(namespaceUri);
        this.localName = localName;
    }
    
    public XmlNameImpl(final String namespaceUri, final String localName, final String prefix) {
        this.namespaceUri = null;
        this.localName = null;
        this.prefix = null;
        this.hash = 0;
        this.setNamespaceUri(namespaceUri);
        this.localName = localName;
        this.prefix = prefix;
    }
    
    @Override
    public String getNamespaceUri() {
        return this.namespaceUri;
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    @Override
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setNamespaceUri(final String namespaceUri) {
        this.hash = 0;
        if (namespaceUri != null && namespaceUri.equals("")) {
            return;
        }
        this.namespaceUri = namespaceUri;
    }
    
    public void setLocalName(final String localName) {
        this.localName = localName;
        this.hash = 0;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public String getQualifiedName() {
        if (this.prefix != null && this.prefix.length() > 0) {
            return this.prefix + ":" + this.localName;
        }
        return this.localName;
    }
    
    @Override
    public String toString() {
        if (this.getNamespaceUri() != null) {
            return "['" + this.getNamespaceUri() + "']:" + this.getQualifiedName();
        }
        return this.getQualifiedName();
    }
    
    @Override
    public final int hashCode() {
        int tmp_hash = this.hash;
        if (tmp_hash == 0) {
            tmp_hash = 17;
            if (this.namespaceUri != null) {
                tmp_hash = 37 * tmp_hash + this.namespaceUri.hashCode();
            }
            if (this.localName != null) {
                tmp_hash = 37 * tmp_hash + this.localName.hashCode();
            }
            this.hash = tmp_hash;
        }
        return tmp_hash;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof XMLName) {
            final XMLName name = (XMLName)obj;
            final String lname = this.localName;
            Label_0055: {
                if (lname == null) {
                    if (name.getLocalName() == null) {
                        break Label_0055;
                    }
                }
                else if (lname.equals(name.getLocalName())) {
                    break Label_0055;
                }
                return false;
            }
            final String uri = this.namespaceUri;
            return (uri == null) ? (name.getNamespaceUri() == null) : uri.equals(name.getNamespaceUri());
        }
        return false;
    }
}
