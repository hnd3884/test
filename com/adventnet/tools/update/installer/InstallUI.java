package com.adventnet.tools.update.installer;

import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultCellEditor;
import java.awt.Color;
import javax.swing.JTable;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.KeyStroke;
import javax.swing.FocusManager;
import javax.swing.DefaultFocusManager;
import java.util.Vector;
import javax.swing.JOptionPane;
import java.awt.Cursor;
import javax.swing.table.TableColumn;
import java.io.File;
import javax.swing.table.TableCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.util.Hashtable;
import java.util.HashMap;
import java.awt.Frame;
import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.ArrayList;
import javax.swing.JFrame;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.applet.Applet;
import java.awt.event.WindowListener;
import javax.swing.JDialog;

public class InstallUI extends JDialog implements WindowListener, ParameterChangeListener
{
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private boolean running;
    JPanel Top;
    JPanel mainPanel;
    JButton backButton;
    JButton nextButton;
    JButton logButton;
    JButton cancelButton;
    CardPanel cardPanel;
    private ParameterObject po;
    GridBagConstraints cons;
    Insets inset;
    private String confProductName;
    private String confProductVersion;
    private String confSubProductName;
    private JFrame frame;
    public ArrayList alist;
    private Common common;
    private int checkBoxCount;
    private boolean innerCheckBoxEnabled;
    private boolean readmeInFailure;
    private ArrayList logsList;
    private LoggingUtil logg;
    
