package org.apache.tika.fork;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.NotSerializableException;
import org.xml.sax.SAXException;
import org.apache.tika.parser.ParserFactory;
import org.apache.tika.exception.TikaException;
import java.io.ByteArrayInputStream;
import java.net.URLStreamHandlerFactory;
import java.net.URL;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

class ForkServer implements Runnable
{
    public static final byte ERROR = -1;
    public static final byte DONE = 0;
    public static final byte CALL = 1;
    public static final byte PING = 2;
    public static final byte RESOURCE = 3;
    public static final byte READY = 4;
    public static final byte FAILED_TO_START = 5;
    public static final byte INIT_PARSER_FACTORY_FACTORY = 6;
    public static final byte INIT_LOADER_PARSER = 7;
    public static final byte INIT_PARSER_FACTORY_FACTORY_LOADER = 8;
    private final Object[] lock;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final boolean active = true;
    private long serverPulseMillis;
    private long serverParserTimeoutMillis;
    private long serverWaitTimeoutMillis;
    private Object parser;
    private ClassLoader classLoader;
    private boolean parsing;
    private long since;
    
    public ForkServer(final InputStream input, final OutputStream output, final long serverPulseMillis, final long serverParserTimeoutMillis, final long serverWaitTimeoutMillis) throws IOException {
        this.lock = new Object[0];
        this.serverPulseMillis = 5000L;
        this.serverParserTimeoutMillis = 60000L;
        this.serverWaitTimeoutMillis = 60000L;
        this.parsing = false;
        this.input = new DataInputStream(input);
        this.output = new DataOutputStream(output);
        this.serverPulseMillis = serverPulseMillis;
        this.serverParserTimeoutMillis = serverParserTimeoutMillis;
        this.serverWaitTimeoutMillis = serverWaitTimeoutMillis;
        this.parsing = false;
        this.since = System.currentTimeMillis();
    }
    
    public static void main(final String[] args) throws Exception {
        final long serverPulseMillis = Long.parseLong(args[0]);
        final long serverParseTimeoutMillis = Long.parseLong(args[1]);
        final long serverWaitTimeoutMillis = Long.parseLong(args[2]);
        URL.setURLStreamHandlerFactory(new MemoryURLStreamHandlerFactory());
        final ForkServer server = new ForkServer(System.in, System.out, serverPulseMillis, serverParseTimeoutMillis, serverWaitTimeoutMillis);
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(System.err);
        final Thread watchdog = new Thread(server, "Tika Watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
        server.processRequests();
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this.lock) {
                    final long elapsed = System.currentTimeMillis() - this.since;
                    if (this.parsing && elapsed > this.serverParserTimeoutMillis) {
                        break;
                    }
                    if (!this.parsing && this.serverWaitTimeoutMillis > 0L && elapsed > this.serverWaitTimeoutMillis) {
                        break;
                    }
                }
                Thread.sleep(this.serverPulseMillis);
            }
            System.exit(0);
        }
        catch (final InterruptedException ex) {}
    }
    
    public void processRequests() {
        Label_0048: {
            try {
                this.initializeParserAndLoader();
                break Label_0048;
            }
            catch (final Throwable t) {
                t.printStackTrace();
                System.err.flush();
                try {
                    this.output.writeByte(5);
                    this.output.flush();
                }
                catch (final IOException e) {
                    e.printStackTrace();
                    System.err.flush();
                }
                return;
            }
            try {
                while (true) {
                    final int request = this.input.read();
                    if (request == -1) {
                        break;
                    }
                    if (request == 2) {
                        this.output.writeByte(2);
                    }
                    else {
                        if (request != 1) {
                            throw new IllegalStateException("Unexpected request");
                        }
                        this.call(this.classLoader, this.parser);
                    }
                    this.output.flush();
                }
            }
            catch (final Throwable t) {
                t.printStackTrace();
            }
        }
        System.err.flush();
    }
    
    private void initializeParserAndLoader() throws IOException, ClassNotFoundException, TikaException, SAXException {
        this.output.writeByte(4);
        this.output.flush();
        final int configIndex = this.input.read();
        if (configIndex == -1) {
            throw new TikaException("eof! pipe closed?!");
        }
        final Object firstObject = this.readObject(ForkServer.class.getClassLoader());
        switch (configIndex) {
            case 6: {
                if (firstObject instanceof ParserFactoryFactory) {
                    this.classLoader = ForkServer.class.getClassLoader();
                    final ParserFactory parserFactory = ((ParserFactoryFactory)firstObject).build();
                    this.parser = parserFactory.build();
                    break;
                }
                throw new IllegalArgumentException("Expecting only one object of class ParserFactoryFactory");
            }
            case 7: {
                if (firstObject instanceof ClassLoader) {
                    this.classLoader = (ClassLoader)firstObject;
                    Thread.currentThread().setContextClassLoader(this.classLoader);
                    this.parser = this.readObject(this.classLoader);
                    break;
                }
                throw new IllegalArgumentException("Expecting ClassLoader followed by a Parser");
            }
            case 8: {
                if (firstObject instanceof ParserFactoryFactory) {
                    final ParserFactory parserFactory = ((ParserFactoryFactory)firstObject).build();
                    this.parser = parserFactory.build();
                    this.classLoader = (ClassLoader)this.readObject(ForkServer.class.getClassLoader());
                    Thread.currentThread().setContextClassLoader(this.classLoader);
                    break;
                }
                throw new IllegalStateException("Expecing ParserFactoryFactory followed by a class loader");
            }
        }
        this.output.writeByte(4);
        this.output.flush();
    }
    
    private void call(final ClassLoader loader, final Object object) throws Exception {
        synchronized (this.lock) {
            this.parsing = true;
            this.since = System.currentTimeMillis();
        }
        try {
            final Method method = this.getMethod(object, this.input.readUTF());
            final Object[] args = new Object[method.getParameterTypes().length];
            for (int i = 0; i < args.length; ++i) {
                args[i] = this.readObject(loader);
            }
            try {
                method.invoke(object, args);
                this.output.write(0);
            }
            catch (final InvocationTargetException e) {
                this.output.write(-1);
                final Throwable toSend = e.getCause();
                try {
                    ForkObjectInputStream.sendObject(toSend, this.output);
                }
                catch (final NotSerializableException nse) {
                    final TikaException te = new TikaException(toSend.getMessage());
                    te.setStackTrace(toSend.getStackTrace());
                    ForkObjectInputStream.sendObject(te, this.output);
                }
            }
        }
        finally {
            synchronized (this.lock) {
                this.parsing = false;
                this.since = System.currentTimeMillis();
            }
        }
    }
    
    private Method getMethod(final Object object, final String name) {
        for (Class<?> klass = object.getClass(); klass != null; klass = klass.getSuperclass()) {
            for (final Class<?> iface : klass.getInterfaces()) {
                for (final Method method : iface.getMethods()) {
                    if (name.equals(method.getName())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
    
    private Object readObject(final ClassLoader loader) throws IOException, ClassNotFoundException {
        final Object object = ForkObjectInputStream.readObject(this.input, loader);
        if (object instanceof ForkProxy) {
            ((ForkProxy)object).init(this.input, this.output);
        }
        this.output.writeByte(0);
        this.output.flush();
        return object;
    }
}
