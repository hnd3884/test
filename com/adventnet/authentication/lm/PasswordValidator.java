package com.adventnet.authentication.lm;

import com.adventnet.persistence.Row;
import javax.security.auth.login.CredentialExpiredException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.PAMException;
import com.adventnet.authentication.util.AuthDBUtil;
import com.adventnet.authentication.UserPrincipal;
import com.adventnet.authentication.callback.DomainCallback;
import com.adventnet.authentication.callback.ServiceCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.security.Principal;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.logging.Logger;
import javax.security.auth.spi.LoginModule;

public class PasswordValidator implements LoginModule
{
    private static Logger logger;
    protected Subject subject;
    protected CallbackHandler callbackHandler;
    protected Principal principal;
    protected boolean committed;
    protected DataObject passwordDO;
    
    public PasswordValidator() {
        this.subject = null;
        this.callbackHandler = null;
        this.principal = null;
        this.committed = false;
        this.passwordDO = null;
        PasswordValidator.logger.log(Level.FINEST, "constructor invoked");
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState, final Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        PasswordValidator.logger.log(Level.FINEST, "intialized with subject : {0}, cbh : {1}, sharedState : {2} & options : {3}", new Object[] { subject, callbackHandler, sharedState, options });
    }
    
    @Override
    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("No callbackHandler specified");
        }
        final Callback[] callbacks = { new NameCallback("Login name : ", "loginname"), new ServiceCallback(), new DomainCallback() };
        String loginName = null;
        String serviceName = null;
        String domainName = null;
        try {
            this.callbackHandler.handle(callbacks);
            loginName = ((NameCallback)callbacks[0]).getName();
            serviceName = new String(((ServiceCallback)callbacks[1]).getServiceName());
            domainName = ((DomainCallback)callbacks[2]).getDomainName();
            PasswordValidator.logger.log(Level.FINEST, "loginName and serviceName obtained via callbacks are : {0} & {1}", new String[] { "*****", serviceName });
        }
        catch (final Exception e) {
            PasswordValidator.logger.log(Level.SEVERE, "Error occured while handling callbacks: ", e);
            throw new LoginException("Error occured while handling callbacks");
        }
        this.validatePassword(loginName, serviceName, domainName);
        this.principal = new UserPrincipal(loginName);
        PasswordValidator.logger.log(Level.FINEST, "principal object constructed is : {0}", this.principal);
        return true;
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (this.principal == null) {
            PasswordValidator.logger.log(Level.SEVERE, "principal obj is null, commit failed");
            return false;
        }
        if (!this.subject.getPrincipals().contains(this.principal)) {
            this.subject.getPrincipals().add(this.principal);
        }
        return this.committed = true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        PasswordValidator.logger.log(Level.FINEST, "logout invoked");
        this.subject.getPrincipals().remove(this.principal);
        this.principal = null;
        this.committed = false;
        return true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        PasswordValidator.logger.log(Level.FINEST, "abort invoked");
        if (this.committed) {
            this.logout();
        }
        this.principal = null;
        return true;
    }
    
    private void validatePassword(final String loginName, final String serviceName, final String domainName) throws LoginException {
        PasswordValidator.logger.log(Level.FINEST, "validatePassword invoked with loginName :: [{0}] and serviceName :: [{1}]", new Object[] { "*****", serviceName });
        try {
            this.passwordDO = AuthDBUtil.getAccountDO(loginName, serviceName, domainName);
        }
        catch (final DataAccessException dae) {
            throw new PAMException("DataAccessException occured while fetching account password Dataobject", (Exception)dae);
        }
        try {
            final String status = (String)this.passwordDO.getFirstValue("AaaPasswordStatus", "STATUS");
            PasswordValidator.logger.log(Level.FINEST, "isValid invoked for password with status : {0}", status);
            if (status.equals("NEW")) {
                final boolean chpassReq = (boolean)this.passwordDO.getFirstValue("AaaPasswordProfile", "CHPASSWD_ONFL");
                PasswordValidator.logger.log(Level.FINEST, "change password required on first login - flag obtained is : {0}", new Boolean(chpassReq));
                PasswordValidator.logger.log(Level.FINEST, "updating password status from NEW to ACTIVE");
                final Row passStatusRow = this.passwordDO.getFirstRow("AaaPasswordStatus");
                passStatusRow.set("STATUS", (Object)"ACTIVE");
                passStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.passwordDO.updateRow(passStatusRow);
                this.passwordDO = DataAccess.update(this.passwordDO);
            }
            else if (status.equals("EXPIRED")) {
                final Row passStatusRow2 = this.passwordDO.getFirstRow("AaaPasswordStatus");
                final Row passProfileRow = this.passwordDO.getFirstRow("AaaPasswordProfile");
                final int afterExpLogin = (int)passStatusRow2.get("AFTEREXP_LOGIN") + 1;
                final int loginAfterExp = (int)passProfileRow.get("LOGIN_AFTEREXP");
                PasswordValidator.logger.log(Level.FINEST, "after expiry login count = {0}, where allowed count = {1}", new Object[] { new Integer(afterExpLogin), new Integer(loginAfterExp) });
                if (loginAfterExp < afterExpLogin) {
                    passStatusRow2.set("AFTEREXP_LOGIN", (Object)new Integer(afterExpLogin));
                    passStatusRow2.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                    this.passwordDO.updateRow(passStatusRow2);
                    this.passwordDO = DataAccess.update(this.passwordDO);
                    throw new CredentialExpiredException("Password expired");
                }
                passStatusRow2.set("AFTEREXP_LOGIN", (Object)new Integer(afterExpLogin));
                passStatusRow2.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.passwordDO.updateRow(passStatusRow2);
                this.passwordDO = DataAccess.update(this.passwordDO);
                return;
            }
            else if (status.equals("BADLOGIN")) {
                final boolean canStatusBeReset = false;
                final int updateInterval = (int)this.passwordDO.getFirstValue("AaaPasswordProfile", "UPDATE_INTERVAL");
                final long statusUpdatedTime = (long)this.passwordDO.getFirstValue("AaaPasswordStatus", "UPDATEDTIME");
                if (updateInterval == -1 || updateInterval <= 0 || statusUpdatedTime + updateInterval * 60 * 1000L >= System.currentTimeMillis()) {
                    PasswordValidator.logger.log(Level.INFO, "current status of user is BADLOGIN. cannot login until reset");
                    throw new LoginException("Password status set as BadLogin. Cannot login until reactivated");
                }
                PasswordValidator.logger.log(Level.FINEST, "time duration has elapsed. The status can be reset as ACTIVE");
                final Row passStatusRow3 = this.passwordDO.getFirstRow("AaaPasswordStatus");
                passStatusRow3.set("STATUS", (Object)"ACTIVE");
                passStatusRow3.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.passwordDO.updateRow(passStatusRow3);
                this.passwordDO = DataAccess.update(this.passwordDO);
                PasswordValidator.logger.log(Level.FINEST, "password status updated from BADLOGIN to ACTIVE");
            }
            final Long expireAt = (Long)this.passwordDO.getFirstValue("AaaPasswordStatus", "EXPIREAT");
            final long now = System.currentTimeMillis();
            PasswordValidator.logger.log(Level.FINEST, "Password expire time obtained is : {0}, tobe verified against : {1}", new Object[] { expireAt, new Long(now) });
            if (expireAt == -1L) {
                PasswordValidator.logger.log(Level.FINEST, "not validating password expiry as the value is set to -1");
            }
            else if (expireAt < now) {
                PasswordValidator.logger.log(Level.INFO, "Password validity period is lesser than current time, i.e, PasswordExpired");
                final Row passStatusRow4 = this.passwordDO.getFirstRow("AaaPasswordStatus");
                passStatusRow4.set("STATUS", (Object)"EXPIRED");
                passStatusRow4.set("AFTEREXP_LOGIN", (Object)new Integer(0));
                passStatusRow4.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.passwordDO.updateRow(passStatusRow4);
                this.passwordDO = DataAccess.update(this.passwordDO);
                throw new CredentialExpiredException("Password validity period expired");
            }
        }
        catch (final DataAccessException dae) {
            PasswordValidator.logger.log(Level.SEVERE, "DataAccessException occured when fetching password status details from DO", (Throwable)dae);
            throw new LoginException("Exception occured while validating password : " + dae.getMessage());
        }
    }
    
    static {
        PasswordValidator.logger = Logger.getLogger(PasswordValidator.class.getName());
    }
}
