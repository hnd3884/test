package com.unboundid.ldap.sdk;

import java.util.Collections;
import com.unboundid.util.InternalUseOnly;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class BasicAsyncSearchResultListener implements AsyncSearchResultListener
{
    private static final long serialVersionUID = 2289128360755244209L;
    private final List<SearchResultEntry> entryList;
    private final List<SearchResultReference> referenceList;
    private volatile SearchResult searchResult;
    
    public BasicAsyncSearchResultListener() {
        this.searchResult = null;
        this.entryList = new ArrayList<SearchResultEntry>(5);
        this.referenceList = new ArrayList<SearchResultReference>(5);
    }
    
    @InternalUseOnly
    @Override
    public void searchEntryReturned(final SearchResultEntry searchEntry) {
        this.entryList.add(searchEntry);
    }
    
    @InternalUseOnly
    @Override
    public void searchReferenceReturned(final SearchResultReference searchReference) {
        this.referenceList.add(searchReference);
    }
    
    @InternalUseOnly
    @Override
    public void searchResultReceived(final AsyncRequestID requestID, final SearchResult searchResult) {
        this.searchResult = searchResult;
    }
    
    public SearchResult getSearchResult() {
        return this.searchResult;
    }
    
    public List<SearchResultEntry> getSearchEntries() {
        return Collections.unmodifiableList((List<? extends SearchResultEntry>)this.entryList);
    }
    
    public List<SearchResultReference> getSearchReferences() {
        return Collections.unmodifiableList((List<? extends SearchResultReference>)this.referenceList);
    }
}
