package com.sun.media.sound;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import java.util.HashMap;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat;
import java.io.InputStream;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveExtensibleFileReader extends AudioFileReader
{
    private static final String[] channelnames;
    private static final String[] allchannelnames;
    private static final GUID SUBTYPE_PCM;
    private static final GUID SUBTYPE_IEEE_FLOAT;
    
    private String decodeChannelMask(final long n) {
        final StringBuffer sb = new StringBuffer();
        long n2 = 1L;
        for (int i = 0; i < WaveExtensibleFileReader.allchannelnames.length; ++i) {
            if ((n & n2) != 0x0L) {
                if (i < WaveExtensibleFileReader.channelnames.length) {
                    sb.append(WaveExtensibleFileReader.channelnames[i] + " ");
                }
                else {
                    sb.append(WaveExtensibleFileReader.allchannelnames[i] + " ");
                }
            }
            n2 *= 2L;
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.substring(0, sb.length() - 1);
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        inputStream.mark(200);
        AudioFileFormat internal_getAudioFileFormat;
        try {
            internal_getAudioFileFormat = this.internal_getAudioFileFormat(inputStream);
        }
        finally {
            inputStream.reset();
        }
        return internal_getAudioFileFormat;
    }
    
    private AudioFileFormat internal_getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final RIFFReader riffReader = new RIFFReader(inputStream);
        if (!riffReader.getFormat().equals("RIFF")) {
            throw new UnsupportedAudioFileException();
        }
        if (!riffReader.getType().equals("WAVE")) {
            throw new UnsupportedAudioFileException();
        }
        boolean b = false;
        boolean b2 = false;
        int unsignedShort = 1;
        long unsignedInt = 1L;
        int unsignedShort2 = 1;
        int unsignedShort3 = 1;
        int unsignedShort4 = 1;
        long unsignedInt2 = 0L;
        GUID read = null;
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("fmt ")) {
                b = true;
                if (nextChunk.readUnsignedShort() != 65534) {
                    throw new UnsupportedAudioFileException();
                }
                unsignedShort = nextChunk.readUnsignedShort();
                unsignedInt = nextChunk.readUnsignedInt();
                nextChunk.readUnsignedInt();
                unsignedShort2 = nextChunk.readUnsignedShort();
                unsignedShort3 = nextChunk.readUnsignedShort();
                if (nextChunk.readUnsignedShort() != 22) {
                    throw new UnsupportedAudioFileException();
                }
                unsignedShort4 = nextChunk.readUnsignedShort();
                if (unsignedShort4 > unsignedShort3) {
                    throw new UnsupportedAudioFileException();
                }
                unsignedInt2 = nextChunk.readUnsignedInt();
                read = GUID.read(nextChunk);
            }
            if (nextChunk.getFormat().equals("data")) {
                b2 = true;
                break;
            }
        }
        if (!b) {
            throw new UnsupportedAudioFileException();
        }
        if (!b2) {
            throw new UnsupportedAudioFileException();
        }
        final HashMap hashMap = new HashMap();
        final String decodeChannelMask = this.decodeChannelMask(unsignedInt2);
        if (decodeChannelMask != null) {
            hashMap.put("channelOrder", decodeChannelMask);
        }
        if (unsignedInt2 != 0L) {
            hashMap.put("channelMask", unsignedInt2);
        }
        hashMap.put("validBitsPerSample", unsignedShort4);
        AudioFormat audioFormat;
        if (read.equals(WaveExtensibleFileReader.SUBTYPE_PCM)) {
            if (unsignedShort3 == 8) {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)unsignedInt, unsignedShort3, unsignedShort, unsignedShort2, (float)unsignedInt, false, hashMap);
            }
            else {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)unsignedInt, unsignedShort3, unsignedShort, unsignedShort2, (float)unsignedInt, false, hashMap);
            }
        }
        else {
            if (!read.equals(WaveExtensibleFileReader.SUBTYPE_IEEE_FLOAT)) {
                throw new UnsupportedAudioFileException();
            }
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)unsignedInt, unsignedShort3, unsignedShort, unsignedShort2, (float)unsignedInt, false, hashMap);
        }
        return new AudioFileFormat(AudioFileFormat.Type.WAVE, audioFormat, -1);
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final AudioFileFormat audioFileFormat = this.getAudioFileFormat(inputStream);
        final RIFFReader riffReader = new RIFFReader(inputStream);
        if (!riffReader.getFormat().equals("RIFF")) {
            throw new UnsupportedAudioFileException();
        }
        if (!riffReader.getType().equals("WAVE")) {
            throw new UnsupportedAudioFileException();
        }
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("data")) {
                return new AudioInputStream(nextChunk, audioFileFormat.getFormat(), nextChunk.getSize());
            }
        }
        throw new UnsupportedAudioFileException();
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        final InputStream openStream = url.openStream();
        AudioFileFormat audioFileFormat;
        try {
            audioFileFormat = this.getAudioFileFormat(new BufferedInputStream(openStream));
        }
        finally {
            openStream.close();
        }
        return audioFileFormat;
    }
    
    @Override
    public AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        AudioFileFormat audioFileFormat;
        try {
            audioFileFormat = this.getAudioFileFormat(new BufferedInputStream(fileInputStream));
        }
        finally {
            fileInputStream.close();
        }
        return audioFileFormat;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        return this.getAudioInputStream(new BufferedInputStream(url.openStream()));
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        return this.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
    }
    
    static {
        channelnames = new String[] { "FL", "FR", "FC", "LF", "BL", "BR", "FLC", "FLR", "BC", "SL", "SR", "TC", "TFL", "TFC", "TFR", "TBL", "TBC", "TBR" };
        allchannelnames = new String[] { "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", "w11", "w12", "w13", "w14", "w15", "w16", "w17", "w18", "w19", "w20", "w21", "w22", "w23", "w24", "w25", "w26", "w27", "w28", "w29", "w30", "w31", "w32", "w33", "w34", "w35", "w36", "w37", "w38", "w39", "w40", "w41", "w42", "w43", "w44", "w45", "w46", "w47", "w48", "w49", "w50", "w51", "w52", "w53", "w54", "w55", "w56", "w57", "w58", "w59", "w60", "w61", "w62", "w63", "w64" };
        SUBTYPE_PCM = new GUID(1L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
        SUBTYPE_IEEE_FLOAT = new GUID(3L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
    }
    
    private static class GUID
    {
        long i1;
        int s1;
        int s2;
        int x1;
        int x2;
        int x3;
        int x4;
        int x5;
        int x6;
        int x7;
        int x8;
        
        private GUID() {
        }
        
        GUID(final long i1, final int s1, final int s2, final int x1, final int x2, final int x3, final int x4, final int x5, final int x6, final int x7, final int x8) {
            this.i1 = i1;
            this.s1 = s1;
            this.s2 = s2;
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.x4 = x4;
            this.x5 = x5;
            this.x6 = x6;
            this.x7 = x7;
            this.x8 = x8;
        }
        
        public static GUID read(final RIFFReader riffReader) throws IOException {
            final GUID guid = new GUID();
            guid.i1 = riffReader.readUnsignedInt();
            guid.s1 = riffReader.readUnsignedShort();
            guid.s2 = riffReader.readUnsignedShort();
            guid.x1 = riffReader.readUnsignedByte();
            guid.x2 = riffReader.readUnsignedByte();
            guid.x3 = riffReader.readUnsignedByte();
            guid.x4 = riffReader.readUnsignedByte();
            guid.x5 = riffReader.readUnsignedByte();
            guid.x6 = riffReader.readUnsignedByte();
            guid.x7 = riffReader.readUnsignedByte();
            guid.x8 = riffReader.readUnsignedByte();
            return guid;
        }
        
        @Override
        public int hashCode() {
            return (int)this.i1;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof GUID)) {
                return false;
            }
            final GUID guid = (GUID)o;
            return this.i1 == guid.i1 && this.s1 == guid.s1 && this.s2 == guid.s2 && this.x1 == guid.x1 && this.x2 == guid.x2 && this.x3 == guid.x3 && this.x4 == guid.x4 && this.x5 == guid.x5 && this.x6 == guid.x6 && this.x7 == guid.x7 && this.x8 == guid.x8;
        }
    }
}
