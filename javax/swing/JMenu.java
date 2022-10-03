package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.awt.event.KeyEvent;
import java.awt.ComponentOrientation;
import javax.swing.event.MenuListener;
import java.beans.PropertyChangeListener;
import java.awt.Insets;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Component;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.MenuItemUI;
import java.awt.Point;
import javax.swing.event.MenuEvent;
import javax.swing.event.ChangeListener;
import javax.accessibility.Accessible;

public class JMenu extends JMenuItem implements Accessible, MenuElement
{
    private static final String uiClassID = "MenuUI";
    private JPopupMenu popupMenu;
    private ChangeListener menuChangeListener;
    private MenuEvent menuEvent;
    private int delay;
    private Point customMenuLocation;
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    protected WinListener popupListener;
    
    public JMenu() {
        this("");
    }
    
    public JMenu(final String s) {
        super(s);
        this.menuChangeListener = null;
        this.menuEvent = null;
        this.customMenuLocation = null;
    }
    
    public JMenu(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JMenu(final String s, final boolean b) {
        this(s);
    }
    
    @Override
    void initFocusability() {
    }
    
    @Override
    public void updateUI() {
        this.setUI((MenuItemUI)UIManager.getUI(this));
        if (this.popupMenu != null) {
            this.popupMenu.setUI((PopupMenuUI)UIManager.getUI(this.popupMenu));
        }
    }
    
    @Override
    public String getUIClassID() {
        return "MenuUI";
    }
    
    @Override
    public void setModel(final ButtonModel buttonModel) {
        final ButtonModel model = this.getModel();
        super.setModel(buttonModel);
        if (model != null && this.menuChangeListener != null) {
            model.removeChangeListener(this.menuChangeListener);
            this.menuChangeListener = null;
        }
        if ((this.model = buttonModel) != null) {
            buttonModel.addChangeListener(this.menuChangeListener = this.createMenuChangeListener());
        }
    }
    
    @Override
    public boolean isSelected() {
        return this.getModel().isSelected();
    }
    
    @Override
    public void setSelected(final boolean selected) {
        final ButtonModel model = this.getModel();
        model.isSelected();
        if (selected != model.isSelected()) {
            this.getModel().setSelected(selected);
        }
    }
    
    public boolean isPopupMenuVisible() {
        this.ensurePopupMenuCreated();
        return this.popupMenu.isVisible();
    }
    
    public void setPopupMenuVisible(final boolean b) {
        if (b != this.isPopupMenuVisible() && (this.isEnabled() || !b)) {
            this.ensurePopupMenuCreated();
            if (b && this.isShowing()) {
                Point point = this.getCustomMenuLocation();
                if (point == null) {
                    point = this.getPopupMenuOrigin();
                }
                this.getPopupMenu().show(this, point.x, point.y);
            }
            else {
                this.getPopupMenu().setVisible(false);
            }
        }
    }
    
    protected Point getPopupMenuOrigin() {
        final JPopupMenu popupMenu = this.getPopupMenu();
        final Dimension size = this.getSize();
        Dimension dimension = popupMenu.getSize();
        if (dimension.width == 0) {
            dimension = popupMenu.getPreferredSize();
        }
        final Point locationOnScreen = this.getLocationOnScreen();
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration();
        Rectangle bounds = new Rectangle(defaultToolkit.getScreenSize());
        final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (int i = 0; i < screenDevices.length; ++i) {
            if (screenDevices[i].getType() == 0) {
                final GraphicsConfiguration defaultConfiguration = screenDevices[i].getDefaultConfiguration();
                if (defaultConfiguration.getBounds().contains(locationOnScreen)) {
                    graphicsConfiguration = defaultConfiguration;
                    break;
                }
            }
        }
        if (graphicsConfiguration != null) {
            bounds = graphicsConfiguration.getBounds();
            final Insets screenInsets = defaultToolkit.getScreenInsets(graphicsConfiguration);
            final Rectangle rectangle = bounds;
            rectangle.width -= Math.abs(screenInsets.left + screenInsets.right);
            final Rectangle rectangle2 = bounds;
            rectangle2.height -= Math.abs(screenInsets.top + screenInsets.bottom);
            final Point point = locationOnScreen;
            point.x -= Math.abs(screenInsets.left);
            final Point point2 = locationOnScreen;
            point2.y -= Math.abs(screenInsets.top);
        }
        int n;
        int n2;
        if (this.getParent() instanceof JPopupMenu) {
            final int int1 = UIManager.getInt("Menu.submenuPopupOffsetX");
            final int int2 = UIManager.getInt("Menu.submenuPopupOffsetY");
            if (SwingUtilities.isLeftToRight(this)) {
                n = size.width + int1;
                if (locationOnScreen.x + n + dimension.width >= bounds.width + bounds.x && bounds.width - size.width < 2 * (locationOnScreen.x - bounds.x)) {
                    n = 0 - int1 - dimension.width;
                }
            }
            else {
                n = 0 - int1 - dimension.width;
                if (locationOnScreen.x + n < bounds.x && bounds.width - size.width > 2 * (locationOnScreen.x - bounds.x)) {
                    n = size.width + int1;
                }
            }
            n2 = int2;
            if (locationOnScreen.y + n2 + dimension.height >= bounds.height + bounds.y && bounds.height - size.height < 2 * (locationOnScreen.y - bounds.y)) {
                n2 = size.height - int2 - dimension.height;
            }
        }
        else {
            final int int3 = UIManager.getInt("Menu.menuPopupOffsetX");
            final int int4 = UIManager.getInt("Menu.menuPopupOffsetY");
            if (SwingUtilities.isLeftToRight(this)) {
                n = int3;
                if (locationOnScreen.x + n + dimension.width >= bounds.width + bounds.x && bounds.width - size.width < 2 * (locationOnScreen.x - bounds.x)) {
                    n = size.width - int3 - dimension.width;
                }
            }
            else {
                n = size.width - int3 - dimension.width;
                if (locationOnScreen.x + n < bounds.x && bounds.width - size.width > 2 * (locationOnScreen.x - bounds.x)) {
                    n = int3;
                }
            }
            n2 = size.height + int4;
            if (locationOnScreen.y + n2 + dimension.height >= bounds.height + bounds.y && bounds.height - size.height < 2 * (locationOnScreen.y - bounds.y)) {
                n2 = 0 - int4 - dimension.height;
            }
        }
        return new Point(n, n2);
    }
    
    public int getDelay() {
        return this.delay;
    }
    
    public void setDelay(final int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be a positive integer");
        }
        this.delay = delay;
    }
    
