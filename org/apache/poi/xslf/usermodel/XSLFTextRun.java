package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontScheme;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.Hyperlink;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.model.ParagraphPropertyFetcher;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.apache.poi.xslf.model.CharacterPropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.draw.DrawPaint;
import java.awt.Color;
import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.POILogger;
import org.apache.poi.sl.usermodel.TextRun;

public class XSLFTextRun implements TextRun
{
    private static final POILogger LOG;
    private final XmlObject _r;
    private final XSLFTextParagraph _p;
    
    protected XSLFTextRun(final XmlObject r, final XSLFTextParagraph p) {
        this._r = r;
        this._p = p;
        if (!(r instanceof CTRegularTextRun) && !(r instanceof CTTextLineBreak) && !(r instanceof CTTextField)) {
            throw new OpenXML4JRuntimeException("unsupported text run of type " + r.getClass());
        }
    }
    
    XSLFTextParagraph getParentParagraph() {
        return this._p;
    }
    
    public String getRawText() {
        if (this._r instanceof CTTextField) {
            return ((CTTextField)this._r).getT();
        }
        if (this._r instanceof CTTextLineBreak) {
            return "\n";
        }
        return ((CTRegularTextRun)this._r).getT();
    }
    
    public void setText(final String text) {
        if (this._r instanceof CTTextField) {
            ((CTTextField)this._r).setT(text);
        }
        else if (!(this._r instanceof CTTextLineBreak)) {
            ((CTRegularTextRun)this._r).setT(text);
        }
    }
    
    @Internal
    public XmlObject getXmlObject() {
        return this._r;
    }
    
    public void setFontColor(final Color color) {
        this.setFontColor((PaintStyle)DrawPaint.createSolidPaint(color));
    }
    
