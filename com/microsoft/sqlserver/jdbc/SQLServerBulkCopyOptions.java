package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;
import java.io.Serializable;

public class SQLServerBulkCopyOptions implements Serializable
{
    private static final long serialVersionUID = 711570696894155194L;
    private int batchSize;
    private int bulkCopyTimeout;
    private boolean checkConstraints;
    private boolean fireTriggers;
    private boolean keepIdentity;
    private boolean keepNulls;
    private boolean tableLock;
    private boolean useInternalTransaction;
    private boolean allowEncryptedValueModifications;
    
    public SQLServerBulkCopyOptions() {
        this.batchSize = 0;
        this.bulkCopyTimeout = 60;
        this.checkConstraints = false;
        this.fireTriggers = false;
        this.keepIdentity = false;
        this.keepNulls = false;
        this.tableLock = false;
        this.useInternalTransaction = false;
        this.allowEncryptedValueModifications = false;
    }
    
    public int getBatchSize() {
        return this.batchSize;
    }
    
    public void setBatchSize(final int batchSize) throws SQLServerException {
        if (batchSize >= 0) {
            this.batchSize = batchSize;
        }
        else {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidNegativeArg"));
            final Object[] msgArgs = { "batchSize" };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
    }
    
    public int getBulkCopyTimeout() {
        return this.bulkCopyTimeout;
    }
    
    public void setBulkCopyTimeout(final int timeout) throws SQLServerException {
        if (timeout >= 0) {
            this.bulkCopyTimeout = timeout;
        }
        else {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidNegativeArg"));
            final Object[] msgArgs = { "timeout" };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
    }
    
    public boolean isKeepIdentity() {
        return this.keepIdentity;
    }
    
    public void setKeepIdentity(final boolean keepIdentity) {
        this.keepIdentity = keepIdentity;
    }
    
    public boolean isKeepNulls() {
        return this.keepNulls;
    }
    
    public void setKeepNulls(final boolean keepNulls) {
        this.keepNulls = keepNulls;
    }
    
    public boolean isTableLock() {
        return this.tableLock;
    }
    
    public void setTableLock(final boolean tableLock) {
        this.tableLock = tableLock;
    }
    
    public boolean isUseInternalTransaction() {
        return this.useInternalTransaction;
    }
    
    public void setUseInternalTransaction(final boolean useInternalTransaction) {
        this.useInternalTransaction = useInternalTransaction;
    }
    
    public boolean isCheckConstraints() {
        return this.checkConstraints;
    }
    
    public void setCheckConstraints(final boolean checkConstraints) {
        this.checkConstraints = checkConstraints;
    }
    
    public boolean isFireTriggers() {
        return this.fireTriggers;
    }
    
    public void setFireTriggers(final boolean fireTriggers) {
        this.fireTriggers = fireTriggers;
    }
    
    public boolean isAllowEncryptedValueModifications() {
        return this.allowEncryptedValueModifications;
    }
    
    public void setAllowEncryptedValueModifications(final boolean allowEncryptedValueModifications) {
        this.allowEncryptedValueModifications = allowEncryptedValueModifications;
    }
}
