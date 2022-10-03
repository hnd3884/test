package io.netty.channel.kqueue;

import io.netty.channel.unix.Unix;
import io.netty.util.internal.ClassInitializerUtil;
import java.nio.channels.FileChannel;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.PeerCredentials;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.logging.InternalLogger;

final class Native
{
    private static final InternalLogger logger;
    static final short EV_ADD;
    static final short EV_ENABLE;
    static final short EV_DISABLE;
    static final short EV_DELETE;
    static final short EV_CLEAR;
    static final short EV_ERROR;
    static final short EV_EOF;
    static final int NOTE_READCLOSED;
    static final int NOTE_CONNRESET;
    static final int NOTE_DISCONNECTED;
    static final int NOTE_RDHUP;
    static final short EV_ADD_CLEAR_ENABLE;
    static final short EV_DELETE_DISABLE;
    static final short EVFILT_READ;
    static final short EVFILT_WRITE;
    static final short EVFILT_USER;
    static final short EVFILT_SOCK;
    private static final int CONNECT_RESUME_ON_READ_WRITE;
    private static final int CONNECT_DATA_IDEMPOTENT;
    static final int CONNECT_TCP_FASTOPEN;
    
    private static native int registerUnix();
    
    static FileDescriptor newKQueue() {
        return new FileDescriptor(kqueueCreate());
    }
    
    static int keventWait(final int kqueueFd, final KQueueEventArray changeList, final KQueueEventArray eventList, final int tvSec, final int tvNsec) throws IOException {
        final int ready = keventWait(kqueueFd, changeList.memoryAddress(), changeList.size(), eventList.memoryAddress(), eventList.capacity(), tvSec, tvNsec);
        if (ready < 0) {
            throw Errors.newIOException("kevent", ready);
        }
        return ready;
    }
    
    private static native int kqueueCreate();
    
    private static native int keventWait(final int p0, final long p1, final int p2, final long p3, final int p4, final int p5, final int p6);
    
    static native int keventTriggerUserEvent(final int p0, final int p1);
    
    static native int keventAddUserEvent(final int p0, final int p1);
    
    static native int sizeofKEvent();
    
    static native int offsetofKEventIdent();
    
    static native int offsetofKEventFlags();
    
    static native int offsetofKEventFFlags();
    
    static native int offsetofKEventFilter();
    
    static native int offsetofKeventData();
    
    private static void loadNativeLibrary() {
        final String name = PlatformDependent.normalizedOs();
        if (!"osx".equals(name) && !name.contains("bsd")) {
            throw new IllegalStateException("Only supported on OSX/BSD");
        }
        final String staticLibName = "netty_transport_native_kqueue";
        final String sharedLibName = staticLibName + '_' + PlatformDependent.normalizedArch();
        final ClassLoader cl = PlatformDependent.getClassLoader(Native.class);
        try {
            NativeLibraryLoader.load(sharedLibName, cl);
        }
        catch (final UnsatisfiedLinkError e1) {
            try {
                NativeLibraryLoader.load(staticLibName, cl);
                Native.logger.debug("Failed to load {}", sharedLibName, e1);
            }
            catch (final UnsatisfiedLinkError e2) {
                ThrowableUtil.addSuppressed(e1, e2);
                throw e1;
            }
        }
    }
    
    private Native() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Native.class);
        ClassInitializerUtil.tryLoadClasses(Native.class, PeerCredentials.class, DefaultFileRegion.class, FileChannel.class, java.io.FileDescriptor.class);
        try {
            sizeofKEvent();
        }
        catch (final UnsatisfiedLinkError ignore) {
            loadNativeLibrary();
        }
        Unix.registerInternal(new Runnable() {
            @Override
            public void run() {
                registerUnix();
            }
        });
        EV_ADD = KQueueStaticallyReferencedJniMethods.evAdd();
        EV_ENABLE = KQueueStaticallyReferencedJniMethods.evEnable();
        EV_DISABLE = KQueueStaticallyReferencedJniMethods.evDisable();
        EV_DELETE = KQueueStaticallyReferencedJniMethods.evDelete();
        EV_CLEAR = KQueueStaticallyReferencedJniMethods.evClear();
        EV_ERROR = KQueueStaticallyReferencedJniMethods.evError();
        EV_EOF = KQueueStaticallyReferencedJniMethods.evEOF();
        NOTE_READCLOSED = KQueueStaticallyReferencedJniMethods.noteReadClosed();
        NOTE_CONNRESET = KQueueStaticallyReferencedJniMethods.noteConnReset();
        NOTE_DISCONNECTED = KQueueStaticallyReferencedJniMethods.noteDisconnected();
        NOTE_RDHUP = (Native.NOTE_READCLOSED | Native.NOTE_CONNRESET | Native.NOTE_DISCONNECTED);
        EV_ADD_CLEAR_ENABLE = (short)(Native.EV_ADD | Native.EV_CLEAR | Native.EV_ENABLE);
        EV_DELETE_DISABLE = (short)(Native.EV_DELETE | Native.EV_DISABLE);
        EVFILT_READ = KQueueStaticallyReferencedJniMethods.evfiltRead();
        EVFILT_WRITE = KQueueStaticallyReferencedJniMethods.evfiltWrite();
        EVFILT_USER = KQueueStaticallyReferencedJniMethods.evfiltUser();
        EVFILT_SOCK = KQueueStaticallyReferencedJniMethods.evfiltSock();
        CONNECT_RESUME_ON_READ_WRITE = KQueueStaticallyReferencedJniMethods.connectResumeOnReadWrite();
        CONNECT_DATA_IDEMPOTENT = KQueueStaticallyReferencedJniMethods.connectDataIdempotent();
        CONNECT_TCP_FASTOPEN = (Native.CONNECT_RESUME_ON_READ_WRITE | Native.CONNECT_DATA_IDEMPOTENT);
    }
}
