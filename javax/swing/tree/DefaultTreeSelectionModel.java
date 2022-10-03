package javax.swing.tree;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.beans.PropertyChangeListener;
import java.util.EventListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import java.io.Serializable;

public class DefaultTreeSelectionModel implements Cloneable, Serializable, TreeSelectionModel
{
    public static final String SELECTION_MODE_PROPERTY = "selectionMode";
    protected SwingPropertyChangeSupport changeSupport;
    protected TreePath[] selection;
    protected EventListenerList listenerList;
    protected transient RowMapper rowMapper;
    protected DefaultListSelectionModel listSelectionModel;
    protected int selectionMode;
    protected TreePath leadPath;
    protected int leadIndex;
    protected int leadRow;
    private Hashtable<TreePath, Boolean> uniquePaths;
    private Hashtable<TreePath, Boolean> lastPaths;
    private TreePath[] tempPaths;
    
    public DefaultTreeSelectionModel() {
        this.listenerList = new EventListenerList();
        this.listSelectionModel = new DefaultListSelectionModel();
        this.selectionMode = 4;
        final int n = -1;
        this.leadRow = n;
        this.leadIndex = n;
        this.uniquePaths = new Hashtable<TreePath, Boolean>();
        this.lastPaths = new Hashtable<TreePath, Boolean>();
        this.tempPaths = new TreePath[1];
    }
    
    @Override
    public void setRowMapper(final RowMapper rowMapper) {
        this.rowMapper = rowMapper;
        this.resetRowSelection();
    }
    
    @Override
    public RowMapper getRowMapper() {
        return this.rowMapper;
    }
    
    @Override
    public void setSelectionMode(final int selectionMode) {
        final int selectionMode2 = this.selectionMode;
        this.selectionMode = selectionMode;
        if (this.selectionMode != 1 && this.selectionMode != 2 && this.selectionMode != 4) {
            this.selectionMode = 4;
        }
        if (selectionMode2 != this.selectionMode && this.changeSupport != null) {
            this.changeSupport.firePropertyChange("selectionMode", selectionMode2, (Object)this.selectionMode);
        }
    }
    
    @Override
    public int getSelectionMode() {
        return this.selectionMode;
    }
    
    @Override
    public void setSelectionPath(final TreePath treePath) {
        if (treePath == null) {
            this.setSelectionPaths(null);
        }
        else {
            this.setSelectionPaths(new TreePath[] { treePath });
        }
    }
    
    @Override
    public void setSelectionPaths(final TreePath[] array) {
        TreePath[] array2 = array;
        int length;
        if (array2 == null) {
            length = 0;
        }
        else {
            length = array2.length;
        }
        int length2;
        if (this.selection == null) {
            length2 = 0;
        }
        else {
            length2 = this.selection.length;
        }
        if (length + length2 != 0) {
            if (this.selectionMode == 1) {
                if (length > 1) {
                    array2 = new TreePath[] { array[0] };
                    length = 1;
                }
            }
            else if (this.selectionMode == 2 && length > 0 && !this.arePathsContiguous(array2)) {
                array2 = new TreePath[] { array[0] };
                length = 1;
            }
            final TreePath leadPath = this.leadPath;
            final Vector vector = new Vector<PathPlaceHolder>(length + length2);
            final ArrayList list = new ArrayList(length);
            this.lastPaths.clear();
            this.leadPath = null;
            for (final TreePath leadPath2 : array2) {
                if (leadPath2 != null && this.lastPaths.get(leadPath2) == null) {
                    this.lastPaths.put(leadPath2, Boolean.TRUE);
                    if (this.uniquePaths.get(leadPath2) == null) {
                        vector.addElement(new PathPlaceHolder(leadPath2, true));
                    }
                    list.add((Object)(this.leadPath = leadPath2));
                }
            }
            final TreePath[] selection = (TreePath[])list.toArray((Object[])new TreePath[list.size()]);
            for (int j = 0; j < length2; ++j) {
                if (this.selection[j] != null && this.lastPaths.get(this.selection[j]) == null) {
                    vector.addElement(new PathPlaceHolder(this.selection[j], false));
                }
            }
            this.selection = selection;
            final Hashtable<TreePath, Boolean> uniquePaths = this.uniquePaths;
            this.uniquePaths = this.lastPaths;
            (this.lastPaths = uniquePaths).clear();
            this.insureUniqueness();
            this.updateLeadIndex();
            this.resetRowSelection();
            if (vector.size() > 0) {
                this.notifyPathChange(vector, leadPath);
            }
        }
    }
    
