package com.adventnet.tools.prevalent;

import java.awt.event.ItemEvent;
import java.io.Serializable;
import java.awt.event.ItemListener;
import java.io.File;
import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.AbstractButton;
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
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import java.applet.Applet;
import javax.swing.JPanel;

public class LUIOptionScreen extends JPanel
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JRadioButton freeRadioButton;
    JRadioButton evalRadioButton;
    JRadioButton registerRadioButton;
    ButtonGroup buttonGroup;
    GridBagConstraints cons;
    Insets inset;
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 622, this.getPreferredSize().height + 325));
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
            this.JPanel1.setBorder(new TitledBorder(new EtchedBorder(0), "User Type", 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JPanel1, ex);
        }
        try {
            this.JPanel1.setBorder(new TitledBorder(new EtchedBorder(0), ToolsUtils.getString("User Type"), 0, 0, new Font("Dialog", 0, 12), new Color(-16777216)));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JPanel1, ex);
        }
        try {
            this.freeRadioButton.setText("Evaluation User");
            this.freeRadioButton.setFont(new Font("sansserif", 0, 12));
            this.freeRadioButton.setMnemonic('F');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.freeRadioButton, ex);
        }
        this.freeRadioButton.setText(ToolsUtils.getString("Free User"));
        try {
            this.evalRadioButton.setText("Evaluation User");
            this.evalRadioButton.setFont(new Font("sansserif", 0, 12));
            this.evalRadioButton.setMnemonic('E');
            this.evalRadioButton.setSelected(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.evalRadioButton, ex);
        }
        this.evalRadioButton.setText(ToolsUtils.getString("Evaluation User"));
        try {
            this.registerRadioButton.setText("Registered User");
            this.registerRadioButton.setFont(new Font("sansserif", 0, 12));
            this.registerRadioButton.setMnemonic('R');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.registerRadioButton, ex);
        }
        this.registerRadioButton.setText(ToolsUtils.getString("Registered User"));
        this.buttonGroup.add(this.freeRadioButton);
        this.buttonGroup.add(this.evalRadioButton);
        this.buttonGroup.add(this.registerRadioButton);
    }
    
    public void initVariables() {
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.freeRadioButton = new JRadioButton();
        this.evalRadioButton = new JRadioButton();
        this.registerRadioButton = new JRadioButton();
        this.buttonGroup = new ButtonGroup();
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.JPanel1, "Center");
        this.JPanel1.setLayout(new GridBagLayout());
        final String f = LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "Free.xml";
        final File file = new File(f);
        if (file.exists()) {
            this.inset = new Insets(5, 5, 5, 5);
            final int x = 0;
            final int y = 0;
            final int width = 1;
            final int height = 1;
            final double wtX = 0.0;
            final double wtY = 0.0;
            final GridBagConstraints cons = this.cons;
            final int anchor = 10;
            final GridBagConstraints cons2 = this.cons;
            this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 0);
            this.JPanel1.add(this.freeRadioButton, this.cons);
            this.inset = new Insets(5, 5, 5, 5);
            final int x2 = 0;
            final int y2 = 1;
            final int width2 = 1;
            final int height2 = 1;
            final double wtX2 = 0.0;
            final double wtY2 = 0.0;
            final GridBagConstraints cons3 = this.cons;
            final int anchor2 = 10;
            final GridBagConstraints cons4 = this.cons;
            this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 2, this.inset, 0, 0);
            this.JPanel1.add(this.evalRadioButton, this.cons);
            this.inset = new Insets(5, 5, 5, 5);
            final int x3 = 0;
            final int y3 = 2;
            final int width3 = 1;
            final int height3 = 1;
            final double wtX3 = 0.0;
            final double wtY3 = 0.0;
            final GridBagConstraints cons5 = this.cons;
            final int anchor3 = 10;
            final GridBagConstraints cons6 = this.cons;
            this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
            this.JPanel1.add(this.registerRadioButton, this.cons);
        }
        else {
            this.inset = new Insets(5, 5, 5, 5);
            final int x4 = 0;
            final int y4 = 0;
            final int width4 = 1;
            final int height4 = 1;
            final double wtX4 = 0.0;
            final double wtY4 = 0.0;
            final GridBagConstraints cons7 = this.cons;
            final int anchor4 = 10;
            final GridBagConstraints cons8 = this.cons;
            this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 2, this.inset, 0, 0);
            this.JPanel1.add(this.evalRadioButton, this.cons);
            this.inset = new Insets(5, 5, 5, 5);
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
            this.JPanel1.add(this.registerRadioButton, this.cons);
        }
    }
    
    public void setUpConnections() {
        final evalRadioButton_registerRadioButton_conn evalRadioButton_registerRadioButton_conn1 = new evalRadioButton_registerRadioButton_conn();
        this.freeRadioButton.addItemListener(evalRadioButton_registerRadioButton_conn1);
        this.evalRadioButton.addItemListener(evalRadioButton_registerRadioButton_conn1);
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    public LUIOptionScreen() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.freeRadioButton = null;
        this.evalRadioButton = null;
        this.registerRadioButton = null;
        this.buttonGroup = null;
        this.cons = new GridBagConstraints();
        this.init();
    }
    
    public LUIOptionScreen(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.freeRadioButton = null;
        this.evalRadioButton = null;
        this.registerRadioButton = null;
        this.buttonGroup = null;
        this.cons = new GridBagConstraints();
        this.applet = applet;
        this.init();
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
    
    class evalRadioButton_registerRadioButton_conn implements ItemListener, Serializable
    {
        @Override
        public void itemStateChanged(final ItemEvent arg0) {
            if (LUIOptionScreen.this.evalRadioButton.isSelected() || LUIOptionScreen.this.freeRadioButton.isSelected()) {
                LUIGeneral.setNextButtonText("Finish");
            }
            else {
                LUIGeneral.setNextButtonText("Next");
            }
        }
    }
}
