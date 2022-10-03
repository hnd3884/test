package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.Transformer;

public final class StAXValidatorHelper implements ValidatorHelper
{
    private XMLSchemaValidatorComponentManager fComponentManager;
    private Transformer identityTransformer1;
    private TransformerHandler identityTransformer2;
    private ValidatorHandlerImpl handler;
    
    public StAXValidatorHelper(final XMLSchemaValidatorComponentManager componentManager) {
        this.identityTransformer1 = null;
        this.identityTransformer2 = null;
        this.handler = null;
        this.fComponentManager = componentManager;
    }
    
    @Override
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result == null || result instanceof StAXResult) {
            if (this.identityTransformer1 == null) {
                try {
                    final SAXTransformerFactory tf = JdkXmlUtils.getSAXTransformFactory(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"));
                    final XMLSecurityManager securityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
                    if (securityManager != null) {
                        for (final XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
                            if (securityManager.isSet(limit.ordinal())) {
                                tf.setAttribute(limit.apiProperty(), securityManager.getLimitValueAsString(limit));
                            }
                        }
                        if (securityManager.printEntityCountInfo()) {
                            tf.setAttribute("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
                        }
                    }
                    this.identityTransformer1 = tf.newTransformer();
                    this.identityTransformer2 = tf.newTransformerHandler();
                }
                catch (final TransformerConfigurationException e) {
                    throw new TransformerFactoryConfigurationError(e);
                }
            }
            this.handler = new ValidatorHandlerImpl(this.fComponentManager);
            if (result != null) {
                this.handler.setContentHandler(this.identityTransformer2);
                this.identityTransformer2.setResult(result);
            }
            try {
                this.identityTransformer1.transform(source, new SAXResult(this.handler));
            }
            catch (final TransformerException e2) {
                if (e2.getException() instanceof SAXException) {
                    throw (SAXException)e2.getException();
                }
                throw new SAXException(e2);
            }
            finally {
                this.handler.setContentHandler(null);
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
}
