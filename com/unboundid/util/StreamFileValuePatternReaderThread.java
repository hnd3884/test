package com.unboundid.util;

import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;
import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;

final class StreamFileValuePatternReaderThread extends Thread
{
    private final AtomicLong nextLineNumber;
    private final AtomicReference<BufferedReader> fileReader;
    private final AtomicReference<StreamFileValuePatternReaderThread> threadRef;
    private final File file;
    private final LinkedBlockingQueue<String> lineQueue;
    private final long maxOfferBlockTimeMillis;
    
    StreamFileValuePatternReaderThread(final File file, final LinkedBlockingQueue<String> lineQueue, final long maxOfferBlockTimeMillis, final AtomicLong nextLineNumber, final AtomicReference<StreamFileValuePatternReaderThread> threadRef) throws IOException {
        this.setName("StreamFileValuePatternReaderThread for file '" + file.getAbsolutePath() + '\'');
        this.setDaemon(true);
        this.file = file;
        this.lineQueue = lineQueue;
        this.maxOfferBlockTimeMillis = maxOfferBlockTimeMillis;
        this.nextLineNumber = nextLineNumber;
        this.threadRef = threadRef;
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        this.fileReader = new AtomicReference<BufferedReader>(bufferedReader);
        for (long linesToSkip = nextLineNumber.get(), i = 0L; i < linesToSkip && bufferedReader.readLine() != null; ++i) {}
    }
    
    @Override
    public void run() {
        BufferedReader bufferedReader = this.fileReader.get();
        try {
            while (true) {
                while (true) {
                    String line;
                    try {
                        while (true) {
                            line = bufferedReader.readLine();
                            if (line != null) {
                                break;
                            }
                            this.nextLineNumber.set(0L);
                            bufferedReader.close();
                            bufferedReader = new BufferedReader(new FileReader(this.file));
                            this.fileReader.set(bufferedReader);
                        }
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        this.nextLineNumber.set(0L);
                        return;
                    }
                    Label_0278: {
                        try {
                            if (this.lineQueue.offer(line, this.maxOfferBlockTimeMillis, TimeUnit.MILLISECONDS)) {
                                this.nextLineNumber.incrementAndGet();
                                break Label_0278;
                            }
                            return;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            return;
                        }
                    }
                    continue;
                }
            }
        }
        finally {
            this.threadRef.set(null);
            try {
                bufferedReader.close();
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.fileReader.set(null);
            }
            finally {
                this.fileReader.set(null);
            }
        }
    }
}
