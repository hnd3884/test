package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRgbColor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIndexedColors;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTIndexedColorsImpl extends XmlComplexContentImpl implements CTIndexedColors
{
    private static final long serialVersionUID = 1L;
    private static final QName RGBCOLOR$0;
    
    public CTIndexedColorsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTRgbColor> getRgbColorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RgbColorList extends AbstractList<CTRgbColor>
            {
                @Override
                public CTRgbColor get(final int n) {
                    return CTIndexedColorsImpl.this.getRgbColorArray(n);
                }
                
                @Override
                public CTRgbColor set(final int n, final CTRgbColor ctRgbColor) {
                    final CTRgbColor rgbColorArray = CTIndexedColorsImpl.this.getRgbColorArray(n);
                    CTIndexedColorsImpl.this.setRgbColorArray(n, ctRgbColor);
                    return rgbColorArray;
                }
                
                @Override
                public void add(final int n, final CTRgbColor ctRgbColor) {
                    CTIndexedColorsImpl.this.insertNewRgbColor(n).set((XmlObject)ctRgbColor);
                }
                
                @Override
                public CTRgbColor remove(final int n) {
                    final CTRgbColor rgbColorArray = CTIndexedColorsImpl.this.getRgbColorArray(n);
                    CTIndexedColorsImpl.this.removeRgbColor(n);
                    return rgbColorArray;
                }
                
                @Override
                public int size() {
                    return CTIndexedColorsImpl.this.sizeOfRgbColorArray();
                }
            }
            return new RgbColorList();
        }
    }
    
    @Deprecated
    public CTRgbColor[] getRgbColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTIndexedColorsImpl.RGBCOLOR$0, (List)list);
            final CTRgbColor[] array = new CTRgbColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRgbColor getRgbColorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRgbColor ctRgbColor = (CTRgbColor)this.get_store().find_element_user(CTIndexedColorsImpl.RGBCOLOR$0, n);
            if (ctRgbColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRgbColor;
        }
    }
    
    public int sizeOfRgbColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTIndexedColorsImpl.RGBCOLOR$0);
        }
    }
    
    public void setRgbColorArray(final CTRgbColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTIndexedColorsImpl.RGBCOLOR$0);
    }
    
    public void setRgbColorArray(final int n, final CTRgbColor ctRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctRgbColor, CTIndexedColorsImpl.RGBCOLOR$0, n, (short)2);
    }
    
    public CTRgbColor insertNewRgbColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRgbColor)this.get_store().insert_element_user(CTIndexedColorsImpl.RGBCOLOR$0, n);
        }
    }
    
    public CTRgbColor addNewRgbColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRgbColor)this.get_store().add_element_user(CTIndexedColorsImpl.RGBCOLOR$0);
        }
    }
    
    public void removeRgbColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTIndexedColorsImpl.RGBCOLOR$0, n);
        }
    }
    
    static {
        RGBCOLOR$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rgbColor");
    }
}
