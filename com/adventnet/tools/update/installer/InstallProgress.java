package com.adventnet.tools.update.installer;

import javax.swing.Icon;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.util.Properties;
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

public class InstallProgress extends JPanel implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JPanel JPanel4;
    JPanel JPanel3;
    public JProgressBar installProgressBar;
    public JLabel percenLabel;
    public JLabel mainLabel;
    JCheckBox closeCheckBox;
    JLabel fileLabel;
    JLabel animationLabel;
    JPanel JPanel5;
    JLabel JLabel4;
    JLabel installDirLabel;
    JLabel JLabel8;
    JLabel ppmSizeLabel;
    JPanel JPanel2;
    JLabel JLabel1;
    JSeparator JSeparator1;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    public JLabel subLabel;
    private int mainLabelLength;
    private int subLabelLength;
    private boolean testbool;
    private static InstallProgress installProgress;
    
    public InstallProgress() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.JPanel4 = null;
        this.JPanel3 = null;
        this.installProgressBar = null;
        this.percenLabel = null;
        this.mainLabel = null;
        this.closeCheckBox = null;
        this.fileLabel = null;
        this.animationLabel = null;
        this.JPanel5 = null;
        this.JLabel4 = null;
        this.installDirLabel = null;
        this.JLabel8 = null;
        this.ppmSizeLabel = null;
        this.JPanel2 = null;
        this.JLabel1 = null;
        this.JSeparator1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.subLabel = null;
        this.mainLabelLength = 0;
        this.subLabelLength = 0;
        this.init();
        InstallProgress.installProgress = this;
    }
    
    public InstallProgress(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.JPanel4 = null;
        this.JPanel3 = null;
        this.installProgressBar = null;
        this.percenLabel = null;
        this.mainLabel = null;
        this.closeCheckBox = null;
        this.fileLabel = null;
        this.animationLabel = null;
        this.JPanel5 = null;
        this.JLabel4 = null;
        this.installDirLabel = null;
        this.JLabel8 = null;
        this.ppmSizeLabel = null;
        this.JPanel2 = null;
        this.JLabel1 = null;
        this.JSeparator1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.subLabel = null;
        this.mainLabelLength = 0;
        this.subLabelLength = 0;
        this.applet = applet;
        this.init();
        InstallProgress.installProgress = this;
    }
    
    public void init() {
        if (this.getParameter("RESOURCE_PROPERTIES") != null) {
            this.localePropertiesFileName = this.getParameter("RESOURCE_PROPERTIES");
        }
        InstallProgress.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 538, this.getPreferredSize().height + 517));
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
            this.showStatus(InstallProgress.resourceBundle.getString("Error in init method"), ex);
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
        if (value == null) {
            if (input.equals("RESOURCE_LOCALE")) {
                value = "en_US";
            }
            if (input.equals("RESOURCE_PROPERTIES")) {
                value = "UpdateManagerResources";
            }
        }
        return value;
    }
    
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
    
    public void setProperties(final Properties props) {
        if (this.po != null) {
            this.po.setParameters(props);
        }
    }
    
    public void setUpProperties() {
        try {
            this.JPanel1.setBorder(new BevelBorder(0));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.JPanel1, ex);
        }
        this.installProgressBar.setFont(UpdateManagerUtil.getFont());
        try {
            this.percenLabel.setHorizontalAlignment(2);
            this.percenLabel.setForeground(new Color(-16777216));
            this.percenLabel.setHorizontalTextPosition(4);
            this.percenLabel.setText(InstallProgress.resourceBundle.getString("  "));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.percenLabel, ex);
        }
        this.percenLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.mainLabel.setHorizontalAlignment(2);
            this.mainLabel.setForeground(new Color(-16777216));
            this.mainLabel.setHorizontalTextPosition(4);
            this.mainLabel.setText(InstallProgress.resourceBundle.getString(" "));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.mainLabel, ex);
        }
        this.mainLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.subLabel.setHorizontalAlignment(2);
            this.subLabel.setForeground(new Color(-16777216));
            this.subLabel.setHorizontalTextPosition(4);
            this.subLabel.setText(InstallProgress.resourceBundle.getString(""));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.subLabel, ex);
        }
        this.subLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.closeCheckBox.setHorizontalTextPosition(4);
            this.closeCheckBox.setMnemonic(86);
            this.closeCheckBox.setText(InstallProgress.resourceBundle.getString("View Readme and Installed files."));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.closeCheckBox, ex);
        }
        this.closeCheckBox.setSelected(true);
        this.closeCheckBox.setFont(UpdateManagerUtil.getFont());
        try {
            this.fileLabel.setHorizontalAlignment(2);
            this.fileLabel.setForeground(new Color(-16777216));
            this.fileLabel.setHorizontalTextPosition(4);
            this.fileLabel.setText(InstallProgress.resourceBundle.getString(" "));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.fileLabel, ex);
        }
        this.fileLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.animationLabel.setForeground(new Color(-16777216));
            this.animationLabel.setHorizontalTextPosition(4);
            this.animationLabel.setHorizontalAlignment(0);
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.animationLabel, ex);
        }
        this.animationLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel4.setHorizontalAlignment(2);
            this.JLabel4.setForeground(new Color(-16777216));
            this.JLabel4.setHorizontalTextPosition(4);
            this.JLabel4.setText(InstallProgress.resourceBundle.getString("  Installation Path :"));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel4, ex);
        }
        this.JLabel4.setFont(UpdateManagerUtil.getFont());
        try {
            this.installDirLabel.setForeground(new Color(-16777216));
            this.installDirLabel.setText(InstallProgress.resourceBundle.getString(" "));
            this.installDirLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
            this.installDirLabel.setBackground(new Color(-3355444));
            this.installDirLabel.setEnabled(true);
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.installDirLabel, ex);
        }
        this.installDirLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel8.setHorizontalAlignment(2);
            this.JLabel8.setForeground(new Color(-16777216));
            this.JLabel8.setHorizontalTextPosition(4);
            this.JLabel8.setText(InstallProgress.resourceBundle.getString("Service Pack Size :"));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel8, ex);
        }
        this.JLabel8.setFont(UpdateManagerUtil.getFont());
        try {
            this.ppmSizeLabel.setHorizontalAlignment(2);
            this.ppmSizeLabel.setForeground(new Color(-16777216));
            this.ppmSizeLabel.setHorizontalTextPosition(4);
            this.ppmSizeLabel.setText(InstallProgress.resourceBundle.getString(""));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.ppmSizeLabel, ex);
        }
        this.ppmSizeLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel1.setHorizontalAlignment(2);
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setHorizontalTextPosition(4);
            this.JLabel1.setText(InstallProgress.resourceBundle.getString("Installation Status"));
        }
        catch (final Exception ex) {
            this.showStatus(InstallProgress.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel1, ex);
        }
        this.JLabel1.setFont(UpdateManagerUtil.getBoldFont());
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.JPanel4 = new JPanel();
        this.JPanel3 = new JPanel();
        this.installProgressBar = new JProgressBar();
        this.percenLabel = new JLabel();
        this.mainLabel = new JLabel();
        this.subLabel = new JLabel();
        this.closeCheckBox = new JCheckBox();
        this.fileLabel = new JLabel();
        this.animationLabel = new JLabel();
        this.JPanel5 = new JPanel();
        this.JLabel4 = new JLabel();
        this.installDirLabel = new JLabel();
        this.JLabel8 = new JLabel();
        this.ppmSizeLabel = new JLabel();
        this.JPanel2 = new JPanel();
        this.JLabel1 = new JLabel();
        this.JSeparator1 = new JSeparator();
        this.initializeParameters();
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
        this.JPanel1.add(this.JPanel4, this.cons);
        this.JPanel4.setLayout(new BorderLayout(5, 5));
        this.JPanel4.add(this.JPanel3, "South");
        this.JPanel3.setLayout(new GridBagLayout());
        this.setConstraints(0, 5, this.cons.gridwidth, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, new Insets(10, 0, 0, 0), 0, 0);
        this.JPanel3.add(this.closeCheckBox, this.cons);
        final int x3 = 0;
        final int y3 = 4;
        final GridBagConstraints cons5 = this.cons;
        this.setConstraints(x3, y3, 0, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, this.cons.insets, 0, 0);
        this.JPanel3.add(this.subLabel, this.cons);
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
        this.JPanel3.add(this.installProgressBar, this.cons);
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
        this.JPanel3.add(this.percenLabel, this.cons);
        final int x6 = 0;
        final int y6 = 2;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x6, y6, 0, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, new Insets(10, 0, 0, 0), 0, 0);
        this.JPanel3.add(this.fileLabel, this.cons);
        final int x7 = 0;
        final int y7 = 1;
        final GridBagConstraints cons11 = this.cons;
        this.setConstraints(x7, y7, 0, this.cons.gridheight, this.cons.weightx, this.cons.weighty, this.cons.anchor, this.cons.fill, new Insets(10, 0, 0, 0), 0, 0);
        this.JPanel3.add(this.mainLabel, this.cons);
        this.JPanel4.add(this.animationLabel, "Center");
        this.JPanel4.add(this.JPanel5, "North");
        this.JPanel5.setLayout(new GridBagLayout());
        this.inset = new Insets(0, 0, 0, 5);
        final int x8 = 0;
        final int y8 = 0;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.0;
        final double wtY5 = 0.0;
        final GridBagConstraints cons12 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons13 = this.cons;
        this.setConstraints(x8, y8, width5, height5, wtX5, wtY5, anchor5, 0, this.inset, 0, 0);
        this.JPanel5.add(this.JLabel4, this.cons);
        this.inset = new Insets(0, 0, 0, 0);
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
        this.JPanel5.add(this.installDirLabel, this.cons);
        this.inset = new Insets(0, 0, 0, 5);
        final int x10 = 0;
        final int y10 = 1;
        final int width7 = 1;
        final int height7 = 1;
        final double wtX7 = 0.0;
        final double wtY7 = 0.0;
        final GridBagConstraints cons16 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons17 = this.cons;
        this.setConstraints(x10, y10, width7, height7, wtX7, wtY7, anchor7, 0, this.inset, 0, 0);
        this.JPanel5.add(this.JLabel8, this.cons);
        this.inset = new Insets(0, 0, 0, 0);
        final int x11 = 1;
        final int y11 = 1;
        final int width8 = 1;
        final int height8 = 1;
        final double wtX8 = 0.1;
        final double wtY8 = 0.0;
        final GridBagConstraints cons18 = this.cons;
        final int anchor8 = 10;
        final GridBagConstraints cons19 = this.cons;
        this.setConstraints(x11, y11, width8, height8, wtX8, wtY8, anchor8, 2, this.inset, 0, 0);
        this.JPanel5.add(this.ppmSizeLabel, this.cons);
        this.inset = new Insets(10, 10, 0, 10);
        final int x12 = 0;
        final int y12 = 0;
        final int width9 = 1;
        final int height9 = 1;
        final double wtX9 = 0.1;
        final double wtY9 = 0.0;
        final GridBagConstraints cons20 = this.cons;
        final int anchor9 = 10;
        final GridBagConstraints cons21 = this.cons;
        this.setConstraints(x12, y12, width9, height9, wtX9, wtY9, anchor9, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JPanel2, this.cons);
        this.JPanel2.setLayout(new GridBagLayout());
        this.inset = new Insets(10, 0, 10, 10);
        final int x13 = 1;
        final int y13 = 0;
        final int width10 = 1;
        final int height10 = 1;
        final double wtX10 = 0.1;
        final double wtY10 = 0.0;
        final GridBagConstraints cons22 = this.cons;
        final int anchor10 = 10;
        final GridBagConstraints cons23 = this.cons;
        this.setConstraints(x13, y13, width10, height10, wtX10, wtY10, anchor10, 2, this.inset, 0, 0);
        this.JPanel2.add(this.JLabel1, this.cons);
        this.inset = new Insets(5, 0, 5, 0);
        final int x14 = 0;
        final int y14 = 1;
        final int width11 = 2;
        final int height11 = 1;
        final double wtX11 = 0.0;
        final double wtY11 = 0.0;
        final GridBagConstraints cons24 = this.cons;
        final int anchor11 = 10;
        final GridBagConstraints cons25 = this.cons;
        this.setConstraints(x14, y14, width11, height11, wtX11, wtY11, anchor11, 2, this.inset, 0, 0);
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
    
    public boolean showReadme() {
        return this.testbool;
    }
    
    public static InstallProgress getInstance() {
        return InstallProgress.installProgress;
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
    
    public void setSubLabelText(final String fileName) {
        this.setSubLabelText(fileName, 0, null);
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
        this.installProgressBar.setValue(status);
    }
    
    public void setInstallDirectory(final String dir) {
        this.installDirLabel.setText(dir);
    }
    
    public void setPPMSize(final String size) {
        this.ppmSizeLabel.setText(size);
    }
    
    public void setDefaultCursor() {
        final InstallUI in = (InstallUI)this.getTopLevelAncestor();
        in.setDefaultCursor();
    }
    
    public void updateTheVersion() {
        final InstallUI unin = (InstallUI)this.getTopLevelAncestor();
        unin.updateTheVersion();
    }
    
    public void enableTheButton() {
        final InstallUI unin = (InstallUI)this.getTopLevelAncestor();
        unin.enableTheButton();
    }
    
    public void updateImage(final String imgPath) {
        this.animationLabel.setIcon(Utility.findImage(imgPath, this.applet, true));
    }
    
    public void failure() {
        final InstallUI unin = (InstallUI)this.getTopLevelAncestor();
        unin.failure();
    }
    
    static {
        InstallProgress.resourceBundle = null;
        InstallProgress.installProgress = null;
    }
}
