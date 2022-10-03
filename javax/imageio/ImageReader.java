package javax.imageio;

import java.awt.Rectangle;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.Set;
import javax.imageio.metadata.IIOMetadata;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadWarningListener;
import java.util.List;
import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;

public abstract class ImageReader
{
    protected ImageReaderSpi originatingProvider;
    protected Object input;
    protected boolean seekForwardOnly;
    protected boolean ignoreMetadata;
    protected int minIndex;
    protected Locale[] availableLocales;
    protected Locale locale;
    protected List<IIOReadWarningListener> warningListeners;
    protected List<Locale> warningLocales;
    protected List<IIOReadProgressListener> progressListeners;
    protected List<IIOReadUpdateListener> updateListeners;
    private boolean abortFlag;
    
    protected ImageReader(final ImageReaderSpi originatingProvider) {
        this.input = null;
        this.seekForwardOnly = false;
        this.ignoreMetadata = false;
        this.minIndex = 0;
        this.availableLocales = null;
        this.locale = null;
        this.warningListeners = null;
        this.warningLocales = null;
        this.progressListeners = null;
        this.updateListeners = null;
        this.abortFlag = false;
        this.originatingProvider = originatingProvider;
    }
    
    public String getFormatName() throws IOException {
        return this.originatingProvider.getFormatNames()[0];
    }
    
    public ImageReaderSpi getOriginatingProvider() {
        return this.originatingProvider;
    }
    
    public void setInput(final Object input, final boolean seekForwardOnly, final boolean ignoreMetadata) {
        if (input != null) {
            boolean b = false;
            if (this.originatingProvider != null) {
                final Class[] inputTypes = this.originatingProvider.getInputTypes();
                for (int i = 0; i < inputTypes.length; ++i) {
                    if (inputTypes[i].isInstance(input)) {
                        b = true;
                        break;
                    }
                }
            }
            else if (input instanceof ImageInputStream) {
                b = true;
            }
            if (!b) {
                throw new IllegalArgumentException("Incorrect input type!");
            }
            this.seekForwardOnly = seekForwardOnly;
            this.ignoreMetadata = ignoreMetadata;
            this.minIndex = 0;
        }
        this.input = input;
    }
    
    public void setInput(final Object o, final boolean b) {
        this.setInput(o, b, false);
    }
    
    public void setInput(final Object o) {
        this.setInput(o, false, false);
    }
    
    public Object getInput() {
        return this.input;
    }
    
    public boolean isSeekForwardOnly() {
        return this.seekForwardOnly;
    }
    
    public boolean isIgnoringMetadata() {
        return this.ignoreMetadata;
    }
    
    public int getMinIndex() {
        return this.minIndex;
    }
    
    public Locale[] getAvailableLocales() {
        if (this.availableLocales == null) {
            return null;
        }
        return this.availableLocales.clone();
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
    
    public abstract int getNumImages(final boolean p0) throws IOException;
    
    public abstract int getWidth(final int p0) throws IOException;
    
    public abstract int getHeight(final int p0) throws IOException;
    
    public boolean isRandomAccessEasy(final int n) throws IOException {
        return false;
    }
    
    public float getAspectRatio(final int n) throws IOException {
        return this.getWidth(n) / (float)this.getHeight(n);
    }
    
    public ImageTypeSpecifier getRawImageType(final int n) throws IOException {
        return this.getImageTypes(n).next();
    }
    
    public abstract Iterator<ImageTypeSpecifier> getImageTypes(final int p0) throws IOException;
    
    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }
    
    public abstract IIOMetadata getStreamMetadata() throws IOException;
    
    public IIOMetadata getStreamMetadata(final String s, final Set<String> set) throws IOException {
        return this.getMetadata(s, set, true, 0);
    }
    
