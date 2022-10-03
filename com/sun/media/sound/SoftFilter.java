package com.sun.media.sound;

public final class SoftFilter
{
    public static final int FILTERTYPE_LP6 = 0;
    public static final int FILTERTYPE_LP12 = 1;
    public static final int FILTERTYPE_HP12 = 17;
    public static final int FILTERTYPE_BP12 = 33;
    public static final int FILTERTYPE_NP12 = 49;
    public static final int FILTERTYPE_LP24 = 3;
    public static final int FILTERTYPE_HP24 = 19;
    private int filtertype;
    private final float samplerate;
    private float x1;
    private float x2;
    private float y1;
    private float y2;
    private float xx1;
    private float xx2;
    private float yy1;
    private float yy2;
    private float a0;
    private float a1;
    private float a2;
    private float b1;
    private float b2;
    private float q;
    private float gain;
    private float wet;
    private float last_wet;
    private float last_a0;
    private float last_a1;
    private float last_a2;
    private float last_b1;
    private float last_b2;
    private float last_q;
    private float last_gain;
    private boolean last_set;
    private double cutoff;
    private double resonancedB;
    private boolean dirty;
    
    public SoftFilter(final float samplerate) {
        this.filtertype = 0;
        this.gain = 1.0f;
        this.wet = 0.0f;
        this.last_wet = 0.0f;
        this.last_set = false;
        this.cutoff = 44100.0;
        this.resonancedB = 0.0;
        this.dirty = true;
        this.samplerate = samplerate;
        this.dirty = true;
    }
    
    public void setFrequency(final double cutoff) {
        if (this.cutoff == cutoff) {
            return;
        }
        this.cutoff = cutoff;
        this.dirty = true;
    }
    
    public void setResonance(final double resonancedB) {
        if (this.resonancedB == resonancedB) {
            return;
        }
        this.resonancedB = resonancedB;
        this.dirty = true;
    }
    
    public void reset() {
        this.dirty = true;
        this.last_set = false;
        this.x1 = 0.0f;
        this.x2 = 0.0f;
        this.y1 = 0.0f;
        this.y2 = 0.0f;
        this.xx1 = 0.0f;
        this.xx2 = 0.0f;
        this.yy1 = 0.0f;
        this.yy2 = 0.0f;
        this.wet = 0.0f;
        this.gain = 1.0f;
        this.a0 = 0.0f;
        this.a1 = 0.0f;
        this.a2 = 0.0f;
        this.b1 = 0.0f;
        this.b2 = 0.0f;
    }
    
    public void setFilterType(final int filtertype) {
        this.filtertype = filtertype;
    }
    
    public void processAudio(final SoftAudioBuffer softAudioBuffer) {
        if (this.filtertype == 0) {
            this.filter1(softAudioBuffer);
        }
        if (this.filtertype == 1) {
            this.filter2(softAudioBuffer);
        }
        if (this.filtertype == 17) {
            this.filter2(softAudioBuffer);
        }
        if (this.filtertype == 33) {
            this.filter2(softAudioBuffer);
        }
        if (this.filtertype == 49) {
            this.filter2(softAudioBuffer);
        }
        if (this.filtertype == 3) {
            this.filter4(softAudioBuffer);
        }
        if (this.filtertype == 19) {
            this.filter4(softAudioBuffer);
        }
    }
    
