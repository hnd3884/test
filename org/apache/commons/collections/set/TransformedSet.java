package org.apache.commons.collections.set;

import java.util.Collection;
import org.apache.commons.collections.Transformer;
import java.util.Set;
import org.apache.commons.collections.collection.TransformedCollection;

public class TransformedSet extends TransformedCollection implements Set
{
    private static final long serialVersionUID = 306127383500410386L;
    
    public static Set decorate(final Set set, final Transformer transformer) {
        return new TransformedSet(set, transformer);
    }
    
    protected TransformedSet(final Set set, final Transformer transformer) {
        super(set, transformer);
    }
}
