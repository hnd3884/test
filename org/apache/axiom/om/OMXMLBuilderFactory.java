package org.apache.axiom.om;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.om.impl.builder.AttachmentsMimePartProvider;
import java.text.ParseException;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.attachments.Attachments;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Node;
import javax.xml.transform.Source;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.apache.axiom.om.util.StAXParserConfiguration;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public class OMXMLBuilderFactory
{
    private OMXMLBuilderFactory() {
    }
    
    public static OMXMLParserWrapper createStAXOMBuilder(final XMLStreamReader parser) {
        final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createStAXOMBuilder(metaFactory.getOMFactory(), parser);
    }
    
    public static OMXMLParserWrapper createStAXOMBuilder(final OMFactory omFactory, final XMLStreamReader parser) {
        return omFactory.getMetaFactory().createStAXOMBuilder(omFactory, parser);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final InputStream in) {
        return createOMBuilder(StAXParserConfiguration.DEFAULT, in);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final InputStream in, final String encoding) {
        return createOMBuilder(StAXParserConfiguration.DEFAULT, in, encoding);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final StAXParserConfiguration configuration, final InputStream in) {
        return createOMBuilder(configuration, in, null);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final StAXParserConfiguration configuration, final InputStream in, final String encoding) {
        final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        final InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), configuration, is);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final InputStream in) {
        return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final InputStream in, final String encoding) {
        return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in, encoding);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final StAXParserConfiguration configuration, final InputStream in) {
        return createOMBuilder(omFactory, configuration, in, null);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final StAXParserConfiguration configuration, final InputStream in, final String encoding) {
        final InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return omFactory.getMetaFactory().createOMBuilder(omFactory, configuration, is);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final Reader in) {
        return createOMBuilder(StAXParserConfiguration.DEFAULT, in);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final StAXParserConfiguration configuration, final Reader in) {
        final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), configuration, new InputSource(in));
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final Reader in) {
        return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final StAXParserConfiguration configuration, final Reader in) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, configuration, new InputSource(in));
    }
    
    public static OMXMLParserWrapper createOMBuilder(final Source source) {
        final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), source);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final Node node, final boolean expandEntityReferences) {
        final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), node, expandEntityReferences);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final SAXSource source, final boolean expandEntityReferences) {
        final OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), source, expandEntityReferences);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final Source source) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final Node node, final boolean expandEntityReferences) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, node, expandEntityReferences);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final SAXSource source, final boolean expandEntityReferences) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source, expandEntityReferences);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final StAXParserConfiguration configuration, final Attachments attachments) {
        return createOMBuilder(OMAbstractFactory.getMetaFactory().getOMFactory(), configuration, attachments);
    }
    
    public static OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final StAXParserConfiguration configuration, final Attachments attachments) {
        ContentType contentType;
        try {
            contentType = new ContentType(attachments.getRootPartContentType());
        }
        catch (final ParseException ex) {
            throw new OMException(ex);
        }
        final InputSource rootPart = getRootPartInputSource(attachments, contentType);
        return omFactory.getMetaFactory().createOMBuilder(configuration, omFactory, rootPart, new AttachmentsMimePartProvider(attachments));
    }
    
    public static SOAPModelBuilder createStAXSOAPModelBuilder(final OMMetaFactory metaFactory, final XMLStreamReader parser) {
        return metaFactory.createStAXSOAPModelBuilder(parser);
    }
    
    public static SOAPModelBuilder createStAXSOAPModelBuilder(final XMLStreamReader parser) {
        return OMAbstractFactory.getMetaFactory().createStAXSOAPModelBuilder(parser);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final InputStream in, final String encoding) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), in, encoding);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final OMMetaFactory metaFactory, final InputStream in, final String encoding) {
        final InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, is);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final Reader in) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), in);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final OMMetaFactory metaFactory, final Reader in) {
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, new InputSource(in));
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final Source source) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), source);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final OMMetaFactory metaFactory, final Source source) {
        return metaFactory.createSOAPModelBuilder(source);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final Attachments attachments) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), attachments);
    }
    
    public static SOAPModelBuilder createSOAPModelBuilder(final OMMetaFactory metaFactory, final Attachments attachments) {
        ContentType contentType;
        try {
            contentType = new ContentType(attachments.getRootPartContentType());
        }
        catch (final ParseException ex) {
            throw new OMException(ex);
        }
        final String type = contentType.getParameter("type");
        SOAPFactory soapFactory;
        if ("text/xml".equalsIgnoreCase(type)) {
            soapFactory = metaFactory.getSOAP11Factory();
        }
        else {
            if (!"application/soap+xml".equalsIgnoreCase(type)) {
                throw new OMException("Unable to determine SOAP version");
            }
            soapFactory = metaFactory.getSOAP12Factory();
        }
        final InputSource rootPart = getRootPartInputSource(attachments, contentType);
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, soapFactory, rootPart, new AttachmentsMimePartProvider(attachments));
    }
    
    private static InputSource getRootPartInputSource(final Attachments attachments, final ContentType contentType) {
        final InputSource rootPart = new InputSource(attachments.getRootPartInputStream(false));
        rootPart.setEncoding(contentType.getParameter("charset"));
        return rootPart;
    }
}
