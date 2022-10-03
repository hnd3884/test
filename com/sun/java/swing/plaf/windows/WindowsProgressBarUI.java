package com.sun.java.swing.plaf.windows;

import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class WindowsProgressBarUI extends BasicProgressBarUI
{
    private Rectangle previousFullBox;
    private Insets indeterminateInsets;
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsProgressBarUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        if (XPStyle.getXP() != null) {
            LookAndFeel.installProperty(this.progressBar, "opaque", Boolean.FALSE);
            this.progressBar.setBorder(null);
            this.indeterminateInsets = UIManager.getInsets("ProgressBar.indeterminateInsets");
        }
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, int n2) {
        int baseline = super.getBaseline(component, n, n2);
        if (XPStyle.getXP() != null && this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
            final FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
            final int top = this.progressBar.getInsets().top;
            int n3;
            if (this.progressBar.isIndeterminate()) {
                n3 = -1;
                --n2;
            }
            else {
                n3 = 0;
                n2 -= 3;
            }
            baseline = n3 + (n2 + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
        }
        return baseline;
    }
    
    @Override
    protected Dimension getPreferredInnerHorizontal() {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            return new Dimension((int)super.getPreferredInnerHorizontal().getWidth(), xp.getSkin(this.progressBar, TMSchema.Part.PP_BAR).getHeight());
        }
        return super.getPreferredInnerHorizontal();
    }
    
    @Override
    protected Dimension getPreferredInnerVertical() {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            return new Dimension(xp.getSkin(this.progressBar, TMSchema.Part.PP_BARVERT).getWidth(), (int)super.getPreferredInnerVertical().getHeight());
        }
        return super.getPreferredInnerVertical();
    }
    
    @Override
    protected void paintDeterminate(final Graphics graphics, final JComponent component) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final boolean b = this.progressBar.getOrientation() == 1;
            final boolean leftToRight = WindowsGraphicsUtils.isLeftToRight(component);
            int width = this.progressBar.getWidth();
            int n = this.progressBar.getHeight() - 1;
            final int amountFull = this.getAmountFull(null, width, n);
            this.paintXPBackground(graphics, b, width, n);
            if (this.progressBar.isStringPainted()) {
                graphics.setColor(this.progressBar.getForeground());
                n -= 2;
                width -= 2;
                if (width <= 0 || n <= 0) {
                    return;
                }
                final Graphics2D graphics2D = (Graphics2D)graphics;
                graphics2D.setStroke(new BasicStroke((float)(b ? width : n), 0, 2));
                if (!b) {
                    if (leftToRight) {
                        graphics2D.drawLine(2, n / 2 + 1, amountFull - 2, n / 2 + 1);
                    }
                    else {
                        graphics2D.drawLine(2 + width, n / 2 + 1, 2 + width - (amountFull - 2), n / 2 + 1);
                    }
                    this.paintString(graphics, 0, 0, width, n, amountFull, null);
                }
                else {
                    graphics2D.drawLine(width / 2 + 1, n + 1, width / 2 + 1, n + 1 - amountFull + 2);
                    this.paintString(graphics, 2, 2, width, n, amountFull, null);
                }
            }
            else {
                final XPStyle.Skin skin = xp.getSkin(this.progressBar, b ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK);
                int n2;
                if (b) {
                    n2 = width - 5;
                }
                else {
                    n2 = n - 5;
                }
                final int int1 = xp.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSCHUNKSIZE, 2);
                final int int2 = xp.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
                int n3 = (amountFull - 4) / (int1 + int2);
                if (int2 > 0 && n3 * (int1 + int2) + int1 < amountFull - 4) {
                    ++n3;
                }
                for (int i = 0; i < n3; ++i) {
                    if (b) {
                        skin.paintSkin(graphics, 3, n - i * (int1 + int2) - int1 - 2, n2, int1, null);
                    }
                    else if (leftToRight) {
                        skin.paintSkin(graphics, 4 + i * (int1 + int2), 2, int1, n2, null);
                    }
                    else {
                        skin.paintSkin(graphics, width - (2 + (i + 1) * (int1 + int2)), 2, int1, n2, null);
                    }
                }
            }
        }
        else {
            super.paintDeterminate(graphics, component);
        }
    }
    
    @Override
    protected void setAnimationIndex(final int animationIndex) {
        super.setAnimationIndex(animationIndex);
        if (XPStyle.getXP() != null) {
            if (this.boxRect != null) {
                final Rectangle fullChunkBounds = this.getFullChunkBounds(this.boxRect);
                if (this.previousFullBox != null) {
                    fullChunkBounds.add(this.previousFullBox);
                }
                this.progressBar.repaint(fullChunkBounds);
            }
            else {
                this.progressBar.repaint();
            }
        }
    }
    
    @Override
    protected int getBoxLength(final int n, final int n2) {
        if (XPStyle.getXP() != null) {
            return 6;
        }
        return super.getBoxLength(n, n2);
    }
    
    @Override
    protected Rectangle getBox(final Rectangle rectangle) {
        final Rectangle box = super.getBox(rectangle);
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final boolean b = this.progressBar.getOrientation() == 1;
            final TMSchema.Part part = b ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
            final Insets indeterminateInsets = this.indeterminateInsets;
            final int animationIndex = this.getAnimationIndex();
            final int n = this.getFrameCount() / 2;
            final int int1 = xp.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
            final int n2 = animationIndex % n;
            if (!b) {
                box.y += indeterminateInsets.top;
                box.height = this.progressBar.getHeight() - indeterminateInsets.top - indeterminateInsets.bottom;
                box.x = (int)((this.progressBar.getWidth() - indeterminateInsets.left - indeterminateInsets.right + (box.width + int1) * 2) / (double)n * n2) + indeterminateInsets.left;
            }
            else {
                box.x += indeterminateInsets.left;
                box.width = this.progressBar.getWidth() - indeterminateInsets.left - indeterminateInsets.right;
                box.y = (int)((this.progressBar.getHeight() - indeterminateInsets.top - indeterminateInsets.bottom + (box.height + int1) * 2) / (double)n * n2) + indeterminateInsets.top;
            }
        }
        return box;
    }
    
    @Override
    protected void paintIndeterminate(final Graphics graphics, final JComponent component) {
        if (XPStyle.getXP() != null) {
            final boolean b = this.progressBar.getOrientation() == 1;
            final int width = this.progressBar.getWidth();
            final int height = this.progressBar.getHeight();
            this.paintXPBackground(graphics, b, width, height);
            this.boxRect = this.getBox(this.boxRect);
            if (this.boxRect != null) {
                graphics.setColor(this.progressBar.getForeground());
                if (!(graphics instanceof Graphics2D)) {
                    return;
                }
                this.paintIndeterminateFrame(this.boxRect, (Graphics2D)graphics, b, width, height);
                if (this.progressBar.isStringPainted()) {
                    if (!b) {
                        this.paintString(graphics, -1, -1, width, height, 0, null);
                    }
                    else {
                        this.paintString(graphics, 1, 1, width, height, 0, null);
                    }
                }
            }
        }
        else {
            super.paintIndeterminate(graphics, component);
        }
    }
    
    private Rectangle getFullChunkBounds(final Rectangle rectangle) {
        final boolean b = this.progressBar.getOrientation() == 1;
        final XPStyle xp = XPStyle.getXP();
        final int n = (xp != null) ? xp.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0) : 0;
        if (!b) {
            final int n2 = rectangle.width + n;
            return new Rectangle(rectangle.x - n2 * 2, rectangle.y, n2 * 3, rectangle.height);
        }
        final int n3 = rectangle.height + n;
        return new Rectangle(rectangle.x, rectangle.y - n3 * 2, rectangle.width, n3 * 3);
    }
    
    private void paintIndeterminateFrame(final Rectangle rectangle, final Graphics2D graphics2D, final boolean b, final int n, final int n2) {
        final XPStyle xp = XPStyle.getXP();
        if (xp == null) {
            return;
        }
        final Graphics2D graphics2D2 = (Graphics2D)graphics2D.create();
        final TMSchema.Part part = b ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
        final TMSchema.Part part2 = b ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK;
        final int int1 = xp.getInt(this.progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
        int n3;
        int n4;
        if (!b) {
            n3 = -rectangle.width - int1;
            n4 = 0;
        }
        else {
            n3 = 0;
            n4 = -rectangle.height - int1;
        }
        final Rectangle fullChunkBounds = this.getFullChunkBounds(rectangle);
        this.previousFullBox = fullChunkBounds;
        final Insets indeterminateInsets = this.indeterminateInsets;
        graphics2D2.clip(new Rectangle(indeterminateInsets.left, indeterminateInsets.top, n - indeterminateInsets.left - indeterminateInsets.right, n2 - indeterminateInsets.top - indeterminateInsets.bottom).intersection(fullChunkBounds));
        final XPStyle.Skin skin = xp.getSkin(this.progressBar, part2);
        graphics2D2.setComposite(AlphaComposite.getInstance(3, 0.8f));
        skin.paintSkin(graphics2D2, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
        rectangle.translate(n3, n4);
        graphics2D2.setComposite(AlphaComposite.getInstance(3, 0.5f));
        skin.paintSkin(graphics2D2, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
        rectangle.translate(n3, n4);
        graphics2D2.setComposite(AlphaComposite.getInstance(3, 0.2f));
        skin.paintSkin(graphics2D2, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
        graphics2D2.dispose();
    }
    
    private void paintXPBackground(final Graphics graphics, final boolean b, final int n, final int n2) {
        final XPStyle xp = XPStyle.getXP();
        if (xp == null) {
            return;
        }
        xp.getSkin(this.progressBar, b ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR).paintSkin(graphics, 0, 0, n, n2, null);
    }
}
