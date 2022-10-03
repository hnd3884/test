package com.adventnet.idioms.treetablenavigator;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import com.adventnet.beans.xtable.ModelException;
import java.util.Locale;
import com.adventnet.beans.treetable.TreeTableModel;
import com.adventnet.beans.rangenavigator.events.NavigationEvent;
import java.util.EventListener;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import javax.swing.tree.TreeNode;
import com.adventnet.beans.xtable.SortColumn;
import com.adventnet.beans.treetable.DefaultTreeTableModel;

public class DefaultTreeTableNavigatorModel extends DefaultTreeTableModel implements TreeTableNavigatorModel
{
    private String dummy;
    private long pageLength;
    private long from;
    private long to;
    private long total;
    private SortColumn[] modelSortedCols;
    private SortColumn[] viewSortedCols;
    private boolean superCall;
    
    public DefaultTreeTableNavigatorModel(final TreeNode treeNode, final String[] array, final String[] array2, final String[] array3, final Class[] array4) {
        super(treeNode, array, array2, array3, array4);
        this.pageLength = 10L;
        this.superCall = false;
        this.init();
    }
    
    private void init() {
        this.dummy = "";
        if (this.getTotalRecordsCount() == 0L) {
            this.from = 0L;
            this.to = 0L;
        }
        else {
            this.from = 1L;
            this.to = this.from + this.pageLength - 1L;
            if (this.to > this.getTotalRecordsCount()) {
                this.to = this.getTotalRecordsCount();
            }
        }
        this.showRange(this.getStartIndex(), this.getEndIndex());
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public long getStartIndex() {
        return this.from;
    }
    
    public long getEndIndex() {
        return this.to;
    }
    
    public long getTotalRecordsCount() {
        return super.getChildCount(this.getRoot());
    }
    
    public void setPageLength(final long pageLength) {
        if (this.pageLength == pageLength) {
            return;
        }
        if (this.validate(this.from, this.to, this.getTotalRecordsCount(), pageLength)) {
            this.pageLength = pageLength;
            this.to = this.from + pageLength - 1L;
            if (this.to > this.getTotalRecordsCount()) {
                this.to = this.getTotalRecordsCount();
            }
            this.adjustValues();
            this.fireTreeStructureChanged((Object)this, (Object[])this.getPathToRoot((TreeNode)this.getRoot()), (int[])null, (Object[])null);
        }
        this.fireNavigationEvent();
    }
    
    public void showRange(final long from, final long to) {
        if (this.from == from && this.to == to) {
            return;
        }
        if (this.validate(from, to, this.getTotalRecordsCount(), this.pageLength)) {
            this.from = from;
            this.to = to;
            if (this.to > this.getTotalRecordsCount()) {
                this.to = this.getTotalRecordsCount();
            }
            this.pageLength = this.to - this.from + 1L;
            this.adjustValues();
            this.fireTreeStructureChanged((Object)this, (Object[])this.getPathToRoot((TreeNode)this.getRoot()), (int[])null, (Object[])null);
        }
        this.fireNavigationEvent();
    }
    
    public void addNavigationListener(final NavigationListener navigationListener) {
        this.listenerList.add(NavigationListener.class, navigationListener);
    }
    
    public void removeNavigationListener(final NavigationListener navigationListener) {
        this.listenerList.remove(NavigationListener.class, navigationListener);
    }
    
    private void fireNavigationEvent() {
        final EventListener[] listeners = this.listenerList.getListeners((Class<EventListener>)NavigationListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((NavigationListener)listeners[i]).navigationChanged(new NavigationEvent((Object)this));
        }
    }
    
    public void sortView(final Object o, final SortColumn[] viewSortedCols) throws ModelException {
        this.viewSortedCols = viewSortedCols;
        if (viewSortedCols != null) {
            this.superCall = true;
            if (o == this.getRoot()) {
                this.getTreeTableModelSorter().sort((TreeTableModel)this, o, viewSortedCols, (Locale)null, (int)this.getStartIndex() - 1, (int)this.getEndIndex());
            }
            else {
                this.getTreeTableModelSorter().sort((TreeTableModel)this, o, viewSortedCols, (Locale)null, 0, ((TreeNode)o).getChildCount());
            }
            this.superCall = false;
            this.fireNavigationEvent();
        }
    }
    
    public void sortModel(final Object o, final SortColumn[] modelSortedCols) throws ModelException {
        this.modelSortedCols = modelSortedCols;
        if (modelSortedCols != null) {
            this.superCall = true;
            this.getTreeTableModelSorter().sort((TreeTableModel)this, o, modelSortedCols, (Locale)null, 0, ((TreeNode)o).getChildCount());
            this.superCall = false;
            this.fireNavigationEvent();
        }
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortedCols;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortedCols;
    }
    
    public int getChildCount(final Object o) {
        if (this.getRoot() != o) {
            return super.getChildCount(o);
        }
        if (this.dummy == null || this.superCall) {
            return super.getChildCount(o);
        }
        return (int)this.getEndIndex() - (int)this.getStartIndex() + 1;
    }
    
    public Object getChild(final Object o, final int n) {
        if (this.getRoot() != o || this.superCall) {
            return super.getChild(o, n);
        }
        return super.getChild(o, n + (int)this.getStartIndex() - 1);
    }
    
    public boolean validate(final long n, final long n2, final long n3, final long n4) {
        return n >= 0L && n2 >= 0L && n4 > 0L && n3 >= 0L && ((n != 0L && n2 != 0L) || n3 == 0L) && n <= n2 && n3 >= n;
    }
    
    public void addNode(final DefaultMutableTreeNode defaultMutableTreeNode, final MutableTreeNode mutableTreeNode) {
        defaultMutableTreeNode.add(mutableTreeNode);
        this.adjustValues();
        this.fireNavigationEvent();
        this.fireTreeStructureChanged((Object)this, (Object[])this.getPathToRoot((TreeNode)defaultMutableTreeNode), (int[])null, (Object[])null);
    }
    
    public void removeNode(final TreeNode treeNode) {
        this.superCall = true;
        this.removeNodeFromParent(treeNode);
        this.superCall = false;
        this.adjustValues();
        this.fireNavigationEvent();
        this.fireTreeStructureChanged((Object)this, (Object[])this.getPathToRoot((TreeNode)this.getRoot()), (int[])null, (Object[])null);
    }
    
    private void adjustValues() {
        if (this.getTotalRecordsCount() == 0L) {
            this.from = 0L;
            this.to = 0L;
            return;
        }
        if (this.from <= 0L && this.getTotalRecordsCount() > 0L) {
            this.from = 1L;
            this.to = this.from + this.pageLength - 1L;
        }
        if (this.to > this.getTotalRecordsCount()) {
            this.to = this.getTotalRecordsCount();
            this.from = this.to - this.pageLength;
            if (this.from < 0L) {
                this.from = ((this.getTotalRecordsCount() > 0L) ? 1 : 0);
            }
        }
        if (this.to - this.from + 1L < this.pageLength && this.getTotalRecordsCount() - this.pageLength < this.from) {
            this.to = this.getTotalRecordsCount();
        }
    }
}
