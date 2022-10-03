package org.apache.lucene.uninverting;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.NumericUtils;
import java.io.PrintStream;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.util.BytesRef;

interface FieldCache
{
    public static final FieldCache DEFAULT = new FieldCacheImpl();
    public static final Parser NUMERIC_UTILS_INT_PARSER = new Parser() {
        @Override
        public long parseValue(final BytesRef term) {
            return NumericUtils.prefixCodedToInt(term);
        }
        
        @Override
        public TermsEnum termsEnum(final Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedInts(terms.iterator());
        }
        
        @Override
        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_INT_PARSER";
        }
    };
    public static final Parser NUMERIC_UTILS_FLOAT_PARSER = new Parser() {
        @Override
        public long parseValue(final BytesRef term) {
            int val = NumericUtils.prefixCodedToInt(term);
            if (val < 0) {
                val ^= Integer.MAX_VALUE;
            }
            return val;
        }
        
        @Override
        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_FLOAT_PARSER";
        }
        
        @Override
        public TermsEnum termsEnum(final Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedInts(terms.iterator());
        }
    };
    public static final Parser NUMERIC_UTILS_LONG_PARSER = new Parser() {
        @Override
        public long parseValue(final BytesRef term) {
            return NumericUtils.prefixCodedToLong(term);
        }
        
        @Override
        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_LONG_PARSER";
        }
        
        @Override
        public TermsEnum termsEnum(final Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedLongs(terms.iterator());
        }
    };
    public static final Parser NUMERIC_UTILS_DOUBLE_PARSER = new Parser() {
        @Override
        public long parseValue(final BytesRef term) {
            long val = NumericUtils.prefixCodedToLong(term);
            if (val < 0L) {
                val ^= Long.MAX_VALUE;
            }
            return val;
        }
        
        @Override
        public String toString() {
            return FieldCache.class.getName() + ".NUMERIC_UTILS_DOUBLE_PARSER";
        }
        
        @Override
        public TermsEnum termsEnum(final Terms terms) throws IOException {
            return NumericUtils.filterPrefixCodedLongs(terms.iterator());
        }
    };
    public static final BytesRef INT32_TERM_PREFIX = new BytesRef(new byte[] { 96 });
    public static final BytesRef INT64_TERM_PREFIX = new BytesRef(new byte[] { 32 });
    
    Bits getDocsWithField(final LeafReader p0, final String p1) throws IOException;
    
    NumericDocValues getNumerics(final LeafReader p0, final String p1, final Parser p2, final boolean p3) throws IOException;
    
    BinaryDocValues getTerms(final LeafReader p0, final String p1, final boolean p2) throws IOException;
    
    BinaryDocValues getTerms(final LeafReader p0, final String p1, final boolean p2, final float p3) throws IOException;
    
    SortedDocValues getTermsIndex(final LeafReader p0, final String p1) throws IOException;
    
    SortedDocValues getTermsIndex(final LeafReader p0, final String p1, final float p2) throws IOException;
    
    SortedSetDocValues getDocTermOrds(final LeafReader p0, final String p1, final BytesRef p2) throws IOException;
    
    CacheEntry[] getCacheEntries();
    
    void purgeAllCaches();
    
    void purgeByCacheKey(final Object p0);
    
    void setInfoStream(final PrintStream p0);
    
    PrintStream getInfoStream();
    
    public static final class CreationPlaceholder implements Accountable
    {
        Accountable value;
        
        public long ramBytesUsed() {
            return RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        }
        
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
    
    public static final class CacheEntry
    {
        private final Object readerKey;
        private final String fieldName;
        private final Class<?> cacheType;
        private final Object custom;
        private final Accountable value;
        
        public CacheEntry(final Object readerKey, final String fieldName, final Class<?> cacheType, final Object custom, final Accountable value) {
            this.readerKey = readerKey;
            this.fieldName = fieldName;
            this.cacheType = cacheType;
            this.custom = custom;
            this.value = value;
        }
        
        public Object getReaderKey() {
            return this.readerKey;
        }
        
        public String getFieldName() {
            return this.fieldName;
        }
        
        public Class<?> getCacheType() {
            return this.cacheType;
        }
        
        public Object getCustom() {
            return this.custom;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public String getEstimatedSize() {
            final long bytesUsed = (this.value == null) ? 0L : this.value.ramBytesUsed();
            return RamUsageEstimator.humanReadableUnits(bytesUsed);
        }
        
        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append("'").append(this.getReaderKey()).append("'=>");
            b.append("'").append(this.getFieldName()).append("',");
            b.append(this.getCacheType()).append(",").append(this.getCustom());
            b.append("=>").append(this.getValue().getClass().getName()).append("#");
            b.append(System.identityHashCode(this.getValue()));
            final String s = this.getEstimatedSize();
            b.append(" (size =~ ").append(s).append(')');
            return b.toString();
        }
    }
    
    public interface Parser
    {
        TermsEnum termsEnum(final Terms p0) throws IOException;
        
        long parseValue(final BytesRef p0);
    }
}
