package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import java.util.Collection;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class SegmentCommitInfo
{
    public final SegmentInfo info;
    private int delCount;
    private long delGen;
    private long nextWriteDelGen;
    private long fieldInfosGen;
    private long nextWriteFieldInfosGen;
    private long docValuesGen;
    private long nextWriteDocValuesGen;
    private final Map<Integer, Set<String>> dvUpdatesFiles;
    private final Set<String> fieldInfosFiles;
    @Deprecated
    private final Map<Long, Set<String>> genUpdatesFiles;
    private volatile long sizeInBytes;
    private long bufferedDeletesGen;
    
    public SegmentCommitInfo(final SegmentInfo info, final int delCount, final long delGen, final long fieldInfosGen, final long docValuesGen) {
        this.dvUpdatesFiles = new HashMap<Integer, Set<String>>();
        this.fieldInfosFiles = new HashSet<String>();
        this.genUpdatesFiles = new HashMap<Long, Set<String>>();
        this.sizeInBytes = -1L;
        this.info = info;
        this.delCount = delCount;
        this.delGen = delGen;
        this.nextWriteDelGen = ((delGen == -1L) ? 1L : (delGen + 1L));
        this.fieldInfosGen = fieldInfosGen;
        this.nextWriteFieldInfosGen = ((fieldInfosGen == -1L) ? 1L : (fieldInfosGen + 1L));
        this.docValuesGen = docValuesGen;
        this.nextWriteDocValuesGen = ((docValuesGen == -1L) ? 1L : (docValuesGen + 1L));
    }
    
    @Deprecated
    public void setGenUpdatesFiles(final Map<Long, Set<String>> genUpdatesFiles) {
        this.genUpdatesFiles.clear();
        for (final Map.Entry<Long, Set<String>> kv : genUpdatesFiles.entrySet()) {
            final Set<String> set = new HashSet<String>();
            for (final String file : kv.getValue()) {
                set.add(this.info.namedForThisSegment(file));
            }
            this.genUpdatesFiles.put(kv.getKey(), set);
        }
    }
    
    public Map<Integer, Set<String>> getDocValuesUpdatesFiles() {
        return Collections.unmodifiableMap((Map<? extends Integer, ? extends Set<String>>)this.dvUpdatesFiles);
    }
    
    public void setDocValuesUpdatesFiles(final Map<Integer, Set<String>> dvUpdatesFiles) {
        this.dvUpdatesFiles.clear();
        for (final Map.Entry<Integer, Set<String>> kv : dvUpdatesFiles.entrySet()) {
            final Set<String> set = new HashSet<String>();
            for (final String file : kv.getValue()) {
                set.add(this.info.namedForThisSegment(file));
            }
            this.dvUpdatesFiles.put(kv.getKey(), set);
        }
    }
    
    public Set<String> getFieldInfosFiles() {
        return Collections.unmodifiableSet((Set<? extends String>)this.fieldInfosFiles);
    }
    
    public void setFieldInfosFiles(final Set<String> fieldInfosFiles) {
        this.fieldInfosFiles.clear();
        for (final String file : fieldInfosFiles) {
            this.fieldInfosFiles.add(this.info.namedForThisSegment(file));
        }
    }
    
    void advanceDelGen() {
        this.delGen = this.nextWriteDelGen;
        this.nextWriteDelGen = this.delGen + 1L;
        this.sizeInBytes = -1L;
    }
    
    void advanceNextWriteDelGen() {
        ++this.nextWriteDelGen;
    }
    
    long getNextWriteDelGen() {
        return this.nextWriteDelGen;
    }
    
    void setNextWriteDelGen(final long v) {
        this.nextWriteDelGen = v;
    }
    
    void advanceFieldInfosGen() {
        this.fieldInfosGen = this.nextWriteFieldInfosGen;
        this.nextWriteFieldInfosGen = this.fieldInfosGen + 1L;
        this.sizeInBytes = -1L;
    }
    
    void advanceNextWriteFieldInfosGen() {
        ++this.nextWriteFieldInfosGen;
    }
    
    long getNextWriteFieldInfosGen() {
        return this.nextWriteFieldInfosGen;
    }
    
    void setNextWriteFieldInfosGen(final long v) {
        this.nextWriteFieldInfosGen = v;
    }
    
    void advanceDocValuesGen() {
        this.docValuesGen = this.nextWriteDocValuesGen;
        this.nextWriteDocValuesGen = this.docValuesGen + 1L;
        this.sizeInBytes = -1L;
    }
    
    void advanceNextWriteDocValuesGen() {
        ++this.nextWriteDocValuesGen;
    }
    
    long getNextWriteDocValuesGen() {
        return this.nextWriteDocValuesGen;
    }
    
    void setNextWriteDocValuesGen(final long v) {
        this.nextWriteDocValuesGen = v;
    }
    
    public long sizeInBytes() throws IOException {
        if (this.sizeInBytes == -1L) {
            long sum = 0L;
            for (final String fileName : this.files()) {
                sum += this.info.dir.fileLength(fileName);
            }
            this.sizeInBytes = sum;
        }
        return this.sizeInBytes;
    }
    
    public Collection<String> files() throws IOException {
        final Collection<String> files = new HashSet<String>(this.info.files());
        this.info.getCodec().liveDocsFormat().files(this, files);
        for (final Set<String> updateFiles : this.genUpdatesFiles.values()) {
            files.addAll(updateFiles);
        }
        for (final Set<String> updatefiles : this.dvUpdatesFiles.values()) {
            files.addAll(updatefiles);
        }
        files.addAll(this.fieldInfosFiles);
        return files;
    }
    
    long getBufferedDeletesGen() {
        return this.bufferedDeletesGen;
    }
    
    void setBufferedDeletesGen(final long v) {
        this.bufferedDeletesGen = v;
        this.sizeInBytes = -1L;
    }
    
    public boolean hasDeletions() {
        return this.delGen != -1L;
    }
    
    public boolean hasFieldUpdates() {
        return this.fieldInfosGen != -1L;
    }
    
    public long getNextFieldInfosGen() {
        return this.nextWriteFieldInfosGen;
    }
    
    public long getFieldInfosGen() {
        return this.fieldInfosGen;
    }
    
    public long getNextDocValuesGen() {
        return this.nextWriteDocValuesGen;
    }
    
    public long getDocValuesGen() {
        return this.docValuesGen;
    }
    
    public long getNextDelGen() {
        return this.nextWriteDelGen;
    }
    
    public long getDelGen() {
        return this.delGen;
    }
    
    public int getDelCount() {
        return this.delCount;
    }
    
    void setDelCount(final int delCount) {
        if (delCount < 0 || delCount > this.info.maxDoc()) {
            throw new IllegalArgumentException("invalid delCount=" + delCount + " (maxDoc=" + this.info.maxDoc() + ")");
        }
        this.delCount = delCount;
    }
    
    @Deprecated
    public String toString(final Directory dir, final int pendingDelCount) {
        return this.toString(pendingDelCount);
    }
    
    public String toString(final int pendingDelCount) {
        String s = this.info.toString(this.delCount + pendingDelCount);
        if (this.delGen != -1L) {
            s = s + ":delGen=" + this.delGen;
        }
        if (this.fieldInfosGen != -1L) {
            s = s + ":fieldInfosGen=" + this.fieldInfosGen;
        }
        if (this.docValuesGen != -1L) {
            s = s + ":dvGen=" + this.docValuesGen;
        }
        return s;
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    public SegmentCommitInfo clone() {
        final SegmentCommitInfo other = new SegmentCommitInfo(this.info, this.delCount, this.delGen, this.fieldInfosGen, this.docValuesGen);
        other.nextWriteDelGen = this.nextWriteDelGen;
        other.nextWriteFieldInfosGen = this.nextWriteFieldInfosGen;
        other.nextWriteDocValuesGen = this.nextWriteDocValuesGen;
        for (final Map.Entry<Long, Set<String>> e : this.genUpdatesFiles.entrySet()) {
            other.genUpdatesFiles.put(e.getKey(), new HashSet<String>(e.getValue()));
        }
        for (final Map.Entry<Integer, Set<String>> e2 : this.dvUpdatesFiles.entrySet()) {
            other.dvUpdatesFiles.put(e2.getKey(), new HashSet<String>(e2.getValue()));
        }
        other.fieldInfosFiles.addAll(this.fieldInfosFiles);
        return other;
    }
}