    public void filter4(final SoftAudioBuffer softAudioBuffer) {
        final float[] array = softAudioBuffer.array();
        if (this.dirty) {
            this.filter2calc();
            this.dirty = false;
        }
        if (!this.last_set) {
            this.last_a0 = this.a0;
            this.last_a1 = this.a1;
            this.last_a2 = this.a2;
            this.last_b1 = this.b1;
            this.last_b2 = this.b2;
            this.last_gain = this.gain;
            this.last_wet = this.wet;
            this.last_set = true;
        }
        if (this.wet > 0.0f || this.last_wet > 0.0f) {
            final int length = array.length;
            float last_a0 = this.last_a0;
            float last_a2 = this.last_a1;
            float last_a3 = this.last_a2;
            float last_b1 = this.last_b1;
            float last_b2 = this.last_b2;
            float last_gain = this.last_gain;
            float last_wet = this.last_wet;
            final float n = (this.a0 - this.last_a0) / length;
            final float n2 = (this.a1 - this.last_a1) / length;
            final float n3 = (this.a2 - this.last_a2) / length;
            final float n4 = (this.b1 - this.last_b1) / length;
            final float n5 = (this.b2 - this.last_b2) / length;
            final float n6 = (this.gain - this.last_gain) / length;
            final float n7 = (this.wet - this.last_wet) / length;
            float x1 = this.x1;
            float x2 = this.x2;
            float y1 = this.y1;
            float y2 = this.y2;
            float xx1 = this.xx1;
            float xx2 = this.xx2;
            float yy1 = this.yy1;
            float yy2 = this.yy2;
            if (n7 != 0.0f) {
                for (int i = 0; i < length; ++i) {
                    last_a0 += n;
                    last_a2 += n2;
                    last_a3 += n3;
                    last_b1 += n4;
                    last_b2 += n5;
                    last_gain += n6;
                    last_wet += n7;
                    final float n8 = array[i];
                    final float n9 = last_a0 * n8 + last_a2 * x1 + last_a3 * x2 - last_b1 * y1 - last_b2 * y2;
                    final float n10 = n9 * last_gain * last_wet + n8 * (1.0f - last_wet);
                    x2 = x1;
                    x1 = n8;
                    y2 = y1;
                    y1 = n9;
                    final float n11 = last_a0 * n10 + last_a2 * xx1 + last_a3 * xx2 - last_b1 * yy1 - last_b2 * yy2;
                    array[i] = n11 * last_gain * last_wet + n10 * (1.0f - last_wet);
                    xx2 = xx1;
                    xx1 = n10;
                    yy2 = yy1;
                    yy1 = n11;
                }
            }
            else if (n == 0.0f && n2 == 0.0f && n3 == 0.0f && n4 == 0.0f && n5 == 0.0f) {
                for (int j = 0; j < length; ++j) {
                    final float n12 = array[j];
                    final float n13 = last_a0 * n12 + last_a2 * x1 + last_a3 * x2 - last_b1 * y1 - last_b2 * y2;
                    final float n14 = n13 * last_gain * last_wet + n12 * (1.0f - last_wet);
                    x2 = x1;
                    x1 = n12;
                    y2 = y1;
                    y1 = n13;
                    final float n15 = last_a0 * n14 + last_a2 * xx1 + last_a3 * xx2 - last_b1 * yy1 - last_b2 * yy2;
                    array[j] = n15 * last_gain * last_wet + n14 * (1.0f - last_wet);
                    xx2 = xx1;
                    xx1 = n14;
                    yy2 = yy1;
                    yy1 = n15;
                }
            }
            else {
                for (int k = 0; k < length; ++k) {
                    last_a0 += n;
                    last_a2 += n2;
                    last_a3 += n3;
                    last_b1 += n4;
                    last_b2 += n5;
                    last_gain += n6;
                    final float n16 = array[k];
                    final float n17 = last_a0 * n16 + last_a2 * x1 + last_a3 * x2 - last_b1 * y1 - last_b2 * y2;
                    final float n18 = n17 * last_gain * last_wet + n16 * (1.0f - last_wet);
                    x2 = x1;
                    x1 = n16;
                    y2 = y1;
                    y1 = n17;
                    final float n19 = last_a0 * n18 + last_a2 * xx1 + last_a3 * xx2 - last_b1 * yy1 - last_b2 * yy2;
                    array[k] = n19 * last_gain * last_wet + n18 * (1.0f - last_wet);
                    xx2 = xx1;
                    xx1 = n18;
                    yy2 = yy1;
                    yy1 = n19;
                }
            }
            if (Math.abs(x1) < 1.0E-8) {
                x1 = 0.0f;
            }
            if (Math.abs(x2) < 1.0E-8) {
                x2 = 0.0f;
            }
            if (Math.abs(y1) < 1.0E-8) {
                y1 = 0.0f;
            }
            if (Math.abs(y2) < 1.0E-8) {
                y2 = 0.0f;
            }
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.xx1 = xx1;
            this.xx2 = xx2;
            this.yy1 = yy1;
            this.yy2 = yy2;
        }
        this.last_a0 = this.a0;
        this.last_a1 = this.a1;
        this.last_a2 = this.a2;
        this.last_b1 = this.b1;
        this.last_b2 = this.b2;
        this.last_gain = this.gain;
        this.last_wet = this.wet;
    }
    
