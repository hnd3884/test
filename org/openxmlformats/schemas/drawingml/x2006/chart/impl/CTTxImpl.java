package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTxImpl extends XmlComplexContentImpl implements CTTx
{
    private static final long serialVersionUID = 1L;
    private static final QName STRREF$0;
    private static final QName RICH$2;
    
    public CTTxImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTStrRef getStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrRef ctStrRef = (CTStrRef)this.get_store().find_element_user(CTTxImpl.STRREF$0, 0);
            if (ctStrRef == null) {
                return null;
            }
            return ctStrRef;
        }
    }
    
    public boolean isSetStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTxImpl.STRREF$0) != 0;
        }
    }
    
    public void setStrRef(final CTStrRef ctStrRef) {
        this.generatedSetterHelperImpl((XmlObject)ctStrRef, CTTxImpl.STRREF$0, 0, (short)1);
    }
    
    public CTStrRef addNewStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrRef)this.get_store().add_element_user(CTTxImpl.STRREF$0);
        }
    }
    
    public void unsetStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTxImpl.STRREF$0, 0);
        }
    }
    
    public CTTextBody getRich() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTTxImpl.RICH$2, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetRich() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTxImpl.RICH$2) != 0;
        }
    }
    
    public void setRich(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTTxImpl.RICH$2, 0, (short)1);
    }
    
    public CTTextBody addNewRich() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTTxImpl.RICH$2);
        }
    }
    
    public void unsetRich() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTxImpl.RICH$2, 0);
        }
    }
    
    static {
        STRREF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "strRef");
        RICH$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "rich");
    }
}
