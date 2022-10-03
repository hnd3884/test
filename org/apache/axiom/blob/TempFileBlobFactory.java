package org.apache.axiom.blob;

import java.io.IOException;
import java.io.File;

final class TempFileBlobFactory implements WritableBlobFactory
{
    private final String tempPrefix;
    private final String tempSuffix;
    private final File tempDirectory;
    
    TempFileBlobFactory(final String tempPrefix, final String tempSuffix, final File tempDirectory) {
        this.tempPrefix = tempPrefix;
        this.tempSuffix = tempSuffix;
        this.tempDirectory = tempDirectory;
    }
    
    public WritableBlob createBlob() {
        return new TempFileBlobImpl(this);
    }
    
    File createTempFile() throws IOException {
        return File.createTempFile(this.tempPrefix, this.tempSuffix, this.tempDirectory);
    }
}
