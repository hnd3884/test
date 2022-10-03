package javax.swing.plaf.basic;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.event.MenuKeyEvent;
import java.awt.Point;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.JMenuItem;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.Container;
import javax.swing.JPopupMenu;
import javax.swing.JMenuBar;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import javax.swing.plaf.UIResource;
import javax.swing.MenuSelectionManager;
import javax.swing.MenuElement;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.Dimension;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuDragMouseListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import sun.swing.DefaultLookup;
import javax.swing.UIManager;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.InputMap;
import javax.swing.event.MenuListener;
import javax.swing.event.ChangeListener;

public class BasicMenuUI extends BasicMenuItemUI
{
    protected ChangeListener changeListener;
    protected MenuListener menuListener;
    private int lastMnemonic;
    private InputMap selectedWindowInputMap;
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    private static boolean crossMenuMnemonic;
    
    public BasicMenuUI() {
        this.lastMnemonic = 0;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicMenuUI();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        BasicMenuItemUI.loadActionMap(lazyActionMap);
        lazyActionMap.put(new Actions("selectMenu", null, true));
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.updateDefaultBackgroundColor();
        ((JMenu)this.menuItem).setDelay(200);
        BasicMenuUI.crossMenuMnemonic = UIManager.getBoolean("Menu.crossMenuMnemonic");
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "Menu";
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        if (this.changeListener == null) {
            this.changeListener = this.createChangeListener(this.menuItem);
        }
        if (this.changeListener != null) {
            this.menuItem.addChangeListener(this.changeListener);
        }
        if (this.menuListener == null) {
            this.menuListener = this.createMenuListener(this.menuItem);
        }
        if (this.menuListener != null) {
            ((JMenu)this.menuItem).addMenuListener(this.menuListener);
        }
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        this.updateMnemonicBinding();
    }
    
