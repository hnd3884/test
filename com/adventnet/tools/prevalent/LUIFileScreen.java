package com.adventnet.tools.prevalent;

import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.EtchedBorder;
import java.util.Properties;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.JPanel;

public class LUIFileScreen extends JPanel
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JLabel userLabel;
    JLabel companyLabel;
    JTextField companyNameTextField;
    JComboBox userNameComboBox;
    JLabel JLabel2;
    JTextArea JLabel1;
    GridBagConstraints cons;
    Insets inset;
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 557, this.getPreferredSize().height + 357));
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
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.userLabel, ex);
        }
        this.userLabel.setText(ToolsUtils.getString("User Name"));
        try {
            this.companyLabel.setHorizontalAlignment(2);
            this.companyLabel.setFont(new Font("SansSerif", 0, 12));
            this.companyLabel.setForeground(new Color(-16777216));
            this.companyLabel.setHorizontalTextPosition(4);
            this.companyLabel.setText("Company Name");
            this.companyLabel.setDisplayedMnemonic('C');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.companyLabel, ex);
        }
        this.companyLabel.setText(ToolsUtils.getString("Company Name"));
        try {
            this.companyNameTextField.setHorizontalAlignment(2);
            this.companyNameTextField.setFont(new Font("SansSerif", 0, 12));
            this.companyNameTextField.setBorder(new BevelBorder(1));
            this.companyNameTextField.setEnabled(false);
            this.companyNameTextField.setEditable(false);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.companyNameTextField, ex);
        }
        try {
            this.userNameComboBox.setFont(new Font("sansserif", 0, 12));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.userNameComboBox, ex);
        }
        try {
            this.JLabel1.setFont(new Font("SansSerif", 0, 12));
            this.JLabel1.setForeground(new Color(-16777216));
            this.JLabel1.setBackground(new Color(-3355444));
            this.JLabel1.setOpaque(false);
            this.JLabel1.setLineWrap(true);
            this.JLabel1.setWrapStyleWord(true);
            this.JLabel1.setDoubleBuffered(true);
            this.JLabel1.setText("The list of User Names for whom the license file is authorized is listed here. Select the User Name with which you want to register this installation copy.");
            this.JLabel1.setEditable(false);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel1, ex);
        }
        this.JLabel1.setText(ToolsUtils.getString("The list of User Names for whom the license file is authorized is listed here. Select the User Name with which you want to register this installation copy."));
    }
    
    public void initVariables() {
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.userLabel = new JLabel();
        this.companyLabel = new JLabel();
        this.companyNameTextField = new JTextField();
        this.userNameComboBox = new JComboBox();
        this.JLabel2 = new JLabel();
        this.JLabel1 = new JTextArea();
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
        final double wtX = 0.1;
        final double wtY = 0.0;
        final GridBagConstraints cons = this.cons;
        final int anchor = 13;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 0);
        this.JPanel1.add(this.userLabel, this.cons);
        this.inset = new Insets(5, 10, 5, 5);
        final int x2 = 0;
        final int y2 = 3;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.0;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 2, this.inset, 0, 0);
        this.JPanel1.add(this.companyLabel, this.cons);
        this.inset = new Insets(5, 10, 0, 5);
        final int x3 = 0;
        final int y3 = 4;
        final int width3 = 1;
        final int height3 = 1;
        final double wtX3 = 0.0;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
        this.JPanel1.add(this.companyNameTextField, this.cons);
        this.inset = new Insets(20, 10, 5, 5);
        final int x4 = 0;
        final int y4 = 2;
        final int width4 = 1;
        final int height4 = 1;
        final double wtX4 = 0.0;
        final double wtY4 = 0.0;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 2, this.inset, 0, 0);
        this.JPanel1.add(this.userNameComboBox, this.cons);
        this.inset = new Insets(0, 0, 0, 0);
        final int x5 = 1;
        final int y5 = 3;
        final int width5 = 1;
        final int height5 = 1;
        final double wtX5 = 0.0;
        final double wtY5 = 0.0;
        final GridBagConstraints cons9 = this.cons;
        final int anchor5 = 10;
        final GridBagConstraints cons10 = this.cons;
        this.setConstraints(x5, y5, width5, height5, wtX5, wtY5, anchor5, 2, this.inset, 80, 0);
        this.JPanel1.add(this.JLabel2, this.cons);
        this.inset = new Insets(15, 10, 5, 5);
        final int x6 = 0;
        final int y6 = 0;
        final int width6 = 2;
        final int height6 = 1;
        final double wtX6 = 0.0;
        final double wtY6 = 0.0;
        final GridBagConstraints cons11 = this.cons;
        final int anchor6 = 10;
        final GridBagConstraints cons12 = this.cons;
        this.setConstraints(x6, y6, width6, height6, wtX6, wtY6, anchor6, 2, this.inset, 0, 0);
        this.JPanel1.add(this.JLabel1, this.cons);
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
    
    public LUIFileScreen() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.userLabel = null;
        this.companyLabel = null;
        this.companyNameTextField = null;
        this.userNameComboBox = null;
        this.JLabel2 = null;
        this.JLabel1 = null;
        this.cons = new GridBagConstraints();
        this.init();
    }
    
    public LUIFileScreen(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.userLabel = null;
        this.companyLabel = null;
        this.companyNameTextField = null;
        this.userNameComboBox = null;
        this.JLabel2 = null;
        this.JLabel1 = null;
        this.cons = new GridBagConstraints();
        this.applet = applet;
        this.init();
    }
}
