package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.Objects;
import java.io.Serializable;

public final class Tables
{
    private Tables() {
    }
    
    public static <R, C, V> Table.Cell<R, C, V> immutableCell(final R rowKey, final C columnKey, final V value) {
        return new ImmutableCell<R, C, V>(rowKey, columnKey, value);
    }
    
    private static <R, C, V> Table<C, R, V> transpose(final Table<R, C, V> table) {
        return (Table<C, R, V>)((table instanceof TransposeTable) ? ((TransposeTable)table).original : new TransposeTable<C, R, V>((Table<Object, Object, Object>)table));
    }
    
    static boolean equalsImpl(final Table<?, ?, ?> table, final Object obj) {
        if (obj == table) {
            return true;
        }
        if (obj instanceof Table) {
            final Table<?, ?, ?> that = (Table<?, ?, ?>)obj;
            return table.cellSet().equals(that.cellSet());
        }
        return false;
    }
    
    static final class ImmutableCell<R, C, V> extends AbstractCell<R, C, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final R rowKey;
        private final C columnKey;
        private final V value;
        
        ImmutableCell(final R rowKey, final C columnKey, final V value) {
            this.rowKey = rowKey;
            this.columnKey = columnKey;
            this.value = value;
        }
        
        @Override
        public R getRowKey() {
            return this.rowKey;
        }
        
        @Override
        public C getColumnKey() {
            return this.columnKey;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
    }
    
    abstract static class AbstractCell<R, C, V> implements Table.Cell<R, C, V>
    {
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Table.Cell) {
                final Table.Cell<?, ?, ?> other = (Table.Cell<?, ?, ?>)obj;
                return Objects.equals(this.getRowKey(), other.getRowKey()) && Objects.equals(this.getColumnKey(), other.getColumnKey()) && Objects.equals(this.getValue(), other.getValue());
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.getRowKey(), this.getColumnKey(), this.getValue());
        }
        
        @Override
        public String toString() {
            return "(" + this.getRowKey() + "," + this.getColumnKey() + ")=" + this.getValue();
        }
    }
    
    private static class TransposeTable<C, R, V> extends AbstractTable<C, R, V>
    {
        private static final Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>> TRANSPOSE_CELL;
        final Table<R, C, V> original;
        
        TransposeTable(final Table<R, C, V> original) {
            this.original = Preconditions.checkNotNull(original);
        }
        
        @Override
        public void clear() {
            this.original.clear();
        }
        
        @Override
        public Map<C, V> column(final R columnKey) {
            return this.original.row(columnKey);
        }
        
        @Override
        public Set<R> columnKeySet() {
            return this.original.rowKeySet();
        }
        
        @Override
        public Map<R, Map<C, V>> columnMap() {
            return this.original.rowMap();
        }
        
        @Override
        public boolean contains(final Object rowKey, final Object columnKey) {
            return this.original.contains(columnKey, rowKey);
        }
        
        @Override
        public boolean containsColumn(final Object columnKey) {
            return this.original.containsRow(columnKey);
        }
        
        @Override
        public boolean containsRow(final Object rowKey) {
            return this.original.containsColumn(rowKey);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return this.original.containsValue(value);
        }
        
        @Override
        public V get(final Object rowKey, final Object columnKey) {
            return this.original.get(columnKey, rowKey);
        }
        
        @Override
        public V put(final C rowKey, final R columnKey, final V value) {
            return this.original.put(columnKey, rowKey, value);
        }
        
        @Override
        public void putAll(final Table<? extends C, ? extends R, ? extends V> table) {
            this.original.putAll(transpose((Table<Object, Object, Object>)table));
        }
        
        @Override
        public V remove(final Object rowKey, final Object columnKey) {
            return this.original.remove(columnKey, rowKey);
        }
        
        @Override
        public Map<R, V> row(final C rowKey) {
            return this.original.column(rowKey);
        }
        
        @Override
        public Set<C> rowKeySet() {
            return this.original.columnKeySet();
        }
        
        @Override
        public Map<C, Map<R, V>> rowMap() {
            return this.original.columnMap();
        }
        
        @Override
        public int size() {
            return this.original.size();
        }
        
        @Override
        Iterator<Table.Cell<C, R, V>> cellIterator() {
            return Iterators.transform(this.original.cellSet().iterator(), (Function<? super Table.Cell<R, C, V>, ? extends Table.Cell<C, R, V>>)TransposeTable.TRANSPOSE_CELL);
        }
        
        static {
            TRANSPOSE_CELL = new Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>>() {
                @Override
                public Table.Cell<?, ?, ?> apply(final Table.Cell<?, ?, ?> cell) {
                    return Tables.immutableCell(cell.getColumnKey(), cell.getRowKey(), cell.getValue());
                }
            };
        }
    }
}
