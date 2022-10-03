package org.owasp.esapi.reference;

import org.owasp.esapi.EncoderConstants;
import org.owasp.esapi.ESAPI;
import java.util.Set;

public class RandomAccessReferenceMap extends AbstractAccessReferenceMap<String>
{
    private static final long serialVersionUID = 8544133840739803001L;
    
    public RandomAccessReferenceMap(final int initialSize) {
        super(initialSize);
    }
    
    public RandomAccessReferenceMap() {
    }
    
    public RandomAccessReferenceMap(final Set<Object> directReferences) {
        super(directReferences.size());
        this.update(directReferences);
    }
    
    public RandomAccessReferenceMap(final Set<Object> directReferences, final int initialSize) {
        super(initialSize);
        this.update(directReferences);
    }
    
    @Override
    protected final synchronized String getUniqueReference() {
        String candidate;
        do {
            candidate = ESAPI.randomizer().getRandomString(6, EncoderConstants.CHAR_ALPHANUMERICS);
        } while (this.itod.keySet().contains(candidate));
        return candidate;
    }
}
