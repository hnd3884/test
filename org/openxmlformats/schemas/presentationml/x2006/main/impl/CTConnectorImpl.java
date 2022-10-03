package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnectorNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectorImpl extends XmlComplexContentImpl implements CTConnector
{
    private static final long serialVersionUID = 1L;
    private static final QName NVCXNSPPR$0;
    private static final QName SPPR$2;
    private static final QName STYLE$4;
    private static final QName EXTLST$6;
    
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
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTConnectorImpl.EXTLST$6, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTConnectorImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTConnectorImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTConnectorImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTConnectorImpl.EXTLST$6, 0);
        }
    }
    
    static {
        NVCXNSPPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvCxnSpPr");
        SPPR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "spPr");
        STYLE$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "style");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
    }
}
