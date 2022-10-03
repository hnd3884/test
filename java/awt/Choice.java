package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ItemEvent;
import java.util.EventListener;
import java.awt.peer.ChoicePeer;
import java.awt.event.ItemListener;
import java.util.Vector;
import javax.accessibility.Accessible;

public class Choice extends Component implements ItemSelectable, Accessible
{
    Vector<String> pItems;
    int selectedIndex;
    transient ItemListener itemListener;
    private static final String base = "choice";
    private static int nameCounter;
    private static final long serialVersionUID = -4075310674757313071L;
    private int choiceSerializedDataVersion;
    
    public Choice() throws HeadlessException {
        this.selectedIndex = -1;
        this.choiceSerializedDataVersion = 1;
        GraphicsEnvironment.checkHeadless();
        this.pItems = new Vector<String>();
    }
    
    @Override
    String constructComponentName() {
        synchronized (Choice.class) {
            return "choice" + Choice.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createChoice(this);
            }
            super.addNotify();
        }
    }
    
    public int getItemCount() {
        return this.countItems();
    }
    
    @Deprecated
    public int countItems() {
        return this.pItems.size();
    }
    
    public String getItem(final int n) {
        return this.getItemImpl(n);
    }
    
    final String getItemImpl(final int n) {
        return this.pItems.elementAt(n);
    }
    
    public void add(final String s) {
        this.addItem(s);
    }
    
    public void addItem(final String s) {
        synchronized (this) {
            this.insertNoInvalidate(s, this.pItems.size());
        }
        this.invalidateIfValid();
    }
    
    private void insertNoInvalidate(final String s, final int n) {
        if (s == null) {
            throw new NullPointerException("cannot add null item to Choice");
        }
        this.pItems.insertElementAt(s, n);
        final ChoicePeer choicePeer = (ChoicePeer)this.peer;
        if (choicePeer != null) {
            choicePeer.add(s, n);
        }
        if (this.selectedIndex < 0 || this.selectedIndex >= n) {
            this.select(0);
        }
    }
    
    public void insert(final String s, int min) {
        synchronized (this) {
            if (min < 0) {
                throw new IllegalArgumentException("index less than zero.");
            }
            min = Math.min(min, this.pItems.size());
            this.insertNoInvalidate(s, min);
        }
        this.invalidateIfValid();
    }
    
    public void remove(final String s) {
        synchronized (this) {
            final int index = this.pItems.indexOf(s);
            if (index < 0) {
                throw new IllegalArgumentException("item " + s + " not found in choice");
            }
            this.removeNoInvalidate(index);
        }
        this.invalidateIfValid();
    }
    
    public void remove(final int n) {
        synchronized (this) {
            this.removeNoInvalidate(n);
        }
        this.invalidateIfValid();
    }
    
    private void removeNoInvalidate(final int n) {
        this.pItems.removeElementAt(n);
        final ChoicePeer choicePeer = (ChoicePeer)this.peer;
        if (choicePeer != null) {
            choicePeer.remove(n);
        }
        if (this.pItems.size() == 0) {
            this.selectedIndex = -1;
        }
        else if (this.selectedIndex == n) {
            this.select(0);
        }
        else if (this.selectedIndex > n) {
            this.select(this.selectedIndex - 1);
        }
    }
    
    public void removeAll() {
        synchronized (this) {
            if (this.peer != null) {
                ((ChoicePeer)this.peer).removeAll();
            }
            this.pItems.removeAllElements();
            this.selectedIndex = -1;
        }
        this.invalidateIfValid();
    }
    
    public synchronized String getSelectedItem() {
        return (this.selectedIndex >= 0) ? this.getItem(this.selectedIndex) : null;
    }
    
    @Override
    public synchronized Object[] getSelectedObjects() {
        if (this.selectedIndex >= 0) {
            return new Object[] { this.getItem(this.selectedIndex) };
        }
        return null;
    }
    
    public int getSelectedIndex() {
        return this.selectedIndex;
    }
    
    public synchronized void select(final int selectedIndex) {
        if (selectedIndex >= this.pItems.size() || selectedIndex < 0) {
            throw new IllegalArgumentException("illegal Choice item position: " + selectedIndex);
        }
        if (this.pItems.size() > 0) {
            this.selectedIndex = selectedIndex;
            final ChoicePeer choicePeer = (ChoicePeer)this.peer;
            if (choicePeer != null) {
                choicePeer.select(selectedIndex);
            }
        }
    }
    
    public synchronized void select(final String s) {
        final int index = this.pItems.indexOf(s);
        if (index >= 0) {
            this.select(index);
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
        return super.paramString() + ",current=" + this.getSelectedItem();
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
            this.accessibleContext = new AccessibleAWTChoice();
        }
        return this.accessibleContext;
    }
    
    static {
        Choice.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
    
    protected class AccessibleAWTChoice extends AccessibleAWTComponent implements AccessibleAction
    {
        private static final long serialVersionUID = 7175603582428509322L;
        
        public AccessibleAWTChoice() {
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COMBO_BOX;
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
    }
}
