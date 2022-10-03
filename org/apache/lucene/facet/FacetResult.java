package org.apache.lucene.facet;

import java.util.Arrays;

public final class FacetResult
{
    public final String dim;
    public final String[] path;
    public final Number value;
    public final int childCount;
    public final LabelAndValue[] labelValues;
    
    public FacetResult(final String dim, final String[] path, final Number value, final LabelAndValue[] labelValues, final int childCount) {
        this.dim = dim;
        this.path = path;
        this.value = value;
        this.labelValues = labelValues;
        this.childCount = childCount;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("dim=");
        sb.append(this.dim);
        sb.append(" path=");
        sb.append(Arrays.toString(this.path));
        sb.append(" value=");
        sb.append(this.value);
        sb.append(" childCount=");
        sb.append(this.childCount);
        sb.append('\n');
        for (final LabelAndValue labelValue : this.labelValues) {
            sb.append("  " + labelValue + "\n");
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object _other) {
        if (!(_other instanceof FacetResult)) {
            return false;
        }
        final FacetResult other = (FacetResult)_other;
        return this.value.equals(other.value) && this.childCount == other.childCount && Arrays.equals(this.labelValues, other.labelValues);
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.value.hashCode() + 31 * this.childCount;
        for (final LabelAndValue labelValue : this.labelValues) {
            hashCode = labelValue.hashCode() + 31 * hashCode;
        }
        return hashCode;
    }
}
