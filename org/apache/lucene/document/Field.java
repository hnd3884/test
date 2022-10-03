package org.apache.lucene.document;

import java.io.IOException;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.BytesTermAttribute;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.analysis.NumericTokenStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.IndexOptions;
import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexableField;

public class Field implements IndexableField
{
    protected final FieldType type;
    protected final String name;
    protected Object fieldsData;
    protected TokenStream tokenStream;
    protected float boost;
    
    protected Field(final String name, final FieldType type) {
        this.boost = 1.0f;
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        this.name = name;
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.type = type;
    }
    
    public Field(final String name, final Reader reader, final FieldType type) {
        this.boost = 1.0f;
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (reader == null) {
            throw new NullPointerException("reader cannot be null");
        }
        if (type.stored()) {
            throw new IllegalArgumentException("fields with a Reader value cannot be stored");
        }
        if (type.indexOptions() != IndexOptions.NONE && !type.tokenized()) {
            throw new IllegalArgumentException("non-tokenized fields must use String values");
        }
        this.name = name;
        this.fieldsData = reader;
        this.type = type;
    }
    
    public Field(final String name, final TokenStream tokenStream, final FieldType type) {
        this.boost = 1.0f;
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (tokenStream == null) {
            throw new NullPointerException("tokenStream cannot be null");
        }
        if (type.indexOptions() == IndexOptions.NONE || !type.tokenized()) {
            throw new IllegalArgumentException("TokenStream fields must be indexed and tokenized");
        }
        if (type.stored()) {
            throw new IllegalArgumentException("TokenStream fields cannot be stored");
        }
        this.name = name;
        this.fieldsData = null;
        this.tokenStream = tokenStream;
        this.type = type;
    }
    
    public Field(final String name, final byte[] value, final FieldType type) {
        this(name, value, 0, value.length, type);
    }
    
    public Field(final String name, final byte[] value, final int offset, final int length, final FieldType type) {
        this(name, new BytesRef(value, offset, length), type);
    }
    
    public Field(final String name, final BytesRef bytes, final FieldType type) {
        this.boost = 1.0f;
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (bytes == null) {
            throw new IllegalArgumentException("bytes cannot be null");
        }
        this.fieldsData = bytes;
        this.type = type;
        this.name = name;
    }
    
    public Field(final String name, final String value, final FieldType type) {
        this.boost = 1.0f;
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        if (!type.stored() && type.indexOptions() == IndexOptions.NONE) {
            throw new IllegalArgumentException("it doesn't make sense to have a field that is neither indexed nor stored");
        }
        this.type = type;
        this.name = name;
        this.fieldsData = value;
    }
    
    @Override
    public String stringValue() {
        if (this.fieldsData instanceof String || this.fieldsData instanceof Number) {
            return this.fieldsData.toString();
        }
        return null;
    }
    
    @Override
    public Reader readerValue() {
        return (this.fieldsData instanceof Reader) ? ((Reader)this.fieldsData) : null;
    }
    
    public TokenStream tokenStreamValue() {
        return this.tokenStream;
    }
    
