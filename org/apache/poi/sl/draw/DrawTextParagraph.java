package org.apache.poi.sl.draw;

import java.io.InvalidObjectException;
import org.apache.poi.util.POILogFactory;
import java.awt.Font;
import org.apache.poi.sl.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.ShapeContainer;
import java.awt.Dimension;
import org.apache.poi.sl.usermodel.TextShape;
import java.util.Arrays;
import java.awt.geom.AffineTransform;
import java.awt.font.FontRenderContext;
import org.apache.poi.util.Internal;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;
import java.awt.RenderingHints;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import java.awt.font.TextAttribute;
import java.awt.Paint;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.awt.font.LineBreakMeasurer;
import org.apache.poi.sl.usermodel.Insets2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.util.Units;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.util.POILogger;

public class DrawTextParagraph implements Drawable
{
    private static final POILogger LOG;
    public static final XlinkAttribute HYPERLINK_HREF;
    public static final XlinkAttribute HYPERLINK_LABEL;
    protected TextParagraph<?, ?, ?> paragraph;
    double x;
    double y;
    protected List<DrawTextFragment> lines;
    protected String rawText;
    protected DrawTextFragment bullet;
    protected int autoNbrIdx;
    protected double maxLineHeight;
    
    public DrawTextParagraph(final TextParagraph<?, ?, ?> paragraph) {
        this.lines = new ArrayList<DrawTextFragment>();
        this.paragraph = paragraph;
    }
    
