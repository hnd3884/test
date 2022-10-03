package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.xmlbeans.XmlCursor;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.poi.util.Units;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.GraphicalFrame;

public class XSLFGraphicFrame extends XSLFShape implements GraphicalFrame<XSLFShape, XSLFTextParagraph>
{
    private static final String DRAWINGML_CHART_URI = "http://schemas.openxmlformats.org/drawingml/2006/chart";
    private static final POILogger LOG;
    
    XSLFGraphicFrame(final CTGraphicalObjectFrame shape, final XSLFSheet sheet) {
        super((XmlObject)shape, sheet);
    }
    
    public ShapeType getShapeType() {
        throw new UnsupportedOperationException();
    }
    
    public Rectangle2D getAnchor() {
        final CTTransform2D xfrm = ((CTGraphicalObjectFrame)this.getXmlObject()).getXfrm();
        final CTPoint2D off = xfrm.getOff();
        final double x = Units.toPoints(off.getX());
        final double y = Units.toPoints(off.getY());
        final CTPositiveSize2D ext = xfrm.getExt();
        final double cx = Units.toPoints(ext.getCx());
        final double cy = Units.toPoints(ext.getCy());
        return new Rectangle2D.Double(x, y, cx, cy);
    }
    
    public void setAnchor(final Rectangle2D anchor) {
        final CTTransform2D xfrm = ((CTGraphicalObjectFrame)this.getXmlObject()).getXfrm();
        final CTPoint2D off = xfrm.isSetOff() ? xfrm.getOff() : xfrm.addNewOff();
        final long x = Units.toEMU(anchor.getX());
        final long y = Units.toEMU(anchor.getY());
        off.setX(x);
        off.setY(y);
        final CTPositiveSize2D ext = xfrm.isSetExt() ? xfrm.getExt() : xfrm.addNewExt();
        final long cx = Units.toEMU(anchor.getWidth());
        final long cy = Units.toEMU(anchor.getHeight());
        ext.setCx(cx);
        ext.setCy(cy);
    }
    
    static XSLFGraphicFrame create(final CTGraphicalObjectFrame shape, final XSLFSheet sheet) {
        final String uri = getUri(shape);
        final String s = (uri == null) ? "" : uri;
        switch (s) {
            case "http://schemas.openxmlformats.org/drawingml/2006/table": {
                return new XSLFTable(shape, sheet);
            }
            case "http://schemas.openxmlformats.org/presentationml/2006/ole": {
                return new XSLFObjectShape(shape, sheet);
            }
            default: {
                return new XSLFGraphicFrame(shape, sheet);
            }
        }
    }
    
    private static String getUri(final CTGraphicalObjectFrame shape) {
        final CTGraphicalObject g = shape.getGraphic();
        if (g == null) {
            return null;
        }
        final CTGraphicalObjectData gd = g.getGraphicData();
        return (gd == null) ? null : gd.getUri();
    }
    
    public void setRotation(final double theta) {
        throw new IllegalArgumentException("Operation not supported");
    }
    
    public double getRotation() {
        return 0.0;
    }
    
    public void setFlipHorizontal(final boolean flip) {
        throw new IllegalArgumentException("Operation not supported");
    }
    
    public void setFlipVertical(final boolean flip) {
        throw new IllegalArgumentException("Operation not supported");
    }
    
    public boolean getFlipHorizontal() {
        return false;
    }
    
    public boolean getFlipVertical() {
        return false;
    }
    
    public boolean hasChart() {
        final String uri = this.getGraphicalData().getUri();
        return uri.equals("http://schemas.openxmlformats.org/drawingml/2006/chart");
    }
    
    private CTGraphicalObjectData getGraphicalData() {
        return ((CTGraphicalObjectFrame)this.getXmlObject()).getGraphic().getGraphicData();
    }
    
    public XSLFChart getChart() {
        if (!this.hasChart()) {
            return null;
        }
        String id = null;
        final String xpath = "declare namespace c='http://schemas.openxmlformats.org/drawingml/2006/chart' c:chart";
        final XmlObject[] obj = this.getGraphicalData().selectPath(xpath);
        if (obj != null && obj.length == 1) {
            final XmlCursor c = obj[0].newCursor();
            final QName idQualifiedName = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
            id = c.getAttributeText(idQualifiedName);
            c.dispose();
        }
        if (id == null) {
            return null;
        }
        return (XSLFChart)this.getSheet().getRelationById(id);
    }
    
