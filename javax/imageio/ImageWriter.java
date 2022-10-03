package javax.imageio;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.image.Raster;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.awt.Dimension;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import java.util.List;
import java.util.Locale;
import javax.imageio.spi.ImageWriterSpi;

public abstract class ImageWriter implements ImageTranscoder
{
    protected ImageWriterSpi originatingProvider;
    protected Object output;
    protected Locale[] availableLocales;
    protected Locale locale;
    protected List<IIOWriteWarningListener> warningListeners;
    protected List<Locale> warningLocales;
    protected List<IIOWriteProgressListener> progressListeners;
    private boolean abortFlag;
    
    protected ImageWriter(final ImageWriterSpi originatingProvider) {
        this.originatingProvider = null;
        this.output = null;
        this.availableLocales = null;
        this.locale = null;
        this.warningListeners = null;
        this.warningLocales = null;
        this.progressListeners = null;
        this.abortFlag = false;
        this.originatingProvider = originatingProvider;
    }
    
    public ImageWriterSpi getOriginatingProvider() {
        return this.originatingProvider;
    }
    
    public void setOutput(final Object output) {
        if (output != null) {
            final ImageWriterSpi originatingProvider = this.getOriginatingProvider();
            if (originatingProvider != null) {
                final Class[] outputTypes = originatingProvider.getOutputTypes();
                boolean b = false;
                for (int i = 0; i < outputTypes.length; ++i) {
                    if (outputTypes[i].isInstance(output)) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    throw new IllegalArgumentException("Illegal output type!");
                }
            }
        }
        this.output = output;
    }
    
    public Object getOutput() {
        return this.output;
    }
    
    public Locale[] getAvailableLocales() {
        return (Locale[])((this.availableLocales == null) ? null : ((Locale[])this.availableLocales.clone()));
    }
    
    public void setLocale(final Locale locale) {
        if (locale != null) {
            final Locale[] availableLocales = this.getAvailableLocales();
            boolean b = false;
            if (availableLocales != null) {
                for (int i = 0; i < availableLocales.length; ++i) {
                    if (locale.equals(availableLocales[i])) {
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                throw new IllegalArgumentException("Invalid locale!");
            }
        }
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public ImageWriteParam getDefaultWriteParam() {
        return new ImageWriteParam(this.getLocale());
    }
    
    public abstract IIOMetadata getDefaultStreamMetadata(final ImageWriteParam p0);
    
    public abstract IIOMetadata getDefaultImageMetadata(final ImageTypeSpecifier p0, final ImageWriteParam p1);
    
    @Override
    public abstract IIOMetadata convertStreamMetadata(final IIOMetadata p0, final ImageWriteParam p1);
    
    @Override
    public abstract IIOMetadata convertImageMetadata(final IIOMetadata p0, final ImageTypeSpecifier p1, final ImageWriteParam p2);
    
    public int getNumThumbnailsSupported(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam, final IIOMetadata iioMetadata, final IIOMetadata iioMetadata2) {
        return 0;
    }
    
    public Dimension[] getPreferredThumbnailSizes(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam, final IIOMetadata iioMetadata, final IIOMetadata iioMetadata2) {
        return null;
    }
    
    public boolean canWriteRasters() {
        return false;
    }
    
    public abstract void write(final IIOMetadata p0, final IIOImage p1, final ImageWriteParam p2) throws IOException;
    
    public void write(final IIOImage iioImage) throws IOException {
        this.write(null, iioImage, null);
    }
    
    public void write(final RenderedImage renderedImage) throws IOException {
        this.write(null, new IIOImage(renderedImage, null, null), null);
    }
    
    private void unsupported() {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        throw new UnsupportedOperationException("Unsupported write variant!");
    }
    
    public boolean canWriteSequence() {
        return false;
    }
    
    public void prepareWriteSequence(final IIOMetadata iioMetadata) throws IOException {
        this.unsupported();
    }
    
    public void writeToSequence(final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        this.unsupported();
    }
    
    public void endWriteSequence() throws IOException {
        this.unsupported();
    }
    
    public boolean canReplaceStreamMetadata() throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void replaceStreamMetadata(final IIOMetadata iioMetadata) throws IOException {
        this.unsupported();
    }
    
    public boolean canReplaceImageMetadata(final int n) throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void replaceImageMetadata(final int n, final IIOMetadata iioMetadata) throws IOException {
        this.unsupported();
    }
    
    public boolean canInsertImage(final int n) throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void writeInsert(final int n, final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        this.unsupported();
    }
    
    public boolean canRemoveImage(final int n) throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void removeImage(final int n) throws IOException {
        this.unsupported();
    }
    
    public boolean canWriteEmpty() throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void prepareWriteEmpty(final IIOMetadata iioMetadata, final ImageTypeSpecifier imageTypeSpecifier, final int n, final int n2, final IIOMetadata iioMetadata2, final List<? extends BufferedImage> list, final ImageWriteParam imageWriteParam) throws IOException {
        this.unsupported();
    }
    
    public void endWriteEmpty() throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        throw new IllegalStateException("No call to prepareWriteEmpty!");
    }
    
    public boolean canInsertEmpty(final int n) throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void prepareInsertEmpty(final int n, final ImageTypeSpecifier imageTypeSpecifier, final int n2, final int n3, final IIOMetadata iioMetadata, final List<? extends BufferedImage> list, final ImageWriteParam imageWriteParam) throws IOException {
        this.unsupported();
    }
    
    public void endInsertEmpty() throws IOException {
        this.unsupported();
    }
    
    public boolean canReplacePixels(final int n) throws IOException {
        if (this.getOutput() == null) {
            throw new IllegalStateException("getOutput() == null!");
        }
        return false;
    }
    
    public void prepareReplacePixels(final int n, final Rectangle rectangle) throws IOException {
        this.unsupported();
    }
    
    public void replacePixels(final RenderedImage renderedImage, final ImageWriteParam imageWriteParam) throws IOException {
        this.unsupported();
    }
    
    public void replacePixels(final Raster raster, final ImageWriteParam imageWriteParam) throws IOException {
        this.unsupported();
    }
    
    public void endReplacePixels() throws IOException {
        this.unsupported();
    }
    
    public synchronized void abort() {
        this.abortFlag = true;
    }
    
    protected synchronized boolean abortRequested() {
        return this.abortFlag;
    }
    
    protected synchronized void clearAbortRequest() {
        this.abortFlag = false;
    }
    
    public void addIIOWriteWarningListener(final IIOWriteWarningListener iioWriteWarningListener) {
        if (iioWriteWarningListener == null) {
            return;
        }
        this.warningListeners = ImageReader.addToList(this.warningListeners, iioWriteWarningListener);
        this.warningLocales = ImageReader.addToList(this.warningLocales, this.getLocale());
    }
    
    public void removeIIOWriteWarningListener(final IIOWriteWarningListener iioWriteWarningListener) {
        if (iioWriteWarningListener == null || this.warningListeners == null) {
            return;
        }
        final int index = this.warningListeners.indexOf(iioWriteWarningListener);
        if (index != -1) {
            this.warningListeners.remove(index);
            this.warningLocales.remove(index);
            if (this.warningListeners.size() == 0) {
                this.warningListeners = null;
                this.warningLocales = null;
            }
        }
    }
    
    public void removeAllIIOWriteWarningListeners() {
        this.warningListeners = null;
        this.warningLocales = null;
    }
    
    public void addIIOWriteProgressListener(final IIOWriteProgressListener iioWriteProgressListener) {
        if (iioWriteProgressListener == null) {
            return;
        }
        this.progressListeners = ImageReader.addToList(this.progressListeners, iioWriteProgressListener);
    }
    
    public void removeIIOWriteProgressListener(final IIOWriteProgressListener iioWriteProgressListener) {
        if (iioWriteProgressListener == null || this.progressListeners == null) {
            return;
        }
        this.progressListeners = ImageReader.removeFromList(this.progressListeners, iioWriteProgressListener);
    }
    
    public void removeAllIIOWriteProgressListeners() {
        this.progressListeners = null;
    }
    
    protected void processImageStarted(final int n) {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).imageStarted(this, n);
        }
    }
    
