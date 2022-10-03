package com.adventnet.iam.security;

import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class OutputEncodingTestFilter implements Filter
{
    private static final Logger LOGGER;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain fc) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final OEResponseWrapper wrapperRes = new OEResponseWrapper(response);
        try {
            fc.doFilter((ServletRequest)request, (ServletResponse)wrapperRes);
            wrapperRes.flushBuffer();
        }
        finally {
            if (wrapperRes.getContentType() != null && wrapperRes.getContentType().startsWith("text/")) {
                final byte[] byteData = wrapperRes.getData();
                final String responseStr = new String(byteData, response.getCharacterEncoding());
                if (responseStr.indexOf("<xss'\">") != -1) {
                    OutputEncodingTestFilter.LOGGER.log(Level.INFO, "HIGH: output encoding not done. {0} \n CONTENT: {1}", new Object[] { request.getRequestURI(), responseStr });
                }
            }
        }
    }
    
    public void destroy() {
    }
    
    static {
        LOGGER = Logger.getLogger(OutputEncodingTestFilter.class.getName());
    }
    
    public class OEResponseWrapper extends HttpServletResponseWrapper
    {
        private ServletOutputStream servletOPStream;
        private PrintWriter writer;
        private ServletOutputStreamData streamData;
        
        public OEResponseWrapper(final HttpServletResponse response) throws IOException {
            super(response);
        }
        
        public ServletOutputStream getOutputStream() throws IOException {
            if (this.writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }
            if (this.servletOPStream == null) {
                this.servletOPStream = this.getResponse().getOutputStream();
                this.streamData = new ServletOutputStreamData((OutputStream)this.servletOPStream);
            }
            return this.streamData;
        }
        
        public PrintWriter getWriter() throws IOException {
            if (this.servletOPStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }
            if (this.writer == null) {
                this.streamData = new ServletOutputStreamData((OutputStream)this.getResponse().getOutputStream());
                this.writer = new PrintWriter(new OutputStreamWriter((OutputStream)this.streamData, this.getResponse().getCharacterEncoding()), true);
            }
            return this.writer;
        }
        
        public void flushBuffer() throws IOException {
            if (this.writer != null) {
                this.writer.flush();
            }
            else if (this.servletOPStream != null) {
                this.streamData.flush();
            }
        }
        
        public byte[] getData() {
            if (this.streamData != null) {
                return this.streamData.getData();
            }
            return new byte[0];
        }
        
        public void setContentLengthLong(final long len) {
        }
    }
    
    public class ServletOutputStreamData extends ServletOutputStream
    {
        private OutputStream opStream;
        private ByteArrayOutputStream byteData;
        
        public ServletOutputStreamData(final OutputStream opStream) {
            this.opStream = opStream;
            this.byteData = new ByteArrayOutputStream(1024);
        }
        
        public void write(final int b) throws IOException {
            this.opStream.write(b);
            this.byteData.write(b);
        }
        
        public byte[] getData() {
            return this.byteData.toByteArray();
        }
        
        public void setWriteListener(final WriteListener listener) {
        }
        
        public boolean isReady() {
            return false;
        }
    }
}