    public void setPosition(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setAutoNumberingIdx(final int index) {
        this.autoNbrIdx = index;
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        if (this.lines.isEmpty()) {
            return;
        }
        double penY = this.y;
        boolean firstLine = true;
        final int indentLevel = this.paragraph.getIndentLevel();
        Double leftMargin = this.paragraph.getLeftMargin();
        if (leftMargin == null) {
            leftMargin = Units.toPoints(347663L * indentLevel);
        }
        Double indent = this.paragraph.getIndent();
        if (indent == null) {
            indent = Units.toPoints(347663L * indentLevel);
        }
        if (this.isHSLF()) {
            indent -= leftMargin;
        }
        Double spacing = this.paragraph.getLineSpacing();
        if (spacing == null) {
            spacing = 100.0;
        }
        for (final DrawTextFragment line : this.lines) {
            double penX;
            if (firstLine) {
                if (!this.isEmptyParagraph()) {
                    this.bullet = this.getBullet(graphics, line.getAttributedString().getIterator());
                }
                if (this.bullet != null) {
                    this.bullet.setPosition(this.x + leftMargin + indent, penY);
                    this.bullet.draw(graphics);
                    final double bulletWidth = this.bullet.getLayout().getAdvance() + 1.0f;
                    penX = this.x + Math.max(leftMargin, leftMargin + indent + bulletWidth);
                }
                else {
                    penX = this.x + leftMargin;
                }
            }
            else {
                penX = this.x + leftMargin;
            }
            final Rectangle2D anchor = DrawShape.getAnchor(graphics, this.paragraph.getParentShape());
            final Insets2D insets = this.paragraph.getParentShape().getInsets();
            final double leftInset = insets.left;
            final double rightInset = insets.right;
            TextParagraph.TextAlign ta = this.paragraph.getTextAlign();
            if (ta == null) {
                ta = TextParagraph.TextAlign.LEFT;
            }
            switch (ta) {
                case CENTER: {
                    penX += (anchor.getWidth() - line.getWidth() - leftInset - rightInset - leftMargin) / 2.0;
                    break;
                }
                case RIGHT: {
                    penX += anchor.getWidth() - line.getWidth() - leftInset - rightInset;
                    break;
                }
            }
            line.setPosition(penX, penY);
            line.draw(graphics);
            if (spacing > 0.0) {
                penY += spacing * 0.01 * line.getHeight();
            }
            else {
                penY += -spacing;
            }
            firstLine = false;
        }
        this.y = penY - this.y;
    }
    
    public float getFirstLineLeading() {
        return this.lines.isEmpty() ? 0.0f : this.lines.get(0).getLeading();
    }
    
    public float getFirstLineHeight() {
        return this.lines.isEmpty() ? 0.0f : this.lines.get(0).getHeight();
    }
    
    public float getLastLineHeight() {
        return this.lines.isEmpty() ? 0.0f : this.lines.get(this.lines.size() - 1).getHeight();
    }
    
    public boolean isEmptyParagraph() {
        return this.lines.isEmpty() || this.rawText.trim().isEmpty();
    }
    
    @Override
    public void applyTransform(final Graphics2D graphics) {
    }
    
    @Override
    public void drawContent(final Graphics2D graphics) {
    }
    
    protected void breakText(final Graphics2D graphics) {
        this.lines.clear();
        final DrawFactory fact = DrawFactory.getInstance(graphics);
        final StringBuilder text = new StringBuilder();
        final AttributedString at = this.getAttributedString(graphics, text);
        final boolean emptyParagraph = text.toString().trim().isEmpty();
        final AttributedCharacterIterator it = at.getIterator();
        final LineBreakMeasurer measurer = new LineBreakMeasurer(it, graphics.getFontRenderContext());
        int endIndex;
        do {
            final int startIndex = measurer.getPosition();
            double wrappingWidth = this.getWrappingWidth(this.lines.isEmpty(), graphics) + 1.0;
            if (wrappingWidth < 0.0) {
                wrappingWidth = 1.0;
            }
            int nextBreak = text.indexOf("\n", startIndex + 1);
            if (nextBreak == -1) {
                nextBreak = it.getEndIndex();
            }
            TextLayout layout = measurer.nextLayout((float)wrappingWidth, nextBreak, true);
            if (layout == null) {
                layout = measurer.nextLayout((float)wrappingWidth, nextBreak, false);
            }
            if (layout == null) {
                break;
            }
            endIndex = measurer.getPosition();
            if (endIndex < it.getEndIndex() && text.charAt(endIndex) == '\n') {
                measurer.setPosition(endIndex + 1);
            }
            final TextParagraph.TextAlign hAlign = this.paragraph.getTextAlign();
            if (hAlign == TextParagraph.TextAlign.JUSTIFY || hAlign == TextParagraph.TextAlign.JUSTIFY_LOW) {
                layout = layout.getJustifiedLayout((float)wrappingWidth);
            }
            final AttributedString str = emptyParagraph ? null : new AttributedString(it, startIndex, endIndex);
            final DrawTextFragment line = fact.getTextFragment(layout, str);
            this.lines.add(line);
            this.maxLineHeight = Math.max(this.maxLineHeight, line.getHeight());
        } while (endIndex != it.getEndIndex());
        this.rawText = text.toString();
    }
    
    protected DrawTextFragment getBullet(final Graphics2D graphics, final AttributedCharacterIterator firstLineAttr) {
        final TextParagraph.BulletStyle bulletStyle = this.paragraph.getBulletStyle();
        if (bulletStyle == null) {
            return null;
        }
        final AutoNumberingScheme ans = bulletStyle.getAutoNumberingScheme();
        String buCharacter;
        if (ans != null) {
            buCharacter = ans.format(this.autoNbrIdx);
        }
        else {
            buCharacter = bulletStyle.getBulletCharacter();
        }
        if (buCharacter == null) {
            return null;
        }
        final PlaceableShape<?, ?> ps = this.getParagraphShape();
        final PaintStyle fgPaintStyle = bulletStyle.getBulletFontColor();
        Paint fgPaint;
        if (fgPaintStyle == null) {
            fgPaint = (Paint)firstLineAttr.getAttribute(TextAttribute.FOREGROUND);
        }
        else {
            fgPaint = new DrawPaint(ps).getPaint(graphics, fgPaintStyle);
        }
        float fontSize = (float)firstLineAttr.getAttribute(TextAttribute.SIZE);
        Double buSz = bulletStyle.getBulletFontSize();
        if (buSz == null) {
            buSz = 100.0;
        }
        if (buSz > 0.0) {
            fontSize *= (float)(buSz * 0.01);
        }
        else {
            fontSize = (float)(Object)(-buSz);
        }
        String buFontStr = bulletStyle.getBulletFont();
        if (buFontStr == null) {
            buFontStr = this.paragraph.getDefaultFontFamily();
        }
        assert buFontStr != null;
        FontInfo buFont = new DrawFontInfo(buFontStr);
        final DrawFontManager dfm = DrawFactory.getInstance(graphics).getFontManager(graphics);
        buFont = dfm.getMappedFont(graphics, buFont);
        final AttributedString str = new AttributedString(dfm.mapFontCharset(graphics, buFont, buCharacter));
        str.addAttribute(TextAttribute.FOREGROUND, fgPaint);
        str.addAttribute(TextAttribute.FAMILY, buFont.getTypeface());
        str.addAttribute(TextAttribute.SIZE, fontSize);
        final TextLayout layout = new TextLayout(str.getIterator(), graphics.getFontRenderContext());
        final DrawFactory fact = DrawFactory.getInstance(graphics);
        return fact.getTextFragment(layout, str);
    }
    
    protected String getRenderableText(final Graphics2D graphics, final TextRun tr) {
        if (tr.getFieldType() == TextRun.FieldType.SLIDE_NUMBER) {
            final Slide<?, ?> slide = (Slide<?, ?>)graphics.getRenderingHint(Drawable.CURRENT_SLIDE);
            return (slide == null) ? "" : Integer.toString(slide.getSlideNumber());
        }
        return this.getRenderableText(tr);
    }
    
    @Internal
    public String getRenderableText(final TextRun tr) {
        String txtSpace = tr.getRawText();
        if (txtSpace == null) {
            return null;
        }
        if (txtSpace.contains("\t")) {
            txtSpace = txtSpace.replace("\t", this.tab2space(tr));
        }
        txtSpace = txtSpace.replace('\u000b', '\n');
        final Locale loc = LocaleUtil.getUserLocale();
        switch (tr.getTextCap()) {
            case ALL: {
                return txtSpace.toUpperCase(loc);
            }
            case SMALL: {
                return txtSpace.toLowerCase(loc);
            }
            default: {
                return txtSpace;
            }
        }
    }
    
    private String tab2space(final TextRun tr) {
        final AttributedString string = new AttributedString(" ");
        String fontFamily = tr.getFontFamily();
        if (fontFamily == null) {
            fontFamily = "Lucida Sans";
        }
        string.addAttribute(TextAttribute.FAMILY, fontFamily);
        Double fs = tr.getFontSize();
        if (fs == null) {
            fs = 12.0;
        }
        string.addAttribute(TextAttribute.SIZE, fs.floatValue());
        final TextLayout l = new TextLayout(string.getIterator(), new FontRenderContext(null, true, true));
        final double wspace = l.getAdvance();
        Double tabSz = this.paragraph.getDefaultTabSize();
        int numSpaces;
        if (wspace <= 0.0) {
            numSpaces = 4;
        }
        else {
            if (tabSz == null) {
                tabSz = wspace * 4.0;
            }
            numSpaces = (int)Math.min(Math.ceil(tabSz / wspace), 20.0);
        }
        final char[] buf = new char[numSpaces];
        Arrays.fill(buf, ' ');
        return new String(buf);
    }
    
    protected double getWrappingWidth(final boolean firstLine, final Graphics2D graphics) {
        final TextShape<?, ?> ts = this.paragraph.getParentShape();
        final Insets2D insets = ts.getInsets();
        final double leftInset = insets.left;
        final double rightInset = insets.right;
        int indentLevel = this.paragraph.getIndentLevel();
        if (indentLevel == -1) {
            indentLevel = 0;
        }
        Double leftMargin = this.paragraph.getLeftMargin();
        if (leftMargin == null) {
            leftMargin = Units.toPoints(347663L * (indentLevel + 1));
        }
        Double indent = this.paragraph.getIndent();
        if (indent == null) {
            indent = Units.toPoints(347663L * indentLevel);
        }
        Double rightMargin = this.paragraph.getRightMargin();
        if (rightMargin == null) {
            rightMargin = 0.0;
        }
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, ts);
        final TextShape.TextDirection textDir = ts.getTextDirection();
        double width = 0.0;
        if (!ts.getWordWrap()) {
            final Dimension pageDim = ts.getSheet().getSlideShow().getPageSize();
            switch (textDir) {
                default: {
                    width = pageDim.getWidth() - anchor.getX();
                    break;
                }
                case VERTICAL: {
                    width = pageDim.getHeight() - anchor.getX();
                    break;
                }
                case VERTICAL_270: {
                    width = anchor.getX();
                    break;
                }
            }
        }
        else {
            switch (textDir) {
                default: {
                    width = anchor.getWidth() - leftInset - rightInset - leftMargin - rightMargin;
                    break;
                }
                case VERTICAL:
                case VERTICAL_270: {
                    width = anchor.getHeight() - leftInset - rightInset - leftMargin - rightMargin;
                    break;
                }
            }
            if (firstLine && !this.isHSLF()) {
                if (this.bullet != null) {
                    if (indent > 0.0) {
                        width -= indent;
                    }
                }
                else if (indent > 0.0) {
                    width -= indent;
                }
                else if (indent < 0.0) {
                    width += leftMargin;
                }
            }
        }
        return width;
    }
    
