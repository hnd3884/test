package java.awt;

import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ItemEvent;
import java.util.EventListener;
import java.awt.peer.CheckboxPeer;
import java.awt.event.ItemListener;
import javax.accessibility.Accessible;

public class Checkbox extends Component implements ItemSelectable, Accessible
{
    String label;
    boolean state;
    CheckboxGroup group;
    transient ItemListener itemListener;
    private static final String base = "checkbox";
    private static int nameCounter;
    private static final long serialVersionUID = 7270714317450821763L;
    private int checkboxSerializedDataVersion;
    
    void setStateInternal(final boolean b) {
        this.state = b;
        final CheckboxPeer checkboxPeer = (CheckboxPeer)this.peer;
        if (checkboxPeer != null) {
            checkboxPeer.setState(b);
        }
    }
    
    public Checkbox() throws HeadlessException {
        this("", false, null);
    }
    
    public Checkbox(final String s) throws HeadlessException {
        this(s, false, null);
    }
    
    public Checkbox(final String s, final boolean b) throws HeadlessException {
        this(s, b, null);
    }
    
    public Checkbox(final String label, final boolean state, final CheckboxGroup group) throws HeadlessException {
        this.checkboxSerializedDataVersion = 1;
        GraphicsEnvironment.checkHeadless();
        this.label = label;
        this.state = state;
        this.group = group;
        if (state && group != null) {
            group.setSelectedCheckbox(this);
        }
    }
    
    public Checkbox(final String s, final CheckboxGroup checkboxGroup, final boolean b) throws HeadlessException {
        this(s, b, checkboxGroup);
    }
    
    @Override
    String constructComponentName() {
        synchronized (Checkbox.class) {
            return "checkbox" + Checkbox.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createCheckbox(this);
            }
            super.addNotify();
        }
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String s) {
        boolean b = false;
        synchronized (this) {
            if (s != this.label && (this.label == null || !this.label.equals(s))) {
                this.label = s;
                final CheckboxPeer checkboxPeer = (CheckboxPeer)this.peer;
                if (checkboxPeer != null) {
                    checkboxPeer.setLabel(s);
                }
                b = true;
            }
        }
        if (b) {
            this.invalidateIfValid();
        }
    }
    
    public boolean getState() {
        return this.state;
    }
    
    public void setState(boolean stateInternal) {
        final CheckboxGroup group = this.group;
        if (group != null) {
            if (stateInternal) {
                group.setSelectedCheckbox(this);
            }
            else if (group.getSelectedCheckbox() == this) {
                stateInternal = true;
            }
        }
        this.setStateInternal(stateInternal);
    }
    
    @Override
    public Object[] getSelectedObjects() {
        if (this.state) {
            return new Object[] { this.label };
        }
        return null;
    }
    
    public CheckboxGroup getCheckboxGroup() {
        return this.group;
    }
    
    public void setCheckboxGroup(final CheckboxGroup checkboxGroup) {
        if (this.group == checkboxGroup) {
            return;
        }
        final CheckboxGroup group;
        final boolean state;
        synchronized (this) {
            group = this.group;
            state = this.getState();
            this.group = checkboxGroup;
            final CheckboxPeer checkboxPeer = (CheckboxPeer)this.peer;
            if (checkboxPeer != null) {
                checkboxPeer.setCheckboxGroup(checkboxGroup);
            }
            if (this.group != null && this.getState()) {
                if (this.group.getSelectedCheckbox() != null) {
                    this.setState(false);
                }
                else {
                    this.group.setSelectedCheckbox(this);
                }
            }
        }
        if (group != null && state) {
            group.setSelectedCheckbox(null);
        }
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
    protected String paramString() {
        String s = super.paramString();
        final String label = this.label;
        if (label != null) {
            s = s + ",label=" + label;
        }
        return s + ",state=" + this.state;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "itemL", this.itemListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
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
            this.accessibleContext = new AccessibleAWTCheckbox();
        }
        return this.accessibleContext;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        Checkbox.nameCounter = 0;
    }
    
    protected class AccessibleAWTCheckbox extends AccessibleAWTComponent implements ItemListener, AccessibleAction, AccessibleValue
    {
        private static final long serialVersionUID = 7881579233144754107L;
        
        public AccessibleAWTCheckbox() {
            Checkbox.this.addItemListener(this);
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            final Checkbox checkbox = (Checkbox)itemEvent.getSource();
            if (Checkbox.this.accessibleContext != null) {
                if (checkbox.getState()) {
                    Checkbox.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
                }
                else {
                    Checkbox.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
                }
            }
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
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (Checkbox.this.getState()) {
                accessibleStateSet.add(AccessibleState.CHECKED);
            }
            return accessibleStateSet;
        }
    }
}
