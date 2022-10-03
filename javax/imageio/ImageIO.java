package javax.imageio;

import java.util.Arrays;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import java.net.URL;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.spi.ImageTranscoderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.spi.ImageReaderSpi;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.stream.ImageOutputStream;
import java.util.Iterator;
import java.io.IOException;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.stream.ImageInputStream;
import java.security.Permission;
import java.io.FilePermission;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.awt.AppContext;
import java.lang.reflect.Method;
import javax.imageio.spi.IIORegistry;

public final class ImageIO
{
    private static final IIORegistry theRegistry;
    private static Method readerFormatNamesMethod;
    private static Method readerFileSuffixesMethod;
    private static Method readerMIMETypesMethod;
    private static Method writerFormatNamesMethod;
    private static Method writerFileSuffixesMethod;
    private static Method writerMIMETypesMethod;
    
    private ImageIO() {
    }
    
    public static void scanForPlugins() {
        ImageIO.theRegistry.registerApplicationClasspathSpis();
    }
    
    private static synchronized CacheInfo getCacheInfo() {
        final AppContext appContext = AppContext.getAppContext();
        CacheInfo cacheInfo = (CacheInfo)appContext.get(CacheInfo.class);
        if (cacheInfo == null) {
            cacheInfo = new CacheInfo();
            appContext.put(CacheInfo.class, cacheInfo);
        }
        return cacheInfo;
    }
    
