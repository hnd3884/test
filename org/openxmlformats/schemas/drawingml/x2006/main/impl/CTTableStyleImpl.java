package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.drawingml.x2006.main.STGuid;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTablePartStyle;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableBackgroundStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleImpl extends XmlComplexContentImpl implements CTTableStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLBG$0;
    private static final QName WHOLETBL$2;
    private static final QName BAND1H$4;
    private static final QName BAND2H$6;
    private static final QName BAND1V$8;
    private static final QName BAND2V$10;
    private static final QName LASTCOL$12;
    private static final QName FIRSTCOL$14;
    private static final QName LASTROW$16;
    private static final QName SECELL$18;
    private static final QName SWCELL$20;
    private static final QName FIRSTROW$22;
    private static final QName NECELL$24;
    private static final QName NWCELL$26;
    private static final QName EXTLST$28;
    private static final QName STYLEID$30;
    private static final QName STYLENAME$32;
    
    public CTTableStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTableBackgroundStyle getTblBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableBackgroundStyle ctTableBackgroundStyle = (CTTableBackgroundStyle)this.get_store().find_element_user(CTTableStyleImpl.TBLBG$0, 0);
            if (ctTableBackgroundStyle == null) {
                return null;
            }
            return ctTableBackgroundStyle;
        }
    }
    
    public boolean isSetTblBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.TBLBG$0) != 0;
        }
    }
    
    public void setTblBg(final CTTableBackgroundStyle ctTableBackgroundStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableBackgroundStyle, CTTableStyleImpl.TBLBG$0, 0, (short)1);
    }
    
    public CTTableBackgroundStyle addNewTblBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableBackgroundStyle)this.get_store().add_element_user(CTTableStyleImpl.TBLBG$0);
        }
    }
    
    public void unsetTblBg() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.TBLBG$0, 0);
        }
    }
    
    public CTTablePartStyle getWholeTbl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.WHOLETBL$2, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetWholeTbl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.WHOLETBL$2) != 0;
        }
    }
    
    public void setWholeTbl(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.WHOLETBL$2, 0, (short)1);
    }
    
    public CTTablePartStyle addNewWholeTbl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.WHOLETBL$2);
        }
    }
    
    public void unsetWholeTbl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.WHOLETBL$2, 0);
        }
    }
    
    public CTTablePartStyle getBand1H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.BAND1H$4, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetBand1H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.BAND1H$4) != 0;
        }
    }
    
    public void setBand1H(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.BAND1H$4, 0, (short)1);
    }
    
    public CTTablePartStyle addNewBand1H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.BAND1H$4);
        }
    }
    
    public void unsetBand1H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.BAND1H$4, 0);
        }
    }
    
    public CTTablePartStyle getBand2H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.BAND2H$6, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetBand2H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.BAND2H$6) != 0;
        }
    }
    
    public void setBand2H(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.BAND2H$6, 0, (short)1);
    }
    
    public CTTablePartStyle addNewBand2H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.BAND2H$6);
        }
    }
    
    public void unsetBand2H() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.BAND2H$6, 0);
        }
    }
    
    public CTTablePartStyle getBand1V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.BAND1V$8, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetBand1V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.BAND1V$8) != 0;
        }
    }
    
    public void setBand1V(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.BAND1V$8, 0, (short)1);
    }
    
    public CTTablePartStyle addNewBand1V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.BAND1V$8);
        }
    }
    
    public void unsetBand1V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.BAND1V$8, 0);
        }
    }
    
    public CTTablePartStyle getBand2V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.BAND2V$10, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetBand2V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.BAND2V$10) != 0;
        }
    }
    
    public void setBand2V(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.BAND2V$10, 0, (short)1);
    }
    
    public CTTablePartStyle addNewBand2V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.BAND2V$10);
        }
    }
    
    public void unsetBand2V() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.BAND2V$10, 0);
        }
    }
    
    public CTTablePartStyle getLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.LASTCOL$12, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.LASTCOL$12) != 0;
        }
    }
    
    public void setLastCol(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.LASTCOL$12, 0, (short)1);
    }
    
    public CTTablePartStyle addNewLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.LASTCOL$12);
        }
    }
    
    public void unsetLastCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.LASTCOL$12, 0);
        }
    }
    
    public CTTablePartStyle getFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.FIRSTCOL$14, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.FIRSTCOL$14) != 0;
        }
    }
    
    public void setFirstCol(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.FIRSTCOL$14, 0, (short)1);
    }
    
    public CTTablePartStyle addNewFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.FIRSTCOL$14);
        }
    }
    
    public void unsetFirstCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.FIRSTCOL$14, 0);
        }
    }
    
    public CTTablePartStyle getLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.LASTROW$16, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.LASTROW$16) != 0;
        }
    }
    
    public void setLastRow(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.LASTROW$16, 0, (short)1);
    }
    
    public CTTablePartStyle addNewLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.LASTROW$16);
        }
    }
    
    public void unsetLastRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.LASTROW$16, 0);
        }
    }
    
    public CTTablePartStyle getSeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.SECELL$18, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetSeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.SECELL$18) != 0;
        }
    }
    
    public void setSeCell(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.SECELL$18, 0, (short)1);
    }
    
    public CTTablePartStyle addNewSeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.SECELL$18);
        }
    }
    
    public void unsetSeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.SECELL$18, 0);
        }
    }
    
    public CTTablePartStyle getSwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.SWCELL$20, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetSwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.SWCELL$20) != 0;
        }
    }
    
    public void setSwCell(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.SWCELL$20, 0, (short)1);
    }
    
    public CTTablePartStyle addNewSwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.SWCELL$20);
        }
    }
    
    public void unsetSwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.SWCELL$20, 0);
        }
    }
    
    public CTTablePartStyle getFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.FIRSTROW$22, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.FIRSTROW$22) != 0;
        }
    }
    
    public void setFirstRow(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.FIRSTROW$22, 0, (short)1);
    }
    
    public CTTablePartStyle addNewFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.FIRSTROW$22);
        }
    }
    
    public void unsetFirstRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.FIRSTROW$22, 0);
        }
    }
    
    public CTTablePartStyle getNeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.NECELL$24, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetNeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.NECELL$24) != 0;
        }
    }
    
    public void setNeCell(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.NECELL$24, 0, (short)1);
    }
    
    public CTTablePartStyle addNewNeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.NECELL$24);
        }
    }
    
    public void unsetNeCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.NECELL$24, 0);
        }
    }
    
    public CTTablePartStyle getNwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTablePartStyle ctTablePartStyle = (CTTablePartStyle)this.get_store().find_element_user(CTTableStyleImpl.NWCELL$26, 0);
            if (ctTablePartStyle == null) {
                return null;
            }
            return ctTablePartStyle;
        }
    }
    
    public boolean isSetNwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.NWCELL$26) != 0;
        }
    }
    
    public void setNwCell(final CTTablePartStyle ctTablePartStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTablePartStyle, CTTableStyleImpl.NWCELL$26, 0, (short)1);
    }
    
    public CTTablePartStyle addNewNwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTablePartStyle)this.get_store().add_element_user(CTTableStyleImpl.NWCELL$26);
        }
    }
    
    public void unsetNwCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.NWCELL$26, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTableStyleImpl.EXTLST$28, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.EXTLST$28) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableStyleImpl.EXTLST$28, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTableStyleImpl.EXTLST$28);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.EXTLST$28, 0);
        }
    }
    
    public String getStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.STYLEID$30);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGuid xgetStyleId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGuid)this.get_store().find_attribute_user(CTTableStyleImpl.STYLEID$30);
        }
    }
    
    public void setStyleId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.STYLEID$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleImpl.STYLEID$30);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyleId(final STGuid stGuid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGuid stGuid2 = (STGuid)this.get_store().find_attribute_user(CTTableStyleImpl.STYLEID$30);
            if (stGuid2 == null) {
                stGuid2 = (STGuid)this.get_store().add_attribute_user(CTTableStyleImpl.STYLEID$30);
            }
            stGuid2.set((XmlObject)stGuid);
        }
    }
    
    public String getStyleName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.STYLENAME$32);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStyleName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTableStyleImpl.STYLENAME$32);
        }
    }
    
    public void setStyleName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.STYLENAME$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleImpl.STYLENAME$32);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyleName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTableStyleImpl.STYLENAME$32);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTableStyleImpl.STYLENAME$32);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        TBLBG$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblBg");
        WHOLETBL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "wholeTbl");
        BAND1H$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "band1H");
        BAND2H$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "band2H");
        BAND1V$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "band1V");
        BAND2V$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "band2V");
        LASTCOL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lastCol");
        FIRSTCOL$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "firstCol");
        LASTROW$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lastRow");
        SECELL$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "seCell");
        SWCELL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "swCell");
        FIRSTROW$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "firstRow");
        NECELL$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "neCell");
        NWCELL$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "nwCell");
        EXTLST$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        STYLEID$30 = new QName("", "styleId");
        STYLENAME$32 = new QName("", "styleName");
    }
}
