package javax.xml.stream;

public class XMLStreamException extends Exception
{
    private static final long serialVersionUID = 2018819321811497362L;
    protected Throwable nested;
    protected Location location;
    
    public XMLStreamException() {
    }
    
    public XMLStreamException(final String s) {
        super(s);
    }
    
    public XMLStreamException(final Throwable nested) {
        this.nested = nested;
    }
    
    public XMLStreamException(final String s, final Throwable nested) {
        super(s);
        this.nested = nested;
    }
    
    public XMLStreamException(final String s, final Location location, final Throwable nested) {
        super(s);
        this.location = location;
        this.nested = nested;
    }
    
    public XMLStreamException(final String s, final Location location) {
        super(s);
        this.location = location;
    }
    
    public Throwable getNestedException() {
        return this.nested;
    }
    
    public Location getLocation() {
        return this.location;
    }
}
