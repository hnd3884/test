package javax.swing.text.html;

import javax.swing.event.DocumentEvent;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Container;
import javax.swing.text.JTextComponent;
import java.util.Arrays;
import javax.swing.text.View;
import java.awt.Rectangle;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.ViewFactory;
import javax.swing.text.BoxView;

class TableView extends BoxView implements ViewFactory
{
    private AttributeSet attr;
    private StyleSheet.BoxPainter painter;
    private int cellSpacing;
    private int borderWidth;
    private int captionIndex;
    private boolean relativeCells;
    private boolean multiRowCells;
    int[] columnSpans;
    int[] columnOffsets;
    SizeRequirements totalColumnRequirements;
    SizeRequirements[] columnRequirements;
    RowIterator rowIterator;
    ColumnIterator colIterator;
    Vector<RowView> rows;
    boolean skipComments;
    boolean gridValid;
    private static final BitSet EMPTY;
    
    public TableView(final Element element) {
        super(element, 1);
        this.rowIterator = new RowIterator();
        this.colIterator = new ColumnIterator();
        this.skipComments = false;
        this.rows = new Vector<RowView>();
        this.gridValid = false;
        this.captionIndex = -1;
        this.totalColumnRequirements = new SizeRequirements();
    }
    
    protected RowView createTableRow(final Element element) {
        if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TR) {
            return new RowView(element);
        }
        return null;
    }
    
    public int getColumnCount() {
        return this.columnSpans.length;
    }
    
    public int getColumnSpan(final int n) {
        if (n < this.columnSpans.length) {
            return this.columnSpans[n];
        }
        return 0;
    }
    
    public int getRowCount() {
        return this.rows.size();
    }
    
    public int getMultiRowSpan(final int n, final int n2) {
        final RowView row = this.getRow(n);
        final RowView row2 = this.getRow(n2);
        if (row != null && row2 != null) {
            final int viewIndex = row.viewIndex;
            final int viewIndex2 = row2.viewIndex;
            return this.getOffset(1, viewIndex2) - this.getOffset(1, viewIndex) + this.getSpan(1, viewIndex2);
        }
        return 0;
    }
    
    public int getRowSpan(final int n) {
        final RowView row = this.getRow(n);
        if (row != null) {
            return this.getSpan(1, row.viewIndex);
        }
        return 0;
    }
    
    RowView getRow(final int n) {
        if (n < this.rows.size()) {
            return this.rows.elementAt(n);
        }
        return null;
    }
    
    @Override
    protected View getViewAtPoint(final int n, final int n2, final Rectangle bounds) {
        final int viewCount = this.getViewCount();
        final Rectangle bounds2 = new Rectangle();
        for (int i = 0; i < viewCount; ++i) {
            bounds2.setBounds(bounds);
            this.childAllocation(i, bounds2);
            final View view = this.getView(i);
            if (view instanceof RowView) {
                final View viewAtPoint = ((RowView)view).findViewAtPoint(n, n2, bounds2);
                if (viewAtPoint != null) {
                    bounds.setBounds(bounds2);
                    return viewAtPoint;
                }
            }
        }
        return super.getViewAtPoint(n, n2, bounds);
    }
    
    protected int getColumnsOccupied(final View view) {
        final AttributeSet attributes = view.getElement().getAttributes();
        if (attributes.isDefined(HTML.Attribute.COLSPAN)) {
            final String s = (String)attributes.getAttribute(HTML.Attribute.COLSPAN);
            if (s != null) {
                try {
                    return Integer.parseInt(s);
                }
                catch (final NumberFormatException ex) {}
            }
        }
        return 1;
    }
    
    protected int getRowsOccupied(final View view) {
        final AttributeSet attributes = view.getElement().getAttributes();
        if (attributes.isDefined(HTML.Attribute.ROWSPAN)) {
            final String s = (String)attributes.getAttribute(HTML.Attribute.ROWSPAN);
            if (s != null) {
                try {
                    return Integer.parseInt(s);
                }
                catch (final NumberFormatException ex) {}
            }
        }
        return 1;
    }
    
    protected void invalidateGrid() {
        this.gridValid = false;
    }
    
    protected StyleSheet getStyleSheet() {
        return ((HTMLDocument)this.getDocument()).getStyleSheet();
    }
    
    void updateInsets() {
        short n = (short)this.painter.getInset(1, this);
        short n2 = (short)this.painter.getInset(3, this);
        if (this.captionIndex != -1) {
            final View view = this.getView(this.captionIndex);
            final short n3 = (short)view.getPreferredSpan(1);
            final Object attribute = view.getAttributes().getAttribute(CSS.Attribute.CAPTION_SIDE);
            if (attribute != null && attribute.equals("bottom")) {
                n2 += n3;
            }
            else {
                n += n3;
            }
        }
        this.setInsets(n, (short)this.painter.getInset(2, this), n2, (short)this.painter.getInset(4, this));
    }
    
    protected void setPropertiesFromAttributes() {
        final StyleSheet styleSheet = this.getStyleSheet();
        this.attr = styleSheet.getViewAttributes(this);
        this.painter = styleSheet.getBoxPainter(this.attr);
        if (this.attr != null) {
            this.setInsets((short)this.painter.getInset(1, this), (short)this.painter.getInset(2, this), (short)this.painter.getInset(3, this), (short)this.painter.getInset(4, this));
            final CSS.LengthValue lengthValue = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.BORDER_SPACING);
            if (lengthValue != null) {
                this.cellSpacing = (int)lengthValue.getValue();
            }
            else {
                this.cellSpacing = 2;
            }
            final CSS.LengthValue lengthValue2 = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.BORDER_TOP_WIDTH);
            if (lengthValue2 != null) {
                this.borderWidth = (int)lengthValue2.getValue();
            }
            else {
                this.borderWidth = 0;
            }
        }
    }
    
    void updateGrid() {
        if (!this.gridValid) {
            this.relativeCells = false;
            this.multiRowCells = false;
            this.captionIndex = -1;
            this.rows.removeAllElements();
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                if (view instanceof RowView) {
                    this.rows.addElement((RowView)view);
                    final RowView rowView = (RowView)view;
                    rowView.clearFilledColumns();
                    rowView.rowIndex = this.rows.size() - 1;
                    rowView.viewIndex = i;
                }
                else {
                    final Object attribute = view.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
                    if (attribute instanceof HTML.Tag && attribute == HTML.Tag.CAPTION) {
                        this.captionIndex = i;
                    }
                }
            }
            int max = 0;
            for (int size = this.rows.size(), j = 0; j < size; ++j) {
                final RowView row = this.getRow(j);
                int n = 0;
                for (int k = 0; k < row.getViewCount(); ++k, ++n) {
                    final View view2 = row.getView(k);
                    if (!this.relativeCells) {
                        final CSS.LengthValue lengthValue = (CSS.LengthValue)view2.getAttributes().getAttribute(CSS.Attribute.WIDTH);
                        if (lengthValue != null && lengthValue.isPercentage()) {
                            this.relativeCells = true;
                        }
                    }
                    while (row.isFilled(n)) {
                        ++n;
                    }
                    final int rowsOccupied = this.getRowsOccupied(view2);
                    if (rowsOccupied > 1) {
                        this.multiRowCells = true;
                    }
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
                this.columnRequirements[n5].maximum = Integer.MAX_VALUE;
            }
            this.gridValid = true;
        }
    }
    
    void addFill(final int n, final int n2) {
        final RowView row = this.getRow(n);
        if (row != null) {
            row.fillColumn(n2);
        }
    }
    
    protected void layoutColumns(final int n, final int[] array, final int[] array2, final SizeRequirements[] array3) {
        Arrays.fill(array, 0);
        Arrays.fill(array2, 0);
        this.colIterator.setLayoutArrays(array, array2, n);
        CSS.calculateTiledLayout(this.colIterator, n);
    }
    
    void calculateColumnRequirements(final int n) {
        for (final SizeRequirements sizeRequirements : this.columnRequirements) {
            sizeRequirements.minimum = 0;
            sizeRequirements.preferred = 0;
            sizeRequirements.maximum = Integer.MAX_VALUE;
        }
        final Container container = this.getContainer();
        if (container != null) {
            if (container instanceof JTextComponent) {
                this.skipComments = !((JTextComponent)container).isEditable();
            }
            else {
                this.skipComments = true;
            }
        }
        boolean b = false;
        final int rowCount = this.getRowCount();
        for (int j = 0; j < rowCount; ++j) {
            final RowView row = this.getRow(j);
            int n2 = 0;
            for (int viewCount = row.getViewCount(), k = 0; k < viewCount; ++k) {
                final View view = row.getView(k);
                if (!this.skipComments || view instanceof CellView) {
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
                    ++n2;
                }
            }
        }
        if (b) {
            for (int l = 0; l < rowCount; ++l) {
                final RowView row2 = this.getRow(l);
                int n3 = 0;
                for (int viewCount2 = row2.getViewCount(), n4 = 0; n4 < viewCount2; ++n4) {
                    final View view2 = row2.getView(n4);
                    if (!this.skipComments || view2 instanceof CellView) {
                        while (row2.isFilled(n3)) {
                            ++n3;
                        }
                        final int columnsOccupied2 = this.getColumnsOccupied(view2);
                        if (columnsOccupied2 > 1) {
                            this.checkMultiColumnCell(n, n3, columnsOccupied2, view2);
                            n3 += columnsOccupied2 - 1;
                        }
                        ++n3;
                    }
                }
            }
        }
    }
    
    void checkSingleColumnCell(final int n, final int n2, final View view) {
        final SizeRequirements sizeRequirements = this.columnRequirements[n2];
        sizeRequirements.minimum = Math.max((int)view.getMinimumSpan(n), sizeRequirements.minimum);
        sizeRequirements.preferred = Math.max((int)view.getPreferredSpan(n), sizeRequirements.preferred);
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
                array[j] = this.columnRequirements[n2 + j];
            }
            final int[] array2 = new int[n3];
            SizeRequirements.calculateTiledPositions(n7, null, array, new int[n3], array2);
            for (int k = 0; k < n3; ++k) {
                final SizeRequirements sizeRequirements2 = array[k];
                sizeRequirements2.minimum = Math.max(array2[k], sizeRequirements2.minimum);
                sizeRequirements2.preferred = Math.max(sizeRequirements2.minimum, sizeRequirements2.preferred);
                sizeRequirements2.maximum = Math.max(sizeRequirements2.preferred, sizeRequirements2.maximum);
            }
        }
        final int n8 = (int)view.getPreferredSpan(n);
        if (n8 > n5) {
            final SizeRequirements[] array3 = new SizeRequirements[n3];
            for (int l = 0; l < n3; ++l) {
                array3[l] = this.columnRequirements[n2 + l];
            }
            final int[] array4 = new int[n3];
            SizeRequirements.calculateTiledPositions(n8, null, array3, new int[n3], array4);
            for (int n9 = 0; n9 < n3; ++n9) {
                final SizeRequirements sizeRequirements3 = array3[n9];
                sizeRequirements3.preferred = Math.max(array4[n9], sizeRequirements3.preferred);
                sizeRequirements3.maximum = Math.max(sizeRequirements3.preferred, sizeRequirements3.maximum);
            }
        }
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
        final int length = this.columnRequirements.length;
        for (int i = 0; i < length; ++i) {
            final SizeRequirements sizeRequirements2 = this.columnRequirements[i];
            n2 += sizeRequirements2.minimum;
            n3 += sizeRequirements2.preferred;
        }
        final int n4 = (length + 1) * this.cellSpacing + 2 * this.borderWidth;
        final long n5 = n2 + n4;
        final long n6 = n3 + n4;
        sizeRequirements.minimum = (int)n5;
        sizeRequirements.preferred = (int)n6;
        sizeRequirements.maximum = (int)n6;
        final AttributeSet attributes = this.getAttributes();
        if (BlockView.spanSetFromAttributes(n, sizeRequirements, (CSS.LengthValue)attributes.getAttribute(CSS.Attribute.WIDTH), null) && sizeRequirements.minimum < (int)n5) {
            final SizeRequirements sizeRequirements3 = sizeRequirements;
            final SizeRequirements sizeRequirements4 = sizeRequirements;
            final SizeRequirements sizeRequirements5 = sizeRequirements;
            final int maximum = (int)n5;
            sizeRequirements5.preferred = maximum;
            sizeRequirements4.minimum = maximum;
            sizeRequirements3.maximum = maximum;
        }
        this.totalColumnRequirements.minimum = sizeRequirements.minimum;
        this.totalColumnRequirements.preferred = sizeRequirements.preferred;
        this.totalColumnRequirements.maximum = sizeRequirements.maximum;
        final Object attribute = attributes.getAttribute(CSS.Attribute.TEXT_ALIGN);
        if (attribute != null) {
            final String string = attribute.toString();
            if (string.equals("left")) {
                sizeRequirements.alignment = 0.0f;
            }
            else if (string.equals("center")) {
                sizeRequirements.alignment = 0.5f;
            }
            else if (string.equals("right")) {
                sizeRequirements.alignment = 1.0f;
            }
            else {
                sizeRequirements.alignment = 0.0f;
            }
        }
        else {
            sizeRequirements.alignment = 0.0f;
        }
        return sizeRequirements;
    }
    
    @Override
    protected SizeRequirements calculateMajorAxisRequirements(final int n, SizeRequirements calculateTiledRequirements) {
        this.updateInsets();
        this.rowIterator.updateAdjustments();
        calculateTiledRequirements = CSS.calculateTiledRequirements(this.rowIterator, calculateTiledRequirements);
        calculateTiledRequirements.maximum = calculateTiledRequirements.preferred;
        return calculateTiledRequirements;
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
    protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
        this.rowIterator.setLayoutArrays(array, array2);
        CSS.calculateTiledLayout(this.rowIterator, n);
        if (this.captionIndex != -1) {
            array2[this.captionIndex] = (int)this.getView(this.captionIndex).getPreferredSpan(1);
            final short n3 = (short)this.painter.getInset(3, this);
            if (n3 != this.getBottomInset()) {
                array[this.captionIndex] = n + n3;
            }
            else {
                array[this.captionIndex] = -this.getTopInset();
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
    
    @Override
    public AttributeSet getAttributes() {
        if (this.attr == null) {
            this.attr = this.getStyleSheet().getViewAttributes(this);
        }
        return this.attr;
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Rectangle bounds = shape.getBounds();
        this.setSize((float)bounds.width, (float)bounds.height);
        if (this.captionIndex != -1) {
            final short n = (short)this.painter.getInset(1, this);
            final short n2 = (short)this.painter.getInset(3, this);
            if (n != this.getTopInset()) {
                final int n3 = this.getTopInset() - n;
                final Rectangle rectangle = bounds;
                rectangle.y += n3;
                final Rectangle rectangle2 = bounds;
                rectangle2.height -= n3;
            }
            else {
                final Rectangle rectangle3 = bounds;
                rectangle3.height -= this.getBottomInset() - n2;
            }
        }
        this.painter.paint(graphics, (float)bounds.x, (float)bounds.y, (float)bounds.width, (float)bounds.height, this);
        for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
            this.getView(i).paint(graphics, this.getChildAllocation(i, shape));
        }
    }
    
    @Override
    public void setParent(final View parent) {
        super.setParent(parent);
        if (parent != null) {
            this.setPropertiesFromAttributes();
        }
    }
    
    @Override
    public ViewFactory getViewFactory() {
        return this;
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.insertUpdate(documentEvent, shape, this);
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.removeUpdate(documentEvent, shape, this);
    }
    
    @Override
    public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        super.changedUpdate(documentEvent, shape, this);
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
    
    @Override
    public View create(final Element element) {
        final Object attribute = element.getAttributes().getAttribute(StyleConstants.NameAttribute);
        if (attribute instanceof HTML.Tag) {
            final HTML.Tag tag = (HTML.Tag)attribute;
            if (tag == HTML.Tag.TR) {
                return this.createTableRow(element);
            }
            if (tag == HTML.Tag.TD || tag == HTML.Tag.TH) {
                return new CellView(element);
            }
            if (tag == HTML.Tag.CAPTION) {
                return new ParagraphView(element);
            }
        }
        final View parent = this.getParent();
        if (parent != null) {
            final ViewFactory viewFactory = parent.getViewFactory();
            if (viewFactory != null) {
                return viewFactory.create(element);
            }
        }
        return null;
    }
    
    static {
        EMPTY = new BitSet();
    }
    
    class ColumnIterator implements CSS.LayoutIterator
    {
        private int col;
        private int[] percentages;
        private int[] adjustmentWeights;
        private int[] offsets;
        private int[] spans;
        
        void disablePercentages() {
            this.percentages = null;
        }
        
        private void updatePercentagesAndAdjustmentWeights(final int n) {
            this.adjustmentWeights = new int[TableView.this.columnRequirements.length];
            for (int i = 0; i < TableView.this.columnRequirements.length; ++i) {
                this.adjustmentWeights[i] = 0;
            }
            if (TableView.this.relativeCells) {
                this.percentages = new int[TableView.this.columnRequirements.length];
            }
            else {
                this.percentages = null;
            }
            for (int rowCount = TableView.this.getRowCount(), j = 0; j < rowCount; ++j) {
                final RowView row = TableView.this.getRow(j);
                for (int n2 = 0, viewCount = row.getViewCount(), k = 0; k < viewCount; ++k, ++n2) {
                    final View view = row.getView(k);
                    while (row.isFilled(n2)) {
                        ++n2;
                    }
                    TableView.this.getRowsOccupied(view);
                    final int columnsOccupied = TableView.this.getColumnsOccupied(view);
                    final CSS.LengthValue lengthValue = (CSS.LengthValue)view.getAttributes().getAttribute(CSS.Attribute.WIDTH);
                    if (lengthValue != null) {
                        final int n3 = (int)(lengthValue.getValue((float)n) / columnsOccupied + 0.5f);
                        for (int l = 0; l < columnsOccupied; ++l) {
                            if (lengthValue.isPercentage()) {
                                this.percentages[n2 + l] = Math.max(this.percentages[n2 + l], n3);
                                this.adjustmentWeights[n2 + l] = Math.max(this.adjustmentWeights[n2 + l], 2);
                            }
                            else {
                                this.adjustmentWeights[n2 + l] = Math.max(this.adjustmentWeights[n2 + l], 1);
                            }
                        }
                    }
                    n2 += columnsOccupied - 1;
                }
            }
        }
        
        public void setLayoutArrays(final int[] offsets, final int[] spans, final int n) {
            this.offsets = offsets;
            this.spans = spans;
            this.updatePercentagesAndAdjustmentWeights(n);
        }
        
        @Override
        public int getCount() {
            return TableView.this.columnRequirements.length;
        }
        
        @Override
        public void setIndex(final int col) {
            this.col = col;
        }
        
        @Override
        public void setOffset(final int n) {
            this.offsets[this.col] = n;
        }
        
        @Override
        public int getOffset() {
            return this.offsets[this.col];
        }
        
        @Override
        public void setSpan(final int n) {
            this.spans[this.col] = n;
        }
        
        @Override
        public int getSpan() {
            return this.spans[this.col];
        }
        
        @Override
        public float getMinimumSpan(final float n) {
            return (float)TableView.this.columnRequirements[this.col].minimum;
        }
        
        @Override
        public float getPreferredSpan(final float n) {
            if (this.percentages != null && this.percentages[this.col] != 0) {
                return (float)Math.max(this.percentages[this.col], TableView.this.columnRequirements[this.col].minimum);
            }
            return (float)TableView.this.columnRequirements[this.col].preferred;
        }
        
        @Override
        public float getMaximumSpan(final float n) {
            return (float)TableView.this.columnRequirements[this.col].maximum;
        }
        
        @Override
        public float getBorderWidth() {
            return (float)TableView.this.borderWidth;
        }
        
        @Override
        public float getLeadingCollapseSpan() {
            return (float)TableView.this.cellSpacing;
        }
        
        @Override
        public float getTrailingCollapseSpan() {
            return (float)TableView.this.cellSpacing;
        }
        
        @Override
        public int getAdjustmentWeight() {
            return this.adjustmentWeights[this.col];
        }
    }
    
    class RowIterator implements CSS.LayoutIterator
    {
        private int row;
        private int[] adjustments;
        private int[] offsets;
        private int[] spans;
        
        void updateAdjustments() {
            final int n = 1;
            if (TableView.this.multiRowCells) {
                final int rowCount = TableView.this.getRowCount();
                this.adjustments = new int[rowCount];
                for (int i = 0; i < rowCount; ++i) {
                    final RowView row = TableView.this.getRow(i);
                    if (row.multiRowCells) {
                        for (int viewCount = row.getViewCount(), j = 0; j < viewCount; ++j) {
                            final View view = row.getView(j);
                            final int rowsOccupied = TableView.this.getRowsOccupied(view);
                            if (rowsOccupied > 1) {
                                this.adjustMultiRowSpan((int)view.getPreferredSpan(n), rowsOccupied, i);
                            }
                        }
                    }
                }
            }
            else {
                this.adjustments = null;
            }
        }
        
        void adjustMultiRowSpan(final int n, int n2, final int n3) {
            if (n3 + n2 > this.getCount()) {
                n2 = this.getCount() - n3;
                if (n2 < 1) {
                    return;
                }
            }
            int n4 = 0;
            for (int i = 0; i < n2; ++i) {
                n4 += (int)TableView.this.getRow(n3 + i).getPreferredSpan(1);
            }
            if (n > n4) {
                final int n5 = n - n4;
                final int n6 = n5 / n2;
                final int n7 = n6 + (n5 - n6 * n2);
                TableView.this.getRow(n3);
                this.adjustments[n3] = Math.max(this.adjustments[n3], n7);
                for (int j = 1; j < n2; ++j) {
                    this.adjustments[n3 + j] = Math.max(this.adjustments[n3 + j], n6);
                }
            }
        }
        
        void setLayoutArrays(final int[] offsets, final int[] spans) {
            this.offsets = offsets;
            this.spans = spans;
        }
        
        @Override
        public void setOffset(final int n) {
            final RowView row = TableView.this.getRow(this.row);
            if (row != null) {
                this.offsets[row.viewIndex] = n;
            }
        }
        
        @Override
        public int getOffset() {
            final RowView row = TableView.this.getRow(this.row);
            if (row != null) {
                return this.offsets[row.viewIndex];
            }
            return 0;
        }
        
        @Override
        public void setSpan(final int n) {
            final RowView row = TableView.this.getRow(this.row);
            if (row != null) {
                this.spans[row.viewIndex] = n;
            }
        }
        
        @Override
        public int getSpan() {
            final RowView row = TableView.this.getRow(this.row);
            if (row != null) {
                return this.spans[row.viewIndex];
            }
            return 0;
        }
        
        @Override
        public int getCount() {
            return TableView.this.rows.size();
        }
        
        @Override
        public void setIndex(final int row) {
            this.row = row;
        }
        
        @Override
        public float getMinimumSpan(final float n) {
            return this.getPreferredSpan(n);
        }
        
        @Override
        public float getPreferredSpan(final float n) {
            final RowView row = TableView.this.getRow(this.row);
            if (row != null) {
                return row.getPreferredSpan(TableView.this.getAxis()) + ((this.adjustments != null) ? this.adjustments[this.row] : 0);
            }
            return 0.0f;
        }
        
        @Override
        public float getMaximumSpan(final float n) {
            return this.getPreferredSpan(n);
        }
        
        @Override
        public float getBorderWidth() {
            return (float)TableView.this.borderWidth;
        }
        
        @Override
        public float getLeadingCollapseSpan() {
            return (float)TableView.this.cellSpacing;
        }
        
        @Override
        public float getTrailingCollapseSpan() {
            return (float)TableView.this.cellSpacing;
        }
        
        @Override
        public int getAdjustmentWeight() {
            return 0;
        }
    }
    
    public class RowView extends BoxView
    {
        private StyleSheet.BoxPainter painter;
        private AttributeSet attr;
        BitSet fillColumns;
        int rowIndex;
        int viewIndex;
        boolean multiRowCells;
        
        public RowView(final Element element) {
            super(element, 0);
            this.fillColumns = new BitSet();
            this.setPropertiesFromAttributes();
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
        public AttributeSet getAttributes() {
            return this.attr;
        }
        
        View findViewAtPoint(final int n, final int n2, final Rectangle rectangle) {
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                if (this.getChildAllocation(i, rectangle).contains(n, n2)) {
                    this.childAllocation(i, rectangle);
                    return this.getView(i);
                }
            }
            return null;
        }
        
        protected StyleSheet getStyleSheet() {
            return ((HTMLDocument)this.getDocument()).getStyleSheet();
        }
        
        @Override
        public void preferenceChanged(final View view, final boolean b, final boolean b2) {
            super.preferenceChanged(view, b, b2);
            if (TableView.this.multiRowCells && b2) {
                for (int i = this.rowIndex - 1; i >= 0; --i) {
                    final RowView row = TableView.this.getRow(i);
                    if (row.multiRowCells) {
                        row.preferenceChanged(null, false, true);
                        break;
                    }
                }
            }
        }
        
        @Override
        protected SizeRequirements calculateMajorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
            final SizeRequirements sizeRequirements2 = new SizeRequirements();
            sizeRequirements2.minimum = TableView.this.totalColumnRequirements.minimum;
            sizeRequirements2.maximum = TableView.this.totalColumnRequirements.maximum;
            sizeRequirements2.preferred = TableView.this.totalColumnRequirements.preferred;
            sizeRequirements2.alignment = 0.0f;
            return sizeRequirements2;
        }
        
        @Override
        public float getMinimumSpan(final int n) {
            float minimumSpan;
            if (n == 0) {
                minimumSpan = (float)(TableView.this.totalColumnRequirements.minimum + this.getLeftInset() + this.getRightInset());
            }
            else {
                minimumSpan = super.getMinimumSpan(n);
            }
            return minimumSpan;
        }
        
        @Override
        public float getMaximumSpan(final int n) {
            float maximumSpan;
            if (n == 0) {
                maximumSpan = 2.14748365E9f;
            }
            else {
                maximumSpan = super.getMaximumSpan(n);
            }
            return maximumSpan;
        }
        
        @Override
        public float getPreferredSpan(final int n) {
            float preferredSpan;
            if (n == 0) {
                preferredSpan = (float)(TableView.this.totalColumnRequirements.preferred + this.getLeftInset() + this.getRightInset());
            }
            else {
                preferredSpan = super.getPreferredSpan(n);
            }
            return preferredSpan;
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            super.changedUpdate(documentEvent, shape, viewFactory);
            final int offset = documentEvent.getOffset();
            if (offset <= this.getStartOffset() && offset + documentEvent.getLength() >= this.getEndOffset()) {
                this.setPropertiesFromAttributes();
            }
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            final Rectangle rectangle = (Rectangle)shape;
            this.painter.paint(graphics, (float)rectangle.x, (float)rectangle.y, (float)rectangle.width, (float)rectangle.height, this);
            super.paint(graphics, rectangle);
        }
        
        @Override
        public void replace(final int n, final int n2, final View[] array) {
            super.replace(n, n2, array);
            TableView.this.invalidateGrid();
        }
        
        @Override
        protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements sizeRequirements) {
            long max = 0L;
            long max2 = 0L;
            long n2 = 0L;
            this.multiRowCells = false;
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                if (TableView.this.getRowsOccupied(view) > 1) {
                    this.multiRowCells = true;
                    n2 = Math.max((int)view.getMaximumSpan(n), n2);
                }
                else {
                    max = Math.max((int)view.getMinimumSpan(n), max);
                    max2 = Math.max((int)view.getPreferredSpan(n), max2);
                    n2 = Math.max((int)view.getMaximumSpan(n), n2);
                }
            }
            if (sizeRequirements == null) {
                sizeRequirements = new SizeRequirements();
                sizeRequirements.alignment = 0.5f;
            }
            sizeRequirements.preferred = (int)max2;
            sizeRequirements.minimum = (int)max;
            sizeRequirements.maximum = (int)n2;
            return sizeRequirements;
        }
        
        @Override
        protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
            int n3 = 0;
            for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
                final View view = this.getView(i);
                if (!TableView.this.skipComments || view instanceof CellView) {
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
                                final int n5 = i;
                                array2[n5] += TableView.this.cellSpacing;
                            }
                        }
                        n3 += columnsOccupied - 1;
                    }
                    ++n3;
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
                    array2[i] = TableView.this.getMultiRowSpan(this.rowIndex, Math.min(this.rowIndex + rowsOccupied - 1, TableView.this.getRowCount() - 1));
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
        
        void setPropertiesFromAttributes() {
            final StyleSheet styleSheet = this.getStyleSheet();
            this.attr = styleSheet.getViewAttributes(this);
            this.painter = styleSheet.getBoxPainter(this.attr);
        }
    }
    
    class CellView extends BlockView
    {
        public CellView(final Element element) {
            super(element, 1);
        }
        
        @Override
        protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
            super.layoutMajorAxis(n, n2, array, array2);
            int n3 = 0;
            final int length = array2.length;
            for (int i = 0; i < length; ++i) {
                n3 += array2[i];
            }
            int n4 = 0;
            if (n3 < n) {
                String s = (String)this.getElement().getAttributes().getAttribute(HTML.Attribute.VALIGN);
                if (s == null) {
                    s = (String)this.getElement().getParentElement().getAttributes().getAttribute(HTML.Attribute.VALIGN);
                }
                if (s == null || s.equals("middle")) {
                    n4 = (n - n3) / 2;
                }
                else if (s.equals("bottom")) {
                    n4 = n - n3;
                }
            }
            if (n4 != 0) {
                for (int j = 0; j < length; ++j) {
                    final int n5 = j;
                    array[n5] += n4;
                }
            }
        }
        
        @Override
        protected SizeRequirements calculateMajorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
            final SizeRequirements calculateMajorAxisRequirements = super.calculateMajorAxisRequirements(n, sizeRequirements);
            calculateMajorAxisRequirements.maximum = Integer.MAX_VALUE;
            return calculateMajorAxisRequirements;
        }
        
        @Override
        protected SizeRequirements calculateMinorAxisRequirements(final int n, final SizeRequirements sizeRequirements) {
            final SizeRequirements calculateMinorAxisRequirements = super.calculateMinorAxisRequirements(n, sizeRequirements);
            final int viewCount = this.getViewCount();
            int max = 0;
            for (int i = 0; i < viewCount; ++i) {
                max = Math.max((int)this.getView(i).getMinimumSpan(n), max);
            }
            calculateMinorAxisRequirements.minimum = Math.min(calculateMinorAxisRequirements.minimum, max);
            return calculateMinorAxisRequirements;
        }
    }
}
