package org.apache.axiom.attachments;

import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.om.OMException;
import java.io.InputStream;
import org.apache.axiom.attachments.lifecycle.impl.LifecycleManagerImpl;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMAttachmentAccessor;

public class Attachments implements OMAttachmentAccessor
{
    private final AttachmentsDelegate delegate;
    private String applicationType;
    private LifecycleManager manager;
    
    public LifecycleManager getLifecycleManager() {
        if (this.manager == null) {
            this.manager = new LifecycleManagerImpl();
        }
        return this.manager;
    }
    
    public void setLifecycleManager(final LifecycleManager manager) {
        this.manager = manager;
    }
    
    public Attachments(final LifecycleManager manager, final InputStream inStream, final String contentTypeString, final boolean fileCacheEnable, final String attachmentRepoDir, final String fileThreshold) throws OMException {
        this(manager, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
    }
    
    public Attachments(final LifecycleManager manager, final InputStream inStream, final String contentTypeString, final boolean fileCacheEnable, final String attachmentRepoDir, final String fileThreshold, final int contentLength) throws OMException {
        this.manager = manager;
        int fileStorageThreshold;
        if (fileThreshold != null && !"".equals(fileThreshold)) {
            fileStorageThreshold = Integer.parseInt(fileThreshold);
        }
        else {
            fileStorageThreshold = 0;
        }
        WritableBlobFactory attachmentBlobFactory;
        if (fileCacheEnable) {
            final WritableBlobFactory tempFileBlobFactory = new LegacyTempFileBlobFactory(this, attachmentRepoDir);
            if (fileStorageThreshold > 0) {
                attachmentBlobFactory = new WritableBlobFactory() {
                    public WritableBlob createBlob() {
                        return Blobs.createOverflowableBlob(fileStorageThreshold, tempFileBlobFactory);
                    }
                };
            }
            else {
                attachmentBlobFactory = tempFileBlobFactory;
            }
        }
        else {
            attachmentBlobFactory = new WritableBlobFactory() {
                public WritableBlob createBlob() {
                    return Blobs.createMemoryBlob();
                }
            };
        }
        this.delegate = new MIMEMessage(inStream, contentTypeString, attachmentBlobFactory, contentLength);
    }
    
    public Attachments(final InputStream inStream, final String contentTypeString, final boolean fileCacheEnable, final String attachmentRepoDir, final String fileThreshold) throws OMException {
        this(null, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
    }
    
    public Attachments(final InputStream inStream, final String contentTypeString, final boolean fileCacheEnable, final String attachmentRepoDir, final String fileThreshold, final int contentLength) throws OMException {
        this(null, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, contentLength);
    }
    
    public Attachments(final InputStream inStream, final String contentTypeString) throws OMException {
        this(null, inStream, contentTypeString, false, null, null);
    }
    
    public Attachments() {
        this.delegate = new AttachmentSet();
    }
    
    public String getAttachmentSpecType() {
        if (this.applicationType == null) {
            final ContentType contentType = this.delegate.getContentType();
            if (contentType == null) {
                throw new OMException("Unable to determine the attachment spec type because the Attachments object doesn't have a known content type");
            }
            this.applicationType = contentType.getParameter("type");
            if ("application/xop+xml".equalsIgnoreCase(this.applicationType)) {
                this.applicationType = "application/xop+xml";
            }
            else if ("text/xml".equalsIgnoreCase(this.applicationType)) {
                this.applicationType = "text/xml";
            }
            else {
                if (!"application/soap+xml".equalsIgnoreCase(this.applicationType)) {
                    throw new OMException("Invalid Application type. Support available for MTOM & SwA only.");
                }
                this.applicationType = "application/soap+xml";
            }
        }
        return this.applicationType;
    }
    
    public DataHandler getDataHandler(final String contentID) {
        return this.delegate.getDataHandler(contentID);
    }
    
    public void addDataHandler(final String contentID, final DataHandler dataHandler) {
        this.delegate.addDataHandler(contentID, dataHandler);
    }
    
    public void removeDataHandler(final String blobContentID) {
        this.delegate.removeDataHandler(blobContentID);
    }
    
    @Deprecated
    public InputStream getSOAPPartInputStream() throws OMException {
        return this.getRootPartInputStream();
    }
    
    @Deprecated
    public String getSOAPPartContentID() {
        return this.getRootPartContentID();
    }
    
    @Deprecated
    public String getSOAPPartContentType() {
        return this.getRootPartContentType();
    }
    
    public InputStream getRootPartInputStream() throws OMException {
        return this.delegate.getRootPartInputStream(true);
    }
    
    public InputStream getRootPartInputStream(final boolean preserve) throws OMException {
        return this.delegate.getRootPartInputStream(preserve);
    }
    
    public String getRootPartContentID() {
        return this.delegate.getRootPartContentID();
    }
    
    public String getRootPartContentType() {
        return this.delegate.getRootPartContentType();
    }
    
    public IncomingAttachmentStreams getIncomingAttachmentStreams() throws IllegalStateException {
        return this.delegate.getIncomingAttachmentStreams();
    }
    
    public String[] getAllContentIDs() {
        final Set cids = this.delegate.getContentIDs(true);
        return cids.toArray(new String[cids.size()]);
    }
    
    public Set getContentIDSet() {
        return this.delegate.getContentIDs(true);
    }
    
    public Map getMap() {
        return this.delegate.getMap();
    }
    
    public List getContentIDList() {
        return new ArrayList(this.delegate.getContentIDs(false));
    }
    
    public long getContentLength() throws IOException {
        return this.delegate.getContentLength();
    }
    
    @Deprecated
    public InputStream getIncomingAttachmentsAsSingleStream() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }
}
