package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.RelType;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.PageType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PageTypeImpl extends XmlComplexContentImpl implements PageType
{
    private static final long serialVersionUID = 1L;
    private static final QName PAGESHEET$0;
    private static final QName REL$2;
    private static final QName ID$4;
    private static final QName NAME$6;
    private static final QName NAMEU$8;
    private static final QName ISCUSTOMNAME$10;
    private static final QName ISCUSTOMNAMEU$12;
    private static final QName BACKGROUND$14;
    private static final QName BACKPAGE$16;
    private static final QName VIEWSCALE$18;
    private static final QName VIEWCENTERX$20;
    private static final QName VIEWCENTERY$22;
    private static final QName REVIEWERID$24;
    private static final QName ASSOCIATEDPAGE$26;
    
    public PageTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public PageSheetType getPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PageSheetType pageSheetType = (PageSheetType)this.get_store().find_element_user(PageTypeImpl.PAGESHEET$0, 0);
            if (pageSheetType == null) {
                return null;
            }
            return pageSheetType;
        }
    }
    
    public boolean isSetPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(PageTypeImpl.PAGESHEET$0) != 0;
        }
    }
    
    public void setPageSheet(final PageSheetType pageSheetType) {
        this.generatedSetterHelperImpl((XmlObject)pageSheetType, PageTypeImpl.PAGESHEET$0, 0, (short)1);
    }
    
    public PageSheetType addNewPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PageSheetType)this.get_store().add_element_user(PageTypeImpl.PAGESHEET$0);
        }
    }
    
    public void unsetPageSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(PageTypeImpl.PAGESHEET$0, 0);
        }
    }
    
    public RelType getRel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final RelType relType = (RelType)this.get_store().find_element_user(PageTypeImpl.REL$2, 0);
            if (relType == null) {
                return null;
            }
            return relType;
        }
    }
    
    public void setRel(final RelType relType) {
        this.generatedSetterHelperImpl((XmlObject)relType, PageTypeImpl.REL$2, 0, (short)1);
    }
    
    public RelType addNewRel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RelType)this.get_store().add_element_user(PageTypeImpl.REL$2);
        }
    }
    
    public long getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ID$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.ID$4);
        }
    }
    
    public void setID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.ID$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.ID$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(PageTypeImpl.ID$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.NAME$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(PageTypeImpl.NAME$6);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.NAME$6) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.NAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.NAME$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(PageTypeImpl.NAME$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(PageTypeImpl.NAME$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.NAME$6);
        }
    }
    
    public String getNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.NAMEU$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(PageTypeImpl.NAMEU$8);
        }
    }
    
    public boolean isSetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.NAMEU$8) != null;
        }
    }
    
    public void setNameU(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.NAMEU$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.NAMEU$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetNameU(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(PageTypeImpl.NAMEU$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(PageTypeImpl.NAMEU$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.NAMEU$8);
        }
    }
    
    public boolean getIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAME$10);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAME$10);
        }
    }
    
    public boolean isSetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAME$10) != null;
        }
    }
    
    public void setIsCustomName(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.ISCUSTOMNAME$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIsCustomName(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAME$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(PageTypeImpl.ISCUSTOMNAME$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIsCustomName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.ISCUSTOMNAME$10);
        }
    }
    
    public boolean getIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12);
        }
    }
    
    public boolean isSetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12) != null;
        }
    }
    
    public void setIsCustomNameU(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIsCustomNameU(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(PageTypeImpl.ISCUSTOMNAMEU$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIsCustomNameU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.ISCUSTOMNAMEU$12);
        }
    }
    
    public boolean getBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.BACKGROUND$14);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(PageTypeImpl.BACKGROUND$14);
        }
    }
    
    public boolean isSetBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.BACKGROUND$14) != null;
        }
    }
    
    public void setBackground(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.BACKGROUND$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.BACKGROUND$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBackground(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(PageTypeImpl.BACKGROUND$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(PageTypeImpl.BACKGROUND$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBackground() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.BACKGROUND$14);
        }
    }
    
    public long getBackPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.BACKPAGE$16);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetBackPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.BACKPAGE$16);
        }
    }
    
    public boolean isSetBackPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.BACKPAGE$16) != null;
        }
    }
    
    public void setBackPage(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.BACKPAGE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.BACKPAGE$16);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetBackPage(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.BACKPAGE$16);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(PageTypeImpl.BACKPAGE$16);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetBackPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.BACKPAGE$16);
        }
    }
    
    public double getViewScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.VIEWSCALE$18);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetViewScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(PageTypeImpl.VIEWSCALE$18);
        }
    }
    
    public boolean isSetViewScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.VIEWSCALE$18) != null;
        }
    }
    
    public void setViewScale(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.VIEWSCALE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.VIEWSCALE$18);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetViewScale(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(PageTypeImpl.VIEWSCALE$18);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(PageTypeImpl.VIEWSCALE$18);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetViewScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.VIEWSCALE$18);
        }
    }
    
    public double getViewCenterX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERX$20);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetViewCenterX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERX$20);
        }
    }
    
    public boolean isSetViewCenterX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERX$20) != null;
        }
    }
    
    public void setViewCenterX(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERX$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.VIEWCENTERX$20);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetViewCenterX(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERX$20);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(PageTypeImpl.VIEWCENTERX$20);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetViewCenterX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.VIEWCENTERX$20);
        }
    }
    
    public double getViewCenterY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERY$22);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetViewCenterY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERY$22);
        }
    }
    
    public boolean isSetViewCenterY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERY$22) != null;
        }
    }
    
    public void setViewCenterY(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERY$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.VIEWCENTERY$22);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetViewCenterY(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(PageTypeImpl.VIEWCENTERY$22);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(PageTypeImpl.VIEWCENTERY$22);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetViewCenterY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.VIEWCENTERY$22);
        }
    }
    
    public long getReviewerID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.REVIEWERID$24);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetReviewerID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.REVIEWERID$24);
        }
    }
    
    public boolean isSetReviewerID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.REVIEWERID$24) != null;
        }
    }
    
    public void setReviewerID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.REVIEWERID$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.REVIEWERID$24);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetReviewerID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.REVIEWERID$24);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(PageTypeImpl.REVIEWERID$24);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetReviewerID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.REVIEWERID$24);
        }
    }
    
    public long getAssociatedPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetAssociatedPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26);
        }
    }
    
    public boolean isSetAssociatedPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26) != null;
        }
    }
    
    public void setAssociatedPage(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetAssociatedPage(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(PageTypeImpl.ASSOCIATEDPAGE$26);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetAssociatedPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(PageTypeImpl.ASSOCIATEDPAGE$26);
        }
    }
    
    static {
        PAGESHEET$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "PageSheet");
        REL$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Rel");
        ID$4 = new QName("", "ID");
        NAME$6 = new QName("", "Name");
        NAMEU$8 = new QName("", "NameU");
        ISCUSTOMNAME$10 = new QName("", "IsCustomName");
        ISCUSTOMNAMEU$12 = new QName("", "IsCustomNameU");
        BACKGROUND$14 = new QName("", "Background");
        BACKPAGE$16 = new QName("", "BackPage");
        VIEWSCALE$18 = new QName("", "ViewScale");
        VIEWCENTERX$20 = new QName("", "ViewCenterX");
        VIEWCENTERY$22 = new QName("", "ViewCenterY");
        REVIEWERID$24 = new QName("", "ReviewerID");
        ASSOCIATEDPAGE$26 = new QName("", "AssociatedPage");
    }
}
