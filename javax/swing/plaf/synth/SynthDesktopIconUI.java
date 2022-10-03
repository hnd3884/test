package javax.swing.plaf.synth;

import java.beans.PropertyVetoException;
import java.awt.event.ActionEvent;
import javax.swing.JInternalFrame;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.ToolTipManager;
import javax.swing.JPopupMenu;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class SynthDesktopIconUI extends BasicDesktopIconUI implements SynthUI, PropertyChangeListener
{
    private SynthStyle style;
    private Handler handler;
    
    public SynthDesktopIconUI() {
        this.handler = new Handler();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthDesktopIconUI();
    }
    
    @Override
    protected void installComponents() {
        if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
            this.iconPane = new JToggleButton(this.frame.getTitle(), this.frame.getFrameIcon()) {
                @Override
                public String getToolTipText() {
                    return this.getText();
                }
                
                @Override
                public JPopupMenu getComponentPopupMenu() {
                    return SynthDesktopIconUI.this.frame.getComponentPopupMenu();
                }
            };
            ToolTipManager.sharedInstance().registerComponent(this.iconPane);
            this.iconPane.setFont(this.desktopIcon.getFont());
            this.iconPane.setBackground(this.desktopIcon.getBackground());
            this.iconPane.setForeground(this.desktopIcon.getForeground());
        }
        else {
            (this.iconPane = new SynthInternalFrameTitlePane(this.frame)).setName("InternalFrame.northPane");
        }
        this.desktopIcon.setLayout(new BorderLayout());
        this.desktopIcon.add(this.iconPane, "Center");
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.desktopIcon.addPropertyChangeListener(this);
        if (this.iconPane instanceof JToggleButton) {
            this.frame.addPropertyChangeListener(this);
            ((JToggleButton)this.iconPane).addActionListener(this.handler);
        }
    }
    
    @Override
    protected void uninstallListeners() {
        if (this.iconPane instanceof JToggleButton) {
            ((JToggleButton)this.iconPane).removeActionListener(this.handler);
            this.frame.removePropertyChangeListener(this);
        }
        this.desktopIcon.removePropertyChangeListener(this);
        super.uninstallListeners();
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.desktopIcon);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.desktopIcon, 1);
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
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintDesktopIconBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        synthContext.getPainter().paintDesktopIconBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getSource() instanceof JInternalFrame.JDesktopIcon) {
            if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
                this.updateStyle((JComponent)propertyChangeEvent.getSource());
            }
        }
        else if (propertyChangeEvent.getSource() instanceof JInternalFrame) {
            final JInternalFrame internalFrame = (JInternalFrame)propertyChangeEvent.getSource();
            if (this.iconPane instanceof JToggleButton) {
                final JToggleButton toggleButton = (JToggleButton)this.iconPane;
                final String propertyName = propertyChangeEvent.getPropertyName();
                if (propertyName == "title") {
                    toggleButton.setText((String)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "frameIcon") {
                    toggleButton.setIcon((Icon)propertyChangeEvent.getNewValue());
                }
                else if (propertyName == "icon" || propertyName == "selected") {
                    toggleButton.setSelected(!internalFrame.isIcon() && internalFrame.isSelected());
                }
            }
        }
    }
    
    private final class Handler implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (actionEvent.getSource() instanceof JToggleButton) {
                final JToggleButton toggleButton = (JToggleButton)actionEvent.getSource();
                try {
                    final boolean selected = toggleButton.isSelected();
                    if (!selected && !SynthDesktopIconUI.this.frame.isIconifiable()) {
                        toggleButton.setSelected(true);
                    }
                    else {
                        SynthDesktopIconUI.this.frame.setIcon(!selected);
                        if (selected) {
                            SynthDesktopIconUI.this.frame.setSelected(true);
                        }
                    }
                }
                catch (final PropertyVetoException ex) {}
            }
        }
    }
}
