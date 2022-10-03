package org.apache.lucene.util;

import java.io.EOFException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.nio.file.OpenOption;
import java.io.DataOutput;
import java.util.Locale;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Comparator;
import java.nio.file.Path;

public final class OfflineSorter
{
    private static Path DEFAULT_TEMP_DIR;
    public static final long MB = 1048576L;
    public static final long GB = 1073741824L;
    public static final long MIN_BUFFER_SIZE_MB = 32L;
    public static final long ABSOLUTE_MIN_SORT_BUFFER_SIZE = 524288L;
    private static final String MIN_BUFFER_SIZE_MSG = "At least 0.5MB RAM buffer is needed";
    public static final int MAX_TEMPFILES = 128;
    private final BufferSize ramBufferSize;
    private final Path tempDirectory;
    private final Counter bufferBytesUsed;
    private final BytesRefArray buffer;
    private SortInfo sortInfo;
    private int maxTempFiles;
    private final Comparator<BytesRef> comparator;
    public static final Comparator<BytesRef> DEFAULT_COMPARATOR;
    
    public OfflineSorter() throws IOException {
        this(OfflineSorter.DEFAULT_COMPARATOR, BufferSize.automatic(), getDefaultTempDir(), 128);
    }
    
    public OfflineSorter(final Comparator<BytesRef> comparator) throws IOException {
        this(comparator, BufferSize.automatic(), getDefaultTempDir(), 128);
    }
    
    public OfflineSorter(final Comparator<BytesRef> comparator, final BufferSize ramBufferSize, final Path tempDirectory, final int maxTempfiles) {
        this.bufferBytesUsed = Counter.newCounter();
        this.buffer = new BytesRefArray(this.bufferBytesUsed);
        if (ramBufferSize.bytes < 524288L) {
            throw new IllegalArgumentException("At least 0.5MB RAM buffer is needed: " + ramBufferSize.bytes);
        }
        if (maxTempfiles < 2) {
            throw new IllegalArgumentException("maxTempFiles must be >= 2");
        }
        this.ramBufferSize = ramBufferSize;
        this.tempDirectory = tempDirectory;
        this.maxTempFiles = maxTempfiles;
        this.comparator = comparator;
    }
    
    public SortInfo sort(final Path input, final Path output) throws IOException {
        this.sortInfo = new SortInfo();
        this.sortInfo.totalTime = System.currentTimeMillis();
        final ArrayList<Path> merges = new ArrayList<Path>();
        boolean success3 = false;
        try {
            final ByteSequencesReader is = new ByteSequencesReader(input);
            boolean success4 = false;
            try {
                int lines = 0;
                while ((lines = this.readPartition(is)) > 0) {
                    merges.add(this.sortPartition(lines));
                    final SortInfo sortInfo = this.sortInfo;
                    ++sortInfo.tempMergeFiles;
                    final SortInfo sortInfo2 = this.sortInfo;
                    sortInfo2.lines += lines;
                    if (merges.size() == this.maxTempFiles) {
                        final Path intermediate = Files.createTempFile(this.tempDirectory, "sort", "intermediate", (FileAttribute<?>[])new FileAttribute[0]);
                        boolean success5 = false;
                        try {
                            this.mergePartitions(merges, intermediate);
                            success5 = true;
                        }
                        finally {
                            if (success5) {
                                IOUtils.deleteFilesIfExist(merges);
                            }
                            else {
                                IOUtils.deleteFilesIgnoringExceptions(merges);
                            }
                            merges.clear();
                            merges.add(intermediate);
                        }
                        final SortInfo sortInfo3 = this.sortInfo;
                        ++sortInfo3.tempMergeFiles;
                    }
                }
                success4 = true;
            }
            finally {
                if (success4) {
                    IOUtils.close(is);
                }
                else {
                    IOUtils.closeWhileHandlingException(is);
                }
            }
            if (merges.size() == 1) {
                Files.move(merges.get(0), output, StandardCopyOption.REPLACE_EXISTING);
            }
            else {
                this.mergePartitions(merges, output);
            }
            success3 = true;
        }
        finally {
            if (success3) {
                IOUtils.deleteFilesIfExist(merges);
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(merges);
                IOUtils.deleteFilesIgnoringExceptions(output);
            }
        }
        this.sortInfo.totalTime = System.currentTimeMillis() - this.sortInfo.totalTime;
        return this.sortInfo;
    }
    
