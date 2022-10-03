package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingShapeProps;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapeNonVisualImpl extends XmlComplexContentImpl implements CTShapeNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVSPPR$2;
    
    public CTShapeNonVisualImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNonVisualDrawingProps getCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTShapeNonVisualImpl.CNVPR$0, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setCNvPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTShapeNonVisualImpl.CNVPR$0, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTShapeNonVisualImpl.CNVPR$0);
        }
    }
    
    public CTNonVisualDrawingShapeProps getCNvSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingShapeProps ctNonVisualDrawingShapeProps = (CTNonVisualDrawingShapeProps)this.get_store().find_element_user(CTShapeNonVisualImpl.CNVSPPR$2, 0);
            if (ctNonVisualDrawingShapeProps == null) {
                return null;
            }
            return ctNonVisualDrawingShapeProps;
        }
    }
    
    public void setCNvSpPr(final CTNonVisualDrawingShapeProps ctNonVisualDrawingShapeProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingShapeProps, CTShapeNonVisualImpl.CNVSPPR$2, 0, (short)1);
    }
    
    public CTNonVisualDrawingShapeProps addNewCNvSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingShapeProps)this.get_store().add_element_user(CTShapeNonVisualImpl.CNVSPPR$2);
        }
    }
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvPr");
        CNVSPPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvSpPr");
    }
}
