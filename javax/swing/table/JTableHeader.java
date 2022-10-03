package javax.swing.table;

import java.awt.event.FocusListener;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Color;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleAction;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.ToolTipManager;
import sun.swing.table.DefaultTableCellHeaderRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import java.awt.Component;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Point;
import java.beans.Transient;
import javax.swing.JTable;
import javax.accessibility.Accessible;
import javax.swing.event.TableColumnModelListener;
import javax.swing.JComponent;

public class JTableHeader extends JComponent implements TableColumnModelListener, Accessible
{
    private static final String uiClassID = "TableHeaderUI";
    protected JTable table;
    protected TableColumnModel columnModel;
    protected boolean reorderingAllowed;
    protected boolean resizingAllowed;
    protected boolean updateTableInRealTime;
    protected transient TableColumn resizingColumn;
    protected transient TableColumn draggedColumn;
    protected transient int draggedDistance;
    private TableCellRenderer defaultRenderer;
    
    public JTableHeader() {
        this(null);
    }
    
    public JTableHeader(TableColumnModel defaultColumnModel) {
        if (defaultColumnModel == null) {
            defaultColumnModel = this.createDefaultColumnModel();
        }
        this.setColumnModel(defaultColumnModel);
        this.initializeLocalVars();
        this.updateUI();
    }
    
    public void setTable(final JTable table) {
        this.firePropertyChange("table", this.table, this.table = table);
    }
    
    public JTable getTable() {
        return this.table;
    }
    
    public void setReorderingAllowed(final boolean reorderingAllowed) {
        this.firePropertyChange("reorderingAllowed", this.reorderingAllowed, this.reorderingAllowed = reorderingAllowed);
    }
    
    public boolean getReorderingAllowed() {
        return this.reorderingAllowed;
    }
    
    public void setResizingAllowed(final boolean resizingAllowed) {
        this.firePropertyChange("resizingAllowed", this.resizingAllowed, this.resizingAllowed = resizingAllowed);
    }
    
    public boolean getResizingAllowed() {
        return this.resizingAllowed;
    }
    
    public TableColumn getDraggedColumn() {
        return this.draggedColumn;
    }
    
    public int getDraggedDistance() {
        return this.draggedDistance;
    }
    
    public TableColumn getResizingColumn() {
        return this.resizingColumn;
    }
    
    public void setUpdateTableInRealTime(final boolean updateTableInRealTime) {
        this.updateTableInRealTime = updateTableInRealTime;
    }
    
    public boolean getUpdateTableInRealTime() {
        return this.updateTableInRealTime;
    }
    
    public void setDefaultRenderer(final TableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }
    
    @Transient
    public TableCellRenderer getDefaultRenderer() {
        return this.defaultRenderer;
    }
    
    public int columnAtPoint(final Point point) {
        int x = point.x;
        if (!this.getComponentOrientation().isLeftToRight()) {
            x = this.getWidthInRightToLeft() - x - 1;
        }
        return this.getColumnModel().getColumnIndexAtX(x);
    }
    
