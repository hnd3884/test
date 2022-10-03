package java.beans;

import org.xml.sax.helpers.DefaultHandler;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.Closeable;
import java.security.AccessController;
import java.io.InputStream;
import org.xml.sax.InputSource;
import com.sun.beans.decoder.DocumentHandler;
import java.security.AccessControlContext;

public class XMLDecoder implements AutoCloseable
{
    private final AccessControlContext acc;
    private final DocumentHandler handler;
    private final InputSource input;
    private Object owner;
    private Object[] array;
    private int index;
    
    public XMLDecoder(final InputStream inputStream) {
        this(inputStream, null);
    }
    
    public XMLDecoder(final InputStream inputStream, final Object o) {
        this(inputStream, o, null);
    }
    
    public XMLDecoder(final InputStream inputStream, final Object o, final ExceptionListener exceptionListener) {
        this(inputStream, o, exceptionListener, null);
    }
    
    public XMLDecoder(final InputStream byteStream, final Object o, final ExceptionListener exceptionListener, final ClassLoader classLoader) {
        this(new InputSource(byteStream), o, exceptionListener, classLoader);
    }
    
    public XMLDecoder(final InputSource inputSource) {
        this(inputSource, null, null, null);
    }
    
    private XMLDecoder(final InputSource input, final Object owner, final ExceptionListener exceptionListener, final ClassLoader classLoader) {
        this.acc = AccessController.getContext();
        this.handler = new DocumentHandler();
        this.input = input;
        this.owner = owner;
        this.setExceptionListener(exceptionListener);
        this.handler.setClassLoader(classLoader);
        this.handler.setOwner(this);
    }
    
    @Override
    public void close() {
        if (this.parsingComplete()) {
            this.close(this.input.getCharacterStream());
            this.close(this.input.getByteStream());
        }
    }
    
    private void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException ex) {
                this.getExceptionListener().exceptionThrown(ex);
            }
        }
    }
    
    private boolean parsingComplete() {
        if (this.input == null) {
            return false;
        }
        if (this.array == null) {
            if (this.acc == null && null != System.getSecurityManager()) {
                throw new SecurityException("AccessControlContext is not set");
            }
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    XMLDecoder.this.handler.parse(XMLDecoder.this.input);
                    return null;
                }
            }, this.acc);
            this.array = this.handler.getObjects();
        }
        return true;
    }
    
    public void setExceptionListener(ExceptionListener defaultExceptionListener) {
        if (defaultExceptionListener == null) {
            defaultExceptionListener = Statement.defaultExceptionListener;
        }
        this.handler.setExceptionListener(defaultExceptionListener);
    }
    
    public ExceptionListener getExceptionListener() {
        return this.handler.getExceptionListener();
    }
    
    public Object readObject() {
        return this.parsingComplete() ? this.array[this.index++] : null;
    }
    
    public void setOwner(final Object owner) {
        this.owner = owner;
    }
    
    public Object getOwner() {
        return this.owner;
    }
    
    public static DefaultHandler createHandler(final Object owner, final ExceptionListener exceptionListener, final ClassLoader classLoader) {
        final DocumentHandler documentHandler = new DocumentHandler();
        documentHandler.setOwner(owner);
        documentHandler.setExceptionListener(exceptionListener);
        documentHandler.setClassLoader(classLoader);
        return documentHandler;
    }
}
