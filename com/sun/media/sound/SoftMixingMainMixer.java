package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;

public final class SoftMixingMainMixer
{
    public static final int CHANNEL_LEFT = 0;
    public static final int CHANNEL_RIGHT = 1;
    public static final int CHANNEL_EFFECT1 = 2;
    public static final int CHANNEL_EFFECT2 = 3;
    public static final int CHANNEL_EFFECT3 = 4;
    public static final int CHANNEL_EFFECT4 = 5;
    public static final int CHANNEL_LEFT_DRY = 10;
    public static final int CHANNEL_RIGHT_DRY = 11;
    public static final int CHANNEL_SCRATCH1 = 12;
    public static final int CHANNEL_SCRATCH2 = 13;
    public static final int CHANNEL_CHANNELMIXER_LEFT = 14;
    public static final int CHANNEL_CHANNELMIXER_RIGHT = 15;
    private final SoftMixingMixer mixer;
    private final AudioInputStream ais;
    private final SoftAudioBuffer[] buffers;
    private final SoftAudioProcessor reverb;
    private final SoftAudioProcessor chorus;
    private final SoftAudioProcessor agc;
    private final int nrofchannels;
    private final Object control_mutex;
    private final List<SoftMixingDataLine> openLinesList;
    private SoftMixingDataLine[] openLines;
    
    public AudioInputStream getInputStream() {
        return this.ais;
    }
    
    void processAudioBuffers() {
        for (int i = 0; i < this.buffers.length; ++i) {
            this.buffers[i].clear();
        }
        final SoftMixingDataLine[] openLines;
        synchronized (this.control_mutex) {
            openLines = this.openLines;
            for (int j = 0; j < openLines.length; ++j) {
                openLines[j].processControlLogic();
            }
            this.chorus.processControlLogic();
            this.reverb.processControlLogic();
            this.agc.processControlLogic();
        }
        for (int k = 0; k < openLines.length; ++k) {
            openLines[k].processAudioLogic(this.buffers);
        }
        this.chorus.processAudio();
        this.reverb.processAudio();
        this.agc.processAudio();
    }
    
    public SoftMixingMainMixer(final SoftMixingMixer mixer) {
        this.openLinesList = new ArrayList<SoftMixingDataLine>();
        this.openLines = new SoftMixingDataLine[0];
        this.mixer = mixer;
        this.nrofchannels = mixer.getFormat().getChannels();
        final int n = (int)(mixer.getFormat().getSampleRate() / mixer.getControlRate());
        this.control_mutex = mixer.control_mutex;
        this.buffers = new SoftAudioBuffer[16];
        for (int i = 0; i < this.buffers.length; ++i) {
            this.buffers[i] = new SoftAudioBuffer(n, mixer.getFormat());
        }
        this.reverb = new SoftReverb();
        this.chorus = new SoftChorus();
        this.agc = new SoftLimiter();
        final float sampleRate = mixer.getFormat().getSampleRate();
        final float controlRate = mixer.getControlRate();
        this.reverb.init(sampleRate, controlRate);
        this.chorus.init(sampleRate, controlRate);
        this.agc.init(sampleRate, controlRate);
        this.reverb.setMixMode(true);
        this.chorus.setMixMode(true);
        this.agc.setMixMode(false);
        this.chorus.setInput(0, this.buffers[3]);
        this.chorus.setOutput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.chorus.setOutput(1, this.buffers[1]);
        }
        this.chorus.setOutput(2, this.buffers[2]);
        this.reverb.setInput(0, this.buffers[2]);
        this.reverb.setOutput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.reverb.setOutput(1, this.buffers[1]);
        }
        this.agc.setInput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.agc.setInput(1, this.buffers[1]);
        }
        this.agc.setOutput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.agc.setOutput(1, this.buffers[1]);
        }
        this.ais = new AudioInputStream(new InputStream() {
            private final SoftAudioBuffer[] buffers = SoftMixingMainMixer.this.buffers;
            private final int nrofchannels = SoftMixingMainMixer.this.mixer.getFormat().getChannels();
            private final int buffersize = this.buffers[0].getSize();
            private final byte[] bbuffer = new byte[this.buffersize * (SoftMixingMainMixer.this.mixer.getFormat().getSampleSizeInBits() / 8) * this.nrofchannels];
            private int bbuffer_pos = 0;
            private final byte[] single = new byte[1];
            
            public void fillBuffer() {
                SoftMixingMainMixer.this.processAudioBuffers();
                for (int i = 0; i < this.nrofchannels; ++i) {
                    this.buffers[i].get(this.bbuffer, i);
                }
                this.bbuffer_pos = 0;
            }
            
            @Override
            public int read(final byte[] array, int i, final int n) {
                final int length = this.bbuffer.length;
                final int n2 = i + n;
                final byte[] bbuffer = this.bbuffer;
                while (i < n2) {
                    if (this.available() == 0) {
                        this.fillBuffer();
                    }
                    else {
                        int bbuffer_pos;
                        for (bbuffer_pos = this.bbuffer_pos; i < n2 && bbuffer_pos < length; array[i++] = bbuffer[bbuffer_pos++]) {}
                        this.bbuffer_pos = bbuffer_pos;
                    }
                }
                return n;
            }
            
            @Override
            public int read() throws IOException {
                if (this.read(this.single) == -1) {
                    return -1;
                }
                return this.single[0] & 0xFF;
            }
            
            @Override
            public int available() {
                return this.bbuffer.length - this.bbuffer_pos;
            }
            
            @Override
            public void close() {
                SoftMixingMainMixer.this.mixer.close();
            }
        }, mixer.getFormat(), -1L);
    }
    
    public void openLine(final SoftMixingDataLine softMixingDataLine) {
        synchronized (this.control_mutex) {
            this.openLinesList.add(softMixingDataLine);
            this.openLines = this.openLinesList.toArray(new SoftMixingDataLine[this.openLinesList.size()]);
        }
    }
    
    public void closeLine(final SoftMixingDataLine softMixingDataLine) {
        synchronized (this.control_mutex) {
            this.openLinesList.remove(softMixingDataLine);
            this.openLines = this.openLinesList.toArray(new SoftMixingDataLine[this.openLinesList.size()]);
            if (this.openLines.length == 0 && this.mixer.implicitOpen) {
                this.mixer.close();
            }
        }
    }
    
    public SoftMixingDataLine[] getOpenLines() {
        synchronized (this.control_mutex) {
            return this.openLines;
        }
    }
    
    public void close() {
        final SoftMixingDataLine[] openLines = this.openLines;
        for (int i = 0; i < openLines.length; ++i) {
            openLines[i].close();
        }
    }
}
