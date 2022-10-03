package com.me.devicemanagement.onpremise.tools.backuprestore.gui;

import java.awt.EventQueue;
import java.net.URI;
import java.awt.Desktop;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.CompressUtil;
import javax.swing.BoxLayout;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import java.util.Arrays;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordProvider;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import com.adventnet.persistence.PersistenceUtil;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DCBackupRestoreException;
import javax.swing.SwingWorker;
import java.beans.PropertyChangeEvent;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Dimension;
import javax.swing.LayoutStyle;
import java.awt.Container;
import javax.swing.GroupLayout;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import com.adventnet.mfw.Starter;
import java.util.logging.LogManager;
import java.io.FileInputStream;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.io.File;
import java.util.Locale;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMRestoreAction;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMBackupAction;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.Properties;
import java.util.logging.Logger;
import java.beans.PropertyChangeListener;
import javax.swing.JFrame;

public class DMBackupMain extends JFrame implements PropertyChangeListener
{
    private static Logger logger;
    private static Properties ppmMessageProperties;
    private static final int BACKUP_TAB = 0;
    private static final int RESTORE_TAB = 1;
    private static final int PASSWORD_FULL = 0;
    private static final int PASSWORD_CHECKBOX = 1;
    private static final int PROGRESS_CARD = 2;
    private final String backupString;
    private final String restoreString;
    BackupRestoreUtil util;
    private final String productName;
    Informable informable;
    String serverHome;
    String paramValue1;
    String paramValue2;
    String baseDirectory;
    DMBackupAction backup;
    DMRestoreAction restore;
    boolean canExit;
    int progress;
    int stages;
    long fileCount;
    long totalSize;
    String passwordForBackup;
    private JButton okButton;
    private JLabel backupIntro;
    private JPanel backupLocationPanel;
    private JPanel passwordTextPanel;
    private JPanel backupPanel;
    private ProgressPanel backupProgress;
    private JButton closeButton;
    private JButton destBrowse;
    private JTextField destLocation;
    private JPasswordField password;
    private JPasswordField confpassword;
    private JTextField passwordHintInput;
    private JFileChooser fileChooser;
    private JLabel helpLabel;
    private JLabel resoreIntro;
    private JPanel restoreLocationPanel;
    private JPanel restorePanel;
    private ProgressPanel restoreProgress;
    private JButton sourceBrowse;
    private JTextField sourceLocation;
    private JTabbedPane tabbedPanel;
    private JPasswordField backupPassword;
    private JTextField passwordHint;
    private JLabel restorePWDLabel;
    private JCheckBox enablePWD;
    
    public DMBackupMain() {
        this.backupString = BackupRestoreUtil.getString("desktopcentral.tools.backup.title", null);
        this.restoreString = BackupRestoreUtil.getString("desktopcentral.tools.restore.title", null);
        this.util = new BackupRestoreUtil();
        this.productName = BackupRestoreUtil.getString("desktopcentral.common.product.name", new Object[] { this.util.getProductName() }, null);
        this.initLog();
        try {
            this.serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            this.fileCount = 0L;
            DMBackupMain.logger.log(Level.INFO, "Starting Backup-Restore utility ( through UI ) ");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.initComponents();
            this.canExit = false;
            this.helpLabel.setCursor(Cursor.getPredefinedCursor(12));
            this.setLocationRelativeTo(null);
            this.getContentPane().setBackground(new Color(236, 233, 216));
            this.setIconImage(new ImageIcon(this.getClass().getResource("/com/me/devicemanagement/onpremise/tools/backuprestore/gui/images/dc_logo.png")).getImage());
            this.addTextFieldListener(this.destLocation, this.okButton);
            this.addTextFieldListener(this.sourceLocation, this.okButton);
            if (this.baseDirectory == null) {
                this.baseDirectory = this.serverHome;
            }
        }
        catch (final Exception e) {
            DMBackupMain.logger.log(Level.WARNING, "Exception in DMBackupMain() - constructor : ", e);
        }
    }
    
