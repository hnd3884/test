package javax.swing.plaf.synth;

import javax.swing.border.Border;
import javax.swing.DefaultListCellRenderer;
import java.awt.Component;
import java.awt.Color;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.UIResource;
import javax.swing.JList;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicListUI;

public class SynthListUI extends BasicListUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private boolean useListColors;
    private boolean useUIBorder;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthListUI();
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintListBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        context.dispose();
        this.paint(graphics, component);
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintListBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.list.addPropertyChangeListener(this);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JComponent)propertyChangeEvent.getSource());
        }
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.list.removePropertyChangeListener(this);
    }
    
    @Override
    protected void installDefaults() {
        if (this.list.getCellRenderer() == null || this.list.getCellRenderer() instanceof UIResource) {
            this.list.setCellRenderer(new SynthListCellRenderer());
        }
        this.updateStyle(this.list);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(this.list, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            context.setComponentState(512);
            final Color selectionBackground = this.list.getSelectionBackground();
            if (selectionBackground == null || selectionBackground instanceof UIResource) {
                this.list.setSelectionBackground(this.style.getColor(context, ColorType.TEXT_BACKGROUND));
            }
            final Color selectionForeground = this.list.getSelectionForeground();
            if (selectionForeground == null || selectionForeground instanceof UIResource) {
                this.list.setSelectionForeground(this.style.getColor(context, ColorType.TEXT_FOREGROUND));
            }
            this.useListColors = this.style.getBoolean(context, "List.rendererUseListColors", true);
            this.useUIBorder = this.style.getBoolean(context, "List.rendererUseUIBorder", true);
            final int int1 = this.style.getInt(context, "List.cellHeight", -1);
            if (int1 != -1) {
                this.list.setFixedCellHeight(int1);
            }
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        final SynthContext context = this.getContext(this.list, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        return SynthLookAndFeel.getComponentState(component);
    }
    
    private class SynthListCellRenderer extends UIResource
    {
        @Override
        public String getName() {
            return "List.cellRenderer";
        }
        
        @Override
        public void setBorder(final Border border) {
            if (SynthListUI.this.useUIBorder || border instanceof SynthBorder) {
                super.setBorder(border);
            }
        }
        
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
            if (!SynthListUI.this.useListColors && (b || b2)) {
                SynthLookAndFeel.setSelectedUI((ComponentUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), b, b2, list.isEnabled(), false);
            }
            else {
                SynthLookAndFeel.resetSelectedUI();
            }
            super.getListCellRendererComponent(list, o, n, b, b2);
            return this;
        }
        
        @Override
        public void paint(final Graphics graphics) {
            super.paint(graphics);
            SynthLookAndFeel.resetSelectedUI();
        }
    }
}
