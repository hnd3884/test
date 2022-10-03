package org.apache.lucene.uninverting;

import org.apache.lucene.index.FilterDirectoryReader;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.NumericDocValues;
import java.util.Iterator;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.FieldInfo;
import java.util.ArrayList;
import org.apache.lucene.index.LeafReader;
import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import java.util.Map;
import org.apache.lucene.index.FilterLeafReader;

public class UninvertingReader extends FilterLeafReader
{
    final Map<String, Type> mapping;
    final FieldInfos fieldInfos;
    
    public static DirectoryReader wrap(final DirectoryReader in, final Map<String, Type> mapping) throws IOException {
        return (DirectoryReader)new UninvertingDirectoryReader(in, mapping);
    }
    
    public UninvertingReader(final LeafReader in, final Map<String, Type> mapping) {
        super(in);
        this.mapping = mapping;
        final ArrayList<FieldInfo> filteredInfos = new ArrayList<FieldInfo>();
        for (final FieldInfo fi : in.getFieldInfos()) {
            DocValuesType type = fi.getDocValuesType();
            if (fi.getIndexOptions() != IndexOptions.NONE && fi.getDocValuesType() == DocValuesType.NONE) {
                final Type t = mapping.get(fi.name);
                if (t != null) {
                    switch (t) {
                        case INTEGER:
                        case LONG:
                        case FLOAT:
                        case DOUBLE: {
                            type = DocValuesType.NUMERIC;
                            break;
                        }
                        case BINARY: {
                            type = DocValuesType.BINARY;
                            break;
                        }
                        case SORTED: {
                            type = DocValuesType.SORTED;
                            break;
                        }
                        case SORTED_SET_BINARY:
                        case SORTED_SET_INTEGER:
                        case SORTED_SET_FLOAT:
                        case SORTED_SET_LONG:
                        case SORTED_SET_DOUBLE: {
                            type = DocValuesType.SORTED_SET;
                            break;
                        }
                        default: {
                            throw new AssertionError();
                        }
                    }
                }
            }
            filteredInfos.add(new FieldInfo(fi.name, fi.number, fi.hasVectors(), fi.omitsNorms(), fi.hasPayloads(), fi.getIndexOptions(), type, fi.getDocValuesGen(), fi.attributes()));
        }
        this.fieldInfos = new FieldInfos((FieldInfo[])filteredInfos.toArray(new FieldInfo[filteredInfos.size()]));
    }
    
    public FieldInfos getFieldInfos() {
        return this.fieldInfos;
    }
    
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        final Type v = this.getType(field);
        if (v != null) {
            switch (v) {
                case INTEGER: {
                    return FieldCache.DEFAULT.getNumerics(this.in, field, FieldCache.NUMERIC_UTILS_INT_PARSER, true);
                }
                case FLOAT: {
                    return FieldCache.DEFAULT.getNumerics(this.in, field, FieldCache.NUMERIC_UTILS_FLOAT_PARSER, true);
                }
                case LONG: {
                    return FieldCache.DEFAULT.getNumerics(this.in, field, FieldCache.NUMERIC_UTILS_LONG_PARSER, true);
                }
                case DOUBLE: {
                    return FieldCache.DEFAULT.getNumerics(this.in, field, FieldCache.NUMERIC_UTILS_DOUBLE_PARSER, true);
                }
            }
        }
        return super.getNumericDocValues(field);
    }
    
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        final Type v = this.getType(field);
        if (v == Type.BINARY) {
            return FieldCache.DEFAULT.getTerms(this.in, field, true);
        }
        return this.in.getBinaryDocValues(field);
    }
    
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        final Type v = this.getType(field);
        if (v == Type.SORTED) {
            return FieldCache.DEFAULT.getTermsIndex(this.in, field);
        }
        return this.in.getSortedDocValues(field);
    }
    
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        final Type v = this.getType(field);
        if (v != null) {
            switch (v) {
                case SORTED_SET_INTEGER:
                case SORTED_SET_FLOAT: {
                    return FieldCache.DEFAULT.getDocTermOrds(this.in, field, FieldCache.INT32_TERM_PREFIX);
                }
                case SORTED_SET_LONG:
                case SORTED_SET_DOUBLE: {
                    return FieldCache.DEFAULT.getDocTermOrds(this.in, field, FieldCache.INT64_TERM_PREFIX);
                }
                case SORTED_SET_BINARY: {
                    return FieldCache.DEFAULT.getDocTermOrds(this.in, field, null);
                }
            }
        }
        return this.in.getSortedSetDocValues(field);
    }
    
    public Bits getDocsWithField(final String field) throws IOException {
        if (this.getType(field) != null) {
            return FieldCache.DEFAULT.getDocsWithField(this.in, field);
        }
        return this.in.getDocsWithField(field);
    }
    
    private Type getType(final String field) {
        final FieldInfo info = this.fieldInfos.fieldInfo(field);
        if (info == null || info.getDocValuesType() == DocValuesType.NONE) {
            return null;
        }
        return this.mapping.get(field);
    }
    
    public Object getCoreCacheKey() {
        return this.in.getCoreCacheKey();
    }
    
    public Object getCombinedCoreAndDeletesKey() {
        return this.in.getCombinedCoreAndDeletesKey();
    }
    
    public String toString() {
        return "Uninverting(" + this.in.toString() + ")";
    }
    
    public static String[] getUninvertedStats() {
        final FieldCache.CacheEntry[] entries = FieldCache.DEFAULT.getCacheEntries();
        final String[] info = new String[entries.length];
        for (int i = 0; i < entries.length; ++i) {
            info[i] = entries[i].toString();
        }
        return info;
    }
    
    public enum Type
    {
        INTEGER, 
        LONG, 
        FLOAT, 
        DOUBLE, 
        BINARY, 
        SORTED, 
        SORTED_SET_BINARY, 
        SORTED_SET_INTEGER, 
        SORTED_SET_FLOAT, 
        SORTED_SET_LONG, 
        SORTED_SET_DOUBLE;
    }
    
    static class UninvertingDirectoryReader extends FilterDirectoryReader
    {
        final Map<String, Type> mapping;
        
        public UninvertingDirectoryReader(final DirectoryReader in, final Map<String, Type> mapping) throws IOException {
            super(in, (FilterDirectoryReader.SubReaderWrapper)new FilterDirectoryReader.SubReaderWrapper() {
                public LeafReader wrap(final LeafReader reader) {
                    return (LeafReader)new UninvertingReader(reader, mapping);
                }
            });
            this.mapping = mapping;
        }
        
        protected DirectoryReader doWrapDirectoryReader(final DirectoryReader in) throws IOException {
            return (DirectoryReader)new UninvertingDirectoryReader(in, this.mapping);
        }
    }
}
