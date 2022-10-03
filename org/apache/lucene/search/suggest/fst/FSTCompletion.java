package org.apache.lucene.search.suggest.fst;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import java.util.List;
import java.util.Collections;
import org.apache.lucene.util.fst.FST;
import java.util.ArrayList;

public class FSTCompletion
{
    public static final int DEFAULT_BUCKETS = 10;
    private static final ArrayList<Completion> EMPTY_RESULT;
    private final FST<Object> automaton;
    private final FST.Arc<Object>[] rootArcs;
    private boolean exactFirst;
    private boolean higherWeightsFirst;
    
    public FSTCompletion(final FST<Object> automaton, final boolean higherWeightsFirst, final boolean exactFirst) {
        this.automaton = automaton;
        if (automaton != null) {
            this.rootArcs = cacheRootArcs(automaton);
        }
        else {
            this.rootArcs = (FST.Arc<Object>[])new FST.Arc[0];
        }
        this.higherWeightsFirst = higherWeightsFirst;
        this.exactFirst = exactFirst;
    }
    
    public FSTCompletion(final FST<Object> automaton) {
        this(automaton, true, true);
    }
    
    private static FST.Arc<Object>[] cacheRootArcs(final FST<Object> automaton) {
        try {
            final List<FST.Arc<Object>> rootArcs = new ArrayList<FST.Arc<Object>>();
            final FST.Arc<Object> arc = (FST.Arc<Object>)automaton.getFirstArc(new FST.Arc());
            final FST.BytesReader fstReader = automaton.getBytesReader();
            automaton.readFirstTargetArc((FST.Arc)arc, (FST.Arc)arc, fstReader);
            while (true) {
                rootArcs.add((FST.Arc<Object>)new FST.Arc().copyFrom((FST.Arc)arc));
                if (arc.isLast()) {
                    break;
                }
                automaton.readNextArc((FST.Arc)arc, fstReader);
            }
            Collections.reverse(rootArcs);
            return rootArcs.toArray(new FST.Arc[rootArcs.size()]);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private int getExactMatchStartingFromRootArc(int rootArcIndex, final BytesRef utf8) {
        try {
            final FST.Arc<Object> scratch = (FST.Arc<Object>)new FST.Arc();
            final FST.BytesReader fstReader = this.automaton.getBytesReader();
            while (rootArcIndex < this.rootArcs.length) {
                final FST.Arc<Object> rootArc = this.rootArcs[rootArcIndex];
                final FST.Arc<Object> arc = (FST.Arc<Object>)scratch.copyFrom((FST.Arc)rootArc);
                if (this.descendWithPrefix(arc, utf8)) {
                    this.automaton.readFirstTargetArc((FST.Arc)arc, (FST.Arc)arc, fstReader);
                    if (arc.label == -1) {
                        return rootArc.label;
                    }
                }
                ++rootArcIndex;
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
    
    public List<Completion> lookup(final CharSequence key, final int num) {
        if (key.length() == 0 || this.automaton == null) {
            return FSTCompletion.EMPTY_RESULT;
        }
        try {
            final BytesRef keyUtf8 = new BytesRef(key);
            if (!this.higherWeightsFirst && this.rootArcs.length > 1) {
                return this.lookupSortedAlphabetically(keyUtf8, num);
            }
            return this.lookupSortedByWeight(keyUtf8, num, false);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private List<Completion> lookupSortedAlphabetically(final BytesRef key, final int num) throws IOException {
        List<Completion> res = this.lookupSortedByWeight(key, num, true);
        Collections.sort(res);
        if (res.size() > num) {
            res = res.subList(0, num);
        }
        return res;
    }
    
    private ArrayList<Completion> lookupSortedByWeight(final BytesRef key, final int num, final boolean collectAll) throws IOException {
        final ArrayList<Completion> res = new ArrayList<Completion>(Math.min(10, num));
        final BytesRef output = BytesRef.deepCopyOf(key);
        for (int i = 0; i < this.rootArcs.length; ++i) {
            final FST.Arc<Object> rootArc = this.rootArcs[i];
            final FST.Arc<Object> arc = (FST.Arc<Object>)new FST.Arc().copyFrom((FST.Arc)rootArc);
            if (this.descendWithPrefix(arc, key)) {
                output.length = key.length - 1;
                if (this.collect(res, num, rootArc.label, output, arc) && !collectAll) {
                    if (this.exactFirst && !this.checkExistingAndReorder(res, key)) {
                        final int exactMatchBucket = this.getExactMatchStartingFromRootArc(i, key);
                        if (exactMatchBucket != -1) {
                            while (res.size() >= num) {
                                res.remove(res.size() - 1);
                            }
                            res.add(0, new Completion(key, exactMatchBucket));
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return res;
    }
    
    private boolean checkExistingAndReorder(final ArrayList<Completion> list, final BytesRef key) {
        int i = list.size();
        while (--i >= 0) {
            if (key.equals((Object)list.get(i).utf8)) {
                list.add(0, list.remove(i));
                return true;
            }
        }
        return false;
    }
    
    private boolean descendWithPrefix(final FST.Arc<Object> arc, final BytesRef utf8) throws IOException {
        final int max = utf8.offset + utf8.length;
        final FST.BytesReader fstReader = this.automaton.getBytesReader();
        for (int i = utf8.offset; i < max; ++i) {
            if (this.automaton.findTargetArc(utf8.bytes[i] & 0xFF, (FST.Arc)arc, (FST.Arc)arc, fstReader) == null) {
                return false;
            }
        }
        return true;
    }
    
    private boolean collect(final List<Completion> res, final int num, final int bucket, final BytesRef output, final FST.Arc<Object> arc) throws IOException {
        if (output.length == output.bytes.length) {
            output.bytes = ArrayUtil.grow(output.bytes);
        }
        assert output.offset == 0;
        output.bytes[output.length++] = (byte)arc.label;
        final FST.BytesReader fstReader = this.automaton.getBytesReader();
        this.automaton.readFirstTargetArc((FST.Arc)arc, (FST.Arc)arc, fstReader);
        while (true) {
            if (arc.label == -1) {
                res.add(new Completion(output, bucket));
                if (res.size() >= num) {
                    return true;
                }
            }
            else {
                final int save = output.length;
                if (this.collect(res, num, bucket, output, (FST.Arc<Object>)new FST.Arc().copyFrom((FST.Arc)arc))) {
                    return true;
                }
                output.length = save;
            }
            if (arc.isLast()) {
                return false;
            }
            this.automaton.readNextArc((FST.Arc)arc, fstReader);
        }
    }
    
    public int getBucketCount() {
        return this.rootArcs.length;
    }
    
    public int getBucket(final CharSequence key) {
        return this.getExactMatchStartingFromRootArc(0, new BytesRef(key));
    }
    
    public FST<Object> getFST() {
        return this.automaton;
    }
    
    static {
        EMPTY_RESULT = new ArrayList<Completion>();
    }
    
    public static final class Completion implements Comparable<Completion>
    {
        public final BytesRef utf8;
        public final int bucket;
        
        Completion(final BytesRef key, final int bucket) {
            this.utf8 = BytesRef.deepCopyOf(key);
            this.bucket = bucket;
        }
        
        @Override
        public String toString() {
            return this.utf8.utf8ToString() + "/" + this.bucket;
        }
        
        @Override
        public int compareTo(final Completion o) {
            return this.utf8.compareTo(o.utf8);
        }
    }
}
