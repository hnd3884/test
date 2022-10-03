package org.apache.poi.xslf.usermodel;

import java.util.function.Supplier;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.apache.xmlbeans.XmlObject;
import java.util.ArrayList;
import org.apache.poi.sl.usermodel.ColorStyle;
import java.util.List;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.util.Units;
import java.awt.geom.Point2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTileInfoProperties;
import org.apache.poi.util.Dimension2DDouble;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.sl.usermodel.PaintStyle;

@Internal
public class XSLFTexturePaint implements PaintStyle.TexturePaint
{
    private final CTBlipFillProperties blipFill;
    private final PackagePart parentPart;
    private final CTBlip blip;
    private final CTSchemeColor phClr;
    private final XSLFTheme theme;
    private final XSLFSheet sheet;
    
    public XSLFTexturePaint(final CTBlipFillProperties blipFill, final PackagePart parentPart, final CTSchemeColor phClr, final XSLFTheme theme, final XSLFSheet sheet) {
        this.blipFill = blipFill;
        this.parentPart = parentPart;
        this.blip = blipFill.getBlip();
        this.phClr = phClr;
        this.theme = theme;
        this.sheet = sheet;
    }
    
    private PackagePart getPart() {
        try {
            final String blipId = this.blip.getEmbed();
            final PackageRelationship rel = this.parentPart.getRelationship(blipId);
            return this.parentPart.getRelatedPart(rel);
        }
        catch (final InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
    
    public InputStream getImageData() {
        try {
            return this.getPart().getInputStream();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getContentType() {
        if (this.blip == null || !this.blip.isSetEmbed() || this.blip.getEmbed().isEmpty()) {
            return null;
        }
        return this.getPart().getContentType();
    }
    
    public int getAlpha() {
        return (this.blip.sizeOfAlphaModFixArray() > 0) ? this.blip.getAlphaModFixArray(0).getAmt() : 100000;
    }
    
    public boolean isRotatedWithShape() {
        return this.blipFill.isSetRotWithShape() && this.blipFill.getRotWithShape();
    }
    
    public Dimension2D getScale() {
        final CTTileInfoProperties tile = this.blipFill.getTile();
        return (Dimension2D)((tile == null) ? null : new Dimension2DDouble(tile.isSetSx() ? (tile.getSx() / 100000.0) : 1.0, tile.isSetSy() ? (tile.getSy() / 100000.0) : 1.0));
    }
    
    public Point2D getOffset() {
        final CTTileInfoProperties tile = this.blipFill.getTile();
        return (tile == null) ? null : new Point2D.Double(tile.isSetTx() ? Units.toPoints(tile.getTx()) : 0.0, tile.isSetTy() ? Units.toPoints(tile.getTy()) : 0.0);
    }
    
    public PaintStyle.FlipMode getFlipMode() {
        final CTTileInfoProperties tile = this.blipFill.getTile();
        switch ((tile == null || tile.getFlip() == null) ? 1 : tile.getFlip().intValue()) {
            default: {
                return PaintStyle.FlipMode.NONE;
            }
            case 2: {
                return PaintStyle.FlipMode.X;
            }
            case 3: {
                return PaintStyle.FlipMode.Y;
            }
            case 4: {
                return PaintStyle.FlipMode.XY;
            }
        }
    }
    
    public PaintStyle.TextureAlignment getAlignment() {
        final CTTileInfoProperties tile = this.blipFill.getTile();
        return (tile == null || !tile.isSetAlgn()) ? null : PaintStyle.TextureAlignment.fromOoxmlId(tile.getAlgn().toString());
    }
    
    public Insets2D getInsets() {
        return getRectVal(this.blipFill.getSrcRect());
    }
    
    public Insets2D getStretch() {
        return getRectVal(this.blipFill.isSetStretch() ? this.blipFill.getStretch().getFillRect() : null);
    }
    
    public List<ColorStyle> getDuoTone() {
        if (this.blip.sizeOfDuotoneArray() == 0) {
            return null;
        }
        final List<ColorStyle> colors = new ArrayList<ColorStyle>();
        final CTDuotoneEffect duoEff = this.blip.getDuotoneArray(0);
        for (final CTSchemeColor phClrDuo : duoEff.getSchemeClrArray()) {
            colors.add(new XSLFColor((XmlObject)phClrDuo, this.theme, this.phClr, this.sheet).getColorStyle());
        }
        return colors;
    }
    
    private static Insets2D getRectVal(final CTRelativeRect rect) {
        return (rect == null) ? null : new Insets2D((double)getRectVal(rect::isSetT, rect::getT), (double)getRectVal(rect::isSetL, rect::getL), (double)getRectVal(rect::isSetB, rect::getB), (double)getRectVal(rect::isSetR, rect::getR));
    }
    
    private static int getRectVal(final Supplier<Boolean> isSet, final Supplier<Integer> val) {
        return isSet.get() ? val.get() : 0;
    }
}
