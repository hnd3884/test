package org.apache.lucene.index;

import java.io.IOException;

public class MappedMultiFields extends FilterLeafReader.FilterFields
{
    final MergeState mergeState;
    
    public MappedMultiFields(final MergeState mergeState, final MultiFields multiFields) {
        super(multiFields);
        this.mergeState = mergeState;
    }
    
    @Override
    public Terms terms(final String field) throws IOException {
        final MultiTerms terms = (MultiTerms)this.in.terms(field);
        if (terms == null) {
            return null;
        }
        return new MappedMultiTerms(field, this.mergeState, terms);
    }
    
    private static class MappedMultiTerms extends FilterLeafReader.FilterTerms
    {
        final MergeState mergeState;
        final String field;
        
        public MappedMultiTerms(final String field, final MergeState mergeState, final MultiTerms multiTerms) {
            super(multiTerms);
            this.field = field;
            this.mergeState = mergeState;
        }
        
        @Override
        public TermsEnum iterator() throws IOException {
            final TermsEnum iterator = this.in.iterator();
            if (iterator == TermsEnum.EMPTY) {
                return TermsEnum.EMPTY;
            }
            return new MappedMultiTermsEnum(this.field, this.mergeState, (MultiTermsEnum)iterator);
        }
        
        @Override
        public long size() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getSumTotalTermFreq() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getSumDocFreq() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getDocCount() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class MappedMultiTermsEnum extends FilterLeafReader.FilterTermsEnum
    {
        final MergeState mergeState;
        final String field;
        
        public MappedMultiTermsEnum(final String field, final MergeState mergeState, final MultiTermsEnum multiTermsEnum) {
            super(multiTermsEnum);
            this.field = field;
            this.mergeState = mergeState;
        }
        
        @Override
        public int docFreq() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long totalTermFreq() throws IOException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
            MappingMultiPostingsEnum mappingDocsAndPositionsEnum;
            if (reuse instanceof MappingMultiPostingsEnum) {
                final MappingMultiPostingsEnum postings = (MappingMultiPostingsEnum)reuse;
                if (postings.field.equals(this.field)) {
                    mappingDocsAndPositionsEnum = postings;
                }
                else {
                    mappingDocsAndPositionsEnum = new MappingMultiPostingsEnum(this.field, this.mergeState);
                }
            }
            else {
                mappingDocsAndPositionsEnum = new MappingMultiPostingsEnum(this.field, this.mergeState);
            }
            final MultiPostingsEnum docsAndPositionsEnum = (MultiPostingsEnum)this.in.postings(mappingDocsAndPositionsEnum.multiDocsAndPositionsEnum, flags);
            mappingDocsAndPositionsEnum.reset(docsAndPositionsEnum);
            return mappingDocsAndPositionsEnum;
        }
    }
}
