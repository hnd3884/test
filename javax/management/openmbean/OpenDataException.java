package javax.management.openmbean;

import javax.management.JMException;

public class OpenDataException extends JMException
{
    private static final long serialVersionUID = 8346311255433349870L;
    
    public OpenDataException() {
    }
    
    public OpenDataException(final String s) {
        super(s);
    }
}
