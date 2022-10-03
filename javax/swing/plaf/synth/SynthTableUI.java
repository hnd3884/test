package javax.swing.plaf.synth;

import java.text.Format;
import java.text.DateFormat;
import java.text.NumberFormat;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JCheckBox;
import java.beans.PropertyChangeEvent;
import java.awt.Container;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.TransferHandler;
import java.awt.Dimension;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ColorUIResource;
import javax.swing.JTable;
import javax.swing.plaf.UIResource;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.util.Date;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicTableUI;

public class SynthTableUI extends BasicTableUI implements SynthUI, PropertyChangeListener
{
    private SynthStyle style;
    private boolean useTableColors;
    private boolean useUIBorder;
    private Color alternateColor;
    private TableCellRenderer dateRenderer;
    private TableCellRenderer numberRenderer;
    private TableCellRenderer doubleRender;
    private TableCellRenderer floatRenderer;
    private TableCellRenderer iconRenderer;
    private TableCellRenderer imageIconRenderer;
    private TableCellRenderer booleanRenderer;
    private TableCellRenderer objectRenderer;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTableUI();
    }
    
    @Override
    protected void installDefaults() {
        this.dateRenderer = this.installRendererIfPossible(Date.class, null);
        this.numberRenderer = this.installRendererIfPossible(Number.class, null);
        this.doubleRender = this.installRendererIfPossible(Double.class, null);
        this.floatRenderer = this.installRendererIfPossible(Float.class, null);
        this.iconRenderer = this.installRendererIfPossible(Icon.class, null);
        this.imageIconRenderer = this.installRendererIfPossible(ImageIcon.class, null);
        this.booleanRenderer = this.installRendererIfPossible(Boolean.class, new SynthBooleanTableCellRenderer());
        this.objectRenderer = this.installRendererIfPossible(Object.class, new SynthTableCellRenderer());
        this.updateStyle(this.table);
    }
    
    private TableCellRenderer installRendererIfPossible(final Class clazz, final TableCellRenderer tableCellRenderer) {
        final TableCellRenderer defaultRenderer = this.table.getDefaultRenderer(clazz);
        if (defaultRenderer instanceof UIResource) {
            this.table.setDefaultRenderer(clazz, tableCellRenderer);
        }
        return defaultRenderer;
    }
    
    private void updateStyle(final JTable table) {
        final SynthContext context = this.getContext(table, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            context.setComponentState(513);
            final Color selectionBackground = this.table.getSelectionBackground();
            if (selectionBackground == null || selectionBackground instanceof UIResource) {
                this.table.setSelectionBackground(this.style.getColor(context, ColorType.TEXT_BACKGROUND));
            }
            final Color selectionForeground = this.table.getSelectionForeground();
            if (selectionForeground == null || selectionForeground instanceof UIResource) {
                this.table.setSelectionForeground(this.style.getColor(context, ColorType.TEXT_FOREGROUND));
            }
            context.setComponentState(1);
            final Color gridColor = this.table.getGridColor();
            if (gridColor == null || gridColor instanceof UIResource) {
                Color color = (Color)this.style.get(context, "Table.gridColor");
                if (color == null) {
                    color = this.style.getColor(context, ColorType.FOREGROUND);
                }
                this.table.setGridColor((color == null) ? new ColorUIResource(Color.GRAY) : color);
            }
            this.useTableColors = this.style.getBoolean(context, "Table.rendererUseTableColors", true);
            this.useUIBorder = this.style.getBoolean(context, "Table.rendererUseUIBorder", true);
            final Object value = this.style.get(context, "Table.rowHeight");
            if (value != null) {
                LookAndFeel.installProperty(this.table, "rowHeight", value);
            }
            if (!this.style.getBoolean(context, "Table.showGrid", true)) {
                this.table.setShowGrid(false);
            }
            Dimension intercellSpacing = this.table.getIntercellSpacing();
            if (intercellSpacing != null) {
                intercellSpacing = (Dimension)this.style.get(context, "Table.intercellSpacing");
            }
            this.alternateColor = (Color)this.style.get(context, "Table.alternateRowColor");
            if (intercellSpacing != null) {
                this.table.setIntercellSpacing(intercellSpacing);
            }
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.table.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallDefaults() {
        this.table.setDefaultRenderer(Date.class, this.dateRenderer);
        this.table.setDefaultRenderer(Number.class, this.numberRenderer);
        this.table.setDefaultRenderer(Double.class, this.doubleRender);
        this.table.setDefaultRenderer(Float.class, this.floatRenderer);
        this.table.setDefaultRenderer(Icon.class, this.iconRenderer);
        this.table.setDefaultRenderer(ImageIcon.class, this.imageIconRenderer);
        this.table.setDefaultRenderer(Boolean.class, this.booleanRenderer);
        this.table.setDefaultRenderer(Object.class, this.objectRenderer);
        if (this.table.getTransferHandler() instanceof UIResource) {
            this.table.setTransferHandler(null);
        }
        final SynthContext context = this.getContext(this.table, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected void uninstallListeners() {
        this.table.removePropertyChangeListener(this);
        super.uninstallListeners();
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintTableBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTableBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        final Rectangle clipBounds = graphics.getClipBounds();
        final Rectangle bounds;
        final Rectangle rectangle = bounds = this.table.getBounds();
        final int n = 0;
        rectangle.y = n;
        bounds.x = n;
        if (this.table.getRowCount() <= 0 || this.table.getColumnCount() <= 0 || !rectangle.intersects(clipBounds)) {
            this.paintDropLines(synthContext, graphics);
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
        this.paintCells(synthContext, graphics, rowAtPoint, rowAtPoint2, columnAtPoint, columnAtPoint2);
        this.paintGrid(synthContext, graphics, rowAtPoint, rowAtPoint2, columnAtPoint, columnAtPoint2);
        this.paintDropLines(synthContext, graphics);
    }
    
    private void paintDropLines(final SynthContext synthContext, final Graphics graphics) {
        final JTable.DropLocation dropLocation = this.table.getDropLocation();
        if (dropLocation == null) {
            return;
        }
        final Color color = (Color)this.style.get(synthContext, "Table.dropLineColor");
        final Color color2 = (Color)this.style.get(synthContext, "Table.dropLineShortColor");
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
    
    private void paintGrid(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        graphics.setColor(this.table.getGridColor());
        final Rectangle union = this.table.getCellRect(n, n3, true).union(this.table.getCellRect(n2, n4, true));
        final SynthGraphicsUtils graphicsUtils = synthContext.getStyle().getGraphicsUtils(synthContext);
        if (this.table.getShowHorizontalLines()) {
            final int n5 = union.x + union.width;
            int y = union.y;
            for (int i = n; i <= n2; ++i) {
                y += this.table.getRowHeight(i);
                graphicsUtils.drawLine(synthContext, "Table.grid", graphics, union.x, y - 1, n5 - 1, y - 1);
            }
        }
        if (this.table.getShowVerticalLines()) {
            final TableColumnModel columnModel = this.table.getColumnModel();
            final int n6 = union.y + union.height;
            if (this.table.getComponentOrientation().isLeftToRight()) {
                int x = union.x;
                for (int j = n3; j <= n4; ++j) {
                    x += columnModel.getColumn(j).getWidth();
                    graphicsUtils.drawLine(synthContext, "Table.grid", graphics, x - 1, 0, x - 1, n6 - 1);
                }
            }
            else {
                int x2 = union.x;
                for (int k = n4; k >= n3; --k) {
                    x2 += columnModel.getColumn(k).getWidth();
                    graphicsUtils.drawLine(synthContext, "Table.grid", graphics, x2 - 1, 0, x2 - 1, n6 - 1);
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
    
    private void paintCells(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
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
                        this.paintCell(synthContext, graphics, cellRect, i, j);
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
                    this.paintCell(synthContext, graphics, cellRect2, k, n3);
                }
                for (int l = n3 + 1; l <= n4; ++l) {
                    final TableColumn column3 = columnModel.getColumn(l);
                    final int width2 = column3.getWidth();
                    cellRect2.width = width2 - columnMargin;
                    final Rectangle rectangle2 = cellRect2;
                    rectangle2.x -= width2;
                    if (column3 != tableColumn) {
                        this.paintCell(synthContext, graphics, cellRect2, k, l);
                    }
                }
            }
        }
        if (tableColumn != null) {
            this.paintDraggedArea(synthContext, graphics, n, n2, tableColumn, tableHeader.getDraggedDistance());
        }
        this.rendererPane.removeAll();
    }
    
    private void paintDraggedArea(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final TableColumn tableColumn, final int n3) {
        final int viewIndexForColumn = this.viewIndexForColumn(tableColumn);
        final Rectangle union = this.table.getCellRect(n, viewIndexForColumn, true).union(this.table.getCellRect(n2, viewIndexForColumn, true));
        graphics.setColor(this.table.getParent().getBackground());
        graphics.fillRect(union.x, union.y, union.width, union.height);
        final Rectangle rectangle = union;
        rectangle.x += n3;
        graphics.setColor(synthContext.getStyle().getColor(synthContext, ColorType.BACKGROUND));
        graphics.fillRect(union.x, union.y, union.width, union.height);
        final SynthGraphicsUtils graphicsUtils = synthContext.getStyle().getGraphicsUtils(synthContext);
        if (this.table.getShowVerticalLines()) {
            graphics.setColor(this.table.getGridColor());
            final int x = union.x;
            final int y = union.y;
            final int n4 = x + union.width - 1;
            final int n5 = y + union.height - 1;
            graphicsUtils.drawLine(synthContext, "Table.grid", graphics, x - 1, y, x - 1, n5);
            graphicsUtils.drawLine(synthContext, "Table.grid", graphics, n4, y, n4, n5);
        }
        for (int i = n; i <= n2; ++i) {
            final Rectangle cellRect;
            final Rectangle rectangle2 = cellRect = this.table.getCellRect(i, viewIndexForColumn, (boolean)(0 != 0));
            cellRect.x += n3;
            this.paintCell(synthContext, graphics, rectangle2, i, viewIndexForColumn);
            if (this.table.getShowHorizontalLines()) {
                graphics.setColor(this.table.getGridColor());
                final Rectangle cellRect2;
                final Rectangle rectangle3 = cellRect2 = this.table.getCellRect(i, viewIndexForColumn, (boolean)(1 != 0));
                cellRect2.x += n3;
                final int x2 = rectangle3.x;
                final int y2 = rectangle3.y;
                final int n6 = x2 + rectangle3.width - 1;
                final int n7 = y2 + rectangle3.height - 1;
                graphicsUtils.drawLine(synthContext, "Table.grid", graphics, x2, n7, n6, n7);
            }
        }
    }
    
    private void paintCell(final SynthContext synthContext, final Graphics graphics, final Rectangle bounds, final int n, final int n2) {
        if (this.table.isEditing() && this.table.getEditingRow() == n && this.table.getEditingColumn() == n2) {
            final Component editorComponent = this.table.getEditorComponent();
            editorComponent.setBounds(bounds);
            editorComponent.validate();
        }
        else {
            final Component prepareRenderer = this.table.prepareRenderer(this.table.getCellRenderer(n, n2), n, n2);
            final Color background = prepareRenderer.getBackground();
            if ((background == null || background instanceof UIResource || prepareRenderer instanceof SynthBooleanTableCellRenderer) && !this.table.isCellSelected(n, n2) && this.alternateColor != null && n % 2 != 0) {
                prepareRenderer.setBackground(this.alternateColor);
            }
            this.rendererPane.paintComponent(graphics, prepareRenderer, this.table, bounds.x, bounds.y, bounds.width, bounds.height, true);
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JTable)propertyChangeEvent.getSource());
        }
    }
    
    private class SynthBooleanTableCellRenderer extends JCheckBox implements TableCellRenderer
    {
        private boolean isRowSelected;
        
        public SynthBooleanTableCellRenderer() {
            this.setHorizontalAlignment(0);
            this.setName("Table.cellRenderer");
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean isRowSelected, final boolean b, final int n, final int n2) {
            this.isRowSelected = isRowSelected;
            if (isRowSelected) {
                this.setForeground(this.unwrap(table.getSelectionForeground()));
                this.setBackground(this.unwrap(table.getSelectionBackground()));
            }
            else {
                this.setForeground(this.unwrap(table.getForeground()));
                this.setBackground(this.unwrap(table.getBackground()));
            }
            this.setSelected(o != null && (boolean)o);
            return this;
        }
        
        private Color unwrap(final Color color) {
            if (color instanceof UIResource) {
                return new Color(color.getRGB());
            }
            return color;
        }
        
        @Override
        public boolean isOpaque() {
            return this.isRowSelected || super.isOpaque();
        }
    }
    
    private class SynthTableCellRenderer extends DefaultTableCellRenderer
    {
        private Object numberFormat;
        private Object dateFormat;
        private boolean opaque;
        
        @Override
        public void setOpaque(final boolean opaque) {
            this.opaque = opaque;
        }
        
        @Override
        public boolean isOpaque() {
            return this.opaque;
        }
        
        @Override
        public String getName() {
            final String name = super.getName();
            if (name == null) {
                return "Table.cellRenderer";
            }
            return name;
        }
        
        @Override
        public void setBorder(final Border border) {
            if (SynthTableUI.this.useUIBorder || border instanceof SynthBorder) {
                super.setBorder(border);
            }
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
            if (!SynthTableUI.this.useTableColors && (b || b2)) {
                SynthLookAndFeel.setSelectedUI((ComponentUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), b, b2, table.isEnabled(), false);
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }
            super.getTableCellRendererComponent(table, o, b, b2, n, n2);
            this.setIcon(null);
            if (table != null) {
                this.configureValue(o, table.getColumnClass(n2));
            }
            return this;
        }
        
        private void configureValue(final Object o, final Class clazz) {
            if (clazz == Object.class || clazz == null) {
                this.setHorizontalAlignment(10);
            }
            else if (clazz == Float.class || clazz == Double.class) {
                if (this.numberFormat == null) {
                    this.numberFormat = NumberFormat.getInstance();
                }
                this.setHorizontalAlignment(11);
                this.setText((o == null) ? "" : ((NumberFormat)this.numberFormat).format(o));
            }
            else if (clazz == Number.class) {
                this.setHorizontalAlignment(11);
            }
            else if (clazz == Icon.class || clazz == ImageIcon.class) {
                this.setHorizontalAlignment(0);
                this.setIcon((o instanceof Icon) ? ((Icon)o) : null);
                this.setText("");
            }
            else if (clazz == Date.class) {
                if (this.dateFormat == null) {
                    this.dateFormat = DateFormat.getDateInstance();
                }
                this.setHorizontalAlignment(10);
                this.setText((o == null) ? "" : ((Format)this.dateFormat).format(o));
            }
            else {
                this.configureValue(o, clazz.getSuperclass());
            }
        }
        
        @Override
        public void paint(final Graphics graphics) {
            super.paint(graphics);
            SynthLookAndFeel.resetSelectedUI();
        }
    }
}
