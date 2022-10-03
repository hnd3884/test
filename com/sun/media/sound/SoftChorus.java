package com.sun.media.sound;

import java.util.Arrays;

public final class SoftChorus implements SoftAudioProcessor
{
    private boolean mix;
    private SoftAudioBuffer inputA;
    private SoftAudioBuffer left;
    private SoftAudioBuffer right;
    private SoftAudioBuffer reverb;
    private LFODelay vdelay1L;
    private LFODelay vdelay1R;
    private float rgain;
    private boolean dirty;
    private double dirty_vdelay1L_rate;
    private double dirty_vdelay1R_rate;
    private double dirty_vdelay1L_depth;
    private double dirty_vdelay1R_depth;
    private float dirty_vdelay1L_feedback;
    private float dirty_vdelay1R_feedback;
    private float dirty_vdelay1L_reverbsendgain;
    private float dirty_vdelay1R_reverbsendgain;
    private float controlrate;
    double silentcounter;
    
    public SoftChorus() {
        this.mix = true;
        this.rgain = 0.0f;
        this.dirty = true;
        this.silentcounter = 1000.0;
    }
    
    @Override
    public void init(final float n, final float controlrate) {
        this.controlrate = controlrate;
        this.vdelay1L = new LFODelay(n, controlrate);
        this.vdelay1R = new LFODelay(n, controlrate);
        this.vdelay1L.setGain(1.0f);
        this.vdelay1R.setGain(1.0f);
        this.vdelay1L.setPhase(1.5707963267948966);
        this.vdelay1R.setPhase(0.0);
        this.globalParameterControlChange(new int[] { 130 }, 0L, 2L);
    }
    
    @Override
    public void globalParameterControlChange(final int[] array, final long n, final long n2) {
        if (array.length == 1 && array[0] == 130) {
            if (n == 0L) {
                switch ((int)n2) {
                    case 0: {
                        this.globalParameterControlChange(array, 3L, 0L);
                        this.globalParameterControlChange(array, 1L, 3L);
                        this.globalParameterControlChange(array, 2L, 5L);
                        this.globalParameterControlChange(array, 4L, 0L);
                        break;
                    }
                    case 1: {
                        this.globalParameterControlChange(array, 3L, 5L);
                        this.globalParameterControlChange(array, 1L, 9L);
                        this.globalParameterControlChange(array, 2L, 19L);
                        this.globalParameterControlChange(array, 4L, 0L);
                        break;
                    }
                    case 2: {
                        this.globalParameterControlChange(array, 3L, 8L);
                        this.globalParameterControlChange(array, 1L, 3L);
                        this.globalParameterControlChange(array, 2L, 19L);
                        this.globalParameterControlChange(array, 4L, 0L);
                        break;
                    }
                    case 3: {
                        this.globalParameterControlChange(array, 3L, 16L);
                        this.globalParameterControlChange(array, 1L, 9L);
                        this.globalParameterControlChange(array, 2L, 16L);
                        this.globalParameterControlChange(array, 4L, 0L);
                        break;
                    }
                    case 4: {
                        this.globalParameterControlChange(array, 3L, 64L);
                        this.globalParameterControlChange(array, 1L, 2L);
                        this.globalParameterControlChange(array, 2L, 24L);
                        this.globalParameterControlChange(array, 4L, 0L);
                        break;
                    }
                    case 5: {
                        this.globalParameterControlChange(array, 3L, 112L);
                        this.globalParameterControlChange(array, 1L, 1L);
                        this.globalParameterControlChange(array, 2L, 5L);
                        this.globalParameterControlChange(array, 4L, 0L);
                        break;
                    }
                }
            }
            else if (n == 1L) {
                this.dirty_vdelay1L_rate = n2 * 0.122;
                this.dirty_vdelay1R_rate = n2 * 0.122;
                this.dirty = true;
            }
            else if (n == 2L) {
                this.dirty_vdelay1L_depth = (n2 + 1L) / 3200.0;
                this.dirty_vdelay1R_depth = (n2 + 1L) / 3200.0;
                this.dirty = true;
            }
            else if (n == 3L) {
                this.dirty_vdelay1L_feedback = n2 * 0.00763f;
                this.dirty_vdelay1R_feedback = n2 * 0.00763f;
                this.dirty = true;
            }
            if (n == 4L) {
                this.rgain = n2 * 0.00787f;
                this.dirty_vdelay1L_reverbsendgain = n2 * 0.00787f;
                this.dirty_vdelay1R_reverbsendgain = n2 * 0.00787f;
                this.dirty = true;
            }
        }
    }
    
