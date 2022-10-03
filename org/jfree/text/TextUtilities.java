package org.jfree.text;

import java.awt.Graphics;
import java.awt.font.TextLayout;
import java.awt.font.LineMetrics;
import java.awt.font.FontRenderContext;
import java.text.BreakIterator;
import java.awt.Paint;
import java.awt.Font;
import org.jfree.ui.TextAnchor;
import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.Graphics2D;
import org.jfree.base.BaseBoot;
import org.jfree.util.Log;
import org.jfree.util.LogContext;

public class TextUtilities
{
    protected static final LogContext logger;
    private static boolean useDrawRotatedStringWorkaround;
    private static boolean useFontMetricsGetStringBounds;
    static /* synthetic */ Class class$org$jfree$text$TextUtilities;
    
    static {
        logger = Log.createContext((TextUtilities.class$org$jfree$text$TextUtilities != null) ? TextUtilities.class$org$jfree$text$TextUtilities : (TextUtilities.class$org$jfree$text$TextUtilities = class$("org.jfree.text.TextUtilities")));
        boolean isJava14;
        try {
            Class.forName("java.nio.Buffer");
            isJava14 = true;
        }
        catch (final ClassNotFoundException ex) {
            isJava14 = false;
        }
        final String configRotatedStringWorkaround = BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.text.UseDrawRotatedStringWorkaround", "auto");
        if (configRotatedStringWorkaround.equals("auto")) {
            TextUtilities.useDrawRotatedStringWorkaround = !isJava14;
        }
        else {
            TextUtilities.useDrawRotatedStringWorkaround = configRotatedStringWorkaround.equals("true");
        }
        final String configFontMetricsStringBounds = BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.text.UseFontMetricsGetStringBounds", "auto");
        if (configFontMetricsStringBounds.equals("auto")) {
            TextUtilities.useFontMetricsGetStringBounds = isJava14;
        }
        else {
            TextUtilities.useFontMetricsGetStringBounds = configFontMetricsStringBounds.equals("true");
        }
    }
    
    private TextUtilities() {
    }
    
    public static Shape calculateRotatedStringBounds(final String text, final Graphics2D g2, final float textX, final float textY, final double angle, final float rotateX, final float rotateY) {
        if (text == null || text.equals("")) {
            return null;
        }
        final FontMetrics fm = g2.getFontMetrics();
        final Rectangle2D bounds = getTextBounds(text, g2, fm);
        final AffineTransform translate = AffineTransform.getTranslateInstance(textX, textY);
        final Shape translatedBounds = translate.createTransformedShape(bounds);
        final AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        final Shape result = rotate.createTransformedShape(translatedBounds);
        return result;
    }
    
