package org.apache.jasper.compiler;

import org.apache.jasper.JspCompilationContext;

public interface ELInterpreter
{
    String interpreterCall(final JspCompilationContext p0, final boolean p1, final String p2, final Class<?> p3, final String p4);
}
