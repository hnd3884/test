package com.adventnet.tools.update.installer;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Frame;
import javax.swing.JFrame;
import java.util.Properties;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.Color;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.applet.Applet;
import javax.swing.JDialog;

public class PatchDetails extends JDialog implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel mainPanel;
    JButton closeButton;
    JTabbedPane tabbedPane;
    JPanel General;
    JLabel JLabel1;
    JLabel patchNameLabel;
    JLabel JLabel3;
    JLabel descLabel;
    JSeparator JSeparator1;
    JLabel JLabel5;
    JLabel sizeLabel;
    JLabel JLabel7;
    JLabel dateLabel;
    JSeparator JSeparator2;
    JPanel JPanel2;
    JLabel JLabel8;
    JLabel readMeLable;
    JScrollPane JScrollPane1;
    JTextArea logsTextArea;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    private String title;
    private String readMeUrl;
    JPanel readMePanel;
    
    @Override
    public void setVisible(final boolean bl) {
        if (bl) {
            this.init();
            this.start();
        }
        else {
            this.stop();
        }
        super.setVisible(bl);
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
    
    public void init() {
        if (this.getParameter("RESOURCE_PROPERTIES") != null) {
            this.localePropertiesFileName = this.getParameter("RESOURCE_PROPERTIES");
        }
        PatchDetails.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 517, this.getPreferredSize().height + 584);
        this.setTitle(PatchDetails.resourceBundle.getString("Patch Details - Update Manager"));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Error in init method"), ex);
        }
        this.initialized = true;
        if (this.title != null) {
            super.setTitle(this.title);
        }
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
    
    public void setUpProperties() {
        this.setSize(new Dimension(500, 500));
        try {
            this.closeButton.setHorizontalTextPosition(4);
            this.closeButton.setMnemonic(67);
            this.closeButton.setText(PatchDetails.resourceBundle.getString("Close"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.closeButton, ex);
        }
        this.closeButton.setFont(UpdateManagerUtil.getFont());
        this.tabbedPane.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel1.setHorizontalAlignment(2);
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setHorizontalTextPosition(4);
            this.JLabel1.setText(PatchDetails.resourceBundle.getString("Service Pack Name:"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel1, ex);
        }
        this.JLabel1.setFont(UpdateManagerUtil.getFont());
        try {
            this.patchNameLabel.setHorizontalAlignment(2);
            this.patchNameLabel.setForeground(new Color(-16777216));
            this.patchNameLabel.setHorizontalTextPosition(4);
            this.patchNameLabel.setText(PatchDetails.resourceBundle.getString("Service Pack"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.patchNameLabel, ex);
        }
        this.patchNameLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel3.setHorizontalAlignment(2);
            this.JLabel3.setForeground(new Color(-16777216));
            this.JLabel3.setHorizontalTextPosition(4);
            this.JLabel3.setText(PatchDetails.resourceBundle.getString("Description:"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel3, ex);
        }
        this.JLabel3.setFont(UpdateManagerUtil.getFont());
        try {
            this.descLabel.setHorizontalAlignment(2);
            this.descLabel.setForeground(new Color(-16777216));
            this.descLabel.setHorizontalTextPosition(4);
            this.descLabel.setText(PatchDetails.resourceBundle.getString("A One line Description about the Patch Goes here..."));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.descLabel, ex);
        }
        this.descLabel.setFont(UpdateManagerUtil.getFont());
        if (this.readMeUrl != null) {
            try {
                this.JLabel8.setHorizontalAlignment(2);
                this.JLabel8.setForeground(new Color(-16777216));
                this.JLabel8.setHorizontalTextPosition(4);
                this.JLabel8.setText(PatchDetails.resourceBundle.getString("Readme:"));
            }
            catch (final Exception ex) {
                this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel3, ex);
            }
            this.JLabel8.setFont(UpdateManagerUtil.getFont());
            try {
                this.readMeLable.setHorizontalAlignment(2);
                this.readMeLable.setForeground(new Color(-16777216));
                this.readMeLable.setHorizontalTextPosition(4);
                this.readMeLable.setText(PatchDetails.resourceBundle.getString("Read me url"));
            }
            catch (final Exception ex) {
                this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.descLabel, ex);
            }
            this.readMeLable.setFont(UpdateManagerUtil.getFont());
        }
        try {
            this.JLabel5.setHorizontalAlignment(2);
            this.JLabel5.setForeground(new Color(-16777216));
            this.JLabel5.setHorizontalTextPosition(4);
            this.JLabel5.setText(PatchDetails.resourceBundle.getString("Service Pack Size:"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel5, ex);
        }
        this.JLabel5.setFont(UpdateManagerUtil.getFont());
        try {
            this.sizeLabel.setHorizontalAlignment(2);
            this.sizeLabel.setForeground(new Color(-16777216));
            this.sizeLabel.setHorizontalTextPosition(4);
            this.sizeLabel.setText(PatchDetails.resourceBundle.getString("6.77 MB"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.sizeLabel, ex);
        }
        this.sizeLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel7.setHorizontalAlignment(2);
            this.JLabel7.setForeground(new Color(-16777216));
            this.JLabel7.setHorizontalTextPosition(4);
            this.JLabel7.setText(PatchDetails.resourceBundle.getString("Installation Time:"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel7, ex);
        }
        this.JLabel7.setFont(UpdateManagerUtil.getFont());
        try {
            this.dateLabel.setHorizontalAlignment(2);
            this.dateLabel.setForeground(new Color(-16777216));
            this.dateLabel.setHorizontalTextPosition(4);
            this.dateLabel.setText(PatchDetails.resourceBundle.getString("Thursday, April 25, 2002"));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.dateLabel, ex);
        }
        this.dateLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JPanel2.setBorder(new TitledBorder(new LineBorder(new Color(-16777216), 1), PatchDetails.resourceBundle.getString("Installed files"), 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.JPanel2, ex);
        }
        this.JPanel2.setBorder(new TitledBorder(new LineBorder(new Color(-16777216), 1), PatchDetails.resourceBundle.getString("Installed files."), 0, 0, UpdateManagerUtil.getFont(), new Color(-16777216)));
        try {
            this.logsTextArea.setWrapStyleWord(true);
            this.logsTextArea.setBorder(new BevelBorder(1));
            this.logsTextArea.setLineWrap(true);
            this.logsTextArea.setCaretColor(new Color(-16776961));
            this.logsTextArea.setEditable(false);
        }
        catch (final Exception ex) {
            this.showStatus(PatchDetails.resourceBundle.getString("Exception while setting properties for bean ") + this.logsTextArea, ex);
        }
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.mainPanel = new JPanel();
        this.closeButton = new JButton();
        this.tabbedPane = new JTabbedPane();
        this.General = new JPanel();
        this.JLabel1 = new JLabel();
        this.patchNameLabel = new JLabel();
        this.JLabel3 = new JLabel();
        this.descLabel = new JLabel();
        this.JSeparator1 = new JSeparator();
        this.JLabel5 = new JLabel();
        this.sizeLabel = new JLabel();
        this.JLabel7 = new JLabel();
        this.dateLabel = new JLabel();
        this.JSeparator2 = new JSeparator();
        this.JPanel2 = new JPanel();
        this.JScrollPane1 = new JScrollPane();
        this.logsTextArea = new JTextArea();
        this.initializeParameters();
        this.JLabel8 = new JLabel();
        this.readMeLable = new JLabel();
        this.readMePanel = new JPanel();
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 10, 5, 10);
        final int x = 0;
        final int y = 1;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.1;
        final double wtY = 0.0;
        final GridBagConstraints cons = this.cons;
        final int anchor = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 0);
        this.Top.add(this.mainPanel, this.cons);
        this.mainPanel.setLayout(new FlowLayout(1, 5, 5));
        this.mainPanel.add(this.closeButton);
        this.inset = new Insets(10, 10, 5, 10);
        final int x2 = 0;
        final int y2 = 0;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.2;
        final double wtY2 = 0.1;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 1, this.inset, 0, 0);
        this.Top.add(this.tabbedPane, this.cons);
        this.tabbedPane.addTab(PatchDetails.resourceBundle.getString("General"), null, this.General, null);
        this.General.setLayout(new GridBagLayout());
        this.inset = new Insets(20, 20, 5, 5);
        final int x3 = 0;
        final int y3 = 0;
        final int width3 = 1;
        final int height3 = 1;
        final double wtX3 = 0.0;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
        this.General.add(this.JLabel1, this.cons);
        this.inset = new Insets(20, 5, 5, 10);
        final int x4 = 1;
        final int y4 = 0;
        final int width4 = 1;
        final int height4 = 1;
        final double wtX4 = 0.0;
        final double wtY4 = 0.0;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 2, this.inset, 0, 0);
        this.General.add(this.patchNameLabel, this.cons);
        this.inset = new Insets(5, 20, 5, 5);
        final int x5 = 0;
        final int y5 = 1;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.0;
        final double wtY5 = 0.0;
        final GridBagConstraints cons9 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x5, y5, width5, height5, wtX5, wtY5, anchor5, 2, this.inset, 0, 0);
        this.General.add(this.JLabel3, this.cons);
        this.inset = new Insets(5, 5, 5, 10);
        final int x6 = 1;
        final int y6 = 1;
        final int width6 = 1;
        final int height6 = 1;
        final double wtX6 = 0.0;
        final double wtY6 = 0.0;
        final GridBagConstraints cons11 = this.cons;
        final int anchor6 = 10;
        final GridBagConstraints cons12 = this.cons;
        this.setConstraints(x6, y6, width6, height6, wtX6, wtY6, anchor6, 2, this.inset, 0, 0);
        this.General.add(this.descLabel, this.cons);
        this.inset = new Insets(5, 20, 5, 5);
        final int x7 = 0;
        final int y7 = 2;
        final int width7 = 1;
        final int height7 = 1;
        final double wtX7 = 0.0;
        final double wtY7 = 0.0;
        final GridBagConstraints cons13 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons14 = this.cons;
        this.setConstraints(x7, y7, width7, height7, wtX7, wtY7, anchor7, 2, this.inset, 0, 0);
        this.General.add(this.JLabel8, this.cons);
        this.inset = new Insets(5, 5, 5, 10);
        final int x8 = 1;
        final int y8 = 2;
        final int width8 = 1;
        final int height8 = 1;
        final double wtX8 = 0.0;
        final double wtY8 = 0.0;
        final GridBagConstraints cons15 = this.cons;
        final int anchor8 = 10;
        final GridBagConstraints cons16 = this.cons;
        this.setConstraints(x8, y8, width8, height8, wtX8, wtY8, anchor8, 2, this.inset, 0, 0);
        this.General.add(this.readMeLable, this.cons);
        this.inset = new Insets(5, 5, 5, 5);
        final int x9 = 0;
        final int y9 = 3;
        final int width9 = 2;
        final int height9 = 1;
        final double wtX9 = 0.0;
        final double wtY9 = 0.0;
        final GridBagConstraints cons17 = this.cons;
        final int anchor9 = 10;
        final GridBagConstraints cons18 = this.cons;
        this.setConstraints(x9, y9, width9, height9, wtX9, wtY9, anchor9, 2, this.inset, 0, 0);
        this.General.add(this.JSeparator1, this.cons);
        this.inset = new Insets(5, 20, 5, 5);
        final int x10 = 0;
        final int y10 = 4;
        final int width10 = 1;
        final int height10 = 1;
        final double wtX10 = 0.0;
        final double wtY10 = 0.0;
        final GridBagConstraints cons19 = this.cons;
        final int anchor10 = 10;
        final GridBagConstraints cons20 = this.cons;
        this.setConstraints(x10, y10, width10, height10, wtX10, wtY10, anchor10, 2, this.inset, 0, 0);
        this.General.add(this.JLabel5, this.cons);
        this.inset = new Insets(5, 5, 5, 10);
        final int x11 = 1;
        final int y11 = 4;
        final int width11 = 1;
        final int height11 = 1;
        final double wtX11 = 0.0;
        final double wtY11 = 0.0;
        final GridBagConstraints cons21 = this.cons;
        final int anchor11 = 10;
        final GridBagConstraints cons22 = this.cons;
        this.setConstraints(x11, y11, width11, height11, wtX11, wtY11, anchor11, 2, this.inset, 0, 0);
        this.General.add(this.sizeLabel, this.cons);
        this.inset = new Insets(5, 20, 5, 5);
        final int x12 = 0;
        final int y12 = 5;
        final int width12 = 1;
        final int height12 = 1;
        final double wtX12 = 0.0;
        final double wtY12 = 0.0;
        final GridBagConstraints cons23 = this.cons;
        final int anchor12 = 10;
        final GridBagConstraints cons24 = this.cons;
        this.setConstraints(x12, y12, width12, height12, wtX12, wtY12, anchor12, 2, this.inset, 0, 0);
        this.General.add(this.JLabel7, this.cons);
        this.inset = new Insets(5, 5, 5, 10);
        final int x13 = 1;
        final int y13 = 5;
        final int width13 = 1;
        final int height13 = 1;
        final double wtX13 = 0.0;
        final double wtY13 = 0.0;
        final GridBagConstraints cons25 = this.cons;
        final int anchor13 = 10;
        final GridBagConstraints cons26 = this.cons;
        this.setConstraints(x13, y13, width13, height13, wtX13, wtY13, anchor13, 2, this.inset, 0, 0);
        this.General.add(this.dateLabel, this.cons);
        this.inset = new Insets(5, 5, 5, 5);
        final int x14 = 0;
        final int y14 = 6;
        final int width14 = 2;
        final int height14 = 1;
        final double wtX14 = 0.0;
        final double wtY14 = 0.0;
        final GridBagConstraints cons27 = this.cons;
        final int anchor14 = 10;
        final GridBagConstraints cons28 = this.cons;
        this.setConstraints(x14, y14, width14, height14, wtX14, wtY14, anchor14, 2, this.inset, 0, 0);
        this.General.add(this.JSeparator2, this.cons);
        this.inset = new Insets(5, 10, 10, 10);
        final int x15 = 0;
        final int y15 = 7;
        final int width15 = 2;
        final int height15 = 1;
        final double wtX15 = 0.2;
        final double wtY15 = 0.1;
        final GridBagConstraints cons29 = this.cons;
        final int anchor15 = 10;
        final GridBagConstraints cons30 = this.cons;
        this.setConstraints(x15, y15, width15, height15, wtX15, wtY15, anchor15, 1, this.inset, 0, 0);
        this.General.add(this.JPanel2, this.cons);
        this.JPanel2.setLayout(new GridBagLayout());
        this.inset = new Insets(7, 7, 7, 7);
        final int x16 = 0;
        final int y16 = 0;
        final int width16 = 1;
        final int height16 = 1;
        final double wtX16 = 0.2;
        final double wtY16 = 0.1;
        final GridBagConstraints cons31 = this.cons;
        final int anchor16 = 10;
        final GridBagConstraints cons32 = this.cons;
        this.setConstraints(x16, y16, width16, height16, wtX16, wtY16, anchor16, 1, this.inset, 0, 0);
        this.JPanel2.add(this.JScrollPane1, this.cons);
        this.JScrollPane1.getViewport().add(this.logsTextArea);
    }
    
    public void setUpConnections() {
        final closeButton_mainPanel_conn closeButton_mainPanel_conn1 = new closeButton_mainPanel_conn();
        this.closeButton.addActionListener(closeButton_mainPanel_conn1);
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    public PatchDetails() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.mainPanel = null;
        this.closeButton = null;
        this.tabbedPane = null;
        this.General = null;
        this.JLabel1 = null;
        this.patchNameLabel = null;
        this.JLabel3 = null;
        this.descLabel = null;
        this.JSeparator1 = null;
        this.JLabel5 = null;
        this.sizeLabel = null;
        this.JLabel7 = null;
        this.dateLabel = null;
        this.JSeparator2 = null;
        this.JPanel2 = null;
        this.JLabel8 = null;
        this.readMeLable = null;
        this.JScrollPane1 = null;
        this.logsTextArea = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.title = null;
        this.readMeUrl = null;
        this.readMePanel = null;
        this.pack();
    }
    
    public PatchDetails(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.mainPanel = null;
        this.closeButton = null;
        this.tabbedPane = null;
        this.General = null;
        this.JLabel1 = null;
        this.patchNameLabel = null;
        this.JLabel3 = null;
        this.descLabel = null;
        this.JSeparator1 = null;
        this.JLabel5 = null;
        this.sizeLabel = null;
        this.JLabel7 = null;
        this.dateLabel = null;
        this.JSeparator2 = null;
        this.JPanel2 = null;
        this.JLabel8 = null;
        this.readMeLable = null;
        this.JScrollPane1 = null;
        this.logsTextArea = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.title = null;
        this.readMeUrl = null;
        this.readMePanel = null;
        this.applet = applet;
        this.pack();
        this.setDefaultCloseOperation(2);
    }
    
    public void setProperties(final Properties props) {
        if (this.po != null) {
            this.po.setParameters(props);
        }
    }
    
    @Override
    public void setParameterObject(final ParameterObject paramObj) {
        this.po = paramObj;
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
    
    public PatchDetails(final JFrame owner) {
        super(owner);
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.mainPanel = null;
        this.closeButton = null;
        this.tabbedPane = null;
        this.General = null;
        this.JLabel1 = null;
        this.patchNameLabel = null;
        this.JLabel3 = null;
        this.descLabel = null;
        this.JSeparator1 = null;
        this.JLabel5 = null;
        this.sizeLabel = null;
        this.JLabel7 = null;
        this.dateLabel = null;
        this.JSeparator2 = null;
        this.JPanel2 = null;
        this.JLabel8 = null;
        this.readMeLable = null;
        this.JScrollPane1 = null;
        this.logsTextArea = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.title = null;
        this.readMeUrl = null;
        this.readMePanel = null;
        this.pack();
    }
    
    public void setDialogTitle(final String s) {
        this.title = s;
    }
    
    public void showCornered() {
        final Point pt = new Point(30, 30);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension dim = toolkit.getScreenSize();
        final int total = (int)dim.getWidth();
        final int ori = pt.x + 500;
        if (ori > total) {
            final int diff = ori - total;
            pt.x -= diff;
            this.setLocation(pt);
        }
        else {
            this.setLocation(pt);
        }
        this.setVisible(true);
    }
    
    public void setPatchReadMeUrl(final String url) {
        this.readMeUrl = url;
    }
    
    static {
        PatchDetails.resourceBundle = null;
    }
    
    class closeButton_mainPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            PatchDetails.this.setVisible(false);
        }
    }
}
