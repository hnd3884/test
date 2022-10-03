package sun.net;

import java.util.Collections;
import jdk.net.ExtendedSocketOptions;
import java.util.HashSet;
import java.net.SocketOption;
import java.util.Set;

public class ExtendedOptionsHelper
{
    private static final boolean keepAliveOptSupported;
    private static final Set<SocketOption<?>> extendedOptions;
    
    private static Set<SocketOption<?>> options() {
        final HashSet set = new HashSet();
        if (ExtendedOptionsHelper.keepAliveOptSupported) {
            set.add(ExtendedSocketOptions.TCP_KEEPCOUNT);
            set.add(ExtendedSocketOptions.TCP_KEEPIDLE);
            set.add(ExtendedSocketOptions.TCP_KEEPINTERVAL);
        }
        return (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set);
    }
    
    public static Set<SocketOption<?>> keepAliveOptions() {
        return ExtendedOptionsHelper.extendedOptions;
    }
    
    static {
        keepAliveOptSupported = ExtendedOptionsImpl.keepAliveOptionsSupported();
        extendedOptions = options();
    }
}
