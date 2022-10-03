package jdk.jfr.internal.instrument;

import java.io.IOException;
import jdk.jfr.events.FileWriteEvent;

@JIInstrumentationTarget("java.io.FileOutputStream")
final class FileOutputStreamInstrumentor
{
    private String path;
    
    private FileOutputStreamInstrumentor() {
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
