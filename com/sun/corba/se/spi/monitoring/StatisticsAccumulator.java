package com.sun.corba.se.spi.monitoring;

public class StatisticsAccumulator
{
    protected double max;
    protected double min;
    private double sampleSum;
    private double sampleSquareSum;
    private long sampleCount;
    protected String unit;
    
    public void sample(final double n) {
        ++this.sampleCount;
        if (n < this.min) {
            this.min = n;
        }
        if (n > this.max) {
            this.max = n;
        }
        this.sampleSum += n;
        this.sampleSquareSum += n * n;
    }
    
    public String getValue() {
        return this.toString();
    }
    
    @Override
    public String toString() {
        return "Minimum Value = " + this.min + " " + this.unit + " Maximum Value = " + this.max + " " + this.unit + " Average Value = " + this.computeAverage() + " " + this.unit + " Standard Deviation = " + this.computeStandardDeviation() + " " + this.unit + " Samples Collected = " + this.sampleCount;
    }
    
    protected double computeAverage() {
        return this.sampleSum / this.sampleCount;
    }
    
    protected double computeStandardDeviation() {
        return Math.sqrt((this.sampleSquareSum - this.sampleSum * this.sampleSum / this.sampleCount) / (this.sampleCount - 1L));
    }
    
    public StatisticsAccumulator(final String unit) {
        this.max = Double.MIN_VALUE;
        this.min = Double.MAX_VALUE;
        this.unit = unit;
        this.sampleCount = 0L;
        this.sampleSum = 0.0;
        this.sampleSquareSum = 0.0;
    }
    
    void clearState() {
        this.min = Double.MAX_VALUE;
        this.max = Double.MIN_VALUE;
        this.sampleCount = 0L;
        this.sampleSum = 0.0;
        this.sampleSquareSum = 0.0;
    }
    
    public void unitTestValidate(final String s, final double n, final double n2, final long n3, final double n4, final double n5) {
        if (!s.equals(this.unit)) {
            throw new RuntimeException("Unit is not same as expected Unit\nUnit = " + this.unit + "ExpectedUnit = " + s);
        }
        if (this.min != n) {
            throw new RuntimeException("Minimum value is not same as expected minimum value\nMin Value = " + this.min + "Expected Min Value = " + n);
        }
        if (this.max != n2) {
            throw new RuntimeException("Maximum value is not same as expected maximum value\nMax Value = " + this.max + "Expected Max Value = " + n2);
        }
        if (this.sampleCount != n3) {
            throw new RuntimeException("Sample count is not same as expected Sample Count\nSampleCount = " + this.sampleCount + "Expected Sample Count = " + n3);
        }
        if (this.computeAverage() != n4) {
            throw new RuntimeException("Average is not same as expected Average\nAverage = " + this.computeAverage() + "Expected Average = " + n4);
        }
        if (Math.abs(this.computeStandardDeviation() - n5) > 1.0) {
            throw new RuntimeException("Standard Deviation is not same as expected Std Deviation\nStandard Dev = " + this.computeStandardDeviation() + "Expected Standard Dev = " + n5);
        }
    }
}