    private static String getTempDir() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.io.tmpdir"));
    }
    
    private static boolean hasCachePermission() {
        final Boolean hasPermission = getCacheInfo().getHasPermission();
        if (hasPermission != null) {
            return hasPermission;
        }
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                final File cacheDirectory = getCacheDirectory();
                String s;
                if (cacheDirectory != null) {
                    s = cacheDirectory.getPath();
                }
                else {
                    s = getTempDir();
                    if (s == null || s.isEmpty()) {
                        getCacheInfo().setHasPermission(Boolean.FALSE);
                        return false;
                    }
                }
                String string = s;
                if (!string.endsWith(File.separator)) {
                    string += File.separator;
                }
                securityManager.checkPermission(new FilePermission(string + "*", "read, write, delete"));
            }
        }
        catch (final SecurityException ex) {
            getCacheInfo().setHasPermission(Boolean.FALSE);
            return false;
        }
        getCacheInfo().setHasPermission(Boolean.TRUE);
        return true;
    }
    
    public static void setUseCache(final boolean useCache) {
        getCacheInfo().setUseCache(useCache);
    }
    
    public static boolean getUseCache() {
        return getCacheInfo().getUseCache();
    }
    
    public static void setCacheDirectory(final File cacheDirectory) {
        if (cacheDirectory != null && !cacheDirectory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory!");
        }
        getCacheInfo().setCacheDirectory(cacheDirectory);
        getCacheInfo().setHasPermission(null);
    }
    
    public static File getCacheDirectory() {
        return getCacheInfo().getCacheDirectory();
    }
    
    public static ImageInputStream createImageInputStream(final Object o) throws IOException {
        if (o == null) {
            throw new IllegalArgumentException("input == null!");
        }
        Iterator<ImageInputStreamSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageInputStreamSpi.class, true);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
        final boolean b = getUseCache() && hasCachePermission();
        while (serviceProviders.hasNext()) {
            final ImageInputStreamSpi imageInputStreamSpi = serviceProviders.next();
            if (imageInputStreamSpi.getInputClass().isInstance(o)) {
                try {
                    return imageInputStreamSpi.createInputStreamInstance(o, b, getCacheDirectory());
                }
                catch (final IOException ex2) {
                    throw new IIOException("Can't create cache file!", ex2);
                }
            }
        }
        return null;
    }
    
    public static ImageOutputStream createImageOutputStream(final Object o) throws IOException {
        if (o == null) {
            throw new IllegalArgumentException("output == null!");
        }
        Iterator<ImageOutputStreamSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageOutputStreamSpi.class, true);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
        final boolean b = getUseCache() && hasCachePermission();
        while (serviceProviders.hasNext()) {
            final ImageOutputStreamSpi imageOutputStreamSpi = serviceProviders.next();
            if (imageOutputStreamSpi.getOutputClass().isInstance(o)) {
                try {
                    return imageOutputStreamSpi.createOutputStreamInstance(o, b, getCacheDirectory());
                }
                catch (final IOException ex2) {
                    throw new IIOException("Can't create cache file!", ex2);
                }
            }
        }
        return null;
    }
    
    private static <S extends ImageReaderWriterSpi> String[] getReaderWriterInfo(final Class<S> clazz, final SpiInfo spiInfo) {
        Iterator<S> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(clazz, true);
        }
        catch (final IllegalArgumentException ex) {
            return new String[0];
        }
        final HashSet set = new HashSet();
        while (serviceProviders.hasNext()) {
            Collections.addAll(set, spiInfo.info(serviceProviders.next()));
        }
        return (String[])set.toArray(new String[set.size()]);
    }
    
    public static String[] getReaderFormatNames() {
        return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FORMAT_NAMES);
    }
    
    public static String[] getReaderMIMETypes() {
        return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.MIME_TYPES);
    }
    
    public static String[] getReaderFileSuffixes() {
        return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FILE_SUFFIXES);
    }
    
    public static Iterator<ImageReader> getImageReaders(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("input == null!");
        }
        Iterator<ImageReaderSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageReaderSpi.class, new CanDecodeInputFilter(o), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageReaderIterator(serviceProviders);
    }
    
    public static Iterator<ImageReader> getImageReadersByFormatName(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        Iterator<ImageReaderSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(ImageIO.readerFormatNamesMethod, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageReaderIterator(serviceProviders);
    }
    
    public static Iterator<ImageReader> getImageReadersBySuffix(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("fileSuffix == null!");
        }
        Iterator<ImageReaderSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(ImageIO.readerFileSuffixesMethod, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageReaderIterator(serviceProviders);
    }
    
    public static Iterator<ImageReader> getImageReadersByMIMEType(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("MIMEType == null!");
        }
        Iterator<ImageReaderSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(ImageIO.readerMIMETypesMethod, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageReaderIterator(serviceProviders);
    }
    
    public static String[] getWriterFormatNames() {
        return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FORMAT_NAMES);
    }
    
    public static String[] getWriterMIMETypes() {
        return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.MIME_TYPES);
    }
    
    public static String[] getWriterFileSuffixes() {
        return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FILE_SUFFIXES);
    }
    
    private static boolean contains(final String[] array, final String s) {
        for (int i = 0; i < array.length; ++i) {
            if (s.equalsIgnoreCase(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static Iterator<ImageWriter> getImageWritersByFormatName(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        Iterator<ImageWriterSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(ImageIO.writerFormatNamesMethod, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageWriterIterator(serviceProviders);
    }
    
    public static Iterator<ImageWriter> getImageWritersBySuffix(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("fileSuffix == null!");
        }
        Iterator<ImageWriterSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(ImageIO.writerFileSuffixesMethod, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageWriterIterator(serviceProviders);
    }
    
    public static Iterator<ImageWriter> getImageWritersByMIMEType(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("MIMEType == null!");
        }
        Iterator<ImageWriterSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(ImageIO.writerMIMETypesMethod, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageWriterIterator(serviceProviders);
    }
    
    public static ImageWriter getImageWriter(final ImageReader imageReader) {
        if (imageReader == null) {
            throw new IllegalArgumentException("reader == null!");
        }
        ImageReaderSpi originatingProvider = imageReader.getOriginatingProvider();
        if (originatingProvider == null) {
            Iterator<ImageReaderSpi> serviceProviders;
            try {
                serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageReaderSpi.class, false);
            }
            catch (final IllegalArgumentException ex) {
                return null;
            }
            while (serviceProviders.hasNext()) {
                final ImageReaderSpi imageReaderSpi = serviceProviders.next();
                if (imageReaderSpi.isOwnReader(imageReader)) {
                    originatingProvider = imageReaderSpi;
                    break;
                }
            }
            if (originatingProvider == null) {
                return null;
            }
        }
        final String[] imageWriterSpiNames = originatingProvider.getImageWriterSpiNames();
        if (imageWriterSpiNames == null) {
            return null;
        }
        Class<?> forName;
        try {
            forName = Class.forName(imageWriterSpiNames[0], true, ClassLoader.getSystemClassLoader());
        }
        catch (final ClassNotFoundException ex2) {
            return null;
        }
        final ImageWriterSpi imageWriterSpi = ImageIO.theRegistry.getServiceProviderByClass(forName);
        if (imageWriterSpi == null) {
            return null;
        }
        try {
            return imageWriterSpi.createWriterInstance();
        }
        catch (final IOException ex3) {
            ImageIO.theRegistry.deregisterServiceProvider(imageWriterSpi, ImageWriterSpi.class);
            return null;
        }
    }
    
    public static ImageReader getImageReader(final ImageWriter imageWriter) {
        if (imageWriter == null) {
            throw new IllegalArgumentException("writer == null!");
        }
        ImageWriterSpi originatingProvider = imageWriter.getOriginatingProvider();
        if (originatingProvider == null) {
            Iterator<ImageWriterSpi> serviceProviders;
            try {
                serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageWriterSpi.class, false);
            }
            catch (final IllegalArgumentException ex) {
                return null;
            }
            while (serviceProviders.hasNext()) {
                final ImageWriterSpi imageWriterSpi = serviceProviders.next();
                if (imageWriterSpi.isOwnWriter(imageWriter)) {
                    originatingProvider = imageWriterSpi;
                    break;
                }
            }
            if (originatingProvider == null) {
                return null;
            }
        }
        final String[] imageReaderSpiNames = originatingProvider.getImageReaderSpiNames();
        if (imageReaderSpiNames == null) {
            return null;
        }
        Class<?> forName;
        try {
            forName = Class.forName(imageReaderSpiNames[0], true, ClassLoader.getSystemClassLoader());
        }
        catch (final ClassNotFoundException ex2) {
            return null;
        }
        final ImageReaderSpi imageReaderSpi = ImageIO.theRegistry.getServiceProviderByClass(forName);
        if (imageReaderSpi == null) {
            return null;
        }
        try {
            return imageReaderSpi.createReaderInstance();
        }
        catch (final IOException ex3) {
            ImageIO.theRegistry.deregisterServiceProvider(imageReaderSpi, ImageReaderSpi.class);
            return null;
        }
    }
    
    public static Iterator<ImageWriter> getImageWriters(final ImageTypeSpecifier imageTypeSpecifier, final String s) {
        if (imageTypeSpecifier == null) {
            throw new IllegalArgumentException("type == null!");
        }
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        Iterator<ImageWriterSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageWriterSpi.class, new CanEncodeImageAndFormatFilter(imageTypeSpecifier, s), true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageWriterIterator(serviceProviders);
    }
    
    public static Iterator<ImageTranscoder> getImageTranscoders(final ImageReader imageReader, final ImageWriter imageWriter) {
        if (imageReader == null) {
            throw new IllegalArgumentException("reader == null!");
        }
        if (imageWriter == null) {
            throw new IllegalArgumentException("writer == null!");
        }
        final TranscoderFilter transcoderFilter = new TranscoderFilter(imageReader.getOriginatingProvider(), imageWriter.getOriginatingProvider());
        Iterator<ImageTranscoderSpi> serviceProviders;
        try {
            serviceProviders = ImageIO.theRegistry.getServiceProviders(ImageTranscoderSpi.class, transcoderFilter, true);
        }
        catch (final IllegalArgumentException ex) {
            return Collections.emptyIterator();
        }
        return new ImageTranscoderIterator(serviceProviders);
    }
    
    public static BufferedImage read(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("input == null!");
        }
        if (!file.canRead()) {
            throw new IIOException("Can't read input file!");
        }
        final ImageInputStream imageInputStream = createImageInputStream(file);
        if (imageInputStream == null) {
            throw new IIOException("Can't create an ImageInputStream!");
        }
        final BufferedImage read = read(imageInputStream);
        if (read == null) {
            imageInputStream.close();
        }
        return read;
    }
    
    public static BufferedImage read(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("input == null!");
        }
        final ImageInputStream imageInputStream = createImageInputStream(inputStream);
        if (imageInputStream == null) {
            throw new IIOException("Can't create an ImageInputStream!");
        }
        final BufferedImage read = read(imageInputStream);
        if (read == null) {
            imageInputStream.close();
        }
        return read;
    }
    
    public static BufferedImage read(final URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("input == null!");
        }
        InputStream openStream;
        try {
            openStream = url.openStream();
        }
        catch (final IOException ex) {
            throw new IIOException("Can't get input stream from URL!", ex);
        }
        final ImageInputStream imageInputStream = createImageInputStream(openStream);
        if (imageInputStream == null) {
            openStream.close();
            throw new IIOException("Can't create an ImageInputStream!");
        }
        BufferedImage read;
        try {
            read = read(imageInputStream);
            if (read == null) {
                imageInputStream.close();
            }
        }
        finally {
            openStream.close();
        }
        return read;
    }
    
    public static BufferedImage read(final ImageInputStream imageInputStream) throws IOException {
        if (imageInputStream == null) {
            throw new IllegalArgumentException("stream == null!");
        }
        final Iterator<ImageReader> imageReaders = getImageReaders(imageInputStream);
        if (!imageReaders.hasNext()) {
            return null;
        }
        final ImageReader imageReader = imageReaders.next();
        final ImageReadParam defaultReadParam = imageReader.getDefaultReadParam();
        imageReader.setInput(imageInputStream, true, true);
        BufferedImage read;
        try {
            read = imageReader.read(0, defaultReadParam);
        }
        finally {
            imageReader.dispose();
            imageInputStream.close();
        }
        return read;
    }
    
    public static boolean write(final RenderedImage renderedImage, final String s, final ImageOutputStream imageOutputStream) throws IOException {
        if (renderedImage == null) {
            throw new IllegalArgumentException("im == null!");
        }
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        if (imageOutputStream == null) {
            throw new IllegalArgumentException("output == null!");
        }
        return doWrite(renderedImage, getWriter(renderedImage, s), imageOutputStream);
    }
    
    public static boolean write(final RenderedImage renderedImage, final String s, final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("output == null!");
        }
        final ImageWriter writer = getWriter(renderedImage, s);
        if (writer == null) {
            return false;
        }
        file.delete();
        final ImageOutputStream imageOutputStream = createImageOutputStream(file);
        if (imageOutputStream == null) {
            throw new IIOException("Can't create an ImageOutputStream!");
        }
        try {
            return doWrite(renderedImage, writer, imageOutputStream);
        }
        finally {
            imageOutputStream.close();
        }
    }
    
    public static boolean write(final RenderedImage renderedImage, final String s, final OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("output == null!");
        }
        final ImageOutputStream imageOutputStream = createImageOutputStream(outputStream);
        if (imageOutputStream == null) {
            throw new IIOException("Can't create an ImageOutputStream!");
        }
        try {
            return doWrite(renderedImage, getWriter(renderedImage, s), imageOutputStream);
        }
        finally {
            imageOutputStream.close();
        }
    }
    
    private static ImageWriter getWriter(final RenderedImage renderedImage, final String s) {
        final Iterator<ImageWriter> imageWriters = getImageWriters(ImageTypeSpecifier.createFromRenderedImage(renderedImage), s);
        if (imageWriters.hasNext()) {
            return imageWriters.next();
        }
        return null;
    }
    
    private static boolean doWrite(final RenderedImage renderedImage, final ImageWriter imageWriter, final ImageOutputStream output) throws IOException {
        if (imageWriter == null) {
            return false;
        }
        imageWriter.setOutput(output);
        try {
            imageWriter.write(renderedImage);
        }
        finally {
            imageWriter.dispose();
            output.flush();
        }
        return true;
    }
    
    static {
        theRegistry = IIORegistry.getDefaultInstance();
        try {
            ImageIO.readerFormatNamesMethod = ImageReaderSpi.class.getMethod("getFormatNames", (Class<?>[])new Class[0]);
            ImageIO.readerFileSuffixesMethod = ImageReaderSpi.class.getMethod("getFileSuffixes", (Class<?>[])new Class[0]);
            ImageIO.readerMIMETypesMethod = ImageReaderSpi.class.getMethod("getMIMETypes", (Class<?>[])new Class[0]);
            ImageIO.writerFormatNamesMethod = ImageWriterSpi.class.getMethod("getFormatNames", (Class<?>[])new Class[0]);
            ImageIO.writerFileSuffixesMethod = ImageWriterSpi.class.getMethod("getFileSuffixes", (Class<?>[])new Class[0]);
            ImageIO.writerMIMETypesMethod = ImageWriterSpi.class.getMethod("getMIMETypes", (Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }
    
    static class CacheInfo
    {
        boolean useCache;
        File cacheDirectory;
        Boolean hasPermission;
        
        public CacheInfo() {
            this.useCache = true;
            this.cacheDirectory = null;
            this.hasPermission = null;
        }
        
        public boolean getUseCache() {
            return this.useCache;
        }
        
        public void setUseCache(final boolean useCache) {
            this.useCache = useCache;
        }
        
        public File getCacheDirectory() {
            return this.cacheDirectory;
        }
        
        public void setCacheDirectory(final File cacheDirectory) {
            this.cacheDirectory = cacheDirectory;
        }
        
        public Boolean getHasPermission() {
            return this.hasPermission;
        }
        
        public void setHasPermission(final Boolean hasPermission) {
            this.hasPermission = hasPermission;
        }
    }
    
    private enum SpiInfo
    {
        FORMAT_NAMES {
            @Override
            String[] info(final ImageReaderWriterSpi imageReaderWriterSpi) {
                return imageReaderWriterSpi.getFormatNames();
            }
        }, 
        MIME_TYPES {
            @Override
            String[] info(final ImageReaderWriterSpi imageReaderWriterSpi) {
                return imageReaderWriterSpi.getMIMETypes();
            }
        }, 
        FILE_SUFFIXES {
            @Override
            String[] info(final ImageReaderWriterSpi imageReaderWriterSpi) {
                return imageReaderWriterSpi.getFileSuffixes();
            }
        };
        
        abstract String[] info(final ImageReaderWriterSpi p0);
    }
    
    static class ImageReaderIterator implements Iterator<ImageReader>
    {
        public Iterator iter;
        
        public ImageReaderIterator(final Iterator iter) {
            this.iter = iter;
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public ImageReader next() {
            ImageReaderSpi imageReaderSpi = null;
            try {
                imageReaderSpi = this.iter.next();
                return imageReaderSpi.createReaderInstance();
            }
            catch (final IOException ex) {
                ImageIO.theRegistry.deregisterServiceProvider(imageReaderSpi, ImageReaderSpi.class);
                return null;
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class CanDecodeInputFilter implements ServiceRegistry.Filter
    {
        Object input;
        
        public CanDecodeInputFilter(final Object input) {
            this.input = input;
        }
        
        @Override
        public boolean filter(final Object o) {
            try {
                final ImageReaderSpi imageReaderSpi = (ImageReaderSpi)o;
                ImageInputStream imageInputStream = null;
                if (this.input instanceof ImageInputStream) {
                    imageInputStream = (ImageInputStream)this.input;
                }
                if (imageInputStream != null) {
                    imageInputStream.mark();
                }
                final boolean canDecodeInput = imageReaderSpi.canDecodeInput(this.input);
                if (imageInputStream != null) {
                    imageInputStream.reset();
                }
                return canDecodeInput;
            }
            catch (final IOException ex) {
                return false;
            }
        }
    }
    
    static class CanEncodeImageAndFormatFilter implements ServiceRegistry.Filter
    {
        ImageTypeSpecifier type;
        String formatName;
        
        public CanEncodeImageAndFormatFilter(final ImageTypeSpecifier type, final String formatName) {
            this.type = type;
            this.formatName = formatName;
        }
        
        @Override
        public boolean filter(final Object o) {
            final ImageWriterSpi imageWriterSpi = (ImageWriterSpi)o;
            return Arrays.asList(imageWriterSpi.getFormatNames()).contains(this.formatName) && imageWriterSpi.canEncodeImage(this.type);
        }
    }
    
    static class ContainsFilter implements ServiceRegistry.Filter
    {
        Method method;
        String name;
        
        public ContainsFilter(final Method method, final String name) {
            this.method = method;
            this.name = name;
        }
        
        @Override
        public boolean filter(final Object o) {
            try {
                return contains((String[])this.method.invoke(o, new Object[0]), this.name);
            }
            catch (final Exception ex) {
                return false;
            }
        }
    }
    
    static class ImageWriterIterator implements Iterator<ImageWriter>
    {
        public Iterator iter;
        
        public ImageWriterIterator(final Iterator iter) {
            this.iter = iter;
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public ImageWriter next() {
            ImageWriterSpi imageWriterSpi = null;
            try {
                imageWriterSpi = this.iter.next();
                return imageWriterSpi.createWriterInstance();
            }
            catch (final IOException ex) {
                ImageIO.theRegistry.deregisterServiceProvider(imageWriterSpi, ImageWriterSpi.class);
                return null;
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class ImageTranscoderIterator implements Iterator<ImageTranscoder>
    {
        public Iterator iter;
        
        public ImageTranscoderIterator(final Iterator iter) {
            this.iter = iter;
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public ImageTranscoder next() {
            return this.iter.next().createTranscoderInstance();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class TranscoderFilter implements ServiceRegistry.Filter
    {
        String readerSpiName;
        String writerSpiName;
        
        public TranscoderFilter(final ImageReaderSpi imageReaderSpi, final ImageWriterSpi imageWriterSpi) {
            this.readerSpiName = imageReaderSpi.getClass().getName();
            this.writerSpiName = imageWriterSpi.getClass().getName();
        }
        
        @Override
        public boolean filter(final Object o) {
            final ImageTranscoderSpi imageTranscoderSpi = (ImageTranscoderSpi)o;
            final String readerServiceProviderName = imageTranscoderSpi.getReaderServiceProviderName();
            final String writerServiceProviderName = imageTranscoderSpi.getWriterServiceProviderName();
            return readerServiceProviderName.equals(this.readerSpiName) && writerServiceProviderName.equals(this.writerSpiName);
        }
    }
}
