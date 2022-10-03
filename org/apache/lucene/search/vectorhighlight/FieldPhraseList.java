package org.apache.lucene.search.vectorhighlight;

import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.util.MergedIterator;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class FieldPhraseList
{
    LinkedList<WeightedPhraseInfo> phraseList;
    
    public FieldPhraseList(final FieldTermStack fieldTermStack, final FieldQuery fieldQuery) {
        this(fieldTermStack, fieldQuery, Integer.MAX_VALUE);
    }
    
    public List<WeightedPhraseInfo> getPhraseList() {
        return this.phraseList;
    }
    
    public FieldPhraseList(final FieldTermStack fieldTermStack, final FieldQuery fieldQuery, final int phraseLimit) {
        this.phraseList = new LinkedList<WeightedPhraseInfo>();
        final String field = fieldTermStack.getFieldName();
        final LinkedList<FieldTermStack.TermInfo> phraseCandidate = new LinkedList<FieldTermStack.TermInfo>();
        FieldQuery.QueryPhraseMap currMap = null;
        FieldQuery.QueryPhraseMap nextMap = null;
        while (!fieldTermStack.isEmpty() && this.phraseList.size() < phraseLimit) {
            phraseCandidate.clear();
            FieldTermStack.TermInfo ti;
            FieldTermStack.TermInfo first;
            for (ti = null, first = null, ti = (first = fieldTermStack.pop()), currMap = fieldQuery.getFieldTermMap(field, ti.getText()); currMap == null && ti.getNext() != first; ti = ti.getNext(), currMap = fieldQuery.getFieldTermMap(field, ti.getText())) {}
            if (currMap == null) {
                continue;
            }
            phraseCandidate.add(ti);
            while (true) {
                ti = (first = fieldTermStack.pop());
                nextMap = null;
                if (ti != null) {
                    for (nextMap = currMap.getTermMap(ti.getText()); nextMap == null && ti.getNext() != first; ti = ti.getNext(), nextMap = currMap.getTermMap(ti.getText())) {}
                }
                if (ti == null || nextMap == null) {
                    break;
                }
                phraseCandidate.add(ti);
                currMap = nextMap;
            }
            if (ti != null) {
                fieldTermStack.push(ti);
            }
            if (currMap.isValidTermOrPhrase(phraseCandidate)) {
                this.addIfNoOverlap(new WeightedPhraseInfo(phraseCandidate, currMap.getBoost(), currMap.getTermOrPhraseNumber()));
            }
            else {
                while (phraseCandidate.size() > 1) {
                    fieldTermStack.push(phraseCandidate.removeLast());
                    currMap = fieldQuery.searchPhrase(field, phraseCandidate);
                    if (currMap != null) {
                        this.addIfNoOverlap(new WeightedPhraseInfo(phraseCandidate, currMap.getBoost(), currMap.getTermOrPhraseNumber()));
                        break;
                    }
                }
            }
        }
    }
    
    public FieldPhraseList(final FieldPhraseList[] toMerge) {
        this.phraseList = new LinkedList<WeightedPhraseInfo>();
        final Iterator<WeightedPhraseInfo>[] allInfos = new Iterator[toMerge.length];
        int index = 0;
        for (final FieldPhraseList fplToMerge : toMerge) {
            allInfos[index++] = fplToMerge.phraseList.iterator();
        }
        final MergedIterator<WeightedPhraseInfo> itr = (MergedIterator<WeightedPhraseInfo>)new MergedIterator(false, (Iterator[])allInfos);
        this.phraseList = new LinkedList<WeightedPhraseInfo>();
        if (!itr.hasNext()) {
            return;
        }
        final List<WeightedPhraseInfo> work = new ArrayList<WeightedPhraseInfo>();
        final WeightedPhraseInfo first = (WeightedPhraseInfo)itr.next();
        work.add(first);
        int workEndOffset = first.getEndOffset();
        while (itr.hasNext()) {
            final WeightedPhraseInfo current = (WeightedPhraseInfo)itr.next();
            if (current.getStartOffset() <= workEndOffset) {
                workEndOffset = Math.max(workEndOffset, current.getEndOffset());
                work.add(current);
            }
            else {
                if (work.size() == 1) {
                    this.phraseList.add(work.get(0));
                    work.set(0, current);
                }
                else {
                    this.phraseList.add(new WeightedPhraseInfo(work));
                    work.clear();
                    work.add(current);
                }
                workEndOffset = current.getEndOffset();
            }
        }
        if (work.size() == 1) {
            this.phraseList.add(work.get(0));
        }
        else {
            this.phraseList.add(new WeightedPhraseInfo(work));
            work.clear();
        }
    }
    
    public void addIfNoOverlap(final WeightedPhraseInfo wpi) {
        for (final WeightedPhraseInfo existWpi : this.getPhraseList()) {
            if (existWpi.isOffsetOverlap(wpi)) {
                existWpi.getTermsInfos().addAll(wpi.getTermsInfos());
                return;
            }
        }
        this.getPhraseList().add(wpi);
    }
    
    public static class WeightedPhraseInfo implements Comparable<WeightedPhraseInfo>
    {
        private List<Toffs> termsOffsets;
        private float boost;
        private int seqnum;
        private ArrayList<FieldTermStack.TermInfo> termsInfos;
        
        public String getText() {
            final StringBuilder text = new StringBuilder();
            for (final FieldTermStack.TermInfo ti : this.termsInfos) {
                text.append(ti.getText());
            }
            return text.toString();
        }
        
        public List<Toffs> getTermsOffsets() {
            return this.termsOffsets;
        }
        
        public float getBoost() {
            return this.boost;
        }
        
        public List<FieldTermStack.TermInfo> getTermsInfos() {
            return this.termsInfos;
        }
        
        public WeightedPhraseInfo(final LinkedList<FieldTermStack.TermInfo> terms, final float boost) {
            this(terms, boost, 0);
        }
        
        public WeightedPhraseInfo(final LinkedList<FieldTermStack.TermInfo> terms, final float boost, final int seqnum) {
            this.boost = boost;
            this.seqnum = seqnum;
            this.termsInfos = new ArrayList<FieldTermStack.TermInfo>(terms);
            this.termsOffsets = new ArrayList<Toffs>(terms.size());
            FieldTermStack.TermInfo ti = terms.get(0);
            this.termsOffsets.add(new Toffs(ti.getStartOffset(), ti.getEndOffset()));
            if (terms.size() == 1) {
                return;
            }
            int pos = ti.getPosition();
            for (int i = 1; i < terms.size(); ++i) {
                ti = terms.get(i);
                if (ti.getPosition() - pos == 1) {
                    final Toffs to = this.termsOffsets.get(this.termsOffsets.size() - 1);
                    to.setEndOffset(ti.getEndOffset());
                }
                else {
                    this.termsOffsets.add(new Toffs(ti.getStartOffset(), ti.getEndOffset()));
                }
                pos = ti.getPosition();
            }
        }
        
        public WeightedPhraseInfo(final Collection<WeightedPhraseInfo> toMerge) {
            final Iterator<WeightedPhraseInfo> toMergeItr = toMerge.iterator();
            if (!toMergeItr.hasNext()) {
                throw new IllegalArgumentException("toMerge must contain at least one WeightedPhraseInfo.");
            }
            final WeightedPhraseInfo first = toMergeItr.next();
            final Iterator<Toffs>[] allToffs = new Iterator[toMerge.size()];
            this.termsInfos = new ArrayList<FieldTermStack.TermInfo>();
            this.seqnum = first.seqnum;
            this.boost = first.boost;
            allToffs[0] = first.termsOffsets.iterator();
            int index = 1;
            while (toMergeItr.hasNext()) {
                final WeightedPhraseInfo info = toMergeItr.next();
                this.boost += info.boost;
                this.termsInfos.addAll(info.termsInfos);
                allToffs[index++] = info.termsOffsets.iterator();
            }
            final MergedIterator<Toffs> itr = (MergedIterator<Toffs>)new MergedIterator(false, (Iterator[])allToffs);
            this.termsOffsets = new ArrayList<Toffs>();
            if (!itr.hasNext()) {
                return;
            }
            Toffs work = (Toffs)itr.next();
            while (itr.hasNext()) {
                final Toffs current = (Toffs)itr.next();
                if (current.startOffset <= work.endOffset) {
                    work.endOffset = Math.max(work.endOffset, current.endOffset);
                }
                else {
                    this.termsOffsets.add(work);
                    work = current;
                }
            }
            this.termsOffsets.add(work);
        }
        
        public int getStartOffset() {
            return this.termsOffsets.get(0).startOffset;
        }
        
        public int getEndOffset() {
            return this.termsOffsets.get(this.termsOffsets.size() - 1).endOffset;
        }
        
        public boolean isOffsetOverlap(final WeightedPhraseInfo other) {
            final int so = this.getStartOffset();
            final int eo = this.getEndOffset();
            final int oso = other.getStartOffset();
            final int oeo = other.getEndOffset();
            return (so <= oso && oso < eo) || (so < oeo && oeo <= eo) || (oso <= so && so < oeo) || (oso < eo && eo <= oeo);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getText()).append('(').append(this.boost).append(")(");
            for (final Toffs to : this.termsOffsets) {
                sb.append(to);
            }
            sb.append(')');
            return sb.toString();
        }
        
        public int getSeqnum() {
            return this.seqnum;
        }
        
        @Override
        public int compareTo(final WeightedPhraseInfo other) {
            int diff = this.getStartOffset() - other.getStartOffset();
            if (diff != 0) {
                return diff;
            }
            diff = this.getEndOffset() - other.getEndOffset();
            if (diff != 0) {
                return diff;
            }
            return (int)Math.signum(this.getBoost() - other.getBoost());
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + this.getStartOffset();
            result = 31 * result + this.getEndOffset();
            final long b = Double.doubleToLongBits(this.getBoost());
            result = 31 * result + (int)(b ^ b >>> 32);
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
            final WeightedPhraseInfo other = (WeightedPhraseInfo)obj;
            return this.getStartOffset() == other.getStartOffset() && this.getEndOffset() == other.getEndOffset() && this.getBoost() == other.getBoost();
        }
        
        public static class Toffs implements Comparable<Toffs>
        {
            private int startOffset;
            private int endOffset;
            
            public Toffs(final int startOffset, final int endOffset) {
                this.startOffset = startOffset;
                this.endOffset = endOffset;
            }
            
            public void setEndOffset(final int endOffset) {
                this.endOffset = endOffset;
            }
            
            public int getStartOffset() {
                return this.startOffset;
            }
            
            public int getEndOffset() {
                return this.endOffset;
            }
            
            @Override
            public int compareTo(final Toffs other) {
                final int diff = this.getStartOffset() - other.getStartOffset();
                if (diff != 0) {
                    return diff;
                }
                return this.getEndOffset() - other.getEndOffset();
            }
            
            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = 31 * result + this.getStartOffset();
                result = 31 * result + this.getEndOffset();
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
                final Toffs other = (Toffs)obj;
                return this.getStartOffset() == other.getStartOffset() && this.getEndOffset() == other.getEndOffset();
            }
            
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append('(').append(this.startOffset).append(',').append(this.endOffset).append(')');
                return sb.toString();
            }
        }
    }
}
