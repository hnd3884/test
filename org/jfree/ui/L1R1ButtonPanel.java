package org.jfree.ui;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class L1R1ButtonPanel extends JPanel
{
    private JButton left;
    private JButton right;
    
    public L1R1ButtonPanel(final String leftLabel, final String rightLabel) {
        this.setLayout(new BorderLayout());
        this.left = new JButton(leftLabel);
        this.right = new JButton(rightLabel);
        this.add(this.left, "West");
        this.add(this.right, "East");
    }
    
    public JButton getLeftButton() {
        return this.left;
    }
    
    public JButton getRightButton() {
        return this.right;
    }
}
