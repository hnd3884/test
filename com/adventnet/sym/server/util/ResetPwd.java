package com.adventnet.sym.server.util;

import java.util.Base64;
import java.security.SecureRandom;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.eventlog.EventLogThreadLocal;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.UpdateQuery;
import com.me.ems.onpremise.uac.core.UserManagementUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
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

public class ResetPwd extends StandAlonePersistence
{
    public static Logger logger;
    
    public ResetPwd() {
        initDBMigrationLog();
        showMsgInConsoleAndLog("\nInitializing DB Connection. Please wait...");
        if (BackupRestoreUtil.getDBType() == 2 && !BackupRestoreUtil.isRemoteDB() && !BackupRestoreUtil.isDBRunning()) {
            new BackupRestoreUtil();
            BackupRestoreUtil.executeInitPgsql(System.getProperty("server.home"));
        }
        BackupRestoreUtil.setDBHome();
        showMsgInConsoleAndLog("\nInitializing DB Connection. Please wait...");
    }
    
    public void startServer() throws Exception {
        try {
            super.startServer();
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private static void showMsgInConsoleAndLog(final String msg) {
        ConsoleOut.println(msg);
        ResetPwd.logger.log(Level.INFO, msg);
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
            ResetPwd.logger.log(Level.INFO, "###########################################################");
        }
        catch (final Exception ex) {
            ResetPwd.logger.log(Level.WARNING, "Custom_Query_Executer_log.txt File not Found", ex);
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
            final ResetPwd resetPwd = new ResetPwd();
            initializeCache();
            resetPwd.startServer();
            int option = 0;
            while (option != 3) {
                showMsgInConsoleAndLog("\n------------------------------------------------------------------------ ");
                showMsgInConsoleAndLog("\n1.Show all users ");
                showMsgInConsoleAndLog("\n2.Change Password ");
                showMsgInConsoleAndLog("\n3.Exit ");
                showMsgInConsoleAndLog("\nEnter your option: ");
                final Scanner scanner = new Scanner(System.in);
                option = scanner.nextInt();
                switch (option) {
                    case 1: {
                        listUsers();
                        continue;
                    }
                    case 2: {
                        changePassword();
                        continue;
                    }
                    case 3: {
                        continue;
                    }
                    default: {
                        showMsgInConsoleAndLog("\n Invalid Option!");
                        continue;
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            new StandAlonePersistence().stopDB();
        }
        System.exit(0);
    }
    
    private static void listUsers() throws Exception {
        final ArrayList userList = getDCUsers();
        final Iterator userListItr = userList.iterator();
        showMsgInConsoleAndLog("\n DomainName\\UserName");
        showMsgInConsoleAndLog("\n----------------------- ");
        while (userListItr.hasNext()) {
            final Hashtable userDetails = userListItr.next();
            String domainName = userDetails.get("DOMAINNAME");
            domainName = ((domainName == null || domainName.equalsIgnoreCase("dc.admin.fos.local")) ? "Local" : domainName);
            showMsgInConsoleAndLog("\n " + domainName + "\\" + userDetails.get("NAME"));
        }
    }
    
    private static void changePassword() throws Exception {
        final Scanner scanner = new Scanner(System.in);
        showMsgInConsoleAndLog("\nEnter user name (domainName\\userName) :");
        String loginNameWithDomain = scanner.nextLine();
        String[] names = loginNameWithDomain.split("\\\\");
        do {
            if (names.length != 2) {
                showMsgInConsoleAndLog("\nImproper format.");
                showMsgInConsoleAndLog("\nEnter user name:(domainName\\userName)");
                loginNameWithDomain = scanner.nextLine();
                names = loginNameWithDomain.split("\\\\");
            }
        } while (names.length != 2);
        final String loginName = names[1].toLowerCase();
        String domainName = names[0].toLowerCase();
        showMsgInConsoleAndLog("\nChanging password...");
        domainName = (domainName.equalsIgnoreCase("Local") ? "-" : domainName);
        final Long loginId = getLoginIdForUser(loginName, domainName);
        if (loginId != null) {
            if (DMOnPremiseUserUtil.isUserCreatedByDC(loginId)) {
                final String tempPassword = generatePasswordWithSpecialChar();
                final String serviceName = SYMClientUtil.getServiceName(loginName);
                addOrUpdateAPIKeyForLoginId(loginId);
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AaaLogin");
                updateQuery.setCriteria(new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
                updateQuery.setUpdateColumn("DOMAINNAME", (Object)"-");
                DataAccess.update(updateQuery);
                final UpdateQuery passwdProfUpd = (UpdateQuery)new UpdateQueryImpl("AaaPassword");
                passwdProfUpd.addJoin(new Join("AaaPassword", "AaaAccPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2));
                passwdProfUpd.addJoin(new Join("AaaAccPassword", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
                passwdProfUpd.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
                passwdProfUpd.setCriteria(new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginId, 0));
                final String[] serviceNames = { serviceName };
                final Criteria profileCriteria = new Criteria(Column.getColumn("AaaAccAdminProfile", "NAME"), (Object)"Profile 2", 0, (boolean)Boolean.FALSE);
                final DataObject profileDO = DataAccess.get("AaaAccAdminProfile", profileCriteria);
                if (profileDO != null && !profileDO.isEmpty()) {
                    final Row profileRow = profileDO.getFirstRow("AaaAccAdminProfile");
                    final Long profileId = (Long)profileRow.get("ACCOUNTPROFILE_ID");
                    passwdProfUpd.setUpdateColumn("PASSWDPROFILE_ID", (Object)profileId);
                    passwdProfUpd.setUpdateColumn("PASSWDRULE_ID", (Object)getCompatiblePassRuleId(serviceNames));
                    DataAccess.update(passwdProfUpd);
                    AuthUtil.changePassword(loginName, serviceName, tempPassword);
                    if (domainName == null) {
                        showMsgInConsoleAndLog("\nPassword has been changed");
                    }
                    else {
                        showMsgInConsoleAndLog("\nPassword has been changed and authentication type as 'local'");
                    }
                    ConsoleOut.println("\n------------");
                    ConsoleOut.println("\nThe new password for " + loginName + " has been changed to: " + tempPassword);
                    ConsoleOut.println("\n------------");
                    showMsgInConsoleAndLog("Use the password to log into the console and change your password immediately!");
                    final DataObject dataObject = DataAccess.constructDataObject();
                    final Row eventLogRow = getEventLogRow(4000, "DC-SYSTEM-USER", " Password of user '" + loginName + " 'has been reset by " + System.getProperty("user.name") + " from resetPassword.bat");
                    dataObject.addRow(eventLogRow);
                    DataAccess.add(dataObject);
                    ResetPwd.logger.log(Level.INFO, "\nevent log added row : {0}", eventLogRow);
                    final Long userID = DMUserHandler.getUserIdForLoginId(loginId);
                    final UserManagementUtil userManagementUtil = new UserManagementUtil();
                    userManagementUtil.defaultPasswordChanged(userID, true);
                    ResetPwd.logger.log(Level.INFO, "\nPassword changed, updated userParams table");
                }
                else {
                    showMsgInConsoleAndLog("\nUnable to change password..Contact support with server logs.");
                }
            }
            else {
                showMsgInConsoleAndLog("\nYour user account was created from another integrated application. \nPlease change your password in that application to proceed.");
            }
        }
        else {
            showMsgInConsoleAndLog("\nUser does not exist. Verify the user name.");
        }
    }
    
    private static ArrayList getDCUsers() {
        final ArrayList<Hashtable> list = new ArrayList<Hashtable>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            final Join userStatusJoin = new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Criteria activeCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0, (boolean)Boolean.FALSE);
            selectQuery.addJoin(userStatusJoin);
            selectQuery.setCriteria(activeCriteria);
            selectQuery.addSelectColumn(new Column("AaaLogin", "NAME"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "USER_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("AaaLogin", "NAME"), true);
            selectQuery.addSortColumn(sortColumn);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            final Iterator loginRows = dataObject.getRows("AaaLogin");
            while (loginRows.hasNext()) {
                final Row loginrow = loginRows.next();
                final Hashtable hashtable = new Hashtable();
                String domainName = (String)loginrow.get("DOMAINNAME");
                domainName = ((domainName == null || domainName.equalsIgnoreCase("-")) ? "Local" : domainName);
                hashtable.put("LOGIN_ID", loginrow.get("LOGIN_ID"));
                hashtable.put("USER_ID", loginrow.get("USER_ID"));
                hashtable.put("NAME", loginrow.get("NAME"));
                hashtable.put("DOMAINNAME", domainName);
                list.add(hashtable);
            }
        }
        catch (final Exception e) {
            ResetPwd.logger.log(Level.SEVERE, "Exception in getting User details", e);
        }
        return list;
    }
    
    private static Long getLoginIdForUser(final String userName, final String domainName) {
        Long loginID = null;
        try {
            Criteria criteria = new Criteria(new Column("AaaLogin", "NAME"), (Object)userName, 0, (boolean)Boolean.FALSE);
            criteria = criteria.and(new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, (boolean)Boolean.FALSE));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject.containsTable("AaaLogin")) {
                final Row row = dataObject.getFirstRow("AaaLogin");
                if (row != null && row.get("LOGIN_ID") != null) {
                    loginID = (Long)row.get("LOGIN_ID");
                }
            }
        }
        catch (final Exception e) {
            ResetPwd.logger.log(Level.SEVERE, "Error occured while checking user account status", e);
        }
        return loginID;
    }
    
    private static void addOrUpdateAPIKeyForLoginId(final Long loginId) {
        final Properties apiProps = new Properties();
        try {
            apiProps.setProperty("apiKey", AuthenticationKeyUtil.getInstance().generateTechAPIKey());
            apiProps.setProperty("loginID", String.valueOf(loginId));
            apiProps.setProperty("SERVICE_TYPE", "301");
            apiProps.setProperty("SCOPE", "REST API");
            AuthenticationKeyUtil.getInstance().addOrUpdateAPIKey(apiProps);
        }
        catch (final Exception ex) {
            ResetPwd.logger.log(Level.SEVERE, ex, () -> "Exception in addOrUpdateAPIKey for login Id " + loginId);
        }
    }
    
    public static Long getCompatiblePassRuleId(final String[] services) throws DataAccessException {
        final int servicesLen = services.length;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaServicePasswordRule"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaServicePasswordRule", "AaaService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        sq.addJoin(new Join("AaaServicePasswordRule", "AaaPasswordRule", new String[] { "PASSWDRULE_ID" }, new String[] { "PASSWDRULE_ID" }, 2));
        sq.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)services, 8));
        DataObject dobj = null;
        try {
            dobj = DataAccess.get(sq);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        final Iterator itr = dobj.getRows("AaaPasswordRule");
        final int count = dobj.size("AaaPasswordRule");
        if (count == 0 || count > 1) {
            ResetPwd.logger.log(Level.FINEST, "more than one password rule obtained. so use password rule mapped to service System");
            final SelectQuery clonedSq = (SelectQuery)sq.clone();
            clonedSq.setCriteria(new Criteria(Column.getColumn("AaaService", "NAME"), (Object)"System", 0));
            try {
                dobj = DataAccess.get(clonedSq);
            }
            catch (final Exception re2) {
                throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re2);
            }
        }
        final Row passRuleRow = dobj.getFirstRow("AaaPasswordRule");
        ResetPwd.logger.log(Level.FINEST, "passwordrule row obtained matching services : {0} is : {1}", new Object[] { services, passRuleRow });
        return (Long)passRuleRow.get("PASSWDRULE_ID");
    }
    
    private static Row getEventLogRow(final int eventID, final String userName, final String remarks) {
        final long currentTime = System.currentTimeMillis();
        final Row eventLogRow = new Row("EventLog");
        eventLogRow.set("EVENT_ID", (Object)new Integer(eventID));
        eventLogRow.set("LOGON_USER_NAME", (Object)userName);
        eventLogRow.set("EVENT_TIMESTAMP", (Object)new Long(currentTime));
        eventLogRow.set("EVENT_REMARKS", (Object)remarks);
        eventLogRow.set("EVENT_SOURCE_IP", (Object)EventLogThreadLocal.getSourceIpAddress());
        eventLogRow.set("EVENT_SOURCE_HOSTNAME", (Object)DCEventLogUtil.getHostNameForEventSource());
        return eventLogRow;
    }
    
    private static String generatePasswordWithSpecialChar() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] token = new byte[10];
        secureRandom.nextBytes(token);
        final String symbol = "!~@#$%^&+=_*";
        final int index = secureRandom.nextInt(symbol.length());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token) + symbol.charAt(index) + index;
    }
    
    static {
        ResetPwd.logger = Logger.getLogger(ResetPwd.class.getName());
    }
}
