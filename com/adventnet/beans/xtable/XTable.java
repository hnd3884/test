package com.adventnet.beans.xtable;

import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import javax.swing.SwingUtilities;
import java.util.EventListener;
import com.adventnet.beans.xtable.events.ColumnViewListener;
import java.util.HashMap;
import javax.swing.table.TableCellRenderer;
import java.awt.FontMetrics;
import javax.swing.table.TableColumn;
import com.adventnet.beans.xtable.events.ColumnSortedEvent;
import javax.swing.border.Border;
import javax.swing.Icon;
import java.awt.event.MouseListener;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.util.Vector;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.ListSelectionModel;
import java.util.Locale;
import com.adventnet.renderers.RowStripingRenderer;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Cursor;
import com.adventnet.beans.xtable.events.ColumnSortedListener;
import javax.swing.JTable;

public class XTable extends JTable implements ColumnSortedListener
{
    private boolean single_sort;
    private boolean multiple_sort;
    private Cursor prevTableCursor;
    private Cursor prevHeaderCursor;
    private Dimension oldCellSpacing;
    private boolean editable;
    private int resizeMode;
    private boolean fitToSizeEnabled;
    private Graphics graph;
    protected RowStripingRenderer rowStripRenderer;
    protected boolean rowStripsEnabled;
    private XTableColumn prevSortedCol;
    private Locale sortLocale;
    protected XHeaderRenderer hr;
    private HeaderClickHandler hchandler;
    
    public XTable() {
        this(4, 4);
    }
    
    public XTable(final XTableModel xTableModel) {
        this(xTableModel, null, null);
    }
    
    public XTable(final XTableModel xTableModel, final XTableColumnModel xTableColumnModel) {
        this(xTableModel, xTableColumnModel, null);
    }
    
    public XTable(final XTableModel xTableModel, final XTableColumnModel xTableColumnModel, final ListSelectionModel listSelectionModel) {
        super(xTableModel, xTableColumnModel, listSelectionModel);
        this.single_sort = false;
        this.multiple_sort = false;
        this.editable = true;
        this.fitToSizeEnabled = false;
        this.hr = new XHeaderRenderer();
        this.hchandler = new HeaderClickHandler();
        if (xTableColumnModel == null) {
            this.createXColumnModel();
        }
        this.initialize();
    }
    
    public XTable(final int n, final int n2) {
        this(new DefaultXTableModel(n, n2));
    }
    
    public XTable(final Vector vector, final Vector vector2) {
        this(new DefaultXTableModel(vector, vector2));
    }
    
    public XTable(final Object[][] array, final Object[] array2) {
        this(new DefaultXTableModel(array, array2));
    }
    
    public void paint(final Graphics graphics) {
        if (this.graph == null && this.isFitToSizeEnabled()) {
            this.graph = graphics;
            this.fitToSizeAll();
        }
        super.paint(this.graph = graphics);
    }
    
    void initialize() {
        this.resizeMode = this.getAutoResizeMode();
        this.addColumnSortedListener(this);
        this.rowStripRenderer = new RowStripingRenderer(Color.lightGray, Color.white);
        this.setAutoCreateColumnsFromModel(false);
        this.setTableHeader(new XTableHeader(this.getColumnModel()));
        this.getXTableHeader().addMouseListener(this.hchandler);
    }
    
    public void setMandatoryColumn(final int n, final boolean mandatoryColumn) {
        this.getColumn(n).setMandatoryColumn(mandatoryColumn);
    }
    
    public void setMandatoryColumn(final Object o, final boolean mandatoryColumn) {
        this.getColumn(this.getColumnModel().getColumnIndex(o)).setMandatoryColumn(mandatoryColumn);
    }
    
    public RightCornerHeaderComponent getRightCornerHeaderComponent() {
        return this.hr.getRightCornerHeaderComponent();
    }
    
    public Icon getRightCornerHeaderComponentIcon() {
        return this.getRightCornerHeaderComponent().getIcon();
    }
    
    public void setRightCornerHeaderComponentIcon(final Icon icon) {
        if (icon != null) {
            this.getRightCornerHeaderComponent().setIcon(icon);
        }
    }
    
