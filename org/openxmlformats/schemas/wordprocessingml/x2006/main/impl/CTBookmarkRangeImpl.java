package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmarkRange;

public class CTBookmarkRangeImpl extends CTMarkupRangeImpl implements CTBookmarkRange
{
    private static final long serialVersionUID = 1L;
    private static final QName COLFIRST$0;
    private static final QName COLLAST$2;
    
    public CTBookmarkRangeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public BigInteger getColFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLFIRST$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    @Override
    public STDecimalNumber xgetColFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLFIRST$0);
        }
    }
    
    @Override
    public boolean isSetColFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLFIRST$0) != null;
        }
    }
    
    @Override
    public void setColFirst(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLFIRST$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookmarkRangeImpl.COLFIRST$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    @Override
    public void xsetColFirst(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLFIRST$0);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTBookmarkRangeImpl.COLFIRST$0);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    @Override
    public void unsetColFirst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookmarkRangeImpl.COLFIRST$0);
        }
    }
    
    @Override
    public BigInteger getColLast() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLLAST$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    @Override
    public STDecimalNumber xgetColLast() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLLAST$2);
        }
    }
    
    @Override
    public boolean isSetColLast() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLLAST$2) != null;
        }
    }
    
    @Override
    public void setColLast(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLLAST$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookmarkRangeImpl.COLLAST$2);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    @Override
    public void xsetColLast(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTBookmarkRangeImpl.COLLAST$2);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTBookmarkRangeImpl.COLLAST$2);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    @Override
    public void unsetColLast() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookmarkRangeImpl.COLLAST$2);
        }
    }
    
    static {
        COLFIRST$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "colFirst");
        COLLAST$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "colLast");
    }
}
