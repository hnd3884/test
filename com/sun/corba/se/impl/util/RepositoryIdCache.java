package com.sun.corba.se.impl.util;

import java.util.Hashtable;

public class RepositoryIdCache extends Hashtable
{
    private RepositoryIdPool pool;
    
    public RepositoryIdCache() {
        (this.pool = new RepositoryIdPool()).setCaches(this);
    }
    
    public final synchronized RepositoryId getId(final String s) {
        final RepositoryId repositoryId = super.get(s);
        if (repositoryId != null) {
            return repositoryId;
        }
        final RepositoryId repositoryId2 = new RepositoryId(s);
        this.put(s, repositoryId2);
        return repositoryId2;
    }
}
