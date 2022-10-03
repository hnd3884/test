package com.octo.captcha.component.image.textpaster.glyphsvisitor;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import com.octo.captcha.component.image.textpaster.Glyphs;

public class OverlapGlyphsUsingShapeVisitor extends OverlapGlyphsVisitor
{
    private double overlapPixels;
    
    public OverlapGlyphsUsingShapeVisitor(final double overlapPixels) {
        super(0.0);
        this.overlapPixels = overlapPixels;
    }
    
    @Override
    public void visit(final Glyphs gv, final Rectangle2D backroundBounds) {
        for (int i = 1; i < gv.size(); ++i) {
            if (this.isSidingOverlapProblem(gv, i)) {
                gv.translate(i, this.getAdjustSidingPosition(gv, i), 0.0);
            }
            else {
                gv.translate(i, this.getSidingPosition(gv, i), 0.0);
                if (this.mayGlyphsOverlapAtIndex(gv, i)) {
                    final double realPossibleOverlap = this.getMaximumPossibleOverlap(gv, i);
                    double currentOverlapWidth = this.intersectAndGetOverlapWidth(gv, i);
                    double currentOverlapStatus = currentOverlapWidth - realPossibleOverlap;
                    double bestReacheadOverlapStatus = Math.abs(currentOverlapStatus);
                    boolean stillOk = true;
                    while (Math.abs(currentOverlapStatus) >= this.overlapPixels / 10.0 && stillOk) {
                        final double step = currentOverlapStatus / 2.0;
                        gv.translate(i, step, 0.0);
                        currentOverlapWidth = this.intersectAndGetOverlapWidth(gv, i);
                        currentOverlapStatus = currentOverlapWidth - realPossibleOverlap;
                        if (Math.abs(currentOverlapStatus) >= bestReacheadOverlapStatus && (currentOverlapWidth != 0.0 || gv.getMaxX(i - 1) - gv.getMinX(i) > gv.getBoundsWidth(i - 1))) {
                            if (currentOverlapWidth == 0.0) {
                                gv.translate(i, this.getSidingPosition(gv, i), 0.0);
                            }
                            else {
                                gv.translate(i, -step, 0.0);
                            }
                            stillOk = false;
                        }
                        bestReacheadOverlapStatus = Math.min(Math.abs(currentOverlapStatus), bestReacheadOverlapStatus);
                    }
                }
            }
        }
    }
    
    boolean isSidingOverlapProblem(final Glyphs gv, final int i) {
        final double sidingPosition = gv.getBoundsX(i - 1) + gv.getBoundsWidth(i - 1) - gv.getBoundsX(i) - Math.abs(gv.getRSB(i - 1)) - Math.abs(gv.getLSB(i));
        return sidingPosition - gv.getBoundsX(i - 1) <= 0.0 && (Math.abs(sidingPosition - gv.getBoundsX(i - 1)) > gv.getBoundsWidth(i - 1) * 0.6 || Math.abs(sidingPosition - gv.getBoundsX(i - 1)) > gv.getBoundsWidth(i));
    }
    
    private double getAdjustSidingPosition(final Glyphs gv, final int i) {
        final double sidingPosition = gv.getBoundsX(i - 1);
        return sidingPosition;
    }
    
    private double getSidingPosition(final Glyphs gv, final int i) {
        final double sidingPosition = gv.getBoundsX(i - 1) + gv.getBoundsWidth(i - 1) - gv.getBoundsX(i) - Math.abs(gv.getRSB(i - 1)) - Math.abs(gv.getLSB(i));
        return sidingPosition;
    }
    
    private double intersectAndGetOverlapWidth(final Glyphs gv, final int i) {
        return this.getIntesection(gv, i).getBounds2D().getWidth();
    }
    
    private Area getIntesection(final Glyphs gv, final int index) {
        final Area intersect = new Area(gv.getOutline(index - 1));
        intersect.intersect(new Area(gv.getOutline(index)));
        return intersect;
    }
    
    private double getMaximumPossibleOverlap(final Glyphs gv, final int index) {
        return Math.min(Math.min(this.overlapPixels, gv.getBoundsWidth(index)), gv.getBoundsWidth(index - 1));
    }
    
    private boolean mayGlyphsOverlapAtIndex(final Glyphs gv, final int index) {
        return gv.getMinY(index - 1) > gv.getMaxY(index) || gv.getMinY(index) > gv.getMaxY(index - 1);
    }
}
