package org.apache.commons.fileupload.disk;

import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.fileupload.FileUploadException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.util.Streams;
import java.util.Map;
import org.apache.commons.fileupload.ParameterParser;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.io.output.DeferredFileOutputStream;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.fileupload.FileItem;

public class DiskFileItem implements FileItem
{
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    private static final String UID;
    private static final AtomicInteger COUNTER;
    private String fieldName;
    private final String contentType;
    private boolean isFormField;
    private final String fileName;
    private long size;
    private final int sizeThreshold;
    private final File repository;
    private byte[] cachedContent;
    private transient DeferredFileOutputStream dfos;
    private transient File tempFile;
    private FileItemHeaders headers;
    private String defaultCharset;
    
    public DiskFileItem(final String fieldName, final String contentType, final boolean isFormField, final String fileName, final int sizeThreshold, final File repository) {
        this.size = -1L;
        this.defaultCharset = "ISO-8859-1";
        this.fieldName = fieldName;
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.isInMemory()) {
            return new FileInputStream(this.dfos.getFile());
        }
        if (this.cachedContent == null) {
            this.cachedContent = this.dfos.getData();
        }
        return new ByteArrayInputStream(this.cachedContent);
    }
    
    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    public String getCharSet() {
        final ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        final Map<String, String> params = parser.parse(this.getContentType(), ';');
        return params.get("charset");
    }
    
    @Override
    public String getName() {
        return Streams.checkFileName(this.fileName);
    }
    
    @Override
    public boolean isInMemory() {
        return this.cachedContent != null || this.dfos.isInMemory();
    }
    
    @Override
    public long getSize() {
        if (this.size >= 0L) {
            return this.size;
        }
        if (this.cachedContent != null) {
            return this.cachedContent.length;
        }
        if (this.dfos.isInMemory()) {
            return this.dfos.getData().length;
        }
        return this.dfos.getFile().length();
    }
    
    @Override
    public byte[] get() {
        if (this.isInMemory()) {
            if (this.cachedContent == null && this.dfos != null) {
                this.cachedContent = this.dfos.getData();
            }
            return this.cachedContent;
        }
        byte[] fileData = new byte[(int)this.getSize()];
        InputStream fis = null;
        try {
            fis = new FileInputStream(this.dfos.getFile());
            IOUtils.readFully(fis, fileData);
        }
        catch (final IOException e) {
            fileData = null;
        }
        finally {
            IOUtils.closeQuietly(fis);
        }
        return fileData;
    }
    
    @Override
    public String getString(final String charset) throws UnsupportedEncodingException {
        return new String(this.get(), charset);
    }
    
    @Override
    public String getString() {
        final byte[] rawdata = this.get();
        String charset = this.getCharSet();
        if (charset == null) {
            charset = this.defaultCharset;
        }
        try {
            return new String(rawdata, charset);
        }
        catch (final UnsupportedEncodingException e) {
            return new String(rawdata);
        }
    }
    
    @Override
    public void write(final File file) throws Exception {
        if (this.isInMemory()) {
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(this.get());
                fout.close();
            }
            finally {
                IOUtils.closeQuietly((OutputStream)fout);
            }
        }
        else {
            final File outputFile = this.getStoreLocation();
            if (outputFile == null) {
                throw new FileUploadException("Cannot write uploaded file to disk!");
            }
            this.size = outputFile.length();
            FileUtils.moveFile(outputFile, file);
        }
    }
    
    @Override
    public void delete() {
        this.cachedContent = null;
        final File outputFile = this.getStoreLocation();
        if (outputFile != null && !this.isInMemory() && outputFile.exists()) {
            outputFile.delete();
        }
    }
    
    @Override
    public String getFieldName() {
        return this.fieldName;
    }
    
    @Override
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    @Override
    public boolean isFormField() {
        return this.isFormField;
    }
    
    @Override
    public void setFormField(final boolean state) {
        this.isFormField = state;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.dfos == null) {
            final File outputFile = this.getTempFile();
            this.dfos = new DeferredFileOutputStream(this.sizeThreshold, outputFile);
        }
        return (OutputStream)this.dfos;
    }
    
    public File getStoreLocation() {
        if (this.dfos == null) {
            return null;
        }
        if (this.isInMemory()) {
            return null;
        }
        return this.dfos.getFile();
    }
    
    @Override
    protected void finalize() {
        if (this.dfos == null || this.dfos.isInMemory()) {
            return;
        }
        final File outputFile = this.dfos.getFile();
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
        }
    }
    
    protected File getTempFile() {
        if (this.tempFile == null) {
            File tempDir = this.repository;
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"));
            }
            final String tempFileName = String.format("upload_%s_%s.tmp", DiskFileItem.UID, getUniqueId());
            this.tempFile = new File(tempDir, tempFileName);
        }
        return this.tempFile;
    }
    
    private static String getUniqueId() {
        final int limit = 100000000;
        final int current = DiskFileItem.COUNTER.getAndIncrement();
        String id = Integer.toString(current);
        if (current < 100000000) {
            id = ("00000000" + id).substring(id.length());
        }
        return id;
    }
    
    @Override
    public String toString() {
        return String.format("name=%s, StoreLocation=%s, size=%s bytes, isFormField=%s, FieldName=%s", this.getName(), this.getStoreLocation(), this.getSize(), this.isFormField(), this.getFieldName());
    }
    
    @Override
    public FileItemHeaders getHeaders() {
        return this.headers;
    }
    
    @Override
    public void setHeaders(final FileItemHeaders pHeaders) {
        this.headers = pHeaders;
    }
    
    public String getDefaultCharset() {
        return this.defaultCharset;
    }
    
    public void setDefaultCharset(final String charset) {
        this.defaultCharset = charset;
    }
    
    static {
        UID = UUID.randomUUID().toString().replace('-', '_');
        COUNTER = new AtomicInteger(0);
    }
}
