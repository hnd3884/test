package jdk.jfr.internal;

import java.util.Collections;
import java.util.Collection;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.util.Date;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.List;
import jdk.jfr.Configuration;
import jdk.jfr.FlightRecorderListener;
import java.util.TreeMap;
import java.util.Iterator;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.io.IOException;
import java.util.StringJoiner;
import java.security.AccessController;
import java.util.LinkedHashMap;
import java.security.AccessControlContext;
import java.util.TimerTask;
import jdk.jfr.Recording;
import java.util.LinkedList;
import jdk.jfr.RecordingState;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;

public final class PlatformRecording implements AutoCloseable
{
    private final PlatformRecorder recorder;
    private final long id;
    private Map<String, String> settings;
    private Duration duration;
    private Duration maxAge;
    private long maxSize;
    private WriteableUserPath destination;
    private boolean toDisk;
    private String name;
    private boolean dumpOnExit;
    private SecuritySupport.SafePath dumpOnExitDirectory;
    private Instant stopTime;
    private Instant startTime;
    private RecordingState state;
    private long size;
    private final LinkedList<RepositoryChunk> chunks;
    private volatile Recording recording;
    private TimerTask stopTask;
    private TimerTask startTask;
    private AccessControlContext noDestinationDumpOnExitAccessControlContext;
    private boolean shuoldWriteActiveRecordingEvent;
    
    PlatformRecording(final PlatformRecorder recorder, final long id) {
        this.settings = new LinkedHashMap<String, String>();
        this.toDisk = true;
        this.dumpOnExitDirectory = new SecuritySupport.SafePath(".");
        this.state = RecordingState.NEW;
        this.chunks = new LinkedList<RepositoryChunk>();
        this.shuoldWriteActiveRecordingEvent = true;
        this.noDestinationDumpOnExitAccessControlContext = AccessController.getContext();
        this.id = id;
        this.recorder = recorder;
        this.name = String.valueOf(id);
    }
    
    public void start() {
        final RecordingState state;
        final RecordingState state2;
        synchronized (this.recorder) {
            state = this.getState();
            if (!Utils.isBefore(this.state, RecordingState.RUNNING)) {
                throw new IllegalStateException("Recording can only be started once.");
            }
            if (this.startTask != null) {
                this.startTask.cancel();
                this.startTask = null;
                this.startTime = null;
            }
            this.recorder.start(this);
            Logger.log(LogTag.JFR, LogLevel.INFO, () -> {
                final StringJoiner stringJoiner = new StringJoiner(", ");
                if (!this.toDisk) {
                    stringJoiner.add("disk=false");
                }
                if (this.maxAge != null) {
                    stringJoiner.add("maxage=" + Utils.formatTimespan(this.maxAge, ""));
                }
                if (this.maxSize != 0L) {
                    stringJoiner.add("maxsize=" + Utils.formatBytesCompact(this.maxSize));
                }
                if (this.dumpOnExit) {
                    stringJoiner.add("dumponexit=true");
                }
                if (this.duration != null) {
                    stringJoiner.add("duration=" + Utils.formatTimespan(this.duration, ""));
                }
                if (this.destination != null) {
                    stringJoiner.add("filename=" + this.destination.getRealPathText());
                }
                stringJoiner.toString();
                String string = null;
                if (string.length() != 0) {
                    string = "{" + string + "}";
                }
                return "Started recording \"" + this.getName() + "\" (" + this.getId() + ") " + string;
            });
            state2 = this.getState();
        }
        this.notifyIfStateChanged(state, state2);
    }
    
