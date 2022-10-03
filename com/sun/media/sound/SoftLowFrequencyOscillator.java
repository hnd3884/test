package com.sun.media.sound;

public final class SoftLowFrequencyOscillator implements SoftProcess
{
    private final int max_count = 10;
    private int used_count;
    private final double[][] out;
    private final double[][] delay;
    private final double[][] delay2;
    private final double[][] freq;
    private final int[] delay_counter;
    private final double[] sin_phase;
    private final double[] sin_stepfreq;
    private final double[] sin_step;
    private double control_time;
    private double sin_factor;
    private static final double PI2 = 6.283185307179586;
    
    public SoftLowFrequencyOscillator() {
        this.used_count = 0;
        this.out = new double[10][1];
        this.delay = new double[10][1];
        this.delay2 = new double[10][1];
        this.freq = new double[10][1];
        this.delay_counter = new int[10];
        this.sin_phase = new double[10];
        this.sin_stepfreq = new double[10];
        this.sin_step = new double[10];
        this.control_time = 0.0;
        this.sin_factor = 0.0;
        for (int i = 0; i < this.sin_stepfreq.length; ++i) {
            this.sin_stepfreq[i] = Double.NEGATIVE_INFINITY;
        }
    }
    
    @Override
    public void reset() {
        for (int i = 0; i < this.used_count; ++i) {
            this.out[i][0] = 0.0;
            this.delay[i][0] = 0.0;
            this.delay2[i][0] = 0.0;
            this.freq[i][0] = 0.0;
            this.delay_counter[i] = 0;
            this.sin_phase[i] = 0.0;
            this.sin_stepfreq[i] = Double.NEGATIVE_INFINITY;
            this.sin_step[i] = 0.0;
        }
        this.used_count = 0;
    }
    
    @Override
    public void init(final SoftSynthesizer softSynthesizer) {
        this.control_time = 1.0 / softSynthesizer.getControlRate();
        this.sin_factor = this.control_time * 2.0 * 3.141592653589793;
        for (int i = 0; i < this.used_count; ++i) {
            this.delay_counter[i] = (int)(Math.pow(2.0, this.delay[i][0] / 1200.0) / this.control_time);
            final int[] delay_counter = this.delay_counter;
            final int n = i;
            delay_counter[n] += (int)(this.delay2[i][0] / (this.control_time * 1000.0));
        }
        this.processControlLogic();
    }
    
    @Override
    public void processControlLogic() {
        for (int i = 0; i < this.used_count; ++i) {
            if (this.delay_counter[i] > 0) {
                final int[] delay_counter = this.delay_counter;
                final int n = i;
                --delay_counter[n];
                this.out[i][0] = 0.5;
            }
            else {
                final double n2 = this.freq[i][0];
                if (this.sin_stepfreq[i] != n2) {
                    this.sin_stepfreq[i] = n2;
                    this.sin_step[i] = 440.0 * Math.exp((n2 - 6900.0) * (Math.log(2.0) / 1200.0)) * this.sin_factor;
                }
                double n3;
                for (n3 = this.sin_phase[i] + this.sin_step[i]; n3 > 6.283185307179586; n3 -= 6.283185307179586) {}
                this.out[i][0] = 0.5 + Math.sin(n3) * 0.5;
                this.sin_phase[i] = n3;
            }
        }
    }
    
    @Override
    public double[] get(final int n, final String s) {
        if (n >= this.used_count) {
            this.used_count = n + 1;
        }
        if (s == null) {
            return this.out[n];
        }
        if (s.equals("delay")) {
            return this.delay[n];
        }
        if (s.equals("delay2")) {
            return this.delay2[n];
        }
        if (s.equals("freq")) {
            return this.freq[n];
        }
        return null;
    }
}
