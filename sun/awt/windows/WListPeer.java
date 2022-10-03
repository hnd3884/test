package sun.awt.windows;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import sun.awt.SunToolkit;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.List;
import java.awt.FontMetrics;
import java.awt.peer.ListPeer;

final class WListPeer extends WComponentPeer implements ListPeer
{
    private FontMetrics fm;
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public int[] getSelectedIndexes() {
        final int countItems = ((List)this.target).countItems();
        final int[] array = new int[countItems];
        int n = 0;
        for (int i = 0; i < countItems; ++i) {
            if (this.isSelected(i)) {
                array[n++] = i;
            }
        }
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    @Override
    public void add(final String s, final int n) {
        this.addItem(s, n);
    }
    
    @Override
    public void removeAll() {
        this.clear();
    }
    
    @Override
    public void setMultipleMode(final boolean multipleSelections) {
        this.setMultipleSelections(multipleSelections);
    }
    
    @Override
    public Dimension getPreferredSize(final int n) {
        return this.preferredSize(n);
    }
    
    @Override
    public Dimension getMinimumSize(final int n) {
        return this.minimumSize(n);
    }
    
    public void addItem(final String s, final int n) {
        this.addItems(new String[] { s }, n, this.fm.stringWidth(s));
    }
    
    native void addItems(final String[] p0, final int p1, final int p2);
    
    @Override
    public native void delItems(final int p0, final int p1);
    
    public void clear() {
        this.delItems(0, ((List)this.target).countItems());
    }
    
    @Override
    public native void select(final int p0);
    
    @Override
    public native void deselect(final int p0);
    
    @Override
    public native void makeVisible(final int p0);
    
    public native void setMultipleSelections(final boolean p0);
    
    public native int getMaxWidth();
    
    public Dimension preferredSize(final int n) {
        if (this.fm == null) {
            this.fm = this.getFontMetrics(((List)this.target).getFont());
        }
        final Dimension minimumSize = this.minimumSize(n);
        minimumSize.width = Math.max(minimumSize.width, this.getMaxWidth() + 20);
        return minimumSize;
    }
    
    public Dimension minimumSize(final int n) {
        return new Dimension(20 + this.fm.stringWidth("0123456789abcde"), this.fm.getHeight() * n + 4);
    }
    
    WListPeer(final List list) {
        super(list);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        final List list = (List)this.target;
        this.fm = this.getFontMetrics(list.getFont());
        final Font font = list.getFont();
        if (font != null) {
            this.setFont(font);
        }
        final int countItems = list.countItems();
        if (countItems > 0) {
            final String[] array = new String[countItems];
            int n = 0;
            for (int i = 0; i < countItems; ++i) {
                array[i] = list.getItem(i);
                final int stringWidth = this.fm.stringWidth(array[i]);
                if (stringWidth > n) {
                    n = stringWidth;
                }
            }
            this.addItems(array, 0, n);
        }
        this.setMultipleSelections(list.allowsMultipleSelections());
        final int[] selectedIndexes = list.getSelectedIndexes();
        for (int j = 0; j < selectedIndexes.length; ++j) {
            this.select(selectedIndexes[j]);
        }
        int visibleIndex = list.getVisibleIndex();
        if (visibleIndex < 0 && selectedIndexes.length > 0) {
            visibleIndex = selectedIndexes[0];
        }
        if (visibleIndex >= 0) {
            this.makeVisible(visibleIndex);
        }
        super.initialize();
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
    
    private native void updateMaxItemWidth();
    
    native boolean isSelected(final int p0);
    
    @Override
    synchronized void _setFont(final Font font) {
        super._setFont(font);
        this.fm = this.getFontMetrics(((List)this.target).getFont());
        this.updateMaxItemWidth();
    }
    
    void handleAction(final int n, final long n2, final int n3) {
        final List list = (List)this.target;
        SunToolkit.executeOnEventHandlerThread(list, new Runnable() {
            @Override
            public void run() {
                list.select(n);
                WListPeer.this.postEvent(new ActionEvent(WListPeer.this.target, 1001, list.getItem(n), n2, n3));
            }
        });
    }
    
    void handleListChanged(final int n) {
        final List list = (List)this.target;
        SunToolkit.executeOnEventHandlerThread(list, new Runnable() {
            @Override
            public void run() {
                WListPeer.this.postEvent(new ItemEvent(list, 701, n, WListPeer.this.isSelected(n) ? 1 : 2));
            }
        });
    }
}
