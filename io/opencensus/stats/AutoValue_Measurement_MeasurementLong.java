package io.opencensus.stats;

final class AutoValue_Measurement_MeasurementLong extends MeasurementLong
{
    private final Measure.MeasureLong measure;
    private final long value;
    
    AutoValue_Measurement_MeasurementLong(final Measure.MeasureLong measure, final long value) {
        if (measure == null) {
            throw new NullPointerException("Null measure");
        }
        this.measure = measure;
        this.value = value;
    }
    
    @Override
    public Measure.MeasureLong getMeasure() {
        return this.measure;
    }
    
    @Override
    public long getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "MeasurementLong{measure=" + this.measure + ", value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MeasurementLong) {
            final MeasurementLong that = (MeasurementLong)o;
            return this.measure.equals(that.getMeasure()) && this.value == that.getValue();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.measure.hashCode();
        h *= 1000003;
        h = (int)((long)h ^ (this.value >>> 32 ^ this.value));
        return h;
    }
}
