package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.codecs.compressing.CompressingStoredFieldsFormat;
import org.apache.lucene.codecs.compressing.CompressionMode;
import org.apache.lucene.codecs.StoredFieldsWriter;
import java.io.IOException;
import org.apache.lucene.codecs.StoredFieldsReader;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import java.util.Objects;
import org.apache.lucene.codecs.StoredFieldsFormat;

public final class Lucene50StoredFieldsFormat extends StoredFieldsFormat
{
    public static final String MODE_KEY;
    final Mode mode;
    
    public Lucene50StoredFieldsFormat() {
        this(Mode.BEST_SPEED);
    }
    
    public Lucene50StoredFieldsFormat(final Mode mode) {
        this.mode = Objects.requireNonNull(mode);
    }
    
    @Override
    public StoredFieldsReader fieldsReader(final Directory directory, final SegmentInfo si, final FieldInfos fn, final IOContext context) throws IOException {
        final String value = si.getAttribute(Lucene50StoredFieldsFormat.MODE_KEY);
        if (value == null) {
            throw new IllegalStateException("missing value for " + Lucene50StoredFieldsFormat.MODE_KEY + " for segment: " + si.name);
        }
        final Mode mode = Mode.valueOf(value);
        return this.impl(mode).fieldsReader(directory, si, fn, context);
    }
    
    @Override
    public StoredFieldsWriter fieldsWriter(final Directory directory, final SegmentInfo si, final IOContext context) throws IOException {
        final String previous = si.putAttribute(Lucene50StoredFieldsFormat.MODE_KEY, this.mode.name());
        if (previous != null) {
            throw new IllegalStateException("found existing value for " + Lucene50StoredFieldsFormat.MODE_KEY + " for segment: " + si.name + "old=" + previous + ", new=" + this.mode.name());
        }
        return this.impl(this.mode).fieldsWriter(directory, si, context);
    }
    
    StoredFieldsFormat impl(final Mode mode) {
        switch (mode) {
            case BEST_SPEED: {
                return new CompressingStoredFieldsFormat("Lucene50StoredFieldsFast", CompressionMode.FAST, 16384, 128, 1024);
            }
            case BEST_COMPRESSION: {
                return new CompressingStoredFieldsFormat("Lucene50StoredFieldsHigh", CompressionMode.HIGH_COMPRESSION, 61440, 512, 1024);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    static {
        MODE_KEY = Lucene50StoredFieldsFormat.class.getSimpleName() + ".mode";
    }
    
    public enum Mode
    {
        BEST_SPEED, 
        BEST_COMPRESSION;
    }
}
