package io.opencensus.common;

final class AutoValue_Timestamp extends Timestamp
{
    private final long seconds;
    private final int nanos;
    
    AutoValue_Timestamp(final long seconds, final int nanos) {
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
        return "Timestamp{seconds=" + this.seconds + ", nanos=" + this.nanos + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Timestamp) {
            final Timestamp that = (Timestamp)o;
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
