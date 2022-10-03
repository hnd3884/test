package HTTPClient;

import java.io.InputStream;
import java.util.Date;
import java.io.IOException;

public interface RoResponse
{
    int getStatusCode() throws IOException;
    
    String getReasonLine() throws IOException;
    
    String getVersion() throws IOException;
    
    String getHeader(final String p0) throws IOException;
    
    int getHeaderAsInt(final String p0) throws IOException, NumberFormatException;
    
    Date getHeaderAsDate(final String p0) throws IOException, IllegalArgumentException;
    
    String getTrailer(final String p0) throws IOException;
    
    int getTrailerAsInt(final String p0) throws IOException, NumberFormatException;
    
    Date getTrailerAsDate(final String p0) throws IOException, IllegalArgumentException;
    
    byte[] getData() throws IOException;
    
    InputStream getInputStream() throws IOException;
}
