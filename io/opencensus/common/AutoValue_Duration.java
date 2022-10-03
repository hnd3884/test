package io.opencensus.common;

final class AutoValue_Duration extends Duration
{
    private final long seconds;
    private final int nanos;
    
    AutoValue_Duration(final long seconds, final int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }
    
    @Override
    public long getSeconds() {
        return this.seconds;
    }
    
    @Override
    public int getNanos() {
        return this.nanos;
    }
    
    @Override
    public String toString() {
        return "Duration{seconds=" + this.seconds + ", nanos=" + this.nanos + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Duration) {
            final Duration that = (Duration)o;
            return this.seconds == that.getSeconds() && this.nanos == that.getNanos();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.seconds >>> 32 ^ this.seconds));
        h *= 1000003;
        h ^= this.nanos;
        return h;
    }
}
