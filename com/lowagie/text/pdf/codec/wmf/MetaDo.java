package com.lowagie.text.pdf.codec.wmf;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.BaseFont;
import java.io.IOException;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.awt.Point;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.io.InputStream;
import com.lowagie.text.pdf.PdfContentByte;

public class MetaDo
{
    public static final int META_SETBKCOLOR = 513;
    public static final int META_SETBKMODE = 258;
    public static final int META_SETMAPMODE = 259;
    public static final int META_SETROP2 = 260;
    public static final int META_SETRELABS = 261;
    public static final int META_SETPOLYFILLMODE = 262;
    public static final int META_SETSTRETCHBLTMODE = 263;
    public static final int META_SETTEXTCHAREXTRA = 264;
    public static final int META_SETTEXTCOLOR = 521;
    public static final int META_SETTEXTJUSTIFICATION = 522;
    public static final int META_SETWINDOWORG = 523;
    public static final int META_SETWINDOWEXT = 524;
    public static final int META_SETVIEWPORTORG = 525;
    public static final int META_SETVIEWPORTEXT = 526;
    public static final int META_OFFSETWINDOWORG = 527;
    public static final int META_SCALEWINDOWEXT = 1040;
    public static final int META_OFFSETVIEWPORTORG = 529;
    public static final int META_SCALEVIEWPORTEXT = 1042;
    public static final int META_LINETO = 531;
    public static final int META_MOVETO = 532;
    public static final int META_EXCLUDECLIPRECT = 1045;
    public static final int META_INTERSECTCLIPRECT = 1046;
    public static final int META_ARC = 2071;
    public static final int META_ELLIPSE = 1048;
    public static final int META_FLOODFILL = 1049;
    public static final int META_PIE = 2074;
    public static final int META_RECTANGLE = 1051;
    public static final int META_ROUNDRECT = 1564;
    public static final int META_PATBLT = 1565;
    public static final int META_SAVEDC = 30;
    public static final int META_SETPIXEL = 1055;
    public static final int META_OFFSETCLIPRGN = 544;
    public static final int META_TEXTOUT = 1313;
    public static final int META_BITBLT = 2338;
    public static final int META_STRETCHBLT = 2851;
    public static final int META_POLYGON = 804;
    public static final int META_POLYLINE = 805;
    public static final int META_ESCAPE = 1574;
    public static final int META_RESTOREDC = 295;
    public static final int META_FILLREGION = 552;
    public static final int META_FRAMEREGION = 1065;
    public static final int META_INVERTREGION = 298;
    public static final int META_PAINTREGION = 299;
    public static final int META_SELECTCLIPREGION = 300;
    public static final int META_SELECTOBJECT = 301;
    public static final int META_SETTEXTALIGN = 302;
    public static final int META_CHORD = 2096;
    public static final int META_SETMAPPERFLAGS = 561;
    public static final int META_EXTTEXTOUT = 2610;
    public static final int META_SETDIBTODEV = 3379;
    public static final int META_SELECTPALETTE = 564;
    public static final int META_REALIZEPALETTE = 53;
    public static final int META_ANIMATEPALETTE = 1078;
    public static final int META_SETPALENTRIES = 55;
    public static final int META_POLYPOLYGON = 1336;
    public static final int META_RESIZEPALETTE = 313;
    public static final int META_DIBBITBLT = 2368;
    public static final int META_DIBSTRETCHBLT = 2881;
    public static final int META_DIBCREATEPATTERNBRUSH = 322;
    public static final int META_STRETCHDIB = 3907;
    public static final int META_EXTFLOODFILL = 1352;
    public static final int META_DELETEOBJECT = 496;
    public static final int META_CREATEPALETTE = 247;
    public static final int META_CREATEPATTERNBRUSH = 505;
    public static final int META_CREATEPENINDIRECT = 762;
    public static final int META_CREATEFONTINDIRECT = 763;
    public static final int META_CREATEBRUSHINDIRECT = 764;
    public static final int META_CREATEREGION = 1791;
    public PdfContentByte cb;
    public InputMeta in;
    int left;
    int top;
    int right;
    int bottom;
    int inch;
    MetaState state;
    
