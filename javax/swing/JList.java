package javax.swing;

import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleIcon;
import java.awt.event.FocusListener;
import java.awt.IllegalComponentStateException;
import java.awt.FontMetrics;
import java.awt.Cursor;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleText;
import java.util.Locale;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleRole;
import javax.swing.event.ListDataEvent;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleState;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeListener;
import javax.accessibility.AccessibleSelection;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Container;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import javax.swing.text.Position;
import sun.swing.SwingUtilities2;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Point;
import java.beans.Transient;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import java.util.Vector;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import javax.accessibility.Accessible;

public class JList<E> extends JComponent implements Scrollable, Accessible
{
    private static final String uiClassID = "ListUI";
    public static final int VERTICAL = 0;
    public static final int VERTICAL_WRAP = 1;
    public static final int HORIZONTAL_WRAP = 2;
    private int fixedCellWidth;
    private int fixedCellHeight;
    private int horizontalScrollIncrement;
    private E prototypeCellValue;
    private int visibleRowCount;
    private Color selectionForeground;
    private Color selectionBackground;
    private boolean dragEnabled;
    private ListSelectionModel selectionModel;
    private ListModel<E> dataModel;
    private ListCellRenderer<? super E> cellRenderer;
    private ListSelectionListener selectionListener;
    private int layoutOrientation;
    private DropMode dropMode;
    private transient DropLocation dropLocation;
    
    public JList(final ListModel<E> dataModel) {
        this.fixedCellWidth = -1;
        this.fixedCellHeight = -1;
        this.horizontalScrollIncrement = -1;
        this.visibleRowCount = 8;
        this.dropMode = DropMode.USE_SELECTION;
        if (dataModel == null) {
            throw new IllegalArgumentException("dataModel must be non null");
        }
        ToolTipManager.sharedInstance().registerComponent(this);
        this.layoutOrientation = 0;
        this.dataModel = dataModel;
        this.selectionModel = this.createSelectionModel();
        this.setAutoscrolls(true);
        this.setOpaque(true);
        this.updateUI();
    }
    
    public JList(final E[] array) {
        this(new AbstractListModel<E>() {
            @Override
            public int getSize() {
                return array.length;
            }
            
            @Override
            public E getElementAt(final int n) {
                return array[n];
            }
        });
    }
    
    public JList(final Vector<? extends E> vector) {
        this(new AbstractListModel<E>() {
            @Override
            public int getSize() {
                return vector.size();
            }
            
            @Override
            public E getElementAt(final int n) {
                return vector.elementAt(n);
            }
        });
    }
    
    public JList() {
        this(new AbstractListModel<E>() {
            @Override
            public int getSize() {
                return 0;
            }
            
            @Override
            public E getElementAt(final int n) {
                throw new IndexOutOfBoundsException("No Data Model");
            }
        });
    }
    
    public ListUI getUI() {
        return (ListUI)this.ui;
    }
    
