package java.awt;

import java.awt.event.FocusListener;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleComponent;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleStateSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.event.ActionEvent;
import java.security.AccessController;
import javax.accessibility.AccessibleContext;
import java.security.AccessControlContext;
import sun.awt.AppContext;
import java.awt.peer.MenuComponentPeer;
import java.io.Serializable;

public abstract class MenuComponent implements Serializable
{
    transient MenuComponentPeer peer;
    transient MenuContainer parent;
    transient AppContext appContext;
    volatile Font font;
    private String name;
    private boolean nameExplicitlySet;
    boolean newEventsOnly;
    private transient volatile AccessControlContext acc;
    static final String actionListenerK = "actionL";
    static final String itemListenerK = "itemL";
    private static final long serialVersionUID = -4536902356223894379L;
    AccessibleContext accessibleContext;
    
    final AccessControlContext getAccessControlContext() {
        if (this.acc == null) {
            throw new SecurityException("MenuComponent is missing AccessControlContext");
        }
        return this.acc;
    }
    
    public MenuComponent() throws HeadlessException {
        this.nameExplicitlySet = false;
        this.newEventsOnly = false;
        this.acc = AccessController.getContext();
        this.accessibleContext = null;
        GraphicsEnvironment.checkHeadless();
        this.appContext = AppContext.getAppContext();
    }
    
    String constructComponentName() {
        return null;
    }
    
    public String getName() {
        if (this.name == null && !this.nameExplicitlySet) {
            synchronized (this) {
                if (this.name == null && !this.nameExplicitlySet) {
                    this.name = this.constructComponentName();
                }
            }
        }
        return this.name;
    }
    
    public void setName(final String name) {
        synchronized (this) {
            this.name = name;
            this.nameExplicitlySet = true;
        }
    }
    
    public MenuContainer getParent() {
        return this.getParent_NoClientCode();
    }
    
    final MenuContainer getParent_NoClientCode() {
        return this.parent;
    }
    
    @Deprecated
    public MenuComponentPeer getPeer() {
        return this.peer;
    }
    
    public Font getFont() {
        final Font font = this.font;
        if (font != null) {
            return font;
        }
        final MenuContainer parent = this.parent;
        if (parent != null) {
            return parent.getFont();
        }
        return null;
    }
    
    final Font getFont_NoClientCode() {
        Font font = this.font;
        if (font != null) {
            return font;
        }
        final MenuContainer parent = this.parent;
        if (parent != null) {
            if (parent instanceof Component) {
                font = ((Component)parent).getFont_NoClientCode();
            }
            else if (parent instanceof MenuComponent) {
                font = ((MenuComponent)parent).getFont_NoClientCode();
            }
        }
        return font;
    }
    
