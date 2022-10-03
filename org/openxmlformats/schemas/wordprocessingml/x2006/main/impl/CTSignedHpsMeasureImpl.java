package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedHpsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSignedHpsMeasureImpl extends XmlComplexContentImpl implements CTSignedHpsMeasure
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTSignedHpsMeasureImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSignedHpsMeasureImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STSignedHpsMeasure xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignedHpsMeasure)this.get_store().find_attribute_user(CTSignedHpsMeasureImpl.VAL$0);
        }
    }
    
    public void setVal(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSignedHpsMeasureImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSignedHpsMeasureImpl.VAL$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetVal(final STSignedHpsMeasure stSignedHpsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignedHpsMeasure stSignedHpsMeasure2 = (STSignedHpsMeasure)this.get_store().find_attribute_user(CTSignedHpsMeasureImpl.VAL$0);
            if (stSignedHpsMeasure2 == null) {
                stSignedHpsMeasure2 = (STSignedHpsMeasure)this.get_store().add_attribute_user(CTSignedHpsMeasureImpl.VAL$0);
            }
            stSignedHpsMeasure2.set((XmlObject)stSignedHpsMeasure);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
