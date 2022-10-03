package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.apache.poi.sl.usermodel.TextBox;

public class XSLFTextBox extends XSLFAutoShape implements TextBox<XSLFShape, XSLFTextParagraph>
{
    XSLFTextBox(final CTShape shape, final XSLFSheet sheet) {
        super(shape, sheet);
    }
    
    static CTShape prototype(final int shapeId) {
        final CTShape ct = CTShape.Factory.newInstance();
        final CTShapeNonVisual nvSpPr = ct.addNewNvSpPr();
        final CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("TextBox " + shapeId);
        cnv.setId((long)shapeId);
        nvSpPr.addNewCNvSpPr().setTxBox(true);
        nvSpPr.addNewNvPr();
        final CTShapeProperties spPr = ct.addNewSpPr();
        final CTPresetGeometry2D prst = spPr.addNewPrstGeom();
        prst.setPrst(STShapeType.RECT);
        prst.addNewAvLst();
        final XDDFTextBody body = new XDDFTextBody(null);
        body.initialize();
        ct.setTxBody(body.getXmlObject());
        return ct;
    }
}
