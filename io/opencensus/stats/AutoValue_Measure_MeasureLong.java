package io.opencensus.stats;

final class AutoValue_Measure_MeasureLong extends MeasureLong
{
    private final String name;
    private final String description;
    private final String unit;
    
    AutoValue_Measure_MeasureLong(final String name, final String description, final String unit) {
        if (name == null) {
            throw new NullPointerException("Null name");
        }
        this.name = name;
        if (description == null) {
            throw new NullPointerException("Null description");
        }
        this.description = description;
        if (unit == null) {
            throw new NullPointerException("Null unit");
        }
        this.unit = unit;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public String getUnit() {
        return this.unit;
    }
    
    @Override
    public String toString() {
        return "MeasureLong{name=" + this.name + ", description=" + this.description + ", unit=" + this.unit + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MeasureLong) {
            final MeasureLong that = (MeasureLong)o;
            return this.name.equals(that.getName()) && this.description.equals(that.getDescription()) && this.unit.equals(that.getUnit());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.name.hashCode();
        h *= 1000003;
        h ^= this.description.hashCode();
        h *= 1000003;
        h ^= this.unit.hashCode();
        return h;
    }
}
