package com.sun.media.sound;

import java.util.Arrays;

public final class SoftReverb implements SoftAudioProcessor
{
    private float roomsize;
    private float damp;
    private float gain;
    private Delay delay;
    private Comb[] combL;
    private Comb[] combR;
    private AllPass[] allpassL;
    private AllPass[] allpassR;
    private float[] input;
    private float[] out;
    private float[] pre1;
    private float[] pre2;
    private float[] pre3;
    private boolean denormal_flip;
    private boolean mix;
    private SoftAudioBuffer inputA;
    private SoftAudioBuffer left;
    private SoftAudioBuffer right;
    private boolean dirty;
    private float dirty_roomsize;
    private float dirty_damp;
    private float dirty_predelay;
    private float dirty_gain;
    private float samplerate;
    private boolean light;
    private boolean silent;
    
    public SoftReverb() {
        this.gain = 1.0f;
        this.denormal_flip = false;
        this.mix = true;
        this.dirty = true;
        this.light = true;
        this.silent = true;
    }
    
    @Override
    public void init(final float samplerate, final float n) {
        this.samplerate = samplerate;
        final double n2 = samplerate / 44100.0;
        final int n3 = 23;
        this.delay = new Delay();
        this.combL = new Comb[8];
        this.combR = new Comb[8];
        this.combL[0] = new Comb((int)(n2 * 1116.0));
        this.combR[0] = new Comb((int)(n2 * (1116 + n3)));
        this.combL[1] = new Comb((int)(n2 * 1188.0));
        this.combR[1] = new Comb((int)(n2 * (1188 + n3)));
        this.combL[2] = new Comb((int)(n2 * 1277.0));
        this.combR[2] = new Comb((int)(n2 * (1277 + n3)));
        this.combL[3] = new Comb((int)(n2 * 1356.0));
        this.combR[3] = new Comb((int)(n2 * (1356 + n3)));
        this.combL[4] = new Comb((int)(n2 * 1422.0));
        this.combR[4] = new Comb((int)(n2 * (1422 + n3)));
        this.combL[5] = new Comb((int)(n2 * 1491.0));
        this.combR[5] = new Comb((int)(n2 * (1491 + n3)));
        this.combL[6] = new Comb((int)(n2 * 1557.0));
        this.combR[6] = new Comb((int)(n2 * (1557 + n3)));
        this.combL[7] = new Comb((int)(n2 * 1617.0));
        this.combR[7] = new Comb((int)(n2 * (1617 + n3)));
        this.allpassL = new AllPass[4];
        this.allpassR = new AllPass[4];
        this.allpassL[0] = new AllPass((int)(n2 * 556.0));
        this.allpassR[0] = new AllPass((int)(n2 * (556 + n3)));
        this.allpassL[1] = new AllPass((int)(n2 * 441.0));
        this.allpassR[1] = new AllPass((int)(n2 * (441 + n3)));
        this.allpassL[2] = new AllPass((int)(n2 * 341.0));
        this.allpassR[2] = new AllPass((int)(n2 * (341 + n3)));
        this.allpassL[3] = new AllPass((int)(n2 * 225.0));
        this.allpassR[3] = new AllPass((int)(n2 * (225 + n3)));
        for (int i = 0; i < this.allpassL.length; ++i) {
            this.allpassL[i].setFeedBack(0.5f);
            this.allpassR[i].setFeedBack(0.5f);
        }
        this.globalParameterControlChange(new int[] { 129 }, 0L, 4L);
    }
    
    @Override
    public void setInput(final int n, final SoftAudioBuffer inputA) {
        if (n == 0) {
            this.inputA = inputA;
        }
    }
    
    @Override
    public void setOutput(final int n, final SoftAudioBuffer softAudioBuffer) {
        if (n == 0) {
            this.left = softAudioBuffer;
        }
        if (n == 1) {
            this.right = softAudioBuffer;
        }
    }
    
    @Override
    public void setMixMode(final boolean mix) {
        this.mix = mix;
    }
    
