package org.apache.lucene.codecs.lucene50;

import java.util.Iterator;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.store.ChecksumIndexInput;
import java.util.Map;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.index.CorruptIndexException;
import java.util.Collections;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.FieldInfosFormat;

public final class Lucene50FieldInfosFormat extends FieldInfosFormat
{
    static final String EXTENSION = "fnm";
    static final String CODEC_NAME = "Lucene50FieldInfos";
    static final int FORMAT_START = 0;
    static final int FORMAT_SAFE_MAPS = 1;
    static final int FORMAT_CURRENT = 1;
    static final byte STORE_TERMVECTOR = 1;
    static final byte OMIT_NORMS = 2;
    static final byte STORE_PAYLOADS = 4;
    
    @Override
    public FieldInfos read(final Directory directory, final SegmentInfo segmentInfo, final String segmentSuffix, final IOContext context) throws IOException {
        final String fileName = IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "fnm");
        try (final ChecksumIndexInput input = directory.openChecksumInput(fileName, context)) {
            Throwable priorE = null;
            FieldInfo[] infos = null;
            try {
                final int format = CodecUtil.checkIndexHeader(input, "Lucene50FieldInfos", 0, 1, segmentInfo.getId(), segmentSuffix);
                final int size = input.readVInt();
                infos = new FieldInfo[size];
                Map<String, String> lastAttributes = Collections.emptyMap();
                for (int i = 0; i < size; ++i) {
                    final String name = input.readString();
                    final int fieldNumber = input.readVInt();
                    if (fieldNumber < 0) {
                        throw new CorruptIndexException("invalid field number for field: " + name + ", fieldNumber=" + fieldNumber, input);
                    }
                    final byte bits = input.readByte();
                    final boolean storeTermVector = (bits & 0x1) != 0x0;
                    final boolean omitNorms = (bits & 0x2) != 0x0;
                    final boolean storePayloads = (bits & 0x4) != 0x0;
                    final IndexOptions indexOptions = getIndexOptions(input, input.readByte());
                    final DocValuesType docValuesType = getDocValuesType(input, input.readByte());
                    final long dvGen = input.readLong();
                    Map<String, String> attributes;
                    if (format >= 1) {
                        attributes = input.readMapOfStrings();
                    }
                    else {
                        attributes = Collections.unmodifiableMap((Map<? extends String, ? extends String>)input.readStringStringMap());
                    }
                    if (attributes.equals(lastAttributes)) {
                        attributes = lastAttributes;
                    }
                    lastAttributes = attributes;
                    try {
                        (infos[i] = new FieldInfo(name, fieldNumber, storeTermVector, omitNorms, storePayloads, indexOptions, docValuesType, dvGen, attributes)).checkConsistency();
                    }
                    catch (final IllegalStateException e) {
                        throw new CorruptIndexException("invalid fieldinfo for field: " + name + ", fieldNumber=" + fieldNumber, input, e);
                    }
                }
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(input, priorE);
            }
            return new FieldInfos(infos);
        }
    }
    
    private static byte docValuesByte(final DocValuesType type) {
        switch (type) {
            case NONE: {
                return 0;
            }
            case NUMERIC: {
                return 1;
            }
            case BINARY: {
                return 2;
            }
            case SORTED: {
                return 3;
            }
            case SORTED_SET: {
                return 4;
            }
            case SORTED_NUMERIC: {
                return 5;
            }
            default: {
                throw new AssertionError((Object)("unhandled DocValuesType: " + type));
            }
        }
    }
    
    private static DocValuesType getDocValuesType(final IndexInput input, final byte b) throws IOException {
        switch (b) {
            case 0: {
                return DocValuesType.NONE;
            }
            case 1: {
                return DocValuesType.NUMERIC;
            }
            case 2: {
                return DocValuesType.BINARY;
            }
            case 3: {
                return DocValuesType.SORTED;
            }
            case 4: {
                return DocValuesType.SORTED_SET;
            }
            case 5: {
                return DocValuesType.SORTED_NUMERIC;
            }
            default: {
                throw new CorruptIndexException("invalid docvalues byte: " + b, input);
            }
        }
    }
    
    private static byte indexOptionsByte(final IndexOptions indexOptions) {
        switch (indexOptions) {
            case NONE: {
                return 0;
            }
            case DOCS: {
                return 1;
            }
            case DOCS_AND_FREQS: {
                return 2;
            }
            case DOCS_AND_FREQS_AND_POSITIONS: {
                return 3;
            }
            case DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS: {
                return 4;
            }
            default: {
                throw new AssertionError((Object)("unhandled IndexOptions: " + indexOptions));
            }
        }
    }
    
    private static IndexOptions getIndexOptions(final IndexInput input, final byte b) throws IOException {
        switch (b) {
            case 0: {
                return IndexOptions.NONE;
            }
            case 1: {
                return IndexOptions.DOCS;
            }
            case 2: {
                return IndexOptions.DOCS_AND_FREQS;
            }
            case 3: {
                return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
            }
            case 4: {
                return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
            }
            default: {
                throw new CorruptIndexException("invalid IndexOptions byte: " + b, input);
            }
        }
    }
    
    @Override
    public void write(final Directory directory, final SegmentInfo segmentInfo, final String segmentSuffix, final FieldInfos infos, final IOContext context) throws IOException {
        final String fileName = IndexFileNames.segmentFileName(segmentInfo.name, segmentSuffix, "fnm");
        try (final IndexOutput output = directory.createOutput(fileName, context)) {
            CodecUtil.writeIndexHeader(output, "Lucene50FieldInfos", 1, segmentInfo.getId(), segmentSuffix);
            output.writeVInt(infos.size());
            for (final FieldInfo fi : infos) {
                fi.checkConsistency();
                output.writeString(fi.name);
                output.writeVInt(fi.number);
                byte bits = 0;
                if (fi.hasVectors()) {
                    bits |= 0x1;
                }
                if (fi.omitsNorms()) {
                    bits |= 0x2;
                }
                if (fi.hasPayloads()) {
                    bits |= 0x4;
                }
                output.writeByte(bits);
                output.writeByte(indexOptionsByte(fi.getIndexOptions()));
                output.writeByte(docValuesByte(fi.getDocValuesType()));
                output.writeLong(fi.getDocValuesGen());
                output.writeMapOfStrings(fi.attributes());
            }
            CodecUtil.writeFooter(output);
        }
    }
    
    static {
        assert DocValuesType.values().length == 6;
        assert IndexOptions.values().length == 5;
    }
}
