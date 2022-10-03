package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualConnectorProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnectorNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectorNonVisualImpl extends XmlComplexContentImpl implements CTConnectorNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVCXNSPPR$2;
    private static final QName NVPR$4;
    
    public CTConnectorNonVisualImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNonVisualDrawingProps getCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTConnectorNonVisualImpl.CNVPR$0, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setCNvPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTConnectorNonVisualImpl.CNVPR$0, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTConnectorNonVisualImpl.CNVPR$0);
        }
    }
    
    public CTNonVisualConnectorProperties getCNvCxnSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualConnectorProperties ctNonVisualConnectorProperties = (CTNonVisualConnectorProperties)this.get_store().find_element_user(CTConnectorNonVisualImpl.CNVCXNSPPR$2, 0);
            if (ctNonVisualConnectorProperties == null) {
                return null;
            }
            return ctNonVisualConnectorProperties;
        }
    }
    
    public void setCNvCxnSpPr(final CTNonVisualConnectorProperties ctNonVisualConnectorProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualConnectorProperties, CTConnectorNonVisualImpl.CNVCXNSPPR$2, 0, (short)1);
    }
    
    public CTNonVisualConnectorProperties addNewCNvCxnSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualConnectorProperties)this.get_store().add_element_user(CTConnectorNonVisualImpl.CNVCXNSPPR$2);
        }
    }
    
    public CTApplicationNonVisualDrawingProps getNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTApplicationNonVisualDrawingProps ctApplicationNonVisualDrawingProps = (CTApplicationNonVisualDrawingProps)this.get_store().find_element_user(CTConnectorNonVisualImpl.NVPR$4, 0);
            if (ctApplicationNonVisualDrawingProps == null) {
                return null;
            }
            return ctApplicationNonVisualDrawingProps;
        }
    }
    
    public void setNvPr(final CTApplicationNonVisualDrawingProps ctApplicationNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctApplicationNonVisualDrawingProps, CTConnectorNonVisualImpl.NVPR$4, 0, (short)1);
    }
    
    public CTApplicationNonVisualDrawingProps addNewNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTApplicationNonVisualDrawingProps)this.get_store().add_element_user(CTConnectorNonVisualImpl.NVPR$4);
        }
    }
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvPr");
        CNVCXNSPPR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cNvCxnSpPr");
        NVPR$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvPr");
    }
}
