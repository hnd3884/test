package org.apache.lucene.index;

import java.util.List;

public final class KeepOnlyLastCommitDeletionPolicy extends IndexDeletionPolicy
{
    @Override
    public void onInit(final List<? extends IndexCommit> commits) {
        this.onCommit(commits);
    }
    
    @Override
    public void onCommit(final List<? extends IndexCommit> commits) {
        for (int size = commits.size(), i = 0; i < size - 1; ++i) {
            ((IndexCommit)commits.get(i)).delete();
        }
    }
}
