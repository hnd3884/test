package com.sun.media.sound;

import javax.sound.sampled.BooleanControl;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import java.util.Arrays;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import java.util.ArrayList;
import javax.sound.sampled.LineListener;
import java.util.List;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.DataLine;

public abstract class SoftMixingDataLine implements DataLine
{
    public static final FloatControl.Type CHORUS_SEND;
    private final Gain gain_control;
    private final Mute mute_control;
    private final Balance balance_control;
    private final Pan pan_control;
    private final ReverbSend reverbsend_control;
    private final ChorusSend chorussend_control;
    private final ApplyReverb apply_reverb;
    private final Control[] controls;
    float leftgain;
    float rightgain;
    float eff1gain;
    float eff2gain;
    List<LineListener> listeners;
    final Object control_mutex;
    SoftMixingMixer mixer;
    Info info;
    
    protected abstract void processControlLogic();
    
    protected abstract void processAudioLogic(final SoftAudioBuffer[] p0);
    
    SoftMixingDataLine(final SoftMixingMixer mixer, final Info info) {
        this.gain_control = new Gain();
        this.mute_control = new Mute();
        this.balance_control = new Balance();
        this.pan_control = new Pan();
        this.reverbsend_control = new ReverbSend();
        this.chorussend_control = new ChorusSend();
        this.apply_reverb = new ApplyReverb();
        this.leftgain = 1.0f;
        this.rightgain = 1.0f;
        this.eff1gain = 0.0f;
        this.eff2gain = 0.0f;
        this.listeners = new ArrayList<LineListener>();
        this.mixer = mixer;
        this.info = info;
        this.control_mutex = mixer.control_mutex;
        this.controls = new Control[] { this.gain_control, this.mute_control, this.balance_control, this.pan_control, this.reverbsend_control, this.chorussend_control, this.apply_reverb };
        this.calcVolume();
    }
    
    final void calcVolume() {
        synchronized (this.control_mutex) {
            double pow = Math.pow(10.0, this.gain_control.getValue() / 20.0);
            if (this.mute_control.getValue()) {
                pow = 0.0;
            }
            this.leftgain = (float)pow;
            this.rightgain = (float)pow;
            if (this.mixer.getFormat().getChannels() > 1) {
                final double n = this.balance_control.getValue();
                if (n > 0.0) {
                    this.leftgain *= (float)(1.0 - n);
                }
                else {
                    this.rightgain *= (float)(1.0 + n);
                }
            }
        }
        this.eff1gain = (float)Math.pow(10.0, this.reverbsend_control.getValue() / 20.0);
        this.eff2gain = (float)Math.pow(10.0, this.chorussend_control.getValue() / 20.0);
        if (!this.apply_reverb.getValue()) {
            this.eff1gain = 0.0f;
        }
    }
    
    final void sendEvent(final LineEvent lineEvent) {
        if (this.listeners.size() == 0) {
            return;
        }
        final LineListener[] array = this.listeners.toArray(new LineListener[this.listeners.size()]);
        for (int length = array.length, i = 0; i < length; ++i) {
            array[i].update(lineEvent);
        }
    }
    
    @Override
    public final void addLineListener(final LineListener lineListener) {
        synchronized (this.control_mutex) {
            this.listeners.add(lineListener);
        }
    }
    
    @Override
    public final void removeLineListener(final LineListener lineListener) {
        synchronized (this.control_mutex) {
            this.listeners.add(lineListener);
        }
    }
    
    @Override
    public final Line.Info getLineInfo() {
        return this.info;
    }
    
    @Override
    public final Control getControl(final Control.Type type) {
        if (type != null) {
            for (int i = 0; i < this.controls.length; ++i) {
                if (this.controls[i].getType() == type) {
                    return this.controls[i];
                }
            }
        }
        throw new IllegalArgumentException("Unsupported control type : " + type);
    }
    
    @Override
    public final Control[] getControls() {
        return Arrays.copyOf(this.controls, this.controls.length);
    }
    
