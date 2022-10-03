package com.adventnet.beans.rangenavigator;

import java.awt.Component;
import javax.swing.text.Document;
import java.awt.LayoutManager;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class LabeledNumericTextField extends JPanel
{
    private JTextField tf;
    private JLabel jl;
    private GridBagLayout gl;
    private GridBagConstraints cons;
    private Insets inset;
    private int fieldSize;
    
    public LabeledNumericTextField(final String label, final int fieldSize) {
        this.tf = null;
        this.jl = null;
        this.cons = null;
        this.inset = null;
        this.fieldSize = 3;
        this.fieldSize = fieldSize;
        this.setLayout(this.gl = new GridBagLayout());
        this.cons = new GridBagConstraints();
        this.inset = new Insets(5, 5, 5, 5);
        this.setLabeledNumericTextFieldUI();
        this.setLabel(label);
    }
    
    public LabeledNumericTextField(final String label, final long number, final int fieldSize) {
        this.tf = null;
        this.jl = null;
        this.cons = null;
        this.inset = null;
        this.fieldSize = 3;
        this.fieldSize = fieldSize;
        this.setLayout(this.gl = new GridBagLayout());
        this.cons = new GridBagConstraints();
        this.inset = new Insets(0, 0, 0, 5);
        this.setLabeledNumericTextFieldUI();
        this.setLabel(label);
        this.setNumber(number);
    }
    
    private void setLabeledNumericTextFieldUI() {
        (this.tf = new JTextField(this.fieldSize)).setMinimumSize(this.tf.getPreferredSize());
        this.tf.setDocument(new NumericTextField("0123456789"));
        this.tf.setHorizontalAlignment(4);
        this.jl = new JLabel();
        final int n = 0;
        final int n2 = 0;
        final int n3 = 1;
        final int n4 = 1;
        final double n5 = 0.0;
        final double n6 = 1.0;
        final GridBagConstraints cons = this.cons;
        final int n7 = 17;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(n, n2, n3, n4, n5, n6, n7, 0, this.inset, 0, 0);
        this.gl.setConstraints(this.jl, this.cons);
        this.add(this.jl);
        final int n8 = 1;
        final int n9 = 0;
        final int n10 = 1;
        final int n11 = 1;
        final double n12 = 0.1;
        final double n13 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int n14 = 17;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(n8, n9, n10, n11, n12, n13, n14, 0, this.inset, 0, 0);
        this.gl.setConstraints(this.tf, this.cons);
        this.add(this.tf);
    }
    
    private void setConstraints(final int gridx, final int gridy, final int gridwidth, final int gridheight, final double weightx, final double weighty, final int anchor, final int fill, final Insets insets, final int ipadx, final int ipady) {
        this.cons.gridx = gridx;
        this.cons.gridy = gridy;
        this.cons.gridwidth = gridwidth;
        this.cons.gridheight = gridheight;
        this.cons.weightx = weightx;
        this.cons.weighty = weighty;
        this.cons.anchor = anchor;
        this.cons.fill = fill;
        this.cons.insets = insets;
        this.cons.ipadx = ipadx;
        this.cons.ipady = ipady;
    }
    
    public void setLabel(final String text) {
        this.jl.setText(text);
    }
    
    public String getLabel() {
        return this.jl.getText();
    }
    
    public void setNumber(final long n) {
        this.tf.setText(n + "");
        this.tf.setCaretPosition(0);
    }
    
    public long getNumber() {
        final String trim = this.tf.getText().trim();
        long long1 = 0L;
        if (!trim.equals("")) {
            if (trim != null) {
                long1 = Long.parseLong(this.tf.getText());
            }
        }
        return long1;
    }
    
    public JTextField getField() {
        return this.tf;
    }
    
    public JLabel getLabelComponent() {
        return this.jl;
    }
}
