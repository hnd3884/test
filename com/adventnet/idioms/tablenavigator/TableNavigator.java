package com.adventnet.idioms.tablenavigator;

import java.awt.Color;
import javax.swing.Icon;
import com.adventnet.beans.xtable.events.ColumnSortedListener;
import com.adventnet.beans.xtable.events.ColumnViewListener;
import com.adventnet.beans.rangenavigator.NavigationModel;
import com.adventnet.beans.xtable.XTableModel;
import java.awt.Component;
import javax.swing.event.TableModelListener;
import com.adventnet.beans.xtable.XTable;

public class TableNavigator extends NavigationView
{
    protected XTable xt;
    private TableModelListener modelListener;
    private TableNavigatorModel tableNavigatorModel;
    
    public TableNavigator() {
        this(new DefaultTableNavigatorModel());
    }
    
    public TableNavigator(final TableNavigatorModel tableNavigatorModel) {
        this.init(tableNavigatorModel);
    }
    
    private void init(final TableNavigatorModel model) {
        this.add((Component)(this.xt = new XTable()));
        this.setModel(model);
    }
    
    public TableNavigatorModel getModel() {
        return this.tableNavigatorModel;
    }
    
    public XTable getXTable() {
        return this.xt;
    }
    
    public void setModel(final TableNavigatorModel model) {
        this.tableNavigatorModel = model;
        this.xt.setModel((XTableModel)model);
        this.rangeNavigator.setModel((NavigationModel)model);
    }
    
    public void addColumnViewListener(final ColumnViewListener columnViewListener) {
        this.xt.addColumnViewListener(columnViewListener);
    }
    
    public void removeColumnViewListener(final ColumnViewListener columnViewListener) {
        this.xt.removeColumnViewListener(columnViewListener);
    }
    
    public void addColumnSortedListener(final ColumnSortedListener columnSortedListener) {
        this.xt.addColumnSortedListener(columnSortedListener);
    }
    
    public void removeColumnSortedListener(final ColumnSortedListener columnSortedListener) {
        this.xt.removeColumnSortedListener(columnSortedListener);
    }
    
    public void setTableEditable(final boolean editable) {
        this.xt.setEditable(editable);
    }
    
    public boolean isTableEditable() {
        return this.xt.isEditable();
    }
    
    public void setFitToSizeEnabled(final boolean fitToSizeEnabled) {
        this.xt.setFitToSizeEnabled(fitToSizeEnabled);
        if (fitToSizeEnabled) {
            this.getScrollPane().setHorizontalScrollBarPolicy(30);
        }
        else {
            this.getScrollPane().setHorizontalScrollBarPolicy(31);
        }
    }
    
    public boolean isFitToSizeEnabled() {
        return this.xt.isFitToSizeEnabled();
    }
    
    public void setRightCornerHeaderComponentIcon(final Icon rightCornerHeaderComponentIcon) {
        this.xt.setRightCornerHeaderComponentIcon(rightCornerHeaderComponentIcon);
    }
    
    public Icon getRightCornerHeaderComponentIcon() {
        return this.xt.getRightCornerHeaderComponentIcon();
    }
    
    public void setRightCornerHeaderComponentVisible(final boolean rightCornerHeaderComponentVisible) {
        this.xt.setRightCornerHeaderComponentVisible(rightCornerHeaderComponentVisible);
    }
    
    public boolean isRightCornerHeaderComponentVisible() {
        return this.xt.isRightCornerHeaderComponentVisible();
    }
    
    public void setRowStripsEnabled(final boolean rowStripsEnabled) {
        this.xt.setRowStripsEnabled(rowStripsEnabled);
    }
    
    public boolean isRowStripsEnabled() {
        return this.xt.isRowStripsEnabled();
    }
    
    public void setStripColor1(final Color stripColor1) {
        this.xt.setStripColor1(stripColor1);
    }
    
    public Color getStripColor1() {
        return this.xt.getStripColor1();
    }
    
    public void setStripColor2(final Color stripColor2) {
        this.xt.setStripColor2(stripColor2);
    }
    
    public Color getStripColor2() {
        return this.xt.getStripColor2();
    }
    
    public void setAscendingIconForModelSort(final Icon ascendingIconForModelSort) {
        this.xt.setAscendingIconForModelSort(ascendingIconForModelSort);
    }
    
    public Icon getAscendingIconForModelSort() {
        return this.xt.getAscendingIconForModelSort();
    }
    
    public void setDescendingIconForModelSort(final Icon descendingIconForModelSort) {
        this.xt.setDescendingIconForModelSort(descendingIconForModelSort);
    }
    
    public Icon getDescendingIconForModelSort() {
        return this.xt.getDescendingIconForModelSort();
    }
    
    public void setAscendingIconForViewSort(final Icon ascendingIconForViewSort) {
        this.xt.setAscendingIconForViewSort(ascendingIconForViewSort);
    }
    
    public Icon getAscendingIconForViewSort() {
        return this.xt.getAscendingIconForViewSort();
    }
    
    public void setDescendingIconForViewSort(final Icon descendingIconForViewSort) {
        this.xt.setDescendingIconForViewSort(descendingIconForViewSort);
    }
    
    public Icon getDescendingIconForViewSort() {
        return this.xt.getDescendingIconForViewSort();
    }
}