    @Override
    public void addSelectionPath(final TreePath treePath) {
        if (treePath != null) {
            this.addSelectionPaths(new TreePath[] { treePath });
        }
    }
    
    @Override
    public void addSelectionPaths(final TreePath[] array) {
        final int n = (array == null) ? 0 : array.length;
        if (n > 0) {
            if (this.selectionMode == 1) {
                this.setSelectionPaths(array);
            }
            else if (this.selectionMode == 2 && !this.canPathsBeAdded(array)) {
                if (this.arePathsContiguous(array)) {
                    this.setSelectionPaths(array);
                }
                else {
                    this.setSelectionPaths(new TreePath[] { array[0] });
                }
            }
            else {
                final TreePath leadPath = this.leadPath;
                Vector<PathPlaceHolder> vector = null;
                int length;
                if (this.selection == null) {
                    length = 0;
                }
                else {
                    length = this.selection.length;
                }
                this.lastPaths.clear();
                int i = 0;
                int n2 = 0;
                while (i < n) {
                    if (array[i] != null) {
                        if (this.uniquePaths.get(array[i]) == null) {
                            ++n2;
                            if (vector == null) {
                                vector = new Vector<PathPlaceHolder>();
                            }
                            vector.addElement(new PathPlaceHolder(array[i], true));
                            this.uniquePaths.put(array[i], Boolean.TRUE);
                            this.lastPaths.put(array[i], Boolean.TRUE);
                        }
                        this.leadPath = array[i];
                    }
                    ++i;
                }
                if (this.leadPath == null) {
                    this.leadPath = leadPath;
                }
                if (n2 > 0) {
                    final TreePath[] selection = new TreePath[length + n2];
                    if (length > 0) {
                        System.arraycopy(this.selection, 0, selection, 0, length);
                    }
                    if (n2 != array.length) {
                        final Enumeration<TreePath> keys = this.lastPaths.keys();
                        int n3 = length;
                        while (keys.hasMoreElements()) {
                            selection[n3++] = keys.nextElement();
                        }
                    }
                    else {
                        System.arraycopy(array, 0, selection, length, n2);
                    }
                    this.selection = selection;
                    this.insureUniqueness();
                    this.updateLeadIndex();
                    this.resetRowSelection();
                    this.notifyPathChange(vector, leadPath);
                }
                else {
                    this.leadPath = leadPath;
                }
                this.lastPaths.clear();
            }
        }
    }
    
    @Override
    public void removeSelectionPath(final TreePath treePath) {
        if (treePath != null) {
            this.removeSelectionPaths(new TreePath[] { treePath });
        }
    }
    
    @Override
    public void removeSelectionPaths(final TreePath[] array) {
        if (array != null && this.selection != null && array.length > 0) {
            if (!this.canPathsBeRemoved(array)) {
                this.clearSelection();
            }
            else {
                Vector<PathPlaceHolder> vector = null;
                for (int i = array.length - 1; i >= 0; --i) {
                    if (array[i] != null && this.uniquePaths.get(array[i]) != null) {
                        if (vector == null) {
                            vector = new Vector<PathPlaceHolder>(array.length);
                        }
                        this.uniquePaths.remove(array[i]);
                        vector.addElement(new PathPlaceHolder(array[i], false));
                    }
                }
                if (vector != null) {
                    final int size = vector.size();
                    final TreePath leadPath = this.leadPath;
                    if (size == this.selection.length) {
                        this.selection = null;
                    }
                    else {
                        final Enumeration<TreePath> keys = this.uniquePaths.keys();
                        int n = 0;
                        this.selection = new TreePath[this.selection.length - size];
                        while (keys.hasMoreElements()) {
                            this.selection[n++] = keys.nextElement();
                        }
                    }
                    if (this.leadPath != null && this.uniquePaths.get(this.leadPath) == null) {
                        if (this.selection != null) {
                            this.leadPath = this.selection[this.selection.length - 1];
                        }
                        else {
                            this.leadPath = null;
                        }
                    }
                    else if (this.selection != null) {
                        this.leadPath = this.selection[this.selection.length - 1];
                    }
                    else {
                        this.leadPath = null;
                    }
                    this.updateLeadIndex();
                    this.resetRowSelection();
                    this.notifyPathChange(vector, leadPath);
                }
            }
        }
    }
    
