package com.me.idps.op;

import com.me.idps.core.sync.synch.DirSingletonQueue;

class OPpkSynchAllocator
{
    private OPpkSynchAllocator() {
    }
    
    static OPpkSynchAllocator getInstance() {
        return Holder.INSTANCE;
    }
    
    public long[] allocatePKs(final DirSingletonQueue dirSingletonQueue, final int numOfPKsrequired) throws Exception {
        synchronized (Holder.INSTANCE) {
            return dirSingletonQueue.allocatePK(numOfPKsrequired, false);
        }
    }
    
    private static class Holder
    {
        private static final OPpkSynchAllocator INSTANCE;
        
        static {
            INSTANCE = new OPpkSynchAllocator(null);
        }
    }
}
