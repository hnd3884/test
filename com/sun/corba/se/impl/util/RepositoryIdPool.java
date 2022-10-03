package com.sun.corba.se.impl.util;

import java.util.EmptyStackException;
import java.util.Stack;

class RepositoryIdPool extends Stack
{
    private static int MAX_CACHE_SIZE;
    private RepositoryIdCache cache;
    
    public final synchronized RepositoryId popId() {
        try {
            return super.pop();
        }
        catch (final EmptyStackException ex) {
            this.increasePool(5);
            return super.pop();
        }
    }
    
    final void increasePool(final int n) {
        for (int i = n; i > 0; --i) {
            this.push(new RepositoryId());
        }
    }
    
    final void setCaches(final RepositoryIdCache cache) {
        this.cache = cache;
    }
    
    static {
        RepositoryIdPool.MAX_CACHE_SIZE = 4;
    }
}
