package com.azul.crs.jfr.access;

import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicReference;
import jdk.jfr.Recording;
import java.time.Instant;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.internal.FlightRecorderAssociate;
import jdk.jfr.FlightRecorder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class FlightRecorderAccess
{
    private static final AccessException initException;
    private static final Method chunkUseMethod;
    private static final Method chunkReleaseMethod;
    private static final Method recorderSetAssociateMethod;
    
    private FlightRecorderAccess() {
    }
    
    public void useRepositoryChunk(final Object chunk) throws AccessException {
        try {
            FlightRecorderAccess.chunkUseMethod.invoke(chunk, new Object[0]);
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new AccessException((Throwable)ex);
        }
    }
    
    public void releaseRepositoryChunk(final Object chunk) throws AccessException {
        try {
            FlightRecorderAccess.chunkReleaseMethod.invoke(chunk, new Object[0]);
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new AccessException((Throwable)ex);
        }
    }
    
    public static FlightRecorderAccess getAccess(final FlightRecorder fr, final FlightRecorderCallbacks callbacks) throws AccessException {
        if (FlightRecorderAccess.initException != null) {
            throw FlightRecorderAccess.initException;
        }
        try {
            FlightRecorderAccess.recorderSetAssociateMethod.invoke(fr, new FlightRecorderAssociate() {
                public void nextChunk(final Object chunk, final SecuritySupport.SafePath path, final Instant startTime, final Instant endTime, final long size, final Recording ignoreMe) {
                    callbacks.nextChunk(chunk, path.toPath(), startTime, endTime, size, ignoreMe);
                }
                
                public void finishJoin() {
                    callbacks.finishJoin();
                }
            });
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new AccessException((Throwable)ex);
        }
        return new FlightRecorderAccess();
    }
    
    static {
        final AtomicReference<AccessException> ex = new AtomicReference<AccessException>();
        final AtomicReference<Method> chunkUeMethodRef = new AtomicReference<Method>();
        final AtomicReference<Method> chunkReleaseMethodRef = new AtomicReference<Method>();
        final AtomicReference<Method> recorderSetAssociateMethodRef = new AtomicReference<Method>();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            final /* synthetic */ AtomicReference val$recorderSetAssociateMethodRef = recorderSetAssociateMethodRef;
            final /* synthetic */ AtomicReference val$chunkUeMethodRef = chunkUeMethodRef;
            final /* synthetic */ AtomicReference val$chunkReleaseMethodRef = chunkReleaseMethodRef;
            final /* synthetic */ AtomicReference val$ex = ex;
            
            @Override
            public Void run() {
                try {
                    Method m = FlightRecorder.class.getDeclaredMethod("setAssociate", FlightRecorderAssociate.class);
                    m.setAccessible(true);
                    this.val$recorderSetAssociateMethodRef.set(m);
                    final Class<?> chunkClass = Class.forName("jdk.jfr.internal.RepositoryChunk");
                    m = chunkClass.getDeclaredMethod("use", (Class<?>[])new Class[0]);
                    m.setAccessible(true);
                    this.val$chunkUeMethodRef.set(m);
                    m = chunkClass.getDeclaredMethod("release", (Class<?>[])new Class[0]);
                    m.setAccessible(true);
                    this.val$chunkReleaseMethodRef.set(m);
                }
                catch (final ClassNotFoundException | NoSuchMethodException e) {
                    this.val$ex.set(new AccessException((Throwable)e));
                }
                return null;
            }
        });
        initException = ex.get();
        chunkUseMethod = chunkUeMethodRef.get();
        chunkReleaseMethod = chunkReleaseMethodRef.get();
        recorderSetAssociateMethod = recorderSetAssociateMethodRef.get();
    }
    
    public static final class AccessException extends Exception
    {
        private static final long serialVersionUID = -3710493080622598156L;
        
        private AccessException(final Throwable cause) {
            super(cause);
        }
    }
    
    public interface FlightRecorderCallbacks
    {
        void nextChunk(final Object p0, final Path p1, final Instant p2, final Instant p3, final long p4, final Recording p5);
        
        void finishJoin();
    }
}
