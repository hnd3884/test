package com.adventnet.tools.update.installer;

import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.io.File;
import com.adventnet.tools.update.UpdateData;
import java.awt.Cursor;
import javax.swing.JDialog;
import javax.swing.event.ListSelectionEvent;
import java.util.Properties;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.ListSelectionModel;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.Icon;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.HashMap;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;

public class ContextScreen extends JPanel implements ListSelectionListener, ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel JPanel11;
    JPanel JPanel31;
    JLabel OptionalPatchImage;
    JTextArea JLabel21;
    JLabel JLabel3;
    ContextSensitiveHelpButton contextHelpButton;
    JPanel JPanel4;
    JScrollPane scrollPane;
    JTable JTable1;
    JCheckBox JCheckBox1;
    JPanel JPanel1;
    JTextArea descLabel;
    JButton JButton1;
    JSeparator JSeparator11;
    JLabel descTitleLabel;
    JLabel InstallImage1;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    private HashMap optionalContext;
    private Hashtable contextTable;
    private String patchPath;
    
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
        ContextScreen.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 462, this.getPreferredSize().height + 452));
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
            this.showStatus(ContextScreen.resourceBundle.getString("Error in init method"), ex);
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
        try {
            this.JPanel11.setBorder(new BevelBorder(0));
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JPanel11, ex);
        }
        try {
            this.OptionalPatchImage.setHorizontalAlignment(2);
            this.OptionalPatchImage.setForeground(new Color(-16777216));
            this.OptionalPatchImage.setHorizontalTextPosition(4);
            this.OptionalPatchImage.setVerticalTextPosition(1);
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.OptionalPatchImage, ex);
        }
        this.OptionalPatchImage.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/context_install.png", this.applet, true));
        this.OptionalPatchImage.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel21.setForeground(new Color(-16777216));
            this.JLabel21.setBackground(new Color(-3355444));
            this.JLabel21.setLineWrap(true);
            this.JLabel21.setWrapStyleWord(true);
            this.JLabel21.setEditable(false);
            this.JLabel21.setText(ContextScreen.resourceBundle.getString("Apart from the mandatory updates, this Service Pack also provides other optional upgrades. You can choose to install them if required. Select the required items listed below for installation."));
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel21, ex);
        }
        this.JLabel21.setFont(UpdateManagerUtil.getFont());
        try {
            this.JLabel3.setHorizontalAlignment(2);
            this.JLabel3.setForeground(new Color(-16777216));
            this.JLabel3.setHorizontalTextPosition(4);
            this.JLabel3.setFont(UpdateManagerUtil.getBoldFont());
            this.JLabel3.setText(ContextScreen.resourceBundle.getString("Optional Upgrades"));
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JLabel3, ex);
        }
        try {
            this.contextHelpButton.setHorizontalTextPosition(4);
            this.contextHelpButton.setText(ContextScreen.resourceBundle.getString(""));
            this.contextHelpButton.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/context_help.png", this.applet, true));
            this.contextHelpButton.setPreferredSize(new Dimension(32, 27));
            this.contextHelpButton.setMaximumSize(new Dimension(32, 27));
            this.contextHelpButton.setMinimumSize(new Dimension(32, 27));
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.contextHelpButton, ex);
        }
        this.contextHelpButton.setFont(UpdateManagerUtil.getFont());
        try {
            this.scrollPane.setForeground(new Color(-16777216));
            this.scrollPane.setAlignmentY(0.0f);
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.scrollPane, ex);
        }
        this.scrollPane.setFont(UpdateManagerUtil.getFont());
        try {
            this.JTable1.setCellSelectionEnabled(false);
            this.JTable1.setRowSelectionAllowed(true);
        }
        catch (final Exception ex) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JTable1, ex);
        }
        final ListSelectionModel rowSM = this.JTable1.getSelectionModel();
        rowSM.setSelectionMode(0);
        rowSM.addListSelectionListener(this);
        try {
            this.JCheckBox1.setHorizontalTextPosition(4);
            this.JCheckBox1.setText(ContextScreen.resourceBundle.getString("Select All"));
            this.JCheckBox1.setMnemonic('S');
        }
        catch (final Exception ex2) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JCheckBox1, ex2);
        }
        this.JCheckBox1.setFont(UpdateManagerUtil.getFont());
        try {
            this.descLabel.setForeground(new Color(-16777216));
            this.descLabel.setLineWrap(true);
            this.descLabel.setWrapStyleWord(true);
            this.descLabel.setBackground(new Color(-3355444));
            this.descLabel.setEditable(false);
        }
        catch (final Exception ex2) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.descLabel, ex2);
        }
        this.descLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.JButton1.setHorizontalTextPosition(4);
            this.JButton1.setMnemonic('R');
            this.JButton1.setText(ContextScreen.resourceBundle.getString("Show Readme"));
        }
        catch (final Exception ex2) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.JButton1, ex2);
        }
        this.JButton1.setFont(UpdateManagerUtil.getFont());
        try {
            this.descTitleLabel.setHorizontalAlignment(2);
            this.descTitleLabel.setForeground(new Color(-16777216));
            this.descTitleLabel.setHorizontalTextPosition(4);
            this.descTitleLabel.setText(ContextScreen.resourceBundle.getString("Description"));
        }
        catch (final Exception ex2) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.descTitleLabel, ex2);
        }
        this.descTitleLabel.setFont(UpdateManagerUtil.getBoldFont());
        try {
            this.InstallImage1.setHorizontalAlignment(2);
            this.InstallImage1.setForeground(new Color(-16777216));
            this.InstallImage1.setHorizontalTextPosition(4);
            this.InstallImage1.setVerticalAlignment(0);
            this.InstallImage1.setVerticalTextPosition(0);
        }
        catch (final Exception ex2) {
            this.showStatus(ContextScreen.resourceBundle.getString("Exception while setting properties for bean ") + this.InstallImage1, ex2);
        }
        this.InstallImage1.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/readme.png", this.applet, true));
        this.InstallImage1.setFont(UpdateManagerUtil.getFont());
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel11 = new JPanel();
        this.JPanel31 = new JPanel();
        this.OptionalPatchImage = new JLabel();
        this.JLabel21 = new JTextArea();
        this.JLabel3 = new JLabel();
        this.contextHelpButton = new ContextSensitiveHelpButton("./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png");
        this.JPanel4 = new JPanel();
        this.scrollPane = new JScrollPane();
        this.JTable1 = new JTable();
        this.JCheckBox1 = new JCheckBox();
        this.JPanel1 = new JPanel();
        this.descLabel = new JTextArea();
        this.JButton1 = new JButton();
        this.JSeparator11 = new JSeparator();
        this.descTitleLabel = new JLabel();
        this.InstallImage1 = new JLabel();
        this.initializeParameters();
        (this.contextHelpButton = ContextSensitiveHelpButton.getHelpButton(UpdateManagerUtil.getHelpXmlFilePath(), UpdateManagerUtil.getHelpHtmlFilePath(), "./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png")).setHelpWindowSize(new Dimension(400, 75));
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
        this.inset = new Insets(5, 5, 0, 5);
        final int x2 = 0;
        final int y2 = 0;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.1;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 1, this.inset, 0, 0);
        this.JPanel11.add(this.JPanel31, this.cons);
        this.JPanel31.setLayout(new GridBagLayout());
        this.inset = new Insets(2, 10, 5, 10);
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
        this.inset = new Insets(0, 10, 5, 10);
        final int x4 = 1;
        final int y4 = 1;
        final int width4 = 2;
        final int height4 = 1;
        final double wtX4 = 0.1;
        final double wtY4 = 0.0;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 1, this.inset, 0, 0);
        this.JPanel31.add(this.JLabel21, this.cons);
        this.inset = new Insets(5, 10, 5, 20);
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
        this.inset = new Insets(0, 90, 5, 0);
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
        this.JPanel31.add(this.contextHelpButton, this.cons);
        this.inset = new Insets(0, 5, 0, 5);
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
        this.JPanel11.add(this.JPanel4, this.cons);
        this.JPanel4.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 60, 5, 60);
        final int x8 = 0;
        final int y8 = 1;
        final int width8 = 1;
        final int height8 = 1;
        final double wtX8 = 0.3;
        final double wtY8 = 0.2;
        final GridBagConstraints cons15 = this.cons;
        final int anchor8 = 11;
        final GridBagConstraints cons16 = this.cons;
        this.setConstraints(x8, y8, width8, height8, wtX8, wtY8, anchor8, 1, this.inset, 0, 10);
        this.JPanel4.add(this.scrollPane, this.cons);
        this.scrollPane.getViewport().add(this.JTable1);
        this.inset = new Insets(5, 60, 0, 0);
        final int x9 = 0;
        final int y9 = 0;
        final int width9 = 1;
        final int height9 = 1;
        final double wtX9 = 0.0;
        final double wtY9 = 0.0;
        final GridBagConstraints cons17 = this.cons;
        final int anchor9 = 18;
        final GridBagConstraints cons18 = this.cons;
        this.setConstraints(x9, y9, width9, height9, wtX9, wtY9, anchor9, 0, this.inset, 0, 0);
        this.JPanel4.add(this.JCheckBox1, this.cons);
        this.inset = new Insets(0, 5, 5, 5);
        final int x10 = 0;
        final int y10 = 2;
        final int width10 = 1;
        final int height10 = 1;
        final double wtX10 = 0.1;
        final double wtY10 = 0.0;
        final GridBagConstraints cons19 = this.cons;
        final int anchor10 = 10;
        final GridBagConstraints cons20 = this.cons;
        this.setConstraints(x10, y10, width10, height10, wtX10, wtY10, anchor10, 1, this.inset, 0, 0);
        this.JPanel11.add(this.JPanel1, this.cons);
        this.JPanel1.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 10, 2, 10);
        final int x11 = 1;
        final int y11 = 2;
        final int width11 = 1;
        final int height11 = 1;
        final double wtX11 = 0.2;
        final double wtY11 = 0.1;
        final GridBagConstraints cons21 = this.cons;
        final int anchor11 = 10;
        final GridBagConstraints cons22 = this.cons;
        this.setConstraints(x11, y11, width11, height11, wtX11, wtY11, anchor11, 1, this.inset, 0, 4);
        this.JPanel1.add(this.descLabel, this.cons);
        this.inset = new Insets(7, 7, 0, 7);
        final int x12 = 1;
        final int y12 = 3;
        final int width12 = 1;
        final int height12 = 1;
        final double wtX12 = 0.0;
        final double wtY12 = 0.0;
        final GridBagConstraints cons23 = this.cons;
        final int anchor12 = 13;
        final GridBagConstraints cons24 = this.cons;
        this.setConstraints(x12, y12, width12, height12, wtX12, wtY12, anchor12, 0, this.inset, 0, 0);
        this.JPanel1.add(this.JButton1, this.cons);
        this.inset = new Insets(0, 0, 0, 0);
        final int x13 = 0;
        final int y13 = 0;
        final int width13 = 2;
        final int height13 = 1;
        final double wtX13 = 0.0;
        final double wtY13 = 0.0;
        final GridBagConstraints cons25 = this.cons;
        final int anchor13 = 10;
        final GridBagConstraints cons26 = this.cons;
        this.setConstraints(x13, y13, width13, height13, wtX13, wtY13, anchor13, 1, this.inset, 0, 0);
        this.JPanel1.add(this.JSeparator11, this.cons);
        this.inset = new Insets(5, 10, 0, 20);
        final int x14 = 1;
        final int y14 = 1;
        final int width14 = 1;
        final int height14 = 1;
        final double wtX14 = 0.1;
        final double wtY14 = 0.0;
        final GridBagConstraints cons27 = this.cons;
        final int anchor14 = 10;
        final GridBagConstraints cons28 = this.cons;
        this.setConstraints(x14, y14, width14, height14, wtX14, wtY14, anchor14, 2, this.inset, 0, 0);
        this.JPanel1.add(this.descTitleLabel, this.cons);
        this.inset = new Insets(2, 10, 5, 10);
        final int x15 = 0;
        final int y15 = 1;
        final int width15 = 1;
        final int height15 = 2;
        final double wtX15 = 0.0;
        final double wtY15 = 0.0;
        final GridBagConstraints cons29 = this.cons;
        final int anchor15 = 10;
        final GridBagConstraints cons30 = this.cons;
        this.setConstraints(x15, y15, width15, height15, wtX15, wtY15, anchor15, 1, this.inset, 0, 0);
        this.JPanel1.add(this.InstallImage1, this.cons);
    }
    
    public void setUpConnections() {
        final JButton1_JTable1_conn JButton1_JTable1_conn1 = new JButton1_JTable1_conn();
        this.JButton1.addActionListener(JButton1_JTable1_conn1);
        final JCheckBox1_JTable1_conn JCheckBox1_JTable1_conn1 = new JCheckBox1_JTable1_conn();
        this.JCheckBox1.addItemListener(JCheckBox1_JTable1_conn1);
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
    
    public ContextScreen() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel11 = null;
        this.JPanel31 = null;
        this.OptionalPatchImage = null;
        this.JLabel21 = null;
        this.JLabel3 = null;
        this.contextHelpButton = null;
        this.JPanel4 = null;
        this.scrollPane = null;
        this.JTable1 = null;
        this.JCheckBox1 = null;
        this.JPanel1 = null;
        this.descLabel = null;
        this.JButton1 = null;
        this.JSeparator11 = null;
        this.descTitleLabel = null;
        this.InstallImage1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.optionalContext = null;
        this.contextTable = null;
        this.patchPath = null;
        this.init();
    }
    
    public ContextScreen(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel11 = null;
        this.JPanel31 = null;
        this.OptionalPatchImage = null;
        this.JLabel21 = null;
        this.JLabel3 = null;
        this.contextHelpButton = null;
        this.JPanel4 = null;
        this.scrollPane = null;
        this.JTable1 = null;
        this.JCheckBox1 = null;
        this.JPanel1 = null;
        this.descLabel = null;
        this.JButton1 = null;
        this.JSeparator11 = null;
        this.descTitleLabel = null;
        this.InstallImage1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.optionalContext = null;
        this.contextTable = null;
        this.patchPath = null;
        this.applet = applet;
        this.init();
    }
    
    @Override
    public void valueChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        final ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (!lsm.isSelectionEmpty()) {
            final int selectedRow = lsm.getMinSelectionIndex();
            final NonEditableTableModel dtm = (NonEditableTableModel)this.JTable1.getModel();
            if (this.optionalContext != null) {
                final String name = (String)dtm.getValueAt(selectedRow, 1);
                final String desc = this.optionalContext.get(name);
                if (desc != null) {
                    this.descTitleLabel.setText(ContextScreen.resourceBundle.getString("Description of ") + name);
                    this.descLabel.setText(desc);
                }
                else {
                    this.descTitleLabel.setText(ContextScreen.resourceBundle.getString("Description") + " ");
                    this.descLabel.setText("");
                }
            }
        }
    }
    
    public void setOptionalContext(final HashMap hash) {
        this.optionalContext = hash;
    }
    
    public void setContextTable(final Hashtable table) {
        this.contextTable = table;
    }
    
    public void setPatchFilePath(final String path) {
        this.patchPath = path;
    }
    
    private JDialog getDialog() {
        return (JDialog)this.getTopLevelAncestor();
    }
    
    private void contextReadmeActionPerformed() {
        this.JButton1.setEnabled(false);
        this.setCursor(new Cursor(3));
        final int selected = this.JTable1.getSelectedRow();
        if (selected != -1) {
            final ContextReadme read = new ContextReadme();
            final String selContext = (String)this.JTable1.getValueAt(selected, 1);
            final UpdateData updateData = this.contextTable.get(selContext);
            final String readmefilename = updateData.getContextReadme();
            final String dirToUnzip = System.getProperty("user.dir");
            final File checkFile = new File(dirToUnzip + File.separator + "patchtemp");
            String fileName = null;
            if (readmefilename.lastIndexOf("/") != -1 || readmefilename.lastIndexOf("\\") != -1) {
                if (readmefilename.startsWith(selContext)) {
                    fileName = readmefilename.substring(selContext.length() + 1);
                }
            }
            else {
                fileName = readmefilename;
            }
            read.writeReadmeFile(checkFile, fileName, this.patchPath, readmefilename);
            read.displayReadme("patchtemp" + File.separator + fileName, this.getDialog(), selContext + " context");
        }
        this.JButton1.setEnabled(true);
        this.setCursor(new Cursor(0));
    }
    
    private void setHelpFiles() {
        this.scrollPane.setName("ContextScreen_Table_Button");
        this.JButton1.setName("ContextScreen_Readme_Button");
    }
    
    static {
        ContextScreen.resourceBundle = null;
    }
    
    class JButton1_JTable1_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContextScreen.this.contextReadmeActionPerformed();
                }
            }, "ContextReadmeThread").start();
        }
    }
    
    class JCheckBox1_JTable1_conn implements ItemListener, Serializable
    {
        @Override
        public void itemStateChanged(final ItemEvent arg0) {
            final NonEditableTableModel dtm = (NonEditableTableModel)ContextScreen.this.JTable1.getModel();
            final int count = dtm.getRowCount();
            final int change = arg0.getStateChange();
            if (change == 1) {
                for (int i = 0; i < count; ++i) {
                    final JCheckBox jcb = (JCheckBox)dtm.getValueAt(i, 0);
                    jcb.setSelected(true);
                }
                ((InstallUI)ContextScreen.this.getDialog()).setCheckBoxCount(count);
            }
            else if (change == 2 && !((InstallUI)ContextScreen.this.getDialog()).getInnerCheckBoxState()) {
                for (int i = 0; i < count; ++i) {
                    final JCheckBox jcb = (JCheckBox)dtm.getValueAt(i, 0);
                    jcb.setSelected(false);
                }
                ((InstallUI)ContextScreen.this.getDialog()).setCheckBoxCount(0);
            }
            ((InstallUI)ContextScreen.this.getDialog()).setInnerCheckBoxState(false);
            ContextScreen.this.JTable1.repaint();
        }
    }
}
