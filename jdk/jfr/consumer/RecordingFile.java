package jdk.jfr.consumer;

import java.util.Collections;
import jdk.jfr.internal.consumer.RecordingInternals;
import java.util.Iterator;
import jdk.jfr.internal.Type;
import jdk.jfr.internal.consumer.ChunkHeader;
import java.util.HashSet;
import java.util.ArrayList;
import jdk.jfr.EventType;
import java.util.List;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;
import jdk.jfr.internal.consumer.RecordingInput;
import java.io.File;
import java.io.Closeable;

public final class RecordingFile implements Closeable
{
    private boolean isLastEventInChunk;
    private final File file;
    private RecordingInput input;
    private ChunkParser chunkParser;
    private RecordedEvent nextEvent;
    private boolean eof;
    
    public RecordingFile(final Path path) throws IOException {
        this.file = path.toFile();
        this.input = new RecordingInput(this.file);
        this.findNext();
    }
    
    public RecordedEvent readEvent() throws IOException {
        if (this.eof) {
            this.ensureOpen();
            throw new EOFException();
        }
        this.isLastEventInChunk = false;
        final RecordedEvent nextEvent = this.nextEvent;
        this.nextEvent = this.chunkParser.readEvent();
        if (this.nextEvent == null) {
            this.isLastEventInChunk = true;
            this.findNext();
        }
        return nextEvent;
    }
    
    public boolean hasMoreEvents() {
        return !this.eof;
    }
    
    public List<EventType> readEventTypes() throws IOException {
        this.ensureOpen();
        final ArrayList list = new ArrayList();
        final HashSet set = new HashSet();
        try (final RecordingInput recordingInput = new RecordingInput(this.file)) {
            ChunkHeader nextHeader = new ChunkHeader(recordingInput);
            aggregateEventTypeForChunk(nextHeader, list, set);
            while (!nextHeader.isLastChunk()) {
                nextHeader = nextHeader.nextHeader();
                aggregateEventTypeForChunk(nextHeader, list, set);
            }
        }
        return list;
    }
    
    List<Type> readTypes() throws IOException {
        this.ensureOpen();
        final ArrayList list = new ArrayList();
        final HashSet set = new HashSet();
        try (final RecordingInput recordingInput = new RecordingInput(this.file)) {
            ChunkHeader nextHeader = new ChunkHeader(recordingInput);
            this.aggregateTypeForChunk(nextHeader, list, set);
            while (!nextHeader.isLastChunk()) {
                nextHeader = nextHeader.nextHeader();
                this.aggregateTypeForChunk(nextHeader, list, set);
            }
        }
        return list;
    }
    
    private void aggregateTypeForChunk(final ChunkHeader chunkHeader, final List<Type> list, final HashSet<Long> set) throws IOException {
        for (final Type type : chunkHeader.readMetadata().getTypes()) {
            if (!set.contains(type.getId())) {
                list.add(type);
                set.add(type.getId());
            }
        }
    }
    
    private static void aggregateEventTypeForChunk(final ChunkHeader chunkHeader, final List<EventType> list, final HashSet<Long> set) throws IOException {
        for (final EventType eventType : chunkHeader.readMetadata().getEventTypes()) {
            if (!set.contains(eventType.getId())) {
                list.add(eventType);
                set.add(eventType.getId());
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.input != null) {
            this.eof = true;
            this.input.close();
            this.chunkParser = null;
            this.input = null;
            this.nextEvent = null;
        }
    }
    
    public static List<RecordedEvent> readAllEvents(final Path path) throws IOException {
        try (final RecordingFile recordingFile = new RecordingFile(path)) {
            final ArrayList list = new ArrayList();
            while (recordingFile.hasMoreEvents()) {
                list.add(recordingFile.readEvent());
            }
            return list;
        }
    }
    
    private void findNext() throws IOException {
        while (this.nextEvent == null) {
            if (this.chunkParser == null) {
                this.chunkParser = new ChunkParser(this.input);
            }
            else {
                if (this.chunkParser.isLastChunk()) {
                    this.eof = true;
                    return;
                }
                this.chunkParser = this.chunkParser.nextChunkParser();
            }
            this.nextEvent = this.chunkParser.readEvent();
        }
    }
    
    private void ensureOpen() throws IOException {
        if (this.input == null) {
            throw new IOException("Stream Closed");
        }
    }
    
    static {
        RecordingInternals.INSTANCE = new RecordingInternals() {
            @Override
            public List<Type> readTypes(final RecordingFile recordingFile) throws IOException {
                return recordingFile.readTypes();
            }
            
            @Override
            public boolean isLastEventInChunk(final RecordingFile recordingFile) {
                return recordingFile.isLastEventInChunk;
            }
            
            @Override
            public Object getOffsetDataTime(final RecordedObject recordedObject, final String s) {
                return recordedObject.getOffsetDateTime(s);
            }
            
            @Override
            public void sort(final List<RecordedEvent> list) {
                Collections.sort((List<Object>)list, (recordedEvent, recordedEvent2) -> Long.compare(recordedEvent.endTime, recordedEvent2.endTime));
            }
        };
    }
}
