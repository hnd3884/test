package javax.servlet.jsp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface HttpJspPage extends JspPage
{
    void _jspService(final HttpServletRequest p0, final HttpServletResponse p1) throws ServletException, IOException;
}
