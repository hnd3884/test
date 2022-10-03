package com.adventnet.beans.rangenavigator;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public class RangePanel extends JPanel
{
    private LabeledNumericTextField first;
    private LabeledNumericTextField second;
    private GridBagLayout gl;
    private GridBagConstraints cons;
    private Insets inset;
    
    public RangePanel(final String s, final long n, final String s2, final long n2, final int n3, final int n4) {
        this.first = new LabeledNumericTextField(s, n, n3);
        this.second = new LabeledNumericTextField(s2, n2, n4);
        this.setRangePanelUI();
    }
    
    private void setRangePanelUI() {
        this.setLayout(this.gl = new GridBagLayout());
        this.cons = new GridBagConstraints();
        this.inset = new Insets(0, 5, 0, 5);
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
        this.gl.setConstraints(this.first, this.cons);
        this.add(this.first);
        this.inset = new Insets(0, 0, 0, 5);
        final int n8 = 1;
        final int n9 = 0;
        final int n10 = 1;
        final int n11 = 1;
        final double n12 = 0.1;
        final double n13 = 1.0;
        final GridBagConstraints cons3 = this.cons;
        final int n14 = 17;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(n8, n9, n10, n11, n12, n13, n14, 0, this.inset, 0, 0);
        this.gl.setConstraints(this.second, this.cons);
        this.add(this.second);
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
    
    public LabeledNumericTextField getFirstComponent() {
        return this.first;
    }
    
    public LabeledNumericTextField getSecondComponent() {
        return this.second;
    }
    
    public void setFirstText(final String label) {
        this.first.setLabel(label);
    }
    
    public void setSecondText(final String label) {
        this.second.setLabel(label);
    }
    
    public String getFirstText() {
        return this.first.getLabel();
    }
    
    public String getSecondText() {
        return this.second.getLabel();
    }
    
    public void setFirstValue(final int n) {
        this.first.setNumber(n);
    }
    
    public void setSecondValue(final int n) {
        this.second.setNumber(n);
    }
}
