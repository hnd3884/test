package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGraphicalObjectFrameImpl extends XmlComplexContentImpl implements CTGraphicalObjectFrame
{
    private static final long serialVersionUID = 1L;
    private static final QName NVGRAPHICFRAMEPR$0;
    private static final QName XFRM$2;
    private static final QName GRAPHIC$4;
    private static final QName MACRO$6;
    private static final QName FPUBLISHED$8;
    
    public CTGraphicalObjectFrameImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGraphicalObjectFrameNonVisual getNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectFrameNonVisual ctGraphicalObjectFrameNonVisual = (CTGraphicalObjectFrameNonVisual)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.NVGRAPHICFRAMEPR$0, 0);
            if (ctGraphicalObjectFrameNonVisual == null) {
                return null;
            }
            return ctGraphicalObjectFrameNonVisual;
        }
    }
    
    public void setNvGraphicFramePr(final CTGraphicalObjectFrameNonVisual ctGraphicalObjectFrameNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectFrameNonVisual, CTGraphicalObjectFrameImpl.NVGRAPHICFRAMEPR$0, 0, (short)1);
    }
    
    public CTGraphicalObjectFrameNonVisual addNewNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrameNonVisual)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.NVGRAPHICFRAMEPR$0);
        }
    }
    
    public CTTransform2D getXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTransform2D ctTransform2D = (CTTransform2D)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.XFRM$2, 0);
            if (ctTransform2D == null) {
                return null;
            }
            return ctTransform2D;
        }
    }
    
    public void setXfrm(final CTTransform2D ctTransform2D) {
        this.generatedSetterHelperImpl((XmlObject)ctTransform2D, CTGraphicalObjectFrameImpl.XFRM$2, 0, (short)1);
    }
    
    public CTTransform2D addNewXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTransform2D)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.XFRM$2);
        }
    }
    
    public CTGraphicalObject getGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObject ctGraphicalObject = (CTGraphicalObject)this.get_store().find_element_user(CTGraphicalObjectFrameImpl.GRAPHIC$4, 0);
            if (ctGraphicalObject == null) {
                return null;
            }
            return ctGraphicalObject;
        }
    }
    
    public void setGraphic(final CTGraphicalObject ctGraphicalObject) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObject, CTGraphicalObjectFrameImpl.GRAPHIC$4, 0, (short)1);
    }
    
    public CTGraphicalObject addNewGraphic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObject)this.get_store().add_element_user(CTGraphicalObjectFrameImpl.GRAPHIC$4);
        }
    }
    
    public String getMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6);
        }
    }
    
    public boolean isSetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6) != null;
        }
    }
    
    public void setMacro(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMacro(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGraphicalObjectFrameImpl.MACRO$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameImpl.MACRO$6);
        }
    }
    
    public boolean getFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8) != null;
        }
    }
    
    public void setFPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGraphicalObjectFrameImpl.FPUBLISHED$8);
        }
    }
    
    static {
        NVGRAPHICFRAMEPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "nvGraphicFramePr");
        XFRM$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "xfrm");
        GRAPHIC$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "graphic");
        MACRO$6 = new QName("", "macro");
        FPUBLISHED$8 = new QName("", "fPublished");
    }
}
