package javax.swing.plaf.metal;

import java.awt.image.RGBImageFilter;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.image.ImageObserver;
import sun.swing.CachedPainter;
import sun.swing.ImageIconUIResource;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.Toolkit;
import javax.swing.Icon;
import java.awt.Image;
import javax.swing.JToolBar;
import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics;

class MetalUtils
{
    static void drawFlush3DBorder(final Graphics graphics, final Rectangle rectangle) {
        drawFlush3DBorder(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    static void drawFlush3DBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.translate(n, n2);
        graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        graphics.drawRect(0, 0, n3 - 2, n4 - 2);
        graphics.setColor(MetalLookAndFeel.getControlHighlight());
        graphics.drawRect(1, 1, n3 - 2, n4 - 2);
        graphics.setColor(MetalLookAndFeel.getControl());
        graphics.drawLine(0, n4 - 1, 1, n4 - 2);
        graphics.drawLine(n3 - 1, 0, n3 - 2, 1);
        graphics.translate(-n, -n2);
    }
    
    static void drawPressed3DBorder(final Graphics graphics, final Rectangle rectangle) {
        drawPressed3DBorder(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    static void drawDisabledBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.translate(n, n2);
        graphics.setColor(MetalLookAndFeel.getControlShadow());
        graphics.drawRect(0, 0, n3 - 1, n4 - 1);
        graphics.translate(-n, -n2);
    }
    
    static void drawPressed3DBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.translate(n, n2);
        drawFlush3DBorder(graphics, 0, 0, n3, n4);
        graphics.setColor(MetalLookAndFeel.getControlShadow());
        graphics.drawLine(1, 1, 1, n4 - 2);
        graphics.drawLine(1, 1, n3 - 2, 1);
        graphics.translate(-n, -n2);
    }
    
    static void drawDark3DBorder(final Graphics graphics, final Rectangle rectangle) {
        drawDark3DBorder(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    static void drawDark3DBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.translate(n, n2);
        drawFlush3DBorder(graphics, 0, 0, n3, n4);
        graphics.setColor(MetalLookAndFeel.getControl());
        graphics.drawLine(1, 1, 1, n4 - 2);
        graphics.drawLine(1, 1, n3 - 2, 1);
        graphics.setColor(MetalLookAndFeel.getControlShadow());
        graphics.drawLine(1, n4 - 2, 1, n4 - 2);
        graphics.drawLine(n3 - 2, 1, n3 - 2, 1);
        graphics.translate(-n, -n2);
    }
    
    static void drawButtonBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final boolean b) {
        if (b) {
            drawActiveButtonBorder(graphics, n, n2, n3, n4);
        }
        else {
            drawFlush3DBorder(graphics, n, n2, n3, n4);
        }
    }
    
    static void drawActiveButtonBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        drawFlush3DBorder(graphics, n, n2, n3, n4);
        graphics.setColor(MetalLookAndFeel.getPrimaryControl());
        graphics.drawLine(n + 1, n2 + 1, n + 1, n4 - 3);
        graphics.drawLine(n + 1, n2 + 1, n3 - 3, n + 1);
        graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
        graphics.drawLine(n + 2, n4 - 2, n3 - 2, n4 - 2);
        graphics.drawLine(n3 - 2, n2 + 2, n3 - 2, n4 - 2);
    }
    
