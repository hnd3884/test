package com.lowagie.text;

import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;

public class Font implements Comparable
{
    public static final int COURIER = 0;
    public static final int HELVETICA = 1;
    public static final int TIMES_ROMAN = 2;
    public static final int SYMBOL = 3;
    public static final int ZAPFDINGBATS = 4;
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINE = 4;
    public static final int STRIKETHRU = 8;
    public static final int BOLDITALIC = 3;
    public static final int UNDEFINED = -1;
    public static final int DEFAULTSIZE = 12;
    private int family;
    private float size;
    private int style;
    private Color color;
    private BaseFont baseFont;
    
    public Font(final Font other) {
        this.family = -1;
        this.size = -1.0f;
        this.style = -1;
        this.color = null;
        this.baseFont = null;
        this.family = other.family;
        this.size = other.size;
        this.style = other.style;
        this.color = other.color;
        this.baseFont = other.baseFont;
    }
    
    public Font(final int family, final float size, final int style, final Color color) {
        this.family = -1;
        this.size = -1.0f;
        this.style = -1;
        this.color = null;
        this.baseFont = null;
        this.family = family;
        this.size = size;
        this.style = style;
        this.color = color;
    }
    
    public Font(final BaseFont bf, final float size, final int style, final Color color) {
        this.family = -1;
        this.size = -1.0f;
        this.style = -1;
        this.color = null;
        this.baseFont = null;
        this.baseFont = bf;
        this.size = size;
        this.style = style;
        this.color = color;
    }
    
    public Font(final BaseFont bf, final float size, final int style) {
        this(bf, size, style, null);
    }
    
    public Font(final BaseFont bf, final float size) {
        this(bf, size, -1, null);
    }
    
    public Font(final BaseFont bf) {
        this(bf, -1.0f, -1, null);
    }
    
    public Font(final int family, final float size, final int style) {
        this(family, size, style, null);
    }
    
    public Font(final int family, final float size) {
        this(family, size, -1, null);
    }
    
    public Font(final int family) {
        this(family, -1.0f, -1, null);
    }
    
    public Font() {
        this(-1, -1.0f, -1, null);
    }
    
    @Override
    public int compareTo(final Object object) {
        if (object == null) {
            return -1;
        }
        try {
            final Font font = (Font)object;
            if (this.baseFont != null && !this.baseFont.equals(font.getBaseFont())) {
                return -2;
            }
            if (this.family != font.getFamily()) {
                return 1;
            }
            if (this.size != font.getSize()) {
                return 2;
            }
            if (this.style != font.getStyle()) {
                return 3;
            }
            if (this.color == null) {
                if (font.color == null) {
                    return 0;
                }
                return 4;
            }
            else {
                if (font.color == null) {
                    return 4;
                }
                if (this.color.equals(font.getColor())) {
                    return 0;
                }
                return 4;
            }
        }
        catch (final ClassCastException cce) {
            return -3;
        }
    }
    
    public int getFamily() {
        return this.family;
    }
    
    public String getFamilyname() {
        String tmp = "unknown";
        switch (this.getFamily()) {
            case 0: {
                return "Courier";
            }
            case 1: {
                return "Helvetica";
            }
            case 2: {
                return "Times-Roman";
            }
            case 3: {
                return "Symbol";
            }
            case 4: {
                return "ZapfDingbats";
            }
            default: {
                if (this.baseFont != null) {
                    final String[][] names = this.baseFont.getFamilyFontName();
                    for (int i = 0; i < names.length; ++i) {
                        if ("0".equals(names[i][2])) {
                            return names[i][3];
                        }
                        if ("1033".equals(names[i][2])) {
                            tmp = names[i][3];
                        }
                        if ("".equals(names[i][2])) {
                            tmp = names[i][3];
                        }
                    }
                }
                return tmp;
            }
        }
    }
    
    public void setFamily(final String family) {
        this.family = getFamilyIndex(family);
    }
    
    public static int getFamilyIndex(final String family) {
        if (family.equalsIgnoreCase("Courier")) {
            return 0;
        }
        if (family.equalsIgnoreCase("Helvetica")) {
            return 1;
        }
        if (family.equalsIgnoreCase("Times-Roman")) {
            return 2;
        }
        if (family.equalsIgnoreCase("Symbol")) {
            return 3;
        }
        if (family.equalsIgnoreCase("ZapfDingbats")) {
            return 4;
        }
        return -1;
    }
    
    public float getSize() {
        return this.size;
    }
    
    public float getCalculatedSize() {
        float s = this.size;
        if (s == -1.0f) {
            s = 12.0f;
        }
        return s;
    }
    
    public float getCalculatedLeading(final float linespacing) {
        return linespacing * this.getCalculatedSize();
    }
    
    public void setSize(final float size) {
        this.size = size;
    }
    
    public int getStyle() {
        return this.style;
    }
    
    public int getCalculatedStyle() {
        int style = this.style;
        if (style == -1) {
            style = 0;
        }
        if (this.baseFont != null) {
            return style;
        }
        if (this.family == 3 || this.family == 4) {
            return style;
        }
        return style & 0xFFFFFFFC;
    }
    
