package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeImpl;

public final class KeywordAttributeImpl extends AttributeImpl implements KeywordAttribute
{
    private boolean keyword;
    
    @Override
    public void clear() {
        this.keyword = false;
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final KeywordAttribute attr = (KeywordAttribute)target;
        attr.setKeyword(this.keyword);
    }
    
    @Override
    public int hashCode() {
        return this.keyword ? 31 : 37;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final KeywordAttributeImpl other = (KeywordAttributeImpl)obj;
        return this.keyword == other.keyword;
    }
    
    @Override
    public boolean isKeyword() {
        return this.keyword;
    }
    
    @Override
    public void setKeyword(final boolean isKeyword) {
        this.keyword = isKeyword;
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(KeywordAttribute.class, "keyword", this.keyword);
    }
}