    public Rectangle getHeaderRect(final int n) {
        final Rectangle rectangle = new Rectangle();
        final TableColumnModel columnModel = this.getColumnModel();
        rectangle.height = this.getHeight();
        if (n < 0) {
            if (!this.getComponentOrientation().isLeftToRight()) {
                rectangle.x = this.getWidthInRightToLeft();
            }
        }
        else if (n >= columnModel.getColumnCount()) {
            if (this.getComponentOrientation().isLeftToRight()) {
                rectangle.x = this.getWidth();
            }
        }
        else {
            for (int i = 0; i < n; ++i) {
                final Rectangle rectangle2 = rectangle;
                rectangle2.x += columnModel.getColumn(i).getWidth();
            }
            if (!this.getComponentOrientation().isLeftToRight()) {
                rectangle.x = this.getWidthInRightToLeft() - rectangle.x - columnModel.getColumn(n).getWidth();
            }
            rectangle.width = columnModel.getColumn(n).getWidth();
        }
        return rectangle;
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        String s = null;
        final Point point = mouseEvent.getPoint();
        final int columnAtPoint;
        if ((columnAtPoint = this.columnAtPoint(point)) != -1) {
            final TableColumn column = this.columnModel.getColumn(columnAtPoint);
            TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
            if (tableCellRenderer == null) {
                tableCellRenderer = this.defaultRenderer;
            }
            final Component tableCellRendererComponent = tableCellRenderer.getTableCellRendererComponent(this.getTable(), column.getHeaderValue(), false, false, -1, columnAtPoint);
            if (tableCellRendererComponent instanceof JComponent) {
                final Rectangle headerRect = this.getHeaderRect(columnAtPoint);
                point.translate(-headerRect.x, -headerRect.y);
                final MouseEvent mouseEvent2 = new MouseEvent(tableCellRendererComponent, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                s = ((JComponent)tableCellRendererComponent).getToolTipText(mouseEvent2);
            }
        }
        if (s == null) {
            s = this.getToolTipText();
        }
        return s;
    }
    
    public TableHeaderUI getUI() {
        return (TableHeaderUI)this.ui;
    }
    
    public void setUI(final TableHeaderUI ui) {
        if (this.ui != ui) {
            super.setUI(ui);
            this.repaint();
        }
    }
    
    @Override
    public void updateUI() {
        this.setUI((TableHeaderUI)UIManager.getUI(this));
        final TableCellRenderer defaultRenderer = this.getDefaultRenderer();
        if (defaultRenderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component)defaultRenderer);
        }
    }
    
    @Override
    public String getUIClassID() {
        return "TableHeaderUI";
    }
    
    public void setColumnModel(final TableColumnModel columnModel) {
        if (columnModel == null) {
            throw new IllegalArgumentException("Cannot set a null ColumnModel");
        }
        final TableColumnModel columnModel2 = this.columnModel;
        if (columnModel != columnModel2) {
            if (columnModel2 != null) {
                columnModel2.removeColumnModelListener(this);
            }
            (this.columnModel = columnModel).addColumnModelListener(this);
            this.firePropertyChange("columnModel", columnModel2, columnModel);
            this.resizeAndRepaint();
        }
    }
    
    public TableColumnModel getColumnModel() {
        return this.columnModel;
    }
    
    @Override
    public void columnAdded(final TableColumnModelEvent tableColumnModelEvent) {
        this.resizeAndRepaint();
    }
    
    @Override
    public void columnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
        this.resizeAndRepaint();
    }
    
    @Override
    public void columnMoved(final TableColumnModelEvent tableColumnModelEvent) {
        this.repaint();
    }
    
    @Override
    public void columnMarginChanged(final ChangeEvent changeEvent) {
        this.resizeAndRepaint();
    }
    
    @Override
    public void columnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
    }
    
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModel();
    }
    
    protected TableCellRenderer createDefaultRenderer() {
        return new DefaultTableCellHeaderRenderer();
    }
    
    protected void initializeLocalVars() {
        this.setOpaque(true);
        this.table = null;
        this.reorderingAllowed = true;
        this.resizingAllowed = true;
        this.draggedColumn = null;
        this.draggedDistance = 0;
        this.resizingColumn = null;
        this.updateTableInRealTime = true;
        ToolTipManager.sharedInstance().registerComponent(this);
        this.setDefaultRenderer(this.createDefaultRenderer());
    }
    
    public void resizeAndRepaint() {
        this.revalidate();
        this.repaint();
    }
    
    public void setDraggedColumn(final TableColumn draggedColumn) {
        this.draggedColumn = draggedColumn;
    }
    
    public void setDraggedDistance(final int draggedDistance) {
        this.draggedDistance = draggedDistance;
    }
    
    public void setResizingColumn(final TableColumn resizingColumn) {
        this.resizingColumn = resizingColumn;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.ui != null && this.getUIClassID().equals("TableHeaderUI")) {
            this.ui.installUI(this);
        }
    }
    
    private int getWidthInRightToLeft() {
        if (this.table != null && this.table.getAutoResizeMode() != 0) {
            return this.table.getWidth();
        }
        return super.getWidth();
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",draggedDistance=" + this.draggedDistance + ",reorderingAllowed=" + (this.reorderingAllowed ? "true" : "false") + ",resizingAllowed=" + (this.resizingAllowed ? "true" : "false") + ",updateTableInRealTime=" + (this.updateTableInRealTime ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJTableHeader();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJTableHeader extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            final int columnAtPoint;
            if ((columnAtPoint = JTableHeader.this.columnAtPoint(point)) != -1) {
                final TableColumn column = JTableHeader.this.columnModel.getColumn(columnAtPoint);
                TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
                if (tableCellRenderer == null) {
                    if (JTableHeader.this.defaultRenderer == null) {
                        return null;
                    }
                    tableCellRenderer = JTableHeader.this.defaultRenderer;
                }
                tableCellRenderer.getTableCellRendererComponent(JTableHeader.this.getTable(), column.getHeaderValue(), false, false, -1, columnAtPoint);
                return new AccessibleJTableHeaderEntry(columnAtPoint, JTableHeader.this, JTableHeader.this.table);
            }
            return null;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return JTableHeader.this.columnModel.getColumnCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (n < 0 || n >= this.getAccessibleChildrenCount()) {
                return null;
            }
            final TableColumn column = JTableHeader.this.columnModel.getColumn(n);
            TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
            if (tableCellRenderer == null) {
                if (JTableHeader.this.defaultRenderer == null) {
                    return null;
                }
                tableCellRenderer = JTableHeader.this.defaultRenderer;
            }
            tableCellRenderer.getTableCellRendererComponent(JTableHeader.this.getTable(), column.getHeaderValue(), false, false, -1, n);
            return new AccessibleJTableHeaderEntry(n, JTableHeader.this, JTableHeader.this.table);
        }
        
        protected class AccessibleJTableHeaderEntry extends AccessibleContext implements Accessible, AccessibleComponent
        {
            private JTableHeader parent;
            private int column;
            private JTable table;
            
            public AccessibleJTableHeaderEntry(final int column, final JTableHeader parent, final JTable table) {
                this.parent = parent;
                this.column = column;
                this.table = table;
                this.setAccessibleParent(this.parent);
            }
            
            @Override
            public AccessibleContext getAccessibleContext() {
                return this;
            }
            
            private AccessibleContext getCurrentAccessibleContext() {
                final TableColumnModel columnModel = this.table.getColumnModel();
                if (columnModel != null) {
                    if (this.column < 0 || this.column >= columnModel.getColumnCount()) {
                        return null;
                    }
                    final TableColumn column = columnModel.getColumn(this.column);
                    TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
                    if (tableCellRenderer == null) {
                        if (JTableHeader.this.defaultRenderer == null) {
                            return null;
                        }
                        tableCellRenderer = JTableHeader.this.defaultRenderer;
                    }
                    final Component tableCellRendererComponent = tableCellRenderer.getTableCellRendererComponent(JTableHeader.this.getTable(), column.getHeaderValue(), false, false, -1, this.column);
                    if (tableCellRendererComponent instanceof Accessible) {
                        return ((Accessible)tableCellRendererComponent).getAccessibleContext();
                    }
                }
                return null;
            }
            
            private Component getCurrentComponent() {
                final TableColumnModel columnModel = this.table.getColumnModel();
                if (columnModel == null) {
                    return null;
                }
                if (this.column < 0 || this.column >= columnModel.getColumnCount()) {
                    return null;
                }
                final TableColumn column = columnModel.getColumn(this.column);
                TableCellRenderer tableCellRenderer = column.getHeaderRenderer();
                if (tableCellRenderer == null) {
                    if (JTableHeader.this.defaultRenderer == null) {
                        return null;
                    }
                    tableCellRenderer = JTableHeader.this.defaultRenderer;
                }
                return tableCellRenderer.getTableCellRendererComponent(JTableHeader.this.getTable(), column.getHeaderValue(), false, false, -1, this.column);
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
                final String s = (String)JTableHeader.this.getClientProperty("AccessibleName");
                if (s != null) {
                    return s;
                }
                return this.table.getColumnName(this.column);
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
                return AccessibleRole.COLUMN_HEADER;
            }
            
            @Override
            public AccessibleStateSet getAccessibleStateSet() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext != null) {
                    final AccessibleStateSet accessibleStateSet = currentAccessibleContext.getAccessibleStateSet();
                    if (this.isShowing()) {
                        accessibleStateSet.add(AccessibleState.SHOWING);
                    }
                    return accessibleStateSet;
                }
                return new AccessibleStateSet();
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
                return this.isVisible() && JTableHeader.this.isShowing();
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
                if (this.parent != null) {
                    final Point locationOnScreen = this.parent.getLocationOnScreen();
                    final Point location = this.getLocation();
                    location.translate(locationOnScreen.x, locationOnScreen.y);
                    return location;
                }
                return null;
            }
            
            @Override
            public Point getLocation() {
                final AccessibleContext currentAccessibleContext = this.getCurrentAccessibleContext();
                if (currentAccessibleContext instanceof AccessibleComponent) {
                    return ((AccessibleComponent)currentAccessibleContext).getBounds().getLocation();
                }
                final Component currentComponent = this.getCurrentComponent();
                if (currentComponent != null) {
                    return currentComponent.getBounds().getLocation();
                }
                return this.getBounds().getLocation();
            }
            
            @Override
            public void setLocation(final Point point) {
            }
            
            @Override
            public Rectangle getBounds() {
                final Rectangle cellRect = this.table.getCellRect(-1, this.column, false);
                cellRect.y = 0;
                return cellRect;
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
                return this.getBounds().getSize();
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
}
