package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.PlaceholderDetails;
import org.apache.poi.sl.usermodel.Shadow;
import org.apache.poi.sl.usermodel.Hyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.poi.sl.usermodel.FillStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import org.apache.poi.sl.draw.geom.Guide;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndLength;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndWidth;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType;
import org.apache.poi.sl.usermodel.LineDecoration;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import org.apache.poi.sl.draw.geom.PresetGeometries;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleItem;
import org.apache.poi.sl.draw.DrawPaint;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineCap;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;
import org.openxmlformats.schemas.drawingml.x2006.main.STCompoundLine;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import java.awt.Color;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.poi.util.Units;
import java.awt.geom.Rectangle2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.POILogger;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.apache.poi.sl.usermodel.SimpleShape;

public abstract class XSLFSimpleShape extends XSLFShape implements SimpleShape<XSLFShape, XSLFTextParagraph>
{
    private static CTOuterShadowEffect NO_SHADOW;
    private static final POILogger LOG;
    
    XSLFSimpleShape(final XmlObject shape, final XSLFSheet sheet) {
        super(shape, sheet);
    }
    
    public void setShapeType(final ShapeType type) {
        final XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(this.getShapeProperties());
        if (gp == null) {
            return;
        }
        if (gp.isSetCustGeom()) {
            gp.unsetCustGeom();
        }
        final CTPresetGeometry2D prst = gp.isSetPrstGeom() ? gp.getPrstGeom() : gp.addNewPrstGeom();
        prst.setPrst(STShapeType.Enum.forInt(type.ooxmlId));
    }
    
    public ShapeType getShapeType() {
        final XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(this.getShapeProperties());
        if (gp != null && gp.isSetPrstGeom()) {
            final STShapeType.Enum geom = gp.getPrstGeom().getPrst();
            if (geom != null) {
                return ShapeType.forId(geom.intValue(), true);
            }
        }
        return null;
    }
    