    private double sinh(final double n) {
        return (Math.exp(n) - Math.exp(-n)) * 0.5;
    }
    
    public void filter2calc() {
        double resonancedB = this.resonancedB;
        if (resonancedB < 0.0) {
            resonancedB = 0.0;
        }
        if (resonancedB > 30.0) {
            resonancedB = 30.0;
        }
        if (this.filtertype == 3 || this.filtertype == 19) {
            resonancedB *= 0.6;
        }
        if (this.filtertype == 33) {
            this.wet = 1.0f;
            double n = this.cutoff / this.samplerate;
            if (n > 0.45) {
                n = 0.45;
            }
            final double n2 = 3.141592653589793 * Math.pow(10.0, -(resonancedB / 20.0));
            final double n3 = 6.283185307179586 * n;
            final double cos = Math.cos(n3);
            final double sin = Math.sin(n3);
            final double n5;
            final double n4 = n5 = sin * this.sinh(Math.log(2.0) * n2 * n3 / (sin * 2.0));
            final double n6 = 0.0;
            final double n7 = -n4;
            final double n8 = 1.0 + n4;
            final double n9 = -2.0 * cos;
            final double n10 = 1.0 - n4;
            final double n11 = 1.0 / n8;
            this.b1 = (float)(n9 * n11);
            this.b2 = (float)(n10 * n11);
            this.a0 = (float)(n5 * n11);
            this.a1 = (float)(n6 * n11);
            this.a2 = (float)(n7 * n11);
        }
        if (this.filtertype == 49) {
            this.wet = 1.0f;
            double n12 = this.cutoff / this.samplerate;
            if (n12 > 0.45) {
                n12 = 0.45;
            }
            final double n13 = 3.141592653589793 * Math.pow(10.0, -(resonancedB / 20.0));
            final double n14 = 6.283185307179586 * n12;
            final double cos2 = Math.cos(n14);
            final double sin2 = Math.sin(n14);
            final double n15 = sin2 * this.sinh(Math.log(2.0) * n13 * n14 / (sin2 * 2.0));
            final double n16 = 1.0;
            final double n17 = -2.0 * cos2;
            final double n18 = 1.0;
            final double n19 = 1.0 + n15;
            final double n20 = -2.0 * cos2;
            final double n21 = 1.0 - n15;
            final double n22 = 1.0 / n19;
            this.b1 = (float)(n20 * n22);
            this.b2 = (float)(n21 * n22);
            this.a0 = (float)(n16 * n22);
            this.a1 = (float)(n17 * n22);
            this.a2 = (float)(n18 * n22);
        }
        if (this.filtertype == 1 || this.filtertype == 3) {
            double n23 = this.cutoff / this.samplerate;
            if (n23 > 0.45) {
                if (this.wet == 0.0f) {
                    if (resonancedB < 1.0E-5) {
                        this.wet = 0.0f;
                    }
                    else {
                        this.wet = 1.0f;
                    }
                }
                n23 = 0.45;
            }
            else {
                this.wet = 1.0f;
            }
            final double n24 = 1.0 / Math.tan(3.141592653589793 * n23);
            final double n25 = n24 * n24;
            final double n26 = Math.sqrt(2.0) * Math.pow(10.0, -(resonancedB / 20.0));
            final double n27 = 1.0 / (1.0 + n26 * n24 + n25);
            final double n28 = 2.0 * n27;
            final double n29 = n27;
            final double n30 = 2.0 * n27 * (1.0 - n25);
            final double n31 = n27 * (1.0 - n26 * n24 + n25);
            this.a0 = (float)n27;
            this.a1 = (float)n28;
            this.a2 = (float)n29;
            this.b1 = (float)n30;
            this.b2 = (float)n31;
        }
        if (this.filtertype == 17 || this.filtertype == 19) {
            double n32 = this.cutoff / this.samplerate;
            if (n32 > 0.45) {
                n32 = 0.45;
            }
            if (n32 < 1.0E-4) {
                n32 = 1.0E-4;
            }
            this.wet = 1.0f;
            final double tan = Math.tan(3.141592653589793 * n32);
            final double n33 = tan * tan;
            final double n34 = Math.sqrt(2.0) * Math.pow(10.0, -(resonancedB / 20.0));
            final double n35 = 1.0 / (1.0 + n34 * tan + n33);
            final double n36 = -2.0 * n35;
            final double n37 = n35;
            final double n38 = 2.0 * n35 * (n33 - 1.0);
            final double n39 = n35 * (1.0 - n34 * tan + n33);
            this.a0 = (float)n35;
            this.a1 = (float)n36;
            this.a2 = (float)n37;
            this.b1 = (float)n38;
            this.b2 = (float)n39;
        }
    }
    
