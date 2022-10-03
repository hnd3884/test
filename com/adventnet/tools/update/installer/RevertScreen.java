package com.adventnet.tools.update.installer;

import java.util.Properties;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.tree.TreeCellRenderer;
import com.adventnet.tools.update.CommonUtil;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.awt.Container;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.JPanel;

public class RevertScreen extends JPanel implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel JPanel11;
    JPanel JPanel31;
    JLabel OptionalPatchImage;
    JTextArea uninstallLabel;
    JLabel JLabel3;
    ContextSensitiveHelpButton revertHelpButton;
    JScrollPane JScrollPane1;
    JTree versionTree;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    
    public void stop() {
        if (!this.running) {
            return;
        }
        this.running = false;
    }
    
    public void start() {
        if (this.running) {
            return;
        }
        this.running = true;
    }
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 462, this.getPreferredSize().height + 376));
        this.setSize(this.getPreferredSize());
        final Container container = this;
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus("Error in init method", ex);
        }
        this.initialized = true;
        this.setHelpFiles();
        this.versionTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Service Pack")));
        this.expandTree();
    }
    
    public String getParameter(final String input) {
        if (this.po != null && this.po.getParameter(input) != null) {
            return (String)this.po.getParameter(input);
        }
        String value = null;
        if (this.applet != null) {
            value = this.applet.getParameter(input);
        }
        else {
            value = (String)Utility.getParameter(input);
        }
        if (value == null) {}
        return value;
    }
    
    public void setUpProperties() {
        try {
            this.JPanel11.setBorder(new BevelBorder(0));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JPanel11, ex);
        }
        try {
            this.OptionalPatchImage.setHorizontalAlignment(2);
            this.OptionalPatchImage.setForeground(new Color(-16777216));
            this.OptionalPatchImage.setHorizontalTextPosition(4);
            this.OptionalPatchImage.setVerticalTextPosition(0);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.OptionalPatchImage, ex);
        }
        this.OptionalPatchImage.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/context_install.png", this.applet, true));
        this.OptionalPatchImage.setFont(UpdateManagerUtil.getFont());
        try {
            this.uninstallLabel.setForeground(new Color(-16777216));
            this.uninstallLabel.setWrapStyleWord(true);
            this.uninstallLabel.setLineWrap(true);
            this.uninstallLabel.setBackground(new Color(-3355444));
            this.uninstallLabel.setEditable(false);
            this.uninstallLabel.setText("Note that Service Pack  ...........  and its dependent (as shown below) will be automatically removed after uninstallation.If you donot want to proceed click 'Cancel' button.");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.uninstallLabel, ex);
        }
        this.uninstallLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel3.setHorizontalAlignment(2);
            this.JLabel3.setForeground(new Color(-16777216));
            this.JLabel3.setHorizontalTextPosition(4);
            this.JLabel3.setText(CommonUtil.getString("Uninstall"));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel3, ex);
        }
        this.JLabel3.setFont(UpdateManagerUtil.getBoldFont());
        try {
            this.revertHelpButton.setHorizontalTextPosition(4);
            this.revertHelpButton.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/context_help.png", this.applet, true));
            this.revertHelpButton.setText("");
            this.revertHelpButton.setPreferredSize(new Dimension(32, 27));
            this.revertHelpButton.setMinimumSize(new Dimension(32, 27));
            this.revertHelpButton.setMaximumSize(new Dimension(32, 27));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.revertHelpButton, ex);
        }
        this.revertHelpButton.setFont(UpdateManagerUtil.getFont());
        try {
            this.versionTree.setEnabled(false);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.versionTree, ex);
        }
        this.versionTree.setCellRenderer(new VersionTreeRenderer());
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel11 = new JPanel();
        this.JPanel31 = new JPanel();
        this.OptionalPatchImage = new JLabel();
        this.uninstallLabel = new JTextArea();
        this.JLabel3 = new JLabel();
        this.revertHelpButton = new ContextSensitiveHelpButton("./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png");
        this.JScrollPane1 = new JScrollPane();
        this.versionTree = new JTree();
        this.initializeParameters();
        (this.revertHelpButton = ContextSensitiveHelpButton.getHelpButton(UpdateManagerUtil.getHelpXmlFilePath(), UpdateManagerUtil.getHelpHtmlFilePath(), "./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png")).setHelpWindowSize(new Dimension(400, 75));
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new GridBagLayout());
        this.inset = new Insets(10, 10, 10, 10);
        final int x = 0;
        final int y = 0;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.2;
        final double wtY = 0.1;
        final GridBagConstraints cons = this.cons;
        final int anchor = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 1, this.inset, 0, 0);
        this.Top.add(this.JPanel11, this.cons);
        this.JPanel11.setLayout(new GridBagLayout());
        this.inset = new Insets(7, 7, 0, 7);
        final int x2 = 0;
        final int y2 = 0;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.1;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 11;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 1, this.inset, 0, 0);
        this.JPanel11.add(this.JPanel31, this.cons);
        this.JPanel31.setLayout(new GridBagLayout());
        this.inset = new Insets(2, 5, 0, 10);
        final int x3 = 0;
        final int y3 = 0;
        final int width3 = 1;
        final int height3 = 2;
        final double wtX3 = 0.0;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 1, this.inset, 0, 0);
        this.JPanel31.add(this.OptionalPatchImage, this.cons);
        this.inset = new Insets(5, 10, 0, 10);
        final int x4 = 1;
        final int y4 = 1;
        final int width4 = 2;
        final int height4 = 1;
        final double wtX4 = 0.2;
        final double wtY4 = 0.1;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 1, this.inset, 0, 0);
        this.JPanel31.add(this.uninstallLabel, this.cons);
        this.inset = new Insets(2, 10, 0, 20);
        final int x5 = 1;
        final int y5 = 0;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.1;
        final double wtY5 = 0.0;
        final GridBagConstraints cons9 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x5, y5, width5, height5, wtX5, wtY5, anchor5, 2, this.inset, 0, 0);
        this.JPanel31.add(this.JLabel3, this.cons);
        this.inset = new Insets(0, 90, 20, 0);
        final int x6 = 2;
        final int y6 = 0;
        final int width6 = 1;
        final int height6 = 1;
        final double wtX6 = 0.0;
        final double wtY6 = 0.0;
        final GridBagConstraints cons11 = this.cons;
        final int anchor6 = 10;
        final GridBagConstraints cons12 = this.cons;
        this.setConstraints(x6, y6, width6, height6, wtX6, wtY6, anchor6, 0, this.inset, 0, 0);
        this.JPanel31.add(this.revertHelpButton, this.cons);
        this.inset = new Insets(7, 13, 13, 13);
        final int x7 = 0;
        final int y7 = 1;
        final int width7 = 1;
        final int height7 = 1;
        final double wtX7 = 0.1;
        final double wtY7 = 0.1;
        final GridBagConstraints cons13 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons14 = this.cons;
        this.setConstraints(x7, y7, width7, height7, wtX7, wtY7, anchor7, 1, this.inset, 0, 0);
        this.JPanel11.add(this.JScrollPane1, this.cons);
        this.JScrollPane1.getViewport().add(this.versionTree);
    }
    
    public void setUpConnections() {
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    public void setProperties(final Properties props) {
        if (this.po != null) {
            this.po.setParameters(props);
        }
    }
    
    @Override
    public void setParameterObject(final ParameterObject paramObj) {
        this.po = paramObj;
        this.initializeParameters();
    }
    
    private void initializeParameters() {
        if (this.po != null) {
            this.po.addParameterChangeListener(this);
        }
    }
    
    @Override
    public void destroy() {
        if (this.po != null) {
            this.po.removeParameterChangeListener(this);
        }
    }
    
    @Override
    public void parameterChanged(final ParameterObject paramObj) {
    }
    
    public void setConstraints(final int x, final int y, final int width, final int height, final double wtX, final double wtY, final int anchor, final int fill, final Insets inset, final int padX, final int padY) {
        this.cons.gridx = x;
        this.cons.gridy = y;
        this.cons.gridwidth = width;
        this.cons.gridheight = height;
        this.cons.weightx = wtX;
        this.cons.weighty = wtY;
        this.cons.anchor = anchor;
        this.cons.fill = fill;
        this.cons.insets = inset;
        this.cons.ipadx = padX;
        this.cons.ipady = padY;
    }
    
    public RevertScreen() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel11 = null;
        this.JPanel31 = null;
        this.OptionalPatchImage = null;
        this.uninstallLabel = null;
        this.JLabel3 = null;
        this.revertHelpButton = null;
        this.JScrollPane1 = null;
        this.versionTree = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.init();
    }
    
    public RevertScreen(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel11 = null;
        this.JPanel31 = null;
        this.OptionalPatchImage = null;
        this.uninstallLabel = null;
        this.JLabel3 = null;
        this.revertHelpButton = null;
        this.JScrollPane1 = null;
        this.versionTree = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.applet = applet;
        this.init();
    }
    
    private void setHelpFiles() {
        this.versionTree.setName("RevertScreen_JList1");
    }
    
    public void expandTree() {
        for (int row = 0; row < this.versionTree.getRowCount(); ++row) {
            this.versionTree.expandRow(row);
        }
        this.versionTree.updateUI();
    }
}