    public void setRightCornerHeaderComponentVisible(final boolean visible) {
        this.hr.getRightCornerHeaderComponent().setVisible(visible);
        this.getXTableHeader().repaint();
    }
    
    public boolean isRightCornerHeaderComponentVisible() {
        return this.hr.getRightCornerHeaderComponent().isVisible();
    }
    
    public void setHeaderBorder(final Border headerBorder) {
        this.hr.setHeaderBorder(headerBorder);
    }
    
    public void setHeaderHeight(final int height) {
        this.getXTableHeader().setHeight(height);
    }
    
    public int getHeaderHeight() {
        return this.getXTableHeader().getHeight();
    }
    
    public void setAscendingIconForModelSort(final Icon ascendingIconForModelSort) {
        this.hr.setAscendingIconForModelSort(ascendingIconForModelSort);
    }
    
    public Icon getAscendingIconForModelSort() {
        return this.hr.getAscendingIconForModelSort();
    }
    
    public void setDescendingIconForModelSort(final Icon descendingIconForModelSort) {
        this.hr.setDescendingIconForModelSort(descendingIconForModelSort);
    }
    
    public Icon getDescendingIconForModelSort() {
        return this.hr.getDescendingIconForModelSort();
    }
    
    public void setAscendingIconForViewSort(final Icon ascendingIconForViewSort) {
        this.hr.setAscendingIconForViewSort(ascendingIconForViewSort);
    }
    
    public Icon getAscendingIconForViewSort() {
        return this.hr.getAscendingIconForViewSort();
    }
    
    public void setDescendingIconForViewSort(final Icon descendingIconForViewSort) {
        this.hr.setDescendingIconForViewSort(descendingIconForViewSort);
    }
    
    public Icon getDescendingIconForViewSort() {
        return this.hr.getDescendingIconForViewSort();
    }
    
