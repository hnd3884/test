package javax.sql.rowset.serial;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.Serializable;

public class SerialDatalink implements Serializable, Cloneable
{
    private URL url;
    private int baseType;
    private String baseTypeName;
    static final long serialVersionUID = 2826907821828733626L;
    
    public SerialDatalink(final URL url) throws SerialException {
        if (url == null) {
            throw new SerialException("Cannot serialize empty URL instance");
        }
        this.url = url;
    }
    
    public URL getDatalink() throws SerialException {
        URL url;
        try {
            url = new URL(this.url.toString());
        }
        catch (final MalformedURLException ex) {
            throw new SerialException("MalformedURLException: " + ex.getMessage());
        }
        return url;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof SerialDatalink && this.url.equals(((SerialDatalink)o).url));
    }
    
    @Override
    public int hashCode() {
        return 31 + this.url.hashCode();
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
}
