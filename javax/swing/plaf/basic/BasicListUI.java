package javax.swing.plaf.basic;

import java.awt.datatransfer.Transferable;
import javax.swing.KeyStroke;
import javax.swing.text.Position;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import sun.swing.SwingUtilities2;
import javax.swing.DefaultListSelectionModel;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.Font;
import javax.swing.LookAndFeel;
import java.awt.LayoutManager;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.dnd.DropTarget;
import javax.swing.plaf.UIResource;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import javax.swing.UIDefaults;
import javax.swing.DefaultListCellRenderer;
import javax.swing.UIManager;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.Color;
import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;
import java.awt.Shape;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Container;
import javax.swing.ListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListCellRenderer;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.TransferHandler;
import java.beans.PropertyChangeListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import java.awt.event.FocusListener;
import javax.swing.CellRendererPane;
import javax.swing.JList;
import javax.swing.plaf.ListUI;

public class BasicListUI extends ListUI
{
    private static final StringBuilder BASELINE_COMPONENT_KEY;
    protected JList list;
    protected CellRendererPane rendererPane;
    protected FocusListener focusListener;
    protected MouseInputListener mouseInputListener;
    protected ListSelectionListener listSelectionListener;
    protected ListDataListener listDataListener;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;
    protected int[] cellHeights;
    protected int cellHeight;
    protected int cellWidth;
    protected int updateLayoutStateNeeded;
    private int listHeight;
    private int listWidth;
    private int layoutOrientation;
    private int columnCount;
    private int preferredHeight;
    private int rowsPerColumn;
    private long timeFactor;
    private boolean isFileList;
    private boolean isLeftToRight;
    protected static final int modelChanged = 1;
    protected static final int selectionModelChanged = 2;
    protected static final int fontChanged = 4;
    protected static final int fixedCellWidthChanged = 8;
    protected static final int fixedCellHeightChanged = 16;
    protected static final int prototypeCellValueChanged = 32;
    protected static final int cellRendererChanged = 64;
    private static final int layoutOrientationChanged = 128;
    private static final int heightChanged = 256;
    private static final int widthChanged = 512;
    private static final int componentOrientationChanged = 1024;
    private static final int DROP_LINE_THICKNESS = 2;
    private static final int CHANGE_LEAD = 0;
    private static final int CHANGE_SELECTION = 1;
    private static final int EXTEND_SELECTION = 2;
    private static final TransferHandler defaultTransferHandler;
    
