package org.apache.lucene.index;

import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import org.apache.lucene.store.TrackingDirectoryWrapper;
import java.util.Objects;
import java.util.Set;
import org.apache.lucene.util.Version;
import java.util.Map;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.store.Directory;

public final class SegmentInfo
{
    public static final int NO = -1;
    public static final int YES = 1;
    public final String name;
    private int maxDoc;
    public final Directory dir;
    private boolean isCompoundFile;
    private final byte[] id;
    private Codec codec;
    private Map<String, String> diagnostics;
    private final Map<String, String> attributes;
    private Version version;
    private Set<String> setFiles;
    
    void setDiagnostics(final Map<String, String> diagnostics) {
        this.diagnostics = Objects.requireNonNull(diagnostics);
    }
    
    public Map<String, String> getDiagnostics() {
        return this.diagnostics;
    }
    
    public SegmentInfo(final Directory dir, final Version version, final String name, final int maxDoc, final boolean isCompoundFile, final Codec codec, final Map<String, String> diagnostics, final byte[] id, final Map<String, String> attributes) {
        assert !(dir instanceof TrackingDirectoryWrapper);
        this.dir = Objects.requireNonNull(dir);
        this.version = Objects.requireNonNull(version);
        this.name = Objects.requireNonNull(name);
        this.maxDoc = maxDoc;
        this.isCompoundFile = isCompoundFile;
        this.codec = codec;
        this.diagnostics = Objects.requireNonNull(diagnostics);
        if ((this.id = id) != null && id.length != 16) {
            throw new IllegalArgumentException("invalid id: " + Arrays.toString(id));
        }
        this.attributes = Objects.requireNonNull(attributes);
    }
    
    void setUseCompoundFile(final boolean isCompoundFile) {
        this.isCompoundFile = isCompoundFile;
    }
    
    public boolean getUseCompoundFile() {
        return this.isCompoundFile;
    }
    
    public void setCodec(final Codec codec) {
        assert this.codec == null;
        if (codec == null) {
            throw new IllegalArgumentException("codec must be non-null");
        }
        this.codec = codec;
    }
    
    public Codec getCodec() {
        return this.codec;
    }
    
    public int maxDoc() {
        if (this.maxDoc == -1) {
            throw new IllegalStateException("maxDoc isn't set yet");
        }
        return this.maxDoc;
    }
    
    void setMaxDoc(final int maxDoc) {
        if (this.maxDoc != -1) {
            throw new IllegalStateException("maxDoc was already set: this.maxDoc=" + this.maxDoc + " vs maxDoc=" + maxDoc);
        }
        this.maxDoc = maxDoc;
    }
    
    public Set<String> files() {
        if (this.setFiles == null) {
            throw new IllegalStateException("files were not computed yet");
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.setFiles);
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    @Deprecated
    public String toString(final Directory dir, final int delCount) {
        return this.toString(delCount);
    }
    
    public String toString(final int delCount) {
        final StringBuilder s = new StringBuilder();
        s.append(this.name).append('(').append((this.version == null) ? "?" : this.version).append(')').append(':');
        final char cfs = this.getUseCompoundFile() ? 'c' : 'C';
        s.append(cfs);
        s.append(this.maxDoc);
        if (delCount != 0) {
            s.append('/').append(delCount);
        }
        final String sorter_key = "sorter";
        final String sorter_val = this.diagnostics.get("sorter");
        if (sorter_val != null) {
            s.append(":[");
            s.append("sorter");
            s.append('=');
            s.append(sorter_val);
            s.append(']');
        }
        return s.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SegmentInfo) {
            final SegmentInfo other = (SegmentInfo)obj;
            return other.dir == this.dir && other.name.equals(this.name);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.dir.hashCode() + this.name.hashCode();
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    public byte[] getId() {
        return (byte[])((this.id == null) ? null : ((byte[])this.id.clone()));
    }
    
    public void setFiles(final Collection<String> files) {
        this.setFiles = new HashSet<String>();
        this.addFiles(files);
    }
    
    public void addFiles(final Collection<String> files) {
        this.checkFileNames(files);
        for (final String f : files) {
            this.setFiles.add(this.namedForThisSegment(f));
        }
    }
    
    public void addFile(final String file) {
        this.checkFileNames(Collections.singleton(file));
        this.setFiles.add(this.namedForThisSegment(file));
    }
    
    private void checkFileNames(final Collection<String> files) {
        final Matcher m = IndexFileNames.CODEC_FILE_PATTERN.matcher("");
        for (final String file : files) {
            m.reset(file);
            if (!m.matches()) {
                throw new IllegalArgumentException("invalid codec filename '" + file + "', must match: " + IndexFileNames.CODEC_FILE_PATTERN.pattern());
            }
        }
    }
    
    String namedForThisSegment(final String file) {
        return this.name + IndexFileNames.stripSegmentName(file);
    }
    
    public String getAttribute(final String key) {
        return this.attributes.get(key);
    }
    
    public String putAttribute(final String key, final String value) {
        return this.attributes.put(key, value);
    }
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }
}
