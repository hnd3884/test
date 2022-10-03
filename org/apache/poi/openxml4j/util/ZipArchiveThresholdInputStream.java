package org.apache.poi.openxml4j.util;

import java.io.EOFException;
import java.util.zip.ZipException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import java.util.Locale;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import org.apache.commons.compress.utils.InputStreamStatistics;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.util.Internal;
import java.io.FilterInputStream;

@Internal
public class ZipArchiveThresholdInputStream extends FilterInputStream
{
    private static final long GRACE_ENTRY_SIZE = 102400L;
    private static final String MAX_ENTRY_SIZE_MSG = "Zip bomb detected! The file would exceed the max size of the expanded data in the zip-file.\nThis may indicates that the file is used to inflate memory usage and thus could pose a security risk.\nYou can adjust this limit via ZipSecureFile.setMaxEntrySize() if you need to work with files which are very large.\nUncompressed size: %d, Raw/compressed size: %d\nLimits: MAX_ENTRY_SIZE: %d, Entry: %s";
    private static final String MIN_INFLATE_RATIO_MSG = "Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.\nThis may indicate that the file is used to inflate memory usage and thus could pose a security risk.\nYou can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit.\nUncompressed size: %d, Raw/compressed size: %d, ratio: %f\nLimits: MIN_INFLATE_RATIO: %f, Entry: %s";
    private ZipArchiveEntry entry;
    private boolean guardState;
    
    public ZipArchiveThresholdInputStream(final InputStream is) {
        super(is);
        this.guardState = true;
        if (!(is instanceof InputStreamStatistics)) {
            throw new IllegalArgumentException("InputStream of class " + is.getClass() + " is not implementing InputStreamStatistics.");
        }
    }
    
    @Override
    public int read() throws IOException {
        final int b = super.read();
        if (b > -1) {
            this.checkThreshold();
        }
        return b;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int cnt = super.read(b, off, len);
        if (cnt > -1) {
            this.checkThreshold();
        }
        return cnt;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long cnt = IOUtils.skipFully(super.in, n);
        if (cnt > 0L) {
            this.checkThreshold();
        }
        return cnt;
    }
    
    public void setGuardState(final boolean guardState) {
        this.guardState = guardState;
    }
    
    private void checkThreshold() throws IOException {
        if (!this.guardState) {
            return;
        }
        final InputStreamStatistics stats = (InputStreamStatistics)this.in;
        final long payloadSize = stats.getUncompressedCount();
        final long rawSize = stats.getCompressedCount();
        final String entryName = (this.entry == null) ? "not set" : this.entry.getName();
        if (payloadSize > ZipSecureFile.MAX_ENTRY_SIZE) {
            throw new IOException(String.format(Locale.ROOT, "Zip bomb detected! The file would exceed the max size of the expanded data in the zip-file.\nThis may indicates that the file is used to inflate memory usage and thus could pose a security risk.\nYou can adjust this limit via ZipSecureFile.setMaxEntrySize() if you need to work with files which are very large.\nUncompressed size: %d, Raw/compressed size: %d\nLimits: MAX_ENTRY_SIZE: %d, Entry: %s", payloadSize, rawSize, ZipSecureFile.MAX_ENTRY_SIZE, entryName));
        }
        if (payloadSize <= 102400L) {
            return;
        }
        final double ratio = rawSize / (double)payloadSize;
        if (ratio >= ZipSecureFile.MIN_INFLATE_RATIO) {
            return;
        }
        throw new IOException(String.format(Locale.ROOT, "Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.\nThis may indicate that the file is used to inflate memory usage and thus could pose a security risk.\nYou can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit.\nUncompressed size: %d, Raw/compressed size: %d, ratio: %f\nLimits: MIN_INFLATE_RATIO: %f, Entry: %s", payloadSize, rawSize, ratio, ZipSecureFile.MIN_INFLATE_RATIO, entryName));
    }
    
    ZipArchiveEntry getNextEntry() throws IOException {
        if (!(this.in instanceof ZipArchiveInputStream)) {
            throw new IllegalStateException("getNextEntry() is only allowed for stream based zip processing.");
        }
        try {
            return this.entry = ((ZipArchiveInputStream)this.in).getNextZipEntry();
        }
        catch (final ZipException ze) {
            if (ze.getMessage().startsWith("Unexpected record signature")) {
                throw new NotOfficeXmlFileException("No valid entries or contents found, this is not a valid OOXML (Office Open XML) file", ze);
            }
            throw ze;
        }
        catch (final EOFException e) {
            return null;
        }
    }
    
    void setEntry(final ZipArchiveEntry entry) {
        this.entry = entry;
    }
}