    protected CTTransform2D getXfrm(final boolean create) {
        final PropertyFetcher<CTTransform2D> fetcher = new PropertyFetcher<CTTransform2D>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final XmlObject xo = shape.getShapeProperties();
                if (xo instanceof CTShapeProperties && ((CTShapeProperties)xo).isSetXfrm()) {
                    this.setValue(((CTShapeProperties)xo).getXfrm());
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        final CTTransform2D xfrm = fetcher.getValue();
        if (!create || xfrm != null) {
            return xfrm;
        }
        final XmlObject xo = this.getShapeProperties();
        if (xo instanceof CTShapeProperties) {
            return ((CTShapeProperties)xo).addNewXfrm();
        }
        XSLFSimpleShape.LOG.log(5, new Object[] { this.getClass() + " doesn't have xfrm element." });
        return null;
    }
    
    public Rectangle2D getAnchor() {
        final CTTransform2D xfrm = this.getXfrm(false);
        if (xfrm == null || !xfrm.isSetOff()) {
            return null;
        }
        final CTPoint2D off = xfrm.getOff();
        final double x = Units.toPoints(off.getX());
        final double y = Units.toPoints(off.getY());
        final CTPositiveSize2D ext = xfrm.getExt();
        final double cx = Units.toPoints(ext.getCx());
        final double cy = Units.toPoints(ext.getCy());
        return new Rectangle2D.Double(x, y, cx, cy);
    }
    
    public void setAnchor(final Rectangle2D anchor) {
        final CTTransform2D xfrm = this.getXfrm(true);
        if (xfrm == null) {
            return;
        }
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
    
    public void setRotation(final double theta) {
        final CTTransform2D xfrm = this.getXfrm(true);
        if (xfrm != null) {
            xfrm.setRot((int)(theta * 60000.0));
        }
    }
    
    public double getRotation() {
        final CTTransform2D xfrm = this.getXfrm(false);
        return (xfrm == null || !xfrm.isSetRot()) ? 0.0 : (xfrm.getRot() / 60000.0);
    }
    
    public void setFlipHorizontal(final boolean flip) {
        final CTTransform2D xfrm = this.getXfrm(true);
        if (xfrm != null) {
            xfrm.setFlipH(flip);
        }
    }
    
    public void setFlipVertical(final boolean flip) {
        final CTTransform2D xfrm = this.getXfrm(true);
        if (xfrm != null) {
            xfrm.setFlipV(flip);
        }
    }
    
    public boolean getFlipHorizontal() {
        final CTTransform2D xfrm = this.getXfrm(false);
        return xfrm != null && xfrm.isSetFlipH() && xfrm.getFlipH();
    }
    
    public boolean getFlipVertical() {
        final CTTransform2D xfrm = this.getXfrm(false);
        return xfrm != null && xfrm.isSetFlipV() && xfrm.getFlipV();
    }
    
    private CTLineProperties getDefaultLineProperties() {
        final CTShapeStyle style = this.getSpStyle();
        if (style == null) {
            return null;
        }
        final CTStyleMatrixReference lnRef = style.getLnRef();
        if (lnRef == null) {
            return null;
        }
        final int idx = Math.toIntExact(lnRef.getIdx());
        final XSLFTheme theme = this.getSheet().getTheme();
        if (theme == null) {
            return null;
        }
        final CTBaseStyles styles = theme.getXmlObject().getThemeElements();
        if (styles == null) {
            return null;
        }
        final CTStyleMatrix styleMatrix = styles.getFmtScheme();
        if (styleMatrix == null) {
            return null;
        }
        final CTLineStyleList lineStyles = styleMatrix.getLnStyleLst();
        if (lineStyles == null || lineStyles.sizeOfLnArray() < idx) {
            return null;
        }
        return lineStyles.getLnArray(idx - 1);
    }
    
    public void setLineColor(final Color color) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (ln.isSetSolidFill()) {
            ln.unsetSolidFill();
        }
        if (ln.isSetGradFill()) {
            ln.unsetGradFill();
        }
        if (ln.isSetPattFill()) {
            ln.unsetPattFill();
        }
        if (ln.isSetNoFill()) {
            ln.unsetNoFill();
        }
        if (color == null) {
            ln.addNewNoFill();
        }
        else {
            final CTSolidColorFillProperties fill = ln.addNewSolidFill();
            final XSLFColor col = new XSLFColor((XmlObject)fill, this.getSheet().getTheme(), fill.getSchemeClr(), this.getSheet());
            col.setColor(color);
        }
    }
    
    public Color getLineColor() {
        final PaintStyle ps = this.getLinePaint();
        if (ps instanceof PaintStyle.SolidPaint) {
            return ((PaintStyle.SolidPaint)ps).getSolidColor().getColor();
        }
        return null;
    }
    
    protected PaintStyle getLinePaint() {
        final XSLFSheet sheet = this.getSheet();
        final XSLFTheme theme = sheet.getTheme();
        final boolean hasPlaceholder = this.getPlaceholder() != null;
        final PropertyFetcher<PaintStyle> fetcher = new PropertyFetcher<PaintStyle>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final CTLineProperties spPr = getLn(shape, false);
                XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate((XmlObject)spPr);
                if (fp != null && fp.isSetNoFill()) {
                    this.setValue(null);
                    return true;
                }
                final PackagePart pp = shape.getSheet().getPackagePart();
                PaintStyle paint = XSLFSimpleShape.this.selectPaint(fp, null, pp, theme, hasPlaceholder);
                if (paint != null) {
                    this.setValue(paint);
                    return true;
                }
                final CTShapeStyle style = shape.getSpStyle();
                if (style != null) {
                    fp = XSLFPropertiesDelegate.getFillDelegate((XmlObject)style.getLnRef());
                    paint = XSLFSimpleShape.this.selectPaint(fp, null, pp, theme, hasPlaceholder);
                    if (paint == null) {
                        paint = this.getThemePaint(style, pp);
                    }
                }
                if (paint != null) {
                    this.setValue(paint);
                    return true;
                }
                return false;
            }
            
            PaintStyle getThemePaint(final CTShapeStyle style, final PackagePart pp) {
                final CTStyleMatrixReference lnRef = style.getLnRef();
                if (lnRef == null) {
                    return null;
                }
                final int idx = Math.toIntExact(lnRef.getIdx());
                final CTSchemeColor phClr = lnRef.getSchemeClr();
                if (idx <= 0) {
                    return null;
                }
                final CTLineProperties props = theme.getXmlObject().getThemeElements().getFmtScheme().getLnStyleLst().getLnArray(idx - 1);
                final XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate((XmlObject)props);
                return XSLFSimpleShape.this.selectPaint(fp, phClr, pp, theme, hasPlaceholder);
            }
        };
        this.fetchShapeProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setLineWidth(final double width) {
        final CTLineProperties lnPr = getLn(this, true);
        if (lnPr == null) {
            return;
        }
        if (width == 0.0) {
            if (lnPr.isSetW()) {
                lnPr.unsetW();
            }
            if (!lnPr.isSetNoFill()) {
                lnPr.addNewNoFill();
            }
            if (lnPr.isSetSolidFill()) {
                lnPr.unsetSolidFill();
            }
            if (lnPr.isSetGradFill()) {
                lnPr.unsetGradFill();
            }
            if (lnPr.isSetPattFill()) {
                lnPr.unsetPattFill();
            }
        }
        else {
            if (lnPr.isSetNoFill()) {
                lnPr.unsetNoFill();
            }
            lnPr.setW(Units.toEMU(width));
        }
    }
    
