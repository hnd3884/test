package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPoint;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPercent;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextSpacingImpl extends XmlComplexContentImpl implements CTTextSpacing
{
    private static final long serialVersionUID = 1L;
    private static final QName SPCPCT$0;
    private static final QName SPCPTS$2;
    
    public CTTextSpacingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextSpacingPercent getSpcPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextSpacingPercent ctTextSpacingPercent = (CTTextSpacingPercent)this.get_store().find_element_user(CTTextSpacingImpl.SPCPCT$0, 0);
            if (ctTextSpacingPercent == null) {
                return null;
            }
            return ctTextSpacingPercent;
        }
    }
    
    public boolean isSetSpcPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextSpacingImpl.SPCPCT$0) != 0;
        }
    }
    
    public void setSpcPct(final CTTextSpacingPercent ctTextSpacingPercent) {
        this.generatedSetterHelperImpl((XmlObject)ctTextSpacingPercent, CTTextSpacingImpl.SPCPCT$0, 0, (short)1);
    }
    
    public CTTextSpacingPercent addNewSpcPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextSpacingPercent)this.get_store().add_element_user(CTTextSpacingImpl.SPCPCT$0);
        }
    }
    
    public void unsetSpcPct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextSpacingImpl.SPCPCT$0, 0);
        }
    }
    
    public CTTextSpacingPoint getSpcPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextSpacingPoint ctTextSpacingPoint = (CTTextSpacingPoint)this.get_store().find_element_user(CTTextSpacingImpl.SPCPTS$2, 0);
            if (ctTextSpacingPoint == null) {
                return null;
            }
            return ctTextSpacingPoint;
        }
    }
    
    public boolean isSetSpcPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextSpacingImpl.SPCPTS$2) != 0;
        }
    }
    
    public void setSpcPts(final CTTextSpacingPoint ctTextSpacingPoint) {
        this.generatedSetterHelperImpl((XmlObject)ctTextSpacingPoint, CTTextSpacingImpl.SPCPTS$2, 0, (short)1);
    }
    
    public CTTextSpacingPoint addNewSpcPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextSpacingPoint)this.get_store().add_element_user(CTTextSpacingImpl.SPCPTS$2);
        }
    }
    
    public void unsetSpcPts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextSpacingImpl.SPCPTS$2, 0);
        }
    }
    
    static {
        SPCPCT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "spcPct");
        SPCPTS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "spcPts");
    }
}
