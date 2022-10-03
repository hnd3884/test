package org.apache.lucene.codecs.lucene50;

import java.util.Iterator;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import org.apache.lucene.store.ChecksumIndexInput;
import java.util.Collection;
import org.apache.lucene.codecs.Codec;
import java.util.Set;
import java.util.Map;
import java.util.Collections;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.util.Version;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.Directory;
import org.apache.lucene.codecs.SegmentInfoFormat;

public class Lucene50SegmentInfoFormat extends SegmentInfoFormat
{
    public static final String SI_EXTENSION = "si";
    static final String CODEC_NAME = "Lucene50SegmentInfo";
    static final int VERSION_START = 0;
    static final int VERSION_SAFE_MAPS = 1;
    static final int VERSION_CURRENT = 1;
    
    @Override
    public SegmentInfo read(final Directory dir, final String segment, final byte[] segmentID, final IOContext context) throws IOException {
        final String fileName = IndexFileNames.segmentFileName(segment, "", "si");
        try (final ChecksumIndexInput input = dir.openChecksumInput(fileName, context)) {
            Throwable priorE = null;
            SegmentInfo si = null;
            try {
                final int format = CodecUtil.checkIndexHeader(input, "Lucene50SegmentInfo", 0, 1, segmentID, "");
                final Version version = Version.fromBits(input.readInt(), input.readInt(), input.readInt());
                final int docCount = input.readInt();
                if (docCount < 0) {
                    throw new CorruptIndexException("invalid docCount: " + docCount, input);
                }
                final boolean isCompoundFile = input.readByte() == 1;
                Map<String, String> diagnostics;
                Set<String> files;
                Map<String, String> attributes;
                if (format >= 1) {
                    diagnostics = input.readMapOfStrings();
                    files = input.readSetOfStrings();
                    attributes = input.readMapOfStrings();
                }
                else {
                    diagnostics = Collections.unmodifiableMap((Map<? extends String, ? extends String>)input.readStringStringMap());
                    files = Collections.unmodifiableSet((Set<? extends String>)input.readStringSet());
                    attributes = Collections.unmodifiableMap((Map<? extends String, ? extends String>)input.readStringStringMap());
                }
                si = new SegmentInfo(dir, version, segment, docCount, isCompoundFile, null, diagnostics, segmentID, attributes);
                si.setFiles(files);
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(input, priorE);
            }
            return si;
        }
    }
    
    @Override
    public void write(final Directory dir, final SegmentInfo si, final IOContext ioContext) throws IOException {
        final String fileName = IndexFileNames.segmentFileName(si.name, "", "si");
        try (final IndexOutput output = dir.createOutput(fileName, ioContext)) {
            si.addFile(fileName);
            CodecUtil.writeIndexHeader(output, "Lucene50SegmentInfo", 1, si.getId(), "");
            final Version version = si.getVersion();
            if (version.major < 5) {
                throw new IllegalArgumentException("invalid major version: should be >= 5 but got: " + version.major + " segment=" + si);
            }
            output.writeInt(version.major);
            output.writeInt(version.minor);
            output.writeInt(version.bugfix);
            assert version.prerelease == 0;
            output.writeInt(si.maxDoc());
            output.writeByte((byte)(si.getUseCompoundFile() ? 1 : -1));
            output.writeMapOfStrings(si.getDiagnostics());
            final Set<String> files = si.files();
            for (final String file : files) {
                if (!IndexFileNames.parseSegmentName(file).equals(si.name)) {
                    throw new IllegalArgumentException("invalid files: expected segment=" + si.name + ", got=" + files);
                }
            }
            output.writeSetOfStrings(files);
            output.writeMapOfStrings(si.getAttributes());
            CodecUtil.writeFooter(output);
        }
    }
}