    @Override
    public void processAudio() {
        final boolean silent = this.inputA.isSilent();
        if (!silent) {
            this.silent = false;
        }
        if (this.silent) {
            if (!this.mix) {
                this.left.clear();
                this.right.clear();
            }
            return;
        }
        final float[] array = this.inputA.array();
        final float[] array2 = this.left.array();
        final float[] array3 = (float[])((this.right == null) ? null : this.right.array());
        final int length = array.length;
        if (this.input == null || this.input.length < length) {
            this.input = new float[length];
        }
        final float n = this.gain * 0.018f / 2.0f;
        this.denormal_flip = !this.denormal_flip;
        if (this.denormal_flip) {
            for (int i = 0; i < length; ++i) {
                this.input[i] = array[i] * n + 1.0E-20f;
            }
        }
        else {
            for (int j = 0; j < length; ++j) {
                this.input[j] = array[j] * n - 1.0E-20f;
            }
        }
        this.delay.processReplace(this.input);
        if (this.light && array3 != null) {
            if (this.pre1 == null || this.pre1.length < length) {
                this.pre1 = new float[length];
                this.pre2 = new float[length];
                this.pre3 = new float[length];
            }
            for (int k = 0; k < this.allpassL.length; ++k) {
                this.allpassL[k].processReplace(this.input);
            }
            this.combL[0].processReplace(this.input, this.pre3);
            this.combL[1].processReplace(this.input, this.pre3);
            this.combL[2].processReplace(this.input, this.pre1);
            for (int l = 4; l < this.combL.length - 2; l += 2) {
                this.combL[l].processMix(this.input, this.pre1);
            }
            this.combL[3].processReplace(this.input, this.pre2);
            for (int n2 = 5; n2 < this.combL.length - 2; n2 += 2) {
                this.combL[n2].processMix(this.input, this.pre2);
            }
            if (!this.mix) {
                Arrays.fill(array3, 0.0f);
                Arrays.fill(array2, 0.0f);
            }
            for (int n3 = this.combR.length - 2; n3 < this.combR.length; ++n3) {
                this.combR[n3].processMix(this.input, array3);
            }
            for (int n4 = this.combL.length - 2; n4 < this.combL.length; ++n4) {
                this.combL[n4].processMix(this.input, array2);
            }
            for (int n5 = 0; n5 < length; ++n5) {
                final float n6 = this.pre1[n5] - this.pre2[n5];
                final float n7 = this.pre3[n5];
                final float[] array4 = array2;
                final int n8 = n5;
                array4[n8] += n7 + n6;
                final float[] array5 = array3;
                final int n9 = n5;
                array5[n9] += n7 - n6;
            }
        }
        else {
            if (this.out == null || this.out.length < length) {
                this.out = new float[length];
            }
            if (array3 != null) {
                if (!this.mix) {
                    Arrays.fill(array3, 0.0f);
                }
                this.allpassR[0].processReplace(this.input, this.out);
                for (int n10 = 1; n10 < this.allpassR.length; ++n10) {
                    this.allpassR[n10].processReplace(this.out);
                }
                for (int n11 = 0; n11 < this.combR.length; ++n11) {
                    this.combR[n11].processMix(this.out, array3);
                }
            }
            if (!this.mix) {
                Arrays.fill(array2, 0.0f);
            }
            this.allpassL[0].processReplace(this.input, this.out);
            for (int n12 = 1; n12 < this.allpassL.length; ++n12) {
                this.allpassL[n12].processReplace(this.out);
            }
            for (int n13 = 0; n13 < this.combL.length; ++n13) {
                this.combL[n13].processMix(this.out, array2);
            }
        }
        if (silent) {
            this.silent = true;
            for (final float n15 : array2) {
                if (n15 > 1.0E-10 || n15 < -1.0E-10) {
                    this.silent = false;
                    break;
                }
            }
        }
    }
    