    protected void processImageProgress(final float n) {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).imageProgress(this, n);
        }
    }
    
    protected void processImageComplete() {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).imageComplete(this);
        }
    }
    
    protected void processThumbnailStarted(final int n, final int n2) {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).thumbnailStarted(this, n, n2);
        }
    }
    
    protected void processThumbnailProgress(final float n) {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).thumbnailProgress(this, n);
        }
    }
    
    protected void processThumbnailComplete() {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).thumbnailComplete(this);
        }
    }
    
    protected void processWriteAborted() {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).writeAborted(this);
        }
    }
    
    protected void processWarningOccurred(final int n, final String s) {
        if (this.warningListeners == null) {
            return;
        }
        if (s == null) {
            throw new IllegalArgumentException("warning == null!");
        }
        for (int size = this.warningListeners.size(), i = 0; i < size; ++i) {
            this.warningListeners.get(i).warningOccurred(this, n, s);
        }
    }
    
    protected void processWarningOccurred(final int n, final String s, final String s2) {
        if (this.warningListeners == null) {
            return;
        }
        if (s == null) {
            throw new IllegalArgumentException("baseName == null!");
        }
        if (s2 == null) {
            throw new IllegalArgumentException("keyword == null!");
        }
        for (int size = this.warningListeners.size(), i = 0; i < size; ++i) {
            final IIOWriteWarningListener iioWriteWarningListener = this.warningListeners.get(i);
            Locale default1 = this.warningLocales.get(i);
            if (default1 == null) {
                default1 = Locale.getDefault();
            }
            final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
                @Override
                public Object run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
            ResourceBundle resourceBundle;
            try {
                resourceBundle = ResourceBundle.getBundle(s, default1, classLoader);
            }
            catch (final MissingResourceException ex) {
                try {
                    resourceBundle = ResourceBundle.getBundle(s, default1);
                }
                catch (final MissingResourceException ex2) {
                    throw new IllegalArgumentException("Bundle not found!");
                }
            }
            String string;
            try {
                string = resourceBundle.getString(s2);
            }
            catch (final ClassCastException ex3) {
                throw new IllegalArgumentException("Resource is not a String!");
            }
            catch (final MissingResourceException ex4) {
                throw new IllegalArgumentException("Resource is missing!");
            }
            iioWriteWarningListener.warningOccurred(this, n, string);
        }
    }
    
    public void reset() {
        this.setOutput(null);
        this.setLocale(null);
        this.removeAllIIOWriteWarningListeners();
        this.removeAllIIOWriteProgressListeners();
        this.clearAbortRequest();
    }
    
    public void dispose() {
    }
}
