package com.adventnet.authentication.lm;

import java.util.Iterator;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.PAMException;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import com.adventnet.authentication.util.ADUtils;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.authentication.PAM;
import java.util.logging.Logger;

public class ADAuthenticator extends Authenticator
{
    protected static final Logger LOGGER;
    
    @Override
    public boolean authenticate() throws LoginException {
        boolean result = false;
        boolean ssl = false;
        try {
            String domain = this.request.getParameter(PAM.DOMAINNAME);
            final ArrayList<String> dclist = new ArrayList<String>();
            final Column column = Column.getColumn("ActiveDirectoryInfo", "DEFAULTDOMAIN");
            final Criteria criteria = new Criteria(column, (Object)domain, 0);
            final DataObject dataObject = DataAccess.get("ActiveDirectoryInfo", criteria);
            final Row row = dataObject.getFirstRow("ActiveDirectoryInfo");
            final String serverName = (String)row.get("SERVERNAME");
            final String secServerName = (String)row.get("SECONDARYSERVERNAME");
            ssl = (boolean)row.get("ISSSL");
            dclist.add(serverName);
            this.updateBadLogin = false;
            if (secServerName != null) {
                for (final String dc : secServerName.split(",")) {
                    dclist.add(dc);
                }
            }
            if (domain == null) {
                domain = (String)row.get("DEFAULTDOMAIN");
            }
            final String userName = (String)row.get("USERNAME");
            final String pword = (String)row.get("PASSWORD");
            String autoImport = this.request.getSession().getServletContext().getInitParameter("AUTO_IMPORT_USER");
            if (autoImport == null) {
                autoImport = "TRUE";
            }
            this.accountDO = AuthDBUtil.getAccountDO(this.loginName, this.serviceName, domain);
            if ("TRUE".equals(autoImport) && !this.accountDO.containsTable("AaaAccount")) {
                Properties props = null;
                for (final Object dc2 : dclist) {
                    try {
                        props = ADUtils.getADUserProps(domain, dc2.toString(), userName, pword, ssl);
                    }
                    catch (final Exception e) {
                        props = null;
                    }
                    if (props != null) {
                        break;
                    }
                }
                if (props == null) {
                    throw new LoginException("No account is configured as well as could not fetch the user " + this.loginName + " from AD: DCLIST is " + dclist.toString());
                }
                ADAuthenticator.LOGGER.log(Level.FINE, "Importing new User ***** present in AD");
                AuthDBUtil.addAccountDO(this.loginName, domain, this.request.getSession().getServletContext().getInitParameter("DEFAULT_ROLE"));
                this.accountDO = AuthDBUtil.getAccountDO(this.loginName, this.serviceName, domain);
            }
            if (this.accountDO == null) {
                throw new PAMException("Account DO fetched is null");
            }
            if (!this.accountDO.containsTable("AaaAccount")) {
                throw new LoginException("No such account configured for the user");
            }
            String dUserName = null;
            if (this.loginName.indexOf("\\") != -1) {
                dUserName = this.loginName.substring(this.loginName.indexOf("\\") + 1, this.loginName.length());
            }
            else {
                dUserName = this.loginName;
            }
            int counter = 0;
            for (final Object dc3 : dclist) {
                try {
                    result = this.authenticateUser(dc3.toString(), domain, dUserName.trim(), this.password, ssl);
                }
                catch (final Exception e2) {
                    try {
                        this.authenticateUser(dc3.toString(), domain, userName, pword, ssl);
                    }
                    catch (final Exception ex) {
                        ADAuthenticator.LOGGER.log(Level.SEVERE, "Domain controller " + dc3 + " is down");
                        ++counter;
                    }
                    continue;
                }
                if (result) {
                    break;
                }
            }
            ADAuthenticator.LOGGER.log(Level.FINE, "Authentication Result for User {0} is {1}", new Object[] { "*****", result });
            if (!result) {
                if (counter == dclist.size()) {
                    throw new LoginException("Domain Controller " + dclist + "is down");
                }
                throw new LoginException("Invalid loginName/password");
            }
        }
        catch (final Exception e3) {
            ADAuthenticator.LOGGER.log(Level.SEVERE, "Exception occured in Native Authentication");
            final LoginException le = new LoginException(e3.getMessage());
            le.initCause(e3);
            throw le;
        }
        return result;
    }
    
    public boolean authenticateLocally() throws LoginException {
        return super.authenticate();
    }
    
    public native boolean authenticateUser(final String p0, final String p1, final String p2, final String p3, final boolean p4) throws Exception;
    
    public native Object getDCNames(final String p0) throws Exception;
    
    public void nativeLog(final String str) {
        ADAuthenticator.LOGGER.log(Level.INFO, "Authentication Log Message : {0}", str);
    }
    
    public void nativeDebugMsg(final String str) {
        ADAuthenticator.logger.log(Level.INFO, "Authentication debug Log Message : {0}", str);
    }
    
    static {
        LOGGER = Logger.getLogger(ADAuthenticator.class.getName());
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            System.loadLibrary("ADAuth");
            System.loadLibrary("ADAuthWmi");
        }
    }
}
