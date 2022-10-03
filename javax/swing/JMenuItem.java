package javax.swing;

import javax.swing.event.ChangeEvent;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleRole;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusEvent;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.MenuDragMouseEvent;
import java.awt.event.MouseEvent;
import java.awt.Component;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.MenuItemUI;
import java.awt.event.FocusListener;
import javax.accessibility.Accessible;

public class JMenuItem extends AbstractButton implements Accessible, MenuElement
{
    private static final String uiClassID = "MenuItemUI";
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    private boolean isMouseDragged;
    private KeyStroke accelerator;
    
    public JMenuItem() {
        this(null, null);
    }
    
    public JMenuItem(final Icon icon) {
        this(null, icon);
    }
    
    public JMenuItem(final String s) {
        this(s, null);
    }
    
    public JMenuItem(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JMenuItem(final String s, final Icon icon) {
        this.isMouseDragged = false;
        this.setModel(new DefaultButtonModel());
        this.init(s, icon);
        this.initFocusability();
    }
    
    public JMenuItem(final String s, final int mnemonic) {
        this.isMouseDragged = false;
        this.setModel(new DefaultButtonModel());
        this.init(s, null);
        this.setMnemonic(mnemonic);
        this.initFocusability();
    }
    
    @Override
    public void setModel(final ButtonModel model) {
        super.setModel(model);
        if (model instanceof DefaultButtonModel) {
            ((DefaultButtonModel)model).setMenuItem(true);
        }
    }
    
    void initFocusability() {
        this.setFocusable(false);
    }
    
    @Override
    protected void init(final String text, final Icon icon) {
        if (text != null) {
            this.setText(text);
        }
        if (icon != null) {
            this.setIcon(icon);
        }
        this.addFocusListener(new MenuItemFocusListener());
        this.setUIProperty("borderPainted", Boolean.FALSE);
        this.setFocusPainted(false);
        this.setHorizontalTextPosition(11);
        this.setHorizontalAlignment(10);
        this.updateUI();
    }
    
    public void setUI(final MenuItemUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((MenuItemUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "MenuItemUI";
    }
    
    public void setArmed(final boolean armed) {
        final ButtonModel model = this.getModel();
        model.isArmed();
        if (model.isArmed() != armed) {
            model.setArmed(armed);
        }
    }
    
    public boolean isArmed() {
        return this.getModel().isArmed();
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        if (!enabled && !UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
            this.setArmed(false);
        }
        super.setEnabled(enabled);
    }
    
    @Override
    boolean alwaysOnTop() {
        return SwingUtilities.getAncestorOfClass(JInternalFrame.class, this) == null;
    }
    
    public void setAccelerator(final KeyStroke accelerator) {
        final KeyStroke accelerator2 = this.accelerator;
        this.accelerator = accelerator;
        this.repaint();
        this.revalidate();
        this.firePropertyChange("accelerator", accelerator2, this.accelerator);
    }
    
    public KeyStroke getAccelerator() {
        return this.accelerator;
    }
    
    @Override
    protected void configurePropertiesFromAction(final Action action) {
        super.configurePropertiesFromAction(action);
        this.configureAcceleratorFromAction(action);
    }
    
    @Override
    void setIconFromAction(final Action action) {
        Icon icon = null;
        if (action != null) {
            icon = (Icon)action.getValue("SmallIcon");
        }
        this.setIcon(icon);
    }
    
    @Override
    void largeIconChanged(final Action action) {
    }
    
    @Override
    void smallIconChanged(final Action iconFromAction) {
        this.setIconFromAction(iconFromAction);
    }
    
    void configureAcceleratorFromAction(final Action action) {
        this.setAccelerator((action == null) ? null : ((KeyStroke)action.getValue("AcceleratorKey")));
    }
    
    @Override
    protected void actionPropertyChanged(final Action action, final String s) {
        if (s == "AcceleratorKey") {
            this.configureAcceleratorFromAction(action);
        }
        else {
            super.actionPropertyChanged(action, s);
        }
    }
    
    @Override
    public void processMouseEvent(final MouseEvent mouseEvent, final MenuElement[] array, final MenuSelectionManager menuSelectionManager) {
        this.processMenuDragMouseEvent(new MenuDragMouseEvent(mouseEvent.getComponent(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), array, menuSelectionManager));
    }
    
    @Override
    public void processKeyEvent(final KeyEvent keyEvent, final MenuElement[] array, final MenuSelectionManager menuSelectionManager) {
        final MenuKeyEvent menuKeyEvent = new MenuKeyEvent(keyEvent.getComponent(), keyEvent.getID(), keyEvent.getWhen(), keyEvent.getModifiers(), keyEvent.getKeyCode(), keyEvent.getKeyChar(), array, menuSelectionManager);
        this.processMenuKeyEvent(menuKeyEvent);
        if (menuKeyEvent.isConsumed()) {
            keyEvent.consume();
        }
    }
    
    public void processMenuDragMouseEvent(final MenuDragMouseEvent menuDragMouseEvent) {
        switch (menuDragMouseEvent.getID()) {
            case 504: {
                this.isMouseDragged = false;
                this.fireMenuDragMouseEntered(menuDragMouseEvent);
                break;
            }
            case 505: {
                this.isMouseDragged = false;
                this.fireMenuDragMouseExited(menuDragMouseEvent);
                break;
            }
            case 506: {
                this.isMouseDragged = true;
                this.fireMenuDragMouseDragged(menuDragMouseEvent);
                break;
            }
            case 502: {
                if (this.isMouseDragged) {
                    this.fireMenuDragMouseReleased(menuDragMouseEvent);
                    break;
                }
                break;
            }
        }
    }
    
    public void processMenuKeyEvent(final MenuKeyEvent menuKeyEvent) {
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
    
    protected void fireMenuDragMouseEntered(final MenuDragMouseEvent menuDragMouseEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuDragMouseListener.class) {
                ((MenuDragMouseListener)listenerList[i + 1]).menuDragMouseEntered(menuDragMouseEvent);
            }
        }
    }
    
    protected void fireMenuDragMouseExited(final MenuDragMouseEvent menuDragMouseEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuDragMouseListener.class) {
                ((MenuDragMouseListener)listenerList[i + 1]).menuDragMouseExited(menuDragMouseEvent);
            }
        }
    }
    
