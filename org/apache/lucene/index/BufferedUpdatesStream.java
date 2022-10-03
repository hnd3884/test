package org.apache.lucene.index;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.IOUtils;
import java.util.Iterator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.BytesRef;
import java.util.List;
import org.apache.lucene.util.Accountable;

class BufferedUpdatesStream implements Accountable
{
    private final List<FrozenBufferedUpdates> updates;
    private long nextGen;
    private BytesRef lastDeleteTerm;
    private final InfoStream infoStream;
    private final AtomicLong bytesUsed;
    private final AtomicInteger numTerms;
    private static final Comparator<SegmentCommitInfo> sortSegInfoByDelGen;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public BufferedUpdatesStream(final InfoStream infoStream) {
        this.updates = new ArrayList<FrozenBufferedUpdates>();
        this.nextGen = 1L;
        this.bytesUsed = new AtomicLong();
        this.numTerms = new AtomicInteger();
        this.infoStream = infoStream;
    }
    
    public synchronized long push(final FrozenBufferedUpdates packet) {
        packet.setDelGen(this.nextGen++);
        assert packet.any();
        assert this.checkDeleteStats();
        assert packet.delGen() < this.nextGen;
        assert this.updates.get(this.updates.size() - 1).delGen() < packet.delGen() : "Delete packets must be in order";
        this.updates.add(packet);
        this.numTerms.addAndGet(packet.numTermDeletes);
        this.bytesUsed.addAndGet(packet.bytesUsed);
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "push deletes " + packet + " segmentPrivate?=" + packet.isSegmentPrivate + " delGen=" + packet.delGen() + " packetCount=" + this.updates.size() + " totBytesUsed=" + this.bytesUsed.get());
        }
        assert this.checkDeleteStats();
        return packet.delGen();
    }
    
    public synchronized void clear() {
        this.updates.clear();
        this.nextGen = 1L;
        this.numTerms.set(0);
        this.bytesUsed.set(0L);
    }
    
    public boolean any() {
        return this.bytesUsed.get() != 0L;
    }
    
    public int numTerms() {
        return this.numTerms.get();
    }
    
    @Override
    public long ramBytesUsed() {
        return this.bytesUsed.get();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public synchronized ApplyDeletesResult applyDeletesAndUpdates(final IndexWriter.ReaderPool pool, List<SegmentCommitInfo> infos) throws IOException {
        final long t0 = System.currentTimeMillis();
        final long gen = this.nextGen++;
        if (infos.size() == 0) {
            return new ApplyDeletesResult(false, gen, null);
        }
        SegmentState[] segStates = null;
        long totDelCount = 0L;
        long totTermVisitedCount = 0L;
        boolean success = false;
        ApplyDeletesResult result = null;
        try {
            if (this.infoStream.isEnabled("BD")) {
                this.infoStream.message("BD", String.format(Locale.ROOT, "applyDeletes: open segment readers took %d msec", System.currentTimeMillis() - t0));
            }
            assert this.checkDeleteStats();
            if (!this.any()) {
                if (this.infoStream.isEnabled("BD")) {
                    this.infoStream.message("BD", "applyDeletes: no segments; skipping");
                }
                return new ApplyDeletesResult(false, gen, null);
            }
            if (this.infoStream.isEnabled("BD")) {
                this.infoStream.message("BD", "applyDeletes: infos=" + infos + " packetCount=" + this.updates.size());
            }
            infos = this.sortByDelGen(infos);
            CoalescedUpdates coalescedUpdates = null;
            int infosIDX = infos.size() - 1;
            int delIDX = this.updates.size() - 1;
            while (infosIDX >= 0) {
                final FrozenBufferedUpdates packet = (delIDX >= 0) ? this.updates.get(delIDX) : null;
                final SegmentCommitInfo info = infos.get(infosIDX);
                final long segGen = info.getBufferedDeletesGen();
                if (packet != null && segGen < packet.delGen()) {
                    if (!packet.isSegmentPrivate && packet.any()) {
                        if (coalescedUpdates == null) {
                            coalescedUpdates = new CoalescedUpdates();
                        }
                        coalescedUpdates.update(packet);
                    }
                    --delIDX;
                }
                else if (packet != null && segGen == packet.delGen()) {
                    assert packet.isSegmentPrivate : "Packet and Segments deletegen can only match on a segment private del packet gen=" + segGen;
                    if (segStates == null) {
                        segStates = this.openSegmentStates(pool, infos);
                    }
                    final SegmentState segState = segStates[infosIDX];
                    assert pool.infoIsLive(info);
                    int delCount = 0;
                    final DocValuesFieldUpdates.Container dvUpdates = new DocValuesFieldUpdates.Container();
                    delCount += (int)applyQueryDeletes(packet.queriesIterable(), segState);
                    this.applyDocValuesUpdates(Arrays.asList(packet.numericDVUpdates), segState, dvUpdates);
                    this.applyDocValuesUpdates(Arrays.asList(packet.binaryDVUpdates), segState, dvUpdates);
                    if (coalescedUpdates != null) {
                        delCount += (int)applyQueryDeletes(coalescedUpdates.queriesIterable(), segState);
                        this.applyDocValuesUpdatesList(coalescedUpdates.numericDVUpdates, segState, dvUpdates);
                        this.applyDocValuesUpdatesList(coalescedUpdates.binaryDVUpdates, segState, dvUpdates);
                    }
                    if (dvUpdates.any()) {
                        segState.rld.writeFieldUpdates(info.info.dir, dvUpdates);
                    }
                    totDelCount += delCount;
                    --delIDX;
                    --infosIDX;
                }
                else {
                    if (coalescedUpdates != null) {
                        if (segStates == null) {
                            segStates = this.openSegmentStates(pool, infos);
                        }
                        final SegmentState segState = segStates[infosIDX];
                        assert pool.infoIsLive(info);
                        int delCount = 0;
                        delCount += (int)applyQueryDeletes(coalescedUpdates.queriesIterable(), segState);
                        final DocValuesFieldUpdates.Container dvUpdates = new DocValuesFieldUpdates.Container();
                        this.applyDocValuesUpdatesList(coalescedUpdates.numericDVUpdates, segState, dvUpdates);
                        this.applyDocValuesUpdatesList(coalescedUpdates.binaryDVUpdates, segState, dvUpdates);
                        if (dvUpdates.any()) {
                            segState.rld.writeFieldUpdates(info.info.dir, dvUpdates);
                        }
                        totDelCount += delCount;
                    }
                    --infosIDX;
                }
            }
            if (coalescedUpdates != null && coalescedUpdates.totalTermCount != 0L) {
                if (segStates == null) {
                    segStates = this.openSegmentStates(pool, infos);
                }
                totTermVisitedCount += this.applyTermDeletes(coalescedUpdates, segStates);
            }
            assert this.checkDeleteStats();
            success = true;
        }
        finally {
            if (segStates != null) {
                result = this.closeSegmentStates(pool, segStates, success, gen);
            }
        }
        if (result == null) {
            result = new ApplyDeletesResult(false, gen, null);
        }
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", String.format(Locale.ROOT, "applyDeletes took %d msec for %d segments, %d newly deleted docs (query deletes), %d visited terms, allDeleted=%s", System.currentTimeMillis() - t0, infos.size(), totDelCount, totTermVisitedCount, result.allDeleted));
        }
        return result;
    }
    
    private List<SegmentCommitInfo> sortByDelGen(List<SegmentCommitInfo> infos) {
        infos = new ArrayList<SegmentCommitInfo>(infos);
        Collections.sort(infos, BufferedUpdatesStream.sortSegInfoByDelGen);
        return infos;
    }
    
    synchronized long getNextGen() {
        return this.nextGen++;
    }
    
    public synchronized void prune(final SegmentInfos segmentInfos) {
        assert this.checkDeleteStats();
        long minGen = Long.MAX_VALUE;
        for (final SegmentCommitInfo info : segmentInfos) {
            minGen = Math.min(info.getBufferedDeletesGen(), minGen);
        }
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "prune sis=" + segmentInfos + " minGen=" + minGen + " packetCount=" + this.updates.size());
        }
        final int limit = this.updates.size();
        int delIDX = 0;
        while (delIDX < limit) {
            if (this.updates.get(delIDX).delGen() >= minGen) {
                this.prune(delIDX);
                assert this.checkDeleteStats();
                return;
            }
            else {
                ++delIDX;
            }
        }
        this.prune(limit);
        assert !this.any();
        assert this.checkDeleteStats();
    }
    
    private synchronized void prune(final int count) {
        if (count > 0) {
            if (this.infoStream.isEnabled("BD")) {
                this.infoStream.message("BD", "pruneDeletes: prune " + count + " packets; " + (this.updates.size() - count) + " packets remain");
            }
            for (int delIDX = 0; delIDX < count; ++delIDX) {
                final FrozenBufferedUpdates packet = this.updates.get(delIDX);
                this.numTerms.addAndGet(-packet.numTermDeletes);
                assert this.numTerms.get() >= 0;
                this.bytesUsed.addAndGet(-packet.bytesUsed);
                assert this.bytesUsed.get() >= 0L;
            }
            this.updates.subList(0, count).clear();
        }
    }
    
    private SegmentState[] openSegmentStates(final IndexWriter.ReaderPool pool, final List<SegmentCommitInfo> infos) throws IOException {
        final int numReaders = infos.size();
        final SegmentState[] segStates = new SegmentState[numReaders];
        boolean success = false;
        try {
            for (int i = 0; i < numReaders; ++i) {
                segStates[i] = new SegmentState(pool, infos.get(i));
            }
            success = true;
        }
        finally {
            if (!success) {
                for (int j = 0; j < numReaders; ++j) {
                    if (segStates[j] != null) {
                        try {
                            segStates[j].finish(pool);
                        }
                        catch (final Throwable t) {}
                    }
                }
            }
        }
        return segStates;
    }
    
    private ApplyDeletesResult closeSegmentStates(final IndexWriter.ReaderPool pool, final SegmentState[] segStates, final boolean success, final long gen) throws IOException {
        final int numReaders = segStates.length;
        Throwable firstExc = null;
        List<SegmentCommitInfo> allDeleted = null;
        long totDelCount = 0L;
        for (int j = 0; j < numReaders; ++j) {
            final SegmentState segState = segStates[j];
            if (success) {
                totDelCount += segState.rld.getPendingDeleteCount() - segState.startDelCount;
                segState.reader.getSegmentInfo().setBufferedDeletesGen(gen);
                final int fullDelCount = segState.rld.info.getDelCount() + segState.rld.getPendingDeleteCount();
                assert fullDelCount <= segState.rld.info.info.maxDoc();
                if (fullDelCount == segState.rld.info.info.maxDoc()) {
                    if (allDeleted == null) {
                        allDeleted = new ArrayList<SegmentCommitInfo>();
                    }
                    allDeleted.add(segState.reader.getSegmentInfo());
                }
            }
            try {
                segStates[j].finish(pool);
            }
            catch (final Throwable th) {
                if (firstExc != null) {
                    firstExc = th;
                }
            }
        }
        if (success) {
            IOUtils.reThrow(firstExc);
        }
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", "applyDeletes: " + totDelCount + " new deleted documents");
        }
        return new ApplyDeletesResult(totDelCount > 0L, gen, allDeleted);
    }
    
    private synchronized long applyTermDeletes(final CoalescedUpdates updates, final SegmentState[] segStates) throws IOException {
        final long startNS = System.nanoTime();
        final int numReaders = segStates.length;
        long delTermVisitedCount = 0L;
        long segTermVisitedCount = 0L;
        final FieldTermIterator iter = updates.termIterator();
        String field = null;
        SegmentQueue queue = null;
        BytesRef term;
        while ((term = iter.next()) != null) {
            if (iter.field() != field) {
                field = iter.field();
                queue = new SegmentQueue(numReaders);
                long segTermCount = 0L;
                for (final SegmentState state : segStates) {
                    final Terms terms = state.reader.fields().terms(field);
                    if (terms != null) {
                        segTermCount += terms.size();
                        state.termsEnum = terms.iterator();
                        state.term = state.termsEnum.next();
                        if (state.term != null) {
                            queue.add(state);
                        }
                    }
                }
                assert this.checkDeleteTerm(null);
            }
            assert this.checkDeleteTerm(term);
            ++delTermVisitedCount;
            final long delGen = iter.delGen();
            while (queue.size() != 0) {
                final SegmentState state2 = queue.top();
                ++segTermVisitedCount;
                final int cmp = term.compareTo(state2.term);
                if (cmp < 0) {
                    break;
                }
                if (cmp != 0) {
                    final TermsEnum.SeekStatus status = state2.termsEnum.seekCeil(term);
                    if (status != TermsEnum.SeekStatus.FOUND) {
                        if (status == TermsEnum.SeekStatus.NOT_FOUND) {
                            state2.term = state2.termsEnum.term();
                            queue.updateTop();
                            continue;
                        }
                        queue.pop();
                        continue;
                    }
                }
                assert state2.delGen != delGen;
                if (state2.delGen < delGen) {
                    final Bits acceptDocs = state2.rld.getLiveDocs();
                    state2.postingsEnum = state2.termsEnum.postings(state2.postingsEnum, 0);
                    assert state2.postingsEnum != null;
                    while (true) {
                        final int docID = state2.postingsEnum.nextDoc();
                        if (docID == Integer.MAX_VALUE) {
                            break;
                        }
                        if (acceptDocs != null && !acceptDocs.get(docID)) {
                            continue;
                        }
                        if (!state2.any) {
                            state2.rld.initWritableLiveDocs();
                            state2.any = true;
                        }
                        state2.rld.delete(docID);
                    }
                }
                state2.term = state2.termsEnum.next();
                if (state2.term == null) {
                    queue.pop();
                }
                else {
                    queue.updateTop();
                }
            }
        }
        if (this.infoStream.isEnabled("BD")) {
            this.infoStream.message("BD", String.format(Locale.ROOT, "applyTermDeletes took %.1f msec for %d segments and %d packets; %d del terms visited; %d seg terms visited", (System.nanoTime() - startNS) / 1000000.0, numReaders, updates.terms.size(), delTermVisitedCount, segTermVisitedCount));
        }
        return delTermVisitedCount;
    }
    
    private synchronized void applyDocValuesUpdatesList(final List<List<DocValuesUpdate>> updates, final SegmentState segState, final DocValuesFieldUpdates.Container dvUpdatesContainer) throws IOException {
        for (int idx = updates.size() - 1; idx >= 0; --idx) {
            this.applyDocValuesUpdates(updates.get(idx), segState, dvUpdatesContainer);
        }
    }
    
    private synchronized void applyDocValuesUpdates(final Iterable<? extends DocValuesUpdate> updates, final SegmentState segState, final DocValuesFieldUpdates.Container dvUpdatesContainer) throws IOException {
        final Fields fields = segState.reader.fields();
        String currentField = null;
        TermsEnum termsEnum = null;
        PostingsEnum postingsEnum = null;
        for (final DocValuesUpdate update : updates) {
            final Term term = update.term;
            final int limit = update.docIDUpto;
            if (!term.field().equals(currentField)) {
                currentField = term.field();
                final Terms terms = fields.terms(currentField);
                if (terms != null) {
                    termsEnum = terms.iterator();
                }
                else {
                    termsEnum = null;
                }
            }
            if (termsEnum == null) {
                continue;
            }
            if (!termsEnum.seekExact(term.bytes())) {
                continue;
            }
            final Bits acceptDocs = segState.rld.getLiveDocs();
            postingsEnum = termsEnum.postings(postingsEnum, 0);
            DocValuesFieldUpdates dvUpdates = dvUpdatesContainer.getUpdates(update.field, update.type);
            if (dvUpdates == null) {
                dvUpdates = dvUpdatesContainer.newUpdates(update.field, update.type, segState.reader.maxDoc());
            }
            int doc;
            while ((doc = postingsEnum.nextDoc()) != Integer.MAX_VALUE) {
                if (doc >= limit) {
                    break;
                }
                if (acceptDocs != null && !acceptDocs.get(doc)) {
                    continue;
                }
                dvUpdates.add(doc, update.value);
            }
        }
    }
    
    private static long applyQueryDeletes(final Iterable<QueryAndLimit> queriesIter, final SegmentState segState) throws IOException {
        long delCount = 0L;
        final LeafReaderContext readerContext = segState.reader.getContext();
        for (final QueryAndLimit ent : queriesIter) {
            final Query query = ent.query;
            final int limit = ent.limit;
            final IndexSearcher searcher = new IndexSearcher(readerContext.reader());
            searcher.setQueryCache(null);
            final Weight weight = searcher.createNormalizedWeight(query, false);
            final Scorer scorer = weight.scorer(readerContext);
            if (scorer != null) {
                final DocIdSetIterator it = scorer.iterator();
                final Bits liveDocs = readerContext.reader().getLiveDocs();
                while (true) {
                    final int doc = it.nextDoc();
                    if (doc >= limit) {
                        break;
                    }
                    if (liveDocs != null && !liveDocs.get(doc)) {
                        continue;
                    }
                    if (!segState.any) {
                        segState.rld.initWritableLiveDocs();
                        segState.any = true;
                    }
                    if (!segState.rld.delete(doc)) {
                        continue;
                    }
                    ++delCount;
                }
            }
        }
        return delCount;
    }
    
    private boolean checkDeleteTerm(final BytesRef term) {
        if (term != null && !BufferedUpdatesStream.$assertionsDisabled && this.lastDeleteTerm != null && term.compareTo(this.lastDeleteTerm) < 0) {
            throw new AssertionError((Object)("lastTerm=" + this.lastDeleteTerm + " vs term=" + term));
        }
        this.lastDeleteTerm = ((term == null) ? null : BytesRef.deepCopyOf(term));
        return true;
    }
    
    private boolean checkDeleteStats() {
        int numTerms2 = 0;
        long bytesUsed2 = 0L;
        for (final FrozenBufferedUpdates packet : this.updates) {
            numTerms2 += packet.numTermDeletes;
            bytesUsed2 += packet.bytesUsed;
        }
        assert numTerms2 == this.numTerms.get() : "numTerms2=" + numTerms2 + " vs " + this.numTerms.get();
        assert bytesUsed2 == this.bytesUsed.get() : "bytesUsed2=" + bytesUsed2 + " vs " + this.bytesUsed;
        return true;
    }
    
    static {
        sortSegInfoByDelGen = new Comparator<SegmentCommitInfo>() {
            @Override
            public int compare(final SegmentCommitInfo si1, final SegmentCommitInfo si2) {
                return Long.compare(si1.getBufferedDeletesGen(), si2.getBufferedDeletesGen());
            }
        };
    }
    
    public static class ApplyDeletesResult
    {
        public final boolean anyDeletes;
        public final long gen;
        public final List<SegmentCommitInfo> allDeleted;
        
        ApplyDeletesResult(final boolean anyDeletes, final long gen, final List<SegmentCommitInfo> allDeleted) {
            this.anyDeletes = anyDeletes;
            this.gen = gen;
            this.allDeleted = allDeleted;
        }
    }
    
    static class SegmentState
    {
        final long delGen;
        final ReadersAndUpdates rld;
        final SegmentReader reader;
        final int startDelCount;
        TermsEnum termsEnum;
        PostingsEnum postingsEnum;
        BytesRef term;
        boolean any;
        
        public SegmentState(final IndexWriter.ReaderPool pool, final SegmentCommitInfo info) throws IOException {
            this.rld = pool.get(info, true);
            this.startDelCount = this.rld.getPendingDeleteCount();
            this.reader = this.rld.getReader(IOContext.READ);
            this.delGen = info.getBufferedDeletesGen();
        }
        
        public void finish(final IndexWriter.ReaderPool pool) throws IOException {
            try {
                this.rld.release(this.reader);
            }
            finally {
                pool.release(this.rld);
            }
        }
    }
    
    static class SegmentQueue extends PriorityQueue<SegmentState>
    {
        public SegmentQueue(final int size) {
            super(size);
        }
        
        @Override
        protected boolean lessThan(final SegmentState a, final SegmentState b) {
            return a.term.compareTo(b.term) < 0;
        }
    }
    
    public static class QueryAndLimit
    {
        public final Query query;
        public final int limit;
        
        public QueryAndLimit(final Query query, final int limit) {
            this.query = query;
            this.limit = limit;
        }
    }
}
