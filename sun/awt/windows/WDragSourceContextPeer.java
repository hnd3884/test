package sun.awt.windows;

import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import sun.awt.dnd.SunDropTargetContextPeer;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Map;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.DragGestureEvent;
import sun.awt.dnd.SunDragSourceContextPeer;

final class WDragSourceContextPeer extends SunDragSourceContextPeer
{
    private static final WDragSourceContextPeer theInstance;
    
    @Override
    public void startSecondaryEventLoop() {
        WToolkit.startSecondaryEventLoop();
    }
    
    @Override
    public void quitSecondaryEventLoop() {
        WToolkit.quitSecondaryEventLoop();
    }
    
    private WDragSourceContextPeer(final DragGestureEvent dragGestureEvent) {
        super(dragGestureEvent);
    }
    
    static WDragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent trigger) throws InvalidDnDOperationException {
        WDragSourceContextPeer.theInstance.setTrigger(trigger);
        return WDragSourceContextPeer.theInstance;
    }
    
    @Override
    protected void startDrag(final Transferable currentJVMLocalSourceTransferable, final long[] array, final Map map) {
        final long dragSource = this.createDragSource(this.getTrigger().getComponent(), currentJVMLocalSourceTransferable, this.getTrigger().getTriggerEvent(), this.getTrigger().getSourceAsDragGestureRecognizer().getSourceActions(), array, map);
        if (dragSource == 0L) {
            throw new InvalidDnDOperationException("failed to create native peer");
        }
        int[] data = null;
        Point dragImageOffset = null;
        final Image dragImage = this.getDragImage();
        int width = -1;
        int height = -1;
        if (dragImage != null) {
            try {
                width = dragImage.getWidth(null);
                height = dragImage.getHeight(null);
                if (width < 0 || height < 0) {
                    throw new InvalidDnDOperationException("drag image is not ready");
                }
                dragImageOffset = this.getDragImageOffset();
                final BufferedImage bufferedImage = new BufferedImage(width, height, 2);
                bufferedImage.getGraphics().drawImage(dragImage, 0, 0, null);
                data = ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
            }
            catch (final Throwable t) {
                throw new InvalidDnDOperationException("drag image creation problem: " + t.getMessage());
            }
        }
        this.setNativeContext(dragSource);
        SunDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(currentJVMLocalSourceTransferable);
        if (data != null) {
            this.doDragDrop(this.getNativeContext(), this.getCursor(), data, width, height, dragImageOffset.x, dragImageOffset.y);
        }
        else {
            this.doDragDrop(this.getNativeContext(), this.getCursor(), null, -1, -1, 0, 0);
        }
    }
    
    native long createDragSource(final Component p0, final Transferable p1, final InputEvent p2, final int p3, final long[] p4, final Map p5);
    
    native void doDragDrop(final long p0, final Cursor p1, final int[] p2, final int p3, final int p4, final int p5, final int p6);
    
    @Override
    protected native void setNativeCursor(final long p0, final Cursor p1, final int p2);
    
    static {
        theInstance = new WDragSourceContextPeer(null);
    }
}
