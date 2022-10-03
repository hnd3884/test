package javax.servlet;

import java.util.Locale;
import java.io.PrintWriter;
import java.io.IOException;

public interface ServletResponse
{
    String getCharacterEncoding();
    
    String getContentType();
    
    ServletOutputStream getOutputStream() throws IOException;
    
    PrintWriter getWriter() throws IOException;
    
    void setCharacterEncoding(final String p0);
    
    void setContentLength(final int p0);
    
    void setContentLengthLong(final long p0);
    
    void setContentType(final String p0);
    
    void setBufferSize(final int p0);
    
    int getBufferSize();
    
    void flushBuffer() throws IOException;
    
    void resetBuffer();
    
    boolean isCommitted();
    
    void reset();
    
    void setLocale(final Locale p0);
    
    Locale getLocale();
}
