package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DocTermsIndexDocValues;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueStr;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;

public class BytesRefFieldSource extends FieldCacheSource
{
    public BytesRefFieldSource(final String field) {
        super(field);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FieldInfo fieldInfo = readerContext.reader().getFieldInfos().fieldInfo(this.field);
        if (fieldInfo != null && fieldInfo.getDocValuesType() == DocValuesType.BINARY) {
            final BinaryDocValues binaryValues = DocValues.getBinary(readerContext.reader(), this.field);
            final Bits docsWithField = DocValues.getDocsWithField(readerContext.reader(), this.field);
            return new FunctionValues() {
                @Override
                public boolean exists(final int doc) {
                    return docsWithField.get(doc);
                }
                
                @Override
                public boolean bytesVal(final int doc, final BytesRefBuilder target) {
                    target.copyBytes(binaryValues.get(doc));
                    return target.length() > 0;
                }
                
                @Override
                public String strVal(final int doc) {
                    final BytesRefBuilder bytes = new BytesRefBuilder();
                    return this.bytesVal(doc, bytes) ? bytes.get().utf8ToString() : null;
                }
                
                @Override
                public Object objectVal(final int doc) {
                    return this.strVal(doc);
                }
                
                @Override
                public String toString(final int doc) {
                    return BytesRefFieldSource.this.description() + '=' + this.strVal(doc);
                }
                
                @Override
                public ValueFiller getValueFiller() {
                    return new ValueFiller() {
                        private final MutableValueStr mval = new MutableValueStr();
                        
                        @Override
                        public MutableValue getValue() {
                            return (MutableValue)this.mval;
                        }
                        
                        @Override
                        public void fillValue(final int doc) {
                            this.mval.exists = docsWithField.get(doc);
                            this.mval.value.clear();
                            this.mval.value.copyBytes(binaryValues.get(doc));
                        }
                    };
                }
            };
        }
        return new DocTermsIndexDocValues(this, readerContext, this.field) {
            @Override
            protected String toTerm(final String readableValue) {
                return readableValue;
            }
            
            @Override
            public Object objectVal(final int doc) {
                return this.strVal(doc);
            }
            
            @Override
            public String toString(final int doc) {
                return BytesRefFieldSource.this.description() + '=' + this.strVal(doc);
            }
        };
    }
}
