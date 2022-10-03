package java.awt;

import java.awt.event.FocusListener;
import java.util.Locale;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.EventListener;
import java.awt.peer.ListPeer;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.accessibility.Accessible;

public class List extends Component implements ItemSelectable, Accessible
{
    Vector<String> items;
    int rows;
    boolean multipleMode;
    int[] selected;
    int visibleIndex;
    transient ActionListener actionListener;
    transient ItemListener itemListener;
    private static final String base = "list";
    private static int nameCounter;
    private static final long serialVersionUID = -3304312411574666869L;
    static final int DEFAULT_VISIBLE_ROWS = 4;
    private int listSerializedDataVersion;
    
    public List() throws HeadlessException {
        this(0, false);
    }
    
    public List(final int n) throws HeadlessException {
        this(n, false);
    }
    
    public List(final int n, final boolean multipleMode) throws HeadlessException {
        this.items = new Vector<String>();
        this.rows = 0;
        this.multipleMode = false;
        this.selected = new int[0];
        this.visibleIndex = -1;
        this.listSerializedDataVersion = 1;
        GraphicsEnvironment.checkHeadless();
        this.rows = ((n != 0) ? n : 4);
        this.multipleMode = multipleMode;
    }
    
    @Override
    String constructComponentName() {
        synchronized (List.class) {
            return "list" + List.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createList(this);
            }
            super.addNotify();
        }
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            final ListPeer listPeer = (ListPeer)this.peer;
            if (listPeer != null) {
                this.selected = listPeer.getSelectedIndexes();
            }
            super.removeNotify();
        }
    }
    
    public int getItemCount() {
        return this.countItems();
    }
    
    @Deprecated
    public int countItems() {
        return this.items.size();
    }
    
    public String getItem(final int n) {
        return this.getItemImpl(n);
    }
    
    final String getItemImpl(final int n) {
        return this.items.elementAt(n);
    }
    
    public synchronized String[] getItems() {
        final String[] array = new String[this.items.size()];
        this.items.copyInto(array);
        return array;
    }
    
    public void add(final String s) {
        this.addItem(s);
    }
    
    @Deprecated
    public void addItem(final String s) {
        this.addItem(s, -1);
    }
    
    public void add(final String s, final int n) {
        this.addItem(s, n);
    }
    
    @Deprecated
    public synchronized void addItem(String s, int n) {
        if (n < -1 || n >= this.items.size()) {
            n = -1;
        }
        if (s == null) {
            s = "";
        }
        if (n == -1) {
            this.items.addElement(s);
        }
        else {
            this.items.insertElementAt(s, n);
        }
        final ListPeer listPeer = (ListPeer)this.peer;
        if (listPeer != null) {
            listPeer.add(s, n);
        }
    }
    
    public synchronized void replaceItem(final String s, final int n) {
        this.remove(n);
        this.add(s, n);
    }
    
    public void removeAll() {
        this.clear();
    }
    
    @Deprecated
    public synchronized void clear() {
        final ListPeer listPeer = (ListPeer)this.peer;
        if (listPeer != null) {
            listPeer.removeAll();
        }
        this.items = new Vector<String>();
        this.selected = new int[0];
    }
    
    public synchronized void remove(final String s) {
        final int index = this.items.indexOf(s);
        if (index < 0) {
            throw new IllegalArgumentException("item " + s + " not found in list");
        }
        this.remove(index);
    }
    
    public void remove(final int n) {
        this.delItem(n);
    }
    
    @Deprecated
    public void delItem(final int n) {
        this.delItems(n, n);
    }
    
    public synchronized int getSelectedIndex() {
        final int[] selectedIndexes = this.getSelectedIndexes();
        return (selectedIndexes.length == 1) ? selectedIndexes[0] : -1;
    }
    
    public synchronized int[] getSelectedIndexes() {
        final ListPeer listPeer = (ListPeer)this.peer;
        if (listPeer != null) {
            this.selected = listPeer.getSelectedIndexes();
        }
        return this.selected.clone();
    }
    
    public synchronized String getSelectedItem() {
        final int selectedIndex = this.getSelectedIndex();
        return (selectedIndex < 0) ? null : this.getItem(selectedIndex);
    }
    
    public synchronized String[] getSelectedItems() {
        final int[] selectedIndexes = this.getSelectedIndexes();
        final String[] array = new String[selectedIndexes.length];
        for (int i = 0; i < selectedIndexes.length; ++i) {
            array[i] = this.getItem(selectedIndexes[i]);
        }
        return array;
    }
    
    @Override
    public Object[] getSelectedObjects() {
        return this.getSelectedItems();
    }
    
    public void select(final int n) {
        ListPeer listPeer;
        do {
            listPeer = (ListPeer)this.peer;
            if (listPeer != null) {
                listPeer.select(n);
                return;
            }
            synchronized (this) {
                boolean b = false;
                for (int i = 0; i < this.selected.length; ++i) {
                    if (this.selected[i] == n) {
                        b = true;
                        break;
                    }
                }
                if (b) {
                    continue;
                }
                if (!this.multipleMode) {
                    (this.selected = new int[1])[0] = n;
                }
                else {
                    final int[] selected = new int[this.selected.length + 1];
                    System.arraycopy(this.selected, 0, selected, 0, this.selected.length);
                    selected[this.selected.length] = n;
                    this.selected = selected;
                }
            }
        } while (listPeer != this.peer);
    }
    
    public synchronized void deselect(final int n) {
        final ListPeer listPeer = (ListPeer)this.peer;
        if (listPeer != null && (this.isMultipleMode() || this.getSelectedIndex() == n)) {
            listPeer.deselect(n);
        }
        for (int i = 0; i < this.selected.length; ++i) {
            if (this.selected[i] == n) {
                final int[] selected = new int[this.selected.length - 1];
                System.arraycopy(this.selected, 0, selected, 0, i);
                System.arraycopy(this.selected, i + 1, selected, i, this.selected.length - (i + 1));
                this.selected = selected;
                return;
            }
        }
    }
    
    public boolean isIndexSelected(final int n) {
        return this.isSelected(n);
    }
    
    @Deprecated
    public boolean isSelected(final int n) {
        final int[] selectedIndexes = this.getSelectedIndexes();
        for (int i = 0; i < selectedIndexes.length; ++i) {
            if (selectedIndexes[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public boolean isMultipleMode() {
        return this.allowsMultipleSelections();
    }
    
    @Deprecated
    public boolean allowsMultipleSelections() {
        return this.multipleMode;
    }
    
    public void setMultipleMode(final boolean multipleSelections) {
        this.setMultipleSelections(multipleSelections);
    }
    
    @Deprecated
    public synchronized void setMultipleSelections(final boolean b) {
        if (b != this.multipleMode) {
            this.multipleMode = b;
            final ListPeer listPeer = (ListPeer)this.peer;
            if (listPeer != null) {
                listPeer.setMultipleMode(b);
            }
        }
    }
    
    public int getVisibleIndex() {
        return this.visibleIndex;
    }
    
    public synchronized void makeVisible(final int visibleIndex) {
        this.visibleIndex = visibleIndex;
        final ListPeer listPeer = (ListPeer)this.peer;
        if (listPeer != null) {
            listPeer.makeVisible(visibleIndex);
        }
    }
    
    public Dimension getPreferredSize(final int n) {
        return this.preferredSize(n);
    }
    
    @Deprecated
    public Dimension preferredSize(final int n) {
        synchronized (this.getTreeLock()) {
            final ListPeer listPeer = (ListPeer)this.peer;
            return (listPeer != null) ? listPeer.getPreferredSize(n) : super.preferredSize();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return this.preferredSize();
    }
    
    @Deprecated
    @Override
    public Dimension preferredSize() {
        synchronized (this.getTreeLock()) {
            return (this.rows > 0) ? this.preferredSize(this.rows) : super.preferredSize();
        }
    }
    
    public Dimension getMinimumSize(final int n) {
        return this.minimumSize(n);
    }
    
    @Deprecated
    public Dimension minimumSize(final int n) {
        synchronized (this.getTreeLock()) {
            final ListPeer listPeer = (ListPeer)this.peer;
            return (listPeer != null) ? listPeer.getMinimumSize(n) : super.minimumSize();
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.minimumSize();
    }
    
    @Deprecated
    @Override
    public Dimension minimumSize() {
        synchronized (this.getTreeLock()) {
            return (this.rows > 0) ? this.minimumSize(this.rows) : super.minimumSize();
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
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        Object o;
        if (clazz == ActionListener.class) {
            o = this.actionListener;
        }
        else {
            if (clazz != ItemListener.class) {
                return super.getListeners(clazz);
            }
            o = this.itemListener;
        }
        return AWTEventMulticaster.getListeners((EventListener)o, clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        switch (awtEvent.id) {
            case 1001: {
                return (this.eventMask & 0x80L) != 0x0L || this.actionListener != null;
            }
            case 701: {
                return (this.eventMask & 0x200L) != 0x0L || this.itemListener != null;
            }
            default: {
                return super.eventEnabled(awtEvent);
            }
        }
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof ItemEvent) {
            this.processItemEvent((ItemEvent)awtEvent);
            return;
        }
        if (awtEvent instanceof ActionEvent) {
            this.processActionEvent((ActionEvent)awtEvent);
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
    
    protected void processActionEvent(final ActionEvent actionEvent) {
        final ActionListener actionListener = this.actionListener;
        if (actionListener != null) {
            actionListener.actionPerformed(actionEvent);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",selected=" + this.getSelectedItem();
    }
    
    @Deprecated
    public synchronized void delItems(final int n, final int n2) {
        for (int i = n2; i >= n; --i) {
            this.items.removeElementAt(i);
        }
        final ListPeer listPeer = (ListPeer)this.peer;
        if (listPeer != null) {
            listPeer.delItems(n, n2);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        synchronized (this) {
            final ListPeer listPeer = (ListPeer)this.peer;
            if (listPeer != null) {
                this.selected = listPeer.getSelectedIndexes();
            }
        }
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "itemL", this.itemListener);
        AWTEventMulticaster.save(objectOutputStream, "actionL", this.actionListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        objectInputStream.defaultReadObject();
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            final String intern = ((String)object).intern();
            if ("itemL" == intern) {
                this.addItemListener((ItemListener)objectInputStream.readObject());
            }
            else if ("actionL" == intern) {
                this.addActionListener((ActionListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTList();
        }
        return this.accessibleContext;
    }
    
    static {
        List.nameCounter = 0;
    }
    
    protected class AccessibleAWTList extends AccessibleAWTComponent implements AccessibleSelection, ItemListener, ActionListener
    {
        private static final long serialVersionUID = 7924617370136012829L;
        final /* synthetic */ List this$0;
        
        public AccessibleAWTList() {
            List.this.addActionListener(this);
            List.this.addItemListener(this);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (List.this.isMultipleMode()) {
                accessibleStateSet.add(AccessibleState.MULTISELECTABLE);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LIST;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            return null;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return List.this.getItemCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            synchronized (List.this) {
                if (n >= List.this.getItemCount()) {
                    return null;
                }
                return new AccessibleAWTListChild(List.this, n);
            }
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            return List.this.getSelectedIndexes().length;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            synchronized (List.this) {
                final int accessibleSelectionCount = this.getAccessibleSelectionCount();
                if (n < 0 || n >= accessibleSelectionCount) {
                    return null;
                }
                return this.getAccessibleChild(List.this.getSelectedIndexes()[n]);
            }
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return List.this.isIndexSelected(n);
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
            List.this.select(n);
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            List.this.deselect(n);
        }
        
        @Override
        public void clearAccessibleSelection() {
            synchronized (List.this) {
                final int[] selectedIndexes = List.this.getSelectedIndexes();
                if (selectedIndexes == null) {
                    return;
                }
                for (int i = selectedIndexes.length - 1; i >= 0; --i) {
                    List.this.deselect(selectedIndexes[i]);
                }
            }
        }
        
        @Override
        public void selectAllAccessibleSelection() {
            synchronized (List.this) {
                for (int i = List.this.getItemCount() - 1; i >= 0; --i) {
                    List.this.select(i);
                }
            }
        }
        
        protected class AccessibleAWTListChild extends AccessibleAWTComponent implements Accessible
        {
            private static final long serialVersionUID = 4412022926028300317L;
            private List parent;
            private int indexInParent;
            
            public AccessibleAWTListChild(final List parent, final int indexInParent) {
                AccessibleAWTList.this.this$0.super();
                this.setAccessibleParent(this.parent = parent);
                this.indexInParent = indexInParent;
            }
            
            @Override
            public AccessibleContext getAccessibleContext() {
                return this;
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.LIST_ITEM;
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
                if (this.parent.isIndexSelected(this.indexInParent)) {
                    accessibleStateSet.add(AccessibleState.SELECTED);
                }
                return accessibleStateSet;
            }
            
            @Override
            public Locale getLocale() {
                return this.parent.getLocale();
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                return this.indexInParent;
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
            public Color getBackground() {
                return this.parent.getBackground();
            }
            
            @Override
            public void setBackground(final Color background) {
                this.parent.setBackground(background);
            }
            
            @Override
            public Color getForeground() {
                return this.parent.getForeground();
            }
            
            @Override
            public void setForeground(final Color foreground) {
                this.parent.setForeground(foreground);
            }
            
            @Override
            public Cursor getCursor() {
                return this.parent.getCursor();
            }
            
            @Override
            public void setCursor(final Cursor cursor) {
                this.parent.setCursor(cursor);
            }
            
            @Override
            public Font getFont() {
                return this.parent.getFont();
            }
            
            @Override
            public void setFont(final Font font) {
                this.parent.setFont(font);
            }
            
            @Override
            public FontMetrics getFontMetrics(final Font font) {
                return this.parent.getFontMetrics(font);
            }
            
            @Override
            public boolean isEnabled() {
                return this.parent.isEnabled();
            }
            
            @Override
            public void setEnabled(final boolean enabled) {
                this.parent.setEnabled(enabled);
            }
            
            @Override
            public boolean isVisible() {
                return false;
            }
            
            @Override
            public void setVisible(final boolean visible) {
                this.parent.setVisible(visible);
            }
            
            @Override
            public boolean isShowing() {
                return false;
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
                return false;
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
        }
    }
}
