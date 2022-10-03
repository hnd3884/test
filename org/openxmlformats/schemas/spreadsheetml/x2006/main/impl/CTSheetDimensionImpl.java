package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetDimensionImpl extends XmlComplexContentImpl implements CTSheetDimension
{
    private static final long serialVersionUID = 1L;
    private static final QName REF$0;
    
    public CTSheetDimensionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetDimensionImpl.REF$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTSheetDimensionImpl.REF$0);
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetDimensionImpl.REF$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetDimensionImpl.REF$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTSheetDimensionImpl.REF$0);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTSheetDimensionImpl.REF$0);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    static {
        REF$0 = new QName("", "ref");
    }
}
