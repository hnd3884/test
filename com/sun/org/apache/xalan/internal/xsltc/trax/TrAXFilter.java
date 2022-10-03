package com.sun.org.apache.xalan.internal.xsltc.trax;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import java.io.IOException;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import org.xml.sax.helpers.XMLFilterImpl;

public class TrAXFilter extends XMLFilterImpl
{
    private Templates _templates;
    private TransformerImpl _transformer;
    private TransformerHandlerImpl _transformerHandler;
    private boolean _overrideDefaultParser;
    
    public TrAXFilter(final Templates templates) throws TransformerConfigurationException {
        this._templates = templates;
        this._transformer = (TransformerImpl)templates.newTransformer();
        this._transformerHandler = new TransformerHandlerImpl(this._transformer);
        this._overrideDefaultParser = this._transformer.overrideDefaultParser();
    }
    
    public Transformer getTransformer() {
        return this._transformer;
    }
    
    private void createParent() throws SAXException {
        final XMLReader parent = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, this._transformer.isSecureProcessing());
        this.setParent(parent);
    }
    
    @Override
    public void parse(final InputSource input) throws SAXException, IOException {
        XMLReader managedReader = null;
        try {
            if (this.getParent() == null) {
                try {
                    managedReader = XMLReaderManager.getInstance(this._overrideDefaultParser).getXMLReader();
                    this.setParent(managedReader);
                }
                catch (final SAXException e) {
                    throw new SAXException(e.toString());
                }
            }
            this.getParent().parse(input);
        }
        finally {
            if (managedReader != null) {
                XMLReaderManager.getInstance(this._overrideDefaultParser).releaseXMLReader(managedReader);
            }
        }
    }
    
    @Override
    public void parse(final String systemId) throws SAXException, IOException {
        this.parse(new InputSource(systemId));
    }
    
    @Override
    public void setContentHandler(final ContentHandler handler) {
        this._transformerHandler.setResult(new SAXResult(handler));
        if (this.getParent() == null) {
            try {
                this.createParent();
            }
            catch (final SAXException e) {
                return;
            }
        }
        this.getParent().setContentHandler(this._transformerHandler);
    }
    
    public void setErrorListener(final ErrorListener handler) {
    }
}
