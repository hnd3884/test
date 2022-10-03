package org.apache.lucene.index;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;
import java.io.Reader;
import org.apache.lucene.util.BytesRef;

public interface IndexableField
{
    String name();
    
    IndexableFieldType fieldType();
    
    float boost();
    
    BytesRef binaryValue();
    
    String stringValue();
    
    Reader readerValue();
    
    Number numericValue();
    
    TokenStream tokenStream(final Analyzer p0, final TokenStream p1);
}
