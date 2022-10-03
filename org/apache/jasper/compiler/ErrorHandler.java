package org.apache.jasper.compiler;

import org.apache.jasper.JasperException;

public interface ErrorHandler
{
    void jspError(final String p0, final int p1, final int p2, final String p3, final Exception p4) throws JasperException;
    
    void jspError(final String p0, final Exception p1) throws JasperException;
    
    void javacError(final JavacErrorDetail[] p0) throws JasperException;
    
    void javacError(final String p0, final Exception p1) throws JasperException;
}
