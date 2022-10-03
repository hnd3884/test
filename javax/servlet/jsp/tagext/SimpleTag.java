package javax.servlet.jsp.tagext;

import javax.servlet.jsp.JspContext;
import java.io.IOException;
import javax.servlet.jsp.JspException;

public interface SimpleTag extends JspTag
{
    void doTag() throws JspException, IOException;
    
    void setParent(final JspTag p0);
    
    JspTag getParent();
    
    void setJspContext(final JspContext p0);
    
    void setJspBody(final JspFragment p0);
}
