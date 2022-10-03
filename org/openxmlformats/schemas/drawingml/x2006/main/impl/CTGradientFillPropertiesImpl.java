package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STTileFlipMode;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPathShadeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLinearShadeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStopList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGradientFillPropertiesImpl extends XmlComplexContentImpl implements CTGradientFillProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName GSLST$0;
    private static final QName LIN$2;
    private static final QName PATH$4;
    private static final QName TILERECT$6;
    private static final QName FLIP$8;
    private static final QName ROTWITHSHAPE$10;
    
    public CTGradientFillPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGradientStopList getGsLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientStopList list = (CTGradientStopList)this.get_store().find_element_user(CTGradientFillPropertiesImpl.GSLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetGsLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGradientFillPropertiesImpl.GSLST$0) != 0;
        }
    }
    
    public void setGsLst(final CTGradientStopList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTGradientFillPropertiesImpl.GSLST$0, 0, (short)1);
    }
    
    public CTGradientStopList addNewGsLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientStopList)this.get_store().add_element_user(CTGradientFillPropertiesImpl.GSLST$0);
        }
    }
    
    public void unsetGsLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGradientFillPropertiesImpl.GSLST$0, 0);
        }
    }
    
    public CTLinearShadeProperties getLin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLinearShadeProperties ctLinearShadeProperties = (CTLinearShadeProperties)this.get_store().find_element_user(CTGradientFillPropertiesImpl.LIN$2, 0);
            if (ctLinearShadeProperties == null) {
                return null;
            }
            return ctLinearShadeProperties;
        }
    }
    
    public boolean isSetLin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGradientFillPropertiesImpl.LIN$2) != 0;
        }
    }
    
    public void setLin(final CTLinearShadeProperties ctLinearShadeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctLinearShadeProperties, CTGradientFillPropertiesImpl.LIN$2, 0, (short)1);
    }
    
    public CTLinearShadeProperties addNewLin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLinearShadeProperties)this.get_store().add_element_user(CTGradientFillPropertiesImpl.LIN$2);
        }
    }
    
    public void unsetLin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGradientFillPropertiesImpl.LIN$2, 0);
        }
    }
    
    public CTPathShadeProperties getPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPathShadeProperties ctPathShadeProperties = (CTPathShadeProperties)this.get_store().find_element_user(CTGradientFillPropertiesImpl.PATH$4, 0);
            if (ctPathShadeProperties == null) {
                return null;
            }
            return ctPathShadeProperties;
        }
    }
    
    public boolean isSetPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGradientFillPropertiesImpl.PATH$4) != 0;
        }
    }
    
    public void setPath(final CTPathShadeProperties ctPathShadeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPathShadeProperties, CTGradientFillPropertiesImpl.PATH$4, 0, (short)1);
    }
    
    public CTPathShadeProperties addNewPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPathShadeProperties)this.get_store().add_element_user(CTGradientFillPropertiesImpl.PATH$4);
        }
    }
    
    public void unsetPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGradientFillPropertiesImpl.PATH$4, 0);
        }
    }
    
    public CTRelativeRect getTileRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelativeRect ctRelativeRect = (CTRelativeRect)this.get_store().find_element_user(CTGradientFillPropertiesImpl.TILERECT$6, 0);
            if (ctRelativeRect == null) {
                return null;
            }
            return ctRelativeRect;
        }
    }
    
    public boolean isSetTileRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGradientFillPropertiesImpl.TILERECT$6) != 0;
        }
    }
    
    public void setTileRect(final CTRelativeRect ctRelativeRect) {
        this.generatedSetterHelperImpl((XmlObject)ctRelativeRect, CTGradientFillPropertiesImpl.TILERECT$6, 0, (short)1);
    }
    
    public CTRelativeRect addNewTileRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelativeRect)this.get_store().add_element_user(CTGradientFillPropertiesImpl.TILERECT$6);
        }
    }
    
    public void unsetTileRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGradientFillPropertiesImpl.TILERECT$6, 0);
        }
    }
    
    public STTileFlipMode.Enum getFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.FLIP$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTileFlipMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTileFlipMode xgetFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTileFlipMode)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.FLIP$8);
        }
    }
    
    public boolean isSetFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.FLIP$8) != null;
        }
    }
    
    public void setFlip(final STTileFlipMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.FLIP$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGradientFillPropertiesImpl.FLIP$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFlip(final STTileFlipMode stTileFlipMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTileFlipMode stTileFlipMode2 = (STTileFlipMode)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.FLIP$8);
            if (stTileFlipMode2 == null) {
                stTileFlipMode2 = (STTileFlipMode)this.get_store().add_attribute_user(CTGradientFillPropertiesImpl.FLIP$8);
            }
            stTileFlipMode2.set((XmlObject)stTileFlipMode);
        }
    }
    
    public void unsetFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGradientFillPropertiesImpl.FLIP$8);
        }
    }
    
    public boolean getRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
        }
    }
    
    public boolean isSetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10) != null;
        }
    }
    
    public void setRotWithShape(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRotWithShape(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRotWithShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGradientFillPropertiesImpl.ROTWITHSHAPE$10);
        }
    }
    
    static {
        GSLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gsLst");
        LIN$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lin");
        PATH$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "path");
        TILERECT$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tileRect");
        FLIP$8 = new QName("", "flip");
        ROTWITHSHAPE$10 = new QName("", "rotWithShape");
    }
}
