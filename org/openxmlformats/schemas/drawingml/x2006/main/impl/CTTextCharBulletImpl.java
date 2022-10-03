package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextCharBulletImpl extends XmlComplexContentImpl implements CTTextCharBullet
{
    private static final long serialVersionUID = 1L;
    private static final QName CHAR$0;
    
    public CTTextCharBulletImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharBulletImpl.CHAR$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTextCharBulletImpl.CHAR$0);
        }
    }
    
    public void setChar(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextCharBulletImpl.CHAR$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextCharBulletImpl.CHAR$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetChar(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTextCharBulletImpl.CHAR$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTextCharBulletImpl.CHAR$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        CHAR$0 = new QName("", "char");
    }
}
