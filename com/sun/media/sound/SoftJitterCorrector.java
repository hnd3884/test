package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;

public final class SoftJitterCorrector extends AudioInputStream
{
    public SoftJitterCorrector(final AudioInputStream audioInputStream, final int n, final int n2) {
        super(new JitterStream(audioInputStream, n, n2), audioInputStream.getFormat(), audioInputStream.getFrameLength());
    }
    
    private static class JitterStream extends InputStream
    {
        static int MAX_BUFFER_SIZE;
        boolean active;
        Thread thread;
        AudioInputStream stream;
        int writepos;
        int readpos;
        byte[][] buffers;
        private final Object buffers_mutex;
        int w_count;
        int w_min_tol;
        int w_max_tol;
        int w;
        int w_min;
        int bbuffer_pos;
        int bbuffer_max;
        byte[] bbuffer;
        
        public byte[] nextReadBuffer() {
            synchronized (this.buffers_mutex) {
                if (this.writepos > this.readpos) {
                    final int w_min = this.writepos - this.readpos;
                    if (w_min < this.w_min) {
                        this.w_min = w_min;
                    }
                    final int readpos = this.readpos;
                    ++this.readpos;
                    return this.buffers[readpos % this.buffers.length];
                }
                this.w_min = -1;
                this.w = this.w_count - 1;
            }
            while (true) {
                try {
                    Thread.sleep(1L);
                }
                catch (final InterruptedException ex) {
                    return null;
                }
                synchronized (this.buffers_mutex) {
                    if (this.writepos > this.readpos) {
                        this.w = 0;
                        this.w_min = -1;
                        this.w = this.w_count - 1;
                        final int readpos2 = this.readpos;
                        ++this.readpos;
                        return this.buffers[readpos2 % this.buffers.length];
                    }
                    continue;
                }
            }
        }
        
        public byte[] nextWriteBuffer() {
            synchronized (this.buffers_mutex) {
                return this.buffers[this.writepos % this.buffers.length];
            }
        }
        
        public void commit() {
            synchronized (this.buffers_mutex) {
                ++this.writepos;
                if (this.writepos - this.readpos > this.buffers.length) {
                    this.buffers = new byte[Math.max(this.buffers.length * 2, this.writepos - this.readpos + 10)][this.buffers[0].length];
                }
            }
        }
        
        JitterStream(final AudioInputStream stream, final int n, final int n2) {
            this.active = true;
            this.writepos = 0;
            this.readpos = 0;
            this.buffers_mutex = new Object();
            this.w_count = 1000;
            this.w_min_tol = 2;
            this.w_max_tol = 10;
            this.w = 0;
            this.w_min = -1;
            this.bbuffer_pos = 0;
            this.bbuffer_max = 0;
            this.bbuffer = null;
            this.w_count = 10 * (n / n2);
            if (this.w_count < 100) {
                this.w_count = 100;
            }
            this.buffers = new byte[n / n2 + 10][n2];
            this.bbuffer_max = JitterStream.MAX_BUFFER_SIZE / n2;
            this.stream = stream;
            (this.thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final AudioFormat format = JitterStream.this.stream.getFormat();
                    final long n = (long)(JitterStream.this.buffers[0].length / format.getFrameSize() * 1.0E9 / format.getSampleRate());
                    long n2 = System.nanoTime() + n;
                    int n3 = 0;
                    while (true) {
                        synchronized (JitterStream.this) {
                            if (!JitterStream.this.active) {
                                break;
                            }
                        }
                        int i;
                        synchronized (JitterStream.this.buffers) {
                            i = JitterStream.this.writepos - JitterStream.this.readpos;
                            if (n3 == 0) {
                                final JitterStream this$0 = JitterStream.this;
                                ++this$0.w;
                                if (JitterStream.this.w_min != Integer.MAX_VALUE && JitterStream.this.w == JitterStream.this.w_count) {
                                    n3 = 0;
                                    if (JitterStream.this.w_min < JitterStream.this.w_min_tol) {
                                        n3 = (JitterStream.this.w_min_tol + JitterStream.this.w_max_tol) / 2 - JitterStream.this.w_min;
                                    }
                                    if (JitterStream.this.w_min > JitterStream.this.w_max_tol) {
                                        n3 = (JitterStream.this.w_min_tol + JitterStream.this.w_max_tol) / 2 - JitterStream.this.w_min;
                                    }
                                    JitterStream.this.w = 0;
                                    JitterStream.this.w_min = Integer.MAX_VALUE;
                                }
                            }
                        }
                        while (i > JitterStream.this.bbuffer_max) {
                            synchronized (JitterStream.this.buffers) {
                                i = JitterStream.this.writepos - JitterStream.this.readpos;
                            }
                            synchronized (JitterStream.this) {
                                if (!JitterStream.this.active) {
                                    break;
                                }
                            }
                            try {
                                Thread.sleep(1L);
                            }
                            catch (final InterruptedException ex) {}
                        }
                        if (n3 < 0) {
                            ++n3;
                        }
                        else {
                            final byte[] nextWriteBuffer = JitterStream.this.nextWriteBuffer();
                            try {
                                int read;
                                for (int j = 0; j != nextWriteBuffer.length; j += read) {
                                    read = JitterStream.this.stream.read(nextWriteBuffer, j, nextWriteBuffer.length - j);
                                    if (read < 0) {
                                        throw new EOFException();
                                    }
                                    if (read == 0) {
                                        Thread.yield();
                                    }
                                }
                            }
                            catch (final IOException ex2) {}
                            JitterStream.this.commit();
                        }
                        if (n3 > 0) {
                            --n3;
                            n2 = System.nanoTime() + n;
                        }
                        else {
                            final long n4 = n2 - System.nanoTime();
                            if (n4 > 0L) {
                                try {
                                    Thread.sleep(n4 / 1000000L);
                                }
                                catch (final InterruptedException ex3) {}
                            }
                            n2 += n;
                        }
                    }
                }
            })).setDaemon(true);
            this.thread.setPriority(10);
            this.thread.start();
        }
        
        @Override
        public void close() throws IOException {
            synchronized (this) {
                this.active = false;
            }
            try {
                this.thread.join();
            }
            catch (final InterruptedException ex) {}
            this.stream.close();
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            if (this.read(array) == -1) {
                return -1;
            }
            return array[0] & 0xFF;
        }
        
        public void fillBuffer() {
            this.bbuffer = this.nextReadBuffer();
            this.bbuffer_pos = 0;
        }
        
        @Override
        public int read(final byte[] array, int i, final int n) {
            if (this.bbuffer == null) {
                this.fillBuffer();
            }
            final int length = this.bbuffer.length;
            final int n2 = i + n;
            while (i < n2) {
                if (this.available() == 0) {
                    this.fillBuffer();
                }
                else {
                    byte[] bbuffer;
                    int bbuffer_pos;
                    for (bbuffer = this.bbuffer, bbuffer_pos = this.bbuffer_pos; i < n2 && bbuffer_pos < length; array[i++] = bbuffer[bbuffer_pos++]) {}
                    this.bbuffer_pos = bbuffer_pos;
                }
            }
            return n;
        }
        
        @Override
        public int available() {
            return this.bbuffer.length - this.bbuffer_pos;
        }
        
        static {
            JitterStream.MAX_BUFFER_SIZE = 1048576;
        }
    }
}
