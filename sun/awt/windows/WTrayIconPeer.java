package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import java.awt.image.DataBufferInt;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.Point;
import java.awt.MenuComponent;
import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.Frame;
import java.awt.peer.TrayIconPeer;

final class WTrayIconPeer extends WObjectPeer implements TrayIconPeer
{
    static final int TRAY_ICON_WIDTH = 16;
    static final int TRAY_ICON_HEIGHT = 16;
    static final int TRAY_ICON_MASK_SIZE = 32;
    IconObserver observer;
    boolean firstUpdate;
    Frame popupParent;
    PopupMenu popup;
    
    @Override
    protected void disposeImpl() {
        if (this.popupParent != null) {
            this.popupParent.dispose();
        }
        this.popupParent.dispose();
        this._dispose();
        WToolkit.targetDisposedPeer(this.target, this);
    }
    
    WTrayIconPeer(final TrayIcon target) {
        this.observer = new IconObserver();
        this.firstUpdate = true;
        this.popupParent = new Frame("PopupMessageWindow");
        this.target = target;
        this.popupParent.addNotify();
        this.create();
        this.updateImage();
    }
    
    @Override
    public void updateImage() {
        final Image image = ((TrayIcon)this.target).getImage();
        if (image != null) {
            this.updateNativeImage(image);
        }
    }
    
    @Override
    public native void setToolTip(final String p0);
    
    @Override
    public synchronized void showPopupMenu(final int n, final int n2) {
        if (this.isDisposed()) {
            return;
        }
        SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
            @Override
            public void run() {
                final PopupMenu popupMenu = ((TrayIcon)WTrayIconPeer.this.target).getPopupMenu();
                if (WTrayIconPeer.this.popup != popupMenu) {
                    if (WTrayIconPeer.this.popup != null) {
                        WTrayIconPeer.this.popupParent.remove(WTrayIconPeer.this.popup);
                    }
                    if (popupMenu != null) {
                        WTrayIconPeer.this.popupParent.add(popupMenu);
                    }
                    WTrayIconPeer.this.popup = popupMenu;
                }
                if (WTrayIconPeer.this.popup != null) {
                    ((WPopupMenuPeer)WTrayIconPeer.this.popup.getPeer()).show(WTrayIconPeer.this.popupParent, new Point(n, n2));
                }
            }
        });
    }
    
    @Override
    public void displayMessage(String s, String s2, final String s3) {
        if (s == null) {
            s = "";
        }
        if (s2 == null) {
            s2 = "";
        }
        this._displayMessage(s, s2, s3);
    }
    
    synchronized void updateNativeImage(final Image image) {
        if (this.isDisposed()) {
            return;
        }
        final boolean imageAutoSize = ((TrayIcon)this.target).isImageAutoSize();
        final BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
        final Graphics2D graphics = bufferedImage.createGraphics();
        if (graphics != null) {
            try {
                graphics.setPaintMode();
                graphics.drawImage(image, 0, 0, imageAutoSize ? 16 : image.getWidth(this.observer), imageAutoSize ? 16 : image.getHeight(this.observer), this.observer);
                this.createNativeImage(bufferedImage);
                this.updateNativeIcon(!this.firstUpdate);
                if (this.firstUpdate) {
                    this.firstUpdate = false;
                }
            }
            finally {
                graphics.dispose();
            }
        }
    }
    
    void createNativeImage(final BufferedImage bufferedImage) {
        final WritableRaster raster = bufferedImage.getRaster();
        final byte[] array = new byte[32];
        final int[] data = ((DataBufferInt)raster.getDataBuffer()).getData();
        final int length = data.length;
        int n = raster.getWidth();
        for (int i = 0; i < length; ++i) {
            final int n2 = i / 8;
            final int n3 = 1 << 7 - i % 8;
            if ((data[i] & 0xFF000000) == 0x0 && n2 < array.length) {
                final byte[] array2 = array;
                final int n4 = n2;
                array2[n4] |= (byte)n3;
            }
        }
        if (raster instanceof IntegerComponentRaster) {
            n = ((IntegerComponentRaster)raster).getScanlineStride();
        }
        this.setNativeIcon(((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData(), array, n, raster.getWidth(), raster.getHeight());
    }
    
    void postEvent(final AWTEvent awtEvent) {
        SunToolkit.postEvent(SunToolkit.targetToAppContext(this.target), awtEvent);
    }
    
    native void create();
    
    synchronized native void _dispose();
    
    native void updateNativeIcon(final boolean p0);
    
    native void setNativeIcon(final int[] p0, final byte[] p1, final int p2, final int p3, final int p4);
    
    native void _displayMessage(final String p0, final String p1, final String p2);
    
    class IconObserver implements ImageObserver
    {
        @Override
        public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
            if (image != ((TrayIcon)WTrayIconPeer.this.target).getImage() || WTrayIconPeer.this.isDisposed()) {
                return false;
            }
            if ((n & 0x33) != 0x0) {
                WTrayIconPeer.this.updateNativeImage(image);
            }
            return (n & 0x20) == 0x0;
        }
    }
}
