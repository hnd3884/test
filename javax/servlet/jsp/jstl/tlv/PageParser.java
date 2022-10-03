package javax.servlet.jsp.jstl.tlv;

import org.xml.sax.SAXException;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.PageData;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import javax.xml.parsers.ParserConfigurationException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.xml.parsers.SAXParserFactory;

class PageParser
{
    private final SAXParserFactory parserFactory;
    
    PageParser(final boolean namespaceAware) {
        this.parserFactory = AccessController.doPrivileged((PrivilegedAction<SAXParserFactory>)new PrivilegedAction<SAXParserFactory>() {
            public SAXParserFactory run() {
                final ClassLoader original = Thread.currentThread().getContextClassLoader();
                final ClassLoader ours = PageParser.class.getClassLoader();
                try {
                    if (original != ours) {
                        Thread.currentThread().setContextClassLoader(ours);
                    }
                    return SAXParserFactory.newInstance();
                }
                finally {
                    if (original != ours) {
                        Thread.currentThread().setContextClassLoader(original);
                    }
                }
            }
        });
        try {
            this.parserFactory.setNamespaceAware(namespaceAware);
            this.parserFactory.setValidating(false);
            this.parserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (final ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
        catch (final SAXNotRecognizedException e2) {
            throw new ExceptionInInitializerError(e2);
        }
        catch (final SAXNotSupportedException e3) {
            throw new ExceptionInInitializerError(e3);
        }
    }
    
    void parse(final PageData pageData, final DefaultHandler handler) throws ParserConfigurationException, SAXException, IOException {
        final SAXParser parser = this.parserFactory.newSAXParser();
        final InputStream is = pageData.getInputStream();
        try {
            parser.parse(is, handler);
        }
        finally {
            try {
                is.close();
            }
            catch (final IOException ex) {}
        }
    }
}
