package org.apache.lucene.analysis.core;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import java.util.Set;
import org.apache.lucene.analysis.util.Lucene43FilteringTokenFilter;

@Deprecated
public final class Lucene43TypeTokenFilter extends Lucene43FilteringTokenFilter
{
    private final Set<String> stopTypes;
    private final TypeAttribute typeAttribute;
    private final boolean useWhiteList;
    
    public Lucene43TypeTokenFilter(final boolean enablePositionIncrements, final TokenStream input, final Set<String> stopTypes, final boolean useWhiteList) {
        super(enablePositionIncrements, input);
        this.typeAttribute = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.stopTypes = stopTypes;
        this.useWhiteList = useWhiteList;
    }
    
    @Override
    protected boolean accept() throws IOException {
        return this.useWhiteList == this.stopTypes.contains(this.typeAttribute.type());
    }
}