    public double getLineWidth() {
        final PropertyFetcher<Double> fetcher = new PropertyFetcher<Double>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final CTLineProperties ln = getLn(shape, false);
                if (ln != null) {
                    if (ln.isSetNoFill()) {
                        this.setValue(0.0);
                        return true;
                    }
                    if (ln.isSetW()) {
                        this.setValue(Units.toPoints((long)ln.getW()));
                        return true;
                    }
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        double lineWidth = 0.0;
        if (fetcher.getValue() == null) {
            final CTLineProperties defaultLn = this.getDefaultLineProperties();
            if (defaultLn != null && defaultLn.isSetW()) {
                lineWidth = Units.toPoints((long)defaultLn.getW());
            }
        }
        else {
            lineWidth = fetcher.getValue();
        }
        return lineWidth;
    }
    
    public void setLineCompound(final StrokeStyle.LineCompound compound) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (compound == null) {
            if (ln.isSetCmpd()) {
                ln.unsetCmpd();
            }
        }
        else {
            STCompoundLine.Enum xCmpd = null;
            switch (compound) {
                default: {
                    xCmpd = STCompoundLine.SNG;
                    break;
                }
                case DOUBLE: {
                    xCmpd = STCompoundLine.DBL;
                    break;
                }
                case THICK_THIN: {
                    xCmpd = STCompoundLine.THICK_THIN;
                    break;
                }
                case THIN_THICK: {
                    xCmpd = STCompoundLine.THIN_THICK;
                    break;
                }
                case TRIPLE: {
                    xCmpd = STCompoundLine.TRI;
                    break;
                }
            }
            ln.setCmpd(xCmpd);
        }
    }
    
    public StrokeStyle.LineCompound getLineCompound() {
        final PropertyFetcher<Integer> fetcher = new PropertyFetcher<Integer>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final CTLineProperties ln = getLn(shape, false);
                if (ln != null) {
                    final STCompoundLine.Enum stCmpd = ln.getCmpd();
                    if (stCmpd != null) {
                        this.setValue(stCmpd.intValue());
                        return true;
                    }
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        final Integer cmpd = fetcher.getValue();
        if (cmpd == null) {
            final CTLineProperties defaultLn = this.getDefaultLineProperties();
            if (defaultLn != null && defaultLn.isSetCmpd()) {
                switch (defaultLn.getCmpd().intValue()) {
                    default: {
                        return StrokeStyle.LineCompound.SINGLE;
                    }
                    case 2: {
                        return StrokeStyle.LineCompound.DOUBLE;
                    }
                    case 3: {
                        return StrokeStyle.LineCompound.THICK_THIN;
                    }
                    case 4: {
                        return StrokeStyle.LineCompound.THIN_THICK;
                    }
                    case 5: {
                        return StrokeStyle.LineCompound.TRIPLE;
                    }
                }
            }
        }
        return null;
    }
    
    public void setLineDash(final StrokeStyle.LineDash dash) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (dash == null) {
            if (ln.isSetPrstDash()) {
                ln.unsetPrstDash();
            }
        }
        else {
            final CTPresetLineDashProperties ldp = ln.isSetPrstDash() ? ln.getPrstDash() : ln.addNewPrstDash();
            ldp.setVal(STPresetLineDashVal.Enum.forInt(dash.ooxmlId));
        }
    }
    