    public void allowToClearSort(final boolean b) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            this.getColumn(i).allowToClearSort(b);
        }
    }
    
    private void sort(final int n, SortColumn[] convertViewIndexesIntoModelIndexes) throws ModelException {
        this.fireBeforeColumnSortedEvent(new ColumnSortedEvent(this, ((XTableModel)this.getModel()).getModelSortedColumns(), ((XTableModel)this.getModel()).getViewSortedColumns(), n));
        SortColumn[] array = null;
        if (convertViewIndexesIntoModelIndexes != null) {
            array = new SortColumn[convertViewIndexesIntoModelIndexes.length];
            this.sortColumnArrayCopy(convertViewIndexesIntoModelIndexes, array);
        }
        convertViewIndexesIntoModelIndexes = this.convertViewIndexesIntoModelIndexes(convertViewIndexesIntoModelIndexes);
        if (n == 0) {
            ((XTableModel)this.getModel()).sortView(convertViewIndexesIntoModelIndexes);
            this.fireAfterColumnSortedEvent(new ColumnSortedEvent(this, null, array, n));
        }
        else if (n == 1) {
            ((XTableModel)this.getModel()).sortModel(convertViewIndexesIntoModelIndexes);
            this.fireAfterColumnSortedEvent(new ColumnSortedEvent(this, array, null, n));
        }
    }
    
    public void sort(final SortColumn[] array, final int sortType) throws ModelException {
        this.restoreDefaultValuesForAllColumns();
        if (array != null) {
            if (array.length > 1) {
                this.multiple_sort = true;
                this.single_sort = false;
                this.prevSortedCol = null;
            }
            else {
                this.single_sort = true;
                this.multiple_sort = false;
                this.prevSortedCol = this.getColumn(array[0].getColumnIndex());
            }
        }
        switch (sortType) {
            case 0: {
                if (array != null && this.multiple_sort) {
                    this.setViewSortNumbering(array);
                    break;
                }
                break;
            }
            case 1: {
                if (array != null && this.multiple_sort) {
                    this.setModelSortNumbering(array);
                    break;
                }
                break;
            }
        }
        for (int i = 0; i < array.length; ++i) {
            final XTableColumn column = this.getColumn(array[i].getColumnIndex());
            column.setSortType(sortType);
            if (sortType == 0 && column.getViewClickCount() == 0) {
                column.setViewClickCount(array[i].isAscending() ? 1 : 2);
            }
            else if (sortType == 1 && column.getModelClickCount() == 0) {
                column.setModelClickCount(array[i].isAscending() ? 1 : 2);
            }
        }
        this.sort(sortType, array);
    }
    
    private void sortColumnArrayCopy(final SortColumn[] array, final SortColumn[] array2) {
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new SortColumn(array[i].getColumnIndex(), array[i].isAscending());
        }
    }
    
    private SortColumn[] convertModelIndexesToViewIndex(final SortColumn[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                array[i].setColumnIndex(this.convertColumnIndexToView(array[i].getColumnIndex()));
            }
        }
        return array;
    }
    
    private SortColumn[] convertViewIndexesIntoModelIndexes(final SortColumn[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                array[i].setColumnIndex(this.convertColumnIndexToModel(array[i].getColumnIndex()));
            }
        }
        return array;
    }
    
    private void createXColumnModel() {
        final XTableColumnModel columnModel = new XTableColumnModel();
        columnModel.setHeaderRenderer(this.hr);
        this.setColumnModel(columnModel);
        this.createDefaultColumnsFromModel();
    }
    
    public void setModel(final XTableModel model) {
        super.setModel(model);
        this.createXColumnModel();
    }
    
    public Object getValueAt(final int n, final int n2) {
        if (this.getColumn(n2).isDummy()) {
            return null;
        }
        return super.getValueAt(n, n2);
    }
    
    public void setValueAt(final Object o, final int n, final int n2) {
        if (this.getColumn(n2).isDummy()) {
            return;
        }
        super.setValueAt(o, n, n2);
        if (this.isFitToSizeEnabled()) {
            this.fitToSizeAll();
        }
    }
    
    public void addDummyColumn(final Object headerValue) {
        final XTableColumn xTableColumn = new XTableColumn();
        if (headerValue != null) {
            xTableColumn.setHeaderValue(headerValue);
        }
        xTableColumn.setDummy(true);
        this.addColumn(xTableColumn);
    }
    
    public void addDummyColumnAt(final Object headerValue, final int n) {
        final XTableColumn xTableColumn = new XTableColumn();
        if (headerValue != null) {
            xTableColumn.setHeaderValue(headerValue);
        }
        xTableColumn.setDummy(true);
        this.addColumn(xTableColumn);
        this.moveColumn(this.getColumnCount() - 1, n);
    }
    
    public void addColumn(final TableColumn tableColumn) {
        final XTableColumn xTableColumn = new XTableColumn(tableColumn);
        if (this.getColumnModel() instanceof XTableColumnModel) {
            this.addColumn(xTableColumn);
        }
    }
    
    public void addColumn(final XTableColumn xTableColumn) {
        if (xTableColumn.getHeaderValue() == null) {
            xTableColumn.setHeaderValue(this.getModel().getColumnName(xTableColumn.getModelIndex()));
        }
        this.getXColumnModel().addColumn(xTableColumn);
    }
    
    public void addColumnAt(final int n, final TableColumn tableColumn) {
        this.addColumn(tableColumn);
        this.moveColumn(this.getColumnCount() - 1, n);
    }
    
    public void addColumnAt(final int n, final XTableColumn xTableColumn) {
        this.addColumn(xTableColumn);
        this.moveColumn(this.getColumnCount() - 1, n);
    }
    
    public void addColumnAtZerothIndex(final TableColumn tableColumn) {
        this.addColumn(tableColumn);
        this.moveColumn(this.getColumnCount() - 1, 0);
    }
    
    public void addColumnAtZerothIndex(final XTableColumn xTableColumn) {
        this.addColumn(xTableColumn);
        this.moveColumn(this.getColumnCount() - 1, 0);
    }
    
    public void addColumnAtAnchorIndex(final TableColumn tableColumn, final boolean b) {
        this.addColumn(tableColumn);
        int n;
        if (b) {
            n = this.getSelectionModel().getAnchorSelectionIndex() - 1;
        }
        else {
            n = this.getSelectionModel().getAnchorSelectionIndex() + 1;
        }
        this.moveColumn(this.getColumnCount() - 1, n);
    }
    
    public void removeColumnAt(final int n) {
        super.removeColumn(this.getColumn(n));
    }
    
    public void addColumnAtAnchorIndex(final XTableColumn xTableColumn, final boolean b) {
        this.addColumn(xTableColumn);
        int n;
        if (b) {
            n = this.getSelectionModel().getAnchorSelectionIndex() - 1;
        }
        else {
            n = this.getSelectionModel().getAnchorSelectionIndex() + 1;
        }
        this.moveColumn(this.getColumnCount() - 1, n);
    }
    
    public void hideColumn(final String s) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (this.getColumn(i).getHeaderValue().equals(s)) {
                this.hideColumn(i);
            }
        }
    }
    
    public void hideColumnByIdentifier(final Object o) {
        this.hideColumn(this.getXColumnModel().getColumnIndex(o));
    }
    
    public void hideColumn(final int n) {
        this.getXColumnModel().hideColumn(n);
    }
    
    public void hideColumn(final int n, final int n2) {
        this.getXColumnModel().hideColumn(n, n2);
    }
    
    public void showColumn(final int n) {
        this.getXColumnModel().showColumn(n);
    }
    
    public void showColumn(final String s) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (this.getColumn(i).getHeaderValue().equals(s)) {
                this.showColumn(i);
            }
        }
    }
    
    public void showColumnByIdentifier(final Object o) {
        this.showColumn(this.getXColumnModel().getColumnIndex(o));
    }
    
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public boolean isCellEditable(final int n, final int n2) {
        return this.isEditable() && super.isCellEditable(n, n2);
    }
    
    public String[] getHeaderNames() {
        final String[] array = new String[this.getColumnCount()];
        for (int i = 0; i < this.getColumnCount(); ++i) {
            array[i] = this.getColumn(i).getHeaderValue().toString();
        }
        return array;
    }
    
    public XTableColumn[] getVisibleColumns() {
        final XTableColumn[] array = new XTableColumn[this.getVisibleColumnCount()];
        int n = 0;
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (!this.getColumn(i).isHidden()) {
                array[n++] = this.getColumn(i);
            }
        }
        return array;
    }
    
    private int getVisibleColumnCount() {
        int n = 0;
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (!this.getColumn(i).isHidden()) {
                ++n;
            }
        }
        return n;
    }
    
    public String[] getVisibleHeaderNames() {
        final String[] array = new String[this.getVisibleColumnCount()];
        int n = 0;
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (!this.getColumn(i).isHidden()) {
                array[n++] = this.getColumn(i).getHeaderValue().toString();
            }
        }
        return array;
    }
    
    public XTableColumn getColumn(final int n) {
        return (XTableColumn)this.getColumnModel().getColumn(n);
    }
    
    public XTableColumnModel getXColumnModel() {
        return (XTableColumnModel)this.getColumnModel();
    }
    
    public XTableHeader getXTableHeader() {
        return (XTableHeader)this.getTableHeader();
    }
    
    public void setStandStill(final int n, final int n2, final boolean b) {
        for (int i = n; i <= n2; ++i) {
            this.getXTableHeader().setStandStill(i, b);
        }
    }
    
    public void setStandStill(final int n, final boolean b) {
        this.setStandStill(n, n, b);
    }
    
    public boolean isStandStill(final int n) {
        return this.getXTableHeader().isStandStill(n);
    }
    
    public void setFitToSizeEnabled(final boolean fitToSizeEnabled) {
        if (fitToSizeEnabled == this.isFitToSizeEnabled()) {
            return;
        }
        final boolean fitToSizeEnabled2 = this.fitToSizeEnabled;
        this.fitToSizeEnabled = fitToSizeEnabled;
        if (!this.isFitToSizeEnabled()) {
            this.setNormalSizeAll();
        }
        else {
            this.fitToSizeAll();
        }
        this.firePropertyChange("fitToSizeEnabled", fitToSizeEnabled2, this.fitToSizeEnabled);
        this.updateUI();
    }
    
    public boolean isFitToSizeEnabled() {
        return this.fitToSizeEnabled;
    }
    
    private void fitToSizeAll() {
        if (this.isFitToSizeEnabled()) {
            this.resizeMode = this.getAutoResizeMode();
            for (int i = 0; i < this.getColumnCount(); ++i) {
                this.fitToSize(i);
            }
        }
    }
    
    private void fitToSize(final int n) {
        if (this.isFitToSizeEnabled() && this.graph != null) {
            this.setAutoResizeMode(this.resizeMode);
            this.setAutoResizeMode(0);
            final FontMetrics fontMetrics = this.graph.getFontMetrics();
            int n2 = 0;
            for (int i = 0; i < this.getRowCount(); ++i) {
                if (this.getValueAt(i, n) != null) {
                    final int stringWidth = fontMetrics.stringWidth(this.getValueAt(i, n).toString());
                    if (stringWidth > n2) {
                        n2 = stringWidth + 20;
                    }
                }
            }
            int stringWidth2 = fontMetrics.stringWidth((String)this.getColumn(n).getHeaderValue());
            if (this.getColumn(n).getViewClickCount() > 0) {
                stringWidth2 += 40;
            }
            if (this.getColumn(n).getModelClickCount() > 0) {
                stringWidth2 += 40;
            }
            if (n2 < stringWidth2) {
                n2 = stringWidth2;
            }
            this.getXColumnModel().fitToSize(n, n2);
        }
    }
    
    public void setNormalSizeAll() {
        this.setAutoResizeMode(4);
        for (int i = 0; i < this.getColumnCount(); ++i) {
            this.setNormalSize(i);
        }
    }
    
    private void setNormalSize(final int normalSize) {
        this.getXColumnModel().setNormalSize(normalSize);
    }
    
    public RowStripingRenderer getRowStripingRenderer() {
        return this.rowStripRenderer;
    }
    
    public void setStripColor1(final Color stripColor1) {
        this.rowStripRenderer.setStripColor1(stripColor1);
    }
    
    public Color getStripColor1() {
        return this.rowStripRenderer.getStripColor1();
    }
    
    public void setStripColor2(final Color stripColor2) {
        this.rowStripRenderer.setStripColor2(stripColor2);
    }
    
    public Color getStripColor2() {
        return this.rowStripRenderer.getStripColor2();
    }
    
    public void setRowStripsEnabled(final boolean rowStripsEnabled) {
        if (rowStripsEnabled == this.isRowStripsEnabled()) {
            return;
        }
        final boolean rowStripsEnabled2 = this.rowStripsEnabled;
        this.rowStripsEnabled = rowStripsEnabled;
        this.showStrips();
        if (rowStripsEnabled) {
            this.setShowGrid(false);
            this.oldCellSpacing = this.getIntercellSpacing();
            this.setIntercellSpacing(new Dimension(0, 0));
        }
        else {
            this.setShowGrid(true);
            this.setIntercellSpacing(this.oldCellSpacing);
        }
        this.updateUI();
        this.firePropertyChange("rowStripsEnabled", rowStripsEnabled2, this.rowStripsEnabled);
    }
    
    void showStrips() {
        if (this.isRowStripsEnabled()) {
            for (int i = 0; i < this.getColumnCount(); ++i) {
                if (this.getColumn(i).getCellRenderer() == null) {
                    this.getColumn(i).setCellRenderer(this.rowStripRenderer);
                }
            }
        }
        else {
            for (int j = 0; j < this.getColumnCount(); ++j) {
                if (this.getColumn(j).getCellRenderer() == this.rowStripRenderer) {
                    this.getColumn(j).setCellRenderer(null);
                }
            }
        }
    }
    
    public boolean isRowStripsEnabled() {
        return this.rowStripsEnabled;
    }
    
    public HashMap getRowProperties(final int n) {
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < this.getColumnCount(); ++i) {
            hashMap.put(this.getColumn(i).getIdentifier(), this.getValueAt(n, i));
        }
        return hashMap;
    }
    
    public Object[] getSelectedValues() {
        final int[] selectedRows = super.getSelectedRows();
        final int[] selectedColumns = super.getSelectedColumns();
        final Object[] array = null;
        if (this.getColumnSelectionAllowed() && !this.getRowSelectionAllowed()) {
            int n = 0;
            final Object[] array2 = new Object[selectedColumns.length * this.getRowCount()];
            for (int i = 0; i < selectedColumns.length; ++i) {
                for (int j = 0; j < this.getRowCount(); ++j) {
                    array2[n++] = this.getValueAt(j, selectedColumns[i]);
                }
            }
            return array2;
        }
        if (!this.getColumnSelectionAllowed() && this.getRowSelectionAllowed()) {
            int n2 = 0;
            final Object[] array3 = new Object[selectedRows.length * this.getColumnCount()];
            for (int k = 0; k < selectedRows.length; ++k) {
                for (int l = 0; l < this.getColumnCount(); ++l) {
                    array3[n2++] = this.getValueAt(selectedRows[k], l);
                }
            }
            return array3;
        }
        if (this.getCellSelectionEnabled()) {
            int n3 = 0;
            final Object[] array4 = new Object[selectedRows.length * selectedColumns.length];
            for (int n4 = 0; n4 < selectedRows.length; ++n4) {
                for (int n5 = 0; n5 < selectedColumns.length; ++n5) {
                    array4[n3++] = this.getValueAt(selectedRows[n4], selectedColumns[n5]);
                }
            }
            return array4;
        }
        return array;
    }
    
    public void setViewSortEnabled(final boolean b) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            this.enableViewSortOnColumn(i, b);
        }
        this.getXTableHeader().repaint();
    }
    
    public void enableViewSortOnColumn(final int n, final boolean viewSortEnabled) {
        this.getColumn(n).setViewSortEnabled(viewSortEnabled);
    }
    
    public boolean isViewSortEnabled() {
        boolean b = true;
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (!this.getColumn(i).isViewSortEnabled()) {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public void setModelSortEnabled(final boolean b) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            this.enableModelSortOnColumn(i, b);
        }
        this.getXTableHeader().repaint();
    }
    
    public void enableModelSortOnColumn(final int n, final boolean modelSortEnabled) {
        this.getColumn(n).setModelSortEnabled(modelSortEnabled);
    }
    
    public boolean isModelSortEnabled() {
        boolean b = true;
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (!this.getColumn(i).isModelSortEnabled()) {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public void setSortEnabled(final boolean b) {
        this.setViewSortEnabled(b);
        this.setModelSortEnabled(b);
    }
    
    public boolean isSortEnabled() {
        return this.isModelSortEnabled() && this.isViewSortEnabled();
    }
    
    public void setCellRenderer(final int n, final TableCellRenderer cellRenderer) {
        this.getColumn(n).setCellRenderer(cellRenderer);
    }
    
    public void addColumnViewListener(final ColumnViewListener columnViewListener) {
        this.getXColumnModel().addColumnViewListener(columnViewListener);
    }
    
    public void removeColumnViewListener(final ColumnViewListener columnViewListener) {
        this.getXColumnModel().removeColumnViewListener(columnViewListener);
    }
    
    public void addColumnSortedListener(final ColumnSortedListener columnSortedListener) {
        this.listenerList.add(ColumnSortedListener.class, columnSortedListener);
    }
    
    public void removeColumnSortedListener(final ColumnSortedListener columnSortedListener) {
        this.listenerList.remove(ColumnSortedListener.class, columnSortedListener);
    }
    
    void fireBeforeColumnSortedEvent(final ColumnSortedEvent columnSortedEvent) {
        final EventListener[] listeners = this.listenerList.getListeners((Class<EventListener>)ColumnSortedListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((ColumnSortedListener)listeners[i]).beforeColumnSorted(columnSortedEvent);
        }
    }
    
    void fireAfterColumnSortedEvent(final ColumnSortedEvent columnSortedEvent) {
        final EventListener[] listeners = this.listenerList.getListeners((Class<EventListener>)ColumnSortedListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((ColumnSortedListener)listeners[i]).afterColumnSorted(columnSortedEvent);
        }
    }
    
    public void afterColumnSorted(final ColumnSortedEvent columnSortedEvent) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                XTable.this.updateUI();
                XTable.this.setCursor(new Cursor(0));
                XTable.this.getXTableHeader().setCursor(new Cursor(0));
                XTable.this.getXTableHeader().repaint();
            }
        });
    }
    
    public void beforeColumnSorted(final ColumnSortedEvent columnSortedEvent) {
        this.prevTableCursor = this.getCursor();
        this.prevHeaderCursor = this.getXTableHeader().getCursor();
        this.setCursor(new Cursor(3));
        this.getXTableHeader().setCursor(new Cursor(3));
    }
    
    public XTableModel getXTableModel() {
        return (XTableModel)this.getModel();
    }
    
    private void setViewSortNumbering(final SortColumn[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                this.getColumn(array[i].getColumnIndex()).setViewSortOrder(i + 1);
            }
        }
    }
    
    private void setModelSortNumbering(final SortColumn[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                this.getColumn(array[i].getColumnIndex()).setModelSortOrder(i + 1);
            }
        }
    }
    
    private void restoreDefaultValuesForAllColumns() {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            final XTableColumn column = this.getColumn(i);
            column.setViewClickCount(0);
            column.setViewSortOrder(-1);
            column.setModelClickCount(0);
            column.setModelSortOrder(-1);
        }
    }
    
    class HeaderClickHandler extends MouseAdapter
    {
        private int colClicked;
        private int dist;
        private int xlen;
        private Rectangle rec;
        private int xpos;
        private int ypos;
        private int lastVisibleCol;
        
        HeaderClickHandler() {
            this.colClicked = 0;
        }
        
        public void mousePressed(final MouseEvent mouseEvent) {
            this.colClicked = XTable.this.columnAtPoint(mouseEvent.getPoint());
            for (int i = XTable.this.getColumnCount() - 1; i >= 0; --i) {
                if (!XTable.this.getColumn(i).isHidden()) {
                    this.lastVisibleCol = i;
                    break;
                }
            }
            if (this.colClicked < 0) {
                return;
            }
            this.rec = XTable.this.getCellRect(-1, this.colClicked, true);
            this.dist = (int)this.rec.getWidth() - XTable.this.getRightCornerHeaderComponent().getWidth();
            this.xlen = mouseEvent.getX() - (int)this.rec.getX();
            this.xpos = mouseEvent.getX();
            this.ypos = mouseEvent.getY();
            if (XTable.this.getRightCornerHeaderComponent().isVisible() && this.colClicked == this.lastVisibleCol && this.xlen >= this.dist && mouseEvent.getButton() == 1) {
                XTable.this.hr.setPushed(true);
                XTable.this.getXTableHeader().repaint();
            }
        }
        
        private SortColumn[] getUpdatedSortArray(SortColumn[] array, final int n, final int n2) {
            XTable.this.getColumn(n);
            if (array == null) {
                array = new SortColumn[0];
            }
            boolean b = false;
            for (int i = 0; i < array.length; ++i) {
                if (array[i].getColumnIndex() == n) {
                    switch (n2) {
                        case 0: {
                            final SortColumn[] array2 = new SortColumn[array.length - 1];
                            int n3 = 0;
                            for (int j = 0; j < array.length; ++j) {
                                if (j != i) {
                                    array2[n3++] = array[j];
                                }
                            }
                            array = array2;
                            break;
                        }
                        case 1: {
                            array[i] = new SortColumn(n, true);
                            break;
                        }
                        case 2: {
                            array[i] = new SortColumn(n, false);
                            break;
                        }
                    }
                    b = true;
                    break;
                }
            }
            if (!b) {
                final SortColumn[] array3 = new SortColumn[array.length + 1];
                int k;
                for (k = 0; k < array.length; ++k) {
                    array3[k] = array[k];
                }
                switch (n2) {
                    case 1: {
                        array3[k] = new SortColumn(n, true);
                        break;
                    }
                    case 2: {
                        array3[k] = new SortColumn(n, false);
                        break;
                    }
                }
                array = array3;
            }
            return array;
        }
        
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (this.colClicked < 0) {
                return;
            }
            if (mouseEvent.getButton() == 1 && XTable.this.getRightCornerHeaderComponent().isVisible() && this.colClicked == this.lastVisibleCol && this.xlen >= this.dist) {
                XTable.this.hr.setPushed(false);
                XTable.this.getXTableHeader().repaint();
                XTable.this.getRightCornerHeaderComponent().fireRightCornerHeaderComponentActionEvent(XTable.this.getXTableHeader(), this.xpos, this.ypos);
            }
        }
        
        public void mouseClicked(final MouseEvent mouseEvent) {
            boolean b = false;
            this.colClicked = XTable.this.columnAtPoint(mouseEvent.getPoint());
            if (XTable.this.getRightCornerHeaderComponent().isVisible() && this.colClicked == this.lastVisibleCol && this.xlen >= this.dist) {
                return;
            }
            if (mouseEvent.getButton() == 1) {
                final XTableColumn column = XTable.this.getColumn(this.colClicked);
                if (!column.isViewSortEnabled() && !column.isModelSortEnabled()) {
                    return;
                }
                if (!column.isViewSortEnabled() && column.isModelSortEnabled()) {
                    b = true;
                }
                if (column.isViewSortEnabled() && !column.isModelSortEnabled() && mouseEvent.isControlDown()) {
                    return;
                }
                if (column.isDummy()) {
                    return;
                }
                if (!mouseEvent.isShiftDown()) {
                    if (XTable.this.multiple_sort) {
                        XTable.this.restoreDefaultValuesForAllColumns();
                        XTable.this.multiple_sort = false;
                    }
                    XTable.this.single_sort = true;
                    if (XTable.this.prevSortedCol != null && XTable.this.prevSortedCol != column) {
                        XTable.this.prevSortedCol.setViewClickCount(0);
                        XTable.this.prevSortedCol.setModelClickCount(0);
                    }
                    if (mouseEvent.isControlDown() || b) {
                        if (column.isModelSortEnabled()) {
                            column.setViewClickCount(0);
                            column.incrementModelClickCount();
                        }
                    }
                    else if (column.isViewSortEnabled()) {
                        column.incrementViewClickCount();
                    }
                    switch (column.getModelClickCount()) {
                        case 0: {
                            new sorterThread(null, 1).start();
                            break;
                        }
                        case 1: {
                            new sorterThread(new SortColumn[] { new SortColumn(this.colClicked, true) }, 1).start();
                            break;
                        }
                        case 2: {
                            new sorterThread(new SortColumn[] { new SortColumn(this.colClicked, false) }, 1).start();
                            break;
                        }
                    }
                    switch (column.getViewClickCount()) {
                        case 0: {
                            new sorterThread(null, 0).start();
                            break;
                        }
                        case 1: {
                            new sorterThread(new SortColumn[] { new SortColumn(this.colClicked, true) }, 0).start();
                            break;
                        }
                        case 2: {
                            new sorterThread(new SortColumn[] { new SortColumn(this.colClicked, false) }, 0).start();
                            break;
                        }
                    }
                    XTable.this.prevSortedCol = column;
                }
                else {
                    if (XTable.this.single_sort) {
                        XTable.this.single_sort = false;
                    }
                    XTable.this.multiple_sort = true;
                    SortColumn[] array = XTable.this.convertModelIndexesToViewIndex(((XTableModel)XTable.this.getModel()).getViewSortedColumns());
                    SortColumn[] array2 = XTable.this.convertModelIndexesToViewIndex(((XTableModel)XTable.this.getModel()).getModelSortedColumns());
                    if (mouseEvent.isControlDown() || b) {
                        if (column.isModelSortEnabled()) {
                            if (column.getViewClickCount() != 0) {
                                column.setViewClickCount(0);
                                array = this.getUpdatedSortArray(array, this.colClicked, column.getViewClickCount());
                            }
                            column.incrementModelClickCount();
                            array2 = this.getUpdatedSortArray(array2, this.colClicked, column.getModelClickCount());
                        }
                    }
                    else if (column.isViewSortEnabled()) {
                        column.incrementViewClickCount();
                        array = this.getUpdatedSortArray(array, this.colClicked, column.getViewClickCount());
                    }
                    if (array != null && XTable.this.multiple_sort) {
                        XTable.this.setViewSortNumbering(array);
                    }
                    if (array2 != null && XTable.this.multiple_sort) {
                        XTable.this.setModelSortNumbering(array2);
                    }
                    if (column.isModelSortEnabled()) {
                        new sorterThread(array2, 1).start();
                    }
                    if (column.isViewSortEnabled()) {
                        new sorterThread(array, 0).start();
                    }
                }
            }
        }
    }
    
    class sorterThread extends Thread
    {
        private SortColumn[] cols;
        private int type;
        
        sorterThread(final SortColumn[] cols, final int type) {
            super("SorterThread");
            this.cols = cols;
            this.type = type;
        }
        
        public void run() {
            try {
                XTable.this.sort(this.type, this.cols);
            }
            catch (final ModelException ex) {
                new Error(ex.getMessage());
            }
        }
    }
}