    public void filter2(final SoftAudioBuffer softAudioBuffer) {
        final float[] array = softAudioBuffer.array();
        if (this.dirty) {
            this.filter2calc();
            this.dirty = false;
        }
        if (!this.last_set) {
            this.last_a0 = this.a0;
            this.last_a1 = this.a1;
            this.last_a2 = this.a2;
            this.last_b1 = this.b1;
            this.last_b2 = this.b2;
            this.last_q = this.q;
            this.last_gain = this.gain;
            this.last_wet = this.wet;
            this.last_set = true;
        }
        if (this.wet > 0.0f || this.last_wet > 0.0f) {
            final int length = array.length;
            float last_a0 = this.last_a0;
            float last_a2 = this.last_a1;
            float last_a3 = this.last_a2;
            float last_b1 = this.last_b1;
            float last_b2 = this.last_b2;
            float last_gain = this.last_gain;
            float last_wet = this.last_wet;
            final float n = (this.a0 - this.last_a0) / length;
            final float n2 = (this.a1 - this.last_a1) / length;
            final float n3 = (this.a2 - this.last_a2) / length;
            final float n4 = (this.b1 - this.last_b1) / length;
            final float n5 = (this.b2 - this.last_b2) / length;
            final float n6 = (this.gain - this.last_gain) / length;
            final float n7 = (this.wet - this.last_wet) / length;
            float x1 = this.x1;
            float x2 = this.x2;
            float y1 = this.y1;
            float y2 = this.y2;
            if (n7 != 0.0f) {
                for (int i = 0; i < length; ++i) {
                    last_a0 += n;
                    last_a2 += n2;
                    last_a3 += n3;
                    last_b1 += n4;
                    last_b2 += n5;
                    last_gain += n6;
                    last_wet += n7;
                    final float n8 = array[i];
                    final float n9 = last_a0 * n8 + last_a2 * x1 + last_a3 * x2 - last_b1 * y1 - last_b2 * y2;
                    array[i] = n9 * last_gain * last_wet + n8 * (1.0f - last_wet);
                    x2 = x1;
                    x1 = n8;
                    y2 = y1;
                    y1 = n9;
                }
            }
            else if (n == 0.0f && n2 == 0.0f && n3 == 0.0f && n4 == 0.0f && n5 == 0.0f) {
                for (int j = 0; j < length; ++j) {
                    final float n10 = array[j];
                    final float n11 = last_a0 * n10 + last_a2 * x1 + last_a3 * x2 - last_b1 * y1 - last_b2 * y2;
                    array[j] = n11 * last_gain;
                    x2 = x1;
                    x1 = n10;
                    y2 = y1;
                    y1 = n11;
                }
            }
            else {
                for (int k = 0; k < length; ++k) {
                    last_a0 += n;
                    last_a2 += n2;
                    last_a3 += n3;
                    last_b1 += n4;
                    last_b2 += n5;
                    last_gain += n6;
                    final float n12 = array[k];
                    final float n13 = last_a0 * n12 + last_a2 * x1 + last_a3 * x2 - last_b1 * y1 - last_b2 * y2;
                    array[k] = n13 * last_gain;
                    x2 = x1;
                    x1 = n12;
                    y2 = y1;
                    y1 = n13;
                }
            }
            if (Math.abs(x1) < 1.0E-8) {
                x1 = 0.0f;
            }
            if (Math.abs(x2) < 1.0E-8) {
                x2 = 0.0f;
            }
            if (Math.abs(y1) < 1.0E-8) {
                y1 = 0.0f;
            }
            if (Math.abs(y2) < 1.0E-8) {
                y2 = 0.0f;
            }
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }
        this.last_a0 = this.a0;
        this.last_a1 = this.a1;
        this.last_a2 = this.a2;
        this.last_b1 = this.b1;
        this.last_b2 = this.b2;
        this.last_q = this.q;
        this.last_gain = this.gain;
        this.last_wet = this.wet;
    }
    
