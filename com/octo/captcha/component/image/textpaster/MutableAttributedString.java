package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.font.FontRenderContext;
import java.text.CharacterIterator;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.awt.Font;
import java.security.SecureRandom;
import java.awt.Graphics2D;
import java.util.Random;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public class MutableAttributedString
{
    AttributedString originalAttributedString;
    AttributedString[] aStrings;
    Rectangle2D[] bounds;
    LineMetrics[] metrics;
    GlyphVector[] glyphVectors;
    private Random myRandom;
    private int kerning;
    
    protected MutableAttributedString(final Graphics2D graphics2D, final AttributedString originalAttributedString, final int kerning) {
        this.myRandom = new SecureRandom();
        this.kerning = kerning;
        this.originalAttributedString = originalAttributedString;
        final AttributedCharacterIterator iterator = originalAttributedString.getIterator();
        final int endIndex = iterator.getEndIndex();
        this.aStrings = new AttributedString[endIndex];
        this.bounds = new Rectangle2D[endIndex];
        this.metrics = new LineMetrics[endIndex];
        for (int i = iterator.getBeginIndex(); i < iterator.getEndIndex(); ++i) {
            iterator.setIndex(i);
            this.aStrings[i] = new AttributedString(iterator, i, i + 1);
            final Font font = (Font)iterator.getAttribute(TextAttribute.FONT);
            if (font != null) {
                graphics2D.setFont(font);
            }
            final FontRenderContext fontRenderContext = graphics2D.getFontRenderContext();
            this.bounds[i] = graphics2D.getFont().getStringBounds(iterator, i, i + 1, fontRenderContext);
            this.metrics[i] = graphics2D.getFont().getLineMetrics(new Character(iterator.current()).toString(), fontRenderContext);
        }
    }
    
    void drawString(final Graphics2D graphics2D) {
        for (int i = 0; i < this.length(); ++i) {
            graphics2D.drawString(this.getIterator(i), (float)this.getX(i), (float)this.getY(i));
        }
    }
    
    void drawString(final Graphics2D graphics2D, final ColorGenerator colorGenerator) {
        for (int i = 0; i < this.length(); ++i) {
            graphics2D.setColor(colorGenerator.getNextColor());
            graphics2D.drawString(this.getIterator(i), (float)this.getX(i), (float)this.getY(i));
        }
    }
    
    Point2D moveToRandomSpot(final BufferedImage bufferedImage) {
        return this.moveToRandomSpot(bufferedImage, null);
    }
    
    Point2D moveToRandomSpot(final BufferedImage bufferedImage, final Point2D point2D) {
        final int n = (int)this.getMaxHeight();
        final double n2 = bufferedImage.getWidth() - this.getTotalWidth() - 10.0;
        final double n3 = bufferedImage.getHeight() - n - 5;
        int n4;
        if (point2D == null) {
            n4 = (int)this.getMaxAscent() + this.myRandom.nextInt(Math.max(1, (int)n3));
        }
        else {
            n4 = (int)(point2D.getY() + this.myRandom.nextInt(10));
        }
        if (n2 < 0.0 || n3 < 0.0) {
            String s = "too tall:";
            if (n2 < 0.0 && n3 > 0.0) {
                s = "too long:";
                this.useMinimumSpacing(this.kerning / 2);
                double reduceHorizontalSpacing = bufferedImage.getWidth() - this.getTotalWidth();
                if (reduceHorizontalSpacing < 0.0) {
                    this.useMinimumSpacing(0.0);
                    reduceHorizontalSpacing = bufferedImage.getWidth() - this.getTotalWidth();
                    if (reduceHorizontalSpacing < 0.0) {
                        reduceHorizontalSpacing = this.reduceHorizontalSpacing(bufferedImage.getWidth(), 0.05);
                    }
                }
                if (reduceHorizontalSpacing > 0.0) {
                    this.moveTo(0.0, n4);
                    return new Point2D.Float(0.0f, (float)n4);
                }
            }
            throw new CaptchaException("word is " + s + " try to use less letters, smaller font" + " or bigger background: " + " text bounds = " + this + " with fonts " + this.getFontListing() + " versus image width = " + bufferedImage.getWidth() + ", height = " + bufferedImage.getHeight());
        }
        int nextInt;
        if (point2D == null) {
            nextInt = this.myRandom.nextInt(Math.max(1, (int)n2));
        }
        else {
            nextInt = (int)(point2D.getX() + this.myRandom.nextInt(10));
        }
        this.moveTo(nextInt, n4);
        return new Point2D.Float((float)nextInt, (float)n4);
    }
    
    String getFontListing() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (int i = 0; i < this.length(); ++i) {
            final Font font = (Font)this.aStrings[i].getIterator().getAttribute(TextAttribute.FONT);
            if (font != null) {
                sb.append(font.toString()).append("\n\t");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    void useMonospacing(final double n) {
        final double maxWidth = this.getMaxWidth();
        for (int i = 1; i < this.bounds.length; ++i) {
            this.getBounds(i).setRect(this.getX(i - 1) + maxWidth + n, this.getY(i), this.getWidth(i), this.getHeight(i));
        }
    }
    
    void useMinimumSpacing(final double n) {
        for (int i = 1; i < this.length(); ++i) {
            this.bounds[i].setRect(this.bounds[i - 1].getX() + this.bounds[i - 1].getWidth() + n, this.bounds[i].getY(), this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }
    
    double reduceHorizontalSpacing(final int n, final double n2) {
        double n3 = n - this.getTotalWidth();
        double n5;
        for (double n4 = n5 = n2 / 25.0; n5 < n2 && n3 < 0.0; n3 = n - this.getTotalWidth(), n5 += n4) {
            for (int i = 1; i < this.length(); ++i) {
                this.bounds[i].setRect((1.0 - n5) * this.bounds[i].getX(), this.bounds[i].getY(), this.bounds[i].getWidth(), this.bounds[i].getHeight());
            }
        }
        return n3;
    }
    
    public void overlap(final double n) {
        for (int i = 1; i < this.length(); ++i) {
            this.bounds[i].setRect(this.bounds[i - 1].getX() + this.bounds[i - 1].getWidth() - n, this.bounds[i].getY(), this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }
    
    void moveTo(final double n, final double n2) {
        this.bounds[0].setRect(n, n2, this.bounds[0].getWidth(), this.bounds[0].getHeight());
        for (int i = 1; i < this.length(); ++i) {
            this.bounds[i].setRect(n + this.bounds[i].getX(), n2, this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }
    
    protected void shiftBoundariesToNonLinearLayout(final double n, final double n2) {
        final double n3 = n / 20.0;
        final double n4 = n2 / 2.0;
        final SecureRandom secureRandom = new SecureRandom();
        this.bounds[0].setRect(n3, n4, this.bounds[0].getWidth(), this.bounds[0].getHeight());
        for (int i = 1; i < this.length(); ++i) {
            final double height = this.bounds[i].getHeight();
            final double n5 = secureRandom.nextInt() % (n2 / 4.0);
            this.bounds[i].setRect(n3 + this.bounds[i].getX(), n4 + (secureRandom.nextBoolean() ? n5 : (-n5)) + height / 4.0, this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("{text=");
        for (int i = 0; i < this.length(); ++i) {
            sb.append(this.aStrings[i].getIterator().current());
        }
        sb.append("\n\t");
        for (int j = 0; j < this.length(); ++j) {
            sb.append(this.bounds[j].toString());
            final LineMetrics lineMetrics = this.metrics[j];
            sb.append(" ascent=").append(lineMetrics.getAscent()).append(" ");
            sb.append("descent=").append(lineMetrics.getDescent()).append(" ");
            sb.append("leading=").append(lineMetrics.getLeading()).append(" ");
            sb.append("\n\t");
        }
        sb.append("}");
        return sb.toString();
    }
    
    public int length() {
        return this.bounds.length;
    }
    
    public double getX(final int n) {
        return this.getBounds(n).getX();
    }
    
    public double getY(final int n) {
        return this.getBounds(n).getY();
    }
    
    public double getHeight(final int n) {
        return this.getBounds(n).getHeight();
    }
    
    public double getTotalWidth() {
        return this.getX(this.length() - 1) + this.getWidth(this.length() - 1);
    }
    
    public double getWidth(final int n) {
        return this.getBounds(n).getWidth();
    }
    
    public double getAscent(final int n) {
        return this.getMetric(n).getAscent();
    }
    
    double getDescent(final int n) {
        return this.getMetric(n).getDescent();
    }
    
    public double getMaxWidth() {
        double n = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            final double width = this.getWidth(i);
            if (n < width) {
                n = width;
            }
        }
        return n;
    }
    
    public double getMaxAscent() {
        double n = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            final double ascent = this.getAscent(i);
            if (n < ascent) {
                n = ascent;
            }
        }
        return n;
    }
    
    public double getMaxDescent() {
        double n = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            final double descent = this.getDescent(i);
            if (n < descent) {
                n = descent;
            }
        }
        return n;
    }
    
    public double getMaxHeight() {
        double n = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            final double height = this.getHeight(i);
            if (n < height) {
                n = height;
            }
        }
        return n;
    }
    
    public double getMaxX() {
        return this.getX(0) + this.getTotalWidth();
    }
    
    public double getMaxY() {
        return this.getY(0) + this.getMaxHeight();
    }
    
    public Rectangle2D getBounds(final int n) {
        return this.bounds[n];
    }
    
    public LineMetrics getMetric(final int n) {
        return this.metrics[n];
    }
    
    public AttributedCharacterIterator getIterator(final int n) {
        return this.aStrings[n].getIterator();
    }
}
