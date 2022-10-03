package com.microsoft.sqlserver.jdbc;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.transaction.xa.XAException;
import java.text.MessageFormat;
import java.sql.SQLTimeoutException;
import java.sql.SQLException;
import javax.transaction.xa.Xid;
import java.sql.CallableStatement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javax.transaction.xa.XAResource;

public final class SQLServerXAResource implements XAResource
{
    private int timeoutSeconds;
    static final int XA_START = 0;
    static final int XA_END = 1;
    static final int XA_PREPARE = 2;
    static final int XA_COMMIT = 3;
    static final int XA_ROLLBACK = 4;
    static final int XA_FORGET = 5;
    static final int XA_RECOVER = 6;
    static final int XA_PREPARE_EX = 7;
    static final int XA_ROLLBACK_EX = 8;
    static final int XA_FORGET_EX = 9;
    static final int XA_INIT = 10;
    private SQLServerConnection controlConnection;
    private SQLServerConnection con;
    private boolean serverInfoRetrieved;
    private String version;
    private String instanceName;
    private int ArchitectureMSSQL;
    private int ArchitectureOS;
    private static boolean xaInitDone;
    private static final Object xaInitLock;
    private String sResourceManagerId;
    private int enlistedTransactionCount;
    private final Logger xaLogger;
    private static final AtomicInteger baseResourceID;
    private int tightlyCoupled;
    private int isTransacrionTimeoutSet;
    public static final int SSTRANSTIGHTLYCPLD = 32768;
    private SQLServerCallableStatement[] xaStatements;
    private final String traceID;
    private int recoveryAttempt;
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    SQLServerXAResource(final SQLServerConnection original, final SQLServerConnection control, final String loginfo) {
        this.tightlyCoupled = 0;
        this.isTransacrionTimeoutSet = 0;
        this.xaStatements = new SQLServerCallableStatement[] { null, null, null, null, null, null, null, null, null, null };
        this.recoveryAttempt = 0;
        this.traceID = " XAResourceID:" + nextResourceID();
        this.xaLogger = SQLServerXADataSource.xaLogger;
        this.controlConnection = control;
        this.con = original;
        final Properties p = original.activeConnectionProperties;
        if (p == null) {
            this.sResourceManagerId = "";
        }
        else {
            this.sResourceManagerId = p.getProperty(SQLServerDriverStringProperty.SERVER_NAME.toString()) + "." + p.getProperty(SQLServerDriverStringProperty.DATABASE_NAME.toString()) + "." + p.getProperty(SQLServerDriverIntProperty.PORT_NUMBER.toString());
        }
        if (this.xaLogger.isLoggable(Level.FINE)) {
            this.xaLogger.fine(this.toString() + " created by (" + loginfo + ")");
        }
        this.serverInfoRetrieved = false;
        this.version = "0";
        this.instanceName = "";
        this.ArchitectureMSSQL = 0;
        this.ArchitectureOS = 0;
    }
    
