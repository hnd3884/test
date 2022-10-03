package sun.awt;

import java.awt.Color;
import java.awt.peer.ComponentPeer;
import java.awt.Point;
import java.awt.GraphicsConfiguration;
import java.awt.geom.Point2D;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Rectangle;
import java.security.AccessControlContext;
import java.awt.peer.MenuComponentPeer;
import java.awt.Font;
import java.awt.MenuContainer;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.awt.CheckboxMenuItem;
import java.awt.Cursor;
import java.awt.MenuBar;
import java.awt.MenuShortcut;
import java.awt.MenuItem;
import java.util.Vector;
import java.awt.Menu;
import java.awt.SystemTray;
import java.awt.AWTException;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;
import java.awt.DefaultKeyboardFocusManager;
import java.util.ResourceBundle;
import java.awt.event.InvocationEvent;
import javax.accessibility.AccessibleContext;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.ScrollPaneAdjustable;
import java.awt.FileDialog;
import java.awt.PopupMenu;
import java.awt.EventQueue;
import java.awt.MenuComponent;
import java.awt.KeyboardFocusManager;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.AWTEvent;
import java.awt.Window;
import java.awt.Container;
import java.awt.Component;
import sun.misc.Unsafe;

public final class AWTAccessor
{
    private static final Unsafe unsafe;
    private static ComponentAccessor componentAccessor;
    private static ContainerAccessor containerAccessor;
    private static WindowAccessor windowAccessor;
    private static AWTEventAccessor awtEventAccessor;
    private static InputEventAccessor inputEventAccessor;
    private static MouseEventAccessor mouseEventAccessor;
    private static FrameAccessor frameAccessor;
    private static KeyboardFocusManagerAccessor kfmAccessor;
    private static MenuComponentAccessor menuComponentAccessor;
    private static EventQueueAccessor eventQueueAccessor;
    private static PopupMenuAccessor popupMenuAccessor;
    private static FileDialogAccessor fileDialogAccessor;
    private static ScrollPaneAdjustableAccessor scrollPaneAdjustableAccessor;
    private static CheckboxMenuItemAccessor checkboxMenuItemAccessor;
    private static CursorAccessor cursorAccessor;
    private static MenuBarAccessor menuBarAccessor;
    private static MenuItemAccessor menuItemAccessor;
    private static MenuAccessor menuAccessor;
    private static KeyEventAccessor keyEventAccessor;
    private static ClientPropertyKeyAccessor clientPropertyKeyAccessor;
    private static SystemTrayAccessor systemTrayAccessor;
    private static TrayIconAccessor trayIconAccessor;
    private static DefaultKeyboardFocusManagerAccessor defaultKeyboardFocusManagerAccessor;
    private static SequencedEventAccessor sequencedEventAccessor;
    private static ToolkitAccessor toolkitAccessor;
    private static InvocationEventAccessor invocationEventAccessor;
    private static SystemColorAccessor systemColorAccessor;
    private static AccessibleContextAccessor accessibleContextAccessor;
    
    private AWTAccessor() {
    }
    
    public static void setComponentAccessor(final ComponentAccessor componentAccessor) {
        AWTAccessor.componentAccessor = componentAccessor;
    }
    
    public static ComponentAccessor getComponentAccessor() {
        if (AWTAccessor.componentAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(Component.class);
        }
        return AWTAccessor.componentAccessor;
    }
    
    public static void setContainerAccessor(final ContainerAccessor containerAccessor) {
        AWTAccessor.containerAccessor = containerAccessor;
    }
    
    public static ContainerAccessor getContainerAccessor() {
        if (AWTAccessor.containerAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(Container.class);
        }
        return AWTAccessor.containerAccessor;
    }
    
    public static void setWindowAccessor(final WindowAccessor windowAccessor) {
        AWTAccessor.windowAccessor = windowAccessor;
    }
    
    public static WindowAccessor getWindowAccessor() {
        if (AWTAccessor.windowAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(Window.class);
        }
        return AWTAccessor.windowAccessor;
    }
    
    public static void setAWTEventAccessor(final AWTEventAccessor awtEventAccessor) {
        AWTAccessor.awtEventAccessor = awtEventAccessor;
    }
    
    public static AWTEventAccessor getAWTEventAccessor() {
        if (AWTAccessor.awtEventAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(AWTEvent.class);
        }
        return AWTAccessor.awtEventAccessor;
    }
    
    public static void setInputEventAccessor(final InputEventAccessor inputEventAccessor) {
        AWTAccessor.inputEventAccessor = inputEventAccessor;
    }
    
