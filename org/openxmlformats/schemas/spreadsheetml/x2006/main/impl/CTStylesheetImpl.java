package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyleXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorders;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFills;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFonts;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmts;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStylesheetImpl extends XmlComplexContentImpl implements CTStylesheet
{
    private static final long serialVersionUID = 1L;
    private static final QName NUMFMTS$0;
    private static final QName FONTS$2;
    private static final QName FILLS$4;
    private static final QName BORDERS$6;
    private static final QName CELLSTYLEXFS$8;
    private static final QName CELLXFS$10;
    private static final QName CELLSTYLES$12;
    private static final QName DXFS$14;
    private static final QName TABLESTYLES$16;
    private static final QName COLORS$18;
    private static final QName EXTLST$20;
    
    public CTStylesheetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNumFmts getNumFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumFmts ctNumFmts = (CTNumFmts)this.get_store().find_element_user(CTStylesheetImpl.NUMFMTS$0, 0);
            if (ctNumFmts == null) {
                return null;
            }
            return ctNumFmts;
        }
    }
    
    public boolean isSetNumFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.NUMFMTS$0) != 0;
        }
    }
    
    public void setNumFmts(final CTNumFmts ctNumFmts) {
        this.generatedSetterHelperImpl((XmlObject)ctNumFmts, CTStylesheetImpl.NUMFMTS$0, 0, (short)1);
    }
    
    public CTNumFmts addNewNumFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumFmts)this.get_store().add_element_user(CTStylesheetImpl.NUMFMTS$0);
        }
    }
    
    public void unsetNumFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.NUMFMTS$0, 0);
        }
    }
    
    public CTFonts getFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFonts ctFonts = (CTFonts)this.get_store().find_element_user(CTStylesheetImpl.FONTS$2, 0);
            if (ctFonts == null) {
                return null;
            }
            return ctFonts;
        }
    }
    
    public boolean isSetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.FONTS$2) != 0;
        }
    }
    
    public void setFonts(final CTFonts ctFonts) {
        this.generatedSetterHelperImpl((XmlObject)ctFonts, CTStylesheetImpl.FONTS$2, 0, (short)1);
    }
    
    public CTFonts addNewFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFonts)this.get_store().add_element_user(CTStylesheetImpl.FONTS$2);
        }
    }
    
    public void unsetFonts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.FONTS$2, 0);
        }
    }
    
    public CTFills getFills() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFills ctFills = (CTFills)this.get_store().find_element_user(CTStylesheetImpl.FILLS$4, 0);
            if (ctFills == null) {
                return null;
            }
            return ctFills;
        }
    }
    
    public boolean isSetFills() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.FILLS$4) != 0;
        }
    }
    
    public void setFills(final CTFills ctFills) {
        this.generatedSetterHelperImpl((XmlObject)ctFills, CTStylesheetImpl.FILLS$4, 0, (short)1);
    }
    
    public CTFills addNewFills() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFills)this.get_store().add_element_user(CTStylesheetImpl.FILLS$4);
        }
    }
    
    public void unsetFills() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.FILLS$4, 0);
        }
    }
    
    public CTBorders getBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorders ctBorders = (CTBorders)this.get_store().find_element_user(CTStylesheetImpl.BORDERS$6, 0);
            if (ctBorders == null) {
                return null;
            }
            return ctBorders;
        }
    }
    
    public boolean isSetBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.BORDERS$6) != 0;
        }
    }
    
    public void setBorders(final CTBorders ctBorders) {
        this.generatedSetterHelperImpl((XmlObject)ctBorders, CTStylesheetImpl.BORDERS$6, 0, (short)1);
    }
    
    public CTBorders addNewBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorders)this.get_store().add_element_user(CTStylesheetImpl.BORDERS$6);
        }
    }
    
    public void unsetBorders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.BORDERS$6, 0);
        }
    }
    
    public CTCellStyleXfs getCellStyleXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellStyleXfs ctCellStyleXfs = (CTCellStyleXfs)this.get_store().find_element_user(CTStylesheetImpl.CELLSTYLEXFS$8, 0);
            if (ctCellStyleXfs == null) {
                return null;
            }
            return ctCellStyleXfs;
        }
    }
    
    public boolean isSetCellStyleXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.CELLSTYLEXFS$8) != 0;
        }
    }
    
    public void setCellStyleXfs(final CTCellStyleXfs ctCellStyleXfs) {
        this.generatedSetterHelperImpl((XmlObject)ctCellStyleXfs, CTStylesheetImpl.CELLSTYLEXFS$8, 0, (short)1);
    }
    
    public CTCellStyleXfs addNewCellStyleXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellStyleXfs)this.get_store().add_element_user(CTStylesheetImpl.CELLSTYLEXFS$8);
        }
    }
    
    public void unsetCellStyleXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.CELLSTYLEXFS$8, 0);
        }
    }
    
    public CTCellXfs getCellXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellXfs ctCellXfs = (CTCellXfs)this.get_store().find_element_user(CTStylesheetImpl.CELLXFS$10, 0);
            if (ctCellXfs == null) {
                return null;
            }
            return ctCellXfs;
        }
    }
    
    public boolean isSetCellXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.CELLXFS$10) != 0;
        }
    }
    
    public void setCellXfs(final CTCellXfs ctCellXfs) {
        this.generatedSetterHelperImpl((XmlObject)ctCellXfs, CTStylesheetImpl.CELLXFS$10, 0, (short)1);
    }
    
    public CTCellXfs addNewCellXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellXfs)this.get_store().add_element_user(CTStylesheetImpl.CELLXFS$10);
        }
    }
    
    public void unsetCellXfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.CELLXFS$10, 0);
        }
    }
    
    public CTCellStyles getCellStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellStyles ctCellStyles = (CTCellStyles)this.get_store().find_element_user(CTStylesheetImpl.CELLSTYLES$12, 0);
            if (ctCellStyles == null) {
                return null;
            }
            return ctCellStyles;
        }
    }
    
    public boolean isSetCellStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.CELLSTYLES$12) != 0;
        }
    }
    
    public void setCellStyles(final CTCellStyles ctCellStyles) {
        this.generatedSetterHelperImpl((XmlObject)ctCellStyles, CTStylesheetImpl.CELLSTYLES$12, 0, (short)1);
    }
    
    public CTCellStyles addNewCellStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellStyles)this.get_store().add_element_user(CTStylesheetImpl.CELLSTYLES$12);
        }
    }
    
    public void unsetCellStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.CELLSTYLES$12, 0);
        }
    }
    
    public CTDxfs getDxfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDxfs ctDxfs = (CTDxfs)this.get_store().find_element_user(CTStylesheetImpl.DXFS$14, 0);
            if (ctDxfs == null) {
                return null;
            }
            return ctDxfs;
        }
    }
    
    public boolean isSetDxfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.DXFS$14) != 0;
        }
    }
    
    public void setDxfs(final CTDxfs ctDxfs) {
        this.generatedSetterHelperImpl((XmlObject)ctDxfs, CTStylesheetImpl.DXFS$14, 0, (short)1);
    }
    
    public CTDxfs addNewDxfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDxfs)this.get_store().add_element_user(CTStylesheetImpl.DXFS$14);
        }
    }
    
    public void unsetDxfs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.DXFS$14, 0);
        }
    }
    
    public CTTableStyles getTableStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyles ctTableStyles = (CTTableStyles)this.get_store().find_element_user(CTStylesheetImpl.TABLESTYLES$16, 0);
            if (ctTableStyles == null) {
                return null;
            }
            return ctTableStyles;
        }
    }
    
    public boolean isSetTableStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.TABLESTYLES$16) != 0;
        }
    }
    
    public void setTableStyles(final CTTableStyles ctTableStyles) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyles, CTStylesheetImpl.TABLESTYLES$16, 0, (short)1);
    }
    
    public CTTableStyles addNewTableStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyles)this.get_store().add_element_user(CTStylesheetImpl.TABLESTYLES$16);
        }
    }
    
    public void unsetTableStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.TABLESTYLES$16, 0);
        }
    }
    
    public CTColors getColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColors ctColors = (CTColors)this.get_store().find_element_user(CTStylesheetImpl.COLORS$18, 0);
            if (ctColors == null) {
                return null;
            }
            return ctColors;
        }
    }
    
    public boolean isSetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.COLORS$18) != 0;
        }
    }
    
    public void setColors(final CTColors ctColors) {
        this.generatedSetterHelperImpl((XmlObject)ctColors, CTStylesheetImpl.COLORS$18, 0, (short)1);
    }
    
    public CTColors addNewColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColors)this.get_store().add_element_user(CTStylesheetImpl.COLORS$18);
        }
    }
    
    public void unsetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.COLORS$18, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTStylesheetImpl.EXTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesheetImpl.EXTLST$20) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStylesheetImpl.EXTLST$20, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTStylesheetImpl.EXTLST$20);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesheetImpl.EXTLST$20, 0);
        }
    }
    
    static {
        NUMFMTS$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "numFmts");
        FONTS$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fonts");
        FILLS$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "fills");
        BORDERS$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "borders");
        CELLSTYLEXFS$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cellStyleXfs");
        CELLXFS$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cellXfs");
        CELLSTYLES$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cellStyles");
        DXFS$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dxfs");
        TABLESTYLES$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableStyles");
        COLORS$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colors");
        EXTLST$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