    static void setDefaultTempDir(final Path tempDir) {
        OfflineSorter.DEFAULT_TEMP_DIR = tempDir;
    }
    
    public static synchronized Path getDefaultTempDir() throws IOException {
        if (OfflineSorter.DEFAULT_TEMP_DIR == null) {
            final String tempDirPath = System.getProperty("java.io.tmpdir");
            if (tempDirPath == null) {
                throw new IOException("Java has no temporary folder property (java.io.tmpdir)?");
            }
            final Path tempDirectory = Paths.get(tempDirPath, new String[0]);
            if (!Files.isWritable(tempDirectory)) {
                throw new IOException("Java's temporary folder not present or writeable?: " + tempDirectory.toAbsolutePath());
            }
            OfflineSorter.DEFAULT_TEMP_DIR = tempDirectory;
        }
        return OfflineSorter.DEFAULT_TEMP_DIR;
    }
    
    protected Path sortPartition(final int len) throws IOException {
        final BytesRefArray data = this.buffer;
        final Path tempFile = Files.createTempFile(this.tempDirectory, "sort", "partition", (FileAttribute<?>[])new FileAttribute[0]);
        final long start = System.currentTimeMillis();
        final SortInfo sortInfo = this.sortInfo;
        sortInfo.sortTime += System.currentTimeMillis() - start;
        final ByteSequencesWriter out = new ByteSequencesWriter(tempFile);
        try {
            final BytesRefIterator iter = this.buffer.iterator(this.comparator);
            BytesRef spare;
            while ((spare = iter.next()) != null) {
                assert spare.length <= 32767;
                out.write(spare);
            }
            out.close();
            data.clear();
            return tempFile;
        }
        finally {
            IOUtils.close(out);
        }
    }
    
    void mergePartitions(final List<Path> merges, final Path outputFile) throws IOException {
        final long start = System.currentTimeMillis();
        final ByteSequencesWriter out = new ByteSequencesWriter(outputFile);
        final PriorityQueue<FileAndTop> queue = new PriorityQueue<FileAndTop>(merges.size()) {
            @Override
            protected boolean lessThan(final FileAndTop a, final FileAndTop b) {
                return OfflineSorter.this.comparator.compare(a.current.get(), b.current.get()) < 0;
            }
        };
        final ByteSequencesReader[] streams = new ByteSequencesReader[merges.size()];
        try {
            for (int i = 0; i < merges.size(); ++i) {
                streams[i] = new ByteSequencesReader(merges.get(i));
                final byte[] line = streams[i].read();
                if (line != null) {
                    queue.insertWithOverflow(new FileAndTop(i, line));
                }
            }
            FileAndTop top;
            while ((top = queue.top()) != null) {
                out.write(top.current.bytes(), 0, top.current.length());
                if (!streams[top.fd].read(top.current)) {
                    queue.pop();
                }
                else {
                    queue.updateTop();
                }
            }
            final SortInfo sortInfo = this.sortInfo;
            sortInfo.mergeTime += System.currentTimeMillis() - start;
            final SortInfo sortInfo2 = this.sortInfo;
            ++sortInfo2.mergeRounds;
        }
        finally {
            try {
                IOUtils.close((Closeable[])streams);
            }
            finally {
                IOUtils.close(out);
            }
        }
    }
    
    int readPartition(final ByteSequencesReader reader) throws IOException {
        final long start = System.currentTimeMillis();
        final BytesRef scratch = new BytesRef();
        while ((scratch.bytes = reader.read()) != null) {
            scratch.length = scratch.bytes.length;
            this.buffer.append(scratch);
            if (this.ramBufferSize.bytes < this.bufferBytesUsed.get()) {
                break;
            }
        }
        final SortInfo sortInfo = this.sortInfo;
        sortInfo.readTime += System.currentTimeMillis() - start;
        return this.buffer.size();
    }
    
    public Comparator<BytesRef> getComparator() {
        return this.comparator;
    }
    
    static {
        DEFAULT_COMPARATOR = BytesRef.getUTF8SortedAsUnicodeComparator();
    }
    
    public static final class BufferSize
    {
        final int bytes;
        
        private BufferSize(final long bytes) {
            if (bytes > 2147483647L) {
                throw new IllegalArgumentException("Buffer too large for Java (2047mb max): " + bytes);
            }
            if (bytes < 524288L) {
                throw new IllegalArgumentException("At least 0.5MB RAM buffer is needed: " + bytes);
            }
            this.bytes = (int)bytes;
        }
        
