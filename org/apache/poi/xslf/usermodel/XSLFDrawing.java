package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import java.awt.Color;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import java.awt.geom.Rectangle2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;

public class XSLFDrawing
{
    private XSLFSheet _sheet;
    private CTGroupShape _spTree;
    
    XSLFDrawing(final XSLFSheet sheet, final CTGroupShape spTree) {
        this._sheet = sheet;
        this._spTree = spTree;
        final XmlObject[] selectPath;
        final XmlObject[] cNvPr = selectPath = sheet.getSpTree().selectPath("declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:cNvPr");
        for (final XmlObject o : selectPath) {
            if (o instanceof CTNonVisualDrawingProps) {
                final CTNonVisualDrawingProps p = (CTNonVisualDrawingProps)o;
                sheet.registerShapeId((int)p.getId());
            }
        }
    }
    
    public XSLFAutoShape createAutoShape() {
        final CTShape sp = this._spTree.addNewSp();
        sp.set((XmlObject)XSLFAutoShape.prototype(this._sheet.allocateShapeId()));
        final XSLFAutoShape shape = new XSLFAutoShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
    
    public XSLFFreeformShape createFreeform() {
        final CTShape sp = this._spTree.addNewSp();
        sp.set((XmlObject)XSLFFreeformShape.prototype(this._sheet.allocateShapeId()));
        final XSLFFreeformShape shape = new XSLFFreeformShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
    
    public XSLFTextBox createTextBox() {
        final CTShape sp = this._spTree.addNewSp();
        sp.set((XmlObject)XSLFTextBox.prototype(this._sheet.allocateShapeId()));
        final XSLFTextBox shape = new XSLFTextBox(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
    
    public XSLFConnectorShape createConnector() {
        final CTConnector sp = this._spTree.addNewCxnSp();
        sp.set((XmlObject)XSLFConnectorShape.prototype(this._sheet.allocateShapeId()));
        final XSLFConnectorShape shape = new XSLFConnectorShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        shape.setLineColor(Color.black);
        shape.setLineWidth(0.75);
        return shape;
    }
    
    public XSLFGroupShape createGroup() {
        final CTGroupShape sp = this._spTree.addNewGrpSp();
        sp.set((XmlObject)XSLFGroupShape.prototype(this._sheet.allocateShapeId()));
        final XSLFGroupShape shape = new XSLFGroupShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
    
    public XSLFPictureShape createPicture(final String rel) {
        final CTPicture sp = this._spTree.addNewPic();
        sp.set((XmlObject)XSLFPictureShape.prototype(this._sheet.allocateShapeId(), rel));
        final XSLFPictureShape shape = new XSLFPictureShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
    
    public XSLFTable createTable() {
        final CTGraphicalObjectFrame sp = this._spTree.addNewGraphicFrame();
        sp.set((XmlObject)XSLFTable.prototype(this._sheet.allocateShapeId()));
        final XSLFTable shape = new XSLFTable(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
    
    public void addChart(final String rID, final Rectangle2D rect2D) {
        final CTGraphicalObjectFrame sp = this._spTree.addNewGraphicFrame();
        sp.set((XmlObject)XSLFChart.prototype(this._sheet.allocateShapeId(), rID, rect2D));
    }
    
    public XSLFObjectShape createOleShape(final String pictureRel) {
        final CTGraphicalObjectFrame sp = this._spTree.addNewGraphicFrame();
        sp.set((XmlObject)XSLFObjectShape.prototype(this._sheet.allocateShapeId(), pictureRel));
        final XSLFObjectShape shape = new XSLFObjectShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
}
