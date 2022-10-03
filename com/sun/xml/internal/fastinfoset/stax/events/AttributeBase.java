package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeBase extends EventBase implements Attribute
{
    private QName _QName;
    private String _value;
    private String _attributeType;
    private boolean _specified;
    
    public AttributeBase() {
        super(10);
        this._attributeType = null;
        this._specified = false;
    }
    
    public AttributeBase(final String name, final String value) {
        super(10);
        this._attributeType = null;
        this._specified = false;
        this._QName = new QName(name);
        this._value = value;
    }
    
    public AttributeBase(final QName qname, final String value) {
        this._attributeType = null;
        this._specified = false;
        this._QName = qname;
        this._value = value;
    }
    
    public AttributeBase(final String prefix, final String localName, final String value) {
        this(prefix, null, localName, value, null);
    }
    
    public AttributeBase(String prefix, final String namespaceURI, final String localName, final String value, final String attributeType) {
        this._attributeType = null;
        this._specified = false;
        if (prefix == null) {
            prefix = "";
        }
        this._QName = new QName(namespaceURI, localName, prefix);
        this._value = value;
        this._attributeType = ((attributeType == null) ? "CDATA" : attributeType);
    }
    
    public void setName(final QName name) {
        this._QName = name;
    }
    
    @Override
    public QName getName() {
        return this._QName;
    }
    
    public void setValue(final String value) {
        this._value = value;
    }
    
    public String getLocalName() {
        return this._QName.getLocalPart();
    }
    
    @Override
    public String getValue() {
        return this._value;
    }
    
    public void setAttributeType(final String attributeType) {
        this._attributeType = attributeType;
    }
    
    @Override
    public String getDTDType() {
        return this._attributeType;
    }
    
    @Override
    public boolean isSpecified() {
        return this._specified;
    }
    
    public void setSpecified(final boolean isSpecified) {
        this._specified = isSpecified;
    }
    
    @Override
    public String toString() {
        final String prefix = this._QName.getPrefix();
        if (!Util.isEmptyString(prefix)) {
            return prefix + ":" + this._QName.getLocalPart() + "='" + this._value + "'";
        }
        return this._QName.getLocalPart() + "='" + this._value + "'";
    }
}
