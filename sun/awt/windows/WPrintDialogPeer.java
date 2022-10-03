package sun.awt.windows;

import java.awt.image.BufferedImage;
import sun.java2d.pipe.Region;
import java.awt.peer.ComponentPeer;
import java.awt.dnd.DropTarget;
import sun.awt.CausedFocusEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Event;
import java.awt.Component;
import sun.awt.AWTAccessor;
import java.util.List;
import java.util.Iterator;
import java.awt.Dialog;
import java.awt.Window;
import java.util.Vector;
import java.awt.peer.DialogPeer;

class WPrintDialogPeer extends WWindowPeer implements DialogPeer
{
    private WComponentPeer parent;
    private Vector<WWindowPeer> blockedWindows;
    
    WPrintDialogPeer(final WPrintDialog wPrintDialog) {
        super(wPrintDialog);
        this.blockedWindows = new Vector<WWindowPeer>();
    }
    
    @Override
    void create(final WComponentPeer parent) {
        this.parent = parent;
    }
    
    @Override
    protected void checkCreation() {
    }
    
    @Override
    protected void disposeImpl() {
        WToolkit.targetDisposedPeer(this.target, this);
    }
    
    private native boolean _show();
    
    @Override
    public void show() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((WPrintDialog)WPrintDialogPeer.this.target).setRetVal(WPrintDialogPeer.this._show());
                }
                catch (final Exception ex) {}
                ((WPrintDialog)WPrintDialogPeer.this.target).setVisible(false);
            }
        }).start();
    }
    
    synchronized void setHWnd(final long hwnd) {
        this.hwnd = hwnd;
        for (final WWindowPeer wWindowPeer : this.blockedWindows) {
            if (hwnd != 0L) {
                wWindowPeer.modalDisable((Dialog)this.target, hwnd);
            }
            else {
                wWindowPeer.modalEnable((Dialog)this.target);
            }
        }
    }
    
    synchronized void blockWindow(final WWindowPeer wWindowPeer) {
        this.blockedWindows.add(wWindowPeer);
        if (this.hwnd != 0L) {
            wWindowPeer.modalDisable((Dialog)this.target, this.hwnd);
        }
    }
    
    synchronized void unblockWindow(final WWindowPeer wWindowPeer) {
        this.blockedWindows.remove(wWindowPeer);
        if (this.hwnd != 0L) {
            wWindowPeer.modalEnable((Dialog)this.target);
        }
    }
    
    @Override
    public void blockWindows(final List<Window> list) {
        final Iterator<Window> iterator = list.iterator();
        while (iterator.hasNext()) {
            final WWindowPeer wWindowPeer = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(iterator.next());
            if (wWindowPeer != null) {
                this.blockWindow(wWindowPeer);
            }
        }
    }
    
    @Override
    public native void toFront();
    
    @Override
    public native void toBack();
    
    @Override
    void initialize() {
    }
    
    @Override
    public void updateAlwaysOnTopState() {
    }
    
    @Override
    public void setResizable(final boolean b) {
    }
    
    @Override
    void hide() {
    }
    
    @Override
    void enable() {
    }
    
    @Override
    void disable() {
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
    }
    
    public boolean handleEvent(final Event event) {
        return false;
    }
    
    @Override
    public void setForeground(final Color color) {
    }
    
    @Override
    public void setBackground(final Color color) {
    }
    
    @Override
    public void setFont(final Font font) {
    }
    
    @Override
    public void updateMinimumSize() {
    }
    
    @Override
    public void updateIconImages() {
    }
    
    public boolean requestFocus(final boolean b, final boolean b2) {
        return false;
    }
    
    @Override
    public boolean requestFocus(final Component component, final boolean b, final boolean b2, final long n, final CausedFocusEvent.Cause cause) {
        return false;
    }
    
    @Override
    public void updateFocusableWindowState() {
    }
    
    @Override
    void start() {
    }
    
    @Override
    public void beginValidate() {
    }
    
    @Override
    public void endValidate() {
    }
    
    void invalidate(final int n, final int n2, final int n3, final int n4) {
    }
    
    @Override
    public void addDropTarget(final DropTarget dropTarget) {
    }
    
    @Override
    public void removeDropTarget(final DropTarget dropTarget) {
    }
    
    @Override
    public void setZOrder(final ComponentPeer componentPeer) {
    }
    
    private static native void initIDs();
    
    @Override
    public void applyShape(final Region region) {
    }
    
    @Override
    public void setOpacity(final float n) {
    }
    
    @Override
    public void setOpaque(final boolean b) {
    }
    
    public void updateWindow(final BufferedImage bufferedImage) {
    }
    
    @Override
    public void createScreenSurface(final boolean b) {
    }
    
    @Override
    public void replaceSurfaceData() {
    }
    
    static {
        initIDs();
    }
}
