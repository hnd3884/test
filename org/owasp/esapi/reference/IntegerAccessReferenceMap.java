package org.owasp.esapi.reference;

import java.util.Set;

public class IntegerAccessReferenceMap extends AbstractAccessReferenceMap<String>
{
    private static final long serialVersionUID = 5311769278372489771L;
    int count;
    
    public IntegerAccessReferenceMap() {
        this.count = 1;
    }
    
    public IntegerAccessReferenceMap(final int initialSize) {
        super(initialSize);
        this.count = 1;
    }
    
    public IntegerAccessReferenceMap(final Set<Object> directReferences) {
        super(directReferences.size());
        this.count = 1;
        this.update(directReferences);
    }
    
    public IntegerAccessReferenceMap(final Set<Object> directReferences, final int initialSize) {
        super(initialSize);
        this.count = 1;
        this.update(directReferences);
    }
    
    @Override
    protected final synchronized String getUniqueReference() {
        return "" + this.count++;
    }
}
