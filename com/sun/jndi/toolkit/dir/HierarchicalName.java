package com.sun.jndi.toolkit.dir;

import javax.naming.Name;
import java.util.Locale;
import javax.naming.InvalidNameException;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import javax.naming.CompoundName;

final class HierarchicalName extends CompoundName
{
    private int hashValue;
    private static final long serialVersionUID = -6717336834584573168L;
    
    HierarchicalName() {
        super(new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }
            
            @Override
            public String nextElement() {
                throw new NoSuchElementException();
            }
        }, HierarchicalNameParser.mySyntax);
        this.hashValue = -1;
    }
    
    HierarchicalName(final Enumeration<String> enumeration, final Properties properties) {
        super(enumeration, properties);
        this.hashValue = -1;
    }
    
    HierarchicalName(final String s, final Properties properties) throws InvalidNameException {
        super(s, properties);
        this.hashValue = -1;
    }
    
    @Override
    public int hashCode() {
        if (this.hashValue == -1) {
            final String upperCase = this.toString().toUpperCase(Locale.ENGLISH);
            final int length = upperCase.length();
            int n = 0;
            final char[] array = new char[length];
            upperCase.getChars(0, length, array, 0);
            for (int i = length; i > 0; --i) {
                this.hashValue = this.hashValue * 37 + array[n++];
            }
        }
        return this.hashValue;
    }
    
    @Override
    public Name getPrefix(final int n) {
        return new HierarchicalName(super.getPrefix(n).getAll(), this.mySyntax);
    }
    
    @Override
    public Name getSuffix(final int n) {
        return new HierarchicalName(super.getSuffix(n).getAll(), this.mySyntax);
    }
    
    @Override
    public Object clone() {
        return new HierarchicalName(this.getAll(), this.mySyntax);
    }
}
