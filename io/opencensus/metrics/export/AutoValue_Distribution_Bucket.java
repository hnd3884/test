package io.opencensus.metrics.export;

import javax.annotation.Nullable;
import io.opencensus.metrics.data.Exemplar;

final class AutoValue_Distribution_Bucket extends Distribution.Bucket
{
    private final long count;
    private final Exemplar exemplar;
    
    AutoValue_Distribution_Bucket(final long count, @Nullable final Exemplar exemplar) {
        this.count = count;
        this.exemplar = exemplar;
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    @Nullable
    @Override
    public Exemplar getExemplar() {
        return this.exemplar;
    }
    
    @Override
    public String toString() {
        return "Bucket{count=" + this.count + ", exemplar=" + this.exemplar + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Distribution.Bucket) {
            final Distribution.Bucket that = (Distribution.Bucket)o;
            return this.count == that.getCount() && ((this.exemplar != null) ? this.exemplar.equals(that.getExemplar()) : (that.getExemplar() == null));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.count >>> 32 ^ this.count));
        h *= 1000003;
        h ^= ((this.exemplar == null) ? 0 : this.exemplar.hashCode());
        return h;
    }
}
