package com.unboundid.util;

import java.util.Collections;
import java.util.SortedMap;
import com.unboundid.ldap.sdk.DN;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPResult;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SubtreeDeleterResult implements Serializable
{
    private static final long serialVersionUID = -4801520019525316763L;
    private final boolean subtreeInaccessible;
    private final LDAPResult setSubtreeAccessibilityError;
    private final long entriesDeleted;
    private final SearchResult searchError;
    private final TreeMap<DN, LDAPResult> deleteErrors;
    
    SubtreeDeleterResult(final LDAPResult setSubtreeAccessibilityError, final boolean subtreeInaccessible, final SearchResult searchError, final long entriesDeleted, final TreeMap<DN, LDAPResult> deleteErrors) {
        this.setSubtreeAccessibilityError = setSubtreeAccessibilityError;
        this.subtreeInaccessible = subtreeInaccessible;
        this.searchError = searchError;
        this.entriesDeleted = entriesDeleted;
        this.deleteErrors = deleteErrors;
    }
    
    public boolean completelySuccessful() {
        return this.setSubtreeAccessibilityError == null && !this.subtreeInaccessible && this.searchError == null && this.deleteErrors.isEmpty();
    }
    
    public LDAPResult getSetSubtreeAccessibilityError() {
        return this.setSubtreeAccessibilityError;
    }
    
    public boolean subtreeInaccessible() {
        return this.subtreeInaccessible;
    }
    
    public SearchResult getSearchError() {
        return this.searchError;
    }
    
    public long getEntriesDeleted() {
        return this.entriesDeleted;
    }
    
    public SortedMap<DN, LDAPResult> getDeleteErrors() {
        return Collections.unmodifiableSortedMap((SortedMap<DN, ? extends LDAPResult>)this.deleteErrors);
    }
    
    public SortedMap<DN, LDAPResult> getDeleteErrorsDescendingMap() {
        return Collections.unmodifiableSortedMap((SortedMap<DN, ? extends LDAPResult>)this.deleteErrors.descendingMap());
    }
    
    TreeMap<DN, LDAPResult> getDeleteErrorsTreeMap() {
        return this.deleteErrors;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SubtreeDeleterResult=(completelySuccessful=");
        buffer.append(this.completelySuccessful());
        if (this.setSubtreeAccessibilityError != null) {
            buffer.append(", setSubtreeAccessibilityError=");
            this.setSubtreeAccessibilityError.toString(buffer);
        }
        if (this.subtreeInaccessible) {
            buffer.append(", subtreeInaccessible=true");
        }
        if (this.searchError != null) {
            buffer.append(", searchError=");
            this.searchError.toString(buffer);
        }
        buffer.append(", entriesDeleted=");
        buffer.append(this.entriesDeleted);
        if (!this.deleteErrors.isEmpty()) {
            buffer.append(", deleteErrors=");
            buffer.append(this.deleteErrors);
        }
    }
}
