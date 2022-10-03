package com.adventnet.idioms.okcancelhelp;

import javax.swing.JRootPane;
import java.util.MissingResourceException;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JPanel;

public class OkCancelHelp extends JPanel
{
    private int state;
    public static int HORIZONTAL;
    public static int VERTICAL;
    private JButton[] jb;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;
    private static ResourceBundle bundle;
    private GridBagConstraints gridBagConstraints;
    private String CMD_CANCEL;
    private String CMD_HELP;
    private String CMD_OK;
    
    public OkCancelHelp() {
        this.state = OkCancelHelp.HORIZONTAL;
        this.jb = null;
        this.buttonPanel = null;
        this.okButton = null;
        this.cancelButton = null;
        this.helpButton = null;
        this.CMD_CANCEL = "cmd.cancel";
        this.CMD_HELP = "cmd.help";
        this.CMD_OK = "cmd.ok";
        this.setButtonUI();
    }
    
    private void setButtonUI() {
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.helpButton = new JButton();
        this.okButton.setActionCommand(this.CMD_OK);
        this.setOKLabel("OK");
        this.cancelButton.setActionCommand(this.CMD_CANCEL);
        this.setCancelLabel("Cancel");
        this.helpButton.setActionCommand(this.CMD_HELP);
        this.setHelpLabel("Help");
        this.setOrientation(0);
        this.layoutButtons();
        this.equalizeButtonSizes();
    }
    
    private void layoutButtons() {
        if (this.getOrientation() == 0) {
            this.removeAll();
            this.setLayout(new GridBagLayout());
            this.gridBagConstraints = new GridBagConstraints();
            this.gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            this.gridBagConstraints.anchor = 13;
            this.gridBagConstraints.weightx = 0.1;
            this.add(this.okButton, this.gridBagConstraints);
            this.gridBagConstraints = new GridBagConstraints();
            this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
            this.gridBagConstraints.anchor = 13;
            this.add(this.cancelButton, this.gridBagConstraints);
            this.gridBagConstraints = new GridBagConstraints();
            this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
            this.gridBagConstraints.anchor = 13;
            this.add(this.helpButton, this.gridBagConstraints);
            this.equalizeButtonSizes();
        }
        if (this.getOrientation() == 1) {
            this.removeAll();
            this.setLayout(new BoxLayout(this, 1));
            this.add(this.okButton);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            this.add(this.cancelButton);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            this.add(this.helpButton);
            this.equalizeButtonSizes();
        }
    }
    
    public void setOKLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "OK";
        }
        this.okButton.setText(getString(s));
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
    
    public String getOKLabel() {
        return this.okButton.getText();
    }
    
    public String getCancelLabel() {
        return this.cancelButton.getText();
    }
    
    public String getHelpLabel() {
        return this.helpButton.getText();
    }
    
    public void setOrientation(final int state) {
        this.state = state;
        this.layoutButtons();
    }
    
    public int getOrientation() {
        return this.state;
    }
    
    public void addOKButtonListener(final ActionListener actionListener) {
        this.okButton.addActionListener(actionListener);
    }
    
    public void addCancelButtonListener(final ActionListener actionListener) {
        this.cancelButton.addActionListener(actionListener);
    }
    
    public void addHelpButtonListener(final ActionListener actionListener) {
        this.helpButton.addActionListener(actionListener);
    }
    
    public void removeOKButtonListener(final ActionListener actionListener) {
        this.okButton.removeActionListener(actionListener);
    }
    
    public void removeCancelButtonListener(final ActionListener actionListener) {
        this.cancelButton.removeActionListener(actionListener);
    }
    
    public void removeHelpButtonListener(final ActionListener actionListener) {
        this.helpButton.removeActionListener(actionListener);
    }
    
    private void equalizeButtonSizes() {
        final JButton[] array = { this.okButton, this.cancelButton, this.helpButton };
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
        OkCancelHelp.bundle = bundle;
        this.setOKLabel(getString("OK"));
        this.setCancelLabel(getString("Cancel"));
        this.setHelpLabel(getString("Help"));
    }
    
    public ResourceBundle getResourceBundle() {
        return OkCancelHelp.bundle;
    }
    
    public static String getString(final String s) {
        String string = null;
        if (OkCancelHelp.bundle != null) {
            try {
                string = OkCancelHelp.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    public void setDefaultButton(final JRootPane rootPane) {
        rootPane.setDefaultButton(this.okButton);
    }
    
    public JButton[] getButtonArray() {
        (this.jb = new JButton[4])[0] = this.okButton;
        this.jb[1] = this.cancelButton;
        this.jb[2] = this.helpButton;
        return this.jb;
    }
    
    static {
        OkCancelHelp.HORIZONTAL = 0;
        OkCancelHelp.VERTICAL = 1;
    }
}
