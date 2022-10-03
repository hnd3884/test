package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapeImpl extends XmlComplexContentImpl implements CTShape
{
    private static final long serialVersionUID = 1L;
    private static final QName NVSPPR$0;
    private static final QName SPPR$2;
    private static final QName STYLE$4;
    private static final QName TXBODY$6;
    private static final QName EXTLST$8;
    private static final QName USEBGFILL$10;
    
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
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTShapeImpl.EXTLST$8, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapeImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTShapeImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTShapeImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapeImpl.EXTLST$8, 0);
        }
    }
    
    public boolean getUseBgFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.USEBGFILL$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapeImpl.USEBGFILL$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUseBgFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTShapeImpl.USEBGFILL$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTShapeImpl.USEBGFILL$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUseBgFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeImpl.USEBGFILL$10) != null;
        }
    }
    
    public void setUseBgFill(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeImpl.USEBGFILL$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeImpl.USEBGFILL$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUseBgFill(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTShapeImpl.USEBGFILL$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTShapeImpl.USEBGFILL$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUseBgFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeImpl.USEBGFILL$10);
        }
    }
    
    static {
        NVSPPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvSpPr");
        SPPR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "spPr");
        STYLE$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "style");
        TXBODY$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "txBody");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
        USEBGFILL$10 = new QName("", "useBgFill");
    }
}
