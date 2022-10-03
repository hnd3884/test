package javax.swing.plaf.basic;

import java.awt.datatransfer.Transferable;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.InputEvent;
import java.awt.AWTEvent;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.TableCellEditor;
import java.util.EventObject;
import javax.swing.DefaultListSelectionModel;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.Graphics;
import java.util.Enumeration;
import javax.swing.table.TableColumn;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.UIDefaults;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ActionMap;
import javax.swing.Action;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.dnd.DropTarget;
import java.awt.Container;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import java.awt.Point;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import javax.swing.CellRendererPane;
import javax.swing.JTable;
import javax.swing.plaf.TableUI;

public class BasicTableUI extends TableUI
{
    private static final StringBuilder BASELINE_COMPONENT_KEY;
    protected JTable table;
    protected CellRendererPane rendererPane;
    protected KeyListener keyListener;
    protected FocusListener focusListener;
    protected MouseInputListener mouseInputListener;
    private Handler handler;
    private boolean isFileList;
    private static final TransferHandler defaultTransferHandler;
    
    public BasicTableUI() {
        this.isFileList = false;
    }
    
    private boolean pointOutsidePrefSize(final int n, final int n2, final Point point) {
        return this.isFileList && SwingUtilities2.pointOutsidePrefSize(this.table, n, n2, point);
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected KeyListener createKeyListener() {
        return null;
    }
    
    protected FocusListener createFocusListener() {
        return this.getHandler();
    }
    
    protected MouseInputListener createMouseInputListener() {
        return this.getHandler();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTableUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.table = (JTable)component;
        this.rendererPane = new CellRendererPane();
        this.table.add(this.rendererPane);
        this.installDefaults();
        this.installDefaults2();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(this.table, "Table.background", "Table.foreground", "Table.font");
        LookAndFeel.installProperty(this.table, "opaque", Boolean.TRUE);
        final Color selectionBackground = this.table.getSelectionBackground();
        if (selectionBackground == null || selectionBackground instanceof UIResource) {
            final Color color = UIManager.getColor("Table.selectionBackground");
            this.table.setSelectionBackground((color != null) ? color : UIManager.getColor("textHighlight"));
        }
        final Color selectionForeground = this.table.getSelectionForeground();
        if (selectionForeground == null || selectionForeground instanceof UIResource) {
            final Color color2 = UIManager.getColor("Table.selectionForeground");
            this.table.setSelectionForeground((color2 != null) ? color2 : UIManager.getColor("textHighlightText"));
        }
        final Color gridColor = this.table.getGridColor();
        if (gridColor == null || gridColor instanceof UIResource) {
            final Color color3 = UIManager.getColor("Table.gridColor");
            this.table.setGridColor((color3 != null) ? color3 : Color.GRAY);
        }
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this.table);
        if (unwrappedParent != null) {
            final Container parent = unwrappedParent.getParent();
            if (parent != null && parent instanceof JScrollPane) {
                LookAndFeel.installBorder((JComponent)parent, "Table.scrollPaneBorder");
            }
        }
        this.isFileList = Boolean.TRUE.equals(this.table.getClientProperty("Table.isFileList"));
    }
    
    private void installDefaults2() {
        final TransferHandler transferHandler = this.table.getTransferHandler();
        if (transferHandler == null || transferHandler instanceof UIResource) {
            this.table.setTransferHandler(BasicTableUI.defaultTransferHandler);
            if (this.table.getDropTarget() instanceof UIResource) {
                this.table.setDropTarget(null);
            }
        }
    }
    
    protected void installListeners() {
        this.focusListener = this.createFocusListener();
        this.keyListener = this.createKeyListener();
        this.mouseInputListener = this.createMouseInputListener();
        this.table.addFocusListener(this.focusListener);
        this.table.addKeyListener(this.keyListener);
        this.table.addMouseListener(this.mouseInputListener);
        this.table.addMouseMotionListener(this.mouseInputListener);
        this.table.addPropertyChangeListener(this.getHandler());
        if (this.isFileList) {
            this.table.getSelectionModel().addListSelectionListener(this.getHandler());
        }
    }
    
    protected void installKeyboardActions() {
        LazyActionMap.installLazyActionMap(this.table, BasicTableUI.class, "Table.actionMap");
        SwingUtilities.replaceUIInputMap(this.table, 1, this.getInputMap(1));
    }
    
