package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import java.math.BigInteger;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLsdException;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLsdExceptionImpl extends XmlComplexContentImpl implements CTLsdException
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName LOCKED$2;
    private static final QName UIPRIORITY$4;
    private static final QName SEMIHIDDEN$6;
    private static final QName UNHIDEWHENUSED$8;
    private static final QName QFORMAT$10;
    
    public CTLsdExceptionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTLsdExceptionImpl.NAME$0);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLsdExceptionImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTLsdExceptionImpl.NAME$0);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTLsdExceptionImpl.NAME$0);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public STOnOff.Enum getLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.LOCKED$2);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.LOCKED$2);
        }
    }
    
    public boolean isSetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLsdExceptionImpl.LOCKED$2) != null;
        }
    }
    
    public void setLocked(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.LOCKED$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLsdExceptionImpl.LOCKED$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLocked(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.LOCKED$2);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLsdExceptionImpl.LOCKED$2);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetLocked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLsdExceptionImpl.LOCKED$2);
        }
    }
    
    public BigInteger getUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4);
        }
    }
    
    public boolean isSetUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4) != null;
        }
    }
    
    public void setUiPriority(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetUiPriority(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTLsdExceptionImpl.UIPRIORITY$4);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetUiPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLsdExceptionImpl.UIPRIORITY$4);
        }
    }
    
    public STOnOff.Enum getSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6);
        }
    }
    
    public boolean isSetSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6) != null;
        }
    }
    
    public void setSemiHidden(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSemiHidden(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLsdExceptionImpl.SEMIHIDDEN$6);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetSemiHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLsdExceptionImpl.SEMIHIDDEN$6);
        }
    }
    
    public STOnOff.Enum getUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
        }
    }
    
    public boolean isSetUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8) != null;
        }
    }
    
    public void setUnhideWhenUsed(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUnhideWhenUsed(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetUnhideWhenUsed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLsdExceptionImpl.UNHIDEWHENUSED$8);
        }
    }
    
    public STOnOff.Enum getQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.QFORMAT$10);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.QFORMAT$10);
        }
    }
    
    public boolean isSetQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLsdExceptionImpl.QFORMAT$10) != null;
        }
    }
    
    public void setQFormat(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLsdExceptionImpl.QFORMAT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLsdExceptionImpl.QFORMAT$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetQFormat(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLsdExceptionImpl.QFORMAT$10);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLsdExceptionImpl.QFORMAT$10);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetQFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLsdExceptionImpl.QFORMAT$10);
        }
    }
    
    static {
        NAME$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "name");
        LOCKED$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "locked");
        UIPRIORITY$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "uiPriority");
        SEMIHIDDEN$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "semiHidden");
        UNHIDEWHENUSED$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "unhideWhenUsed");
        QFORMAT$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "qFormat");
    }
}
