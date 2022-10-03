package com.sun.xml.internal.stream.buffer;

import org.xml.sax.Attributes;

public final class AttributesHolder implements Attributes
{
    private static final int DEFAULT_CAPACITY = 8;
    private static final int ITEM_SIZE = 8;
    private static final int PREFIX = 0;
    private static final int URI = 1;
    private static final int LOCAL_NAME = 2;
    private static final int QNAME = 3;
    private static final int TYPE = 4;
    private static final int VALUE = 5;
    private int _attributeCount;
    private String[] _strings;
    
    public AttributesHolder() {
        this._strings = new String[64];
    }
    
    @Override
    public final int getLength() {
        return this._attributeCount;
    }
    
    public final String getPrefix(final int index) {
        return (index >= 0 && index < this._attributeCount) ? this._strings[(index << 3) + 0] : null;
    }
    
    @Override
    public final String getLocalName(final int index) {
        return (index >= 0 && index < this._attributeCount) ? this._strings[(index << 3) + 2] : null;
    }
    
    @Override
    public final String getQName(final int index) {
        return (index >= 0 && index < this._attributeCount) ? this._strings[(index << 3) + 3] : null;
    }
    
    @Override
    public final String getType(final int index) {
        return (index >= 0 && index < this._attributeCount) ? this._strings[(index << 3) + 4] : null;
    }
    
    @Override
    public final String getURI(final int index) {
        return (index >= 0 && index < this._attributeCount) ? this._strings[(index << 3) + 1] : null;
    }
    
    @Override
    public final String getValue(final int index) {
        return (index >= 0 && index < this._attributeCount) ? this._strings[(index << 3) + 5] : null;
    }
    
    @Override
    public final int getIndex(final String qName) {
        for (int i = 0; i < this._attributeCount; ++i) {
            if (qName.equals(this._strings[(i << 3) + 3])) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public final String getType(final String qName) {
        final int i = (this.getIndex(qName) << 3) + 4;
        return (i >= 0) ? this._strings[i] : null;
    }
    
    @Override
    public final String getValue(final String qName) {
        final int i = (this.getIndex(qName) << 3) + 5;
        return (i >= 0) ? this._strings[i] : null;
    }
    
    @Override
    public final int getIndex(final String uri, final String localName) {
        for (int i = 0; i < this._attributeCount; ++i) {
            if (localName.equals(this._strings[(i << 3) + 2]) && uri.equals(this._strings[(i << 3) + 1])) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public final String getType(final String uri, final String localName) {
        final int i = (this.getIndex(uri, localName) << 3) + 4;
        return (i >= 0) ? this._strings[i] : null;
    }
    
    @Override
    public final String getValue(final String uri, final String localName) {
        final int i = (this.getIndex(uri, localName) << 3) + 5;
        return (i >= 0) ? this._strings[i] : null;
    }
    
    public final void clear() {
        if (this._attributeCount > 0) {
            for (int i = 0; i < this._attributeCount; ++i) {
                this._strings[(i << 3) + 5] = null;
            }
            this._attributeCount = 0;
        }
    }
    
    public final void addAttributeWithQName(final String uri, final String localName, final String qName, final String type, final String value) {
        final int i = this._attributeCount << 3;
        if (i == this._strings.length) {
            this.resize(i);
        }
        this._strings[i + 0] = null;
        this._strings[i + 1] = uri;
        this._strings[i + 2] = localName;
        this._strings[i + 3] = qName;
        this._strings[i + 4] = type;
        this._strings[i + 5] = value;
        ++this._attributeCount;
    }
    
    public final void addAttributeWithPrefix(final String prefix, final String uri, final String localName, final String type, final String value) {
        final int i = this._attributeCount << 3;
        if (i == this._strings.length) {
            this.resize(i);
        }
        this._strings[i + 0] = prefix;
        this._strings[i + 1] = uri;
        this._strings[i + 2] = localName;
        this._strings[i + 3] = null;
        this._strings[i + 4] = type;
        this._strings[i + 5] = value;
        ++this._attributeCount;
    }
    
    private void resize(final int length) {
        final int newLength = length * 2;
        final String[] strings = new String[newLength];
        System.arraycopy(this._strings, 0, strings, 0, length);
        this._strings = strings;
    }
}
