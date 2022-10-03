package io.opencensus.stats;

final class AutoValue_View_Name extends View.Name
{
    private final String asString;
    
    AutoValue_View_Name(final String asString) {
        if (asString == null) {
            throw new NullPointerException("Null asString");
        }
        this.asString = asString;
    }
    
    @Override
    public String asString() {
        return this.asString;
    }
    
    @Override
    public String toString() {
        return "Name{asString=" + this.asString + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof View.Name) {
            final View.Name that = (View.Name)o;
            return this.asString.equals(that.asString());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.asString.hashCode();
        return h;
    }
}
