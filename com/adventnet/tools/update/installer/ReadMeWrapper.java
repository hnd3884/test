package com.adventnet.tools.update.installer;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import com.adventnet.tools.update.CommonUtil;
import java.awt.Cursor;
import javax.swing.event.HyperlinkEvent;
import java.awt.Toolkit;
import java.awt.Point;
import javax.swing.SwingUtilities;
import java.net.URL;
import java.awt.Frame;
import javax.swing.JFrame;
import java.awt.Dialog;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Component;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.Dimension;
import java.util.Properties;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.applet.Applet;
import javax.swing.event.HyperlinkListener;
import javax.swing.JDialog;

public class ReadMeWrapper extends JDialog implements HyperlinkListener, Runnable, ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel JPanel2;
    JButton JButton1;
    JPanel JPanel1;
    private ParameterObject po;
    private String current_urlName;
    private String title;
    private ReadmeUI read;
    
    public ReadMeWrapper(final String urlName) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel2 = null;
        this.JButton1 = null;
        this.JPanel1 = null;
        this.po = null;
        this.current_urlName = "";
        this.title = null;
        this.read = null;
        this.init();
        this.setPage(urlName);
    }
    
    public ReadMeWrapper() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel2 = null;
        this.JButton1 = null;
        this.JPanel1 = null;
        this.po = null;
        this.current_urlName = "";
        this.title = null;
        this.read = null;
        this.pack();
    }
    
    public ReadMeWrapper(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel2 = null;
        this.JButton1 = null;
        this.JPanel1 = null;
        this.po = null;
        this.current_urlName = "";
        this.title = null;
        this.read = null;
        this.applet = applet;
        this.pack();
        this.setDefaultCloseOperation(2);
    }
    
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
    
    public void init() {
        if (this.getParameter("RESOURCE_PROPERTIES") != null) {
            this.localePropertiesFileName = this.getParameter("RESOURCE_PROPERTIES");
        }
        ReadMeWrapper.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 491, this.getPreferredSize().height + 384);
        this.setTitle(ReadMeWrapper.resourceBundle.getString("ReadMe"));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus(ReadMeWrapper.resourceBundle.getString("Error in init method"), ex);
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
        this.setSize(new Dimension(500, 500));
        try {
            this.JButton1.setHorizontalTextPosition(4);
            this.JButton1.setText(ReadMeWrapper.resourceBundle.getString("Close"));
            this.JButton1.setMnemonic('C');
        }
        catch (final Exception ex) {
            this.showStatus(ReadMeWrapper.resourceBundle.getString("Exception while setting properties for bean ") + this.JButton1, ex);
        }
        this.JButton1.setFont(UpdateManagerUtil.getFont());
        this.JPanel1.setPreferredSize(new Dimension(this.JPanel1.getPreferredSize().width + 453, this.JPanel1.getPreferredSize().height + 287));
        this.JButton1.setPreferredSize(new Dimension(this.JButton1.getPreferredSize().width + 12, this.JButton1.getPreferredSize().height + 0));
        this.JPanel2.setPreferredSize(new Dimension(this.JPanel2.getPreferredSize().width + 368, this.JPanel2.getPreferredSize().height + 0));
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel2 = new JPanel();
        this.JButton1 = new JButton();
        this.JPanel1 = new JPanel();
        this.initializeParameters();
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
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new BorderLayout(5, 5));
        this.Top.add(this.JPanel2, "South");
        this.JPanel2.setLayout(new FlowLayout(1, 5, 5));
        this.JPanel2.add(this.JButton1);
        this.Top.add(this.JPanel1, "Center");
        this.JPanel1.setLayout(new BorderLayout(5, 5));
    }
    
    public void setUpConnections() {
        final JButton1_JPanel2_conn JButton1_JPanel2_conn1 = new JButton1_JPanel2_conn();
        this.JButton1.addActionListener(JButton1_JPanel2_conn1);
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    public void addReadMePanel(final JPanel panel) {
        if (panel != null) {
            this.JPanel1.add(panel);
            this.read = (ReadmeUI)panel;
            this.read.editor.addHyperlinkListener(this);
        }
    }
    
    public void addTabbedPane(final JTabbedPane pane) {
        if (pane != null) {
            this.JPanel1.add(pane);
            this.read = (ReadmeUI)pane.getComponentAt(0);
            this.read.editor.addHyperlinkListener(this);
        }
    }
    
    public void addLogsPanel(final JPanel panel) {
        if (panel != null) {
            this.JPanel1.add(panel);
        }
    }
    
    public ReadMeWrapper(final JDialog owner) {
        super(owner);
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel2 = null;
        this.JButton1 = null;
        this.JPanel1 = null;
        this.po = null;
        this.current_urlName = "";
        this.title = null;
        this.read = null;
        this.pack();
    }
    
    public ReadMeWrapper(final JFrame owner) {
        super(owner);
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel2 = null;
        this.JButton1 = null;
        this.JPanel1 = null;
        this.po = null;
        this.current_urlName = "";
        this.title = null;
        this.read = null;
        this.pack();
    }
    
    public void setDialogTitle(final String s) {
        this.title = s;
    }
    
    @Override
    public void run() {
        try {
            final URL url = new URL("file", "localhost", this.current_urlName);
            this.read.editor.setPage(url);
            this.setVisible(true);
        }
        catch (final Exception e) {
            System.err.println("File not found");
        }
    }
    
    public void setPage(final String urlName) {
        if (urlName.startsWith("patchtemp")) {
            if (urlName.startsWith("/")) {
                this.current_urlName = urlName.substring(1);
            }
            else {
                this.current_urlName = urlName;
            }
        }
        else {
            this.current_urlName = urlName;
        }
        SwingUtilities.invokeLater(this);
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
    
    @Override
    public void hyperlinkUpdate(final HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            this.read.editor.setCursor(Cursor.getPredefinedCursor(12));
        }
        else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            this.read.editor.setCursor(Cursor.getPredefinedCursor(0));
        }
        else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            CommonUtil.displayURL(e.getURL().toExternalForm());
        }
    }
    
    static {
        ReadMeWrapper.resourceBundle = null;
    }
    
    class JButton1_JPanel2_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            ReadMeWrapper.this.dispose();
        }
    }
}
