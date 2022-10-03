package com.sun.media.sound;

import java.io.DataOutputStream;
import javax.sound.sampled.AudioFormat;
import java.io.DataInputStream;
import javax.sound.sampled.AudioInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;

public final class AiffFileReader extends SunFileReader
{
    private static final int MAX_READ_LENGTH = 8;
    
    @Override
    public AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat comm = this.getCOMM(inputStream, true);
        inputStream.reset();
        return comm;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat comm = null;
        final InputStream openStream = url.openStream();
        try {
            comm = this.getCOMM(openStream, false);
        }
        finally {
            openStream.close();
        }
        return comm;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat comm = null;
        final FileInputStream fileInputStream = new FileInputStream(file);
        try {
            comm = this.getCOMM(fileInputStream, false);
        }
        finally {
            fileInputStream.close();
        }
        return comm;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat comm = this.getCOMM(inputStream, true);
        return new AudioInputStream(inputStream, comm.getFormat(), comm.getFrameLength());
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        final InputStream openStream = url.openStream();
        AudioFileFormat comm = null;
        try {
            comm = this.getCOMM(openStream, false);
        }
        finally {
            if (comm == null) {
                openStream.close();
            }
        }
        return new AudioInputStream(openStream, comm.getFormat(), comm.getFrameLength());
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        AudioFileFormat comm = null;
        try {
            comm = this.getCOMM(fileInputStream, false);
        }
        finally {
            if (comm == null) {
                fileInputStream.close();
            }
        }
        return new AudioInputStream(fileInputStream, comm.getFormat(), comm.getFrameLength());
    }
    
    private AudioFileFormat getCOMM(final InputStream inputStream, final boolean b) throws UnsupportedAudioFileException, IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        if (b) {
            dataInputStream.mark(8);
        }
        int n = 0;
        int n2 = 0;
        AudioFormat audioFormat = null;
        if (dataInputStream.readInt() != 1179603533) {
            if (b) {
                dataInputStream.reset();
            }
            throw new UnsupportedAudioFileException("not an AIFF file");
        }
        int int1 = dataInputStream.readInt();
        final int int2 = dataInputStream.readInt();
        n += 12;
        int n3;
        if (int1 <= 0) {
            int1 = -1;
            n3 = -1;
        }
        else {
            n3 = int1 + 8;
        }
        boolean b2 = false;
        if (int2 == 1095321155) {
            b2 = true;
        }
        int i = 0;
        while (i == 0) {
            final int int3 = dataInputStream.readInt();
            final int int4 = dataInputStream.readInt();
            n += 8;
            int n4 = 0;
            switch (int3) {
                case 1129270605: {
                    if ((!b2 && int4 < 18) || (b2 && int4 < 22)) {
                        throw new UnsupportedAudioFileException("Invalid AIFF/COMM chunksize");
                    }
                    final int unsignedShort = dataInputStream.readUnsignedShort();
                    if (unsignedShort <= 0) {
                        throw new UnsupportedAudioFileException("Invalid number of channels");
                    }
                    dataInputStream.readInt();
                    int unsignedShort2 = dataInputStream.readUnsignedShort();
                    if (unsignedShort2 < 1 || unsignedShort2 > 32) {
                        throw new UnsupportedAudioFileException("Invalid AIFF/COMM sampleSize");
                    }
                    final float n5 = (float)this.read_ieee_extended(dataInputStream);
                    n4 += 18;
                    AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
                    if (b2) {
                        final int int5 = dataInputStream.readInt();
                        n4 += 4;
                        switch (int5) {
                            case 1313820229: {
                                encoding = AudioFormat.Encoding.PCM_SIGNED;
                                break;
                            }
                            case 1970037111: {
                                encoding = AudioFormat.Encoding.ULAW;
                                unsignedShort2 = 8;
                                break;
                            }
                            default: {
                                throw new UnsupportedAudioFileException("Invalid AIFF encoding");
                            }
                        }
                    }
                    audioFormat = new AudioFormat(encoding, n5, unsignedShort2, unsignedShort, SunFileReader.calculatePCMFrameSize(unsignedShort2, unsignedShort), n5, true);
                    break;
                }
                case 1397968452: {
                    dataInputStream.readInt();
                    dataInputStream.readInt();
                    n4 += 8;
                    if (int4 < int1) {
                        n2 = int4 - n4;
                    }
                    else {
                        n2 = int1 - (n + n4);
                    }
                    i = 1;
                    break;
                }
            }
            n += n4;
            if (i == 0) {
                final int n6 = int4 - n4;
                if (n6 <= 0) {
                    continue;
                }
                n += dataInputStream.skipBytes(n6);
            }
        }
        if (audioFormat == null) {
            throw new UnsupportedAudioFileException("missing COMM chunk");
        }
        return new AiffFileFormat(b2 ? AudioFileFormat.Type.AIFC : AudioFileFormat.Type.AIFF, n3, audioFormat, n2 / audioFormat.getFrameSize());
    }
    
    private void write_ieee_extended(final DataOutputStream dataOutputStream, final double n) throws IOException {
        int n2;
        double n3;
        for (n2 = 16398, n3 = n; n3 < 44000.0; n3 *= 2.0, --n2) {}
        dataOutputStream.writeShort(n2);
        dataOutputStream.writeInt((int)n3 << 16);
        dataOutputStream.writeInt(0);
    }
    
    private double read_ieee_extended(final DataInputStream dataInputStream) throws IOException {
        final double n = 3.4028234663852886E38;
        int unsignedShort = dataInputStream.readUnsignedShort();
        final long n2 = (long)dataInputStream.readUnsignedShort() << 16 | (long)dataInputStream.readUnsignedShort();
        final long n3 = (long)dataInputStream.readUnsignedShort() << 16 | (long)dataInputStream.readUnsignedShort();
        double n4;
        if (unsignedShort == 0 && n2 == 0L && n3 == 0L) {
            n4 = 0.0;
        }
        else if (unsignedShort == 32767) {
            n4 = n;
        }
        else {
            unsignedShort -= 16383;
            unsignedShort -= 31;
            final double n5 = n2 * Math.pow(2.0, unsignedShort);
            unsignedShort -= 32;
            n4 = n5 + n3 * Math.pow(2.0, unsignedShort);
        }
        return n4;
    }
}
