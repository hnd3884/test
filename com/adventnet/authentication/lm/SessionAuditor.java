package com.adventnet.authentication.lm;

import java.util.Set;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.util.AuthUtil;
import java.util.Properties;
import com.adventnet.authentication.callback.SessionIdCallback;
import javax.security.auth.callback.Callback;
import com.adventnet.authentication.internal.WritableCredential;
import javax.security.auth.login.LoginException;
import java.util.Map;
import java.util.logging.Level;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.logging.Logger;
import javax.security.auth.spi.LoginModule;

public class SessionAuditor implements LoginModule
{
    private static Logger logger;
    protected Subject subject;
    protected CallbackHandler callbackHandler;
    
    public SessionAuditor() {
        this.subject = null;
        this.callbackHandler = null;
        SessionAuditor.logger.log(Level.FINEST, "constructor invoked");
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map sharedState, final Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        SessionAuditor.logger.log(Level.FINEST, "intialized with subject : {0}, cbh : {1}, sharedState : {2} & options : {3}", new Object[] { subject, callbackHandler, sharedState, options });
    }
    
    @Override
    public boolean login() throws LoginException {
        return true;
    }
    
    @Override
    public boolean commit() throws LoginException {
        boolean toreturn = false;
        final WritableCredential wcr = getWritableCredential(this.subject);
        if (wcr != null) {
            toreturn = this.auditLogin(wcr);
        }
        else {
            SessionAuditor.logger.log(Level.SEVERE, "Writable credential obtained is null, unable to create audit record");
            toreturn = false;
        }
        return toreturn;
    }
    
    @Override
    public boolean logout() throws LoginException {
        SessionAuditor.logger.log(Level.FINEST, "logout invoked");
        if (this.callbackHandler == null) {
            throw new LoginException("No callbackHandler specified");
        }
        final Callback[] callbacks = { new SessionIdCallback() };
        Long sessionId = null;
        try {
            this.callbackHandler.handle(callbacks);
            sessionId = ((SessionIdCallback)callbacks[0]).getSessionId();
            SessionAuditor.logger.log(Level.FINEST, "sessionId obtained via callback is : {0}", sessionId);
        }
        catch (final Exception e) {
            SessionAuditor.logger.log(Level.SEVERE, "Error occured while handling callbacks: ", e);
            throw new LoginException("Error occured while handling callbacks");
        }
        if (sessionId != null) {
            this.auditLogout(sessionId);
            return true;
        }
        SessionAuditor.logger.log(Level.WARNING, "unable to audit logout action as sessionId obtained is null");
        return false;
    }
    
    @Override
    public boolean abort() throws LoginException {
        SessionAuditor.logger.log(Level.FINEST, "abort invoked");
        boolean toreturn = false;
        final WritableCredential wcr = getWritableCredential(this.subject);
        SessionAuditor.logger.log(Level.FINEST, "writableCredential obtained in abort is : {0}", wcr);
        if (wcr != null) {
            toreturn = this.auditFailedLogin(wcr);
        }
        else {
            SessionAuditor.logger.log(Level.SEVERE, "Writable credential obtained is null, unable to create audit record");
            toreturn = false;
        }
        return toreturn;
    }
    
    private boolean auditLogin(final WritableCredential wcr) throws LoginException {
        try {
            final DataObject auditRecord = this.constructAuditRecordDO(wcr, "SUCCESS", "LOGIN");
            AuthUtil.createAuditRecord(auditRecord, null);
            return true;
        }
        catch (final Exception e) {
            throw new LoginException("DataAccessException occured while creating audit record");
        }
    }
    
    private boolean auditLogout(final Long sessionId) {
        final DataObject dobj = this.getLoggedInAuditRecord(sessionId);
        SessionAuditor.logger.log(Level.FINEST, "logged in audit record DO obtained for sessionid : {0} is - {1}", new Object[] { sessionId, dobj });
        if (dobj == null) {
            return false;
        }
        try {
            final Long currTime = new Long(System.currentTimeMillis());
            final Row auditRow = new Row("AuditRecord");
            auditRow.set("PRINCIPAL", dobj.getFirstValue("AuditRecord", "PRINCIPAL"));
            auditRow.set("TIMESTAMP", (Object)currTime);
            auditRow.set("RECORDTYPE", (Object)"OperationAuditRecord");
            final Row operAuditRow = new Row("OperationAuditRecord");
            operAuditRow.set("AUDITID", auditRow.get("AUDITID"));
            operAuditRow.set("HOSTNAME", dobj.getFirstValue("OperationAuditRecord", "HOSTNAME"));
            operAuditRow.set("RESOURCENAME", dobj.getFirstValue("OperationAuditRecord", "RESOURCENAME"));
            operAuditRow.set("OPERATIONNAME", (Object)"LOGOUT");
            operAuditRow.set("STARTTIME", (Object)currTime);
            operAuditRow.set("COMPLETIONTIME", (Object)currTime);
            operAuditRow.set("RESULT", (Object)"SUCCESS");
            operAuditRow.set("SEVERITY", (Object)"Nil");
            final Row sessionAuditRow = new Row("AaaAccSessionAudit");
            sessionAuditRow.set("SESSION_ID", (Object)sessionId);
            sessionAuditRow.set("AUDITID", auditRow.get("AUDITID"));
            final DataObject auditRecDO = DataAccess.constructDataObject();
            auditRecDO.addRow(auditRow);
            auditRecDO.addRow(operAuditRow);
            auditRecDO.addRow(sessionAuditRow);
            DataAccess.add(auditRecDO);
            return true;
        }
        catch (final DataAccessException dae) {
            SessionAuditor.logger.log(Level.FINEST, "DataAccessException occured while creating auditrecord : ", (Throwable)dae);
            SessionAuditor.logger.log(Level.SEVERE, "DataAccessException occured while creating auditrecord for logout of sessionId : {0} - {1}", new Object[] { sessionId, dae.getMessage() });
            return false;
        }
    }
    
