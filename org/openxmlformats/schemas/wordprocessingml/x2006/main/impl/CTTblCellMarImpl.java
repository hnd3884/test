package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblCellMarImpl extends XmlComplexContentImpl implements CTTblCellMar
{
    private static final long serialVersionUID = 1L;
    private static final QName TOP$0;
    private static final QName LEFT$2;
    private static final QName BOTTOM$4;
    private static final QName RIGHT$6;
    
    public CTTblCellMarImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTblWidth getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblCellMarImpl.TOP$0, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblCellMarImpl.TOP$0) != 0;
        }
    }
    
    public void setTop(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblCellMarImpl.TOP$0, 0, (short)1);
    }
    
    public CTTblWidth addNewTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblCellMarImpl.TOP$0);
        }
    }
    
    public void unsetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblCellMarImpl.TOP$0, 0);
        }
    }
    
    public CTTblWidth getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblCellMarImpl.LEFT$2, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblCellMarImpl.LEFT$2) != 0;
        }
    }
    
    public void setLeft(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblCellMarImpl.LEFT$2, 0, (short)1);
    }
    
    public CTTblWidth addNewLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblCellMarImpl.LEFT$2);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblCellMarImpl.LEFT$2, 0);
        }
    }
    
    public CTTblWidth getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblCellMarImpl.BOTTOM$4, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblCellMarImpl.BOTTOM$4) != 0;
        }
    }
    
    public void setBottom(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblCellMarImpl.BOTTOM$4, 0, (short)1);
    }
    
    public CTTblWidth addNewBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblCellMarImpl.BOTTOM$4);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblCellMarImpl.BOTTOM$4, 0);
        }
    }
    
    public CTTblWidth getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTblCellMarImpl.RIGHT$6, 0);
            if (ctTblWidth == null) {
                return null;
            }
            return ctTblWidth;
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblCellMarImpl.RIGHT$6) != 0;
        }
    }
    
    public void setRight(final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTblCellMarImpl.RIGHT$6, 0, (short)1);
    }
    
    public CTTblWidth addNewRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTblCellMarImpl.RIGHT$6);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblCellMarImpl.RIGHT$6, 0);
        }
    }
    
    static {
        TOP$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "top");
        LEFT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "left");
        BOTTOM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bottom");
        RIGHT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "right");
    }
}
