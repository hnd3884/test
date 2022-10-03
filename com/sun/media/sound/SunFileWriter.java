package com.sun.media.sound;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.spi.AudioFileWriter;

abstract class SunFileWriter extends AudioFileWriter
{
    protected static final int bufferSize = 16384;
    protected static final int bisBufferSize = 4096;
    final AudioFileFormat.Type[] types;
    
    SunFileWriter(final AudioFileFormat.Type[] types) {
        this.types = types;
    }
    
    @Override
    public final AudioFileFormat.Type[] getAudioFileTypes() {
        final AudioFileFormat.Type[] array = new AudioFileFormat.Type[this.types.length];
        System.arraycopy(this.types, 0, array, 0, this.types.length);
        return array;
    }
    
    @Override
    public abstract AudioFileFormat.Type[] getAudioFileTypes(final AudioInputStream p0);
    
    @Override
    public abstract int write(final AudioInputStream p0, final AudioFileFormat.Type p1, final OutputStream p2) throws IOException;
    
    @Override
    public abstract int write(final AudioInputStream p0, final AudioFileFormat.Type p1, final File p2) throws IOException;
    
    final int rllong(final DataInputStream dataInputStream) throws IOException {
        final int int1 = dataInputStream.readInt();
        return (int1 & 0xFF) << 24 | (int1 & 0xFF00) << 8 | (int1 & 0xFF0000) >> 8 | (int1 & 0xFF000000) >>> 24;
    }
    
    final int big2little(int n) {
        n = ((n & 0xFF) << 24 | (n & 0xFF00) << 8 | (n & 0xFF0000) >> 8 | (n & 0xFF000000) >>> 24);
        return n;
    }
    
    final short rlshort(final DataInputStream dataInputStream) throws IOException {
        final short short1 = dataInputStream.readShort();
        return (short)((short)((short1 & 0xFF) << 8) | (short)((short1 & 0xFF00) >>> 8));
    }
    
    final short big2littleShort(final short n) {
        return (short)((short)((n & 0xFF) << 8) | (short)((n & 0xFF00) >>> 8));
    }
    
    final class NoCloseInputStream extends InputStream
    {
        private final InputStream in;
        
        NoCloseInputStream(final InputStream in) {
            this.in = in;
        }
        
        @Override
        public int read() throws IOException {
            return this.in.read();
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.in.read(array);
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            return this.in.read(array, n, n2);
        }
        
        @Override
        public long skip(final long n) throws IOException {
            return this.in.skip(n);
        }
        
        @Override
        public int available() throws IOException {
            return this.in.available();
        }
        
        @Override
        public void close() throws IOException {
        }
        
        @Override
        public void mark(final int n) {
            this.in.mark(n);
        }
        
        @Override
        public void reset() throws IOException {
            this.in.reset();
        }
        
        @Override
        public boolean markSupported() {
            return this.in.markSupported();
        }
    }
}
