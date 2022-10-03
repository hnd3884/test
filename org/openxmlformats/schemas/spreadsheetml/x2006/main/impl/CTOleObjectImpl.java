package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOleUpdate;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDvAspect;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOleObjectImpl extends XmlComplexContentImpl implements CTOleObject
{
    private static final long serialVersionUID = 1L;
    private static final QName PROGID$0;
    private static final QName DVASPECT$2;
    private static final QName LINK$4;
    private static final QName OLEUPDATE$6;
    private static final QName AUTOLOAD$8;
    private static final QName SHAPEID$10;
    private static final QName ID$12;
    
    public CTOleObjectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$0);
        }
    }
    
    public boolean isSetProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$0) != null;
        }
    }
    
    public void setProgId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.PROGID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetProgId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTOleObjectImpl.PROGID$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.PROGID$0);
        }
    }
    
    public STDvAspect.Enum getDvAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.DVASPECT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOleObjectImpl.DVASPECT$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDvAspect.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDvAspect xgetDvAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDvAspect stDvAspect = (STDvAspect)this.get_store().find_attribute_user(CTOleObjectImpl.DVASPECT$2);
            if (stDvAspect == null) {
                stDvAspect = (STDvAspect)this.get_default_attribute_value(CTOleObjectImpl.DVASPECT$2);
            }
            return stDvAspect;
        }
    }
    
    public boolean isSetDvAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.DVASPECT$2) != null;
        }
    }
    
    public void setDvAspect(final STDvAspect.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.DVASPECT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.DVASPECT$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDvAspect(final STDvAspect stDvAspect) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDvAspect stDvAspect2 = (STDvAspect)this.get_store().find_attribute_user(CTOleObjectImpl.DVASPECT$2);
            if (stDvAspect2 == null) {
                stDvAspect2 = (STDvAspect)this.get_store().add_attribute_user(CTOleObjectImpl.DVASPECT$2);
            }
            stDvAspect2.set((XmlObject)stDvAspect);
        }
    }
    
    public void unsetDvAspect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.DVASPECT$2);
        }
    }
    
    public String getLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.LINK$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTOleObjectImpl.LINK$4);
        }
    }
    
    public boolean isSetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.LINK$4) != null;
        }
    }
    
    public void setLink(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.LINK$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.LINK$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLink(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTOleObjectImpl.LINK$4);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTOleObjectImpl.LINK$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.LINK$4);
        }
    }
    
    public STOleUpdate.Enum getOleUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.OLEUPDATE$6);
            if (simpleValue == null) {
                return null;
            }
            return (STOleUpdate.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOleUpdate xgetOleUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOleUpdate)this.get_store().find_attribute_user(CTOleObjectImpl.OLEUPDATE$6);
        }
    }
    
    public boolean isSetOleUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.OLEUPDATE$6) != null;
        }
    }
    
    public void setOleUpdate(final STOleUpdate.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.OLEUPDATE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.OLEUPDATE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOleUpdate(final STOleUpdate stOleUpdate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOleUpdate stOleUpdate2 = (STOleUpdate)this.get_store().find_attribute_user(CTOleObjectImpl.OLEUPDATE$6);
            if (stOleUpdate2 == null) {
                stOleUpdate2 = (STOleUpdate)this.get_store().add_attribute_user(CTOleObjectImpl.OLEUPDATE$6);
            }
            stOleUpdate2.set((XmlObject)stOleUpdate);
        }
    }
    
    public void unsetOleUpdate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.OLEUPDATE$6);
        }
    }
    
    public boolean getAutoLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.AUTOLOAD$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOleObjectImpl.AUTOLOAD$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOleObjectImpl.AUTOLOAD$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOleObjectImpl.AUTOLOAD$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.AUTOLOAD$8) != null;
        }
    }
    
    public void setAutoLoad(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.AUTOLOAD$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.AUTOLOAD$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoLoad(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOleObjectImpl.AUTOLOAD$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOleObjectImpl.AUTOLOAD$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.AUTOLOAD$8);
        }
    }
    
    public long getShapeId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.SHAPEID$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetShapeId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTOleObjectImpl.SHAPEID$10);
        }
    }
    
    public void setShapeId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.SHAPEID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.SHAPEID$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetShapeId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTOleObjectImpl.SHAPEID$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTOleObjectImpl.SHAPEID$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.ID$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTOleObjectImpl.ID$12);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.ID$12) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.ID$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.ID$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTOleObjectImpl.ID$12);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTOleObjectImpl.ID$12);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.ID$12);
        }
    }
    
    static {
        PROGID$0 = new QName("", "progId");
        DVASPECT$2 = new QName("", "dvAspect");
        LINK$4 = new QName("", "link");
        OLEUPDATE$6 = new QName("", "oleUpdate");
        AUTOLOAD$8 = new QName("", "autoLoad");
        SHAPEID$10 = new QName("", "shapeId");
        ID$12 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