    public boolean stop(final String s) {
        final RecordingState state;
        final RecordingState state2;
        synchronized (this.recorder) {
            state = this.getState();
            if (this.stopTask != null) {
                this.stopTask.cancel();
                this.stopTask = null;
            }
            this.recorder.stop(this);
            Logger.log(LogTag.JFR, LogLevel.INFO, "Stopped recording \"" + this.getName() + "\" (" + this.getId() + ")" + ((s == null) ? "" : (". Reason \"" + s + "\".")));
            this.stopTime = Instant.now();
            state2 = this.getState();
        }
        final WriteableUserPath destination = this.getDestination();
        if (destination != null) {
            try {
                this.dumpStopped(destination);
                Logger.log(LogTag.JFR, LogLevel.INFO, "Wrote recording \"" + this.getName() + "\" (" + this.getId() + ") to " + destination.getRealPathText());
                this.notifyIfStateChanged(state2, state);
                this.close();
            }
            catch (final IOException ex) {}
        }
        else {
            this.notifyIfStateChanged(state2, state);
        }
        return true;
    }
    
    public void scheduleStart(final Duration duration) {
        synchronized (this.recorder) {
            this.ensureOkForSchedule();
            this.startTime = Instant.now().plus((TemporalAmount)duration);
            final LocalDateTime plus = LocalDateTime.now().plus((TemporalAmount)duration);
            this.setState(RecordingState.DELAYED);
            this.startTask = this.createStartTask();
            this.recorder.getTimer().schedule(this.startTask, duration.toMillis());
            Logger.log(LogTag.JFR, LogLevel.INFO, "Scheduled recording \"" + this.getName() + "\" (" + this.getId() + ") to start at " + plus);
        }
    }
    
    private void ensureOkForSchedule() {
        if (this.getState() != RecordingState.NEW) {
            throw new IllegalStateException("Only a new recoridng can be scheduled for start");
        }
    }
    
    private TimerTask createStartTask() {
        return new TimerTask() {
            @Override
            public void run() {
                synchronized (PlatformRecording.this.recorder) {
                    if (PlatformRecording.this.getState() != RecordingState.DELAYED) {
                        return;
                    }
                    PlatformRecording.this.start();
                }
            }
        };
    }
    
    void scheduleStart(final Instant startTime) {
        synchronized (this.recorder) {
            this.ensureOkForSchedule();
            this.startTime = startTime;
            this.setState(RecordingState.DELAYED);
            this.startTask = this.createStartTask();
            this.recorder.getTimer().schedule(this.startTask, startTime.toEpochMilli());
        }
    }
    
    public Map<String, String> getSettings() {
        synchronized (this.recorder) {
            return this.settings;
        }
    }
    
    public long getSize() {
        return this.size;
    }
    
    public Instant getStopTime() {
        synchronized (this.recorder) {
            return this.stopTime;
        }
    }
    
    public Instant getStartTime() {
        synchronized (this.recorder) {
            return this.startTime;
        }
    }
    
    public Long getMaxSize() {
        synchronized (this.recorder) {
            return this.maxSize;
        }
    }
    
    public Duration getMaxAge() {
        synchronized (this.recorder) {
            return this.maxAge;
        }
    }
    
    public String getName() {
        synchronized (this.recorder) {
            return this.name;
        }
    }
    
    public RecordingState getState() {
        synchronized (this.recorder) {
            return this.state;
        }
    }
    
    @Override
    public void close() {
        final RecordingState state;
        final RecordingState state2;
        synchronized (this.recorder) {
            state = this.getState();
            if (RecordingState.CLOSED != this.getState()) {
                if (this.startTask != null) {
                    this.startTask.cancel();
                    this.startTask = null;
                }
                this.recorder.finish(this);
                final Iterator<Object> iterator = this.chunks.iterator();
                while (iterator.hasNext()) {
                    this.removed(iterator.next());
                }
                this.chunks.clear();
                this.setState(RecordingState.CLOSED);
                Logger.log(LogTag.JFR, LogLevel.INFO, "Closed recording \"" + this.getName() + "\" (" + this.getId() + ")");
            }
            state2 = this.getState();
        }
        this.notifyIfStateChanged(state2, state);
    }
    
