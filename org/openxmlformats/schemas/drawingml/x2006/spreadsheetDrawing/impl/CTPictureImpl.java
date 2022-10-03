package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPictureNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPictureImpl extends XmlComplexContentImpl implements CTPicture
{
    private static final long serialVersionUID = 1L;
    private static final QName NVPICPR$0;
    private static final QName BLIPFILL$2;
    private static final QName SPPR$4;
    private static final QName STYLE$6;
    private static final QName MACRO$8;
    private static final QName FPUBLISHED$10;
    
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
    
    public CTShapeStyle getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeStyle ctShapeStyle = (CTShapeStyle)this.get_store().find_element_user(CTPictureImpl.STYLE$6, 0);
            if (ctShapeStyle == null) {
                return null;
            }
            return ctShapeStyle;
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPictureImpl.STYLE$6) != 0;
        }
    }
    
    public void setStyle(final CTShapeStyle ctShapeStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeStyle, CTPictureImpl.STYLE$6, 0, (short)1);
    }
    
    public CTShapeStyle addNewStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeStyle)this.get_store().add_element_user(CTPictureImpl.STYLE$6);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPictureImpl.STYLE$6, 0);
        }
    }
    
    public String getMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureImpl.MACRO$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureImpl.MACRO$8);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTPictureImpl.MACRO$8);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTPictureImpl.MACRO$8);
            }
            return xmlString;
        }
    }
    
    public boolean isSetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureImpl.MACRO$8) != null;
        }
    }
    
    public void setMacro(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureImpl.MACRO$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureImpl.MACRO$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMacro(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTPictureImpl.MACRO$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTPictureImpl.MACRO$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureImpl.MACRO$8);
        }
    }
    
    public boolean getFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureImpl.FPUBLISHED$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPictureImpl.FPUBLISHED$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPictureImpl.FPUBLISHED$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPictureImpl.FPUBLISHED$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPictureImpl.FPUBLISHED$10) != null;
        }
    }
    
    public void setFPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPictureImpl.FPUBLISHED$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPictureImpl.FPUBLISHED$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPictureImpl.FPUBLISHED$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPictureImpl.FPUBLISHED$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPictureImpl.FPUBLISHED$10);
        }
    }
    
    static {
        NVPICPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "nvPicPr");
        BLIPFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "blipFill");
        SPPR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "spPr");
        STYLE$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "style");
        MACRO$8 = new QName("", "macro");
        FPUBLISHED$10 = new QName("", "fPublished");
    }
}