    private IIOMetadata getMetadata(final String s, final Set set, final boolean b, final int n) throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        if (set == null) {
            throw new IllegalArgumentException("nodeNames == null!");
        }
        final IIOMetadata iioMetadata = b ? this.getStreamMetadata() : this.getImageMetadata(n);
        if (iioMetadata != null) {
            if (iioMetadata.isStandardMetadataFormatSupported() && s.equals("javax_imageio_1.0")) {
                return iioMetadata;
            }
            final String nativeMetadataFormatName = iioMetadata.getNativeMetadataFormatName();
            if (nativeMetadataFormatName != null && s.equals(nativeMetadataFormatName)) {
                return iioMetadata;
            }
            final String[] extraMetadataFormatNames = iioMetadata.getExtraMetadataFormatNames();
            if (extraMetadataFormatNames != null) {
                for (int i = 0; i < extraMetadataFormatNames.length; ++i) {
                    if (s.equals(extraMetadataFormatNames[i])) {
                        return iioMetadata;
                    }
                }
            }
        }
        return null;
    }
    
    public abstract IIOMetadata getImageMetadata(final int p0) throws IOException;
    
    public IIOMetadata getImageMetadata(final int n, final String s, final Set<String> set) throws IOException {
        return this.getMetadata(s, set, false, n);
    }
    
    public BufferedImage read(final int n) throws IOException {
        return this.read(n, null);
    }
    
    public abstract BufferedImage read(final int p0, final ImageReadParam p1) throws IOException;
    
    public IIOImage readAll(final int n, final ImageReadParam imageReadParam) throws IOException {
        if (n < this.getMinIndex()) {
            throw new IndexOutOfBoundsException("imageIndex < getMinIndex()!");
        }
        final BufferedImage read = this.read(n, imageReadParam);
        ArrayList<BufferedImage> list = null;
        final int numThumbnails = this.getNumThumbnails(n);
        if (numThumbnails > 0) {
            list = new ArrayList<BufferedImage>();
            for (int i = 0; i < numThumbnails; ++i) {
                list.add(this.readThumbnail(n, i));
            }
        }
        return new IIOImage(read, list, this.getImageMetadata(n));
    }
    
    public Iterator<IIOImage> readAll(final Iterator<? extends ImageReadParam> iterator) throws IOException {
        final ArrayList list = new ArrayList();
        int minIndex = this.getMinIndex();
        this.processSequenceStarted(minIndex);
        while (true) {
            ImageReadParam imageReadParam = null;
            if (iterator != null && iterator.hasNext()) {
                final ImageReadParam next = iterator.next();
                if (next != null) {
                    if (!(next instanceof ImageReadParam)) {
                        throw new IllegalArgumentException("Non-ImageReadParam supplied as part of params!");
                    }
                    imageReadParam = next;
                }
            }
            Label_0185: {
                BufferedImage read;
                try {
                    read = this.read(minIndex, imageReadParam);
                }
                catch (final IndexOutOfBoundsException ex) {
                    break Label_0185;
                }
                ArrayList<BufferedImage> list2 = null;
                final int numThumbnails = this.getNumThumbnails(minIndex);
                if (numThumbnails > 0) {
                    list2 = new ArrayList<BufferedImage>();
                    for (int i = 0; i < numThumbnails; ++i) {
                        list2.add(this.readThumbnail(minIndex, i));
                    }
                }
                list.add(new IIOImage(read, list2, this.getImageMetadata(minIndex)));
                ++minIndex;
                continue;
            }
            this.processSequenceComplete();
            return list.iterator();
        }
    }
    
    public boolean canReadRaster() {
        return false;
    }
    
    public Raster readRaster(final int n, final ImageReadParam imageReadParam) throws IOException {
        throw new UnsupportedOperationException("readRaster not supported!");
    }
    
    public boolean isImageTiled(final int n) throws IOException {
        return false;
    }
    
    public int getTileWidth(final int n) throws IOException {
        return this.getWidth(n);
    }
    
    public int getTileHeight(final int n) throws IOException {
        return this.getHeight(n);
    }
    
    public int getTileGridXOffset(final int n) throws IOException {
        return 0;
    }
    
    public int getTileGridYOffset(final int n) throws IOException {
        return 0;
    }
    
    public BufferedImage readTile(final int n, final int n2, final int n3) throws IOException {
        if (n2 != 0 || n3 != 0) {
            throw new IllegalArgumentException("Invalid tile indices");
        }
        return this.read(n);
    }
    
    public Raster readTileRaster(final int n, final int n2, final int n3) throws IOException {
        if (!this.canReadRaster()) {
            throw new UnsupportedOperationException("readTileRaster not supported!");
        }
        if (n2 != 0 || n3 != 0) {
            throw new IllegalArgumentException("Invalid tile indices");
        }
        return this.readRaster(n, null);
    }
    
    public RenderedImage readAsRenderedImage(final int n, final ImageReadParam imageReadParam) throws IOException {
        return this.read(n, imageReadParam);
    }
    
    public boolean readerSupportsThumbnails() {
        return false;
    }
    
    public boolean hasThumbnails(final int n) throws IOException {
        return this.getNumThumbnails(n) > 0;
    }
    
    public int getNumThumbnails(final int n) throws IOException {
        return 0;
    }
    
    public int getThumbnailWidth(final int n, final int n2) throws IOException {
        return this.readThumbnail(n, n2).getWidth();
    }
    
    public int getThumbnailHeight(final int n, final int n2) throws IOException {
        return this.readThumbnail(n, n2).getHeight();
    }
    
    public BufferedImage readThumbnail(final int n, final int n2) throws IOException {
        throw new UnsupportedOperationException("Thumbnails not supported!");
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
    
    static List addToList(List list, final Object o) {
        if (list == null) {
            list = new ArrayList();
        }
        list.add(o);
        return list;
    }
    
    static List removeFromList(List list, final Object o) {
        if (list == null) {
            return list;
        }
        list.remove(o);
        if (list.size() == 0) {
            list = null;
        }
        return list;
    }
    
    public void addIIOReadWarningListener(final IIOReadWarningListener iioReadWarningListener) {
        if (iioReadWarningListener == null) {
            return;
        }
        this.warningListeners = addToList(this.warningListeners, iioReadWarningListener);
        this.warningLocales = addToList(this.warningLocales, this.getLocale());
    }
    
    public void removeIIOReadWarningListener(final IIOReadWarningListener iioReadWarningListener) {
        if (iioReadWarningListener == null || this.warningListeners == null) {
            return;
        }
        final int index = this.warningListeners.indexOf(iioReadWarningListener);
        if (index != -1) {
            this.warningListeners.remove(index);
            this.warningLocales.remove(index);
            if (this.warningListeners.size() == 0) {
                this.warningListeners = null;
                this.warningLocales = null;
            }
        }
    }
    
    public void removeAllIIOReadWarningListeners() {
        this.warningListeners = null;
        this.warningLocales = null;
    }
    
    public void addIIOReadProgressListener(final IIOReadProgressListener iioReadProgressListener) {
        if (iioReadProgressListener == null) {
            return;
        }
        this.progressListeners = addToList(this.progressListeners, iioReadProgressListener);
    }
    
    public void removeIIOReadProgressListener(final IIOReadProgressListener iioReadProgressListener) {
        if (iioReadProgressListener == null || this.progressListeners == null) {
            return;
        }
        this.progressListeners = removeFromList(this.progressListeners, iioReadProgressListener);
    }
    
    public void removeAllIIOReadProgressListeners() {
        this.progressListeners = null;
    }
    
    public void addIIOReadUpdateListener(final IIOReadUpdateListener iioReadUpdateListener) {
        if (iioReadUpdateListener == null) {
            return;
        }
        this.updateListeners = addToList(this.updateListeners, iioReadUpdateListener);
    }
    
    public void removeIIOReadUpdateListener(final IIOReadUpdateListener iioReadUpdateListener) {
        if (iioReadUpdateListener == null || this.updateListeners == null) {
            return;
        }
        this.updateListeners = removeFromList(this.updateListeners, iioReadUpdateListener);
    }
    
    public void removeAllIIOReadUpdateListeners() {
        this.updateListeners = null;
    }
    
    protected void processSequenceStarted(final int n) {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).sequenceStarted(this, n);
        }
    }
    
    protected void processSequenceComplete() {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).sequenceComplete(this);
        }
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
    
    protected void processReadAborted() {
        if (this.progressListeners == null) {
            return;
        }
        for (int size = this.progressListeners.size(), i = 0; i < size; ++i) {
            this.progressListeners.get(i).readAborted(this);
        }
    }
    
    protected void processPassStarted(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int[] array) {
        if (this.updateListeners == null) {
            return;
        }
        for (int size = this.updateListeners.size(), i = 0; i < size; ++i) {
            this.updateListeners.get(i).passStarted(this, bufferedImage, n, n2, n3, n4, n5, n6, n7, array);
        }
    }
    
    protected void processImageUpdate(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        if (this.updateListeners == null) {
            return;
        }
        for (int size = this.updateListeners.size(), i = 0; i < size; ++i) {
            this.updateListeners.get(i).imageUpdate(this, bufferedImage, n, n2, n3, n4, n5, n6, array);
        }
    }
    
    protected void processPassComplete(final BufferedImage bufferedImage) {
        if (this.updateListeners == null) {
            return;
        }
        for (int size = this.updateListeners.size(), i = 0; i < size; ++i) {
            this.updateListeners.get(i).passComplete(this, bufferedImage);
        }
    }
    
    protected void processThumbnailPassStarted(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int[] array) {
        if (this.updateListeners == null) {
            return;
        }
        for (int size = this.updateListeners.size(), i = 0; i < size; ++i) {
            this.updateListeners.get(i).thumbnailPassStarted(this, bufferedImage, n, n2, n3, n4, n5, n6, n7, array);
        }
    }
    
    protected void processThumbnailUpdate(final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
        if (this.updateListeners == null) {
            return;
        }
        for (int size = this.updateListeners.size(), i = 0; i < size; ++i) {
            this.updateListeners.get(i).thumbnailUpdate(this, bufferedImage, n, n2, n3, n4, n5, n6, array);
        }
    }
    
    protected void processThumbnailPassComplete(final BufferedImage bufferedImage) {
        if (this.updateListeners == null) {
            return;
        }
        for (int size = this.updateListeners.size(), i = 0; i < size; ++i) {
            this.updateListeners.get(i).thumbnailPassComplete(this, bufferedImage);
        }
    }
    
    protected void processWarningOccurred(final String s) {
        if (this.warningListeners == null) {
            return;
        }
        if (s == null) {
            throw new IllegalArgumentException("warning == null!");
        }
        for (int size = this.warningListeners.size(), i = 0; i < size; ++i) {
            this.warningListeners.get(i).warningOccurred(this, s);
        }
    }
    
    protected void processWarningOccurred(final String s, final String s2) {
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
            final IIOReadWarningListener iioReadWarningListener = this.warningListeners.get(i);
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
            iioReadWarningListener.warningOccurred(this, string);
        }
    }
    
    public void reset() {
        this.setInput(null, false, false);
        this.setLocale(null);
        this.removeAllIIOReadUpdateListeners();
        this.removeAllIIOReadProgressListeners();
        this.removeAllIIOReadWarningListeners();
        this.clearAbortRequest();
    }
    
    public void dispose() {
    }
    
    protected static Rectangle getSourceRegion(final ImageReadParam imageReadParam, final int n, final int n2) {
        Rectangle intersection = new Rectangle(0, 0, n, n2);
        if (imageReadParam != null) {
            final Rectangle sourceRegion = imageReadParam.getSourceRegion();
            if (sourceRegion != null) {
                intersection = intersection.intersection(sourceRegion);
            }
            final int subsamplingXOffset = imageReadParam.getSubsamplingXOffset();
            final int subsamplingYOffset = imageReadParam.getSubsamplingYOffset();
            final Rectangle rectangle = intersection;
            rectangle.x += subsamplingXOffset;
            final Rectangle rectangle2 = intersection;
            rectangle2.y += subsamplingYOffset;
            final Rectangle rectangle3 = intersection;
            rectangle3.width -= subsamplingXOffset;
            final Rectangle rectangle4 = intersection;
            rectangle4.height -= subsamplingYOffset;
        }
        return intersection;
    }
    
    protected static void computeRegions(final ImageReadParam imageReadParam, final int n, final int n2, final BufferedImage bufferedImage, final Rectangle rectangle, final Rectangle rectangle2) {
        if (rectangle == null) {
            throw new IllegalArgumentException("srcRegion == null!");
        }
        if (rectangle2 == null) {
            throw new IllegalArgumentException("destRegion == null!");
        }
        rectangle.setBounds(0, 0, n, n2);
        rectangle2.setBounds(0, 0, n, n2);
        int sourceXSubsampling = 1;
        int sourceYSubsampling = 1;
        if (imageReadParam != null) {
            final Rectangle sourceRegion = imageReadParam.getSourceRegion();
            if (sourceRegion != null) {
                rectangle.setBounds(rectangle.intersection(sourceRegion));
            }
            sourceXSubsampling = imageReadParam.getSourceXSubsampling();
            sourceYSubsampling = imageReadParam.getSourceYSubsampling();
            final int subsamplingXOffset = imageReadParam.getSubsamplingXOffset();
            final int subsamplingYOffset = imageReadParam.getSubsamplingYOffset();
            rectangle.translate(subsamplingXOffset, subsamplingYOffset);
            rectangle.width -= subsamplingXOffset;
            rectangle.height -= subsamplingYOffset;
            rectangle2.setLocation(imageReadParam.getDestinationOffset());
        }
        if (rectangle2.x < 0) {
            final int n3 = -rectangle2.x * sourceXSubsampling;
            rectangle.x += n3;
            rectangle.width -= n3;
            rectangle2.x = 0;
        }
        if (rectangle2.y < 0) {
            final int n4 = -rectangle2.y * sourceYSubsampling;
            rectangle.y += n4;
            rectangle.height -= n4;
            rectangle2.y = 0;
        }
        final int width = (rectangle.width + sourceXSubsampling - 1) / sourceXSubsampling;
        final int height = (rectangle.height + sourceYSubsampling - 1) / sourceYSubsampling;
        rectangle2.width = width;
        rectangle2.height = height;
        if (bufferedImage != null) {
            rectangle2.setBounds(rectangle2.intersection(new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight())));
            if (rectangle2.isEmpty()) {
                throw new IllegalArgumentException("Empty destination region!");
            }
            final int n5 = rectangle2.x + width - bufferedImage.getWidth();
            if (n5 > 0) {
                rectangle.width -= n5 * sourceXSubsampling;
            }
            final int n6 = rectangle2.y + height - bufferedImage.getHeight();
            if (n6 > 0) {
                rectangle.height -= n6 * sourceYSubsampling;
            }
        }
        if (rectangle.isEmpty() || rectangle2.isEmpty()) {
            throw new IllegalArgumentException("Empty region!");
        }
    }
    
    protected static void checkReadParamBandSettings(final ImageReadParam imageReadParam, final int n, final int n2) {
        int[] sourceBands = null;
        int[] destinationBands = null;
        if (imageReadParam != null) {
            sourceBands = imageReadParam.getSourceBands();
            destinationBands = imageReadParam.getDestinationBands();
        }
        if (((sourceBands == null) ? n : sourceBands.length) != ((destinationBands == null) ? n2 : destinationBands.length)) {
            throw new IllegalArgumentException("ImageReadParam num source & dest bands differ!");
        }
        if (sourceBands != null) {
            for (int i = 0; i < sourceBands.length; ++i) {
                if (sourceBands[i] >= n) {
                    throw new IllegalArgumentException("ImageReadParam source bands contains a value >= the number of source bands!");
                }
            }
        }
        if (destinationBands != null) {
            for (int j = 0; j < destinationBands.length; ++j) {
                if (destinationBands[j] >= n2) {
                    throw new IllegalArgumentException("ImageReadParam dest bands contains a value >= the number of dest bands!");
                }
            }
        }
    }
    
    protected static BufferedImage getDestination(final ImageReadParam imageReadParam, final Iterator<ImageTypeSpecifier> iterator, final int n, final int n2) throws IIOException {
        if (iterator == null || !iterator.hasNext()) {
            throw new IllegalArgumentException("imageTypes null or empty!");
        }
        if (n * (long)n2 > 2147483647L) {
            throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!");
        }
        ImageTypeSpecifier destinationType = null;
        if (imageReadParam != null) {
            final BufferedImage destination = imageReadParam.getDestination();
            if (destination != null) {
                return destination;
            }
            destinationType = imageReadParam.getDestinationType();
        }
        if (destinationType == null) {
            final ImageTypeSpecifier next = iterator.next();
            if (!(next instanceof ImageTypeSpecifier)) {
                throw new IllegalArgumentException("Non-ImageTypeSpecifier retrieved from imageTypes!");
            }
            destinationType = next;
        }
        else {
            boolean b = false;
            while (iterator.hasNext()) {
                if (iterator.next().equals(destinationType)) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                throw new IIOException("Destination type from ImageReadParam does not match!");
            }
        }
        final Rectangle rectangle = new Rectangle(0, 0, 0, 0);
        final Rectangle rectangle2 = new Rectangle(0, 0, 0, 0);
        computeRegions(imageReadParam, n, n2, null, rectangle, rectangle2);
        return destinationType.createBufferedImage(rectangle2.x + rectangle2.width, rectangle2.y + rectangle2.height);
    }
}
