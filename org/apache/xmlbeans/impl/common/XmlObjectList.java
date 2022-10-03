package org.apache.xmlbeans.impl.common;

import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;

public class XmlObjectList
{
    private final XmlObject[] _objects;
    
    public XmlObjectList(final int objectCount) {
        this._objects = new XmlObject[objectCount];
    }
    
    public boolean set(final XmlObject o, final int index) {
        if (this._objects[index] != null) {
            return false;
        }
        this._objects[index] = o;
        return true;
    }
    
    public boolean filled() {
        for (int i = 0; i < this._objects.length; ++i) {
            if (this._objects[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    public int unfilled() {
        for (int i = 0; i < this._objects.length; ++i) {
            if (this._objects[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XmlObjectList)) {
            return false;
        }
        final XmlObjectList other = (XmlObjectList)o;
        if (other._objects.length != this._objects.length) {
            return false;
        }
        for (int i = 0; i < this._objects.length; ++i) {
            if (this._objects[i] == null || other._objects[i] == null) {
                return false;
            }
            if (!this._objects[i].valueEquals(other._objects[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < this._objects.length; ++i) {
            if (this._objects[i] != null) {
                h = 31 * h + this._objects[i].valueHashCode();
            }
        }
        return h;
    }
    
    private static String prettytrim(final String s) {
        int end;
        for (end = s.length(); end > 0 && XMLChar.isSpace(s.charAt(end - 1)); --end) {}
        int start;
        for (start = 0; start < end && XMLChar.isSpace(s.charAt(start)); ++start) {}
        return s.substring(start, end);
    }
    
    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        for (int i = 0; i < this._objects.length; ++i) {
            if (i != 0) {
                b.append(" ");
            }
            b.append(prettytrim(((SimpleValue)this._objects[i]).getStringValue()));
        }
        return b.toString();
    }
}
