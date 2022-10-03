package io.netty.channel.epoll;

import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.SystemPropertyUtil;

public final class Epoll
{
    private static final Throwable UNAVAILABILITY_CAUSE;
    
    public static boolean isAvailable() {
        return Epoll.UNAVAILABILITY_CAUSE == null;
    }
    
    public static void ensureAvailability() {
        if (Epoll.UNAVAILABILITY_CAUSE != null) {
            throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(Epoll.UNAVAILABILITY_CAUSE);
        }
    }
    
    public static Throwable unavailabilityCause() {
        return Epoll.UNAVAILABILITY_CAUSE;
    }
    
    private Epoll() {
    }
    
    static {
        Throwable cause = null;
        if (SystemPropertyUtil.getBoolean("io.netty.transport.noNative", false)) {
            cause = new UnsupportedOperationException("Native transport was explicit disabled with -Dio.netty.transport.noNative=true");
        }
        else {
            FileDescriptor epollFd = null;
            FileDescriptor eventFd = null;
            try {
                epollFd = Native.newEpollCreate();
                eventFd = Native.newEventFd();
            }
            catch (final Throwable t) {
                cause = t;
            }
            finally {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (final Exception ex) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (final Exception ex2) {}
                }
            }
        }
        UNAVAILABILITY_CAUSE = cause;
    }
}
