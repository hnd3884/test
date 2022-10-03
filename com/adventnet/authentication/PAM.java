package com.adventnet.authentication;

import com.adventnet.persistence.PersistenceInitializer;
import java.util.Set;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import com.adventnet.authentication.realm.CustomJAASRealm;
import com.adventnet.authentication.util.AuthUtil;
import java.security.Principal;
import org.apache.catalina.connector.Request;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.internal.WritableCredential;
import com.adventnet.persistence.DataObject;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import javax.security.auth.login.Configuration;
import com.adventnet.authentication.config.AuthConfiguration;
import java.util.logging.Logger;

public abstract class PAM
{
    private static Logger logger;
    private static String pamImplClassName;
    private static PAM pamImpl;
    public static int workload;
    public static final String DOMAINNAME;
    public static final String LOGINACTION = "j_security_check";
    public static final String TWOFACTORLOGINACTION = "two_factor_auth";
    public static final String AUTHRULENAME = "AUTHRULE_NAME";
    public static final String ADAUTHENTICATOR = "ADAuthenticator";
    public static final String USERNAME = "j_username";
    public static final String PASSWORD = "j_password";
    public static final String TWOFACTORPASSWORD = "2factor_password";
    
    public static void init(final String serviceName) throws PAMException {
        Configuration.setConfiguration(new AuthConfiguration(serviceName));
        if (PAM.pamImplClassName == null) {
            PAM.pamImplClassName = System.getProperty("com.adventnet.authentication.pam.impl", "com.adventnet.authentication.internal.PAMImpl");
            PAM.logger.log(Level.FINEST, "pamImpl class to be used is : {0}", PAM.pamImplClassName);
        }
        try {
            PAM.pamImpl = (PAM)PAM.class.getClassLoader().loadClass(PAM.pamImplClassName).newInstance();
            PAM.logger.log(Level.FINEST, "pamImpl instance created is : {0}", PAM.pamImpl);
        }
        catch (final Exception e) {
            throw new PAMException("Unable to instantiate pamImpl : " + PAM.pamImplClassName, e);
        }
    }
    
    public static void init() throws PAMException {
        init(null);
    }
    
    public abstract Subject validate(final String p0, final String p1, final String p2, final HttpServletRequest p3) throws PAMException, LoginException;
    
    public abstract void inValidate(final Long p0) throws PAMException, LoginException;
    
    public static Subject login(final String loginName, final String password, final String service) throws PAMException, LoginException {
        return login(loginName, password, service, null);
    }
    
    private static WritableCredential constructWritableCredential(final DataObject accountDO, final HttpServletRequest request) throws PAMException {
        final WritableCredential wcr = new WritableCredential();
        final String hostName = request.getRemoteHost();
        final String serverHostName = request.getServerName();
        wcr.setHostName(hostName);
        try {
            Row temp = null;
            if (accountDO.containsTable("AaaLogin")) {
                temp = accountDO.getFirstRow("AaaLogin");
                wcr.setUserId((Long)temp.get(2));
                wcr.setLoginId((Long)temp.get(1));
                wcr.setLoginName((String)temp.get(3));
                wcr.setDomainName((String)temp.get(4));
            }
            if (accountDO.containsTable("AaaAccount")) {
                temp = accountDO.getFirstRow("AaaAccount");
                wcr.setAccountId((Long)temp.get(1));
            }
            if (accountDO.containsTable("AaaService")) {
                temp = accountDO.getFirstRow("AaaService");
                wcr.setServiceName((String)temp.get(2));
            }
            if (accountDO.containsTable("AaaUserProfile")) {
                temp = accountDO.getFirstRow("AaaUserProfile");
                wcr.setTimeZone((String)temp.get(5));
                wcr.setCountryCode((String)temp.get(7));
                wcr.setLangCode((String)temp.get(6));
            }
        }
        catch (final DataAccessException dae) {
            throw new PAMException("DataAccessException occured while constructing Wr.credential", (Exception)dae);
        }
        try {
            final Row sessionRow = new Row("AaaAccSession");
            sessionRow.set(2, accountDO.getFirstValue("AaaAccount", "ACCOUNT_ID"));
            sessionRow.set(4, (Object)serverHostName);
            sessionRow.set(5, (Object)new Long(System.currentTimeMillis()));
            sessionRow.set(3, (Object)hostName);
            sessionRow.set(8, (Object)hostName);
            sessionRow.set(7, (Object)"ACTIVE");
            if ("success".equals(request.getSession().getAttribute("NTLM"))) {
                sessionRow.set("AUTHENTICATOR", (Object)"NTLM");
            }
            DataObject sessionDO = DataAccess.constructDataObject();
            sessionDO.addRow(sessionRow);
            sessionDO = AuthDBUtil.getPersistence("PurePersistence").add(sessionDO);
            final Long sessionId = (Long)sessionDO.getFirstValue("AaaAccSession", "SESSION_ID");
            wcr.setSessionId(sessionId);
            final String authRuleName = request.getParameter("AUTHRULE_NAME");
            wcr.setAuthRuleName(authRuleName);
            request.getSession().setAttribute("AUTHRULE_NAME", (Object)authRuleName);
        }
        catch (final Exception e) {
            throw new PAMException("Exception occured while creating account session", e);
        }
        return wcr;
    }
    
