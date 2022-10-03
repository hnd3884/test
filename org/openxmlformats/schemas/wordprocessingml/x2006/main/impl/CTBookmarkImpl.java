package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;

public class CTBookmarkImpl extends CTBookmarkRangeImpl implements CTBookmark
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    
    public CTBookmarkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookmarkImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public STString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTBookmarkImpl.NAME$0);
        }
    }
    
    @Override
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookmarkImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookmarkImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetName(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTBookmarkImpl.NAME$0);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTBookmarkImpl.NAME$0);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    static {
        NAME$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "name");
    }
}
