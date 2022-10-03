package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFillPropertiesImpl extends XmlComplexContentImpl implements CTFillProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName NOFILL$0;
    private static final QName SOLIDFILL$2;
    private static final QName GRADFILL$4;
    private static final QName BLIPFILL$6;
    private static final QName PATTFILL$8;
    private static final QName GRPFILL$10;
    
    public CTFillPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNoFillProperties getNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTFillPropertiesImpl.NOFILL$0, 0);
            if (ctNoFillProperties == null) {
                return null;
            }
            return ctNoFillProperties;
        }
    }
    
    public boolean isSetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillPropertiesImpl.NOFILL$0) != 0;
        }
    }
    
    public void setNoFill(final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTFillPropertiesImpl.NOFILL$0, 0, (short)1);
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTFillPropertiesImpl.NOFILL$0);
        }
    }
    
    public void unsetNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillPropertiesImpl.NOFILL$0, 0);
        }
    }
    
    public CTSolidColorFillProperties getSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTFillPropertiesImpl.SOLIDFILL$2, 0);
            if (ctSolidColorFillProperties == null) {
                return null;
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public boolean isSetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillPropertiesImpl.SOLIDFILL$2) != 0;
        }
    }
    
    public void setSolidFill(final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTFillPropertiesImpl.SOLIDFILL$2, 0, (short)1);
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTFillPropertiesImpl.SOLIDFILL$2);
        }
    }
    
    public void unsetSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillPropertiesImpl.SOLIDFILL$2, 0);
        }
    }
    
    public CTGradientFillProperties getGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTFillPropertiesImpl.GRADFILL$4, 0);
            if (ctGradientFillProperties == null) {
                return null;
            }
            return ctGradientFillProperties;
        }
    }
    
    public boolean isSetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillPropertiesImpl.GRADFILL$4) != 0;
        }
    }
    
    public void setGradFill(final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTFillPropertiesImpl.GRADFILL$4, 0, (short)1);
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTFillPropertiesImpl.GRADFILL$4);
        }
    }
    
    public void unsetGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillPropertiesImpl.GRADFILL$4, 0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTFillPropertiesImpl.BLIPFILL$6, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public boolean isSetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillPropertiesImpl.BLIPFILL$6) != 0;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTFillPropertiesImpl.BLIPFILL$6, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTFillPropertiesImpl.BLIPFILL$6);
        }
    }
    
    public void unsetBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillPropertiesImpl.BLIPFILL$6, 0);
        }
    }
    
    public CTPatternFillProperties getPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTFillPropertiesImpl.PATTFILL$8, 0);
            if (ctPatternFillProperties == null) {
                return null;
            }
            return ctPatternFillProperties;
        }
    }
    
    public boolean isSetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillPropertiesImpl.PATTFILL$8) != 0;
        }
    }
    
    public void setPattFill(final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTFillPropertiesImpl.PATTFILL$8, 0, (short)1);
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTFillPropertiesImpl.PATTFILL$8);
        }
    }
    
    public void unsetPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillPropertiesImpl.PATTFILL$8, 0);
        }
    }
    
    public CTGroupFillProperties getGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTFillPropertiesImpl.GRPFILL$10, 0);
            if (ctGroupFillProperties == null) {
                return null;
            }
            return ctGroupFillProperties;
        }
    }
    
    public boolean isSetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFillPropertiesImpl.GRPFILL$10) != 0;
        }
    }
    
    public void setGrpFill(final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTFillPropertiesImpl.GRPFILL$10, 0, (short)1);
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTFillPropertiesImpl.GRPFILL$10);
        }
    }
    
    public void unsetGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFillPropertiesImpl.GRPFILL$10, 0);
        }
    }
    
    static {
        NOFILL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
    }
}
