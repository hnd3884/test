package com.adventnet.idioms.treetablenavigator;

import com.adventnet.beans.xtable.XTableColumn;
import com.adventnet.beans.rangenavigator.NavigationModel;
import com.adventnet.beans.treetable.TreeTableModel;
import java.awt.Component;
import com.adventnet.beans.treetable.TreeTable;
import com.adventnet.idioms.tablenavigator.NavigationView;

public class TreeTableNavigator extends NavigationView
{
    private TreeTableNavigatorModel model;
    protected TreeTable treeTable;
    
    public TreeTableNavigator() {
        this.treeTable = null;
        this.init();
    }
    
    public TreeTableNavigator(final TreeTableNavigatorModel model) {
        this.treeTable = null;
        this.init();
        this.setModel(model);
    }
    
    private void init() {
        this.add((Component)(this.treeTable = new TreeTable()));
    }
    
    public void setModel(final TreeTableNavigatorModel model) {
        this.model = model;
        this.treeTable.setTreeTableModel((TreeTableModel)model);
        this.getRangeNavigator().setModel((NavigationModel)model);
    }
    
    public TreeTableNavigatorModel getModel() {
        return this.model;
    }
    
    public void setModelSortEnabled(final boolean modelSortEnabled) {
        this.treeTable.setModelSortEnabled(modelSortEnabled);
    }
    
    public void enableModelSortOnColumn(final int n, final boolean b) {
        this.treeTable.enableModelSortOnColumn(n, b);
    }
    
    public boolean isModelSortEnabled() {
        return this.treeTable.isModelSortEnabled();
    }
    
    public void setViewSortEnabled(final boolean b) {
        for (int i = 0; i < this.treeTable.getColumnCount(); ++i) {
            this.enableViewSortOnColumn(i, b);
        }
        this.treeTable.getXTableHeader().repaint();
    }
    
    public void enableViewSortOnColumn(final int n, final boolean viewSortEnabled) {
        ((XTableColumn)this.treeTable.getColumnModel().getColumn(n)).setViewSortEnabled(viewSortEnabled);
    }
    
    public boolean isViewSortEnabled() {
        boolean b = true;
        for (int i = 0; i < this.treeTable.getColumnCount(); ++i) {
            if (!((XTableColumn)this.treeTable.getColumnModel().getColumn(i)).isViewSortEnabled()) {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public TreeTable getTreeTable() {
        return this.treeTable;
    }
    
    public TreeTableNavigatorModel getTreeTableNavigatorModel() {
        return this.model;
    }
}
