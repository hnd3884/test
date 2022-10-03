package com.me.devicemanagement.onpremise.tools.dbmigration.handler;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.onpremise.webclient.authentication.ConfirmPasswordAction;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.tools.dbmigration.utils.DBMigrationUtils;
import com.adventnet.mfw.logging.LoggerUtil;
import java.util.logging.Logger;
import com.adventnet.persistence.StandAlonePersistence;

public class MasterkeyPasswordProvider extends StandAlonePersistence
{
    private static Logger logger;
    private static final String DLL_LIBRARY_FILE_NAME = "SyMNative";
    
    public static void main(final String[] args) {
        String domainName = null;
        String uName = null;
        String uPassword = null;
        boolean isValid = false;
        try {
            LoggerUtil.initLog("ProvideMssqlMasterKey", true);
            if (DBMigrationUtils.getDBName().equalsIgnoreCase("mssql")) {
                BackupRestoreUtil.setDBHome();
                MasterkeyPasswordProvider.logger.log(Level.INFO, "\nInitializing DB Connection. Please wait...");
                final MasterkeyPasswordProvider h = new MasterkeyPasswordProvider();
                h.startServer();
                try {
                    if (args[0] != null && args[0].trim().length() > 0 && args[1] != null && args[1].trim().length() > 0) {
                        uName = args[0].trim();
                        uPassword = args[1].trim();
                        if (args.length >= 3 && args[2] != null) {
                            domainName = args[2].trim();
                        }
                    }
                    else {
                        ConsoleOut.println("Please enter valid loginname and password");
                        System.exit(0);
                    }
                }
                catch (final Exception ex) {
                    MasterkeyPasswordProvider.logger.log(Level.INFO, ex.toString(), ex);
                }
                final ConfirmPasswordAction confirmPasswordAction = new ConfirmPasswordAction();
                if (domainName != null && domainName.trim().length() > 0) {
                    isValid = confirmPasswordAction.validateADUser(uName, domainName, uPassword);
                }
                else {
                    isValid = confirmPasswordAction.validateDCUser(uName, uPassword);
                }
                final long userID = DMUserHandler.getUserID(uName);
                final long loginID = DMUserHandler.getLoginIdForUserId(Long.valueOf(userID));
                final boolean isAdminUser = DMUserHandler.isUserInAdminRole(Long.valueOf(loginID));
                if (isAdminUser && isValid) {
                    provideMasterKeyPassword();
                }
                else {
                    ConsoleOut.println("Authentication Failed !!(provide valid login details)");
                }
            }
            else {
                ConsoleOut.println("Current DB: " + DBMigrationUtils.getDBName() + "\n MasterKey is avilable only for Mssql");
            }
        }
        catch (final Exception ex2) {
            MasterkeyPasswordProvider.logger.log(Level.INFO, "Exception while getting master key ", ex2);
        }
        System.exit(0);
    }
    
    private static void provideMasterKeyPassword() {
        final String masterKeyPassword = getMasterKeyPassword();
        if (masterKeyPassword != null) {
            ConsoleOut.println("MasterKeyPassword : " + CryptoUtil.decrypt(masterKeyPassword));
        }
        else {
            ConsoleOut.println("MasterKey Doesn't Exit");
        }
    }
    
    private static String getMasterKeyPassword() {
        String masterKeyPassword = null;
        try {
            final NodeList propertyList = DBMigrationUtils.writePersistenceConfiguration(masterKeyPassword);
            if (propertyList != null && propertyList.getLength() > 0) {
                for (int i = 0; i < propertyList.getLength(); ++i) {
                    final Node propertyEl = propertyList.item(i);
                    if (propertyEl.getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("masterkey.password")) {
                        masterKeyPassword = propertyEl.getAttributes().getNamedItem("value").getNodeValue();
                        break;
                    }
                }
            }
        }
        catch (final Exception ex) {
            MasterkeyPasswordProvider.logger.log(Level.SEVERE, ex.toString(), ex);
        }
        return masterKeyPassword;
    }
    
    static {
        MasterkeyPasswordProvider.logger = Logger.getLogger(MasterkeyPasswordProvider.class.getName());
        System.loadLibrary("SyMNative");
    }
}
