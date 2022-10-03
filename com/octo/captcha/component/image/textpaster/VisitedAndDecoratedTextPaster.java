package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.AttributedString;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.Color;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.textpaster.textvisitor.TextVisitor;

public class VisitedAndDecoratedTextPaster extends AbstractTextPaster
{
    protected final int kerning = 20;
    private TextVisitor[] textVisitors;
    private TextDecorator[] textDecorators;
    
    public VisitedAndDecoratedTextPaster(final Integer n, final Integer n2, final TextVisitor[] textVisitors, final TextDecorator[] textDecorators) {
        super(n, n2);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }
    
    public VisitedAndDecoratedTextPaster(final Integer n, final Integer n2, final Color color, final TextVisitor[] textVisitors, final TextDecorator[] textDecorators) {
        super(n, n2, color);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }
    
    public VisitedAndDecoratedTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final TextVisitor[] textVisitors, final TextDecorator[] textDecorators) {
        super(n, n2, colorGenerator);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }
    
    public VisitedAndDecoratedTextPaster(final Integer n, final Integer n2, final ColorGenerator colorGenerator, final Boolean b, final TextVisitor[] textVisitors, final TextDecorator[] textDecorators) {
        super(n, n2, colorGenerator, b);
        this.textVisitors = textVisitors;
        this.textDecorators = textDecorators;
    }
    
    public BufferedImage pasteText(final BufferedImage bufferedImage, final AttributedString attributedString) throws CaptchaException {
        final BufferedImage copyBackground = this.copyBackground(bufferedImage);
        final Graphics2D pasteBackgroundAndSetTextColor = this.pasteBackgroundAndSetTextColor(copyBackground, bufferedImage);
        pasteBackgroundAndSetTextColor.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        pasteBackgroundAndSetTextColor.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        pasteBackgroundAndSetTextColor.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final MutableAttributedString mutableAttributedString = new MutableAttributedString(pasteBackgroundAndSetTextColor, attributedString, 20);
        if (this.textVisitors != null) {
            for (int i = 0; i < this.textVisitors.length; ++i) {
                this.textVisitors[i].visit(mutableAttributedString);
            }
        }
        mutableAttributedString.moveToRandomSpot(bufferedImage);
        if (this.isManageColorPerGlyph()) {
            mutableAttributedString.drawString(pasteBackgroundAndSetTextColor, this.getColorGenerator());
        }
        else {
            mutableAttributedString.drawString(pasteBackgroundAndSetTextColor);
        }
        if (this.textDecorators != null) {
            for (int j = 0; j < this.textDecorators.length; ++j) {
                this.textDecorators[j].decorateAttributedString(pasteBackgroundAndSetTextColor, mutableAttributedString);
            }
        }
        pasteBackgroundAndSetTextColor.dispose();
        return copyBackground;
    }
}
