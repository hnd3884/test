package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVisibility;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBookViewImpl extends XmlComplexContentImpl implements CTBookView
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName VISIBILITY$2;
    private static final QName MINIMIZED$4;
    private static final QName SHOWHORIZONTALSCROLL$6;
    private static final QName SHOWVERTICALSCROLL$8;
    private static final QName SHOWSHEETTABS$10;
    private static final QName XWINDOW$12;
    private static final QName YWINDOW$14;
    private static final QName WINDOWWIDTH$16;
    private static final QName WINDOWHEIGHT$18;
    private static final QName TABRATIO$20;
    private static final QName FIRSTSHEET$22;
    private static final QName ACTIVETAB$24;
    private static final QName AUTOFILTERDATEGROUPING$26;
    
    public CTBookViewImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTBookViewImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBookViewImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBookViewImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTBookViewImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBookViewImpl.EXTLST$0, 0);
        }
    }
    
    public STVisibility.Enum getVisibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.VISIBILITY$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.VISIBILITY$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STVisibility.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STVisibility xgetVisibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVisibility stVisibility = (STVisibility)this.get_store().find_attribute_user(CTBookViewImpl.VISIBILITY$2);
            if (stVisibility == null) {
                stVisibility = (STVisibility)this.get_default_attribute_value(CTBookViewImpl.VISIBILITY$2);
            }
            return stVisibility;
        }
    }
    
    public boolean isSetVisibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.VISIBILITY$2) != null;
        }
    }
    
    public void setVisibility(final STVisibility.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.VISIBILITY$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.VISIBILITY$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVisibility(final STVisibility stVisibility) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVisibility stVisibility2 = (STVisibility)this.get_store().find_attribute_user(CTBookViewImpl.VISIBILITY$2);
            if (stVisibility2 == null) {
                stVisibility2 = (STVisibility)this.get_store().add_attribute_user(CTBookViewImpl.VISIBILITY$2);
            }
            stVisibility2.set((XmlObject)stVisibility);
        }
    }
    
    public void unsetVisibility() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.VISIBILITY$2);
        }
    }
    
    public boolean getMinimized() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.MINIMIZED$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.MINIMIZED$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMinimized() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.MINIMIZED$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBookViewImpl.MINIMIZED$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMinimized() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.MINIMIZED$4) != null;
        }
    }
    
    public void setMinimized(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.MINIMIZED$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.MINIMIZED$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMinimized(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.MINIMIZED$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBookViewImpl.MINIMIZED$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMinimized() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.MINIMIZED$4);
        }
    }
    
    public boolean getShowHorizontalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowHorizontalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowHorizontalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6) != null;
        }
    }
    
    public void setShowHorizontalScroll(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowHorizontalScroll(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowHorizontalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.SHOWHORIZONTALSCROLL$6);
        }
    }
    
    public boolean getShowVerticalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowVerticalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowVerticalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8) != null;
        }
    }
    
    public void setShowVerticalScroll(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowVerticalScroll(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBookViewImpl.SHOWVERTICALSCROLL$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowVerticalScroll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.SHOWVERTICALSCROLL$8);
        }
    }
    
    public boolean getShowSheetTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.SHOWSHEETTABS$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowSheetTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBookViewImpl.SHOWSHEETTABS$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowSheetTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10) != null;
        }
    }
    
    public void setShowSheetTabs(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowSheetTabs(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBookViewImpl.SHOWSHEETTABS$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowSheetTabs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.SHOWSHEETTABS$10);
        }
    }
    
    public int getXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.XWINDOW$12);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(CTBookViewImpl.XWINDOW$12);
        }
    }
    
    public boolean isSetXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.XWINDOW$12) != null;
        }
    }
    
    public void setXWindow(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.XWINDOW$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.XWINDOW$12);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetXWindow(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTBookViewImpl.XWINDOW$12);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTBookViewImpl.XWINDOW$12);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.XWINDOW$12);
        }
    }
    
    public int getYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.YWINDOW$14);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(CTBookViewImpl.YWINDOW$14);
        }
    }
    
    public boolean isSetYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.YWINDOW$14) != null;
        }
    }
    
    public void setYWindow(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.YWINDOW$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.YWINDOW$14);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetYWindow(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTBookViewImpl.YWINDOW$14);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTBookViewImpl.YWINDOW$14);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.YWINDOW$14);
        }
    }
    
    public long getWindowWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWWIDTH$16);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetWindowWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWWIDTH$16);
        }
    }
    
    public boolean isSetWindowWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.WINDOWWIDTH$16) != null;
        }
    }
    
    public void setWindowWidth(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWWIDTH$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.WINDOWWIDTH$16);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetWindowWidth(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWWIDTH$16);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBookViewImpl.WINDOWWIDTH$16);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetWindowWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.WINDOWWIDTH$16);
        }
    }
    
    public long getWindowHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetWindowHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18);
        }
    }
    
    public boolean isSetWindowHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18) != null;
        }
    }
    
    public void setWindowHeight(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetWindowHeight(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBookViewImpl.WINDOWHEIGHT$18);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetWindowHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.WINDOWHEIGHT$18);
        }
    }
    
    public long getTabRatio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.TABRATIO$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.TABRATIO$20);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetTabRatio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.TABRATIO$20);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTBookViewImpl.TABRATIO$20);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetTabRatio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.TABRATIO$20) != null;
        }
    }
    
    public void setTabRatio(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.TABRATIO$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.TABRATIO$20);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTabRatio(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.TABRATIO$20);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBookViewImpl.TABRATIO$20);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetTabRatio() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.TABRATIO$20);
        }
    }
    
    public long getFirstSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.FIRSTSHEET$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.FIRSTSHEET$22);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFirstSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.FIRSTSHEET$22);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTBookViewImpl.FIRSTSHEET$22);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetFirstSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.FIRSTSHEET$22) != null;
        }
    }
    
    public void setFirstSheet(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.FIRSTSHEET$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.FIRSTSHEET$22);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFirstSheet(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.FIRSTSHEET$22);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBookViewImpl.FIRSTSHEET$22);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFirstSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.FIRSTSHEET$22);
        }
    }
    
    public long getActiveTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.ACTIVETAB$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.ACTIVETAB$24);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetActiveTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.ACTIVETAB$24);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTBookViewImpl.ACTIVETAB$24);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetActiveTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.ACTIVETAB$24) != null;
        }
    }
    
    public void setActiveTab(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.ACTIVETAB$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.ACTIVETAB$24);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetActiveTab(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBookViewImpl.ACTIVETAB$24);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBookViewImpl.ACTIVETAB$24);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetActiveTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.ACTIVETAB$24);
        }
    }
    
    public boolean getAutoFilterDateGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoFilterDateGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoFilterDateGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26) != null;
        }
    }
    
    public void setAutoFilterDateGrouping(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoFilterDateGrouping(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoFilterDateGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBookViewImpl.AUTOFILTERDATEGROUPING$26);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        VISIBILITY$2 = new QName("", "visibility");
        MINIMIZED$4 = new QName("", "minimized");
        SHOWHORIZONTALSCROLL$6 = new QName("", "showHorizontalScroll");
        SHOWVERTICALSCROLL$8 = new QName("", "showVerticalScroll");
        SHOWSHEETTABS$10 = new QName("", "showSheetTabs");
        XWINDOW$12 = new QName("", "xWindow");
        YWINDOW$14 = new QName("", "yWindow");
        WINDOWWIDTH$16 = new QName("", "windowWidth");
        WINDOWHEIGHT$18 = new QName("", "windowHeight");
        TABRATIO$20 = new QName("", "tabRatio");
        FIRSTSHEET$22 = new QName("", "firstSheet");
        ACTIVETAB$24 = new QName("", "activeTab");
        AUTOFILTERDATEGROUPING$26 = new QName("", "autoFilterDateGrouping");
    }
}
