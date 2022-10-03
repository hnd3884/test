package com.microsoft.schemas.office.visio.x2012.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class StyleSheetsTypeImpl extends XmlComplexContentImpl implements StyleSheetsType
{
    private static final long serialVersionUID = 1L;
    private static final QName STYLESHEET$0;
    
    public StyleSheetsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<StyleSheetType> getStyleSheetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StyleSheetList extends AbstractList<StyleSheetType>
            {
                @Override
                public StyleSheetType get(final int n) {
                    return StyleSheetsTypeImpl.this.getStyleSheetArray(n);
                }
                
                @Override
                public StyleSheetType set(final int n, final StyleSheetType styleSheetType) {
                    final StyleSheetType styleSheetArray = StyleSheetsTypeImpl.this.getStyleSheetArray(n);
                    StyleSheetsTypeImpl.this.setStyleSheetArray(n, styleSheetType);
                    return styleSheetArray;
                }
                
                @Override
                public void add(final int n, final StyleSheetType styleSheetType) {
                    StyleSheetsTypeImpl.this.insertNewStyleSheet(n).set((XmlObject)styleSheetType);
                }
                
                @Override
                public StyleSheetType remove(final int n) {
                    final StyleSheetType styleSheetArray = StyleSheetsTypeImpl.this.getStyleSheetArray(n);
                    StyleSheetsTypeImpl.this.removeStyleSheet(n);
                    return styleSheetArray;
                }
                
                @Override
                public int size() {
                    return StyleSheetsTypeImpl.this.sizeOfStyleSheetArray();
                }
            }
            return new StyleSheetList();
        }
    }
    
    @Deprecated
    public StyleSheetType[] getStyleSheetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(StyleSheetsTypeImpl.STYLESHEET$0, (List)list);
            final StyleSheetType[] array = new StyleSheetType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public StyleSheetType getStyleSheetArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final StyleSheetType styleSheetType = (StyleSheetType)this.get_store().find_element_user(StyleSheetsTypeImpl.STYLESHEET$0, n);
            if (styleSheetType == null) {
                throw new IndexOutOfBoundsException();
            }
            return styleSheetType;
        }
    }
    
    public int sizeOfStyleSheetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(StyleSheetsTypeImpl.STYLESHEET$0);
        }
    }
    
    public void setStyleSheetArray(final StyleSheetType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, StyleSheetsTypeImpl.STYLESHEET$0);
    }
    
    public void setStyleSheetArray(final int n, final StyleSheetType styleSheetType) {
        this.generatedSetterHelperImpl((XmlObject)styleSheetType, StyleSheetsTypeImpl.STYLESHEET$0, n, (short)2);
    }
    
    public StyleSheetType insertNewStyleSheet(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (StyleSheetType)this.get_store().insert_element_user(StyleSheetsTypeImpl.STYLESHEET$0, n);
        }
    }
    
    public StyleSheetType addNewStyleSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (StyleSheetType)this.get_store().add_element_user(StyleSheetsTypeImpl.STYLESHEET$0);
        }
    }
    
    public void removeStyleSheet(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(StyleSheetsTypeImpl.STYLESHEET$0, n);
        }
    }
    
    static {
        STYLESHEET$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "StyleSheet");
    }
}
