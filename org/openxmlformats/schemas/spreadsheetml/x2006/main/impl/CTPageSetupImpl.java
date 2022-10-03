package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPrintError;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellComments;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOrientation;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPageOrder;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageSetupImpl extends XmlComplexContentImpl implements CTPageSetup
{
    private static final long serialVersionUID = 1L;
    private static final QName PAPERSIZE$0;
    private static final QName SCALE$2;
    private static final QName FIRSTPAGENUMBER$4;
    private static final QName FITTOWIDTH$6;
    private static final QName FITTOHEIGHT$8;
    private static final QName PAGEORDER$10;
    private static final QName ORIENTATION$12;
    private static final QName USEPRINTERDEFAULTS$14;
    private static final QName BLACKANDWHITE$16;
    private static final QName DRAFT$18;
    private static final QName CELLCOMMENTS$20;
    private static final QName USEFIRSTPAGENUMBER$22;
    private static final QName ERRORS$24;
    private static final QName HORIZONTALDPI$26;
    private static final QName VERTICALDPI$28;
    private static final QName COPIES$30;
    private static final QName ID$32;
    
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
    
    public long getScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.SCALE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.SCALE$2);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.SCALE$2);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.SCALE$2);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.SCALE$2) != null;
        }
    }
    
    public void setScale(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.SCALE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.SCALE$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetScale(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.SCALE$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.SCALE$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.SCALE$2);
        }
    }
    
    public long getFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.FIRSTPAGENUMBER$4);
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
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4) != null;
        }
    }
    
    public void setFirstPageNumber(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFirstPageNumber(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.FIRSTPAGENUMBER$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.FIRSTPAGENUMBER$4);
        }
    }
    
    public long getFitToWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOWIDTH$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.FITTOWIDTH$6);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFitToWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOWIDTH$6);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.FITTOWIDTH$6);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetFitToWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.FITTOWIDTH$6) != null;
        }
    }
    
    public void setFitToWidth(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOWIDTH$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.FITTOWIDTH$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFitToWidth(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOWIDTH$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.FITTOWIDTH$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFitToWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.FITTOWIDTH$6);
        }
    }
    
    public long getFitToHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.FITTOHEIGHT$8);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFitToHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.FITTOHEIGHT$8);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetFitToHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8) != null;
        }
    }
    
    public void setFitToHeight(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFitToHeight(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.FITTOHEIGHT$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFitToHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.FITTOHEIGHT$8);
        }
    }
    
    public STPageOrder.Enum getPageOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.PAGEORDER$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.PAGEORDER$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPageOrder.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPageOrder xgetPageOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPageOrder stPageOrder = (STPageOrder)this.get_store().find_attribute_user(CTPageSetupImpl.PAGEORDER$10);
            if (stPageOrder == null) {
                stPageOrder = (STPageOrder)this.get_default_attribute_value(CTPageSetupImpl.PAGEORDER$10);
            }
            return stPageOrder;
        }
    }
    
    public boolean isSetPageOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.PAGEORDER$10) != null;
        }
    }
    
    public void setPageOrder(final STPageOrder.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.PAGEORDER$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.PAGEORDER$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPageOrder(final STPageOrder stPageOrder) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPageOrder stPageOrder2 = (STPageOrder)this.get_store().find_attribute_user(CTPageSetupImpl.PAGEORDER$10);
            if (stPageOrder2 == null) {
                stPageOrder2 = (STPageOrder)this.get_store().add_attribute_user(CTPageSetupImpl.PAGEORDER$10);
            }
            stPageOrder2.set((XmlObject)stPageOrder);
        }
    }
    
    public void unsetPageOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.PAGEORDER$10);
        }
    }
    
    public STOrientation.Enum getOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.ORIENTATION$12);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STOrientation.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOrientation xgetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOrientation stOrientation = (STOrientation)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$12);
            if (stOrientation == null) {
                stOrientation = (STOrientation)this.get_default_attribute_value(CTPageSetupImpl.ORIENTATION$12);
            }
            return stOrientation;
        }
    }
    
    public boolean isSetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$12) != null;
        }
    }
    
    public void setOrientation(final STOrientation.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.ORIENTATION$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOrientation(final STOrientation stOrientation) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOrientation stOrientation2 = (STOrientation)this.get_store().find_attribute_user(CTPageSetupImpl.ORIENTATION$12);
            if (stOrientation2 == null) {
                stOrientation2 = (STOrientation)this.get_store().add_attribute_user(CTPageSetupImpl.ORIENTATION$12);
            }
            stOrientation2.set((XmlObject)stOrientation);
        }
    }
    
    public void unsetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.ORIENTATION$12);
        }
    }
    
    public boolean getUsePrinterDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUsePrinterDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUsePrinterDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14) != null;
        }
    }
    
    public void setUsePrinterDefaults(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUsePrinterDefaults(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUsePrinterDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.USEPRINTERDEFAULTS$14);
        }
    }
    
    public boolean getBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.BLACKANDWHITE$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.BLACKANDWHITE$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16) != null;
        }
    }
    
    public void setBlackAndWhite(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBlackAndWhite(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.BLACKANDWHITE$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBlackAndWhite() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.BLACKANDWHITE$16);
        }
    }
    
    public boolean getDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.DRAFT$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.DRAFT$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$18) != null;
        }
    }
    
    public void setDraft(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.DRAFT$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDraft(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.DRAFT$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.DRAFT$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDraft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.DRAFT$18);
        }
    }
    
    public STCellComments.Enum getCellComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.CELLCOMMENTS$20);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STCellComments.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCellComments xgetCellComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellComments stCellComments = (STCellComments)this.get_store().find_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20);
            if (stCellComments == null) {
                stCellComments = (STCellComments)this.get_default_attribute_value(CTPageSetupImpl.CELLCOMMENTS$20);
            }
            return stCellComments;
        }
    }
    
    public boolean isSetCellComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20) != null;
        }
    }
    
    public void setCellComments(final STCellComments.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCellComments(final STCellComments stCellComments) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellComments stCellComments2 = (STCellComments)this.get_store().find_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20);
            if (stCellComments2 == null) {
                stCellComments2 = (STCellComments)this.get_store().add_attribute_user(CTPageSetupImpl.CELLCOMMENTS$20);
            }
            stCellComments2.set((XmlObject)stCellComments);
        }
    }
    
    public void unsetCellComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.CELLCOMMENTS$20);
        }
    }
    
    public boolean getUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22) != null;
        }
    }
    
    public void setUseFirstPageNumber(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUseFirstPageNumber(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUseFirstPageNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.USEFIRSTPAGENUMBER$22);
        }
    }
    
    public STPrintError.Enum getErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ERRORS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.ERRORS$24);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPrintError.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPrintError xgetErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPrintError stPrintError = (STPrintError)this.get_store().find_attribute_user(CTPageSetupImpl.ERRORS$24);
            if (stPrintError == null) {
                stPrintError = (STPrintError)this.get_default_attribute_value(CTPageSetupImpl.ERRORS$24);
            }
            return stPrintError;
        }
    }
    
    public boolean isSetErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.ERRORS$24) != null;
        }
    }
    
    public void setErrors(final STPrintError.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ERRORS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.ERRORS$24);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetErrors(final STPrintError stPrintError) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPrintError stPrintError2 = (STPrintError)this.get_store().find_attribute_user(CTPageSetupImpl.ERRORS$24);
            if (stPrintError2 == null) {
                stPrintError2 = (STPrintError)this.get_store().add_attribute_user(CTPageSetupImpl.ERRORS$24);
            }
            stPrintError2.set((XmlObject)stPrintError);
        }
    }
    
    public void unsetErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.ERRORS$24);
        }
    }
    
    public long getHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.HORIZONTALDPI$26);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.HORIZONTALDPI$26);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26) != null;
        }
    }
    
    public void setHorizontalDpi(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetHorizontalDpi(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.HORIZONTALDPI$26);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetHorizontalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.HORIZONTALDPI$26);
        }
    }
    
    public long getVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.VERTICALDPI$28);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$28);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.VERTICALDPI$28);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$28) != null;
        }
    }
    
    public void setVerticalDpi(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.VERTICALDPI$28);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetVerticalDpi(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.VERTICALDPI$28);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.VERTICALDPI$28);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetVerticalDpi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.VERTICALDPI$28);
        }
    }
    
    public long getCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPageSetupImpl.COPIES$30);
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
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$30);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPageSetupImpl.COPIES$30);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$30) != null;
        }
    }
    
    public void setCopies(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.COPIES$30);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCopies(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPageSetupImpl.COPIES$30);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPageSetupImpl.COPIES$30);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCopies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.COPIES$30);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ID$32);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTPageSetupImpl.ID$32);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPageSetupImpl.ID$32) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageSetupImpl.ID$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageSetupImpl.ID$32);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTPageSetupImpl.ID$32);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTPageSetupImpl.ID$32);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPageSetupImpl.ID$32);
        }
    }
    
    static {
        PAPERSIZE$0 = new QName("", "paperSize");
        SCALE$2 = new QName("", "scale");
        FIRSTPAGENUMBER$4 = new QName("", "firstPageNumber");
        FITTOWIDTH$6 = new QName("", "fitToWidth");
        FITTOHEIGHT$8 = new QName("", "fitToHeight");
        PAGEORDER$10 = new QName("", "pageOrder");
        ORIENTATION$12 = new QName("", "orientation");
        USEPRINTERDEFAULTS$14 = new QName("", "usePrinterDefaults");
        BLACKANDWHITE$16 = new QName("", "blackAndWhite");
        DRAFT$18 = new QName("", "draft");
        CELLCOMMENTS$20 = new QName("", "cellComments");
        USEFIRSTPAGENUMBER$22 = new QName("", "useFirstPageNumber");
        ERRORS$24 = new QName("", "errors");
        HORIZONTALDPI$26 = new QName("", "horizontalDpi");
        VERTICALDPI$28 = new QName("", "verticalDpi");
        COPIES$30 = new QName("", "copies");
        ID$32 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
    }
}
