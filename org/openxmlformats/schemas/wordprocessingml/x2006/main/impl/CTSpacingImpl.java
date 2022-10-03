package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTwipsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSpacingImpl extends XmlComplexContentImpl implements CTSpacing
{
    private static final long serialVersionUID = 1L;
    private static final QName BEFORE$0;
    private static final QName BEFORELINES$2;
    private static final QName BEFOREAUTOSPACING$4;
    private static final QName AFTER$6;
    private static final QName AFTERLINES$8;
    private static final QName AFTERAUTOSPACING$10;
    private static final QName LINE$12;
    private static final QName LINERULE$14;
    
    public CTSpacingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.BEFORE$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STTwipsMeasure xgetBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTSpacingImpl.BEFORE$0);
        }
    }
    
    public boolean isSetBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.BEFORE$0) != null;
        }
    }
    
    public void setBefore(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.BEFORE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.BEFORE$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetBefore(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTSpacingImpl.BEFORE$0);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTSpacingImpl.BEFORE$0);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    public void unsetBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.BEFORE$0);
        }
    }
    
    public BigInteger getBeforeLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.BEFORELINES$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetBeforeLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTSpacingImpl.BEFORELINES$2);
        }
    }
    
    public boolean isSetBeforeLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.BEFORELINES$2) != null;
        }
    }
    
    public void setBeforeLines(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.BEFORELINES$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.BEFORELINES$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetBeforeLines(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTSpacingImpl.BEFORELINES$2);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTSpacingImpl.BEFORELINES$2);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetBeforeLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.BEFORELINES$2);
        }
    }
    
    public STOnOff.Enum getBeforeAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetBeforeAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4);
        }
    }
    
    public boolean isSetBeforeAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4) != null;
        }
    }
    
    public void setBeforeAutospacing(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBeforeAutospacing(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTSpacingImpl.BEFOREAUTOSPACING$4);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetBeforeAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.BEFOREAUTOSPACING$4);
        }
    }
    
    public BigInteger getAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.AFTER$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STTwipsMeasure xgetAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTSpacingImpl.AFTER$6);
        }
    }
    
    public boolean isSetAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.AFTER$6) != null;
        }
    }
    
    public void setAfter(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.AFTER$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.AFTER$6);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetAfter(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTSpacingImpl.AFTER$6);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTSpacingImpl.AFTER$6);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    public void unsetAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.AFTER$6);
        }
    }
    
    public BigInteger getAfterLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.AFTERLINES$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetAfterLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTSpacingImpl.AFTERLINES$8);
        }
    }
    
    public boolean isSetAfterLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.AFTERLINES$8) != null;
        }
    }
    
    public void setAfterLines(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.AFTERLINES$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.AFTERLINES$8);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetAfterLines(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTSpacingImpl.AFTERLINES$8);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTSpacingImpl.AFTERLINES$8);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetAfterLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.AFTERLINES$8);
        }
    }
    
    public STOnOff.Enum getAfterAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetAfterAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10);
        }
    }
    
    public boolean isSetAfterAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10) != null;
        }
    }
    
    public void setAfterAutospacing(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAfterAutospacing(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTSpacingImpl.AFTERAUTOSPACING$10);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetAfterAutospacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.AFTERAUTOSPACING$10);
        }
    }
    
    public BigInteger getLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.LINE$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STSignedTwipsMeasure xgetLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTSpacingImpl.LINE$12);
        }
    }
    
    public boolean isSetLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.LINE$12) != null;
        }
    }
    
    public void setLine(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.LINE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.LINE$12);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetLine(final STSignedTwipsMeasure stSignedTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignedTwipsMeasure stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTSpacingImpl.LINE$12);
            if (stSignedTwipsMeasure2 == null) {
                stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().add_attribute_user(CTSpacingImpl.LINE$12);
            }
            stSignedTwipsMeasure2.set((XmlObject)stSignedTwipsMeasure);
        }
    }
    
    public void unsetLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.LINE$12);
        }
    }
    
    public STLineSpacingRule.Enum getLineRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.LINERULE$14);
            if (simpleValue == null) {
                return null;
            }
            return (STLineSpacingRule.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLineSpacingRule xgetLineRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLineSpacingRule)this.get_store().find_attribute_user(CTSpacingImpl.LINERULE$14);
        }
    }
    
    public boolean isSetLineRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSpacingImpl.LINERULE$14) != null;
        }
    }
    
    public void setLineRule(final STLineSpacingRule.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSpacingImpl.LINERULE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSpacingImpl.LINERULE$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLineRule(final STLineSpacingRule stLineSpacingRule) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLineSpacingRule stLineSpacingRule2 = (STLineSpacingRule)this.get_store().find_attribute_user(CTSpacingImpl.LINERULE$14);
            if (stLineSpacingRule2 == null) {
                stLineSpacingRule2 = (STLineSpacingRule)this.get_store().add_attribute_user(CTSpacingImpl.LINERULE$14);
            }
            stLineSpacingRule2.set((XmlObject)stLineSpacingRule);
        }
    }
    
    public void unsetLineRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSpacingImpl.LINERULE$14);
        }
    }
    
    static {
        BEFORE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "before");
        BEFORELINES$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "beforeLines");
        BEFOREAUTOSPACING$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "beforeAutospacing");
        AFTER$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "after");
        AFTERLINES$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "afterLines");
        AFTERAUTOSPACING$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "afterAutospacing");
        LINE$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "line");
        LINERULE$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lineRule");
    }
}
