package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerSize;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMarkerImpl extends XmlComplexContentImpl implements CTMarker
{
    private static final long serialVersionUID = 1L;
    private static final QName SYMBOL$0;
    private static final QName SIZE$2;
    private static final QName SPPR$4;
    private static final QName EXTLST$6;
    
    public CTMarkerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTMarkerStyle getSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkerStyle ctMarkerStyle = (CTMarkerStyle)this.get_store().find_element_user(CTMarkerImpl.SYMBOL$0, 0);
            if (ctMarkerStyle == null) {
                return null;
            }
            return ctMarkerStyle;
        }
    }
    
    public boolean isSetSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMarkerImpl.SYMBOL$0) != 0;
        }
    }
    
    public void setSymbol(final CTMarkerStyle ctMarkerStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkerStyle, CTMarkerImpl.SYMBOL$0, 0, (short)1);
    }
    
    public CTMarkerStyle addNewSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkerStyle)this.get_store().add_element_user(CTMarkerImpl.SYMBOL$0);
        }
    }
    
    public void unsetSymbol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMarkerImpl.SYMBOL$0, 0);
        }
    }
    
    public CTMarkerSize getSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarkerSize ctMarkerSize = (CTMarkerSize)this.get_store().find_element_user(CTMarkerImpl.SIZE$2, 0);
            if (ctMarkerSize == null) {
                return null;
            }
            return ctMarkerSize;
        }
    }
    
    public boolean isSetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMarkerImpl.SIZE$2) != 0;
        }
    }
    
    public void setSize(final CTMarkerSize ctMarkerSize) {
        this.generatedSetterHelperImpl((XmlObject)ctMarkerSize, CTMarkerImpl.SIZE$2, 0, (short)1);
    }
    
    public CTMarkerSize addNewSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarkerSize)this.get_store().add_element_user(CTMarkerImpl.SIZE$2);
        }
    }
    
    public void unsetSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMarkerImpl.SIZE$2, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTMarkerImpl.SPPR$4, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMarkerImpl.SPPR$4) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTMarkerImpl.SPPR$4, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTMarkerImpl.SPPR$4);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMarkerImpl.SPPR$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTMarkerImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMarkerImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTMarkerImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTMarkerImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMarkerImpl.EXTLST$6, 0);
        }
    }
    
    static {
        SYMBOL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "symbol");
        SIZE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "size");
        SPPR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
