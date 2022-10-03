package com.sun.media.sound;

import java.io.DataInputStream;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;
import javax.sound.sampled.spi.AudioFileReader;

abstract class SunFileReader extends AudioFileReader
{
    protected static final int bisBufferSize = 4096;
    
    @Override
    public abstract AudioFileFormat getAudioFileFormat(final InputStream p0) throws UnsupportedAudioFileException, IOException;
    
    @Override
    public abstract AudioFileFormat getAudioFileFormat(final URL p0) throws UnsupportedAudioFileException, IOException;
    
    @Override
    public abstract AudioFileFormat getAudioFileFormat(final File p0) throws UnsupportedAudioFileException, IOException;
    
    @Override
    public abstract AudioInputStream getAudioInputStream(final InputStream p0) throws UnsupportedAudioFileException, IOException;
    
    @Override
    public abstract AudioInputStream getAudioInputStream(final URL p0) throws UnsupportedAudioFileException, IOException;
    
    @Override
    public abstract AudioInputStream getAudioInputStream(final File p0) throws UnsupportedAudioFileException, IOException;
    
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
    
    static final int calculatePCMFrameSize(final int n, final int n2) {
        return (n + 7) / 8 * n2;
    }
}
