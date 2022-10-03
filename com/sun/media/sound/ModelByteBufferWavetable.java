package com.sun.media.sound;

import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;

public final class ModelByteBufferWavetable implements ModelWavetable
{
    private float loopStart;
    private float loopLength;
    private final ModelByteBuffer buffer;
    private ModelByteBuffer buffer8;
    private AudioFormat format;
    private float pitchcorrection;
    private float attenuation;
    private int loopType;
    
    public ModelByteBufferWavetable(final ModelByteBuffer buffer) {
        this.loopStart = -1.0f;
        this.loopLength = -1.0f;
        this.buffer8 = null;
        this.format = null;
        this.pitchcorrection = 0.0f;
        this.attenuation = 0.0f;
        this.loopType = 0;
        this.buffer = buffer;
    }
    
    public ModelByteBufferWavetable(final ModelByteBuffer buffer, final float pitchcorrection) {
        this.loopStart = -1.0f;
        this.loopLength = -1.0f;
        this.buffer8 = null;
        this.format = null;
        this.pitchcorrection = 0.0f;
        this.attenuation = 0.0f;
        this.loopType = 0;
        this.buffer = buffer;
        this.pitchcorrection = pitchcorrection;
    }
    
    public ModelByteBufferWavetable(final ModelByteBuffer buffer, final AudioFormat format) {
        this.loopStart = -1.0f;
        this.loopLength = -1.0f;
        this.buffer8 = null;
        this.format = null;
        this.pitchcorrection = 0.0f;
        this.attenuation = 0.0f;
        this.loopType = 0;
        this.format = format;
        this.buffer = buffer;
    }
    
    public ModelByteBufferWavetable(final ModelByteBuffer buffer, final AudioFormat format, final float pitchcorrection) {
        this.loopStart = -1.0f;
        this.loopLength = -1.0f;
        this.buffer8 = null;
        this.format = null;
        this.pitchcorrection = 0.0f;
        this.attenuation = 0.0f;
        this.loopType = 0;
        this.format = format;
        this.buffer = buffer;
        this.pitchcorrection = pitchcorrection;
    }
    
    public void set8BitExtensionBuffer(final ModelByteBuffer buffer8) {
        this.buffer8 = buffer8;
    }
    
    public ModelByteBuffer get8BitExtensionBuffer() {
        return this.buffer8;
    }
    
    public ModelByteBuffer getBuffer() {
        return this.buffer;
    }
    
    public AudioFormat getFormat() {
        if (this.format != null) {
            return this.format;
        }
        if (this.buffer == null) {
            return null;
        }
        final InputStream inputStream = this.buffer.getInputStream();
        AudioFormat format = null;
        try {
            format = AudioSystem.getAudioFileFormat(inputStream).getFormat();
        }
        catch (final Exception ex) {}
        try {
            inputStream.close();
        }
        catch (final IOException ex2) {}
        return format;
    }
    
    @Override
    public AudioFloatInputStream openStream() {
        if (this.buffer == null) {
            return null;
        }
        if (this.format == null) {
            final InputStream inputStream = this.buffer.getInputStream();
            AudioInputStream audioInputStream;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(inputStream);
            }
            catch (final Exception ex) {
                return null;
            }
            return AudioFloatInputStream.getInputStream(audioInputStream);
        }
        if (this.buffer.array() == null) {
            return AudioFloatInputStream.getInputStream(new AudioInputStream(this.buffer.getInputStream(), this.format, this.buffer.capacity() / this.format.getFrameSize()));
        }
        if (this.buffer8 != null && (this.format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || this.format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) {
            return AudioFloatInputStream.getInputStream(new AudioInputStream(new Buffer8PlusInputStream(), new AudioFormat(this.format.getEncoding(), this.format.getSampleRate(), this.format.getSampleSizeInBits() + 8, this.format.getChannels(), this.format.getFrameSize() + 1 * this.format.getChannels(), this.format.getFrameRate(), this.format.isBigEndian()), this.buffer.capacity() / this.format.getFrameSize()));
        }
        return AudioFloatInputStream.getInputStream(this.format, this.buffer.array(), (int)this.buffer.arrayOffset(), (int)this.buffer.capacity());
    }
    
    @Override
    public int getChannels() {
        return this.getFormat().getChannels();
    }
    
