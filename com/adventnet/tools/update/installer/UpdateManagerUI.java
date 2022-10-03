package com.adventnet.tools.update.installer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.UIManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellEditor;
import javax.swing.JCheckBox;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.util.Hashtable;
import java.nio.file.Path;
import java.util.Collection;
import com.zoho.tools.CertificateUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import javax.swing.JDialog;
import java.util.logging.Level;
import com.adventnet.tools.update.viewer.DiffUtility;
import java.util.Vector;
import java.util.HashMap;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.io.File;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.ListCellRenderer;
import java.awt.Font;
import javax.swing.ListModel;
import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import com.adventnet.tools.update.CommonUtil;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Insets;
import java.util.List;
import java.awt.GridBagConstraints;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.applet.Applet;
import java.util.logging.Logger;
import java.awt.Color;
import javax.swing.event.HyperlinkListener;
import javax.swing.JFrame;

public class UpdateManagerUI extends JFrame implements ParameterChangeListener, HyperlinkListener
{
    private static final Color DEFAULT_BACKGROUND_COLOR;
    private static Logger out;
    private boolean initialized;
    private Applet applet;
    private String localePropertiesFileName;
    static BuilderResourceBundle resourceBundle;
    private static final String[] PARAM;
    private boolean running;
    JPanel topPanel;
    JPanel appButtonsPanel;
    JPanel optionsPanel;
    JButton exitButton;
    JButton mainHelpButton;
    JSeparator separator1;
    JSeparator separator2;
    JPanel installPanel;
    JPanel installCenterPanel;
    JPanel installBottomPanel;
    JLabel installIconLabel;
    JLabel installLabel;
    JTextArea installDescriptionLabel;
    JButton browsePatchButton;
    JButton previewButton;
    JButton importCertButton;
    JButton readmeButton;
    JButton installButton;
    JTextField patchPath;
    ContextSensitiveHelpButton helpButton;
    JPanel installedServicePackTopPanel;
    JPanel installedServicePackPanel;
    JPanel installedServicePackBottomPanel;
    JLabel installedServicePackIconLabel;
    JLabel installedServicePackLabel;
    JTextArea installedServicePackDescriptionLabel;
    JButton uninstallButton;
    JButton detailsButton;
    JScrollPane installedServicePackPane;
    JList<String> installedServicePackList;
    DefaultListModel<String> defaultListModel1;
    private static ParameterObject po;
    GridBagConstraints cons;
    private String confProductName;
    private String confSubProductName;
    private boolean standaloneBoolean;
    private ReadmeUI readUI;
    private List<String> fpackList;
    private Common common;
    private boolean innerCheckBoxEnabled;
    private int checkBoxCount;
    private CardPanel cardPanel;
    private static final Insets LAYOUT_INSETS;
    private static final Insets PANEL_INSETS;
    private static final Insets ICON_INSETS;
    private static final Insets LABEL_INSETS;
    private static final Insets INPUT_INSETS;
    private static final Insets INLINE_BUTTON_INSETS;
    private static final Insets ZERO_INSETS;
    
