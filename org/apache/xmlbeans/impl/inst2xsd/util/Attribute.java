package org.apache.xmlbeans.impl.inst2xsd.util;

import javax.xml.namespace.QName;

public class Attribute
{
    private QName _name;
    private Type _type;
    private Attribute _ref;
    private boolean _isGlobal;
    private boolean _isOptional;
    
    public Attribute() {
        this._ref = null;
        this._isGlobal = false;
        this._isOptional = false;
    }
    
    public QName getName() {
        return this._name;
    }
    
    public void setName(final QName name) {
        this._name = name;
    }
    
    public Type getType() {
        return this.isRef() ? this.getRef().getType() : this._type;
    }
    
    public void setType(final Type type) {
        assert !this.isRef();
        this._type = type;
    }
    
    public boolean isRef() {
        return this._ref != null;
    }
    
    public Attribute getRef() {
        return this._ref;
    }
    
    public void setRef(final Attribute ref) {
        assert !this.isGlobal();
        this._ref = ref;
        this._type = null;
    }
    
    public boolean isGlobal() {
        return this._isGlobal;
    }
    
    public void setGlobal(final boolean isGlobal) {
        this._isGlobal = isGlobal;
    }
    
    public boolean isOptional() {
        return this._isOptional;
    }
    
    public void setOptional(final boolean isOptional) {
        assert isOptional && !this.isGlobal() : "Global attributes cannot be optional.";
        this._isOptional = isOptional;
    }
    
    @Override
    public String toString() {
        return "\n    Attribute{_name=" + this._name + ", _type=" + this._type + ", _ref=" + (this._ref != null) + ", _isGlobal=" + this._isGlobal + ", _isOptional=" + this._isOptional + "}";
    }
}
