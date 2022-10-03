package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSerTxImpl extends XmlComplexContentImpl implements CTSerTx
{
    private static final long serialVersionUID = 1L;
    private static final QName STRREF$0;
    private static final QName V$2;
    
    public CTSerTxImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTStrRef getStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrRef ctStrRef = (CTStrRef)this.get_store().find_element_user(CTSerTxImpl.STRREF$0, 0);
            if (ctStrRef == null) {
                return null;
            }
            return ctStrRef;
        }
    }
    
    public boolean isSetStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSerTxImpl.STRREF$0) != 0;
        }
    }
    
    public void setStrRef(final CTStrRef ctStrRef) {
        this.generatedSetterHelperImpl((XmlObject)ctStrRef, CTSerTxImpl.STRREF$0, 0, (short)1);
    }
    
    public CTStrRef addNewStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrRef)this.get_store().add_element_user(CTSerTxImpl.STRREF$0);
        }
    }
    
    public void unsetStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSerTxImpl.STRREF$0, 0);
        }
    }
    
    public String getV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSerTxImpl.V$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTSerTxImpl.V$2, 0);
        }
    }
    
    public boolean isSetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSerTxImpl.V$2) != 0;
        }
    }
    
    public void setV(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSerTxImpl.V$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSerTxImpl.V$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetV(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTSerTxImpl.V$2, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTSerTxImpl.V$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSerTxImpl.V$2, 0);
        }
    }
    
    static {
        STRREF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "strRef");
        V$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "v");
    }
}
