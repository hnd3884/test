package javax.swing.plaf.synth;

import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import java.beans.PropertyChangeEvent;
import javax.swing.JViewport;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ContainerListener;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.JScrollPane;
import java.awt.Rectangle;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicScrollPaneUI;

public class SynthScrollPaneUI extends BasicScrollPaneUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private boolean viewportViewHasFocus;
    private ViewportViewFocusHandler viewportViewFocusHandler;
    
    public SynthScrollPaneUI() {
        this.viewportViewHasFocus = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthScrollPaneUI();
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintScrollPaneBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        final Border viewportBorder = this.scrollpane.getViewportBorder();
        if (viewportBorder != null) {
            final Rectangle viewportBorderBounds = this.scrollpane.getViewportBorderBounds();
            viewportBorder.paintBorder(this.scrollpane, graphics, viewportBorderBounds.x, viewportBorderBounds.y, viewportBorderBounds.width, viewportBorderBounds.height);
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintScrollPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    protected void installDefaults(final JScrollPane scrollPane) {
        this.updateStyle(scrollPane);
    }
    
    private void updateStyle(final JScrollPane scrollPane) {
        final SynthContext context = this.getContext(scrollPane, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            final Border viewportBorder = this.scrollpane.getViewportBorder();
            if (viewportBorder == null || viewportBorder instanceof UIResource) {
                this.scrollpane.setViewportBorder(new ViewportBorder(context));
            }
            if (style != null) {
                this.uninstallKeyboardActions(scrollPane);
                this.installKeyboardActions(scrollPane);
            }
        }
        context.dispose();
    }
    
    @Override
    protected void installListeners(final JScrollPane scrollPane) {
        super.installListeners(scrollPane);
        scrollPane.addPropertyChangeListener(this);
        if (UIManager.getBoolean("ScrollPane.useChildTextComponentFocus")) {
            this.viewportViewFocusHandler = new ViewportViewFocusHandler();
            scrollPane.getViewport().addContainerListener(this.viewportViewFocusHandler);
            final Component view = scrollPane.getViewport().getView();
            if (view instanceof JTextComponent) {
                view.addFocusListener(this.viewportViewFocusHandler);
            }
        }
    }
    
    @Override
    protected void uninstallDefaults(final JScrollPane scrollPane) {
        final SynthContext context = this.getContext(scrollPane, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        if (this.scrollpane.getViewportBorder() instanceof UIResource) {
            this.scrollpane.setViewportBorder(null);
        }
    }
    
    @Override
    protected void uninstallListeners(final JComponent component) {
        super.uninstallListeners(component);
        component.removePropertyChangeListener(this);
        if (this.viewportViewFocusHandler != null) {
            final JViewport viewport = ((JScrollPane)component).getViewport();
            viewport.removeContainerListener(this.viewportViewFocusHandler);
            if (viewport.getView() != null) {
                viewport.getView().removeFocusListener(this.viewportViewFocusHandler);
            }
            this.viewportViewFocusHandler = null;
        }
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        int componentState = SynthLookAndFeel.getComponentState(component);
        if (this.viewportViewFocusHandler != null && this.viewportViewHasFocus) {
            componentState |= 0x100;
        }
        return componentState;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle(this.scrollpane);
        }
    }
    
    private class ViewportBorder extends AbstractBorder implements UIResource
    {
        private Insets insets;
        
        ViewportBorder(final SynthContext synthContext) {
            this.insets = (Insets)synthContext.getStyle().get(synthContext, "ScrollPane.viewportBorderInsets");
            if (this.insets == null) {
                this.insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
            }
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final SynthContext context = SynthScrollPaneUI.this.getContext((JComponent)component);
            if (context.getStyle() != null) {
                context.getPainter().paintViewportBorder(context, graphics, n, n2, n3, n4);
                context.dispose();
                return;
            }
            assert false : "SynthBorder is being used outside after the  UI has been uninstalled";
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            if (insets == null) {
                return new Insets(this.insets.top, this.insets.left, this.insets.bottom, this.insets.right);
            }
            insets.top = this.insets.top;
            insets.bottom = this.insets.bottom;
            insets.left = this.insets.left;
            insets.right = this.insets.left;
            return insets;
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
    
    private class ViewportViewFocusHandler implements ContainerListener, FocusListener
    {
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            if (containerEvent.getChild() instanceof JTextComponent) {
                containerEvent.getChild().addFocusListener(this);
                SynthScrollPaneUI.this.viewportViewHasFocus = containerEvent.getChild().isFocusOwner();
                SynthScrollPaneUI.this.scrollpane.repaint();
            }
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            if (containerEvent.getChild() instanceof JTextComponent) {
                containerEvent.getChild().removeFocusListener(this);
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            SynthScrollPaneUI.this.viewportViewHasFocus = true;
            SynthScrollPaneUI.this.scrollpane.repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            SynthScrollPaneUI.this.viewportViewHasFocus = false;
            SynthScrollPaneUI.this.scrollpane.repaint();
        }
    }
}
