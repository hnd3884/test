package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STProofErr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTProofErr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTProofErrImpl extends XmlComplexContentImpl implements CTProofErr
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    
    public CTProofErrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STProofErr.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTProofErrImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STProofErr.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STProofErr xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STProofErr)this.get_store().find_attribute_user(CTProofErrImpl.TYPE$0);
        }
    }
    
    public void setType(final STProofErr.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTProofErrImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTProofErrImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STProofErr stProofErr) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STProofErr stProofErr2 = (STProofErr)this.get_store().find_attribute_user(CTProofErrImpl.TYPE$0);
            if (stProofErr2 == null) {
                stProofErr2 = (STProofErr)this.get_store().add_attribute_user(CTProofErrImpl.TYPE$0);
            }
            stProofErr2.set((XmlObject)stProofErr);
        }
    }
    
    static {
        TYPE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "type");
    }
}
