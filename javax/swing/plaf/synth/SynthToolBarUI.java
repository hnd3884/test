package javax.swing.plaf.synth;

import javax.swing.Box;
import javax.swing.JSeparator;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import sun.swing.plaf.synth.SynthIcon;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JToolBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Rectangle;
import javax.swing.Icon;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicToolBarUI;

public class SynthToolBarUI extends BasicToolBarUI implements PropertyChangeListener, SynthUI
{
    private Icon handleIcon;
    private Rectangle contentRect;
    private SynthStyle style;
    private SynthStyle contentStyle;
    private SynthStyle dragWindowStyle;
    
    public SynthToolBarUI() {
        this.handleIcon = null;
        this.contentRect = new Rectangle();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthToolBarUI();
    }
    
    @Override
    protected void installDefaults() {
        this.toolBar.setLayout(this.createLayout());
        this.updateStyle(this.toolBar);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.toolBar.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.toolBar.removePropertyChangeListener(this);
    }
    
    private void updateStyle(final JToolBar toolBar) {
        final SynthContext context = this.getContext(toolBar, Region.TOOL_BAR_CONTENT, null, 1);
        this.contentStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
        final SynthContext context2 = this.getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, null, 1);
        this.dragWindowStyle = SynthLookAndFeel.updateStyle(context2, this);
        context2.dispose();
        final SynthContext context3 = this.getContext(toolBar, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context3, this);
        if (style != this.style) {
            this.handleIcon = this.style.getIcon(context3, "ToolBar.handleIcon");
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context3.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.toolBar, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        this.handleIcon = null;
        final SynthContext context2 = this.getContext(this.toolBar, Region.TOOL_BAR_CONTENT, this.contentStyle, 1);
        this.contentStyle.uninstallDefaults(context2);
        context2.dispose();
        this.contentStyle = null;
        final SynthContext context3 = this.getContext(this.toolBar, Region.TOOL_BAR_DRAG_WINDOW, this.dragWindowStyle, 1);
        this.dragWindowStyle.uninstallDefaults(context3);
        context3.dispose();
        this.dragWindowStyle = null;
        this.toolBar.setLayout(null);
    }
    
    @Override
    protected void installComponents() {
    }
    
    @Override
    protected void uninstallComponents() {
    }
    
    protected LayoutManager createLayout() {
        return new SynthToolBarLayoutManager();
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final SynthStyle synthStyle) {
        return SynthContext.getContext(component, region, synthStyle, this.getComponentState(component, region));
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final SynthStyle synthStyle, final int n) {
        return SynthContext.getContext(component, region, synthStyle, n);
    }
    