    public void setUI(final ListUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ListUI)UIManager.getUI(this));
        final ListCellRenderer<? super E> cellRenderer = this.getCellRenderer();
        if (cellRenderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component)cellRenderer);
        }
    }
    
    @Override
    public String getUIClassID() {
        return "ListUI";
    }
    
    private void updateFixedCellSize() {
        final ListCellRenderer<? super E> cellRenderer = this.getCellRenderer();
        final Object prototypeCellValue = this.getPrototypeCellValue();
        if (cellRenderer != null && prototypeCellValue != null) {
            final Component listCellRendererComponent = cellRenderer.getListCellRendererComponent(this, prototypeCellValue, 0, false, false);
            final Font font = listCellRendererComponent.getFont();
            listCellRendererComponent.setFont(this.getFont());
            final Dimension preferredSize = listCellRendererComponent.getPreferredSize();
            this.fixedCellWidth = preferredSize.width;
            this.fixedCellHeight = preferredSize.height;
            listCellRendererComponent.setFont(font);
        }
    }
    
    public E getPrototypeCellValue() {
        return this.prototypeCellValue;
    }
    
    public void setPrototypeCellValue(final E prototypeCellValue) {
        final E prototypeCellValue2 = this.prototypeCellValue;
        this.prototypeCellValue = prototypeCellValue;
        if (prototypeCellValue != null && !prototypeCellValue.equals(prototypeCellValue2)) {
            this.updateFixedCellSize();
        }
        this.firePropertyChange("prototypeCellValue", prototypeCellValue2, prototypeCellValue);
    }
    
    public int getFixedCellWidth() {
        return this.fixedCellWidth;
    }
    
    public void setFixedCellWidth(final int fixedCellWidth) {
        this.firePropertyChange("fixedCellWidth", this.fixedCellWidth, this.fixedCellWidth = fixedCellWidth);
    }
    
    public int getFixedCellHeight() {
        return this.fixedCellHeight;
    }
    
    public void setFixedCellHeight(final int fixedCellHeight) {
        this.firePropertyChange("fixedCellHeight", this.fixedCellHeight, this.fixedCellHeight = fixedCellHeight);
    }
    
    @Transient
    public ListCellRenderer<? super E> getCellRenderer() {
        return this.cellRenderer;
    }
    
    public void setCellRenderer(final ListCellRenderer<? super E> cellRenderer) {
        final ListCellRenderer<? super E> cellRenderer2 = this.cellRenderer;
        this.cellRenderer = cellRenderer;
        if (cellRenderer != null && !cellRenderer.equals(cellRenderer2)) {
            this.updateFixedCellSize();
        }
        this.firePropertyChange("cellRenderer", cellRenderer2, cellRenderer);
    }
    
    public Color getSelectionForeground() {
        return this.selectionForeground;
    }
    
    public void setSelectionForeground(final Color selectionForeground) {
        this.firePropertyChange("selectionForeground", this.selectionForeground, this.selectionForeground = selectionForeground);
    }
    
    public Color getSelectionBackground() {
        return this.selectionBackground;
    }
    
    public void setSelectionBackground(final Color selectionBackground) {
        this.firePropertyChange("selectionBackground", this.selectionBackground, this.selectionBackground = selectionBackground);
    }
    
    public int getVisibleRowCount() {
        return this.visibleRowCount;
    }
    
    public void setVisibleRowCount(final int n) {
        final int visibleRowCount = this.visibleRowCount;
        this.visibleRowCount = Math.max(0, n);
        this.firePropertyChange("visibleRowCount", visibleRowCount, n);
    }
    
    public int getLayoutOrientation() {
        return this.layoutOrientation;
    }
    
    public void setLayoutOrientation(final int layoutOrientation) {
        final int layoutOrientation2 = this.layoutOrientation;
        switch (layoutOrientation) {
            case 0:
            case 1:
            case 2: {
                this.firePropertyChange("layoutOrientation", layoutOrientation2, this.layoutOrientation = layoutOrientation);
                return;
            }
            default: {
                throw new IllegalArgumentException("layoutOrientation must be one of: VERTICAL, HORIZONTAL_WRAP or VERTICAL_WRAP");
            }
        }
    }
    
    public int getFirstVisibleIndex() {
        final Rectangle visibleRect = this.getVisibleRect();
        int n;
        if (this.getComponentOrientation().isLeftToRight()) {
            n = this.locationToIndex(visibleRect.getLocation());
        }
        else {
            n = this.locationToIndex(new Point(visibleRect.x + visibleRect.width - 1, visibleRect.y));
        }
        if (n != -1) {
            final Rectangle cellBounds = this.getCellBounds(n, n);
            if (cellBounds != null) {
                SwingUtilities.computeIntersection(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height, cellBounds);
                if (cellBounds.width == 0 || cellBounds.height == 0) {
                    n = -1;
                }
            }
        }
        return n;
    }
    
    public int getLastVisibleIndex() {
        final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
        final Rectangle visibleRect = this.getVisibleRect();
        Point point;
        if (leftToRight) {
            point = new Point(visibleRect.x + visibleRect.width - 1, visibleRect.y + visibleRect.height - 1);
        }
        else {
            point = new Point(visibleRect.x, visibleRect.y + visibleRect.height - 1);
        }
        int locationToIndex = this.locationToIndex(point);
        if (locationToIndex != -1) {
            final Rectangle cellBounds = this.getCellBounds(locationToIndex, locationToIndex);
            if (cellBounds != null) {
                SwingUtilities.computeIntersection(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height, cellBounds);
                if (cellBounds.width == 0 || cellBounds.height == 0) {
                    final boolean b = this.getLayoutOrientation() == 2;
                    final Point point2 = b ? new Point(point.x, visibleRect.y) : new Point(visibleRect.x, point.y);
                    int locationToIndex2 = -1;
                    final int n = locationToIndex;
                    locationToIndex = -1;
                    int n2;
                    do {
                        n2 = locationToIndex2;
                        locationToIndex2 = this.locationToIndex(point2);
                        if (locationToIndex2 != -1) {
                            final Rectangle cellBounds2 = this.getCellBounds(locationToIndex2, locationToIndex2);
                            if (locationToIndex2 != n && cellBounds2 != null && cellBounds2.contains(point2)) {
                                locationToIndex = locationToIndex2;
                                if (b) {
                                    point2.y = cellBounds2.y + cellBounds2.height;
                                    if (point2.y < point.y) {
                                        continue;
                                    }
                                    n2 = locationToIndex2;
                                }
                                else {
                                    point2.x = cellBounds2.x + cellBounds2.width;
                                    if (point2.x < point.x) {
                                        continue;
                                    }
                                    n2 = locationToIndex2;
                                }
                            }
                            else {
                                n2 = locationToIndex2;
                            }
                        }
                    } while (locationToIndex2 != -1 && n2 != locationToIndex2);
                }
            }
        }
        return locationToIndex;
    }
    
    public void ensureIndexIsVisible(final int n) {
        final Rectangle cellBounds = this.getCellBounds(n, n);
        if (cellBounds != null) {
            this.scrollRectToVisible(cellBounds);
        }
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
                case ON_OR_INSERT: {
                    this.dropMode = dropMode;
                    return;
                }
            }
        }
        throw new IllegalArgumentException(dropMode + ": Unsupported drop mode for list");
    }
    
    public final DropMode getDropMode() {
        return this.dropMode;
    }
    
    @Override
    DropLocation dropLocationForPoint(final Point point) {
        DropLocation dropLocation = null;
        Rectangle cellBounds = null;
        int locationToIndex = this.locationToIndex(point);
        if (locationToIndex != -1) {
            cellBounds = this.getCellBounds(locationToIndex, locationToIndex);
        }
        switch (this.dropMode) {
            case USE_SELECTION:
            case ON: {
                dropLocation = new DropLocation(point, (cellBounds != null && cellBounds.contains(point)) ? locationToIndex : -1, false);
                break;
            }
            case INSERT: {
                if (locationToIndex == -1) {
                    dropLocation = new DropLocation(point, this.getModel().getSize(), true);
                    break;
                }
                if (this.layoutOrientation == 2) {
                    if (SwingUtilities2.liesInHorizontal(cellBounds, point, this.getComponentOrientation().isLeftToRight(), false) == SwingUtilities2.Section.TRAILING) {
                        ++locationToIndex;
                    }
                    else if (locationToIndex == this.getModel().getSize() - 1 && point.y >= cellBounds.y + cellBounds.height) {
                        ++locationToIndex;
                    }
                }
                else if (SwingUtilities2.liesInVertical(cellBounds, point, false) == SwingUtilities2.Section.TRAILING) {
                    ++locationToIndex;
                }
                dropLocation = new DropLocation(point, locationToIndex, true);
                break;
            }
            case ON_OR_INSERT: {
                if (locationToIndex == -1) {
                    dropLocation = new DropLocation(point, this.getModel().getSize(), true);
                    break;
                }
                boolean b = false;
                if (this.layoutOrientation == 2) {
                    final SwingUtilities2.Section liesInHorizontal = SwingUtilities2.liesInHorizontal(cellBounds, point, this.getComponentOrientation().isLeftToRight(), true);
                    if (liesInHorizontal == SwingUtilities2.Section.TRAILING) {
                        ++locationToIndex;
                        b = true;
                    }
                    else if (locationToIndex == this.getModel().getSize() - 1 && point.y >= cellBounds.y + cellBounds.height) {
                        ++locationToIndex;
                        b = true;
                    }
                    else if (liesInHorizontal == SwingUtilities2.Section.LEADING) {
                        b = true;
                    }
                }
                else {
                    final SwingUtilities2.Section liesInVertical = SwingUtilities2.liesInVertical(cellBounds, point, true);
                    if (liesInVertical == SwingUtilities2.Section.LEADING) {
                        b = true;
                    }
                    else if (liesInVertical == SwingUtilities2.Section.TRAILING) {
                        ++locationToIndex;
                        b = true;
                    }
                }
                dropLocation = new DropLocation(point, locationToIndex, b);
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
                    this.setSelectedIndices(((int[][])o)[0]);
                    SwingUtilities2.setLeadAnchorWithoutSelection(this.getSelectionModel(), ((int[][])o)[1][1], ((int[][])o)[1][0]);
                }
            }
            else {
                if (this.dropLocation == null) {
                    o2 = new int[][] { this.getSelectedIndices(), { this.getAnchorSelectionIndex(), this.getLeadSelectionIndex() } };
                }
                else {
                    o2 = o;
                }
                final int index = dropLocation2.getIndex();
                if (index == -1) {
                    this.clearSelection();
                    this.getSelectionModel().setAnchorSelectionIndex(-1);
                    this.getSelectionModel().setLeadSelectionIndex(-1);
                }
                else {
                    this.setSelectionInterval(index, index);
                }
            }
        }
        this.firePropertyChange("dropLocation", this.dropLocation, this.dropLocation = dropLocation2);
        return o2;
    }
    
    public final DropLocation getDropLocation() {
        return this.dropLocation;
    }
    
    public int getNextMatch(String upperCase, final int n, final Position.Bias bias) {
        final ListModel<E> model = this.getModel();
        final int size = model.getSize();
        if (upperCase == null) {
            throw new IllegalArgumentException();
        }
        if (n < 0 || n >= size) {
            throw new IllegalArgumentException();
        }
        upperCase = upperCase.toUpperCase();
        final int n2 = (bias == Position.Bias.Forward) ? 1 : -1;
        int i = n;
        do {
            final Object element = model.getElementAt(i);
            if (element != null) {
                String s;
                if (element instanceof String) {
                    s = ((String)element).toUpperCase();
                }
                else {
                    s = element.toString();
                    if (s != null) {
                        s = s.toUpperCase();
                    }
                }
                if (s != null && s.startsWith(upperCase)) {
                    return i;
                }
            }
            i = (i + n2 + size) % size;
        } while (i != n);
        return -1;
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        if (mouseEvent != null) {
            final Point point = mouseEvent.getPoint();
            final int locationToIndex = this.locationToIndex(point);
            final ListCellRenderer<? super E> cellRenderer = this.getCellRenderer();
            final Rectangle cellBounds;
            if (locationToIndex != -1 && cellRenderer != null && (cellBounds = this.getCellBounds(locationToIndex, locationToIndex)) != null && cellBounds.contains(point.x, point.y)) {
                final ListSelectionModel selectionModel = this.getSelectionModel();
                final Component listCellRendererComponent = cellRenderer.getListCellRendererComponent((JList<? extends E>)this, this.getModel().getElementAt(locationToIndex), locationToIndex, selectionModel.isSelectedIndex(locationToIndex), this.hasFocus() && selectionModel.getLeadSelectionIndex() == locationToIndex);
                if (listCellRendererComponent instanceof JComponent) {
                    point.translate(-cellBounds.x, -cellBounds.y);
                    final MouseEvent mouseEvent2 = new MouseEvent(listCellRendererComponent, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                    final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                    mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                    final String toolTipText = ((JComponent)listCellRendererComponent).getToolTipText(mouseEvent2);
                    if (toolTipText != null) {
                        return toolTipText;
                    }
                }
            }
        }
        return super.getToolTipText();
    }
    
    public int locationToIndex(final Point point) {
        final ListUI ui = this.getUI();
        return (ui != null) ? ui.locationToIndex(this, point) : -1;
    }
    
    public Point indexToLocation(final int n) {
        final ListUI ui = this.getUI();
        return (ui != null) ? ui.indexToLocation(this, n) : null;
    }
    
    public Rectangle getCellBounds(final int n, final int n2) {
        final ListUI ui = this.getUI();
        return (ui != null) ? ui.getCellBounds(this, n, n2) : null;
    }
    
    public ListModel<E> getModel() {
        return this.dataModel;
    }
    
    public void setModel(final ListModel<E> dataModel) {
        if (dataModel == null) {
            throw new IllegalArgumentException("model must be non null");
        }
        this.firePropertyChange("model", this.dataModel, this.dataModel = dataModel);
        this.clearSelection();
    }
    
    public void setListData(final E[] array) {
        this.setModel(new AbstractListModel<E>() {
            @Override
            public int getSize() {
                return array.length;
            }
            
            @Override
            public E getElementAt(final int n) {
                return array[n];
            }
        });
    }
    
    public void setListData(final Vector<? extends E> vector) {
        this.setModel(new AbstractListModel<E>() {
            @Override
            public int getSize() {
                return vector.size();
            }
            
            @Override
            public E getElementAt(final int n) {
                return vector.elementAt(n);
            }
        });
    }
    
    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }
    
    public ListSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    protected void fireSelectionValueChanged(final int n, final int n2, final boolean b) {
        final Object[] listenerList = this.listenerList.getListenerList();
        ListSelectionEvent listSelectionEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ListSelectionListener.class) {
                if (listSelectionEvent == null) {
                    listSelectionEvent = new ListSelectionEvent(this, n, n2, b);
                }
                ((ListSelectionListener)listenerList[i + 1]).valueChanged(listSelectionEvent);
            }
        }
    }
    
    public void addListSelectionListener(final ListSelectionListener listSelectionListener) {
        if (this.selectionListener == null) {
            this.selectionListener = new ListSelectionHandler();
            this.getSelectionModel().addListSelectionListener(this.selectionListener);
        }
        this.listenerList.add(ListSelectionListener.class, listSelectionListener);
    }
    
    public void removeListSelectionListener(final ListSelectionListener listSelectionListener) {
        this.listenerList.remove(ListSelectionListener.class, listSelectionListener);
    }
    
    public ListSelectionListener[] getListSelectionListeners() {
        return this.listenerList.getListeners(ListSelectionListener.class);
    }
    
    public void setSelectionModel(final ListSelectionModel selectionModel) {
        if (selectionModel == null) {
            throw new IllegalArgumentException("selectionModel must be non null");
        }
        if (this.selectionListener != null) {
            this.selectionModel.removeListSelectionListener(this.selectionListener);
            selectionModel.addListSelectionListener(this.selectionListener);
        }
        this.firePropertyChange("selectionModel", this.selectionModel, this.selectionModel = selectionModel);
    }
    
    public void setSelectionMode(final int selectionMode) {
        this.getSelectionModel().setSelectionMode(selectionMode);
    }
    
    public int getSelectionMode() {
        return this.getSelectionModel().getSelectionMode();
    }
    
    public int getAnchorSelectionIndex() {
        return this.getSelectionModel().getAnchorSelectionIndex();
    }
    
    public int getLeadSelectionIndex() {
        return this.getSelectionModel().getLeadSelectionIndex();
    }
    
    public int getMinSelectionIndex() {
        return this.getSelectionModel().getMinSelectionIndex();
    }
    
    public int getMaxSelectionIndex() {
        return this.getSelectionModel().getMaxSelectionIndex();
    }
    
    public boolean isSelectedIndex(final int n) {
        return this.getSelectionModel().isSelectedIndex(n);
    }
    
    public boolean isSelectionEmpty() {
        return this.getSelectionModel().isSelectionEmpty();
    }
    
    public void clearSelection() {
        this.getSelectionModel().clearSelection();
    }
    
    public void setSelectionInterval(final int n, final int n2) {
        this.getSelectionModel().setSelectionInterval(n, n2);
    }
    
    public void addSelectionInterval(final int n, final int n2) {
        this.getSelectionModel().addSelectionInterval(n, n2);
    }
    
    public void removeSelectionInterval(final int n, final int n2) {
        this.getSelectionModel().removeSelectionInterval(n, n2);
    }
    
    public void setValueIsAdjusting(final boolean valueIsAdjusting) {
        this.getSelectionModel().setValueIsAdjusting(valueIsAdjusting);
    }
    
    public boolean getValueIsAdjusting() {
        return this.getSelectionModel().getValueIsAdjusting();
    }
    
    @Transient
    public int[] getSelectedIndices() {
        final ListSelectionModel selectionModel = this.getSelectionModel();
        final int minSelectionIndex = selectionModel.getMinSelectionIndex();
        final int maxSelectionIndex = selectionModel.getMaxSelectionIndex();
        if (minSelectionIndex < 0 || maxSelectionIndex < 0) {
            return new int[0];
        }
        final int[] array = new int[1 + (maxSelectionIndex - minSelectionIndex)];
        int n = 0;
        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
            if (selectionModel.isSelectedIndex(i)) {
                array[n++] = i;
            }
        }
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public void setSelectedIndex(final int n) {
        if (n >= this.getModel().getSize()) {
            return;
        }
        this.getSelectionModel().setSelectionInterval(n, n);
    }
    
    public void setSelectedIndices(final int[] array) {
        final ListSelectionModel selectionModel = this.getSelectionModel();
        selectionModel.clearSelection();
        final int size = this.getModel().getSize();
        for (final int n : array) {
            if (n < size) {
                selectionModel.addSelectionInterval(n, n);
            }
        }
    }
    
    @Deprecated
    public Object[] getSelectedValues() {
        final ListSelectionModel selectionModel = this.getSelectionModel();
        final ListModel<E> model = this.getModel();
        final int minSelectionIndex = selectionModel.getMinSelectionIndex();
        final int maxSelectionIndex = selectionModel.getMaxSelectionIndex();
        if (minSelectionIndex < 0 || maxSelectionIndex < 0) {
            return new Object[0];
        }
        final Object[] array = new Object[1 + (maxSelectionIndex - minSelectionIndex)];
        int n = 0;
        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
            if (selectionModel.isSelectedIndex(i)) {
                array[n++] = model.getElementAt(i);
            }
        }
        final Object[] array2 = new Object[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public List<E> getSelectedValuesList() {
        final ListSelectionModel selectionModel = this.getSelectionModel();
        final ListModel<E> model = this.getModel();
        final int minSelectionIndex = selectionModel.getMinSelectionIndex();
        final int maxSelectionIndex = selectionModel.getMaxSelectionIndex();
        if (minSelectionIndex < 0 || maxSelectionIndex < 0) {
            return Collections.emptyList();
        }
        final ArrayList list = new ArrayList();
        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
            if (selectionModel.isSelectedIndex(i)) {
                list.add(model.getElementAt(i));
            }
        }
        return list;
    }
    
    public int getSelectedIndex() {
        return this.getMinSelectionIndex();
    }
    
    public E getSelectedValue() {
        final int minSelectionIndex = this.getMinSelectionIndex();
        return (minSelectionIndex == -1) ? null : this.getModel().getElementAt(minSelectionIndex);
    }
    
    public void setSelectedValue(final Object o, final boolean b) {
        if (o == null) {
            this.setSelectedIndex(-1);
        }
        else if (!o.equals(this.getSelectedValue())) {
            final ListModel<E> model = this.getModel();
            for (int i = 0; i < model.getSize(); ++i) {
                if (o.equals(model.getElementAt(i))) {
                    this.setSelectedIndex(i);
                    if (b) {
                        this.ensureIndexIsVisible(i);
                    }
                    this.repaint();
                    return;
                }
            }
            this.setSelectedIndex(-1);
        }
        this.repaint();
    }
    
    private void checkScrollableParameters(final Rectangle rectangle, final int n) {
        if (rectangle == null) {
            throw new IllegalArgumentException("visibleRect must be non-null");
        }
        switch (n) {
            case 0:
            case 1: {
                return;
            }
            default: {
                throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
            }
        }
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        if (this.getLayoutOrientation() != 0) {
            return this.getPreferredSize();
        }
        final Insets insets = this.getInsets();
        final int n = insets.left + insets.right;
        final int n2 = insets.top + insets.bottom;
        final int visibleRowCount = this.getVisibleRowCount();
        final int fixedCellWidth = this.getFixedCellWidth();
        final int fixedCellHeight = this.getFixedCellHeight();
        if (fixedCellWidth > 0 && fixedCellHeight > 0) {
            return new Dimension(fixedCellWidth + n, visibleRowCount * fixedCellHeight + n2);
        }
        if (this.getModel().getSize() > 0) {
            final int width = this.getPreferredSize().width;
            final Rectangle cellBounds = this.getCellBounds(0, 0);
            int n3;
            if (cellBounds != null) {
                n3 = visibleRowCount * cellBounds.height + n2;
            }
            else {
                n3 = 1;
            }
            return new Dimension(width, n3);
        }
        return new Dimension((fixedCellWidth > 0) ? fixedCellWidth : 256, ((fixedCellHeight > 0) ? fixedCellHeight : 16) * visibleRowCount);
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int n, final int n2) {
        this.checkScrollableParameters(rectangle, n);
        if (n != 1) {
            if (n == 0 && this.getLayoutOrientation() != 0) {
                final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
                Point location;
                if (leftToRight) {
                    location = rectangle.getLocation();
                }
                else {
                    location = new Point(rectangle.x + rectangle.width - 1, rectangle.y);
                }
                final int locationToIndex = this.locationToIndex(location);
                if (locationToIndex != -1) {
                    final Rectangle cellBounds = this.getCellBounds(locationToIndex, locationToIndex);
                    if (cellBounds != null && cellBounds.contains(location)) {
                        int x;
                        int x2;
                        if (leftToRight) {
                            x = rectangle.x;
                            x2 = cellBounds.x;
                        }
                        else {
                            x = rectangle.x + rectangle.width;
                            x2 = cellBounds.x + cellBounds.width;
                        }
                        if (x2 == x) {
                            return cellBounds.width;
                        }
                        if (n2 < 0) {
                            return Math.abs(x - x2);
                        }
                        if (leftToRight) {
                            return x2 + cellBounds.width - x;
                        }
                        return x - cellBounds.x;
                    }
                }
            }
            final Font font = this.getFont();
            return (font != null) ? font.getSize() : 1;
        }
        final int locationToIndex2 = this.locationToIndex(rectangle.getLocation());
        if (locationToIndex2 == -1) {
            return 0;
        }
        if (n2 > 0) {
            final Rectangle cellBounds2 = this.getCellBounds(locationToIndex2, locationToIndex2);
            return (cellBounds2 == null) ? 0 : (cellBounds2.height - (rectangle.y - cellBounds2.y));
        }
        final Rectangle cellBounds3 = this.getCellBounds(locationToIndex2, locationToIndex2);
        if (cellBounds3.y == rectangle.y && locationToIndex2 == 0) {
            return 0;
        }
        if (cellBounds3.y != rectangle.y) {
            return rectangle.y - cellBounds3.y;
        }
        final Point location2;
        final Point point = location2 = cellBounds3.getLocation();
        --location2.y;
        final int locationToIndex3 = this.locationToIndex(point);
        final Rectangle cellBounds4 = this.getCellBounds(locationToIndex3, locationToIndex3);
        if (cellBounds4 == null || cellBounds4.y >= cellBounds3.y) {
            return 0;
        }
        return cellBounds4.height;
    }
    
    @Override
    public int getScrollableBlockIncrement(final Rectangle rectangle, final int n, final int n2) {
        this.checkScrollableParameters(rectangle, n);
        if (n == 1) {
            int n3 = rectangle.height;
            if (n2 > 0) {
                final int locationToIndex = this.locationToIndex(new Point(rectangle.x, rectangle.y + rectangle.height - 1));
                if (locationToIndex != -1) {
                    final Rectangle cellBounds = this.getCellBounds(locationToIndex, locationToIndex);
                    if (cellBounds != null) {
                        n3 = cellBounds.y - rectangle.y;
                        if (n3 == 0 && locationToIndex < this.getModel().getSize() - 1) {
                            n3 = cellBounds.height;
                        }
                    }
                }
            }
            else {
                int locationToIndex2 = this.locationToIndex(new Point(rectangle.x, rectangle.y - rectangle.height));
                int n4 = this.getFirstVisibleIndex();
                if (locationToIndex2 != -1) {
                    if (n4 == -1) {
                        n4 = this.locationToIndex(rectangle.getLocation());
                    }
                    Rectangle rectangle2 = this.getCellBounds(locationToIndex2, locationToIndex2);
                    final Rectangle cellBounds2 = this.getCellBounds(n4, n4);
                    if (rectangle2 != null && cellBounds2 != null) {
                        while (rectangle2.y + rectangle.height < cellBounds2.y + cellBounds2.height && rectangle2.y < cellBounds2.y) {
                            ++locationToIndex2;
                            rectangle2 = this.getCellBounds(locationToIndex2, locationToIndex2);
                        }
                        n3 = rectangle.y - rectangle2.y;
                        if (n3 <= 0 && rectangle2.y > 0) {
                            --locationToIndex2;
                            final Rectangle cellBounds3 = this.getCellBounds(locationToIndex2, locationToIndex2);
                            if (cellBounds3 != null) {
                                n3 = rectangle.y - cellBounds3.y;
                            }
                        }
                    }
                }
            }
            return n3;
        }
        if (n == 0 && this.getLayoutOrientation() != 0) {
            final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
            int n5 = rectangle.width;
            if (n2 > 0) {
                final int locationToIndex3 = this.locationToIndex(new Point(rectangle.x + (leftToRight ? (rectangle.width - 1) : 0), rectangle.y));
                if (locationToIndex3 != -1) {
                    final Rectangle cellBounds4 = this.getCellBounds(locationToIndex3, locationToIndex3);
                    if (cellBounds4 != null) {
                        if (leftToRight) {
                            n5 = cellBounds4.x - rectangle.x;
                        }
                        else {
                            n5 = rectangle.x + rectangle.width - (cellBounds4.x + cellBounds4.width);
                        }
                        if (n5 < 0) {
                            n5 += cellBounds4.width;
                        }
                        else if (n5 == 0 && locationToIndex3 < this.getModel().getSize() - 1) {
                            n5 = cellBounds4.width;
                        }
                    }
                }
            }
            else {
                final int locationToIndex4 = this.locationToIndex(new Point(rectangle.x + (leftToRight ? (-rectangle.width) : (rectangle.width - 1 + rectangle.width)), rectangle.y));
                if (locationToIndex4 != -1) {
                    final Rectangle cellBounds5 = this.getCellBounds(locationToIndex4, locationToIndex4);
                    if (cellBounds5 != null) {
                        final int n6 = cellBounds5.x + cellBounds5.width;
                        if (leftToRight) {
                            if (cellBounds5.x < rectangle.x - rectangle.width && n6 < rectangle.x) {
                                n5 = rectangle.x - n6;
                            }
                            else {
                                n5 = rectangle.x - cellBounds5.x;
                            }
                        }
                        else {
                            final int n7 = rectangle.x + rectangle.width;
                            if (n6 > n7 + rectangle.width && cellBounds5.x > n7) {
                                n5 = cellBounds5.x - n7;
                            }
                            else {
                                n5 = n6 - n7;
                            }
                        }
                    }
                }
            }
            return n5;
        }
        return rectangle.width;
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (this.getLayoutOrientation() == 2 && this.getVisibleRowCount() <= 0) {
            return true;
        }
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return unwrappedParent instanceof JViewport && unwrappedParent.getWidth() > this.getPreferredSize().width;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (this.getLayoutOrientation() == 1 && this.getVisibleRowCount() <= 0) {
            return true;
        }
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return unwrappedParent instanceof JViewport && unwrappedParent.getHeight() > this.getPreferredSize().height;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ListUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",fixedCellHeight=" + this.fixedCellHeight + ",fixedCellWidth=" + this.fixedCellWidth + ",horizontalScrollIncrement=" + this.horizontalScrollIncrement + ",selectionBackground=" + ((this.selectionBackground != null) ? this.selectionBackground.toString() : "") + ",selectionForeground=" + ((this.selectionForeground != null) ? this.selectionForeground.toString() : "") + ",visibleRowCount=" + this.visibleRowCount + ",layoutOrientation=" + this.layoutOrientation;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJList();
        }
        return this.accessibleContext;
    }
    
    public static final class DropLocation extends TransferHandler.DropLocation
    {
        private final int index;
        private final boolean isInsert;
        
        private DropLocation(final Point point, final int index, final boolean isInsert) {
            super(point);
            this.index = index;
            this.isInsert = isInsert;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public boolean isInsert() {
            return this.isInsert;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",index=" + this.index + ",insert=" + this.isInsert + "]";
        }
    }
    
    private class ListSelectionHandler implements ListSelectionListener, Serializable
    {
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            JList.this.fireSelectionValueChanged(listSelectionEvent.getFirstIndex(), listSelectionEvent.getLastIndex(), listSelectionEvent.getValueIsAdjusting());
        }
    }
    
    protected class AccessibleJList extends AccessibleJComponent implements AccessibleSelection, PropertyChangeListener, ListSelectionListener, ListDataListener
    {
        int leadSelectionIndex;
        
        public AccessibleJList() {
            JList.this.addPropertyChangeListener(this);
            JList.this.getSelectionModel().addListSelectionListener(this);
            JList.this.getModel().addListDataListener(this);
            this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final Object oldValue = propertyChangeEvent.getOldValue();
            final Object newValue = propertyChangeEvent.getNewValue();
            if (propertyName.compareTo("model") == 0) {
                if (oldValue != null && oldValue instanceof ListModel) {
                    ((ListModel)oldValue).removeListDataListener(this);
                }
                if (newValue != null && newValue instanceof ListModel) {
                    ((ListModel)newValue).addListDataListener(this);
                }
            }
            else if (propertyName.compareTo("selectionModel") == 0) {
                if (oldValue != null && oldValue instanceof ListSelectionModel) {
                    ((ListSelectionModel)oldValue).removeListSelectionListener(this);
                }
                if (newValue != null && newValue instanceof ListSelectionModel) {
                    ((ListSelectionModel)newValue).addListSelectionListener(this);
                }
                this.firePropertyChange("AccessibleSelection", false, true);
            }
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            final int leadSelectionIndex = this.leadSelectionIndex;
            this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
            if (leadSelectionIndex != this.leadSelectionIndex) {
                this.firePropertyChange("AccessibleActiveDescendant", (leadSelectionIndex >= 0) ? this.getAccessibleChild(leadSelectionIndex) : null, (this.leadSelectionIndex >= 0) ? this.getAccessibleChild(this.leadSelectionIndex) : null);
            }
            this.firePropertyChange("AccessibleVisibleData", false, true);
            this.firePropertyChange("AccessibleSelection", false, true);
            final AccessibleStateSet accessibleStateSet = this.getAccessibleStateSet();
            if (JList.this.getSelectionModel().getSelectionMode() != 0) {
                if (!accessibleStateSet.contains(AccessibleState.MULTISELECTABLE)) {
                    accessibleStateSet.add(AccessibleState.MULTISELECTABLE);
                    this.firePropertyChange("AccessibleState", null, AccessibleState.MULTISELECTABLE);
                }
            }
            else if (accessibleStateSet.contains(AccessibleState.MULTISELECTABLE)) {
                accessibleStateSet.remove(AccessibleState.MULTISELECTABLE);
                this.firePropertyChange("AccessibleState", AccessibleState.MULTISELECTABLE, null);
            }
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            this.firePropertyChange("AccessibleVisibleData", false, true);
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            this.firePropertyChange("AccessibleVisibleData", false, true);
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            this.firePropertyChange("AccessibleVisibleData", false, true);
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JList.this.selectionModel.getSelectionMode() != 0) {
                accessibleStateSet.add(AccessibleState.MULTISELECTABLE);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LIST;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            final int locationToIndex = JList.this.locationToIndex(point);
            if (locationToIndex >= 0) {
                return new ActionableAccessibleJListChild(JList.this, locationToIndex);
            }
            return null;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return JList.this.getModel().getSize();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (n >= JList.this.getModel().getSize()) {
                return null;
            }
            return new ActionableAccessibleJListChild(JList.this, n);
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            return JList.this.getSelectedIndices().length;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            final int accessibleSelectionCount = this.getAccessibleSelectionCount();
            if (n < 0 || n >= accessibleSelectionCount) {
                return null;
            }
            return this.getAccessibleChild(JList.this.getSelectedIndices()[n]);
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return JList.this.isSelectedIndex(n);
        }
        
        @Override
        public void addAccessibleSelection(final int n) {
            JList.this.addSelectionInterval(n, n);
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            JList.this.removeSelectionInterval(n, n);
        }
        
        @Override
        public void clearAccessibleSelection() {
            JList.this.clearSelection();
        }
        
        @Override
        public void selectAllAccessibleSelection() {
            JList.this.addSelectionInterval(0, this.getAccessibleChildrenCount() - 1);
        }
        
        protected class AccessibleJListChild extends AccessibleContext implements Accessible, AccessibleComponent
        {
            private JList<E> parent;
            int indexInParent;
            private Component component;
            private AccessibleContext accessibleContext;
            private ListModel<E> listModel;
            private ListCellRenderer<? super E> cellRenderer;
            
            public AccessibleJListChild(final JList<E> parent, final int indexInParent) {
                this.parent = null;
                this.component = null;
                this.accessibleContext = null;
                this.cellRenderer = null;
                this.setAccessibleParent(this.parent = parent);
                this.indexInParent = indexInParent;
                if (parent != null) {
                    this.listModel = parent.getModel();
                    this.cellRenderer = parent.getCellRenderer();
                }
            }
            
            private Component getCurrentComponent() {
                return this.getComponentAtIndex(this.indexInParent);
            }
            
            AccessibleContext getCurrentAccessibleContext() {
                final Component componentAtIndex = this.getComponentAtIndex(this.indexInParent);
                if (componentAtIndex instanceof Accessible) {
                    return componentAtIndex.getAccessibleContext();
                }
                return null;
            }
            
            private Component getComponentAtIndex(final int n) {
                if (n < 0 || n >= this.listModel.getSize()) {
                    return null;
                }
                if (this.parent != null && this.listModel != null && this.cellRenderer != null) {
                    return this.cellRenderer.getListCellRendererComponent((JList<? extends E>)this.parent, this.listModel.getElementAt(n), n, this.parent.isSelectedIndex(n), this.parent.isFocusOwner() && n == this.parent.getLeadSelectionIndex());
                }
                return null;
            }
            
            @Override
            public AccessibleContext getAccessibleContext() {
                return this;
            }
            
            @Override
            public String getAccessibleName() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleName();
                }
                return null;
            }
            
            @Override
            public void setAccessibleName(final String accessibleName) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleName(accessibleName);
                }
            }
            
            @Override
            public String getAccessibleDescription() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleDescription();
                }
                return null;
            }
            
            @Override
            public void setAccessibleDescription(final String accessibleDescription) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.setAccessibleDescription(accessibleDescription);
                }
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleRole();
                }
                return null;
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                AccessibleStateSet accessibleStateSet;
                if (currentAccessibleContext != null) {
                    accessibleStateSet = currentAccessibleContext.getAccessibleStateSet();
                }
                else {
                    accessibleStateSet = new AccessibleStateSet();
                }
                accessibleStateSet.add(AccessibleState.SELECTABLE);
                if (this.parent.isFocusOwner() && this.indexInParent == this.parent.getLeadSelectionIndex()) {
                    accessibleStateSet.add(AccessibleState.ACTIVE);
                }
                if (this.parent.isSelectedIndex(this.indexInParent)) {
                    accessibleStateSet.add(AccessibleState.SELECTED);
                }
                if (this.isShowing()) {
                    accessibleStateSet.add(AccessibleState.SHOWING);
                }
                else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
                    accessibleStateSet.remove(AccessibleState.SHOWING);
                }
                if (this.isVisible()) {
                    accessibleStateSet.add(AccessibleState.VISIBLE);
                }
                else if (accessibleStateSet.contains(AccessibleState.VISIBLE)) {
                    accessibleStateSet.remove(AccessibleState.VISIBLE);
                }
                accessibleStateSet.add(AccessibleState.TRANSIENT);
                return accessibleStateSet;
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                return this.indexInParent;
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
            }
            
            @Override
            public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    currentAccessibleContext.removePropertyChangeListener(propertyChangeListener);
                }
            }
            
            @Override
            public AccessibleComponent getAccessibleComponent() {
                return this;
            }
            
            @Override
            public AccessibleSelection getAccessibleSelection() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                return (currentAccessibleContext != null) ? currentAccessibleContext.getAccessibleSelection() : null;
            }
            
            @Override
            public AccessibleText getAccessibleText() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                return (currentAccessibleContext != null) ? currentAccessibleContext.getAccessibleText() : null;
            }
            
            @Override
            public AccessibleValue getAccessibleValue() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                return (currentAccessibleContext != null) ? currentAccessibleContext.getAccessibleValue() : null;
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
                final int firstVisibleIndex = this.parent.getFirstVisibleIndex();
                int lastVisibleIndex = this.parent.getLastVisibleIndex();
                if (lastVisibleIndex == -1) {
                    lastVisibleIndex = this.parent.getModel().getSize() - 1;
                }
                return this.indexInParent >= firstVisibleIndex && this.indexInParent <= lastVisibleIndex;
            }
            
            @Override
            public void setVisible(final boolean b) {
            }
            
            @Override
            public boolean isShowing() {
                return this.parent.isShowing() && this.isVisible();
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
                if (this.parent == null) {
                    return null;
                }
                Point locationOnScreen;
                try {
                    locationOnScreen = this.parent.getLocationOnScreen();
                }
                catch (final IllegalComponentStateException ex) {
                    return null;
                }
                final Point indexToLocation = this.parent.indexToLocation(this.indexInParent);
                if (indexToLocation != null) {
                    indexToLocation.translate(locationOnScreen.x, locationOnScreen.y);
                    return indexToLocation;
                }
                return null;
            }
            
            @Override
            public Point getLocation() {
                if (this.parent != null) {
                    return this.parent.indexToLocation(this.indexInParent);
                }
                return null;
            }
            
            @Override
            public void setLocation(final Point point) {
                if (this.parent != null && this.parent.contains(point)) {
                    JList.this.ensureIndexIsVisible(this.indexInParent);
                }
            }
            
            @Override
            public Rectangle getBounds() {
                if (this.parent != null) {
                    return this.parent.getCellBounds(this.indexInParent, this.indexInParent);
                }
                return null;
            }
            
            @Override
            public void setBounds(final Rectangle bounds) {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    ((AccessibleComponent)currentAccessibleContext).setBounds(bounds);
                }
            }
            
            @Override
            public Dimension getSize() {
                final Rectangle bounds = this.getBounds();
                if (bounds != null) {
                    return bounds.getSize();
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
            
            @Override
            public AccessibleIcon[] getAccessibleIcon() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    return currentAccessibleContext.getAccessibleIcon();
                }
                return null;
            }
        }
        
        private class ActionableAccessibleJListChild extends AccessibleJListChild implements AccessibleAction
        {
            ActionableAccessibleJListChild(final JList<E> list, final int n) {
                list.super(n);
            }
            
            @Override
            public AccessibleAction getAccessibleAction() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext == null) {
                    return null;
                }
                final AccessibleAction accessibleAction = currentAccessibleContext.getAccessibleAction();
                if (accessibleAction != null) {
                    return accessibleAction;
                }
                return this;
            }
            
            @Override
            public boolean doAccessibleAction(final int n) {
                if (n == 0) {
                    JList.this.setSelectedIndex(this.indexInParent);
                    return true;
                }
                return false;
            }
            
            @Override
            public String getAccessibleActionDescription(final int n) {
                if (n == 0) {
                    return UIManager.getString("AbstractButton.clickText");
                }
                return null;
            }
            
            @Override
            public int getAccessibleActionCount() {
                return 1;
            }
        }
    }
}