    static void drawDefaultButtonBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final boolean b) {
        drawButtonBorder(graphics, n + 1, n2 + 1, n3 - 1, n4 - 1, b);
        graphics.translate(n, n2);
        graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        graphics.drawRect(0, 0, n3 - 3, n4 - 3);
        graphics.drawLine(n3 - 2, 0, n3 - 2, 0);
        graphics.drawLine(0, n4 - 2, 0, n4 - 2);
        graphics.translate(-n, -n2);
    }
    
    static void drawDefaultButtonPressedBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        drawPressed3DBorder(graphics, n + 1, n2 + 1, n3 - 1, n4 - 1);
        graphics.translate(n, n2);
        graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        graphics.drawRect(0, 0, n3 - 3, n4 - 3);
        graphics.drawLine(n3 - 2, 0, n3 - 2, 0);
        graphics.drawLine(0, n4 - 2, 0, n4 - 2);
        graphics.setColor(MetalLookAndFeel.getControl());
        graphics.drawLine(n3 - 1, 0, n3 - 1, 0);
        graphics.drawLine(0, n4 - 1, 0, n4 - 1);
        graphics.translate(-n, -n2);
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
    
    static int getInt(final Object o, final int n) {
        final Object value = UIManager.get(o);
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            }
            catch (final NumberFormatException ex) {}
        }
        return n;
    }
    
    static boolean drawGradient(final Component component, final Graphics graphics, final String s, final int n, final int n2, final int n3, final int n4, final boolean b) {
        final List list = (List)UIManager.get(s);
        if (list == null || !(graphics instanceof Graphics2D)) {
            return false;
        }
        if (n3 <= 0 || n4 <= 0) {
            return true;
        }
        GradientPainter.INSTANCE.paint(component, (Graphics2D)graphics, list, n, n2, n3, n4, b);
        return true;
    }
    
    static boolean isToolBarButton(final JComponent component) {
        return component.getParent() instanceof JToolBar;
    }
    
    static Icon getOceanToolBarIcon(final Image image) {
        return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new OceanToolBarImageFilter())));
    }
    
    static Icon getOceanDisabledButtonIcon(final Image image) {
        final Object[] array = (Object[])UIManager.get("Button.disabledGrayRange");
        int intValue = 180;
        int intValue2 = 215;
        if (array != null) {
            intValue = (int)array[0];
            intValue2 = (int)array[1];
        }
        return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new OceanDisabledButtonImageFilter(intValue, intValue2))));
    }
    
    private static class GradientPainter extends CachedPainter
    {
        public static final GradientPainter INSTANCE;
        private static final int IMAGE_SIZE = 64;
        private int w;
        private int h;
        
        GradientPainter(final int n) {
            super(n);
        }
        
        public void paint(final Component component, final Graphics2D graphics2D, final List list, final int n, final int n2, final int w, final int h, final boolean b) {
            int n3;
            int n4;
            if (b) {
                n3 = 64;
                n4 = h;
            }
            else {
                n3 = w;
                n4 = 64;
            }
            synchronized (component.getTreeLock()) {
                this.w = w;
                this.h = h;
                this.paint(component, graphics2D, n, n2, n3, n4, list, b);
            }
        }
        
        @Override
        protected void paintToImage(final Component component, final Image image, final Graphics graphics, final int n, final int n2, final Object[] array) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final List list = (List)array[0];
            if (array[1]) {
                this.drawVerticalGradient(graphics2D, ((Number)list.get(0)).floatValue(), ((Number)list.get(1)).floatValue(), (Color)list.get(2), (Color)list.get(3), (Color)list.get(4), n, n2);
            }
            else {
                this.drawHorizontalGradient(graphics2D, ((Number)list.get(0)).floatValue(), ((Number)list.get(1)).floatValue(), (Color)list.get(2), (Color)list.get(3), (Color)list.get(4), n, n2);
            }
        }
        
        @Override
        protected void paintImage(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Image image, final Object[] array) {
            final boolean booleanValue = (boolean)array[1];
            graphics.translate(n, n2);
            if (booleanValue) {
                for (int i = 0; i < this.w; i += 64) {
                    final int min = Math.min(64, this.w - i);
                    graphics.drawImage(image, i, 0, i + min, this.h, 0, 0, min, this.h, null);
                }
            }
            else {
                for (int j = 0; j < this.h; j += 64) {
                    final int min2 = Math.min(64, this.h - j);
                    graphics.drawImage(image, 0, j, this.w, j + min2, 0, 0, this.w, min2, null);
                }
            }
            graphics.translate(-n, -n2);
        }
        
        private void drawVerticalGradient(final Graphics2D graphics2D, final float n, final float n2, final Color color, final Color color2, final Color color3, final int n3, final int n4) {
            final int n5 = (int)(n * n4);
            final int n6 = (int)(n2 * n4);
            if (n5 > 0) {
                graphics2D.setPaint(this.getGradient(0.0f, 0.0f, color, 0.0f, (float)n5, color2));
                graphics2D.fillRect(0, 0, n3, n5);
            }
            if (n6 > 0) {
                graphics2D.setColor(color2);
                graphics2D.fillRect(0, n5, n3, n6);
            }
            if (n5 > 0) {
                graphics2D.setPaint(this.getGradient(0.0f, n5 + (float)n6, color2, 0.0f, n5 * 2.0f + n6, color));
                graphics2D.fillRect(0, n5 + n6, n3, n5);
            }
            if (n4 - n5 * 2 - n6 > 0) {
                graphics2D.setPaint(this.getGradient(0.0f, n5 * 2.0f + n6, color, 0.0f, (float)n4, color3));
                graphics2D.fillRect(0, n5 * 2 + n6, n3, n4 - n5 * 2 - n6);
            }
        }
        
        private void drawHorizontalGradient(final Graphics2D graphics2D, final float n, final float n2, final Color color, final Color color2, final Color color3, final int n3, final int n4) {
            final int n5 = (int)(n * n3);
            final int n6 = (int)(n2 * n3);
            if (n5 > 0) {
                graphics2D.setPaint(this.getGradient(0.0f, 0.0f, color, (float)n5, 0.0f, color2));
                graphics2D.fillRect(0, 0, n5, n4);
            }
            if (n6 > 0) {
                graphics2D.setColor(color2);
                graphics2D.fillRect(n5, 0, n6, n4);
            }
            if (n5 > 0) {
                graphics2D.setPaint(this.getGradient(n5 + (float)n6, 0.0f, color2, n5 * 2.0f + n6, 0.0f, color));
                graphics2D.fillRect(n5 + n6, 0, n5, n4);
            }
            if (n3 - n5 * 2 - n6 > 0) {
                graphics2D.setPaint(this.getGradient(n5 * 2.0f + n6, 0.0f, color, (float)n3, 0.0f, color3));
                graphics2D.fillRect(n5 * 2 + n6, 0, n3 - n5 * 2 - n6, n4);
            }
        }
        
        private GradientPaint getGradient(final float n, final float n2, final Color color, final float n3, final float n4, final Color color2) {
            return new GradientPaint(n, n2, color, n3, n4, color2, true);
        }
        
        static {
            INSTANCE = new GradientPainter(8);
        }
    }
    
    private static class OceanDisabledButtonImageFilter extends RGBImageFilter
    {
        private float min;
        private float factor;
        
        OceanDisabledButtonImageFilter(final int n, final int n2) {
            this.canFilterIndexColorModel = true;
            this.min = (float)n;
            this.factor = (n2 - n) / 255.0f;
        }
        
        @Override
        public int filterRGB(final int n, final int n2, final int n3) {
            final int min = Math.min(255, (int)((0.2125f * (n3 >> 16 & 0xFF) + 0.7154f * (n3 >> 8 & 0xFF) + 0.0721f * (n3 & 0xFF) + 0.5f) * this.factor + this.min));
            return (n3 & 0xFF000000) | min << 16 | min << 8 | min << 0;
        }
    }
    
    private static class OceanToolBarImageFilter extends RGBImageFilter
    {
        OceanToolBarImageFilter() {
            this.canFilterIndexColorModel = true;
        }
        
        @Override
        public int filterRGB(final int n, final int n2, final int n3) {
            final int max = Math.max(Math.max(n3 >> 16 & 0xFF, n3 >> 8 & 0xFF), n3 & 0xFF);
            return (n3 & 0xFF000000) | max << 16 | max << 8 | max << 0;
        }
    }
}
