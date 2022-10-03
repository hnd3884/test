package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import sun.swing.SwingUtilities2;
import javax.swing.table.TableModel;
import javax.swing.RowSorter;
import java.awt.event.FocusEvent;
import java.util.Enumeration;
import javax.swing.table.TableCellRenderer;
import java.awt.Graphics;
import javax.swing.table.TableColumnModel;
import java.awt.Point;
import java.awt.Dimension;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;
import java.awt.Rectangle;
import javax.swing.JTable;
import java.awt.Container;
import javax.swing.JScrollPane;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.LookAndFeel;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.event.FocusListener;
import javax.swing.event.MouseInputListener;
import javax.swing.CellRendererPane;
import javax.swing.table.JTableHeader;
import java.awt.Cursor;
import javax.swing.plaf.TableHeaderUI;

public class BasicTableHeaderUI extends TableHeaderUI
{
    private static Cursor resizeCursor;
    protected JTableHeader header;
    protected CellRendererPane rendererPane;
    protected MouseInputListener mouseInputListener;
    private int rolloverColumn;
    private int selectedColumnIndex;
    private static FocusListener focusListener;
    
    public BasicTableHeaderUI() {
        this.rolloverColumn = -1;
        this.selectedColumnIndex = 0;
    }
    
    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTableHeaderUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.header = (JTableHeader)component;
        this.rendererPane = new CellRendererPane();
        this.header.add(this.rendererPane);
        this.installDefaults();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(this.header, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
        LookAndFeel.installProperty(this.header, "opaque", Boolean.TRUE);
    }
    
