package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGradientFill;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFillImpl extends XmlComplexContentImpl implements CTFill
{
    private static final long serialVersionUID = 1L;
    private static final QName PATTERNFILL$0;
    private static final QName GRADIENTFILL$2;
    
    public CTFillImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPatternFill getPatternFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFill ctPatternFill = (CTPatternFill)this.get_store().find_element_user(CTFillImpl.PATTERNFILL$0, 0);
            if (ctPatternFill == null) {
                return null;
            }
            return ctPatternFill;
        }
    }
    
    public boolean isSetPatternFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillImpl.PATTERNFILL$0) != 0;
        }
    }
    
    public void setPatternFill(final CTPatternFill ctPatternFill) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFill, CTFillImpl.PATTERNFILL$0, 0, (short)1);
    }
    
    public CTPatternFill addNewPatternFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFill)this.get_store().add_element_user(CTFillImpl.PATTERNFILL$0);
        }
    }
    
    public void unsetPatternFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillImpl.PATTERNFILL$0, 0);
        }
    }
    
    public CTGradientFill getGradientFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFill ctGradientFill = (CTGradientFill)this.get_store().find_element_user(CTFillImpl.GRADIENTFILL$2, 0);
            if (ctGradientFill == null) {
                return null;
            }
            return ctGradientFill;
        }
    }
    
    public boolean isSetGradientFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillImpl.GRADIENTFILL$2) != 0;
        }
    }
    
    public void setGradientFill(final CTGradientFill ctGradientFill) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFill, CTFillImpl.GRADIENTFILL$2, 0, (short)1);
    }
    
    public CTGradientFill addNewGradientFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFill)this.get_store().add_element_user(CTFillImpl.GRADIENTFILL$2);
        }
    }
    
    public void unsetGradientFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillImpl.GRADIENTFILL$2, 0);
        }
    }
    
    static {
        PATTERNFILL$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "patternFill");
        GRADIENTFILL$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "gradientFill");
    }
}
