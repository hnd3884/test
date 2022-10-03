package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.ldap.sdk.ModificationType;
import java.util.Collections;
import java.util.Arrays;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldif.LDIFReader;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyAuditLogMessage extends AuditLogMessage
{
    private static final long serialVersionUID = -5262466264778465574L;
    private final Boolean isSoftDeletedEntry;
    private final LDIFModifyChangeRecord modifyChangeRecord;
    
    public ModifyAuditLogMessage(final String... logMessageLines) throws AuditLogException {
        this(StaticUtils.toList(logMessageLines), logMessageLines);
    }
    
    public ModifyAuditLogMessage(final List<String> logMessageLines) throws AuditLogException {
        this(logMessageLines, StaticUtils.toArray(logMessageLines, String.class));
    }
    
    private ModifyAuditLogMessage(final List<String> logMessageLineList, final String[] logMessageLineArray) throws AuditLogException {
        super(logMessageLineList);
        try {
            final LDIFChangeRecord changeRecord = LDIFReader.decodeChangeRecord(logMessageLineArray);
            if (!(changeRecord instanceof LDIFModifyChangeRecord)) {
                throw new AuditLogException(logMessageLineList, LogMessages.ERR_MODIFY_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_MODIFY.get(changeRecord.getChangeType().getName(), ChangeType.MODIFY.getName()));
            }
            this.modifyChangeRecord = (LDIFModifyChangeRecord)changeRecord;
        }
        catch (final LDIFException e) {
            Debug.debugException(e);
            throw new AuditLogException(logMessageLineList, LogMessages.ERR_MODIFY_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD.get(StaticUtils.getExceptionMessage(e)), e);
        }
        this.isSoftDeletedEntry = AuditLogMessage.getNamedValueAsBoolean("isSoftDeletedEntry", this.getHeaderNamedValues());
    }
    
    ModifyAuditLogMessage(final List<String> logMessageLines, final LDIFModifyChangeRecord modifyChangeRecord) throws AuditLogException {
        super(logMessageLines);
        this.modifyChangeRecord = modifyChangeRecord;
        this.isSoftDeletedEntry = AuditLogMessage.getNamedValueAsBoolean("isSoftDeletedEntry", this.getHeaderNamedValues());
    }
    
    @Override
    public String getDN() {
        return this.modifyChangeRecord.getDN();
    }
    
    public List<Modification> getModifications() {
        return Collections.unmodifiableList((List<? extends Modification>)Arrays.asList((T[])this.modifyChangeRecord.getModifications()));
    }
    
    public Boolean getIsSoftDeletedEntry() {
        return this.isSoftDeletedEntry;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.MODIFY;
    }
    
    @Override
    public LDIFModifyChangeRecord getChangeRecord() {
        return this.modifyChangeRecord;
    }
    
    @Override
    public boolean isRevertible() {
        for (final Modification m : this.modifyChangeRecord.getModifications()) {
            if (!modificationIsRevertible(m)) {
                return false;
            }
        }
        return true;
    }
    
    static boolean modificationIsRevertible(final Modification m) {
        switch (m.getModificationType().intValue()) {
            case 0:
            case 3: {
                return true;
            }
            case 1: {
                return m.hasValue();
            }
            default: {
                return false;
            }
        }
    }
    
    static Modification getRevertModification(final Modification m) {
        switch (m.getModificationType().intValue()) {
            case 0: {
                return new Modification(ModificationType.DELETE, m.getAttributeName(), m.getRawValues());
            }
            case 3: {
                final String firstValue = m.getValues()[0];
                if (firstValue.startsWith("-")) {
                    return new Modification(ModificationType.INCREMENT, m.getAttributeName(), firstValue.substring(1));
                }
                return new Modification(ModificationType.INCREMENT, m.getAttributeName(), '-' + firstValue);
            }
            case 1: {
                if (m.hasValue()) {
                    return new Modification(ModificationType.ADD, m.getAttributeName(), m.getRawValues());
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public List<LDIFChangeRecord> getRevertChangeRecords() throws AuditLogException {
        final Modification[] mods = this.modifyChangeRecord.getModifications();
        final Modification[] revertMods = new Modification[mods.length];
        for (int i = mods.length - 1, j = 0; i >= 0; --i, ++j) {
            revertMods[j] = getRevertModification(mods[i]);
            if (revertMods[j] == null) {
                throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_AUDIT_LOG_MESSAGE_MOD_NOT_REVERTIBLE.get(this.modifyChangeRecord.getDN(), String.valueOf(mods[i])));
            }
        }
        return (List<LDIFChangeRecord>)Collections.singletonList(new LDIFModifyChangeRecord(this.modifyChangeRecord.getDN(), revertMods));
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.getUncommentedHeaderLine());
        buffer.append("; changeType=modify; dn=\"");
        buffer.append(this.modifyChangeRecord.getDN());
        buffer.append('\"');
    }
}
