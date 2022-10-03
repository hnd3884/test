package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.transform.TransformerException;
import org.xml.sax.XMLFilter;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.sax.SAXTransformerFactory;

public class SmartTransformerFactoryImpl extends SAXTransformerFactory
{
    private static final String CLASS_NAME = "SmartTransformerFactoryImpl";
    private SAXTransformerFactory _xsltcFactory;
    private SAXTransformerFactory _xalanFactory;
    private SAXTransformerFactory _currFactory;
    private ErrorListener _errorlistener;
    private URIResolver _uriresolver;
    private boolean featureSecureProcessing;
    
    public SmartTransformerFactoryImpl() {
        this._xsltcFactory = null;
        this._xalanFactory = null;
        this._currFactory = null;
        this._errorlistener = null;
        this._uriresolver = null;
        this.featureSecureProcessing = false;
    }
    
    private void createXSLTCTransformerFactory() {
        this._xsltcFactory = new TransformerFactoryImpl();
        this._currFactory = this._xsltcFactory;
    }
    
    private void createXalanTransformerFactory() {
        final String xalanMessage = "com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.";
        try {
            final Class xalanFactClass = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl", true);
            this._xalanFactory = xalanFactClass.newInstance();
        }
        catch (final ClassNotFoundException e) {
            System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
        }
        catch (final InstantiationException e2) {
            System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
        }
        catch (final IllegalAccessException e3) {
            System.err.println("com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl could not create an com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl.");
        }
        this._currFactory = this._xalanFactory;
    }
    
    @Override
    public void setErrorListener(final ErrorListener listener) throws IllegalArgumentException {
        this._errorlistener = listener;
    }
    
    @Override
    public ErrorListener getErrorListener() {
        return this._errorlistener;
    }
    
    @Override
    public Object getAttribute(final String name) throws IllegalArgumentException {
        if (name.equals("translet-name") || name.equals("debug")) {
            if (this._xsltcFactory == null) {
                this.createXSLTCTransformerFactory();
            }
            return this._xsltcFactory.getAttribute(name);
        }
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        return this._xalanFactory.getAttribute(name);
    }
    
    @Override
    public void setAttribute(final String name, final Object value) throws IllegalArgumentException {
        if (name.equals("translet-name") || name.equals("debug")) {
            if (this._xsltcFactory == null) {
                this.createXSLTCTransformerFactory();
            }
            this._xsltcFactory.setAttribute(name, value);
        }
        else {
            if (this._xalanFactory == null) {
                this.createXalanTransformerFactory();
            }
            this._xalanFactory.setAttribute(name, value);
        }
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws TransformerConfigurationException {
        if (name == null) {
            final ErrorMsg err = new ErrorMsg("JAXP_SET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.featureSecureProcessing = value;
            return;
        }
        final ErrorMsg err = new ErrorMsg("JAXP_UNSUPPORTED_FEATURE", name);
        throw new TransformerConfigurationException(err.toString());
    }
    
    @Override
    public boolean getFeature(final String name) {
        final String[] features = { "http://javax.xml.transform.dom.DOMSource/feature", "http://javax.xml.transform.dom.DOMResult/feature", "http://javax.xml.transform.sax.SAXSource/feature", "http://javax.xml.transform.sax.SAXResult/feature", "http://javax.xml.transform.stream.StreamSource/feature", "http://javax.xml.transform.stream.StreamResult/feature" };
        if (name == null) {
            final ErrorMsg err = new ErrorMsg("JAXP_GET_FEATURE_NULL_NAME");
            throw new NullPointerException(err.toString());
        }
        for (int i = 0; i < features.length; ++i) {
            if (name.equals(features[i])) {
                return true;
            }
        }
        return name.equals("http://javax.xml.XMLConstants/feature/secure-processing") && this.featureSecureProcessing;
    }
    
    @Override
    public URIResolver getURIResolver() {
        return this._uriresolver;
    }
    
    @Override
    public void setURIResolver(final URIResolver resolver) {
        this._uriresolver = resolver;
    }
    
    @Override
    public Source getAssociatedStylesheet(final Source source, final String media, final String title, final String charset) throws TransformerConfigurationException {
        if (this._currFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        return this._currFactory.getAssociatedStylesheet(source, media, title, charset);
    }
    
    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        this._currFactory = this._xalanFactory;
        return this._currFactory.newTransformer();
    }
    
    @Override
    public Transformer newTransformer(final Source source) throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        this._currFactory = this._xalanFactory;
        return this._currFactory.newTransformer(source);
    }
    
    @Override
    public Templates newTemplates(final Source source) throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        this._currFactory = this._xsltcFactory;
        return this._currFactory.newTemplates(source);
    }
    
    @Override
    public TemplatesHandler newTemplatesHandler() throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        return this._xsltcFactory.newTemplatesHandler();
    }
    
    @Override
    public TransformerHandler newTransformerHandler() throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        return this._xalanFactory.newTransformerHandler();
    }
    
    @Override
    public TransformerHandler newTransformerHandler(final Source src) throws TransformerConfigurationException {
        if (this._xalanFactory == null) {
            this.createXalanTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xalanFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xalanFactory.setURIResolver(this._uriresolver);
        }
        return this._xalanFactory.newTransformerHandler(src);
    }
    
    @Override
    public TransformerHandler newTransformerHandler(final Templates templates) throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        return this._xsltcFactory.newTransformerHandler(templates);
    }
    
    @Override
    public XMLFilter newXMLFilter(final Source src) throws TransformerConfigurationException {
        if (this._xsltcFactory == null) {
            this.createXSLTCTransformerFactory();
        }
        if (this._errorlistener != null) {
            this._xsltcFactory.setErrorListener(this._errorlistener);
        }
        if (this._uriresolver != null) {
            this._xsltcFactory.setURIResolver(this._uriresolver);
        }
        final Templates templates = this._xsltcFactory.newTemplates(src);
        if (templates == null) {
            return null;
        }
        return this.newXMLFilter(templates);
    }
    
    @Override
    public XMLFilter newXMLFilter(final Templates templates) throws TransformerConfigurationException {
        try {
            return new TrAXFilter(templates);
        }
        catch (final TransformerConfigurationException e1) {
            if (this._xsltcFactory == null) {
                this.createXSLTCTransformerFactory();
            }
            final ErrorListener errorListener = this._xsltcFactory.getErrorListener();
            if (errorListener != null) {
                try {
                    errorListener.fatalError(e1);
                    return null;
                }
                catch (final TransformerException e2) {
                    new TransformerConfigurationException(e2);
                }
            }
            throw e1;
        }
    }
}
