package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

abstract class AbstractTable<R, C, V> implements Table<R, C, V>
{
    private transient Set<Cell<R, C, V>> cellSet;
    
    @Override
    public boolean containsRow(final Object rowKey) {
        return Maps.safeContainsKey(this.rowMap(), rowKey);
    }
    
    @Override
    public boolean containsColumn(final Object columnKey) {
        return Maps.safeContainsKey(this.columnMap(), columnKey);
    }
    
    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }
    
    @Override
    public Set<C> columnKeySet() {
        return this.columnMap().keySet();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        for (final Map<C, V> row : this.rowMap().values()) {
            if (row.containsValue(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean contains(final Object rowKey, final Object columnKey) {
        final Map<C, V> row = Maps.safeGet(this.rowMap(), rowKey);
        return row != null && Maps.safeContainsKey(row, columnKey);
    }
    
    @Override
    public V get(final Object rowKey, final Object columnKey) {
        final Map<C, V> row = Maps.safeGet(this.rowMap(), rowKey);
        return (row == null) ? null : Maps.safeGet(row, columnKey);
    }
    
    @Override
    public void clear() {
        Iterators.clear(this.cellSet().iterator());
    }
    
    @Override
    public V remove(final Object rowKey, final Object columnKey) {
        final Map<C, V> row = Maps.safeGet(this.rowMap(), rowKey);
        return (row == null) ? null : Maps.safeRemove(row, columnKey);
    }
    
    @Override
    public V put(final R rowKey, final C columnKey, final V value) {
        return this.row(rowKey).put(columnKey, value);
    }
    
    @Override
    public void putAll(final Table<? extends R, ? extends C, ? extends V> table) {
        for (final Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
            this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }
    
    @Override
    public Set<Cell<R, C, V>> cellSet() {
        final Set<Cell<R, C, V>> result = this.cellSet;
        return (result == null) ? (this.cellSet = this.createCellSet()) : result;
    }
    
    private Set<Cell<R, C, V>> createCellSet() {
        return new CellSet();
    }
    
    abstract Iterator<Cell<R, C, V>> cellIterator();
    
    @Override
    public boolean equals(final Object obj) {
        return Tables.equalsImpl(this, obj);
    }
    
    @Override
    public int hashCode() {
        return this.cellSet().hashCode();
    }
    
    @Override
    public String toString() {
        return this.rowMap().toString();
    }
    
    private class CellSet extends AbstractSet<Cell<R, C, V>>
    {
        @Override
        public boolean contains(final Object o) {
            if (o instanceof Cell) {
                final Cell<?, ?, ?> cell = (Cell<?, ?, ?>)o;
                final Map<C, V> row = Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
                return row != null && Collections2.safeContains(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue()));
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (o instanceof Cell) {
                final Cell<?, ?, ?> cell = (Cell<?, ?, ?>)o;
                final Map<C, V> row = Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
                return row != null && Collections2.safeRemove(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue()));
            }
            return false;
        }
        
        @Override
        public void clear() {
            AbstractTable.this.clear();
        }
        
        @Override
        public Iterator<Cell<R, C, V>> iterator() {
            return AbstractTable.this.cellIterator();
        }
        
        @Override
        public int size() {
            return AbstractTable.this.size();
        }
    }
}
