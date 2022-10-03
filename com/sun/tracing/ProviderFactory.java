package com.sun.tracing;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Field;
import java.io.PrintStream;
import java.util.Set;
import sun.tracing.MultiplexProviderFactory;
import sun.tracing.NullProviderFactory;
import sun.tracing.PrintStreamProviderFactory;
import sun.tracing.dtrace.DTraceProviderFactory;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.HashSet;

public abstract class ProviderFactory
{
    protected ProviderFactory() {
    }
    
    public abstract <T extends Provider> T createProvider(final Class<T> p0);
    
    public static ProviderFactory getDefaultFactory() {
        final HashSet set = new HashSet();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("com.sun.tracing.dtrace"));
        if ((s == null || !s.equals("disable")) && DTraceProviderFactory.isSupported()) {
            set.add(new DTraceProviderFactory());
        }
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.tracing.stream"));
        if (s2 != null) {
            final String[] split = s2.split(",");
            for (int length = split.length, i = 0; i < length; ++i) {
                final PrintStream printStreamFromSpec = getPrintStreamFromSpec(split[i]);
                if (printStreamFromSpec != null) {
                    set.add(new PrintStreamProviderFactory(printStreamFromSpec));
                }
            }
        }
        if (set.size() == 0) {
            return new NullProviderFactory();
        }
        if (set.size() == 1) {
            return ((ProviderFactory[])set.toArray(new ProviderFactory[1]))[0];
        }
        return new MultiplexProviderFactory(set);
    }
    
    private static PrintStream getPrintStreamFromSpec(final String s) {
        try {
            final int lastIndex = s.lastIndexOf(46);
            return (PrintStream)AccessController.doPrivileged((PrivilegedExceptionAction<Field>)new PrivilegedExceptionAction<Field>() {
                final /* synthetic */ Class val$cls = Class.forName(s.substring(0, lastIndex));
                
                @Override
                public Field run() throws NoSuchFieldException {
                    return this.val$cls.getField(s.substring(lastIndex + 1));
                }
            }).get(null);
        }
        catch (final ClassNotFoundException ex) {
            throw new AssertionError((Object)ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new AssertionError((Object)ex2);
        }
        catch (final PrivilegedActionException ex3) {
            throw new AssertionError((Object)ex3);
        }
    }
}
