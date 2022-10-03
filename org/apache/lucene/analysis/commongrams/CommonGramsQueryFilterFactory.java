package org.apache.lucene.analysis.commongrams;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;

public class CommonGramsQueryFilterFactory extends CommonGramsFilterFactory
{
    public CommonGramsQueryFilterFactory(final Map<String, String> args) {
        super(args);
    }
    
    @Override
    public TokenFilter create(final TokenStream input) {
        final CommonGramsFilter commonGrams = (CommonGramsFilter)super.create(input);
        return new CommonGramsQueryFilter(commonGrams);
    }
}
