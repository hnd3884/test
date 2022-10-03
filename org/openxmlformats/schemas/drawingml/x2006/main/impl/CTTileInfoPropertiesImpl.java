package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STRectAlignment;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STTileFlipMode;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTileInfoPropertiesImpl extends XmlComplexContentImpl implements CTTileInfoProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName TX$0;
    private static final QName TY$2;
    private static final QName SX$4;
    private static final QName SY$6;
    private static final QName FLIP$8;
    private static final QName ALGN$10;
    
    public CTTileInfoPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TX$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TX$0);
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TX$0) != null;
        }
    }
    
    public void setTx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TX$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.TX$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTx(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TX$0);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.TX$0);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTileInfoPropertiesImpl.TX$0);
        }
    }
    
    public long getTy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TY$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCoordinate xgetTy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TY$2);
        }
    }
    
    public boolean isSetTy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TY$2) != null;
        }
    }
    
    public void setTy(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TY$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.TY$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTy(final STCoordinate stCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate stCoordinate2 = (STCoordinate)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.TY$2);
            if (stCoordinate2 == null) {
                stCoordinate2 = (STCoordinate)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.TY$2);
            }
            stCoordinate2.set((XmlObject)stCoordinate);
        }
    }
    
    public void unsetTy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTileInfoPropertiesImpl.TY$2);
        }
    }
    
    public int getSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SX$4);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SX$4);
        }
    }
    
    public boolean isSetSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SX$4) != null;
        }
    }
    
    public void setSx(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SX$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.SX$4);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSx(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SX$4);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.SX$4);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetSx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTileInfoPropertiesImpl.SX$4);
        }
    }
    
    public int getSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SY$6);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SY$6);
        }
    }
    
    public boolean isSetSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SY$6) != null;
        }
    }
    
    public void setSy(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SY$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.SY$6);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSy(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.SY$6);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.SY$6);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetSy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTileInfoPropertiesImpl.SY$6);
        }
    }
    
    public STTileFlipMode.Enum getFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.FLIP$8);
            if (simpleValue == null) {
                return null;
            }
            return (STTileFlipMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTileFlipMode xgetFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTileFlipMode)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.FLIP$8);
        }
    }
    
    public boolean isSetFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.FLIP$8) != null;
        }
    }
    
    public void setFlip(final STTileFlipMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.FLIP$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.FLIP$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFlip(final STTileFlipMode stTileFlipMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTileFlipMode stTileFlipMode2 = (STTileFlipMode)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.FLIP$8);
            if (stTileFlipMode2 == null) {
                stTileFlipMode2 = (STTileFlipMode)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.FLIP$8);
            }
            stTileFlipMode2.set((XmlObject)stTileFlipMode);
        }
    }
    
    public void unsetFlip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTileInfoPropertiesImpl.FLIP$8);
        }
    }
    
    public STRectAlignment.Enum getAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.ALGN$10);
            if (simpleValue == null) {
                return null;
            }
            return (STRectAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STRectAlignment xgetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRectAlignment)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.ALGN$10);
        }
    }
    
    public boolean isSetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.ALGN$10) != null;
        }
    }
    
    public void setAlgn(final STRectAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.ALGN$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.ALGN$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlgn(final STRectAlignment stRectAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRectAlignment stRectAlignment2 = (STRectAlignment)this.get_store().find_attribute_user(CTTileInfoPropertiesImpl.ALGN$10);
            if (stRectAlignment2 == null) {
                stRectAlignment2 = (STRectAlignment)this.get_store().add_attribute_user(CTTileInfoPropertiesImpl.ALGN$10);
            }
            stRectAlignment2.set((XmlObject)stRectAlignment);
        }
    }
    
    public void unsetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTileInfoPropertiesImpl.ALGN$10);
        }
    }
    
    static {
        TX$0 = new QName("", "tx");
        TY$2 = new QName("", "ty");
        SX$4 = new QName("", "sx");
        SY$6 = new QName("", "sy");
        FLIP$8 = new QName("", "flip");
        ALGN$10 = new QName("", "algn");
    }
}
