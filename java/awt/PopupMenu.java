package java.awt;

import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleContext;
import java.awt.peer.PopupMenuPeer;

public class PopupMenu extends Menu
{
    private static final String base = "popup";
    static int nameCounter;
    transient boolean isTrayIconPopup;
    private static final long serialVersionUID = -4620452533522760060L;
    
    public PopupMenu() throws HeadlessException {
        this("");
    }
    
    public PopupMenu(final String s) throws HeadlessException {
        super(s);
        this.isTrayIconPopup = false;
    }
    
    @Override
    public MenuContainer getParent() {
        if (this.isTrayIconPopup) {
            return null;
        }
        return super.getParent();
    }
    
    @Override
    String constructComponentName() {
        synchronized (PopupMenu.class) {
            return "popup" + PopupMenu.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.parent != null && !(this.parent instanceof Component)) {
                super.addNotify();
            }
            else {
                if (this.peer == null) {
                    this.peer = Toolkit.getDefaultToolkit().createPopupMenu(this);
                }
                for (int itemCount = this.getItemCount(), i = 0; i < itemCount; ++i) {
                    final MenuItem item = this.getItem(i);
                    item.parent = this;
                    item.addNotify();
                }
            }
        }
    }
    
    public void show(final Component component, final int n, final int n2) {
        final MenuContainer parent = this.parent;
        if (parent == null) {
            throw new NullPointerException("parent is null");
        }
        if (!(parent instanceof Component)) {
            throw new IllegalArgumentException("PopupMenus with non-Component parents cannot be shown");
        }
        final Component component2 = (Component)parent;
        if (component2 != component) {
            if (!(component2 instanceof Container)) {
                throw new IllegalArgumentException("origin not in parent's hierarchy");
            }
            if (!((Container)component2).isAncestorOf(component)) {
                throw new IllegalArgumentException("origin not in parent's hierarchy");
            }
        }
        if (component2.getPeer() == null || !component2.isShowing()) {
            throw new RuntimeException("parent not showing on screen");
        }
        if (this.peer == null) {
            this.addNotify();
        }
        synchronized (this.getTreeLock()) {
            if (this.peer != null) {
                ((PopupMenuPeer)this.peer).show(new Event(component, 0L, 501, n, n2, 0, 0));
            }
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTPopupMenu();
        }
        return this.accessibleContext;
    }
    
    static {
        PopupMenu.nameCounter = 0;
        AWTAccessor.setPopupMenuAccessor(new AWTAccessor.PopupMenuAccessor() {
            @Override
            public boolean isTrayIconPopup(final PopupMenu popupMenu) {
                return popupMenu.isTrayIconPopup;
            }
        });
    }
    
    protected class AccessibleAWTPopupMenu extends AccessibleAWTMenu
    {
        private static final long serialVersionUID = -4282044795947239955L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.POPUP_MENU;
        }
    }
}
