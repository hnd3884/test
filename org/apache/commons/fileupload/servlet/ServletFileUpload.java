package org.apache.commons.fileupload.servlet;

import java.io.IOException;
import org.apache.commons.fileupload.FileItemIterator;
import java.util.Map;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.FileUploadBase;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileUpload;

public class ServletFileUpload extends FileUpload
{
    private static final String POST_METHOD = "POST";
    
    public static final boolean isMultipartContent(final HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) && FileUploadBase.isMultipartContent(new ServletRequestContext(request));
    }
    
    public ServletFileUpload() {
    }
    
    public ServletFileUpload(final FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }
    
    @Override
    public List<FileItem> parseRequest(final HttpServletRequest request) throws FileUploadException {
        return this.parseRequest(new ServletRequestContext(request));
    }
    
    public Map<String, List<FileItem>> parseParameterMap(final HttpServletRequest request) throws FileUploadException {
        return this.parseParameterMap(new ServletRequestContext(request));
    }
    
    public FileItemIterator getItemIterator(final HttpServletRequest request) throws FileUploadException, IOException {
        return super.getItemIterator(new ServletRequestContext(request));
    }
}
