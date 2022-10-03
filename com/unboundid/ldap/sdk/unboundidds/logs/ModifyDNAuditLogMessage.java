package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Set;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.util.ObjectPair;
import java.util.HashSet;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import java.util.Iterator;
import java.util.Arrays;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldif.LDIFReader;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyDNAuditLogMessage extends AuditLogMessage
{
    private static final long serialVersionUID = 3954476664207635518L;
    private final LDIFModifyDNChangeRecord modifyDNChangeRecord;
    private final List<Modification> attributeModifications;
    
    public ModifyDNAuditLogMessage(final String... logMessageLines) throws AuditLogException {
        this(StaticUtils.toList(logMessageLines), logMessageLines);
    }
    
    public ModifyDNAuditLogMessage(final List<String> logMessageLines) throws AuditLogException {
        this(logMessageLines, StaticUtils.toArray(logMessageLines, String.class));
    }
    
    private ModifyDNAuditLogMessage(final List<String> logMessageLineList, final String[] logMessageLineArray) throws AuditLogException {
        super(logMessageLineList);
        try {
            final LDIFChangeRecord changeRecord = LDIFReader.decodeChangeRecord(logMessageLineArray);
            if (!(changeRecord instanceof LDIFModifyDNChangeRecord)) {
                throw new AuditLogException(logMessageLineList, LogMessages.ERR_MODIFY_DN_AUDIT_LOG_MESSAGE_CHANGE_TYPE_NOT_MODIFY_DN.get(changeRecord.getChangeType().getName(), ChangeType.MODIFY_DN.getName()));
            }
            this.modifyDNChangeRecord = (LDIFModifyDNChangeRecord)changeRecord;
        }
        catch (final LDIFException e) {
            Debug.debugException(e);
            throw new AuditLogException(logMessageLineList, LogMessages.ERR_MODIFY_DN_AUDIT_LOG_MESSAGE_LINES_NOT_CHANGE_RECORD.get(StaticUtils.getExceptionMessage(e)), e);
        }
        this.attributeModifications = decodeAttributeModifications(logMessageLineList, this.modifyDNChangeRecord);
    }
    
    ModifyDNAuditLogMessage(final List<String> logMessageLines, final LDIFModifyDNChangeRecord modifyDNChangeRecord) throws AuditLogException {
        super(logMessageLines);
        this.modifyDNChangeRecord = modifyDNChangeRecord;
        this.attributeModifications = decodeAttributeModifications(logMessageLines, modifyDNChangeRecord);
    }
    
    private static List<Modification> decodeAttributeModifications(final List<String> logMessageLines, final LDIFModifyDNChangeRecord modifyDNChangeRecord) {
        List<String> ldifLines = null;
        for (final String line : logMessageLines) {
            if (!line.startsWith("# ")) {
                break;
            }
            final String uncommentedLine = line.substring(2);
            if (ldifLines == null) {
                final String lowerLine = StaticUtils.toLowerCase(uncommentedLine);
                if (!lowerLine.startsWith("modifydn attribute modifications")) {
                    continue;
                }
                ldifLines = new ArrayList<String>(logMessageLines.size());
            }
            else {
                if (ldifLines.isEmpty()) {
                    ldifLines.add("dn: " + modifyDNChangeRecord.getDN());
                    ldifLines.add("changetype: modify");
                }
                ldifLines.add(uncommentedLine);
            }
        }
        if (ldifLines == null) {
            return null;
        }
        if (ldifLines.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            final String[] ldifLineArray = ldifLines.toArray(StaticUtils.NO_STRINGS);
            final LDIFModifyChangeRecord changeRecord = (LDIFModifyChangeRecord)LDIFReader.decodeChangeRecord(ldifLineArray);
            return Collections.unmodifiableList((List<? extends Modification>)Arrays.asList((T[])changeRecord.getModifications()));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    @Override
    public String getDN() {
        return this.modifyDNChangeRecord.getDN();
    }
    
    public String getNewRDN() {
        return this.modifyDNChangeRecord.getNewRDN();
    }
    
    public boolean deleteOldRDN() {
        return this.modifyDNChangeRecord.deleteOldRDN();
    }
    
    public String getNewSuperiorDN() {
        return this.modifyDNChangeRecord.getNewSuperiorDN();
    }
    
    public List<Modification> getAttributeModifications() {
        return this.attributeModifications;
    }
    
    @Override
    public ChangeType getChangeType() {
        return ChangeType.MODIFY_DN;
    }
    
    @Override
    public LDIFModifyDNChangeRecord getChangeRecord() {
        return this.modifyDNChangeRecord;
    }
    
    @Override
    public boolean isRevertible() {
        RDN oldRDN;
        try {
            final DN parsedDN = this.modifyDNChangeRecord.getParsedDN();
            oldRDN = parsedDN.getRDN();
            if (oldRDN == null) {
                return false;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
        RDN newRDN;
        try {
            final DN newDN = this.modifyDNChangeRecord.getNewDN();
            newRDN = this.modifyDNChangeRecord.getParsedNewRDN();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            return false;
        }
        if (this.attributeModifications == null) {
            return false;
        }
        if (this.attributeModifications.isEmpty() && this.modifyDNChangeRecord.deleteOldRDN() && !newRDN.equals(oldRDN)) {
            return false;
        }
        for (final Modification m : this.attributeModifications) {
            if (!ModifyAuditLogMessage.modificationIsRevertible(m)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public List<LDIFChangeRecord> getRevertChangeRecords() throws AuditLogException {
        if (this.attributeModifications == null) {
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_DN_NOT_REVERTIBLE.get(this.modifyDNChangeRecord.getDN()));
        }
        DN newDN;
        DN originalDN;
        DN newSuperiorDN;
        RDN newRDN;
        try {
            newDN = this.modifyDNChangeRecord.getNewDN();
            originalDN = this.modifyDNChangeRecord.getParsedDN();
            newSuperiorDN = this.modifyDNChangeRecord.getParsedNewSuperiorDN();
            newRDN = this.modifyDNChangeRecord.getParsedNewRDN();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (this.modifyDNChangeRecord.getNewSuperiorDN() == null) {
                throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_DN_CANNOT_GET_NEW_DN_WITHOUT_NEW_SUPERIOR.get(this.modifyDNChangeRecord.getDN(), this.modifyDNChangeRecord.getNewRDN()), e);
            }
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_DN_CANNOT_GET_NEW_DN_WITH_NEW_SUPERIOR.get(this.modifyDNChangeRecord.getDN(), this.modifyDNChangeRecord.getNewRDN(), this.modifyDNChangeRecord.getNewSuperiorDN()), e);
        }
        if (originalDN.isNullDN()) {
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_DN_CANNOT_REVERT_NULL_DN.get());
        }
        if (this.attributeModifications.isEmpty() && this.modifyDNChangeRecord.deleteOldRDN() && !newRDN.equals(originalDN.getRDN())) {
            throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_DN_CANNOT_REVERT_WITHOUT_NECESSARY_MODS.get(this.modifyDNChangeRecord.getDN()));
        }
        final String revertedDN = newDN.toString();
        final String revertedNewRDN = originalDN.getRDNString();
        String revertedNewSuperiorDN;
        if (newSuperiorDN == null) {
            revertedNewSuperiorDN = null;
        }
        else {
            revertedNewSuperiorDN = originalDN.getParentString();
        }
        if (this.attributeModifications.isEmpty()) {
            return (List<LDIFChangeRecord>)Collections.singletonList(new LDIFModifyDNChangeRecord(revertedDN, revertedNewRDN, false, revertedNewSuperiorDN));
        }
        final int numNewRDNs = newRDN.getAttributeNames().length;
        final Set<ObjectPair<String, byte[]>> addedNewRDNValues = new HashSet<ObjectPair<String, byte[]>>(StaticUtils.computeMapCapacity(numNewRDNs));
        final RDN originalRDN = originalDN.getRDN();
        final List<Modification> additionalModifications = new ArrayList<Modification>(this.attributeModifications.size());
        final int numModifications = this.attributeModifications.size();
        for (int i = numModifications - 1; i >= 0; --i) {
            final Modification m = this.attributeModifications.get(i);
            if (m.getModificationType() == ModificationType.ADD) {
                final Attribute a = m.getAttribute();
                final ArrayList<byte[]> retainedValues = new ArrayList<byte[]>(a.size());
                for (final ASN1OctetString value : a.getRawValues()) {
                    final byte[] valueBytes = value.getValue();
                    if (newRDN.hasAttributeValue(a.getName(), valueBytes)) {
                        addedNewRDNValues.add(new ObjectPair<String, byte[]>(a.getName(), valueBytes));
                    }
                    else {
                        retainedValues.add(valueBytes);
                    }
                }
                if (retainedValues.size() == a.size()) {
                    additionalModifications.add(new Modification(ModificationType.DELETE, a.getName(), a.getRawValues()));
                }
                else if (!retainedValues.isEmpty()) {
                    additionalModifications.add(new Modification(ModificationType.DELETE, a.getName(), (byte[][])StaticUtils.toArray(retainedValues, byte[].class)));
                }
            }
            else if (m.getModificationType() == ModificationType.DELETE) {
                final Attribute a = m.getAttribute();
                final ArrayList<byte[]> retainedValues = new ArrayList<byte[]>(a.size());
                for (final ASN1OctetString value : a.getRawValues()) {
                    final byte[] valueBytes = value.getValue();
                    if (!originalRDN.hasAttributeValue(a.getName(), valueBytes)) {
                        retainedValues.add(valueBytes);
                    }
                }
                if (retainedValues.size() == a.size()) {
                    additionalModifications.add(new Modification(ModificationType.ADD, a.getName(), a.getRawValues()));
                }
                else if (!retainedValues.isEmpty()) {
                    additionalModifications.add(new Modification(ModificationType.ADD, a.getName(), (byte[][])StaticUtils.toArray(retainedValues, byte[].class)));
                }
            }
            else {
                final Modification revertModification = ModifyAuditLogMessage.getRevertModification(m);
                if (revertModification == null) {
                    throw new AuditLogException(this.getLogMessageLines(), LogMessages.ERR_MODIFY_DN_MOD_NOT_REVERTIBLE.get(this.modifyDNChangeRecord.getDN(), m.getModificationType().getName(), m.getAttributeName()));
                }
                additionalModifications.add(revertModification);
            }
        }
        boolean revertedDeleteOldRDN;
        if (addedNewRDNValues.size() == numNewRDNs) {
            revertedDeleteOldRDN = true;
        }
        else {
            revertedDeleteOldRDN = false;
            if (!addedNewRDNValues.isEmpty()) {
                for (final ObjectPair<String, byte[]> p : addedNewRDNValues) {
                    additionalModifications.add(0, new Modification(ModificationType.DELETE, p.getFirst(), p.getSecond()));
                }
            }
        }
        final List<LDIFChangeRecord> changeRecords = new ArrayList<LDIFChangeRecord>(2);
        changeRecords.add(new LDIFModifyDNChangeRecord(revertedDN, revertedNewRDN, revertedDeleteOldRDN, revertedNewSuperiorDN));
        if (!additionalModifications.isEmpty()) {
            changeRecords.add(new LDIFModifyChangeRecord(originalDN.toString(), additionalModifications));
        }
        return Collections.unmodifiableList((List<? extends LDIFChangeRecord>)changeRecords);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.getUncommentedHeaderLine());
        buffer.append("; changeType=modify-dn; dn=\"");
        buffer.append(this.modifyDNChangeRecord.getDN());
        buffer.append("\", newRDN=\"");
        buffer.append(this.modifyDNChangeRecord.getNewRDN());
        buffer.append("\", deleteOldRDN=");
        buffer.append(this.modifyDNChangeRecord.deleteOldRDN());
        final String newSuperiorDN = this.modifyDNChangeRecord.getNewSuperiorDN();
        if (newSuperiorDN != null) {
            buffer.append(", newSuperiorDN=\"");
            buffer.append(newSuperiorDN);
            buffer.append('\"');
        }
    }
}
