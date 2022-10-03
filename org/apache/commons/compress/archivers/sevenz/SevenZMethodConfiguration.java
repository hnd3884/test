package org.apache.commons.compress.archivers.sevenz;

import java.util.Objects;

public class SevenZMethodConfiguration
{
    private final SevenZMethod method;
    private final Object options;
    
    public SevenZMethodConfiguration(final SevenZMethod method) {
        this(method, null);
    }
    
    public SevenZMethodConfiguration(final SevenZMethod method, final Object options) {
        this.method = method;
        this.options = options;
        if (options != null && !Coders.findByMethod(method).canAcceptOptions(options)) {
            throw new IllegalArgumentException("The " + method + " method doesn't support options of type " + options.getClass());
        }
    }
    
    public SevenZMethod getMethod() {
        return this.method;
    }
    
    public Object getOptions() {
        return this.options;
    }
    
    @Override
    public int hashCode() {
        return (this.method == null) ? 0 : this.method.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final SevenZMethodConfiguration other = (SevenZMethodConfiguration)obj;
        return Objects.equals(this.method, other.method) && Objects.equals(this.options, other.options);
    }
}
