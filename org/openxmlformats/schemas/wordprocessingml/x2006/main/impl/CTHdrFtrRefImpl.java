package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtrRef;

public class CTHdrFtrRefImpl extends CTRelImpl implements CTHdrFtrRef
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    
    public CTHdrFtrRefImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public STHdrFtr.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHdrFtrRefImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STHdrFtr.Enum)simpleValue.getEnumValue();
        }
    }
    
    @Override
    public STHdrFtr xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHdrFtr)this.get_store().find_attribute_user(CTHdrFtrRefImpl.TYPE$0);
        }
    }
    
    @Override
    public void setType(final STHdrFtr.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHdrFtrRefImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHdrFtrRefImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    @Override
    public void xsetType(final STHdrFtr stHdrFtr) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHdrFtr stHdrFtr2 = (STHdrFtr)this.get_store().find_attribute_user(CTHdrFtrRefImpl.TYPE$0);
            if (stHdrFtr2 == null) {
                stHdrFtr2 = (STHdrFtr)this.get_store().add_attribute_user(CTHdrFtrRefImpl.TYPE$0);
            }
            stHdrFtr2.set((XmlObject)stHdrFtr);
        }
    }
    
    static {
        TYPE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
    }
}
