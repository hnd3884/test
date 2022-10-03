package com.adventnet.iam.security.antivirus.restapi.virustotal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

public class MultipartEntity
{
    public static final String BOUNDARY = "e2a540ab4e6c5ed79c01157c255a2b5007e157d7";
    public static final String LINE_FEED = "\r\n";
    public static final String CHARSET = "UTF-8";
    private final ByteArrayOutputStream outputStream;
    private final PrintWriter writer;
    
    public MultipartEntity() {
        this.outputStream = new ByteArrayOutputStream();
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(this.outputStream, "UTF-8"), true);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException();
        }
    }
    
    public void addHeader(final String name, final String value) {
        this.writer.append(name + ": " + value).append("\r\n");
        this.writer.flush();
    }
    
    public void addTextBody(final String type, final String value) {
        this.writer.append("--e2a540ab4e6c5ed79c01157c255a2b5007e157d7").append("\r\n");
        this.writer.append("Content-Disposition: form-data; name=\"").append(type).append('\"').append("\r\n");
        this.writer.append("Content-Type: text/plain; charset=").append("UTF-8").append("\r\n");
        this.writer.append("\r\n");
        this.writer.append(value).append("\r\n");
        this.writer.flush();
    }
    
    public void addBinaryBody(final String type, final String fileName, final FileInputStream inputStream) throws IOException {
        this.writer.append("--e2a540ab4e6c5ed79c01157c255a2b5007e157d7").append("\r\n");
        this.writer.append("Content-Disposition: form-data; name=\"").append(type).append("\"; filename=\"").append(fileName).append('\"').append("\r\n");
        this.writer.append("Content-Type: application/octet-stream").append("\r\n");
        this.writer.append("Content-Transfer-Encoding: chunked").append("\r\n");
        this.writer.append("\r\n");
        this.writer.flush();
        final byte[] buffer = new byte[1048576];
        int bytes;
        while ((bytes = inputStream.read(buffer)) != -1) {
            this.outputStream.write(buffer, 0, bytes);
        }
        this.outputStream.flush();
        this.writer.flush();
    }
    
    public void addBinaryBody(final String type, final File file) throws IOException {
        final String fileName = file.getName();
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            this.addBinaryBody(type, fileName, fileInputStream);
        }
    }
    
    public InputStream getContent() throws IOException {
        this.writer.append("\r\n").flush();
        this.writer.append("--e2a540ab4e6c5ed79c01157c255a2b5007e157d7--").append("\r\n");
        this.writer.close();
        this.outputStream.flush();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(this.outputStream.toByteArray());
        this.outputStream.close();
        return inputStream;
    }
}
