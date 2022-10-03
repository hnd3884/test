package org.apache.lucene.bkdtree;

import org.apache.lucene.util.DocIdSetBuilder;
import org.apache.lucene.store.ByteArrayDataInput;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.index.SortedNumericDocValues;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Accountable;

@Deprecated
final class BKDTreeReader implements Accountable
{
    private final int[] splitValues;
    private final int leafNodeOffset;
    private final long[] leafBlockFPs;
    final int maxDoc;
    final IndexInput in;
    
    public BKDTreeReader(final IndexInput in, final int maxDoc) throws IOException {
        final int numLeaves = in.readVInt();
        this.leafNodeOffset = numLeaves;
        this.splitValues = new int[numLeaves];
        for (int i = 0; i < numLeaves; ++i) {
            this.splitValues[i] = in.readInt();
        }
        this.leafBlockFPs = new long[numLeaves];
        for (int i = 0; i < numLeaves; ++i) {
            this.leafBlockFPs[i] = in.readVLong();
        }
        this.maxDoc = maxDoc;
        this.in = in;
    }
    
    public DocIdSet intersect(final double latMin, final double latMax, final double lonMin, final double lonMax, final LatLonFilter filter, final SortedNumericDocValues sndv) throws IOException {
        if (!BKDTreeWriter.validLat(latMin)) {
            throw new IllegalArgumentException("invalid latMin: " + latMin);
        }
        if (!BKDTreeWriter.validLat(latMax)) {
            throw new IllegalArgumentException("invalid latMax: " + latMax);
        }
        if (!BKDTreeWriter.validLon(lonMin)) {
            throw new IllegalArgumentException("invalid lonMin: " + lonMin);
        }
        if (!BKDTreeWriter.validLon(lonMax)) {
            throw new IllegalArgumentException("invalid lonMax: " + lonMax);
        }
        final int latMinEnc = BKDTreeWriter.encodeLat(latMin);
        final int latMaxEnc = BKDTreeWriter.encodeLat(latMax);
        final int lonMinEnc = BKDTreeWriter.encodeLon(lonMin);
        final int lonMaxEnc = BKDTreeWriter.encodeLon(lonMax);
        final QueryState state = new QueryState(this.in.clone(), this.maxDoc, latMinEnc, latMaxEnc, lonMinEnc, lonMaxEnc, filter, sndv);
        final int hitCount = this.intersect(state, 1, BKDTreeWriter.encodeLat(-90.0), BKDTreeWriter.encodeLat(Math.nextAfter(90.0, Double.POSITIVE_INFINITY)), BKDTreeWriter.encodeLon(-180.0), BKDTreeWriter.encodeLon(Math.nextAfter(180.0, Double.POSITIVE_INFINITY)));
        return state.docs.build((long)hitCount);
    }
    
    private int addAll(final QueryState state, final int nodeID) throws IOException {
        if (nodeID >= this.leafNodeOffset) {
            final long fp = this.leafBlockFPs[nodeID - this.leafNodeOffset];
            if (fp == 0L) {
                return 0;
            }
            state.in.seek(fp);
            final int count = state.in.readVInt();
            state.docs.grow(count);
            for (int i = 0; i < count; ++i) {
                final int docID = state.in.readInt();
                state.docs.add(docID);
            }
            return count;
        }
        else {
            final int splitValue = this.splitValues[nodeID];
            if (splitValue == Integer.MAX_VALUE) {
                return 0;
            }
            int count2 = this.addAll(state, 2 * nodeID);
            count2 += this.addAll(state, 2 * nodeID + 1);
            return count2;
        }
    }
    
