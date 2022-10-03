package sun.awt.windows;

import sun.awt.SunToolkit;
import sun.awt.PeerEvent;
import sun.awt.dnd.SunDropTargetEvent;
import java.io.IOException;
import java.io.FileInputStream;
import sun.awt.dnd.SunDropTargetContextPeer;

final class WDropTargetContextPeer extends SunDropTargetContextPeer
{
    static WDropTargetContextPeer getWDropTargetContextPeer() {
        return new WDropTargetContextPeer();
    }
    
    private WDropTargetContextPeer() {
    }
    
    private static FileInputStream getFileStream(final String s, final long n) throws IOException {
        return new WDropTargetContextPeerFileStream(s, n);
    }
    
    private static Object getIStream(final long n) throws IOException {
        return new WDropTargetContextPeerIStream(n);
    }
    
    @Override
    protected Object getNativeData(final long n) {
        return this.getData(this.getNativeDragContext(), n);
    }
    
    @Override
    protected void doDropDone(final boolean b, final int n, final boolean b2) {
        this.dropDone(this.getNativeDragContext(), b, n);
    }
    
    @Override
    protected void eventPosted(final SunDropTargetEvent sunDropTargetEvent) {
        if (sunDropTargetEvent.getID() != 502) {
            SunToolkit.executeOnEventHandlerThread(new PeerEvent(sunDropTargetEvent.getSource(), new Runnable() {
                @Override
                public void run() {
                    sunDropTargetEvent.getDispatcher().unregisterAllEvents();
                }
            }, 0L));
        }
    }
    
    private native Object getData(final long p0, final long p1);
    
    private native void dropDone(final long p0, final boolean p1, final int p2);
}
