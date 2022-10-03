package org.jfree.ui.about;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.jfree.ui.SortableTableModel;

public class SystemPropertiesTableModel extends SortableTableModel
{
    private List properties;
    private String nameColumnLabel;
    private String valueColumnLabel;
    
    public SystemPropertiesTableModel() {
        this.properties = new ArrayList();
        try {
            final Properties p = System.getProperties();
            final Iterator iterator = ((Hashtable<Object, V>)p).keySet().iterator();
            while (iterator.hasNext()) {
                final String name = iterator.next();
                final String value = System.getProperty(name);
                final SystemProperty sp = new SystemProperty(name, value);
                this.properties.add(sp);
            }
        }
        catch (final SecurityException ex) {}
        Collections.sort((List<Object>)this.properties, new SystemPropertyComparator(true));
        final String baseName = "org.jfree.ui.about.resources.AboutResources";
        final ResourceBundle resources = ResourceBundle.getBundle("org.jfree.ui.about.resources.AboutResources");
        this.nameColumnLabel = resources.getString("system-properties-table.column.name");
        this.valueColumnLabel = resources.getString("system-properties-table.column.value");
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public String getColumnName(final int column) {
        if (column == 0) {
            return this.nameColumnLabel;
        }
        return this.valueColumnLabel;
    }
    
    public int getRowCount() {
        return this.properties.size();
    }
    
    public Object getValueAt(final int row, final int column) {
        final SystemProperty sp = this.properties.get(row);
        if (column == 0) {
            return sp.getName();
        }
        if (column == 1) {
            return sp.getValue();
        }
        return null;
    }
    
    public boolean isSortable(final int column) {
        return column == 0;
    }
    
    public void sortByColumn(final int column, final boolean ascending) {
        if (this.isSortable(column)) {
            super.sortByColumn(column, ascending);
            Collections.sort((List<Object>)this.properties, new SystemPropertyComparator(ascending));
        }
    }
    
    protected static class SystemProperty
    {
        private String name;
        private String value;
        
        public SystemProperty(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    protected static class SystemPropertyComparator implements Comparator
    {
        private boolean ascending;
        
        public SystemPropertyComparator(final boolean ascending) {
            this.ascending = ascending;
        }
        
        public int compare(final Object o1, final Object o2) {
            if (!(o1 instanceof SystemProperty) || !(o2 instanceof SystemProperty)) {
                return 0;
            }
            final SystemProperty sp1 = (SystemProperty)o1;
            final SystemProperty sp2 = (SystemProperty)o2;
            if (this.ascending) {
                return sp1.getName().compareTo(sp2.getName());
            }
            return sp2.getName().compareTo(sp1.getName());
        }
        
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SystemPropertyComparator)) {
                return false;
            }
            final SystemPropertyComparator systemPropertyComparator = (SystemPropertyComparator)o;
            return this.ascending == systemPropertyComparator.ascending;
        }
        
        public int hashCode() {
            return this.ascending ? 1 : 0;
        }
    }
}
