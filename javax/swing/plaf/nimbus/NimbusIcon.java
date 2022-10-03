package javax.swing.plaf.nimbus;

import java.awt.Component;
import javax.swing.JComponent;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.JMenu;
import javax.swing.plaf.UIResource;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.Painter;
import java.awt.Graphics;
import javax.swing.plaf.synth.SynthContext;
import sun.swing.plaf.synth.SynthIcon;

class NimbusIcon extends SynthIcon
{
    private int width;
    private int height;
    private String prefix;
    private String key;
    
    NimbusIcon(final String prefix, final String key, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.prefix = prefix;
        this.key = key;
    }
    
    @Override
    public void paintIcon(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        Painter painter = null;
        if (synthContext != null) {
            painter = (Painter)synthContext.getStyle().get(synthContext, this.key);
        }
        if (painter == null) {
            painter = (Painter)UIManager.get(this.prefix + "[Enabled]." + this.key);
        }
        if (painter != null && synthContext != null) {
            final JComponent component = synthContext.getComponent();
            boolean b = false;
            boolean b2 = false;
            int n5 = 0;
            int n6 = 0;
            if (component instanceof JToolBar) {
                final JToolBar toolBar = (JToolBar)component;
                b = (toolBar.getOrientation() == 1);
                b2 = !toolBar.getComponentOrientation().isLeftToRight();
                final Object resolveToolbarConstraint = NimbusLookAndFeel.resolveToolbarConstraint(toolBar);
                if (toolBar.getBorder() instanceof UIResource) {
                    if (resolveToolbarConstraint == "South") {
                        n6 = 1;
                    }
                    else if (resolveToolbarConstraint == "East") {
                        n5 = 1;
                    }
                }
            }
            else if (component instanceof JMenu) {
                b2 = !component.getComponentOrientation().isLeftToRight();
            }
            if (graphics instanceof Graphics2D) {
                final Graphics2D graphics2D = (Graphics2D)graphics;
                graphics2D.translate(n, n2);
                graphics2D.translate(n5, n6);
                if (b) {
                    graphics2D.rotate(Math.toRadians(90.0));
                    graphics2D.translate(0, -n3);
                    painter.paint(graphics2D, synthContext.getComponent(), n4, n3);
                    graphics2D.translate(0, n3);
                    graphics2D.rotate(Math.toRadians(-90.0));
                }
                else if (b2) {
                    graphics2D.scale(-1.0, 1.0);
                    graphics2D.translate(-n3, 0);
                    painter.paint(graphics2D, synthContext.getComponent(), n3, n4);
                    graphics2D.translate(n3, 0);
                    graphics2D.scale(-1.0, 1.0);
                }
                else {
                    painter.paint(graphics2D, synthContext.getComponent(), n3, n4);
                }
                graphics2D.translate(-n5, -n6);
                graphics2D.translate(-n, -n2);
            }
            else {
                final BufferedImage bufferedImage = new BufferedImage(n3, n4, 2);
                final Graphics2D graphics2 = bufferedImage.createGraphics();
                if (b) {
                    graphics2.rotate(Math.toRadians(90.0));
                    graphics2.translate(0, -n3);
                    painter.paint(graphics2, synthContext.getComponent(), n4, n3);
                }
                else if (b2) {
                    graphics2.scale(-1.0, 1.0);
                    graphics2.translate(-n3, 0);
                    painter.paint(graphics2, synthContext.getComponent(), n3, n4);
                }
                else {
                    painter.paint(graphics2, synthContext.getComponent(), n3, n4);
                }
                graphics2.dispose();
                graphics.drawImage(bufferedImage, n, n2, null);
            }
        }
    }
    
    @Override
    public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        final Painter painter = (Painter)UIManager.get(this.prefix + "[Enabled]." + this.key);
        if (painter != null) {
            final JComponent component2 = (component instanceof JComponent) ? ((JComponent)component) : null;
            final Graphics2D graphics2D = (Graphics2D)graphics;
            graphics2D.translate(n, n2);
            painter.paint(graphics2D, component2, this.width, this.height);
            graphics2D.translate(-n, -n2);
        }
    }
    
    @Override
    public int getIconWidth(final SynthContext synthContext) {
        if (synthContext == null) {
            return this.width;
        }
        final JComponent component = synthContext.getComponent();
        if (!(component instanceof JToolBar) || ((JToolBar)component).getOrientation() != 1) {
            return this.scale(synthContext, this.width);
        }
        if (component.getBorder() instanceof UIResource) {
            return component.getWidth() - 1;
        }
        return component.getWidth();
    }
    
    @Override
    public int getIconHeight(final SynthContext synthContext) {
        if (synthContext == null) {
            return this.height;
        }
        final JComponent component = synthContext.getComponent();
        if (!(component instanceof JToolBar)) {
            return this.scale(synthContext, this.height);
        }
        final JToolBar toolBar = (JToolBar)component;
        if (toolBar.getOrientation() != 0) {
            return this.scale(synthContext, this.width);
        }
        if (toolBar.getBorder() instanceof UIResource) {
            return component.getHeight() - 1;
        }
        return component.getHeight();
    }
    
    private int scale(final SynthContext synthContext, int n) {
        if (synthContext == null || synthContext.getComponent() == null) {
            return n;
        }
        final String s = (String)synthContext.getComponent().getClientProperty("JComponent.sizeVariant");
        if (s != null) {
            if ("large".equals(s)) {
                n *= (int)1.15;
            }
            else if ("small".equals(s)) {
                n *= (int)0.857;
            }
            else if ("mini".equals(s)) {
                n *= (int)0.784;
            }
        }
        return n;
    }
}
