package org.apache.poi.xslf.usermodel;

import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrameNonVisual;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import java.awt.geom.Rectangle2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;

public final class XSLFChart extends XDDFChart
{
    private static String CHART_URI;
    
    protected XSLFChart() {
    }
    
    protected XSLFChart(final PackagePart part) throws IOException, XmlException {
        super(part);
    }
    
    @Override
    protected POIXMLRelation getChartRelation() {
        return XSLFRelation.CHART;
    }
    
    @Override
    protected POIXMLRelation getChartWorkbookRelation() {
        return XSLFRelation.WORKBOOK;
    }
    
    @Override
    protected POIXMLFactory getChartFactory() {
        return XSLFFactory.getInstance();
    }
    
    public XSLFTextShape getTitleShape() {
        if (!this.chart.isSetTitle()) {
            this.chart.addNewTitle();
        }
        final CTTitle title = this.chart.getTitle();
        if (title.getTx() != null && title.getTx().isSetRich()) {
            return new XSLFTextShape(title, null) {
                @Override
                protected CTTextBody getTextBody(final boolean create) {
                    return title.getTx().getRich();
                }
            };
        }
        return new XSLFTextShape(title, null) {
            @Override
            protected CTTextBody getTextBody(final boolean create) {
                return title.getTxPr();
            }
        };
    }
    
    static CTGraphicalObjectFrame prototype(final int shapeId, final String rID, final Rectangle2D anchor) {
        final CTGraphicalObjectFrame frame = CTGraphicalObjectFrame.Factory.newInstance();
        final CTGraphicalObjectFrameNonVisual nvGr = frame.addNewNvGraphicFramePr();
        final CTNonVisualDrawingProps cnv = nvGr.addNewCNvPr();
        cnv.setName("Chart " + shapeId);
        cnv.setId((long)shapeId);
        nvGr.addNewCNvGraphicFramePr().addNewGraphicFrameLocks().setNoGrp(true);
        nvGr.addNewNvPr();
        final CTTransform2D xfrm = frame.addNewXfrm();
        final CTPoint2D off = xfrm.addNewOff();
        off.setX((long)(int)anchor.getX());
        off.setY((long)(int)anchor.getY());
        final CTPositiveSize2D ext = xfrm.addNewExt();
        ext.setCx((long)(int)anchor.getWidth());
        ext.setCy((long)(int)anchor.getHeight());
        xfrm.setExt(ext);
        xfrm.setOff(off);
        final CTGraphicalObjectData gr = frame.addNewGraphic().addNewGraphicData();
        final XmlCursor grCur = gr.newCursor();
        grCur.toNextToken();
        grCur.beginElement(new QName(XSLFChart.CHART_URI, "chart"));
        grCur.insertAttributeWithValue("id", "http://schemas.openxmlformats.org/officeDocument/2006/relationships", rID);
        grCur.dispose();
        gr.setUri(XSLFChart.CHART_URI);
        return frame;
    }
    
    static {
        XSLFChart.CHART_URI = "http://schemas.openxmlformats.org/drawingml/2006/chart";
    }
}
