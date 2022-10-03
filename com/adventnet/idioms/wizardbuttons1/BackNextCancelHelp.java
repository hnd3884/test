package com.adventnet.idioms.wizardbuttons1;

import javax.swing.JRootPane;
import java.util.MissingResourceException;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BackNextCancelHelp extends JPanel
{
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;
    private JButton helpButton;
    private JPanel buttonPanel;
    private static ResourceBundle bundle;
    private GridBagConstraints gridBagConstraints;
    private JButton[] jb;
    private String CMD_BACK;
    private String CMD_CANCEL;
    private String CMD_HELP;
    private String CMD_NEXT;
    
    public BackNextCancelHelp() {
        this.backButton = null;
        this.nextButton = null;
        this.cancelButton = null;
        this.helpButton = null;
        this.buttonPanel = null;
        this.jb = null;
        this.CMD_BACK = "cmd.back";
        this.CMD_CANCEL = "cmd.cancel";
        this.CMD_HELP = "cmd.help";
        this.CMD_NEXT = "cmd.next";
        this.setButtonUI();
    }
    
    private void setButtonUI() {
        this.backButton = new JButton();
        this.nextButton = new JButton();
        this.cancelButton = new JButton();
        this.helpButton = new JButton();
        this.buttonPanel = new JPanel();
        this.backButton.setActionCommand(this.CMD_BACK);
        this.setBackLabel("< Back");
        this.nextButton.setActionCommand(this.CMD_NEXT);
        this.setNextLabel("Next >");
        this.cancelButton.setActionCommand(this.CMD_CANCEL);
        this.setCancelLabel("Cancel");
        this.helpButton.setActionCommand(this.CMD_HELP);
        this.setHelpLabel("Help");
        this.layoutButtons();
        this.equalizeButtonSizes();
    }
    
    private void layoutButtons() {
        this.removeAll();
        this.setLayout(new GridBagLayout());
        this.gridBagConstraints = new GridBagConstraints();
        this.gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.gridBagConstraints.anchor = 17;
        this.add(this.backButton, this.gridBagConstraints);
        this.gridBagConstraints = new GridBagConstraints();
        this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        this.gridBagConstraints.anchor = 17;
        this.gridBagConstraints.weightx = 0.1;
        this.add(this.nextButton, this.gridBagConstraints);
        this.gridBagConstraints = new GridBagConstraints();
        this.gridBagConstraints.insets = new Insets(5, 17, 5, 5);
        this.gridBagConstraints.anchor = 13;
        this.gridBagConstraints.weightx = 0.1;
        this.add(this.cancelButton, this.gridBagConstraints);
        this.gridBagConstraints = new GridBagConstraints();
        this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
        this.gridBagConstraints.anchor = 13;
        this.add(this.helpButton, this.gridBagConstraints);
        this.equalizeButtonSizes();
    }
    
    public void setBackLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "< Back";
        }
        this.backButton.setText(getString(s));
        this.backButton.setMnemonic(this.getBackLabel().charAt(2));
        this.equalizeButtonSizes();
    }
    
    public void setNextLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Next >";
        }
        this.nextButton.setText(getString(s));
        this.nextButton.setMnemonic(this.getNextLabel().charAt(0));
        this.equalizeButtonSizes();
    }
    
    public void setCancelLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Cancel";
        }
        this.cancelButton.setText(getString(s));
        this.equalizeButtonSizes();
    }
    
    public void setHelpLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Help";
        }
        this.helpButton.setText(getString(s));
        this.helpButton.setMnemonic(this.getHelpLabel().charAt(0));
        this.equalizeButtonSizes();
    }
    
    public String getBackLabel() {
        return this.backButton.getText();
    }
    
    public String getNextLabel() {
        return this.nextButton.getText();
    }
    
    public String getCancelLabel() {
        return this.cancelButton.getText();
    }
    
    public String getHelpLabel() {
        return this.helpButton.getText();
    }
    
    public void addBackButtonListener(final ActionListener actionListener) {
        this.backButton.addActionListener(actionListener);
    }
    
    public void addNextButtonListener(final ActionListener actionListener) {
        this.nextButton.addActionListener(actionListener);
    }
    
    public void addCancelButtonListener(final ActionListener actionListener) {
        this.cancelButton.addActionListener(actionListener);
    }
    
    public void addHelpButtonListener(final ActionListener actionListener) {
        this.helpButton.addActionListener(actionListener);
    }
    
    public void removeBackButtonListener(final ActionListener actionListener) {
        this.backButton.removeActionListener(actionListener);
    }
    
    public void removeNextButtonListener(final ActionListener actionListener) {
        this.nextButton.removeActionListener(actionListener);
    }
    
    public void removeCancelButtonListener(final ActionListener actionListener) {
        this.cancelButton.removeActionListener(actionListener);
    }
    
    public void removeHelpButtonListener(final ActionListener actionListener) {
        this.helpButton.removeActionListener(actionListener);
    }
    
    private void equalizeButtonSizes() {
        final JButton[] array = { this.backButton, this.nextButton, this.cancelButton, this.helpButton };
        final String[] array2 = new String[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i].getText();
        }
        final Dimension dimension = new Dimension(0, 0);
        final FontMetrics fontMetrics = array[0].getFontMetrics(array[0].getFont());
        final Graphics graphics = this.getGraphics();
        for (int j = 0; j < array2.length; ++j) {
            final Rectangle2D stringBounds = fontMetrics.getStringBounds(array2[j], graphics);
            dimension.width = Math.max(dimension.width, (int)stringBounds.getWidth());
            dimension.height = Math.max(dimension.height, (int)stringBounds.getHeight());
        }
        final Insets borderInsets = array[0].getBorder().getBorderInsets(array[0]);
        final Dimension dimension2 = dimension;
        dimension2.width += borderInsets.left + borderInsets.right;
        final Dimension dimension3 = dimension;
        dimension3.height += borderInsets.top + borderInsets.bottom;
        for (int k = 0; k < array.length; ++k) {
            array[k].setPreferredSize((Dimension)dimension.clone());
            array[k].setMaximumSize((Dimension)dimension.clone());
        }
    }
    
    public void setResourceBundle(final ResourceBundle bundle) {
        BackNextCancelHelp.bundle = bundle;
        this.setBackLabel(getString("< Back"));
        this.setNextLabel(getString("Next >"));
        this.setCancelLabel(getString("Cancel"));
        this.setHelpLabel(getString("Help"));
    }
    
    public ResourceBundle getResourceBundle() {
        return BackNextCancelHelp.bundle;
    }
    
    public static String getString(final String s) {
        String string = null;
        if (BackNextCancelHelp.bundle != null) {
            try {
                string = BackNextCancelHelp.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    public void setDefaultButton(final JRootPane rootPane) {
        rootPane.setDefaultButton(this.nextButton);
    }
    
    public void backButtonEnabled(final boolean b) {
        if (b) {
            this.backButton.setEnabled(true);
        }
        else {
            this.backButton.setEnabled(false);
        }
    }
    
    public void nextButtonEnabled(final boolean b) {
        if (b) {
            this.nextButton.setEnabled(true);
        }
        else {
            this.nextButton.setEnabled(false);
        }
    }
    
    public JButton[] getButtonArray() {
        (this.jb = new JButton[4])[0] = this.backButton;
        this.jb[1] = this.nextButton;
        this.jb[2] = this.cancelButton;
        this.jb[3] = this.helpButton;
        return this.jb;
    }
}
