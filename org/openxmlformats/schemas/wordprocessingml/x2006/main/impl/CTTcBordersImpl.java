package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTcBordersImpl extends XmlComplexContentImpl implements CTTcBorders
{
    private static final long serialVersionUID = 1L;
    private static final QName TOP$0;
    private static final QName LEFT$2;
    private static final QName BOTTOM$4;
    private static final QName RIGHT$6;
    private static final QName INSIDEH$8;
    private static final QName INSIDEV$10;
    private static final QName TL2BR$12;
    private static final QName TR2BL$14;
    
    public CTTcBordersImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBorder getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.TOP$0, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.TOP$0) != 0;
        }
    }
    
    public void setTop(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.TOP$0, 0, (short)1);
    }
    
    public CTBorder addNewTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.TOP$0);
        }
    }
    
    public void unsetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.TOP$0, 0);
        }
    }
    
    public CTBorder getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.LEFT$2, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.LEFT$2) != 0;
        }
    }
    
    public void setLeft(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.LEFT$2, 0, (short)1);
    }
    
    public CTBorder addNewLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.LEFT$2);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.LEFT$2, 0);
        }
    }
    
    public CTBorder getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.BOTTOM$4, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.BOTTOM$4) != 0;
        }
    }
    
    public void setBottom(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.BOTTOM$4, 0, (short)1);
    }
    
    public CTBorder addNewBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.BOTTOM$4);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.BOTTOM$4, 0);
        }
    }
    
    public CTBorder getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.RIGHT$6, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.RIGHT$6) != 0;
        }
    }
    
    public void setRight(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.RIGHT$6, 0, (short)1);
    }
    
    public CTBorder addNewRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.RIGHT$6);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.RIGHT$6, 0);
        }
    }
    
    public CTBorder getInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.INSIDEH$8, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.INSIDEH$8) != 0;
        }
    }
    
    public void setInsideH(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.INSIDEH$8, 0, (short)1);
    }
    
    public CTBorder addNewInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.INSIDEH$8);
        }
    }
    
    public void unsetInsideH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.INSIDEH$8, 0);
        }
    }
    
    public CTBorder getInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.INSIDEV$10, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.INSIDEV$10) != 0;
        }
    }
    
    public void setInsideV(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.INSIDEV$10, 0, (short)1);
    }
    
    public CTBorder addNewInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.INSIDEV$10);
        }
    }
    
    public void unsetInsideV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.INSIDEV$10, 0);
        }
    }
    
    public CTBorder getTl2Br() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.TL2BR$12, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetTl2Br() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.TL2BR$12) != 0;
        }
    }
    
    public void setTl2Br(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.TL2BR$12, 0, (short)1);
    }
    
    public CTBorder addNewTl2Br() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.TL2BR$12);
        }
    }
    
    public void unsetTl2Br() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.TL2BR$12, 0);
        }
    }
    
    public CTBorder getTr2Bl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTTcBordersImpl.TR2BL$14, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetTr2Bl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcBordersImpl.TR2BL$14) != 0;
        }
    }
    
    public void setTr2Bl(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTTcBordersImpl.TR2BL$14, 0, (short)1);
    }
    
    public CTBorder addNewTr2Bl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTTcBordersImpl.TR2BL$14);
        }
    }
    
    public void unsetTr2Bl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcBordersImpl.TR2BL$14, 0);
        }
    }
    
    static {
        TOP$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "top");
        LEFT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "left");
        BOTTOM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bottom");
        RIGHT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "right");
        INSIDEH$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "insideH");
        INSIDEV$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "insideV");
        TL2BR$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tl2br");
        TR2BL$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tr2bl");
    }
}
