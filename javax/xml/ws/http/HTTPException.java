package javax.xml.ws.http;

import javax.xml.ws.ProtocolException;

public class HTTPException extends ProtocolException
{
    private int statusCode;
    
    public HTTPException(final int statusCode) {
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
}