    public StrokeStyle.LineDash getLineDash() {
        final PropertyFetcher<StrokeStyle.LineDash> fetcher = new PropertyFetcher<StrokeStyle.LineDash>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final CTLineProperties ln = getLn(shape, false);
                if (ln == null || !ln.isSetPrstDash()) {
                    return false;
                }
                this.setValue(StrokeStyle.LineDash.fromOoxmlId(ln.getPrstDash().getVal().intValue()));
                return true;
            }
        };
        this.fetchShapeProperty(fetcher);
        StrokeStyle.LineDash dash = fetcher.getValue();
        if (dash == null) {
            final CTLineProperties defaultLn = this.getDefaultLineProperties();
            if (defaultLn != null && defaultLn.isSetPrstDash()) {
                dash = StrokeStyle.LineDash.fromOoxmlId(defaultLn.getPrstDash().getVal().intValue());
            }
        }
        return dash;
    }
    
    public void setLineCap(final StrokeStyle.LineCap cap) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (cap == null) {
            if (ln.isSetCap()) {
                ln.unsetCap();
            }
        }
        else {
            ln.setCap(STLineCap.Enum.forInt(cap.ooxmlId));
        }
    }
    
    public StrokeStyle.LineCap getLineCap() {
        final PropertyFetcher<StrokeStyle.LineCap> fetcher = new PropertyFetcher<StrokeStyle.LineCap>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final CTLineProperties ln = getLn(shape, false);
                if (ln != null && ln.isSetCap()) {
                    this.setValue(StrokeStyle.LineCap.fromOoxmlId(ln.getCap().intValue()));
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        StrokeStyle.LineCap cap = fetcher.getValue();
        if (cap == null) {
            final CTLineProperties defaultLn = this.getDefaultLineProperties();
            if (defaultLn != null && defaultLn.isSetCap()) {
                cap = StrokeStyle.LineCap.fromOoxmlId(defaultLn.getCap().intValue());
            }
        }
        return cap;
    }
    
    public void setFillColor(final Color color) {
        final XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(this.getShapeProperties());
        if (fp == null) {
            return;
        }
        if (color == null) {
            if (fp.isSetSolidFill()) {
                fp.unsetSolidFill();
            }
            if (fp.isSetGradFill()) {
                fp.unsetGradFill();
            }
            if (fp.isSetPattFill()) {
                fp.unsetGradFill();
            }
            if (fp.isSetBlipFill()) {
                fp.unsetBlipFill();
            }
            if (!fp.isSetNoFill()) {
                fp.addNewNoFill();
            }
        }
        else {
            if (fp.isSetNoFill()) {
                fp.unsetNoFill();
            }
            final CTSolidColorFillProperties fill = fp.isSetSolidFill() ? fp.getSolidFill() : fp.addNewSolidFill();
            final XSLFColor col = new XSLFColor((XmlObject)fill, this.getSheet().getTheme(), fill.getSchemeClr(), this.getSheet());
            col.setColor(color);
        }
    }
    
    public Color getFillColor() {
        final PaintStyle ps = this.getFillPaint();
        if (ps instanceof PaintStyle.SolidPaint) {
            return DrawPaint.applyColorTransform(((PaintStyle.SolidPaint)ps).getSolidColor());
        }
        return null;
    }
    
    public XSLFShadow getShadow() {
        final PropertyFetcher<CTOuterShadowEffect> fetcher = new PropertyFetcher<CTOuterShadowEffect>() {
            @Override
            public boolean fetch(final XSLFShape shape) {
                final XSLFPropertiesDelegate.XSLFEffectProperties ep = XSLFPropertiesDelegate.getEffectDelegate(shape.getShapeProperties());
                if (ep != null && ep.isSetEffectLst()) {
                    final CTOuterShadowEffect obj = ep.getEffectLst().getOuterShdw();
                    this.setValue((obj == null) ? XSLFSimpleShape.NO_SHADOW : obj);
                    return true;
                }
                return false;
            }
        };
        this.fetchShapeProperty(fetcher);
        CTOuterShadowEffect obj = fetcher.getValue();
        if (obj == null) {
            final CTShapeStyle style = this.getSpStyle();
            if (style != null && style.getEffectRef() != null) {
                final int idx = (int)style.getEffectRef().getIdx();
                if (idx != 0) {
                    final CTStyleMatrix styleMatrix = this.getSheet().getTheme().getXmlObject().getThemeElements().getFmtScheme();
                    final CTEffectStyleItem ef = styleMatrix.getEffectStyleLst().getEffectStyleArray(idx - 1);
                    obj = ef.getEffectLst().getOuterShdw();
                }
            }
        }
        return (obj == null || obj == XSLFSimpleShape.NO_SHADOW) ? null : new XSLFShadow(obj, this);
    }
    
    public CustomGeometry getGeometry() {
        final XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(this.getShapeProperties());
        if (gp == null) {
            return null;
        }
        final PresetGeometries dict = PresetGeometries.getInstance();
        CustomGeometry geom;
        if (gp.isSetPrstGeom()) {
            final String name = gp.getPrstGeom().getPrst().toString();
            geom = (CustomGeometry)dict.get((Object)name);
            if (geom == null) {
                throw new IllegalStateException("Unknown shape geometry: " + name + ", available geometries are: " + dict.keySet());
            }
        }
        else if (gp.isSetCustGeom()) {
            final XMLStreamReader staxReader = gp.getCustGeom().newXMLStreamReader();
            geom = PresetGeometries.convertCustomGeometry(staxReader);
            try {
                staxReader.close();
            }
            catch (final XMLStreamException e) {
                XSLFSimpleShape.LOG.log(5, new Object[] { "An error occurred while closing a Custom Geometry XML Stream Reader: " + e.getMessage() });
            }
        }
        else {
            geom = (CustomGeometry)dict.get((Object)"rect");
        }
        return geom;
    }
    
    @Override
    void copy(final XSLFShape sh) {
        super.copy(sh);
        final XSLFSimpleShape s = (XSLFSimpleShape)sh;
        final Color srsSolidFill = s.getFillColor();
        final Color tgtSoliFill = this.getFillColor();
        if (srsSolidFill != null && !srsSolidFill.equals(tgtSoliFill)) {
            this.setFillColor(srsSolidFill);
        }
        final XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(this.getShapeProperties());
        if (fp != null && fp.isSetBlipFill()) {
            final CTBlip blip = fp.getBlipFill().getBlip();
            final String blipId = blip.getEmbed();
            final String relId = this.getSheet().importBlip(blipId, s.getSheet());
            blip.setEmbed(relId);
        }
        final Color srcLineColor = s.getLineColor();
        final Color tgtLineColor = this.getLineColor();
        if (srcLineColor != null && !srcLineColor.equals(tgtLineColor)) {
            this.setLineColor(srcLineColor);
        }
        final double srcLineWidth = s.getLineWidth();
        final double tgtLineWidth = this.getLineWidth();
        if (srcLineWidth != tgtLineWidth) {
            this.setLineWidth(srcLineWidth);
        }
        final StrokeStyle.LineDash srcLineDash = s.getLineDash();
        final StrokeStyle.LineDash tgtLineDash = this.getLineDash();
        if (srcLineDash != null && srcLineDash != tgtLineDash) {
            this.setLineDash(srcLineDash);
        }
        final StrokeStyle.LineCap srcLineCap = s.getLineCap();
        final StrokeStyle.LineCap tgtLineCap = this.getLineCap();
        if (srcLineCap != null && srcLineCap != tgtLineCap) {
            this.setLineCap(srcLineCap);
        }
    }
    
    public void setLineHeadDecoration(final LineDecoration.DecorationShape style) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        final CTLineEndProperties lnEnd = ln.isSetHeadEnd() ? ln.getHeadEnd() : ln.addNewHeadEnd();
        if (style == null) {
            if (lnEnd.isSetType()) {
                lnEnd.unsetType();
            }
        }
        else {
            lnEnd.setType(STLineEndType.Enum.forInt(style.ooxmlId));
        }
    }
    
    public LineDecoration.DecorationShape getLineHeadDecoration() {
        final CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationShape ds = LineDecoration.DecorationShape.NONE;
        if (ln != null && ln.isSetHeadEnd() && ln.getHeadEnd().isSetType()) {
            ds = LineDecoration.DecorationShape.fromOoxmlId(ln.getHeadEnd().getType().intValue());
        }
        return ds;
    }
    
    public void setLineHeadWidth(final LineDecoration.DecorationSize style) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        final CTLineEndProperties lnEnd = ln.isSetHeadEnd() ? ln.getHeadEnd() : ln.addNewHeadEnd();
        if (style == null) {
            if (lnEnd.isSetW()) {
                lnEnd.unsetW();
            }
        }
        else {
            lnEnd.setW(STLineEndWidth.Enum.forInt(style.ooxmlId));
        }
    }
    
    public LineDecoration.DecorationSize getLineHeadWidth() {
        final CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetHeadEnd() && ln.getHeadEnd().isSetW()) {
            ds = LineDecoration.DecorationSize.fromOoxmlId(ln.getHeadEnd().getW().intValue());
        }
        return ds;
    }
    
    public void setLineHeadLength(final LineDecoration.DecorationSize style) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        final CTLineEndProperties lnEnd = ln.isSetHeadEnd() ? ln.getHeadEnd() : ln.addNewHeadEnd();
        if (style == null) {
            if (lnEnd.isSetLen()) {
                lnEnd.unsetLen();
            }
        }
        else {
            lnEnd.setLen(STLineEndLength.Enum.forInt(style.ooxmlId));
        }
    }
    
    public LineDecoration.DecorationSize getLineHeadLength() {
        final CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetHeadEnd() && ln.getHeadEnd().isSetLen()) {
            ds = LineDecoration.DecorationSize.fromOoxmlId(ln.getHeadEnd().getLen().intValue());
        }
        return ds;
    }
    
    public void setLineTailDecoration(final LineDecoration.DecorationShape style) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        final CTLineEndProperties lnEnd = ln.isSetTailEnd() ? ln.getTailEnd() : ln.addNewTailEnd();
        if (style == null) {
            if (lnEnd.isSetType()) {
                lnEnd.unsetType();
            }
        }
        else {
            lnEnd.setType(STLineEndType.Enum.forInt(style.ooxmlId));
        }
    }
    
    public LineDecoration.DecorationShape getLineTailDecoration() {
        final CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationShape ds = LineDecoration.DecorationShape.NONE;
        if (ln != null && ln.isSetTailEnd() && ln.getTailEnd().isSetType()) {
            ds = LineDecoration.DecorationShape.fromOoxmlId(ln.getTailEnd().getType().intValue());
        }
        return ds;
    }
    
    public void setLineTailWidth(final LineDecoration.DecorationSize style) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        final CTLineEndProperties lnEnd = ln.isSetTailEnd() ? ln.getTailEnd() : ln.addNewTailEnd();
        if (style == null) {
            if (lnEnd.isSetW()) {
                lnEnd.unsetW();
            }
        }
        else {
            lnEnd.setW(STLineEndWidth.Enum.forInt(style.ooxmlId));
        }
    }
    
    public LineDecoration.DecorationSize getLineTailWidth() {
        final CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetTailEnd() && ln.getTailEnd().isSetW()) {
            ds = LineDecoration.DecorationSize.fromOoxmlId(ln.getTailEnd().getW().intValue());
        }
        return ds;
    }
    
    public void setLineTailLength(final LineDecoration.DecorationSize style) {
        final CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        final CTLineEndProperties lnEnd = ln.isSetTailEnd() ? ln.getTailEnd() : ln.addNewTailEnd();
        if (style == null) {
            if (lnEnd.isSetLen()) {
                lnEnd.unsetLen();
            }
        }
        else {
            lnEnd.setLen(STLineEndLength.Enum.forInt(style.ooxmlId));
        }
    }
    
    public LineDecoration.DecorationSize getLineTailLength() {
        final CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetTailEnd() && ln.getTailEnd().isSetLen()) {
            ds = LineDecoration.DecorationSize.fromOoxmlId(ln.getTailEnd().getLen().intValue());
        }
        return ds;
    }
    
    public Guide getAdjustValue(final String name) {
        final XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(this.getShapeProperties());
        if (gp != null && gp.isSetPrstGeom() && gp.getPrstGeom().isSetAvLst()) {
            for (final CTGeomGuide g : gp.getPrstGeom().getAvLst().getGdArray()) {
                if (g.getName().equals(name)) {
                    return new Guide(g.getName(), g.getFmla());
                }
            }
        }
        return null;
    }
    
    public LineDecoration getLineDecoration() {
        return (LineDecoration)new LineDecoration() {
            public LineDecoration.DecorationShape getHeadShape() {
                return XSLFSimpleShape.this.getLineHeadDecoration();
            }
            
            public LineDecoration.DecorationSize getHeadWidth() {
                return XSLFSimpleShape.this.getLineHeadWidth();
            }
            
            public LineDecoration.DecorationSize getHeadLength() {
                return XSLFSimpleShape.this.getLineHeadLength();
            }
            
            public LineDecoration.DecorationShape getTailShape() {
                return XSLFSimpleShape.this.getLineTailDecoration();
            }
            
            public LineDecoration.DecorationSize getTailWidth() {
                return XSLFSimpleShape.this.getLineTailWidth();
            }
            
            public LineDecoration.DecorationSize getTailLength() {
                return XSLFSimpleShape.this.getLineTailLength();
            }
        };
    }
    
    public FillStyle getFillStyle() {
        return this::getFillPaint;
    }
    
    public StrokeStyle getStrokeStyle() {
        return (StrokeStyle)new StrokeStyle() {
            public PaintStyle getPaint() {
                return XSLFSimpleShape.this.getLinePaint();
            }
            
            public StrokeStyle.LineCap getLineCap() {
                return XSLFSimpleShape.this.getLineCap();
            }
            
            public StrokeStyle.LineDash getLineDash() {
                return XSLFSimpleShape.this.getLineDash();
            }
            
            public double getLineWidth() {
                return XSLFSimpleShape.this.getLineWidth();
            }
            
            public StrokeStyle.LineCompound getLineCompound() {
                return XSLFSimpleShape.this.getLineCompound();
            }
        };
    }
    
    public void setStrokeStyle(final Object... styles) {
        if (styles.length == 0) {
            this.setLineColor(null);
            return;
        }
        for (final Object st : styles) {
            if (st instanceof Number) {
                this.setLineWidth(((Number)st).doubleValue());
            }
            else if (st instanceof StrokeStyle.LineCap) {
                this.setLineCap((StrokeStyle.LineCap)st);
            }
            else if (st instanceof StrokeStyle.LineDash) {
                this.setLineDash((StrokeStyle.LineDash)st);
            }
            else if (st instanceof StrokeStyle.LineCompound) {
                this.setLineCompound((StrokeStyle.LineCompound)st);
            }
            else if (st instanceof Color) {
                this.setLineColor((Color)st);
            }
        }
    }
    
    public XSLFHyperlink getHyperlink() {
        final CTNonVisualDrawingProps cNvPr = this.getCNvPr();
        if (!cNvPr.isSetHlinkClick()) {
            return null;
        }
        return new XSLFHyperlink(cNvPr.getHlinkClick(), this.getSheet());
    }
    
    public XSLFHyperlink createHyperlink() {
        XSLFHyperlink hl = this.getHyperlink();
        if (hl == null) {
            final CTNonVisualDrawingProps cNvPr = this.getCNvPr();
            hl = new XSLFHyperlink(cNvPr.addNewHlinkClick(), this.getSheet());
        }
        return hl;
    }
    
    private static CTLineProperties getLn(final XSLFShape shape, final boolean create) {
        final XmlObject pr = shape.getShapeProperties();
        if (!(pr instanceof CTShapeProperties)) {
            XSLFSimpleShape.LOG.log(5, new Object[] { shape.getClass() + " doesn't have line properties" });
            return null;
        }
        final CTShapeProperties spr = (CTShapeProperties)pr;
        return (spr.isSetLn() || !create) ? spr.getLn() : spr.addNewLn();
    }
    
    static {
        XSLFSimpleShape.NO_SHADOW = CTOuterShadowEffect.Factory.newInstance();
        LOG = POILogFactory.getLogger((Class)XSLFSimpleShape.class);
    }
}
