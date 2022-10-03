package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.Collections;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldap.sdk.unboundidds.controls.SoftDeleteRequestControl;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldif.LDIFReader;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AddAuditLogMessage extends AuditLogMessage
{
    private static final long serialVersionUID = -4103749134439291911L;
    private final Boolean isUndelete;
    private final LDIFAddChangeRecord addChangeRecord;
    private final ReadOnlyEntry undeleteRequestEntry;
    
    public AddAuditLogMessage(final String... logMessageLines) throws AuditLogException {
        this(StaticUtils.toList(logMessageLines), logMessageLines);
    }
    
    public AddAuditLogMessage(final List<String> logMessageLines) throws AuditLogException {
        this(logMessageLines, StaticUtils.toArray(logMessageLines, String.class));
    }
    
    private AddAuditLogMessage(final List<String> logMessageLineList, final String[] logMessageLineArray) throws AuditLogException {
        super(logMessageLineList);
        try {
            final LDIFChangeRecord changeRecord = LDIFReader.decodeChangeRecord(logMessageLineArray);
            if (!(changeRecord instanceof LDIFAddChangeRecord)) {
                throw new AuditLogException(logMessageLineList, LogMessages.ERR_ADD_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_ADD.get(changeRecord.getChangeType().getName(), ChangeType.ADD.getName()));
            }
            this.addChangeRecord = (LDIFAddChangeRecord)changeRecord;
        }
        catch (final LDIFException e) {
            Debug.debugException(e);
            throw new AuditLogException(logMessageLineList, LogMessages.ERR_ADD_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD.get(StaticUtils.getExceptionMessage(e)), e);
        }
        this.isUndelete = AuditLogMessage.getNamedValueAsBoolean("isUndelete", this.getHeaderNamedValues());
        this.undeleteRequestEntry = AuditLogMessage.decodeCommentedEntry("Undelete request entry", logMessageLineList, null);
    }
    
    AddAuditLogMessage(final List<String> logMessageLines, final LDIFAddChangeRecord addChangeRecord) throws AuditLogException {
        super(logMessageLines);
        this.addChangeRecord = addChangeRecord;
        this.isUndelete = AuditLogMessage.getNamedValueAsBoolean("isUndelete", this.getHeaderNamedValues());
        this.undeleteRequestEntry = AuditLogMessage.decodeCommentedEntry("Undelete request entry", logMessageLines, null);
    }
    
    @Override
    public String getDN() {
        return this.addChangeRecord.getDN();
    }
    
    public ReadOnlyEntry getEntry() {
        return new ReadOnlyEntry(this.addChangeRecord.getEntryToAdd());
    }
    
    public Boolean getIsUndelete() {
        return this.isUndelete;
    }
    
    public ReadOnlyEntry getUndeleteRequestEntry() {
        return this.undeleteRequestEntry;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.ADD;
    }
    
    @Override
    public LDIFAddChangeRecord getChangeRecord() {
        return this.addChangeRecord;
    }
    
    @Override
    public boolean isRevertible() {
        return true;
    }
    
    @Override
    public List<LDIFChangeRecord> getRevertChangeRecords() {
        if (this.isUndelete != null && this.isUndelete) {
            return (List<LDIFChangeRecord>)Collections.singletonList(new LDIFDeleteChangeRecord(SoftDeleteRequestControl.createSoftDeleteRequest(this.addChangeRecord.getDN(), false, true)));
        }
        return (List<LDIFChangeRecord>)Collections.singletonList(new LDIFDeleteChangeRecord(this.addChangeRecord.getDN()));
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.getUncommentedHeaderLine());
        buffer.append("; changeType=add; dn=\"");
        buffer.append(this.addChangeRecord.getDN());
        buffer.append('\"');
    }
}
