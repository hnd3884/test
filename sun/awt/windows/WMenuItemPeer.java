package sun.awt.windows;

import java.security.AccessController;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.security.PrivilegedAction;
import java.awt.event.ActionEvent;
import sun.awt.SunToolkit;
import java.awt.AWTEvent;
import java.awt.MenuShortcut;
import java.awt.MenuItem;
import java.awt.Font;
import sun.util.logging.PlatformLogger;
import java.awt.peer.MenuItemPeer;

class WMenuItemPeer extends WObjectPeer implements MenuItemPeer
{
    private static final PlatformLogger log;
    String shortcutLabel;
    protected WMenuPeer parent;
    private final boolean isCheckbox;
    private static Font defaultMenuFont;
    
    private synchronized native void _dispose();
    
    @Override
    protected void disposeImpl() {
        WToolkit.targetDisposedPeer(this.target, this);
        this._dispose();
    }
    
    @Override
    public void setEnabled(final boolean b) {
        this.enable(b);
    }
    
    public void enable() {
        this.enable(true);
    }
    
    public void disable() {
        this.enable(false);
    }
    
    private void readShortcutLabel() {
        WMenuPeer wMenuPeer;
        for (wMenuPeer = this.parent; wMenuPeer != null && !(wMenuPeer instanceof WMenuBarPeer); wMenuPeer = wMenuPeer.parent) {}
        if (wMenuPeer instanceof WMenuBarPeer) {
            final MenuShortcut shortcut = ((MenuItem)this.target).getShortcut();
            this.shortcutLabel = ((shortcut != null) ? shortcut.toString() : null);
        }
        else {
            this.shortcutLabel = null;
        }
    }
    
    @Override
    public void setLabel(final String s) {
        this.readShortcutLabel();
        this._setLabel(s);
    }
    
    public native void _setLabel(final String p0);
    
    protected WMenuItemPeer() {
        this.isCheckbox = false;
    }
    
    WMenuItemPeer(final MenuItem menuItem) {
        this(menuItem, false);
    }
    
    WMenuItemPeer(final MenuItem target, final boolean isCheckbox) {
        this.target = target;
        this.parent = (WMenuPeer)WToolkit.targetToPeer(target.getParent());
        this.isCheckbox = isCheckbox;
        this.parent.addChildPeer(this);
        this.create(this.parent);
        this.checkMenuCreation();
        this.readShortcutLabel();
    }
    
    void checkMenuCreation() {
        if (this.pData != 0L) {
            return;
        }
        if (this.createError != null) {
            throw this.createError;
        }
        throw new InternalError("couldn't create menu peer");
    }
    
    void postEvent(final AWTEvent awtEvent) {
        SunToolkit.postEvent(SunToolkit.targetToAppContext(this.target), awtEvent);
    }
    
    native void create(final WMenuPeer p0);
    
    native void enable(final boolean p0);
    
    void handleAction(final long n, final int n2) {
        SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
            @Override
            public void run() {
                WMenuItemPeer.this.postEvent(new ActionEvent(WMenuItemPeer.this.target, 1001, ((MenuItem)WMenuItemPeer.this.target).getActionCommand(), n, n2));
            }
        });
    }
    
    static Font getDefaultFont() {
        return WMenuItemPeer.defaultMenuFont;
    }
    
    private static native void initIDs();
    
    private native void _setFont(final Font p0);
    
    @Override
    public void setFont(final Font font) {
        this._setFont(font);
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.WMenuItemPeer");
        initIDs();
        WMenuItemPeer.defaultMenuFont = AccessController.doPrivileged((PrivilegedAction<Font>)new PrivilegedAction<Font>() {
            @Override
            public Font run() {
                try {
                    return Font.decode(ResourceBundle.getBundle("sun.awt.windows.awtLocalization").getString("menuFont"));
                }
                catch (final MissingResourceException ex) {
                    if (WMenuItemPeer.log.isLoggable(PlatformLogger.Level.FINE)) {
                        WMenuItemPeer.log.fine("WMenuItemPeer: " + ex.getMessage() + ". Using default MenuItem font.", ex);
                    }
                    return new Font("SanSerif", 0, 11);
                }
            }
        });
    }
}
