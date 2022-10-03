package com.dd.plist;

import java.io.IOException;
import java.util.Arrays;

public class NSArray extends NSObject
{
    private NSObject[] array;
    
    public NSArray(final int length) {
        this.array = new NSObject[length];
    }
    
    public NSArray(final NSObject... a) {
        this.array = a;
    }
    
    public NSObject objectAtIndex(final int i) {
        return this.array[i];
    }
    
    public void remove(final int i) {
        if (i >= this.array.length || i < 0) {
            throw new ArrayIndexOutOfBoundsException("invalid index:" + i + ";the array length is " + this.array.length);
        }
        final NSObject[] newArray = new NSObject[this.array.length - 1];
        System.arraycopy(this.array, 0, newArray, 0, i);
        System.arraycopy(this.array, i + 1, newArray, i, this.array.length - i - 1);
        this.array = newArray;
    }
    
    public void setValue(final int key, final Object value) {
        this.array[key] = NSObject.fromJavaObject(value);
    }
    
    public NSObject[] getArray() {
        return this.array;
    }
    
    public int count() {
        return this.array.length;
    }
    
    public boolean containsObject(final Object obj) {
        final NSObject nso = NSObject.fromJavaObject(obj);
        for (final NSObject elem : this.array) {
            if (elem == null) {
                if (obj == null) {
                    return true;
                }
            }
            else if (elem.equals(nso)) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOfObject(final Object obj) {
        final NSObject nso = NSObject.fromJavaObject(obj);
        for (int i = 0; i < this.array.length; ++i) {
            if (this.array[i].equals(nso)) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexOfIdenticalObject(final Object obj) {
        final NSObject nso = NSObject.fromJavaObject(obj);
        for (int i = 0; i < this.array.length; ++i) {
            if (this.array[i] == nso) {
                return i;
            }
        }
        return -1;
    }
    
    public NSObject lastObject() {
        return this.array[this.array.length - 1];
    }
    
    public NSObject[] objectsAtIndexes(final int... indexes) {
        final NSObject[] result = new NSObject[indexes.length];
        Arrays.sort(indexes);
        for (int i = 0; i < indexes.length; ++i) {
            result[i] = this.array[indexes[i]];
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass().equals(NSArray.class)) {
            return Arrays.equals(((NSArray)obj).getArray(), this.array);
        }
        final NSObject nso = NSObject.fromJavaObject(obj);
        return nso.getClass().equals(NSArray.class) && Arrays.equals(((NSArray)nso).getArray(), this.array);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Arrays.deepHashCode(this.array);
        return hash;
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<array>");
        xml.append(NSObject.NEWLINE);
        for (final NSObject o : this.array) {
            o.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</array>");
    }
    
    @Override
    void assignIDs(final BinaryPropertyListWriter out) {
        super.assignIDs(out);
        for (final NSObject obj : this.array) {
            obj.assignIDs(out);
        }
    }
    
    @Override
    void toBinary(final BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(10, this.array.length);
        for (final NSObject obj : this.array) {
            out.writeID(out.getID(obj));
        }
    }
    
    public String toASCIIPropertyList() {
        final StringBuilder ascii = new StringBuilder();
        this.toASCII(ascii, 0);
        ascii.append(NSArray.NEWLINE);
        return ascii.toString();
    }
    
    public String toGnuStepASCIIPropertyList() {
        final StringBuilder ascii = new StringBuilder();
        this.toASCIIGnuStep(ascii, 0);
        ascii.append(NSArray.NEWLINE);
        return ascii.toString();
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('(');
        int indexOfLastNewLine = ascii.lastIndexOf(NSArray.NEWLINE);
        for (int i = 0; i < this.array.length; ++i) {
            final Class<?> objClass = this.array[i].getClass();
            if ((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) && indexOfLastNewLine != ascii.length()) {
                ascii.append(NSArray.NEWLINE);
                indexOfLastNewLine = ascii.length();
                this.array[i].toASCII(ascii, level + 1);
            }
            else {
                if (i != 0) {
                    ascii.append(' ');
                }
                this.array[i].toASCII(ascii, 0);
            }
            if (i != this.array.length - 1) {
                ascii.append(',');
            }
            if (ascii.length() - indexOfLastNewLine > 80) {
                ascii.append(NSArray.NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(')');
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('(');
        int indexOfLastNewLine = ascii.lastIndexOf(NSArray.NEWLINE);
        for (int i = 0; i < this.array.length; ++i) {
            final Class<?> objClass = this.array[i].getClass();
            if ((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) && indexOfLastNewLine != ascii.length()) {
                ascii.append(NSArray.NEWLINE);
                indexOfLastNewLine = ascii.length();
                this.array[i].toASCIIGnuStep(ascii, level + 1);
            }
            else {
                if (i != 0) {
                    ascii.append(' ');
                }
                this.array[i].toASCIIGnuStep(ascii, 0);
            }
            if (i != this.array.length - 1) {
                ascii.append(',');
            }
            if (ascii.length() - indexOfLastNewLine > 80) {
                ascii.append(NSArray.NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(')');
    }
}
