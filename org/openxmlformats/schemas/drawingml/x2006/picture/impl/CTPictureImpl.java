package org.openxmlformats.schemas.drawingml.x2006.picture.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPictureImpl extends XmlComplexContentImpl implements CTPicture
{
    private static final long serialVersionUID = 1L;
    private static final QName NVPICPR$0;
    private static final QName BLIPFILL$2;
    private static final QName SPPR$4;
    
    public CTPictureImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPictureNonVisual getNvPicPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPictureNonVisual ctPictureNonVisual = (CTPictureNonVisual)this.get_store().find_element_user(CTPictureImpl.NVPICPR$0, 0);
            if (ctPictureNonVisual == null) {
                return null;
            }
            return ctPictureNonVisual;
        }
    }
    
    public void setNvPicPr(final CTPictureNonVisual ctPictureNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctPictureNonVisual, CTPictureImpl.NVPICPR$0, 0, (short)1);
    }
    
    public CTPictureNonVisual addNewNvPicPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPictureNonVisual)this.get_store().add_element_user(CTPictureImpl.NVPICPR$0);
        }
    }
    
    public CTBlipFillProperties getBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTPictureImpl.BLIPFILL$2, 0);
            if (ctBlipFillProperties == null) {
                return null;
            }
            return ctBlipFillProperties;
        }
    }
    
    public void setBlipFill(final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTPictureImpl.BLIPFILL$2, 0, (short)1);
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTPictureImpl.BLIPFILL$2);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTPictureImpl.SPPR$4, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTPictureImpl.SPPR$4, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTPictureImpl.SPPR$4);
        }
    }
    
    static {
        NVPICPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/picture", "nvPicPr");
        BLIPFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/picture", "blipFill");
        SPPR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/picture", "spPr");
    }
}
