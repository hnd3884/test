package com.sun.media.sound;

import java.util.Arrays;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.midi.Soundbank;
import javax.sound.sampled.AudioFormat;
import javax.sound.midi.SoundbankResource;

public final class DLSSample extends SoundbankResource
{
    byte[] guid;
    DLSInfo info;
    DLSSampleOptions sampleoptions;
    ModelByteBuffer data;
    AudioFormat format;
    
    public DLSSample(final Soundbank soundbank) {
        super(soundbank, null, AudioInputStream.class);
        this.guid = null;
        this.info = new DLSInfo();
    }
    
    public DLSSample() {
        super(null, null, AudioInputStream.class);
        this.guid = null;
        this.info = new DLSInfo();
    }
    
    public DLSInfo getInfo() {
        return this.info;
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
    
    public AudioFormat getFormat() {
        return this.format;
    }
    
    public void setFormat(final AudioFormat format) {
        this.format = format;
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
    
    @Override
    public String getName() {
        return this.info.name;
    }
    
    public void setName(final String name) {
        this.info.name = name;
    }
    
    public DLSSampleOptions getSampleoptions() {
        return this.sampleoptions;
    }
    
    public void setSampleoptions(final DLSSampleOptions sampleoptions) {
        this.sampleoptions = sampleoptions;
    }
    
    @Override
    public String toString() {
        return "Sample: " + this.info.name;
    }
    
    public byte[] getGuid() {
        return (byte[])((this.guid == null) ? null : Arrays.copyOf(this.guid, this.guid.length));
    }
    
    public void setGuid(final byte[] array) {
        this.guid = (byte[])((array == null) ? null : Arrays.copyOf(array, array.length));
    }
}
