package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.packed.PackedLongValues;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.InfoStream;
import java.util.Map;
import org.apache.lucene.search.Sort;

public final class SortingMergePolicy extends MergePolicyWrapper
{
    public static final String SORTER_ID_PROP = "sorter";
    final Sorter sorter;
    final Sort sort;
    
    public static boolean isSorted(final LeafReader reader, final Sort sort) {
        final String description = getSortDescription(reader);
        return description != null && description.equals(sort.toString());
    }
    
    private static String getSortDescription(final LeafReader reader) {
        if (reader instanceof SegmentReader) {
            final SegmentReader segReader = (SegmentReader)reader;
            final Map<String, String> diagnostics = segReader.getSegmentInfo().info.getDiagnostics();
            if (diagnostics != null) {
                return diagnostics.get("sorter");
            }
        }
        else if (reader instanceof FilterLeafReader) {
            return getSortDescription(FilterLeafReader.unwrap(reader));
        }
        return null;
    }
    
    private MergePolicy.MergeSpecification sortedMergeSpecification(final MergePolicy.MergeSpecification specification, final InfoStream infoStream) {
        if (specification == null) {
            return null;
        }
        final MergePolicy.MergeSpecification sortingSpec = new SortingMergeSpecification(infoStream);
        for (final MergePolicy.OneMerge merge : specification.merges) {
            sortingSpec.add(merge);
        }
        return sortingSpec;
    }
    
    public SortingMergePolicy(final MergePolicy in, final Sort sort) {
        super(in);
        this.sorter = new Sorter(sort);
        this.sort = sort;
    }
    
    public Sort getSort() {
        return this.sort;
    }
    
    public MergePolicy.MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
        return this.sortedMergeSpecification(this.in.findMerges(mergeTrigger, segmentInfos, writer), writer.infoStream);
    }
    
    public MergePolicy.MergeSpecification findForcedMerges(final SegmentInfos segmentInfos, final int maxSegmentCount, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
        return this.sortedMergeSpecification(this.in.findForcedMerges(segmentInfos, maxSegmentCount, (Map)segmentsToMerge, writer), writer.infoStream);
    }
    
    public MergePolicy.MergeSpecification findForcedDeletesMerges(final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
        return this.sortedMergeSpecification(this.in.findForcedDeletesMerges(segmentInfos, writer), writer.infoStream);
    }
    
    public String toString() {
        return "SortingMergePolicy(" + this.in + ", sorter=" + this.sorter + ")";
    }
    
    class SortingOneMerge extends MergePolicy.OneMerge
    {
        List<CodecReader> unsortedReaders;
        Sorter.DocMap docMap;
        LeafReader sortedView;
        final InfoStream infoStream;
        
        SortingOneMerge(final List<SegmentCommitInfo> segments, final InfoStream infoStream) {
            super((List)segments);
            this.infoStream = infoStream;
        }
        
        public List<CodecReader> getMergeReaders() throws IOException {
            if (this.unsortedReaders == null) {
                this.unsortedReaders = super.getMergeReaders();
                if (this.infoStream.isEnabled("SMP")) {
                    this.infoStream.message("SMP", "sorting " + this.unsortedReaders);
                    for (final LeafReader leaf : this.unsortedReaders) {
                        String sortDescription = getSortDescription(leaf);
                        if (sortDescription == null) {
                            sortDescription = "not sorted";
                        }
                        this.infoStream.message("SMP", "seg=" + leaf + " " + sortDescription);
                    }
                }
                final List<LeafReader> wrapped = new ArrayList<LeafReader>(this.unsortedReaders.size());
                for (LeafReader leaf2 : this.unsortedReaders) {
                    if (leaf2 instanceof SegmentReader) {
                        leaf2 = new MergeReaderWrapper((SegmentReader)leaf2);
                    }
                    wrapped.add(leaf2);
                }
                LeafReader atomicView;
                if (wrapped.size() == 1) {
                    atomicView = wrapped.get(0);
                }
                else {
                    final CompositeReader multiReader = (CompositeReader)new MultiReader((IndexReader[])wrapped.toArray(new LeafReader[wrapped.size()]));
                    atomicView = (LeafReader)new SlowCompositeReaderWrapper(multiReader, true);
                }
                this.docMap = SortingMergePolicy.this.sorter.sort(atomicView);
                this.sortedView = SortingLeafReader.wrap(atomicView, this.docMap);
            }
            if (this.docMap == null) {
                if (this.infoStream.isEnabled("SMP")) {
                    this.infoStream.message("SMP", "readers already sorted, omitting sort");
                }
                return this.unsortedReaders;
            }
            if (this.infoStream.isEnabled("SMP")) {
                this.infoStream.message("SMP", "sorting readers by " + SortingMergePolicy.this.sort);
            }
            return Collections.singletonList(SlowCodecReaderWrapper.wrap(this.sortedView));
        }
        
        public void setMergeInfo(final SegmentCommitInfo info) {
            final Map<String, String> diagnostics = info.info.getDiagnostics();
            diagnostics.put("sorter", SortingMergePolicy.this.sorter.getID());
            super.setMergeInfo(info);
        }
        
        private PackedLongValues getDeletes(final List<CodecReader> readers) {
            final PackedLongValues.Builder deletes = PackedLongValues.monotonicBuilder(0.0f);
            int deleteCount = 0;
            for (final LeafReader reader : readers) {
                final int maxDoc = reader.maxDoc();
                final Bits liveDocs = reader.getLiveDocs();
                for (int i = 0; i < maxDoc; ++i) {
                    if (liveDocs != null && !liveDocs.get(i)) {
                        ++deleteCount;
                    }
                    else {
                        deletes.add((long)deleteCount);
                    }
                }
            }
            return deletes.build();
        }
        
        public MergePolicy.DocMap getDocMap(final MergeState mergeState) {
            if (this.unsortedReaders == null) {
                throw new IllegalStateException();
            }
            if (this.docMap == null) {
                return super.getDocMap(mergeState);
            }
            assert mergeState.docMaps.length == 1;
            final PackedLongValues deletes = this.getDeletes(this.unsortedReaders);
            return new MergePolicy.DocMap() {
                public int map(final int old) {
                    final int oldWithDeletes = old + (int)deletes.get(old);
                    final int newWithDeletes = SortingOneMerge.this.docMap.oldToNew(oldWithDeletes);
                    return mergeState.docMaps[0].get(newWithDeletes);
                }
            };
        }
        
        public String toString() {
            return "SortingMergePolicy.SortingOneMerge(segments=" + this.segString() + " sort=" + SortingMergePolicy.this.sort + ")";
        }
    }
    
    class SortingMergeSpecification extends MergePolicy.MergeSpecification
    {
        final InfoStream infoStream;
        
        SortingMergeSpecification(final InfoStream infoStream) {
            this.infoStream = infoStream;
        }
        
        public void add(final MergePolicy.OneMerge merge) {
            super.add((MergePolicy.OneMerge)new SortingOneMerge(merge.segments, this.infoStream));
        }
        
        public String segString(final Directory dir) {
            return "SortingMergeSpec(" + super.segString(dir) + ", sorter=" + SortingMergePolicy.this.sorter + ")";
        }
    }
}
