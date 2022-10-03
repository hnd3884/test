package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumLvlImpl extends XmlComplexContentImpl implements CTNumLvl
{
    private static final long serialVersionUID = 1L;
    private static final QName STARTOVERRIDE$0;
    private static final QName LVL$2;
    private static final QName ILVL$4;
    
    public CTNumLvlImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDecimalNumber getStartOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTNumLvlImpl.STARTOVERRIDE$0, 0);
            if (ctDecimalNumber == null) {
                return null;
            }
            return ctDecimalNumber;
        }
    }
    
    public boolean isSetStartOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumLvlImpl.STARTOVERRIDE$0) != 0;
        }
    }
    
    public void setStartOverride(final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTNumLvlImpl.STARTOVERRIDE$0, 0, (short)1);
    }
    
    public CTDecimalNumber addNewStartOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTNumLvlImpl.STARTOVERRIDE$0);
        }
    }
    
    public void unsetStartOverride() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumLvlImpl.STARTOVERRIDE$0, 0);
        }
    }
    
    public CTLvl getLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLvl ctLvl = (CTLvl)this.get_store().find_element_user(CTNumLvlImpl.LVL$2, 0);
            if (ctLvl == null) {
                return null;
            }
            return ctLvl;
        }
    }
    
    public boolean isSetLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumLvlImpl.LVL$2) != 0;
        }
    }
    
    public void setLvl(final CTLvl ctLvl) {
        this.generatedSetterHelperImpl((XmlObject)ctLvl, CTNumLvlImpl.LVL$2, 0, (short)1);
    }
    
    public CTLvl addNewLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLvl)this.get_store().add_element_user(CTNumLvlImpl.LVL$2);
        }
    }
    
    public void unsetLvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumLvlImpl.LVL$2, 0);
        }
    }
    
    public BigInteger getIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumLvlImpl.ILVL$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetIlvl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTNumLvlImpl.ILVL$4);
        }
    }
    
    public void setIlvl(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumLvlImpl.ILVL$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumLvlImpl.ILVL$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetIlvl(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTNumLvlImpl.ILVL$4);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTNumLvlImpl.ILVL$4);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    static {
        STARTOVERRIDE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "startOverride");
        LVL$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lvl");
        ILVL$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ilvl");
    }
}
