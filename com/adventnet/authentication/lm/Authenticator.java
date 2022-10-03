package com.adventnet.authentication.lm;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.authentication.PAMException;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.authentication.callback.SessionIdCallback;
import com.adventnet.authentication.internal.WritableCredential;
import java.util.List;
import com.adventnet.authentication.RolePrincipal;
import com.adventnet.authentication.UserPrincipal;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.authentication.callback.DomainCallback;
import com.adventnet.authentication.callback.ServletCallback;
import com.adventnet.authentication.callback.ServiceCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.logging.Logger;
import javax.security.auth.spi.LoginModule;

public class Authenticator implements LoginModule
{
    protected static Logger logger;
    protected Subject subject;
    protected CallbackHandler callbackHandler;
    protected Principal principal;
    protected boolean committed;
    protected String loginName;
    protected String password;
    protected String serviceName;
    protected String domainName;
    protected HttpServletRequest request;
    protected String hostName;
    protected String serverHostName;
    protected DataObject accountDO;
    protected boolean updateBadLogin;
    
    public Authenticator() {
        this.subject = null;
        this.callbackHandler = null;
        this.principal = null;
        this.committed = false;
        this.loginName = null;
        this.password = null;
        this.serviceName = null;
        this.domainName = null;
        this.request = null;
        this.hostName = null;
        this.serverHostName = null;
        this.accountDO = null;
        this.updateBadLogin = true;
        Authenticator.logger.log(Level.FINEST, "constructor invoked");
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState, final Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        Authenticator.logger.log(Level.FINEST, "intialized with subject : {0}, cbh : {1}, sharedState : {2} & options : {3}", new Object[] { subject, callbackHandler, sharedState, options });
    }
    
