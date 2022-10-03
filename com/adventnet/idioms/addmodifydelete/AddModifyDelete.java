package com.adventnet.idioms.addmodifydelete;

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

public class AddModifyDelete extends JPanel
{
    private int state;
    public static int HORIZONTAL;
    public static int VERTICAL;
    private JButton[] jb;
    private JPanel buttonPanel;
    private JButton addButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private static ResourceBundle bundle;
    private GridBagConstraints gridBagConstraints;
    private String CMD_ADD;
    private String CMD_MODIFY;
    private String CMD_DELETE;
    
    public AddModifyDelete() {
        this.state = AddModifyDelete.HORIZONTAL;
        this.jb = null;
        this.buttonPanel = null;
        this.addButton = null;
        this.modifyButton = null;
        this.deleteButton = null;
        this.CMD_ADD = "cmd.add";
        this.CMD_MODIFY = "cmd.modify";
        this.CMD_DELETE = "cmd.delete";
        this.setButtonUI();
    }
    
    private void setButtonUI() {
        this.addButton = new JButton();
        this.modifyButton = new JButton();
        this.deleteButton = new JButton();
        this.addButton.setActionCommand(this.CMD_ADD);
        this.setAddLabel("Add");
        this.modifyButton.setActionCommand(this.CMD_MODIFY);
        this.setModifyLabel("Modify");
        this.deleteButton.setActionCommand(this.CMD_DELETE);
        this.setDeleteLabel("Delete");
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
            this.add(this.addButton, this.gridBagConstraints);
            this.gridBagConstraints = new GridBagConstraints();
            this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
            this.gridBagConstraints.anchor = 13;
            this.add(this.modifyButton, this.gridBagConstraints);
            this.gridBagConstraints = new GridBagConstraints();
            this.gridBagConstraints.insets = new Insets(5, 0, 5, 5);
            this.gridBagConstraints.anchor = 13;
            this.add(this.deleteButton, this.gridBagConstraints);
            this.equalizeButtonSizes();
        }
        if (this.getOrientation() == 1) {
            this.removeAll();
            this.setLayout(new BoxLayout(this, 1));
            this.add(this.addButton);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            this.add(this.modifyButton);
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            this.add(this.deleteButton);
            this.equalizeButtonSizes();
        }
    }
    
    public void setAddLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Add";
        }
        this.addButton.setText(getString(s));
        this.addButton.setMnemonic(this.getAddLabel().charAt(0));
        this.equalizeButtonSizes();
    }
    
    public void setModifyLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Modify";
        }
        this.modifyButton.setText(getString(s));
        this.modifyButton.setMnemonic(this.getModifyLabel().charAt(0));
        this.equalizeButtonSizes();
    }
    
    public void setDeleteLabel(String s) {
        if (s.trim().equals("") || s == null) {
            s = "Delete";
        }
        this.deleteButton.setText(getString(s));
        this.deleteButton.setMnemonic(this.getDeleteLabel().charAt(0));
        this.equalizeButtonSizes();
    }
    
    public String getAddLabel() {
        return this.addButton.getText();
    }
    
    public String getModifyLabel() {
        return this.modifyButton.getText();
    }
    
    public String getDeleteLabel() {
        return this.deleteButton.getText();
    }
    
    public void setOrientation(final int state) {
        this.state = state;
        this.layoutButtons();
    }
    
    public int getOrientation() {
        return this.state;
    }
    
    public void addAddButtonListener(final ActionListener actionListener) {
        this.addButton.addActionListener(actionListener);
    }
    
    public void addModifyButtonListener(final ActionListener actionListener) {
        this.modifyButton.addActionListener(actionListener);
    }
    
    public void addDeleteButtonListener(final ActionListener actionListener) {
        this.deleteButton.addActionListener(actionListener);
    }
    
    public void removeAddButtonListener(final ActionListener actionListener) {
        this.addButton.removeActionListener(actionListener);
    }
    
    public void removeModifyButtonListener(final ActionListener actionListener) {
        this.modifyButton.removeActionListener(actionListener);
    }
    
    public void removeDeleteButtonListener(final ActionListener actionListener) {
        this.deleteButton.removeActionListener(actionListener);
    }
    
    private void equalizeButtonSizes() {
        final JButton[] array = { this.addButton, this.modifyButton, this.deleteButton };
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
        AddModifyDelete.bundle = bundle;
        this.setAddLabel(getString("Add"));
        this.setModifyLabel(getString("Modify"));
        this.setDeleteLabel(getString("Delete"));
    }
    
    public ResourceBundle getResourceBundle() {
        return AddModifyDelete.bundle;
    }
    
    public static String getString(final String s) {
        String string = null;
        if (AddModifyDelete.bundle != null) {
            try {
                string = AddModifyDelete.bundle.getString(s);
            }
            catch (final MissingResourceException ex) {}
        }
        if (string == null || string.equals("")) {
            return s.trim();
        }
        return string;
    }
    
    public void addButtonEnabled(final boolean b) {
        if (b) {
            this.addButton.setEnabled(true);
        }
        else {
            this.addButton.setEnabled(false);
        }
    }
    
    public void modifyButtonEnabled(final boolean b) {
        if (b) {
            this.modifyButton.setEnabled(true);
        }
        else {
            this.modifyButton.setEnabled(false);
        }
    }
    
    public void deleteButtonEnabled(final boolean b) {
        if (b) {
            this.deleteButton.setEnabled(true);
        }
        else {
            this.deleteButton.setEnabled(false);
        }
    }
    
    public JButton[] getButtonArray() {
        (this.jb = new JButton[3])[0] = this.addButton;
        this.jb[1] = this.modifyButton;
        this.jb[2] = this.deleteButton;
        return this.jb;
    }
    
    static {
        AddModifyDelete.HORIZONTAL = 0;
        AddModifyDelete.VERTICAL = 1;
    }
}
