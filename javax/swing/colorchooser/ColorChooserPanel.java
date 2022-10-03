package javax.swing.colorchooser;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import java.beans.PropertyChangeListener;

final class ColorChooserPanel extends AbstractColorChooserPanel implements PropertyChangeListener
{
    private static final int MASK = -16777216;
    private final ColorModel model;
    private final ColorPanel panel;
    private final DiagramComponent slider;
    private final DiagramComponent diagram;
    private final JFormattedTextField text;
    private final JLabel label;
    
    ColorChooserPanel(final ColorModel model) {
        this.model = model;
        this.panel = new ColorPanel(this.model);
        this.slider = new DiagramComponent(this.panel, false);
        this.diagram = new DiagramComponent(this.panel, true);
        this.text = new JFormattedTextField();
        this.label = new JLabel(null, null, 4);
        ValueFormatter.init(6, true, this.text);
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        setEnabled(this, enabled);
    }
    
    private static void setEnabled(final Container container, final boolean enabled) {
        for (final Component component : container.getComponents()) {
            component.setEnabled(enabled);
            if (component instanceof Container) {
                setEnabled((Container)component, enabled);
            }
        }
    }
    
    @Override
    public void updateChooser() {
        final Color colorFromModel = this.getColorFromModel();
        if (colorFromModel != null) {
            this.panel.setColor(colorFromModel);
            this.text.setValue(colorFromModel.getRGB());
            this.slider.repaint();
            this.diagram.repaint();
        }
    }
    
    @Override
    protected void buildChooser() {
        if (0 == this.getComponentCount()) {
            this.setLayout(new GridBagLayout());
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.anchor = 11;
            gridBagConstraints.fill = 2;
            gridBagConstraints.insets.top = 10;
            gridBagConstraints.insets.right = 10;
            this.add(this.panel, gridBagConstraints);
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.0;
            gridBagConstraints.anchor = 10;
            gridBagConstraints.insets.right = 5;
            gridBagConstraints.insets.bottom = 10;
            this.add(this.label, gridBagConstraints);
            gridBagConstraints.gridx = 4;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets.right = 10;
            this.add(this.text, gridBagConstraints);
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.anchor = 11;
            gridBagConstraints.ipadx = this.text.getPreferredSize().height;
            gridBagConstraints.ipady = this.getPreferredSize().height;
            this.add(this.slider, gridBagConstraints);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.insets.left = 10;
            gridBagConstraints.ipadx = gridBagConstraints.ipady;
            this.add(this.diagram, gridBagConstraints);
            this.label.setLabelFor(this.text);
            this.text.addPropertyChangeListener("value", this);
            this.slider.setBorder(this.text.getBorder());
            this.diagram.setBorder(this.text.getBorder());
            setInheritsPopupMenu(this, true);
        }
        final String text = this.model.getText(this, "HexCode");
        final boolean b = text != null;
        this.text.setVisible(b);
        this.text.getAccessibleContext().setAccessibleDescription(text);
        this.label.setVisible(b);
        if (b) {
            this.label.setText(text);
            final int integer = this.model.getInteger(this, "HexCodeMnemonic");
            if (integer > 0) {
                this.label.setDisplayedMnemonic(integer);
                final int integer2 = this.model.getInteger(this, "HexCodeMnemonicIndex");
                if (integer2 >= 0) {
                    this.label.setDisplayedMnemonicIndex(integer2);
                }
            }
        }
        this.panel.buildPanel();
    }
    
    @Override
    public String getDisplayName() {
        return this.model.getText(this, "Name");
    }
    
    @Override
    public int getMnemonic() {
        return this.model.getInteger(this, "Mnemonic");
    }
    
    @Override
    public int getDisplayedMnemonicIndex() {
        return this.model.getInteger(this, "DisplayedMnemonicIndex");
    }
    
    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }
    
    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final ColorSelectionModel colorSelectionModel = this.getColorSelectionModel();
        if (colorSelectionModel != null) {
            final Object newValue = propertyChangeEvent.getNewValue();
            if (newValue instanceof Integer) {
                colorSelectionModel.setSelectedColor(new Color((0xFF000000 & colorSelectionModel.getSelectedColor().getRGB()) | (int)newValue, true));
            }
        }
        this.text.selectAll();
    }
    
    private static void setInheritsPopupMenu(final JComponent component, final boolean inheritsPopupMenu) {
        component.setInheritsPopupMenu(inheritsPopupMenu);
        for (final Component component2 : component.getComponents()) {
            if (component2 instanceof JComponent) {
                setInheritsPopupMenu((JComponent)component2, inheritsPopupMenu);
            }
        }
    }
}
