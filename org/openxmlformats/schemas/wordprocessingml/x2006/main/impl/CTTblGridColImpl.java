package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTwipsMeasure;
import org.apache.xmlbeans.SimpleValue;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblGridColImpl extends XmlComplexContentImpl implements CTTblGridCol
{
    private static final long serialVersionUID = 1L;
    private static final QName W$0;
    
    public CTTblGridColImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public BigInteger getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblGridColImpl.W$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STTwipsMeasure xgetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTwipsMeasure)this.get_store().find_attribute_user(CTTblGridColImpl.W$0);
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTblGridColImpl.W$0) != null;
        }
    }
    
    public void setW(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTblGridColImpl.W$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTblGridColImpl.W$0);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetW(final STTwipsMeasure stTwipsMeasure) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTwipsMeasure stTwipsMeasure2 = (STTwipsMeasure)this.get_store().find_attribute_user(CTTblGridColImpl.W$0);
            if (stTwipsMeasure2 == null) {
                stTwipsMeasure2 = (STTwipsMeasure)this.get_store().add_attribute_user(CTTblGridColImpl.W$0);
            }
            stTwipsMeasure2.set((XmlObject)stTwipsMeasure);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTblGridColImpl.W$0);
        }
    }
    
    static {
        W$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w");
    }
}
