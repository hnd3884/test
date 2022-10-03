package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.Shadow;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnectorNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.apache.poi.sl.usermodel.ConnectorShape;

public class XSLFConnectorShape extends XSLFSimpleShape implements ConnectorShape<XSLFShape, XSLFTextParagraph>
{
    XSLFConnectorShape(final CTConnector shape, final XSLFSheet sheet) {
        super((XmlObject)shape, sheet);
    }
    
    static CTConnector prototype(final int shapeId) {
        final CTConnector ct = CTConnector.Factory.newInstance();
        final CTConnectorNonVisual nvSpPr = ct.addNewNvCxnSpPr();
        final CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Connector " + shapeId);
        cnv.setId((long)shapeId);
        nvSpPr.addNewCNvCxnSpPr();
        nvSpPr.addNewNvPr();
        final CTShapeProperties spPr = ct.addNewSpPr();
        final CTPresetGeometry2D prst = spPr.addNewPrstGeom();
        prst.setPrst(STShapeType.LINE);
        prst.addNewAvLst();
        spPr.addNewLn();
        return ct;
    }
    
    @Override
    public XSLFShadow getShadow() {
        return null;
    }
    
    public void setPlaceholder(final Placeholder placeholder) {
        throw new POIXMLException("A connector shape can't be a placeholder.");
    }
}
