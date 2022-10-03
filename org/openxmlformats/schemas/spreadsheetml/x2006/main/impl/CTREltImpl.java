package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTREltImpl extends XmlComplexContentImpl implements CTRElt
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    private static final QName T$2;
    
    public CTREltImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRPrElt getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPrElt ctrPrElt = (CTRPrElt)this.get_store().find_element_user(CTREltImpl.RPR$0, 0);
            if (ctrPrElt == null) {
                return null;
            }
            return ctrPrElt;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTREltImpl.RPR$0) != 0;
        }
    }
    
    public void setRPr(final CTRPrElt ctrPrElt) {
        this.generatedSetterHelperImpl((XmlObject)ctrPrElt, CTREltImpl.RPR$0, 0, (short)1);
    }
    
    public CTRPrElt addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPrElt)this.get_store().add_element_user(CTREltImpl.RPR$0);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTREltImpl.RPR$0, 0);
        }
    }
    
    public String getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTREltImpl.T$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTREltImpl.T$2, 0);
        }
    }
    
    public void setT(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTREltImpl.T$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTREltImpl.T$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetT(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTREltImpl.T$2, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTREltImpl.T$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rPr");
        T$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "t");
    }
}
