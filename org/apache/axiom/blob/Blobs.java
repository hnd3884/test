package org.apache.axiom.blob;

import java.io.File;

public final class Blobs
{
    private Blobs() {
    }
    
    public static MemoryBlob createMemoryBlob() {
        return new MemoryBlobImpl();
    }
    
    public static OverflowableBlob createOverflowableBlob(final int threshold, final WritableBlobFactory overflowBlobFactory) {
        final int numberOfChunks = Math.max(16, Math.min(1, threshold / 4096));
        final int chunkSize = threshold / numberOfChunks;
        return new OverflowableBlobImpl(numberOfChunks, chunkSize, overflowBlobFactory);
    }
    
    public static OverflowableBlob createOverflowableBlob(final int threshold, final String tempPrefix, final String tempSuffix, final File tempDirectory) {
        return createOverflowableBlob(threshold, new TempFileBlobFactory(tempPrefix, tempSuffix, tempDirectory));
    }
}
