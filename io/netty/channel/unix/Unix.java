package io.netty.channel.unix;

import io.netty.util.internal.ClassInitializerUtil;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Unix
{
    private static final AtomicBoolean registered;
    
    public static void registerInternal(final Runnable registerTask) {
        if (Unix.registered.compareAndSet(false, true)) {
            registerTask.run();
            Socket.initialize();
        }
    }
    
    @Deprecated
    public static boolean isAvailable() {
        return false;
    }
    
    @Deprecated
    public static void ensureAvailability() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static Throwable unavailabilityCause() {
        return new UnsupportedOperationException();
    }
    
    private Unix() {
    }
    
    static {
        registered = new AtomicBoolean();
        ClassInitializerUtil.tryLoadClasses(Unix.class, OutOfMemoryError.class, RuntimeException.class, ClosedChannelException.class, IOException.class, PortUnreachableException.class, DatagramSocketAddress.class, DomainDatagramSocketAddress.class, InetSocketAddress.class);
    }
}
