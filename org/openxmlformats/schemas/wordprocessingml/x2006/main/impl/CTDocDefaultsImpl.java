package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrDefault;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDocDefaultsImpl extends XmlComplexContentImpl implements CTDocDefaults
{
    private static final long serialVersionUID = 1L;
    private static final QName RPRDEFAULT$0;
    private static final QName PPRDEFAULT$2;
    
    public CTDocDefaultsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRPrDefault getRPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPrDefault ctrPrDefault = (CTRPrDefault)this.get_store().find_element_user(CTDocDefaultsImpl.RPRDEFAULT$0, 0);
            if (ctrPrDefault == null) {
                return null;
            }
            return ctrPrDefault;
        }
    }
    
    public boolean isSetRPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDocDefaultsImpl.RPRDEFAULT$0) != 0;
        }
    }
    
    public void setRPrDefault(final CTRPrDefault ctrPrDefault) {
        this.generatedSetterHelperImpl((XmlObject)ctrPrDefault, CTDocDefaultsImpl.RPRDEFAULT$0, 0, (short)1);
    }
    
    public CTRPrDefault addNewRPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPrDefault)this.get_store().add_element_user(CTDocDefaultsImpl.RPRDEFAULT$0);
        }
    }
    
    public void unsetRPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDocDefaultsImpl.RPRDEFAULT$0, 0);
        }
    }
    
    public CTPPrDefault getPPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPPrDefault ctpPrDefault = (CTPPrDefault)this.get_store().find_element_user(CTDocDefaultsImpl.PPRDEFAULT$2, 0);
            if (ctpPrDefault == null) {
                return null;
            }
            return ctpPrDefault;
        }
    }
    
    public boolean isSetPPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDocDefaultsImpl.PPRDEFAULT$2) != 0;
        }
    }
    
    public void setPPrDefault(final CTPPrDefault ctpPrDefault) {
        this.generatedSetterHelperImpl((XmlObject)ctpPrDefault, CTDocDefaultsImpl.PPRDEFAULT$2, 0, (short)1);
    }
    
    public CTPPrDefault addNewPPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPPrDefault)this.get_store().add_element_user(CTDocDefaultsImpl.PPRDEFAULT$2);
        }
    }
    
    public void unsetPPrDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDocDefaultsImpl.PPRDEFAULT$2, 0);
        }
    }
    
    static {
        RPRDEFAULT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPrDefault");
        PPRDEFAULT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pPrDefault");
    }
}