        public static BufferSize megabytes(final long mb) {
            return new BufferSize(mb * 1048576L);
        }
        
        public static BufferSize automatic() {
            final Runtime rt = Runtime.getRuntime();
            final long max = rt.maxMemory();
            final long total = rt.totalMemory();
            final long free = rt.freeMemory();
            final long totalAvailableBytes = max - total + free;
            long sortBufferByteSize = free / 2L;
            final long minBufferSizeBytes = 33554432L;
            if (sortBufferByteSize < 33554432L || totalAvailableBytes > 335544320L) {
                if (totalAvailableBytes / 2L > 33554432L) {
                    sortBufferByteSize = totalAvailableBytes / 2L;
                }
                else {
                    sortBufferByteSize = Math.max(524288L, sortBufferByteSize);
                }
            }
            return new BufferSize(Math.min(2147483647L, sortBufferByteSize));
        }
    }
    
    public class SortInfo
    {
        public int tempMergeFiles;
        public int mergeRounds;
        public int lines;
        public long mergeTime;
        public long sortTime;
        public long totalTime;
        public long readTime;
        public final long bufferSize;
        
        public SortInfo() {
            this.bufferSize = OfflineSorter.this.ramBufferSize.bytes;
        }
        
        @Override
        public String toString() {
            return String.format(Locale.ROOT, "time=%.2f sec. total (%.2f reading, %.2f sorting, %.2f merging), lines=%d, temp files=%d, merges=%d, soft ram limit=%.2f MB", this.totalTime / 1000.0, this.readTime / 1000.0, this.sortTime / 1000.0, this.mergeTime / 1000.0, this.lines, this.tempMergeFiles, this.mergeRounds, this.bufferSize / 1048576.0);
        }
    }
    
    static class FileAndTop
    {
        final int fd;
        final BytesRefBuilder current;
        
        FileAndTop(final int fd, final byte[] firstLine) {
            this.fd = fd;
            (this.current = new BytesRefBuilder()).copyBytes(firstLine, 0, firstLine.length);
        }
    }
    
    public static class ByteSequencesWriter implements Closeable
    {
        private final DataOutput os;
        
        public ByteSequencesWriter(final Path path) throws IOException {
            this(new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path, new OpenOption[0]))));
        }
        
        public ByteSequencesWriter(final DataOutput os) {
            this.os = os;
        }
        
        public void write(final BytesRef ref) throws IOException {
            assert ref != null;
            this.write(ref.bytes, ref.offset, ref.length);
        }
        
        public void write(final byte[] bytes) throws IOException {
            this.write(bytes, 0, bytes.length);
        }
        
        public void write(final byte[] bytes, final int off, final int len) throws IOException {
            assert bytes != null;
            assert off >= 0 && off + len <= bytes.length;
            assert len >= 0;
            if (len > 32767) {
                throw new IllegalArgumentException("len must be <= 32767; got " + len);
            }
            this.os.writeShort(len);
            this.os.write(bytes, off, len);
        }
        
        @Override
        public void close() throws IOException {
            if (this.os instanceof Closeable) {
                ((Closeable)this.os).close();
            }
        }
    }
    
    public static class ByteSequencesReader implements Closeable
    {
        private final DataInput is;
        
        public ByteSequencesReader(final Path path) throws IOException {
            this(new DataInputStream(new BufferedInputStream(Files.newInputStream(path, new OpenOption[0]))));
        }
        
        public ByteSequencesReader(final DataInput is) {
            this.is = is;
        }
        
        public boolean read(final BytesRefBuilder ref) throws IOException {
            short length;
            try {
                length = this.is.readShort();
            }
            catch (final EOFException e) {
                return false;
            }
            ref.grow(length);
            ref.setLength(length);
            this.is.readFully(ref.bytes(), 0, length);
            return true;
        }
        
        public byte[] read() throws IOException {
            short length;
            try {
                length = this.is.readShort();
            }
            catch (final EOFException e) {
                return null;
            }
            assert length >= 0 : "Sanity: sequence length < 0: " + length;
            final byte[] result = new byte[length];
            this.is.readFully(result);
            return result;
        }
        
        @Override
        public void close() throws IOException {
            if (this.is instanceof Closeable) {
                ((Closeable)this.is).close();
            }
        }
    }
}
