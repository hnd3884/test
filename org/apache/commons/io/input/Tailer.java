package org.apache.commons.io.input;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.io.Closeable;
import org.apache.commons.io.IOUtils;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.File;

public class Tailer implements Runnable
{
    private final File file;
    private final long delay;
    private final boolean end;
    private final TailerListener listener;
    private volatile boolean run;
    
    public Tailer(final File file, final TailerListener listener) {
        this(file, listener, 1000L);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delay) {
        this(file, listener, delay, false);
    }
    
    public Tailer(final File file, final TailerListener listener, final long delay, final boolean end) {
        this.run = true;
        this.file = file;
        this.delay = delay;
        this.end = end;
        (this.listener = listener).init(this);
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delay, final boolean end) {
        final Tailer tailer = new Tailer(file, listener, delay, end);
        final Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }
    
    public static Tailer create(final File file, final TailerListener listener, final long delay) {
        return create(file, listener, delay, false);
    }
    
    public static Tailer create(final File file, final TailerListener listener) {
        return create(file, listener, 1000L, false);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    @Override
    public void run() {
        RandomAccessFile reader = null;
        try {
            long last = 0L;
            long position = 0L;
            while (this.run && reader == null) {
                try {
                    reader = new RandomAccessFile(this.file, "r");
                }
                catch (final FileNotFoundException e) {
                    this.listener.fileNotFound();
                }
                if (reader == null) {
                    try {
                        Thread.sleep(this.delay);
                    }
                    catch (final InterruptedException e2) {}
                }
                else {
                    position = (this.end ? this.file.length() : 0L);
                    last = System.currentTimeMillis();
                    reader.seek(position);
                }
            }
            while (this.run) {
                final long length = this.file.length();
                if (length < position) {
                    this.listener.fileRotated();
                    try {
                        final RandomAccessFile save = reader;
                        reader = new RandomAccessFile(this.file, "r");
                        position = 0L;
                        IOUtils.closeQuietly(save);
                    }
                    catch (final FileNotFoundException e3) {
                        this.listener.fileNotFound();
                    }
                }
                else {
                    if (length > position) {
                        last = System.currentTimeMillis();
                        position = this.readLines(reader);
                    }
                    else if (FileUtils.isFileNewer(this.file, last)) {
                        position = 0L;
                        reader.seek(position);
                        last = System.currentTimeMillis();
                        position = this.readLines(reader);
                    }
                    try {
                        Thread.sleep(this.delay);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        }
        catch (final Exception e4) {
            this.listener.handle(e4);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }
    
    public void stop() {
        this.run = false;
    }
    
    private long readLines(final RandomAccessFile reader) throws IOException {
        long pos = reader.getFilePointer();
        for (String line = this.readLine(reader); line != null; line = this.readLine(reader)) {
            pos = reader.getFilePointer();
            this.listener.handle(line);
        }
        reader.seek(pos);
        return pos;
    }
    
    private String readLine(final RandomAccessFile reader) throws IOException {
        final StringBuffer sb = new StringBuffer();
        boolean seenCR = false;
        int ch;
        while ((ch = reader.read()) != -1) {
            switch (ch) {
                case 10: {
                    return sb.toString();
                }
                case 13: {
                    seenCR = true;
                    continue;
                }
                default: {
                    if (seenCR) {
                        sb.append('\r');
                        seenCR = false;
                    }
                    sb.append((char)ch);
                    continue;
                }
            }
        }
        return null;
    }
}
