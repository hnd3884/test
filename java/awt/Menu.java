package java.awt;

import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.util.Vector;
import javax.accessibility.Accessible;

public class Menu extends MenuItem implements MenuContainer, Accessible
{
    Vector<MenuComponent> items;
    boolean tearOff;
    boolean isHelpMenu;
    private static final String base = "menu";
    private static int nameCounter;
    private static final long serialVersionUID = -8809584163345499784L;
    private int menuSerializedDataVersion;
    
    public Menu() throws HeadlessException {
        this("", false);
    }
    
    public Menu(final String s) throws HeadlessException {
        this(s, false);
    }
    
    public Menu(final String s, final boolean tearOff) throws HeadlessException {
        super(s);
        this.items = new Vector<MenuComponent>();
        this.menuSerializedDataVersion = 1;
        this.tearOff = tearOff;
    }
    
    @Override
    String constructComponentName() {
        synchronized (Menu.class) {
            return "menu" + Menu.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = Toolkit.getDefaultToolkit().createMenu(this);
            }
            for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
                final MenuItem item = this.getItem(i);
                item.parent = this;
                item.addNotify();
            }
        }
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
                this.getItem(i).removeNotify();
            }
            super.removeNotify();
        }
    }
    
    public boolean isTearOff() {
        return this.tearOff;
    }
    
    public int getItemCount() {
        return this.countItems();
    }
    
    @Deprecated
    public int countItems() {
        return this.countItemsImpl();
    }
    
    final int countItemsImpl() {
        return this.items.size();
    }
    
    public MenuItem getItem(final int n) {
        return this.getItemImpl(n);
    }
    
    final MenuItem getItemImpl(final int n) {
        return this.items.elementAt(n);
    }
    
    public MenuItem add(final MenuItem menuItem) {
        synchronized (this.getTreeLock()) {
            if (menuItem.parent != null) {
                menuItem.parent.remove(menuItem);
            }
            this.items.addElement(menuItem);
            menuItem.parent = this;
            final MenuPeer menuPeer = (MenuPeer)this.peer;
            if (menuPeer != null) {
                menuItem.addNotify();
                menuPeer.addItem(menuItem);
            }
            return menuItem;
        }
    }
    
    public void add(final String s) {
        this.add(new MenuItem(s));
    }
    
    public void insert(final MenuItem menuItem, final int n) {
        synchronized (this.getTreeLock()) {
            if (n < 0) {
                throw new IllegalArgumentException("index less than zero.");
            }
            final int itemCount = this.getItemCount();
            final Vector vector = new Vector();
            for (int i = n; i < itemCount; ++i) {
                vector.addElement(this.getItem(n));
                this.remove(n);
            }
            this.add(menuItem);
            for (int j = 0; j < vector.size(); ++j) {
                this.add((MenuItem)vector.elementAt(j));
            }
        }
    }
    
    public void insert(final String s, final int n) {
        this.insert(new MenuItem(s), n);
    }
    
    public void addSeparator() {
        this.add("-");
    }
    
    public void insertSeparator(final int n) {
        synchronized (this.getTreeLock()) {
            if (n < 0) {
                throw new IllegalArgumentException("index less than zero.");
            }
            final int itemCount = this.getItemCount();
            final Vector vector = new Vector();
            for (int i = n; i < itemCount; ++i) {
                vector.addElement(this.getItem(n));
                this.remove(n);
            }
            this.addSeparator();
            for (int j = 0; j < vector.size(); ++j) {
                this.add((MenuItem)vector.elementAt(j));
            }
        }
    }
    
    public void remove(final int n) {
        synchronized (this.getTreeLock()) {
            final MenuItem item = this.getItem(n);
            this.items.removeElementAt(n);
            final MenuPeer menuPeer = (MenuPeer)this.peer;
            if (menuPeer != null) {
                menuPeer.delItem(n);
                item.removeNotify();
                item.parent = null;
            }
        }
    }
    
    @Override
    public void remove(final MenuComponent menuComponent) {
        synchronized (this.getTreeLock()) {
            final int index = this.items.indexOf(menuComponent);
            if (index >= 0) {
                this.remove(index);
            }
        }
    }
    
    public void removeAll() {
        synchronized (this.getTreeLock()) {
            for (int i = this.getItemCount() - 1; i >= 0; --i) {
                this.remove(i);
            }
        }
    }
    
    @Override
    boolean handleShortcut(final KeyEvent keyEvent) {
        for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
            if (this.getItem(i).handleShortcut(keyEvent)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    MenuItem getShortcutMenuItem(final MenuShortcut menuShortcut) {
        for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
            final MenuItem shortcutMenuItem = this.getItem(i).getShortcutMenuItem(menuShortcut);
            if (shortcutMenuItem != null) {
                return shortcutMenuItem;
            }
        }
        return null;
    }
    
    synchronized Enumeration<MenuShortcut> shortcuts() {
        final Vector vector = new Vector();
        for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
            final MenuItem item = this.getItem(i);
            if (item instanceof Menu) {
                final Enumeration<MenuShortcut> shortcuts = ((Menu)item).shortcuts();
                while (shortcuts.hasMoreElements()) {
                    vector.addElement(shortcuts.nextElement());
                }
            }
            else {
                final MenuShortcut shortcut = item.getShortcut();
                if (shortcut != null) {
                    vector.addElement(shortcut);
                }
            }
        }
        return vector.elements();
    }
    
    @Override
    void deleteShortcut(final MenuShortcut menuShortcut) {
        for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
            this.getItem(i).deleteShortcut(menuShortcut);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException, HeadlessException {
        objectInputStream.defaultReadObject();
        for (int i = 0; i < this.items.size(); ++i) {
            ((MenuItem)this.items.elementAt(i)).parent = this;
        }
    }
    
    @Override
    public String paramString() {
        return super.paramString() + (",tearOff=" + this.tearOff + ",isHelpMenu=" + this.isHelpMenu);
    }
    
    private static native void initIDs();
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTMenu();
        }
        return this.accessibleContext;
    }
    
    @Override
    int getAccessibleChildIndex(final MenuComponent menuComponent) {
        return this.items.indexOf(menuComponent);
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setMenuAccessor(new AWTAccessor.MenuAccessor() {
            @Override
            public Vector<MenuComponent> getItems(final Menu menu) {
                return menu.items;
            }
        });
        Menu.nameCounter = 0;
    }
    
    protected class AccessibleAWTMenu extends AccessibleAWTMenuItem
    {
        private static final long serialVersionUID = 5228160894980069094L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU;
        }
    }
}
