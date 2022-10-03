package com.me.devicemanagement.onpremise.webclient.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadBase;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import eu.medsea.mimeutil.MimeUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import com.adventnet.iam.security.UploadedFileItem;
import java.util.List;
import javax.servlet.http.HttpServletRequestWrapper;

public class SecurityRequestWrapper extends HttpServletRequestWrapper
{
    private String streamContent;
    public static final String MULTIPART = "multipart/";
    private List<UploadedFileItem> multiPartValues;
    private HashMap<String, List<String>> multiPartPostParams;
    private static final String TEMPDIR;
    
    public SecurityRequestWrapper(final HttpServletRequest request) {
        super(request);
        this.streamContent = null;
        this.multiPartValues = null;
        this.multiPartPostParams = null;
    }
    
    static File getTemporaryDir() {
        return new File(SecurityRequestWrapper.TEMPDIR);
    }
    
    public String getParameter(final String paramName) {
        final String value = super.getParameter(paramName);
        if (value == null && this.multiPartPostParams != null && this.multiPartPostParams.containsKey(paramName)) {
            return this.multiPartPostParams.get(paramName).get(0);
        }
        return value;
    }
    
    String getOriginalInputStreamContent() {
        return this.streamContent;
    }
    
    void setOriginalInputStreamContent(final String content) {
        this.streamContent = content;
    }
    
    public String[] getParameterValues(final String paramName) {
        if (this.multiPartPostParams != null && this.multiPartPostParams.containsKey(paramName)) {
            return (String[])this.multiPartPostParams.get(paramName).toArray(new String[0]);
        }
        return super.getParameterValues(paramName);
    }
    