    private void ensurePopupMenuCreated() {
        if (this.popupMenu == null) {
            (this.popupMenu = new JPopupMenu()).setInvoker(this);
            this.popupListener = this.createWinListener(this.popupMenu);
        }
    }
    
    private Point getCustomMenuLocation() {
        return this.customMenuLocation;
    }
    
    public void setMenuLocation(final int n, final int n2) {
        this.customMenuLocation = new Point(n, n2);
        if (this.popupMenu != null) {
            this.popupMenu.setLocation(n, n2);
        }
    }
    
    public JMenuItem add(final JMenuItem menuItem) {
        this.ensurePopupMenuCreated();
        return this.popupMenu.add(menuItem);
    }
    
    @Override
    public Component add(final Component component) {
        this.ensurePopupMenuCreated();
        this.popupMenu.add(component);
        return component;
    }
    
    @Override
    public Component add(final Component component, final int n) {
        this.ensurePopupMenuCreated();
        this.popupMenu.add(component, n);
        return component;
    }
    
    public JMenuItem add(final String s) {
        return this.add(new JMenuItem(s));
    }
    
    public JMenuItem add(final Action action) {
        final JMenuItem actionComponent = this.createActionComponent(action);
        actionComponent.setAction(action);
        this.add(actionComponent);
        return actionComponent;
    }
    
    protected JMenuItem createActionComponent(final Action action) {
        final JMenuItem menuItem = new JMenuItem() {
            @Override
            protected PropertyChangeListener createActionPropertyChangeListener(final Action action) {
                PropertyChangeListener propertyChangeListener = JMenu.this.createActionChangeListener(this);
                if (propertyChangeListener == null) {
                    propertyChangeListener = super.createActionPropertyChangeListener(action);
                }
                return propertyChangeListener;
            }
        };
        menuItem.setHorizontalTextPosition(11);
        menuItem.setVerticalTextPosition(0);
        return menuItem;
    }
    