    @Override
    public void setVisible(final boolean bl) {
        if (bl) {
            this.init();
            this.start();
        }
        else {
            final int state = UpdateManagerUtil.getInstallState();
            if (state == UpdateManagerUtil.INSTALL_IN_PROGRESS) {
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
        InstallUI.resourceBundle = Utility.getBundle(this.localePropertiesFileName, this.getParameter("RESOURCE_LOCALE"), this.applet);
        if (this.initialized) {
            return;
        }
        this.setSize(this.getPreferredSize().width + 506, this.getPreferredSize().height + 411);
        this.setTitle(InstallUI.resourceBundle.getString("Installation Wizard "));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus(InstallUI.resourceBundle.getString("Error in init method"), ex);
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
                InstallUI.this.closingOperation();
            }
        });
        this.setLocationRelativeTo(this.frame);
        try {
            this.backButton.setHorizontalTextPosition(4);
            this.backButton.setText(InstallUI.resourceBundle.getString("Back"));
            this.backButton.setMnemonic(66);
        }
        catch (final Exception ex) {
            this.showStatus(InstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.backButton, ex);
        }
        this.backButton.setFont(UpdateManagerUtil.getFont());
        try {
            this.logButton.setHorizontalTextPosition(4);
            this.logButton.setText(InstallUI.resourceBundle.getString("log"));
            this.logButton.setMnemonic('L');
            this.logButton.setEnabled(false);
        }
        catch (final Exception ex) {
            this.showStatus(InstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.logButton, ex);
        }
        try {
            this.nextButton.setHorizontalTextPosition(4);
            this.nextButton.setText(InstallUI.resourceBundle.getString("Install..."));
            this.nextButton.setMnemonic('I');
            this.nextButton.setEnabled(false);
        }
        catch (final Exception ex) {
            this.showStatus(InstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.nextButton, ex);
        }
        this.nextButton.setFont(UpdateManagerUtil.getFont());
        this.logButton.setFont(UpdateManagerUtil.getFont());
        try {
            this.cancelButton.setHorizontalTextPosition(4);
            this.cancelButton.setText(InstallUI.resourceBundle.getString("Cancel"));
        }
        catch (final Exception ex) {
            this.showStatus(InstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.cancelButton, ex);
        }
        this.cancelButton.setFont(UpdateManagerUtil.getFont());
        try {
            final String[] cardPanelcardAndClassNames_array = { InstallUI.resourceBundle.getString("first=com.adventnet.tools.update.installer.PatchScreen"), InstallUI.resourceBundle.getString("second=com.adventnet.tools.update.installer.ContextScreen"), InstallUI.resourceBundle.getString("third=com.adventnet.tools.update.installer.InstallProgress") };
            this.cardPanel.setCardAndClassNames(cardPanelcardAndClassNames_array);
        }
        catch (final Exception ex) {
            this.showStatus(InstallUI.resourceBundle.getString("Exception while setting properties for bean ") + this.cardPanel, ex);
        }
        this.backButton.setPreferredSize(new Dimension(this.backButton.getPreferredSize().width + 11, this.backButton.getPreferredSize().height + 0));
        this.nextButton.setText("Next");
        this.nextButton.setMnemonic('N');
        this.logButton.setText("View Log >>");
        this.logButton.setMnemonic('L');
        this.logButton.setEnabled(false);
        this.backButton.setEnabled(false);
        this.cardPanel.showCard("first");
    }
    
    public void initVariables() {
        if (this.po == null) {
            this.po = new ParameterObject();
        }
        this.Top = new JPanel();
        this.mainPanel = new JPanel();
        this.backButton = new JButton();
        this.nextButton = new JButton();
        this.logButton = new JButton();
        this.cancelButton = new JButton();
        this.cardPanel = new CardPanel(this.applet);
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
        this.Top.add(this.mainPanel, this.cons);
        this.mainPanel.setLayout(new FlowLayout(3, 5, 5));
        this.mainPanel.add(this.nextButton);
        this.mainPanel.add(this.logButton);
        this.mainPanel.add(this.cancelButton);
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
        this.Top.add(this.cardPanel, this.cons);
    }
    
    public void setUpConnections() {
        final nextButton_cardPanel_conn nextButton_cardPanel_conn1 = new nextButton_cardPanel_conn();
        this.nextButton.addActionListener(nextButton_cardPanel_conn1);
        final logButton_cardPanel_conn logButton_cardPanel_conn1 = new logButton_cardPanel_conn();
        this.logButton.addActionListener(logButton_cardPanel_conn1);
        final cancelButton_cardPanel_conn cancelButton_cardPanel_conn1 = new cancelButton_cardPanel_conn();
        this.cancelButton.addActionListener(cancelButton_cardPanel_conn1);
        final backButton_cardPanel_conn backButton_cardPanel_conn1 = new backButton_cardPanel_conn();
        this.backButton.addActionListener(backButton_cardPanel_conn1);
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
    
    public InstallUI() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.mainPanel = null;
        this.backButton = null;
        this.nextButton = null;
        this.logButton = null;
        this.cancelButton = null;
        this.cardPanel = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.confProductName = null;
        this.confProductVersion = null;
        this.confSubProductName = null;
        this.frame = null;
        this.alist = null;
        this.common = null;
        this.checkBoxCount = 0;
        this.innerCheckBoxEnabled = false;
        this.readmeInFailure = false;
        this.logsList = null;
        this.logg = null;
        this.pack();
        this.addWinListenerWithEscSupport();
    }
    
    public InstallUI(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.mainPanel = null;
        this.backButton = null;
        this.nextButton = null;
        this.logButton = null;
        this.cancelButton = null;
        this.cardPanel = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.confProductName = null;
        this.confProductVersion = null;
        this.confSubProductName = null;
        this.frame = null;
        this.alist = null;
        this.common = null;
        this.checkBoxCount = 0;
        this.innerCheckBoxEnabled = false;
        this.readmeInFailure = false;
        this.logsList = null;
        this.logg = null;
        this.applet = applet;
        this.pack();
        this.setDefaultCloseOperation(2);
        this.addWinListenerWithEscSupport();
    }
    
    public InstallUI(final JFrame owner, final String confProductName, final String confProductVersion, final String confSubProductName) {
        super(owner);
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.Top = null;
        this.mainPanel = null;
        this.backButton = null;
        this.nextButton = null;
        this.logButton = null;
        this.cancelButton = null;
        this.cardPanel = null;
        this.po = null;
        this.cons = new GridBagConstraints();
        this.confProductName = null;
        this.confProductVersion = null;
        this.confSubProductName = null;
        this.frame = null;
        this.alist = null;
        this.common = null;
        this.checkBoxCount = 0;
        this.innerCheckBoxEnabled = false;
        this.readmeInFailure = false;
        this.logsList = null;
        this.logg = null;
        this.frame = owner;
        this.confProductName = confProductName;
        this.confProductVersion = confProductVersion;
        this.confSubProductName = confSubProductName;
        this.pack();
        this.addWinListenerWithEscSupport();
    }
    
    private boolean contextInPatch(final HashMap hash, final Hashtable table, final String patchPath, final String versionDir) {
        final Object[] hashContext = hash.keySet().toArray();
        final int size = hashContext.length;
        final NonEditableTableModel dm = new NonEditableTableModel(0, 2);
        final ContextScreen contextScreen = (ContextScreen)this.cardPanel.getCard("second");
        contextScreen.JTable1.setModel(dm);
        contextScreen.setOptionalContext(hash);
        contextScreen.setContextTable(table);
        contextScreen.setPatchFilePath(patchPath);
        contextScreen.JTable1.setShowGrid(false);
        contextScreen.JTable1.setTableHeader(null);
        final TableColumn firstColumn = contextScreen.JTable1.getColumnModel().getColumn(0);
        firstColumn.setMaxWidth(25);
        firstColumn.setMinWidth(25);
        firstColumn.setCellRenderer(new CheckBoxRenderer());
        firstColumn.setCellEditor(new CheckBoxEditor(new JCheckBox()));
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        VersionProfile verProfile = null;
        boolean filePresent = false;
        if (new File(specsFile).exists()) {
            verProfile = VersionProfile.getInstance();
            filePresent = true;
            verProfile.readDocument(specsFile, false, false);
        }
        final Object[] val = new Object[2];
        for (int i = size; i > 0; --i) {
            final String select = (String)hashContext[i - 1];
            if (filePresent) {
                final int contextInt = verProfile.isContextPresent(versionDir, select, specsFile);
                if (contextInt == 6 || contextInt == 0) {
                    val[0] = new JCheckBox();
                    val[1] = select;
                    dm.addRow(val);
                }
            }
            else {
                val[0] = new JCheckBox();
                val[1] = select;
                dm.addRow(val);
            }
        }
        final int rowCount = contextScreen.JTable1.getRowCount();
        if (rowCount == 0) {
            return false;
        }
        contextScreen.JTable1.setRowSelectionInterval(0, 0);
        return true;
    }
    
    private String[] getTableHeader(final String title, final String option) {
        final String[] s = { title, option };
        return s;
    }
    
    public void installActionPerformed() {
        UpdateManagerUtil.setParent(this.getDialog());
        final String CardName = this.cardPanel.getSelectedCardName();
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        this.nextButton.setCursor(new Cursor(3));
        if (CardName.equals("first")) {
            final PatchScreen pScreen = (PatchScreen)this.cardPanel.getSelectedCard();
            final String patchPath = pScreen.patchPath.getText();
            final File testFileName = new File(patchPath);
            if (!testFileName.exists()) {
                JOptionPane.showMessageDialog(this.getDialog(), InstallUI.resourceBundle.getString("The file that you have specified doesnot exist."), InstallUI.resourceBundle.getString("Error"), 2);
                return;
            }
            if (!patchPath.endsWith(".ppm")) {
                JOptionPane.showMessageDialog(this.getDialog(), InstallUI.resourceBundle.getString("The format of the file that you have specified is not compatible."), InstallUI.resourceBundle.getString("Error"), 2);
                return;
            }
            this.common = pScreen.getCommon();
            if (this.common != null && !patchPath.equals("")) {
                final HashMap hashList = this.common.getTheCompatibleContext(this.confSubProductName);
                final Vector conv = this.common.getTheContext();
                final String patchVersion = this.common.getPatchVersion();
                if (!conv.contains(this.confSubProductName) && !conv.contains("NoContext")) {
                    JOptionPane.showMessageDialog(this.getDialog(), InstallUI.resourceBundle.getString("The selected Service Pack does not contains Mandatory upgrade "), InstallUI.resourceBundle.getString("Information"), 1);
                    return;
                }
                if (hashList.isEmpty()) {
                    this.alist = new ArrayList();
                    if (conv.contains("NoContext")) {
                        this.alist.add("NoContext");
                    }
                    else {
                        this.alist.add(this.confSubProductName);
                    }
                    final ApplyPatch apply = new ApplyPatch(this.alist, dirToUnzip, patchVersion, this.common, true, this.frame);
                    if (apply.isPatchAlreadyInstalled()) {
                        JOptionPane.showMessageDialog(this.getDialog(), InstallUI.resourceBundle.getString("The selected Service Pack is already installed"), InstallUI.resourceBundle.getString("Information"), 1);
                        return;
                    }
                    this.logsList = apply.getSelectedContext();
                    final InstallProgress inprogress = (InstallProgress)this.cardPanel.getCard("third");
                    inprogress.setInstallDirectory(dirToUnzip);
                    inprogress.setPPMSize("0 ");
                    this.cancelButton.setText(InstallUI.resourceBundle.getString("Close"));
                    this.cancelButton.setMnemonic('C');
                    this.disableButtons(false);
                    this.setCursor(new Cursor(3));
                    this.cardPanel.showCard("third");
                    final Thread installThread = new Thread(apply);
                    installThread.start();
                }
                else {
                    final boolean isContextInstalled = this.contextInPatch(hashList, this.common.getContextTable(), patchPath, patchVersion);
                    if (!isContextInstalled) {
                        JOptionPane.showMessageDialog(this.getDialog(), InstallUI.resourceBundle.getString("The selected Service Pack is already installed"), InstallUI.resourceBundle.getString("Information"), 1);
                        return;
                    }
                    this.backButton.setEnabled(true);
                    this.cancelButton.setText(InstallUI.resourceBundle.getString("Cancel"));
                    this.nextButton.setText(InstallUI.resourceBundle.getString("Finish"));
                    this.logButton.setText(InstallUI.resourceBundle.getString("View Log >>"));
                    this.cardPanel.showCard("second");
                }
            }
        }
        else if (CardName.equals("second")) {
            final ContextScreen conScreen = (ContextScreen)this.cardPanel.getSelectedCard();
            final NonEditableTableModel dtm = (NonEditableTableModel)conScreen.JTable1.getModel();
            final int count = dtm.getRowCount();
            (this.alist = new ArrayList()).add(this.confSubProductName);
            for (int i = 0; i < count; ++i) {
                final JCheckBox jcb = (JCheckBox)dtm.getValueAt(i, 0);
                if (jcb.isSelected()) {
                    this.alist.add(dtm.getValueAt(i, 1));
                }
            }
            final String patchVersion2 = this.common.getPatchVersion();
            final ApplyPatch apply2 = new ApplyPatch(this.alist, dirToUnzip, patchVersion2, this.common, true, this.frame);
            if (apply2.isPatchAlreadyInstalled()) {
                JOptionPane.showMessageDialog(this.getDialog(), InstallUI.resourceBundle.getString("Select an optional upgrade to install"), InstallUI.resourceBundle.getString("Information"), 1);
                return;
            }
            this.logsList = apply2.getSelectedContext();
            final InstallProgress inprogress2 = (InstallProgress)this.cardPanel.getCard("third");
            inprogress2.setInstallDirectory(dirToUnzip);
            inprogress2.setPPMSize("0 ");
            this.disableButtons(false);
            this.setCursor(new Cursor(3));
            this.cancelButton.setText(InstallUI.resourceBundle.getString("Close"));
            this.cancelButton.setMnemonic('C');
            this.cardPanel.showCard("third");
            final Thread installThread2 = new Thread(apply2);
            installThread2.start();
        }
        this.nextButton.setCursor(new Cursor(0));
    }
    
    public void disableButtons(final boolean bool) {
        this.nextButton.setEnabled(false);
        this.backButton.setEnabled(false);
        if (UpdateManagerUtil.getExitStatus() == 1 && UpdateManagerUtil.getInstallState() != -1 && UpdateManagerUtil.getInstallState() != 2) {
            this.logButton.setEnabled(true);
        }
        this.cancelButton.setEnabled(bool);
    }
    
    public void setDefaultCursor() {
        this.readmeInFailure = true;
        this.setCursor(new Cursor(0));
        this.disableButtons(true);
    }
    
    public void setWaitCursor(final boolean bool) {
        if (bool) {
            this.setCursor(new Cursor(3));
        }
        else {
            this.setCursor(new Cursor(0));
        }
    }
    
    public void updateTheVersion() {
        ((UpdateManagerUI)this.frame).getVersionInfo();
    }
    
    public void enableTheButton() {
        this.disableButtons(true);
        final DefaultFocusManager fm = (DefaultFocusManager)FocusManager.getCurrentManager();
        fm.focusNextComponent(this.nextButton);
        this.setCursor(new Cursor(0));
    }
    
    public void failure() {
        this.disableButtons(false);
    }
    
    public String getConfProductName() {
        return this.confProductName;
    }
    
    public String getConfProductVersion() {
        return this.confProductVersion;
    }
    
    public String getConfSubProductVersion() {
        return this.confSubProductName;
    }
    
    public JFrame getTopFrame() {
        return this.frame;
    }
    
    public JDialog getDialog() {
        return this;
    }
    
    public void addWinListenerWithEscSupport() {
        this.setDefaultCloseOperation(0);
        this.addWindowListener(this);
        final KeyStroke escStroke = KeyStroke.getKeyStroke(27, 0);
        this.getRootPane().registerKeyboardAction(new EscListenerClass(), " ", escStroke, 2);
    }
    
    private void closingOperation() {
        final int state = UpdateManagerUtil.getInstallState();
        if (state == UpdateManagerUtil.INSTALL_IN_PROGRESS) {
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
    
    public void setNextButtonEnabled(final boolean bool) {
        this.nextButton.setEnabled(bool);
    }
    
    public void setCheckBoxCount(final int count) {
        this.checkBoxCount = count;
    }
    
    public boolean getInnerCheckBoxState() {
        return this.innerCheckBoxEnabled;
    }
    
    public void setInnerCheckBoxState(final boolean bool) {
        this.innerCheckBoxEnabled = bool;
    }
    
    private void showLogsDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ContextReadme read = new ContextReadme();
                final String pVersion = InstallUI.this.common.getPatchVersion();
                if (InstallUI.this.logsList != null) {
                    read.displayLogsDialog(InstallUI.this.logsList, InstallUI.this.getDialog(), pVersion);
                }
            }
        }, "InstallLogsThread").start();
    }
    
    public CardPanel getCardPanel() {
        return this.cardPanel;
    }
    
    public void showDialog(final boolean bool) {
        super.setVisible(bool);
    }
    
    public void setCancelButtonText(final String text) {
        this.cancelButton.setText(text);
        this.cancelButton.setMnemonic('C');
    }
    
    public void setNextButtonText(final String text) {
        this.nextButton.setText(text);
    }
    
    public void setCommonObject(final Common com) {
        this.common = com;
    }
    
    static {
        InstallUI.resourceBundle = null;
    }
    
    class logButton_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final LoggingUtil logg = new LoggingUtil();
            logg.showError(InstallUI.this.frame);
        }
    }
    
    class nextButton_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            InstallUI.this.installActionPerformed();
            InstallUI.this.nextButton.setCursor(new Cursor(0));
        }
    }
    
    class cancelButton_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final String CardName = InstallUI.this.cardPanel.getSelectedCardName();
            if (CardName.equals("third")) {
                final InstallProgress ipScreen = (InstallProgress)InstallUI.this.cardPanel.getSelectedCard();
                final boolean bool = ipScreen.closeCheckBox.isSelected();
                if (bool) {
                    InstallUI.this.setVisible(false);
                    InstallUI.this.dispose();
                    if (InstallUI.this.readmeInFailure) {
                        InstallUI.this.showLogsDialog();
                    }
                    else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final ContextReadme read = new ContextReadme();
                                final String pVersion = InstallUI.this.common.getPatchVersion();
                                final String readmefilename = InstallUI.this.common.getPatchReadme();
                                final String fileName = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + pVersion + File.separator + readmefilename;
                                read.readTheInfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + pVersion + File.separator + "inf.xml");
                                if (read.getReadMeType() == 1) {
                                    read.displayTabbedReadme(fileName, InstallUI.this.getDialog(), pVersion);
                                }
                                else {
                                    UpdateManagerUtil.displayReadMe(read.getPatchFileReadme(), read.getReadMeType(), File.separator + "Patch" + File.separator + pVersion);
                                }
                            }
                        }, "InstallReadmeThread").start();
                    }
                }
                else {
                    InstallUI.this.setVisible(false);
                    InstallUI.this.dispose();
                }
            }
            else {
                InstallUI.this.setVisible(false);
                InstallUI.this.dispose();
            }
        }
    }
    
    class backButton_cardPanel_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            final String CardName = InstallUI.this.cardPanel.getSelectedCardName();
            if (CardName.equals("third")) {
                InstallUI.this.nextButton.setText(InstallUI.resourceBundle.getString("Next"));
                InstallUI.this.nextButton.setMnemonic('N');
                InstallUI.this.cancelButton.setText(InstallUI.resourceBundle.getString("Cancel"));
                InstallUI.this.cardPanel.showCard("second");
            }
            else if (CardName.equals("second")) {
                InstallUI.this.nextButton.setText(InstallUI.resourceBundle.getString("Next"));
                InstallUI.this.nextButton.setMnemonic('N');
                InstallUI.this.cancelButton.setText(InstallUI.resourceBundle.getString("Cancel"));
                InstallUI.this.backButton.setEnabled(false);
                InstallUI.this.cardPanel.showCard("first");
            }
        }
    }
    
    class CheckBoxRenderer implements TableCellRenderer
    {
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            if (value == null) {
                return null;
            }
            final Component comp = (Component)value;
            if (isSelected) {
                final Color bg = table.getSelectionBackground();
                comp.setBackground(bg);
                final Color fg = table.getSelectionForeground();
                comp.setForeground(fg);
            }
            else {
                comp.setBackground(Color.white);
                comp.setForeground(Color.white);
            }
            return comp;
        }
    }
    
    class CheckBoxEditor extends DefaultCellEditor implements ItemListener
    {
        private JCheckBox comp;
        private JTable table;
        
        public CheckBoxEditor(final JCheckBox checkBox) {
            super(checkBox);
        }
        
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
            this.table = table;
            if (value == null) {
                return null;
            }
            this.comp = (JCheckBox)value;
            if (isSelected) {
                this.comp.setBackground(Color.white);
                this.comp.setForeground(Color.white);
            }
            else {
                final Color bg = table.getSelectionBackground();
                this.comp.setBackground(bg);
                final Color fg = table.getSelectionForeground();
                this.comp.setForeground(fg);
            }
            this.comp.addItemListener(this);
            return this.comp;
        }
        
        @Override
        public Object getCellEditorValue() {
            this.comp.removeItemListener(this);
            return this.comp;
        }
        
        @Override
        public void itemStateChanged(final ItemEvent e) {
            super.fireEditingStopped();
            final NonEditableTableModel dtm = (NonEditableTableModel)this.table.getModel();
            final int count = dtm.getRowCount();
            InstallUI.this.innerCheckBoxEnabled = true;
            if (this.comp.isSelected()) {
                InstallUI.this.checkBoxCount++;
            }
            else if (InstallUI.this.checkBoxCount > 0) {
                InstallUI.this.checkBoxCount--;
            }
            final ContextScreen conScreen = (ContextScreen)InstallUI.this.cardPanel.getCard("second");
            if (count == InstallUI.this.checkBoxCount) {
                conScreen.JCheckBox1.setSelected(true);
            }
            else if (InstallUI.this.checkBoxCount == 0) {
                conScreen.JCheckBox1.setSelected(false);
            }
            else if (conScreen.JCheckBox1.isSelected()) {
                conScreen.JCheckBox1.setSelected(false);
            }
            conScreen.JCheckBox1.repaint();
        }
    }
    
    public class EscListenerClass implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent aEvtArg) {
            InstallUI.this.closingOperation();
            InstallUI.this.windowClosing(new WindowEvent(InstallUI.this, 201));
        }
    }
}
