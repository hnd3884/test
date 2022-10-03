package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioSystem;
import java.net.URL;

public abstract class AudioFloatInputStream
{
    public static AudioFloatInputStream getInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(url));
    }
    
    public static AudioFloatInputStream getInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(file));
    }
    
    public static AudioFloatInputStream getInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        return new DirectAudioFloatInputStream(AudioSystem.getAudioInputStream(inputStream));
    }
    
    public static AudioFloatInputStream getInputStream(final AudioInputStream audioInputStream) {
        return new DirectAudioFloatInputStream(audioInputStream);
    }
    
    public static AudioFloatInputStream getInputStream(final AudioFormat audioFormat, final byte[] array, final int n, final int n2) {
        final AudioFloatConverter converter = AudioFloatConverter.getConverter(audioFormat);
        if (converter != null) {
            return new BytaArrayAudioFloatInputStream(converter, array, n, n2);
        }
        return getInputStream(new AudioInputStream(new ByteArrayInputStream(array, n, n2), audioFormat, (audioFormat.getFrameSize() == -1) ? -1L : (n2 / audioFormat.getFrameSize())));
    }
    
    public abstract AudioFormat getFormat();
    
    public abstract long getFrameLength();
    
    public abstract int read(final float[] p0, final int p1, final int p2) throws IOException;
    
    public final int read(final float[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    public final float read() throws IOException {
        final float[] array = { 0.0f };
        final int read = this.read(array, 0, 1);
        if (read == -1 || read == 0) {
            return 0.0f;
        }
        return array[0];
    }
    
    public abstract long skip(final long p0) throws IOException;
    
    public abstract int available() throws IOException;
    
    public abstract void close() throws IOException;
    
    public abstract void mark(final int p0);
    
    public abstract boolean markSupported();
    
    public abstract void reset() throws IOException;
    
    private static class BytaArrayAudioFloatInputStream extends AudioFloatInputStream
    {
        private int pos;
        private int markpos;
        private final AudioFloatConverter converter;
        private final AudioFormat format;
        private final byte[] buffer;
        private final int buffer_offset;
        private final int buffer_len;
        private final int framesize_pc;
        
        BytaArrayAudioFloatInputStream(final AudioFloatConverter converter, final byte[] buffer, final int buffer_offset, final int n) {
            this.pos = 0;
            this.markpos = 0;
            this.converter = converter;
            this.format = converter.getFormat();
            this.buffer = buffer;
            this.buffer_offset = buffer_offset;
            this.framesize_pc = this.format.getFrameSize() / this.format.getChannels();
            this.buffer_len = n / this.framesize_pc;
        }
        
        @Override
        public AudioFormat getFormat() {
            return this.format;
        }
        
        @Override
        public long getFrameLength() {
            return this.buffer_len;
        }
        
        @Override
        public int read(final float[] array, final int n, int n2) throws IOException {
            if (array == null) {
                throw new NullPointerException();
            }
            if (n < 0 || n2 < 0 || n2 > array.length - n) {
                throw new IndexOutOfBoundsException();
            }
            if (this.pos >= this.buffer_len) {
                return -1;
            }
            if (n2 == 0) {
                return 0;
            }
            if (this.pos + n2 > this.buffer_len) {
                n2 = this.buffer_len - this.pos;
            }
            this.converter.toFloatArray(this.buffer, this.buffer_offset + this.pos * this.framesize_pc, array, n, n2);
            this.pos += n2;
            return n2;
        }
        
        @Override
        public long skip(long n) throws IOException {
            if (this.pos >= this.buffer_len) {
                return -1L;
            }
            if (n <= 0L) {
                return 0L;
            }
            if (this.pos + n > this.buffer_len) {
                n = this.buffer_len - this.pos;
            }
            this.pos += (int)n;
            return n;
        }
        
        @Override
        public int available() throws IOException {
            return this.buffer_len - this.pos;
        }
        
        @Override
        public void close() throws IOException {
        }
        
        @Override
        public void mark(final int n) {
            this.markpos = this.pos;
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public void reset() throws IOException {
            this.pos = this.markpos;
        }
    }
    
    private static class DirectAudioFloatInputStream extends AudioFloatInputStream
    {
        private final AudioInputStream stream;
        private AudioFloatConverter converter;
        private final int framesize_pc;
        private byte[] buffer;
        
        DirectAudioFloatInputStream(AudioInputStream audioInputStream) {
            this.converter = AudioFloatConverter.getConverter(audioInputStream.getFormat());
            if (this.converter == null) {
                final AudioFormat format = audioInputStream.getFormat();
                final AudioFormat[] targetFormats = AudioSystem.getTargetFormats(AudioFormat.Encoding.PCM_SIGNED, format);
                AudioFormat audioFormat;
                if (targetFormats.length != 0) {
                    audioFormat = targetFormats[0];
                }
                else {
                    final float sampleRate = format.getSampleRate();
                    format.getSampleSizeInBits();
                    format.getFrameSize();
                    format.getFrameRate();
                    final int n = 16;
                    audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, n, format.getChannels(), format.getChannels() * (n / 8), sampleRate, false);
                }
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
                this.converter = AudioFloatConverter.getConverter(audioInputStream.getFormat());
            }
            this.framesize_pc = audioInputStream.getFormat().getFrameSize() / audioInputStream.getFormat().getChannels();
            this.stream = audioInputStream;
        }
        
        @Override
        public AudioFormat getFormat() {
            return this.stream.getFormat();
        }
        
        @Override
        public long getFrameLength() {
            return this.stream.getFrameLength();
        }
        
        @Override
        public int read(final float[] array, final int n, final int n2) throws IOException {
            final int n3 = n2 * this.framesize_pc;
            if (this.buffer == null || this.buffer.length < n3) {
                this.buffer = new byte[n3];
            }
            final int read = this.stream.read(this.buffer, 0, n3);
            if (read == -1) {
                return -1;
            }
            this.converter.toFloatArray(this.buffer, array, n, read / this.framesize_pc);
            return read / this.framesize_pc;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final long skip = this.stream.skip(n * this.framesize_pc);
            if (skip == -1L) {
                return -1L;
            }
            return skip / this.framesize_pc;
        }
        
        @Override
        public int available() throws IOException {
            return this.stream.available() / this.framesize_pc;
        }
        
        @Override
        public void close() throws IOException {
            this.stream.close();
        }
        
        @Override
        public void mark(final int n) {
            this.stream.mark(n * this.framesize_pc);
        }
        
        @Override
        public boolean markSupported() {
            return this.stream.markSupported();
        }
        
        @Override
        public void reset() throws IOException {
            this.stream.reset();
        }
    }
}
