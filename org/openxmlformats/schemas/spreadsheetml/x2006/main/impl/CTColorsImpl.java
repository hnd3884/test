package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMRUColors;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIndexedColors;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColors;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorsImpl extends XmlComplexContentImpl implements CTColors
{
    private static final long serialVersionUID = 1L;
    private static final QName INDEXEDCOLORS$0;
    private static final QName MRUCOLORS$2;
    
    public CTColorsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTIndexedColors getIndexedColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIndexedColors ctIndexedColors = (CTIndexedColors)this.get_store().find_element_user(CTColorsImpl.INDEXEDCOLORS$0, 0);
            if (ctIndexedColors == null) {
                return null;
            }
            return ctIndexedColors;
        }
    }
    
    public boolean isSetIndexedColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorsImpl.INDEXEDCOLORS$0) != 0;
        }
    }
    
    public void setIndexedColors(final CTIndexedColors ctIndexedColors) {
        this.generatedSetterHelperImpl((XmlObject)ctIndexedColors, CTColorsImpl.INDEXEDCOLORS$0, 0, (short)1);
    }
    
    public CTIndexedColors addNewIndexedColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIndexedColors)this.get_store().add_element_user(CTColorsImpl.INDEXEDCOLORS$0);
        }
    }
    
    public void unsetIndexedColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorsImpl.INDEXEDCOLORS$0, 0);
        }
    }
    
    public CTMRUColors getMruColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMRUColors ctmruColors = (CTMRUColors)this.get_store().find_element_user(CTColorsImpl.MRUCOLORS$2, 0);
            if (ctmruColors == null) {
                return null;
            }
            return ctmruColors;
        }
    }
    
    public boolean isSetMruColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorsImpl.MRUCOLORS$2) != 0;
        }
    }
    
    public void setMruColors(final CTMRUColors ctmruColors) {
        this.generatedSetterHelperImpl((XmlObject)ctmruColors, CTColorsImpl.MRUCOLORS$2, 0, (short)1);
    }
    
    public CTMRUColors addNewMruColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMRUColors)this.get_store().add_element_user(CTColorsImpl.MRUCOLORS$2);
        }
    }
    
    public void unsetMruColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorsImpl.MRUCOLORS$2, 0);
        }
    }
    
    static {
        INDEXEDCOLORS$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "indexedColors");
        MRUCOLORS$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "mruColors");
    }
}
