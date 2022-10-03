package javax.swing.plaf.synth;

import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ComponentEvent;
import javax.swing.border.Border;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import java.awt.LayoutManager;
import java.awt.Insets;
import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import javax.swing.plaf.UIResource;
import java.awt.Dimension;
import javax.swing.JDesktopPane;
import javax.swing.JLayeredPane;
import java.awt.Component;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import java.awt.event.ContainerListener;
import java.awt.event.ComponentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.DesktopManager;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class SynthDesktopPaneUI extends BasicDesktopPaneUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private TaskBar taskBar;
    private DesktopManager oldDesktopManager;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthDesktopPaneUI();
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.desktop.addPropertyChangeListener(this);
        if (this.taskBar != null) {
            this.desktop.addComponentListener(this.taskBar);
            this.desktop.addContainerListener(this.taskBar);
        }
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.desktop);
        if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
            this.taskBar = new TaskBar();
            for (final Component component : this.desktop.getComponents()) {
                Label_0141: {
                    JInternalFrame.JDesktopIcon desktopIcon;
                    if (component instanceof JInternalFrame.JDesktopIcon) {
                        desktopIcon = (JInternalFrame.JDesktopIcon)component;
                    }
                    else {
                        if (!(component instanceof JInternalFrame)) {
                            break Label_0141;
                        }
                        desktopIcon = ((JInternalFrame)component).getDesktopIcon();
                    }
                    if (desktopIcon.getParent() == this.desktop) {
                        this.desktop.remove(desktopIcon);
                    }
                    if (desktopIcon.getParent() != this.taskBar) {
                        this.taskBar.add(desktopIcon);
                        desktopIcon.getInternalFrame().addComponentListener(this.taskBar);
                    }
                }
            }
            this.taskBar.setBackground(this.desktop.getBackground());
            this.desktop.add(this.taskBar, (Object)(JLayeredPane.PALETTE_LAYER + 1));
            if (this.desktop.isShowing()) {
                this.taskBar.adjustSize();
            }
        }
    }
    
    private void updateStyle(final JDesktopPane desktopPane) {
        final SynthStyle style = this.style;
        final SynthContext context = this.getContext(desktopPane, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (style != null) {
            this.uninstallKeyboardActions();
            this.installKeyboardActions();
        }
        context.dispose();
    }
    
    @Override
    protected void uninstallListeners() {
        if (this.taskBar != null) {
            this.desktop.removeComponentListener(this.taskBar);
            this.desktop.removeContainerListener(this.taskBar);
        }
        this.desktop.removePropertyChangeListener(this);
        super.uninstallListeners();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.desktop, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        if (this.taskBar != null) {
            final Component[] components = this.taskBar.getComponents();
            for (int length = components.length, i = 0; i < length; ++i) {
                final JInternalFrame.JDesktopIcon desktopIcon = (JInternalFrame.JDesktopIcon)components[i];
                this.taskBar.remove(desktopIcon);
                desktopIcon.setPreferredSize(null);
                final JInternalFrame internalFrame = desktopIcon.getInternalFrame();
                if (internalFrame.isIcon()) {
                    this.desktop.add(desktopIcon);
                }
                internalFrame.removeComponentListener(this.taskBar);
            }
            this.desktop.remove(this.taskBar);
            this.taskBar = null;
        }
    }
    
    @Override
    protected void installDesktopManager() {
        if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
            final DesktopManager desktopManager = this.desktop.getDesktopManager();
            this.oldDesktopManager = desktopManager;
            this.desktopManager = desktopManager;
            if (!(this.desktopManager instanceof SynthDesktopManager)) {
                this.desktopManager = new SynthDesktopManager();
                this.desktop.setDesktopManager(this.desktopManager);
            }
        }
        else {
            super.installDesktopManager();
        }
    }
    
    @Override
    protected void uninstallDesktopManager() {
        if (this.oldDesktopManager != null && !(this.oldDesktopManager instanceof UIResource)) {
            this.desktopManager = this.desktop.getDesktopManager();
            if (this.desktopManager == null || this.desktopManager instanceof UIResource) {
                this.desktop.setDesktopManager(this.oldDesktopManager);
            }
        }
        this.oldDesktopManager = null;
        super.uninstallDesktopManager();
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
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintDesktopPaneBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintDesktopPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JDesktopPane)propertyChangeEvent.getSource());
        }
        if (propertyChangeEvent.getPropertyName() == "ancestor" && this.taskBar != null) {
            this.taskBar.adjustSize();
        }
    }
    
    static class TaskBar extends JPanel implements ComponentListener, ContainerListener
    {
        TaskBar() {
            this.setOpaque(true);
            this.setLayout(new FlowLayout(0, 0, 0) {
                @Override
                public void layoutContainer(final Container container) {
                    final Component[] components = container.getComponents();
                    final int length = components.length;
                    if (length > 0) {
                        int width = 0;
                        for (final Component component : components) {
                            component.setPreferredSize(null);
                            final Dimension preferredSize = component.getPreferredSize();
                            if (preferredSize.width > width) {
                                width = preferredSize.width;
                            }
                        }
                        final Insets insets = container.getInsets();
                        final int min = Math.min(width, Math.max(10, (container.getWidth() - insets.left - insets.right) / length));
                        for (final Component component2 : components) {
                            component2.setPreferredSize(new Dimension(min, component2.getPreferredSize().height));
                        }
                    }
                    super.layoutContainer(container);
                }
            });
            this.setBorder(new BevelBorder(0) {
                @Override
                protected void paintRaisedBevel(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
                    final Color color = graphics.getColor();
                    graphics.translate(n, n2);
                    graphics.setColor(this.getHighlightOuterColor(component));
                    graphics.drawLine(0, 0, 0, n4 - 2);
                    graphics.drawLine(1, 0, n3 - 2, 0);
                    graphics.setColor(this.getShadowOuterColor(component));
                    graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
                    graphics.drawLine(n3 - 1, 0, n3 - 1, n4 - 2);
                    graphics.translate(-n, -n2);
                    graphics.setColor(color);
                }
            });
        }
        
        void adjustSize() {
            final JDesktopPane desktopPane = (JDesktopPane)this.getParent();
            if (desktopPane != null) {
                int n = this.getPreferredSize().height;
                final Insets insets = this.getInsets();
                if (n == insets.top + insets.bottom) {
                    if (this.getHeight() <= n) {
                        n += 21;
                    }
                    else {
                        n = this.getHeight();
                    }
                }
                this.setBounds(0, desktopPane.getHeight() - n, desktopPane.getWidth(), n);
                this.revalidate();
                this.repaint();
            }
        }
        
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            if (componentEvent.getSource() instanceof JDesktopPane) {
                this.adjustSize();
            }
        }
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void componentShown(final ComponentEvent componentEvent) {
            if (componentEvent.getSource() instanceof JInternalFrame) {
                this.adjustSize();
            }
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
            if (componentEvent.getSource() instanceof JInternalFrame) {
                ((JInternalFrame)componentEvent.getSource()).getDesktopIcon().setVisible(false);
                this.revalidate();
            }
        }
        
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            if (containerEvent.getChild() instanceof JInternalFrame) {
                final JDesktopPane desktopPane = (JDesktopPane)containerEvent.getSource();
                final JInternalFrame internalFrame = (JInternalFrame)containerEvent.getChild();
                final JInternalFrame.JDesktopIcon desktopIcon = internalFrame.getDesktopIcon();
                final Component[] components = this.getComponents();
                for (int length = components.length, i = 0; i < length; ++i) {
                    if (components[i] == desktopIcon) {
                        return;
                    }
                }
                this.add(desktopIcon);
                internalFrame.addComponentListener(this);
                if (this.getComponentCount() == 1) {
                    this.adjustSize();
                }
            }
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            if (containerEvent.getChild() instanceof JInternalFrame) {
                final JInternalFrame internalFrame = (JInternalFrame)containerEvent.getChild();
                if (!internalFrame.isIcon()) {
                    this.remove(internalFrame.getDesktopIcon());
                    internalFrame.removeComponentListener(this);
                    this.revalidate();
                    this.repaint();
                }
            }
        }
    }
    
    class SynthDesktopManager extends DefaultDesktopManager implements UIResource
    {
        @Override
        public void maximizeFrame(final JInternalFrame internalFrame) {
            if (internalFrame.isIcon()) {
                try {
                    internalFrame.setIcon(false);
                }
                catch (final PropertyVetoException ex) {}
            }
            else {
                internalFrame.setNormalBounds(internalFrame.getBounds());
                final Container parent = internalFrame.getParent();
                this.setBoundsForFrame(internalFrame, 0, 0, parent.getWidth(), parent.getHeight() - SynthDesktopPaneUI.this.taskBar.getHeight());
            }
            try {
                internalFrame.setSelected(true);
            }
            catch (final PropertyVetoException ex2) {}
        }
        
        @Override
        public void iconifyFrame(final JInternalFrame internalFrame) {
            final Container parent = internalFrame.getParent();
            internalFrame.getDesktopPane();
            final boolean selected = internalFrame.isSelected();
            if (parent == null) {
                return;
            }
            internalFrame.getDesktopIcon();
            if (!internalFrame.isMaximum()) {
                internalFrame.setNormalBounds(internalFrame.getBounds());
            }
            parent.remove(internalFrame);
            parent.repaint(internalFrame.getX(), internalFrame.getY(), internalFrame.getWidth(), internalFrame.getHeight());
            try {
                internalFrame.setSelected(false);
            }
            catch (final PropertyVetoException ex) {}
            if (selected) {
                for (final Component component : parent.getComponents()) {
                    if (component instanceof JInternalFrame) {
                        try {
                            ((JInternalFrame)component).setSelected(true);
                        }
                        catch (final PropertyVetoException ex2) {}
                        ((JInternalFrame)component).moveToFront();
                        return;
                    }
                }
            }
        }
        
        @Override
        public void deiconifyFrame(final JInternalFrame internalFrame) {
            final Container parent = internalFrame.getDesktopIcon().getParent();
            if (parent != null) {
                final Container parent2 = parent.getParent();
                if (parent2 != null) {
                    parent2.add(internalFrame);
                    if (internalFrame.isMaximum()) {
                        final int width = parent2.getWidth();
                        final int n = parent2.getHeight() - SynthDesktopPaneUI.this.taskBar.getHeight();
                        if (internalFrame.getWidth() != width || internalFrame.getHeight() != n) {
                            this.setBoundsForFrame(internalFrame, 0, 0, width, n);
                        }
                    }
                    if (internalFrame.isSelected()) {
                        internalFrame.moveToFront();
                    }
                    else {
                        try {
                            internalFrame.setSelected(true);
                        }
                        catch (final PropertyVetoException ex) {}
                    }
                }
            }
        }
        
        @Override
        protected void removeIconFor(final JInternalFrame internalFrame) {
            super.removeIconFor(internalFrame);
            SynthDesktopPaneUI.this.taskBar.validate();
        }
        
        @Override
        public void setBoundsForFrame(final JComponent component, final int n, final int n2, final int n3, final int n4) {
            super.setBoundsForFrame(component, n, n2, n3, n4);
            if (SynthDesktopPaneUI.this.taskBar != null && n2 >= SynthDesktopPaneUI.this.taskBar.getY()) {
                component.setLocation(component.getX(), SynthDesktopPaneUI.this.taskBar.getY() - component.getInsets().top);
            }
        }
    }
}