    protected void installListeners() {
        this.mouseInputListener = this.createMouseInputListener();
        this.header.addMouseListener(this.mouseInputListener);
        this.header.addMouseMotionListener(this.mouseInputListener);
        this.header.addFocusListener(BasicTableHeaderUI.focusListener);
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.header, 1, (InputMap)DefaultLookup.get(this.header, this, "TableHeader.ancestorInputMap"));
        LazyActionMap.installLazyActionMap(this.header, BasicTableHeaderUI.class, "TableHeader.actionMap");
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        this.header.remove(this.rendererPane);
        this.rendererPane = null;
        this.header = null;
    }
    
    protected void uninstallDefaults() {
    }
    
    protected void uninstallListeners() {
        this.header.removeMouseListener(this.mouseInputListener);
        this.header.removeMouseMotionListener(this.mouseInputListener);
        this.mouseInputListener = null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.header, 0, null);
        SwingUtilities.replaceUIActionMap(this.header, null);
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("toggleSortOrder"));
        lazyActionMap.put(new Actions("selectColumnToLeft"));
        lazyActionMap.put(new Actions("selectColumnToRight"));
        lazyActionMap.put(new Actions("moveColumnLeft"));
        lazyActionMap.put(new Actions("moveColumnRight"));
        lazyActionMap.put(new Actions("resizeLeft"));
        lazyActionMap.put(new Actions("resizeRight"));
        lazyActionMap.put(new Actions("focusTable"));
    }
    
    protected int getRolloverColumn() {
        return this.rolloverColumn;
    }
    
    protected void rolloverColumnUpdated(final int n, final int n2) {
    }
    
    private void updateRolloverColumn(final MouseEvent mouseEvent) {
        if (this.header.getDraggedColumn() == null && this.header.contains(mouseEvent.getPoint())) {
            final int columnAtPoint = this.header.columnAtPoint(mouseEvent.getPoint());
            if (columnAtPoint != this.rolloverColumn) {
                this.rolloverColumnUpdated(this.rolloverColumn, this.rolloverColumn = columnAtPoint);
            }
        }
    }
    
    private int selectNextColumn(final boolean b) {
        int selectedColumnIndex = this.getSelectedColumnIndex();
        if (selectedColumnIndex < this.header.getColumnModel().getColumnCount() - 1) {
            ++selectedColumnIndex;
            if (b) {
                this.selectColumn(selectedColumnIndex);
            }
        }
        return selectedColumnIndex;
    }
    
    private int selectPreviousColumn(final boolean b) {
        int selectedColumnIndex = this.getSelectedColumnIndex();
        if (selectedColumnIndex > 0) {
            --selectedColumnIndex;
            if (b) {
                this.selectColumn(selectedColumnIndex);
            }
        }
        return selectedColumnIndex;
    }
    
    void selectColumn(final int n) {
        this.selectColumn(n, true);
    }
    
    void selectColumn(final int selectedColumnIndex, final boolean b) {
        this.header.repaint(this.header.getHeaderRect(this.selectedColumnIndex));
        this.selectedColumnIndex = selectedColumnIndex;
        this.header.repaint(this.header.getHeaderRect(selectedColumnIndex));
        if (b) {
            this.scrollToColumn(selectedColumnIndex);
        }
    }
    
    private void scrollToColumn(final int n) {
        final Container parent;
        final JTable table;
        if (this.header.getParent() == null || (parent = this.header.getParent().getParent()) == null || !(parent instanceof JScrollPane) || (table = this.header.getTable()) == null) {
            return;
        }
        final Rectangle visibleRect = table.getVisibleRect();
        final Rectangle cellRect = table.getCellRect(0, n, true);
        visibleRect.x = cellRect.x;
        visibleRect.width = cellRect.width;
        table.scrollRectToVisible(visibleRect);
    }
    
    private int getSelectedColumnIndex() {
        final int columnCount = this.header.getColumnModel().getColumnCount();
        if (this.selectedColumnIndex >= columnCount && columnCount > 0) {
            this.selectedColumnIndex = columnCount - 1;
        }
        return this.selectedColumnIndex;
    }
    
    private static boolean canResize(final TableColumn tableColumn, final JTableHeader tableHeader) {
        return tableColumn != null && tableHeader.getResizingAllowed() && tableColumn.getResizable();
    }
    
    private int changeColumnWidth(final TableColumn tableColumn, final JTableHeader tableHeader, final int n, final int width) {
        tableColumn.setWidth(width);
        final Container parent;
        final JTable table;
        if (tableHeader.getParent() == null || (parent = tableHeader.getParent().getParent()) == null || !(parent instanceof JScrollPane) || (table = tableHeader.getTable()) == null) {
            return 0;
        }
        if (!parent.getComponentOrientation().isLeftToRight() && !tableHeader.getComponentOrientation().isLeftToRight()) {
            final JViewport viewport = ((JScrollPane)parent).getViewport();
            final int width2 = viewport.getWidth();
            final int n2 = width - n;
            final int n3 = table.getWidth() + n2;
            final Dimension size2;
            final Dimension size = size2 = table.getSize();
            size2.width += n2;
            table.setSize(size);
            if (n3 >= width2 && table.getAutoResizeMode() == 0) {
                final Point viewPosition = viewport.getViewPosition();
                viewPosition.x = Math.max(0, Math.min(n3 - width2, viewPosition.x + n2));
                viewport.setViewPosition(viewPosition);
                return n2;
            }
        }
        return 0;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        int n3 = -1;
        final TableColumnModel columnModel = this.header.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i) {
            columnModel.getColumn(i);
            final Component headerRenderer = this.getHeaderRenderer(i);
            final int baseline = headerRenderer.getBaseline(headerRenderer.getPreferredSize().width, n2);
            if (baseline >= 0) {
                if (n3 == -1) {
                    n3 = baseline;
                }
                else if (n3 != baseline) {
                    n3 = -1;
                    break;
                }
            }
        }
        return n3;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (this.header.getColumnModel().getColumnCount() <= 0) {
            return;
        }
        final boolean leftToRight = this.header.getComponentOrientation().isLeftToRight();
        final Rectangle clipBounds = graphics.getClipBounds();
        final Point location = clipBounds.getLocation();
        final Point point = new Point(clipBounds.x + clipBounds.width - 1, clipBounds.y);
        final TableColumnModel columnModel = this.header.getColumnModel();
        int columnAtPoint = this.header.columnAtPoint(leftToRight ? location : point);
        int columnAtPoint2 = this.header.columnAtPoint(leftToRight ? point : location);
        if (columnAtPoint == -1) {
            columnAtPoint = 0;
        }
        if (columnAtPoint2 == -1) {
            columnAtPoint2 = columnModel.getColumnCount() - 1;
        }
        final TableColumn draggedColumn = this.header.getDraggedColumn();
        final Rectangle headerRect = this.header.getHeaderRect(leftToRight ? columnAtPoint : columnAtPoint2);
        if (leftToRight) {
            for (int i = columnAtPoint; i <= columnAtPoint2; ++i) {
                final TableColumn column = columnModel.getColumn(i);
                final int width = column.getWidth();
                headerRect.width = width;
                if (column != draggedColumn) {
                    this.paintCell(graphics, headerRect, i);
                }
                final Rectangle rectangle = headerRect;
                rectangle.x += width;
            }
        }
        else {
            for (int j = columnAtPoint2; j >= columnAtPoint; --j) {
                final TableColumn column2 = columnModel.getColumn(j);
                final int width2 = column2.getWidth();
                headerRect.width = width2;
                if (column2 != draggedColumn) {
                    this.paintCell(graphics, headerRect, j);
                }
                final Rectangle rectangle2 = headerRect;
                rectangle2.x += width2;
            }
        }
        if (draggedColumn != null) {
            final int viewIndexForColumn = this.viewIndexForColumn(draggedColumn);
            final Rectangle headerRect2 = this.header.getHeaderRect(viewIndexForColumn);
            graphics.setColor(this.header.getParent().getBackground());
            graphics.fillRect(headerRect2.x, headerRect2.y, headerRect2.width, headerRect2.height);
            final Rectangle rectangle3 = headerRect2;
            rectangle3.x += this.header.getDraggedDistance();
            graphics.setColor(this.header.getBackground());
            graphics.fillRect(headerRect2.x, headerRect2.y, headerRect2.width, headerRect2.height);
            this.paintCell(graphics, headerRect2, viewIndexForColumn);
        }
        this.rendererPane.removeAll();
    }
    
    private Component getHeaderRenderer(final int n) {
        final TableColumn column = this.header.getColumnModel().getColumn(n);
        TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
        if (tableCellRenderer == null) {
            tableCellRenderer = this.header.getDefaultRenderer();
        }
        return tableCellRenderer.getTableCellRendererComponent(this.header.getTable(), column.getHeaderValue(), false, !this.header.isPaintingForPrint() && n == this.getSelectedColumnIndex() && this.header.hasFocus(), -1, n);
    }
    
    private void paintCell(final Graphics graphics, final Rectangle rectangle, final int n) {
        this.rendererPane.paintComponent(graphics, this.getHeaderRenderer(n), this.header, rectangle.x, rectangle.y, rectangle.width, rectangle.height, true);
    }
    
    private int viewIndexForColumn(final TableColumn tableColumn) {
        final TableColumnModel columnModel = this.header.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i) {
            if (columnModel.getColumn(i) == tableColumn) {
                return i;
            }
        }
        return -1;
    }
    
    private int getHeaderHeight() {
        int max = 0;
        int n = 0;
        final TableColumnModel columnModel = this.header.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i) {
            final TableColumn column = columnModel.getColumn(i);
            final boolean b = column.getHeaderRenderer() == null;
            if (!b || n == 0) {
                final int height = this.getHeaderRenderer(i).getPreferredSize().height;
                max = Math.max(max, height);
                if (b && height > 0) {
                    final Object headerValue = column.getHeaderValue();
                    if (headerValue != null) {
                        final String string = headerValue.toString();
                        if (string != null && !string.equals("")) {
                            n = 1;
                        }
                    }
                }
            }
        }
        return max;
    }
    
    private Dimension createHeaderSize(long n) {
        if (n > 2147483647L) {
            n = 2147483647L;
        }
        return new Dimension((int)n, this.getHeaderHeight());
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        long n = 0L;
        final Enumeration<TableColumn> columns = this.header.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            n += columns.nextElement().getMinWidth();
        }
        return this.createHeaderSize(n);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        long n = 0L;
        final Enumeration<TableColumn> columns = this.header.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            n += columns.nextElement().getPreferredWidth();
        }
        return this.createHeaderSize(n);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        long n = 0L;
        final Enumeration<TableColumn> columns = this.header.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            n += columns.nextElement().getMaxWidth();
        }
        return this.createHeaderSize(n);
    }
    
    static {
        BasicTableHeaderUI.resizeCursor = Cursor.getPredefinedCursor(11);
        BasicTableHeaderUI.focusListener = new FocusListener() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                this.repaintHeader(focusEvent.getSource());
            }
            
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                this.repaintHeader(focusEvent.getSource());
            }
            
            private void repaintHeader(final Object o) {
                if (o instanceof JTableHeader) {
                    final JTableHeader tableHeader = (JTableHeader)o;
                    final BasicTableHeaderUI basicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(tableHeader.getUI(), BasicTableHeaderUI.class);
                    if (basicTableHeaderUI == null) {
                        return;
                    }
                    tableHeader.repaint(tableHeader.getHeaderRect(basicTableHeaderUI.getSelectedColumnIndex()));
                }
            }
        };
    }
    
    public class MouseInputHandler implements MouseInputListener
    {
        private int mouseXOffset;
        private Cursor otherCursor;
        
        public MouseInputHandler() {
            this.otherCursor = BasicTableHeaderUI.resizeCursor;
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            if (mouseEvent.getClickCount() % 2 == 1 && SwingUtilities.isLeftMouseButton(mouseEvent)) {
                final JTable table = BasicTableHeaderUI.this.header.getTable();
                final RowSorter<? extends TableModel> rowSorter;
                if (table != null && (rowSorter = table.getRowSorter()) != null) {
                    final int columnAtPoint = BasicTableHeaderUI.this.header.columnAtPoint(mouseEvent.getPoint());
                    if (columnAtPoint != -1) {
                        rowSorter.toggleSortOrder(table.convertColumnIndexToModel(columnAtPoint));
                    }
                }
            }
        }
        
        private TableColumn getResizingColumn(final Point point) {
            return this.getResizingColumn(point, BasicTableHeaderUI.this.header.columnAtPoint(point));
        }
        
        private TableColumn getResizingColumn(final Point point, final int n) {
            if (n == -1) {
                return null;
            }
            final Rectangle headerRect = BasicTableHeaderUI.this.header.getHeaderRect(n);
            headerRect.grow(-3, 0);
            if (headerRect.contains(point)) {
                return null;
            }
            final int n2 = headerRect.x + headerRect.width / 2;
            int n3;
            if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
                n3 = ((point.x < n2) ? (n - 1) : n);
            }
            else {
                n3 = ((point.x < n2) ? n : (n - 1));
            }
            if (n3 == -1) {
                return null;
            }
            return BasicTableHeaderUI.this.header.getColumnModel().getColumn(n3);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            BasicTableHeaderUI.this.header.setDraggedColumn(null);
            BasicTableHeaderUI.this.header.setResizingColumn(null);
            BasicTableHeaderUI.this.header.setDraggedDistance(0);
            final Point point = mouseEvent.getPoint();
            final TableColumnModel columnModel = BasicTableHeaderUI.this.header.getColumnModel();
            final int columnAtPoint = BasicTableHeaderUI.this.header.columnAtPoint(point);
            if (columnAtPoint != -1) {
                final TableColumn resizingColumn = this.getResizingColumn(point, columnAtPoint);
                if (canResize(resizingColumn, BasicTableHeaderUI.this.header)) {
                    BasicTableHeaderUI.this.header.setResizingColumn(resizingColumn);
                    if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
                        this.mouseXOffset = point.x - resizingColumn.getWidth();
                    }
                    else {
                        this.mouseXOffset = point.x + resizingColumn.getWidth();
                    }
                }
                else if (BasicTableHeaderUI.this.header.getReorderingAllowed()) {
                    BasicTableHeaderUI.this.header.setDraggedColumn(columnModel.getColumn(columnAtPoint));
                    this.mouseXOffset = point.x;
                }
            }
            if (BasicTableHeaderUI.this.header.getReorderingAllowed()) {
                final int access$300 = BasicTableHeaderUI.this.rolloverColumn;
                BasicTableHeaderUI.this.rolloverColumn = -1;
                BasicTableHeaderUI.this.rolloverColumnUpdated(access$300, BasicTableHeaderUI.this.rolloverColumn);
            }
        }
        
        private void swapCursor() {
            final Cursor cursor = BasicTableHeaderUI.this.header.getCursor();
            BasicTableHeaderUI.this.header.setCursor(this.otherCursor);
            this.otherCursor = cursor;
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            if (canResize(this.getResizingColumn(mouseEvent.getPoint()), BasicTableHeaderUI.this.header) != (BasicTableHeaderUI.this.header.getCursor() == BasicTableHeaderUI.resizeCursor)) {
                this.swapCursor();
            }
            BasicTableHeaderUI.this.updateRolloverColumn(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            final int x = mouseEvent.getX();
            final TableColumn resizingColumn = BasicTableHeaderUI.this.header.getResizingColumn();
            final TableColumn draggedColumn = BasicTableHeaderUI.this.header.getDraggedColumn();
            final boolean leftToRight = BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight();
            if (resizingColumn != null) {
                final int width = resizingColumn.getWidth();
                int n;
                if (leftToRight) {
                    n = x - this.mouseXOffset;
                }
                else {
                    n = this.mouseXOffset - x;
                }
                this.mouseXOffset += BasicTableHeaderUI.this.changeColumnWidth(resizingColumn, BasicTableHeaderUI.this.header, width, n);
            }
            else if (draggedColumn != null) {
                final TableColumnModel columnModel = BasicTableHeaderUI.this.header.getColumnModel();
                final int n2 = x - this.mouseXOffset;
                final int n3 = (n2 < 0) ? -1 : 1;
                final int access$600 = BasicTableHeaderUI.this.viewIndexForColumn(draggedColumn);
                final int n4 = access$600 + (leftToRight ? n3 : (-n3));
                if (0 <= n4 && n4 < columnModel.getColumnCount()) {
                    final int width2 = columnModel.getColumn(n4).getWidth();
                    if (Math.abs(n2) > width2 / 2) {
                        this.mouseXOffset += n3 * width2;
                        BasicTableHeaderUI.this.header.setDraggedDistance(n2 - n3 * width2);
                        final int convertColumnIndexToModel = SwingUtilities2.convertColumnIndexToModel(BasicTableHeaderUI.this.header.getColumnModel(), BasicTableHeaderUI.this.getSelectedColumnIndex());
                        columnModel.moveColumn(access$600, n4);
                        BasicTableHeaderUI.this.selectColumn(SwingUtilities2.convertColumnIndexToView(BasicTableHeaderUI.this.header.getColumnModel(), convertColumnIndexToModel), false);
                        return;
                    }
                }
                this.setDraggedDistance(n2, access$600);
            }
            BasicTableHeaderUI.this.updateRolloverColumn(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            this.setDraggedDistance(0, BasicTableHeaderUI.this.viewIndexForColumn(BasicTableHeaderUI.this.header.getDraggedColumn()));
            BasicTableHeaderUI.this.header.setResizingColumn(null);
            BasicTableHeaderUI.this.header.setDraggedColumn(null);
            BasicTableHeaderUI.this.updateRolloverColumn(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            BasicTableHeaderUI.this.updateRolloverColumn(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (!BasicTableHeaderUI.this.header.isEnabled()) {
                return;
            }
            final int access$300 = BasicTableHeaderUI.this.rolloverColumn;
            BasicTableHeaderUI.this.rolloverColumn = -1;
            BasicTableHeaderUI.this.rolloverColumnUpdated(access$300, BasicTableHeaderUI.this.rolloverColumn);
        }
        
        private void setDraggedDistance(final int draggedDistance, final int n) {
            BasicTableHeaderUI.this.header.setDraggedDistance(draggedDistance);
            if (n != -1) {
                BasicTableHeaderUI.this.header.getColumnModel().moveColumn(n, n);
            }
        }
    }
    
    private static class Actions extends UIAction
    {
        public static final String TOGGLE_SORT_ORDER = "toggleSortOrder";
        public static final String SELECT_COLUMN_TO_LEFT = "selectColumnToLeft";
        public static final String SELECT_COLUMN_TO_RIGHT = "selectColumnToRight";
        public static final String MOVE_COLUMN_LEFT = "moveColumnLeft";
        public static final String MOVE_COLUMN_RIGHT = "moveColumnRight";
        public static final String RESIZE_LEFT = "resizeLeft";
        public static final String RESIZE_RIGHT = "resizeRight";
        public static final String FOCUS_TABLE = "focusTable";
        
        public Actions(final String s) {
            super(s);
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            if (o instanceof JTableHeader) {
                final JTableHeader tableHeader = (JTableHeader)o;
                final TableColumnModel columnModel = tableHeader.getColumnModel();
                if (columnModel.getColumnCount() <= 0) {
                    return false;
                }
                final String name = this.getName();
                final BasicTableHeaderUI basicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(tableHeader.getUI(), BasicTableHeaderUI.class);
                if (basicTableHeaderUI != null) {
                    if (name == "moveColumnLeft") {
                        return tableHeader.getReorderingAllowed() && this.maybeMoveColumn(true, tableHeader, basicTableHeaderUI, false);
                    }
                    if (name == "moveColumnRight") {
                        return tableHeader.getReorderingAllowed() && this.maybeMoveColumn(false, tableHeader, basicTableHeaderUI, false);
                    }
                    if (name == "resizeLeft" || name == "resizeRight") {
                        return canResize(columnModel.getColumn(basicTableHeaderUI.getSelectedColumnIndex()), tableHeader);
                    }
                    if (name == "focusTable") {
                        return tableHeader.getTable() != null;
                    }
                }
            }
            return true;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JTableHeader tableHeader = (JTableHeader)actionEvent.getSource();
            final BasicTableHeaderUI basicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(tableHeader.getUI(), BasicTableHeaderUI.class);
            if (basicTableHeaderUI == null) {
                return;
            }
            final String name = this.getName();
            if ("toggleSortOrder" == name) {
                final JTable table = tableHeader.getTable();
                final RowSorter<? extends TableModel> rowSorter = (table == null) ? null : table.getRowSorter();
                if (rowSorter != null) {
                    rowSorter.toggleSortOrder(table.convertColumnIndexToModel(basicTableHeaderUI.getSelectedColumnIndex()));
                }
            }
            else if ("selectColumnToLeft" == name) {
                if (tableHeader.getComponentOrientation().isLeftToRight()) {
                    basicTableHeaderUI.selectPreviousColumn(true);
                }
                else {
                    basicTableHeaderUI.selectNextColumn(true);
                }
            }
            else if ("selectColumnToRight" == name) {
                if (tableHeader.getComponentOrientation().isLeftToRight()) {
                    basicTableHeaderUI.selectNextColumn(true);
                }
                else {
                    basicTableHeaderUI.selectPreviousColumn(true);
                }
            }
            else if ("moveColumnLeft" == name) {
                this.moveColumn(true, tableHeader, basicTableHeaderUI);
            }
            else if ("moveColumnRight" == name) {
                this.moveColumn(false, tableHeader, basicTableHeaderUI);
            }
            else if ("resizeLeft" == name) {
                this.resize(true, tableHeader, basicTableHeaderUI);
            }
            else if ("resizeRight" == name) {
                this.resize(false, tableHeader, basicTableHeaderUI);
            }
            else if ("focusTable" == name) {
                final JTable table2 = tableHeader.getTable();
                if (table2 != null) {
                    table2.requestFocusInWindow();
                }
            }
        }
        
        private void moveColumn(final boolean b, final JTableHeader tableHeader, final BasicTableHeaderUI basicTableHeaderUI) {
            this.maybeMoveColumn(b, tableHeader, basicTableHeaderUI, true);
        }
        
        private boolean maybeMoveColumn(final boolean b, final JTableHeader tableHeader, final BasicTableHeaderUI basicTableHeaderUI, final boolean b2) {
            final int access$000 = basicTableHeaderUI.getSelectedColumnIndex();
            int n;
            if (tableHeader.getComponentOrientation().isLeftToRight()) {
                n = (b ? basicTableHeaderUI.selectPreviousColumn(b2) : basicTableHeaderUI.selectNextColumn(b2));
            }
            else {
                n = (b ? basicTableHeaderUI.selectNextColumn(b2) : basicTableHeaderUI.selectPreviousColumn(b2));
            }
            if (n != access$000) {
                if (!b2) {
                    return true;
                }
                tableHeader.getColumnModel().moveColumn(access$000, n);
            }
            return false;
        }
        
        private void resize(final boolean b, final JTableHeader tableHeader, final BasicTableHeaderUI basicTableHeaderUI) {
            final TableColumn column = tableHeader.getColumnModel().getColumn(basicTableHeaderUI.getSelectedColumnIndex());
            tableHeader.setResizingColumn(column);
            final int width;
            final int n = width = column.getWidth();
            int n2;
            if (tableHeader.getComponentOrientation().isLeftToRight()) {
                n2 = width + (b ? -1 : 1);
            }
            else {
                n2 = width + (b ? 1 : -1);
            }
            basicTableHeaderUI.changeColumnWidth(column, tableHeader, n, n2);
        }
    }
}
