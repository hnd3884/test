package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetFormatPrImpl extends XmlComplexContentImpl implements CTSheetFormatPr
{
    private static final long serialVersionUID = 1L;
    private static final QName BASECOLWIDTH$0;
    private static final QName DEFAULTCOLWIDTH$2;
    private static final QName DEFAULTROWHEIGHT$4;
    private static final QName CUSTOMHEIGHT$6;
    private static final QName ZEROHEIGHT$8;
    private static final QName THICKTOP$10;
    private static final QName THICKBOTTOM$12;
    private static final QName OUTLINELEVELROW$14;
    private static final QName OUTLINELEVELCOL$16;
    
    public CTSheetFormatPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getBaseColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetBaseColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetBaseColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0) != null;
        }
    }
    
    public void setBaseColWidth(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetBaseColWidth(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetFormatPrImpl.BASECOLWIDTH$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetBaseColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.BASECOLWIDTH$0);
        }
    }
    
    public double getDefaultColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetDefaultColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
        }
    }
    
    public boolean isSetDefaultColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2) != null;
        }
    }
    
    public void setDefaultColWidth(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetDefaultColWidth(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetDefaultColWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.DEFAULTCOLWIDTH$2);
        }
    }
    
    public double getDefaultRowHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTROWHEIGHT$4);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetDefaultRowHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTROWHEIGHT$4);
        }
    }
    
    public void setDefaultRowHeight(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTROWHEIGHT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.DEFAULTROWHEIGHT$4);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetDefaultRowHeight(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTSheetFormatPrImpl.DEFAULTROWHEIGHT$4);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTSheetFormatPrImpl.DEFAULTROWHEIGHT$4);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public boolean getCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6) != null;
        }
    }
    
    public void setCustomHeight(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCustomHeight(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCustomHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.CUSTOMHEIGHT$6);
        }
    }
    
    public boolean getZeroHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetZeroHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetZeroHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8) != null;
        }
    }
    
    public void setZeroHeight(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetZeroHeight(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetFormatPrImpl.ZEROHEIGHT$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetZeroHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.ZEROHEIGHT$8);
        }
    }
    
    public boolean getThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKTOP$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.THICKTOP$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKTOP$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetFormatPrImpl.THICKTOP$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKTOP$10) != null;
        }
    }
    
    public void setThickTop(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKTOP$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.THICKTOP$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetThickTop(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKTOP$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetFormatPrImpl.THICKTOP$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetThickTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.THICKTOP$10);
        }
    }
    
    public boolean getThickBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.THICKBOTTOM$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetThickBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetFormatPrImpl.THICKBOTTOM$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetThickBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12) != null;
        }
    }
    
    public void setThickBottom(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetThickBottom(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetFormatPrImpl.THICKBOTTOM$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetThickBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.THICKBOTTOM$12);
        }
    }
    
    public short getOutlineLevelRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetOutlineLevelRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetOutlineLevelRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14) != null;
        }
    }
    
    public void setOutlineLevelRow(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetOutlineLevelRow(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetOutlineLevelRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.OUTLINELEVELROW$14);
        }
    }
    
    public short getOutlineLevelCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetOutlineLevelCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetOutlineLevelCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16) != null;
        }
    }
    
    public void setOutlineLevelCol(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetOutlineLevelCol(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetOutlineLevelCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetFormatPrImpl.OUTLINELEVELCOL$16);
        }
    }
    
    static {
        BASECOLWIDTH$0 = new QName("", "baseColWidth");
        DEFAULTCOLWIDTH$2 = new QName("", "defaultColWidth");
        DEFAULTROWHEIGHT$4 = new QName("", "defaultRowHeight");
        CUSTOMHEIGHT$6 = new QName("", "customHeight");
        ZEROHEIGHT$8 = new QName("", "zeroHeight");
        THICKTOP$10 = new QName("", "thickTop");
        THICKBOTTOM$12 = new QName("", "thickBottom");
        OUTLINELEVELROW$14 = new QName("", "outlineLevelRow");
        OUTLINELEVELCOL$16 = new QName("", "outlineLevelCol");
    }
}
