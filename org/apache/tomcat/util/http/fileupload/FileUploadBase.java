package org.apache.tomcat.util.http.fileupload;

import org.apache.tomcat.util.http.fileupload.util.FileItemHeadersImpl;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.impl.FileUploadIOException;
import org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl;
import java.util.Locale;

public abstract class FileUploadBase
{
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String FORM_DATA = "form-data";
    public static final String ATTACHMENT = "attachment";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String MULTIPART_MIXED = "multipart/mixed";
    private long sizeMax;
    private long fileSizeMax;
    private String headerEncoding;
    private ProgressListener listener;
    
    public FileUploadBase() {
        this.sizeMax = -1L;
        this.fileSizeMax = -1L;
    }
    
    public static final boolean isMultipartContent(final RequestContext ctx) {
        final String contentType = ctx.getContentType();
        return contentType != null && contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/");
    }
    
    public abstract FileItemFactory getFileItemFactory();
    
    public abstract void setFileItemFactory(final FileItemFactory p0);
    
    public long getSizeMax() {
        return this.sizeMax;
    }
    
    public void setSizeMax(final long sizeMax) {
        this.sizeMax = sizeMax;
    }
    
    public long getFileSizeMax() {
        return this.fileSizeMax;
    }
    
    public void setFileSizeMax(final long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }
    
    public String getHeaderEncoding() {
        return this.headerEncoding;
    }
    
    public void setHeaderEncoding(final String encoding) {
        this.headerEncoding = encoding;
    }
    
    public FileItemIterator getItemIterator(final RequestContext ctx) throws FileUploadException, IOException {
        try {
            return new FileItemIteratorImpl(this, ctx);
        }
        catch (final FileUploadIOException e) {
            throw (FileUploadException)e.getCause();
        }
    }
    
    public List<FileItem> parseRequest(final RequestContext ctx) throws FileUploadException {
        final List<FileItem> items = new ArrayList<FileItem>();
        boolean successful = false;
        try {
            final FileItemIterator iter = this.getItemIterator(ctx);
            final FileItemFactory fileItemFactory = Objects.requireNonNull(this.getFileItemFactory(), "No FileItemFactory has been set.");
            final byte[] buffer = new byte[8192];
            while (iter.hasNext()) {
                final FileItemStream item = iter.next();
                final String fileName = item.getName();
                final FileItem fileItem = fileItemFactory.createItem(item.getFieldName(), item.getContentType(), item.isFormField(), fileName);
                items.add(fileItem);
                try {
                    Streams.copy(item.openStream(), fileItem.getOutputStream(), true, buffer);
                }
                catch (final FileUploadIOException e) {
                    throw (FileUploadException)e.getCause();
                }
                catch (final IOException e2) {
                    throw new IOFileUploadException(String.format("Processing of %s request failed. %s", "multipart/form-data", e2.getMessage()), e2);
                }
                final FileItemHeaders fih = item.getHeaders();
                fileItem.setHeaders(fih);
            }
            successful = true;
            return items;
        }
        catch (final FileUploadException e3) {
            throw e3;
        }
        catch (final IOException e4) {
            throw new FileUploadException(e4.getMessage(), e4);
        }
        finally {
            if (!successful) {
                for (final FileItem fileItem2 : items) {
                    try {
                        fileItem2.delete();
                    }
                    catch (final Exception ex) {}
                }
            }
        }
    }
    
    public Map<String, List<FileItem>> parseParameterMap(final RequestContext ctx) throws FileUploadException {
        final List<FileItem> items = this.parseRequest(ctx);
        final Map<String, List<FileItem>> itemsMap = new HashMap<String, List<FileItem>>(items.size());
        for (final FileItem fileItem : items) {
            final String fieldName = fileItem.getFieldName();
            List<FileItem> mappedItems = itemsMap.get(fieldName);
            if (mappedItems == null) {
                mappedItems = new ArrayList<FileItem>();
                itemsMap.put(fieldName, mappedItems);
            }
            mappedItems.add(fileItem);
        }
        return itemsMap;
    }
    
    public byte[] getBoundary(final String contentType) {
        final ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        final Map<String, String> params = parser.parse(contentType, new char[] { ';', ',' });
        final String boundaryStr = params.get("boundary");
        if (boundaryStr == null) {
            return null;
        }
        final byte[] boundary = boundaryStr.getBytes(StandardCharsets.ISO_8859_1);
        return boundary;
    }
    
    public String getFileName(final FileItemHeaders headers) {
        return this.getFileName(headers.getHeader("Content-disposition"));
    }
    
    private String getFileName(final String pContentDisposition) {
        String fileName = null;
        if (pContentDisposition != null) {
            final String cdl = pContentDisposition.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith("form-data") || cdl.startsWith("attachment")) {
                final ParameterParser parser = new ParameterParser();
                parser.setLowerCaseNames(true);
                final Map<String, String> params = parser.parse(pContentDisposition, ';');
                if (params.containsKey("filename")) {
                    fileName = params.get("filename");
                    if (fileName != null) {
                        fileName = fileName.trim();
                    }
                    else {
                        fileName = "";
                    }
                }
            }
        }
        return fileName;
    }
    
    public String getFieldName(final FileItemHeaders headers) {
        return this.getFieldName(headers.getHeader("Content-disposition"));
    }
    
    private String getFieldName(final String pContentDisposition) {
        String fieldName = null;
        if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith("form-data")) {
            final ParameterParser parser = new ParameterParser();
            parser.setLowerCaseNames(true);
            final Map<String, String> params = parser.parse(pContentDisposition, ';');
            fieldName = params.get("name");
            if (fieldName != null) {
                fieldName = fieldName.trim();
            }
        }
        return fieldName;
    }
    
    public FileItemHeaders getParsedHeaders(final String headerPart) {
        final int len = headerPart.length();
        final FileItemHeadersImpl headers = this.newFileItemHeaders();
        int start = 0;
        while (true) {
            int end = this.parseEndOfLine(headerPart, start);
            if (start == end) {
                break;
            }
            final StringBuilder header = new StringBuilder(headerPart.substring(start, end));
            for (start = end + 2; start < len; start = end + 2) {
                int nonWs;
                for (nonWs = start; nonWs < len; ++nonWs) {
                    final char c = headerPart.charAt(nonWs);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                }
                if (nonWs == start) {
                    break;
                }
                end = this.parseEndOfLine(headerPart, nonWs);
                header.append(' ').append(headerPart, nonWs, end);
            }
            this.parseHeaderLine(headers, header.toString());
        }
        return headers;
    }
    
    protected FileItemHeadersImpl newFileItemHeaders() {
        return new FileItemHeadersImpl();
    }
    
    private int parseEndOfLine(final String headerPart, final int end) {
        int index = end;
        while (true) {
            final int offset = headerPart.indexOf(13, index);
            if (offset == -1 || offset + 1 >= headerPart.length()) {
                throw new IllegalStateException("Expected headers to be terminated by an empty line.");
            }
            if (headerPart.charAt(offset + 1) == '\n') {
                return offset;
            }
            index = offset + 1;
        }
    }
    
    private void parseHeaderLine(final FileItemHeadersImpl headers, final String header) {
        final int colonOffset = header.indexOf(58);
        if (colonOffset == -1) {
            return;
        }
        final String headerName = header.substring(0, colonOffset).trim();
        final String headerValue = header.substring(colonOffset + 1).trim();
        headers.addHeader(headerName, headerValue);
    }
    
    public ProgressListener getProgressListener() {
        return this.listener;
    }
    
    public void setProgressListener(final ProgressListener pListener) {
        this.listener = pListener;
    }
}
