package io.netty.util;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;

public final class ReferenceCountUtil
{
    private static final InternalLogger logger;
    
    public static <T> T retain(final T msg) {
        if (msg instanceof ReferenceCounted) {
            return (T)((ReferenceCounted)msg).retain();
        }
        return msg;
    }
    
    public static <T> T retain(final T msg, final int increment) {
        ObjectUtil.checkPositive(increment, "increment");
        if (msg instanceof ReferenceCounted) {
            return (T)((ReferenceCounted)msg).retain(increment);
        }
        return msg;
    }
    
    public static <T> T touch(final T msg) {
        if (msg instanceof ReferenceCounted) {
            return (T)((ReferenceCounted)msg).touch();
        }
        return msg;
    }
    
    public static <T> T touch(final T msg, final Object hint) {
        if (msg instanceof ReferenceCounted) {
            return (T)((ReferenceCounted)msg).touch(hint);
        }
        return msg;
    }
    
    public static boolean release(final Object msg) {
        return msg instanceof ReferenceCounted && ((ReferenceCounted)msg).release();
    }
    
    public static boolean release(final Object msg, final int decrement) {
        ObjectUtil.checkPositive(decrement, "decrement");
        return msg instanceof ReferenceCounted && ((ReferenceCounted)msg).release(decrement);
    }
    
    public static void safeRelease(final Object msg) {
        try {
            release(msg);
        }
        catch (final Throwable t) {
            ReferenceCountUtil.logger.warn("Failed to release a message: {}", msg, t);
        }
    }
    
    public static void safeRelease(final Object msg, final int decrement) {
        try {
            ObjectUtil.checkPositive(decrement, "decrement");
            release(msg, decrement);
        }
        catch (final Throwable t) {
            if (ReferenceCountUtil.logger.isWarnEnabled()) {
                ReferenceCountUtil.logger.warn("Failed to release a message: {} (decrement: {})", msg, decrement, t);
            }
        }
    }
    
    @Deprecated
    public static <T> T releaseLater(final T msg) {
        return releaseLater(msg, 1);
    }
    
    @Deprecated
    public static <T> T releaseLater(final T msg, final int decrement) {
        ObjectUtil.checkPositive(decrement, "decrement");
        if (msg instanceof ReferenceCounted) {
            ThreadDeathWatcher.watch(Thread.currentThread(), new ReleasingTask((ReferenceCounted)msg, decrement));
        }
        return msg;
    }
    
    public static int refCnt(final Object msg) {
        return (msg instanceof ReferenceCounted) ? ((ReferenceCounted)msg).refCnt() : -1;
    }
    
    private ReferenceCountUtil() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ReferenceCountUtil.class);
        ResourceLeakDetector.addExclusions(ReferenceCountUtil.class, "touch");
    }
    
    private static final class ReleasingTask implements Runnable
    {
        private final ReferenceCounted obj;
        private final int decrement;
        
        ReleasingTask(final ReferenceCounted obj, final int decrement) {
            this.obj = obj;
            this.decrement = decrement;
        }
        
        @Override
        public void run() {
            try {
                if (!this.obj.release(this.decrement)) {
                    ReferenceCountUtil.logger.warn("Non-zero refCnt: {}", this);
                }
                else {
                    ReferenceCountUtil.logger.debug("Released: {}", this);
                }
            }
            catch (final Exception ex) {
                ReferenceCountUtil.logger.warn("Failed to release an object: {}", this.obj, ex);
            }
        }
        
        @Override
        public String toString() {
            return StringUtil.simpleClassName(this.obj) + ".release(" + this.decrement + ") refCnt: " + this.obj.refCnt();
        }
    }
}