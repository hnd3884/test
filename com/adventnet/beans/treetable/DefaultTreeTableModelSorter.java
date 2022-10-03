package com.adventnet.beans.treetable;

import java.text.Collator;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Vector;
import com.adventnet.beans.xtable.SortColumn;
import java.util.Comparator;
import java.util.Locale;

public class DefaultTreeTableModelSorter implements TreeTableModelSorter
{
    private Locale locale;
    private Comparator comparator;
    
    public DefaultTreeTableModelSorter() {
        this.comparator = new ColumnSorter();
    }
    
    public void sort(final TreeTableModel treeTableModel, final Object o, final SortColumn[] array, final Locale locale, final int n, final int n2) {
        this.locale = locale;
        final Vector vector = new Vector();
        treeTableModel.getChildCount(o);
        for (int i = n; i < n2; ++i) {
            final Object child = treeTableModel.getChild(o, i);
            vector.add(child);
            final TreeNode treeNode = (TreeNode)child;
            if (treeNode.getChildCount() > 0) {
                this.sort(treeTableModel, treeNode, array, locale, 0, treeNode.getChildCount());
            }
        }
        this.performSort(treeTableModel, vector, o, array, n, n2);
    }
    
    private void performSort(final TreeTableModel treeTableModel, final Vector vector, final Object o, final SortColumn[] array, final int n, final int n2) {
        for (int i = 0; i < array.length; ++i) {
            int columnIndex = -1;
            if (i > 0) {
                columnIndex = array[i - 1].getColumnIndex();
            }
            final int columnIndex2 = array[i].getColumnIndex();
            for (int j = 0; j < vector.size(); ++j) {
                for (int k = j + 1; k < vector.size(); ++k) {
                    Object value = null;
                    Object value2 = null;
                    if (columnIndex != -1) {
                        final Object value3 = vector.get(j);
                        final Object value4 = vector.get(k);
                        value = treeTableModel.getValueAt(value3, columnIndex);
                        value2 = treeTableModel.getValueAt(value4, columnIndex);
                    }
                    if (this.comparator.compare(value, value2) != 0) {
                        break;
                    }
                    final Object value5 = treeTableModel.getValueAt(vector.get(j), columnIndex2);
                    final Object value6 = treeTableModel.getValueAt(vector.get(k), columnIndex2);
                    if (this.comparator.compare(value5, value6) < 0) {
                        if (array[i].isAscending()) {
                            final Object value7 = vector.get(j);
                            vector.set(j, vector.get(k));
                            vector.set(k, value7);
                        }
                    }
                    else if (this.comparator.compare(value5, value6) > 0 && !array[i].isAscending()) {
                        final Object value8 = vector.get(j);
                        vector.set(j, vector.get(k));
                        vector.set(k, value8);
                    }
                }
            }
        }
        final DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode)o;
        final Vector vector2 = new Vector<DefaultMutableTreeNode>();
        for (int l = n2; l < defaultMutableTreeNode.getChildCount(); ++l) {
            vector2.add(defaultMutableTreeNode.getChildAt(l));
        }
        for (int n3 = defaultMutableTreeNode.getChildCount() - 1; n3 >= n; --n3) {
            defaultMutableTreeNode.remove(n3);
        }
        for (int n4 = 0; n4 < vector.size(); ++n4) {
            defaultMutableTreeNode.add((MutableTreeNode)vector.get(n4));
        }
        for (int n5 = 0; n5 < vector2.size(); ++n5) {
            defaultMutableTreeNode.add((MutableTreeNode)vector2.get(n5));
        }
    }
    
    public class ColumnSorter implements Comparator
    {
        private int prevSortColumn;
        private int sortCol;
        boolean ascending;
        int baseCol;
        
        public int compare(Object o, Object o2) {
            if (o instanceof String && ((String)o).length() == 0) {
                o = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
            if (o == null && o2 == null) {
                return 0;
            }
            if (o == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            Collator collator;
            if (DefaultTreeTableModelSorter.this.locale == null) {
                collator = Collator.getInstance();
            }
            else {
                collator = Collator.getInstance(DefaultTreeTableModelSorter.this.locale);
            }
            if (o instanceof String && o2 instanceof String) {
                if (this.ascending) {
                    return collator.compare(o, o2);
                }
                return collator.compare(o2, o);
            }
            else {
                final String name = o.getClass().getName();
                final String name2 = o2.getClass().getName();
                if (o instanceof Comparable && o2 instanceof Comparable && name.equals(name2)) {
                    if (this.ascending) {
                        return ((Comparable)o).compareTo(o2);
                    }
                    return ((Comparable)o2).compareTo(o);
                }
                else {
                    if (this.ascending) {
                        return collator.compare(o.toString(), o2.toString());
                    }
                    return collator.compare(o2.toString(), o.toString());
                }
            }
        }
    }
}
