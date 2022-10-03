package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.SimpleValue;
import java.util.Calendar;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.ResponderIDType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.OCSPIdentifierType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class OCSPIdentifierTypeImpl extends XmlComplexContentImpl implements OCSPIdentifierType
{
    private static final long serialVersionUID = 1L;
    private static final QName RESPONDERID$0;
    private static final QName PRODUCEDAT$2;
    private static final QName URI$4;
    
    public OCSPIdentifierTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public ResponderIDType getResponderID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ResponderIDType responderIDType = (ResponderIDType)this.get_store().find_element_user(OCSPIdentifierTypeImpl.RESPONDERID$0, 0);
            if (responderIDType == null) {
                return null;
            }
            return responderIDType;
        }
    }
    
    public void setResponderID(final ResponderIDType responderIDType) {
        this.generatedSetterHelperImpl((XmlObject)responderIDType, OCSPIdentifierTypeImpl.RESPONDERID$0, 0, (short)1);
    }
    
    public ResponderIDType addNewResponderID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ResponderIDType)this.get_store().add_element_user(OCSPIdentifierTypeImpl.RESPONDERID$0);
        }
    }
    
    public Calendar getProducedAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(OCSPIdentifierTypeImpl.PRODUCEDAT$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetProducedAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_element_user(OCSPIdentifierTypeImpl.PRODUCEDAT$2, 0);
        }
    }
    
    public void setProducedAt(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(OCSPIdentifierTypeImpl.PRODUCEDAT$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(OCSPIdentifierTypeImpl.PRODUCEDAT$2);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetProducedAt(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(OCSPIdentifierTypeImpl.PRODUCEDAT$2, 0);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_element_user(OCSPIdentifierTypeImpl.PRODUCEDAT$2);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public String getURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(OCSPIdentifierTypeImpl.URI$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(OCSPIdentifierTypeImpl.URI$4);
        }
    }
    
    public boolean isSetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(OCSPIdentifierTypeImpl.URI$4) != null;
        }
    }
    
    public void setURI(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(OCSPIdentifierTypeImpl.URI$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(OCSPIdentifierTypeImpl.URI$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetURI(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(OCSPIdentifierTypeImpl.URI$4);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(OCSPIdentifierTypeImpl.URI$4);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(OCSPIdentifierTypeImpl.URI$4);
        }
    }
    
    static {
        RESPONDERID$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ResponderID");
        PRODUCEDAT$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ProducedAt");
        URI$4 = new QName("", "URI");
    }
}
