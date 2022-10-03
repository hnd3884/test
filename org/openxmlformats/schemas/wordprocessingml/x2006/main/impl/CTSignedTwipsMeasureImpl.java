package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSignedTwipsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSignedTwipsMeasureImpl extends XmlComplexContentImpl implements CTSignedTwipsMeasure
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTSignedTwipsMeasureImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSignedTwipsMeasureImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STSignedTwipsMeasure xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTSignedTwipsMeasureImpl.VAL$0);
        }
    }
    
    public void setVal(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSignedTwipsMeasureImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSignedTwipsMeasureImpl.VAL$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetVal(final STSignedTwipsMeasure stSignedTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignedTwipsMeasure stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().find_attribute_user(CTSignedTwipsMeasureImpl.VAL$0);
            if (stSignedTwipsMeasure2 == null) {
                stSignedTwipsMeasure2 = (STSignedTwipsMeasure)this.get_store().add_attribute_user(CTSignedTwipsMeasureImpl.VAL$0);
            }
            stSignedTwipsMeasure2.set((XmlObject)stSignedTwipsMeasure);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
