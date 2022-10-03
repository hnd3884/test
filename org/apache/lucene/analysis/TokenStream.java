package org.apache.lucene.analysis;

import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import java.io.IOException;
import java.lang.reflect.Modifier;
import org.apache.lucene.util.AttributeFactory;
import java.io.Closeable;
import org.apache.lucene.util.AttributeSource;

public abstract class TokenStream extends AttributeSource implements Closeable
{
    public static final AttributeFactory DEFAULT_TOKEN_ATTRIBUTE_FACTORY;
    
    protected TokenStream() {
        super(TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY);
        assert this.assertFinal();
    }
    
    protected TokenStream(final AttributeSource input) {
        super(input);
        assert this.assertFinal();
    }
    
    protected TokenStream(final AttributeFactory factory) {
        super(factory);
        assert this.assertFinal();
    }
    
    private boolean assertFinal() {
        try {
            final Class<?> clazz = this.getClass();
            if (!clazz.desiredAssertionStatus()) {
                return true;
            }
            assert !(!Modifier.isFinal(clazz.getMethod("incrementToken", (Class<?>[])new Class[0]).getModifiers())) : "TokenStream implementation classes or at least their incrementToken() implementation must be final";
            return true;
        }
        catch (final NoSuchMethodException nsme) {
            return false;
        }
    }
    
    public abstract boolean incrementToken() throws IOException;
    
    public void end() throws IOException {
        this.clearAttributes();
        final PositionIncrementAttribute posIncAtt = this.getAttribute(PositionIncrementAttribute.class);
        if (posIncAtt != null) {
            posIncAtt.setPositionIncrement(0);
        }
    }
    
    public void reset() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
    }
    
    static {
        DEFAULT_TOKEN_ATTRIBUTE_FACTORY = AttributeFactory.getStaticImplementation(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, PackedTokenAttributeImpl.class);
    }
}
