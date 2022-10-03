package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontFactory;
import java.io.IOException;
import java.util.Locale;
import java.io.UnsupportedEncodingException;
import com.lowagie.text.pdf.BaseFont;

public class MetaFont extends MetaObject
{
    static final String[] fontNames;
    static final int MARKER_BOLD = 1;
    static final int MARKER_ITALIC = 2;
    static final int MARKER_COURIER = 0;
    static final int MARKER_HELVETICA = 4;
    static final int MARKER_TIMES = 8;
    static final int MARKER_SYMBOL = 12;
    static final int DEFAULT_PITCH = 0;
    static final int FIXED_PITCH = 1;
    static final int VARIABLE_PITCH = 2;
    static final int FF_DONTCARE = 0;
    static final int FF_ROMAN = 1;
    static final int FF_SWISS = 2;
    static final int FF_MODERN = 3;
    static final int FF_SCRIPT = 4;
    static final int FF_DECORATIVE = 5;
    static final int BOLDTHRESHOLD = 600;
    static final int nameSize = 32;
    static final int ETO_OPAQUE = 2;
    static final int ETO_CLIPPED = 4;
    int height;
    float angle;
    int bold;
    int italic;
    boolean underline;
    boolean strikeout;
    int charset;
    int pitchAndFamily;
    String faceName;
    BaseFont font;
    
    public MetaFont() {
        this.faceName = "arial";
        this.font = null;
        this.type = 3;
    }
    
    public void init(final InputMeta in) throws IOException {
        this.height = Math.abs(in.readShort());
        in.skip(2);
        this.angle = (float)(in.readShort() / 1800.0 * 3.141592653589793);
        in.skip(2);
        this.bold = ((in.readShort() >= 600) ? 1 : 0);
        this.italic = ((in.readByte() != 0) ? 2 : 0);
        this.underline = (in.readByte() != 0);
        this.strikeout = (in.readByte() != 0);
        this.charset = in.readByte();
        in.skip(3);
        this.pitchAndFamily = in.readByte();
        final byte[] name = new byte[32];
        int k;
        for (k = 0; k < 32; ++k) {
            final int c = in.readByte();
            if (c == 0) {
                break;
            }
            name[k] = (byte)c;
        }
        try {
            this.faceName = new String(name, 0, k, "Cp1252");
        }
        catch (final UnsupportedEncodingException e) {
            this.faceName = new String(name, 0, k);
        }
        this.faceName = this.faceName.toLowerCase(Locale.ROOT);
    }
    
    public BaseFont getFont() {
        if (this.font != null) {
            return this.font;
        }
        final Font ff2 = FontFactory.getFont(this.faceName, "Cp1252", true, 10.0f, ((this.italic != 0) ? 2 : 0) | ((this.bold != 0) ? 1 : 0));
        this.font = ff2.getBaseFont();
        if (this.font != null) {
            return this.font;
        }
        Label_0463: {
            String fontName = null;
            Block_13: {
                if (this.faceName.indexOf("courier") != -1 || this.faceName.indexOf("terminal") != -1 || this.faceName.indexOf("fixedsys") != -1) {
                    fontName = MetaFont.fontNames[0 + this.italic + this.bold];
                }
                else if (this.faceName.indexOf("ms sans serif") != -1 || this.faceName.indexOf("arial") != -1 || this.faceName.indexOf("system") != -1) {
                    fontName = MetaFont.fontNames[4 + this.italic + this.bold];
                }
                else if (this.faceName.indexOf("arial black") != -1) {
                    fontName = MetaFont.fontNames[4 + this.italic + 1];
                }
                else if (this.faceName.indexOf("times") != -1 || this.faceName.indexOf("ms serif") != -1 || this.faceName.indexOf("roman") != -1) {
                    fontName = MetaFont.fontNames[8 + this.italic + this.bold];
                }
                else if (this.faceName.indexOf("symbol") != -1) {
                    fontName = MetaFont.fontNames[12];
                }
                else {
                    final int pitch = this.pitchAndFamily & 0x3;
                    final int family = this.pitchAndFamily >> 4 & 0x7;
                    switch (family) {
                        case 3: {
                            fontName = MetaFont.fontNames[0 + this.italic + this.bold];
                            break;
                        }
                        case 1: {
                            fontName = MetaFont.fontNames[8 + this.italic + this.bold];
                            break;
                        }
                        case 2:
                        case 4:
                        case 5: {
                            fontName = MetaFont.fontNames[4 + this.italic + this.bold];
                            break;
                        }
                        default: {
                            switch (pitch) {
                                case 1: {
                                    fontName = MetaFont.fontNames[0 + this.italic + this.bold];
                                    break Label_0463;
                                }
                                default: {
                                    fontName = MetaFont.fontNames[4 + this.italic + this.bold];
                                    break Block_13;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            try {
                this.font = BaseFont.createFont(fontName, "Cp1252", false);
            }
            catch (final Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        return this.font;
    }
    
    public float getAngle() {
        return this.angle;
    }
    
    public boolean isUnderline() {
        return this.underline;
    }
    
    public boolean isStrikeout() {
        return this.strikeout;
    }
    
    public float getFontSize(final MetaState state) {
        return Math.abs(state.transformY(this.height) - state.transformY(0)) * Document.wmfFontCorrection;
    }
    
    static {
        fontNames = new String[] { "Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique", "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique", "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic", "Symbol", "ZapfDingbats" };
    }
}
