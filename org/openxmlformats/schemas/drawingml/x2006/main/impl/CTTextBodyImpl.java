package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextBodyImpl extends XmlComplexContentImpl implements CTTextBody
{
    private static final long serialVersionUID = 1L;
    private static final QName BODYPR$0;
    private static final QName LSTSTYLE$2;
    private static final QName P$4;
    
    public CTTextBodyImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextBodyProperties getBodyPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBodyProperties ctTextBodyProperties = (CTTextBodyProperties)this.get_store().find_element_user(CTTextBodyImpl.BODYPR$0, 0);
            if (ctTextBodyProperties == null) {
                return null;
            }
            return ctTextBodyProperties;
        }
    }
    
    public void setBodyPr(final CTTextBodyProperties ctTextBodyProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBodyProperties, CTTextBodyImpl.BODYPR$0, 0, (short)1);
    }
    
    public CTTextBodyProperties addNewBodyPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBodyProperties)this.get_store().add_element_user(CTTextBodyImpl.BODYPR$0);
        }
    }
    
    public CTTextListStyle getLstStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextListStyle ctTextListStyle = (CTTextListStyle)this.get_store().find_element_user(CTTextBodyImpl.LSTSTYLE$2, 0);
            if (ctTextListStyle == null) {
                return null;
            }
            return ctTextListStyle;
        }
    }
    
    public boolean isSetLstStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyImpl.LSTSTYLE$2) != 0;
        }
    }
    
    public void setLstStyle(final CTTextListStyle ctTextListStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTextListStyle, CTTextBodyImpl.LSTSTYLE$2, 0, (short)1);
    }
    
    public CTTextListStyle addNewLstStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextListStyle)this.get_store().add_element_user(CTTextBodyImpl.LSTSTYLE$2);
        }
    }
    
    public void unsetLstStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyImpl.LSTSTYLE$2, 0);
        }
    }
    
    public List<CTTextParagraph> getPList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PList extends AbstractList<CTTextParagraph>
            {
                @Override
                public CTTextParagraph get(final int n) {
                    return CTTextBodyImpl.this.getPArray(n);
                }
                
                @Override
                public CTTextParagraph set(final int n, final CTTextParagraph ctTextParagraph) {
                    final CTTextParagraph pArray = CTTextBodyImpl.this.getPArray(n);
                    CTTextBodyImpl.this.setPArray(n, ctTextParagraph);
                    return pArray;
                }
                
                @Override
                public void add(final int n, final CTTextParagraph ctTextParagraph) {
                    CTTextBodyImpl.this.insertNewP(n).set((XmlObject)ctTextParagraph);
                }
                
                @Override
                public CTTextParagraph remove(final int n) {
                    final CTTextParagraph pArray = CTTextBodyImpl.this.getPArray(n);
                    CTTextBodyImpl.this.removeP(n);
                    return pArray;
                }
                
                @Override
                public int size() {
                    return CTTextBodyImpl.this.sizeOfPArray();
                }
            }
            return new PList();
        }
    }
    
    @Deprecated
    public CTTextParagraph[] getPArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTextBodyImpl.P$4, (List)list);
            final CTTextParagraph[] array = new CTTextParagraph[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextParagraph getPArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraph ctTextParagraph = (CTTextParagraph)this.get_store().find_element_user(CTTextBodyImpl.P$4, n);
            if (ctTextParagraph == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextParagraph;
        }
    }
    
    public int sizeOfPArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyImpl.P$4);
        }
    }
    
    public void setPArray(final CTTextParagraph[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTextBodyImpl.P$4);
    }
    
    public void setPArray(final int n, final CTTextParagraph ctTextParagraph) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraph, CTTextBodyImpl.P$4, n, (short)2);
    }
    
    public CTTextParagraph insertNewP(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraph)this.get_store().insert_element_user(CTTextBodyImpl.P$4, n);
        }
    }
    
    public CTTextParagraph addNewP() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraph)this.get_store().add_element_user(CTTextBodyImpl.P$4);
        }
    }
    
    public void removeP(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyImpl.P$4, n);
        }
    }
    
    static {
        BODYPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bodyPr");
        LSTSTYLE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lstStyle");
        P$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "p");
    }
}
