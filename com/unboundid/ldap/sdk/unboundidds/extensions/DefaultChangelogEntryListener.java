package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.List;
import com.unboundid.ldap.sdk.IntermediateResponse;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class DefaultChangelogEntryListener implements ChangelogEntryListener, Serializable
{
    private static final long serialVersionUID = 4372347455698298062L;
    private final ArrayList<ChangelogEntryIntermediateResponse> entryList;
    
    DefaultChangelogEntryListener(final GetChangelogBatchExtendedRequest r) {
        this.entryList = new ArrayList<ChangelogEntryIntermediateResponse>(r.getMaxChanges());
    }
    
    @Override
    public void handleChangelogEntry(final ChangelogEntryIntermediateResponse ir) {
        this.entryList.add(ir);
    }
    
    @Override
    public void handleMissingChangelogEntries(final MissingChangelogEntriesIntermediateResponse ir) {
    }
    
    @Override
    public void handleOtherIntermediateResponse(final IntermediateResponse ir) {
    }
    
    List<ChangelogEntryIntermediateResponse> getEntryList() {
        return this.entryList;
    }
}
