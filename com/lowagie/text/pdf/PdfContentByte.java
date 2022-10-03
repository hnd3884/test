package com.lowagie.text.pdf;

import java.util.HashMap;
import java.io.IOException;
import com.lowagie.text.ExceptionConverter;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterJob;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import com.lowagie.text.exceptions.IllegalPdfSyntaxException;
import java.util.Iterator;
import com.lowagie.text.pdf.internal.PdfAnnotationsImp;
import com.lowagie.text.Annotation;
import java.io.OutputStream;
import com.lowagie.text.ImgJBIG2;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import java.awt.Color;
import com.lowagie.text.pdf.internal.PdfXConformanceImp;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PdfContentByte
{
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 2;
    public static final int LINE_CAP_BUTT = 0;
    public static final int LINE_CAP_ROUND = 1;
    public static final int LINE_CAP_PROJECTING_SQUARE = 2;
    public static final int LINE_JOIN_MITER = 0;
    public static final int LINE_JOIN_ROUND = 1;
    public static final int LINE_JOIN_BEVEL = 2;
    public static final int TEXT_RENDER_MODE_FILL = 0;
    public static final int TEXT_RENDER_MODE_STROKE = 1;
    public static final int TEXT_RENDER_MODE_FILL_STROKE = 2;
    public static final int TEXT_RENDER_MODE_INVISIBLE = 3;
    public static final int TEXT_RENDER_MODE_FILL_CLIP = 4;
    public static final int TEXT_RENDER_MODE_STROKE_CLIP = 5;
    public static final int TEXT_RENDER_MODE_FILL_STROKE_CLIP = 6;
    public static final int TEXT_RENDER_MODE_CLIP = 7;
    private static final float[] unitRect;
    protected ByteBuffer content;
    protected PdfWriter writer;
    protected PdfDocument pdf;
    protected GraphicState state;
    private static Map<PdfName, String> abrev;
    protected List<GraphicState> stateList;
    protected int separator;
    private int mcDepth;
    private boolean inText;
    protected List<Integer> layerDepth;
    
    public PdfContentByte(final PdfWriter wr) {
        this.content = new ByteBuffer();
        this.state = new GraphicState();
        this.stateList = new ArrayList<GraphicState>();
        this.separator = 10;
        this.mcDepth = 0;
        this.inText = false;
        if (wr != null) {
            this.writer = wr;
            this.pdf = this.writer.getPdfDocument();
        }
    }
    
    @Override
    public String toString() {
        return this.content.toString();
    }
    
    public ByteBuffer getInternalBuffer() {
        return this.content;
    }
    
    public byte[] toPdf(final PdfWriter writer) {
        this.sanityCheck();
        return this.content.toByteArray();
    }
    
    public void add(final PdfContentByte other) {
        if (other.writer != null && this.writer != other.writer) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.writers.are.you.mixing.two.documents"));
        }
        this.content.append(other.content);
    }
    
    public float getXTLM() {
        return this.state.xTLM;
    }
    
    public float getYTLM() {
        return this.state.yTLM;
    }
    
    public float getLeading() {
        return this.state.leading;
    }
    
    public float getCharacterSpacing() {
        return this.state.charSpace;
    }
    
    public float getWordSpacing() {
        return this.state.wordSpace;
    }
    
    public float getHorizontalScaling() {
        return this.state.scale;
    }
    
    public void setFlatness(final float flatness) {
        if (flatness >= 0.0f && flatness <= 100.0f) {
            this.content.append(flatness).append(" i").append_i(this.separator);
        }
    }
    
    public void setLineCap(final int style) {
        if (style >= 0 && style <= 2) {
            this.content.append(style).append(" J").append_i(this.separator);
        }
    }
    
    public void setLineDash(final float phase) {
        this.content.append("[] ").append(phase).append(" d").append_i(this.separator);
    }
    
    public void setLineDash(final float unitsOn, final float phase) {
        this.content.append("[").append(unitsOn).append("] ").append(phase).append(" d").append_i(this.separator);
    }
    
    public void setLineDash(final float unitsOn, final float unitsOff, final float phase) {
        this.content.append("[").append(unitsOn).append(' ').append(unitsOff).append("] ").append(phase).append(" d").append_i(this.separator);
    }
    
    public final void setLineDash(final float[] array, final float phase) {
        this.content.append("[");
        for (int i = 0; i < array.length; ++i) {
            this.content.append(array[i]);
            if (i < array.length - 1) {
                this.content.append(' ');
            }
        }
        this.content.append("] ").append(phase).append(" d").append_i(this.separator);
    }
    
    public void setLineJoin(final int style) {
        if (style >= 0 && style <= 2) {
            this.content.append(style).append(" j").append_i(this.separator);
        }
    }
    
    public void setLineWidth(final float w) {
        this.content.append(w).append(" w").append_i(this.separator);
    }
    
    public void setMiterLimit(final float miterLimit) {
        if (miterLimit > 1.0f) {
            this.content.append(miterLimit).append(" M").append_i(this.separator);
        }
    }
    
    public void clip() {
        this.content.append("W").append_i(this.separator);
    }
    
    public void eoClip() {
        this.content.append("W*").append_i(this.separator);
    }
    
    public void setGrayFill(final float gray) {
        this.content.append(gray).append(" g").append_i(this.separator);
    }
    
    public void resetGrayFill() {
        this.content.append("0 g").append_i(this.separator);
    }
    
    public void setGrayStroke(final float gray) {
        this.content.append(gray).append(" G").append_i(this.separator);
    }
    
    public void resetGrayStroke() {
        this.content.append("0 G").append_i(this.separator);
    }
    
    private void HelperRGB(float red, float green, float blue) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 3, null);
        if (red < 0.0f) {
            red = 0.0f;
        }
        else if (red > 1.0f) {
            red = 1.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        }
        else if (green > 1.0f) {
            green = 1.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        }
        else if (blue > 1.0f) {
            blue = 1.0f;
        }
        this.content.append(red).append(' ').append(green).append(' ').append(blue);
    }
    
    public void setRGBColorFillF(final float red, final float green, final float blue) {
        this.HelperRGB(red, green, blue);
        this.content.append(" rg").append_i(this.separator);
    }
    
    public void resetRGBColorFill() {
        this.content.append("0 g").append_i(this.separator);
    }
    
    public void setRGBColorStrokeF(final float red, final float green, final float blue) {
        this.HelperRGB(red, green, blue);
        this.content.append(" RG").append_i(this.separator);
    }
    
    public void resetRGBColorStroke() {
        this.content.append("0 G").append_i(this.separator);
    }
    
    private void HelperCMYK(float cyan, float magenta, float yellow, float black) {
        if (cyan < 0.0f) {
            cyan = 0.0f;
        }
        else if (cyan > 1.0f) {
            cyan = 1.0f;
        }
        if (magenta < 0.0f) {
            magenta = 0.0f;
        }
        else if (magenta > 1.0f) {
            magenta = 1.0f;
        }
        if (yellow < 0.0f) {
            yellow = 0.0f;
        }
        else if (yellow > 1.0f) {
            yellow = 1.0f;
        }
        if (black < 0.0f) {
            black = 0.0f;
        }
        else if (black > 1.0f) {
            black = 1.0f;
        }
        this.content.append(cyan).append(' ').append(magenta).append(' ').append(yellow).append(' ').append(black);
    }
    
    public void setCMYKColorFillF(final float cyan, final float magenta, final float yellow, final float black) {
        this.HelperCMYK(cyan, magenta, yellow, black);
        this.content.append(" k").append_i(this.separator);
    }
    
    public void resetCMYKColorFill() {
        this.content.append("0 0 0 1 k").append_i(this.separator);
    }
    
    public void setCMYKColorStrokeF(final float cyan, final float magenta, final float yellow, final float black) {
        this.HelperCMYK(cyan, magenta, yellow, black);
        this.content.append(" K").append_i(this.separator);
    }
    
    public void resetCMYKColorStroke() {
        this.content.append("0 0 0 1 K").append_i(this.separator);
    }
    
    public void moveTo(final float x, final float y) {
        this.content.append(x).append(' ').append(y).append(" m").append_i(this.separator);
    }
    
    public void lineTo(final float x, final float y) {
        this.content.append(x).append(' ').append(y).append(" l").append_i(this.separator);
    }
    
    public void curveTo(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        this.content.append(x1).append(' ').append(y1).append(' ').append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" c").append_i(this.separator);
    }
    
    public void curveTo(final float x2, final float y2, final float x3, final float y3) {
        this.content.append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" v").append_i(this.separator);
    }
    
    public void curveFromTo(final float x1, final float y1, final float x3, final float y3) {
        this.content.append(x1).append(' ').append(y1).append(' ').append(x3).append(' ').append(y3).append(" y").append_i(this.separator);
    }
    
    public void circle(final float x, final float y, final float r) {
        final float b = 0.5523f;
        this.moveTo(x + r, y);
        this.curveTo(x + r, y + r * b, x + r * b, y + r, x, y + r);
        this.curveTo(x - r * b, y + r, x - r, y + r * b, x - r, y);
        this.curveTo(x - r, y - r * b, x - r * b, y - r, x, y - r);
        this.curveTo(x + r * b, y - r, x + r, y - r * b, x + r, y);
    }
    
    public void rectangle(final float x, final float y, final float w, final float h) {
        this.content.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re").append_i(this.separator);
    }
    
    private boolean compareColors(final Color c1, final Color c2) {
        if (c1 == null && c2 == null) {
            return true;
        }
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1 instanceof ExtendedColor) {
            return c1.equals(c2);
        }
        return c2.equals(c1);
    }
    
    public void variableRectangle(final Rectangle rect) {
        final float t = rect.getTop();
        final float b = rect.getBottom();
        final float r = rect.getRight();
        final float l = rect.getLeft();
        final float wt = rect.getBorderWidthTop();
        final float wb = rect.getBorderWidthBottom();
        final float wr = rect.getBorderWidthRight();
        final float wl = rect.getBorderWidthLeft();
        final Color ct = rect.getBorderColorTop();
        final Color cb = rect.getBorderColorBottom();
        final Color cr = rect.getBorderColorRight();
        final Color cl = rect.getBorderColorLeft();
        this.saveState();
        this.setLineCap(0);
        this.setLineJoin(0);
        float clw = 0.0f;
        boolean cdef = false;
        Color ccol = null;
        boolean cdefi = false;
        Color cfil = null;
        if (wt > 0.0f) {
            this.setLineWidth(clw = wt);
            cdef = true;
            if (ct == null) {
                this.resetRGBColorStroke();
            }
            else {
                this.setColorStroke(ct);
            }
            ccol = ct;
            this.moveTo(l, t - wt / 2.0f);
            this.lineTo(r, t - wt / 2.0f);
            this.stroke();
        }
        if (wb > 0.0f) {
            if (wb != clw) {
                this.setLineWidth(clw = wb);
            }
            if (!cdef || !this.compareColors(ccol, cb)) {
                cdef = true;
                if (cb == null) {
                    this.resetRGBColorStroke();
                }
                else {
                    this.setColorStroke(cb);
                }
                ccol = cb;
            }
            this.moveTo(r, b + wb / 2.0f);
            this.lineTo(l, b + wb / 2.0f);
            this.stroke();
        }
        if (wr > 0.0f) {
            if (wr != clw) {
                this.setLineWidth(clw = wr);
            }
            if (!cdef || !this.compareColors(ccol, cr)) {
                cdef = true;
                if (cr == null) {
                    this.resetRGBColorStroke();
                }
                else {
                    this.setColorStroke(cr);
                }
                ccol = cr;
            }
            final boolean bt = this.compareColors(ct, cr);
            final boolean bb = this.compareColors(cb, cr);
            this.moveTo(r - wr / 2.0f, bt ? t : (t - wt));
            this.lineTo(r - wr / 2.0f, bb ? b : (b + wb));
            this.stroke();
            if (!bt || !bb) {
                cdefi = true;
                if (cr == null) {
                    this.resetRGBColorFill();
                }
                else {
                    this.setColorFill(cr);
                }
                cfil = cr;
                if (!bt) {
                    this.moveTo(r, t);
                    this.lineTo(r, t - wt);
                    this.lineTo(r - wr, t - wt);
                    this.fill();
                }
                if (!bb) {
                    this.moveTo(r, b);
                    this.lineTo(r, b + wb);
                    this.lineTo(r - wr, b + wb);
                    this.fill();
                }
            }
        }
        if (wl > 0.0f) {
            if (wl != clw) {
                this.setLineWidth(wl);
            }
            if (!cdef || !this.compareColors(ccol, cl)) {
                if (cl == null) {
                    this.resetRGBColorStroke();
                }
                else {
                    this.setColorStroke(cl);
                }
            }
            final boolean bt = this.compareColors(ct, cl);
            final boolean bb = this.compareColors(cb, cl);
            this.moveTo(l + wl / 2.0f, bt ? t : (t - wt));
            this.lineTo(l + wl / 2.0f, bb ? b : (b + wb));
            this.stroke();
            if (!bt || !bb) {
                if (!cdefi || !this.compareColors(cfil, cl)) {
                    if (cl == null) {
                        this.resetRGBColorFill();
                    }
                    else {
                        this.setColorFill(cl);
                    }
                }
                if (!bt) {
                    this.moveTo(l, t);
                    this.lineTo(l, t - wt);
                    this.lineTo(l + wl, t - wt);
                    this.fill();
                }
                if (!bb) {
                    this.moveTo(l, b);
                    this.lineTo(l, b + wb);
                    this.lineTo(l + wl, b + wb);
                    this.fill();
                }
            }
        }
        this.restoreState();
    }
    
    public void rectangle(final Rectangle rectangle) {
        final float x1 = rectangle.getLeft();
        final float y1 = rectangle.getBottom();
        final float x2 = rectangle.getRight();
        final float y2 = rectangle.getTop();
        final Color background = rectangle.getBackgroundColor();
        if (background != null) {
            this.saveState();
            this.setColorFill(background);
            this.rectangle(x1, y1, x2 - x1, y2 - y1);
            this.fill();
            this.restoreState();
        }
        if (!rectangle.hasBorders()) {
            return;
        }
        if (rectangle.isUseVariableBorders()) {
            this.variableRectangle(rectangle);
        }
        else {
            if (rectangle.getBorderWidth() != -1.0f) {
                this.setLineWidth(rectangle.getBorderWidth());
            }
            final Color color = rectangle.getBorderColor();
            if (color != null) {
                this.setColorStroke(color);
            }
            if (rectangle.hasBorder(15)) {
                this.rectangle(x1, y1, x2 - x1, y2 - y1);
            }
            else {
                if (rectangle.hasBorder(8)) {
                    this.moveTo(x2, y1);
                    this.lineTo(x2, y2);
                }
                if (rectangle.hasBorder(4)) {
                    this.moveTo(x1, y1);
                    this.lineTo(x1, y2);
                }
                if (rectangle.hasBorder(2)) {
                    this.moveTo(x1, y1);
                    this.lineTo(x2, y1);
                }
                if (rectangle.hasBorder(1)) {
                    this.moveTo(x1, y2);
                    this.lineTo(x2, y2);
                }
            }
            this.stroke();
            if (color != null) {
                this.resetRGBColorStroke();
            }
        }
    }
    
    public void closePath() {
        this.content.append("h").append_i(this.separator);
    }
    
    public void newPath() {
        this.content.append("n").append_i(this.separator);
    }
    
    public void stroke() {
        this.content.append("S").append_i(this.separator);
    }
    
    public void closePathStroke() {
        this.content.append("s").append_i(this.separator);
    }
    
    public void fill() {
        this.content.append("f").append_i(this.separator);
    }
    
    public void eoFill() {
        this.content.append("f*").append_i(this.separator);
    }
    
    public void fillStroke() {
        this.content.append("B").append_i(this.separator);
    }
    
    public void closePathFillStroke() {
        this.content.append("b").append_i(this.separator);
    }
    
    public void eoFillStroke() {
        this.content.append("B*").append_i(this.separator);
    }
    
    public void closePathEoFillStroke() {
        this.content.append("b*").append_i(this.separator);
    }
    
    public void addImage(final Image image) throws DocumentException {
        this.addImage(image, false);
    }
    
    public void addImage(final Image image, final boolean inlineImage) throws DocumentException {
        if (!image.hasAbsoluteY()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.image.must.have.absolute.positioning"));
        }
        final float[] matrix = image.matrix();
        matrix[4] = image.getAbsoluteX() - matrix[4];
        matrix[5] = image.getAbsoluteY() - matrix[5];
        this.addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5], inlineImage);
    }
    
    public void addImage(final Image image, final float a, final float b, final float c, final float d, final float e, final float f) throws DocumentException {
        this.addImage(image, a, b, c, d, e, f, false);
    }
    
    public void addImage(final Image image, final float a, final float b, final float c, final float d, final float e, final float f, final boolean inlineImage) throws DocumentException {
        try {
            if (image.getLayer() != null) {
                this.beginLayer(image.getLayer());
            }
            if (image.isImgTemplate()) {
                this.writer.addDirectImageSimple(image);
                final PdfTemplate template = image.getTemplateData();
                final float w = template.getWidth();
                final float h = template.getHeight();
                this.addTemplate(template, a / w, b / w, c / h, d / h, e, f);
            }
            else {
                this.content.append("q ");
                this.content.append(a).append(' ');
                this.content.append(b).append(' ');
                this.content.append(c).append(' ');
                this.content.append(d).append(' ');
                this.content.append(e).append(' ');
                this.content.append(f).append(" cm");
                if (inlineImage) {
                    this.content.append("\nBI\n");
                    final PdfImage pimage = new PdfImage(image, "", null);
                    if (image instanceof ImgJBIG2) {
                        final byte[] globals = ((ImgJBIG2)image).getGlobalBytes();
                        if (globals != null) {
                            final PdfDictionary decodeparms = new PdfDictionary();
                            decodeparms.put(PdfName.JBIG2GLOBALS, this.writer.getReferenceJBIG2Globals(globals));
                            pimage.put(PdfName.DECODEPARMS, decodeparms);
                        }
                    }
                    for (final PdfName key : pimage.getKeys()) {
                        PdfObject value = pimage.get(key);
                        final String s = PdfContentByte.abrev.get(key);
                        if (s == null) {
                            continue;
                        }
                        this.content.append(s);
                        boolean check = true;
                        if (key.equals(PdfName.COLORSPACE) && value.isArray()) {
                            final PdfArray ar = (PdfArray)value;
                            if (ar.size() == 4 && PdfName.INDEXED.equals(ar.getAsName(0)) && ar.getPdfObject(1).isName() && ar.getPdfObject(2).isNumber() && ar.getPdfObject(3).isString()) {
                                check = false;
                            }
                        }
                        if (check && key.equals(PdfName.COLORSPACE) && !value.isName()) {
                            final PdfName cs = this.writer.getColorspaceName();
                            final PageResources prs = this.getPageResources();
                            prs.addColor(cs, this.writer.addToBody(value).getIndirectReference());
                            value = cs;
                        }
                        value.toPdf(null, this.content);
                        this.content.append('\n');
                    }
                    this.content.append("ID\n");
                    pimage.writeContent(this.content);
                    this.content.append("\nEI\nQ").append_i(this.separator);
                }
                else {
                    final PageResources prs2 = this.getPageResources();
                    final Image maskImage = image.getImageMask();
                    if (maskImage != null) {
                        final PdfName name = this.writer.addDirectImageSimple(maskImage);
                        prs2.addXObject(name, this.writer.getImageReference(name));
                    }
                    PdfName name = this.writer.addDirectImageSimple(image);
                    name = prs2.addXObject(name, this.writer.getImageReference(name));
                    this.content.append(' ').append(name.getBytes()).append(" Do Q").append_i(this.separator);
                }
            }
            if (image.hasBorders()) {
                this.saveState();
                final float w2 = image.getWidth();
                final float h2 = image.getHeight();
                this.concatCTM(a / w2, b / w2, c / h2, d / h2, e, f);
                this.rectangle(image);
                this.restoreState();
            }
            if (image.getLayer() != null) {
                this.endLayer();
            }
            Annotation annot = image.getAnnotation();
            if (annot == null) {
                return;
            }
            final float[] r = new float[PdfContentByte.unitRect.length];
            for (int k = 0; k < PdfContentByte.unitRect.length; k += 2) {
                r[k] = a * PdfContentByte.unitRect[k] + c * PdfContentByte.unitRect[k + 1] + e;
                r[k + 1] = b * PdfContentByte.unitRect[k] + d * PdfContentByte.unitRect[k + 1] + f;
            }
            float llx = r[0];
            float lly = r[1];
            float urx = llx;
            float ury = lly;
            for (int i = 2; i < r.length; i += 2) {
                llx = Math.min(llx, r[i]);
                lly = Math.min(lly, r[i + 1]);
                urx = Math.max(urx, r[i]);
                ury = Math.max(ury, r[i + 1]);
            }
            annot = new Annotation(annot);
            annot.setDimensions(llx, lly, urx, ury);
            final PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, new Rectangle(llx, lly, urx, ury));
            if (an == null) {
                return;
            }
            this.addAnnotation(an);
        }
        catch (final Exception ee) {
            throw new DocumentException(ee);
        }
    }
    
    public void reset() {
        this.reset(true);
    }
    
    public void reset(final boolean validateContent) {
        this.content.reset();
        if (validateContent) {
            this.sanityCheck();
        }
        this.state = new GraphicState();
    }
    
    public void beginText() {
        if (this.inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        this.inText = true;
        this.state.xTLM = 0.0f;
        this.state.yTLM = 0.0f;
        this.content.append("BT").append_i(this.separator);
    }
    
    public void endText() {
        if (!this.inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        this.inText = false;
        this.content.append("ET").append_i(this.separator);
    }
    
    public void saveState() {
        this.content.append("q").append_i(this.separator);
        this.stateList.add(new GraphicState(this.state));
    }
    
    public void restoreState() {
        this.content.append("Q").append_i(this.separator);
        final int idx = this.stateList.size() - 1;
        if (idx < 0) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators"));
        }
        this.state = this.stateList.get(idx);
        this.stateList.remove(idx);
    }
    
    public void setCharacterSpacing(final float charSpace) {
        this.state.charSpace = charSpace;
        this.content.append(charSpace).append(" Tc").append_i(this.separator);
    }
    
    public void setWordSpacing(final float wordSpace) {
        this.state.wordSpace = wordSpace;
        this.content.append(wordSpace).append(" Tw").append_i(this.separator);
    }
    
    public void setHorizontalScaling(final float scale) {
        this.state.scale = scale;
        this.content.append(scale).append(" Tz").append_i(this.separator);
    }
    
    public void setLeading(final float leading) {
        this.state.leading = leading;
        this.content.append(leading).append(" TL").append_i(this.separator);
    }
    
    public void setFontAndSize(final BaseFont bf, final float size) {
        this.checkWriter();
        if (size < 1.0E-4f && size > -1.0E-4f) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("font.size.too.small.1", String.valueOf(size)));
        }
        this.state.size = size;
        this.state.fontDetails = this.writer.addSimple(bf);
        final PageResources prs = this.getPageResources();
        PdfName name = this.state.fontDetails.getFontName();
        name = prs.addFont(name, this.state.fontDetails.getIndirectReference());
        this.content.append(name.getBytes()).append(' ').append(size).append(" Tf").append_i(this.separator);
    }
    
    public void setTextRenderingMode(final int rendering) {
        this.content.append(rendering).append(" Tr").append_i(this.separator);
    }
    
    public void setTextRise(final float rise) {
        this.content.append(rise).append(" Ts").append_i(this.separator);
    }
    
    private void showText2(final String text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        final byte[] b = this.state.fontDetails.convertToBytes(text);
        escapeString(b, this.content);
    }
    
    public void showText(final String text) {
        this.showText2(text);
        this.content.append("Tj").append_i(this.separator);
    }
    
    public void showText(final GlyphVector glyphVector) {
        final byte[] b = this.state.fontDetails.convertToBytes(glyphVector);
        escapeString(b, this.content);
        this.content.append("Tj").append_i(this.separator);
    }
    
    public static PdfTextArray getKernArray(final String text, final BaseFont font) {
        final PdfTextArray pa = new PdfTextArray();
        final StringBuffer acc = new StringBuffer();
        final int len = text.length() - 1;
        final char[] c = text.toCharArray();
        if (len >= 0) {
            acc.append(c, 0, 1);
        }
        for (int k = 0; k < len; ++k) {
            final char c2 = c[k + 1];
            final int kern = font.getKerning(c[k], c2);
            if (kern == 0) {
                acc.append(c2);
            }
            else {
                pa.add(acc.toString());
                acc.setLength(0);
                acc.append(c, k + 1, 1);
                pa.add((float)(-kern));
            }
        }
        pa.add(acc.toString());
        return pa;
    }
    
    public void showTextKerned(final String text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        final BaseFont bf = this.state.fontDetails.getBaseFont();
        if (bf.hasKernPairs()) {
            this.showText(getKernArray(text, bf));
        }
        else {
            this.showText(text);
        }
    }
    
    public void newlineShowText(final String text) {
        final GraphicState state = this.state;
        state.yTLM -= this.state.leading;
        this.showText2(text);
        this.content.append("'").append_i(this.separator);
    }
    
    public void newlineShowText(final float wordSpacing, final float charSpacing, final String text) {
        final GraphicState state = this.state;
        state.yTLM -= this.state.leading;
        this.content.append(wordSpacing).append(' ').append(charSpacing);
        this.showText2(text);
        this.content.append("\"").append_i(this.separator);
        this.state.charSpace = charSpacing;
        this.state.wordSpace = wordSpacing;
    }
    
    public void setTextMatrix(final float a, final float b, final float c, final float d, final float x, final float y) {
        this.state.xTLM = x;
        this.state.yTLM = y;
        this.content.append(a).append(' ').append(b).append_i(32).append(c).append_i(32).append(d).append_i(32).append(x).append_i(32).append(y).append(" Tm").append_i(this.separator);
    }
    
    public void setTextMatrix(final float x, final float y) {
        this.setTextMatrix(1.0f, 0.0f, 0.0f, 1.0f, x, y);
    }
    
    public void moveText(final float x, final float y) {
        final GraphicState state = this.state;
        state.xTLM += x;
        final GraphicState state2 = this.state;
        state2.yTLM += y;
        this.content.append(x).append(' ').append(y).append(" Td").append_i(this.separator);
    }
    
    public void moveTextWithLeading(final float x, final float y) {
        final GraphicState state = this.state;
        state.xTLM += x;
        final GraphicState state2 = this.state;
        state2.yTLM += y;
        this.state.leading = -y;
        this.content.append(x).append(' ').append(y).append(" TD").append_i(this.separator);
    }
    
    public void newlineText() {
        final GraphicState state = this.state;
        state.yTLM -= this.state.leading;
        this.content.append("T*").append_i(this.separator);
    }
    
    int size() {
        return this.content.size();
    }
    
    static byte[] escapeString(final byte[] b) {
        final ByteBuffer content = new ByteBuffer();
        escapeString(b, content);
        return content.toByteArray();
    }
    
    static void escapeString(final byte[] b, final ByteBuffer content) {
        content.append_i(40);
        for (int k = 0; k < b.length; ++k) {
            final byte c = b[k];
            switch (c) {
                case 13: {
                    content.append("\\r");
                    break;
                }
                case 10: {
                    content.append("\\n");
                    break;
                }
                case 9: {
                    content.append("\\t");
                    break;
                }
                case 8: {
                    content.append("\\b");
                    break;
                }
                case 12: {
                    content.append("\\f");
                    break;
                }
                case 40:
                case 41:
                case 92: {
                    content.append_i(92).append_i(c);
                    break;
                }
                default: {
                    content.append_i(c);
                    break;
                }
            }
        }
        content.append(")");
    }
    
    public void addOutline(final PdfOutline outline, final String name) {
        this.checkWriter();
        this.pdf.addOutline(outline, name);
    }
    
    public PdfOutline getRootOutline() {
        this.checkWriter();
        return this.pdf.getRootOutline();
    }
    
    public float getEffectiveStringWidth(final String text, final boolean kerned) {
        final BaseFont bf = this.state.fontDetails.getBaseFont();
        float w;
        if (kerned) {
            w = bf.getWidthPointKerned(text, this.state.size);
        }
        else {
            w = bf.getWidthPoint(text, this.state.size);
        }
        if (this.state.charSpace != 0.0f && text.length() > 1) {
            w += this.state.charSpace * (text.length() - 1);
        }
        final int ft = bf.getFontType();
        if (this.state.wordSpace != 0.0f && (ft == 0 || ft == 1 || ft == 5)) {
            for (int i = 0; i < text.length() - 1; ++i) {
                if (text.charAt(i) == ' ') {
                    w += this.state.wordSpace;
                }
            }
        }
        if (this.state.scale != 100.0) {
            w = w * this.state.scale / 100.0f;
        }
        return w;
    }
    
    public void showTextAligned(final int alignment, final String text, final float x, final float y, final float rotation) {
        this.showTextAligned(alignment, text, x, y, rotation, false);
    }
    
    private void showTextAligned(final int alignment, final String text, float x, float y, final float rotation, final boolean kerned) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        if (rotation == 0.0f) {
            switch (alignment) {
                case 1: {
                    x -= this.getEffectiveStringWidth(text, kerned) / 2.0f;
                    break;
                }
                case 2: {
                    x -= this.getEffectiveStringWidth(text, kerned);
                    break;
                }
            }
            this.setTextMatrix(x, y);
            if (kerned) {
                this.showTextKerned(text);
            }
            else {
                this.showText(text);
            }
        }
        else {
            final double alpha = rotation * 3.141592653589793 / 180.0;
            final float cos = (float)Math.cos(alpha);
            final float sin = (float)Math.sin(alpha);
            switch (alignment) {
                case 1: {
                    final float len = this.getEffectiveStringWidth(text, kerned) / 2.0f;
                    x -= len * cos;
                    y -= len * sin;
                    break;
                }
                case 2: {
                    final float len = this.getEffectiveStringWidth(text, kerned);
                    x -= len * cos;
                    y -= len * sin;
                    break;
                }
            }
            this.setTextMatrix(cos, sin, -sin, cos, x, y);
            if (kerned) {
                this.showTextKerned(text);
            }
            else {
                this.showText(text);
            }
            this.setTextMatrix(0.0f, 0.0f);
        }
    }
    
    public void showTextAlignedKerned(final int alignment, final String text, final float x, final float y, final float rotation) {
        this.showTextAligned(alignment, text, x, y, rotation, true);
    }
    
    public void concatCTM(final float a, final float b, final float c, final float d, final float e, final float f) {
        this.content.append(a).append(' ').append(b).append(' ').append(c).append(' ');
        this.content.append(d).append(' ').append(e).append(' ').append(f).append(" cm").append_i(this.separator);
    }
    
    public static List<float[]> bezierArc(float x1, float y1, float x2, float y2, final float startAng, final float extent) {
        if (x1 > x2) {
            final float tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y2 > y1) {
            final float tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        float fragAngle;
        int Nfrag;
        if (Math.abs(extent) <= 90.0f) {
            fragAngle = extent;
            Nfrag = 1;
        }
        else {
            Nfrag = (int)Math.ceil(Math.abs(extent) / 90.0f);
            fragAngle = extent / Nfrag;
        }
        final float x_cen = (x1 + x2) / 2.0f;
        final float y_cen = (y1 + y2) / 2.0f;
        final float rx = (x2 - x1) / 2.0f;
        final float ry = (y2 - y1) / 2.0f;
        final float halfAng = (float)(fragAngle * 3.141592653589793 / 360.0);
        final float kappa = (float)Math.abs(1.3333333333333333 * (1.0 - Math.cos(halfAng)) / Math.sin(halfAng));
        final List<float[]> pointList = new ArrayList<float[]>();
        for (int i = 0; i < Nfrag; ++i) {
            final float theta0 = (float)((startAng + i * fragAngle) * 3.141592653589793 / 180.0);
            final float theta2 = (float)((startAng + (i + 1) * fragAngle) * 3.141592653589793 / 180.0);
            final float cos0 = (float)Math.cos(theta0);
            final float cos2 = (float)Math.cos(theta2);
            final float sin0 = (float)Math.sin(theta0);
            final float sin2 = (float)Math.sin(theta2);
            if (fragAngle > 0.0f) {
                pointList.add(new float[] { x_cen + rx * cos0, y_cen - ry * sin0, x_cen + rx * (cos0 - kappa * sin0), y_cen - ry * (sin0 + kappa * cos0), x_cen + rx * (cos2 + kappa * sin2), y_cen - ry * (sin2 - kappa * cos2), x_cen + rx * cos2, y_cen - ry * sin2 });
            }
            else {
                pointList.add(new float[] { x_cen + rx * cos0, y_cen - ry * sin0, x_cen + rx * (cos0 + kappa * sin0), y_cen - ry * (sin0 - kappa * cos0), x_cen + rx * (cos2 - kappa * sin2), y_cen - ry * (sin2 + kappa * cos2), x_cen + rx * cos2, y_cen - ry * sin2 });
            }
        }
        return pointList;
    }
    
    public void arc(final float x1, final float y1, final float x2, final float y2, final float startAng, final float extent) {
        final List<float[]> ar = bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty()) {
            return;
        }
        float[] pt = ar.get(0);
        this.moveTo(pt[0], pt[1]);
        final Iterator<float[]> iterator = ar.iterator();
        while (iterator.hasNext()) {
            final float[] anAr = pt = iterator.next();
            this.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
        }
    }
    
    public void ellipse(final float x1, final float y1, final float x2, final float y2) {
        this.arc(x1, y1, x2, y2, 0.0f, 360.0f);
    }
    
    public PdfPatternPainter createPattern(final float width, final float height, final float xstep, final float ystep) {
        this.checkWriter();
        if (xstep == 0.0f || ystep == 0.0f) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("xstep.or.ystep.can.not.be.zero"));
        }
        final PdfPatternPainter painter = new PdfPatternPainter(this.writer);
        painter.setWidth(width);
        painter.setHeight(height);
        painter.setXStep(xstep);
        painter.setYStep(ystep);
        this.writer.addSimplePattern(painter);
        return painter;
    }
    
    public PdfPatternPainter createPattern(final float width, final float height) {
        return this.createPattern(width, height, width, height);
    }
    
    public PdfPatternPainter createPattern(final float width, final float height, final float xstep, final float ystep, final Color color) {
        this.checkWriter();
        if (xstep == 0.0f || ystep == 0.0f) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("xstep.or.ystep.can.not.be.zero"));
        }
        final PdfPatternPainter painter = new PdfPatternPainter(this.writer, color);
        painter.setWidth(width);
        painter.setHeight(height);
        painter.setXStep(xstep);
        painter.setYStep(ystep);
        this.writer.addSimplePattern(painter);
        return painter;
    }
    
    public PdfPatternPainter createPattern(final float width, final float height, final Color color) {
        return this.createPattern(width, height, width, height, color);
    }
    
    public PdfTemplate createTemplate(final float width, final float height) {
        return this.createTemplate(width, height, null);
    }
    
    PdfTemplate createTemplate(final float width, final float height, final PdfName forcedName) {
        this.checkWriter();
        final PdfTemplate template = new PdfTemplate(this.writer);
        template.setWidth(width);
        template.setHeight(height);
        this.writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }
    
    public PdfAppearance createAppearance(final float width, final float height) {
        return this.createAppearance(width, height, null);
    }
    
    PdfAppearance createAppearance(final float width, final float height, final PdfName forcedName) {
        this.checkWriter();
        final PdfAppearance template = new PdfAppearance(this.writer);
        template.setWidth(width);
        template.setHeight(height);
        this.writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }
    
    public void addPSXObject(final PdfPSXObject psobject) {
        this.checkWriter();
        PdfName name = this.writer.addDirectTemplateSimple(psobject, null);
        final PageResources prs = this.getPageResources();
        name = prs.addXObject(name, psobject.getIndirectReference());
        this.content.append(name.getBytes()).append(" Do").append_i(this.separator);
    }
    
    public void addTemplate(final PdfTemplate template, final float a, final float b, final float c, final float d, final float e, final float f) {
        this.checkWriter();
        this.checkNoPattern(template);
        PdfName name = this.writer.addDirectTemplateSimple(template, null);
        final PageResources prs = this.getPageResources();
        name = prs.addXObject(name, template.getIndirectReference());
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm ");
        this.content.append(name.getBytes()).append(" Do Q").append_i(this.separator);
    }
    
    void addTemplateReference(final PdfIndirectReference template, PdfName name, final float a, final float b, final float c, final float d, final float e, final float f) {
        this.checkWriter();
        final PageResources prs = this.getPageResources();
        name = prs.addXObject(name, template);
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm ");
        this.content.append(name.getBytes()).append(" Do Q").append_i(this.separator);
    }
    
    public void addTemplate(final PdfTemplate template, final float x, final float y) {
        this.addTemplate(template, 1.0f, 0.0f, 0.0f, 1.0f, x, y);
    }
    
    public void setCMYKColorFill(final int cyan, final int magenta, final int yellow, final int black) {
        this.content.append((cyan & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((magenta & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((yellow & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((black & 0xFF) / 255.0f);
        this.content.append(" k").append_i(this.separator);
    }
    
    public void setCMYKColorStroke(final int cyan, final int magenta, final int yellow, final int black) {
        this.content.append((cyan & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((magenta & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((yellow & 0xFF) / 255.0f);
        this.content.append(' ');
        this.content.append((black & 0xFF) / 255.0f);
        this.content.append(" K").append_i(this.separator);
    }
    
    public void setRGBColorFill(final int red, final int green, final int blue) {
        this.HelperRGB((red & 0xFF) / 255.0f, (green & 0xFF) / 255.0f, (blue & 0xFF) / 255.0f);
        this.content.append(" rg").append_i(this.separator);
    }
    
    public void setRGBColorStroke(final int red, final int green, final int blue) {
        this.HelperRGB((red & 0xFF) / 255.0f, (green & 0xFF) / 255.0f, (blue & 0xFF) / 255.0f);
        this.content.append(" RG").append_i(this.separator);
    }
    
    public void setColorStroke(final Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        final int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                this.setGrayStroke(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                final CMYKColor cmyk = (CMYKColor)color;
                this.setCMYKColorStrokeF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
                break;
            }
            case 3: {
                final SpotColor spot = (SpotColor)color;
                this.setColorStroke(spot.getPdfSpotColor(), spot.getTint());
                break;
            }
            case 4: {
                final PatternColor pat = (PatternColor)color;
                this.setPatternStroke(pat.getPainter());
                break;
            }
            case 5: {
                final ShadingColor shading = (ShadingColor)color;
                this.setShadingStroke(shading.getPdfShadingPattern());
                break;
            }
            default: {
                this.setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
                break;
            }
        }
    }
    
    public void setColorFill(final Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        final int type = ExtendedColor.getType(color);
        switch (type) {
            case 1: {
                this.setGrayFill(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                final CMYKColor cmyk = (CMYKColor)color;
                this.setCMYKColorFillF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
                break;
            }
            case 3: {
                final SpotColor spot = (SpotColor)color;
                this.setColorFill(spot.getPdfSpotColor(), spot.getTint());
                break;
            }
            case 4: {
                final PatternColor pat = (PatternColor)color;
                this.setPatternFill(pat.getPainter());
                break;
            }
            case 5: {
                final ShadingColor shading = (ShadingColor)color;
                this.setShadingFill(shading.getPdfShadingPattern());
                break;
            }
            default: {
                this.setRGBColorFill(color.getRed(), color.getGreen(), color.getBlue());
                break;
            }
        }
    }
    
    public void setColorFill(final PdfSpotColor sp, final float tint) {
        this.checkWriter();
        this.state.colorDetails = this.writer.addSimple(sp);
        final PageResources prs = this.getPageResources();
        PdfName name = this.state.colorDetails.getColorName();
        name = prs.addColor(name, this.state.colorDetails.getIndirectReference());
        this.content.append(name.getBytes()).append(" cs ").append(tint).append(" scn").append_i(this.separator);
    }
    
    public void setColorStroke(final PdfSpotColor sp, final float tint) {
        this.checkWriter();
        this.state.colorDetails = this.writer.addSimple(sp);
        final PageResources prs = this.getPageResources();
        PdfName name = this.state.colorDetails.getColorName();
        name = prs.addColor(name, this.state.colorDetails.getIndirectReference());
        this.content.append(name.getBytes()).append(" CS ").append(tint).append(" SCN").append_i(this.separator);
    }
    
    public void setPatternFill(final PdfPatternPainter p) {
        if (p.isStencil()) {
            this.setPatternFill(p, p.getDefaultColor());
            return;
        }
        this.checkWriter();
        final PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
    }
    
    void outputColorNumbers(final Color color, final float tint) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        final int type = ExtendedColor.getType(color);
        switch (type) {
            case 0: {
                this.content.append(color.getRed() / 255.0f);
                this.content.append(' ');
                this.content.append(color.getGreen() / 255.0f);
                this.content.append(' ');
                this.content.append(color.getBlue() / 255.0f);
                break;
            }
            case 1: {
                this.content.append(((GrayColor)color).getGray());
                break;
            }
            case 2: {
                final CMYKColor cmyk = (CMYKColor)color;
                this.content.append(cmyk.getCyan()).append(' ').append(cmyk.getMagenta());
                this.content.append(' ').append(cmyk.getYellow()).append(' ').append(cmyk.getBlack());
                break;
            }
            case 3: {
                this.content.append(tint);
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type"));
            }
        }
    }
    
    public void setPatternFill(final PdfPatternPainter p, final Color color) {
        if (ExtendedColor.getType(color) == 3) {
            this.setPatternFill(p, color, ((SpotColor)color).getTint());
        }
        else {
            this.setPatternFill(p, color, 0.0f);
        }
    }
    
    public void setPatternFill(final PdfPatternPainter p, final Color color, final float tint) {
        this.checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected"));
        }
        final PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        final ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
        final PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        this.content.append(cName.getBytes()).append(" cs").append_i(this.separator);
        this.outputColorNumbers(color, tint);
        this.content.append(' ').append(name.getBytes()).append(" scn").append_i(this.separator);
    }
    
    public void setPatternStroke(final PdfPatternPainter p, final Color color) {
        if (ExtendedColor.getType(color) == 3) {
            this.setPatternStroke(p, color, ((SpotColor)color).getTint());
        }
        else {
            this.setPatternStroke(p, color, 0.0f);
        }
    }
    
    public void setPatternStroke(final PdfPatternPainter p, final Color color, final float tint) {
        this.checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected"));
        }
        final PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        final ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
        final PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        this.content.append(cName.getBytes()).append(" CS").append_i(this.separator);
        this.outputColorNumbers(color, tint);
        this.content.append(' ').append(name.getBytes()).append(" SCN").append_i(this.separator);
    }
    
    public void setPatternStroke(final PdfPatternPainter p) {
        if (p.isStencil()) {
            this.setPatternStroke(p, p.getDefaultColor());
            return;
        }
        this.checkWriter();
        final PageResources prs = this.getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        name = prs.addPattern(name, p.getIndirectReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
    }
    
    public void paintShading(final PdfShading shading) {
        this.writer.addSimpleShading(shading);
        final PageResources prs = this.getPageResources();
        final PdfName name = prs.addShading(shading.getShadingName(), shading.getShadingReference());
        this.content.append(name.getBytes()).append(" sh").append_i(this.separator);
        final ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }
    
    public void paintShading(final PdfShadingPattern shading) {
        this.paintShading(shading.getShading());
    }
    
    public void setShadingFill(final PdfShadingPattern shading) {
        this.writer.addSimpleShadingPattern(shading);
        final PageResources prs = this.getPageResources();
        final PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
        final ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }
    
    public void setShadingStroke(final PdfShadingPattern shading) {
        this.writer.addSimpleShadingPattern(shading);
        final PageResources prs = this.getPageResources();
        final PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
        final ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }
    
    protected void checkWriter() {
        if (this.writer == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("the.writer.in.pdfcontentbyte.is.null"));
        }
    }
    
    public void showText(final PdfTextArray text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text"));
        }
        this.content.append("[");
        final List arrayList = text.getArrayList();
        boolean lastWasNumber = false;
        for (final Object obj : arrayList) {
            if (obj instanceof String) {
                this.showText2((String)obj);
                lastWasNumber = false;
            }
            else {
                if (lastWasNumber) {
                    this.content.append(' ');
                }
                else {
                    lastWasNumber = true;
                }
                this.content.append((float)obj);
            }
        }
        this.content.append("]TJ").append_i(this.separator);
    }
    
    public PdfWriter getPdfWriter() {
        return this.writer;
    }
    
    public PdfDocument getPdfDocument() {
        return this.pdf;
    }
    
    public void localGoto(final String name, final float llx, final float lly, final float urx, final float ury) {
        this.pdf.localGoto(name, llx, lly, urx, ury);
    }
    
    public boolean localDestination(final String name, final PdfDestination destination) {
        return this.pdf.localDestination(name, destination);
    }
    
    public PdfContentByte getDuplicate() {
        return new PdfContentByte(this.writer);
    }
    
    public void remoteGoto(final String filename, final String name, final float llx, final float lly, final float urx, final float ury) {
        this.pdf.remoteGoto(filename, name, llx, lly, urx, ury);
    }
    
    public void remoteGoto(final String filename, final int page, final float llx, final float lly, final float urx, final float ury) {
        this.pdf.remoteGoto(filename, page, llx, lly, urx, ury);
    }
    
    public void roundRectangle(float x, float y, float w, float h, float r) {
        if (w < 0.0f) {
            x += w;
            w = -w;
        }
        if (h < 0.0f) {
            y += h;
            h = -h;
        }
        if (r < 0.0f) {
            r = -r;
        }
        final float b = 0.4477f;
        this.moveTo(x + r, y);
        this.lineTo(x + w - r, y);
        this.curveTo(x + w - r * b, y, x + w, y + r * b, x + w, y + r);
        this.lineTo(x + w, y + h - r);
        this.curveTo(x + w, y + h - r * b, x + w - r * b, y + h, x + w - r, y + h);
        this.lineTo(x + r, y + h);
        this.curveTo(x + r * b, y + h, x, y + h - r * b, x, y + h - r);
        this.lineTo(x, y + r);
        this.curveTo(x, y + r * b, x + r * b, y, x + r, y);
    }
    
    public void setAction(final PdfAction action, final float llx, final float lly, final float urx, final float ury) {
        this.pdf.setAction(action, llx, lly, urx, ury);
    }
    
    public void setLiteral(final String s) {
        this.content.append(s);
    }
    
    public void setLiteral(final char c) {
        this.content.append(c);
    }
    
    public void setLiteral(final float n) {
        this.content.append(n);
    }
    
    void checkNoPattern(final PdfTemplate t) {
        if (t.getType() == 3) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.use.of.a.pattern.a.template.was.expected"));
        }
    }
    
    public void drawRadioField(float llx, float lly, float urx, float ury, final boolean on) {
        if (llx > urx) {
            final float x = llx;
            llx = urx;
            urx = x;
        }
        if (lly > ury) {
            final float y = lly;
            lly = ury;
            ury = y;
        }
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(192, 192, 192));
        this.arc(llx + 1.0f, lly + 1.0f, urx - 1.0f, ury - 1.0f, 0.0f, 360.0f);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(160, 160, 160));
        this.arc(llx + 0.5f, lly + 0.5f, urx - 0.5f, ury - 0.5f, 45.0f, 180.0f);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(1);
        this.setColorStroke(new Color(0, 0, 0));
        this.arc(llx + 1.5f, lly + 1.5f, urx - 1.5f, ury - 1.5f, 45.0f, 180.0f);
        this.stroke();
        if (on) {
            this.setLineWidth(1.0f);
            this.setLineCap(1);
            this.setColorFill(new Color(0, 0, 0));
            this.arc(llx + 4.0f, lly + 4.0f, urx - 4.0f, ury - 4.0f, 0.0f, 360.0f);
            this.fill();
        }
    }
    
    public void drawTextField(float llx, float lly, float urx, float ury) {
        if (llx > urx) {
            final float x = llx;
            llx = urx;
            urx = x;
        }
        if (lly > ury) {
            final float y = lly;
            lly = ury;
            ury = y;
        }
        this.setColorStroke(new Color(192, 192, 192));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.rectangle(llx, lly, urx - llx, ury - lly);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.setColorFill(new Color(255, 255, 255));
        this.rectangle(llx + 0.5f, lly + 0.5f, urx - llx - 1.0f, ury - lly - 1.0f);
        this.fill();
        this.setColorStroke(new Color(192, 192, 192));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.5f);
        this.lineTo(urx - 1.5f, lly + 1.5f);
        this.lineTo(urx - 1.5f, ury - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(160, 160, 160));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.0f);
        this.lineTo(llx + 1.0f, ury - 1.0f);
        this.lineTo(urx - 1.0f, ury - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(0, 0, 0));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 2.0f, lly + 2.0f);
        this.lineTo(llx + 2.0f, ury - 2.0f);
        this.lineTo(urx - 2.0f, ury - 2.0f);
        this.stroke();
    }
    
    public void drawButton(float llx, float lly, float urx, float ury, final String text, final BaseFont bf, final float size) {
        if (llx > urx) {
            final float x = llx;
            llx = urx;
            urx = x;
        }
        if (lly > ury) {
            final float y = lly;
            lly = ury;
            ury = y;
        }
        this.setColorStroke(new Color(0, 0, 0));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.rectangle(llx, lly, urx - llx, ury - lly);
        this.stroke();
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.setColorFill(new Color(192, 192, 192));
        this.rectangle(llx + 0.5f, lly + 0.5f, urx - llx - 1.0f, ury - lly - 1.0f);
        this.fill();
        this.setColorStroke(new Color(255, 255, 255));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.0f);
        this.lineTo(llx + 1.0f, ury - 1.0f);
        this.lineTo(urx - 1.0f, ury - 1.0f);
        this.stroke();
        this.setColorStroke(new Color(160, 160, 160));
        this.setLineWidth(1.0f);
        this.setLineCap(0);
        this.moveTo(llx + 1.0f, lly + 1.0f);
        this.lineTo(urx - 1.0f, lly + 1.0f);
        this.lineTo(urx - 1.0f, ury - 1.0f);
        this.stroke();
        this.resetRGBColorFill();
        this.beginText();
        this.setFontAndSize(bf, size);
        this.showTextAligned(1, text, llx + (urx - llx) / 2.0f, lly + (ury - lly - size) / 2.0f, 0.0f);
        this.endText();
    }
    
    public Graphics2D createGraphicsShapes(final float width, final float height) {
        return new PdfGraphics2D(this, width, height, null, true, false, 0.0f);
    }
    
    public Graphics2D createPrinterGraphicsShapes(final float width, final float height, final PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, true, false, 0.0f, printerJob);
    }
    
    public Graphics2D createGraphics(final float width, final float height) {
        return new PdfGraphics2D(this, width, height, null, false, false, 0.0f);
    }
    
    public Graphics2D createPrinterGraphics(final float width, final float height, final PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, false, false, 0.0f, printerJob);
    }
    
    public Graphics2D createGraphics(final float width, final float height, final boolean convertImagesToJPEG, final float quality) {
        return new PdfGraphics2D(this, width, height, null, false, convertImagesToJPEG, quality);
    }
    
    public Graphics2D createPrinterGraphics(final float width, final float height, final boolean convertImagesToJPEG, final float quality, final PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, false, convertImagesToJPEG, quality, printerJob);
    }
    
    public Graphics2D createGraphicsShapes(final float width, final float height, final boolean convertImagesToJPEG, final float quality) {
        return new PdfGraphics2D(this, width, height, null, true, convertImagesToJPEG, quality);
    }
    
    public Graphics2D createPrinterGraphicsShapes(final float width, final float height, final boolean convertImagesToJPEG, final float quality, final PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, null, true, convertImagesToJPEG, quality, printerJob);
    }
    
    public Graphics2D createGraphics(final float width, final float height, final FontMapper fontMapper) {
        return new PdfGraphics2D(this, width, height, fontMapper, false, false, 0.0f);
    }
    
    public Graphics2D createPrinterGraphics(final float width, final float height, final FontMapper fontMapper, final PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, fontMapper, false, false, 0.0f, printerJob);
    }
    
    public Graphics2D createGraphics(final float width, final float height, final FontMapper fontMapper, final boolean convertImagesToJPEG, final float quality) {
        return new PdfGraphics2D(this, width, height, fontMapper, false, convertImagesToJPEG, quality);
    }
    
    public Graphics2D createPrinterGraphics(final float width, final float height, final FontMapper fontMapper, final boolean convertImagesToJPEG, final float quality, final PrinterJob printerJob) {
        return new PdfPrinterGraphics2D(this, width, height, fontMapper, false, convertImagesToJPEG, quality, printerJob);
    }
    
    PageResources getPageResources() {
        return this.pdf.getPageResources();
    }
    
    public void setGState(final PdfGState gstate) {
        final PdfObject[] obj = this.writer.addSimpleExtGState(gstate);
        final PageResources prs = this.getPageResources();
        final PdfName name = prs.addExtGState((PdfName)obj[0], (PdfIndirectReference)obj[1]);
        this.content.append(name.getBytes()).append(" gs").append_i(this.separator);
    }
    
    public void beginLayer(final PdfOCG layer) {
        if (layer instanceof PdfLayer && ((PdfLayer)layer).getTitle() != null) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.title.is.not.a.layer"));
        }
        if (this.layerDepth == null) {
            this.layerDepth = new ArrayList<Integer>();
        }
        if (layer instanceof PdfLayerMembership) {
            this.layerDepth.add(1);
            this.beginLayer2(layer);
            return;
        }
        int n = 0;
        for (PdfLayer la = (PdfLayer)layer; la != null; la = la.getParent()) {
            if (la.getTitle() == null) {
                this.beginLayer2(la);
                ++n;
            }
        }
        this.layerDepth.add(n);
    }
    
    private void beginLayer2(final PdfOCG layer) {
        PdfName name = (PdfName)this.writer.addSimpleProperty(layer, layer.getRef())[0];
        final PageResources prs = this.getPageResources();
        name = prs.addProperty(name, layer.getRef());
        this.content.append("/OC ").append(name.getBytes()).append(" BDC").append_i(this.separator);
    }
    
    public void endLayer() {
        int n = 1;
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            n = this.layerDepth.get(this.layerDepth.size() - 1);
            this.layerDepth.remove(this.layerDepth.size() - 1);
            while (n-- > 0) {
                this.content.append("EMC").append_i(this.separator);
            }
            return;
        }
        throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators"));
    }
    
    public void transform(final AffineTransform af) {
        final double[] arr = new double[6];
        af.getMatrix(arr);
        this.content.append(arr[0]).append(' ').append(arr[1]).append(' ').append(arr[2]).append(' ');
        this.content.append(arr[3]).append(' ').append(arr[4]).append(' ').append(arr[5]).append(" cm").append_i(this.separator);
    }
    
    void addAnnotation(final PdfAnnotation annot) {
        this.writer.addAnnotation(annot);
    }
    
    public void setDefaultColorspace(final PdfName name, final PdfObject obj) {
        final PageResources prs = this.getPageResources();
        prs.addDefaultColor(name, obj);
    }
    
    public void beginMarkedContentSequence(final PdfStructureElement struc) {
        final PdfDictionary dict = new PdfDictionary();
        this.beginMarkedContentSequence(struc, dict);
    }
    
    public void beginMarkedContentSequence(final PdfStructureElement struc, final PdfDictionary dict) {
        final PdfObject obj = struc.get(PdfName.K);
        final int mark = this.pdf.getMarkPoint();
        if (obj != null) {
            PdfArray ar = null;
            if (obj.isNumber()) {
                ar = new PdfArray();
                ar.add(obj);
                struc.put(PdfName.K, ar);
            }
            else {
                if (!obj.isArray()) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("unknown.object.at.k.1", obj.getClass().toString()));
                }
                ar = (PdfArray)obj;
                if (!ar.getPdfObject(0).isNumber()) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.structure.has.kids"));
                }
            }
            final PdfDictionary dic = new PdfDictionary(PdfName.MCR);
            dic.put(PdfName.PG, this.writer.getCurrentPage());
            dic.put(PdfName.MCID, new PdfNumber(mark));
            ar.add(dic);
            struc.setPageMark(this.writer.getPageNumber() - 1, -1);
        }
        else {
            struc.setPageMark(this.writer.getPageNumber() - 1, mark);
            struc.put(PdfName.PG, this.writer.getCurrentPage());
        }
        this.pdf.incMarkPoint();
        ++this.mcDepth;
        dict.put(PdfName.MCID, new PdfNumber(mark));
        this.content.append(struc.get(PdfName.S).getBytes()).append(" ");
        try {
            dict.toPdf(this.writer, this.content);
        }
        catch (final IOException e) {
            throw new ExceptionConverter(e);
        }
        this.content.append(" BDC").append_i(this.separator);
    }
    
    public void endMarkedContentSequence() {
        if (this.mcDepth == 0) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.marked.content.operators"));
        }
        --this.mcDepth;
        this.content.append("EMC").append_i(this.separator);
    }
    
    public void beginMarkedContentSequence(final PdfName tag, final PdfDictionary property, final boolean inline) {
        if (property == null) {
            this.content.append(tag.getBytes()).append(" BMC").append_i(this.separator);
            return;
        }
        this.content.append(tag.getBytes()).append(' ');
        Label_0164: {
            if (inline) {
                try {
                    property.toPdf(this.writer, this.content);
                    break Label_0164;
                }
                catch (final Exception e) {
                    throw new ExceptionConverter(e);
                }
            }
            PdfObject[] objs;
            if (this.writer.propertyExists(property)) {
                objs = this.writer.addSimpleProperty(property, null);
            }
            else {
                objs = this.writer.addSimpleProperty(property, this.writer.getPdfIndirectReference());
            }
            PdfName name = (PdfName)objs[0];
            final PageResources prs = this.getPageResources();
            name = prs.addProperty(name, (PdfIndirectReference)objs[1]);
            this.content.append(name.getBytes());
        }
        this.content.append(" BDC").append_i(this.separator);
        ++this.mcDepth;
    }
    
    public void beginMarkedContentSequence(final PdfName tag) {
        this.beginMarkedContentSequence(tag, null, false);
    }
    
    public void sanityCheck() {
        if (this.mcDepth != 0) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.marked.content.operators"));
        }
        if (this.inText) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators"));
        }
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators"));
        }
        if (!this.stateList.isEmpty()) {
            throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators"));
        }
    }
    
    static {
        unitRect = new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f };
        (PdfContentByte.abrev = new HashMap<PdfName, String>()).put(PdfName.BITSPERCOMPONENT, "/BPC ");
        PdfContentByte.abrev.put(PdfName.COLORSPACE, "/CS ");
        PdfContentByte.abrev.put(PdfName.DECODE, "/D ");
        PdfContentByte.abrev.put(PdfName.DECODEPARMS, "/DP ");
        PdfContentByte.abrev.put(PdfName.FILTER, "/F ");
        PdfContentByte.abrev.put(PdfName.HEIGHT, "/H ");
        PdfContentByte.abrev.put(PdfName.IMAGEMASK, "/IM ");
        PdfContentByte.abrev.put(PdfName.INTENT, "/Intent ");
        PdfContentByte.abrev.put(PdfName.INTERPOLATE, "/I ");
        PdfContentByte.abrev.put(PdfName.WIDTH, "/W ");
    }
    
    static class GraphicState
    {
        FontDetails fontDetails;
        ColorDetails colorDetails;
        float size;
        protected float xTLM;
        protected float yTLM;
        protected float leading;
        protected float scale;
        protected float charSpace;
        protected float wordSpace;
        
        GraphicState() {
            this.xTLM = 0.0f;
            this.yTLM = 0.0f;
            this.leading = 0.0f;
            this.scale = 100.0f;
            this.charSpace = 0.0f;
            this.wordSpace = 0.0f;
        }
        
        GraphicState(final GraphicState cp) {
            this.xTLM = 0.0f;
            this.yTLM = 0.0f;
            this.leading = 0.0f;
            this.scale = 100.0f;
            this.charSpace = 0.0f;
            this.wordSpace = 0.0f;
            this.fontDetails = cp.fontDetails;
            this.colorDetails = cp.colorDetails;
            this.size = cp.size;
            this.xTLM = cp.xTLM;
            this.yTLM = cp.yTLM;
            this.leading = cp.leading;
            this.scale = cp.scale;
            this.charSpace = cp.charSpace;
            this.wordSpace = cp.wordSpace;
        }
    }
}
