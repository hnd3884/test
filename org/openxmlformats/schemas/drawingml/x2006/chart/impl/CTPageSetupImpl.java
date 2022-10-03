package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.chart.STPageSetupOrientation;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageSetup;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageSetupImpl extends XmlComplexContentImpl implements CTPageSetup
{
    private static final long serialVersionUID = 1L;
    private static final QName PAPERSIZE$0;
    private static final QName FIRSTPAGENUMBER$2;
    private static final QName ORIENTATION$4;
    private static final QName BLACKANDWHITE$6;
    private static final QName DRAFT$8;
    private static final QName USEFIRSTPAGENUMBER$10;
    private static final QName HORIZONTALDPI$12;
    private static final QName VERTICALDPI$14;
    private static final QName COPIES$16;
    
    public CTPageSetupImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getPaperSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.PAPERSIZE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.PAPERSIZE$0);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetPaperSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.PAPERSIZE$0);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.PAPERSIZE$0);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetPaperSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.PAPERSIZE$0) != null;
        }
    }
    
    public void setPaperSize(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.PAPERSIZE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.PAPERSIZE$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetPaperSize(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.PAPERSIZE$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.PAPERSIZE$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetPaperSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.PAPERSIZE$0);
        }
    }
    
    public long getFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2) != null;
        }
    }
    
    public void setFirstPageNumber(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFirstPageNumber(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.FIRSTPAGENUMBER$2);
        }
    }
    
    public STPageSetupOrientation.Enum getOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.ORIENTATION$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPageSetupOrientation.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPageSetupOrientation xgetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPageSetupOrientation stPageSetupOrientation = (STPageSetupOrientation)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$4);
            if (stPageSetupOrientation == null) {
                stPageSetupOrientation = (STPageSetupOrientation)this.get_default_attribute_value(CTPageSetupImpl.ORIENTATION$4);
            }
            return stPageSetupOrientation;
        }
    }
    
    public boolean isSetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$4) != null;
        }
    }
    
    public void setOrientation(final STPageSetupOrientation.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.ORIENTATION$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOrientation(final STPageSetupOrientation stPageSetupOrientation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPageSetupOrientation stPageSetupOrientation2 = (STPageSetupOrientation)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$4);
            if (stPageSetupOrientation2 == null) {
                stPageSetupOrientation2 = (STPageSetupOrientation)this.get_store().add_attribute_user(CTPageSetupImpl.ORIENTATION$4);
            }
            stPageSetupOrientation2.set((XmlObject)stPageSetupOrientation);
        }
    }
    
    public void unsetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.ORIENTATION$4);
        }
    }
    
    public boolean getBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.BLACKANDWHITE$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.BLACKANDWHITE$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6) != null;
        }
    }
    
    public void setBlackAndWhite(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBlackAndWhite(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.BLACKANDWHITE$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.BLACKANDWHITE$6);
        }
    }
    
    public boolean getDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.DRAFT$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.DRAFT$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$8) != null;
        }
    }
    
    public void setDraft(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.DRAFT$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDraft(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.DRAFT$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.DRAFT$8);
        }
    }
    
    public boolean getUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10) != null;
        }
    }
    
    public void setUseFirstPageNumber(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUseFirstPageNumber(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.USEFIRSTPAGENUMBER$10);
        }
    }
    
    public int getHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.HORIZONTALDPI$12);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTPageSetupImpl.HORIZONTALDPI$12);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12) != null;
        }
    }
    
    public void setHorizontalDpi(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetHorizontalDpi(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTPageSetupImpl.HORIZONTALDPI$12);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.HORIZONTALDPI$12);
        }
    }
    
    public int getVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.VERTICALDPI$14);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$14);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTPageSetupImpl.VERTICALDPI$14);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$14) != null;
        }
    }
    
    public void setVerticalDpi(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.VERTICALDPI$14);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVerticalDpi(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$14);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTPageSetupImpl.VERTICALDPI$14);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.VERTICALDPI$14);
        }
    }
    
    public long getCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.COPIES$16);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$16);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.COPIES$16);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$16) != null;
        }
    }
    
    public void setCopies(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.COPIES$16);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCopies(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$16);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.COPIES$16);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.COPIES$16);
        }
    }
    
    static {
        PAPERSIZE$0 = new QName("", "paperSize");
        FIRSTPAGENUMBER$2 = new QName("", "firstPageNumber");
        ORIENTATION$4 = new QName("", "orientation");
        BLACKANDWHITE$6 = new QName("", "blackAndWhite");
        DRAFT$8 = new QName("", "draft");
        USEFIRSTPAGENUMBER$10 = new QName("", "useFirstPageNumber");
        HORIZONTALDPI$12 = new QName("", "horizontalDpi");
        VERTICALDPI$14 = new QName("", "verticalDpi");
        COPIES$16 = new QName("", "copies");
    }
}
