package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGraphicalObjectFrameNonVisualImpl extends XmlComplexContentImpl implements CTGraphicalObjectFrameNonVisual
{
    private static final long serialVersionUID = 1L;
    private static final QName CNVPR$0;
    private static final QName CNVGRAPHICFRAMEPR$2;
    
    public CTGraphicalObjectFrameNonVisualImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNonVisualDrawingProps getCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualDrawingProps ctNonVisualDrawingProps = (CTNonVisualDrawingProps)this.get_store().find_element_user(CTGraphicalObjectFrameNonVisualImpl.CNVPR$0, 0);
            if (ctNonVisualDrawingProps == null) {
                return null;
            }
            return ctNonVisualDrawingProps;
        }
    }
    
    public void setCNvPr(final CTNonVisualDrawingProps ctNonVisualDrawingProps) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualDrawingProps, CTGraphicalObjectFrameNonVisualImpl.CNVPR$0, 0, (short)1);
    }
    
    public CTNonVisualDrawingProps addNewCNvPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualDrawingProps)this.get_store().add_element_user(CTGraphicalObjectFrameNonVisualImpl.CNVPR$0);
        }
    }
    
    public CTNonVisualGraphicFrameProperties getCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties = (CTNonVisualGraphicFrameProperties)this.get_store().find_element_user(CTGraphicalObjectFrameNonVisualImpl.CNVGRAPHICFRAMEPR$2, 0);
            if (ctNonVisualGraphicFrameProperties == null) {
                return null;
            }
            return ctNonVisualGraphicFrameProperties;
        }
    }
    
    public void setCNvGraphicFramePr(final CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNonVisualGraphicFrameProperties, CTGraphicalObjectFrameNonVisualImpl.CNVGRAPHICFRAMEPR$2, 0, (short)1);
    }
    
    public CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNonVisualGraphicFrameProperties)this.get_store().add_element_user(CTGraphicalObjectFrameNonVisualImpl.CNVGRAPHICFRAMEPR$2);
        }
    }
    
    static {
        CNVPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvPr");
        CNVGRAPHICFRAMEPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cNvGraphicFramePr");
    }
}