    InputMap getInputMap(final int n) {
        if (n != 1) {
            return null;
        }
        final InputMap parent = (InputMap)DefaultLookup.get(this.table, this, "Table.ancestorInputMap");
        final InputMap inputMap;
        if (this.table.getComponentOrientation().isLeftToRight() || (inputMap = (InputMap)DefaultLookup.get(this.table, this, "Table.ancestorInputMap.RightToLeft")) == null) {
            return parent;
        }
        inputMap.setParent(parent);
        return inputMap;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("selectNextColumn", 1, 0, false, false));
        lazyActionMap.put(new Actions("selectNextColumnChangeLead", 1, 0, false, false));
        lazyActionMap.put(new Actions("selectPreviousColumn", -1, 0, false, false));
        lazyActionMap.put(new Actions("selectPreviousColumnChangeLead", -1, 0, false, false));
        lazyActionMap.put(new Actions("selectNextRow", 0, 1, false, false));
        lazyActionMap.put(new Actions("selectNextRowChangeLead", 0, 1, false, false));
        lazyActionMap.put(new Actions("selectPreviousRow", 0, -1, false, false));
        lazyActionMap.put(new Actions("selectPreviousRowChangeLead", 0, -1, false, false));
        lazyActionMap.put(new Actions("selectNextColumnExtendSelection", 1, 0, true, false));
        lazyActionMap.put(new Actions("selectPreviousColumnExtendSelection", -1, 0, true, false));
        lazyActionMap.put(new Actions("selectNextRowExtendSelection", 0, 1, true, false));
        lazyActionMap.put(new Actions("selectPreviousRowExtendSelection", 0, -1, true, false));
        lazyActionMap.put(new Actions("scrollUpChangeSelection", false, false, true, false));
        lazyActionMap.put(new Actions("scrollDownChangeSelection", false, true, true, false));
        lazyActionMap.put(new Actions("selectFirstColumn", false, false, false, true));
        lazyActionMap.put(new Actions("selectLastColumn", false, true, false, true));
        lazyActionMap.put(new Actions("scrollUpExtendSelection", true, false, true, false));
        lazyActionMap.put(new Actions("scrollDownExtendSelection", true, true, true, false));
        lazyActionMap.put(new Actions("selectFirstColumnExtendSelection", true, false, false, true));
        lazyActionMap.put(new Actions("selectLastColumnExtendSelection", true, true, false, true));
        lazyActionMap.put(new Actions("selectFirstRow", false, false, true, true));
        lazyActionMap.put(new Actions("selectLastRow", false, true, true, true));
        lazyActionMap.put(new Actions("selectFirstRowExtendSelection", true, false, true, true));
        lazyActionMap.put(new Actions("selectLastRowExtendSelection", true, true, true, true));
        lazyActionMap.put(new Actions("selectNextColumnCell", 1, 0, false, true));
        lazyActionMap.put(new Actions("selectPreviousColumnCell", -1, 0, false, true));
        lazyActionMap.put(new Actions("selectNextRowCell", 0, 1, false, true));
        lazyActionMap.put(new Actions("selectPreviousRowCell", 0, -1, false, true));
        lazyActionMap.put(new Actions("selectAll"));
        lazyActionMap.put(new Actions("clearSelection"));
        lazyActionMap.put(new Actions("cancel"));
        lazyActionMap.put(new Actions("startEditing"));
        lazyActionMap.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
        lazyActionMap.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
        lazyActionMap.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
        lazyActionMap.put(new Actions("scrollLeftChangeSelection", false, false, false, false));
        lazyActionMap.put(new Actions("scrollRightChangeSelection", false, true, false, false));
        lazyActionMap.put(new Actions("scrollLeftExtendSelection", true, false, false, false));
        lazyActionMap.put(new Actions("scrollRightExtendSelection", true, true, false, false));
        lazyActionMap.put(new Actions("addToSelection"));
        lazyActionMap.put(new Actions("toggleAndAnchor"));
        lazyActionMap.put(new Actions("extendTo"));
        lazyActionMap.put(new Actions("moveSelectionTo"));
        lazyActionMap.put(new Actions("focusHeader"));
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        this.table.remove(this.rendererPane);
        this.rendererPane = null;
        this.table = null;
    }
    
    protected void uninstallDefaults() {
        if (this.table.getTransferHandler() instanceof UIResource) {
            this.table.setTransferHandler(null);
        }
    }
    
    protected void uninstallListeners() {
        this.table.removeFocusListener(this.focusListener);
        this.table.removeKeyListener(this.keyListener);
        this.table.removeMouseListener(this.mouseInputListener);
        this.table.removeMouseMotionListener(this.mouseInputListener);
        this.table.removePropertyChangeListener(this.getHandler());
        if (this.isFileList) {
            this.table.getSelectionModel().removeListSelectionListener(this.getHandler());
        }
        this.focusListener = null;
        this.keyListener = null;
        this.mouseInputListener = null;
        this.handler = null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.table, 1, null);
        SwingUtilities.replaceUIActionMap(this.table, null);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        Component tableCellRendererComponent = (Component)lookAndFeelDefaults.get(BasicTableUI.BASELINE_COMPONENT_KEY);
        if (tableCellRendererComponent == null) {
            tableCellRendererComponent = new DefaultTableCellRenderer().getTableCellRendererComponent(this.table, "a", false, false, -1, -1);
            lookAndFeelDefaults.put(BasicTableUI.BASELINE_COMPONENT_KEY, tableCellRendererComponent);
        }
        tableCellRendererComponent.setFont(this.table.getFont());
        final int rowMargin = this.table.getRowMargin();
        return tableCellRendererComponent.getBaseline(Integer.MAX_VALUE, this.table.getRowHeight() - rowMargin) + rowMargin / 2;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    private Dimension createTableSize(final long n) {
        int n2 = 0;
        final int rowCount = this.table.getRowCount();
        if (rowCount > 0 && this.table.getColumnCount() > 0) {
            final Rectangle cellRect = this.table.getCellRect(rowCount - 1, 0, true);
            n2 = cellRect.y + cellRect.height;
        }
        long abs = Math.abs(n);
        if (abs > 2147483647L) {
            abs = 2147483647L;
        }
        return new Dimension((int)abs, n2);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        long n = 0L;
        final Enumeration<TableColumn> columns = this.table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            n += columns.nextElement().getMinWidth();
        }
        return this.createTableSize(n);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        long n = 0L;
        final Enumeration<TableColumn> columns = this.table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            n += columns.nextElement().getPreferredWidth();
        }
        return this.createTableSize(n);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        long n = 0L;
        final Enumeration<TableColumn> columns = this.table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            n += columns.nextElement().getMaxWidth();
        }
        return this.createTableSize(n);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Rectangle clipBounds = graphics.getClipBounds();
        final Rectangle bounds;
        final Rectangle rectangle = bounds = this.table.getBounds();
        final int n = 0;
        rectangle.y = n;
        bounds.x = n;
        if (this.table.getRowCount() <= 0 || this.table.getColumnCount() <= 0 || !rectangle.intersects(clipBounds)) {
            this.paintDropLines(graphics);
            return;
        }
        final boolean leftToRight = this.table.getComponentOrientation().isLeftToRight();
        final Point location = clipBounds.getLocation();
        final Point point = new Point(clipBounds.x + clipBounds.width - 1, clipBounds.y + clipBounds.height - 1);
        int rowAtPoint = this.table.rowAtPoint(location);
        int rowAtPoint2 = this.table.rowAtPoint(point);
        if (rowAtPoint == -1) {
            rowAtPoint = 0;
        }
        if (rowAtPoint2 == -1) {
            rowAtPoint2 = this.table.getRowCount() - 1;
        }
        int columnAtPoint = this.table.columnAtPoint(leftToRight ? location : point);
        int columnAtPoint2 = this.table.columnAtPoint(leftToRight ? point : location);
        if (columnAtPoint == -1) {
            columnAtPoint = 0;
        }
        if (columnAtPoint2 == -1) {
            columnAtPoint2 = this.table.getColumnCount() - 1;
        }
        this.paintGrid(graphics, rowAtPoint, rowAtPoint2, columnAtPoint, columnAtPoint2);
        this.paintCells(graphics, rowAtPoint, rowAtPoint2, columnAtPoint, columnAtPoint2);
        this.paintDropLines(graphics);
    }
    
    private void paintDropLines(final Graphics graphics) {
        final JTable.DropLocation dropLocation = this.table.getDropLocation();
        if (dropLocation == null) {
            return;
        }
        final Color color = UIManager.getColor("Table.dropLineColor");
        final Color color2 = UIManager.getColor("Table.dropLineShortColor");
        if (color == null && color2 == null) {
            return;
        }
        final Rectangle hDropLineRect = this.getHDropLineRect(dropLocation);
        if (hDropLineRect != null) {
            final int x = hDropLineRect.x;
            final int width = hDropLineRect.width;
            if (color != null) {
                this.extendRect(hDropLineRect, true);
                graphics.setColor(color);
                graphics.fillRect(hDropLineRect.x, hDropLineRect.y, hDropLineRect.width, hDropLineRect.height);
            }
            if (!dropLocation.isInsertColumn() && color2 != null) {
                graphics.setColor(color2);
                graphics.fillRect(x, hDropLineRect.y, width, hDropLineRect.height);
            }
        }
        final Rectangle vDropLineRect = this.getVDropLineRect(dropLocation);
        if (vDropLineRect != null) {
            final int y = vDropLineRect.y;
            final int height = vDropLineRect.height;
            if (color != null) {
                this.extendRect(vDropLineRect, false);
                graphics.setColor(color);
                graphics.fillRect(vDropLineRect.x, vDropLineRect.y, vDropLineRect.width, vDropLineRect.height);
            }
            if (!dropLocation.isInsertRow() && color2 != null) {
                graphics.setColor(color2);
                graphics.fillRect(vDropLineRect.x, y, vDropLineRect.width, height);
            }
        }
    }
    
    private Rectangle getHDropLineRect(final JTable.DropLocation dropLocation) {
        if (!dropLocation.isInsertRow()) {
            return null;
        }
        int row = dropLocation.getRow();
        int column = dropLocation.getColumn();
        if (column >= this.table.getColumnCount()) {
            --column;
        }
        final Rectangle cellRect = this.table.getCellRect(row, column, true);
        if (row >= this.table.getRowCount()) {
            --row;
            final Rectangle cellRect2 = this.table.getCellRect(row, column, true);
            cellRect.y = cellRect2.y + cellRect2.height;
        }
        if (cellRect.y == 0) {
            cellRect.y = -1;
        }
        else {
            final Rectangle rectangle = cellRect;
            rectangle.y -= 2;
        }
        cellRect.height = 3;
        return cellRect;
    }
    
    private Rectangle getVDropLineRect(final JTable.DropLocation dropLocation) {
        if (!dropLocation.isInsertColumn()) {
            return null;
        }
        final boolean leftToRight = this.table.getComponentOrientation().isLeftToRight();
        int column = dropLocation.getColumn();
        Rectangle rectangle = this.table.getCellRect(dropLocation.getRow(), column, true);
        if (column >= this.table.getColumnCount()) {
            --column;
            rectangle = this.table.getCellRect(dropLocation.getRow(), column, true);
            if (leftToRight) {
                rectangle.x += rectangle.width;
            }
        }
        else if (!leftToRight) {
            rectangle.x += rectangle.width;
        }
        if (rectangle.x == 0) {
            rectangle.x = -1;
        }
        else {
            final Rectangle rectangle2 = rectangle;
            rectangle2.x -= 2;
        }
        rectangle.width = 3;
        return rectangle;
    }
    
    private Rectangle extendRect(final Rectangle rectangle, final boolean b) {
        if (rectangle == null) {
            return rectangle;
        }
        if (b) {
            rectangle.x = 0;
            rectangle.width = this.table.getWidth();
        }
        else {
            rectangle.y = 0;
            if (this.table.getRowCount() != 0) {
                final Rectangle cellRect = this.table.getCellRect(this.table.getRowCount() - 1, 0, true);
                rectangle.height = cellRect.y + cellRect.height;
            }
            else {
                rectangle.height = this.table.getHeight();
            }
        }
        return rectangle;
    }
    
    private void paintGrid(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.setColor(this.table.getGridColor());
        final Rectangle union = this.table.getCellRect(n, n3, true).union(this.table.getCellRect(n2, n4, true));
        if (this.table.getShowHorizontalLines()) {
            final int n5 = union.x + union.width;
            int y = union.y;
            for (int i = n; i <= n2; ++i) {
                y += this.table.getRowHeight(i);
                graphics.drawLine(union.x, y - 1, n5 - 1, y - 1);
            }
        }
        if (this.table.getShowVerticalLines()) {
            final TableColumnModel columnModel = this.table.getColumnModel();
            final int n6 = union.y + union.height;
            if (this.table.getComponentOrientation().isLeftToRight()) {
                int x = union.x;
                for (int j = n3; j <= n4; ++j) {
                    x += columnModel.getColumn(j).getWidth();
                    graphics.drawLine(x - 1, 0, x - 1, n6 - 1);
                }
            }
            else {
                int x2 = union.x;
                for (int k = n4; k >= n3; --k) {
                    x2 += columnModel.getColumn(k).getWidth();
                    graphics.drawLine(x2 - 1, 0, x2 - 1, n6 - 1);
                }
            }
        }
    }
    
    private int viewIndexForColumn(final TableColumn tableColumn) {
        final TableColumnModel columnModel = this.table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i) {
            if (columnModel.getColumn(i) == tableColumn) {
                return i;
            }
        }
        return -1;
    }
    
    private void paintCells(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final JTableHeader tableHeader = this.table.getTableHeader();
        final TableColumn tableColumn = (tableHeader == null) ? null : tableHeader.getDraggedColumn();
        final TableColumnModel columnModel = this.table.getColumnModel();
        final int columnMargin = columnModel.getColumnMargin();
        if (this.table.getComponentOrientation().isLeftToRight()) {
            for (int i = n; i <= n2; ++i) {
                final Rectangle cellRect = this.table.getCellRect(i, n3, false);
                for (int j = n3; j <= n4; ++j) {
                    final TableColumn column = columnModel.getColumn(j);
                    final int width = column.getWidth();
                    cellRect.width = width - columnMargin;
                    if (column != tableColumn) {
                        this.paintCell(graphics, cellRect, i, j);
                    }
                    final Rectangle rectangle = cellRect;
                    rectangle.x += width;
                }
            }
        }
        else {
            for (int k = n; k <= n2; ++k) {
                final Rectangle cellRect2 = this.table.getCellRect(k, n3, false);
                final TableColumn column2 = columnModel.getColumn(n3);
                if (column2 != tableColumn) {
                    cellRect2.width = column2.getWidth() - columnMargin;
                    this.paintCell(graphics, cellRect2, k, n3);
                }
                for (int l = n3 + 1; l <= n4; ++l) {
                    final TableColumn column3 = columnModel.getColumn(l);
                    final int width2 = column3.getWidth();
                    cellRect2.width = width2 - columnMargin;
                    final Rectangle rectangle2 = cellRect2;
                    rectangle2.x -= width2;
                    if (column3 != tableColumn) {
                        this.paintCell(graphics, cellRect2, k, l);
                    }
                }
            }
        }
        if (tableColumn != null) {
            this.paintDraggedArea(graphics, n, n2, tableColumn, tableHeader.getDraggedDistance());
        }
        this.rendererPane.removeAll();
    }
    
    private void paintDraggedArea(final Graphics graphics, final int n, final int n2, final TableColumn tableColumn, final int n3) {
        final int viewIndexForColumn = this.viewIndexForColumn(tableColumn);
        final Rectangle union = this.table.getCellRect(n, viewIndexForColumn, true).union(this.table.getCellRect(n2, viewIndexForColumn, true));
        graphics.setColor(this.table.getParent().getBackground());
        graphics.fillRect(union.x, union.y, union.width, union.height);
        final Rectangle rectangle = union;
        rectangle.x += n3;
        graphics.setColor(this.table.getBackground());
        graphics.fillRect(union.x, union.y, union.width, union.height);
        if (this.table.getShowVerticalLines()) {
            graphics.setColor(this.table.getGridColor());
            final int x = union.x;
            final int y = union.y;
            final int n4 = x + union.width - 1;
            final int n5 = y + union.height - 1;
            graphics.drawLine(x - 1, y, x - 1, n5);
            graphics.drawLine(n4, y, n4, n5);
        }
        for (int i = n; i <= n2; ++i) {
            final Rectangle cellRect;
            final Rectangle rectangle2 = cellRect = this.table.getCellRect(i, viewIndexForColumn, (boolean)(0 != 0));
            cellRect.x += n3;
            this.paintCell(graphics, rectangle2, i, viewIndexForColumn);
            if (this.table.getShowHorizontalLines()) {
                graphics.setColor(this.table.getGridColor());
                final Rectangle cellRect2;
                final Rectangle rectangle3 = cellRect2 = this.table.getCellRect(i, viewIndexForColumn, (boolean)(1 != 0));
                cellRect2.x += n3;
                final int x2 = rectangle3.x;
                final int y2 = rectangle3.y;
                final int n6 = x2 + rectangle3.width - 1;
                final int n7 = y2 + rectangle3.height - 1;
                graphics.drawLine(x2, n7, n6, n7);
            }
        }
    }
    
    private void paintCell(final Graphics graphics, final Rectangle bounds, final int n, final int n2) {
        if (this.table.isEditing() && this.table.getEditingRow() == n && this.table.getEditingColumn() == n2) {
            final Component editorComponent = this.table.getEditorComponent();
            editorComponent.setBounds(bounds);
            editorComponent.validate();
        }
        else {
            this.rendererPane.paintComponent(graphics, this.table.prepareRenderer(this.table.getCellRenderer(n, n2), n, n2), this.table, bounds.x, bounds.y, bounds.width, bounds.height, true);
        }
    }
    
    private static int getAdjustedLead(final JTable table, final boolean b, final ListSelectionModel listSelectionModel) {
        final int leadSelectionIndex = listSelectionModel.getLeadSelectionIndex();
        return (leadSelectionIndex < (b ? table.getRowCount() : table.getColumnCount())) ? leadSelectionIndex : -1;
    }
    
    private static int getAdjustedLead(final JTable table, final boolean b) {
        return b ? getAdjustedLead(table, b, table.getSelectionModel()) : getAdjustedLead(table, b, table.getColumnModel().getSelectionModel());
    }
    
    static {
        BASELINE_COMPONENT_KEY = new StringBuilder("Table.baselineComponent");
        defaultTransferHandler = new TableTransferHandler();
    }
    
    private static class Actions extends UIAction
    {
        private static final String CANCEL_EDITING = "cancel";
        private static final String SELECT_ALL = "selectAll";
        private static final String CLEAR_SELECTION = "clearSelection";
        private static final String START_EDITING = "startEditing";
        private static final String NEXT_ROW = "selectNextRow";
        private static final String NEXT_ROW_CELL = "selectNextRowCell";
        private static final String NEXT_ROW_EXTEND_SELECTION = "selectNextRowExtendSelection";
        private static final String NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
        private static final String PREVIOUS_ROW = "selectPreviousRow";
        private static final String PREVIOUS_ROW_CELL = "selectPreviousRowCell";
        private static final String PREVIOUS_ROW_EXTEND_SELECTION = "selectPreviousRowExtendSelection";
        private static final String PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
        private static final String NEXT_COLUMN = "selectNextColumn";
        private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
        private static final String NEXT_COLUMN_EXTEND_SELECTION = "selectNextColumnExtendSelection";
        private static final String NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
        private static final String PREVIOUS_COLUMN = "selectPreviousColumn";
        private static final String PREVIOUS_COLUMN_CELL = "selectPreviousColumnCell";
        private static final String PREVIOUS_COLUMN_EXTEND_SELECTION = "selectPreviousColumnExtendSelection";
        private static final String PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
        private static final String SCROLL_LEFT_CHANGE_SELECTION = "scrollLeftChangeSelection";
        private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
        private static final String SCROLL_RIGHT_CHANGE_SELECTION = "scrollRightChangeSelection";
        private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
        private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
        private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
        private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
        private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
        private static final String FIRST_COLUMN = "selectFirstColumn";
        private static final String FIRST_COLUMN_EXTEND_SELECTION = "selectFirstColumnExtendSelection";
        private static final String LAST_COLUMN = "selectLastColumn";
        private static final String LAST_COLUMN_EXTEND_SELECTION = "selectLastColumnExtendSelection";
        private static final String FIRST_ROW = "selectFirstRow";
        private static final String FIRST_ROW_EXTEND_SELECTION = "selectFirstRowExtendSelection";
        private static final String LAST_ROW = "selectLastRow";
        private static final String LAST_ROW_EXTEND_SELECTION = "selectLastRowExtendSelection";
        private static final String ADD_TO_SELECTION = "addToSelection";
        private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
        private static final String EXTEND_TO = "extendTo";
        private static final String MOVE_SELECTION_TO = "moveSelectionTo";
        private static final String FOCUS_HEADER = "focusHeader";
        protected int dx;
        protected int dy;
        protected boolean extend;
        protected boolean inSelection;
        protected boolean forwards;
        protected boolean vertically;
        protected boolean toLimit;
        protected int leadRow;
        protected int leadColumn;
        
        Actions(final String s) {
            super(s);
        }
        
        Actions(final String s, int sign, int sign2, final boolean extend, final boolean b) {
            super(s);
            if (b) {
                this.inSelection = true;
                sign = sign(sign);
                sign2 = sign(sign2);
                assert (sign == 0 || sign2 == 0) && (sign != 0 || sign2 != 0);
            }
            this.dx = sign;
            this.dy = sign2;
            this.extend = extend;
        }
        
        Actions(final String s, final boolean b, final boolean forwards, final boolean vertically, final boolean toLimit) {
            this(s, 0, 0, b, false);
            this.forwards = forwards;
            this.vertically = vertically;
            this.toLimit = toLimit;
        }
        
        private static int clipToRange(final int n, final int n2, final int n3) {
            return Math.min(Math.max(n, n2), n3 - 1);
        }
        
        private void moveWithinTableRange(final JTable table, final int n, final int n2) {
            this.leadRow = clipToRange(this.leadRow + n2, 0, table.getRowCount());
            this.leadColumn = clipToRange(this.leadColumn + n, 0, table.getColumnCount());
        }
        
        private static int sign(final int n) {
            return (n < 0) ? -1 : ((n == 0) ? 0 : 1);
        }
        
        private boolean moveWithinSelectedRange(final JTable table, final int n, final int n2, final ListSelectionModel listSelectionModel, final ListSelectionModel listSelectionModel2) {
            final boolean rowSelectionAllowed = table.getRowSelectionAllowed();
            final boolean columnSelectionAllowed = table.getColumnSelectionAllowed();
            int n3;
            int leadColumn;
            int leadColumn2;
            int leadRow;
            int leadRow2;
            if (rowSelectionAllowed && columnSelectionAllowed) {
                n3 = table.getSelectedRowCount() * table.getSelectedColumnCount();
                leadColumn = listSelectionModel2.getMinSelectionIndex();
                leadColumn2 = listSelectionModel2.getMaxSelectionIndex();
                leadRow = listSelectionModel.getMinSelectionIndex();
                leadRow2 = listSelectionModel.getMaxSelectionIndex();
            }
            else if (rowSelectionAllowed) {
                n3 = table.getSelectedRowCount();
                leadColumn = 0;
                leadColumn2 = table.getColumnCount() - 1;
                leadRow = listSelectionModel.getMinSelectionIndex();
                leadRow2 = listSelectionModel.getMaxSelectionIndex();
            }
            else if (columnSelectionAllowed) {
                n3 = table.getSelectedColumnCount();
                leadColumn = listSelectionModel2.getMinSelectionIndex();
                leadColumn2 = listSelectionModel2.getMaxSelectionIndex();
                leadRow = 0;
                leadRow2 = table.getRowCount() - 1;
            }
            else {
                n3 = 0;
                leadColumn2 = (leadColumn = (leadRow = (leadRow2 = 0)));
            }
            boolean b;
            if (n3 == 0 || (n3 == 1 && table.isCellSelected(this.leadRow, this.leadColumn))) {
                b = false;
                leadColumn2 = table.getColumnCount() - 1;
                leadRow2 = table.getRowCount() - 1;
                leadColumn = Math.min(0, leadColumn2);
                leadRow = Math.min(0, leadRow2);
            }
            else {
                b = true;
            }
            if (n2 == 1 && this.leadColumn == -1) {
                this.leadColumn = leadColumn;
                this.leadRow = -1;
            }
            else if (n == 1 && this.leadRow == -1) {
                this.leadRow = leadRow;
                this.leadColumn = -1;
            }
            else if (n2 == -1 && this.leadColumn == -1) {
                this.leadColumn = leadColumn2;
                this.leadRow = leadRow2 + 1;
            }
            else if (n == -1 && this.leadRow == -1) {
                this.leadRow = leadRow2;
                this.leadColumn = leadColumn2 + 1;
            }
            this.leadRow = Math.min(Math.max(this.leadRow, leadRow - 1), leadRow2 + 1);
            this.leadColumn = Math.min(Math.max(this.leadColumn, leadColumn - 1), leadColumn2 + 1);
            do {
                this.calcNextPos(n, leadColumn, leadColumn2, n2, leadRow, leadRow2);
            } while (b && !table.isCellSelected(this.leadRow, this.leadColumn));
            return b;
        }
        
        private void calcNextPos(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            if (n != 0) {
                this.leadColumn += n;
                if (this.leadColumn > n3) {
                    this.leadColumn = n2;
                    ++this.leadRow;
                    if (this.leadRow > n6) {
                        this.leadRow = n5;
                    }
                }
                else if (this.leadColumn < n2) {
                    this.leadColumn = n3;
                    --this.leadRow;
                    if (this.leadRow < n5) {
                        this.leadRow = n6;
                    }
                }
            }
            else {
                this.leadRow += n4;
                if (this.leadRow > n6) {
                    this.leadRow = n5;
                    ++this.leadColumn;
                    if (this.leadColumn > n3) {
                        this.leadColumn = n2;
                    }
                }
                else if (this.leadRow < n5) {
                    this.leadRow = n6;
                    --this.leadColumn;
                    if (this.leadColumn < n2) {
                        this.leadColumn = n3;
                    }
                }
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final String name = this.getName();
            final JTable table = (JTable)actionEvent.getSource();
            final ListSelectionModel selectionModel = table.getSelectionModel();
            this.leadRow = getAdjustedLead(table, true, selectionModel);
            final ListSelectionModel selectionModel2 = table.getColumnModel().getSelectionModel();
            this.leadColumn = getAdjustedLead(table, false, selectionModel2);
            if (name == "scrollLeftChangeSelection" || name == "scrollLeftExtendSelection" || name == "scrollRightChangeSelection" || name == "scrollRightExtendSelection" || name == "scrollUpChangeSelection" || name == "scrollUpExtendSelection" || name == "scrollDownChangeSelection" || name == "scrollDownExtendSelection" || name == "selectFirstColumn" || name == "selectFirstColumnExtendSelection" || name == "selectFirstRow" || name == "selectFirstRowExtendSelection" || name == "selectLastColumn" || name == "selectLastColumnExtendSelection" || name == "selectLastRow" || name == "selectLastRowExtendSelection") {
                if (this.toLimit) {
                    if (this.vertically) {
                        final int rowCount = table.getRowCount();
                        this.dx = 0;
                        this.dy = (this.forwards ? rowCount : (-rowCount));
                    }
                    else {
                        final int columnCount = table.getColumnCount();
                        this.dx = (this.forwards ? columnCount : (-columnCount));
                        this.dy = 0;
                    }
                }
                else {
                    if (!(SwingUtilities.getUnwrappedParent(table).getParent() instanceof JScrollPane)) {
                        return;
                    }
                    final Dimension size = table.getParent().getSize();
                    if (this.vertically) {
                        final Rectangle cellRect = table.getCellRect(this.leadRow, 0, true);
                        if (this.forwards) {
                            final Rectangle rectangle = cellRect;
                            rectangle.y += Math.max(size.height, cellRect.height);
                        }
                        else {
                            final Rectangle rectangle2 = cellRect;
                            rectangle2.y -= size.height;
                        }
                        this.dx = 0;
                        int n = table.rowAtPoint(cellRect.getLocation());
                        if (n == -1 && this.forwards) {
                            n = table.getRowCount();
                        }
                        this.dy = n - this.leadRow;
                    }
                    else {
                        final Rectangle cellRect2 = table.getCellRect(0, this.leadColumn, true);
                        if (this.forwards) {
                            final Rectangle rectangle3 = cellRect2;
                            rectangle3.x += Math.max(size.width, cellRect2.width);
                        }
                        else {
                            final Rectangle rectangle4 = cellRect2;
                            rectangle4.x -= size.width;
                        }
                        int columnAtPoint = table.columnAtPoint(cellRect2.getLocation());
                        if (columnAtPoint == -1) {
                            final boolean leftToRight = table.getComponentOrientation().isLeftToRight();
                            columnAtPoint = (this.forwards ? (leftToRight ? table.getColumnCount() : 0) : (leftToRight ? 0 : table.getColumnCount()));
                        }
                        this.dx = columnAtPoint - this.leadColumn;
                        this.dy = 0;
                    }
                }
            }
            if (name == "selectNextRow" || name == "selectNextRowCell" || name == "selectNextRowExtendSelection" || name == "selectNextRowChangeLead" || name == "selectNextColumn" || name == "selectNextColumnCell" || name == "selectNextColumnExtendSelection" || name == "selectNextColumnChangeLead" || name == "selectPreviousRow" || name == "selectPreviousRowCell" || name == "selectPreviousRowExtendSelection" || name == "selectPreviousRowChangeLead" || name == "selectPreviousColumn" || name == "selectPreviousColumnCell" || name == "selectPreviousColumnExtendSelection" || name == "selectPreviousColumnChangeLead" || name == "scrollLeftChangeSelection" || name == "scrollLeftExtendSelection" || name == "scrollRightChangeSelection" || name == "scrollRightExtendSelection" || name == "scrollUpChangeSelection" || name == "scrollUpExtendSelection" || name == "scrollDownChangeSelection" || name == "scrollDownExtendSelection" || name == "selectFirstColumn" || name == "selectFirstColumnExtendSelection" || name == "selectFirstRow" || name == "selectFirstRowExtendSelection" || name == "selectLastColumn" || name == "selectLastColumnExtendSelection" || name == "selectLastRow" || name == "selectLastRowExtendSelection") {
                if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
                    return;
                }
                int n2 = 0;
                if (name == "selectNextRowChangeLead" || name == "selectPreviousRowChangeLead") {
                    n2 = ((selectionModel.getSelectionMode() == 2) ? 1 : 0);
                }
                else if (name == "selectNextColumnChangeLead" || name == "selectPreviousColumnChangeLead") {
                    n2 = ((selectionModel2.getSelectionMode() == 2) ? 1 : 0);
                }
                if (n2 != 0) {
                    this.moveWithinTableRange(table, this.dx, this.dy);
                    if (this.dy != 0) {
                        ((DefaultListSelectionModel)selectionModel).moveLeadSelectionIndex(this.leadRow);
                        if (getAdjustedLead(table, false, selectionModel2) == -1 && table.getColumnCount() > 0) {
                            ((DefaultListSelectionModel)selectionModel2).moveLeadSelectionIndex(0);
                        }
                    }
                    else {
                        ((DefaultListSelectionModel)selectionModel2).moveLeadSelectionIndex(this.leadColumn);
                        if (getAdjustedLead(table, true, selectionModel) == -1 && table.getRowCount() > 0) {
                            ((DefaultListSelectionModel)selectionModel).moveLeadSelectionIndex(0);
                        }
                    }
                    final Rectangle cellRect3 = table.getCellRect(this.leadRow, this.leadColumn, false);
                    if (cellRect3 != null) {
                        table.scrollRectToVisible(cellRect3);
                    }
                }
                else if (!this.inSelection) {
                    this.moveWithinTableRange(table, this.dx, this.dy);
                    table.changeSelection(this.leadRow, this.leadColumn, false, this.extend);
                }
                else {
                    if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
                        return;
                    }
                    if (this.moveWithinSelectedRange(table, this.dx, this.dy, selectionModel, selectionModel2)) {
                        if (selectionModel.isSelectedIndex(this.leadRow)) {
                            selectionModel.addSelectionInterval(this.leadRow, this.leadRow);
                        }
                        else {
                            selectionModel.removeSelectionInterval(this.leadRow, this.leadRow);
                        }
                        if (selectionModel2.isSelectedIndex(this.leadColumn)) {
                            selectionModel2.addSelectionInterval(this.leadColumn, this.leadColumn);
                        }
                        else {
                            selectionModel2.removeSelectionInterval(this.leadColumn, this.leadColumn);
                        }
                        final Rectangle cellRect4 = table.getCellRect(this.leadRow, this.leadColumn, false);
                        if (cellRect4 != null) {
                            table.scrollRectToVisible(cellRect4);
                        }
                    }
                    else {
                        table.changeSelection(this.leadRow, this.leadColumn, false, false);
                    }
                }
            }
            else if (name == "cancel") {
                table.removeEditor();
            }
            else if (name == "selectAll") {
                table.selectAll();
            }
            else if (name == "clearSelection") {
                table.clearSelection();
            }
            else if (name == "startEditing") {
                if (!table.hasFocus()) {
                    final TableCellEditor cellEditor = table.getCellEditor();
                    if (cellEditor != null && !cellEditor.stopCellEditing()) {
                        return;
                    }
                    table.requestFocus();
                }
                else {
                    table.editCellAt(this.leadRow, this.leadColumn, actionEvent);
                    final Component editorComponent = table.getEditorComponent();
                    if (editorComponent != null) {
                        editorComponent.requestFocus();
                    }
                }
            }
            else if (name == "addToSelection") {
                if (!table.isCellSelected(this.leadRow, this.leadColumn)) {
                    final int anchorSelectionIndex = selectionModel.getAnchorSelectionIndex();
                    final int anchorSelectionIndex2 = selectionModel2.getAnchorSelectionIndex();
                    selectionModel.setValueIsAdjusting(true);
                    selectionModel2.setValueIsAdjusting(true);
                    table.changeSelection(this.leadRow, this.leadColumn, true, false);
                    selectionModel.setAnchorSelectionIndex(anchorSelectionIndex);
                    selectionModel2.setAnchorSelectionIndex(anchorSelectionIndex2);
                    selectionModel.setValueIsAdjusting(false);
                    selectionModel2.setValueIsAdjusting(false);
                }
            }
            else if (name == "toggleAndAnchor") {
                table.changeSelection(this.leadRow, this.leadColumn, true, false);
            }
            else if (name == "extendTo") {
                table.changeSelection(this.leadRow, this.leadColumn, false, true);
            }
            else if (name == "moveSelectionTo") {
                table.changeSelection(this.leadRow, this.leadColumn, false, false);
            }
            else if (name == "focusHeader") {
                final JTableHeader tableHeader = table.getTableHeader();
                if (tableHeader != null) {
                    final int selectedColumn = table.getSelectedColumn();
                    if (selectedColumn >= 0) {
                        final TableHeaderUI ui = tableHeader.getUI();
                        if (ui instanceof BasicTableHeaderUI) {
                            ((BasicTableHeaderUI)ui).selectColumn(selectedColumn);
                        }
                    }
                    tableHeader.requestFocusInWindow();
                }
            }
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            final String name = this.getName();
            if (o instanceof JTable && Boolean.TRUE.equals(((JTable)o).getClientProperty("Table.isFileList")) && (name == "selectNextColumn" || name == "selectNextColumnCell" || name == "selectNextColumnExtendSelection" || name == "selectNextColumnChangeLead" || name == "selectPreviousColumn" || name == "selectPreviousColumnCell" || name == "selectPreviousColumnExtendSelection" || name == "selectPreviousColumnChangeLead" || name == "scrollLeftChangeSelection" || name == "scrollLeftExtendSelection" || name == "scrollRightChangeSelection" || name == "scrollRightExtendSelection" || name == "selectFirstColumn" || name == "selectFirstColumnExtendSelection" || name == "selectLastColumn" || name == "selectLastColumnExtendSelection" || name == "selectNextRowCell" || name == "selectPreviousRowCell")) {
                return false;
            }
            if (name == "cancel" && o instanceof JTable) {
                return ((JTable)o).isEditing();
            }
            if (name == "selectNextRowChangeLead" || name == "selectPreviousRowChangeLead") {
                return o != null && ((JTable)o).getSelectionModel() instanceof DefaultListSelectionModel;
            }
            if (name == "selectNextColumnChangeLead" || name == "selectPreviousColumnChangeLead") {
                return o != null && ((JTable)o).getColumnModel().getSelectionModel() instanceof DefaultListSelectionModel;
            }
            if (name == "addToSelection" && o instanceof JTable) {
                final JTable table = (JTable)o;
                final int access$100 = getAdjustedLead(table, true);
                final int access$101 = getAdjustedLead(table, false);
                return !table.isEditing() && !table.isCellSelected(access$100, access$101);
            }
            return name != "focusHeader" || !(o instanceof JTable) || ((JTable)o).getTableHeader() != null;
        }
    }
    
    public class KeyHandler implements KeyListener
    {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            BasicTableUI.this.getHandler().keyPressed(keyEvent);
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            BasicTableUI.this.getHandler().keyReleased(keyEvent);
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            BasicTableUI.this.getHandler().keyTyped(keyEvent);
        }
    }
    
    public class FocusHandler implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicTableUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicTableUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    public class MouseInputHandler implements MouseInputListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mouseClicked(mouseEvent);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mouseReleased(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mouseExited(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mouseMoved(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicTableUI.this.getHandler().mouseDragged(mouseEvent);
        }
    }
    
    private class Handler implements FocusListener, MouseInputListener, PropertyChangeListener, ListSelectionListener, ActionListener, DragRecognitionSupport.BeforeDrag
    {
        private Component dispatchComponent;
        private int pressedRow;
        private int pressedCol;
        private MouseEvent pressedEvent;
        private boolean dragPressDidSelection;
        private boolean dragStarted;
        private boolean shouldStartTimer;
        private boolean outsidePrefSize;
        private Timer timer;
        
        private Handler() {
            this.timer = null;
        }
        
        private void repaintLeadCell() {
            final int access$100 = getAdjustedLead(BasicTableUI.this.table, true);
            final int access$101 = getAdjustedLead(BasicTableUI.this.table, false);
            if (access$100 < 0 || access$101 < 0) {
                return;
            }
            BasicTableUI.this.table.repaint(BasicTableUI.this.table.getCellRect(access$100, access$101, false));
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            this.repaintLeadCell();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            this.repaintLeadCell();
        }
        
        public void keyPressed(final KeyEvent keyEvent) {
        }
        
        public void keyReleased(final KeyEvent keyEvent) {
        }
        
        public void keyTyped(final KeyEvent keyEvent) {
            final KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent.getKeyChar(), keyEvent.getModifiers());
            final InputMap inputMap = BasicTableUI.this.table.getInputMap(0);
            if (inputMap != null && inputMap.get(keyStroke) != null) {
                return;
            }
            final InputMap inputMap2 = BasicTableUI.this.table.getInputMap(1);
            if (inputMap2 != null && inputMap2.get(keyStroke) != null) {
                return;
            }
            final KeyStroke keyStrokeForEvent = KeyStroke.getKeyStrokeForEvent(keyEvent);
            if (keyEvent.getKeyChar() == '\r') {
                return;
            }
            final int access$100 = getAdjustedLead(BasicTableUI.this.table, true);
            final int access$101 = getAdjustedLead(BasicTableUI.this.table, false);
            if (access$100 != -1 && access$101 != -1 && !BasicTableUI.this.table.isEditing() && !BasicTableUI.this.table.editCellAt(access$100, access$101)) {
                return;
            }
            final Component editorComponent = BasicTableUI.this.table.getEditorComponent();
            if (BasicTableUI.this.table.isEditing() && editorComponent != null && editorComponent instanceof JComponent) {
                final JComponent component = (JComponent)editorComponent;
                final InputMap inputMap3 = component.getInputMap(0);
                Object o = (inputMap3 != null) ? inputMap3.get(keyStrokeForEvent) : null;
                if (o == null) {
                    final InputMap inputMap4 = component.getInputMap(1);
                    o = ((inputMap4 != null) ? inputMap4.get(keyStrokeForEvent) : null);
                }
                if (o != null) {
                    final ActionMap actionMap = component.getActionMap();
                    final Action action = (actionMap != null) ? actionMap.get(o) : null;
                    if (action != null && SwingUtilities.notifyAction(action, keyStrokeForEvent, keyEvent, component, keyEvent.getModifiers())) {
                        keyEvent.consume();
                    }
                }
            }
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        private void setDispatchComponent(final MouseEvent mouseEvent) {
            final Component editorComponent = BasicTableUI.this.table.getEditorComponent();
            final Point convertPoint = SwingUtilities.convertPoint(BasicTableUI.this.table, mouseEvent.getPoint(), editorComponent);
            SwingUtilities2.setSkipClickCount(this.dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, convertPoint.x, convertPoint.y), mouseEvent.getClickCount() - 1);
        }
        
        private boolean repostEvent(final MouseEvent mouseEvent) {
            if (this.dispatchComponent == null || !BasicTableUI.this.table.isEditing()) {
                return false;
            }
            this.dispatchComponent.dispatchEvent(SwingUtilities.convertMouseEvent(BasicTableUI.this.table, mouseEvent, this.dispatchComponent));
            return true;
        }
        
        private void setValueIsAdjusting(final boolean b) {
            BasicTableUI.this.table.getSelectionModel().setValueIsAdjusting(b);
            BasicTableUI.this.table.getColumnModel().getSelectionModel().setValueIsAdjusting(b);
        }
        
        private boolean canStartDrag() {
            if (this.pressedRow == -1 || this.pressedCol == -1) {
                return false;
            }
            if (BasicTableUI.this.isFileList) {
                return !this.outsidePrefSize;
            }
            return (BasicTableUI.this.table.getSelectionModel().getSelectionMode() == 0 && BasicTableUI.this.table.getColumnModel().getSelectionModel().getSelectionMode() == 0) || BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicTableUI.this.table)) {
                return;
            }
            if (BasicTableUI.this.table.isEditing() && !BasicTableUI.this.table.getCellEditor().stopCellEditing()) {
                final Component editorComponent = BasicTableUI.this.table.getEditorComponent();
                if (editorComponent != null && !editorComponent.hasFocus()) {
                    SwingUtilities2.compositeRequestFocus(editorComponent);
                }
                return;
            }
            final Point point = mouseEvent.getPoint();
            this.pressedRow = BasicTableUI.this.table.rowAtPoint(point);
            this.pressedCol = BasicTableUI.this.table.columnAtPoint(point);
            this.outsidePrefSize = BasicTableUI.this.pointOutsidePrefSize(this.pressedRow, this.pressedCol, point);
            if (BasicTableUI.this.isFileList) {
                this.shouldStartTimer = (BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol) && !mouseEvent.isShiftDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent) && !this.outsidePrefSize);
            }
            if (BasicTableUI.this.table.getDragEnabled()) {
                this.mousePressedDND(mouseEvent);
            }
            else {
                SwingUtilities2.adjustFocus(BasicTableUI.this.table);
                if (!BasicTableUI.this.isFileList) {
                    this.setValueIsAdjusting(true);
                }
                this.adjustSelection(mouseEvent);
            }
        }
        
        private void mousePressedDND(final MouseEvent pressedEvent) {
            this.pressedEvent = pressedEvent;
            boolean b = true;
            this.dragStarted = false;
            if (this.canStartDrag() && DragRecognitionSupport.mousePressed(pressedEvent)) {
                this.dragPressDidSelection = false;
                if (BasicGraphicsUtils.isMenuShortcutKeyDown(pressedEvent) && BasicTableUI.this.isFileList) {
                    return;
                }
                if (!pressedEvent.isShiftDown() && BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol)) {
                    BasicTableUI.this.table.getSelectionModel().addSelectionInterval(this.pressedRow, this.pressedRow);
                    BasicTableUI.this.table.getColumnModel().getSelectionModel().addSelectionInterval(this.pressedCol, this.pressedCol);
                    return;
                }
                this.dragPressDidSelection = true;
                b = false;
            }
            else if (!BasicTableUI.this.isFileList) {
                this.setValueIsAdjusting(true);
            }
            if (b) {
                SwingUtilities2.adjustFocus(BasicTableUI.this.table);
            }
            this.adjustSelection(pressedEvent);
        }
        
        private void adjustSelection(final MouseEvent dispatchComponent) {
            if (this.outsidePrefSize) {
                if (dispatchComponent.getID() == 501 && (!dispatchComponent.isShiftDown() || BasicTableUI.this.table.getSelectionModel().getSelectionMode() == 0)) {
                    BasicTableUI.this.table.clearSelection();
                    final TableCellEditor cellEditor = BasicTableUI.this.table.getCellEditor();
                    if (cellEditor != null) {
                        cellEditor.stopCellEditing();
                    }
                }
                return;
            }
            if (this.pressedCol == -1 || this.pressedRow == -1) {
                return;
            }
            final boolean dragEnabled = BasicTableUI.this.table.getDragEnabled();
            if (!dragEnabled && !BasicTableUI.this.isFileList && BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, dispatchComponent)) {
                this.setDispatchComponent(dispatchComponent);
                this.repostEvent(dispatchComponent);
            }
            final TableCellEditor cellEditor2 = BasicTableUI.this.table.getCellEditor();
            if (dragEnabled || cellEditor2 == null || cellEditor2.shouldSelectCell(dispatchComponent)) {
                BasicTableUI.this.table.changeSelection(this.pressedRow, this.pressedCol, BasicGraphicsUtils.isMenuShortcutKeyDown(dispatchComponent), dispatchComponent.isShiftDown());
            }
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            if (this.timer != null) {
                this.timer.stop();
                this.timer = null;
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, null);
            final Component editorComponent = BasicTableUI.this.table.getEditorComponent();
            if (editorComponent != null && !editorComponent.hasFocus()) {
                SwingUtilities2.compositeRequestFocus(editorComponent);
            }
        }
        
        private void maybeStartTimer() {
            if (!this.shouldStartTimer) {
                return;
            }
            if (this.timer == null) {
                (this.timer = new Timer(1200, this)).setRepeats(false);
            }
            this.timer.start();
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicTableUI.this.table)) {
                return;
            }
            if (BasicTableUI.this.table.getDragEnabled()) {
                this.mouseReleasedDND(mouseEvent);
            }
            else if (BasicTableUI.this.isFileList) {
                this.maybeStartTimer();
            }
            this.pressedEvent = null;
            this.repostEvent(mouseEvent);
            this.dispatchComponent = null;
            this.setValueIsAdjusting(false);
        }
        
        private void mouseReleasedDND(final MouseEvent mouseEvent) {
            final MouseEvent mouseReleased = DragRecognitionSupport.mouseReleased(mouseEvent);
            if (mouseReleased != null) {
                SwingUtilities2.adjustFocus(BasicTableUI.this.table);
                if (!this.dragPressDidSelection) {
                    this.adjustSelection(mouseReleased);
                }
            }
            if (!this.dragStarted) {
                if (BasicTableUI.this.isFileList) {
                    this.maybeStartTimer();
                    return;
                }
                final Point point = mouseEvent.getPoint();
                if (this.pressedEvent != null && BasicTableUI.this.table.rowAtPoint(point) == this.pressedRow && BasicTableUI.this.table.columnAtPoint(point) == this.pressedCol && BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, this.pressedEvent)) {
                    this.setDispatchComponent(this.pressedEvent);
                    this.repostEvent(this.pressedEvent);
                    final TableCellEditor cellEditor = BasicTableUI.this.table.getCellEditor();
                    if (cellEditor != null) {
                        cellEditor.shouldSelectCell(this.pressedEvent);
                    }
                }
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void dragStarting(final MouseEvent mouseEvent) {
            this.dragStarted = true;
            if (BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent) && BasicTableUI.this.isFileList) {
                BasicTableUI.this.table.getSelectionModel().addSelectionInterval(this.pressedRow, this.pressedRow);
                BasicTableUI.this.table.getColumnModel().getSelectionModel().addSelectionInterval(this.pressedCol, this.pressedCol);
            }
            this.pressedEvent = null;
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicTableUI.this.table)) {
                return;
            }
            if (BasicTableUI.this.table.getDragEnabled() && (DragRecognitionSupport.mouseDragged(mouseEvent, this) || this.dragStarted)) {
                return;
            }
            this.repostEvent(mouseEvent);
            if (BasicTableUI.this.isFileList || BasicTableUI.this.table.isEditing()) {
                return;
            }
            final Point point = mouseEvent.getPoint();
            final int rowAtPoint = BasicTableUI.this.table.rowAtPoint(point);
            final int columnAtPoint = BasicTableUI.this.table.columnAtPoint(point);
            if (columnAtPoint == -1 || rowAtPoint == -1) {
                return;
            }
            BasicTableUI.this.table.changeSelection(rowAtPoint, columnAtPoint, BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent), true);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if ("componentOrientation" == propertyName) {
                SwingUtilities.replaceUIInputMap(BasicTableUI.this.table, 1, BasicTableUI.this.getInputMap(1));
                final JTableHeader tableHeader = BasicTableUI.this.table.getTableHeader();
                if (tableHeader != null) {
                    tableHeader.setComponentOrientation((ComponentOrientation)propertyChangeEvent.getNewValue());
                }
            }
            else if ("dropLocation" == propertyName) {
                this.repaintDropLocation((JTable.DropLocation)propertyChangeEvent.getOldValue());
                this.repaintDropLocation(BasicTableUI.this.table.getDropLocation());
            }
            else if ("Table.isFileList" == propertyName) {
                BasicTableUI.this.isFileList = Boolean.TRUE.equals(BasicTableUI.this.table.getClientProperty("Table.isFileList"));
                BasicTableUI.this.table.revalidate();
                BasicTableUI.this.table.repaint();
                if (BasicTableUI.this.isFileList) {
                    BasicTableUI.this.table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
                }
                else {
                    BasicTableUI.this.table.getSelectionModel().removeListSelectionListener(BasicTableUI.this.getHandler());
                    this.timer = null;
                }
            }
            else if ("selectionModel" == propertyName && BasicTableUI.this.isFileList) {
                ((ListSelectionModel)propertyChangeEvent.getOldValue()).removeListSelectionListener(BasicTableUI.this.getHandler());
                BasicTableUI.this.table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
            }
        }
        
        private void repaintDropLocation(final JTable.DropLocation dropLocation) {
            if (dropLocation == null) {
                return;
            }
            if (!dropLocation.isInsertRow() && !dropLocation.isInsertColumn()) {
                final Rectangle cellRect = BasicTableUI.this.table.getCellRect(dropLocation.getRow(), dropLocation.getColumn(), false);
                if (cellRect != null) {
                    BasicTableUI.this.table.repaint(cellRect);
                }
                return;
            }
            if (dropLocation.isInsertRow()) {
                final Rectangle access$600 = BasicTableUI.this.extendRect(BasicTableUI.this.getHDropLineRect(dropLocation), true);
                if (access$600 != null) {
                    BasicTableUI.this.table.repaint(access$600);
                }
            }
            if (dropLocation.isInsertColumn()) {
                final Rectangle access$601 = BasicTableUI.this.extendRect(BasicTableUI.this.getVDropLineRect(dropLocation), false);
                if (access$601 != null) {
                    BasicTableUI.this.table.repaint(access$601);
                }
            }
        }
    }
    
    static class TableTransferHandler extends TransferHandler implements UIResource
    {
        @Override
        protected Transferable createTransferable(final JComponent component) {
            if (!(component instanceof JTable)) {
                return null;
            }
            final JTable table = (JTable)component;
            if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
                return null;
            }
            int[] selectedRows;
            if (!table.getRowSelectionAllowed()) {
                final int rowCount = table.getRowCount();
                selectedRows = new int[rowCount];
                for (int i = 0; i < rowCount; ++i) {
                    selectedRows[i] = i;
                }
            }
            else {
                selectedRows = table.getSelectedRows();
            }
            int[] selectedColumns;
            if (!table.getColumnSelectionAllowed()) {
                final int columnCount = table.getColumnCount();
                selectedColumns = new int[columnCount];
                for (int j = 0; j < columnCount; ++j) {
                    selectedColumns[j] = j;
                }
            }
            else {
                selectedColumns = table.getSelectedColumns();
            }
            if (selectedRows == null || selectedColumns == null || selectedRows.length == 0 || selectedColumns.length == 0) {
                return null;
            }
            final StringBuffer sb = new StringBuffer();
            final StringBuffer sb2 = new StringBuffer();
            sb2.append("<html>\n<body>\n<table>\n");
            for (int k = 0; k < selectedRows.length; ++k) {
                sb2.append("<tr>\n");
                for (int l = 0; l < selectedColumns.length; ++l) {
                    final Object value = table.getValueAt(selectedRows[k], selectedColumns[l]);
                    final String s = (value == null) ? "" : value.toString();
                    sb.append(s + "\t");
                    sb2.append("  <td>" + s + "</td>\n");
                }
                sb.deleteCharAt(sb.length() - 1).append("\n");
                sb2.append("</tr>\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb2.append("</table>\n</body>\n</html>");
            return new BasicTransferable(sb.toString(), sb2.toString());
        }
        
        @Override
        public int getSourceActions(final JComponent component) {
            return 1;
        }
    }
}
