package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBufHolder;
import io.netty.util.ReferenceCounted;
import io.netty.channel.ChannelException;
import io.netty.buffer.ByteBuf;
import java.io.File;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;

public class DiskFileUpload extends AbstractDiskHttpData implements FileUpload
{
    public static String baseDirectory;
    public static boolean deleteOnExitTemporaryFile;
    public static final String prefix = "FUp_";
    public static final String postfix = ".tmp";
    private final String baseDir;
    private final boolean deleteOnExit;
    private String filename;
    private String contentType;
    private String contentTransferEncoding;
    
    public DiskFileUpload(final String name, final String filename, final String contentType, final String contentTransferEncoding, final Charset charset, final long size, final String baseDir, final boolean deleteOnExit) {
        super(name, charset, size);
        this.setFilename(filename);
        this.setContentType(contentType);
        this.setContentTransferEncoding(contentTransferEncoding);
        this.baseDir = ((baseDir == null) ? DiskFileUpload.baseDirectory : baseDir);
        this.deleteOnExit = deleteOnExit;
    }
    
    public DiskFileUpload(final String name, final String filename, final String contentType, final String contentTransferEncoding, final Charset charset, final long size) {
        this(name, filename, contentType, contentTransferEncoding, charset, size, DiskFileUpload.baseDirectory, DiskFileUpload.deleteOnExitTemporaryFile);
    }
    
    @Override
    public InterfaceHttpData.HttpDataType getHttpDataType() {
        return InterfaceHttpData.HttpDataType.FileUpload;
    }
    
    @Override
    public String getFilename() {
        return this.filename;
    }
    
    @Override
    public void setFilename(final String filename) {
        this.filename = ObjectUtil.checkNotNull(filename, "filename");
    }
    
    @Override
    public int hashCode() {
        return FileUploadUtil.hashCode(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof FileUpload && FileUploadUtil.equals(this, (FileUpload)o);
    }
    
    @Override
    public int compareTo(final InterfaceHttpData o) {
        if (!(o instanceof FileUpload)) {
            throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + o.getHttpDataType());
        }
        return this.compareTo((FileUpload)o);
    }
    
    public int compareTo(final FileUpload o) {
        return FileUploadUtil.compareTo(this, o);
    }
    
    @Override
    public void setContentType(final String contentType) {
        this.contentType = ObjectUtil.checkNotNull(contentType, "contentType");
    }
    
    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    @Override
    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }
    
    @Override
    public void setContentTransferEncoding(final String contentTransferEncoding) {
        this.contentTransferEncoding = contentTransferEncoding;
    }
    
    @Override
    public String toString() {
        File file = null;
        try {
            file = this.getFile();
        }
        catch (final IOException ex) {}
        return (Object)HttpHeaderNames.CONTENT_DISPOSITION + ": " + (Object)HttpHeaderValues.FORM_DATA + "; " + (Object)HttpHeaderValues.NAME + "=\"" + this.getName() + "\"; " + (Object)HttpHeaderValues.FILENAME + "=\"" + this.filename + "\"\r\n" + (Object)HttpHeaderNames.CONTENT_TYPE + ": " + this.contentType + ((this.getCharset() != null) ? ("; " + (Object)HttpHeaderValues.CHARSET + '=' + this.getCharset().name() + "\r\n") : "\r\n") + (Object)HttpHeaderNames.CONTENT_LENGTH + ": " + this.length() + "\r\nCompleted: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory() + "\r\nRealFile: " + ((file != null) ? file.getAbsolutePath() : "null") + " DeleteAfter: " + this.deleteOnExit;
    }
    
    @Override
    protected boolean deleteOnExit() {
        return this.deleteOnExit;
    }
    
    @Override
    protected String getBaseDirectory() {
        return this.baseDir;
    }
    
    @Override
    protected String getDiskFilename() {
        return "upload";
    }
    
    @Override
    protected String getPostfix() {
        return ".tmp";
    }
    
    @Override
    protected String getPrefix() {
        return "FUp_";
    }
    
    @Override
    public FileUpload copy() {
        final ByteBuf content = this.content();
        return this.replace((content != null) ? content.copy() : null);
    }
    
    @Override
    public FileUpload duplicate() {
        final ByteBuf content = this.content();
        return this.replace((content != null) ? content.duplicate() : null);
    }
    
    @Override
    public FileUpload retainedDuplicate() {
        ByteBuf content = this.content();
        if (content != null) {
            content = content.retainedDuplicate();
            boolean success = false;
            try {
                final FileUpload duplicate = this.replace(content);
                success = true;
                return duplicate;
            }
            finally {
                if (!success) {
                    content.release();
                }
            }
        }
        return this.replace(null);
    }
    
    @Override
    public FileUpload replace(final ByteBuf content) {
        final DiskFileUpload upload = new DiskFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size, this.baseDir, this.deleteOnExit);
        if (content != null) {
            try {
                upload.setContent(content);
            }
            catch (final IOException e) {
                throw new ChannelException(e);
            }
        }
        return upload;
    }
    
    @Override
    public FileUpload retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public FileUpload retain() {
        super.retain();
        return this;
    }
    
    @Override
    public FileUpload touch() {
        super.touch();
        return this;
    }
    
    @Override
    public FileUpload touch(final Object hint) {
        super.touch(hint);
        return this;
    }
    
    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
    }
}
