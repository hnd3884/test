package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;

public class StyleSheetTypeImpl extends SheetTypeImpl implements StyleSheetType
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName NAME$2;
    private static final QName NAMEU$4;
    private static final QName ISCUSTOMNAME$6;
    private static final QName ISCUSTOMNAMEU$8;
    
    public StyleSheetTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public long getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.ID$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    @Override
    public XmlUnsignedInt xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(StyleSheetTypeImpl.ID$0);
        }
    }
    
    @Override
    public void setID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(StyleSheetTypeImpl.ID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    @Override
    public void xsetID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(StyleSheetTypeImpl.ID$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(StyleSheetTypeImpl.ID$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    @Override
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAME$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAME$2);
        }
    }
    
    @Override
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(StyleSheetTypeImpl.NAME$2) != null;
        }
    }
    
    @Override
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAME$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(StyleSheetTypeImpl.NAME$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAME$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(StyleSheetTypeImpl.NAME$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    @Override
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(StyleSheetTypeImpl.NAME$2);
        }
    }
    
    @Override
    public String getNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAMEU$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    @Override
    public XmlString xgetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAMEU$4);
        }
    }
    
    @Override
    public boolean isSetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(StyleSheetTypeImpl.NAMEU$4) != null;
        }
    }
    
    @Override
    public void setNameU(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAMEU$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(StyleSheetTypeImpl.NAMEU$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    @Override
    public void xsetNameU(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(StyleSheetTypeImpl.NAMEU$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(StyleSheetTypeImpl.NAMEU$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    @Override
    public void unsetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(StyleSheetTypeImpl.NAMEU$4);
        }
    }
    
    @Override
    public boolean getIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6);
        }
    }
    
    @Override
    public boolean isSetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6) != null;
        }
    }
    
    @Override
    public void setIsCustomName(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    @Override
    public void xsetIsCustomName(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAME$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    @Override
    public void unsetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(StyleSheetTypeImpl.ISCUSTOMNAME$6);
        }
    }
    
    @Override
    public boolean getIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    @Override
    public XmlBoolean xgetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
        }
    }
    
    @Override
    public boolean isSetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8) != null;
        }
    }
    
    @Override
    public void setIsCustomNameU(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    @Override
    public void xsetIsCustomNameU(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    @Override
    public void unsetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(StyleSheetTypeImpl.ISCUSTOMNAMEU$8);
        }
    }
    
    static {
        ID$0 = new QName("", "ID");
        NAME$2 = new QName("", "Name");
        NAMEU$4 = new QName("", "NameU");
        ISCUSTOMNAME$6 = new QName("", "IsCustomName");
        ISCUSTOMNAMEU$8 = new QName("", "IsCustomNameU");
    }
}
