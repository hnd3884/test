package javax.swing;

import javax.accessibility.AccessibleSelection;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.accessibility.AccessibleState;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleRole;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import javax.swing.event.MenuKeyEvent;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import javax.accessibility.AccessibleContext;
import java.awt.Graphics;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuListener;
import java.util.Iterator;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import sun.awt.SunToolkit;
import java.awt.GraphicsDevice;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.Component;
import javax.accessibility.Accessible;

public class JPopupMenu extends JComponent implements Accessible, MenuElement
{
    private static final String uiClassID = "PopupMenuUI";
    private static final Object defaultLWPopupEnabledKey;
    static boolean popupPostionFixDisabled;
    transient Component invoker;
    transient Popup popup;
    transient Frame frame;
    private int desiredLocationX;
    private int desiredLocationY;
    private String label;
    private boolean paintBorder;
    private Insets margin;
    private boolean lightWeightPopup;
    private SingleSelectionModel selectionModel;
    private static final Object classLock;
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    
    public static void setDefaultLightWeightPopupEnabled(final boolean b) {
        SwingUtilities.appContextPut(JPopupMenu.defaultLWPopupEnabledKey, b);
    }
    
    public static boolean getDefaultLightWeightPopupEnabled() {
        final Boolean b = (Boolean)SwingUtilities.appContextGet(JPopupMenu.defaultLWPopupEnabledKey);
        if (b == null) {
            SwingUtilities.appContextPut(JPopupMenu.defaultLWPopupEnabledKey, Boolean.TRUE);
            return true;
        }
        return b;
    }
    
    public JPopupMenu() {
        this(null);
    }
    
    public JPopupMenu(final String label) {
        this.label = null;
        this.paintBorder = true;
        this.margin = null;
        this.lightWeightPopup = true;
        this.label = label;
        this.lightWeightPopup = getDefaultLightWeightPopupEnabled();
        this.setSelectionModel(new DefaultSingleSelectionModel());
        this.enableEvents(16L);
        this.setFocusTraversalKeysEnabled(false);
        this.updateUI();
    }
    
    public PopupMenuUI getUI() {
        return (PopupMenuUI)this.ui;
    }
    
