package org.apache.lucene.index;

import org.apache.lucene.store.IndexInput;
import java.util.List;
import org.apache.lucene.store.DataInput;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.store.IndexOutput;
import java.util.Collection;
import java.util.Collections;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.util.Map;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.IOContext;
import java.io.IOException;
import org.apache.lucene.store.Directory;

public class PersistentSnapshotDeletionPolicy extends SnapshotDeletionPolicy
{
    public static final String SNAPSHOTS_PREFIX = "snapshots_";
    private static final int VERSION_START = 0;
    private static final int VERSION_CURRENT = 0;
    private static final String CODEC_NAME = "snapshots";
    private long nextWriteGen;
    private final Directory dir;
    
    public PersistentSnapshotDeletionPolicy(final IndexDeletionPolicy primary, final Directory dir) throws IOException {
        this(primary, dir, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }
    
    public PersistentSnapshotDeletionPolicy(final IndexDeletionPolicy primary, final Directory dir, final IndexWriterConfig.OpenMode mode) throws IOException {
        super(primary);
        this.dir = dir;
        if (mode == IndexWriterConfig.OpenMode.CREATE) {
            this.clearPriorSnapshots();
        }
        this.loadPriorSnapshots();
        if (mode == IndexWriterConfig.OpenMode.APPEND && this.nextWriteGen == 0L) {
            throw new IllegalStateException("no snapshots stored in this directory");
        }
    }
    
    @Override
    public synchronized IndexCommit snapshot() throws IOException {
        final IndexCommit ic = super.snapshot();
        boolean success = false;
        try {
            this.persist();
            success = true;
        }
        finally {
            if (!success) {
                try {
                    super.release(ic);
                }
                catch (final Exception ex) {}
            }
        }
        return ic;
    }
    
    @Override
    public synchronized void release(final IndexCommit commit) throws IOException {
        super.release(commit);
        boolean success = false;
        try {
            this.persist();
            success = true;
        }
        finally {
            if (!success) {
                try {
                    this.incRef(commit);
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    public synchronized void release(final long gen) throws IOException {
        super.releaseGen(gen);
        this.persist();
    }
    
    private synchronized void persist() throws IOException {
        final String fileName = "snapshots_" + this.nextWriteGen;
        final IndexOutput out = this.dir.createOutput(fileName, IOContext.DEFAULT);
        boolean success = false;
        try {
            CodecUtil.writeHeader(out, "snapshots", 0);
            out.writeVInt(this.refCounts.size());
            for (final Map.Entry<Long, Integer> ent : this.refCounts.entrySet()) {
                out.writeVLong(ent.getKey());
                out.writeVInt(ent.getValue());
            }
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(out);
                IOUtils.deleteFilesIgnoringExceptions(this.dir, fileName);
            }
            else {
                IOUtils.close(out);
            }
        }
        this.dir.sync(Collections.singletonList(fileName));
        if (this.nextWriteGen > 0L) {
            final String lastSaveFile = "snapshots_" + (this.nextWriteGen - 1L);
            IOUtils.deleteFilesIgnoringExceptions(this.dir, lastSaveFile);
        }
        ++this.nextWriteGen;
    }
    
    private synchronized void clearPriorSnapshots() throws IOException {
        for (final String file : this.dir.listAll()) {
            if (file.startsWith("snapshots_")) {
                this.dir.deleteFile(file);
            }
        }
    }
    
    public String getLastSaveFile() {
        if (this.nextWriteGen == 0L) {
            return null;
        }
        return "snapshots_" + (this.nextWriteGen - 1L);
    }
    
    private synchronized void loadPriorSnapshots() throws IOException {
        long genLoaded = -1L;
        IOException ioe = null;
        final List<String> snapshotFiles = new ArrayList<String>();
        for (final String file : this.dir.listAll()) {
            if (file.startsWith("snapshots_")) {
                final long gen = Long.parseLong(file.substring("snapshots_".length()));
                if (genLoaded == -1L || gen > genLoaded) {
                    snapshotFiles.add(file);
                    final Map<Long, Integer> m = new HashMap<Long, Integer>();
                    final IndexInput in = this.dir.openInput(file, IOContext.DEFAULT);
                    try {
                        CodecUtil.checkHeader(in, "snapshots", 0, 0);
                        for (int count = in.readVInt(), i = 0; i < count; ++i) {
                            final long commitGen = in.readVLong();
                            final int refCount = in.readVInt();
                            m.put(commitGen, refCount);
                        }
                    }
                    catch (final IOException ioe2) {
                        if (ioe == null) {
                            ioe = ioe2;
                        }
                    }
                    finally {
                        in.close();
                    }
                    genLoaded = gen;
                    this.refCounts.clear();
                    this.refCounts.putAll(m);
                }
            }
        }
        if (genLoaded == -1L) {
            if (ioe != null) {
                throw ioe;
            }
        }
        else {
            if (snapshotFiles.size() > 1) {
                final String curFileName = "snapshots_" + genLoaded;
                for (final String file2 : snapshotFiles) {
                    if (!curFileName.equals(file2)) {
                        IOUtils.deleteFilesIgnoringExceptions(this.dir, file2);
                    }
                }
            }
            this.nextWriteGen = 1L + genLoaded;
        }
    }
}
