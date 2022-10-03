package com.adventnet.beans.xtable;

import java.util.EventListener;
import com.adventnet.beans.xtable.events.ColumnViewListener;
import java.beans.PropertyChangeListener;
import com.adventnet.beans.xtable.events.ColumnViewEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.DefaultTableColumnModel;

public class XTableColumnModel extends DefaultTableColumnModel
{
    private boolean addColumnEnabled;
    private String ADD_COLUMN_ENABLED_PROPERTY;
    private boolean removeColumnEnabled;
    private String REMOVE_COLUMN_ENABLED_PROPERTY;
    private boolean moveColumnEnabled;
    private String MOVE_COLUMN_ENABLED_PROPERTY;
    private boolean hideColumnEnabled;
    private String HIDE_COLUMN_ENABLED_PROPERTY;
    protected SwingPropertyChangeSupport support;
    protected XHeaderRenderer headerRenderer;
    
    public XTableColumnModel() {
        this.addColumnEnabled = true;
        this.ADD_COLUMN_ENABLED_PROPERTY = "addColumnEnabled";
        this.removeColumnEnabled = true;
        this.REMOVE_COLUMN_ENABLED_PROPERTY = "removeColumnEnabled";
        this.moveColumnEnabled = true;
        this.MOVE_COLUMN_ENABLED_PROPERTY = "moveColumnEnabled";
        this.hideColumnEnabled = true;
        this.HIDE_COLUMN_ENABLED_PROPERTY = "hideColumnEnabled";
        this.support = new SwingPropertyChangeSupport(this);
    }
    
    public void setHeaderRenderer(final XHeaderRenderer headerRenderer) {
        this.headerRenderer = headerRenderer;
    }
    
    public void addColumn(final TableColumn tableColumn) {
        if (this.isAddColumnEnabled()) {
            this.addColumn(new XTableColumn(tableColumn));
        }
    }
    
    public void addColumn(final XTableColumn xTableColumn) {
        if (this.isAddColumnEnabled()) {
            xTableColumn.setHeaderRenderer(this.headerRenderer);
            super.addColumn(xTableColumn);
        }
    }
    
    public void setAddColumnEnabled(final boolean addColumnEnabled) {
        final boolean addColumnEnabled2 = this.addColumnEnabled;
        this.addColumnEnabled = addColumnEnabled;
        this.support.firePropertyChange(this.ADD_COLUMN_ENABLED_PROPERTY, addColumnEnabled2, this.addColumnEnabled);
    }
    
    public boolean isAddColumnEnabled() {
        return this.addColumnEnabled;
    }
    
    public void removeColumn(final TableColumn tableColumn) {
        if (this.isRemoveColumnEnabled()) {
            super.removeColumn(tableColumn);
        }
    }
    
    public void setRemoveColumnEnabled(final boolean removeColumnEnabled) {
        final boolean removeColumnEnabled2 = this.removeColumnEnabled;
        this.removeColumnEnabled = removeColumnEnabled;
        this.support.firePropertyChange(this.REMOVE_COLUMN_ENABLED_PROPERTY, removeColumnEnabled2, this.removeColumnEnabled);
    }
    
    public boolean isRemoveColumnEnabled() {
        return this.removeColumnEnabled;
    }
    
    public void moveColumn(final int n, final int n2) {
        if (this.isMoveColumnEnabled()) {
            super.moveColumn(n, n2);
        }
    }
    
    public void setMoveColumnEnabled(final boolean moveColumnEnabled) {
        final boolean moveColumnEnabled2 = this.moveColumnEnabled;
        this.moveColumnEnabled = moveColumnEnabled;
        this.support.firePropertyChange(this.MOVE_COLUMN_ENABLED_PROPERTY, moveColumnEnabled2, this.moveColumnEnabled);
    }
    
    public boolean isMoveColumnEnabled() {
        return this.moveColumnEnabled;
    }
    
    public void setHideColumnEnabled(final boolean hideColumnEnabled) {
        final boolean hideColumnEnabled2 = this.hideColumnEnabled;
        this.hideColumnEnabled = hideColumnEnabled;
        this.support.firePropertyChange(this.HIDE_COLUMN_ENABLED_PROPERTY, hideColumnEnabled2, this.hideColumnEnabled);
    }
    
    public boolean isHideColumnEnabled() {
        return this.hideColumnEnabled;
    }
    
    public void hideColumn(final int n, final int n2) {
        if (this.isHideColumnEnabled()) {
            for (int i = n; i <= n2; ++i) {
                ((XTableColumn)this.getColumn(i)).hide();
            }
            this.fireColumnHiddenEvent(new ColumnViewEvent(this, n, n2));
        }
    }
    
    public void hideColumn(final int n) {
        this.hideColumn(n, n);
    }
    
    public void showColumn(final int n, final int n2) {
        for (int i = n; i <= n2; ++i) {
            ((XTableColumn)this.getColumn(i)).show();
        }
        this.fireColumnShownEvent(new ColumnViewEvent(this, n, n2));
    }
    
    public void showColumn(final int n) {
        this.showColumn(n, n);
    }
    
    public void fitToSize(final int n, final int n2) {
        ((XTableColumn)this.getColumn(n)).fitToSize(n2);
    }
    
    public void setNormalSize(final int n) {
        ((XTableColumn)this.getColumn(n)).setNormalSize();
    }
    
    public XHeaderRenderer getXHeaderRenderer() {
        return this.headerRenderer;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.support.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.support.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        this.support.removePropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.support.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    public void addColumnViewListener(final ColumnViewListener columnViewListener) {
        this.listenerList.add(ColumnViewListener.class, columnViewListener);
    }
    
    public void removeColumnViewListener(final ColumnViewListener columnViewListener) {
        this.listenerList.remove(ColumnViewListener.class, columnViewListener);
    }
    
    void fireColumnShownEvent(final ColumnViewEvent columnViewEvent) {
        final EventListener[] listeners = this.listenerList.getListeners((Class<EventListener>)ColumnViewListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((ColumnViewListener)listeners[i]).columnShown(columnViewEvent);
        }
    }
    
    void fireColumnHiddenEvent(final ColumnViewEvent columnViewEvent) {
        final EventListener[] listeners = this.listenerList.getListeners((Class<EventListener>)ColumnViewListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((ColumnViewListener)listeners[i]).columnHidden(columnViewEvent);
        }
    }
}
