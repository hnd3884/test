package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldap.sdk.unboundidds.controls.UndeleteRequestControl;
import com.unboundid.ldif.LDIFChangeRecord;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldif.LDIFReader;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeleteAuditLogMessage extends AuditLogMessage
{
    private static final long serialVersionUID = 2082830761413726711L;
    private final Boolean deletedAsPartOfSubtreeDelete;
    private final Boolean isSubtreeDelete;
    private final Boolean isSoftDelete;
    private final Boolean isSoftDeletedEntry;
    private final LDIFDeleteChangeRecord deleteChangeRecord;
    private final List<Attribute> deletedEntryVirtualAttributes;
    private final ReadOnlyEntry deletedEntry;
    private final String softDeletedEntryDN;
    
    public DeleteAuditLogMessage(final String... logMessageLines) throws AuditLogException {
        this(StaticUtils.toList(logMessageLines), logMessageLines);
    }
    
    public DeleteAuditLogMessage(final List<String> logMessageLines) throws AuditLogException {
        this(logMessageLines, StaticUtils.toArray(logMessageLines, String.class));
    }
    
    private DeleteAuditLogMessage(final List<String> logMessageLineList, final String[] logMessageLineArray) throws AuditLogException {
        super(logMessageLineList);
        try {
            final LDIFChangeRecord changeRecord = LDIFReader.decodeChangeRecord(logMessageLineArray);
            if (!(changeRecord instanceof LDIFDeleteChangeRecord)) {
                throw new AuditLogException(logMessageLineList, LogMessages.ERR_DELETE_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_DELETE.get(changeRecord.getChangeType().getName(), ChangeType.DELETE.getName()));
            }
            this.deleteChangeRecord = (LDIFDeleteChangeRecord)changeRecord;
        }
        catch (final LDIFException e) {
            Debug.debugException(e);
            throw new AuditLogException(logMessageLineList, LogMessages.ERR_DELETE_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD.get(StaticUtils.getExceptionMessage(e)), e);
        }
        this.deletedAsPartOfSubtreeDelete = AuditLogMessage.getNamedValueAsBoolean("deletedAsPartOfSubtreeDelete", this.getHeaderNamedValues());
        this.isSubtreeDelete = AuditLogMessage.getNamedValueAsBoolean("isSubtreeDelete", this.getHeaderNamedValues());
        this.isSoftDelete = AuditLogMessage.getNamedValueAsBoolean("isSoftDelete", this.getHeaderNamedValues());
        this.isSoftDeletedEntry = AuditLogMessage.getNamedValueAsBoolean("isSoftDeletedEntry", this.getHeaderNamedValues());
        this.softDeletedEntryDN = this.getHeaderNamedValues().get("softDeletedEntryDN");
        this.deletedEntry = AuditLogMessage.decodeCommentedEntry("Deleted entry real attributes", logMessageLineList, this.deleteChangeRecord.getDN());
        final ReadOnlyEntry virtualAttributeEntry = AuditLogMessage.decodeCommentedEntry("Deleted entry virtual attributes", logMessageLineList, this.deleteChangeRecord.getDN());
        if (virtualAttributeEntry == null) {
            this.deletedEntryVirtualAttributes = null;
        }
        else {
            this.deletedEntryVirtualAttributes = Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(virtualAttributeEntry.getAttributes()));
        }
    }
    
    DeleteAuditLogMessage(final List<String> logMessageLines, final LDIFDeleteChangeRecord deleteChangeRecord) throws AuditLogException {
        super(logMessageLines);
        this.deleteChangeRecord = deleteChangeRecord;
        this.deletedAsPartOfSubtreeDelete = AuditLogMessage.getNamedValueAsBoolean("deletedAsPartOfSubtreeDelete", this.getHeaderNamedValues());
        this.isSubtreeDelete = AuditLogMessage.getNamedValueAsBoolean("isSubtreeDelete", this.getHeaderNamedValues());
        this.isSoftDelete = AuditLogMessage.getNamedValueAsBoolean("isSoftDelete", this.getHeaderNamedValues());
        this.isSoftDeletedEntry = AuditLogMessage.getNamedValueAsBoolean("isSoftDeletedEntry", this.getHeaderNamedValues());
        this.softDeletedEntryDN = this.getHeaderNamedValues().get("softDeletedEntryDN");
        this.deletedEntry = AuditLogMessage.decodeCommentedEntry("Deleted entry real attributes", logMessageLines, deleteChangeRecord.getDN());
        final ReadOnlyEntry virtualAttributeEntry = AuditLogMessage.decodeCommentedEntry("Deleted entry virtual attributes", logMessageLines, deleteChangeRecord.getDN());
        if (virtualAttributeEntry == null) {
            this.deletedEntryVirtualAttributes = null;
        }
        else {
            this.deletedEntryVirtualAttributes = Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(virtualAttributeEntry.getAttributes()));
        }
    }
    
    @Override
    public String getDN() {
        return this.deleteChangeRecord.getDN();
    }
    
    public Boolean getIsSubtreeDelete() {
        return this.isSubtreeDelete;
    }
    
    public Boolean getDeletedAsPartOfSubtreeDelete() {
        return this.deletedAsPartOfSubtreeDelete;
    }
    
    public Boolean getIsSoftDelete() {
        return this.isSoftDelete;
    }
    
    public String getSoftDeletedEntryDN() {
        return this.softDeletedEntryDN;
    }
    
    public Boolean getIsSoftDeletedEntry() {
        return this.isSoftDeletedEntry;
    }
    
    public ReadOnlyEntry getDeletedEntry() {
        return this.deletedEntry;
    }
    
    public List<Attribute> getDeletedEntryVirtualAttributes() {
        return this.deletedEntryVirtualAttributes;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.DELETE;
    }
    
    @Override
    public LDIFDeleteChangeRecord getChangeRecord() {
        return this.deleteChangeRecord;
    }
    
    @Override
    public boolean isRevertible() {
        if (this.isSubtreeDelete != null && this.isSubtreeDelete) {
            return false;
        }
        if (this.isSoftDelete != null && this.isSoftDelete) {
            return this.softDeletedEntryDN != null;
        }
        return this.deletedEntry != null;
    }
    
    @Override
    public List<LDIFChangeRecord> getRevertChangeRecords() throws AuditLogException {
        if (this.isSubtreeDelete != null && this.isSubtreeDelete) {
            if (this.deletedEntry == null) {
                throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_DELETE_AUDIT_LOG_MESSAGE_SUBTREE_DELETE_WITHOUT_ENTRY.get(this.deleteChangeRecord.getDN()));
            }
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_DELETE_AUDIT_LOG_MESSAGE_SUBTREE_DELETE_WITH_ENTRY.get(this.deleteChangeRecord.getDN()));
        }
        else if (this.isSoftDelete != null && this.isSoftDelete) {
            if (this.softDeletedEntryDN != null) {
                return (List<LDIFChangeRecord>)Collections.singletonList(new LDIFAddChangeRecord(UndeleteRequestControl.createUndeleteRequest(this.deleteChangeRecord.getDN(), this.softDeletedEntryDN)));
            }
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_DELETE_AUDIT_LOG_MESSAGE_NO_SOFT_DELETED_ENTRY_DN.get(this.deleteChangeRecord.getDN()));
        }
        else {
            if (this.deletedEntry != null) {
                return (List<LDIFChangeRecord>)Collections.singletonList(new LDIFAddChangeRecord(this.deletedEntry));
            }
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_DELETE_AUDIT_LOG_MESSAGE_DELETED_ENTRY.get(this.deleteChangeRecord.getDN()));
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.getUncommentedHeaderLine());
        buffer.append("; changeType=delete; dn=\"");
        buffer.append(this.deleteChangeRecord.getDN());
        buffer.append('\"');
    }
}
