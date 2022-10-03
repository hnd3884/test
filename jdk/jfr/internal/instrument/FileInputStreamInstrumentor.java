package jdk.jfr.internal.instrument;

import java.io.IOException;
import jdk.jfr.events.FileReadEvent;

@JIInstrumentationTarget("java.io.FileInputStream")
final class FileInputStreamInstrumentor
{
    private String path;
    
    private FileInputStreamInstrumentor() {
    }
    
    @JIInstrumentationMethod
    public int read() throws IOException {
        final FileReadEvent fileReadEvent = FileReadEvent.EVENT.get();
        if (!fileReadEvent.isEnabled()) {
            return this.read();
        }
        int read = 0;
        try {
            fileReadEvent.begin();
            read = this.read();
            if (read < 0) {
                fileReadEvent.endOfFile = true;
            }
            else {
                fileReadEvent.bytesRead = 1L;
            }
        }
        finally {
            fileReadEvent.path = this.path;
            fileReadEvent.commit();
            fileReadEvent.reset();
        }
        return read;
    }
    
    @JIInstrumentationMethod
    public int read(final byte[] array) throws IOException {
        final FileReadEvent fileReadEvent = FileReadEvent.EVENT.get();
        if (!fileReadEvent.isEnabled()) {
            return this.read(array);
        }
        int read = 0;
        try {
            fileReadEvent.begin();
            read = this.read(array);
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
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final FileReadEvent fileReadEvent = FileReadEvent.EVENT.get();
        if (!fileReadEvent.isEnabled()) {
            return this.read(array, n, n2);
        }
        int read = 0;
        try {
            fileReadEvent.begin();
            read = this.read(array, n, n2);
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
}
