package sun.awt;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Component;
import sun.util.logging.PlatformLogger;

public abstract class SunGraphicsCallback
{
    public static final int HEAVYWEIGHTS = 1;
    public static final int LIGHTWEIGHTS = 2;
    public static final int TWO_PASSES = 4;
    private static final PlatformLogger log;
    
    public abstract void run(final Component p0, final Graphics p1);
    
    protected void constrainGraphics(final Graphics graphics, final Rectangle rectangle) {
        if (graphics instanceof ConstrainableGraphics) {
            ((ConstrainableGraphics)graphics).constrain(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        else {
            graphics.translate(rectangle.x, rectangle.y);
        }
        graphics.clipRect(0, 0, rectangle.width, rectangle.height);
    }
    
    public final void runOneComponent(final Component component, Rectangle bounds, final Graphics graphics, final Shape shape, final int n) {
        if (component == null || component.getPeer() == null || !component.isVisible()) {
            return;
        }
        final boolean lightweight = component.isLightweight();
        if ((lightweight && (n & 0x2) == 0x0) || (!lightweight && (n & 0x1) == 0x0)) {
            return;
        }
        if (bounds == null) {
            bounds = component.getBounds();
        }
        if (shape == null || shape.intersects(bounds)) {
            final Graphics create = graphics.create();
            try {
                this.constrainGraphics(create, bounds);
                create.setFont(component.getFont());
                create.setColor(component.getForeground());
                if (create instanceof Graphics2D) {
                    ((Graphics2D)create).setBackground(component.getBackground());
                }
                else if (create instanceof Graphics2Delegate) {
                    ((Graphics2Delegate)create).setBackground(component.getBackground());
                }
                this.run(component, create);
            }
            finally {
                create.dispose();
            }
        }
    }
    
    public final void runComponents(final Component[] array, final Graphics graphics, final int n) {
        final int length = array.length;
        final Shape clip = graphics.getClip();
        if (SunGraphicsCallback.log.isLoggable(PlatformLogger.Level.FINER) && clip != null) {
            final Rectangle bounds = clip.getBounds();
            SunGraphicsCallback.log.finer("x = " + bounds.x + ", y = " + bounds.y + ", width = " + bounds.width + ", height = " + bounds.height);
        }
        if ((n & 0x4) != 0x0) {
            for (int i = length - 1; i >= 0; --i) {
                this.runOneComponent(array[i], null, graphics, clip, 2);
            }
            for (int j = length - 1; j >= 0; --j) {
                this.runOneComponent(array[j], null, graphics, clip, 1);
            }
        }
        else {
            for (int k = length - 1; k >= 0; --k) {
                this.runOneComponent(array[k], null, graphics, clip, n);
            }
        }
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.SunGraphicsCallback");
    }
    
    public static final class PaintHeavyweightComponentsCallback extends SunGraphicsCallback
    {
        private static PaintHeavyweightComponentsCallback instance;
        
        private PaintHeavyweightComponentsCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            if (!component.isLightweight()) {
                component.paintAll(graphics);
            }
            else if (component instanceof Container) {
                this.runComponents(((Container)component).getComponents(), graphics, 3);
            }
        }
        
        public static PaintHeavyweightComponentsCallback getInstance() {
            return PaintHeavyweightComponentsCallback.instance;
        }
        
        static {
            PaintHeavyweightComponentsCallback.instance = new PaintHeavyweightComponentsCallback();
        }
    }
    
    public static final class PrintHeavyweightComponentsCallback extends SunGraphicsCallback
    {
        private static PrintHeavyweightComponentsCallback instance;
        
        private PrintHeavyweightComponentsCallback() {
        }
        
        @Override
        public void run(final Component component, final Graphics graphics) {
            if (!component.isLightweight()) {
                component.printAll(graphics);
            }
            else if (component instanceof Container) {
                this.runComponents(((Container)component).getComponents(), graphics, 3);
            }
        }
        
        public static PrintHeavyweightComponentsCallback getInstance() {
            return PrintHeavyweightComponentsCallback.instance;
        }
        
        static {
            PrintHeavyweightComponentsCallback.instance = new PrintHeavyweightComponentsCallback();
        }
    }
}
