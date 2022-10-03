package com.azul.crs.com.fasterxml.jackson.databind.jsontype;

import java.util.Objects;
import java.io.Serializable;

public final class NamedType implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Class<?> _class;
    protected final int _hashCode;
    protected String _name;
    
    public NamedType(final Class<?> c) {
        this(c, null);
    }
    
    public NamedType(final Class<?> c, final String name) {
        this._class = c;
        this._hashCode = c.getName().hashCode() + ((name == null) ? 0 : name.hashCode());
        this.setName(name);
    }
    
    public Class<?> getType() {
        return this._class;
    }
    
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = ((name == null || name.isEmpty()) ? null : name);
    }
    
    public boolean hasName() {
        return this._name != null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final NamedType other = (NamedType)o;
        return this._class == other._class && Objects.equals(this._name, other._name);
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    @Override
    public String toString() {
        return "[NamedType, class " + this._class.getName() + ", name: " + ((this._name == null) ? "null" : ("'" + this._name + "'")) + "]";
    }
}
