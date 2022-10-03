package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPBdrImpl extends XmlComplexContentImpl implements CTPBdr
{
    private static final long serialVersionUID = 1L;
    private static final QName TOP$0;
    private static final QName LEFT$2;
    private static final QName BOTTOM$4;
    private static final QName RIGHT$6;
    private static final QName BETWEEN$8;
    private static final QName BAR$10;
    
    public CTPBdrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBorder getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTPBdrImpl.TOP$0, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPBdrImpl.TOP$0) != 0;
        }
    }
    
    public void setTop(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTPBdrImpl.TOP$0, 0, (short)1);
    }
    
    public CTBorder addNewTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTPBdrImpl.TOP$0);
        }
    }
    
    public void unsetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPBdrImpl.TOP$0, 0);
        }
    }
    
    public CTBorder getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTPBdrImpl.LEFT$2, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPBdrImpl.LEFT$2) != 0;
        }
    }
    
    public void setLeft(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTPBdrImpl.LEFT$2, 0, (short)1);
    }
    
    public CTBorder addNewLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTPBdrImpl.LEFT$2);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPBdrImpl.LEFT$2, 0);
        }
    }
    
    public CTBorder getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTPBdrImpl.BOTTOM$4, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPBdrImpl.BOTTOM$4) != 0;
        }
    }
    
    public void setBottom(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTPBdrImpl.BOTTOM$4, 0, (short)1);
    }
    
    public CTBorder addNewBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTPBdrImpl.BOTTOM$4);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPBdrImpl.BOTTOM$4, 0);
        }
    }
    
    public CTBorder getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTPBdrImpl.RIGHT$6, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPBdrImpl.RIGHT$6) != 0;
        }
    }
    
    public void setRight(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTPBdrImpl.RIGHT$6, 0, (short)1);
    }
    
    public CTBorder addNewRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTPBdrImpl.RIGHT$6);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPBdrImpl.RIGHT$6, 0);
        }
    }
    
    public CTBorder getBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTPBdrImpl.BETWEEN$8, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPBdrImpl.BETWEEN$8) != 0;
        }
    }
    
    public void setBetween(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTPBdrImpl.BETWEEN$8, 0, (short)1);
    }
    
    public CTBorder addNewBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTPBdrImpl.BETWEEN$8);
        }
    }
    
    public void unsetBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPBdrImpl.BETWEEN$8, 0);
        }
    }
    
    public CTBorder getBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTPBdrImpl.BAR$10, 0);
            if (ctBorder == null) {
                return null;
            }
            return ctBorder;
        }
    }
    
    public boolean isSetBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPBdrImpl.BAR$10) != 0;
        }
    }
    
    public void setBar(final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTPBdrImpl.BAR$10, 0, (short)1);
    }
    
    public CTBorder addNewBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTPBdrImpl.BAR$10);
        }
    }
    
    public void unsetBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPBdrImpl.BAR$10, 0);
        }
    }
    
    static {
        TOP$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "top");
        LEFT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "left");
        BOTTOM$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bottom");
        RIGHT$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "right");
        BETWEEN$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "between");
        BAR$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bar");
    }
}
