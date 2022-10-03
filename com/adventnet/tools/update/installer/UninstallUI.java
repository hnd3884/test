package com.adventnet.tools.update.installer;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.KeyStroke;
import javax.swing.FocusManager;
import javax.swing.DefaultFocusManager;
import java.awt.Cursor;
import javax.swing.tree.MutableTreeNode;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Frame;
import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.adventnet.tools.update.UpdateManagerUtil;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.applet.Applet;
import java.awt.event.WindowListener;
import javax.swing.JDialog;

public class UninstallUI extends JDialog implements WindowListener, ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel JPanel1;
    JButton nextButton;
    JButton cancelButton;
    CardPanel CardPanel1;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    private ArrayList list;
    private String confProductName;
    private String confProductVersion;
    private String confSubProductName;
    private JFrame frame;
    
    @Override
    public void setVisible(final boolean bl) {
        if (bl) {
            this.init();
            this.start();
        }
        else {
            final int state = UpdateManagerUtil.getRevertState();
            if (state == 1) {
                return;
            }
        }
        UpdateManagerUtil.setParent(null);
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
        UninstallUI.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 455, this.getPreferredSize().height + 364);
        this.setTitle(UninstallUI.resourceBundle.getString("Uninstall"));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus(UninstallUI.resourceBundle.getString("Error in init method"), ex);
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
        this.setSize(new Dimension(425, 425));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                final int state = UpdateManagerUtil.getRevertState();
                if (state == 1) {
                    return;
                }
                UninstallUI.this.setVisible(false);
                UninstallUI.this.dispose();
            }
        });
        Assorted.positionTheWindow(this, "Center");
        try {
            this.nextButton.setHorizontalTextPosition(4);
            this.nextButton.setMnemonic(78);
            this.nextButton.setText(UninstallUI.resourceBundle.getString("Finish"));
            this.nextButton.setPreferredSize(new Dimension(73, 27));
            this.nextButton.setMaximumSize(new Dimension(73, 27));
            this.nextButton.setMinimumSize(new Dimension(73, 27));
        }
        catch (final Exception ex) {
            this.showStatus(UninstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.nextButton, ex);
        }
        this.nextButton.setFont(UpdateManagerUtil.getFont());
        try {
            this.cancelButton.setHorizontalTextPosition(4);
            this.cancelButton.setText(UninstallUI.resourceBundle.getString("Cancel"));
            this.cancelButton.setPreferredSize(new Dimension(73, 27));
            this.cancelButton.setMinimumSize(new Dimension(73, 27));
            this.cancelButton.setMaximumSize(new Dimension(73, 27));
        }
        catch (final Exception ex) {
            this.showStatus(UninstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.cancelButton, ex);
        }
        this.cancelButton.setFont(UpdateManagerUtil.getFont());
        try {
            final String[] CardPanel1cardAndClassNames_array = { UninstallUI.resourceBundle.getString("first=com.adventnet.tools.update.installer.RevertScreen"), UninstallUI.resourceBundle.getString("second=com.adventnet.tools.update.installer.RevertProgress") };
            this.CardPanel1.setCardAndClassNames(CardPanel1cardAndClassNames_array);
        }
        catch (final Exception ex) {
            this.showStatus(UninstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.CardPanel1, ex);
        }
        this.cancelButton.setPreferredSize(new Dimension(this.cancelButton.getPreferredSize().width + 20, this.cancelButton.getPreferredSize().height + 0));
        this.nextButton.setPreferredSize(new Dimension(this.nextButton.getPreferredSize().width + 20, this.nextButton.getPreferredSize().height + 0));
        this.initProperty();
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.JPanel1 = new JPanel();
        this.nextButton = new JButton();
        this.cancelButton = new JButton();
        this.CardPanel1 = new CardPanel(this.applet);
        this.initializeParameters();
    }
    
    public void setUpGUI(final Container container) {
        container.add(this.Top, "Center");
        this.Top.setLayout(new GridBagLayout());
        this.inset = new Insets(5, 5, 5, 5);
        final int x = 0;
        final int y = 1;
        final int width = 1;
        final int height = 1;
        final double wtX = 0.0;
        final double wtY = 0.0;
        final GridBagConstraints cons = this.cons;
        final int anchor = 10;
        final GridBagConstraints cons2 = this.cons;
        this.setConstraints(x, y, width, height, wtX, wtY, anchor, 2, this.inset, 0, 0);
        this.Top.add(this.JPanel1, this.cons);
        this.JPanel1.setLayout(new FlowLayout(1, 5, 5));
        this.JPanel1.add(this.nextButton);
        this.JPanel1.add(this.cancelButton);
        this.inset = new Insets(0, 0, 0, 0);
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
        this.Top.add(this.CardPanel1, this.cons);
    }
    
    public void setUpConnections() {
        final nextButton_CardPanel1_conn nextButton_CardPanel1_conn1 = new nextButton_CardPanel1_conn();
        this.nextButton.addActionListener(nextButton_CardPanel1_conn1);
        final cancelButton_cancelButton_conn cancelButton_cancelButton_conn1 = new cancelButton_cancelButton_conn();
        this.cancelButton.addActionListener(cancelButton_cancelButton_conn1);
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
    
    public UninstallUI() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.nextButton = null;
        this.cancelButton = null;
        this.CardPanel1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.list = null;
        this.confProductName = null;
        this.confProductVersion = null;
        this.confSubProductName = null;
        this.frame = null;
        this.pack();
        this.addWinListenerWithEscSupport();
    }
    
    public UninstallUI(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.nextButton = null;
        this.cancelButton = null;
        this.CardPanel1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.list = null;
        this.confProductName = null;
        this.confProductVersion = null;
        this.confSubProductName = null;
        this.frame = null;
        this.applet = applet;
        this.pack();
        this.setDefaultCloseOperation(2);
        this.addWinListenerWithEscSupport();
    }
    
    public UninstallUI(final JFrame owner, final ArrayList list, final String confProductName, final String confProductVersion, final String confSubProductName) {
        super(owner);
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.JPanel1 = null;
        this.nextButton = null;
        this.cancelButton = null;
        this.CardPanel1 = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.list = null;
        this.confProductName = null;
        this.confProductVersion = null;
        this.confSubProductName = null;
        this.frame = null;
        this.frame = owner;
        this.confProductName = confProductName;
        this.confProductVersion = confProductVersion;
        this.confSubProductName = confSubProductName;
        this.list = list;
        this.pack();
        this.addWinListenerWithEscSupport();
    }
    
    private void initProperty() {
        this.nextButton.setText(UninstallUI.resourceBundle.getString("Finish"));
        this.nextButton.setMnemonic('F');
        final RevertScreen rvScreen = (RevertScreen)this.CardPanel1.getCard("first");
        if (!this.list.isEmpty()) {
            final int size = this.list.size();
            final String versionName = this.list.get(size - 1);
            final String displayName = UpdateManagerUtil.getDisplayVersionName(versionName);
            rvScreen.uninstallLabel.setText(UninstallUI.resourceBundle.getString("Note that Service Pack ") + displayName + " " + UninstallUI.resourceBundle.getString("and its dependent (as shown below) will be automatically removed after uninstallation.If you donot want to proceed click 'Cancel' button."));
            final DefaultMutableTreeNode dmt = this.getTreeNodes(this.list);
            rvScreen.versionTree.setModel(new DefaultTreeModel(dmt));
            rvScreen.expandTree();
            this.CardPanel1.showCard("first");
        }
    }
    
    private DefaultMutableTreeNode getTreeNodes(final ArrayList list) {
        DefaultMutableTreeNode dmt = null;
        try {
            final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
            final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
            final File specFile = new File(specsPath);
            if (!specFile.exists()) {
                return null;
            }
            final VersionProfile vProfile = VersionProfile.getInstance();
            vProfile.readDocument(specsPath, false, false);
            final int size = list.size();
            dmt = new DefaultMutableTreeNode("Service Pack");
            for (int i = size; i > 0; --i) {
                final String versionName = list.get(i - 1);
                String displayName = vProfile.getTheAdditionalDetail(versionName, "DisplayName");
                if (displayName == null || displayName.trim().equals("")) {
                    displayName = versionName;
                }
                final DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode(displayName);
                final String[] contextArray = vProfile.getTheContext(versionName);
                if (contextArray != null) {
                    for (final String contextName : contextArray) {
                        if (!contextName.equals(this.confSubProductName)) {
                            final DefaultMutableTreeNode contextNode = new DefaultMutableTreeNode(contextName);
                            versionNode.add(contextNode);
                        }
                    }
                }
                dmt.add(versionNode);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return dmt;
    }
    
    private void disableButtons(final boolean bool) {
        this.nextButton.setEnabled(false);
        this.cancelButton.setEnabled(bool);
    }
    
    public void setDefaultCursor() {
        this.disableButtons(true);
        this.setCursor(new Cursor(0));
    }
    
    public void updateTheVersion(final boolean bool) {
        ((UpdateManagerUI)this.frame).getVersionInfo();
        this.setCursor(new Cursor(0));
        this.disableButtons(true);
        final DefaultFocusManager fm = (DefaultFocusManager)FocusManager.getCurrentManager();
        fm.focusNextComponent(this.nextButton);
        if (bool) {
            this.setVisible(false);
        }
    }
    
    public void addWinListenerWithEscSupport() {
        this.setDefaultCloseOperation(0);
        this.addWindowListener(this);
        final KeyStroke escStroke = KeyStroke.getKeyStroke(27, 0);
        this.getRootPane().registerKeyboardAction(new EscListenerClass(), " ", escStroke, 2);
    }
    
    private void closingOperation() {
        final int state = UpdateManagerUtil.getRevertState();
        if (state == 1) {
            return;
        }
        this.setVisible(false);
        this.dispose();
    }
    
    @Override
    public void windowClosing(final WindowEvent e) {
        this.closingOperation();
    }
    
    @Override
    public void windowOpened(final WindowEvent e) {
    }
    
    @Override
    public void windowClosed(final WindowEvent e) {
    }
    
    @Override
    public void windowIconified(final WindowEvent e) {
    }
    
    @Override
    public void windowDeiconified(final WindowEvent e) {
    }
    
    @Override
    public void windowActivated(final WindowEvent e) {
    }
    
    @Override
    public void windowDeactivated(final WindowEvent e) {
    }
    
    public JDialog getDialog() {
        return this;
    }
    
    static {
        UninstallUI.resourceBundle = null;
    }
    
    class nextButton_CardPanel1_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final String cardName = UninstallUI.this.CardPanel1.getSelectedCardName();
            if (cardName.equals("first")) {
                UpdateManagerUtil.setParent(UninstallUI.this.getDialog());
                UninstallUI.this.setCursor(new Cursor(3));
                UninstallUI.this.cancelButton.setText(UninstallUI.resourceBundle.getString("Close"));
                UninstallUI.this.cancelButton.setMnemonic('C');
                UninstallUI.this.disableButtons(false);
                UninstallUI.this.CardPanel1.showCard("second");
                final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
                final Common common = new Common(dirToUnzip, "", true, UninstallUI.this.confProductName);
                final Revert revert = new Revert(common, UninstallUI.this.list, true, UninstallUI.this.frame);
                final Thread revertThread = new Thread(revert);
                revertThread.start();
            }
        }
    }
    
    public class EscListenerClass implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent aEvtArg) {
            UninstallUI.this.closingOperation();
            UninstallUI.this.windowClosing(new WindowEvent(UninstallUI.this, 201));
        }
    }
    
    class cancelButton_cancelButton_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            UninstallUI.this.setVisible(false);
        }
    }
}
