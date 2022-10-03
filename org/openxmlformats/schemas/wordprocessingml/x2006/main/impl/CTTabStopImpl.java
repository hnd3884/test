package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabTlc;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTabStopImpl extends XmlComplexContentImpl implements CTTabStop
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName LEADER$2;
    private static final QName POS$4;
    
    public CTTabStopImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTabJc.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTabStopImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STTabJc.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTabJc xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTabJc)this.get_store().find_attribute_user(CTTabStopImpl.VAL$0);
        }
    }
    
    public void setVal(final STTabJc.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTabStopImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTabStopImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STTabJc stTabJc) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTabJc stTabJc2 = (STTabJc)this.get_store().find_attribute_user(CTTabStopImpl.VAL$0);
            if (stTabJc2 == null) {
                stTabJc2 = (STTabJc)this.get_store().add_attribute_user(CTTabStopImpl.VAL$0);
            }
            stTabJc2.set((XmlObject)stTabJc);
        }
    }
    
    public STTabTlc.Enum getLeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTabStopImpl.LEADER$2);
            if (simpleValue == null) {
                return null;
            }
            return (STTabTlc.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTabTlc xgetLeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTabTlc)this.get_store().find_attribute_user(CTTabStopImpl.LEADER$2);
        }
    }
    
    public boolean isSetLeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTabStopImpl.LEADER$2) != null;
        }
    }
    
    public void setLeader(final STTabTlc.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTabStopImpl.LEADER$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTabStopImpl.LEADER$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLeader(final STTabTlc stTabTlc) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTabTlc stTabTlc2 = (STTabTlc)this.get_store().find_attribute_user(CTTabStopImpl.LEADER$2);
            if (stTabTlc2 == null) {
                stTabTlc2 = (STTabTlc)this.get_store().add_attribute_user(CTTabStopImpl.LEADER$2);
            }
            stTabTlc2.set((XmlObject)stTabTlc);
        }
    }
    
    public void unsetLeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTabStopImpl.LEADER$2);
        }
    }
    
    public BigInteger getPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTabStopImpl.POS$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STSignedTwipsMeasure xgetPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTTabStopImpl.POS$4);
        }
    }
    
    public void setPos(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTabStopImpl.POS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTabStopImpl.POS$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetPos(final STSignedTwipsMeasure stSignedTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignedTwipsMeasure stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTTabStopImpl.POS$4);
            if (stSignedTwipsMeasure2 == null) {
                stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().add_attribute_user(CTTabStopImpl.POS$4);
            }
            stSignedTwipsMeasure2.set((XmlObject)stSignedTwipsMeasure);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        LEADER$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "leader");
        POS$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pos");
    }
}
