package com.sun.java.accessibility;

import java.awt.AWTEvent;
import java.awt.event.InvocationEvent;
import sun.awt.AWTAccessor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.MenuElement;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import java.awt.KeyboardFocusManager;
import javax.swing.event.ChangeEvent;
import sun.awt.SunToolkit;
import sun.awt.AppContext;
import com.sun.java.accessibility.util.AccessibilityEventMonitor;
import com.sun.java.accessibility.util.SwingEventMonitor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.MouseListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.MenuListener;
import javax.swing.event.CaretListener;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.awt.EventQueue;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.MenuEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.CaretEvent;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleExtendedTable;
import javax.swing.JTree;
import javax.accessibility.AccessibleEditableText;
import java.util.StringTokenizer;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleExtendedComponent;
import javax.swing.KeyStroke;
import javax.swing.text.TabSet;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleAction;
import java.awt.Rectangle;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleText;
import com.sun.java.accessibility.util.Translator;
import com.sun.java.accessibility.util.AWTEventMonitor;
import java.awt.Point;
import javax.accessibility.AccessibleComponent;
import java.util.concurrent.Callable;
import javax.accessibility.Accessible;
import java.awt.Button;
import java.lang.reflect.InvocationTargetException;
import java.awt.Component;
import java.lang.reflect.Field;
import java.util.Locale;
import javax.swing.event.ChangeListener;
import javax.swing.MenuSelectionManager;
import com.sun.java.accessibility.util.EventQueueMonitor;
import java.util.WeakHashMap;
import javax.accessibility.AccessibleHyperlink;
import javax.accessibility.AccessibleHypertext;
import java.util.Map;
import javax.accessibility.AccessibleTable;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.accessibility.AccessibleRole;
import java.util.concurrent.ConcurrentHashMap;
import jdk.Exported;

@Exported(false)
public final class AccessBridge extends AccessBridgeLoader
{
    private final String AccessBridgeVersion = "AccessBridge 2.0.4";
    private static AccessBridge theAccessBridge;
    private ObjectReferences references;
    private EventHandler eventHandler;
    private boolean runningOnJDK1_4;
    private boolean runningOnJDK1_5;
    private ConcurrentHashMap<String, AccessibleRole> accessibleRoleMap;
    private ArrayList<AccessibleRole> extendedVirtualNameSearchRoles;
    private ArrayList<AccessibleRole> noExtendedVirtualNameSearchParentRoles;
    private Method javaGetComponentFromNativeWindowHandleMethod;
    private Method javaGetNativeWindowHandleFromComponentMethod;
    Toolkit toolkit;
    private static ConcurrentHashMap<Integer, AccessibleContext> windowHandleToContextMap;
    private static ConcurrentHashMap<AccessibleContext, Integer> contextToWindowHandleMap;
    private static Vector<NativeWindowHandler> nativeWindowHandlers;
    ConcurrentHashMap<AccessibleTable, AccessibleContext> hashtab;
    private Map<AccessibleHypertext, AccessibleContext> hyperTextContextMap;
    private Map<AccessibleHyperlink, AccessibleContext> hyperLinkContextMap;
    private int _visibleChildrenCount;
    private AccessibleContext _visibleChild;
    private int _currentVisibleIndex;
    private boolean _foundVisibleChild;
    private static final long PROPERTY_CHANGE_EVENTS = 1L;
    private static final long FOCUS_GAINED_EVENTS = 2L;
    private static final long FOCUS_LOST_EVENTS = 4L;
    private static final long FOCUS_EVENTS = 6L;
    private static final long CARET_UPATE_EVENTS = 8L;
    private static final long CARET_EVENTS = 8L;
    private static final long MOUSE_CLICKED_EVENTS = 16L;
    private static final long MOUSE_ENTERED_EVENTS = 32L;
    private static final long MOUSE_EXITED_EVENTS = 64L;
    private static final long MOUSE_PRESSED_EVENTS = 128L;
    private static final long MOUSE_RELEASED_EVENTS = 256L;
    private static final long MOUSE_EVENTS = 496L;
    private static final long MENU_CANCELED_EVENTS = 512L;
    private static final long MENU_DESELECTED_EVENTS = 1024L;
    private static final long MENU_SELECTED_EVENTS = 2048L;
    private static final long MENU_EVENTS = 3584L;
    private static final long POPUPMENU_CANCELED_EVENTS = 4096L;
    private static final long POPUPMENU_WILL_BECOME_INVISIBLE_EVENTS = 8192L;
    private static final long POPUPMENU_WILL_BECOME_VISIBLE_EVENTS = 16384L;
    private static final long POPUPMENU_EVENTS = 28672L;
    private static final long PROPERTY_NAME_CHANGE_EVENTS = 1L;
    private static final long PROPERTY_DESCRIPTION_CHANGE_EVENTS = 2L;
    private static final long PROPERTY_STATE_CHANGE_EVENTS = 4L;
    private static final long PROPERTY_VALUE_CHANGE_EVENTS = 8L;
    private static final long PROPERTY_SELECTION_CHANGE_EVENTS = 16L;
    private static final long PROPERTY_TEXT_CHANGE_EVENTS = 32L;
    private static final long PROPERTY_CARET_CHANGE_EVENTS = 64L;
    private static final long PROPERTY_VISIBLEDATA_CHANGE_EVENTS = 128L;
    private static final long PROPERTY_CHILD_CHANGE_EVENTS = 256L;
    private static final long PROPERTY_ACTIVEDESCENDENT_CHANGE_EVENTS = 512L;
    private static final long PROPERTY_EVENTS = 1023L;
    private AccessibleRole[] allAccessibleRoles;
    
    public AccessBridge() {
        this.runningOnJDK1_4 = false;
        this.runningOnJDK1_5 = false;
        this.accessibleRoleMap = new ConcurrentHashMap<String, AccessibleRole>();
        this.extendedVirtualNameSearchRoles = new ArrayList<AccessibleRole>();
        this.noExtendedVirtualNameSearchParentRoles = new ArrayList<AccessibleRole>();
        this.hashtab = new ConcurrentHashMap<AccessibleTable, AccessibleContext>();
        this.hyperTextContextMap = new WeakHashMap<AccessibleHypertext, AccessibleContext>();
        this.hyperLinkContextMap = new WeakHashMap<AccessibleHyperlink, AccessibleContext>();
        this.allAccessibleRoles = new AccessibleRole[] { AccessibleRole.ALERT, AccessibleRole.COLUMN_HEADER, AccessibleRole.CANVAS, AccessibleRole.COMBO_BOX, AccessibleRole.DESKTOP_ICON, AccessibleRole.INTERNAL_FRAME, AccessibleRole.DESKTOP_PANE, AccessibleRole.OPTION_PANE, AccessibleRole.WINDOW, AccessibleRole.FRAME, AccessibleRole.DIALOG, AccessibleRole.COLOR_CHOOSER, AccessibleRole.DIRECTORY_PANE, AccessibleRole.FILE_CHOOSER, AccessibleRole.FILLER, AccessibleRole.ICON, AccessibleRole.LABEL, AccessibleRole.ROOT_PANE, AccessibleRole.GLASS_PANE, AccessibleRole.LAYERED_PANE, AccessibleRole.LIST, AccessibleRole.LIST_ITEM, AccessibleRole.MENU_BAR, AccessibleRole.POPUP_MENU, AccessibleRole.MENU, AccessibleRole.MENU_ITEM, AccessibleRole.SEPARATOR, AccessibleRole.PAGE_TAB_LIST, AccessibleRole.PAGE_TAB, AccessibleRole.PANEL, AccessibleRole.PROGRESS_BAR, AccessibleRole.PASSWORD_TEXT, AccessibleRole.PUSH_BUTTON, AccessibleRole.TOGGLE_BUTTON, AccessibleRole.CHECK_BOX, AccessibleRole.RADIO_BUTTON, AccessibleRole.ROW_HEADER, AccessibleRole.SCROLL_PANE, AccessibleRole.SCROLL_BAR, AccessibleRole.VIEWPORT, AccessibleRole.SLIDER, AccessibleRole.SPLIT_PANE, AccessibleRole.TABLE, AccessibleRole.TEXT, AccessibleRole.TREE, AccessibleRole.TOOL_BAR, AccessibleRole.TOOL_TIP, AccessibleRole.AWT_COMPONENT, AccessibleRole.SWING_COMPONENT, AccessibleRole.UNKNOWN };
        AccessBridge.theAccessBridge = this;
        this.references = new ObjectReferences();
        Runtime.getRuntime().addShutdownHook(new Thread(new shutdownHook()));
        this.initAccessibleRoleMap();
        final String javaVersionProperty = this.getJavaVersionProperty();
        this.debugString("[INFO]:JDK version = " + javaVersionProperty);
        this.runningOnJDK1_4 = (javaVersionProperty.compareTo("1.4") >= 0);
        this.runningOnJDK1_5 = (javaVersionProperty.compareTo("1.5") >= 0);
        if (this.initHWNDcalls()) {
            EventQueueMonitor.isGUIInitialized();
            this.eventHandler = new EventHandler(this);
            if (this.runningOnJDK1_4) {
                MenuSelectionManager.defaultManager().addChangeListener(this.eventHandler);
            }
            addNativeWindowHandler(new DefaultNativeWindowHandler());
            final Thread thread = new Thread(new dllRunner());
            thread.setDaemon(true);
            thread.start();
            this.debugString("[INFO]:AccessBridge started");
        }
    }
    
