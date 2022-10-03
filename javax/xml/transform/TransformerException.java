package javax.xml.transform;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class TransformerException extends Exception
{
    private static final long serialVersionUID = 975798773772956428L;
    SourceLocator locator;
    Throwable containedException;
    
    public SourceLocator getLocator() {
        return this.locator;
    }
    
    public void setLocator(final SourceLocator locator) {
        this.locator = locator;
    }
    
    public Throwable getException() {
        return this.containedException;
    }
    
    public Throwable getCause() {
        return (this.containedException == this) ? null : this.containedException;
    }
    
    public synchronized Throwable initCause(final Throwable containedException) {
        if (this.containedException != null) {
            throw new IllegalStateException("Can't overwrite cause");
        }
        if (containedException == this) {
            throw new IllegalArgumentException("Self-causation not permitted");
        }
        this.containedException = containedException;
        return this;
    }
    
    public TransformerException(final String s) {
        super(s);
        this.containedException = null;
        this.locator = null;
    }
    
    public TransformerException(final Throwable containedException) {
        super(containedException.toString());
        this.containedException = containedException;
        this.locator = null;
    }
    
    public TransformerException(final String s, final Throwable containedException) {
        super((s == null || s.length() == 0) ? containedException.toString() : s);
        this.containedException = containedException;
        this.locator = null;
    }
    
    public TransformerException(final String s, final SourceLocator locator) {
        super(s);
        this.containedException = null;
        this.locator = locator;
    }
    
    public TransformerException(final String s, final SourceLocator locator, final Throwable containedException) {
        super(s);
        this.containedException = containedException;
        this.locator = locator;
    }
    
    public String getMessageAndLocation() {
        final StringBuffer sb = new StringBuffer();
        final String message = super.getMessage();
        if (null != message) {
            sb.append(message);
        }
        if (null != this.locator) {
            final String systemId = this.locator.getSystemId();
            final int lineNumber = this.locator.getLineNumber();
            final int columnNumber = this.locator.getColumnNumber();
            if (null != systemId) {
                sb.append("; SystemID: ");
                sb.append(systemId);
            }
            if (lineNumber != 0) {
                sb.append("; Line#: ");
                sb.append(lineNumber);
            }
            if (columnNumber != 0) {
                sb.append("; Column#: ");
                sb.append(columnNumber);
            }
        }
        return sb.toString();
    }
    
    public String getLocationAsString() {
        if (null != this.locator) {
            final StringBuffer sb = new StringBuffer();
            final String systemId = this.locator.getSystemId();
            final int lineNumber = this.locator.getLineNumber();
            final int columnNumber = this.locator.getColumnNumber();
            if (null != systemId) {
                sb.append("; SystemID: ");
                sb.append(systemId);
            }
            if (lineNumber != 0) {
                sb.append("; Line#: ");
                sb.append(lineNumber);
            }
            if (columnNumber != 0) {
                sb.append("; Column#: ");
                sb.append(columnNumber);
            }
            return sb.toString();
        }
        return null;
    }
    
    public void printStackTrace() {
        this.printStackTrace(new PrintWriter(System.err, true));
    }
    
    public void printStackTrace(final PrintStream printStream) {
        this.printStackTrace(new PrintWriter(printStream));
    }
    
    public void printStackTrace(PrintWriter printWriter) {
        if (printWriter == null) {
            printWriter = new PrintWriter(System.err, true);
        }
        try {
            final String locationAsString = this.getLocationAsString();
            if (null != locationAsString) {
                printWriter.println(locationAsString);
            }
            super.printStackTrace(printWriter);
        }
        catch (final Throwable t) {}
        boolean b = false;
        try {
            Throwable.class.getMethod("getCause", (Class[])null);
            b = true;
        }
        catch (final NoSuchMethodException ex) {}
        if (!b) {
            Throwable exception = this.getException();
            for (int n = 0; n < 10 && null != exception; ++n) {
                printWriter.println("---------");
                try {
                    if (exception instanceof TransformerException) {
                        final String locationAsString2 = ((TransformerException)exception).getLocationAsString();
                        if (null != locationAsString2) {
                            printWriter.println(locationAsString2);
                        }
                    }
                    exception.printStackTrace(printWriter);
                }
                catch (final Throwable t2) {
                    printWriter.println("Could not print stack trace...");
                }
                try {
                    final Method method = ((TransformerException)exception).getClass().getMethod("getException", (Class<?>[])null);
                    if (null != method) {
                        final TransformerException ex2 = (TransformerException)exception;
                        exception = (Throwable)method.invoke(exception, (Object[])null);
                        if (ex2 == exception) {
                            break;
                        }
                    }
                    else {
                        exception = null;
                    }
                }
                catch (final InvocationTargetException ex3) {
                    exception = null;
                }
                catch (final IllegalAccessException ex4) {
                    exception = null;
                }
                catch (final NoSuchMethodException ex5) {
                    exception = null;
                }
            }
        }
        printWriter.flush();
    }
}
