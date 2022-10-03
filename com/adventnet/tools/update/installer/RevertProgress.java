package com.adventnet.tools.update.installer;

import javax.swing.Icon;
import java.util.Properties;
import java.awt.GridBagLayout;
import java.awt.Component;
import com.adventnet.tools.update.CommonUtil;
import java.awt.Color;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.applet.Applet;
import javax.swing.JPanel;

public class RevertProgress extends JPanel implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JPanel JPanel41;
    JPanel JPanel31;
    JProgressBar revertProgressBar;
    JLabel percenLabel;
    public JLabel mainLabel;
    public JLabel subLabel;
    JLabel fileLabel;
    JCheckBox closeCheckBox;
    JLabel animationLabel;
    JPanel JPanel2;
    JLabel JLabel1;
    JSeparator JSeparator1;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    private static RevertProgress revertProgress;
    private int mainLabelLength;
    private int subLabelLength;
    
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
            this.JPanel1.setBorder(new BevelBorder(0));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JPanel1, ex);
        }
        this.revertProgressBar.setFont(UpdateManagerUtil.getFont());
        try {
            this.percenLabel.setHorizontalAlignment(2);
            this.percenLabel.setForeground(new Color(-16777216));
            this.percenLabel.setHorizontalTextPosition(4);
            this.percenLabel.setText(" ");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.percenLabel, ex);
        }
        this.percenLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.mainLabel.setHorizontalAlignment(2);
            this.mainLabel.setForeground(new Color(-16777216));
            this.mainLabel.setHorizontalTextPosition(4);
            this.mainLabel.setText("Uninstalling ");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.mainLabel, ex);
        }
        this.mainLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.subLabel.setHorizontalAlignment(2);
            this.subLabel.setForeground(new Color(-16777216));
            this.subLabel.setHorizontalTextPosition(4);
            this.subLabel.setText("");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.subLabel, ex);
        }
        this.subLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.fileLabel.setHorizontalAlignment(2);
            this.fileLabel.setForeground(new Color(-16777216));
            this.fileLabel.setHorizontalTextPosition(4);
            this.fileLabel.setText("  ");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.fileLabel, ex);
        }
        this.fileLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.closeCheckBox.setHorizontalTextPosition(4);
            this.closeCheckBox.setText(CommonUtil.getString("Automatically close after uninstallation"));
            this.closeCheckBox.setMnemonic('A');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.closeCheckBox, ex);
        }
        this.closeCheckBox.setSelected(false);
        this.closeCheckBox.setFont(UpdateManagerUtil.getFont());
        try {
            this.animationLabel.setForeground(new Color(-16777216));
            this.animationLabel.setHorizontalAlignment(0);
            this.animationLabel.setHorizontalTextPosition(0);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.animationLabel, ex);
        }
        this.animationLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel1.setHorizontalAlignment(2);
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setHorizontalTextPosition(4);
            this.JLabel1.setText(CommonUtil.getString("Uninstallation status"));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel1, ex);
        }
        this.JLabel1.setFont(UpdateManagerUtil.getBoldFont());
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.JPanel41 = new JPanel();
        this.JPanel31 = new JPanel();
        this.revertProgressBar = new JProgressBar();
        this.percenLabel = new JLabel();
        this.mainLabel = new JLabel();
        this.subLabel = new JLabel();
        this.fileLabel = new JLabel();
        this.closeCheckBox = new JCheckBox();
        this.animationLabel = new JLabel();
        this.JPanel2 = new JPanel();
        this.JLabel1 = new JLabel();
        this.JSeparator1 = new JSeparator();
        this.initializeParameters();
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
        this.Top.add(this.JPanel1, this.cons);
        this.JPanel1.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 10, 10, 10);
        final int x2 = 0;
        final int y2 = 1;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.2;
        final double wtY2 = 0.1;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 1, this.inset, 0, 0);
        this.JPanel1.add(this.JPanel41, this.cons);
        this.JPanel41.setLayout(new BorderLayout(5, 5));
        this.JPanel41.add(this.JPanel31, "South");
        this.JPanel31.setLayout(new GridBagLayout());
        this.setConstraints(0, 5, this.cons.gridwidth, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, new Insets(10, 0, 0, 0), 0, 0);
        this.JPanel31.add(this.closeCheckBox, this.cons);
        final int x3 = 0;
        final int y3 = 4;
        final GridBagConstraints cons5 = this.cons;
        this.setConstraints(x3, y3, 0, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, this.cons.insets, 0, 0);
        this.JPanel31.add(this.subLabel, this.cons);
        final int x4 = 0;
        final int y4 = 3;
        final int width3 = 1;
        final int height3 = 1;
        final double wtX3 = 0.1;
        final double wtY3 = 0.0;
        final GridBagConstraints cons6 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons7 = this.cons;
        this.setConstraints(x4, y4, width3, height3, wtX3, wtY3, anchor3, 2, new Insets(10, 0, 0, 10), 0, 0);
        this.JPanel31.add(this.revertProgressBar, this.cons);
        final int x5 = 1;
        final int y5 = 3;
        final int width4 = 1;
        final int height4 = 1;
        final double wtX4 = 0.0;
        final double wtY4 = 0.0;
        final GridBagConstraints cons8 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons9 = this.cons;
        this.setConstraints(x5, y5, width4, height4, wtX4, wtY4, anchor4, 2, new Insets(10, 0, 0, 10), 0, 0);
        this.JPanel31.add(this.percenLabel, this.cons);
        final int x6 = 0;
        final int y6 = 2;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x6, y6, 0, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, new Insets(10, 0, 0, 0), 0, 0);
        this.JPanel31.add(this.fileLabel, this.cons);
        final int x7 = 0;
        final int y7 = 1;
        final GridBagConstraints cons11 = this.cons;
        this.setConstraints(x7, y7, 0, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, new Insets(10, 0, 0, 0), 0, 0);
        this.JPanel31.add(this.mainLabel, this.cons);
        this.JPanel41.add(this.animationLabel, "Center");
        this.inset = new Insets(10, 10, 0, 10);
        final int x8 = 0;
        final int y8 = 0;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.1;
        final double wtY5 = 0.0;
        final GridBagConstraints cons12 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons13 = this.cons;
        this.setConstraints(x8, y8, width5, height5, wtX5, wtY5, anchor5, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JPanel2, this.cons);
        this.JPanel2.setLayout(new GridBagLayout());
        this.inset = new Insets(10, 0, 10, 10);
        final int x9 = 1;
        final int y9 = 0;
        final int width6 = 1;
        final int height6 = 1;
        final double wtX6 = 0.1;
        final double wtY6 = 0.0;
        final GridBagConstraints cons14 = this.cons;
        final int anchor6 = 10;
        final GridBagConstraints cons15 = this.cons;
        this.setConstraints(x9, y9, width6, height6, wtX6, wtY6, anchor6, 2, this.inset, 0, 0);
        this.JPanel2.add(this.JLabel1, this.cons);
        this.inset = new Insets(5, 0, 5, 0);
        final int x10 = 0;
        final int y10 = 1;
        final int width7 = 2;
        final int height7 = 1;
        final double wtX7 = 0.0;
        final double wtY7 = 0.0;
        final GridBagConstraints cons16 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons17 = this.cons;
        this.setConstraints(x10, y10, width7, height7, wtX7, wtY7, anchor7, 2, this.inset, 0, 0);
        this.JPanel2.add(this.JSeparator1, this.cons);
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
    
    public RevertProgress() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.JPanel41 = null;
        this.JPanel31 = null;
        this.revertProgressBar = null;
        this.percenLabel = null;
        this.mainLabel = null;
        this.subLabel = null;
        this.fileLabel = null;
        this.closeCheckBox = null;
        this.animationLabel = null;
        this.JPanel2 = null;
        this.JLabel1 = null;
        this.JSeparator1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.mainLabelLength = 0;
        this.subLabelLength = 0;
        this.init();
        RevertProgress.revertProgress = this;
    }
    
    public RevertProgress(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.JPanel41 = null;
        this.JPanel31 = null;
        this.revertProgressBar = null;
        this.percenLabel = null;
        this.mainLabel = null;
        this.subLabel = null;
        this.fileLabel = null;
        this.closeCheckBox = null;
        this.animationLabel = null;
        this.JPanel2 = null;
        this.JLabel1 = null;
        this.JSeparator1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.mainLabelLength = 0;
        this.subLabelLength = 0;
        this.applet = applet;
        this.init();
        RevertProgress.revertProgress = this;
    }
    
    public static RevertProgress getInstance() {
        return RevertProgress.revertProgress;
    }
    
    public void setFileLabelText(final String fileName) {
        this.fileLabel.setText(fileName);
    }
    
    public void setMainLabelText(final String fileName) {
        this.setMainLabelText(fileName, 0, null);
    }
    
    public void setMainLabelText(String fileName, final int fontSize, final String fontColor) {
        if (fileName != null) {
            fileName = UpdateManagerUtil.getNewText(fileName, fontSize, fontColor);
            this.mainLabelLength = fileName.length();
            final int newWidth = UpdateManagerUtil.getTextWidth(fontSize, this.subLabelLength, this.mainLabelLength);
            UpdateManagerUtil.resizeThirdPanel(newWidth, newWidth);
        }
        this.mainLabel.setText(fileName);
    }
    
    public void setSubLabelText(String fileName, final int fontSize, final String fontColor) {
        if (fileName != null) {
            fileName = UpdateManagerUtil.getNewText(fileName, fontSize, fontColor);
            this.subLabelLength = fileName.length();
            final int newWidth = UpdateManagerUtil.getTextWidth(fontSize, this.subLabelLength, this.mainLabelLength);
            UpdateManagerUtil.resizeThirdPanel(newWidth, newWidth);
        }
        this.subLabel.setText(fileName);
    }
    
    public void setPercenLabelText(final String fileName) {
        this.percenLabel.setText(fileName);
    }
    
    public void setPercenStatus(final int status) {
        this.revertProgressBar.setValue(status);
    }
    
    public void setDefaultCursor() {
        final UninstallUI unin = (UninstallUI)this.getTopLevelAncestor();
        unin.setDefaultCursor();
    }
    
    public void updateTheVersion() {
        final UninstallUI unin = (UninstallUI)this.getTopLevelAncestor();
        if (this.closeCheckBox.isSelected()) {
            unin.updateTheVersion(true);
        }
        else {
            unin.updateTheVersion(false);
        }
    }
    
    public void updateImage(final String imgPath) {
        this.animationLabel.setIcon(Utility.findImage(imgPath, this.applet, true));
    }
    
    static {
        RevertProgress.revertProgress = null;
    }
}
