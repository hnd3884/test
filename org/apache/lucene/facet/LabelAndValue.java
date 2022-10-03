package org.apache.lucene.facet;

public final class LabelAndValue
{
    public final String label;
    public final Number value;
    
    public LabelAndValue(final String label, final Number value) {
        this.label = label;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.label + " (" + this.value + ")";
    }
    
    @Override
    public boolean equals(final Object _other) {
        if (!(_other instanceof LabelAndValue)) {
            return false;
        }
        final LabelAndValue other = (LabelAndValue)_other;
        return this.label.equals(other.label) && this.value.equals(other.value);
    }
    
    @Override
    public int hashCode() {
        return this.label.hashCode() + 1439 * this.value.hashCode();
    }
}
