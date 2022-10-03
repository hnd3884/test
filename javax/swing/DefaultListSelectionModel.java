package javax.swing;

import java.beans.Transient;
import java.util.EventListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.EventListenerList;
import java.util.BitSet;
import java.io.Serializable;

public class DefaultListSelectionModel implements ListSelectionModel, Cloneable, Serializable
{
    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    private int selectionMode;
    private int minIndex;
    private int maxIndex;
    private int anchorIndex;
    private int leadIndex;
    private int firstAdjustedIndex;
    private int lastAdjustedIndex;
    private boolean isAdjusting;
    private int firstChangedIndex;
    private int lastChangedIndex;
    private BitSet value;
    protected EventListenerList listenerList;
    protected boolean leadAnchorNotificationEnabled;
    
    public DefaultListSelectionModel() {
        this.selectionMode = 2;
        this.minIndex = Integer.MAX_VALUE;
        this.maxIndex = -1;
        this.anchorIndex = -1;
        this.leadIndex = -1;
        this.firstAdjustedIndex = Integer.MAX_VALUE;
        this.lastAdjustedIndex = -1;
        this.isAdjusting = false;
        this.firstChangedIndex = Integer.MAX_VALUE;
        this.lastChangedIndex = -1;
        this.value = new BitSet(32);
        this.listenerList = new EventListenerList();
        this.leadAnchorNotificationEnabled = true;
    }
    
    @Override
    public int getMinSelectionIndex() {
        return this.isSelectionEmpty() ? -1 : this.minIndex;
    }
    
    @Override
    public int getMaxSelectionIndex() {
        return this.maxIndex;
    }
    
    @Override
    public boolean getValueIsAdjusting() {
        return this.isAdjusting;
    }
    
    @Override
    public int getSelectionMode() {
        return this.selectionMode;
    }
    
    @Override
    public void setSelectionMode(final int selectionMode) {
        switch (selectionMode) {
            case 0:
            case 1:
            case 2: {
                this.selectionMode = selectionMode;
                return;
            }
            default: {
                throw new IllegalArgumentException("invalid selectionMode");
            }
        }
    }
    
    @Override
    public boolean isSelectedIndex(final int n) {
        return n >= this.minIndex && n <= this.maxIndex && this.value.get(n);
    }
    
    @Override
    public boolean isSelectionEmpty() {
        return this.minIndex > this.maxIndex;
    }
    
    @Override
    public void addListSelectionListener(final ListSelectionListener listSelectionListener) {
        this.listenerList.add(ListSelectionListener.class, listSelectionListener);
    }
    
    @Override
    public void removeListSelectionListener(final ListSelectionListener listSelectionListener) {
        this.listenerList.remove(ListSelectionListener.class, listSelectionListener);
    }
    
    public ListSelectionListener[] getListSelectionListeners() {
        return this.listenerList.getListeners(ListSelectionListener.class);
    }
    
    protected void fireValueChanged(final boolean b) {
        if (this.lastChangedIndex == -1) {
            return;
        }
        final int firstChangedIndex = this.firstChangedIndex;
        final int lastChangedIndex = this.lastChangedIndex;
        this.firstChangedIndex = Integer.MAX_VALUE;
        this.lastChangedIndex = -1;
        this.fireValueChanged(firstChangedIndex, lastChangedIndex, b);
    }
    
    protected void fireValueChanged(final int n, final int n2) {
        this.fireValueChanged(n, n2, this.getValueIsAdjusting());
    }
    
