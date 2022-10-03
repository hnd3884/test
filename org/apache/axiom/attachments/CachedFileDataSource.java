package org.apache.axiom.attachments;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.axiom.ext.activation.SizeAwareDataSource;
import javax.activation.FileDataSource;

public class CachedFileDataSource extends FileDataSource implements SizeAwareDataSource
{
    private static final Log log;
    String contentType;
    private static AttachmentCacheMonitor acm;
    private String cachedFileName;
    
    public CachedFileDataSource(final File file) {
        super(file);
        this.contentType = null;
        this.cachedFileName = null;
        if (CachedFileDataSource.log.isDebugEnabled()) {
            CachedFileDataSource.log.debug((Object)"Enter CachedFileDataSource ctor");
        }
        if (file != null) {
            try {
                this.cachedFileName = file.getCanonicalPath();
            }
            catch (final IOException e) {
                CachedFileDataSource.log.error((Object)("IOException caught: " + e));
            }
        }
        if (this.cachedFileName != null) {
            if (CachedFileDataSource.log.isDebugEnabled()) {
                CachedFileDataSource.log.debug((Object)("Cached file: " + this.cachedFileName));
                CachedFileDataSource.log.debug((Object)"Registering the file with AttachmentCacheMonitor and also marked it as being accessed");
            }
            CachedFileDataSource.acm.access(this.cachedFileName);
            CachedFileDataSource.acm.register(this.cachedFileName);
        }
    }
    
    @Override
    public String getContentType() {
        if (this.contentType != null) {
            return this.contentType;
        }
        return super.getContentType();
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
    
    public long getSize() {
        return this.getFile().length();
    }
    
    static {
        log = LogFactory.getLog((Class)CachedFileDataSource.class);
        CachedFileDataSource.acm = AttachmentCacheMonitor.getAttachmentCacheMonitor();
    }
}
