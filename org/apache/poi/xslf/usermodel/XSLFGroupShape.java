package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.AutoShape;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.sl.usermodel.ObjectShape;
import java.awt.Dimension;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObject;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.sl.usermodel.PictureData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import java.util.function.Consumer;
import java.util.Collection;
import java.util.ArrayList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.poi.util.Units;
import java.awt.geom.Rectangle2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import java.util.List;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.GroupShape;

public class XSLFGroupShape extends XSLFShape implements XSLFShapeContainer, GroupShape<XSLFShape, XSLFTextParagraph>
{
    private static final POILogger _logger;
    private final List<XSLFShape> _shapes;
    private final CTGroupShapeProperties _grpSpPr;
    private XSLFDrawing _drawing;
    
    protected XSLFGroupShape(final CTGroupShape shape, final XSLFSheet sheet) {
        super((XmlObject)shape, sheet);
        this._shapes = XSLFSheet.buildShapes(shape, this);
        this._grpSpPr = shape.getGrpSpPr();
    }
    
    @Override
    protected CTGroupShapeProperties getGrpSpPr() {
        return this._grpSpPr;
    }
    
    private CTGroupTransform2D getSafeXfrm() {
        final CTGroupTransform2D xfrm = this.getXfrm();
        return (xfrm == null) ? this.getGrpSpPr().addNewXfrm() : xfrm;
    }
    
    protected CTGroupTransform2D getXfrm() {
        return this.getGrpSpPr().getXfrm();
    }
    
    public Rectangle2D getAnchor() {
        final CTGroupTransform2D xfrm = this.getXfrm();
        final CTPoint2D off = xfrm.getOff();
        final double x = Units.toPoints(off.getX());
        final double y = Units.toPoints(off.getY());
        final CTPositiveSize2D ext = xfrm.getExt();
        final double cx = Units.toPoints(ext.getCx());
        final double cy = Units.toPoints(ext.getCy());
        return new Rectangle2D.Double(x, y, cx, cy);
    }
    
