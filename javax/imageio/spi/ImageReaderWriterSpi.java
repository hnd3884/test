package javax.imageio.spi;

import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataFormat;

public abstract class ImageReaderWriterSpi extends IIOServiceProvider
{
    protected String[] names;
    protected String[] suffixes;
    protected String[] MIMETypes;
    protected String pluginClassName;
    protected boolean supportsStandardStreamMetadataFormat;
    protected String nativeStreamMetadataFormatName;
    protected String nativeStreamMetadataFormatClassName;
    protected String[] extraStreamMetadataFormatNames;
    protected String[] extraStreamMetadataFormatClassNames;
    protected boolean supportsStandardImageMetadataFormat;
    protected String nativeImageMetadataFormatName;
    protected String nativeImageMetadataFormatClassName;
    protected String[] extraImageMetadataFormatNames;
    protected String[] extraImageMetadataFormatClassNames;
    
    public ImageReaderWriterSpi(final String s, final String s2, final String[] array, final String[] array2, final String[] array3, final String pluginClassName, final boolean supportsStandardStreamMetadataFormat, final String nativeStreamMetadataFormatName, final String nativeStreamMetadataFormatClassName, final String[] array4, final String[] array5, final boolean supportsStandardImageMetadataFormat, final String nativeImageMetadataFormatName, final String nativeImageMetadataFormatClassName, final String[] array6, final String[] array7) {
        super(s, s2);
        this.names = null;
        this.suffixes = null;
        this.MIMETypes = null;
        this.pluginClassName = null;
        this.supportsStandardStreamMetadataFormat = false;
        this.nativeStreamMetadataFormatName = null;
        this.nativeStreamMetadataFormatClassName = null;
        this.extraStreamMetadataFormatNames = null;
        this.extraStreamMetadataFormatClassNames = null;
        this.supportsStandardImageMetadataFormat = false;
        this.nativeImageMetadataFormatName = null;
        this.nativeImageMetadataFormatClassName = null;
        this.extraImageMetadataFormatNames = null;
        this.extraImageMetadataFormatClassNames = null;
        if (array == null) {
            throw new IllegalArgumentException("names == null!");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("names.length == 0!");
        }
        if (pluginClassName == null) {
            throw new IllegalArgumentException("pluginClassName == null!");
        }
        this.names = array.clone();
        if (array2 != null && array2.length > 0) {
            this.suffixes = array2.clone();
        }
        if (array3 != null && array3.length > 0) {
            this.MIMETypes = array3.clone();
        }
        this.pluginClassName = pluginClassName;
        this.supportsStandardStreamMetadataFormat = supportsStandardStreamMetadataFormat;
        this.nativeStreamMetadataFormatName = nativeStreamMetadataFormatName;
        this.nativeStreamMetadataFormatClassName = nativeStreamMetadataFormatClassName;
        if (array4 != null && array4.length > 0) {
            this.extraStreamMetadataFormatNames = array4.clone();
        }
        if (array5 != null && array5.length > 0) {
            this.extraStreamMetadataFormatClassNames = array5.clone();
        }
        this.supportsStandardImageMetadataFormat = supportsStandardImageMetadataFormat;
        this.nativeImageMetadataFormatName = nativeImageMetadataFormatName;
        this.nativeImageMetadataFormatClassName = nativeImageMetadataFormatClassName;
        if (array6 != null && array6.length > 0) {
            this.extraImageMetadataFormatNames = array6.clone();
        }
        if (array7 != null && array7.length > 0) {
            this.extraImageMetadataFormatClassNames = array7.clone();
        }
    }
    
    public ImageReaderWriterSpi() {
        this.names = null;
        this.suffixes = null;
        this.MIMETypes = null;
        this.pluginClassName = null;
        this.supportsStandardStreamMetadataFormat = false;
        this.nativeStreamMetadataFormatName = null;
        this.nativeStreamMetadataFormatClassName = null;
        this.extraStreamMetadataFormatNames = null;
        this.extraStreamMetadataFormatClassNames = null;
        this.supportsStandardImageMetadataFormat = false;
        this.nativeImageMetadataFormatName = null;
        this.nativeImageMetadataFormatClassName = null;
        this.extraImageMetadataFormatNames = null;
        this.extraImageMetadataFormatClassNames = null;
    }
    
    public String[] getFormatNames() {
        return this.names.clone();
    }
    
    public String[] getFileSuffixes() {
        return (String[])((this.suffixes == null) ? null : ((String[])this.suffixes.clone()));
    }
    
    public String[] getMIMETypes() {
        return (String[])((this.MIMETypes == null) ? null : ((String[])this.MIMETypes.clone()));
    }
    
    public String getPluginClassName() {
        return this.pluginClassName;
    }
    
    public boolean isStandardStreamMetadataFormatSupported() {
        return this.supportsStandardStreamMetadataFormat;
    }
    
    public String getNativeStreamMetadataFormatName() {
        return this.nativeStreamMetadataFormatName;
    }
    
    public String[] getExtraStreamMetadataFormatNames() {
        return (String[])((this.extraStreamMetadataFormatNames == null) ? null : ((String[])this.extraStreamMetadataFormatNames.clone()));
    }
    
    public boolean isStandardImageMetadataFormatSupported() {
        return this.supportsStandardImageMetadataFormat;
    }
    
    public String getNativeImageMetadataFormatName() {
        return this.nativeImageMetadataFormatName;
    }
    
    public String[] getExtraImageMetadataFormatNames() {
        return (String[])((this.extraImageMetadataFormatNames == null) ? null : ((String[])this.extraImageMetadataFormatNames.clone()));
    }
    
    public IIOMetadataFormat getStreamMetadataFormat(final String s) {
        return this.getMetadataFormat(s, this.supportsStandardStreamMetadataFormat, this.nativeStreamMetadataFormatName, this.nativeStreamMetadataFormatClassName, this.extraStreamMetadataFormatNames, this.extraStreamMetadataFormatClassNames);
    }
    
    public IIOMetadataFormat getImageMetadataFormat(final String s) {
        return this.getMetadataFormat(s, this.supportsStandardImageMetadataFormat, this.nativeImageMetadataFormatName, this.nativeImageMetadataFormatClassName, this.extraImageMetadataFormatNames, this.extraImageMetadataFormatClassNames);
    }
    
    private IIOMetadataFormat getMetadataFormat(final String s, final boolean b, final String s2, final String s3, final String[] array, final String[] array2) {
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        if (b && s.equals("javax_imageio_1.0")) {
            return IIOMetadataFormatImpl.getStandardFormatInstance();
        }
        String s4 = null;
        if (s.equals(s2)) {
            s4 = s3;
        }
        else if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (s.equals(array[i])) {
                    s4 = array2[i];
                    break;
                }
            }
        }
        if (s4 == null) {
            throw new IllegalArgumentException("Unsupported format name");
        }
        try {
            return (IIOMetadataFormat)Class.forName(s4, true, ClassLoader.getSystemClassLoader()).getMethod("getInstance", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (final Exception ex) {
            final IllegalStateException ex2 = new IllegalStateException("Can't obtain format");
            ex2.initCause(ex);
            throw ex2;
        }
    }
}