    @Override
    void installLazyActionMap() {
        LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuUI.class, this.getPropertyPrefix() + ".actionMap");
    }
    
    void updateMnemonicBinding() {
        final int mnemonic = this.menuItem.getModel().getMnemonic();
        int[] array = (int[])DefaultLookup.get(this.menuItem, this, "Menu.shortcutKeys");
        if (array == null) {
            array = new int[] { 8 };
        }
        if (mnemonic == this.lastMnemonic) {
            return;
        }
        InputMap inputMap = SwingUtilities.getUIInputMap(this.menuItem, 2);
        if (this.lastMnemonic != 0 && inputMap != null) {
            final int[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                inputMap.remove(KeyStroke.getKeyStroke(this.lastMnemonic, array2[i], false));
            }
        }
        if (mnemonic != 0) {
            if (inputMap == null) {
                inputMap = this.createInputMap(2);
                SwingUtilities.replaceUIInputMap(this.menuItem, 2, inputMap);
            }
            final int[] array3 = array;
            for (int length2 = array3.length, j = 0; j < length2; ++j) {
                inputMap.put(KeyStroke.getKeyStroke(mnemonic, array3[j], false), "selectMenu");
            }
        }
        this.lastMnemonic = mnemonic;
    }
    
    @Override
    protected void uninstallKeyboardActions() {
        super.uninstallKeyboardActions();
        this.lastMnemonic = 0;
    }
    
    @Override
    protected MouseInputListener createMouseInputListener(final JComponent component) {
        return this.getHandler();
    }
    
    protected MenuListener createMenuListener(final JComponent component) {
        return null;
    }
    
    protected ChangeListener createChangeListener(final JComponent component) {
        return null;
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener(final JComponent component) {
        return this.getHandler();
    }
    
    @Override
    BasicMenuItemUI.Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    @Override
    protected void uninstallDefaults() {
        this.menuItem.setArmed(false);
        this.menuItem.setSelected(false);
        this.menuItem.resetKeyboardActions();
        super.uninstallDefaults();
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        if (this.changeListener != null) {
            this.menuItem.removeChangeListener(this.changeListener);
        }
        if (this.menuListener != null) {
            ((JMenu)this.menuItem).removeMenuListener(this.menuListener);
        }
        this.changeListener = null;
        this.menuListener = null;
        this.handler = null;
    }
    
    @Override
    protected MenuDragMouseListener createMenuDragMouseListener(final JComponent component) {
        return this.getHandler();
    }
    
    @Override
    protected MenuKeyListener createMenuKeyListener(final JComponent component) {
        return (MenuKeyListener)this.getHandler();
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        if (((JMenu)this.menuItem).isTopLevelMenu()) {
            return new Dimension(component.getPreferredSize().width, 32767);
        }
        return null;
    }
    
    protected void setupPostTimer(final JMenu menu) {
        final Timer timer = new Timer(menu.getDelay(), new Actions("selectMenu", menu, false));
        timer.setRepeats(false);
        timer.start();
    }
    
    private static void appendPath(final MenuElement[] array, final MenuElement menuElement) {
        final MenuElement[] selectedPath = new MenuElement[array.length + 1];
        System.arraycopy(array, 0, selectedPath, 0, array.length);
        selectedPath[array.length] = menuElement;
        MenuSelectionManager.defaultManager().setSelectedPath(selectedPath);
    }
    
    private void updateDefaultBackgroundColor() {
        if (!UIManager.getBoolean("Menu.useMenuBarBackgroundForTopLevel")) {
            return;
        }
        final JMenu menu = (JMenu)this.menuItem;
        if (menu.getBackground() instanceof UIResource) {
            if (menu.isTopLevelMenu()) {
                menu.setBackground(UIManager.getColor("MenuBar.background"));
            }
            else {
                menu.setBackground(UIManager.getColor(this.getPropertyPrefix() + ".background"));
            }
        }
    }
    
    static {
        BasicMenuUI.crossMenuMnemonic = true;
    }
    
    private static class Actions extends UIAction
    {
        private static final String SELECT = "selectMenu";
        private JMenu menu;
        private boolean force;
        
        Actions(final String s, final JMenu menu, final boolean force) {
            super(s);
            this.force = false;
            this.menu = menu;
            this.force = force;
        }
        
        private JMenu getMenu(final ActionEvent actionEvent) {
            if (actionEvent.getSource() instanceof JMenu) {
                return (JMenu)actionEvent.getSource();
            }
            return this.menu;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JMenu menu = this.getMenu(actionEvent);
            if (!BasicMenuUI.crossMenuMnemonic) {
                final JPopupMenu lastPopup = BasicPopupMenuUI.getLastPopup();
                if (lastPopup != null && lastPopup != menu.getParent()) {
                    return;
                }
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if (this.force) {
                final Container parent = menu.getParent();
                if (parent != null && parent instanceof JMenuBar) {
                    final MenuElement[] subElements = menu.getPopupMenu().getSubElements();
                    MenuElement[] selectedPath;
                    if (subElements.length > 0) {
                        selectedPath = new MenuElement[] { (MenuElement)parent, menu, menu.getPopupMenu(), subElements[0] };
                    }
                    else {
                        selectedPath = new MenuElement[] { (MenuElement)parent, menu, menu.getPopupMenu() };
                    }
                    defaultManager.setSelectedPath(selectedPath);
                }
            }
            else {
                final MenuElement[] selectedPath2 = defaultManager.getSelectedPath();
                if (selectedPath2.length > 0 && selectedPath2[selectedPath2.length - 1] == menu) {
                    appendPath(selectedPath2, menu.getPopupMenu());
                }
            }
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            return !(o instanceof JMenu) || ((JMenu)o).isEnabled();
        }
    }
    
    protected class MouseInputHandler implements MouseInputListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mouseClicked(mouseEvent);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mouseReleased(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mouseExited(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mouseDragged(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicMenuUI.this.getHandler().mouseMoved(mouseEvent);
        }
    }
    
    public class ChangeHandler implements ChangeListener
    {
        public JMenu menu;
        public BasicMenuUI ui;
        public boolean isSelected;
        public Component wasFocused;
        
        public ChangeHandler(final JMenu menu, final BasicMenuUI ui) {
            this.isSelected = false;
            this.menu = menu;
            this.ui = ui;
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
        }
    }
    
    private class Handler extends BasicMenuItemUI.Handler implements MenuKeyListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName() == "mnemonic") {
                BasicMenuUI.this.updateMnemonicBinding();
            }
            else {
                if (propertyChangeEvent.getPropertyName().equals("ancestor")) {
                    BasicMenuUI.this.updateDefaultBackgroundColor();
                }
                super.propertyChange(propertyChangeEvent);
            }
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            final JMenu menu = (JMenu)BasicMenuUI.this.menuItem;
            if (!menu.isEnabled()) {
                return;
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if (menu.isTopLevelMenu()) {
                if (menu.isSelected() && menu.getPopupMenu().isShowing()) {
                    defaultManager.clearSelectedPath();
                }
                else {
                    final Container parent = menu.getParent();
                    if (parent != null && parent instanceof JMenuBar) {
                        defaultManager.setSelectedPath(new MenuElement[] { (MenuElement)parent, menu });
                    }
                }
            }
            final MenuElement[] selectedPath = defaultManager.getSelectedPath();
            if (selectedPath.length > 0 && selectedPath[selectedPath.length - 1] != menu.getPopupMenu()) {
                if (menu.isTopLevelMenu() || menu.getDelay() == 0) {
                    appendPath(selectedPath, menu.getPopupMenu());
                }
                else {
                    BasicMenuUI.this.setupPostTimer(menu);
                }
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (!BasicMenuUI.this.menuItem.isEnabled()) {
                return;
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            defaultManager.processMouseEvent(mouseEvent);
            if (!mouseEvent.isConsumed()) {
                defaultManager.clearSelectedPath();
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            final JMenu menu = (JMenu)BasicMenuUI.this.menuItem;
            if (!menu.isEnabled() && !UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
                return;
            }
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final MenuElement[] selectedPath = defaultManager.getSelectedPath();
            if (!menu.isTopLevelMenu()) {
                if (selectedPath.length <= 0 || selectedPath[selectedPath.length - 1] != menu.getPopupMenu()) {
                    if (menu.getDelay() == 0) {
                        appendPath(BasicMenuUI.this.getPath(), menu.getPopupMenu());
                    }
                    else {
                        defaultManager.setSelectedPath(BasicMenuUI.this.getPath());
                        BasicMenuUI.this.setupPostTimer(menu);
                    }
                }
            }
            else if (selectedPath.length > 0 && selectedPath[0] == menu.getParent()) {
                final MenuElement[] selectedPath2 = { (MenuElement)menu.getParent(), menu, null };
                if (BasicPopupMenuUI.getLastPopup() != null) {
                    selectedPath2[2] = menu.getPopupMenu();
                }
                defaultManager.setSelectedPath(selectedPath2);
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (!BasicMenuUI.this.menuItem.isEnabled()) {
                return;
            }
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void menuDragMouseEntered(final MenuDragMouseEvent menuDragMouseEvent) {
        }
        
        @Override
        public void menuDragMouseDragged(final MenuDragMouseEvent menuDragMouseEvent) {
            if (!BasicMenuUI.this.menuItem.isEnabled()) {
                return;
            }
            final MenuSelectionManager menuSelectionManager = menuDragMouseEvent.getMenuSelectionManager();
            final MenuElement[] path = menuDragMouseEvent.getPath();
            final Point point = menuDragMouseEvent.getPoint();
            if (point.x >= 0 && point.x < BasicMenuUI.this.menuItem.getWidth() && point.y >= 0 && point.y < BasicMenuUI.this.menuItem.getHeight()) {
                final JMenu menu = (JMenu)BasicMenuUI.this.menuItem;
                final MenuElement[] selectedPath = menuSelectionManager.getSelectedPath();
                if (selectedPath.length <= 0 || selectedPath[selectedPath.length - 1] != menu.getPopupMenu()) {
                    if (menu.isTopLevelMenu() || menu.getDelay() == 0 || menuDragMouseEvent.getID() == 506) {
                        appendPath(path, menu.getPopupMenu());
                    }
                    else {
                        menuSelectionManager.setSelectedPath(path);
                        BasicMenuUI.this.setupPostTimer(menu);
                    }
                }
            }
            else if (menuDragMouseEvent.getID() == 502 && menuSelectionManager.componentForPoint(menuDragMouseEvent.getComponent(), menuDragMouseEvent.getPoint()) == null) {
                menuSelectionManager.clearSelectedPath();
            }
        }
        
        @Override
        public void menuDragMouseExited(final MenuDragMouseEvent menuDragMouseEvent) {
        }
        
        @Override
        public void menuDragMouseReleased(final MenuDragMouseEvent menuDragMouseEvent) {
        }
        
        @Override
        public void menuKeyTyped(final MenuKeyEvent menuKeyEvent) {
            if (!BasicMenuUI.crossMenuMnemonic && BasicPopupMenuUI.getLastPopup() != null) {
                return;
            }
            if (BasicPopupMenuUI.getPopups().size() != 0) {
                return;
            }
            final char lowerCase = Character.toLowerCase((char)BasicMenuUI.this.menuItem.getMnemonic());
            final MenuElement[] path = menuKeyEvent.getPath();
            if (lowerCase == Character.toLowerCase(menuKeyEvent.getKeyChar())) {
                final JPopupMenu popupMenu = ((JMenu)BasicMenuUI.this.menuItem).getPopupMenu();
                final ArrayList list = new ArrayList<JPopupMenu>(Arrays.asList(path));
                list.add(popupMenu);
                final MenuElement enabledChild = BasicPopupMenuUI.findEnabledChild(popupMenu.getSubElements(), -1, true);
                if (enabledChild != null) {
                    list.add((JPopupMenu)enabledChild);
                }
                menuKeyEvent.getMenuSelectionManager().setSelectedPath(list.toArray(new MenuElement[0]));
                menuKeyEvent.consume();
            }
        }
        
        @Override
        public void menuKeyPressed(final MenuKeyEvent menuKeyEvent) {
        }
        
        @Override
        public void menuKeyReleased(final MenuKeyEvent menuKeyEvent) {
        }
    }
}
