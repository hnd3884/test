package org.apache.lucene.analysis;

import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;

@Deprecated
public class Token extends PackedTokenAttributeImpl implements FlagsAttribute, PayloadAttribute
{
    private int flags;
    private BytesRef payload;
    public static final AttributeFactory TOKEN_ATTRIBUTE_FACTORY;
    
    public Token() {
    }
    
    public Token(final CharSequence text, final int start, final int end) {
        this.append(text);
        this.setOffset(start, end);
    }
    
    @Override
    public int getFlags() {
        return this.flags;
    }
    
    @Override
    public void setFlags(final int flags) {
        this.flags = flags;
    }
    
    @Override
    public BytesRef getPayload() {
        return this.payload;
    }
    
    @Override
    public void setPayload(final BytesRef payload) {
        this.payload = payload;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.flags = 0;
        this.payload = null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Token) {
            final Token other = (Token)obj;
            if (this.flags == other.flags) {
                if (this.payload == null) {
                    if (other.payload != null) {
                        return false;
                    }
                }
                else if (!this.payload.equals(other.payload)) {
                    return false;
                }
                if (super.equals(obj)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = super.hashCode();
        code = code * 31 + this.flags;
        if (this.payload != null) {
            code = code * 31 + this.payload.hashCode();
        }
        return code;
    }
    
    @Override
    public Token clone() {
        final Token t = (Token)super.clone();
        if (this.payload != null) {
            t.payload = this.payload.clone();
        }
        return t;
    }
    
    public void reinit(final Token prototype) {
        prototype.copyToWithoutPayloadClone(this);
    }
    
    private void copyToWithoutPayloadClone(final AttributeImpl target) {
        super.copyTo(target);
        ((FlagsAttribute)target).setFlags(this.flags);
        ((PayloadAttribute)target).setPayload(this.payload);
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        super.copyTo(target);
        ((FlagsAttribute)target).setFlags(this.flags);
        ((PayloadAttribute)target).setPayload((this.payload == null) ? null : this.payload.clone());
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        super.reflectWith(reflector);
        reflector.reflect(FlagsAttribute.class, "flags", this.flags);
        reflector.reflect(PayloadAttribute.class, "payload", this.payload);
    }
    
    static {
        TOKEN_ATTRIBUTE_FACTORY = AttributeFactory.getStaticImplementation(AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, Token.class);
    }
}
