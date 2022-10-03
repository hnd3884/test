package org.jfree.ui;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class L1R3ButtonPanel extends JPanel
{
    private JButton left;
    private JButton right1;
    private JButton right2;
    private JButton right3;
    
    public L1R3ButtonPanel(final String label1, final String label2, final String label3, final String label4) {
        this.setLayout(new BorderLayout());
        final JPanel panel = new JPanel(new BorderLayout());
        final JPanel panel2 = new JPanel(new BorderLayout());
        this.left = new JButton(label1);
        this.right1 = new JButton(label2);
        this.right2 = new JButton(label3);
        this.right3 = new JButton(label4);
        panel.add(this.left, "West");
        panel2.add(this.right1, "East");
        panel.add(panel2, "Center");
        panel.add(this.right2, "East");
        this.add(panel, "Center");
        this.add(this.right3, "East");
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
    
    public JButton getRightButton3() {
        return this.right3;
    }
}