    public static Shape calculateRotatedStringBounds(final String text, final Graphics2D g2, final float x, final float y, final TextAnchor textAnchor, final double angle, final TextAnchor rotationAnchor) {
        if (text == null || text.equals("")) {
            return null;
        }
        final float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        if (TextUtilities.logger.isDebugEnabled()) {
            TextUtilities.logger.debug("TextBoundsAnchorOffsets = " + textAdj[0] + ", " + textAdj[1]);
        }
        final float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        if (TextUtilities.logger.isDebugEnabled()) {
            TextUtilities.logger.debug("RotationAnchorOffsets = " + rotateAdj[0] + ", " + rotateAdj[1]);
        }
        final Shape result = calculateRotatedStringBounds(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
        return result;
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static TextBlock createTextBlock(final String text, final Font font, final Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        final TextBlock result = new TextBlock();
        String input = text;
        boolean moreInputToProcess = text.length() > 0;
        final int start = 0;
        while (moreInputToProcess) {
            final int index = input.indexOf("\n");
            if (index > 0) {
                final String line = input.substring(0, index);
                if (index < input.length() - 1) {
                    result.addLine(line, font, paint);
                    input = input.substring(index + 1);
                }
                else {
                    moreInputToProcess = false;
                }
            }
            else if (index == 0) {
                if (index < input.length() - 1) {
                    input = input.substring(index + 1);
                }
                else {
                    moreInputToProcess = false;
                }
            }
            else {
                result.addLine(input, font, paint);
                moreInputToProcess = false;
            }
        }
        return result;
    }
    
    public static TextBlock createTextBlock(final String text, final Font font, final Paint paint, final float maxWidth, final int maxLines, final TextMeasurer measurer) {
        final TextBlock result = new TextBlock();
        final BreakIterator iterator = BreakIterator.getLineInstance();
        iterator.setText(text);
        int current = 0;
        int lines = 0;
        final int length = text.length();
        while (current < length && lines < maxLines) {
            final int next = nextLineBreak(text, current, maxWidth, iterator, measurer);
            if (next == -1) {
                result.addLine(text.substring(current), font, paint);
                return result;
            }
            result.addLine(text.substring(current, next), font, paint);
            ++lines;
            for (current = next; current < text.length() && text.charAt(current) == '\n'; ++current) {}
        }
        if (current < length) {
            final TextLine lastLine = result.getLastLine();
            final TextFragment lastFragment = lastLine.getLastTextFragment();
            final String oldStr = lastFragment.getText();
            String newStr = "...";
            if (oldStr.length() > 3) {
                newStr = String.valueOf(oldStr.substring(0, oldStr.length() - 3)) + "...";
            }
            lastLine.removeFragment(lastFragment);
            final TextFragment newFragment = new TextFragment(newStr, lastFragment.getFont(), lastFragment.getPaint());
            lastLine.addFragment(newFragment);
        }
        return result;
    }
    
    public static TextBlock createTextBlock(final String text, final Font font, final Paint paint, final float maxWidth, final TextMeasurer measurer) {
        return createTextBlock(text, font, paint, maxWidth, Integer.MAX_VALUE, measurer);
    }
    
    private static float[] deriveRotationAnchorOffsets(final Graphics2D g2, final String text, final TextAnchor anchor) {
        final float[] result = new float[2];
        final FontRenderContext frc = g2.getFontRenderContext();
        final LineMetrics metrics = g2.getFont().getLineMetrics(text, frc);
        final FontMetrics fm = g2.getFontMetrics();
        final Rectangle2D bounds = getTextBounds(text, g2, fm);
        final float ascent = metrics.getAscent();
        final float halfAscent = ascent / 2.0f;
        final float descent = metrics.getDescent();
        final float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.HALF_ASCENT_LEFT) {
            xAdj = 0.0f;
        }
        else if (anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.CENTER || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.HALF_ASCENT_CENTER) {
            xAdj = (float)bounds.getWidth() / 2.0f;
        }
        else if (anchor == TextAnchor.TOP_RIGHT || anchor == TextAnchor.CENTER_RIGHT || anchor == TextAnchor.BOTTOM_RIGHT || anchor == TextAnchor.BASELINE_RIGHT || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            xAdj = (float)bounds.getWidth();
        }
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            yAdj = descent + leading - (float)bounds.getHeight();
        }
        else if (anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.CENTER || anchor == TextAnchor.CENTER_RIGHT) {
            yAdj = descent + leading - (float)(bounds.getHeight() / 2.0);
        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT || anchor == TextAnchor.HALF_ASCENT_CENTER || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            yAdj = -halfAscent;
        }
        else if (anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.BASELINE_RIGHT) {
            yAdj = 0.0f;
        }
        else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            yAdj = metrics.getDescent() + metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }
    
