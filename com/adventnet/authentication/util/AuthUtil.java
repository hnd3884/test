package com.adventnet.authentication.util;

import com.adventnet.ds.query.DeleteQuery;
import org.apache.commons.codec.digest.DigestUtils;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.io.UnsupportedEncodingException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.persistence.Persistence;
import jcifs.smb.NtlmPasswordAuthentication;
import java.security.Principal;
import jcifs.smb.SmbAuthException;
import jcifs.http.NtlmSsp;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;
import jcifs.ntlmssp.Type2Message;
import jcifs.smb.SmbSession;
import jcifs.UniAddress;
import jcifs.ntlmssp.Type1Message;
import sun.misc.BASE64Decoder;
import com.adventnet.authentication.internal.WritableCredential;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.HashMap;
import com.adventnet.audit.AuditException;
import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.authentication.PasswordException;
import java.util.Iterator;
import org.mindrot.jbcrypt.BCrypt;
import com.adventnet.authentication.PAM;
import java.util.ArrayList;
import java.util.Collection;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.Credential;
import java.net.InetAddress;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.logging.Level;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.authentication.rememberme.RememberMeService;
import com.adventnet.audit.Audit;
import java.util.logging.Logger;

public class AuthUtil
{
    private static final Logger LOGGER;
    private static Audit auditInstance;
    public static RememberMeService rememberMeService;
    public static String ENCODING_FORMAT;
    private static byte[] encoding;
    private static CredentialStack credentialStack;
    private static String defaultAuthRule;
    
    public static void clearCookies(final HttpServletRequest hreq, final HttpServletResponse hres, final String[] cookieArr) {
        final List cookieList = Arrays.asList(cookieArr);
        final Cookie[] cookies = hreq.getCookies();
        final int len = (cookies == null) ? 0 : cookies.length;
        Cookie cookie = null;
        for (int i = 0; i < len; ++i) {
            cookie = cookies[i];
            if (cookieList.contains(cookie.getName())) {
                cookie.setMaxAge(0);
                AuthUtil.LOGGER.log(Level.FINE, "removed cookie : {0} with value : {1}", new Object[] { cookie.getName(), cookie.getValue() });
                hres.addCookie(cookie);
            }
        }
    }
    
    public static String getLocalHostName() {
        String hostName = "unknown";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (final Exception e) {
            AuthUtil.LOGGER.log(Level.SEVERE, "Exception occured while using InetAddress.getLocalHost", e);
        }
        return hostName;
    }
    
    public static Long getUserId(final String loginName) throws DataAccessException {
        String domainName = null;
        final Credential credential = getUserCredential();
        if (credential != null) {
            domainName = credential.getDomainName();
        }
        return getUserId(loginName, domainName);
    }
    
    public static Long getUserId(final String loginName, String domainName) throws DataAccessException {
        try {
            try {
                domainName = (String)((domainName == null || domainName.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domainName);
            }
            catch (final MetaDataException e1) {
                AuthUtil.LOGGER.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
                e1.printStackTrace();
            }
            final ReadOnlyPersistence rp = (ReadOnlyPersistence)BeanUtil.lookup("PureCachedPersistence");
            Criteria c = new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0, false);
            c = c.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
            final DataObject aaaLoginDO = rp.get("AaaLogin", c);
            return (Long)aaaLoginDO.getRow("AaaLogin").get(2);
        }
        catch (final Exception e2) {
            throw new DataAccessException("Exception occured while fetching userID for loginName :: [" + loginName + "] and domainName :: [" + domainName + "]");
        }
    }
    
    public static Long getServiceId(final String serviceName) throws DataAccessException {
        return (Long)AuthDBUtil.getObject("AaaService", "SERVICE_ID", "NAME", serviceName);
    }
    
    public static Long getAccountProfileId(final String profileName) throws DataAccessException {
        return (Long)AuthDBUtil.getObject("AaaAccAdminProfile", "ACCOUNTPROFILE_ID", "NAME", profileName);
    }
    
    public static Long getPasswordProfileId(final String profileName) throws DataAccessException {
        return (Long)AuthDBUtil.getObject("AaaPasswordProfile", "PASSWDPROFILE_ID", "NAME", profileName);
    }
    
    public static Long getRoleId(final String roleName) throws DataAccessException {
        return (Long)AuthDBUtil.getObject("AaaRole", "ROLE_ID", "NAME", roleName);
    }
    
    public static String getEmailId(final Long userId) throws DataAccessException {
        final Long contactInfoId = (Long)AuthDBUtil.getObject("AaaUserContactInfo", "CONTACTINFO_ID", "USER_ID", userId);
        final String emailId = (String)AuthDBUtil.getObject("AaaContactInfo", "EMAILID", "CONTACTINFO_ID", contactInfoId);
        return emailId;
    }
    
    public static Long getAccountId(final String loginName, final String serviceName) throws DataAccessException {
        return getAccountId(loginName, serviceName, null);
    }
    
