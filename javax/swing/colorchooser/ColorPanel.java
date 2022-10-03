package javax.swing.colorchooser;

import java.awt.Container;
import javax.swing.JSpinner;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import javax.swing.border.Border;
import java.awt.FocusTraversalPolicy;
import java.awt.ContainerOrderFocusTraversalPolicy;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.AbstractButton;
import java.awt.Component;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

final class ColorPanel extends JPanel implements ActionListener
{
    private final SlidingSpinner[] spinners;
    private final float[] values;
    private final ColorModel model;
    private Color color;
    private int x;
    private int y;
    private int z;
    
    ColorPanel(final ColorModel model) {
        super(new GridBagLayout());
        this.spinners = new SlidingSpinner[5];
        this.values = new float[this.spinners.length];
        this.x = 1;
        this.y = 2;
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.gridx = 1;
        final ButtonGroup buttonGroup = new ButtonGroup();
        Border border = null;
        for (int i = 0; i < this.spinners.length; ++i) {
            if (i < 3) {
                final JRadioButton radioButton = new JRadioButton();
                if (i == 0) {
                    final Insets insets = radioButton.getInsets();
                    insets.left = radioButton.getPreferredSize().width;
                    border = new EmptyBorder(insets);
                    radioButton.setSelected(true);
                    gridBagConstraints.insets.top = 5;
                }
                this.add(radioButton, gridBagConstraints);
                buttonGroup.add(radioButton);
                radioButton.setActionCommand(Integer.toString(i));
                radioButton.addActionListener(this);
                this.spinners[i] = new SlidingSpinner(this, radioButton);
            }
            else {
                final JLabel label = new JLabel();
                this.add(label, gridBagConstraints);
                label.setBorder(border);
                label.setFocusable(false);
                this.spinners[i] = new SlidingSpinner(this, label);
            }
        }
        gridBagConstraints.gridx = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets.top = 0;
        gridBagConstraints.insets.left = 5;
        final SlidingSpinner[] spinners = this.spinners;
        for (int length = spinners.length, j = 0; j < length; ++j) {
            this.add(spinners[j].getSlider(), gridBagConstraints);
            gridBagConstraints.insets.top = 5;
        }
        gridBagConstraints.gridx = 3;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets.top = 0;
        final SlidingSpinner[] spinners2 = this.spinners;
        for (int length2 = spinners2.length, k = 0; k < length2; ++k) {
            this.add(spinners2[k].getSpinner(), gridBagConstraints);
            gridBagConstraints.insets.top = 5;
        }
        this.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
        this.setFocusTraversalPolicyProvider(true);
        this.setFocusable(false);
        this.model = model;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        try {
            this.z = Integer.parseInt(actionEvent.getActionCommand());
            this.y = ((this.z != 2) ? 2 : 1);
            this.x = ((this.z == 0) ? 1 : 0);
            this.getParent().repaint();
        }
        catch (final NumberFormatException ex) {}
    }
    
    void buildPanel() {
        final int count = this.model.getCount();
        this.spinners[4].setVisible(count > 4);
        for (int i = 0; i < count; ++i) {
            final String label = this.model.getLabel(this, i);
            final JComponent label2 = this.spinners[i].getLabel();
            if (label2 instanceof JRadioButton) {
                final JRadioButton radioButton = (JRadioButton)label2;
                radioButton.setText(label);
                radioButton.getAccessibleContext().setAccessibleDescription(label);
            }
            else if (label2 instanceof JLabel) {
                ((JLabel)label2).setText(label);
            }
            this.spinners[i].setRange(this.model.getMinimum(i), this.model.getMaximum(i));
            this.spinners[i].setValue(this.values[i]);
            this.spinners[i].getSlider().getAccessibleContext().setAccessibleName(label);
            this.spinners[i].getSpinner().getAccessibleContext().setAccessibleName(label);
            final JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)this.spinners[i].getSpinner().getEditor();
            defaultEditor.getTextField().getAccessibleContext().setAccessibleName(label);
            this.spinners[i].getSlider().getAccessibleContext().setAccessibleDescription(label);
            this.spinners[i].getSpinner().getAccessibleContext().setAccessibleDescription(label);
            defaultEditor.getTextField().getAccessibleContext().setAccessibleDescription(label);
        }
    }
    
    void colorChanged() {
        this.color = new Color(this.getColor(0), true);
        final Container parent = this.getParent();
        if (parent instanceof ColorChooserPanel) {
            final ColorChooserPanel colorChooserPanel = (ColorChooserPanel)parent;
            colorChooserPanel.setSelectedColor(this.color);
            colorChooserPanel.repaint();
        }
    }
    
    float getValueX() {
        return this.spinners[this.x].getValue();
    }
    
    float getValueY() {
        return 1.0f - this.spinners[this.y].getValue();
    }
    
    float getValueZ() {
        return 1.0f - this.spinners[this.z].getValue();
    }
    
    void setValue(final float n) {
        this.spinners[this.z].setValue(1.0f - n);
        this.colorChanged();
    }
    
    void setValue(final float value, final float n) {
        this.spinners[this.x].setValue(value);
        this.spinners[this.y].setValue(1.0f - n);
        this.colorChanged();
    }
    
    int getColor(final float n) {
        this.setDefaultValue(this.x);
        this.setDefaultValue(this.y);
        this.values[this.z] = 1.0f - n;
        return this.getColor(3);
    }
    
    int getColor(final float n, final float n2) {
        this.values[this.x] = n;
        this.values[this.y] = 1.0f - n2;
        this.setValue(this.z);
        return this.getColor(3);
    }
    
    void setColor(final Color color) {
        if (!color.equals(this.color)) {
            this.color = color;
            this.model.setColor(color.getRGB(), this.values);
            for (int i = 0; i < this.model.getCount(); ++i) {
                this.spinners[i].setValue(this.values[i]);
            }
        }
    }
    
    private int getColor(int i) {
        while (i < this.model.getCount()) {
            this.setValue(i++);
        }
        return this.model.getColor(this.values);
    }
    
    private void setValue(final int n) {
        this.values[n] = this.spinners[n].getValue();
    }
    
    private void setDefaultValue(final int n) {
        final float default1 = this.model.getDefault(n);
        this.values[n] = ((default1 < 0.0f) ? this.spinners[n].getValue() : default1);
    }
}
