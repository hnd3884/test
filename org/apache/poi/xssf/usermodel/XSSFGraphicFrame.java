package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ChildAnchor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.XmlCursor;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;
import org.apache.poi.util.Internal;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;

public final class XSSFGraphicFrame extends XSSFShape
{
    private static CTGraphicalObjectFrame prototype;
    private CTGraphicalObjectFrame graphicFrame;
    
    protected XSSFGraphicFrame(final XSSFDrawing drawing, final CTGraphicalObjectFrame ctGraphicFrame) {
        this.drawing = drawing;
        this.graphicFrame = ctGraphicFrame;
        final CTGraphicalObjectData graphicData = this.graphicFrame.getGraphic().getGraphicData();
        if (graphicData != null) {
            final NodeList nodes = graphicData.getDomNode().getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeName().equals("c:chart")) {
                    final POIXMLDocumentPart relation = drawing.getRelationById(node.getAttributes().getNamedItem("r:id").getNodeValue());
                    if (relation instanceof XSSFChart) {
                        ((XSSFChart)relation).setGraphicFrame(this);
                    }
                }
            }
        }
    }
    
    @Internal
    public CTGraphicalObjectFrame getCTGraphicalObjectFrame() {
        return this.graphicFrame;
    }
    
    protected static CTGraphicalObjectFrame prototype() {
        if (XSSFGraphicFrame.prototype == null) {
            final CTGraphicalObjectFrame graphicFrame = CTGraphicalObjectFrame.Factory.newInstance();
            final CTGraphicalObjectFrameNonVisual nvGraphic = graphicFrame.addNewNvGraphicFramePr();
            final CTNonVisualDrawingProps props = nvGraphic.addNewCNvPr();
            props.setId(0L);
            props.setName("Diagramm 1");
            nvGraphic.addNewCNvGraphicFramePr();
            final CTTransform2D transform = graphicFrame.addNewXfrm();
            final CTPositiveSize2D extPoint = transform.addNewExt();
            final CTPoint2D offPoint = transform.addNewOff();
            extPoint.setCx(0L);
            extPoint.setCy(0L);
            offPoint.setX(0L);
            offPoint.setY(0L);
            graphicFrame.addNewGraphic();
            XSSFGraphicFrame.prototype = graphicFrame;
        }
        return XSSFGraphicFrame.prototype;
    }
    
    public void setMacro(final String macro) {
        this.graphicFrame.setMacro(macro);
    }
    
    public void setName(final String name) {
        this.getNonVisualProperties().setName(name);
    }
    
    public String getName() {
        return this.getNonVisualProperties().getName();
    }
    
    private CTNonVisualDrawingProps getNonVisualProperties() {
        final CTGraphicalObjectFrameNonVisual nvGraphic = this.graphicFrame.getNvGraphicFramePr();
        return nvGraphic.getCNvPr();
    }
    
    protected void setAnchor(final XSSFClientAnchor anchor) {
        this.anchor = anchor;
    }
    
    @Override
    public XSSFClientAnchor getAnchor() {
        return (XSSFClientAnchor)this.anchor;
    }
    
    protected void setChart(final XSSFChart chart, final String relId) {
        final CTGraphicalObjectData data = this.graphicFrame.getGraphic().addNewGraphicData();
        this.appendChartElement(data, relId);
        chart.setGraphicFrame(this);
    }
    
    public long getId() {
        return this.graphicFrame.getNvGraphicFramePr().getCNvPr().getId();
    }
    
    protected void setId(final long id) {
        this.graphicFrame.getNvGraphicFramePr().getCNvPr().setId(id);
    }
    
    private void appendChartElement(final CTGraphicalObjectData data, final String id) {
        final String r_namespaceUri = STRelationshipId.type.getName().getNamespaceURI();
        final String c_namespaceUri = "http://schemas.openxmlformats.org/drawingml/2006/chart";
        final XmlCursor cursor = data.newCursor();
        cursor.toNextToken();
        cursor.beginElement(new QName(c_namespaceUri, "chart", "c"));
        cursor.insertAttributeWithValue(new QName(r_namespaceUri, "id", "r"), id);
        cursor.dispose();
        data.setUri(c_namespaceUri);
    }
    
    @Override
    protected CTShapeProperties getShapeProperties() {
        return null;
    }
    
    public String getShapeName() {
        return this.graphicFrame.getNvGraphicFramePr().getCNvPr().getName();
    }
}
