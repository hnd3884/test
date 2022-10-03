package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.awt.Font;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.textpaster.glyphsdecorator.GlyphsDecorator;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;

public class GlyphsPaster extends AbstractTextPaster
{
    private GlyphsVisitors[] glyphVisitors;
    private GlyphsDecorator[] glyphsDecorators;
    
    public GlyphsPaster(final Integer n, final Integer n2) {
        super(n, n2);
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final Color color) {
        super(n, n2, color);
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator) {
        super(n, n2, colorGenerator);
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b) {
        super(n, n2, colorGenerator, b);
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final GlyphsVisitors[] glyphVisitors) {
        super(n, n2);
        this.glyphVisitors = glyphVisitors;
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final Color color, final GlyphsVisitors[] glyphVisitors) {
        super(n, n2, color);
        this.glyphVisitors = glyphVisitors;
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final GlyphsVisitors[] glyphVisitors) {
        super(n, n2, colorGenerator);
        this.glyphVisitors = glyphVisitors;
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final GlyphsVisitors[] glyphVisitors, final GlyphsDecorator[] glyphsDecorators) {
        super(n, n2, colorGenerator);
        this.glyphVisitors = glyphVisitors;
        this.glyphsDecorators = glyphsDecorators;
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b, final GlyphsVisitors[] glyphVisitors) {
        super(n, n2, colorGenerator, b);
        this.glyphVisitors = glyphVisitors;
    }
    
    public GlyphsPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b, final GlyphsVisitors[] glyphVisitors, final GlyphsDecorator[] glyphsDecorators) {
        super(n, n2, colorGenerator, b);
        this.glyphVisitors = glyphVisitors;
        this.glyphsDecorators = glyphsDecorators;
    }
    
    public BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
        final BufferedImage copyBackground = this.copyBackground(bufferedImage);
        final Graphics2D pasteBackgroundAndSetTextColor = this.pasteBackgroundAndSetTextColor(copyBackground, bufferedImage);
        this.customizeGraphicsRenderingHints(pasteBackgroundAndSetTextColor);
        final AttributedCharacterIterator iterator = attributedString.getIterator();
        final Glyphs glyphs = new Glyphs();
        for (int i = 0; i < iterator.getEndIndex(); ++i) {
            final Font font = (Font)iterator.getAttribute(TextAttribute.FONT);
            pasteBackgroundAndSetTextColor.setFont(font);
            glyphs.addGlyphVector(font.createGlyphVector(pasteBackgroundAndSetTextColor.getFontRenderContext(), new char[] { iterator.current() }));
            iterator.next();
        }
        final Rectangle2D.Float float1 = new Rectangle2D.Float(0.0f, 0.0f, (float)bufferedImage.getWidth(), (float)bufferedImage.getHeight());
        if (this.glyphVisitors != null) {
            for (int j = 0; j < this.glyphVisitors.length; ++j) {
                this.glyphVisitors[j].visit(glyphs, float1);
            }
        }
        for (int k = 0; k < glyphs.size(); ++k) {
            pasteBackgroundAndSetTextColor.drawGlyphVector(glyphs.get(k), 0.0f, 0.0f);
            if (this.isManageColorPerGlyph()) {
                pasteBackgroundAndSetTextColor.setColor(this.getColorGenerator().getNextColor());
            }
        }
        if (this.glyphsDecorators != null) {
            for (int l = 0; l < this.glyphsDecorators.length; ++l) {
                this.glyphsDecorators[l].decorate(pasteBackgroundAndSetTextColor, glyphs, bufferedImage);
            }
        }
        pasteBackgroundAndSetTextColor.dispose();
        return copyBackground;
    }
}
