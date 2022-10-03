package jdk.jfr.internal;

import java.nio.channels.ReadableByteChannel;
import java.util.Objects;
import java.io.IOException;
import java.nio.file.Path;
import java.time.temporal.TemporalAccessor;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.util.Comparator;

final class RepositoryChunk
{
    private static final int MAX_CHUNK_NAMES = 100;
    static final Comparator<RepositoryChunk> END_TIME_COMPARATOR;
    private final SecuritySupport.SafePath repositoryPath;
    private final SecuritySupport.SafePath unFinishedFile;
    private final SecuritySupport.SafePath file;
    private final Instant startTime;
    private final RandomAccessFile unFinishedRAF;
    private Instant endTime;
    private int refCount;
    private long size;
    
    RepositoryChunk(final SecuritySupport.SafePath repositoryPath, final Instant startTime) throws Exception {
        this.endTime = null;
        this.refCount = 0;
        final String format = Repository.REPO_DATE_FORMAT.format(LocalDateTime.ofInstant(startTime, ZonedDateTime.now().getZone()));
        this.startTime = startTime;
        this.repositoryPath = repositoryPath;
        this.unFinishedFile = findFileName(this.repositoryPath, format, ".part");
        this.file = findFileName(this.repositoryPath, format, ".jfr");
        this.unFinishedRAF = SecuritySupport.createRandomAccessFile(this.unFinishedFile);
        SecuritySupport.touch(this.file);
    }
    
    private static SecuritySupport.SafePath findFileName(final SecuritySupport.SafePath safePath, final String s, final String s2) throws Exception {
        Path path = safePath.toPath().resolve(s + s2);
        for (int i = 1; i < 100; ++i) {
            final SecuritySupport.SafePath safePath2 = new SecuritySupport.SafePath(path);
            if (!SecuritySupport.exists(safePath2)) {
                return safePath2;
            }
            path = safePath.toPath().resolve(String.format("%s_%02d%s", s, i, s2));
        }
        return SecuritySupport.toRealPath(new SecuritySupport.SafePath(safePath.toPath().resolve(s + "_" + System.currentTimeMillis() + s2)));
    }
    
    public SecuritySupport.SafePath getUnfishedFile() {
        return this.unFinishedFile;
    }
    
    void finish(final Instant instant) {
        try {
            this.finishWithException(instant);
        }
        catch (final IOException ex) {
            Logger.log(LogTag.JFR, LogLevel.ERROR, "Could not finish chunk. " + ex.getMessage());
        }
    }
    
    private void finishWithException(final Instant endTime) throws IOException {
        this.unFinishedRAF.close();
        this.size = finish(this.unFinishedFile, this.file);
        this.endTime = endTime;
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.DEBUG, () -> "Chunk finished: " + this.file);
    }
    
    private static long finish(final SecuritySupport.SafePath safePath, final SecuritySupport.SafePath safePath2) throws IOException {
        Objects.requireNonNull(safePath);
        Objects.requireNonNull(safePath2);
        SecuritySupport.delete(safePath2);
        SecuritySupport.moveReplace(safePath, safePath2);
        return SecuritySupport.getFileSize(safePath2);
    }
    
    public Instant getStartTime() {
        return this.startTime;
    }
    
    public Instant getEndTime() {
        return this.endTime;
    }
    
    private void delete(final SecuritySupport.SafePath safePath) {
        try {
            SecuritySupport.delete(safePath);
            Logger.log(LogTag.JFR, LogLevel.DEBUG, () -> "Repository chunk " + safePath2 + " deleted");
        }
        catch (final IOException ex) {
            Logger.log(LogTag.JFR, LogLevel.ERROR, () -> "Repository chunk " + safePath3 + " could not be deleted: " + ex2.getMessage());
            if (safePath != null) {
                SecuritySupport.deleteOnExit(safePath);
            }
        }
    }
    
    private void destroy() {
        if (!this.isFinished()) {
            this.finish(Instant.MIN);
        }
        if (this.file != null) {
            this.delete(this.file);
        }
        try {
            this.unFinishedRAF.close();
        }
        catch (final IOException ex) {
            Logger.log(LogTag.JFR, LogLevel.ERROR, () -> "Could not close random access file: " + this.unFinishedFile.toString() + ". File will not be deleted due to: " + ex2.getMessage());
        }
    }
    
    public synchronized void use() {
        ++this.refCount;
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.DEBUG, () -> "Use chunk " + this.toString() + " ref count now " + this.refCount);
    }
    
    public synchronized void release() {
        --this.refCount;
        Logger.log(LogTag.JFR_SYSTEM, LogLevel.DEBUG, () -> "Release chunk " + this.toString() + " ref count now " + this.refCount);
        if (this.refCount == 0) {
            this.destroy();
        }
    }
    
    @Override
    protected void finalize() {
        boolean b = false;
        synchronized (this) {
            if (this.refCount > 0) {
                b = true;
            }
        }
        if (b) {
            this.destroy();
        }
    }
    
    public long getSize() {
        return this.size;
    }
    
    public boolean isFinished() {
        return this.endTime != null;
    }
    
    @Override
    public String toString() {
        if (this.isFinished()) {
            return this.file.toString();
        }
        return this.unFinishedFile.toString();
    }
    
    ReadableByteChannel newChannel() throws IOException {
        if (!this.isFinished()) {
            throw new IOException("Chunk not finished");
        }
        return SecuritySupport.newFileChannelToRead(this.file);
    }
    
    public boolean inInterval(final Instant instant, final Instant instant2) {
        return (instant == null || !this.getEndTime().isBefore(instant)) && (instant2 == null || !this.getStartTime().isAfter(instant2));
    }
    
    public SecuritySupport.SafePath getFile() {
        return this.file;
    }
    
    static {
        END_TIME_COMPARATOR = new Comparator<RepositoryChunk>() {
            @Override
            public int compare(final RepositoryChunk repositoryChunk, final RepositoryChunk repositoryChunk2) {
                return repositoryChunk.endTime.compareTo(repositoryChunk2.endTime);
            }
        };
    }
}
