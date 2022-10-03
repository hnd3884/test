package org.apache.poi.xssf.usermodel;

import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.apache.poi.ss.usermodel.ShapeContainer;

public final class XSSFShapeGroup extends XSSFShape implements ShapeContainer<XSSFShape>
{
    private static CTGroupShape prototype;
    private CTGroupShape ctGroup;
    
    protected XSSFShapeGroup(final XSSFDrawing drawing, final CTGroupShape ctGroup) {
        this.drawing = drawing;
        this.ctGroup = ctGroup;
    }
    
    protected static CTGroupShape prototype() {
        if (XSSFShapeGroup.prototype == null) {
            final CTGroupShape shape = CTGroupShape.Factory.newInstance();
            final CTGroupShapeNonVisual nv = shape.addNewNvGrpSpPr();
            final CTNonVisualDrawingProps nvpr = nv.addNewCNvPr();
            nvpr.setId(0L);
            nvpr.setName("Group 0");
            nv.addNewCNvGrpSpPr();
            final CTGroupShapeProperties sp = shape.addNewGrpSpPr();
            final CTGroupTransform2D t2d = sp.addNewXfrm();
            final CTPositiveSize2D p1 = t2d.addNewExt();
            p1.setCx(0L);
            p1.setCy(0L);
            final CTPoint2D p2 = t2d.addNewOff();
            p2.setX(0L);
            p2.setY(0L);
            final CTPositiveSize2D p3 = t2d.addNewChExt();
            p3.setCx(0L);
            p3.setCy(0L);
            final CTPoint2D p4 = t2d.addNewChOff();
            p4.setX(0L);
            p4.setY(0L);
            XSSFShapeGroup.prototype = shape;
        }
        return XSSFShapeGroup.prototype;
    }
    
    public XSSFTextBox createTextbox(final XSSFChildAnchor anchor) {
        final CTShape ctShape = this.ctGroup.addNewSp();
        ctShape.set((XmlObject)XSSFSimpleShape.prototype());
        final XSSFTextBox shape = new XSSFTextBox(this.getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.setXfrm(anchor.getCTTransform2D());
        return shape;
    }
    
    public XSSFSimpleShape createSimpleShape(final XSSFChildAnchor anchor) {
        final CTShape ctShape = this.ctGroup.addNewSp();
        ctShape.set((XmlObject)XSSFSimpleShape.prototype());
        final XSSFSimpleShape shape = new XSSFSimpleShape(this.getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.setXfrm(anchor.getCTTransform2D());
        return shape;
    }
    
    public XSSFConnector createConnector(final XSSFChildAnchor anchor) {
        final CTConnector ctShape = this.ctGroup.addNewCxnSp();
        ctShape.set((XmlObject)XSSFConnector.prototype());
        final XSSFConnector shape = new XSSFConnector(this.getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.getCTConnector().getSpPr().setXfrm(anchor.getCTTransform2D());
        return shape;
    }
    
    public XSSFPicture createPicture(final XSSFClientAnchor anchor, final int pictureIndex) {
        final PackageRelationship rel = this.getDrawing().addPictureReference(pictureIndex);
        final CTPicture ctShape = this.ctGroup.addNewPic();
        ctShape.set((XmlObject)XSSFPicture.prototype());
        final XSSFPicture shape = new XSSFPicture(this.getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.setPictureReference(rel);
        return shape;
    }
    
    public XSSFShapeGroup createGroup(final XSSFChildAnchor anchor) {
        final CTGroupShape ctShape = this.ctGroup.addNewGrpSp();
        ctShape.set((XmlObject)prototype());
        final XSSFShapeGroup shape = new XSSFShapeGroup(this.getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        final CTGroupTransform2D xfrm = shape.getCTGroupShape().getGrpSpPr().getXfrm();
        final CTTransform2D t2 = anchor.getCTTransform2D();
        xfrm.setOff(t2.getOff());
        xfrm.setExt(t2.getExt());
        xfrm.setChExt(t2.getExt());
        xfrm.setFlipH(t2.getFlipH());
        xfrm.setFlipV(t2.getFlipV());
        return shape;
    }
    
    @Internal
    public CTGroupShape getCTGroupShape() {
        return this.ctGroup;
    }
    
    public void setCoordinates(final int x1, final int y1, final int x2, final int y2) {
        final CTGroupTransform2D t2d = this.ctGroup.getGrpSpPr().getXfrm();
        final CTPoint2D off = t2d.getOff();
        off.setX((long)x1);
        off.setY((long)y1);
        final CTPositiveSize2D ext = t2d.getExt();
        ext.setCx((long)x2);
        ext.setCy((long)y2);
        final CTPoint2D chOff = t2d.getChOff();
        chOff.setX((long)x1);
        chOff.setY((long)y1);
        final CTPositiveSize2D chExt = t2d.getChExt();
        chExt.setCx((long)x2);
        chExt.setCy((long)y2);
    }
    
    @Override
    protected CTShapeProperties getShapeProperties() {
        throw new IllegalStateException("Not supported for shape group");
    }
    
    public Iterator<XSSFShape> iterator() {
        return this.getDrawing().getShapes(this).iterator();
    }
    
    public String getShapeName() {
        return this.ctGroup.getNvGrpSpPr().getCNvPr().getName();
    }
}
