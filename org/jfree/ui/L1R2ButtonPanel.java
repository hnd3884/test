package org.jfree.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class L1R2ButtonPanel extends JPanel
{
    private JButton left;
    private JButton right1;
    private JButton right2;
    
    public L1R2ButtonPanel(final String label1, final String label2, final String label3) {
        this.setLayout(new BorderLayout());
        this.left = new JButton(label1);
        final JPanel rightButtonPanel = new JPanel(new GridLayout(1, 2));
        this.right1 = new JButton(label2);
        this.right2 = new JButton(label3);
        rightButtonPanel.add(this.right1);
        rightButtonPanel.add(this.right2);
        this.add(this.left, "West");
        this.add(rightButtonPanel, "East");
    }
    
    public JButton getLeftButton() {
        return this.left;
    }
    
    public JButton getRightButton1() {
        return this.right1;
    }
    
    public JButton getRightButton2() {
        return this.right2;
    }
}
