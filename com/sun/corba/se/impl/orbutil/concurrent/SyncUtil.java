package com.sun.corba.se.impl.orbutil.concurrent;

public class SyncUtil
{
    private SyncUtil() {
    }
    
    public static void acquire(final Sync sync) {
        int i = 0;
        while (i == 0) {
            try {
                sync.acquire();
                i = 1;
            }
            catch (final InterruptedException ex) {
                i = 0;
            }
        }
    }
}
