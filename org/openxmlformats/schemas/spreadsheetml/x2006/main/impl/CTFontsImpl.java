package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFonts;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontsImpl extends XmlComplexContentImpl implements CTFonts
{
    private static final long serialVersionUID = 1L;
    private static final QName FONT$0;
    private static final QName COUNT$2;
    
    public CTFontsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFont> getFontList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FontList extends AbstractList<CTFont>
            {
                @Override
                public CTFont get(final int n) {
                    return CTFontsImpl.this.getFontArray(n);
                }
                
                @Override
                public CTFont set(final int n, final CTFont ctFont) {
                    final CTFont fontArray = CTFontsImpl.this.getFontArray(n);
                    CTFontsImpl.this.setFontArray(n, ctFont);
                    return fontArray;
                }
                
                @Override
                public void add(final int n, final CTFont ctFont) {
                    CTFontsImpl.this.insertNewFont(n).set((XmlObject)ctFont);
                }
                
                @Override
                public CTFont remove(final int n) {
                    final CTFont fontArray = CTFontsImpl.this.getFontArray(n);
                    CTFontsImpl.this.removeFont(n);
                    return fontArray;
                }
                
                @Override
                public int size() {
                    return CTFontsImpl.this.sizeOfFontArray();
                }
            }
            return new FontList();
        }
    }
    
    @Deprecated
    public CTFont[] getFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontsImpl.FONT$0, (List)list);
            final CTFont[] array = new CTFont[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFont getFontArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFont ctFont = (CTFont)this.get_store().find_element_user(CTFontsImpl.FONT$0, n);
            if (ctFont == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFont;
        }
    }
    
    public int sizeOfFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontsImpl.FONT$0);
        }
    }
    
    public void setFontArray(final CTFont[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontsImpl.FONT$0);
    }
    
    public void setFontArray(final int n, final CTFont ctFont) {
        this.generatedSetterHelperImpl((XmlObject)ctFont, CTFontsImpl.FONT$0, n, (short)2);
    }
    
    public CTFont insertNewFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFont)this.get_store().insert_element_user(CTFontsImpl.FONT$0, n);
        }
    }
    
    public CTFont addNewFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFont)this.get_store().add_element_user(CTFontsImpl.FONT$0);
        }
    }
    
    public void removeFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontsImpl.FONT$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTFontsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFontsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFontsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFontsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTFontsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTFontsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFontsImpl.COUNT$2);
        }
    }
    
    static {
        FONT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "font");
        COUNT$2 = new QName("", "count");
    }
}
