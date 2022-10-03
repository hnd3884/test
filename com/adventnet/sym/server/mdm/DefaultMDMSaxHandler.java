package com.adventnet.sym.server.mdm;

import org.xml.sax.SAXParseException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.adventnet.sym.server.mdm.util.AppleDTDUtil;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public class DefaultMDMSaxHandler implements ErrorHandler, EntityResolver
{
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        if (systemId.equals(AppleDTDUtil.dtdUrl_1_0)) {
            return new InputSource(new ByteArrayInputStream(AppleDTDUtil.getDtdAsBytes()));
        }
        throw new SAXException("SystemId / Doctype not valid");
    }
    
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        throw exception;
    }
}
