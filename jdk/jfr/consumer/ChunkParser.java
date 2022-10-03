package jdk.jfr.consumer;

import jdk.jfr.EventType;
import java.util.List;
import java.util.Collection;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import java.io.IOException;
import jdk.jfr.internal.Type;
import jdk.jfr.internal.MetadataDescriptor;
import jdk.jfr.internal.consumer.ChunkHeader;
import jdk.jfr.internal.consumer.RecordingInput;

final class ChunkParser
{
    private static final long CONSTANT_POOL_TYPE_ID = 1L;
    private final RecordingInput input;
    private final LongMap<Parser> parsers;
    private final ChunkHeader chunkHeader;
    private final long absoluteChunkEnd;
    private final MetadataDescriptor metadata;
    private final LongMap<Type> typeMap;
    private final TimeConverter timeConverter;
    
    public ChunkParser(final RecordingInput recordingInput) throws IOException {
        this(new ChunkHeader(recordingInput));
    }
    
    private ChunkParser(final ChunkHeader chunkHeader) throws IOException {
        this.input = chunkHeader.getInput();
        this.chunkHeader = chunkHeader;
        this.metadata = chunkHeader.readMetadata();
        this.absoluteChunkEnd = chunkHeader.getEnd();
        this.timeConverter = new TimeConverter(this.chunkHeader, this.metadata.getGMTOffset());
        final ParserFactory parserFactory = new ParserFactory(this.metadata, this.timeConverter);
        final LongMap<ConstantMap> constantPools = parserFactory.getConstantPools();
        this.parsers = parserFactory.getParsers();
        this.typeMap = parserFactory.getTypeMap();
        this.fillConstantPools(this.parsers, constantPools);
        constantPools.forEach(ConstantMap::setIsResolving);
        constantPools.forEach(ConstantMap::resolve);
        constantPools.forEach(ConstantMap::setResolved);
        this.input.position(this.chunkHeader.getEventStart());
    }
    
    public RecordedEvent readEvent() throws IOException {
        while (this.input.position() < this.absoluteChunkEnd) {
            final long position = this.input.position();
            final int int1 = this.input.readInt();
            if (int1 == 0) {
                throw new IOException("Event can't have zero size");
            }
            final long long1 = this.input.readLong();
            if (long1 > 1L) {
                final Parser parser = this.parsers.get(long1);
                if (parser instanceof EventParser) {
                    return (RecordedEvent)parser.parse(this.input);
                }
            }
            this.input.position(position + int1);
        }
        return null;
    }
    
    private void fillConstantPools(final LongMap<Parser> longMap, final LongMap<ConstantMap> longMap2) throws IOException {
        long absoluteChunkStart = this.chunkHeader.getAbsoluteChunkStart();
        long constantPoolPosition = this.chunkHeader.getConstantPoolPosition();
        while (constantPoolPosition != 0L) {
            absoluteChunkStart += constantPoolPosition;
            this.input.position(absoluteChunkStart);
            final int int1 = this.input.readInt();
            final long long1 = this.input.readLong();
            if (long1 != 1L) {
                throw new IOException("Expected check point event (id = 1) at position " + absoluteChunkStart + ", but found type id = " + long1);
            }
            this.input.readLong();
            this.input.readLong();
            final long long2;
            constantPoolPosition = (long2 = this.input.readLong());
            this.input.readBoolean();
            final int int2 = this.input.readInt();
            Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.TRACE, () -> "New constant pool: startPosition=" + n + ", size=" + n2 + ", deltaToNext=" + n3 + ", flush=" + b + ", poolCount=" + n4);
            for (int i = 0; i < int2; ++i) {
                final long long3 = this.input.readLong();
                ConstantMap constantMap = longMap2.get(long3);
                final Type type = this.typeMap.get(long3);
                if (constantMap == null) {
                    Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.INFO, "Found constant pool(" + long3 + ") that is never used");
                    if (type == null) {
                        throw new IOException("Error parsing constant pool type " + this.getName(long3) + " at position " + this.input.position() + " at check point between [" + absoluteChunkStart + ", " + absoluteChunkStart + int1 + "]");
                    }
                    constantMap = new ConstantMap(ObjectFactory.create(type, this.timeConverter), type.getName());
                    longMap2.put(type.getId(), constantMap);
                }
                final Parser parser = longMap.get(long3);
                if (parser == null) {
                    throw new IOException("Could not find constant pool type with id = " + long3);
                }
                try {
                    final int int3 = this.input.readInt();
                    Logger.log(LogTag.JFR_SYSTEM_PARSER, LogLevel.TRACE, () -> "Constant: " + this.getName(n5) + "[" + n6 + "]");
                    for (int j = 0; j < int3; ++j) {
                        constantMap.put(this.input.readLong(), parser.parse(this.input));
                    }
                }
                catch (final Exception ex) {
                    throw new IOException("Error parsing constant pool type " + this.getName(long3) + " at position " + this.input.position() + " at check point between [" + absoluteChunkStart + ", " + absoluteChunkStart + int1 + "]", ex);
                }
            }
            if (this.input.position() != absoluteChunkStart + int1) {
                throw new IOException("Size of check point event doesn't match content");
            }
        }
    }
    
    private String getName(final long n) {
        final Type type = this.typeMap.get(n);
        return (type == null) ? ("unknown(" + n + ")") : type.getName();
    }
    
    public Collection<Type> getTypes() {
        return this.metadata.getTypes();
    }
    
    public List<EventType> getEventTypes() {
        return this.metadata.getEventTypes();
    }
    
    public boolean isLastChunk() {
        return this.chunkHeader.isLastChunk();
    }
    
    public ChunkParser nextChunkParser() throws IOException {
        return new ChunkParser(this.chunkHeader.nextHeader());
    }
}
