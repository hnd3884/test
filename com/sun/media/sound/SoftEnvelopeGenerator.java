package com.sun.media.sound;

public final class SoftEnvelopeGenerator implements SoftProcess
{
    public static final int EG_OFF = 0;
    public static final int EG_DELAY = 1;
    public static final int EG_ATTACK = 2;
    public static final int EG_HOLD = 3;
    public static final int EG_DECAY = 4;
    public static final int EG_SUSTAIN = 5;
    public static final int EG_RELEASE = 6;
    public static final int EG_SHUTDOWN = 7;
    public static final int EG_END = 8;
    int max_count;
    int used_count;
    private final int[] stage;
    private final int[] stage_ix;
    private final double[] stage_v;
    private final int[] stage_count;
    private final double[][] on;
    private final double[][] active;
    private final double[][] out;
    private final double[][] delay;
    private final double[][] attack;
    private final double[][] hold;
    private final double[][] decay;
    private final double[][] sustain;
    private final double[][] release;
    private final double[][] shutdown;
    private final double[][] release2;
    private final double[][] attack2;
    private final double[][] decay2;
    private double control_time;
    
    public SoftEnvelopeGenerator() {
        this.max_count = 10;
        this.used_count = 0;
        this.stage = new int[this.max_count];
        this.stage_ix = new int[this.max_count];
        this.stage_v = new double[this.max_count];
        this.stage_count = new int[this.max_count];
        this.on = new double[this.max_count][1];
        this.active = new double[this.max_count][1];
        this.out = new double[this.max_count][1];
        this.delay = new double[this.max_count][1];
        this.attack = new double[this.max_count][1];
        this.hold = new double[this.max_count][1];
        this.decay = new double[this.max_count][1];
        this.sustain = new double[this.max_count][1];
        this.release = new double[this.max_count][1];
        this.shutdown = new double[this.max_count][1];
        this.release2 = new double[this.max_count][1];
        this.attack2 = new double[this.max_count][1];
        this.decay2 = new double[this.max_count][1];
        this.control_time = 0.0;
    }
    
    @Override
    public void reset() {
        for (int i = 0; i < this.used_count; ++i) {
            this.stage[i] = 0;
            this.on[i][0] = 0.0;
            this.out[i][0] = 0.0;
            this.delay[i][0] = 0.0;
            this.attack[i][0] = 0.0;
            this.hold[i][0] = 0.0;
            this.decay[i][0] = 0.0;
            this.sustain[i][0] = 0.0;
            this.release[i][0] = 0.0;
            this.shutdown[i][0] = 0.0;
            this.attack2[i][0] = 0.0;
            this.decay2[i][0] = 0.0;
            this.release2[i][0] = 0.0;
        }
        this.used_count = 0;
    }
    
    @Override
    public void init(final SoftSynthesizer softSynthesizer) {
        this.control_time = 1.0 / softSynthesizer.getControlRate();
        this.processControlLogic();
    }
    
    @Override
    public double[] get(final int n, final String s) {
        if (n >= this.used_count) {
            this.used_count = n + 1;
        }
        if (s == null) {
            return this.out[n];
        }
        if (s.equals("on")) {
            return this.on[n];
        }
        if (s.equals("active")) {
            return this.active[n];
        }
        if (s.equals("delay")) {
            return this.delay[n];
        }
        if (s.equals("attack")) {
            return this.attack[n];
        }
        if (s.equals("hold")) {
            return this.hold[n];
        }
        if (s.equals("decay")) {
            return this.decay[n];
        }
        if (s.equals("sustain")) {
            return this.sustain[n];
        }
        if (s.equals("release")) {
            return this.release[n];
        }
        if (s.equals("shutdown")) {
            return this.shutdown[n];
        }
        if (s.equals("attack2")) {
            return this.attack2[n];
        }
        if (s.equals("decay2")) {
            return this.decay2[n];
        }
        if (s.equals("release2")) {
            return this.release2[n];
        }
        return null;
    }
    