    public PlatformRecording newSnapshotClone(final String s, final Boolean b) throws IOException {
        if (!Thread.holdsLock(this.recorder)) {
            throw new InternalError("Caller must have recorder lock");
        }
        final RecordingState state = this.getState();
        if (state == RecordingState.CLOSED) {
            throw new IOException("Recording \"" + this.name + "\" (id=" + this.id + ") has been closed, no contents to write");
        }
        if (state == RecordingState.DELAYED || state == RecordingState.NEW) {
            throw new IOException("Recording \"" + this.name + "\" (id=" + this.id + ") has not started, no contents to write");
        }
        if (state == RecordingState.STOPPED) {
            final PlatformRecording temporaryRecording = this.recorder.newTemporaryRecording();
            final Iterator<Object> iterator = this.chunks.iterator();
            while (iterator.hasNext()) {
                temporaryRecording.add(iterator.next());
            }
            return temporaryRecording;
        }
        final PlatformRecording temporaryRecording2 = this.recorder.newTemporaryRecording();
        temporaryRecording2.setShouldWriteActiveRecordingEvent(false);
        temporaryRecording2.setName(this.getName());
        temporaryRecording2.setToDisk(true);
        if (!this.isToDisk()) {
            temporaryRecording2.start();
        }
        else {
            final Iterator<Object> iterator2 = this.chunks.iterator();
            while (iterator2.hasNext()) {
                temporaryRecording2.add(iterator2.next());
            }
            temporaryRecording2.setState(RecordingState.RUNNING);
            temporaryRecording2.setStartTime(this.getStartTime());
        }
        if (b == null) {
            temporaryRecording2.setSettings(this.getSettings());
            temporaryRecording2.stop(s);
        }
        else {
            synchronized (MetadataRepository.getInstance()) {
                temporaryRecording2.setSettings(OldObjectSample.createSettingsForSnapshot(this, b));
                temporaryRecording2.stop(s);
            }
        }
        return temporaryRecording2;
    }
    
    public boolean isToDisk() {
        synchronized (this.recorder) {
            return this.toDisk;
        }
    }
    
    public void setMaxSize(final long maxSize) {
        synchronized (this.recorder) {
            if (this.getState() == RecordingState.CLOSED) {
                throw new IllegalStateException("Can't set max age when recording is closed");
            }
            this.maxSize = maxSize;
            this.trimToSize();
        }
    }
    
    public void setDestination(final WriteableUserPath destination) throws IOException {
        synchronized (this.recorder) {
            if (Utils.isState(this.getState(), RecordingState.STOPPED, RecordingState.CLOSED)) {
                throw new IllegalStateException("Destination can't be set on a recording that has been stopped/closed");
            }
            this.destination = destination;
        }
    }
    
    public WriteableUserPath getDestination() {
        synchronized (this.recorder) {
            return this.destination;
        }
    }
    
    void setState(final RecordingState state) {
        synchronized (this.recorder) {
            this.state = state;
        }
    }
    
    void setStartTime(final Instant startTime) {
        synchronized (this.recorder) {
            this.startTime = startTime;
        }
    }
    
    void setStopTime(final Instant stopTime) {
        synchronized (this.recorder) {
            this.stopTime = stopTime;
        }
    }
    
    public long getId() {
        synchronized (this.recorder) {
            return this.id;
        }
    }
    
    public void setName(final String name) {
        synchronized (this.recorder) {
            this.ensureNotClosed();
            this.name = name;
        }
    }
    
    private void ensureNotClosed() {
        if (this.getState() == RecordingState.CLOSED) {
            throw new IllegalStateException("Can't change name on a closed recording");
        }
    }
    
    public void setDumpOnExit(final boolean dumpOnExit) {
        synchronized (this.recorder) {
            this.dumpOnExit = dumpOnExit;
        }
    }
    
    public boolean getDumpOnExit() {
        synchronized (this.recorder) {
            return this.dumpOnExit;
        }
    }
    
    public void setToDisk(final boolean toDisk) {
        synchronized (this.recorder) {
            if (!Utils.isState(this.getState(), RecordingState.NEW, RecordingState.DELAYED)) {
                throw new IllegalStateException("Recording option disk can't be changed after recording has started");
            }
            this.toDisk = toDisk;
        }
    }
    
