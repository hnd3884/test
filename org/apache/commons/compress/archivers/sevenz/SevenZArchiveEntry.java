package org.apache.commons.compress.archivers.sevenz;

import java.util.TimeZone;
import java.util.Calendar;
import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Date;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class SevenZArchiveEntry implements ArchiveEntry
{
    private String name;
    private boolean hasStream;
    private boolean isDirectory;
    private boolean isAntiItem;
    private boolean hasCreationDate;
    private boolean hasLastModifiedDate;
    private boolean hasAccessDate;
    private long creationDate;
    private long lastModifiedDate;
    private long accessDate;
    private boolean hasWindowsAttributes;
    private int windowsAttributes;
    private boolean hasCrc;
    private long crc;
    private long compressedCrc;
    private long size;
    private long compressedSize;
    private Iterable<? extends SevenZMethodConfiguration> contentMethods;
    static final SevenZArchiveEntry[] EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY;
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean hasStream() {
        return this.hasStream;
    }
    
    public void setHasStream(final boolean hasStream) {
        this.hasStream = hasStream;
    }
    
    @Override
    public boolean isDirectory() {
        return this.isDirectory;
    }
    
    public void setDirectory(final boolean isDirectory) {
        this.isDirectory = isDirectory;
    }
    
    public boolean isAntiItem() {
        return this.isAntiItem;
    }
    
    public void setAntiItem(final boolean isAntiItem) {
        this.isAntiItem = isAntiItem;
    }
    
    public boolean getHasCreationDate() {
        return this.hasCreationDate;
    }
    
    public void setHasCreationDate(final boolean hasCreationDate) {
        this.hasCreationDate = hasCreationDate;
    }
    
    public Date getCreationDate() {
        if (this.hasCreationDate) {
            return ntfsTimeToJavaTime(this.creationDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }
    
    public void setCreationDate(final long ntfsCreationDate) {
        this.creationDate = ntfsCreationDate;
    }
    
    public void setCreationDate(final Date creationDate) {
        this.hasCreationDate = (creationDate != null);
        if (this.hasCreationDate) {
            this.creationDate = javaTimeToNtfsTime(creationDate);
        }
    }
    
    public boolean getHasLastModifiedDate() {
        return this.hasLastModifiedDate;
    }
    
    public void setHasLastModifiedDate(final boolean hasLastModifiedDate) {
        this.hasLastModifiedDate = hasLastModifiedDate;
    }
    
    @Override
    public Date getLastModifiedDate() {
        if (this.hasLastModifiedDate) {
            return ntfsTimeToJavaTime(this.lastModifiedDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }
    
    public void setLastModifiedDate(final long ntfsLastModifiedDate) {
        this.lastModifiedDate = ntfsLastModifiedDate;
    }
    
    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.hasLastModifiedDate = (lastModifiedDate != null);
        if (this.hasLastModifiedDate) {
            this.lastModifiedDate = javaTimeToNtfsTime(lastModifiedDate);
        }
    }
    
    public boolean getHasAccessDate() {
        return this.hasAccessDate;
    }
    
    public void setHasAccessDate(final boolean hasAcessDate) {
        this.hasAccessDate = hasAcessDate;
    }
    
    public Date getAccessDate() {
        if (this.hasAccessDate) {
            return ntfsTimeToJavaTime(this.accessDate);
        }
        throw new UnsupportedOperationException("The entry doesn't have this timestamp");
    }
    
    public void setAccessDate(final long ntfsAccessDate) {
        this.accessDate = ntfsAccessDate;
    }
    
    public void setAccessDate(final Date accessDate) {
        this.hasAccessDate = (accessDate != null);
        if (this.hasAccessDate) {
            this.accessDate = javaTimeToNtfsTime(accessDate);
        }
    }
    
    public boolean getHasWindowsAttributes() {
        return this.hasWindowsAttributes;
    }
    
    public void setHasWindowsAttributes(final boolean hasWindowsAttributes) {
        this.hasWindowsAttributes = hasWindowsAttributes;
    }
    
    public int getWindowsAttributes() {
        return this.windowsAttributes;
    }
    
    public void setWindowsAttributes(final int windowsAttributes) {
        this.windowsAttributes = windowsAttributes;
    }
    
    public boolean getHasCrc() {
        return this.hasCrc;
    }
    
    public void setHasCrc(final boolean hasCrc) {
        this.hasCrc = hasCrc;
    }
    
    @Deprecated
    public int getCrc() {
        return (int)this.crc;
    }
    
    @Deprecated
    public void setCrc(final int crc) {
        this.crc = crc;
    }
    
    public long getCrcValue() {
        return this.crc;
    }
    
    public void setCrcValue(final long crc) {
        this.crc = crc;
    }
    
    @Deprecated
    int getCompressedCrc() {
        return (int)this.compressedCrc;
    }
    
    @Deprecated
    void setCompressedCrc(final int crc) {
        this.compressedCrc = crc;
    }
    
    long getCompressedCrcValue() {
        return this.compressedCrc;
    }
    
    void setCompressedCrcValue(final long crc) {
        this.compressedCrc = crc;
    }
    
    @Override
    public long getSize() {
        return this.size;
    }
    
    public void setSize(final long size) {
        this.size = size;
    }
    
    long getCompressedSize() {
        return this.compressedSize;
    }
    
    void setCompressedSize(final long size) {
        this.compressedSize = size;
    }
    
    public void setContentMethods(final Iterable<? extends SevenZMethodConfiguration> methods) {
        if (methods != null) {
            final LinkedList<SevenZMethodConfiguration> l = new LinkedList<SevenZMethodConfiguration>();
            for (final SevenZMethodConfiguration m : methods) {
                l.addLast(m);
            }
            this.contentMethods = (Iterable<? extends SevenZMethodConfiguration>)Collections.unmodifiableList((List<?>)l);
        }
        else {
            this.contentMethods = null;
        }
    }
    
    public Iterable<? extends SevenZMethodConfiguration> getContentMethods() {
        return this.contentMethods;
    }
    
    @Override
    public int hashCode() {
        final String n = this.getName();
        return (n == null) ? 0 : n.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final SevenZArchiveEntry other = (SevenZArchiveEntry)obj;
        return Objects.equals(this.name, other.name) && this.hasStream == other.hasStream && this.isDirectory == other.isDirectory && this.isAntiItem == other.isAntiItem && this.hasCreationDate == other.hasCreationDate && this.hasLastModifiedDate == other.hasLastModifiedDate && this.hasAccessDate == other.hasAccessDate && this.creationDate == other.creationDate && this.lastModifiedDate == other.lastModifiedDate && this.accessDate == other.accessDate && this.hasWindowsAttributes == other.hasWindowsAttributes && this.windowsAttributes == other.windowsAttributes && this.hasCrc == other.hasCrc && this.crc == other.crc && this.compressedCrc == other.compressedCrc && this.size == other.size && this.compressedSize == other.compressedSize && this.equalSevenZMethods(this.contentMethods, other.contentMethods);
    }
    
    public static Date ntfsTimeToJavaTime(final long ntfsTime) {
        final Calendar ntfsEpoch = Calendar.getInstance();
        ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
        ntfsEpoch.set(14, 0);
        final long realTime = ntfsEpoch.getTimeInMillis() + ntfsTime / 10000L;
        return new Date(realTime);
    }
    
    public static long javaTimeToNtfsTime(final Date date) {
        final Calendar ntfsEpoch = Calendar.getInstance();
        ntfsEpoch.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        ntfsEpoch.set(1601, 0, 1, 0, 0, 0);
        ntfsEpoch.set(14, 0);
        return (date.getTime() - ntfsEpoch.getTimeInMillis()) * 1000L * 10L;
    }
    
    private boolean equalSevenZMethods(final Iterable<? extends SevenZMethodConfiguration> c1, final Iterable<? extends SevenZMethodConfiguration> c2) {
        if (c1 == null) {
            return c2 == null;
        }
        if (c2 == null) {
            return false;
        }
        final Iterator<? extends SevenZMethodConfiguration> i1 = c1.iterator();
        final Iterator<? extends SevenZMethodConfiguration> i2 = c2.iterator();
        while (i1.hasNext()) {
            if (!i2.hasNext()) {
                return false;
            }
            if (!((SevenZMethodConfiguration)i1.next()).equals(i2.next())) {
                return false;
            }
        }
        return !i2.hasNext();
    }
    
    static {
        EMPTY_SEVEN_Z_ARCHIVE_ENTRY_ARRAY = new SevenZArchiveEntry[0];
    }
}