    @Override
    public ModelOscillatorStream open(final float n) {
        return null;
    }
    
    @Override
    public float getAttenuation() {
        return this.attenuation;
    }
    
    public void setAttenuation(final float attenuation) {
        this.attenuation = attenuation;
    }
    
    @Override
    public float getLoopLength() {
        return this.loopLength;
    }
    
    public void setLoopLength(final float loopLength) {
        this.loopLength = loopLength;
    }
    
    @Override
    public float getLoopStart() {
        return this.loopStart;
    }
    
    public void setLoopStart(final float loopStart) {
        this.loopStart = loopStart;
    }
    
    public void setLoopType(final int loopType) {
        this.loopType = loopType;
    }
    
    @Override
    public int getLoopType() {
        return this.loopType;
    }
    
    @Override
    public float getPitchcorrection() {
        return this.pitchcorrection;
    }
    
    public void setPitchcorrection(final float pitchcorrection) {
        this.pitchcorrection = pitchcorrection;
    }
    
    private class Buffer8PlusInputStream extends InputStream
    {
        private final boolean bigendian;
        private final int framesize_pc;
        int pos;
        int pos2;
        int markpos;
        int markpos2;
        
        Buffer8PlusInputStream() {
            this.pos = 0;
            this.pos2 = 0;
            this.markpos = 0;
            this.markpos2 = 0;
            this.framesize_pc = ModelByteBufferWavetable.this.format.getFrameSize() / ModelByteBufferWavetable.this.format.getChannels();
            this.bigendian = ModelByteBufferWavetable.this.format.isBigEndian();
        }
        
        @Override
        public int read(final byte[] array, final int n, int n2) throws IOException {
            final int available = this.available();
            if (available <= 0) {
                return -1;
            }
            if (n2 > available) {
                n2 = available;
            }
            final byte[] array2 = ModelByteBufferWavetable.this.buffer.array();
            final byte[] array3 = ModelByteBufferWavetable.this.buffer8.array();
            this.pos += (int)ModelByteBufferWavetable.this.buffer.arrayOffset();
            this.pos2 += (int)ModelByteBufferWavetable.this.buffer8.arrayOffset();
            if (this.bigendian) {
                for (int i = 0; i < n2; i += this.framesize_pc + 1) {
                    System.arraycopy(array2, this.pos, array, i, this.framesize_pc);
                    System.arraycopy(array3, this.pos2, array, i + this.framesize_pc, 1);
                    this.pos += this.framesize_pc;
                    ++this.pos2;
                }
            }
            else {
                for (int j = 0; j < n2; j += this.framesize_pc + 1) {
                    System.arraycopy(array3, this.pos2, array, j, 1);
                    System.arraycopy(array2, this.pos, array, j + 1, this.framesize_pc);
                    this.pos += this.framesize_pc;
                    ++this.pos2;
                }
            }
            this.pos -= (int)ModelByteBufferWavetable.this.buffer.arrayOffset();
            this.pos2 -= (int)ModelByteBufferWavetable.this.buffer8.arrayOffset();
            return n2;
        }
        
        @Override
        public long skip(long n) throws IOException {
            final int available = this.available();
            if (available <= 0) {
                return -1L;
            }
            if (n > available) {
                n = available;
            }
            this.pos += (int)(n / (this.framesize_pc + 1) * this.framesize_pc);
            this.pos2 += (int)(n / (this.framesize_pc + 1));
            return super.skip(n);
        }
        
        @Override
        public int read(final byte[] array) throws IOException {
            return this.read(array, 0, array.length);
        }
        
        @Override
        public int read() throws IOException {
            if (this.read(new byte[1], 0, 1) == -1) {
                return -1;
            }
            return 0;
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public int available() throws IOException {
            return (int)ModelByteBufferWavetable.this.buffer.capacity() + (int)ModelByteBufferWavetable.this.buffer8.capacity() - this.pos - this.pos2;
        }
        
        @Override
        public synchronized void mark(final int n) {
            this.markpos = this.pos;
            this.markpos2 = this.pos2;
        }
        
        @Override
        public synchronized void reset() throws IOException {
            this.pos = this.markpos;
            this.pos2 = this.markpos2;
        }
    }
}