    public void setUI(final PopupMenuUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((PopupMenuUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "PopupMenuUI";
    }
    
    @Override
    protected void processFocusEvent(final FocusEvent focusEvent) {
        super.processFocusEvent(focusEvent);
    }
    
    @Override
    protected void processKeyEvent(final KeyEvent keyEvent) {
        MenuSelectionManager.defaultManager().processKeyEvent(keyEvent);
        if (keyEvent.isConsumed()) {
            return;
        }
        super.processKeyEvent(keyEvent);
    }
    
    public SingleSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    public void setSelectionModel(final SingleSelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }
    
    public JMenuItem add(final JMenuItem menuItem) {
        super.add(menuItem);
        return menuItem;
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
    
    Point adjustPopupLocationToFitScreen(final int n, final int n2) {
        final Point point = new Point(n, n2);
        if (JPopupMenu.popupPostionFixDisabled || GraphicsEnvironment.isHeadless()) {
            return point;
        }
        final GraphicsConfiguration currentGraphicsConfiguration = this.getCurrentGraphicsConfiguration(point);
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Rectangle bounds;
        if (currentGraphicsConfiguration != null) {
            bounds = currentGraphicsConfiguration.getBounds();
        }
        else {
            bounds = new Rectangle(defaultToolkit.getScreenSize());
        }
        final Dimension preferredSize = this.getPreferredSize();
        final long n3 = point.x + (long)preferredSize.width;
        final long n4 = point.y + (long)preferredSize.height;
        int width = bounds.width;
        int height = bounds.height;
        if (!canPopupOverlapTaskBar()) {
            final Insets screenInsets = defaultToolkit.getScreenInsets(currentGraphicsConfiguration);
            final Rectangle rectangle = bounds;
            rectangle.x += screenInsets.left;
            final Rectangle rectangle2 = bounds;
            rectangle2.y += screenInsets.top;
            width -= screenInsets.left + screenInsets.right;
            height -= screenInsets.top + screenInsets.bottom;
        }
        final int n5 = bounds.x + width;
        final int n6 = bounds.y + height;
        if (n3 > n5) {
            point.x = n5 - preferredSize.width;
        }
        if (n4 > n6) {
            point.y = n6 - preferredSize.height;
        }
        if (point.x < bounds.x) {
            point.x = bounds.x;
        }
        if (point.y < bounds.y) {
            point.y = bounds.y;
        }
        return point;
    }
    
    private GraphicsConfiguration getCurrentGraphicsConfiguration(final Point point) {
        GraphicsConfiguration graphicsConfiguration = null;
        final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (int i = 0; i < screenDevices.length; ++i) {
            if (screenDevices[i].getType() == 0) {
                final GraphicsConfiguration defaultConfiguration = screenDevices[i].getDefaultConfiguration();
                if (defaultConfiguration.getBounds().contains(point)) {
                    graphicsConfiguration = defaultConfiguration;
                    break;
                }
            }
        }
        if (graphicsConfiguration == null && this.getInvoker() != null) {
            graphicsConfiguration = this.getInvoker().getGraphicsConfiguration();
        }
        return graphicsConfiguration;
    }
    
    static boolean canPopupOverlapTaskBar() {
        boolean canPopupOverlapTaskBar = true;
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            canPopupOverlapTaskBar = ((SunToolkit)defaultToolkit).canPopupOverlapTaskBar();
        }
        return canPopupOverlapTaskBar;
    }
    
    protected JMenuItem createActionComponent(final Action action) {
        final JMenuItem menuItem = new JMenuItem() {
            @Override
            protected PropertyChangeListener createActionPropertyChangeListener(final Action action) {
                PropertyChangeListener propertyChangeListener = JPopupMenu.this.createActionChangeListener(this);
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
    
    @Override
    public void remove(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        if (n > this.getComponentCount() - 1) {
            throw new IllegalArgumentException("index greater than the number of items.");
        }
        super.remove(n);
    }
    
    public void setLightWeightPopupEnabled(final boolean lightWeightPopup) {
        this.lightWeightPopup = lightWeightPopup;
    }
    
    public boolean isLightWeightPopupEnabled() {
        return this.lightWeightPopup;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String label) {
        final String label2 = this.label;
        this.firePropertyChange("label", label2, this.label = label);
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", label2, label);
        }
        this.invalidate();
        this.repaint();
    }
    
    public void addSeparator() {
        this.add(new Separator());
    }
    
    public void insert(final Action action, final int n) {
        final JMenuItem actionComponent = this.createActionComponent(action);
        actionComponent.setAction(action);
        this.insert(actionComponent, n);
    }
    
    public void insert(final Component component, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("index less than zero.");
        }
        final int componentCount = this.getComponentCount();
        final Vector vector = new Vector();
        for (int i = n; i < componentCount; ++i) {
            vector.addElement(this.getComponent(n));
            this.remove(n);
        }
        this.add(component);
        final Iterator iterator = vector.iterator();
        while (iterator.hasNext()) {
            this.add((Component)iterator.next());
        }
    }
    
    public void addPopupMenuListener(final PopupMenuListener popupMenuListener) {
        this.listenerList.add(PopupMenuListener.class, popupMenuListener);
    }
    
    public void removePopupMenuListener(final PopupMenuListener popupMenuListener) {
        this.listenerList.remove(PopupMenuListener.class, popupMenuListener);
    }
    
    public PopupMenuListener[] getPopupMenuListeners() {
        return this.listenerList.getListeners(PopupMenuListener.class);
    }
    
    public void addMenuKeyListener(final MenuKeyListener menuKeyListener) {
        this.listenerList.add(MenuKeyListener.class, menuKeyListener);
    }
    
    public void removeMenuKeyListener(final MenuKeyListener menuKeyListener) {
        this.listenerList.remove(MenuKeyListener.class, menuKeyListener);
    }
    
    public MenuKeyListener[] getMenuKeyListeners() {
        return this.listenerList.getListeners(MenuKeyListener.class);
    }
    
    protected void firePopupMenuWillBecomeVisible() {
        final Object[] listenerList = this.listenerList.getListenerList();
        PopupMenuEvent popupMenuEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PopupMenuListener.class) {
                if (popupMenuEvent == null) {
                    popupMenuEvent = new PopupMenuEvent(this);
                }
                ((PopupMenuListener)listenerList[i + 1]).popupMenuWillBecomeVisible(popupMenuEvent);
            }
        }
    }
    
    protected void firePopupMenuWillBecomeInvisible() {
        final Object[] listenerList = this.listenerList.getListenerList();
        PopupMenuEvent popupMenuEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PopupMenuListener.class) {
                if (popupMenuEvent == null) {
                    popupMenuEvent = new PopupMenuEvent(this);
                }
                ((PopupMenuListener)listenerList[i + 1]).popupMenuWillBecomeInvisible(popupMenuEvent);
            }
        }
    }
    
    protected void firePopupMenuCanceled() {
        final Object[] listenerList = this.listenerList.getListenerList();
        PopupMenuEvent popupMenuEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == PopupMenuListener.class) {
                if (popupMenuEvent == null) {
                    popupMenuEvent = new PopupMenuEvent(this);
                }
                ((PopupMenuListener)listenerList[i + 1]).popupMenuCanceled(popupMenuEvent);
            }
        }
    }
    
    @Override
    boolean alwaysOnTop() {
        return true;
    }
    
    public void pack() {
        if (this.popup != null) {
            final Dimension preferredSize = this.getPreferredSize();
            if (preferredSize == null || preferredSize.width != this.getWidth() || preferredSize.height != this.getHeight()) {
                this.showPopup();
            }
            else {
                this.validate();
            }
        }
    }
    
    @Override
    public void setVisible(final boolean b) {
        if (b == this.isVisible()) {
            return;
        }
        if (!b) {
            final Boolean b2 = (Boolean)this.getClientProperty("JPopupMenu.firePopupMenuCanceled");
            if (b2 != null && b2 == Boolean.TRUE) {
                this.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.FALSE);
                this.firePopupMenuCanceled();
            }
            this.getSelectionModel().clearSelection();
        }
        else if (this.isPopupMenu()) {
            MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[] { this });
        }
        if (b) {
            this.firePopupMenuWillBecomeVisible();
            this.showPopup();
            this.firePropertyChange("visible", Boolean.FALSE, Boolean.TRUE);
        }
        else if (this.popup != null) {
            this.firePopupMenuWillBecomeInvisible();
            this.popup.hide();
            this.popup = null;
            this.firePropertyChange("visible", Boolean.TRUE, Boolean.FALSE);
            if (this.isPopupMenu()) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        }
    }
    
    private void showPopup() {
        final Popup popup = this.popup;
        if (popup != null) {
            popup.hide();
        }
        final PopupFactory sharedInstance = PopupFactory.getSharedInstance();
        if (this.isLightWeightPopupEnabled()) {
            sharedInstance.setPopupType(0);
        }
        else {
            sharedInstance.setPopupType(2);
        }
        final Point adjustPopupLocationToFitScreen = this.adjustPopupLocationToFitScreen(this.desiredLocationX, this.desiredLocationY);
        this.desiredLocationX = adjustPopupLocationToFitScreen.x;
        this.desiredLocationY = adjustPopupLocationToFitScreen.y;
        final Popup popup2 = this.getUI().getPopup(this, this.desiredLocationX, this.desiredLocationY);
        sharedInstance.setPopupType(0);
        (this.popup = popup2).show();
    }
    
    @Override
    public boolean isVisible() {
        return this.popup != null;
    }
    
    @Override
    public void setLocation(final int desiredLocationX, final int desiredLocationY) {
        final int desiredLocationX2 = this.desiredLocationX;
        final int desiredLocationY2 = this.desiredLocationY;
        this.desiredLocationX = desiredLocationX;
        this.desiredLocationY = desiredLocationY;
        if (this.popup != null && (desiredLocationX != desiredLocationX2 || desiredLocationY != desiredLocationY2)) {
            this.showPopup();
        }
    }
    
    private boolean isPopupMenu() {
        return this.invoker != null && !(this.invoker instanceof JMenu);
    }
    
    public Component getInvoker() {
        return this.invoker;
    }
    
    public void setInvoker(final Component invoker) {
        final Component invoker2 = this.invoker;
        this.invoker = invoker;
        if (invoker2 != this.invoker && this.ui != null) {
            this.ui.uninstallUI(this);
            this.ui.installUI(this);
        }
        this.invalidate();
    }
    
    public void show(final Component invoker, final int n, final int n2) {
        this.setInvoker(invoker);
        final Frame frame = getFrame(invoker);
        if (frame != this.frame && frame != null) {
            this.frame = frame;
            if (this.popup != null) {
                this.setVisible(false);
            }
        }
        if (invoker != null) {
            final Point locationOnScreen = invoker.getLocationOnScreen();
            long n3 = locationOnScreen.x + (long)n;
            long n4 = locationOnScreen.y + (long)n2;
            if (n3 > 2147483647L) {
                n3 = 2147483647L;
            }
            if (n3 < -2147483648L) {
                n3 = -2147483648L;
            }
            if (n4 > 2147483647L) {
                n4 = 2147483647L;
            }
            if (n4 < -2147483648L) {
                n4 = -2147483648L;
            }
            this.setLocation((int)n3, (int)n4);
        }
        else {
            this.setLocation(n, n2);
        }
        this.setVisible(true);
    }
    
    JPopupMenu getRootPopupMenu() {
        JPopupMenu popupMenu;
        for (popupMenu = this; popupMenu != null && !popupMenu.isPopupMenu() && popupMenu.getInvoker() != null && popupMenu.getInvoker().getParent() != null && popupMenu.getInvoker().getParent() instanceof JPopupMenu; popupMenu = (JPopupMenu)popupMenu.getInvoker().getParent()) {}
        return popupMenu;
    }
    
    @Deprecated
    public Component getComponentAtIndex(final int n) {
        return this.getComponent(n);
    }
    
    public int getComponentIndex(final Component component) {
        final int componentCount = this.getComponentCount();
        final Component[] components = this.getComponents();
        for (int i = 0; i < componentCount; ++i) {
            if (components[i] == component) {
                return i;
            }
        }
        return -1;
    }
    
    public void setPopupSize(final Dimension preferredSize) {
        final Dimension preferredSize2 = this.getPreferredSize();
        this.setPreferredSize(preferredSize);
        if (this.popup != null && !preferredSize2.equals(this.getPreferredSize())) {
            this.showPopup();
        }
    }
    
    public void setPopupSize(final int n, final int n2) {
        this.setPopupSize(new Dimension(n, n2));
    }
    
    public void setSelected(final Component component) {
        this.getSelectionModel().setSelectedIndex(this.getComponentIndex(component));
    }
    
    public boolean isBorderPainted() {
        return this.paintBorder;
    }
    
    public void setBorderPainted(final boolean paintBorder) {
        this.paintBorder = paintBorder;
        this.repaint();
    }
    
    @Override
    protected void paintBorder(final Graphics graphics) {
        if (this.isBorderPainted()) {
            super.paintBorder(graphics);
        }
    }
    
    public Insets getMargin() {
        if (this.margin == null) {
            return new Insets(0, 0, 0, 0);
        }
        return this.margin;
    }
    
    boolean isSubPopupMenu(final JPopupMenu popupMenu) {
        final int componentCount = this.getComponentCount();
        for (final Component component : this.getComponents()) {
            if (component instanceof JMenu) {
                final JPopupMenu popupMenu2 = ((JMenu)component).getPopupMenu();
                if (popupMenu2 == popupMenu) {
                    return true;
                }
                if (popupMenu2.isSubPopupMenu(popupMenu)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static Frame getFrame(final Component component) {
        Component parent;
        for (parent = component; !(parent instanceof Frame) && parent != null; parent = parent.getParent()) {}
        return (Frame)parent;
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",desiredLocationX=" + this.desiredLocationX + ",desiredLocationY=" + this.desiredLocationY + ",label=" + ((this.label != null) ? this.label : "") + ",lightWeightPopupEnabled=" + (this.isLightWeightPopupEnabled() ? "true" : "false") + ",margin=" + ((this.margin != null) ? this.margin.toString() : "") + ",paintBorder=" + (this.paintBorder ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJPopupMenu();
        }
        return this.accessibleContext;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Vector vector = new Vector();
        objectOutputStream.defaultWriteObject();
        if (this.invoker != null && this.invoker instanceof Serializable) {
            vector.addElement("invoker");
            vector.addElement(this.invoker);
        }
        if (this.popup != null && this.popup instanceof Serializable) {
            vector.addElement("popup");
            vector.addElement(this.popup);
        }
        objectOutputStream.writeObject(vector);
        if (this.getUIClassID().equals("PopupMenuUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Vector vector = (Vector)objectInputStream.readObject();
        int n = 0;
        final int size = vector.size();
        if (n < size && vector.elementAt(n).equals("invoker")) {
            this.invoker = (Component)vector.elementAt(++n);
            ++n;
        }
        if (n < size && vector.elementAt(n).equals("popup")) {
            this.popup = (Popup)vector.elementAt(++n);
            ++n;
        }
    }
    
    @Override
    public void processMouseEvent(final MouseEvent mouseEvent, final MenuElement[] array, final MenuSelectionManager menuSelectionManager) {
    }
    
    @Override
    public void processKeyEvent(final KeyEvent keyEvent, final MenuElement[] array, final MenuSelectionManager menuSelectionManager) {
        final MenuKeyEvent menuKeyEvent = new MenuKeyEvent(keyEvent.getComponent(), keyEvent.getID(), keyEvent.getWhen(), keyEvent.getModifiers(), keyEvent.getKeyCode(), keyEvent.getKeyChar(), array, menuSelectionManager);
        this.processMenuKeyEvent(menuKeyEvent);
        if (menuKeyEvent.isConsumed()) {
            keyEvent.consume();
        }
    }
    
    private void processMenuKeyEvent(final MenuKeyEvent menuKeyEvent) {
        switch (menuKeyEvent.getID()) {
            case 401: {
                this.fireMenuKeyPressed(menuKeyEvent);
                break;
            }
            case 402: {
                this.fireMenuKeyReleased(menuKeyEvent);
                break;
            }
            case 400: {
                this.fireMenuKeyTyped(menuKeyEvent);
                break;
            }
        }
    }
    
    private void fireMenuKeyPressed(final MenuKeyEvent menuKeyEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuKeyListener.class) {
                ((MenuKeyListener)listenerList[i + 1]).menuKeyPressed(menuKeyEvent);
            }
        }
    }
    
    private void fireMenuKeyReleased(final MenuKeyEvent menuKeyEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuKeyListener.class) {
                ((MenuKeyListener)listenerList[i + 1]).menuKeyReleased(menuKeyEvent);
            }
        }
    }
    
    private void fireMenuKeyTyped(final MenuKeyEvent menuKeyEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuKeyListener.class) {
                ((MenuKeyListener)listenerList[i + 1]).menuKeyTyped(menuKeyEvent);
            }
        }
    }
    
    @Override
    public void menuSelectionChanged(final boolean b) {
        if (this.invoker instanceof JMenu) {
            final JMenu menu = (JMenu)this.invoker;
            if (b) {
                menu.setPopupMenuVisible(true);
            }
            else {
                menu.setPopupMenuVisible(false);
            }
        }
        if (this.isPopupMenu() && !b) {
            this.setVisible(false);
        }
    }
    
    @Override
    public MenuElement[] getSubElements() {
        final Vector vector = new Vector();
        for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
            final Component component = this.getComponent(i);
            if (component instanceof MenuElement) {
                vector.addElement(component);
            }
        }
        final MenuElement[] array = new MenuElement[vector.size()];
        for (int j = 0; j < vector.size(); ++j) {
            array[j] = (MenuElement)vector.elementAt(j);
        }
        return array;
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    public boolean isPopupTrigger(final MouseEvent mouseEvent) {
        return this.getUI().isPopupTrigger(mouseEvent);
    }
    
    static {
        defaultLWPopupEnabledKey = new StringBuffer("JPopupMenu.defaultLWPopupEnabledKey");
        JPopupMenu.popupPostionFixDisabled = false;
        JPopupMenu.popupPostionFixDisabled = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("javax.swing.adjustPopupLocationToFit", "")).equals("false");
        classLock = new Object();
    }
    
    protected class AccessibleJPopupMenu extends AccessibleJComponent implements PropertyChangeListener
    {
        protected AccessibleJPopupMenu() {
            JPopupMenu.this.addPropertyChangeListener(this);
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.POPUP_MENU;
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName() == "visible") {
                if (propertyChangeEvent.getOldValue() == Boolean.FALSE && propertyChangeEvent.getNewValue() == Boolean.TRUE) {
                    this.handlePopupIsVisibleEvent(true);
                }
                else if (propertyChangeEvent.getOldValue() == Boolean.TRUE && propertyChangeEvent.getNewValue() == Boolean.FALSE) {
                    this.handlePopupIsVisibleEvent(false);
                }
            }
        }
        
        private void handlePopupIsVisibleEvent(final boolean b) {
            if (b) {
                this.firePropertyChange("AccessibleState", null, AccessibleState.VISIBLE);
                this.fireActiveDescendant();
            }
            else {
                this.firePropertyChange("AccessibleState", AccessibleState.VISIBLE, null);
            }
        }
        
        private void fireActiveDescendant() {
            if (JPopupMenu.this instanceof BasicComboPopup) {
                final JList list = ((BasicComboPopup)JPopupMenu.this).getList();
                if (list == null) {
                    return;
                }
                final AccessibleSelection accessibleSelection = list.getAccessibleContext().getAccessibleSelection();
                if (accessibleSelection == null) {
                    return;
                }
                final Accessible accessibleSelection2 = accessibleSelection.getAccessibleSelection(0);
                if (accessibleSelection2 == null) {
                    return;
                }
                final AccessibleContext accessibleContext = accessibleSelection2.getAccessibleContext();
                if (accessibleContext != null && JPopupMenu.this.invoker != null) {
                    final AccessibleContext accessibleContext2 = JPopupMenu.this.invoker.getAccessibleContext();
                    if (accessibleContext2 != null) {
                        accessibleContext2.firePropertyChange("AccessibleActiveDescendant", null, accessibleContext);
                    }
                }
            }
        }
    }
    
    public static class Separator extends JSeparator
    {
        public Separator() {
            super(0);
        }
        
        @Override
        public String getUIClassID() {
            return "PopupMenuSeparatorUI";
        }
    }
}
