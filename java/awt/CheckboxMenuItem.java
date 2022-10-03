package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleAction;
import sun.awt.AWTAccessor;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ItemEvent;
import java.util.EventListener;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.event.ItemListener;
import javax.accessibility.Accessible;

public class CheckboxMenuItem extends MenuItem implements ItemSelectable, Accessible
{
    boolean state;
    transient ItemListener itemListener;
    private static final String base = "chkmenuitem";
    private static int nameCounter;
    private static final long serialVersionUID = 6190621106981774043L;
    private int checkboxMenuItemSerializedDataVersion;
    
    public CheckboxMenuItem() throws HeadlessException {
        this("", false);
    }
    
    public CheckboxMenuItem(final String s) throws HeadlessException {
        this(s, false);
    }
    
    public CheckboxMenuItem(final String s, final boolean state) throws HeadlessException {
        super(s);
        this.state = false;
        this.checkboxMenuItemSerializedDataVersion = 1;
        this.state = state;
    }
    
    @Override
    String constructComponentName() {
        synchronized (CheckboxMenuItem.class) {
            return "chkmenuitem" + CheckboxMenuItem.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
            }
            super.addNotify();
        }
    }
    
    public boolean getState() {
        return this.state;
    }
    
    public synchronized void setState(final boolean b) {
        this.state = b;
        final CheckboxMenuItemPeer checkboxMenuItemPeer = (CheckboxMenuItemPeer)this.peer;
        if (checkboxMenuItemPeer != null) {
            checkboxMenuItemPeer.setState(b);
        }
    }
    
    @Override
    public synchronized Object[] getSelectedObjects() {
        if (this.state) {
            return new Object[] { this.label };
        }
        return null;
    }
    
    @Override
    public synchronized void addItemListener(final ItemListener itemListener) {
        if (itemListener == null) {
            return;
        }
        this.itemListener = AWTEventMulticaster.add(this.itemListener, itemListener);
        this.newEventsOnly = true;
    }
    
    @Override
    public synchronized void removeItemListener(final ItemListener itemListener) {
        if (itemListener == null) {
            return;
        }
        this.itemListener = AWTEventMulticaster.remove(this.itemListener, itemListener);
    }
    
    public synchronized ItemListener[] getItemListeners() {
        return this.getListeners(ItemListener.class);
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        if (clazz == ItemListener.class) {
            return AWTEventMulticaster.getListeners(this.itemListener, clazz);
        }
        return super.getListeners(clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        if (awtEvent.id == 701) {
            return (this.eventMask & 0x200L) != 0x0L || this.itemListener != null;
        }
        return super.eventEnabled(awtEvent);
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof ItemEvent) {
            this.processItemEvent((ItemEvent)awtEvent);
            return;
        }
        super.processEvent(awtEvent);
    }
    
    protected void processItemEvent(final ItemEvent itemEvent) {
        final ItemListener itemListener = this.itemListener;
        if (itemListener != null) {
            itemListener.itemStateChanged(itemEvent);
        }
    }
    
    @Override
    void doMenuEvent(final long n, final int n2) {
        this.setState(!this.state);
        Toolkit.getEventQueue().postEvent(new ItemEvent(this, 701, this.getLabel(), this.state ? 1 : 2));
    }
    
    @Override
    public String paramString() {
        return super.paramString() + ",state=" + this.state;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "itemL", this.itemListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            if ("itemL" == ((String)object).intern()) {
                this.addItemListener((ItemListener)objectInputStream.readObject());
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
            this.accessibleContext = new AccessibleAWTCheckboxMenuItem();
        }
        return this.accessibleContext;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setCheckboxMenuItemAccessor(new AWTAccessor.CheckboxMenuItemAccessor() {
            @Override
            public boolean getState(final CheckboxMenuItem checkboxMenuItem) {
                return checkboxMenuItem.state;
            }
        });
        CheckboxMenuItem.nameCounter = 0;
    }
    
    protected class AccessibleAWTCheckboxMenuItem extends AccessibleAWTMenuItem implements AccessibleAction, AccessibleValue
    {
        private static final long serialVersionUID = -1122642964303476L;
        
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
            return 0;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            return null;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            return false;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return null;
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            return false;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return null;
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return null;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }
    }
}