    private PlaceableShape<?, ?> getParagraphShape() {
        return new PlaceableShape() {
            @Override
            public ShapeContainer<?, ?> getParent() {
                return null;
            }
            
            @Override
            public Rectangle2D getAnchor() {
                return DrawTextParagraph.this.paragraph.getParentShape().getAnchor();
            }
            
            @Override
            public void setAnchor(final Rectangle2D anchor) {
            }
            
            @Override
            public double getRotation() {
                return 0.0;
            }
            
            @Override
            public void setRotation(final double theta) {
            }
            
            @Override
            public void setFlipHorizontal(final boolean flip) {
            }
            
            @Override
            public void setFlipVertical(final boolean flip) {
            }
            
            @Override
            public boolean getFlipHorizontal() {
                return false;
            }
            
            @Override
            public boolean getFlipVertical() {
                return false;
            }
            
            @Override
            public Sheet<?, ?> getSheet() {
                return DrawTextParagraph.this.paragraph.getParentShape().getSheet();
            }
        };
    }
    
    protected AttributedString getAttributedString(final Graphics2D graphics, StringBuilder text) {
        final List<AttributedStringData> attList = new ArrayList<AttributedStringData>();
        if (text == null) {
            text = new StringBuilder();
        }
        final PlaceableShape<?, ?> ps = this.getParagraphShape();
        final DrawFontManager dfm = DrawFactory.getInstance(graphics).getFontManager(graphics);
        assert dfm != null;
        for (final TextRun run : this.paragraph) {
            String runText = this.getRenderableText(graphics, run);
            if (runText.isEmpty()) {
                continue;
            }
            runText = dfm.mapFontCharset(graphics, run.getFontInfo(null), runText);
            final int beginIndex = text.length();
            text.append(runText);
            final int endIndex = text.length();
            final PaintStyle fgPaintStyle = run.getFontColor();
            final Paint fgPaint = new DrawPaint(ps).getPaint(graphics, fgPaintStyle);
            attList.add(new AttributedStringData(TextAttribute.FOREGROUND, fgPaint, beginIndex, endIndex));
            Double fontSz = run.getFontSize();
            if (fontSz == null) {
                fontSz = this.paragraph.getDefaultFontSize();
            }
            attList.add(new AttributedStringData(TextAttribute.SIZE, fontSz.floatValue(), beginIndex, endIndex));
            if (run.isBold()) {
                attList.add(new AttributedStringData(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, beginIndex, endIndex));
            }
            if (run.isItalic()) {
                attList.add(new AttributedStringData(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, beginIndex, endIndex));
            }
            if (run.isUnderlined()) {
                attList.add(new AttributedStringData(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, beginIndex, endIndex));
                attList.add(new AttributedStringData(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL, beginIndex, endIndex));
            }
            if (run.isStrikethrough()) {
                attList.add(new AttributedStringData(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, beginIndex, endIndex));
            }
            if (run.isSubscript()) {
                attList.add(new AttributedStringData(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, beginIndex, endIndex));
            }
            if (run.isSuperscript()) {
                attList.add(new AttributedStringData(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, beginIndex, endIndex));
            }
            final Hyperlink<?, ?> hl = run.getHyperlink();
            if (hl != null) {
                attList.add(new AttributedStringData(DrawTextParagraph.HYPERLINK_HREF, hl.getAddress(), beginIndex, endIndex));
                attList.add(new AttributedStringData(DrawTextParagraph.HYPERLINK_LABEL, hl.getLabel(), beginIndex, endIndex));
            }
            this.processGlyphs(graphics, dfm, attList, beginIndex, run, runText);
        }
        if (text.length() == 0) {
            final Double fontSz2 = this.paragraph.getDefaultFontSize();
            text.append(" ");
            attList.add(new AttributedStringData(TextAttribute.SIZE, fontSz2.floatValue(), 0, 1));
        }
        final AttributedString string = new AttributedString(text.toString());
        for (final AttributedStringData asd : attList) {
            string.addAttribute(asd.attribute, asd.value, asd.beginIndex, asd.endIndex);
        }
        return string;
    }
    
