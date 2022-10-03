package com.zoho.security.agent;

import java.io.InputStream;
import com.adventnet.iam.security.SecurityUtil;
import java.io.FileInputStream;
import java.util.Iterator;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.util.logging.Level;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class HttpCall
{
    private static final Logger LOGGER;
    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";
    private String url;
    private String method;
    private String body;
    private List<MultipartFile> files;
    private Map<String, String> postParams;
    private HttpURLConnection connection;
    private String response;
    private int status;
    
    public HttpCall(final String url, final String method) {
        this.url = null;
        this.method = "GET";
        this.body = null;
        this.files = null;
        this.postParams = null;
        this.connection = null;
        this.response = null;
        this.status = -1;
        this.url = url;
        this.method = method;
        try {
            this.init();
        }
        catch (final IOException e) {
            HttpCall.LOGGER.log(Level.SEVERE, "URL Connection object creation failed for URL : {0}, Exception : {1}", new Object[] { url, e.getMessage() });
        }
    }
    
    public HttpCall(final String url, final String method, final String body) {
        this(url, method);
        this.body = body;
    }
    
    public HttpCall(final String url, final String method, final List<MultipartFile> files) {
        this(url, method);
        this.files = files;
    }
    
    private void init() throws MalformedURLException, IOException {
        (this.connection = (HttpURLConnection)new URL(this.url).openConnection()).setRequestMethod(this.method.toUpperCase());
    }
    
    public void triggerRequest() throws IOException {
        if ("POST".equals(this.method)) {
            if (this.files != null) {
                this.updateMultipartData();
            }
            else if (this.body != null) {
                this.connection.setDoOutput(true);
                final OutputStream os = this.connection.getOutputStream();
                os.write(this.body.getBytes());
                os.flush();
                os.close();
            }
        }
    }
    
    public void updateMultipartData() throws IOException {
        final String boundary = Long.toHexString(System.currentTimeMillis());
        this.connection.setDoOutput(true);
        this.connection.setRequestMethod(this.method);
        this.connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        PrintWriter writer = null;
        try {
            final OutputStream output = this.connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
            if (this.files != null) {
                for (final MultipartFile file : this.files) {
                    this.appendMultiPartFile(file, boundary, writer);
                }
            }
            if (this.postParams != null) {
                for (final String paramName : this.postParams.keySet()) {
                    this.appendMultiPartParam(writer, boundary, paramName, this.postParams.get(paramName));
                }
            }
            writer.append("\r\n");
            writer.append("--" + boundary + "--").append("\r\n");
            writer.flush();
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public void appendMultiPartFile(final MultipartFile file, final String boundary, final PrintWriter writer) throws IOException {
        FileInputStream inputStream = null;
        try {
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"" + file.getFieldName() + "\"");
            if (file.getFile() != null) {
                final String fileName = file.getFile().getName();
                writer.append("; filename=\"" + fileName + "\"").append("\r\n");
                writer.append("Content-Type: " + file.getContentType()).append("\r\n");
                writer.append("\r\n");
                writer.flush();
                inputStream = new FileInputStream(file.getFile());
                final String fileContent = SecurityUtil.convertInputStreamAsString((InputStream)inputStream, -1L);
                writer.append(fileContent);
                writer.append("\r\n");
                writer.flush();
            }
            else {
                writer.append("; filename=\"\"").append("\r\n");
                writer.append("Content-Type: application/octet-stream").append("\r\n").append("\r\n");
                writer.flush();
            }
        }
        catch (final IOException ex) {
            throw ex;
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    private void appendMultiPartParam(final PrintWriter writer, final String boundary, final String name, final String value) {
        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append("\r\n");
        writer.append("Content-Type: text/plain; charset=UTF-8").append("\r\n");
        writer.append("\r\n");
        writer.append(value).append("\r\n").flush();
    }
    
    public int getResponseCode() throws IOException {
        if (this.status == -1) {
            this.status = this.connection.getResponseCode();
        }
        return this.status;
    }
    
    public String getResponse() throws IOException {
        if (this.status == -1) {
            this.getResponseCode();
        }
        if (200 == this.status) {
            this.response = SecurityUtil.convertInputStreamAsString(this.connection.getInputStream(), -1L);
        }
        return this.response;
    }
    
    static {
        LOGGER = Logger.getLogger(HttpCall.class.getName());
    }
}