    protected PropertyChangeListener createActionChangeListener(final JMenuItem menuItem) {
        return menuItem.createActionPropertyChangeListener0(menuItem.getAction());
    }
    
    public void addSeparator() {
        this.ensurePopupMenuCreated();
        this.popupMenu.addSeparator();
    }
    
    public void insert(final String s, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        this.ensurePopupMenuCreated();
        this.popupMenu.insert(new JMenuItem(s), n);
    }
    
    public JMenuItem insert(final JMenuItem menuItem, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        this.ensurePopupMenuCreated();
        this.popupMenu.insert(menuItem, n);
        return menuItem;
    }
    
    public JMenuItem insert(final Action action, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        this.ensurePopupMenuCreated();
        final JMenuItem menuItem = new JMenuItem(action);
        menuItem.setHorizontalTextPosition(11);
        menuItem.setVerticalTextPosition(0);
        this.popupMenu.insert(menuItem, n);
        return menuItem;
    }
    
    public void insertSeparator(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        this.ensurePopupMenuCreated();
        this.popupMenu.insert(new JPopupMenu.Separator(), n);
    }
    
    public JMenuItem getItem(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        final Component menuComponent = this.getMenuComponent(n);
        if (menuComponent instanceof JMenuItem) {
            return (JMenuItem)menuComponent;
        }
        return null;
    }
    
    public int getItemCount() {
        return this.getMenuComponentCount();
    }
    
    public boolean isTearOff() {
        throw new Error("boolean isTearOff() {} not yet implemented");
    }
    
    public void remove(final JMenuItem menuItem) {
        if (this.popupMenu != null) {
            this.popupMenu.remove(menuItem);
        }
    }
    
    @Override
    public void remove(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        if (n > this.getItemCount()) {
            throw new IllegalArgumentException("index greater than the number of items.");
        }
        if (this.popupMenu != null) {
            this.popupMenu.remove(n);
        }
    }
    
    @Override
    public void remove(final Component component) {
        if (this.popupMenu != null) {
            this.popupMenu.remove(component);
        }
    }
    
    @Override
    public void removeAll() {
        if (this.popupMenu != null) {
            this.popupMenu.removeAll();
        }
    }
    
    public int getMenuComponentCount() {
        int componentCount = 0;
        if (this.popupMenu != null) {
            componentCount = this.popupMenu.getComponentCount();
        }
        return componentCount;
    }
    
    public Component getMenuComponent(final int n) {
        if (this.popupMenu != null) {
            return this.popupMenu.getComponent(n);
        }
        return null;
    }
    
    public Component[] getMenuComponents() {
        if (this.popupMenu != null) {
            return this.popupMenu.getComponents();
        }
        return new Component[0];
    }
    
    public boolean isTopLevelMenu() {
        return this.getParent() instanceof JMenuBar;
    }
    
    public boolean isMenuComponent(final Component component) {
        if (component == this) {
            return true;
        }
        if (component instanceof JPopupMenu && component == this.getPopupMenu()) {
            return true;
        }
        final int menuComponentCount = this.getMenuComponentCount();
        for (final Component component2 : this.getMenuComponents()) {
            if (component2 == component) {
                return true;
            }
            if (component2 instanceof JMenu && ((JMenu)component2).isMenuComponent(component)) {
                return true;
            }
        }
        return false;
    }
    
    private Point translateToPopupMenu(final Point point) {
        return this.translateToPopupMenu(point.x, point.y);
    }
    
    private Point translateToPopupMenu(final int n, final int n2) {
        int n3;
        int n4;
        if (this.getParent() instanceof JPopupMenu) {
            n3 = n - this.getSize().width;
            n4 = n2;
        }
        else {
            n3 = n;
            n4 = n2 - this.getSize().height;
        }
        return new Point(n3, n4);
    }
    
    public JPopupMenu getPopupMenu() {
        this.ensurePopupMenuCreated();
        return this.popupMenu;
    }
    
