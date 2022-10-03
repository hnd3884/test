package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStretchInfoProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBlipFillPropertiesImpl extends XmlComplexContentImpl implements CTBlipFillProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName BLIP$0;
    private static final QName SRCRECT$2;
    private static final QName TILE$4;
    private static final QName STRETCH$6;
    private static final QName DPI$8;
    private static final QName ROTWITHSHAPE$10;
    
    public CTBlipFillPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBlip getBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlip ctBlip = (CTBlip)this.get_store().find_element_user(CTBlipFillPropertiesImpl.BLIP$0, 0);
            if (ctBlip == null) {
                return null;
            }
            return ctBlip;
        }
    }
    
    public boolean isSetBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipFillPropertiesImpl.BLIP$0) != 0;
        }
    }
    
    public void setBlip(final CTBlip ctBlip) {
        this.generatedSetterHelperImpl((XmlObject)ctBlip, CTBlipFillPropertiesImpl.BLIP$0, 0, (short)1);
    }
    
    public CTBlip addNewBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlip)this.get_store().add_element_user(CTBlipFillPropertiesImpl.BLIP$0);
        }
    }
    
    public void unsetBlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipFillPropertiesImpl.BLIP$0, 0);
        }
    }
    
    public CTRelativeRect getSrcRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelativeRect ctRelativeRect = (CTRelativeRect)this.get_store().find_element_user(CTBlipFillPropertiesImpl.SRCRECT$2, 0);
            if (ctRelativeRect == null) {
                return null;
            }
            return ctRelativeRect;
        }
    }
    
    public boolean isSetSrcRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipFillPropertiesImpl.SRCRECT$2) != 0;
        }
    }
    
    public void setSrcRect(final CTRelativeRect ctRelativeRect) {
        this.generatedSetterHelperImpl((XmlObject)ctRelativeRect, CTBlipFillPropertiesImpl.SRCRECT$2, 0, (short)1);
    }
    
    public CTRelativeRect addNewSrcRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelativeRect)this.get_store().add_element_user(CTBlipFillPropertiesImpl.SRCRECT$2);
        }
    }
    
    public void unsetSrcRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipFillPropertiesImpl.SRCRECT$2, 0);
        }
    }
    
    public CTTileInfoProperties getTile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTileInfoProperties ctTileInfoProperties = (CTTileInfoProperties)this.get_store().find_element_user(CTBlipFillPropertiesImpl.TILE$4, 0);
            if (ctTileInfoProperties == null) {
                return null;
            }
            return ctTileInfoProperties;
        }
    }
    
    public boolean isSetTile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipFillPropertiesImpl.TILE$4) != 0;
        }
    }
    
    public void setTile(final CTTileInfoProperties ctTileInfoProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTileInfoProperties, CTBlipFillPropertiesImpl.TILE$4, 0, (short)1);
    }
    
    public CTTileInfoProperties addNewTile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTileInfoProperties)this.get_store().add_element_user(CTBlipFillPropertiesImpl.TILE$4);
        }
    }
    
    public void unsetTile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipFillPropertiesImpl.TILE$4, 0);
        }
    }
    
    public CTStretchInfoProperties getStretch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStretchInfoProperties ctStretchInfoProperties = (CTStretchInfoProperties)this.get_store().find_element_user(CTBlipFillPropertiesImpl.STRETCH$6, 0);
            if (ctStretchInfoProperties == null) {
                return null;
            }
            return ctStretchInfoProperties;
        }
    }
    
    public boolean isSetStretch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipFillPropertiesImpl.STRETCH$6) != 0;
        }
    }
    
    public void setStretch(final CTStretchInfoProperties ctStretchInfoProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctStretchInfoProperties, CTBlipFillPropertiesImpl.STRETCH$6, 0, (short)1);
    }
    
    public CTStretchInfoProperties addNewStretch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStretchInfoProperties)this.get_store().add_element_user(CTBlipFillPropertiesImpl.STRETCH$6);
        }
    }
    
    public void unsetStretch() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipFillPropertiesImpl.STRETCH$6, 0);
        }
    }
    
    public long getDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.DPI$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.DPI$8);
        }
    }
    
    public boolean isSetDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.DPI$8) != null;
        }
    }
    
    public void setDpi(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.DPI$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBlipFillPropertiesImpl.DPI$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDpi(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.DPI$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBlipFillPropertiesImpl.DPI$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBlipFillPropertiesImpl.DPI$8);
        }
    }
    
    public boolean getRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
        }
    }
    
    public boolean isSetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10) != null;
        }
    }
    
    public void setRotWithShape(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRotWithShape(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBlipFillPropertiesImpl.ROTWITHSHAPE$10);
        }
    }
    
    static {
        BLIP$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blip");
        SRCRECT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "srcRect");
        TILE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tile");
        STRETCH$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "stretch");
        DPI$8 = new QName("", "dpi");
        ROTWITHSHAPE$10 = new QName("", "rotWithShape");
    }
}
