package org.apache.commons.collections.collection;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.collections.Transformer;

public class TransformedCollection extends AbstractSerializableCollectionDecorator
{
    private static final long serialVersionUID = 8692300188161871514L;
    protected final Transformer transformer;
    
    public static Collection decorate(final Collection coll, final Transformer transformer) {
        return new TransformedCollection(coll, transformer);
    }
    
    protected TransformedCollection(final Collection coll, final Transformer transformer) {
        super(coll);
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        this.transformer = transformer;
    }
    
    protected Object transform(final Object object) {
        return this.transformer.transform(object);
    }
    
    protected Collection transform(final Collection coll) {
        final List list = new ArrayList(coll.size());
        final Iterator it = coll.iterator();
        while (it.hasNext()) {
            list.add(this.transform(it.next()));
        }
        return list;
    }
    
    public boolean add(Object object) {
        object = this.transform(object);
        return this.getCollection().add(object);
    }
    
    public boolean addAll(Collection coll) {
        coll = this.transform(coll);
        return this.getCollection().addAll(coll);
    }
}