    protected void fireMenuDragMouseDragged(final MenuDragMouseEvent menuDragMouseEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuDragMouseListener.class) {
                ((MenuDragMouseListener)listenerList[i + 1]).menuDragMouseDragged(menuDragMouseEvent);
            }
        }
    }
    
    protected void fireMenuDragMouseReleased(final MenuDragMouseEvent menuDragMouseEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuDragMouseListener.class) {
                ((MenuDragMouseListener)listenerList[i + 1]).menuDragMouseReleased(menuDragMouseEvent);
            }
        }
    }
    
    protected void fireMenuKeyPressed(final MenuKeyEvent menuKeyEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuKeyListener.class) {
                ((MenuKeyListener)listenerList[i + 1]).menuKeyPressed(menuKeyEvent);
            }
        }
    }
    
    protected void fireMenuKeyReleased(final MenuKeyEvent menuKeyEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuKeyListener.class) {
                ((MenuKeyListener)listenerList[i + 1]).menuKeyReleased(menuKeyEvent);
            }
        }
    }
    
    protected void fireMenuKeyTyped(final MenuKeyEvent menuKeyEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == MenuKeyListener.class) {
                ((MenuKeyListener)listenerList[i + 1]).menuKeyTyped(menuKeyEvent);
            }
        }
    }
    
    @Override
    public void menuSelectionChanged(final boolean armed) {
        this.setArmed(armed);
    }
    
    @Override
    public MenuElement[] getSubElements() {
        return new MenuElement[0];
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    public void addMenuDragMouseListener(final MenuDragMouseListener menuDragMouseListener) {
        this.listenerList.add(MenuDragMouseListener.class, menuDragMouseListener);
    }
    
    public void removeMenuDragMouseListener(final MenuDragMouseListener menuDragMouseListener) {
        this.listenerList.remove(MenuDragMouseListener.class, menuDragMouseListener);
    }
    
    public MenuDragMouseListener[] getMenuDragMouseListeners() {
        return this.listenerList.getListeners(MenuDragMouseListener.class);
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
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.getUIClassID().equals("MenuItemUI")) {
            this.updateUI();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("MenuItemUI")) {
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
            this.accessibleContext = new AccessibleJMenuItem();
        }
        return this.accessibleContext;
    }
    
    private static class MenuItemFocusListener implements FocusListener, Serializable
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            final JMenuItem menuItem = (JMenuItem)focusEvent.getSource();
            if (menuItem.isFocusPainted()) {
                menuItem.repaint();
            }
        }
    }
    
    protected class AccessibleJMenuItem extends AccessibleAbstractButton implements ChangeListener
    {
        private boolean isArmed;
        private boolean hasFocus;
        private boolean isPressed;
        private boolean isSelected;
        
        AccessibleJMenuItem() {
            this.isArmed = false;
            this.hasFocus = false;
            this.isPressed = false;
            this.isSelected = false;
            JMenuItem.this.addChangeListener(this);
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_ITEM;
        }
        
        private void fireAccessibilityFocusedEvent(final JMenuItem menuItem) {
            final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
            if (selectedPath.length > 0 && menuItem == selectedPath[selectedPath.length - 1]) {
                this.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.firePropertyChange("AccessibleVisibleData", false, true);
            if (JMenuItem.this.getModel().isArmed()) {
                if (!this.isArmed) {
                    this.isArmed = true;
                    this.firePropertyChange("AccessibleState", null, AccessibleState.ARMED);
                    this.fireAccessibilityFocusedEvent(JMenuItem.this);
                }
            }
            else if (this.isArmed) {
                this.isArmed = false;
                this.firePropertyChange("AccessibleState", AccessibleState.ARMED, null);
            }
            if (JMenuItem.this.isFocusOwner()) {
                if (!this.hasFocus) {
                    this.hasFocus = true;
                    this.firePropertyChange("AccessibleState", null, AccessibleState.FOCUSED);
                }
            }
            else if (this.hasFocus) {
                this.hasFocus = false;
                this.firePropertyChange("AccessibleState", AccessibleState.FOCUSED, null);
            }
            if (JMenuItem.this.getModel().isPressed()) {
                if (!this.isPressed) {
                    this.isPressed = true;
                    this.firePropertyChange("AccessibleState", null, AccessibleState.PRESSED);
                }
            }
            else if (this.isPressed) {
                this.isPressed = false;
                this.firePropertyChange("AccessibleState", AccessibleState.PRESSED, null);
            }
            if (JMenuItem.this.getModel().isSelected()) {
                if (!this.isSelected) {
                    this.isSelected = true;
                    this.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
                    this.fireAccessibilityFocusedEvent(JMenuItem.this);
                }
            }
            else if (this.isSelected) {
                this.isSelected = false;
                this.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
            }
        }
    }
}
