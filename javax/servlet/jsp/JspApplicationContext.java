package javax.servlet.jsp;

import javax.el.ExpressionFactory;
import javax.el.ELResolver;
import javax.el.ELContextListener;

public interface JspApplicationContext
{
    void addELContextListener(final ELContextListener p0);
    
    void addELResolver(final ELResolver p0);
    
    ExpressionFactory getExpressionFactory();
}
