package javax.imageio.metadata;

import org.w3c.dom.Node;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class IIOMetadata
{
    protected boolean standardFormatSupported;
    protected String nativeMetadataFormatName;
    protected String nativeMetadataFormatClassName;
    protected String[] extraMetadataFormatNames;
    protected String[] extraMetadataFormatClassNames;
    protected IIOMetadataController defaultController;
    protected IIOMetadataController controller;
    
    protected IIOMetadata() {
        this.nativeMetadataFormatName = null;
        this.nativeMetadataFormatClassName = null;
        this.extraMetadataFormatNames = null;
        this.extraMetadataFormatClassNames = null;
        this.defaultController = null;
        this.controller = null;
    }
    
    protected IIOMetadata(final boolean standardFormatSupported, final String nativeMetadataFormatName, final String nativeMetadataFormatClassName, final String[] array, final String[] array2) {
        this.nativeMetadataFormatName = null;
        this.nativeMetadataFormatClassName = null;
        this.extraMetadataFormatNames = null;
        this.extraMetadataFormatClassNames = null;
        this.defaultController = null;
        this.controller = null;
        this.standardFormatSupported = standardFormatSupported;
        this.nativeMetadataFormatName = nativeMetadataFormatName;
        this.nativeMetadataFormatClassName = nativeMetadataFormatClassName;
        if (array != null) {
            if (array.length == 0) {
                throw new IllegalArgumentException("extraMetadataFormatNames.length == 0!");
            }
            if (array2 == null) {
                throw new IllegalArgumentException("extraMetadataFormatNames != null && extraMetadataFormatClassNames == null!");
            }
            if (array2.length != array.length) {
                throw new IllegalArgumentException("extraMetadataFormatClassNames.length != extraMetadataFormatNames.length!");
            }
            this.extraMetadataFormatNames = array.clone();
            this.extraMetadataFormatClassNames = array2.clone();
        }
        else if (array2 != null) {
            throw new IllegalArgumentException("extraMetadataFormatNames == null && extraMetadataFormatClassNames != null!");
        }
    }
    
    public boolean isStandardMetadataFormatSupported() {
        return this.standardFormatSupported;
    }
    
    public abstract boolean isReadOnly();
    
    public String getNativeMetadataFormatName() {
        return this.nativeMetadataFormatName;
    }
    
    public String[] getExtraMetadataFormatNames() {
        if (this.extraMetadataFormatNames == null) {
            return null;
        }
        return this.extraMetadataFormatNames.clone();
    }
    
    public String[] getMetadataFormatNames() {
        final String nativeMetadataFormatName = this.getNativeMetadataFormatName();
        final String s = this.isStandardMetadataFormatSupported() ? "javax_imageio_1.0" : null;
        final String[] extraMetadataFormatNames = this.getExtraMetadataFormatNames();
        int n = 0;
        if (nativeMetadataFormatName != null) {
            ++n;
        }
        if (s != null) {
            ++n;
        }
        if (extraMetadataFormatNames != null) {
            n += extraMetadataFormatNames.length;
        }
        if (n == 0) {
            return null;
        }
        final String[] array = new String[n];
        int n2 = 0;
        if (nativeMetadataFormatName != null) {
            array[n2++] = nativeMetadataFormatName;
        }
        if (s != null) {
            array[n2++] = s;
        }
        if (extraMetadataFormatNames != null) {
            for (int i = 0; i < extraMetadataFormatNames.length; ++i) {
                array[n2++] = extraMetadataFormatNames[i];
            }
        }
        return array;
    }
    
    public IIOMetadataFormat getMetadataFormat(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        if (this.standardFormatSupported && s.equals("javax_imageio_1.0")) {
            return IIOMetadataFormatImpl.getStandardFormatInstance();
        }
        String nativeMetadataFormatClassName = null;
        if (s.equals(this.nativeMetadataFormatName)) {
            nativeMetadataFormatClassName = this.nativeMetadataFormatClassName;
        }
        else if (this.extraMetadataFormatNames != null) {
            for (int i = 0; i < this.extraMetadataFormatNames.length; ++i) {
                if (s.equals(this.extraMetadataFormatNames[i])) {
                    nativeMetadataFormatClassName = this.extraMetadataFormatClassNames[i];
                    break;
                }
            }
        }
        if (nativeMetadataFormatClassName == null) {
            throw new IllegalArgumentException("Unsupported format name");
        }
        try {
            final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
                final /* synthetic */ Object val$o;
                
                @Override
                public Object run() {
                    return this.val$o.getClass().getClassLoader();
                }
            });
            Class<?> clazz;
            try {
                clazz = Class.forName(nativeMetadataFormatClassName, true, classLoader);
            }
            catch (final ClassNotFoundException ex) {
                final ClassLoader classLoader2 = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
                try {
                    clazz = Class.forName(nativeMetadataFormatClassName, true, classLoader2);
                }
                catch (final ClassNotFoundException ex2) {
                    clazz = Class.forName(nativeMetadataFormatClassName, true, ClassLoader.getSystemClassLoader());
                }
            }
            return (IIOMetadataFormat)clazz.getMethod("getInstance", (Class[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (final Exception ex3) {
            final IllegalStateException ex4 = new IllegalStateException("Can't obtain format");
            ex4.initCause(ex3);
            throw ex4;
        }
    }
    
    public abstract Node getAsTree(final String p0);
    
    public abstract void mergeTree(final String p0, final Node p1) throws IIOInvalidTreeException;
    
    protected IIOMetadataNode getStandardChromaNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardCompressionNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardDataNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardDimensionNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardDocumentNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardTextNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardTileNode() {
        return null;
    }
    
    protected IIOMetadataNode getStandardTransparencyNode() {
        return null;
    }
    
    private void append(final IIOMetadataNode iioMetadataNode, final IIOMetadataNode iioMetadataNode2) {
        if (iioMetadataNode2 != null) {
            iioMetadataNode.appendChild(iioMetadataNode2);
        }
    }
    
    protected final IIOMetadataNode getStandardTree() {
        final IIOMetadataNode iioMetadataNode = new IIOMetadataNode("javax_imageio_1.0");
        this.append(iioMetadataNode, this.getStandardChromaNode());
        this.append(iioMetadataNode, this.getStandardCompressionNode());
        this.append(iioMetadataNode, this.getStandardDataNode());
        this.append(iioMetadataNode, this.getStandardDimensionNode());
        this.append(iioMetadataNode, this.getStandardDocumentNode());
        this.append(iioMetadataNode, this.getStandardTextNode());
        this.append(iioMetadataNode, this.getStandardTileNode());
        this.append(iioMetadataNode, this.getStandardTransparencyNode());
        return iioMetadataNode;
    }
    
    public void setFromTree(final String s, final Node node) throws IIOInvalidTreeException {
        this.reset();
        this.mergeTree(s, node);
    }
    
    public abstract void reset();
    
    public void setController(final IIOMetadataController controller) {
        this.controller = controller;
    }
    
    public IIOMetadataController getController() {
        return this.controller;
    }
    
    public IIOMetadataController getDefaultController() {
        return this.defaultController;
    }
    
    public boolean hasController() {
        return this.getController() != null;
    }
    
    public boolean activateController() {
        if (!this.hasController()) {
            throw new IllegalStateException("hasController() == false!");
        }
        return this.getController().activate(this);
    }
}
