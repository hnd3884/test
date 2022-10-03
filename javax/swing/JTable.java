package javax.swing;

import java.awt.event.FocusListener;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleAction;
import java.util.Locale;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleTableModelChange;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleExtendedTable;
import javax.accessibility.AccessibleSelection;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import sun.reflect.misc.ReflectUtil;
import javax.swing.border.LineBorder;
import java.lang.reflect.Constructor;
import javax.swing.border.EmptyBorder;
import java.text.DateFormat;
import java.text.NumberFormat;
import javax.accessibility.AccessibleContext;
import java.awt.print.PrinterAbortException;
import java.awt.print.Printable;
import sun.swing.PrintingStatus;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;
import java.text.MessageFormat;
import java.awt.print.PrinterException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.table.DefaultTableColumnModel;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.KeyEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.TableModelEvent;
import java.util.Enumeration;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableUI;
import java.util.EventObject;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import javax.swing.table.TableRowSorter;
import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Point;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.KeyboardFocusManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import java.awt.Container;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.awt.AWTKeyStroke;
import java.util.Set;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.event.RowSorterListener;
import javax.accessibility.Accessible;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;

public class JTable extends JComponent implements TableModelListener, Scrollable, TableColumnModelListener, ListSelectionListener, CellEditorListener, Accessible, RowSorterListener
{
    private static final String uiClassID = "TableUI";
    public static final int AUTO_RESIZE_OFF = 0;
    public static final int AUTO_RESIZE_NEXT_COLUMN = 1;
    public static final int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;
    public static final int AUTO_RESIZE_LAST_COLUMN = 3;
    public static final int AUTO_RESIZE_ALL_COLUMNS = 4;
    protected TableModel dataModel;
    protected TableColumnModel columnModel;
    protected ListSelectionModel selectionModel;
    protected JTableHeader tableHeader;
    protected int rowHeight;
    protected int rowMargin;
    protected Color gridColor;
    protected boolean showHorizontalLines;
    protected boolean showVerticalLines;
    protected int autoResizeMode;
    protected boolean autoCreateColumnsFromModel;
    protected Dimension preferredViewportSize;
    protected boolean rowSelectionAllowed;
    protected boolean cellSelectionEnabled;
    protected transient Component editorComp;
    protected transient TableCellEditor cellEditor;
    protected transient int editingColumn;
    protected transient int editingRow;
    protected transient Hashtable defaultRenderersByColumnClass;
    protected transient Hashtable defaultEditorsByColumnClass;
    protected Color selectionForeground;
    protected Color selectionBackground;
    private SizeSequence rowModel;
    private boolean dragEnabled;
    private boolean surrendersFocusOnKeystroke;
    private PropertyChangeListener editorRemover;
    private boolean columnSelectionAdjusting;
    private boolean rowSelectionAdjusting;
    private Throwable printError;
    private boolean isRowHeightSet;
    private boolean updateSelectionOnSort;
    private transient SortManager sortManager;
    private boolean ignoreSortChange;
    private boolean sorterChanged;
    private boolean autoCreateRowSorter;
    private boolean fillsViewportHeight;
    private DropMode dropMode;
    private transient DropLocation dropLocation;
    
    public JTable() {
        this(null, null, null);
    }
    
    public JTable(final TableModel tableModel) {
        this(tableModel, null, null);
    }
    
    public JTable(final TableModel tableModel, final TableColumnModel tableColumnModel) {
        this(tableModel, tableColumnModel, null);
    }
    
    public JTable(TableModel defaultDataModel, TableColumnModel defaultColumnModel, ListSelectionModel defaultSelectionModel) {
        this.editorRemover = null;
        this.dropMode = DropMode.USE_SELECTION;
        this.setLayout(null);
        this.setFocusTraversalKeys(0, JComponent.getManagingFocusForwardTraversalKeys());
        this.setFocusTraversalKeys(1, JComponent.getManagingFocusBackwardTraversalKeys());
        if (defaultColumnModel == null) {
            defaultColumnModel = this.createDefaultColumnModel();
            this.autoCreateColumnsFromModel = true;
        }
        this.setColumnModel(defaultColumnModel);
        if (defaultSelectionModel == null) {
            defaultSelectionModel = this.createDefaultSelectionModel();
        }
        this.setSelectionModel(defaultSelectionModel);
        if (defaultDataModel == null) {
            defaultDataModel = this.createDefaultDataModel();
        }
        this.setModel(defaultDataModel);
        this.initializeLocalVars();
        this.updateUI();
    }
    
    public JTable(final int n, final int n2) {
        this(new DefaultTableModel(n, n2));
    }
    
    public JTable(final Vector vector, final Vector vector2) {
        this(new DefaultTableModel(vector, vector2));
    }
    
    public JTable(final Object[][] array, final Object[] array2) {
        this(new AbstractTableModel() {
            @Override
            public String getColumnName(final int n) {
                return array2[n].toString();
            }
            
            @Override
            public int getRowCount() {
                return array.length;
            }
            
            @Override
            public int getColumnCount() {
                return array2.length;
            }
            
            @Override
            public Object getValueAt(final int n, final int n2) {
                return array[n][n2];
            }
            
            @Override
            public boolean isCellEditable(final int n, final int n2) {
                return true;
            }
            
            @Override
            public void setValueAt(final Object o, final int n, final int n2) {
                array[n][n2] = o;
                this.fireTableCellUpdated(n, n2);
            }
        });
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        this.configureEnclosingScrollPane();
    }
    
    protected void configureEnclosingScrollPane() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        if (unwrappedParent instanceof JViewport) {
            final Container parent = ((JViewport)unwrappedParent).getParent();
            if (parent instanceof JScrollPane) {
                final JScrollPane scrollPane = (JScrollPane)parent;
                final JViewport viewport = scrollPane.getViewport();
                if (viewport == null || SwingUtilities.getUnwrappedView(viewport) != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(this.getTableHeader());
                this.configureEnclosingScrollPaneUI();
            }
        }
    }
    
