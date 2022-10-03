package sun.awt;

import java.awt.Dimension;
import java.awt.image.PixelGrabber;
import java.awt.image.ImageObserver;
import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Canvas;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Image;
import java.awt.Cursor;

public abstract class CustomCursor extends Cursor
{
    protected Image image;
    
    public CustomCursor(Image scaledInstance, final Point point, final String s) throws IndexOutOfBoundsException {
        super(s);
        this.image = scaledInstance;
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final Canvas canvas = new Canvas();
        final MediaTracker mediaTracker = new MediaTracker(canvas);
        mediaTracker.addImage(scaledInstance, 0);
        try {
            mediaTracker.waitForAll();
        }
        catch (final InterruptedException ex) {}
        int n = scaledInstance.getWidth(canvas);
        int n2 = scaledInstance.getHeight(canvas);
        if (mediaTracker.isErrorAny() || n < 0 || n2 < 0) {
            final int n3 = 0;
            point.y = n3;
            point.x = n3;
        }
        final Dimension bestCursorSize = defaultToolkit.getBestCursorSize(n, n2);
        if ((bestCursorSize.width != n || bestCursorSize.height != n2) && bestCursorSize.width != 0 && bestCursorSize.height != 0) {
            scaledInstance = scaledInstance.getScaledInstance(bestCursorSize.width, bestCursorSize.height, 1);
            n = bestCursorSize.width;
            n2 = bestCursorSize.height;
        }
        if (point.x >= n || point.y >= n2 || point.x < 0 || point.y < 0) {
            throw new IndexOutOfBoundsException("invalid hotSpot");
        }
        final int[] array = new int[n * n2];
        final PixelGrabber pixelGrabber = new PixelGrabber(scaledInstance.getSource(), 0, 0, n, n2, array, 0, n);
        try {
            pixelGrabber.grabPixels();
        }
        catch (final InterruptedException ex2) {}
        this.createNativeCursor(this.image, array, n, n2, point.x, point.y);
    }
    
    protected abstract void createNativeCursor(final Image p0, final int[] p1, final int p2, final int p3, final int p4, final int p5);
}
