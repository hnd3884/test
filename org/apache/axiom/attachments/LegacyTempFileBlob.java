package org.apache.axiom.attachments;

import javax.activation.DataSource;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.blob.AbstractWritableBlob;

final class LegacyTempFileBlob extends AbstractWritableBlob
{
    private final LifecycleManager lifecycleManager;
    private final String attachmentDir;
    private FileAccessor fileAccessor;
    
    LegacyTempFileBlob(final LifecycleManager lifecycleManager, final String attachmentDir) {
        this.lifecycleManager = lifecycleManager;
        this.attachmentDir = attachmentDir;
    }
    
    public OutputStream getOutputStream() throws IOException {
        this.fileAccessor = this.lifecycleManager.create(this.attachmentDir);
        return this.fileAccessor.getOutputStream();
    }
    
    public InputStream getInputStream() throws IOException {
        return this.fileAccessor.getInputStream();
    }
    
    DataSource getDataSource(final String contentType) {
        final CachedFileDataSource ds = new CachedFileDataSource(this.fileAccessor.getFile());
        ds.setContentType(contentType);
        return ds;
    }
    
    public long getSize() {
        return this.fileAccessor.getSize();
    }
    
    public void release() throws IOException {
        this.lifecycleManager.delete(this.fileAccessor.getFile());
    }
}
