package javax.swing.text.html;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.EventListenerList;
import java.util.BitSet;
import java.io.Serializable;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;

class OptionListModel<E> extends DefaultListModel<E> implements ListSelectionModel, Serializable
{
    private static final int MIN = -1;
    private static final int MAX = Integer.MAX_VALUE;
    private int selectionMode;
    private int minIndex;
    private int maxIndex;
    private int anchorIndex;
    private int leadIndex;
    private int firstChangedIndex;
    private int lastChangedIndex;
    private boolean isAdjusting;
    private BitSet value;
    private BitSet initialValue;
    protected EventListenerList listenerList;
    protected boolean leadAnchorNotificationEnabled;
    
    OptionListModel() {
        this.selectionMode = 0;
        this.minIndex = Integer.MAX_VALUE;
        this.maxIndex = -1;
        this.anchorIndex = -1;
        this.leadIndex = -1;
        this.firstChangedIndex = Integer.MAX_VALUE;
        this.lastChangedIndex = -1;
        this.isAdjusting = false;
        this.value = new BitSet(32);
        this.initialValue = new BitSet(32);
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
        this.fireValueChanged(this.getMinSelectionIndex(), this.getMaxSelectionIndex(), b);
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
        if (this.lastChangedIndex == -1) {
            return;
        }
        final int firstChangedIndex = this.firstChangedIndex;
        final int lastChangedIndex = this.lastChangedIndex;
        this.firstChangedIndex = Integer.MAX_VALUE;
        this.lastChangedIndex = -1;
        this.fireValueChanged(firstChangedIndex, lastChangedIndex);
    }
    
    private void markAsDirty(final int n) {
        this.firstChangedIndex = Math.min(this.firstChangedIndex, n);
        this.lastChangedIndex = Math.max(this.lastChangedIndex, n);
    }
    
    private void set(final int n) {
        if (this.value.get(n)) {
            return;
        }
        this.value.set(n);
        this.get(n).setSelection(true);
        this.markAsDirty(n);
        this.minIndex = Math.min(this.minIndex, n);
        this.maxIndex = Math.max(this.maxIndex, n);
    }
    
    private void clear(final int n) {
        if (!this.value.get(n)) {
            return;
        }
        this.value.clear(n);
        this.get(n).setSelection(false);
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
                if (this.anchorIndex != -1) {
                    this.markAsDirty(this.anchorIndex);
                }
                this.markAsDirty(anchorIndex);
            }
            if (this.leadIndex != leadIndex) {
                if (this.leadIndex != -1) {
                    this.markAsDirty(this.leadIndex);
                }
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
        this.removeSelectionInterval(this.minIndex, this.maxIndex);
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
        if (this.getSelectionMode() != 2) {
            this.setSelectionInterval(n, n2);
            return;
        }
        this.updateLeadAnchorIndices(n, n2);
        this.changeSelection(Integer.MAX_VALUE, -1, Math.min(n, n2), Math.max(n, n2));
    }
    
    @Override
    public void removeSelectionInterval(final int n, final int n2) {
        if (n == -1 || n2 == -1) {
            return;
        }
        this.updateLeadAnchorIndices(n, n2);
        this.changeSelection(Math.min(n, n2), Math.max(n, n2), Integer.MAX_VALUE, -1);
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
        final boolean value = this.value.get(n);
        for (int j = n3; j <= n4; ++j) {
            this.setState(j, value);
        }
    }
    
    @Override
    public void removeIndexInterval(final int n, final int n2) {
        final int min = Math.min(n, n2);
        final int n3 = Math.max(n, n2) - min + 1;
        for (int i = min; i <= this.maxIndex; ++i) {
            this.setState(i, this.value.get(i + n3));
        }
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
        final OptionListModel optionListModel = (OptionListModel)super.clone();
        optionListModel.value = (BitSet)this.value.clone();
        optionListModel.listenerList = new EventListenerList();
        return optionListModel;
    }
    
    @Override
    public int getAnchorSelectionIndex() {
        return this.anchorIndex;
    }
    
    @Override
    public int getLeadSelectionIndex() {
        return this.leadIndex;
    }
    
    @Override
    public void setAnchorSelectionIndex(final int anchorIndex) {
        this.anchorIndex = anchorIndex;
    }
    
    @Override
    public void setLeadSelectionIndex(final int leadIndex) {
        int anchorIndex = this.anchorIndex;
        if (this.getSelectionMode() == 0) {
            anchorIndex = leadIndex;
        }
        final int min = Math.min(this.anchorIndex, this.leadIndex);
        final int max = Math.max(this.anchorIndex, this.leadIndex);
        final int min2 = Math.min(anchorIndex, leadIndex);
        final int max2 = Math.max(anchorIndex, leadIndex);
        if (this.value.get(this.anchorIndex)) {
            this.changeSelection(min, max, min2, max2);
        }
        else {
            this.changeSelection(min2, max2, min, max, false);
        }
        this.anchorIndex = anchorIndex;
        this.leadIndex = leadIndex;
    }
    
    public void setInitialSelection(final int n) {
        if (this.initialValue.get(n)) {
            return;
        }
        if (this.selectionMode == 0) {
            this.initialValue.and(new BitSet());
        }
        this.initialValue.set(n);
    }
    
    public BitSet getInitialSelection() {
        return this.initialValue;
    }
}
