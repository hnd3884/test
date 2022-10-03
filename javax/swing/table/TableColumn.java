package javax.swing.table;

import javax.swing.UIManager;
import java.beans.PropertyChangeListener;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.event.SwingPropertyChangeSupport;
import java.io.Serializable;

public class TableColumn implements Serializable
{
    public static final String COLUMN_WIDTH_PROPERTY = "columWidth";
    public static final String HEADER_VALUE_PROPERTY = "headerValue";
    public static final String HEADER_RENDERER_PROPERTY = "headerRenderer";
    public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
    protected int modelIndex;
    protected Object identifier;
    protected int width;
    protected int minWidth;
    private int preferredWidth;
    protected int maxWidth;
    protected TableCellRenderer headerRenderer;
    protected Object headerValue;
    protected TableCellRenderer cellRenderer;
    protected TableCellEditor cellEditor;
    protected boolean isResizable;
    @Deprecated
    protected transient int resizedPostingDisableCount;
    private SwingPropertyChangeSupport changeSupport;
    
    public TableColumn() {
        this(0);
    }
    
    public TableColumn(final int n) {
        this(n, 75, null, null);
    }
    
    public TableColumn(final int n, final int n2) {
        this(n, n2, null, null);
    }
    
    public TableColumn(final int modelIndex, final int n, final TableCellRenderer cellRenderer, final TableCellEditor cellEditor) {
        this.modelIndex = modelIndex;
        final int max = Math.max(n, 0);
        this.width = max;
        this.preferredWidth = max;
        this.cellRenderer = cellRenderer;
        this.cellEditor = cellEditor;
        this.minWidth = Math.min(15, this.width);
        this.maxWidth = Integer.MAX_VALUE;
        this.isResizable = true;
        this.resizedPostingDisableCount = 0;
        this.headerValue = null;
    }
    
    private void firePropertyChange(final String s, final Object o, final Object o2) {
        if (this.changeSupport != null) {
            this.changeSupport.firePropertyChange(s, o, o2);
        }
    }
    
    private void firePropertyChange(final String s, final int n, final int n2) {
        if (n != n2) {
            this.firePropertyChange(s, n, (Object)n2);
        }
    }
    
    private void firePropertyChange(final String s, final boolean b, final boolean b2) {
        if (b != b2) {
            this.firePropertyChange(s, b, (Object)b2);
        }
    }
    
    public void setModelIndex(final int modelIndex) {
        this.firePropertyChange("modelIndex", this.modelIndex, this.modelIndex = modelIndex);
    }
    
    public int getModelIndex() {
        return this.modelIndex;
    }
    
    public void setIdentifier(final Object identifier) {
        this.firePropertyChange("identifier", this.identifier, this.identifier = identifier);
    }
    
    public Object getIdentifier() {
        return (this.identifier != null) ? this.identifier : this.getHeaderValue();
    }
    
    public void setHeaderValue(final Object headerValue) {
        this.firePropertyChange("headerValue", this.headerValue, this.headerValue = headerValue);
    }
    
    public Object getHeaderValue() {
        return this.headerValue;
    }
    
    public void setHeaderRenderer(final TableCellRenderer headerRenderer) {
        this.firePropertyChange("headerRenderer", this.headerRenderer, this.headerRenderer = headerRenderer);
    }
    
    public TableCellRenderer getHeaderRenderer() {
        return this.headerRenderer;
    }
    
    public void setCellRenderer(final TableCellRenderer cellRenderer) {
        this.firePropertyChange("cellRenderer", this.cellRenderer, this.cellRenderer = cellRenderer);
    }
    
    public TableCellRenderer getCellRenderer() {
        return this.cellRenderer;
    }
    
    public void setCellEditor(final TableCellEditor cellEditor) {
        this.firePropertyChange("cellEditor", this.cellEditor, this.cellEditor = cellEditor);
    }
    
    public TableCellEditor getCellEditor() {
        return this.cellEditor;
    }
    
    public void setWidth(final int n) {
        this.firePropertyChange("width", this.width, this.width = Math.min(Math.max(n, this.minWidth), this.maxWidth));
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setPreferredWidth(final int n) {
        this.firePropertyChange("preferredWidth", this.preferredWidth, this.preferredWidth = Math.min(Math.max(n, this.minWidth), this.maxWidth));
    }
    
    public int getPreferredWidth() {
        return this.preferredWidth;
    }
    
    public void setMinWidth(final int n) {
        final int minWidth = this.minWidth;
        this.minWidth = Math.max(Math.min(n, this.maxWidth), 0);
        if (this.width < this.minWidth) {
            this.setWidth(this.minWidth);
        }
        if (this.preferredWidth < this.minWidth) {
            this.setPreferredWidth(this.minWidth);
        }
        this.firePropertyChange("minWidth", minWidth, this.minWidth);
    }
    
    public int getMinWidth() {
        return this.minWidth;
    }
    
    public void setMaxWidth(final int n) {
        final int maxWidth = this.maxWidth;
        this.maxWidth = Math.max(this.minWidth, n);
        if (this.width > this.maxWidth) {
            this.setWidth(this.maxWidth);
        }
        if (this.preferredWidth > this.maxWidth) {
            this.setPreferredWidth(this.maxWidth);
        }
        this.firePropertyChange("maxWidth", maxWidth, this.maxWidth);
    }
    
    public int getMaxWidth() {
        return this.maxWidth;
    }
    
    public void setResizable(final boolean isResizable) {
        this.firePropertyChange("isResizable", this.isResizable, this.isResizable = isResizable);
    }
    
    public boolean getResizable() {
        return this.isResizable;
    }
    
    public void sizeWidthToFit() {
        if (this.headerRenderer == null) {
            return;
        }
        final Component tableCellRendererComponent = this.headerRenderer.getTableCellRendererComponent(null, this.getHeaderValue(), false, false, 0, 0);
        this.setMinWidth(tableCellRendererComponent.getMinimumSize().width);
        this.setMaxWidth(tableCellRendererComponent.getMaximumSize().width);
        this.setPreferredWidth(tableCellRendererComponent.getPreferredSize().width);
        this.setWidth(this.getPreferredWidth());
    }
    
    @Deprecated
    public void disableResizedPosting() {
        ++this.resizedPostingDisableCount;
    }
    
    @Deprecated
    public void enableResizedPosting() {
        --this.resizedPostingDisableCount;
    }
    
    public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport != null) {
            this.changeSupport.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    protected TableCellRenderer createDefaultHeaderRenderer() {
        final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
                if (table != null) {
                    final JTableHeader tableHeader = table.getTableHeader();
                    if (tableHeader != null) {
                        this.setForeground(tableHeader.getForeground());
                        this.setBackground(tableHeader.getBackground());
                        this.setFont(tableHeader.getFont());
                    }
                }
                this.setText((o == null) ? "" : o.toString());
                this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return this;
            }
        };
        defaultTableCellRenderer.setHorizontalAlignment(0);
        return defaultTableCellRenderer;
    }
}
