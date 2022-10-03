package org.glassfish.jersey.server.wadl.internal;

import java.util.List;
import javax.ws.rs.core.UriInfo;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.bind.JAXBException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

public class WadlUtils
{
    public static final String DETAILED_WADL_QUERY_PARAM = "detail";
    
    public static <T> T unmarshall(final InputStream inputStream, final SAXParserFactory saxParserFactory, final Class<T> resultClass) throws JAXBException, ParserConfigurationException, SAXException {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(resultClass);
        }
        catch (final JAXBException ex) {
            throw new ProcessingException(LocalizationMessages.ERROR_WADL_JAXB_CONTEXT(), (Throwable)ex);
        }
        final SAXParser saxParser = saxParserFactory.newSAXParser();
        final SAXSource source = new SAXSource(saxParser.getXMLReader(), new InputSource(inputStream));
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final Object result = unmarshaller.unmarshal(source);
        return resultClass.cast(result);
    }
    
    public static boolean isDetailedWadlRequested(final UriInfo uriInfo) {
        final List<String> simple = (List<String>)uriInfo.getQueryParameters().get((Object)"detail");
        if (simple == null) {
            return false;
        }
        if (simple.size() == 0) {
            return true;
        }
        final String value = simple.get(0).trim();
        return value.isEmpty() || value.toUpperCase().equals("TRUE");
    }
}
