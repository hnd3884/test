package com.adventnet.iam.security;

import java.util.Map;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import java.util.Locale;
import java.util.Collection;
import java.io.Closeable;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.fileupload.disk.DiskFileItem;
import javax.servlet.http.Part;

public class UploadedFilePart extends UploadedFileItem implements Part
{
    UploadedFilePart(final String fileName, final String fieldName, final String contentType) {
        super(fileName, fieldName, contentType, null);
    }
    
    public InputStream getInputStream() throws IOException {
        return this.isValidated() ? new FileInputStream(this.getUploadedFileForValidation()) : null;
    }
    
    public String getContentType() {
        return this.getDetectedContentType();
    }
    
    public String getName() {
        return this.getFieldName();
    }
    
    public String getSubmittedFileName() {
        return this.getFileName();
    }
    
    public long getSize() {
        return this.getFileSize();
    }
    
    public void write(final String filePath) throws IOException {
        if (!this.isValidated() || !SecurityUtil.isValid(filePath)) {
            throw new IOException("Part not yet validated");
        }
        final File file = new File(filePath);
        if (!file.isAbsolute()) {
            throw new IllegalArgumentException("Invalid path " + filePath);
        }
        if (file.exists() && !file.delete()) {
            throw new IOException("Cannot write uploaded file to disk!");
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(this.getUploadedFile()));
            out = new BufferedOutputStream(new FileOutputStream(file));
            IOUtils.copy((InputStream)in, (OutputStream)out);
            out.close();
        }
        finally {
            IOUtils.closeQuietly((Closeable)in);
            IOUtils.closeQuietly((Closeable)out);
        }
    }
    
    @Override
    public DiskFileItem getDiskFileItem() {
        return null;
    }
    
    public void delete() throws IOException {
        this.deleteFile();
    }
    
    public Collection<String> getHeaderNames() {
        return (this.getHeaderFields() != null) ? this.getHeaderFields().keySet() : null;
    }
    
    public static String getSubmittedFileName(final Part part) {
        String fileName = null;
        final String cd = part.getHeader("Content-Disposition");
        if (cd != null) {
            final String cdl = cd.toLowerCase(Locale.ENGLISH);
            if (cdl.startsWith("form-data") || cdl.startsWith("attachment")) {
                final ParameterParser paramParser = new ParameterParser();
                paramParser.setLowerCaseNames(true);
                final Map<String, String> params = paramParser.parse(cd, ';');
                if (params.containsKey("filename")) {
                    fileName = params.get("filename");
                    if (fileName != null) {
                        if (fileName.indexOf(92) > -1) {
                            fileName = HttpParser.unquote(fileName.trim());
                        }
                        else {
                            fileName = fileName.trim();
                        }
                    }
                    else {
                        fileName = "";
                    }
                }
            }
        }
        return fileName;
    }
}