    public static Principal login(final String loginName, final String service, final Request hreq) throws PAMException, LoginException, DataAccessException {
        final HttpServletRequest request = hreq.getRequest();
        String domainName = request.getParameter(PAM.DOMAINNAME);
        if (request.getAttribute("domainName") != null) {
            domainName = request.getAttribute("domainName").toString();
        }
        final DataObject accountDO = AuthDBUtil.getAccountDO(loginName, service, domainName);
        final List roleList = AuthDBUtil.getAuthorizedRoles(accountDO);
        final Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(loginName));
        final WritableCredential wcr = constructWritableCredential(accountDO, request);
        String roleName = null;
        for (int noOfRoles = roleList.size(), i = 0; i < noOfRoles; ++i) {
            roleName = roleList.get(i);
            subject.getPrincipals().add(new RolePrincipal(roleName));
            wcr.addRole(roleName);
        }
        final Credential cr = AuthUtil.transform(wcr);
        PAM.logger.log(Level.FINE, "Credential obtained after authentication : {0}", cr);
        if (cr != null) {
            AuthUtil.setUserCredential(cr);
            subject.getPublicCredentials().add(cr);
            PAM.logger.log(Level.INFO, "credential obj set as attribute to request");
            request.getSession().setAttribute("com.adventnet.authentication.Credential", (Object)cr);
        }
        request.setAttribute("login_status", (Object)"Authenticated");
        final CustomJAASRealm realm = new CustomJAASRealm();
        final Principal principal = realm.createPrincipal(loginName, subject);
        PAM.logger.log(Level.INFO, "principal object constructed for loginName : {0} is : {1}", new Object[] { "*****", principal });
        if (principal == null) {
            PAM.logger.log(Level.INFO, "authentication failed for user (principal is null) : {0}", loginName);
            return null;
        }
        PAM.logger.log(Level.INFO, "successfully authenticated user : ***** ");
        final String ssoId = (String)hreq.getNote("org.apache.catalina.request.SSOID");
        if (ssoId != null) {
            request.getSession().setAttribute("JSESSIONIDSSO", (Object)ssoId);
            AuthDBUtil.associateSsoSession(ssoId, cr);
        }
        return principal;
    }
    
    public static Principal login(final String loginName, final String service, final HttpServletRequest request) throws PAMException, LoginException, DataAccessException {
        String domainName = request.getParameter(PAM.DOMAINNAME);
        if (request.getAttribute("domainName") != null) {
            domainName = request.getAttribute("domainName").toString();
        }
        final DataObject accountDO = AuthDBUtil.getAccountDO(loginName, service, domainName);
        final List roleList = AuthDBUtil.getAuthorizedRoles(accountDO);
        final Subject subject = new Subject();
        subject.getPrincipals().add(new UserPrincipal(loginName));
        final WritableCredential wcr = constructWritableCredential(accountDO, request);
        String roleName = null;
        for (int noOfRoles = roleList.size(), i = 0; i < noOfRoles; ++i) {
            roleName = roleList.get(i);
            subject.getPrincipals().add(new RolePrincipal(roleName));
            wcr.addRole(roleName);
        }
        final Credential cr = AuthUtil.transform(wcr);
        PAM.logger.log(Level.FINE, "Credential obtained after authentication : {0}", cr);
        if (cr != null) {
            AuthUtil.setUserCredential(cr);
            subject.getPublicCredentials().add(cr);
            PAM.logger.log(Level.INFO, "credential obj set as attribute to request");
            request.getSession().setAttribute("com.adventnet.authentication.Credential", (Object)cr);
        }
        request.setAttribute("login_status", (Object)"Authenticated");
        final CustomJAASRealm realm = new CustomJAASRealm();
        final Principal principal = realm.createPrincipal(loginName, subject);
        PAM.logger.log(Level.INFO, "principal object constructed for loginName : {0} is : {1}", new Object[] { "*****", principal });
        if (principal == null) {
            PAM.logger.log(Level.INFO, "authentication failed for user (principal is null) : {0}", loginName);
            return null;
        }
        PAM.logger.log(Level.INFO, "successfully authenticated user : ***** ");
        if (request instanceof Request) {
            final Request hreq = (Request)request;
            final String ssoId = (String)hreq.getNote("org.apache.catalina.request.SSOID");
            if (ssoId != null) {
                request.getSession().setAttribute("JSESSIONIDSSO", (Object)ssoId);
                AuthDBUtil.associateSsoSession(ssoId, cr);
            }
        }
        return principal;
    }
    
    public static Subject login(final String loginName, final String password, String service, final HttpServletRequest request) throws PAMException, LoginException {
        if (loginName == null) {
            throw new LoginException("login name cannot be null");
        }
        if (password == null) {
            throw new LoginException("password cannot be null");
        }
        if (service == null) {
            PAM.logger.log(Level.INFO, "service name passed is null, assuming service as 'System'");
            service = "System";
        }
        init(service);
        final Subject subject = PAM.pamImpl.validate(loginName, password, service, request);
        if (subject == null) {
            return null;
        }
        PAM.logger.log(Level.FINEST, "Subject obtained after authentication is : {0}", subject);
        final WritableCredential wcr = getWritableCredential(subject);
        final String authRuleName = request.getParameter("AUTHRULE_NAME");
        wcr.setAuthRuleName(authRuleName);
        request.getSession().setAttribute("AUTHRULE_NAME", (Object)authRuleName);
        final Credential cr = AuthUtil.transform(wcr);
        PAM.logger.log(Level.INFO, "Credential obtained after authentication : {0}", cr);
        if (cr != null) {
            AuthUtil.setUserCredential(cr);
            subject.getPublicCredentials().add(cr);
        }
        final Object auth_bypass = request.getAttribute("AUTHENTICATE_BYEPASS");
        if (auth_bypass != null) {
            if (!auth_bypass.equals(Boolean.FALSE)) {
                return subject;
            }
        }
        try {
            final DataObject accDo = AuthDBUtil.getAccountDO(loginName, service, request.getParameter(PAM.DOMAINNAME));
            final Row pwdProfileRow = accDo.getFirstRow("AaaPasswordProfile");
            final Object workFactor = pwdProfileRow.get("FACTOR");
            if (workFactor != null && Integer.parseInt(workFactor.toString()) > 0) {
                PAM.workload = Integer.parseInt(workFactor.toString());
            }
            String salt = null;
            final Row passwordRow = accDo.getFirstRow("AaaPassword");
            final String algo = (String)passwordRow.get("ALGORITHM");
            if (!algo.equalsIgnoreCase("bcrypt")) {
                salt = BCrypt.gensalt(PAM.workload);
                passwordRow.set("FACTOR", (Object)PAM.workload);
                final String hashed_password = AuthUtil.getEncryptedPassword(password, salt, "bcrypt");
                PAM.logger.info("password's algorithm successfully updated");
                passwordRow.set("ALGORITHM", (Object)"bcrypt");
                passwordRow.set("PASSWORD", (Object)hashed_password);
                passwordRow.set("SALT", (Object)salt);
                passwordRow.set("CREATEDTIME", (Object)System.currentTimeMillis());
                accDo.updateRow(passwordRow);
                DataAccess.update(accDo);
            }
            else if (algo.equalsIgnoreCase("bcrypt") && workFactor != null && workFactor != passwordRow.get("FACTOR")) {
                salt = BCrypt.gensalt(Integer.parseInt(workFactor.toString()));
                final String hashed_password = AuthUtil.getEncryptedPassword(password, salt, algo);
                PAM.logger.info("strength of password hash has been updated ");
                passwordRow.set("PASSWORD", (Object)hashed_password);
                passwordRow.set("SALT", (Object)salt);
                passwordRow.set("CREATEDTIME", (Object)System.currentTimeMillis());
                passwordRow.set("FACTOR", workFactor);
                accDo.updateRow(passwordRow);
                DataAccess.update(accDo);
            }
        }
        catch (final Exception e) {
            PAM.logger.log(Level.WARNING, "Failed to migrate the password's algorithm :: " + e.getMessage());
            e.printStackTrace();
        }
        return subject;
    }
    
    public static void logout(final Long sessionId) throws PAMException, LoginException {
        PAM.logger.log(Level.FINEST, "logout(sessionId) invoked");
        if (PAM.pamImpl == null) {
            init();
        }
        try {
            PAM.pamImpl.inValidate(sessionId);
        }
        finally {
            AuthUtil.flushCredentials();
        }
    }
    
    public static void logout(final String ssoId) throws PAMException, LoginException {
        PAM.logger.log(Level.FINEST, "logout(ssoid) invoked");
        if (ssoId == null) {
            PAM.logger.log(Level.WARNING, "ssoId obtained is null, could not logout");
        }
        final List sessionIds = AuthDBUtil.getSessionIds(ssoId);
        final int size = sessionIds.size();
        Long sessionId = null;
        for (int i = 0; i < size; ++i) {
            sessionId = sessionIds.get(i);
            try {
                logout(sessionId);
            }
            catch (final Exception e) {
                PAM.logger.log(Level.SEVERE, "Exception occured while logout sessionId : {0}", sessionId);
            }
        }
    }
    
    private static WritableCredential getWritableCredential(final Subject subject) {
        final Set set = subject.getPublicCredentials(WritableCredential.class);
        WritableCredential wcr = null;
        if (set != null) {
            final Object[] objArr = set.toArray();
            wcr = (WritableCredential)((objArr.length > 0) ? objArr[0] : null);
        }
        return wcr;
    }
    
    static {
        PAM.logger = Logger.getLogger(PAM.class.getName());
        PAM.pamImplClassName = null;
        PAM.pamImpl = null;
        PAM.workload = 12;
        DOMAINNAME = PersistenceInitializer.getConfigurationValue("domainName");
    }
}