    private void initAccessibleRoleMap() {
        try {
            final Class<?> forName = Class.forName("javax.accessibility.AccessibleRole");
            if (null != forName) {
                final AccessibleRole unknown = AccessibleRole.UNKNOWN;
                final Field[] fields = forName.getFields();
                for (int i = 0; i < fields.length; ++i) {
                    final Field field = fields[i];
                    if (AccessibleRole.class == field.getType()) {
                        final AccessibleRole accessibleRole = (AccessibleRole)field.get(unknown);
                        this.accessibleRoleMap.put(accessibleRole.toDisplayString(Locale.US), accessibleRole);
                    }
                }
            }
        }
        catch (final Exception ex) {}
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.COMBO_BOX);
        try {
            this.extendedVirtualNameSearchRoles.add(AccessibleRole.DATE_EDITOR);
        }
        catch (final NoSuchFieldError noSuchFieldError) {}
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.LIST);
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.PASSWORD_TEXT);
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.SLIDER);
        try {
            this.extendedVirtualNameSearchRoles.add(AccessibleRole.SPIN_BOX);
        }
        catch (final NoSuchFieldError noSuchFieldError2) {}
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.TABLE);
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.TEXT);
        this.extendedVirtualNameSearchRoles.add(AccessibleRole.UNKNOWN);
        this.noExtendedVirtualNameSearchParentRoles.add(AccessibleRole.TABLE);
        this.noExtendedVirtualNameSearchParentRoles.add(AccessibleRole.TOOL_BAR);
    }
    
    private native void runDLL();
    
    private native void sendDebugString(final String p0);
    
    private void debugString(final String s) {
        this.sendDebugString(s);
    }
    
    private void decrementReference(final Object o) {
        this.references.decrement(o);
    }
    
    private String getJavaVersionProperty() {
        final String property = System.getProperty("java.version");
        if (property != null) {
            this.references.increment(property);
            return property;
        }
        return null;
    }
    
    private String getAccessBridgeVersion() {
        final String s = new String("AccessBridge 2.0.4");
        this.references.increment(s);
        return s;
    }
    
    private native int isJAWTInstalled();
    
    private native int jawtGetNativeWindowHandleFromComponent(final Component p0);
    
    private native Component jawtGetComponentFromNativeWindowHandle(final int p0);
    
    private boolean initHWNDcalls() {
        final Class[] array = { Integer.TYPE };
        final Class[] array2 = { null };
        try {
            array2[0] = Class.forName("java.awt.Component");
        }
        catch (final ClassNotFoundException ex) {
            this.debugString("[ERROR]:Exception: " + ex.toString());
        }
        final Object[] array3 = { null };
        boolean b = false;
        this.toolkit = Toolkit.getDefaultToolkit();
        if (this.useJAWT_DLL) {
            b = true;
        }
        else {
            try {
                this.javaGetComponentFromNativeWindowHandleMethod = this.toolkit.getClass().getMethod("getComponentFromNativeWindowHandle", (Class<?>[])array);
                if (this.javaGetComponentFromNativeWindowHandleMethod != null) {
                    try {
                        array3[0] = new Integer(1);
                        final Component component = (Component)this.javaGetComponentFromNativeWindowHandleMethod.invoke(this.toolkit, array3);
                        b = true;
                    }
                    catch (final InvocationTargetException ex2) {
                        this.debugString("[ERROR]:Exception: " + ex2.toString());
                    }
                    catch (final IllegalAccessException ex3) {
                        this.debugString("[ERROR]:Exception: " + ex3.toString());
                    }
                }
            }
            catch (final NoSuchMethodException ex4) {
                this.debugString("[ERROR]:Exception: " + ex4.toString());
            }
            catch (final SecurityException ex5) {
                this.debugString("[ERROR]:Exception: " + ex5.toString());
            }
            try {
                this.javaGetNativeWindowHandleFromComponentMethod = this.toolkit.getClass().getMethod("getNativeWindowHandleFromComponent", (Class<?>[])array2);
                if (this.javaGetNativeWindowHandleFromComponentMethod != null) {
                    try {
                        array3[0] = new Button("OK");
                        final Integer n = (Integer)this.javaGetNativeWindowHandleFromComponentMethod.invoke(this.toolkit, array3);
                        b = true;
                    }
                    catch (final InvocationTargetException ex6) {
                        this.debugString("[ERROR]:Exception: " + ex6.toString());
                    }
                    catch (final IllegalAccessException ex7) {
                        this.debugString("[ERROR]:Exception: " + ex7.toString());
                    }
                    catch (final Exception ex8) {
                        this.debugString("[ERROR]:Exception: " + ex8.toString());
                    }
                }
            }
            catch (final NoSuchMethodException ex9) {
                this.debugString("[ERROR]:Exception: " + ex9.toString());
            }
            catch (final SecurityException ex10) {
                this.debugString("[ERROR]:Exception: " + ex10.toString());
            }
        }
        return b;
    }
    
    private static void registerVirtualFrame(final Accessible accessible, final Integer n) {
        if (accessible != null) {
            final AccessibleContext accessibleContext = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                @Override
                public AccessibleContext call() throws Exception {
                    return accessible.getAccessibleContext();
                }
            }, accessible);
            AccessBridge.windowHandleToContextMap.put(n, accessibleContext);
            AccessBridge.contextToWindowHandleMap.put(accessibleContext, n);
        }
    }
    
    private static void revokeVirtualFrame(final Accessible accessible, final Integer n) {
        final AccessibleContext accessibleContext = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                return accessible.getAccessibleContext();
            }
        }, accessible);
        AccessBridge.windowHandleToContextMap.remove(n);
        AccessBridge.contextToWindowHandleMap.remove(accessibleContext);
    }
    
    private static void addNativeWindowHandler(final NativeWindowHandler nativeWindowHandler) {
        if (nativeWindowHandler == null) {
            throw new IllegalArgumentException();
        }
        AccessBridge.nativeWindowHandlers.addElement(nativeWindowHandler);
    }
    
    private static boolean removeNativeWindowHandler(final NativeWindowHandler nativeWindowHandler) {
        if (nativeWindowHandler == null) {
            throw new IllegalArgumentException();
        }
        return AccessBridge.nativeWindowHandlers.removeElement(nativeWindowHandler);
    }
    
    private boolean isJavaWindow(final int n) {
        final AccessibleContext contextFromNativeWindowHandle = this.getContextFromNativeWindowHandle(n);
        if (contextFromNativeWindowHandle != null) {
            this.saveContextToWindowHandleMapping(contextFromNativeWindowHandle, n);
            return true;
        }
        return false;
    }
    
    private void saveContextToWindowHandleMapping(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]:saveContextToWindowHandleMapping...");
        if (accessibleContext == null) {
            return;
        }
        if (!AccessBridge.contextToWindowHandleMap.containsKey(accessibleContext)) {
            this.debugString("[INFO]: saveContextToWindowHandleMapping: ac = " + accessibleContext + "; handle = " + n);
            AccessBridge.contextToWindowHandleMap.put(accessibleContext, n);
        }
    }
    
    private AccessibleContext getContextFromNativeWindowHandle(final int n) {
        final AccessibleContext accessibleContext = AccessBridge.windowHandleToContextMap.get(n);
        if (accessibleContext != null) {
            this.saveContextToWindowHandleMapping(accessibleContext, n);
            return accessibleContext;
        }
        for (int size = AccessBridge.nativeWindowHandlers.size(), i = 0; i < size; ++i) {
            final Accessible accessibleFromNativeWindowHandle = AccessBridge.nativeWindowHandlers.elementAt(i).getAccessibleFromNativeWindowHandle(n);
            if (accessibleFromNativeWindowHandle != null) {
                final AccessibleContext accessibleContext2 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return accessibleFromNativeWindowHandle.getAccessibleContext();
                    }
                }, accessibleFromNativeWindowHandle);
                this.saveContextToWindowHandleMapping(accessibleContext2, n);
                return accessibleContext2;
            }
        }
        return null;
    }
    
    private int getNativeWindowHandleFromContext(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getNativeWindowHandleFromContext: ac = " + accessibleContext);
        try {
            return AccessBridge.contextToWindowHandleMap.get(accessibleContext);
        }
        catch (final Exception ex) {
            return 0;
        }
    }
    
    private int getNativeWindowHandleFromComponent(final Component component) {
        if (this.useJAWT_DLL) {
            this.debugString("[INFO]:*** calling jawtGetNativeWindowHandleFromComponent");
            return this.jawtGetNativeWindowHandleFromComponent(component);
        }
        final Object[] array = { null };
        this.debugString("[INFO]:*** calling javaGetNativeWindowHandleFromComponent");
        if (this.javaGetNativeWindowHandleFromComponentMethod != null) {
            try {
                array[0] = component;
                final Integer n = (Integer)this.javaGetNativeWindowHandleFromComponentMethod.invoke(this.toolkit, array);
                AccessBridge.contextToWindowHandleMap.put(InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return component.getAccessibleContext();
                    }
                }, component), n);
                return n;
            }
            catch (final InvocationTargetException ex) {
                this.debugString("[ERROR]:Exception: " + ex.toString());
            }
            catch (final IllegalAccessException ex2) {
                this.debugString("[ERROR]:Exception: " + ex2.toString());
            }
        }
        return -1;
    }
    
    private AccessibleContext getAccessibleContextAt(final int n, final int n2, final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return null;
        }
        if (AccessBridge.windowHandleToContextMap != null && AccessBridge.windowHandleToContextMap.containsValue(this.getRootAccessibleContext(accessibleContext))) {
            return this.getAccessibleContextAt_1(n, n2, accessibleContext);
        }
        return this.getAccessibleContextAt_2(n, n2, accessibleContext);
    }
    
    private AccessibleContext getRootAccessibleContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent == null) {
                    return accessibleContext;
                }
                for (Accessible accessible = accessibleParent.getAccessibleContext().getAccessibleParent(); accessible != null; accessible = accessibleParent.getAccessibleContext().getAccessibleParent()) {
                    accessibleParent = accessible;
                }
                return accessibleParent.getAccessibleContext();
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getAccessibleContextAt_1(final int n, final int n2, final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleContextAt_1 called");
        this.debugString("[INFO]:   -> x = " + n + " y = " + n2 + " parent = " + accessibleContext);
        if (accessibleContext == null) {
            return null;
        }
        final AccessibleComponent accessibleComponent = InvocationUtils.invokeAndWait((Callable<AccessibleComponent>)new Callable<AccessibleComponent>() {
            @Override
            public AccessibleComponent call() throws Exception {
                return accessibleContext.getAccessibleComponent();
            }
        }, accessibleContext);
        if (accessibleComponent != null) {
            final Point point = InvocationUtils.invokeAndWait((Callable<Point>)new Callable<Point>() {
                @Override
                public Point call() throws Exception {
                    return accessibleComponent.getLocation();
                }
            }, accessibleContext);
            final Accessible accessible = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
                @Override
                public Accessible call() throws Exception {
                    return accessibleComponent.getAccessibleAt(new Point(n - point.x, n2 - point.y));
                }
            }, accessibleContext);
            if (accessible != null) {
                final AccessibleContext accessibleContext2 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return accessible.getAccessibleContext();
                    }
                }, accessibleContext);
                if (accessibleContext2 != null) {
                    if (accessibleContext2 != accessibleContext) {
                        return this.getAccessibleContextAt_1(n - point.x, n2 - point.y, accessibleContext2);
                    }
                    return accessibleContext2;
                }
            }
        }
        return accessibleContext;
    }
    
    private AccessibleContext getAccessibleContextAt_2(final int n, final int n2, final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleContextAt_2 called");
        this.debugString("[INFO]:   -> x = " + n + " y = " + n2 + " parent = " + accessibleContext);
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                final Accessible accessible = EventQueueMonitor.getAccessibleAt(new Point(n, n2));
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    if (accessibleContext != null) {
                        AccessBridge.this.debugString("[INFO]:   returning childAC = " + accessibleContext);
                        return accessibleContext;
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getAccessibleContextWithFocus() {
        final Component componentWithFocus = AWTEventMonitor.getComponentWithFocus();
        if (componentWithFocus != null) {
            final Accessible accessible = Translator.getAccessible(componentWithFocus);
            if (accessible != null) {
                final AccessibleContext accessibleContext = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return accessible.getAccessibleContext();
                    }
                }, componentWithFocus);
                if (accessibleContext != null) {
                    return accessibleContext;
                }
            }
        }
        return null;
    }
    
    private String getAccessibleNameFromContext(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: ***** ac = " + accessibleContext.getClass());
        if (accessibleContext == null) {
            this.debugString("[INFO]: getAccessibleNameFromContext; ac = null!");
            return null;
        }
        final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                return accessibleContext.getAccessibleName();
            }
        }, accessibleContext);
        if (s != null) {
            this.references.increment(s);
            this.debugString("[INFO]: Returning AccessibleName from Context: " + s);
            return s;
        }
        return null;
    }
    
    private String getVirtualAccessibleNameFromContext(final AccessibleContext accessibleContext) {
        if (null == accessibleContext) {
            this.debugString("[ERROR]: AccessBridge::getVirtualAccessibleNameFromContext error - ac == null.");
            return null;
        }
        final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                return accessibleContext.getAccessibleName();
            }
        }, accessibleContext);
        if (null != s && 0 != s.length()) {
            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from AccessibleContext::getAccessibleName.");
            this.references.increment(s);
            return s;
        }
        final String s2 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                return accessibleContext.getAccessibleDescription();
            }
        }, accessibleContext);
        if (null != s2 && 0 != s2.length()) {
            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from AccessibleContext::getAccessibleDescription.");
            this.references.increment(s2);
            return s2;
        }
        this.debugString("[WARN]: The Virtual Accessible Name was not found using AccessibleContext::getAccessibleDescription. or getAccessibleName");
        boolean b = false;
        final AccessibleRole accessibleRole = InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
            @Override
            public AccessibleRole call() throws Exception {
                return accessibleContext.getAccessibleRole();
            }
        }, accessibleContext);
        AccessibleContext accessibleContext2 = null;
        AccessibleRole unknown = AccessibleRole.UNKNOWN;
        if (this.extendedVirtualNameSearchRoles.contains(accessibleRole)) {
            accessibleContext2 = this.getAccessibleParentFromContext(accessibleContext);
            if (null != accessibleContext2) {
                unknown = InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                    @Override
                    public AccessibleRole call() throws Exception {
                        return accessibleContext2.getAccessibleRole();
                    }
                }, accessibleContext);
                if (AccessibleRole.UNKNOWN != unknown) {
                    b = true;
                    if (this.noExtendedVirtualNameSearchParentRoles.contains(unknown)) {
                        b = false;
                    }
                }
            }
        }
        if (!b) {
            this.debugString("[INFO]: bk -- getVirtualAccessibleNameFromContext will not use the extended name search algorithm.  role = " + ((accessibleRole != null) ? accessibleRole.toDisplayString(Locale.US) : "null"));
            if (AccessibleRole.LABEL == accessibleRole) {
                final AccessibleText accessibleText = InvocationUtils.invokeAndWait((Callable<AccessibleText>)new Callable<AccessibleText>() {
                    @Override
                    public AccessibleText call() throws Exception {
                        return accessibleContext.getAccessibleText();
                    }
                }, accessibleContext);
                if (null != accessibleText) {
                    final String accessibleTextRangeFromContext = this.getAccessibleTextRangeFromContext(accessibleContext, 0, InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            return accessibleText.getCharCount();
                        }
                    }, accessibleContext));
                    if (null != accessibleTextRangeFromContext) {
                        this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from the Accessible Text of the LABEL object.");
                        this.references.increment(accessibleTextRangeFromContext);
                        return accessibleTextRangeFromContext;
                    }
                }
                this.debugString("[INFO]: bk -- Attempting to obtain the Virtual Accessible Name from the Accessible Icon information.");
                final AccessibleIcon[] array = InvocationUtils.invokeAndWait((Callable<AccessibleIcon[]>)new Callable<AccessibleIcon[]>() {
                    @Override
                    public AccessibleIcon[] call() throws Exception {
                        return accessibleContext.getAccessibleIcon();
                    }
                }, accessibleContext);
                if (null != array && array.length > 0) {
                    final String s3 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return array[0].getAccessibleIconDescription();
                        }
                    }, accessibleContext);
                    if (s3 != null) {
                        this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from the description of the first Accessible Icon found in the LABEL object.");
                        this.references.increment(s3);
                        return s3;
                    }
                }
                else {
                    final AccessibleContext accessibleParentFromContext = this.getAccessibleParentFromContext(accessibleContext);
                    if (null != accessibleParentFromContext && AccessibleRole.TABLE == InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                        @Override
                        public AccessibleRole call() throws Exception {
                            return accessibleParentFromContext.getAccessibleRole();
                        }
                    }, accessibleContext)) {
                        final AccessibleContext accessibleChildFromContext = this.getAccessibleChildFromContext(accessibleParentFromContext, InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                            @Override
                            public Integer call() throws Exception {
                                return accessibleContext.getAccessibleIndexInParent();
                            }
                        }, accessibleContext));
                        this.debugString("[INFO]: bk -- Making a second attempt to obtain the Virtual Accessible Name from the Accessible Icon information for the Table Cell.");
                        if (accessibleChildFromContext != null) {
                            final AccessibleIcon[] array2 = InvocationUtils.invokeAndWait((Callable<AccessibleIcon[]>)new Callable<AccessibleIcon[]>() {
                                @Override
                                public AccessibleIcon[] call() throws Exception {
                                    return accessibleChildFromContext.getAccessibleIcon();
                                }
                            }, accessibleContext);
                            if (null != array2 && array2.length > 0) {
                                final String s4 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                                    @Override
                                    public String call() {
                                        return array2[0].getAccessibleIconDescription();
                                    }
                                }, accessibleContext);
                                if (s4 != null) {
                                    this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from the description of the first Accessible Icon found in the Table Cell object.");
                                    this.references.increment(s4);
                                    return s4;
                                }
                            }
                        }
                    }
                }
            }
            else if (AccessibleRole.TOGGLE_BUTTON == accessibleRole || AccessibleRole.PUSH_BUTTON == accessibleRole) {
                this.debugString("[INFO]: bk -- Attempting to obtain the Virtual Accessible Name from the Accessible Icon information.");
                final AccessibleIcon[] array3 = InvocationUtils.invokeAndWait((Callable<AccessibleIcon[]>)new Callable<AccessibleIcon[]>() {
                    @Override
                    public AccessibleIcon[] call() {
                        return accessibleContext.getAccessibleIcon();
                    }
                }, accessibleContext);
                if (null != array3 && array3.length > 0) {
                    final String s5 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                        @Override
                        public String call() {
                            return array3[0].getAccessibleIconDescription();
                        }
                    }, accessibleContext);
                    if (s5 != null) {
                        this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from the description of the first Accessible Icon found in the TOGGLE_BUTTON or PUSH_BUTTON object.");
                        this.references.increment(s5);
                        return s5;
                    }
                }
            }
            else if (AccessibleRole.CHECK_BOX == accessibleRole) {
                final AccessibleValue accessibleValue = InvocationUtils.invokeAndWait((Callable<AccessibleValue>)new Callable<AccessibleValue>() {
                    @Override
                    public AccessibleValue call() throws Exception {
                        return accessibleContext.getAccessibleValue();
                    }
                }, accessibleContext);
                if (null != accessibleValue) {
                    final Number n = InvocationUtils.invokeAndWait((Callable<Number>)new Callable<Number>() {
                        @Override
                        public Number call() throws Exception {
                            return accessibleValue.getCurrentAccessibleValue();
                        }
                    }, accessibleContext);
                    if (null != n) {
                        String s6;
                        if (1 == n.intValue()) {
                            s6 = Boolean.TRUE.toString();
                        }
                        else if (0 == n.intValue()) {
                            s6 = Boolean.FALSE.toString();
                        }
                        else {
                            s6 = n.toString();
                        }
                        if (null != s6) {
                            this.references.increment(s6);
                            return s6;
                        }
                    }
                }
            }
            return null;
        }
        final AccessibleContext accessibleContext3 = accessibleContext2;
        final String s7 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                return accessibleContext3.getAccessibleName();
            }
        }, accessibleContext);
        final String s8 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                return accessibleContext3.getAccessibleDescription();
            }
        }, accessibleContext);
        if (AccessibleRole.SLIDER == accessibleRole && AccessibleRole.PANEL == unknown && null != s7) {
            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from the Accessible Name of the SLIDER object's parent object.");
            this.references.increment(s7);
            return s7;
        }
        boolean b2 = false;
        AccessibleContext accessibleContext4 = accessibleContext;
        if (AccessibleRole.TEXT == accessibleRole && AccessibleRole.COMBO_BOX == unknown) {
            b2 = true;
            if (null != s7) {
                this.debugString("[INFO]: bk -- The Virtual Accessible Name for this Edit Combo box was obtained from the Accessible Name of the object's parent object.");
                this.references.increment(s7);
                return s7;
            }
            if (null != s8) {
                this.debugString("[INFO]: bk -- The Virtual Accessible Name for this Edit Combo box was obtained from the Accessible Description of the object's parent object.");
                this.references.increment(s8);
                return s8;
            }
            accessibleContext4 = accessibleContext2;
            final AccessibleRole unknown2 = AccessibleRole.UNKNOWN;
            accessibleContext2 = this.getAccessibleParentFromContext(accessibleContext4);
            if (null != accessibleContext2) {
                final AccessibleRole accessibleRole2 = InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                    @Override
                    public AccessibleRole call() throws Exception {
                        return accessibleContext2.getAccessibleRole();
                    }
                }, accessibleContext);
            }
        }
        final String javaVersionProperty = this.getJavaVersionProperty();
        if (null != javaVersionProperty && javaVersionProperty.compareTo("1.3") >= 0) {
            final AccessibleRelationSet set = InvocationUtils.invokeAndWait((Callable<AccessibleRelationSet>)new Callable<AccessibleRelationSet>() {
                @Override
                public AccessibleRelationSet call() throws Exception {
                    return accessibleContext2.getAccessibleRelationSet();
                }
            }, accessibleContext);
            if (set != null && set.size() > 0 && set.contains(AccessibleRelation.LABELED_BY)) {
                final AccessibleRelation value = set.get(AccessibleRelation.LABELED_BY);
                if (value != null) {
                    final Object o = value.getTarget()[0];
                    if (o instanceof Accessible) {
                        final AccessibleContext accessibleContext5 = ((Accessible)o).getAccessibleContext();
                        if (accessibleContext5 != null) {
                            final String accessibleName = accessibleContext5.getAccessibleName();
                            final String accessibleDescription = accessibleContext5.getAccessibleDescription();
                            if (null != accessibleName) {
                                this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained using the LABELED_BY AccessibleRelation -- Name Case.");
                                this.references.increment(accessibleName);
                                return accessibleName;
                            }
                            if (null != accessibleDescription) {
                                this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained using the LABELED_BY AccessibleRelation -- Description Case.");
                                this.references.increment(accessibleDescription);
                                return accessibleDescription;
                            }
                        }
                    }
                }
            }
        }
        else {
            this.debugString("[ERROR]:bk -- This version of Java does not support AccessibleContext::getAccessibleRelationSet.");
        }
        int intValue = 0;
        final int intValue2 = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleContext4.getAccessibleIndexInParent();
            }
        }, accessibleContext);
        if (null != accessibleContext2) {
            intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return accessibleContext2.getAccessibleChildrenCount() - 1;
                }
            }, accessibleContext);
        }
        final int accessibleXcoordFromContext = this.getAccessibleXcoordFromContext(accessibleContext4);
        final int accessibleYcoordFromContext = this.getAccessibleYcoordFromContext(accessibleContext4);
        this.getAccessibleWidthFromContext(accessibleContext4);
        this.getAccessibleHeightFromContext(accessibleContext4);
        final int n2 = accessibleXcoordFromContext + 2;
        final int n3 = accessibleYcoordFromContext + 2;
        for (int i = intValue2 - 1; i >= 0; --i) {
            final Accessible accessible = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
                @Override
                public Accessible call() throws Exception {
                    return accessibleContext2.getAccessibleChild(i);
                }
            }, accessibleContext);
            if (null != accessible) {
                final AccessibleContext accessibleContext6 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return accessible.getAccessibleContext();
                    }
                }, accessibleContext);
                if (null != accessibleContext6 && AccessibleRole.LABEL == InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                    @Override
                    public AccessibleRole call() throws Exception {
                        return accessibleContext6.getAccessibleRole();
                    }
                }, accessibleContext)) {
                    final int accessibleXcoordFromContext2 = this.getAccessibleXcoordFromContext(accessibleContext6);
                    final int accessibleYcoordFromContext2 = this.getAccessibleYcoordFromContext(accessibleContext6);
                    final int accessibleWidthFromContext = this.getAccessibleWidthFromContext(accessibleContext6);
                    final int accessibleHeightFromContext = this.getAccessibleHeightFromContext(accessibleContext6);
                    if (accessibleXcoordFromContext2 < accessibleXcoordFromContext && accessibleYcoordFromContext2 <= n3 && n3 <= accessibleYcoordFromContext2 + accessibleHeightFromContext) {
                        final String s9 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext6.getAccessibleName();
                            }
                        }, accessibleContext);
                        if (null != s9) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Name of a LABEL object positioned to the left of the object.");
                            this.references.increment(s9);
                            return s9;
                        }
                        final String s10 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext6.getAccessibleDescription();
                            }
                        }, accessibleContext);
                        if (null != s10) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Description of a LABEL object positioned to the left of the object.");
                            this.references.increment(s10);
                            return s10;
                        }
                    }
                    else if (accessibleYcoordFromContext2 < n3 && accessibleXcoordFromContext2 <= n2 && n2 <= accessibleXcoordFromContext2 + accessibleWidthFromContext) {
                        final String s11 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext6.getAccessibleName();
                            }
                        }, accessibleContext);
                        if (null != s11) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Name of a LABEL object positioned above the object.");
                            this.references.increment(s11);
                            return s11;
                        }
                        final String s12 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext6.getAccessibleDescription();
                            }
                        }, accessibleContext);
                        if (null != s12) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Description of a LABEL object positioned above the object.");
                            this.references.increment(s12);
                            return s12;
                        }
                    }
                }
            }
        }
        for (int j = intValue2 + 1; j <= intValue; ++j) {
            final Accessible accessible2 = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
                @Override
                public Accessible call() throws Exception {
                    return accessibleContext2.getAccessibleChild(j);
                }
            }, accessibleContext);
            if (null != accessible2) {
                final AccessibleContext accessibleContext7 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return accessible2.getAccessibleContext();
                    }
                }, accessibleContext);
                if (null != accessibleContext7 && AccessibleRole.LABEL == InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                    @Override
                    public AccessibleRole call() throws Exception {
                        return accessibleContext7.getAccessibleRole();
                    }
                }, accessibleContext)) {
                    final int accessibleXcoordFromContext3 = this.getAccessibleXcoordFromContext(accessibleContext7);
                    final int accessibleYcoordFromContext3 = this.getAccessibleYcoordFromContext(accessibleContext7);
                    final int accessibleWidthFromContext2 = this.getAccessibleWidthFromContext(accessibleContext7);
                    final int accessibleHeightFromContext2 = this.getAccessibleHeightFromContext(accessibleContext7);
                    if (accessibleXcoordFromContext3 < accessibleXcoordFromContext && accessibleYcoordFromContext3 <= n3 && n3 <= accessibleYcoordFromContext3 + accessibleHeightFromContext2) {
                        final String s13 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext7.getAccessibleName();
                            }
                        }, accessibleContext);
                        if (null != s13) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Name of a LABEL object positioned to the left of the object.");
                            this.references.increment(s13);
                            return s13;
                        }
                        final String s14 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext7.getAccessibleDescription();
                            }
                        }, accessibleContext);
                        if (null != s14) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Description of a LABEL object positioned to the left of the object.");
                            this.references.increment(s14);
                            return s14;
                        }
                    }
                    else if (accessibleYcoordFromContext3 < n3 && accessibleXcoordFromContext3 <= n2 && n2 <= accessibleXcoordFromContext3 + accessibleWidthFromContext2) {
                        final String s15 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext7.getAccessibleName();
                            }
                        }, accessibleContext);
                        if (null != s15) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Name of a LABEL object positioned above the object.");
                            this.references.increment(s15);
                            return s15;
                        }
                        final String s16 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                            @Override
                            public String call() {
                                return accessibleContext7.getAccessibleDescription();
                            }
                        }, accessibleContext);
                        if (null != s16) {
                            this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Description of a LABEL object positioned above the object.");
                            this.references.increment(s16);
                            return s16;
                        }
                    }
                }
            }
        }
        if (AccessibleRole.TEXT == accessibleRole || AccessibleRole.COMBO_BOX == accessibleRole || b2) {
            for (int k = intValue2 - 1; k >= 0; --k) {
                final Accessible accessible3 = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
                    @Override
                    public Accessible call() throws Exception {
                        return accessibleContext2.getAccessibleChild(k);
                    }
                }, accessibleContext);
                if (null != accessible3) {
                    final AccessibleContext accessibleContext8 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                        @Override
                        public AccessibleContext call() throws Exception {
                            return accessible3.getAccessibleContext();
                        }
                    }, accessibleContext);
                    if (null != accessibleContext8) {
                        final AccessibleRole accessibleRole3 = InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                            @Override
                            public AccessibleRole call() throws Exception {
                                return accessibleContext8.getAccessibleRole();
                            }
                        }, accessibleContext);
                        if (AccessibleRole.PUSH_BUTTON == accessibleRole3 || AccessibleRole.TOGGLE_BUTTON == accessibleRole3) {
                            final int accessibleXcoordFromContext4 = this.getAccessibleXcoordFromContext(accessibleContext8);
                            final int accessibleYcoordFromContext4 = this.getAccessibleYcoordFromContext(accessibleContext8);
                            this.getAccessibleWidthFromContext(accessibleContext8);
                            final int accessibleHeightFromContext3 = this.getAccessibleHeightFromContext(accessibleContext8);
                            if (accessibleXcoordFromContext4 < accessibleXcoordFromContext && accessibleYcoordFromContext4 <= n3 && n3 <= accessibleYcoordFromContext4 + accessibleHeightFromContext3) {
                                final String s17 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                                    @Override
                                    public String call() {
                                        return accessibleContext8.getAccessibleName();
                                    }
                                }, accessibleContext);
                                if (null != s17) {
                                    this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Name of a PUSH_BUTTON or TOGGLE_BUTTON object positioned to the left of the object.");
                                    this.references.increment(s17);
                                    return s17;
                                }
                                final String s18 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                                    @Override
                                    public String call() {
                                        return accessibleContext8.getAccessibleDescription();
                                    }
                                }, accessibleContext);
                                if (null != s18) {
                                    this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Description of a PUSH_BUTTON or TOGGLE_BUTTON object positioned to the left of the object.");
                                    this.references.increment(s18);
                                    return s18;
                                }
                            }
                        }
                    }
                }
            }
            for (int l = intValue2 + 1; l <= intValue; ++l) {
                final Accessible accessible4 = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
                    @Override
                    public Accessible call() throws Exception {
                        return accessibleContext2.getAccessibleChild(l);
                    }
                }, accessibleContext);
                if (null != accessible4) {
                    final AccessibleContext accessibleContext9 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                        @Override
                        public AccessibleContext call() throws Exception {
                            return accessible4.getAccessibleContext();
                        }
                    }, accessibleContext);
                    if (null != accessibleContext9) {
                        final AccessibleRole accessibleRole4 = InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                            @Override
                            public AccessibleRole call() throws Exception {
                                return accessibleContext9.getAccessibleRole();
                            }
                        }, accessibleContext);
                        if (AccessibleRole.PUSH_BUTTON == accessibleRole4 || AccessibleRole.TOGGLE_BUTTON == accessibleRole4) {
                            final int accessibleXcoordFromContext5 = this.getAccessibleXcoordFromContext(accessibleContext9);
                            final int accessibleYcoordFromContext5 = this.getAccessibleYcoordFromContext(accessibleContext9);
                            this.getAccessibleWidthFromContext(accessibleContext9);
                            final int accessibleHeightFromContext4 = this.getAccessibleHeightFromContext(accessibleContext9);
                            if (accessibleXcoordFromContext5 < accessibleXcoordFromContext && accessibleYcoordFromContext5 <= n3 && n3 <= accessibleYcoordFromContext5 + accessibleHeightFromContext4) {
                                final String s19 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                                    @Override
                                    public String call() {
                                        return accessibleContext9.getAccessibleName();
                                    }
                                }, accessibleContext);
                                if (null != s19) {
                                    this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Name of a PUSH_BUTTON or TOGGLE_BUTTON object positioned to the left of the object.");
                                    this.references.increment(s19);
                                    return s19;
                                }
                                final String s20 = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                                    @Override
                                    public String call() {
                                        return accessibleContext9.getAccessibleDescription();
                                    }
                                }, accessibleContext);
                                if (null != s20) {
                                    this.debugString("[INFO]: bk -- The Virtual Accessible Name was obtained from Accessible Description of a PUSH_BUTTON or TOGGLE_BUTTON object positioned to the left of the object.");
                                    this.references.increment(s20);
                                    return s20;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private String getAccessibleDescriptionFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return accessibleContext.getAccessibleDescription();
                }
            }, accessibleContext);
            if (s != null) {
                this.references.increment(s);
                this.debugString("[INFO]: Returning AccessibleDescription from Context: " + s);
                return s;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleDescriptionFromContext; ac = null");
        }
        return null;
    }
    
    private String getAccessibleRoleStringFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final AccessibleRole accessibleRole = InvocationUtils.invokeAndWait((Callable<AccessibleRole>)new Callable<AccessibleRole>() {
                @Override
                public AccessibleRole call() throws Exception {
                    return accessibleContext.getAccessibleRole();
                }
            }, accessibleContext);
            if (accessibleRole != null) {
                final String displayString = accessibleRole.toDisplayString(Locale.US);
                if (displayString != null) {
                    this.references.increment(displayString);
                    this.debugString("[INFO]: Returning AccessibleRole from Context: " + displayString);
                    return displayString;
                }
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleRoleStringFromContext; ac = null");
        }
        return null;
    }
    
    private String getAccessibleRoleStringFromContext_en_US(final AccessibleContext accessibleContext) {
        return this.getAccessibleRoleStringFromContext(accessibleContext);
    }
    
    private String getAccessibleStatesStringFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final AccessibleStateSet set = InvocationUtils.invokeAndWait((Callable<AccessibleStateSet>)new Callable<AccessibleStateSet>() {
                @Override
                public AccessibleStateSet call() throws Exception {
                    return accessibleContext.getAccessibleStateSet();
                }
            }, accessibleContext);
            if (set != null) {
                String s = set.toString();
                if (s != null && s.indexOf(AccessibleState.MANAGES_DESCENDANTS.toDisplayString(Locale.US)) == -1) {
                    final AccessibleRole accessibleRole = InvocationUtils.invokeAndWait(() -> accessibleContext2.getAccessibleRole(), accessibleContext);
                    if (accessibleRole == AccessibleRole.LIST || accessibleRole == AccessibleRole.TABLE || accessibleRole == AccessibleRole.TREE) {
                        s = s + "," + AccessibleState.MANAGES_DESCENDANTS.toDisplayString(Locale.US);
                    }
                    this.references.increment(s);
                    this.debugString("[INFO]: Returning AccessibleStateSet from Context: " + s);
                    return s;
                }
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleStatesStringFromContext; ac = null");
        }
        return null;
    }
    
    private String getAccessibleStatesStringFromContext_en_US(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final AccessibleStateSet set = InvocationUtils.invokeAndWait((Callable<AccessibleStateSet>)new Callable<AccessibleStateSet>() {
                @Override
                public AccessibleStateSet call() throws Exception {
                    return accessibleContext.getAccessibleStateSet();
                }
            }, accessibleContext);
            if (set != null) {
                String s = "";
                final AccessibleState[] array = set.toArray();
                if (array != null && array.length > 0) {
                    s = array[0].toDisplayString(Locale.US);
                    for (int i = 1; i < array.length; ++i) {
                        s = s + "," + array[i].toDisplayString(Locale.US);
                    }
                }
                this.references.increment(s);
                this.debugString("[INFO]: Returning AccessibleStateSet en_US from Context: " + s);
                return s;
            }
        }
        this.debugString("[ERROR]: getAccessibleStatesStringFromContext; ac = null");
        return null;
    }
    
    private AccessibleContext getAccessibleParentFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent != null) {
                    final AccessibleContext accessibleContext = accessibleParent.getAccessibleContext();
                    if (accessibleContext != null) {
                        return accessibleContext;
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleIndexInParentFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleContext.getAccessibleIndexInParent();
            }
        }, accessibleContext);
    }
    
    private int getAccessibleChildrenCountFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleContext.getAccessibleChildrenCount();
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getAccessibleChildFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext == null) {
            return null;
        }
        final JTable table = InvocationUtils.invokeAndWait((Callable<JTable>)new Callable<JTable>() {
            @Override
            public JTable call() throws Exception {
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent != null) {
                    final Accessible accessibleChild = accessibleParent.getAccessibleContext().getAccessibleChild(accessibleContext.getAccessibleIndexInParent());
                    if (accessibleChild instanceof JTable) {
                        return (JTable)accessibleChild;
                    }
                }
                return null;
            }
        }, accessibleContext);
        if (table == null) {
            return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                @Override
                public AccessibleContext call() throws Exception {
                    final Accessible accessibleChild = accessibleContext.getAccessibleChild(n);
                    if (accessibleChild != null) {
                        return accessibleChild.getAccessibleContext();
                    }
                    return null;
                }
            }, accessibleContext);
        }
        final AccessibleTable accessibleTableFromContext = this.getAccessibleTableFromContext(accessibleContext);
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            final /* synthetic */ int val$row = AccessBridge.this.getAccessibleTableRow(accessibleTableFromContext, n);
            final /* synthetic */ int val$column = AccessBridge.this.getAccessibleTableColumn(accessibleTableFromContext, n);
            
            @Override
            public AccessibleContext call() throws Exception {
                TableCellRenderer tableCellRenderer = table.getCellRenderer(this.val$row, this.val$column);
                if (tableCellRenderer == null) {
                    tableCellRenderer = table.getDefaultRenderer(table.getColumnClass(this.val$column));
                }
                final Component tableCellRendererComponent = tableCellRenderer.getTableCellRendererComponent(table, table.getValueAt(this.val$row, this.val$column), false, false, this.val$row, this.val$column);
                if (tableCellRendererComponent instanceof Accessible) {
                    return tableCellRendererComponent.getAccessibleContext();
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private Rectangle getAccessibleBoundsOnScreenFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<Rectangle>)new Callable<Rectangle>() {
            @Override
            public Rectangle call() throws Exception {
                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (accessibleComponent != null) {
                    final Rectangle bounds = accessibleComponent.getBounds();
                    if (bounds != null) {
                        try {
                            final Point locationOnScreen = accessibleComponent.getLocationOnScreen();
                            if (locationOnScreen != null) {
                                bounds.x = locationOnScreen.x;
                                bounds.y = locationOnScreen.y;
                                return bounds;
                            }
                        }
                        catch (final Exception ex) {
                            return null;
                        }
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleXcoordFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final Rectangle accessibleBoundsOnScreenFromContext = this.getAccessibleBoundsOnScreenFromContext(accessibleContext);
            if (accessibleBoundsOnScreenFromContext != null) {
                this.debugString("[INFO]: Returning Accessible x coord from Context: " + accessibleBoundsOnScreenFromContext.x);
                return accessibleBoundsOnScreenFromContext.x;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleXcoordFromContext ac = null");
        }
        return -1;
    }
    
    private int getAccessibleYcoordFromContext(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleYcoordFromContext() called");
        if (accessibleContext != null) {
            final Rectangle accessibleBoundsOnScreenFromContext = this.getAccessibleBoundsOnScreenFromContext(accessibleContext);
            if (accessibleBoundsOnScreenFromContext != null) {
                return accessibleBoundsOnScreenFromContext.y;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleYcoordFromContext; ac = null");
        }
        return -1;
    }
    
    private int getAccessibleHeightFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final Rectangle accessibleBoundsOnScreenFromContext = this.getAccessibleBoundsOnScreenFromContext(accessibleContext);
            if (accessibleBoundsOnScreenFromContext != null) {
                return accessibleBoundsOnScreenFromContext.height;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleHeightFromContext; ac = null");
        }
        return -1;
    }
    
    private int getAccessibleWidthFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final Rectangle accessibleBoundsOnScreenFromContext = this.getAccessibleBoundsOnScreenFromContext(accessibleContext);
            if (accessibleBoundsOnScreenFromContext != null) {
                return accessibleBoundsOnScreenFromContext.width;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleWidthFromContext; ac = null");
        }
        return -1;
    }
    
    private AccessibleComponent getAccessibleComponentFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final AccessibleComponent accessibleComponent = InvocationUtils.invokeAndWait(() -> accessibleContext2.getAccessibleComponent(), accessibleContext);
            if (accessibleComponent != null) {
                this.debugString("[INFO]: Returning AccessibleComponent Context");
                return accessibleComponent;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleComponentFromContext; ac = null");
        }
        return null;
    }
    
    private AccessibleAction getAccessibleActionFromContext(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: Returning AccessibleAction Context");
        return (accessibleContext == null) ? null : InvocationUtils.invokeAndWait((Callable<AccessibleAction>)new Callable<AccessibleAction>() {
            @Override
            public AccessibleAction call() throws Exception {
                return accessibleContext.getAccessibleAction();
            }
        }, accessibleContext);
    }
    
    private AccessibleSelection getAccessibleSelectionFromContext(final AccessibleContext accessibleContext) {
        return (accessibleContext == null) ? null : InvocationUtils.invokeAndWait((Callable<AccessibleSelection>)new Callable<AccessibleSelection>() {
            @Override
            public AccessibleSelection call() throws Exception {
                return accessibleContext.getAccessibleSelection();
            }
        }, accessibleContext);
    }
    
    private AccessibleText getAccessibleTextFromContext(final AccessibleContext accessibleContext) {
        return (accessibleContext == null) ? null : InvocationUtils.invokeAndWait((Callable<AccessibleText>)new Callable<AccessibleText>() {
            @Override
            public AccessibleText call() throws Exception {
                return accessibleContext.getAccessibleText();
            }
        }, accessibleContext);
    }
    
    private AccessibleValue getAccessibleValueFromContext(final AccessibleContext accessibleContext) {
        return (accessibleContext == null) ? null : InvocationUtils.invokeAndWait((Callable<AccessibleValue>)new Callable<AccessibleValue>() {
            @Override
            public AccessibleValue call() throws Exception {
                return accessibleContext.getAccessibleValue();
            }
        }, accessibleContext);
    }
    
    private Rectangle getCaretLocation(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getCaretLocation");
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<Rectangle>)new Callable<Rectangle>() {
            @Override
            public Rectangle call() throws Exception {
                Rectangle modelToView = null;
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent instanceof Accessible) {
                    final Accessible accessibleChild = accessibleParent.getAccessibleContext().getAccessibleChild(accessibleContext.getAccessibleIndexInParent());
                    if (accessibleChild instanceof JTextComponent) {
                        final JTextComponent textComponent = (JTextComponent)accessibleChild;
                        try {
                            modelToView = textComponent.modelToView(textComponent.getCaretPosition());
                            if (modelToView != null) {
                                final Point locationOnScreen = textComponent.getLocationOnScreen();
                                modelToView.translate(locationOnScreen.x, locationOnScreen.y);
                            }
                        }
                        catch (final BadLocationException ex) {}
                    }
                }
                return modelToView;
            }
        }, accessibleContext);
    }
    
    private int getCaretLocationX(final AccessibleContext accessibleContext) {
        final Rectangle caretLocation = this.getCaretLocation(accessibleContext);
        if (caretLocation != null) {
            return caretLocation.x;
        }
        return -1;
    }
    
    private int getCaretLocationY(final AccessibleContext accessibleContext) {
        final Rectangle caretLocation = this.getCaretLocation(accessibleContext);
        if (caretLocation != null) {
            return caretLocation.y;
        }
        return -1;
    }
    
    private int getCaretLocationHeight(final AccessibleContext accessibleContext) {
        final Rectangle caretLocation = this.getCaretLocation(accessibleContext);
        if (caretLocation != null) {
            return caretLocation.height;
        }
        return -1;
    }
    
    private int getCaretLocationWidth(final AccessibleContext accessibleContext) {
        final Rectangle caretLocation = this.getCaretLocation(accessibleContext);
        if (caretLocation != null) {
            return caretLocation.width;
        }
        return -1;
    }
    
    private int getAccessibleCharCountFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText != null) {
                    return accessibleText.getCharCount();
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleCaretPositionFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText != null) {
                    return accessibleText.getCaretPosition();
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleIndexAtPointFromContext(final AccessibleContext accessibleContext, final int n, final int n2) {
        this.debugString("[INFO]: getAccessibleIndexAtPointFromContext: x = " + n + "; y = " + n2);
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (accessibleText != null && accessibleComponent != null) {
                    try {
                        final Point locationOnScreen = accessibleComponent.getLocationOnScreen();
                        if (locationOnScreen != null) {
                            int n = n - locationOnScreen.x;
                            if (n < 0) {
                                n = 0;
                            }
                            int n2 = n2 - locationOnScreen.y;
                            if (n2 < 0) {
                                n2 = 0;
                            }
                            final Point point = new Point(n, n2);
                            return accessibleText.getIndexAtPoint(new Point(n, n2));
                        }
                    }
                    catch (final Exception ex) {}
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private String getAccessibleLetterAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText == null) {
                        return null;
                    }
                    return accessibleText.getAtIndex(1, n);
                }
            }, accessibleContext);
            if (s != null) {
                this.references.increment(s);
                return s;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleLetterAtIndexFromContext; ac = null");
        }
        return null;
    }
    
    private String getAccessibleWordAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText == null) {
                        return null;
                    }
                    return accessibleText.getAtIndex(2, n);
                }
            }, accessibleContext);
            if (s != null) {
                this.references.increment(s);
                return s;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleWordAtIndexFromContext; ac = null");
        }
        return null;
    }
    
    private String getAccessibleSentenceAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText == null) {
                        return null;
                    }
                    return accessibleText.getAtIndex(3, n);
                }
            }, accessibleContext);
            if (s != null) {
                this.references.increment(s);
                return s;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleSentenceAtIndexFromContext; ac = null");
        }
        return null;
    }
    
    private int getAccessibleTextSelectionStartFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText != null) {
                    return accessibleText.getSelectionStart();
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleTextSelectionEndFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText != null) {
                    return accessibleText.getSelectionEnd();
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private String getAccessibleTextSelectedTextFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                @Override
                public String call() throws Exception {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText == null) {
                        return null;
                    }
                    return accessibleText.getSelectedText();
                }
            }, accessibleContext);
            if (s != null) {
                this.references.increment(s);
                return s;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleTextSelectedTextFromContext; ac = null");
        }
        return null;
    }
    
    private String getAccessibleAttributesAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext == null) {
            return null;
        }
        final String expandStyleConstants = this.expandStyleConstants(InvocationUtils.invokeAndWait((Callable<AttributeSet>)new Callable<AttributeSet>() {
            @Override
            public AttributeSet call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText != null) {
                    return accessibleText.getCharacterAttribute(n);
                }
                return null;
            }
        }, accessibleContext));
        if (expandStyleConstants != null) {
            this.references.increment(expandStyleConstants);
            return expandStyleConstants;
        }
        return null;
    }
    
    private int getAccessibleTextLineLeftBoundsFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText == null) {
                    return -1;
                }
                final Rectangle characterBounds = accessibleText.getCharacterBounds(n);
                accessibleText.getCharCount();
                if (characterBounds == null) {
                    return -1;
                }
                int n = 1;
                int n2 = (n - n < 0) ? 0 : (n - n);
                for (Rectangle rectangle = accessibleText.getCharacterBounds(n2); rectangle != null && rectangle.y >= characterBounds.y && n2 > 0; n2 = ((n - n < 0) ? 0 : (n - n)), rectangle = accessibleText.getCharacterBounds(n2)) {
                    n <<= 1;
                }
                if (n2 != 0) {
                    for (int i = n >> 1; i > 0; i >>= 1) {
                        if (accessibleText.getCharacterBounds(n2 + i).y < characterBounds.y) {
                            n2 += i;
                        }
                    }
                    ++n2;
                }
                return n2;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleTextLineRightBoundsFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (accessibleText == null) {
                    return -1;
                }
                final Rectangle characterBounds = accessibleText.getCharacterBounds(n);
                final int charCount = accessibleText.getCharCount();
                if (characterBounds == null) {
                    return -1;
                }
                int n = 1;
                int n2 = (n + n > charCount - 1) ? (charCount - 1) : (n + n);
                for (Rectangle rectangle = accessibleText.getCharacterBounds(n2); rectangle != null && rectangle.y <= characterBounds.y && n2 < charCount - 1; n2 = ((n + n > charCount - 1) ? (charCount - 1) : (n + n)), rectangle = accessibleText.getCharacterBounds(n2)) {
                    n <<= 1;
                }
                if (n2 != charCount - 1) {
                    for (int i = n >> 1; i > 0; i >>= 1) {
                        if (accessibleText.getCharacterBounds(n2 - i).y > characterBounds.y) {
                            n2 -= i;
                        }
                    }
                    --n2;
                }
                return n2;
            }
        }, accessibleContext);
    }
    
    private String getAccessibleTextRangeFromContext(final AccessibleContext accessibleContext, final int n, final int n2) {
        final String s = InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText != null) {
                        if (n > n2) {
                            return null;
                        }
                        if (n2 >= accessibleText.getCharCount()) {
                            return null;
                        }
                        final StringBuffer sb = new StringBuffer(n2 - n + 1);
                        for (int i = n; i <= n2; ++i) {
                            sb.append(accessibleText.getAtIndex(1, i));
                        }
                        return sb.toString();
                    }
                }
                return null;
            }
        }, accessibleContext);
        if (s != null) {
            this.references.increment(s);
            return s;
        }
        return null;
    }
    
    private AttributeSet getAccessibleAttributeSetAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        return InvocationUtils.invokeAndWait((Callable<AttributeSet>)new Callable<AttributeSet>() {
            @Override
            public AttributeSet call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText != null) {
                        final AttributeSet characterAttribute = accessibleText.getCharacterAttribute(n);
                        if (characterAttribute != null) {
                            AccessBridge.this.references.increment(characterAttribute);
                            return characterAttribute;
                        }
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private Rectangle getAccessibleTextRectAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        final Rectangle rectangle = InvocationUtils.invokeAndWait((Callable<Rectangle>)new Callable<Rectangle>() {
            @Override
            public Rectangle call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                    if (accessibleText != null) {
                        final Rectangle characterBounds = accessibleText.getCharacterBounds(n);
                        if (characterBounds != null) {
                            final String atIndex = accessibleText.getAtIndex(1, n);
                            if (atIndex != null && atIndex.equals("\n")) {
                                characterBounds.width = 0;
                            }
                            return characterBounds;
                        }
                    }
                }
                return null;
            }
        }, accessibleContext);
        final Rectangle accessibleBoundsOnScreenFromContext = this.getAccessibleBoundsOnScreenFromContext(accessibleContext);
        if (rectangle != null && accessibleBoundsOnScreenFromContext != null) {
            rectangle.translate(accessibleBoundsOnScreenFromContext.x, accessibleBoundsOnScreenFromContext.y);
            return rectangle;
        }
        return null;
    }
    
    private int getAccessibleXcoordTextRectAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final Rectangle accessibleTextRectAtIndexFromContext = this.getAccessibleTextRectAtIndexFromContext(accessibleContext, n);
            if (accessibleTextRectAtIndexFromContext != null) {
                return accessibleTextRectAtIndexFromContext.x;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleXcoordTextRectAtIndexFromContext; ac = null");
        }
        return -1;
    }
    
    private int getAccessibleYcoordTextRectAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final Rectangle accessibleTextRectAtIndexFromContext = this.getAccessibleTextRectAtIndexFromContext(accessibleContext, n);
            if (accessibleTextRectAtIndexFromContext != null) {
                return accessibleTextRectAtIndexFromContext.y;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleYcoordTextRectAtIndexFromContext; ac = null");
        }
        return -1;
    }
    
    private int getAccessibleHeightTextRectAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final Rectangle accessibleTextRectAtIndexFromContext = this.getAccessibleTextRectAtIndexFromContext(accessibleContext, n);
            if (accessibleTextRectAtIndexFromContext != null) {
                return accessibleTextRectAtIndexFromContext.height;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleHeightTextRectAtIndexFromContext; ac = null");
        }
        return -1;
    }
    
    private int getAccessibleWidthTextRectAtIndexFromContext(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext != null) {
            final Rectangle accessibleTextRectAtIndexFromContext = this.getAccessibleTextRectAtIndexFromContext(accessibleContext, n);
            if (accessibleTextRectAtIndexFromContext != null) {
                return accessibleTextRectAtIndexFromContext.width;
            }
        }
        else {
            this.debugString("[ERROR]: getAccessibleWidthTextRectAtIndexFromContext; ac = null");
        }
        return -1;
    }
    
    private boolean getBoldFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.isBold(set);
        }
        this.debugString("[ERROR]: getBoldFromAttributeSet; as = null");
        return false;
    }
    
    private boolean getItalicFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.isItalic(set);
        }
        this.debugString("[ERROR]: getItalicFromAttributeSet; as = null");
        return false;
    }
    
    private boolean getUnderlineFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.isUnderline(set);
        }
        this.debugString("[ERROR]: getUnderlineFromAttributeSet; as = null");
        return false;
    }
    
    private boolean getStrikethroughFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.isStrikeThrough(set);
        }
        this.debugString("[ERROR]: getStrikethroughFromAttributeSet; as = null");
        return false;
    }
    
    private boolean getSuperscriptFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.isSuperscript(set);
        }
        this.debugString("[ERROR]: getSuperscriptFromAttributeSet; as = null");
        return false;
    }
    
    private boolean getSubscriptFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.isSubscript(set);
        }
        this.debugString("[ERROR]: getSubscriptFromAttributeSet; as = null");
        return false;
    }
    
    private String getBackgroundColorFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            final String string = StyleConstants.getBackground(set).toString();
            if (string != null) {
                this.references.increment(string);
                return string;
            }
        }
        else {
            this.debugString("[ERROR]: getBackgroundColorFromAttributeSet; as = null");
        }
        return null;
    }
    
    private String getForegroundColorFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            final String string = StyleConstants.getForeground(set).toString();
            if (string != null) {
                this.references.increment(string);
                return string;
            }
        }
        else {
            this.debugString("[ERROR]: getForegroundColorFromAttributeSet; as = null");
        }
        return null;
    }
    
    private String getFontFamilyFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            final String string = StyleConstants.getFontFamily(set).toString();
            if (string != null) {
                this.references.increment(string);
                return string;
            }
        }
        else {
            this.debugString("[ERROR]: getFontFamilyFromAttributeSet; as = null");
        }
        return null;
    }
    
    private int getFontSizeFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getFontSize(set);
        }
        this.debugString("[ERROR]: getFontSizeFromAttributeSet; as = null");
        return -1;
    }
    
    private int getAlignmentFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getAlignment(set);
        }
        this.debugString("[ERROR]: getAlignmentFromAttributeSet; as = null");
        return -1;
    }
    
    private int getBidiLevelFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getBidiLevel(set);
        }
        this.debugString("[ERROR]: getBidiLevelFromAttributeSet; as = null");
        return -1;
    }
    
    private float getFirstLineIndentFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getFirstLineIndent(set);
        }
        this.debugString("[ERROR]: getFirstLineIndentFromAttributeSet; as = null");
        return -1.0f;
    }
    
    private float getLeftIndentFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getLeftIndent(set);
        }
        this.debugString("[ERROR]: getLeftIndentFromAttributeSet; as = null");
        return -1.0f;
    }
    
    private float getRightIndentFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getRightIndent(set);
        }
        this.debugString("[ERROR]: getRightIndentFromAttributeSet; as = null");
        return -1.0f;
    }
    
    private float getLineSpacingFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getLineSpacing(set);
        }
        this.debugString("[ERROR]: getLineSpacingFromAttributeSet; as = null");
        return -1.0f;
    }
    
    private float getSpaceAboveFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getSpaceAbove(set);
        }
        this.debugString("[ERROR]: getSpaceAboveFromAttributeSet; as = null");
        return -1.0f;
    }
    
    private float getSpaceBelowFromAttributeSet(final AttributeSet set) {
        if (set != null) {
            return StyleConstants.getSpaceBelow(set);
        }
        this.debugString("[ERROR]: getSpaceBelowFromAttributeSet; as = null");
        return -1.0f;
    }
    
    private String expandStyleConstants(final AttributeSet set) {
        String s = "" + "BidiLevel = " + StyleConstants.getBidiLevel(set);
        final Component component = StyleConstants.getComponent(set);
        if (component != null) {
            if (component instanceof Accessible) {
                final AccessibleContext accessibleContext = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return component.getAccessibleContext();
                    }
                }, component);
                if (accessibleContext != null) {
                    s = s + "; Accessible Component = " + InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return accessibleContext.getAccessibleName();
                        }
                    }, accessibleContext);
                }
                else {
                    s = s + "; Innaccessible Component = " + component;
                }
            }
            else {
                s = s + "; Innaccessible Component = " + component;
            }
        }
        final Icon icon = StyleConstants.getIcon(set);
        if (icon != null) {
            if (icon instanceof ImageIcon) {
                s = s + "; ImageIcon = " + ((ImageIcon)icon).getDescription();
            }
            else {
                s = s + "; Icon = " + icon;
            }
        }
        String s2 = s + "; FontFamily = " + StyleConstants.getFontFamily(set) + "; FontSize = " + StyleConstants.getFontSize(set);
        if (StyleConstants.isBold(set)) {
            s2 += "; bold";
        }
        if (StyleConstants.isItalic(set)) {
            s2 += "; italic";
        }
        if (StyleConstants.isUnderline(set)) {
            s2 += "; underline";
        }
        if (StyleConstants.isStrikeThrough(set)) {
            s2 += "; strikethrough";
        }
        if (StyleConstants.isSuperscript(set)) {
            s2 += "; superscript";
        }
        if (StyleConstants.isSubscript(set)) {
            s2 += "; subscript";
        }
        final Color foreground = StyleConstants.getForeground(set);
        if (foreground != null) {
            s2 = s2 + "; Foreground = " + foreground;
        }
        final Color background = StyleConstants.getBackground(set);
        if (background != null) {
            s2 = s2 + "; Background = " + background;
        }
        String s3 = s2 + "; FirstLineIndent = " + StyleConstants.getFirstLineIndent(set) + "; RightIndent = " + StyleConstants.getRightIndent(set) + "; LeftIndent = " + StyleConstants.getLeftIndent(set) + "; LineSpacing = " + StyleConstants.getLineSpacing(set) + "; SpaceAbove = " + StyleConstants.getSpaceAbove(set) + "; SpaceBelow = " + StyleConstants.getSpaceBelow(set) + "; Alignment = " + StyleConstants.getAlignment(set);
        final TabSet tabSet = StyleConstants.getTabSet(set);
        if (tabSet != null) {
            s3 = s3 + "; TabSet = " + tabSet;
        }
        return s3;
    }
    
    private String getCurrentAccessibleValueFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final Number n = InvocationUtils.invokeAndWait((Callable<Number>)new Callable<Number>() {
                @Override
                public Number call() throws Exception {
                    final AccessibleValue accessibleValue = accessibleContext.getAccessibleValue();
                    if (accessibleValue == null) {
                        return null;
                    }
                    return accessibleValue.getCurrentAccessibleValue();
                }
            }, accessibleContext);
            if (n != null) {
                final String string = n.toString();
                if (string != null) {
                    this.references.increment(string);
                    return string;
                }
            }
        }
        else {
            this.debugString("[ERROR]: getCurrentAccessibleValueFromContext; ac = null");
        }
        return null;
    }
    
    private String getMaximumAccessibleValueFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final Number n = InvocationUtils.invokeAndWait((Callable<Number>)new Callable<Number>() {
                @Override
                public Number call() throws Exception {
                    final AccessibleValue accessibleValue = accessibleContext.getAccessibleValue();
                    if (accessibleValue == null) {
                        return null;
                    }
                    return accessibleValue.getMaximumAccessibleValue();
                }
            }, accessibleContext);
            if (n != null) {
                final String string = n.toString();
                if (string != null) {
                    this.references.increment(string);
                    return string;
                }
            }
        }
        else {
            this.debugString("[ERROR]: getMaximumAccessibleValueFromContext; ac = null");
        }
        return null;
    }
    
    private String getMinimumAccessibleValueFromContext(final AccessibleContext accessibleContext) {
        if (accessibleContext != null) {
            final Number n = InvocationUtils.invokeAndWait((Callable<Number>)new Callable<Number>() {
                @Override
                public Number call() throws Exception {
                    final AccessibleValue accessibleValue = accessibleContext.getAccessibleValue();
                    if (accessibleValue == null) {
                        return null;
                    }
                    return accessibleValue.getMinimumAccessibleValue();
                }
            }, accessibleContext);
            if (n != null) {
                final String string = n.toString();
                if (string != null) {
                    this.references.increment(string);
                    return string;
                }
            }
        }
        else {
            this.debugString("[ERROR]: getMinimumAccessibleValueFromContext; ac = null");
        }
        return null;
    }
    
    private void addAccessibleSelectionFromContext(final AccessibleContext accessibleContext, final int n) {
        try {
            InvocationUtils.invokeAndWait((Callable<Object>)new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (accessibleContext != null) {
                        final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                        if (accessibleSelection != null) {
                            accessibleSelection.addAccessibleSelection(n);
                        }
                    }
                    return null;
                }
            }, accessibleContext);
        }
        catch (final Exception ex) {}
    }
    
    private void clearAccessibleSelectionFromContext(final AccessibleContext accessibleContext) {
        try {
            InvocationUtils.invokeAndWait((Callable<Object>)new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        accessibleSelection.clearAccessibleSelection();
                    }
                    return null;
                }
            }, accessibleContext);
        }
        catch (final Exception ex) {}
    }
    
    private AccessibleContext getAccessibleSelectionFromContext(final AccessibleContext accessibleContext, final int n) {
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        final Accessible accessibleSelection2 = accessibleSelection.getAccessibleSelection(n);
                        if (accessibleSelection2 == null) {
                            return null;
                        }
                        return accessibleSelection2.getAccessibleContext();
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleSelectionCountFromContext(final AccessibleContext accessibleContext) {
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        return accessibleSelection.getAccessibleSelectionCount();
                    }
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private boolean isAccessibleChildSelectedFromContext(final AccessibleContext accessibleContext, final int n) {
        return InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        return accessibleSelection.isAccessibleChildSelected(n);
                    }
                }
                return false;
            }
        }, accessibleContext);
    }
    
    private void removeAccessibleSelectionFromContext(final AccessibleContext accessibleContext, final int n) {
        InvocationUtils.invokeAndWait((Callable<Object>)new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        accessibleSelection.removeAccessibleSelection(n);
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private void selectAllAccessibleSelectionFromContext(final AccessibleContext accessibleContext) {
        InvocationUtils.invokeAndWait((Callable<Object>)new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                    if (accessibleSelection != null) {
                        accessibleSelection.selectAllAccessibleSelection();
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private AccessibleTable getAccessibleTableFromContext(final AccessibleContext accessibleContext) {
        final String javaVersionProperty = this.getJavaVersionProperty();
        if (javaVersionProperty != null && javaVersionProperty.compareTo("1.3") >= 0) {
            return InvocationUtils.invokeAndWait((Callable<AccessibleTable>)new Callable<AccessibleTable>() {
                @Override
                public AccessibleTable call() throws Exception {
                    if (accessibleContext != null) {
                        final AccessibleTable accessibleTable = accessibleContext.getAccessibleTable();
                        if (accessibleTable != null) {
                            AccessBridge.this.hashtab.put(accessibleTable, accessibleContext);
                            return accessibleTable;
                        }
                    }
                    return null;
                }
            }, accessibleContext);
        }
        return null;
    }
    
    private AccessibleContext getContextFromAccessibleTable(final AccessibleTable accessibleTable) {
        return this.hashtab.get(accessibleTable);
    }
    
    private int getAccessibleTableRowCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: ##### getAccessibleTableRowCount");
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleTable accessibleTable = accessibleContext.getAccessibleTable();
                    if (accessibleTable != null) {
                        return accessibleTable.getAccessibleRowCount();
                    }
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleTableColumnCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: ##### getAccessibleTableColumnCount");
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleTable accessibleTable = accessibleContext.getAccessibleTable();
                    if (accessibleTable != null) {
                        return accessibleTable.getAccessibleColumnCount();
                    }
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getAccessibleTableCellAccessibleContext(final AccessibleTable accessibleTable, final int n, final int n2) {
        this.debugString("[INFO]: getAccessibleTableCellAccessibleContext: at = " + accessibleTable.getClass());
        if (accessibleTable == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                if (!(accessibleTable instanceof AccessibleContext)) {
                    final Accessible accessible = accessibleTable.getAccessibleAt(n, n2);
                    if (accessible != null) {
                        return accessible.getAccessibleContext();
                    }
                }
                else {
                    final AccessibleContext accessibleContext = (AccessibleContext)accessibleTable;
                    final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                    if (accessibleParent != null) {
                        final Accessible accessibleChild = accessibleParent.getAccessibleContext().getAccessibleChild(accessibleContext.getAccessibleIndexInParent());
                        if (accessibleChild instanceof JTable) {
                            final JTable table = (JTable)accessibleChild;
                            TableCellRenderer tableCellRenderer = table.getCellRenderer(n, n2);
                            if (tableCellRenderer == null) {
                                tableCellRenderer = table.getDefaultRenderer(table.getColumnClass(n2));
                            }
                            final Component tableCellRendererComponent = tableCellRenderer.getTableCellRendererComponent(table, table.getValueAt(n, n2), false, false, n, n2);
                            if (tableCellRendererComponent instanceof Accessible) {
                                return tableCellRendererComponent.getAccessibleContext();
                            }
                        }
                    }
                }
                return null;
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableCellIndex(final AccessibleTable accessibleTable, final int n, final int n2) {
        this.debugString("[INFO]: ##### getAccessibleTableCellIndex: at=" + accessibleTable);
        if (accessibleTable != null) {
            final int n3 = n * InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return accessibleTable.getAccessibleColumnCount();
                }
            }, this.getContextFromAccessibleTable(accessibleTable)) + n2;
            this.debugString("[INFO]:    ##### getAccessibleTableCellIndex=" + n3);
            return n3;
        }
        this.debugString("[ERROR]: ##### getAccessibleTableCellIndex FAILED");
        return -1;
    }
    
    private int getAccessibleTableCellRowExtent(final AccessibleTable accessibleTable, final int n, final int n2) {
        this.debugString("[INFO]: ##### getAccessibleTableCellRowExtent");
        if (accessibleTable != null) {
            final int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return accessibleTable.getAccessibleRowExtentAt(n, n2);
                }
            }, this.getContextFromAccessibleTable(accessibleTable));
            this.debugString("[INFO]:   ##### getAccessibleTableCellRowExtent=" + intValue);
            return intValue;
        }
        this.debugString("[ERROR]: ##### getAccessibleTableCellRowExtent FAILED");
        return -1;
    }
    
    private int getAccessibleTableCellColumnExtent(final AccessibleTable accessibleTable, final int n, final int n2) {
        this.debugString("[INFO]: ##### getAccessibleTableCellColumnExtent");
        if (accessibleTable != null) {
            final int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return accessibleTable.getAccessibleColumnExtentAt(n, n2);
                }
            }, this.getContextFromAccessibleTable(accessibleTable));
            this.debugString("[INFO]:   ##### getAccessibleTableCellColumnExtent=" + intValue);
            return intValue;
        }
        this.debugString("[ERROR]: ##### getAccessibleTableCellColumnExtent FAILED");
        return -1;
    }
    
    private boolean isAccessibleTableCellSelected(final AccessibleTable accessibleTable, final int n, final int n2) {
        this.debugString("[INFO]: ##### isAccessibleTableCellSelected: [" + n + "][" + n2 + "]");
        return accessibleTable != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean contains = false;
                final Accessible accessible = accessibleTable.getAccessibleAt(n, n2);
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    if (accessibleContext == null) {
                        return false;
                    }
                    final AccessibleStateSet accessibleStateSet = accessibleContext.getAccessibleStateSet();
                    if (accessibleStateSet != null) {
                        contains = accessibleStateSet.contains(AccessibleState.SELECTED);
                    }
                }
                return contains;
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private AccessibleTable getAccessibleTableRowHeader(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: #####  getAccessibleTableRowHeader called");
        final AccessibleTable accessibleTable = InvocationUtils.invokeAndWait((Callable<AccessibleTable>)new Callable<AccessibleTable>() {
            @Override
            public AccessibleTable call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleTable accessibleTable = accessibleContext.getAccessibleTable();
                    if (accessibleTable != null) {
                        return accessibleTable.getAccessibleRowHeader();
                    }
                }
                return null;
            }
        }, accessibleContext);
        if (accessibleTable != null) {
            this.hashtab.put(accessibleTable, accessibleContext);
        }
        return accessibleTable;
    }
    
    private AccessibleTable getAccessibleTableColumnHeader(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: ##### getAccessibleTableColumnHeader");
        if (accessibleContext == null) {
            return null;
        }
        final AccessibleTable accessibleTable = InvocationUtils.invokeAndWait((Callable<AccessibleTable>)new Callable<AccessibleTable>() {
            @Override
            public AccessibleTable call() throws Exception {
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent != null) {
                    final Accessible accessibleChild = accessibleParent.getAccessibleContext().getAccessibleChild(accessibleContext.getAccessibleIndexInParent());
                    if (accessibleChild instanceof JTable && ((JTable)accessibleChild).getTableHeader() == null) {
                        return null;
                    }
                }
                final AccessibleTable accessibleTable = accessibleContext.getAccessibleTable();
                if (accessibleTable != null) {
                    return accessibleTable.getAccessibleColumnHeader();
                }
                return null;
            }
        }, accessibleContext);
        if (accessibleTable != null) {
            this.hashtab.put(accessibleTable, accessibleContext);
        }
        return accessibleTable;
    }
    
    private int getAccessibleTableRowHeaderRowCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: #####  getAccessibleTableRowHeaderRowCount called");
        if (accessibleContext != null) {
            final AccessibleTable accessibleTableRowHeader = this.getAccessibleTableRowHeader(accessibleContext);
            if (accessibleTableRowHeader != null) {
                return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        if (accessibleTableRowHeader != null) {
                            return accessibleTableRowHeader.getAccessibleRowCount();
                        }
                        return -1;
                    }
                }, accessibleContext);
            }
        }
        return -1;
    }
    
    private int getAccessibleTableRowHeaderColumnCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: #####  getAccessibleTableRowHeaderColumnCount called");
        if (accessibleContext != null) {
            final AccessibleTable accessibleTableRowHeader = this.getAccessibleTableRowHeader(accessibleContext);
            if (accessibleTableRowHeader != null) {
                return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        if (accessibleTableRowHeader != null) {
                            return accessibleTableRowHeader.getAccessibleColumnCount();
                        }
                        return -1;
                    }
                }, accessibleContext);
            }
        }
        this.debugString("[ERROR]: ##### getAccessibleTableRowHeaderColumnCount FAILED");
        return -1;
    }
    
    private int getAccessibleTableColumnHeaderRowCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: ##### getAccessibleTableColumnHeaderRowCount");
        if (accessibleContext != null) {
            final AccessibleTable accessibleTableColumnHeader = this.getAccessibleTableColumnHeader(accessibleContext);
            if (accessibleTableColumnHeader != null) {
                return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        if (accessibleTableColumnHeader != null) {
                            return accessibleTableColumnHeader.getAccessibleRowCount();
                        }
                        return -1;
                    }
                }, accessibleContext);
            }
        }
        this.debugString("[ERROR]: ##### getAccessibleTableColumnHeaderRowCount FAILED");
        return -1;
    }
    
    private int getAccessibleTableColumnHeaderColumnCount(final AccessibleContext accessibleContext) {
        this.debugString("[ERROR]: #####  getAccessibleTableColumnHeaderColumnCount");
        if (accessibleContext != null) {
            final AccessibleTable accessibleTableColumnHeader = this.getAccessibleTableColumnHeader(accessibleContext);
            if (accessibleTableColumnHeader != null) {
                return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        if (accessibleTableColumnHeader != null) {
                            return accessibleTableColumnHeader.getAccessibleColumnCount();
                        }
                        return -1;
                    }
                }, accessibleContext);
            }
        }
        this.debugString("[ERROR]: ##### getAccessibleTableColumnHeaderColumnCount FAILED");
        return -1;
    }
    
    private AccessibleContext getAccessibleTableRowDescription(final AccessibleTable accessibleTable, final int n) {
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                if (accessibleTable != null) {
                    final Accessible accessibleRowDescription = accessibleTable.getAccessibleRowDescription(n);
                    if (accessibleRowDescription != null) {
                        return accessibleRowDescription.getAccessibleContext();
                    }
                }
                return null;
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private AccessibleContext getAccessibleTableColumnDescription(final AccessibleTable accessibleTable, final int n) {
        if (accessibleTable == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                final Accessible accessibleColumnDescription = accessibleTable.getAccessibleColumnDescription(n);
                if (accessibleColumnDescription != null) {
                    return accessibleColumnDescription.getAccessibleContext();
                }
                return null;
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableRowSelectionCount(final AccessibleTable accessibleTable) {
        if (accessibleTable != null) {
            return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    final int[] selectedAccessibleRows = accessibleTable.getSelectedAccessibleRows();
                    if (selectedAccessibleRows != null) {
                        return selectedAccessibleRows.length;
                    }
                    return -1;
                }
            }, this.getContextFromAccessibleTable(accessibleTable));
        }
        return -1;
    }
    
    private int getAccessibleTableRowSelections(final AccessibleTable accessibleTable, final int n) {
        if (accessibleTable != null) {
            return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    final int[] selectedAccessibleRows = accessibleTable.getSelectedAccessibleRows();
                    if (selectedAccessibleRows.length > n) {
                        return selectedAccessibleRows[n];
                    }
                    return -1;
                }
            }, this.getContextFromAccessibleTable(accessibleTable));
        }
        return -1;
    }
    
    private boolean isAccessibleTableRowSelected(final AccessibleTable accessibleTable, final int n) {
        return accessibleTable != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return accessibleTable.isAccessibleRowSelected(n);
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private boolean isAccessibleTableColumnSelected(final AccessibleTable accessibleTable, final int n) {
        return accessibleTable != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return accessibleTable.isAccessibleColumnSelected(n);
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableColumnSelectionCount(final AccessibleTable accessibleTable) {
        if (accessibleTable == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final int[] selectedAccessibleColumns = accessibleTable.getSelectedAccessibleColumns();
                if (selectedAccessibleColumns != null) {
                    return selectedAccessibleColumns.length;
                }
                return -1;
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableColumnSelections(final AccessibleTable accessibleTable, final int n) {
        if (accessibleTable == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final int[] selectedAccessibleColumns = accessibleTable.getSelectedAccessibleColumns();
                if (selectedAccessibleColumns != null && selectedAccessibleColumns.length > n) {
                    return selectedAccessibleColumns[n];
                }
                return -1;
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableRow(final AccessibleTable accessibleTable, final int n) {
        if (accessibleTable == null) {
            return -1;
        }
        return n / InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleTable.getAccessibleColumnCount();
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableColumn(final AccessibleTable accessibleTable, final int n) {
        if (accessibleTable == null) {
            return -1;
        }
        return n % InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleTable.getAccessibleColumnCount();
            }
        }, this.getContextFromAccessibleTable(accessibleTable));
    }
    
    private int getAccessibleTableIndex(final AccessibleTable accessibleTable, final int n, final int n2) {
        if (accessibleTable == null) {
            return -1;
        }
        return n * InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleTable.getAccessibleColumnCount();
            }
        }, this.getContextFromAccessibleTable(accessibleTable)) + n2;
    }
    
    private int getAccessibleRelationCount(final AccessibleContext accessibleContext) {
        final String javaVersionProperty = this.getJavaVersionProperty();
        if (javaVersionProperty != null && javaVersionProperty.compareTo("1.3") >= 0 && accessibleContext != null) {
            final AccessibleRelationSet set = InvocationUtils.invokeAndWait((Callable<AccessibleRelationSet>)new Callable<AccessibleRelationSet>() {
                @Override
                public AccessibleRelationSet call() throws Exception {
                    return accessibleContext.getAccessibleRelationSet();
                }
            }, accessibleContext);
            if (set != null) {
                return set.size();
            }
        }
        return 0;
    }
    
    private String getAccessibleRelationKey(final AccessibleContext accessibleContext, final int n) {
        return InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleRelationSet accessibleRelationSet = accessibleContext.getAccessibleRelationSet();
                    if (accessibleRelationSet != null) {
                        final AccessibleRelation[] array = accessibleRelationSet.toArray();
                        if (array != null && n >= 0 && n < array.length) {
                            return array[n].getKey();
                        }
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private int getAccessibleRelationTargetCount(final AccessibleContext accessibleContext, final int n) {
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleRelationSet accessibleRelationSet = accessibleContext.getAccessibleRelationSet();
                    if (accessibleRelationSet != null) {
                        final AccessibleRelation[] array = accessibleRelationSet.toArray();
                        if (array != null && n >= 0 && n < array.length) {
                            return array[n].getTarget().length;
                        }
                    }
                }
                return -1;
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getAccessibleRelationTarget(final AccessibleContext accessibleContext, final int n, final int n2) {
        this.debugString("[INFO]: ***** getAccessibleRelationTarget");
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                if (accessibleContext != null) {
                    final AccessibleRelationSet accessibleRelationSet = accessibleContext.getAccessibleRelationSet();
                    if (accessibleRelationSet != null) {
                        final AccessibleRelation[] array = accessibleRelationSet.toArray();
                        if (array != null && n >= 0 && n < array.length) {
                            final Object[] target = array[n].getTarget();
                            if (target != null && (n2 >= 0 & n2 < target.length)) {
                                final Object o = target[n2];
                                if (o instanceof Accessible) {
                                    return ((Accessible)o).getAccessibleContext();
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private AccessibleHypertext getAccessibleHypertext(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleHyperlink");
        if (accessibleContext == null) {
            return null;
        }
        final AccessibleHypertext accessibleHypertext = InvocationUtils.invokeAndWait((Callable<AccessibleHypertext>)new Callable<AccessibleHypertext>() {
            @Override
            public AccessibleHypertext call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (!(accessibleText instanceof AccessibleHypertext)) {
                    return null;
                }
                return (AccessibleHypertext)accessibleText;
            }
        }, accessibleContext);
        this.hyperTextContextMap.put(accessibleHypertext, accessibleContext);
        return accessibleHypertext;
    }
    
    private int getAccessibleHyperlinkCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleHyperlinkCount");
        if (accessibleContext == null) {
            return 0;
        }
        final AccessibleHypertext accessibleHypertext = this.getAccessibleHypertext(accessibleContext);
        if (accessibleHypertext == null) {
            return 0;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleHypertext.getLinkCount();
            }
        }, accessibleContext);
    }
    
    private AccessibleHyperlink getAccessibleHyperlink(final AccessibleHypertext accessibleHypertext, final int n) {
        this.debugString("[INFO]: getAccessibleHyperlink");
        if (accessibleHypertext == null) {
            return null;
        }
        final AccessibleContext accessibleContext = this.hyperTextContextMap.get(accessibleHypertext);
        if (n < 0 || n >= InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleHypertext.getLinkCount();
            }
        }, accessibleContext)) {
            return null;
        }
        final AccessibleHyperlink accessibleHyperlink = InvocationUtils.invokeAndWait((Callable<AccessibleHyperlink>)new Callable<AccessibleHyperlink>() {
            @Override
            public AccessibleHyperlink call() throws Exception {
                final AccessibleHyperlink link = accessibleHypertext.getLink(n);
                if (link == null || !link.isValid()) {
                    return null;
                }
                return link;
            }
        }, accessibleContext);
        this.hyperLinkContextMap.put(accessibleHyperlink, accessibleContext);
        return accessibleHyperlink;
    }
    
    private String getAccessibleHyperlinkText(final AccessibleHyperlink accessibleHyperlink) {
        this.debugString("[INFO]: getAccessibleHyperlinkText");
        if (accessibleHyperlink == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                final String accessibleActionDescription = accessibleHyperlink.getAccessibleActionDescription(0);
                if (accessibleActionDescription != null) {
                    return accessibleActionDescription.toString();
                }
                return null;
            }
        }, this.hyperLinkContextMap.get(accessibleHyperlink));
    }
    
    private String getAccessibleHyperlinkURL(final AccessibleHyperlink accessibleHyperlink) {
        this.debugString("[INFO]: getAccessibleHyperlinkURL");
        if (accessibleHyperlink == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                final Object accessibleActionObject = accessibleHyperlink.getAccessibleActionObject(0);
                if (accessibleActionObject != null) {
                    return accessibleActionObject.toString();
                }
                return null;
            }
        }, this.hyperLinkContextMap.get(accessibleHyperlink));
    }
    
    private int getAccessibleHyperlinkStartIndex(final AccessibleHyperlink accessibleHyperlink) {
        this.debugString("[INFO]: getAccessibleHyperlinkStartIndex");
        if (accessibleHyperlink == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleHyperlink.getStartIndex();
            }
        }, this.hyperLinkContextMap.get(accessibleHyperlink));
    }
    
    private int getAccessibleHyperlinkEndIndex(final AccessibleHyperlink accessibleHyperlink) {
        this.debugString("[INFO]: getAccessibleHyperlinkEndIndex");
        if (accessibleHyperlink == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleHyperlink.getEndIndex();
            }
        }, this.hyperLinkContextMap.get(accessibleHyperlink));
    }
    
    private int getAccessibleHypertextLinkIndex(final AccessibleHypertext accessibleHypertext, final int n) {
        this.debugString("[INFO]: getAccessibleHypertextLinkIndex: charIndex = " + n);
        if (accessibleHypertext == null) {
            return -1;
        }
        final int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleHypertext.getLinkIndex(n);
            }
        }, this.hyperTextContextMap.get(accessibleHypertext));
        this.debugString("[INFO]: getAccessibleHypertextLinkIndex returning " + intValue);
        return intValue;
    }
    
    private boolean activateAccessibleHyperlink(final AccessibleContext accessibleContext, final AccessibleHyperlink accessibleHyperlink) {
        if (accessibleHyperlink == null) {
            return false;
        }
        final boolean booleanValue = InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return accessibleHyperlink.doAccessibleAction(0);
            }
        }, accessibleContext);
        this.debugString("[INFO]: activateAccessibleHyperlink: returning = " + booleanValue);
        return booleanValue;
    }
    
    private KeyStroke getMnemonic(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<KeyStroke>)new Callable<KeyStroke>() {
            @Override
            public KeyStroke call() throws Exception {
                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (!(accessibleComponent instanceof AccessibleExtendedComponent)) {
                    return null;
                }
                final AccessibleExtendedComponent accessibleExtendedComponent = (AccessibleExtendedComponent)accessibleComponent;
                if (accessibleExtendedComponent != null) {
                    final AccessibleKeyBinding accessibleKeyBinding = accessibleExtendedComponent.getAccessibleKeyBinding();
                    if (accessibleKeyBinding != null) {
                        final Object accessibleKeyBinding2 = accessibleKeyBinding.getAccessibleKeyBinding(0);
                        if (accessibleKeyBinding2 instanceof KeyStroke) {
                            return (KeyStroke)accessibleKeyBinding2;
                        }
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private KeyStroke getAccelerator(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<KeyStroke>)new Callable<KeyStroke>() {
            @Override
            public KeyStroke call() throws Exception {
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent instanceof Accessible) {
                    final Accessible accessibleChild = accessibleParent.getAccessibleContext().getAccessibleChild(accessibleContext.getAccessibleIndexInParent());
                    if (accessibleChild instanceof JMenuItem) {
                        final JMenuItem menuItem = (JMenuItem)accessibleChild;
                        if (menuItem == null) {
                            return null;
                        }
                        return menuItem.getAccelerator();
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private int fKeyNumber(final KeyStroke keyStroke) {
        if (keyStroke == null) {
            return 0;
        }
        int n = 0;
        final String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());
        if (keyText != null && (keyText.length() == 2 || keyText.length() == 3) && keyText.substring(0, 1).equals("F")) {
            try {
                final int int1 = Integer.parseInt(keyText.substring(1));
                if (int1 >= 1 && int1 <= 24) {
                    n = int1;
                }
            }
            catch (final Exception ex) {}
        }
        return n;
    }
    
    private int controlCode(final KeyStroke keyStroke) {
        if (keyStroke == null) {
            return 0;
        }
        int keyCode = keyStroke.getKeyCode();
        switch (keyCode) {
            case 8:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 127:
            case 155:
            case 224:
            case 225:
            case 226:
            case 227: {
                break;
            }
            default: {
                keyCode = 0;
                break;
            }
        }
        return keyCode;
    }
    
    private char getKeyChar(final KeyStroke keyStroke) {
        if (keyStroke == null) {
            return '\0';
        }
        final int fKeyNumber = this.fKeyNumber(keyStroke);
        if (fKeyNumber != 0) {
            this.debugString("[INFO]:   Shortcut is: F" + fKeyNumber);
            return (char)fKeyNumber;
        }
        final int controlCode = this.controlCode(keyStroke);
        if (controlCode != 0) {
            this.debugString("[INFO]:   Shortcut is control character: " + Integer.toHexString(controlCode));
            return (char)controlCode;
        }
        final String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());
        this.debugString("[INFO]:   Shortcut is: " + keyText);
        if (keyText != null || keyText.length() > 0) {
            final CharSequence subSequence = keyText.subSequence(0, 1);
            if (subSequence != null || subSequence.length() > 0) {
                return subSequence.charAt(0);
            }
        }
        return '\0';
    }
    
    private int getModifiers(final KeyStroke keyStroke) {
        if (keyStroke == null) {
            return 0;
        }
        this.debugString("[INFO]: In AccessBridge.getModifiers");
        int n = 0;
        if (this.fKeyNumber(keyStroke) != 0) {
            n |= 0x100;
        }
        if (this.controlCode(keyStroke) != 0) {
            n |= 0x200;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(keyStroke.toString());
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.startsWith("met")) {
                this.debugString("[INFO]:   found meta");
                n |= 0x4;
            }
            if (nextToken.startsWith("ctr")) {
                this.debugString("[INFO]:   found ctrl");
                n |= 0x2;
            }
            if (nextToken.startsWith("alt")) {
                this.debugString("[INFO]:   found alt");
                n |= 0x8;
            }
            if (nextToken.startsWith("shi")) {
                this.debugString("[INFO]:   found shift");
                n |= 0x1;
            }
        }
        this.debugString("[INFO]:   returning modifiers: 0x" + Integer.toHexString(n));
        return n;
    }
    
    private int getAccessibleKeyBindingsCount(final AccessibleContext accessibleContext) {
        if (accessibleContext == null || !this.runningOnJDK1_4) {
            return 0;
        }
        int n = 0;
        if (this.getMnemonic(accessibleContext) != null) {
            ++n;
        }
        if (this.getAccelerator(accessibleContext) != null) {
            ++n;
        }
        return n;
    }
    
    private char getAccessibleKeyBindingChar(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext == null || !this.runningOnJDK1_4) {
            return '\0';
        }
        if (n == 0 && this.getMnemonic(accessibleContext) == null) {
            final KeyStroke accelerator = this.getAccelerator(accessibleContext);
            if (accelerator != null) {
                return this.getKeyChar(accelerator);
            }
        }
        if (n == 0) {
            final KeyStroke mnemonic = this.getMnemonic(accessibleContext);
            if (mnemonic != null) {
                return this.getKeyChar(mnemonic);
            }
        }
        else if (n == 1) {
            final KeyStroke accelerator2 = this.getAccelerator(accessibleContext);
            if (accelerator2 != null) {
                return this.getKeyChar(accelerator2);
            }
        }
        return '\0';
    }
    
    private int getAccessibleKeyBindingModifiers(final AccessibleContext accessibleContext, final int n) {
        if (accessibleContext == null || !this.runningOnJDK1_4) {
            return 0;
        }
        if (n == 0 && this.getMnemonic(accessibleContext) == null) {
            final KeyStroke accelerator = this.getAccelerator(accessibleContext);
            if (accelerator != null) {
                return this.getModifiers(accelerator);
            }
        }
        if (n == 0) {
            final KeyStroke mnemonic = this.getMnemonic(accessibleContext);
            if (mnemonic != null) {
                return this.getModifiers(mnemonic);
            }
        }
        else if (n == 1) {
            final KeyStroke accelerator2 = this.getAccelerator(accessibleContext);
            if (accelerator2 != null) {
                return this.getModifiers(accelerator2);
            }
        }
        return 0;
    }
    
    private int getAccessibleIconsCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleIconsCount");
        if (accessibleContext == null) {
            return 0;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleIcon[] accessibleIcon = accessibleContext.getAccessibleIcon();
                if (accessibleIcon == null) {
                    return 0;
                }
                return accessibleIcon.length;
            }
        }, accessibleContext);
    }
    
    private String getAccessibleIconDescription(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]: getAccessibleIconDescription: index = " + n);
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                final AccessibleIcon[] accessibleIcon = accessibleContext.getAccessibleIcon();
                if (accessibleIcon == null || n < 0 || n >= accessibleIcon.length) {
                    return null;
                }
                return accessibleIcon[n].getAccessibleIconDescription();
            }
        }, accessibleContext);
    }
    
    private int getAccessibleIconHeight(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]: getAccessibleIconHeight: index = " + n);
        if (accessibleContext == null) {
            return 0;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleIcon[] accessibleIcon = accessibleContext.getAccessibleIcon();
                if (accessibleIcon == null || n < 0 || n >= accessibleIcon.length) {
                    return 0;
                }
                return accessibleIcon[n].getAccessibleIconHeight();
            }
        }, accessibleContext);
    }
    
    private int getAccessibleIconWidth(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]: getAccessibleIconWidth: index = " + n);
        if (accessibleContext == null) {
            return 0;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleIcon[] accessibleIcon = accessibleContext.getAccessibleIcon();
                if (accessibleIcon == null || n < 0 || n >= accessibleIcon.length) {
                    return 0;
                }
                return accessibleIcon[n].getAccessibleIconWidth();
            }
        }, accessibleContext);
    }
    
    private int getAccessibleActionsCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getAccessibleActionsCount");
        if (accessibleContext == null) {
            return 0;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
                if (accessibleAction == null) {
                    return 0;
                }
                return accessibleAction.getAccessibleActionCount();
            }
        }, accessibleContext);
    }
    
    private String getAccessibleActionName(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]: getAccessibleActionName: index = " + n);
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                final AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
                if (accessibleAction == null) {
                    return null;
                }
                return accessibleAction.getAccessibleActionDescription(n);
            }
        }, accessibleContext);
    }
    
    private boolean doAccessibleActions(final AccessibleContext accessibleContext, final String s) {
        this.debugString("[INFO]: doAccessibleActions: action name = " + s);
        return accessibleContext != null && s != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
                if (accessibleAction == null) {
                    return false;
                }
                int n = -1;
                for (int accessibleActionCount = accessibleAction.getAccessibleActionCount(), i = 0; i < accessibleActionCount; ++i) {
                    if (s.equals(accessibleAction.getAccessibleActionDescription(i))) {
                        n = i;
                        break;
                    }
                }
                if (n == -1) {
                    return false;
                }
                return accessibleAction.doAccessibleAction(n);
            }
        }, accessibleContext);
    }
    
    private boolean setTextContents(final AccessibleContext accessibleContext, final String s) {
        this.debugString("[INFO]: setTextContents: ac = " + accessibleContext + "; text = " + s);
        if (!(accessibleContext instanceof AccessibleEditableText)) {
            this.debugString("[WARN]:   ac not instanceof AccessibleEditableText: " + accessibleContext);
            return false;
        }
        if (s == null) {
            this.debugString("[WARN]:   text is null");
            return false;
        }
        return InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (!accessibleContext.getAccessibleStateSet().contains(AccessibleState.ENABLED)) {
                    return false;
                }
                ((AccessibleEditableText)accessibleContext).setTextContents(s);
                return true;
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getInternalFrame(final AccessibleContext accessibleContext) {
        return this.getParentWithRole(accessibleContext, AccessibleRole.INTERNAL_FRAME.toString());
    }
    
    private AccessibleContext getTopLevelObject(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getTopLevelObject; ac = " + accessibleContext);
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                if (accessibleContext.getAccessibleRole() == AccessibleRole.DIALOG) {
                    return accessibleContext;
                }
                Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent == null) {
                    return accessibleContext;
                }
                for (Accessible accessibleParent2 = accessibleParent; accessibleParent2 != null && accessibleParent2.getAccessibleContext() != null; accessibleParent2 = accessibleParent.getAccessibleContext().getAccessibleParent()) {
                    final AccessibleContext accessibleContext = accessibleParent2.getAccessibleContext();
                    if (accessibleContext != null && accessibleContext.getAccessibleRole() == AccessibleRole.DIALOG) {
                        return accessibleContext;
                    }
                    accessibleParent = accessibleParent2;
                }
                return accessibleParent.getAccessibleContext();
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getParentWithRole(final AccessibleContext accessibleContext, final String s) {
        this.debugString("[INFO]: getParentWithRole; ac = " + accessibleContext + "\n role = " + s);
        if (accessibleContext == null || s == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                final AccessibleRole accessibleRole = AccessBridge.this.accessibleRoleMap.get(s);
                if (accessibleRole == null) {
                    return accessibleContext;
                }
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent == null && accessibleContext.getAccessibleRole() == accessibleRole) {
                    return accessibleContext;
                }
                AccessibleContext accessibleContext;
                for (Accessible accessibleParent2 = accessibleParent; accessibleParent2 != null && (accessibleContext = accessibleParent2.getAccessibleContext()) != null; accessibleParent2 = accessibleParent2.getAccessibleContext().getAccessibleParent()) {
                    if (accessibleContext.getAccessibleRole() == accessibleRole) {
                        return accessibleContext;
                    }
                }
                return null;
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getParentWithRoleElseRoot(final AccessibleContext accessibleContext, final String s) {
        AccessibleContext accessibleContext2 = this.getParentWithRole(accessibleContext, s);
        if (accessibleContext2 == null) {
            accessibleContext2 = this.getTopLevelObject(accessibleContext);
        }
        return accessibleContext2;
    }
    
    private int getObjectDepth(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getObjectDepth: ac = " + accessibleContext);
        if (accessibleContext == null) {
            return -1;
        }
        return InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int n = 0;
                final Accessible accessibleParent = accessibleContext.getAccessibleParent();
                if (accessibleParent == null) {
                    return n;
                }
                for (Accessible accessibleParent2 = accessibleParent; accessibleParent2 != null && accessibleParent2.getAccessibleContext() != null; accessibleParent2 = accessibleParent2.getAccessibleContext().getAccessibleParent(), ++n) {}
                return n;
            }
        }, accessibleContext);
    }
    
    private AccessibleContext getActiveDescendent(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getActiveDescendent: ac = " + accessibleContext);
        if (accessibleContext == null) {
            return null;
        }
        final Accessible accessible = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
            @Override
            public Accessible call() throws Exception {
                return accessibleContext.getAccessibleParent();
            }
        }, accessibleContext);
        if (accessible != null) {
            final Accessible accessible2 = InvocationUtils.invokeAndWait((Callable<Accessible>)new Callable<Accessible>() {
                @Override
                public Accessible call() throws Exception {
                    return accessible.getAccessibleContext().getAccessibleChild(accessibleContext.getAccessibleIndexInParent());
                }
            }, accessibleContext);
            if (accessible2 instanceof JTree) {
                return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    final /* synthetic */ JTree val$tree = (JTree)accessible2;
                    
                    @Override
                    public AccessibleContext call() throws Exception {
                        return new AccessibleJTreeNode(this.val$tree, this.val$tree.getSelectionPath(), null);
                    }
                }, accessible2);
            }
        }
        return InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
            @Override
            public AccessibleContext call() throws Exception {
                final AccessibleSelection accessibleSelection = accessibleContext.getAccessibleSelection();
                if (accessibleSelection == null) {
                    return null;
                }
                if (accessibleSelection.getAccessibleSelectionCount() != 1) {
                    return null;
                }
                final Accessible accessibleSelection2 = accessibleSelection.getAccessibleSelection(0);
                if (accessibleSelection2 == null) {
                    return null;
                }
                return accessibleSelection2.getAccessibleContext();
            }
        }, accessibleContext);
    }
    
    private String getJAWSAccessibleName(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]:  getJAWSAccessibleName");
        if (accessibleContext == null) {
            return null;
        }
        return InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                return accessibleContext.getAccessibleName();
            }
        }, accessibleContext);
    }
    
    private boolean requestFocus(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]:  requestFocus");
        return accessibleContext != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (accessibleComponent == null) {
                    return false;
                }
                accessibleComponent.requestFocus();
                return accessibleContext.getAccessibleStateSet().contains(AccessibleState.FOCUSED);
            }
        }, accessibleContext);
    }
    
    private boolean selectTextRange(final AccessibleContext accessibleContext, final int n, final int n2) {
        this.debugString("[INFO]:  selectTextRange: start = " + n + "; end = " + n2);
        return accessibleContext != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (!(accessibleText instanceof AccessibleEditableText)) {
                    return false;
                }
                ((AccessibleEditableText)accessibleText).selectText(n, n2);
                return accessibleText.getSelectionStart() == n && accessibleText.getSelectionEnd() == n2;
            }
        }, accessibleContext);
    }
    
    private boolean setCaretPosition(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]: setCaretPosition: position = " + n);
        return accessibleContext != null && InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                final AccessibleText accessibleText = accessibleContext.getAccessibleText();
                if (!(accessibleText instanceof AccessibleEditableText)) {
                    return false;
                }
                ((AccessibleEditableText)accessibleText).selectText(n, n);
                return accessibleText.getCaretPosition() == n;
            }
        }, accessibleContext);
    }
    
    private int getVisibleChildrenCount(final AccessibleContext accessibleContext) {
        this.debugString("[INFO]: getVisibleChildrenCount");
        if (accessibleContext == null) {
            return -1;
        }
        this._visibleChildrenCount = 0;
        this._getVisibleChildrenCount(accessibleContext);
        this.debugString("[INFO]:   _visibleChildrenCount = " + this._visibleChildrenCount);
        return this._visibleChildrenCount;
    }
    
    private void _getVisibleChildrenCount(final AccessibleContext accessibleContext) {
        if (accessibleContext == null) {
            return;
        }
        if (accessibleContext instanceof AccessibleExtendedTable) {
            this._getVisibleChildrenCount((AccessibleExtendedTable)accessibleContext);
            return;
        }
        for (int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleContext.getAccessibleChildrenCount();
            }
        }, accessibleContext), i = 0; i < intValue; ++i) {
            final AccessibleContext accessibleContext2 = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                @Override
                public AccessibleContext call() throws Exception {
                    final Accessible accessibleChild = accessibleContext.getAccessibleChild(i);
                    if (accessibleChild != null) {
                        return accessibleChild.getAccessibleContext();
                    }
                    return null;
                }
            }, accessibleContext);
            if (accessibleContext2 != null) {
                if (InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return accessibleContext2.getAccessibleStateSet().contains(AccessibleState.SHOWING);
                    }
                }, accessibleContext)) {
                    ++this._visibleChildrenCount;
                    if (InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            return accessibleContext2.getAccessibleChildrenCount();
                        }
                    }, accessibleContext) > 0) {
                        this._getVisibleChildrenCount(accessibleContext2);
                    }
                }
            }
        }
    }
    
    private void _getVisibleChildrenCount(final AccessibleExtendedTable accessibleExtendedTable) {
        if (accessibleExtendedTable == null) {
            return;
        }
        int n = -1;
        int n2 = -1;
        int n3 = 0;
        final int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleExtendedTable.getAccessibleRowCount();
            }
        }, accessibleExtendedTable);
        final int intValue2 = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleExtendedTable.getAccessibleColumnCount();
            }
        }, accessibleExtendedTable);
        for (int i = 0; i < intValue; ++i) {
            for (int j = 0; j < intValue2; ++j) {
                if (n == -1 || i <= n) {
                    if (n2 == -1 || j <= n2) {
                        final AccessibleContext accessibleContext = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                            @Override
                            public AccessibleContext call() throws Exception {
                                final Accessible accessible = accessibleExtendedTable.getAccessibleAt(i, j);
                                if (accessible == null) {
                                    return null;
                                }
                                return accessible.getAccessibleContext();
                            }
                        }, accessibleExtendedTable);
                        if (accessibleContext == null || !InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return accessibleContext.getAccessibleStateSet().contains(AccessibleState.SHOWING);
                            }
                        }, accessibleExtendedTable)) {
                            if (n3 != 0) {
                                if (j != 0 && n2 == -1) {
                                    n2 = j - 1;
                                }
                                else if (j == 0 && n == -1) {
                                    n = i - 1;
                                }
                            }
                        }
                        else {
                            n3 = 1;
                            ++this._visibleChildrenCount;
                            if (InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                                @Override
                                public Integer call() throws Exception {
                                    return accessibleContext.getAccessibleChildrenCount();
                                }
                            }, accessibleExtendedTable) > 0) {
                                this._getVisibleChildrenCount(accessibleContext);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private AccessibleContext getVisibleChild(final AccessibleContext accessibleContext, final int n) {
        this.debugString("[INFO]: getVisibleChild: index = " + n);
        if (accessibleContext == null) {
            return null;
        }
        this._visibleChild = null;
        this._currentVisibleIndex = 0;
        this._foundVisibleChild = false;
        this._getVisibleChild(accessibleContext, n);
        if (this._visibleChild != null) {
            this.debugString("[INFO]:     getVisibleChild: found child = " + InvocationUtils.invokeAndWait((Callable<String>)new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return AccessBridge.this._visibleChild.getAccessibleName();
                }
            }, accessibleContext));
        }
        return this._visibleChild;
    }
    
    private void _getVisibleChild(final AccessibleContext accessibleContext, final int n) {
        if (this._visibleChild != null) {
            return;
        }
        if (accessibleContext instanceof AccessibleExtendedTable) {
            this._getVisibleChild((AccessibleExtendedTable)accessibleContext, n);
            return;
        }
        for (int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleContext.getAccessibleChildrenCount();
            }
        }, accessibleContext), i = 0; i < intValue; ++i) {
            final AccessibleContext visibleChild = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                @Override
                public AccessibleContext call() throws Exception {
                    final Accessible accessibleChild = accessibleContext.getAccessibleChild(i);
                    if (accessibleChild == null) {
                        return null;
                    }
                    return accessibleChild.getAccessibleContext();
                }
            }, accessibleContext);
            if (visibleChild != null) {
                if (InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return visibleChild.getAccessibleStateSet().contains(AccessibleState.SHOWING);
                    }
                }, accessibleContext)) {
                    if (!this._foundVisibleChild && this._currentVisibleIndex == n) {
                        this._visibleChild = visibleChild;
                        this._foundVisibleChild = true;
                        return;
                    }
                    ++this._currentVisibleIndex;
                    if (InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            return visibleChild.getAccessibleChildrenCount();
                        }
                    }, accessibleContext) > 0) {
                        this._getVisibleChild(visibleChild, n);
                    }
                }
            }
        }
    }
    
    private void _getVisibleChild(final AccessibleExtendedTable accessibleExtendedTable, final int n) {
        if (this._visibleChild != null) {
            return;
        }
        int n2 = -1;
        int n3 = -1;
        int n4 = 0;
        final int intValue = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleExtendedTable.getAccessibleRowCount();
            }
        }, accessibleExtendedTable);
        final int intValue2 = InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return accessibleExtendedTable.getAccessibleColumnCount();
            }
        }, accessibleExtendedTable);
        for (int i = 0; i < intValue; ++i) {
            for (int j = 0; j < intValue2; ++j) {
                if (n2 == -1 || i <= n2) {
                    if (n3 == -1 || j <= n3) {
                        final AccessibleContext visibleChild = InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                            @Override
                            public AccessibleContext call() throws Exception {
                                final Accessible accessible = accessibleExtendedTable.getAccessibleAt(i, j);
                                if (accessible == null) {
                                    return null;
                                }
                                return accessible.getAccessibleContext();
                            }
                        }, accessibleExtendedTable);
                        if (visibleChild == null || !InvocationUtils.invokeAndWait((Callable<Boolean>)new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return visibleChild.getAccessibleStateSet().contains(AccessibleState.SHOWING);
                            }
                        }, accessibleExtendedTable)) {
                            if (n4 != 0) {
                                if (j != 0 && n3 == -1) {
                                    n3 = j - 1;
                                }
                                else if (j == 0 && n2 == -1) {
                                    n2 = i - 1;
                                }
                            }
                        }
                        else {
                            n4 = 1;
                            if (!this._foundVisibleChild && this._currentVisibleIndex == n) {
                                this._visibleChild = visibleChild;
                                this._foundVisibleChild = true;
                                return;
                            }
                            ++this._currentVisibleIndex;
                            if (InvocationUtils.invokeAndWait((Callable<Integer>)new Callable<Integer>() {
                                @Override
                                public Integer call() throws Exception {
                                    return visibleChild.getAccessibleChildrenCount();
                                }
                            }, accessibleExtendedTable) > 0) {
                                this._getVisibleChild(visibleChild, n);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private native void propertyCaretChange(final PropertyChangeEvent p0, final AccessibleContext p1, final int p2, final int p3);
    
    private native void propertyDescriptionChange(final PropertyChangeEvent p0, final AccessibleContext p1, final String p2, final String p3);
    
    private native void propertyNameChange(final PropertyChangeEvent p0, final AccessibleContext p1, final String p2, final String p3);
    
    private native void propertySelectionChange(final PropertyChangeEvent p0, final AccessibleContext p1);
    
    private native void propertyStateChange(final PropertyChangeEvent p0, final AccessibleContext p1, final String p2, final String p3);
    
    private native void propertyTextChange(final PropertyChangeEvent p0, final AccessibleContext p1);
    
    private native void propertyValueChange(final PropertyChangeEvent p0, final AccessibleContext p1, final String p2, final String p3);
    
    private native void propertyVisibleDataChange(final PropertyChangeEvent p0, final AccessibleContext p1);
    
    private native void propertyChildChange(final PropertyChangeEvent p0, final AccessibleContext p1, final AccessibleContext p2, final AccessibleContext p3);
    
    private native void propertyActiveDescendentChange(final PropertyChangeEvent p0, final AccessibleContext p1, final AccessibleContext p2, final AccessibleContext p3);
    
    private native void javaShutdown();
    
    private native void focusGained(final FocusEvent p0, final AccessibleContext p1);
    
    private native void focusLost(final FocusEvent p0, final AccessibleContext p1);
    
    private native void caretUpdate(final CaretEvent p0, final AccessibleContext p1);
    
    private native void mouseClicked(final MouseEvent p0, final AccessibleContext p1);
    
    private native void mouseEntered(final MouseEvent p0, final AccessibleContext p1);
    
    private native void mouseExited(final MouseEvent p0, final AccessibleContext p1);
    
    private native void mousePressed(final MouseEvent p0, final AccessibleContext p1);
    
    private native void mouseReleased(final MouseEvent p0, final AccessibleContext p1);
    
    private native void menuCanceled(final MenuEvent p0, final AccessibleContext p1);
    
    private native void menuDeselected(final MenuEvent p0, final AccessibleContext p1);
    
    private native void menuSelected(final MenuEvent p0, final AccessibleContext p1);
    
    private native void popupMenuCanceled(final PopupMenuEvent p0, final AccessibleContext p1);
    
    private native void popupMenuWillBecomeInvisible(final PopupMenuEvent p0, final AccessibleContext p1);
    
    private native void popupMenuWillBecomeVisible(final PopupMenuEvent p0, final AccessibleContext p1);
    
    private void addJavaEventNotification(final long n) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AccessBridge.this.eventHandler.addJavaEventNotification(n);
            }
        });
    }
    
    private void removeJavaEventNotification(final long n) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AccessBridge.this.eventHandler.removeJavaEventNotification(n);
            }
        });
    }
    
    private void addAccessibilityEventNotification(final long n) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AccessBridge.this.eventHandler.addAccessibilityEventNotification(n);
            }
        });
    }
    
    private void removeAccessibilityEventNotification(final long n) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AccessBridge.this.eventHandler.removeAccessibilityEventNotification(n);
            }
        });
    }
    
    static {
        AccessBridge.windowHandleToContextMap = new ConcurrentHashMap<Integer, AccessibleContext>();
        AccessBridge.contextToWindowHandleMap = new ConcurrentHashMap<AccessibleContext, Integer>();
        AccessBridge.nativeWindowHandlers = new Vector<NativeWindowHandler>();
    }
    
    private class dllRunner implements Runnable
    {
        @Override
        public void run() {
            AccessBridge.this.runDLL();
        }
    }
    
    private class shutdownHook implements Runnable
    {
        @Override
        public void run() {
            AccessBridge.this.debugString("[INFO]:***** shutdownHook: shutting down...");
            AccessBridge.this.javaShutdown();
        }
    }
    
    private class DefaultNativeWindowHandler implements NativeWindowHandler
    {
        @Override
        public Accessible getAccessibleFromNativeWindowHandle(final int n) {
            final Component componentFromNativeWindowHandle = this.getComponentFromNativeWindowHandle(n);
            if (componentFromNativeWindowHandle instanceof Accessible) {
                AccessBridge.this.saveContextToWindowHandleMapping(InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                    @Override
                    public AccessibleContext call() throws Exception {
                        return componentFromNativeWindowHandle.getAccessibleContext();
                    }
                }, componentFromNativeWindowHandle), n);
                return (Accessible)componentFromNativeWindowHandle;
            }
            return null;
        }
        
        private Component getComponentFromNativeWindowHandle(final int n) {
            if (AccessBridge.this.useJAWT_DLL) {
                AccessBridge.this.debugString("[INFO]:*** calling jawtGetComponentFromNativeWindowHandle");
                return AccessBridge.this.jawtGetComponentFromNativeWindowHandle(n);
            }
            AccessBridge.this.debugString("[INFO]:*** calling javaGetComponentFromNativeWindowHandle");
            final Object[] array = { null };
            if (AccessBridge.this.javaGetComponentFromNativeWindowHandleMethod != null) {
                try {
                    array[0] = n;
                    final Object invoke = AccessBridge.this.javaGetComponentFromNativeWindowHandleMethod.invoke(AccessBridge.this.toolkit, array);
                    if (invoke instanceof Accessible) {
                        AccessBridge.this.saveContextToWindowHandleMapping(InvocationUtils.invokeAndWait((Callable<AccessibleContext>)new Callable<AccessibleContext>() {
                            final /* synthetic */ Accessible val$acc = (Accessible)invoke;
                            
                            @Override
                            public AccessibleContext call() throws Exception {
                                return this.val$acc.getAccessibleContext();
                            }
                        }, (Component)invoke), n);
                    }
                    return (Component)invoke;
                }
                catch (final InvocationTargetException | IllegalAccessException ex) {
                    AccessBridge.this.debugString("[ERROR]:Exception: " + ((Throwable)ex).toString());
                }
            }
            return null;
        }
    }
    
    private class ObjectReferences
    {
        private ConcurrentHashMap<Object, Reference> refs;
        
        ObjectReferences() {
            this.refs = new ConcurrentHashMap<Object, Reference>(4);
        }
        
        String dump() {
            return this.refs.toString();
        }
        
        void increment(final Object o) {
            if (o == null) {
                AccessBridge.this.debugString("[WARN]: ObjectReferences::increment - Passed in object is null");
                return;
            }
            if (this.refs.containsKey(o)) {
                this.refs.get(o).value++;
            }
            else {
                this.refs.put(o, new Reference(1));
            }
        }
        
        void decrement(final Object o) {
            final Reference reference = this.refs.get(o);
            if (reference != null) {
                reference.value--;
                if (reference.value == 0) {
                    this.refs.remove(o);
                }
                else if (reference.value < 0) {
                    AccessBridge.this.debugString("[ERROR]: decrementing reference count below 0");
                }
            }
            else {
                AccessBridge.this.debugString("[ERROR]: object to decrement not in ObjectReferences table");
            }
        }
        
        private class Reference
        {
            private int value;
            
            Reference(final int value) {
                this.value = value;
            }
            
            @Override
            public String toString() {
                return "refCount: " + this.value;
            }
        }
    }
    
    private class EventHandler implements PropertyChangeListener, FocusListener, CaretListener, MenuListener, PopupMenuListener, MouseListener, WindowListener, ChangeListener
    {
        private AccessBridge accessBridge;
        private long javaEventMask;
        private long accessibilityEventMask;
        private AccessibleContext prevAC;
        private boolean stateChangeListenerAdded;
        
        EventHandler(final AccessBridge accessBridge) {
            this.javaEventMask = 0L;
            this.accessibilityEventMask = 0L;
            this.prevAC = null;
            this.stateChangeListenerAdded = false;
            this.accessBridge = accessBridge;
        }
        
        @Override
        public void windowOpened(final WindowEvent windowEvent) {
            Object source = null;
            if (windowEvent != null) {
                source = windowEvent.getSource();
            }
            if (source instanceof NativeWindowHandler) {
                addNativeWindowHandler((NativeWindowHandler)source);
            }
        }
        
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowClosed(final WindowEvent windowEvent) {
            Object source = null;
            if (windowEvent != null) {
                source = windowEvent.getSource();
            }
            if (source instanceof NativeWindowHandler) {
                removeNativeWindowHandler((NativeWindowHandler)source);
            }
        }
        
        @Override
        public void windowIconified(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeiconified(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
        }
        
        void addJavaEventNotification(final long n) {
            final long javaEventMask = this.javaEventMask | n;
            if ((this.javaEventMask & 0x6L) == 0x0L && (javaEventMask & 0x6L) != 0x0L) {
                AWTEventMonitor.addFocusListener(this);
            }
            if ((this.javaEventMask & 0x8L) == 0x0L && (javaEventMask & 0x8L) != 0x0L) {
                SwingEventMonitor.addCaretListener(this);
            }
            if ((this.javaEventMask & 0x1F0L) == 0x0L && (javaEventMask & 0x1F0L) != 0x0L) {
                AWTEventMonitor.addMouseListener(this);
            }
            if ((this.javaEventMask & 0xE00L) == 0x0L && (javaEventMask & 0xE00L) != 0x0L) {
                SwingEventMonitor.addMenuListener(this);
                SwingEventMonitor.addPopupMenuListener(this);
            }
            if ((this.javaEventMask & 0x7000L) == 0x0L && (javaEventMask & 0x7000L) != 0x0L) {
                SwingEventMonitor.addPopupMenuListener(this);
            }
            this.javaEventMask = javaEventMask;
        }
        
        void removeJavaEventNotification(final long n) {
            final long javaEventMask = this.javaEventMask & ~n;
            if ((this.javaEventMask & 0x6L) != 0x0L && (javaEventMask & 0x6L) == 0x0L) {
                AWTEventMonitor.removeFocusListener(this);
            }
            if ((this.javaEventMask & 0x8L) != 0x0L && (javaEventMask & 0x8L) == 0x0L) {
                SwingEventMonitor.removeCaretListener(this);
            }
            if ((this.javaEventMask & 0x1F0L) == 0x0L && (javaEventMask & 0x1F0L) != 0x0L) {
                AWTEventMonitor.removeMouseListener(this);
            }
            if ((this.javaEventMask & 0xE00L) == 0x0L && (javaEventMask & 0xE00L) != 0x0L) {
                SwingEventMonitor.removeMenuListener(this);
            }
            if ((this.javaEventMask & 0x7000L) == 0x0L && (javaEventMask & 0x7000L) != 0x0L) {
                SwingEventMonitor.removePopupMenuListener(this);
            }
            this.javaEventMask = javaEventMask;
        }
        
        void addAccessibilityEventNotification(final long n) {
            final long accessibilityEventMask = this.accessibilityEventMask | n;
            if ((this.accessibilityEventMask & 0x3FFL) == 0x0L && (accessibilityEventMask & 0x3FFL) != 0x0L) {
                AccessibilityEventMonitor.addPropertyChangeListener(this);
            }
            this.accessibilityEventMask = accessibilityEventMask;
        }
        
        void removeAccessibilityEventNotification(final long n) {
            final long accessibilityEventMask = this.accessibilityEventMask & ~n;
            if ((this.accessibilityEventMask & 0x3FFL) != 0x0L && (accessibilityEventMask & 0x3FFL) == 0x0L) {
                AccessibilityEventMonitor.removePropertyChangeListener(this);
            }
            this.accessibilityEventMask = accessibilityEventMask;
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            this.accessBridge.debugString("[INFO]: propertyChange(" + propertyChangeEvent.toString() + ") called");
            if (propertyChangeEvent != null && (this.accessibilityEventMask & 0x3FFL) != 0x0L) {
                final Object source = propertyChangeEvent.getSource();
                AccessibleContext accessibleContext;
                if (source instanceof AccessibleContext) {
                    accessibleContext = (AccessibleContext)source;
                }
                else {
                    final Accessible accessible = Translator.getAccessible(propertyChangeEvent.getSource());
                    if (accessible == null) {
                        return;
                    }
                    accessibleContext = accessible.getAccessibleContext();
                }
                if (accessibleContext != null) {
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.debugString("[INFO]: AccessibleContext: " + accessibleContext);
                    final String propertyName = propertyChangeEvent.getPropertyName();
                    if (propertyName.compareTo("AccessibleCaret") == 0) {
                        int intValue = 0;
                        int intValue2 = 0;
                        if (propertyChangeEvent.getOldValue() instanceof Integer) {
                            intValue = (int)propertyChangeEvent.getOldValue();
                        }
                        if (propertyChangeEvent.getNewValue() instanceof Integer) {
                            intValue2 = (int)propertyChangeEvent.getNewValue();
                        }
                        this.accessBridge.debugString("[INFO]:  - about to call propertyCaretChange()   old value: " + intValue + "new value: " + intValue2);
                        this.accessBridge.propertyCaretChange(propertyChangeEvent, accessibleContext, intValue, intValue2);
                    }
                    else if (propertyName.compareTo("AccessibleDescription") == 0) {
                        String string = null;
                        String string2 = null;
                        if (propertyChangeEvent.getOldValue() != null) {
                            string = propertyChangeEvent.getOldValue().toString();
                        }
                        if (propertyChangeEvent.getNewValue() != null) {
                            string2 = propertyChangeEvent.getNewValue().toString();
                        }
                        this.accessBridge.debugString("[INFO]:  - about to call propertyDescriptionChange()   old value: " + string + "new value: " + string2);
                        this.accessBridge.propertyDescriptionChange(propertyChangeEvent, accessibleContext, string, string2);
                    }
                    else if (propertyName.compareTo("AccessibleName") == 0) {
                        String string3 = null;
                        String string4 = null;
                        if (propertyChangeEvent.getOldValue() != null) {
                            string3 = propertyChangeEvent.getOldValue().toString();
                        }
                        if (propertyChangeEvent.getNewValue() != null) {
                            string4 = propertyChangeEvent.getNewValue().toString();
                        }
                        this.accessBridge.debugString("[INFO]:  - about to call propertyNameChange()   old value: " + string3 + " new value: " + string4);
                        this.accessBridge.propertyNameChange(propertyChangeEvent, accessibleContext, string3, string4);
                    }
                    else if (propertyName.compareTo("AccessibleSelection") == 0) {
                        this.accessBridge.debugString("[INFO]:  - about to call propertySelectionChange() " + accessibleContext + "   " + Thread.currentThread() + "   " + propertyChangeEvent.getSource());
                        this.accessBridge.propertySelectionChange(propertyChangeEvent, accessibleContext);
                    }
                    else if (propertyName.compareTo("AccessibleState") == 0) {
                        String displayString = null;
                        String displayString2 = null;
                        if (propertyChangeEvent.getOldValue() != null) {
                            displayString = ((AccessibleState)propertyChangeEvent.getOldValue()).toDisplayString(Locale.US);
                        }
                        if (propertyChangeEvent.getNewValue() != null) {
                            displayString2 = ((AccessibleState)propertyChangeEvent.getNewValue()).toDisplayString(Locale.US);
                        }
                        this.accessBridge.debugString("[INFO]:  - about to call propertyStateChange()");
                        this.accessBridge.propertyStateChange(propertyChangeEvent, accessibleContext, displayString, displayString2);
                    }
                    else if (propertyName.compareTo("AccessibleText") == 0) {
                        this.accessBridge.debugString("[INFO]:  - about to call propertyTextChange()");
                        this.accessBridge.propertyTextChange(propertyChangeEvent, accessibleContext);
                    }
                    else if (propertyName.compareTo("AccessibleValue") == 0) {
                        String string5 = null;
                        String string6 = null;
                        if (propertyChangeEvent.getOldValue() != null) {
                            string5 = propertyChangeEvent.getOldValue().toString();
                        }
                        if (propertyChangeEvent.getNewValue() != null) {
                            string6 = propertyChangeEvent.getNewValue().toString();
                        }
                        this.accessBridge.debugString("[INFO]:  - about to call propertyDescriptionChange()");
                        this.accessBridge.propertyValueChange(propertyChangeEvent, accessibleContext, string5, string6);
                    }
                    else if (propertyName.compareTo("AccessibleVisibleData") == 0) {
                        this.accessBridge.propertyVisibleDataChange(propertyChangeEvent, accessibleContext);
                    }
                    else if (propertyName.compareTo("AccessibleChild") == 0) {
                        AccessibleContext accessibleContext2 = null;
                        Object o = null;
                        if (propertyChangeEvent.getOldValue() instanceof AccessibleContext) {
                            accessibleContext2 = (AccessibleContext)propertyChangeEvent.getOldValue();
                            InvocationUtils.registerAccessibleContext(accessibleContext2, AppContext.getAppContext());
                        }
                        if (propertyChangeEvent.getNewValue() instanceof AccessibleContext) {
                            o = propertyChangeEvent.getNewValue();
                            InvocationUtils.registerAccessibleContext((AccessibleContext)o, AppContext.getAppContext());
                        }
                        this.accessBridge.debugString("[INFO]:  - about to call propertyChildChange()   old AC: " + accessibleContext2 + "new AC: " + o);
                        this.accessBridge.propertyChildChange(propertyChangeEvent, accessibleContext, accessibleContext2, (AccessibleContext)o);
                    }
                    else if (propertyName.compareTo("AccessibleActiveDescendant") == 0) {
                        this.handleActiveDescendentEvent(propertyChangeEvent, accessibleContext);
                    }
                }
            }
        }
        
        private void handleActiveDescendentEvent(final PropertyChangeEvent propertyChangeEvent, final AccessibleContext accessibleContext) {
            if (propertyChangeEvent == null || accessibleContext == null) {
                return;
            }
            AccessibleContext accessibleContext2 = null;
            AccessibleContext prevAC = null;
            if (propertyChangeEvent.getOldValue() instanceof Accessible) {
                accessibleContext2 = ((Accessible)propertyChangeEvent.getOldValue()).getAccessibleContext();
            }
            else if (propertyChangeEvent.getOldValue() instanceof Component) {
                final Accessible accessible = Translator.getAccessible(propertyChangeEvent.getOldValue());
                if (accessible != null) {
                    accessibleContext2 = accessible.getAccessibleContext();
                }
            }
            if (accessibleContext2 != null && accessibleContext2.getAccessibleParent() instanceof JTree) {
                accessibleContext2 = this.prevAC;
            }
            if (propertyChangeEvent.getNewValue() instanceof Accessible) {
                prevAC = ((Accessible)propertyChangeEvent.getNewValue()).getAccessibleContext();
            }
            else if (propertyChangeEvent.getNewValue() instanceof Component) {
                final Accessible accessible2 = Translator.getAccessible(propertyChangeEvent.getNewValue());
                if (accessible2 != null) {
                    prevAC = accessible2.getAccessibleContext();
                }
            }
            if (prevAC != null) {
                final Accessible accessibleParent = prevAC.getAccessibleParent();
                if (accessibleParent instanceof JTree) {
                    final JTree tree = (JTree)accessibleParent;
                    prevAC = new AccessibleJTreeNode(tree, tree.getSelectionPath(), null);
                }
            }
            this.prevAC = prevAC;
            this.accessBridge.debugString("[INFO]:   - about to call propertyActiveDescendentChange()   AC: " + accessibleContext + "   old AC: " + accessibleContext2 + "new AC: " + prevAC);
            InvocationUtils.registerAccessibleContext(accessibleContext2, AppContext.getAppContext());
            InvocationUtils.registerAccessibleContext(prevAC, AppContext.getAppContext());
            this.accessBridge.propertyActiveDescendentChange(propertyChangeEvent, accessibleContext, accessibleContext2, prevAC);
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            if (AccessBridge.this.runningOnJDK1_4) {
                this.processFocusGained();
            }
            else if ((this.javaEventMask & 0x2L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(focusEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, SunToolkit.targetToAppContext(focusEvent.getSource()));
                    this.accessBridge.focusGained(focusEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.processFocusGained();
        }
        
        private void processFocusGained() {
            final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner == null) {
                return;
            }
            if (focusOwner instanceof JRootPane) {
                final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                if (selectedPath.length > 1) {
                    final Component component = selectedPath[selectedPath.length - 2].getComponent();
                    final Component component2 = selectedPath[selectedPath.length - 1].getComponent();
                    if (component2 instanceof JPopupMenu) {
                        final FocusEvent focusEvent = new FocusEvent(component, 1004);
                        final AccessibleContext accessibleContext = component.getAccessibleContext();
                        InvocationUtils.registerAccessibleContext(accessibleContext, SunToolkit.targetToAppContext(component));
                        this.accessBridge.focusGained(focusEvent, accessibleContext);
                    }
                    else if (component instanceof JPopupMenu) {
                        final FocusEvent focusEvent2 = new FocusEvent(component2, 1004);
                        final AccessibleContext accessibleContext2 = component2.getAccessibleContext();
                        InvocationUtils.registerAccessibleContext(accessibleContext2, SunToolkit.targetToAppContext(component2));
                        this.accessBridge.debugString("[INFO]:  - about to call focusGained()   AC: " + accessibleContext2);
                        this.accessBridge.focusGained(focusEvent2, accessibleContext2);
                    }
                }
            }
            else if (focusOwner instanceof Accessible) {
                final FocusEvent focusEvent3 = new FocusEvent(focusOwner, 1004);
                final AccessibleContext accessibleContext3 = focusOwner.getAccessibleContext();
                InvocationUtils.registerAccessibleContext(accessibleContext3, SunToolkit.targetToAppContext(focusOwner));
                this.accessBridge.debugString("[INFO]:  - about to call focusGained()   AC: " + accessibleContext3);
                this.accessBridge.focusGained(focusEvent3, accessibleContext3);
            }
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            if (focusEvent != null && (this.javaEventMask & 0x4L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(focusEvent.getSource());
                if (accessible != null) {
                    this.accessBridge.debugString("[INFO]:  - about to call focusLost()   AC: " + accessible.getAccessibleContext());
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.focusLost(focusEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void caretUpdate(final CaretEvent caretEvent) {
            if (caretEvent != null && (this.javaEventMask & 0x8L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(caretEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.caretUpdate(caretEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (mouseEvent != null && (this.javaEventMask & 0x10L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(mouseEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.mouseClicked(mouseEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (mouseEvent != null && (this.javaEventMask & 0x20L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(mouseEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.mouseEntered(mouseEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (mouseEvent != null && (this.javaEventMask & 0x40L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(mouseEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.mouseExited(mouseEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent != null && (this.javaEventMask & 0x80L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(mouseEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.mousePressed(mouseEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (mouseEvent != null && (this.javaEventMask & 0x100L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(mouseEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.mouseReleased(mouseEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void menuCanceled(final MenuEvent menuEvent) {
            if (menuEvent != null && (this.javaEventMask & 0x200L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(menuEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.menuCanceled(menuEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void menuDeselected(final MenuEvent menuEvent) {
            if (menuEvent != null && (this.javaEventMask & 0x400L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(menuEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.menuDeselected(menuEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void menuSelected(final MenuEvent menuEvent) {
            if (menuEvent != null && (this.javaEventMask & 0x800L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(menuEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.menuSelected(menuEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
            if (popupMenuEvent != null && (this.javaEventMask & 0x1000L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(popupMenuEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.popupMenuCanceled(popupMenuEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
            if (popupMenuEvent != null && (this.javaEventMask & 0x2000L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(popupMenuEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.popupMenuWillBecomeInvisible(popupMenuEvent, accessibleContext);
                }
            }
        }
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
            if (popupMenuEvent != null && (this.javaEventMask & 0x4000L) != 0x0L) {
                final Accessible accessible = Translator.getAccessible(popupMenuEvent.getSource());
                if (accessible != null) {
                    final AccessibleContext accessibleContext = accessible.getAccessibleContext();
                    InvocationUtils.registerAccessibleContext(accessibleContext, AppContext.getAppContext());
                    this.accessBridge.popupMenuWillBecomeVisible(popupMenuEvent, accessibleContext);
                }
            }
        }
    }
    
    private class AccessibleJTreeNode extends AccessibleContext implements Accessible, AccessibleComponent, AccessibleSelection, AccessibleAction
    {
        private JTree tree;
        private TreeModel treeModel;
        private Object obj;
        private TreePath path;
        private Accessible accessibleParent;
        private int index;
        private boolean isLeaf;
        
        AccessibleJTreeNode(final JTree tree, final TreePath path, final Accessible accessibleParent) {
            this.tree = null;
            this.treeModel = null;
            this.obj = null;
            this.path = null;
            this.accessibleParent = null;
            this.index = 0;
            this.isLeaf = false;
            this.tree = tree;
            this.path = path;
            this.accessibleParent = accessibleParent;
            if (tree != null) {
                this.treeModel = tree.getModel();
            }
            if (path != null) {
                this.obj = path.getLastPathComponent();
                if (this.treeModel != null && this.obj != null) {
                    this.isLeaf = this.treeModel.isLeaf(this.obj);
                }
            }
            AccessBridge.this.debugString("[INFO]: AccessibleJTreeNode: name = " + this.getAccessibleName() + "; TreePath = " + path + "; parent = " + accessibleParent);
        }
        
        private TreePath getChildTreePath(final int n) {
            if (n < 0 || n >= this.getAccessibleChildrenCount() || this.path == null || this.treeModel == null) {
                return null;
            }
            final Object child = this.treeModel.getChild(this.obj, n);
            final Object[] path = this.path.getPath();
            final Object[] array = new Object[path.length + 1];
            System.arraycopy(path, 0, array, 0, path.length);
            array[array.length - 1] = child;
            return new TreePath(array);
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            return this;
        }
        
        private AccessibleContext getCurrentAccessibleContext() {
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent instanceof Accessible) {
                return currentComponent.getAccessibleContext();
            }
            return null;
        }
        
        private Component getCurrentComponent() {
            AccessBridge.this.debugString("[INFO]: AccessibleJTreeNode: getCurrentComponent");
            if (this.tree != null && this.tree.isVisible(this.path)) {
                final TreeCellRenderer cellRenderer = this.tree.getCellRenderer();
                if (cellRenderer == null) {
                    AccessBridge.this.debugString("[WARN]:  returning null 1");
                    return null;
                }
                final TreeUI ui = this.tree.getUI();
                if (ui != null) {
                    final Component treeCellRendererComponent = cellRenderer.getTreeCellRendererComponent(this.tree, this.obj, this.tree.isPathSelected(this.path), this.tree.isExpanded(this.path), this.isLeaf, ui.getRowForPath(this.tree, this.path), false);
                    AccessBridge.this.debugString("[INFO]:   returning = " + treeCellRendererComponent.getClass());
                    return treeCellRendererComponent;
                }
            }
            AccessBridge.this.debugString("[WARN]:  returning null 2");
            return null;
        }
        
        @Override
        public String getAccessibleName() {
            AccessBridge.this.debugString("[INFO]: AccessibleJTreeNode: getAccessibleName");
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                final String accessibleName = currentAccessibleContext.getAccessibleName();
                if (accessibleName != null && !accessibleName.isEmpty()) {
                    final String accessibleName2 = currentAccessibleContext.getAccessibleName();
                    AccessBridge.this.debugString("[INFO]:     returning " + accessibleName2);
                    return accessibleName2;
                }
                return null;
            }
            else {
                if (this.accessibleName != null && this.accessibleName.isEmpty()) {
                    return this.accessibleName;
                }
                return null;
            }
        }
        
        @Override
        public void setAccessibleName(final String s) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                currentAccessibleContext.setAccessibleName(s);
            }
            else {
                super.setAccessibleName(s);
            }
        }
        
        @Override
        public String getAccessibleDescription() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                return currentAccessibleContext.getAccessibleDescription();
            }
            return super.getAccessibleDescription();
        }
        
        @Override
        public void setAccessibleDescription(final String s) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                currentAccessibleContext.setAccessibleDescription(s);
            }
            else {
                super.setAccessibleDescription(s);
            }
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                return currentAccessibleContext.getAccessibleRole();
            }
            return AccessibleRole.UNKNOWN;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            if (this.tree == null) {
                return null;
            }
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            final int rowForPath = this.tree.getUI().getRowForPath(this.tree, this.path);
            final int leadSelectionRow = this.tree.getLeadSelectionRow();
            AccessibleStateSet accessibleStateSet;
            if (currentAccessibleContext != null) {
                accessibleStateSet = currentAccessibleContext.getAccessibleStateSet();
            }
            else {
                accessibleStateSet = new AccessibleStateSet();
            }
            if (this.isShowing()) {
                accessibleStateSet.add(AccessibleState.SHOWING);
            }
            else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
                accessibleStateSet.remove(AccessibleState.SHOWING);
            }
            if (this.isVisible()) {
                accessibleStateSet.add(AccessibleState.VISIBLE);
            }
            else if (accessibleStateSet.contains(AccessibleState.VISIBLE)) {
                accessibleStateSet.remove(AccessibleState.VISIBLE);
            }
            if (this.tree.isPathSelected(this.path)) {
                accessibleStateSet.add(AccessibleState.SELECTED);
            }
            if (leadSelectionRow == rowForPath) {
                accessibleStateSet.add(AccessibleState.ACTIVE);
            }
            if (!this.isLeaf) {
                accessibleStateSet.add(AccessibleState.EXPANDABLE);
            }
            if (this.tree.isExpanded(this.path)) {
                accessibleStateSet.add(AccessibleState.EXPANDED);
            }
            else {
                accessibleStateSet.add(AccessibleState.COLLAPSED);
            }
            if (this.tree.isEditable()) {
                accessibleStateSet.add(AccessibleState.EDITABLE);
            }
            return accessibleStateSet;
        }
        
        @Override
        public Accessible getAccessibleParent() {
            if (this.accessibleParent == null && this.path != null) {
                final Object[] path = this.path.getPath();
                if (path.length > 1) {
                    final Object o = path[path.length - 2];
                    if (this.treeModel != null) {
                        this.index = this.treeModel.getIndexOfChild(o, this.obj);
                    }
                    final Object[] array = new Object[path.length - 1];
                    System.arraycopy(path, 0, array, 0, path.length - 1);
                    this.setAccessibleParent(this.accessibleParent = new AccessibleJTreeNode(this.tree, new TreePath(array), null));
                }
                else if (this.treeModel != null) {
                    this.accessibleParent = this.tree;
                    this.index = 0;
                    this.setAccessibleParent(this.accessibleParent);
                }
            }
            return this.accessibleParent;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            if (this.accessibleParent == null) {
                this.getAccessibleParent();
            }
            if (this.path != null) {
                final Object[] path = this.path.getPath();
                if (path.length > 1) {
                    final Object o = path[path.length - 2];
                    if (this.treeModel != null) {
                        this.index = this.treeModel.getIndexOfChild(o, this.obj);
                    }
                }
            }
            return this.index;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            if (this.obj != null && this.treeModel != null) {
                return this.treeModel.getChildCount(this.obj);
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (n < 0 || n >= this.getAccessibleChildrenCount() || this.path == null || this.treeModel == null) {
                return null;
            }
            final Object child = this.treeModel.getChild(this.obj, n);
            final Object[] path = this.path.getPath();
            final Object[] array = new Object[path.length + 1];
            System.arraycopy(path, 0, array, 0, path.length);
            array[array.length - 1] = child;
            return new AccessibleJTreeNode(this.tree, new TreePath(array), this);
        }
        
        @Override
        public Locale getLocale() {
            if (this.tree == null) {
                return null;
            }
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                return currentAccessibleContext.getLocale();
            }
            return this.tree.getLocale();
        }
        
        @Override
        public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                currentAccessibleContext.addPropertyChangeListener(propertyChangeListener);
            }
            else {
                super.addPropertyChangeListener(propertyChangeListener);
            }
        }
        
        @Override
        public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                currentAccessibleContext.removePropertyChangeListener(propertyChangeListener);
            }
            else {
                super.removePropertyChangeListener(propertyChangeListener);
            }
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public AccessibleComponent getAccessibleComponent() {
            return this;
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            if (this.getCurrentAccessibleContext() != null && this.isLeaf) {
                return this.getCurrentAccessibleContext().getAccessibleSelection();
            }
            return this;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            if (this.getCurrentAccessibleContext() != null) {
                return this.getCurrentAccessibleContext().getAccessibleText();
            }
            return null;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            if (this.getCurrentAccessibleContext() != null) {
                return this.getCurrentAccessibleContext().getAccessibleValue();
            }
            return null;
        }
        
        @Override
        public Color getBackground() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getBackground();
            }
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent != null) {
                return currentComponent.getBackground();
            }
            return null;
        }
        
        @Override
        public void setBackground(final Color color) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setBackground(color);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setBackground(color);
                }
            }
        }
        
        @Override
        public Color getForeground() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getForeground();
            }
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent != null) {
                return currentComponent.getForeground();
            }
            return null;
        }
        
        @Override
        public void setForeground(final Color color) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setForeground(color);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setForeground(color);
                }
            }
        }
        
        @Override
        public Cursor getCursor() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getCursor();
            }
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent != null) {
                return currentComponent.getCursor();
            }
            final Accessible accessibleParent = this.getAccessibleParent();
            if (accessibleParent instanceof AccessibleComponent) {
                return ((AccessibleComponent)accessibleParent).getCursor();
            }
            return null;
        }
        
        @Override
        public void setCursor(final Cursor cursor) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setCursor(cursor);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setCursor(cursor);
                }
            }
        }
        
        @Override
        public Font getFont() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getFont();
            }
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent != null) {
                return currentComponent.getFont();
            }
            return null;
        }
        
        @Override
        public void setFont(final Font font) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setFont(font);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setFont(font);
                }
            }
        }
        
        @Override
        public FontMetrics getFontMetrics(final Font font) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getFontMetrics(font);
            }
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent != null) {
                return currentComponent.getFontMetrics(font);
            }
            return null;
        }
        
        @Override
        public boolean isEnabled() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).isEnabled();
            }
            final Component currentComponent = this.getCurrentComponent();
            return currentComponent != null && currentComponent.isEnabled();
        }
        
        @Override
        public void setEnabled(final boolean b) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setEnabled(b);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setEnabled(b);
                }
            }
        }
        
        @Override
        public boolean isVisible() {
            if (this.tree == null) {
                return false;
            }
            final Rectangle pathBounds = this.tree.getPathBounds(this.path);
            final Rectangle visibleRect = this.tree.getVisibleRect();
            return pathBounds != null && visibleRect != null && visibleRect.intersects(pathBounds);
        }
        
        @Override
        public void setVisible(final boolean b) {
        }
        
        @Override
        public boolean isShowing() {
            return this.tree.isShowing() && this.isVisible();
        }
        
        @Override
        public boolean contains(final Point point) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getBounds().contains(point);
            }
            final Component currentComponent = this.getCurrentComponent();
            if (currentComponent != null) {
                return currentComponent.getBounds().contains(point);
            }
            return this.getBounds().contains(point);
        }
        
        @Override
        public Point getLocationOnScreen() {
            if (this.tree == null) {
                return null;
            }
            final Point locationOnScreen = this.tree.getLocationOnScreen();
            final Rectangle pathBounds = this.tree.getPathBounds(this.path);
            if (locationOnScreen != null && pathBounds != null) {
                final Point point = new Point(pathBounds.x, pathBounds.y);
                point.translate(locationOnScreen.x, locationOnScreen.y);
                return point;
            }
            return null;
        }
        
        private Point getLocationInJTree() {
            final Rectangle pathBounds = this.tree.getPathBounds(this.path);
            if (pathBounds != null) {
                return pathBounds.getLocation();
            }
            return null;
        }
        
        @Override
        public Point getLocation() {
            final Rectangle bounds = this.getBounds();
            if (bounds != null) {
                return bounds.getLocation();
            }
            return null;
        }
        
        @Override
        public void setLocation(final Point point) {
        }
        
        @Override
        public Rectangle getBounds() {
            if (this.tree == null) {
                return null;
            }
            final Rectangle pathBounds = this.tree.getPathBounds(this.path);
            final Accessible accessibleParent = this.getAccessibleParent();
            if (accessibleParent instanceof AccessibleJTreeNode) {
                final Point locationInJTree = ((AccessibleJTreeNode)accessibleParent).getLocationInJTree();
                if (locationInJTree == null || pathBounds == null) {
                    return null;
                }
                pathBounds.translate(-locationInJTree.x, -locationInJTree.y);
            }
            return pathBounds;
        }
        
        @Override
        public void setBounds(final Rectangle rectangle) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setBounds(rectangle);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setBounds(rectangle);
                }
            }
        }
        
        @Override
        public Dimension getSize() {
            return this.getBounds().getSize();
        }
        
        @Override
        public void setSize(final Dimension dimension) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).setSize(dimension);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.setSize(dimension);
                }
            }
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).getAccessibleAt(point);
            }
            return null;
        }
        
        @Override
        public boolean isFocusTraversable() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                return ((AccessibleComponent)currentAccessibleContext).isFocusTraversable();
            }
            final Component currentComponent = this.getCurrentComponent();
            return currentComponent != null && currentComponent.isFocusable();
        }
        
        @Override
        public void requestFocus() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).requestFocus();
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.requestFocus();
                }
            }
        }
        
        @Override
        public void addFocusListener(final FocusListener focusListener) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).addFocusListener(focusListener);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.addFocusListener(focusListener);
                }
            }
        }
        
        @Override
        public void removeFocusListener(final FocusListener focusListener) {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext instanceof AccessibleComponent) {
                ((AccessibleComponent)currentAccessibleContext).removeFocusListener(focusListener);
            }
            else {
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    currentComponent.removeFocusListener(focusListener);
                }
            }
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            int n = 0;
            for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                if (this.tree.isPathSelected(this.getChildTreePath(i))) {
                    ++n;
                }
            }
            return n;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            final int accessibleChildrenCount = this.getAccessibleChildrenCount();
            if (n < 0 || n >= accessibleChildrenCount) {
                return null;
            }
            for (int n2 = 0, n3 = 0; n3 < accessibleChildrenCount && n >= n2; ++n3) {
                final TreePath childTreePath = this.getChildTreePath(n3);
                if (this.tree.isPathSelected(childTreePath)) {
                    if (n2 == n) {
                        return new AccessibleJTreeNode(this.tree, childTreePath, this);
                    }
                    ++n2;
                }
            }
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            final int accessibleChildrenCount = this.getAccessibleChildrenCount();
            return n >= 0 && n < accessibleChildrenCount && this.tree.isPathSelected(this.getChildTreePath(n));
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
            if (this.tree == null) {
                return;
            }
            if (this.tree.getModel() != null && n >= 0 && n < this.getAccessibleChildrenCount()) {
                this.tree.addSelectionPath(this.getChildTreePath(n));
            }
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            if (this.tree == null) {
                return;
            }
            if (this.tree.getModel() != null && n >= 0 && n < this.getAccessibleChildrenCount()) {
                this.tree.removeSelectionPath(this.getChildTreePath(n));
            }
        }
        
        @Override
        public void clearAccessibleSelection() {
            for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                this.removeAccessibleSelection(i);
            }
        }
        
        @Override
        public void selectAllAccessibleSelection() {
            if (this.tree == null) {
                return;
            }
            if (this.tree.getModel() != null) {
                for (int accessibleChildrenCount = this.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                    this.tree.addSelectionPath(this.getChildTreePath(i));
                }
            }
        }
        
        @Override
        public int getAccessibleActionCount() {
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (currentAccessibleContext != null) {
                final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                if (accessibleAction != null) {
                    return accessibleAction.getAccessibleActionCount() + (this.isLeaf ? 0 : 1);
                }
            }
            return this.isLeaf ? 0 : 1;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            if (n < 0 || n >= this.getAccessibleActionCount()) {
                return null;
            }
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (n == 0) {
                return "toggle expand";
            }
            if (currentAccessibleContext != null) {
                final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                if (accessibleAction != null) {
                    return accessibleAction.getAccessibleActionDescription(n - 1);
                }
            }
            return null;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            if (n < 0 || n >= this.getAccessibleActionCount()) {
                return false;
            }
            final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
            if (n == 0) {
                if (this.tree.isExpanded(this.path)) {
                    this.tree.collapsePath(this.path);
                }
                else {
                    this.tree.expandPath(this.path);
                }
                return true;
            }
            if (currentAccessibleContext != null) {
                final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                if (accessibleAction != null) {
                    return accessibleAction.doAccessibleAction(n - 1);
                }
            }
            return false;
        }
    }
    
    private static class InvocationUtils
    {
        public static <T> T invokeAndWait(final Callable<T> callable, final AccessibleExtendedTable accessibleExtendedTable) {
            if (accessibleExtendedTable instanceof AccessibleContext) {
                return invokeAndWait(callable, (AccessibleContext)accessibleExtendedTable);
            }
            throw new RuntimeException("Unmapped AccessibleContext used to dispatch event: " + accessibleExtendedTable);
        }
        
        public static <T> T invokeAndWait(final Callable<T> callable, final Accessible accessible) {
            if (accessible instanceof Component) {
                return invokeAndWait(callable, (Component)accessible);
            }
            if (accessible instanceof AccessibleContext) {
                return invokeAndWait(callable, (AccessibleContext)accessible);
            }
            throw new RuntimeException("Unmapped Accessible used to dispatch event: " + accessible);
        }
        
        public static <T> T invokeAndWait(final Callable<T> callable, final Component component) {
            return invokeAndWait(callable, SunToolkit.targetToAppContext(component));
        }
        
        public static <T> T invokeAndWait(final Callable<T> callable, final AccessibleContext accessibleContext) {
            final AppContext appContext = AWTAccessor.getAccessibleContextAccessor().getAppContext(accessibleContext);
            if (appContext != null) {
                return invokeAndWait(callable, appContext);
            }
            if (accessibleContext instanceof Translator) {
                final Object source = ((Translator)accessibleContext).getSource();
                if (source instanceof Component) {
                    return invokeAndWait(callable, (Component)source);
                }
            }
            throw new RuntimeException("Unmapped AccessibleContext used to dispatch event: " + accessibleContext);
        }
        
        private static <T> T invokeAndWait(final Callable<T> callable, final AppContext appContext) {
            final CallableWrapper callableWrapper = new CallableWrapper((Callable<T>)callable);
            try {
                invokeAndWait(callableWrapper, appContext);
                final Object result = callableWrapper.getResult();
                updateAppContextMap(result, appContext);
                return (T)result;
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        private static void invokeAndWait(final Runnable runnable, final AppContext appContext) throws InterruptedException, InvocationTargetException {
            final EventQueue systemEventQueueImplPP = SunToolkit.getSystemEventQueueImplPP(appContext);
            final Object o = new Object();
            final InvocationEvent invocationEvent = new InvocationEvent(Toolkit.getDefaultToolkit(), runnable, o, true);
            synchronized (o) {
                systemEventQueueImplPP.postEvent(invocationEvent);
                o.wait();
            }
            final Throwable throwable = invocationEvent.getThrowable();
            if (throwable != null) {
                throw new InvocationTargetException(throwable);
            }
        }
        
        public static void registerAccessibleContext(final AccessibleContext accessibleContext, final AppContext appContext) {
            if (accessibleContext != null) {
                AWTAccessor.getAccessibleContextAccessor().setAppContext(accessibleContext, appContext);
            }
        }
        
        private static <T> void updateAppContextMap(final T t, final AppContext appContext) {
            if (t instanceof AccessibleContext) {
                registerAccessibleContext((AccessibleContext)t, appContext);
            }
        }
        
        private static class CallableWrapper<T> implements Runnable
        {
            private final Callable<T> callable;
            private volatile T object;
            private Exception e;
            
            CallableWrapper(final Callable<T> callable) {
                this.callable = callable;
            }
            
            @Override
            public void run() {
                try {
                    if (this.callable != null) {
                        this.object = this.callable.call();
                    }
                }
                catch (final Exception e) {
                    this.e = e;
                }
            }
            
            T getResult() throws Exception {
                if (this.e != null) {
                    throw this.e;
                }
                return this.object;
            }
        }
    }
    
    private interface NativeWindowHandler
    {
        Accessible getAccessibleFromNativeWindowHandle(final int p0);
    }
}