    private void processGlyphs(final Graphics2D graphics, final DrawFontManager dfm, final List<AttributedStringData> attList, final int beginIndex, final TextRun run, final String runText) {
        final List<FontGroup.FontGroupRange> ttrList = FontGroup.getFontGroupRanges(runText);
        int rangeBegin = 0;
        for (final FontGroup.FontGroupRange ttr : ttrList) {
            FontInfo fiRun = run.getFontInfo(ttr.getFontGroup());
            if (fiRun == null) {
                fiRun = run.getFontInfo(FontGroup.LATIN);
            }
            FontInfo fiMapped = dfm.getMappedFont(graphics, fiRun);
            final FontInfo fiFallback = dfm.getFallbackFont(graphics, fiRun);
            assert fiFallback != null;
            if (fiMapped == null) {
                fiMapped = dfm.getMappedFont(graphics, new DrawFontInfo(this.paragraph.getDefaultFontFamily()));
            }
            if (fiMapped == null) {
                fiMapped = fiFallback;
            }
            final Font fontMapped = dfm.createAWTFont(graphics, fiMapped, 10.0, run.isBold(), run.isItalic());
            final Font fontFallback = dfm.createAWTFont(graphics, fiFallback, 10.0, run.isBold(), run.isItalic());
            final int rangeLen = ttr.getLength();
            int partEnd = rangeBegin;
            while (partEnd < rangeBegin + rangeLen) {
                int partBegin = partEnd;
                partEnd = nextPart(fontMapped, runText, partBegin, rangeBegin + rangeLen, true);
                if (partBegin < partEnd) {
                    attList.add(new AttributedStringData(TextAttribute.FAMILY, fontMapped.getFontName(Locale.ROOT), beginIndex + partBegin, beginIndex + partEnd));
                    if (DrawTextParagraph.LOG.check(1)) {
                        DrawTextParagraph.LOG.log(1, "mapped: ", fontMapped.getFontName(Locale.ROOT), " ", beginIndex + partBegin, " ", beginIndex + partEnd, " - ", runText.substring(beginIndex + partBegin, beginIndex + partEnd));
                    }
                }
                partBegin = partEnd;
                partEnd = nextPart(fontMapped, runText, partBegin, rangeBegin + rangeLen, false);
                if (partBegin < partEnd) {
                    attList.add(new AttributedStringData(TextAttribute.FAMILY, fontFallback.getFontName(Locale.ROOT), beginIndex + partBegin, beginIndex + partEnd));
                    if (!DrawTextParagraph.LOG.check(1)) {
                        continue;
                    }
                    DrawTextParagraph.LOG.log(1, "fallback: ", fontFallback.getFontName(Locale.ROOT), " ", beginIndex + partBegin, " ", beginIndex + partEnd, " - ", runText.substring(beginIndex + partBegin, beginIndex + partEnd));
                }
            }
            rangeBegin += rangeLen;
        }
    }
    
