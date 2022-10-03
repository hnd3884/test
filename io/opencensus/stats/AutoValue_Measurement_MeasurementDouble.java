package io.opencensus.stats;

final class AutoValue_Measurement_MeasurementDouble extends MeasurementDouble
{
    private final Measure.MeasureDouble measure;
    private final double value;
    
    AutoValue_Measurement_MeasurementDouble(final Measure.MeasureDouble measure, final double value) {
        if (measure == null) {
            throw new NullPointerException("Null measure");
        }
        this.measure = measure;
        this.value = value;
    }
    
    @Override
    public Measure.MeasureDouble getMeasure() {
        return this.measure;
    }
    
    @Override
    public double getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "MeasurementDouble{measure=" + this.measure + ", value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MeasurementDouble) {
            final MeasurementDouble that = (MeasurementDouble)o;
            return this.measure.equals(that.getMeasure()) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.measure.hashCode();
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.value) >>> 32 ^ Double.doubleToLongBits(this.value)));
        return h;
    }
}
