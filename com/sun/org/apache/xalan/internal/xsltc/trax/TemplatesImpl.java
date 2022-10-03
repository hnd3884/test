package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import javax.xml.transform.Transformer;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import java.util.HashMap;
import java.security.AccessController;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import java.security.PrivilegedAction;
import javax.xml.transform.TransformerConfigurationException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.IOException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import javax.xml.transform.URIResolver;
import java.util.Properties;
import java.util.Map;
import java.io.Serializable;
import javax.xml.transform.Templates;

public final class TemplatesImpl implements Templates, Serializable
{
    static final long serialVersionUID = 673094361519270707L;
    public static final String DESERIALIZE_TRANSLET = "jdk.xml.enableTemplatesImplDeserialization";
    private static String ABSTRACT_TRANSLET;
    private String _name;
    private byte[][] _bytecodes;
    private Class[] _class;
    private int _transletIndex;
    private transient Map<String, Class<?>> _auxClasses;
    private Properties _outputProperties;
    private int _indentNumber;
    private transient URIResolver _uriResolver;
    private transient ThreadLocal _sdom;
    private transient TransformerFactoryImpl _tfactory;
    private transient boolean _overrideDefaultParser;
    private transient String _accessExternalStylesheet;
    private static final ObjectStreamField[] serialPersistentFields;
    
    protected TemplatesImpl(final byte[][] bytecodes, final String transletName, final Properties outputProperties, final int indentNumber, final TransformerFactoryImpl tfactory) {
        this._name = null;
        this._bytecodes = null;
        this._class = null;
        this._transletIndex = -1;
        this._auxClasses = null;
        this._uriResolver = null;
        this._sdom = new ThreadLocal();
        this._tfactory = null;
        this._accessExternalStylesheet = "all";
        this._bytecodes = bytecodes;
        this.init(transletName, outputProperties, indentNumber, tfactory);
    }
    
    protected TemplatesImpl(final Class[] transletClasses, final String transletName, final Properties outputProperties, final int indentNumber, final TransformerFactoryImpl tfactory) {
        this._name = null;
        this._bytecodes = null;
        this._class = null;
        this._transletIndex = -1;
        this._auxClasses = null;
        this._uriResolver = null;
        this._sdom = new ThreadLocal();
        this._tfactory = null;
        this._accessExternalStylesheet = "all";
        this._class = transletClasses;
        this._transletIndex = 0;
        this.init(transletName, outputProperties, indentNumber, tfactory);
    }
    
    private void init(final String transletName, final Properties outputProperties, final int indentNumber, final TransformerFactoryImpl tfactory) {
        this._name = transletName;
        this._outputProperties = outputProperties;
        this._indentNumber = indentNumber;
        this._tfactory = tfactory;
        this._overrideDefaultParser = tfactory.overrideDefaultParser();
        this._accessExternalStylesheet = (String)tfactory.getAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet");
    }
    
    public TemplatesImpl() {
        this._name = null;
        this._bytecodes = null;
        this._class = null;
        this._transletIndex = -1;
        this._auxClasses = null;
        this._uriResolver = null;
        this._sdom = new ThreadLocal();
        this._tfactory = null;
        this._accessExternalStylesheet = "all";
    }
    
