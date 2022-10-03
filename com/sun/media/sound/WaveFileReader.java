package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import java.io.EOFException;
import java.io.DataInputStream;
import javax.sound.sampled.AudioInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;

public final class WaveFileReader extends SunFileReader
{
    private static final int MAX_READ_LENGTH = 12;
    
    @Override
    public AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat fmt = this.getFMT(inputStream, true);
        inputStream.reset();
        return fmt;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        final InputStream openStream = url.openStream();
        AudioFileFormat fmt = null;
        try {
            fmt = this.getFMT(openStream, false);
        }
        finally {
            openStream.close();
        }
        return fmt;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat fmt = null;
        final FileInputStream fileInputStream = new FileInputStream(file);
        try {
            fmt = this.getFMT(fileInputStream, false);
        }
        finally {
            fileInputStream.close();
        }
        return fmt;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat fmt = this.getFMT(inputStream, true);
        return new AudioInputStream(inputStream, fmt.getFormat(), fmt.getFrameLength());
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        final InputStream openStream = url.openStream();
        AudioFileFormat fmt = null;
        try {
            fmt = this.getFMT(openStream, false);
        }
        finally {
            if (fmt == null) {
                openStream.close();
            }
        }
        return new AudioInputStream(openStream, fmt.getFormat(), fmt.getFrameLength());
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        AudioFileFormat fmt = null;
        try {
            fmt = this.getFMT(fileInputStream, false);
        }
        finally {
            if (fmt == null) {
                fileInputStream.close();
            }
        }
        return new AudioInputStream(fileInputStream, fmt.getFormat(), fmt.getFrameLength());
    }
    
    private AudioFileFormat getFMT(final InputStream inputStream, final boolean b) throws UnsupportedAudioFileException, IOException {
        int n = 0;
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        if (b) {
            dataInputStream.mark(12);
        }
        final int int1 = dataInputStream.readInt();
        final int rllong = this.rllong(dataInputStream);
        final int int2 = dataInputStream.readInt();
        int n2;
        if (rllong <= 0) {
            n2 = -1;
        }
        else {
            n2 = rllong + 8;
        }
        if (int1 != 1380533830 || int2 != 1463899717) {
            if (b) {
                dataInputStream.reset();
            }
            throw new UnsupportedAudioFileException("not a WAVE file");
        }
        Label_0109: {
            break Label_0109;
            try {
                while (true) {
                    final int int3 = dataInputStream.readInt();
                    n += 4;
                    if (int3 == 1718449184) {
                        break;
                    }
                    int rllong2 = this.rllong(dataInputStream);
                    n += 4;
                    if (rllong2 % 2 > 0) {
                        ++rllong2;
                    }
                    n += dataInputStream.skipBytes(rllong2);
                }
            }
            catch (final EOFException ex) {
                throw new UnsupportedAudioFileException("Not a valid WAV file");
            }
        }
        int rllong3 = this.rllong(dataInputStream);
        n += 4;
        final int n3 = n + rllong3;
        final short rlshort = this.rlshort(dataInputStream);
        n += 2;
        AudioFormat.Encoding encoding;
        if (rlshort == 1) {
            encoding = AudioFormat.Encoding.PCM_SIGNED;
        }
        else if (rlshort == 6) {
            encoding = AudioFormat.Encoding.ALAW;
        }
        else {
            if (rlshort != 7) {
                throw new UnsupportedAudioFileException("Not a supported WAV file");
            }
            encoding = AudioFormat.Encoding.ULAW;
        }
        final short rlshort2 = this.rlshort(dataInputStream);
        n += 2;
        if (rlshort2 <= 0) {
            throw new UnsupportedAudioFileException("Invalid number of channels");
        }
        final long n4 = this.rllong(dataInputStream);
        n += 4;
        final long n5 = this.rllong(dataInputStream);
        n += 4;
        this.rlshort(dataInputStream);
        n += 2;
        final short rlshort3 = this.rlshort(dataInputStream);
        n += 2;
        if (rlshort3 <= 0) {
            throw new UnsupportedAudioFileException("Invalid bitsPerSample");
        }
        if (rlshort3 == 8 && encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            encoding = AudioFormat.Encoding.PCM_UNSIGNED;
        }
        if (rllong3 % 2 != 0) {
            ++rllong3;
        }
        if (n3 > n) {
            final int n6 = n + dataInputStream.skipBytes(n3 - n);
        }
        int n7 = 0;
        try {
            while (true) {
                final int int4 = dataInputStream.readInt();
                n7 += 4;
                if (int4 == 1684108385) {
                    break;
                }
                int rllong4 = this.rllong(dataInputStream);
                n7 += 4;
                if (rllong4 % 2 > 0) {
                    ++rllong4;
                }
                n7 += dataInputStream.skipBytes(rllong4);
            }
        }
        catch (final EOFException ex2) {
            throw new UnsupportedAudioFileException("Not a valid WAV file");
        }
        final int rllong5 = this.rllong(dataInputStream);
        n7 += 4;
        final AudioFormat audioFormat = new AudioFormat(encoding, (float)n4, rlshort3, rlshort2, SunFileReader.calculatePCMFrameSize(rlshort3, rlshort2), (float)n4, false);
        return new WaveFileFormat(AudioFileFormat.Type.WAVE, n2, audioFormat, rllong5 / audioFormat.getFrameSize());
    }
}