    public MetaDo(final InputStream in, final PdfContentByte cb) {
        this.state = new MetaState();
        this.cb = cb;
        this.in = new InputMeta(in);
    }
    
    public void readAll() throws IOException, DocumentException {
        if (this.in.readInt() != -1698247209) {
            throw new DocumentException(MessageLocalization.getComposedMessage("not.a.placeable.windows.metafile"));
        }
        this.in.readWord();
        this.left = this.in.readShort();
        this.top = this.in.readShort();
        this.right = this.in.readShort();
        this.bottom = this.in.readShort();
        this.inch = this.in.readWord();
        this.state.setScalingX((this.right - this.left) / (float)this.inch * 72.0f);
        this.state.setScalingY((this.bottom - this.top) / (float)this.inch * 72.0f);
        this.state.setOffsetWx(this.left);
        this.state.setOffsetWy(this.top);
        this.state.setExtentWx(this.right - this.left);
        this.state.setExtentWy(this.bottom - this.top);
        this.in.readInt();
        this.in.readWord();
        this.in.skip(18);
        this.cb.setLineCap(1);
        this.cb.setLineJoin(1);
        while (true) {
            final int lenMarker = this.in.getLength();
            final int tsize = this.in.readInt();
            if (tsize < 3) {
                break;
            }
            final int function = this.in.readWord();
            switch (function) {
                case 247:
                case 322:
                case 1791: {
                    this.state.addMetaObject(new MetaObject());
                    break;
                }
                case 762: {
                    final MetaPen pen = new MetaPen();
                    pen.init(this.in);
                    this.state.addMetaObject(pen);
                    break;
                }
                case 764: {
                    final MetaBrush brush = new MetaBrush();
                    brush.init(this.in);
                    this.state.addMetaObject(brush);
                    break;
                }
                case 763: {
                    final MetaFont font = new MetaFont();
                    font.init(this.in);
                    this.state.addMetaObject(font);
                    break;
                }
                case 301: {
                    final int idx = this.in.readWord();
                    this.state.selectMetaObject(idx, this.cb);
                    break;
                }
                case 496: {
                    final int idx = this.in.readWord();
                    this.state.deleteMetaObject(idx);
                    break;
                }
                case 30: {
                    this.state.saveState(this.cb);
                    break;
                }
                case 295: {
                    final int idx = this.in.readShort();
                    this.state.restoreState(idx, this.cb);
                    break;
                }
                case 523: {
                    this.state.setOffsetWy(this.in.readShort());
                    this.state.setOffsetWx(this.in.readShort());
                    break;
                }
                case 524: {
                    this.state.setExtentWy(this.in.readShort());
                    this.state.setExtentWx(this.in.readShort());
                    break;
                }
                case 532: {
                    final int y = this.in.readShort();
                    final Point p = new Point(this.in.readShort(), y);
                    this.state.setCurrentPoint(p);
                    break;
                }
                case 531: {
                    final int y = this.in.readShort();
                    final int x = this.in.readShort();
                    final Point p2 = this.state.getCurrentPoint();
                    this.cb.moveTo(this.state.transformX(p2.x), this.state.transformY(p2.y));
                    this.cb.lineTo(this.state.transformX(x), this.state.transformY(y));
                    this.cb.stroke();
                    this.state.setCurrentPoint(new Point(x, y));
                    break;
                }
                case 805: {
                    this.state.setLineJoinPolygon(this.cb);
                    final int len = this.in.readWord();
                    int x = this.in.readShort();
                    int y2 = this.in.readShort();
                    this.cb.moveTo(this.state.transformX(x), this.state.transformY(y2));
                    for (int k = 1; k < len; ++k) {
                        x = this.in.readShort();
                        y2 = this.in.readShort();
                        this.cb.lineTo(this.state.transformX(x), this.state.transformY(y2));
                    }
                    this.cb.stroke();
                    break;
                }
                case 804: {
                    if (this.isNullStrokeFill(false)) {
                        break;
                    }
                    final int len = this.in.readWord();
                    final int sx = this.in.readShort();
                    final int sy = this.in.readShort();
                    this.cb.moveTo(this.state.transformX(sx), this.state.transformY(sy));
                    for (int k = 1; k < len; ++k) {
                        final int x2 = this.in.readShort();
                        final int y3 = this.in.readShort();
                        this.cb.lineTo(this.state.transformX(x2), this.state.transformY(y3));
                    }
                    this.cb.lineTo(this.state.transformX(sx), this.state.transformY(sy));
                    this.strokeAndFill();
                    break;
                }
                case 1336: {
                    if (this.isNullStrokeFill(false)) {
                        break;
                    }
                    final int numPoly = this.in.readWord();
                    final int[] lens = new int[numPoly];
                    for (int i = 0; i < lens.length; ++i) {
                        lens[i] = this.in.readWord();
                    }
                    for (final int len2 : lens) {
                        final int sx2 = this.in.readShort();
                        final int sy2 = this.in.readShort();
                        this.cb.moveTo(this.state.transformX(sx2), this.state.transformY(sy2));
                        for (int j = 1; j < len2; ++j) {
                            final int x3 = this.in.readShort();
                            final int y4 = this.in.readShort();
                            this.cb.lineTo(this.state.transformX(x3), this.state.transformY(y4));
                        }
                        this.cb.lineTo(this.state.transformX(sx2), this.state.transformY(sy2));
                    }
                    this.strokeAndFill();
                    break;
                }
                case 1048: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) {
                        break;
                    }
                    final int b = this.in.readShort();
                    final int r = this.in.readShort();
                    final int t = this.in.readShort();
                    final int l = this.in.readShort();
                    this.cb.arc(this.state.transformX(l), this.state.transformY(b), this.state.transformX(r), this.state.transformY(t), 0.0f, 360.0f);
                    this.strokeAndFill();
                    break;
                }
                case 2071: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) {
                        break;
                    }
                    final float yend = this.state.transformY(this.in.readShort());
                    final float xend = this.state.transformX(this.in.readShort());
                    final float ystart = this.state.transformY(this.in.readShort());
                    final float xstart = this.state.transformX(this.in.readShort());
                    final float b2 = this.state.transformY(this.in.readShort());
                    final float r2 = this.state.transformX(this.in.readShort());
                    final float t2 = this.state.transformY(this.in.readShort());
                    final float m = this.state.transformX(this.in.readShort());
                    final float cx = (r2 + m) / 2.0f;
                    final float cy = (t2 + b2) / 2.0f;
                    final float arc1 = getArc(cx, cy, xstart, ystart);
                    float arc2 = getArc(cx, cy, xend, yend);
                    arc2 -= arc1;
                    if (arc2 <= 0.0f) {
                        arc2 += 360.0f;
                    }
                    this.cb.arc(m, b2, r2, t2, arc1, arc2);
                    this.cb.stroke();
                    break;
                }
                case 2074: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) {
                        break;
                    }
                    final float yend = this.state.transformY(this.in.readShort());
                    final float xend = this.state.transformX(this.in.readShort());
                    final float ystart = this.state.transformY(this.in.readShort());
                    final float xstart = this.state.transformX(this.in.readShort());
                    final float b2 = this.state.transformY(this.in.readShort());
                    final float r2 = this.state.transformX(this.in.readShort());
                    final float t2 = this.state.transformY(this.in.readShort());
                    final float m = this.state.transformX(this.in.readShort());
                    final float cx = (r2 + m) / 2.0f;
                    final float cy = (t2 + b2) / 2.0f;
                    final float arc1 = getArc(cx, cy, xstart, ystart);
                    float arc2 = getArc(cx, cy, xend, yend);
                    arc2 -= arc1;
                    if (arc2 <= 0.0f) {
                        arc2 += 360.0f;
                    }
                    final List<float[]> ar = PdfContentByte.bezierArc(m, b2, r2, t2, arc1, arc2);
                    if (ar.isEmpty()) {
                        break;
                    }
                    float[] pt = ar.get(0);
                    this.cb.moveTo(cx, cy);
                    this.cb.lineTo(pt[0], pt[1]);
                    final Iterator<float[]> iterator = ar.iterator();
                    while (iterator.hasNext()) {
                        final float[] anAr = pt = iterator.next();
                        this.cb.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
                    }
                    this.cb.lineTo(cx, cy);
                    this.strokeAndFill();
                    break;
                }
                case 2096: {
                    if (this.isNullStrokeFill(this.state.getLineNeutral())) {
                        break;
                    }
                    final float yend = this.state.transformY(this.in.readShort());
                    final float xend = this.state.transformX(this.in.readShort());
                    final float ystart = this.state.transformY(this.in.readShort());
                    final float xstart = this.state.transformX(this.in.readShort());
                    final float b2 = this.state.transformY(this.in.readShort());
                    final float r2 = this.state.transformX(this.in.readShort());
                    final float t2 = this.state.transformY(this.in.readShort());
                    final float m = this.state.transformX(this.in.readShort());
                    float cx = (r2 + m) / 2.0f;
                    float cy = (t2 + b2) / 2.0f;
                    final float arc1 = getArc(cx, cy, xstart, ystart);
                    float arc2 = getArc(cx, cy, xend, yend);
                    arc2 -= arc1;
                    if (arc2 <= 0.0f) {
                        arc2 += 360.0f;
                    }
                    final List<float[]> ar = PdfContentByte.bezierArc(m, b2, r2, t2, arc1, arc2);
                    if (ar.isEmpty()) {
                        break;
                    }
                    float[] pt = ar.get(0);
                    cx = pt[0];
                    cy = pt[1];
                    this.cb.moveTo(cx, cy);
                    for (int k2 = 0; k2 < ar.size(); ++k2) {
                        pt = ar.get(k2);
                        this.cb.curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
                    }
                    this.cb.lineTo(cx, cy);
                    this.strokeAndFill();
                    break;
                }
                case 1051: {
                    if (this.isNullStrokeFill(true)) {
                        break;
                    }
                    final float b3 = this.state.transformY(this.in.readShort());
                    final float r3 = this.state.transformX(this.in.readShort());
                    final float t3 = this.state.transformY(this.in.readShort());
                    final float l2 = this.state.transformX(this.in.readShort());
                    this.cb.rectangle(l2, b3, r3 - l2, t3 - b3);
                    this.strokeAndFill();
                    break;
                }
                case 1564: {
                    if (this.isNullStrokeFill(true)) {
                        break;
                    }
                    final float h = this.state.transformY(0) - this.state.transformY(this.in.readShort());
                    final float w = this.state.transformX(this.in.readShort()) - this.state.transformX(0);
                    final float b4 = this.state.transformY(this.in.readShort());
                    final float r4 = this.state.transformX(this.in.readShort());
                    final float t4 = this.state.transformY(this.in.readShort());
                    final float l3 = this.state.transformX(this.in.readShort());
                    this.cb.roundRectangle(l3, b4, r4 - l3, t4 - b4, (h + w) / 4.0f);
                    this.strokeAndFill();
                    break;
                }
                case 1046: {
                    final float b3 = this.state.transformY(this.in.readShort());
                    final float r3 = this.state.transformX(this.in.readShort());
                    final float t3 = this.state.transformY(this.in.readShort());
                    final float l2 = this.state.transformX(this.in.readShort());
                    this.cb.rectangle(l2, b3, r3 - l2, t3 - b3);
                    this.cb.eoClip();
                    this.cb.newPath();
                    break;
                }
                case 2610: {
                    final int y = this.in.readShort();
                    final int x = this.in.readShort();
                    final int count = this.in.readWord();
                    final int flag = this.in.readWord();
                    int x4 = 0;
                    int y5 = 0;
                    int x5 = 0;
                    int y6 = 0;
                    if ((flag & 0x6) != 0x0) {
                        x4 = this.in.readShort();
                        y5 = this.in.readShort();
                        x5 = this.in.readShort();
                        y6 = this.in.readShort();
                    }
                    final byte[] text = new byte[count];
                    int k3;
                    for (k3 = 0; k3 < count; ++k3) {
                        final byte c = (byte)this.in.readByte();
                        if (c == 0) {
                            break;
                        }
                        text[k3] = c;
                    }
                    String s;
                    try {
                        s = new String(text, 0, k3, "Cp1252");
                    }
                    catch (final UnsupportedEncodingException e) {
                        s = new String(text, 0, k3);
                    }
                    this.outputText(x, y, flag, x4, y5, x5, y6, s);
                    break;
                }
                case 1313: {
                    int count2 = this.in.readWord();
                    final byte[] text2 = new byte[count2];
                    int i;
                    for (i = 0; i < count2; ++i) {
                        final byte c2 = (byte)this.in.readByte();
                        if (c2 == 0) {
                            break;
                        }
                        text2[i] = c2;
                    }
                    String s2;
                    try {
                        s2 = new String(text2, 0, i, "Cp1252");
                    }
                    catch (final UnsupportedEncodingException e2) {
                        s2 = new String(text2, 0, i);
                    }
                    count2 = (count2 + 1 & 0xFFFE);
                    this.in.skip(count2 - i);
                    final int y7 = this.in.readShort();
                    final int x6 = this.in.readShort();
                    this.outputText(x6, y7, 0, 0, 0, 0, 0, s2);
                    break;
                }
                case 513: {
                    this.state.setCurrentBackgroundColor(this.in.readColor());
                    break;
                }
                case 521: {
                    this.state.setCurrentTextColor(this.in.readColor());
                    break;
                }
                case 302: {
                    this.state.setTextAlign(this.in.readWord());
                    break;
                }
                case 258: {
                    this.state.setBackgroundMode(this.in.readWord());
                    break;
                }
                case 262: {
                    this.state.setPolyFillMode(this.in.readWord());
                    break;
                }
                case 1055: {
                    final Color color = this.in.readColor();
                    final int y8 = this.in.readShort();
                    final int x7 = this.in.readShort();
                    this.cb.saveState();
                    this.cb.setColorFill(color);
                    this.cb.rectangle(this.state.transformX(x7), this.state.transformY(y8), 0.2f, 0.2f);
                    this.cb.fill();
                    this.cb.restoreState();
                    break;
                }
            }
            this.in.skip(tsize * 2 - (this.in.getLength() - lenMarker));
        }
        this.state.cleanup(this.cb);
    }
    
    public void outputText(final int x, final int y, final int flag, final int x1, final int y1, final int x2, final int y2, final String text) {
        final MetaFont font = this.state.getCurrentFont();
        final float refX = this.state.transformX(x);
        final float refY = this.state.transformY(y);
        final float angle = this.state.transformAngle(font.getAngle());
        final float sin = (float)Math.sin(angle);
        final float cos = (float)Math.cos(angle);
        final float fontSize = font.getFontSize(this.state);
        final BaseFont bf = font.getFont();
        final int align = this.state.getTextAlign();
        final float textWidth = bf.getWidthPoint(text, fontSize);
        float tx = 0.0f;
        float ty = 0.0f;
        final float descender = bf.getFontDescriptor(3, fontSize);
        final float ury = bf.getFontDescriptor(8, fontSize);
        this.cb.saveState();
        this.cb.concatCTM(cos, sin, -sin, cos, refX, refY);
        if ((align & 0x6) == 0x6) {
            tx = -textWidth / 2.0f;
        }
        else if ((align & 0x2) == 0x2) {
            tx = -textWidth;
        }
        if ((align & 0x18) == 0x18) {
            ty = 0.0f;
        }
        else if ((align & 0x8) == 0x8) {
            ty = -descender;
        }
        else {
            ty = -ury;
        }
        if (this.state.getBackgroundMode() == 2) {
            final Color textColor = this.state.getCurrentBackgroundColor();
            this.cb.setColorFill(textColor);
            this.cb.rectangle(tx, ty + descender, textWidth, ury - descender);
            this.cb.fill();
        }
        final Color textColor = this.state.getCurrentTextColor();
        this.cb.setColorFill(textColor);
        this.cb.beginText();
        this.cb.setFontAndSize(bf, fontSize);
        this.cb.setTextMatrix(tx, ty);
        this.cb.showText(text);
        this.cb.endText();
        if (font.isUnderline()) {
            this.cb.rectangle(tx, ty - fontSize / 4.0f, textWidth, fontSize / 15.0f);
            this.cb.fill();
        }
        if (font.isStrikeout()) {
            this.cb.rectangle(tx, ty + fontSize / 3.0f, textWidth, fontSize / 15.0f);
            this.cb.fill();
        }
        this.cb.restoreState();
    }
    
    public boolean isNullStrokeFill(final boolean isRectangle) {
        final MetaPen pen = this.state.getCurrentPen();
        final MetaBrush brush = this.state.getCurrentBrush();
        final boolean noPen = pen.getStyle() == 5;
        final int style = brush.getStyle();
        final boolean isBrush = style == 0 || (style == 2 && this.state.getBackgroundMode() == 2);
        final boolean result = noPen && !isBrush;
        if (!noPen) {
            if (isRectangle) {
                this.state.setLineJoinRectangle(this.cb);
            }
            else {
                this.state.setLineJoinPolygon(this.cb);
            }
        }
        return result;
    }
    
    public void strokeAndFill() {
        final MetaPen pen = this.state.getCurrentPen();
        final MetaBrush brush = this.state.getCurrentBrush();
        final int penStyle = pen.getStyle();
        final int brushStyle = brush.getStyle();
        if (penStyle == 5) {
            this.cb.closePath();
            if (this.state.getPolyFillMode() == 1) {
                this.cb.eoFill();
            }
            else {
                this.cb.fill();
            }
        }
        else {
            final boolean isBrush = brushStyle == 0 || (brushStyle == 2 && this.state.getBackgroundMode() == 2);
            if (isBrush) {
                if (this.state.getPolyFillMode() == 1) {
                    this.cb.closePathEoFillStroke();
                }
                else {
                    this.cb.closePathFillStroke();
                }
            }
            else {
                this.cb.closePathStroke();
            }
        }
    }
    
    static float getArc(final float xCenter, final float yCenter, final float xDot, final float yDot) {
        double s = Math.atan2(yDot - yCenter, xDot - xCenter);
        if (s < 0.0) {
            s += 6.283185307179586;
        }
        return (float)(s / 3.141592653589793 * 180.0);
    }
    
    public static byte[] wrapBMP(final Image image) throws IOException {
        if (image.getOriginalType() != 4) {
            throw new IOException(MessageLocalization.getComposedMessage("only.bmp.can.be.wrapped.in.wmf"));
        }
        byte[] data = null;
        if (image.getOriginalData() == null) {
            final InputStream imgIn = image.getUrl().openStream();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            int b = 0;
            while ((b = imgIn.read()) != -1) {
                out.write(b);
            }
            imgIn.close();
            data = out.toByteArray();
        }
        else {
            data = image.getOriginalData();
        }
        final int sizeBmpWords = data.length - 14 + 1 >>> 1;
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        writeWord(os, 1);
        writeWord(os, 9);
        writeWord(os, 768);
        writeDWord(os, 23 + (13 + sizeBmpWords) + 3);
        writeWord(os, 1);
        writeDWord(os, 14 + sizeBmpWords);
        writeWord(os, 0);
        writeDWord(os, 4);
        writeWord(os, 259);
        writeWord(os, 8);
        writeDWord(os, 5);
        writeWord(os, 523);
        writeWord(os, 0);
        writeWord(os, 0);
        writeDWord(os, 5);
        writeWord(os, 524);
        writeWord(os, (int)image.getHeight());
        writeWord(os, (int)image.getWidth());
        writeDWord(os, 13 + sizeBmpWords);
        writeWord(os, 2881);
        writeDWord(os, 13369376);
        writeWord(os, (int)image.getHeight());
        writeWord(os, (int)image.getWidth());
        writeWord(os, 0);
        writeWord(os, 0);
        writeWord(os, (int)image.getHeight());
        writeWord(os, (int)image.getWidth());
        writeWord(os, 0);
        writeWord(os, 0);
        os.write(data, 14, data.length - 14);
        if ((data.length & 0x1) == 0x1) {
            os.write(0);
        }
        writeDWord(os, 3);
        writeWord(os, 0);
        os.close();
        return os.toByteArray();
    }
    
    public static void writeWord(final OutputStream os, final int v) throws IOException {
        os.write(v & 0xFF);
        os.write(v >>> 8 & 0xFF);
    }
    
    public static void writeDWord(final OutputStream os, final int v) throws IOException {
        writeWord(os, v & 0xFFFF);
        writeWord(os, v >>> 16 & 0xFFFF);
    }
}
