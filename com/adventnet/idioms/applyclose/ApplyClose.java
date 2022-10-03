package com.adventnet.idioms.applyclose;

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

public class ApplyClose extends JPanel
{
    private int state;
    public static int HORIZONTAL;
    public static int VERTICAL;
    private JButton[] jb;
    private JPanel buttonPanel;
    private JButton applyButton;
    private JButton closeButton;
    private static ResourceBundle bundle;
    private GridBagConstraints gridBagConstraints;
    private String CMD_APPLY;
    private String CMD_CLOSE;
    
    public ApplyClose() {
        this.state = ApplyClose.HORIZONTAL;
        this.jb = null;
        this.buttonPanel = null;
        this.applyButton = null;
        this.closeButton = null;
        this.CMD_APPLY = "cmd.apply";
        this.CMD_CLOSE = "cmd.close";
        this.setButtonUI();
    }
    
    private void setButtonUI() {
        this.applyButton = new JButton();
        this.closeButton = new JButton();
        this.applyButton.setActionCommand(this.CMD_APPLY);
        this.setApplyLabel("Apply");
        this.closeButton.setActionCommand(this.CMD_CLOSE);
        this.setCloseLabel("Close");
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
            this.add(this.applyButton, this.gridBagConstraints);
            this.gridBagConstraints = new GridBagConstraints();
            this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
            this.gridBagConstraints.anchor = 13;
            this.add(this.closeButton, this.gridBagConstraints);
            this.equalizeButtonSizes();
        }
        if (this.getOrientation() == 1) {
            this.removeAll();
            this.setLayout(new BoxLayout(this, 1));
            this.add(this.applyButton);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            this.add(this.closeButton);
            this.equalizeButtonSizes();
        }
    }
    
    public void setApplyLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Apply";
        }
        this.applyButton.setText(getString(s));
        this.equalizeButtonSizes();
    }
    
    public void setCloseLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Close";
        }
        this.closeButton.setText(getString(s));
        this.closeButton.setMnemonic(this.getCloseLabel().charAt(0));
        this.equalizeButtonSizes();
    }
    
    public String getApplyLabel() {
        return this.applyButton.getText();
    }
    
    public String getCloseLabel() {
        return this.closeButton.getText();
    }
    
    public void setOrientation(final int state) {
        this.state = state;
        this.layoutButtons();
    }
    
    public int getOrientation() {
        return this.state;
    }
    
    public void addApplyButtonListener(final ActionListener actionListener) {
        this.applyButton.addActionListener(actionListener);
    }
    
    public void addCloseButtonListener(final ActionListener actionListener) {
        this.closeButton.addActionListener(actionListener);
    }
    
    public void removeApplyButtonListener(final ActionListener actionListener) {
        this.applyButton.removeActionListener(actionListener);
    }
    
    public void removeCloseButtonListener(final ActionListener actionListener) {
        this.closeButton.removeActionListener(actionListener);
    }
    
    private void equalizeButtonSizes() {
        final JButton[] array = { this.applyButton, this.closeButton };
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
        ApplyClose.bundle = bundle;
        this.setApplyLabel(getString("Apply"));
        this.setCloseLabel(getString("Close"));
    }
    
    public ResourceBundle getResourceBundle() {
        return ApplyClose.bundle;
    }
    
    public static String getString(final String s) {
        String string = null;
        if (ApplyClose.bundle != null) {
            try {
                string = ApplyClose.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    public void setDefaultButton(final JRootPane rootPane) {
        rootPane.setDefaultButton(this.applyButton);
    }
    
    public void applyButtonEnabled(final boolean b) {
        if (b) {
            this.applyButton.setEnabled(true);
        }
        else {
            this.applyButton.setEnabled(false);
        }
    }
    
    public JButton[] getButtonArray() {
        (this.jb = new JButton[2])[0] = this.applyButton;
        this.jb[1] = this.closeButton;
        return this.jb;
    }
    
    static {
        ApplyClose.HORIZONTAL = 0;
        ApplyClose.VERTICAL = 1;
    }
}