    @Override
    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("No callbackHandler specified");
        }
        final Callback[] callbacks = { new NameCallback("Login name : ", "loginname"), new PasswordCallback("Password : ", false), new ServiceCallback(), new ServletCallback(), new DomainCallback() };
        try {
            this.callbackHandler.handle(callbacks);
            this.loginName = ((NameCallback)callbacks[0]).getName();
            this.password = new String(((PasswordCallback)callbacks[1]).getPassword());
            this.serviceName = ((ServiceCallback)callbacks[2]).getServiceName();
            this.request = ((ServletCallback)callbacks[3]).getHttpServletRequest();
            this.domainName = ((DomainCallback)callbacks[4]).getDomainName();
            Authenticator.logger.log(Level.FINE, "domainName :: [{0}]", this.domainName);
            this.serverHostName = ((this.request == null) ? AuthUtil.getLocalHostName() : this.request.getServerName());
            this.hostName = ((this.request == null) ? this.serverHostName : this.request.getRemoteHost());
            Authenticator.logger.log(Level.FINEST, "loginName obtained via callbacks are : *****");
        }
        catch (final Exception e) {
            Authenticator.logger.log(Level.SEVERE, "Error occured while handling callbacks: ", e);
            throw new LoginException("Error occured while handling callbacks");
        }
        if (!this.authenticate()) {
            Authenticator.logger.log(Level.SEVERE, "authenticate failed for the user [{0}], and hence returning return false", this.loginName);
            return false;
        }
        this.principal = new UserPrincipal(this.loginName);
        Authenticator.logger.log(Level.FINEST, "principal object constructed is : {0}", this.principal);
        return true;
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (this.principal == null) {
            Authenticator.logger.log(Level.SEVERE, "principal obj is null, commit failed");
            return false;
        }
        if (!this.subject.getPrincipals().contains(this.principal)) {
            this.subject.getPrincipals().add(this.principal);
        }
        this.removeBadLoginStatusIfPresent();
        final List roleList = this.getRoles();
        final WritableCredential wcr = this.constructWritableCredential();
        wcr.setHostName(this.hostName);
        String roleName = null;
        for (int noOfRoles = roleList.size(), i = 0; i < noOfRoles; ++i) {
            roleName = roleList.get(i);
            this.subject.getPrincipals().add(new RolePrincipal(roleName));
            wcr.addRole(roleName);
        }
        final Long sessionId = this.createAccSession();
        wcr.setSessionId(sessionId);
        this.subject.getPublicCredentials().add(wcr);
        Authenticator.logger.log(Level.FINEST, "[{0}] successfully logged in from the host [{1}]", new Object[] { this.principal.getName(), this.hostName });
        return this.committed = true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        Authenticator.logger.log(Level.FINEST, "logout invoked");
        if (this.callbackHandler == null) {
            throw new LoginException("No callbackHandler specified");
        }
        final Callback[] callbacks = { new SessionIdCallback() };
        Long sessionId = null;
        try {
            this.callbackHandler.handle(callbacks);
            sessionId = ((SessionIdCallback)callbacks[0]).getSessionId();
            Authenticator.logger.log(Level.FINEST, "sessionId obtained via callback is : {0}", sessionId);
        }
        catch (final Exception e) {
            Authenticator.logger.log(Level.FINEST, "Error occured while handling callbacks: ", e);
            throw new LoginException("Error occured while handling callbacks");
        }
        this.closeAccSession(sessionId);
        return true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        Authenticator.logger.log(Level.FINEST, "abort invoked");
        if (this.updateBadLogin) {
            this.updateBadLoginCount();
        }
        if (this.committed) {
            this.logout();
        }
        final WritableCredential wcr = new WritableCredential();
        wcr.setHostName(this.hostName);
        wcr.setLoginName(this.loginName);
        wcr.setServiceName(this.serviceName);
        wcr.setDomainName(this.domainName);
        this.subject.getPublicCredentials().add(wcr);
        Authenticator.logger.log(Level.FINEST, "Login failed for [{0}] from the host [{1}]", new Object[] { this.loginName, this.hostName });
        this.principal = null;
        return true;
    }
    
    public boolean authenticate() throws LoginException {
        Authenticator.logger.log(Level.FINE, "authenticate invoked with loginName [{0}], serviceName :: [{1}], domainName :: [{2}]", new Object[] { "*****", this.serviceName, this.domainName });
        Row passwordRow = null;
        try {
            this.accountDO = AuthDBUtil.getAccountDO(this.loginName, this.serviceName, this.domainName);
            Authenticator.logger.log(Level.FINE, "accountDO :: {0}", this.accountDO);
            if (this.accountDO == null) {
                throw new PAMException("Account DO fetched is null for the loginName [" + this.loginName + "]");
            }
            if (!this.accountDO.containsTable("AaaAccount")) {
                throw new LoginException("No such account configured for the user [" + this.loginName + "]");
            }
            if (!this.accountDO.containsTable("AaaPassword")) {
                throw new LoginException("No password configured for this account");
            }
            final Row pstatsRow = this.accountDO.getRow("AaaPasswordStatus");
            if (pstatsRow != null && pstatsRow.get("STATUS").equals("BADLOGIN")) {
                final int updateInterval = (int)this.accountDO.getFirstValue("AaaPasswordProfile", "UPDATE_INTERVAL");
                final long statusUpdatedTime = (long)this.accountDO.getFirstValue("AaaPasswordStatus", "UPDATEDTIME");
                if (updateInterval == -1 || updateInterval <= 0 || statusUpdatedTime + updateInterval * 60 * 1000L >= System.currentTimeMillis()) {
                    Authenticator.logger.log(Level.INFO, "current status of user is BADLOGIN. cannot login until reset");
                    throw new LoginException("Account has been locked due to BADLOGINS. Contact the administrator.");
                }
                Authenticator.logger.log(Level.FINEST, "time duration has elapsed. The status can be reset as ACTIVE");
                final Row passStatusRow = this.accountDO.getFirstRow("AaaPasswordStatus");
                passStatusRow.set("STATUS", (Object)"ACTIVE");
                passStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.accountDO.updateRow(passStatusRow);
                this.accountDO = DataAccess.update(this.accountDO);
                Authenticator.logger.log(Level.FINEST, "password status updated from BADLOGIN to ACTIVE");
            }
            passwordRow = this.accountDO.getFirstRow("AaaPassword");
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
            throw new PAMException("DataAccessException occured while fetching account Dataobject", (Exception)dae);
        }
        final String passwordFromDb = (String)passwordRow.get("PASSWORD");
        final String salt = (String)passwordRow.get("SALT");
        final String algorithm = (String)passwordRow.get("ALGORITHM");
        final String encPassword = AuthUtil.getEncryptedPassword(this.password, salt, algorithm);
        final boolean result = passwordFromDb.equals(encPassword);
        if (!result) {
            throw new LoginException("Invalid loginName/password");
        }
        return result;
    }
    
    protected void removeBadLoginStatusIfPresent() throws PAMException {
        try {
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("AaaAccBadLoginStatus");
            uq.setUpdateColumn("NUMOF_BADLOGIN", (Object)new Integer(0));
            uq.setCriteria(new Criteria(Column.getColumn("AaaAccBadLoginStatus", "ACCOUNT_ID"), this.accountDO.getRow("AaaAccount").get(1), 0));
            DataAccess.update(uq);
        }
        catch (final DataAccessException dae) {
            throw new PAMException("DataAccessExcepion occured while remove bad login status", (Exception)dae);
        }
    }
    
    protected void updateBadLoginCount() throws PAMException {
        try {
            final Row accAdminProfileRow = this.accountDO.getFirstRow("AaaAccAdminProfile");
            final Row pwdStatusRow = this.accountDO.getRow("AaaPasswordStatus");
            if (pwdStatusRow.get(4).equals("BADLOGIN")) {
                this.request.setAttribute("account_status", (Object)"BADLOGIN");
                this.request.setAttribute("login_attempts_left", (Object)0);
                return;
            }
            final int allowBadLoginCount = (int)accAdminProfileRow.get(10);
            final Row badLoginRow = new Row("AaaAccBadLoginStatus");
            badLoginRow.set(1, this.accountDO.getFirstValue("AaaAccount", 1));
            badLoginRow.set(3, (Object)this.hostName);
            badLoginRow.set(2, (Object)new Integer(1));
            badLoginRow.set(4, (Object)new Long(System.currentTimeMillis()));
            badLoginRow.set(5, (Object)this.hostName);
            this.accountDO.addRow(badLoginRow);
            final int totalBadLogins = this.accountDO.size("AaaAccBadLoginStatus");
            if (allowBadLoginCount >= 0 && totalBadLogins >= allowBadLoginCount) {
                pwdStatusRow.set(4, (Object)"BADLOGIN");
                pwdStatusRow.set(5, (Object)new Long(System.currentTimeMillis()));
                this.accountDO.updateRow(pwdStatusRow);
            }
            this.request.setAttribute("account_status", pwdStatusRow.get("STATUS"));
            int loginAttemptsLeft = allowBadLoginCount - totalBadLogins;
            loginAttemptsLeft = ((loginAttemptsLeft < 0) ? 0 : loginAttemptsLeft);
            this.request.setAttribute("login_attempts_left", (Object)loginAttemptsLeft);
            ((Persistence)BeanUtil.lookup("Persistence")).update(this.accountDO);
        }
        catch (final Exception e) {
            Authenticator.logger.log(Level.WARNING, "Exception occured while updating BadLoginCount." + e);
        }
    }
    
    protected List getRoles() {
        List roleList = new ArrayList();
        try {
            roleList = AuthDBUtil.getAuthorizedRoles(this.accountDO);
        }
        catch (final DataAccessException dae) {
            Authenticator.logger.log(Level.SEVERE, "DataAccessException occured while getRoles : ", (Throwable)dae);
        }
        return roleList;
    }
    
    protected WritableCredential constructWritableCredential() throws PAMException {
        final WritableCredential wcr = new WritableCredential();
        try {
            Row temp = null;
            if (this.accountDO.containsTable("AaaLogin")) {
                temp = this.accountDO.getFirstRow("AaaLogin");
                wcr.setUserId((Long)temp.get("USER_ID"));
                wcr.setLoginId((Long)temp.get("LOGIN_ID"));
                wcr.setLoginName((String)temp.get("NAME"));
                wcr.setDomainName((String)temp.get("DOMAINNAME"));
            }
            if (this.accountDO.containsTable("AaaAccount")) {
                temp = this.accountDO.getFirstRow("AaaAccount");
                wcr.setAccountId((Long)temp.get("ACCOUNT_ID"));
            }
            if (this.accountDO.containsTable("AaaService")) {
                temp = this.accountDO.getFirstRow("AaaService");
                wcr.setServiceName((String)temp.get("NAME"));
            }
            if (this.accountDO.containsTable("AaaUserProfile")) {
                temp = this.accountDO.getFirstRow("AaaUserProfile");
                wcr.setTimeZone((String)temp.get("TIMEZONE"));
                wcr.setCountryCode((String)temp.get("COUNTRY_CODE"));
                wcr.setLangCode((String)temp.get("LANGUAGE_CODE"));
            }
        }
        catch (final DataAccessException dae) {
            throw new PAMException("DataAccessException occured while constructing Wr.credential", (Exception)dae);
        }
        return wcr;
    }
    
    protected Long createAccSession() throws PAMException {
        try {
            final Row sessionRow = new Row("AaaAccSession");
            sessionRow.set("ACCOUNT_ID", this.accountDO.getFirstValue("AaaAccount", "ACCOUNT_ID"));
            sessionRow.set("APPLICATION_HOST", (Object)this.serverHostName);
            sessionRow.set("OPENTIME", (Object)new Long(System.currentTimeMillis()));
            sessionRow.set("USER_HOST", (Object)this.hostName);
            sessionRow.set("USER_HOST_NAME", (Object)this.hostName);
            sessionRow.set("STATUS", (Object)"ACTIVE");
            if ("success".equals(this.request.getSession().getAttribute("NTLM"))) {
                sessionRow.set("AUTHENTICATOR", (Object)"NTLM");
            }
            DataObject sessionDO = DataAccess.constructDataObject();
            sessionDO.addRow(sessionRow);
            sessionDO = AuthDBUtil.getPersistence("PurePersistence").add(sessionDO);
            return (Long)sessionDO.getFirstValue("AaaAccSession", "SESSION_ID");
        }
        catch (final Exception e) {
            throw new PAMException("Exception occured while creating account session", e);
        }
    }
    
    protected void closeAccSession(final Long sessionId) throws PAMException {
        Authenticator.logger.log(Level.FINEST, "closeAccSession for id : {0}", sessionId);
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.addJoin(new Join("AaaAccSession", "AaaAccHttpSession", new String[] { "SESSION_ID" }, new String[] { "SESSION_ID" }, 1));
            sq.setCriteria(new Criteria(Column.getColumn("AaaAccSession", "SESSION_ID"), (Object)sessionId, 0));
            final DataObject dobj = DataAccess.get(sq);
            if (dobj.containsTable("AaaAccHttpSession")) {
                final Row row = dobj.getFirstRow("AaaAccHttpSession");
                dobj.deleteRow(row);
            }
            dobj.set("AaaAccSession", "CLOSETIME", (Object)new Long(System.currentTimeMillis()));
            dobj.set("AaaAccSession", "STATUS", (Object)"CLOSED");
            AuthDBUtil.getPersistence("PurePersistence").update(dobj);
            Authenticator.logger.log(Level.FINEST, "The user [{0}] has sucessfully logged out from the host [{1}]", new Object[] { "*****", this.hostName });
        }
        catch (final Exception e) {
            throw new PAMException("Exception occured while closing account session", e);
        }
    }
    
    static {
        Authenticator.logger = Logger.getLogger(Authenticator.class.getName());
    }
}
