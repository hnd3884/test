package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import java.util.Collections;
import java.util.Set;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class UniquenessRequestControlProperties implements Serializable
{
    private static final long serialVersionUID = 4330352906527176309L;
    private boolean preventConflictsWithSoftDeletedEntries;
    private Filter filter;
    private Set<String> attributeTypes;
    private String baseDN;
    private UniquenessMultipleAttributeBehavior multipleAttributeBehavior;
    private UniquenessValidationLevel postCommitValidationLevel;
    private UniquenessValidationLevel preCommitValidationLevel;
    
    private UniquenessRequestControlProperties() {
        this.preventConflictsWithSoftDeletedEntries = false;
        this.filter = null;
        this.attributeTypes = Collections.emptySet();
        this.baseDN = null;
        this.multipleAttributeBehavior = UniquenessMultipleAttributeBehavior.UNIQUE_WITHIN_EACH_ATTRIBUTE;
        this.postCommitValidationLevel = UniquenessValidationLevel.ALL_SUBTREE_VIEWS;
        this.preCommitValidationLevel = UniquenessValidationLevel.ALL_SUBTREE_VIEWS;
    }
    
    public UniquenessRequestControlProperties(final String... attributeTypes) {
        this();
        Validator.ensureTrue(attributeTypes != null && attributeTypes.length > 0, "The set of attribute types must not be null or empty.");
        this.attributeTypes = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(StaticUtils.toList(attributeTypes)));
    }
    
    public UniquenessRequestControlProperties(final Collection<String> attributeTypes) {
        this();
        Validator.ensureTrue(attributeTypes != null && !attributeTypes.isEmpty(), "The set of attribute types must not be null or empty.");
        this.attributeTypes = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(attributeTypes));
    }
    
    public UniquenessRequestControlProperties(final Filter filter) {
        this();
        Validator.ensureNotNull(filter);
        this.filter = filter;
    }
    
    public Set<String> getAttributeTypes() {
        return this.attributeTypes;
    }
    
    public void setAttributeTypes(final String... attributeTypes) {
        if (attributeTypes == null) {
            this.attributeTypes = Collections.emptySet();
        }
        else {
            this.attributeTypes = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(StaticUtils.toList(attributeTypes)));
        }
    }
    
    public void setAttributeTypes(final Collection<String> attributeTypes) {
        if (attributeTypes == null) {
            this.attributeTypes = Collections.emptySet();
        }
        else {
            this.attributeTypes = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(attributeTypes));
        }
    }
    
    public UniquenessMultipleAttributeBehavior getMultipleAttributeBehavior() {
        return this.multipleAttributeBehavior;
    }
    
    public void setMultipleAttributeBehavior(final UniquenessMultipleAttributeBehavior multipleAttributeBehavior) {
        Validator.ensureNotNull(multipleAttributeBehavior);
        this.multipleAttributeBehavior = multipleAttributeBehavior;
    }
    
    public String getBaseDN() {
        return this.baseDN;
    }
    
    public void setBaseDN(final String baseDN) {
        this.baseDN = baseDN;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }
    
    public boolean preventConflictsWithSoftDeletedEntries() {
        return this.preventConflictsWithSoftDeletedEntries;
    }
    
    public void setPreventConflictsWithSoftDeletedEntries(final boolean preventConflictsWithSoftDeletedEntries) {
        this.preventConflictsWithSoftDeletedEntries = preventConflictsWithSoftDeletedEntries;
    }
    
    public UniquenessValidationLevel getPreCommitValidationLevel() {
        return this.preCommitValidationLevel;
    }
    
    public void setPreCommitValidationLevel(final UniquenessValidationLevel preCommitValidationLevel) {
        Validator.ensureNotNull(preCommitValidationLevel);
        this.preCommitValidationLevel = preCommitValidationLevel;
    }
    
    public UniquenessValidationLevel getPostCommitValidationLevel() {
        return this.postCommitValidationLevel;
    }
    
    public void setPostCommitValidationLevel(final UniquenessValidationLevel postCommitValidationLevel) {
        Validator.ensureNotNull(postCommitValidationLevel);
        this.postCommitValidationLevel = postCommitValidationLevel;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("UniquenessRequestControlProperties(attributeTypes={");
        final Iterator<String> attributeTypesIterator = this.attributeTypes.iterator();
        while (attributeTypesIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(attributeTypesIterator.next());
            buffer.append('\'');
            if (attributeTypesIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, multipleAttributeBehavior=");
        buffer.append(this.multipleAttributeBehavior);
        if (this.baseDN != null) {
            buffer.append(", baseDN='");
            buffer.append(this.baseDN);
            buffer.append('\'');
        }
        if (this.filter != null) {
            buffer.append(", filter='");
            buffer.append(this.filter);
            buffer.append('\'');
        }
        buffer.append(", preventConflictsWithSoftDeletedEntries=");
        buffer.append(this.preventConflictsWithSoftDeletedEntries);
        buffer.append(", preCommitValidationLevel=");
        buffer.append(this.preCommitValidationLevel);
        buffer.append(", postCommitValidationLevel=");
        buffer.append(this.postCommitValidationLevel);
        buffer.append(')');
    }
}
