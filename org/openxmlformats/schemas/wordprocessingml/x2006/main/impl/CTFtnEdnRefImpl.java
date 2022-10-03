package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFtnEdnRefImpl extends XmlComplexContentImpl implements CTFtnEdnRef
{
    private static final long serialVersionUID = 1L;
    private static final QName CUSTOMMARKFOLLOWS$0;
    private static final QName ID$2;
    
    public CTFtnEdnRefImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STOnOff.Enum getCustomMarkFollows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetCustomMarkFollows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
        }
    }
    
    public boolean isSetCustomMarkFollows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0) != null;
        }
    }
    
    public void setCustomMarkFollows(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCustomMarkFollows(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetCustomMarkFollows() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFtnEdnRefImpl.CUSTOMMARKFOLLOWS$0);
        }
    }
    
    public BigInteger getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFtnEdnRefImpl.ID$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTFtnEdnRefImpl.ID$2);
        }
    }
    
    public void setId(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFtnEdnRefImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFtnEdnRefImpl.ID$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetId(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTFtnEdnRefImpl.ID$2);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTFtnEdnRefImpl.ID$2);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    static {
        CUSTOMMARKFOLLOWS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "customMarkFollows");
        ID$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "id");
    }
}
