package org.apache.lucene.facet.taxonomy;

import java.util.Arrays;

public class FacetLabel implements Comparable<FacetLabel>
{
    public static final int MAX_CATEGORY_PATH_LENGTH = 8191;
    public final String[] components;
    public final int length;
    
    private FacetLabel(final FacetLabel copyFrom, final int prefixLen) {
        assert prefixLen >= 0 && prefixLen <= copyFrom.components.length : "prefixLen cannot be negative nor larger than the given components' length: prefixLen=" + prefixLen + " components.length=" + copyFrom.components.length;
        this.components = copyFrom.components;
        this.length = prefixLen;
    }
    
    public FacetLabel(final String... components) {
        this.components = components;
        this.length = components.length;
        this.checkComponents();
    }
    
    public FacetLabel(final String dim, final String[] path) {
        (this.components = new String[1 + path.length])[0] = dim;
        System.arraycopy(path, 0, this.components, 1, path.length);
        this.length = this.components.length;
        this.checkComponents();
    }
    
    private void checkComponents() {
        long len = 0L;
        for (final String comp : this.components) {
            if (comp == null || comp.isEmpty()) {
                throw new IllegalArgumentException("empty or null components not allowed: " + Arrays.toString(this.components));
            }
            len += comp.length();
        }
        len += this.components.length - 1;
        if (len > 8191L) {
            throw new IllegalArgumentException("category path exceeds maximum allowed path length: max=8191 len=" + len + " path=" + Arrays.toString(this.components).substring(0, 30) + "...");
        }
    }
    
    @Override
    public int compareTo(final FacetLabel other) {
        for (int len = (this.length < other.length) ? this.length : other.length, i = 0, j = 0; i < len; ++i, ++j) {
            final int cmp = this.components[i].compareTo(other.components[j]);
            if (cmp < 0) {
                return -1;
            }
            if (cmp > 0) {
                return 1;
            }
        }
        return this.length - other.length;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FacetLabel)) {
            return false;
        }
        final FacetLabel other = (FacetLabel)obj;
        if (this.length != other.length) {
            return false;
        }
        for (int i = this.length - 1; i >= 0; --i) {
            if (!this.components[i].equals(other.components[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.length == 0) {
            return 0;
        }
        int hash = this.length;
        for (int i = 0; i < this.length; ++i) {
            hash = hash * 31 + this.components[i].hashCode();
        }
        return hash;
    }
    
    public long longHashCode() {
        if (this.length == 0) {
            return 0L;
        }
        long hash = this.length;
        for (int i = 0; i < this.length; ++i) {
            hash = hash * 65599L + this.components[i].hashCode();
        }
        return hash;
    }
    
    public FacetLabel subpath(final int length) {
        if (length >= this.length || length < 0) {
            return this;
        }
        return new FacetLabel(this, length);
    }
    
    @Override
    public String toString() {
        if (this.length == 0) {
            return "FacetLabel: []";
        }
        final String[] parts = new String[this.length];
        System.arraycopy(this.components, 0, parts, 0, this.length);
        return "FacetLabel: " + Arrays.toString(parts);
    }
}
