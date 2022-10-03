package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBorderImpl extends XmlComplexContentImpl implements CTBorder
{
    private static final long serialVersionUID = 1L;
    private static final QName LEFT$0;
    private static final QName RIGHT$2;
    private static final QName TOP$4;
    private static final QName BOTTOM$6;
    private static final QName DIAGONAL$8;
    private static final QName VERTICAL$10;
    private static final QName HORIZONTAL$12;
    private static final QName DIAGONALUP$14;
    private static final QName DIAGONALDOWN$16;
    private static final QName OUTLINE$18;
    
    public CTBorderImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBorderPr getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.LEFT$0, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.LEFT$0) != 0;
        }
    }
    
    public void setLeft(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.LEFT$0, 0, (short)1);
    }
    
    public CTBorderPr addNewLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.LEFT$0);
        }
    }
    
    public void unsetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.LEFT$0, 0);
        }
    }
    
    public CTBorderPr getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.RIGHT$2, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.RIGHT$2) != 0;
        }
    }
    
    public void setRight(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.RIGHT$2, 0, (short)1);
    }
    
    public CTBorderPr addNewRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.RIGHT$2);
        }
    }
    
    public void unsetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.RIGHT$2, 0);
        }
    }
    
    public CTBorderPr getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.TOP$4, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.TOP$4) != 0;
        }
    }
    
    public void setTop(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.TOP$4, 0, (short)1);
    }
    
    public CTBorderPr addNewTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.TOP$4);
        }
    }
    
    public void unsetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.TOP$4, 0);
        }
    }
    
    public CTBorderPr getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.BOTTOM$6, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.BOTTOM$6) != 0;
        }
    }
    
    public void setBottom(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.BOTTOM$6, 0, (short)1);
    }
    
    public CTBorderPr addNewBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.BOTTOM$6);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.BOTTOM$6, 0);
        }
    }
    
    public CTBorderPr getDiagonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.DIAGONAL$8, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetDiagonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.DIAGONAL$8) != 0;
        }
    }
    
    public void setDiagonal(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.DIAGONAL$8, 0, (short)1);
    }
    
    public CTBorderPr addNewDiagonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.DIAGONAL$8);
        }
    }
    
    public void unsetDiagonal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.DIAGONAL$8, 0);
        }
    }
    
    public CTBorderPr getVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.VERTICAL$10, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.VERTICAL$10) != 0;
        }
    }
    
    public void setVertical(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.VERTICAL$10, 0, (short)1);
    }
    
    public CTBorderPr addNewVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.VERTICAL$10);
        }
    }
    
    public void unsetVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.VERTICAL$10, 0);
        }
    }
    
    public CTBorderPr getHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorderPr ctBorderPr = (CTBorderPr)this.get_store().find_element_user(CTBorderImpl.HORIZONTAL$12, 0);
            if (ctBorderPr == null) {
                return null;
            }
            return ctBorderPr;
        }
    }
    
    public boolean isSetHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBorderImpl.HORIZONTAL$12) != 0;
        }
    }
    
    public void setHorizontal(final CTBorderPr ctBorderPr) {
        this.generatedSetterHelperImpl((XmlObject)ctBorderPr, CTBorderImpl.HORIZONTAL$12, 0, (short)1);
    }
    
    public CTBorderPr addNewHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorderPr)this.get_store().add_element_user(CTBorderImpl.HORIZONTAL$12);
        }
    }
    
    public void unsetHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBorderImpl.HORIZONTAL$12, 0);
        }
    }
    
    public boolean getDiagonalUp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALUP$14);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDiagonalUp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALUP$14);
        }
    }
    
    public boolean isSetDiagonalUp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.DIAGONALUP$14) != null;
        }
    }
    
    public void setDiagonalUp(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALUP$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.DIAGONALUP$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDiagonalUp(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALUP$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBorderImpl.DIAGONALUP$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDiagonalUp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.DIAGONALUP$14);
        }
    }
    
    public boolean getDiagonalDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALDOWN$16);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDiagonalDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALDOWN$16);
        }
    }
    
    public boolean isSetDiagonalDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.DIAGONALDOWN$16) != null;
        }
    }
    
    public void setDiagonalDown(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALDOWN$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.DIAGONALDOWN$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDiagonalDown(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBorderImpl.DIAGONALDOWN$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBorderImpl.DIAGONALDOWN$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDiagonalDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.DIAGONALDOWN$16);
        }
    }
    
    public boolean getOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.OUTLINE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBorderImpl.OUTLINE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBorderImpl.OUTLINE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBorderImpl.OUTLINE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBorderImpl.OUTLINE$18) != null;
        }
    }
    
    public void setOutline(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBorderImpl.OUTLINE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBorderImpl.OUTLINE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetOutline(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBorderImpl.OUTLINE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBorderImpl.OUTLINE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBorderImpl.OUTLINE$18);
        }
    }
    
    static {
        LEFT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "left");
        RIGHT$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "right");
        TOP$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "top");
        BOTTOM$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "bottom");
        DIAGONAL$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "diagonal");
        VERTICAL$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "vertical");
        HORIZONTAL$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "horizontal");
        DIAGONALUP$14 = new QName("", "diagonalUp");
        DIAGONALDOWN$16 = new QName("", "diagonalDown");
        OUTLINE$18 = new QName("", "outline");
    }
}
