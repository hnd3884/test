package org.apache.jasper.compiler;

import org.apache.jasper.JasperException;

class DefaultErrorHandler implements ErrorHandler
{
    @Override
    public void jspError(final String fname, final int line, final int column, final String errMsg, final Exception ex) throws JasperException {
        throw new JasperException(fname + " (" + Localizer.getMessage("jsp.error.location", Integer.toString(line), Integer.toString(column)) + ") " + errMsg, ex);
    }
    
    @Override
    public void jspError(final String errMsg, final Exception ex) throws JasperException {
        throw new JasperException(errMsg, ex);
    }
    
    @Override
    public void javacError(final JavacErrorDetail[] details) throws JasperException {
        if (details == null) {
            return;
        }
        Object[] args = null;
        final StringBuilder buf = new StringBuilder();
        for (final JavacErrorDetail detail : details) {
            if (detail.getJspBeginLineNumber() >= 0) {
                args = new Object[] { detail.getJspBeginLineNumber(), detail.getJspFileName() };
                buf.append(System.lineSeparator());
                buf.append(System.lineSeparator());
                buf.append(Localizer.getMessage("jsp.error.single.line.number", args));
                buf.append(System.lineSeparator());
                buf.append(detail.getErrorMessage());
                buf.append(System.lineSeparator());
                buf.append(detail.getJspExtract());
            }
            else {
                args = new Object[] { detail.getJavaLineNumber(), detail.getJavaFileName() };
                buf.append(System.lineSeparator());
                buf.append(System.lineSeparator());
                buf.append(Localizer.getMessage("jsp.error.java.line.number", args));
                buf.append(System.lineSeparator());
                buf.append(detail.getErrorMessage());
            }
        }
        buf.append(System.lineSeparator());
        buf.append(System.lineSeparator());
        buf.append("Stacktrace:");
        throw new JasperException(Localizer.getMessage("jsp.error.unable.compile") + ": " + (Object)buf);
    }
    
    @Override
    public void javacError(final String errorReport, final Exception exception) throws JasperException {
        throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"), exception);
    }
}
