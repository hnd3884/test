package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTblBordersImpl extends XmlComplexContentImpl implements CTTblBorders
{
    private static final long serialVersionUID = 1L;
    private static final QName TOP$0;
    private static final QName LEFT$2;
    private static final QName BOTTOM$4;
    private static final QName RIGHT$6;
    private static final QName INSIDEH$8;
    private static final QName INSIDEV$10;
    
    public CTTblBordersImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBorder getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTblBordersImpl.TOP$0, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblBordersImpl.TOP$0) != 0;
        }
    }
    
    public void setTop(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTblBordersImpl.TOP$0, 0, (short)1);
    }
    
    public CTBorder addNewTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTblBordersImpl.TOP$0);
        }
    }
    
    public void unsetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblBordersImpl.TOP$0, 0);
        }
    }
    
    public CTBorder getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTblBordersImpl.LEFT$2, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblBordersImpl.LEFT$2) != 0;
        }
    }
    
    public void setLeft(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTblBordersImpl.LEFT$2, 0, (short)1);
    }
    
    public CTBorder addNewLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTblBordersImpl.LEFT$2);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblBordersImpl.LEFT$2, 0);
        }
    }
    
    public CTBorder getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTblBordersImpl.BOTTOM$4, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblBordersImpl.BOTTOM$4) != 0;
        }
    }
    
    public void setBottom(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTblBordersImpl.BOTTOM$4, 0, (short)1);
    }
    
    public CTBorder addNewBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTblBordersImpl.BOTTOM$4);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblBordersImpl.BOTTOM$4, 0);
        }
    }
    
    public CTBorder getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTblBordersImpl.RIGHT$6, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblBordersImpl.RIGHT$6) != 0;
        }
    }
    
    public void setRight(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTblBordersImpl.RIGHT$6, 0, (short)1);
    }
    
    public CTBorder addNewRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTblBordersImpl.RIGHT$6);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblBordersImpl.RIGHT$6, 0);
        }
    }
    
    public CTBorder getInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTblBordersImpl.INSIDEH$8, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblBordersImpl.INSIDEH$8) != 0;
        }
    }
    
    public void setInsideH(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTblBordersImpl.INSIDEH$8, 0, (short)1);
    }
    
    public CTBorder addNewInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTblBordersImpl.INSIDEH$8);
        }
    }
    
    public void unsetInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblBordersImpl.INSIDEH$8, 0);
        }
    }
    
    public CTBorder getInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTblBordersImpl.INSIDEV$10, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTblBordersImpl.INSIDEV$10) != 0;
        }
    }
    
    public void setInsideV(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTblBordersImpl.INSIDEV$10, 0, (short)1);
    }
    
    public CTBorder addNewInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTblBordersImpl.INSIDEV$10);
        }
    }
    
    public void unsetInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTblBordersImpl.INSIDEV$10, 0);
        }
    }
    
    static {
        TOP$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "top");
        LEFT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "left");
        BOTTOM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bottom");
        RIGHT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "right");
        INSIDEH$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "insideH");
        INSIDEV$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "insideV");
    }
}
