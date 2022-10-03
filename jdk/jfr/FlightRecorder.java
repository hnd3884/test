package jdk.jfr;

import jdk.jfr.internal.FlightRecorderAssociate;
import jdk.jfr.internal.JVM;
import jdk.jfr.internal.RequestEngine;
import java.security.AccessController;
import jdk.jfr.internal.Repository;
import jdk.jfr.internal.Options;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import jdk.jfr.internal.MetadataRepository;
import jdk.jfr.internal.Utils;
import jdk.jfr.internal.JVMSupport;
import java.util.Objects;
import java.util.Iterator;
import java.util.Collections;
import jdk.jfr.internal.PlatformRecording;
import java.util.ArrayList;
import java.util.List;
import jdk.jfr.internal.PlatformRecorder;
import jdk.Exported;

@Exported
public final class FlightRecorder
{
    private static volatile FlightRecorder platformRecorder;
    private static volatile boolean initialized;
    private final PlatformRecorder internal;
    
    private FlightRecorder(final PlatformRecorder internal) {
        this.internal = internal;
    }
    
    public List<Recording> getRecordings() {
        final ArrayList list = new ArrayList();
        final Iterator<PlatformRecording> iterator = this.internal.getRecordings().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getRecording());
        }
        return (List<Recording>)Collections.unmodifiableList((List<?>)list);
    }
    
    public Recording takeSnapshot() {
        final Recording recording = new Recording();
        recording.setName("Snapshot");
        this.internal.fillWithRecordedData(recording.getInternal(), null);
        return recording;
    }
    
    public static void register(final Class<? extends Event> clazz) {
        Objects.requireNonNull(clazz);
        if (JVMSupport.isNotAvailable()) {
            return;
        }
        Utils.ensureValidEventSubclass(clazz);
        MetadataRepository.getInstance().register(clazz);
    }
    
    public static void unregister(final Class<? extends Event> clazz) {
        Objects.requireNonNull(clazz);
        if (JVMSupport.isNotAvailable()) {
            return;
        }
        Utils.ensureValidEventSubclass(clazz);
        MetadataRepository.getInstance().unregister(clazz);
    }
    
    public static FlightRecorder getFlightRecorder() throws IllegalStateException, SecurityException {
        synchronized (PlatformRecorder.class) {
            Utils.checkAccessFlightRecorder();
            JVMSupport.ensureWithIllegalStateException();
            if (FlightRecorder.platformRecorder == null) {
                try {
                    FlightRecorder.platformRecorder = new FlightRecorder(new PlatformRecorder());
                }
                catch (final IllegalStateException ex) {
                    throw ex;
                }
                catch (final Exception ex2) {
                    throw new IllegalStateException("Can't create Flight Recorder. " + ex2.getMessage(), ex2);
                }
                FlightRecorder.initialized = true;
                Logger.log(LogTag.JFR, LogLevel.INFO, "Flight Recorder initialized");
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "maxchunksize: " + Options.getMaxChunkSize() + " bytes");
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "memorysize: " + Options.getMemorySize() + " bytes");
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "globalbuffersize: " + Options.getGlobalBufferSize() + " bytes");
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "globalbuffercount: " + Options.getGlobalBufferCount());
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "dumppath: " + Options.getDumpPath());
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "samplethreads: " + Options.getSampleThreads());
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "stackdepth: " + Options.getStackDepth());
                Logger.log(LogTag.JFR, LogLevel.DEBUG, "threadbuffersize: " + Options.getThreadBufferSize());
                Logger.log(LogTag.JFR, LogLevel.INFO, "Created repository " + Repository.getRepository().getRepositoryPath().toString());
                PlatformRecorder.notifyRecorderInitialized(FlightRecorder.platformRecorder);
            }
        }
        return FlightRecorder.platformRecorder;
    }
    
    public static void addPeriodicEvent(final Class<? extends Event> clazz, final Runnable runnable) throws SecurityException {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(runnable);
        if (JVMSupport.isNotAvailable()) {
            return;
        }
        Utils.ensureValidEventSubclass(clazz);
        Utils.checkRegisterPermission();
        RequestEngine.addHook(AccessController.getContext(), EventType.getEventType(clazz).getPlatformEventType(), runnable);
    }
    
    public static boolean removePeriodicEvent(final Runnable runnable) throws SecurityException {
        Objects.requireNonNull(runnable);
        Utils.checkRegisterPermission();
        return !JVMSupport.isNotAvailable() && RequestEngine.removeHook(runnable);
    }
    
    public List<EventType> getEventTypes() {
        return Collections.unmodifiableList((List<? extends EventType>)MetadataRepository.getInstance().getRegisteredEventTypes());
    }
    
    public static void addListener(final FlightRecorderListener flightRecorderListener) {
        Objects.requireNonNull(flightRecorderListener);
        Utils.checkAccessFlightRecorder();
        if (JVMSupport.isNotAvailable()) {
            return;
        }
        PlatformRecorder.addListener(flightRecorderListener);
    }
    
    public static boolean removeListener(final FlightRecorderListener flightRecorderListener) {
        Objects.requireNonNull(flightRecorderListener);
        Utils.checkAccessFlightRecorder();
        return !JVMSupport.isNotAvailable() && PlatformRecorder.removeListener(flightRecorderListener);
    }
    
    public static boolean isAvailable() {
        return !JVMSupport.isNotAvailable() && JVM.getJVM().isAvailable();
    }
    
    public static boolean isInitialized() {
        return FlightRecorder.initialized;
    }
    
    PlatformRecorder getInternal() {
        return this.internal;
    }
    
    private void setAssociate(final FlightRecorderAssociate associate) {
        this.internal.setAssociate(associate);
    }
}
