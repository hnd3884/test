package org.apache.xmlbeans;

import javax.xml.namespace.QName;

public final class QNameCache
{
    private static final float DEFAULT_LOAD = 0.7f;
    private final float loadFactor;
    private int numEntries;
    private int threshold;
    private int hashmask;
    private QName[] table;
    
    public QNameCache(final int initialCapacity, final float loadFactor) {
        this.numEntries = 0;
        assert initialCapacity > 0;
        assert loadFactor > 0.0f && loadFactor < 1.0f;
        int capacity;
        for (capacity = 16; capacity < initialCapacity; capacity <<= 1) {}
        this.loadFactor = loadFactor;
        this.hashmask = capacity - 1;
        this.threshold = (int)(capacity * loadFactor);
        this.table = new QName[capacity];
    }
    
    public QNameCache(final int initialCapacity) {
        this(initialCapacity, 0.7f);
    }
    
    public QName getName(final String uri, final String localName) {
        return this.getName(uri, localName, "");
    }
    
    public QName getName(String uri, final String localName, String prefix) {
        assert localName != null;
        if (uri == null) {
            uri = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        int index = hash(uri, localName, prefix) & this.hashmask;
        while (true) {
            final QName q = this.table[index];
            if (q == null) {
                ++this.numEntries;
                if (this.numEntries >= this.threshold) {
                    this.rehash();
                }
                return this.table[index] = new QName(uri, localName, prefix);
            }
            if (equals(q, uri, localName, prefix)) {
                return q;
            }
            index = (index - 1 & this.hashmask);
        }
    }
    
    private void rehash() {
        final int newLength = this.table.length * 2;
        final QName[] newTable = new QName[newLength];
        final int newHashmask = newLength - 1;
        for (int i = 0; i < this.table.length; ++i) {
            final QName q = this.table[i];
            if (q != null) {
                int newIndex;
                for (newIndex = (hash(q.getNamespaceURI(), q.getLocalPart(), q.getPrefix()) & newHashmask); newTable[newIndex] != null; newIndex = (newIndex - 1 & newHashmask)) {}
                newTable[newIndex] = q;
            }
        }
        this.table = newTable;
        this.hashmask = newHashmask;
        this.threshold = (int)(newLength * this.loadFactor);
    }
    
    private static int hash(final String uri, final String localName, final String prefix) {
        int h = 0;
        h += prefix.hashCode() << 10;
        h += uri.hashCode() << 5;
        h += localName.hashCode();
        return h;
    }
    
    private static boolean equals(final QName q, final String uri, final String localName, final String prefix) {
        return q.getLocalPart().equals(localName) && q.getNamespaceURI().equals(uri) && q.getPrefix().equals(prefix);
    }
}
