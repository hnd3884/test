package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import java.io.IOException;

public class ExitableDirectoryReader extends FilterDirectoryReader
{
    private QueryTimeout queryTimeout;
    
    public ExitableDirectoryReader(final DirectoryReader in, final QueryTimeout queryTimeout) throws IOException {
        super(in, new ExitableSubReaderWrapper(queryTimeout));
        this.queryTimeout = queryTimeout;
    }
    
    @Override
    protected DirectoryReader doWrapDirectoryReader(final DirectoryReader in) throws IOException {
        return new ExitableDirectoryReader(in, this.queryTimeout);
    }
    
    public static DirectoryReader wrap(final DirectoryReader in, final QueryTimeout queryTimeout) throws IOException {
        return new ExitableDirectoryReader(in, queryTimeout);
    }
    
    @Override
    public String toString() {
        return "ExitableDirectoryReader(" + this.in.toString() + ")";
    }
    
    public static class ExitingReaderException extends RuntimeException
    {
        ExitingReaderException(final String msg) {
            super(msg);
        }
    }
    
    public static class ExitableSubReaderWrapper extends SubReaderWrapper
    {
        private QueryTimeout queryTimeout;
        
        public ExitableSubReaderWrapper(final QueryTimeout queryTimeout) {
            this.queryTimeout = queryTimeout;
        }
        
        @Override
        public LeafReader wrap(final LeafReader reader) {
            return new ExitableFilterAtomicReader(reader, this.queryTimeout);
        }
    }
    
    public static class ExitableFilterAtomicReader extends FilterLeafReader
    {
        private QueryTimeout queryTimeout;
        
        public ExitableFilterAtomicReader(final LeafReader in, final QueryTimeout queryTimeout) {
            super(in);
            this.queryTimeout = queryTimeout;
        }
        
        @Override
        public Fields fields() throws IOException {
            return new ExitableFields(super.fields(), this.queryTimeout);
        }
        
        @Override
        public Object getCoreCacheKey() {
            return this.in.getCoreCacheKey();
        }
        
        @Override
        public Object getCombinedCoreAndDeletesKey() {
            return this.in.getCombinedCoreAndDeletesKey();
        }
    }
    
    public static class ExitableFields extends FilterLeafReader.FilterFields
    {
        private QueryTimeout queryTimeout;
        
        public ExitableFields(final Fields fields, final QueryTimeout queryTimeout) {
            super(fields);
            this.queryTimeout = queryTimeout;
        }
        
        @Override
        public Terms terms(final String field) throws IOException {
            final Terms terms = this.in.terms(field);
            if (terms == null) {
                return null;
            }
            return new ExitableTerms(terms, this.queryTimeout);
        }
    }
    
    public static class ExitableTerms extends FilterLeafReader.FilterTerms
    {
        private QueryTimeout queryTimeout;
        
        public ExitableTerms(final Terms terms, final QueryTimeout queryTimeout) {
            super(terms);
            this.queryTimeout = queryTimeout;
        }
        
        @Override
        public TermsEnum intersect(final CompiledAutomaton compiled, final BytesRef startTerm) throws IOException {
            return new ExitableTermsEnum(this.in.intersect(compiled, startTerm), this.queryTimeout);
        }
        
        @Override
        public TermsEnum iterator() throws IOException {
            return new ExitableTermsEnum(this.in.iterator(), this.queryTimeout);
        }
    }
    
    public static class ExitableTermsEnum extends FilterLeafReader.FilterTermsEnum
    {
        private QueryTimeout queryTimeout;
        
        public ExitableTermsEnum(final TermsEnum termsEnum, final QueryTimeout queryTimeout) {
            super(termsEnum);
            this.queryTimeout = queryTimeout;
            this.checkAndThrow();
        }
        
        private void checkAndThrow() {
            if (this.queryTimeout.shouldExit()) {
                throw new ExitingReaderException("The request took too long to iterate over terms. Timeout: " + this.queryTimeout.toString() + ", TermsEnum=" + this.in);
            }
            if (Thread.interrupted()) {
                throw new ExitingReaderException("Interrupted while iterating over terms. TermsEnum=" + this.in);
            }
        }
        
        @Override
        public BytesRef next() throws IOException {
            this.checkAndThrow();
            return this.in.next();
        }
    }
}
