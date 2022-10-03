package javax.swing.plaf.nimbus;

import javax.swing.JSlider;
import java.awt.Color;
import javax.swing.JComponent;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.Painter;
import javax.swing.plaf.synth.SynthPainter;

class SynthPainterImpl extends SynthPainter
{
    private NimbusStyle style;
    
    SynthPainterImpl(final NimbusStyle style) {
        this.style = style;
    }
    
    private void paint(final Painter painter, final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final AffineTransform affineTransform) {
        if (painter != null) {
            if (graphics instanceof Graphics2D) {
                final Graphics2D graphics2D = (Graphics2D)graphics;
                if (affineTransform != null) {
                    graphics2D.transform(affineTransform);
                }
                graphics2D.translate(n, n2);
                painter.paint(graphics2D, synthContext.getComponent(), n3, n4);
                graphics2D.translate(-n, -n2);
                if (affineTransform != null) {
                    try {
                        graphics2D.transform(affineTransform.createInverse());
                    }
                    catch (final NoninvertibleTransformException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else {
                final BufferedImage bufferedImage = new BufferedImage(n3, n4, 2);
                final Graphics2D graphics2 = bufferedImage.createGraphics();
                if (affineTransform != null) {
                    graphics2.transform(affineTransform);
                }
                painter.paint(graphics2, synthContext.getComponent(), n3, n4);
                graphics2.dispose();
                graphics.drawImage(bufferedImage, n, n2, null);
            }
        }
    }
    
    private void paintBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final AffineTransform affineTransform) {
        final JComponent component = synthContext.getComponent();
        final Color color = (component != null) ? component.getBackground() : null;
        if (color == null || color.getAlpha() > 0) {
            final Painter backgroundPainter = this.style.getBackgroundPainter(synthContext);
            if (backgroundPainter != null) {
                this.paint(backgroundPainter, synthContext, graphics, n, n2, n3, n4, affineTransform);
            }
        }
    }
    
    private void paintForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final AffineTransform affineTransform) {
        final Painter foregroundPainter = this.style.getForegroundPainter(synthContext);
        if (foregroundPainter != null) {
            this.paint(foregroundPainter, synthContext, graphics, n, n2, n3, n4, affineTransform);
        }
    }
    
    private void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final AffineTransform affineTransform) {
        final Painter borderPainter = this.style.getBorderPainter(synthContext);
        if (borderPainter != null) {
            this.paint(borderPainter, synthContext, graphics, n, n2, n3, n4, affineTransform);
        }
    }
    
