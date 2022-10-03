package jdk.jfr.internal.instrument;

import jdk.jfr.events.FileWriteEvent;
import java.io.IOException;
import jdk.jfr.events.FileReadEvent;

@JIInstrumentationTarget("java.io.RandomAccessFile")
final class RandomAccessFileInstrumentor
{
    private String path;
    
    private RandomAccessFileInstrumentor() {
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
    
    @JIInstrumentationMethod
    public void write(final int n) throws IOException {
        final FileWriteEvent fileWriteEvent = FileWriteEvent.EVENT.get();
        if (!fileWriteEvent.isEnabled()) {
            this.write(n);
            return;
        }
        try {
            fileWriteEvent.begin();
            this.write(n);
            fileWriteEvent.bytesWritten = 1L;
        }
        finally {
            fileWriteEvent.path = this.path;
            fileWriteEvent.commit();
            fileWriteEvent.reset();
        }
    }
    
    @JIInstrumentationMethod
    public void write(final byte[] array) throws IOException {
        final FileWriteEvent fileWriteEvent = FileWriteEvent.EVENT.get();
        if (!fileWriteEvent.isEnabled()) {
            this.write(array);
            return;
        }
        try {
            fileWriteEvent.begin();
            this.write(array);
            fileWriteEvent.bytesWritten = array.length;
        }
        finally {
            fileWriteEvent.path = this.path;
            fileWriteEvent.commit();
            fileWriteEvent.reset();
        }
    }
    
    @JIInstrumentationMethod
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        final FileWriteEvent fileWriteEvent = FileWriteEvent.EVENT.get();
        if (!fileWriteEvent.isEnabled()) {
            this.write(array, n, n2);
            return;
        }
        try {
            fileWriteEvent.begin();
            this.write(array, n, n2);
            fileWriteEvent.bytesWritten = n2;
        }
        finally {
            fileWriteEvent.path = this.path;
            fileWriteEvent.commit();
            fileWriteEvent.reset();
        }
    }
}
