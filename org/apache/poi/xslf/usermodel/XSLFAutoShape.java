package org.apache.poi.xslf.usermodel;

import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.apache.poi.sl.usermodel.AutoShape;

public class XSLFAutoShape extends XSLFTextShape implements AutoShape<XSLFShape, XSLFTextParagraph>
{
    XSLFAutoShape(final CTShape shape, final XSLFSheet sheet) {
        super((XmlObject)shape, sheet);
    }
    
    static XSLFAutoShape create(final CTShape shape, final XSLFSheet sheet) {
        if (shape.getSpPr().isSetCustGeom()) {
            return new XSLFFreeformShape(shape, sheet);
        }
        if (shape.getNvSpPr().getCNvSpPr().isSetTxBox()) {
            return new XSLFTextBox(shape, sheet);
        }
        return new XSLFAutoShape(shape, sheet);
    }
    
    static CTShape prototype(final int shapeId) {
        final CTShape ct = CTShape.Factory.newInstance();
        final CTShapeNonVisual nvSpPr = ct.addNewNvSpPr();
        final CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("AutoShape " + shapeId);
        cnv.setId((long)shapeId);
        nvSpPr.addNewCNvSpPr();
        nvSpPr.addNewNvPr();
        final CTShapeProperties spPr = ct.addNewSpPr();
        final CTPresetGeometry2D prst = spPr.addNewPrstGeom();
        prst.setPrst(STShapeType.RECT);
        prst.addNewAvLst();
        return ct;
    }
    
    @Override
    protected CTTextBody getTextBody(final boolean create) {
        final CTShape shape = (CTShape)this.getXmlObject();
        CTTextBody txBody = shape.getTxBody();
        if (txBody == null && create) {
            final XDDFTextBody body = new XDDFTextBody(this);
            shape.setTxBody(body.getXmlObject());
            txBody = shape.getTxBody();
        }
        return txBody;
    }
    
    public String toString() {
        return "[" + this.getClass().getSimpleName() + "] " + this.getShapeName();
    }
}
