package org.apache.lucene.analysis.charfilter;

import java.util.Iterator;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import java.util.TreeMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.FST;

public class NormalizeCharMap
{
    final FST<CharsRef> map;
    final Map<Character, FST.Arc<CharsRef>> cachedRootArcs;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private NormalizeCharMap(final FST<CharsRef> map) {
        this.cachedRootArcs = new HashMap<Character, FST.Arc<CharsRef>>();
        this.map = map;
        if (map != null) {
            try {
                final FST.Arc<CharsRef> scratchArc = (FST.Arc<CharsRef>)new FST.Arc();
                final FST.BytesReader fstReader = map.getBytesReader();
                map.getFirstArc((FST.Arc)scratchArc);
                if (FST.targetHasArcs((FST.Arc)scratchArc)) {
                    map.readFirstRealTargetArc(scratchArc.target, (FST.Arc)scratchArc, fstReader);
                    while (NormalizeCharMap.$assertionsDisabled || scratchArc.label != -1) {
                        this.cachedRootArcs.put((char)scratchArc.label, (FST.Arc<CharsRef>)new FST.Arc().copyFrom((FST.Arc)scratchArc));
                        if (scratchArc.isLast()) {
                            return;
                        }
                        map.readNextRealArc((FST.Arc)scratchArc, fstReader);
                    }
                    throw new AssertionError();
                }
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
    
    public static class Builder
    {
        private final Map<String, String> pendingPairs;
        
        public Builder() {
            this.pendingPairs = new TreeMap<String, String>();
        }
        
        public void add(final String match, final String replacement) {
            if (match.length() == 0) {
                throw new IllegalArgumentException("cannot match the empty string");
            }
            if (this.pendingPairs.containsKey(match)) {
                throw new IllegalArgumentException("match \"" + match + "\" was already added");
            }
            this.pendingPairs.put(match, replacement);
        }
        
        public NormalizeCharMap build() {
            FST<CharsRef> map;
            try {
                final Outputs<CharsRef> outputs = (Outputs<CharsRef>)CharSequenceOutputs.getSingleton();
                final org.apache.lucene.util.fst.Builder<CharsRef> builder = (org.apache.lucene.util.fst.Builder<CharsRef>)new org.apache.lucene.util.fst.Builder(FST.INPUT_TYPE.BYTE2, (Outputs)outputs);
                final IntsRefBuilder scratch = new IntsRefBuilder();
                for (final Map.Entry<String, String> ent : this.pendingPairs.entrySet()) {
                    builder.add(Util.toUTF16((CharSequence)ent.getKey(), scratch), (Object)new CharsRef((String)ent.getValue()));
                }
                map = (FST<CharsRef>)builder.finish();
                this.pendingPairs.clear();
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
            return new NormalizeCharMap(map, null);
        }
    }
}
