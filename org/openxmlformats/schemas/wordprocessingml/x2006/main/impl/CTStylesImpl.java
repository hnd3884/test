package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLatentStyles;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStylesImpl extends XmlComplexContentImpl implements CTStyles
{
    private static final long serialVersionUID = 1L;
    private static final QName DOCDEFAULTS$0;
    private static final QName LATENTSTYLES$2;
    private static final QName STYLE$4;
    
    public CTStylesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDocDefaults getDocDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocDefaults ctDocDefaults = (CTDocDefaults)this.get_store().find_element_user(CTStylesImpl.DOCDEFAULTS$0, 0);
            if (ctDocDefaults == null) {
                return null;
            }
            return ctDocDefaults;
        }
    }
    
    public boolean isSetDocDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesImpl.DOCDEFAULTS$0) != 0;
        }
    }
    
    public void setDocDefaults(final CTDocDefaults ctDocDefaults) {
        this.generatedSetterHelperImpl((XmlObject)ctDocDefaults, CTStylesImpl.DOCDEFAULTS$0, 0, (short)1);
    }
    
    public CTDocDefaults addNewDocDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocDefaults)this.get_store().add_element_user(CTStylesImpl.DOCDEFAULTS$0);
        }
    }
    
    public void unsetDocDefaults() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesImpl.DOCDEFAULTS$0, 0);
        }
    }
    
    public CTLatentStyles getLatentStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLatentStyles ctLatentStyles = (CTLatentStyles)this.get_store().find_element_user(CTStylesImpl.LATENTSTYLES$2, 0);
            if (ctLatentStyles == null) {
                return null;
            }
            return ctLatentStyles;
        }
    }
    
    public boolean isSetLatentStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesImpl.LATENTSTYLES$2) != 0;
        }
    }
    
    public void setLatentStyles(final CTLatentStyles ctLatentStyles) {
        this.generatedSetterHelperImpl((XmlObject)ctLatentStyles, CTStylesImpl.LATENTSTYLES$2, 0, (short)1);
    }
    
    public CTLatentStyles addNewLatentStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLatentStyles)this.get_store().add_element_user(CTStylesImpl.LATENTSTYLES$2);
        }
    }
    
    public void unsetLatentStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesImpl.LATENTSTYLES$2, 0);
        }
    }
    
    public List<CTStyle> getStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StyleList extends AbstractList<CTStyle>
            {
                @Override
                public CTStyle get(final int n) {
                    return CTStylesImpl.this.getStyleArray(n);
                }
                
                @Override
                public CTStyle set(final int n, final CTStyle ctStyle) {
                    final CTStyle styleArray = CTStylesImpl.this.getStyleArray(n);
                    CTStylesImpl.this.setStyleArray(n, ctStyle);
                    return styleArray;
                }
                
                @Override
                public void add(final int n, final CTStyle ctStyle) {
                    CTStylesImpl.this.insertNewStyle(n).set((XmlObject)ctStyle);
                }
                
                @Override
                public CTStyle remove(final int n) {
                    final CTStyle styleArray = CTStylesImpl.this.getStyleArray(n);
                    CTStylesImpl.this.removeStyle(n);
                    return styleArray;
                }
                
                @Override
                public int size() {
                    return CTStylesImpl.this.sizeOfStyleArray();
                }
            }
            return new StyleList();
        }
    }
    
    @Deprecated
    public CTStyle[] getStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTStylesImpl.STYLE$4, (List)list);
            final CTStyle[] array = new CTStyle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTStyle getStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyle ctStyle = (CTStyle)this.get_store().find_element_user(CTStylesImpl.STYLE$4, n);
            if (ctStyle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctStyle;
        }
    }
    
    public int sizeOfStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStylesImpl.STYLE$4);
        }
    }
    
    public void setStyleArray(final CTStyle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTStylesImpl.STYLE$4);
    }
    
    public void setStyleArray(final int n, final CTStyle ctStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctStyle, CTStylesImpl.STYLE$4, n, (short)2);
    }
    
    public CTStyle insertNewStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyle)this.get_store().insert_element_user(CTStylesImpl.STYLE$4, n);
        }
    }
    
    public CTStyle addNewStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyle)this.get_store().add_element_user(CTStylesImpl.STYLE$4);
        }
    }
    
    public void removeStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStylesImpl.STYLE$4, n);
        }
    }
    
    static {
        DOCDEFAULTS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docDefaults");
        LATENTSTYLES$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "latentStyles");
        STYLE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "style");
    }
}
