package com.sun.media.sound;

import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public final class SF2Sample extends SoundbankResource
{
    String name;
    long startLoop;
    long endLoop;
    long sampleRate;
    int originalPitch;
    byte pitchCorrection;
    int sampleLink;
    int sampleType;
    ModelByteBuffer data;
    ModelByteBuffer data24;
    
    public SF2Sample(final Soundbank soundbank) {
        super(soundbank, null, AudioInputStream.class);
        this.name = "";
        this.startLoop = 0L;
        this.endLoop = 0L;
        this.sampleRate = 44100L;
        this.originalPitch = 60;
        this.pitchCorrection = 0;
        this.sampleLink = 0;
        this.sampleType = 0;
    }
    
    public SF2Sample() {
        super(null, null, AudioInputStream.class);
        this.name = "";
        this.startLoop = 0L;
        this.endLoop = 0L;
        this.sampleRate = 44100L;
        this.originalPitch = 60;
        this.pitchCorrection = 0;
        this.sampleLink = 0;
        this.sampleType = 0;
    }
    
    @Override
    public Object getData() {
        final AudioFormat format = this.getFormat();
        final InputStream inputStream = this.data.getInputStream();
        if (inputStream == null) {
            return null;
        }
        return new AudioInputStream(inputStream, format, this.data.capacity());
    }
    
    public ModelByteBuffer getDataBuffer() {
        return this.data;
    }
    
    public ModelByteBuffer getData24Buffer() {
        return this.data24;
    }
    
    public AudioFormat getFormat() {
        return new AudioFormat((float)this.sampleRate, 16, 1, true, false);
    }
    
    public void setData(final ModelByteBuffer data) {
        this.data = data;
    }
    
    public void setData(final byte[] array) {
        this.data = new ModelByteBuffer(array);
    }
    
    public void setData(final byte[] array, final int n, final int n2) {
        this.data = new ModelByteBuffer(array, n, n2);
    }
    
    public void setData24(final ModelByteBuffer data24) {
        this.data24 = data24;
    }
    
    public void setData24(final byte[] array) {
        this.data24 = new ModelByteBuffer(array);
    }
    
    public void setData24(final byte[] array, final int n, final int n2) {
        this.data24 = new ModelByteBuffer(array, n, n2);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public long getEndLoop() {
        return this.endLoop;
    }
    
    public void setEndLoop(final long endLoop) {
        this.endLoop = endLoop;
    }
    
    public int getOriginalPitch() {
        return this.originalPitch;
    }
    
    public void setOriginalPitch(final int originalPitch) {
        this.originalPitch = originalPitch;
    }
    
    public byte getPitchCorrection() {
        return this.pitchCorrection;
    }
    
    public void setPitchCorrection(final byte pitchCorrection) {
        this.pitchCorrection = pitchCorrection;
    }
    
    public int getSampleLink() {
        return this.sampleLink;
    }
    
    public void setSampleLink(final int sampleLink) {
        this.sampleLink = sampleLink;
    }
    
    public long getSampleRate() {
        return this.sampleRate;
    }
    
    public void setSampleRate(final long sampleRate) {
        this.sampleRate = sampleRate;
    }
    
    public int getSampleType() {
        return this.sampleType;
    }
    
    public void setSampleType(final int sampleType) {
        this.sampleType = sampleType;
    }
    
    public long getStartLoop() {
        return this.startLoop;
    }
    
    public void setStartLoop(final long startLoop) {
        this.startLoop = startLoop;
    }
    
    @Override
    public String toString() {
        return "Sample: " + this.name;
    }
}
