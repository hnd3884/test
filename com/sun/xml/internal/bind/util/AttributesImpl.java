package com.sun.xml.internal.bind.util;

import org.xml.sax.Attributes;

public class AttributesImpl implements Attributes
{
    int length;
    String[] data;
    
    public AttributesImpl() {
        this.length = 0;
        this.data = null;
    }
    
    public AttributesImpl(final Attributes atts) {
        this.setAttributes(atts);
    }
    
    @Override
    public int getLength() {
        return this.length;
    }
    
    @Override
    public String getURI(final int index) {
        if (index >= 0 && index < this.length) {
            return this.data[index * 5];
        }
        return null;
    }
    
    @Override
    public String getLocalName(final int index) {
        if (index >= 0 && index < this.length) {
            return this.data[index * 5 + 1];
        }
        return null;
    }
    
    @Override
    public String getQName(final int index) {
        if (index >= 0 && index < this.length) {
            return this.data[index * 5 + 2];
        }
        return null;
    }
    
    @Override
    public String getType(final int index) {
        if (index >= 0 && index < this.length) {
            return this.data[index * 5 + 3];
        }
        return null;
    }
    
    @Override
    public String getValue(final int index) {
        if (index >= 0 && index < this.length) {
            return this.data[index * 5 + 4];
        }
        return null;
    }
    
    @Override
    public int getIndex(final String uri, final String localName) {
        for (int max = this.length * 5, i = 0; i < max; i += 5) {
            if (this.data[i].equals(uri) && this.data[i + 1].equals(localName)) {
                return i / 5;
            }
        }
        return -1;
    }
    
    public int getIndexFast(final String uri, final String localName) {
        for (int i = (this.length - 1) * 5; i >= 0; i -= 5) {
            if (this.data[i + 1] == localName && this.data[i] == uri) {
                return i / 5;
            }
        }
        return -1;
    }
    
    @Override
    public int getIndex(final String qName) {
        for (int max = this.length * 5, i = 0; i < max; i += 5) {
            if (this.data[i + 2].equals(qName)) {
                return i / 5;
            }
        }
        return -1;
    }
    
    @Override
    public String getType(final String uri, final String localName) {
        for (int max = this.length * 5, i = 0; i < max; i += 5) {
            if (this.data[i].equals(uri) && this.data[i + 1].equals(localName)) {
                return this.data[i + 3];
            }
        }
        return null;
    }
    
    @Override
    public String getType(final String qName) {
        for (int max = this.length * 5, i = 0; i < max; i += 5) {
            if (this.data[i + 2].equals(qName)) {
                return this.data[i + 3];
            }
        }
        return null;
    }
    
    @Override
    public String getValue(final String uri, final String localName) {
        for (int max = this.length * 5, i = 0; i < max; i += 5) {
            if (this.data[i].equals(uri) && this.data[i + 1].equals(localName)) {
                return this.data[i + 4];
            }
        }
        return null;
    }
    
    @Override
    public String getValue(final String qName) {
        for (int max = this.length * 5, i = 0; i < max; i += 5) {
            if (this.data[i + 2].equals(qName)) {
                return this.data[i + 4];
            }
        }
        return null;
    }
    
    public void clear() {
        if (this.data != null) {
            for (int i = 0; i < this.length * 5; ++i) {
                this.data[i] = null;
            }
        }
        this.length = 0;
    }
    
    public void setAttributes(final Attributes atts) {
        this.clear();
        this.length = atts.getLength();
        if (this.length > 0) {
            this.data = new String[this.length * 5];
            for (int i = 0; i < this.length; ++i) {
                this.data[i * 5] = atts.getURI(i);
                this.data[i * 5 + 1] = atts.getLocalName(i);
                this.data[i * 5 + 2] = atts.getQName(i);
                this.data[i * 5 + 3] = atts.getType(i);
                this.data[i * 5 + 4] = atts.getValue(i);
            }
        }
    }
    
    public void addAttribute(final String uri, final String localName, final String qName, final String type, final String value) {
        this.ensureCapacity(this.length + 1);
        this.data[this.length * 5] = uri;
        this.data[this.length * 5 + 1] = localName;
        this.data[this.length * 5 + 2] = qName;
        this.data[this.length * 5 + 3] = type;
        this.data[this.length * 5 + 4] = value;
        ++this.length;
    }
    
    public void setAttribute(final int index, final String uri, final String localName, final String qName, final String type, final String value) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5] = uri;
            this.data[index * 5 + 1] = localName;
            this.data[index * 5 + 2] = qName;
            this.data[index * 5 + 3] = type;
            this.data[index * 5 + 4] = value;
        }
        else {
            this.badIndex(index);
        }
    }
    
    public void removeAttribute(int index) {
        if (index >= 0 && index < this.length) {
            if (index < this.length - 1) {
                System.arraycopy(this.data, (index + 1) * 5, this.data, index * 5, (this.length - index - 1) * 5);
            }
            index = (this.length - 1) * 5;
            this.data[index++] = null;
            this.data[index++] = null;
            this.data[index++] = null;
            this.data[index++] = null;
            this.data[index] = null;
            --this.length;
        }
        else {
            this.badIndex(index);
        }
    }
    
    public void setURI(final int index, final String uri) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5] = uri;
        }
        else {
            this.badIndex(index);
        }
    }
    
    public void setLocalName(final int index, final String localName) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 1] = localName;
        }
        else {
            this.badIndex(index);
        }
    }
    
    public void setQName(final int index, final String qName) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 2] = qName;
        }
        else {
            this.badIndex(index);
        }
    }
    
    public void setType(final int index, final String type) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 3] = type;
        }
        else {
            this.badIndex(index);
        }
    }
    
    public void setValue(final int index, final String value) {
        if (index >= 0 && index < this.length) {
            this.data[index * 5 + 4] = value;
        }
        else {
            this.badIndex(index);
        }
    }
    
    private void ensureCapacity(final int n) {
        if (n <= 0) {
            return;
        }
        int max;
        if (this.data == null || this.data.length == 0) {
            max = 25;
        }
        else {
            if (this.data.length >= n * 5) {
                return;
            }
            max = this.data.length;
        }
        while (max < n * 5) {
            max *= 2;
        }
        final String[] newData = new String[max];
        if (this.length > 0) {
            System.arraycopy(this.data, 0, newData, 0, this.length * 5);
        }
        this.data = newData;
    }
    
    private void badIndex(final int index) throws ArrayIndexOutOfBoundsException {
        final String msg = "Attempt to modify attribute at illegal index: " + index;
        throw new ArrayIndexOutOfBoundsException(msg);
    }
}
