package org.apache.lucene.search.vectorhighlight;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Fields;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.index.IndexReader;
import java.util.LinkedList;

public class FieldTermStack
{
    private final String fieldName;
    LinkedList<TermInfo> termList;
    
    public FieldTermStack(final IndexReader reader, final int docId, final String fieldName, final FieldQuery fieldQuery) throws IOException {
        this.termList = new LinkedList<TermInfo>();
        this.fieldName = fieldName;
        final Set<String> termSet = fieldQuery.getTermSet(fieldName);
        if (termSet == null) {
            return;
        }
        final Fields vectors = reader.getTermVectors(docId);
        if (vectors == null) {
            return;
        }
        final Terms vector = vectors.terms(fieldName);
        if (vector == null || !vector.hasPositions()) {
            return;
        }
        final CharsRefBuilder spare = new CharsRefBuilder();
        final TermsEnum termsEnum = vector.iterator();
        PostingsEnum dpEnum = null;
        final int numDocs = reader.maxDoc();
        BytesRef text;
        while ((text = termsEnum.next()) != null) {
            spare.copyUTF8Bytes(text);
            final String term = spare.toString();
            if (!termSet.contains(term)) {
                continue;
            }
            dpEnum = termsEnum.postings(dpEnum, 24);
            dpEnum.nextDoc();
            final float weight = (float)(Math.log(numDocs / (double)(reader.docFreq(new Term(fieldName, text)) + 1)) + 1.0);
            for (int freq = dpEnum.freq(), i = 0; i < freq; ++i) {
                final int pos = dpEnum.nextPosition();
                if (dpEnum.startOffset() < 0) {
                    return;
                }
                this.termList.add(new TermInfo(term, dpEnum.startOffset(), dpEnum.endOffset(), pos, weight));
            }
        }
        Collections.sort(this.termList);
        int currentPos = -1;
        TermInfo previous = null;
        TermInfo first = null;
        final Iterator<TermInfo> iterator = this.termList.iterator();
        while (iterator.hasNext()) {
            final TermInfo current = iterator.next();
            if (current.position == currentPos) {
                assert previous != null;
                previous.setNext(current);
                previous = current;
                iterator.remove();
            }
            else {
                if (previous != null) {
                    previous.setNext(first);
                }
                first = (previous = current);
                currentPos = current.position;
            }
        }
        if (previous != null) {
            previous.setNext(first);
        }
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public TermInfo pop() {
        return this.termList.poll();
    }
    
    public void push(final TermInfo termInfo) {
        this.termList.push(termInfo);
    }
    
    public boolean isEmpty() {
        return this.termList == null || this.termList.size() == 0;
    }
    
    public static class TermInfo implements Comparable<TermInfo>
    {
        private final String text;
        private final int startOffset;
        private final int endOffset;
        private final int position;
        private final float weight;
        private TermInfo next;
        
        public TermInfo(final String text, final int startOffset, final int endOffset, final int position, final float weight) {
            this.text = text;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.position = position;
            this.weight = weight;
            this.next = this;
        }
        
        void setNext(final TermInfo next) {
            this.next = next;
        }
        
        public TermInfo getNext() {
            return this.next;
        }
        
        public String getText() {
            return this.text;
        }
        
        public int getStartOffset() {
            return this.startOffset;
        }
        
        public int getEndOffset() {
            return this.endOffset;
        }
        
        public int getPosition() {
            return this.position;
        }
        
        public float getWeight() {
            return this.weight;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.text).append('(').append(this.startOffset).append(',').append(this.endOffset).append(',').append(this.position).append(')');
            return sb.toString();
        }
        
        @Override
        public int compareTo(final TermInfo o) {
            return this.position - o.position;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + this.position;
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final TermInfo other = (TermInfo)obj;
            return this.position == other.position;
        }
    }
}