    public boolean isBold() {
        return this.style != -1 && (this.style & 0x1) == 0x1;
    }
    
    public boolean isItalic() {
        return this.style != -1 && (this.style & 0x2) == 0x2;
    }
    
    public boolean isUnderlined() {
        return this.style != -1 && (this.style & 0x4) == 0x4;
    }
    
    public boolean isStrikethru() {
        return this.style != -1 && (this.style & 0x8) == 0x8;
    }
    
    public void setStyle(final int style) {
        this.style = style;
    }
    
    public void setStyle(final String style) {
        if (this.style == -1) {
            this.style = 0;
        }
        this.style |= getStyleValue(style);
    }
    
    public static int getStyleValue(final String style) {
        int s = 0;
        if (style.indexOf("normal") != -1) {
            s |= 0x0;
        }
        if (style.indexOf("bold") != -1) {
            s |= 0x1;
        }
        if (style.indexOf("italic") != -1) {
            s |= 0x2;
        }
        if (style.indexOf("oblique") != -1) {
            s |= 0x2;
        }
        if (style.indexOf("underline") != -1) {
            s |= 0x4;
        }
        if (style.indexOf("line-through") != -1) {
            s |= 0x8;
        }
        return s;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor(final Color color) {
        this.color = color;
    }
    
    public void setColor(final int red, final int green, final int blue) {
        this.color = new Color(red, green, blue);
    }
    
    public BaseFont getBaseFont() {
        return this.baseFont;
    }
    
    public BaseFont getCalculatedBaseFont(final boolean specialEncoding) {
        if (this.baseFont != null) {
            return this.baseFont;
        }
        int style = this.style;
        if (style == -1) {
            style = 0;
        }
        String fontName = "Helvetica";
        String encoding = "Cp1252";
        BaseFont cfont = null;
        Label_0261: {
            Block_5: {
                switch (this.family) {
                    case 0: {
                        switch (style & 0x3) {
                            case 1: {
                                fontName = "Courier-Bold";
                                break Label_0261;
                            }
                            case 2: {
                                fontName = "Courier-Oblique";
                                break Label_0261;
                            }
                            case 3: {
                                fontName = "Courier-BoldOblique";
                                break Label_0261;
                            }
                            default: {
                                fontName = "Courier";
                                break Label_0261;
                            }
                        }
                        break;
                    }
                    case 2: {
                        switch (style & 0x3) {
                            case 1: {
                                fontName = "Times-Bold";
                                break Label_0261;
                            }
                            case 2: {
                                fontName = "Times-Italic";
                                break Label_0261;
                            }
                            case 3: {
                                fontName = "Times-BoldItalic";
                                break Label_0261;
                            }
                            default: {
                                fontName = "Times-Roman";
                                break Label_0261;
                            }
                        }
                        break;
                    }
                    case 3: {
                        fontName = "Symbol";
                        if (specialEncoding) {
                            encoding = "Symbol";
                            break;
                        }
                        break;
                    }
                    case 4: {
                        fontName = "ZapfDingbats";
                        if (specialEncoding) {
                            encoding = "ZapfDingbats";
                            break;
                        }
                        break;
                    }
                    default: {
                        switch (style & 0x3) {
                            case 1: {
                                fontName = "Helvetica-Bold";
                                break Label_0261;
                            }
                            case 2: {
                                fontName = "Helvetica-Oblique";
                                break Label_0261;
                            }
                            case 3: {
                                fontName = "Helvetica-BoldOblique";
                                break Label_0261;
                            }
                            default: {
                                fontName = "Helvetica";
                                break Block_5;
                            }
                        }
                        break;
                    }
                }
            }
            try {
                cfont = BaseFont.createFont(fontName, encoding, false);
            }
            catch (final Exception ee) {
                throw new ExceptionConverter(ee);
            }
        }
        return cfont;
    }
    
    public boolean isStandardFont() {
        return this.family == -1 && this.size == -1.0f && this.style == -1 && this.color == null && this.baseFont == null;
    }
    
    public Font difference(final Font font) {
        if (font == null) {
            return this;
        }
        float dSize = font.size;
        if (dSize == -1.0f) {
            dSize = this.size;
        }
        int dStyle = -1;
        int style1 = this.style;
        int style2 = font.getStyle();
        if (style1 != -1 || style2 != -1) {
            if (style1 == -1) {
                style1 = 0;
            }
            if (style2 == -1) {
                style2 = 0;
            }
            dStyle = (style1 | style2);
        }
        Color dColor = font.color;
        if (dColor == null) {
            dColor = this.color;
        }
        if (font.baseFont != null) {
            return new Font(font.baseFont, dSize, dStyle, dColor);
        }
        if (font.getFamily() != -1) {
            return new Font(font.family, dSize, dStyle, dColor);
        }
        if (this.baseFont == null) {
            return new Font(this.family, dSize, dStyle, dColor);
        }
        if (dStyle == style1) {
            return new Font(this.baseFont, dSize, dStyle, dColor);
        }
        return FontFactory.getFont(this.getFamilyname(), dSize, dStyle, dColor);
    }
}