    @Override
    public void processControlLogic() {
        if (this.dirty) {
            this.dirty = false;
            this.vdelay1L.setRate(this.dirty_vdelay1L_rate);
            this.vdelay1R.setRate(this.dirty_vdelay1R_rate);
            this.vdelay1L.setDepth(this.dirty_vdelay1L_depth);
            this.vdelay1R.setDepth(this.dirty_vdelay1R_depth);
            this.vdelay1L.setFeedBack(this.dirty_vdelay1L_feedback);
            this.vdelay1R.setFeedBack(this.dirty_vdelay1R_feedback);
            this.vdelay1L.setReverbSendGain(this.dirty_vdelay1L_reverbsendgain);
            this.vdelay1R.setReverbSendGain(this.dirty_vdelay1R_reverbsendgain);
        }
    }
    
    @Override
    public void processAudio() {
        if (this.inputA.isSilent()) {
            this.silentcounter += 1.0f / this.controlrate;
            if (this.silentcounter > 1.0) {
                if (!this.mix) {
                    this.left.clear();
                    this.right.clear();
                }
                return;
            }
        }
        else {
            this.silentcounter = 0.0;
        }
        final float[] array = this.inputA.array();
        final float[] array2 = this.left.array();
        final float[] array3 = (float[])((this.right == null) ? null : this.right.array());
        final float[] array4 = (float[])((this.rgain != 0.0f) ? this.reverb.array() : null);
        if (this.mix) {
            this.vdelay1L.processMix(array, array2, array4);
            if (array3 != null) {
                this.vdelay1R.processMix(array, array3, array4);
            }
        }
        else {
            this.vdelay1L.processReplace(array, array2, array4);
            if (array3 != null) {
                this.vdelay1R.processReplace(array, array3, array4);
            }
        }
    }
    
    @Override
    public void setInput(final int n, final SoftAudioBuffer inputA) {
        if (n == 0) {
            this.inputA = inputA;
        }
    }
    
    @Override
    public void setMixMode(final boolean mix) {
        this.mix = mix;
    }
    
    @Override
    public void setOutput(final int n, final SoftAudioBuffer reverb) {
        if (n == 0) {
            this.left = reverb;
        }
        if (n == 1) {
            this.right = reverb;
        }
        if (n == 2) {
            this.reverb = reverb;
        }
    }
    
    private static class VariableDelay
    {
        private final float[] delaybuffer;
        private int rovepos;
        private float gain;
        private float rgain;
        private float delay;
        private float lastdelay;
        private float feedback;
        
        VariableDelay(final int n) {
            this.rovepos = 0;
            this.gain = 1.0f;
            this.rgain = 0.0f;
            this.delay = 0.0f;
            this.lastdelay = 0.0f;
            this.feedback = 0.0f;
            this.delaybuffer = new float[n];
        }
        
        public void setDelay(final float delay) {
            this.delay = delay;
        }
        
        public void setFeedBack(final float feedback) {
            this.feedback = feedback;
        }
        
        public void setGain(final float gain) {
            this.gain = gain;
        }
        
        public void setReverbSendGain(final float rgain) {
            this.rgain = rgain;
        }
        
