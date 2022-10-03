package com.sun.media.sound;

public final class SoftLimiter implements SoftAudioProcessor
{
    float lastmax;
    float gain;
    float[] temp_bufferL;
    float[] temp_bufferR;
    boolean mix;
    SoftAudioBuffer bufferL;
    SoftAudioBuffer bufferR;
    SoftAudioBuffer bufferLout;
    SoftAudioBuffer bufferRout;
    float controlrate;
    double silentcounter;
    
    public SoftLimiter() {
        this.lastmax = 0.0f;
        this.gain = 1.0f;
        this.mix = false;
        this.silentcounter = 0.0;
    }
    
    @Override
    public void init(final float n, final float controlrate) {
        this.controlrate = controlrate;
    }
    
    @Override
    public void setInput(final int n, final SoftAudioBuffer softAudioBuffer) {
        if (n == 0) {
            this.bufferL = softAudioBuffer;
        }
        if (n == 1) {
            this.bufferR = softAudioBuffer;
        }
    }
    
    @Override
    public void setOutput(final int n, final SoftAudioBuffer softAudioBuffer) {
        if (n == 0) {
            this.bufferLout = softAudioBuffer;
        }
        if (n == 1) {
            this.bufferRout = softAudioBuffer;
        }
    }
    
    @Override
    public void setMixMode(final boolean mix) {
        this.mix = mix;
    }
    
    @Override
    public void globalParameterControlChange(final int[] array, final long n, final long n2) {
    }
    
    @Override
    public void processAudio() {
        if (this.bufferL.isSilent() && (this.bufferR == null || this.bufferR.isSilent())) {
            this.silentcounter += 1.0f / this.controlrate;
            if (this.silentcounter > 60.0) {
                if (!this.mix) {
                    this.bufferLout.clear();
                    if (this.bufferRout != null) {
                        this.bufferRout.clear();
                    }
                }
                return;
            }
        }
        else {
            this.silentcounter = 0.0;
        }
        final float[] array = this.bufferL.array();
        final float[] array2 = (float[])((this.bufferR == null) ? null : this.bufferR.array());
        final float[] array3 = this.bufferLout.array();
        final float[] array4 = (float[])((this.bufferRout == null) ? null : this.bufferRout.array());
        if (this.temp_bufferL == null || this.temp_bufferL.length < array.length) {
            this.temp_bufferL = new float[array.length];
        }
        if (array2 != null && (this.temp_bufferR == null || this.temp_bufferR.length < array2.length)) {
            this.temp_bufferR = new float[array2.length];
        }
        float lastmax = 0.0f;
        final int length = array.length;
        if (array2 == null) {
            for (int i = 0; i < length; ++i) {
                if (array[i] > lastmax) {
                    lastmax = array[i];
                }
                if (-array[i] > lastmax) {
                    lastmax = -array[i];
                }
            }
        }
        else {
            for (int j = 0; j < length; ++j) {
                if (array[j] > lastmax) {
                    lastmax = array[j];
                }
                if (array2[j] > lastmax) {
                    lastmax = array2[j];
                }
                if (-array[j] > lastmax) {
                    lastmax = -array[j];
                }
                if (-array2[j] > lastmax) {
                    lastmax = -array2[j];
                }
            }
        }
        final float lastmax2 = this.lastmax;
        this.lastmax = lastmax;
        if (lastmax2 > lastmax) {
            lastmax = lastmax2;
        }
        float gain;
        if (lastmax > 0.99f) {
            gain = 0.99f / lastmax;
        }
        else {
            gain = 1.0f;
        }
        if (gain > this.gain) {
            gain = (gain + this.gain * 9.0f) / 10.0f;
        }
        final float n = (gain - this.gain) / length;
        if (this.mix) {
            if (array2 == null) {
                for (int k = 0; k < length; ++k) {
                    this.gain += n;
                    final float n2 = array[k];
                    final float n3 = this.temp_bufferL[k];
                    this.temp_bufferL[k] = n2;
                    final float[] array5 = array3;
                    final int n4 = k;
                    array5[n4] += n3 * this.gain;
                }
            }
            else {
                for (int l = 0; l < length; ++l) {
                    this.gain += n;
                    final float n5 = array[l];
                    final float n6 = array2[l];
                    final float n7 = this.temp_bufferL[l];
                    final float n8 = this.temp_bufferR[l];
                    this.temp_bufferL[l] = n5;
                    this.temp_bufferR[l] = n6;
                    final float[] array6 = array3;
                    final int n9 = l;
                    array6[n9] += n7 * this.gain;
                    final float[] array7 = array4;
                    final int n10 = l;
                    array7[n10] += n8 * this.gain;
                }
            }
        }
        else if (array2 == null) {
            for (int n11 = 0; n11 < length; ++n11) {
                this.gain += n;
                final float n12 = array[n11];
                final float n13 = this.temp_bufferL[n11];
                this.temp_bufferL[n11] = n12;
                array3[n11] = n13 * this.gain;
            }
        }
        else {
            for (int n14 = 0; n14 < length; ++n14) {
                this.gain += n;
                final float n15 = array[n14];
                final float n16 = array2[n14];
                final float n17 = this.temp_bufferL[n14];
                final float n18 = this.temp_bufferR[n14];
                this.temp_bufferL[n14] = n15;
                this.temp_bufferR[n14] = n16;
                array3[n14] = n17 * this.gain;
                array4[n14] = n18 * this.gain;
            }
        }
        this.gain = gain;
    }
    
    @Override
    public void processControlLogic() {
    }
}
