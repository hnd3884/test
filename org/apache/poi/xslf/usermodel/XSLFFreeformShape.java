package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import java.awt.Shape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DClose;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DCubicBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DQuadBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import org.apache.poi.sl.draw.geom.PresetGeometries;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import java.awt.geom.AffineTransform;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import java.awt.geom.Path2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.FreeformShape;

public class XSLFFreeformShape extends XSLFAutoShape implements FreeformShape<XSLFShape, XSLFTextParagraph>
{
    private static final POILogger LOG;
    
    XSLFFreeformShape(final CTShape shape, final XSLFSheet sheet) {
        super(shape, sheet);
    }
    
    public int setPath(final Path2D path) {
        final CTPath2D ctPath = CTPath2D.Factory.newInstance();
        final Rectangle2D bounds = path.getBounds2D();
        final int x0 = Units.toEMU(bounds.getX());
        final int y0 = Units.toEMU(bounds.getY());
        final PathIterator it = path.getPathIterator(new AffineTransform());
        int numPoints = 0;
        ctPath.setH((long)Units.toEMU(bounds.getHeight()));
        ctPath.setW((long)Units.toEMU(bounds.getWidth()));
        final double[] vals = new double[6];
        while (!it.isDone()) {
            final int type = it.currentSegment(vals);
            CTAdjPoint2D[] points = null;
            switch (type) {
                case 0: {
                    points = addMoveTo(ctPath);
                    break;
                }
                case 1: {
                    points = addLineTo(ctPath);
                    break;
                }
                case 2: {
                    points = addQuadBezierTo(ctPath);
                    break;
                }
                case 3: {
                    points = addCubicBezierTo(ctPath);
                    break;
                }
                case 4: {
                    points = addClosePath(ctPath);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unrecognized path segment type: " + type);
                }
            }
            int i = 0;
            for (final CTAdjPoint2D point : points) {
                point.setX((Object)(Units.toEMU(vals[i++]) - x0));
                point.setY((Object)(Units.toEMU(vals[i++]) - y0));
            }
            numPoints += Math.max(points.length, 1);
            it.next();
        }
        final XmlObject xo = this.getShapeProperties();
        if (!(xo instanceof CTShapeProperties)) {
            return -1;
        }
        ((CTShapeProperties)xo).getCustGeom().getPathLst().setPathArray(new CTPath2D[] { ctPath });
        this.setAnchor(bounds);
        return numPoints;
    }
    
    public CustomGeometry getGeometry() {
        final XmlObject xo = this.getShapeProperties();
        if (!(xo instanceof CTShapeProperties)) {
            return null;
        }
        final XmlOptions xop = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xop.setSaveOuter();
        final XMLStreamReader staxReader = ((CTShapeProperties)xo).getCustGeom().newXMLStreamReader(xop);
        final CustomGeometry custGeo = PresetGeometries.convertCustomGeometry(staxReader);
        try {
            staxReader.close();
        }
        catch (final XMLStreamException e) {
            XSLFFreeformShape.LOG.log(5, new Object[] { "An error occurred while closing a Custom Geometry XML Stream Reader: " + e.getMessage() });
        }
        return custGeo;
    }
    
    public Path2D.Double getPath() {
        final Path2D.Double path = new Path2D.Double();
        final XmlObject xo = this.getShapeProperties();
        if (!(xo instanceof CTShapeProperties)) {
            return null;
        }
        final CTCustomGeometry2D geom = ((CTShapeProperties)xo).getCustGeom();
        for (final CTPath2D spPath : geom.getPathLst().getPathArray()) {
            final XmlCursor cursor = spPath.newCursor();
            try {
                if (cursor.toFirstChild()) {
                    do {
                        final XmlObject ch = cursor.getObject();
                        if (ch instanceof CTPath2DMoveTo) {
                            addMoveTo(path, (CTPath2DMoveTo)ch);
                        }
                        else if (ch instanceof CTPath2DLineTo) {
                            addLineTo(path, (CTPath2DLineTo)ch);
                        }
                        else if (ch instanceof CTPath2DQuadBezierTo) {
                            addQuadBezierTo(path, (CTPath2DQuadBezierTo)ch);
                        }
                        else if (ch instanceof CTPath2DCubicBezierTo) {
                            addCubicBezierTo(path, (CTPath2DCubicBezierTo)ch);
                        }
                        else if (ch instanceof CTPath2DClose) {
                            addClosePath(path);
                        }
                        else {
                            XSLFFreeformShape.LOG.log(5, new Object[] { "can't handle path of type " + xo.getClass() });
                        }
                    } while (cursor.toNextSibling());
                }
            }
            finally {
                cursor.dispose();
            }
        }
        final AffineTransform at = new AffineTransform();
        final CTTransform2D xfrm = this.getXfrm(false);
        final Rectangle2D xfrm2d = new Rectangle2D.Double((double)xfrm.getOff().getX(), (double)xfrm.getOff().getY(), (double)xfrm.getExt().getCx(), (double)xfrm.getExt().getCy());
        final Rectangle2D bounds = this.getAnchor();
        at.translate(bounds.getX() + bounds.getCenterX(), bounds.getY() + bounds.getCenterY());
        at.scale(7.874015748031496E-5, 7.874015748031496E-5);
        at.translate(-xfrm2d.getCenterX(), -xfrm2d.getCenterY());
        return new Path2D.Double(at.createTransformedShape(path));
    }
    
