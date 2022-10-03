package org.apache.xmlbeans.impl.config;

import java.util.HashSet;
import java.util.Set;

public class NameSetBuilder
{
    private boolean _isFinite;
    private Set _finiteSet;
    
    public NameSetBuilder() {
        this._isFinite = true;
        this._finiteSet = new HashSet();
    }
    
    public void invert() {
        this._isFinite = !this._isFinite;
    }
    
    public void add(final String name) {
        if (this._isFinite) {
            this._finiteSet.add(name);
        }
        else {
            this._finiteSet.remove(name);
        }
    }
    
    public NameSet toNameSet() {
        if (this._finiteSet.size() != 0) {
            return NameSet.newInstance(this._isFinite, this._finiteSet);
        }
        if (this._isFinite) {
            return NameSet.EMPTY;
        }
        return NameSet.EVERYTHING;
    }
}