    @Override
    public TreePath getSelectionPath() {
        if (this.selection != null && this.selection.length > 0) {
            return this.selection[0];
        }
        return null;
    }
    
    @Override
    public TreePath[] getSelectionPaths() {
        if (this.selection != null) {
            final int length = this.selection.length;
            final TreePath[] array = new TreePath[length];
            System.arraycopy(this.selection, 0, array, 0, length);
            return array;
        }
        return new TreePath[0];
    }
    
    @Override
    public int getSelectionCount() {
        return (this.selection == null) ? 0 : this.selection.length;
    }
    
    @Override
    public boolean isPathSelected(final TreePath treePath) {
        return treePath != null && this.uniquePaths.get(treePath) != null;
    }
    
    @Override
    public boolean isSelectionEmpty() {
        return this.selection == null || this.selection.length == 0;
    }
    
    @Override
    public void clearSelection() {
        if (this.selection != null && this.selection.length > 0) {
            final int length = this.selection.length;
            final boolean[] array = new boolean[length];
            for (int i = 0; i < length; ++i) {
                array[i] = false;
            }
            final TreeSelectionEvent treeSelectionEvent = new TreeSelectionEvent(this, this.selection, array, this.leadPath, null);
            this.leadPath = null;
            final int n = -1;
            this.leadRow = n;
            this.leadIndex = n;
            this.uniquePaths.clear();
            this.selection = null;
            this.resetRowSelection();
            this.fireValueChanged(treeSelectionEvent);
        }
    }
    
