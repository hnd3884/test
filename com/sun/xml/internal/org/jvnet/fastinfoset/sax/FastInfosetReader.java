package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetParser;
import org.xml.sax.XMLReader;

public interface FastInfosetReader extends XMLReader, FastInfosetParser
{
    public static final String ENCODING_ALGORITHM_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler";
    public static final String PRIMITIVE_TYPE_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler";
    
    void parse(final InputStream p0) throws IOException, FastInfosetException, SAXException;
    
    void setLexicalHandler(final LexicalHandler p0);
    
    LexicalHandler getLexicalHandler();
    
    void setDeclHandler(final DeclHandler p0);
    
    DeclHandler getDeclHandler();
    
    void setEncodingAlgorithmContentHandler(final EncodingAlgorithmContentHandler p0);
    
    EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler();
    
    void setPrimitiveTypeContentHandler(final PrimitiveTypeContentHandler p0);
    
    PrimitiveTypeContentHandler getPrimitiveTypeContentHandler();
}
