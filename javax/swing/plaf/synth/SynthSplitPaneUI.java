package javax.swing.plaf.synth;

import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Canvas;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.beans.PropertyChangeEvent;
import java.awt.Component;
import javax.swing.LookAndFeel;
import javax.swing.JSplitPane;
import java.awt.AWTKeyStroke;
import java.util.HashSet;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.util.Set;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SynthSplitPaneUI extends BasicSplitPaneUI implements PropertyChangeListener, SynthUI
{
    private static Set<KeyStroke> managingFocusForwardTraversalKeys;
    private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
    private SynthStyle style;
    private SynthStyle dividerStyle;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthSplitPaneUI();
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.splitPane);
        this.setOrientation(this.splitPane.getOrientation());
        this.setContinuousLayout(this.splitPane.isContinuousLayout());
        this.resetLayoutManager();
        if (this.nonContinuousLayoutDivider == null) {
            this.setNonContinuousLayoutDivider(this.createDefaultNonContinuousLayoutDivider(), true);
        }
        else {
            this.setNonContinuousLayoutDivider(this.nonContinuousLayoutDivider, true);
        }
        if (SynthSplitPaneUI.managingFocusForwardTraversalKeys == null) {
            (SynthSplitPaneUI.managingFocusForwardTraversalKeys = new HashSet<KeyStroke>()).add(KeyStroke.getKeyStroke(9, 0));
        }
        this.splitPane.setFocusTraversalKeys(0, SynthSplitPaneUI.managingFocusForwardTraversalKeys);
        if (SynthSplitPaneUI.managingFocusBackwardTraversalKeys == null) {
            (SynthSplitPaneUI.managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>()).add(KeyStroke.getKeyStroke(9, 1));
        }
        this.splitPane.setFocusTraversalKeys(1, SynthSplitPaneUI.managingFocusBackwardTraversalKeys);
    }
    
    private void updateStyle(final JSplitPane splitPane) {
        final SynthContext context = this.getContext(splitPane, Region.SPLIT_PANE_DIVIDER, 1);
        final SynthStyle dividerStyle = this.dividerStyle;
        this.dividerStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
        final SynthContext context2 = this.getContext(splitPane, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context2, this);
        if (this.style != style) {
            Object o = this.style.get(context2, "SplitPane.size");
            if (o == null) {
                o = 6;
            }
            LookAndFeel.installProperty(splitPane, "dividerSize", o);
            final Object value = this.style.get(context2, "SplitPane.oneTouchExpandable");
            if (value != null) {
                LookAndFeel.installProperty(splitPane, "oneTouchExpandable", value);
            }
            if (this.divider != null) {
                splitPane.remove(this.divider);
                this.divider.setDividerSize(splitPane.getDividerSize());
            }
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        if (this.style != style || this.dividerStyle != dividerStyle) {
            if (this.divider != null) {
                splitPane.remove(this.divider);
            }
            (this.divider = this.createDefaultDivider()).setBasicSplitPaneUI(this);
            splitPane.add(this.divider, "divider");
        }
        context2.dispose();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.splitPane.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.splitPane, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        final SynthContext context2 = this.getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER, 1);
        this.dividerStyle.uninstallDefaults(context2);
        context2.dispose();
        this.dividerStyle = null;
        super.uninstallDefaults();
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.splitPane.removePropertyChangeListener(this);
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    SynthContext getContext(final JComponent component, final Region region) {
        return this.getContext(component, region, this.getComponentState(component, region));
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final int n) {
        if (region == Region.SPLIT_PANE_DIVIDER) {
            return SynthContext.getContext(component, region, this.dividerStyle, n);
        }
        return SynthContext.getContext(component, region, this.style, n);
    }
    
    private int getComponentState(final JComponent component, final Region region) {
        int componentState = SynthLookAndFeel.getComponentState(component);
        if (this.divider.isMouseOver()) {
            componentState |= 0x2;
        }
        return componentState;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JSplitPane)propertyChangeEvent.getSource());
        }
    }
    
    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        final SynthSplitPaneDivider synthSplitPaneDivider = new SynthSplitPaneDivider(this);
        synthSplitPaneDivider.setDividerSize(this.splitPane.getDividerSize());
        return synthSplitPaneDivider;
    }
    
    @Override
    protected Component createDefaultNonContinuousLayoutDivider() {
        return new Canvas() {
            @Override
            public void paint(final Graphics graphics) {
                SynthSplitPaneUI.this.paintDragDivider(graphics, 0, 0, this.getWidth(), this.getHeight());
            }
        };
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintSplitPaneBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        super.paint(graphics, this.splitPane);
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintSplitPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    private void paintDragDivider(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        final SynthContext context = this.getContext(this.splitPane, Region.SPLIT_PANE_DIVIDER);
        context.setComponentState(((context.getComponentState() | 0x2) ^ 0x2) | 0x4);
        final Shape clip = graphics.getClip();
        graphics.clipRect(n, n2, n3, n4);
        context.getPainter().paintSplitPaneDragDivider(context, graphics, n, n2, n3, n4, this.splitPane.getOrientation());
        graphics.setClip(clip);
        context.dispose();
    }
    
    @Override
    public void finishedPaintingChildren(final JSplitPane splitPane, final Graphics graphics) {
        if (splitPane == this.splitPane && this.getLastDragLocation() != -1 && !this.isContinuousLayout() && !this.draggingHW) {
            if (splitPane.getOrientation() == 1) {
                this.paintDragDivider(graphics, this.getLastDragLocation(), 0, this.dividerSize - 1, this.splitPane.getHeight() - 1);
            }
            else {
                this.paintDragDivider(graphics, 0, this.getLastDragLocation(), this.splitPane.getWidth() - 1, this.dividerSize - 1);
            }
        }
    }
}
