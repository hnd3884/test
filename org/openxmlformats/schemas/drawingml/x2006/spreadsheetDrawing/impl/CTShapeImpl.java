package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapeImpl extends XmlComplexContentImpl implements CTShape
{
    private static final long serialVersionUID = 1L;
    private static final QName NVSPPR$0;
    private static final QName SPPR$2;
    private static final QName STYLE$4;
    private static final QName TXBODY$6;
    private static final QName MACRO$8;
    private static final QName TEXTLINK$10;
    private static final QName FLOCKSTEXT$12;
    private static final QName FPUBLISHED$14;
    
    public CTShapeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTShapeNonVisual getNvSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeNonVisual ctShapeNonVisual = (CTShapeNonVisual)this.get_store().find_element_user(CTShapeImpl.NVSPPR$0, 0);
            if (ctShapeNonVisual == null) {
                return null;
            }
            return ctShapeNonVisual;
        }
    }
    
    public void setNvSpPr(final CTShapeNonVisual ctShapeNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeNonVisual, CTShapeImpl.NVSPPR$0, 0, (short)1);
    }
    
    public CTShapeNonVisual addNewNvSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeNonVisual)this.get_store().add_element_user(CTShapeImpl.NVSPPR$0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTShapeImpl.SPPR$2, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTShapeImpl.SPPR$2, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTShapeImpl.SPPR$2);
        }
    }
    
    public CTShapeStyle getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeStyle ctShapeStyle = (CTShapeStyle)this.get_store().find_element_user(CTShapeImpl.STYLE$4, 0);
            if (ctShapeStyle == null) {
                return null;
            }
            return ctShapeStyle;
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapeImpl.STYLE$4) != 0;
        }
    }
    
    public void setStyle(final CTShapeStyle ctShapeStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeStyle, CTShapeImpl.STYLE$4, 0, (short)1);
    }
    
    public CTShapeStyle addNewStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeStyle)this.get_store().add_element_user(CTShapeImpl.STYLE$4);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapeImpl.STYLE$4, 0);
        }
    }
    
    public CTTextBody getTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTShapeImpl.TXBODY$6, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapeImpl.TXBODY$6) != 0;
        }
    }
    
    public void setTxBody(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTShapeImpl.TXBODY$6, 0, (short)1);
    }
    
    public CTTextBody addNewTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTShapeImpl.TXBODY$6);
        }
    }
    
    public void unsetTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapeImpl.TXBODY$6, 0);
        }
    }
    
    public String getMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.MACRO$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapeImpl.MACRO$8);
        }
    }
    
    public boolean isSetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeImpl.MACRO$8) != null;
        }
    }
    
    public void setMacro(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.MACRO$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeImpl.MACRO$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMacro(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapeImpl.MACRO$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapeImpl.MACRO$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeImpl.MACRO$8);
        }
    }
    
    public String getTextlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.TEXTLINK$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTextlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapeImpl.TEXTLINK$10);
        }
    }
    
    public boolean isSetTextlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeImpl.TEXTLINK$10) != null;
        }
    }
    
    public void setTextlink(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.TEXTLINK$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeImpl.TEXTLINK$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTextlink(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapeImpl.TEXTLINK$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapeImpl.TEXTLINK$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTextlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeImpl.TEXTLINK$10);
        }
    }
    
    public boolean getFLocksText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.FLOCKSTEXT$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapeImpl.FLOCKSTEXT$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFLocksText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTShapeImpl.FLOCKSTEXT$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTShapeImpl.FLOCKSTEXT$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFLocksText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeImpl.FLOCKSTEXT$12) != null;
        }
    }
    
    public void setFLocksText(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.FLOCKSTEXT$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeImpl.FLOCKSTEXT$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFLocksText(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTShapeImpl.FLOCKSTEXT$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTShapeImpl.FLOCKSTEXT$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFLocksText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeImpl.FLOCKSTEXT$12);
        }
    }
    
    public boolean getFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.FPUBLISHED$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapeImpl.FPUBLISHED$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTShapeImpl.FPUBLISHED$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTShapeImpl.FPUBLISHED$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeImpl.FPUBLISHED$14) != null;
        }
    }
    
    public void setFPublished(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.FPUBLISHED$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeImpl.FPUBLISHED$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFPublished(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTShapeImpl.FPUBLISHED$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTShapeImpl.FPUBLISHED$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFPublished() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeImpl.FPUBLISHED$14);
        }
    }
    
    static {
        NVSPPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "nvSpPr");
        SPPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "spPr");
        STYLE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "style");
        TXBODY$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "txBody");
        MACRO$8 = new QName("", "macro");
        TEXTLINK$10 = new QName("", "textlink");
        FLOCKSTEXT$12 = new QName("", "fLocksText");
        FPUBLISHED$14 = new QName("", "fPublished");
    }
}