    private void readObject(final ObjectInputStream is) throws IOException, ClassNotFoundException {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            final String temp = SecuritySupport.getSystemProperty("jdk.xml.enableTemplatesImplDeserialization");
            if (temp == null || (temp.length() != 0 && !temp.equalsIgnoreCase("true"))) {
                final ErrorMsg err = new ErrorMsg("DESERIALIZE_TEMPLATES_ERR");
                throw new UnsupportedOperationException(err.toString());
            }
        }
        final ObjectInputStream.GetField gf = is.readFields();
        this._name = (String)gf.get("_name", null);
        this._bytecodes = (byte[][])gf.get("_bytecodes", null);
        this._class = (Class[])gf.get("_class", null);
        this._transletIndex = gf.get("_transletIndex", -1);
        this._outputProperties = (Properties)gf.get("_outputProperties", null);
        this._indentNumber = gf.get("_indentNumber", 0);
        if (is.readBoolean()) {
            this._uriResolver = (URIResolver)is.readObject();
        }
        this._tfactory = new TransformerFactoryImpl();
    }
    
    private void writeObject(final ObjectOutputStream os) throws IOException, ClassNotFoundException {
        if (this._auxClasses != null) {
            throw new NotSerializableException("com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable");
        }
        final ObjectOutputStream.PutField pf = os.putFields();
        pf.put("_name", this._name);
        pf.put("_bytecodes", this._bytecodes);
        pf.put("_class", this._class);
        pf.put("_transletIndex", this._transletIndex);
        pf.put("_outputProperties", this._outputProperties);
        pf.put("_indentNumber", this._indentNumber);
        os.writeFields();
        if (this._uriResolver instanceof Serializable) {
            os.writeBoolean(true);
            os.writeObject(this._uriResolver);
        }
        else {
            os.writeBoolean(false);
        }
    }
    
    public boolean overrideDefaultParser() {
        return this._overrideDefaultParser;
    }
    
    public synchronized void setURIResolver(final URIResolver resolver) {
        this._uriResolver = resolver;
    }
    
    private synchronized void setTransletBytecodes(final byte[][] bytecodes) {
        this._bytecodes = bytecodes;
    }
    
    private synchronized byte[][] getTransletBytecodes() {
        return this._bytecodes;
    }
    
    private synchronized Class[] getTransletClasses() {
        try {
            if (this._class == null) {
                this.defineTransletClasses();
            }
        }
        catch (final TransformerConfigurationException ex) {}
        return this._class;
    }
    
    public synchronized int getTransletIndex() {
        try {
            if (this._class == null) {
                this.defineTransletClasses();
            }
        }
        catch (final TransformerConfigurationException ex) {}
        return this._transletIndex;
    }
    
    protected synchronized void setTransletName(final String name) {
        this._name = name;
    }
    
    protected synchronized String getTransletName() {
        return this._name;
    }
    
    private void defineTransletClasses() throws TransformerConfigurationException {
        if (this._bytecodes == null) {
            final ErrorMsg err = new ErrorMsg("NO_TRANSLET_CLASS_ERR");
            throw new TransformerConfigurationException(err.toString());
        }
        final TransletClassLoader loader = AccessController.doPrivileged((PrivilegedAction<TransletClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return new TransletClassLoader(ObjectFactory.findClassLoader(), TemplatesImpl.this._tfactory.getExternalExtensionsMap());
            }
        });
        try {
            final int classCount = this._bytecodes.length;
            this._class = new Class[classCount];
            if (classCount > 1) {
                this._auxClasses = new HashMap<String, Class<?>>();
            }
            for (int i = 0; i < classCount; ++i) {
                this._class[i] = loader.defineClass(this._bytecodes[i]);
                final Class superClass = this._class[i].getSuperclass();
                if (superClass.getName().equals(TemplatesImpl.ABSTRACT_TRANSLET)) {
                    this._transletIndex = i;
                }
                else {
                    this._auxClasses.put(this._class[i].getName(), this._class[i]);
                }
            }
            if (this._transletIndex < 0) {
                final ErrorMsg err2 = new ErrorMsg("NO_MAIN_TRANSLET_ERR", this._name);
                throw new TransformerConfigurationException(err2.toString());
            }
        }
        catch (final ClassFormatError e) {
            final ErrorMsg err2 = new ErrorMsg("TRANSLET_CLASS_ERR", this._name);
            throw new TransformerConfigurationException(err2.toString());
        }
        catch (final LinkageError e2) {
            final ErrorMsg err2 = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
            throw new TransformerConfigurationException(err2.toString());
        }
    }
    
    private Translet getTransletInstance() throws TransformerConfigurationException {
        try {
            if (this._name == null) {
                return null;
            }
            if (this._class == null) {
                this.defineTransletClasses();
            }
            final AbstractTranslet translet = this._class[this._transletIndex].newInstance();
            translet.postInitialization();
            translet.setTemplates(this);
            translet.setOverrideDefaultParser(this._overrideDefaultParser);
            translet.setAllowedProtocols(this._accessExternalStylesheet);
            if (this._auxClasses != null) {
                translet.setAuxiliaryClasses(this._auxClasses);
            }
            return translet;
        }
        catch (final InstantiationException e) {
            final ErrorMsg err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
            throw new TransformerConfigurationException(err.toString());
        }
        catch (final IllegalAccessException e2) {
            final ErrorMsg err = new ErrorMsg("TRANSLET_OBJECT_ERR", this._name);
            throw new TransformerConfigurationException(err.toString());
        }
    }
    
    @Override
    public synchronized Transformer newTransformer() throws TransformerConfigurationException {
        final TransformerImpl transformer = new TransformerImpl(this.getTransletInstance(), this._outputProperties, this._indentNumber, this._tfactory);
        if (this._uriResolver != null) {
            transformer.setURIResolver(this._uriResolver);
        }
        if (this._tfactory.getFeature("http://javax.xml.XMLConstants/feature/secure-processing")) {
            transformer.setSecureProcessing(true);
        }
        return transformer;
    }
    
    @Override
    public synchronized Properties getOutputProperties() {
        try {
            return this.newTransformer().getOutputProperties();
        }
        catch (final TransformerConfigurationException e) {
            return null;
        }
    }
    
    public DOM getStylesheetDOM() {
        return this._sdom.get();
    }
    
    public void setStylesheetDOM(final DOM sdom) {
        this._sdom.set(sdom);
    }
    
    static {
        TemplatesImpl.ABSTRACT_TRANSLET = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("_name", String.class), new ObjectStreamField("_bytecodes", byte[][].class), new ObjectStreamField("_class", Class[].class), new ObjectStreamField("_transletIndex", Integer.TYPE), new ObjectStreamField("_outputProperties", Properties.class), new ObjectStreamField("_indentNumber", Integer.TYPE) };
    }
    
    static final class TransletClassLoader extends ClassLoader
    {
        private final Map<String, Class> _loadedExternalExtensionFunctions;
        
        TransletClassLoader(final ClassLoader parent) {
            super(parent);
            this._loadedExternalExtensionFunctions = null;
        }
        
        TransletClassLoader(final ClassLoader parent, final Map<String, Class> mapEF) {
            super(parent);
            this._loadedExternalExtensionFunctions = mapEF;
        }
        
        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException {
            Class<?> ret = null;
            if (this._loadedExternalExtensionFunctions != null) {
                ret = this._loadedExternalExtensionFunctions.get(name);
            }
            if (ret == null) {
                ret = super.loadClass(name);
            }
            return ret;
        }
        
        Class defineClass(final byte[] b) {
            return this.defineClass(null, b, 0, b.length);
        }
    }
}