    public void setAnchor(final Rectangle2D anchor) {
        final CTGroupTransform2D xfrm = this.getSafeXfrm();
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
    
    public Rectangle2D getInteriorAnchor() {
        final CTGroupTransform2D xfrm = this.getXfrm();
        final CTPoint2D off = xfrm.getChOff();
        final double x = Units.toPoints(off.getX());
        final double y = Units.toPoints(off.getY());
        final CTPositiveSize2D ext = xfrm.getChExt();
        final double cx = Units.toPoints(ext.getCx());
        final double cy = Units.toPoints(ext.getCy());
        return new Rectangle2D.Double(x, y, cx, cy);
    }
    
    public void setInteriorAnchor(final Rectangle2D anchor) {
        final CTGroupTransform2D xfrm = this.getSafeXfrm();
        final CTPoint2D off = xfrm.isSetChOff() ? xfrm.getChOff() : xfrm.addNewChOff();
        final long x = Units.toEMU(anchor.getX());
        final long y = Units.toEMU(anchor.getY());
        off.setX(x);
        off.setY(y);
        final CTPositiveSize2D ext = xfrm.isSetChExt() ? xfrm.getChExt() : xfrm.addNewChExt();
        final long cx = Units.toEMU(anchor.getWidth());
        final long cy = Units.toEMU(anchor.getHeight());
        ext.setCx(cx);
        ext.setCy(cy);
    }
    
    public List<XSLFShape> getShapes() {
        return this._shapes;
    }
    
    public Iterator<XSLFShape> iterator() {
        return this._shapes.iterator();
    }
    
    public boolean removeShape(final XSLFShape xShape) {
        final XmlObject obj = xShape.getXmlObject();
        final CTGroupShape grpSp = (CTGroupShape)this.getXmlObject();
        this.getSheet().deregisterShapeId(xShape.getShapeId());
        if (obj instanceof CTShape) {
            grpSp.getSpList().remove(obj);
        }
        else if (obj instanceof CTGroupShape) {
            final XSLFGroupShape gs = (XSLFGroupShape)xShape;
            new ArrayList(gs.getShapes()).forEach((Consumer)gs::removeShape);
            grpSp.getGrpSpList().remove(obj);
        }
        else if (obj instanceof CTConnector) {
            grpSp.getCxnSpList().remove(obj);
        }
        else if (obj instanceof CTGraphicalObjectFrame) {
            grpSp.getGraphicFrameList().remove(obj);
        }
        else {
            if (!(obj instanceof CTPicture)) {
                throw new IllegalArgumentException("Unsupported shape: " + xShape);
            }
            final XSLFPictureShape ps = (XSLFPictureShape)xShape;
            final XSLFSheet sh = this.getSheet();
            if (sh != null) {
                sh.removePictureRelation(ps);
            }
            grpSp.getPicList().remove(obj);
        }
        return this._shapes.remove(xShape);
    }
    
    static CTGroupShape prototype(final int shapeId) {
        final CTGroupShape ct = CTGroupShape.Factory.newInstance();
        final CTGroupShapeNonVisual nvSpPr = ct.addNewNvGrpSpPr();
        final CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Group " + shapeId);
        cnv.setId((long)shapeId);
        nvSpPr.addNewCNvGrpSpPr();
        nvSpPr.addNewNvPr();
        ct.addNewGrpSpPr();
        return ct;
    }
    
    private XSLFDrawing getDrawing() {
        if (this._drawing == null) {
            this._drawing = new XSLFDrawing(this.getSheet(), (CTGroupShape)this.getXmlObject());
        }
        return this._drawing;
    }
    
    @Override
    public XSLFAutoShape createAutoShape() {
        final XSLFAutoShape sh = this.getDrawing().createAutoShape();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFFreeformShape createFreeform() {
        final XSLFFreeformShape sh = this.getDrawing().createFreeform();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFTextBox createTextBox() {
        final XSLFTextBox sh = this.getDrawing().createTextBox();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFConnectorShape createConnector() {
        final XSLFConnectorShape sh = this.getDrawing().createConnector();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFGroupShape createGroup() {
        final XSLFGroupShape sh = this.getDrawing().createGroup();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    @Override
    public XSLFPictureShape createPicture(final PictureData pictureData) {
        if (!(pictureData instanceof XSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type XSLFPictureData");
        }
        final POIXMLDocumentPart.RelationPart rp = this.getSheet().addRelation(null, XSLFRelation.IMAGES, (POIXMLDocumentPart)pictureData);
        final XSLFPictureShape sh = this.getDrawing().createPicture(rp.getRelationship().getId());
        new DrawPictureShape((PictureShape)sh).resize();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    public XSLFObjectShape createOleShape(final PictureData pictureData) {
        if (!(pictureData instanceof XSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type XSLFPictureData");
        }
        final POIXMLDocumentPart.RelationPart rp = this.getSheet().addRelation(null, XSLFRelation.IMAGES, (POIXMLDocumentPart)pictureData);
        final XSLFObjectShape sh = this.getDrawing().createOleShape(rp.getRelationship().getId());
        final CTOleObject oleObj = sh.getCTOleObject();
        final Dimension dim = pictureData.getImageDimension();
        oleObj.setImgW(Units.toEMU(dim.getWidth()));
        oleObj.setImgH(Units.toEMU(dim.getHeight()));
        this.getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }
    
    public XSLFTable createTable() {
        final XSLFTable sh = this.getDrawing().createTable();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }
    
    public XSLFTable createTable(final int numRows, final int numCols) {
        if (numRows < 1 || numCols < 1) {
            throw new IllegalArgumentException("numRows and numCols must be greater than 0");
        }
        final XSLFTable sh = this.getDrawing().createTable();
        this._shapes.add(sh);
        sh.setParent(this);
        for (int r = 0; r < numRows; ++r) {
            final XSLFTableRow row = sh.addRow();
            for (int c = 0; c < numCols; ++c) {
                row.addCell();
            }
        }
        return sh;
    }
    
    public void setFlipHorizontal(final boolean flip) {
        this.getSafeXfrm().setFlipH(flip);
    }
    
    public void setFlipVertical(final boolean flip) {
        this.getSafeXfrm().setFlipV(flip);
    }
    
    public boolean getFlipHorizontal() {
        final CTGroupTransform2D xfrm = this.getXfrm();
        return xfrm != null && xfrm.isSetFlipH() && xfrm.getFlipH();
    }
    
    public boolean getFlipVertical() {
        final CTGroupTransform2D xfrm = this.getXfrm();
        return xfrm != null && xfrm.isSetFlipV() && xfrm.getFlipV();
    }
    
    public void setRotation(final double theta) {
        this.getSafeXfrm().setRot((int)(theta * 60000.0));
    }
    
    public double getRotation() {
        final CTGroupTransform2D xfrm = this.getXfrm();
        return (xfrm == null || !xfrm.isSetRot()) ? 0.0 : (xfrm.getRot() / 60000.0);
    }
    
    @Override
    void copy(final XSLFShape src) {
        final XSLFGroupShape gr = (XSLFGroupShape)src;
        final List<XSLFShape> tgtShapes = this.getShapes();
        final List<XSLFShape> srcShapes = gr.getShapes();
        if (tgtShapes.size() == srcShapes.size()) {
            for (int i = 0; i < tgtShapes.size(); ++i) {
                final XSLFShape s1 = srcShapes.get(i);
                final XSLFShape s2 = tgtShapes.get(i);
                s2.copy(s1);
            }
        }
        else {
            this.clear();
            for (final XSLFShape shape : srcShapes) {
                XSLFShape newShape;
                if (shape instanceof XSLFTextBox) {
                    newShape = this.createTextBox();
                }
                else if (shape instanceof XSLFFreeformShape) {
                    newShape = this.createFreeform();
                }
                else if (shape instanceof XSLFAutoShape) {
                    newShape = this.createAutoShape();
                }
                else if (shape instanceof XSLFConnectorShape) {
                    newShape = this.createConnector();
                }
                else if (shape instanceof XSLFPictureShape) {
                    final XSLFPictureShape p = (XSLFPictureShape)shape;
                    final XSLFPictureData pd = p.getPictureData();
                    final XSLFPictureData pdNew = this.getSheet().getSlideShow().addPicture(pd.getData(), pd.getType());
                    newShape = this.createPicture((PictureData)pdNew);
                }
                else if (shape instanceof XSLFGroupShape) {
                    newShape = this.createGroup();
                }
                else {
                    if (!(shape instanceof XSLFTable)) {
                        XSLFGroupShape._logger.log(5, new Object[] { "copying of class " + shape.getClass() + " not supported." });
                        continue;
                    }
                    newShape = this.createTable();
                }
                newShape.copy(shape);
            }
        }
    }
    
    @Override
    public void clear() {
        final List<XSLFShape> shapes = new ArrayList<XSLFShape>(this.getShapes());
        for (final XSLFShape shape : shapes) {
            this.removeShape(shape);
        }
    }
    
    public void addShape(final XSLFShape shape) {
        throw new UnsupportedOperationException("Adding a shape from a different container is not supported - create it from scratch with XSLFGroupShape.create* methods");
    }
    
    static {
        _logger = POILogFactory.getLogger((Class)XSLFGroupShape.class);
    }
}
