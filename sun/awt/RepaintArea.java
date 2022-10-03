package sun.awt;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Component;
import java.awt.Rectangle;

public class RepaintArea
{
    private static final int MAX_BENEFIT_RATIO = 4;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int UPDATE = 2;
    private static final int RECT_COUNT = 3;
    private Rectangle[] paintRects;
    
    public RepaintArea() {
        this.paintRects = new Rectangle[3];
    }
    
    private RepaintArea(final RepaintArea repaintArea) {
        this.paintRects = new Rectangle[3];
        for (int i = 0; i < 3; ++i) {
            this.paintRects[i] = repaintArea.paintRects[i];
        }
    }
    
    public synchronized void add(final Rectangle rectangle, final int n) {
        if (rectangle.isEmpty()) {
            return;
        }
        int n2 = 2;
        if (n == 800) {
            n2 = ((rectangle.width <= rectangle.height) ? 1 : 0);
        }
        if (this.paintRects[n2] != null) {
            this.paintRects[n2].add(rectangle);
        }
        else {
            this.paintRects[n2] = new Rectangle(rectangle);
        }
    }
    
    private synchronized RepaintArea cloneAndReset() {
        final RepaintArea repaintArea = new RepaintArea(this);
        for (int i = 0; i < 3; ++i) {
            this.paintRects[i] = null;
        }
        return repaintArea;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < 3; ++i) {
            if (this.paintRects[i] != null) {
                return false;
            }
        }
        return true;
    }
    
    public synchronized void constrain(final int x, final int y, final int n, final int n2) {
        for (int i = 0; i < 3; ++i) {
            final Rectangle rectangle = this.paintRects[i];
            if (rectangle != null) {
                if (rectangle.x < x) {
                    final Rectangle rectangle2 = rectangle;
                    rectangle2.width -= x - rectangle.x;
                    rectangle.x = x;
                }
                if (rectangle.y < y) {
                    final Rectangle rectangle3 = rectangle;
                    rectangle3.height -= y - rectangle.y;
                    rectangle.y = y;
                }
                final int n3 = rectangle.x + rectangle.width - x - n;
                if (n3 > 0) {
                    final Rectangle rectangle4 = rectangle;
                    rectangle4.width -= n3;
                }
                final int n4 = rectangle.y + rectangle.height - y - n2;
                if (n4 > 0) {
                    final Rectangle rectangle5 = rectangle;
                    rectangle5.height -= n4;
                }
                if (rectangle.width <= 0 || rectangle.height <= 0) {
                    this.paintRects[i] = null;
                }
            }
        }
    }
    
    public synchronized void subtract(final int n, final int n2, final int n3, final int n4) {
        final Rectangle rectangle = new Rectangle(n, n2, n3, n4);
        for (int i = 0; i < 3; ++i) {
            if (subtract(this.paintRects[i], rectangle) && this.paintRects[i] != null && this.paintRects[i].isEmpty()) {
                this.paintRects[i] = null;
            }
        }
    }
    
    public void paint(final Object o, final boolean b) {
        final Component component = (Component)o;
        if (this.isEmpty()) {
            return;
        }
        if (!component.isVisible()) {
            return;
        }
        final RepaintArea cloneAndReset = this.cloneAndReset();
        if (!subtract(cloneAndReset.paintRects[1], cloneAndReset.paintRects[0])) {
            subtract(cloneAndReset.paintRects[0], cloneAndReset.paintRects[1]);
        }
        if (cloneAndReset.paintRects[0] != null && cloneAndReset.paintRects[1] != null) {
            final Rectangle union = cloneAndReset.paintRects[0].union(cloneAndReset.paintRects[1]);
            final int n = union.width * union.height;
            if (4 * (n - cloneAndReset.paintRects[0].width * cloneAndReset.paintRects[0].height - cloneAndReset.paintRects[1].width * cloneAndReset.paintRects[1].height) < n) {
                cloneAndReset.paintRects[0] = union;
                cloneAndReset.paintRects[1] = null;
            }
        }
        for (int i = 0; i < this.paintRects.length; ++i) {
            if (cloneAndReset.paintRects[i] != null && !cloneAndReset.paintRects[i].isEmpty()) {
                final Graphics graphics = component.getGraphics();
                if (graphics != null) {
                    try {
                        graphics.setClip(cloneAndReset.paintRects[i]);
                        if (i == 2) {
                            this.updateComponent(component, graphics);
                        }
                        else {
                            if (b) {
                                graphics.clearRect(cloneAndReset.paintRects[i].x, cloneAndReset.paintRects[i].y, cloneAndReset.paintRects[i].width, cloneAndReset.paintRects[i].height);
                            }
                            this.paintComponent(component, graphics);
                        }
                    }
                    finally {
                        graphics.dispose();
                    }
                }
            }
        }
    }
    
    protected void updateComponent(final Component component, final Graphics graphics) {
        if (component != null) {
            component.update(graphics);
        }
    }
    
    protected void paintComponent(final Component component, final Graphics graphics) {
        if (component != null) {
            component.paint(graphics);
        }
    }
    
    static boolean subtract(final Rectangle rectangle, final Rectangle rectangle2) {
        if (rectangle == null || rectangle2 == null) {
            return true;
        }
        final Rectangle intersection = rectangle.intersection(rectangle2);
        if (intersection.isEmpty()) {
            return true;
        }
        if (rectangle.x == intersection.x && rectangle.y == intersection.y) {
            if (rectangle.width == intersection.width) {
                rectangle.y += intersection.height;
                rectangle.height -= intersection.height;
                return true;
            }
            if (rectangle.height == intersection.height) {
                rectangle.x += intersection.width;
                rectangle.width -= intersection.width;
                return true;
            }
        }
        else if (rectangle.x + rectangle.width == intersection.x + intersection.width && rectangle.y + rectangle.height == intersection.y + intersection.height) {
            if (rectangle.width == intersection.width) {
                rectangle.height -= intersection.height;
                return true;
            }
            if (rectangle.height == intersection.height) {
                rectangle.width -= intersection.width;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[ horizontal=" + this.paintRects[0] + " vertical=" + this.paintRects[1] + " update=" + this.paintRects[2] + "]";
    }
}
