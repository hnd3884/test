package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleAction;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.peer.MenuItemPeer;
import java.awt.event.ActionListener;
import javax.accessibility.Accessible;

public class MenuItem extends MenuComponent implements Accessible
{
    boolean enabled;
    String label;
    String actionCommand;
    long eventMask;
    transient ActionListener actionListener;
    private MenuShortcut shortcut;
    private static final String base = "menuitem";
    private static int nameCounter;
    private static final long serialVersionUID = -21757335363267194L;
    private int menuItemSerializedDataVersion;
    
    public MenuItem() throws HeadlessException {
        this("", null);
    }
    
    public MenuItem(final String s) throws HeadlessException {
        this(s, null);
    }
    
    public MenuItem(final String label, final MenuShortcut shortcut) throws HeadlessException {
        this.enabled = true;
        this.shortcut = null;
        this.menuItemSerializedDataVersion = 1;
        this.label = label;
        this.shortcut = shortcut;
    }
    
    @Override
    String constructComponentName() {
        synchronized (MenuItem.class) {
            return "menuitem" + MenuItem.nameCounter++;
        }
    }
    
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = Toolkit.getDefaultToolkit().createMenuItem(this);
            }
        }
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public synchronized void setLabel(final String s) {
        this.label = s;
        final MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
        if (menuItemPeer != null) {
            menuItemPeer.setLabel(s);
        }
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public synchronized void setEnabled(final boolean b) {
        this.enable(b);
    }
    
    @Deprecated
    public synchronized void enable() {
        this.enabled = true;
        final MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
        if (menuItemPeer != null) {
            menuItemPeer.setEnabled(true);
        }
    }
    
    @Deprecated
    public void enable(final boolean b) {
        if (b) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    @Deprecated
    public synchronized void disable() {
        this.enabled = false;
        final MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
        if (menuItemPeer != null) {
            menuItemPeer.setEnabled(false);
        }
    }
    
    public MenuShortcut getShortcut() {
        return this.shortcut;
    }
    
    public void setShortcut(final MenuShortcut shortcut) {
        this.shortcut = shortcut;
        final MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
        if (menuItemPeer != null) {
            menuItemPeer.setLabel(this.label);
        }
    }
    
    public void deleteShortcut() {
        this.shortcut = null;
        final MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
        if (menuItemPeer != null) {
            menuItemPeer.setLabel(this.label);
        }
    }
    
    void deleteShortcut(final MenuShortcut menuShortcut) {
        if (menuShortcut.equals(this.shortcut)) {
            this.shortcut = null;
            final MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
            if (menuItemPeer != null) {
                menuItemPeer.setLabel(this.label);
            }
        }
    }
    
    void doMenuEvent(final long n, final int n2) {
        Toolkit.getEventQueue().postEvent(new ActionEvent(this, 1001, this.getActionCommand(), n, n2));
    }
    
    private final boolean isItemEnabled() {
        if (!this.isEnabled()) {
            return false;
        }
        MenuContainer menuContainer = this.getParent_NoClientCode();
        while (menuContainer instanceof Menu) {
            final Menu menu = (Menu)menuContainer;
            if (!menu.isEnabled()) {
                return false;
            }
            menuContainer = menu.getParent_NoClientCode();
            if (menuContainer == null) {
                return true;
            }
        }
        return true;
    }
    
    boolean handleShortcut(final KeyEvent keyEvent) {
        final MenuShortcut menuShortcut = new MenuShortcut(keyEvent.getKeyCode(), (keyEvent.getModifiers() & 0x1) > 0);
        final MenuShortcut menuShortcut2 = new MenuShortcut(keyEvent.getExtendedKeyCode(), (keyEvent.getModifiers() & 0x1) > 0);
        if ((menuShortcut.equals(this.shortcut) || menuShortcut2.equals(this.shortcut)) && this.isItemEnabled()) {
            if (keyEvent.getID() == 401) {
                this.doMenuEvent(keyEvent.getWhen(), keyEvent.getModifiers());
            }
            return true;
        }
        return false;
    }
    
    MenuItem getShortcutMenuItem(final MenuShortcut menuShortcut) {
        return menuShortcut.equals(this.shortcut) ? this : null;
    }
    
    protected final void enableEvents(final long n) {
        this.eventMask |= n;
        this.newEventsOnly = true;
    }
    
    protected final void disableEvents(final long n) {
        this.eventMask &= ~n;
    }
    
    public void setActionCommand(final String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public String getActionCommand() {
        return this.getActionCommandImpl();
    }
    
    final String getActionCommandImpl() {
        return (this.actionCommand == null) ? this.label : this.actionCommand;
    }
    
    public synchronized void addActionListener(final ActionListener actionListener) {
        if (actionListener == null) {
            return;
        }
        this.actionListener = AWTEventMulticaster.add(this.actionListener, actionListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeActionListener(final ActionListener actionListener) {
        if (actionListener == null) {
            return;
        }
        this.actionListener = AWTEventMulticaster.remove(this.actionListener, actionListener);
    }
    
    public synchronized ActionListener[] getActionListeners() {
        return this.getListeners(ActionListener.class);
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        EventListener actionListener = null;
        if (clazz == ActionListener.class) {
            actionListener = this.actionListener;
        }
        return AWTEventMulticaster.getListeners(actionListener, clazz);
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof ActionEvent) {
            this.processActionEvent((ActionEvent)awtEvent);
        }
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        if (awtEvent.id == 1001) {
            return (this.eventMask & 0x80L) != 0x0L || this.actionListener != null;
        }
        return super.eventEnabled(awtEvent);
    }
    
    protected void processActionEvent(final ActionEvent actionEvent) {
        final ActionListener actionListener = this.actionListener;
        if (actionListener != null) {
            actionListener.actionPerformed(actionEvent);
        }
    }
    
    public String paramString() {
        String s = ",label=" + this.label;
        if (this.shortcut != null) {
            s = s + ",shortcut=" + this.shortcut;
        }
        return super.paramString() + s;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "actionL", this.actionListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        objectInputStream.defaultReadObject();
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            if ("actionL" == ((String)object).intern()) {
                this.addActionListener((ActionListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
    }
    
    private static native void initIDs();
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTMenuItem();
        }
        return this.accessibleContext;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setMenuItemAccessor(new AWTAccessor.MenuItemAccessor() {
            @Override
            public boolean isEnabled(final MenuItem menuItem) {
                return menuItem.enabled;
            }
            
            @Override
            public String getLabel(final MenuItem menuItem) {
                return menuItem.label;
            }
            
            @Override
            public MenuShortcut getShortcut(final MenuItem menuItem) {
                return menuItem.shortcut;
            }
            
            @Override
            public String getActionCommandImpl(final MenuItem menuItem) {
                return menuItem.getActionCommandImpl();
            }
            
            @Override
            public boolean isItemEnabled(final MenuItem menuItem) {
                return menuItem.isItemEnabled();
            }
        });
        MenuItem.nameCounter = 0;
    }
    
    protected class AccessibleAWTMenuItem extends AccessibleAWTMenuComponent implements AccessibleAction, AccessibleValue
    {
        private static final long serialVersionUID = -217847831945965825L;
        
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (MenuItem.this.getLabel() == null) {
                return super.getAccessibleName();
            }
            return MenuItem.this.getLabel();
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_ITEM;
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public int getAccessibleActionCount() {
            return 1;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            if (n == 0) {
                return "click";
            }
            return null;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            if (n == 0) {
                Toolkit.getEventQueue().postEvent(new ActionEvent(MenuItem.this, 1001, MenuItem.this.getActionCommand(), EventQueue.getMostRecentEventTime(), 0));
                return true;
            }
            return false;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return 0;
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            return false;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return 0;
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return 0;
        }
    }
}
