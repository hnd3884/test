package HTTPClient;

import java.io.IOException;

public class SocksException extends IOException
{
    public SocksException() {
    }
    
    public SocksException(final String s) {
        super(s);
    }
}
