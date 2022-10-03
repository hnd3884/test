package com.adventnet.tools.prevalent;

import java.util.Properties;
import java.awt.GridBagLayout;
import javax.swing.border.BevelBorder;
import java.awt.Component;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.EtchedBorder;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.JPanel;

public class LUIRegisterDetails extends JPanel
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JLabel userLabel;
    JTextField userNameTextField;
    JLabel companyLabel;
    JTextField companyNameTextField;
    JLabel JLabel1;
    JTextField keyTextField;
    JTextArea JLabel11;
    JLabel JLabel3;
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
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 554, this.getPreferredSize().height + 390));
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
        final String value = null;
        return value;
    }
    
    public void setUpProperties() {
        try {
            this.Top.setBorder(new TitledBorder(new EtchedBorder(0), "Licensee Details", 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.Top, ex);
        }
        try {
            this.Top.setBorder(new TitledBorder(new EtchedBorder(0), ToolsUtils.getString("Licensee Details"), 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.Top, ex);
        }
        try {
            this.JPanel1.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JPanel1, ex);
        }
        try {
            this.userLabel.setHorizontalAlignment(2);
            this.userLabel.setFont(new Font("SansSerif", 0, 12));
            this.userLabel.setForeground(new Color(-16777216));
            this.userLabel.setHorizontalTextPosition(4);
            this.userLabel.setDisplayedMnemonic('U');
            this.userLabel.setText("User Name");
            this.userLabel.setLabelFor(this.userNameTextField);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.userLabel, ex);
        }
        this.userLabel.setText(ToolsUtils.getString("User Name"));
        try {
            this.userNameTextField.setHorizontalAlignment(2);
            this.userNameTextField.setFont(new Font("SansSerif", 0, 12));
            this.userNameTextField.setBorder(new BevelBorder(1));
            this.userNameTextField.setEditable(true);
            this.userNameTextField.setEnabled(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.userNameTextField, ex);
        }
        try {
            this.companyLabel.setHorizontalAlignment(2);
            this.companyLabel.setFont(new Font("SansSerif", 0, 12));
            this.companyLabel.setForeground(new Color(-16777216));
            this.companyLabel.setHorizontalTextPosition(4);
            this.companyLabel.setText("Company Name");
            this.companyLabel.setDisplayedMnemonic('C');
            this.companyLabel.setLabelFor(this.companyNameTextField);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.companyLabel, ex);
        }
        this.companyLabel.setText(ToolsUtils.getString("Company Name"));
        try {
            this.companyNameTextField.setHorizontalAlignment(2);
            this.companyNameTextField.setFont(new Font("SansSerif", 0, 12));
            this.companyNameTextField.setBorder(new BevelBorder(1));
            this.companyNameTextField.setEditable(true);
            this.companyNameTextField.setEnabled(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.companyNameTextField, ex);
        }
        try {
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setFont(new Font("sansserif", 0, 12));
            this.JLabel1.setText("License Key");
            this.JLabel1.setDisplayedMnemonic('L');
            this.JLabel1.setLabelFor(this.keyTextField);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel1, ex);
        }
        this.JLabel1.setText(ToolsUtils.getString("License Key"));
        try {
            this.keyTextField.setHorizontalAlignment(2);
            this.keyTextField.setFont(new Font("SansSerif", 0, 12));
            this.keyTextField.setBorder(new BevelBorder(1));
            this.keyTextField.setEditable(true);
            this.keyTextField.setEnabled(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.keyTextField, ex);
        }
        try {
            this.JLabel11.setFont(new Font("SansSerif", 0, 12));
            this.JLabel11.setForeground(new Color(-16777216));
            this.JLabel11.setBackground(new Color(-3355444));
            this.JLabel11.setLineWrap(true);
            this.JLabel11.setDoubleBuffered(true);
            this.JLabel11.setWrapStyleWord(true);
            this.JLabel11.setText("Specify the following details and provide the License key.");
            this.JLabel11.setEditable(false);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel11, ex);
        }
        this.JLabel11.setText(ToolsUtils.getString("Specify the following details and provide the License key."));
    }
    
    public void initVariables() {
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.userLabel = new JLabel();
        this.userNameTextField = new JTextField();
        this.companyLabel = new JLabel();
        this.companyNameTextField = new JTextField();
        this.JLabel1 = new JLabel();
        this.keyTextField = new JTextField();
        this.JLabel11 = new JTextArea();
        this.JLabel3 = new JLabel();
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
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.JPanel1, "Center");
        this.JPanel1.setLayout(new GridBagLayout());
        this.inset = new Insets(15, 10, 5, 5);
        final int x = 0;
        final int y = 1;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.0;
        final double wtY = 0.0;
        final GridBagConstraints cons = this.cons;
        final int anchor = 13;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 0);
        this.JPanel1.add(this.userLabel, this.cons);
        this.inset = new Insets(5, 10, 5, 5);
        final int x2 = 0;
        final int y2 = 2;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.1;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 2, this.inset, 50, 0);
        this.JPanel1.add(this.userNameTextField, this.cons);
        this.inset = new Insets(5, 10, 5, 5);
        final int x3 = 0;
        final int y3 = 3;
        final int width3 = 1;
        final int height3 = 1;
        final double wtX3 = 0.0;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
        this.JPanel1.add(this.companyLabel, this.cons);
        this.inset = new Insets(5, 10, 0, 5);
        final int x4 = 0;
        final int y4 = 4;
        final int width4 = 1;
        final int height4 = 1;
        final double wtX4 = 0.0;
        final double wtY4 = 0.0;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 2, this.inset, 0, 0);
        this.JPanel1.add(this.companyNameTextField, this.cons);
        this.inset = new Insets(5, 10, 5, 5);
        final int x5 = 0;
        final int y5 = 5;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.1;
        final double wtY5 = 0.0;
        final GridBagConstraints cons9 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x5, y5, width5, height5, wtX5, wtY5, anchor5, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JLabel1, this.cons);
        this.inset = new Insets(5, 10, 5, 5);
        final int x6 = 0;
        final int y6 = 6;
        final int width6 = 1;
        final int height6 = 1;
        final double wtX6 = 0.0;
        final double wtY6 = 0.0;
        final GridBagConstraints cons11 = this.cons;
        final int anchor6 = 10;
        final GridBagConstraints cons12 = this.cons;
        this.setConstraints(x6, y6, width6, height6, wtX6, wtY6, anchor6, 2, this.inset, 0, 0);
        this.JPanel1.add(this.keyTextField, this.cons);
        this.inset = new Insets(20, 10, 5, 5);
        final int x7 = 0;
        final int y7 = 0;
        final int width7 = 2;
        final int height7 = 1;
        final double wtX7 = 0.0;
        final double wtY7 = 0.0;
        final GridBagConstraints cons13 = this.cons;
        final int anchor7 = 10;
        final GridBagConstraints cons14 = this.cons;
        this.setConstraints(x7, y7, width7, height7, wtX7, wtY7, anchor7, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JLabel11, this.cons);
        this.inset = new Insets(0, 0, 0, 0);
        final int x8 = 1;
        final int y8 = 1;
        final int width8 = 1;
        final int height8 = 1;
        final double wtX8 = 0.0;
        final double wtY8 = 0.0;
        final GridBagConstraints cons15 = this.cons;
        final int anchor8 = 10;
        final GridBagConstraints cons16 = this.cons;
        this.setConstraints(x8, y8, width8, height8, wtX8, wtY8, anchor8, 0, this.inset, 80, 0);
        this.JPanel1.add(this.JLabel3, this.cons);
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
    }
    
    public LUIRegisterDetails() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.userLabel = null;
        this.userNameTextField = null;
        this.companyLabel = null;
        this.companyNameTextField = null;
        this.JLabel1 = null;
        this.keyTextField = null;
        this.JLabel11 = null;
        this.JLabel3 = null;
        this.cons = new GridBagConstraints();
        this.init();
    }
    
    public LUIRegisterDetails(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.userLabel = null;
        this.userNameTextField = null;
        this.companyLabel = null;
        this.companyNameTextField = null;
        this.JLabel1 = null;
        this.keyTextField = null;
        this.JLabel11 = null;
        this.JLabel3 = null;
        this.cons = new GridBagConstraints();
        this.applet = applet;
        this.init();
    }
}
