package org.apache.axiom.attachments;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;

final class LegacyTempFileBlobFactory implements WritableBlobFactory
{
    private final Attachments attachments;
    private final String attachmentDir;
    
    LegacyTempFileBlobFactory(final Attachments attachments, final String attachmentDir) {
        this.attachments = attachments;
        this.attachmentDir = attachmentDir;
    }
    
    public WritableBlob createBlob() {
        return new LegacyTempFileBlob(this.attachments.getLifecycleManager(), this.attachmentDir);
    }
}
