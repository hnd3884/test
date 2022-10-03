package javax.swing.plaf.basic;

import java.awt.event.KeyEvent;
import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.util.Iterator;
import java.applet.Applet;
import javax.swing.JComboBox;
import sun.awt.UngrabEvent;
import java.awt.AWTEvent;
import javax.swing.event.ChangeEvent;
import sun.awt.SunToolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.awt.event.ComponentListener;
import java.awt.event.AWTEventListener;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import javax.swing.JMenuItem;
import java.util.Collection;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.PopupMenuEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.event.ChangeListener;
import javax.swing.MenuSelectionManager;
import sun.awt.AppContext;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.JPopupMenu;
import javax.swing.plaf.PopupMenuUI;

public class BasicPopupMenuUI extends PopupMenuUI
{
    static final StringBuilder MOUSE_GRABBER_KEY;
    static final StringBuilder MENU_KEYBOARD_HELPER_KEY;
    protected JPopupMenu popupMenu;
    private transient PopupMenuListener popupMenuListener;
    private MenuKeyListener menuKeyListener;
    private static boolean checkedUnpostPopup;
    private static boolean unpostPopup;
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicPopupMenuUI();
    }
    
    public BasicPopupMenuUI() {
        this.popupMenu = null;
        this.popupMenuListener = null;
        this.menuKeyListener = null;
        BasicLookAndFeel.needsEventHelper = true;
        final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel instanceof BasicLookAndFeel) {
            ((BasicLookAndFeel)lookAndFeel).installAWTEventListener();
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.popupMenu = (JPopupMenu)component;
        this.installDefaults();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    public void installDefaults() {
        if (this.popupMenu.getLayout() == null || this.popupMenu.getLayout() instanceof UIResource) {
            this.popupMenu.setLayout(new DefaultMenuLayout(this.popupMenu, 1));
        }
        LookAndFeel.installProperty(this.popupMenu, "opaque", Boolean.TRUE);
        LookAndFeel.installBorder(this.popupMenu, "PopupMenu.border");
        LookAndFeel.installColorsAndFont(this.popupMenu, "PopupMenu.background", "PopupMenu.foreground", "PopupMenu.font");
    }
    
    protected void installListeners() {
        if (this.popupMenuListener == null) {
            this.popupMenuListener = new BasicPopupMenuListener();
        }
        this.popupMenu.addPopupMenuListener(this.popupMenuListener);
        if (this.menuKeyListener == null) {
            this.menuKeyListener = new BasicMenuKeyListener();
        }
        this.popupMenu.addMenuKeyListener(this.menuKeyListener);
        final AppContext appContext = AppContext.getAppContext();
        synchronized (BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
            if (appContext.get(BasicPopupMenuUI.MOUSE_GRABBER_KEY) == null) {
                appContext.put(BasicPopupMenuUI.MOUSE_GRABBER_KEY, new MouseGrabber());
            }
        }
        synchronized (BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
            if (appContext.get(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) == null) {
                final MenuKeyboardHelper menuKeyboardHelper = new MenuKeyboardHelper();
                appContext.put(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY, menuKeyboardHelper);
                MenuSelectionManager.defaultManager().addChangeListener(menuKeyboardHelper);
            }
        }
    }
    
    protected void installKeyboardActions() {
    }
    
    static InputMap getInputMap(final JPopupMenu popupMenu, final JComponent component) {
        InputMap componentInputMap = null;
        final Object[] array = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings");
        if (array != null) {
            componentInputMap = LookAndFeel.makeComponentInputMap(component, array);
            if (!popupMenu.getComponentOrientation().isLeftToRight()) {
                final Object[] array2 = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft");
                if (array2 != null) {
                    final ComponentInputMap componentInputMap2 = LookAndFeel.makeComponentInputMap(component, array2);
                    componentInputMap2.setParent(componentInputMap);
                    componentInputMap = componentInputMap2;
                }
            }
        }
        return componentInputMap;
    }
    
    static ActionMap getActionMap() {
        return LazyActionMap.getActionMap(BasicPopupMenuUI.class, "PopupMenu.actionMap");
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("cancel"));
        lazyActionMap.put(new Actions("selectNext"));
        lazyActionMap.put(new Actions("selectPrevious"));
        lazyActionMap.put(new Actions("selectParent"));
        lazyActionMap.put(new Actions("selectChild"));
        lazyActionMap.put(new Actions("return"));
        BasicLookAndFeel.installAudioActionMap(lazyActionMap);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        this.popupMenu = null;
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.popupMenu);
    }
    
    protected void uninstallListeners() {
        if (this.popupMenuListener != null) {
            this.popupMenu.removePopupMenuListener(this.popupMenuListener);
        }
        if (this.menuKeyListener != null) {
            this.popupMenu.removeMenuKeyListener(this.menuKeyListener);
        }
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.popupMenu, null);
        SwingUtilities.replaceUIInputMap(this.popupMenu, 2, null);
    }
    
    static MenuElement getFirstPopup() {
        final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
        MenuElement menuElement = null;
        for (int n = 0; menuElement == null && n < selectedPath.length; ++n) {
            if (selectedPath[n] instanceof JPopupMenu) {
                menuElement = selectedPath[n];
            }
        }
        return menuElement;
    }
    
    static JPopupMenu getLastPopup() {
        final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
        JPopupMenu popupMenu = null;
        for (int n = selectedPath.length - 1; popupMenu == null && n >= 0; --n) {
            if (selectedPath[n] instanceof JPopupMenu) {
                popupMenu = (JPopupMenu)selectedPath[n];
            }
        }
        return popupMenu;
    }
    
    static List<JPopupMenu> getPopups() {
        final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
        final ArrayList list = new ArrayList(selectedPath.length);
        for (final MenuElement menuElement : selectedPath) {
            if (menuElement instanceof JPopupMenu) {
                list.add((Object)menuElement);
            }
        }
        return (List<JPopupMenu>)list;
    }
    
    @Override
    public boolean isPopupTrigger(final MouseEvent mouseEvent) {
        return mouseEvent.getID() == 502 && (mouseEvent.getModifiers() & 0x4) != 0x0;
    }
    
    private static boolean checkInvokerEqual(final MenuElement menuElement, final MenuElement menuElement2) {
        Component component = menuElement.getComponent();
        Component component2 = menuElement2.getComponent();
        if (component instanceof JPopupMenu) {
            component = ((JPopupMenu)component).getInvoker();
        }
        if (component2 instanceof JPopupMenu) {
            component2 = ((JPopupMenu)component2).getInvoker();
        }
        return component == component2;
    }
    
    private static MenuElement nextEnabledChild(final MenuElement[] array, final int n, final int n2) {
        for (int i = n; i <= n2; ++i) {
            if (array[i] != null) {
                final Component component = array[i].getComponent();
                if (component != null && (component.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) && component.isVisible()) {
                    return array[i];
                }
            }
        }
        return null;
    }
    
    private static MenuElement previousEnabledChild(final MenuElement[] array, final int n, final int n2) {
        for (int i = n; i >= n2; --i) {
            if (array[i] != null) {
                final Component component = array[i].getComponent();
                if (component != null && (component.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) && component.isVisible()) {
                    return array[i];
                }
            }
        }
        return null;
    }
    
    static MenuElement findEnabledChild(final MenuElement[] array, final int n, final boolean b) {
        MenuElement menuElement;
        if (b) {
            menuElement = nextEnabledChild(array, n + 1, array.length - 1);
            if (menuElement == null) {
                menuElement = nextEnabledChild(array, 0, n - 1);
            }
        }
        else {
            menuElement = previousEnabledChild(array, n - 1, 0);
            if (menuElement == null) {
                menuElement = previousEnabledChild(array, array.length - 1, n + 1);
            }
        }
        return menuElement;
    }
    
    static MenuElement findEnabledChild(final MenuElement[] array, final MenuElement menuElement, final boolean b) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == menuElement) {
                return findEnabledChild(array, i, b);
            }
        }
        return null;
    }
    
    static {
        MOUSE_GRABBER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MouseGrabber");
        MENU_KEYBOARD_HELPER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MenuKeyboardHelper");
    }
    
    private class BasicPopupMenuListener implements PopupMenuListener
    {
        @Override
        public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
        }
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
            BasicLookAndFeel.playSound((JComponent)popupMenuEvent.getSource(), "PopupMenu.popupSound");
        }
    }
    
    private class BasicMenuKeyListener implements MenuKeyListener
    {
        MenuElement menuToOpen;
        
        private BasicMenuKeyListener() {
            this.menuToOpen = null;
        }
        
        @Override
        public void menuKeyTyped(final MenuKeyEvent menuKeyEvent) {
            if (this.menuToOpen != null) {
                final JPopupMenu popupMenu = ((JMenu)this.menuToOpen).getPopupMenu();
                final MenuElement enabledChild = BasicPopupMenuUI.findEnabledChild(popupMenu.getSubElements(), -1, true);
                final ArrayList list = new ArrayList((Collection<? extends E>)Arrays.asList(menuKeyEvent.getPath()));
                list.add(this.menuToOpen);
                list.add(popupMenu);
                if (enabledChild != null) {
                    list.add(enabledChild);
                }
                MenuSelectionManager.defaultManager().setSelectedPath(list.toArray(new MenuElement[0]));
                menuKeyEvent.consume();
            }
            this.menuToOpen = null;
        }
        
        @Override
        public void menuKeyPressed(final MenuKeyEvent menuKeyEvent) {
            final char keyChar = menuKeyEvent.getKeyChar();
            if (!Character.isLetterOrDigit(keyChar)) {
                return;
            }
            final MenuSelectionManager menuSelectionManager = menuKeyEvent.getMenuSelectionManager();
            final MenuElement[] path = menuKeyEvent.getPath();
            final MenuElement[] subElements = BasicPopupMenuUI.this.popupMenu.getSubElements();
            int n = -1;
            int n2 = 0;
            int n3 = -1;
            int[] array = null;
            for (int i = 0; i < subElements.length; ++i) {
                if (subElements[i] instanceof JMenuItem) {
                    final JMenuItem menuItem = (JMenuItem)subElements[i];
                    final int mnemonic = menuItem.getMnemonic();
                    if (menuItem.isEnabled() && menuItem.isVisible() && this.lower(keyChar) == this.lower(mnemonic)) {
                        if (n2 == 0) {
                            n3 = i;
                            ++n2;
                        }
                        else {
                            if (array == null) {
                                array = new int[subElements.length];
                                array[0] = n3;
                            }
                            array[n2++] = i;
                        }
                    }
                    if (menuItem.isArmed() || menuItem.isSelected()) {
                        n = n2 - 1;
                    }
                }
            }
            if (n2 != 0) {
                if (n2 == 1) {
                    final JMenuItem menuToOpen = (JMenuItem)subElements[n3];
                    if (menuToOpen instanceof JMenu) {
                        this.menuToOpen = menuToOpen;
                    }
                    else if (menuToOpen.isEnabled()) {
                        menuSelectionManager.clearSelectedPath();
                        menuToOpen.doClick();
                    }
                    menuKeyEvent.consume();
                }
                else {
                    final MenuElement menuElement = subElements[array[(n + 1) % n2]];
                    final MenuElement[] selectedPath = new MenuElement[path.length + 1];
                    System.arraycopy(path, 0, selectedPath, 0, path.length);
                    selectedPath[path.length] = menuElement;
                    menuSelectionManager.setSelectedPath(selectedPath);
                    menuKeyEvent.consume();
                }
            }
        }
        
        @Override
        public void menuKeyReleased(final MenuKeyEvent menuKeyEvent) {
        }
        
        private char lower(final char c) {
            return Character.toLowerCase(c);
        }
        
        private char lower(final int n) {
            return Character.toLowerCase((char)n);
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String CANCEL = "cancel";
        private static final String SELECT_NEXT = "selectNext";
        private static final String SELECT_PREVIOUS = "selectPrevious";
        private static final String SELECT_PARENT = "selectParent";
        private static final String SELECT_CHILD = "selectChild";
        private static final String RETURN = "return";
        private static final boolean FORWARD = true;
        private static final boolean BACKWARD = false;
        private static final boolean PARENT = false;
        private static final boolean CHILD = true;
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final String name = this.getName();
            if (name == "cancel") {
                this.cancel();
            }
            else if (name == "selectNext") {
                this.selectItem(true);
            }
            else if (name == "selectPrevious") {
                this.selectItem(false);
            }
            else if (name == "selectParent") {
                this.selectParentChild(false);
            }
            else if (name == "selectChild") {
                this.selectParentChild(true);
            }
            else if (name == "return") {
                this.doReturn();
            }
        }
        
        private void doReturn() {
            final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner != null && !(focusOwner instanceof JRootPane)) {
                return;
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final MenuElement[] selectedPath = defaultManager.getSelectedPath();
            if (selectedPath.length > 0) {
                final MenuElement menuElement = selectedPath[selectedPath.length - 1];
                if (menuElement instanceof JMenu) {
                    final MenuElement[] selectedPath2 = new MenuElement[selectedPath.length + 1];
                    System.arraycopy(selectedPath, 0, selectedPath2, 0, selectedPath.length);
                    selectedPath2[selectedPath.length] = ((JMenu)menuElement).getPopupMenu();
                    defaultManager.setSelectedPath(selectedPath2);
                }
                else if (menuElement instanceof JMenuItem) {
                    final JMenuItem menuItem = (JMenuItem)menuElement;
                    if (menuItem.getUI() instanceof BasicMenuItemUI) {
                        ((BasicMenuItemUI)menuItem.getUI()).doClick(defaultManager);
                    }
                    else {
                        defaultManager.clearSelectedPath();
                        menuItem.doClick(0);
                    }
                }
            }
        }
        
        private void selectParentChild(final boolean b) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final MenuElement[] selectedPath = defaultManager.getSelectedPath();
            final int length = selectedPath.length;
            if (!b) {
                int n = length - 1;
                if (length > 2 && (selectedPath[n] instanceof JPopupMenu || selectedPath[--n] instanceof JPopupMenu) && !((JMenu)selectedPath[n - 1]).isTopLevelMenu()) {
                    final MenuElement[] selectedPath2 = new MenuElement[n];
                    System.arraycopy(selectedPath, 0, selectedPath2, 0, n);
                    defaultManager.setSelectedPath(selectedPath2);
                    return;
                }
            }
            else if (length > 0 && selectedPath[length - 1] instanceof JMenu && !((JMenu)selectedPath[length - 1]).isTopLevelMenu()) {
                final JPopupMenu popupMenu = ((JMenu)selectedPath[length - 1]).getPopupMenu();
                final MenuElement enabledChild = BasicPopupMenuUI.findEnabledChild(popupMenu.getSubElements(), -1, true);
                MenuElement[] selectedPath3;
                if (enabledChild == null) {
                    selectedPath3 = new MenuElement[length + 1];
                }
                else {
                    selectedPath3 = new MenuElement[length + 2];
                    selectedPath3[length + 1] = enabledChild;
                }
                System.arraycopy(selectedPath, 0, selectedPath3, 0, length);
                selectedPath3[length] = popupMenu;
                defaultManager.setSelectedPath(selectedPath3);
                return;
            }
            if (length > 1 && selectedPath[0] instanceof JMenuBar) {
                final MenuElement menuElement = selectedPath[1];
                final MenuElement enabledChild2 = BasicPopupMenuUI.findEnabledChild(selectedPath[0].getSubElements(), menuElement, b);
                if (enabledChild2 != null && enabledChild2 != menuElement) {
                    MenuElement[] selectedPath4;
                    if (length == 2) {
                        selectedPath4 = new MenuElement[] { selectedPath[0], enabledChild2 };
                    }
                    else {
                        selectedPath4 = new MenuElement[] { selectedPath[0], enabledChild2, ((JMenu)enabledChild2).getPopupMenu() };
                    }
                    defaultManager.setSelectedPath(selectedPath4);
                }
            }
        }
        
        private void selectItem(final boolean b) {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final MenuElement[] selectedPath = defaultManager.getSelectedPath();
            if (selectedPath.length == 0) {
                return;
            }
            final int length = selectedPath.length;
            if (length == 1 && selectedPath[0] instanceof JPopupMenu) {
                final JPopupMenu popupMenu = (JPopupMenu)selectedPath[0];
                defaultManager.setSelectedPath(new MenuElement[] { popupMenu, BasicPopupMenuUI.findEnabledChild(popupMenu.getSubElements(), -1, b) });
            }
            else if (length == 2 && selectedPath[0] instanceof JMenuBar && selectedPath[1] instanceof JMenu) {
                final JPopupMenu popupMenu2 = ((JMenu)selectedPath[1]).getPopupMenu();
                final MenuElement enabledChild = BasicPopupMenuUI.findEnabledChild(popupMenu2.getSubElements(), -1, true);
                MenuElement[] selectedPath2;
                if (enabledChild != null) {
                    selectedPath2 = new MenuElement[] { null, null, null, enabledChild };
                }
                else {
                    selectedPath2 = new MenuElement[3];
                }
                System.arraycopy(selectedPath, 0, selectedPath2, 0, 2);
                selectedPath2[2] = popupMenu2;
                defaultManager.setSelectedPath(selectedPath2);
            }
            else if (selectedPath[length - 1] instanceof JPopupMenu && selectedPath[length - 2] instanceof JMenu) {
                final JMenu menu = (JMenu)selectedPath[length - 2];
                final MenuElement enabledChild2 = BasicPopupMenuUI.findEnabledChild(menu.getPopupMenu().getSubElements(), -1, b);
                if (enabledChild2 != null) {
                    final MenuElement[] selectedPath3 = new MenuElement[length + 1];
                    System.arraycopy(selectedPath, 0, selectedPath3, 0, length);
                    selectedPath3[length] = enabledChild2;
                    defaultManager.setSelectedPath(selectedPath3);
                }
                else if (length > 2 && selectedPath[length - 3] instanceof JPopupMenu) {
                    final MenuElement enabledChild3 = BasicPopupMenuUI.findEnabledChild(((JPopupMenu)selectedPath[length - 3]).getSubElements(), menu, b);
                    if (enabledChild3 != null && enabledChild3 != menu) {
                        final MenuElement[] selectedPath4 = new MenuElement[length - 1];
                        System.arraycopy(selectedPath, 0, selectedPath4, 0, length - 2);
                        selectedPath4[length - 2] = enabledChild3;
                        defaultManager.setSelectedPath(selectedPath4);
                    }
                }
            }
            else {
                final MenuElement[] subElements = selectedPath[length - 2].getSubElements();
                MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(subElements, selectedPath[length - 1], b);
                if (menuElement == null) {
                    menuElement = BasicPopupMenuUI.findEnabledChild(subElements, -1, b);
                }
                if (menuElement != null) {
                    selectedPath[length - 1] = menuElement;
                    defaultManager.setSelectedPath(selectedPath);
                }
            }
        }
        
        private void cancel() {
            final JPopupMenu lastPopup = BasicPopupMenuUI.getLastPopup();
            if (lastPopup != null) {
                lastPopup.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
            }
            if ("hideMenuTree".equals(UIManager.getString("Menu.cancelMode"))) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
            else {
                this.shortenSelectedPath();
            }
        }
        
        private void shortenSelectedPath() {
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (selectedPath.length <= 2) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                return;
            }
            int length = 2;
            final MenuElement menuElement = selectedPath[selectedPath.length - 1];
            final JPopupMenu lastPopup = BasicPopupMenuUI.getLastPopup();
            if (menuElement == lastPopup) {
                final MenuElement menuElement2 = selectedPath[selectedPath.length - 2];
                if (menuElement2 instanceof JMenu) {
                    if (((JMenu)menuElement2).isEnabled() && lastPopup.getComponentCount() > 0) {
                        length = 1;
                    }
                    else {
                        length = 3;
                    }
                }
            }
            if (selectedPath.length - length <= 2 && !UIManager.getBoolean("Menu.preserveTopLevelSelection")) {
                length = selectedPath.length;
            }
            final MenuElement[] selectedPath2 = new MenuElement[selectedPath.length - length];
            System.arraycopy(selectedPath, 0, selectedPath2, 0, selectedPath.length - length);
            MenuSelectionManager.defaultManager().setSelectedPath(selectedPath2);
        }
    }
    
    static class MouseGrabber implements ChangeListener, AWTEventListener, ComponentListener, WindowListener
    {
        Window grabbedWindow;
        MenuElement[] lastPathSelected;
        
        public MouseGrabber() {
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            defaultManager.addChangeListener(this);
            this.lastPathSelected = defaultManager.getSelectedPath();
            if (this.lastPathSelected.length != 0) {
                this.grabWindow(this.lastPathSelected);
            }
        }
        
        void uninstall() {
            synchronized (BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
                MenuSelectionManager.defaultManager().removeChangeListener(this);
                this.ungrabWindow();
                AppContext.getAppContext().remove(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
            }
        }
        
        void grabWindow(final MenuElement[] array) {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    defaultToolkit.addAWTEventListener(MouseGrabber.this, -2147352464L);
                    return null;
                }
            });
            Component component = array[0].getComponent();
            if (component instanceof JPopupMenu) {
                component = ((JPopupMenu)component).getInvoker();
            }
            this.grabbedWindow = (Window)((component instanceof Window) ? component : SwingUtilities.getWindowAncestor(component));
            if (this.grabbedWindow != null) {
                if (defaultToolkit instanceof SunToolkit) {
                    ((SunToolkit)defaultToolkit).grab(this.grabbedWindow);
                }
                else {
                    this.grabbedWindow.addComponentListener(this);
                    this.grabbedWindow.addWindowListener(this);
                }
            }
        }
        
        void ungrabWindow() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                final /* synthetic */ Toolkit val$tk = Toolkit.getDefaultToolkit();
                
                @Override
                public Object run() {
                    this.val$tk.removeAWTEventListener(MouseGrabber.this);
                    return null;
                }
            });
            this.realUngrabWindow();
        }
        
        void realUngrabWindow() {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            if (this.grabbedWindow != null) {
                if (defaultToolkit instanceof SunToolkit) {
                    ((SunToolkit)defaultToolkit).ungrab(this.grabbedWindow);
                }
                else {
                    this.grabbedWindow.removeComponentListener(this);
                    this.grabbedWindow.removeWindowListener(this);
                }
                this.grabbedWindow = null;
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (this.lastPathSelected.length == 0 && selectedPath.length != 0) {
                this.grabWindow(selectedPath);
            }
            if (this.lastPathSelected.length != 0 && selectedPath.length == 0) {
                this.ungrabWindow();
            }
            this.lastPathSelected = selectedPath;
        }
        
        @Override
        public void eventDispatched(final AWTEvent awtEvent) {
            if (awtEvent instanceof UngrabEvent) {
                this.cancelPopupMenu();
                return;
            }
            if (!(awtEvent instanceof MouseEvent)) {
                return;
            }
            final MouseEvent mouseEvent = (MouseEvent)awtEvent;
            final Component component = mouseEvent.getComponent();
            switch (mouseEvent.getID()) {
                case 501: {
                    if (this.isInPopup(component) || (component instanceof JMenu && ((JMenu)component).isSelected())) {
                        return;
                    }
                    if (!(component instanceof JComponent) || ((JComponent)component).getClientProperty("doNotCancelPopup") != BasicComboBoxUI.HIDE_POPUP_KEY) {
                        this.cancelPopupMenu();
                        if (UIManager.getBoolean("PopupMenu.consumeEventOnClose") && !(component instanceof MenuElement)) {
                            mouseEvent.consume();
                        }
                        break;
                    }
                    break;
                }
                case 502: {
                    if (!(component instanceof MenuElement) && this.isInPopup(component)) {
                        break;
                    }
                    if (component instanceof JMenu || !(component instanceof JMenuItem)) {
                        MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
                        break;
                    }
                    break;
                }
                case 506: {
                    if (!(component instanceof MenuElement) && this.isInPopup(component)) {
                        break;
                    }
                    MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
                    break;
                }
                case 507: {
                    if (this.isInPopup(component) || (component instanceof JComboBox && ((JComboBox)component).isPopupVisible())) {
                        return;
                    }
                    this.cancelPopupMenu();
                    break;
                }
            }
        }
        
        boolean isInPopup(final Component component) {
            for (Component parent = component; parent != null && !(parent instanceof Applet) && !(parent instanceof Window); parent = parent.getParent()) {
                if (parent instanceof JPopupMenu) {
                    return true;
                }
            }
            return false;
        }
        
        void cancelPopupMenu() {
            try {
                final Iterator<JPopupMenu> iterator = BasicPopupMenuUI.getPopups().iterator();
                while (iterator.hasNext()) {
                    iterator.next().putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
                }
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
            catch (final RuntimeException ex) {
                this.realUngrabWindow();
                throw ex;
            }
            catch (final Error error) {
                this.realUngrabWindow();
                throw error;
            }
        }
        
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void componentShown(final ComponentEvent componentEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void windowClosed(final WindowEvent windowEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void windowIconified(final WindowEvent windowEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
            this.cancelPopupMenu();
        }
        
        @Override
        public void windowOpened(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeiconified(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
        }
    }
    
    static class MenuKeyboardHelper implements ChangeListener, KeyListener
    {
        private Component lastFocused;
        private MenuElement[] lastPathSelected;
        private JPopupMenu lastPopup;
        private JRootPane invokerRootPane;
        private ActionMap menuActionMap;
        private InputMap menuInputMap;
        private boolean focusTraversalKeysEnabled;
        private boolean receivedKeyPressed;
        private FocusListener rootPaneFocusListener;
        
        MenuKeyboardHelper() {
            this.lastFocused = null;
            this.lastPathSelected = new MenuElement[0];
            this.menuActionMap = BasicPopupMenuUI.getActionMap();
            this.receivedKeyPressed = false;
            this.rootPaneFocusListener = new FocusAdapter() {
                @Override
                public void focusGained(final FocusEvent focusEvent) {
                    final Component oppositeComponent = focusEvent.getOppositeComponent();
                    if (oppositeComponent != null) {
                        MenuKeyboardHelper.this.lastFocused = oppositeComponent;
                    }
                    focusEvent.getComponent().removeFocusListener(this);
                }
            };
        }
        
        void removeItems() {
            if (this.lastFocused != null) {
                if (!this.lastFocused.requestFocusInWindow()) {
                    final Window focusedWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
                    if (focusedWindow != null && "###focusableSwingPopup###".equals(focusedWindow.getName())) {
                        this.lastFocused.requestFocus();
                    }
                }
                this.lastFocused = null;
            }
            if (this.invokerRootPane != null) {
                this.invokerRootPane.removeKeyListener(this);
                this.invokerRootPane.setFocusTraversalKeysEnabled(this.focusTraversalKeysEnabled);
                this.removeUIInputMap(this.invokerRootPane, this.menuInputMap);
                this.removeUIActionMap(this.invokerRootPane, this.menuActionMap);
                this.invokerRootPane = null;
            }
            this.receivedKeyPressed = false;
        }
        
        JPopupMenu getActivePopup(final MenuElement[] array) {
            for (int i = array.length - 1; i >= 0; --i) {
                final MenuElement menuElement = array[i];
                if (menuElement instanceof JPopupMenu) {
                    return (JPopupMenu)menuElement;
                }
            }
            return null;
        }
        
        void addUIInputMap(final JComponent component, final InputMap parent) {
            InputMap inputMap = null;
            InputMap parent2;
            for (parent2 = component.getInputMap(2); parent2 != null && !(parent2 instanceof UIResource); parent2 = parent2.getParent()) {
                inputMap = parent2;
            }
            if (inputMap == null) {
                component.setInputMap(2, parent);
            }
            else {
                inputMap.setParent(parent);
            }
            parent.setParent(parent2);
        }
        
        void addUIActionMap(final JComponent component, final ActionMap actionMap) {
            ActionMap actionMap2 = null;
            ActionMap parent;
            for (parent = component.getActionMap(); parent != null && !(parent instanceof UIResource); parent = parent.getParent()) {
                actionMap2 = parent;
            }
            if (actionMap2 == null) {
                component.setActionMap(actionMap);
            }
            else {
                actionMap2.setParent(actionMap);
            }
            actionMap.setParent(parent);
        }
        
        void removeUIInputMap(final JComponent component, final InputMap inputMap) {
            InputMap inputMap2 = null;
            InputMap inputMap3 = component.getInputMap(2);
            while (inputMap3 != null) {
                if (inputMap3 == inputMap) {
                    if (inputMap2 == null) {
                        component.setInputMap(2, inputMap.getParent());
                        break;
                    }
                    inputMap2.setParent(inputMap.getParent());
                    break;
                }
                else {
                    inputMap2 = inputMap3;
                    inputMap3 = inputMap3.getParent();
                }
            }
        }
        
        void removeUIActionMap(final JComponent component, final ActionMap actionMap) {
            ActionMap actionMap2 = null;
            ActionMap actionMap3 = component.getActionMap();
            while (actionMap3 != null) {
                if (actionMap3 == actionMap) {
                    if (actionMap2 == null) {
                        component.setActionMap(actionMap.getParent());
                        break;
                    }
                    actionMap2.setParent(actionMap.getParent());
                    break;
                }
                else {
                    actionMap2 = actionMap3;
                    actionMap3 = actionMap3.getParent();
                }
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (!(UIManager.getLookAndFeel() instanceof BasicLookAndFeel)) {
                this.uninstall();
                return;
            }
            final MenuElement[] selectedPath = ((MenuSelectionManager)changeEvent.getSource()).getSelectedPath();
            JPopupMenu lastPopup = this.getActivePopup(selectedPath);
            if (lastPopup != null && !lastPopup.isFocusable()) {
                return;
            }
            if (this.lastPathSelected.length != 0 && selectedPath.length != 0 && !checkInvokerEqual(selectedPath[0], this.lastPathSelected[0])) {
                this.removeItems();
                this.lastPathSelected = new MenuElement[0];
            }
            if (this.lastPathSelected.length == 0 && selectedPath.length > 0) {
                JComponent component;
                if (lastPopup == null) {
                    if (selectedPath.length != 2 || !(selectedPath[0] instanceof JMenuBar) || !(selectedPath[1] instanceof JMenu)) {
                        return;
                    }
                    component = (JComponent)selectedPath[1];
                    lastPopup = ((JMenu)component).getPopupMenu();
                }
                else {
                    Component component2 = lastPopup.getInvoker();
                    if (component2 instanceof JFrame) {
                        component = ((JFrame)component2).getRootPane();
                    }
                    else if (component2 instanceof JDialog) {
                        component = ((JDialog)component2).getRootPane();
                    }
                    else if (component2 instanceof JApplet) {
                        component = ((JApplet)component2).getRootPane();
                    }
                    else {
                        while (!(component2 instanceof JComponent)) {
                            if (component2 == null) {
                                return;
                            }
                            component2 = component2.getParent();
                        }
                        component = (JComponent)component2;
                    }
                }
                this.lastFocused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                this.invokerRootPane = SwingUtilities.getRootPane(component);
                if (this.invokerRootPane != null) {
                    this.invokerRootPane.addFocusListener(this.rootPaneFocusListener);
                    this.invokerRootPane.requestFocus(true);
                    this.invokerRootPane.addKeyListener(this);
                    this.focusTraversalKeysEnabled = this.invokerRootPane.getFocusTraversalKeysEnabled();
                    this.invokerRootPane.setFocusTraversalKeysEnabled(false);
                    this.menuInputMap = BasicPopupMenuUI.getInputMap(lastPopup, this.invokerRootPane);
                    this.addUIInputMap(this.invokerRootPane, this.menuInputMap);
                    this.addUIActionMap(this.invokerRootPane, this.menuActionMap);
                }
            }
            else if (this.lastPathSelected.length != 0 && selectedPath.length == 0) {
                this.removeItems();
            }
            else if (lastPopup != this.lastPopup) {
                this.receivedKeyPressed = false;
            }
            this.lastPathSelected = selectedPath;
            this.lastPopup = lastPopup;
        }
        
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            this.receivedKeyPressed = true;
            MenuSelectionManager.defaultManager().processKeyEvent(keyEvent);
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            if (this.receivedKeyPressed) {
                this.receivedKeyPressed = false;
                MenuSelectionManager.defaultManager().processKeyEvent(keyEvent);
            }
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            if (this.receivedKeyPressed) {
                MenuSelectionManager.defaultManager().processKeyEvent(keyEvent);
            }
        }
        
        void uninstall() {
            synchronized (BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
                MenuSelectionManager.defaultManager().removeChangeListener(this);
                AppContext.getAppContext().remove(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
            }
        }
    }
}
