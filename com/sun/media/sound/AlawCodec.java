package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public final class AlawCodec extends SunCodec
{
    private static final byte[] ALAW_TABH;
    private static final byte[] ALAW_TABL;
    private static final AudioFormat.Encoding[] alawEncodings;
    private static final short[] seg_end;
    
    public AlawCodec() {
        super(AlawCodec.alawEncodings, AlawCodec.alawEncodings);
    }
    
    @Override
    public AudioFormat.Encoding[] getTargetEncodings(final AudioFormat audioFormat) {
        if (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
            if (audioFormat.getSampleSizeInBits() == 16) {
                return new AudioFormat.Encoding[] { AudioFormat.Encoding.ALAW };
            }
            return new AudioFormat.Encoding[0];
        }
        else {
            if (!audioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
                return new AudioFormat.Encoding[0];
            }
            if (audioFormat.getSampleSizeInBits() == 8) {
                return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED };
            }
            return new AudioFormat.Encoding[0];
        }
    }
    
    @Override
    public AudioFormat[] getTargetFormats(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        if ((encoding.equals(AudioFormat.Encoding.PCM_SIGNED) && audioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) || (encoding.equals(AudioFormat.Encoding.ALAW) && audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))) {
            return this.getOutputFormats(audioFormat);
        }
        return new AudioFormat[0];
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final AudioFormat.Encoding encoding, final AudioInputStream audioInputStream) {
        final AudioFormat format = audioInputStream.getFormat();
        final AudioFormat.Encoding encoding2 = format.getEncoding();
        if (encoding2.equals(encoding)) {
            return audioInputStream;
        }
        if (!this.isConversionSupported(encoding, audioInputStream.getFormat())) {
            throw new IllegalArgumentException("Unsupported conversion: " + audioInputStream.getFormat().toString() + " to " + encoding.toString());
        }
        AudioFormat audioFormat;
        if (encoding2.equals(AudioFormat.Encoding.ALAW) && encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            audioFormat = new AudioFormat(encoding, format.getSampleRate(), 16, format.getChannels(), 2 * format.getChannels(), format.getSampleRate(), format.isBigEndian());
        }
        else {
            if (!encoding2.equals(AudioFormat.Encoding.PCM_SIGNED) || !encoding.equals(AudioFormat.Encoding.ALAW)) {
                throw new IllegalArgumentException("Unsupported conversion: " + audioInputStream.getFormat().toString() + " to " + encoding.toString());
            }
            audioFormat = new AudioFormat(encoding, format.getSampleRate(), 8, format.getChannels(), format.getChannels(), format.getSampleRate(), false);
        }
        return this.getAudioInputStream(audioFormat, audioInputStream);
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final AudioFormat audioFormat, final AudioInputStream audioInputStream) {
        return this.getConvertedStream(audioFormat, audioInputStream);
    }
    
    private AudioInputStream getConvertedStream(final AudioFormat audioFormat, final AudioInputStream audioInputStream) {
        AudioInputStream audioInputStream2;
        if (audioInputStream.getFormat().matches(audioFormat)) {
            audioInputStream2 = audioInputStream;
        }
        else {
            audioInputStream2 = new AlawCodecStream(audioInputStream, audioFormat);
        }
        return audioInputStream2;
    }
    
    private AudioFormat[] getOutputFormats(final AudioFormat audioFormat) {
        final Vector vector = new Vector();
        if (AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding())) {
            vector.addElement(new AudioFormat(AudioFormat.Encoding.ALAW, audioFormat.getSampleRate(), 8, audioFormat.getChannels(), audioFormat.getChannels(), audioFormat.getSampleRate(), false));
        }
        if (AudioFormat.Encoding.ALAW.equals(audioFormat.getEncoding())) {
            vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false));
            vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), true));
        }
        final AudioFormat[] array = new AudioFormat[vector.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (AudioFormat)vector.elementAt(i);
        }
        return array;
    }
    
    static {
        ALAW_TABH = new byte[256];
        ALAW_TABL = new byte[256];
        alawEncodings = new AudioFormat.Encoding[] { AudioFormat.Encoding.ALAW, AudioFormat.Encoding.PCM_SIGNED };
        seg_end = new short[] { 255, 511, 1023, 2047, 4095, 8191, 16383, 32767 };
        for (int i = 0; i < 256; ++i) {
            final int n = i ^ 0x55;
            final int n2 = (n & 0xF) << 4;
            final int n3 = (n & 0x70) >> 4;
            int n4 = n2 + 8;
            if (n3 >= 1) {
                n4 += 256;
            }
            if (n3 > 1) {
                n4 <<= n3 - 1;
            }
            if ((n & 0x80) == 0x0) {
                n4 = -n4;
            }
            AlawCodec.ALAW_TABL[i] = (byte)n4;
            AlawCodec.ALAW_TABH[i] = (byte)(n4 >> 8);
        }
    }
    
    final class AlawCodecStream extends AudioInputStream
    {
        private static final int tempBufferSize = 64;
        private byte[] tempBuffer;
        boolean encode;
        AudioFormat encodeFormat;
        AudioFormat decodeFormat;
        byte[] tabByte1;
        byte[] tabByte2;
        int highByte;
        int lowByte;
        
        AlawCodecStream(final AudioInputStream audioInputStream, final AudioFormat audioFormat) {
            super(audioInputStream, audioFormat, -1L);
            this.tempBuffer = null;
            this.encode = false;
            this.tabByte1 = null;
            this.tabByte2 = null;
            this.highByte = 0;
            this.lowByte = 1;
            final AudioFormat format = audioInputStream.getFormat();
            if (!AlawCodec.this.isConversionSupported(audioFormat, format)) {
                throw new IllegalArgumentException("Unsupported conversion: " + format.toString() + " to " + audioFormat.toString());
            }
            boolean b;
            if (AudioFormat.Encoding.ALAW.equals(format.getEncoding())) {
                this.encode = false;
                this.encodeFormat = format;
                this.decodeFormat = audioFormat;
                b = audioFormat.isBigEndian();
            }
            else {
                this.encode = true;
                this.encodeFormat = audioFormat;
                this.decodeFormat = format;
                b = format.isBigEndian();
                this.tempBuffer = new byte[64];
            }
            if (b) {
                this.tabByte1 = AlawCodec.ALAW_TABH;
                this.tabByte2 = AlawCodec.ALAW_TABL;
                this.highByte = 0;
                this.lowByte = 1;
            }
            else {
                this.tabByte1 = AlawCodec.ALAW_TABL;
                this.tabByte2 = AlawCodec.ALAW_TABH;
                this.highByte = 1;
                this.lowByte = 0;
            }
            if (audioInputStream instanceof AudioInputStream) {
                this.frameLength = audioInputStream.getFrameLength();
            }
            this.framePos = 0L;
            this.frameSize = format.getFrameSize();
            if (this.frameSize == -1) {
                this.frameSize = 1;
            }
        }
        
        private short search(final short n, final short[] array, final short n2) {
            for (short n3 = 0; n3 < n2; ++n3) {
                if (n <= array[n3]) {
                    return n3;
                }
            }
            return n2;
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read(final byte[] array, final int n, int n2) throws IOException {
            if (n2 % this.frameSize != 0) {
                n2 -= n2 % this.frameSize;
            }
            if (this.encode) {
                final int n3 = 15;
                final int n4 = 4;
                int n5 = n;
                int read;
                for (int n6 = n2 * 2, n7 = (n6 > 64) ? 64 : n6; (read = super.read(this.tempBuffer, 0, n7)) > 0; n7 = ((n6 > 64) ? 64 : n6)) {
                    for (int i = 0; i < read; i += 2) {
                        short n8 = (short)((short)(this.tempBuffer[i + this.highByte] << 8 & 0xFF00) | (short)(this.tempBuffer[i + this.lowByte] & 0xFF));
                        int n9;
                        if (n8 >= 0) {
                            n9 = 213;
                        }
                        else {
                            n9 = 85;
                            n8 = (short)(-n8 - 8);
                        }
                        final short search = this.search(n8, AlawCodec.seg_end, (short)8);
                        byte b;
                        if (search >= 8) {
                            b = (byte)(0x7F ^ n9);
                        }
                        else {
                            final byte b2 = (byte)(search << n4);
                            byte b3;
                            if (search < 2) {
                                b3 = (byte)(b2 | (byte)(n8 >> 4 & n3));
                            }
                            else {
                                b3 = (byte)(b2 | (byte)(n8 >> search + 3 & n3));
                            }
                            b = (byte)(b3 ^ n9);
                        }
                        array[n5] = b;
                        ++n5;
                    }
                    n6 -= read;
                }
                if (n5 == n && read < 0) {
                    return read;
                }
                return n5 - n;
            }
            else {
                final int n10 = n2 / 2;
                int n11 = n + n2 / 2;
                int read2;
                int j;
                for (read2 = super.read(array, n11, n10), j = n; j < n + read2 * 2; j += 2) {
                    array[j] = this.tabByte1[array[n11] & 0xFF];
                    array[j + 1] = this.tabByte2[array[n11] & 0xFF];
                    ++n11;
                }
                if (read2 < 0) {
                    return read2;
                }
                return j - n;
            }
        }
    }
}
