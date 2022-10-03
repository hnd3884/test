package com.unboundid.ldap.sdk;

import com.unboundid.ldif.LDIFDeleteChangeRecord;
import java.util.Arrays;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import java.nio.charset.StandardCharsets;
import com.unboundid.ldif.LDIFException;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.ldif.TrailingSpaceBehavior;
import java.util.StringTokenizer;
import java.util.ArrayList;
import com.unboundid.ldap.matchingrules.BooleanMatchingRule;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldap.matchingrules.OctetStringMatchingRule;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldap.matchingrules.DistinguishedNameMatchingRule;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.IntegerMatchingRule;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ChangeLogEntry extends ReadOnlyEntry
{
    public static final String ATTR_CHANGE_NUMBER = "changeNumber";
    public static final String ATTR_TARGET_DN = "targetDN";
    public static final String ATTR_CHANGE_TYPE = "changeType";
    public static final String ATTR_CHANGES = "changes";
    public static final String ATTR_NEW_RDN = "newRDN";
    public static final String ATTR_DELETE_OLD_RDN = "deleteOldRDN";
    public static final String ATTR_NEW_SUPERIOR = "newSuperior";
    public static final String ATTR_DELETED_ENTRY_ATTRS = "deletedEntryAttrs";
    private static final long serialVersionUID = -4018129098468341663L;
    private final boolean deleteOldRDN;
    private final ChangeType changeType;
    private final List<Attribute> attributes;
    private final List<Modification> modifications;
    private final long changeNumber;
    private final String newRDN;
    private final String newSuperior;
    private final String targetDN;
    
    public ChangeLogEntry(final Entry entry) throws LDAPException {
        super(entry);
        final Attribute changeNumberAttr = entry.getAttribute("changeNumber");
        if (changeNumberAttr == null || !changeNumberAttr.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_NO_CHANGE_NUMBER.get());
        }
        try {
            this.changeNumber = Long.parseLong(changeNumberAttr.getValue());
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_INVALID_CHANGE_NUMBER.get(changeNumberAttr.getValue()), nfe);
        }
        final Attribute targetDNAttr = entry.getAttribute("targetDN");
        if (targetDNAttr == null || !targetDNAttr.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_NO_TARGET_DN.get());
        }
        this.targetDN = targetDNAttr.getValue();
        final Attribute changeTypeAttr = entry.getAttribute("changeType");
        if (changeTypeAttr == null || !changeTypeAttr.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_NO_CHANGE_TYPE.get());
        }
        this.changeType = ChangeType.forName(changeTypeAttr.getValue());
        if (this.changeType == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_INVALID_CHANGE_TYPE.get(changeTypeAttr.getValue()));
        }
        switch (this.changeType) {
            case ADD: {
                this.attributes = parseAddAttributeList(entry, "changes", this.targetDN);
                this.modifications = null;
                this.newRDN = null;
                this.deleteOldRDN = false;
                this.newSuperior = null;
                break;
            }
            case DELETE: {
                this.attributes = parseDeletedAttributeList(entry, this.targetDN);
                this.modifications = null;
                this.newRDN = null;
                this.deleteOldRDN = false;
                this.newSuperior = null;
                break;
            }
            case MODIFY: {
                this.attributes = null;
                this.modifications = parseModificationList(entry, this.targetDN);
                this.newRDN = null;
                this.deleteOldRDN = false;
                this.newSuperior = null;
                break;
            }
            case MODIFY_DN: {
                this.attributes = null;
                this.modifications = parseModificationList(entry, this.targetDN);
                this.newSuperior = this.getAttributeValue("newSuperior");
                final Attribute newRDNAttr = this.getAttribute("newRDN");
                if (newRDNAttr == null || !newRDNAttr.hasValue()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_MISSING_NEW_RDN.get());
                }
                this.newRDN = newRDNAttr.getValue();
                final Attribute deleteOldRDNAttr = this.getAttribute("deleteOldRDN");
                if (deleteOldRDNAttr == null || !deleteOldRDNAttr.hasValue()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_MISSING_DELETE_OLD_RDN.get());
                }
                final String delOldRDNStr = StaticUtils.toLowerCase(deleteOldRDNAttr.getValue());
                if (delOldRDNStr.equals("true")) {
                    this.deleteOldRDN = true;
                    break;
                }
                if (delOldRDNStr.equals("false")) {
                    this.deleteOldRDN = false;
                    break;
                }
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_MISSING_DELETE_OLD_RDN.get(delOldRDNStr));
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_INVALID_CHANGE_TYPE.get(changeTypeAttr.getValue()));
            }
        }
    }
    
    public static ChangeLogEntry constructChangeLogEntry(final long changeNumber, final LDIFChangeRecord changeRecord) throws LDAPException {
        final Entry e = new Entry("changeNumber=" + changeNumber + ",cn=changelog");
        e.addAttribute("objectClass", "top", "changeLogEntry");
        e.addAttribute(new Attribute("changeNumber", IntegerMatchingRule.getInstance(), String.valueOf(changeNumber)));
        e.addAttribute(new Attribute("targetDN", DistinguishedNameMatchingRule.getInstance(), changeRecord.getDN()));
        e.addAttribute("changeType", changeRecord.getChangeType().getName());
        switch (changeRecord.getChangeType()) {
            case ADD: {
                final LDIFAddChangeRecord addRecord = (LDIFAddChangeRecord)changeRecord;
                final Entry addEntry = new Entry(addRecord.getDN(), addRecord.getAttributes());
                final String[] entryLdifLines = addEntry.toLDIF(0);
                final StringBuilder entryLDIFBuffer = new StringBuilder();
                for (int i = 1; i < entryLdifLines.length; ++i) {
                    entryLDIFBuffer.append(entryLdifLines[i]);
                    entryLDIFBuffer.append(StaticUtils.EOL);
                }
                e.addAttribute(new Attribute("changes", OctetStringMatchingRule.getInstance(), entryLDIFBuffer.toString()));
            }
            case MODIFY: {
                final String[] modLdifLines = changeRecord.toLDIF(0);
                final StringBuilder modLDIFBuffer = new StringBuilder();
                for (int j = 2; j < modLdifLines.length; ++j) {
                    modLDIFBuffer.append(modLdifLines[j]);
                    modLDIFBuffer.append(StaticUtils.EOL);
                }
                e.addAttribute(new Attribute("changes", OctetStringMatchingRule.getInstance(), modLDIFBuffer.toString()));
                break;
            }
            case MODIFY_DN: {
                final LDIFModifyDNChangeRecord modDNRecord = (LDIFModifyDNChangeRecord)changeRecord;
                e.addAttribute(new Attribute("newRDN", DistinguishedNameMatchingRule.getInstance(), modDNRecord.getNewRDN()));
                e.addAttribute(new Attribute("deleteOldRDN", BooleanMatchingRule.getInstance(), modDNRecord.deleteOldRDN() ? "TRUE" : "FALSE"));
                if (modDNRecord.getNewSuperiorDN() != null) {
                    e.addAttribute(new Attribute("newSuperior", DistinguishedNameMatchingRule.getInstance(), modDNRecord.getNewSuperiorDN()));
                    break;
                }
                break;
            }
        }
        return new ChangeLogEntry(e);
    }
    
    protected static List<Attribute> parseAddAttributeList(final Entry entry, final String attrName, final String targetDN) throws LDAPException {
        final Attribute changesAttr = entry.getAttribute(attrName);
        if (changesAttr == null || !changesAttr.hasValue()) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_MISSING_CHANGES.get());
        }
        final ArrayList<String> ldifLines = new ArrayList<String>(20);
        ldifLines.add("dn: " + targetDN);
        final StringTokenizer tokenizer = new StringTokenizer(changesAttr.getValue(), "\r\n");
        while (tokenizer.hasMoreTokens()) {
            ldifLines.add(tokenizer.nextToken());
        }
        final String[] lineArray = new String[ldifLines.size()];
        ldifLines.toArray(lineArray);
        try {
            final Entry e = LDIFReader.decodeEntry(true, TrailingSpaceBehavior.RETAIN, null, lineArray);
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        catch (final LDIFException le) {
            Debug.debugException(le);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_CANNOT_PARSE_ATTR_LIST.get(attrName, StaticUtils.getExceptionMessage(le)), le);
        }
    }
    
    private static List<Attribute> parseDeletedAttributeList(final Entry entry, final String targetDN) throws LDAPException {
        final Attribute deletedEntryAttrs = entry.getAttribute("deletedEntryAttrs");
        if (deletedEntryAttrs == null || !deletedEntryAttrs.hasValue()) {
            return null;
        }
        final byte[] valueBytes = deletedEntryAttrs.getValueByteArray();
        if (valueBytes.length > 0 && valueBytes[valueBytes.length - 1] == 0) {
            final String valueStr = new String(valueBytes, 0, valueBytes.length - 2, StandardCharsets.UTF_8);
            final ArrayList<String> ldifLines = new ArrayList<String>(20);
            ldifLines.add("dn: " + targetDN);
            ldifLines.add("changetype: modify");
            final StringTokenizer tokenizer = new StringTokenizer(valueStr, "\r\n");
            while (tokenizer.hasMoreTokens()) {
                ldifLines.add(tokenizer.nextToken());
            }
            final String[] lineArray = new String[ldifLines.size()];
            ldifLines.toArray(lineArray);
            try {
                final LDIFModifyChangeRecord changeRecord = (LDIFModifyChangeRecord)LDIFReader.decodeChangeRecord(lineArray);
                final Modification[] mods = changeRecord.getModifications();
                final ArrayList<Attribute> attrs = new ArrayList<Attribute>(mods.length);
                for (final Modification m : mods) {
                    if (!m.getModificationType().equals(ModificationType.DELETE)) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_INVALID_DELENTRYATTRS_MOD_TYPE.get("deletedEntryAttrs"));
                    }
                    attrs.add(m.getAttribute());
                }
                return Collections.unmodifiableList((List<? extends Attribute>)attrs);
            }
            catch (final LDIFException le) {
                Debug.debugException(le);
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_INVALID_DELENTRYATTRS_MODS.get("deletedEntryAttrs", StaticUtils.getExceptionMessage(le)), le);
            }
        }
        final ArrayList<String> ldifLines2 = new ArrayList<String>(20);
        ldifLines2.add("dn: " + targetDN);
        final StringTokenizer tokenizer2 = new StringTokenizer(deletedEntryAttrs.getValue(), "\r\n");
        while (tokenizer2.hasMoreTokens()) {
            ldifLines2.add(tokenizer2.nextToken());
        }
        final String[] lineArray2 = new String[ldifLines2.size()];
        ldifLines2.toArray(lineArray2);
        try {
            final Entry e = LDIFReader.decodeEntry(true, TrailingSpaceBehavior.RETAIN, null, lineArray2);
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        catch (final LDIFException le2) {
            Debug.debugException(le2);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_CANNOT_PARSE_DELENTRYATTRS.get("deletedEntryAttrs", StaticUtils.getExceptionMessage(le2)), le2);
        }
    }
    
    private static List<Modification> parseModificationList(final Entry entry, final String targetDN) throws LDAPException {
        final Attribute changesAttr = entry.getAttribute("changes");
        if (changesAttr == null || !changesAttr.hasValue()) {
            return null;
        }
        final byte[] valueBytes = changesAttr.getValueByteArray();
        if (valueBytes.length == 0) {
            return null;
        }
        final ArrayList<String> ldifLines = new ArrayList<String>(20);
        ldifLines.add("dn: " + targetDN);
        ldifLines.add("changetype: modify");
        StringTokenizer tokenizer;
        if (valueBytes.length > 0 && valueBytes[valueBytes.length - 1] == 0) {
            final String fullValue = changesAttr.getValue();
            final String realValue = fullValue.substring(0, fullValue.length() - 2);
            tokenizer = new StringTokenizer(realValue, "\r\n");
        }
        else {
            tokenizer = new StringTokenizer(changesAttr.getValue(), "\r\n");
        }
        while (tokenizer.hasMoreTokens()) {
            ldifLines.add(tokenizer.nextToken());
        }
        final String[] lineArray = new String[ldifLines.size()];
        ldifLines.toArray(lineArray);
        try {
            final LDIFModifyChangeRecord changeRecord = (LDIFModifyChangeRecord)LDIFReader.decodeChangeRecord(lineArray);
            return Collections.unmodifiableList((List<? extends Modification>)Arrays.asList((T[])changeRecord.getModifications()));
        }
        catch (final LDIFException le) {
            Debug.debugException(le);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_CHANGELOG_CANNOT_PARSE_MOD_LIST.get("changes", StaticUtils.getExceptionMessage(le)), le);
        }
    }
    
    public final long getChangeNumber() {
        return this.changeNumber;
    }
    
    public final String getTargetDN() {
        return this.targetDN;
    }
    
    public final ChangeType getChangeType() {
        return this.changeType;
    }
    
    public final List<Attribute> getAddAttributes() {
        if (this.changeType == ChangeType.ADD) {
            return this.attributes;
        }
        return null;
    }
    
    public final List<Attribute> getDeletedEntryAttributes() {
        if (this.changeType == ChangeType.DELETE) {
            return this.attributes;
        }
        return null;
    }
    
    public final List<Modification> getModifications() {
        return this.modifications;
    }
    
    public final String getNewRDN() {
        return this.newRDN;
    }
    
    public final boolean deleteOldRDN() {
        return this.deleteOldRDN;
    }
    
    public final String getNewSuperior() {
        return this.newSuperior;
    }
    
    public final String getNewDN() {
        switch (this.changeType) {
            case ADD:
            case MODIFY: {
                return this.targetDN;
            }
            case MODIFY_DN: {
                try {
                    final RDN parsedNewRDN = new RDN(this.newRDN);
                    if (this.newSuperior != null) {
                        final DN parsedNewSuperior = new DN(this.newSuperior);
                        return new DN(parsedNewRDN, parsedNewSuperior).toString();
                    }
                    final DN parsedTargetDN = new DN(this.targetDN);
                    final DN parentDN = parsedTargetDN.getParent();
                    if (parentDN == null) {
                        return new DN(new RDN[] { parsedNewRDN }).toString();
                    }
                    return new DN(parsedNewRDN, parentDN).toString();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return null;
                }
                break;
            }
            default: {
                return null;
            }
        }
    }
    
    public final LDIFChangeRecord toLDIFChangeRecord() {
        switch (this.changeType) {
            case ADD: {
                return new LDIFAddChangeRecord(this.targetDN, this.attributes);
            }
            case DELETE: {
                return new LDIFDeleteChangeRecord(this.targetDN);
            }
            case MODIFY: {
                return new LDIFModifyChangeRecord(this.targetDN, this.modifications);
            }
            case MODIFY_DN: {
                return new LDIFModifyDNChangeRecord(this.targetDN, this.newRDN, this.deleteOldRDN, this.newSuperior);
            }
            default: {
                return null;
            }
        }
    }
    
    public final LDAPResult processChange(final LDAPInterface connection) throws LDAPException {
        switch (this.changeType) {
            case ADD: {
                return connection.add(this.targetDN, this.attributes);
            }
            case DELETE: {
                return connection.delete(this.targetDN);
            }
            case MODIFY: {
                return connection.modify(this.targetDN, this.modifications);
            }
            case MODIFY_DN: {
                return connection.modifyDN(this.targetDN, this.newRDN, this.deleteOldRDN, this.newSuperior);
            }
            default: {
                return null;
            }
        }
    }
}