    public Map getParameterMap() {
        final Enumeration<String> e = this.getParameterNames();
        if (e == null) {
            return new HashMap();
        }
        final HashMap map = new HashMap(super.getParameterMap().size());
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            final String[] value = this.getParameterValues(key);
            if (value != null) {
                map.put(key, value);
            }
            else {
                map.put(key, new String[] { "" });
            }
        }
        return map;
    }
    
    public Enumeration<String> getParameterNames() {
        if (this.isMultipartRequest() && this.multiPartPostParams != null) {
            final Enumeration<String> requestParams = super.getParameterNames();
            final ArrayList<String> list = Collections.list(requestParams);
            list.addAll(this.multiPartPostParams.keySet());
            return Collections.enumeration(list);
        }
        return super.getParameterNames();
    }
    
    public boolean isMultipartRequest() {
        if (!"post".equals(this.getMethod().toLowerCase()) && !"put".equals(this.getMethod().toLowerCase())) {
            return false;
        }
        final String contentType = this.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }
    
    public void initMultipartParams(final Long maxFileSize) {
        try {
            this.multiPartValues = new ArrayList<UploadedFileItem>();
            this.multiPartPostParams = new HashMap<String, List<String>>();
            final File tmpDir = getTemporaryDir();
            final DiskFileItemFactory factory = new DiskFileItemFactory(0, tmpDir);
            final ServletFileUpload upload = new ServletFileUpload((FileItemFactory)factory);
            upload.setSizeMax((long)maxFileSize);
            final FileItemIterator iter = upload.getItemIterator((HttpServletRequest)this);
            while (iter.hasNext()) {
                final FileItemStream item = iter.next();
                if (item.isFormField()) {
                    List<String> values = this.multiPartPostParams.get(item.getFieldName());
                    if (values == null) {
                        values = new ArrayList<String>();
                        this.multiPartPostParams.put(item.getFieldName(), values);
                    }
                    values.add(Streams.asString(item.openStream(), "UTF-8"));
                }
                else {
                    final DiskFileItem fileItem = (DiskFileItem)factory.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), item.getName());
                    final String filename = fileItem.getName();
                    final String[] str = filename.split("[\\/\\\\]");
                    final String fileName = str[str.length - 1];
                    final UploadedFileItem uploadFileItem = new UploadedFile(fileName, -1L, null, fileItem.getFieldName(), fileItem.getContentType(), null, fileItem);
                    copy(item.openStream(), new byte[8192], maxFileSize, fileItem);
                    final File fileLocation = fileItem.getStoreLocation();
                    if (fileLocation == null) {
                        continue;
                    }
                    if (fileItem.isInMemory() && !fileLocation.exists() && !fileItem.getName().isEmpty()) {
                        try {
                            fileLocation.createNewFile();
                        }
                        catch (final Exception e) {
                            Logger.getLogger(SecurityRequestWrapper.class.getName()).log(Level.SEVERE, "Cannot write uploaded empty file to disk", e);
                        }
                    }
                    String contentType = null;
                    final Collection mimeTypes = MimeUtil.getMimeTypes(fileItem.getStoreLocation());
                    final Iterator mimeIterator = mimeTypes.iterator();
                    if (mimeIterator.hasNext()) {
                        contentType = mimeIterator.next().toString();
                    }
                    uploadFileItem.setFileSize(fileItem.getSize());
                    uploadFileItem.setUploadedFile(fileItem.getStoreLocation());
                    uploadFileItem.setContentTypeDetected(contentType);
                    this.multiPartValues.add(uploadFileItem);
                }
            }
            this.setAttribute("MULTIPART_FORM_REQUEST", (Object)this.multiPartValues);
            DMThreadLocal.setUploadedFileItem(this.multiPartValues);
        }
        catch (final FileUploadBase.SizeLimitExceededException e2) {
            Logger.getLogger(SecurityRequestWrapper.class.getName()).log(Level.SEVERE, "Exception while parsing Multipart-form request for {0} {1} size limit exceeded", new Object[] { SecurityUtil.getNormalizedRequestURI((HttpServletRequest)this), e2 });
        }
        catch (final Exception e3) {
            Logger.getLogger(SecurityRequestWrapper.class.getName()).log(Level.SEVERE, "Exception while parsing Multipart-form request for {0} {1}", new Object[] { SecurityUtil.getNormalizedRequestURI((HttpServletRequest)this), e3 });
        }
    }
    
    public static long copy(final InputStream pIn, byte[] pBuffer, final long maxSizeInBytes, final DiskFileItem fileItem) throws IOException {
        if (maxSizeInBytes > 0L && maxSizeInBytes < 8192L) {
            pBuffer = new byte[(int)maxSizeInBytes];
        }
        OutputStream out = fileItem.getOutputStream();
        InputStream in = pIn;
        try {
            long total = 0L;
            while (true) {
                final int res = in.read(pBuffer);
                if (res == -1) {
                    break;
                }
                if (res <= 0) {
                    continue;
                }
                total += res;
                if (out == null) {
                    continue;
                }
                out.write(pBuffer, 0, res);
            }
            if (out != null) {
                out.close();
                out.flush();
                out = null;
            }
            in.close();
            in = null;
            return total;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Throwable t) {}
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final Throwable t2) {}
            }
        }
    }
    
    public UploadedFileItem getMultipartParameter(final String paramName) {
        if (this.multiPartValues != null) {
            for (final UploadedFileItem uf : this.multiPartValues) {
                if (uf.getFieldName().equals(paramName)) {
                    return uf;
                }
            }
        }
        return null;
    }
    
    public List<UploadedFileItem> getMultipartParameters(final String paramName) {
        if (this.multiPartValues == null) {
            return null;
        }
        final List<UploadedFileItem> files = new ArrayList<UploadedFileItem>();
        for (final UploadedFileItem uf : this.multiPartValues) {
            if (uf.getFieldName().equals(paramName)) {
                files.add(uf);
            }
        }
        return files;
    }
    
    public List<UploadedFileItem> getMultipartFiles() {
        return this.multiPartValues;
    }
    
    long getRequestSize() {
        final int contentLength = this.getContentLength();
        final String contentLenHeader = this.getHeader("content-length");
        if (contentLength != -1) {
            return this.getContentLength();
        }
        if (com.adventnet.iam.security.SecurityUtil.isValid((Object)contentLenHeader) && Double.parseDouble(contentLenHeader) != -1.0) {
            return Long.parseLong(contentLenHeader);
        }
        if (com.adventnet.iam.security.SecurityUtil.isValid((Object)this.getOriginalInputStreamContent())) {
            return this.getOriginalInputStreamContent().length();
        }
        if (this.getParameterMap() != null) {
            return this.getRequestParamMapSize(this);
        }
        return 0L;
    }
    
    private long getRequestParamMapSize(final SecurityRequestWrapper req) {
        long size = 0L;
        final HashMap map = (HashMap)req.getParameterMap();
        for (final Map.Entry entry : map.entrySet()) {
            if (com.adventnet.iam.security.SecurityUtil.isValid(entry.getKey())) {
                size += entry.getKey().toString().length();
            }
            if (com.adventnet.iam.security.SecurityUtil.isValid(entry.getValue())) {
                final String[] values = entry.getValue();
                int len = 0;
                for (int i = 0; i < values.length; ++i) {
                    len += values[i].length();
                }
                size += len;
            }
            ++size;
        }
        if (map.size() > 0) {
            size += map.size() - 1;
        }
        final String queryString = req.getQueryString();
        if (com.adventnet.iam.security.SecurityUtil.isValid((Object)queryString)) {
            if (queryString.length() < size) {
                size -= queryString.length() + 1;
            }
            else {
                size -= queryString.length();
            }
        }
        return size;
    }
    
    static {
        TEMPDIR = System.getProperty("java.io.tmpdir");
        if (SecurityRequestWrapper.TEMPDIR == null) {
            throw new RuntimeException("System property \"java.io.tmpdir\" is null");
        }
        final File tmpDir = new File(SecurityRequestWrapper.TEMPDIR);
        if (!tmpDir.isDirectory() && !tmpDir.mkdir()) {
            throw new RuntimeException("Temp Directory Creation Failed");
        }
    }
}
