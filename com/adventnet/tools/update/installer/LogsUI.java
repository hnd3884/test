package com.adventnet.tools.update.installer;

import java.awt.GridBagLayout;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import com.adventnet.tools.update.CommonUtil;
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
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import java.applet.Applet;
import javax.swing.JPanel;

public class LogsUI extends JPanel implements ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel logsPanel;
    JLabel logsImageLabel;
    JLabel logsLabel;
    JSeparator JSeparator3;
    JPanel JPanel3;
    JScrollPane logsScrollPane;
    JTextArea logsTextArea;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    
    public void init() {
        if (this.initialized) {
            return;
        }
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 549, this.getPreferredSize().height + 396));
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
            this.logsPanel.setEnabled(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.logsPanel, ex);
        }
        try {
            this.logsImageLabel.setHorizontalAlignment(2);
            this.logsImageLabel.setForeground(new Color(-16777216));
            this.logsImageLabel.setHorizontalTextPosition(4);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.logsImageLabel, ex);
        }
        this.logsImageLabel.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/readme.png", this.applet, true));
        this.logsImageLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.logsLabel.setHorizontalAlignment(2);
            this.logsLabel.setForeground(new Color(-16777216));
            this.logsLabel.setHorizontalTextPosition(4);
            this.logsLabel.setText(CommonUtil.getString("Installed files"));
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.logsLabel, ex);
        }
        this.logsLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.logsTextArea.setEditable(false);
            this.logsTextArea.setBorder(new BevelBorder(1));
            this.logsTextArea.setText("");
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.logsTextArea, ex);
        }
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.logsPanel = new JPanel();
        this.logsImageLabel = new JLabel();
        this.logsLabel = new JLabel();
        this.JSeparator3 = new JSeparator();
        this.JPanel3 = new JPanel();
        this.logsScrollPane = new JScrollPane();
        this.logsTextArea = new JTextArea();
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
    public void setParameterObject(final ParameterObject paramObj) {
        this.po = paramObj;
        this.initializeParameters();
    }
    
    @Override
    public void parameterChanged(final ParameterObject paramObj) {
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.logsPanel, "Center");
        this.logsPanel.setLayout(new GridBagLayout());
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
        this.logsPanel.add(this.logsImageLabel, this.cons);
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
        this.logsPanel.add(this.logsLabel, this.cons);
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
        this.logsPanel.add(this.JSeparator3, this.cons);
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
        this.logsPanel.add(this.JPanel3, this.cons);
        this.JPanel3.setLayout(new BorderLayout(5, 5));
        this.JPanel3.add(this.logsScrollPane, "Center");
        this.logsScrollPane.getViewport().add(this.logsTextArea);
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
    
    public LogsUI() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.logsPanel = null;
        this.logsImageLabel = null;
        this.logsLabel = null;
        this.JSeparator3 = null;
        this.JPanel3 = null;
        this.logsScrollPane = null;
        this.logsTextArea = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.init();
    }
    
    public LogsUI(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.logsPanel = null;
        this.logsImageLabel = null;
        this.logsLabel = null;
        this.JSeparator3 = null;
        this.JPanel3 = null;
        this.logsScrollPane = null;
        this.logsTextArea = null;
        this.po = null;
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
}
