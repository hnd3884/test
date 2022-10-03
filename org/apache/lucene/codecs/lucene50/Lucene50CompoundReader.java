package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.store.Lock;
import java.util.Collection;
import org.apache.lucene.store.IndexOutput;
import java.io.FileNotFoundException;
import org.apache.lucene.store.ChecksumIndexInput;
import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.IndexInput;
import java.util.Map;
import org.apache.lucene.store.Directory;

final class Lucene50CompoundReader extends Directory
{
    private final Directory directory;
    private final String segmentName;
    private final Map<String, FileEntry> entries;
    private final IndexInput handle;
    private int version;
    
    public Lucene50CompoundReader(final Directory directory, final SegmentInfo si, final IOContext context) throws IOException {
        this.directory = directory;
        this.segmentName = si.name;
        final String dataFileName = IndexFileNames.segmentFileName(this.segmentName, "", "cfs");
        final String entriesFileName = IndexFileNames.segmentFileName(this.segmentName, "", "cfe");
        this.entries = this.readEntries(si.getId(), directory, entriesFileName);
        boolean success = false;
        long expectedLength = CodecUtil.indexHeaderLength("Lucene50CompoundData", "");
        for (final Map.Entry<String, FileEntry> ent : this.entries.entrySet()) {
            expectedLength += ent.getValue().length;
        }
        expectedLength += CodecUtil.footerLength();
        this.handle = directory.openInput(dataFileName, context);
        try {
            CodecUtil.checkIndexHeader(this.handle, "Lucene50CompoundData", this.version, this.version, si.getId(), "");
            CodecUtil.retrieveChecksum(this.handle);
            if (this.handle.length() != expectedLength) {
                throw new CorruptIndexException("length should be " + expectedLength + " bytes, but is " + this.handle.length() + " instead", this.handle);
            }
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.handle);
            }
        }
    }
    
    private final Map<String, FileEntry> readEntries(final byte[] segmentID, final Directory dir, final String entriesFileName) throws IOException {
        Map<String, FileEntry> mapping = null;
        try (final ChecksumIndexInput entriesStream = dir.openChecksumInput(entriesFileName, IOContext.READONCE)) {
            Throwable priorE = null;
            try {
                this.version = CodecUtil.checkIndexHeader(entriesStream, "Lucene50CompoundEntries", 0, 0, segmentID, "");
                final int numEntries = entriesStream.readVInt();
                mapping = new HashMap<String, FileEntry>(numEntries);
                for (int i = 0; i < numEntries; ++i) {
                    final FileEntry fileEntry = new FileEntry();
                    final String id = entriesStream.readString();
                    final FileEntry previous = mapping.put(id, fileEntry);
                    if (previous != null) {
                        throw new CorruptIndexException("Duplicate cfs entry id=" + id + " in CFS ", entriesStream);
                    }
                    fileEntry.offset = entriesStream.readLong();
                    fileEntry.length = entriesStream.readLong();
                }
            }
            catch (final Throwable exception) {
                priorE = exception;
            }
            finally {
                CodecUtil.checkFooter(entriesStream, priorE);
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends FileEntry>)mapping);
    }
    
    @Override
    public void close() throws IOException {
        IOUtils.close(this.handle);
    }
    
    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final String id = IndexFileNames.stripSegmentName(name);
        final FileEntry entry = this.entries.get(id);
        if (entry == null) {
            throw new FileNotFoundException("No sub-file with id " + id + " found (fileName=" + name + " files: " + this.entries.keySet() + ")");
        }
        return this.handle.slice(name, entry.offset, entry.length);
    }
    
    @Override
    public String[] listAll() {
        this.ensureOpen();
        final String[] res = this.entries.keySet().toArray(new String[this.entries.size()]);
        for (int i = 0; i < res.length; ++i) {
            res[i] = this.segmentName + res[i];
        }
        return res;
    }
    
    @Override
    public void deleteFile(final String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void renameFile(final String from, final String to) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long fileLength(final String name) throws IOException {
        this.ensureOpen();
        final FileEntry e = this.entries.get(IndexFileNames.stripSegmentName(name));
        if (e == null) {
            throw new FileNotFoundException(name);
        }
        return e.length;
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sync(final Collection<String> names) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Lock obtainLock(final String name) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return "CompoundFileDirectory(segment=\"" + this.segmentName + "\" in dir=" + this.directory + ")";
    }
    
    public static final class FileEntry
    {
        long offset;
        long length;
    }
}
