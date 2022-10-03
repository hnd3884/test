package com.adventnet.beans.xtable;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.beans.Introspector;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.TableColumn;

public class XTableColumn extends TableColumn
{
    private int sortType;
    private boolean hidden;
    private boolean standStill;
    private boolean dummy;
    private boolean viewSortEnabled;
    private boolean modelSortEnabled;
    private SwingPropertyChangeSupport support;
    private int oldMinWidth;
    private int oldMaxWidth;
    private int oldWidth;
    private int viewClickCount;
    private int modelClickCount;
    private int viewSortOrder;
    private int modelSortOrder;
    private boolean allowClearSort;
    private boolean mandatoryColumn;
    
    public XTableColumn() {
        this(0);
    }
    
    public XTableColumn(final int n) {
        this(n, 75, null, null);
    }
    
    public XTableColumn(final int n, final int n2) {
        this(n, n2, null, null);
    }
    
    public XTableColumn(final int n, final int n2, final TableCellRenderer tableCellRenderer, final TableCellEditor tableCellEditor) {
        super(n, n2, tableCellRenderer, tableCellEditor);
        this.viewSortOrder = -1;
        this.modelSortOrder = -1;
        this.allowClearSort = false;
        this.mandatoryColumn = false;
        this.support = new SwingPropertyChangeSupport(this);
    }
    
    public XTableColumn(final TableColumn tableColumn) {
        this.viewSortOrder = -1;
        this.modelSortOrder = -1;
        this.allowClearSort = false;
        this.mandatoryColumn = false;
        try {
            final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(tableColumn.getClass()).getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; ++i) {
                final Method readMethod = propertyDescriptors[i].getReadMethod();
                if (readMethod != null) {
                    final Object invoke = readMethod.invoke(tableColumn, (Object[])null);
                    final Method writeMethod = propertyDescriptors[i].getWriteMethod();
                    if (writeMethod != null) {
                        writeMethod.invoke(this, invoke);
                    }
                }
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    void show() {
        if (this.isHidden()) {
            this.setMinWidth(this.oldMinWidth);
            this.setMaxWidth(this.oldMaxWidth);
            this.setWidth(this.oldWidth);
            this.setPreferredWidth(this.oldWidth);
            this.setHidden(false);
        }
    }
    
    void hide() {
        if (!this.isHidden()) {
            this.oldMinWidth = this.getMinWidth();
            this.oldMaxWidth = this.getMaxWidth();
            this.oldWidth = this.getWidth();
            this.setMinWidth(0);
            this.setMaxWidth(0);
            this.setWidth(0);
            this.setHidden(true);
        }
    }
    
    void setHidden(final boolean hidden) {
        final boolean hidden2 = this.hidden;
        this.hidden = hidden;
        this.support.firePropertyChange("hidden", hidden2, this.hidden);
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public void setStandStill(final boolean standStill) {
        final boolean standStill2 = this.standStill;
        this.standStill = standStill;
        this.support.firePropertyChange("standStill", standStill2, this.standStill);
    }
    
    public boolean isStandStill() {
        return this.standStill;
    }
    
    void fitToSize(final int preferredWidth) {
        this.setPreferredWidth(preferredWidth);
    }
    
    void setNormalSize() {
        this.setPreferredWidth(75);
    }
    
    public void setSortEnabled(final boolean b) {
        this.setViewSortEnabled(b);
        this.setModelSortEnabled(b);
    }
    
    public void setViewSortEnabled(final boolean viewSortEnabled) {
        this.viewSortEnabled = viewSortEnabled;
    }
    
    public boolean isViewSortEnabled() {
        return this.viewSortEnabled;
    }
    
    public void setModelSortEnabled(final boolean modelSortEnabled) {
        this.modelSortEnabled = modelSortEnabled;
    }
    
    public boolean isModelSortEnabled() {
        return this.modelSortEnabled;
    }
    
    public boolean isSortEnabled() {
        return this.modelSortEnabled && this.viewSortEnabled;
    }
    
    public void setSortType(final int sortType) {
        final int sortType2 = this.sortType;
        this.sortType = sortType;
        this.support.firePropertyChange("standStill", sortType2, this.sortType);
    }
    
    public int getSortType() {
        return this.sortType;
    }
    
    public void setDummy(final boolean dummy) {
        final boolean dummy2 = this.dummy;
        this.dummy = dummy;
        this.support.firePropertyChange("dummy", dummy2, this.dummy);
    }
    
    public boolean isDummy() {
        return this.dummy;
    }
    
    public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        super.addPropertyChangeListener(propertyChangeListener);
        if (this.support == null) {
            this.support = new SwingPropertyChangeSupport(this);
        }
        this.support.addPropertyChangeListener(propertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        super.removePropertyChangeListener(propertyChangeListener);
        if (this.support != null) {
            this.support.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        final PropertyChangeListener[] propertyChangeListeners = super.getPropertyChangeListeners();
        PropertyChangeListener[] propertyChangeListeners2;
        if (this.support == null) {
            propertyChangeListeners2 = new PropertyChangeListener[0];
        }
        else {
            propertyChangeListeners2 = this.support.getPropertyChangeListeners();
        }
        final PropertyChangeListener[] array = new PropertyChangeListener[propertyChangeListeners.length + propertyChangeListeners2.length];
        int i;
        for (i = 0; i < propertyChangeListeners.length; ++i) {
            array[i] = propertyChangeListeners[i];
        }
        for (int j = 0; j < propertyChangeListeners2.length; ++j) {
            array[i++] = propertyChangeListeners2[i];
        }
        return array;
    }
    
    void incrementViewClickCount() {
        this.viewClickCount = ++this.viewClickCount % 3;
        if (!this.allowClearSort && this.viewClickCount == 0) {
            this.viewClickCount = 1;
        }
    }
    
    int getViewClickCount() {
        return this.viewClickCount;
    }
    
    void setViewClickCount(final int viewClickCount) {
        this.viewClickCount = viewClickCount;
    }
    
    void incrementModelClickCount() {
        this.modelClickCount = ++this.modelClickCount % 3;
        if (!this.allowClearSort && this.modelClickCount == 0) {
            this.modelClickCount = 1;
        }
    }
    
    int getModelClickCount() {
        return this.modelClickCount;
    }
    
    void setModelClickCount(final int modelClickCount) {
        this.modelClickCount = modelClickCount;
    }
    
    void setViewSortOrder(final int viewSortOrder) {
        this.viewSortOrder = viewSortOrder;
    }
    
    int getViewSortOrder() {
        return this.viewSortOrder;
    }
    
    void setModelSortOrder(final int modelSortOrder) {
        this.modelSortOrder = modelSortOrder;
    }
    
    int getModelSortOrder() {
        return this.modelSortOrder;
    }
    
    public void allowToClearSort(final boolean allowClearSort) {
        this.allowClearSort = allowClearSort;
    }
    
    public boolean isClearSortAllowed() {
        return this.allowClearSort;
    }
    
    public void setMandatoryColumn(final boolean mandatoryColumn) {
        this.mandatoryColumn = mandatoryColumn;
    }
    
    public boolean isMandatoryColumn() {
        return this.mandatoryColumn;
    }
}
