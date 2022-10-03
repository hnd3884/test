package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticAlignment;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontId;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPhoneticPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPhoneticPrImpl extends XmlComplexContentImpl implements CTPhoneticPr
{
    private static final long serialVersionUID = 1L;
    private static final QName FONTID$0;
    private static final QName TYPE$2;
    private static final QName ALIGNMENT$4;
    
    public CTPhoneticPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getFontId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPhoneticPrImpl.FONTID$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STFontId xgetFontId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFontId)this.get_store().find_attribute_user(CTPhoneticPrImpl.FONTID$0);
        }
    }
    
    public void setFontId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPhoneticPrImpl.FONTID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPhoneticPrImpl.FONTID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFontId(final STFontId stFontId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFontId stFontId2 = (STFontId)this.get_store().find_attribute_user(CTPhoneticPrImpl.FONTID$0);
            if (stFontId2 == null) {
                stFontId2 = (STFontId)this.get_store().add_attribute_user(CTPhoneticPrImpl.FONTID$0);
            }
            stFontId2.set((XmlObject)stFontId);
        }
    }
    
    public STPhoneticType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPhoneticPrImpl.TYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPhoneticPrImpl.TYPE$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPhoneticType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPhoneticType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPhoneticType stPhoneticType = (STPhoneticType)this.get_store().find_attribute_user(CTPhoneticPrImpl.TYPE$2);
            if (stPhoneticType == null) {
                stPhoneticType = (STPhoneticType)this.get_default_attribute_value(CTPhoneticPrImpl.TYPE$2);
            }
            return stPhoneticType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPhoneticPrImpl.TYPE$2) != null;
        }
    }
    
    public void setType(final STPhoneticType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPhoneticPrImpl.TYPE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPhoneticPrImpl.TYPE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STPhoneticType stPhoneticType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPhoneticType stPhoneticType2 = (STPhoneticType)this.get_store().find_attribute_user(CTPhoneticPrImpl.TYPE$2);
            if (stPhoneticType2 == null) {
                stPhoneticType2 = (STPhoneticType)this.get_store().add_attribute_user(CTPhoneticPrImpl.TYPE$2);
            }
            stPhoneticType2.set((XmlObject)stPhoneticType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPhoneticPrImpl.TYPE$2);
        }
    }
    
    public STPhoneticAlignment.Enum getAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPhoneticPrImpl.ALIGNMENT$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPhoneticAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPhoneticAlignment xgetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPhoneticAlignment stPhoneticAlignment = (STPhoneticAlignment)this.get_store().find_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4);
            if (stPhoneticAlignment == null) {
                stPhoneticAlignment = (STPhoneticAlignment)this.get_default_attribute_value(CTPhoneticPrImpl.ALIGNMENT$4);
            }
            return stPhoneticAlignment;
        }
    }
    
    public boolean isSetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4) != null;
        }
    }
    
    public void setAlignment(final STPhoneticAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlignment(final STPhoneticAlignment stPhoneticAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPhoneticAlignment stPhoneticAlignment2 = (STPhoneticAlignment)this.get_store().find_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4);
            if (stPhoneticAlignment2 == null) {
                stPhoneticAlignment2 = (STPhoneticAlignment)this.get_store().add_attribute_user(CTPhoneticPrImpl.ALIGNMENT$4);
            }
            stPhoneticAlignment2.set((XmlObject)stPhoneticAlignment);
        }
    }
    
    public void unsetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPhoneticPrImpl.ALIGNMENT$4);
        }
    }
    
    static {
        FONTID$0 = new QName("", "fontId");
        TYPE$2 = new QName("", "type");
        ALIGNMENT$4 = new QName("", "alignment");
    }
}
