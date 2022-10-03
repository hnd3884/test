package com.sun.org.apache.xalan.internal.xsltc.dom;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXSource;
import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX;
import javax.xml.transform.dom.DOMSource;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXStream2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXEvent2SAX;
import javax.xml.transform.stax.StAXSource;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;

public class XSLTCDTMManager extends DTMManagerDefault
{
    private static final boolean DUMPTREE = false;
    private static final boolean DEBUG = false;
    
    public static XSLTCDTMManager newInstance() {
        return new XSLTCDTMManager();
    }
    
    public static XSLTCDTMManager createNewDTMManagerInstance() {
        return newInstance();
    }
    
    @Override
    public DTM getDTM(final Source source, final boolean unique, final DTMWSFilter whiteSpaceFilter, final boolean incremental, final boolean doIndexing) {
        return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, false, 0, true, false);
    }
    
    public DTM getDTM(final Source source, final boolean unique, final DTMWSFilter whiteSpaceFilter, final boolean incremental, final boolean doIndexing, final boolean buildIdIndex) {
        return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, false, 0, buildIdIndex, false);
    }
    
    public DTM getDTM(final Source source, final boolean unique, final DTMWSFilter whiteSpaceFilter, final boolean incremental, final boolean doIndexing, final boolean buildIdIndex, final boolean newNameTable) {
        return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, false, 0, buildIdIndex, newNameTable);
    }
    
    public DTM getDTM(final Source source, final boolean unique, final DTMWSFilter whiteSpaceFilter, final boolean incremental, final boolean doIndexing, final boolean hasUserReader, final int size, final boolean buildIdIndex) {
        return this.getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing, hasUserReader, size, buildIdIndex, false);
    }
    
    public DTM getDTM(final Source source, final boolean unique, final DTMWSFilter whiteSpaceFilter, final boolean incremental, final boolean doIndexing, boolean hasUserReader, final int size, final boolean buildIdIndex, final boolean newNameTable) {
        final int dtmPos = this.getFirstFreeDTMID();
        final int documentID = dtmPos << 16;
        if (null != source && source instanceof StAXSource) {
            final StAXSource staxSource = (StAXSource)source;
            StAXEvent2SAX staxevent2sax = null;
            StAXStream2SAX staxStream2SAX = null;
            if (staxSource.getXMLEventReader() != null) {
                final XMLEventReader xmlEventReader = staxSource.getXMLEventReader();
                staxevent2sax = new StAXEvent2SAX(xmlEventReader);
            }
            else if (staxSource.getXMLStreamReader() != null) {
                final XMLStreamReader xmlStreamReader = staxSource.getXMLStreamReader();
                staxStream2SAX = new StAXStream2SAX(xmlStreamReader);
            }
            SAXImpl dtm;
            if (size <= 0) {
                dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, null, doIndexing, 512, buildIdIndex, newNameTable);
            }
            else {
                dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, null, doIndexing, size, buildIdIndex, newNameTable);
            }
            dtm.setDocumentURI(source.getSystemId());
            this.addDTM(dtm, dtmPos, 0);
            try {
                if (staxevent2sax != null) {
                    staxevent2sax.setContentHandler(dtm);
                    staxevent2sax.parse();
                }
                else if (staxStream2SAX != null) {
                    staxStream2SAX.setContentHandler(dtm);
                    staxStream2SAX.parse();
                }
            }
            catch (final RuntimeException re) {
                throw re;
            }
            catch (final Exception e) {
                throw new WrappedRuntimeException(e);
            }
            return dtm;
        }
        if (null != source && source instanceof DOMSource) {
            final DOMSource domsrc = (DOMSource)source;
            final Node node = domsrc.getNode();
            final DOM2SAX dom2sax = new DOM2SAX(node);
            SAXImpl dtm;
            if (size <= 0) {
                dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, null, doIndexing, 512, buildIdIndex, newNameTable);
            }
            else {
                dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter, null, doIndexing, size, buildIdIndex, newNameTable);
            }
            dtm.setDocumentURI(source.getSystemId());
            this.addDTM(dtm, dtmPos, 0);
            dom2sax.setContentHandler(dtm);
            try {
                dom2sax.parse();
            }
            catch (final RuntimeException re) {
                throw re;
            }
            catch (final Exception e) {
                throw new WrappedRuntimeException(e);
            }
            return dtm;
        }
        final boolean isSAXSource = null == source || source instanceof SAXSource;
        final boolean isStreamSource = null != source && source instanceof StreamSource;
        if (!isSAXSource && !isStreamSource) {
            throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[] { source }));
        }
        InputSource xmlSource;
        XMLReader reader;
        if (null == source) {
            xmlSource = null;
            reader = null;
            hasUserReader = false;
        }
        else {
            reader = this.getXMLReader(source);
            xmlSource = SAXSource.sourceToInputSource(source);
            String urlOfSource = xmlSource.getSystemId();
            if (null != urlOfSource) {
                try {
                    urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
                }
                catch (final Exception e2) {
                    System.err.println("Can not absolutize URL: " + urlOfSource);
                }
                xmlSource.setSystemId(urlOfSource);
            }
        }
        SAXImpl dtm2;
        if (size <= 0) {
            dtm2 = new SAXImpl(this, source, documentID, whiteSpaceFilter, null, doIndexing, 512, buildIdIndex, newNameTable);
        }
        else {
            dtm2 = new SAXImpl(this, source, documentID, whiteSpaceFilter, null, doIndexing, size, buildIdIndex, newNameTable);
        }
        this.addDTM(dtm2, dtmPos, 0);
        if (null == reader) {
            return dtm2;
        }
        reader.setContentHandler(dtm2.getBuilder());
        if (!hasUserReader || null == reader.getDTDHandler()) {
            reader.setDTDHandler(dtm2);
        }
        if (!hasUserReader || null == reader.getErrorHandler()) {
            reader.setErrorHandler(dtm2);
        }
        try {
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", dtm2);
        }
        catch (final SAXNotRecognizedException ex) {}
        catch (final SAXNotSupportedException ex2) {}
        try {
            reader.parse(xmlSource);
        }
        catch (final RuntimeException re2) {
            throw re2;
        }
        catch (final Exception e2) {
            throw new WrappedRuntimeException(e2);
        }
        finally {
            if (!hasUserReader) {
                this.releaseXMLReader(reader);
            }
        }
        return dtm2;
    }
}
