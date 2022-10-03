package com.adventnet.tools.update.installer;

import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.Icon;
import java.awt.Color;
import java.util.Properties;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.JPanel;

public class ReadmeUI extends JPanel implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel Readme;
    JLabel JLabel9;
    JLabel readmeLabel;
    JSeparator JSeparator3;
    JPanel JPanel3;
    JScrollPane JScrollPane2;
    JEditorPane editor;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    
    public ReadmeUI() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.Readme = null;
        this.JLabel9 = null;
        this.readmeLabel = null;
        this.JSeparator3 = null;
        this.JPanel3 = null;
        this.JScrollPane2 = null;
        this.editor = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.init();
    }
    
    public ReadmeUI(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.Readme = null;
        this.JLabel9 = null;
        this.readmeLabel = null;
        this.JSeparator3 = null;
        this.JPanel3 = null;
        this.JScrollPane2 = null;
        this.editor = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.applet = applet;
        this.init();
    }
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 500, this.getPreferredSize().height + 375));
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
            this.Readme.setEnabled(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.Readme, ex);
        }
        try {
            this.JLabel9.setHorizontalAlignment(2);
            this.JLabel9.setForeground(new Color(-16777216));
            this.JLabel9.setHorizontalTextPosition(4);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.JLabel9, ex);
        }
        this.JLabel9.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/readme.png", this.applet, true));
        this.JLabel9.setFont(UpdateManagerUtil.getFont());
        try {
            this.readmeLabel.setHorizontalAlignment(2);
            this.readmeLabel.setForeground(new Color(-16777216));
            this.readmeLabel.setHorizontalTextPosition(4);
            this.readmeLabel.setText("The Readme for Service Pack");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.readmeLabel, ex);
        }
        this.readmeLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.editor.setEditable(false);
            this.editor.setBorder(new BevelBorder(1));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.editor, ex);
        }
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.Readme = new JPanel();
        this.JLabel9 = new JLabel();
        this.readmeLabel = new JLabel();
        this.JSeparator3 = new JSeparator();
        this.JPanel3 = new JPanel();
        this.JScrollPane2 = new JScrollPane();
        this.editor = new JEditorPane();
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
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.Readme, "Center");
        this.Readme.setLayout(new GridBagLayout());
        this.inset = new Insets(20, 10, 10, 10);
        final int x = 0;
        final int y = 0;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.0;
        final double wtY = 0.0;
        final GridBagConstraints cons = this.cons;
        final int anchor = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 3, this.inset, 0, 0);
        this.Readme.add(this.JLabel9, this.cons);
        this.inset = new Insets(20, 5, 10, 10);
        final int x2 = 1;
        final int y2 = 0;
        final int width2 = 1;
        final int height2 = 1;
        final double wtX2 = 0.1;
        final double wtY2 = 0.0;
        final GridBagConstraints cons3 = this.cons;
        final int anchor2 = 10;
        final GridBagConstraints cons4 = this.cons;
        this.setConstraints(x2, y2, width2, height2, wtX2, wtY2, anchor2, 2, this.inset, 0, 0);
        this.Readme.add(this.readmeLabel, this.cons);
        this.inset = new Insets(0, 10, 10, 10);
        final int x3 = 0;
        final int y3 = 1;
        final int width3 = 2;
        final int height3 = 1;
        final double wtX3 = 0.0;
        final double wtY3 = 0.0;
        final GridBagConstraints cons5 = this.cons;
        final int anchor3 = 10;
        final GridBagConstraints cons6 = this.cons;
        this.setConstraints(x3, y3, width3, height3, wtX3, wtY3, anchor3, 2, this.inset, 0, 0);
        this.Readme.add(this.JSeparator3, this.cons);
        this.inset = new Insets(0, 10, 10, 10);
        final int x4 = 0;
        final int y4 = 2;
        final int width4 = 2;
        final int height4 = 1;
        final double wtX4 = 0.2;
        final double wtY4 = 0.1;
        final GridBagConstraints cons7 = this.cons;
        final int anchor4 = 10;
        final GridBagConstraints cons8 = this.cons;
        this.setConstraints(x4, y4, width4, height4, wtX4, wtY4, anchor4, 1, this.inset, 0, 0);
        this.Readme.add(this.JPanel3, this.cons);
        this.JPanel3.setLayout(new BorderLayout(5, 5));
        this.JPanel3.add(this.JScrollPane2, "Center");
        this.JScrollPane2.getViewport().add(this.editor);
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
    
    public void setReadmeTitle(final String title) {
        this.readmeLabel.setText(title);
    }
}
