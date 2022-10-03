package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.Modification;
import java.util.Iterator;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ChangeLogEntry;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnboundIDChangeLogEntry extends ChangeLogEntry
{
    public static final String ATTR_BEFORE_VALUES = "ds-changelog-before-values";
    public static final String ATTR_AFTER_VALUES = "ds-changelog-after-values";
    public static final String ATTR_CHANGE_TO_SOFT_DELETED_ENTRY = "ds-change-to-soft-deleted-entry";
    public static final String ATTR_KEY_VALUES = "ds-changelog-entry-key-attr-values";
    public static final String ATTR_EXCEEDED_MAX_VALUES = "ds-changelog-attr-exceeded-max-values-count";
    public static final String ATTR_EXCLUDED_USER_ATTR_COUNT = "ds-changelog-num-excluded-user-attributes";
    public static final String ATTR_EXCLUDED_OPERATIONAL_ATTR_COUNT = "ds-changelog-num-excluded-operational-attributes";
    public static final String ATTR_EXCLUDED_USER_ATTR_NAME = "ds-changelog-excluded-user-attribute";
    public static final String ATTR_EXCLUDED_OPERATIONAL_ATTR_NAME = "ds-changelog-excluded-operational-attribute";
    public static final String ATTR_TARGET_UNIQUE_ID = "targetUniqueID";
    public static final String ATTR_CHANGE_TIME = "changeTime";
    public static final String ATTR_LOCAL_CSN = "localCSN";
    public static final String ATTR_SOFT_DELETE_TO_DN = "ds-soft-delete-entry-dn";
    public static final String ATTR_TARGET_ATTRIBUTE = "ds-changelog-target-attribute";
    public static final String ATTR_UNDELETE_FROM_DN = "ds-undelete-from-dn";
    public static final String ATTR_VIRTUAL_ATTRS = "ds-changelog-virtual-attributes";
    public static final String ATTR_BEFORE_VIRTUAL_VALUES = "ds-changelog-before-virtual-values";
    public static final String ATTR_AFTER_VIRTUAL_VALUES = "ds-changelog-after-virtual-values";
    public static final String ATTR_KEY_VIRTUAL_VALUES = "ds-changelog-entry-key-virtual-values";
    public static final String ATTR_VIRTUAL_EXCEEDED_MAX_VALUES = "ds-changelog-virtual-attr-exceeded-max-values-count";
    public static final String ATTR_NOTIFICATION_DESTINATION_ENTRY_UUID = "ds-notification-destination-entry-uuid";
    public static final String ATTR_NOTIFICATION_PROPERTIES = "ds-changelog-notification-properties";
    private static final long serialVersionUID = -6127912254495185946L;
    private final Boolean changeToSoftDeletedEntry;
    private final Date changeTime;
    private final Integer numExcludedUserAttributes;
    private final Integer numExcludedOperationalAttributes;
    private final List<Attribute> entryVirtualAttributes;
    private final List<Attribute> keyEntryAttributes;
    private final List<Attribute> keyEntryVirtualAttributes;
    private final List<Attribute> updatedAttributesAfterChange;
    private final List<Attribute> updatedAttributesBeforeChange;
    private final List<Attribute> updatedVirtualAttributesAfterChange;
    private final List<Attribute> updatedVirtualAttributesBeforeChange;
    private final List<ChangeLogEntryAttributeExceededMaxValuesCount> attributesThatExceededMaxValuesCount;
    private final List<ChangeLogEntryAttributeExceededMaxValuesCount> virtualAttributesThatExceededMaxValuesCount;
    private final List<String> excludedUserAttributeNames;
    private final List<String> excludedOperationalAttributeNames;
    private final List<String> notificationDestinationEntryUUIDs;
    private final List<String> notificationProperties;
    private final List<String> targetAttributeNames;
    private final String localCSN;
    private final String softDeleteToDN;
    private final String targetUniqueID;
    private final String undeleteFromDN;
    
    public UnboundIDChangeLogEntry(final Entry entry) throws LDAPException {
        super(entry);
        final String targetDN = entry.getAttributeValue("targetDN");
        this.targetUniqueID = entry.getAttributeValue("targetUniqueID");
        this.localCSN = entry.getAttributeValue("localCSN");
        this.changeTime = entry.getAttributeValueAsDate("changeTime");
        this.softDeleteToDN = entry.getAttributeValue("ds-soft-delete-entry-dn");
        this.undeleteFromDN = entry.getAttributeValue("ds-undelete-from-dn");
        this.changeToSoftDeletedEntry = entry.getAttributeValueAsBoolean("ds-change-to-soft-deleted-entry");
        if (entry.hasAttribute("ds-changelog-virtual-attributes")) {
            this.entryVirtualAttributes = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-virtual-attributes", targetDN);
        }
        else {
            this.entryVirtualAttributes = Collections.emptyList();
        }
        if (entry.hasAttribute("ds-changelog-before-values")) {
            this.updatedAttributesBeforeChange = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-before-values", targetDN);
        }
        else {
            this.updatedAttributesBeforeChange = Collections.emptyList();
        }
        if (entry.hasAttribute("ds-changelog-before-virtual-values")) {
            this.updatedVirtualAttributesBeforeChange = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-before-virtual-values", targetDN);
        }
        else {
            this.updatedVirtualAttributesBeforeChange = Collections.emptyList();
        }
        if (entry.hasAttribute("ds-changelog-after-values")) {
            this.updatedAttributesAfterChange = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-after-values", targetDN);
        }
        else {
            this.updatedAttributesAfterChange = Collections.emptyList();
        }
        if (entry.hasAttribute("ds-changelog-after-virtual-values")) {
            this.updatedVirtualAttributesAfterChange = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-after-virtual-values", targetDN);
        }
        else {
            this.updatedVirtualAttributesAfterChange = Collections.emptyList();
        }
        if (entry.hasAttribute("ds-changelog-entry-key-attr-values")) {
            this.keyEntryAttributes = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-entry-key-attr-values", targetDN);
        }
        else {
            this.keyEntryAttributes = Collections.emptyList();
        }
        if (entry.hasAttribute("ds-changelog-entry-key-virtual-values")) {
            this.keyEntryVirtualAttributes = ChangeLogEntry.parseAddAttributeList(entry, "ds-changelog-entry-key-virtual-values", targetDN);
        }
        else {
            this.keyEntryVirtualAttributes = Collections.emptyList();
        }
        final Attribute exceededMaxValues = entry.getAttribute("ds-changelog-attr-exceeded-max-values-count");
        if (exceededMaxValues == null) {
            this.attributesThatExceededMaxValuesCount = Collections.emptyList();
        }
        else {
            final String[] values = exceededMaxValues.getValues();
            final ArrayList<ChangeLogEntryAttributeExceededMaxValuesCount> l = new ArrayList<ChangeLogEntryAttributeExceededMaxValuesCount>(values.length);
            for (final String value : values) {
                l.add(new ChangeLogEntryAttributeExceededMaxValuesCount(value));
            }
            this.attributesThatExceededMaxValuesCount = Collections.unmodifiableList((List<? extends ChangeLogEntryAttributeExceededMaxValuesCount>)l);
        }
        final Attribute virtualExceededMaxValues = entry.getAttribute("ds-changelog-virtual-attr-exceeded-max-values-count");
        if (virtualExceededMaxValues == null) {
            this.virtualAttributesThatExceededMaxValuesCount = Collections.emptyList();
        }
        else {
            final String[] values2 = virtualExceededMaxValues.getValues();
            final ArrayList<ChangeLogEntryAttributeExceededMaxValuesCount> i = new ArrayList<ChangeLogEntryAttributeExceededMaxValuesCount>(values2.length);
            for (final String value2 : values2) {
                i.add(new ChangeLogEntryAttributeExceededMaxValuesCount(value2));
            }
            this.virtualAttributesThatExceededMaxValuesCount = Collections.unmodifiableList((List<? extends ChangeLogEntryAttributeExceededMaxValuesCount>)i);
        }
        this.numExcludedUserAttributes = entry.getAttributeValueAsInteger("ds-changelog-num-excluded-user-attributes");
        this.numExcludedOperationalAttributes = entry.getAttributeValueAsInteger("ds-changelog-num-excluded-operational-attributes");
        final String[] excludedUserAttrNames = entry.getAttributeValues("ds-changelog-excluded-user-attribute");
        if (excludedUserAttrNames == null) {
            this.excludedUserAttributeNames = Collections.emptyList();
        }
        else {
            this.excludedUserAttributeNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(Arrays.asList(excludedUserAttrNames)));
        }
        final String[] excludedOpAttrNames = entry.getAttributeValues("ds-changelog-excluded-operational-attribute");
        if (excludedOpAttrNames == null) {
            this.excludedOperationalAttributeNames = Collections.emptyList();
        }
        else {
            this.excludedOperationalAttributeNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(Arrays.asList(excludedOpAttrNames)));
        }
        final String[] targetAttrNames = entry.getAttributeValues("ds-changelog-target-attribute");
        if (targetAttrNames == null) {
            this.targetAttributeNames = Collections.emptyList();
        }
        else {
            this.targetAttributeNames = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(Arrays.asList(targetAttrNames)));
        }
        final String[] notificationUUIDValues = entry.getAttributeValues("ds-notification-destination-entry-uuid");
        if (notificationUUIDValues == null) {
            this.notificationDestinationEntryUUIDs = Collections.emptyList();
        }
        else {
            this.notificationDestinationEntryUUIDs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(Arrays.asList(notificationUUIDValues)));
        }
        final String[] notificationPropertyValues = entry.getAttributeValues("ds-changelog-notification-properties");
        if (notificationPropertyValues == null) {
            this.notificationProperties = Collections.emptyList();
        }
        else {
            this.notificationProperties = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(Arrays.asList(notificationPropertyValues)));
        }
    }
    
    public String getTargetUniqueID() {
        return this.targetUniqueID;
    }
    
    public String getLocalCSN() {
        return this.localCSN;
    }
    
    public Date getChangeTime() {
        return this.changeTime;
    }
    
    public List<Attribute> getAddAttributes(final boolean includeVirtual) {
        if (includeVirtual && this.getChangeType() == ChangeType.ADD && !this.entryVirtualAttributes.isEmpty()) {
            final Entry e = new Entry(this.getTargetDN(), this.getAddAttributes());
            for (final Attribute a : this.entryVirtualAttributes) {
                e.addAttribute(a);
            }
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        return this.getAddAttributes();
    }
    
    public List<Attribute> getAddVirtualAttributes() {
        if (this.getChangeType() == ChangeType.ADD) {
            return this.entryVirtualAttributes;
        }
        return null;
    }
    
    public List<Attribute> getDeletedEntryAttributes(final boolean includeVirtual) {
        if (includeVirtual && this.getChangeType() == ChangeType.DELETE && !this.entryVirtualAttributes.isEmpty()) {
            final List<Attribute> realAttrs = this.getDeletedEntryAttributes();
            Entry e;
            if (realAttrs != null) {
                e = new Entry(this.getTargetDN(), realAttrs);
                for (final Attribute a : this.entryVirtualAttributes) {
                    e.addAttribute(a);
                }
            }
            else {
                e = new Entry(this.getTargetDN(), this.entryVirtualAttributes);
            }
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        return this.getDeletedEntryAttributes();
    }
    
    public List<Attribute> getDeletedEntryVirtualAttributes() {
        if (this.getChangeType() == ChangeType.DELETE) {
            return this.entryVirtualAttributes;
        }
        return null;
    }
    
    public List<Attribute> getUpdatedAttributesBeforeChange() {
        return this.updatedAttributesBeforeChange;
    }
    
    public List<Attribute> getUpdatedAttributesBeforeChange(final boolean includeVirtual) {
        if (includeVirtual && !this.updatedVirtualAttributesBeforeChange.isEmpty()) {
            final Entry e = new Entry(this.getTargetDN(), this.updatedAttributesBeforeChange);
            for (final Attribute a : this.updatedVirtualAttributesBeforeChange) {
                e.addAttribute(a);
            }
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        return this.updatedAttributesBeforeChange;
    }
    
    public List<Attribute> getUpdatedVirtualAttributesBeforeChange() {
        return this.updatedVirtualAttributesBeforeChange;
    }
    
    public List<Attribute> getUpdatedAttributesAfterChange() {
        return this.updatedAttributesAfterChange;
    }
    
    public List<Attribute> getUpdatedAttributesAfterChange(final boolean includeVirtual) {
        if (includeVirtual && !this.updatedVirtualAttributesAfterChange.isEmpty()) {
            final Entry e = new Entry(this.getTargetDN(), this.updatedAttributesAfterChange);
            for (final Attribute a : this.updatedVirtualAttributesAfterChange) {
                e.addAttribute(a);
            }
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        return this.updatedAttributesAfterChange;
    }
    
    public List<Attribute> getUpdatedVirtualAttributesAfterChange() {
        return this.updatedVirtualAttributesAfterChange;
    }
    
    public List<ChangeLogEntryAttributeExceededMaxValuesCount> getAttributesThatExceededMaxValuesCount() {
        return this.attributesThatExceededMaxValuesCount;
    }
    
    public List<ChangeLogEntryAttributeExceededMaxValuesCount> getVirtualAttributesThatExceededMaxValuesCount() {
        return this.virtualAttributesThatExceededMaxValuesCount;
    }
    
    public List<Attribute> getKeyEntryAttributes() {
        return this.keyEntryAttributes;
    }
    
    public List<Attribute> getKeyEntryAttributes(final boolean includeVirtual) {
        if (includeVirtual && !this.keyEntryVirtualAttributes.isEmpty()) {
            final Entry e = new Entry(this.getTargetDN(), this.keyEntryAttributes);
            for (final Attribute a : this.keyEntryVirtualAttributes) {
                e.addAttribute(a);
            }
            return Collections.unmodifiableList((List<? extends Attribute>)new ArrayList<Attribute>(e.getAttributes()));
        }
        return this.keyEntryAttributes;
    }
    
    public List<Attribute> getKeyEntryVirtualAttributes() {
        return this.keyEntryVirtualAttributes;
    }
    
    public int getNumExcludedUserAttributes() {
        if (this.numExcludedUserAttributes == null) {
            return -1;
        }
        return this.numExcludedUserAttributes;
    }
    
    public int getNumExcludedOperationalAttributes() {
        if (this.numExcludedOperationalAttributes == null) {
            return -1;
        }
        return this.numExcludedOperationalAttributes;
    }
    
    public List<String> getExcludedUserAttributeNames() {
        return this.excludedUserAttributeNames;
    }
    
    public List<String> getExcludedOperationalAttributeNames() {
        return this.excludedOperationalAttributeNames;
    }
    
    public Boolean getChangeToSoftDeletedEntry() {
        return this.changeToSoftDeletedEntry;
    }
    
    public String getSoftDeleteToDN() {
        return this.softDeleteToDN;
    }
    
    public String getUndeleteFromDN() {
        return this.undeleteFromDN;
    }
    
    public List<String> getTargetAttributeNames() {
        return this.targetAttributeNames;
    }
    
    public List<String> getNotificationDestinationEntryUUIDs() {
        return this.notificationDestinationEntryUUIDs;
    }
    
    public List<String> getNotificationProperties() {
        return this.notificationProperties;
    }
    
    public Attribute getAttributeBeforeChange(final String name) throws ChangeLogEntryAttributeExceededMaxValuesException {
        return this.getAttributeBeforeChange(name, false);
    }
    
    public Attribute getAttributeBeforeChange(final String name, final boolean includeVirtual) throws ChangeLogEntryAttributeExceededMaxValuesException {
        if (this.getChangeType() == ChangeType.ADD) {
            return null;
        }
        for (final Attribute a : this.getUpdatedAttributesBeforeChange(includeVirtual)) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        for (final ChangeLogEntryAttributeExceededMaxValuesCount a2 : this.attributesThatExceededMaxValuesCount) {
            if (a2.getAttributeName().equalsIgnoreCase(name)) {
                throw new ChangeLogEntryAttributeExceededMaxValuesException(UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_BEFORE_VALUE_COUNT.get(name, this.getTargetDN(), a2.getBeforeCount()), a2);
            }
        }
        if (includeVirtual) {
            for (final ChangeLogEntryAttributeExceededMaxValuesCount a2 : this.virtualAttributesThatExceededMaxValuesCount) {
                if (a2.getAttributeName().equalsIgnoreCase(name)) {
                    throw new ChangeLogEntryAttributeExceededMaxValuesException(UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VIRTUAL_BEFORE_VALUE_COUNT.get(name, this.getTargetDN(), a2.getBeforeCount()), a2);
                }
            }
        }
        for (final Attribute a : this.getKeyEntryAttributes(includeVirtual)) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        final List<Attribute> deletedAttrs = this.getDeletedEntryAttributes(includeVirtual);
        if (deletedAttrs != null) {
            for (final Attribute a3 : deletedAttrs) {
                if (a3.getName().equalsIgnoreCase(name)) {
                    return a3;
                }
            }
        }
        return null;
    }
    
    public Attribute getAttributeAfterChange(final String name) throws ChangeLogEntryAttributeExceededMaxValuesException {
        return this.getAttributeAfterChange(name, false);
    }
    
    public Attribute getAttributeAfterChange(final String name, final boolean includeVirtual) throws ChangeLogEntryAttributeExceededMaxValuesException {
        if (this.getChangeType() == ChangeType.DELETE) {
            return null;
        }
        for (final Attribute a : this.getUpdatedAttributesAfterChange(includeVirtual)) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        for (final Attribute a : this.getKeyEntryAttributes(includeVirtual)) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        for (final ChangeLogEntryAttributeExceededMaxValuesCount a2 : this.attributesThatExceededMaxValuesCount) {
            if (a2.getAttributeName().equalsIgnoreCase(name)) {
                throw new ChangeLogEntryAttributeExceededMaxValuesException(UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_AFTER_VALUE_COUNT.get(name, this.getTargetDN(), a2.getAfterCount()), a2);
            }
        }
        if (includeVirtual) {
            for (final ChangeLogEntryAttributeExceededMaxValuesCount a2 : this.virtualAttributesThatExceededMaxValuesCount) {
                if (a2.getAttributeName().equalsIgnoreCase(name)) {
                    throw new ChangeLogEntryAttributeExceededMaxValuesException(UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VIRTUAL_AFTER_VALUE_COUNT.get(name, this.getTargetDN(), a2.getAfterCount()), a2);
                }
            }
        }
        final List<Attribute> addAttrs = this.getAddAttributes(includeVirtual);
        if (addAttrs != null) {
            for (final Attribute a3 : addAttrs) {
                if (a3.getName().equalsIgnoreCase(name)) {
                    return a3;
                }
            }
        }
        final List<Modification> mods = this.getModifications();
        if (mods != null) {
            for (final Modification m : mods) {
                if (m.getAttributeName().equalsIgnoreCase(name)) {
                    final byte[][] values = m.getValueByteArrays();
                    if (m.getModificationType() == ModificationType.REPLACE && values.length > 0) {
                        return new Attribute(name, values);
                    }
                    continue;
                }
            }
        }
        return null;
    }
    
    public ReadOnlyEntry constructPartialEntryBeforeChange() {
        return this.constructPartialEntryBeforeChange(false);
    }
    
    public ReadOnlyEntry constructPartialEntryBeforeChange(final boolean includeVirtual) {
        if (this.getChangeType() == ChangeType.ADD) {
            return null;
        }
        final Entry e = new Entry(this.getTargetDN());
        final List<Attribute> deletedEntryAttrs = this.getDeletedEntryAttributes(includeVirtual);
        if (deletedEntryAttrs != null) {
            for (final Attribute a : deletedEntryAttrs) {
                e.addAttribute(a);
            }
        }
        for (final Attribute a : this.getUpdatedAttributesBeforeChange(includeVirtual)) {
            e.addAttribute(a);
        }
        for (final Attribute a : this.getKeyEntryAttributes(includeVirtual)) {
            boolean shouldExclude = e.hasAttribute(a.getName());
            for (final Attribute ba : this.getUpdatedAttributesAfterChange(includeVirtual)) {
                if (ba.getName().equalsIgnoreCase(a.getName())) {
                    shouldExclude = true;
                }
            }
            for (final ChangeLogEntryAttributeExceededMaxValuesCount ea : this.attributesThatExceededMaxValuesCount) {
                if (ea.getAttributeName().equalsIgnoreCase(a.getName())) {
                    shouldExclude = true;
                }
            }
            if (includeVirtual) {
                for (final ChangeLogEntryAttributeExceededMaxValuesCount ea : this.virtualAttributesThatExceededMaxValuesCount) {
                    if (ea.getAttributeName().equalsIgnoreCase(a.getName())) {
                        shouldExclude = true;
                    }
                }
            }
            if (!shouldExclude) {
                e.addAttribute(a);
            }
        }
        return new ReadOnlyEntry(e);
    }
    
    public ReadOnlyEntry constructPartialEntryAfterChange() {
        return this.constructPartialEntryAfterChange(false);
    }
    
    public ReadOnlyEntry constructPartialEntryAfterChange(final boolean includeVirtual) {
        Entry e = null;
        switch (this.getChangeType()) {
            case ADD:
            case MODIFY: {
                e = new Entry(this.getTargetDN());
                break;
            }
            case MODIFY_DN: {
                e = new Entry(this.getNewDN());
                break;
            }
            default: {
                return null;
            }
        }
        final List<Attribute> addAttrs = this.getAddAttributes(includeVirtual);
        if (addAttrs != null) {
            for (final Attribute a : addAttrs) {
                e.addAttribute(a);
            }
        }
        final List<Modification> mods = this.getModifications();
        if (mods != null) {
            for (final Modification m : mods) {
                final byte[][] values = m.getValueByteArrays();
                if (m.getModificationType() == ModificationType.REPLACE && values.length > 0) {
                    e.addAttribute(m.getAttributeName(), values);
                }
            }
        }
        for (final Attribute a2 : this.getUpdatedAttributesAfterChange(includeVirtual)) {
            e.addAttribute(a2);
        }
        for (final Attribute a2 : this.getKeyEntryAttributes(includeVirtual)) {
            e.addAttribute(a2);
        }
        return new ReadOnlyEntry(e);
    }
}
