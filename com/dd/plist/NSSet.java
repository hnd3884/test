package com.dd.plist;

import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class NSSet extends NSObject
{
    private Set<NSObject> set;
    private boolean ordered;
    
    public NSSet() {
        this.ordered = false;
        this.set = new LinkedHashSet<NSObject>();
    }
    
    public NSSet(final boolean ordered) {
        this.ordered = false;
        if (!(this.ordered = ordered)) {
            this.set = new LinkedHashSet<NSObject>();
        }
        else {
            this.set = new TreeSet<NSObject>();
        }
    }
    
    public NSSet(final NSObject... objects) {
        this.ordered = false;
        (this.set = new LinkedHashSet<NSObject>()).addAll(Arrays.asList(objects));
    }
    
    public NSSet(final boolean ordered, final NSObject... objects) {
        this.ordered = false;
        if (!(this.ordered = ordered)) {
            this.set = new LinkedHashSet<NSObject>();
        }
        else {
            this.set = new TreeSet<NSObject>();
        }
        this.set.addAll(Arrays.asList(objects));
    }
    
    public synchronized void addObject(final NSObject obj) {
        this.set.add(obj);
    }
    
    public synchronized void removeObject(final NSObject obj) {
        this.set.remove(obj);
    }
    
    public synchronized NSObject[] allObjects() {
        return this.set.toArray(new NSObject[this.count()]);
    }
    
    public synchronized NSObject anyObject() {
        if (this.set.isEmpty()) {
            return null;
        }
        return this.set.iterator().next();
    }
    
    public boolean containsObject(final NSObject obj) {
        return this.set.contains(obj);
    }
    
    public synchronized NSObject member(final NSObject obj) {
        for (final NSObject o : this.set) {
            if (o.equals(obj)) {
                return o;
            }
        }
        return null;
    }
    
    public synchronized boolean intersectsSet(final NSSet otherSet) {
        for (final NSObject o : this.set) {
            if (otherSet.containsObject(o)) {
                return true;
            }
        }
        return false;
    }
    
    public synchronized boolean isSubsetOfSet(final NSSet otherSet) {
        for (final NSObject o : this.set) {
            if (!otherSet.containsObject(o)) {
                return false;
            }
        }
        return true;
    }
    
    public synchronized Iterator<NSObject> objectIterator() {
        return this.set.iterator();
    }
    
    Set<NSObject> getSet() {
        return this.set;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + ((this.set != null) ? this.set.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NSSet other = (NSSet)obj;
        return this.set == other.set || (this.set != null && this.set.equals(other.set));
    }
    
    public synchronized int count() {
        return this.set.size();
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<array>");
        xml.append(NSObject.NEWLINE);
        for (final NSObject o : this.set) {
            o.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</array>");
    }
    
    @Override
    void assignIDs(final BinaryPropertyListWriter out) {
        super.assignIDs(out);
        for (final NSObject obj : this.set) {
            obj.assignIDs(out);
        }
    }
    
    @Override
    void toBinary(final BinaryPropertyListWriter out) throws IOException {
        if (this.ordered) {
            out.writeIntHeader(11, this.set.size());
        }
        else {
            out.writeIntHeader(12, this.set.size());
        }
        for (final NSObject obj : this.set) {
            out.writeID(out.getID(obj));
        }
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        final NSObject[] array = this.allObjects();
        ascii.append('(');
        int indexOfLastNewLine = ascii.lastIndexOf(NSSet.NEWLINE);
        for (int i = 0; i < array.length; ++i) {
            final Class<?> objClass = array[i].getClass();
            if ((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) && indexOfLastNewLine != ascii.length()) {
                ascii.append(NSSet.NEWLINE);
                indexOfLastNewLine = ascii.length();
                array[i].toASCII(ascii, level + 1);
            }
            else {
                if (i != 0) {
                    ascii.append(' ');
                }
                array[i].toASCII(ascii, 0);
            }
            if (i != array.length - 1) {
                ascii.append(',');
            }
            if (ascii.length() - indexOfLastNewLine > 80) {
                ascii.append(NSSet.NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(')');
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        final NSObject[] array = this.allObjects();
        ascii.append('(');
        int indexOfLastNewLine = ascii.lastIndexOf(NSSet.NEWLINE);
        for (int i = 0; i < array.length; ++i) {
            final Class<?> objClass = array[i].getClass();
            if ((objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) && indexOfLastNewLine != ascii.length()) {
                ascii.append(NSSet.NEWLINE);
                indexOfLastNewLine = ascii.length();
                array[i].toASCIIGnuStep(ascii, level + 1);
            }
            else {
                if (i != 0) {
                    ascii.append(' ');
                }
                array[i].toASCIIGnuStep(ascii, 0);
            }
            if (i != array.length - 1) {
                ascii.append(',');
            }
            if (ascii.length() - indexOfLastNewLine > 80) {
                ascii.append(NSSet.NEWLINE);
                indexOfLastNewLine = ascii.length();
            }
        }
        ascii.append(')');
    }
}
