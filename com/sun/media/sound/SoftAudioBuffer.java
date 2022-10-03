package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.sampled.AudioFormat;

public final class SoftAudioBuffer
{
    private int size;
    private float[] buffer;
    private boolean empty;
    private AudioFormat format;
    private AudioFloatConverter converter;
    private byte[] converter_buffer;
    
    public SoftAudioBuffer(final int size, final AudioFormat format) {
        this.empty = true;
        this.size = size;
        this.format = format;
        this.converter = AudioFloatConverter.getConverter(format);
    }
    
    public void swap(final SoftAudioBuffer softAudioBuffer) {
        final int size = this.size;
        final float[] buffer = this.buffer;
        final boolean empty = this.empty;
        final AudioFormat format = this.format;
        final AudioFloatConverter converter = this.converter;
        final byte[] converter_buffer = this.converter_buffer;
        this.size = softAudioBuffer.size;
        this.buffer = softAudioBuffer.buffer;
        this.empty = softAudioBuffer.empty;
        this.format = softAudioBuffer.format;
        this.converter = softAudioBuffer.converter;
        this.converter_buffer = softAudioBuffer.converter_buffer;
        softAudioBuffer.size = size;
        softAudioBuffer.buffer = buffer;
        softAudioBuffer.empty = empty;
        softAudioBuffer.format = format;
        softAudioBuffer.converter = converter;
        softAudioBuffer.converter_buffer = converter_buffer;
    }
    
    public AudioFormat getFormat() {
        return this.format;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void clear() {
        if (!this.empty) {
            Arrays.fill(this.buffer, 0.0f);
            this.empty = true;
        }
    }
    
    public boolean isSilent() {
        return this.empty;
    }
    
    public float[] array() {
        this.empty = false;
        if (this.buffer == null) {
            this.buffer = new float[this.size];
        }
        return this.buffer;
    }
    
    public void get(final byte[] array, final int n) {
        final int n2 = this.format.getFrameSize() / this.format.getChannels();
        final int n3 = this.size * n2;
        if (this.converter_buffer == null || this.converter_buffer.length < n3) {
            this.converter_buffer = new byte[n3];
        }
        if (this.format.getChannels() == 1) {
            this.converter.toByteArray(this.array(), this.size, array);
        }
        else {
            this.converter.toByteArray(this.array(), this.size, this.converter_buffer);
            if (n >= this.format.getChannels()) {
                return;
            }
            final int n4 = this.format.getChannels() * n2;
            final int n5 = n2;
            for (int i = 0; i < n2; ++i) {
                int n6 = i;
                int n7 = n * n2 + i;
                for (int j = 0; j < this.size; ++j) {
                    array[n7] = this.converter_buffer[n6];
                    n7 += n4;
                    n6 += n5;
                }
            }
        }
    }
}
