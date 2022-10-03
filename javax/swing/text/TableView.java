package javax.swing.text;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import javax.swing.text.html.HTML;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;

public abstract class TableView extends BoxView
{
    int[] columnSpans;
    int[] columnOffsets;
    SizeRequirements[] columnRequirements;
    Vector<TableRow> rows;
    boolean gridValid;
    private static final BitSet EMPTY;
    
    public TableView(final Element element) {
        super(element, 1);
        this.rows = new Vector<TableRow>();
        this.gridValid = false;
    }
    
    protected TableRow createTableRow(final Element element) {
        return new TableRow(element);
    }
    
    @Deprecated
    protected TableCell createTableCell(final Element element) {
        return new TableCell(element);
    }
    
    int getColumnCount() {
        return this.columnSpans.length;
    }
    
    int getColumnSpan(final int n) {
        return this.columnSpans[n];
    }
    
    int getRowCount() {
        return this.rows.size();
    }
    
    int getRowSpan(final int n) {
        final TableRow row = this.getRow(n);
        if (row != null) {
            return (int)row.getPreferredSpan(1);
        }
        return 0;
    }
    
    TableRow getRow(final int n) {
        if (n < this.rows.size()) {
            return this.rows.elementAt(n);
        }
        return null;
    }
    
    int getColumnsOccupied(final View view) {
        final String s = (String)view.getElement().getAttributes().getAttribute(HTML.Attribute.COLSPAN);
        if (s != null) {
            try {
                return Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {}
        }
        return 1;
    }
    
    int getRowsOccupied(final View view) {
        final String s = (String)view.getElement().getAttributes().getAttribute(HTML.Attribute.ROWSPAN);
        if (s != null) {
            try {
                return Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {}
        }
        return 1;
    }
    
    void invalidateGrid() {
        this.gridValid = false;
    }
    
    @Override
    protected void forwardUpdate(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.forwardUpdate(elementChange, documentEvent, shape, viewFactory);
        if (shape != null) {
            final Container container = this.getContainer();
            if (container != null) {
                final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
                container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
        }
    }
    
    @Override
    public void replace(final int n, final int n2, final View[] array) {
        super.replace(n, n2, array);
        this.invalidateGrid();
    }
    
    void updateGrid() {
        if (!this.gridValid) {
            this.rows.removeAllElements();
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                if (view instanceof TableRow) {
                    this.rows.addElement((TableRow)view);
                    final TableRow tableRow = (TableRow)view;
                    tableRow.clearFilledColumns();
                    tableRow.setRow(i);
                }
            }
            int max = 0;
            for (int size = this.rows.size(), j = 0; j < size; ++j) {
                final TableRow row = this.getRow(j);
                int n = 0;
                for (int k = 0; k < row.getViewCount(); ++k, ++n) {
                    final View view2 = row.getView(k);
                    while (row.isFilled(n)) {
                        ++n;
                    }
                    final int rowsOccupied = this.getRowsOccupied(view2);
                    final int columnsOccupied = this.getColumnsOccupied(view2);
                    if (columnsOccupied > 1 || rowsOccupied > 1) {
                        final int n2 = j + rowsOccupied;
                        final int n3 = n + columnsOccupied;
                        for (int l = j; l < n2; ++l) {
                            for (int n4 = n; n4 < n3; ++n4) {
                                if (l != j || n4 != n) {
                                    this.addFill(l, n4);
                                }
                            }
                        }
                        if (columnsOccupied > 1) {
                            n += columnsOccupied - 1;
                        }
                    }
                }
                max = Math.max(max, n);
            }
            this.columnSpans = new int[max];
            this.columnOffsets = new int[max];
            this.columnRequirements = new SizeRequirements[max];
            for (int n5 = 0; n5 < max; ++n5) {
                this.columnRequirements[n5] = new SizeRequirements();
            }
            this.gridValid = true;
        }
    }
    
    void addFill(final int n, final int n2) {
        final TableRow row = this.getRow(n);
        if (row != null) {
            row.fillColumn(n2);
        }
    }
    
    protected void layoutColumns(final int n, final int[] array, final int[] array2, final SizeRequirements[] array3) {
        SizeRequirements.calculateTiledPositions(n, null, array3, array, array2);
    }
    
    @Override
    protected void layoutMinorAxis(final int n, final int n2, final int[] array, final int[] array2) {
        this.updateGrid();
        for (int rowCount = this.getRowCount(), i = 0; i < rowCount; ++i) {
            this.getRow(i).layoutChanged(n2);
        }
        this.layoutColumns(n, this.columnOffsets, this.columnSpans, this.columnRequirements);
        super.layoutMinorAxis(n, n2, array, array2);
    }
    
    @Override
    protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements sizeRequirements) {
        this.updateGrid();
        this.calculateColumnRequirements(n);
        if (sizeRequirements == null) {
            sizeRequirements = new SizeRequirements();
        }
        long n2 = 0L;
        long n3 = 0L;
        long n4 = 0L;
        for (final SizeRequirements sizeRequirements2 : this.columnRequirements) {
            n2 += sizeRequirements2.minimum;
            n3 += sizeRequirements2.preferred;
            n4 += sizeRequirements2.maximum;
        }
        sizeRequirements.minimum = (int)n2;
        sizeRequirements.preferred = (int)n3;
        sizeRequirements.maximum = (int)n4;
        sizeRequirements.alignment = 0.0f;
        return sizeRequirements;
    }
    
    void calculateColumnRequirements(final int n) {
        boolean b = false;
        final int rowCount = this.getRowCount();
        for (int i = 0; i < rowCount; ++i) {
            final TableRow row = this.getRow(i);
            for (int n2 = 0, viewCount = row.getViewCount(), j = 0; j < viewCount; ++j, ++n2) {
                final View view = row.getView(j);
                while (row.isFilled(n2)) {
                    ++n2;
                }
                this.getRowsOccupied(view);
                final int columnsOccupied = this.getColumnsOccupied(view);
                if (columnsOccupied == 1) {
                    this.checkSingleColumnCell(n, n2, view);
                }
                else {
                    b = true;
                    n2 += columnsOccupied - 1;
                }
            }
        }
        if (b) {
            for (int k = 0; k < rowCount; ++k) {
                final TableRow row2 = this.getRow(k);
                for (int n3 = 0, viewCount2 = row2.getViewCount(), l = 0; l < viewCount2; ++l, ++n3) {
                    final View view2 = row2.getView(l);
                    while (row2.isFilled(n3)) {
                        ++n3;
                    }
                    final int columnsOccupied2 = this.getColumnsOccupied(view2);
                    if (columnsOccupied2 > 1) {
                        this.checkMultiColumnCell(n, n3, columnsOccupied2, view2);
                        n3 += columnsOccupied2 - 1;
                    }
                }
            }
        }
    }
    
    void checkSingleColumnCell(final int n, final int n2, final View view) {
        final SizeRequirements sizeRequirements = this.columnRequirements[n2];
        sizeRequirements.minimum = Math.max((int)view.getMinimumSpan(n), sizeRequirements.minimum);
        sizeRequirements.preferred = Math.max((int)view.getPreferredSpan(n), sizeRequirements.preferred);
        sizeRequirements.maximum = Math.max((int)view.getMaximumSpan(n), sizeRequirements.maximum);
    }
    
    void checkMultiColumnCell(final int n, final int n2, final int n3, final View view) {
        long n4 = 0L;
        long n5 = 0L;
        long n6 = 0L;
        for (int i = 0; i < n3; ++i) {
            final SizeRequirements sizeRequirements = this.columnRequirements[n2 + i];
            n4 += sizeRequirements.minimum;
            n5 += sizeRequirements.preferred;
            n6 += sizeRequirements.maximum;
        }
        final int n7 = (int)view.getMinimumSpan(n);
        if (n7 > n4) {
            final SizeRequirements[] array = new SizeRequirements[n3];
            for (int j = 0; j < n3; ++j) {
                final SizeRequirements[] array2 = array;
                final int n8 = j;
                final SizeRequirements sizeRequirements2 = this.columnRequirements[n2 + j];
                array2[n8] = sizeRequirements2;
                final SizeRequirements sizeRequirements3 = sizeRequirements2;
                sizeRequirements3.maximum = Math.max(sizeRequirements3.maximum, (int)view.getMaximumSpan(n));
            }
            final int[] array3 = new int[n3];
            SizeRequirements.calculateTiledPositions(n7, null, array, new int[n3], array3);
            for (int k = 0; k < n3; ++k) {
                final SizeRequirements sizeRequirements4 = array[k];
                sizeRequirements4.minimum = Math.max(array3[k], sizeRequirements4.minimum);
                sizeRequirements4.preferred = Math.max(sizeRequirements4.minimum, sizeRequirements4.preferred);
                sizeRequirements4.maximum = Math.max(sizeRequirements4.preferred, sizeRequirements4.maximum);
            }
        }
        final int n9 = (int)view.getPreferredSpan(n);
        if (n9 > n5) {
            final SizeRequirements[] array4 = new SizeRequirements[n3];
            for (int l = 0; l < n3; ++l) {
                array4[l] = this.columnRequirements[n2 + l];
            }
            final int[] array5 = new int[n3];
            SizeRequirements.calculateTiledPositions(n9, null, array4, new int[n3], array5);
            for (int n10 = 0; n10 < n3; ++n10) {
                final SizeRequirements sizeRequirements5 = array4[n10];
                sizeRequirements5.preferred = Math.max(array5[n10], sizeRequirements5.preferred);
                sizeRequirements5.maximum = Math.max(sizeRequirements5.preferred, sizeRequirements5.maximum);
            }
        }
    }
    
    @Override
    protected View getViewAtPosition(final int n, final Rectangle rectangle) {
        final int viewCount = this.getViewCount();
        for (int i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            final int startOffset = view.getStartOffset();
            final int endOffset = view.getEndOffset();
            if (n >= startOffset && n < endOffset) {
                if (rectangle != null) {
                    this.childAllocation(i, rectangle);
                }
                return view;
            }
        }
        if (n == this.getEndOffset()) {
            final View view2 = this.getView(viewCount - 1);
            if (rectangle != null) {
                this.childAllocation(viewCount - 1, rectangle);
            }
            return view2;
        }
        return null;
    }
    
    static {
        EMPTY = new BitSet();
    }
    
    public class TableRow extends BoxView
    {
        BitSet fillColumns;
        int row;
        
        public TableRow(final Element element) {
            super(element, 0);
            this.fillColumns = new BitSet();
        }
        
        void clearFilledColumns() {
            this.fillColumns.and(TableView.EMPTY);
        }
        
        void fillColumn(final int n) {
            this.fillColumns.set(n);
        }
        
        boolean isFilled(final int n) {
            return this.fillColumns.get(n);
        }
        
        int getRow() {
            return this.row;
        }
        
        void setRow(final int row) {
            this.row = row;
        }
        
        int getColumnCount() {
            int n = 0;
            for (int size = this.fillColumns.size(), i = 0; i < size; ++i) {
                if (this.fillColumns.get(i)) {
                    ++n;
                }
            }
            return this.getViewCount() + n;
        }
        
        @Override
        public void replace(final int n, final int n2, final View[] array) {
            super.replace(n, n2, array);
            TableView.this.invalidateGrid();
        }
        
        @Override
        protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
            for (int n3 = 0, viewCount = this.getViewCount(), i = 0; i < viewCount; ++i, ++n3) {
                final View view = this.getView(i);
                while (this.isFilled(n3)) {
                    ++n3;
                }
                final int columnsOccupied = TableView.this.getColumnsOccupied(view);
                array2[i] = TableView.this.columnSpans[n3];
                array[i] = TableView.this.columnOffsets[n3];
                if (columnsOccupied > 1) {
                    final int length = TableView.this.columnSpans.length;
                    for (int j = 1; j < columnsOccupied; ++j) {
                        if (n3 + j < length) {
                            final int n4 = i;
                            array2[n4] += TableView.this.columnSpans[n3 + j];
                        }
                    }
                    n3 += columnsOccupied - 1;
                }
            }
        }
        
        @Override
        protected void layoutMinorAxis(final int n, final int n2, final int[] array, final int[] array2) {
            super.layoutMinorAxis(n, n2, array, array2);
            for (int n3 = 0, viewCount = this.getViewCount(), i = 0; i < viewCount; ++i, ++n3) {
                final View view = this.getView(i);
                while (this.isFilled(n3)) {
                    ++n3;
                }
                final int columnsOccupied = TableView.this.getColumnsOccupied(view);
                final int rowsOccupied = TableView.this.getRowsOccupied(view);
                if (rowsOccupied > 1) {
                    for (int j = 1; j < rowsOccupied; ++j) {
                        if (this.getRow() + j < TableView.this.getViewCount()) {
                            final int span = TableView.this.getSpan(1, this.getRow() + j);
                            final int n4 = i;
                            array2[n4] += span;
                        }
                    }
                }
                if (columnsOccupied > 1) {
                    n3 += columnsOccupied - 1;
                }
            }
        }
        
        @Override
        public int getResizeWeight(final int n) {
            return 1;
        }
        
        @Override
        protected View getViewAtPosition(final int n, final Rectangle rectangle) {
            final int viewCount = this.getViewCount();
            for (int i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                final int startOffset = view.getStartOffset();
                final int endOffset = view.getEndOffset();
                if (n >= startOffset && n < endOffset) {
                    if (rectangle != null) {
                        this.childAllocation(i, rectangle);
                    }
                    return view;
                }
            }
            if (n == this.getEndOffset()) {
                final View view2 = this.getView(viewCount - 1);
                if (rectangle != null) {
                    this.childAllocation(viewCount - 1, rectangle);
                }
                return view2;
            }
            return null;
        }
    }
    
    @Deprecated
    public class TableCell extends BoxView implements GridCell
    {
        int row;
        int col;
        
        public TableCell(final Element element) {
            super(element, 1);
        }
        
        @Override
        public int getColumnCount() {
            return 1;
        }
        
        @Override
        public int getRowCount() {
            return 1;
        }
        
        @Override
        public void setGridLocation(final int row, final int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public int getGridRow() {
            return this.row;
        }
        
        @Override
        public int getGridColumn() {
            return this.col;
        }
    }
    
    interface GridCell
    {
        void setGridLocation(final int p0, final int p1);
        
        int getGridRow();
        
        int getGridColumn();
        
        int getColumnCount();
        
        int getRowCount();
    }
}
