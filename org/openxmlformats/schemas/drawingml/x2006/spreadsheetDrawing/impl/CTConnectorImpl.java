package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnectorNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectorImpl extends XmlComplexContentImpl implements CTConnector
{
    private static final long serialVersionUID = 1L;
    private static final QName NVCXNSPPR$0;
    private static final QName SPPR$2;
    private static final QName STYLE$4;
    private static final QName MACRO$6;
    private static final QName FPUBLISHED$8;
    
    public CTConnectorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTConnectorNonVisual getNvCxnSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnectorNonVisual ctConnectorNonVisual = (CTConnectorNonVisual)this.get_store().find_element_user(CTConnectorImpl.NVCXNSPPR$0, 0);
            if (ctConnectorNonVisual == null) {
                return null;
            }
            return ctConnectorNonVisual;
        }
    }
    
    public void setNvCxnSpPr(final CTConnectorNonVisual ctConnectorNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctConnectorNonVisual, CTConnectorImpl.NVCXNSPPR$0, 0, (short)1);
    }
    
    public CTConnectorNonVisual addNewNvCxnSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnectorNonVisual)this.get_store().add_element_user(CTConnectorImpl.NVCXNSPPR$0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTConnectorImpl.SPPR$2, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTConnectorImpl.SPPR$2, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTConnectorImpl.SPPR$2);
        }
    }
    
    public CTShapeStyle getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeStyle ctShapeStyle = (CTShapeStyle)this.get_store().find_element_user(CTConnectorImpl.STYLE$4, 0);
            if (ctShapeStyle == null) {
                return null;
            }
            return ctShapeStyle;
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTConnectorImpl.STYLE$4) != 0;
        }
    }
    
    public void setStyle(final CTShapeStyle ctShapeStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeStyle, CTConnectorImpl.STYLE$4, 0, (short)1);
    }
    
    public CTShapeStyle addNewStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeStyle)this.get_store().add_element_user(CTConnectorImpl.STYLE$4);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTConnectorImpl.STYLE$4, 0);
        }
    }
    
    public String getMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectorImpl.MACRO$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTConnectorImpl.MACRO$6);
        }
    }
    
    public boolean isSetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTConnectorImpl.MACRO$6) != null;
        }
    }
    
    public void setMacro(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectorImpl.MACRO$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConnectorImpl.MACRO$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMacro(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTConnectorImpl.MACRO$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTConnectorImpl.MACRO$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTConnectorImpl.MACRO$6);
        }
    }
    
    public boolean getFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectorImpl.FPUBLISHED$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTConnectorImpl.FPUBLISHED$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTConnectorImpl.FPUBLISHED$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTConnectorImpl.FPUBLISHED$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTConnectorImpl.FPUBLISHED$8) != null;
        }
    }
    
    public void setFPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectorImpl.FPUBLISHED$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConnectorImpl.FPUBLISHED$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTConnectorImpl.FPUBLISHED$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTConnectorImpl.FPUBLISHED$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTConnectorImpl.FPUBLISHED$8);
        }
    }
    
    static {
        NVCXNSPPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "nvCxnSpPr");
        SPPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "spPr");
        STYLE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "style");
        MACRO$6 = new QName("", "macro");
        FPUBLISHED$8 = new QName("", "fPublished");
    }
}