    private int getComponentState(final JComponent component, final Region region) {
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintToolBarBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight(), this.toolBar.getOrientation());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintToolBarBorder(synthContext, graphics, n, n2, n3, n4, this.toolBar.getOrientation());
    }
    
    @Override
    protected void setBorderToNonRollover(final Component component) {
    }
    
    @Override
    protected void setBorderToRollover(final Component component) {
    }
    
    @Override
    protected void setBorderToNormal(final Component component) {
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        if (this.handleIcon != null && this.toolBar.isFloatable()) {
            SynthIcon.paintIcon(this.handleIcon, synthContext, graphics, this.toolBar.getComponentOrientation().isLeftToRight() ? 0 : (this.toolBar.getWidth() - SynthIcon.getIconWidth(this.handleIcon, synthContext)), 0, SynthIcon.getIconWidth(this.handleIcon, synthContext), SynthIcon.getIconHeight(this.handleIcon, synthContext));
        }
        final SynthContext context = this.getContext(this.toolBar, Region.TOOL_BAR_CONTENT, this.contentStyle);
        this.paintContent(context, graphics, this.contentRect);
        context.dispose();
    }
    
    protected void paintContent(final SynthContext synthContext, final Graphics graphics, final Rectangle rectangle) {
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle);
        synthContext.getPainter().paintToolBarContentBackground(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this.toolBar.getOrientation());
        synthContext.getPainter().paintToolBarContentBorder(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this.toolBar.getOrientation());
    }
    
    @Override
    protected void paintDragWindow(final Graphics graphics) {
        final int width = this.dragWindow.getWidth();
        final int height = this.dragWindow.getHeight();
        final SynthContext context = this.getContext(this.toolBar, Region.TOOL_BAR_DRAG_WINDOW, this.dragWindowStyle);
        SynthLookAndFeel.updateSubregion(context, graphics, new Rectangle(0, 0, width, height));
        context.getPainter().paintToolBarDragWindowBackground(context, graphics, 0, 0, width, height, this.dragWindow.getOrientation());
        context.getPainter().paintToolBarDragWindowBorder(context, graphics, 0, 0, width, height, this.dragWindow.getOrientation());
        context.dispose();
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JToolBar)propertyChangeEvent.getSource());
        }
    }
    
    class SynthToolBarLayoutManager implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            final JToolBar toolBar = (JToolBar)container;
            final Insets insets = toolBar.getInsets();
            final Dimension dimension = new Dimension();
            final SynthContext context = SynthToolBarUI.this.getContext(toolBar);
            if (toolBar.getOrientation() == 0) {
                dimension.width = (toolBar.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, context) : 0);
                for (int i = 0; i < toolBar.getComponentCount(); ++i) {
                    final Component component = toolBar.getComponent(i);
                    if (component.isVisible()) {
                        final Dimension minimumSize = component.getMinimumSize();
                        final Dimension dimension2 = dimension;
                        dimension2.width += minimumSize.width;
                        dimension.height = Math.max(dimension.height, minimumSize.height);
                    }
                }
            }
            else {
                dimension.height = (toolBar.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, context) : 0);
                for (int j = 0; j < toolBar.getComponentCount(); ++j) {
                    final Component component2 = toolBar.getComponent(j);
                    if (component2.isVisible()) {
                        final Dimension minimumSize2 = component2.getMinimumSize();
                        dimension.width = Math.max(dimension.width, minimumSize2.width);
                        final Dimension dimension3 = dimension;
                        dimension3.height += minimumSize2.height;
                    }
                }
            }
            final Dimension dimension4 = dimension;
            dimension4.width += insets.left + insets.right;
            final Dimension dimension5 = dimension;
            dimension5.height += insets.top + insets.bottom;
            context.dispose();
            return dimension;
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            final JToolBar toolBar = (JToolBar)container;
            final Insets insets = toolBar.getInsets();
            final Dimension dimension = new Dimension();
            final SynthContext context = SynthToolBarUI.this.getContext(toolBar);
            if (toolBar.getOrientation() == 0) {
                dimension.width = (toolBar.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, context) : 0);
                for (int i = 0; i < toolBar.getComponentCount(); ++i) {
                    final Component component = toolBar.getComponent(i);
                    if (component.isVisible()) {
                        final Dimension preferredSize = component.getPreferredSize();
                        final Dimension dimension2 = dimension;
                        dimension2.width += preferredSize.width;
                        dimension.height = Math.max(dimension.height, preferredSize.height);
                    }
                }
            }
            else {
                dimension.height = (toolBar.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, context) : 0);
                for (int j = 0; j < toolBar.getComponentCount(); ++j) {
                    final Component component2 = toolBar.getComponent(j);
                    if (component2.isVisible()) {
                        final Dimension preferredSize2 = component2.getPreferredSize();
                        dimension.width = Math.max(dimension.width, preferredSize2.width);
                        final Dimension dimension3 = dimension;
                        dimension3.height += preferredSize2.height;
                    }
                }
            }
            final Dimension dimension4 = dimension;
            dimension4.width += insets.left + insets.right;
            final Dimension dimension5 = dimension;
            dimension5.height += insets.top + insets.bottom;
            context.dispose();
            return dimension;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final JToolBar toolBar = (JToolBar)container;
            final Insets insets = toolBar.getInsets();
            final boolean leftToRight = toolBar.getComponentOrientation().isLeftToRight();
            final SynthContext context = SynthToolBarUI.this.getContext(toolBar);
            int n = 0;
            for (int i = 0; i < toolBar.getComponentCount(); ++i) {
                if (this.isGlue(toolBar.getComponent(i))) {
                    ++n;
                }
            }
            if (toolBar.getOrientation() == 0) {
                final int n2 = toolBar.isFloatable() ? SynthIcon.getIconWidth(SynthToolBarUI.this.handleIcon, context) : 0;
                SynthToolBarUI.this.contentRect.x = (leftToRight ? n2 : false);
                SynthToolBarUI.this.contentRect.y = 0;
                SynthToolBarUI.this.contentRect.width = toolBar.getWidth() - n2;
                SynthToolBarUI.this.contentRect.height = toolBar.getHeight();
                int n3 = leftToRight ? (n2 + insets.left) : (toolBar.getWidth() - n2 - insets.right);
                final int top = insets.top;
                final int n4 = toolBar.getHeight() - insets.top - insets.bottom;
                int n5 = 0;
                if (n > 0) {
                    n5 = (toolBar.getWidth() - this.minimumLayoutSize(container).width) / n;
                    if (n5 < 0) {
                        n5 = 0;
                    }
                }
                for (int j = 0; j < toolBar.getComponentCount(); ++j) {
                    final Component component = toolBar.getComponent(j);
                    if (component.isVisible()) {
                        final Dimension preferredSize = component.getPreferredSize();
                        int n6;
                        int height;
                        if (preferredSize.height >= n4 || component instanceof JSeparator) {
                            n6 = top;
                            height = n4;
                        }
                        else {
                            n6 = top + n4 / 2 - preferredSize.height / 2;
                            height = preferredSize.height;
                        }
                        if (this.isGlue(component)) {
                            final Dimension dimension = preferredSize;
                            dimension.width += n5;
                        }
                        component.setBounds(leftToRight ? n3 : (n3 - preferredSize.width), n6, preferredSize.width, height);
                        n3 = (leftToRight ? (n3 + preferredSize.width) : (n3 - preferredSize.width));
                    }
                }
            }
            else {
                final int y = toolBar.isFloatable() ? SynthIcon.getIconHeight(SynthToolBarUI.this.handleIcon, context) : 0;
                SynthToolBarUI.this.contentRect.x = 0;
                SynthToolBarUI.this.contentRect.y = y;
                SynthToolBarUI.this.contentRect.width = toolBar.getWidth();
                SynthToolBarUI.this.contentRect.height = toolBar.getHeight() - y;
                final int left = insets.left;
                final int n7 = toolBar.getWidth() - insets.left - insets.right;
                int n8 = y + insets.top;
                int n9 = 0;
                if (n > 0) {
                    n9 = (toolBar.getHeight() - this.minimumLayoutSize(container).height) / n;
                    if (n9 < 0) {
                        n9 = 0;
                    }
                }
                for (int k = 0; k < toolBar.getComponentCount(); ++k) {
                    final Component component2 = toolBar.getComponent(k);
                    if (component2.isVisible()) {
                        final Dimension preferredSize2 = component2.getPreferredSize();
                        int n10;
                        int width;
                        if (preferredSize2.width >= n7 || component2 instanceof JSeparator) {
                            n10 = left;
                            width = n7;
                        }
                        else {
                            n10 = left + n7 / 2 - preferredSize2.width / 2;
                            width = preferredSize2.width;
                        }
                        if (this.isGlue(component2)) {
                            final Dimension dimension2 = preferredSize2;
                            dimension2.height += n9;
                        }
                        component2.setBounds(n10, n8, width, preferredSize2.height);
                        n8 += preferredSize2.height;
                    }
                }
            }
            context.dispose();
        }
        
        private boolean isGlue(final Component component) {
            if (component.isVisible() && component instanceof Box.Filler) {
                final Box.Filler filler = (Box.Filler)component;
                final Dimension minimumSize = filler.getMinimumSize();
                final Dimension preferredSize = filler.getPreferredSize();
                return minimumSize.width == 0 && minimumSize.height == 0 && preferredSize.width == 0 && preferredSize.height == 0;
            }
            return false;
        }
    }
}