    public void setStringValue(final String value) {
        if (!(this.fieldsData instanceof String)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to String");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        this.fieldsData = value;
    }
    
    public void setReaderValue(final Reader value) {
        if (!(this.fieldsData instanceof Reader)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Reader");
        }
        this.fieldsData = value;
    }
    
    public void setBytesValue(final byte[] value) {
        this.setBytesValue(new BytesRef(value));
    }
    
    public void setBytesValue(final BytesRef value) {
        if (!(this.fieldsData instanceof BytesRef)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to BytesRef");
        }
        if (this.type.indexOptions() != IndexOptions.NONE) {
            throw new IllegalArgumentException("cannot set a BytesRef value on an indexed field");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        this.fieldsData = value;
    }
    
    public void setByteValue(final byte value) {
        if (!(this.fieldsData instanceof Byte)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Byte");
        }
        this.fieldsData = value;
    }
    
    public void setShortValue(final short value) {
        if (!(this.fieldsData instanceof Short)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Short");
        }
        this.fieldsData = value;
    }
    
    public void setIntValue(final int value) {
        if (!(this.fieldsData instanceof Integer)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Integer");
        }
        this.fieldsData = value;
    }
    
    public void setLongValue(final long value) {
        if (!(this.fieldsData instanceof Long)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Long");
        }
        this.fieldsData = value;
    }
    
    public void setFloatValue(final float value) {
        if (!(this.fieldsData instanceof Float)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Float");
        }
        this.fieldsData = value;
    }
    
    public void setDoubleValue(final double value) {
        if (!(this.fieldsData instanceof Double)) {
            throw new IllegalArgumentException("cannot change value type from " + this.fieldsData.getClass().getSimpleName() + " to Double");
        }
        this.fieldsData = value;
    }
    
    public void setTokenStream(final TokenStream tokenStream) {
        if (this.type.indexOptions() == IndexOptions.NONE || !this.type.tokenized()) {
            throw new IllegalArgumentException("TokenStream fields must be indexed and tokenized");
        }
        if (this.type.numericType() != null) {
            throw new IllegalArgumentException("cannot set private TokenStream on numeric fields");
        }
        this.tokenStream = tokenStream;
    }
    
    @Override
    public String name() {
        return this.name;
    }
    
    @Override
    public float boost() {
        return this.boost;
    }
    
    public void setBoost(final float boost) {
        if (boost != 1.0f && (this.type.indexOptions() == IndexOptions.NONE || this.type.omitNorms())) {
            throw new IllegalArgumentException("You cannot set an index-time boost on an unindexed field, or one that omits norms");
        }
        this.boost = boost;
    }
    
    @Override
    public Number numericValue() {
        if (this.fieldsData instanceof Number) {
            return (Number)this.fieldsData;
        }
        return null;
    }
    
    @Override
    public BytesRef binaryValue() {
        if (this.fieldsData instanceof BytesRef) {
            return (BytesRef)this.fieldsData;
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.type.toString());
        result.append('<');
        result.append(this.name);
        result.append(':');
        if (this.fieldsData != null) {
            result.append(this.fieldsData);
        }
        result.append('>');
        return result.toString();
    }
    
    @Override
    public FieldType fieldType() {
        return this.type;
    }
    
    @Override
    public TokenStream tokenStream(final Analyzer analyzer, TokenStream reuse) {
        if (this.fieldType().indexOptions() == IndexOptions.NONE) {
            return null;
        }
        final FieldType.NumericType numericType = this.fieldType().numericType();
        if (numericType != null) {
            if (!(reuse instanceof NumericTokenStream) || ((NumericTokenStream)reuse).getPrecisionStep() != this.type.numericPrecisionStep()) {
                reuse = new NumericTokenStream(this.type.numericPrecisionStep());
            }
            final NumericTokenStream nts = (NumericTokenStream)reuse;
            final Number val = (Number)this.fieldsData;
            switch (numericType) {
                case INT: {
                    nts.setIntValue(val.intValue());
                    break;
                }
                case LONG: {
                    nts.setLongValue(val.longValue());
                    break;
                }
                case FLOAT: {
                    nts.setFloatValue(val.floatValue());
                    break;
                }
                case DOUBLE: {
                    nts.setDoubleValue(val.doubleValue());
                    break;
                }
                default: {
                    throw new AssertionError((Object)"Should never get here");
                }
            }
            return reuse;
        }
        if (!this.fieldType().tokenized()) {
            if (this.stringValue() != null) {
                if (!(reuse instanceof StringTokenStream)) {
                    reuse = new StringTokenStream();
                }
                ((StringTokenStream)reuse).setValue(this.stringValue());
                return reuse;
            }
            if (this.binaryValue() != null) {
                if (!(reuse instanceof BinaryTokenStream)) {
                    reuse = new BinaryTokenStream();
                }
                ((BinaryTokenStream)reuse).setValue(this.binaryValue());
                return reuse;
            }
            throw new IllegalArgumentException("Non-Tokenized Fields must have a String value");
        }
        else {
            if (this.tokenStream != null) {
                return this.tokenStream;
            }
            if (this.readerValue() != null) {
                return analyzer.tokenStream(this.name(), this.readerValue());
            }
            if (this.stringValue() != null) {
                return analyzer.tokenStream(this.name(), this.stringValue());
            }
            throw new IllegalArgumentException("Field must have either TokenStream, String, Reader or Number value; got " + this);
        }
    }
    
    @Deprecated
    public static final FieldType translateFieldType(final Store store, final Index index, final TermVector termVector) {
        final FieldType ft = new FieldType();
        ft.setStored(store == Store.YES);
        switch (index) {
            case ANALYZED: {
                ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                ft.setTokenized(true);
                break;
            }
            case ANALYZED_NO_NORMS: {
                ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                ft.setTokenized(true);
                ft.setOmitNorms(true);
                break;
            }
            case NOT_ANALYZED: {
                ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                ft.setTokenized(false);
                break;
            }
            case NOT_ANALYZED_NO_NORMS: {
                ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
                ft.setTokenized(false);
                ft.setOmitNorms(true);
                break;
            }
        }
        switch (termVector) {
            case YES: {
                ft.setStoreTermVectors(true);
                break;
            }
            case WITH_POSITIONS: {
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorPositions(true);
                break;
            }
            case WITH_OFFSETS: {
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorOffsets(true);
                break;
            }
            case WITH_POSITIONS_OFFSETS: {
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorPositions(true);
                ft.setStoreTermVectorOffsets(true);
                break;
            }
        }
        ft.freeze();
        return ft;
    }
    
    @Deprecated
    public Field(final String name, final String value, final Store store, final Index index) {
        this(name, value, translateFieldType(store, index, TermVector.NO));
    }
    
    @Deprecated
    public Field(final String name, final String value, final Store store, final Index index, final TermVector termVector) {
        this(name, value, translateFieldType(store, index, termVector));
    }
    
    @Deprecated
    public Field(final String name, final Reader reader) {
        this(name, reader, TermVector.NO);
    }
    
    @Deprecated
    public Field(final String name, final Reader reader, final TermVector termVector) {
        this(name, reader, translateFieldType(Store.NO, Index.ANALYZED, termVector));
    }
    
    @Deprecated
    public Field(final String name, final TokenStream tokenStream) {
        this(name, tokenStream, TermVector.NO);
    }
    
    @Deprecated
    public Field(final String name, final TokenStream tokenStream, final TermVector termVector) {
        this(name, tokenStream, translateFieldType(Store.NO, Index.ANALYZED, termVector));
    }
    
    @Deprecated
    public Field(final String name, final byte[] value) {
        this(name, value, translateFieldType(Store.YES, Index.NO, TermVector.NO));
    }
    
    @Deprecated
    public Field(final String name, final byte[] value, final int offset, final int length) {
        this(name, value, offset, length, translateFieldType(Store.YES, Index.NO, TermVector.NO));
    }
    
    private static final class BinaryTokenStream extends TokenStream
    {
        private final BytesTermAttribute bytesAtt;
        private boolean used;
        private BytesRef value;
        
        BinaryTokenStream() {
            this.bytesAtt = this.addAttribute(BytesTermAttribute.class);
            this.used = true;
        }
        
        public void setValue(final BytesRef value) {
            this.value = value;
        }
        
        @Override
        public boolean incrementToken() {
            if (this.used) {
                return false;
            }
            this.clearAttributes();
            this.bytesAtt.setBytesRef(this.value);
            return this.used = true;
        }
        
        @Override
        public void reset() {
            this.used = false;
        }
        
        @Override
        public void close() {
            this.value = null;
        }
    }
    
    private static final class StringTokenStream extends TokenStream
    {
        private final CharTermAttribute termAttribute;
        private final OffsetAttribute offsetAttribute;
        private boolean used;
        private String value;
        
        StringTokenStream() {
            this.termAttribute = this.addAttribute(CharTermAttribute.class);
            this.offsetAttribute = this.addAttribute(OffsetAttribute.class);
            this.used = true;
            this.value = null;
        }
        
        void setValue(final String value) {
            this.value = value;
        }
        
        @Override
        public boolean incrementToken() {
            if (this.used) {
                return false;
            }
            this.clearAttributes();
            this.termAttribute.append(this.value);
            this.offsetAttribute.setOffset(0, this.value.length());
            return this.used = true;
        }
        
        @Override
        public void end() throws IOException {
            super.end();
            final int finalOffset = this.value.length();
            this.offsetAttribute.setOffset(finalOffset, finalOffset);
        }
        
        @Override
        public void reset() {
            this.used = false;
        }
        
        @Override
        public void close() {
            this.value = null;
        }
    }
    
    public enum Store
    {
        YES, 
        NO;
    }
    
    @Deprecated
    public enum Index
    {
        NO {
            @Override
            public boolean isIndexed() {
                return false;
            }
            
            @Override
            public boolean isAnalyzed() {
                return false;
            }
            
            @Override
            public boolean omitNorms() {
                return true;
            }
        }, 
        ANALYZED {
            @Override
            public boolean isIndexed() {
                return true;
            }
            
            @Override
            public boolean isAnalyzed() {
                return true;
            }
            
            @Override
            public boolean omitNorms() {
                return false;
            }
        }, 
        NOT_ANALYZED {
            @Override
            public boolean isIndexed() {
                return true;
            }
            
            @Override
            public boolean isAnalyzed() {
                return false;
            }
            
            @Override
            public boolean omitNorms() {
                return false;
            }
        }, 
        NOT_ANALYZED_NO_NORMS {
            @Override
            public boolean isIndexed() {
                return true;
            }
            
            @Override
            public boolean isAnalyzed() {
                return false;
            }
            
            @Override
            public boolean omitNorms() {
                return true;
            }
        }, 
        ANALYZED_NO_NORMS {
            @Override
            public boolean isIndexed() {
                return true;
            }
            
            @Override
            public boolean isAnalyzed() {
                return true;
            }
            
            @Override
            public boolean omitNorms() {
                return true;
            }
        };
        
        public static Index toIndex(final boolean indexed, final boolean analyzed) {
            return toIndex(indexed, analyzed, false);
        }
        
        public static Index toIndex(final boolean indexed, final boolean analyzed, final boolean omitNorms) {
            if (!indexed) {
                return Index.NO;
            }
            if (!omitNorms) {
                if (analyzed) {
                    return Index.ANALYZED;
                }
                return Index.NOT_ANALYZED;
            }
            else {
                if (analyzed) {
                    return Index.ANALYZED_NO_NORMS;
                }
                return Index.NOT_ANALYZED_NO_NORMS;
            }
        }
        
        public abstract boolean isIndexed();
        
        public abstract boolean isAnalyzed();
        
        public abstract boolean omitNorms();
    }
    
    @Deprecated
    public enum TermVector
    {
        NO {
            @Override
            public boolean isStored() {
                return false;
            }
            
            @Override
            public boolean withPositions() {
                return false;
            }
            
            @Override
            public boolean withOffsets() {
                return false;
            }
        }, 
        YES {
            @Override
            public boolean isStored() {
                return true;
            }
            
            @Override
            public boolean withPositions() {
                return false;
            }
            
            @Override
            public boolean withOffsets() {
                return false;
            }
        }, 
        WITH_POSITIONS {
            @Override
            public boolean isStored() {
                return true;
            }
            
            @Override
            public boolean withPositions() {
                return true;
            }
            
            @Override
            public boolean withOffsets() {
                return false;
            }
        }, 
        WITH_OFFSETS {
            @Override
            public boolean isStored() {
                return true;
            }
            
            @Override
            public boolean withPositions() {
                return false;
            }
            
            @Override
            public boolean withOffsets() {
                return true;
            }
        }, 
        WITH_POSITIONS_OFFSETS {
            @Override
            public boolean isStored() {
                return true;
            }
            
            @Override
            public boolean withPositions() {
                return true;
            }
            
            @Override
            public boolean withOffsets() {
                return true;
            }
        };
        
        public static TermVector toTermVector(final boolean stored, final boolean withOffsets, final boolean withPositions) {
            if (!stored) {
                return TermVector.NO;
            }
            if (withOffsets) {
                if (withPositions) {
                    return TermVector.WITH_POSITIONS_OFFSETS;
                }
                return TermVector.WITH_OFFSETS;
            }
            else {
                if (withPositions) {
                    return TermVector.WITH_POSITIONS;
                }
                return TermVector.YES;
            }
        }
        
        public abstract boolean isStored();
        
        public abstract boolean withPositions();
        
        public abstract boolean withOffsets();
    }
}
