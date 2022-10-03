package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;

public final class PCMtoPCMCodec extends SunCodec
{
    private static final AudioFormat.Encoding[] inputEncodings;
    private static final AudioFormat.Encoding[] outputEncodings;
    private static final int tempBufferSize = 64;
    private byte[] tempBuffer;
    
    public PCMtoPCMCodec() {
        super(PCMtoPCMCodec.inputEncodings, PCMtoPCMCodec.outputEncodings);
        this.tempBuffer = null;
    }
    
    @Override
    public AudioFormat.Encoding[] getTargetEncodings(final AudioFormat audioFormat) {
        if (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
        }
        return new AudioFormat.Encoding[0];
    }
    
    @Override
    public AudioFormat[] getTargetFormats(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        final AudioFormat[] outputFormats = this.getOutputFormats(audioFormat);
        final Vector vector = new Vector();
        for (int i = 0; i < outputFormats.length; ++i) {
            if (outputFormats[i].getEncoding().equals(encoding)) {
                vector.addElement(outputFormats[i]);
            }
        }
        final AudioFormat[] array = new AudioFormat[vector.size()];
        for (int j = 0; j < array.length; ++j) {
            array[j] = (AudioFormat)vector.elementAt(j);
        }
        return array;
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final AudioFormat.Encoding encoding, final AudioInputStream audioInputStream) {
        if (this.isConversionSupported(encoding, audioInputStream.getFormat())) {
            final AudioFormat format = audioInputStream.getFormat();
            return this.getAudioInputStream(new AudioFormat(encoding, format.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), format.isBigEndian()), audioInputStream);
        }
        throw new IllegalArgumentException("Unsupported conversion: " + audioInputStream.getFormat().toString() + " to " + encoding.toString());
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
            audioInputStream2 = new PCMtoPCMCodecStream(audioInputStream, audioFormat);
            this.tempBuffer = new byte[64];
        }
        return audioInputStream2;
    }
    
    private AudioFormat[] getOutputFormats(final AudioFormat audioFormat) {
        final Vector vector = new Vector();
        final int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        final boolean bigEndian = audioFormat.isBigEndian();
        if (sampleSizeInBits == 8) {
            if (AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding())) {
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
            }
            if (AudioFormat.Encoding.PCM_UNSIGNED.equals(audioFormat.getEncoding())) {
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
            }
        }
        else if (sampleSizeInBits == 16) {
            if (AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding()) && bigEndian) {
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), true));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
            }
            if (AudioFormat.Encoding.PCM_UNSIGNED.equals(audioFormat.getEncoding()) && bigEndian) {
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), true));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
            }
            if (AudioFormat.Encoding.PCM_SIGNED.equals(audioFormat.getEncoding()) && !bigEndian) {
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), true));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), true));
            }
            if (AudioFormat.Encoding.PCM_UNSIGNED.equals(audioFormat.getEncoding()) && !bigEndian) {
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), false));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), true));
                vector.addElement(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), audioFormat.getFrameSize(), audioFormat.getFrameRate(), true));
            }
        }
        final AudioFormat[] array;
        synchronized (vector) {
            array = new AudioFormat[vector.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (AudioFormat)vector.elementAt(i);
            }
        }
        return array;
    }
    
    static {
        inputEncodings = new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
        outputEncodings = new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
    }
    
    class PCMtoPCMCodecStream extends AudioInputStream
    {
        private final int PCM_SWITCH_SIGNED_8BIT = 1;
        private final int PCM_SWITCH_ENDIAN = 2;
        private final int PCM_SWITCH_SIGNED_LE = 3;
        private final int PCM_SWITCH_SIGNED_BE = 4;
        private final int PCM_UNSIGNED_LE2SIGNED_BE = 5;
        private final int PCM_SIGNED_LE2UNSIGNED_BE = 6;
        private final int PCM_UNSIGNED_BE2SIGNED_LE = 7;
        private final int PCM_SIGNED_BE2UNSIGNED_LE = 8;
        private final int sampleSizeInBytes;
        private int conversionType;
        
        PCMtoPCMCodecStream(final AudioInputStream audioInputStream, final AudioFormat audioFormat) {
            super(audioInputStream, audioFormat, -1L);
            this.conversionType = 0;
            final AudioFormat format = audioInputStream.getFormat();
            if (!PCMtoPCMCodec.this.isConversionSupported(format, audioFormat)) {
                throw new IllegalArgumentException("Unsupported conversion: " + format.toString() + " to " + audioFormat.toString());
            }
            final AudioFormat.Encoding encoding = format.getEncoding();
            final AudioFormat.Encoding encoding2 = audioFormat.getEncoding();
            final boolean bigEndian = format.isBigEndian();
            final boolean bigEndian2 = audioFormat.isBigEndian();
            final int sampleSizeInBits = format.getSampleSizeInBits();
            this.sampleSizeInBytes = sampleSizeInBits / 8;
            if (sampleSizeInBits == 8) {
                if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && AudioFormat.Encoding.PCM_SIGNED.equals(encoding2)) {
                    this.conversionType = 1;
                }
                else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding2)) {
                    this.conversionType = 1;
                }
            }
            else if (encoding.equals(encoding2) && bigEndian != bigEndian2) {
                this.conversionType = 2;
            }
            else if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && !bigEndian && AudioFormat.Encoding.PCM_SIGNED.equals(encoding2) && bigEndian2) {
                this.conversionType = 5;
            }
            else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && !bigEndian && AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding2) && bigEndian2) {
                this.conversionType = 6;
            }
            else if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding) && bigEndian && AudioFormat.Encoding.PCM_SIGNED.equals(encoding2) && !bigEndian2) {
                this.conversionType = 7;
            }
            else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && bigEndian && AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding2) && !bigEndian2) {
                this.conversionType = 8;
            }
            this.frameSize = format.getFrameSize();
            if (this.frameSize == -1) {
                this.frameSize = 1;
            }
            if (audioInputStream instanceof AudioInputStream) {
                this.frameLength = audioInputStream.getFrameLength();
            }
            else {
                this.frameLength = -1L;
            }
            this.framePos = 0L;
        }
        
        @Override
        public int read() throws IOException {
            if (this.frameSize != 1) {
                throw new IOException("cannot read a single byte if frame size > 1");
            }
            if (this.conversionType != 1) {
                throw new IOException("cannot read a single byte if frame size > 1");
            }
            final int read = super.read();
            if (read < 0) {
                return read;
            }
            final byte b = (byte)(read & 0xF);
            return ((b >= 0) ? ((byte)(0x80 | b)) : ((byte)(0x7F & b))) & 0xF;
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
            if (this.frameLength != -1L && n2 / this.frameSize > this.frameLength - this.framePos) {
                n2 = (int)(this.frameLength - this.framePos) * this.frameSize;
            }
            final int read = super.read(array, n, n2);
            if (read < 0) {
                return read;
            }
            switch (this.conversionType) {
                case 1: {
                    this.switchSigned8bit(array, n, n2, read);
                    break;
                }
                case 2: {
                    this.switchEndian(array, n, n2, read);
                    break;
                }
                case 3: {
                    this.switchSignedLE(array, n, n2, read);
                    break;
                }
                case 4: {
                    this.switchSignedBE(array, n, n2, read);
                    break;
                }
                case 5:
                case 6: {
                    this.switchSignedLE(array, n, n2, read);
                    this.switchEndian(array, n, n2, read);
                    break;
                }
                case 7:
                case 8: {
                    this.switchSignedBE(array, n, n2, read);
                    this.switchEndian(array, n, n2, read);
                    break;
                }
            }
            return read;
        }
        
        private void switchSigned8bit(final byte[] array, final int n, final int n2, final int n3) {
            for (int i = n; i < n + n3; ++i) {
                array[i] = ((array[i] >= 0) ? ((byte)(0x80 | array[i])) : ((byte)(0x7F & array[i])));
            }
        }
        
        private void switchSignedBE(final byte[] array, final int n, final int n2, final int n3) {
            for (int i = n; i < n + n3; i += this.sampleSizeInBytes) {
                array[i] = ((array[i] >= 0) ? ((byte)(0x80 | array[i])) : ((byte)(0x7F & array[i])));
            }
        }
        
        private void switchSignedLE(final byte[] array, final int n, final int n2, final int n3) {
            for (int i = n + this.sampleSizeInBytes - 1; i < n + n3; i += this.sampleSizeInBytes) {
                array[i] = ((array[i] >= 0) ? ((byte)(0x80 | array[i])) : ((byte)(0x7F & array[i])));
            }
        }
        
        private void switchEndian(final byte[] array, final int n, final int n2, final int n3) {
            if (this.sampleSizeInBytes == 2) {
                for (int i = n; i < n + n3; i += this.sampleSizeInBytes) {
                    final byte b = array[i];
                    array[i] = array[i + 1];
                    array[i + 1] = b;
                }
            }
        }
    }
}
