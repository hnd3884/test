package javax.imageio.spi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import com.sun.imageio.spi.RAFImageOutputStreamSpi;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import com.sun.imageio.spi.OutputStreamImageOutputStreamSpi;
import com.sun.imageio.spi.InputStreamImageInputStreamSpi;
import com.sun.imageio.spi.FileImageOutputStreamSpi;
import com.sun.imageio.spi.FileImageInputStreamSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi;
import com.sun.imageio.plugins.bmp.BMPImageWriterSpi;
import com.sun.imageio.plugins.bmp.BMPImageReaderSpi;
import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import sun.awt.AppContext;
import java.util.Vector;

public final class IIORegistry extends ServiceRegistry
{
    private static final Vector initialCategories;
    
    private IIORegistry() {
        super(IIORegistry.initialCategories.iterator());
        this.registerStandardSpis();
        this.registerApplicationClasspathSpis();
    }
    
    public static IIORegistry getDefaultInstance() {
        final AppContext appContext = AppContext.getAppContext();
        IIORegistry iioRegistry = (IIORegistry)appContext.get(IIORegistry.class);
        if (iioRegistry == null) {
            iioRegistry = new IIORegistry();
            appContext.put(IIORegistry.class, iioRegistry);
        }
        return iioRegistry;
    }
    
    private void registerStandardSpis() {
        this.registerServiceProvider(new GIFImageReaderSpi());
        this.registerServiceProvider(new GIFImageWriterSpi());
        this.registerServiceProvider(new BMPImageReaderSpi());
        this.registerServiceProvider(new BMPImageWriterSpi());
        this.registerServiceProvider(new WBMPImageReaderSpi());
        this.registerServiceProvider(new WBMPImageWriterSpi());
        this.registerServiceProvider(new PNGImageReaderSpi());
        this.registerServiceProvider(new PNGImageWriterSpi());
        this.registerServiceProvider(new JPEGImageReaderSpi());
        this.registerServiceProvider(new JPEGImageWriterSpi());
        this.registerServiceProvider(new FileImageInputStreamSpi());
        this.registerServiceProvider(new FileImageOutputStreamSpi());
        this.registerServiceProvider(new InputStreamImageInputStreamSpi());
        this.registerServiceProvider(new OutputStreamImageOutputStreamSpi());
        this.registerServiceProvider(new RAFImageInputStreamSpi());
        this.registerServiceProvider(new RAFImageOutputStreamSpi());
        this.registerInstalledProviders();
    }
    
    public void registerApplicationClasspathSpis() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final Iterator<Class<?>> categories = this.getCategories();
        while (categories.hasNext()) {
            final Iterator<Object> iterator = (Iterator<Object>)ServiceLoader.load((Class<IIOServiceProvider>)categories.next(), contextClassLoader).iterator();
            while (iterator.hasNext()) {
                try {
                    this.registerServiceProvider(iterator.next());
                }
                catch (final ServiceConfigurationError serviceConfigurationError) {
                    if (System.getSecurityManager() == null) {
                        throw serviceConfigurationError;
                    }
                    serviceConfigurationError.printStackTrace();
                }
            }
        }
    }
    
    private void registerInstalledProviders() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final Iterator<Class<?>> categories = IIORegistry.this.getCategories();
                while (categories.hasNext()) {
                    final Iterator<Object> iterator = (Iterator<Object>)ServiceLoader.loadInstalled((Class<IIOServiceProvider>)categories.next()).iterator();
                    while (iterator.hasNext()) {
                        IIORegistry.this.registerServiceProvider(iterator.next());
                    }
                }
                return this;
            }
        });
    }
    
    static {
        (initialCategories = new Vector(5)).add(ImageReaderSpi.class);
        IIORegistry.initialCategories.add(ImageWriterSpi.class);
        IIORegistry.initialCategories.add(ImageTranscoderSpi.class);
        IIORegistry.initialCategories.add(ImageInputStreamSpi.class);
        IIORegistry.initialCategories.add(ImageOutputStreamSpi.class);
    }
}