    public void initLog() {
        InputStream inputStream = null;
        try {
            final String fileName = System.getProperty("backup.logging.config.file");
            if (fileName == null || !new File(fileName).exists()) {
                inputStream = this.getClass().getResourceAsStream("/conf/logging.properties");
            }
            else {
                inputStream = new FileInputStream(fileName);
            }
            LogManager.getLogManager().readConfiguration(inputStream);
            System.setOut((PrintStream)new Starter.SysLogStream(true));
            System.setErr((PrintStream)new Starter.SysLogStream(false));
        }
        catch (final IOException e) {
            DMBackupMain.logger.log(Level.WARNING, "IOException in initializing log manager for DCBackupRestore(UI) ", e);
        }
        catch (final Exception ex) {
            DMBackupMain.logger.log(Level.WARNING, "Exception in initializing log manager for DCBackupRestore(UI) ", ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (final Exception ex2) {}
        }
    }
    
    private void addTextFieldListener(final JTextField textField, final JButton button) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(final DocumentEvent e) {
                this.checkValid();
            }
            
            @Override
            public void removeUpdate(final DocumentEvent e) {
                this.checkValid();
            }
            
            @Override
            public void insertUpdate(final DocumentEvent e) {
                this.checkValid();
            }
            
            void checkValid() {
                final String location = textField.getText();
                if (location.trim().equals("")) {
                    button.setEnabled(false);
                }
                else {
                    button.setEnabled(true);
                }
                textField.setToolTipText(location);
            }
        });
    }
    
    private String getCurrentOperationName() {
        final int currentTab = this.tabbedPanel.getSelectedIndex();
        if (currentTab == 0) {
            return this.backupString;
        }
        if (currentTab == 1) {
            return this.restoreString;
        }
        return null;
    }
    
    private int getLockedTab() {
        int lockedTabIndex = -1;
        for (int totalTabs = this.tabbedPanel.getTabCount(), i = 0; i < totalTabs; ++i) {
            if (!this.tabbedPanel.isEnabledAt(i)) {
                lockedTabIndex = i;
            }
        }
        return lockedTabIndex;
    }
    
    private void initComponents() {
        this.fileChooser = new JFileChooser();
        this.tabbedPanel = new JTabbedPane();
        this.backupPanel = new JPanel();
        this.backupIntro = new JLabel();
        this.backupLocationPanel = new JPanel();
        this.passwordTextPanel = new JPanel();
        this.destLocation = new JTextField();
        this.password = new JPasswordField();
        this.confpassword = new JPasswordField();
        this.passwordHintInput = new JTextField();
        this.destBrowse = new JButton();
        this.okButton = new JButton();
        this.backupProgress = new ProgressPanel();
        this.restorePanel = new JPanel();
        this.resoreIntro = new JLabel();
        this.restoreLocationPanel = new JPanel();
        this.sourceLocation = new JTextField();
        this.sourceBrowse = new JButton();
        this.restoreProgress = new ProgressPanel();
        this.closeButton = new JButton();
        this.helpLabel = new JLabel();
        this.backupPassword = new JPasswordField(15);
        this.passwordHint = new JTextField();
        this.restorePWDLabel = new JLabel();
        this.enablePWD = new JCheckBox(BackupRestoreUtil.getString("desktopcentral.tools.restore.backup_pwd_enable", null));
        final JPanel enterpassword = new JPanel();
        final JLabel enterpasswordText = new JLabel(BackupRestoreUtil.getString("desktopcentral.tools.restore.backup_pwd_enter", null));
        final JLabel reenterpasswordText = new JLabel(BackupRestoreUtil.getString("desktopcentral.tools.restore.backup_pwd_re", null));
        final JLabel passwordHintText = new JLabel(BackupRestoreUtil.getString("desktopcentral.tools.restore.backup_pwd_Hint", null));
        this.setDefaultCloseOperation(0);
        this.setTitle(this.productName);
        this.setResizable(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                DMBackupMain.this.formWindowClosing(evt);
            }
        });
        this.tabbedPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent evt) {
                DMBackupMain.this.tabbedPanelStateChanged(evt);
            }
        });
        this.backupPanel.setBackground(new Color(248, 248, 244));
        this.backupIntro.setText(BackupRestoreUtil.getString("desktopcentral.tools.backup.intro", new Object[] { this.util.getValueFromGenProps("displayname") }, null));
        this.backupLocationPanel.setBackground(new Color(248, 248, 244));
        this.backupLocationPanel.setBorder(BorderFactory.createTitledBorder(BackupRestoreUtil.getString("desktopcentral.tools.backup.select_location", null)));
        this.passwordTextPanel.setBackground(new Color(248, 248, 244));
        this.destLocation.setBackground(new Color(255, 255, 255));
        this.destLocation.setEditable(false);
        this.password.setBackground(new Color(255, 255, 255));
        this.password.setEditable(true);
        this.enablePWD.setBackground(new Color(248, 248, 244));
        this.okButton.setBackground(new Color(32, 169, 248));
        this.okButton.setOpaque(true);
        this.enablePWD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                DMBackupMain.this.pwdShowHide(evt);
            }
        });
        this.confpassword.setBackground(new Color(255, 255, 255));
        this.confpassword.setEditable(true);
        this.destBrowse.setText("...");
        this.destBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                DMBackupMain.this.destBrowseActionPerformed(evt);
            }
        });
        this.okButton.setText(BackupRestoreUtil.getString("desktopcentral.tools.common.ok", null));
        this.okButton.setEnabled(false);
        this.okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                DMBackupMain.this.okButtonActionPerformed(evt);
            }
        });
        enterpassword.setBackground(new Color(248, 248, 244));
        enterpassword.setLayout(new GridLayout(0, 1));
        reenterpasswordText.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        passwordHintText.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        enterpassword.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));
        enterpassword.add(enterpasswordText);
        enterpassword.add(this.password);
        enterpassword.add(reenterpasswordText);
        enterpassword.add(this.confpassword);
        enterpassword.add(passwordHintText);
        enterpassword.add(this.passwordHintInput);
        final GroupLayout passwordTextPanelLayout = new GroupLayout(this.passwordTextPanel);
        this.passwordTextPanel.setLayout(passwordTextPanelLayout);
        passwordTextPanelLayout.setHorizontalGroup(passwordTextPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(passwordTextPanelLayout.createSequentialGroup().addGroup(passwordTextPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.enablePWD, GroupLayout.Alignment.LEADING, -1, 311, 32767).addComponent(enterpassword, GroupLayout.Alignment.LEADING, -1, 311, 32767)).addContainerGap()));
        passwordTextPanelLayout.setVerticalGroup(passwordTextPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(passwordTextPanelLayout.createSequentialGroup().addComponent(this.enablePWD, -2, -1, -2).addComponent(enterpassword, -2, -1, -2).addContainerGap()));
        final GroupLayout backupLocationPanelLayout = new GroupLayout(this.backupLocationPanel);
        this.backupLocationPanel.setLayout(backupLocationPanelLayout);
        backupLocationPanelLayout.setHorizontalGroup(backupLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(backupLocationPanelLayout.createSequentialGroup().addContainerGap().addGroup(backupLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, backupLocationPanelLayout.createSequentialGroup().addComponent(this.destLocation, -1, 258, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.destBrowse, -2, 21, -2)))));
        backupLocationPanelLayout.setVerticalGroup(backupLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(backupLocationPanelLayout.createSequentialGroup().addGap(11, 11, 11).addGroup(backupLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.destLocation, -2, -1, -2).addComponent(this.destBrowse)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, 32767)));
        this.repaintBackupPanel(1);
        this.tabbedPanel.addTab(this.backupString, this.backupPanel);
        this.restorePanel.setBackground(new Color(248, 248, 244));
        this.resoreIntro.setText(BackupRestoreUtil.getString("desktopcentral.tools.restore.intro", new Object[] { this.util.getValueFromGenProps("displayname") }, null));
        this.restoreLocationPanel.setBackground(new Color(248, 248, 244));
        this.restoreLocationPanel.setBorder(BorderFactory.createTitledBorder(BackupRestoreUtil.getString("desktopcentral.tools.restore.select_location", null)));
        this.sourceLocation.setBackground(new Color(255, 255, 255));
        this.sourceLocation.setEditable(false);
        this.sourceBrowse.setText("...");
        this.sourceBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                DMBackupMain.this.sourceBrowseActionPerformed(evt);
            }
        });
        final GroupLayout restoreLocationPanelLayout = new GroupLayout(this.restoreLocationPanel);
        this.restoreLocationPanel.setLayout(restoreLocationPanelLayout);
        restoreLocationPanelLayout.setHorizontalGroup(restoreLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(restoreLocationPanelLayout.createSequentialGroup().addContainerGap().addGroup(restoreLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, restoreLocationPanelLayout.createSequentialGroup().addComponent(this.sourceLocation, -1, 258, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.sourceBrowse, -2, 21, -2)))));
        restoreLocationPanelLayout.setVerticalGroup(restoreLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, restoreLocationPanelLayout.createSequentialGroup().addContainerGap(-1, 32767).addGroup(restoreLocationPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.sourceLocation, -2, -1, -2).addComponent(this.sourceBrowse)).addGap(11, 11, 11)));
        final GroupLayout restorePanelLayout = new GroupLayout(this.restorePanel);
        this.restorePanel.setLayout(restorePanelLayout);
        restorePanelLayout.setHorizontalGroup(restorePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, restorePanelLayout.createSequentialGroup().addContainerGap().addGroup(restorePanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.restoreProgress, GroupLayout.Alignment.LEADING, -1, 311, 32767).addComponent(this.resoreIntro, GroupLayout.Alignment.LEADING, -1, 311, 32767).addComponent(this.restoreLocationPanel, GroupLayout.Alignment.LEADING, -1, -1, 32767)).addContainerGap()));
        restorePanelLayout.setVerticalGroup(restorePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(restorePanelLayout.createSequentialGroup().addContainerGap().addComponent(this.resoreIntro).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.restoreLocationPanel, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 22, 32767).addComponent(this.restoreProgress, -2, -1, -2).addContainerGap()));
        this.tabbedPanel.addTab(this.restoreString, this.restorePanel);
        this.closeButton.setText(BackupRestoreUtil.getString("desktopcentral.tools.common.close", null));
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                DMBackupMain.this.closeButtonActionPerformed(evt);
            }
        });
        this.helpLabel.setForeground(new Color(0, 51, 153));
        this.helpLabel.setHorizontalAlignment(11);
        this.helpLabel.setText("<html><b>  " + BackupRestoreUtil.getString("desktopcentral.tools.common.learn_more", null) + "</b></html>");
        this.helpLabel.setMaximumSize(new Dimension(32, 14));
        this.helpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent evt) {
                DMBackupMain.this.helpLabelMouseClicked(evt);
            }
        });
        final GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.tabbedPanel, GroupLayout.Alignment.LEADING, -1, 336, 32767).addGroup(layout.createSequentialGroup().addComponent(this.helpLabel, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 188, 32767).addComponent(this.okButton, -2, 82, -2).addGap(10).addComponent(this.closeButton, -2, 82, -2))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(this.tabbedPanel, -1, 361, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.closeButton).addComponent(this.okButton).addComponent(this.helpLabel, -2, -1, -2)).addContainerGap()));
        this.pack();
    }
    
    private void closeButtonActionPerformed(final ActionEvent evt) {
        this.closeApplication();
    }
    
    private void pwdShowHide(final ActionEvent evt) {
        try {
            if (this.enablePWD.isSelected()) {
                this.repaintBackupPanel(0);
            }
            else {
                this.repaintBackupPanel(1);
            }
        }
        catch (final Exception e) {
            DMBackupMain.logger.log(Level.SEVERE, "error in pwdshowhide", e);
        }
    }
    
    public void closeApplication() {
        final int lockedTab = this.getLockedTab();
        if (lockedTab == -1) {
            this.disposeWindow();
        }
        else {
            JOptionPane.showMessageDialog(this, BackupRestoreUtil.getString("desktopcentral.tools.common.application_cannot_be_closed", this.getCurrentOperationName(), null));
        }
    }
    
    public boolean confirmCancel() {
        boolean confirm = false;
        final String message = BackupRestoreUtil.getString("desktopcentral.tools.common.are_you_sure_to_cancel", this.getCurrentOperationName(), null);
        final int option = JOptionPane.showOptionDialog(this, message, this.productName, 0, 2, null, null, null);
        if (option == 0) {
            confirm = true;
        }
        return confirm;
    }
    
    public void disposeWindow() {
        DMBackupMain.logger.log(Level.INFO, "Quitting application.");
        this.dispose();
        System.exit(0);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String eventName = event.getPropertyName();
        final Object oldValue = event.getOldValue();
        final Object newValue = event.getNewValue();
        final int index = this.tabbedPanel.getSelectedIndex();
        final boolean isBackupPanel = index == 0;
        final ProgressPanel panel = isBackupPanel ? this.backupProgress : this.restoreProgress;
        final int otherTab = isBackupPanel ? 1 : 0;
        if (eventName.equalsIgnoreCase("base_directory")) {
            this.baseDirectory = newValue.toString();
        }
        if (eventName.equalsIgnoreCase("result_param")) {
            this.paramValue1 = oldValue.toString();
            this.paramValue2 = newValue.toString();
        }
        if (eventName.equalsIgnoreCase("file_count")) {
            this.fileCount = Long.parseLong(newValue.toString());
        }
        if (eventName.equalsIgnoreCase("progress")) {
            panel.setProgress(100);
        }
        if (eventName.equalsIgnoreCase("status")) {
            panel.getProgressBar().setIndeterminate((boolean)oldValue);
            final String key = this.getStatusKey(Integer.parseInt(newValue.toString()));
            if (key != null) {
                panel.setStatusMessage(BackupRestoreUtil.getString(key, null));
            }
        }
        if (eventName.equalsIgnoreCase("total_count")) {
            this.totalSize = Long.parseLong(newValue.toString());
            this.fileCount = 0L;
            panel.setProgress(0);
        }
        if (eventName.equalsIgnoreCase("state") && newValue == SwingWorker.StateValue.STARTED) {
            this.tabbedPanel.setEnabledAt(otherTab, false);
        }
        if (eventName.equalsIgnoreCase("result")) {
            this.tabbedPanel.setEnabledAt(otherTab, true);
            final boolean success = (boolean)oldValue;
            final String operationName = isBackupPanel ? this.backupString : this.restoreString;
            this.refreshUI(index, panel);
            if (success) {
                DMBackupMain.logger.log(Level.INFO, "COMPLETED SUCCESSFULLY.");
                this.repaintBackupPanel(1);
                this.enablePWD.setSelected(false);
                this.pwdShowHide(null);
                this.okButton.setEnabled(true);
                this.showSuccessDialog(operationName);
            }
            else {
                final DCBackupRestoreException error = (DCBackupRestoreException)newValue;
                final int errorCode = error.getErrorCode();
                DMBackupMain.logger.log(Level.INFO, "OPERATION NOT COMPLETED");
                final String errorMessage = error.getErrorMessage();
                final String errorDetail = error.getErrorDetail();
                boolean includeContactSupport = false;
                switch (errorCode) {
                    case -13:
                    case -12:
                    case -9:
                    case -8:
                    case -6:
                    case -3:
                    case -2: {
                        includeContactSupport = true;
                        break;
                    }
                }
                this.showErrorDialog(errorMessage, errorDetail, includeContactSupport);
                this.canExit = true;
            }
        }
    }
    
    public void refreshUI(final int currentTab, final ProgressPanel progressPanel) {
        if (currentTab == 0) {
            this.enablePanel(this.backupLocationPanel, true);
            this.enablePanel(this.passwordTextPanel, true);
            this.tabbedPanel.setEnabledAt(1, true);
        }
        if (currentTab == 1) {
            this.enablePanel(this.restoreLocationPanel, true);
            this.tabbedPanel.setEnabledAt(0, true);
        }
        this.clearTab(currentTab);
        this.fileCount = 0L;
        progressPanel.resetComponents();
        progressPanel.showCard("emptyCard");
    }
    
    public void showSuccessDialog(final String operation) {
        JOptionPane.showMessageDialog(this.tabbedPanel, BackupRestoreUtil.getString("desktopcentral.tools.common.operation_complete", operation, null));
    }
    
    public void continueProgress(final ProgressPanel panel) {
        final int nextProgress = this.progress + 100 / this.stages;
        final Runnable showProgress = new Runnable() {
            @Override
            public void run() {
                while (DMBackupMain.this.progress < nextProgress) {
                    try {
                        DMBackupMain.logger.log(Level.INFO, "Progress : " + DMBackupMain.this.progress);
                        panel.setProgress(++DMBackupMain.this.progress);
                        Thread.sleep(2000L);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        };
        new Thread(showProgress).start();
    }
    
    public void setProgress(final ProgressPanel panel) {
        int i = panel.getProgress();
        try {
            while (i <= this.progress) {
                DMBackupMain.logger.log(Level.INFO, "Progress : " + this.progress);
                panel.setProgress(++i);
                Thread.sleep(1000L);
            }
        }
        catch (final InterruptedException e) {
            DMBackupMain.logger.log(Level.WARNING, "Progress Setter interrupted . ", e);
        }
    }
    
    public String getStatusKey(final int status) {
        String key = null;
        switch (status) {
            case 11: {
                key = "desktopcentral.tools.common.inprogress.cancelling";
                break;
            }
            case 5: {
                key = "desktopcentral.tools.common.inprogress.cleaning_up";
                break;
            }
            case 4: {
                key = "desktopcentral.tools.common.inprogress.compressing";
                break;
            }
            case 2: {
                key = "desktopcentral.tools.common.inprogress.copying";
                break;
            }
            case 6: {
                key = "desktopcentral.tools.common.inprogress.copying_current_config";
                break;
            }
            case 3: {
                key = "desktopcentral.tools.common.inprogress.dumping";
                break;
            }
            case 7: {
                key = "desktopcentral.tools.common.inprogress.extracting";
                break;
            }
            case 9: {
                key = "desktopcentral.tools.common.inprogress.restored_prev_config";
                break;
            }
            case 8: {
                key = "desktopcentral.tools.common.inprogress.restoring";
                break;
            }
            case 10: {
                key = "desktopcentral.tools.common.inprogress.reverting";
                break;
            }
            case 12: {
                key = "desktopcentral.tools.common.inprogress.estimating";
                break;
            }
            case 14: {
                key = "desktopcentral.tools.common.inprogress.file_backup_in_progress";
                break;
            }
            case 15: {
                key = "desktopcentral.tools.common.inprogress.db_backup_in_progress";
                break;
            }
            case 16: {
                key = "desktopcentral.tools.common.inprogress.running_precheck";
                break;
            }
            case 17: {
                key = "desktopcentral.tools.common.inprogress.file_restore_in_progress";
                break;
            }
            case 18: {
                key = "desktopcentral.tools.common.inprogress.db_restore_in_progress";
                break;
            }
            case 19: {
                key = "dc.backuprestore.redis_backup_in_progress";
                break;
            }
            case 20: {
                key = "dc.backuprestore.redis_restore_in_progress";
                break;
            }
        }
        return key;
    }
    
    public void showErrorDialog(final String errorMessage, final String errorDetail, final boolean showContactSupport) {
        final String dialogTitle = this.productName + " - " + BackupRestoreUtil.getString("desktopcentral.tools.common.warning_message.title", null);
        final String heading = "<html><h4>" + errorMessage + "</h4></html>";
        final StyledDocument document = new DefaultStyledDocument();
        try {
            document.insertString(document.getLength(), errorDetail, null);
            if (showContactSupport) {
                document.insertString(document.getLength(), "\n\n" + BackupRestoreUtil.getString("desktopcentral.tools.common.contact_support.header", null) + " ", null);
                final StyleContext context = new StyleContext();
                final Style style = context.getStyle("default");
                StyleConstants.setForeground(style, Color.blue);
                StyleConstants.setUnderline(style, true);
                document.insertString(document.getLength(), BackupRestoreUtil.getString(this.util.getValueFromGenProps("supportmailid"), null), style);
                document.insertString(document.getLength(), " " + BackupRestoreUtil.getString("desktopcentral.tools.common.contact_support.tail", null), null);
            }
        }
        catch (final Exception e) {
            DMBackupMain.logger.log(Level.WARNING, "Error in displaying Warning Message ", e);
        }
        final Color optionPaneBG = (ColorUIResource)UIManager.get("OptionPane.background");
        final Color errorPaneBG = new Color(optionPaneBG.getRGB());
        final JTextPane errorDetailPane = new JTextPane(document);
        errorDetailPane.setBackground(errorPaneBG);
        errorDetailPane.setEditable(false);
        final Object[] message = { heading, errorDetailPane };
        JOptionPane.showMessageDialog(this.tabbedPanel, message, dialogTitle, 2);
        final int index = this.tabbedPanel.getSelectedIndex();
        final boolean isBackupPanel = index == 0;
        if (isBackupPanel) {
            this.repaintBackupPanel(0);
        }
    }
    
    private void repaintBackupPanel(final int type) {
        this.backupPanel.removeAll();
        this.backupPanel.revalidate();
        this.backupPanel.repaint();
        if (type == 0) {
            final GroupLayout backupPanelLayout = new GroupLayout(this.backupPanel);
            this.backupPanel.setLayout(backupPanelLayout);
            backupPanelLayout.setHorizontalGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, backupPanelLayout.createSequentialGroup().addContainerGap().addGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.backupLocationPanel, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.backupIntro, GroupLayout.Alignment.LEADING, -2, 280, -2).addComponent(this.enablePWD, GroupLayout.Alignment.LEADING, -2, 280, -2).addComponent(this.passwordTextPanel, GroupLayout.Alignment.LEADING, -1, -1, 32767)).addContainerGap()));
            backupPanelLayout.setVerticalGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(backupPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.backupIntro).addGap(11, 11, 11).addComponent(this.backupLocationPanel, -2, -1, -2).addGap(11, 11, 11).addComponent(this.enablePWD, -2, -1, -2).addComponent(this.passwordTextPanel, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 22, 32767).addContainerGap()));
        }
        else if (type == 1) {
            final GroupLayout backupPanelLayout = new GroupLayout(this.backupPanel);
            this.backupPanel.setLayout(backupPanelLayout);
            backupPanelLayout.setHorizontalGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, backupPanelLayout.createSequentialGroup().addContainerGap().addGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.backupProgress, GroupLayout.Alignment.LEADING, -1, 311, 32767).addComponent(this.backupLocationPanel, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.enablePWD, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.backupIntro, GroupLayout.Alignment.LEADING, -2, 280, -2)).addContainerGap()));
            backupPanelLayout.setVerticalGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(backupPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.backupIntro).addGap(11, 11, 11).addComponent(this.backupLocationPanel, -2, -1, -2).addGap(11, 11, 11).addComponent(this.enablePWD, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 22, 32767).addComponent(this.backupProgress, -2, -1, -2).addContainerGap()));
        }
        else {
            final GroupLayout backupPanelLayout = new GroupLayout(this.backupPanel);
            this.backupPanel.setLayout(backupPanelLayout);
            backupPanelLayout.setHorizontalGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, backupPanelLayout.createSequentialGroup().addContainerGap().addGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.backupProgress, GroupLayout.Alignment.LEADING, -1, 311, 32767).addComponent(this.backupLocationPanel, GroupLayout.Alignment.LEADING, -1, -1, 32767).addComponent(this.backupIntro, GroupLayout.Alignment.LEADING, -2, 280, -2)).addContainerGap()));
            backupPanelLayout.setVerticalGroup(backupPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(backupPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.backupIntro).addGap(11, 11, 11).addComponent(this.backupLocationPanel, -2, -1, -2).addGap(11, 11, 11).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 22, 32767).addComponent(this.backupProgress, -2, -1, -2).addContainerGap()));
        }
    }
    
    private void okButtonActionPerformed(final ActionEvent evt) {
        final int index = this.tabbedPanel.getSelectedIndex();
        if (index == 0) {
            this.passwordForBackup = PersistenceUtil.generateRandomPassword();
            this.backupButtonActionPerformed();
        }
        else {
            this.restoreButtonActionPerformed();
        }
    }
    
    private void backupButtonActionPerformed() {
        DMBackupMain.logger.log(Level.INFO, "=========================");
        DMBackupMain.logger.log(Level.INFO, "Backup operation invoked.");
        DMBackupMain.logger.log(Level.INFO, "=========================");
        this.canExit = false;
        if (this.canProceed() && this.validPassword()) {
            this.enablePanel(this.backupLocationPanel, false);
            this.showProgressPanel(this.backupProgress);
            (this.backup = new DMBackupAction(this.destLocation.getText(), this.informable, true)).addPropertyChangeListener(this);
            if (this.enablePWD.isSelected()) {
                DMBackupPasswordHandler.getInstance().setEncryptionType(2);
            }
            else {
                DMBackupPasswordHandler.getInstance().setEncryptionType(1);
            }
            DMBackupPasswordHandler.getInstance().setPasswordProvider(new DMBackupPasswordProvider() {
                private String defaultPassword = null;
                
                @Override
                public String getPassword() {
                    if (DMBackupPasswordHandler.getInstance().getEncryptionType() == 2) {
                        return new String(DMBackupMain.this.password.getPassword());
                    }
                    if (this.defaultPassword == null) {
                        this.defaultPassword = PersistenceUtil.generateRandomPassword();
                    }
                    return this.defaultPassword;
                }
                
                @Override
                public String getPasswordHint() {
                    if (DMBackupPasswordHandler.getInstance().getEncryptionType() == 2) {
                        return DMBackupMain.this.passwordHintInput.getText();
                    }
                    return null;
                }
            });
            this.repaintBackupPanel(2);
            this.okButton.setEnabled(false);
            this.backup.execute();
        }
    }
    
    private boolean validPassword() {
        if (!this.enablePWD.isSelected()) {
            return true;
        }
        if (!Arrays.equals(this.password.getPassword(), this.confpassword.getPassword())) {
            this.showErrorDialog(BackupRestoreUtil.getString("desktopcentral.tools.restore.pwd_verification", null), BackupRestoreUtil.getString("desktopcentral.tools.backup.password.validationM", null), false);
            return false;
        }
        if (this.password.getPassword().length < 5) {
            this.showErrorDialog(BackupRestoreUtil.getString("desktopcentral.tools.restore.pwd_verification", null), BackupRestoreUtil.getString("desktopcentral.tools.backup.password.validationL", null), false);
            return false;
        }
        return true;
    }
    
    public void cancelBackupOperation() {
        this.backup.cancel(true);
    }
    
    public void enablePanel(final JPanel panel, final boolean enable) {
        final Component[] components2;
        final Component[] components = components2 = panel.getComponents();
        for (final Component component : components2) {
            component.setEnabled(enable);
        }
    }
    
    private void restoreButtonActionPerformed() {
        DMBackupMain.logger.log(Level.INFO, "==========================");
        DMBackupMain.logger.log(Level.INFO, "Restore operation invoked.");
        DMBackupMain.logger.log(Level.INFO, "==========================");
        if (!this.getBackupPassord()) {
            return;
        }
        this.canExit = false;
        if (this.canProceed()) {
            this.enablePanel(this.restoreLocationPanel, false);
            this.showProgressPanel(this.restoreProgress);
            (this.restore = new DMRestoreAction(this.sourceLocation.getText(), this.informable, true)).addPropertyChangeListener(this);
            DMBackupPasswordHandler.getInstance().setEncryptionType(Integer.parseInt(this.getPWDHintFromZip().getProperty("encryptionType")));
            DMBackupPasswordHandler.getInstance().setPasswordProvider(new DMBackupPasswordProvider() {
                private String defaultPassword = null;
                
                @Override
                public String getPassword() {
                    if (DMBackupPasswordHandler.getInstance().getEncryptionType() == 1) {
                        if (this.defaultPassword == null) {
                            this.defaultPassword = ScheduleDBBackupUtil.getDecryptedDBBackupPassword(DMBackupMain.this.getPWDHintFromZip().getProperty("pwd"));
                        }
                        return this.defaultPassword;
                    }
                    return new String(DMBackupMain.this.backupPassword.getPassword());
                }
                
                @Override
                public String getPasswordHint() {
                    return DMBackupMain.this.passwordHint.getText();
                }
            });
            this.okButton.setEnabled(false);
            this.restore.execute();
        }
    }
    
    private boolean getBackupPassord() {
        final Properties pwdProps = this.getPWDHintFromZip();
        if (pwdProps.getProperty("encryptionType").equalsIgnoreCase(String.valueOf(1))) {
            return true;
        }
        final JPanel pwdPanel = new JPanel();
        this.restorePWDLabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.restore.backup_pwd", null));
        pwdPanel.setLayout(new BoxLayout(pwdPanel, 3));
        pwdPanel.add(this.restorePWDLabel);
        pwdPanel.add(this.backupPassword);
        final JLabel wrongPWDAlert = new JLabel();
        wrongPWDAlert.setForeground(Color.RED);
        pwdPanel.add(wrongPWDAlert);
        final JLabel pwdHintLabel = new JLabel();
        if (pwdProps != null && pwdProps.size() > 0) {
            pwdHintLabel.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.pwd_hint", null) + ":" + pwdProps.getProperty("pwdHint"));
            pwdPanel.add(pwdHintLabel);
        }
        final String[] options = { BackupRestoreUtil.getString("desktopcentral.tools.common.ok", null), BackupRestoreUtil.getString("desktopcentral.tools.common.cancel", null) };
        int option;
        do {
            option = JOptionPane.showOptionDialog(this.tabbedPanel, pwdPanel, BackupRestoreUtil.getString("desktopcentral.tools.restore.pwd_verification", null), 1, -1, null, options, options[1]);
            if (option == 0) {
                if (this.validateBackupPWD()) {
                    return true;
                }
                wrongPWDAlert.setText(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.wrong_pwd", null));
            }
        } while (option == 0);
        return false;
    }
    
    private boolean validateBackupPWD() {
        DMBackupMain.logger.log(Level.INFO, "Validating password...");
        boolean isvalidPWD = false;
        try {
            final CompressUtil compressUtil = new CompressUtil(new String(this.backupPassword.getPassword()));
            isvalidPWD = compressUtil.extractFileFromArchive(this.sourceLocation.getText(), "backup-files.xml", this.serverHome);
        }
        catch (final Exception ex) {
            DMBackupMain.logger.log(Level.WARNING, "Exception while validating password : " + ex.getMessage());
            isvalidPWD = false;
        }
        finally {
            new File(this.serverHome + File.separator + "backup-files.xml").delete();
        }
        return isvalidPWD;
    }
    
    private Properties getPWDHintFromZip() {
        DMBackupMain.logger.log(Level.INFO, "Getting password hint from file...");
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            final CompressUtil compressUtil = new CompressUtil();
            compressUtil.extractFileFromArchive(this.sourceLocation.getText(), "DB_Password_Hint.txt", this.serverHome);
            fis = new FileInputStream(this.serverHome + File.separator + "DB_Password_Hint.txt");
            props.load(fis);
        }
        catch (final Exception ex) {
            DMBackupMain.logger.log(Level.WARNING, "Exception while getting password hint ", ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e) {
                DMBackupMain.logger.log(Level.WARNING, "Exception while finally  ", e);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e2) {
                DMBackupMain.logger.log(Level.WARNING, "Exception while finally  ", e2);
            }
        }
        return props;
    }
    
    public void cancelRestoreOperation() {
        this.restore.cancel(true);
    }
    
    private boolean canProceed() {
        boolean status = true;
        final int index = this.tabbedPanel.getSelectedIndex();
        String errorMessage = null;
        String errorDetail = null;
        if (index == 0) {
            final File file = new File(this.destLocation.getText());
            if (!file.exists() && !file.mkdirs() && !file.canWrite()) {
                errorMessage = BackupRestoreUtil.getString("desktopcentral.tools.backup.error.invalid_path.title", null);
                errorDetail = BackupRestoreUtil.getString("desktopcentral.tools.backup.error.invalid_path.detail", null);
                status = false;
            }
        }
        if (index == 1) {
            final String sourceText = this.sourceLocation.getText();
            if (!sourceText.endsWith(".zip") || !new File(sourceText).exists()) {
                errorMessage = BackupRestoreUtil.getString("desktopcentral.tools.restore.error.file_corrupted.title", null);
                errorDetail = BackupRestoreUtil.getString("desktopcentral.tools.restore.error.file_corrupted.detail", null);
                status = false;
            }
        }
        if (!status) {
            this.showErrorDialog(errorMessage, errorDetail, false);
            this.clearTab(index);
        }
        return status;
    }
    
    private void showProgressPanel(final ProgressPanel panel) {
        try {
            this.informable = new Informable() {
                @Override
                public void messageRead(final String message) {
                    DMBackupMain.this.updateMessageInUI(panel, message);
                }
            };
            panel.showCard("progressCard");
            panel.setServerLocation(new File(System.getProperty("server.home")).getCanonicalPath());
            panel.getServerLocationTextField().setCaretPosition(0);
        }
        catch (final IOException e) {
            DMBackupMain.logger.log(Level.WARNING, "Exception in getting canonical path : ", e);
        }
    }
    
    public void updateMessageInUI(final ProgressPanel panel, final String message) {
        panel.setProgressMessage(message);
        final int progress1 = (int)(this.fileCount++ / (double)this.totalSize * 100.0);
        if (progress1 <= 100) {
            panel.setProgress(progress1);
        }
    }
    
    public void setFileCount(final long fileCount) {
        this.fileCount = fileCount;
    }
    
    private void destBrowseActionPerformed(final ActionEvent evt) {
        this.fileChooser.setFileSelectionMode(1);
        if (this.fileChooser.showOpenDialog(this) == 0) {
            this.destLocation.setText(this.fileChooser.getSelectedFile().getAbsolutePath());
            this.destLocation.setCaretPosition(0);
        }
    }
    
    private void helpLabelMouseClicked(final MouseEvent evt) {
        if (evt.getModifiers() == 16) {
            try {
                this.helpLabel.setCursor(Cursor.getPredefinedCursor(3));
                final Desktop desktop = Desktop.getDesktop();
                String helpDoc = null;
                final String prodCode = this.util.getValueFromGenProps("productcode");
                if (prodCode.contains("DC")) {
                    helpDoc = this.util.getValueFromGenProps("dcUrl") + "/help/misc/desktop_central_data_backup_restore.html";
                }
                if (prodCode.contains("MDM")) {
                    helpDoc = this.util.getValueFromGenProps("mdmUrl") + "/help/configuring_mobile_device_manager/data_backup_and_restore.html";
                }
                if (helpDoc == null) {
                    helpDoc = this.util.getValueFromGenProps("dcUrl") + "/help/misc/desktop_central_data_backup_restore.html";
                }
                desktop.browse(new URI(helpDoc));
                this.helpLabel.setCursor(Cursor.getPredefinedCursor(12));
            }
            catch (final Exception e) {
                DMBackupMain.logger.log(Level.WARNING, "Exception in opening browser . ", e);
                this.helpLabel.setCursor(Cursor.getPredefinedCursor(12));
            }
        }
    }
    
    private void tabbedPanelStateChanged(final ChangeEvent evt) {
        final JTabbedPane pane = (JTabbedPane)evt.getSource();
        final int currentTab = pane.getSelectedIndex();
        this.clearTab(currentTab);
    }
    
    private void formWindowClosing(final WindowEvent evt) {
        this.closeApplication();
    }
    
    private void sourceBrowseActionPerformed(final ActionEvent evt) {
        this.fileChooser.setFileSelectionMode(2);
        if (this.fileChooser.showOpenDialog(this) == 0) {
            this.sourceLocation.setText(this.fileChooser.getSelectedFile().getAbsolutePath());
            this.sourceLocation.setCaretPosition(0);
        }
    }
    
    private void clearTab(final int tabIndex) {
        if (tabIndex == 0) {
            this.destLocation.setText("");
            this.destBrowse.setEnabled(true);
            this.backupProgress.showCard("emptyCard");
            this.enablePanel(this.backupLocationPanel, true);
            this.okButton.setEnabled(false);
            this.fileCount = 0L;
        }
        else if (tabIndex == 1) {
            this.sourceLocation.setText("");
            this.sourceBrowse.setEnabled(true);
            this.restoreProgress.showCard("emptyCard");
            this.enablePanel(this.restoreLocationPanel, true);
            this.okButton.setEnabled(false);
            this.fileCount = 0L;
        }
    }
    
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DMBackupMain().setVisible(true);
            }
        });
    }
    
    static {
        DMBackupMain.logger = Logger.getLogger("DCBackupRestoreUI");
        DMBackupMain.ppmMessageProperties = new Properties();
    }
}