        public void processMix(final float[] array, final float[] array2, final float[] array3) {
            final float gain = this.gain;
            final float delay = this.delay;
            final float feedback = this.feedback;
            final float[] delaybuffer = this.delaybuffer;
            final int length = array.length;
            final float n = (delay - this.lastdelay) / length;
            final int length2 = delaybuffer.length;
            int rovepos = this.rovepos;
            if (array3 == null) {
                for (int i = 0; i < length; ++i) {
                    final float n2 = rovepos - (this.lastdelay + 2.0f) + length2;
                    final int n3 = (int)n2;
                    final float n4 = n2 - n3;
                    final float n5 = delaybuffer[n3 % length2] * (1.0f - n4) + delaybuffer[(n3 + 1) % length2] * n4;
                    final int n6 = i;
                    array2[n6] += n5 * gain;
                    delaybuffer[rovepos] = array[i] + n5 * feedback;
                    rovepos = (rovepos + 1) % length2;
                    this.lastdelay += n;
                }
            }
            else {
                for (int j = 0; j < length; ++j) {
                    final float n7 = rovepos - (this.lastdelay + 2.0f) + length2;
                    final int n8 = (int)n7;
                    final float n9 = n7 - n8;
                    final float n10 = delaybuffer[n8 % length2] * (1.0f - n9) + delaybuffer[(n8 + 1) % length2] * n9;
                    final int n11 = j;
                    array2[n11] += n10 * gain;
                    final int n12 = j;
                    array3[n12] += n10 * this.rgain;
                    delaybuffer[rovepos] = array[j] + n10 * feedback;
                    rovepos = (rovepos + 1) % length2;
                    this.lastdelay += n;
                }
            }
            this.rovepos = rovepos;
            this.lastdelay = delay;
        }
        
        public void processReplace(final float[] array, final float[] array2, final float[] array3) {
            Arrays.fill(array2, 0.0f);
            Arrays.fill(array3, 0.0f);
            this.processMix(array, array2, array3);
        }
    }
    
    private static class LFODelay
    {
        private double phase;
        private double phase_step;
        private double depth;
        private VariableDelay vdelay;
        private final double samplerate;
        private final double controlrate;
        
        LFODelay(final double samplerate, final double controlrate) {
            this.phase = 1.0;
            this.phase_step = 0.0;
            this.depth = 0.0;
            this.samplerate = samplerate;
            this.controlrate = controlrate;
            this.vdelay = new VariableDelay((int)((this.depth + 10.0) * 2.0));
        }
        
        public void setDepth(final double n) {
            this.depth = n * this.samplerate;
            this.vdelay = new VariableDelay((int)((this.depth + 10.0) * 2.0));
        }
        
        public void setRate(final double n) {
            this.phase_step = 6.283185307179586 * (n / this.controlrate);
        }
        
        public void setPhase(final double phase) {
            this.phase = phase;
        }
        
        public void setFeedBack(final float feedBack) {
            this.vdelay.setFeedBack(feedBack);
        }
        
        public void setGain(final float gain) {
            this.vdelay.setGain(gain);
        }
        
        public void setReverbSendGain(final float reverbSendGain) {
            this.vdelay.setReverbSendGain(reverbSendGain);
        }
        
        public void processMix(final float[] array, final float[] array2, final float[] array3) {
            this.phase += this.phase_step;
            while (this.phase > 6.283185307179586) {
                this.phase -= 6.283185307179586;
            }
            this.vdelay.setDelay((float)(this.depth * 0.5 * (Math.cos(this.phase) + 2.0)));
            this.vdelay.processMix(array, array2, array3);
        }
        
        public void processReplace(final float[] array, final float[] array2, final float[] array3) {
            this.phase += this.phase_step;
            while (this.phase > 6.283185307179586) {
                this.phase -= 6.283185307179586;
            }
            this.vdelay.setDelay((float)(this.depth * 0.5 * (Math.cos(this.phase) + 2.0)));
            this.vdelay.processReplace(array, array2, array3);
        }
    }
}