    private synchronized SQLServerCallableStatement getXACallableStatementHandle(final int number) throws SQLServerException {
        assert number >= 0 && number <= 9;
        assert number < this.xaStatements.length;
        if (null != this.xaStatements[number]) {
            return this.xaStatements[number];
        }
        CallableStatement CS = null;
        switch (number) {
            case 0: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_start(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
                break;
            }
            case 1: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_end(?, ?, ?, ?, ?, ?, ?)}");
                break;
            }
            case 2: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_prepare(?, ?, ?, ?, ?)}");
                break;
            }
            case 3: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_commit(?, ?, ?, ?, ?, ?)}");
                break;
            }
            case 4: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_rollback(?, ?, ?, ?, ?)}");
                break;
            }
            case 5: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_forget(?, ?, ?, ?, ?)}");
                break;
            }
            case 6: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_recover(?, ?, ?, ?)}");
                break;
            }
            case 7: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_prepare_ex(?, ?, ?, ?, ?, ?)}");
                break;
            }
            case 8: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_rollback_ex(?, ?, ?, ?, ?, ?)}");
                break;
            }
            case 9: {
                CS = this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_forget_ex(?, ?, ?, ?, ?, ?)}");
                break;
            }
            default: {
                assert false : "Bad handle request:" + number;
                break;
            }
        }
        return this.xaStatements[number] = (SQLServerCallableStatement)CS;
    }
    
    private synchronized void closeXAStatements() throws SQLServerException {
        for (int i = 0; i < this.xaStatements.length; ++i) {
            if (null != this.xaStatements[i]) {
                this.xaStatements[i].close();
                this.xaStatements[i] = null;
            }
        }
    }
    
    final synchronized void close() throws SQLServerException {
        try {
            this.closeXAStatements();
        }
        catch (final Exception e) {
            if (this.xaLogger.isLoggable(Level.WARNING)) {
                this.xaLogger.warning(this.toString() + "Closing exception ignored: " + e);
            }
        }
        if (null != this.controlConnection) {
            this.controlConnection.close();
        }
    }
    
    private String flagsDisplay(final int flags) {
        if (0 == flags) {
            return "TMNOFLAGS";
        }
        final StringBuilder sb = new StringBuilder(100);
        if (0x0 != (0x800000 & flags)) {
            sb.append("TMENDRSCAN");
        }
        if (0x0 != (0x20000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMFAIL");
        }
        if (0x0 != (0x200000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMJOIN");
        }
        if (0x0 != (0x40000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMONEPHASE");
        }
        if (0x0 != (0x8000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMRESUME");
        }
        if (0x0 != (0x1000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMSTARTRSCAN");
        }
        if (0x0 != (0x4000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMSUCCESS");
        }
        if (0x0 != (0x2000000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("TMSUSPEND");
        }
        if (0x0 != (0x8000 & flags)) {
            if (sb.length() > 0) {
                sb.append("|");
            }
            sb.append("SSTRANSTIGHTLYCPLD");
        }
        return sb.toString();
    }
    
    private String cookieDisplay(final byte[] cookie) {
        return Util.byteToHexDisplayString(cookie);
    }
    
    private String typeDisplay(final int type) {
        switch (type) {
            case 0: {
                return "XA_START";
            }
            case 1: {
                return "XA_END";
            }
            case 2: {
                return "XA_PREPARE";
            }
            case 3: {
                return "XA_COMMIT";
            }
            case 4: {
                return "XA_ROLLBACK";
            }
            case 5: {
                return "XA_FORGET";
            }
            case 6: {
                return "XA_RECOVER";
            }
            default: {
                return "UNKNOWN" + type;
            }
        }
    }
    
    private XAReturnValue DTC_XA_Interface(final int nType, final Xid xid, final int xaFlags) throws XAException {
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " Calling XA function for type:" + this.typeDisplay(nType) + " flags:" + this.flagsDisplay(xaFlags) + " xid:" + XidImpl.xidDisplay(xid));
        }
        int formatId = 0;
        byte[] gid = null;
        byte[] bid = null;
        if (xid != null) {
            formatId = xid.getFormatId();
            gid = xid.getGlobalTransactionId();
            bid = xid.getBranchQualifier();
        }
        String sContext = "DTC_XA_";
        int n = 1;
        int nStatus = 0;
        final XAReturnValue returnStatus = new XAReturnValue();
        SQLServerCallableStatement cs = null;
        try {
            synchronized (this) {
                if (!SQLServerXAResource.xaInitDone) {
                    try {
                        synchronized (SQLServerXAResource.xaInitLock) {
                            SQLServerCallableStatement initCS = null;
                            initCS = (SQLServerCallableStatement)this.controlConnection.prepareCall("{call master..xp_sqljdbc_xa_init_ex(?, ?,?)}");
                            initCS.registerOutParameter(1, 4);
                            initCS.registerOutParameter(2, 1);
                            initCS.registerOutParameter(3, 1);
                            try {
                                initCS.execute();
                            }
                            catch (final SQLServerException eX) {
                                try {
                                    initCS.close();
                                    this.controlConnection.close();
                                }
                                catch (final SQLException e3) {
                                    if (this.xaLogger.isLoggable(Level.FINER)) {
                                        this.xaLogger.finer(this.toString() + " Ignoring exception when closing failed execution. exception:" + e3);
                                    }
                                }
                                if (this.xaLogger.isLoggable(Level.FINER)) {
                                    this.xaLogger.finer(this.toString() + " exception:" + eX);
                                }
                                throw eX;
                            }
                            catch (final SQLTimeoutException e4) {
                                if (this.xaLogger.isLoggable(Level.FINER)) {
                                    this.xaLogger.finer(this.toString() + " exception:" + e4);
                                }
                                throw new SQLServerException(e4.getMessage(), SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, null);
                            }
                            final int initStatus = initCS.getInt(1);
                            final String initErr = initCS.getString(2);
                            final String versionNumberXADLL = initCS.getString(3);
                            if (this.xaLogger.isLoggable(Level.FINE)) {
                                this.xaLogger.fine(this.toString() + " Server XA DLL version:" + versionNumberXADLL);
                            }
                            initCS.close();
                            if (0 != initStatus) {
                                assert null != initErr && initErr.length() > 1;
                                this.controlConnection.close();
                                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_failedToInitializeXA"));
                                final Object[] msgArgs = { String.valueOf(initStatus), initErr };
                                final XAException xex = new XAException(form.format(msgArgs));
                                xex.errorCode = initStatus;
                                if (this.xaLogger.isLoggable(Level.FINER)) {
                                    this.xaLogger.finer(this.toString() + " exception:" + xex);
                                }
                                throw xex;
                            }
                        }
                    }
                    catch (final SQLServerException e5) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_failedToCreateXAConnection"));
                        final Object[] msgArgs2 = { e5.getMessage() };
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " exception:" + form2.format(msgArgs2));
                        }
                        SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), null, true);
                    }
                    SQLServerXAResource.xaInitDone = true;
                }
            }
            switch (nType) {
                case 0: {
                    if (!this.serverInfoRetrieved) {
                        Statement stmt = null;
                        try {
                            this.serverInfoRetrieved = true;
                            final String query = "select convert(varchar(100), SERVERPROPERTY('Edition'))as edition,  convert(varchar(100), SERVERPROPERTY('InstanceName'))as instance, convert(varchar(100), SERVERPROPERTY('ProductVersion')) as version, SUBSTRING(@@VERSION, CHARINDEX('<', @@VERSION)+2, 2)";
                            stmt = this.controlConnection.createStatement();
                            final ResultSet rs = stmt.executeQuery(query);
                            rs.next();
                            final String edition = rs.getString(1);
                            this.ArchitectureMSSQL = ((null != edition && edition.contains("(64-bit)")) ? 64 : 32);
                            this.instanceName = ((rs.getString(2) == null) ? "MSSQLSERVER" : rs.getString(2));
                            this.version = rs.getString(3);
                            if (null == this.version) {
                                this.version = "0";
                            }
                            else if (-1 != this.version.indexOf(46)) {
                                this.version = this.version.substring(0, this.version.indexOf(46));
                            }
                            this.ArchitectureOS = Integer.parseInt(rs.getString(4));
                            rs.close();
                        }
                        catch (final Exception e6) {
                            if (this.xaLogger.isLoggable(Level.WARNING)) {
                                this.xaLogger.warning(this.toString() + " Cannot retrieve server information: :" + e6.getMessage());
                            }
                            if (null != stmt) {
                                try {
                                    stmt.close();
                                }
                                catch (final SQLException e7) {
                                    if (this.xaLogger.isLoggable(Level.FINER)) {
                                        this.xaLogger.finer(this.toString());
                                    }
                                }
                            }
                        }
                        finally {
                            if (null != stmt) {
                                try {
                                    stmt.close();
                                }
                                catch (final SQLException e8) {
                                    if (this.xaLogger.isLoggable(Level.FINER)) {
                                        this.xaLogger.finer(this.toString());
                                    }
                                }
                            }
                        }
                    }
                    sContext = "START:";
                    cs = this.getXACallableStatementHandle(0);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    cs.setInt(n++, xaFlags);
                    cs.registerOutParameter(n++, -2);
                    cs.setInt(n++, this.timeoutSeconds);
                    cs.setInt(n++, formatId);
                    cs.registerOutParameter(n++, 1);
                    cs.setInt(n++, Integer.parseInt(this.version));
                    cs.setInt(n++, this.instanceName.length());
                    cs.setBytes(n++, this.instanceName.getBytes());
                    cs.setInt(n++, this.ArchitectureMSSQL);
                    cs.setInt(n++, this.ArchitectureOS);
                    cs.setInt(n++, this.isTransacrionTimeoutSet);
                    cs.registerOutParameter(n++, -2);
                    break;
                }
                case 1: {
                    sContext = "END:";
                    cs = this.getXACallableStatementHandle(1);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    cs.setInt(n++, xaFlags);
                    cs.setInt(n++, formatId);
                    cs.registerOutParameter(n++, -2);
                    break;
                }
                case 2: {
                    sContext = "PREPARE:";
                    if ((0x8000 & xaFlags) == 0x8000) {
                        cs = this.getXACallableStatementHandle(7);
                    }
                    else {
                        cs = this.getXACallableStatementHandle(2);
                    }
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    if ((0x8000 & xaFlags) == 0x8000) {
                        cs.setInt(n++, xaFlags);
                    }
                    cs.setInt(n++, formatId);
                    break;
                }
                case 3: {
                    sContext = "COMMIT:";
                    cs = this.getXACallableStatementHandle(3);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    cs.setInt(n++, xaFlags);
                    cs.setInt(n++, formatId);
                    break;
                }
                case 4: {
                    sContext = "ROLLBACK:";
                    if ((0x8000 & xaFlags) == 0x8000) {
                        cs = this.getXACallableStatementHandle(8);
                    }
                    else {
                        cs = this.getXACallableStatementHandle(4);
                    }
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    if ((0x8000 & xaFlags) == 0x8000) {
                        cs.setInt(n++, xaFlags);
                    }
                    cs.setInt(n++, formatId);
                    break;
                }
                case 5: {
                    sContext = "FORGET:";
                    if ((0x8000 & xaFlags) == 0x8000) {
                        cs = this.getXACallableStatementHandle(9);
                    }
                    else {
                        cs = this.getXACallableStatementHandle(5);
                    }
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setBytes(n++, gid);
                    cs.setBytes(n++, bid);
                    if ((0x8000 & xaFlags) == 0x8000) {
                        cs.setInt(n++, xaFlags);
                    }
                    cs.setInt(n++, formatId);
                    break;
                }
                case 6: {
                    sContext = "RECOVER:";
                    cs = this.getXACallableStatementHandle(6);
                    cs.registerOutParameter(n++, 4);
                    cs.registerOutParameter(n++, 1);
                    cs.setInt(n++, xaFlags);
                    cs.registerOutParameter(n++, -2);
                    break;
                }
                default: {
                    assert false : "Unknown execution type:" + nType;
                    break;
                }
            }
            cs.execute();
            nStatus = cs.getInt(1);
            final String sErr = cs.getString(2);
            if (nType == 0) {
                final String versionNumberXADLL2 = cs.getString(9);
                if (this.xaLogger.isLoggable(Level.FINE)) {
                    this.xaLogger.fine(this.toString() + " Server XA DLL version:" + versionNumberXADLL2);
                    if (null != cs.getString(16)) {
                        final StringBuffer strBuf = new StringBuffer(cs.getString(16));
                        strBuf.insert(20, '-');
                        strBuf.insert(16, '-');
                        strBuf.insert(12, '-');
                        strBuf.insert(8, '-');
                        this.xaLogger.fine(this.toString() + " XID to UoW mapping for XA type:XA_START XID: " + XidImpl.xidDisplay(xid) + " UoW: " + strBuf.toString());
                    }
                }
            }
            if (nType == 1 && this.xaLogger.isLoggable(Level.FINE) && null != cs.getString(7)) {
                final StringBuffer strBuf2 = new StringBuffer(cs.getString(7));
                strBuf2.insert(20, '-');
                strBuf2.insert(16, '-');
                strBuf2.insert(12, '-');
                strBuf2.insert(8, '-');
                this.xaLogger.fine(this.toString() + " XID to UoW mapping for XA type:XA_END XID: " + XidImpl.xidDisplay(xid) + " UoW: " + strBuf2.toString());
            }
            if (6 == nType && 0 != nStatus && this.recoveryAttempt < 1) {
                ++this.recoveryAttempt;
                this.DTC_XA_Interface(0, xid, 0);
                return this.DTC_XA_Interface(6, xid, xaFlags);
            }
            if ((3 == nStatus && 1 != nType && 2 != nType) || (0 != nStatus && 3 != nStatus)) {
                assert null != sErr && sErr.length() > 1;
                final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_failedFunctionXA"));
                final Object[] msgArgs3 = { sContext, String.valueOf(nStatus), sErr };
                final XAException e9 = new XAException(form3.format(msgArgs3));
                e9.errorCode = nStatus;
                if (nType == 1 && -7 == nStatus) {
                    try {
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " Begin un-enlist, enlisted count:" + this.enlistedTransactionCount);
                        }
                        this.con.JTAUnenlistConnection();
                        --this.enlistedTransactionCount;
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " End un-enlist, enlisted count:" + this.enlistedTransactionCount);
                        }
                    }
                    catch (final SQLServerException e10) {
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " Ignoring exception:" + e10);
                        }
                    }
                }
                throw e9;
            }
            else {
                if (nType == 0) {
                    final byte[] transactionCookie = cs.getBytes(6);
                    if (transactionCookie == null) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_noTransactionCookie"));
                        final Object[] msgArgs2 = { sContext };
                        SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), null, true);
                    }
                    else {
                        try {
                            if (this.xaLogger.isLoggable(Level.FINER)) {
                                this.xaLogger.finer(this.toString() + " Begin enlisting, cookie:" + this.cookieDisplay(transactionCookie) + " enlisted count:" + this.enlistedTransactionCount);
                            }
                            this.con.JTAEnlistConnection(transactionCookie);
                            ++this.enlistedTransactionCount;
                            if (this.xaLogger.isLoggable(Level.FINER)) {
                                this.xaLogger.finer(this.toString() + " End enlisting, cookie:" + this.cookieDisplay(transactionCookie) + " enlisted count:" + this.enlistedTransactionCount);
                            }
                        }
                        catch (final SQLServerException e11) {
                            final MessageFormat form4 = new MessageFormat(SQLServerException.getErrString("R_failedToEnlist"));
                            final Object[] msgArgs4 = { e11.getMessage() };
                            SQLServerException.makeFromDriverError(null, null, form4.format(msgArgs4), null, true);
                        }
                    }
                }
                if (nType == 1) {
                    try {
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " Begin un-enlist, enlisted count:" + this.enlistedTransactionCount);
                        }
                        this.con.JTAUnenlistConnection();
                        --this.enlistedTransactionCount;
                        if (this.xaLogger.isLoggable(Level.FINER)) {
                            this.xaLogger.finer(this.toString() + " End un-enlist, enlisted count:" + this.enlistedTransactionCount);
                        }
                    }
                    catch (final SQLServerException e5) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_failedToUnEnlist"));
                        final Object[] msgArgs2 = { e5.getMessage() };
                        SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), null, true);
                    }
                }
                if (nType == 6) {
                    try {
                        returnStatus.bData = cs.getBytes(4);
                    }
                    catch (final SQLServerException e5) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_failedToReadRecoveryXIDs"));
                        final Object[] msgArgs2 = { e5.getMessage() };
                        SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), null, true);
                    }
                }
            }
        }
        catch (final SQLServerException | SQLTimeoutException ex) {
            if (this.xaLogger.isLoggable(Level.FINER)) {
                this.xaLogger.finer(this.toString() + " exception:" + ex);
            }
            final XAException e12 = new XAException(ex.toString());
            e12.errorCode = -3;
            throw e12;
        }
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " Status:" + nStatus);
        }
        returnStatus.nStatus = nStatus;
        return returnStatus;
    }
    
    @Override
    public void start(final Xid xid, final int flags) throws XAException {
        this.tightlyCoupled = (flags & 0x8000);
        this.DTC_XA_Interface(0, xid, flags);
    }
    
    @Override
    public void end(final Xid xid, final int flags) throws XAException {
        this.DTC_XA_Interface(1, xid, flags | this.tightlyCoupled);
    }
    
    @Override
    public int prepare(final Xid xid) throws XAException {
        int nStatus = 0;
        final XAReturnValue r = this.DTC_XA_Interface(2, xid, this.tightlyCoupled);
        nStatus = r.nStatus;
        return nStatus;
    }
    
    @Override
    public void commit(final Xid xid, final boolean onePhase) throws XAException {
        this.DTC_XA_Interface(3, xid, (onePhase ? 1073741824 : 0) | this.tightlyCoupled);
    }
    
    @Override
    public void rollback(final Xid xid) throws XAException {
        this.DTC_XA_Interface(4, xid, this.tightlyCoupled);
    }
    
    @Override
    public void forget(final Xid xid) throws XAException {
        this.DTC_XA_Interface(5, xid, this.tightlyCoupled);
    }
    
    @Override
    public Xid[] recover(final int flags) throws XAException {
        final XAReturnValue r = this.DTC_XA_Interface(6, null, flags | this.tightlyCoupled);
        int offset = 0;
        final ArrayList<XidImpl> al = new ArrayList<XidImpl>();
        if (null == r.bData) {
            return new XidImpl[0];
        }
        while (offset < r.bData.length) {
            int power = 1;
            int formatId = 0;
            for (int i = 0; i < 4; ++i) {
                int x = r.bData[offset + i] & 0xFF;
                x *= power;
                formatId += x;
                power *= 256;
            }
            try {
                offset += 4;
                final int gid_len = r.bData[offset++] & 0xFF;
                final int bid_len = r.bData[offset++] & 0xFF;
                final byte[] gid = new byte[gid_len];
                final byte[] bid = new byte[bid_len];
                System.arraycopy(r.bData, offset, gid, 0, gid_len);
                offset += gid_len;
                System.arraycopy(r.bData, offset, bid, 0, bid_len);
                offset += bid_len;
                final XidImpl xid = new XidImpl(formatId, gid, bid);
                al.add(xid);
            }
            catch (final ArrayIndexOutOfBoundsException e) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
                final Object[] msgArgs = { offset };
                final XAException xex = new XAException(form.format(msgArgs));
                xex.errorCode = -3;
                if (this.xaLogger.isLoggable(Level.FINER)) {
                    this.xaLogger.finer(this.toString() + " exception:" + xex);
                }
                throw xex;
            }
        }
        final XidImpl[] xids = new XidImpl[al.size()];
        for (int j = 0; j < al.size(); ++j) {
            xids[j] = al.get(j);
            if (this.xaLogger.isLoggable(Level.FINER)) {
                this.xaLogger.finer(this.toString() + xids[j].toString());
            }
        }
        return xids;
    }
    
    @Override
    public boolean isSameRM(final XAResource xares) throws XAException {
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " xares:" + xares);
        }
        if (!(xares instanceof SQLServerXAResource)) {
            return false;
        }
        final SQLServerXAResource jxa = (SQLServerXAResource)xares;
        return jxa.sResourceManagerId.equals(this.sResourceManagerId);
    }
    
    @Override
    public boolean setTransactionTimeout(final int seconds) throws XAException {
        this.isTransacrionTimeoutSet = 1;
        this.timeoutSeconds = seconds;
        if (this.xaLogger.isLoggable(Level.FINER)) {
            this.xaLogger.finer(this.toString() + " TransactionTimeout:" + seconds);
        }
        return true;
    }
    
    @Override
    public int getTransactionTimeout() throws XAException {
        return this.timeoutSeconds;
    }
    
    private static int nextResourceID() {
        return SQLServerXAResource.baseResourceID.incrementAndGet();
    }
    
    static {
        baseResourceID = new AtomicInteger(0);
        xaInitLock = new Object();
    }
}