    private static int nextPart(final Font fontMapped, final String runText, final int beginPart, final int endPart, final boolean isDisplayed) {
        int rIdx;
        int codepoint;
        for (rIdx = beginPart; rIdx < endPart; rIdx += Character.charCount(codepoint)) {
            codepoint = runText.codePointAt(rIdx);
            if (fontMapped.canDisplay(codepoint) != isDisplayed) {
                break;
            }
        }
        return rIdx;
    }
    
    protected boolean isHSLF() {
        return DrawShape.isHSLF(this.paragraph.getParentShape());
    }
    
    static {
        LOG = POILogFactory.getLogger(DrawTextParagraph.class);
        HYPERLINK_HREF = new XlinkAttribute("href");
        HYPERLINK_LABEL = new XlinkAttribute("label");
    }
    
    private static class XlinkAttribute extends AttributedCharacterIterator.Attribute
    {
        XlinkAttribute(final String name) {
            super(name);
        }
        
        @Override
        protected Object readResolve() throws InvalidObjectException {
            if (DrawTextParagraph.HYPERLINK_HREF.getName().equals(this.getName())) {
                return DrawTextParagraph.HYPERLINK_HREF;
            }
            if (DrawTextParagraph.HYPERLINK_LABEL.getName().equals(this.getName())) {
                return DrawTextParagraph.HYPERLINK_LABEL;
            }
            throw new InvalidObjectException("unknown attribute name");
        }
    }
    
    private static class AttributedStringData
    {
        AttributedCharacterIterator.Attribute attribute;
        Object value;
        int beginIndex;
        int endIndex;
        
        AttributedStringData(final AttributedCharacterIterator.Attribute attribute, final Object value, final int beginIndex, final int endIndex) {
            this.attribute = attribute;
            this.value = value;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }
    }
}
