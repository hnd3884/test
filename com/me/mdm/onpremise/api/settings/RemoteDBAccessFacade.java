package com.me.mdm.onpremise.api.settings;

import java.util.regex.Pattern;
import com.adventnet.persistence.PersistenceException;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.zoho.mickey.exception.PasswordException;
import com.me.devicemanagement.onpremise.webclient.authentication.ConfirmPasswordAction;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.me.devicemanagement.onpremise.server.util.PgsqlHbaConfUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import java.util.Map;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.webclient.admin.RemoteDBAccessUtil;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class RemoteDBAccessFacade
{
    private Logger logger;
    public static final String DEFAULT_PASSWORD = "medc";
    public static Properties dbProps;
    
    public RemoteDBAccessFacade() {
        this.logger = Logger.getLogger("RemoteDBAccessLog");
    }
    
    public JSONObject getRemoteDBAccessDetails() {
        try {
            Boolean isDBPortChanged = false;
            String defaultDBPort = "";
            String productName = "";
            final String dbName = RemoteDBAccessUtil.getInstance().getDBName();
            if (dbName != null && dbName.equalsIgnoreCase("pgsql")) {
                final DBUtil dbUtil = new DBUtil();
                final Map dbMap = dbUtil.getDBPropertiesFromFile();
                final String dbPort = dbMap.get("PORT").toString().trim();
                this.logger.log(Level.INFO, "DB port : {0}", dbPort);
                final String serverHome = System.getProperty("server.home") + File.separator + "conf";
                final String dbSettingsConf = serverHome + File.separator + "dbSettings.conf";
                if (new File(dbSettingsConf).exists()) {
                    final Properties defaultDBProperties = FileAccessUtil.readProperties(dbSettingsConf);
                    if (defaultDBProperties != null && defaultDBProperties.containsKey("PGSQL_DB_PORT")) {
                        defaultDBPort = defaultDBProperties.getProperty("PGSQL_DB_PORT").trim();
                        this.logger.log(Level.INFO, "Default DB port : {0}", defaultDBPort);
                        if (!dbPort.equalsIgnoreCase(defaultDBPort)) {
                            isDBPortChanged = true;
                            final String generalSettingsConf = serverHome + File.separator + "general_properties.conf";
                            if (new File(generalSettingsConf).exists()) {
                                final Properties generalDBProperties = FileAccessUtil.readProperties(generalSettingsConf);
                                final String productDisplayName = productName = generalDBProperties.getProperty("displayname");
                            }
                        }
                    }
                }
            }
            final JSONObject resultJson = new JSONObject();
            resultJson.put("isdbport_changed", (Object)isDBPortChanged);
            resultJson.put("selected_db", (Object)dbName);
            resultJson.put("default_port", (Object)defaultDBPort);
            resultJson.put("product_name", (Object)productName);
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting Remote DB Access details...", ex);
            throw new APIHTTPException("RDB001", new Object[0]);
        }
    }
    
    public JSONObject resetRemoteDBAccess(final JSONObject jsonObject) throws Exception {
        final String dbName = DBUtil.getActiveDBName();
        final DBUtil dbUtil = new DBUtil();
        final Map dbMap = dbUtil.getDBPropertiesFromFile();
        final int mysqlPort = Integer.parseInt(dbMap.get("PORT").toString());
        this.logger.log(Level.FINE, "mysql port is {0}", mysqlPort);
        final boolean isFirewallEnabled = WinAccessProvider.getInstance().isFirewallEnabledInDCServer((long)mysqlPort);
        if (isFirewallEnabled) {
            final WinAccessProvider winAccess = WinAccessProvider.getInstance();
            final boolean isFirewallportOpened = winAccess.openFirewallPort((long)mysqlPort);
            this.logger.log(Level.INFO, "openFirewallPort  {0}", isFirewallportOpened);
        }
        final JSONObject body = jsonObject.getJSONObject("msg_body");
        final JSONObject responseJSON = new JSONObject();
        String machineName = body.optString("remotecomputer_name");
        final String mode = body.optString("privilege");
        this.logger.log(Level.INFO, "machine Name is {0} Privi = {1}", new Object[] { machineName, mode });
        final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
        machineName = machineName.trim();
        if (!isValidMachineName(machineName)) {
            throw new APIHTTPException("RDB004", new Object[0]);
        }
        if (mode != null && mode.equalsIgnoreCase("read") && machineName != null) {
            final Boolean accessResult = RemoteDBAccessUtil.getInstance().accessPrivilage(machineName, dbName);
            if (accessResult) {
                return responseJSON.put("success", true);
            }
            throw new APIHTTPException("RDB002", new Object[0]);
        }
        else {
            if (mode == null || !mode.equalsIgnoreCase("reset") || machineName == null) {
                throw new APIHTTPException("RDB002", new Object[0]);
            }
            Boolean resetResult = false;
            final JSONArray machinesWithDbAccess = this.getRemoteDBAccessCompList().optJSONArray("ComputerList");
            if (machinesWithDbAccess != null) {
                boolean hasAccess = false;
                final String currHostOrIP = new PgsqlHbaConfUtil().addCIDRMaskToIPAddress(machineName);
                for (int i = 0; i < machinesWithDbAccess.length(); ++i) {
                    final String allowedHostOrIP = machinesWithDbAccess.optString(i);
                    if (currHostOrIP.equalsIgnoreCase(allowedHostOrIP)) {
                        resetResult = RemoteDBAccessUtil.getInstance().resetPrivilage(machineName, dbName);
                        hasAccess = true;
                        break;
                    }
                }
                if (!hasAccess) {
                    this.logger.info("Revoke Remote DB access failed. No access is granted for machine : " + machineName);
                }
            }
            if (resetResult) {
                return responseJSON.put("success", true);
            }
            throw new APIHTTPException("RDB003", new Object[] { machineName });
        }
    }
    
    public JSONObject getRemoteDBAccessCompList() {
        try {
            final JSONObject resultData = new JSONObject();
            List computerList = null;
            computerList = RemoteDBAccessUtil.getInstance().getComputerListfromDB(computerList);
            final JSONArray computerArray = JSONUtil.getInstance().convertListToJSONArray(computerList);
            resultData.put("ComputerList", (Object)computerArray);
            return resultData;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting remote DB Computer List details...", ex);
            throw new APIHTTPException("RDB005", new Object[0]);
        }
    }
    
    public JSONObject isValidLogin(final JSONObject jsonObject) {
        boolean isSuccess = false;
        final JSONObject responseJSON = new JSONObject();
        try {
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final String password = body.getString("userpassword");
            if (password != null && this.isValidAdminCheck(password)) {
                isSuccess = true;
            }
            return responseJSON.put("isvalidlogin", isSuccess);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in validating password", ex);
            throw new APIHTTPException("RDB001", new Object[0]);
        }
    }
    
    private boolean isValidAdminCheck(final String password) throws Exception {
        final String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        final String domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
        Boolean isValid = Boolean.FALSE;
        final ConfirmPasswordAction checker = new ConfirmPasswordAction();
        if (domainName != null && !domainName.equalsIgnoreCase("-")) {
            isValid = checker.validateADUser(loginName, domainName, password);
        }
        else {
            isValid = checker.validateDCUser(loginName, password);
        }
        return isValid;
    }
    
    public JSONObject updateRemoteUserCredential(final JSONObject jsonObject) {
        final JSONObject body = jsonObject.getJSONObject("msg_body");
        final String newPassword = body.optString("newpassword");
        final JSONObject responseJSON = new JSONObject();
        Boolean isPasswordChanged = false;
        try {
            if (newPassword.length() < 5 || newPassword.length() > 25) {
                throw new PasswordException("Length of the new password should be between 5-25");
            }
            this.changePassword(newPassword);
            if (!this.updateRemoteDBPasswordInDBConf(newPassword)) {
                this.logger.log(Level.SEVERE, "Unable to update RemoteDB password in config");
                throw new APIHTTPException("RDB006", new Object[0]);
            }
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            DCEventLogUtil.getInstance().addEvent(10030, userName, (HashMap)null, "mdmp.remotedbaccess.db_pwd_change", (Object)null, false);
            this.logger.info("RemoteDB password updated in config");
            isPasswordChanged = true;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in changing remote db password", ex);
            throw new APIHTTPException("RDB006", new Object[0]);
        }
        return responseJSON.put("isPasswordChanged", (Object)isPasswordChanged);
    }
    
    public void changePassword(final String newPassword) {
        Connection connection = null;
        try {
            final Properties dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
            final String readOnlyPass = PersistenceUtil.getDBPasswordProvider().getPassword((Object)dbProps.getProperty("r_password", this.getRemoteDBUserName()));
            connection = DriverManager.getConnection(dbProps.getProperty("url"), "medc", readOnlyPass);
            RelationalAPI.getInstance().getDBAdapter().changePassword("medc", readOnlyPass, newPassword, connection);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while resetting the password. Exception : ", ex);
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }
            catch (final Exception c) {
                this.logger.log(Level.SEVERE, "Error while closing the connection", c);
            }
        }
        finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }
            catch (final Exception c2) {
                this.logger.log(Level.SEVERE, "Error while closing the connection", c2);
            }
        }
    }
    
    public boolean updateRemoteDBPasswordInDBConf(String password) throws IOException, PersistenceException, PasswordException {
        final String fileNameWithAbsolutePath = PersistenceInitializer.getDBParamsFilePath();
        final File dbparamsPath = new File(fileNameWithAbsolutePath);
        password = PersistenceUtil.getDBPasswordProvider("postgres").getEncryptedPassword(password);
        final StringBuffer buffer = new StringBuffer();
        BufferedReader br = null;
        BufferedWriter bw = null;
        FileReader fr = null;
        FileWriter fw = null;
        try {
            fr = new FileReader(dbparamsPath);
            br = new BufferedReader(fr);
            String str = br.readLine();
            boolean addedNewPassword = false;
            while (str != null) {
                str = str.trim();
                if (str.matches("(#*)(r_password)(=| ).*")) {
                    if (!addedNewPassword) {
                        buffer.append("r_password=" + password + "\n");
                        addedNewPassword = true;
                    }
                }
                else {
                    buffer.append(str + "\n");
                }
                str = br.readLine();
            }
            if (!addedNewPassword) {
                buffer.append("r_password=" + password + "\n");
            }
            fw = new FileWriter(dbparamsPath);
            bw = new BufferedWriter(fw);
            bw.write(buffer.toString());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while updating remote access db password", ex);
            return false;
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (fr != null) {
                    fr.close();
                }
                if (fw != null) {
                    fw.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Error while closing stream", e);
            }
        }
        return true;
    }
    
    public String getRemoteDBUserName() {
        final Properties dbProps = this.getDBProps();
        return dbProps.getProperty("r_username", "medc");
    }
    
    public Properties getDBProps() {
        if (RemoteDBAccessFacade.dbProps == null) {
            try {
                RemoteDBAccessFacade.dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception occurred while getting the Remote DB user Name : ", e);
            }
        }
        return RemoteDBAccessFacade.dbProps;
    }
    
    public static boolean isValidMachineName(final String address) {
        if (address == null) {
            return false;
        }
        final String ipv4 = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
        final String ipv5 = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
        final String fqdn = "(?=^.{1,254}$)(^(?:(?!\\d+\\.|-)[a-zA-Z0-9_\\-]{1,63}(?<!-)\\.?)+(?:[a-zA-Z]{2,})$)";
        final String machineName = "^(?![0-9]{1,15}$)[a-zA-Z0-9-]{1,15}$";
        for (final String regex : new String[] { "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])", "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}", "^(?![0-9]{1,15}$)[a-zA-Z0-9-]{1,15}$", "(?=^.{1,254}$)(^(?:(?!\\d+\\.|-)[a-zA-Z0-9_\\-]{1,63}(?<!-)\\.?)+(?:[a-zA-Z]{2,})$)" }) {
            if (Pattern.matches(regex, address)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        RemoteDBAccessFacade.dbProps = null;
    }
}
