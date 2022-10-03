package javax.swing;

import java.text.Collator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

public abstract class DefaultRowSorter<M, I> extends RowSorter<M>
{
    private boolean sortsOnUpdates;
    private Row[] viewToModel;
    private int[] modelToView;
    private Comparator[] comparators;
    private boolean[] isSortable;
    private SortKey[] cachedSortKeys;
    private Comparator[] sortComparators;
    private RowFilter<? super M, ? super I> filter;
    private FilterEntry filterEntry;
    private List<SortKey> sortKeys;
    private boolean[] useToString;
    private boolean sorted;
    private int maxSortKeys;
    private ModelWrapper<M, I> modelWrapper;
    private int modelRowCount;
    
    public DefaultRowSorter() {
        this.sortKeys = Collections.emptyList();
        this.maxSortKeys = 3;
    }
    
    protected final void setModelWrapper(final ModelWrapper<M, I> modelWrapper) {
        if (modelWrapper == null) {
            throw new IllegalArgumentException("modelWrapper most be non-null");
        }
        final ModelWrapper<M, I> modelWrapper2 = this.modelWrapper;
        this.modelWrapper = modelWrapper;
        if (modelWrapper2 != null) {
            this.modelStructureChanged();
        }
        else {
            this.modelRowCount = this.getModelWrapper().getRowCount();
        }
    }
    
    protected final ModelWrapper<M, I> getModelWrapper() {
        return this.modelWrapper;
    }
    
    @Override
    public final M getModel() {
        return this.getModelWrapper().getModel();
    }
    
    public void setSortable(final int n, final boolean b) {
        this.checkColumn(n);
        if (this.isSortable == null) {
            this.isSortable = new boolean[this.getModelWrapper().getColumnCount()];
            for (int i = this.isSortable.length - 1; i >= 0; --i) {
                this.isSortable[i] = true;
            }
        }
        this.isSortable[n] = b;
    }
    
    public boolean isSortable(final int n) {
        this.checkColumn(n);
        return this.isSortable == null || this.isSortable[n];
    }
    
    @Override
    public void setSortKeys(final List<? extends SortKey> list) {
        final List<SortKey> sortKeys = this.sortKeys;
        if (list != null && list.size() > 0) {
            final int columnCount = this.getModelWrapper().getColumnCount();
            for (final SortKey sortKey : list) {
                if (sortKey == null || sortKey.getColumn() < 0 || sortKey.getColumn() >= columnCount) {
                    throw new IllegalArgumentException("Invalid SortKey");
                }
            }
            this.sortKeys = Collections.unmodifiableList((List<? extends SortKey>)new ArrayList<SortKey>(list));
        }
        else {
            this.sortKeys = Collections.emptyList();
        }
        if (!this.sortKeys.equals(sortKeys)) {
            this.fireSortOrderChanged();
            if (this.viewToModel == null) {
                this.sort();
            }
            else {
                this.sortExistingData();
            }
        }
    }
    
    @Override
    public List<? extends SortKey> getSortKeys() {
        return this.sortKeys;
    }
    
    public void setMaxSortKeys(final int maxSortKeys) {
        if (maxSortKeys < 1) {
            throw new IllegalArgumentException("Invalid max");
        }
        this.maxSortKeys = maxSortKeys;
    }
    
    public int getMaxSortKeys() {
        return this.maxSortKeys;
    }
    
    public void setSortsOnUpdates(final boolean sortsOnUpdates) {
        this.sortsOnUpdates = sortsOnUpdates;
    }
    
    public boolean getSortsOnUpdates() {
        return this.sortsOnUpdates;
    }
    
    public void setRowFilter(final RowFilter<? super M, ? super I> filter) {
        this.filter = filter;
        this.sort();
    }
    
    public RowFilter<? super M, ? super I> getRowFilter() {
        return this.filter;
    }
    
    @Override
    public void toggleSortOrder(final int n) {
        this.checkColumn(n);
        if (this.isSortable(n)) {
            List subList;
            int n2;
            for (subList = new ArrayList(this.getSortKeys()), n2 = subList.size() - 1; n2 >= 0 && ((SortKey)subList.get(n2)).getColumn() != n; --n2) {}
            if (n2 == -1) {
                subList.add(0, new SortKey(n, SortOrder.ASCENDING));
            }
            else if (n2 == 0) {
                subList.set(0, this.toggle((SortKey)subList.get(0)));
            }
            else {
                subList.remove(n2);
                subList.add(0, new SortKey(n, SortOrder.ASCENDING));
            }
            if (subList.size() > this.getMaxSortKeys()) {
                subList = subList.subList(0, this.getMaxSortKeys());
            }
            this.setSortKeys(subList);
        }
    }
    