    protected void fireValueChanged(final int n, final int n2, final boolean b) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ListSelectionEvent listSelectionEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ListSelectionListener.class) {
                if (listSelectionEvent == null) {
                    listSelectionEvent = new ListSelectionEvent(this, n, n2, b);
                }
                ((ListSelectionListener)listenerList[i + 1]).valueChanged(listSelectionEvent);
            }
        }
    }
    
    private void fireValueChanged() {
        if (this.lastAdjustedIndex == -1) {
            return;
        }
        if (this.getValueIsAdjusting()) {
            this.firstChangedIndex = Math.min(this.firstChangedIndex, this.firstAdjustedIndex);
            this.lastChangedIndex = Math.max(this.lastChangedIndex, this.lastAdjustedIndex);
        }
        final int firstAdjustedIndex = this.firstAdjustedIndex;
        final int lastAdjustedIndex = this.lastAdjustedIndex;
        this.firstAdjustedIndex = Integer.MAX_VALUE;
        this.lastAdjustedIndex = -1;
        this.fireValueChanged(firstAdjustedIndex, lastAdjustedIndex);
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    private void markAsDirty(final int n) {
        if (n == -1) {
            return;
        }
        this.firstAdjustedIndex = Math.min(this.firstAdjustedIndex, n);
        this.lastAdjustedIndex = Math.max(this.lastAdjustedIndex, n);
    }
    
    private void set(final int n) {
        if (this.value.get(n)) {
            return;
        }
        this.value.set(n);
        this.markAsDirty(n);
        this.minIndex = Math.min(this.minIndex, n);
        this.maxIndex = Math.max(this.maxIndex, n);
    }
    
    private void clear(final int n) {
        if (!this.value.get(n)) {
            return;
        }
        this.value.clear(n);
        this.markAsDirty(n);
        if (n == this.minIndex) {
            ++this.minIndex;
            while (this.minIndex <= this.maxIndex) {
                if (this.value.get(this.minIndex)) {
                    break;
                }
                ++this.minIndex;
            }
        }
        if (n == this.maxIndex) {
            --this.maxIndex;
            while (this.minIndex <= this.maxIndex) {
                if (this.value.get(this.maxIndex)) {
                    break;
                }
                --this.maxIndex;
            }
        }
        if (this.isSelectionEmpty()) {
            this.minIndex = Integer.MAX_VALUE;
            this.maxIndex = -1;
        }
    }
    
    public void setLeadAnchorNotificationEnabled(final boolean leadAnchorNotificationEnabled) {
        this.leadAnchorNotificationEnabled = leadAnchorNotificationEnabled;
    }
    
    public boolean isLeadAnchorNotificationEnabled() {
        return this.leadAnchorNotificationEnabled;
    }
    
    private void updateLeadAnchorIndices(final int anchorIndex, final int leadIndex) {
        if (this.leadAnchorNotificationEnabled) {
            if (this.anchorIndex != anchorIndex) {
                this.markAsDirty(this.anchorIndex);
                this.markAsDirty(anchorIndex);
            }
            if (this.leadIndex != leadIndex) {
                this.markAsDirty(this.leadIndex);
                this.markAsDirty(leadIndex);
            }
        }
        this.anchorIndex = anchorIndex;
        this.leadIndex = leadIndex;
    }
    
    private boolean contains(final int n, final int n2, final int n3) {
        return n3 >= n && n3 <= n2;
    }
    
    private void changeSelection(final int n, final int n2, final int n3, final int n4, final boolean b) {
        for (int i = Math.min(n3, n); i <= Math.max(n4, n2); ++i) {
            int contains = this.contains(n, n2, i) ? 1 : 0;
            int contains2 = this.contains(n3, n4, i) ? 1 : 0;
            if (contains2 != 0 && contains != 0) {
                if (b) {
                    contains = 0;
                }
                else {
                    contains2 = 0;
                }
            }
            if (contains2 != 0) {
                this.set(i);
            }
            if (contains != 0) {
                this.clear(i);
            }
        }
        this.fireValueChanged();
    }
    
    private void changeSelection(final int n, final int n2, final int n3, final int n4) {
        this.changeSelection(n, n2, n3, n4, true);
    }
    
    @Override
    public void clearSelection() {
        this.removeSelectionIntervalImpl(this.minIndex, this.maxIndex, false);
    }
    
    @Override
    public void setSelectionInterval(int n, final int n2) {
        if (n == -1 || n2 == -1) {
            return;
        }
        if (this.getSelectionMode() == 0) {
            n = n2;
        }
        this.updateLeadAnchorIndices(n, n2);
        this.changeSelection(this.minIndex, this.maxIndex, Math.min(n, n2), Math.max(n, n2));
    }
    
    @Override
    public void addSelectionInterval(final int n, final int n2) {
        if (n == -1 || n2 == -1) {
            return;
        }
        if (this.getSelectionMode() == 0) {
            this.setSelectionInterval(n, n2);
            return;
        }
        this.updateLeadAnchorIndices(n, n2);
        final int n3 = Integer.MAX_VALUE;
        final int n4 = -1;
        final int min = Math.min(n, n2);
        final int max = Math.max(n, n2);
        if (this.getSelectionMode() == 1 && (max < this.minIndex - 1 || min > this.maxIndex + 1)) {
            this.setSelectionInterval(n, n2);
            return;
        }
        this.changeSelection(n3, n4, min, max);
    }
    
    @Override
    public void removeSelectionInterval(final int n, final int n2) {
        this.removeSelectionIntervalImpl(n, n2, true);
    }
    
    private void removeSelectionIntervalImpl(final int n, final int n2, final boolean b) {
        if (n == -1 || n2 == -1) {
            return;
        }
        if (b) {
            this.updateLeadAnchorIndices(n, n2);
        }
        final int min = Math.min(n, n2);
        int n3 = Math.max(n, n2);
        final int n4 = Integer.MAX_VALUE;
        final int n5 = -1;
        if (this.getSelectionMode() != 2 && min > this.minIndex && n3 < this.maxIndex) {
            n3 = this.maxIndex;
        }
        this.changeSelection(min, n3, n4, n5);
    }
    
    private void setState(final int n, final boolean b) {
        if (b) {
            this.set(n);
        }
        else {
            this.clear(n);
        }
    }
    
    @Override
    public void insertIndexInterval(final int n, final int n2, final boolean b) {
        final int n3 = b ? n : (n + 1);
        final int n4 = n3 + n2 - 1;
        for (int i = this.maxIndex; i >= n3; --i) {
            this.setState(i + n2, this.value.get(i));
        }
        final boolean b2 = this.getSelectionMode() != 0 && this.value.get(n);
        for (int j = n3; j <= n4; ++j) {
            this.setState(j, b2);
        }
        int leadIndex = this.leadIndex;
        if (leadIndex > n || (b && leadIndex == n)) {
            leadIndex = this.leadIndex + n2;
        }
        int anchorIndex = this.anchorIndex;
        if (anchorIndex > n || (b && anchorIndex == n)) {
            anchorIndex = this.anchorIndex + n2;
        }
        if (leadIndex != this.leadIndex || anchorIndex != this.anchorIndex) {
            this.updateLeadAnchorIndices(anchorIndex, leadIndex);
        }
        this.fireValueChanged();
    }
    
    @Override
    public void removeIndexInterval(final int n, final int n2) {
        final int min = Math.min(n, n2);
        final int max = Math.max(n, n2);
        final int n3 = max - min + 1;
        for (int i = min; i <= this.maxIndex; ++i) {
            this.setState(i, this.value.get(i + n3));
        }
        int leadIndex = this.leadIndex;
        if (leadIndex != 0 || min != 0) {
            if (leadIndex > max) {
                leadIndex = this.leadIndex - n3;
            }
            else if (leadIndex >= min) {
                leadIndex = min - 1;
            }
        }
        int anchorIndex = this.anchorIndex;
        if (anchorIndex != 0 || min != 0) {
            if (anchorIndex > max) {
                anchorIndex = this.anchorIndex - n3;
            }
            else if (anchorIndex >= min) {
                anchorIndex = min - 1;
            }
        }
        if (leadIndex != this.leadIndex || anchorIndex != this.anchorIndex) {
            this.updateLeadAnchorIndices(anchorIndex, leadIndex);
        }
        this.fireValueChanged();
    }
    
    @Override
    public void setValueIsAdjusting(final boolean isAdjusting) {
        if (isAdjusting != this.isAdjusting) {
            this.fireValueChanged(this.isAdjusting = isAdjusting);
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " " + Integer.toString(this.hashCode()) + " " + ((this.getValueIsAdjusting() ? "~" : "=") + this.value.toString());
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DefaultListSelectionModel defaultListSelectionModel = (DefaultListSelectionModel)super.clone();
        defaultListSelectionModel.value = (BitSet)this.value.clone();
        defaultListSelectionModel.listenerList = new EventListenerList();
        return defaultListSelectionModel;
    }
    
    @Transient
    @Override
    public int getAnchorSelectionIndex() {
        return this.anchorIndex;
    }
    
    @Transient
    @Override
    public int getLeadSelectionIndex() {
        return this.leadIndex;
    }
    
    @Override
    public void setAnchorSelectionIndex(final int n) {
        this.updateLeadAnchorIndices(n, this.leadIndex);
        this.fireValueChanged();
    }
    
    public void moveLeadSelectionIndex(final int n) {
        if (n == -1 && this.anchorIndex != -1) {
            return;
        }
        this.updateLeadAnchorIndices(this.anchorIndex, n);
        this.fireValueChanged();
    }
    
    @Override
    public void setLeadSelectionIndex(final int leadIndex) {
        int anchorIndex = this.anchorIndex;
        if (leadIndex == -1) {
            if (anchorIndex == -1) {
                this.updateLeadAnchorIndices(anchorIndex, leadIndex);
                this.fireValueChanged();
            }
            return;
        }
        if (anchorIndex == -1) {
            return;
        }
        if (this.leadIndex == -1) {
            this.leadIndex = leadIndex;
        }
        boolean value = this.value.get(this.anchorIndex);
        if (this.getSelectionMode() == 0) {
            anchorIndex = leadIndex;
            value = true;
        }
        final int min = Math.min(this.anchorIndex, this.leadIndex);
        final int max = Math.max(this.anchorIndex, this.leadIndex);
        final int min2 = Math.min(anchorIndex, leadIndex);
        final int max2 = Math.max(anchorIndex, leadIndex);
        this.updateLeadAnchorIndices(anchorIndex, leadIndex);
        if (value) {
            this.changeSelection(min, max, min2, max2);
        }
        else {
            this.changeSelection(min2, max2, min, max, false);
        }
    }
}
