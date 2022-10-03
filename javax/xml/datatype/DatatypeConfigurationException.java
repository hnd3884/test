package javax.xml.datatype;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class DatatypeConfigurationException extends Exception
{
    private static final long serialVersionUID = -1699373159027047238L;
    private Throwable causeOnJDK13OrBelow;
    private transient boolean isJDK14OrAbove;
    
    public DatatypeConfigurationException() {
        this.isJDK14OrAbove = false;
    }
    
    public DatatypeConfigurationException(final String s) {
        super(s);
        this.isJDK14OrAbove = false;
    }
    
    public DatatypeConfigurationException(final String s, final Throwable t) {
        super(s);
        this.isJDK14OrAbove = false;
        this.initCauseByReflection(t);
    }
    
    public DatatypeConfigurationException(final Throwable t) {
        super((t == null) ? null : t.toString());
        this.isJDK14OrAbove = false;
        this.initCauseByReflection(t);
    }
    
    public void printStackTrace() {
        if (!this.isJDK14OrAbove && this.causeOnJDK13OrBelow != null) {
            this.printStackTrace0(new PrintWriter(System.err, true));
        }
        else {
            super.printStackTrace();
        }
    }
    
    public void printStackTrace(final PrintStream printStream) {
        if (!this.isJDK14OrAbove && this.causeOnJDK13OrBelow != null) {
            this.printStackTrace0(new PrintWriter(printStream));
        }
        else {
            super.printStackTrace(printStream);
        }
    }
    
    public void printStackTrace(final PrintWriter printWriter) {
        if (!this.isJDK14OrAbove && this.causeOnJDK13OrBelow != null) {
            this.printStackTrace0(printWriter);
        }
        else {
            super.printStackTrace(printWriter);
        }
    }
    
    private void printStackTrace0(final PrintWriter printWriter) {
        this.causeOnJDK13OrBelow.printStackTrace(printWriter);
        printWriter.println("------------------------------------------");
        super.printStackTrace(printWriter);
    }
    
    private void initCauseByReflection(final Throwable causeOnJDK13OrBelow) {
        this.causeOnJDK13OrBelow = causeOnJDK13OrBelow;
        try {
            this.getClass().getMethod("initCause", Throwable.class).invoke(this, causeOnJDK13OrBelow);
            this.isJDK14OrAbove = true;
        }
        catch (final Exception ex) {}
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            final Throwable causeOnJDK13OrBelow = (Throwable)this.getClass().getMethod("getCause", (Class<?>[])new Class[0]).invoke(this, new Object[0]);
            if (this.causeOnJDK13OrBelow == null) {
                this.causeOnJDK13OrBelow = causeOnJDK13OrBelow;
            }
            else if (causeOnJDK13OrBelow == null) {
                this.getClass().getMethod("initCause", Throwable.class).invoke(this, this.causeOnJDK13OrBelow);
            }
            this.isJDK14OrAbove = true;
        }
        catch (final Exception ex) {}
    }
}
