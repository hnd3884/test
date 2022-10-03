package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHpsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHpsMeasureImpl extends XmlComplexContentImpl implements CTHpsMeasure
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTHpsMeasureImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHpsMeasureImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STHpsMeasure xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHpsMeasure)this.get_store().find_attribute_user(CTHpsMeasureImpl.VAL$0);
        }
    }
    
    public void setVal(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHpsMeasureImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHpsMeasureImpl.VAL$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetVal(final STHpsMeasure stHpsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHpsMeasure stHpsMeasure2 = (STHpsMeasure)this.get_store().find_attribute_user(CTHpsMeasureImpl.VAL$0);
            if (stHpsMeasure2 == null) {
                stHpsMeasure2 = (STHpsMeasure)this.get_store().add_attribute_user(CTHpsMeasureImpl.VAL$0);
            }
            stHpsMeasure2.set((XmlObject)stHpsMeasure);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