    public void addMenuListener(final MenuListener menuListener) {
        this.listenerList.add(MenuListener.class, menuListener);
    }
    
    public void removeMenuListener(final MenuListener menuListener) {
        this.listenerList.remove(MenuListener.class, menuListener);
    }
    
    public MenuListener[] getMenuListeners() {
        return this.listenerList.getListeners(MenuListener.class);
    }
    
    protected void fireMenuSelected() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuListener.class) {
                if (listenerList[i + 1] == null) {
                    throw new Error(this.getText() + " has a NULL Listener!! " + i);
                }
                if (this.menuEvent == null) {
                    this.menuEvent = new MenuEvent(this);
                }
                ((MenuListener)listenerList[i + 1]).menuSelected(this.menuEvent);
            }
        }
    }
    
    protected void fireMenuDeselected() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuListener.class) {
                if (listenerList[i + 1] == null) {
                    throw new Error(this.getText() + " has a NULL Listener!! " + i);
                }
                if (this.menuEvent == null) {
                    this.menuEvent = new MenuEvent(this);
                }
                ((MenuListener)listenerList[i + 1]).menuDeselected(this.menuEvent);
            }
        }
    }
    
    protected void fireMenuCanceled() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuListener.class) {
                if (listenerList[i + 1] == null) {
                    throw new Error(this.getText() + " has a NULL Listener!! " + i);
                }
                if (this.menuEvent == null) {
                    this.menuEvent = new MenuEvent(this);
                }
                ((MenuListener)listenerList[i + 1]).menuCanceled(this.menuEvent);
            }
        }
    }
    
    @Override
    void configureAcceleratorFromAction(final Action action) {
    }
    
    private ChangeListener createMenuChangeListener() {
        return new MenuChangeListener();
    }
    
    protected WinListener createWinListener(final JPopupMenu popupMenu) {
        return new WinListener(popupMenu);
    }
    
    @Override
    public void menuSelectionChanged(final boolean selected) {
        this.setSelected(selected);
    }
    
    @Override
    public MenuElement[] getSubElements() {
        if (this.popupMenu == null) {
            return new MenuElement[0];
        }
        return new MenuElement[] { this.popupMenu };
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void applyComponentOrientation(final ComponentOrientation componentOrientation) {
        super.applyComponentOrientation(componentOrientation);
        if (this.popupMenu != null) {
            for (int menuComponentCount = this.getMenuComponentCount(), i = 0; i < menuComponentCount; ++i) {
                this.getMenuComponent(i).applyComponentOrientation(componentOrientation);
            }
            this.popupMenu.setComponentOrientation(componentOrientation);
        }
    }
    
    @Override
    public void setComponentOrientation(final ComponentOrientation componentOrientation) {
        super.setComponentOrientation(componentOrientation);
        if (this.popupMenu != null) {
            this.popupMenu.setComponentOrientation(componentOrientation);
        }
    }
    
    @Override
    public void setAccelerator(final KeyStroke keyStroke) {
        throw new Error("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead.");
    }
    
    @Override
    protected void processKeyEvent(final KeyEvent keyEvent) {
        MenuSelectionManager.defaultManager().processKeyEvent(keyEvent);
        if (keyEvent.isConsumed()) {
            return;
        }
        super.processKeyEvent(keyEvent);
    }
    
    @Override
    public void doClick(final int n) {
        MenuSelectionManager.defaultManager().setSelectedPath(this.buildMenuElementArray(this));
    }
    
    private MenuElement[] buildMenuElementArray(final JMenu menu) {
        final Vector vector = new Vector();
        Component component = menu.getPopupMenu();
        while (true) {
            if (component instanceof JPopupMenu) {
                final JPopupMenu popupMenu = (JPopupMenu)component;
                vector.insertElementAt(popupMenu, 0);
                component = popupMenu.getInvoker();
            }
            else if (component instanceof JMenu) {
                final JMenu menu2 = (JMenu)component;
                vector.insertElementAt(menu2, 0);
                component = menu2.getParent();
            }
            else {
                if (component instanceof JMenuBar) {
                    break;
                }
                continue;
            }
        }
        vector.insertElementAt(component, 0);
        final MenuElement[] array = new MenuElement[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("MenuUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJMenu();
        }
        return this.accessibleContext;
    }
    
    class MenuChangeListener implements ChangeListener, Serializable
    {
        boolean isSelected;
        
        MenuChangeListener() {
            this.isSelected = false;
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final boolean selected = ((ButtonModel)changeEvent.getSource()).isSelected();
            if (selected != this.isSelected) {
                if (selected) {
                    JMenu.this.fireMenuSelected();
                }
                else {
                    JMenu.this.fireMenuDeselected();
                }
                this.isSelected = selected;
            }
        }
    }
    
    protected class WinListener extends WindowAdapter implements Serializable
    {
        JPopupMenu popupMenu;
        
        public WinListener(final JPopupMenu popupMenu) {
            this.popupMenu = popupMenu;
        }
        
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
            JMenu.this.setSelected(false);
        }
    }
    
    protected class AccessibleJMenu extends AccessibleJMenuItem implements AccessibleSelection
    {
        @Override
        public int getAccessibleChildrenCount() {
            final Component[] menuComponents = JMenu.this.getMenuComponents();
            int n = 0;
            final Component[] array = menuComponents;
            for (int length = array.length, i = 0; i < length; ++i) {
                if (array[i] instanceof Accessible) {
                    ++n;
                }
            }
            return n;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            final Component[] menuComponents = JMenu.this.getMenuComponents();
            int n2 = 0;
            for (final Component component : menuComponents) {
                if (component instanceof Accessible) {
                    if (n2 == n) {
                        if (component instanceof JComponent) {
                            component.getAccessibleContext().setAccessibleParent(JMenu.this);
                        }
                        return (Accessible)component;
                    }
                    ++n2;
                }
            }
            return null;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU;
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (selectedPath != null) {
                for (int i = 0; i < selectedPath.length; ++i) {
                    if (selectedPath[i] == JMenu.this && i + 1 < selectedPath.length) {
                        return 1;
                    }
                }
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            if (n < 0 || n >= JMenu.this.getItemCount()) {
                return null;
            }
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (selectedPath != null) {
                for (int i = 0; i < selectedPath.length; ++i) {
                    if (selectedPath[i] == JMenu.this) {
                        while (++i < selectedPath.length) {
                            if (selectedPath[i] instanceof JMenuItem) {
                                return (Accessible)selectedPath[i];
                            }
                        }
                    }
                }
            }
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (selectedPath != null) {
                final JMenuItem item = JMenu.this.getItem(n);
                for (int i = 0; i < selectedPath.length; ++i) {
                    if (selectedPath[i] == item) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
            if (n < 0 || n >= JMenu.this.getItemCount()) {
                return;
            }
            final JMenuItem item = JMenu.this.getItem(n);
            if (item != null) {
                if (item instanceof JMenu) {
                    MenuSelectionManager.defaultManager().setSelectedPath(JMenu.this.buildMenuElementArray((JMenu)item));
                }
                else {
                    MenuSelectionManager.defaultManager().setSelectedPath(null);
                }
            }
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            if (n < 0 || n >= JMenu.this.getItemCount()) {
                return;
            }
            final JMenuItem item = JMenu.this.getItem(n);
            if (item != null && item instanceof JMenu && item.isSelected()) {
                final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                final MenuElement[] selectedPath2 = new MenuElement[selectedPath.length - 2];
                for (int i = 0; i < selectedPath.length - 2; ++i) {
                    selectedPath2[i] = selectedPath[i];
                }
                MenuSelectionManager.defaultManager().setSelectedPath(selectedPath2);
            }
        }
        
        @Override
        public void clearAccessibleSelection() {
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (selectedPath != null) {
                for (int i = 0; i < selectedPath.length; ++i) {
                    if (selectedPath[i] == JMenu.this) {
                        final MenuElement[] selectedPath2 = new MenuElement[i + 1];
                        System.arraycopy(selectedPath, 0, selectedPath2, 0, i);
                        selectedPath2[i] = JMenu.this.getPopupMenu();
                        MenuSelectionManager.defaultManager().setSelectedPath(selectedPath2);
                    }
                }
            }
        }
        
        @Override
        public void selectAllAccessibleSelection() {
        }
    }
}
