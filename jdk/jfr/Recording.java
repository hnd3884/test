package jdk.jfr;

import jdk.jfr.internal.Type;
import java.io.InputStream;
import java.io.IOException;
import jdk.jfr.internal.WriteableUserPath;
import java.nio.file.Path;
import jdk.jfr.internal.Utils;
import java.time.Instant;
import java.util.Objects;
import java.time.Duration;
import java.util.HashMap;
import jdk.jfr.internal.PlatformRecorder;
import java.util.Map;
import jdk.jfr.internal.PlatformRecording;
import jdk.Exported;
import java.io.Closeable;

@Exported
public final class Recording implements Closeable
{
    private final PlatformRecording internal;
    
    public Recording(final Map<String, String> map) {
        final PlatformRecorder internal = FlightRecorder.getFlightRecorder().getInternal();
        synchronized (internal) {
            (this.internal = internal.newRecording(map)).setRecording(this);
            if (this.internal.getRecording() != this) {
                throw new InternalError("Internal recording not properly setup");
            }
        }
    }
    
    public Recording() {
        this(new HashMap<String, String>());
    }
    
    public Recording(final Configuration configuration) {
        this(configuration.getSettings());
    }
    
    public void start() {
        this.internal.start();
    }
    
    public void scheduleStart(final Duration duration) {
        Objects.requireNonNull(duration);
        this.internal.scheduleStart(duration);
    }
    
    public boolean stop() {
        return this.internal.stop("Stopped by user");
    }
    
    public Map<String, String> getSettings() {
        return new HashMap<String, String>(this.internal.getSettings());
    }
    
    public long getSize() {
        return this.internal.getSize();
    }
    
    public Instant getStopTime() {
        return this.internal.getStopTime();
    }
    
    public Instant getStartTime() {
        return this.internal.getStartTime();
    }
    
    public long getMaxSize() {
        return this.internal.getMaxSize();
    }
    
    public Duration getMaxAge() {
        return this.internal.getMaxAge();
    }
    
    public String getName() {
        return this.internal.getName();
    }
    
    public void setSettings(final Map<String, String> map) {
        Objects.requireNonNull(map);
        this.internal.setSettings(Utils.sanitizeNullFreeStringMap(map));
    }
    
    public RecordingState getState() {
        return this.internal.getState();
    }
    
    @Override
    public void close() {
        this.internal.close();
    }
    
    public Recording copy(final boolean b) {
        return this.internal.newCopy(b);
    }
    
    public void dump(final Path path) throws IOException {
        Objects.requireNonNull(path);
        this.internal.dump(new WriteableUserPath(path));
    }
    
    public boolean isToDisk() {
        return this.internal.isToDisk();
    }
    
    public void setMaxSize(final long maxSize) {
        if (maxSize < 0L) {
            throw new IllegalArgumentException("Max size of recording can't be negative");
        }
        this.internal.setMaxSize(maxSize);
    }
    
    public void setMaxAge(final Duration maxAge) {
        if (maxAge != null && maxAge.isNegative()) {
            throw new IllegalArgumentException("Max age of recording can't be negative");
        }
        this.internal.setMaxAge(maxAge);
    }
    
    public void setDestination(final Path path) throws IOException {
        this.internal.setDestination((path != null) ? new WriteableUserPath(path) : null);
    }
    
    public Path getDestination() {
        final WriteableUserPath destination = this.internal.getDestination();
        if (destination == null) {
            return null;
        }
        return destination.getPotentiallyMaliciousOriginal();
    }
    
    public long getId() {
        return this.internal.getId();
    }
    
    public void setName(final String name) {
        Objects.requireNonNull(name);
        this.internal.setName(name);
    }
    
    public void setDumpOnExit(final boolean dumpOnExit) {
        this.internal.setDumpOnExit(dumpOnExit);
    }
    
    public boolean getDumpOnExit() {
        return this.internal.getDumpOnExit();
    }
    
    public void setToDisk(final boolean toDisk) {
        this.internal.setToDisk(toDisk);
    }
    
    public InputStream getStream(final Instant instant, final Instant instant2) throws IOException {
        if (instant != null && instant2 != null && instant2.isBefore(instant)) {
            throw new IllegalArgumentException("End time of requested stream must not be before start time");
        }
        return this.internal.open(instant, instant2);
    }
    
    public Duration getDuration() {
        return this.internal.getDuration();
    }
    
    public void setDuration(final Duration duration) {
        this.internal.setDuration(duration);
    }
    
    public EventSettings enable(final String s) {
        Objects.requireNonNull(s);
        final RecordingSettings recordingSettings = new RecordingSettings(this, s);
        recordingSettings.with("enabled", "true");
        return recordingSettings;
    }
    
    public EventSettings disable(final String s) {
        Objects.requireNonNull(s);
        final RecordingSettings recordingSettings = new RecordingSettings(this, s);
        recordingSettings.with("enabled", "false");
        return recordingSettings;
    }
    
    public EventSettings enable(final Class<? extends Event> clazz) {
        Objects.requireNonNull(clazz);
        final RecordingSettings recordingSettings = new RecordingSettings(this, clazz);
        recordingSettings.with("enabled", "true");
        return recordingSettings;
    }
    
    public EventSettings disable(final Class<? extends Event> clazz) {
        Objects.requireNonNull(clazz);
        final RecordingSettings recordingSettings = new RecordingSettings(this, clazz);
        recordingSettings.with("enabled", "false");
        return recordingSettings;
    }
    
    PlatformRecording getInternal() {
        return this.internal;
    }
    
    private void setSetting(final String s, final String s2) {
        Objects.requireNonNull(s);
        Objects.requireNonNull(s2);
        this.internal.setSetting(s, s2);
    }
    
    private static class RecordingSettings extends EventSettings
    {
        private final Recording recording;
        private final String identifier;
        
        RecordingSettings(final Recording recording, final String identifier) {
            this.recording = recording;
            this.identifier = identifier;
        }
        
        RecordingSettings(final Recording recording, final Class<? extends Event> clazz) {
            Utils.ensureValidEventSubclass(clazz);
            this.recording = recording;
            this.identifier = String.valueOf(Type.getTypeId(clazz));
        }
        
        @Override
        public EventSettings with(final String s, final String s2) {
            Objects.requireNonNull(s2);
            this.recording.setSetting(this.identifier + "#" + s, s2);
            return this;
        }
        
        public Map<String, String> toMap() {
            return this.recording.getSettings();
        }
    }
}
