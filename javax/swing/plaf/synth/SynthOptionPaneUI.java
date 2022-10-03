package javax.swing.plaf.synth;

import javax.swing.JSeparator;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import sun.swing.DefaultLookup;
import javax.swing.JOptionPane;
import java.beans.PropertyChangeEvent;
import java.awt.Graphics;
import java.awt.Container;
import javax.swing.Box;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class SynthOptionPaneUI extends BasicOptionPaneUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthOptionPaneUI();
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.optionPane);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.optionPane.addPropertyChangeListener(this);
    }
    
    private void updateStyle(final JComponent component) {
        final SynthContext context = this.getContext(component, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.minimumSize = (Dimension)this.style.get(context, "OptionPane.minimumSize");
            if (this.minimumSize == null) {
                this.minimumSize = new Dimension(262, 90);
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
        final SynthContext context = this.getContext(this.optionPane, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.optionPane.removePropertyChangeListener(this);
    }
    
    @Override
    protected void installComponents() {
        this.optionPane.add(this.createMessageArea());
        final Container separator = this.createSeparator();
        if (separator != null) {
            this.optionPane.add(separator);
            final SynthContext context = this.getContext(this.optionPane, 1);
            this.optionPane.add(Box.createVerticalStrut(context.getStyle().getInt(context, "OptionPane.separatorPadding", 6)));
            context.dispose();
        }
        this.optionPane.add(this.createButtonArea());
        this.optionPane.applyComponentOrientation(this.optionPane.getComponentOrientation());
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
        context.getPainter().paintOptionPaneBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
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
        synthContext.getPainter().paintOptionPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle((JComponent)propertyChangeEvent.getSource());
        }
    }
    
    @Override
    protected boolean getSizeButtonsToSameWidth() {
        return DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.sameSizeButtons", true);
    }
    
    @Override
    protected Container createMessageArea() {
        final JPanel panel = new JPanel();
        panel.setName("OptionPane.messageArea");
        panel.setLayout(new BorderLayout());
        final JPanel panel2 = new JPanel(new GridBagLayout());
        final JPanel panel3 = new JPanel(new BorderLayout());
        panel2.setName("OptionPane.body");
        panel3.setName("OptionPane.realBody");
        if (this.getIcon() != null) {
            final JPanel panel4 = new JPanel();
            panel4.setName("OptionPane.separator");
            panel4.setPreferredSize(new Dimension(15, 1));
            panel3.add(panel4, "Before");
        }
        panel3.add(panel2, "Center");
        final GridBagConstraints gridBagConstraints3;
        final GridBagConstraints gridBagConstraints2;
        final GridBagConstraints gridBagConstraints = gridBagConstraints2 = (gridBagConstraints3 = new GridBagConstraints());
        final int n = 0;
        gridBagConstraints2.gridy = n;
        gridBagConstraints3.gridx = n;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridheight = 1;
        final SynthContext context = this.getContext(this.optionPane, 1);
        gridBagConstraints.anchor = context.getStyle().getInt(context, "OptionPane.messageAnchor", 10);
        context.dispose();
        gridBagConstraints.insets = new Insets(0, 0, 3, 0);
        this.addMessageComponents(panel2, gridBagConstraints, this.getMessage(), this.getMaxCharactersPerLineCount(), false);
        panel.add(panel3, "Center");
        this.addIcon(panel);
        return panel;
    }
    
    @Override
    protected Container createSeparator() {
        final JSeparator separator = new JSeparator(0);
        separator.setName("OptionPane.separator");
        return separator;
    }
}
