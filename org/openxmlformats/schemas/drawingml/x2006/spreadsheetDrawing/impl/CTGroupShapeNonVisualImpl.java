package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGroupDrawingShapeProps;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShapeNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGroupShapeNonVisualImpl extends XmlComplexContentImpl implements CTGroupShapeNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVGRPSPPR$2;
    
    public CTGroupShapeNonVisualImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNonVisualDrawingProps getCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTGroupShapeNonVisualImpl.CNVPR$0, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setCNvPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTGroupShapeNonVisualImpl.CNVPR$0, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTGroupShapeNonVisualImpl.CNVPR$0);
        }
    }
    
    public CTNonVisualGroupDrawingShapeProps getCNvGrpSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualGroupDrawingShapeProps ctNonVisualGroupDrawingShapeProps = (CTNonVisualGroupDrawingShapeProps)this.get_store().find_element_user(CTGroupShapeNonVisualImpl.CNVGRPSPPR$2, 0);
            if (ctNonVisualGroupDrawingShapeProps == null) {
                return null;
            }
            return ctNonVisualGroupDrawingShapeProps;
        }
    }
    
    public void setCNvGrpSpPr(final CTNonVisualGroupDrawingShapeProps ctNonVisualGroupDrawingShapeProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualGroupDrawingShapeProps, CTGroupShapeNonVisualImpl.CNVGRPSPPR$2, 0, (short)1);
    }
    
    public CTNonVisualGroupDrawingShapeProps addNewCNvGrpSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualGroupDrawingShapeProps)this.get_store().add_element_user(CTGroupShapeNonVisualImpl.CNVGRPSPPR$2);
        }
    }
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvPr");
        CNVGRPSPPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvGrpSpPr");
    }
}
