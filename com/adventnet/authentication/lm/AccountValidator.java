package com.adventnet.authentication.lm;

import com.adventnet.persistence.Row;
import javax.security.auth.login.AccountExpiredException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.PAMException;
import com.adventnet.authentication.util.AuthDBUtil;
import java.util.Set;
import com.adventnet.authentication.internal.WritableCredential;
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

public class AccountValidator implements LoginModule
{
    private static Logger logger;
    protected Subject subject;
    protected CallbackHandler callbackHandler;
    protected Principal principal;
    protected boolean committed;
    protected DataObject accountDO;
    
    public AccountValidator() {
        this.subject = null;
        this.callbackHandler = null;
        this.principal = null;
        this.committed = false;
        this.accountDO = null;
        AccountValidator.logger.log(Level.FINEST, "constructor invoked");
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState, final Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        AccountValidator.logger.log(Level.FINEST, "intialized with subject : {0}, cbh : {1}, sharedState : {2} & options : {3}", new Object[] { subject, callbackHandler, sharedState, options });
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
            AccountValidator.logger.log(Level.FINEST, "loginName and serviceName obtained via callbacks are : {0} & {1}", new String[] { "*****", serviceName });
        }
        catch (final Exception e) {
            AccountValidator.logger.log(Level.SEVERE, "Error occured while handling callbacks: ", e);
            throw new LoginException("Error occured while handling callbacks");
        }
        this.validateAccount(loginName, serviceName, domainName);
        this.principal = new UserPrincipal(loginName);
        AccountValidator.logger.log(Level.FINEST, "principal object constructed is : {0}", this.principal);
        return true;
    }
    
    @Override
    public boolean commit() throws LoginException {
        AccountValidator.logger.log(Level.FINEST, "subject in AccountValidator : {0}", this.subject);
        if (this.principal == null) {
            AccountValidator.logger.log(Level.SEVERE, "principal obj is null, commit failed");
            return false;
        }
        if (!this.subject.getPrincipals().contains(this.principal)) {
            this.subject.getPrincipals().add(this.principal);
        }
        return this.committed = true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        AccountValidator.logger.log(Level.FINEST, "logout invoked");
        this.subject.getPrincipals().remove(this.principal);
        this.principal = null;
        this.committed = false;
        return true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        AccountValidator.logger.log(Level.FINEST, "abort invoked");
        if (this.committed) {
            this.logout();
        }
        this.principal = null;
        return true;
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
    
    private void validateAccount(final String loginName, final String serviceName, final String domainName) throws LoginException {
        AccountValidator.logger.log(Level.FINEST, "validateAccount invoked with loginName and serviceName");
        try {
            this.accountDO = AuthDBUtil.getAccountDO(loginName, serviceName, domainName);
        }
        catch (final DataAccessException dae) {
            throw new PAMException("DataAccessException occured while fetching account Dataobject", (Exception)dae);
        }
        try {
            if (this.accountDO.containsTable("AaaUserStatus")) {
                final String status = (String)this.accountDO.getFirstValue("AaaUserStatus", "STATUS");
                if (status.equals("DISABLED")) {
                    throw new LoginException("User status is disabled");
                }
            }
            final String status = (String)this.accountDO.getFirstValue("AaaAccountStatus", "STATUS");
            if (status.equals("NEW")) {
                AccountValidator.logger.log(Level.INFO, "updating account status from NEW to ACTIVE");
                final Row accStatusRow = this.accountDO.getFirstRow("AaaAccountStatus");
                accStatusRow.set("STATUS", (Object)"ACTIVE");
                accStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.accountDO.updateRow(accStatusRow);
                this.accountDO = DataAccess.update(this.accountDO);
            }
            else if (status.equals("INACTIVE")) {
                AccountValidator.logger.log(Level.INFO, "[-TODO-] status INACTIVE, no validation is done");
            }
            else {
                if (status.equals("DEACTIVATED")) {
                    throw new LoginException("Account deactivated");
                }
                if (status.equals("LOCKED")) {
                    throw new LoginException("Account locked");
                }
                if (status.equals("EXPIRED")) {
                    final Row accStatusRow = this.accountDO.getFirstRow("AaaAccountStatus");
                    final int afterExpLogin = (int)accStatusRow.get("AFTEREXP_LOGIN");
                    accStatusRow.set("AFTEREXP_LOGIN", (Object)new Integer(afterExpLogin + 1));
                    accStatusRow.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                    this.accountDO.updateRow(accStatusRow);
                    this.accountDO = DataAccess.update(this.accountDO);
                    throw new AccountExpiredException("Account expired");
                }
            }
            final Long expireAt = (Long)this.accountDO.getFirstValue("AaaAccountStatus", "EXPIREAT");
            final long now = System.currentTimeMillis();
            AccountValidator.logger.log(Level.FINEST, "Account expire time obtained is : {0}, tobe verified against : {1}", new Object[] { expireAt, new Long(now) });
            if (expireAt == -1L) {
                AccountValidator.logger.log(Level.FINEST, "not validating account expiry as the value is set to -1");
            }
            else if (expireAt < now) {
                AccountValidator.logger.log(Level.INFO, "Account validity period is lesser than current time, i.e, AccountExpired");
                final Row accStatusRow2 = this.accountDO.getFirstRow("AaaAccountStatus");
                accStatusRow2.set("STATUS", (Object)"EXPIRED");
                accStatusRow2.set("AFTEREXP_LOGIN", (Object)new Integer(0));
                accStatusRow2.set("UPDATEDTIME", (Object)new Long(System.currentTimeMillis()));
                this.accountDO.updateRow(accStatusRow2);
                this.accountDO = DataAccess.update(this.accountDO);
                throw new AccountExpiredException("Account validity period expired");
            }
        }
        catch (final DataAccessException e) {
            AccountValidator.logger.log(Level.SEVERE, "DataAccessException occured while validating account", (Throwable)e);
            throw new LoginException("Exception occured while validating account : " + e.getMessage());
        }
    }
    
    static {
        AccountValidator.logger = Logger.getLogger(AccountValidator.class.getName());
    }
}