    @Override
    public void processControlLogic() {
        for (int i = 0; i < this.used_count; ++i) {
            if (this.stage[i] != 8) {
                if (this.stage[i] > 0 && this.stage[i] < 6 && this.on[i][0] < 0.5) {
                    if (this.on[i][0] < -0.5) {
                        this.stage_count[i] = (int)(Math.pow(2.0, this.shutdown[i][0] / 1200.0) / this.control_time);
                        if (this.stage_count[i] < 0) {
                            this.stage_count[i] = 0;
                        }
                        this.stage_v[i] = this.out[i][0];
                        this.stage_ix[i] = 0;
                        this.stage[i] = 7;
                    }
                    else {
                        if (this.release2[i][0] < 1.0E-6 && this.release[i][0] < 0.0 && Double.isInfinite(this.release[i][0])) {
                            this.out[i][0] = 0.0;
                            this.active[i][0] = 0.0;
                            this.stage[i] = 8;
                            continue;
                        }
                        this.stage_count[i] = (int)(Math.pow(2.0, this.release[i][0] / 1200.0) / this.control_time);
                        final int[] stage_count = this.stage_count;
                        final int n = i;
                        stage_count[n] += (int)(this.release2[i][0] / (this.control_time * 1000.0));
                        if (this.stage_count[i] < 0) {
                            this.stage_count[i] = 0;
                        }
                        this.stage_ix[i] = 0;
                        this.stage_ix[i] = (int)(this.stage_count[i] * (1.0 - this.out[i][0]));
                        this.stage[i] = 6;
                    }
                }
                switch (this.stage[i]) {
                    case 0: {
                        this.active[i][0] = 1.0;
                        if (this.on[i][0] < 0.5) {
                            break;
                        }
                        this.stage[i] = 1;
                        this.stage_ix[i] = (int)(Math.pow(2.0, this.delay[i][0] / 1200.0) / this.control_time);
                        if (this.stage_ix[i] < 0) {
                            this.stage_ix[i] = 0;
                        }
                    }
                    case 1: {
                        if (this.stage_ix[i] == 0) {
                            final double n2 = this.attack[i][0];
                            final double n3 = this.attack2[i][0];
                            if (n3 < 1.0E-6 && n2 < 0.0 && Double.isInfinite(n2)) {
                                this.out[i][0] = 1.0;
                                this.stage[i] = 3;
                                this.stage_count[i] = (int)(Math.pow(2.0, this.hold[i][0] / 1200.0) / this.control_time);
                                this.stage_ix[i] = 0;
                            }
                            else {
                                this.stage[i] = 2;
                                this.stage_count[i] = (int)(Math.pow(2.0, n2 / 1200.0) / this.control_time);
                                final int[] stage_count2 = this.stage_count;
                                final int n4 = i;
                                stage_count2[n4] += (int)(n3 / (this.control_time * 1000.0));
                                if (this.stage_count[i] < 0) {
                                    this.stage_count[i] = 0;
                                }
                                this.stage_ix[i] = 0;
                            }
                            break;
                        }
                        final int[] stage_ix = this.stage_ix;
                        final int n5 = i;
                        --stage_ix[n5];
                        break;
                    }
                    case 2: {
                        final int[] stage_ix2 = this.stage_ix;
                        final int n6 = i;
                        ++stage_ix2[n6];
                        if (this.stage_ix[i] >= this.stage_count[i]) {
                            this.out[i][0] = 1.0;
                            this.stage[i] = 3;
                            break;
                        }
                        double n7 = 1.0 + 0.4166666666666667 / Math.log(10.0) * Math.log(this.stage_ix[i] / (double)this.stage_count[i]);
                        if (n7 < 0.0) {
                            n7 = 0.0;
                        }
                        else if (n7 > 1.0) {
                            n7 = 1.0;
                        }
                        this.out[i][0] = n7;
                        break;
                    }
                    case 3: {
                        final int[] stage_ix3 = this.stage_ix;
                        final int n8 = i;
                        ++stage_ix3[n8];
                        if (this.stage_ix[i] >= this.stage_count[i]) {
                            this.stage[i] = 4;
                            this.stage_count[i] = (int)(Math.pow(2.0, this.decay[i][0] / 1200.0) / this.control_time);
                            final int[] stage_count3 = this.stage_count;
                            final int n9 = i;
                            stage_count3[n9] += (int)(this.decay2[i][0] / (this.control_time * 1000.0));
                            if (this.stage_count[i] < 0) {
                                this.stage_count[i] = 0;
                            }
                            this.stage_ix[i] = 0;
                            break;
                        }
                        break;
                    }
                    case 4: {
                        final int[] stage_ix4 = this.stage_ix;
                        final int n10 = i;
                        ++stage_ix4[n10];
                        final double n11 = this.sustain[i][0] * 0.001;
                        if (this.stage_ix[i] < this.stage_count[i]) {
                            final double n12 = this.stage_ix[i] / (double)this.stage_count[i];
                            this.out[i][0] = 1.0 - n12 + n11 * n12;
                            break;
                        }
                        this.out[i][0] = n11;
                        this.stage[i] = 5;
                        if (n11 < 0.001) {
                            this.out[i][0] = 0.0;
                            this.active[i][0] = 0.0;
                            this.stage[i] = 8;
                            break;
                        }
                        break;
                    }
                    case 6: {
                        final int[] stage_ix5 = this.stage_ix;
                        final int n13 = i;
                        ++stage_ix5[n13];
                        if (this.stage_ix[i] >= this.stage_count[i]) {
                            this.out[i][0] = 0.0;
                            this.active[i][0] = 0.0;
                            this.stage[i] = 8;
                            break;
                        }
                        this.out[i][0] = 1.0 - this.stage_ix[i] / (double)this.stage_count[i];
                        if (this.on[i][0] < -0.5) {
                            this.stage_count[i] = (int)(Math.pow(2.0, this.shutdown[i][0] / 1200.0) / this.control_time);
                            if (this.stage_count[i] < 0) {
                                this.stage_count[i] = 0;
                            }
                            this.stage_v[i] = this.out[i][0];
                            this.stage_ix[i] = 0;
                            this.stage[i] = 7;
                        }
                        if (this.on[i][0] > 0.5) {
                            final double n14 = this.sustain[i][0] * 0.001;
                            if (this.out[i][0] > n14) {
                                this.stage[i] = 4;
                                this.stage_count[i] = (int)(Math.pow(2.0, this.decay[i][0] / 1200.0) / this.control_time);
                                final int[] stage_count4 = this.stage_count;
                                final int n15 = i;
                                stage_count4[n15] += (int)(this.decay2[i][0] / (this.control_time * 1000.0));
                                if (this.stage_count[i] < 0) {
                                    this.stage_count[i] = 0;
                                }
                                this.stage_ix[i] = (int)(this.stage_count[i] * ((this.out[i][0] - 1.0) / (n14 - 1.0)));
                            }
                        }
                        break;
                    }
                    case 7: {
                        final int[] stage_ix6 = this.stage_ix;
                        final int n16 = i;
                        ++stage_ix6[n16];
                        if (this.stage_ix[i] >= this.stage_count[i]) {
                            this.out[i][0] = 0.0;
                            this.active[i][0] = 0.0;
                            this.stage[i] = 8;
                            break;
                        }
                        this.out[i][0] = (1.0 - this.stage_ix[i] / (double)this.stage_count[i]) * this.stage_v[i];
                        break;
                    }
                }
            }
        }
    }
}
