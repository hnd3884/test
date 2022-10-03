package org.apache.lucene.facet.taxonomy;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SlowCodecReaderWrapper;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.CodecReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.store.Directory;

public abstract class TaxonomyMergeUtils
{
    private TaxonomyMergeUtils() {
    }
    
    public static void merge(final Directory srcIndexDir, final Directory srcTaxoDir, final DirectoryTaxonomyWriter.OrdinalMap map, final IndexWriter destIndexWriter, final DirectoryTaxonomyWriter destTaxoWriter, final FacetsConfig srcConfig) throws IOException {
        destTaxoWriter.addTaxonomy(srcTaxoDir, map);
        final int[] ordinalMap = map.getMap();
        final DirectoryReader reader = DirectoryReader.open(srcIndexDir);
        try {
            final List<LeafReaderContext> leaves = reader.leaves();
            final int numReaders = leaves.size();
            final CodecReader[] wrappedLeaves = new CodecReader[numReaders];
            for (int i = 0; i < numReaders; ++i) {
                wrappedLeaves[i] = SlowCodecReaderWrapper.wrap((LeafReader)new OrdinalMappingLeafReader(leaves.get(i).reader(), ordinalMap, srcConfig));
            }
            destIndexWriter.addIndexes(wrappedLeaves);
            destTaxoWriter.commit();
            destIndexWriter.commit();
        }
        finally {
            reader.close();
        }
    }
}
