package com.sun.xml.internal.fastinfoset;

import javax.xml.namespace.QName;

public class QualifiedName
{
    public String prefix;
    public String namespaceName;
    public String localName;
    public String qName;
    public int index;
    public int prefixIndex;
    public int namespaceNameIndex;
    public int localNameIndex;
    public int attributeId;
    public int attributeHash;
    private QName qNameObject;
    
    public QualifiedName() {
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName, final String qName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
    }
    
    public void set(final String prefix, final String namespaceName, final String localName, final String qName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
        this.qNameObject = null;
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName, final String qName, final int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName, final String localName, final String qName, final int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName, final String qName, final int index, final int prefixIndex, final int namespaceNameIndex, final int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName, final String localName, final String qName, final int index, final int prefixIndex, final int namespaceNameIndex, final int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = qName;
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.qNameObject = null;
        return this;
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = this.createQNameString(prefix, localName);
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName, final String localName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = this.createQNameString(prefix, localName);
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName, final int prefixIndex, final int namespaceNameIndex, final int localNameIndex, final char[] charBuffer) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        if (charBuffer != null) {
            final int l1 = prefix.length();
            final int l2 = localName.length();
            final int total = l1 + l2 + 1;
            if (total < charBuffer.length) {
                prefix.getChars(0, l1, charBuffer, 0);
                charBuffer[l1] = ':';
                localName.getChars(0, l2, charBuffer, l1 + 1);
                this.qName = new String(charBuffer, 0, total);
            }
            else {
                this.qName = this.createQNameString(prefix, localName);
            }
        }
        else {
            this.qName = this.localName;
        }
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.index = -1;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName, final String localName, final int prefixIndex, final int namespaceNameIndex, final int localNameIndex, final char[] charBuffer) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        if (charBuffer != null) {
            final int l1 = prefix.length();
            final int l2 = localName.length();
            final int total = l1 + l2 + 1;
            if (total < charBuffer.length) {
                prefix.getChars(0, l1, charBuffer, 0);
                charBuffer[l1] = ':';
                localName.getChars(0, l2, charBuffer, l1 + 1);
                this.qName = new String(charBuffer, 0, total);
            }
            else {
                this.qName = this.createQNameString(prefix, localName);
            }
        }
        else {
            this.qName = this.localName;
        }
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.index = -1;
        this.qNameObject = null;
        return this;
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName, final int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = this.createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName, final String localName, final int index) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = this.createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }
    
    public QualifiedName(final String prefix, final String namespaceName, final String localName, final int index, final int prefixIndex, final int namespaceNameIndex, final int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = this.createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName, final String localName, final int index, final int prefixIndex, final int namespaceNameIndex, final int localNameIndex) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = localName;
        this.qName = this.createQNameString(prefix, localName);
        this.index = index;
        this.prefixIndex = prefixIndex + 1;
        this.namespaceNameIndex = namespaceNameIndex + 1;
        this.localNameIndex = localNameIndex;
        this.qNameObject = null;
        return this;
    }
    
    public QualifiedName(final String prefix, final String namespaceName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = "";
        this.qName = "";
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
    }
    
    public final QualifiedName set(final String prefix, final String namespaceName) {
        this.prefix = prefix;
        this.namespaceName = namespaceName;
        this.localName = "";
        this.qName = "";
        this.index = -1;
        this.prefixIndex = 0;
        this.namespaceNameIndex = 0;
        this.localNameIndex = -1;
        this.qNameObject = null;
        return this;
    }
    
    public final QName getQName() {
        if (this.qNameObject == null) {
            this.qNameObject = new QName(this.namespaceName, this.localName, this.prefix);
        }
        return this.qNameObject;
    }
    
    public final String getQNameString() {
        if (this.qName != "") {
            return this.qName;
        }
        return this.qName = this.createQNameString(this.prefix, this.localName);
    }
    
    public final void createAttributeValues(final int size) {
        this.attributeId = (this.localNameIndex | this.namespaceNameIndex << 20);
        this.attributeHash = this.localNameIndex % size;
    }
    
    private final String createQNameString(final String p, final String l) {
        if (p != null && p.length() > 0) {
            final StringBuffer b = new StringBuffer(p);
            b.append(':');
            b.append(l);
            return b.toString();
        }
        return l;
    }
}
