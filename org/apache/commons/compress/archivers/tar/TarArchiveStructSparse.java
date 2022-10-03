package org.apache.commons.compress.archivers.tar;

import java.util.Objects;

public final class TarArchiveStructSparse
{
    private final long offset;
    private final long numbytes;
    
    public TarArchiveStructSparse(final long offset, final long numbytes) {
        if (offset < 0L) {
            throw new IllegalArgumentException("offset must not be negative");
        }
        if (numbytes < 0L) {
            throw new IllegalArgumentException("numbytes must not be negative");
        }
        this.offset = offset;
        this.numbytes = numbytes;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TarArchiveStructSparse that = (TarArchiveStructSparse)o;
        return this.offset == that.offset && this.numbytes == that.numbytes;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.offset, this.numbytes);
    }
    
    @Override
    public String toString() {
        return "TarArchiveStructSparse{offset=" + this.offset + ", numbytes=" + this.numbytes + '}';
    }
    
    public long getOffset() {
        return this.offset;
    }
    
    public long getNumbytes() {
        return this.numbytes;
    }
}
