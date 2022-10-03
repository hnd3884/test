package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.InfoStream;

public class SimpleMergedSegmentWarmer extends IndexWriter.IndexReaderWarmer
{
    private final InfoStream infoStream;
    
    public SimpleMergedSegmentWarmer(final InfoStream infoStream) {
        this.infoStream = infoStream;
    }
    
    @Override
    public void warm(final LeafReader reader) throws IOException {
        final long startTime = System.currentTimeMillis();
        int indexedCount = 0;
        int docValuesCount = 0;
        int normsCount = 0;
        for (final FieldInfo info : reader.getFieldInfos()) {
            if (info.getIndexOptions() != IndexOptions.NONE) {
                reader.terms(info.name);
                ++indexedCount;
                if (info.hasNorms()) {
                    reader.getNormValues(info.name);
                    ++normsCount;
                }
            }
            if (info.getDocValuesType() != DocValuesType.NONE) {
                switch (info.getDocValuesType()) {
                    case NUMERIC: {
                        reader.getNumericDocValues(info.name);
                        break;
                    }
                    case BINARY: {
                        reader.getBinaryDocValues(info.name);
                        break;
                    }
                    case SORTED: {
                        reader.getSortedDocValues(info.name);
                        break;
                    }
                    case SORTED_NUMERIC: {
                        reader.getSortedNumericDocValues(info.name);
                        break;
                    }
                    case SORTED_SET: {
                        reader.getSortedSetDocValues(info.name);
                        break;
                    }
                    default: {
                        assert false;
                        break;
                    }
                }
                ++docValuesCount;
            }
        }
        reader.document(0);
        reader.getTermVectors(0);
        if (this.infoStream.isEnabled("SMSW")) {
            this.infoStream.message("SMSW", "Finished warming segment: " + reader + ", indexed=" + indexedCount + ", docValues=" + docValuesCount + ", norms=" + normsCount + ", time=" + (System.currentTimeMillis() - startTime));
        }
    }
}