    private static CTAdjPoint2D[] addMoveTo(final CTPath2D path) {
        return new CTAdjPoint2D[] { path.addNewMoveTo().addNewPt() };
    }
    
    private static void addMoveTo(final Path2D path, final CTPath2DMoveTo xo) {
        final CTAdjPoint2D pt = xo.getPt();
        path.moveTo((double)(long)pt.getX(), (double)(long)pt.getY());
    }
    
    private static CTAdjPoint2D[] addLineTo(final CTPath2D path) {
        return new CTAdjPoint2D[] { path.addNewLnTo().addNewPt() };
    }
    
    private static void addLineTo(final Path2D path, final CTPath2DLineTo xo) {
        final CTAdjPoint2D pt = xo.getPt();
        path.lineTo((double)(long)pt.getX(), (double)(long)pt.getY());
    }
    
    private static CTAdjPoint2D[] addQuadBezierTo(final CTPath2D path) {
        final CTPath2DQuadBezierTo bez = path.addNewQuadBezTo();
        return new CTAdjPoint2D[] { bez.addNewPt(), bez.addNewPt() };
    }
    
    private static void addQuadBezierTo(final Path2D path, final CTPath2DQuadBezierTo xo) {
        final CTAdjPoint2D pt1 = xo.getPtArray(0);
        final CTAdjPoint2D pt2 = xo.getPtArray(1);
        path.quadTo((double)(long)pt1.getX(), (double)(long)pt1.getY(), (double)(long)pt2.getX(), (double)(long)pt2.getY());
    }
    
    private static CTAdjPoint2D[] addCubicBezierTo(final CTPath2D path) {
        final CTPath2DCubicBezierTo bez = path.addNewCubicBezTo();
        return new CTAdjPoint2D[] { bez.addNewPt(), bez.addNewPt(), bez.addNewPt() };
    }
    
    private static void addCubicBezierTo(final Path2D path, final CTPath2DCubicBezierTo xo) {
        final CTAdjPoint2D pt1 = xo.getPtArray(0);
        final CTAdjPoint2D pt2 = xo.getPtArray(1);
        final CTAdjPoint2D pt3 = xo.getPtArray(2);
        path.curveTo((double)(long)pt1.getX(), (double)(long)pt1.getY(), (double)(long)pt2.getX(), (double)(long)pt2.getY(), (double)(long)pt3.getX(), (double)(long)pt3.getY());
    }
    
    private static CTAdjPoint2D[] addClosePath(final CTPath2D path) {
        path.addNewClose();
        return new CTAdjPoint2D[0];
    }
    
    private static void addClosePath(final Path2D path) {
        path.closePath();
    }
    
    static CTShape prototype(final int shapeId) {
        final CTShape ct = CTShape.Factory.newInstance();
        final CTShapeNonVisual nvSpPr = ct.addNewNvSpPr();
        final CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Freeform " + shapeId);
        cnv.setId((long)shapeId);
        nvSpPr.addNewCNvSpPr();
        nvSpPr.addNewNvPr();
        final CTShapeProperties spPr = ct.addNewSpPr();
        final CTCustomGeometry2D geom = spPr.addNewCustGeom();
        geom.addNewAvLst();
        geom.addNewGdLst();
        geom.addNewAhLst();
        geom.addNewCxnLst();
        final CTGeomRect rect = geom.addNewRect();
        rect.setR((Object)"r");
        rect.setB((Object)"b");
        rect.setT((Object)"t");
        rect.setL((Object)"l");
        geom.addNewPathLst();
        return ct;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSLFFreeformShape.class);
    }
}
