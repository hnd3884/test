package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChangeNumbering;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumPrImpl extends XmlComplexContentImpl implements CTNumPr
{
    private static final long serialVersionUID = 1L;
    private static final QName ILVL$0;
    private static final QName NUMID$2;
    private static final QName NUMBERINGCHANGE$4;
    private static final QName INS$6;
    
    public CTNumPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDecimalNumber getIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTNumPrImpl.ILVL$0, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumPrImpl.ILVL$0) != 0;
        }
    }
    
    public void setIlvl(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTNumPrImpl.ILVL$0, 0, (short)1);
    }
    
    public CTDecimalNumber addNewIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTNumPrImpl.ILVL$0);
        }
    }
    
    public void unsetIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumPrImpl.ILVL$0, 0);
        }
    }
    
    public CTDecimalNumber getNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTNumPrImpl.NUMID$2, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumPrImpl.NUMID$2) != 0;
        }
    }
    
    public void setNumId(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTNumPrImpl.NUMID$2, 0, (short)1);
    }
    
    public CTDecimalNumber addNewNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTNumPrImpl.NUMID$2);
        }
    }
    
    public void unsetNumId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumPrImpl.NUMID$2, 0);
        }
    }
    
    public CTTrackChangeNumbering getNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChangeNumbering ctTrackChangeNumbering = (CTTrackChangeNumbering)this.get_store().find_element_user(CTNumPrImpl.NUMBERINGCHANGE$4, 0);
            if (ctTrackChangeNumbering == null) {
                return null;
            }
            return ctTrackChangeNumbering;
        }
    }
    
    public boolean isSetNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumPrImpl.NUMBERINGCHANGE$4) != 0;
        }
    }
    
    public void setNumberingChange(final CTTrackChangeNumbering ctTrackChangeNumbering) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChangeNumbering, CTNumPrImpl.NUMBERINGCHANGE$4, 0, (short)1);
    }
    
    public CTTrackChangeNumbering addNewNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChangeNumbering)this.get_store().add_element_user(CTNumPrImpl.NUMBERINGCHANGE$4);
        }
    }
    
    public void unsetNumberingChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumPrImpl.NUMBERINGCHANGE$4, 0);
        }
    }
    
    public CTTrackChange getIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTNumPrImpl.INS$6, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    public boolean isSetIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumPrImpl.INS$6) != 0;
        }
    }
    
    public void setIns(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTNumPrImpl.INS$6, 0, (short)1);
    }
    
    public CTTrackChange addNewIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTNumPrImpl.INS$6);
        }
    }
    
    public void unsetIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumPrImpl.INS$6, 0);
        }
    }
    
    static {
        ILVL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ilvl");
        NUMID$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numId");
        NUMBERINGCHANGE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numberingChange");
        INS$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ins");
    }
}
