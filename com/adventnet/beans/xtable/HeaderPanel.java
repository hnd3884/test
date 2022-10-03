package com.adventnet.beans.xtable;

import java.awt.Insets;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class HeaderPanel extends JPanel
{
    JLabel modelSort;
    JLabel header;
    JLabel viewSort;
    
    public HeaderPanel() {
        this.initComponents();
    }
    
    private void initComponents() {
        this.header = new JLabel();
        this.modelSort = new JLabel();
        this.viewSort = new JLabel();
        this.setLayout(new GridBagLayout());
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.weightx = 0.1;
        this.add(this.header, gridBagConstraints);
        this.modelSort.setIconTextGap(0);
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.anchor = 13;
        this.add(this.modelSort, gridBagConstraints2);
        this.viewSort.setIconTextGap(0);
        final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.anchor = 13;
        gridBagConstraints3.insets = new Insets(0, 2, 0, 0);
        this.add(this.viewSort, gridBagConstraints3);
    }
}
