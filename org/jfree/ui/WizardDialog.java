package org.jfree.ui;

import java.awt.Component;
import javax.swing.BorderFactory;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import javax.swing.JFrame;
import java.awt.Container;
import java.util.ArrayList;
import java.awt.Dialog;
import javax.swing.JButton;
import java.util.List;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class WizardDialog extends JDialog implements ActionListener
{
    private Object result;
    private int step;
    private WizardPanel currentPanel;
    private List panels;
    private JButton previousButton;
    private JButton nextButton;
    private JButton finishButton;
    private JButton helpButton;
    
    public WizardDialog(final JDialog owner, final boolean modal, final String title, final WizardPanel firstPanel) {
        super(owner, String.valueOf(title) + " : step 1", modal);
        this.result = null;
        this.currentPanel = firstPanel;
        this.step = 0;
        (this.panels = new ArrayList()).add(firstPanel);
        this.setContentPane(this.createContent());
    }
    
    public WizardDialog(final JFrame owner, final boolean modal, final String title, final WizardPanel firstPanel) {
        super(owner, String.valueOf(title) + " : step 1", modal);
        this.result = null;
        this.currentPanel = firstPanel;
        this.step = 0;
        (this.panels = new ArrayList()).add(firstPanel);
        this.setContentPane(this.createContent());
    }
    
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        if (command.equals("nextButton")) {
            this.next();
        }
        else if (command.equals("previousButton")) {
            this.previous();
        }
        else if (command.equals("finishButton")) {
            this.finish();
        }
    }
    
    public boolean canDoNextPanel() {
        return this.currentPanel.hasNextPanel();
    }
    
    public boolean canDoPreviousPanel() {
        return this.step > 0;
    }
    
    public boolean canFinish() {
        return this.currentPanel.canFinish();
    }
    
    public JPanel createContent() {
        final JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        content.add(this.panels.get(0));
        final L1R3ButtonPanel buttons = new L1R3ButtonPanel("Help", "Previous", "Next", "Finish");
        (this.helpButton = buttons.getLeftButton()).setEnabled(false);
        (this.previousButton = buttons.getRightButton1()).setActionCommand("previousButton");
        this.previousButton.addActionListener(this);
        this.previousButton.setEnabled(false);
        (this.nextButton = buttons.getRightButton2()).setActionCommand("nextButton");
        this.nextButton.addActionListener(this);
        this.nextButton.setEnabled(true);
        (this.finishButton = buttons.getRightButton3()).setActionCommand("finishButton");
        this.finishButton.addActionListener(this);
        this.finishButton.setEnabled(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add(buttons, "South");
        return content;
    }
    
    private void enableButtons() {
        this.previousButton.setEnabled(this.step > 0);
        this.nextButton.setEnabled(this.canDoNextPanel());
        this.finishButton.setEnabled(this.canFinish());
        this.helpButton.setEnabled(false);
    }
    
    public void finish() {
        this.result = this.currentPanel.getResult();
        this.setVisible(false);
    }
    
    public Object getResult() {
        return this.result;
    }
    
    public int getStepCount() {
        return 0;
    }
    
    public WizardPanel getWizardPanel(final int step) {
        if (step < this.panels.size()) {
            return this.panels.get(step);
        }
        return null;
    }
    
    public boolean isCancelled() {
        return false;
    }
    
    public void next() {
        WizardPanel nextPanel = this.getWizardPanel(this.step + 1);
        if (nextPanel != null) {
            if (!this.currentPanel.canRedisplayNextPanel()) {
                nextPanel = this.currentPanel.getNextPanel();
            }
        }
        else {
            nextPanel = this.currentPanel.getNextPanel();
        }
        ++this.step;
        if (this.step < this.panels.size()) {
            this.panels.set(this.step, nextPanel);
        }
        else {
            this.panels.add(nextPanel);
        }
        final Container content = this.getContentPane();
        content.remove(this.currentPanel);
        content.add(nextPanel);
        this.currentPanel = nextPanel;
        this.setTitle("Step " + (this.step + 1));
        this.enableButtons();
        this.pack();
    }
    
    public void previous() {
        if (this.step > 0) {
            final WizardPanel previousPanel = this.getWizardPanel(this.step - 1);
            previousPanel.returnFromLaterStep();
            final Container content = this.getContentPane();
            content.remove(this.currentPanel);
            content.add(previousPanel);
            --this.step;
            this.currentPanel = previousPanel;
            this.setTitle("Step " + (this.step + 1));
            this.enableButtons();
            this.pack();
        }
    }
}
