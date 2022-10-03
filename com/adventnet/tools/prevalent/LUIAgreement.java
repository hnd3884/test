package com.adventnet.tools.prevalent;

import java.awt.event.ItemEvent;
import java.io.Serializable;
import java.util.Properties;
import java.awt.event.ItemListener;
import java.awt.Component;
import java.awt.Font;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import java.applet.Applet;
import javax.swing.JPanel;

public class LUIAgreement extends JPanel
{
    private boolean initialized;
    private Applet applet;
    private boolean running;
    JPanel Top;
    JPanel firstPanel;
    JCheckBox acceptCheckBox;
    JScrollPane agreementScrollPane;
    JTextArea agreementTextArea;
    
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
        this.setPreferredSize(new Dimension(this.getPreferredSize().width + 410, this.getPreferredSize().height + 333));
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
            this.acceptCheckBox.setFont(new Font("SansSerif", 0, 12));
            this.acceptCheckBox.setHorizontalTextPosition(4);
            this.acceptCheckBox.setText("I accept the License Agreement");
            this.acceptCheckBox.setMnemonic('I');
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.acceptCheckBox, ex);
        }
        this.acceptCheckBox.setText(ToolsUtils.getString("I accept the License Agreement"));
        try {
            this.agreementTextArea.setWrapStyleWord(true);
            this.agreementTextArea.setEditable(false);
            this.agreementTextArea.setLineWrap(true);
        }
        catch (final Exception ex) {
            this.showStatus("Exception while setting properties for bean " + this.agreementTextArea, ex);
        }
        this.agreementScrollPane.setPreferredSize(new Dimension(this.agreementScrollPane.getPreferredSize().width + 15, this.agreementScrollPane.getPreferredSize().height + 144));
        this.acceptCheckBox.setPreferredSize(new Dimension(this.acceptCheckBox.getPreferredSize().width + 28, this.acceptCheckBox.getPreferredSize().height + 0));
        this.firstPanel.setPreferredSize(new Dimension(this.firstPanel.getPreferredSize().width + 10, this.firstPanel.getPreferredSize().height + 10));
    }
    
    public void initVariables() {
        this.Top = new JPanel();
        this.firstPanel = new JPanel();
        this.acceptCheckBox = new JCheckBox();
        this.agreementScrollPane = new JScrollPane();
        this.agreementTextArea = new JTextArea();
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.firstPanel, "Center");
        this.firstPanel.setLayout(new BorderLayout(5, 5));
        this.firstPanel.add(this.acceptCheckBox, "South");
        this.firstPanel.add(this.agreementScrollPane, "Center");
        this.agreementScrollPane.getViewport().add(this.agreementTextArea);
    }
    
    public void setUpConnections() {
        final acceptCheckBox_agreementTextArea_conn acceptCheckBox_agreementTextArea_conn1 = new acceptCheckBox_agreementTextArea_conn();
        this.acceptCheckBox.addItemListener(acceptCheckBox_agreementTextArea_conn1);
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
    
    public LUIAgreement() {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.firstPanel = null;
        this.acceptCheckBox = null;
        this.agreementScrollPane = null;
        this.agreementTextArea = null;
        this.init();
    }
    
    public LUIAgreement(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.running = false;
        this.Top = null;
        this.firstPanel = null;
        this.acceptCheckBox = null;
        this.agreementScrollPane = null;
        this.agreementTextArea = null;
        this.applet = applet;
        this.init();
    }
    
    class acceptCheckBox_agreementTextArea_conn implements ItemListener, Serializable
    {
        @Override
        public void itemStateChanged(final ItemEvent arg0) {
            final int i = arg0.getStateChange();
            if (i == 1) {
                LUIGeneral.acceptAgreement(true);
            }
            else if (i == 2) {
                LUIGeneral.acceptAgreement(false);
            }
        }
    }
}
