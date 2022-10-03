package java.awt;

import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.awt.peer.MenuBarPeer;
import java.util.Vector;
import javax.accessibility.Accessible;

public class MenuBar extends MenuComponent implements MenuContainer, Accessible
{
    Vector<Menu> menus;
    Menu helpMenu;
    private static final String base = "menubar";
    private static int nameCounter;
    private static final long serialVersionUID = -4930327919388951260L;
    private int menuBarSerializedDataVersion;
    
    public MenuBar() throws HeadlessException {
        this.menus = new Vector<Menu>();
        this.menuBarSerializedDataVersion = 1;
    }
    
    @Override
    String constructComponentName() {
        synchronized (MenuBar.class) {
            return "menubar" + MenuBar.nameCounter++;
        }
    }
    
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = Toolkit.getDefaultToolkit().createMenuBar(this);
            }
            for (int menuCount = this.getMenuCount(), i = 0; i < menuCount; ++i) {
                this.getMenu(i).addNotify();
            }
        }
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            for (int menuCount = this.getMenuCount(), i = 0; i < menuCount; ++i) {
                this.getMenu(i).removeNotify();
            }
            super.removeNotify();
        }
    }
    
    public Menu getHelpMenu() {
        return this.helpMenu;
    }
    
    public void setHelpMenu(final Menu helpMenu) {
        synchronized (this.getTreeLock()) {
            if (this.helpMenu == helpMenu) {
                return;
            }
            if (this.helpMenu != null) {
                this.remove(this.helpMenu);
            }
            if ((this.helpMenu = helpMenu) != null) {
                if (helpMenu.parent != this) {
                    this.add(helpMenu);
                }
                helpMenu.isHelpMenu = true;
                helpMenu.parent = this;
                final MenuBarPeer menuBarPeer = (MenuBarPeer)this.peer;
                if (menuBarPeer != null) {
                    if (helpMenu.peer == null) {
                        helpMenu.addNotify();
                    }
                    menuBarPeer.addHelpMenu(helpMenu);
                }
            }
        }
    }
    
    public Menu add(final Menu menu) {
        synchronized (this.getTreeLock()) {
            if (menu.parent != null) {
                menu.parent.remove(menu);
            }
            menu.parent = this;
            final MenuBarPeer menuBarPeer = (MenuBarPeer)this.peer;
            if (menuBarPeer != null) {
                if (menu.peer == null) {
                    menu.addNotify();
                }
                this.menus.addElement(menu);
                menuBarPeer.addMenu(menu);
            }
            else {
                this.menus.addElement(menu);
            }
            return menu;
        }
    }
    
    public void remove(final int n) {
        synchronized (this.getTreeLock()) {
            final Menu menu = this.getMenu(n);
            this.menus.removeElementAt(n);
            final MenuBarPeer menuBarPeer = (MenuBarPeer)this.peer;
            if (menuBarPeer != null) {
                menuBarPeer.delMenu(n);
                menu.removeNotify();
                menu.parent = null;
            }
            if (this.helpMenu == menu) {
                this.helpMenu = null;
                menu.isHelpMenu = false;
            }
        }
    }
    
    @Override
    public void remove(final MenuComponent menuComponent) {
        synchronized (this.getTreeLock()) {
            final int index = this.menus.indexOf(menuComponent);
            if (index >= 0) {
                this.remove(index);
            }
        }
    }
    
    public int getMenuCount() {
        return this.countMenus();
    }
    
    @Deprecated
    public int countMenus() {
        return this.getMenuCountImpl();
    }
    
    final int getMenuCountImpl() {
        return this.menus.size();
    }
    
    public Menu getMenu(final int n) {
        return this.getMenuImpl(n);
    }
    
    final Menu getMenuImpl(final int n) {
        return this.menus.elementAt(n);
    }
    
    public synchronized Enumeration<MenuShortcut> shortcuts() {
        final Vector vector = new Vector();
        for (int menuCount = this.getMenuCount(), i = 0; i < menuCount; ++i) {
            final Enumeration<MenuShortcut> shortcuts = this.getMenu(i).shortcuts();
            while (shortcuts.hasMoreElements()) {
                vector.addElement(shortcuts.nextElement());
            }
        }
        return vector.elements();
    }
    
    public MenuItem getShortcutMenuItem(final MenuShortcut menuShortcut) {
        for (int menuCount = this.getMenuCount(), i = 0; i < menuCount; ++i) {
            final MenuItem shortcutMenuItem = this.getMenu(i).getShortcutMenuItem(menuShortcut);
            if (shortcutMenuItem != null) {
                return shortcutMenuItem;
            }
        }
        return null;
    }
    
    boolean handleShortcut(final KeyEvent keyEvent) {
        final int id = keyEvent.getID();
        if (id != 401 && id != 402) {
            return false;
        }
        if ((keyEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0x0) {
            return false;
        }
        for (int menuCount = this.getMenuCount(), i = 0; i < menuCount; ++i) {
            if (this.getMenu(i).handleShortcut(keyEvent)) {
                return true;
            }
        }
        return false;
    }
    
    public void deleteShortcut(final MenuShortcut menuShortcut) {
        for (int menuCount = this.getMenuCount(), i = 0; i < menuCount; ++i) {
            this.getMenu(i).deleteShortcut(menuShortcut);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws ClassNotFoundException, IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        objectInputStream.defaultReadObject();
        for (int i = 0; i < this.menus.size(); ++i) {
            this.menus.elementAt(i).parent = this;
        }
    }
    
    private static native void initIDs();
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTMenuBar();
        }
        return this.accessibleContext;
    }
    
    @Override
    int getAccessibleChildIndex(final MenuComponent menuComponent) {
        return this.menus.indexOf(menuComponent);
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setMenuBarAccessor(new AWTAccessor.MenuBarAccessor() {
            @Override
            public Menu getHelpMenu(final MenuBar menuBar) {
                return menuBar.helpMenu;
            }
            
            @Override
            public Vector<Menu> getMenus(final MenuBar menuBar) {
                return menuBar.menus;
            }
        });
        MenuBar.nameCounter = 0;
    }
    
    protected class AccessibleAWTMenuBar extends AccessibleAWTMenuComponent
    {
        private static final long serialVersionUID = -8577604491830083815L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_BAR;
        }
    }
}