    public void setFontColor(final PaintStyle color) {
        if (!(color instanceof PaintStyle.SolidPaint)) {
            XSLFTextRun.LOG.log(5, new Object[] { "Currently only SolidPaint is supported!" });
            return;
        }
        final PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint)color;
        final Color c = DrawPaint.applyColorTransform(sp.getSolidColor());
        final CTTextCharacterProperties rPr = this.getRPr(true);
        final CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
        final XSLFSheet sheet = this.getParentParagraph().getParentShape().getSheet();
        final XSLFColor col = new XSLFColor((XmlObject)fill, sheet.getTheme(), fill.getSchemeClr(), sheet);
        col.setColor(c);
    }
    
    public PaintStyle getFontColor() {
        final boolean hasPlaceholder = this.getParentParagraph().getParentShape().getPlaceholder() != null;
        final CharacterPropertyFetcher<PaintStyle> fetcher = new CharacterPropertyFetcher<PaintStyle>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props == null) {
                    return false;
                }
                final XSLFShape shape = XSLFTextRun.this._p.getParentShape();
                final CTShapeStyle style = shape.getSpStyle();
                CTSchemeColor phClr = null;
                if (style != null && style.getFontRef() != null) {
                    phClr = style.getFontRef().getSchemeClr();
                }
                final XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate((XmlObject)props);
                final XSLFSheet sheet = shape.getSheet();
                final PackagePart pp = sheet.getPackagePart();
                final XSLFTheme theme = sheet.getTheme();
                final PaintStyle ps = shape.selectPaint(fp, phClr, pp, theme, hasPlaceholder);
                if (ps != null) {
                    this.setValue(ps);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue();
    }
    
    public void setFontSize(final Double fontSize) {
        final CTTextCharacterProperties rPr = this.getRPr(true);
        if (fontSize == null) {
            if (rPr.isSetSz()) {
                rPr.unsetSz();
            }
        }
        else {
            if (fontSize < 1.0) {
                throw new IllegalArgumentException("Minimum font size is 1pt but was " + fontSize);
            }
            rPr.setSz((int)(100.0 * fontSize));
        }
    }
    
    public Double getFontSize() {
        double scale = 1.0;
        final XSLFTextShape ps = this.getParentParagraph().getParentShape();
        if (ps != null) {
            final CTTextBodyProperties tbp = ps.getTextBodyPr();
            if (tbp != null) {
                final CTTextNormalAutofit afit = tbp.getNormAutofit();
                if (afit != null && afit.isSetFontScale()) {
                    scale = afit.getFontScale() / 100000.0;
                }
            }
        }
        final CharacterPropertyFetcher<Double> fetcher = new CharacterPropertyFetcher<Double>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetSz()) {
                    this.setValue(props.getSz() * 0.01);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return (fetcher.getValue() == null) ? null : Double.valueOf(fetcher.getValue() * scale);
    }
    
    public double getCharacterSpacing() {
        final CharacterPropertyFetcher<Double> fetcher = new CharacterPropertyFetcher<Double>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetSpc()) {
                    this.setValue(props.getSpc() * 0.01);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return (fetcher.getValue() == null) ? 0.0 : fetcher.getValue();
    }
    
    public void setCharacterSpacing(final double spc) {
        final CTTextCharacterProperties rPr = this.getRPr(true);
        if (spc == 0.0) {
            if (rPr.isSetSpc()) {
                rPr.unsetSpc();
            }
        }
        else {
            rPr.setSpc((int)(100.0 * spc));
        }
    }
    
    public void setFontFamily(final String typeface) {
        final FontGroup fg = FontGroup.getFontGroupFirst(this.getRawText());
        new XSLFFontInfo(fg).setTypeface(typeface);
    }
    
    public void setFontFamily(final String typeface, final FontGroup fontGroup) {
        new XSLFFontInfo(fontGroup).setTypeface(typeface);
    }
    
    public void setFontInfo(final FontInfo fontInfo, final FontGroup fontGroup) {
        new XSLFFontInfo(fontGroup).copyFrom(fontInfo);
    }
    
    public String getFontFamily() {
        final FontGroup fg = FontGroup.getFontGroupFirst(this.getRawText());
        return new XSLFFontInfo(fg).getTypeface();
    }
    
    public String getFontFamily(final FontGroup fontGroup) {
        return new XSLFFontInfo(fontGroup).getTypeface();
    }
    
    public FontInfo getFontInfo(final FontGroup fontGroup) {
        final XSLFFontInfo fontInfo = new XSLFFontInfo(fontGroup);
        return (FontInfo)((fontInfo.getTypeface() != null) ? fontInfo : null);
    }
    
    public byte getPitchAndFamily() {
        final FontGroup fg = FontGroup.getFontGroupFirst(this.getRawText());
        final XSLFFontInfo fontInfo = new XSLFFontInfo(fg);
        FontPitch pitch = fontInfo.getPitch();
        if (pitch == null) {
            pitch = FontPitch.VARIABLE;
        }
        FontFamily family = fontInfo.getFamily();
        if (family == null) {
            family = FontFamily.FF_SWISS;
        }
        return FontPitch.getNativeId(pitch, family);
    }
    
    public void setStrikethrough(final boolean strike) {
        this.getRPr(true).setStrike(strike ? STTextStrikeType.SNG_STRIKE : STTextStrikeType.NO_STRIKE);
    }
    
    public boolean isStrikethrough() {
        final CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetStrike()) {
                    this.setValue(props.getStrike() != STTextStrikeType.NO_STRIKE);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public boolean isSuperscript() {
        final CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetBaseline()) {
                    this.setValue(props.getBaseline() > 0);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public void setBaselineOffset(final double baselineOffset) {
        this.getRPr(true).setBaseline((int)baselineOffset * 1000);
    }
    
    public void setSuperscript(final boolean flag) {
        this.setBaselineOffset(flag ? 30.0 : 0.0);
    }
    
    public void setSubscript(final boolean flag) {
        this.setBaselineOffset(flag ? -25.0 : 0.0);
    }
    
    public boolean isSubscript() {
        final CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetBaseline()) {
                    this.setValue(props.getBaseline() < 0);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public TextRun.TextCap getTextCap() {
        final CharacterPropertyFetcher<TextRun.TextCap> fetcher = new CharacterPropertyFetcher<TextRun.TextCap>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetCap()) {
                    final int idx = props.getCap().intValue() - 1;
                    this.setValue(TextRun.TextCap.values()[idx]);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return (fetcher.getValue() == null) ? TextRun.TextCap.NONE : fetcher.getValue();
    }
    
    public void setBold(final boolean bold) {
        this.getRPr(true).setB(bold);
    }
    
    public boolean isBold() {
        final CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetB()) {
                    this.setValue(props.getB());
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public void setItalic(final boolean italic) {
        this.getRPr(true).setI(italic);
    }
    
    public boolean isItalic() {
        final CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetI()) {
                    this.setValue(props.getI());
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    public void setUnderlined(final boolean underline) {
        this.getRPr(true).setU(underline ? STTextUnderlineType.SNG : STTextUnderlineType.NONE);
    }
    
    public boolean isUnderlined() {
        final CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) {
            @Override
            public boolean fetch(final CTTextCharacterProperties props) {
                if (props != null && props.isSetU()) {
                    this.setValue(props.getU() != STTextUnderlineType.NONE);
                    return true;
                }
                return false;
            }
        };
        this.fetchCharacterProperty(fetcher);
        return fetcher.getValue() != null && fetcher.getValue();
    }
    
    protected CTTextCharacterProperties getRPr(final boolean create) {
        if (this._r instanceof CTTextField) {
            final CTTextField tf = (CTTextField)this._r;
            if (tf.isSetRPr()) {
                return tf.getRPr();
            }
            if (create) {
                return tf.addNewRPr();
            }
        }
        else if (this._r instanceof CTTextLineBreak) {
            final CTTextLineBreak tlb = (CTTextLineBreak)this._r;
            if (tlb.isSetRPr()) {
                return tlb.getRPr();
            }
            if (create) {
                return tlb.addNewRPr();
            }
        }
        else {
            final CTRegularTextRun tr = (CTRegularTextRun)this._r;
            if (tr.isSetRPr()) {
                return tr.getRPr();
            }
            if (create) {
                return tr.addNewRPr();
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "[" + this.getClass() + "]" + this.getRawText();
    }
    
    public XSLFHyperlink createHyperlink() {
        final XSLFHyperlink hl = this.getHyperlink();
        if (hl != null) {
            return hl;
        }
        final CTTextCharacterProperties rPr = this.getRPr(true);
        return new XSLFHyperlink(rPr.addNewHlinkClick(), this._p.getParentShape().getSheet());
    }
    
    public XSLFHyperlink getHyperlink() {
        final CTTextCharacterProperties rPr = this.getRPr(false);
        if (rPr == null) {
            return null;
        }
        final CTHyperlink hl = rPr.getHlinkClick();
        if (hl == null) {
            return null;
        }
        return new XSLFHyperlink(hl, this._p.getParentShape().getSheet());
    }
    
    private void fetchCharacterProperty(final CharacterPropertyFetcher<?> visitor) {
        final XSLFTextShape shape = this._p.getParentShape();
        final CTTextCharacterProperties rPr = this.getRPr(false);
        if (rPr != null && visitor.fetch(rPr)) {
            return;
        }
        if (shape.fetchShapeProperty(visitor)) {
            return;
        }
        if (this._p.fetchThemeProperty(visitor)) {
            return;
        }
        this._p.fetchMasterProperty(visitor);
    }
    
    void copy(final XSLFTextRun r) {
        final String srcFontFamily = r.getFontFamily();
        if (srcFontFamily != null && !srcFontFamily.equals(this.getFontFamily())) {
            this.setFontFamily(srcFontFamily);
        }
        final PaintStyle srcFontColor = r.getFontColor();
        if (srcFontColor != null && !srcFontColor.equals(this.getFontColor())) {
            this.setFontColor(srcFontColor);
        }
        final Double srcFontSize = r.getFontSize();
        if (srcFontSize == null) {
            if (this.getFontSize() != null) {
                this.setFontSize(null);
            }
        }
        else if (!srcFontSize.equals(this.getFontSize())) {
            this.setFontSize(srcFontSize);
        }
        final boolean bold = r.isBold();
        if (bold != this.isBold()) {
            this.setBold(bold);
        }
        final boolean italic = r.isItalic();
        if (italic != this.isItalic()) {
            this.setItalic(italic);
        }
        final boolean underline = r.isUnderlined();
        if (underline != this.isUnderlined()) {
            this.setUnderlined(underline);
        }
        final boolean strike = r.isStrikethrough();
        if (strike != this.isStrikethrough()) {
            this.setStrikethrough(strike);
        }
        final XSLFHyperlink hyperSrc = r.getHyperlink();
        if (hyperSrc != null) {
            final XSLFHyperlink hyperDst = this.getHyperlink();
            hyperDst.copy(hyperSrc);
        }
    }
    
    public TextRun.FieldType getFieldType() {
        if (this._r instanceof CTTextField) {
            final CTTextField tf = (CTTextField)this._r;
            if ("slidenum".equals(tf.getType())) {
                return TextRun.FieldType.SLIDE_NUMBER;
            }
        }
        return null;
    }
    
    public XSLFTextParagraph getParagraph() {
        return this._p;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)XSLFTextRun.class);
    }
    
    private final class XSLFFontInfo implements FontInfo
    {
        private final FontGroup fontGroup;
        
        private XSLFFontInfo(final FontGroup fontGroup) {
            this.fontGroup = ((fontGroup != null) ? fontGroup : FontGroup.getFontGroupFirst(XSLFTextRun.this.getRawText()));
        }
        
        void copyFrom(final FontInfo fontInfo) {
            final CTTextFont tf = this.getXmlObject(true);
            if (tf == null) {
                return;
            }
            this.setTypeface(fontInfo.getTypeface());
            this.setCharset(fontInfo.getCharset());
            final FontPitch pitch = fontInfo.getPitch();
            final FontFamily family = fontInfo.getFamily();
            if (pitch == null && family == null) {
                if (tf.isSetPitchFamily()) {
                    tf.unsetPitchFamily();
                }
            }
            else {
                this.setPitch(pitch);
                this.setFamily(family);
            }
        }
        
        public String getTypeface() {
            final CTTextFont tf = this.getXmlObject(false);
            return (tf != null && tf.isSetTypeface()) ? tf.getTypeface() : null;
        }
        
        public void setTypeface(final String typeface) {
            if (typeface != null) {
                final CTTextFont tf = this.getXmlObject(true);
                if (tf != null) {
                    tf.setTypeface(typeface);
                }
                return;
            }
            final CTTextCharacterProperties props = XSLFTextRun.this.getRPr(false);
            if (props == null) {
                return;
            }
            final FontGroup fg = FontGroup.getFontGroupFirst(XSLFTextRun.this.getRawText());
            switch (fg) {
                default: {
                    if (props.isSetLatin()) {
                        props.unsetLatin();
                        break;
                    }
                    break;
                }
                case EAST_ASIAN: {
                    if (props.isSetEa()) {
                        props.unsetEa();
                        break;
                    }
                    break;
                }
                case COMPLEX_SCRIPT: {
                    if (props.isSetCs()) {
                        props.unsetCs();
                        break;
                    }
                    break;
                }
                case SYMBOL: {
                    if (props.isSetSym()) {
                        props.unsetSym();
                        break;
                    }
                    break;
                }
            }
        }
        
        public FontCharset getCharset() {
            final CTTextFont tf = this.getXmlObject(false);
            return (tf != null && tf.isSetCharset()) ? FontCharset.valueOf(tf.getCharset() & 0xFF) : null;
        }
        
        public void setCharset(final FontCharset charset) {
            final CTTextFont tf = this.getXmlObject(true);
            if (tf == null) {
                return;
            }
            if (charset != null) {
                tf.setCharset((byte)charset.getNativeId());
            }
            else if (tf.isSetCharset()) {
                tf.unsetCharset();
            }
        }
        
        public FontFamily getFamily() {
            final CTTextFont tf = this.getXmlObject(false);
            return (tf != null && tf.isSetPitchFamily()) ? FontFamily.valueOfPitchFamily(tf.getPitchFamily()) : null;
        }
        
        public void setFamily(final FontFamily family) {
            final CTTextFont tf = this.getXmlObject(true);
            if (tf == null || (family == null && !tf.isSetPitchFamily())) {
                return;
            }
            final FontPitch pitch = tf.isSetPitchFamily() ? FontPitch.valueOfPitchFamily(tf.getPitchFamily()) : FontPitch.VARIABLE;
            final byte pitchFamily = FontPitch.getNativeId(pitch, (family != null) ? family : FontFamily.FF_SWISS);
            tf.setPitchFamily(pitchFamily);
        }
        
        public FontPitch getPitch() {
            final CTTextFont tf = this.getXmlObject(false);
            return (tf != null && tf.isSetPitchFamily()) ? FontPitch.valueOfPitchFamily(tf.getPitchFamily()) : null;
        }
        
        public void setPitch(final FontPitch pitch) {
            final CTTextFont tf = this.getXmlObject(true);
            if (tf == null || (pitch == null && !tf.isSetPitchFamily())) {
                return;
            }
            final FontFamily family = tf.isSetPitchFamily() ? FontFamily.valueOfPitchFamily(tf.getPitchFamily()) : FontFamily.FF_SWISS;
            final byte pitchFamily = FontPitch.getNativeId((pitch != null) ? pitch : FontPitch.VARIABLE, family);
            tf.setPitchFamily(pitchFamily);
        }
        
        private CTTextFont getXmlObject(final boolean create) {
            if (create) {
                return this.getCTTextFont(XSLFTextRun.this.getRPr(true), true);
            }
            final CharacterPropertyFetcher<CTTextFont> visitor = new CharacterPropertyFetcher<CTTextFont>(XSLFTextRun.this._p.getIndentLevel()) {
                @Override
                public boolean fetch(final CTTextCharacterProperties props) {
                    final CTTextFont font = XSLFFontInfo.this.getCTTextFont(props, false);
                    if (font == null) {
                        return false;
                    }
                    this.setValue(font);
                    return true;
                }
            };
            XSLFTextRun.this.fetchCharacterProperty(visitor);
            return visitor.getValue();
        }
        
        private CTTextFont getCTTextFont(final CTTextCharacterProperties props, final boolean create) {
            if (props == null) {
                return null;
            }
            CTTextFont font = null;
            switch (this.fontGroup) {
                default: {
                    font = props.getLatin();
                    if (font == null && create) {
                        font = props.addNewLatin();
                        break;
                    }
                    break;
                }
                case EAST_ASIAN: {
                    font = props.getEa();
                    if (font == null && create) {
                        font = props.addNewEa();
                        break;
                    }
                    break;
                }
                case COMPLEX_SCRIPT: {
                    font = props.getCs();
                    if (font == null && create) {
                        font = props.addNewCs();
                        break;
                    }
                    break;
                }
                case SYMBOL: {
                    font = props.getSym();
                    if (font == null && create) {
                        font = props.addNewSym();
                        break;
                    }
                    break;
                }
            }
            if (font == null) {
                return null;
            }
            final String typeface = font.isSetTypeface() ? font.getTypeface() : "";
            if (typeface.startsWith("+mj-") || typeface.startsWith("+mn-")) {
                final XSLFTheme theme = XSLFTextRun.this._p.getParentShape().getSheet().getTheme();
                final CTFontScheme fontTheme = theme.getXmlObject().getThemeElements().getFontScheme();
                final CTFontCollection coll = typeface.startsWith("+mj-") ? fontTheme.getMajorFont() : fontTheme.getMinorFont();
                final String fgStr = typeface.substring(4);
                if ("ea".equals(fgStr)) {
                    font = coll.getEa();
                }
                else if ("cs".equals(fgStr)) {
                    font = coll.getCs();
                }
                else {
                    font = coll.getLatin();
                }
                if (font == null || !font.isSetTypeface() || "".equals(font.getTypeface())) {
                    font = coll.getLatin();
                }
            }
            return font;
        }
    }
}
