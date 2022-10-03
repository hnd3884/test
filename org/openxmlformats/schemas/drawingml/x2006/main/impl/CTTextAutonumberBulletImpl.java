package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextBulletStartAtNum;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAutonumberScheme;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextAutonumberBulletImpl extends XmlComplexContentImpl implements CTTextAutonumberBullet
{
    private static final long serialVersionUID = 1L;
    private static final QName TYPE$0;
    private static final QName STARTAT$2;
    
    public CTTextAutonumberBulletImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTextAutonumberScheme.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.TYPE$0);
            if (simpleValue == null) {
                return null;
            }
            return (STTextAutonumberScheme.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextAutonumberScheme xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextAutonumberScheme)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.TYPE$0);
        }
    }
    
    public void setType(final STTextAutonumberScheme.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.TYPE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextAutonumberBulletImpl.TYPE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STTextAutonumberScheme stTextAutonumberScheme) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextAutonumberScheme stTextAutonumberScheme2 = (STTextAutonumberScheme)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.TYPE$0);
            if (stTextAutonumberScheme2 == null) {
                stTextAutonumberScheme2 = (STTextAutonumberScheme)this.get_store().add_attribute_user(CTTextAutonumberBulletImpl.TYPE$0);
            }
            stTextAutonumberScheme2.set((XmlObject)stTextAutonumberScheme);
        }
    }
    
    public int getStartAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextAutonumberBulletImpl.STARTAT$2);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextBulletStartAtNum xgetStartAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextBulletStartAtNum stTextBulletStartAtNum = (STTextBulletStartAtNum)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2);
            if (stTextBulletStartAtNum == null) {
                stTextBulletStartAtNum = (STTextBulletStartAtNum)this.get_default_attribute_value(CTTextAutonumberBulletImpl.STARTAT$2);
            }
            return stTextBulletStartAtNum;
        }
    }
    
    public boolean isSetStartAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2) != null;
        }
    }
    
    public void setStartAt(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetStartAt(final STTextBulletStartAtNum stTextBulletStartAtNum) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextBulletStartAtNum stTextBulletStartAtNum2 = (STTextBulletStartAtNum)this.get_store().find_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2);
            if (stTextBulletStartAtNum2 == null) {
                stTextBulletStartAtNum2 = (STTextBulletStartAtNum)this.get_store().add_attribute_user(CTTextAutonumberBulletImpl.STARTAT$2);
            }
            stTextBulletStartAtNum2.set((XmlObject)stTextBulletStartAtNum);
        }
    }
    
    public void unsetStartAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextAutonumberBulletImpl.STARTAT$2);
        }
    }
    
    static {
        TYPE$0 = new QName("", "type");
        STARTAT$2 = new QName("", "startAt");
    }
}
