package java.util.zip;

import java.io.IOException;

public class ZipException extends IOException
{
    private static final long serialVersionUID = 8000196834066748623L;
    
    public ZipException() {
    }
    
    public ZipException(final String s) {
        super(s);
    }
}