    public static Long getAccountId(final String loginName, final String serviceName, String domainName) throws DataAccessException {
        try {
            domainName = (String)((domainName == null || domainName.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domainName);
        }
        catch (final MetaDataException e1) {
            AuthUtil.LOGGER.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0, false);
        criteria = criteria.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), (Object)domainName, 0, false));
        criteria = criteria.and(Column.getColumn("AaaService", "NAME"), (Object)serviceName, 0);
        DataObject dobj = null;
        try {
            dobj = DataAccess.get("AaaAccount", criteria);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        final Row accRow = dobj.getFirstRow("AaaAccount");
        return (Long)accRow.get("ACCOUNT_ID");
    }
    
    public static DataObject createUserAccount(final DataObject accountDO) throws DataAccessException, PasswordException {
        int workload = 0;
        AuthUtil.LOGGER.log(Level.FINEST, "createUserAccount invoked with dataobject : {0}", accountDO);
        Iterator accItr = accountDO.getRows("AaaAccount");
        final int numOfAcc = getCount(accItr);
        final Credential credential = getUserCredential();
        if (credential != null) {
            validateForAccountCreation(credential.getAccountId(), numOfAcc);
        }
        final List requiredTables = Arrays.asList("AaaUser", "AaaLogin", "AaaAccount", "AaaPassword", "AaaAccPassword");
        final List tablesFromDO = accountDO.getTableNames();
        if (!tablesFromDO.containsAll(requiredTables)) {
            throw new DataAccessException("In sufficient data for creating an account, required tables in dataobject " + requiredTables);
        }
        final long now = System.currentTimeMillis();
        final Row userRow = accountDO.getFirstRow("AaaUser");
        userRow.set("CREATEDTIME", (Object)new Long(now));
        final Row userStatusRow = new Row("AaaUserStatus");
        userStatusRow.set("USER_ID", userRow.get("USER_ID"));
        userStatusRow.set("STATUS", (Object)"ACTIVE");
        userStatusRow.set("UPDATEDTIME", (Object)new Long(now));
        accountDO.addRow(userStatusRow);
        final Row loginRow = accountDO.getFirstRow("AaaLogin");
        try {
            final String domain = (String)loginRow.get("DOMAINNAME");
            loginRow.set("DOMAINNAME", (domain == null || domain.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domain);
        }
        catch (final MetaDataException e) {
            throw new DataAccessException("Exception occured while obtaining default value of [AAALOGIN.DOMAINNAME]" + e);
        }
        Criteria c = new Criteria(Column.getColumn("AaaLogin", "NAME"), loginRow.get("NAME"), 0, false);
        c = c.and(new Criteria(Column.getColumn("AaaLogin", "DOMAINNAME"), loginRow.get("DOMAINNAME"), 0, false));
        final DataObject dobj = DataAccess.get("AaaLogin", c);
        if (!dobj.isEmpty()) {
            AuthUtil.LOGGER.log(Level.SEVERE, "Could not create new User :: {0}, as the user with given LoginName and DomainName already exists", new Object[] { loginRow.get("NAME") });
            throw new DataAccessException("Could not create new User ::" + loginRow.get("NAME") + ", as the given LoginName and DomainName :: " + loginRow.get("DOMAINNAME") + "already exists");
        }
        if (loginRow.get("USER_ID") == null) {
            loginRow.set("USER_ID", userRow.get("USER_ID"));
            accountDO.updateRow(loginRow);
        }
        accItr = accountDO.getRows("AaaAccount");
        Row accountRow = null;
        final List serviceIds = new ArrayList();
        while (accItr.hasNext()) {
            accountRow = accItr.next();
            serviceIds.add(accountRow.get("SERVICE_ID"));
            if (accountRow.get("LOGIN_ID") == null) {
                accountRow.set("LOGIN_ID", loginRow.get("LOGIN_ID"));
            }
            accountRow.set("CREATEDTIME", (Object)new Long(now));
            accountDO.updateRow(accountRow);
            Row accOwnerProfileRow = null;
            try {
                accOwnerProfileRow = accountDO.getFirstRow("AaaAccOwnerProfile", accountRow);
            }
            catch (final DataAccessException ex) {}
            if (accOwnerProfileRow == null) {
                accOwnerProfileRow = new Row("AaaAccOwnerProfile");
                accOwnerProfileRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
                accOwnerProfileRow.set("ALLOWED_SUBACCOUNT", (Object)new Integer(0));
                accountDO.addRow(accOwnerProfileRow);
            }
            if (!accountDO.containsTable("AaaAccountOwner") && credential != null) {
                final Row accOwnerRow = new Row("AaaAccountOwner");
                accOwnerRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
                accOwnerRow.set("OWNERACCOUNT_ID", (Object)credential.getAccountId());
                accountDO.addRow(accOwnerRow);
            }
            final String accAdminProfile = (String)AuthDBUtil.getObject("AaaAccAdminProfile", "NAME", "ACCOUNTPROFILE_ID", accountRow.get("ACCOUNTPROFILE_ID"));
            final Row accStatusRow = constructAccStatusRow(accountRow, accAdminProfile);
            accountDO.addRow(accStatusRow);
        }
        final Iterator passItr = accountDO.getRows("AaaPassword");
        Row passwordRow = null;
        while (passItr.hasNext()) {
            passwordRow = passItr.next();
            Long passRuleId = (Long)passwordRow.get("PASSWDRULE_ID");
            final Long passProfileId = (Long)passwordRow.get("PASSWDPROFILE_ID");
            if (passRuleId == null) {
                final String[] serviceNames = getServiceNames(serviceIds);
                passRuleId = getCompatiblePassRuleId(serviceNames);
                passwordRow.set("PASSWDRULE_ID", (Object)passRuleId);
            }
            final Row passRuleRow = AuthDBUtil.getRowMatching("AaaPasswordRule", "PASSWDRULE_ID", passRuleId);
            validateForPasswordRule((String)loginRow.get("NAME"), (String)passwordRow.get("PASSWORD"), passRuleRow);
            final String passwordProfile = (String)AuthDBUtil.getObject("AaaPasswordProfile", "NAME", "PASSWDPROFILE_ID", passProfileId);
            final Row passProfileRow = AuthDBUtil.getRowMatching("AaaPasswordProfile", "NAME", passwordProfile);
            final Object workFactor = passProfileRow.get("FACTOR");
            if (!((String)passwordRow.get("ALGORITHM")).equalsIgnoreCase("bcrypt")) {
                AuthUtil.LOGGER.log(Level.INFO, "algorithm used to hash password should be bcrypt, hence updating algorithm as bcrypt");
                passwordRow.set("ALGORITHM", (Object)"bcrypt");
            }
            if (workFactor != null && Integer.parseInt(workFactor.toString()) > 0) {
                workload = Integer.parseInt(workFactor.toString());
            }
            else {
                workload = PAM.workload;
            }
            final String salt = BCrypt.gensalt(workload);
            final String encPass = getEncryptedPassword((String)passwordRow.get("PASSWORD"), salt, (String)passwordRow.get("ALGORITHM"));
            passwordRow.set("PASSWORD", (Object)encPass);
            passwordRow.set("FACTOR", (Object)workload);
            passwordRow.set("ALGORITHM", passwordRow.get("ALGORITHM"));
            passwordRow.set("CREATEDTIME", (Object)new Long(now));
            passwordRow.set("SALT", (Object)salt);
            accountDO.updateRow(passwordRow);
            final Row passStatusRow = constructPassStatusRow(passwordRow, passwordProfile);
            accountDO.addRow(passStatusRow);
        }
        AuthUtil.LOGGER.log(Level.FINEST, "account validated dataobject is : {0}", accountDO);
        final DataObject addedDO = AuthDBUtil.getPersistence("Persistence").add(accountDO);
        AuthUtil.LOGGER.log(Level.FINEST, "account added successfully");
        return addedDO;
    }
    
    public static String getPasswordRuleAsString(final String ruleName) throws DataAccessException {
        final Row passRuleRow = AuthDBUtil.getRowMatching("AaaPasswordRule", "NAME", ruleName);
        final int minLen = (int)passRuleRow.get("MIN_LENGTH");
        final int maxLen = (int)passRuleRow.get("MAX_LENGTH");
        final boolean loginNameIndep = (boolean)passRuleRow.get("LOGINNAME_INDEPNDT");
        final StringBuffer sb = new StringBuffer();
        sb.append("length ( " + minLen + " to " + maxLen + ")");
        if (loginNameIndep) {
            sb.append(", should not be same as loginname");
        }
        return sb.toString();
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
        final int count = getCount(itr);
        if (count == 0 || count > 1) {
            AuthUtil.LOGGER.log(Level.FINEST, "more than one password rule obtained. so use password rule mapped to service System");
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
        AuthUtil.LOGGER.log(Level.FINEST, "passwordrule row obtained matching services : {0} is : {1}", new Object[] { services, passRuleRow });
        return (Long)passRuleRow.get("PASSWDRULE_ID");
    }
    
    public static void createAuditRecord(final DataObject dobj, final Properties prop) throws AuditException {
        AuthUtil.LOGGER.log(Level.FINEST, "createAuditRecord invoked with dataobject : {0}", dobj);
        if (AuthUtil.auditInstance == null) {
            AuthUtil.auditInstance = Audit.getInstance("Authentication");
        }
        AuthUtil.auditInstance.createAuditRecord(dobj, (Hashtable)prop);
    }
    
    public static DataObject constructAuditRecordDO(final String loginname, final String service, final String hostname, final String result, final String operationname, final Properties prop) {
        try {
            final Row auditRecordRow = new Row("AuditRecord");
            auditRecordRow.set("PRINCIPAL", (Object)loginname);
            auditRecordRow.set("RECORDTYPE", (Object)"OperationAuditRecord");
            auditRecordRow.set("TIMESTAMP", (Object)new Long(System.currentTimeMillis()));
            final Row operAuditRecordRow = new Row("OperationAuditRecord");
            operAuditRecordRow.set("AUDITID", auditRecordRow.get("AUDITID"));
            operAuditRecordRow.set("HOSTNAME", (Object)hostname);
            operAuditRecordRow.set("OPERATIONNAME", (Object)operationname);
            operAuditRecordRow.set("RESOURCENAME", (Object)service);
            operAuditRecordRow.set("RESULT", (Object)result);
            operAuditRecordRow.set("STARTTIME", (Object)new Long(System.currentTimeMillis()));
            operAuditRecordRow.set("COMPLETIONTIME", (Object)new Long(System.currentTimeMillis()));
            final DataObject auditRecordDO = DataAccess.constructDataObject();
            auditRecordDO.addRow(auditRecordRow);
            auditRecordDO.addRow(operAuditRecordRow);
            return auditRecordDO;
        }
        catch (final Exception e) {
            AuthUtil.LOGGER.log(Level.FINEST, "Exception occured while constructing audit record", e);
            return null;
        }
    }
    
    public static void changePassword(final String loginName, final String serviceName, final String oldPassword, final String newPasswd, String domainName) throws PasswordException {
        try {
            domainName = (String)((domainName == null || domainName.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domainName);
        }
        catch (final MetaDataException e1) {
            AuthUtil.LOGGER.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        changePassword(loginName, serviceName, oldPassword, newPasswd, false, domainName);
    }
    
    public static void changePassword(final String loginName, final String serviceName, final String oldPassword, final String newPasswd) throws PasswordException {
        String domainName = null;
        try {
            domainName = (String)MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue();
        }
        catch (final MetaDataException e1) {
            AuthUtil.LOGGER.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        changePassword(loginName, serviceName, oldPassword, newPasswd, false, domainName);
    }
    
    public static void changePassword(final String loginName, final String serviceName, final String newPasswd) throws PasswordException {
        String domainName = null;
        try {
            domainName = (String)MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue();
        }
        catch (final MetaDataException e1) {
            AuthUtil.LOGGER.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        changePassword(loginName, serviceName, null, newPasswd, true, domainName);
    }
    
    public static void changePasswordForUser(final String loginName, final String serviceName, final String oldPassword, final String newPasswd, String domainName) throws PasswordException {
        try {
            domainName = (String)((domainName == null || domainName.trim().length() == 0) ? MetaDataUtil.getTableDefinitionByName("AaaLogin").getColumnDefinitionByName("DOMAINNAME").getDefaultValue() : domainName);
        }
        catch (final MetaDataException e1) {
            AuthUtil.LOGGER.info("Exception occured while obtaining default of [AAALOGIN.DOMAINNAME]");
            e1.printStackTrace();
        }
        changePassword(loginName, serviceName, oldPassword, newPasswd, oldPassword == null, domainName);
    }
    
    private static String[] getServiceNames(final List serviceIds) {
        final String[] serviceNames = new String[serviceIds.size()];
        try {
            final Criteria criteria = new Criteria(Column.getColumn("AaaService", "SERVICE_ID"), (Object)serviceIds.toArray(), 8);
            final DataObject dobj = DataAccess.get("AaaService", criteria);
            final Iterator itr = dobj.getRows("AaaService");
            Row serviceRow = null;
            int i = 0;
            while (itr.hasNext()) {
                serviceRow = itr.next();
                serviceNames[i] = (String)serviceRow.get("NAME");
                ++i;
            }
        }
        catch (final DataAccessException dae) {
            AuthUtil.LOGGER.log(Level.FINEST, "DataAccessException ocured while fetching service names from ids ", (Throwable)dae);
        }
        return serviceNames;
    }
    
    private static void validateForAccountCreation(final Long accountId, final int numOfAccToCreate) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccount", "parentAcc"));
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        sq.addJoin(new Join("AaaAccount", "AaaAccountOwner", new String[] { "ACCOUNT_ID" }, new String[] { "OWNERACCOUNT_ID" }, "parentAcc", "AaaAccountOwner", 1));
        sq.addJoin(new Join("AaaAccountOwner", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, "AaaAccountOwner", "childAcc", 1));
        sq.addJoin(new Join("AaaAccount", "AaaAccOwnerProfile", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, "parentAcc", "AaaAccOwnerProfile", 1));
        final Criteria criteria = new Criteria(Column.getColumn("parentAcc", "ACCOUNT_ID"), (Object)accountId, 0);
        sq.setCriteria(criteria);
        final DataObject dobj = DataAccess.get(sq);
        AuthUtil.LOGGER.log(Level.FINEST, "DataObject fetched for childaccounts : {0}", dobj);
        if (!dobj.containsTable("AaaAccOwnerProfile")) {
            throw new DataAccessException("No account profile configured");
        }
        final Row accOwnerProfileRow = dobj.getFirstRow("AaaAccOwnerProfile");
        final int allowedSubAcc = (int)accOwnerProfileRow.get("ALLOWED_SUBACCOUNT");
        if (allowedSubAcc == -1) {
            return;
        }
        final Iterator itr = dobj.getRows("childAcc");
        final int noOfChildAccAvble = getCount(itr);
        if (allowedSubAcc >= noOfChildAccAvble + numOfAccToCreate) {
            return;
        }
        AuthUtil.LOGGER.log(Level.WARNING, "user permitted to add only {0} accounts. Already {1} created", new Object[] { new Integer(allowedSubAcc), new Integer(noOfChildAccAvble) });
        throw new DataAccessException("Not permitted to add more than [" + allowedSubAcc + "] user accounts");
    }
    
    private static void changePassword(final String loginName, final String serviceName, final String oldPasswd, final String newPasswd, final boolean asAdmin, final String domainName) throws PasswordException {
        try {
            final DataObject accountDO = AuthDBUtil.getAccountPasswordDO(loginName, serviceName, domainName);
            String message = null;
            final long now = System.currentTimeMillis();
            int workload = 0;
            if (accountDO.isEmpty()) {
                message = "No account configured for user : " + loginName + " to access service : " + serviceName;
                AuthUtil.LOGGER.log(Level.FINEST, message);
                throw new PasswordException(message);
            }
            final Row passRow = accountDO.getFirstRow("currentPass");
            final Row passProfileRow = accountDO.getFirstRow("AaaPasswordProfile");
            final Row passRuleRow = accountDO.getFirstRow("AaaPasswordRule");
            final Row passStatusRow = accountDO.getFirstRow("currentPassStatus");
            if (!asAdmin) {
                validateOldPassword(passRow, oldPasswd);
            }
            validateForPasswordRule(loginName, newPasswd, passRuleRow);
            final int oldpass_remembered = (int)passProfileRow.get("NUMOF_OLDPASSWD");
            if (oldpass_remembered != -1) {
                final Iterator oldPassItr = accountDO.getRows("oldPass");
                if (!asAdmin) {
                    if (oldPasswd.equals(newPasswd)) {
                        throw new PasswordException("New password cannot be same as old password");
                    }
                    validateNewPassword(oldPassItr, newPasswd);
                }
                final Row newOldPassRow = new Row("AaaPassword");
                newOldPassRow.set("ALGORITHM", passRow.get("ALGORITHM"));
                newOldPassRow.set("CREATEDTIME", passRow.get("CREATEDTIME"));
                newOldPassRow.set("PASSWDPROFILE_ID", passRow.get("PASSWDPROFILE_ID"));
                newOldPassRow.set("PASSWDRULE_ID", passRow.get("PASSWDRULE_ID"));
                newOldPassRow.set("PASSWORD", passRow.get("PASSWORD"));
                newOldPassRow.set("SALT", passRow.get("SALT"));
                accountDO.addRow(newOldPassRow);
                final Row newOldPasswordStatusRow = new Row("AaaPasswordStatus");
                newOldPasswordStatusRow.set("PASSWORD_ID", newOldPassRow.get("PASSWORD_ID"));
                newOldPasswordStatusRow.set("STATUS", (Object)"OLD");
                newOldPasswordStatusRow.set("AFTEREXP_LOGIN", passStatusRow.get("AFTEREXP_LOGIN"));
                newOldPasswordStatusRow.set("EXPIREAT", passStatusRow.get("EXPIREAT"));
                newOldPasswordStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                accountDO.addRow(newOldPasswordStatusRow);
                final Row accPassRow = accountDO.getFirstRow("AaaAccPassword");
                final Row newAccOldPassRow = new Row("AaaAccOldPassword");
                newAccOldPassRow.set("ACCOUNT_ID", accPassRow.get("ACCOUNT_ID"));
                newAccOldPassRow.set("OLDPASSWORD_ID", newOldPassRow.get("PASSWORD_ID"));
                accountDO.addRow(newAccOldPassRow);
            }
            final String algorithm = (String)passRow.get("ALGORITHM");
            final Object workFactor = passRow.get("FACTOR");
            if (workFactor != null && Integer.parseInt(workFactor.toString()) > 0) {
                workload = Integer.parseInt(workFactor.toString());
            }
            else {
                workload = PAM.workload;
            }
            final String salt = algorithm.equals("bcrypt") ? BCrypt.gensalt(workload) : String.valueOf(now);
            final String pass = getEncryptedPassword(newPasswd, salt, algorithm);
            passRow.set("CREATEDTIME", (Object)new Long(now));
            passRow.set("PASSWORD", (Object)pass);
            passRow.set("SALT", (Object)salt);
            accountDO.updateRow(passRow);
            final int expPeriod = (int)passProfileRow.get("EXP_AFTER");
            long expAt = -1L;
            if (expPeriod != -1) {
                expAt = now + expPeriod * 24 * 60 * 60 * 1000L;
            }
            passStatusRow.set("AFTEREXP_LOGIN", (Object)new Integer(0));
            passStatusRow.set("EXPIREAT", (Object)new Long(expAt));
            passStatusRow.set("STATUS", (Object)"ACTIVE");
            passStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
            accountDO.updateRow(passStatusRow);
            removeInvalidOldPasswords(accountDO, (int)passProfileRow.get("NUMOF_OLDPASSWD"));
            AuthDBUtil.getPersistence("PurePersistence").update(accountDO);
        }
        catch (final DataAccessException dae) {
            AuthUtil.LOGGER.log(Level.SEVERE, "DataAccessException occured while trying to changed password ", (Throwable)dae);
            throw new PasswordException("Error occured while trying to change password");
        }
    }
    
    public static void validateForPasswordRule(final String loginName, final String newPasswd, final Row passRuleRow) throws PasswordException {
        final int minLen = (int)passRuleRow.get("MIN_LENGTH");
        final int maxLen = (int)passRuleRow.get("MAX_LENGTH");
        final boolean loginNameIndep = (boolean)passRuleRow.get("LOGINNAME_INDEPNDT");
        final boolean beginWithLetter = (boolean)passRuleRow.get("BEGINWITH_LETTER");
        final int numOfSplchars = (int)passRuleRow.get("NUMOF_SPLCHAR");
        final boolean reqMixedCase = (boolean)passRuleRow.get("REQ_MIXEDCASE");
        final int currSize = newPasswd.length();
        if (currSize < minLen) {
            throw new PasswordException("Length of the new password is smaller than expected value of : " + minLen);
        }
        if (currSize > maxLen) {
            throw new PasswordException("Length of the new password is greater than expected value of : " + maxLen);
        }
        if (loginNameIndep && loginName.equals(newPasswd)) {
            throw new PasswordException("password cannot be the same as loginname");
        }
        if (beginWithLetter && !Character.isAlphabetic(newPasswd.charAt(0))) {
            throw new PasswordException("password should begin with an alphabet");
        }
        if (numOfSplchars > 0 && newPasswd.replaceAll("\\p{Alnum}", "").length() < numOfSplchars) {
            throw new PasswordException("password should at least have " + numOfSplchars + " special characters");
        }
        if (reqMixedCase && (newPasswd.equals(newPasswd.toLowerCase()) || newPasswd.equals(newPasswd.toUpperCase()))) {
            throw new PasswordException("password should have both upper and lower case letters");
        }
    }
    
    private static void removeInvalidOldPasswords(final DataObject passDO, final int oldPasswordsReq) {
        try {
            if (oldPasswordsReq == -1) {
                return;
            }
            Iterator accOldPassItr = passDO.getRows("oldPass");
            final int count = getCount(accOldPassItr);
            if (count + 1 <= oldPasswordsReq) {
                return;
            }
            accOldPassItr = passDO.getRows("oldPass");
            long oldestOne = Long.MAX_VALUE;
            final HashMap temp = new HashMap();
            while (accOldPassItr.hasNext()) {
                final Row passRow = accOldPassItr.next();
                final long createtime = (long)passRow.get("CREATEDTIME");
                if (createtime < oldestOne) {
                    oldestOne = createtime;
                    temp.put(new Long(createtime), passRow);
                }
            }
            AuthUtil.LOGGER.log(Level.FINEST, "oldestOne obtained is : {0}", new Long(oldestOne));
            passDO.deleteRow((Row)temp.get(new Long(oldestOne)));
        }
        catch (final DataAccessException dae) {
            AuthUtil.LOGGER.log(Level.SEVERE, "DataAccessException occured while trying to remove invalid old password", (Throwable)dae);
        }
    }
    
    private static void validateOldPassword(final Row passRow, final String oldPasswd) throws PasswordException {
        final String salt = (String)passRow.get("SALT");
        final String algorithm = (String)passRow.get("ALGORITHM");
        final String password = (String)passRow.get("PASSWORD");
        final String oldPasswordEnc = getEncryptedPassword(oldPasswd, salt, algorithm);
        AuthUtil.LOGGER.log(Level.FINEST, "encrypted oldpassword : {0}", oldPasswordEnc);
        if (!password.equals(oldPasswordEnc)) {
            AuthUtil.LOGGER.log(Level.FINEST, "old password specified does not match with the value in db");
            throw new PasswordException("Old password specified does not match");
        }
    }
    
    private static void validateNewPassword(final Iterator oldPassItr, final String newPassword) throws PasswordException {
        Row oldPassRow = null;
        String oldPasswd = null;
        String salt = null;
        while (oldPassItr.hasNext()) {
            oldPassRow = oldPassItr.next();
            salt = (String)oldPassRow.get("SALT");
            oldPasswd = (String)oldPassRow.get("PASSWORD");
            final String algorithm = (String)oldPassRow.get("ALGORITHM");
            if (getEncryptedPassword(newPassword, salt, algorithm).equals(oldPasswd)) {
                throw new PasswordException("new password matches one of old password");
            }
        }
    }
    
    private static Row constructPassStatusRow(final Row passwordRow, final String passwordProfile) throws DataAccessException {
        final Row passProfileRow = AuthDBUtil.getRowMatching("AaaPasswordProfile", "NAME", passwordProfile);
        final int expPeriod = (int)passProfileRow.get("EXP_AFTER");
        long expAt = -1L;
        if (expPeriod != -1) {
            final long createdTime = (long)passwordRow.get("CREATEDTIME");
            expAt = createdTime + expPeriod * 24 * 60 * 60 * 1000L;
        }
        final Row passwordStatusRow = new Row("AaaPasswordStatus");
        passwordStatusRow.set("PASSWORD_ID", passwordRow.get("PASSWORD_ID"));
        passwordStatusRow.set("STATUS", (Object)"NEW");
        passwordStatusRow.set("EXPIREAT", (Object)new Long(expAt));
        passwordStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
        AuthUtil.LOGGER.log(Level.FINEST, "password status row constructed is : {0}", passwordStatusRow);
        return passwordStatusRow;
    }
    
    private static Row constructAccStatusRow(final Row accountRow, final String accAdminProfile) throws DataAccessException {
        final Row accProfileRow = AuthDBUtil.getRowMatching("AaaAccAdminProfile", "NAME", accAdminProfile);
        final int expPeriod = (int)accProfileRow.get("EXP_AFTER");
        long expAt = -1L;
        if (expPeriod != -1) {
            final long createdTime = (long)accountRow.get("CREATEDTIME");
            expAt = createdTime + expPeriod * 24 * 60 * 60 * 1000L;
        }
        final Row accStatusRow = new Row("AaaAccountStatus");
        accStatusRow.set("ACCOUNT_ID", accountRow.get("ACCOUNT_ID"));
        accStatusRow.set("STATUS", (Object)"NEW");
        accStatusRow.set("EXPIREAT", (Object)new Long(expAt));
        accStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
        AuthUtil.LOGGER.log(Level.FINEST, "account status row constructed is : {0}", new Object[] { accStatusRow });
        return accStatusRow;
    }
    
    private static int getCount(final Iterator itr) {
        int count = 0;
        while (itr.hasNext()) {
            ++count;
            itr.next();
        }
        return count;
    }
    
    private static int byte1(final byte[] buf, final int offset) {
        return (buf[offset] & 0xFC) >> 2;
    }
    
    private static int byte2(final byte[] buf, final int offset) {
        return (buf[offset] & 0x3) << 4 | (buf[offset + 1] & 0xF0) >>> 4;
    }
    
    private static int byte3(final byte[] buf, final int offset) {
        return (buf[offset + 1] & 0xF) << 2 | (buf[offset + 2] & 0xC0) >>> 6;
    }
    
    private static int byte4(final byte[] buf, final int offset) {
        return buf[offset + 2] & 0x3F;
    }
    
    private static void process(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[1024];
        int read = -1;
        int offset = 0;
        int count = 0;
        while ((read = in.read(buffer, offset, 1024 - offset)) > 0) {
            if (read >= 3) {
                for (read += offset, offset = 0; offset + 3 <= read; offset += 3) {
                    final int c1 = byte1(buffer, offset);
                    final int c2 = byte2(buffer, offset);
                    final int c3 = byte3(buffer, offset);
                    final int c4 = byte4(buffer, offset);
                    switch (count) {
                        case 73: {
                            out.write(AuthUtil.encoding[c1]);
                            out.write(AuthUtil.encoding[c2]);
                            out.write(AuthUtil.encoding[c3]);
                            out.write(10);
                            out.write(AuthUtil.encoding[c4]);
                            count = 1;
                            break;
                        }
                        case 74: {
                            out.write(AuthUtil.encoding[c1]);
                            out.write(AuthUtil.encoding[c2]);
                            out.write(10);
                            out.write(AuthUtil.encoding[c3]);
                            out.write(AuthUtil.encoding[c4]);
                            count = 2;
                            break;
                        }
                        case 75: {
                            out.write(AuthUtil.encoding[c1]);
                            out.write(10);
                            out.write(AuthUtil.encoding[c2]);
                            out.write(AuthUtil.encoding[c3]);
                            out.write(AuthUtil.encoding[c4]);
                            count = 3;
                            break;
                        }
                        case 76: {
                            out.write(10);
                            out.write(AuthUtil.encoding[c1]);
                            out.write(AuthUtil.encoding[c2]);
                            out.write(AuthUtil.encoding[c3]);
                            out.write(AuthUtil.encoding[c4]);
                            count = 4;
                            break;
                        }
                        default: {
                            out.write(AuthUtil.encoding[c1]);
                            out.write(AuthUtil.encoding[c2]);
                            out.write(AuthUtil.encoding[c3]);
                            out.write(AuthUtil.encoding[c4]);
                            count += 4;
                            break;
                        }
                    }
                }
                for (int i = 0; i < 3; ++i) {
                    buffer[i] = (byte)((i < read - offset) ? buffer[offset + i] : 0);
                }
                offset = read - offset;
            }
            else {
                offset += read;
            }
        }
        switch (offset) {
            case 1: {
                out.write(AuthUtil.encoding[byte1(buffer, 0)]);
                out.write(AuthUtil.encoding[byte2(buffer, 0)]);
                out.write(61);
                out.write(61);
                break;
            }
            case 2: {
                out.write(AuthUtil.encoding[byte1(buffer, 0)]);
                out.write(AuthUtil.encoding[byte2(buffer, 0)]);
                out.write(AuthUtil.encoding[byte3(buffer, 0)]);
                out.write(61);
                break;
            }
        }
    }
    
    public static byte[] convertToByteArray(final String str) {
        byte[] pass = null;
        try {
            pass = str.getBytes(AuthUtil.ENCODING_FORMAT);
        }
        catch (final Exception e) {
            pass = str.getBytes();
        }
        return pass;
    }
    
    public static String convertToString(final byte[] input) {
        String str = null;
        ByteArrayInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = new ByteArrayInputStream(input);
            outputStream = new ByteArrayOutputStream();
            process(inputStream, outputStream);
            str = outputStream.toString(AuthUtil.ENCODING_FORMAT);
        }
        catch (final Exception e) {
            AuthUtil.LOGGER.log(Level.SEVERE, "Exception occured while converting byte [] to string : ", e);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (final IOException e2) {
                AuthUtil.LOGGER.log(Level.SEVERE, "IOException occured while closing input and output streams", e2);
            }
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (final IOException e3) {
                AuthUtil.LOGGER.log(Level.SEVERE, "IOException occured while closing input and output streams", e3);
            }
        }
        return str;
    }
    
    public static String getEncryptedPassword(final String password, final String salt) {
        return getEncryptedPassword(password, salt, "MD5");
    }
    
    public static String getEncryptedPassword(final String password, final String salt, final String algorithm) {
        if (algorithm.equals("bcrypt")) {
            String hashedPassword = null;
            if (salt.matches("\\$2a\\$.*\\$.*")) {
                hashedPassword = BCrypt.hashpw(password, salt);
                return hashedPassword;
            }
            throw new IllegalArgumentException("Invalid Salt value.. To use Bcrypt hashing, salt should be generated through 'Bcrypt.genSalt(workload)' or in the form '$2a$(workload)$(22char)' ");
        }
        else {
            final byte[] password_ba = convertToByteArray(password);
            final byte[] salt_ba = convertToByteArray(salt);
            try {
                final MessageDigest md = MessageDigest.getInstance(algorithm);
                md.update(password_ba);
                md.update(salt_ba);
                final byte[] cipher = md.digest();
                return convertToString(cipher);
            }
            catch (final NoSuchAlgorithmException nsae) {
                AuthUtil.LOGGER.log(Level.SEVERE, "Exception occured when getting MessageDigest Instance for Algorithm {0}. Returning unencrypted Password", algorithm);
                return password;
            }
        }
    }
    
    public static void setUserCredential(final Credential credential) {
        AuthUtil.LOGGER.log(Level.FINEST, "setUserCredential invoked : {0}", credential);
        AuthUtil.credentialStack.push(credential);
    }
    
    public static Credential getUserCredential() {
        AuthUtil.LOGGER.log(Level.FINEST, "getUserCredential invoked");
        return AuthUtil.credentialStack.peek();
    }
    
    public static void flushCredentials() {
        AuthUtil.LOGGER.log(Level.FINEST, "flushCredentials invoked");
        AuthUtil.credentialStack.flush();
    }
    
    public static Credential transform(final WritableCredential wcr) {
        if (wcr == null) {
            return null;
        }
        final Credential cr = new Credential(wcr.getUserId(), wcr.getLoginId(), wcr.getAccountId(), wcr.getSessionId(), wcr.getLocaleId(), wcr.getLoginName(), wcr.getServiceName(), wcr.getHostName(), wcr.getTimeZone(), wcr.getLocale(), wcr.getRoles(), wcr.getDomainName());
        String authRuleName = wcr.getAuthRuleName();
        if (authRuleName == null) {
            try {
                authRuleName = getDefaultAuthRule();
            }
            catch (final Exception ex) {
                AuthUtil.LOGGER.log(Level.WARNING, "Exception occured while finding the default auth rule ", ex);
            }
        }
        cr.setAuthRuleName(authRuleName);
        return cr;
    }
    
    public static String encryptString(String string) {
        string = new StringBuffer().append(string).reverse().toString();
        final int[] bits = new int[string.length()];
        final StringBuffer whole = new StringBuffer();
        for (int k = 0; k < string.length(); ++k) {
            bits[k] = string.charAt(k) - '\u001c';
            final StringBuffer temp3 = new StringBuffer().append(bits[k]);
            while (temp3.length() != 2) {
                temp3.insert(0, "0");
            }
            whole.append(temp3.toString());
        }
        string = whole.toString();
        return baseConvertor(string);
    }
    
    public static String decryptString(String encodedString) throws Exception {
        encodedString = baseDeconvertor(encodedString);
        final StringBuffer temp2 = new StringBuffer().append(encodedString);
        final String[] strbits = new String[temp2.length() / 2];
        final int[] intbits = new int[temp2.length() / 2];
        StringBuffer fin = new StringBuffer();
        for (int k = 0; k < temp2.length() / 2; ++k) {
            strbits[k] = temp2.toString().substring(2 * k, 2 * k + 2);
            intbits[k] = Integer.parseInt(strbits[k]) + 28;
            fin.append((char)intbits[k]);
        }
        fin = new StringBuffer().append(fin).reverse();
        return fin.toString();
    }
    
    private static String baseConvertor(final String name1) {
        final StringBuffer encoded = new StringBuffer();
        final String[] base = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        base[43] = "I";
        base[44] = "J";
        base[45] = "K";
        base[46] = "L";
        base[47] = "M";
        base[48] = "N";
        base[49] = "O";
        base[50] = "P";
        base[51] = "Q";
        base[52] = "R";
        base[53] = "S";
        base[54] = "T";
        base[55] = "U";
        base[56] = "V";
        base[57] = "W";
        base[58] = "X";
        base[59] = "Y";
        long quotient = 0L;
        long reminder = 0L;
        final boolean check = false;
        int k1 = 0;
        String fame = new String(name1);
        for (int j1 = 0; j1 < name1.length(); j1 = j1 + k1 - 1, ++j1) {
            fame = name1.substring(j1);
            final StringBuffer part = new StringBuffer();
            int p = 0;
            k1 = 0;
            reminder = 0L;
            while (part.length() != 5 && k1 < fame.length()) {
                final String test = fame.substring(p, k1 + 1);
                if (test.equals("0")) {
                    part.append("0");
                    ++p;
                }
                else {
                    quotient = Long.parseLong(test) / 60L;
                    reminder = Long.parseLong(test) % 60L;
                    final StringBuffer elabtemp = new StringBuffer();
                    if (quotient != 0L) {
                        elabtemp.append(quotient);
                    }
                    final String elab = elabtemp.toString();
                    final StringBuffer temp1 = new StringBuffer();
                    for (int q = 0; q < elab.length(); ++q) {
                        if (elab.length() != q + 1 && !elab.substring(q, q + 1).equals("0") && Integer.parseInt(elab.substring(q, q + 2)) < 60) {
                            temp1.append(base[Integer.parseInt(elab.substring(q, q + 2))]);
                            ++q;
                        }
                        else {
                            temp1.append(Integer.parseInt(elab.substring(q, q + 1)));
                        }
                    }
                    if (temp1.length() == 5 - part.length() || k1 == fame.length() - 1) {
                        part.append(temp1.toString());
                    }
                }
                ++k1;
            }
            part.append(base[(int)reminder]);
            encoded.append(part.toString());
        }
        String toBeReturned = encoded.toString();
        for (int triplepos = 0; (triplepos = toBeReturned.indexOf("000")) != -1; toBeReturned = new StringBuffer(toBeReturned).replace(triplepos, triplepos + 3, "Z").toString()) {}
        return toBeReturned;
    }
    
    private static String baseDeconvertor(String input) throws Exception {
        final String[] base = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        base[43] = "I";
        base[44] = "J";
        base[45] = "K";
        base[46] = "L";
        base[47] = "M";
        base[48] = "N";
        base[49] = "O";
        base[50] = "P";
        base[51] = "Q";
        base[52] = "R";
        base[53] = "S";
        base[54] = "T";
        base[55] = "U";
        base[56] = "V";
        base[57] = "W";
        base[58] = "X";
        base[59] = "Y";
        int Zpos = 0;
        while ((Zpos = input.indexOf("Z")) != -1) {
            final String temp1 = input.substring(0, Zpos);
            final String temp2 = input.substring(Zpos + 1);
            input = new StringBuffer(temp1).append("000").append(temp2).toString();
        }
        StringBuffer answer = new StringBuffer();
        int k = 0;
        long reminder = 0L;
        for (int co = input.length() / 6; k < co; ++k) {
            final String part = input.substring(6 * k, 6 * k + 6);
            final StringBuffer partnum = new StringBuffer();
            boolean startnum = false;
            for (int i = 0; i < 5; ++i) {
                boolean isthere = false;
                int pos = 0;
                while (!isthere) {
                    if (part.substring(i, i + 1).equals(base[pos])) {
                        isthere = true;
                        partnum.append(pos);
                        if (pos == 0) {
                            if (!startnum) {
                                answer.append("0");
                            }
                        }
                        else {
                            startnum = true;
                        }
                    }
                    ++pos;
                }
            }
            boolean isthere2 = false;
            int pos2 = 0;
            while (!isthere2) {
                if (part.substring(5).equals(base[pos2])) {
                    isthere2 = true;
                    reminder = pos2;
                }
                ++pos2;
            }
            if (partnum.toString().equals("00000")) {
                if (reminder != 0L) {
                    final String tempo = new Long(reminder).toString();
                    final String temp3 = answer.toString().substring(0, answer.length() - tempo.length());
                    answer = new StringBuffer(temp3).append(tempo);
                }
            }
            else {
                answer.append(Long.parseLong(partnum.toString()) * 60L + reminder);
            }
        }
        if (input.length() % 6 != 0) {
            final String end = input.substring(6 * k);
            final StringBuffer partnum = new StringBuffer();
            if (end.length() > 1) {
                int j = 0;
                boolean startnum2 = false;
                for (j = 0; j < end.length() - 1; ++j) {
                    boolean isthere = false;
                    int pos = 0;
                    while (!isthere) {
                        if (end.substring(j, j + 1).equals(base[pos])) {
                            isthere = true;
                            partnum.append(pos);
                            if (pos == 0) {
                                if (!startnum2) {
                                    answer.append("0");
                                }
                            }
                            else {
                                startnum2 = true;
                            }
                        }
                        ++pos;
                    }
                }
                boolean isthere = false;
                int pos = 0;
                while (!isthere) {
                    if (end.substring(j).equals(base[pos])) {
                        isthere = true;
                        reminder = pos;
                    }
                    ++pos;
                }
                answer.append(Long.parseLong(partnum.toString()) * 60L + reminder);
            }
            else {
                boolean isthere3 = false;
                for (int pos3 = 0; !isthere3 && pos3 < base.length; ++pos3) {
                    if (end.equals(base[pos3])) {
                        isthere3 = true;
                        reminder = pos3;
                    }
                }
                answer.append(reminder);
            }
        }
        return answer.toString();
    }
    
    public static String getTwoFactorImplName(final Long userId) throws DataAccessException {
        return (String)AuthDBUtil.getObject("AaaUserTwoFactorDetails", "TWOFACTORAUTHIMPL", "USER_ID", userId);
    }
    
    public static boolean isTwofactorLoginEnabled(final Long userId) {
        Boolean enabled = new Boolean(false);
        if (System.getProperty("2factor.auth") != null) {
            enabled = new Boolean(true);
            try {
                enabled = isEnabled(userId);
            }
            catch (final Exception e) {
                return enabled;
            }
            return enabled;
        }
        try {
            enabled = isEnabled(userId);
        }
        catch (final Exception e) {
            return enabled;
        }
        return enabled;
    }
    
    public static Object createInstance(final String className) {
        Class klass = null;
        Object obj = null;
        try {
            final Thread currentThread = Thread.currentThread();
            final ClassLoader loader = currentThread.getContextClassLoader();
            klass = loader.loadClass(className);
            obj = klass.newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error In Creating Class" + className);
        }
        return obj;
    }
    
    public static Object getTwoFactorImpl(final Long userId) throws Exception {
        if (!isTwofactorLoginEnabled(userId)) {
            return null;
        }
        String implementation = null;
        if (System.getProperty("2factor.auth") != null) {
            implementation = System.getProperty("2factor.auth");
        }
        else {
            try {
                if (!isTwofactorLoginEnabled(userId)) {
                    return null;
                }
            }
            catch (final Exception e) {
                AuthUtil.LOGGER.log(Level.SEVERE, "Exception while reading db for userid" + userId);
            }
        }
        try {
            if (isTwofactorLoginEnabled(userId)) {
                implementation = getTwoFactorImplName(userId);
            }
        }
        catch (final Exception e) {
            if (implementation == null) {
                AuthUtil.LOGGER.log(Level.WARNING, "Cannot find implementation class for userid {0}", userId);
                return null;
            }
        }
        return createInstance(implementation);
    }
    
    private static boolean isEnabled(final Long userId) throws Exception {
        final ReadOnlyPersistence rp = (ReadOnlyPersistence)BeanUtil.lookup("Persistence");
        final Criteria c = new Criteria(Column.getColumn("AaaUserTwoFactorDetails", "USER_ID"), (Object)userId, 0);
        final Row r = rp.get("AaaUserTwoFactorDetails", c).getRow("AaaUserTwoFactorDetails");
        return r != null && (boolean)r.get("ENABLED");
    }
    
    @Deprecated
    public static boolean tryNtlmAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null) {
            response.setStatus(401);
            response.setHeader("WWW-Authenticate", "NTLM");
            response.flushBuffer();
            request.setAttribute("NTLMCOMM", (Object)"true");
            return false;
        }
        if (auth.startsWith("NTLM ")) {
            final byte[] msg = new BASE64Decoder().decodeBuffer(auth.substring(5));
            int off = 0;
            if (msg[8] == 1) {
                final Type1Message type1 = new Type1Message(msg);
                final String domain = type1.getSuppliedDomain();
                String domainController = null;
                if (domain != null) {
                    domainController = getDomainController(domain);
                    final UniAddress dc = UniAddress.getByName(domainController, true);
                    final byte[] challenge = SmbSession.getChallenge(dc);
                    final Type2Message type2 = new Type2Message(type1, challenge, (String)null);
                    auth = Base64.encode(type2.toByteArray());
                    response.setHeader("WWW-Authenticate", "NTLM " + auth);
                    response.sendError(401);
                    request.setAttribute("NTLMCOMM", (Object)"true");
                    return false;
                }
                return false;
            }
            else if (msg[8] == 3) {
                request.setAttribute("NTLMCOMM", (Object)"false");
                off = 30;
                final Type3Message type3 = new Type3Message(msg);
                byte[] lmResponse = type3.getLMResponse();
                if (lmResponse == null) {
                    lmResponse = new byte[0];
                }
                byte[] ntResponse = type3.getNTResponse();
                if (ntResponse == null) {
                    ntResponse = new byte[0];
                }
                int length = msg[off + 17] * 256 + msg[off + 16];
                int offset = msg[off + 19] * 256 + msg[off + 18];
                final String remoteHost = etrim(new String(msg, offset, length));
                length = msg[off + 1] * 256 + msg[off];
                offset = msg[off + 3] * 256 + msg[off + 2];
                final String domain2 = etrim(new String(msg, offset, length));
                length = msg[off + 9] * 256 + msg[off + 8];
                offset = msg[off + 11] * 256 + msg[off + 10];
                final String username = etrim(new String(msg, offset, length));
                String domainController2 = null;
                final Principal principal = request.getUserPrincipal();
                try {
                    if (principal == null) {
                        domainController2 = getDomainController(domain2);
                        final UniAddress dc2 = UniAddress.getByName(domainController2, true);
                        final byte[] challenge2 = SmbSession.getChallenge(dc2);
                        final NtlmPasswordAuthentication nt = NtlmSsp.authenticate(request, response, challenge2);
                        SmbSession.logon(dc2, nt);
                        request.setAttribute("domainName", (Object)domain2);
                        request.setAttribute("userName", (Object)username);
                        request.getSession().setAttribute("NTLM", (Object)"success");
                    }
                }
                catch (final SmbAuthException e) {
                    fixIENtlmPostProblem(request, response, msg);
                    AuthUtil.LOGGER.log(Level.SEVERE, "Failed to Login User {0} through NTLM", username);
                    return false;
                }
                catch (final Exception e2) {
                    fixIENtlmPostProblem(request, response, msg);
                    AuthUtil.LOGGER.log(Level.SEVERE, "Exception occured while trying NTLM authentication reason is {0}", e2.getMessage());
                    return false;
                }
                fixIENtlmPostProblem(request, response, msg);
                return true;
            }
        }
        return false;
    }
    
    public static String getDomainController(final String domain) {
        try {
            final Table baseTable = Table.getTable("ActiveDirectoryInfo");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            final Column confColumn = Column.getColumn("ActiveDirectoryInfo", "*");
            query.addSelectColumn(confColumn);
            final Column col = Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN");
            final Criteria crit = new Criteria(col, (Object)domain, 0, false);
            query.setCriteria(crit);
            final DataObject dob = ((Persistence)BeanUtil.lookup("Persistence")).get(query);
            final Iterator it = dob.getRows("ActiveDirectoryInfo");
            String domainController = null;
            while (it.hasNext()) {
                final Row rw = it.next();
                domainController = (String)rw.get("SERVERNAME");
            }
            return domainController;
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private static String etrim(final String str) {
        final int len = str.length();
        final StringBuffer returnString = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            if (i % 2 == 0) {
                returnString.append(str.charAt(i));
            }
        }
        return returnString.toString();
    }
    
    @Deprecated
    public static void fixIENtlmPostProblem(final HttpServletRequest request, final HttpServletResponse response, final byte[] msg) throws IOException {
        final HttpServletResponse _tmp = response;
        response.setStatus(401);
        request.setAttribute("NTLMAUTHEN", (Object)"success");
    }
    
    public static String getDefaultAuthRule() throws Exception {
        if (AuthUtil.defaultAuthRule == null) {
            final ReadOnlyPersistence rp = (ReadOnlyPersistence)BeanUtil.lookup("PureCachedPersistence");
            final SelectQuery sq = QueryConstructor.get("AaaPamModule", (Criteria)null);
            sq.setRange(new Range(0, 1));
            sq.addSortColumn(new SortColumn(Column.getColumn("AaaPamModule", "PAMMODULE_ID"), true));
            AuthUtil.LOGGER.log(Level.FINE, "Query constructed to fetch the default Authenticator is {0}", sq);
            final DataObject dob = rp.get(sq);
            AuthUtil.LOGGER.log(Level.FINE, "DataObject obtained for the default Authenticator is {0}", dob);
            AuthUtil.defaultAuthRule = (String)dob.getFirstValue("AaaPamModule", "NAME");
            AuthUtil.LOGGER.log(Level.FINE, "The default auth rule is {0}", AuthUtil.defaultAuthRule);
        }
        return AuthUtil.defaultAuthRule;
    }
    
    public static void unlockAccountForUser(final String loginName, final String domainName) throws Exception {
        unlockAccountForUser(loginName, domainName, "System");
    }
    
    public static void unlockAccountForUser(final String loginName, final String domainName, final String serviceName) throws Exception {
        DataObject accountDO = AuthDBUtil.getAccountDO(loginName, serviceName, domainName);
        final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("AaaAccBadLoginStatus");
        uq.setUpdateColumn("NUMOF_BADLOGIN", (Object)new Integer(0));
        uq.setCriteria(new Criteria(Column.getColumn("AaaAccBadLoginStatus", "ACCOUNT_ID"), accountDO.getRow("AaaAccount").get(1), 0));
        DataAccess.update(uq);
        final Row passStatusRow = accountDO.getFirstRow("AaaPasswordStatus");
        passStatusRow.set("STATUS", (Object)"ACTIVE");
        passStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
        accountDO.updateRow(passStatusRow);
        accountDO = DataAccess.update(accountDO);
        AuthUtil.LOGGER.log(Level.FINEST, "password status updated from BADLOGIN to ACTIVE");
    }
    
    public static RememberMeService getRememberMeService() {
        if (AuthUtil.rememberMeService != null) {
            return AuthUtil.rememberMeService;
        }
        String rememberMeServiceImpl = null;
        try {
            rememberMeServiceImpl = PersistenceInitializer.getConfigurationValue("RememberMeHandler");
            if (rememberMeServiceImpl == null || rememberMeServiceImpl.isEmpty()) {
                rememberMeServiceImpl = "com.adventnet.authentication.rememberme.RememberMeServiceImpl";
            }
            AuthUtil.LOGGER.log(Level.INFO, "rememberMeServiceImpl :: " + rememberMeServiceImpl);
            AuthUtil.rememberMeService = (RememberMeService)Class.forName(rememberMeServiceImpl).newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("exception occurred while initializing rememberMeService implementation class :: " + rememberMeServiceImpl);
        }
        return AuthUtil.rememberMeService;
    }
    
    public static String base64decode(final String input) {
        return toStringUtf8(org.apache.commons.codec.binary.Base64.decodeBase64(toBytesUtf8(input)));
    }
    
    public static String base64encode(final String input) {
        return toStringUtf8(org.apache.commons.codec.binary.Base64.encodeBase64(toBytesUtf8(input)));
    }
    
    private static String toStringUtf8(final byte[] byteArray) {
        try {
            return new String(byteArray, "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private static byte[] toBytesUtf8(final String str) {
        try {
            return str.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String getCookieValue(final HttpServletRequest request, final String cookieName) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie obj : cookies) {
                if (obj.getName().equals(cookieName)) {
                    return obj.getValue();
                }
            }
        }
        return null;
    }
    
    public static boolean hasValidSessions(final HttpServletRequest request) throws Exception {
        final Long userId = (getUserCredential() != null) ? getUserCredential().getUserId() : -1L;
        if (userId == null || userId == -1L) {
            throw new Exception("no active user present. User should be logged-in to know about his old sessions");
        }
        final String ssoId = getCookieValue(request, System.getProperty("org.apache.catalina.authenticator.Constants.SSO_SESSION_COOKIE_NAME", "JSESSIONIDSSO"));
        if (ssoId == null || ssoId.isEmpty()) {
            throw new Exception("request doesn't have sso cookie");
        }
        SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaLogin"));
        sq.addSelectColumn(Column.getColumn("AaaAccHttpSession", "SESSION_ID"));
        sq.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        sq.addJoin(new Join("AaaAccount", "AaaAccSession", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        sq.addJoin(new Join("AaaAccSession", "AaaAccHttpSession", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 2));
        Criteria cri = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userId, 0);
        cri = cri.and(new Criteria(Column.getColumn("AaaAccHttpSession", "SSO_ID"), (Object)ssoId, 1));
        sq.setCriteria(cri);
        DataObject dObj = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
        if (dObj.containsTable("AaaAccHttpSession")) {
            return true;
        }
        String rememberMeCookieName = PersistenceInitializer.getConfigurationValue("rememberMeCookieName");
        rememberMeCookieName = ((rememberMeCookieName != null) ? rememberMeCookieName : "nks85rfb9");
        final String cookieValue = getCookieValue(request, rememberMeCookieName);
        sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AAARememberMeInfo"));
        sq.addSelectColumn(Column.getColumn("AAARememberMeInfo", "ID"));
        cri = new Criteria(Column.getColumn("AAARememberMeInfo", "USER_ID"), (Object)userId, 0);
        cri.and(new Criteria(Column.getColumn("AAARememberMeInfo", "TOKEN"), (Object)cookieValue, 1));
        sq.setCriteria(cri);
        dObj = AuthDBUtil.getCachedPersistence("PureCachedPersistence").get(sq);
        return dObj.containsTable("AaaLogin");
    }
    
    public static void closeAllOtherSessions(final HttpServletRequest request) throws Exception {
        final Long userId = (getUserCredential() != null) ? getUserCredential().getUserId() : -1L;
        if (userId == null || userId == -1L) {
            throw new Exception("no active user present. User should be logged-in to clear his old sessions");
        }
        final String ssoId = getCookieValue(request, System.getProperty("org.apache.catalina.authenticator.Constants.SSO_SESSION_COOKIE_NAME", "JSESSIONIDSSO"));
        if (ssoId == null || ssoId.isEmpty()) {
            throw new Exception("request doesn't have sso cookie");
        }
        String rememberMeCookieName = PersistenceInitializer.getConfigurationValue("rememberMeCookieName");
        rememberMeCookieName = ((rememberMeCookieName != null) ? rememberMeCookieName : "nks85rfb9");
        closeSessions(userId, ssoId, getCookieValue(request, rememberMeCookieName));
    }
    
    public static void closeAllSessions(final String loginName, final String domainName) throws Exception {
        closeAllSessions(getUserId(loginName, domainName));
    }
    
    public static void closeAllSessions(final Long userId) throws Exception {
        if (userId == null || userId == -1L) {
            throw new Exception("userId is not valid.");
        }
        closeSessions(userId, null, null);
    }
    
    private static void closeSessions(final Long userId, final String ssoId, final String rememberMeCookie) throws Exception {
        DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AaaAccHttpSession");
        deleteQuery.addJoin(new Join("AaaAccHttpSession", "AaaAccSession", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 2));
        deleteQuery.addJoin(new Join("AaaAccSession", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        deleteQuery.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        Criteria cri = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userId, 0);
        if (ssoId != null) {
            cri = cri.and(new Criteria(Column.getColumn("AaaAccHttpSession", "SSO_ID"), (Object)ssoId, 1));
        }
        deleteQuery.setCriteria(cri);
        AuthDBUtil.getPersistence("Persistence").delete(deleteQuery);
        String rememberMeCookieName = PersistenceInitializer.getConfigurationValue("rememberMeCookieName");
        rememberMeCookieName = ((rememberMeCookieName != null) ? rememberMeCookieName : "nks85rfb9");
        cri = new Criteria(Column.getColumn("AAARememberMeInfo", "USER_ID"), (Object)userId, 0);
        if (rememberMeCookie != null) {
            cri = cri.and(new Criteria(Column.getColumn("AAARememberMeInfo", "TOKEN"), (Object)DigestUtils.sha256Hex(base64decode(rememberMeCookie)), 1));
        }
        deleteQuery = (DeleteQuery)new DeleteQueryImpl("AAARememberMeInfo");
        deleteQuery.setCriteria(cri);
        AuthDBUtil.getPersistence("Persistence").delete(deleteQuery);
    }
    
    static {
        LOGGER = Logger.getLogger(AuthUtil.class.getName());
        AuthUtil.auditInstance = null;
        AuthUtil.rememberMeService = null;
        AuthUtil.ENCODING_FORMAT = "ISO-8859-1";
        AuthUtil.encoding = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47, 61 };
        AuthUtil.credentialStack = new CredentialStack();
        AuthUtil.defaultAuthRule = null;
    }
    
    private static class CredentialStack extends ThreadLocal
    {
        private static final Logger LOGGER;
        
        @Override
        protected Object initialValue() {
            CredentialStack.LOGGER.log(Level.FINEST, "initialValue invoked");
            return new ArrayList();
        }
        
        public void push(final Credential credential) {
            final ArrayList credentialStack = super.get();
            credentialStack.add(credential);
        }
        
        public Credential pop() {
            final ArrayList credentialStack = super.get();
            final int stackSize = credentialStack.size();
            CredentialStack.LOGGER.log(Level.FINEST, "credentialStackSize in thread local is : {0}", new Integer(stackSize));
            final int lastIndex = stackSize - 1;
            Credential credential = null;
            if (lastIndex >= 0) {
                credential = credentialStack.remove(lastIndex);
            }
            return credential;
        }
        
        public Credential peek() {
            final ArrayList credentialStack = super.get();
            final int stackSize = credentialStack.size();
            CredentialStack.LOGGER.log(Level.FINEST, "credentialStackSize in thread local is : {0}", new Integer(stackSize));
            final int lastIndex = stackSize - 1;
            Credential credential = null;
            if (lastIndex >= 0) {
                credential = credentialStack.get(lastIndex);
            }
            return credential;
        }
        
        public void flush() {
            final ArrayList credentialStack = super.get();
            final int size = credentialStack.size();
            CredentialStack.LOGGER.log(Level.FINEST, "No of entries in stack to be cleaned = {0}", new Integer(size));
            credentialStack.clear();
        }
        
        static {
            LOGGER = Logger.getLogger(CredentialStack.class.getName());
        }
    }
}
