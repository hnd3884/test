package org.jfree.ui;

import java.awt.event.MouseEvent;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class Spinner extends JPanel implements MouseListener
{
    private int value;
    private JTextField textField;
    private JPanel buttonPanel;
    private ArrowPanel upButton;
    private ArrowPanel downButton;
    
    public Spinner(final int value) {
        super(new BorderLayout());
        this.value = value;
        (this.textField = new JTextField(Integer.toString(this.value))).setHorizontalAlignment(4);
        this.add(this.textField);
        this.buttonPanel = new JPanel(new GridLayout(2, 1, 0, 1));
        (this.upButton = new ArrowPanel(0)).addMouseListener(this);
        (this.downButton = new ArrowPanel(1)).addMouseListener(this);
        this.buttonPanel.add(this.upButton);
        this.buttonPanel.add(this.downButton);
        this.add(this.buttonPanel, "East");
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void mouseClicked(final MouseEvent e) {
        if (e.getSource() == this.upButton) {
            ++this.value;
            this.textField.setText(Integer.toString(this.value));
            this.firePropertyChange("value", this.value - 1, this.value);
        }
        else if (e.getSource() == this.downButton) {
            --this.value;
            this.textField.setText(Integer.toString(this.value));
            this.firePropertyChange("value", this.value + 1, this.value);
        }
    }
    
    public void mouseEntered(final MouseEvent e) {
    }
    
    public void mouseExited(final MouseEvent e) {
    }
    
    public void mousePressed(final MouseEvent e) {
    }
    
    public void mouseReleased(final MouseEvent e) {
    }
}
