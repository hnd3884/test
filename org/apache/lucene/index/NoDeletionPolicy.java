package org.apache.lucene.index;

import java.util.List;

public final class NoDeletionPolicy extends IndexDeletionPolicy
{
    public static final IndexDeletionPolicy INSTANCE;
    
    private NoDeletionPolicy() {
    }
    
    @Override
    public void onCommit(final List<? extends IndexCommit> commits) {
    }
    
    @Override
    public void onInit(final List<? extends IndexCommit> commits) {
    }
    
    public IndexDeletionPolicy clone() {
        return this;
    }
    
    static {
        INSTANCE = new NoDeletionPolicy();
    }
}
