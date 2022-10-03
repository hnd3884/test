package com.adventnet.beans.rangenavigator;

import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class NavigationButtonsPanel extends JPanel implements MouseListener
{
    private JButton firstButton;
    private JButton lastButton;
    private JButton nextButton;
    private JButton previousButton;
    
    public NavigationButtonsPanel() {
        this.initComponents();
        this.init();
    }
    
    private void init() {
        this.firstButton.addMouseListener(this);
        this.nextButton.addMouseListener(this);
        this.previousButton.addMouseListener(this);
        this.lastButton.addMouseListener(this);
    }
    
    private void initComponents() {
        this.firstButton = new JButton();
        this.nextButton = new JButton();
        this.previousButton = new JButton();
        this.lastButton = new JButton();
        this.setLayout(new GridBagLayout());
        this.firstButton.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/first.png")));
        this.firstButton.setBorderPainted(false);
        this.firstButton.setDisabledIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/first_dis.png")));
        this.firstButton.setPreferredSize(new Dimension(30, 26));
        this.firstButton.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/first_over.png")));
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        this.add(this.firstButton, gridBagConstraints);
        this.nextButton.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/next.png")));
        this.nextButton.setBorderPainted(false);
        this.nextButton.setDisabledIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/next_dis.png")));
        this.nextButton.setPreferredSize(new Dimension(30, 26));
        this.nextButton.setRolloverEnabled(true);
        this.nextButton.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/next_over.png")));
        final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.anchor = 17;
        this.add(this.nextButton, gridBagConstraints2);
        this.previousButton.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/previous.png")));
        this.previousButton.setBorderPainted(false);
        this.previousButton.setDisabledIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/previous_dis.png")));
        this.previousButton.setPreferredSize(new Dimension(30, 26));
        this.previousButton.setRolloverEnabled(true);
        this.previousButton.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/previous_over.png")));
        final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.anchor = 17;
        this.add(this.previousButton, gridBagConstraints3);
        this.lastButton.setIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/last.png")));
        this.lastButton.setBorderPainted(false);
        this.lastButton.setDisabledIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/last_dis.png")));
        this.lastButton.setPreferredSize(new Dimension(30, 26));
        this.lastButton.setRolloverEnabled(true);
        this.lastButton.setRolloverIcon(new ImageIcon(this.getClass().getResource("/com/adventnet/beans/rangenavigator/last_over.png")));
        final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 3;
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.anchor = 17;
        gridBagConstraints4.weightx = 0.1;
        gridBagConstraints4.insets = new Insets(0, 0, 0, 10);
        this.add(this.lastButton, gridBagConstraints4);
    }
    
    public JButton getFirstButton() {
        return this.firstButton;
    }
    
    public JButton getPreviousButton() {
        return this.previousButton;
    }
    
    public JButton getNextButton() {
        return this.nextButton;
    }
    
    public JButton getLastButton() {
        return this.lastButton;
    }
    
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    public void mouseEntered(final MouseEvent mouseEvent) {
        ((JButton)mouseEvent.getSource()).setBorderPainted(true);
    }
    
    public void mouseExited(final MouseEvent mouseEvent) {
        ((JButton)mouseEvent.getSource()).setBorderPainted(false);
    }
    
    public void mousePressed(final MouseEvent mouseEvent) {
    }
    
    public void mouseReleased(final MouseEvent mouseEvent) {
    }
}
