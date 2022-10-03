package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSurfaceImpl extends XmlComplexContentImpl implements CTSurface
{
    private static final long serialVersionUID = 1L;
    private static final QName THICKNESS$0;
    private static final QName SPPR$2;
    private static final QName PICTUREOPTIONS$4;
    private static final QName EXTLST$6;
    
    public CTSurfaceImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getThickness() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTSurfaceImpl.THICKNESS$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public boolean isSetThickness() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceImpl.THICKNESS$0) != 0;
        }
    }
    
    public void setThickness(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTSurfaceImpl.THICKNESS$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewThickness() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTSurfaceImpl.THICKNESS$0);
        }
    }
    
    public void unsetThickness() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceImpl.THICKNESS$0, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTSurfaceImpl.SPPR$2, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceImpl.SPPR$2) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTSurfaceImpl.SPPR$2, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTSurfaceImpl.SPPR$2);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceImpl.SPPR$2, 0);
        }
    }
    
    public CTPictureOptions getPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPictureOptions ctPictureOptions = (CTPictureOptions)this.get_store().find_element_user(CTSurfaceImpl.PICTUREOPTIONS$4, 0);
            if (ctPictureOptions == null) {
                return null;
            }
            return ctPictureOptions;
        }
    }
    
    public boolean isSetPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceImpl.PICTUREOPTIONS$4) != 0;
        }
    }
    
    public void setPictureOptions(final CTPictureOptions ctPictureOptions) {
        this.generatedSetterHelperImpl((XmlObject)ctPictureOptions, CTSurfaceImpl.PICTUREOPTIONS$4, 0, (short)1);
    }
    
    public CTPictureOptions addNewPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPictureOptions)this.get_store().add_element_user(CTSurfaceImpl.PICTUREOPTIONS$4);
        }
    }
    
    public void unsetPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceImpl.PICTUREOPTIONS$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSurfaceImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSurfaceImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSurfaceImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceImpl.EXTLST$6, 0);
        }
    }
    
    static {
        THICKNESS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "thickness");
        SPPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        PICTUREOPTIONS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pictureOptions");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
