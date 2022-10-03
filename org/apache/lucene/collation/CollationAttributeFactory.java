package org.apache.lucene.collation;

import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.analysis.TokenStream;
import java.text.Collator;
import org.apache.lucene.collation.tokenattributes.CollatedTermAttributeImpl;
import org.apache.lucene.util.AttributeFactory;

public class CollationAttributeFactory extends AttributeFactory.StaticImplementationAttributeFactory<CollatedTermAttributeImpl>
{
    private final Collator collator;
    
    public CollationAttributeFactory(final Collator collator) {
        this(TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, collator);
    }
    
    public CollationAttributeFactory(final AttributeFactory delegate, final Collator collator) {
        super(delegate, (Class)CollatedTermAttributeImpl.class);
        this.collator = collator;
    }
    
    public CollatedTermAttributeImpl createInstance() {
        return new CollatedTermAttributeImpl(this.collator);
    }
}