    private void paintBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        boolean leftToRight = synthContext.getComponent().getComponentOrientation().isLeftToRight();
        if (synthContext.getComponent() instanceof JSlider) {
            leftToRight = true;
        }
        if (n5 == 1 && leftToRight) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(-1.0, 1.0);
            affineTransform.rotate(Math.toRadians(90.0));
            this.paintBackground(synthContext, graphics, n2, n, n4, n3, affineTransform);
        }
        else if (n5 == 1) {
            final AffineTransform affineTransform2 = new AffineTransform();
            affineTransform2.rotate(Math.toRadians(90.0));
            affineTransform2.translate(0.0, -(n + n3));
            this.paintBackground(synthContext, graphics, n2, n, n4, n3, affineTransform2);
        }
        else if (n5 == 0 && leftToRight) {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform3 = new AffineTransform();
            affineTransform3.translate(n, n2);
            affineTransform3.scale(-1.0, 1.0);
            affineTransform3.translate(-n3, 0.0);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform3);
        }
    }
    
    private void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        final boolean leftToRight = synthContext.getComponent().getComponentOrientation().isLeftToRight();
        if (n5 == 1 && leftToRight) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(-1.0, 1.0);
            affineTransform.rotate(Math.toRadians(90.0));
            this.paintBorder(synthContext, graphics, n2, n, n4, n3, affineTransform);
        }
        else if (n5 == 1) {
            final AffineTransform affineTransform2 = new AffineTransform();
            affineTransform2.rotate(Math.toRadians(90.0));
            affineTransform2.translate(0.0, -(n + n3));
            this.paintBorder(synthContext, graphics, n2, 0, n4, n3, affineTransform2);
        }
        else if (n5 == 0 && leftToRight) {
            this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
        }
    }
    
    private void paintForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        final boolean leftToRight = synthContext.getComponent().getComponentOrientation().isLeftToRight();
        if (n5 == 1 && leftToRight) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(-1.0, 1.0);
            affineTransform.rotate(Math.toRadians(90.0));
            this.paintForeground(synthContext, graphics, n2, n, n4, n3, affineTransform);
        }
        else if (n5 == 1) {
            final AffineTransform affineTransform2 = new AffineTransform();
            affineTransform2.rotate(Math.toRadians(90.0));
            affineTransform2.translate(0.0, -(n + n3));
            this.paintForeground(synthContext, graphics, n2, 0, n4, n3, affineTransform2);
        }
        else if (n5 == 0 && leftToRight) {
            this.paintForeground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            this.paintForeground(synthContext, graphics, n, n2, n3, n4, null);
        }
    }
    
    @Override
    public void paintArrowButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (synthContext.getComponent().getComponentOrientation().isLeftToRight()) {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(n, n2);
            affineTransform.scale(-1.0, 1.0);
            affineTransform.translate(-n3, 0.0);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform);
        }
    }
    
    @Override
    public void paintArrowButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintArrowButtonForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        final String name = synthContext.getComponent().getName();
        final boolean leftToRight = synthContext.getComponent().getComponentOrientation().isLeftToRight();
        if ("Spinner.nextButton".equals(name) || "Spinner.previousButton".equals(name)) {
            if (leftToRight) {
                this.paintForeground(synthContext, graphics, n, n2, n3, n4, null);
            }
            else {
                final AffineTransform affineTransform = new AffineTransform();
                affineTransform.translate(n3, 0.0);
                affineTransform.scale(-1.0, 1.0);
                this.paintForeground(synthContext, graphics, n, n2, n3, n4, affineTransform);
            }
        }
        else if (n5 == 7) {
            this.paintForeground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else if (n5 == 1) {
            if (leftToRight) {
                final AffineTransform affineTransform2 = new AffineTransform();
                affineTransform2.scale(-1.0, 1.0);
                affineTransform2.rotate(Math.toRadians(90.0));
                this.paintForeground(synthContext, graphics, n2, 0, n4, n3, affineTransform2);
            }
            else {
                final AffineTransform affineTransform3 = new AffineTransform();
                affineTransform3.rotate(Math.toRadians(90.0));
                affineTransform3.translate(0.0, -(n + n3));
                this.paintForeground(synthContext, graphics, n2, 0, n4, n3, affineTransform3);
            }
        }
        else if (n5 == 3) {
            final AffineTransform affineTransform4 = new AffineTransform();
            affineTransform4.translate(n3, 0.0);
            affineTransform4.scale(-1.0, 1.0);
            this.paintForeground(synthContext, graphics, n, n2, n3, n4, affineTransform4);
        }
        else if (n5 == 5) {
            if (leftToRight) {
                final AffineTransform affineTransform5 = new AffineTransform();
                affineTransform5.rotate(Math.toRadians(-90.0));
                affineTransform5.translate(-n4, 0.0);
                this.paintForeground(synthContext, graphics, n2, n, n4, n3, affineTransform5);
            }
            else {
                final AffineTransform affineTransform6 = new AffineTransform();
                affineTransform6.scale(-1.0, 1.0);
                affineTransform6.rotate(Math.toRadians(-90.0));
                affineTransform6.translate(-(n4 + n2), -(n3 + n));
                this.paintForeground(synthContext, graphics, n2, n, n4, n3, affineTransform6);
            }
        }
    }
    
    @Override
    public void paintButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintCheckBoxMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintCheckBoxMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintCheckBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintCheckBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintColorChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintColorChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintComboBoxBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (synthContext.getComponent().getComponentOrientation().isLeftToRight()) {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(n, n2);
            affineTransform.scale(-1.0, 1.0);
            affineTransform.translate(-n3, 0.0);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform);
        }
    }
    
    @Override
    public void paintComboBoxBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintDesktopIconBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintDesktopIconBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintDesktopPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintDesktopPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintEditorPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintEditorPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintFileChooserBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintFileChooserBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintFormattedTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (synthContext.getComponent().getComponentOrientation().isLeftToRight()) {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(n, n2);
            affineTransform.scale(-1.0, 1.0);
            affineTransform.translate(-n3, 0.0);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform);
        }
    }
    
    @Override
    public void paintFormattedTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (synthContext.getComponent().getComponentOrientation().isLeftToRight()) {
            this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(n, n2);
            affineTransform.scale(-1.0, 1.0);
            affineTransform.translate(-n3, 0.0);
            this.paintBorder(synthContext, graphics, 0, 0, n3, n4, affineTransform);
        }
    }
    
    @Override
    public void paintInternalFrameTitlePaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintInternalFrameTitlePaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintInternalFrameBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintInternalFrameBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintLabelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintLabelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintListBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintListBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintMenuBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintMenuBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintOptionPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintOptionPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintPanelBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintPanelBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintPasswordFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintPasswordFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintPopupMenuBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintPopupMenuBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintProgressBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintProgressBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintProgressBarForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintForeground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintRadioButtonMenuItemBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintRadioButtonMenuItemBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintRadioButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintRadioButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintRootPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintRootPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintScrollBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintScrollBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintScrollBarThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintScrollBarThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintScrollBarTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintScrollBarTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintScrollPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintScrollPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSeparatorBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSeparatorBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSeparatorForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintForeground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSliderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSliderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSliderThumbBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, int n5) {
        if (synthContext.getComponent().getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE) {
            if (n5 == 0) {
                n5 = 1;
            }
            else {
                n5 = 0;
            }
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
        else {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
        }
    }
    
    @Override
    public void paintSliderThumbBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSliderTrackBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSliderTrackBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintSpinnerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSpinnerBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSplitPaneDividerBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n5 == 1) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(-1.0, 1.0);
            affineTransform.rotate(Math.toRadians(90.0));
            this.paintBackground(synthContext, graphics, n2, n, n4, n3, affineTransform);
        }
        else {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
    }
    
    @Override
    public void paintSplitPaneDividerForeground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintForeground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSplitPaneDragDivider(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSplitPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintSplitPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n5 == 2) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(-1.0, 1.0);
            affineTransform.rotate(Math.toRadians(90.0));
            this.paintBackground(synthContext, graphics, n2, n, n4, n3, affineTransform);
        }
        else if (n5 == 4) {
            final AffineTransform affineTransform2 = new AffineTransform();
            affineTransform2.rotate(Math.toRadians(90.0));
            affineTransform2.translate(0.0, -(n + n3));
            this.paintBackground(synthContext, graphics, n2, 0, n4, n3, affineTransform2);
        }
        else if (n5 == 3) {
            final AffineTransform affineTransform3 = new AffineTransform();
            affineTransform3.translate(n, n2);
            affineTransform3.scale(1.0, -1.0);
            affineTransform3.translate(0.0, -n4);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform3);
        }
        else {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
    }
    
    @Override
    public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneTabAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneTabBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (n6 == 2) {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(-1.0, 1.0);
            affineTransform.rotate(Math.toRadians(90.0));
            this.paintBackground(synthContext, graphics, n2, n, n4, n3, affineTransform);
        }
        else if (n6 == 4) {
            final AffineTransform affineTransform2 = new AffineTransform();
            affineTransform2.rotate(Math.toRadians(90.0));
            affineTransform2.translate(0.0, -(n + n3));
            this.paintBackground(synthContext, graphics, n2, 0, n4, n3, affineTransform2);
        }
        else if (n6 == 3) {
            final AffineTransform affineTransform3 = new AffineTransform();
            affineTransform3.translate(n, n2);
            affineTransform3.scale(1.0, -1.0);
            affineTransform3.translate(0.0, -n4);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform3);
        }
        else {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
    }
    
    @Override
    public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneTabBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTabbedPaneContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTableHeaderBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTableHeaderBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTableBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTableBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTextAreaBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTextAreaBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTextPaneBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTextPaneBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTextFieldBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (synthContext.getComponent().getComponentOrientation().isLeftToRight()) {
            this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(n, n2);
            affineTransform.scale(-1.0, 1.0);
            affineTransform.translate(-n3, 0.0);
            this.paintBackground(synthContext, graphics, 0, 0, n3, n4, affineTransform);
        }
    }
    
    @Override
    public void paintTextFieldBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (synthContext.getComponent().getComponentOrientation().isLeftToRight()) {
            this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
        }
        else {
            final AffineTransform affineTransform = new AffineTransform();
            affineTransform.translate(n, n2);
            affineTransform.scale(-1.0, 1.0);
            affineTransform.translate(-n3, 0.0);
            this.paintBorder(synthContext, graphics, 0, 0, n3, n4, affineTransform);
        }
    }
    
    @Override
    public void paintToggleButtonBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToggleButtonBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarContentBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarDragWindowBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolBarDragWindowBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, n5);
    }
    
    @Override
    public void paintToolTipBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintToolTipBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTreeBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTreeBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTreeCellBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTreeCellBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintTreeCellFocus(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public void paintViewportBackground(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBackground(synthContext, graphics, n, n2, n3, n4, null);
    }
    
    @Override
    public void paintViewportBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.paintBorder(synthContext, graphics, n, n2, n3, n4, null);
    }
}
