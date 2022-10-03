package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import java.awt.Color;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.apache.poi.sl.usermodel.Background;

public class XSLFBackground extends XSLFSimpleShape implements Background<XSLFShape, XSLFTextParagraph>
{
    XSLFBackground(final CTBackground shape, final XSLFSheet sheet) {
        super((XmlObject)shape, sheet);
    }
    
    @Override
    public Rectangle2D getAnchor() {
        final Dimension pg = this.getSheet().getSlideShow().getPageSize();
        return new Rectangle2D.Double(0.0, 0.0, pg.getWidth(), pg.getHeight());
    }
    
    @Override
    protected CTTransform2D getXfrm(final boolean create) {
        return null;
    }
    
    public void setPlaceholder(final Placeholder placeholder) {
        throw new POIXMLException("Can't set a placeholder for a background");
    }
    
    protected CTBackgroundProperties getBgPr(final boolean create) {
        final CTBackground bg = (CTBackground)this.getXmlObject();
        if (!bg.isSetBgPr() && create) {
            if (bg.isSetBgRef()) {
                bg.unsetBgRef();
            }
            return bg.addNewBgPr();
        }
        return bg.getBgPr();
    }
    
    @Override
    public void setFillColor(final Color color) {
        final CTBackgroundProperties bgPr = this.getBgPr(true);
        if (bgPr.isSetBlipFill()) {
            bgPr.unsetBlipFill();
        }
        if (bgPr.isSetGradFill()) {
            bgPr.unsetGradFill();
        }
        if (bgPr.isSetGrpFill()) {
            bgPr.unsetGrpFill();
        }
        if (bgPr.isSetPattFill()) {
            bgPr.unsetPattFill();
        }
        if (color == null) {
            if (bgPr.isSetSolidFill()) {
                bgPr.unsetSolidFill();
            }
            if (!bgPr.isSetNoFill()) {
                bgPr.addNewNoFill();
            }
        }
        else {
            if (bgPr.isSetNoFill()) {
                bgPr.unsetNoFill();
            }
            final CTSolidColorFillProperties fill = bgPr.isSetSolidFill() ? bgPr.getSolidFill() : bgPr.addNewSolidFill();
            final XSLFColor col = new XSLFColor((XmlObject)fill, this.getSheet().getTheme(), fill.getSchemeClr(), this.getSheet());
            col.setColor(color);
        }
    }
    
    protected XmlObject getShapeProperties() {
        final CTBackground bg = (CTBackground)this.getXmlObject();
        if (bg.isSetBgPr()) {
            return (XmlObject)bg.getBgPr();
        }
        if (bg.isSetBgRef()) {
            return (XmlObject)bg.getBgRef();
        }
        return null;
    }
}
