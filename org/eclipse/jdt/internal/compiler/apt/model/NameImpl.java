package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.element.Name;

public class NameImpl implements Name
{
    private final String _name;
    
    private NameImpl() {
        this._name = null;
    }
    
    public NameImpl(final CharSequence cs) {
        this._name = cs.toString();
    }
    
    public NameImpl(final char[] chars) {
        this._name = String.valueOf(chars);
    }
    
    @Override
    public boolean contentEquals(final CharSequence cs) {
        return this._name.equals(cs.toString());
    }
    
    @Override
    public char charAt(final int index) {
        return this._name.charAt(index);
    }
    
    @Override
    public int length() {
        return this._name.length();
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this._name.subSequence(start, end);
    }
    
    @Override
    public String toString() {
        return this._name;
    }
    
    @Override
    public int hashCode() {
        return this._name.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NameImpl other = (NameImpl)obj;
        return this._name.equals(other._name);
    }
}