    public void setSetting(final String s, final String s2) {
        synchronized (this.recorder) {
            this.settings.put(s, s2);
            if (this.getState() == RecordingState.RUNNING) {
                this.recorder.updateSettings();
            }
        }
    }
    
    public void setSettings(final Map<String, String> map) {
        this.setSettings(map, true);
    }
    
    private void setSettings(final Map<String, String> map, final boolean b) {
        if (Logger.shouldLog(LogTag.JFR_SETTING, LogLevel.INFO) && b) {
            final TreeMap treeMap = new TreeMap((Map<? extends K, ? extends V>)map);
            Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, "New settings for recording \"" + this.getName() + "\" (" + this.getId() + ")");
            for (final Map.Entry entry : treeMap.entrySet()) {
                Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, (String)entry.getKey() + "=\"" + (String)entry.getValue() + "\"");
            }
        }
        synchronized (this.recorder) {
            this.settings = new LinkedHashMap<String, String>(map);
            if (this.getState() == RecordingState.RUNNING && b) {
                this.recorder.updateSettings();
            }
        }
    }
    
    private void notifyIfStateChanged(final RecordingState recordingState, final RecordingState recordingState2) {
        if (recordingState2 == recordingState) {
            return;
        }
        for (final FlightRecorderListener flightRecorderListener : PlatformRecorder.getListeners()) {
            try {
                flightRecorderListener.recordingStateChanged(this.getRecording());
            }
            catch (final RuntimeException ex) {
                Logger.log(LogTag.JFR, LogLevel.WARN, "Error notifying recorder listener:" + ex.getMessage());
            }
        }
    }
    
    public void setRecording(final Recording recording) {
        this.recording = recording;
    }
    
    public Recording getRecording() {
        return this.recording;
    }
    
    @Override
    public String toString() {
        return this.getName() + " (id=" + this.getId() + ") " + this.getState();
    }
    
    public void setConfiguration(final Configuration configuration) {
        this.setSettings(configuration.getSettings());
    }
    
    public void setMaxAge(final Duration maxAge) {
        synchronized (this.recorder) {
            if (this.getState() == RecordingState.CLOSED) {
                throw new IllegalStateException("Can't set max age when recording is closed");
            }
            if ((this.maxAge = maxAge) != null) {
                this.trimToAge(Instant.now().minus((TemporalAmount)maxAge));
            }
        }
    }
    
    void appendChunk(final RepositoryChunk repositoryChunk) {
        if (!repositoryChunk.isFinished()) {
            throw new Error("not finished chunk " + repositoryChunk.getStartTime());
        }
        synchronized (this.recorder) {
            if (!this.toDisk) {
                return;
            }
            if (this.maxAge != null) {
                this.trimToAge(repositoryChunk.getEndTime().minus((TemporalAmount)this.maxAge));
            }
            this.chunks.addLast(repositoryChunk);
            this.added(repositoryChunk);
            this.trimToSize();
        }
    }
    
    private void trimToSize() {
        if (this.maxSize == 0L) {
            return;
        }
        while (this.size > this.maxSize && this.chunks.size() > 1) {
            this.removed(this.chunks.removeFirst());
        }
    }
    
    private void trimToAge(final Instant instant) {
        while (!this.chunks.isEmpty()) {
            final RepositoryChunk repositoryChunk = this.chunks.peek();
            if (repositoryChunk.getEndTime().isAfter(instant)) {
                return;
            }
            this.chunks.removeFirst();
            this.removed(repositoryChunk);
        }
    }
    
    void add(final RepositoryChunk repositoryChunk) {
        this.chunks.add(repositoryChunk);
        this.added(repositoryChunk);
    }
    
    private void added(final RepositoryChunk repositoryChunk) {
        repositoryChunk.use();
        this.size += repositoryChunk.getSize();
        Logger.log(LogTag.JFR, LogLevel.DEBUG, () -> "Recording \"" + this.name + "\" (" + this.id + ") added chunk " + repositoryChunk2.toString() + ", current size=" + this.size);
    }
    
    private void removed(final RepositoryChunk repositoryChunk) {
        this.size -= repositoryChunk.getSize();
        Logger.log(LogTag.JFR, LogLevel.DEBUG, () -> "Recording \"" + this.name + "\" (" + this.id + ") removed chunk " + repositoryChunk2.toString() + ", current size=" + this.size);
        repositoryChunk.release();
    }
    
    public List<RepositoryChunk> getChunks() {
        return this.chunks;
    }
    
    public InputStream open(final Instant instant, final Instant instant2) throws IOException {
        synchronized (this.recorder) {
            if (this.getState() != RecordingState.STOPPED) {
                throw new IOException("Recording must be stopped before it can be read.");
            }
            final ArrayList list = new ArrayList();
            for (final RepositoryChunk repositoryChunk : this.chunks) {
                if (repositoryChunk.isFinished()) {
                    final Instant startTime = repositoryChunk.getStartTime();
                    final Instant endTime = repositoryChunk.getEndTime();
                    if ((instant != null && endTime.isBefore(instant)) || (instant2 != null && startTime.isAfter(instant2))) {
                        continue;
                    }
                    list.add(repositoryChunk);
                }
            }
            if (list.isEmpty()) {
                return null;
            }
            return new ChunkInputStream(list);
        }
    }
    
    public Duration getDuration() {
        synchronized (this.recorder) {
            return this.duration;
        }
    }
    
    void setInternalDuration(final Duration duration) {
        this.duration = duration;
    }
    
    public void setDuration(final Duration internalDuration) {
        synchronized (this.recorder) {
            if (Utils.isState(this.getState(), RecordingState.STOPPED, RecordingState.CLOSED)) {
                throw new IllegalStateException("Duration can't be set after a recording has been stopped/closed");
            }
            this.setInternalDuration(internalDuration);
            if (this.getState() != RecordingState.NEW) {
                this.updateTimer();
            }
        }
    }
    
    void updateTimer() {
        if (this.stopTask != null) {
            this.stopTask.cancel();
            this.stopTask = null;
        }
        if (this.getState() == RecordingState.CLOSED) {
            return;
        }
        if (this.duration != null) {
            this.stopTask = this.createStopTask();
            this.recorder.getTimer().schedule(this.stopTask, new Date(this.startTime.plus((TemporalAmount)this.duration).toEpochMilli()));
        }
    }
    
    TimerTask createStopTask() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    PlatformRecording.this.stop("End of duration reached");
                }
                catch (final Throwable t) {
                    Logger.log(LogTag.JFR, LogLevel.ERROR, "Could not stop recording.");
                }
            }
        };
    }
    
    public Recording newCopy(final boolean b) {
        return this.recorder.newCopy(this, b);
    }
    
    void setStopTask(final TimerTask stopTask) {
        synchronized (this.recorder) {
            this.stopTask = stopTask;
        }
    }
    
    void clearDestination() {
        this.destination = null;
    }
    
    public AccessControlContext getNoDestinationDumpOnExitAccessControlContext() {
        return this.noDestinationDumpOnExitAccessControlContext;
    }
    
    void setShouldWriteActiveRecordingEvent(final boolean shuoldWriteActiveRecordingEvent) {
        this.shuoldWriteActiveRecordingEvent = shuoldWriteActiveRecordingEvent;
    }
    
    boolean shouldWriteMetadataEvent() {
        return this.shuoldWriteActiveRecordingEvent;
    }
    
    public void dump(final WriteableUserPath writeableUserPath) throws IOException {
        synchronized (this.recorder) {
            try (final PlatformRecording snapshotClone = this.newSnapshotClone("Dumped by user", null)) {
                snapshotClone.dumpStopped(writeableUserPath);
            }
        }
    }
    
    public void dumpStopped(final WriteableUserPath writeableUserPath) throws IOException {
        synchronized (this.recorder) {
            writeableUserPath.doPriviligedIO(() -> {
                final ChunksChannel chunksChannel = new ChunksChannel(this.chunks);
                try {
                    FileChannel.open(writeableUserPath2.getReal(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                    try {
                        final FileChannel fileChannel;
                        chunksChannel.transferTo(fileChannel);
                        fileChannel.force(true);
                    }
                    catch (final Throwable t) {
                        throw t;
                    }
                    finally {
                        final FileChannel fileChannel;
                        if (fileChannel != null) {
                            final Throwable t2;
                            if (t2 != null) {
                                try {
                                    fileChannel.close();
                                }
                                catch (final Throwable t3) {
                                    t2.addSuppressed(t3);
                                }
                            }
                            else {
                                fileChannel.close();
                            }
                        }
                    }
                }
                catch (final Throwable t4) {
                    throw t4;
                }
                finally {
                    if (chunksChannel != null) {
                        final Throwable t5;
                        if (t5 != null) {
                            try {
                                chunksChannel.close();
                            }
                            catch (final Throwable t6) {
                                t5.addSuppressed(t6);
                            }
                        }
                        else {
                            chunksChannel.close();
                        }
                    }
                }
                return null;
            });
        }
    }
    
    public void filter(final Instant instant, final Instant instant2, final Long n) {
        synchronized (this.recorder) {
            List<RepositoryChunk> list = removeAfter(instant2, removeBefore(instant, new ArrayList<RepositoryChunk>(this.chunks)));
            if (n != null) {
                if (instant != null && instant2 == null) {
                    list = reduceFromBeginning(n, list);
                }
                else {
                    list = reduceFromEnd(n, list);
                }
            }
            int n2 = 0;
            for (final RepositoryChunk repositoryChunk : list) {
                n2 += (int)repositoryChunk.getSize();
                repositoryChunk.use();
            }
            this.size = n2;
            final Iterator<Object> iterator2 = this.chunks.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().release();
            }
            this.chunks.clear();
            this.chunks.addAll(list);
        }
    }
    
    private static List<RepositoryChunk> removeBefore(final Instant instant, final List<RepositoryChunk> list) {
        if (instant == null) {
            return list;
        }
        final ArrayList list2 = new ArrayList(list.size());
        for (final RepositoryChunk repositoryChunk : list) {
            if (!repositoryChunk.getEndTime().isBefore(instant)) {
                list2.add(repositoryChunk);
            }
        }
        return list2;
    }
    
    private static List<RepositoryChunk> removeAfter(final Instant instant, final List<RepositoryChunk> list) {
        if (instant == null) {
            return list;
        }
        final ArrayList list2 = new ArrayList(list.size());
        for (final RepositoryChunk repositoryChunk : list) {
            if (!repositoryChunk.getStartTime().isAfter(instant)) {
                list2.add(repositoryChunk);
            }
        }
        return list2;
    }
    
    private static List<RepositoryChunk> reduceFromBeginning(final Long n, final List<RepositoryChunk> list) {
        if (n == null || list.isEmpty()) {
            return list;
        }
        final ArrayList list2 = new ArrayList(list.size());
        long n2 = 0L;
        for (final RepositoryChunk repositoryChunk : list) {
            n2 += repositoryChunk.getSize();
            if (n2 > n) {
                break;
            }
            list2.add((Object)repositoryChunk);
        }
        if (list2.isEmpty()) {
            list2.add((Object)list.get(0));
        }
        return (List<RepositoryChunk>)list2;
    }
    
    private static List<RepositoryChunk> reduceFromEnd(final Long n, final List<RepositoryChunk> list) {
        Collections.reverse(list);
        final List<RepositoryChunk> reduceFromBeginning = reduceFromBeginning(n, list);
        Collections.reverse(reduceFromBeginning);
        return reduceFromBeginning;
    }
    
    public void setDumpOnExitDirectory(final SecuritySupport.SafePath dumpOnExitDirectory) {
        this.dumpOnExitDirectory = dumpOnExitDirectory;
    }
    
    public SecuritySupport.SafePath getDumpOnExitDirectory() {
        return this.dumpOnExitDirectory;
    }
}
