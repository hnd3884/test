package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumFmtImpl extends XmlComplexContentImpl implements CTNumFmt
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTNumFmtImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STNumberFormat.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STNumberFormat.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STNumberFormat xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STNumberFormat)this.get_store().find_attribute_user(CTNumFmtImpl.VAL$0);
        }
    }
    
    public void setVal(final STNumberFormat.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNumFmtImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNumFmtImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STNumberFormat stNumberFormat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STNumberFormat stNumberFormat2 = (STNumberFormat)this.get_store().find_attribute_user(CTNumFmtImpl.VAL$0);
            if (stNumberFormat2 == null) {
                stNumberFormat2 = (STNumberFormat)this.get_store().add_attribute_user(CTNumFmtImpl.VAL$0);
            }
            stNumberFormat2.set((XmlObject)stNumberFormat);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