    private int intersect(final QueryState state, final int nodeID, final int cellLatMinEnc, final int cellLatMaxEnc, final int cellLonMinEnc, final int cellLonMaxEnc) throws IOException {
        if (state.latLonFilter != null) {
            if (cellLatMinEnc > state.latMinEnc || cellLatMaxEnc < state.latMaxEnc || cellLonMinEnc > state.lonMinEnc || cellLonMaxEnc < state.lonMaxEnc) {
                final Relation r = state.latLonFilter.compare(BKDTreeWriter.decodeLat(cellLatMinEnc), BKDTreeWriter.decodeLat(cellLatMaxEnc), BKDTreeWriter.decodeLon(cellLonMinEnc), BKDTreeWriter.decodeLon(cellLonMaxEnc));
                if (r == Relation.SHAPE_OUTSIDE_CELL) {
                    return 0;
                }
                if (r == Relation.CELL_INSIDE_SHAPE) {
                    return this.addAll(state, nodeID);
                }
            }
        }
        else if (state.latMinEnc <= cellLatMinEnc && state.latMaxEnc >= cellLatMaxEnc && state.lonMinEnc <= cellLonMinEnc && state.lonMaxEnc >= cellLonMaxEnc) {
            return this.addAll(state, nodeID);
        }
        final long latRange = cellLatMaxEnc - (long)cellLatMinEnc;
        final long lonRange = cellLonMaxEnc - (long)cellLonMinEnc;
        int dim;
        if (latRange >= lonRange) {
            dim = 0;
        }
        else {
            dim = 1;
        }
        if (nodeID >= this.leafNodeOffset) {
            int hitCount = 0;
            final long fp = this.leafBlockFPs[nodeID - this.leafNodeOffset];
            if (fp == 0L) {
                return 0;
            }
            state.in.seek(fp);
            final int count = state.in.readVInt();
            state.docs.grow(count);
            for (int i = 0; i < count; ++i) {
                final int docID = state.in.readInt();
                state.sndv.setDocument(docID);
                for (int docValueCount = state.sndv.count(), j = 0; j < docValueCount; ++j) {
                    final long enc = state.sndv.valueAt(j);
                    final int latEnc = (int)(enc >> 32 & 0xFFFFFFFFL);
                    final int lonEnc = (int)(enc & 0xFFFFFFFFL);
                    if (latEnc >= state.latMinEnc && latEnc < state.latMaxEnc && lonEnc >= state.lonMinEnc && lonEnc < state.lonMaxEnc && (state.latLonFilter == null || state.latLonFilter.accept(BKDTreeWriter.decodeLat(latEnc), BKDTreeWriter.decodeLon(lonEnc)))) {
                        state.docs.add(docID);
                        ++hitCount;
                        break;
                    }
                }
            }
            return hitCount;
        }
        else {
            final int splitValue = this.splitValues[nodeID];
            if (splitValue == Integer.MAX_VALUE) {
                return 0;
            }
            int count2 = 0;
            if (dim == 0) {
                if (state.latMinEnc < splitValue) {
                    count2 += this.intersect(state, 2 * nodeID, cellLatMinEnc, splitValue, cellLonMinEnc, cellLonMaxEnc);
                }
                if (state.latMaxEnc >= splitValue) {
                    count2 += this.intersect(state, 2 * nodeID + 1, splitValue, cellLatMaxEnc, cellLonMinEnc, cellLonMaxEnc);
                }
            }
            else {
                assert dim == 1;
                if (state.lonMinEnc < splitValue) {
                    count2 += this.intersect(state, 2 * nodeID, cellLatMinEnc, cellLatMaxEnc, cellLonMinEnc, splitValue);
                }
                if (state.lonMaxEnc >= splitValue) {
                    count2 += this.intersect(state, 2 * nodeID + 1, cellLatMinEnc, cellLatMaxEnc, splitValue, cellLonMaxEnc);
                }
            }
            return count2;
        }
    }
    
    public long ramBytesUsed() {
        return this.splitValues.length * 4 + this.leafBlockFPs.length * 8;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    enum Relation
    {
        CELL_INSIDE_SHAPE, 
        SHAPE_CROSSES_CELL, 
        SHAPE_OUTSIDE_CELL;
    }
    
    private static final class QueryState
    {
        final IndexInput in;
        byte[] scratch;
        final ByteArrayDataInput scratchReader;
        final DocIdSetBuilder docs;
        final int latMinEnc;
        final int latMaxEnc;
        final int lonMinEnc;
        final int lonMaxEnc;
        final LatLonFilter latLonFilter;
        final SortedNumericDocValues sndv;
        
        public QueryState(final IndexInput in, final int maxDoc, final int latMinEnc, final int latMaxEnc, final int lonMinEnc, final int lonMaxEnc, final LatLonFilter latLonFilter, final SortedNumericDocValues sndv) {
            this.scratch = new byte[16];
            this.scratchReader = new ByteArrayDataInput(this.scratch);
            this.in = in;
            this.docs = new DocIdSetBuilder(maxDoc);
            this.latMinEnc = latMinEnc;
            this.latMaxEnc = latMaxEnc;
            this.lonMinEnc = lonMinEnc;
            this.lonMaxEnc = lonMaxEnc;
            this.latLonFilter = latLonFilter;
            this.sndv = sndv;
        }
    }
    
    interface LatLonFilter
    {
        boolean accept(final double p0, final double p1);
        
        Relation compare(final double p0, final double p1, final double p2, final double p3);
    }
}
