package com.me.ems.onpremise.uac.core;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.devicemanagement.framework.server.authentication.UserMgmtUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.adventnet.persistence.cache.CacheRepository;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.cache.CacheRepositoryImpl;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import java.util.Properties;
import java.io.File;
import java.util.logging.Level;
import com.adventnet.tools.prevalent.ConsoleOut;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.logging.Logger;
import com.adventnet.persistence.StandAlonePersistence;

public class DisableTFA extends StandAlonePersistence
{
    protected static Logger logger;
    
    protected DisableTFA() {
        initDBMigrationLog();
        BackupRestoreUtil.setDBHome();
        showMsgInConsoleAndLog("\nInitializing DB Connection. Please wait...");
        initializeCache();
    }
    
    public void startServer() throws Exception {
        try {
            super.startServer();
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected static void showMsgInConsoleAndLog(final String msg) {
        ConsoleOut.println(msg);
        DisableTFA.logger.log(Level.INFO, msg);
    }
    
    private static void initDBMigrationLog() {
        try {
            final String homeDir = System.getProperty("server.home");
            final String logFilePath = homeDir + File.separator + "logs" + File.separator + "cmdlinequeryexecuterlog_%g.txt";
            final Properties properties = new Properties();
            properties.setProperty("java.util.logging.FileHandler.pattern", logFilePath.replace("\\", "/"));
            properties.setProperty("java.util.logging.FileHandler.limit", "5000000");
            properties.setProperty("java.util.logging.FileHandler.count", "2");
            DCLogUtil.initLogger(properties);
            DisableTFA.logger.log(Level.INFO, "###########################################################");
        }
        catch (final Exception ex) {
            DisableTFA.logger.log(Level.WARNING, "Custom_Query_Executer_log.txt File not Found", ex);
        }
    }
    
    private static void initializeCache() {
        final CacheRepository cacheRepository = (CacheRepository)new CacheRepositoryImpl();
        cacheRepository.initialize(-1, true);
        cacheRepository.setCloningStatus(false);
        cacheRepository.setCachingStatus(true);
        CacheManager.setCacheRepository(cacheRepository);
    }
    
    public static void main(final String[] args) throws Exception {
        try {
            final DisableTFA disableTfa = new DisableTFA();
            disableTfa.startServer();
            if (args.length > 0 && args[0].equalsIgnoreCase("TempDisable")) {
                final String userName = disableTfa.validateUserForAdminRole();
                if (userName != null) {
                    disableTfa.disableTFATemporarily(userName);
                }
                return;
            }
            final String enteredKey = disableTfa.getTfaDisablingKey();
            if (enteredKey == null || enteredKey.isEmpty()) {
                showMsgInConsoleAndLog("\nKey must not be empty.Please enter the key.");
                return;
            }
            final String userName2 = disableTfa.validateUserForAdminRole();
            if (userName2 != null && enteredKey.equalsIgnoreCase(SecurityUtil.getSecurityParameter("TFADisablingKey"))) {
                disableTfa.disableTFAPermanently(userName2);
            }
            else {
                showMsgInConsoleAndLog("\nYou have entered an invalid key.");
            }
        }
        catch (final Exception e) {
            showMsgInConsoleAndLog("\nUnable to disable TFA");
            DisableTFA.logger.log(Level.SEVERE, "exception in main() while disableTFA:- ", e);
        }
        finally {
            new StandAlonePersistence().stopDB();
        }
        System.exit(0);
    }
    
    private void disableTFAPermanently(final String userName) throws Exception {
        try {
            TFAUtil.updateTwoFactorAuthDisabledDetails();
            final Map dataMap = new HashMap();
            dataMap.put("authType", "disabled");
            dataMap.put("otp", "0");
            dataMap.put("isTfaPermanentDisable", "true");
            TFAUtil.disableTwoFactorAuthentication(userName, dataMap);
            SecurityUtil.deleteSecurityParameter("TFADisablingKey");
            ApiFactoryProvider.getCacheAccessAPI().putCache("isTFAToBeEnabled", (Object)false);
            showMsgInConsoleAndLog("\nTFA permanently disabled successfully");
        }
        catch (final Exception exception) {
            DisableTFA.logger.log(Level.INFO, "disableTFAPermanently():- ", exception);
            throw exception;
        }
    }
    
    private void disableTFATemporarily(final String userName) throws Exception {
        try {
            TFAUtil.updateTwoFactorAuthDisabledDetails();
            final Map dataMap = new HashMap();
            dataMap.put("authType", "disabled");
            dataMap.put("otp", "0");
            TFAUtil.disableTwoFactorAuthentication(userName, dataMap);
            UserMgmtUtil.updateUserMgmtParameter("TFADisableExpiry", String.valueOf(System.currentTimeMillis() + 691200000L));
            ApiFactoryProvider.getCacheAccessAPI().putCache("isTFAToBeEnabled", (Object)false);
            showMsgInConsoleAndLog("\nTFA temporarily disabled successfully");
        }
        catch (final Exception exception) {
            DisableTFA.logger.log(Level.INFO, "disableTFATemporarily():- ", exception);
            throw exception;
        }
    }
    
    protected String validateUserForAdminRole() {
        final String username = this.getUserName();
        final String password = this.getPassword();
        final String domain = this.getDomainIfAvailable();
        if (this.validateUserForAdminRole(username, password, domain)) {
            return username;
        }
        return null;
    }
    
    private boolean validateUserForAdminRole(final String username, final String password, final String domain) {
        User user = null;
        try {
            user = UserManagementUtil.validateAndAuthenticateUser(username, password, domain);
        }
        catch (final Exception ex) {
            DisableTFA.logger.log(Level.INFO, "Exception while validating user", ex);
        }
        if (user == null) {
            showMsgInConsoleAndLog("Invalid Username or Password or Domain");
            return false;
        }
        if (user.isAdminUser()) {
            DisableTFA.logger.log(Level.INFO, "User is in admin role");
            return true;
        }
        showMsgInConsoleAndLog("User does not have enough privilege to execute query");
        return false;
    }
    
    private String getTfaDisablingKey() {
        String tfaDisablingKey = null;
        try {
            tfaDisablingKey = this.getInputFromCommandLine("\nEnter the 2FA disable key : ", false);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read tfaDisablingKey!");
            System.exit(1);
        }
        return tfaDisablingKey;
    }
    
    private String getUserName() {
        String username = null;
        try {
            username = this.getInputFromCommandLine("Administrator Username : ", false);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read Username!");
            System.exit(1);
        }
        return username;
    }
    
    private String getPassword() {
        String password = "";
        try {
            password = this.getInputFromCommandLine("Password : ", true);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read Password!");
            System.exit(1);
        }
        return password;
    }
    
    private String getDomainIfAvailable() {
        String domain = null;
        try {
            domain = this.getInputFromCommandLine("Domain (If AD User) :  ", false);
        }
        catch (final IOException e) {
            ConsoleOut.println("Error trying to read Domain!");
            System.exit(1);
        }
        return domain;
    }
    
    private String getInputFromCommandLine(final String prompt, final boolean isHidden) throws IOException {
        String password = "";
        ConsoleEraser consoleEraser = null;
        if (isHidden) {
            consoleEraser = new ConsoleEraser();
        }
        ConsoleOut.print(prompt);
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        if (consoleEraser != null) {
            consoleEraser.start();
        }
        password = in.readLine();
        if (consoleEraser != null) {
            consoleEraser.halt();
        }
        ConsoleOut.print("\b");
        return password;
    }
    
    static {
        DisableTFA.logger = Logger.getLogger(DisableTFA.class.getName());
    }
    
    private class ConsoleEraser extends Thread
    {
        private boolean running;
        
        private ConsoleEraser() {
            this.running = true;
        }
        
        @Override
        public void run() {
            while (this.running) {
                ConsoleOut.print("\b ");
                try {
                    currentThread();
                    Thread.sleep(1L);
                    continue;
                }
                catch (final InterruptedException e) {}
                break;
            }
        }
        
        public synchronized void halt() {
            this.running = false;
        }
    }
}
