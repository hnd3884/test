package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import java.util.Set;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

public final class TypeTokenFilter extends FilteringTokenFilter
{
    private final Set<String> stopTypes;
    private final TypeAttribute typeAttribute;
    private final boolean useWhiteList;
    
    public TypeTokenFilter(final TokenStream input, final Set<String> stopTypes, final boolean useWhiteList) {
        super(input);
        this.typeAttribute = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.stopTypes = stopTypes;
        this.useWhiteList = useWhiteList;
    }
    
    public TypeTokenFilter(final TokenStream input, final Set<String> stopTypes) {
        this(input, stopTypes, false);
    }
    
    @Override
    protected boolean accept() {
        return this.useWhiteList == this.stopTypes.contains(this.typeAttribute.type());
    }
}
