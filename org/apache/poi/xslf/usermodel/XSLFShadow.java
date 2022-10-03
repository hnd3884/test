package org.apache.poi.xslf.usermodel;

import org.apache.poi.sl.usermodel.SimpleShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.draw.DrawPaint;
import java.awt.Color;
import org.apache.poi.util.Units;
import java.awt.geom.Rectangle2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.apache.poi.sl.usermodel.Shadow;

public class XSLFShadow extends XSLFShape implements Shadow<XSLFShape, XSLFTextParagraph>
{
    private XSLFSimpleShape _parent;
    
    XSLFShadow(final CTOuterShadowEffect shape, final XSLFSimpleShape parentShape) {
        super((XmlObject)shape, parentShape.getSheet());
        this._parent = parentShape;
    }
    
    public XSLFSimpleShape getShadowParent() {
        return this._parent;
    }
    
    public Rectangle2D getAnchor() {
        return this._parent.getAnchor();
    }
    
    public void setAnchor(final Rectangle2D anchor) {
        throw new IllegalStateException("You can't set anchor of a shadow");
    }
    
    public double getDistance() {
        final CTOuterShadowEffect ct = (CTOuterShadowEffect)this.getXmlObject();
        return ct.isSetDist() ? Units.toPoints(ct.getDist()) : 0.0;
    }
    
    public double getAngle() {
        final CTOuterShadowEffect ct = (CTOuterShadowEffect)this.getXmlObject();
        return ct.isSetDir() ? (ct.getDir() / 60000.0) : 0.0;
    }
    
    public double getBlur() {
        final CTOuterShadowEffect ct = (CTOuterShadowEffect)this.getXmlObject();
        return ct.isSetBlurRad() ? Units.toPoints(ct.getBlurRad()) : 0.0;
    }
    
    public Color getFillColor() {
        final PaintStyle.SolidPaint ps = this.getFillStyle();
        if (ps == null) {
            return null;
        }
        return DrawPaint.applyColorTransform(ps.getSolidColor());
    }
    
    public PaintStyle.SolidPaint getFillStyle() {
        final XSLFTheme theme = this.getSheet().getTheme();
        final CTOuterShadowEffect ct = (CTOuterShadowEffect)this.getXmlObject();
        if (ct == null) {
            return null;
        }
        final CTSchemeColor phClr = ct.getSchemeClr();
        final XSLFColor xc = new XSLFColor((XmlObject)ct, theme, phClr, this.getSheet());
        return DrawPaint.createSolidPaint(xc.getColorStyle());
    }
}