    @Override
    void copy(final XSLFShape sh) {
        super.copy(sh);
        final CTGraphicalObjectData data = this.getGraphicalData();
        final String uri = data.getUri();
        if (uri.equals("http://schemas.openxmlformats.org/drawingml/2006/diagram")) {
            this.copyDiagram(data, (XSLFGraphicFrame)sh);
        }
        if (uri.equals("http://schemas.openxmlformats.org/drawingml/2006/chart")) {
            this.copyChart(data, (XSLFGraphicFrame)sh);
        }
    }
    
    private void copyChart(final CTGraphicalObjectData objData, final XSLFGraphicFrame srcShape) {
        final XSLFSlide slide = (XSLFSlide)this.getSheet();
        final XSLFSheet src = srcShape.getSheet();
        final String xpath = "declare namespace c='http://schemas.openxmlformats.org/drawingml/2006/chart' c:chart";
        final XmlObject[] obj = objData.selectPath(xpath);
        if (obj != null && obj.length == 1) {
            final XmlCursor c = obj[0].newCursor();
            try {
                final QName idQualifiedName = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
                final String id = c.getAttributeText(idQualifiedName);
                final XSLFChart srcChart = (XSLFChart)src.getRelationById(id);
                final XSLFChart chartCopy = slide.getSlideShow().createChart(slide);
                chartCopy.importContent(srcChart);
                chartCopy.setWorkbook(srcChart.getWorkbook());
                c.setAttributeText(idQualifiedName, slide.getRelationId(chartCopy));
            }
            catch (final InvalidFormatException | IOException e) {
                throw new POIXMLException(e);
            }
            c.dispose();
        }
    }
    
    private void copyDiagram(final CTGraphicalObjectData objData, final XSLFGraphicFrame srcShape) {
        final String xpath = "declare namespace dgm='http://schemas.openxmlformats.org/drawingml/2006/diagram' $this//dgm:relIds";
        final XmlObject[] obj = objData.selectPath(xpath);
        if (obj != null && obj.length == 1) {
            final XmlCursor c = obj[0].newCursor();
            final XSLFSheet sheet = srcShape.getSheet();
            try {
                final String dm = c.getAttributeText(new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "dm"));
                final PackageRelationship dmRel = sheet.getPackagePart().getRelationship(dm);
                final PackagePart dmPart = sheet.getPackagePart().getRelatedPart(dmRel);
                this.getSheet().importPart(dmRel, dmPart);
                final String lo = c.getAttributeText(new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "lo"));
                final PackageRelationship loRel = sheet.getPackagePart().getRelationship(lo);
                final PackagePart loPart = sheet.getPackagePart().getRelatedPart(loRel);
                this.getSheet().importPart(loRel, loPart);
                final String qs = c.getAttributeText(new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "qs"));
                final PackageRelationship qsRel = sheet.getPackagePart().getRelationship(qs);
                final PackagePart qsPart = sheet.getPackagePart().getRelatedPart(qsRel);
                this.getSheet().importPart(qsRel, qsPart);
                final String cs = c.getAttributeText(new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "cs"));
                final PackageRelationship csRel = sheet.getPackagePart().getRelationship(cs);
                final PackagePart csPart = sheet.getPackagePart().getRelatedPart(csRel);
                this.getSheet().importPart(csRel, csPart);
            }
            catch (final InvalidFormatException e) {
                throw new POIXMLException(e);
            }
            c.dispose();
        }
    }
    
    public XSLFPictureShape getFallbackPicture() {
        final String xquery = "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main'; declare namespace mc='http://schemas.openxmlformats.org/markup-compatibility/2006' .//mc:Fallback/*/p:pic";
        final XmlObject xo = this.selectProperty(XmlObject.class, xquery);
        if (xo == null) {
            return null;
        }
        CTGroupShape gs;
        try {
            gs = CTGroupShape.Factory.parse(xo.newDomNode());
        }
        catch (final XmlException e) {
            XSLFGraphicFrame.LOG.log(5, new Object[] { "Can't parse fallback picture stream of graphical frame", e });
            return null;
        }
        if (gs.sizeOfPicArray() == 0) {
            return null;
        }
        return new XSLFPictureShape(gs.getPicArray(0), this.getSheet());
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSLFGraphicFrame.class);
    }
}
