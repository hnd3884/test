package org.apache.xmlbeans.impl.inst2xsd.util;

import javax.xml.namespace.QName;

public class Element
{
    private QName _name;
    private Element _ref;
    private boolean _isGlobal;
    private int _minOccurs;
    private int _maxOccurs;
    public static final int UNBOUNDED = -1;
    private boolean _isNillable;
    private Type _type;
    private String _comment;
    
    public Element() {
        this._name = null;
        this._ref = null;
        this._isGlobal = false;
        this._minOccurs = 1;
        this._maxOccurs = 1;
        this._isNillable = false;
        this._type = null;
        this._comment = null;
    }
    
    public QName getName() {
        return this._name;
    }
    
    public void setName(final QName name) {
        this._name = name;
    }
    
    public boolean isRef() {
        return this._ref != null;
    }
    
    public Element getRef() {
        return this._ref;
    }
    
    public void setRef(final Element ref) {
        assert !this._isGlobal;
        this._ref = ref;
        this._type = null;
    }
    
    public boolean isGlobal() {
        return this._isGlobal;
    }
    
    public void setGlobal(final boolean isGlobal) {
        this._isGlobal = isGlobal;
        this._minOccurs = 1;
        this._maxOccurs = 1;
    }
    
    public int getMinOccurs() {
        return this._minOccurs;
    }
    
    public void setMinOccurs(final int minOccurs) {
        this._minOccurs = minOccurs;
    }
    
    public int getMaxOccurs() {
        return this._maxOccurs;
    }
    
    public void setMaxOccurs(final int maxOccurs) {
        this._maxOccurs = maxOccurs;
    }
    
    public boolean isNillable() {
        return this._isNillable;
    }
    
    public void setNillable(final boolean isNillable) {
        this._isNillable = isNillable;
    }
    
    public Type getType() {
        return this.isRef() ? this.getRef().getType() : this._type;
    }
    
    public void setType(final Type type) {
        assert !this.isRef();
        this._type = type;
    }
    
    public String getComment() {
        return this._comment;
    }
    
    public void setComment(final String comment) {
        this._comment = comment;
    }
    
    @Override
    public String toString() {
        return "\n  Element{ _name = " + this._name + ", _ref = " + (this._ref != null) + ", _isGlobal = " + this._isGlobal + ", _minOccurs = " + this._minOccurs + ", _maxOccurs = " + this._maxOccurs + ", _isNillable = " + this._isNillable + ", _comment = " + this._comment + ",\n    _type = " + ((this._type == null) ? "null" : (this._type.isGlobal() ? this._type.getName().toString() : this._type.toString())) + "\n  }";
    }
}
