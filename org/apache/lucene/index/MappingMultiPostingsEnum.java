package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import java.io.IOException;

final class MappingMultiPostingsEnum extends PostingsEnum
{
    private MultiPostingsEnum.EnumWithSlice[] subs;
    int numSubs;
    int upto;
    MergeState.DocMap currentMap;
    PostingsEnum current;
    int currentBase;
    int doc;
    private MergeState mergeState;
    MultiPostingsEnum multiDocsAndPositionsEnum;
    final String field;
    
    public MappingMultiPostingsEnum(final String field, final MergeState mergeState) {
        this.doc = -1;
        this.field = field;
        this.mergeState = mergeState;
    }
    
    MappingMultiPostingsEnum reset(final MultiPostingsEnum postingsEnum) {
        this.numSubs = postingsEnum.getNumSubs();
        this.subs = postingsEnum.getSubs();
        this.upto = -1;
        this.doc = -1;
        this.current = null;
        this.multiDocsAndPositionsEnum = postingsEnum;
        return this;
    }
    
    public int getNumSubs() {
        return this.numSubs;
    }
    
    public MultiPostingsEnum.EnumWithSlice[] getSubs() {
        return this.subs;
    }
    
    @Override
    public int freq() throws IOException {
        return this.current.freq();
    }
    
    @Override
    public int docID() {
        return this.doc;
    }
    
    @Override
    public int advance(final int target) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int nextDoc() throws IOException {
        while (true) {
            if (this.current == null) {
                if (this.upto == this.numSubs - 1) {
                    return this.doc = Integer.MAX_VALUE;
                }
                ++this.upto;
                final int reader = this.subs[this.upto].slice.readerIndex;
                this.current = this.subs[this.upto].postingsEnum;
                this.currentBase = this.mergeState.docBase[reader];
                this.currentMap = this.mergeState.docMaps[reader];
            }
            int doc = this.current.nextDoc();
            if (doc != Integer.MAX_VALUE) {
                doc = this.currentMap.get(doc);
                if (doc == -1) {
                    continue;
                }
                return this.doc = this.currentBase + doc;
            }
            else {
                this.current = null;
            }
        }
    }
    
    @Override
    public int nextPosition() throws IOException {
        final int pos = this.current.nextPosition();
        if (pos < 0) {
            throw new CorruptIndexException("position=" + pos + " is negative, field=\"" + this.field + " doc=" + this.doc, this.mergeState.fieldsProducers[this.upto].toString());
        }
        if (pos > 2147483519) {
            throw new CorruptIndexException("position=" + pos + " is too large (> IndexWriter.MAX_POSITION=" + 2147483519 + "), field=\"" + this.field + "\" doc=" + this.doc, this.mergeState.fieldsProducers[this.upto].toString());
        }
        return pos;
    }
    
    @Override
    public int startOffset() throws IOException {
        return this.current.startOffset();
    }
    
    @Override
    public int endOffset() throws IOException {
        return this.current.endOffset();
    }
    
    @Override
    public BytesRef getPayload() throws IOException {
        return this.current.getPayload();
    }
    
    @Override
    public long cost() {
        long cost = 0L;
        for (final MultiPostingsEnum.EnumWithSlice enumWithSlice : this.subs) {
            cost += enumWithSlice.postingsEnum.cost();
        }
        return cost;
    }
}
