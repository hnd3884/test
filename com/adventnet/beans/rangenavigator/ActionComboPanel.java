package com.adventnet.beans.rangenavigator;

import java.util.Vector;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;
import java.awt.Dimension;
import javax.swing.JToggleButton;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ActionComboPanel extends JPanel
{
    private JComboBox actionCombo;
    private JLabel actionLabel;
    
    public ActionComboPanel() {
        this.initComponents();
    }
    
    private void initComponents() {
        this.actionLabel = new JLabel();
        this.actionCombo = new JComboBox();
        this.setLayout(new GridBagLayout());
        this.actionLabel.setText("Actions");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weighty = 0.1;
        this.add(this.actionLabel, gridBagConstraints);
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = 2;
        gridBagConstraints2.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints2.anchor = 17;
        gridBagConstraints2.weightx = 0.1;
        this.add(this.actionCombo, gridBagConstraints2);
    }
    
    public String getActionLabel() {
        return this.actionLabel.getText();
    }
    
    public JLabel getActionLabelComponent() {
        return this.actionLabel;
    }
    
    public void setActionLabel(final String text) {
        this.actionLabel.setText(text);
    }
    
    JComboBox getActionCombo() {
        return this.actionCombo;
    }
    
    public void addActionComboButtonItems(final JToggleButton toggleButton) {
        final ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(30, 30));
        this.actionCombo.setRenderer(renderer);
        this.actionCombo.setMaximumRowCount(8);
        if (toggleButton != null) {
            this.actionCombo.addItem(toggleButton);
        }
    }
    
    public void addActionComboStringItems(String[] removeDuplicates) {
        if (removeDuplicates != null) {
            this.actionCombo.setRenderer(new DefaultListCellRenderer());
            this.actionCombo.setMaximumRowCount(3);
            for (int i = 0; i < removeDuplicates.length; ++i) {
                if (removeDuplicates[i] == null) {
                    removeDuplicates[i] = "";
                }
            }
            removeDuplicates = this.removeDuplicates(removeDuplicates);
            for (int j = 0; j < removeDuplicates.length; ++j) {
                this.actionCombo.addItem(removeDuplicates[j]);
            }
        }
    }
    
    private String[] removeDuplicates(final String[] array) {
        final Vector vector = new Vector();
        for (int i = 0; i < array.length; ++i) {
            if (!vector.contains(array[i].trim())) {
                vector.add(array[i]);
            }
        }
        final Object[] array2 = vector.toArray();
        final String[] array3 = new String[array2.length];
        for (int j = 0; j < array2.length; ++j) {
            array3[j] = array2[j].toString();
        }
        return array3;
    }
}
