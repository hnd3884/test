package com.adventnet.tools.update.installer;

import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import com.adventnet.tools.update.viewer.DiffUtility;
import java.util.Vector;
import java.util.HashMap;
import javax.swing.JFrame;
import java.io.File;
import java.awt.Cursor;
import javax.swing.JDialog;
import java.util.Properties;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.Icon;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.JPanel;

public class PatchScreen extends JPanel implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel JPanel4;
    JPanel JPanel5;
    JLabel InstallLabel1;
    JLabel InstallImage1;
    JTextArea JLabel11;
    JPanel JPanel2;
    JButton diffButton;
    JButton viewReadme;
    JPanel JPanel6;
    JLabel InstallLabel;
    JLabel InstallImage;
    JTextArea JLabel1;
    ContextSensitiveHelpButton installHelpButton;
    JPanel JPanel1;
    JButton browsePatch;
    JTextField patchPath;
    JSeparator JSeparator1;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    private Common common;
    
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
        if (this.getParameter("RESOURCE_PROPERTIES") != null) {
            this.localePropertiesFileName = this.getParameter("RESOURCE_PROPERTIES");
        }
        PatchScreen.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 458, this.getPreferredSize().height + 429));
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
            this.showStatus(PatchScreen.resourceBundle.getString("Error in init method"), ex);
        }
        this.initialized = true;
        this.setHelpFiles();
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
            if (input.equals("RESOURCE_PROPERTIES")) {
                value = "UpdateManagerResources";
            }
            if (input.equals("RESOURCE_LOCALE")) {
                value = "en_US";
            }
        }
        return value;
    }
    
    public void setUpProperties() {
        try {
            this.JPanel4.setBorder(new BevelBorder(0));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JPanel4, ex);
        }
        try {
            this.InstallLabel1.setHorizontalAlignment(2);
            this.InstallLabel1.setForeground(new Color(-16777216));
            this.InstallLabel1.setHorizontalTextPosition(4);
            this.InstallLabel1.setFont(UpdateManagerUtil.getBoldFont());
            if (UpdateManagerUtil.isDeploymentToolEnabled()) {
                this.InstallLabel1.setText(PatchScreen.resourceBundle.getString("DiffViewer/Readme"));
            }
            else {
                this.InstallLabel1.setText(PatchScreen.resourceBundle.getString("Readme"));
            }
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.InstallLabel1, ex);
        }
        try {
            this.InstallImage1.setHorizontalAlignment(2);
            this.InstallImage1.setFont(UpdateManagerUtil.getFont());
            this.InstallImage1.setForeground(new Color(-16777216));
            this.InstallImage1.setHorizontalTextPosition(4);
            this.InstallImage1.setVerticalTextPosition(1);
            this.InstallImage1.setVerticalAlignment(1);
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.InstallImage1, ex);
        }
        this.InstallImage1.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/readme.png", this.applet, true));
        try {
            this.JLabel11.setFont(UpdateManagerUtil.getFont());
            this.JLabel11.setForeground(new Color(-16777216));
            this.JLabel11.setBackground(new Color(-3355444));
            this.JLabel11.setLineWrap(true);
            this.JLabel11.setWrapStyleWord(true);
            this.JLabel11.setEditable(false);
            if (UpdateManagerUtil.isDeploymentToolEnabled()) {
                this.JLabel11.setText(PatchScreen.resourceBundle.getString("To know the details of the Service Pack(Upgrade) click on the 'DiffViewer / Show Readme' buttons."));
            }
            else {
                this.JLabel11.setText(PatchScreen.resourceBundle.getString("To know more about this Service Pack (Upgrade) click on the 'Show Readme' button. "));
            }
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel11, ex);
        }
        try {
            this.diffButton.setFont(UpdateManagerUtil.getFont());
            this.diffButton.setHorizontalTextPosition(4);
            this.diffButton.setText(PatchScreen.resourceBundle.getString("DiffViewer"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.diffButton, ex);
        }
        try {
            this.viewReadme.setFont(UpdateManagerUtil.getFont());
            this.viewReadme.setHorizontalTextPosition(4);
            this.viewReadme.setMnemonic(82);
            this.viewReadme.setText(PatchScreen.resourceBundle.getString("Show Readme"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.viewReadme, ex);
        }
        try {
            this.InstallLabel.setHorizontalAlignment(2);
            this.InstallLabel.setForeground(new Color(-16777216));
            this.InstallLabel.setHorizontalTextPosition(4);
            this.InstallLabel.setFont(UpdateManagerUtil.getBoldFont());
            this.InstallLabel.setText(PatchScreen.resourceBundle.getString("Upgrade"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.InstallLabel, ex);
        }
        try {
            this.InstallImage.setHorizontalAlignment(2);
            this.InstallImage.setFont(UpdateManagerUtil.getFont());
            this.InstallImage.setForeground(new Color(-16777216));
            this.InstallImage.setHorizontalTextPosition(4);
            this.InstallImage.setVerticalAlignment(0);
            this.InstallImage.setVerticalTextPosition(0);
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.InstallImage, ex);
        }
        this.InstallImage.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/install.png", this.applet, true));
        try {
            this.JLabel1.setFont(UpdateManagerUtil.getFont());
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setBackground(new Color(-3355444));
            this.JLabel1.setLineWrap(true);
            this.JLabel1.setWrapStyleWord(true);
            this.JLabel1.setEditable(false);
            this.JLabel1.setText(PatchScreen.resourceBundle.getString("To upgrade the product, please specify the location (path) of the corresponding service pack file (.ppm). You can also use the Browse button for selecting the file."));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel1, ex);
        }
        try {
            this.installHelpButton.setFont(UpdateManagerUtil.getFont());
            this.installHelpButton.setHorizontalTextPosition(4);
            this.installHelpButton.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/context_help.png", this.applet, true));
            this.installHelpButton.setText(PatchScreen.resourceBundle.getString(""));
            this.installHelpButton.setPreferredSize(new Dimension(32, 27));
            this.installHelpButton.setMaximumSize(new Dimension(32, 27));
            this.installHelpButton.setMinimumSize(new Dimension(32, 27));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.installHelpButton, ex);
        }
        try {
            this.browsePatch.setFont(UpdateManagerUtil.getFont());
            this.browsePatch.setHorizontalTextPosition(4);
            this.browsePatch.setText(PatchScreen.resourceBundle.getString("Browse"));
            this.browsePatch.setMnemonic('w');
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.browsePatch, ex);
        }
        try {
            this.patchPath.setHorizontalAlignment(2);
            this.patchPath.setFont(UpdateManagerUtil.getFont());
            this.patchPath.setMinimumSize(new Dimension(4, 24));
            this.patchPath.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            this.patchPath.setPreferredSize(new Dimension(4, 24));
        }
        catch (final Exception ex) {
            this.showStatus(PatchScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.patchPath, ex);
        }
        this.viewReadme.setPreferredSize(new Dimension(this.viewReadme.getPreferredSize().width + 1, this.viewReadme.getPreferredSize().height + 1));
        if (this.patchPath.getText().equals("")) {
            this.viewReadme.setEnabled(false);
            this.diffButton.setEnabled(false);
        }
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel4 = new JPanel();
        this.JPanel5 = new JPanel();
        this.InstallLabel1 = new JLabel();
        this.InstallImage1 = new JLabel();
        this.JLabel11 = new JTextArea();
        this.JPanel2 = new JPanel();
        this.diffButton = new JButton();
        this.viewReadme = new JButton();
        this.JPanel6 = new JPanel();
        this.InstallLabel = new JLabel();
        this.InstallImage = new JLabel();
        this.JLabel1 = new JTextArea();
        this.installHelpButton = new ContextSensitiveHelpButton("./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png");
        this.JPanel1 = new JPanel();
        this.browsePatch = new JButton();
        this.patchPath = new JTextField();
        this.JSeparator1 = new JSeparator();
        this.initializeParameters();
        (this.installHelpButton = ContextSensitiveHelpButton.getHelpButton(UpdateManagerUtil.getHelpXmlFilePath(), UpdateManagerUtil.getHelpHtmlFilePath(), "./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png")).setHelpWindowSize(new Dimension(400, 75));
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
        this.Top.add(this.JPanel4, this.cons);
        this.JPanel4.setLayout(new GridBagLayout());
        this.inset = new Insets(0, 5, 5, 5);
        final int x2 = 0;
        final int y2 = 2;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.1;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 1, this.inset, 0, 0);
        this.JPanel4.add(this.JPanel5, this.cons);
        this.JPanel5.setLayout(new GridBagLayout());
        this.inset = new Insets(10, 10, 5, 10);
        final int x3 = 1;
        final int y3 = 0;
        final int width3 = 2;
        final int height3 = 1;
        final double wtX3 = 0.1;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
        this.JPanel5.add(this.InstallLabel1, this.cons);
        this.inset = new Insets(10, 10, 5, 10);
        final int x4 = 0;
        final int y4 = 0;
        final int width4 = 1;
        final int height4 = 2;
        final double wtX4 = 0.0;
        final double wtY4 = 0.0;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 2, this.inset, 0, 0);
        this.JPanel5.add(this.InstallImage1, this.cons);
        this.inset = new Insets(0, 10, 5, 10);
        final int x5 = 1;
        final int y5 = 1;
        final int width5 = 2;
        final int height5 = 1;
        final double wtX5 = 0.1;
        final double wtY5 = 0.0;
        final GridBagConstraints cons9 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x5, y5, width5, height5, wtX5, wtY5, anchor5, 1, this.inset, 0, 0);
        this.JPanel5.add(this.JLabel11, this.cons);
        this.inset = new Insets(5, 5, 5, 5);
        final int x6 = 2;
        final int y6 = 2;
        final int width6 = 1;
        final int height6 = 1;
        final double wtX6 = 0.0;
        final double wtY6 = 0.0;
        final GridBagConstraints cons11 = this.cons;
        final int anchor6 = 13;
        final GridBagConstraints cons12 = this.cons;
        this.setConstraints(x6, y6, width6, height6, wtX6, wtY6, anchor6, 0, this.inset, 0, 0);
        this.JPanel5.add(this.JPanel2, this.cons);
        this.JPanel2.setLayout(new FlowLayout(1, 5, 5));
        this.JPanel2.add(this.diffButton);
        this.JPanel2.add(this.viewReadme);
        this.inset = new Insets(5, 5, 0, 5);
        final int x7 = 0;
        final int y7 = 0;
        final int width7 = 1;
        final int height7 = 1;
        final double wtX7 = 0.1;
        final double wtY7 = 0.0;
        final GridBagConstraints cons13 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons14 = this.cons;
        this.setConstraints(x7, y7, width7, height7, wtX7, wtY7, anchor7, 1, this.inset, 0, 0);
        this.JPanel4.add(this.JPanel6, this.cons);
        this.JPanel6.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 20, 5, 5);
        final int x8 = 1;
        final int y8 = 0;
        final int width8 = 1;
        final int height8 = 1;
        final double wtX8 = 0.1;
        final double wtY8 = 0.0;
        final GridBagConstraints cons15 = this.cons;
        final int anchor8 = 10;
        final GridBagConstraints cons16 = this.cons;
        this.setConstraints(x8, y8, width8, height8, wtX8, wtY8, anchor8, 2, this.inset, 0, 0);
        this.JPanel6.add(this.InstallLabel, this.cons);
        this.inset = new Insets(0, 10, 5, 5);
        final int x9 = 0;
        final int y9 = 0;
        final int width9 = 1;
        final int height9 = 2;
        final double wtX9 = 0.0;
        final double wtY9 = 0.0;
        final GridBagConstraints cons17 = this.cons;
        final int anchor9 = 10;
        final GridBagConstraints cons18 = this.cons;
        this.setConstraints(x9, y9, width9, height9, wtX9, wtY9, anchor9, 1, this.inset, 0, 0);
        this.JPanel6.add(this.InstallImage, this.cons);
        this.inset = new Insets(0, 20, 5, 5);
        final int x10 = 1;
        final int y10 = 1;
        final int width10 = 2;
        final int height10 = 1;
        final double wtX10 = 0.0;
        final double wtY10 = 0.0;
        final GridBagConstraints cons19 = this.cons;
        final int anchor10 = 10;
        final GridBagConstraints cons20 = this.cons;
        this.setConstraints(x10, y10, width10, height10, wtX10, wtY10, anchor10, 2, this.inset, 0, 0);
        this.JPanel6.add(this.JLabel1, this.cons);
        this.inset = new Insets(0, 90, 5, 0);
        final int x11 = 2;
        final int y11 = 0;
        final int width11 = 1;
        final int height11 = 1;
        final double wtX11 = 0.0;
        final double wtY11 = 0.0;
        final GridBagConstraints cons21 = this.cons;
        final int anchor11 = 10;
        final GridBagConstraints cons22 = this.cons;
        this.setConstraints(x11, y11, width11, height11, wtX11, wtY11, anchor11, 0, this.inset, 0, 0);
        this.JPanel6.add(this.installHelpButton, this.cons);
        this.inset = new Insets(0, 5, 0, 5);
        final int x12 = 0;
        final int y12 = 1;
        final int width12 = 1;
        final int height12 = 1;
        final double wtX12 = 0.1;
        final double wtY12 = 0.1;
        final GridBagConstraints cons23 = this.cons;
        final int anchor12 = 10;
        final GridBagConstraints cons24 = this.cons;
        this.setConstraints(x12, y12, width12, height12, wtX12, wtY12, anchor12, 1, this.inset, 0, 0);
        this.JPanel4.add(this.JPanel1, this.cons);
        this.JPanel1.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 5, 5, 5);
        final int x13 = 1;
        final int y13 = 0;
        final int width13 = 1;
        final int height13 = 1;
        final double wtX13 = 0.0;
        final double wtY13 = 0.0;
        final GridBagConstraints cons25 = this.cons;
        final int anchor13 = 10;
        final GridBagConstraints cons26 = this.cons;
        this.setConstraints(x13, y13, width13, height13, wtX13, wtY13, anchor13, 0, this.inset, 0, 0);
        this.JPanel1.add(this.browsePatch, this.cons);
        this.inset = new Insets(5, 40, 5, 5);
        final int x14 = 0;
        final int y14 = 0;
        final int width14 = 1;
        final int height14 = 1;
        final double wtX14 = 0.1;
        final double wtY14 = 0.0;
        final GridBagConstraints cons27 = this.cons;
        final int anchor14 = 10;
        final GridBagConstraints cons28 = this.cons;
        this.setConstraints(x14, y14, width14, height14, wtX14, wtY14, anchor14, 2, this.inset, 0, 0);
        this.JPanel1.add(this.patchPath, this.cons);
        this.inset = new Insets(15, 5, 0, 5);
        final int x15 = 0;
        final int y15 = 1;
        final int width15 = 2;
        final int height15 = 1;
        final double wtX15 = 0.0;
        final double wtY15 = 0.0;
        final GridBagConstraints cons29 = this.cons;
        final int anchor15 = 10;
        final GridBagConstraints cons30 = this.cons;
        this.setConstraints(x15, y15, width15, height15, wtX15, wtY15, anchor15, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JSeparator1, this.cons);
        if (UpdateManagerUtil.isDeploymentToolEnabled()) {
            this.diffButton.setVisible(true);
        }
        else {
            this.diffButton.setVisible(false);
        }
    }
    
    public void setUpConnections() {
        final patchPath_viewReadme_conn patchPath_viewReadme_conn1 = new patchPath_viewReadme_conn();
        this.patchPath.addInputMethodListener(patchPath_viewReadme_conn1);
        final viewReadme_patchPath_conn viewReadme_patchPath_conn1 = new viewReadme_patchPath_conn();
        this.viewReadme.addActionListener(viewReadme_patchPath_conn1);
        final browsePatch_patchPath_conn browsePatch_patchPath_conn1 = new browsePatch_patchPath_conn();
        this.browsePatch.addActionListener(browsePatch_patchPath_conn1);
        final patchPath_patchPath_conn1 patchPath_patchPath_conn11 = new patchPath_patchPath_conn1();
        this.patchPath.addFocusListener(patchPath_patchPath_conn11);
        final diffButton_diffButton_conn diffButton_diffButton_conn1 = new diffButton_diffButton_conn();
        this.diffButton.addActionListener(diffButton_diffButton_conn1);
        final patchPath_patchPath_conn patchPath_patchPath_conn12 = new patchPath_patchPath_conn();
        this.patchPath.addKeyListener(patchPath_patchPath_conn12);
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
    
    public PatchScreen() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel4 = null;
        this.JPanel5 = null;
        this.InstallLabel1 = null;
        this.InstallImage1 = null;
        this.JLabel11 = null;
        this.JPanel2 = null;
        this.diffButton = null;
        this.viewReadme = null;
        this.JPanel6 = null;
        this.InstallLabel = null;
        this.InstallImage = null;
        this.JLabel1 = null;
        this.installHelpButton = null;
        this.JPanel1 = null;
        this.browsePatch = null;
        this.patchPath = null;
        this.JSeparator1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.common = null;
        this.init();
    }
    
    public PatchScreen(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel4 = null;
        this.JPanel5 = null;
        this.InstallLabel1 = null;
        this.InstallImage1 = null;
        this.JLabel11 = null;
        this.JPanel2 = null;
        this.diffButton = null;
        this.viewReadme = null;
        this.JPanel6 = null;
        this.InstallLabel = null;
        this.InstallImage = null;
        this.JLabel1 = null;
        this.installHelpButton = null;
        this.JPanel1 = null;
        this.browsePatch = null;
        this.patchPath = null;
        this.JSeparator1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.common = null;
        this.applet = applet;
        this.init();
    }
    
    private JDialog getFrame() {
        return (JDialog)this.getTopLevelAncestor();
    }
    
    public void readmeActionPerformed() {
        this.viewReadme.setEnabled(false);
        this.setCursor(new Cursor(3));
        final String patchFile = this.patchPath.getText();
        final String dirToUnzip = System.getProperty("user.dir");
        final File checkFile = new File(dirToUnzip + File.separator + "patchtemp");
        final ContextReadme read = new ContextReadme();
        final JFrame frame = ((InstallUI)this.getFrame()).getTopFrame();
        final boolean check = read.extractInfFile(dirToUnzip, patchFile, frame);
        if (check) {
            read.readTheInfFile("patchtemp" + File.separator + "inf.xml");
            final String readmefilename = read.getPatchFileReadme();
            final String patchVersion = read.getPatchVersion();
            read.writeReadmeFile(checkFile, readmefilename, patchFile, readmefilename);
            read.displayReadme("patchtemp" + File.separator + readmefilename, frame, patchVersion + " version");
        }
        this.setCursor(new Cursor(0));
        this.viewReadme.setEnabled(true);
    }
    
    public Common getCommon() {
        return this.common;
    }
    
    private void validateTheFile(final String patchFile) {
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final String confProductName = ((InstallUI)this.getFrame()).getConfProductName();
        final String confProductVersion = ((InstallUI)this.getFrame()).getConfProductVersion();
        final String confSubProductName = ((InstallUI)this.getFrame()).getConfSubProductVersion();
        final JFrame frame = ((InstallUI)this.getFrame()).getTopFrame();
        this.common = new Common(dirToUnzip, patchFile, true, confProductName);
        if (this.common.install(frame)) {
            this.patchPath.setText(patchFile);
            final HashMap hashList = this.common.getTheCompatibleContext(confSubProductName);
            final Vector conv = this.common.getTheContext();
            if (conv.contains(confSubProductName) || conv.contains("NoContext")) {
                if (hashList.isEmpty()) {
                    ((InstallUI)this.getFrame()).nextButton.setText(PatchScreen.resourceBundle.getString("Finish"));
                }
                else {
                    ((InstallUI)this.getFrame()).nextButton.setText(PatchScreen.resourceBundle.getString("Next"));
                }
            }
            this.viewReadme.setEnabled(true);
            this.diffButton.setEnabled(true);
            ((InstallUI)this.getFrame()).setNextButtonEnabled(true);
        }
        else {
            this.viewReadme.setEnabled(false);
            this.diffButton.setEnabled(false);
            ((InstallUI)this.getFrame()).setNextButtonEnabled(false);
        }
        this.setCursor(new Cursor(0));
        this.setNextFocusableComponent(((InstallUI)this.getFrame()).nextButton);
        ((InstallUI)this.getFrame()).nextButton.requestFocus();
    }
    
    private void setHelpFiles() {
        this.browsePatch.setName("PatchScreen_Browse_Button");
        this.viewReadme.setName("PatchScreen_Readme_Button");
        this.patchPath.setName("PatchScreen_TextField_Button");
    }
    
    public void diffViewerActionPerformed() {
        this.diffButton.setEnabled(false);
        this.setCursor(new Cursor(3));
        final String patchFile = this.patchPath.getText();
        final String dirToUnzip = System.getProperty("user.dir");
        final String confProductName = ((InstallUI)this.getFrame()).getConfProductName();
        final String confProductVersion = ((InstallUI)this.getFrame()).getConfProductVersion();
        final String confSubProductName = ((InstallUI)this.getFrame()).getConfSubProductVersion();
        final JFrame frame = ((InstallUI)this.getFrame()).getTopFrame();
        final DiffUtility diff = new DiffUtility(frame, confProductName, confProductVersion, confSubProductName, patchFile);
        this.setCursor(new Cursor(0));
        this.diffButton.setEnabled(true);
    }
    
    static {
        PatchScreen.resourceBundle = null;
    }
    
    class browsePatch_patchPath_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            PatchScreen.this.browsePatch.setEnabled(false);
            PatchScreen.this.setCursor(new Cursor(3));
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(0);
            fileChooser.setDialogTitle(PatchScreen.resourceBundle.getString("Select a File"));
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
            fileChooser.addChoosableFileFilter(new PatchFilter("ppm"));
            final int fileChoosen = fileChooser.showOpenDialog(PatchScreen.this.getFrame());
            PatchScreen.this.browsePatch.setEnabled(true);
            if (fileChoosen != 0) {
                PatchScreen.this.setCursor(new Cursor(0));
                return;
            }
            final File selFile = fileChooser.getSelectedFile();
            if (!selFile.exists()) {
                JOptionPane.showMessageDialog(PatchScreen.this.getFrame(), PatchScreen.resourceBundle.getString("The file that you have specified doesnot exist."), PatchScreen.resourceBundle.getString("Error"), 2);
                PatchScreen.this.setCursor(new Cursor(0));
                return;
            }
            final String patchFile = selFile.getAbsolutePath().trim();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PatchScreen.this.validateTheFile(patchFile);
                }
            }, "BrowseThread").start();
        }
    }
    
    class viewReadme_patchPath_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PatchScreen.this.readmeActionPerformed();
                }
            }, "MainReadmeThread").start();
        }
    }
    
    class patchPath_patchPath_conn extends KeyAdapter implements Serializable
    {
        @Override
        public void keyReleased(final KeyEvent arg0) {
            final String patchFile = PatchScreen.this.patchPath.getText().trim();
            if (patchFile.equals("")) {
                PatchScreen.this.viewReadme.setEnabled(false);
                PatchScreen.this.diffButton.setEnabled(false);
                ((InstallUI)PatchScreen.this.getFrame()).setNextButtonEnabled(false);
            }
            else if (arg0.getKeyCode() == 10) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PatchScreen.this.validateTheFile(patchFile);
                    }
                }, "EnterBrowseThread").start();
            }
            else if (arg0.getKeyCode() == 9) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PatchScreen.this.validateTheFile(patchFile);
                    }
                }, "TabBrowseThread").start();
            }
            else {
                PatchScreen.this.viewReadme.setEnabled(false);
                PatchScreen.this.diffButton.setEnabled(false);
                ((InstallUI)PatchScreen.this.getFrame()).setNextButtonEnabled(false);
            }
        }
    }
    
    class patchPath_viewReadme_conn implements InputMethodListener, Serializable
    {
        @Override
        public void inputMethodTextChanged(final InputMethodEvent arg0) {
            if (PatchScreen.this.patchPath.getText().equals("")) {
                PatchScreen.this.viewReadme.setEnabled(false);
                PatchScreen.this.diffButton.setEnabled(false);
                ((InstallUI)PatchScreen.this.getFrame()).setNextButtonEnabled(false);
            }
            else {
                PatchScreen.this.viewReadme.setEnabled(true);
                PatchScreen.this.diffButton.setEnabled(true);
                ((InstallUI)PatchScreen.this.getFrame()).setNextButtonEnabled(true);
            }
        }
        
        @Override
        public void caretPositionChanged(final InputMethodEvent arg0) {
        }
    }
    
    class patchPath_patchPath_conn1 extends FocusAdapter implements Serializable
    {
        @Override
        public void focusLost(final FocusEvent arg0) {
        }
    }
    
    class diffButton_diffButton_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PatchScreen.this.diffViewerActionPerformed();
                }
            }, "DiffViewerThread").start();
        }
    }
}
