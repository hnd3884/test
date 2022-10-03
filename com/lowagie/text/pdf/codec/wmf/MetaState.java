package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class MetaState
{
    public static final int TA_NOUPDATECP = 0;
    public static final int TA_UPDATECP = 1;
    public static final int TA_LEFT = 0;
    public static final int TA_RIGHT = 2;
    public static final int TA_CENTER = 6;
    public static final int TA_TOP = 0;
    public static final int TA_BOTTOM = 8;
    public static final int TA_BASELINE = 24;
    public static final int TRANSPARENT = 1;
    public static final int OPAQUE = 2;
    public static final int ALTERNATE = 1;
    public static final int WINDING = 2;
    public Stack savedStates;
    public ArrayList MetaObjects;
    public Point currentPoint;
    public MetaPen currentPen;
    public MetaBrush currentBrush;
    public MetaFont currentFont;
    public Color currentBackgroundColor;
    public Color currentTextColor;
    public int backgroundMode;
    public int polyFillMode;
    public int lineJoin;
    public int textAlign;
    public int offsetWx;
    public int offsetWy;
    public int extentWx;
    public int extentWy;
    public float scalingX;
    public float scalingY;
    
    public MetaState() {
        this.currentBackgroundColor = Color.white;
        this.currentTextColor = Color.black;
        this.backgroundMode = 2;
        this.polyFillMode = 1;
        this.lineJoin = 1;
        this.savedStates = new Stack();
        this.MetaObjects = new ArrayList();
        this.currentPoint = new Point(0, 0);
        this.currentPen = new MetaPen();
        this.currentBrush = new MetaBrush();
        this.currentFont = new MetaFont();
    }
    
    public MetaState(final MetaState state) {
        this.currentBackgroundColor = Color.white;
        this.currentTextColor = Color.black;
        this.backgroundMode = 2;
        this.polyFillMode = 1;
        this.lineJoin = 1;
        this.setMetaState(state);
    }
    
    public void setMetaState(final MetaState state) {
        this.savedStates = state.savedStates;
        this.MetaObjects = state.MetaObjects;
        this.currentPoint = state.currentPoint;
        this.currentPen = state.currentPen;
        this.currentBrush = state.currentBrush;
        this.currentFont = state.currentFont;
        this.currentBackgroundColor = state.currentBackgroundColor;
        this.currentTextColor = state.currentTextColor;
        this.backgroundMode = state.backgroundMode;
        this.polyFillMode = state.polyFillMode;
        this.textAlign = state.textAlign;
        this.lineJoin = state.lineJoin;
        this.offsetWx = state.offsetWx;
        this.offsetWy = state.offsetWy;
        this.extentWx = state.extentWx;
        this.extentWy = state.extentWy;
        this.scalingX = state.scalingX;
        this.scalingY = state.scalingY;
    }
    
    public void addMetaObject(final MetaObject object) {
        for (int k = 0; k < this.MetaObjects.size(); ++k) {
            if (this.MetaObjects.get(k) == null) {
                this.MetaObjects.set(k, object);
                return;
            }
        }
        this.MetaObjects.add(object);
    }
    
    public void selectMetaObject(final int index, final PdfContentByte cb) {
        final MetaObject obj = this.MetaObjects.get(index);
        if (obj == null) {
            return;
        }
        switch (obj.getType()) {
            case 2: {
                this.currentBrush = (MetaBrush)obj;
                final int style = this.currentBrush.getStyle();
                if (style == 0) {
                    final Color color = this.currentBrush.getColor();
                    cb.setColorFill(color);
                    break;
                }
                if (style == 2) {
                    final Color color = this.currentBackgroundColor;
                    cb.setColorFill(color);
                    break;
                }
                break;
            }
            case 1: {
                this.currentPen = (MetaPen)obj;
                final int style = this.currentPen.getStyle();
                if (style != 5) {
                    final Color color = this.currentPen.getColor();
                    cb.setColorStroke(color);
                    cb.setLineWidth(Math.abs(this.currentPen.getPenWidth() * this.scalingX / this.extentWx));
                    switch (style) {
                        case 1: {
                            cb.setLineDash(18.0f, 6.0f, 0.0f);
                            break;
                        }
                        case 3: {
                            cb.setLiteral("[9 6 3 6]0 d\n");
                            break;
                        }
                        case 4: {
                            cb.setLiteral("[9 3 3 3 3 3]0 d\n");
                            break;
                        }
                        case 2: {
                            cb.setLineDash(3.0f, 0.0f);
                            break;
                        }
                        default: {
                            cb.setLineDash(0.0f);
                            break;
                        }
                    }
                    break;
                }
                break;
            }
            case 3: {
                this.currentFont = (MetaFont)obj;
                break;
            }
        }
    }
    
    public void deleteMetaObject(final int index) {
        this.MetaObjects.set(index, null);
    }
    
    public void saveState(final PdfContentByte cb) {
        cb.saveState();
        final MetaState state = new MetaState(this);
        this.savedStates.push(state);
    }
    
    public void restoreState(final int index, final PdfContentByte cb) {
        int pops;
        if (index < 0) {
            pops = Math.min(-index, this.savedStates.size());
        }
        else {
            pops = Math.max(this.savedStates.size() - index, 0);
        }
        if (pops == 0) {
            return;
        }
        MetaState state = null;
        while (pops-- != 0) {
            cb.restoreState();
            state = this.savedStates.pop();
        }
        this.setMetaState(state);
    }
    
    public void cleanup(final PdfContentByte cb) {
        int k = this.savedStates.size();
        while (k-- > 0) {
            cb.restoreState();
        }
    }
    
    public float transformX(final int x) {
        return (x - (float)this.offsetWx) * this.scalingX / this.extentWx;
    }
    
    public float transformY(final int y) {
        return (1.0f - (y - (float)this.offsetWy) / this.extentWy) * this.scalingY;
    }
    
    public void setScalingX(final float scalingX) {
        this.scalingX = scalingX;
    }
    
    public void setScalingY(final float scalingY) {
        this.scalingY = scalingY;
    }
    
    public void setOffsetWx(final int offsetWx) {
        this.offsetWx = offsetWx;
    }
    
    public void setOffsetWy(final int offsetWy) {
        this.offsetWy = offsetWy;
    }
    
    public void setExtentWx(final int extentWx) {
        this.extentWx = extentWx;
    }
    
    public void setExtentWy(final int extentWy) {
        this.extentWy = extentWy;
    }
    
    public float transformAngle(final float angle) {
        final float ta = (this.scalingY < 0.0f) ? (-angle) : angle;
        return (float)((this.scalingX < 0.0f) ? (3.141592653589793 - ta) : ta);
    }
    
    public void setCurrentPoint(final Point p) {
        this.currentPoint = p;
    }
    
    public Point getCurrentPoint() {
        return this.currentPoint;
    }
    
    public MetaBrush getCurrentBrush() {
        return this.currentBrush;
    }
    
    public MetaPen getCurrentPen() {
        return this.currentPen;
    }
    
    public MetaFont getCurrentFont() {
        return this.currentFont;
    }
    
    public Color getCurrentBackgroundColor() {
        return this.currentBackgroundColor;
    }
    
    public void setCurrentBackgroundColor(final Color currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }
    
    public Color getCurrentTextColor() {
        return this.currentTextColor;
    }
    
    public void setCurrentTextColor(final Color currentTextColor) {
        this.currentTextColor = currentTextColor;
    }
    
    public int getBackgroundMode() {
        return this.backgroundMode;
    }
    
    public void setBackgroundMode(final int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }
    
    public int getTextAlign() {
        return this.textAlign;
    }
    
    public void setTextAlign(final int textAlign) {
        this.textAlign = textAlign;
    }
    
    public int getPolyFillMode() {
        return this.polyFillMode;
    }
    
    public void setPolyFillMode(final int polyFillMode) {
        this.polyFillMode = polyFillMode;
    }
    
    public void setLineJoinRectangle(final PdfContentByte cb) {
        if (this.lineJoin != 0) {
            cb.setLineJoin(this.lineJoin = 0);
        }
    }
    
    public void setLineJoinPolygon(final PdfContentByte cb) {
        if (this.lineJoin == 0) {
            cb.setLineJoin(this.lineJoin = 1);
        }
    }
    
    public boolean getLineNeutral() {
        return this.lineJoin == 0;
    }
}
