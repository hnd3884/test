package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSupplementalFont;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontCollectionImpl extends XmlComplexContentImpl implements CTFontCollection
{
    private static final long serialVersionUID = 1L;
    private static final QName LATIN$0;
    private static final QName EA$2;
    private static final QName CS$4;
    private static final QName FONT$6;
    private static final QName EXTLST$8;
    
    public CTFontCollectionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextFont getLatin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTFontCollectionImpl.LATIN$0, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public void setLatin(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTFontCollectionImpl.LATIN$0, 0, (short)1);
    }
    
    public CTTextFont addNewLatin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTFontCollectionImpl.LATIN$0);
        }
    }
    
    public CTTextFont getEa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTFontCollectionImpl.EA$2, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public void setEa(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTFontCollectionImpl.EA$2, 0, (short)1);
    }
    
    public CTTextFont addNewEa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTFontCollectionImpl.EA$2);
        }
    }
    
    public CTTextFont getCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextFont ctTextFont = (CTTextFont)this.get_store().find_element_user(CTFontCollectionImpl.CS$4, 0);
            if (ctTextFont == null) {
                return null;
            }
            return ctTextFont;
        }
    }
    
    public void setCs(final CTTextFont ctTextFont) {
        this.generatedSetterHelperImpl((XmlObject)ctTextFont, CTFontCollectionImpl.CS$4, 0, (short)1);
    }
    
    public CTTextFont addNewCs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextFont)this.get_store().add_element_user(CTFontCollectionImpl.CS$4);
        }
    }
    
    public List<CTSupplementalFont> getFontList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FontList extends AbstractList<CTSupplementalFont>
            {
                @Override
                public CTSupplementalFont get(final int n) {
                    return CTFontCollectionImpl.this.getFontArray(n);
                }
                
                @Override
                public CTSupplementalFont set(final int n, final CTSupplementalFont ctSupplementalFont) {
                    final CTSupplementalFont fontArray = CTFontCollectionImpl.this.getFontArray(n);
                    CTFontCollectionImpl.this.setFontArray(n, ctSupplementalFont);
                    return fontArray;
                }
                
                @Override
                public void add(final int n, final CTSupplementalFont ctSupplementalFont) {
                    CTFontCollectionImpl.this.insertNewFont(n).set((XmlObject)ctSupplementalFont);
                }
                
                @Override
                public CTSupplementalFont remove(final int n) {
                    final CTSupplementalFont fontArray = CTFontCollectionImpl.this.getFontArray(n);
                    CTFontCollectionImpl.this.removeFont(n);
                    return fontArray;
                }
                
                @Override
                public int size() {
                    return CTFontCollectionImpl.this.sizeOfFontArray();
                }
            }
            return new FontList();
        }
    }
    
    @Deprecated
    public CTSupplementalFont[] getFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontCollectionImpl.FONT$6, (List)list);
            final CTSupplementalFont[] array = new CTSupplementalFont[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSupplementalFont getFontArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSupplementalFont ctSupplementalFont = (CTSupplementalFont)this.get_store().find_element_user(CTFontCollectionImpl.FONT$6, n);
            if (ctSupplementalFont == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSupplementalFont;
        }
    }
    
    public int sizeOfFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontCollectionImpl.FONT$6);
        }
    }
    
    public void setFontArray(final CTSupplementalFont[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontCollectionImpl.FONT$6);
    }
    
    public void setFontArray(final int n, final CTSupplementalFont ctSupplementalFont) {
        this.generatedSetterHelperImpl((XmlObject)ctSupplementalFont, CTFontCollectionImpl.FONT$6, n, (short)2);
    }
    
    public CTSupplementalFont insertNewFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSupplementalFont)this.get_store().insert_element_user(CTFontCollectionImpl.FONT$6, n);
        }
    }
    
    public CTSupplementalFont addNewFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSupplementalFont)this.get_store().add_element_user(CTFontCollectionImpl.FONT$6);
        }
    }
    
    public void removeFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontCollectionImpl.FONT$6, n);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTFontCollectionImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontCollectionImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTFontCollectionImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTFontCollectionImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontCollectionImpl.EXTLST$8, 0);
        }
    }
    
    static {
        LATIN$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "latin");
        EA$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ea");
        CS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cs");
        FONT$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "font");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
    }
}