    private SortKey toggle(final SortKey sortKey) {
        if (sortKey.getSortOrder() == SortOrder.ASCENDING) {
            return new SortKey(sortKey.getColumn(), SortOrder.DESCENDING);
        }
        return new SortKey(sortKey.getColumn(), SortOrder.ASCENDING);
    }
    
    @Override
    public int convertRowIndexToView(final int n) {
        if (this.modelToView != null) {
            return this.modelToView[n];
        }
        if (n < 0 || n >= this.getModelWrapper().getRowCount()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return n;
    }
    
    @Override
    public int convertRowIndexToModel(final int n) {
        if (this.viewToModel != null) {
            return this.viewToModel[n].modelIndex;
        }
        if (n < 0 || n >= this.getModelWrapper().getRowCount()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return n;
    }
    
    private boolean isUnsorted() {
        final List<? extends SortKey> sortKeys = this.getSortKeys();
        return sortKeys.size() == 0 || ((SortKey)sortKeys.get(0)).getSortOrder() == SortOrder.UNSORTED;
    }
    
    private void sortExistingData() {
        final int[] viewToModelAsInts = this.getViewToModelAsInts(this.viewToModel);
        this.updateUseToString();
        this.cacheSortKeys(this.getSortKeys());
        if (this.isUnsorted()) {
            if (this.getRowFilter() == null) {
                this.viewToModel = null;
                this.modelToView = null;
            }
            else {
                int n = 0;
                for (int i = 0; i < this.modelToView.length; ++i) {
                    if (this.modelToView[i] != -1) {
                        this.viewToModel[n].modelIndex = i;
                        this.modelToView[i] = n++;
                    }
                }
            }
        }
        else {
            Arrays.sort(this.viewToModel);
            this.setModelToViewFromViewToModel(false);
        }
        this.fireRowSorterChanged(viewToModelAsInts);
    }
    
    public void sort() {
        this.sorted = true;
        final int[] viewToModelAsInts = this.getViewToModelAsInts(this.viewToModel);
        this.updateUseToString();
        if (this.isUnsorted()) {
            this.cachedSortKeys = new SortKey[0];
            if (this.getRowFilter() == null) {
                if (this.viewToModel == null) {
                    return;
                }
                this.viewToModel = null;
                this.modelToView = null;
            }
            else {
                this.initializeFilteredMapping();
            }
        }
        else {
            this.cacheSortKeys(this.getSortKeys());
            if (this.getRowFilter() != null) {
                this.initializeFilteredMapping();
            }
            else {
                this.createModelToView(this.getModelWrapper().getRowCount());
                this.createViewToModel(this.getModelWrapper().getRowCount());
            }
            Arrays.sort(this.viewToModel);
            this.setModelToViewFromViewToModel(false);
        }
        this.fireRowSorterChanged(viewToModelAsInts);
    }
    
    private void updateUseToString() {
        int i = this.getModelWrapper().getColumnCount();
        if (this.useToString == null || this.useToString.length != i) {
            this.useToString = new boolean[i];
        }
        --i;
        while (i >= 0) {
            this.useToString[i] = this.useToString(i);
            --i;
        }
    }
    
    private void initializeFilteredMapping() {
        final int rowCount = this.getModelWrapper().getRowCount();
        int n = 0;
        this.createModelToView(rowCount);
        for (int i = 0; i < rowCount; ++i) {
            if (this.include(i)) {
                this.modelToView[i] = i - n;
            }
            else {
                this.modelToView[i] = -1;
                ++n;
            }
        }
        this.createViewToModel(rowCount - n);
        int j = 0;
        int n2 = 0;
        while (j < rowCount) {
            if (this.modelToView[j] != -1) {
                this.viewToModel[n2++].modelIndex = j;
            }
            ++j;
        }
    }
    
    private void createModelToView(final int n) {
        if (this.modelToView == null || this.modelToView.length != n) {
            this.modelToView = new int[n];
        }
    }
    
    private void createViewToModel(final int n) {
        int min = 0;
        if (this.viewToModel != null) {
            min = Math.min(n, this.viewToModel.length);
            if (this.viewToModel.length != n) {
                System.arraycopy(this.viewToModel, 0, this.viewToModel = new Row[n], 0, min);
            }
        }
        else {
            this.viewToModel = new Row[n];
        }
        for (int i = 0; i < min; ++i) {
            this.viewToModel[i].modelIndex = i;
        }
        for (int j = min; j < n; ++j) {
            this.viewToModel[j] = new Row(this, j);
        }
    }
    
    private void cacheSortKeys(final List<? extends SortKey> list) {
        final int size = list.size();
        this.sortComparators = new Comparator[size];
        for (int i = 0; i < size; ++i) {
            this.sortComparators[i] = this.getComparator0(((SortKey)list.get(i)).getColumn());
        }
        this.cachedSortKeys = list.toArray(new SortKey[size]);
    }
    
    protected boolean useToString(final int n) {
        return this.getComparator(n) == null;
    }
    
    private void setModelToViewFromViewToModel(final boolean b) {
        if (b) {
            for (int i = this.modelToView.length - 1; i >= 0; --i) {
                this.modelToView[i] = -1;
            }
        }
        for (int j = this.viewToModel.length - 1; j >= 0; --j) {
            this.modelToView[this.viewToModel[j].modelIndex] = j;
        }
    }
    
    private int[] getViewToModelAsInts(final Row[] array) {
        if (array != null) {
            final int[] array2 = new int[array.length];
            for (int i = array.length - 1; i >= 0; --i) {
                array2[i] = array[i].modelIndex;
            }
            return array2;
        }
        return new int[0];
    }
    
    public void setComparator(final int n, final Comparator<?> comparator) {
        this.checkColumn(n);
        if (this.comparators == null) {
            this.comparators = new Comparator[this.getModelWrapper().getColumnCount()];
        }
        this.comparators[n] = comparator;
    }
    
    public Comparator<?> getComparator(final int n) {
        this.checkColumn(n);
        if (this.comparators != null) {
            return this.comparators[n];
        }
        return null;
    }
    
    private Comparator getComparator0(final int n) {
        final Comparator<?> comparator = this.getComparator(n);
        if (comparator != null) {
            return comparator;
        }
        return Collator.getInstance();
    }
    
    private RowFilter.Entry<M, I> getFilterEntry(final int modelIndex) {
        if (this.filterEntry == null) {
            this.filterEntry = new FilterEntry();
        }
        this.filterEntry.modelIndex = modelIndex;
        return this.filterEntry;
    }
    
    @Override
    public int getViewRowCount() {
        if (this.viewToModel != null) {
            return this.viewToModel.length;
        }
        return this.getModelWrapper().getRowCount();
    }
    
    @Override
    public int getModelRowCount() {
        return this.getModelWrapper().getRowCount();
    }
    
    private void allChanged() {
        this.modelToView = null;
        this.viewToModel = null;
        this.comparators = null;
        this.isSortable = null;
        if (this.isUnsorted()) {
            this.sort();
        }
        else {
            this.setSortKeys(null);
        }
    }
    
    @Override
    public void modelStructureChanged() {
        this.allChanged();
        this.modelRowCount = this.getModelWrapper().getRowCount();
    }
    
    @Override
    public void allRowsChanged() {
        this.modelRowCount = this.getModelWrapper().getRowCount();
        this.sort();
    }
    
    @Override
    public void rowsInserted(final int n, final int n2) {
        this.checkAgainstModel(n, n2);
        final int rowCount = this.getModelWrapper().getRowCount();
        if (n2 >= rowCount) {
            throw new IndexOutOfBoundsException("Invalid range");
        }
        this.modelRowCount = rowCount;
        if (this.shouldOptimizeChange(n, n2)) {
            this.rowsInserted0(n, n2);
        }
    }
    
    @Override
    public void rowsDeleted(final int n, final int n2) {
        this.checkAgainstModel(n, n2);
        if (n >= this.modelRowCount || n2 >= this.modelRowCount) {
            throw new IndexOutOfBoundsException("Invalid range");
        }
        this.modelRowCount = this.getModelWrapper().getRowCount();
        if (this.shouldOptimizeChange(n, n2)) {
            this.rowsDeleted0(n, n2);
        }
    }
    
    @Override
    public void rowsUpdated(final int n, final int n2) {
        this.checkAgainstModel(n, n2);
        if (n >= this.modelRowCount || n2 >= this.modelRowCount) {
            throw new IndexOutOfBoundsException("Invalid range");
        }
        if (this.getSortsOnUpdates()) {
            if (this.shouldOptimizeChange(n, n2)) {
                this.rowsUpdated0(n, n2);
            }
        }
        else {
            this.sorted = false;
        }
    }
    
    @Override
    public void rowsUpdated(final int n, final int n2, final int n3) {
        this.checkColumn(n3);
        this.rowsUpdated(n, n2);
    }
    
    private void checkAgainstModel(final int n, final int n2) {
        if (n > n2 || n < 0 || n2 < 0 || n > this.modelRowCount) {
            throw new IndexOutOfBoundsException("Invalid range");
        }
    }
    
    private boolean include(final int n) {
        final RowFilter<? super M, ? super I> rowFilter = this.getRowFilter();
        return rowFilter == null || rowFilter.include(this.getFilterEntry(n));
    }
    
    private int compare(final int n, final int n2) {
        for (int i = 0; i < this.cachedSortKeys.length; ++i) {
            final int column = this.cachedSortKeys[i].getColumn();
            final SortOrder sortOrder = this.cachedSortKeys[i].getSortOrder();
            int compare;
            if (sortOrder == SortOrder.UNSORTED) {
                compare = n - n2;
            }
            else {
                Object o;
                Object o2;
                if (this.useToString[column]) {
                    o = this.getModelWrapper().getStringValueAt(n, column);
                    o2 = this.getModelWrapper().getStringValueAt(n2, column);
                }
                else {
                    o = this.getModelWrapper().getValueAt(n, column);
                    o2 = this.getModelWrapper().getValueAt(n2, column);
                }
                if (o == null) {
                    if (o2 == null) {
                        compare = 0;
                    }
                    else {
                        compare = -1;
                    }
                }
                else if (o2 == null) {
                    compare = 1;
                }
                else {
                    compare = this.sortComparators[i].compare(o, o2);
                }
                if (sortOrder == SortOrder.DESCENDING) {
                    compare *= -1;
                }
            }
            if (compare != 0) {
                return compare;
            }
        }
        return n - n2;
    }
    
    private boolean isTransformed() {
        return this.viewToModel != null;
    }
    
    private void insertInOrder(final List<Row> list, final Row[] array) {
        int n = 0;
        final int size = list.size();
        for (int i = 0; i < size; ++i) {
            int binarySearch = Arrays.binarySearch(array, list.get(i));
            if (binarySearch < 0) {
                binarySearch = -1 - binarySearch;
            }
            System.arraycopy(array, n, this.viewToModel, n + i, binarySearch - n);
            this.viewToModel[binarySearch + i] = list.get(i);
            n = binarySearch;
        }
        System.arraycopy(array, n, this.viewToModel, n + size, array.length - n);
    }
    
    private boolean shouldOptimizeChange(final int n, final int n2) {
        if (!this.isTransformed()) {
            return false;
        }
        if (!this.sorted || n2 - n > this.viewToModel.length / 10) {
            this.sort();
            return false;
        }
        return true;
    }
    
    private void rowsInserted0(final int n, final int n2) {
        final int[] viewToModelAsInts = this.getViewToModelAsInts(this.viewToModel);
        final int n3 = n2 - n + 1;
        final ArrayList list = new ArrayList(n3);
        for (int i = n; i <= n2; ++i) {
            if (this.include(i)) {
                list.add((Object)new Row(this, i));
            }
        }
        for (int j = this.modelToView.length - 1; j >= n; --j) {
            final int n4 = this.modelToView[j];
            if (n4 != -1) {
                final Row row = this.viewToModel[n4];
                row.modelIndex += n3;
            }
        }
        if (list.size() > 0) {
            Collections.sort((List<Comparable>)list);
            final Row[] viewToModel = this.viewToModel;
            this.viewToModel = new Row[this.viewToModel.length + list.size()];
            this.insertInOrder((List<Row>)list, viewToModel);
        }
        this.createModelToView(this.getModelWrapper().getRowCount());
        this.setModelToViewFromViewToModel(true);
        this.fireRowSorterChanged(viewToModelAsInts);
    }
    
    private void rowsDeleted0(final int n, final int n2) {
        final int[] viewToModelAsInts = this.getViewToModelAsInts(this.viewToModel);
        int n3 = 0;
        for (int i = n; i <= n2; ++i) {
            final int n4 = this.modelToView[i];
            if (n4 != -1) {
                ++n3;
                this.viewToModel[n4] = null;
            }
        }
        final int n5 = n2 - n + 1;
        for (int j = this.modelToView.length - 1; j > n2; --j) {
            final int n6 = this.modelToView[j];
            if (n6 != -1) {
                final Row row = this.viewToModel[n6];
                row.modelIndex -= n5;
            }
        }
        if (n3 > 0) {
            final Row[] viewToModel = new Row[this.viewToModel.length - n3];
            int n7 = 0;
            int n8 = 0;
            for (int k = 0; k < this.viewToModel.length; ++k) {
                if (this.viewToModel[k] == null) {
                    System.arraycopy(this.viewToModel, n8, viewToModel, n7, k - n8);
                    n7 += k - n8;
                    n8 = k + 1;
                }
            }
            System.arraycopy(this.viewToModel, n8, viewToModel, n7, this.viewToModel.length - n8);
            this.viewToModel = viewToModel;
        }
        this.createModelToView(this.getModelWrapper().getRowCount());
        this.setModelToViewFromViewToModel(true);
        this.fireRowSorterChanged(viewToModelAsInts);
    }
    
    private void rowsUpdated0(final int n, final int n2) {
        final int[] viewToModelAsInts = this.getViewToModelAsInts(this.viewToModel);
        final int n3 = n2 - n + 1;
        if (this.getRowFilter() == null) {
            final Row[] array = new Row[n3];
            for (int n4 = 0, i = n; i <= n2; ++i, ++n4) {
                array[n4] = this.viewToModel[this.modelToView[i]];
            }
            Arrays.sort(array);
            final Row[] array2 = new Row[this.viewToModel.length - n3];
            int j = 0;
            int n5 = 0;
            while (j < this.viewToModel.length) {
                final int modelIndex = this.viewToModel[j].modelIndex;
                if (modelIndex < n || modelIndex > n2) {
                    array2[n5++] = this.viewToModel[j];
                }
                ++j;
            }
            this.insertInOrder(Arrays.asList(array), array2);
            this.setModelToViewFromViewToModel(false);
        }
        else {
            final ArrayList list = new ArrayList(n3);
            int n6 = 0;
            int n7 = 0;
            int n8 = 0;
            for (int k = n; k <= n2; ++k) {
                if (this.modelToView[k] == -1) {
                    if (this.include(k)) {
                        list.add((Object)new Row(this, k));
                        ++n6;
                    }
                }
                else {
                    if (!this.include(k)) {
                        ++n7;
                    }
                    else {
                        list.add((Object)this.viewToModel[this.modelToView[k]]);
                    }
                    this.modelToView[k] = -2;
                    ++n8;
                }
            }
            Collections.sort((List<Comparable>)list);
            final Row[] array3 = new Row[this.viewToModel.length - n8];
            int l = 0;
            int n9 = 0;
            while (l < this.viewToModel.length) {
                if (this.modelToView[this.viewToModel[l].modelIndex] != -2) {
                    array3[n9++] = this.viewToModel[l];
                }
                ++l;
            }
            if (n6 != n7) {
                this.viewToModel = new Row[this.viewToModel.length + n6 - n7];
            }
            this.insertInOrder((List<Row>)list, array3);
            this.setModelToViewFromViewToModel(true);
        }
        this.fireRowSorterChanged(viewToModelAsInts);
    }
    
    private void checkColumn(final int n) {
        if (n < 0 || n >= this.getModelWrapper().getColumnCount()) {
            throw new IndexOutOfBoundsException("column beyond range of TableModel");
        }
    }
    
    protected abstract static class ModelWrapper<M, I>
    {
        public abstract M getModel();
        
        public abstract int getColumnCount();
        
        public abstract int getRowCount();
        
        public abstract Object getValueAt(final int p0, final int p1);
        
        public String getStringValueAt(final int n, final int n2) {
            final Object value = this.getValueAt(n, n2);
            if (value == null) {
                return "";
            }
            final String string = value.toString();
            if (string == null) {
                return "";
            }
            return string;
        }
        
        public abstract I getIdentifier(final int p0);
    }
    
    private class FilterEntry extends RowFilter.Entry<M, I>
    {
        int modelIndex;
        
        @Override
        public M getModel() {
            return DefaultRowSorter.this.getModelWrapper().getModel();
        }
        
        @Override
        public int getValueCount() {
            return DefaultRowSorter.this.getModelWrapper().getColumnCount();
        }
        
        @Override
        public Object getValue(final int n) {
            return DefaultRowSorter.this.getModelWrapper().getValueAt(this.modelIndex, n);
        }
        
        @Override
        public String getStringValue(final int n) {
            return DefaultRowSorter.this.getModelWrapper().getStringValueAt(this.modelIndex, n);
        }
        
        @Override
        public I getIdentifier() {
            return DefaultRowSorter.this.getModelWrapper().getIdentifier(this.modelIndex);
        }
    }
    
    private static class Row implements Comparable<Row>
    {
        private DefaultRowSorter sorter;
        int modelIndex;
        
        public Row(final DefaultRowSorter sorter, final int modelIndex) {
            this.sorter = sorter;
            this.modelIndex = modelIndex;
        }
        
        @Override
        public int compareTo(final Row row) {
            return this.sorter.compare(this.modelIndex, row.modelIndex);
        }
    }
}
