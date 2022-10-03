package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;

public final class UlawCodec extends SunCodec
{
    private static final byte[] ULAW_TABH;
    private static final byte[] ULAW_TABL;
    private static final AudioFormat.Encoding[] ulawEncodings;
    private static final short[] seg_end;
    
    public UlawCodec() {
        super(UlawCodec.ulawEncodings, UlawCodec.ulawEncodings);
    }
    
    @Override
    public AudioFormat.Encoding[] getTargetEncodings(final AudioFormat audioFormat) {
        if (AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding())) {
            if (audioFormat.getSampleSizeInBits() == 16) {
                return new AudioFormat.Encoding[] { AudioFormat.Encoding.ULAW };
            }
            return new AudioFormat.Encoding[0];
        }
        else {
            if (!AudioFormat.Encoding.ULAW.equals(audioFormat.getEncoding())) {
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
        if ((AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && AudioFormat.Encoding.ULAW.equals(audioFormat.getEncoding())) || (AudioFormat.Encoding.ULAW.equals(encoding) && AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding()))) {
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
        if (AudioFormat.Encoding.ULAW.equals(encoding2) && AudioFormat.Encoding.PCM_SIGNED.equals(encoding)) {
            audioFormat = new AudioFormat(encoding, format.getSampleRate(), 16, format.getChannels(), 2 * format.getChannels(), format.getSampleRate(), format.isBigEndian());
        }
        else {
            if (!AudioFormat.Encoding.PCM_SIGNED.equals(encoding2) || !AudioFormat.Encoding.ULAW.equals(encoding)) {
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
            audioInputStream2 = new UlawCodecStream(audioInputStream, audioFormat);
        }
        return audioInputStream2;
    }
    
    private AudioFormat[] getOutputFormats(final AudioFormat audioFormat) {
        final Vector vector = new Vector();
        if (audioFormat.getSampleSizeInBits() == 16 && AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding())) {
            vector.addElement(new AudioFormat(AudioFormat.Encoding.ULAW, audioFormat.getSampleRate(), 8, audioFormat.getChannels(), audioFormat.getChannels(), audioFormat.getSampleRate(), false));
        }
        if (AudioFormat.Encoding.ULAW.equals(audioFormat.getEncoding())) {
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
        ULAW_TABH = new byte[256];
        ULAW_TABL = new byte[256];
        ulawEncodings = new AudioFormat.Encoding[] { AudioFormat.Encoding.ULAW, AudioFormat.Encoding.PCM_SIGNED };
        seg_end = new short[] { 255, 511, 1023, 2047, 4095, 8191, 16383, 32767 };
        for (int i = 0; i < 256; ++i) {
            final int n = ~i & 0xFF;
            final int n2 = ((n & 0xF) << 3) + 132 << ((n & 0x70) >> 4);
            final int n3 = ((n & 0x80) != 0x0) ? (132 - n2) : (n2 - 132);
            UlawCodec.ULAW_TABL[i] = (byte)(n3 & 0xFF);
            UlawCodec.ULAW_TABH[i] = (byte)(n3 >> 8 & 0xFF);
        }
    }
    
    class UlawCodecStream extends AudioInputStream
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
        
        UlawCodecStream(final AudioInputStream audioInputStream, final AudioFormat audioFormat) {
            super(audioInputStream, audioFormat, -1L);
            this.tempBuffer = null;
            this.encode = false;
            this.tabByte1 = null;
            this.tabByte2 = null;
            this.highByte = 0;
            this.lowByte = 1;
            final AudioFormat format = audioInputStream.getFormat();
            if (!UlawCodec.this.isConversionSupported(audioFormat, format)) {
                throw new IllegalArgumentException("Unsupported conversion: " + format.toString() + " to " + audioFormat.toString());
            }
            boolean b;
            if (AudioFormat.Encoding.ULAW.equals(format.getEncoding())) {
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
                this.tabByte1 = UlawCodec.ULAW_TABH;
                this.tabByte2 = UlawCodec.ULAW_TABL;
                this.highByte = 0;
                this.lowByte = 1;
            }
            else {
                this.tabByte1 = UlawCodec.ULAW_TABL;
                this.tabByte2 = UlawCodec.ULAW_TABH;
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
            if (this.read(array, 0, array.length) == 1) {
                return array[1] & 0xFF;
            }
            return -1;
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
                final short n3 = 132;
                int n4 = n;
                int read;
                for (int n5 = n2 * 2, n6 = (n5 > 64) ? 64 : n5; (read = super.read(this.tempBuffer, 0, n6)) > 0; n6 = ((n5 > 64) ? 64 : n5)) {
                    for (int i = 0; i < read; i += 2) {
                        final short n7 = (short)((short)(this.tempBuffer[i + this.highByte] << 8 & 0xFF00) | (short)(this.tempBuffer[i + this.lowByte] & 0xFF));
                        short n8;
                        int n9;
                        if (n7 < 0) {
                            n8 = (short)(n3 - n7);
                            n9 = 127;
                        }
                        else {
                            n8 = (short)(n7 + n3);
                            n9 = 255;
                        }
                        final short search = this.search(n8, UlawCodec.seg_end, (short)8);
                        byte b;
                        if (search >= 8) {
                            b = (byte)(0x7F ^ n9);
                        }
                        else {
                            b = (byte)((byte)(search << 4 | (n8 >> search + 3 & 0xF)) ^ n9);
                        }
                        array[n4] = b;
                        ++n4;
                    }
                    n5 -= read;
                }
                if (n4 == n && read < 0) {
                    return read;
                }
                return n4 - n;
            }
            else {
                final int n10 = n2 / 2;
                int n11 = n + n2 / 2;
                final int read2 = super.read(array, n11, n10);
                if (read2 < 0) {
                    return read2;
                }
                int j;
                for (j = n; j < n + read2 * 2; j += 2) {
                    array[j] = this.tabByte1[array[n11] & 0xFF];
                    array[j + 1] = this.tabByte2[array[n11] & 0xFF];
                    ++n11;
                }
                return j - n;
            }
        }
    }
}