    private static float[] deriveTextBoundsAnchorOffsets(final Graphics2D g2, final String text, final TextAnchor anchor) {
        final float[] result = new float[2];
        final FontRenderContext frc = g2.getFontRenderContext();
        final Font f = g2.getFont();
        final FontMetrics fm = g2.getFontMetrics(f);
        final Rectangle2D bounds = getTextBounds(text, g2, fm);
        final LineMetrics metrics = f.getLineMetrics(text, frc);
        final float ascent = metrics.getAscent();
        final float halfAscent = ascent / 2.0f;
        final float descent = metrics.getDescent();
        final float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.CENTER || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.HALF_ASCENT_CENTER) {
            xAdj = (float)(-bounds.getWidth()) / 2.0f;
        }
        else if (anchor == TextAnchor.TOP_RIGHT || anchor == TextAnchor.CENTER_RIGHT || anchor == TextAnchor.BOTTOM_RIGHT || anchor == TextAnchor.BASELINE_RIGHT || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            xAdj = (float)(-bounds.getWidth());
        }
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            yAdj = -descent - leading + (float)bounds.getHeight();
        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT || anchor == TextAnchor.HALF_ASCENT_CENTER || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            yAdj = halfAscent;
        }
        else if (anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.CENTER || anchor == TextAnchor.CENTER_RIGHT) {
            yAdj = -descent - leading + (float)(bounds.getHeight() / 2.0);
        }
        else if (anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.BASELINE_RIGHT) {
            yAdj = 0.0f;
        }
        else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            yAdj = -metrics.getDescent() - metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }
    
    private static float[] deriveTextBoundsAnchorOffsets(final Graphics2D g2, final String text, final TextAnchor anchor, final Rectangle2D textBounds) {
        final float[] result = new float[3];
        final FontRenderContext frc = g2.getFontRenderContext();
        final Font f = g2.getFont();
        final FontMetrics fm = g2.getFontMetrics(f);
        final Rectangle2D bounds = getTextBounds(text, g2, fm);
        final LineMetrics metrics = f.getLineMetrics(text, frc);
        final float ascent = metrics.getAscent();
        result[2] = -ascent;
        final float halfAscent = ascent / 2.0f;
        final float descent = metrics.getDescent();
        final float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.CENTER || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.HALF_ASCENT_CENTER) {
            xAdj = (float)(-bounds.getWidth()) / 2.0f;
        }
        else if (anchor == TextAnchor.TOP_RIGHT || anchor == TextAnchor.CENTER_RIGHT || anchor == TextAnchor.BOTTOM_RIGHT || anchor == TextAnchor.BASELINE_RIGHT || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            xAdj = (float)(-bounds.getWidth());
        }
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            yAdj = -descent - leading + (float)bounds.getHeight();
        }
        else if (anchor == TextAnchor.HALF_ASCENT_LEFT || anchor == TextAnchor.HALF_ASCENT_CENTER || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            yAdj = halfAscent;
        }
        else if (anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.CENTER || anchor == TextAnchor.CENTER_RIGHT) {
            yAdj = -descent - leading + (float)(bounds.getHeight() / 2.0);
        }
        else if (anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.BASELINE_RIGHT) {
            yAdj = 0.0f;
        }
        else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            yAdj = -metrics.getDescent() - metrics.getLeading();
        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }
    
    public static Rectangle2D drawAlignedString(final String text, final Graphics2D g2, final float x, final float y, final TextAnchor anchor) {
        final Rectangle2D textBounds = new Rectangle2D.Double();
        final float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        textBounds.setRect(x + adjust[0], y + adjust[1] + adjust[2], textBounds.getWidth(), textBounds.getHeight());
        g2.drawString(text, x + adjust[0], y + adjust[1]);
        return textBounds;
    }
    
    public static void drawRotatedString(final String text, final Graphics2D g2, final double angle, final float x, final float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }
    
    public static void drawRotatedString(final String text, final Graphics2D g2, final float textX, final float textY, final double angle, final float rotateX, final float rotateY) {
        if (text == null || text.equals("")) {
            return;
        }
        final AffineTransform saved = g2.getTransform();
        final AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        g2.transform(rotate);
        if (TextUtilities.useDrawRotatedStringWorkaround) {
            final TextLayout tl = new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
            tl.draw(g2, textX, textY);
        }
        else {
            g2.drawString(text, textX, textY);
        }
        g2.setTransform(saved);
    }
    
    public static void drawRotatedString(final String text, final Graphics2D g2, final float x, final float y, final TextAnchor textAnchor, final double angle, final float rotationX, final float rotationY) {
        if (text == null || text.equals("")) {
            return;
        }
        final float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, rotationX, rotationY);
    }
    
    public static void drawRotatedString(final String text, final Graphics2D g2, final float x, final float y, final TextAnchor textAnchor, final double angle, final TextAnchor rotationAnchor) {
        if (text == null || text.equals("")) {
            return;
        }
        final float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        final float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
    }
    
    public static Rectangle2D getTextBounds(final String text, final Graphics2D g2, final FontMetrics fm) {
        Rectangle2D bounds;
        if (TextUtilities.useFontMetricsGetStringBounds) {
            bounds = fm.getStringBounds(text, g2);
            final LineMetrics lm = fm.getFont().getLineMetrics(text, g2.getFontRenderContext());
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), lm.getHeight());
        }
        else {
            final double width = fm.stringWidth(text);
            final double height = fm.getHeight();
            if (TextUtilities.logger.isDebugEnabled()) {
                TextUtilities.logger.debug("Height = " + height);
            }
            bounds = new Rectangle2D.Double(0.0, -fm.getAscent(), width, height);
        }
        return bounds;
    }
    
    public static boolean getUseFontMetricsGetStringBounds() {
        return TextUtilities.useFontMetricsGetStringBounds;
    }
    
    public static boolean isUseDrawRotatedStringWorkaround() {
        return TextUtilities.useDrawRotatedStringWorkaround;
    }
    
    private static int nextLineBreak(final String text, final int start, final float width, final BreakIterator iterator, final TextMeasurer measurer) {
        int current = start;
        float x = 0.0f;
        boolean firstWord = true;
        int newline = text.indexOf(10, start);
        if (newline < 0) {
            newline = Integer.MAX_VALUE;
        }
        int end;
        while ((end = iterator.next()) != -1) {
            if (end > newline) {
                return newline;
            }
            x += measurer.getStringWidth(text, current, end);
            if (x > width) {
                if (firstWord) {
                    while (measurer.getStringWidth(text, start, end) > width) {
                        if (--end <= start) {
                            return end;
                        }
                    }
                    return end;
                }
                end = iterator.previous();
                return end;
            }
            else {
                firstWord = false;
                current = end;
            }
        }
        return -1;
    }
    
    public static void setUseDrawRotatedStringWorkaround(final boolean use) {
        TextUtilities.useDrawRotatedStringWorkaround = use;
    }
    
    public static void setUseFontMetricsGetStringBounds(final boolean use) {
        TextUtilities.useFontMetricsGetStringBounds = use;
    }
}
