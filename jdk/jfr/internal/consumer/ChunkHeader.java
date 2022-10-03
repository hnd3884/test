package jdk.jfr.internal.consumer;

import jdk.jfr.internal.MetadataDescriptor;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import java.io.DataInput;
import java.io.IOException;

public final class ChunkHeader
{
    private static final long METADATA_TYPE_ID = 0L;
    private static final byte[] FILE_MAGIC;
    private final short major;
    private final short minor;
    private final long chunkSize;
    private final long chunkStartTicks;
    private final long ticksPerSecond;
    private final long chunkStartNanos;
    private final long metadataPosition;
    private final long absoluteChunkEnd;
    private final long absoluteEventStart;
    private final long absoluteChunkStart;
    private final boolean lastChunk;
    private final RecordingInput input;
    private final long durationNanos;
    private final long id;
    private long constantPoolPosition;
    
    public ChunkHeader(final RecordingInput recordingInput) throws IOException {
        this(recordingInput, 0L, 0L);
    }
    
    private ChunkHeader(final RecordingInput input, final long absoluteChunkStart, final long id) throws IOException {
        input.position(absoluteChunkStart);
        if (input.position() >= input.size()) {
            throw new IOException("Chunk contains no data");
        }
        verifyMagic(input);
        this.input = input;
        this.id = id;
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk " + id);
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: startPosition=" + absoluteChunkStart);
        this.major = input.readRawShort();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: major=" + this.major);
        this.minor = input.readRawShort();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: minor=" + this.minor);
        if (this.major != 1 && this.major != 2) {
            throw new IOException("File version " + this.major + "." + this.minor + ". Only Flight Recorder files of version 1.x and 2.x can be read by this JDK.");
        }
        this.chunkSize = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: chunkSize=" + this.chunkSize);
        this.constantPoolPosition = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: constantPoolPosition=" + this.constantPoolPosition);
        this.metadataPosition = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: metadataPosition=" + this.metadataPosition);
        this.chunkStartNanos = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: startNanos=" + this.chunkStartNanos);
        this.durationNanos = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: durationNanos=" + this.durationNanos);
        this.chunkStartTicks = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: startTicks=" + this.chunkStartTicks);
        this.ticksPerSecond = input.readRawLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Chunk: ticksPerSecond=" + this.ticksPerSecond);
        input.readRawInt();
        this.absoluteChunkStart = absoluteChunkStart;
        this.absoluteChunkEnd = absoluteChunkStart + this.chunkSize;
        this.lastChunk = (input.size() == this.absoluteChunkEnd);
        input.position(this.absoluteEventStart = input.position());
    }
    
    public ChunkHeader nextHeader() throws IOException {
        return new ChunkHeader(this.input, this.absoluteChunkEnd, this.id + 1L);
    }
    
    public MetadataDescriptor readMetadata() throws IOException {
        this.input.position(this.absoluteChunkStart + this.metadataPosition);
        this.input.readInt();
        final long long1 = this.input.readLong();
        if (long1 != 0L) {
            throw new IOException("Expected metadata event. Type id=" + long1 + ", should have been " + 0L);
        }
        this.input.readLong();
        this.input.readLong();
        Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.TRACE, "Metadata id=" + this.input.readLong());
        return MetadataDescriptor.read(this.input);
    }
    
    public boolean isLastChunk() {
        return this.lastChunk;
    }
    
    public short getMajor() {
        return this.major;
    }
    
    public short getMinor() {
        return this.minor;
    }
    
    public long getAbsoluteChunkStart() {
        return this.absoluteChunkStart;
    }
    
    public long getConstantPoolPosition() {
        return this.constantPoolPosition;
    }
    
    public long getStartTicks() {
        return this.chunkStartTicks;
    }
    
    public double getTicksPerSecond() {
        return (double)this.ticksPerSecond;
    }
    
    public long getStartNanos() {
        return this.chunkStartNanos;
    }
    
    public long getEnd() {
        return this.absoluteChunkEnd;
    }
    
    public long getSize() {
        return this.chunkSize;
    }
    
    public long getDurationNanos() {
        return this.durationNanos;
    }
    
    public RecordingInput getInput() {
        return this.input;
    }
    
    private static void verifyMagic(final DataInput dataInput) throws IOException {
        final byte[] file_MAGIC = ChunkHeader.FILE_MAGIC;
        for (int length = file_MAGIC.length, i = 0; i < length; ++i) {
            if (dataInput.readByte() != file_MAGIC[i]) {
                throw new IOException("Not a Flight Recorder file");
            }
        }
    }
    
    public long getEventStart() {
        return this.absoluteEventStart;
    }
    
    static {
        FILE_MAGIC = new byte[] { 70, 76, 82, 0 };
    }
}
