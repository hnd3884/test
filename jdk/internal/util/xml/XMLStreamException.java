package jdk.internal.util.xml;

public class XMLStreamException extends Exception
{
    private static final long serialVersionUID = 1L;
    protected Throwable nested;
    
    public XMLStreamException() {
    }
    
    public XMLStreamException(final String s) {
        super(s);
    }
    
    public XMLStreamException(final Throwable nested) {
        super(nested);
        this.nested = nested;
    }
    
    public XMLStreamException(final String s, final Throwable nested) {
        super(s, nested);
        this.nested = nested;
    }
    
    public Throwable getNestedException() {
        return this.nested;
    }
}
