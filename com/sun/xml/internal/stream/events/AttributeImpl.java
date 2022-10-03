package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeImpl extends DummyEvent implements Attribute
{
    private String fValue;
    private String fNonNormalizedvalue;
    private QName fQName;
    private String fAttributeType;
    private boolean fIsSpecified;
    
    public AttributeImpl() {
        this.fAttributeType = "CDATA";
        this.init();
    }
    
    public AttributeImpl(final String name, final String value) {
        this.fAttributeType = "CDATA";
        this.init();
        this.fQName = new QName(name);
        this.fValue = value;
    }
    
    public AttributeImpl(final String prefix, final String name, final String value) {
        this(prefix, null, name, value, null, null, false);
    }
    
    public AttributeImpl(final String prefix, final String uri, final String localPart, final String value, final String type) {
        this(prefix, uri, localPart, value, null, type, false);
    }
    
    public AttributeImpl(final String prefix, final String uri, final String localPart, final String value, final String nonNormalizedvalue, final String type, final boolean isSpecified) {
        this(new QName(uri, localPart, prefix), value, nonNormalizedvalue, type, isSpecified);
    }
    
    public AttributeImpl(final QName qname, final String value, final String nonNormalizedvalue, final String type, final boolean isSpecified) {
        this.fAttributeType = "CDATA";
        this.init();
        this.fQName = qname;
        this.fValue = value;
        if (type != null && !type.equals("")) {
            this.fAttributeType = type;
        }
        this.fNonNormalizedvalue = nonNormalizedvalue;
        this.fIsSpecified = isSpecified;
    }
    
    @Override
    public String toString() {
        if (this.fQName.getPrefix() != null && this.fQName.getPrefix().length() > 0) {
            return this.fQName.getPrefix() + ":" + this.fQName.getLocalPart() + "='" + this.fValue + "'";
        }
        return this.fQName.getLocalPart() + "='" + this.fValue + "'";
    }
    
    public void setName(final QName name) {
        this.fQName = name;
    }
    
    @Override
    public QName getName() {
        return this.fQName;
    }
    
    public void setValue(final String value) {
        this.fValue = value;
    }
    
    @Override
    public String getValue() {
        return this.fValue;
    }
    
    public void setNonNormalizedValue(final String nonNormalizedvalue) {
        this.fNonNormalizedvalue = nonNormalizedvalue;
    }
    
    public String getNonNormalizedValue() {
        return this.fNonNormalizedvalue;
    }
    
    public void setAttributeType(final String attributeType) {
        this.fAttributeType = attributeType;
    }
    
    @Override
    public String getDTDType() {
        return this.fAttributeType;
    }
    
    public void setSpecified(final boolean isSpecified) {
        this.fIsSpecified = isSpecified;
    }
    
    @Override
    public boolean isSpecified() {
        return this.fIsSpecified;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write(this.toString());
    }
    
    protected void init() {
        this.setEventType(10);
    }
}
