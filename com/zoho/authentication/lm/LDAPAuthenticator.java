package com.zoho.authentication.lm;

import com.zoho.framework.utils.OSCheckUtil;
import com.unboundid.ldap.sdk.BindResult;
import java.util.Objects;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import javax.net.SocketFactory;
import com.unboundid.ldap.sdk.LDAPConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import com.unboundid.util.ssl.SSLUtil;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.PAMException;
import com.adventnet.authentication.util.AuthDBUtil;
import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.authentication.PAM;
import java.util.logging.Logger;
import com.adventnet.authentication.lm.Authenticator;

public class LDAPAuthenticator extends Authenticator
{
    protected static final Logger LOGGER;
    private static boolean isWindows;
    
    @Override
    public boolean authenticate() throws LoginException {
        boolean result = false;
        boolean ssl = false;
        try {
            final String domain = this.request.getParameter(PAM.DOMAINNAME);
            final ArrayList<String> dclist = new ArrayList<String>();
            final Column column = Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN");
            final Criteria criteria = new Criteria(column, (Object)domain, 0);
            final DataObject dataObject = DataAccess.get("ActiveDirectoryInfo", criteria);
            final Row row = dataObject.getFirstRow("ActiveDirectoryInfo");
            final String serverName = (String)row.get("SERVERNAME");
            final String secServerName = (String)row.get("SECONDARYSERVERNAME");
            ssl = (boolean)row.get("ISSSL");
            Integer port = (Integer)row.get("port");
            port = ((port == null) ? (ssl ? 636 : 389) : port);
            dclist.add(serverName);
            this.updateBadLogin = false;
            if (secServerName != null) {
                for (final String dc : secServerName.split(",")) {
                    dclist.add(dc);
                }
            }
            final String userName = (String)row.get("USERNAME");
            final String pword = (String)row.get("PASSWORD");
            String dUserName = null;
            if (this.loginName.indexOf("\\") != -1) {
                dUserName = this.loginName.substring(this.loginName.indexOf("\\") + 1, this.loginName.length());
            }
            else {
                dUserName = this.loginName;
            }
            for (final Object dc2 : dclist) {
                try {
                    result = this.authenticateUser(dc2.toString(), domain, dUserName.trim(), this.password, ssl, port);
                }
                catch (final Exception e) {
                    LDAPAuthenticator.LOGGER.log(Level.SEVERE, "failed :: ", e);
                    try {
                        this.authenticateUser(dc2.toString(), domain, userName, pword, ssl, port);
                    }
                    catch (final Exception ex) {
                        LDAPAuthenticator.LOGGER.log(Level.SEVERE, "Domain controller {0} is down", dc2);
                    }
                    continue;
                }
                if (result) {
                    break;
                }
            }
            LDAPAuthenticator.LOGGER.log(Level.FINE, "Authentication Result for User {0} is {1}", new Object[] { "*****", result });
            if (!result) {
                throw new LoginException("Invalid loginName/password");
            }
            String autoImport = this.request.getSession().getServletContext().getInitParameter("AUTO_IMPORT_USER");
            if (autoImport == null) {
                autoImport = "TRUE";
            }
            this.accountDO = AuthDBUtil.getAccountDO(this.loginName, this.serviceName, domain);
            if ("TRUE".equals(autoImport) && !this.accountDO.containsTable("AaaAccount")) {
                LDAPAuthenticator.LOGGER.log(Level.FINE, "Importing new User ***** present in AD");
                AuthDBUtil.addAccountDO(this.loginName, domain, this.request.getSession().getServletContext().getInitParameter("DEFAULT_ROLE"));
                this.accountDO = AuthDBUtil.getAccountDO(this.loginName, this.serviceName, domain);
            }
            if (this.accountDO == null) {
                throw new PAMException("Account DO fetched is null");
            }
            if (!this.accountDO.containsTable("AaaAccount")) {
                throw new LoginException("No such account configured for the user");
            }
        }
        catch (final Exception e2) {
            LDAPAuthenticator.LOGGER.log(Level.SEVERE, "Exception occured during LDAP Authentication");
            final LoginException le = new LoginException(e2.getMessage());
            le.initCause(e2);
            throw le;
        }
        return result;
    }
    
    public boolean authenticateUser(final String dc, final String domain, final String userName, String password, final boolean ssl, final int port) throws Exception {
        final String userId = StringUtils.isNotEmpty((CharSequence)userName) ? (userName + "@" + domain) : "";
        password = (StringUtils.isNotEmpty((CharSequence)password) ? password : "");
        SSLSocketFactory sslSocketFactory = null;
        if (ssl) {
            try {
                final TrustManagerFactory trus = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trus.init((KeyStore)null);
                final SSLUtil sslUtil = new SSLUtil(trus.getTrustManagers());
                sslSocketFactory = sslUtil.createSSLSocketFactory();
                return this.authenticateWithLDAPServer(dc, userId, password, port, sslSocketFactory);
            }
            catch (final Exception e) {
                if (!LDAPAuthenticator.isWindows) {
                    throw e;
                }
                LDAPAuthenticator.LOGGER.log(Level.WARNING, "Exception occurred while authenticating using java default keystore :: ", e.getMessage());
                LDAPAuthenticator.LOGGER.log(Level.INFO, "trying to authenticate using windows keystore");
                final TrustManagerFactory trus2 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                final KeyStore windowsKeystore = KeyStore.getInstance("WINDOWS-ROOT");
                windowsKeystore.load(null, null);
                trus2.init(windowsKeystore);
                final SSLUtil sslUtil2 = new SSLUtil(trus2.getTrustManagers());
                sslSocketFactory = sslUtil2.createSSLSocketFactory();
                return this.authenticateWithLDAPServer(dc, userId, password, port, sslSocketFactory);
            }
        }
        return this.authenticateWithLDAPServer(dc, userId, password, port, sslSocketFactory);
    }
    
    private boolean authenticateWithLDAPServer(final String dc, final String userId, final String password, final int port, final SSLSocketFactory sslSocketFactory) throws Exception {
        try (final LDAPConnection ldapConnection = new LDAPConnection((SocketFactory)sslSocketFactory, dc, port)) {
            final SimpleBindRequest bindRequest = new SimpleBindRequest(userId, password);
            final BindResult result = ldapConnection.bind((BindRequest)bindRequest);
            return Objects.equals(result.getResultCode(), ResultCode.SUCCESS);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(LDAPAuthenticator.class.getName());
        LDAPAuthenticator.isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
    }
}
