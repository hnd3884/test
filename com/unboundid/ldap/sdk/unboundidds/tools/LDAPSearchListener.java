package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.transformations.EntryTransformation;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.SearchResultListener;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class LDAPSearchListener implements SearchResultListener
{
    private static final long serialVersionUID = -1334215024363357539L;
    private final LDAPSearchOutputHandler outputHandler;
    private final List<EntryTransformation> entryTransformations;
    
    LDAPSearchListener(final LDAPSearchOutputHandler outputHandler, final List<EntryTransformation> entryTransformations) {
        this.outputHandler = outputHandler;
        this.entryTransformations = entryTransformations;
    }
    
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        SearchResultEntry sre;
        if (this.entryTransformations == null) {
            sre = searchEntry;
        }
        else {
            Entry e = searchEntry;
            for (final EntryTransformation t : this.entryTransformations) {
                e = t.transformEntry(e);
            }
            sre = new SearchResultEntry(searchEntry.getMessageID(), e, searchEntry.getControls());
        }
        this.outputHandler.formatSearchResultEntry(sre);
    }
    
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        this.outputHandler.formatSearchResultReference(searchReference);
    }
}
