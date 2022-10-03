package jdk.jfr.internal;

import java.time.temporal.Temporal;
import java.util.TimerTask;
import jdk.jfr.Recording;
import java.util.Comparator;
import java.util.HashSet;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.time.Instant;
import java.util.Iterator;
import jdk.jfr.FlightRecorder;
import java.security.AccessController;
import jdk.jfr.FlightRecorderListener;
import java.util.Collections;
import java.util.Collection;
import jdk.jfr.RecordingState;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import jdk.jfr.events.ActiveSettingEvent;
import jdk.jfr.Event;
import jdk.jfr.events.ActiveRecordingEvent;
import jdk.jfr.internal.instrument.JDKEvents;
import java.util.ArrayList;
import jdk.jfr.EventType;
import java.util.Timer;
import java.util.List;

public final class PlatformRecorder
{
    private final List<PlatformRecording> recordings;
    private static final List<SecuritySupport.SecureRecorderListener> changeListeners;
    private static FlightRecorderAssociate associate;
    private final Repository repository;
    private final Timer timer;
    private static final JVM jvm;
    private final EventType activeRecordingEvent;
    private final EventType activeSettingEvent;
    private final Thread shutdownHook;
    private long recordingCounter;
    private RepositoryChunk currentChunk;
    
    public PlatformRecorder() throws Exception {
        this.recordings = new ArrayList<PlatformRecording>();
        this.recordingCounter = 0L;
        this.repository = Repository.getRepository();
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Initialized disk repository");
        this.repository.ensureRepository();
        PlatformRecorder.jvm.createNativeJFR();
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Created native");
        JDKEvents.initialize();
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.INFO, "Registered JDK events");
        JDKEvents.addInstrumentation();
        this.startDiskMonitor();
        SecuritySupport.registerEvent((Class<? extends Event>)ActiveRecordingEvent.class);
        this.activeRecordingEvent = EventType.getEventType((Class<? extends Event>)ActiveRecordingEvent.class);
        SecuritySupport.registerEvent((Class<? extends Event>)ActiveSettingEvent.class);
        this.activeSettingEvent = EventType.getEventType((Class<? extends Event>)ActiveSettingEvent.class);
        SecuritySupport.setUncaughtExceptionHandler(this.shutdownHook = SecuritySupport.createThreadWitNoPermissions("JFR: Shutdown Hook", new ShutdownHook(this)), new ShutdownHook.ExceptionHandler());
        SecuritySupport.registerShutdownHook(this.shutdownHook);
        this.timer = createTimer();
    }
    
    private static Timer createTimer() {
        try {
            final CopyOnWriteArrayList list = new CopyOnWriteArrayList();
            final Thread threadWitNoPermissions = SecuritySupport.createThreadWitNoPermissions("Permissionless thread", () -> list2.add(new Timer("JFR Recording Scheduler", (boolean)(1 != 0))));
            threadWitNoPermissions.start();
            threadWitNoPermissions.join();
            return (Timer)list.get(0);
        }
        catch (final InterruptedException ex) {
            throw new IllegalStateException("Not able to create timer task. " + ex.getMessage(), ex);
        }
    }
    
    public synchronized PlatformRecording newRecording(final Map<String, String> map) {
        final long recordingCounter = this.recordingCounter + 1L;
        this.recordingCounter = recordingCounter;
        return this.newRecording(map, recordingCounter);
    }
    
    public PlatformRecording newTemporaryRecording() {
        if (!Thread.holdsLock(this)) {
            throw new InternalError("Caller must have recorder lock");
        }
        return this.newRecording(new HashMap<String, String>(), 0L);
    }
    
    private synchronized PlatformRecording newRecording(final Map<String, String> settings, final long n) {
        final PlatformRecording platformRecording = new PlatformRecording(this, n);
        if (!settings.isEmpty()) {
            platformRecording.setSettings(settings);
        }
        this.recordings.add(platformRecording);
        return platformRecording;
    }
    
    synchronized void finish(final PlatformRecording platformRecording) {
        if (platformRecording.getState() == RecordingState.RUNNING) {
            platformRecording.stop("Recording closed");
        }
        this.recordings.remove(platformRecording);
    }
    
    public synchronized List<PlatformRecording> getRecordings() {
        return Collections.unmodifiableList((List<? extends PlatformRecording>)new ArrayList<PlatformRecording>(this.recordings));
    }
    
    public static synchronized void addListener(final FlightRecorderListener flightRecorderListener) {
        final SecuritySupport.SecureRecorderListener secureRecorderListener = new SecuritySupport.SecureRecorderListener(AccessController.getContext(), flightRecorderListener);
        final boolean initialized;
        synchronized (PlatformRecorder.class) {
            initialized = FlightRecorder.isInitialized();
            PlatformRecorder.changeListeners.add(secureRecorderListener);
        }
        if (initialized) {
            secureRecorderListener.recorderInitialized(FlightRecorder.getFlightRecorder());
        }
    }
    
    public static synchronized boolean removeListener(final FlightRecorderListener flightRecorderListener) {
        for (final SecuritySupport.SecureRecorderListener secureRecorderListener : new ArrayList(PlatformRecorder.changeListeners)) {
            if (secureRecorderListener.getChangeListener() == flightRecorderListener) {
                PlatformRecorder.changeListeners.remove(secureRecorderListener);
                return true;
            }
        }
        return false;
    }
    
    static synchronized List<FlightRecorderListener> getListeners() {
        return new ArrayList<FlightRecorderListener>(PlatformRecorder.changeListeners);
    }
    
    Timer getTimer() {
        return this.timer;
    }
    
    public static void notifyRecorderInitialized(final FlightRecorder flightRecorder) {
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.TRACE, "Notifying listeners that Flight Recorder is initialized");
        final Iterator<FlightRecorderListener> iterator = getListeners().iterator();
        while (iterator.hasNext()) {
            iterator.next().recorderInitialized(flightRecorder);
        }
    }
    
    synchronized void destroy() {
        try {
            this.timer.cancel();
        }
        catch (final Exception ex) {
            Logger.log(LogTag.JFR_SYSTEM, LogLevel.WARN, "Shutdown hook could not cancel timer");
        }
        for (final PlatformRecording platformRecording : this.getRecordings()) {
            if (platformRecording.getState() == RecordingState.RUNNING) {
                try {
                    platformRecording.stop("Shutdown");
                }
                catch (final Exception ex2) {
                    Logger.log(LogTag.JFR, LogLevel.WARN, "Recording " + platformRecording.getName() + ":" + platformRecording.getId() + " could not be stopped");
                }
            }
        }
        JDKEvents.remove();
        if (PlatformRecorder.jvm.hasNativeJFR()) {
            if (PlatformRecorder.jvm.isRecording()) {
                PlatformRecorder.jvm.endRecording_();
            }
            PlatformRecorder.jvm.destroyNativeJFR();
        }
        if (PlatformRecorder.associate != null) {
            PlatformRecorder.associate.finishJoin();
        }
        this.repository.clear();
    }
    
    synchronized void start(final PlatformRecording platformRecording) {
        final Instant now = Instant.now();
        platformRecording.setStartTime(now);
        platformRecording.updateTimer();
        final Duration duration = platformRecording.getDuration();
        if (duration != null) {
            platformRecording.setStopTime(now.plus((TemporalAmount)duration));
        }
        boolean toDisk = platformRecording.isToDisk();
        boolean b = true;
        for (final PlatformRecording platformRecording2 : this.getRecordings()) {
            if (platformRecording2.getState() == RecordingState.RUNNING) {
                b = false;
                if (!platformRecording2.isToDisk()) {
                    continue;
                }
                toDisk = true;
            }
        }
        if (b) {
            RepositoryChunk chunk = null;
            if (toDisk) {
                chunk = this.repository.newChunk(now);
                MetadataRepository.getInstance().setOutput(chunk.getUnfishedFile().toString());
            }
            else {
                MetadataRepository.getInstance().setOutput(null);
            }
            this.currentChunk = chunk;
            PlatformRecorder.jvm.beginRecording_();
            platformRecording.setState(RecordingState.RUNNING);
            this.updateSettings();
            this.writeMetaEvents();
        }
        else {
            RepositoryChunk chunk2 = null;
            if (toDisk) {
                chunk2 = this.repository.newChunk(now);
                RequestEngine.doChunkEnd();
                MetadataRepository.getInstance().setOutput(chunk2.getUnfishedFile().toString());
            }
            platformRecording.setState(RecordingState.RUNNING);
            this.updateSettings();
            this.writeMetaEvents();
            if (this.currentChunk != null) {
                this.finishChunk(this.currentChunk, now, platformRecording);
            }
            this.currentChunk = chunk2;
        }
        RequestEngine.doChunkBegin();
    }
    
    synchronized void stop(final PlatformRecording platformRecording) {
        final RecordingState state = platformRecording.getState();
        if (Utils.isAfter(state, RecordingState.RUNNING)) {
            throw new IllegalStateException("Can't stop an already stopped recording.");
        }
        if (Utils.isBefore(state, RecordingState.RUNNING)) {
            throw new IllegalStateException("Recording must be started before it can be stopped.");
        }
        final Instant now = Instant.now();
        boolean b = false;
        boolean b2 = true;
        for (final PlatformRecording platformRecording2 : this.getRecordings()) {
            final RecordingState state2 = platformRecording2.getState();
            if (platformRecording2 != platformRecording && RecordingState.RUNNING == state2) {
                b2 = false;
                if (!platformRecording2.isToDisk()) {
                    continue;
                }
                b = true;
            }
        }
        OldObjectSample.emit(platformRecording);
        if (b2) {
            RequestEngine.doChunkEnd();
            if (platformRecording.isToDisk()) {
                if (this.currentChunk != null) {
                    MetadataRepository.getInstance().setOutput(null);
                    this.finishChunk(this.currentChunk, now, null);
                    this.currentChunk = null;
                }
            }
            else {
                this.dumpMemoryToDestination(platformRecording);
            }
            PlatformRecorder.jvm.endRecording_();
            this.disableEvents();
        }
        else {
            RepositoryChunk chunk = null;
            RequestEngine.doChunkEnd();
            this.updateSettingsButIgnoreRecording(platformRecording);
            if (b) {
                chunk = this.repository.newChunk(now);
                MetadataRepository.getInstance().setOutput(chunk.getUnfishedFile().toString());
            }
            else {
                MetadataRepository.getInstance().setOutput(null);
            }
            this.writeMetaEvents();
            if (this.currentChunk != null) {
                this.finishChunk(this.currentChunk, now, null);
            }
            this.currentChunk = chunk;
            RequestEngine.doChunkBegin();
        }
        platformRecording.setState(RecordingState.STOPPED);
    }
    
    private void dumpMemoryToDestination(final PlatformRecording platformRecording) {
        final WriteableUserPath destination = platformRecording.getDestination();
        if (destination != null) {
            MetadataRepository.getInstance().setOutput(destination.getRealPathText());
            platformRecording.clearDestination();
        }
    }
    
    private void disableEvents() {
        MetadataRepository.getInstance().disableEvents();
    }
    
    void updateSettings() {
        this.updateSettingsButIgnoreRecording(null);
    }
    
    void updateSettingsButIgnoreRecording(final PlatformRecording platformRecording) {
        final List<PlatformRecording> runningRecordings = this.getRunningRecordings();
        final ArrayList settings = new ArrayList(runningRecordings.size());
        for (final PlatformRecording platformRecording2 : runningRecordings) {
            if (platformRecording2 != platformRecording) {
                settings.add((Object)platformRecording2.getSettings());
            }
        }
        MetadataRepository.getInstance().setSettings((List<Map<String, String>>)settings);
    }
    
    synchronized void rotateDisk() {
        final Instant now = Instant.now();
        final RepositoryChunk chunk = this.repository.newChunk(now);
        RequestEngine.doChunkEnd();
        MetadataRepository.getInstance().setOutput(chunk.getUnfishedFile().toString());
        this.writeMetaEvents();
        if (this.currentChunk != null) {
            this.finishChunk(this.currentChunk, now, null);
        }
        this.currentChunk = chunk;
        RequestEngine.doChunkBegin();
    }
    
    private List<PlatformRecording> getRunningRecordings() {
        final ArrayList list = new ArrayList();
        for (final PlatformRecording platformRecording : this.getRecordings()) {
            if (platformRecording.getState() == RecordingState.RUNNING) {
                list.add(platformRecording);
            }
        }
        return list;
    }
    
    private List<RepositoryChunk> makeChunkList(final Instant instant, final Instant instant2) {
        final HashSet set = new HashSet();
        final Iterator<PlatformRecording> iterator = this.getRecordings().iterator();
        while (iterator.hasNext()) {
            set.addAll(iterator.next().getChunks());
        }
        if (set.size() > 0) {
            final ArrayList list = new ArrayList(set.size());
            for (final RepositoryChunk repositoryChunk : set) {
                if (repositoryChunk.inInterval(instant, instant2)) {
                    list.add((Object)repositoryChunk);
                }
            }
            Collections.sort((List<Object>)list, (Comparator<? super Object>)RepositoryChunk.END_TIME_COMPARATOR);
            return (List<RepositoryChunk>)list;
        }
        return Collections.emptyList();
    }
    
    private void startDiskMonitor() {
        final Thread threadWitNoPermissions = SecuritySupport.createThreadWitNoPermissions("JFR Periodic Tasks", () -> this.periodicTask());
        SecuritySupport.setDaemonThread(threadWitNoPermissions, true);
        threadWitNoPermissions.start();
    }
    
    private void finishChunk(final RepositoryChunk repositoryChunk, final Instant instant, final PlatformRecording platformRecording) {
        repositoryChunk.finish(instant);
        if (PlatformRecorder.associate != null) {
            PlatformRecorder.associate.nextChunk(repositoryChunk, repositoryChunk.getFile(), repositoryChunk.getStartTime(), repositoryChunk.getEndTime(), repositoryChunk.getSize(), (platformRecording == null) ? null : platformRecording.getRecording());
        }
        for (final PlatformRecording platformRecording2 : this.getRecordings()) {
            if (platformRecording2 != platformRecording && platformRecording2.getState() == RecordingState.RUNNING) {
                platformRecording2.appendChunk(repositoryChunk);
            }
        }
    }
    
    private void writeMetaEvents() {
        if (this.activeRecordingEvent.isEnabled()) {
            for (final PlatformRecording platformRecording : this.getRecordings()) {
                if (platformRecording.getState() == RecordingState.RUNNING && platformRecording.shouldWriteMetadataEvent()) {
                    final ActiveRecordingEvent activeRecordingEvent = new ActiveRecordingEvent();
                    activeRecordingEvent.id = platformRecording.getId();
                    activeRecordingEvent.name = platformRecording.getName();
                    final WriteableUserPath destination = platformRecording.getDestination();
                    activeRecordingEvent.destination = ((destination == null) ? null : destination.getRealPathText());
                    final Duration duration = platformRecording.getDuration();
                    activeRecordingEvent.recordingDuration = ((duration == null) ? Long.MAX_VALUE : duration.toMillis());
                    final Duration maxAge = platformRecording.getMaxAge();
                    activeRecordingEvent.maxAge = ((maxAge == null) ? Long.MAX_VALUE : maxAge.toMillis());
                    final Long maxSize = platformRecording.getMaxSize();
                    activeRecordingEvent.maxSize = ((maxSize == null) ? Long.MAX_VALUE : maxSize);
                    final Instant startTime = platformRecording.getStartTime();
                    activeRecordingEvent.recordingStart = ((startTime == null) ? Long.MAX_VALUE : startTime.toEpochMilli());
                    activeRecordingEvent.commit();
                }
            }
        }
        if (this.activeSettingEvent.isEnabled()) {
            final Iterator<EventControl> iterator2 = MetadataRepository.getInstance().getEventControls().iterator();
            while (iterator2.hasNext()) {
                iterator2.next().writeActiveSettingEvent();
            }
        }
    }
    
    private void periodicTask() {
        if (!PlatformRecorder.jvm.hasNativeJFR()) {
            return;
        }
        while (true) {
            synchronized (this) {
                if (PlatformRecorder.jvm.shouldRotateDisk()) {
                    this.rotateDisk();
                }
            }
            this.takeNap(Math.min(RequestEngine.doPeriodic(), Options.getWaitInterval()));
        }
    }
    
    private void takeNap(final long n) {
        try {
            synchronized (JVM.FILE_DELTA_CHANGE) {
                JVM.FILE_DELTA_CHANGE.wait((n < 10L) ? 10L : n);
            }
        }
        catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    synchronized Recording newCopy(final PlatformRecording platformRecording, final boolean b) {
        final Recording recording = new Recording();
        final PlatformRecording platformRecording2 = PrivateAccess.getInstance().getPlatformRecording(recording);
        platformRecording2.setSettings(platformRecording.getSettings());
        platformRecording2.setMaxAge(platformRecording.getMaxAge());
        platformRecording2.setMaxSize(platformRecording.getMaxSize());
        platformRecording2.setDumpOnExit(platformRecording.getDumpOnExit());
        platformRecording2.setName("Clone of " + platformRecording.getName());
        platformRecording2.setToDisk(platformRecording.isToDisk());
        platformRecording2.setInternalDuration(platformRecording.getDuration());
        platformRecording2.setStartTime(platformRecording.getStartTime());
        platformRecording2.setStopTime(platformRecording.getStopTime());
        if (platformRecording.getState() == RecordingState.NEW) {
            return recording;
        }
        if (platformRecording.getState() == RecordingState.DELAYED) {
            platformRecording2.scheduleStart(platformRecording.getStartTime());
            return recording;
        }
        platformRecording2.setState(platformRecording.getState());
        final Iterator<RepositoryChunk> iterator = platformRecording.getChunks().iterator();
        while (iterator.hasNext()) {
            platformRecording2.add(iterator.next());
        }
        if (platformRecording.getState() == RecordingState.RUNNING) {
            if (b) {
                platformRecording2.stop("Stopped when cloning recording '" + platformRecording.getName() + "'");
            }
            else if (platformRecording.getStopTime() != null) {
                final TimerTask stopTask = platformRecording2.createStopTask();
                platformRecording2.setStopTask(platformRecording2.createStopTask());
                this.getTimer().schedule(stopTask, platformRecording.getStopTime().toEpochMilli());
            }
        }
        return recording;
    }
    
    public synchronized void fillWithRecordedData(final PlatformRecording platformRecording, final Boolean b) {
        boolean b2 = false;
        boolean b3 = false;
        for (final PlatformRecording platformRecording2 : this.recordings) {
            if (platformRecording2.getState() == RecordingState.RUNNING) {
                b2 = true;
                if (!platformRecording2.isToDisk()) {
                    continue;
                }
                b3 = true;
            }
        }
        if (b2) {
            if (!b3) {
                try (final PlatformRecording temporaryRecording = this.newTemporaryRecording()) {
                    temporaryRecording.setToDisk(true);
                    temporaryRecording.setShouldWriteActiveRecordingEvent(false);
                    temporaryRecording.start();
                    OldObjectSample.emit(this.recordings, b);
                    temporaryRecording.stop("Snapshot dump");
                    this.fillWithDiskChunks(platformRecording);
                }
                return;
            }
            OldObjectSample.emit(this.recordings, b);
            this.rotateDisk();
        }
        this.fillWithDiskChunks(platformRecording);
    }
    
    private void fillWithDiskChunks(final PlatformRecording platformRecording) {
        final Iterator<RepositoryChunk> iterator = this.makeChunkList(null, null).iterator();
        while (iterator.hasNext()) {
            platformRecording.add(iterator.next());
        }
        platformRecording.setState(RecordingState.STOPPED);
        Instant startTime = null;
        Instant endTime = null;
        for (final RepositoryChunk repositoryChunk : platformRecording.getChunks()) {
            if (startTime == null || repositoryChunk.getStartTime().isBefore(startTime)) {
                startTime = repositoryChunk.getStartTime();
            }
            if (endTime == null || repositoryChunk.getEndTime().isAfter(endTime)) {
                endTime = repositoryChunk.getEndTime();
            }
        }
        final Instant now = Instant.now();
        if (startTime == null) {
            startTime = now;
        }
        if (endTime == null) {
            endTime = now;
        }
        platformRecording.setStartTime(startTime);
        platformRecording.setStopTime(endTime);
        platformRecording.setInternalDuration(Duration.between(startTime, endTime));
    }
    
    public void setAssociate(final FlightRecorderAssociate associate) {
        PlatformRecorder.associate = associate;
    }
    
    static {
        changeListeners = new ArrayList<SecuritySupport.SecureRecorderListener>();
        jvm = JVM.getJVM();
    }
}