    public static InputEventAccessor getInputEventAccessor() {
        if (AWTAccessor.inputEventAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(InputEvent.class);
        }
        return AWTAccessor.inputEventAccessor;
    }
    
    public static void setMouseEventAccessor(final MouseEventAccessor mouseEventAccessor) {
        AWTAccessor.mouseEventAccessor = mouseEventAccessor;
    }
    
    public static MouseEventAccessor getMouseEventAccessor() {
        if (AWTAccessor.mouseEventAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(MouseEvent.class);
        }
        return AWTAccessor.mouseEventAccessor;
    }
    
    public static void setFrameAccessor(final FrameAccessor frameAccessor) {
        AWTAccessor.frameAccessor = frameAccessor;
    }
    
    public static FrameAccessor getFrameAccessor() {
        if (AWTAccessor.frameAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(Frame.class);
        }
        return AWTAccessor.frameAccessor;
    }
    
    public static void setKeyboardFocusManagerAccessor(final KeyboardFocusManagerAccessor kfmAccessor) {
        AWTAccessor.kfmAccessor = kfmAccessor;
    }
    
    public static KeyboardFocusManagerAccessor getKeyboardFocusManagerAccessor() {
        if (AWTAccessor.kfmAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(KeyboardFocusManager.class);
        }
        return AWTAccessor.kfmAccessor;
    }
    
    public static void setMenuComponentAccessor(final MenuComponentAccessor menuComponentAccessor) {
        AWTAccessor.menuComponentAccessor = menuComponentAccessor;
    }
    
    public static MenuComponentAccessor getMenuComponentAccessor() {
        if (AWTAccessor.menuComponentAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(MenuComponent.class);
        }
        return AWTAccessor.menuComponentAccessor;
    }
    
    public static void setEventQueueAccessor(final EventQueueAccessor eventQueueAccessor) {
        AWTAccessor.eventQueueAccessor = eventQueueAccessor;
    }
    
    public static EventQueueAccessor getEventQueueAccessor() {
        if (AWTAccessor.eventQueueAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(EventQueue.class);
        }
        return AWTAccessor.eventQueueAccessor;
    }
    
    public static void setPopupMenuAccessor(final PopupMenuAccessor popupMenuAccessor) {
        AWTAccessor.popupMenuAccessor = popupMenuAccessor;
    }
    
    public static PopupMenuAccessor getPopupMenuAccessor() {
        if (AWTAccessor.popupMenuAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(PopupMenu.class);
        }
        return AWTAccessor.popupMenuAccessor;
    }
    
    public static void setFileDialogAccessor(final FileDialogAccessor fileDialogAccessor) {
        AWTAccessor.fileDialogAccessor = fileDialogAccessor;
    }
    
    public static FileDialogAccessor getFileDialogAccessor() {
        if (AWTAccessor.fileDialogAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(FileDialog.class);
        }
        return AWTAccessor.fileDialogAccessor;
    }
    
    public static void setScrollPaneAdjustableAccessor(final ScrollPaneAdjustableAccessor scrollPaneAdjustableAccessor) {
        AWTAccessor.scrollPaneAdjustableAccessor = scrollPaneAdjustableAccessor;
    }
    
    public static ScrollPaneAdjustableAccessor getScrollPaneAdjustableAccessor() {
        if (AWTAccessor.scrollPaneAdjustableAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(ScrollPaneAdjustable.class);
        }
        return AWTAccessor.scrollPaneAdjustableAccessor;
    }
    
    public static void setCheckboxMenuItemAccessor(final CheckboxMenuItemAccessor checkboxMenuItemAccessor) {
        AWTAccessor.checkboxMenuItemAccessor = checkboxMenuItemAccessor;
    }
    
    public static CheckboxMenuItemAccessor getCheckboxMenuItemAccessor() {
        if (AWTAccessor.checkboxMenuItemAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(CheckboxMenuItemAccessor.class);
        }
        return AWTAccessor.checkboxMenuItemAccessor;
    }
    
    public static void setCursorAccessor(final CursorAccessor cursorAccessor) {
        AWTAccessor.cursorAccessor = cursorAccessor;
    }
    
    public static CursorAccessor getCursorAccessor() {
        if (AWTAccessor.cursorAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(CursorAccessor.class);
        }
        return AWTAccessor.cursorAccessor;
    }
    
    public static void setMenuBarAccessor(final MenuBarAccessor menuBarAccessor) {
        AWTAccessor.menuBarAccessor = menuBarAccessor;
    }
    
    public static MenuBarAccessor getMenuBarAccessor() {
        if (AWTAccessor.menuBarAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(MenuBarAccessor.class);
        }
        return AWTAccessor.menuBarAccessor;
    }
    
