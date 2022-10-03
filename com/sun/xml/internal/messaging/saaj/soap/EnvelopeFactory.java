package com.sun.xml.internal.messaging.saaj.soap;

import javax.xml.soap.SOAPException;
import javax.xml.transform.Transformer;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.Result;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;
import com.sun.xml.internal.messaging.saaj.util.RejectDoctypeSaxFilter;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.messaging.saaj.util.ParserPool;
import java.util.logging.Logger;

public class EnvelopeFactory
{
    protected static final Logger log;
    private static ContextClassloaderLocal<ParserPool> parserPool;
    
    public static Envelope createEnvelope(Source src, final SOAPPartImpl soapPart) throws SOAPException {
        SAXParser saxParser = null;
        if (src instanceof StreamSource) {
            if (src instanceof JAXMStreamSource) {
                try {
                    if (!SOAPPartImpl.lazyContentLength) {
                        ((JAXMStreamSource)src).reset();
                    }
                }
                catch (final IOException ioe) {
                    EnvelopeFactory.log.severe("SAAJ0515.source.reset.exception");
                    throw new SOAPExceptionImpl(ioe);
                }
            }
            try {
                saxParser = EnvelopeFactory.parserPool.get().get();
            }
            catch (final Exception e) {
                EnvelopeFactory.log.severe("SAAJ0601.util.newSAXParser.exception");
                throw new SOAPExceptionImpl("Couldn't get a SAX parser while constructing a envelope", e);
            }
            final InputSource is = SAXSource.sourceToInputSource(src);
            if (is.getEncoding() == null && soapPart.getSourceCharsetEncoding() != null) {
                is.setEncoding(soapPart.getSourceCharsetEncoding());
            }
            XMLReader rejectFilter;
            try {
                rejectFilter = new RejectDoctypeSaxFilter(saxParser);
            }
            catch (final Exception ex) {
                EnvelopeFactory.log.severe("SAAJ0510.soap.cannot.create.envelope");
                throw new SOAPExceptionImpl("Unable to create envelope from given source: ", ex);
            }
            src = new SAXSource(rejectFilter, is);
        }
        try {
            final Transformer transformer = EfficientStreamingTransformer.newTransformer();
            final DOMResult result = new DOMResult(soapPart);
            transformer.transform(src, result);
            final Envelope env = (Envelope)soapPart.getEnvelope();
            return env;
        }
        catch (final Exception ex2) {
            if (ex2 instanceof SOAPVersionMismatchException) {
                throw (SOAPVersionMismatchException)ex2;
            }
            EnvelopeFactory.log.severe("SAAJ0511.soap.cannot.create.envelope");
            throw new SOAPExceptionImpl("Unable to create envelope from given source: ", ex2);
        }
        finally {
            if (saxParser != null) {
                EnvelopeFactory.parserPool.get().returnParser(saxParser);
            }
        }
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
        EnvelopeFactory.parserPool = new ContextClassloaderLocal<ParserPool>() {
            @Override
            protected ParserPool initialValue() throws Exception {
                return new ParserPool(5);
            }
        };
    }
}