    public BasicListUI() {
        this.list = null;
        this.cellHeights = null;
        this.cellHeight = -1;
        this.cellWidth = -1;
        this.updateLayoutStateNeeded = 1;
        this.timeFactor = 1000L;
        this.isFileList = false;
        this.isLeftToRight = true;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("selectPreviousColumn"));
        lazyActionMap.put(new Actions("selectPreviousColumnExtendSelection"));
        lazyActionMap.put(new Actions("selectPreviousColumnChangeLead"));
        lazyActionMap.put(new Actions("selectNextColumn"));
        lazyActionMap.put(new Actions("selectNextColumnExtendSelection"));
        lazyActionMap.put(new Actions("selectNextColumnChangeLead"));
        lazyActionMap.put(new Actions("selectPreviousRow"));
        lazyActionMap.put(new Actions("selectPreviousRowExtendSelection"));
        lazyActionMap.put(new Actions("selectPreviousRowChangeLead"));
        lazyActionMap.put(new Actions("selectNextRow"));
        lazyActionMap.put(new Actions("selectNextRowExtendSelection"));
        lazyActionMap.put(new Actions("selectNextRowChangeLead"));
        lazyActionMap.put(new Actions("selectFirstRow"));
        lazyActionMap.put(new Actions("selectFirstRowExtendSelection"));
        lazyActionMap.put(new Actions("selectFirstRowChangeLead"));
        lazyActionMap.put(new Actions("selectLastRow"));
        lazyActionMap.put(new Actions("selectLastRowExtendSelection"));
        lazyActionMap.put(new Actions("selectLastRowChangeLead"));
        lazyActionMap.put(new Actions("scrollUp"));
        lazyActionMap.put(new Actions("scrollUpExtendSelection"));
        lazyActionMap.put(new Actions("scrollUpChangeLead"));
        lazyActionMap.put(new Actions("scrollDown"));
        lazyActionMap.put(new Actions("scrollDownExtendSelection"));
        lazyActionMap.put(new Actions("scrollDownChangeLead"));
        lazyActionMap.put(new Actions("selectAll"));
        lazyActionMap.put(new Actions("clearSelection"));
        lazyActionMap.put(new Actions("addToSelection"));
        lazyActionMap.put(new Actions("toggleAndAnchor"));
        lazyActionMap.put(new Actions("extendTo"));
        lazyActionMap.put(new Actions("moveSelectionTo"));
        lazyActionMap.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
        lazyActionMap.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
        lazyActionMap.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    }
    
    protected void paintCell(final Graphics graphics, final int n, final Rectangle rectangle, final ListCellRenderer listCellRenderer, final ListModel listModel, final ListSelectionModel listSelectionModel, final int n2) {
        final Component listCellRendererComponent = listCellRenderer.getListCellRendererComponent(this.list, listModel.getElementAt(n), n, listSelectionModel.isSelectedIndex(n), this.list.hasFocus() && n == n2);
        int x = rectangle.x;
        final int y = rectangle.y;
        int width = rectangle.width;
        final int height = rectangle.height;
        if (this.isFileList) {
            final int min = Math.min(width, listCellRendererComponent.getPreferredSize().width + 4);
            if (!this.isLeftToRight) {
                x += width - min;
            }
            width = min;
        }
        this.rendererPane.paintComponent(graphics, listCellRendererComponent, this.list, x, y, width, height, true);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Shape clip = graphics.getClip();
        this.paintImpl(graphics, component);
        graphics.setClip(clip);
        this.paintDropLine(graphics);
    }
    
    private void paintImpl(final Graphics graphics, final JComponent component) {
        switch (this.layoutOrientation) {
            case 1: {
                if (this.list.getHeight() != this.listHeight) {
                    this.updateLayoutStateNeeded |= 0x100;
                    this.redrawList();
                    break;
                }
                break;
            }
            case 2: {
                if (this.list.getWidth() != this.listWidth) {
                    this.updateLayoutStateNeeded |= 0x200;
                    this.redrawList();
                    break;
                }
                break;
            }
        }
        this.maybeUpdateLayoutState();
        final ListCellRenderer cellRenderer = this.list.getCellRenderer();
        final ListModel model = this.list.getModel();
        final ListSelectionModel selectionModel = this.list.getSelectionModel();
        final int size;
        if (cellRenderer == null || (size = model.getSize()) == 0) {
            return;
        }
        final Rectangle clipBounds = graphics.getClipBounds();
        int n;
        int n2;
        if (component.getComponentOrientation().isLeftToRight()) {
            n = this.convertLocationToColumn(clipBounds.x, clipBounds.y);
            n2 = this.convertLocationToColumn(clipBounds.x + clipBounds.width, clipBounds.y);
        }
        else {
            n = this.convertLocationToColumn(clipBounds.x + clipBounds.width, clipBounds.y);
            n2 = this.convertLocationToColumn(clipBounds.x, clipBounds.y);
        }
        final int n3 = clipBounds.y + clipBounds.height;
        final int adjustIndex = adjustIndex(this.list.getLeadSelectionIndex(), this.list);
        final int n4 = (this.layoutOrientation == 2) ? this.columnCount : 1;
        for (int i = n; i <= n2; ++i) {
            int convertLocationToRowInColumn = this.convertLocationToRowInColumn(clipBounds.y, i);
            final int rowCount = this.getRowCount(i);
            int modelIndex = this.getModelIndex(i, convertLocationToRowInColumn);
            final Rectangle cellBounds = this.getCellBounds(this.list, modelIndex, modelIndex);
            if (cellBounds == null) {
                return;
            }
            while (convertLocationToRowInColumn < rowCount && cellBounds.y < n3 && modelIndex < size) {
                cellBounds.height = this.getHeight(i, convertLocationToRowInColumn);
                graphics.setClip(cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height);
                graphics.clipRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
                this.paintCell(graphics, modelIndex, cellBounds, cellRenderer, model, selectionModel, adjustIndex);
                final Rectangle rectangle = cellBounds;
                rectangle.y += cellBounds.height;
                modelIndex += n4;
                ++convertLocationToRowInColumn;
            }
        }
        this.rendererPane.removeAll();
    }
    
    private void paintDropLine(final Graphics graphics) {
        final JList.DropLocation dropLocation = this.list.getDropLocation();
        if (dropLocation == null || !dropLocation.isInsert()) {
            return;
        }
        final Color color = DefaultLookup.getColor(this.list, this, "List.dropLineColor", null);
        if (color != null) {
            graphics.setColor(color);
            final Rectangle dropLineRect = this.getDropLineRect(dropLocation);
            graphics.fillRect(dropLineRect.x, dropLineRect.y, dropLineRect.width, dropLineRect.height);
        }
    }
    
    private Rectangle getDropLineRect(final JList.DropLocation dropLocation) {
        final int size = this.list.getModel().getSize();
        if (size != 0) {
            int index = dropLocation.getIndex();
            int n = 0;
            Rectangle rectangle;
            if (this.layoutOrientation == 2) {
                if (index == size) {
                    n = 1;
                }
                else if (index != 0 && this.convertModelToRow(index) != this.convertModelToRow(index - 1)) {
                    final Rectangle cellBounds = this.getCellBounds(this.list, index - 1);
                    final Rectangle cellBounds2 = this.getCellBounds(this.list, index);
                    final Point dropPoint = dropLocation.getDropPoint();
                    if (this.isLeftToRight) {
                        n = ((Point2D.distance(cellBounds.x + cellBounds.width, cellBounds.y + (int)(cellBounds.height / 2.0), dropPoint.x, dropPoint.y) < Point2D.distance(cellBounds2.x, cellBounds2.y + (int)(cellBounds2.height / 2.0), dropPoint.x, dropPoint.y)) ? 1 : 0);
                    }
                    else {
                        n = ((Point2D.distance(cellBounds.x, cellBounds.y + (int)(cellBounds.height / 2.0), dropPoint.x, dropPoint.y) < Point2D.distance(cellBounds2.x + cellBounds2.width, cellBounds2.y + (int)(cellBounds.height / 2.0), dropPoint.x, dropPoint.y)) ? 1 : 0);
                    }
                }
                if (n != 0) {
                    --index;
                    rectangle = this.getCellBounds(this.list, index);
                    if (this.isLeftToRight) {
                        final Rectangle rectangle2 = rectangle;
                        rectangle2.x += rectangle.width;
                    }
                    else {
                        final Rectangle rectangle3 = rectangle;
                        rectangle3.x -= 2;
                    }
                }
                else {
                    rectangle = this.getCellBounds(this.list, index);
                    if (!this.isLeftToRight) {
                        final Rectangle rectangle4 = rectangle;
                        rectangle4.x += rectangle.width - 2;
                    }
                }
                if (rectangle.x >= this.list.getWidth()) {
                    rectangle.x = this.list.getWidth() - 2;
                }
                else if (rectangle.x < 0) {
                    rectangle.x = 0;
                }
                rectangle.width = 2;
            }
            else if (this.layoutOrientation == 1) {
                if (index == size) {
                    --index;
                    final Rectangle cellBounds3;
                    rectangle = (cellBounds3 = this.getCellBounds(this.list, index));
                    cellBounds3.y += rectangle.height;
                }
                else if (index != 0 && this.convertModelToColumn(index) != this.convertModelToColumn(index - 1)) {
                    final Rectangle cellBounds4 = this.getCellBounds(this.list, index - 1);
                    final Rectangle cellBounds5 = this.getCellBounds(this.list, index);
                    final Point dropPoint2 = dropLocation.getDropPoint();
                    if (Point2D.distance(cellBounds4.x + (int)(cellBounds4.width / 2.0), cellBounds4.y + cellBounds4.height, dropPoint2.x, dropPoint2.y) < Point2D.distance(cellBounds5.x + (int)(cellBounds5.width / 2.0), cellBounds5.y, dropPoint2.x, dropPoint2.y)) {
                        --index;
                        final Rectangle cellBounds6;
                        rectangle = (cellBounds6 = this.getCellBounds(this.list, index));
                        cellBounds6.y += rectangle.height;
                    }
                    else {
                        rectangle = this.getCellBounds(this.list, index);
                    }
                }
                else {
                    rectangle = this.getCellBounds(this.list, index);
                }
                if (rectangle.y >= this.list.getHeight()) {
                    rectangle.y = this.list.getHeight() - 2;
                }
                rectangle.height = 2;
            }
            else {
                if (index == size) {
                    --index;
                    final Rectangle cellBounds7;
                    rectangle = (cellBounds7 = this.getCellBounds(this.list, index));
                    cellBounds7.y += rectangle.height;
                }
                else {
                    rectangle = this.getCellBounds(this.list, index);
                }
                if (rectangle.y >= this.list.getHeight()) {
                    rectangle.y = this.list.getHeight() - 2;
                }
                rectangle.height = 2;
            }
            return rectangle;
        }
        final Insets insets = this.list.getInsets();
        if (this.layoutOrientation != 2) {
            return new Rectangle(insets.left, insets.top, this.list.getWidth() - insets.left - insets.right, 2);
        }
        if (this.isLeftToRight) {
            return new Rectangle(insets.left, insets.top, 2, 20);
        }
        return new Rectangle(this.list.getWidth() - 2 - insets.right, insets.top, 2, 20);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        int n3 = this.list.getFixedCellHeight();
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        Component listCellRendererComponent = (Component)lookAndFeelDefaults.get(BasicListUI.BASELINE_COMPONENT_KEY);
        if (listCellRendererComponent == null) {
            ListCellRenderer listCellRenderer = (ListCellRenderer)UIManager.get("List.cellRenderer");
            if (listCellRenderer == null) {
                listCellRenderer = new DefaultListCellRenderer();
            }
            listCellRendererComponent = listCellRenderer.getListCellRendererComponent(this.list, "a", -1, false, false);
            lookAndFeelDefaults.put(BasicListUI.BASELINE_COMPONENT_KEY, listCellRendererComponent);
        }
        listCellRendererComponent.setFont(this.list.getFont());
        if (n3 == -1) {
            n3 = listCellRendererComponent.getPreferredSize().height;
        }
        return listCellRendererComponent.getBaseline(Integer.MAX_VALUE, n3) + this.list.getInsets().top;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        this.maybeUpdateLayoutState();
        final int n = this.list.getModel().getSize() - 1;
        if (n < 0) {
            return new Dimension(0, 0);
        }
        final Insets insets = this.list.getInsets();
        final int n2 = this.cellWidth * this.columnCount + insets.left + insets.right;
        int preferredHeight;
        if (this.layoutOrientation != 0) {
            preferredHeight = this.preferredHeight;
        }
        else {
            final Rectangle cellBounds = this.getCellBounds(this.list, n);
            if (cellBounds != null) {
                preferredHeight = cellBounds.y + cellBounds.height + insets.bottom;
            }
            else {
                preferredHeight = 0;
            }
        }
        return new Dimension(n2, preferredHeight);
    }
    
    protected void selectPreviousIndex() {
        int selectedIndex = this.list.getSelectedIndex();
        if (selectedIndex > 0) {
            --selectedIndex;
            this.list.setSelectedIndex(selectedIndex);
            this.list.ensureIndexIsVisible(selectedIndex);
        }
    }
    
    protected void selectNextIndex() {
        int selectedIndex = this.list.getSelectedIndex();
        if (selectedIndex + 1 < this.list.getModel().getSize()) {
            ++selectedIndex;
            this.list.setSelectedIndex(selectedIndex);
            this.list.ensureIndexIsVisible(selectedIndex);
        }
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.list, 0, this.getInputMap(0));
        LazyActionMap.installLazyActionMap(this.list, BasicListUI.class, "List.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n != 0) {
            return null;
        }
        final InputMap parent = (InputMap)DefaultLookup.get(this.list, this, "List.focusInputMap");
        final InputMap inputMap;
        if (this.isLeftToRight || (inputMap = (InputMap)DefaultLookup.get(this.list, this, "List.focusInputMap.RightToLeft")) == null) {
            return parent;
        }
        inputMap.setParent(parent);
        return inputMap;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.list, null);
        SwingUtilities.replaceUIInputMap(this.list, 0, null);
    }
    
    protected void installListeners() {
        final TransferHandler transferHandler = this.list.getTransferHandler();
        if (transferHandler == null || transferHandler instanceof UIResource) {
            this.list.setTransferHandler(BasicListUI.defaultTransferHandler);
            if (this.list.getDropTarget() instanceof UIResource) {
                this.list.setDropTarget(null);
            }
        }
        this.focusListener = this.createFocusListener();
        this.mouseInputListener = this.createMouseInputListener();
        this.propertyChangeListener = this.createPropertyChangeListener();
        this.listSelectionListener = this.createListSelectionListener();
        this.listDataListener = this.createListDataListener();
        this.list.addFocusListener(this.focusListener);
        this.list.addMouseListener(this.mouseInputListener);
        this.list.addMouseMotionListener(this.mouseInputListener);
        this.list.addPropertyChangeListener(this.propertyChangeListener);
        this.list.addKeyListener(this.getHandler());
        final ListModel model = this.list.getModel();
        if (model != null) {
            model.addListDataListener(this.listDataListener);
        }
        final ListSelectionModel selectionModel = this.list.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.addListSelectionListener(this.listSelectionListener);
        }
    }
    
    protected void uninstallListeners() {
        this.list.removeFocusListener(this.focusListener);
        this.list.removeMouseListener(this.mouseInputListener);
        this.list.removeMouseMotionListener(this.mouseInputListener);
        this.list.removePropertyChangeListener(this.propertyChangeListener);
        this.list.removeKeyListener(this.getHandler());
        final ListModel model = this.list.getModel();
        if (model != null) {
            model.removeListDataListener(this.listDataListener);
        }
        final ListSelectionModel selectionModel = this.list.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.removeListSelectionListener(this.listSelectionListener);
        }
        this.focusListener = null;
        this.mouseInputListener = null;
        this.listSelectionListener = null;
        this.listDataListener = null;
        this.propertyChangeListener = null;
        this.handler = null;
    }
    
    protected void installDefaults() {
        this.list.setLayout(null);
        LookAndFeel.installBorder(this.list, "List.border");
        LookAndFeel.installColorsAndFont(this.list, "List.background", "List.foreground", "List.font");
        LookAndFeel.installProperty(this.list, "opaque", Boolean.TRUE);
        if (this.list.getCellRenderer() == null) {
            this.list.setCellRenderer((ListCellRenderer)UIManager.get("List.cellRenderer"));
        }
        final Color selectionBackground = this.list.getSelectionBackground();
        if (selectionBackground == null || selectionBackground instanceof UIResource) {
            this.list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
        }
        final Color selectionForeground = this.list.getSelectionForeground();
        if (selectionForeground == null || selectionForeground instanceof UIResource) {
            this.list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
        }
        final Long n = (Long)UIManager.get("List.timeFactor");
        this.timeFactor = ((n != null) ? n : 1000L);
        this.updateIsFileList();
    }
    
    private void updateIsFileList() {
        final boolean equals = Boolean.TRUE.equals(this.list.getClientProperty("List.isFileList"));
        if (equals != this.isFileList) {
            this.isFileList = equals;
            final Font font = this.list.getFont();
            if (font == null || font instanceof UIResource) {
                final Font font2 = UIManager.getFont(equals ? "FileChooser.listFont" : "List.font");
                if (font2 != null && font2 != font) {
                    this.list.setFont(font2);
                }
            }
        }
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.list);
        if (this.list.getFont() instanceof UIResource) {
            this.list.setFont(null);
        }
        if (this.list.getForeground() instanceof UIResource) {
            this.list.setForeground(null);
        }
        if (this.list.getBackground() instanceof UIResource) {
            this.list.setBackground(null);
        }
        if (this.list.getSelectionBackground() instanceof UIResource) {
            this.list.setSelectionBackground(null);
        }
        if (this.list.getSelectionForeground() instanceof UIResource) {
            this.list.setSelectionForeground(null);
        }
        if (this.list.getCellRenderer() instanceof UIResource) {
            this.list.setCellRenderer(null);
        }
        if (this.list.getTransferHandler() instanceof UIResource) {
            this.list.setTransferHandler(null);
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.list = (JList)component;
        this.layoutOrientation = this.list.getLayoutOrientation();
        this.rendererPane = new CellRendererPane();
        this.list.add(this.rendererPane);
        this.columnCount = 1;
        this.updateLayoutStateNeeded = 1;
        this.isLeftToRight = this.list.getComponentOrientation().isLeftToRight();
        this.installDefaults();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallListeners();
        this.uninstallDefaults();
        this.uninstallKeyboardActions();
        final int n = -1;
        this.cellHeight = n;
        this.cellWidth = n;
        this.cellHeights = null;
        final int n2 = -1;
        this.listHeight = n2;
        this.listWidth = n2;
        this.list.remove(this.rendererPane);
        this.rendererPane = null;
        this.list = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicListUI();
    }
    
    @Override
    public int locationToIndex(final JList list, final Point point) {
        this.maybeUpdateLayoutState();
        return this.convertLocationToModel(point.x, point.y);
    }
    
    @Override
    public Point indexToLocation(final JList list, final int n) {
        this.maybeUpdateLayoutState();
        final Rectangle cellBounds = this.getCellBounds(list, n, n);
        if (cellBounds != null) {
            return new Point(cellBounds.x, cellBounds.y);
        }
        return null;
    }
    
    @Override
    public Rectangle getCellBounds(final JList list, final int n, final int n2) {
        this.maybeUpdateLayoutState();
        final int min = Math.min(n, n2);
        final int max = Math.max(n, n2);
        if (min >= list.getModel().getSize()) {
            return null;
        }
        final Rectangle cellBounds = this.getCellBounds(list, min);
        if (cellBounds == null) {
            return null;
        }
        if (min == max) {
            return cellBounds;
        }
        final Rectangle cellBounds2 = this.getCellBounds(list, max);
        if (cellBounds2 != null) {
            if (this.layoutOrientation == 2) {
                if (this.convertModelToRow(min) != this.convertModelToRow(max)) {
                    cellBounds.x = 0;
                    cellBounds.width = list.getWidth();
                }
            }
            else if (cellBounds.x != cellBounds2.x) {
                cellBounds.y = 0;
                cellBounds.height = list.getHeight();
            }
            cellBounds.add(cellBounds2);
        }
        return cellBounds;
    }
    
    private Rectangle getCellBounds(final JList list, final int n) {
        this.maybeUpdateLayoutState();
        final int convertModelToRow = this.convertModelToRow(n);
        final int convertModelToColumn = this.convertModelToColumn(n);
        if (convertModelToRow == -1 || convertModelToColumn == -1) {
            return null;
        }
        final Insets insets = list.getInsets();
        int cellWidth = this.cellWidth;
        int top = insets.top;
        int left = 0;
        int n2 = 0;
        switch (this.layoutOrientation) {
            case 1:
            case 2: {
                if (this.isLeftToRight) {
                    left = insets.left + convertModelToColumn * this.cellWidth;
                }
                else {
                    left = list.getWidth() - insets.right - (convertModelToColumn + 1) * this.cellWidth;
                }
                top += this.cellHeight * convertModelToRow;
                n2 = this.cellHeight;
                break;
            }
            default: {
                left = insets.left;
                if (this.cellHeights == null) {
                    top += this.cellHeight * convertModelToRow;
                }
                else if (convertModelToRow >= this.cellHeights.length) {
                    top = 0;
                }
                else {
                    for (int i = 0; i < convertModelToRow; ++i) {
                        top += this.cellHeights[i];
                    }
                }
                cellWidth = list.getWidth() - (insets.left + insets.right);
                n2 = this.getRowHeight(n);
                break;
            }
        }
        return new Rectangle(left, top, cellWidth, n2);
    }
    
    protected int getRowHeight(final int n) {
        return this.getHeight(0, n);
    }
    
    protected int convertYToRow(final int n) {
        return this.convertLocationToRow(0, n, false);
    }
    
    protected int convertRowToY(final int n) {
        if (n >= this.getRowCount(0) || n < 0) {
            return -1;
        }
        return this.getCellBounds(this.list, n, n).y;
    }
    
    private int getHeight(final int n, final int n2) {
        if (n < 0 || n > this.columnCount || n2 < 0) {
            return -1;
        }
        if (this.layoutOrientation != 0) {
            return this.cellHeight;
        }
        if (n2 >= this.list.getModel().getSize()) {
            return -1;
        }
        return (this.cellHeights == null) ? this.cellHeight : ((n2 < this.cellHeights.length) ? this.cellHeights[n2] : -1);
    }
    
    private int convertLocationToRow(final int n, final int n2, final boolean b) {
        final int size = this.list.getModel().getSize();
        if (size <= 0) {
            return -1;
        }
        final Insets insets = this.list.getInsets();
        if (this.cellHeights == null) {
            int n3 = (this.cellHeight == 0) ? 0 : ((n2 - insets.top) / this.cellHeight);
            if (b) {
                if (n3 < 0) {
                    n3 = 0;
                }
                else if (n3 >= size) {
                    n3 = size - 1;
                }
            }
            return n3;
        }
        if (size > this.cellHeights.length) {
            return -1;
        }
        int top = insets.top;
        int n4 = 0;
        if (b && n2 < top) {
            return 0;
        }
        int i;
        for (i = 0; i < size; ++i) {
            if (n2 >= top && n2 < top + this.cellHeights[i]) {
                return n4;
            }
            top += this.cellHeights[i];
            ++n4;
        }
        return i - 1;
    }
    
    private int convertLocationToRowInColumn(final int n, final int n2) {
        int n3 = 0;
        if (this.layoutOrientation != 0) {
            if (this.isLeftToRight) {
                n3 = n2 * this.cellWidth;
            }
            else {
                n3 = this.list.getWidth() - (n2 + 1) * this.cellWidth - this.list.getInsets().right;
            }
        }
        return this.convertLocationToRow(n3, n, true);
    }
    
    private int convertLocationToModel(final int n, final int n2) {
        final int convertLocationToRow = this.convertLocationToRow(n, n2, true);
        final int convertLocationToColumn = this.convertLocationToColumn(n, n2);
        if (convertLocationToRow >= 0 && convertLocationToColumn >= 0) {
            return this.getModelIndex(convertLocationToColumn, convertLocationToRow);
        }
        return -1;
    }
    
    private int getRowCount(final int n) {
        if (n < 0 || n >= this.columnCount) {
            return -1;
        }
        if (this.layoutOrientation == 0 || (n == 0 && this.columnCount == 1)) {
            return this.list.getModel().getSize();
        }
        if (n >= this.columnCount) {
            return -1;
        }
        if (this.layoutOrientation == 1) {
            if (n < this.columnCount - 1) {
                return this.rowsPerColumn;
            }
            return this.list.getModel().getSize() - (this.columnCount - 1) * this.rowsPerColumn;
        }
        else {
            if (n >= this.columnCount - (this.columnCount * this.rowsPerColumn - this.list.getModel().getSize())) {
                return Math.max(0, this.rowsPerColumn - 1);
            }
            return this.rowsPerColumn;
        }
    }
    
    private int getModelIndex(final int n, final int n2) {
        switch (this.layoutOrientation) {
            case 1: {
                return Math.min(this.list.getModel().getSize() - 1, this.rowsPerColumn * n + Math.min(n2, this.rowsPerColumn - 1));
            }
            case 2: {
                return Math.min(this.list.getModel().getSize() - 1, n2 * this.columnCount + n);
            }
            default: {
                return n2;
            }
        }
    }
    
    private int convertLocationToColumn(final int n, final int n2) {
        if (this.cellWidth <= 0) {
            return 0;
        }
        if (this.layoutOrientation == 0) {
            return 0;
        }
        final Insets insets = this.list.getInsets();
        int n3;
        if (this.isLeftToRight) {
            n3 = (n - insets.left) / this.cellWidth;
        }
        else {
            n3 = (this.list.getWidth() - n - insets.right - 1) / this.cellWidth;
        }
        if (n3 < 0) {
            return 0;
        }
        if (n3 >= this.columnCount) {
            return this.columnCount - 1;
        }
        return n3;
    }
    
    private int convertModelToRow(final int n) {
        final int size = this.list.getModel().getSize();
        if (n < 0 || n >= size) {
            return -1;
        }
        if (this.layoutOrientation == 0 || this.columnCount <= 1 || this.rowsPerColumn <= 0) {
            return n;
        }
        if (this.layoutOrientation == 1) {
            return n % this.rowsPerColumn;
        }
        return n / this.columnCount;
    }
    
    private int convertModelToColumn(final int n) {
        final int size = this.list.getModel().getSize();
        if (n < 0 || n >= size) {
            return -1;
        }
        if (this.layoutOrientation == 0 || this.rowsPerColumn <= 0 || this.columnCount <= 1) {
            return 0;
        }
        if (this.layoutOrientation == 1) {
            return n / this.rowsPerColumn;
        }
        return n % this.columnCount;
    }
    
    protected void maybeUpdateLayoutState() {
        if (this.updateLayoutStateNeeded != 0) {
            this.updateLayoutState();
            this.updateLayoutStateNeeded = 0;
        }
    }
    
    protected void updateLayoutState() {
        final int fixedCellHeight = this.list.getFixedCellHeight();
        final int fixedCellWidth = this.list.getFixedCellWidth();
        this.cellWidth = ((fixedCellWidth != -1) ? fixedCellWidth : -1);
        if (fixedCellHeight != -1) {
            this.cellHeight = fixedCellHeight;
            this.cellHeights = null;
        }
        else {
            this.cellHeight = -1;
            this.cellHeights = new int[this.list.getModel().getSize()];
        }
        if (fixedCellWidth == -1 || fixedCellHeight == -1) {
            final ListModel model = this.list.getModel();
            final int size = model.getSize();
            final ListCellRenderer cellRenderer = this.list.getCellRenderer();
            if (cellRenderer != null) {
                for (int i = 0; i < size; ++i) {
                    final Component listCellRendererComponent = cellRenderer.getListCellRendererComponent(this.list, model.getElementAt(i), i, false, false);
                    this.rendererPane.add(listCellRendererComponent);
                    final Dimension preferredSize = listCellRendererComponent.getPreferredSize();
                    if (fixedCellWidth == -1) {
                        this.cellWidth = Math.max(preferredSize.width, this.cellWidth);
                    }
                    if (fixedCellHeight == -1) {
                        this.cellHeights[i] = preferredSize.height;
                    }
                }
            }
            else {
                if (this.cellWidth == -1) {
                    this.cellWidth = 0;
                }
                if (this.cellHeights == null) {
                    this.cellHeights = new int[size];
                }
                for (int j = 0; j < size; ++j) {
                    this.cellHeights[j] = 0;
                }
            }
        }
        this.columnCount = 1;
        if (this.layoutOrientation != 0) {
            this.updateHorizontalLayoutState(fixedCellWidth, fixedCellHeight);
        }
    }
    
    private void updateHorizontalLayoutState(final int n, final int n2) {
        final int visibleRowCount = this.list.getVisibleRowCount();
        final int size = this.list.getModel().getSize();
        final Insets insets = this.list.getInsets();
        this.listHeight = this.list.getHeight();
        this.listWidth = this.list.getWidth();
        if (size == 0) {
            final int n3 = 0;
            this.columnCount = n3;
            this.rowsPerColumn = n3;
            this.preferredHeight = insets.top + insets.bottom;
            return;
        }
        int n4;
        if (n2 != -1) {
            n4 = n2;
        }
        else {
            int max = 0;
            if (this.cellHeights.length > 0) {
                max = this.cellHeights[this.cellHeights.length - 1];
                for (int i = this.cellHeights.length - 2; i >= 0; --i) {
                    max = Math.max(max, this.cellHeights[i]);
                }
            }
            final int cellHeight = max;
            this.cellHeight = cellHeight;
            n4 = cellHeight;
            this.cellHeights = null;
        }
        this.rowsPerColumn = size;
        if (visibleRowCount > 0) {
            this.rowsPerColumn = visibleRowCount;
            this.columnCount = Math.max(1, size / this.rowsPerColumn);
            if (size > 0 && size > this.rowsPerColumn && size % this.rowsPerColumn != 0) {
                ++this.columnCount;
            }
            if (this.layoutOrientation == 2) {
                this.rowsPerColumn = size / this.columnCount;
                if (size % this.columnCount > 0) {
                    ++this.rowsPerColumn;
                }
            }
        }
        else if (this.layoutOrientation == 1 && n4 != 0) {
            this.rowsPerColumn = Math.max(1, (this.listHeight - insets.top - insets.bottom) / n4);
            this.columnCount = Math.max(1, size / this.rowsPerColumn);
            if (size > 0 && size > this.rowsPerColumn && size % this.rowsPerColumn != 0) {
                ++this.columnCount;
            }
        }
        else if (this.layoutOrientation == 2 && this.cellWidth > 0 && this.listWidth > 0) {
            this.columnCount = Math.max(1, (this.listWidth - insets.left - insets.right) / this.cellWidth);
            this.rowsPerColumn = size / this.columnCount;
            if (size % this.columnCount > 0) {
                ++this.rowsPerColumn;
            }
        }
        this.preferredHeight = this.rowsPerColumn * this.cellHeight + insets.top + insets.bottom;
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected MouseInputListener createMouseInputListener() {
        return this.getHandler();
    }
    
    protected FocusListener createFocusListener() {
        return this.getHandler();
    }
    
    protected ListSelectionListener createListSelectionListener() {
        return this.getHandler();
    }
    
    private void redrawList() {
        this.list.revalidate();
        this.list.repaint();
    }
    
    protected ListDataListener createListDataListener() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    private static int adjustIndex(final int n, final JList list) {
        return (n < list.getModel().getSize()) ? n : -1;
    }
    
    static {
        BASELINE_COMPONENT_KEY = new StringBuilder("List.baselineComponent");
        defaultTransferHandler = new ListTransferHandler();
    }
    
    public class MouseInputHandler implements MouseInputListener
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mouseClicked(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mouseExited(mouseEvent);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mouseDragged(mouseEvent);
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mouseMoved(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicListUI.this.getHandler().mouseReleased(mouseEvent);
        }
    }
    
    public class FocusHandler implements FocusListener
    {
        protected void repaintCellFocus() {
            BasicListUI.this.getHandler().repaintCellFocus();
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicListUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicListUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    public class ListSelectionHandler implements ListSelectionListener
    {
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            BasicListUI.this.getHandler().valueChanged(listSelectionEvent);
        }
    }
    
    public class ListDataHandler implements ListDataListener
    {
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            BasicListUI.this.getHandler().intervalAdded(listDataEvent);
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            BasicListUI.this.getHandler().intervalRemoved(listDataEvent);
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            BasicListUI.this.getHandler().contentsChanged(listDataEvent);
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicListUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String SELECT_PREVIOUS_COLUMN = "selectPreviousColumn";
        private static final String SELECT_PREVIOUS_COLUMN_EXTEND = "selectPreviousColumnExtendSelection";
        private static final String SELECT_PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
        private static final String SELECT_NEXT_COLUMN = "selectNextColumn";
        private static final String SELECT_NEXT_COLUMN_EXTEND = "selectNextColumnExtendSelection";
        private static final String SELECT_NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
        private static final String SELECT_PREVIOUS_ROW = "selectPreviousRow";
        private static final String SELECT_PREVIOUS_ROW_EXTEND = "selectPreviousRowExtendSelection";
        private static final String SELECT_PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
        private static final String SELECT_NEXT_ROW = "selectNextRow";
        private static final String SELECT_NEXT_ROW_EXTEND = "selectNextRowExtendSelection";
        private static final String SELECT_NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
        private static final String SELECT_FIRST_ROW = "selectFirstRow";
        private static final String SELECT_FIRST_ROW_EXTEND = "selectFirstRowExtendSelection";
        private static final String SELECT_FIRST_ROW_CHANGE_LEAD = "selectFirstRowChangeLead";
        private static final String SELECT_LAST_ROW = "selectLastRow";
        private static final String SELECT_LAST_ROW_EXTEND = "selectLastRowExtendSelection";
        private static final String SELECT_LAST_ROW_CHANGE_LEAD = "selectLastRowChangeLead";
        private static final String SCROLL_UP = "scrollUp";
        private static final String SCROLL_UP_EXTEND = "scrollUpExtendSelection";
        private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
        private static final String SCROLL_DOWN = "scrollDown";
        private static final String SCROLL_DOWN_EXTEND = "scrollDownExtendSelection";
        private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
        private static final String SELECT_ALL = "selectAll";
        private static final String CLEAR_SELECTION = "clearSelection";
        private static final String ADD_TO_SELECTION = "addToSelection";
        private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
        private static final String EXTEND_TO = "extendTo";
        private static final String MOVE_SELECTION_TO = "moveSelectionTo";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final String name = this.getName();
            final JList list = (JList)actionEvent.getSource();
            final BasicListUI basicListUI = (BasicListUI)BasicLookAndFeel.getUIOfType(list.getUI(), BasicListUI.class);
            if (name == "selectPreviousColumn") {
                this.changeSelection(list, 1, this.getNextColumnIndex(list, basicListUI, -1), -1);
            }
            else if (name == "selectPreviousColumnExtendSelection") {
                this.changeSelection(list, 2, this.getNextColumnIndex(list, basicListUI, -1), -1);
            }
            else if (name == "selectPreviousColumnChangeLead") {
                this.changeSelection(list, 0, this.getNextColumnIndex(list, basicListUI, -1), -1);
            }
            else if (name == "selectNextColumn") {
                this.changeSelection(list, 1, this.getNextColumnIndex(list, basicListUI, 1), 1);
            }
            else if (name == "selectNextColumnExtendSelection") {
                this.changeSelection(list, 2, this.getNextColumnIndex(list, basicListUI, 1), 1);
            }
            else if (name == "selectNextColumnChangeLead") {
                this.changeSelection(list, 0, this.getNextColumnIndex(list, basicListUI, 1), 1);
            }
            else if (name == "selectPreviousRow") {
                this.changeSelection(list, 1, this.getNextIndex(list, basicListUI, -1), -1);
            }
            else if (name == "selectPreviousRowExtendSelection") {
                this.changeSelection(list, 2, this.getNextIndex(list, basicListUI, -1), -1);
            }
            else if (name == "selectPreviousRowChangeLead") {
                this.changeSelection(list, 0, this.getNextIndex(list, basicListUI, -1), -1);
            }
            else if (name == "selectNextRow") {
                this.changeSelection(list, 1, this.getNextIndex(list, basicListUI, 1), 1);
            }
            else if (name == "selectNextRowExtendSelection") {
                this.changeSelection(list, 2, this.getNextIndex(list, basicListUI, 1), 1);
            }
            else if (name == "selectNextRowChangeLead") {
                this.changeSelection(list, 0, this.getNextIndex(list, basicListUI, 1), 1);
            }
            else if (name == "selectFirstRow") {
                this.changeSelection(list, 1, 0, -1);
            }
            else if (name == "selectFirstRowExtendSelection") {
                this.changeSelection(list, 2, 0, -1);
            }
            else if (name == "selectFirstRowChangeLead") {
                this.changeSelection(list, 0, 0, -1);
            }
            else if (name == "selectLastRow") {
                this.changeSelection(list, 1, list.getModel().getSize() - 1, 1);
            }
            else if (name == "selectLastRowExtendSelection") {
                this.changeSelection(list, 2, list.getModel().getSize() - 1, 1);
            }
            else if (name == "selectLastRowChangeLead") {
                this.changeSelection(list, 0, list.getModel().getSize() - 1, 1);
            }
            else if (name == "scrollUp") {
                this.changeSelection(list, 1, this.getNextPageIndex(list, -1), -1);
            }
            else if (name == "scrollUpExtendSelection") {
                this.changeSelection(list, 2, this.getNextPageIndex(list, -1), -1);
            }
            else if (name == "scrollUpChangeLead") {
                this.changeSelection(list, 0, this.getNextPageIndex(list, -1), -1);
            }
            else if (name == "scrollDown") {
                this.changeSelection(list, 1, this.getNextPageIndex(list, 1), 1);
            }
            else if (name == "scrollDownExtendSelection") {
                this.changeSelection(list, 2, this.getNextPageIndex(list, 1), 1);
            }
            else if (name == "scrollDownChangeLead") {
                this.changeSelection(list, 0, this.getNextPageIndex(list, 1), 1);
            }
            else if (name == "selectAll") {
                this.selectAll(list);
            }
            else if (name == "clearSelection") {
                this.clearSelection(list);
            }
            else if (name == "addToSelection") {
                final int access$200 = adjustIndex(list.getSelectionModel().getLeadSelectionIndex(), list);
                if (!list.isSelectedIndex(access$200)) {
                    final int anchorSelectionIndex = list.getSelectionModel().getAnchorSelectionIndex();
                    list.setValueIsAdjusting(true);
                    list.addSelectionInterval(access$200, access$200);
                    list.getSelectionModel().setAnchorSelectionIndex(anchorSelectionIndex);
                    list.setValueIsAdjusting(false);
                }
            }
            else if (name == "toggleAndAnchor") {
                final int access$201 = adjustIndex(list.getSelectionModel().getLeadSelectionIndex(), list);
                if (list.isSelectedIndex(access$201)) {
                    list.removeSelectionInterval(access$201, access$201);
                }
                else {
                    list.addSelectionInterval(access$201, access$201);
                }
            }
            else if (name == "extendTo") {
                this.changeSelection(list, 2, adjustIndex(list.getSelectionModel().getLeadSelectionIndex(), list), 0);
            }
            else if (name == "moveSelectionTo") {
                this.changeSelection(list, 1, adjustIndex(list.getSelectionModel().getLeadSelectionIndex(), list), 0);
            }
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            final String name = this.getName();
            return (name != "selectPreviousColumnChangeLead" && name != "selectNextColumnChangeLead" && name != "selectPreviousRowChangeLead" && name != "selectNextRowChangeLead" && name != "selectFirstRowChangeLead" && name != "selectLastRowChangeLead" && name != "scrollUpChangeLead" && name != "scrollDownChangeLead") || (o != null && ((JList)o).getSelectionModel() instanceof DefaultListSelectionModel);
        }
        
        private void clearSelection(final JList list) {
            list.clearSelection();
        }
        
        private void selectAll(final JList list) {
            final int size = list.getModel().getSize();
            if (size > 0) {
                final ListSelectionModel selectionModel = list.getSelectionModel();
                int access$200 = adjustIndex(selectionModel.getLeadSelectionIndex(), list);
                if (selectionModel.getSelectionMode() == 0) {
                    if (access$200 == -1) {
                        final int access$201 = adjustIndex(list.getMinSelectionIndex(), list);
                        access$200 = ((access$201 == -1) ? 0 : access$201);
                    }
                    list.setSelectionInterval(access$200, access$200);
                    list.ensureIndexIsVisible(access$200);
                }
                else {
                    list.setValueIsAdjusting(true);
                    final int access$202 = adjustIndex(selectionModel.getAnchorSelectionIndex(), list);
                    list.setSelectionInterval(0, size - 1);
                    SwingUtilities2.setLeadAnchorWithoutSelection(selectionModel, access$202, access$200);
                    list.setValueIsAdjusting(false);
                }
            }
        }
        
        private int getNextPageIndex(final JList list, int n) {
            if (list.getModel().getSize() == 0) {
                return -1;
            }
            final Rectangle visibleRect = list.getVisibleRect();
            final int access$200 = adjustIndex(list.getSelectionModel().getLeadSelectionIndex(), list);
            final Rectangle rectangle = (access$200 == -1) ? new Rectangle() : list.getCellBounds(access$200, access$200);
            int n2;
            if (list.getLayoutOrientation() == 1 && list.getVisibleRowCount() <= 0) {
                if (!list.getComponentOrientation().isLeftToRight()) {
                    n = -n;
                }
                if (n < 0) {
                    visibleRect.x = rectangle.x + rectangle.width - visibleRect.width;
                    final Point point = new Point(visibleRect.x - 1, rectangle.y);
                    n2 = list.locationToIndex(point);
                    Rectangle rectangle2 = list.getCellBounds(n2, n2);
                    if (visibleRect.intersects(rectangle2)) {
                        point.x = rectangle2.x - 1;
                        n2 = list.locationToIndex(point);
                        rectangle2 = list.getCellBounds(n2, n2);
                    }
                    if (rectangle2.y != rectangle.y) {
                        point.x = rectangle2.x + rectangle2.width;
                        n2 = list.locationToIndex(point);
                    }
                }
                else {
                    visibleRect.x = rectangle.x;
                    final Point point2 = new Point(visibleRect.x + visibleRect.width, rectangle.y);
                    n2 = list.locationToIndex(point2);
                    Rectangle rectangle3 = list.getCellBounds(n2, n2);
                    if (visibleRect.intersects(rectangle3)) {
                        point2.x = rectangle3.x + rectangle3.width;
                        n2 = list.locationToIndex(point2);
                        rectangle3 = list.getCellBounds(n2, n2);
                    }
                    if (rectangle3.y != rectangle.y) {
                        point2.x = rectangle3.x - 1;
                        n2 = list.locationToIndex(point2);
                    }
                }
            }
            else if (n < 0) {
                final Point point3 = new Point(rectangle.x, visibleRect.y);
                n2 = list.locationToIndex(point3);
                if (access$200 <= n2) {
                    visibleRect.y = rectangle.y + rectangle.height - visibleRect.height;
                    point3.y = visibleRect.y;
                    n2 = list.locationToIndex(point3);
                    Rectangle rectangle4 = list.getCellBounds(n2, n2);
                    if (rectangle4.y < visibleRect.y) {
                        point3.y = rectangle4.y + rectangle4.height;
                        n2 = list.locationToIndex(point3);
                        rectangle4 = list.getCellBounds(n2, n2);
                    }
                    if (rectangle4.y >= rectangle.y) {
                        point3.y = rectangle.y - 1;
                        n2 = list.locationToIndex(point3);
                    }
                }
            }
            else {
                final Point point4 = new Point(rectangle.x, visibleRect.y + visibleRect.height - 1);
                n2 = list.locationToIndex(point4);
                final Rectangle cellBounds = list.getCellBounds(n2, n2);
                if (cellBounds.y + cellBounds.height > visibleRect.y + visibleRect.height) {
                    point4.y = cellBounds.y - 1;
                    final int locationToIndex = list.locationToIndex(point4);
                    list.getCellBounds(locationToIndex, locationToIndex);
                    n2 = Math.max(locationToIndex, access$200);
                }
                if (access$200 >= n2) {
                    visibleRect.y = rectangle.y;
                    point4.y = visibleRect.y + visibleRect.height - 1;
                    n2 = list.locationToIndex(point4);
                    Rectangle rectangle5 = list.getCellBounds(n2, n2);
                    if (rectangle5.y + rectangle5.height > visibleRect.y + visibleRect.height) {
                        point4.y = rectangle5.y - 1;
                        n2 = list.locationToIndex(point4);
                        rectangle5 = list.getCellBounds(n2, n2);
                    }
                    if (rectangle5.y <= rectangle.y) {
                        point4.y = rectangle.y + rectangle.height;
                        n2 = list.locationToIndex(point4);
                    }
                }
            }
            return n2;
        }
        
        private void changeSelection(final JList list, int n, final int selectedIndex, final int n2) {
            if (selectedIndex >= 0 && selectedIndex < list.getModel().getSize()) {
                final ListSelectionModel selectionModel = list.getSelectionModel();
                if (n == 0 && list.getSelectionMode() != 2) {
                    n = 1;
                }
                this.adjustScrollPositionIfNecessary(list, selectedIndex, n2);
                if (n == 2) {
                    int access$200 = adjustIndex(selectionModel.getAnchorSelectionIndex(), list);
                    if (access$200 == -1) {
                        access$200 = 0;
                    }
                    list.setSelectionInterval(access$200, selectedIndex);
                }
                else if (n == 1) {
                    list.setSelectedIndex(selectedIndex);
                }
                else {
                    ((DefaultListSelectionModel)selectionModel).moveLeadSelectionIndex(selectedIndex);
                }
            }
        }
        
        private void adjustScrollPositionIfNecessary(final JList list, final int n, final int n2) {
            if (n2 == 0) {
                return;
            }
            Rectangle cellBounds = list.getCellBounds(n, n);
            final Rectangle visibleRect = list.getVisibleRect();
            if (cellBounds != null && !visibleRect.contains(cellBounds)) {
                if (list.getLayoutOrientation() == 1 && list.getVisibleRowCount() <= 0) {
                    if (list.getComponentOrientation().isLeftToRight()) {
                        if (n2 > 0) {
                            final int max = Math.max(0, cellBounds.x + cellBounds.width - visibleRect.width);
                            final int locationToIndex = list.locationToIndex(new Point(max, cellBounds.y));
                            Rectangle rectangle = list.getCellBounds(locationToIndex, locationToIndex);
                            if (rectangle.x < max && rectangle.x < cellBounds.x) {
                                final Rectangle rectangle2 = rectangle;
                                rectangle2.x += rectangle.width;
                                final int locationToIndex2 = list.locationToIndex(rectangle.getLocation());
                                rectangle = list.getCellBounds(locationToIndex2, locationToIndex2);
                            }
                            cellBounds = rectangle;
                        }
                        cellBounds.width = visibleRect.width;
                    }
                    else if (n2 > 0) {
                        final int n3 = cellBounds.x + visibleRect.width;
                        final int locationToIndex3 = list.locationToIndex(new Point(n3, cellBounds.y));
                        final Rectangle cellBounds2 = list.getCellBounds(locationToIndex3, locationToIndex3);
                        if (cellBounds2.x + cellBounds2.width > n3 && cellBounds2.x > cellBounds.x) {
                            cellBounds2.width = 0;
                        }
                        cellBounds.x = Math.max(0, cellBounds2.x + cellBounds2.width - visibleRect.width);
                        cellBounds.width = visibleRect.width;
                    }
                    else {
                        final Rectangle rectangle3 = cellBounds;
                        rectangle3.x += Math.max(0, cellBounds.width - visibleRect.width);
                        cellBounds.width = Math.min(cellBounds.width, visibleRect.width);
                    }
                }
                else if (n2 > 0 && (cellBounds.y < visibleRect.y || cellBounds.y + cellBounds.height > visibleRect.y + visibleRect.height)) {
                    final int max2 = Math.max(0, cellBounds.y + cellBounds.height - visibleRect.height);
                    final int locationToIndex4 = list.locationToIndex(new Point(cellBounds.x, max2));
                    Rectangle rectangle4 = list.getCellBounds(locationToIndex4, locationToIndex4);
                    if (rectangle4.y < max2 && rectangle4.y < cellBounds.y) {
                        final Rectangle rectangle5 = rectangle4;
                        rectangle5.y += rectangle4.height;
                        final int locationToIndex5 = list.locationToIndex(rectangle4.getLocation());
                        rectangle4 = list.getCellBounds(locationToIndex5, locationToIndex5);
                    }
                    cellBounds = rectangle4;
                    cellBounds.height = visibleRect.height;
                }
                else {
                    cellBounds.height = Math.min(cellBounds.height, visibleRect.height);
                }
                list.scrollRectToVisible(cellBounds);
            }
        }
        
        private int getNextColumnIndex(final JList list, final BasicListUI basicListUI, final int n) {
            if (list.getLayoutOrientation() == 0) {
                return -1;
            }
            final int access$200 = adjustIndex(list.getLeadSelectionIndex(), list);
            final int size = list.getModel().getSize();
            if (access$200 == -1) {
                return 0;
            }
            if (size == 1) {
                return 0;
            }
            if (basicListUI == null || basicListUI.columnCount <= 1) {
                return -1;
            }
            final int access$201 = basicListUI.convertModelToColumn(access$200);
            final int access$202 = basicListUI.convertModelToRow(access$200);
            final int n2 = access$201 + n;
            if (n2 >= basicListUI.columnCount || n2 < 0) {
                return -1;
            }
            if (access$202 >= basicListUI.getRowCount(n2)) {
                return -1;
            }
            return basicListUI.getModelIndex(n2, access$202);
        }
        
        private int getNextIndex(final JList list, final BasicListUI basicListUI, final int n) {
            int access$200 = adjustIndex(list.getLeadSelectionIndex(), list);
            final int size = list.getModel().getSize();
            if (access$200 == -1) {
                if (size > 0) {
                    if (n > 0) {
                        access$200 = 0;
                    }
                    else {
                        access$200 = size - 1;
                    }
                }
            }
            else if (size == 1) {
                access$200 = 0;
            }
            else if (list.getLayoutOrientation() == 2) {
                if (basicListUI != null) {
                    access$200 += basicListUI.columnCount * n;
                }
            }
            else {
                access$200 += n;
            }
            return access$200;
        }
    }
    
    private class Handler implements FocusListener, KeyListener, ListDataListener, ListSelectionListener, MouseInputListener, PropertyChangeListener, DragRecognitionSupport.BeforeDrag
    {
        private String prefix;
        private String typedString;
        private long lastTime;
        private boolean dragPressDidSelection;
        
        private Handler() {
            this.prefix = "";
            this.typedString = "";
            this.lastTime = 0L;
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            final JList list = (JList)keyEvent.getSource();
            final ListModel model = list.getModel();
            if (model.getSize() == 0 || keyEvent.isAltDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(keyEvent) || this.isNavigationKey(keyEvent)) {
                return;
            }
            boolean b = true;
            final char keyChar = keyEvent.getKeyChar();
            final long when = keyEvent.getWhen();
            int access$200 = adjustIndex(list.getLeadSelectionIndex(), BasicListUI.this.list);
            if (when - this.lastTime < BasicListUI.this.timeFactor) {
                this.typedString += keyChar;
                if (this.prefix.length() == 1 && keyChar == this.prefix.charAt(0)) {
                    ++access$200;
                }
                else {
                    this.prefix = this.typedString;
                }
            }
            else {
                ++access$200;
                this.typedString = "" + keyChar;
                this.prefix = this.typedString;
            }
            this.lastTime = when;
            if (access$200 < 0 || access$200 >= model.getSize()) {
                b = false;
                access$200 = 0;
            }
            final int nextMatch = list.getNextMatch(this.prefix, access$200, Position.Bias.Forward);
            if (nextMatch >= 0) {
                list.setSelectedIndex(nextMatch);
                list.ensureIndexIsVisible(nextMatch);
            }
            else if (b) {
                final int nextMatch2 = list.getNextMatch(this.prefix, 0, Position.Bias.Forward);
                if (nextMatch2 >= 0) {
                    list.setSelectedIndex(nextMatch2);
                    list.ensureIndexIsVisible(nextMatch2);
                }
            }
        }
        
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (this.isNavigationKey(keyEvent)) {
                this.prefix = "";
                this.typedString = "";
                this.lastTime = 0L;
            }
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
        }
        
        private boolean isNavigationKey(final KeyEvent keyEvent) {
            final InputMap inputMap = BasicListUI.this.list.getInputMap(1);
            final KeyStroke keyStrokeForEvent = KeyStroke.getKeyStrokeForEvent(keyEvent);
            return inputMap != null && inputMap.get(keyStrokeForEvent) != null;
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "model") {
                final ListModel listModel = (ListModel)propertyChangeEvent.getOldValue();
                final ListModel listModel2 = (ListModel)propertyChangeEvent.getNewValue();
                if (listModel != null) {
                    listModel.removeListDataListener(BasicListUI.this.listDataListener);
                }
                if (listModel2 != null) {
                    listModel2.addListDataListener(BasicListUI.this.listDataListener);
                }
                final BasicListUI this$0 = BasicListUI.this;
                this$0.updateLayoutStateNeeded |= 0x1;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "selectionModel") {
                final ListSelectionModel listSelectionModel = (ListSelectionModel)propertyChangeEvent.getOldValue();
                final ListSelectionModel listSelectionModel2 = (ListSelectionModel)propertyChangeEvent.getNewValue();
                if (listSelectionModel != null) {
                    listSelectionModel.removeListSelectionListener(BasicListUI.this.listSelectionListener);
                }
                if (listSelectionModel2 != null) {
                    listSelectionModel2.addListSelectionListener(BasicListUI.this.listSelectionListener);
                }
                final BasicListUI this$2 = BasicListUI.this;
                this$2.updateLayoutStateNeeded |= 0x1;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "cellRenderer") {
                final BasicListUI this$3 = BasicListUI.this;
                this$3.updateLayoutStateNeeded |= 0x40;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "font") {
                final BasicListUI this$4 = BasicListUI.this;
                this$4.updateLayoutStateNeeded |= 0x4;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "prototypeCellValue") {
                final BasicListUI this$5 = BasicListUI.this;
                this$5.updateLayoutStateNeeded |= 0x20;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "fixedCellHeight") {
                final BasicListUI this$6 = BasicListUI.this;
                this$6.updateLayoutStateNeeded |= 0x10;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "fixedCellWidth") {
                final BasicListUI this$7 = BasicListUI.this;
                this$7.updateLayoutStateNeeded |= 0x8;
                BasicListUI.this.redrawList();
            }
            else if (propertyName == "selectionForeground") {
                BasicListUI.this.list.repaint();
            }
            else if (propertyName == "selectionBackground") {
                BasicListUI.this.list.repaint();
            }
            else if ("layoutOrientation" == propertyName) {
                final BasicListUI this$8 = BasicListUI.this;
                this$8.updateLayoutStateNeeded |= 0x80;
                BasicListUI.this.layoutOrientation = BasicListUI.this.list.getLayoutOrientation();
                BasicListUI.this.redrawList();
            }
            else if ("visibleRowCount" == propertyName) {
                if (BasicListUI.this.layoutOrientation != 0) {
                    final BasicListUI this$9 = BasicListUI.this;
                    this$9.updateLayoutStateNeeded |= 0x80;
                    BasicListUI.this.redrawList();
                }
            }
            else if ("componentOrientation" == propertyName) {
                BasicListUI.this.isLeftToRight = BasicListUI.this.list.getComponentOrientation().isLeftToRight();
                final BasicListUI this$10 = BasicListUI.this;
                this$10.updateLayoutStateNeeded |= 0x400;
                BasicListUI.this.redrawList();
                SwingUtilities.replaceUIInputMap(BasicListUI.this.list, 0, BasicListUI.this.getInputMap(0));
            }
            else if ("List.isFileList" == propertyName) {
                BasicListUI.this.updateIsFileList();
                BasicListUI.this.redrawList();
            }
            else if ("dropLocation" == propertyName) {
                this.repaintDropLocation((JList.DropLocation)propertyChangeEvent.getOldValue());
                this.repaintDropLocation(BasicListUI.this.list.getDropLocation());
            }
        }
        
        private void repaintDropLocation(final JList.DropLocation dropLocation) {
            if (dropLocation == null) {
                return;
            }
            Rectangle rectangle;
            if (dropLocation.isInsert()) {
                rectangle = BasicListUI.this.getDropLineRect(dropLocation);
            }
            else {
                rectangle = BasicListUI.this.getCellBounds(BasicListUI.this.list, dropLocation.getIndex());
            }
            if (rectangle != null) {
                BasicListUI.this.list.repaint(rectangle);
            }
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            BasicListUI.this.updateLayoutStateNeeded = 1;
            final int min = Math.min(listDataEvent.getIndex0(), listDataEvent.getIndex1());
            final int max = Math.max(listDataEvent.getIndex0(), listDataEvent.getIndex1());
            final ListSelectionModel selectionModel = BasicListUI.this.list.getSelectionModel();
            if (selectionModel != null) {
                selectionModel.insertIndexInterval(min, max - min + 1, true);
            }
            BasicListUI.this.redrawList();
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            BasicListUI.this.updateLayoutStateNeeded = 1;
            final ListSelectionModel selectionModel = BasicListUI.this.list.getSelectionModel();
            if (selectionModel != null) {
                selectionModel.removeIndexInterval(listDataEvent.getIndex0(), listDataEvent.getIndex1());
            }
            BasicListUI.this.redrawList();
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            BasicListUI.this.updateLayoutStateNeeded = 1;
            BasicListUI.this.redrawList();
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            BasicListUI.this.maybeUpdateLayoutState();
            final int size = BasicListUI.this.list.getModel().getSize();
            final Rectangle cellBounds = BasicListUI.this.getCellBounds(BasicListUI.this.list, Math.min(size - 1, Math.max(listSelectionEvent.getFirstIndex(), 0)), Math.min(size - 1, Math.max(listSelectionEvent.getLastIndex(), 0)));
            if (cellBounds != null) {
                BasicListUI.this.list.repaint(cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height);
            }
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicListUI.this.list)) {
                return;
            }
            final boolean dragEnabled = BasicListUI.this.list.getDragEnabled();
            boolean b = true;
            if (dragEnabled) {
                final int loc2IndexFileList = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, mouseEvent.getPoint());
                if (loc2IndexFileList != -1 && DragRecognitionSupport.mousePressed(mouseEvent)) {
                    this.dragPressDidSelection = false;
                    if (BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent)) {
                        return;
                    }
                    if (!mouseEvent.isShiftDown() && BasicListUI.this.list.isSelectedIndex(loc2IndexFileList)) {
                        BasicListUI.this.list.addSelectionInterval(loc2IndexFileList, loc2IndexFileList);
                        return;
                    }
                    b = false;
                    this.dragPressDidSelection = true;
                }
            }
            else {
                BasicListUI.this.list.setValueIsAdjusting(true);
            }
            if (b) {
                SwingUtilities2.adjustFocus(BasicListUI.this.list);
            }
            this.adjustSelection(mouseEvent);
        }
        
        private void adjustSelection(final MouseEvent mouseEvent) {
            final int loc2IndexFileList = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, mouseEvent.getPoint());
            if (loc2IndexFileList < 0) {
                if (BasicListUI.this.isFileList && mouseEvent.getID() == 501 && (!mouseEvent.isShiftDown() || BasicListUI.this.list.getSelectionMode() == 0)) {
                    BasicListUI.this.list.clearSelection();
                }
            }
            else {
                int access$200 = adjustIndex(BasicListUI.this.list.getAnchorSelectionIndex(), BasicListUI.this.list);
                boolean selectedIndex;
                if (access$200 == -1) {
                    access$200 = 0;
                    selectedIndex = false;
                }
                else {
                    selectedIndex = BasicListUI.this.list.isSelectedIndex(access$200);
                }
                if (BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent)) {
                    if (mouseEvent.isShiftDown()) {
                        if (selectedIndex) {
                            BasicListUI.this.list.addSelectionInterval(access$200, loc2IndexFileList);
                        }
                        else {
                            BasicListUI.this.list.removeSelectionInterval(access$200, loc2IndexFileList);
                            if (BasicListUI.this.isFileList) {
                                BasicListUI.this.list.addSelectionInterval(loc2IndexFileList, loc2IndexFileList);
                                BasicListUI.this.list.getSelectionModel().setAnchorSelectionIndex(access$200);
                            }
                        }
                    }
                    else if (BasicListUI.this.list.isSelectedIndex(loc2IndexFileList)) {
                        BasicListUI.this.list.removeSelectionInterval(loc2IndexFileList, loc2IndexFileList);
                    }
                    else {
                        BasicListUI.this.list.addSelectionInterval(loc2IndexFileList, loc2IndexFileList);
                    }
                }
                else if (mouseEvent.isShiftDown()) {
                    BasicListUI.this.list.setSelectionInterval(access$200, loc2IndexFileList);
                }
                else {
                    BasicListUI.this.list.setSelectionInterval(loc2IndexFileList, loc2IndexFileList);
                }
            }
        }
        
        @Override
        public void dragStarting(final MouseEvent mouseEvent) {
            if (BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent)) {
                final int loc2IndexFileList = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, mouseEvent.getPoint());
                BasicListUI.this.list.addSelectionInterval(loc2IndexFileList, loc2IndexFileList);
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicListUI.this.list)) {
                return;
            }
            if (BasicListUI.this.list.getDragEnabled()) {
                DragRecognitionSupport.mouseDragged(mouseEvent, this);
                return;
            }
            if (mouseEvent.isShiftDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent)) {
                return;
            }
            final int locationToIndex = BasicListUI.this.locationToIndex(BasicListUI.this.list, mouseEvent.getPoint());
            if (locationToIndex != -1) {
                if (BasicListUI.this.isFileList) {
                    return;
                }
                final Rectangle cellBounds = BasicListUI.this.getCellBounds(BasicListUI.this.list, locationToIndex, locationToIndex);
                if (cellBounds != null) {
                    BasicListUI.this.list.scrollRectToVisible(cellBounds);
                    BasicListUI.this.list.setSelectionInterval(locationToIndex, locationToIndex);
                }
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (SwingUtilities2.shouldIgnore(mouseEvent, BasicListUI.this.list)) {
                return;
            }
            if (BasicListUI.this.list.getDragEnabled()) {
                final MouseEvent mouseReleased = DragRecognitionSupport.mouseReleased(mouseEvent);
                if (mouseReleased != null) {
                    SwingUtilities2.adjustFocus(BasicListUI.this.list);
                    if (!this.dragPressDidSelection) {
                        this.adjustSelection(mouseReleased);
                    }
                }
            }
            else {
                BasicListUI.this.list.setValueIsAdjusting(false);
            }
        }
        
        protected void repaintCellFocus() {
            final int access$200 = adjustIndex(BasicListUI.this.list.getLeadSelectionIndex(), BasicListUI.this.list);
            if (access$200 != -1) {
                final Rectangle cellBounds = BasicListUI.this.getCellBounds(BasicListUI.this.list, access$200, access$200);
                if (cellBounds != null) {
                    BasicListUI.this.list.repaint(cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height);
                }
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            this.repaintCellFocus();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            this.repaintCellFocus();
        }
    }
    
    static class ListTransferHandler extends TransferHandler implements UIResource
    {
        @Override
        protected Transferable createTransferable(final JComponent component) {
            if (!(component instanceof JList)) {
                return null;
            }
            final Object[] selectedValues = ((JList)component).getSelectedValues();
            if (selectedValues == null || selectedValues.length == 0) {
                return null;
            }
            final StringBuffer sb = new StringBuffer();
            final StringBuffer sb2 = new StringBuffer();
            sb2.append("<html>\n<body>\n<ul>\n");
            for (int i = 0; i < selectedValues.length; ++i) {
                final Object o = selectedValues[i];
                final String s = (o == null) ? "" : o.toString();
                sb.append(s + "\n");
                sb2.append("  <li>" + s + "\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb2.append("</ul>\n</body>\n</html>");
            return new BasicTransferable(sb.toString(), sb2.toString());
        }
        
        @Override
        public int getSourceActions(final JComponent component) {
            return 1;
        }
    }
}
