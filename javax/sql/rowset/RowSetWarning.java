package javax.sql.rowset;

import java.sql.SQLException;

public class RowSetWarning extends SQLException
{
    static final long serialVersionUID = 6678332766434564774L;
    
    public RowSetWarning(final String s) {
        super(s);
    }
    
    public RowSetWarning() {
    }
    
    public RowSetWarning(final String s, final String s2) {
        super(s, s2);
    }
    
    public RowSetWarning(final String s, final String s2, final int n) {
        super(s, s2, n);
    }
    
    public RowSetWarning getNextWarning() {
        final SQLException nextException = this.getNextException();
        if (nextException == null || nextException instanceof RowSetWarning) {
            return (RowSetWarning)nextException;
        }
        throw new Error("RowSetWarning chain holds value that is not a RowSetWarning: ");
    }
    
    public void setNextWarning(final RowSetWarning nextException) {
        this.setNextException(nextException);
    }
}
