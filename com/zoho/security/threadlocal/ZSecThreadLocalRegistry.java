package com.zoho.security.threadlocal;

import java.util.function.Supplier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ZSecThreadLocalRegistry
{
    private static ThreadLocal<Set<ThreadLocal<?>>> threadLocalSet;
    
    public static void registerThreadLocal(final ThreadLocal<?> t) {
        ZSecThreadLocalRegistry.threadLocalSet.get().add(t);
    }
    
    public static void resetThreadLocals() {
        final Set<ThreadLocal<?>> threadLocals = ZSecThreadLocalRegistry.threadLocalSet.get();
        for (final ThreadLocal<?> threadLocal : threadLocals) {
            threadLocal.remove();
        }
        threadLocals.clear();
        ZSecThreadLocalRegistry.threadLocalSet.remove();
    }
    
    static {
        ZSecThreadLocalRegistry.threadLocalSet = ThreadLocal.withInitial((Supplier<? extends Set<ThreadLocal<?>>>)HashSet::new);
    }
}