    public static void setMenuItemAccessor(final MenuItemAccessor menuItemAccessor) {
        AWTAccessor.menuItemAccessor = menuItemAccessor;
    }
    
    public static MenuItemAccessor getMenuItemAccessor() {
        if (AWTAccessor.menuItemAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(MenuItemAccessor.class);
        }
        return AWTAccessor.menuItemAccessor;
    }
    
    public static void setMenuAccessor(final MenuAccessor menuAccessor) {
        AWTAccessor.menuAccessor = menuAccessor;
    }
    
    public static MenuAccessor getMenuAccessor() {
        if (AWTAccessor.menuAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(MenuAccessor.class);
        }
        return AWTAccessor.menuAccessor;
    }
    
    public static void setKeyEventAccessor(final KeyEventAccessor keyEventAccessor) {
        AWTAccessor.keyEventAccessor = keyEventAccessor;
    }
    
    public static KeyEventAccessor getKeyEventAccessor() {
        if (AWTAccessor.keyEventAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(KeyEventAccessor.class);
        }
        return AWTAccessor.keyEventAccessor;
    }
    
    public static void setClientPropertyKeyAccessor(final ClientPropertyKeyAccessor clientPropertyKeyAccessor) {
        AWTAccessor.clientPropertyKeyAccessor = clientPropertyKeyAccessor;
    }
    
    public static ClientPropertyKeyAccessor getClientPropertyKeyAccessor() {
        if (AWTAccessor.clientPropertyKeyAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(ClientPropertyKeyAccessor.class);
        }
        return AWTAccessor.clientPropertyKeyAccessor;
    }
    
    public static void setSystemTrayAccessor(final SystemTrayAccessor systemTrayAccessor) {
        AWTAccessor.systemTrayAccessor = systemTrayAccessor;
    }
    
    public static SystemTrayAccessor getSystemTrayAccessor() {
        if (AWTAccessor.systemTrayAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(SystemTrayAccessor.class);
        }
        return AWTAccessor.systemTrayAccessor;
    }
    
    public static void setTrayIconAccessor(final TrayIconAccessor trayIconAccessor) {
        AWTAccessor.trayIconAccessor = trayIconAccessor;
    }
    
    public static TrayIconAccessor getTrayIconAccessor() {
        if (AWTAccessor.trayIconAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(TrayIconAccessor.class);
        }
        return AWTAccessor.trayIconAccessor;
    }
    
    public static void setDefaultKeyboardFocusManagerAccessor(final DefaultKeyboardFocusManagerAccessor defaultKeyboardFocusManagerAccessor) {
        AWTAccessor.defaultKeyboardFocusManagerAccessor = defaultKeyboardFocusManagerAccessor;
    }
    
    public static DefaultKeyboardFocusManagerAccessor getDefaultKeyboardFocusManagerAccessor() {
        if (AWTAccessor.defaultKeyboardFocusManagerAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(DefaultKeyboardFocusManagerAccessor.class);
        }
        return AWTAccessor.defaultKeyboardFocusManagerAccessor;
    }
    
    public static void setSequencedEventAccessor(final SequencedEventAccessor sequencedEventAccessor) {
        AWTAccessor.sequencedEventAccessor = sequencedEventAccessor;
    }
    
    public static SequencedEventAccessor getSequencedEventAccessor() {
        return AWTAccessor.sequencedEventAccessor;
    }
    
    public static void setToolkitAccessor(final ToolkitAccessor toolkitAccessor) {
        AWTAccessor.toolkitAccessor = toolkitAccessor;
    }
    
    public static ToolkitAccessor getToolkitAccessor() {
        if (AWTAccessor.toolkitAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(Toolkit.class);
        }
        return AWTAccessor.toolkitAccessor;
    }
    
    public static void setInvocationEventAccessor(final InvocationEventAccessor invocationEventAccessor) {
        AWTAccessor.invocationEventAccessor = invocationEventAccessor;
    }
    
    public static InvocationEventAccessor getInvocationEventAccessor() {
        return AWTAccessor.invocationEventAccessor;
    }
    
    public static SystemColorAccessor getSystemColorAccessor() {
        if (AWTAccessor.systemColorAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(SystemColor.class);
        }
        return AWTAccessor.systemColorAccessor;
    }
    
    public static void setSystemColorAccessor(final SystemColorAccessor systemColorAccessor) {
        AWTAccessor.systemColorAccessor = systemColorAccessor;
    }
    
    public static AccessibleContextAccessor getAccessibleContextAccessor() {
        if (AWTAccessor.accessibleContextAccessor == null) {
            AWTAccessor.unsafe.ensureClassInitialized(AccessibleContext.class);
        }
        return AWTAccessor.accessibleContextAccessor;
    }
    
