package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualConnectorProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnectorNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectorNonVisualImpl extends XmlComplexContentImpl implements CTConnectorNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVCXNSPPR$2;
    
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
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvPr");
        CNVCXNSPPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvCxnSpPr");
    }
}
