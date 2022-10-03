package jdk.jfr.internal;

import java.security.PrivilegedActionException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import jdk.jfr.RecordingState;

final class ShutdownHook implements Runnable
{
    private final PlatformRecorder recorder;
    Object tlabDummyObject;
    
    ShutdownHook(final PlatformRecorder recorder) {
        this.recorder = recorder;
    }
    
    @Override
    public void run() {
        this.tlabDummyObject = new Object();
        for (final PlatformRecording platformRecording : this.recorder.getRecordings()) {
            if (platformRecording.getDumpOnExit() && platformRecording.getState() == RecordingState.RUNNING) {
                this.dump(platformRecording);
            }
        }
        this.recorder.destroy();
    }
    
    private void dump(final PlatformRecording platformRecording) {
        try {
            WriteableUserPath destination = platformRecording.getDestination();
            if (destination == null) {
                destination = this.makeDumpOnExitPath(platformRecording);
                platformRecording.setDestination(destination);
            }
            if (destination != null) {
                platformRecording.stop("Dump on exit");
            }
        }
        catch (final Exception ex) {
            Logger.log(LogTag.JFR, LogLevel.DEBUG, () -> "Could not dump recording " + platformRecording2.getName() + " on exit.");
        }
    }
    
    private WriteableUserPath makeDumpOnExitPath(final PlatformRecording platformRecording) {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<WriteableUserPath>)new PrivilegedExceptionAction<WriteableUserPath>() {
                final /* synthetic */ String val$name = Utils.makeFilename(platformRecording.getRecording());
                
                @Override
                public WriteableUserPath run() throws Exception {
                    return new WriteableUserPath(platformRecording.getDumpOnExitDirectory().toPath().resolve(this.val$name));
                }
            }, platformRecording.getNoDestinationDumpOnExitAccessControlContext());
        }
        catch (final PrivilegedActionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof SecurityException) {
                Logger.log(LogTag.JFR, LogLevel.WARN, "Not allowed to create dump path for recording " + platformRecording.getId() + " on exit.");
            }
            if (cause instanceof IOException) {
                Logger.log(LogTag.JFR, LogLevel.WARN, "Could not dump " + platformRecording.getId() + " on exit.");
            }
            return null;
        }
    }
    
    static final class ExceptionHandler implements Thread.UncaughtExceptionHandler
    {
        @Override
        public void uncaughtException(final Thread thread, final Throwable t) {
            JVM.getJVM().uncaughtException(thread, t);
        }
    }
}
