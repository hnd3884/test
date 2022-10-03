package org.owasp.esapi.waf.internal;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import java.util.Enumeration;
import org.apache.commons.fileupload.util.Streams;
import java.io.File;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.http.HttpServletRequest;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import java.io.RandomAccessFile;
import java.util.Vector;
import javax.servlet.http.HttpServletRequestWrapper;

public class InterceptingHTTPServletRequest extends HttpServletRequestWrapper
{
    private Vector<Parameter> allParameters;
    private Vector<String> allParameterNames;
    private static int CHUNKED_BUFFER_SIZE;
    private boolean isMultipart;
    private RandomAccessFile requestBody;
    private RAFInputStream is;
    
    public ServletInputStream getInputStream() throws IOException {
        if (this.isMultipart) {
            return this.is;
        }
        return super.getInputStream();
    }
    
    public BufferedReader getReader() throws IOException {
        String enc = this.getCharacterEncoding();
        if (enc == null) {
            enc = "UTF-8";
        }
        return new BufferedReader(new InputStreamReader((InputStream)this.getInputStream(), enc));
    }
    
    public InterceptingHTTPServletRequest(final HttpServletRequest request) throws FileUploadException, IOException {
        super(request);
        this.isMultipart = false;
        this.allParameters = new Vector<Parameter>();
        this.allParameterNames = new Vector<String>();
        final Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            final String param = e.nextElement();
            this.allParameters.add(new Parameter(param, request.getParameter(param), false));
            this.allParameterNames.add(param);
        }
        this.isMultipart = ServletFileUpload.isMultipartContent(request);
        if (this.isMultipart) {
            this.requestBody = new RandomAccessFile(File.createTempFile("oew", "mpc"), "rw");
            final byte[] buffer = new byte[InterceptingHTTPServletRequest.CHUNKED_BUFFER_SIZE];
            long size = 0L;
            int len = 0;
            while (len != -1 && size <= 2147483647L) {
                len = request.getInputStream().read(buffer, 0, InterceptingHTTPServletRequest.CHUNKED_BUFFER_SIZE);
                if (len != -1) {
                    size += len;
                    this.requestBody.write(buffer, 0, len);
                }
            }
            this.is = new RAFInputStream(this.requestBody);
            final ServletFileUpload sfu = new ServletFileUpload();
            final FileItemIterator iter = sfu.getItemIterator((HttpServletRequest)this);
            while (iter.hasNext()) {
                final FileItemStream item = iter.next();
                final String name = item.getFieldName();
                final InputStream stream = item.openStream();
                if (item.isFormField()) {
                    final String value = Streams.asString(stream);
                    this.allParameters.add(new Parameter(name, value, true));
                    this.allParameterNames.add(name);
                }
            }
            this.requestBody.seek(0L);
        }
    }
    
    public String getDictionaryParameter(final String s) {
        for (int i = 0; i < this.allParameters.size(); ++i) {
            final Parameter p = this.allParameters.get(i);
            if (p.getName().equals(s)) {
                return p.getValue();
            }
        }
        return null;
    }
    
    public Enumeration getDictionaryParameterNames() {
        return this.allParameterNames.elements();
    }
    
    static {
        InterceptingHTTPServletRequest.CHUNKED_BUFFER_SIZE = 1024;
    }
    
    private class RAFInputStream extends ServletInputStream
    {
        RandomAccessFile raf;
        
        public RAFInputStream(final RandomAccessFile raf) throws IOException {
            (this.raf = raf).seek(0L);
        }
        
        public int read() throws IOException {
            return this.raf.read();
        }
        
        public synchronized void reset() throws IOException {
            this.raf.seek(0L);
        }
    }
}
