package javax.activation;

import java.io.IOException;

public class UnsupportedDataTypeException extends IOException
{
    public UnsupportedDataTypeException() {
    }
    
    public UnsupportedDataTypeException(final String s) {
        super(s);
    }
}
