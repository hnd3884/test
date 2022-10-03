package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTIndImpl extends XmlComplexContentImpl implements CTInd
{
    private static final long serialVersionUID = 1L;
    private static final QName LEFT$0;
    private static final QName LEFTCHARS$2;
    private static final QName RIGHT$4;
    private static final QName RIGHTCHARS$6;
    private static final QName HANGING$8;
    private static final QName HANGINGCHARS$10;
    private static final QName FIRSTLINE$12;
    private static final QName FIRSTLINECHARS$14;
    
    public CTIndImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.LEFT$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STSignedTwipsMeasure xgetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.LEFT$0);
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.LEFT$0) != null;
        }
    }
    
    public void setLeft(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.LEFT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.LEFT$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetLeft(final STSignedTwipsMeasure stSignedTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignedTwipsMeasure stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.LEFT$0);
            if (stSignedTwipsMeasure2 == null) {
                stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().add_attribute_user(CTIndImpl.LEFT$0);
            }
            stSignedTwipsMeasure2.set((XmlObject)stSignedTwipsMeasure);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.LEFT$0);
        }
    }
    
    public BigInteger getLeftChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.LEFTCHARS$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetLeftChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.LEFTCHARS$2);
        }
    }
    
    public boolean isSetLeftChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.LEFTCHARS$2) != null;
        }
    }
    
    public void setLeftChars(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.LEFTCHARS$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.LEFTCHARS$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetLeftChars(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.LEFTCHARS$2);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTIndImpl.LEFTCHARS$2);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetLeftChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.LEFTCHARS$2);
        }
    }
    
    public BigInteger getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.RIGHT$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STSignedTwipsMeasure xgetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.RIGHT$4);
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.RIGHT$4) != null;
        }
    }
    
    public void setRight(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.RIGHT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.RIGHT$4);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetRight(final STSignedTwipsMeasure stSignedTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignedTwipsMeasure stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.RIGHT$4);
            if (stSignedTwipsMeasure2 == null) {
                stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().add_attribute_user(CTIndImpl.RIGHT$4);
            }
            stSignedTwipsMeasure2.set((XmlObject)stSignedTwipsMeasure);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.RIGHT$4);
        }
    }
    
    public BigInteger getRightChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.RIGHTCHARS$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetRightChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.RIGHTCHARS$6);
        }
    }
    
    public boolean isSetRightChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.RIGHTCHARS$6) != null;
        }
    }
    
    public void setRightChars(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.RIGHTCHARS$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.RIGHTCHARS$6);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetRightChars(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.RIGHTCHARS$6);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTIndImpl.RIGHTCHARS$6);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetRightChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.RIGHTCHARS$6);
        }
    }
    
    public BigInteger getHanging() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.HANGING$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STTwipsMeasure xgetHanging() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.HANGING$8);
        }
    }
    
    public boolean isSetHanging() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.HANGING$8) != null;
        }
    }
    
    public void setHanging(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.HANGING$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.HANGING$8);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetHanging(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.HANGING$8);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTIndImpl.HANGING$8);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    public void unsetHanging() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.HANGING$8);
        }
    }
    
    public BigInteger getHangingChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.HANGINGCHARS$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetHangingChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.HANGINGCHARS$10);
        }
    }
    
    public boolean isSetHangingChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.HANGINGCHARS$10) != null;
        }
    }
    
    public void setHangingChars(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.HANGINGCHARS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.HANGINGCHARS$10);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetHangingChars(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.HANGINGCHARS$10);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTIndImpl.HANGINGCHARS$10);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetHangingChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.HANGINGCHARS$10);
        }
    }
    
    public BigInteger getFirstLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINE$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STTwipsMeasure xgetFirstLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINE$12);
        }
    }
    
    public boolean isSetFirstLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.FIRSTLINE$12) != null;
        }
    }
    
    public void setFirstLine(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.FIRSTLINE$12);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetFirstLine(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINE$12);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTIndImpl.FIRSTLINE$12);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    public void unsetFirstLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.FIRSTLINE$12);
        }
    }
    
    public BigInteger getFirstLineChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINECHARS$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetFirstLineChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINECHARS$14);
        }
    }
    
    public boolean isSetFirstLineChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIndImpl.FIRSTLINECHARS$14) != null;
        }
    }
    
    public void setFirstLineChars(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINECHARS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIndImpl.FIRSTLINECHARS$14);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetFirstLineChars(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTIndImpl.FIRSTLINECHARS$14);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTIndImpl.FIRSTLINECHARS$14);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetFirstLineChars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIndImpl.FIRSTLINECHARS$14);
        }
    }
    
    static {
        LEFT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "left");
        LEFTCHARS$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "leftChars");
        RIGHT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "right");
        RIGHTCHARS$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rightChars");
        HANGING$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hanging");
        HANGINGCHARS$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hangingChars");
        FIRSTLINE$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "firstLine");
        FIRSTLINECHARS$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "firstLineChars");
    }
}
