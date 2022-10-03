package com.sun.media.sound;

import javax.sound.sampled.AudioInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.net.URL;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.DataInputStream;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;

public final class AuFileReader extends SunFileReader
{
    @Override
    public AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final int n = 28;
        boolean b = false;
        int n2 = 0;
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        dataInputStream.mark(n);
        final int int1 = dataInputStream.readInt();
        if (int1 != 779316836 || int1 == 779314176 || int1 == 1684960046 || int1 == 6583086) {
            dataInputStream.reset();
            throw new UnsupportedAudioFileException("not an AU file");
        }
        if (int1 == 779316836 || int1 == 779314176) {
            b = true;
        }
        final int n3 = b ? dataInputStream.readInt() : this.rllong(dataInputStream);
        n2 += 4;
        final int n4 = b ? dataInputStream.readInt() : this.rllong(dataInputStream);
        n2 += 4;
        final int n5 = b ? dataInputStream.readInt() : this.rllong(dataInputStream);
        n2 += 4;
        final int n6 = b ? dataInputStream.readInt() : this.rllong(dataInputStream);
        n2 += 4;
        final int n7 = b ? dataInputStream.readInt() : this.rllong(dataInputStream);
        n2 += 4;
        if (n7 <= 0) {
            dataInputStream.reset();
            throw new UnsupportedAudioFileException("Invalid number of channels");
        }
        final int n8 = n6;
        AudioFormat.Encoding encoding = null;
        int n9 = 0;
        switch (n5) {
            case 1: {
                encoding = AudioFormat.Encoding.ULAW;
                n9 = 8;
                break;
            }
            case 27: {
                encoding = AudioFormat.Encoding.ALAW;
                n9 = 8;
                break;
            }
            case 2: {
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                n9 = 8;
                break;
            }
            case 3: {
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                n9 = 16;
                break;
            }
            case 4: {
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                n9 = 24;
                break;
            }
            case 5: {
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                n9 = 32;
                break;
            }
            default: {
                dataInputStream.reset();
                throw new UnsupportedAudioFileException("not a valid AU file");
            }
        }
        final int calculatePCMFrameSize = SunFileReader.calculatePCMFrameSize(n9, n7);
        int n10;
        if (n4 < 0) {
            n10 = -1;
        }
        else {
            n10 = n4 / calculatePCMFrameSize;
        }
        final AuFileFormat auFileFormat = new AuFileFormat(AudioFileFormat.Type.AU, n4 + n3, new AudioFormat(encoding, (float)n6, n9, n7, calculatePCMFrameSize, (float)n8, b), n10);
        dataInputStream.reset();
        return auFileFormat;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat audioFileFormat = null;
        final InputStream openStream = url.openStream();
        try {
            audioFileFormat = this.getAudioFileFormat(new BufferedInputStream(openStream, 4096));
        }
        finally {
            openStream.close();
        }
        return audioFileFormat;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat audioFileFormat = null;
        final FileInputStream fileInputStream = new FileInputStream(file);
        try {
            audioFileFormat = this.getAudioFileFormat(new BufferedInputStream(fileInputStream, 4096));
        }
        finally {
            fileInputStream.close();
        }
        return audioFileFormat;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat audioFileFormat = this.getAudioFileFormat(inputStream);
        final AudioFormat format = audioFileFormat.getFormat();
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        dataInputStream.readInt();
        dataInputStream.skipBytes((format.isBigEndian() ? dataInputStream.readInt() : this.rllong(dataInputStream)) - 8);
        return new AudioInputStream(dataInputStream, format, audioFileFormat.getFrameLength());
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        final InputStream openStream = url.openStream();
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = this.getAudioInputStream(new BufferedInputStream(openStream, 4096));
        }
        finally {
            if (audioInputStream == null) {
                openStream.close();
            }
        }
        return audioInputStream;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = this.getAudioInputStream(new BufferedInputStream(fileInputStream, 4096));
        }
        finally {
            if (audioInputStream == null) {
                fileInputStream.close();
            }
        }
        return audioInputStream;
    }
}
