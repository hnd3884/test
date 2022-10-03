package javax.sql.rowset.serial;

import java.sql.SQLException;

public class SerialException extends SQLException
{
    static final long serialVersionUID = -489794565168592690L;
    
    public SerialException() {
    }
    
    public SerialException(final String s) {
        super(s);
    }
}
