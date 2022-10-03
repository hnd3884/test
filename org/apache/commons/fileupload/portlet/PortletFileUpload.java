package org.apache.commons.fileupload.portlet;

import java.io.IOException;
import org.apache.commons.fileupload.FileItemIterator;
import java.util.Map;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.FileUploadBase;
import javax.portlet.ActionRequest;
import org.apache.commons.fileupload.FileUpload;

public class PortletFileUpload extends FileUpload
{
    public static final boolean isMultipartContent(final ActionRequest request) {
        return FileUploadBase.isMultipartContent(new PortletRequestContext(request));
    }
    
    public PortletFileUpload() {
    }
    
    public PortletFileUpload(final FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }
    
    public List<FileItem> parseRequest(final ActionRequest request) throws FileUploadException {
        return this.parseRequest(new PortletRequestContext(request));
    }
    
    public Map<String, List<FileItem>> parseParameterMap(final ActionRequest request) throws FileUploadException {
        return this.parseParameterMap(new PortletRequestContext(request));
    }
    
    public FileItemIterator getItemIterator(final ActionRequest request) throws FileUploadException, IOException {
        return super.getItemIterator(new PortletRequestContext(request));
    }
}
