package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.apache.poi.sl.usermodel.Insets2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathShadeType;
import org.apache.poi.sl.usermodel.PaintStyle.GradientPaint;
import org.apache.xmlbeans.XmlObject;
import java.util.Arrays;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.sl.usermodel.PaintStyle;

@Internal
public class XSLFGradientPaint implements PaintStyle.GradientPaint
{
    private final CTGradientFillProperties gradFill;
    final ColorStyle[] cs;
    final float[] fractions;
    
    public XSLFGradientPaint(final CTGradientFillProperties gradFill, final CTSchemeColor phClr, final XSLFTheme theme, final XSLFSheet sheet) {
        this.gradFill = gradFill;
        final CTGradientStop[] gs = (gradFill.getGsLst() == null) ? new CTGradientStop[0] : gradFill.getGsLst().getGsArray();
        Arrays.sort(gs, (o1, o2) -> {
            final int pos1 = o1.getPos();
            final int pos2 = o2.getPos();
            return Integer.compare(pos1, pos2);
        });
        this.cs = new ColorStyle[gs.length];
        this.fractions = new float[gs.length];
        int i = 0;
        for (final CTGradientStop cgs : gs) {
            CTSchemeColor phClrCgs = phClr;
            if (phClrCgs == null && cgs.isSetSchemeClr()) {
                phClrCgs = cgs.getSchemeClr();
            }
            this.cs[i] = new XSLFColor((XmlObject)cgs, theme, phClrCgs, sheet).getColorStyle();
            this.fractions[i] = cgs.getPos() / 100000.0f;
            ++i;
        }
    }
    
    public double getGradientAngle() {
        return this.gradFill.isSetLin() ? (this.gradFill.getLin().getAng() / 60000.0) : 0.0;
    }
    
    public ColorStyle[] getGradientColors() {
        return this.cs;
    }
    
    public float[] getGradientFractions() {
        return this.fractions;
    }
    
    public boolean isRotatedWithShape() {
        return this.gradFill.getRotWithShape();
    }
    
    public GradientPaint.GradientType getGradientType() {
        if (this.gradFill.isSetLin()) {
            return GradientPaint.GradientType.linear;
        }
        if (this.gradFill.isSetPath()) {
            final STPathShadeType.Enum ps = this.gradFill.getPath().getPath();
            if (ps == STPathShadeType.CIRCLE) {
                return GradientPaint.GradientType.circular;
            }
            if (ps == STPathShadeType.SHAPE) {
                return GradientPaint.GradientType.shape;
            }
            if (ps == STPathShadeType.RECT) {
                return GradientPaint.GradientType.rectangular;
            }
        }
        return GradientPaint.GradientType.linear;
    }
    
    public Insets2D getFillToInsets() {
        if (this.gradFill.isSetPath() && this.gradFill.getPath().isSetFillToRect()) {
            final double base = 100000.0;
            final CTRelativeRect rect = this.gradFill.getPath().getFillToRect();
            return new Insets2D(rect.getT() / 100000.0, rect.getL() / 100000.0, rect.getB() / 100000.0, rect.getR() / 100000.0);
        }
        return null;
    }
}
