package com.maverick.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Vector;
import java.io.OutputStream;
import java.io.InputStream;

public class IOStreamConnector
{
    private InputStream j;
    private OutputStream c;
    private Thread i;
    private long k;
    private boolean d;
    private boolean b;
    boolean e;
    boolean g;
    Throwable h;
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    int f;
    protected Vector listenerList;
    
    public IOStreamConnector() {
        this.j = null;
        this.c = null;
        this.d = true;
        this.b = true;
        this.e = false;
        this.g = false;
        this.f = 32768;
        this.listenerList = new Vector();
    }
    
    public IOStreamConnector(final InputStream inputStream, final OutputStream outputStream) {
        this.j = null;
        this.c = null;
        this.d = true;
        this.b = true;
        this.e = false;
        this.g = false;
        this.f = 32768;
        this.listenerList = new Vector();
        this.connect(inputStream, outputStream);
    }
    
    public void close() {
        if (this.i == null) {
            this.g = true;
        }
        this.e = false;
        if (this.i != null) {
            this.i.interrupt();
        }
    }
    
    public Throwable getLastError() {
        return this.h;
    }
    
    public void setCloseInput(final boolean d) {
        this.d = d;
    }
    
    public void setCloseOutput(final boolean b) {
        this.b = b;
    }
    
    public void setBufferSize(final int f) {
        if (f <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than zero!");
        }
        this.f = f;
    }
    
    public void connect(final InputStream j, final OutputStream c) {
        this.j = j;
        this.c = c;
        (this.i = new Thread(new _b())).setDaemon(true);
        this.i.setName("IOStreamConnector " + j.toString() + ">>" + c.toString());
        this.i.start();
    }
    
    public long getBytes() {
        return this.k;
    }
    
    public boolean isClosed() {
        return this.g;
    }
    
    public void addListener(final IOStreamConnectorListener ioStreamConnectorListener) {
        this.listenerList.addElement(ioStreamConnectorListener);
    }
    
    public void removeListener(final IOStreamConnectorListener ioStreamConnectorListener) {
        this.listenerList.removeElement(ioStreamConnectorListener);
    }
    
    public interface IOStreamConnectorListener
    {
        void connectorClosed(final IOStreamConnector p0);
        
        void connectorTimeout(final IOStreamConnector p0);
        
        void dataTransfered(final byte[] p0, final int p1);
    }
    
    class _b implements Runnable
    {
        public void run() {
            final byte[] array = new byte[IOStreamConnector.this.f];
            IOStreamConnector.this.e = true;
            while (IOStreamConnector.this.e) {
                try {
                    final int read = IOStreamConnector.this.j.read(array, 0, array.length);
                    if (read > 0) {
                        IOStreamConnector.this.c.write(array, 0, read);
                        IOStreamConnector.this.k += read;
                        IOStreamConnector.this.c.flush();
                        for (int i = 0; i < IOStreamConnector.this.listenerList.size(); ++i) {
                            ((IOStreamConnectorListener)IOStreamConnector.this.listenerList.elementAt(i)).dataTransfered(array, read);
                        }
                    }
                    else {
                        if (read >= 0) {
                            continue;
                        }
                        IOStreamConnector.this.e = false;
                    }
                }
                catch (final InterruptedIOException ex) {
                    for (int j = 0; j < IOStreamConnector.this.listenerList.size(); ++j) {
                        ((IOStreamConnectorListener)IOStreamConnector.this.listenerList.elementAt(j)).connectorTimeout(IOStreamConnector.this);
                    }
                }
                catch (final Throwable h) {
                    if (!IOStreamConnector.this.e) {
                        continue;
                    }
                    IOStreamConnector.this.h = h;
                    IOStreamConnector.this.e = false;
                }
            }
            if (IOStreamConnector.this.d) {
                try {
                    IOStreamConnector.this.j.close();
                }
                catch (final IOException ex2) {}
            }
            if (IOStreamConnector.this.b) {
                try {
                    IOStreamConnector.this.c.close();
                }
                catch (final IOException ex3) {}
            }
            IOStreamConnector.this.g = true;
            for (int k = 0; k < IOStreamConnector.this.listenerList.size(); ++k) {
                ((IOStreamConnectorListener)IOStreamConnector.this.listenerList.elementAt(k)).connectorClosed(IOStreamConnector.this);
            }
            IOStreamConnector.this.i = null;
        }
    }
}