    @Override
    public void globalParameterControlChange(final int[] array, final long n, final long n2) {
        if (array.length == 1 && array[0] == 129) {
            if (n == 0L) {
                if (n2 == 0L) {
                    this.dirty_roomsize = 1.1f;
                    this.dirty_damp = 5000.0f;
                    this.dirty_predelay = 0.0f;
                    this.dirty_gain = 4.0f;
                    this.dirty = true;
                }
                if (n2 == 1L) {
                    this.dirty_roomsize = 1.3f;
                    this.dirty_damp = 5000.0f;
                    this.dirty_predelay = 0.0f;
                    this.dirty_gain = 3.0f;
                    this.dirty = true;
                }
                if (n2 == 2L) {
                    this.dirty_roomsize = 1.5f;
                    this.dirty_damp = 5000.0f;
                    this.dirty_predelay = 0.0f;
                    this.dirty_gain = 2.0f;
                    this.dirty = true;
                }
                if (n2 == 3L) {
                    this.dirty_roomsize = 1.8f;
                    this.dirty_damp = 24000.0f;
                    this.dirty_predelay = 0.02f;
                    this.dirty_gain = 1.5f;
                    this.dirty = true;
                }
                if (n2 == 4L) {
                    this.dirty_roomsize = 1.8f;
                    this.dirty_damp = 24000.0f;
                    this.dirty_predelay = 0.03f;
                    this.dirty_gain = 1.5f;
                    this.dirty = true;
                }
                if (n2 == 8L) {
                    this.dirty_roomsize = 1.3f;
                    this.dirty_damp = 2500.0f;
                    this.dirty_predelay = 0.0f;
                    this.dirty_gain = 6.0f;
                    this.dirty = true;
                }
            }
            else if (n == 1L) {
                this.dirty_roomsize = (float)Math.exp((n2 - 40L) * 0.025);
                this.dirty = true;
            }
        }
    }
    
    @Override
    public void processControlLogic() {
        if (this.dirty) {
            this.dirty = false;
            this.setRoomSize(this.dirty_roomsize);
            this.setDamp(this.dirty_damp);
            this.setPreDelay(this.dirty_predelay);
            this.setGain(this.dirty_gain);
        }
    }
    
    public void setRoomSize(final float n) {
        this.roomsize = 1.0f - 0.17f / n;
        for (int i = 0; i < this.combL.length; ++i) {
            this.combL[i].feedback = this.roomsize;
            this.combR[i].feedback = this.roomsize;
        }
    }
    
    public void setPreDelay(final float n) {
        this.delay.setDelay((int)(n * this.samplerate));
    }
    
    public void setGain(final float gain) {
        this.gain = gain;
    }
    
    public void setDamp(final float n) {
        final double n2 = 2.0 - Math.cos(n / this.samplerate * 6.283185307179586);
        this.damp = (float)(n2 - Math.sqrt(n2 * n2 - 1.0));
        if (this.damp > 1.0f) {
            this.damp = 1.0f;
        }
        if (this.damp < 0.0f) {
            this.damp = 0.0f;
        }
        for (int i = 0; i < this.combL.length; ++i) {
            this.combL[i].setDamp(this.damp);
            this.combR[i].setDamp(this.damp);
        }
    }
    
    public void setLightMode(final boolean light) {
        this.light = light;
    }
    
    private static final class Delay
    {
        private float[] delaybuffer;
        private int rovepos;
        
        Delay() {
            this.rovepos = 0;
            this.delaybuffer = null;
        }
        
        public void setDelay(final int n) {
            if (n == 0) {
                this.delaybuffer = null;
            }
            else {
                this.delaybuffer = new float[n];
            }
            this.rovepos = 0;
        }
        
        public void processReplace(final float[] array) {
            if (this.delaybuffer == null) {
                return;
            }
            final int length = array.length;
            final int length2 = this.delaybuffer.length;
            int rovepos = this.rovepos;
            for (int i = 0; i < length; ++i) {
                final float n = array[i];
                array[i] = this.delaybuffer[rovepos];
                this.delaybuffer[rovepos] = n;
                if (++rovepos == length2) {
                    rovepos = 0;
                }
            }
            this.rovepos = rovepos;
        }
    }
    
