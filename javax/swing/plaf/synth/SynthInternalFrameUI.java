package javax.swing.plaf.synth;

import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import javax.swing.JDesktopPane;
import java.awt.event.ComponentEvent;
import javax.swing.UIManager;
import java.awt.event.ComponentListener;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.plaf.UIResource;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class SynthInternalFrameUI extends BasicInternalFrameUI implements SynthUI, PropertyChangeListener
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthInternalFrameUI((JInternalFrame)component);
    }
    
    protected SynthInternalFrameUI(final JInternalFrame internalFrame) {
        super(internalFrame);
    }
    
    public void installDefaults() {
        this.frame.setLayout(this.internalFrameLayout = this.createLayoutManager());
        this.updateStyle(this.frame);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.frame.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallComponents() {
        if (this.frame.getComponentPopupMenu() instanceof UIResource) {
            this.frame.setComponentPopupMenu(null);
        }
        super.uninstallComponents();
    }
    
    @Override
    protected void uninstallListeners() {
        this.frame.removePropertyChangeListener(this);
        super.uninstallListeners();
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            final Icon frameIcon = this.frame.getFrameIcon();
            if (frameIcon == null || frameIcon instanceof UIResource) {
                this.frame.setFrameIcon(context.getStyle().getIcon(context, "InternalFrame.icon"));
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
        final SynthContext context = this.getContext(this.frame, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        if (this.frame.getLayout() == this.internalFrameLayout) {
            this.frame.setLayout(null);
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
        return SynthLookAndFeel.getComponentState(component);
    }
    
    @Override
    protected JComponent createNorthPane(final JInternalFrame internalFrame) {
        (this.titlePane = new SynthInternalFrameTitlePane(internalFrame)).setName("InternalFrame.northPane");
        return this.titlePane;
    }
    
    @Override
    protected ComponentListener createComponentListener() {
        if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
            return new ComponentHandler() {
                @Override
                public void componentResized(final ComponentEvent componentEvent) {
                    if (SynthInternalFrameUI.this.frame != null && SynthInternalFrameUI.this.frame.isMaximum()) {
                        final JDesktopPane desktopPane = (JDesktopPane)componentEvent.getSource();
                        for (final Component component : desktopPane.getComponents()) {
                            if (component instanceof SynthDesktopPaneUI.TaskBar) {
                                SynthInternalFrameUI.this.frame.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight() - component.getHeight());
                                SynthInternalFrameUI.this.frame.revalidate();
                                break;
                            }
                        }
                    }
                    final JInternalFrame access$400 = SynthInternalFrameUI.this.frame;
                    SynthInternalFrameUI.this.frame = null;
                    super.componentResized(componentEvent);
                    SynthInternalFrameUI.this.frame = access$400;
                }
            };
        }
        return super.createComponentListener();
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintInternalFrameBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        synthContext.getPainter().paintInternalFrameBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final SynthStyle style = this.style;
        final JInternalFrame internalFrame = (JInternalFrame)propertyChangeEvent.getSource();
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle(internalFrame);
        }
        if (this.style == style && (propertyName == "maximum" || propertyName == "selected")) {
            final SynthContext context = this.getContext(internalFrame, 1);
            this.style.uninstallDefaults(context);
            this.style.installDefaults(context, this);
        }
    }
}