    public static void setAccessibleContextAccessor(final AccessibleContextAccessor accessibleContextAccessor) {
        AWTAccessor.accessibleContextAccessor = accessibleContextAccessor;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
    
    public interface AccessibleContextAccessor
    {
        void setAppContext(final AccessibleContext p0, final AppContext p1);
        
        AppContext getAppContext(final AccessibleContext p0);
    }
    
    public interface SystemColorAccessor
    {
        void updateSystemColors();
    }
    
    public interface InvocationEventAccessor
    {
        void dispose(final InvocationEvent p0);
    }
    
    public interface ToolkitAccessor
    {
        void setPlatformResources(final ResourceBundle p0);
    }
    
    public interface SequencedEventAccessor
    {
        AWTEvent getNested(final AWTEvent p0);
        
        boolean isSequencedEvent(final AWTEvent p0);
    }
    
    public interface DefaultKeyboardFocusManagerAccessor
    {
        void consumeNextKeyTyped(final DefaultKeyboardFocusManager p0, final KeyEvent p1);
    }
    
    public interface TrayIconAccessor
    {
        void addNotify(final TrayIcon p0) throws AWTException;
        
        void removeNotify(final TrayIcon p0);
    }
    
    public interface SystemTrayAccessor
    {
        void firePropertyChange(final SystemTray p0, final String p1, final Object p2, final Object p3);
    }
    
    public interface ClientPropertyKeyAccessor
    {
        Object getJComponent_TRANSFER_HANDLER();
    }
    
    public interface KeyEventAccessor
    {
        void setRawCode(final KeyEvent p0, final long p1);
        
        void setPrimaryLevelUnicode(final KeyEvent p0, final long p1);
        
        void setExtendedKeyCode(final KeyEvent p0, final long p1);
        
        Component getOriginalSource(final KeyEvent p0);
    }
    
    public interface MenuAccessor
    {
        Vector getItems(final Menu p0);
    }
    
    public interface MenuItemAccessor
    {
        boolean isEnabled(final MenuItem p0);
        
        String getActionCommandImpl(final MenuItem p0);
        
        boolean isItemEnabled(final MenuItem p0);
        
        String getLabel(final MenuItem p0);
        
        MenuShortcut getShortcut(final MenuItem p0);
    }
    
    public interface MenuBarAccessor
    {
        Menu getHelpMenu(final MenuBar p0);
        
        Vector getMenus(final MenuBar p0);
    }
    
    public interface CursorAccessor
    {
        long getPData(final Cursor p0);
        
        void setPData(final Cursor p0, final long p1);
        
        int getType(final Cursor p0);
    }
    
    public interface CheckboxMenuItemAccessor
    {
        boolean getState(final CheckboxMenuItem p0);
    }
    
    public interface ScrollPaneAdjustableAccessor
    {
        void setTypedValue(final ScrollPaneAdjustable p0, final int p1, final int p2);
    }
    
    public interface FileDialogAccessor
    {
        void setFiles(final FileDialog p0, final File[] p1);
        
        void setFile(final FileDialog p0, final String p1);
        
        void setDirectory(final FileDialog p0, final String p1);
        
        boolean isMultipleMode(final FileDialog p0);
    }
    
    public interface PopupMenuAccessor
    {
        boolean isTrayIconPopup(final PopupMenu p0);
    }
    
    public interface EventQueueAccessor
    {
        Thread getDispatchThread(final EventQueue p0);
        
        boolean isDispatchThreadImpl(final EventQueue p0);
        
        void removeSourceEvents(final EventQueue p0, final Object p1, final boolean p2);
        
        boolean noEvents(final EventQueue p0);
        
        void wakeup(final EventQueue p0, final boolean p1);
        
        void invokeAndWait(final Object p0, final Runnable p1) throws InterruptedException, InvocationTargetException;
        
        void setFwDispatcher(final EventQueue p0, final FwDispatcher p1);
        
        long getMostRecentEventTime(final EventQueue p0);
    }
    
    public interface MenuComponentAccessor
    {
        AppContext getAppContext(final MenuComponent p0);
        
        void setAppContext(final MenuComponent p0, final AppContext p1);
        
        MenuContainer getParent(final MenuComponent p0);
        
        Font getFont_NoClientCode(final MenuComponent p0);
        
         <T extends MenuComponentPeer> T getPeer(final MenuComponent p0);
    }
    
    public interface KeyboardFocusManagerAccessor
    {
        int shouldNativelyFocusHeavyweight(final Component p0, final Component p1, final boolean p2, final boolean p3, final long p4, final CausedFocusEvent.Cause p5);
        
        boolean processSynchronousLightweightTransfer(final Component p0, final Component p1, final boolean p2, final boolean p3, final long p4);
        
        void removeLastFocusRequest(final Component p0);
        
        void setMostRecentFocusOwner(final Window p0, final Component p1);
        
        KeyboardFocusManager getCurrentKeyboardFocusManager(final AppContext p0);
        
        Container getCurrentFocusCycleRoot();
    }
    
    public interface AWTEventAccessor
    {
        void setPosted(final AWTEvent p0);
        
        void setSystemGenerated(final AWTEvent p0);
        
        boolean isSystemGenerated(final AWTEvent p0);
        
        AccessControlContext getAccessControlContext(final AWTEvent p0);
        
        byte[] getBData(final AWTEvent p0);
        
        void setBData(final AWTEvent p0, final byte[] p1);
    }
    
    public interface FrameAccessor
    {
        void setExtendedState(final Frame p0, final int p1);
        
        int getExtendedState(final Frame p0);
        
        Rectangle getMaximizedBounds(final Frame p0);
    }
    
    public interface MouseEventAccessor
    {
        boolean isCausedByTouchEvent(final MouseEvent p0);
        
        void setCausedByTouchEvent(final MouseEvent p0, final boolean p1);
    }
    
    public interface InputEventAccessor
    {
        int[] getButtonDownMasks();
    }
    
    public interface WindowAccessor
    {
        float getOpacity(final Window p0);
        
        void setOpacity(final Window p0, final float p1);
        
        Shape getShape(final Window p0);
        
        void setShape(final Window p0, final Shape p1);
        
        void setOpaque(final Window p0, final boolean p1);
        
        void updateWindow(final Window p0);
        
        Dimension getSecurityWarningSize(final Window p0);
        
        void setSecurityWarningSize(final Window p0, final int p1, final int p2);
        
        void setSecurityWarningPosition(final Window p0, final Point2D p1, final float p2, final float p3);
        
        Point2D calculateSecurityWarningPosition(final Window p0, final double p1, final double p2, final double p3, final double p4);
        
        void setLWRequestStatus(final Window p0, final boolean p1);
        
        boolean isAutoRequestFocus(final Window p0);
        
        boolean isTrayIconWindow(final Window p0);
        
        void setTrayIconWindow(final Window p0, final boolean p1);
        
        Window[] getOwnedWindows(final Window p0);
    }
    
    public interface ContainerAccessor
    {
        void validateUnconditionally(final Container p0);
        
        Component findComponentAt(final Container p0, final int p1, final int p2, final boolean p3);
    }
    
    public interface ComponentAccessor
    {
        void setBackgroundEraseDisabled(final Component p0, final boolean p1);
        
        boolean getBackgroundEraseDisabled(final Component p0);
        
        Rectangle getBounds(final Component p0);
        
        void setMixingCutoutShape(final Component p0, final Shape p1);
        
        void setGraphicsConfiguration(final Component p0, final GraphicsConfiguration p1);
        
        boolean requestFocus(final Component p0, final CausedFocusEvent.Cause p1);
        
        boolean canBeFocusOwner(final Component p0);
        
        boolean isVisible(final Component p0);
        
        void setRequestFocusController(final RequestFocusController p0);
        
        AppContext getAppContext(final Component p0);
        
        void setAppContext(final Component p0, final AppContext p1);
        
        Container getParent(final Component p0);
        
        void setParent(final Component p0, final Container p1);
        
        void setSize(final Component p0, final int p1, final int p2);
        
        Point getLocation(final Component p0);
        
        void setLocation(final Component p0, final int p1, final int p2);
        
        boolean isEnabled(final Component p0);
        
        boolean isDisplayable(final Component p0);
        
        Cursor getCursor(final Component p0);
        
        ComponentPeer getPeer(final Component p0);
        
        void setPeer(final Component p0, final ComponentPeer p1);
        
        boolean isLightweight(final Component p0);
        
        boolean getIgnoreRepaint(final Component p0);
        
        int getWidth(final Component p0);
        
        int getHeight(final Component p0);
        
        int getX(final Component p0);
        
        int getY(final Component p0);
        
        Color getForeground(final Component p0);
        
        Color getBackground(final Component p0);
        
        void setBackground(final Component p0, final Color p1);
        
        Font getFont(final Component p0);
        
        void processEvent(final Component p0, final AWTEvent p1);
        
        AccessControlContext getAccessControlContext(final Component p0);
        
        void revalidateSynchronously(final Component p0);
    }
}
