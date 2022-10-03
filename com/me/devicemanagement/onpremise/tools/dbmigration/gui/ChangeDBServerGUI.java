package com.me.devicemanagement.onpremise.tools.dbmigration.gui;

import com.me.devicemanagement.onpremise.tools.dbmigration.action.DBMigrationAction;
import com.adventnet.mfw.Starter;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.adventnet.mfw.logging.LoggerUtil;
import java.util.Vector;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.PortUnreachableException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.List;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.persistence.fos.FOS;
import com.me.devicemanagement.onpremise.tools.dbmigration.metrack.DBMMETracker;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMDBBackupRestore;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.tools.dbmigration.utils.DBMigrationUtils;
import java.util.HashMap;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.PersistenceInitializer;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.Icon;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.AbstractButton;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import java.io.File;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.UIManager;
import java.util.Locale;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import com.me.devicemanagement.onpremise.tools.dbmigration.action.ChangeDBServer;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class ChangeDBServerGUI extends JFrame
{
    public static String masterkeyPassword;
    public static String masterKey;
    private static Logger logger;
    BackupRestoreUtil util;
    private final String productName;
    private static ChangeDBServerGUI changeDBServerGUIObject;
    private static String[] args;
    private static boolean isMigrationStarted;
    public static boolean enableSecureConnection;
    private static boolean isWindowLoaded;
    public static int dbCreationSuccess;
    public static int dbAlreadyExists;
    public static int oldDatabaseFileExists;
    public static final String REMOTE_DATABASE_CONFIGURATIONS = "remote_database_configurations";
    private ChangeDBServer objChangeDBServer;
    private JComboBox serverType;
    private JComboBox sqlinstance;
    private JLabel sqlinstancelabel;
    private JLabel serverTypeLabel;
    private JLabel addressLabel;
    private JLabel authtypelabel;
    private JLabel dblabel;
    private JLabel domainlabel;
    private JLabel portlabel;
    private JLabel userLabel;
    private JLabel pwdlabel;
    private JPasswordField password;
    private JTextField serverAddress;
    private JTextField database;
    private JRadioButton winauthtype;
    private JRadioButton sqlauthtype;
    private ButtonGroup radioButtonGroup;
    private JCheckBox ntlmCheckbox;
    private JTextField domainName;
    private JTextField userName;
    private JTextField port;
    private JToggleButton cancelButton;
    private JToggleButton testButton;
    private JToggleButton saveButton;
    private JButton refreshButton;
    private JPanel jPanel1;
    private boolean dbNameEditable;
    private JCheckBox migrateCheckbox;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private boolean portEditable;
    
    public static ChangeDBServerGUI getInstance() {
        if (ChangeDBServerGUI.changeDBServerGUIObject == null) {
            ChangeDBServerGUI.changeDBServerGUIObject = new ChangeDBServerGUI();
        }
        return ChangeDBServerGUI.changeDBServerGUIObject;
    }
    
    public ChangeDBServerGUI() {
        this.util = new BackupRestoreUtil();
        this.productName = BackupRestoreUtil.getString("desktopcentral.common.product.name", new Object[] { this.util.getProductName() }, (Locale)null);
        this.radioButtonGroup = new ButtonGroup();
        this.dbNameEditable = false;
        this.portEditable = false;
        try {
            this.objChangeDBServer = ChangeDBServer.getInstance();
            this.jPanel1 = new JPanel();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.initComponents(ChangeDBServerGUI.args);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent we) {
                    if (!ChangeDBServerGUI.isMigrationStarted) {
                        ChangeDBServerGUI.this.objChangeDBServer.closeWindow(0);
                    }
                }
            });
            this.setLocationRelativeTo(null);
            this.getContentPane().setBackground(new Color(236, 233, 216));
            this.setIconImage(new ImageIcon(System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "ChangeDBServer" + File.separator + "dc_logo.png").getImage());
            this.setResizable(false);
        }
        catch (final Exception ex) {
            ChangeDBServerGUI.logger.log(Level.WARNING, "Exception in ChangeDBServerGUI constructor : ", ex);
        }
    }
    
    private void initComponents(final String[] args) {
        ChangeDBServerGUI.logger.log(Level.INFO, "initComponents Method called ");
        ChangeDBServerGUI.isWindowLoaded = false;
        final String homeDir = System.getProperty("server.home");
        this.serverTypeLabel = new JLabel();
        this.serverType = new JComboBox();
        this.sqlinstance = new JComboBox();
        this.refreshButton = new JButton();
        this.sqlinstancelabel = new JLabel();
        this.addressLabel = new JLabel();
        this.serverAddress = new JTextField();
        this.port = new JTextField();
        this.authtypelabel = new JLabel();
        this.dblabel = new JLabel();
        this.domainlabel = new JLabel();
        this.portlabel = new JLabel();
        this.winauthtype = new JRadioButton();
        this.sqlauthtype = new JRadioButton();
        this.database = new JTextField();
        this.domainName = new JTextField();
        this.userLabel = new JLabel();
        this.userName = new JTextField();
        this.pwdlabel = new JLabel();
        this.password = new JPasswordField();
        this.cancelButton = new JToggleButton();
        this.testButton = new JToggleButton();
        this.saveButton = new JToggleButton();
        this.ntlmCheckbox = new JCheckBox(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.ntlm_auth", (Locale)null));
        this.migrateCheckbox = new JCheckBox("<html> <div style = \"text-align: center\"> " + BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.migrate_db", (Locale)null) + "</html> ");
        this.progressLabel = new JLabel();
        this.add(this.progressBar = new JProgressBar(0, 100));
        this.progressBar.setForeground(new Color(2, 181, 0));
        this.progressBar.setVisible(false);
        for (String cmdArgs : args) {
            cmdArgs = cmdArgs.trim();
            if ("DBNameEditable=true".equalsIgnoreCase(cmdArgs)) {
                this.dbNameEditable = true;
            }
            else if ("PortEditable=true".equalsIgnoreCase(cmdArgs)) {
                this.portEditable = true;
            }
            else if (cmdArgs.contains("masterkey")) {
                final String[] strArray = cmdArgs.split("=");
                ChangeDBServerGUI.masterkeyPassword = strArray[1];
            }
            else if ("enableSecureConnection=true".equalsIgnoreCase(cmdArgs)) {
                ChangeDBServerGUI.enableSecureConnection = true;
            }
        }
        this.radioButtonGroup.add(this.winauthtype);
        this.radioButtonGroup.add(this.sqlauthtype);
        this.setDefaultCloseOperation(2);
        this.setTitle(this.productName);
        this.serverTypeLabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.db_type", (Locale)null));
        this.add(this.serverTypeLabel);
        this.serverTypeLabel.setBounds(30, 50, 100, 20);
        final String[] dbList = this.getDBList();
        this.serverType.setModel(new DefaultComboBoxModel(dbList));
        this.add(this.serverType);
        this.serverType.setBounds(170, 50, 180, 24);
        this.serverType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ChangeDBServerGUI.this.changeWindow();
            }
        });
        HashMap hash = null;
        String stype = null;
        try {
            stype = this.objChangeDBServer.getActiveDBName();
            if ("postgres".equals(stype) || "mssql".equals(stype)) {
                this.sqlinstance.setVisible(true);
                this.refreshButton.setVisible(true);
                this.sqlinstancelabel.setVisible(true);
                hash = this.objChangeDBServer.getDBDetails(stype);
                stype = "SQL Server";
            }
            else if ("mysql".equals(stype)) {
                stype = "Postgres";
                this.sqlinstance.setVisible(false);
                this.refreshButton.setVisible(false);
                this.sqlinstancelabel.setVisible(false);
                hash = this.objChangeDBServer.getDBPropertiesFromFile(System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_postgres.conf", "postgres");
            }
            this.serverType.setSelectedItem(stype);
        }
        catch (final Exception e) {
            ChangeDBServerGUI.logger.log(Level.INFO, "Exception occured while checking for the SQL instance");
            e.printStackTrace();
        }
        this.addressLabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.host_name", (Locale)null));
        this.add(this.addressLabel);
        this.addressLabel.setBounds(30, 90, 110, 15);
        if ("SQL Server".equals(stype)) {
            this.serverAddress.setText(hash.get("HOST").toString());
        }
        this.add(this.serverAddress);
        this.serverAddress.setBounds(170, 90, 180, 19);
        ChangeDBServerGUI.logger.log(Level.INFO, "The server address is " + this.serverType.getSelectedItem().toString() + " " + this.serverAddress.getText());
        this.serverAddress.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent evt) {
                final String stype = ChangeDBServerGUI.this.serverType.getSelectedItem().toString();
                if ("SQL Server".equals(stype)) {
                    ChangeDBServerGUI.this.sqlinstance.setModel(new DefaultComboBoxModel(ChangeDBServerGUI.this.getNamedInstance(ChangeDBServerGUI.this.serverAddress.getText())));
                    ChangeDBServerGUI.this.parseNamedInstances();
                }
            }
        });
        this.add(this.portlabel);
        this.portlabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.port", (Locale)null));
        this.portlabel.setBounds(30, 120, 110, 15);
        this.add(this.port);
        this.port.setBounds(170, 120, 180, 19);
        this.add(this.sqlinstancelabel);
        this.sqlinstancelabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.available_sql_instances", (Locale)null));
        this.sqlinstancelabel.setBounds(30, 150, 420, 24);
        this.add(this.sqlinstance);
        this.sqlinstance.setBounds(30, 180, 290, 24);
        this.sqlinstance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ChangeDBServerGUI.this.parseNamedInstances();
            }
        });
        final String refreshImageLocation = homeDir + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "ChangeDBServer" + File.separator + "refresh-icon.png";
        this.refreshButton.setToolTipText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.refresh_sql_instances", (Locale)null));
        this.refreshButton.setIcon(new ImageIcon(refreshImageLocation));
        this.add(this.refreshButton);
        this.refreshButton.setBounds(325, 180, 25, 24);
        this.refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent evt) {
                ChangeDBServerGUI.this.sqlinstance.setModel(new DefaultComboBoxModel(ChangeDBServerGUI.this.getNamedInstance(ChangeDBServerGUI.this.serverAddress.getText())));
                ChangeDBServerGUI.this.parseNamedInstances();
            }
        });
        this.refreshButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final int keycode = e.getKeyCode();
                if (keycode == 10 || keycode == 32) {
                    ChangeDBServerGUI.this.sqlinstance.setModel(new DefaultComboBoxModel(ChangeDBServerGUI.this.getNamedInstance(ChangeDBServerGUI.this.serverAddress.getText())));
                    ChangeDBServerGUI.this.parseNamedInstances();
                }
            }
        });
        int inc = 0;
        if ("SQL Server".equals(stype)) {
            inc = 70;
        }
        this.dblabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.db", (Locale)null));
        this.add(this.dblabel);
        this.dblabel.setBounds(30, 150 + inc, 110, 15);
        this.database.setEditable(this.dbNameEditable);
        this.database.setText(hash.get("DATABASE").toString());
        this.add(this.database);
        this.database.setBounds(170, 150 + inc, 180, 19);
        this.authtypelabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.connect_using", (Locale)null));
        this.add(this.authtypelabel);
        this.authtypelabel.setBounds(30, 180 + inc, 110, 15);
        this.winauthtype.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.windows_auth", (Locale)null));
        this.add(this.winauthtype);
        this.winauthtype.setBounds(25, 210 + inc, 170, 15);
        this.sqlauthtype.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.sql_auth", (Locale)null));
        this.add(this.sqlauthtype);
        this.sqlauthtype.setBounds(200, 210 + inc, 180, 15);
        this.add(this.ntlmCheckbox);
        this.ntlmCheckbox.setBounds(25, 240 + inc, 180, 15);
        this.ntlmCheckbox.setSelected(true);
        this.domainlabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.domain_name", (Locale)null));
        this.domainlabel.setBounds(30, 270 + inc, 110, 15);
        this.add(this.domainlabel);
        this.domainName.setText(hash.get("DOMAIN_NAME"));
        this.domainName.setBounds(170, 270 + inc, 180, 19);
        this.add(this.domainName);
        if ("SQL Server".equals(stype)) {
            inc = 160;
        }
        this.winauthtype.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ChangeDBServerGUI.this.domainlabel.setEnabled(true);
                ChangeDBServerGUI.this.domainName.setEnabled(true);
                ChangeDBServerGUI.this.ntlmCheckbox.setSelected(true);
            }
        });
        this.sqlauthtype.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ChangeDBServerGUI.this.domainlabel.setEnabled(false);
                ChangeDBServerGUI.this.domainName.setText("");
                ChangeDBServerGUI.this.domainName.setEnabled(false);
                ChangeDBServerGUI.this.ntlmCheckbox.setSelected(false);
            }
        });
        this.userLabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.user_name", (Locale)null));
        this.add(this.userLabel);
        this.userLabel.setBounds(30, 210 + inc, 110, 15);
        this.add(this.userName);
        if ("SQL Server".equals(stype)) {
            this.userName.setText(hash.get("USER").toString());
        }
        this.userName.setBounds(170, 210 + inc, 180, 19);
        this.pwdlabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.pwd", (Locale)null));
        this.add(this.pwdlabel);
        this.pwdlabel.setBounds(30, 240 + inc, 110, 15);
        this.add(this.password);
        String pword = hash.get("PASSWORD").toString();
        Label_1911: {
            if (pword == null) {
                if (!"".equals(pword)) {
                    break Label_1911;
                }
            }
            try {
                PersistenceInitializer.loadPersistenceConfigurations();
                pword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)pword);
            }
            catch (final Exception ex) {
                ChangeDBServerGUI.logger.log(Level.WARNING, "Exception While decrypting DB password", ex);
            }
        }
        this.password.setText(pword);
        this.password.setBounds(170, 240 + inc, 180, 19);
        this.add(this.migrateCheckbox);
        this.migrateCheckbox.setBounds(25, 270 + inc, 280, 15);
        this.progressLabel.setHorizontalAlignment(0);
        this.add(this.progressLabel);
        this.cancelButton.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.cancel", (Locale)null));
        this.add(this.cancelButton);
        this.cancelButton.setBounds(180, 300 + inc, 80, 25);
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ChangeDBServerGUI.this.objChangeDBServer.closeWindow(0);
            }
        });
        this.testButton.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.test", (Locale)null));
        this.add(this.testButton);
        this.testButton.setBounds(270, 300 + inc, 80, 25);
        this.testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                ChangeDBServerGUI.this.doCheckDBOperation();
            }
        });
        this.saveButton.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.save", (Locale)null));
        this.add(this.saveButton);
        this.saveButton.setBounds(90, 300 + inc, 80, 25);
        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final Thread dbThread = new Thread() {
                    @Override
                    public void run() {
                        ChangeDBServerGUI.this.doSaveDBOperation();
                    }
                };
                dbThread.start();
            }
        });
        this.changeWindow();
        this.add(this.jPanel1);
        this.jPanel1.setBounds(0, 0, 430, 480);
        ChangeDBServerGUI.isWindowLoaded = true;
    }
    
    private void doSaveDBOperation() {
        System.setProperty("dbm_start_time", String.valueOf(System.currentTimeMillis()));
        DBMigrationUtils.checkDBMigrationConfBackUP();
        boolean validation = true;
        final String stype = this.serverType.getSelectedItem().toString();
        ChangeDBServerGUI.logger.log(Level.INFO, "The stype selected  : " + stype);
        final String sname = this.serverAddress.getText();
        String sport = this.port.getText();
        String db = this.database.getText();
        final String domain = this.domainName.getText();
        String uname = this.userName.getText();
        String pwd = this.password.getText();
        final String uPassWord = new String(this.password.getPassword());
        ChangeDBServerGUI.masterKey = DBMigrationUtils.createDynamicMasterKey(uPassWord);
        final String instance_details = (String)this.sqlinstance.getSelectedItem();
        String instanceName = null;
        String sourceDB = this.objChangeDBServer.getActiveDBName();
        boolean isWinAuthType = false;
        boolean isNTLMEnabled = false;
        boolean isMigrationEnabled = false;
        String err = null;
        if ("pgsql".equals(sourceDB)) {
            if (!DBUtil.isRemoteDB()) {
                sourceDB = "bundled postgres";
            }
            else {
                sourceDB = "remote postgres";
            }
            ChangeDBServerGUI.logger.log(Level.INFO, "source database : " + sourceDB);
        }
        ChangeDBServerGUI.logger.log(Level.INFO, "source database : " + sourceDB);
        if ("SQL Server".equals(stype) && instance_details != null) {
            final String[] values = instance_details.split(";");
            ChangeDBServerGUI.logger.log(Level.INFO, "The instance name selected is " + values[1]);
            instanceName = values[1];
            final Object[] authType = this.winauthtype.getSelectedObjects();
            if (authType != null) {
                isWinAuthType = true;
            }
        }
        if (!"Bundled Postgres".equals(stype) && sname.trim().equals("")) {
            err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_valid_address", (Locale)null);
            validation = false;
        }
        else if ("SQL Server".equals(stype) && this.portEditable) {
            if (!StringUtils.isNumeric((CharSequence)sport)) {
                err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_valid_port", (Locale)null);
                validation = false;
            }
        }
        else if ("SQL Server".equals(stype) && !this.portEditable && instance_details == null) {
            err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.rootcause_for_unabletofetch_server", new Object[] { sname }, (Locale)null);
            validation = false;
        }
        else if ("SQL Server".equals(stype) && !this.portEditable && instance_details.split(";").length < 3) {
            err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.unable_to_fetch_sql_port", (Locale)null);
            validation = false;
        }
        else if (!"Bundled Postgres".equals(stype) && db.trim().equals("")) {
            err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_valid_database", (Locale)null);
            validation = false;
        }
        else if (!"Bundled Postgres".equals(stype) && domain.trim().equals("") && isWinAuthType) {
            err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_valid_domain", (Locale)null);
            validation = false;
        }
        else if (!"Bundled Postgres".equals(stype) && uname.trim().equals("")) {
            err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_valid_username", (Locale)null);
            validation = false;
        }
        else if (!"Bundled Postgres".equals(stype)) {
            try {
                Integer.parseInt(sport);
            }
            catch (final Exception e) {
                err = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_valid_serverport", (Locale)null);
                validation = false;
            }
        }
        if (!validation) {
            JOptionPane.showMessageDialog(null, err, BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.close", (Locale)null), 0);
        }
        else {
            if (!this.isDestDBSame()) {
                Label_0879: {
                    if (!"SQL Server".equals(stype)) {
                        if (!"Remote Postgres".equals(stype)) {
                            break Label_0879;
                        }
                    }
                    try {
                        final Properties existingDBprops = this.objChangeDBServer.isDBAlreadyExists(stype, sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, db);
                        if (!existingDBprops.isEmpty()) {
                            final String isDBexist = existingDBprops.getProperty("isDBalreadyExist");
                            if (isDBexist.equalsIgnoreCase("true")) {
                                final String build_number = existingDBprops.getProperty("build_number");
                                final String servername = existingDBprops.getProperty("server_name");
                                try {
                                    if (build_number != null && servername != null) {
                                        JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.db_already_exists_is_running", new Object[] { db, this.util.getValueFromGenProps("displayname"), servername, build_number }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.error", (Locale)null), 0);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.db_already_exists_is_corrupted", new Object[] { db }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.error", (Locale)null), 0);
                                    }
                                }
                                catch (final Exception e2) {
                                    ChangeDBServerGUI.logger.log(Level.WARNING, "Exception while getting existing database name... ", e2);
                                }
                            }
                            return;
                        }
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
                if ("Remote Postgres".equals(stype)) {
                    try {
                        final boolean isCompatibleDBVersion = this.objChangeDBServer.remotPostgreSQLCompatibleVersion(sname, sport, uname, pwd);
                        if (!isCompatibleDBVersion) {
                            JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.db_incompatible", new Object[] { db, this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.error", (Locale)null), 0);
                            return;
                        }
                    }
                    catch (final Exception e) {
                        ChangeDBServerGUI.logger.log(Level.WARNING, "Exception while checking the compatibility of remote postgresql: ", e);
                    }
                }
            }
            else {
                this.migrateCheckbox.setVisible(false);
                this.migrateCheckbox.setSelected(false);
            }
            try {
                isMigrationEnabled = this.migrateCheckbox.isSelected();
                ChangeDBServerGUI.logger.log(Level.INFO, "Is Enable Migrate with Data option : " + isMigrationEnabled);
                final String success = stype + " " + BackupRestoreUtil.getString("desktopcentral.tools.changedb.configured_successfully", (Locale)null);
                this.disableFields();
                final String dbSettingsConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "dbSettings.conf";
                if ("SQL Server".equals(stype)) {
                    ChangeDBServerGUI.logger.log(Level.INFO, "SQL server is selected");
                    try {
                        isNTLMEnabled = this.ntlmCheckbox.isSelected();
                        final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                        final String dbAdapter = "com.adventnet.db.adapter.mssql.DCMssqlDBAdapter";
                        final String sqlGenerator = "com.adventnet.db.adapter.mssql.DCMssqlSQLGenerator";
                        final String exceptionSorter = "com.me.devicemanagement.onpremise.server.sql.MssqlExceptionSorter";
                        final String fileURL = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_mssql.conf";
                        String message = this.objChangeDBServer.createDB("mssql", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, driver, db);
                        final int dbCreateStatus = ChangeDBServer.dbCreationErrorCode;
                        ChangeDBServerGUI.logger.log(Level.INFO, "createDB status :  " + message + " Code : " + dbCreateStatus);
                        if (DMDBBackupRestore.isBakFormatEnabled) {
                            DBMigrationUtils.checkAndWaitForPermissionForBakBeforeMigration(sname, sport, domain, isWinAuthType, isNTLMEnabled, db, driver, uname, pwd);
                        }
                        if (dbCreateStatus == ChangeDBServerGUI.dbCreationSuccess || dbCreateStatus == ChangeDBServerGUI.dbAlreadyExists) {
                            if (isMigrationEnabled) {
                                this.objChangeDBServer.updateDBConfigFile("mssql", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, null, instanceName, db, driver, dbAdapter, sqlGenerator, exceptionSorter, true, fileURL, false);
                                String running = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.pbm_in_detail", (Locale)null);
                                try {
                                    running = this.objChangeDBServer.isDBServerRunning("mssql", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd);
                                }
                                catch (final Exception e3) {
                                    ChangeDBServerGUI.logger.log(Level.WARNING, "Exception while checking the server running : ", e3);
                                }
                                if (!"yes".equals(running)) {
                                    JOptionPane.showMessageDialog(null, running);
                                    return;
                                }
                                this.setSize(390, 620);
                                try {
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Calling DM migration to MSSQL");
                                    this.callDBMigration("mssql", System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_mssql.conf");
                                }
                                catch (final Exception ex) {
                                    DBMMETracker.getInstance().addDBMTracker(2, sourceDB, "mssql");
                                    this.progressLabel.setVisible(false);
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Problem while migrating database. Going to show migration failure Msg and close UI");
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Exception occurs during migration process ", ex);
                                    JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unable_to_migrate", new Object[] { this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.change_db_failed", (Locale)null), 0);
                                    this.objChangeDBServer.closeWindow(1);
                                }
                                this.objChangeDBServer.writePersistenceConfiguration("mssql", false);
                                this.progressLabel.setVisible(false);
                                this.objChangeDBServer.updateServerStartupTimeInFile("mssql", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, db, true);
                                if (FOS.isEnabled()) {
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Failover server  is enabled..");
                                    final FOS fos = new FOS();
                                    fos.initialize();
                                    final String peerIP = fos.getOtherNode();
                                    if (peerIP != null) {
                                        final String fosConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
                                        final Properties fosProps = FileUtils.readPropertyFile(new File(fosConfFile));
                                        final String remoteInstallationDir = fosProps.getProperty("repl.remoteinstallationDir");
                                        this.util.checkAndExecuteNetusePrefixCommand(peerIP);
                                        ChangeDBServerGUI.logger.log(Level.INFO, "Pushing changes to remote server..");
                                        this.util.pushFile(peerIP, remoteInstallationDir, "conf", "database_params.conf");
                                        this.util.pushFile(peerIP, remoteInstallationDir, "conf", "server.starttime");
                                        this.util.checkAndExecuteNetuseSuffixCommand(peerIP);
                                    }
                                    else {
                                        ChangeDBServerGUI.logger.log(Level.WARNING, "Failover is enabled and Secondary server is not configured (Peer IP is null ) , Since the database is changed, create a new zip file using Clone_Primary_Server.bat");
                                    }
                                }
                            }
                            else {
                                this.objChangeDBServer.updateDBConfigFile("mssql", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, null, instanceName, db, driver, dbAdapter, sqlGenerator, exceptionSorter, false, fileURL, false);
                                if (FOS.isEnabled()) {
                                    ChangeDBServerGUI.logger.log(Level.WARNING, "Failover is enabled and empty database is created, hence manually copy the 'dbmigration.conf' and 'server.starttime' files to failover setup before starting the server");
                                    JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.fos_enabled_empty_db_msg", (Locale)null));
                                }
                            }
                            ChangeDBServerGUI.logger.log(Level.INFO, "DB migration completed successfully");
                            ChangeDBServer.printOneLineLog(Level.INFO, "DB migration completed successfully");
                            final String filePath = System.getProperty("backupfile.path", DBMigrationUtils.BACKUPPATH);
                            if (filePath != null) {
                                DBMigrationUtils.deleteBackUpFile(filePath);
                            }
                            DBMMETracker.getInstance().addDBMTracker(1, sourceDB, "mssql");
                            final String startupMessage = BackupRestoreUtil.getString("desktopcentral.tools.changedb.server_startup_message", (Locale)null);
                            this.progressLabel.setVisible(false);
                            if (dbCreateStatus == ChangeDBServerGUI.dbCreationSuccess) {
                                JOptionPane.showMessageDialog(null, success);
                                JOptionPane.showMessageDialog(null, startupMessage);
                                this.objChangeDBServer.startDCServer();
                            }
                            else {
                                JOptionPane.showMessageDialog(null, success);
                            }
                            ChangeDBServerGUI.logger.log(Level.INFO, "DB migration completed successfully. Going to close the window");
                            this.objChangeDBServer.closeWindow(0);
                        }
                        else {
                            if (dbCreateStatus == ChangeDBServerGUI.oldDatabaseFileExists) {
                                message = "MDF and LDF files from old database have not been deleted. \nNotes : \n" + message;
                            }
                            this.progressLabel.setVisible(false);
                            final String connectionFailedMsg = this.objChangeDBServer.getValue(BackupRestoreUtil.getString("desktopcentral.tools.changedb.connection_failed", (Locale)null), new Object[] { stype });
                            JOptionPane.showMessageDialog(null, connectionFailedMsg, BackupRestoreUtil.getString("desktopcentral.tools.changedb.login_failed", (Locale)null), 0);
                            this.objChangeDBServer.closeWindow(2);
                        }
                    }
                    catch (final Exception ex2) {
                        ChangeDBServer.printOneLineLog(Level.WARNING, "Exception occurred during DB Migration due to :" + ex2.getMessage());
                        ChangeDBServerGUI.logger.log(Level.INFO, "Exception occurs during migration process ", ex2);
                        JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unable_to_migrate", new Object[] { this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.change_db_failed", (Locale)null), 0);
                        this.objChangeDBServer.closeWindow(3);
                    }
                }
                else if ("Mysql".equals(stype)) {
                    ChangeDBServerGUI.logger.log(Level.INFO, "Mysql is selected");
                }
                else if ("Remote Postgres".equals(stype)) {
                    ChangeDBServerGUI.logger.log(Level.INFO, "Remote Postgges is selected");
                    try {
                        this.setSize(390, 410);
                        this.progressLabel.setVisible(true);
                        final String driver = "org.postgresql.Driver";
                        final String dbAdapter = "com.adventnet.db.adapter.postgres.DMPostgresDBAdapter";
                        final String sqlGenerator = "com.adventnet.db.adapter.postgres.DCPostgresSQLGenerator";
                        final String exceptionSorter = "com.adventnet.db.adapter.postgres.PostgresExceptionSorter";
                        final String fileURL = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_remote_postgres.conf";
                        String message = this.objChangeDBServer.createDB("postgres", sname, sport, false, false, domain, uname, pwd, driver, db);
                        final int dbCreateStatus = ChangeDBServer.dbCreationErrorCode;
                        ChangeDBServerGUI.logger.log(Level.INFO, "createDB status :  " + message + " Code : " + dbCreateStatus);
                        if (dbCreateStatus == ChangeDBServerGUI.dbCreationSuccess || dbCreateStatus == ChangeDBServerGUI.dbAlreadyExists) {
                            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "db_migration.conf";
                            final Properties dbProps = this.objChangeDBServer.dbmigrationProperties(confFile);
                            ChangeDBServerGUI.logger.log(Level.INFO, "db props from the db mirgation file : " + dbProps);
                            dbProps.setProperty("start.dest.postgres.server", "false");
                            FileAccessUtil.storeProperties(dbProps, confFile, false);
                            if (isMigrationEnabled) {
                                this.objChangeDBServer.updateDBConfigFile("postgres", sname, sport, false, false, domain, uname, pwd, null, instanceName, db, driver, dbAdapter, sqlGenerator, exceptionSorter, true, fileURL, false);
                                String running2 = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.pbm_in_detail", (Locale)null);
                                try {
                                    running2 = this.objChangeDBServer.isDBServerRunning("postgres", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd);
                                }
                                catch (final Exception e4) {
                                    ChangeDBServerGUI.logger.log(Level.WARNING, "Exceptio while checking the server running : ", e4);
                                }
                                if (!"yes".equals(running2)) {
                                    JOptionPane.showMessageDialog(null, running2);
                                    return;
                                }
                                this.setSize(390, 410);
                                try {
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Calling DM migration to POSTGRES");
                                    this.callDBMigration("postgres", System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_remote_postgres.conf");
                                }
                                catch (final Exception ex3) {
                                    DBMMETracker.getInstance().addDBMTracker(2, sourceDB, "remote postgres");
                                    this.progressLabel.setVisible(false);
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Problem while migrating database. Going to show migration failure Msg and close UI");
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Exception occurs during migration process ", ex3);
                                    JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unable_to_migrate", new Object[] { this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.change_db_failed", (Locale)null), 0);
                                    this.objChangeDBServer.closeWindow(1);
                                }
                                this.objChangeDBServer.writePersistenceConfiguration("postgres", false);
                                this.progressLabel.setVisible(false);
                                this.objChangeDBServer.updateServerStartupTimeInFile("postgres", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, db, true);
                                if (FOS.isEnabled()) {
                                    ChangeDBServerGUI.logger.log(Level.INFO, "Failover server  is enabled..");
                                    final FOS fos2 = new FOS();
                                    fos2.initialize();
                                    final String peerIP2 = fos2.getOtherNode();
                                    if (peerIP2 != null) {
                                        final String fosConfFile2 = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
                                        final Properties fosProps2 = FileUtils.readPropertyFile(new File(fosConfFile2));
                                        final String remoteInstallationDir2 = fosProps2.getProperty("repl.remoteinstallationDir");
                                        this.util.checkAndExecuteNetusePrefixCommand(peerIP2);
                                        ChangeDBServerGUI.logger.log(Level.INFO, "Pushing changes to remote server..");
                                        this.util.pushFile(peerIP2, remoteInstallationDir2, "conf", "database_params.conf");
                                        this.util.pushFile(peerIP2, remoteInstallationDir2, "conf", "server.starttime");
                                        this.util.checkAndExecuteNetuseSuffixCommand(peerIP2);
                                    }
                                    else {
                                        ChangeDBServerGUI.logger.log(Level.WARNING, "Failover is enabled and Secondary server is not configured (Peer IP is null ) , Since the database is changed, create a new zip file using Clone_Primary_Server.bat");
                                    }
                                }
                            }
                            else {
                                this.objChangeDBServer.updateDBConfigFile("postgres", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, null, instanceName, db, driver, dbAdapter, sqlGenerator, exceptionSorter, false, fileURL, false);
                                if (FOS.isEnabled()) {
                                    ChangeDBServerGUI.logger.log(Level.WARNING, "Failover is enabled and empty database is created, hence manually copy the 'dbmigration.conf' and 'server.starttime' files to failover setup before starting the server");
                                    JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.fos_enabled_empty_db_msg", (Locale)null));
                                }
                            }
                            ChangeDBServerGUI.logger.log(Level.INFO, "DB migration completed successfully");
                            ChangeDBServer.printOneLineLog(Level.INFO, "DB migration completed successfully");
                            final String filePath2 = System.getProperty("backupfile.path", DBMigrationUtils.BACKUPPATH);
                            if (filePath2 != null) {
                                DBMigrationUtils.deleteBackUpFile(filePath2);
                            }
                            DBMMETracker.getInstance().addDBMTracker(1, sourceDB, "remote postgres");
                            final String startupMessage2 = BackupRestoreUtil.getString("desktopcentral.tools.changedb.server_startup_message", (Locale)null);
                            this.progressLabel.setVisible(false);
                            if (dbCreateStatus == ChangeDBServerGUI.dbCreationSuccess) {
                                JOptionPane.showMessageDialog(null, success);
                                JOptionPane.showMessageDialog(null, startupMessage2);
                                this.objChangeDBServer.startDCServer();
                            }
                            else {
                                JOptionPane.showMessageDialog(null, success);
                            }
                            ChangeDBServerGUI.logger.log(Level.INFO, "DB migration completed successfully. Going to close the window");
                            this.objChangeDBServer.closeWindow(0);
                        }
                        else {
                            if (dbCreateStatus == ChangeDBServerGUI.oldDatabaseFileExists) {
                                message = "Database already exists in the destination Database server. \nNotes : \n" + message;
                            }
                            this.progressLabel.setVisible(false);
                            final String connectionFailed = this.objChangeDBServer.getValue(BackupRestoreUtil.getString("desktopcentral.tools.changedb.connection_failed", (Locale)null), new Object[] { stype });
                            JOptionPane.showMessageDialog(null, connectionFailed, BackupRestoreUtil.getString("desktopcentral.tools.changedb.login_failed", (Locale)null), 0);
                            this.objChangeDBServer.closeWindow(2);
                        }
                    }
                    catch (final Exception ex2) {
                        DBMMETracker.getInstance().addDBMTracker(2, sourceDB, "remote postgres");
                        ChangeDBServerGUI.logger.log(Level.INFO, "Problem while migrating database. Going to show migration failure Msg and close UI");
                        ChangeDBServerGUI.logger.log(Level.INFO, "Exception occurs during migration process ", ex2);
                        JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unable_to_migrate", new Object[] { this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.change_db_failed", (Locale)null), 0);
                        this.objChangeDBServer.closeWindow(4);
                    }
                }
                else if ("Bundled Postgres".equals(stype)) {
                    ChangeDBServerGUI.logger.log(Level.INFO, "Bundled Postgres is selected");
                    try {
                        this.setSize(390, 280);
                        this.progressLabel.setVisible(true);
                        final String desServer = "postgres";
                        final String host = "localhost";
                        final String driver2 = "org.postgresql.Driver";
                        final String dbAdapter2 = "com.adventnet.db.adapter.postgres.DMPostgresDBAdapter";
                        final String sqlGenerator2 = "com.adventnet.db.adapter.postgres.DCPostgresSQLGenerator";
                        final String exceptionSorter2 = "com.adventnet.db.adapter.postgres.PostgresExceptionSorter";
                        final HashMap bundledDBProps = this.objChangeDBServer.getDBPropertiesFromFile(System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_postgres.conf", "postgres");
                        uname = bundledDBProps.get("USER").toString();
                        pwd = bundledDBProps.get("USER").toString();
                        db = bundledDBProps.get("DATABASE").toString();
                        String fileURL2 = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_bundled_postgres.conf";
                        if (new File(dbSettingsConf).exists()) {
                            final Properties defaultDBProperties = FileAccessUtil.readProperties(dbSettingsConf);
                            if (defaultDBProperties != null && defaultDBProperties.containsKey("PGSQL_DB_PORT")) {
                                sport = defaultDBProperties.getProperty("PGSQL_DB_PORT");
                            }
                        }
                        final String confFile2 = System.getProperty("server.home") + File.separator + "conf" + File.separator + "db_migration.conf";
                        final Properties dbProps2 = this.objChangeDBServer.dbmigrationProperties(confFile2);
                        ChangeDBServerGUI.logger.log(Level.INFO, "db props from the db mirgation file : " + dbProps2);
                        dbProps2.setProperty("start.dest.postgres.server", "true");
                        FileAccessUtil.storeProperties(dbProps2, confFile2, false);
                        if (isMigrationEnabled) {
                            final String dataFolderPath = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data";
                            final String dataFolderRenamePath = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data_" + System.currentTimeMillis();
                            if (new File(dataFolderPath).exists()) {
                                final Boolean isDataFolderRenamed = renameFolder(dataFolderPath, dataFolderRenamePath);
                                if (isDataFolderRenamed) {
                                    executeInitPgsql(System.getProperty("server.home"), true);
                                }
                            }
                            this.setSize(390, 280);
                            this.objChangeDBServer.updateDBConfigFile(desServer, host, sport, false, false, domain, uname, pwd, null, instanceName, db, driver2, dbAdapter2, sqlGenerator2, exceptionSorter2, true, fileURL2, true);
                            try {
                                ChangeDBServerGUI.logger.log(Level.INFO, "Calling DM migration to Bundled POSTGRES");
                                this.callDBMigration("postgres", System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_bundled_postgres.conf");
                            }
                            catch (final Exception e5) {
                                DBMMETracker.getInstance().addDBMTracker(2, sourceDB, "bundled postgres");
                                this.progressLabel.setVisible(false);
                                ChangeDBServerGUI.logger.log(Level.INFO, "Problem while migrating database. Going to show migration failure Msg and close UI");
                                ChangeDBServerGUI.logger.log(Level.INFO, "Exception occurs during migration process ", e5);
                                JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unable_to_migrate", new Object[] { this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.change_db_failed", (Locale)null), 0);
                                this.objChangeDBServer.closeWindow(1);
                            }
                            this.objChangeDBServer.writePersistenceConfiguration("postgres", true);
                            this.progressLabel.setVisible(false);
                            this.objChangeDBServer.updateServerStartupTimeInFile("postgres", sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd, db, true);
                            try {
                                final ChangeDBServer objChangeDBServer = this.objChangeDBServer;
                                ChangeDBServer.addDBPropsIntoFile("superuser_pass=Stonebraker");
                            }
                            catch (final Exception e5) {
                                ChangeDBServerGUI.logger.log(Level.INFO, "Problem while updating the database params file");
                            }
                        }
                        else {
                            final String dataFolderPath = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data";
                            final String dataFolderRenamePath = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data_" + System.currentTimeMillis();
                            fileURL2 = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
                            if (new File(dataFolderPath).exists()) {
                                final Boolean isDataFolderRenamed = renameFolder(dataFolderPath, dataFolderRenamePath);
                                if (isDataFolderRenamed) {
                                    executeInitPgsql(System.getProperty("server.home"), true);
                                }
                            }
                            this.objChangeDBServer.updateDBConfigFile(desServer, host, sport, false, false, domain, uname, pwd, null, instanceName, db, driver2, dbAdapter2, sqlGenerator2, exceptionSorter2, false, fileURL2, true);
                        }
                        this.progressLabel.setVisible(false);
                        JOptionPane.showMessageDialog(null, success);
                        ChangeDBServerGUI.logger.log(Level.INFO, "DB migration completed successfully");
                        final String filePath3 = System.getProperty("backupfile.path", DBMigrationUtils.BACKUPPATH);
                        if (filePath3 != null) {
                            DBMigrationUtils.deleteBackUpFile(filePath3);
                        }
                        if (new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + ".lock").exists()) {
                            try {
                                new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + ".lock").delete();
                            }
                            catch (final Exception e6) {
                                ChangeDBServerGUI.logger.log(Level.SEVERE, ".lock file cannot be delted ", e6);
                            }
                        }
                        if (deleteLockFile()) {
                            this.objChangeDBServer.changeSuperUserPassword();
                        }
                        DBMMETracker.getInstance().addDBMTracker(1, sourceDB, "bundled postgres");
                        final String startupMessage3 = BackupRestoreUtil.getString("desktopcentral.tools.changedb.server_startup_message", (Locale)null);
                        JOptionPane.showMessageDialog(null, startupMessage3);
                        this.objChangeDBServer.startDCServer();
                        this.objChangeDBServer.closeWindow(0);
                    }
                    catch (final Exception ex2) {
                        DBMMETracker.getInstance().addDBMTracker(2, sourceDB, "bundled postgres");
                        ChangeDBServerGUI.logger.log(Level.INFO, "Problem while migrating database. Going to show migration failure Msg and close UI");
                        ChangeDBServerGUI.logger.log(Level.INFO, "Exception occurs during migration process ", ex2);
                        JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unable_to_migrate", new Object[] { this.util.getValueFromGenProps("displayname") }, (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.change_db_failed", (Locale)null), 0);
                        this.objChangeDBServer.closeWindow(4);
                    }
                }
            }
            catch (final Exception ex4) {
                ChangeDBServerGUI.logger.log(Level.INFO, "Exception in doSaveDBOperation method ", ex4);
            }
        }
    }
    
    public static boolean deleteLockFile() {
        final String lockFile = System.getProperty("server.home") + File.separator + "bin" + File.separator + ".lock";
        for (int i = 0; i < 3; ++i) {
            if (!new File(lockFile).exists()) {
                return true;
            }
            new File(lockFile).delete();
        }
        return false;
    }
    
    public static boolean renameFolder(final String oldFolder, final String newFolder) {
        try {
            final File newFile = new File(newFolder);
            newFile.getParentFile().mkdirs();
            Files.move(Paths.get(oldFolder, new String[0]), Paths.get(newFolder, new String[0]), StandardCopyOption.ATOMIC_MOVE);
            ChangeDBServerGUI.logger.log(Level.INFO, oldFolder + " Folder is successfully renamed to " + newFolder);
            return true;
        }
        catch (final Exception e) {
            ChangeDBServerGUI.logger.log(Level.SEVERE, "Exception while renaming the Folder" + e);
            return false;
        }
    }
    
    public static void executeInitPgsql(final String serverHome, final boolean initialize) {
        PrintWriter out = null;
        BufferedReader in = null;
        final String binFolder = serverHome + File.separator + "bin";
        final String initPgsqlBat = binFolder + File.separator + "initPgsql.bat";
        ChangeDBServerGUI.logger.log(Level.INFO, "going to execute command" + initPgsqlBat);
        final File filepath = new File(binFolder);
        final List<String> command = new ArrayList<String>();
        ChangeDBServerGUI.logger.log(Level.INFO, "Going to set privilage for Data Folder - post upgrade process ");
        try {
            command.add("cmd.exe");
            command.add("/c");
            command.add("initPgsql.bat");
            if (initialize) {
                command.add("Stonebraker");
            }
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            processBuilder.directory(filepath);
            ChangeDBServerGUI.logger.log(Level.INFO, "COMMAND : {0}", processBuilder.command());
            final Process process = processBuilder.start();
            final File dbDataPrivilegeFile = new File(serverHome + File.separator + "logs" + File.separator + "data_folder_privilege.txt");
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            out = new PrintWriter(new FileOutputStream(dbDataPrivilegeFile, true));
            out.append("\n***************************************Start of initPgsql.bat Execution ********************************************\n");
            String outputLine = null;
            if (in != null) {
                while ((outputLine = in.readLine()) != null) {
                    out.append(outputLine + "\n");
                }
                out.append("***************************************End of initPgsql.bat Execution ***********************************************");
                out.close();
                ChangeDBServerGUI.logger.log(Level.INFO, "initPgsql.bat command executed successfully. ");
                in.close();
            }
            else {
                ChangeDBServerGUI.logger.log(Level.INFO, "initPgsql.bat command execution failed.");
            }
            ChangeDBServerGUI.logger.log(Level.INFO, "Successfully created the  Data Folder");
        }
        catch (final Exception ex) {
            ChangeDBServerGUI.logger.log(Level.WARNING, "Exception occured while creating the Data Folder ", ex);
        }
    }
    
    public HashMap defaultPostgresValues() {
        final HashMap defaultValues = new HashMap();
        defaultValues.put("driver", "org.postgresql.Driver");
        defaultValues.put("dbAdapter", "com.adventnet.db.adapter.postgres.DMPostgresDBAdapter");
        defaultValues.put("sqlGenerator", "com.adventnet.db.adapter.postgres.DCPostgresSQLGenerator");
        defaultValues.put("exceptionSorter", "com.adventnet.db.adapter.postgres.PostgresExceptionSorter");
        defaultValues.put("uname", "dcuser");
        defaultValues.put("pwd", "dcuser");
        return defaultValues;
    }
    
    private void disableFields() {
        ChangeDBServerGUI.isMigrationStarted = true;
        this.setDefaultCloseOperation(0);
        this.saveButton.setEnabled(false);
        this.saveButton.setSelected(false);
        this.testButton.setSelected(false);
        this.testButton.setEnabled(false);
        this.cancelButton.setEnabled(false);
        this.userName.setEnabled(false);
        this.password.setEnabled(false);
        this.domainName.setEnabled(false);
        this.serverAddress.setEnabled(false);
        this.migrateCheckbox.setEnabled(false);
        this.serverType.setEnabled(false);
        this.winauthtype.setEnabled(false);
        this.sqlauthtype.setEnabled(false);
        this.serverType.setEnabled(false);
        this.port.setEnabled(false);
    }
    
    private void parseNamedInstances() {
        if (this.sqlinstance.getSelectedItem() != null) {
            final String instance_details = this.sqlinstance.getSelectedItem().toString();
            final String[] values = instance_details.split(";");
            if (values.length > 2) {
                if ("localhost".equalsIgnoreCase(this.serverAddress.getText())) {
                    this.serverAddress.setText(values[0]);
                }
                this.port.setText(values[2]);
            }
            else {
                this.port.setText("");
                JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.unable_to_fetch_sql_port", (Locale)null), BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.close", (Locale)null), 0);
            }
        }
    }
    
    private Object[] getNamedInstance(final String hostName) {
        final byte[] bytBuffer = new byte[2048];
        if (hostName != null && !hostName.trim().equals("")) {
            try {
                final DatagramSocket ds = new DatagramSocket();
                ds.setSoTimeout(6000);
                final InetAddress iadd = InetAddress.getByName(hostName);
                ds.connect(iadd, 1434);
                final byte[] msg = { 2 };
                final DatagramPacket dp = new DatagramPacket(msg, msg.length);
                ds.send(dp);
                final DatagramPacket dpr = new DatagramPacket(bytBuffer, bytBuffer.length);
                ds.receive(dpr);
            }
            catch (final PortUnreachableException ure) {
                if (ChangeDBServerGUI.isWindowLoaded && !this.portEditable) {
                    final String msg2 = BackupRestoreUtil.getString("desktopcentral.tools.changedb.rootcause_for_unabletofetch_server", new Object[] { hostName }, (Locale)null);
                    JOptionPane.showMessageDialog(null, msg2);
                }
            }
            catch (final UnknownHostException uhe) {
                ChangeDBServerGUI.logger.log(Level.INFO, "Unknown host : " + hostName);
                JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.unknown_host", new Object[] { hostName }, (Locale)null));
            }
            catch (final SocketTimeoutException ste) {
                JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.socket_timeout", new Object[] { hostName }, (Locale)null));
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        final String instances = new String(bytBuffer);
        final String[] instance = instances.split(";;");
        final int len = instance.length;
        final Vector v = new Vector();
        for (int i = 0; i < len; ++i) {
            if (!instance[i].trim().equals("")) {
                final String[] temp = instance[i].split(";");
                final int l = temp.length;
                String val = "";
                for (int j = 0; j < l; j += 2) {
                    if (temp[j].indexOf("ServerName") >= 0) {
                        val += temp[j + 1].trim();
                    }
                    else if (temp[j].indexOf("InstanceName") >= 0) {
                        val = val + ";" + temp[j + 1].trim();
                    }
                    else if (temp[j].indexOf("tcp") >= 0) {
                        val = val + ";" + temp[j + 1].trim();
                    }
                }
                v.add(val);
            }
        }
        ChangeDBServerGUI.logger.log(Level.INFO, "The vector array is  " + v);
        return v.toArray();
    }
    
    private void doCheckDBOperation() {
        final String stype = this.serverType.getSelectedItem().toString();
        final String sname = this.serverAddress.getText();
        final String sport = this.port.getText();
        final String domain = this.domainName.getText();
        final String uname = this.userName.getText();
        final String pwd = this.password.getText();
        String serverType = "mysql";
        boolean isWinAuthType = false;
        final boolean isNTLMEnabled = this.ntlmCheckbox.isSelected();
        if ("SQL Server".equals(stype)) {
            serverType = "mssql";
            final Object[] authType = this.winauthtype.getSelectedObjects();
            if (authType != null) {
                isWinAuthType = true;
            }
        }
        else if ("Remote Postgres".equals(stype)) {
            serverType = "postgres";
        }
        String running = "no";
        try {
            running = this.objChangeDBServer.isDBServerRunning(serverType, sname, sport, isWinAuthType, isNTLMEnabled, domain, uname, pwd);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if ("yes".equals(running)) {
            JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.connection_established", (Locale)null));
        }
        else if (running.contains("Connection refused") || running.contains("verify") || running.contains("Access denied") || running.contains("Out of range") || running.contains("Unknown Host") || running.contains("required fields")) {
            JOptionPane.showMessageDialog(null, running);
        }
        else {
            JOptionPane.showMessageDialog(null, BackupRestoreUtil.getString("desktopcentral.tools.changedb.cannot_establish_connection", (Locale)null));
        }
    }
    
    private static void initDBMigrationLog() {
        final String logFileName = "DBMigration";
        try {
            LoggerUtil.initLog(logFileName, true);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String[] getDBList() {
        String[] dblist = { "Mysql", "SQL Server", "Bundled Postgres", "Remote Postgres" };
        final String dbName = ChangeDBServer.getInstance().getActiveDBName();
        final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        boolean allowRemotePG = false;
        if (frameworkConfigurations.has("remote_database_configurations") && ((JSONObject)frameworkConfigurations.get("remote_database_configurations")).has("isRemotePostgreSQLApplicable")) {
            allowRemotePG = Boolean.valueOf(String.valueOf(((JSONObject)frameworkConfigurations.get("remote_database_configurations")).get("isRemotePostgreSQLApplicable")));
        }
        if (dbName.equalsIgnoreCase("mssql")) {
            if (allowRemotePG) {
                dblist = new String[] { "SQL Server", "Bundled Postgres", "Remote Postgres" };
            }
            else {
                dblist = new String[] { "SQL Server", "Bundled Postgres" };
            }
        }
        else if (dbName.equalsIgnoreCase("mysql")) {
            dblist = new String[] { "Bundled Postgres", "SQL Server" };
        }
        else if (dbName.equalsIgnoreCase("postgres")) {
            if (allowRemotePG) {
                if (DBUtil.isRemoteDB()) {
                    dblist = new String[] { "Remote Postgres", "SQL Server", "Bundled Postgres" };
                }
                else {
                    dblist = new String[] { "Remote Postgres", "SQL Server" };
                }
            }
            else {
                dblist = new String[] { "SQL Server" };
            }
        }
        return dblist;
    }
    
    public static void main(final String[] argslocal) throws Exception {
        initDBMigrationLog();
        ChangeDBServerGUI.args = argslocal;
        ChangeDBServerGUI.isMigrationStarted = false;
        ChangeDBServerGUI.logger.log(Level.INFO, "==============================");
        ChangeDBServerGUI.logger.log(Level.INFO, "Starting ChangeDBServer window");
        ChangeDBServerGUI.logger.log(Level.INFO, "==============================");
        if (ChangeDBServer.ismigrationRevertFound() && ChangeDBServer.isFileLocked()) {
            ChangeDBServerGUI.logger.log(Level.SEVERE, "migration.lock file exist");
            final String msg = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.previous_fail", new Object[] { new BackupRestoreUtil().getValueFromGenProps("displayname") }, (Locale)null);
            JOptionPane.showMessageDialog(null, msg, BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.alert", (Locale)null), 2);
            System.exit(6);
        }
        if (!Starter.checkShutdownListenerPort()) {
            final String msg = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.cannot_change_db", new Object[] { new BackupRestoreUtil().getValueFromGenProps("displayname") }, (Locale)null);
            JOptionPane.showMessageDialog(null, msg, BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.alert", (Locale)null), 2);
            System.exit(5);
        }
        if (!BackupRestoreUtil.maintenanceCompletedSuccessfully()) {
            final String msg = BackupRestoreUtil.getString("dc.tools.changedb.info.cannot_change_db_maint", new Object[] { new BackupRestoreUtil().getValueFromGenProps("displayname") }, (Locale)null);
            JOptionPane.showMessageDialog(null, msg, BackupRestoreUtil.getString("desktopcentral.tools.changedb.common.alert", (Locale)null), 2);
            System.exit(5);
        }
        ChangeDBServer.printOneLineLog(Level.INFO, "DB Migration Window opened");
        final ChangeDBServerGUI obj = getInstance();
        ChangeDBServer.createMigrateLockFile();
        obj.setLocation(300, 200);
        obj.setVisible(true);
    }
    
    private void callDBMigration(final String dsName, final String dbPropsPath) throws Exception {
        this.showProgressBar();
        Starter.loadSystemProperties();
        Starter.initialize();
        Starter.LoadJars();
        executeInitPgsql(System.getProperty("server.home"), false);
        new DBMigrationAction().migrateDB(dsName, dbPropsPath);
    }
    
    private void showProgressBar() {
        this.progressLabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.progress.start_process", (Locale)null));
        this.progressBar.setIndeterminate(true);
        this.progressLabel.setVisible(true);
        this.progressBar.setVisible(true);
        final String destDBType = this.serverType.getSelectedItem().toString();
        if ("SQL Server".equals(destDBType)) {
            this.progressLabel.setBounds(30, 510, 320, 20);
            this.progressBar.setBounds(30, 540, 320, 15);
        }
        else if ("Remote Postgres".equals(destDBType)) {
            this.progressLabel.setBounds(30, 300, 320, 20);
            this.progressBar.setBounds(30, 330, 320, 15);
        }
        else {
            this.progressLabel.setBounds(30, 190, 320, 20);
            this.progressBar.setBounds(30, 220, 320, 15);
        }
    }
    
    private boolean isDestDBSame() {
        boolean returnValue = false;
        String oldDB = null;
        String newDB = null;
        String oldIP = null;
        String newIP = null;
        final String srcDBConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        if (!this.objChangeDBServer.getActiveDBName().equals("mssql")) {
            return false;
        }
        try {
            final HashMap oldDBHash = this.objChangeDBServer.getDBPropertiesFromFile(srcDBConf, "mssql");
            InetAddress inetAddress = InetAddress.getByName(oldDBHash.get("HOST"));
            oldIP = inetAddress.getHostAddress();
            oldDB = oldIP + ":" + oldDBHash.get("PORT");
            inetAddress = InetAddress.getByName(this.serverAddress.getText());
            newIP = inetAddress.getHostAddress();
            newDB = newIP + ":" + this.port.getText();
            if (oldDB.equalsIgnoreCase(newDB)) {
                returnValue = true;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }
    
    public void showProgress() {
        this.progressBar.setIndeterminate(false);
        this.progressBar.setStringPainted(true);
    }
    
    public void setProgressBarValue(final int value) {
        this.progressBar.setValue(value);
    }
    
    public void setProgressText(final String message) {
        this.progressLabel.setText("<html> <div style = \"text-align: center\"> " + message + "</html>");
    }
    
    private void changeWindow() {
        HashMap hash = null;
        final String stype = this.serverType.getSelectedItem().toString();
        try {
            if ("SQL Server".equals(stype)) {
                this.setSize(390, 570);
                hash = this.objChangeDBServer.getDBDetails("mssql");
                final String domain = hash.get("DOMAIN_NAME");
                boolean domainStatus = false;
                this.domainlabel.setVisible(true);
                this.domainName.setVisible(true);
                this.ntlmCheckbox.setVisible(false);
                this.migrateCheckbox.setVisible(true);
                this.migrateCheckbox.setSelected(true);
                this.port.setVisible(true);
                this.portlabel.setVisible(true);
                this.addressLabel.setVisible(true);
                this.serverAddress.setVisible(true);
                this.userName.setVisible(true);
                this.userLabel.setVisible(true);
                if (domain != null) {
                    this.winauthtype.setSelected(true);
                    this.sqlauthtype.setSelected(false);
                    domainStatus = true;
                    this.ntlmCheckbox.setEnabled(true);
                    this.domainlabel.setEnabled(true);
                    this.domainName.setEnabled(true);
                    if (hash.get("NTLMSetting") != null) {
                        this.ntlmCheckbox.setSelected(true);
                    }
                }
                else {
                    this.winauthtype.setSelected(false);
                    this.sqlauthtype.setSelected(true);
                    this.ntlmCheckbox.setEnabled(false);
                    this.domainlabel.setEnabled(false);
                    this.domainName.setEnabled(false);
                    this.domainName.setText("");
                }
                this.sqlinstance.setVisible(true);
                this.refreshButton.setVisible(true);
                this.sqlinstancelabel.setVisible(true);
                this.authtypelabel.setVisible(true);
                this.winauthtype.setVisible(true);
                this.sqlauthtype.setVisible(true);
                this.progressLabel.setVisible(false);
                this.domainlabel.setEnabled(domainStatus);
                this.domainName.setEnabled(domainStatus);
                this.userName.setEditable(true);
                this.password.setEditable(true);
                this.port.setEditable(this.portEditable);
                this.serverAddress.setEditable(true);
                this.sqlinstance.setModel(new DefaultComboBoxModel(this.getNamedInstance(hash.get("HOST").toString())));
                this.authtypelabel.setLocation((int)this.authtypelabel.getLocation().getX(), (int)this.authtypelabel.getLocation().getY() + 60);
                this.winauthtype.setLocation((int)this.winauthtype.getLocation().getX(), (int)this.winauthtype.getLocation().getY() + 60);
                this.sqlauthtype.setLocation((int)this.sqlauthtype.getLocation().getX(), (int)this.sqlauthtype.getLocation().getY() + 60);
                this.ntlmCheckbox.setLocation((int)this.ntlmCheckbox.getLocation().getX(), (int)this.ntlmCheckbox.getLocation().getY() + 60);
                this.dblabel.setLocation((int)this.dblabel.getLocation().getX(), (int)this.dblabel.getLocation().getY() + 60);
                this.database.setLocation((int)this.database.getLocation().getX(), (int)this.database.getLocation().getY() + 60);
                this.domainlabel.setLocation((int)this.domainlabel.getLocation().getX(), (int)this.domainlabel.getLocation().getY() + 60);
                this.domainName.setLocation((int)this.domainName.getLocation().getX(), (int)this.domainName.getLocation().getY() + 60);
                this.userLabel.setLocation((int)this.userLabel.getLocation().getX(), (int)this.userLabel.getLocation().getY() + 60);
                this.userName.setLocation((int)this.userName.getLocation().getX(), (int)this.userName.getLocation().getY() + 60);
                this.pwdlabel.setLocation((int)this.pwdlabel.getLocation().getX(), (int)this.pwdlabel.getLocation().getY() + 60);
                this.password.setLocation((int)this.password.getLocation().getX(), (int)this.password.getLocation().getY() + 60);
                this.migrateCheckbox.setLocation((int)this.migrateCheckbox.getLocation().getX(), (int)this.migrateCheckbox.getLocation().getY() + 60);
                this.saveButton.setLocation((int)this.saveButton.getLocation().getX(), (int)this.saveButton.getLocation().getY() + 60);
                this.cancelButton.setLocation((int)this.cancelButton.getLocation().getX(), (int)this.cancelButton.getLocation().getY() + 60);
                this.testButton.setLocation((int)this.testButton.getLocation().getX(), (int)this.testButton.getLocation().getY() + 60);
                this.progressLabel.setLocation((int)this.progressLabel.getLocation().getX(), (int)this.progressLabel.getLocation().getY() + 60);
                this.dblabel.setBounds(30, 220, 110, 15);
                this.database.setBounds(170, 220, 180, 19);
                this.authtypelabel.setBounds(30, 250, 110, 15);
                this.winauthtype.setBounds(25, 280, 170, 15);
                this.sqlauthtype.setBounds(200, 280, 180, 15);
                this.ntlmCheckbox.setBounds(30, 310, 180, 15);
                this.domainlabel.setBounds(30, 340, 110, 15);
                this.domainName.setBounds(170, 340, 180, 19);
                this.userLabel.setBounds(30, 370, 110, 15);
                this.userName.setBounds(170, 370, 180, 19);
                this.pwdlabel.setBounds(30, 400, 110, 15);
                this.password.setBounds(170, 400, 180, 19);
                this.migrateCheckbox.setBounds(30, 440, 280, 15);
                this.cancelButton.setBounds(180, 480, 80, 25);
                this.testButton.setBounds(270, 480, 80, 25);
                this.saveButton.setBounds(90, 480, 80, 25);
                this.testButton.setEnabled(true);
                if (ChangeDBServerGUI.isWindowLoaded) {
                    this.serverAddress.setText(hash.get("HOST").toString());
                    this.port.setText(hash.get("PORT").toString());
                    this.database.setText(hash.get("DATABASE").toString());
                    this.domainName.setText(hash.get("DOMAIN_NAME"));
                    this.userName.setText(hash.get("USER").toString());
                    this.parseNamedInstances();
                }
                String pword = hash.get("PASSWORD").toString();
                Label_1370: {
                    if (pword == null) {
                        if (!"".equals(pword)) {
                            break Label_1370;
                        }
                    }
                    try {
                        PersistenceInitializer.loadPersistenceConfigurations();
                        pword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)pword);
                    }
                    catch (final Exception ex) {
                        ChangeDBServerGUI.logger.log(Level.WARNING, "Exception While decrypting DB password ", ex);
                    }
                }
                this.password.setText(pword);
            }
            else if ("Remote Postgres".equals(stype)) {
                this.setSize(390, 370);
                hash = this.objChangeDBServer.getDBPropertiesFromFile(System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_postgres.conf", "postgres");
                this.sqlinstance.setVisible(false);
                this.refreshButton.setVisible(false);
                this.sqlinstancelabel.setVisible(false);
                this.authtypelabel.setVisible(false);
                this.winauthtype.setVisible(false);
                this.sqlauthtype.setVisible(false);
                this.domainlabel.setVisible(false);
                this.domainName.setVisible(false);
                this.ntlmCheckbox.setVisible(false);
                this.migrateCheckbox.setVisible(true);
                this.migrateCheckbox.setSelected(true);
                this.userName.setEditable(true);
                this.password.setEditable(true);
                this.port.setEditable(true);
                this.serverAddress.setEditable(true);
                this.progressLabel.setVisible(false);
                this.port.setVisible(true);
                this.portlabel.setVisible(true);
                this.addressLabel.setVisible(true);
                this.serverAddress.setVisible(true);
                this.userName.setVisible(true);
                this.userLabel.setVisible(true);
                this.serverAddress.setText("");
                this.port.setText("");
                this.database.setText(hash.get("DATABASE").toString());
                this.domainName.setText(hash.get("DOMAIN_NAME"));
                this.userName.setText("");
                this.database.setLocation((int)this.database.getLocation().getX(), (int)this.database.getLocation().getY() - 60);
                this.dblabel.setLocation((int)this.dblabel.getLocation().getX(), (int)this.dblabel.getLocation().getY() - 60);
                this.userLabel.setLocation((int)this.userLabel.getLocation().getX(), (int)this.userLabel.getLocation().getY() - 60);
                this.userName.setLocation((int)this.userName.getLocation().getX(), (int)this.userName.getLocation().getY() - 60);
                this.pwdlabel.setLocation((int)this.pwdlabel.getLocation().getX(), (int)this.pwdlabel.getLocation().getY() - 60);
                this.password.setLocation((int)this.password.getLocation().getX(), (int)this.password.getLocation().getY() - 60);
                this.migrateCheckbox.setLocation((int)this.migrateCheckbox.getLocation().getX(), (int)this.migrateCheckbox.getLocation().getY() - 60);
                this.saveButton.setLocation((int)this.saveButton.getLocation().getX(), (int)this.saveButton.getLocation().getY() - 60);
                this.cancelButton.setLocation((int)this.cancelButton.getLocation().getX(), (int)this.cancelButton.getLocation().getY() - 60);
                this.testButton.setLocation((int)this.testButton.getLocation().getX(), (int)this.testButton.getLocation().getY() - 60);
                this.dblabel.setBounds(30, 150, 110, 15);
                this.database.setBounds(170, 150, 180, 19);
                this.userLabel.setBounds(30, 180, 110, 15);
                this.userName.setBounds(170, 180, 180, 19);
                this.pwdlabel.setBounds(30, 210, 110, 15);
                this.password.setBounds(170, 210, 180, 19);
                this.migrateCheckbox.setBounds(30, 240, 280, 15);
                this.cancelButton.setBounds(180, 270, 80, 25);
                this.testButton.setBounds(270, 270, 80, 25);
                this.saveButton.setBounds(90, 270, 80, 25);
                this.testButton.setEnabled(true);
            }
            else if ("Bundled Postgres".equals(stype)) {
                this.setSize(390, 220);
                hash = this.objChangeDBServer.getDBPropertiesFromFile(System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "database_params_postgres.conf", "postgres");
                this.sqlinstance.setVisible(false);
                this.refreshButton.setVisible(false);
                this.sqlinstancelabel.setVisible(false);
                this.authtypelabel.setVisible(false);
                this.winauthtype.setVisible(false);
                this.sqlauthtype.setVisible(false);
                this.domainlabel.setVisible(false);
                this.domainName.setVisible(false);
                this.addressLabel.setVisible(false);
                this.serverAddress.setVisible(false);
                this.port.setEditable(false);
                this.port.setVisible(false);
                this.portlabel.setVisible(false);
                this.userLabel.setVisible(false);
                this.userName.setVisible(false);
                this.ntlmCheckbox.setVisible(false);
                this.migrateCheckbox.setVisible(true);
                this.migrateCheckbox.setSelected(true);
                this.userName.setEditable(false);
                this.password.setEditable(false);
                this.pwdlabel.setVisible(false);
                this.password.setVisible(false);
                this.serverAddress.setEditable(false);
                this.dblabel.setLocation((int)this.dblabel.getLocation().getX(), (int)this.dblabel.getLocation().getY() - 60);
                this.database.setLocation((int)this.database.getLocation().getX(), (int)this.database.getLocation().getY() - 60);
                this.migrateCheckbox.setLocation((int)this.migrateCheckbox.getLocation().getX(), (int)this.migrateCheckbox.getLocation().getY() - 60);
                this.saveButton.setLocation((int)this.saveButton.getLocation().getX(), (int)this.saveButton.getLocation().getY() - 60);
                this.cancelButton.setLocation((int)this.cancelButton.getLocation().getX(), (int)this.cancelButton.getLocation().getY() - 60);
                this.dblabel.setBounds(30, 90, 110, 15);
                this.database.setBounds(170, 90, 180, 19);
                this.migrateCheckbox.setBounds(30, 120, 280, 15);
                this.cancelButton.setBounds(180, 150, 80, 25);
                this.saveButton.setBounds(90, 150, 80, 25);
            }
        }
        catch (final Exception e) {
            ChangeDBServerGUI.logger.log(Level.INFO, "Exception occoured while changing the combo box of the in the database servers GUI");
            e.printStackTrace();
        }
    }
    
    static {
        ChangeDBServerGUI.logger = Logger.getLogger(ChangeDBServerGUI.class.getName());
        ChangeDBServerGUI.changeDBServerGUIObject = null;
        ChangeDBServerGUI.args = null;
        ChangeDBServerGUI.dbCreationSuccess = 1000;
        ChangeDBServerGUI.dbAlreadyExists = 1801;
        ChangeDBServerGUI.oldDatabaseFileExists = 5170;
    }
}
