package jdk.jfr.internal.instrument;

import jdk.jfr.events.FileWriteEvent;
import jdk.jfr.events.FileReadEvent;
import java.nio.ByteBuffer;
import java.io.IOException;
import jdk.jfr.events.FileForceEvent;

@JIInstrumentationTarget("sun.nio.ch.FileChannelImpl")
final class FileChannelImplInstrumentor
{
    private String path;
    
    private FileChannelImplInstrumentor() {
    }
    
    @JIInstrumentationMethod
    public void force(final boolean metaData) throws IOException {
        final FileForceEvent fileForceEvent = FileForceEvent.EVENT.get();
        if (!fileForceEvent.isEnabled()) {
            this.force(metaData);
            return;
        }
        try {
            fileForceEvent.begin();
            this.force(metaData);
        }
        finally {
            fileForceEvent.path = this.path;
            fileForceEvent.metaData = metaData;
            fileForceEvent.commit();
            fileForceEvent.reset();
        }
    }
    
    @JIInstrumentationMethod
    public int read(final ByteBuffer byteBuffer) throws IOException {
        final FileReadEvent fileReadEvent = FileReadEvent.EVENT.get();
        if (!fileReadEvent.isEnabled()) {
            return this.read(byteBuffer);
        }
        int read = 0;
        try {
            fileReadEvent.begin();
            read = this.read(byteBuffer);
        }
        finally {
            if (read < 0) {
                fileReadEvent.endOfFile = true;
            }
            else {
                fileReadEvent.bytesRead = read;
            }
            fileReadEvent.path = this.path;
            fileReadEvent.commit();
            fileReadEvent.reset();
        }
        return read;
    }
    
    @JIInstrumentationMethod
    public int read(final ByteBuffer byteBuffer, final long n) throws IOException {
        final FileReadEvent fileReadEvent = FileReadEvent.EVENT.get();
        if (!fileReadEvent.isEnabled()) {
            return this.read(byteBuffer, n);
        }
        int read = 0;
        try {
            fileReadEvent.begin();
            read = this.read(byteBuffer, n);
        }
        finally {
            if (read < 0) {
                fileReadEvent.endOfFile = true;
            }
            else {
                fileReadEvent.bytesRead = read;
            }
            fileReadEvent.path = this.path;
            fileReadEvent.commit();
            fileReadEvent.reset();
        }
        return read;
    }
    
    @JIInstrumentationMethod
    public long read(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        final FileReadEvent fileReadEvent = FileReadEvent.EVENT.get();
        if (!fileReadEvent.isEnabled()) {
            return this.read(array, n, n2);
        }
        long read = 0L;
        try {
            fileReadEvent.begin();
            read = this.read(array, n, n2);
        }
        finally {
            if (read < 0L) {
                fileReadEvent.endOfFile = true;
            }
            else {
                fileReadEvent.bytesRead = read;
            }
            fileReadEvent.path = this.path;
            fileReadEvent.commit();
            fileReadEvent.reset();
        }
        return read;
    }
    
    @JIInstrumentationMethod
    public int write(final ByteBuffer byteBuffer) throws IOException {
        final FileWriteEvent fileWriteEvent = FileWriteEvent.EVENT.get();
        if (!fileWriteEvent.isEnabled()) {
            return this.write(byteBuffer);
        }
        int write = 0;
        try {
            fileWriteEvent.begin();
            write = this.write(byteBuffer);
        }
        finally {
            fileWriteEvent.bytesWritten = ((write > 0) ? write : 0L);
            fileWriteEvent.path = this.path;
            fileWriteEvent.commit();
            fileWriteEvent.reset();
        }
        return write;
    }
    
    @JIInstrumentationMethod
    public int write(final ByteBuffer byteBuffer, final long n) throws IOException {
        final FileWriteEvent fileWriteEvent = FileWriteEvent.EVENT.get();
        if (!fileWriteEvent.isEnabled()) {
            return this.write(byteBuffer, n);
        }
        int write = 0;
        try {
            fileWriteEvent.begin();
            write = this.write(byteBuffer, n);
        }
        finally {
            fileWriteEvent.bytesWritten = ((write > 0) ? write : 0L);
            fileWriteEvent.path = this.path;
            fileWriteEvent.commit();
            fileWriteEvent.reset();
        }
        return write;
    }
    
    @JIInstrumentationMethod
    public long write(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        final FileWriteEvent fileWriteEvent = FileWriteEvent.EVENT.get();
        if (!fileWriteEvent.isEnabled()) {
            return this.write(array, n, n2);
        }
        long write = 0L;
        try {
            fileWriteEvent.begin();
            write = this.write(array, n, n2);
        }
        finally {
            fileWriteEvent.bytesWritten = ((write > 0L) ? write : 0L);
            fileWriteEvent.path = this.path;
            fileWriteEvent.commit();
            fileWriteEvent.reset();
        }
        return write;
    }
}