    public static void main(final String[] args) {
        UpdateManagerUI.po = new ParameterObject(UpdateManagerUI.PARAM, args);
        Utility.parseAndSetParameters(UpdateManagerUI.PARAM, args);
        final UpdateManagerUI frame = new UpdateManagerUI();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                System.exit(0);
            }
        });
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
        UpdateManagerUI.resourceBundle = CommonUtil.getResourceBundle();
        if (this.initialized) {
            this.setUpProperties();
            return;
        }
        this.setSize(this.getPreferredSize().width + 476, this.getPreferredSize().height + 770);
        this.setTitle(UpdateManagerUI.resourceBundle.getString("Update Manager"));
        final Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        try {
            this.initVariables();
            this.setUpGUI(container);
            this.setUpProperties();
            this.setUpConnections();
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Error in init method"), ex);
        }
        this.initialized = true;
        this.getVersionInfo();
        this.setHelpFiles();
        this.closingOperation();
    }
    
    public String getParameter(final String input) {
        if (UpdateManagerUI.po != null && UpdateManagerUI.po.getParameter(input) != null) {
            return (String)UpdateManagerUI.po.getParameter(input);
        }
        String value;
        if (this.applet != null) {
            value = this.applet.getParameter(input);
        }
        else {
            value = (String)Utility.getParameter(input);
        }
        if (value == null) {
            if (input.equals("RESOURCE_PROPERTIES")) {
                value = "UpdateManagerResources";
            }
            if (input.equals("RESOURCE_LOCALE")) {
                value = "en_US";
            }
        }
        return value;
    }
    
    public void setUpProperties() {
        this.setIconImage(Utility.findImage("./com/adventnet/tools/update/installer/images/update_manager_icon.png", this.applet, true).getImage());
        final Dimension dimension = new Dimension(500, 600);
        this.setSize(dimension);
        this.setMinimumSize(dimension);
        if (!UpdateManager.isInvokedForAutoApplyOfPatches() || UpdateManager.getSuccessfullyAppliedPatchesCount() == 0 || UpdateManager.getAlreadyCompletedPrePostClassName() != null) {
            Assorted.positionTheWindow(this, "Center");
        }
        this.setResizable(true);
        try {
            this.exitButton.setFont(UpdateManagerUtil.getFont());
            this.exitButton.setHorizontalTextPosition(4);
            this.exitButton.setText(CommonUtil.getString(MessageConstants.EXIT));
            this.exitButton.setMnemonic('x');
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.exitButton, ex);
        }
        try {
            this.mainHelpButton.setFont(UpdateManagerUtil.getFont());
            this.mainHelpButton.setHorizontalTextPosition(4);
            this.mainHelpButton.setText(CommonUtil.getString(MessageConstants.HELP));
            this.mainHelpButton.setMnemonic('H');
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.mainHelpButton, ex);
        }
        try {
            this.optionsPanel.setBorder(new BevelBorder(0));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.optionsPanel, ex);
        }
        try {
            this.uninstallButton.setFont(UpdateManagerUtil.getFont());
            this.uninstallButton.setHorizontalTextPosition(4);
            this.uninstallButton.setMnemonic(85);
            this.uninstallButton.setText(CommonUtil.getString(MessageConstants.UNINSTALL));
            this.uninstallButton.setEnabled(false);
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.uninstallButton, ex);
        }
        try {
            this.detailsButton.setFont(UpdateManagerUtil.getFont());
            this.detailsButton.setHorizontalTextPosition(4);
            this.detailsButton.setMnemonic(68);
            this.detailsButton.setEnabled(false);
            this.detailsButton.setText(CommonUtil.getString(MessageConstants.DETAILS));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.detailsButton, ex);
        }
        try {
            this.installedServicePackLabel.setHorizontalAlignment(2);
            this.installedServicePackLabel.setForeground(new Color(-16777216));
            this.installedServicePackLabel.setHorizontalTextPosition(4);
            this.installedServicePackLabel.setFont(UpdateManagerUtil.getBoldFont());
            this.installedServicePackLabel.setText(CommonUtil.getString(MessageConstants.SERVICE_PACK_LIST).toUpperCase());
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installedServicePackLabel, ex);
        }
        try {
            this.installedServicePackDescriptionLabel.setForeground(new Color(-16777216));
            this.installedServicePackDescriptionLabel.setBackground(UpdateManagerUI.DEFAULT_BACKGROUND_COLOR);
            this.installedServicePackDescriptionLabel.setLineWrap(true);
            this.installedServicePackDescriptionLabel.setWrapStyleWord(true);
            this.installedServicePackDescriptionLabel.setEditable(false);
            this.installedServicePackDescriptionLabel.setText(CommonUtil.getString(MessageConstants.SERVICE_PACK_LIST_DESCRIPTION));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installedServicePackDescriptionLabel, ex);
        }
        this.installedServicePackDescriptionLabel.setFont(UpdateManagerUtil.getFont());
        try {
            this.installedServicePackIconLabel.setHorizontalAlignment(2);
            this.installedServicePackIconLabel.setFont(UpdateManagerUtil.getFont());
            this.installedServicePackIconLabel.setForeground(new Color(-16777216));
            this.installedServicePackIconLabel.setHorizontalTextPosition(4);
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installedServicePackIconLabel, ex);
        }
        this.installedServicePackIconLabel.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/uninstall.png", this.applet, true));
        try {
            this.installedServicePackList.setBorder(new BevelBorder(1));
            this.installedServicePackList.setModel(this.defaultListModel1);
            this.installedServicePackList.setSelectionMode(0);
            this.installedServicePackList.setSelectedIndex(-1);
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installedServicePackList, ex);
        }
        try {
            this.installLabel.setHorizontalAlignment(2);
            this.installLabel.setForeground(Color.BLACK);
            this.installLabel.setHorizontalTextPosition(4);
            this.installLabel.setText(CommonUtil.getString(MessageConstants.INSTALL).toUpperCase());
            this.installLabel.setFont(UpdateManagerUtil.getBoldFont());
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installLabel, ex);
        }
        try {
            this.installIconLabel.setHorizontalAlignment(2);
            this.installIconLabel.setFont(UpdateManagerUtil.getFont());
            this.installIconLabel.setForeground(new Color(-16777216));
            this.installIconLabel.setHorizontalTextPosition(4);
            this.installIconLabel.setVerticalTextPosition(0);
            this.installIconLabel.setVerticalAlignment(0);
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installIconLabel, ex);
        }
        this.installIconLabel.setIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/install.png", this.applet, true));
        try {
            this.installDescriptionLabel.setFont(UpdateManagerUtil.getFont());
            this.installDescriptionLabel.setForeground(new Color(-16777216));
            this.installDescriptionLabel.setBackground(UpdateManagerUI.DEFAULT_BACKGROUND_COLOR);
            this.installDescriptionLabel.setLineWrap(true);
            this.installDescriptionLabel.setWrapStyleWord(true);
            this.installDescriptionLabel.setEditable(false);
            this.installDescriptionLabel.setText(CommonUtil.getString(MessageConstants.INSTALL_DESCRIPTION));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installDescriptionLabel, ex);
        }
        try {
            this.helpButton.setFont(UpdateManagerUtil.getFont());
            this.helpButton.setHorizontalTextPosition(4);
            this.helpButton.setPreferredSize(new Dimension(32, 27));
            this.helpButton.setMinimumSize(new Dimension(32, 27));
            this.helpButton.setMaximumSize(new Dimension(32, 27));
            this.helpButton.setIcon(Utility.getResizedIcon(Utility.findImage("./com/adventnet/tools/update/installer/images/context_help.png", this.applet, true), 16, 16));
            this.helpButton.setText(UpdateManagerUI.resourceBundle.getString(""));
            this.helpButton.setBorderPainted(false);
            this.helpButton.setToolTipText("Help");
            this.helpButton.setFocusable(false);
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.helpButton, ex);
        }
        try {
            this.previewButton.setFont(new Font("SansSerif", 0, 12));
            this.previewButton.setHorizontalTextPosition(4);
            this.previewButton.setText(UpdateManagerUI.resourceBundle.getString("Preview"));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.previewButton, ex);
        }
        try {
            this.importCertButton.setFont(new Font("SansSerif", 0, 12));
            this.importCertButton.setHorizontalTextPosition(4);
            this.importCertButton.setText(CommonUtil.getString(MessageConstants.IMPORT_CERTIFICATE));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.importCertButton, ex);
        }
        try {
            this.readmeButton.setFont(new Font("SansSerif", 0, 12));
            this.readmeButton.setHorizontalTextPosition(4);
            this.readmeButton.setText(CommonUtil.getString(MessageConstants.README));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.readmeButton, ex);
        }
        try {
            this.installButton.setFont(new Font("SansSerif", 0, 12));
            this.installButton.setHorizontalTextPosition(4);
            this.installButton.setText(CommonUtil.getString(MessageConstants.INSTALL));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.installButton, ex);
        }
        try {
            this.browsePatchButton.setFont(new Font("SansSerif", 0, 12));
            this.browsePatchButton.setHorizontalTextPosition(4);
            this.browsePatchButton.setText(CommonUtil.getString(MessageConstants.BROWSE));
            this.browsePatchButton.setMnemonic('w');
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.browsePatchButton, ex);
        }
        try {
            this.patchPath.setHorizontalAlignment(2);
            this.patchPath.setFont(new Font("SansSerif", 0, 12));
            this.patchPath.setMinimumSize(new Dimension(4, 24));
            this.patchPath.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            this.patchPath.setPreferredSize(new Dimension(4, 24));
        }
        catch (final Exception ex) {
            this.showStatus(UpdateManagerUI.resourceBundle.getString("Exception while setting properties for bean ") + this.patchPath, ex);
        }
        this.detailsButton.setPreferredSize(new Dimension(this.detailsButton.getPreferredSize().width + 14, this.detailsButton.getPreferredSize().height + 1));
        this.uninstallButton.setPreferredSize(new Dimension(this.uninstallButton.getPreferredSize().width + 3, this.uninstallButton.getPreferredSize().height + 1));
        this.mainHelpButton.setPreferredSize(new Dimension(this.mainHelpButton.getPreferredSize().width + 27, this.mainHelpButton.getPreferredSize().height + 1));
        this.exitButton.setPreferredSize(new Dimension(this.exitButton.getPreferredSize().width + 34, this.exitButton.getPreferredSize().height + 1));
        if (this.patchPath.getText().equals("")) {
            this.readmeButton.setEnabled(false);
            this.previewButton.setEnabled(false);
            this.installButton.setEnabled(false);
        }
    }
    
    public void initVariables() {
        this.topPanel = new JPanel();
        this.appButtonsPanel = new JPanel();
        this.exitButton = new JButton();
        this.mainHelpButton = new JButton();
        this.optionsPanel = new JPanel();
        this.separator1 = new JSeparator();
        this.installedServicePackPanel = new JPanel();
        this.installedServicePackBottomPanel = new JPanel();
        this.uninstallButton = new JButton();
        this.detailsButton = new JButton();
        this.installedServicePackTopPanel = new JPanel();
        this.installedServicePackLabel = new JLabel();
        this.installedServicePackDescriptionLabel = new JTextArea();
        this.installedServicePackIconLabel = new JLabel();
        this.installedServicePackPane = new JScrollPane();
        this.installedServicePackList = new JList<String>();
        this.installPanel = new JPanel();
        this.installLabel = new JLabel();
        this.installIconLabel = new JLabel();
        this.installDescriptionLabel = new JTextArea();
        this.separator2 = new JSeparator();
        this.installCenterPanel = new JPanel();
        this.installBottomPanel = new JPanel();
        this.browsePatchButton = new JButton();
        this.patchPath = new JTextField();
        this.previewButton = new JButton();
        this.readmeButton = new JButton();
        this.installButton = new JButton();
        this.helpButton = new ContextSensitiveHelpButton("./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png");
        this.defaultListModel1 = new DefaultListModel<String>();
        this.initializeParameters();
        this.installedServicePackList.setCellRenderer(new MyCellRenderer());
        (this.helpButton = ContextSensitiveHelpButton.getHelpButton(UpdateManagerUtil.getHelpXmlFilePath(), UpdateManagerUtil.getHelpHtmlFilePath(), "./com/adventnet/tools/update/installer/images/help_icon.png", "./com/adventnet/tools/update/installer/images/no_help_icon.png")).setHelpWindowSize(new Dimension(400, 75));
    }
    
    private void initInstall() {
        this.installPanel.setLayout(new GridBagLayout());
        this.setConstraints(2, 0, 1, 1, 0.0, 0.0, 10, 0, UpdateManagerUI.ZERO_INSETS, 0, 0);
        this.installPanel.add(this.importCertButton, this.cons);
        this.setConstraints(3, 0, 1, 1, 0.0, 0.0, 10, 0, UpdateManagerUI.ZERO_INSETS, 0, 0);
        if (UpdateManagerUtil.getHelpXmlFilePath() != null && !UpdateManagerUtil.getHelpXmlFilePath().trim().isEmpty() && new File(UpdateManagerUtil.getHelpXmlFilePath()).exists()) {
            this.installPanel.add(this.helpButton, this.cons);
        }
        this.setConstraints(0, 1, 1, 2, 0.0, 0.0, 10, 1, UpdateManagerUI.ICON_INSETS, 0, 0);
        this.installPanel.add(this.installIconLabel, this.cons);
        this.setConstraints(1, 1, 1, 1, 0.1, 0.0, 10, 2, UpdateManagerUI.LABEL_INSETS, 0, 0);
        this.installPanel.add(this.installLabel, this.cons);
        this.setConstraints(1, 2, 2, 1, 0.0, 0.0, 10, 2, UpdateManagerUI.LABEL_INSETS, 0, 0);
        this.installPanel.add(this.installDescriptionLabel, this.cons);
        this.setConstraints(1, 3, 0, 1, 0.0, 0.0, 10, 2, UpdateManagerUI.ZERO_INSETS, 0, 0);
        this.installPanel.add(this.installCenterPanel, this.cons);
        this.setConstraints(0, 4, 0, 1, 0.0, 0.0, 10, 2, UpdateManagerUI.PANEL_INSETS, 0, 0);
        this.installPanel.add(this.installBottomPanel, this.cons);
        this.installCenterPanel.setLayout(new GridBagLayout());
        this.setConstraints(0, 0, 1, 1, 0.1, 0.0, 10, 2, UpdateManagerUI.INPUT_INSETS, 0, 0);
        this.installCenterPanel.add(this.patchPath, this.cons);
        this.setConstraints(1, 0, 1, 1, 0.0, 0.0, 10, 0, UpdateManagerUI.INLINE_BUTTON_INSETS, 0, 0);
        this.installCenterPanel.add(this.browsePatchButton, this.cons);
        this.installBottomPanel.setLayout(new FlowLayout(2, 5, 5));
        this.installBottomPanel.add(this.previewButton);
        this.installBottomPanel.add(this.readmeButton);
        this.installBottomPanel.add(this.installButton);
    }
    
    private void initInstalledSP() {
        this.installedServicePackTopPanel.setLayout(new GridBagLayout());
        this.setConstraints(0, 0, 1, 2, 0.0, 0.0, 10, 1, UpdateManagerUI.ICON_INSETS, 0, 0);
        this.installedServicePackTopPanel.add(this.installedServicePackIconLabel, this.cons);
        this.setConstraints(1, 0, 1, 1, 0.1, 0.0, 10, 2, UpdateManagerUI.LABEL_INSETS, 0, 0);
        this.installedServicePackTopPanel.add(this.installedServicePackLabel, this.cons);
        this.setConstraints(1, 1, 1, 1, 0.0, 0.0, 10, 2, UpdateManagerUI.LABEL_INSETS, 0, 0);
        this.installedServicePackTopPanel.add(this.installedServicePackDescriptionLabel, this.cons);
        this.installedServicePackPane.getViewport().add(this.installedServicePackList);
        this.installedServicePackBottomPanel.setLayout(new FlowLayout(2, 5, 5));
        this.installedServicePackBottomPanel.add(this.uninstallButton);
        this.installedServicePackBottomPanel.add(this.detailsButton);
        this.installedServicePackPanel.setLayout(new GridBagLayout());
        this.setConstraints(0, 0, 1, 1, 0.1, 0.0, 10, 2, UpdateManagerUI.PANEL_INSETS, 0, 0);
        this.installedServicePackPanel.add(this.installedServicePackTopPanel, this.cons);
        this.setConstraints(0, 1, 1, 1, 0.2, 0.1, 10, 1, UpdateManagerUI.ICON_INSETS, 0, 0);
        this.installedServicePackPanel.add(this.installedServicePackPane, this.cons);
        this.setConstraints(0, 2, 1, 1, 0.0, 0.0, 10, 1, UpdateManagerUI.PANEL_INSETS, 0, 0);
        this.installedServicePackPanel.add(this.installedServicePackBottomPanel, this.cons);
    }
    
    public void setUpGUI(final Container container) {
        this.initInstall();
        this.initInstalledSP();
        container.add(this.topPanel, "Center");
        this.topPanel.setLayout(new GridBagLayout());
        this.optionsPanel.setLayout(new GridBagLayout());
        this.setConstraints(0, 1, 1, 1, 0.1, 0.0, 10, 1, UpdateManagerUI.LAYOUT_INSETS, 0, 0);
        this.optionsPanel.add(this.installPanel, this.cons);
        this.setConstraints(0, 2, 1, 1, 0.1, 0.01, 10, 1, UpdateManagerUI.ICON_INSETS, 0, 0);
        this.optionsPanel.add(this.separator1, this.cons);
        this.setConstraints(0, 5, 1, 1, 0.4, 0.2, 10, 1, UpdateManagerUI.LAYOUT_INSETS, 0, 0);
        this.optionsPanel.add(this.installedServicePackPanel, this.cons);
        this.appButtonsPanel.setLayout(new FlowLayout(2, 5, 5));
        this.appButtonsPanel.add(this.exitButton);
        this.appButtonsPanel.add(this.mainHelpButton);
        this.setConstraints(0, 0, 1, 1, 0.2, 0.1, 10, 1, UpdateManagerUI.LAYOUT_INSETS, 0, 0);
        this.topPanel.add(this.optionsPanel, this.cons);
        this.setConstraints(0, 1, 1, 1, 0.1, 0.0, 10, 2, UpdateManagerUI.PANEL_INSETS, 0, 0);
        this.topPanel.add(this.appButtonsPanel, this.cons);
        this.previewButton.setVisible(UpdateManagerUtil.isDeploymentToolEnabled());
    }
    
    public void setUpConnections() {
        this.importCertButton.addActionListener(new ImportCertificateButtonAction());
        this.patchPath.addKeyListener(new PatchPathAction());
        this.browsePatchButton.addActionListener(new BrowsePatchButtonAction());
        this.installButton.addActionListener(new InstallButtonAction());
        this.installedServicePackList.addMouseListener(new InstalledServicePackMouseAction());
        this.installedServicePackList.addListSelectionListener(new InstalledServicePackListAction());
        this.uninstallButton.addActionListener(new UninstallButtonAction());
        this.detailsButton.addActionListener(new DetailsButtonAction());
        this.readmeButton.addActionListener(new ReadMeButtonAction());
        final diffButton_diffButton_conn diffButton_diffButton_conn1 = new diffButton_diffButton_conn();
        this.previewButton.addActionListener(diffButton_diffButton_conn1);
        this.mainHelpButton.addActionListener(new MainHelpButtonAction());
        this.exitButton.addActionListener(new ExitButtonAction());
        this.addWindowListener(new UpdateManagerUIWindowAdapter());
    }
    
    public void showStatus(final String message) {
        System.out.println("Internal Error :" + message);
    }
    
    public void showStatus(final String message, final Exception ex) {
        System.out.println("Internal Error :" + message);
        ex.printStackTrace();
    }
    
    public void setProperties(final Properties props) {
        if (UpdateManagerUI.po != null) {
            UpdateManagerUI.po.setParameters(props);
        }
    }
    
    @Override
    public void setParameterObject(final ParameterObject paramObj) {
        UpdateManagerUI.po = paramObj;
    }
    
    private void initializeParameters() {
        if (UpdateManagerUI.po != null) {
            UpdateManagerUI.po.addParameterChangeListener(this);
        }
    }
    
    @Override
    public void destroy() {
        if (UpdateManagerUI.po != null) {
            UpdateManagerUI.po.removeParameterChangeListener(this);
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
    
    public UpdateManagerUI() {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.topPanel = null;
        this.appButtonsPanel = null;
        this.optionsPanel = null;
        this.exitButton = null;
        this.mainHelpButton = null;
        this.separator1 = null;
        this.separator2 = null;
        this.installPanel = null;
        this.installCenterPanel = null;
        this.installBottomPanel = null;
        this.installIconLabel = null;
        this.installLabel = null;
        this.installDescriptionLabel = null;
        this.browsePatchButton = null;
        this.previewButton = null;
        this.importCertButton = new JButton();
        this.readmeButton = null;
        this.installButton = null;
        this.patchPath = null;
        this.helpButton = null;
        this.installedServicePackTopPanel = null;
        this.installedServicePackPanel = null;
        this.installedServicePackBottomPanel = null;
        this.installedServicePackIconLabel = null;
        this.installedServicePackLabel = null;
        this.installedServicePackDescriptionLabel = null;
        this.uninstallButton = null;
        this.detailsButton = null;
        this.installedServicePackPane = null;
        this.installedServicePackList = null;
        this.defaultListModel1 = null;
        this.cons = new GridBagConstraints();
        this.confProductName = null;
        this.confSubProductName = null;
        this.standaloneBoolean = false;
        this.readUI = null;
        this.fpackList = null;
        this.common = null;
        this.innerCheckBoxEnabled = false;
        this.checkBoxCount = 0;
        this.cardPanel = null;
        this.pack();
    }
    
    public UpdateManagerUI(final Applet applet) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.topPanel = null;
        this.appButtonsPanel = null;
        this.optionsPanel = null;
        this.exitButton = null;
        this.mainHelpButton = null;
        this.separator1 = null;
        this.separator2 = null;
        this.installPanel = null;
        this.installCenterPanel = null;
        this.installBottomPanel = null;
        this.installIconLabel = null;
        this.installLabel = null;
        this.installDescriptionLabel = null;
        this.browsePatchButton = null;
        this.previewButton = null;
        this.importCertButton = new JButton();
        this.readmeButton = null;
        this.installButton = null;
        this.patchPath = null;
        this.helpButton = null;
        this.installedServicePackTopPanel = null;
        this.installedServicePackPanel = null;
        this.installedServicePackBottomPanel = null;
        this.installedServicePackIconLabel = null;
        this.installedServicePackLabel = null;
        this.installedServicePackDescriptionLabel = null;
        this.uninstallButton = null;
        this.detailsButton = null;
        this.installedServicePackPane = null;
        this.installedServicePackList = null;
        this.defaultListModel1 = null;
        this.cons = new GridBagConstraints();
        this.confProductName = null;
        this.confSubProductName = null;
        this.standaloneBoolean = false;
        this.readUI = null;
        this.fpackList = null;
        this.common = null;
        this.innerCheckBoxEnabled = false;
        this.checkBoxCount = 0;
        this.cardPanel = null;
        this.applet = applet;
        this.pack();
    }
    
    private JFrame getFrame() {
        return this;
    }
    
    public UpdateManagerUI(final String confProductName, final String confSubProductName, final boolean standaloneBoolean) {
        this.initialized = false;
        this.applet = null;
        this.localePropertiesFileName = "UpdateManagerResources";
        this.running = false;
        this.topPanel = null;
        this.appButtonsPanel = null;
        this.optionsPanel = null;
        this.exitButton = null;
        this.mainHelpButton = null;
        this.separator1 = null;
        this.separator2 = null;
        this.installPanel = null;
        this.installCenterPanel = null;
        this.installBottomPanel = null;
        this.installIconLabel = null;
        this.installLabel = null;
        this.installDescriptionLabel = null;
        this.browsePatchButton = null;
        this.previewButton = null;
        this.importCertButton = new JButton();
        this.readmeButton = null;
        this.installButton = null;
        this.patchPath = null;
        this.helpButton = null;
        this.installedServicePackTopPanel = null;
        this.installedServicePackPanel = null;
        this.installedServicePackBottomPanel = null;
        this.installedServicePackIconLabel = null;
        this.installedServicePackLabel = null;
        this.installedServicePackDescriptionLabel = null;
        this.uninstallButton = null;
        this.detailsButton = null;
        this.installedServicePackPane = null;
        this.installedServicePackList = null;
        this.defaultListModel1 = null;
        this.cons = new GridBagConstraints();
        this.confProductName = null;
        this.confSubProductName = null;
        this.standaloneBoolean = false;
        this.readUI = null;
        this.fpackList = null;
        this.common = null;
        this.innerCheckBoxEnabled = false;
        this.checkBoxCount = 0;
        this.cardPanel = null;
        UpdateManagerUtil.loadUIManager();
        this.confProductName = confProductName;
        this.confSubProductName = confSubProductName;
        this.standaloneBoolean = standaloneBoolean;
        this.pack();
    }
    
    public void getVersionInfo() {
        final String installDir = UpdateManagerUtil.getHomeDirectory();
        final String specsPath = installDir + File.separator + "Patch" + File.separator + "specs.xml";
        final File specFile = new File(specsPath);
        final DefaultListModel<String> dlm = (DefaultListModel)this.installedServicePackList.getModel();
        dlm.clear();
        this.fpackList = new ArrayList<String>();
        if (specFile.isFile()) {
            final VersionProfile vProfile = VersionProfile.getInstance();
            vProfile.readDocument(specsPath, false, false);
            if (vProfile.getRootElement() == null) {
                throw new RuntimeException("\"" + specFile.getAbsolutePath() + "\" which holds already applied patch details is Empty.");
            }
            final String[] versionArray = vProfile.getAllVersions();
            if (versionArray != null) {
                int i;
                for (int size = i = versionArray.length; i > 0; --i) {
                    final String version = versionArray[i - 1];
                    final String desc = vProfile.getTheAdditionalDetail(version, "Description");
                    String displayName = vProfile.getTheAdditionalDetail(version, "DisplayName");
                    if (displayName == null || displayName.trim().equals("")) {
                        displayName = version;
                    }
                    final String disp = displayName + " [" + desc.trim() + "]";
                    String type = vProfile.getTheAdditionalDetail(version, "Type");
                    if (type == null || type.trim().equals("")) {
                        type = "SP";
                    }
                    if (type.equals("FP")) {
                        this.fpackList.add(disp);
                    }
                    dlm.addElement(disp);
                }
            }
            else {
                this.uninstallButton.setEnabled(false);
                this.detailsButton.setEnabled(false);
            }
            if (!dlm.isEmpty()) {
                this.installedServicePackList.setSelectedIndex(0);
            }
            this.installedServicePackList.repaint();
            this.installedServicePackList.revalidate();
        }
        else {
            this.uninstallButton.setEnabled(false);
            this.detailsButton.setEnabled(false);
        }
    }
    
    private void uninstallActionPerformed() {
        final int selected = this.installedServicePackList.getSelectedIndex();
        final int count = ((DefaultListModel)this.installedServicePackList.getModel()).size();
        if (count == 0) {
            JOptionPane.showMessageDialog(this.getFrame(), UpdateManagerUI.resourceBundle.getString("No Service Pack is installed"), UpdateManagerUI.resourceBundle.getString("Information"), 1);
            return;
        }
        if (selected != -1) {
            this.setCursor(new Cursor(3));
            final String selectedVer = ((DefaultListModel)this.installedServicePackList.getModel()).get(selected);
            final ArrayList<String> list = UpdateManagerUtil.getTheListToUninstall(selectedVer, selected, true, this.getFrame(), true, true);
            if (list == null) {
                this.setCursor(new Cursor(0));
                return;
            }
            final UninstallUI uninstall = new UninstallUI(this.getFrame(), list, this.confProductName, UpdateManager.getProductVersion(UpdateManager.getUpdateConfPath()), this.confSubProductName);
            uninstall.setModal(true);
            uninstall.setVisible(true);
            this.setCursor(new Cursor(0));
        }
        else {
            JOptionPane.showMessageDialog(this.getFrame(), UpdateManagerUI.resourceBundle.getString("Select a version to uninstall"), UpdateManagerUI.resourceBundle.getString("Information"), 1);
        }
    }
    
    private void detailsActionPerformed() {
        this.detailsButton.setEnabled(false);
        this.setCursor(new Cursor(3));
        final int count = ((DefaultListModel)this.installedServicePackList.getModel()).size();
        if (count == 0) {
            this.setCursor(new Cursor(0));
            return;
        }
        final String selectedVer = this.installedServicePackList.getSelectedValue();
        final String selec = selectedVer.substring(0, selectedVer.indexOf(" "));
        final String selected = UpdateManagerUtil.getOriginalVersion(selec);
        final int selectedIndex = this.installedServicePackList.getSelectedIndex();
        if (selected == null || selectedIndex == -1) {
            JOptionPane.showMessageDialog(this.getFrame(), UpdateManagerUI.resourceBundle.getString("Select a version to view its details"), UpdateManagerUI.resourceBundle.getString("Information"), 1);
        }
        else {
            final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
            final ContextReadme read = new ContextReadme();
            final String path = dirToUnzip + File.separator + "Patch" + File.separator + selected + File.separator;
            read.readTheInfFile(path + "inf.xml");
            final String readmeFileUrl = read.getPatchFileReadme();
            final int readMeType = read.getReadMeType();
            this.detailsButton.setCursor(new Cursor(3));
            final String specsFile = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
            final VersionProfile verProfile = VersionProfile.getInstance();
            verProfile.readDocument(specsFile, false, false);
            if (verProfile.getRootElement() == null) {
                throw new RuntimeException("\"" + specsFile + "\" which holds already applied patch details is Empty.");
            }
            final String desc = verProfile.getTheAdditionalDetail(selected, "Description");
            final String date = verProfile.getTheAdditionalDetail(selected, "Date");
            final String size = verProfile.getTheAdditionalDetail(selected, "Size");
            final String patchName = verProfile.getTheAdditionalDetail(selected, "PatchName");
            final String readme = read.getPatchFileReadme();
            final String readmePath = path + readme;
            if (readMeType == 1) {
                final String patchVersion = read.getPatchVersion();
                (this.readUI = new ReadmeUI()).setReadmeTitle(UpdateManagerUI.resourceBundle.getString("The Readme for Service Pack ") + " " + patchVersion + " " + UpdateManagerUI.resourceBundle.getString("is shown below"));
                try {
                    final URL url = new URL("file", "localhost", readmePath);
                    this.readUI.editor.addHyperlinkListener(this);
                    this.readUI.editor.setPage(url);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
            final String[] contextArray = verProfile.getTheContext(selected);
            final int arrayLength = contextArray.length;
            final StringBuffer appendLogs = new StringBuffer();
            appendLogs.append(selected + " \n\n");
            int k = 0;
            while (k < arrayLength) {
                final String con = contextArray[k];
                String logName = null;
                if (con.equals("NoContext")) {
                    logName = selected + "log.txt";
                    final File noContextLogFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "logs" + File.separator + logName);
                    final StringBuffer append = read.getTheLogs(noContextLogFile);
                    if (append != null) {
                        appendLogs.append(append.toString());
                        break;
                    }
                    break;
                }
                else {
                    logName = selected + con + "log.txt";
                    final File noContextLogFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "logs" + File.separator + logName);
                    final StringBuffer append = read.getTheLogs(noContextLogFile);
                    if (append != null) {
                        appendLogs.append(con + " context \n\n");
                        appendLogs.append(append.toString());
                        appendLogs.append("---------------------------------------------------------------------------------------------------\n\n");
                    }
                    ++k;
                }
            }
            final PatchDetails patchDescription = new PatchDetails(this.getFrame());
            patchDescription.setDialogTitle(selec + " " + UpdateManagerUI.resourceBundle.getString("version details"));
            patchDescription.init();
            if (appendLogs != null) {
                patchDescription.logsTextArea.append(appendLogs.toString());
            }
            if (readMeType == 1) {
                patchDescription.tabbedPane.addTab(UpdateManagerUI.resourceBundle.getString("Readme"), this.readUI);
            }
            else {
                patchDescription.setPatchReadMeUrl(readmeFileUrl);
                patchDescription.JLabel8.setText(UpdateManagerUI.resourceBundle.getString("Readme:"));
                patchDescription.readMeLable.setText("<html><a href=\"\">" + this.breakReadMeUrl(this.getReadMeUrlToDisplay(readmeFileUrl, readMeType, "Patch" + File.separator + selected + File.separator)) + "</a></html>");
                patchDescription.readMeLable.setCursor(new Cursor(12));
                this.readMeMouseCLick(patchDescription.readMeLable, readmeFileUrl, readMeType, "Patch" + File.separator + selected + File.separator);
            }
            patchDescription.patchNameLabel.setText(patchName);
            patchDescription.descLabel.setText(desc);
            patchDescription.dateLabel.setText(date);
            final String patchSize = UpdateManagerUtil.getSizeString(Long.parseLong(size));
            patchDescription.sizeLabel.setText(patchSize);
            patchDescription.setModal(true);
            patchDescription.showCornered();
            this.detailsButton.setCursor(new Cursor(0));
        }
        this.detailsButton.setEnabled(true);
        this.setCursor(new Cursor(0));
    }
    
    private void setHelpFiles() {
        this.installButton.setName("UpdateManagerUI_Install_Button");
        this.uninstallButton.setName("UpdateManagerUI_Uninstall_Button");
        this.detailsButton.setName("UpdateManagerUI_Details_Button");
        this.importCertButton.setName("UpdateManagerUI_ImportCert_Button");
        this.browsePatchButton.setName("UpdateManagerUI_Browse_Button");
    }
    
    public void closingOperation() {
        if (this.standaloneBoolean) {
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent evt) {
                    final Window[] arr$;
                    final Window[] win = arr$ = UpdateManagerUI.this.getOwnedWindows();
                    for (final Window window : arr$) {
                        if (window.isShowing()) {
                            UpdateManagerUI.this.setDefaultCloseOperation(0);
                            return;
                        }
                    }
                    System.exit(0);
                }
            });
        }
    }
    
    @Override
    public void hyperlinkUpdate(final HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            this.readUI.editor.setCursor(Cursor.getPredefinedCursor(12));
        }
        else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            this.readUI.editor.setCursor(Cursor.getPredefinedCursor(0));
        }
        else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            CommonUtil.displayURL(e.getURL().toExternalForm());
        }
    }
    
    public boolean validateTheFile(final String patchFile, final boolean performPreValidation) {
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final JFrame frame = this.getFrame();
        boolean returnValue = true;
        this.common = new Common(dirToUnzip, patchFile, true, this.confProductName, performPreValidation);
        if (this.common.install(frame)) {
            this.patchPath.setText(patchFile);
            final HashMap hashList = this.common.getTheCompatibleContext(this.confSubProductName);
            final Vector conv = this.common.getTheContext();
            if ((!conv.contains(this.confSubProductName) && !conv.contains("NoContext")) || hashList.isEmpty()) {}
            this.readmeButton.setEnabled(true);
            this.previewButton.setEnabled(true);
            this.installButton.setEnabled(true);
        }
        else {
            this.readmeButton.setEnabled(false);
            this.previewButton.setEnabled(false);
            this.installButton.setEnabled(false);
            if (this.patchPath != null && this.patchPath.getText() != null && !this.patchPath.getText().trim().isEmpty()) {
                this.patchPath.setText("");
            }
            returnValue = false;
        }
        this.setCursor(new Cursor(0));
        return returnValue;
    }
    
    public void readmeActionPerformed() {
        this.readmeButton.setEnabled(false);
        this.setCursor(new Cursor(3));
        final String patchFile = this.patchPath.getText();
        final String dirToUnzip = System.getProperty("user.dir");
        final File checkFile = new File(dirToUnzip + File.separator + "patchtemp");
        final ContextReadme read = new ContextReadme();
        final boolean check = read.extractInfFile(dirToUnzip, patchFile, this.getFrame());
        if (check) {
            read.readTheInfFile("patchtemp" + File.separator + "inf.xml");
            final String readmefilename = read.getPatchFileReadme();
            final String patchVersion = read.getPatchVersion();
            if (read.getReadMeType() != 3) {
                read.writeReadmeFile(checkFile, readmefilename, patchFile, readmefilename);
            }
            if (read.getReadMeType() == 1) {
                read.displayReadme("patchtemp" + File.separator + readmefilename, this.getFrame(), patchVersion + " version");
            }
            else {
                UpdateManagerUtil.displayReadMe(readmefilename, read.getReadMeType(), "patchtemp");
            }
        }
        this.setCursor(new Cursor(0));
        this.readmeButton.setEnabled(true);
    }
    
    public void diffViewerActionPerformed() {
        this.previewButton.setEnabled(false);
        this.setCursor(new Cursor(3));
        final String patchFile = this.patchPath.getText();
        final DiffUtility diff = new DiffUtility(this.getFrame(), this.confProductName, UpdateManager.getProductVersion(UpdateManager.getUpdateConfPath()), this.confSubProductName, patchFile);
        this.setCursor(new Cursor(0));
        this.previewButton.setEnabled(true);
    }
    
    private void installActionPerformed(final String patchFilePath) {
        try {
            if (UpdateManagerUtil.isContainerOfPatches(patchFilePath)) {
                if (UpdateManager.getAlreadyAppliedPatchesCount() == 0) {
                    Unzipper.extractPatchesFromPatch(patchFilePath, UpdateManagerUtil.getHomeDirectory());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new AutoApplyPatches().applyPatches(null);
                    }
                }).start();
                return;
            }
        }
        catch (final Throwable e) {
            Logger.getLogger(UpdateManagerUI.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        final InstallUI install = new InstallUI(this.getFrame(), this.confProductName, UpdateManager.getProductVersion(UpdateManager.getUpdateConfPath()), this.confSubProductName);
        install.init();
        install.setWaitCursor(true);
        install.setModal(true);
        this.cardPanel = install.getCardPanel();
        UpdateManagerUtil.setParent(install);
        final String CardName = this.cardPanel.getSelectedCardName();
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        List<String> alist = null;
        ArrayList logsList = null;
        install.setCommonObject(this.common);
        final String patchVersion = this.common.getPatchVersion();
        final HashMap hashList = this.common.getTheCompatibleContext(this.confSubProductName);
        final Vector conv = this.common.getTheContext();
        if (conv.contains(this.confSubProductName) || conv.contains("NoContext")) {
            if (hashList.isEmpty()) {
                alist = new ArrayList<String>();
                if (conv.contains("NoContext")) {
                    alist.add("NoContext");
                }
                else {
                    alist.add(this.confSubProductName);
                }
                final String homeDirectory = UpdateManagerUtil.getHomeDirectory();
                final String confPath = Paths.get(homeDirectory, "conf").toString();
                final Path instanceConfigPath = Paths.get(confPath, "um_instance.config");
                final Path keyStore = Paths.get(confPath, "manageengine.keystore");
                if (UpdateManager.getAlreadyCompletedPrePostClassName() != null) {
                    if (!Files.exists(instanceConfigPath, new LinkOption[0])) {
                        try {
                            UpdateManagerUtil.getInstanceConfig(instanceConfigPath.toString());
                        }
                        catch (final Exception e2) {
                            UpdateManagerUI.out.log(Level.SEVERE, "Problem while reading/generating instance config : " + e2.getMessage(), e2);
                        }
                    }
                    if (!Files.exists(keyStore, new LinkOption[0])) {
                        UpdateManager.autoImportCertificate(homeDirectory);
                        if (!CertificateUtil.isKeyStoreExists(confPath) || PatchIntegrityVerifier.verifyPatch(patchFilePath, confPath, UpdateManager.getInstanceConfig().getKeyStorePassword()) == PatchIntegrityState.SIGNATURE_DOES_NOT_MATCH) {
                            this.importCertificateActionPerformed();
                        }
                    }
                }
                final ApplyPatch apply = new ApplyPatch(new ArrayList((Collection<? extends E>)alist), dirToUnzip, patchVersion, this.common, true, this.getFrame());
                if (apply.isPatchAlreadyInstalled()) {
                    JOptionPane.showMessageDialog(this.getFrame(), UpdateManagerUI.resourceBundle.getString("The selected Service Pack is already installed"), UpdateManagerUI.resourceBundle.getString("Information"), 1);
                    return;
                }
                logsList = apply.getSelectedContext();
                final InstallProgress inProgress = (InstallProgress)this.cardPanel.getCard("third");
                inProgress.setInstallDirectory(dirToUnzip);
                inProgress.setPPMSize("0 ");
                install.setNextButtonText(UpdateManagerUI.resourceBundle.getString("Finish"));
                install.setCancelButtonText(UpdateManagerUI.resourceBundle.getString("Close"));
                install.disableButtons(false);
                this.cardPanel.showCard("third");
                final Thread installThread = new Thread(apply);
                installThread.start();
                install.showDialog(true);
            }
            else {
                final boolean isContextInstalled = this.contextInPatch(hashList, this.common.getContextTable(), patchFilePath, patchVersion, this.cardPanel);
                if (!isContextInstalled) {
                    JOptionPane.showMessageDialog(this.getFrame(), UpdateManagerUI.resourceBundle.getString("The selected Service Pack is already installed"), UpdateManagerUI.resourceBundle.getString("Information"), 1);
                    return;
                }
                install.setWaitCursor(false);
                install.setCancelButtonText(UpdateManagerUI.resourceBundle.getString("Cancel"));
                install.setNextButtonText(UpdateManagerUI.resourceBundle.getString("Finish"));
                install.setNextButtonEnabled(true);
                this.cardPanel.showCard("second");
                install.showDialog(true);
            }
            return;
        }
        JOptionPane.showMessageDialog(this.getFrame(), UpdateManagerUI.resourceBundle.getString("The selected Service Pack does not contains Mandatory upgrade "), UpdateManagerUI.resourceBundle.getString("Information"), 1);
    }
    
    private boolean contextInPatch(final HashMap hash, final Hashtable table, final String patchPath, final String versionDir, final CardPanel cardPanel) {
        final Object[] hashContext = hash.keySet().toArray();
        final int size = hashContext.length;
        final NonEditableTableModel dm = new NonEditableTableModel(0, 2);
        final ContextScreen contextScreen = (ContextScreen)cardPanel.getCard("second");
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
            if (verProfile.getRootElement() == null) {
                throw new RuntimeException("\"" + specsFile + "\" which holds already applied patch details is Empty.");
            }
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
    
    public boolean getInnerCheckBoxState() {
        return this.innerCheckBoxEnabled;
    }
    
    public void setInnerCheckBoxState(final boolean bool) {
        this.innerCheckBoxEnabled = bool;
    }
    
    public void setCheckBoxCount(final int count) {
        this.checkBoxCount = count;
    }
    
    public void showUI(final boolean bl) {
        super.setVisible(bl);
    }
    
    public void callActionPerformed(final String patchPath) {
        this.setCursor(new Cursor(3));
        this.installActionPerformed(patchPath);
        this.setCursor(new Cursor(0));
    }
    
    public void setPPMPath(final String path) {
        this.patchPath.setText(path.trim());
    }
    
    public String getPPMPath() {
        return this.patchPath.getText().trim();
    }
    
    public String getReadMeUrlToDisplay(String url, final int readMeType, final String patchToAppend) {
        if (readMeType == 2) {
            url = UpdateManagerUtil.getHomeDirectory() + File.separator + patchToAppend + File.separator + url;
        }
        return url;
    }
    
    public void readMeMouseCLick(final JLabel readMeLabel, final String readUrl, final int readMe, final String appendUrl) {
        final String rmurl = readUrl;
        final int rmurlType = readMe;
        final String pathToAppend = appendUrl;
        readMeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                UpdateManagerUtil.displayReadMe(rmurl, rmurlType, pathToAppend);
            }
        });
    }
    
    private String breakReadMeUrl(String readmeURL) {
        try {
            if (readmeURL != null && readmeURL.length() > 38) {
                readmeURL = readmeURL.substring(0, 38) + "<br>" + readmeURL.substring(38);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return readmeURL;
    }
    
    void importCertificateActionPerformed() {
        final ImportCertificateDialog importCertificateDialog = new ImportCertificateDialog();
        importCertificateDialog.pack();
        importCertificateDialog.setModal(true);
        importCertificateDialog.setVisible(true);
    }
    
    static {
        DEFAULT_BACKGROUND_COLOR = UIManager.getColor("Panel.background");
        UpdateManagerUI.out = Logger.getLogger(UpdateManagerUI.class.getName());
        UpdateManagerUI.resourceBundle = null;
        PARAM = new String[] { "RESOURCE_PROPERTIES", "RESOURCE_LOCALE" };
        UpdateManagerUI.po = null;
        LAYOUT_INSETS = new Insets(10, 10, 10, 10);
        PANEL_INSETS = new Insets(0, 5, 0, 5);
        ICON_INSETS = new Insets(5, 5, 5, 5);
        LABEL_INSETS = new Insets(5, 10, 5, 5);
        INPUT_INSETS = new Insets(5, 4, 5, 5);
        INLINE_BUTTON_INSETS = new Insets(5, 10, 5, 5);
        ZERO_INSETS = new Insets(0, 0, 0, 0);
    }
    
    class UninstallButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            UpdateManagerUI.this.uninstallActionPerformed();
        }
    }
    
    class DetailsButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UpdateManagerUI.this.detailsActionPerformed();
                }
            }, "DetailsThread").start();
        }
    }
    
    class InstalledServicePackListAction implements ListSelectionListener, Serializable
    {
        @Override
        public void valueChanged(final ListSelectionEvent arg0) {
            final String selected = UpdateManagerUI.this.installedServicePackList.getSelectedValue();
            final int selectedIndex = UpdateManagerUI.this.installedServicePackList.getSelectedIndex();
            if (selected == null || selectedIndex == -1) {
                UpdateManagerUI.this.uninstallButton.setEnabled(false);
                UpdateManagerUI.this.detailsButton.setEnabled(false);
            }
            else {
                UpdateManagerUI.this.uninstallButton.setEnabled(UpdateManagerUtil.getAllowUninstalltion());
                UpdateManagerUI.this.detailsButton.setEnabled(true);
            }
        }
    }
    
    class InstalledServicePackMouseAction extends MouseAdapter implements Serializable
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 2) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateManagerUI.this.detailsActionPerformed();
                    }
                }, "DoubleClickDetailsThread").start();
            }
        }
    }
    
    static class MainHelpButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            CommonUtil.displayURL(UpdateManagerUtil.getHelpHtmlFilePath());
        }
    }
    
    class ExitButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (UpdateManagerUI.this.standaloneBoolean) {
                System.exit(0);
            }
            else {
                UpdateManagerUI.this.setVisible(false);
            }
        }
    }
    
    class MyCellRenderer extends JLabel implements ListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final String s = value.toString();
            this.setText(s);
            this.setOpaque(true);
            if (isSelected) {
                this.setBackground(new Color(204, 204, 255));
                this.setForeground(Color.black);
                this.setFPColor(s);
            }
            else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
                this.setFPColor(s);
            }
            this.setEnabled(list.isEnabled());
            this.setFont(list.getFont());
            return this;
        }
        
        private void setFPColor(final String s) {
            if (UpdateManagerUI.this.fpackList.contains(s)) {
                this.setForeground(Color.blue);
            }
        }
    }
    
    class BrowsePatchButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            UpdateManagerUI.this.browsePatchButton.setEnabled(false);
            UpdateManagerUI.this.setCursor(new Cursor(3));
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(0);
            fileChooser.setDialogTitle(CommonUtil.getString(MessageConstants.SELECT_FILE));
            fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
            fileChooser.addChoosableFileFilter(new PatchFilter("ppm"));
            final int fileChosen = fileChooser.showOpenDialog(UpdateManagerUI.this.getFrame());
            UpdateManagerUI.this.browsePatchButton.setEnabled(true);
            if (fileChosen != 0) {
                UpdateManagerUI.this.setCursor(new Cursor(0));
                return;
            }
            final File selFile = fileChooser.getSelectedFile();
            if (!selFile.exists()) {
                JOptionPane.showMessageDialog(UpdateManagerUI.this.getFrame(), UpdateManagerUI.resourceBundle.getString("The file that you have specified does not exist."), UpdateManagerUI.resourceBundle.getString("Error"), 2);
                UpdateManagerUI.this.setCursor(new Cursor(0));
                return;
            }
            final String patchFile = selFile.getAbsolutePath().trim();
            UpdateManager.setPatchPath(patchFile);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UpdateManagerUI.this.validateTheFile(patchFile, true);
                }
            }, "BrowseThread").start();
        }
    }
    
    class PatchPathAction extends KeyAdapter implements Serializable
    {
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            final String patchFile = UpdateManagerUI.this.patchPath.getText().trim();
            if (patchFile.equals("")) {
                UpdateManagerUI.this.readmeButton.setEnabled(false);
                UpdateManagerUI.this.previewButton.setEnabled(false);
                UpdateManagerUI.this.installButton.setEnabled(false);
            }
            else if (keyEvent.getKeyCode() == 10) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateManagerUI.this.validateTheFile(patchFile, true);
                    }
                }, "EnterBrowseThread").start();
            }
            else if (keyEvent.getKeyCode() == 9) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateManagerUI.this.validateTheFile(patchFile, true);
                    }
                }, "TabBrowseThread").start();
            }
            else {
                UpdateManagerUI.this.readmeButton.setEnabled(false);
                UpdateManagerUI.this.previewButton.setEnabled(false);
                UpdateManagerUI.this.installButton.setEnabled(false);
            }
        }
    }
    
    class ReadMeButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UpdateManagerUI.this.readmeActionPerformed();
                }
            }, "MainReadmeThread").start();
        }
    }
    
    class diffButton_diffButton_conn implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent arg0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UpdateManagerUI.this.diffViewerActionPerformed();
                }
            }, "DiffViewerThread").start();
        }
    }
    
    class InstallButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            UpdateManagerUI.this.setCursor(new Cursor(3));
            UpdateManagerUI.this.installActionPerformed(UpdateManagerUI.this.patchPath.getText().trim());
            UpdateManagerUI.this.setCursor(new Cursor(0));
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
            UpdateManagerUI.this.innerCheckBoxEnabled = true;
            if (this.comp.isSelected()) {
                UpdateManagerUI.this.checkBoxCount++;
            }
            else if (UpdateManagerUI.this.checkBoxCount > 0) {
                UpdateManagerUI.this.checkBoxCount--;
            }
            final ContextScreen conScreen = (ContextScreen)UpdateManagerUI.this.cardPanel.getCard("second");
            if (count == UpdateManagerUI.this.checkBoxCount) {
                conScreen.JCheckBox1.setSelected(true);
            }
            else if (UpdateManagerUI.this.checkBoxCount == 0) {
                conScreen.JCheckBox1.setSelected(false);
            }
            else if (conScreen.JCheckBox1.isSelected()) {
                conScreen.JCheckBox1.setSelected(false);
            }
            conScreen.JCheckBox1.repaint();
        }
    }
    
    class ImportCertificateButtonAction implements ActionListener, Serializable
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            UpdateManagerUI.this.importCertificateActionPerformed();
        }
    }
    
    class UpdateManagerUIWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowOpened(final WindowEvent e) {
            UpdateManagerUI.this.patchPath.requestFocus();
        }
        
        @Override
        public void windowClosing(final WindowEvent evt) {
            System.exit(0);
        }
    }
}
