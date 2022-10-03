package org.apache.lucene.index;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.Collection;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.store.IndexInput;
import java.util.Set;
import java.util.HashMap;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.IOContext;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.lucene.util.Version;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public final class SegmentInfos implements Cloneable, Iterable<SegmentCommitInfo>
{
    public static final int VERSION_40 = 0;
    public static final int VERSION_46 = 1;
    public static final int VERSION_48 = 2;
    public static final int VERSION_49 = 3;
    public static final int VERSION_50 = 4;
    public static final int VERSION_51 = 5;
    public static final int VERSION_53 = 6;
    static final int VERSION_CURRENT = 6;
    public int counter;
    public long version;
    private long generation;
    private long lastGeneration;
    public Map<String, String> userData;
    private List<SegmentCommitInfo> segments;
    private static PrintStream infoStream;
    private byte[] id;
    private Version luceneVersion;
    private Version minSegmentLuceneVersion;
    private static final List<String> unsupportedCodecs;
    boolean pendingCommit;
    
    public SegmentInfos() {
        this.userData = Collections.emptyMap();
        this.segments = new ArrayList<SegmentCommitInfo>();
    }
    
    public SegmentCommitInfo info(final int i) {
        return this.segments.get(i);
    }
    
    public static long getLastCommitGeneration(final String[] files) {
        long max = -1L;
        for (final String file : files) {
            if (file.startsWith("segments") && !file.equals("segments.gen")) {
                final long gen = generationFromSegmentsFileName(file);
                if (gen > max) {
                    max = gen;
                }
            }
        }
        return max;
    }
    
    public static long getLastCommitGeneration(final Directory directory) throws IOException {
        return getLastCommitGeneration(directory.listAll());
    }
    
    public static String getLastCommitSegmentsFileName(final String[] files) {
        return IndexFileNames.fileNameFromGeneration("segments", "", getLastCommitGeneration(files));
    }
    
    public static String getLastCommitSegmentsFileName(final Directory directory) throws IOException {
        return IndexFileNames.fileNameFromGeneration("segments", "", getLastCommitGeneration(directory));
    }
    
    public String getSegmentsFileName() {
        return IndexFileNames.fileNameFromGeneration("segments", "", this.lastGeneration);
    }
    
    public static long generationFromSegmentsFileName(final String fileName) {
        if (fileName.equals("segments")) {
            return 0L;
        }
        if (fileName.startsWith("segments")) {
            return Long.parseLong(fileName.substring(1 + "segments".length()), 36);
        }
        throw new IllegalArgumentException("fileName \"" + fileName + "\" is not a segments file");
    }
    
    private long getNextPendingGeneration() {
        if (this.generation == -1L) {
            return 1L;
        }
        return this.generation + 1L;
    }
    
    public byte[] getId() {
        return (byte[])((this.id == null) ? null : ((byte[])this.id.clone()));
    }
    
    public static final SegmentInfos readCommit(final Directory directory, final String segmentFileName) throws IOException {
        final long generation = generationFromSegmentsFileName(segmentFileName);
        try (final ChecksumIndexInput input = directory.openChecksumInput(segmentFileName, IOContext.READ)) {
            final int magic = input.readInt();
            if (magic != 1071082519) {
                throw new IndexFormatTooOldException(input, magic, 1071082519, 1071082519);
            }
            final int format = CodecUtil.checkHeaderNoMagic(input, "segments", 0, 6);
            byte[] id = null;
            if (format >= 4) {
                id = new byte[16];
                input.readBytes(id, 0, id.length);
                CodecUtil.checkIndexHeaderSuffix(input, Long.toString(generation, 36));
            }
            final SegmentInfos infos = new SegmentInfos();
            infos.id = id;
            infos.generation = generation;
            infos.lastGeneration = generation;
            if (format >= 6) {
                infos.luceneVersion = Version.fromBits(input.readVInt(), input.readVInt(), input.readVInt());
            }
            infos.version = input.readLong();
            infos.counter = input.readInt();
            final int numSegments = input.readInt();
            if (numSegments < 0) {
                throw new CorruptIndexException("invalid segment count: " + numSegments, input);
            }
            if (format >= 6 && numSegments > 0) {
                infos.minSegmentLuceneVersion = Version.fromBits(input.readVInt(), input.readVInt(), input.readVInt());
                if (!infos.minSegmentLuceneVersion.onOrAfter(Version.LUCENE_4_0_0_ALPHA)) {
                    throw new IndexFormatTooOldException(input, "this index contains a too-old segment (version: " + infos.minSegmentLuceneVersion + ")");
                }
            }
            long totalDocs = 0L;
            for (int seg = 0; seg < numSegments; ++seg) {
                final String segName = input.readString();
                byte[] segmentID;
                if (format >= 4) {
                    final byte hasID = input.readByte();
                    if (hasID == 1) {
                        segmentID = new byte[16];
                        input.readBytes(segmentID, 0, segmentID.length);
                    }
                    else {
                        if (hasID != 0) {
                            throw new CorruptIndexException("invalid hasID byte, got: " + hasID, input);
                        }
                        segmentID = null;
                    }
                }
                else {
                    segmentID = null;
                }
                final Codec codec = readCodec(input, format < 6);
                final SegmentInfo info = codec.segmentInfoFormat().read(directory, segName, segmentID, IOContext.READ);
                info.setCodec(codec);
                totalDocs += info.maxDoc();
                final long delGen = input.readLong();
                final int delCount = input.readInt();
                if (delCount < 0 || delCount > info.maxDoc()) {
                    throw new CorruptIndexException("invalid deletion count: " + delCount + " vs maxDoc=" + info.maxDoc(), input);
                }
                long fieldInfosGen = -1L;
                if (format >= 1) {
                    fieldInfosGen = input.readLong();
                }
                long dvGen = -1L;
                if (format >= 3) {
                    dvGen = input.readLong();
                }
                else {
                    dvGen = fieldInfosGen;
                }
                final SegmentCommitInfo siPerCommit = new SegmentCommitInfo(info, delCount, delGen, fieldInfosGen, dvGen);
                if (format >= 1) {
                    if (format < 3) {
                        final int numGensUpdatesFiles = input.readInt();
                        Map<Long, Set<String>> genUpdatesFiles;
                        if (numGensUpdatesFiles == 0) {
                            genUpdatesFiles = Collections.emptyMap();
                        }
                        else {
                            genUpdatesFiles = new HashMap<Long, Set<String>>(numGensUpdatesFiles);
                            for (int i = 0; i < numGensUpdatesFiles; ++i) {
                                genUpdatesFiles.put(input.readLong(), input.readStringSet());
                            }
                        }
                        siPerCommit.setGenUpdatesFiles(genUpdatesFiles);
                    }
                    else {
                        if (format >= 5) {
                            siPerCommit.setFieldInfosFiles(input.readSetOfStrings());
                        }
                        else {
                            siPerCommit.setFieldInfosFiles(Collections.unmodifiableSet((Set<? extends String>)input.readStringSet()));
                        }
                        final int numDVFields = input.readInt();
                        Map<Integer, Set<String>> dvUpdateFiles;
                        if (numDVFields == 0) {
                            dvUpdateFiles = Collections.emptyMap();
                        }
                        else {
                            final Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>(numDVFields);
                            for (int j = 0; j < numDVFields; ++j) {
                                if (format >= 5) {
                                    map.put(input.readInt(), input.readSetOfStrings());
                                }
                                else {
                                    map.put(input.readInt(), Collections.unmodifiableSet((Set<? extends String>)input.readStringSet()));
                                }
                            }
                            dvUpdateFiles = Collections.unmodifiableMap((Map<? extends Integer, ? extends Set<String>>)map);
                        }
                        siPerCommit.setDocValuesUpdatesFiles(dvUpdateFiles);
                    }
                }
                infos.add(siPerCommit);
                final Version segmentVersion = info.getVersion();
                if (format < 6) {
                    if (infos.minSegmentLuceneVersion == null || !segmentVersion.onOrAfter(infos.minSegmentLuceneVersion)) {
                        infos.minSegmentLuceneVersion = segmentVersion;
                    }
                }
                else if (!segmentVersion.onOrAfter(infos.minSegmentLuceneVersion)) {
                    throw new CorruptIndexException("segments file recorded minSegmentLuceneVersion=" + infos.minSegmentLuceneVersion + " but segment=" + info + " has older version=" + segmentVersion, input);
                }
            }
            if (format >= 5) {
                infos.userData = input.readMapOfStrings();
            }
            else {
                infos.userData = Collections.unmodifiableMap((Map<? extends String, ? extends String>)input.readStringStringMap());
            }
            if (format >= 2) {
                CodecUtil.checkFooter(input);
            }
            else {
                final long checksumNow = input.getChecksum();
                final long checksumThen = input.readLong();
                if (checksumNow != checksumThen) {
                    throw new CorruptIndexException("checksum failed (hardware problem?) : expected=" + Long.toHexString(checksumThen) + " actual=" + Long.toHexString(checksumNow), input);
                }
                CodecUtil.checkEOF(input);
            }
            if (totalDocs > IndexWriter.getActualMaxDocs()) {
                throw new CorruptIndexException("Too many documents: an index cannot exceed " + IndexWriter.getActualMaxDocs() + " but readers have total maxDoc=" + totalDocs, input);
            }
            return infos;
        }
    }
    
    private static Codec readCodec(final DataInput input, final boolean unsupportedAllowed) throws IOException {
        final String name = input.readString();
        try {
            return Codec.forName(name);
        }
        catch (final IllegalArgumentException e) {
            if (SegmentInfos.unsupportedCodecs.contains(name)) {
                assert unsupportedAllowed;
                final IOException newExc = new IndexFormatTooOldException(input, "Codec '" + name + "' is too old");
                newExc.initCause(e);
                throw newExc;
            }
            else {
                if (name.startsWith("Lucene")) {
                    throw new IllegalArgumentException("Could not load codec '" + name + "'.  Did you forget to add lucene-backward-codecs.jar?", e);
                }
                throw e;
            }
        }
    }
    
    public static final SegmentInfos readLatestCommit(final Directory directory) throws IOException {
        return new FindSegmentsFile<SegmentInfos>(directory) {
            @Override
            protected SegmentInfos doBody(final String segmentFileName) throws IOException {
                return SegmentInfos.readCommit(this.directory, segmentFileName);
            }
        }.run();
    }
    
    private void write(final Directory directory) throws IOException {
        final long nextGeneration = this.getNextPendingGeneration();
        final String segmentFileName = IndexFileNames.fileNameFromGeneration("pending_segments", "", nextGeneration);
        this.generation = nextGeneration;
        IndexOutput segnOutput = null;
        boolean success = false;
        try {
            segnOutput = directory.createOutput(segmentFileName, IOContext.DEFAULT);
            CodecUtil.writeIndexHeader(segnOutput, "segments", 6, StringHelper.randomId(), Long.toString(nextGeneration, 36));
            segnOutput.writeVInt(Version.LATEST.major);
            segnOutput.writeVInt(Version.LATEST.minor);
            segnOutput.writeVInt(Version.LATEST.bugfix);
            segnOutput.writeLong(this.version);
            segnOutput.writeInt(this.counter);
            segnOutput.writeInt(this.size());
            if (this.size() > 0) {
                Version minSegmentVersion = null;
                for (final SegmentCommitInfo siPerCommit : this) {
                    final Version segmentVersion = siPerCommit.info.getVersion();
                    if (minSegmentVersion == null || !segmentVersion.onOrAfter(minSegmentVersion)) {
                        minSegmentVersion = segmentVersion;
                    }
                }
                segnOutput.writeVInt(minSegmentVersion.major);
                segnOutput.writeVInt(minSegmentVersion.minor);
                segnOutput.writeVInt(minSegmentVersion.bugfix);
            }
            for (final SegmentCommitInfo siPerCommit2 : this) {
                final SegmentInfo si = siPerCommit2.info;
                segnOutput.writeString(si.name);
                final byte[] segmentID = si.getId();
                if (segmentID == null) {
                    segnOutput.writeByte((byte)0);
                }
                else {
                    if (segmentID.length != 16) {
                        throw new IllegalStateException("cannot write segment: invalid id segment=" + si.name + "id=" + StringHelper.idToString(segmentID));
                    }
                    segnOutput.writeByte((byte)1);
                    segnOutput.writeBytes(segmentID, segmentID.length);
                }
                segnOutput.writeString(si.getCodec().getName());
                segnOutput.writeLong(siPerCommit2.getDelGen());
                final int delCount = siPerCommit2.getDelCount();
                if (delCount < 0 || delCount > si.maxDoc()) {
                    throw new IllegalStateException("cannot write segment: invalid maxDoc segment=" + si.name + " maxDoc=" + si.maxDoc() + " delCount=" + delCount);
                }
                segnOutput.writeInt(delCount);
                segnOutput.writeLong(siPerCommit2.getFieldInfosGen());
                segnOutput.writeLong(siPerCommit2.getDocValuesGen());
                segnOutput.writeSetOfStrings(siPerCommit2.getFieldInfosFiles());
                final Map<Integer, Set<String>> dvUpdatesFiles = siPerCommit2.getDocValuesUpdatesFiles();
                segnOutput.writeInt(dvUpdatesFiles.size());
                for (final Map.Entry<Integer, Set<String>> e : dvUpdatesFiles.entrySet()) {
                    segnOutput.writeInt(e.getKey());
                    segnOutput.writeSetOfStrings(e.getValue());
                }
            }
            segnOutput.writeMapOfStrings(this.userData);
            CodecUtil.writeFooter(segnOutput);
            segnOutput.close();
            directory.sync(Collections.singleton(segmentFileName));
            success = true;
        }
        finally {
            if (success) {
                this.pendingCommit = true;
            }
            else {
                IOUtils.closeWhileHandlingException(segnOutput);
                IOUtils.deleteFilesIgnoringExceptions(directory, segmentFileName);
            }
        }
    }
    
    public SegmentInfos clone() {
        try {
            final SegmentInfos sis = (SegmentInfos)super.clone();
            sis.segments = new ArrayList<SegmentCommitInfo>(this.size());
            for (final SegmentCommitInfo info : this) {
                assert info.info.getCodec() != null;
                sis.add(info.clone());
            }
            sis.userData = new HashMap<String, String>(this.userData);
            return sis;
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException("should not happen", e);
        }
    }
    
    public long getVersion() {
        return this.version;
    }
    
    public long getGeneration() {
        return this.generation;
    }
    
    public long getLastGeneration() {
        return this.lastGeneration;
    }
    
    public static void setInfoStream(final PrintStream infoStream) {
        SegmentInfos.infoStream = infoStream;
    }
    
    public static PrintStream getInfoStream() {
        return SegmentInfos.infoStream;
    }
    
    private static void message(final String message) {
        SegmentInfos.infoStream.println("SIS [" + Thread.currentThread().getName() + "]: " + message);
    }
    
    void updateGeneration(final SegmentInfos other) {
        this.lastGeneration = other.lastGeneration;
        this.generation = other.generation;
    }
    
    void updateGenerationVersionAndCounter(final SegmentInfos other) {
        this.updateGeneration(other);
        this.version = other.version;
        this.counter = other.counter;
    }
    
    void setNextWriteGeneration(final long generation) {
        assert generation >= this.generation;
        this.generation = generation;
    }
    
    final void rollbackCommit(final Directory dir) {
        if (this.pendingCommit) {
            this.pendingCommit = false;
            final String pending = IndexFileNames.fileNameFromGeneration("pending_segments", "", this.generation);
            IOUtils.deleteFilesIgnoringExceptions(dir, pending);
        }
    }
    
    final void prepareCommit(final Directory dir) throws IOException {
        if (this.pendingCommit) {
            throw new IllegalStateException("prepareCommit was already called");
        }
        this.write(dir);
    }
    
    @Deprecated
    public final Collection<String> files(final Directory dir, final boolean includeSegmentsFile) throws IOException {
        return this.files(includeSegmentsFile);
    }
    
    public Collection<String> files(final boolean includeSegmentsFile) throws IOException {
        final HashSet<String> files = new HashSet<String>();
        if (includeSegmentsFile) {
            final String segmentFileName = this.getSegmentsFileName();
            if (segmentFileName != null) {
                files.add(segmentFileName);
            }
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            final SegmentCommitInfo info = this.info(i);
            files.addAll((Collection<?>)info.files());
        }
        return files;
    }
    
    final String finishCommit(final Directory dir) throws IOException {
        if (!this.pendingCommit) {
            throw new IllegalStateException("prepareCommit was not called");
        }
        boolean success = false;
        String dest;
        try {
            final String src = IndexFileNames.fileNameFromGeneration("pending_segments", "", this.generation);
            dest = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
            dir.renameFile(src, dest);
            success = true;
        }
        finally {
            if (!success) {
                this.rollbackCommit(dir);
            }
        }
        this.pendingCommit = false;
        this.lastGeneration = this.generation;
        return dest;
    }
    
    final void commit(final Directory dir) throws IOException {
        this.prepareCommit(dir);
        this.finishCommit(dir);
    }
    
    @Deprecated
    public String toString(final Directory dir) {
        return this.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.getSegmentsFileName()).append(": ");
        for (int count = this.size(), i = 0; i < count; ++i) {
            if (i > 0) {
                buffer.append(' ');
            }
            final SegmentCommitInfo info = this.info(i);
            buffer.append(info.toString(0));
        }
        return buffer.toString();
    }
    
    public Map<String, String> getUserData() {
        return this.userData;
    }
    
    void setUserData(final Map<String, String> data) {
        if (data == null) {
            this.userData = Collections.emptyMap();
        }
        else {
            this.userData = data;
        }
        this.changed();
    }
    
    void replace(final SegmentInfos other) {
        this.rollbackSegmentInfos(other.asList());
        this.lastGeneration = other.lastGeneration;
    }
    
    public int totalMaxDoc() {
        long count = 0L;
        for (final SegmentCommitInfo info : this) {
            count += info.info.maxDoc();
        }
        assert count <= IndexWriter.getActualMaxDocs();
        return (int)count;
    }
    
    public void changed() {
        ++this.version;
    }
    
    void applyMergeChanges(final MergePolicy.OneMerge merge, final boolean dropSegment) {
        final Set<SegmentCommitInfo> mergedAway = new HashSet<SegmentCommitInfo>(merge.segments);
        boolean inserted = false;
        int newSegIdx = 0;
        for (int segIdx = 0, cnt = this.segments.size(); segIdx < cnt; ++segIdx) {
            assert segIdx >= newSegIdx;
            final SegmentCommitInfo info = this.segments.get(segIdx);
            if (mergedAway.contains(info)) {
                if (!inserted && !dropSegment) {
                    this.segments.set(segIdx, merge.info);
                    inserted = true;
                    ++newSegIdx;
                }
            }
            else {
                this.segments.set(newSegIdx, info);
                ++newSegIdx;
            }
        }
        this.segments.subList(newSegIdx, this.segments.size()).clear();
        if (!inserted && !dropSegment) {
            this.segments.add(0, merge.info);
        }
    }
    
    List<SegmentCommitInfo> createBackupSegmentInfos() {
        final List<SegmentCommitInfo> list = new ArrayList<SegmentCommitInfo>(this.size());
        for (final SegmentCommitInfo info : this) {
            assert info.info.getCodec() != null;
            list.add(info.clone());
        }
        return list;
    }
    
    void rollbackSegmentInfos(final List<SegmentCommitInfo> infos) {
        this.clear();
        this.addAll(infos);
    }
    
    @Override
    public Iterator<SegmentCommitInfo> iterator() {
        return this.asList().iterator();
    }
    
    public List<SegmentCommitInfo> asList() {
        return Collections.unmodifiableList((List<? extends SegmentCommitInfo>)this.segments);
    }
    
    public int size() {
        return this.segments.size();
    }
    
    public void add(final SegmentCommitInfo si) {
        this.segments.add(si);
    }
    
    public void addAll(final Iterable<SegmentCommitInfo> sis) {
        for (final SegmentCommitInfo si : sis) {
            this.add(si);
        }
    }
    
    public void clear() {
        this.segments.clear();
    }
    
    public void remove(final SegmentCommitInfo si) {
        this.segments.remove(si);
    }
    
    void remove(final int index) {
        this.segments.remove(index);
    }
    
    boolean contains(final SegmentCommitInfo si) {
        return this.segments.contains(si);
    }
    
    int indexOf(final SegmentCommitInfo si) {
        return this.segments.indexOf(si);
    }
    
    public Version getCommitLuceneVersion() {
        return this.luceneVersion;
    }
    
    public Version getMinSegmentLuceneVersion() {
        return this.minSegmentLuceneVersion;
    }
    
    static {
        SegmentInfos.infoStream = null;
        unsupportedCodecs = Arrays.asList("Lucene3x");
    }
    
    public abstract static class FindSegmentsFile<T>
    {
        final Directory directory;
        
        public FindSegmentsFile(final Directory directory) {
            this.directory = directory;
        }
        
        public T run() throws IOException {
            return this.run(null);
        }
        
        public T run(final IndexCommit commit) throws IOException {
            if (commit == null) {
                long lastGen = -1L;
                long gen = -1L;
                IOException exc = null;
                while (true) {
                    lastGen = gen;
                    final String[] files = this.directory.listAll();
                    final String[] files2 = this.directory.listAll();
                    Arrays.sort(files);
                    Arrays.sort(files2);
                    if (!Arrays.equals(files, files2)) {
                        continue;
                    }
                    gen = SegmentInfos.getLastCommitGeneration(files);
                    if (SegmentInfos.infoStream != null) {
                        message("directory listing gen=" + gen);
                    }
                    if (gen == -1L) {
                        throw new IndexNotFoundException("no segments* file found in " + this.directory + ": files: " + Arrays.toString(files));
                    }
                    if (gen > lastGen) {
                        final String segmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", gen);
                        try {
                            final T t = this.doBody(segmentFileName);
                            if (SegmentInfos.infoStream != null) {
                                message("success on " + segmentFileName);
                            }
                            return t;
                        }
                        catch (final IOException err) {
                            if (exc == null) {
                                exc = err;
                            }
                            if (SegmentInfos.infoStream == null) {
                                continue;
                            }
                            message("primary Exception on '" + segmentFileName + "': " + err + "'; will retry: gen = " + gen);
                            continue;
                        }
                        break;
                    }
                    break;
                }
                throw exc;
            }
            if (this.directory != commit.getDirectory()) {
                throw new IOException("the specified commit does not match the specified Directory");
            }
            return this.doBody(commit.getSegmentsFileName());
        }
        
        protected abstract T doBody(final String p0) throws IOException;
    }
}
