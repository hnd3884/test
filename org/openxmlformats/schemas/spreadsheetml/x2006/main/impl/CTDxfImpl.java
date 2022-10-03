package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDxfImpl extends XmlComplexContentImpl implements CTDxf
{
    private static final long serialVersionUID = 1L;
    private static final QName FONT$0;
    private static final QName NUMFMT$2;
    private static final QName FILL$4;
    private static final QName ALIGNMENT$6;
    private static final QName BORDER$8;
    private static final QName PROTECTION$10;
    private static final QName EXTLST$12;
    
    public CTDxfImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTFont getFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFont ctFont = (CTFont)this.get_store().find_element_user(CTDxfImpl.FONT$0, 0);
            if (ctFont == null) {
                return null;
            }
            return ctFont;
        }
    }
    
    public boolean isSetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.FONT$0) != 0;
        }
    }
    
    public void setFont(final CTFont ctFont) {
        this.generatedSetterHelperImpl((XmlObject)ctFont, CTDxfImpl.FONT$0, 0, (short)1);
    }
    
    public CTFont addNewFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFont)this.get_store().add_element_user(CTDxfImpl.FONT$0);
        }
    }
    
    public void unsetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.FONT$0, 0);
        }
    }
    
    public CTNumFmt getNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumFmt ctNumFmt = (CTNumFmt)this.get_store().find_element_user(CTDxfImpl.NUMFMT$2, 0);
            if (ctNumFmt == null) {
                return null;
            }
            return ctNumFmt;
        }
    }
    
    public boolean isSetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.NUMFMT$2) != 0;
        }
    }
    
    public void setNumFmt(final CTNumFmt ctNumFmt) {
        this.generatedSetterHelperImpl((XmlObject)ctNumFmt, CTDxfImpl.NUMFMT$2, 0, (short)1);
    }
    
    public CTNumFmt addNewNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumFmt)this.get_store().add_element_user(CTDxfImpl.NUMFMT$2);
        }
    }
    
    public void unsetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.NUMFMT$2, 0);
        }
    }
    
    public CTFill getFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFill ctFill = (CTFill)this.get_store().find_element_user(CTDxfImpl.FILL$4, 0);
            if (ctFill == null) {
                return null;
            }
            return ctFill;
        }
    }
    
    public boolean isSetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.FILL$4) != 0;
        }
    }
    
    public void setFill(final CTFill ctFill) {
        this.generatedSetterHelperImpl((XmlObject)ctFill, CTDxfImpl.FILL$4, 0, (short)1);
    }
    
    public CTFill addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().add_element_user(CTDxfImpl.FILL$4);
        }
    }
    
    public void unsetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.FILL$4, 0);
        }
    }
    
    public CTCellAlignment getAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellAlignment ctCellAlignment = (CTCellAlignment)this.get_store().find_element_user(CTDxfImpl.ALIGNMENT$6, 0);
            if (ctCellAlignment == null) {
                return null;
            }
            return ctCellAlignment;
        }
    }
    
    public boolean isSetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.ALIGNMENT$6) != 0;
        }
    }
    
    public void setAlignment(final CTCellAlignment ctCellAlignment) {
        this.generatedSetterHelperImpl((XmlObject)ctCellAlignment, CTDxfImpl.ALIGNMENT$6, 0, (short)1);
    }
    
    public CTCellAlignment addNewAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellAlignment)this.get_store().add_element_user(CTDxfImpl.ALIGNMENT$6);
        }
    }
    
    public void unsetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.ALIGNMENT$6, 0);
        }
    }
    
    public CTBorder getBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTDxfImpl.BORDER$8, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.BORDER$8) != 0;
        }
    }
    
    public void setBorder(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTDxfImpl.BORDER$8, 0, (short)1);
    }
    
    public CTBorder addNewBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTDxfImpl.BORDER$8);
        }
    }
    
    public void unsetBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.BORDER$8, 0);
        }
    }
    
    public CTCellProtection getProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellProtection ctCellProtection = (CTCellProtection)this.get_store().find_element_user(CTDxfImpl.PROTECTION$10, 0);
            if (ctCellProtection == null) {
                return null;
            }
            return ctCellProtection;
        }
    }
    
    public boolean isSetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.PROTECTION$10) != 0;
        }
    }
    
    public void setProtection(final CTCellProtection ctCellProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctCellProtection, CTDxfImpl.PROTECTION$10, 0, (short)1);
    }
    
    public CTCellProtection addNewProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellProtection)this.get_store().add_element_user(CTDxfImpl.PROTECTION$10);
        }
    }
    
    public void unsetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.PROTECTION$10, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTDxfImpl.EXTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfImpl.EXTLST$12) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTDxfImpl.EXTLST$12, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTDxfImpl.EXTLST$12);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfImpl.EXTLST$12, 0);
        }
    }
    
    static {
        FONT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "font");
        NUMFMT$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "numFmt");
        FILL$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fill");
        ALIGNMENT$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "alignment");
        BORDER$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "border");
        PROTECTION$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "protection");
        EXTLST$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