    @Override
    public final boolean isControlSupported(final Control.Type type) {
        if (type != null) {
            for (int i = 0; i < this.controls.length; ++i) {
                if (this.controls[i].getType() == type) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        CHORUS_SEND = new FloatControl.Type("Chorus Send") {};
    }
    
    protected static final class AudioFloatInputStreamResampler extends AudioFloatInputStream
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
        private int nrofchannels;
        private float[][] cbuffer;
        private final int buffer_len = 512;
        private final int pad;
        private final int pad2;
        private final float[] ix;
        private final int[] ox;
        private float[][] mark_ibuffer;
        private float mark_ibuffer_index;
        private int mark_ibuffer_len;
        
        public AudioFloatInputStreamResampler(final AudioFloatInputStream ais, final AudioFormat audioFormat) {
            this.pitch = new float[1];
            this.ibuffer_index = 0.0f;
            this.ibuffer_len = 0;
            this.nrofchannels = 0;
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
            int i = n2 / this.nrofchannels;
            int n3 = 0;
            int n4 = this.ibuffer_len;
            while (i > 0) {
                if (this.ibuffer_len >= 0) {
                    if (this.ibuffer_index >= this.ibuffer_len + this.pad) {
                        this.readNextBuffer();
                    }
                    n4 = this.ibuffer_len + this.pad;
                }
                if (this.ibuffer_len < 0) {
                    n4 = this.pad2;
                    if (this.ibuffer_index >= n4) {
                        break;
                    }
                }
                if (this.ibuffer_index < 0.0f) {
                    break;
                }
                final int n5 = n3;
                for (int j = 0; j < this.nrofchannels; ++j) {
                    this.ix[0] = this.ibuffer_index;
                    this.ox[0] = n3;
                    this.resampler.interpolate(this.ibuffer[j], this.ix, (float)n4, this.pitch, 0.0f, this.cbuffer[j], this.ox, n2 / this.nrofchannels);
                }
                this.ibuffer_index = this.ix[0];
                n3 = this.ox[0];
                i -= n3 - n5;
            }
            for (int k = 0; k < this.nrofchannels; ++k) {
                int n6 = 0;
                final float[] array2 = this.cbuffer[k];
                for (int l = k; l < array.length; l += this.nrofchannels) {
                    array[l] = array2[n6++];
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
            if (n > 0L) {
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
    
    private final class Gain extends FloatControl
    {
        private Gain() {
            super(Type.MASTER_GAIN, -80.0f, 6.0206f, 0.625f, -1, 0.0f, "dB", "Minimum", "", "Maximum");
        }
        
        @Override
        public void setValue(final float value) {
            super.setValue(value);
            SoftMixingDataLine.this.calcVolume();
        }
    }
    
    private final class Mute extends BooleanControl
    {
        private Mute() {
            super(Type.MUTE, false, "True", "False");
        }
        
        @Override
        public void setValue(final boolean value) {
            super.setValue(value);
            SoftMixingDataLine.this.calcVolume();
        }
    }
    
    private final class ApplyReverb extends BooleanControl
    {
        private ApplyReverb() {
            super(Type.APPLY_REVERB, false, "True", "False");
        }
        
        @Override
        public void setValue(final boolean value) {
            super.setValue(value);
            SoftMixingDataLine.this.calcVolume();
        }
    }
    
    private final class Balance extends FloatControl
    {
        private Balance() {
            super(Type.BALANCE, -1.0f, 1.0f, 0.0078125f, -1, 0.0f, "", "Left", "Center", "Right");
        }
        
        @Override
        public void setValue(final float value) {
            super.setValue(value);
            SoftMixingDataLine.this.calcVolume();
        }
    }
    
    private final class Pan extends FloatControl
    {
        private Pan() {
            super(Type.PAN, -1.0f, 1.0f, 0.0078125f, -1, 0.0f, "", "Left", "Center", "Right");
        }
        
        @Override
        public void setValue(final float n) {
            super.setValue(n);
            SoftMixingDataLine.this.balance_control.setValue(n);
        }
        
        @Override
        public float getValue() {
            return SoftMixingDataLine.this.balance_control.getValue();
        }
    }
    
    private final class ReverbSend extends FloatControl
    {
        private ReverbSend() {
            super(Type.REVERB_SEND, -80.0f, 6.0206f, 0.625f, -1, -80.0f, "dB", "Minimum", "", "Maximum");
        }
        
        @Override
        public void setValue(final float n) {
            super.setValue(n);
            SoftMixingDataLine.this.balance_control.setValue(n);
        }
    }
    
    private final class ChorusSend extends FloatControl
    {
        private ChorusSend() {
            super(SoftMixingDataLine.CHORUS_SEND, -80.0f, 6.0206f, 0.625f, -1, -80.0f, "dB", "Minimum", "", "Maximum");
        }
        
        @Override
        public void setValue(final float n) {
            super.setValue(n);
            SoftMixingDataLine.this.balance_control.setValue(n);
        }
    }
}
