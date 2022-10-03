package sun.awt.windows;

import java.security.AccessController;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.security.PrivilegedAction;
import java.awt.image.BufferedImage;
import sun.java2d.pipe.Region;
import java.awt.peer.ComponentPeer;
import java.awt.dnd.DropTarget;
import sun.awt.CausedFocusEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.Event;
import java.awt.Component;
import java.util.List;
import sun.awt.SunToolkit;
import sun.awt.AWTAccessor;
import java.util.Iterator;
import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import java.awt.FileDialog;
import java.util.Vector;
import java.io.FilenameFilter;
import java.awt.peer.FileDialogPeer;

final class WFileDialogPeer extends WWindowPeer implements FileDialogPeer
{
    private WComponentPeer parent;
    private FilenameFilter fileFilter;
    private Vector<WWindowPeer> blockedWindows;
    
    private static native void setFilterString(final String p0);
    
    @Override
    public void setFilenameFilter(final FilenameFilter fileFilter) {
        this.fileFilter = fileFilter;
    }
    
    boolean checkFilenameFilter(final String s) {
        final FileDialog fileDialog = (FileDialog)this.target;
        if (this.fileFilter == null) {
            return true;
        }
        final File file = new File(s);
        return this.fileFilter.accept(new File(file.getParent()), file.getName());
    }
    
    WFileDialogPeer(final FileDialog fileDialog) {
        super(fileDialog);
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
    void initialize() {
        this.setFilenameFilter(((FileDialog)this.target).getFilenameFilter());
    }
    
    private native void _dispose();
    
    @Override
    protected void disposeImpl() {
        WToolkit.targetDisposedPeer(this.target, this);
        this._dispose();
    }
    
    private native void _show();
    
    private native void _hide();
    
    @Override
    public void show() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WFileDialogPeer.this._show();
            }
        }).start();
    }
    
    @Override
    void hide() {
        this._hide();
    }
    
    void setHWnd(final long hwnd) {
        if (this.hwnd == hwnd) {
            return;
        }
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
    
    void handleSelected(final char[] array) {
        final String[] split = new String(array).split("\u0000");
        String s;
        File[] array2;
        String substring;
        if (split.length > 1) {
            s = split[0];
            final int n = split.length - 1;
            array2 = new File[n];
            for (int i = 0; i < n; ++i) {
                array2[i] = new File(s, split[i + 1]);
            }
            substring = split[1];
        }
        else {
            final int lastIndex = split[0].lastIndexOf(File.separatorChar);
            if (lastIndex == -1) {
                s = "." + File.separator;
                substring = split[0];
            }
            else {
                s = split[0].substring(0, lastIndex + 1);
                substring = split[0].substring(lastIndex + 1);
            }
            array2 = new File[] { new File(s, substring) };
        }
        final FileDialog fileDialog = (FileDialog)this.target;
        final AWTAccessor.FileDialogAccessor fileDialogAccessor = AWTAccessor.getFileDialogAccessor();
        fileDialogAccessor.setDirectory(fileDialog, s);
        fileDialogAccessor.setFile(fileDialog, substring);
        fileDialogAccessor.setFiles(fileDialog, array2);
        SunToolkit.executeOnEventHandlerThread(fileDialog, new Runnable() {
            @Override
            public void run() {
                fileDialog.setVisible(false);
            }
        });
    }
    
    void handleCancel() {
        final FileDialog fileDialog = (FileDialog)this.target;
        AWTAccessor.getFileDialogAccessor().setFile(fileDialog, null);
        AWTAccessor.getFileDialogAccessor().setFiles(fileDialog, null);
        AWTAccessor.getFileDialogAccessor().setDirectory(fileDialog, null);
        SunToolkit.executeOnEventHandlerThread(fileDialog, new Runnable() {
            @Override
            public void run() {
                fileDialog.setVisible(false);
            }
        });
    }
    
    void blockWindow(final WWindowPeer wWindowPeer) {
        this.blockedWindows.add(wWindowPeer);
        if (this.hwnd != 0L) {
            wWindowPeer.modalDisable((Dialog)this.target, this.hwnd);
        }
    }
    
    void unblockWindow(final WWindowPeer wWindowPeer) {
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
    public void updateAlwaysOnTopState() {
    }
    
    @Override
    public void setDirectory(final String s) {
    }
    
    @Override
    public void setFile(final String s) {
    }
    
    @Override
    public void setTitle(final String s) {
    }
    
    @Override
    public void setResizable(final boolean b) {
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
    public void updateFocusableWindowState() {
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
    
    public boolean isMultipleMode() {
        return AWTAccessor.getFileDialogAccessor().isMultipleMode((FileDialog)this.target);
    }
    
    static {
        initIDs();
        setFilterString(AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    return ResourceBundle.getBundle("sun.awt.windows.awtLocalization").getString("allFiles");
                }
                catch (final MissingResourceException ex) {
                    return "All Files";
                }
            }
        }));
    }
}
