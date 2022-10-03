package org.apache.tomcat.util.http.fileupload.impl;

import java.util.ArrayList;
import org.apache.tomcat.util.http.fileupload.FileItem;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import java.io.Closeable;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import java.io.InputStream;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.apache.tomcat.util.http.fileupload.UploadContext;
import java.util.Locale;
import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;

public class FileItemIteratorImpl implements FileItemIterator
{
    private final FileUploadBase fileUploadBase;
    private final RequestContext ctx;
    private long sizeMax;
    private long fileSizeMax;
    private MultipartStream multiPartStream;
    private MultipartStream.ProgressNotifier progressNotifier;
    private byte[] multiPartBoundary;
    private FileItemStreamImpl currentItem;
    private String currentFieldName;
    private boolean skipPreamble;
    private boolean itemValid;
    private boolean eof;
    
    @Override
    public long getSizeMax() {
        return this.sizeMax;
    }
    
    @Override
    public void setSizeMax(final long sizeMax) {
        this.sizeMax = sizeMax;
    }
    
    @Override
    public long getFileSizeMax() {
        return this.fileSizeMax;
    }
    
    @Override
    public void setFileSizeMax(final long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }
    
    public FileItemIteratorImpl(final FileUploadBase fileUploadBase, final RequestContext requestContext) throws FileUploadException, IOException {
        this.fileUploadBase = fileUploadBase;
        this.sizeMax = fileUploadBase.getSizeMax();
        this.fileSizeMax = fileUploadBase.getFileSizeMax();
        this.ctx = Objects.requireNonNull(requestContext, "requestContext");
        this.skipPreamble = true;
        this.findNextItem();
    }
    
    protected void init(final FileUploadBase fileUploadBase, final RequestContext pRequestContext) throws FileUploadException, IOException {
        final String contentType = this.ctx.getContentType();
        if (null == contentType || !contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/")) {
            throw new InvalidContentTypeException(String.format("the request doesn't contain a %s or %s stream, content type header is %s", "multipart/form-data", "multipart/mixed", contentType));
        }
        final long requestSize = ((UploadContext)this.ctx).contentLength();
        InputStream input;
        if (this.sizeMax >= 0L) {
            if (requestSize != -1L && requestSize > this.sizeMax) {
                throw new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", requestSize, this.sizeMax), requestSize, this.sizeMax);
            }
            input = new LimitedInputStream(this.ctx.getInputStream(), this.sizeMax) {
                @Override
                protected void raiseError(final long pSizeMax, final long pCount) throws IOException {
                    final FileUploadException ex = new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", pCount, pSizeMax), pCount, pSizeMax);
                    throw new FileUploadIOException(ex);
                }
            };
        }
        else {
            input = this.ctx.getInputStream();
        }
        String charEncoding = fileUploadBase.getHeaderEncoding();
        if (charEncoding == null) {
            charEncoding = this.ctx.getCharacterEncoding();
        }
        this.multiPartBoundary = fileUploadBase.getBoundary(contentType);
        if (this.multiPartBoundary == null) {
            IOUtils.closeQuietly(input);
            throw new FileUploadException("the request was rejected because no multipart boundary was found");
        }
        this.progressNotifier = new MultipartStream.ProgressNotifier(fileUploadBase.getProgressListener(), requestSize);
        try {
            this.multiPartStream = new MultipartStream(input, this.multiPartBoundary, this.progressNotifier);
        }
        catch (final IllegalArgumentException iae) {
            IOUtils.closeQuietly(input);
            throw new InvalidContentTypeException(String.format("The boundary specified in the %s header is too long", "Content-type"), iae);
        }
        this.multiPartStream.setHeaderEncoding(charEncoding);
    }
    
    public MultipartStream getMultiPartStream() throws FileUploadException, IOException {
        if (this.multiPartStream == null) {
            this.init(this.fileUploadBase, this.ctx);
        }
        return this.multiPartStream;
    }
    
    private boolean findNextItem() throws FileUploadException, IOException {
        if (this.eof) {
            return false;
        }
        if (this.currentItem != null) {
            this.currentItem.close();
            this.currentItem = null;
        }
        final MultipartStream multi = this.getMultiPartStream();
        while (true) {
            boolean nextPart;
            if (this.skipPreamble) {
                nextPart = multi.skipPreamble();
            }
            else {
                nextPart = multi.readBoundary();
            }
            if (!nextPart) {
                if (this.currentFieldName == null) {
                    this.eof = true;
                    return false;
                }
                multi.setBoundary(this.multiPartBoundary);
                this.currentFieldName = null;
            }
            else {
                final FileItemHeaders headers = this.fileUploadBase.getParsedHeaders(multi.readHeaders());
                if (this.currentFieldName == null) {
                    final String fieldName = this.fileUploadBase.getFieldName(headers);
                    if (fieldName != null) {
                        final String subContentType = headers.getHeader("Content-type");
                        if (subContentType != null && subContentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/mixed")) {
                            this.currentFieldName = fieldName;
                            final byte[] subBoundary = this.fileUploadBase.getBoundary(subContentType);
                            multi.setBoundary(subBoundary);
                            this.skipPreamble = true;
                            continue;
                        }
                        final String fileName = this.fileUploadBase.getFileName(headers);
                        (this.currentItem = new FileItemStreamImpl(this, fileName, fieldName, headers.getHeader("Content-type"), fileName == null, this.getContentLength(headers))).setHeaders(headers);
                        this.progressNotifier.noteItem();
                        return this.itemValid = true;
                    }
                }
                else {
                    final String fileName2 = this.fileUploadBase.getFileName(headers);
                    if (fileName2 != null) {
                        (this.currentItem = new FileItemStreamImpl(this, fileName2, this.currentFieldName, headers.getHeader("Content-type"), false, this.getContentLength(headers))).setHeaders(headers);
                        this.progressNotifier.noteItem();
                        return this.itemValid = true;
                    }
                }
                multi.discardBodyData();
            }
        }
    }
    
    private long getContentLength(final FileItemHeaders pHeaders) {
        try {
            return Long.parseLong(pHeaders.getHeader("Content-length"));
        }
        catch (final Exception e) {
            return -1L;
        }
    }
    
    @Override
    public boolean hasNext() throws FileUploadException, IOException {
        if (this.eof) {
            return false;
        }
        if (this.itemValid) {
            return true;
        }
        try {
            return this.findNextItem();
        }
        catch (final FileUploadIOException e) {
            throw (FileUploadException)e.getCause();
        }
    }
    
    @Override
    public FileItemStream next() throws FileUploadException, IOException {
        if (this.eof || (!this.itemValid && !this.hasNext())) {
            throw new NoSuchElementException();
        }
        this.itemValid = false;
        return this.currentItem;
    }
    
    @Override
    public List<FileItem> getFileItems() throws FileUploadException, IOException {
        final List<FileItem> items = new ArrayList<FileItem>();
        while (this.hasNext()) {
            final FileItemStream fis = this.next();
            final FileItem fi = this.fileUploadBase.getFileItemFactory().createItem(fis.getFieldName(), fis.getContentType(), fis.isFormField(), fis.getName());
            items.add(fi);
        }
        return items;
    }
}