    private static final class AllPass
    {
        private final float[] delaybuffer;
        private final int delaybuffersize;
        private int rovepos;
        private float feedback;
        
        AllPass(final int delaybuffersize) {
            this.rovepos = 0;
            this.delaybuffer = new float[delaybuffersize];
            this.delaybuffersize = delaybuffersize;
        }
        
        public void setFeedBack(final float feedback) {
            this.feedback = feedback;
        }
        
        public void processReplace(final float[] array) {
            final int length = array.length;
            final int delaybuffersize = this.delaybuffersize;
            int rovepos = this.rovepos;
            for (int i = 0; i < length; ++i) {
                final float n = this.delaybuffer[rovepos];
                final float n2 = array[i];
                array[i] = n - n2;
                this.delaybuffer[rovepos] = n2 + n * this.feedback;
                if (++rovepos == delaybuffersize) {
                    rovepos = 0;
                }
            }
            this.rovepos = rovepos;
        }
        
        public void processReplace(final float[] array, final float[] array2) {
            final int length = array.length;
            final int delaybuffersize = this.delaybuffersize;
            int rovepos = this.rovepos;
            for (int i = 0; i < length; ++i) {
                final float n = this.delaybuffer[rovepos];
                final float n2 = array[i];
                array2[i] = n - n2;
                this.delaybuffer[rovepos] = n2 + n * this.feedback;
                if (++rovepos == delaybuffersize) {
                    rovepos = 0;
                }
            }
            this.rovepos = rovepos;
        }
    }
    
    private static final class Comb
    {
        private final float[] delaybuffer;
        private final int delaybuffersize;
        private int rovepos;
        private float feedback;
        private float filtertemp;
        private float filtercoeff1;
        private float filtercoeff2;
        
        Comb(final int delaybuffersize) {
            this.rovepos = 0;
            this.filtertemp = 0.0f;
            this.filtercoeff1 = 0.0f;
            this.filtercoeff2 = 1.0f;
            this.delaybuffer = new float[delaybuffersize];
            this.delaybuffersize = delaybuffersize;
        }
        
        public void setFeedBack(final float feedback) {
            this.feedback = feedback;
            this.filtercoeff2 = (1.0f - this.filtercoeff1) * feedback;
        }
        
        public void processMix(final float[] array, final float[] array2) {
            final int length = array.length;
            final int delaybuffersize = this.delaybuffersize;
            int rovepos = this.rovepos;
            float filtertemp = this.filtertemp;
            final float filtercoeff1 = this.filtercoeff1;
            final float filtercoeff2 = this.filtercoeff2;
            for (int i = 0; i < length; ++i) {
                final float n = this.delaybuffer[rovepos];
                filtertemp = n * filtercoeff2 + filtertemp * filtercoeff1;
                final int n2 = i;
                array2[n2] += n;
                this.delaybuffer[rovepos] = array[i] + filtertemp;
                if (++rovepos == delaybuffersize) {
                    rovepos = 0;
                }
            }
            this.filtertemp = filtertemp;
            this.rovepos = rovepos;
        }
        
        public void processReplace(final float[] array, final float[] array2) {
            final int length = array.length;
            final int delaybuffersize = this.delaybuffersize;
            int rovepos = this.rovepos;
            float filtertemp = this.filtertemp;
            final float filtercoeff1 = this.filtercoeff1;
            final float filtercoeff2 = this.filtercoeff2;
            for (int i = 0; i < length; ++i) {
                final float n = this.delaybuffer[rovepos];
                filtertemp = n * filtercoeff2 + filtertemp * filtercoeff1;
                array2[i] = n;
                this.delaybuffer[rovepos] = array[i] + filtertemp;
                if (++rovepos == delaybuffersize) {
                    rovepos = 0;
                }
            }
            this.filtertemp = filtertemp;
            this.rovepos = rovepos;
        }
        
        public void setDamp(final float filtercoeff1) {
            this.filtercoeff1 = filtercoeff1;
            this.filtercoeff2 = (1.0f - this.filtercoeff1) * this.feedback;
        }
    }
}
