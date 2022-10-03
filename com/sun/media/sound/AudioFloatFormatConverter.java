package com.sun.media.sound;

import java.util.Arrays;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.spi.FormatConversionProvider;

public final class AudioFloatFormatConverter extends FormatConversionProvider
{
    private final AudioFormat.Encoding[] formats;
    
    public AudioFloatFormatConverter() {
        this.formats = new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final AudioFormat.Encoding encoding, final AudioInputStream audioInputStream) {
        if (audioInputStream.getFormat().getEncoding().equals(encoding)) {
            return audioInputStream;
        }
        final AudioFormat format = audioInputStream.getFormat();
        final int channels = format.getChannels();
        final float sampleRate = format.getSampleRate();
        int sampleSizeInBits = format.getSampleSizeInBits();
        final boolean bigEndian = format.isBigEndian();
        if (encoding.equals(AudioFormat.Encoding.PCM_FLOAT)) {
            sampleSizeInBits = 32;
        }
        return this.getAudioInputStream(new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, channels * sampleSizeInBits / 8, sampleRate, bigEndian), audioInputStream);
    }
    
    @Override
    public AudioInputStream getAudioInputStream(final AudioFormat audioFormat, final AudioInputStream audioInputStream) {
        if (!this.isConversionSupported(audioFormat, audioInputStream.getFormat())) {
            throw new IllegalArgumentException("Unsupported conversion: " + audioInputStream.getFormat().toString() + " to " + audioFormat.toString());
        }
        return this.getAudioInputStream(audioFormat, AudioFloatInputStream.getInputStream(audioInputStream));
    }
    
    public AudioInputStream getAudioInputStream(final AudioFormat audioFormat, AudioFloatInputStream audioFloatInputStream) {
        if (!this.isConversionSupported(audioFormat, audioFloatInputStream.getFormat())) {
            throw new IllegalArgumentException("Unsupported conversion: " + audioFloatInputStream.getFormat().toString() + " to " + audioFormat.toString());
        }
        if (audioFormat.getChannels() != audioFloatInputStream.getFormat().getChannels()) {
            audioFloatInputStream = new AudioFloatInputStreamChannelMixer(audioFloatInputStream, audioFormat.getChannels());
        }
        if (Math.abs(audioFormat.getSampleRate() - audioFloatInputStream.getFormat().getSampleRate()) > 1.0E-6) {
            audioFloatInputStream = new AudioFloatInputStreamResampler(audioFloatInputStream, audioFormat);
        }
        return new AudioInputStream(new AudioFloatFormatConverterInputStream(audioFormat, audioFloatInputStream), audioFormat, audioFloatInputStream.getFrameLength());
    }
    
    @Override
    public AudioFormat.Encoding[] getSourceEncodings() {
        return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
    }
    
    @Override
    public AudioFormat.Encoding[] getTargetEncodings() {
        return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
    }
    
    @Override
    public AudioFormat.Encoding[] getTargetEncodings(final AudioFormat audioFormat) {
        if (AudioFloatConverter.getConverter(audioFormat) == null) {
            return new AudioFormat.Encoding[0];
        }
        return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
    }
    
    @Override
    public AudioFormat[] getTargetFormats(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        if (AudioFloatConverter.getConverter(audioFormat) == null) {
            return new AudioFormat[0];
        }
        final int channels = audioFormat.getChannels();
        final ArrayList list = new ArrayList();
        if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, 8, channels, channels, -1.0f, false));
        }
        if (encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0f, 8, channels, channels, -1.0f, false));
        }
        for (int i = 16; i < 32; i += 8) {
            if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, i, channels, channels * i / 8, -1.0f, false));
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, i, channels, channels * i / 8, -1.0f, true));
            }
            if (encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0f, i, channels, channels * i / 8, -1.0f, true));
                list.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0f, i, channels, channels * i / 8, -1.0f, false));
            }
        }
        if (encoding.equals(AudioFormat.Encoding.PCM_FLOAT)) {
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 32, channels, channels * 4, -1.0f, false));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 32, channels, channels * 4, -1.0f, true));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 64, channels, channels * 8, -1.0f, false));
            list.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0f, 64, channels, channels * 8, -1.0f, true));
        }
        return list.toArray(new AudioFormat[list.size()]);
    }
    
    @Override
    public boolean isConversionSupported(final AudioFormat audioFormat, final AudioFormat audioFormat2) {
        return AudioFloatConverter.getConverter(audioFormat2) != null && AudioFloatConverter.getConverter(audioFormat) != null && audioFormat2.getChannels() > 0 && audioFormat.getChannels() > 0;
    }
    
    @Override
    public boolean isConversionSupported(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        if (AudioFloatConverter.getConverter(audioFormat) == null) {
            return false;
        }
        for (int i = 0; i < this.formats.length; ++i) {
            if (encoding.equals(this.formats[i])) {
                return true;
            }
        }
        return false;
    }
    
    private static class AudioFloatFormatConverterInputStream extends InputStream
    {
        private final AudioFloatConverter converter;
        private final AudioFloatInputStream stream;
        private float[] readfloatbuffer;
        private final int fsize;
        
        AudioFloatFormatConverterInputStream(final AudioFormat audioFormat, final AudioFloatInputStream stream) {
            this.stream = stream;
            this.converter = AudioFloatConverter.getConverter(audioFormat);
            this.fsize = (audioFormat.getSampleSizeInBits() + 7) / 8;
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            final int read = this.read(array);
            if (read < 0) {
                return read;
            }
            return array[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            final int n3 = n2 / this.fsize;
            if (this.readfloatbuffer == null || this.readfloatbuffer.length < n3) {
                this.readfloatbuffer = new float[n3];
            }
            final int read = this.stream.read(this.readfloatbuffer, 0, n3);
            if (read < 0) {
                return read;
            }
            this.converter.toByteArray(this.readfloatbuffer, 0, read, array, n);
            return read * this.fsize;
        }
        
        @Override
        public int available() throws IOException {
            final int available = this.stream.available();
            if (available < 0) {
                return available;
            }
            return available * this.fsize;
        }
        
        @Override
        public void close() throws IOException {
            this.stream.close();
        }
        
        @Override
        public synchronized void mark(final int n) {
            this.stream.mark(n * this.fsize);
        }
        
        @Override
        public boolean markSupported() {
            return this.stream.markSupported();
        }
        
        @Override
        public synchronized void reset() throws IOException {
            this.stream.reset();
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final long skip = this.stream.skip(n / this.fsize);
            if (skip < 0L) {
                return skip;
            }
            return skip * this.fsize;
        }
    }
    
    private static class AudioFloatInputStreamChannelMixer extends AudioFloatInputStream
    {
        private final int targetChannels;
        private final int sourceChannels;
        private final AudioFloatInputStream ais;
        private final AudioFormat targetFormat;
        private float[] conversion_buffer;
        
        AudioFloatInputStreamChannelMixer(final AudioFloatInputStream ais, final int targetChannels) {
            this.sourceChannels = ais.getFormat().getChannels();
            this.targetChannels = targetChannels;
            this.ais = ais;
            final AudioFormat format = ais.getFormat();
            this.targetFormat = new AudioFormat(format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(), targetChannels, format.getFrameSize() / this.sourceChannels * targetChannels, format.getFrameRate(), format.isBigEndian());
        }
        
        @Override
        public int available() throws IOException {
            return this.ais.available() / this.sourceChannels * this.targetChannels;
        }
        
        @Override
        public void close() throws IOException {
            this.ais.close();
        }
        
        @Override
        public AudioFormat getFormat() {
            return this.targetFormat;
        }
        
        @Override
        public long getFrameLength() {
            return this.ais.getFrameLength();
        }
        
        @Override
        public void mark(final int n) {
            this.ais.mark(n / this.targetChannels * this.sourceChannels);
        }
        
        @Override
        public boolean markSupported() {
            return this.ais.markSupported();
        }
        
        @Override
        public int read(final float[] array, final int n, final int n2) throws IOException {
            final int n3 = n2 / this.targetChannels * this.sourceChannels;
            if (this.conversion_buffer == null || this.conversion_buffer.length < n3) {
                this.conversion_buffer = new float[n3];
            }
            final int read = this.ais.read(this.conversion_buffer, 0, n3);
            if (read < 0) {
                return read;
            }
            if (this.sourceChannels == 1) {
                final int targetChannels = this.targetChannels;
                for (int i = 0; i < this.targetChannels; ++i) {
                    for (int j = 0, n4 = n + i; j < n3; ++j, n4 += targetChannels) {
                        array[n4] = this.conversion_buffer[j];
                    }
                }
            }
            else if (this.targetChannels == 1) {
                final int sourceChannels = this.sourceChannels;
                for (int k = 0, n5 = n; k < n3; k += sourceChannels, ++n5) {
                    array[n5] = this.conversion_buffer[k];
                }
                for (int l = 1; l < this.sourceChannels; ++l) {
                    for (int n6 = l, n7 = n; n6 < n3; n6 += sourceChannels, ++n7) {
                        final int n8 = n7;
                        array[n8] += this.conversion_buffer[n6];
                    }
                }
                final float n9 = 1.0f / this.sourceChannels;
                for (int n10 = 0, n11 = n; n10 < n3; n10 += sourceChannels, ++n11) {
                    final int n12 = n11;
                    array[n12] *= n9;
                }
            }
            else {
                final int min = Math.min(this.sourceChannels, this.targetChannels);
                final int n13 = n + n2;
                final int targetChannels2 = this.targetChannels;
                final int sourceChannels2 = this.sourceChannels;
                for (int n14 = 0; n14 < min; ++n14) {
                    for (int n15 = n + n14, n16 = n14; n15 < n13; n15 += targetChannels2, n16 += sourceChannels2) {
                        array[n15] = this.conversion_buffer[n16];
                    }
                }
                for (int n17 = min; n17 < this.targetChannels; ++n17) {
                    for (int n18 = n + n17; n18 < n13; n18 += targetChannels2) {
                        array[n18] = 0.0f;
                    }
                }
            }
            return read / this.sourceChannels * this.targetChannels;
        }
        
        @Override
        public void reset() throws IOException {
            this.ais.reset();
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final long skip = this.ais.skip(n / this.targetChannels * this.sourceChannels);
            if (skip < 0L) {
                return skip;
            }
            return skip / this.sourceChannels * this.targetChannels;
        }
    }
    
    private static class AudioFloatInputStreamResampler extends AudioFloatInputStream
    {
        private final AudioFloatInputStream ais;
        private final AudioFormat targetFormat;
        private float[] skipbuffer;
        private SoftAbstractResampler resampler;
        private final float[] pitch;
        private final float[] ibuffer2;
        private final float[][] ibuffer;
        private float ibuffer_index;
        private int ibuffer_len;
        private final int nrofchannels;
        private float[][] cbuffer;
        private final int buffer_len = 512;
        private final int pad;
        private final int pad2;
        private final float[] ix;
        private final int[] ox;
        private float[][] mark_ibuffer;
        private float mark_ibuffer_index;
        private int mark_ibuffer_len;
        
        AudioFloatInputStreamResampler(final AudioFloatInputStream ais, final AudioFormat audioFormat) {
            this.pitch = new float[1];
            this.ibuffer_index = 0.0f;
            this.ibuffer_len = 0;
            this.ix = new float[1];
            this.ox = new int[1];
            this.mark_ibuffer = null;
            this.mark_ibuffer_index = 0.0f;
            this.mark_ibuffer_len = 0;
            this.ais = ais;
            final AudioFormat format = ais.getFormat();
            this.targetFormat = new AudioFormat(format.getEncoding(), audioFormat.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), audioFormat.getSampleRate(), format.isBigEndian());
            this.nrofchannels = this.targetFormat.getChannels();
            final Object property = audioFormat.getProperty("interpolation");
            if (property != null && property instanceof String) {
                final String s = (String)property;
                if (s.equalsIgnoreCase("point")) {
                    this.resampler = new SoftPointResampler();
                }
                if (s.equalsIgnoreCase("linear")) {
                    this.resampler = new SoftLinearResampler2();
                }
                if (s.equalsIgnoreCase("linear1")) {
                    this.resampler = new SoftLinearResampler();
                }
                if (s.equalsIgnoreCase("linear2")) {
                    this.resampler = new SoftLinearResampler2();
                }
                if (s.equalsIgnoreCase("cubic")) {
                    this.resampler = new SoftCubicResampler();
                }
                if (s.equalsIgnoreCase("lanczos")) {
                    this.resampler = new SoftLanczosResampler();
                }
                if (s.equalsIgnoreCase("sinc")) {
                    this.resampler = new SoftSincResampler();
                }
            }
            if (this.resampler == null) {
                this.resampler = new SoftLinearResampler2();
            }
            this.pitch[0] = format.getSampleRate() / audioFormat.getSampleRate();
            this.pad = this.resampler.getPadding();
            this.pad2 = this.pad * 2;
            this.ibuffer = new float[this.nrofchannels][512 + this.pad2];
            this.ibuffer2 = new float[this.nrofchannels * 512];
            this.ibuffer_index = (float)(512 + this.pad);
            this.ibuffer_len = 512;
        }
        
        @Override
        public int available() throws IOException {
            return 0;
        }
        
        @Override
        public void close() throws IOException {
            this.ais.close();
        }
        
        @Override
        public AudioFormat getFormat() {
            return this.targetFormat;
        }
        
        @Override
        public long getFrameLength() {
            return -1L;
        }
        
        @Override
        public void mark(final int n) {
            this.ais.mark((int)(n * this.pitch[0]));
            this.mark_ibuffer_index = this.ibuffer_index;
            this.mark_ibuffer_len = this.ibuffer_len;
            if (this.mark_ibuffer == null) {
                this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length];
            }
            for (int i = 0; i < this.ibuffer.length; ++i) {
                final float[] array = this.ibuffer[i];
                final float[] array2 = this.mark_ibuffer[i];
                for (int j = 0; j < array2.length; ++j) {
                    array2[j] = array[j];
                }
            }
        }
        
        @Override
        public boolean markSupported() {
            return this.ais.markSupported();
        }
        
        private void readNextBuffer() throws IOException {
            if (this.ibuffer_len == -1) {
                return;
            }
            for (int i = 0; i < this.nrofchannels; ++i) {
                final float[] array = this.ibuffer[i];
                for (int n = this.ibuffer_len + this.pad2, j = this.ibuffer_len, n2 = 0; j < n; ++j, ++n2) {
                    array[n2] = array[j];
                }
            }
            this.ibuffer_index -= this.ibuffer_len;
            this.ibuffer_len = this.ais.read(this.ibuffer2);
            if (this.ibuffer_len >= 0) {
                while (this.ibuffer_len < this.ibuffer2.length) {
                    final int read = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
                    if (read == -1) {
                        break;
                    }
                    this.ibuffer_len += read;
                }
                Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0f);
                this.ibuffer_len /= this.nrofchannels;
            }
            else {
                Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0f);
            }
            final int length = this.ibuffer2.length;
            for (int k = 0; k < this.nrofchannels; ++k) {
                final float[] array2 = this.ibuffer[k];
                for (int l = k, pad2 = this.pad2; l < length; l += this.nrofchannels, ++pad2) {
                    array2[pad2] = this.ibuffer2[l];
                }
            }
        }
        
        @Override
        public int read(final float[] array, final int n, final int n2) throws IOException {
            if (this.cbuffer == null || this.cbuffer[0].length < n2 / this.nrofchannels) {
                this.cbuffer = new float[this.nrofchannels][n2 / this.nrofchannels];
            }
            if (this.ibuffer_len == -1) {
                return -1;
            }
            if (n2 < 0) {
                return 0;
            }
            final int n3 = n + n2;
            int i = n2 / this.nrofchannels;
            int n4 = 0;
            int n5 = this.ibuffer_len;
            while (i > 0) {
                if (this.ibuffer_len >= 0) {
                    if (this.ibuffer_index >= this.ibuffer_len + this.pad) {
                        this.readNextBuffer();
                    }
                    n5 = this.ibuffer_len + this.pad;
                }
                if (this.ibuffer_len < 0) {
                    n5 = this.pad2;
                    if (this.ibuffer_index >= n5) {
                        break;
                    }
                }
                if (this.ibuffer_index < 0.0f) {
                    break;
                }
                final int n6 = n4;
                for (int j = 0; j < this.nrofchannels; ++j) {
                    this.ix[0] = this.ibuffer_index;
                    this.ox[0] = n4;
                    this.resampler.interpolate(this.ibuffer[j], this.ix, (float)n5, this.pitch, 0.0f, this.cbuffer[j], this.ox, n2 / this.nrofchannels);
                }
                this.ibuffer_index = this.ix[0];
                n4 = this.ox[0];
                i -= n4 - n6;
            }
            for (int k = 0; k < this.nrofchannels; ++k) {
                int n7 = 0;
                final float[] array2 = this.cbuffer[k];
                for (int l = k + n; l < n3; l += this.nrofchannels) {
                    array[l] = array2[n7++];
                }
            }
            return n2 - i * this.nrofchannels;
        }
        
        @Override
        public void reset() throws IOException {
            this.ais.reset();
            if (this.mark_ibuffer == null) {
                return;
            }
            this.ibuffer_index = this.mark_ibuffer_index;
            this.ibuffer_len = this.mark_ibuffer_len;
            for (int i = 0; i < this.ibuffer.length; ++i) {
                final float[] array = this.mark_ibuffer[i];
                final float[] array2 = this.ibuffer[i];
                for (int j = 0; j < array2.length; ++j) {
                    array2[j] = array[j];
                }
            }
        }
        
        @Override
        public long skip(final long n) throws IOException {
            if (n < 0L) {
                return 0L;
            }
            if (this.skipbuffer == null) {
                this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()];
            }
            final float[] skipbuffer = this.skipbuffer;
            long n2 = n;
            while (n2 > 0L) {
                final int read = this.read(skipbuffer, 0, (int)Math.min(n2, this.skipbuffer.length));
                if (read < 0) {
                    if (n2 == n) {
                        return read;
                    }
                    break;
                }
                else {
                    n2 -= read;
                }
            }
            return n - n2;
        }
    }
}