    public void setFont(final Font font) {
        synchronized (this.getTreeLock()) {
            this.font = font;
            final MenuComponentPeer peer = this.peer;
            if (peer != null) {
                peer.setFont(font);
            }
        }
    }
    
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            final MenuComponentPeer peer = this.peer;
            if (peer != null) {
                Toolkit.getEventQueue().removeSourceEvents(this, true);
                this.peer = null;
                peer.dispose();
            }
        }
    }
    
    @Deprecated
    public boolean postEvent(final Event event) {
        final MenuContainer parent = this.parent;
        if (parent != null) {
            parent.postEvent(event);
        }
        return false;
    }
    
    public final void dispatchEvent(final AWTEvent awtEvent) {
        this.dispatchEventImpl(awtEvent);
    }
    
    void dispatchEventImpl(final AWTEvent currentEventAndMostRecentTime) {
        EventQueue.setCurrentEventAndMostRecentTime(currentEventAndMostRecentTime);
        Toolkit.getDefaultToolkit().notifyAWTEventListeners(currentEventAndMostRecentTime);
        if (this.newEventsOnly || (this.parent != null && this.parent instanceof MenuComponent && ((MenuComponent)this.parent).newEventsOnly)) {
            if (this.eventEnabled(currentEventAndMostRecentTime)) {
                this.processEvent(currentEventAndMostRecentTime);
            }
            else if (currentEventAndMostRecentTime instanceof ActionEvent && this.parent != null) {
                currentEventAndMostRecentTime.setSource(this.parent);
                ((MenuComponent)this.parent).dispatchEvent(currentEventAndMostRecentTime);
            }
        }
        else {
            final Event convertToOld = currentEventAndMostRecentTime.convertToOld();
            if (convertToOld != null) {
                this.postEvent(convertToOld);
            }
        }
    }
    
    boolean eventEnabled(final AWTEvent awtEvent) {
        return false;
    }
    
    protected void processEvent(final AWTEvent awtEvent) {
    }
    
    protected String paramString() {
        final String name = this.getName();
        return (name != null) ? name : "";
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.paramString() + "]";
    }
    
    protected final Object getTreeLock() {
        return Component.LOCK;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        this.acc = AccessController.getContext();
        objectInputStream.defaultReadObject();
        this.appContext = AppContext.getAppContext();
    }
    
    private static native void initIDs();
    
    public AccessibleContext getAccessibleContext() {
        return this.accessibleContext;
    }
    
    int getAccessibleIndexInParent() {
        final MenuContainer parent = this.parent;
        if (!(parent instanceof MenuComponent)) {
            return -1;
        }
        return ((MenuComponent)parent).getAccessibleChildIndex(this);
    }
    
    int getAccessibleChildIndex(final MenuComponent menuComponent) {
        return -1;
    }
    
    AccessibleStateSet getAccessibleStateSet() {
        return new AccessibleStateSet();
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setMenuComponentAccessor(new AWTAccessor.MenuComponentAccessor() {
            @Override
            public AppContext getAppContext(final MenuComponent menuComponent) {
                return menuComponent.appContext;
            }
            
            @Override
            public void setAppContext(final MenuComponent menuComponent, final AppContext appContext) {
                menuComponent.appContext = appContext;
            }
            
            @Override
            public MenuContainer getParent(final MenuComponent menuComponent) {
                return menuComponent.parent;
            }
            
            @Override
            public Font getFont_NoClientCode(final MenuComponent menuComponent) {
                return menuComponent.getFont_NoClientCode();
            }
            
            @Override
            public <T extends MenuComponentPeer> T getPeer(final MenuComponent menuComponent) {
                return (T)menuComponent.peer;
            }
        });
    }
    
    protected abstract class AccessibleAWTMenuComponent extends AccessibleContext implements Serializable, AccessibleComponent, AccessibleSelection
    {
        private static final long serialVersionUID = -4269533416223798698L;
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public String getAccessibleName() {
            return this.accessibleName;
        }
        
        @Override
        public String getAccessibleDescription() {
            return this.accessibleDescription;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.AWT_COMPONENT;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return MenuComponent.this.getAccessibleStateSet();
        }
        
        @Override
        public Accessible getAccessibleParent() {
            if (this.accessibleParent != null) {
                return this.accessibleParent;
            }
            final MenuContainer parent = MenuComponent.this.getParent();
            if (parent instanceof Accessible) {
                return (Accessible)parent;
            }
            return null;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            return MenuComponent.this.getAccessibleIndexInParent();
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            return null;
        }
        
        @Override
        public Locale getLocale() {
            final MenuContainer parent = MenuComponent.this.getParent();
            if (parent instanceof Component) {
                return ((Component)parent).getLocale();
            }
            return Locale.getDefault();
        }
        
        @Override
        public AccessibleComponent getAccessibleComponent() {
            return this;
        }
        
        @Override
        public Color getBackground() {
            return null;
        }
        
        @Override
        public void setBackground(final Color color) {
        }
        
        @Override
        public Color getForeground() {
            return null;
        }
        
        @Override
        public void setForeground(final Color color) {
        }
        
        @Override
        public Cursor getCursor() {
            return null;
        }
        
        @Override
        public void setCursor(final Cursor cursor) {
        }
        
        @Override
        public Font getFont() {
            return MenuComponent.this.getFont();
        }
        
        @Override
        public void setFont(final Font font) {
            MenuComponent.this.setFont(font);
        }
        
        @Override
        public FontMetrics getFontMetrics(final Font font) {
            return null;
        }
        
        @Override
        public boolean isEnabled() {
            return true;
        }
        
        @Override
        public void setEnabled(final boolean b) {
        }
        
        @Override
        public boolean isVisible() {
            return true;
        }
        
        @Override
        public void setVisible(final boolean b) {
        }
        
        @Override
        public boolean isShowing() {
            return true;
        }
        
        @Override
        public boolean contains(final Point point) {
            return false;
        }
        
        @Override
        public Point getLocationOnScreen() {
            return null;
        }
        
        @Override
        public Point getLocation() {
            return null;
        }
        
        @Override
        public void setLocation(final Point point) {
        }
        
        @Override
        public Rectangle getBounds() {
            return null;
        }
        
        @Override
        public void setBounds(final Rectangle rectangle) {
        }
        
        @Override
        public Dimension getSize() {
            return null;
        }
        
        @Override
        public void setSize(final Dimension dimension) {
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            return null;
        }
        
        @Override
        public boolean isFocusTraversable() {
            return true;
        }
        
        @Override
        public void requestFocus() {
        }
        
        @Override
        public void addFocusListener(final FocusListener focusListener) {
        }
        
        @Override
        public void removeFocusListener(final FocusListener focusListener) {
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            return 0;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return false;
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
        }
        
        @Override
        public void clearAccessibleSelection() {
        }
        
        @Override
        public void selectAllAccessibleSelection() {
        }
    }
}