    public void filter1calc() {
        if (this.cutoff < 120.0) {
            this.cutoff = 120.0;
        }
        double n = 7.3303828583761845 * this.cutoff / this.samplerate;
        if (n > 1.0) {
            n = 1.0;
        }
        this.a0 = (float)(Math.sqrt(1.0 - Math.cos(n)) * Math.sqrt(1.5707963267948966));
        if (this.resonancedB < 0.0) {
            this.resonancedB = 0.0;
        }
        if (this.resonancedB > 20.0) {
            this.resonancedB = 20.0;
        }
        this.q = (float)(Math.sqrt(0.5) * Math.pow(10.0, -(this.resonancedB / 20.0)));
        this.gain = (float)Math.pow(10.0, -this.resonancedB / 40.0);
        if (this.wet == 0.0f && (this.resonancedB > 1.0E-5 || n < 0.9999999)) {
            this.wet = 1.0f;
        }
    }
    
    public void filter1(final SoftAudioBuffer softAudioBuffer) {
        if (this.dirty) {
            this.filter1calc();
            this.dirty = false;
        }
        if (!this.last_set) {
            this.last_a0 = this.a0;
            this.last_q = this.q;
            this.last_gain = this.gain;
            this.last_wet = this.wet;
            this.last_set = true;
        }
        if (this.wet > 0.0f || this.last_wet > 0.0f) {
            final float[] array = softAudioBuffer.array();
            final int length = array.length;
            float last_a0 = this.last_a0;
            float last_q = this.last_q;
            float last_gain = this.last_gain;
            float last_wet = this.last_wet;
            final float n = (this.a0 - this.last_a0) / length;
            final float n2 = (this.q - this.last_q) / length;
            final float n3 = (this.gain - this.last_gain) / length;
            final float n4 = (this.wet - this.last_wet) / length;
            float y2 = this.y2;
            float y3 = this.y1;
            if (n4 != 0.0f) {
                for (int i = 0; i < length; ++i) {
                    last_a0 += n;
                    last_q += n2;
                    last_gain += n3;
                    last_wet += n4;
                    final float n5 = 1.0f - last_q * last_a0;
                    y3 = n5 * y3 + last_a0 * (array[i] - y2);
                    y2 = n5 * y2 + last_a0 * y3;
                    array[i] = y2 * last_gain * last_wet + array[i] * (1.0f - last_wet);
                }
            }
            else if (n == 0.0f && n2 == 0.0f) {
                final float n6 = 1.0f - last_q * last_a0;
                for (int j = 0; j < length; ++j) {
                    y3 = n6 * y3 + last_a0 * (array[j] - y2);
                    y2 = n6 * y2 + last_a0 * y3;
                    array[j] = y2 * last_gain;
                }
            }
            else {
                for (int k = 0; k < length; ++k) {
                    last_a0 += n;
                    last_q += n2;
                    last_gain += n3;
                    final float n7 = 1.0f - last_q * last_a0;
                    y3 = n7 * y3 + last_a0 * (array[k] - y2);
                    y2 = n7 * y2 + last_a0 * y3;
                    array[k] = y2 * last_gain;
                }
            }
            if (Math.abs(y2) < 1.0E-8) {
                y2 = 0.0f;
            }
            if (Math.abs(y3) < 1.0E-8) {
                y3 = 0.0f;
            }
            this.y2 = y2;
            this.y1 = y3;
        }
        this.last_a0 = this.a0;
        this.last_q = this.q;
        this.last_gain = this.gain;
        this.last_wet = this.wet;
    }
}
