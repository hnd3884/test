package javax.servlet.jsp.el;

import java.lang.reflect.Method;

public interface FunctionMapper
{
    Method resolveFunction(final String p0, final String p1);
}