    private void configureEnclosingScrollPaneUI() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        if (unwrappedParent instanceof JViewport) {
            final Container parent = ((JViewport)unwrappedParent).getParent();
            if (parent instanceof JScrollPane) {
                final JScrollPane scrollPane = (JScrollPane)parent;
                final JViewport viewport = scrollPane.getViewport();
                if (viewport == null || SwingUtilities.getUnwrappedView(viewport) != this) {
                    return;
                }
                final Border border = scrollPane.getBorder();
                if (border == null || border instanceof UIResource) {
                    final Border border2 = UIManager.getBorder("Table.scrollPaneBorder");
                    if (border2 != null) {
                        scrollPane.setBorder(border2);
                    }
                }
                final Component corner = scrollPane.getCorner("UPPER_TRAILING_CORNER");
                if (corner == null || corner instanceof UIResource) {
                    Component component = null;
                    try {
                        component = (Component)UIManager.get("Table.scrollPaneCornerComponent");
                    }
                    catch (final Exception ex) {}
                    scrollPane.setCorner("UPPER_TRAILING_CORNER", component);
                }
            }
        }
    }
    
    @Override
    public void removeNotify() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
        this.editorRemover = null;
        this.unconfigureEnclosingScrollPane();
        super.removeNotify();
    }
    
    protected void unconfigureEnclosingScrollPane() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        if (unwrappedParent instanceof JViewport) {
            final Container parent = ((JViewport)unwrappedParent).getParent();
            if (parent instanceof JScrollPane) {
                final JScrollPane scrollPane = (JScrollPane)parent;
                final JViewport viewport = scrollPane.getViewport();
                if (viewport == null || SwingUtilities.getUnwrappedView(viewport) != this) {
                    return;
                }
                scrollPane.setColumnHeaderView(null);
                if (scrollPane.getCorner("UPPER_TRAILING_CORNER") instanceof UIResource) {
                    scrollPane.setCorner("UPPER_TRAILING_CORNER", null);
                }
            }
        }
    }
    
    @Override
    void setUIProperty(final String s, final Object o) {
        if (s == "rowHeight") {
            if (!this.isRowHeightSet) {
                this.setRowHeight(((Number)o).intValue());
                this.isRowHeightSet = false;
            }
            return;
        }
        super.setUIProperty(s, o);
    }
    
    @Deprecated
    public static JScrollPane createScrollPaneForTable(final JTable table) {
        return new JScrollPane(table);
    }
    
    public void setTableHeader(final JTableHeader tableHeader) {
        if (this.tableHeader != tableHeader) {
            final JTableHeader tableHeader2 = this.tableHeader;
            if (tableHeader2 != null) {
                tableHeader2.setTable(null);
            }
            if ((this.tableHeader = tableHeader) != null) {
                tableHeader.setTable(this);
            }
            this.firePropertyChange("tableHeader", tableHeader2, tableHeader);
        }
    }
    
    public JTableHeader getTableHeader() {
        return this.tableHeader;
    }
    
    public void setRowHeight(final int rowHeight) {
        if (rowHeight <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
        final int rowHeight2 = this.rowHeight;
        this.rowHeight = rowHeight;
        this.rowModel = null;
        if (this.sortManager != null) {
            this.sortManager.modelRowSizes = null;
        }
        this.isRowHeightSet = true;
        this.resizeAndRepaint();
        this.firePropertyChange("rowHeight", rowHeight2, rowHeight);
    }
    
    public int getRowHeight() {
        return this.rowHeight;
    }
    
    private SizeSequence getRowModel() {
        if (this.rowModel == null) {
            this.rowModel = new SizeSequence(this.getRowCount(), this.getRowHeight());
        }
        return this.rowModel;
    }
    
    public void setRowHeight(final int n, final int n2) {
        if (n2 <= 0) {
            throw new IllegalArgumentException("New row height less than 1");
        }
        this.getRowModel().setSize(n, n2);
        if (this.sortManager != null) {
            this.sortManager.setViewRowHeight(n, n2);
        }
        this.resizeAndRepaint();
    }
    
    public int getRowHeight(final int n) {
        return (this.rowModel == null) ? this.getRowHeight() : this.rowModel.getSize(n);
    }
    
    public void setRowMargin(final int rowMargin) {
        final int rowMargin2 = this.rowMargin;
        this.rowMargin = rowMargin;
        this.resizeAndRepaint();
        this.firePropertyChange("rowMargin", rowMargin2, rowMargin);
    }
    
    public int getRowMargin() {
        return this.rowMargin;
    }
    
    public void setIntercellSpacing(final Dimension dimension) {
        this.setRowMargin(dimension.height);
        this.getColumnModel().setColumnMargin(dimension.width);
        this.resizeAndRepaint();
    }
    
    public Dimension getIntercellSpacing() {
        return new Dimension(this.getColumnModel().getColumnMargin(), this.rowMargin);
    }
    
    public void setGridColor(final Color gridColor) {
        if (gridColor == null) {
            throw new IllegalArgumentException("New color is null");
        }
        this.firePropertyChange("gridColor", this.gridColor, this.gridColor = gridColor);
        this.repaint();
    }
    
    public Color getGridColor() {
        return this.gridColor;
    }
    
    public void setShowGrid(final boolean b) {
        this.setShowHorizontalLines(b);
        this.setShowVerticalLines(b);
        this.repaint();
    }
    
    public void setShowHorizontalLines(final boolean showHorizontalLines) {
        this.firePropertyChange("showHorizontalLines", this.showHorizontalLines, this.showHorizontalLines = showHorizontalLines);
        this.repaint();
    }
    
    public void setShowVerticalLines(final boolean showVerticalLines) {
        this.firePropertyChange("showVerticalLines", this.showVerticalLines, this.showVerticalLines = showVerticalLines);
        this.repaint();
    }
    
    public boolean getShowHorizontalLines() {
        return this.showHorizontalLines;
    }
    
    public boolean getShowVerticalLines() {
        return this.showVerticalLines;
    }
    
    public void setAutoResizeMode(final int autoResizeMode) {
        if (autoResizeMode == 0 || autoResizeMode == 1 || autoResizeMode == 2 || autoResizeMode == 3 || autoResizeMode == 4) {
            final int autoResizeMode2 = this.autoResizeMode;
            this.autoResizeMode = autoResizeMode;
            this.resizeAndRepaint();
            if (this.tableHeader != null) {
                this.tableHeader.resizeAndRepaint();
            }
            this.firePropertyChange("autoResizeMode", autoResizeMode2, this.autoResizeMode);
        }
    }
    
    public int getAutoResizeMode() {
        return this.autoResizeMode;
    }
    
    public void setAutoCreateColumnsFromModel(final boolean autoCreateColumnsFromModel) {
        if (this.autoCreateColumnsFromModel != autoCreateColumnsFromModel) {
            final boolean autoCreateColumnsFromModel2 = this.autoCreateColumnsFromModel;
            this.autoCreateColumnsFromModel = autoCreateColumnsFromModel;
            if (autoCreateColumnsFromModel) {
                this.createDefaultColumnsFromModel();
            }
            this.firePropertyChange("autoCreateColumnsFromModel", autoCreateColumnsFromModel2, autoCreateColumnsFromModel);
        }
    }
    
    public boolean getAutoCreateColumnsFromModel() {
        return this.autoCreateColumnsFromModel;
    }
    
    public void createDefaultColumnsFromModel() {
        final TableModel model = this.getModel();
        if (model != null) {
            final TableColumnModel columnModel = this.getColumnModel();
            while (columnModel.getColumnCount() > 0) {
                columnModel.removeColumn(columnModel.getColumn(0));
            }
            for (int i = 0; i < model.getColumnCount(); ++i) {
                this.addColumn(new TableColumn(i));
            }
        }
    }
    
    public void setDefaultRenderer(final Class<?> clazz, final TableCellRenderer tableCellRenderer) {
        if (tableCellRenderer != null) {
            this.defaultRenderersByColumnClass.put(clazz, tableCellRenderer);
        }
        else {
            this.defaultRenderersByColumnClass.remove(clazz);
        }
    }
    
    public TableCellRenderer getDefaultRenderer(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        final Object value = this.defaultRenderersByColumnClass.get(clazz);
        if (value != null) {
            return (TableCellRenderer)value;
        }
        Class<? super Object> superclass = clazz.getSuperclass();
        if (superclass == null && clazz != Object.class) {
            superclass = Object.class;
        }
        return this.getDefaultRenderer(superclass);
    }
    
    public void setDefaultEditor(final Class<?> clazz, final TableCellEditor tableCellEditor) {
        if (tableCellEditor != null) {
            this.defaultEditorsByColumnClass.put(clazz, tableCellEditor);
        }
        else {
            this.defaultEditorsByColumnClass.remove(clazz);
        }
    }
    
    public TableCellEditor getDefaultEditor(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        final TableCellEditor value = this.defaultEditorsByColumnClass.get(clazz);
        if (value != null) {
            return value;
        }
        return this.getDefaultEditor(clazz.getSuperclass());
    }
    
    public void setDragEnabled(final boolean dragEnabled) {
        if (dragEnabled && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        this.dragEnabled = dragEnabled;
    }
    
    public boolean getDragEnabled() {
        return this.dragEnabled;
    }
    
    public final void setDropMode(final DropMode dropMode) {
        if (dropMode != null) {
            switch (dropMode) {
                case USE_SELECTION:
                case ON:
                case INSERT:
                case INSERT_ROWS:
                case INSERT_COLS:
                case ON_OR_INSERT:
                case ON_OR_INSERT_ROWS:
                case ON_OR_INSERT_COLS: {
                    this.dropMode = dropMode;
                    return;
                }
            }
        }
        throw new IllegalArgumentException(dropMode + ": Unsupported drop mode for table");
    }
    
    public final DropMode getDropMode() {
        return this.dropMode;
    }
    
    @Override
    DropLocation dropLocationForPoint(final Point point) {
        DropLocation dropLocation = null;
        int rowAtPoint = this.rowAtPoint(point);
        int columnAtPoint = this.columnAtPoint(point);
        final boolean b = Boolean.TRUE == this.getClientProperty("Table.isFileList") && SwingUtilities2.pointOutsidePrefSize(this, rowAtPoint, columnAtPoint, point);
        final Rectangle cellRect = this.getCellRect(rowAtPoint, columnAtPoint, true);
        boolean b2 = false;
        final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
        switch (this.dropMode) {
            case USE_SELECTION:
            case ON: {
                if (rowAtPoint == -1 || columnAtPoint == -1 || b) {
                    dropLocation = new DropLocation(point, -1, -1, false, false);
                    break;
                }
                dropLocation = new DropLocation(point, rowAtPoint, columnAtPoint, false, false);
                break;
            }
            case INSERT: {
                if (rowAtPoint == -1 && columnAtPoint == -1) {
                    dropLocation = new DropLocation(point, 0, 0, true, true);
                    break;
                }
                final SwingUtilities2.Section liesInHorizontal = SwingUtilities2.liesInHorizontal(cellRect, point, leftToRight, true);
                if (rowAtPoint == -1) {
                    if (liesInHorizontal == SwingUtilities2.Section.LEADING) {
                        dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint, true, true);
                        break;
                    }
                    if (liesInHorizontal == SwingUtilities2.Section.TRAILING) {
                        dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint + 1, true, true);
                        break;
                    }
                    dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint, true, false);
                    break;
                }
                else {
                    if (liesInHorizontal == SwingUtilities2.Section.LEADING || liesInHorizontal == SwingUtilities2.Section.TRAILING) {
                        final SwingUtilities2.Section liesInVertical = SwingUtilities2.liesInVertical(cellRect, point, true);
                        if (liesInVertical == SwingUtilities2.Section.LEADING) {
                            b2 = true;
                        }
                        else if (liesInVertical == SwingUtilities2.Section.TRAILING) {
                            ++rowAtPoint;
                            b2 = true;
                        }
                        dropLocation = new DropLocation(point, rowAtPoint, (liesInHorizontal == SwingUtilities2.Section.TRAILING) ? (columnAtPoint + 1) : columnAtPoint, b2, true);
                        break;
                    }
                    if (SwingUtilities2.liesInVertical(cellRect, point, false) == SwingUtilities2.Section.TRAILING) {
                        ++rowAtPoint;
                    }
                    dropLocation = new DropLocation(point, rowAtPoint, columnAtPoint, true, false);
                    break;
                }
                break;
            }
            case INSERT_ROWS: {
                if (rowAtPoint == -1 && columnAtPoint == -1) {
                    dropLocation = new DropLocation(point, -1, -1, false, false);
                    break;
                }
                if (rowAtPoint == -1) {
                    dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint, true, false);
                    break;
                }
                if (SwingUtilities2.liesInVertical(cellRect, point, false) == SwingUtilities2.Section.TRAILING) {
                    ++rowAtPoint;
                }
                dropLocation = new DropLocation(point, rowAtPoint, columnAtPoint, true, false);
                break;
            }
            case ON_OR_INSERT_ROWS: {
                if (rowAtPoint == -1 && columnAtPoint == -1) {
                    dropLocation = new DropLocation(point, -1, -1, false, false);
                    break;
                }
                if (rowAtPoint == -1) {
                    dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint, true, false);
                    break;
                }
                final SwingUtilities2.Section liesInVertical2 = SwingUtilities2.liesInVertical(cellRect, point, true);
                if (liesInVertical2 == SwingUtilities2.Section.LEADING) {
                    b2 = true;
                }
                else if (liesInVertical2 == SwingUtilities2.Section.TRAILING) {
                    ++rowAtPoint;
                    b2 = true;
                }
                dropLocation = new DropLocation(point, rowAtPoint, columnAtPoint, b2, false);
                break;
            }
            case INSERT_COLS: {
                if (rowAtPoint == -1) {
                    dropLocation = new DropLocation(point, -1, -1, false, false);
                    break;
                }
                if (columnAtPoint == -1) {
                    dropLocation = new DropLocation(point, this.getColumnCount(), columnAtPoint, false, true);
                    break;
                }
                if (SwingUtilities2.liesInHorizontal(cellRect, point, leftToRight, false) == SwingUtilities2.Section.TRAILING) {
                    ++columnAtPoint;
                }
                dropLocation = new DropLocation(point, rowAtPoint, columnAtPoint, false, true);
                break;
            }
            case ON_OR_INSERT_COLS: {
                if (rowAtPoint == -1) {
                    dropLocation = new DropLocation(point, -1, -1, false, false);
                    break;
                }
                if (columnAtPoint == -1) {
                    dropLocation = new DropLocation(point, rowAtPoint, this.getColumnCount(), false, true);
                    break;
                }
                final SwingUtilities2.Section liesInHorizontal2 = SwingUtilities2.liesInHorizontal(cellRect, point, leftToRight, true);
                if (liesInHorizontal2 == SwingUtilities2.Section.LEADING) {
                    b2 = true;
                }
                else if (liesInHorizontal2 == SwingUtilities2.Section.TRAILING) {
                    ++columnAtPoint;
                    b2 = true;
                }
                dropLocation = new DropLocation(point, rowAtPoint, columnAtPoint, false, b2);
                break;
            }
            case ON_OR_INSERT: {
                if (rowAtPoint == -1 && columnAtPoint == -1) {
                    dropLocation = new DropLocation(point, 0, 0, true, true);
                    break;
                }
                final SwingUtilities2.Section liesInHorizontal3 = SwingUtilities2.liesInHorizontal(cellRect, point, leftToRight, true);
                if (rowAtPoint != -1) {
                    final SwingUtilities2.Section liesInVertical3 = SwingUtilities2.liesInVertical(cellRect, point, true);
                    if (liesInVertical3 == SwingUtilities2.Section.LEADING) {
                        b2 = true;
                    }
                    else if (liesInVertical3 == SwingUtilities2.Section.TRAILING) {
                        ++rowAtPoint;
                        b2 = true;
                    }
                    dropLocation = new DropLocation(point, rowAtPoint, (liesInHorizontal3 == SwingUtilities2.Section.TRAILING) ? (columnAtPoint + 1) : columnAtPoint, b2, liesInHorizontal3 != SwingUtilities2.Section.MIDDLE);
                    break;
                }
                if (liesInHorizontal3 == SwingUtilities2.Section.LEADING) {
                    dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint, true, true);
                    break;
                }
                if (liesInHorizontal3 == SwingUtilities2.Section.TRAILING) {
                    dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint + 1, true, true);
                    break;
                }
                dropLocation = new DropLocation(point, this.getRowCount(), columnAtPoint, true, false);
                break;
            }
            default: {
                assert false : "Unexpected drop mode";
                break;
            }
        }
        return dropLocation;
    }
    
    @Override
    Object setDropLocation(final TransferHandler.DropLocation dropLocation, final Object o, final boolean b) {
        Object o2 = null;
        final DropLocation dropLocation2 = (DropLocation)dropLocation;
        if (this.dropMode == DropMode.USE_SELECTION) {
            if (dropLocation2 == null) {
                if (!b && o != null) {
                    this.clearSelection();
                    final int[] array = ((int[][])o)[0];
                    final int[] array2 = ((int[][])o)[1];
                    final int[] array3 = ((int[][])o)[2];
                    for (final int n : array) {
                        this.addRowSelectionInterval(n, n);
                    }
                    for (final int n2 : array2) {
                        this.addColumnSelectionInterval(n2, n2);
                    }
                    SwingUtilities2.setLeadAnchorWithoutSelection(this.getSelectionModel(), array3[1], array3[0]);
                    SwingUtilities2.setLeadAnchorWithoutSelection(this.getColumnModel().getSelectionModel(), array3[3], array3[2]);
                }
            }
            else {
                if (this.dropLocation == null) {
                    o2 = new int[][] { this.getSelectedRows(), this.getSelectedColumns(), { this.getAdjustedIndex(this.getSelectionModel().getAnchorSelectionIndex(), true), this.getAdjustedIndex(this.getSelectionModel().getLeadSelectionIndex(), true), this.getAdjustedIndex(this.getColumnModel().getSelectionModel().getAnchorSelectionIndex(), false), this.getAdjustedIndex(this.getColumnModel().getSelectionModel().getLeadSelectionIndex(), false) } };
                }
                else {
                    o2 = o;
                }
                if (dropLocation2.getRow() == -1) {
                    this.clearSelectionAndLeadAnchor();
                }
                else {
                    this.setRowSelectionInterval(dropLocation2.getRow(), dropLocation2.getRow());
                    this.setColumnSelectionInterval(dropLocation2.getColumn(), dropLocation2.getColumn());
                }
            }
        }
        this.firePropertyChange("dropLocation", this.dropLocation, this.dropLocation = dropLocation2);
        return o2;
    }
    
    public final DropLocation getDropLocation() {
        return this.dropLocation;
    }
    
    public void setAutoCreateRowSorter(final boolean autoCreateRowSorter) {
        final boolean autoCreateRowSorter2 = this.autoCreateRowSorter;
        this.autoCreateRowSorter = autoCreateRowSorter;
        if (autoCreateRowSorter) {
            this.setRowSorter((RowSorter<? extends TableModel>)new TableRowSorter(this.getModel()));
        }
        this.firePropertyChange("autoCreateRowSorter", autoCreateRowSorter2, autoCreateRowSorter);
    }
    
    public boolean getAutoCreateRowSorter() {
        return this.autoCreateRowSorter;
    }
    
    public void setUpdateSelectionOnSort(final boolean updateSelectionOnSort) {
        if (this.updateSelectionOnSort != updateSelectionOnSort) {
            this.updateSelectionOnSort = updateSelectionOnSort;
            this.firePropertyChange("updateSelectionOnSort", !updateSelectionOnSort, updateSelectionOnSort);
        }
    }
    
    public boolean getUpdateSelectionOnSort() {
        return this.updateSelectionOnSort;
    }
    
    public void setRowSorter(final RowSorter<? extends TableModel> rowSorter) {
        Object sorter = null;
        if (this.sortManager != null) {
            sorter = this.sortManager.sorter;
            this.sortManager.dispose();
            this.sortManager = null;
        }
        this.rowModel = null;
        this.clearSelectionAndLeadAnchor();
        if (rowSorter != null) {
            this.sortManager = new SortManager(rowSorter);
        }
        this.resizeAndRepaint();
        this.firePropertyChange("rowSorter", sorter, rowSorter);
        this.firePropertyChange("sorter", sorter, rowSorter);
    }
    
    public RowSorter<? extends TableModel> getRowSorter() {
        return (this.sortManager != null) ? this.sortManager.sorter : null;
    }
    
    public void setSelectionMode(final int n) {
        this.clearSelection();
        this.getSelectionModel().setSelectionMode(n);
        this.getColumnModel().getSelectionModel().setSelectionMode(n);
    }
    
    public void setRowSelectionAllowed(final boolean rowSelectionAllowed) {
        final boolean rowSelectionAllowed2 = this.rowSelectionAllowed;
        this.rowSelectionAllowed = rowSelectionAllowed;
        if (rowSelectionAllowed2 != rowSelectionAllowed) {
            this.repaint();
        }
        this.firePropertyChange("rowSelectionAllowed", rowSelectionAllowed2, rowSelectionAllowed);
    }
    
    public boolean getRowSelectionAllowed() {
        return this.rowSelectionAllowed;
    }
    
    public void setColumnSelectionAllowed(final boolean columnSelectionAllowed) {
        final boolean columnSelectionAllowed2 = this.columnModel.getColumnSelectionAllowed();
        this.columnModel.setColumnSelectionAllowed(columnSelectionAllowed);
        if (columnSelectionAllowed2 != columnSelectionAllowed) {
            this.repaint();
        }
        this.firePropertyChange("columnSelectionAllowed", columnSelectionAllowed2, columnSelectionAllowed);
    }
    
    public boolean getColumnSelectionAllowed() {
        return this.columnModel.getColumnSelectionAllowed();
    }
    
    public void setCellSelectionEnabled(final boolean cellSelectionEnabled) {
        this.setRowSelectionAllowed(cellSelectionEnabled);
        this.setColumnSelectionAllowed(cellSelectionEnabled);
        this.firePropertyChange("cellSelectionEnabled", this.cellSelectionEnabled, this.cellSelectionEnabled = cellSelectionEnabled);
    }
    
    public boolean getCellSelectionEnabled() {
        return this.getRowSelectionAllowed() && this.getColumnSelectionAllowed();
    }
    
    public void selectAll() {
        if (this.isEditing()) {
            this.removeEditor();
        }
        if (this.getRowCount() > 0 && this.getColumnCount() > 0) {
            final ListSelectionModel selectionModel = this.selectionModel;
            selectionModel.setValueIsAdjusting(true);
            final int adjustedIndex = this.getAdjustedIndex(selectionModel.getLeadSelectionIndex(), true);
            final int adjustedIndex2 = this.getAdjustedIndex(selectionModel.getAnchorSelectionIndex(), true);
            this.setRowSelectionInterval(0, this.getRowCount() - 1);
            SwingUtilities2.setLeadAnchorWithoutSelection(selectionModel, adjustedIndex, adjustedIndex2);
            selectionModel.setValueIsAdjusting(false);
            final ListSelectionModel selectionModel2 = this.columnModel.getSelectionModel();
            selectionModel2.setValueIsAdjusting(true);
            final int adjustedIndex3 = this.getAdjustedIndex(selectionModel2.getLeadSelectionIndex(), false);
            final int adjustedIndex4 = this.getAdjustedIndex(selectionModel2.getAnchorSelectionIndex(), false);
            this.setColumnSelectionInterval(0, this.getColumnCount() - 1);
            SwingUtilities2.setLeadAnchorWithoutSelection(selectionModel2, adjustedIndex3, adjustedIndex4);
            selectionModel2.setValueIsAdjusting(false);
        }
    }
    
    public void clearSelection() {
        this.selectionModel.clearSelection();
        this.columnModel.getSelectionModel().clearSelection();
    }
    
    private void clearSelectionAndLeadAnchor() {
        this.selectionModel.setValueIsAdjusting(true);
        this.columnModel.getSelectionModel().setValueIsAdjusting(true);
        this.clearSelection();
        this.selectionModel.setAnchorSelectionIndex(-1);
        this.selectionModel.setLeadSelectionIndex(-1);
        this.columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
        this.columnModel.getSelectionModel().setLeadSelectionIndex(-1);
        this.selectionModel.setValueIsAdjusting(false);
        this.columnModel.getSelectionModel().setValueIsAdjusting(false);
    }
    
    private int getAdjustedIndex(final int n, final boolean b) {
        return (n < (b ? this.getRowCount() : this.getColumnCount())) ? n : -1;
    }
    
    private int boundRow(final int n) throws IllegalArgumentException {
        if (n < 0 || n >= this.getRowCount()) {
            throw new IllegalArgumentException("Row index out of range");
        }
        return n;
    }
    
    private int boundColumn(final int n) {
        if (n < 0 || n >= this.getColumnCount()) {
            throw new IllegalArgumentException("Column index out of range");
        }
        return n;
    }
    
    public void setRowSelectionInterval(final int n, final int n2) {
        this.selectionModel.setSelectionInterval(this.boundRow(n), this.boundRow(n2));
    }
    
    public void setColumnSelectionInterval(final int n, final int n2) {
        this.columnModel.getSelectionModel().setSelectionInterval(this.boundColumn(n), this.boundColumn(n2));
    }
    
    public void addRowSelectionInterval(final int n, final int n2) {
        this.selectionModel.addSelectionInterval(this.boundRow(n), this.boundRow(n2));
    }
    
    public void addColumnSelectionInterval(final int n, final int n2) {
        this.columnModel.getSelectionModel().addSelectionInterval(this.boundColumn(n), this.boundColumn(n2));
    }
    
    public void removeRowSelectionInterval(final int n, final int n2) {
        this.selectionModel.removeSelectionInterval(this.boundRow(n), this.boundRow(n2));
    }
    
    public void removeColumnSelectionInterval(final int n, final int n2) {
        this.columnModel.getSelectionModel().removeSelectionInterval(this.boundColumn(n), this.boundColumn(n2));
    }
    
    public int getSelectedRow() {
        return this.selectionModel.getMinSelectionIndex();
    }
    
    public int getSelectedColumn() {
        return this.columnModel.getSelectionModel().getMinSelectionIndex();
    }
    
    public int[] getSelectedRows() {
        final int minSelectionIndex = this.selectionModel.getMinSelectionIndex();
        final int maxSelectionIndex = this.selectionModel.getMaxSelectionIndex();
        if (minSelectionIndex == -1 || maxSelectionIndex == -1) {
            return new int[0];
        }
        final int[] array = new int[1 + (maxSelectionIndex - minSelectionIndex)];
        int n = 0;
        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
            if (this.selectionModel.isSelectedIndex(i)) {
                array[n++] = i;
            }
        }
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public int[] getSelectedColumns() {
        return this.columnModel.getSelectedColumns();
    }
    
    public int getSelectedRowCount() {
        final int minSelectionIndex = this.selectionModel.getMinSelectionIndex();
        final int maxSelectionIndex = this.selectionModel.getMaxSelectionIndex();
        int n = 0;
        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
            if (this.selectionModel.isSelectedIndex(i)) {
                ++n;
            }
        }
        return n;
    }
    
    public int getSelectedColumnCount() {
        return this.columnModel.getSelectedColumnCount();
    }
    
    public boolean isRowSelected(final int n) {
        return this.selectionModel.isSelectedIndex(n);
    }
    
    public boolean isColumnSelected(final int n) {
        return this.columnModel.getSelectionModel().isSelectedIndex(n);
    }
    
    public boolean isCellSelected(final int n, final int n2) {
        return (this.getRowSelectionAllowed() || this.getColumnSelectionAllowed()) && (!this.getRowSelectionAllowed() || this.isRowSelected(n)) && (!this.getColumnSelectionAllowed() || this.isColumnSelected(n2));
    }
    
    private void changeSelectionModel(final ListSelectionModel listSelectionModel, final int n, final boolean b, final boolean b2, final boolean b3, final int anchorSelectionIndex, final boolean b4) {
        if (b2) {
            if (b) {
                if (b4) {
                    listSelectionModel.addSelectionInterval(anchorSelectionIndex, n);
                }
                else {
                    listSelectionModel.removeSelectionInterval(anchorSelectionIndex, n);
                    if (Boolean.TRUE == this.getClientProperty("Table.isFileList")) {
                        listSelectionModel.addSelectionInterval(n, n);
                        listSelectionModel.setAnchorSelectionIndex(anchorSelectionIndex);
                    }
                }
            }
            else {
                listSelectionModel.setSelectionInterval(anchorSelectionIndex, n);
            }
        }
        else if (b) {
            if (b3) {
                listSelectionModel.removeSelectionInterval(n, n);
            }
            else {
                listSelectionModel.addSelectionInterval(n, n);
            }
        }
        else {
            listSelectionModel.setSelectionInterval(n, n);
        }
    }
    
    public void changeSelection(final int n, final int n2, final boolean b, final boolean b2) {
        final ListSelectionModel selectionModel = this.getSelectionModel();
        final ListSelectionModel selectionModel2 = this.getColumnModel().getSelectionModel();
        int adjustedIndex = this.getAdjustedIndex(selectionModel.getAnchorSelectionIndex(), true);
        int adjustedIndex2 = this.getAdjustedIndex(selectionModel2.getAnchorSelectionIndex(), false);
        boolean b3 = true;
        if (adjustedIndex == -1) {
            if (this.getRowCount() > 0) {
                adjustedIndex = 0;
            }
            b3 = false;
        }
        if (adjustedIndex2 == -1) {
            if (this.getColumnCount() > 0) {
                adjustedIndex2 = 0;
            }
            b3 = false;
        }
        final boolean cellSelected = this.isCellSelected(n, n2);
        final boolean b4 = b3 && this.isCellSelected(adjustedIndex, adjustedIndex2);
        this.changeSelectionModel(selectionModel2, n2, b, b2, cellSelected, adjustedIndex2, b4);
        this.changeSelectionModel(selectionModel, n, b, b2, cellSelected, adjustedIndex, b4);
        if (this.getAutoscrolls()) {
            final Rectangle cellRect = this.getCellRect(n, n2, false);
            if (cellRect != null) {
                this.scrollRectToVisible(cellRect);
            }
        }
    }
    
    public Color getSelectionForeground() {
        return this.selectionForeground;
    }
    
    public void setSelectionForeground(final Color selectionForeground) {
        this.firePropertyChange("selectionForeground", this.selectionForeground, this.selectionForeground = selectionForeground);
        this.repaint();
    }
    
    public Color getSelectionBackground() {
        return this.selectionBackground;
    }
    
    public void setSelectionBackground(final Color selectionBackground) {
        this.firePropertyChange("selectionBackground", this.selectionBackground, this.selectionBackground = selectionBackground);
        this.repaint();
    }
    
    public TableColumn getColumn(final Object o) {
        final TableColumnModel columnModel = this.getColumnModel();
        return columnModel.getColumn(columnModel.getColumnIndex(o));
    }
    
    public int convertColumnIndexToModel(final int n) {
        return SwingUtilities2.convertColumnIndexToModel(this.getColumnModel(), n);
    }
    
    public int convertColumnIndexToView(final int n) {
        return SwingUtilities2.convertColumnIndexToView(this.getColumnModel(), n);
    }
    
    public int convertRowIndexToView(final int n) {
        final RowSorter<? extends TableModel> rowSorter = this.getRowSorter();
        if (rowSorter != null) {
            return rowSorter.convertRowIndexToView(n);
        }
        return n;
    }
    
    public int convertRowIndexToModel(final int n) {
        final RowSorter<? extends TableModel> rowSorter = this.getRowSorter();
        if (rowSorter != null) {
            return rowSorter.convertRowIndexToModel(n);
        }
        return n;
    }
    
    public int getRowCount() {
        final RowSorter<? extends TableModel> rowSorter = this.getRowSorter();
        if (rowSorter != null) {
            return rowSorter.getViewRowCount();
        }
        return this.getModel().getRowCount();
    }
    
    public int getColumnCount() {
        return this.getColumnModel().getColumnCount();
    }
    
    public String getColumnName(final int n) {
        return this.getModel().getColumnName(this.convertColumnIndexToModel(n));
    }
    
    public Class<?> getColumnClass(final int n) {
        return this.getModel().getColumnClass(this.convertColumnIndexToModel(n));
    }
    
    public Object getValueAt(final int n, final int n2) {
        return this.getModel().getValueAt(this.convertRowIndexToModel(n), this.convertColumnIndexToModel(n2));
    }
    
    public void setValueAt(final Object o, final int n, final int n2) {
        this.getModel().setValueAt(o, this.convertRowIndexToModel(n), this.convertColumnIndexToModel(n2));
    }
    
    public boolean isCellEditable(final int n, final int n2) {
        return this.getModel().isCellEditable(this.convertRowIndexToModel(n), this.convertColumnIndexToModel(n2));
    }
    
    public void addColumn(final TableColumn tableColumn) {
        if (tableColumn.getHeaderValue() == null) {
            tableColumn.setHeaderValue(this.getModel().getColumnName(tableColumn.getModelIndex()));
        }
        this.getColumnModel().addColumn(tableColumn);
    }
    
    public void removeColumn(final TableColumn tableColumn) {
        this.getColumnModel().removeColumn(tableColumn);
    }
    
    public void moveColumn(final int n, final int n2) {
        this.getColumnModel().moveColumn(n, n2);
    }
    
    public int columnAtPoint(final Point point) {
        int x = point.x;
        if (!this.getComponentOrientation().isLeftToRight()) {
            x = this.getWidth() - x - 1;
        }
        return this.getColumnModel().getColumnIndexAtX(x);
    }
    
    public int rowAtPoint(final Point point) {
        final int y = point.y;
        final int n = (this.rowModel == null) ? (y / this.getRowHeight()) : this.rowModel.getIndex(y);
        if (n < 0) {
            return -1;
        }
        if (n >= this.getRowCount()) {
            return -1;
        }
        return n;
    }
    
    public Rectangle getCellRect(final int n, final int n2, final boolean b) {
        final Rectangle rectangle = new Rectangle();
        boolean b2 = true;
        if (n < 0) {
            b2 = false;
        }
        else if (n >= this.getRowCount()) {
            rectangle.y = this.getHeight();
            b2 = false;
        }
        else {
            rectangle.height = this.getRowHeight(n);
            rectangle.y = ((this.rowModel == null) ? (n * rectangle.height) : this.rowModel.getPosition(n));
        }
        if (n2 < 0) {
            if (!this.getComponentOrientation().isLeftToRight()) {
                rectangle.x = this.getWidth();
            }
            b2 = false;
        }
        else if (n2 >= this.getColumnCount()) {
            if (this.getComponentOrientation().isLeftToRight()) {
                rectangle.x = this.getWidth();
            }
            b2 = false;
        }
        else {
            final TableColumnModel columnModel = this.getColumnModel();
            if (this.getComponentOrientation().isLeftToRight()) {
                for (int i = 0; i < n2; ++i) {
                    final Rectangle rectangle2 = rectangle;
                    rectangle2.x += columnModel.getColumn(i).getWidth();
                }
            }
            else {
                for (int j = columnModel.getColumnCount() - 1; j > n2; --j) {
                    final Rectangle rectangle3 = rectangle;
                    rectangle3.x += columnModel.getColumn(j).getWidth();
                }
            }
            rectangle.width = columnModel.getColumn(n2).getWidth();
        }
        if (b2 && !b) {
            final int min = Math.min(this.getRowMargin(), rectangle.height);
            final int min2 = Math.min(this.getColumnModel().getColumnMargin(), rectangle.width);
            rectangle.setBounds(rectangle.x + min2 / 2, rectangle.y + min / 2, rectangle.width - min2, rectangle.height - min);
        }
        return rectangle;
    }
    
    private int viewIndexForColumn(final TableColumn tableColumn) {
        final TableColumnModel columnModel = this.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i) {
            if (columnModel.getColumn(i) == tableColumn) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void doLayout() {
        final TableColumn resizingColumn = this.getResizingColumn();
        if (resizingColumn == null) {
            this.setWidthsFromPreferredWidths(false);
        }
        else {
            this.accommodateDelta(this.viewIndexForColumn(resizingColumn), this.getWidth() - this.getColumnModel().getTotalColumnWidth());
            final int n = this.getWidth() - this.getColumnModel().getTotalColumnWidth();
            if (n != 0) {
                resizingColumn.setWidth(resizingColumn.getWidth() + n);
            }
            this.setWidthsFromPreferredWidths(true);
        }
        super.doLayout();
    }
    
    private TableColumn getResizingColumn() {
        return (this.tableHeader == null) ? null : this.tableHeader.getResizingColumn();
    }
    
    @Deprecated
    public void sizeColumnsToFit(final boolean b) {
        final int autoResizeMode = this.autoResizeMode;
        this.setAutoResizeMode(b ? 3 : 4);
        this.sizeColumnsToFit(-1);
        this.setAutoResizeMode(autoResizeMode);
    }
    
    public void sizeColumnsToFit(final int n) {
        if (n == -1) {
            this.setWidthsFromPreferredWidths(false);
        }
        else if (this.autoResizeMode == 0) {
            final TableColumn column = this.getColumnModel().getColumn(n);
            column.setPreferredWidth(column.getWidth());
        }
        else {
            this.accommodateDelta(n, this.getWidth() - this.getColumnModel().getTotalColumnWidth());
            this.setWidthsFromPreferredWidths(true);
        }
    }
    
    private void setWidthsFromPreferredWidths(final boolean b) {
        final int width = this.getWidth();
        final int width2 = this.getPreferredSize().width;
        this.adjustSizes(b ? width2 : width, new Resizable3() {
            final /* synthetic */ TableColumnModel val$cm = JTable.this.columnModel;
            
            @Override
            public int getElementCount() {
                return this.val$cm.getColumnCount();
            }
            
            @Override
            public int getLowerBoundAt(final int n) {
                return this.val$cm.getColumn(n).getMinWidth();
            }
            
            @Override
            public int getUpperBoundAt(final int n) {
                return this.val$cm.getColumn(n).getMaxWidth();
            }
            
            @Override
            public int getMidPointAt(final int n) {
                if (!b) {
                    return this.val$cm.getColumn(n).getPreferredWidth();
                }
                return this.val$cm.getColumn(n).getWidth();
            }
            
            @Override
            public void setSizeAt(final int n, final int n2) {
                if (!b) {
                    this.val$cm.getColumn(n2).setWidth(n);
                }
                else {
                    this.val$cm.getColumn(n2).setPreferredWidth(n);
                }
            }
        }, b);
    }
    
    private void accommodateDelta(final int n, final int n2) {
        final int columnCount = this.getColumnCount();
        int n3 = 0;
        int min = 0;
        switch (this.autoResizeMode) {
            case 1: {
                n3 = n + 1;
                min = Math.min(n3 + 1, columnCount);
                break;
            }
            case 2: {
                n3 = n + 1;
                min = columnCount;
                break;
            }
            case 3: {
                n3 = columnCount - 1;
                min = n3 + 1;
                break;
            }
            case 4: {
                n3 = 0;
                min = columnCount;
                break;
            }
            default: {
                return;
            }
        }
        final Resizable3 resizable3 = new Resizable3() {
            final /* synthetic */ TableColumnModel val$cm = JTable.this.columnModel;
            
            @Override
            public int getElementCount() {
                return min - n3;
            }
            
            @Override
            public int getLowerBoundAt(final int n) {
                return this.val$cm.getColumn(n + n3).getMinWidth();
            }
            
            @Override
            public int getUpperBoundAt(final int n) {
                return this.val$cm.getColumn(n + n3).getMaxWidth();
            }
            
            @Override
            public int getMidPointAt(final int n) {
                return this.val$cm.getColumn(n + n3).getWidth();
            }
            
            @Override
            public void setSizeAt(final int width, final int n) {
                this.val$cm.getColumn(n + n3).setWidth(width);
            }
        };
        int n4 = 0;
        for (int i = n3; i < min; ++i) {
            n4 += this.columnModel.getColumn(i).getWidth();
        }
        this.adjustSizes(n4 + n2, resizable3, false);
    }
    
    private void adjustSizes(final long n, final Resizable3 resizable3, final boolean b) {
        final int elementCount = resizable3.getElementCount();
        long n2 = 0L;
        for (int i = 0; i < elementCount; ++i) {
            n2 += resizable3.getMidPointAt(i);
        }
        Resizable2 resizable4;
        if (n < n2 == !b) {
            resizable4 = new Resizable2() {
                @Override
                public int getElementCount() {
                    return resizable3.getElementCount();
                }
                
                @Override
                public int getLowerBoundAt(final int n) {
                    return resizable3.getLowerBoundAt(n);
                }
                
                @Override
                public int getUpperBoundAt(final int n) {
                    return resizable3.getMidPointAt(n);
                }
                
                @Override
                public void setSizeAt(final int n, final int n2) {
                    resizable3.setSizeAt(n, n2);
                }
            };
        }
        else {
            resizable4 = new Resizable2() {
                @Override
                public int getElementCount() {
                    return resizable3.getElementCount();
                }
                
                @Override
                public int getLowerBoundAt(final int n) {
                    return resizable3.getMidPointAt(n);
                }
                
                @Override
                public int getUpperBoundAt(final int n) {
                    return resizable3.getUpperBoundAt(n);
                }
                
                @Override
                public void setSizeAt(final int n, final int n2) {
                    resizable3.setSizeAt(n, n2);
                }
            };
        }
        this.adjustSizes(n, resizable4, !b);
    }
    
    private void adjustSizes(long min, final Resizable2 resizable2, final boolean b) {
        long n = 0L;
        long n2 = 0L;
        for (int i = 0; i < resizable2.getElementCount(); ++i) {
            n += resizable2.getLowerBoundAt(i);
            n2 += resizable2.getUpperBoundAt(i);
        }
        if (b) {
            min = Math.min(Math.max(n, min), n2);
        }
        for (int j = 0; j < resizable2.getElementCount(); ++j) {
            final int lowerBound = resizable2.getLowerBoundAt(j);
            final int upperBound = resizable2.getUpperBoundAt(j);
            int n3;
            if (n == n2) {
                n3 = lowerBound;
            }
            else {
                n3 = (int)Math.round(lowerBound + (min - n) / (double)(n2 - n) * (upperBound - lowerBound));
            }
            resizable2.setSizeAt(n3, j);
            min -= n3;
            n -= lowerBound;
            n2 -= upperBound;
        }
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        String s = null;
        final Point point = mouseEvent.getPoint();
        final int columnAtPoint = this.columnAtPoint(point);
        final int rowAtPoint = this.rowAtPoint(point);
        if (columnAtPoint != -1 && rowAtPoint != -1) {
            final Component prepareRenderer = this.prepareRenderer(this.getCellRenderer(rowAtPoint, columnAtPoint), rowAtPoint, columnAtPoint);
            if (prepareRenderer instanceof JComponent) {
                final Rectangle cellRect = this.getCellRect(rowAtPoint, columnAtPoint, false);
                point.translate(-cellRect.x, -cellRect.y);
                final MouseEvent mouseEvent2 = new MouseEvent(prepareRenderer, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                s = ((JComponent)prepareRenderer).getToolTipText(mouseEvent2);
            }
        }
        if (s == null) {
            s = this.getToolTipText();
        }
        return s;
    }
    
    public void setSurrendersFocusOnKeystroke(final boolean surrendersFocusOnKeystroke) {
        this.surrendersFocusOnKeystroke = surrendersFocusOnKeystroke;
    }
    
    public boolean getSurrendersFocusOnKeystroke() {
        return this.surrendersFocusOnKeystroke;
    }
    
    public boolean editCellAt(final int n, final int n2) {
        return this.editCellAt(n, n2, null);
    }
    
    public boolean editCellAt(final int editingRow, final int editingColumn, final EventObject eventObject) {
        if (this.cellEditor != null && !this.cellEditor.stopCellEditing()) {
            return false;
        }
        if (editingRow < 0 || editingRow >= this.getRowCount() || editingColumn < 0 || editingColumn >= this.getColumnCount()) {
            return false;
        }
        if (!this.isCellEditable(editingRow, editingColumn)) {
            return false;
        }
        if (this.editorRemover == null) {
            final KeyboardFocusManager currentKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            currentKeyboardFocusManager.addPropertyChangeListener("permanentFocusOwner", this.editorRemover = new CellEditorRemover(currentKeyboardFocusManager));
        }
        final TableCellEditor cellEditor = this.getCellEditor(editingRow, editingColumn);
        if (cellEditor == null || !cellEditor.isCellEditable(eventObject)) {
            return false;
        }
        this.editorComp = this.prepareEditor(cellEditor, editingRow, editingColumn);
        if (this.editorComp == null) {
            this.removeEditor();
            return false;
        }
        this.editorComp.setBounds(this.getCellRect(editingRow, editingColumn, false));
        this.add(this.editorComp);
        this.editorComp.validate();
        this.editorComp.repaint();
        this.setCellEditor(cellEditor);
        this.setEditingRow(editingRow);
        this.setEditingColumn(editingColumn);
        cellEditor.addCellEditorListener(this);
        return true;
    }
    
    public boolean isEditing() {
        return this.cellEditor != null;
    }
    
    public Component getEditorComponent() {
        return this.editorComp;
    }
    
    public int getEditingColumn() {
        return this.editingColumn;
    }
    
    public int getEditingRow() {
        return this.editingRow;
    }
    
    public TableUI getUI() {
        return (TableUI)this.ui;
    }
    
    public void setUI(final TableUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            this.repaint();
        }
    }
    
    @Override
    public void updateUI() {
        final TableColumnModel columnModel = this.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); ++i) {
            final TableColumn column = columnModel.getColumn(i);
            SwingUtilities.updateRendererOrEditorUI(column.getCellRenderer());
            SwingUtilities.updateRendererOrEditorUI(column.getCellEditor());
            SwingUtilities.updateRendererOrEditorUI(column.getHeaderRenderer());
        }
        final Enumeration elements = this.defaultRenderersByColumnClass.elements();
        while (elements.hasMoreElements()) {
            SwingUtilities.updateRendererOrEditorUI(elements.nextElement());
        }
        final Enumeration elements2 = this.defaultEditorsByColumnClass.elements();
        while (elements2.hasMoreElements()) {
            SwingUtilities.updateRendererOrEditorUI(elements2.nextElement());
        }
        if (this.tableHeader != null && this.tableHeader.getParent() == null) {
            this.tableHeader.updateUI();
        }
        this.configureEnclosingScrollPaneUI();
        this.setUI((TableUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "TableUI";
    }
    
    public void setModel(final TableModel dataModel) {
        if (dataModel == null) {
            throw new IllegalArgumentException("Cannot set a null TableModel");
        }
        if (this.dataModel != dataModel) {
            final TableModel dataModel2 = this.dataModel;
            if (dataModel2 != null) {
                dataModel2.removeTableModelListener(this);
            }
            (this.dataModel = dataModel).addTableModelListener(this);
            this.tableChanged(new TableModelEvent(dataModel, -1));
            this.firePropertyChange("model", dataModel2, dataModel);
            if (this.getAutoCreateRowSorter()) {
                this.setRowSorter((RowSorter<? extends TableModel>)new TableRowSorter(dataModel));
            }
        }
    }
    
    public TableModel getModel() {
        return this.dataModel;
    }
    
    public void setColumnModel(final TableColumnModel tableColumnModel) {
        if (tableColumnModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }
        final TableColumnModel columnModel = this.columnModel;
        if (tableColumnModel != columnModel) {
            if (columnModel != null) {
                columnModel.removeColumnModelListener(this);
            }
            (this.columnModel = tableColumnModel).addColumnModelListener(this);
            if (this.tableHeader != null) {
                this.tableHeader.setColumnModel(tableColumnModel);
            }
            this.firePropertyChange("columnModel", columnModel, tableColumnModel);
            this.resizeAndRepaint();
        }
    }
    
    public TableColumnModel getColumnModel() {
        return this.columnModel;
    }
    
    public void setSelectionModel(final ListSelectionModel selectionModel) {
        if (selectionModel == null) {
            throw new IllegalArgumentException("Cannot set a null SelectionModel");
        }
        final ListSelectionModel selectionModel2 = this.selectionModel;
        if (selectionModel != selectionModel2) {
            if (selectionModel2 != null) {
                selectionModel2.removeListSelectionListener(this);
            }
            (this.selectionModel = selectionModel).addListSelectionListener(this);
            this.firePropertyChange("selectionModel", selectionModel2, selectionModel);
            this.repaint();
        }
    }
    
    public ListSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    @Override
    public void sorterChanged(final RowSorterEvent rowSorterEvent) {
        if (rowSorterEvent.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
            final JTableHeader tableHeader = this.getTableHeader();
            if (tableHeader != null) {
                tableHeader.repaint();
            }
        }
        else if (rowSorterEvent.getType() == RowSorterEvent.Type.SORTED) {
            this.sorterChanged = true;
            if (!this.ignoreSortChange) {
                this.sortedTableChanged(rowSorterEvent, null);
            }
        }
    }
    
    private void sortedTableChanged(final RowSorterEvent rowSorterEvent, final TableModelEvent tableModelEvent) {
        int convertRowIndexToModel = -1;
        final ModelChange modelChange = (tableModelEvent != null) ? new ModelChange(tableModelEvent) : null;
        if ((modelChange == null || !modelChange.allRowsChanged) && this.editingRow != -1) {
            convertRowIndexToModel = this.convertRowIndexToModel(rowSorterEvent, this.editingRow);
        }
        this.sortManager.prepareForChange(rowSorterEvent, modelChange);
        if (tableModelEvent != null) {
            if (modelChange.type == 0) {
                this.repaintSortedRows(modelChange);
            }
            this.notifySorter(modelChange);
            if (modelChange.type != 0) {
                this.sorterChanged = true;
            }
        }
        else {
            this.sorterChanged = true;
        }
        this.sortManager.processChange(rowSorterEvent, modelChange, this.sorterChanged);
        if (this.sorterChanged) {
            if (this.editingRow != -1) {
                this.restoreSortingEditingRow((convertRowIndexToModel == -1) ? -1 : this.convertRowIndexToView(convertRowIndexToModel, modelChange));
            }
            if (tableModelEvent == null || modelChange.type != 0) {
                this.resizeAndRepaint();
            }
        }
        if (modelChange != null && modelChange.allRowsChanged) {
            this.clearSelectionAndLeadAnchor();
            this.resizeAndRepaint();
        }
    }
    
    private void repaintSortedRows(final ModelChange modelChange) {
        if (modelChange.startModelIndex > modelChange.endModelIndex || modelChange.startModelIndex + 10 < modelChange.endModelIndex) {
            this.repaint();
            return;
        }
        final int column;
        final int n = column = modelChange.event.getColumn();
        int convertColumnIndexToView;
        if (column == -1) {
            convertColumnIndexToView = 0;
        }
        else {
            convertColumnIndexToView = this.convertColumnIndexToView(column);
            if (convertColumnIndexToView == -1) {
                return;
            }
        }
        int i = modelChange.startModelIndex;
        while (i <= modelChange.endModelIndex) {
            final int convertRowIndexToView = this.convertRowIndexToView(i++);
            if (convertRowIndexToView != -1) {
                final Rectangle cellRect = this.getCellRect(convertRowIndexToView, convertColumnIndexToView, false);
                int x = cellRect.x;
                int n2 = cellRect.width;
                if (n == -1) {
                    x = 0;
                    n2 = this.getWidth();
                }
                this.repaint(x, cellRect.y, n2, cellRect.height);
            }
        }
    }
    
    private void restoreSortingSelection(final int[] array, int convertRowIndexToView, final ModelChange modelChange) {
        for (int i = array.length - 1; i >= 0; --i) {
            array[i] = this.convertRowIndexToView(array[i], modelChange);
        }
        convertRowIndexToView = this.convertRowIndexToView(convertRowIndexToView, modelChange);
        if (array.length == 0 || (array.length == 1 && array[0] == this.getSelectedRow())) {
            return;
        }
        this.selectionModel.setValueIsAdjusting(true);
        this.selectionModel.clearSelection();
        for (int j = array.length - 1; j >= 0; --j) {
            if (array[j] != -1) {
                this.selectionModel.addSelectionInterval(array[j], array[j]);
            }
        }
        SwingUtilities2.setLeadAnchorWithoutSelection(this.selectionModel, convertRowIndexToView, convertRowIndexToView);
        this.selectionModel.setValueIsAdjusting(false);
    }
    
    private void restoreSortingEditingRow(final int editingRow) {
        if (editingRow == -1) {
            final TableCellEditor cellEditor = this.getCellEditor();
            if (cellEditor != null) {
                cellEditor.cancelCellEditing();
                if (this.getCellEditor() != null) {
                    this.removeEditor();
                }
            }
        }
        else {
            this.editingRow = editingRow;
            this.repaint();
        }
    }
    
    private void notifySorter(final ModelChange modelChange) {
        try {
            this.ignoreSortChange = true;
            this.sorterChanged = false;
            switch (modelChange.type) {
                case 0: {
                    if (modelChange.event.getLastRow() == Integer.MAX_VALUE) {
                        this.sortManager.sorter.allRowsChanged();
                        break;
                    }
                    if (modelChange.event.getColumn() == -1) {
                        this.sortManager.sorter.rowsUpdated(modelChange.startModelIndex, modelChange.endModelIndex);
                        break;
                    }
                    this.sortManager.sorter.rowsUpdated(modelChange.startModelIndex, modelChange.endModelIndex, modelChange.event.getColumn());
                    break;
                }
                case 1: {
                    this.sortManager.sorter.rowsInserted(modelChange.startModelIndex, modelChange.endModelIndex);
                    break;
                }
                case -1: {
                    this.sortManager.sorter.rowsDeleted(modelChange.startModelIndex, modelChange.endModelIndex);
                    break;
                }
            }
        }
        finally {
            this.ignoreSortChange = false;
        }
    }
    
    private int convertRowIndexToView(final int n, final ModelChange modelChange) {
        if (n < 0) {
            return -1;
        }
        if (modelChange != null && n >= modelChange.startModelIndex) {
            if (modelChange.type == 1) {
                if (n + modelChange.length >= modelChange.modelRowCount) {
                    return -1;
                }
                return this.sortManager.sorter.convertRowIndexToView(n + modelChange.length);
            }
            else if (modelChange.type == -1) {
                if (n <= modelChange.endModelIndex) {
                    return -1;
                }
                if (n - modelChange.length >= modelChange.modelRowCount) {
                    return -1;
                }
                return this.sortManager.sorter.convertRowIndexToView(n - modelChange.length);
            }
        }
        if (n >= this.getModel().getRowCount()) {
            return -1;
        }
        return this.sortManager.sorter.convertRowIndexToView(n);
    }
    
    private int[] convertSelectionToModel(final RowSorterEvent rowSorterEvent) {
        final int[] selectedRows = this.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; --i) {
            selectedRows[i] = this.convertRowIndexToModel(rowSorterEvent, selectedRows[i]);
        }
        return selectedRows;
    }
    
    private int convertRowIndexToModel(final RowSorterEvent rowSorterEvent, final int n) {
        if (rowSorterEvent != null) {
            if (rowSorterEvent.getPreviousRowCount() == 0) {
                return n;
            }
            return rowSorterEvent.convertPreviousRowIndexToModel(n);
        }
        else {
            if (n < 0 || n >= this.getRowCount()) {
                return -1;
            }
            return this.convertRowIndexToModel(n);
        }
    }
    
    @Override
    public void tableChanged(final TableModelEvent tableModelEvent) {
        if (tableModelEvent == null || tableModelEvent.getFirstRow() == -1) {
            this.clearSelectionAndLeadAnchor();
            this.rowModel = null;
            if (this.sortManager != null) {
                try {
                    this.ignoreSortChange = true;
                    this.sortManager.sorter.modelStructureChanged();
                }
                finally {
                    this.ignoreSortChange = false;
                }
                this.sortManager.allChanged();
            }
            if (this.getAutoCreateColumnsFromModel()) {
                this.createDefaultColumnsFromModel();
                return;
            }
            this.resizeAndRepaint();
        }
        else {
            if (this.sortManager != null) {
                this.sortedTableChanged(null, tableModelEvent);
                return;
            }
            if (this.rowModel != null) {
                this.repaint();
            }
            if (tableModelEvent.getType() == 1) {
                this.tableRowsInserted(tableModelEvent);
                return;
            }
            if (tableModelEvent.getType() == -1) {
                this.tableRowsDeleted(tableModelEvent);
                return;
            }
            final int column = tableModelEvent.getColumn();
            final int firstRow = tableModelEvent.getFirstRow();
            final int lastRow = tableModelEvent.getLastRow();
            Rectangle cellRect;
            if (column == -1) {
                cellRect = new Rectangle(0, firstRow * this.getRowHeight(), this.getColumnModel().getTotalColumnWidth(), 0);
            }
            else {
                cellRect = this.getCellRect(firstRow, this.convertColumnIndexToView(column), false);
            }
            if (lastRow != Integer.MAX_VALUE) {
                cellRect.height = (lastRow - firstRow + 1) * this.getRowHeight();
                this.repaint(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
            }
            else {
                this.clearSelectionAndLeadAnchor();
                this.resizeAndRepaint();
                this.rowModel = null;
            }
        }
    }
    
    private void tableRowsInserted(final TableModelEvent tableModelEvent) {
        int firstRow = tableModelEvent.getFirstRow();
        int lastRow = tableModelEvent.getLastRow();
        if (firstRow < 0) {
            firstRow = 0;
        }
        if (lastRow < 0) {
            lastRow = this.getRowCount() - 1;
        }
        final int n = lastRow - firstRow + 1;
        this.selectionModel.insertIndexInterval(firstRow, n, true);
        if (this.rowModel != null) {
            this.rowModel.insertEntries(firstRow, n, this.getRowHeight());
        }
        final int rowHeight = this.getRowHeight();
        final Rectangle rectangle = new Rectangle(0, firstRow * rowHeight, this.getColumnModel().getTotalColumnWidth(), (this.getRowCount() - firstRow) * rowHeight);
        this.revalidate();
        this.repaint(rectangle);
    }
    
    private void tableRowsDeleted(final TableModelEvent tableModelEvent) {
        int firstRow = tableModelEvent.getFirstRow();
        int lastRow = tableModelEvent.getLastRow();
        if (firstRow < 0) {
            firstRow = 0;
        }
        if (lastRow < 0) {
            lastRow = this.getRowCount() - 1;
        }
        final int n = lastRow - firstRow + 1;
        final int n2 = this.getRowCount() + n;
        this.selectionModel.removeIndexInterval(firstRow, lastRow);
        if (this.rowModel != null) {
            this.rowModel.removeEntries(firstRow, n);
        }
        final int rowHeight = this.getRowHeight();
        final Rectangle rectangle = new Rectangle(0, firstRow * rowHeight, this.getColumnModel().getTotalColumnWidth(), (n2 - firstRow) * rowHeight);
        this.revalidate();
        this.repaint(rectangle);
    }
    
    @Override
    public void columnAdded(final TableColumnModelEvent tableColumnModelEvent) {
        if (this.isEditing()) {
            this.removeEditor();
        }
        this.resizeAndRepaint();
    }
    
    @Override
    public void columnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
        if (this.isEditing()) {
            this.removeEditor();
        }
        this.resizeAndRepaint();
    }
    
    @Override
    public void columnMoved(final TableColumnModelEvent tableColumnModelEvent) {
        if (this.isEditing() && !this.getCellEditor().stopCellEditing()) {
            this.getCellEditor().cancelCellEditing();
        }
        this.repaint();
    }
    
    @Override
    public void columnMarginChanged(final ChangeEvent changeEvent) {
        if (this.isEditing() && !this.getCellEditor().stopCellEditing()) {
            this.getCellEditor().cancelCellEditing();
        }
        final TableColumn resizingColumn = this.getResizingColumn();
        if (resizingColumn != null && this.autoResizeMode == 0) {
            resizingColumn.setPreferredWidth(resizingColumn.getWidth());
        }
        this.resizeAndRepaint();
    }
    
    private int limit(final int n, final int n2, final int n3) {
        return Math.min(n3, Math.max(n, n2));
    }
    
    @Override
    public void columnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
        final boolean valueIsAdjusting = listSelectionEvent.getValueIsAdjusting();
        if (this.columnSelectionAdjusting && !valueIsAdjusting) {
            this.columnSelectionAdjusting = false;
            return;
        }
        this.columnSelectionAdjusting = valueIsAdjusting;
        if (this.getRowCount() <= 0 || this.getColumnCount() <= 0) {
            return;
        }
        final int limit = this.limit(listSelectionEvent.getFirstIndex(), 0, this.getColumnCount() - 1);
        final int limit2 = this.limit(listSelectionEvent.getLastIndex(), 0, this.getColumnCount() - 1);
        int n = 0;
        int n2 = this.getRowCount() - 1;
        if (this.getRowSelectionAllowed()) {
            n = this.selectionModel.getMinSelectionIndex();
            n2 = this.selectionModel.getMaxSelectionIndex();
            final int adjustedIndex = this.getAdjustedIndex(this.selectionModel.getLeadSelectionIndex(), true);
            if (n == -1 || n2 == -1) {
                if (adjustedIndex == -1) {
                    return;
                }
                n2 = (n = adjustedIndex);
            }
            else if (adjustedIndex != -1) {
                n = Math.min(n, adjustedIndex);
                n2 = Math.max(n2, adjustedIndex);
            }
        }
        this.repaint(this.getCellRect(n, limit, false).union(this.getCellRect(n2, limit2, false)));
    }
    
    @Override
    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        if (this.sortManager != null) {
            this.sortManager.viewSelectionChanged(listSelectionEvent);
        }
        final boolean valueIsAdjusting = listSelectionEvent.getValueIsAdjusting();
        if (this.rowSelectionAdjusting && !valueIsAdjusting) {
            this.rowSelectionAdjusting = false;
            return;
        }
        this.rowSelectionAdjusting = valueIsAdjusting;
        if (this.getRowCount() <= 0 || this.getColumnCount() <= 0) {
            return;
        }
        this.repaint(this.getCellRect(this.limit(listSelectionEvent.getFirstIndex(), 0, this.getRowCount() - 1), 0, false).union(this.getCellRect(this.limit(listSelectionEvent.getLastIndex(), 0, this.getRowCount() - 1), this.getColumnCount() - 1, false)));
    }
    
    @Override
    public void editingStopped(final ChangeEvent changeEvent) {
        final TableCellEditor cellEditor = this.getCellEditor();
        if (cellEditor != null) {
            this.setValueAt(cellEditor.getCellEditorValue(), this.editingRow, this.editingColumn);
            this.removeEditor();
        }
    }
    
    @Override
    public void editingCanceled(final ChangeEvent changeEvent) {
        this.removeEditor();
    }
    
    public void setPreferredScrollableViewportSize(final Dimension preferredViewportSize) {
        this.preferredViewportSize = preferredViewportSize;
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.preferredViewportSize;
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int n, final int n2) {
        int leadingRow = this.getLeadingRow(rectangle);
        int leadingCol = this.getLeadingCol(rectangle);
        if (n == 1 && leadingRow < 0) {
            return this.getRowHeight();
        }
        if (n == 0 && leadingCol < 0) {
            return 100;
        }
        final Rectangle cellRect = this.getCellRect(leadingRow, leadingCol, true);
        final int leadingEdge = this.leadingEdge(rectangle, n);
        final int leadingEdge2 = this.leadingEdge(cellRect, n);
        int n3;
        if (n == 1) {
            n3 = cellRect.height;
        }
        else {
            n3 = cellRect.width;
        }
        if (leadingEdge == leadingEdge2) {
            if (n2 < 0) {
                int n4 = 0;
                if (n == 1) {
                    while (--leadingRow >= 0) {
                        n4 = this.getRowHeight(leadingRow);
                        if (n4 != 0) {
                            break;
                        }
                    }
                }
                else {
                    while (--leadingCol >= 0) {
                        n4 = this.getCellRect(leadingRow, leadingCol, true).width;
                        if (n4 != 0) {
                            break;
                        }
                    }
                }
                return n4;
            }
            return n3;
        }
        else {
            final int abs = Math.abs(leadingEdge - leadingEdge2);
            final int n5 = n3 - abs;
            if (n2 > 0) {
                return n5;
            }
            return abs;
        }
    }
    
    @Override
    public int getScrollableBlockIncrement(final Rectangle rectangle, final int n, final int n2) {
        if (this.getRowCount() == 0) {
            if (1 == n) {
                final int rowHeight = this.getRowHeight();
                return (rowHeight > 0) ? Math.max(rowHeight, rectangle.height / rowHeight * rowHeight) : rectangle.height;
            }
            return rectangle.width;
        }
        else {
            if (null == this.rowModel && 1 == n) {
                final int rowAtPoint = this.rowAtPoint(rectangle.getLocation());
                assert rowAtPoint != -1;
                if (this.getCellRect(rowAtPoint, this.columnAtPoint(rectangle.getLocation()), true).y == rectangle.y) {
                    final int rowHeight2 = this.getRowHeight();
                    assert rowHeight2 > 0;
                    return Math.max(rowHeight2, rectangle.height / rowHeight2 * rowHeight2);
                }
            }
            if (n2 < 0) {
                return this.getPreviousBlockIncrement(rectangle, n);
            }
            return this.getNextBlockIncrement(rectangle, n);
        }
    }
    
    private int getPreviousBlockIncrement(final Rectangle rectangle, final int n) {
        final int leadingEdge = this.leadingEdge(rectangle, n);
        final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
        int n2;
        Point point;
        if (n == 1) {
            n2 = leadingEdge - rectangle.height;
            point = new Point(rectangle.x + (leftToRight ? 0 : rectangle.width), n2);
        }
        else if (leftToRight) {
            n2 = leadingEdge - rectangle.width;
            point = new Point(n2, rectangle.y);
        }
        else {
            n2 = leadingEdge + rectangle.width;
            point = new Point(n2 - 1, rectangle.y);
        }
        final int rowAtPoint = this.rowAtPoint(point);
        final int columnAtPoint = this.columnAtPoint(point);
        int width;
        if (n == 1 & rowAtPoint < 0) {
            width = 0;
        }
        else if (n == 0 & columnAtPoint < 0) {
            if (leftToRight) {
                width = 0;
            }
            else {
                width = this.getWidth();
            }
        }
        else {
            final Rectangle cellRect = this.getCellRect(rowAtPoint, columnAtPoint, true);
            final int leadingEdge2 = this.leadingEdge(cellRect, n);
            final int trailingEdge = this.trailingEdge(cellRect, n);
            if ((n == 1 || leftToRight) && trailingEdge >= leadingEdge) {
                width = leadingEdge2;
            }
            else if (n == 0 && !leftToRight && trailingEdge <= leadingEdge) {
                width = leadingEdge2;
            }
            else if (n2 == leadingEdge2) {
                width = leadingEdge2;
            }
            else {
                width = trailingEdge;
            }
        }
        return Math.abs(leadingEdge - width);
    }
    
    private int getNextBlockIncrement(final Rectangle rectangle, final int n) {
        final int trailingRow = this.getTrailingRow(rectangle);
        final int trailingCol = this.getTrailingCol(rectangle);
        final int leadingEdge = this.leadingEdge(rectangle, n);
        if (n == 1 && trailingRow < 0) {
            return rectangle.height;
        }
        if (n == 0 && trailingCol < 0) {
            return rectangle.width;
        }
        final Rectangle cellRect = this.getCellRect(trailingRow, trailingCol, true);
        final int leadingEdge2 = this.leadingEdge(cellRect, n);
        final int trailingEdge = this.trailingEdge(cellRect, n);
        boolean b;
        if (n == 1 || this.getComponentOrientation().isLeftToRight()) {
            b = (leadingEdge2 <= leadingEdge);
        }
        else {
            b = (leadingEdge2 >= leadingEdge);
        }
        int n2;
        if (b) {
            n2 = trailingEdge;
        }
        else if (trailingEdge == this.trailingEdge(rectangle, n)) {
            n2 = trailingEdge;
        }
        else {
            n2 = leadingEdge2;
        }
        return Math.abs(n2 - leadingEdge);
    }
    
    private int getLeadingRow(final Rectangle rectangle) {
        Point point;
        if (this.getComponentOrientation().isLeftToRight()) {
            point = new Point(rectangle.x, rectangle.y);
        }
        else {
            point = new Point(rectangle.x + rectangle.width - 1, rectangle.y);
        }
        return this.rowAtPoint(point);
    }
    
    private int getLeadingCol(final Rectangle rectangle) {
        Point point;
        if (this.getComponentOrientation().isLeftToRight()) {
            point = new Point(rectangle.x, rectangle.y);
        }
        else {
            point = new Point(rectangle.x + rectangle.width - 1, rectangle.y);
        }
        return this.columnAtPoint(point);
    }
    
    private int getTrailingRow(final Rectangle rectangle) {
        Point point;
        if (this.getComponentOrientation().isLeftToRight()) {
            point = new Point(rectangle.x, rectangle.y + rectangle.height - 1);
        }
        else {
            point = new Point(rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1);
        }
        return this.rowAtPoint(point);
    }
    
    private int getTrailingCol(final Rectangle rectangle) {
        Point point;
        if (this.getComponentOrientation().isLeftToRight()) {
            point = new Point(rectangle.x + rectangle.width - 1, rectangle.y);
        }
        else {
            point = new Point(rectangle.x, rectangle.y);
        }
        return this.columnAtPoint(point);
    }
    
    private int leadingEdge(final Rectangle rectangle, final int n) {
        if (n == 1) {
            return rectangle.y;
        }
        if (this.getComponentOrientation().isLeftToRight()) {
            return rectangle.x;
        }
        return rectangle.x + rectangle.width;
    }
    
    private int trailingEdge(final Rectangle rectangle, final int n) {
        if (n == 1) {
            return rectangle.y + rectangle.height;
        }
        if (this.getComponentOrientation().isLeftToRight()) {
            return rectangle.x + rectangle.width;
        }
        return rectangle.x;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return this.autoResizeMode != 0;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return this.getFillsViewportHeight() && unwrappedParent instanceof JViewport && unwrappedParent.getHeight() > this.getPreferredSize().height;
    }
    
    public void setFillsViewportHeight(final boolean fillsViewportHeight) {
        final boolean fillsViewportHeight2 = this.fillsViewportHeight;
        this.fillsViewportHeight = fillsViewportHeight;
        this.resizeAndRepaint();
        this.firePropertyChange("fillsViewportHeight", fillsViewportHeight2, fillsViewportHeight);
    }
    
    public boolean getFillsViewportHeight() {
        return this.fillsViewportHeight;
    }
    
    @Override
    protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
        boolean b2 = super.processKeyBinding(keyStroke, keyEvent, n, b);
        if (!b2 && n == 1 && this.isFocusOwner() && !Boolean.FALSE.equals(this.getClientProperty("JTable.autoStartsEdit"))) {
            Component component = this.getEditorComponent();
            if (component == null) {
                if (keyEvent == null || keyEvent.getID() != 401) {
                    return false;
                }
                final int keyCode = keyEvent.getKeyCode();
                if (keyCode == 16 || keyCode == 17 || keyCode == 18) {
                    return false;
                }
                final int leadSelectionIndex = this.getSelectionModel().getLeadSelectionIndex();
                final int leadSelectionIndex2 = this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                if (leadSelectionIndex != -1 && leadSelectionIndex2 != -1 && !this.isEditing() && !this.editCellAt(leadSelectionIndex, leadSelectionIndex2, keyEvent)) {
                    return false;
                }
                component = this.getEditorComponent();
                if (component == null) {
                    return false;
                }
            }
            if (component instanceof JComponent) {
                b2 = ((JComponent)component).processKeyBinding(keyStroke, keyEvent, 0, b);
                if (this.getSurrendersFocusOnKeystroke()) {
                    component.requestFocus();
                }
            }
        }
        return b2;
    }
    
    protected void createDefaultRenderers() {
        (this.defaultRenderersByColumnClass = new UIDefaults(8, 0.75f)).put(Object.class, p0 -> new DefaultTableCellRenderer.UIResource());
        this.defaultRenderersByColumnClass.put(Number.class, p0 -> new NumberRenderer());
        this.defaultRenderersByColumnClass.put(Float.class, p0 -> new DoubleRenderer());
        this.defaultRenderersByColumnClass.put(Double.class, p0 -> new DoubleRenderer());
        this.defaultRenderersByColumnClass.put(Date.class, p0 -> new DateRenderer());
        this.defaultRenderersByColumnClass.put(Icon.class, p0 -> new IconRenderer());
        this.defaultRenderersByColumnClass.put(ImageIcon.class, p0 -> new IconRenderer());
        this.defaultRenderersByColumnClass.put(Boolean.class, p0 -> new BooleanRenderer());
    }
    
    protected void createDefaultEditors() {
        (this.defaultEditorsByColumnClass = new UIDefaults(3, 0.75f)).put(Object.class, p0 -> new GenericEditor());
        this.defaultEditorsByColumnClass.put(Number.class, p0 -> new NumberEditor());
        this.defaultEditorsByColumnClass.put(Boolean.class, p0 -> new BooleanEditor());
    }
    
    protected void initializeLocalVars() {
        this.setOpaque(this.updateSelectionOnSort = true);
        this.createDefaultRenderers();
        this.createDefaultEditors();
        this.setTableHeader(this.createDefaultTableHeader());
        this.setShowGrid(true);
        this.setAutoResizeMode(2);
        this.setRowHeight(16);
        this.isRowHeightSet = false;
        this.setRowMargin(1);
        this.setRowSelectionAllowed(true);
        this.setCellEditor(null);
        this.setEditingColumn(-1);
        this.setEditingRow(-1);
        this.setSurrendersFocusOnKeystroke(false);
        this.setPreferredScrollableViewportSize(new Dimension(450, 400));
        ToolTipManager.sharedInstance().registerComponent(this);
        this.setAutoscrolls(true);
    }
    
    protected TableModel createDefaultDataModel() {
        return new DefaultTableModel();
    }
    
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModel();
    }
    
    protected ListSelectionModel createDefaultSelectionModel() {
        return new DefaultListSelectionModel();
    }
    
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(this.columnModel);
    }
    
    protected void resizeAndRepaint() {
        this.revalidate();
        this.repaint();
    }
    
    public TableCellEditor getCellEditor() {
        return this.cellEditor;
    }
    
    public void setCellEditor(final TableCellEditor cellEditor) {
        this.firePropertyChange("tableCellEditor", this.cellEditor, this.cellEditor = cellEditor);
    }
    
    public void setEditingColumn(final int editingColumn) {
        this.editingColumn = editingColumn;
    }
    
    public void setEditingRow(final int editingRow) {
        this.editingRow = editingRow;
    }
    
    public TableCellRenderer getCellRenderer(final int n, final int n2) {
        TableCellRenderer tableCellRenderer = this.getColumnModel().getColumn(n2).getCellRenderer();
        if (tableCellRenderer == null) {
            tableCellRenderer = this.getDefaultRenderer(this.getColumnClass(n2));
        }
        return tableCellRenderer;
    }
    
    public Component prepareRenderer(final TableCellRenderer tableCellRenderer, final int n, final int n2) {
        final Object value = this.getValueAt(n, n2);
        boolean cellSelected = false;
        boolean b = false;
        if (!this.isPaintingForPrint()) {
            cellSelected = this.isCellSelected(n, n2);
            final boolean b2 = this.selectionModel.getLeadSelectionIndex() == n;
            final boolean b3 = this.columnModel.getSelectionModel().getLeadSelectionIndex() == n2;
            b = (b2 && b3 && this.isFocusOwner());
        }
        return tableCellRenderer.getTableCellRendererComponent(this, value, cellSelected, b, n, n2);
    }
    
    public TableCellEditor getCellEditor(final int n, final int n2) {
        TableCellEditor tableCellEditor = this.getColumnModel().getColumn(n2).getCellEditor();
        if (tableCellEditor == null) {
            tableCellEditor = this.getDefaultEditor(this.getColumnClass(n2));
        }
        return tableCellEditor;
    }
    
    public Component prepareEditor(final TableCellEditor tableCellEditor, final int n, final int n2) {
        final Component tableCellEditorComponent = tableCellEditor.getTableCellEditorComponent(this, this.getValueAt(n, n2), this.isCellSelected(n, n2), n, n2);
        if (tableCellEditorComponent instanceof JComponent) {
            final JComponent component = (JComponent)tableCellEditorComponent;
            if (component.getNextFocusableComponent() == null) {
                component.setNextFocusableComponent(this);
            }
        }
        return tableCellEditorComponent;
    }
    
    public void removeEditor() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
        this.editorRemover = null;
        final TableCellEditor cellEditor = this.getCellEditor();
        if (cellEditor != null) {
            cellEditor.removeCellEditorListener(this);
            if (this.editorComp != null) {
                final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                final boolean b = focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, this);
                this.remove(this.editorComp);
                if (b) {
                    this.requestFocusInWindow();
                }
            }
            final Rectangle cellRect = this.getCellRect(this.editingRow, this.editingColumn, false);
            this.setCellEditor(null);
            this.setEditingColumn(-1);
            this.setEditingRow(-1);
            this.editorComp = null;
            this.repaint(cellRect);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("TableUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.ui != null && this.getUIClassID().equals("TableUI")) {
            this.ui.installUI(this);
        }
        this.createDefaultRenderers();
        this.createDefaultEditors();
        if (this.getToolTipText() == null) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
    }
    
    @Override
    void compWriteObjectNotify() {
        super.compWriteObjectNotify();
        if (this.getToolTipText() == null) {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }
    
    @Override
    protected String paramString() {
        final String s = (this.gridColor != null) ? this.gridColor.toString() : "";
        final String s2 = this.showHorizontalLines ? "true" : "false";
        final String s3 = this.showVerticalLines ? "true" : "false";
        String s4;
        if (this.autoResizeMode == 0) {
            s4 = "AUTO_RESIZE_OFF";
        }
        else if (this.autoResizeMode == 1) {
            s4 = "AUTO_RESIZE_NEXT_COLUMN";
        }
        else if (this.autoResizeMode == 2) {
            s4 = "AUTO_RESIZE_SUBSEQUENT_COLUMNS";
        }
        else if (this.autoResizeMode == 3) {
            s4 = "AUTO_RESIZE_LAST_COLUMN";
        }
        else if (this.autoResizeMode == 4) {
            s4 = "AUTO_RESIZE_ALL_COLUMNS";
        }
        else {
            s4 = "";
        }
        return super.paramString() + ",autoCreateColumnsFromModel=" + (this.autoCreateColumnsFromModel ? "true" : "false") + ",autoResizeMode=" + s4 + ",cellSelectionEnabled=" + (this.cellSelectionEnabled ? "true" : "false") + ",editingColumn=" + this.editingColumn + ",editingRow=" + this.editingRow + ",gridColor=" + s + ",preferredViewportSize=" + ((this.preferredViewportSize != null) ? this.preferredViewportSize.toString() : "") + ",rowHeight=" + this.rowHeight + ",rowMargin=" + this.rowMargin + ",rowSelectionAllowed=" + (this.rowSelectionAllowed ? "true" : "false") + ",selectionBackground=" + ((this.selectionBackground != null) ? this.selectionBackground.toString() : "") + ",selectionForeground=" + ((this.selectionForeground != null) ? this.selectionForeground.toString() : "") + ",showHorizontalLines=" + s2 + ",showVerticalLines=" + s3;
    }
    
    public boolean print() throws PrinterException {
        return this.print(PrintMode.FIT_WIDTH);
    }
    
    public boolean print(final PrintMode printMode) throws PrinterException {
        return this.print(printMode, null, null);
    }
    
    public boolean print(final PrintMode printMode, final MessageFormat messageFormat, final MessageFormat messageFormat2) throws PrinterException {
        final boolean b = !GraphicsEnvironment.isHeadless();
        return this.print(printMode, messageFormat, messageFormat2, b, null, b);
    }
    
    public boolean print(final PrintMode printMode, final MessageFormat messageFormat, final MessageFormat messageFormat2, final boolean b, final PrintRequestAttributeSet set, final boolean b2) throws PrinterException, HeadlessException {
        return this.print(printMode, messageFormat, messageFormat2, b, set, b2, null);
    }
    
    public boolean print(final PrintMode printMode, final MessageFormat messageFormat, final MessageFormat messageFormat2, final boolean b, PrintRequestAttributeSet set, final boolean b2, final PrintService printService) throws PrinterException, HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            if (b) {
                throw new HeadlessException("Can't show print dialog.");
            }
            if (b2) {
                throw new HeadlessException("Can't run interactively.");
            }
        }
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (this.isEditing() && !this.getCellEditor().stopCellEditing()) {
            this.getCellEditor().cancelCellEditing();
        }
        if (set == null) {
            set = new HashPrintRequestAttributeSet();
        }
        Printable printable = this.getPrintable(printMode, messageFormat, messageFormat2);
        PrintingStatus printingStatus;
        if (b2) {
            final ThreadSafePrintable threadSafePrintable = new ThreadSafePrintable(printable);
            printingStatus = PrintingStatus.createPrintingStatus(this, printerJob);
            printable = printingStatus.createNotificationPrintable(threadSafePrintable);
        }
        else {
            printingStatus = null;
        }
        printerJob.setPrintable(printable);
        if (printService != null) {
            printerJob.setPrintService(printService);
        }
        if (b && !printerJob.printDialog(set)) {
            return false;
        }
        if (!b2) {
            printerJob.print(set);
            return true;
        }
        this.printError = null;
        final Object o = new Object();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printerJob.print(set);
                }
                catch (final Throwable t) {
                    synchronized (o) {
                        JTable.this.printError = t;
                    }
                }
                finally {
                    printingStatus.dispose();
                }
            }
        }).start();
        printingStatus.showModal(true);
        final Throwable printError;
        synchronized (o) {
            printError = this.printError;
            this.printError = null;
        }
        if (printError == null) {
            return true;
        }
        if (printError instanceof PrinterAbortException) {
            return false;
        }
        if (printError instanceof PrinterException) {
            throw (PrinterException)printError;
        }
        if (printError instanceof RuntimeException) {
            throw (RuntimeException)printError;
        }
        if (printError instanceof Error) {
            throw (Error)printError;
        }
        throw new AssertionError((Object)printError);
    }
    
    public Printable getPrintable(final PrintMode printMode, final MessageFormat messageFormat, final MessageFormat messageFormat2) {
        return new TablePrintable(this, printMode, messageFormat, messageFormat2);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJTable();
        }
        return this.accessibleContext;
    }
    
    public enum PrintMode
    {
        NORMAL, 
        FIT_WIDTH;
    }
    
    public static final class DropLocation extends TransferHandler.DropLocation
    {
        private final int row;
        private final int col;
        private final boolean isInsertRow;
        private final boolean isInsertCol;
        
        private DropLocation(final Point point, final int row, final int col, final boolean isInsertRow, final boolean isInsertCol) {
            super(point);
            this.row = row;
            this.col = col;
            this.isInsertRow = isInsertRow;
            this.isInsertCol = isInsertCol;
        }
        
        public int getRow() {
            return this.row;
        }
        
        public int getColumn() {
            return this.col;
        }
        
        public boolean isInsertRow() {
            return this.isInsertRow;
        }
        
        public boolean isInsertColumn() {
            return this.isInsertCol;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",row=" + this.row + ",column=" + this.col + ",insertRow=" + this.isInsertRow + ",insertColumn=" + this.isInsertCol + "]";
        }
    }
    
    private final class SortManager
    {
        RowSorter<? extends TableModel> sorter;
        private ListSelectionModel modelSelection;
        private int modelLeadIndex;
        private boolean syncingSelection;
        private int[] lastModelSelection;
        private SizeSequence modelRowSizes;
        
        SortManager(final RowSorter<? extends TableModel> sorter) {
            (this.sorter = sorter).addRowSorterListener(JTable.this);
        }
        
        public void dispose() {
            if (this.sorter != null) {
                this.sorter.removeRowSorterListener(JTable.this);
            }
        }
        
        public void setViewRowHeight(final int n, final int n2) {
            if (this.modelRowSizes == null) {
                this.modelRowSizes = new SizeSequence(JTable.this.getModel().getRowCount(), JTable.this.getRowHeight());
            }
            this.modelRowSizes.setSize(JTable.this.convertRowIndexToModel(n), n2);
        }
        
        public void allChanged() {
            this.modelLeadIndex = -1;
            this.modelSelection = null;
            this.modelRowSizes = null;
        }
        
        public void viewSelectionChanged(final ListSelectionEvent listSelectionEvent) {
            if (!this.syncingSelection && this.modelSelection != null) {
                this.modelSelection = null;
            }
        }
        
        public void prepareForChange(final RowSorterEvent rowSorterEvent, final ModelChange modelChange) {
            if (JTable.this.getUpdateSelectionOnSort()) {
                this.cacheSelection(rowSorterEvent, modelChange);
            }
        }
        
        private void cacheSelection(final RowSorterEvent rowSorterEvent, final ModelChange modelChange) {
            if (rowSorterEvent != null) {
                if (this.modelSelection == null && this.sorter.getViewRowCount() != JTable.this.getModel().getRowCount()) {
                    this.modelSelection = new DefaultListSelectionModel();
                    final ListSelectionModel selectionModel = JTable.this.getSelectionModel();
                    final int minSelectionIndex = selectionModel.getMinSelectionIndex();
                    for (int maxSelectionIndex = selectionModel.getMaxSelectionIndex(), i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
                        if (selectionModel.isSelectedIndex(i)) {
                            final int access$200 = JTable.this.convertRowIndexToModel(rowSorterEvent, i);
                            if (access$200 != -1) {
                                this.modelSelection.addSelectionInterval(access$200, access$200);
                            }
                        }
                    }
                    final int access$201 = JTable.this.convertRowIndexToModel(rowSorterEvent, selectionModel.getLeadSelectionIndex());
                    SwingUtilities2.setLeadAnchorWithoutSelection(this.modelSelection, access$201, access$201);
                }
                else if (this.modelSelection == null) {
                    this.cacheModelSelection(rowSorterEvent);
                }
            }
            else if (modelChange.allRowsChanged) {
                this.modelSelection = null;
            }
            else if (this.modelSelection != null) {
                switch (modelChange.type) {
                    case -1: {
                        this.modelSelection.removeIndexInterval(modelChange.startModelIndex, modelChange.endModelIndex);
                        break;
                    }
                    case 1: {
                        this.modelSelection.insertIndexInterval(modelChange.startModelIndex, modelChange.length, true);
                        break;
                    }
                }
            }
            else {
                this.cacheModelSelection(null);
            }
        }
        
        private void cacheModelSelection(final RowSorterEvent rowSorterEvent) {
            this.lastModelSelection = JTable.this.convertSelectionToModel(rowSorterEvent);
            this.modelLeadIndex = JTable.this.convertRowIndexToModel(rowSorterEvent, JTable.this.selectionModel.getLeadSelectionIndex());
        }
        
        public void processChange(final RowSorterEvent rowSorterEvent, final ModelChange modelChange, final boolean b) {
            if (modelChange != null) {
                if (modelChange.allRowsChanged) {
                    this.modelRowSizes = null;
                    JTable.this.rowModel = null;
                }
                else if (this.modelRowSizes != null) {
                    if (modelChange.type == 1) {
                        this.modelRowSizes.insertEntries(modelChange.startModelIndex, modelChange.endModelIndex - modelChange.startModelIndex + 1, JTable.this.getRowHeight());
                    }
                    else if (modelChange.type == -1) {
                        this.modelRowSizes.removeEntries(modelChange.startModelIndex, modelChange.endModelIndex - modelChange.startModelIndex + 1);
                    }
                }
            }
            if (b) {
                this.setViewRowHeightsFromModel();
                this.restoreSelection(modelChange);
            }
        }
        
        private void setViewRowHeightsFromModel() {
            if (this.modelRowSizes != null) {
                JTable.this.rowModel.setSizes(JTable.this.getRowCount(), JTable.this.getRowHeight());
                for (int i = JTable.this.getRowCount() - 1; i >= 0; --i) {
                    JTable.this.rowModel.setSize(i, this.modelRowSizes.getSize(JTable.this.convertRowIndexToModel(i)));
                }
            }
        }
        
        private void restoreSelection(final ModelChange modelChange) {
            this.syncingSelection = true;
            if (this.lastModelSelection != null) {
                JTable.this.restoreSortingSelection(this.lastModelSelection, this.modelLeadIndex, modelChange);
                this.lastModelSelection = null;
            }
            else if (this.modelSelection != null) {
                final ListSelectionModel selectionModel = JTable.this.getSelectionModel();
                selectionModel.setValueIsAdjusting(true);
                selectionModel.clearSelection();
                final int minSelectionIndex = this.modelSelection.getMinSelectionIndex();
                for (int maxSelectionIndex = this.modelSelection.getMaxSelectionIndex(), i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
                    if (this.modelSelection.isSelectedIndex(i)) {
                        final int convertRowIndexToView = JTable.this.convertRowIndexToView(i);
                        if (convertRowIndexToView != -1) {
                            selectionModel.addSelectionInterval(convertRowIndexToView, convertRowIndexToView);
                        }
                    }
                }
                int n = this.modelSelection.getLeadSelectionIndex();
                if (n != -1 && !this.modelSelection.isSelectionEmpty()) {
                    n = JTable.this.convertRowIndexToView(n);
                }
                SwingUtilities2.setLeadAnchorWithoutSelection(selectionModel, n, n);
                selectionModel.setValueIsAdjusting(false);
            }
            this.syncingSelection = false;
        }
    }
    
    private final class ModelChange
    {
        int startModelIndex;
        int endModelIndex;
        int type;
        int modelRowCount;
        TableModelEvent event;
        int length;
        boolean allRowsChanged;
        
        ModelChange(final TableModelEvent event) {
            this.startModelIndex = Math.max(0, event.getFirstRow());
            this.endModelIndex = event.getLastRow();
            this.modelRowCount = JTable.this.getModel().getRowCount();
            if (this.endModelIndex < 0) {
                this.endModelIndex = Math.max(0, this.modelRowCount - 1);
            }
            this.length = this.endModelIndex - this.startModelIndex + 1;
            this.type = event.getType();
            this.event = event;
            this.allRowsChanged = (event.getLastRow() == Integer.MAX_VALUE);
        }
    }
    
    static class NumberRenderer extends UIResource
    {
        public NumberRenderer() {
            this.setHorizontalAlignment(4);
        }
    }
    
    static class DoubleRenderer extends NumberRenderer
    {
        NumberFormat formatter;
        
        public DoubleRenderer() {
        }
        
        public void setValue(final Object o) {
            if (this.formatter == null) {
                this.formatter = NumberFormat.getInstance();
            }
            this.setText((o == null) ? "" : this.formatter.format(o));
        }
    }
    
    static class DateRenderer extends UIResource
    {
        DateFormat formatter;
        
        public DateRenderer() {
        }
        
        public void setValue(final Object o) {
            if (this.formatter == null) {
                this.formatter = DateFormat.getDateInstance();
            }
            this.setText((o == null) ? "" : this.formatter.format(o));
        }
    }
    
    static class IconRenderer extends UIResource
    {
        public IconRenderer() {
            this.setHorizontalAlignment(0);
        }
        
        public void setValue(final Object o) {
            this.setIcon((o instanceof Icon) ? ((Icon)o) : null);
        }
    }
    
    static class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource
    {
        private static final Border noFocusBorder;
        
        public BooleanRenderer() {
            this.setHorizontalAlignment(0);
            this.setBorderPainted(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
            if (b) {
                this.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            }
            else {
                this.setForeground(table.getForeground());
                this.setBackground(table.getBackground());
            }
            this.setSelected(o != null && (boolean)o);
            if (b2) {
                this.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            }
            else {
                this.setBorder(BooleanRenderer.noFocusBorder);
            }
            return this;
        }
        
        static {
            noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        }
    }
    
    static class GenericEditor extends DefaultCellEditor
    {
        Class[] argTypes;
        Constructor constructor;
        Object value;
        
        public GenericEditor() {
            super(new JTextField());
            this.argTypes = new Class[] { String.class };
            this.getComponent().setName("Table.editor");
        }
        
        @Override
        public boolean stopCellEditing() {
            final String value = (String)super.getCellEditorValue();
            try {
                if ("".equals(value)) {
                    if (this.constructor.getDeclaringClass() == String.class) {
                        this.value = value;
                    }
                    return super.stopCellEditing();
                }
                SwingUtilities2.checkAccess(this.constructor.getModifiers());
                this.value = this.constructor.newInstance(value);
            }
            catch (final Exception ex) {
                ((JComponent)this.getComponent()).setBorder(new LineBorder(Color.red));
                return false;
            }
            return super.stopCellEditing();
        }
        
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object o, final boolean b, final int n, final int n2) {
            this.value = null;
            ((JComponent)this.getComponent()).setBorder(new LineBorder(Color.black));
            try {
                Class<?> columnClass = table.getColumnClass(n2);
                if (columnClass == Object.class) {
                    columnClass = String.class;
                }
                ReflectUtil.checkPackageAccess(columnClass);
                SwingUtilities2.checkAccess(columnClass.getModifiers());
                this.constructor = columnClass.getConstructor((Class[])this.argTypes);
            }
            catch (final Exception ex) {
                return null;
            }
            return super.getTableCellEditorComponent(table, o, b, n, n2);
        }
        
        @Override
        public Object getCellEditorValue() {
            return this.value;
        }
    }
    
    static class NumberEditor extends GenericEditor
    {
        public NumberEditor() {
            ((JTextField)this.getComponent()).setHorizontalAlignment(4);
        }
    }
    
    static class BooleanEditor extends DefaultCellEditor
    {
        public BooleanEditor() {
            super(new JCheckBox());
            ((JCheckBox)this.getComponent()).setHorizontalAlignment(0);
        }
    }
    
    class CellEditorRemover implements PropertyChangeListener
    {
        KeyboardFocusManager focusManager;
        
        public CellEditorRemover(final KeyboardFocusManager focusManager) {
            this.focusManager = focusManager;
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (!JTable.this.isEditing() || JTable.this.getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE) {
                return;
            }
            Component component = this.focusManager.getPermanentFocusOwner();
            while (component != null) {
                if (component == JTable.this) {
                    return;
                }
                if (component instanceof Window || (component instanceof Applet && component.getParent() == null)) {
                    if (component == SwingUtilities.getRoot(JTable.this) && !JTable.this.getCellEditor().stopCellEditing()) {
                        JTable.this.getCellEditor().cancelCellEditing();
                        break;
                    }
                    break;
                }
                else {
                    component = component.getParent();
                }
            }
        }
    }
    
    private class ThreadSafePrintable implements Printable
    {
        private Printable printDelegate;
        private int retVal;
        private Throwable retThrowable;
        
        public ThreadSafePrintable(final Printable printDelegate) {
            this.printDelegate = printDelegate;
        }
        
        @Override
        public int print(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
            final Runnable runnable = new Runnable() {
                @Override
                public synchronized void run() {
                    try {
                        ThreadSafePrintable.this.retVal = ThreadSafePrintable.this.printDelegate.print(graphics, pageFormat, n);
                    }
                    catch (final Throwable t) {
                        ThreadSafePrintable.this.retThrowable = t;
                    }
                    finally {
                        this.notifyAll();
                    }
                }
            };
            synchronized (runnable) {
                this.retVal = -1;
                this.retThrowable = null;
                SwingUtilities.invokeLater(runnable);
                while (this.retVal == -1 && this.retThrowable == null) {
                    try {
                        runnable.wait();
                    }
                    catch (final InterruptedException ex) {}
                }
                if (this.retThrowable == null) {
                    return this.retVal;
                }
                if (this.retThrowable instanceof PrinterException) {
                    throw (PrinterException)this.retThrowable;
                }
                if (this.retThrowable instanceof RuntimeException) {
                    throw (RuntimeException)this.retThrowable;
                }
                if (this.retThrowable instanceof Error) {
                    throw (Error)this.retThrowable;
                }
                throw new AssertionError((Object)this.retThrowable);
            }
        }
    }
    
    protected class AccessibleJTable extends AccessibleJComponent implements AccessibleSelection, ListSelectionListener, TableModelListener, TableColumnModelListener, CellEditorListener, PropertyChangeListener, AccessibleExtendedTable
    {
        int previousFocusedRow;
        int previousFocusedCol;
        private Accessible caption;
        private Accessible summary;
        private Accessible[] rowDescription;
        private Accessible[] columnDescription;
        
        protected AccessibleJTable() {
            JTable.this.addPropertyChangeListener(this);
            JTable.this.getSelectionModel().addListSelectionListener(this);
            final TableColumnModel columnModel = JTable.this.getColumnModel();
            columnModel.addColumnModelListener(this);
            columnModel.getSelectionModel().addListSelectionListener(this);
            JTable.this.getModel().addTableModelListener(this);
            this.previousFocusedRow = JTable.this.getSelectionModel().getLeadSelectionIndex();
            this.previousFocusedCol = JTable.this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final Object oldValue = propertyChangeEvent.getOldValue();
            final Object newValue = propertyChangeEvent.getNewValue();
            if (propertyName.compareTo("model") == 0) {
                if (oldValue != null && oldValue instanceof TableModel) {
                    ((TableModel)oldValue).removeTableModelListener(this);
                }
                if (newValue != null && newValue instanceof TableModel) {
                    ((TableModel)newValue).addTableModelListener(this);
                }
            }
            else if (propertyName.compareTo("selectionModel") == 0) {
                final Object source = propertyChangeEvent.getSource();
                if (source == JTable.this) {
                    if (oldValue != null && oldValue instanceof ListSelectionModel) {
                        ((ListSelectionModel)oldValue).removeListSelectionListener(this);
                    }
                    if (newValue != null && newValue instanceof ListSelectionModel) {
                        ((ListSelectionModel)newValue).addListSelectionListener(this);
                    }
                }
                else if (source == JTable.this.getColumnModel()) {
                    if (oldValue != null && oldValue instanceof ListSelectionModel) {
                        ((ListSelectionModel)oldValue).removeListSelectionListener(this);
                    }
                    if (newValue != null && newValue instanceof ListSelectionModel) {
                        ((ListSelectionModel)newValue).addListSelectionListener(this);
                    }
                }
            }
            else if (propertyName.compareTo("columnModel") == 0) {
                if (oldValue != null && oldValue instanceof TableColumnModel) {
                    final TableColumnModel tableColumnModel = (TableColumnModel)oldValue;
                    tableColumnModel.removeColumnModelListener(this);
                    tableColumnModel.getSelectionModel().removeListSelectionListener(this);
                }
                if (newValue != null && newValue instanceof TableColumnModel) {
                    final TableColumnModel tableColumnModel2 = (TableColumnModel)newValue;
                    tableColumnModel2.addColumnModelListener(this);
                    tableColumnModel2.getSelectionModel().addListSelectionListener(this);
                }
            }
            else if (propertyName.compareTo("tableCellEditor") == 0) {
                if (oldValue != null && oldValue instanceof TableCellEditor) {
                    ((TableCellEditor)oldValue).removeCellEditorListener(this);
                }
                if (newValue != null && newValue instanceof TableCellEditor) {
                    ((TableCellEditor)newValue).addCellEditorListener(this);
                }
            }
        }
        
        @Override
        public void tableChanged(final TableModelEvent tableModelEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
            if (tableModelEvent != null) {
                int column = tableModelEvent.getColumn();
                int column2 = tableModelEvent.getColumn();
                if (column == -1) {
                    column = 0;
                    column2 = JTable.this.getColumnCount() - 1;
                }
                this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(tableModelEvent.getType(), tableModelEvent.getFirstRow(), tableModelEvent.getLastRow(), column, column2));
            }
        }
        
        public void tableRowsInserted(final TableModelEvent tableModelEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
            int column = tableModelEvent.getColumn();
            int column2 = tableModelEvent.getColumn();
            if (column == -1) {
                column = 0;
                column2 = JTable.this.getColumnCount() - 1;
            }
            this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(tableModelEvent.getType(), tableModelEvent.getFirstRow(), tableModelEvent.getLastRow(), column, column2));
        }
        
        public void tableRowsDeleted(final TableModelEvent tableModelEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
            int column = tableModelEvent.getColumn();
            int column2 = tableModelEvent.getColumn();
            if (column == -1) {
                column = 0;
                column2 = JTable.this.getColumnCount() - 1;
            }
            this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(tableModelEvent.getType(), tableModelEvent.getFirstRow(), tableModelEvent.getLastRow(), column, column2));
        }
        
        @Override
        public void columnAdded(final TableColumnModelEvent tableColumnModelEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
            this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(1, 0, 0, tableColumnModelEvent.getFromIndex(), tableColumnModelEvent.getToIndex()));
        }
        
        @Override
        public void columnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
            this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(-1, 0, 0, tableColumnModelEvent.getFromIndex(), tableColumnModelEvent.getToIndex()));
        }
        
        @Override
        public void columnMoved(final TableColumnModelEvent tableColumnModelEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
            this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(-1, 0, 0, tableColumnModelEvent.getFromIndex(), tableColumnModelEvent.getFromIndex()));
            this.firePropertyChange("accessibleTableModelChanged", null, new AccessibleJTableModelChange(1, 0, 0, tableColumnModelEvent.getToIndex(), tableColumnModelEvent.getToIndex()));
        }
        
        @Override
        public void columnMarginChanged(final ChangeEvent changeEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
        }
        
        @Override
        public void columnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
        }
        
        @Override
        public void editingStopped(final ChangeEvent changeEvent) {
            this.firePropertyChange("AccessibleVisibleData", null, null);
        }
        
        @Override
        public void editingCanceled(final ChangeEvent changeEvent) {
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            this.firePropertyChange("AccessibleSelection", false, true);
            final int leadSelectionIndex = JTable.this.getSelectionModel().getLeadSelectionIndex();
            final int leadSelectionIndex2 = JTable.this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
            if (leadSelectionIndex != this.previousFocusedRow || leadSelectionIndex2 != this.previousFocusedCol) {
                this.firePropertyChange("AccessibleActiveDescendant", this.getAccessibleAt(this.previousFocusedRow, this.previousFocusedCol), this.getAccessibleAt(leadSelectionIndex, leadSelectionIndex2));
                this.previousFocusedRow = leadSelectionIndex;
                this.previousFocusedCol = leadSelectionIndex2;
            }
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TABLE;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            final int columnAtPoint = JTable.this.columnAtPoint(point);
            final int rowAtPoint = JTable.this.rowAtPoint(point);
            if (columnAtPoint != -1 && rowAtPoint != -1) {
                TableCellRenderer tableCellRenderer = JTable.this.getColumnModel().getColumn(columnAtPoint).getCellRenderer();
                if (tableCellRenderer == null) {
                    tableCellRenderer = JTable.this.getDefaultRenderer(JTable.this.getColumnClass(columnAtPoint));
                }
                tableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, rowAtPoint, columnAtPoint);
                return new AccessibleJTableCell(JTable.this, rowAtPoint, columnAtPoint, this.getAccessibleIndexAt(rowAtPoint, columnAtPoint));
            }
            return null;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return JTable.this.getColumnCount() * JTable.this.getRowCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (n < 0 || n >= this.getAccessibleChildrenCount()) {
                return null;
            }
            final int accessibleColumnAtIndex = this.getAccessibleColumnAtIndex(n);
            final int accessibleRowAtIndex = this.getAccessibleRowAtIndex(n);
            TableCellRenderer tableCellRenderer = JTable.this.getColumnModel().getColumn(accessibleColumnAtIndex).getCellRenderer();
            if (tableCellRenderer == null) {
                tableCellRenderer = JTable.this.getDefaultRenderer(JTable.this.getColumnClass(accessibleColumnAtIndex));
            }
            tableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, accessibleRowAtIndex, accessibleColumnAtIndex);
            return new AccessibleJTableCell(JTable.this, accessibleRowAtIndex, accessibleColumnAtIndex, this.getAccessibleIndexAt(accessibleRowAtIndex, accessibleColumnAtIndex));
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            final int selectedRowCount = JTable.this.getSelectedRowCount();
            final int selectedColumnCount = JTable.this.getSelectedColumnCount();
            if (JTable.this.cellSelectionEnabled) {
                return selectedRowCount * selectedColumnCount;
            }
            if (JTable.this.getRowSelectionAllowed() && JTable.this.getColumnSelectionAllowed()) {
                return selectedRowCount * JTable.this.getColumnCount() + selectedColumnCount * JTable.this.getRowCount() - selectedRowCount * selectedColumnCount;
            }
            if (JTable.this.getRowSelectionAllowed()) {
                return selectedRowCount * JTable.this.getColumnCount();
            }
            if (JTable.this.getColumnSelectionAllowed()) {
                return selectedColumnCount * JTable.this.getRowCount();
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            if (n < 0 || n > this.getAccessibleSelectionCount()) {
                return null;
            }
            JTable.this.getSelectedRowCount();
            final int selectedColumnCount = JTable.this.getSelectedColumnCount();
            final int[] selectedRows = JTable.this.getSelectedRows();
            final int[] selectedColumns = JTable.this.getSelectedColumns();
            final int columnCount = JTable.this.getColumnCount();
            final int rowCount = JTable.this.getRowCount();
            if (JTable.this.cellSelectionEnabled) {
                return this.getAccessibleChild(selectedRows[n / selectedColumnCount] * columnCount + selectedColumns[n % selectedColumnCount]);
            }
            if (JTable.this.getRowSelectionAllowed() && JTable.this.getColumnSelectionAllowed()) {
                int n2 = n;
                int n3 = (selectedRows[0] != 0) ? 1 : 0;
                int i = 0;
                int n4 = -1;
                while (i < selectedRows.length) {
                    switch (n3) {
                        case 0: {
                            if (n2 < columnCount) {
                                return this.getAccessibleChild(selectedRows[i] * columnCount + n2 % columnCount);
                            }
                            n2 -= columnCount;
                            if (i + 1 == selectedRows.length || selectedRows[i] != selectedRows[i + 1] - 1) {
                                n3 = 1;
                                n4 = selectedRows[i];
                            }
                            ++i;
                            continue;
                        }
                        case 1: {
                            if (n2 < selectedColumnCount * (selectedRows[i] - ((n4 == -1) ? 0 : (n4 + 1)))) {
                                return this.getAccessibleChild((((i > 0) ? (selectedRows[i - 1] + 1) : 0) + n2 / selectedColumnCount) * columnCount + selectedColumns[n2 % selectedColumnCount]);
                            }
                            n2 -= selectedColumnCount * (selectedRows[i] - ((n4 == -1) ? 0 : (n4 + 1)));
                            n3 = 0;
                            continue;
                        }
                    }
                }
                if (n2 < selectedColumnCount * (rowCount - ((n4 == -1) ? 0 : (n4 + 1)))) {
                    return this.getAccessibleChild((selectedRows[i - 1] + n2 / selectedColumnCount + 1) * columnCount + selectedColumns[n2 % selectedColumnCount]);
                }
            }
            else {
                if (JTable.this.getRowSelectionAllowed()) {
                    return this.getAccessibleChild(selectedRows[n / columnCount] * columnCount + n % columnCount);
                }
                if (JTable.this.getColumnSelectionAllowed()) {
                    return this.getAccessibleChild(n / selectedColumnCount * columnCount + selectedColumns[n % selectedColumnCount]);
                }
            }
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return JTable.this.isCellSelected(this.getAccessibleRowAtIndex(n), this.getAccessibleColumnAtIndex(n));
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
            JTable.this.changeSelection(this.getAccessibleRowAtIndex(n), this.getAccessibleColumnAtIndex(n), true, false);
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            if (JTable.this.cellSelectionEnabled) {
                final int accessibleColumnAtIndex = this.getAccessibleColumnAtIndex(n);
                final int accessibleRowAtIndex = this.getAccessibleRowAtIndex(n);
                JTable.this.removeRowSelectionInterval(accessibleRowAtIndex, accessibleRowAtIndex);
                JTable.this.removeColumnSelectionInterval(accessibleColumnAtIndex, accessibleColumnAtIndex);
            }
        }
        
        @Override
        public void clearAccessibleSelection() {
            JTable.this.clearSelection();
        }
        
        @Override
        public void selectAllAccessibleSelection() {
            if (JTable.this.cellSelectionEnabled) {
                JTable.this.selectAll();
            }
        }
        
        @Override
        public int getAccessibleRow(final int n) {
            return this.getAccessibleRowAtIndex(n);
        }
        
        @Override
        public int getAccessibleColumn(final int n) {
            return this.getAccessibleColumnAtIndex(n);
        }
        
        @Override
        public int getAccessibleIndex(final int n, final int n2) {
            return this.getAccessibleIndexAt(n, n2);
        }
        
        @Override
        public AccessibleTable getAccessibleTable() {
            return this;
        }
        
        @Override
        public Accessible getAccessibleCaption() {
            return this.caption;
        }
        
        @Override
        public void setAccessibleCaption(final Accessible caption) {
            this.firePropertyChange("accessibleTableCaptionChanged", this.caption, this.caption = caption);
        }
        
        @Override
        public Accessible getAccessibleSummary() {
            return this.summary;
        }
        
        @Override
        public void setAccessibleSummary(final Accessible summary) {
            this.firePropertyChange("accessibleTableSummaryChanged", this.summary, this.summary = summary);
        }
        
        @Override
        public int getAccessibleRowCount() {
            return JTable.this.getRowCount();
        }
        
        @Override
        public int getAccessibleColumnCount() {
            return JTable.this.getColumnCount();
        }
        
        @Override
        public Accessible getAccessibleAt(final int n, final int n2) {
            return this.getAccessibleChild(n * this.getAccessibleColumnCount() + n2);
        }
        
        @Override
        public int getAccessibleRowExtentAt(final int n, final int n2) {
            return 1;
        }
        
        @Override
        public int getAccessibleColumnExtentAt(final int n, final int n2) {
            return 1;
        }
        
        @Override
        public AccessibleTable getAccessibleRowHeader() {
            return null;
        }
        
        @Override
        public void setAccessibleRowHeader(final AccessibleTable accessibleTable) {
        }
        
        @Override
        public AccessibleTable getAccessibleColumnHeader() {
            final JTableHeader tableHeader = JTable.this.getTableHeader();
            return (tableHeader == null) ? null : new AccessibleTableHeader(tableHeader);
        }
        
        @Override
        public void setAccessibleColumnHeader(final AccessibleTable accessibleTable) {
        }
        
        @Override
        public Accessible getAccessibleRowDescription(final int n) {
            if (n < 0 || n >= this.getAccessibleRowCount()) {
                throw new IllegalArgumentException(Integer.toString(n));
            }
            if (this.rowDescription == null) {
                return null;
            }
            return this.rowDescription[n];
        }
        
        @Override
        public void setAccessibleRowDescription(final int n, final Accessible accessible) {
            if (n < 0 || n >= this.getAccessibleRowCount()) {
                throw new IllegalArgumentException(Integer.toString(n));
            }
            if (this.rowDescription == null) {
                this.rowDescription = new Accessible[this.getAccessibleRowCount()];
            }
            this.rowDescription[n] = accessible;
        }
        
        @Override
        public Accessible getAccessibleColumnDescription(final int n) {
            if (n < 0 || n >= this.getAccessibleColumnCount()) {
                throw new IllegalArgumentException(Integer.toString(n));
            }
            if (this.columnDescription == null) {
                return null;
            }
            return this.columnDescription[n];
        }
        
        @Override
        public void setAccessibleColumnDescription(final int n, final Accessible accessible) {
            if (n < 0 || n >= this.getAccessibleColumnCount()) {
                throw new IllegalArgumentException(Integer.toString(n));
            }
            if (this.columnDescription == null) {
                this.columnDescription = new Accessible[this.getAccessibleColumnCount()];
            }
            this.columnDescription[n] = accessible;
        }
        
        @Override
        public boolean isAccessibleSelected(final int n, final int n2) {
            return JTable.this.isCellSelected(n, n2);
        }
        
        @Override
        public boolean isAccessibleRowSelected(final int n) {
            return JTable.this.isRowSelected(n);
        }
        
        @Override
        public boolean isAccessibleColumnSelected(final int n) {
            return JTable.this.isColumnSelected(n);
        }
        
        @Override
        public int[] getSelectedAccessibleRows() {
            return JTable.this.getSelectedRows();
        }
        
        @Override
        public int[] getSelectedAccessibleColumns() {
            return JTable.this.getSelectedColumns();
        }
        
        public int getAccessibleRowAtIndex(final int n) {
            final int accessibleColumnCount = this.getAccessibleColumnCount();
            if (accessibleColumnCount == 0) {
                return -1;
            }
            return n / accessibleColumnCount;
        }
        
        public int getAccessibleColumnAtIndex(final int n) {
            final int accessibleColumnCount = this.getAccessibleColumnCount();
            if (accessibleColumnCount == 0) {
                return -1;
            }
            return n % accessibleColumnCount;
        }
        
        public int getAccessibleIndexAt(final int n, final int n2) {
            return n * this.getAccessibleColumnCount() + n2;
        }
        
        protected class AccessibleJTableModelChange implements AccessibleTableModelChange
        {
            protected int type;
            protected int firstRow;
            protected int lastRow;
            protected int firstColumn;
            protected int lastColumn;
            
            protected AccessibleJTableModelChange(final int type, final int firstRow, final int lastRow, final int firstColumn, final int lastColumn) {
                this.type = type;
                this.firstRow = firstRow;
                this.lastRow = lastRow;
                this.firstColumn = firstColumn;
                this.lastColumn = lastColumn;
            }
            
            @Override
            public int getType() {
                return this.type;
            }
            
            @Override
            public int getFirstRow() {
                return this.firstRow;
            }
            
            @Override
            public int getLastRow() {
                return this.lastRow;
            }
            
            @Override
            public int getFirstColumn() {
                return this.firstColumn;
            }
            
            @Override
            public int getLastColumn() {
                return this.lastColumn;
            }
        }
        
        private class AccessibleTableHeader implements AccessibleTable
        {
            private JTableHeader header;
            private TableColumnModel headerModel;
            
            AccessibleTableHeader(final JTableHeader header) {
                this.header = header;
                this.headerModel = header.getColumnModel();
            }
            
            @Override
            public Accessible getAccessibleCaption() {
                return null;
            }
            
            @Override
            public void setAccessibleCaption(final Accessible accessible) {
            }
            
            @Override
            public Accessible getAccessibleSummary() {
                return null;
            }
            
            @Override
            public void setAccessibleSummary(final Accessible accessible) {
            }
            
            @Override
            public int getAccessibleRowCount() {
                return 1;
            }
            
            @Override
            public int getAccessibleColumnCount() {
                return this.headerModel.getColumnCount();
            }
            
            @Override
            public Accessible getAccessibleAt(final int n, final int n2) {
                final TableColumn column = this.headerModel.getColumn(n2);
                TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
                if (tableCellRenderer == null) {
                    tableCellRenderer = this.header.getDefaultRenderer();
                }
                return new AccessibleJTableHeaderCell(n, n2, JTable.this.getTableHeader(), tableCellRenderer.getTableCellRendererComponent(this.header.getTable(), column.getHeaderValue(), false, false, -1, n2));
            }
            
            @Override
            public int getAccessibleRowExtentAt(final int n, final int n2) {
                return 1;
            }
            
            @Override
            public int getAccessibleColumnExtentAt(final int n, final int n2) {
                return 1;
            }
            
            @Override
            public AccessibleTable getAccessibleRowHeader() {
                return null;
            }
            
            @Override
            public void setAccessibleRowHeader(final AccessibleTable accessibleTable) {
            }
            
            @Override
            public AccessibleTable getAccessibleColumnHeader() {
                return null;
            }
            
            @Override
            public void setAccessibleColumnHeader(final AccessibleTable accessibleTable) {
            }
            
            @Override
            public Accessible getAccessibleRowDescription(final int n) {
                return null;
            }
            
            @Override
            public void setAccessibleRowDescription(final int n, final Accessible accessible) {
            }
            
            @Override
            public Accessible getAccessibleColumnDescription(final int n) {
                return null;
            }
            
            @Override
            public void setAccessibleColumnDescription(final int n, final Accessible accessible) {
            }
            
            @Override
            public boolean isAccessibleSelected(final int n, final int n2) {
                return false;
            }
            
            @Override
            public boolean isAccessibleRowSelected(final int n) {
                return false;
            }
            
            @Override
            public boolean isAccessibleColumnSelected(final int n) {
                return false;
            }
            
            @Override
            public int[] getSelectedAccessibleRows() {
                return new int[0];
            }
            
            @Override
            public int[] getSelectedAccessibleColumns() {
                return new int[0];
            }
        }
        
        protected class AccessibleJTableCell extends AccessibleContext implements Accessible, AccessibleComponent
        {
            private JTable parent;
            private int row;
            private int column;
            private int index;
            
            public AccessibleJTableCell(final JTable parent, final int row, final int column, final int index) {
                this.parent = parent;
                this.row = row;
                this.column = column;
                this.index = index;
                this.setAccessibleParent(this.parent);
            }
            
            @Override
            public AccessibleContext getAccessibleContext() {
                return this;
            }
            
            protected AccessibleContext getCurrentAccessibleContext() {
                TableCellRenderer tableCellRenderer = JTable.this.getColumnModel().getColumn(this.column).getCellRenderer();
                if (tableCellRenderer == null) {
                    tableCellRenderer = JTable.this.getDefaultRenderer(JTable.this.getColumnClass(this.column));
                }
                final Component tableCellRendererComponent = tableCellRenderer.getTableCellRendererComponent(JTable.this, JTable.this.getValueAt(this.row, this.column), false, false, this.row, this.column);
                if (tableCellRendererComponent instanceof Accessible) {
                    return tableCellRendererComponent.getAccessibleContext();
                }
                return null;
            }
            
            protected Component getCurrentComponent() {
                TableCellRenderer tableCellRenderer = JTable.this.getColumnModel().getColumn(this.column).getCellRenderer();
                if (tableCellRenderer == null) {
                    tableCellRenderer = JTable.this.getDefaultRenderer(JTable.this.getColumnClass(this.column));
                }
                return tableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, this.row, this.column);
            }
            
            @Override
            public String getAccessibleName() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final String accessibleName = currentAccessibleContext.getAccessibleName();
                    if (accessibleName != null && accessibleName != "") {
                        return accessibleName;
                    }
                }
                if (this.accessibleName != null && this.accessibleName != "") {
                    return this.accessibleName;
                }
                return (String)JTable.this.getClientProperty("AccessibleName");
            }
            
            @Override
            public void setAccessibleName(final String s) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleName(s);
                }
                else {
                    super.setAccessibleName(s);
                }
            }
            
            @Override
            public String getAccessibleDescription() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleDescription();
                }
                return super.getAccessibleDescription();
            }
            
            @Override
            public void setAccessibleDescription(final String s) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleDescription(s);
                }
                else {
                    super.setAccessibleDescription(s);
                }
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleRole();
                }
                return AccessibleRole.UNKNOWN;
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                AccessibleStateSet accessibleStateSet = null;
                if (currentAccessibleContext != null) {
                    accessibleStateSet = currentAccessibleContext.getAccessibleStateSet();
                }
                if (accessibleStateSet == null) {
                    accessibleStateSet = new AccessibleStateSet();
                }
                if (JTable.this.getVisibleRect().intersects(JTable.this.getCellRect(this.row, this.column, false))) {
                    accessibleStateSet.add(AccessibleState.SHOWING);
                }
                else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
                    accessibleStateSet.remove(AccessibleState.SHOWING);
                }
                if (this.parent.isCellSelected(this.row, this.column)) {
                    accessibleStateSet.add(AccessibleState.SELECTED);
                }
                else if (accessibleStateSet.contains(AccessibleState.SELECTED)) {
                    accessibleStateSet.remove(AccessibleState.SELECTED);
                }
                if (this.row == JTable.this.getSelectedRow() && this.column == JTable.this.getSelectedColumn()) {
                    accessibleStateSet.add(AccessibleState.ACTIVE);
                }
                accessibleStateSet.add(AccessibleState.TRANSIENT);
                return accessibleStateSet;
            }
            
            @Override
            public Accessible getAccessibleParent() {
                return this.parent;
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                return this.index;
            }
            
            @Override
            public int getAccessibleChildrenCount() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleChildrenCount();
                }
                return 0;
            }
            
            @Override
            public Accessible getAccessibleChild(final int n) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final Accessible accessibleChild = currentAccessibleContext.getAccessibleChild(n);
                    currentAccessibleContext.setAccessibleParent(this);
                    return accessibleChild;
                }
                return null;
            }
            
            @Override
            public Locale getLocale() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getLocale();
                }
                return null;
            }
            
            @Override
            public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.addPropertyChangeListener(propertyChangeListener);
                }
                else {
                    super.addPropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.removePropertyChangeListener(propertyChangeListener);
                }
                else {
                    super.removePropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public AccessibleAction getAccessibleAction() {
                return this.getCurrentAccessibleContext().getAccessibleAction();
            }
            
            @Override
            public AccessibleComponent getAccessibleComponent() {
                return this;
            }
            
            @Override
            public AccessibleSelection getAccessibleSelection() {
                return this.getCurrentAccessibleContext().getAccessibleSelection();
            }
            
            @Override
            public AccessibleText getAccessibleText() {
                return this.getCurrentAccessibleContext().getAccessibleText();
            }
            
            @Override
            public AccessibleValue getAccessibleValue() {
                return this.getCurrentAccessibleContext().getAccessibleValue();
            }
            
            @Override
            public Color getBackground() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBackground();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBackground();
                }
                return null;
            }
            
            @Override
            public void setBackground(final Color color) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBackground(color);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setBackground(color);
                    }
                }
            }
            
            @Override
            public Color getForeground() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getForeground();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getForeground();
                }
                return null;
            }
            
            @Override
            public void setForeground(final Color color) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setForeground(color);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setForeground(color);
                    }
                }
            }
            
            @Override
            public Cursor getCursor() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getCursor();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getCursor();
                }
                final Accessible accessibleParent = this.getAccessibleParent();
                if (accessibleParent instanceof AccessibleComponent) {
                    return ((AccessibleComponent)accessibleParent).getCursor();
                }
                return null;
            }
            
            @Override
            public void setCursor(final Cursor cursor) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setCursor(cursor);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setCursor(cursor);
                    }
                }
            }
            
            @Override
            public Font getFont() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getFont();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getFont();
                }
                return null;
            }
            
            @Override
            public void setFont(final Font font) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setFont(font);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setFont(font);
                    }
                }
            }
            
            @Override
            public FontMetrics getFontMetrics(final Font font) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getFontMetrics(font);
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getFontMetrics(font);
                }
                return null;
            }
            
            @Override
            public boolean isEnabled() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isEnabled();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isEnabled();
            }
            
            @Override
            public void setEnabled(final boolean b) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setEnabled(b);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setEnabled(b);
                    }
                }
            }
            
            @Override
            public boolean isVisible() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isVisible();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isVisible();
            }
            
            @Override
            public void setVisible(final boolean b) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setVisible(b);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setVisible(b);
                    }
                }
            }
            
            @Override
            public boolean isShowing() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (!(currentAccessibleContext instanceof AccessibleComponent)) {
                    final Component currentComponent = this.getCurrentComponent();
                    return currentComponent != null && currentComponent.isShowing();
                }
                if (currentAccessibleContext.getAccessibleParent() != null) {
                    return ((AccessibleComponent)currentAccessibleContext).isShowing();
                }
                return this.isVisible();
            }
            
            @Override
            public boolean contains(final Point point) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBounds().contains(point);
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBounds().contains(point);
                }
                return this.getBounds().contains(point);
            }
            
            @Override
            public Point getLocationOnScreen() {
                if (this.parent != null && this.parent.isShowing()) {
                    final Point locationOnScreen = this.parent.getLocationOnScreen();
                    final Point location = this.getLocation();
                    location.translate(locationOnScreen.x, locationOnScreen.y);
                    return location;
                }
                return null;
            }
            
            @Override
            public Point getLocation() {
                if (this.parent != null) {
                    final Rectangle cellRect = this.parent.getCellRect(this.row, this.column, false);
                    if (cellRect != null) {
                        return cellRect.getLocation();
                    }
                }
                return null;
            }
            
            @Override
            public void setLocation(final Point point) {
            }
            
            @Override
            public Rectangle getBounds() {
                if (this.parent != null) {
                    return this.parent.getCellRect(this.row, this.column, false);
                }
                return null;
            }
            
            @Override
            public void setBounds(final Rectangle rectangle) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBounds(rectangle);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setBounds(rectangle);
                    }
                }
            }
            
            @Override
            public Dimension getSize() {
                if (this.parent != null) {
                    final Rectangle cellRect = this.parent.getCellRect(this.row, this.column, false);
                    if (cellRect != null) {
                        return cellRect.getSize();
                    }
                }
                return null;
            }
            
            @Override
            public void setSize(final Dimension dimension) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setSize(dimension);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setSize(dimension);
                    }
                }
            }
            
            @Override
            public Accessible getAccessibleAt(final Point point) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getAccessibleAt(point);
                }
                return null;
            }
            
            @Override
            public boolean isFocusTraversable() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isFocusTraversable();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isFocusTraversable();
            }
            
            @Override
            public void requestFocus() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).requestFocus();
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.requestFocus();
                    }
                }
            }
            
            @Override
            public void addFocusListener(final FocusListener focusListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).addFocusListener(focusListener);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.addFocusListener(focusListener);
                    }
                }
            }
            
            @Override
            public void removeFocusListener(final FocusListener focusListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).removeFocusListener(focusListener);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.removeFocusListener(focusListener);
                    }
                }
            }
        }
        
        private class AccessibleJTableHeaderCell extends AccessibleContext implements Accessible, AccessibleComponent
        {
            private int row;
            private int column;
            private JTableHeader parent;
            private Component rendererComponent;
            
            public AccessibleJTableHeaderCell(final int row, final int column, final JTableHeader tableHeader, final Component rendererComponent) {
                this.row = row;
                this.column = column;
                this.parent = tableHeader;
                this.rendererComponent = rendererComponent;
                this.setAccessibleParent(tableHeader);
            }
            
            @Override
            public AccessibleContext getAccessibleContext() {
                return this;
            }
            
            private AccessibleContext getCurrentAccessibleContext() {
                return this.rendererComponent.getAccessibleContext();
            }
            
            private Component getCurrentComponent() {
                return this.rendererComponent;
            }
            
            @Override
            public String getAccessibleName() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final String accessibleName = currentAccessibleContext.getAccessibleName();
                    if (accessibleName != null && accessibleName != "") {
                        return currentAccessibleContext.getAccessibleName();
                    }
                }
                if (this.accessibleName != null && this.accessibleName != "") {
                    return this.accessibleName;
                }
                return null;
            }
            
            @Override
            public void setAccessibleName(final String s) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleName(s);
                }
                else {
                    super.setAccessibleName(s);
                }
            }
            
            @Override
            public String getAccessibleDescription() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleDescription();
                }
                return super.getAccessibleDescription();
            }
            
            @Override
            public void setAccessibleDescription(final String s) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleDescription(s);
                }
                else {
                    super.setAccessibleDescription(s);
                }
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleRole();
                }
                return AccessibleRole.UNKNOWN;
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                AccessibleStateSet accessibleStateSet = null;
                if (currentAccessibleContext != null) {
                    accessibleStateSet = currentAccessibleContext.getAccessibleStateSet();
                }
                if (accessibleStateSet == null) {
                    accessibleStateSet = new AccessibleStateSet();
                }
                if (JTable.this.getVisibleRect().intersects(JTable.this.getCellRect(this.row, this.column, false))) {
                    accessibleStateSet.add(AccessibleState.SHOWING);
                }
                else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
                    accessibleStateSet.remove(AccessibleState.SHOWING);
                }
                if (JTable.this.isCellSelected(this.row, this.column)) {
                    accessibleStateSet.add(AccessibleState.SELECTED);
                }
                else if (accessibleStateSet.contains(AccessibleState.SELECTED)) {
                    accessibleStateSet.remove(AccessibleState.SELECTED);
                }
                if (this.row == JTable.this.getSelectedRow() && this.column == JTable.this.getSelectedColumn()) {
                    accessibleStateSet.add(AccessibleState.ACTIVE);
                }
                accessibleStateSet.add(AccessibleState.TRANSIENT);
                return accessibleStateSet;
            }
            
            @Override
            public Accessible getAccessibleParent() {
                return this.parent;
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                return this.column;
            }
            
            @Override
            public int getAccessibleChildrenCount() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleChildrenCount();
                }
                return 0;
            }
            
            @Override
            public Accessible getAccessibleChild(final int n) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final Accessible accessibleChild = currentAccessibleContext.getAccessibleChild(n);
                    currentAccessibleContext.setAccessibleParent(this);
                    return accessibleChild;
                }
                return null;
            }
            
            @Override
            public Locale getLocale() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getLocale();
                }
                return null;
            }
            
            @Override
            public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.addPropertyChangeListener(propertyChangeListener);
                }
                else {
                    super.addPropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.removePropertyChangeListener(propertyChangeListener);
                }
                else {
                    super.removePropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public AccessibleAction getAccessibleAction() {
                return this.getCurrentAccessibleContext().getAccessibleAction();
            }
            
            @Override
            public AccessibleComponent getAccessibleComponent() {
                return this;
            }
            
            @Override
            public AccessibleSelection getAccessibleSelection() {
                return this.getCurrentAccessibleContext().getAccessibleSelection();
            }
            
            @Override
            public AccessibleText getAccessibleText() {
                return this.getCurrentAccessibleContext().getAccessibleText();
            }
            
            @Override
            public AccessibleValue getAccessibleValue() {
                return this.getCurrentAccessibleContext().getAccessibleValue();
            }
            
            @Override
            public Color getBackground() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBackground();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBackground();
                }
                return null;
            }
            
            @Override
            public void setBackground(final Color color) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBackground(color);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setBackground(color);
                    }
                }
            }
            
            @Override
            public Color getForeground() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getForeground();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getForeground();
                }
                return null;
            }
            
            @Override
            public void setForeground(final Color color) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setForeground(color);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setForeground(color);
                    }
                }
            }
            
            @Override
            public Cursor getCursor() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getCursor();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getCursor();
                }
                final Accessible accessibleParent = this.getAccessibleParent();
                if (accessibleParent instanceof AccessibleComponent) {
                    return ((AccessibleComponent)accessibleParent).getCursor();
                }
                return null;
            }
            
            @Override
            public void setCursor(final Cursor cursor) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setCursor(cursor);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setCursor(cursor);
                    }
                }
            }
            
            @Override
            public Font getFont() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getFont();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getFont();
                }
                return null;
            }
            
            @Override
            public void setFont(final Font font) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setFont(font);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setFont(font);
                    }
                }
            }
            
            @Override
            public FontMetrics getFontMetrics(final Font font) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getFontMetrics(font);
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getFontMetrics(font);
                }
                return null;
            }
            
            @Override
            public boolean isEnabled() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isEnabled();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isEnabled();
            }
            
            @Override
            public void setEnabled(final boolean b) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setEnabled(b);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setEnabled(b);
                    }
                }
            }
            
            @Override
            public boolean isVisible() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isVisible();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isVisible();
            }
            
            @Override
            public void setVisible(final boolean b) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setVisible(b);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setVisible(b);
                    }
                }
            }
            
            @Override
            public boolean isShowing() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (!(currentAccessibleContext instanceof AccessibleComponent)) {
                    final Component currentComponent = this.getCurrentComponent();
                    return currentComponent != null && currentComponent.isShowing();
                }
                if (currentAccessibleContext.getAccessibleParent() != null) {
                    return ((AccessibleComponent)currentAccessibleContext).isShowing();
                }
                return this.isVisible();
            }
            
            @Override
            public boolean contains(final Point point) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBounds().contains(point);
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBounds().contains(point);
                }
                return this.getBounds().contains(point);
            }
            
            @Override
            public Point getLocationOnScreen() {
                if (this.parent != null && this.parent.isShowing()) {
                    final Point locationOnScreen = this.parent.getLocationOnScreen();
                    final Point location = this.getLocation();
                    location.translate(locationOnScreen.x, locationOnScreen.y);
                    return location;
                }
                return null;
            }
            
            @Override
            public Point getLocation() {
                if (this.parent != null) {
                    final Rectangle headerRect = this.parent.getHeaderRect(this.column);
                    if (headerRect != null) {
                        return headerRect.getLocation();
                    }
                }
                return null;
            }
            
            @Override
            public void setLocation(final Point point) {
            }
            
            @Override
            public Rectangle getBounds() {
                if (this.parent != null) {
                    return this.parent.getHeaderRect(this.column);
                }
                return null;
            }
            
            @Override
            public void setBounds(final Rectangle rectangle) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBounds(rectangle);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setBounds(rectangle);
                    }
                }
            }
            
            @Override
            public Dimension getSize() {
                if (this.parent != null) {
                    final Rectangle headerRect = this.parent.getHeaderRect(this.column);
                    if (headerRect != null) {
                        return headerRect.getSize();
                    }
                }
                return null;
            }
            
            @Override
            public void setSize(final Dimension dimension) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setSize(dimension);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.setSize(dimension);
                    }
                }
            }
            
            @Override
            public Accessible getAccessibleAt(final Point point) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getAccessibleAt(point);
                }
                return null;
            }
            
            @Override
            public boolean isFocusTraversable() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).isFocusTraversable();
                }
                final Component currentComponent = this.getCurrentComponent();
                return currentComponent != null && currentComponent.isFocusTraversable();
            }
            
            @Override
            public void requestFocus() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).requestFocus();
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.requestFocus();
                    }
                }
            }
            
            @Override
            public void addFocusListener(final FocusListener focusListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).addFocusListener(focusListener);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.addFocusListener(focusListener);
                    }
                }
            }
            
            @Override
            public void removeFocusListener(final FocusListener focusListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).removeFocusListener(focusListener);
                }
                else {
                    final Component currentComponent = this.getCurrentComponent();
                    if (currentComponent != null) {
                        currentComponent.removeFocusListener(focusListener);
                    }
                }
            }
        }
    }
    
    private interface Resizable3 extends Resizable2
    {
        int getMidPointAt(final int p0);
    }
    
    private interface Resizable2
    {
        int getElementCount();
        
        int getLowerBoundAt(final int p0);
        
        int getUpperBoundAt(final int p0);
        
        void setSizeAt(final int p0, final int p1);
    }
}
