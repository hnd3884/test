package javax.servlet.jsp.jstl.sql;

import java.sql.SQLException;
import java.sql.ResultSet;

public class ResultSupport
{
    public static Result toResult(final ResultSet rs) {
        try {
            return new ResultImpl(rs, -1, -1);
        }
        catch (final SQLException ex) {
            return null;
        }
    }
    
    public static Result toResult(final ResultSet rs, final int maxRows) {
        try {
            return new ResultImpl(rs, -1, maxRows);
        }
        catch (final SQLException ex) {
            return null;
        }
    }
}
