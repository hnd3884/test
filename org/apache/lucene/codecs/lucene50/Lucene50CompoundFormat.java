package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.store.IndexInput;
import java.util.Iterator;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import java.io.IOException;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.CompoundFormat;

public final class Lucene50CompoundFormat extends CompoundFormat
{
    static final String DATA_EXTENSION = "cfs";
    static final String ENTRIES_EXTENSION = "cfe";
    static final String DATA_CODEC = "Lucene50CompoundData";
    static final String ENTRY_CODEC = "Lucene50CompoundEntries";
    static final int VERSION_START = 0;
    static final int VERSION_CURRENT = 0;
    
    @Override
    public Directory getCompoundReader(final Directory dir, final SegmentInfo si, final IOContext context) throws IOException {
        return new Lucene50CompoundReader(dir, si, context);
    }
    
    @Override
    public void write(final Directory dir, final SegmentInfo si, final IOContext context) throws IOException {
        final String dataFile = IndexFileNames.segmentFileName(si.name, "", "cfs");
        final String entriesFile = IndexFileNames.segmentFileName(si.name, "", "cfe");
        try (final IndexOutput data = dir.createOutput(dataFile, context);
             final IndexOutput entries = dir.createOutput(entriesFile, context)) {
            CodecUtil.writeIndexHeader(data, "Lucene50CompoundData", 0, si.getId(), "");
            CodecUtil.writeIndexHeader(entries, "Lucene50CompoundEntries", 0, si.getId(), "");
            entries.writeVInt(si.files().size());
            for (final String file : si.files()) {
                final long startOffset = data.getFilePointer();
                try (final IndexInput in = dir.openInput(file, IOContext.READONCE)) {
                    data.copyBytes(in, in.length());
                }
                final long endOffset = data.getFilePointer();
                final long length = endOffset - startOffset;
                entries.writeString(IndexFileNames.stripSegmentName(file));
                entries.writeLong(startOffset);
                entries.writeLong(length);
            }
            CodecUtil.writeFooter(data);
            CodecUtil.writeFooter(entries);
        }
    }
}
