package io.netty.util;

import java.net.InetAddress;
import java.net.Inet6Address;
import com.oracle.svm.core.annotate.InjectAccessors;
import com.oracle.svm.core.annotate.Alias;
import java.net.Inet4Address;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(NetUtil.class)
final class NetUtilSubstitutions
{
    @Alias
    @InjectAccessors(NetUtilLocalhost4Accessor.class)
    public static Inet4Address LOCALHOST4;
    @Alias
    @InjectAccessors(NetUtilLocalhost6Accessor.class)
    public static Inet6Address LOCALHOST6;
    @Alias
    @InjectAccessors(NetUtilLocalhostAccessor.class)
    public static InetAddress LOCALHOST;
    
    private NetUtilSubstitutions() {
    }
    
    private static final class NetUtilLocalhost4Accessor
    {
        static Inet4Address get() {
            return NetUtilLocalhost4LazyHolder.LOCALHOST4;
        }
        
        static void set(final Inet4Address ignored) {
        }
    }
    
    private static final class NetUtilLocalhost4LazyHolder
    {
        private static final Inet4Address LOCALHOST4;
        
        static {
            LOCALHOST4 = NetUtilInitializations.createLocalhost4();
        }
    }
    
    private static final class NetUtilLocalhost6Accessor
    {
        static Inet6Address get() {
            return NetUtilLocalhost6LazyHolder.LOCALHOST6;
        }
        
        static void set(final Inet6Address ignored) {
        }
    }
    
    private static final class NetUtilLocalhost6LazyHolder
    {
        private static final Inet6Address LOCALHOST6;
        
        static {
            LOCALHOST6 = NetUtilInitializations.createLocalhost6();
        }
    }
    
    private static final class NetUtilLocalhostAccessor
    {
        static InetAddress get() {
            return NetUtilLocalhostLazyHolder.LOCALHOST;
        }
        
        static void set(final InetAddress ignored) {
        }
    }
    
    private static final class NetUtilLocalhostLazyHolder
    {
        private static final InetAddress LOCALHOST;
        
        static {
            LOCALHOST = NetUtilInitializations.determineLoopback(NetUtilLocalhost4LazyHolder.LOCALHOST4, NetUtilLocalhost6LazyHolder.LOCALHOST6).address();
        }
    }
}
