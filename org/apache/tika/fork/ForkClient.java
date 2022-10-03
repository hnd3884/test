package org.apache.tika.fork;

import java.io.NotSerializableException;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.xml.sax.ContentHandler;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.util.jar.JarEntry;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import org.apache.tika.utils.ProcessUtils;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.nio.file.Path;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class ForkClient
{
    private static final AtomicInteger CLIENT_COUNTER;
    private final List<ForkResource> resources;
    private final ClassLoader loader;
    private final File jar;
    private final Process process;
    private final DataOutputStream output;
    private final DataInputStream input;
    private final int id;
    private volatile int filesProcessed;
    
    public ForkClient(final Path tikaDir, final ParserFactoryFactory parserFactoryFactory, final List<String> java, final TimeoutLimits timeoutLimits) throws IOException, TikaException {
        this(tikaDir, parserFactoryFactory, null, java, timeoutLimits);
    }
    
    public ForkClient(final Path tikaDir, final ParserFactoryFactory parserFactoryFactory, final ClassLoader classLoader, final List<String> java, final TimeoutLimits timeoutLimits) throws IOException, TikaException {
        this.resources = new ArrayList<ForkResource>();
        this.id = ForkClient.CLIENT_COUNTER.incrementAndGet();
        this.filesProcessed = 0;
        this.jar = null;
        this.loader = null;
        boolean ok = false;
        final ProcessBuilder builder = new ProcessBuilder(new String[0]);
        final List<String> command = new ArrayList<String>(java);
        command.add("-cp");
        String dirString = tikaDir.toAbsolutePath().toString();
        if (!dirString.endsWith("/")) {
            dirString += "/*";
        }
        else {
            dirString += "/";
        }
        dirString = ProcessUtils.escapeCommandLine(dirString);
        command.add(dirString);
        command.add("org.apache.tika.fork.ForkServer");
        command.add(Long.toString(timeoutLimits.getPulseMS()));
        command.add(Long.toString(timeoutLimits.getParseTimeoutMS()));
        command.add(Long.toString(timeoutLimits.getWaitTimeoutMS()));
        builder.command(command);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            this.process = builder.start();
            this.output = new DataOutputStream(this.process.getOutputStream());
            this.input = new DataInputStream(this.process.getInputStream());
            this.waitForStartBeacon();
            if (classLoader != null) {
                this.output.writeByte(8);
            }
            else {
                this.output.writeByte(6);
            }
            this.output.flush();
            this.sendObject(parserFactoryFactory, this.resources);
            if (classLoader != null) {
                this.sendObject(classLoader, this.resources);
            }
            this.waitForStartBeacon();
            ok = true;
        }
        catch (final Throwable t) {
            t.printStackTrace();
            throw t;
        }
        finally {
            if (!ok) {
                this.close();
            }
        }
    }
    
    public ForkClient(final ClassLoader loader, final Object object, final List<String> java, final TimeoutLimits timeoutLimits) throws IOException, TikaException {
        this.resources = new ArrayList<ForkResource>();
        this.id = ForkClient.CLIENT_COUNTER.incrementAndGet();
        this.filesProcessed = 0;
        boolean ok = false;
        try {
            this.loader = loader;
            this.jar = createBootstrapJar();
            final ProcessBuilder builder = new ProcessBuilder(new String[0]);
            final List<String> command = new ArrayList<String>(java);
            command.add("-jar");
            command.add(this.jar.getPath());
            command.add(Long.toString(timeoutLimits.getPulseMS()));
            command.add(Long.toString(timeoutLimits.getParseTimeoutMS()));
            command.add(Long.toString(timeoutLimits.getWaitTimeoutMS()));
            builder.command(command);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            this.process = builder.start();
            this.output = new DataOutputStream(this.process.getOutputStream());
            this.input = new DataInputStream(this.process.getInputStream());
            this.waitForStartBeacon();
            this.output.writeByte(7);
            this.output.flush();
            this.sendObject(loader, this.resources);
            this.sendObject(object, this.resources);
            this.waitForStartBeacon();
            ok = true;
        }
        finally {
            if (!ok) {
                this.close();
            }
        }
    }
    
    private static File createBootstrapJar() throws IOException {
        final File file = File.createTempFile("apache-tika-fork-", ".jar");
        boolean ok = false;
        try {
            fillBootstrapJar(file);
            ok = true;
        }
        finally {
            if (!ok) {
                file.delete();
            }
        }
        return file;
    }
    
    private static void fillBootstrapJar(final File file) throws IOException {
        try (final JarOutputStream jar = new JarOutputStream(new FileOutputStream(file))) {
            final String manifest = "Main-Class: " + ForkServer.class.getName() + "\n";
            jar.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            jar.write(manifest.getBytes(StandardCharsets.UTF_8));
            final Class<?>[] bootstrap = { ForkServer.class, ForkObjectInputStream.class, ForkProxy.class, ClassLoaderProxy.class, MemoryURLConnection.class, MemoryURLStreamHandler.class, MemoryURLStreamHandlerFactory.class, MemoryURLStreamRecord.class, TikaException.class };
            final ClassLoader loader = ForkServer.class.getClassLoader();
            for (final Class<?> klass : bootstrap) {
                final String path = klass.getName().replace('.', '/') + ".class";
                try (final InputStream input = loader.getResourceAsStream(path)) {
                    jar.putNextEntry(new JarEntry(path));
                    IOUtils.copy(input, (OutputStream)jar);
                }
            }
        }
    }
    
    private void waitForStartBeacon() throws IOException {
        while (true) {
            final int type = this.input.read();
            if ((byte)type == 4) {
                return;
            }
            if ((byte)type == 5) {
                throw new IOException("Server had a catastrophic initialization failure");
            }
            if (type == -1) {
                throw new IOException("EOF while waiting for start beacon");
            }
        }
    }
    
    public synchronized boolean ping() {
        try {
            this.output.writeByte(2);
            this.output.flush();
            final int type = this.input.read();
            return type == 2;
        }
        catch (final IOException e) {
            return false;
        }
    }
    
    public synchronized Throwable call(final String method, final Object... args) throws IOException, TikaException {
        ++this.filesProcessed;
        final List<ForkResource> r = new ArrayList<ForkResource>(this.resources);
        this.output.writeByte(1);
        this.output.writeUTF(method);
        for (final Object arg : args) {
            this.sendObject(arg, r);
        }
        return this.waitForResponse(r);
    }
    
    public int getFilesProcessed() {
        return this.filesProcessed;
    }
    
    private void sendObject(Object object, final List<ForkResource> resources) throws IOException, TikaException {
        final int n = resources.size();
        if (object instanceof InputStream) {
            resources.add(new InputStreamResource((InputStream)object));
            object = new InputStreamProxy(n);
        }
        else if (object instanceof RecursiveParserWrapperHandler) {
            resources.add(new RecursiveMetadataContentHandlerResource((RecursiveParserWrapperHandler)object));
            object = new RecursiveMetadataContentHandlerProxy(n, ((RecursiveParserWrapperHandler)object).getContentHandlerFactory());
        }
        else if (object instanceof ContentHandler && !(object instanceof AbstractRecursiveParserWrapperHandler)) {
            resources.add(new ContentHandlerResource((ContentHandler)object));
            object = new ContentHandlerProxy(n);
        }
        else if (object instanceof ClassLoader) {
            resources.add(new ClassLoaderResource((ClassLoader)object));
            object = new ClassLoaderProxy(n);
        }
        try {
            ForkObjectInputStream.sendObject(object, this.output);
        }
        catch (final NotSerializableException nse) {
            throw new TikaException("Unable to serialize " + object.getClass().getSimpleName() + " to pass to the Forked Parser", nse);
        }
        this.waitForResponse(resources);
    }
    
    public synchronized void close() {
        try {
            if (this.output != null) {
                this.output.close();
            }
            if (this.input != null) {
                this.input.close();
            }
        }
        catch (final IOException ex) {}
        if (this.process != null) {
            this.process.destroyForcibly();
            try {
                this.process.waitFor();
            }
            catch (final InterruptedException ex2) {}
        }
        if (this.jar != null) {
            this.jar.delete();
        }
    }
    
    private Throwable waitForResponse(final List<ForkResource> resources) throws IOException {
        this.output.flush();
        while (true) {
            final int type = this.input.read();
            if (type == -1) {
                throw new IOException("Lost connection to a forked server process");
            }
            if (type != 3) {
                if ((byte)type == -1) {
                    try {
                        return (Throwable)ForkObjectInputStream.readObject(this.input, this.loader);
                    }
                    catch (final ClassNotFoundException e) {
                        throw new IOException("Unable to deserialize an exception", e);
                    }
                }
                return null;
            }
            final ForkResource resource = resources.get(this.input.readUnsignedByte());
            resource.process(this.input, this.output);
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    static {
        CLIENT_COUNTER = new AtomicInteger(0);
    }
}
