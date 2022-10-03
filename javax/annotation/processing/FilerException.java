package javax.annotation.processing;

import java.io.IOException;

public class FilerException extends IOException
{
    static final long serialVersionUID = 8426423106453163293L;
    
    public FilerException(final String s) {
        super(s);
    }
}
