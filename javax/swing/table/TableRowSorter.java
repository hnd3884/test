package javax.swing.table;

import java.text.Collator;
import java.util.Comparator;
import javax.swing.DefaultRowSorter;

public class TableRowSorter<M extends TableModel> extends DefaultRowSorter<M, Integer>
{
    private static final Comparator COMPARABLE_COMPARATOR;
    private M tableModel;
    private TableStringConverter stringConverter;
    
    public TableRowSorter() {
        this(null);
    }
    
    public TableRowSorter(final M model) {
        this.setModel(model);
    }
    
    public void setModel(final M tableModel) {
        this.tableModel = tableModel;
        this.setModelWrapper(new TableRowSorterModelWrapper());
    }
    
    public void setStringConverter(final TableStringConverter stringConverter) {
        this.stringConverter = stringConverter;
    }
    
    public TableStringConverter getStringConverter() {
        return this.stringConverter;
    }
    
    @Override
    public Comparator<?> getComparator(final int n) {
        final Comparator<?> comparator = super.getComparator(n);
        if (comparator != null) {
            return comparator;
        }
        final Class<?> columnClass = this.getModel().getColumnClass(n);
        if (columnClass == String.class) {
            return Collator.getInstance();
        }
        if (Comparable.class.isAssignableFrom(columnClass)) {
            return TableRowSorter.COMPARABLE_COMPARATOR;
        }
        return Collator.getInstance();
    }
    
    @Override
    protected boolean useToString(final int n) {
        if (super.getComparator(n) != null) {
            return false;
        }
        final Class<?> columnClass = this.getModel().getColumnClass(n);
        return columnClass != String.class && !Comparable.class.isAssignableFrom(columnClass);
    }
    
    static {
        COMPARABLE_COMPARATOR = new ComparableComparator();
    }
    
    private class TableRowSorterModelWrapper extends ModelWrapper<M, Integer>
    {
        @Override
        public M getModel() {
            return TableRowSorter.this.tableModel;
        }
        
        @Override
        public int getColumnCount() {
            return (TableRowSorter.this.tableModel == null) ? 0 : TableRowSorter.this.tableModel.getColumnCount();
        }
        
        @Override
        public int getRowCount() {
            return (TableRowSorter.this.tableModel == null) ? 0 : TableRowSorter.this.tableModel.getRowCount();
        }
        
        @Override
        public Object getValueAt(final int n, final int n2) {
            return TableRowSorter.this.tableModel.getValueAt(n, n2);
        }
        
        @Override
        public String getStringValueAt(final int n, final int n2) {
            final TableStringConverter stringConverter = TableRowSorter.this.getStringConverter();
            if (stringConverter != null) {
                final String string = stringConverter.toString(TableRowSorter.this.tableModel, n, n2);
                if (string != null) {
                    return string;
                }
                return "";
            }
            else {
                final Object value = this.getValueAt(n, n2);
                if (value == null) {
                    return "";
                }
                final String string2 = value.toString();
                if (string2 == null) {
                    return "";
                }
                return string2;
            }
        }
        
        @Override
        public Integer getIdentifier(final int n) {
            return n;
        }
    }
    
    private static class ComparableComparator implements Comparator
    {
        @Override
        public int compare(final Object o, final Object o2) {
            return ((Comparable)o).compareTo(o2);
        }
    }
}
