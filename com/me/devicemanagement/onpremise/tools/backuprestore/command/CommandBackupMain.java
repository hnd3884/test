package com.me.devicemanagement.onpremise.tools.backuprestore.command;

import java.io.IOException;
import java.util.Properties;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.CompressUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMRestoreAction;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DCBackupRestoreException;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMBackupAction;
import com.adventnet.persistence.PersistenceUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordProvider;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import com.adventnet.mfw.ConsoleOut;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.io.InputStream;
import java.util.logging.Level;
import java.io.PrintStream;
import com.adventnet.mfw.Starter;
import java.util.logging.LogManager;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Logger;

public class CommandBackupMain
{
    private Logger logger;
    private CommandArgumentProvider argumentProvider;
    private static String status;
    public static String serverHome;
    
    public CommandBackupMain(final String[] args) {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.argumentProvider = CommandArgumentProvider.getInstance();
        this.initLog();
        this.initialize(args);
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
            inputStream.close();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in initializing log manager for CommandBackupMain ", ex);
        }
    }
    
    private void initialize(final String[] args) {
        final int argsLength = args.length;
        if (argsLength <= 0) {
            this.exitWithUsage("Missed the command arguments.", true);
        }
        try {
            for (int i = 0; i < argsLength; ++i) {
                if (args[i].trim().equalsIgnoreCase("-h") || args[i].trim().equalsIgnoreCase("-help")) {
                    this.exitWithUsage("Help Console Window.", true);
                }
                else if (args[i].trim().equalsIgnoreCase("-operation")) {
                    this.argumentProvider.setOperation(args[++i].trim());
                }
                else if (args[i].trim().equalsIgnoreCase("-backuppath")) {
                    this.argumentProvider.setBackupPath(args[++i].trim());
                }
                else if (args[i].trim().equalsIgnoreCase("-restorepath")) {
                    this.argumentProvider.setRestorePath(args[++i].trim());
                }
                else if (args[i].trim().equalsIgnoreCase("-password")) {
                    if (args[++i].trim().length() < 5) {
                        this.exitWithUsage(BackupRestoreUtil.getString("desktopcentral.tools.backup.password.validationL", null), false);
                    }
                    this.argumentProvider.setPassword(args[i].trim());
                    this.argumentProvider.setEncryptionType(2);
                }
                else if (args[i].trim().equalsIgnoreCase("-passwordhint")) {
                    this.argumentProvider.setPasswordhint(args[++i].trim());
                }
                else {
                    this.exitWithUsage("Given command is not valid for the Backup and Restore.", true);
                }
            }
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            this.logger.log(Level.WARNING, "Exception : ", e);
            this.exitWithUsage("Missed the value.", true);
        }
        if (this.argumentProvider.getOperation() == null || (this.argumentProvider.getBackupPath() == null && this.argumentProvider.getRestorePath() == null)) {
            this.exitWithUsage("Operation or Path Location not provided.", true);
        }
        else if ((this.argumentProvider.getOperation().equalsIgnoreCase("b") && this.argumentProvider.getBackupPath() == null) || (this.argumentProvider.getOperation().equalsIgnoreCase("r") && this.argumentProvider.getRestorePath() == null)) {
            this.exitWithUsage("Operation path invalid.", true);
        }
    }
    
    private void commandBackupRestore() {
        try {
            CommandBackupMain.serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            this.logger.log(Level.INFO, "Starting Backup-Restore utility ( through Command Prompt Mode ) ");
            if (this.isValidPath()) {
                if (this.argumentProvider.getOperation().equalsIgnoreCase("B")) {
                    this.backupCommandAction();
                }
                else if (this.argumentProvider.getOperation().equalsIgnoreCase("R")) {
                    this.restoreCommandAction();
                }
                else {
                    this.exitWithUsage("Please Select any one operation Backup or Restore.", true);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in commandBackupRestore() - method : ", e);
        }
    }
    
    private void backupCommandAction() {
        boolean backupStatus = true;
        DCBackupRestoreException errorInfo = null;
        this.logger.log(Level.INFO, "=================================");
        this.logger.log(Level.INFO, "Command Backup operation invoked.");
        this.logger.log(Level.INFO, "=================================");
        ConsoleOut.println("Processing ...");
        DMBackupPasswordHandler.getInstance().setEncryptionType(this.argumentProvider.getEncryptionType());
        DMBackupPasswordHandler.getInstance().setPasswordProvider(new DMBackupPasswordProvider() {
            private String defaultPassword = null;
            
            @Override
            public String getPassword() {
                if (DMBackupPasswordHandler.getInstance().getEncryptionType() == 2) {
                    return CommandBackupMain.this.argumentProvider.getPassword();
                }
                if (this.defaultPassword == null) {
                    this.defaultPassword = PersistenceUtil.generateRandomPassword();
                }
                return this.defaultPassword;
            }
            
            @Override
            public String getPasswordHint() {
                if (DMBackupPasswordHandler.getInstance().getEncryptionType() == 2) {
                    return CommandBackupMain.this.argumentProvider.getPasswordhint();
                }
                return null;
            }
        });
        try {
            final DMBackupAction backup = new DMBackupAction(this.argumentProvider.getBackupPath());
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Calling backup from Command mode");
            backup.backup();
        }
        catch (final DCBackupRestoreException e) {
            backupStatus = false;
            errorInfo = e;
            this.logger.log(Level.INFO, "Exception while performing backup :: ", e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.INFO, "Exception while performing backup :: ", e2);
            backupStatus = false;
            final String operationName = BackupRestoreUtil.getString("desktopcentral.tools.backup.title", null);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            errorInfo = BackupRestoreUtil.createException(-6, new Object[] { displayName, operationName }, e2);
        }
        if (errorInfo != null && !backupStatus) {
            CommandBackupMain.status = "FAILED";
            this.logger.log(Level.WARNING, errorInfo.getMessage());
            this.logger.log(Level.INFO, "BACKUP OPERATION FAILED.");
        }
        else {
            CommandBackupMain.status = "SUCCESS";
            this.logger.log(Level.INFO, "BACKUP COMPLETED SUCCESSFULLY.");
        }
    }
    
    private void restoreCommandAction() {
        boolean restoreStatus = true;
        DCBackupRestoreException errorInfo = null;
        this.logger.log(Level.INFO, "==================================");
        this.logger.log(Level.INFO, "Command Restore operation invoked.");
        this.logger.log(Level.INFO, "==================================");
        if (!this.isValidateBackupPWD()) {
            this.exitWithUsage(BackupRestoreUtil.getString("desktopcentral.tools.changedb.gui.wrong_pwd", null), false);
        }
        ConsoleOut.println("Processing ...");
        DMBackupPasswordHandler.getInstance().setEncryptionType(this.argumentProvider.getEncryptionType());
        DMBackupPasswordHandler.getInstance().setPasswordProvider(new DMBackupPasswordProvider() {
            private String defaultPassword = null;
            
            @Override
            public String getPassword() {
                if (DMBackupPasswordHandler.getInstance().getEncryptionType() == 2) {
                    return CommandBackupMain.this.argumentProvider.getPassword();
                }
                if (this.defaultPassword == null) {
                    this.defaultPassword = ScheduleDBBackupUtil.getDecryptedDBBackupPassword(CommandBackupMain.this.getPWDHintFromZip("pwd"));
                }
                return this.defaultPassword;
            }
            
            @Override
            public String getPasswordHint() {
                return CommandBackupMain.this.getPWDHintFromZip("pwdHint");
            }
        });
        try {
            final DMRestoreAction restore = new DMRestoreAction(this.argumentProvider.getRestorePath(), null, true, null, false);
            restore.restore();
        }
        catch (final DCBackupRestoreException e) {
            restoreStatus = false;
            errorInfo = e;
            this.logger.log(Level.INFO, "Exception while performing restore :: ", e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.INFO, "Exception while performing restore :: ", e2);
            restoreStatus = false;
            final String operationName = BackupRestoreUtil.getString("desktopcentral.tools.backup.title", null);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            errorInfo = BackupRestoreUtil.createException(-6, new Object[] { displayName, operationName }, e2);
        }
        if (errorInfo != null && !restoreStatus) {
            CommandBackupMain.status = "FAILED";
            this.logger.log(Level.WARNING, errorInfo.getMessage());
            this.logger.log(Level.INFO, "RESTORE OPERATION FAILED.");
        }
        else {
            CommandBackupMain.status = "SUCCESS";
            this.logger.log(Level.INFO, "RESTORE COMPLETED SUCCESSFULLY.");
        }
    }
    
    private boolean isValidateBackupPWD() {
        this.logger.log(Level.INFO, "Validating password...");
        boolean isvalidPWD = false;
        if (this.getPWDHintFromZip("encryptionType").equalsIgnoreCase(String.valueOf(1)) && this.argumentProvider.getPassword() == null) {
            return true;
        }
        try {
            final CompressUtil compressUtil = new CompressUtil(this.argumentProvider.getPassword());
            isvalidPWD = compressUtil.extractFileFromArchive(this.argumentProvider.getRestorePath(), "backup-files.xml", CommandBackupMain.serverHome);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while validating password : ", ex);
            isvalidPWD = false;
        }
        finally {
            new File(CommandBackupMain.serverHome + File.separator + "backup-files.xml").delete();
        }
        return isvalidPWD;
    }
    
    private String getPWDHintFromZip(final String key) {
        this.logger.log(Level.INFO, "Getting password hint from file...");
        final Properties passwordHint = new Properties();
        FileInputStream fis = null;
        try {
            final CompressUtil compressUtil = new CompressUtil();
            compressUtil.extractFileFromArchive(this.argumentProvider.getRestorePath(), "DB_Password_Hint.txt", CommandBackupMain.serverHome);
            fis = new FileInputStream(CommandBackupMain.serverHome + File.separator + "DB_Password_Hint.txt");
            passwordHint.load(fis);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting password hint ", ex);
            try {
                if (fis != null) {
                    fis.close();
                    new File(CommandBackupMain.serverHome + File.separator + "DB_Password_Hint.txt").delete();
                }
            }
            catch (final IOException e) {
                this.logger.log(Level.WARNING, "Exception while finally  ", e);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                    new File(CommandBackupMain.serverHome + File.separator + "DB_Password_Hint.txt").delete();
                }
            }
            catch (final IOException e2) {
                this.logger.log(Level.WARNING, "Exception while finally  ", e2);
            }
        }
        if (passwordHint.getProperty(key) == null) {
            return "";
        }
        return passwordHint.getProperty(key);
    }
    
    private boolean isValidPath() {
        boolean status = true;
        String errorMessage = null;
        String errorDetail = null;
        if (this.argumentProvider.getOperation().equalsIgnoreCase("B")) {
            this.argumentProvider.setBackupPath(this.getPath(this.argumentProvider.getBackupPath()));
            final File file = new File(this.argumentProvider.getBackupPath());
            if (!file.exists() && !file.mkdirs() && !file.canWrite()) {
                errorMessage = BackupRestoreUtil.getString("desktopcentral.tools.backup.error.invalid_path.title", null);
                errorDetail = BackupRestoreUtil.getString("desktopcentral.tools.backup.error.invalid_path.detail", null);
                status = false;
            }
        }
        else if (this.argumentProvider.getOperation().equalsIgnoreCase("R")) {
            this.argumentProvider.setRestorePath(this.getPath(this.argumentProvider.getRestorePath()));
            final String sourceText = this.argumentProvider.getRestorePath();
            if (!sourceText.endsWith(".zip") || !new File(sourceText).exists()) {
                errorMessage = BackupRestoreUtil.getString("desktopcentral.tools.restore.error.file_corrupted.title", null);
                errorDetail = BackupRestoreUtil.getString("desktopcentral.tools.restore.error.file_corrupted.detail", null);
                status = false;
            }
        }
        if (!status) {
            this.logger.log(Level.WARNING, errorMessage + " : ", errorDetail);
        }
        return status;
    }
    
    private String getPath(String path) {
        String CanonicalPath = null;
        path = path.replaceAll("..\"", "..\\\\");
        try {
            CanonicalPath = (path.startsWith("..") ? new File(CommandBackupMain.serverHome + File.separator + "bin" + File.separator + path).getCanonicalPath() : path);
        }
        catch (final IOException e) {
            this.logger.log(Level.WARNING, "Exception getting Canonical Path : ", e);
        }
        return CanonicalPath;
    }
    
    private void exitWithUsage(final String error, final boolean usage) {
        if (usage) {
            ConsoleOut.println("----------------------------------------------------------------------------------------------\nUsage:\n Options are:\n [-h or -help to Displays this usage.]\n [-operation (B) to backup, (R) to restore ]\n [-backuppath ServicePack file path, -restorepath ServicePack file path should be (*.zip) file]\n [-password Backup & Restore Password]\n [-passwordhint Set Backup Password Hint]\n\nExamples :\n [commandModeBackupRestore.bat -operation B -backupPath C: -password ***** -passwordHint hint ]\n [commandModeBackupRestore.bat -operation R -restorePath C:*zip -password ***** ]\n----------------------------------------------------------------------------------------------\n");
        }
        ConsoleOut.println(error);
        this.logger.log(Level.WARNING, error);
        this.logger.log(Level.INFO, "Closing Command Mode Backup and Restore operation.");
        System.exit(0);
    }
    
    public static void main(final String[] args) {
        final CommandBackupMain cmd = new CommandBackupMain(args);
        cmd.commandBackupRestore();
        cmd.exitWithUsage(CommandBackupMain.status, false);
    }
    
    static {
        CommandBackupMain.status = "FAILED";
    }
}