    private boolean auditFailedLogin(final WritableCredential wcr) {
        try {
            final Long currTime = new Long(System.currentTimeMillis());
            final String loginName = wcr.getLoginName();
            final String serviceName = (wcr.getServiceName() == null) ? "unknown" : wcr.getServiceName();
            final String hostName = (wcr.getHostName() == null) ? "unknown" : wcr.getHostName();
            final Row auditRow = new Row("AuditRecord");
            auditRow.set("PRINCIPAL", (Object)loginName);
            auditRow.set("TIMESTAMP", (Object)currTime);
            auditRow.set("RECORDTYPE", (Object)"OperationAuditRecord");
            final Row operAuditRow = new Row("OperationAuditRecord");
            operAuditRow.set("AUDITID", auditRow.get("AUDITID"));
            operAuditRow.set("HOSTNAME", (Object)hostName);
            operAuditRow.set("RESOURCENAME", (Object)serviceName);
            operAuditRow.set("OPERATIONNAME", (Object)"LOGIN");
            operAuditRow.set("STARTTIME", (Object)currTime);
            operAuditRow.set("COMPLETIONTIME", (Object)currTime);
            operAuditRow.set("RESULT", (Object)"FAILED");
            operAuditRow.set("SEVERITY", (Object)"Nil");
            final DataObject auditRecDO = DataAccess.constructDataObject();
            auditRecDO.addRow(auditRow);
            auditRecDO.addRow(operAuditRow);
            DataAccess.add(auditRecDO);
            return true;
        }
        catch (final DataAccessException dae) {
            SessionAuditor.logger.log(Level.FINEST, "DataAccessException occured while creating auditrecord : ", (Throwable)dae);
            SessionAuditor.logger.log(Level.SEVERE, "DataAccessException occured while creating auditrecord for failed login for wcr : {0} - {1}", new Object[] { wcr, dae.getMessage() });
            return false;
        }
    }
    
    private DataObject getLoggedInAuditRecord(final Long sessionId) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AuditRecord"));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.addJoin(new Join("AuditRecord", "OperationAuditRecord", new String[] { "AUDITID" }, new String[] { "AUDITID" }, 2));
            sq.addJoin(new Join("AuditRecord", "AaaAccSessionAudit", new String[] { "AUDITID" }, new String[] { "AUDITID" }, 2));
            Criteria cr = new Criteria(Column.getColumn("AaaAccSessionAudit", "SESSION_ID"), (Object)sessionId, 0);
            cr = cr.and(Column.getColumn("OperationAuditRecord", "OPERATIONNAME"), (Object)"LOGIN", 0);
            sq.setCriteria(cr);
            final DataObject dobj = DataAccess.get(sq);
            if (dobj.containsTable("AuditRecord") && dobj.containsTable("OperationAuditRecord")) {
                return dobj;
            }
            return null;
        }
        catch (final DataAccessException dae) {
            SessionAuditor.logger.log(Level.SEVERE, "DataAccessException occured while retrieving login audit record for sessionid : {0} : {1}", new Object[] { sessionId, dae.getMessage() });
            return null;
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
    
    private DataObject constructAuditRecordDO(final WritableCredential wcr, final String result, final String operationname) {
        try {
            final Row auditRecordRow = new Row("AuditRecord");
            auditRecordRow.set("PRINCIPAL", (Object)wcr.getLoginName());
            auditRecordRow.set("RECORDTYPE", (Object)"OperationAuditRecord");
            auditRecordRow.set("TIMESTAMP", (Object)new Long(System.currentTimeMillis()));
            final Row operAuditRecordRow = new Row("OperationAuditRecord");
            operAuditRecordRow.set("AUDITID", auditRecordRow.get("AUDITID"));
            operAuditRecordRow.set("HOSTNAME", (Object)wcr.getHostName());
            operAuditRecordRow.set("OPERATIONNAME", (Object)operationname);
            operAuditRecordRow.set("RESOURCENAME", (Object)wcr.getServiceName());
            operAuditRecordRow.set("RESULT", (Object)result);
            operAuditRecordRow.set("STARTTIME", (Object)new Long(System.currentTimeMillis()));
            operAuditRecordRow.set("COMPLETIONTIME", (Object)new Long(System.currentTimeMillis()));
            final Row sessionAuditRow = new Row("AaaAccSessionAudit");
            sessionAuditRow.set("AUDITID", auditRecordRow.get("AUDITID"));
            sessionAuditRow.set("SESSION_ID", (Object)wcr.getSessionId());
            final DataObject auditRecordDO = DataAccess.constructDataObject();
            auditRecordDO.addRow(auditRecordRow);
            auditRecordDO.addRow(operAuditRecordRow);
            auditRecordDO.addRow(sessionAuditRow);
            return auditRecordDO;
        }
        catch (final Exception e) {
            SessionAuditor.logger.log(Level.FINEST, "Exception occured while constructing audit record", e);
            return null;
        }
    }
    
    static {
        SessionAuditor.logger = Logger.getLogger(SessionAuditor.class.getName());
    }
}