    @Override
    public void addTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        this.listenerList.add(TreeSelectionListener.class, treeSelectionListener);
    }
    
    @Override
    public void removeTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        this.listenerList.remove(TreeSelectionListener.class, treeSelectionListener);
    }
    
    public TreeSelectionListener[] getTreeSelectionListeners() {
        return this.listenerList.getListeners(TreeSelectionListener.class);
    }
    
    protected void fireValueChanged(final TreeSelectionEvent treeSelectionEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TreeSelectionListener.class) {
                ((TreeSelectionListener)listenerList[i + 1]).valueChanged(treeSelectionEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    @Override
    public int[] getSelectionRows() {
        if (this.rowMapper != null && this.selection != null && this.selection.length > 0) {
            int[] rowsForPaths = this.rowMapper.getRowsForPaths(this.selection);
            if (rowsForPaths != null) {
                int n = 0;
                for (int i = rowsForPaths.length - 1; i >= 0; --i) {
                    if (rowsForPaths[i] == -1) {
                        ++n;
                    }
                }
                if (n > 0) {
                    if (n == rowsForPaths.length) {
                        rowsForPaths = null;
                    }
                    else {
                        final int[] array = new int[rowsForPaths.length - n];
                        int j = rowsForPaths.length - 1;
                        int n2 = 0;
                        while (j >= 0) {
                            if (rowsForPaths[j] != -1) {
                                array[n2++] = rowsForPaths[j];
                            }
                            --j;
                        }
                        rowsForPaths = array;
                    }
                }
            }
            return rowsForPaths;
        }
        return new int[0];
    }
    
    @Override
    public int getMinSelectionRow() {
        return this.listSelectionModel.getMinSelectionIndex();
    }
    
    @Override
    public int getMaxSelectionRow() {
        return this.listSelectionModel.getMaxSelectionIndex();
    }
    
    @Override
    public boolean isRowSelected(final int n) {
        return this.listSelectionModel.isSelectedIndex(n);
    }
    
    @Override
    public void resetRowSelection() {
        this.listSelectionModel.clearSelection();
        if (this.selection != null && this.rowMapper != null) {
            final int[] rowsForPaths = this.rowMapper.getRowsForPaths(this.selection);
            for (int i = 0; i < this.selection.length; ++i) {
                final int n = rowsForPaths[i];
                if (n != -1) {
                    this.listSelectionModel.addSelectionInterval(n, n);
                }
            }
            if (this.leadIndex != -1 && rowsForPaths != null) {
                this.leadRow = rowsForPaths[this.leadIndex];
            }
            else if (this.leadPath != null) {
                this.tempPaths[0] = this.leadPath;
                final int[] rowsForPaths2 = this.rowMapper.getRowsForPaths(this.tempPaths);
                this.leadRow = ((rowsForPaths2 != null) ? rowsForPaths2[0] : -1);
            }
            else {
                this.leadRow = -1;
            }
            this.insureRowContinuity();
        }
        else {
            this.leadRow = -1;
        }
    }
    
    @Override
    public int getLeadSelectionRow() {
        return this.leadRow;
    }
    
    @Override
    public TreePath getLeadSelectionPath() {
        return this.leadPath;
    }
    
    @Override
    public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            return;
        }
        this.changeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    protected void insureRowContinuity() {
        if (this.selectionMode == 2 && this.selection != null && this.rowMapper != null) {
            final DefaultListSelectionModel listSelectionModel = this.listSelectionModel;
            final int minSelectionIndex = listSelectionModel.getMinSelectionIndex();
            if (minSelectionIndex != -1) {
                for (int i = minSelectionIndex; i <= listSelectionModel.getMaxSelectionIndex(); ++i) {
                    if (!listSelectionModel.isSelectedIndex(i)) {
                        if (i != minSelectionIndex) {
                            final TreePath[] selectionPaths = new TreePath[i - minSelectionIndex];
                            final int[] rowsForPaths = this.rowMapper.getRowsForPaths(this.selection);
                            for (int j = 0; j < rowsForPaths.length; ++j) {
                                if (rowsForPaths[j] < i) {
                                    selectionPaths[rowsForPaths[j] - minSelectionIndex] = this.selection[j];
                                }
                            }
                            this.setSelectionPaths(selectionPaths);
                            break;
                        }
                        this.clearSelection();
                    }
                }
            }
        }
        else if (this.selectionMode == 1 && this.selection != null && this.selection.length > 1) {
            this.setSelectionPath(this.selection[0]);
        }
    }
    
    protected boolean arePathsContiguous(final TreePath[] array) {
        if (this.rowMapper == null || array.length < 2) {
            return true;
        }
        final BitSet set = new BitSet(32);
        final int length = array.length;
        int n = 0;
        final TreePath[] array2 = { array[0] };
        int n2 = this.rowMapper.getRowsForPaths(array2)[0];
        for (int i = 0; i < length; ++i) {
            if (array[i] != null) {
                array2[0] = array[i];
                final int[] rowsForPaths = this.rowMapper.getRowsForPaths(array2);
                if (rowsForPaths == null) {
                    return false;
                }
                final int n3 = rowsForPaths[0];
                if (n3 == -1 || n3 < n2 - length || n3 > n2 + length) {
                    return false;
                }
                if (n3 < n2) {
                    n2 = n3;
                }
                if (!set.get(n3)) {
                    set.set(n3);
                    ++n;
                }
            }
        }
        for (int n4 = n + n2, j = n2; j < n4; ++j) {
            if (!set.get(j)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean canPathsBeAdded(final TreePath[] array) {
        if (array == null || array.length == 0 || this.rowMapper == null || this.selection == null || this.selectionMode == 4) {
            return true;
        }
        final BitSet set = new BitSet();
        final DefaultListSelectionModel listSelectionModel = this.listSelectionModel;
        int n = listSelectionModel.getMinSelectionIndex();
        int n2 = listSelectionModel.getMaxSelectionIndex();
        final TreePath[] array2 = { null };
        if (n != -1) {
            for (int i = n; i <= n2; ++i) {
                if (listSelectionModel.isSelectedIndex(i)) {
                    set.set(i);
                }
            }
        }
        else {
            array2[0] = array[0];
            n2 = (n = this.rowMapper.getRowsForPaths(array2)[0]);
        }
        for (int j = array.length - 1; j >= 0; --j) {
            if (array[j] != null) {
                array2[0] = array[j];
                final int[] rowsForPaths = this.rowMapper.getRowsForPaths(array2);
                if (rowsForPaths == null) {
                    return false;
                }
                final int n3 = rowsForPaths[0];
                n = Math.min(n3, n);
                n2 = Math.max(n3, n2);
                if (n3 == -1) {
                    return false;
                }
                set.set(n3);
            }
        }
        for (int k = n; k <= n2; ++k) {
            if (!set.get(k)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean canPathsBeRemoved(final TreePath[] array) {
        if (this.rowMapper == null || this.selection == null || this.selectionMode == 4) {
            return true;
        }
        final BitSet set = new BitSet();
        final int length = array.length;
        int min = -1;
        int n = 0;
        final TreePath[] array2 = { null };
        this.lastPaths.clear();
        for (int i = 0; i < length; ++i) {
            if (array[i] != null) {
                this.lastPaths.put(array[i], Boolean.TRUE);
            }
        }
        for (int j = this.selection.length - 1; j >= 0; --j) {
            if (this.lastPaths.get(this.selection[j]) == null) {
                array2[0] = this.selection[j];
                final int[] rowsForPaths = this.rowMapper.getRowsForPaths(array2);
                if (rowsForPaths != null && rowsForPaths[0] != -1 && !set.get(rowsForPaths[0])) {
                    ++n;
                    if (min == -1) {
                        min = rowsForPaths[0];
                    }
                    else {
                        min = Math.min(min, rowsForPaths[0]);
                    }
                    set.set(rowsForPaths[0]);
                }
            }
        }
        this.lastPaths.clear();
        if (n > 1) {
            for (int k = min + n - 1; k >= min; --k) {
                if (!set.get(k)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Deprecated
    protected void notifyPathChange(final Vector<?> vector, final TreePath treePath) {
        final int size = vector.size();
        final boolean[] array = new boolean[size];
        final TreePath[] array2 = new TreePath[size];
        for (int i = 0; i < size; ++i) {
            final PathPlaceHolder pathPlaceHolder = vector.elementAt(i);
            array[i] = pathPlaceHolder.isNew;
            array2[i] = pathPlaceHolder.path;
        }
        this.fireValueChanged(new TreeSelectionEvent(this, array2, array, treePath, this.leadPath));
    }
    
    protected void updateLeadIndex() {
        if (this.leadPath != null) {
            if (this.selection == null) {
                this.leadPath = null;
                final int n = -1;
                this.leadRow = n;
                this.leadIndex = n;
            }
            else {
                final int n2 = -1;
                this.leadIndex = n2;
                this.leadRow = n2;
                for (int i = this.selection.length - 1; i >= 0; --i) {
                    if (this.selection[i] == this.leadPath) {
                        this.leadIndex = i;
                        break;
                    }
                }
            }
        }
        else {
            this.leadIndex = -1;
        }
    }
    
    protected void insureUniqueness() {
    }
    
    @Override
    public String toString() {
        final int selectionCount = this.getSelectionCount();
        final StringBuffer sb = new StringBuffer();
        int[] rowsForPaths;
        if (this.rowMapper != null) {
            rowsForPaths = this.rowMapper.getRowsForPaths(this.selection);
        }
        else {
            rowsForPaths = null;
        }
        sb.append(this.getClass().getName() + " " + this.hashCode() + " [ ");
        for (int i = 0; i < selectionCount; ++i) {
            if (rowsForPaths != null) {
                sb.append(this.selection[i].toString() + "@" + Integer.toString(rowsForPaths[i]) + " ");
            }
            else {
                sb.append(this.selection[i].toString() + " ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DefaultTreeSelectionModel defaultTreeSelectionModel = (DefaultTreeSelectionModel)super.clone();
        defaultTreeSelectionModel.changeSupport = null;
        if (this.selection != null) {
            final int length = this.selection.length;
            defaultTreeSelectionModel.selection = new TreePath[length];
            System.arraycopy(this.selection, 0, defaultTreeSelectionModel.selection, 0, length);
        }
        defaultTreeSelectionModel.listenerList = new EventListenerList();
        defaultTreeSelectionModel.listSelectionModel = (DefaultListSelectionModel)this.listSelectionModel.clone();
        defaultTreeSelectionModel.uniquePaths = new Hashtable<TreePath, Boolean>();
        defaultTreeSelectionModel.lastPaths = new Hashtable<TreePath, Boolean>();
        defaultTreeSelectionModel.tempPaths = new TreePath[1];
        return defaultTreeSelectionModel;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Object[] array;
        if (this.rowMapper != null && this.rowMapper instanceof Serializable) {
            array = new Object[] { "rowMapper", this.rowMapper };
        }
        else {
            array = new Object[0];
        }
        objectOutputStream.writeObject(array);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Object[] array = (Object[])objectInputStream.readObject();
        if (array.length > 0 && array[0].equals("rowMapper")) {
            this.rowMapper = (RowMapper)array[1];
        }
    }
}
