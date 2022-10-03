package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class NumDocsValueSource extends ValueSource
{
    public String name() {
        return "numdocs";
    }
    
    @Override
    public String description() {
        return this.name() + "()";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        return new ConstIntDocValues(ReaderUtil.getTopLevelContext((IndexReaderContext)readerContext).reader().numDocs(), this);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.getClass() == o.getClass();
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
