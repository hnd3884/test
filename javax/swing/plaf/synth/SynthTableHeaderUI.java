package javax.swing.plaf.synth;

import javax.swing.border.Border;
import java.util.List;
import javax.swing.table.TableModel;
import javax.swing.RowSorter;
import javax.swing.JTable;
import sun.swing.table.DefaultTableCellHeaderRenderer;
import java.beans.PropertyChangeEvent;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.table.TableCellRenderer;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;

public class SynthTableHeaderUI extends BasicTableHeaderUI implements PropertyChangeListener, SynthUI
{
    private TableCellRenderer prevRenderer;
    private SynthStyle style;
    
    public SynthTableHeaderUI() {
        this.prevRenderer = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTableHeaderUI();
    }
    
    @Override
    protected void installDefaults() {
        this.prevRenderer = this.header.getDefaultRenderer();
        if (this.prevRenderer instanceof UIResource) {
            this.header.setDefaultRenderer(new HeaderRenderer());
        }
        this.updateStyle(this.header);
    }
    
    private void updateStyle(final JTableHeader tableHeader) {
        final SynthContext context = this.getContext(tableHeader, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style && style != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
        }
        context.dispose();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.header.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallDefaults() {
        if (this.header.getDefaultRenderer() instanceof HeaderRenderer) {
            this.header.setDefaultRenderer(this.prevRenderer);
        }
        final SynthContext context = this.getContext(this.header, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected void uninstallListeners() {
        this.header.removePropertyChangeListener(this);
        super.uninstallListeners();
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintTableHeaderBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        super.paint(graphics, synthContext.getComponent());
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTableHeaderBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    @Override
    protected void rolloverColumnUpdated(final int n, final int n2) {
        this.header.repaint(this.header.getHeaderRect(n));
        this.header.repaint(this.header.getHeaderRect(n2));
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JTableHeader)propertyChangeEvent.getSource());
        }
    }
    
    private class HeaderRenderer extends DefaultTableCellHeaderRenderer
    {
        HeaderRenderer() {
            this.setHorizontalAlignment(10);
            this.setName("TableHeader.renderer");
        }
        
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
            final boolean b3 = n2 == BasicTableHeaderUI.this.getRolloverColumn();
            if (b || b3 || b2) {
                SynthLookAndFeel.setSelectedUI((ComponentUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), b, b2, table.isEnabled(), b3);
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }
            final RowSorter<? extends TableModel> rowSorter = (table == null) ? null : table.getRowSorter();
            final List list = (rowSorter == null) ? null : rowSorter.getSortKeys();
            if (list != null && list.size() > 0 && ((RowSorter.SortKey)list.get(0)).getColumn() == table.convertColumnIndexToModel(n2)) {
                switch (((RowSorter.SortKey)list.get(0)).getSortOrder()) {
                    case ASCENDING: {
                        this.putClientProperty("Table.sortOrder", "ASCENDING");
                        break;
                    }
                    case DESCENDING: {
                        this.putClientProperty("Table.sortOrder", "DESCENDING");
                        break;
                    }
                    case UNSORTED: {
                        this.putClientProperty("Table.sortOrder", "UNSORTED");
                        break;
                    }
                    default: {
                        throw new AssertionError((Object)"Cannot happen");
                    }
                }
            }
            else {
                this.putClientProperty("Table.sortOrder", "UNSORTED");
            }
            super.getTableCellRendererComponent(table, o, b, b2, n, n2);
            return this;
        }
        
        @Override
        public void setBorder(final Border border) {
            if (border instanceof SynthBorder) {
                super.setBorder(border);
            }
        }
    }
}
